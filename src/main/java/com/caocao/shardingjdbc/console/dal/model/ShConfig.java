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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", regNamespace=").append(regNamespace);
        sb.append(", regId=").append(regId);
        sb.append(", regServerList=").append(regServerList);
        sb.append(", dataSourceName=").append(dataSourceName);
        sb.append(", status=").append(status);
        sb.append(", createBy=").append(createBy);
        sb.append(", updateBy=").append(updateBy);
        sb.append("]");
        return sb.toString();
    }
}