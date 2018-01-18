package com.caocao.shardingjdbc.console.dal.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caocao.shardingjdbc.console.common.Constants;
import com.caocao.shardingjdbc.console.common.CuratorService;
import com.caocao.shardingjdbc.console.common.Utils;
import com.caocao.shardingjdbc.console.dal.dao.ShConfigMapper;
import com.caocao.shardingjdbc.console.dal.ext.Page;
import com.caocao.shardingjdbc.console.dal.model.ShConfig;
import com.caocao.shardingjdbc.console.dal.service.ShConfigService;
import com.caocao.shardingjdbc.console.dal.service.ShMetadataService;
import com.caocao.shardingjdbc.console.dto.ShConfigDto;
import com.caocao.shardingjdbc.console.dto.ShMetadataDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ShConfigServiceImpl implements ShConfigService {
    @Autowired
    private ShConfigMapper shConfigMapper;
    @Resource
    private ShMetadataService shMetadataService;
    @Resource
    private CuratorService curatorService;

    @Override
    public void queryConfigList(Page<ShConfigDto> page, String keywords) {
        int total = shConfigMapper.totalCount(keywords);
        page.setTotal(total);
        List<ShConfigDto> list = shConfigMapper.queryConfigList(page.getOffset(), page.getLimit(), keywords);
        for (ShConfigDto shConfigDto : list) {
            Boolean isValid = curatorService.init(shConfigDto.getRegServerList());
            if (!isValid) {
                return;
            }
            String dataSourceName = shConfigDto.getDataSourceName();
            ShMetadataDto shMetadataDto = shMetadataService.queryByName(dataSourceName);
            Byte datatype = shMetadataDto.getType();
            Map<String, Object> map = new HashMap<>();
            try {
                String dataSourcePth = "/" + shConfigDto.getRegNamespace() + "/"
                        + shConfigDto.getDataSourceName() + Constants.CONFIG + Constants.DATASOURCE;
                String dataSource = curatorService.getData(dataSourcePth);

                JSONArray object1 = (JSONArray) JSONObject.parse(dataSource);
                for (int i = 0; i < object1.size(); i++) {
                    JSONObject object = (JSONObject) object1.get(i);
                    String password = Utils.druidEnc(object.getString("password"));
                    object.remove("password");
                    object.put("password", password);
                }

                map.put("dataSource", JSONObject.toJSONString(object1));
                if (Constants.MASTER_SLAVE_INTERGER.equals(datatype)) {
                    String masterslaveRulePath = "/" + shConfigDto.getRegNamespace() + "/" + shConfigDto.getDataSourceName() + Constants.CONFIG + Constants.MASTERSLAVE + Constants.RUL;
                    String masterslaveRule = curatorService.getData(masterslaveRulePath);
                    String configmapPath = "/" + shConfigDto.getRegNamespace() + "/" + shConfigDto.getDataSourceName() + Constants.CONFIG + Constants.MASTERSLAVE + Constants.CONFIGMAP;
                    String configmap = curatorService.getData(configmapPath);
                    map.put("masterslaveRule", masterslaveRule);
                    map.put("configmap", configmap);
                } else if (Constants.SHARDING_INTERGER.equals(datatype)) {
                    String shardingRulePath = "/" + shConfigDto.getRegNamespace() + "/" + shConfigDto.getDataSourceName() + Constants.CONFIG + Constants.SHARDINGS + Constants.RUL;
                    String shardingPropsPath = "/" + shConfigDto.getRegNamespace() + "/" + shConfigDto.getDataSourceName() + Constants.CONFIG + Constants.SHARDINGS + Constants.PROPS;
                    String configmapPath = "/" + shConfigDto.getRegNamespace() + "/" + shConfigDto.getDataSourceName() + Constants.CONFIG + Constants.SHARDINGS + Constants.CONFIGMAP;
                    String shardingRule = curatorService.getData(shardingRulePath);
                    String shardingProps = curatorService.getData(shardingPropsPath);
                    String configmap = curatorService.getData(configmapPath);
                    map.put("configmap", configmap);
                    map.put("shardingProps", shardingProps);
                    map.put("shardingRule", shardingRule);
                }
            } catch (Exception e) {
                log.error("error queryConfigList", e);
            }
            shConfigDto.setZkInfo(map);
        }
        page.setRows(list);
    }

    @Override
    public void updateInfo(ShConfig shConfig) {
        shConfigMapper.updateInfo(shConfig);
    }

    @Override
    public void insertInfo(ShConfig shConfig) {
        shConfigMapper.insertInfo(shConfig);
    }

    @Override
    public Integer queryIdByRegNamespace(String name) {
        return shConfigMapper.queryIdByRegNamespace(name);
    }

    @Override
    public void deleteInfo(String id) {
        shConfigMapper.deleteInfo(NumberUtils.toInt(id));
    }

    @Override
    public String queryDataSourceNameByid(int id) {
        return shConfigMapper.queryDataSourceNameByid(id);
    }

    @Override
    public void updateStatusById(Long id, byte type) {
        shConfigMapper.updateStatusById(id, type);
    }

    @Override
    public void updateStatusByDataSourceName(String name, Byte type) {
        shConfigMapper.updateStatusByDataSourceName(name, type);
    }

    @Override
    public List<ShConfig> queryByDataSourceName(String dataSourceName) {
        return shConfigMapper.queryByDataSourceName(dataSourceName);
    }
}
