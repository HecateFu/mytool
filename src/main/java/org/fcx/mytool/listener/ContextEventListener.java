package org.fcx.mytool.listener;

import lombok.extern.slf4j.Slf4j;
import org.fcx.mytool.controller.FqController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class ContextEventListener implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private FqController fq;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {

            fq.preLoadProxies();
        } catch (Exception e) {
            log.error("pre load proxies error",e);
        }
    }
}
