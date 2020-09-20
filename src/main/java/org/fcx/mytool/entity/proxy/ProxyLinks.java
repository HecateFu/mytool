package org.fcx.mytool.entity.proxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.fcx.mytool.exception.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
@Component
@Slf4j
public class ProxyLinks {
    @Autowired
    private ObjectMapper objectMapper;
    // 可能有并发修改的问题，但只有我自己用，so没有并发的情况
    public Map<String,String> linksMap;
    @Value("${mytool.proxy-links-file-path}")
    private Resource proxyLinksFile;

    @PostConstruct
    public void loadLinks () {
        try {
            if(!proxyLinksFile.exists()) {
                throw new MyException("加载代理链接出错："+proxyLinksFile.getFile().getAbsolutePath()+"不存在");
            }
            linksMap = objectMapper.readValue(proxyLinksFile.getFile(),HashMap.class);
            log.debug("{}",linksMap);
        } catch (IOException e) {
            log.error("init proxy links err",e);
            throw new MyException("init proxy links err",e);
        }
    }

    public void updateProxyLinks () {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(proxyLinksFile.getFile(),this.linksMap);
        } catch (IOException e) {
            log.error("update proxy links error",e);
        }
    }
}
