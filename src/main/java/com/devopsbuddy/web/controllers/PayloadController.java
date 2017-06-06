package com.devopsbuddy.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by root on 05/06/17.
 */
@Controller
public class PayloadController {

    /** The payload view name */
    public static final String PAYLOAD_VIEW_NAME = "payload/payload";

    @RequestMapping("/payload")
    public String login() {
        return PAYLOAD_VIEW_NAME;
    }
}
