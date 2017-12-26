package com.caocao.shardingjdbc.console.dal.service;

import com.caocao.shardingjdbc.console.dal.ext.Page;
import com.caocao.shardingjdbc.console.dal.model.ShConfig;
import com.caocao.shardingjdbc.console.dto.ShConfigDto;

import java.util.List;

public interface ShConfigService {
    void queryConfigList(Page<ShConfigDto> page, String keywords);

    void updateInfo(ShConfig shConfig);

    void insertInfo(ShConfig shConfig);

    Integer queryIdByRegNamespace(String name);

    void deleteInfo(String id);

    String queryDataSourceNameByid(int id);

    void updateStatusById(Long id, byte type);

    void updateStatusByDataSourceName(String name, Byte type);

    List<ShConfig> queryByDataSourceName(String dataSourceName);
}
