package com.caocao.shardingjdbc.console.dal.dao;

import com.caocao.shardingjdbc.console.dal.model.ShConfig;
import com.caocao.shardingjdbc.console.dto.ShConfigDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShConfigMapper {
    int totalCount(@Param("keywords") String keywords);

    List<ShConfigDto> queryConfigList(@Param("begin") Integer begin, @Param("end") Integer end,
                                      @Param("keywords") String keywords);

    void updateInfo(ShConfig shConfig);

    void insertInfo(ShConfig shConfig);

    Integer queryIdByRegNamespace(String name);

    void deleteInfo(int id);

    String queryDataSourceNameByid(int id);

    void updateStatusById(@Param("id") Long id, @Param("type") byte type);

    void updateStatusByDataSourceName(@Param("name") String name, @Param("type") Byte type);

    List<ShConfig> queryByDataSourceName(String dataSourceName);

}