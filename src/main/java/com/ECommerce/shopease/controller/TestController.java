package com.ECommerce.shopease.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/tests")
public class TestController {

    @GetMapping
    public String Test(){
        return "Hello Piyumal";
    }
}
