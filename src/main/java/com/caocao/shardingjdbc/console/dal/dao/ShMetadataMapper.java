package com.caocao.shardingjdbc.console.dal.dao;

import com.caocao.shardingjdbc.console.dal.model.ShMetadata;
import com.caocao.shardingjdbc.console.dto.ShMetadataDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShMetadataMapper {
    int totalCount(@Param("type") String type, @Param("keywords") String keywords);

    List<ShMetadata> queryDataSourceList(@Param("begin") Integer begin, @Param("end") Integer end,
                                         @Param("type") String type, @Param("keywords") String keywords);

    int deleteInfo(int i);

    void insertInfo(ShMetadataDto shMetadataDto);

    void updateInfo(ShMetadataDto shMetadataDto);

    List<ShMetadata> queryDataSourceCount();

    ShMetadata queryInfoById(Integer materId);

    String queryMasterPropertiesById(int i);

    Integer queryNameById(String dataSourceName);

    List<ShMetadata> queryDataSourceCountNoSharding();

    ShMetadataDto queryByName(String name);

    List<ShMetadata> queryDataSourceCountNoMysql();

    String queryPropertiesByDataSourceName(String dataSourceName);
}