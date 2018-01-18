package com.caocao.shardingjdbc.console.common;

import com.alibaba.druid.filter.config.ConfigTools;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Utils {
    /**
     * 解密
     *
     * @param encPassword
     * @return
     */
    public static String druidDec(String encPassword) {
        String encString = null;
        try {
            encString = ConfigTools.decrypt(encPassword);
        } catch (Exception e) {
            log.error("error druidDec:{}", encPassword, e);
        }
        return encString;
    }

    /**
     * 加密
     *
     * @param password
     * @return
     */
    public static String druidEnc(String password) {
        String encString = null;
        try {
            encString = ConfigTools.encrypt(password);
        } catch (Exception e) {
            log.error("error druidEnc:{}", password, e);
        }
        return encString;
    }
}
