package org.fcx.mytool.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import lombok.extern.slf4j.Slf4j;
import org.fcx.mytool.entity.proxy.ProxyLinks;
import org.fcx.mytool.entity.proxy.clash.Config;
import org.fcx.mytool.entity.proxy.clash.Proxy;
import org.fcx.mytool.exception.MyException;
import org.fcx.mytool.util.MyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("fq")
public class FqController {
    @Autowired
    private ProxyLinks proxyLinks;

    private Map<String,List<Proxy>> proxiesMap = new HashMap<>();
    @Autowired
    private RestTemplate rt;

    @ResponseBody
    @RequestMapping("config/{alias}.yml")
    public String getConfig(@PathVariable("alias")String alias){
        List<Proxy> proxies = proxiesMap.get(alias);

        if(CollectionUtils.isEmpty(proxies)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"not found alias :"+alias);
        }
        Config config = new Config(proxies);

        StringWriter sw = new StringWriter();
        YAMLFactory yf = new YAMLFactory();
        yf.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);
        yf.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        ObjectMapper om = new ObjectMapper(yf);
        try {
            om.writeValue(sw,config);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sw.toString();
    }

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
//            log.info("start download server from {}", link);
            long ds = System.currentTimeMillis();
            String raw = this.downloadProxyServersRaw(link);
            long de = System.currentTimeMillis();
            log.info("finish download {} ,time {} ms", link,(de - ds));
            if (StringUtils.isEmpty(raw)){
                log.warn("can't download raw proxy info from link: {} ,check link",link);
                return new ArrayList<>();
            }
            long ps = System.currentTimeMillis();
            List<Proxy> proxies = this.parseProxyList(raw);
            long pe = System.currentTimeMillis();
            log.info("parse {} proxy list {} ms", link, pe - ps);
            return proxies;
        } catch (Exception e) {
            log.error("downloadAndParseProxies error link : "+link,e);
            return new ArrayList<>();
        }
    }

    /**
     * 访问所有的订阅地址、解析结果、并把节点在本地缓存
     */
    @RequestMapping("refresh")
    @ResponseBody
    public String preLoadProxies() {
        proxiesMap = proxyLinks.linksMap.entrySet().parallelStream().map(link -> {
            String key = link.getKey();
            String value = link.getValue();
            List<Proxy> proxies = downloadAndParseProxies(value);
            return new HashMap.SimpleEntry<>(key,proxies);
        }).collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
        return "success";
    }

    @GetMapping("updatelink")
    @ResponseBody
    public Map<String,String> updateLinks(@RequestParam String key,
                                          @RequestParam(required = false) String link,
                                          @RequestParam(required = false) String del){
        if("true".equals(del)){
            proxyLinks.linksMap.remove(key);
        } else {
            try {
                if (StringUtils.isEmpty(link)) {
                    return Collections.singletonMap("error","miss link");
                }
                proxyLinks.linksMap.put(key, URLDecoder.decode(link,"utf-8"));
            } catch (UnsupportedEncodingException e) {
                log.error("updatelinks decode links error",e);
            }
        }
        proxyLinks.updateProxyLinks();
        return proxyLinks.linksMap;
    }

    /**
     * 请求代理订阅地址返回原始结果
     * @param link
     * @return
     */
    public String downloadProxyServersRaw(String link){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_PLAIN));
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        ResponseEntity<String> resp = rt.exchange(link,HttpMethod.GET,entity,String.class);
        String raw = resp.getBody();
        if(HttpStatus.OK.equals(resp.getStatusCode())){
            return raw;
        } else {
            log.error("get proxy servers info failed, request link {} , resp status code{},resp body {}",link,resp.getStatusCode(),raw);
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
