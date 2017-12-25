package com.caocao.shardingjdbc.console.dal.service;

import com.caocao.shardingjdbc.console.dal.ext.Page;
import com.caocao.shardingjdbc.console.dal.model.ShMetadata;
import com.caocao.shardingjdbc.console.dto.ShMetadataDto;

import java.util.List;

public interface ShMetadataService {
    void queryDataSourceList(Page<ShMetadataDto> page, String type, String keywords);

    void deleteInfo(String id);

    void insertInfo(ShMetadataDto shMetadataDto);

    void updateInfo(ShMetadataDto shMetadataDto);

    List<ShMetadata> queryDataSourceCount(String id);

    void installProperties(ShMetadataDto shMetadataDto);

    String queryMasterPropertiesById(int id);

    Integer queryNameById(String dataSourceName);

    void updatePropertiesById(int id, String data);

    ShMetadata queryInfoById(int id);

    List<ShMetadata> queryDataSourceCountNoSharding();

    void installPropertiesSharding(ShMetadataDto shMetadataDto);

    ShMetadataDto queryByName(String name);

    List<ShMetadata> queryDataSourceCountNoMysql();
}
