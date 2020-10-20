package org.fcx.mytool.listener;

import lombok.extern.slf4j.Slf4j;
import org.fcx.mytool.controller.FqController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Slf4j
@Component
public class ContextEventListener implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private FqController fq;
    @Value("${mytool.init-proxies}")
    private Boolean initProxies;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            if(initProxies) {
                fq.preLoadProxies();
            } else {
                log.info("don't init proxies");
            }
        } catch (Exception e) {
            log.error("pre load proxies error",e);
        }
    }
}
