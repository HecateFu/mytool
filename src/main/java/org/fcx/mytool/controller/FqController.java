package org.fcx.mytool.controller;

import lombok.extern.slf4j.Slf4j;
import org.fcx.mytool.entity.proxy.ProxyLinks;
import org.fcx.mytool.entity.proxy.clash.Proxy;
import org.fcx.mytool.exception.MyException;
import org.fcx.mytool.util.MyUtil;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("fq")
public class FqController {

    private static Map<String,List<Proxy>> proxiesMap = new HashMap<>();

    @RequestMapping("config/{software}/{alias}")
    public ModelAndView getTpConfig(@PathVariable("software")String software, @PathVariable("alias")String alias, HttpServletRequest servletRequest){

        List<Proxy> proxies = proxiesMap.get(alias);

        if(CollectionUtils.isEmpty(proxies)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"not found alias :"+alias);
        }

        String updateUrl = servletRequest.getRequestURL().toString();

        Map<String,Object> param = new HashMap<String,Object>();
        param.put("updateUrl",updateUrl);
        param.put("proxies",proxies);
        return new ModelAndView("proxy/"+software,param);
    }

    /**
     * 请求代理订阅地址并将请求结果解析成List<Proxy>
     * @param link
     * @return
     */
    public List<Proxy> downloadAndParseProxies(String link){
        try {
            log.info("start download server from {}", link);
            long ds = System.currentTimeMillis();
            String raw = this.downloadProxyServersRaw(link);
            long de = System.currentTimeMillis();
            log.info("finish download {} ms", de - ds);

            long ps = System.currentTimeMillis();
            List<Proxy> proxies = this.parseProxyList(raw);
            long pe = System.currentTimeMillis();
            log.info("parse proxy list {} ms", pe - ps);
            return proxies;
        } catch (Exception e) {
            log.error("downloadAndParseProxies link : "+link,e);
            return null;
        }
    }

    /**
     * 访问所有的订阅地址、解析结果、并把节点在本地缓存
     */
    @RequestMapping("refresh")
    @ResponseBody
    public String preLoadProxies() {
        for (Map.Entry<String,String> links:ProxyLinks.linksMap.entrySet()){
            String key = links.getKey();
            String link = links.getValue();
            List<Proxy> proxies = downloadAndParseProxies(link);
            proxiesMap.put(key,proxies);
        }
        return "success";
    }

    /**
     * 请求代理订阅地址返回原始结果
     * @param link
     * @return
     */
    public String downloadProxyServersRaw(String link){
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_PLAIN));
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        ResponseEntity<String> resp = rt.exchange(link,HttpMethod.GET,entity,String.class);
        String raw = resp.getBody();
        if(HttpStatus.OK.equals(resp.getStatusCode())){
            return raw;
        } else {
            log.error("get proxy servers info failed, request link {}, resp status code{},resp body {}",link,resp.getStatusCode(),raw);
            throw new MyException("get proxy servers info failed");
        }
    }

    /**
     * 解析原始的加密节点信息
     * @param raw
     * @return
     */
    public List<Proxy> parseProxyList(String raw) {
        String first = MyUtil.base64Decode(raw);
        String[] links = first.split("\n");
        List<Proxy> proxyList = new ArrayList<>();
        for (String s : links) {
            try {
                Proxy proxy = Proxy.factory(s);
                proxyList.add(proxy);
            } catch (MyException ex) {
                log.warn(ex.getMessage());
            }
        }
        return proxyList;
    }

}
