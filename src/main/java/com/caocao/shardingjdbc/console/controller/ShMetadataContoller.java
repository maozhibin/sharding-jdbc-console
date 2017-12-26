package com.caocao.shardingjdbc.console.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caocao.shardingjdbc.console.common.Constants;
import com.caocao.shardingjdbc.console.common.JsonResponseMsg;
import com.caocao.shardingjdbc.console.dal.ext.Page;
import com.caocao.shardingjdbc.console.dal.model.ShConfig;
import com.caocao.shardingjdbc.console.dal.model.ShMetadata;
import com.caocao.shardingjdbc.console.dal.service.ShConfigService;
import com.caocao.shardingjdbc.console.dal.service.ShMetadataService;
import com.caocao.shardingjdbc.console.dto.ShMetadataDto;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@Controller
@RequestMapping("sj-api/api")
public class ShMetadataContoller {
    @Resource
    private ShMetadataService shMetadataService;
    @Resource
    private ShConfigService shConfigService;
    /**
     * 数据列表
     */
    @RequestMapping(value = "queryDataSourceList.json" ,method = RequestMethod.POST)
    @ResponseBody
    public JsonResponseMsg queryDataSourceList(@RequestParam(defaultValue = "10", required = false) Integer limit,
                                               @RequestParam(defaultValue = "1", required = false) Integer offset,String type,String keywords){
        JsonResponseMsg result = new JsonResponseMsg();
        Page<ShMetadataDto> page = new Page<>(limit, offset);
        shMetadataService.queryDataSourceList(page,type,keywords);
        Map<String,Object> map = new HashMap<>();
        map.put("page",page);
        return result.fill(JsonResponseMsg.CODE_SUCCESS,"查询成功",map);
    }

    /**
     * 查询全部数量普通数据源，如果是编辑查本身和普通数据源，新增就查普通数据源的数据
     * @return
     */
    @RequestMapping(value = "queryDataSourceCount.json" ,method = RequestMethod.POST)
    @ResponseBody
    public JsonResponseMsg queryDataSourceCount(String id){
        JsonResponseMsg result = new JsonResponseMsg();
        Map<String,Object> map = new HashMap<>();
        List<ShMetadata> lsit = shMetadataService.queryDataSourceCount(id);
        String properties = null;
        if(NumberUtils.isNumber(id)){
             properties = shMetadataService.queryMasterPropertiesById(NumberUtils.toInt(id));
        }
        map.put("properties",properties);
        map.put("ShMetadatas",lsit);
        return result.fill(JsonResponseMsg.CODE_SUCCESS,"查询成功",map);
    }
    /**
     * 查询除了类型为分库分表的数据源
     *
     */
    @RequestMapping(value = "queryDataSourceCountNoSharding.json" ,method = RequestMethod.POST)
    @ResponseBody
    public JsonResponseMsg queryDataSourceCountNoSharding(String id){
        JsonResponseMsg result = new JsonResponseMsg();
        Map<String,Object> map = new HashMap<>();
        List<ShMetadata> lsit = shMetadataService.queryDataSourceCountNoSharding();
//        String properties = null;
//        if(NumberUtils.isNumber(id)){
//            properties = shMetadataService.queryMasterPropertiesById(NumberUtils.toInt(id));
//        }
//        map.put("properties",properties);
        map.put("Sharding",lsit);
        return result.fill(JsonResponseMsg.CODE_SUCCESS,"查询成功",map);
    }

    /**
     * 查询除了普通数据源的数据
     * @param
     * @return
     */
    @RequestMapping(value = "queryDataSourceCountNoMysql.json" ,method = RequestMethod.POST)
    @ResponseBody
    public JsonResponseMsg queryDataSourceCountNoMysql(){
        JsonResponseMsg result = new JsonResponseMsg();
        Map<String,Object> map = new HashMap<>();
        List<ShMetadata> lsit = shMetadataService.queryDataSourceCountNoMysql();
        map.put("mysqlList",lsit);
        return result.fill(JsonResponseMsg.CODE_SUCCESS,"查询成功",map);
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
        ShMetadata shMetadata = shMetadataService.queryInfoById(NumberUtils.toInt(id));
        List<ShConfig> shConfigList = shConfigService.queryByDataSourceName(shMetadata.getDataSourceName());
        if(!CollectionUtils.isEmpty(shConfigList)){
            return  result.fill(JsonResponseMsg.CODE_FAIL,"对不起，你已经被注册中心引用不能进行删除");
        }
        shMetadataService.deleteInfo(id);
        return result.fill(JsonResponseMsg.CODE_SUCCESS,"删除成功");
    }

    /**
     * 编辑或者新增，有id传入为编辑无则为新增
     * @return
     */
    @RequestMapping(value = "editOrAddInfo",method = RequestMethod.POST)
    @ResponseBody
    public  JsonResponseMsg editOrAddInfo(@RequestBody ShMetadataDto shMetadataDto,@RequestParam("type")String type){
        JsonResponseMsg result = new JsonResponseMsg();
        if(Constants.MASTER_SLAVE_INTERGER.equals(Byte.parseByte(type))){
            shMetadataService.installProperties(shMetadataDto);
        }else if(Constants.SHARDING_INTERGER.equals(Byte.parseByte(type))){
            shMetadataService.installPropertiesSharding(shMetadataDto);
        }
        String name = shMetadataDto.getDataSourceName();
        if(shMetadataDto.getId()!=null){
            shMetadataService.updateInfo(shMetadataDto);
            shConfigService.updateStatusByDataSourceName(name, (byte) 2);
        }else{
            Integer id =shMetadataService.queryNameById(name);
            if(id != null){
                return result.fill(JsonResponseMsg.CODE_FAIL,"你输入的数据库名字已经存在，请重新输入");
            }
            shMetadataService.insertInfo(shMetadataDto);
        }
        return result.fill(JsonResponseMsg.CODE_SUCCESS,"修改成功");
    }
    /**
     *根据id修改properties属性
     */
    @RequestMapping(value = "updatePropertiesById" ,method = RequestMethod.POST)
    @ResponseBody
    public  JsonResponseMsg updatePropertiesById(@RequestBody ShMetadataDto shMetadataDto) {
        JsonResponseMsg result = new JsonResponseMsg();
        if(shMetadataDto.getId()==null){
            return  result.fill(JsonResponseMsg.CODE_FAIL,"参数错误");
        }
        shMetadataService.updateInfo(shMetadataDto);
        shConfigService.updateStatusByDataSourceName(shMetadataDto.getDataSourceName(), (byte) 2);
        return result.fill(JsonResponseMsg.CODE_SUCCESS, "修改成功");
    }
    /**
     * 根据数据库名查询id
     */
    @RequestMapping(value = "queryNameById" ,method = RequestMethod.POST)
    @ResponseBody
    public  JsonResponseMsg queryNameById(@RequestParam("dataSourceName")String dataSourceName){
        JsonResponseMsg result = new JsonResponseMsg();
        if(StringUtils.isEmpty(dataSourceName)){
            return  result.fill(JsonResponseMsg.CODE_FAIL,"参数错误");
        }
        Integer id = shMetadataService.queryNameById(dataSourceName);
        Map<String,Object> map = new HashMap<>();
        map.put("id",id);
        return result.fill(JsonResponseMsg.CODE_SUCCESS,"修改成功",map);
    }


    /**
     * 根据ID查询properties
     */
    @RequestMapping(value = "queryPropertiesById" ,method = RequestMethod.POST)
    @ResponseBody
    public  JsonResponseMsg queryPropertiesById(@RequestParam("id")String id) {
        JsonResponseMsg result = new JsonResponseMsg();
        if(!NumberUtils.isNumber(id)){
            return  result.fill(JsonResponseMsg.CODE_FAIL,"参数错误");
        }
        String properties = shMetadataService.queryMasterPropertiesById(NumberUtils.toInt(id));
        Map<String, Object> map = new HashMap<>();
        map.put("properties",properties);
        return result.fill(JsonResponseMsg.CODE_SUCCESS, "查询成功", map);
    }

    /**
     * 根据ID查询
     */
    @RequestMapping(value = "queryById" ,method = RequestMethod.POST)
    @ResponseBody
    public  JsonResponseMsg queryById(@RequestParam("id")String id) {
        JsonResponseMsg result = new JsonResponseMsg();
        if(!NumberUtils.isNumber(id)){
            return result.fill(JsonResponseMsg.CODE_FAIL,"参数错误");
        }
        ShMetadata shMetadata=shMetadataService.queryInfoById(NumberUtils.toInt(id));
        Map<String, Object> map = new HashMap<>();
        map.put("shMetadataDto",shMetadata);
        return result.fill(JsonResponseMsg.CODE_SUCCESS, "查询成功", map);
    }

    /**
     * 根据sharding类型的名字查询他的属性
     */
    @RequestMapping(value = "queryDataSourceByName" ,method = RequestMethod.POST)
    @ResponseBody
    public  JsonResponseMsg queryDataSourceByName(@RequestParam("name")String name) {
        JsonResponseMsg result = new JsonResponseMsg();
        if(StringUtils.isEmpty(name)){
            return  result.fill(JsonResponseMsg.CODE_FAIL,"参数错误");
        }
        ShMetadataDto shMetadataDto=shMetadataService.queryByName(name);
        Map<String, Object> map = new HashMap<>();
        map.put("properties",shMetadataDto.getProperties());
        return result.fill(JsonResponseMsg.CODE_SUCCESS, "查询成功", map);
    }


}
