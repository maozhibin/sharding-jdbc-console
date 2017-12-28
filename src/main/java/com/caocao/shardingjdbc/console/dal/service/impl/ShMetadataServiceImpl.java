package com.caocao.shardingjdbc.console.dal.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caocao.shardingjdbc.console.common.Constants;
import com.caocao.shardingjdbc.console.dal.dao.ShConfigMapper;
import com.caocao.shardingjdbc.console.dal.dao.ShMetadataMapper;
import com.caocao.shardingjdbc.console.dal.ext.Page;
import com.caocao.shardingjdbc.console.dal.model.ShConfig;
import com.caocao.shardingjdbc.console.dal.model.ShMetadata;
import com.caocao.shardingjdbc.console.dal.service.ShMetadataService;
import com.caocao.shardingjdbc.console.dto.ShMetadataDto;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ShMetadataServiceImpl implements ShMetadataService {
    @Resource
    private ShMetadataMapper shMetadataMapper;
    @Resource
    private ShConfigMapper shConfigMapper;

    @Override
    public void queryDataSourceList(Page<ShMetadataDto> page, String type, String keywords) {
        int total = 0;
        List<ShMetadata> queryDataSourceList = null;
        total = shMetadataMapper.totalCount(type, keywords);
        queryDataSourceList = shMetadataMapper.queryDataSourceList(page.getOffset(), page.getLimit(), type, keywords);
        page.setTotal(total);
        List<ShMetadataDto> queryShMetadataDtoList = new ArrayList<>();
        for (ShMetadata shMetadata : queryDataSourceList) {
            ShMetadataDto shMetadataDto = new ShMetadataDto();
            shMetadataDto.setCreateBy(shMetadata.getCreateBy());
            shMetadataDto.setDataSourceName(shMetadata.getDataSourceName());
            shMetadataDto.setId(shMetadata.getId());
            shMetadataDto.setUpdateBy(shMetadata.getUpdateBy());
            shMetadataDto.setType(shMetadata.getType());
            shMetadataDto.setProperties(shMetadata.getProperties());
            List<ShConfig> shConfigList = shConfigMapper.queryByDataSourceName(shMetadata.getDataSourceName());
            if (CollectionUtils.isEmpty(shConfigList)) {
                shMetadataDto.setQuote((byte) 0);
            } else {
                shMetadataDto.setQuote((byte) 1);
            }

            if (Constants.MYSQL_INTERGER.equals(shMetadata.getType())) {
                shMetadataDto.setTypeValue(Constants.MYSQL);
            } else if (Constants.MASTER_SLAVE_INTERGER.equals(shMetadata.getType())) {
                shMetadataDto.setTypeValue(Constants.MASTER_SLAVE);
            } else if (Constants.SHARDING_INTERGER.equals(shMetadata.getType())) {
                shMetadataDto.setTypeValue(Constants.SHARDING);
            }

            JSONObject object = (JSONObject) JSONObject.parse(shMetadata.getProperties());
            if (Constants.MASTER_SLAVE_INTERGER.equals(shMetadata.getType())) {
                String masterDataSourceName = object.getString("masterDataSourceName");
                JSONArray slaveDataSourceNames = object.getJSONArray("slaveDataSourceNames");
                List<Integer> slaveIds = new ArrayList<>();
                for (int i = 0; i < slaveDataSourceNames.size(); i++) {
                    String name = (String) slaveDataSourceNames.get(i);
                    slaveIds.add(this.queryNameById(name));
                }
                shMetadataDto.setSlaveIds(slaveIds);
                shMetadataDto.setMasterId(this.queryNameById(masterDataSourceName));
            } else if (Constants.SHARDING_INTERGER.equals(shMetadata.getType())) {
                String dataSourceNames = object.getString("dataSourceNames");
                String[] arrs = dataSourceNames.split(",");
                List<Integer> dataSourceNamesIds = new ArrayList<>();
                for (int j = 0; j < arrs.length; j++) {
                    dataSourceNamesIds.add(this.queryNameById(arrs[j]));
                }
                shMetadataDto.setDataSourceNamesId(dataSourceNamesIds);
            }

            queryShMetadataDtoList.add(shMetadataDto);
        }
        page.setRows(queryShMetadataDtoList);
    }

    @Override
    public void deleteInfo(String id) {
        shMetadataMapper.deleteInfo(NumberUtils.toInt(id));
    }

    @Override
    public void insertInfo(ShMetadataDto shMetadataDto) {
        shMetadataMapper.insertInfo(shMetadataDto);
    }

    @Override
    public void updateInfo(ShMetadataDto shMetadataDto) {
        shMetadataMapper.updateInfo(shMetadataDto);
    }

    @Override
    public List<ShMetadata> queryDataSourceCount(String id) {
        return shMetadataMapper.queryDataSourceCount();
    }

    /**
     * 分开分表组装Properties属性
     *
     * @param shMetadataDto
     */
    @Override
    public void installProperties(ShMetadataDto shMetadataDto) {
        Set<Object> dataSources = new HashSet<>();
        Map<String, Object> map = new HashMap<>();
        Integer materId = shMetadataDto.getMasterId();
        List<Integer> slaveIds = shMetadataDto.getSlaveIds();
        ShMetadata masterDataSource = shMetadataMapper.queryInfoById(materId);
        dataSources.add(JSONObject.parse(masterDataSource.getProperties()));
        map.put("name", shMetadataDto.getDataSourceName());
        map.put("masterDataSourceName", masterDataSource.getDataSourceName());
        List<String> slaveDataSourceNames = new ArrayList<>();
        for (Integer str : slaveIds) {
            ShMetadata slaveDataSource = shMetadataMapper.queryInfoById(str);
            slaveDataSourceNames.add(slaveDataSource.getDataSourceName());
            dataSources.add(JSONObject.parse(slaveDataSource.getProperties()));
        }
        map.put("slaveDataSourceNames", slaveDataSourceNames);
        map.put("loadBalanceAlgorithmType", shMetadataDto.getLoadBalanceAlgorithmType());
        map.put("dataSources", dataSources);
        String json = JSONObject.toJSONString(map);
        shMetadataDto.setProperties(json);
    }

    @Override
    public void installPropertiesSharding(ShMetadataDto shMetadataDto) {
        Set<Object> dataSources = new HashSet<>();
        List<Object> masterSlaveRuleConfigs = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("name", shMetadataDto.getDataSourceName());
        map.put("tableRuleConfigs", JSONObject.parse(shMetadataDto.getTableRuleConfigs()));
        map.put("bindingTableGroups", JSONObject.parse(shMetadataDto.getBindingTableGroups()));
        map.put("props", JSONObject.parse(shMetadataDto.getProps()));
        List<Integer> DataSourceNamesIds = shMetadataDto.getDataSourceNamesId();
        String dataSourceNames = "";
        List<String> masterNameList = new ArrayList<>();
        for (int i = 0; i < DataSourceNamesIds.size(); i++) {
            ShMetadata shMetadatas = shMetadataMapper.queryInfoById(DataSourceNamesIds.get(i));
            if (i == DataSourceNamesIds.size() - 1) {
                dataSourceNames += shMetadatas.getDataSourceName();
            } else {
                dataSourceNames += shMetadatas.getDataSourceName() + ",";
            }
            //如果在properties中出现相同的数据库名信息，以master的为准
            JSONObject object = null;
            if (Constants.MASTER_SLAVE_INTERGER.equals(shMetadatas.getType())) {
                object = (JSONObject) JSONObject.parse(shMetadatas.getProperties());
                JSONArray arrays = object.getJSONArray("dataSources");
                for (int j = 0; j < arrays.size(); j++) {
                    JSONObject object1 = (JSONObject) JSONObject.parse(String.valueOf(arrays.get(j)));
                    if (!masterNameList.contains(object1.getString("name"))) {//如果master引用的mysql数据源都相同只保存第一个
                        dataSources.add(arrays.get(j));
                    }
                    masterNameList.add(object1.getString("name"));
                }
//                for (Object array:arrays) {
//                    dataSources.add(array);
//                }
            }
            if (Constants.MASTER_SLAVE_INTERGER.equals(shMetadatas.getType())) {
                Map<String, Object> map1 = new HashMap<>();
                String masterDataSourceName = object.getString("masterDataSourceName");
                JSONArray slaveDataSourceNames = object.getJSONArray("slaveDataSourceNames");
                String name = object.getString("name");
                String loadBalanceAlgorithmType = object.getString("loadBalanceAlgorithmType");
                map1.put("masterDataSourceName", masterDataSourceName);
                map1.put("slaveDataSourceNames", slaveDataSourceNames);
                map1.put("name", name);
                map1.put("loadBalanceAlgorithmType", loadBalanceAlgorithmType);
                masterSlaveRuleConfigs.add(map1);
            }
        }
        //上面有添加则此次添加跳过
        for (int i = 0; i < DataSourceNamesIds.size(); i++) {
            ShMetadata shMetadatas = shMetadataMapper.queryInfoById(DataSourceNamesIds.get(i));
            if (!Constants.MASTER_SLAVE_INTERGER.equals(shMetadatas.getType())) {
                JSONObject object2 = (JSONObject) JSONObject.parse(shMetadatas.getProperties());
                if (!masterNameList.contains(object2.getString("name"))) {
                    dataSources.add(object2);
                }

            }
        }
        map.put("dataSourceNames", dataSourceNames);
        map.put("dataSources", dataSources);
        map.put("masterSlaveRuleConfigs", masterSlaveRuleConfigs);
        String json = JSONObject.toJSONString(map);
        shMetadataDto.setProperties(json);
    }

    @Override
    public ShMetadataDto queryByName(String name) {
        return shMetadataMapper.queryByName(name);
    }

    @Override
    public List<ShMetadata> queryDataSourceCountNoMysql() {
        return shMetadataMapper.queryDataSourceCountNoMysql();
    }


    @Override
    public String queryMasterPropertiesById(int id) {
        return shMetadataMapper.queryMasterPropertiesById(id);
    }

    @Override
    public Integer queryNameById(String dataSourceName) {
        return shMetadataMapper.queryNameById(dataSourceName);
    }


    @Override
    public ShMetadata queryInfoById(int id) {
        return shMetadataMapper.queryInfoById(id);
    }

    @Override
    public List<ShMetadata> queryDataSourceCountNoSharding() {
        return shMetadataMapper.queryDataSourceCountNoSharding();
    }


}
