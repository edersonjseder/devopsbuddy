package com.devopsbuddy.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by root on 04/06/17.
 */
@Controller
public class IndexController {

    @RequestMapping("/")
    public String home(){
        return "index";
    }
}
