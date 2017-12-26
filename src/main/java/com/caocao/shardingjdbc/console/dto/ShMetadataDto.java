package com.caocao.shardingjdbc.console.dto;

import lombok.Data;

import java.util.List;

@Data
public class ShMetadataDto {
    private Long id;

    private Byte type;

    private Byte quote;

    private String dataSourceName;

    private String createBy;

    private String updateBy;

    private String properties;

    private String typeValue;//type中文字段

    private Integer masterId; //主库id

    private List<Integer> slaveIds;//从库ids

    private String loadBalanceAlgorithmType;//分片算法

    private List<Integer> dataSourceNamesId;//分开分表使用的数据库id

    private String dataSourceNames;


    private String tableRuleConfigs;

    private String bindingTableGroups;

    private String props;

}
