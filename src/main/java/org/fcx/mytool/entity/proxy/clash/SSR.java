package org.fcx.mytool.entity.proxy.clash;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.fcx.mytool.exception.MyException;
import org.fcx.mytool.util.MyUtil;

/**
 * ssr://server:port:protocol:method:obfs:password_base64/?obfsparam=base64&protoparam=base46&remarks=base64&group=base64
 * server 服务器地址
 * port 服务器端口
 * protocol 协议
 * cipher = method 加密算法
 * obfs 混淆方式
 * password 密码
 * obfs-param = obfsparam 混淆参数
 * protocol-param = protoparam 协议参数
 * remarks = name 节点名称
 * group 组群名称（clash，surfboard中用不到，ssr client中使用）
 */
@Getter
@Setter
@ToString(callSuper = true)
@Slf4j
public class SSR extends Proxy {
    private String cipher;
    private String password;
    private String protocol;
    private boolean udp = true;
    @JsonProperty("protocol-param")
    private String protocolParam;
    private String obfs;
    @JsonProperty("obfs-param")
    private String obfsParam;

    public SSR() {
        super("ssr");
    }

    public SSR(String ssrLink) {
        super("ssr");
        String raw = ssrLink.substring(6);
        String decodeServer = MyUtil.base64UrlDecode(raw);
        log.debug(decodeServer);
        String[] fields = decodeServer.split("/\\?");
        String[] serverInfo = fields[0].split(":");
        // 服务器地址
        setServer(serverInfo[0]);
        // 服务器端口
        setPort(Integer.parseInt(serverInfo[1]));
        // 协议
        this.protocol = serverInfo[2];
        // 加密算法
        this.cipher = serverInfo[3];
        // 混淆方式
        this.obfs = serverInfo[4];
        // 密码
        this.password = MyUtil.base64UrlDecode(serverInfo[5]);
        String[] encodedParams = fields[1].split("&");
        for(String kvstr : encodedParams) {
            String[] kv = kvstr.split("=");

            switch (kv[0]) {
                case "obfsparam":// 混淆参数
                    if(kv.length!=2){
                        if(obfs.equals("plain")){
                            this.obfsParam = "plain";
                        } else {
                            log.warn("create ssr miss obfs-param obfs: "+obfs+",serverInfo: "+decodeServer);
                        }
                    } else {
                        this.obfsParam = MyUtil.base64UrlDecode(kv[1]);
                    }
                    break;
                case "protoparam":// 协议参数
                    if(kv.length!=2){
                        if(protocol.equals("origin")){
                            this.protocolParam = "origin";
                        } else {
                            log.warn("create ssr miss protocol-param protocol: "+protocol+",serverInfo: "+decodeServer);
                        }
                    } else {
                        this.protocolParam = MyUtil.base64UrlDecode(kv[1]);
                    }
                    break;
                case "remarks":
                    setName(MyUtil.base64UrlDecode(kv[1]));
                    break;
            }
        }
    }
}
