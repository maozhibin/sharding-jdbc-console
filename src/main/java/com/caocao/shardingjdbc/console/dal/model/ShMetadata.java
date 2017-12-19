package com.caocao.shardingjdbc.console.dal.model;

import lombok.Data;

@Data
public class ShMetadata {
    private Long id;

    private Byte type;

    private String dataSourceName;

    private String createBy;

    private String updateBy;

    private String properties;
}