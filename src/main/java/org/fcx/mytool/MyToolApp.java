package org.fcx.mytool;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
@Slf4j
@EnableScheduling
@SpringBootApplication
@MapperScan("org.fcx.mytool.dao.mapper")
public class MyToolApp {

    public static void main(String[] args) {
        ConfigurableApplicationContext ac = SpringApplication.run(MyToolApp.class,args);
        String[] activeProfiles = ac.getEnvironment().getActiveProfiles();
        log.info("activeProfiles = {}", Arrays.toString(activeProfiles));
    }

}