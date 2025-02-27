package com.recommendation.staticservice.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("page")
public class PageController {
    @RequestMapping("/users/info")
    public String userHomePage(){
        return "user";
    }
    @GetMapping("/index")
    public String indexPage(){
        return "index";
    }
    @GetMapping("/movies/info")
    public String movieInfoPage(){
        return "movie";
    }
    @GetMapping("/rec")
    public String recPage(){return "rec";}
    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }

    @GetMapping("/register")
    public String regPage(){
        return "register";
    }
}
