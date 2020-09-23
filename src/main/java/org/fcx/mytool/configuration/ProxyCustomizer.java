package org.fcx.mytool.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
@Slf4j
@Configuration
public class ProxyCustomizer implements RestTemplateCustomizer {
    @Value("${mytool.proxy-host}")
    private String proxyHost;
    @Value("${mytool.proxy-port}")
    private Integer proxyPort;
    @Value("${mytool.use-proxy}")
    private Boolean useProxy;
    @Override
    public void customize(RestTemplate restTemplate) {
        log.info("use proxy {} , host: {} , port: {} ",useProxy,proxyHost,proxyPort);
        HttpHost proxy = new HttpHost(proxyHost,proxyPort);
        HttpClient client = HttpClientBuilder.create().setRoutePlanner(new DefaultProxyRoutePlanner(proxy) {
            @Override
            public HttpHost determineProxy(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
                if(useProxy && target.getHostName().equals("raw.githubusercontent.com")) {
                    return super.determineProxy(target, request, context);
                } else {
                    return null;
                }
            }
        }).build();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
    }
}
