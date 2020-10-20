package org.fcx.mytool.controller;

import lombok.extern.slf4j.Slf4j;
import org.fcx.mytool.entity.proxy.ProxyLinks;
import org.fcx.mytool.entity.proxy.clash.Proxy;
import org.fcx.mytool.exception.MyException;
import org.fcx.mytool.util.MyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("fq")
public class FqController {
    @Autowired
    private ProxyLinks proxyLinks;

    private Map<String,Set<Proxy>> proxiesMap = new HashMap<>();
    @Autowired
    private RestTemplate rt;

    @RequestMapping("config/{software}/{alias}")
    public ModelAndView getTpConfig(@PathVariable("software")String software, @PathVariable("alias")String alias, HttpServletRequest servletRequest){

        Set<Proxy> proxies = proxiesMap.get(alias);

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
    public Set<Proxy> downloadAndParseProxies(String link){
        try {
            long ds = System.currentTimeMillis();
            String raw = this.downloadProxyServersRaw(link);
            long de = System.currentTimeMillis();
            log.info("finish download {} ,time {} ms", link,(de - ds));
            if (StringUtils.isEmpty(raw)){
                log.warn("can't download raw proxy info from link: {} ,check link",link);
                return new HashSet<>();
            }
            long ps = System.currentTimeMillis();
            Set<Proxy> proxies = this.parseProxyList(raw);
            long pe = System.currentTimeMillis();
            log.info("parse {} proxy list {} ms", link, pe - ps);
            return proxies;
        } catch (Exception e) {
            log.error("downloadAndParseProxies error link : "+link,e);
            return new HashSet<>();
        }
    }

    /**
     * 手动触发订阅刷新
     */
    @RequestMapping("refresh")
    @ResponseBody
    public String refresh() {
        preLoadProxies();
        return "success";
    }

    /**
     * 定时触发订阅刷新
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void autoRefresh() {
        log.info("auto refresh start");
        preLoadProxies();
        log.info("auto refresh end");
    }

    /**
     * 访问所有的订阅地址、解析结果、并把节点在本地缓存
     */
    public synchronized void preLoadProxies() {
        long start = System.currentTimeMillis();
        proxiesMap = proxyLinks.linksMap.entrySet().parallelStream().map(link -> {
            String key = link.getKey();
            String value = link.getValue();
            Set<Proxy> proxies = downloadAndParseProxies(value);
            return new HashMap.SimpleEntry<>(key,proxies);
        }).collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
        long end = System.currentTimeMillis();
        log.info("preLoadProxies elapsed {} ms",(end-start));
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
    public Set<Proxy> parseProxyList(String raw) {
        String first = MyUtil.base64Decode(raw);
        String[] links = first.split("\n");
        Set<Proxy> proxyList = new TreeSet<>((proxy1,proxy2) -> {
            String p1 = proxy1.getServer()+proxy1.getPort();
            String p2 = proxy2.getServer()+proxy2.getPort();
            return p1.compareTo(p2);
        });
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
