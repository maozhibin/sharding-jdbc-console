package com.caocao.shardingjdbc.console.dal.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caocao.shardingjdbc.console.common.Constants;
import com.caocao.shardingjdbc.console.dal.dao.ShMetadataMapper;
import com.caocao.shardingjdbc.console.dal.ext.Page;
import com.caocao.shardingjdbc.console.dal.model.ShMetadata;
import com.caocao.shardingjdbc.console.dal.service.ShMetadataService;
import com.caocao.shardingjdbc.console.dto.ShMetadataDto;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ShMetadataServiceImpl implements ShMetadataService {
    @Resource
    private ShMetadataMapper shMetadataMapper;

    @Override
    public void queryDataSourceList(Page<ShMetadataDto> page, String type,String keywords) {
        int total =0;
        List<ShMetadata> queryDataSourceList= null;
        total = shMetadataMapper.totalCount(type,keywords);
        queryDataSourceList= shMetadataMapper.queryDataSourceList(page.getOffset(),page.getLimit(),type,keywords);
        page.setTotal(total);
        List<ShMetadataDto> queryShMetadataDtoList = new ArrayList<>();
        for (ShMetadata shMetadata:queryDataSourceList) {
             ShMetadataDto shMetadataDto = new ShMetadataDto();
            shMetadataDto.setCreateBy(shMetadata.getCreateBy());
            shMetadataDto.setDataSourceName(shMetadata.getDataSourceName());
            shMetadataDto.setId(shMetadata.getId());
            shMetadataDto.setUpdateBy(shMetadata.getUpdateBy());
            shMetadataDto.setType(shMetadata.getType());
            shMetadataDto.setProperties(shMetadata.getProperties());
            if(Constants.MYSQL_INTERGER.equals(shMetadata.getType())){
                shMetadataDto.setTypeValue(Constants.MYSQL);
            }else  if(Constants.MASTER_SLAVE_INTERGER.equals(shMetadata.getType())){
                shMetadataDto.setTypeValue(Constants.MASTER_SLAVE);
            }else if(Constants.SHARDING_INTERGER.equals(shMetadata.getType())){
                shMetadataDto.setTypeValue(Constants.SHARDING);
            }

            JSONObject object = (JSONObject) JSONObject.parse(shMetadata.getProperties());
            String masterDataSourceName = object.getString("masterDataSourceName");
            JSONArray slaveDataSourceNames = object.getJSONArray("slaveDataSourceNames");
            List<Integer> slaveIds = new ArrayList<>();
            for(int i =0;i<slaveDataSourceNames.size();i++){
                String name = (String) slaveDataSourceNames.get(i);
                slaveIds.add(this.queryNameById(name));
            }
            shMetadataDto.setSlaveIds(slaveIds);
            shMetadataDto.setMasterId(this.queryNameById(masterDataSourceName));
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
        List<ShMetadata> list = shMetadataMapper.queryDataSourceCount();
        return list;
    }

    /**
     * 组装Properties属性
     * @param shMetadataDto
     */
    @Override
    public void installProperties(ShMetadataDto shMetadataDto) {
        Set<Object> dataSources = new HashSet<>();
        Map<String,Object> map = new HashMap<>();
        Integer materId = shMetadataDto.getMasterId();
        List<Integer> slaveIds = shMetadataDto.getSlaveIds();
        ShMetadata masterDataSource = shMetadataMapper.queryInfoById(materId);
        dataSources.add(JSONObject.parse(masterDataSource.getProperties()));
        map.put("name",shMetadataDto.getDataSourceName());
        map.put("masterDataSourceName",masterDataSource.getDataSourceName());
        List<String> slaveDataSourceNames = new ArrayList<>();
        for (Integer str:slaveIds) {
            ShMetadata slaveDataSource = shMetadataMapper.queryInfoById(str);
            slaveDataSourceNames.add(slaveDataSource.getDataSourceName());
            dataSources.add(JSONObject.parse(slaveDataSource.getProperties()));
        }
        map.put("slaveDataSourceNames",slaveDataSourceNames);
        map.put("loadBalanceAlgorithmType",shMetadataDto.getLoadBalanceAlgorithmType());
        map.put("dataSources",dataSources);
        String json = JSONObject.toJSONString(map);
        shMetadataDto.setProperties(json);
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
    public void updatePropertiesById(int id, String data) {
        String properties = this.queryMasterPropertiesById(id);
        JSONObject object = (JSONObject) JSONObject.parse(properties);
        String dataSources  =  object.getString("dataSources");

    }

    @Override
    public ShMetadata queryInfoById(int id) {
        return shMetadataMapper.queryInfoById(id);
    }
}
