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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Duration;
import java.time.Instant;
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

    @Test
    public void updateLinksTest () throws  Exception{
        String resp = mvc.perform(MockMvcRequestBuilders
                .get("/fq/updatelink")
                .param("key","ss")
                .param("link","https%3A%2F%2Fwww.111003.ml%2F%2Flink%2FXp7XUEOqe3dYQVNu%3Fsub%3D1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println(resp);
    }

    @Test
    public void loadProxiesTest () {
        Instant s = Instant.now();
        fq.preLoadProxies();
        Instant e = Instant.now();
        System.out.println(Duration.between(s,e).getSeconds());
    }
}