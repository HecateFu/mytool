package org.fcx.mytool.controller;

import org.fcx.mytool.entity.security.Fund;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class SecurityControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    private SecurityController controller;

    @Test
    public void getFundDataTest() throws Exception {
        mvc.perform(get("/securities/fundData")).andExpect(status().isOk()).andExpect(content().string("success"));
    }

    @Test
    public void restTemplateTest() throws Exception {
        String url = "http://fund.eastmoney.com/data/rankhandler.aspx?op=ph&dt=kf&ft=all&rs=&gs=0&sc=zzf&st=desc&sd=2019-07-14&ed=2020-07-14&qdii=&tabSubtype=,,,,,&pi=1&pn=10000&dx=1&v=0.5065587004601584";
        HttpHeaders head = new HttpHeaders();
        head.set("Connection","keep-alive");
        head.set("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.89 Safari/537.36 Edg/84.0.522.40");
        head.set("Accept","*/*");
        head.set("Referer","http://fund.eastmoney.com/data/fundranking.html");
        head.set("Accept-Encoding","gzip, deflate");
        head.set("Accept-Language","zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
        HttpEntity reqEntity = new HttpEntity(head);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET,reqEntity,byte[].class);
        GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(response.getBody()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(gzip));
        StringWriter writer = new StringWriter();
        String line;
        while ((line = reader.readLine()) != null) {
            writer.write(line);
        }
        String responseString = writer.toString();
        System.out.println(responseString);
    }

    @Test
    public void downloadFundDataTest() throws Exception {
        String raw = controller.downloadFundData();
        System.out.println(raw);
    }

    @Test
    public void parseFundDataTest() throws Exception {
        InputStream is = this.getClass().getResourceAsStream("/rankhandler.js");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"))) {
            String raw = br.readLine();
            List<Fund> fundList = controller.parseFundData(raw);
            fundList.stream().forEach(System.out::println);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}