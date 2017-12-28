package com.caocao.shardingjdbc.console.controller;

import com.caocao.shardingjdbc.console.common.JsonResponseMsg;
import com.caocao.shardingjdbc.console.dal.ldap.LdapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Controller
@RequestMapping("/user")
public class LoginController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private LdapService ldapService;

    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponseMsg login(@RequestParam("username") String username, @RequestParam("password") String password) {
        JsonResponseMsg result = new JsonResponseMsg();
        boolean flag = false;
        try {
            flag = ldapService.authenticate(username, password);
        } catch (Exception e) {
            flag = false;
            logger.error("登录异常", e);
        }
        if (flag) {
            logger.info("登录认证success");
            return result.fill(JsonResponseMsg.CODE_SUCCESS,"登录认证success");
        } else {
            logger.info("登录认证异常");
            return result.fill(JsonResponseMsg.CODE_FAIL,"登录认证异常");
        }
    }
//    @RequestMapping(value = "/logout", method = RequestMethod.POST)
//    @ResponseBody
//    public JsonResponseMsg logout() {
//        JsonResponseMsg result = new JsonResponseMsg();
//        return result.fill(JsonResponseMsg.CODE_FAIL,"登录认证异常");
//    }

}
