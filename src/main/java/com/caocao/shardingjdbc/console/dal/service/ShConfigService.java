package com.caocao.shardingjdbc.console.dal.service;

import com.caocao.shardingjdbc.console.dal.ext.Page;
import com.caocao.shardingjdbc.console.dal.model.ShConfig;

public interface ShConfigService {
    void queryConfigList(Page<ShConfig> page, String keywords);

    void updateInfo(ShConfig shConfig);

    void insertInfo(ShConfig shConfig);

    Integer queryIdByRegNamespace(String name);

    void deleteInfo(String id);
}
