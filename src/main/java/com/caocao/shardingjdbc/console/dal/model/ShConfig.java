package com.caocao.shardingjdbc.console.dal.model;

import lombok.Data;

@Data
public class ShConfig {
    private Long id;

    private String regNamespace;

    private String regId;

    private String regServerList;

    private String dataSourceName;

    private Byte status;

    private String createBy;

    private String updateBy;
}