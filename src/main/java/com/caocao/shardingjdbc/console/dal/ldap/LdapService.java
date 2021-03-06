package com.caocao.shardingjdbc.console.dal.ldap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.security.NoSuchAlgorithmException;

/**
 * Example code for retrieving a Users Primary Group
 * from Microsoft Active Directory via. its LDAP API
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
@Service
@Slf4j
public class LdapService {
    @Autowired
    private LdapHelper ldapHelper;

    public boolean addUser(String usr, String pwd) {
        DirContext ctx = null;
        try {
            ctx = ldapHelper.getCtx();
            BasicAttributes attrsbu = new BasicAttributes();
            BasicAttribute objclassSet = new BasicAttribute("objectclass");
            objclassSet.add("person");
            objclassSet.add("top");
            objclassSet.add("organizationalPerson");
            objclassSet.add("inetOrgPerson");
            attrsbu.put(objclassSet);
            attrsbu.put("sn", usr);
            attrsbu.put("uid", usr);
            attrsbu.put("userPassword", pwd);
            ctx.createSubcontext("cn=" + usr + ",ou=People", attrsbu);
            ctx.close();
            return true;
        } catch (NamingException ex) {
            try {
                if (ctx != null) {
                    ctx.close();
                }
            } catch (NamingException namingException) {
                log.error("error add User", namingException);
            }
        }
        return false;
    }

    public boolean authenticate(String usr, String pwd) {
        boolean success = false;
        DirContext ctx = null;
        try {
            ctx = ldapHelper.getCtx();
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            // constraints.setSearchScope(SearchControls.ONELEVEL_SCOPE);
            NamingEnumeration en = ctx.search("", "cn=" + usr, constraints); // 查询所有用户
            log.info("en is " + (en == null));
            while (en != null && en.hasMoreElements()) {
                Object obj = en.nextElement();
                if (obj instanceof SearchResult) {
                    SearchResult si = (SearchResult) obj;
                    log.info("name:   " + si.getName() + ",input name:" + usr);
                    Attributes attrs = si.getAttributes();
                    if (attrs == null) {
                        log.info("No   attributes");

                    } else {
                        Attribute attr = attrs.get("userPassword");
                        Object o = attr.get();
                        byte[] s = (byte[]) o;
                        String pwd2 = new String(s);
                        log.info("passwd: " + pwd2 + ",input passwd: " + pwd);
                        success = ldapHelper.verifySHA(pwd2, pwd);
                        return success;
                    }
                } else {
                    log.info("{}", obj);
                }
            }
            ctx.close();
        } catch (NoSuchAlgorithmException ex) {
            log.info(ex.getLocalizedMessage());
            try {
                if (ctx != null) {
                    ctx.close();
                }
            } catch (NamingException namingException) {
                namingException.printStackTrace();
            }
        } catch (NamingException ex) {
            log.info(ex.getLocalizedMessage());
            try {
                if (ctx != null) {
                    ctx.close();
                }
            } catch (NamingException namingException) {
                log.error("error auth user", namingException);
            }
        }
        return false;
    }


}