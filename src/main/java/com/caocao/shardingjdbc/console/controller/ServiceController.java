package com.caocao.shardingjdbc.console.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("console")
public class ServiceController {
    @RequestMapping(value = "service/check" ,method = RequestMethod.GET)
    public Long check(){
        return System.currentTimeMillis();
    }
}
