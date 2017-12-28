package com.caocao.shardingjdbc.console.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caocao.shardingjdbc.console.common.Constants;
import com.caocao.shardingjdbc.console.common.CuratorService;
import com.caocao.shardingjdbc.console.common.JsonResponseMsg;
import com.caocao.shardingjdbc.console.dal.ext.Page;
import com.caocao.shardingjdbc.console.dal.model.ShConfig;
import com.caocao.shardingjdbc.console.dal.service.ShConfigService;
import com.caocao.shardingjdbc.console.dal.service.ShMetadataService;
import com.caocao.shardingjdbc.console.dto.ShConfigDto;
import com.caocao.shardingjdbc.console.dto.ShMetadataDto;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@Controller
@RequestMapping("sj-api/config")
public class ShConfigController {
    private static final Logger logger = LoggerFactory.getLogger(ShConfigController.class);
    @Resource
    private ShConfigService shConfigService;
    @Resource
    private ShMetadataService shMetadataService;
    @Resource
    private CuratorService curatorService;
    /**
     * 列表查询
     * @param limit
     * @param offset
     * @param keywords
     * @return
     */
    @RequestMapping(value = "queryConfigList" ,method = RequestMethod.POST)
    @ResponseBody
    public JsonResponseMsg queryConfigList(@RequestParam(defaultValue = "10", required = false) Integer limit,
                                               @RequestParam(defaultValue = "1", required = false) Integer offset,String keywords){
        JsonResponseMsg result = new JsonResponseMsg();
        Page<ShConfigDto> page = new Page<>(limit, offset);
        shConfigService.queryConfigList(page,keywords);
        Map<String,Object> map = new HashMap<>();
        map.put("page",page);
        return result.fill(JsonResponseMsg.CODE_SUCCESS,"查询成功",map);
    }

    /**
     * 编辑或者新增，有id传入为编辑无则为新增
     * @return
     */
    @RequestMapping(value = "editOrAddInfo",method = RequestMethod.POST)
    @ResponseBody
    public  JsonResponseMsg editOrAddInfo(@RequestBody ShConfig shConfig){
        JsonResponseMsg result = new JsonResponseMsg();
        if(shConfig.getId()!=null){
            shConfigService.updateInfo(shConfig);
        }else{
            String name = shConfig.getRegNamespace();
            Integer id =shConfigService.queryIdByRegNamespace(name);
            if(id != null){
                return result.fill(JsonResponseMsg.CODE_FAIL,"你输入的注册中心的命名空间已经存在，请重新输入");
            }
            shConfig.setStatus((byte) 0);
            shConfigService.insertInfo(shConfig);
        }
        return result.fill(JsonResponseMsg.CODE_SUCCESS,"修改成功");
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @RequestMapping(value = "delete",method = RequestMethod.POST)
    @ResponseBody
    public  JsonResponseMsg deleteInfo(@RequestParam("id") String id){
        JsonResponseMsg result = new JsonResponseMsg();
        if(!NumberUtils.isNumber(id)){
            return  result.fill(JsonResponseMsg.CODE_FAIL,"参数错误");
        }
        shConfigService.deleteInfo(id);
        return result.fill(JsonResponseMsg.CODE_SUCCESS,"删除成功");
    }

    /**
     * 状态修改
     */
    @RequestMapping(value = "updateStatus",method = RequestMethod.POST)
    @ResponseBody
    public  JsonResponseMsg updateStatus(@RequestBody ShConfigDto shConfigDto, @RequestParam("type")String type){
        JsonResponseMsg result = new JsonResponseMsg();
        if(shConfigDto.getId()==null){
            return result.fill(JsonResponseMsg.CODE_FAIL,"参数错误");
        }
        if(!NumberUtils.isNumber(type)){
            return result.fill(JsonResponseMsg.CODE_FAIL,"参数错误");
        }
        Boolean isValid = curatorService.init(shConfigDto.getRegServerList());
        if(!isValid){
            return result.fill(JsonResponseMsg.CODE_FAIL,"zk链接超时");
        }
        //数据同步到zk中
        if(Constants.IS_QUOTE.equals(NumberUtils.toByte(type))){
            String dataSourceName = shConfigDto.getDataSourceName();
            ShMetadataDto shMetadataDto = shMetadataService.queryByName(dataSourceName);
            JSONObject properties = (JSONObject) JSONObject.parse(shMetadataDto.getProperties());
            JSONArray arrays = properties.getJSONArray("dataSources");
            String dataSourcePth = "/"+shConfigDto.getRegNamespace()+"/"+shConfigDto.getDataSourceName()+Constants.CONFIG+Constants.DATASOURCE;
            try {
                if(!curatorService.isExists(dataSourcePth)){
                    curatorService.create(dataSourcePth,JSONObject.toJSONString(arrays));
                }else {
                    curatorService.update(dataSourcePth,JSONObject.toJSONString(arrays));
                }
                Byte datatype = shMetadataDto.getType();
                if(Constants.MASTER_SLAVE_INTERGER.equals(datatype)){
                    Map<String,Object> map = new HashMap<>();
                    String masterDataSourceName = properties.getString("masterDataSourceName");
                    JSONArray slaveDataSourceNames = properties.getJSONArray("slaveDataSourceNames");
                    String name = properties.getString("name");
                    String loadBalanceAlgorithmType = properties.getString("loadBalanceAlgorithmType");
                    map.put("masterDataSourceName",masterDataSourceName);
                    map.put("slaveDataSourceNames",slaveDataSourceNames);
                    map.put("name",name);
                    map.put("loadBalanceAlgorithmType",loadBalanceAlgorithmType);
                    String json = JSONObject.toJSONString(map);
                    String masterslaveRulePath ="/"+shConfigDto.getRegNamespace()+"/"+shConfigDto.getDataSourceName()+Constants.CONFIG+Constants.MASTERSLAVE+Constants.RUL;
                    if(!curatorService.isExists(masterslaveRulePath)){
                        curatorService.create(masterslaveRulePath,json);
                    }else {
                        curatorService.update(masterslaveRulePath,json);
                    }
                }else if(Constants.SHARDING_INTERGER.equals(datatype)){
                    Map<String,Object> map = new HashMap<>();
                    JSONArray bindingTableGroups = properties.getJSONArray("bindingTableGroups");
                    JSONArray masterSlaveRuleConfigs = properties.getJSONArray("masterSlaveRuleConfigs");
                    JSONArray tableRuleConfigs = properties.getJSONArray("tableRuleConfigs");
                    map.put("bindingTableGroups",bindingTableGroups);
                    map.put("masterSlaveRuleConfigs",masterSlaveRuleConfigs);
                    map.put("tableRuleConfigs",tableRuleConfigs);
                    String json = JSONObject.toJSONString(map);
                    String shardingRulePath ="/"+shConfigDto.getRegNamespace()+"/"+shConfigDto.getDataSourceName()+Constants.CONFIG+Constants.SHARDINGS+Constants.RUL;
                    if(!curatorService.isExists(shardingRulePath)){
                        curatorService.create(shardingRulePath,json);
                    }else {
                        curatorService.update(shardingRulePath,json);
                    }
                    String props = properties.getString("props");
                    String shardingPropsPath ="/"+shConfigDto.getRegNamespace()+"/"+shConfigDto.getDataSourceName()+Constants.CONFIG+Constants.SHARDINGS+Constants.PROPS;
                    if(!curatorService.isExists(shardingPropsPath)){
                        curatorService.create(shardingPropsPath,props);
                    }else {
                        curatorService.update(shardingPropsPath,props);
                    }
                }
            } catch (Exception e) {
                logger.error("数据同步zk出错",e);
                e.printStackTrace();
            }
        }
        shConfigService.updateStatusById(shConfigDto.getId(),NumberUtils.toByte(type));
        return result.fill(JsonResponseMsg.CODE_SUCCESS,"状态修改成功");
    }


}
