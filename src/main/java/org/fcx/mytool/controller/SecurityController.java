package org.fcx.mytool.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.fcx.mytool.entity.security.Fund;
import org.fcx.mytool.exception.MyException;
import org.fcx.mytool.mapper.FundMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("securities")
@Slf4j
public class SecurityController {
    @Autowired
    private FundMapper fundMapper;

    @GetMapping("fundData")
    public String getFundData(){
        String raw = this.downloadFundData();
        List<Fund> fundList = null;
        try {
            fundList = this.parseFundData(raw);
        } catch (IOException e) {
            log.error("parse fund raw data failed ",e);
        }
        if(fundList==null){
            return "failed";
        }
        this.saveFundData(fundList);
        return "success";
    }

    public String downloadFundData () {

        String url = "http://fund.eastmoney.com/data/rankhandler.aspx?op=ph&dt=kf&ft=all&rs=&gs=0&sc=zzf&st=desc&sd=2018-01-01&ed=2018-12-31&qdii=&tabSubtype=,,,,,&pi=1&pn=10000&dx=1&v=0.5065587004601584";
        HttpGet get = new HttpGet(url);
        get.addHeader("Connection","keep-alive");
        get.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.89 Safari/537.36 Edg/84.0.522.40");
        get.addHeader("Accept","*/*");
        get.addHeader("Referer","http://fund.eastmoney.com/data/fundranking.html");
        get.addHeader("Accept-Encoding","gzip, deflate");
        get.addHeader("Accept-Language","zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
        String raw = null;
        try(CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse resp = httpClient.execute(get);) {
            HttpEntity entity = resp.getEntity();
            raw = EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            log.error("download fund raw data failed",e);
        }
        return raw;
    }

    public List<Fund> parseFundData (String raw) throws IOException {
        if(StringUtils.isEmpty((raw))){
            throw new MyException("下载基金数据失败，无数据");
        }
        int first = raw.indexOf("[");
        int last = raw.lastIndexOf("]");
        if (first < 0 || last < 0) {
            throw new MyException("下载基金数据失败:"+raw);
        }
        String fundraw = raw.substring(first,last+1);
        ObjectMapper jsonMapper = new ObjectMapper();
        List<String> fundStrList = jsonMapper.readValue(fundraw, List.class);
        List<Fund> fundList = fundStrList.stream().map(fundstr -> new Fund(fundstr)).collect(Collectors.toList());
        return fundList;
    }

    @Transactional
    public void saveFundData (List<Fund> fundList) {
        long start = System.currentTimeMillis();
        Collections.synchronizedList(fundList).parallelStream().forEach(fund -> {
//            System.out.println(fund.toString());
            fundMapper.insert(fund);
        });
        long end = System.currentTimeMillis();
        System.out.printf("耗时：%d %n",end-start);
    }
}
