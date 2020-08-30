package org.fcx.mytool.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class DemoController {
    @RequestMapping("/now")
    public String nowTime(){
        LocalDateTime ldt = LocalDateTime.now();
        return ldt.toString();
    }
}
