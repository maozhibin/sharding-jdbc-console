package com.caocao.shardingjdbc.console.dal.ldap;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;

@Slf4j
@Component("ldapHelper")
public class LdapHelper {
    private DirContext ctx;
    @Autowired
    private LdapConfig ldapConfig;

    @SuppressWarnings(value = "unchecked")
    public DirContext getCtx() {
        //设置连接LDAP的实现工厂
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        // 指定LDAP服务器的主机名和端口号
        env.put(Context.PROVIDER_URL, ldapConfig.getProviderUrl() + ldapConfig.getRoot());
        //给环境提供认证方法
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        //指定进入的目录识别名DN
        env.put(Context.SECURITY_PRINCIPAL, "cn=" + ldapConfig.getAccount());
        //进入的目录密码
        env.put(Context.SECURITY_CREDENTIALS, ldapConfig.getPassword().trim());
        log.info(ldapConfig.getProviderUrl() + ldapConfig.getRoot());
        log.info(ldapConfig.getAccount());
        log.info(ldapConfig.getPassword());

        try {
            // 链接ldap  // 得到初始目录环境的一个引用
            ctx = new InitialDirContext(env);
            log.info("认证成功");
        } catch (javax.naming.AuthenticationException e) {
            log.error("认证失败", e);
        } catch (Exception e) {
            log.error("认证出错：", e);
        }
        return ctx;
    }

    public void closeCtx() {
        try {
            ctx.close();
        } catch (NamingException ex) {
            log.error("error close ctx", ex);
        }
    }

    @SuppressWarnings(value = "unchecked")
    public boolean verifySHA(String ldappw, String inputpw)
            throws NoSuchAlgorithmException {

        // MessageDigest 提供了消息摘要算法，如 MD5 或 SHA，的功能，这里LDAP使用的是SHA-1
        MessageDigest md = MessageDigest.getInstance("SHA-1");

        // 取出加密字符
        if (ldappw.startsWith("{SSHA}")) {
            ldappw = ldappw.substring(6);
        } else if (ldappw.startsWith("{SHA}")) {
            ldappw = ldappw.substring(5);
        }

        // 解码BASE64
        byte[] ldappwbyte = Base64.decode(ldappw);
        byte[] shacode;
        byte[] salt;

        // 前20位是SHA-1加密段，20位后是最初加密时的随机明文
        if (ldappwbyte.length <= 20) {
            shacode = ldappwbyte;
            salt = new byte[0];
        } else {
            shacode = new byte[20];
            salt = new byte[ldappwbyte.length - 20];
            System.arraycopy(ldappwbyte, 0, shacode, 0, 20);
            System.arraycopy(ldappwbyte, 20, salt, 0, salt.length);
        }

        // 把用户输入的密码添加到摘要计算信息
        md.update(inputpw.getBytes());
        // 把随机明文添加到摘要计算信息
        md.update(salt);

        // 按SSHA把当前用户密码进行计算
        byte[] inputpwbyte = md.digest();

        // 返回校验结果
        return MessageDigest.isEqual(shacode, inputpwbyte);
    }

}