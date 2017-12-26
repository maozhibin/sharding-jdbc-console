package com.caocao.shardingjdbc.console.dal.service.impl;

import com.caocao.shardingjdbc.console.dal.dao.ShConfigMapper;
import com.caocao.shardingjdbc.console.dal.ext.Page;
import com.caocao.shardingjdbc.console.dal.model.ShConfig;
import com.caocao.shardingjdbc.console.dal.service.ShConfigService;
import com.caocao.shardingjdbc.console.dto.ShConfigDto;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShConfigServiceImpl implements ShConfigService{
    @Autowired
    private ShConfigMapper shConfigMapper;

    @Override
    public void queryConfigList(Page<ShConfigDto> page, String keywords) {
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

    @Override
    public String queryDataSourceNameByid(int id) {
        return shConfigMapper.queryDataSourceNameByid(id);
    }

    @Override
    public void updateStatusById(Long id, byte type) {
        shConfigMapper.updateStatusById(id,type);
    }

    @Override
    public void updateStatusByDataSourceName(String name, Byte type) {
        shConfigMapper.updateStatusByDataSourceName(name,type);
    }

    @Override
    public List<ShConfig> queryByDataSourceName(String dataSourceName) {
       return shConfigMapper.queryByDataSourceName(dataSourceName);
    }
}
