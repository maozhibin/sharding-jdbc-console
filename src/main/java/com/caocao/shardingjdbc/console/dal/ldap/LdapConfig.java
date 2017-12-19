package com.caocao.shardingjdbc.console.dal.ldap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("ldapConfig")
public class LdapConfig {
    @Value(value = "${ldap.account}")
    private String account;
    @Value(value = "${ldap.password}")
    private String password;
    @Value(value = "${ldap.providerUrl}")
    private String providerUrl;
    @Value(value = "${ldap.securityAuthentication}")
    private String securityAuthentication;
    @Value(value = "${ldap.root}")
    private String root;

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProviderUrl() {
        return providerUrl;
    }

    public void setProviderUrl(String providerUrl) {
        this.providerUrl = providerUrl;
    }

    public String getSecurityAuthentication() {
        return securityAuthentication;
    }

    public void setSecurityAuthentication(String securityAuthentication) {
        this.securityAuthentication = securityAuthentication;
    }
}
