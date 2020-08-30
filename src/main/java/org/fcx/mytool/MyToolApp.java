package org.fcx.mytool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;
@Slf4j
@SpringBootApplication
public class MyToolApp {

    public static void main(String[] args) {
        ConfigurableApplicationContext ac = SpringApplication.run(MyToolApp.class,args);
        String[] activeProfiles = ac.getEnvironment().getActiveProfiles();
        log.info("activeProfiles = {}", Arrays.toString(activeProfiles));
    }

}