package com.recommendation.recservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("recservice/page")
public class PageController {

    @RequestMapping("test")
    public String testPage(){
        return "test";
    }
}
