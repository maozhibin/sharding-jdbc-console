package com.caocao.shardingjdbc.console.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ShConfigDto {
    private Long id;

    private String regNamespace;

    private String regId;

    private String regServerList;

    private String dataSourceName;

    private Byte status;

    private String statusValue;

    private String createBy;

    private String updateBy;

    private Map<String,Object> zkInfo;
}
