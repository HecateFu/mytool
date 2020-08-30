package org.fcx.mytool.controller;

import org.fcx.mytool.entity.proxy.clash.Proxy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class FqControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    FqController fq;
    @Test
    public void getProxyServersRawTest() throws Exception {
        mvc.perform(get("/fq/config/ml.yaml")).andExpect(status().isOk());
    }
    @Test
    public void decodeServerInfoTest() throws Exception{
        String link = "https://jiang.netlify.com/";
        String raw = fq.downloadProxyServersRaw(link);
        List<Proxy> proxies = fq.parseProxyList(raw);
        proxies.stream().forEach(System.out::println);
    }
}