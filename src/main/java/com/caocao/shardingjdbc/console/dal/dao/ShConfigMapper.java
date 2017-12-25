package com.caocao.shardingjdbc.console.dal.dao;

import com.caocao.shardingjdbc.console.dal.model.ShConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShConfigMapper {
    int totalCount(@Param("keywords")String keywords);


    List<ShConfig> queryConfigList(@Param("begin") Integer begin, @Param("end") Integer end, @Param("keywords")String keywords);

    void updateInfo(ShConfig shConfig);

    void insertInfo(ShConfig shConfig);

    Integer queryIdByRegNamespace(String name);

    void deleteInfo(int id);

//    int insert(ShConfig record);
//
//    int insertSelective(ShConfig record);
//
//    ShConfig selectByPrimaryKey(Long id);
//
//    int updateByPrimaryKeySelective(ShConfig record);
//
//    int updateByPrimaryKey(ShConfig record);
}