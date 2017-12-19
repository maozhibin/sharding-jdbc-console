package com.caocao.shardingjdbc.console.dal.model;

public class ShConfig {
    private Long id;

    private String regNamespace;

    private String regId;

    private String regServerList;

    private String dataSourceName;

    private Byte status;

    private String createBy;

    private String updateBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegNamespace() {
        return regNamespace;
    }

    public void setRegNamespace(String regNamespace) {
        this.regNamespace = regNamespace;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getRegServerList() {
        return regServerList;
    }

    public void setRegServerList(String regServerList) {
        this.regServerList = regServerList;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

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