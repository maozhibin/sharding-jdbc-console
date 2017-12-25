package com.caocao.shardingjdbc.console.dal.service.impl;

import com.caocao.shardingjdbc.console.dal.dao.ShConfigMapper;
import com.caocao.shardingjdbc.console.dal.ext.Page;
import com.caocao.shardingjdbc.console.dal.model.ShConfig;
import com.caocao.shardingjdbc.console.dal.service.ShConfigService;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShConfigServiceImpl implements ShConfigService{
    @Autowired
    private ShConfigMapper shConfigMapper;

    @Override
    public void queryConfigList(Page<ShConfig> page, String keywords) {
        int total = shConfigMapper.totalCount(keywords);
        page.setTotal(total);
        page.setRows(shConfigMapper.queryConfigList(page.getOffset(),page.getLimit(),keywords));
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
}
