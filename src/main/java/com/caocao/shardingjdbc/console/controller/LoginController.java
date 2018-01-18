package com.caocao.shardingjdbc.console.controller;

import com.caocao.shardingjdbc.console.common.Encryption;
import com.caocao.shardingjdbc.console.common.JsonResponseMsg;
import com.caocao.shardingjdbc.console.dal.ldap.LdapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Controller
@Slf4j
@RequestMapping("/user")
public class LoginController {
    @Autowired
    private LdapService ldapService;

    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponseMsg login(@RequestParam("username") String username, @RequestParam("password") String password) {
        JsonResponseMsg result = new JsonResponseMsg();
        boolean flag = false;
        try {
            flag = ldapService.authenticate(new String(Encryption.decode(username)), new String(Encryption.decode(password)));
        } catch (Exception e) {
            flag = false;
            log.error("{}登录异常", new String(Encryption.decode(username)), e);
        }
        if (flag) {
            log.info("登录认证success:{}", new String(Encryption.decode(username)));
            return result.fill(JsonResponseMsg.CODE_SUCCESS, "登录认证success");
        } else {
            log.info("登录认证异常,{}", new String(Encryption.decode(username)));
            return result.fill(JsonResponseMsg.CODE_FAIL, "登录认证异常");
        }
    }
}
