package com.logzc.webzic.web.controller;

import com.logzc.webzic.annotation.RequestMapping;

import java.util.Map;

/**
 * default error controller. WITHOUT @RestController Annotation.
 * Created by lishuang on 2016/7/25.
 */
@RequestMapping(path = "/webzic")
public class ErrorController {

    @RequestMapping(path = "/error")
    public Map<String, ?> error() {

        return null;
    }
}