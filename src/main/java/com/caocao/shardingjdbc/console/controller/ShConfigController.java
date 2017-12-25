package com.caocao.shardingjdbc.console.controller;

import com.caocao.shardingjdbc.console.common.JsonResponseMsg;
import com.caocao.shardingjdbc.console.dal.ext.Page;
import com.caocao.shardingjdbc.console.dal.model.ShConfig;
import com.caocao.shardingjdbc.console.dal.service.ShConfigService;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@Controller
@RequestMapping("sj-api/config")
public class ShConfigController {
    @Autowired
    private ShConfigService shConfigService;

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
        Page<ShConfig> page = new Page<>(limit, offset);
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
}
