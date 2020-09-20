package org.fcx.mytool.listener;

import org.fcx.mytool.controller.FqController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class ContextEventListener implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private FqController fq;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        fq.preLoadProxies();
    }
}
