package org.fcx.mytool.entity.proxy.clash;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.fcx.mytool.exception.MyException;
import org.fcx.mytool.util.JacksonObjectMapperUtil;
import org.fcx.mytool.util.MyUtil;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Slf4j
public class Vmess extends Proxy {
    private String uuid;
    private int alterId;
    private String cipher;
    private boolean tls;
    private String network;
    private boolean udp = true;
    @JsonProperty("ws-path")
    private String wsPath;
    @JsonProperty("ws-headers")
    private Map<String,String> wsHeaders;

    public Vmess() {
        super("vmess");
    }

    public Vmess (String link) {
        super("vmess");
        String raw = link.substring(8);
        String serverJsonStr = MyUtil.base64Decode(raw);
        log.debug(serverJsonStr);
        ObjectMapper om = JacksonObjectMapperUtil.getJsonMapper();
        try {
            Map<String,?> map = om.readValue(serverJsonStr,Map.class);
            setName((String)map.get("ps"));
            if(getName().startsWith("??")){
                setName(getName().replace("??",""));
            }
            setServer((String)map.get("add"));
            if (map.get("port") instanceof String) {
                setPort(Integer.parseInt((String)map.get("port")));
            }else {
                setPort((Integer) map.get("port"));
            }
            this.uuid = (String)map.get("id");
            if(map.get("aid")!=null){
                if(map.get("aid") instanceof String) {
                    this.alterId = Integer.parseInt((String) map.get("aid"));
                } else {
                    this.alterId = (Integer)map.get("aid");
                }
            } else {
                this.alterId = (Integer)map.get("alterId");
            }
            this.cipher = "auto";
            this.tls = map.get("tls")!=null && map.get("tls").equals("tls");
            this.network = (String)map.get("net");
            if(this.network.equals("ws")) {
                this.wsPath = (String)map.get("path");
                if (StringUtils.isEmpty(wsPath)){
                    this.wsPath="/";
                }
                this.wsHeaders = new HashMap<>();
//                this.wsHeaders.put("Host",(String)map.get("add"));
                if(StringUtils.isEmpty(map.get("host"))){
                    this.wsHeaders.put("Host",(String)map.get("add"));
                } else {
                    this.wsHeaders.put("Host",(String)map.get("host"));
                }
            }
        } catch (IOException e) {
            log.error("create vmess failed",e);
            MyException me = new MyException("create vmess jsonStr parse Map err : "+serverJsonStr);
            me.setStackTrace(e.getStackTrace());
            throw me;
        }
    }
}
