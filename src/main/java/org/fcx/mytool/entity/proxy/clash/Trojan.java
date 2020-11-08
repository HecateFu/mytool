package org.fcx.mytool.entity.proxy.clash;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.fcx.mytool.exception.MyException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
@Slf4j
@Getter
@Setter
@ToString(callSuper = true)
public class Trojan extends Proxy {
    private String password;
    private boolean udp = true;
    public Trojan() {
        super("trojan");
    }

    public Trojan(String link) {
        super("trojan");
        try {
            String raw = link.substring(9);
            // 密码
            String[] sa1 = raw.split("@");
            this.password = sa1[0];
            // 节点名称
            String[] sa2 = sa1[1].split("#");
            setName(URLDecoder.decode(sa2[1], StandardCharsets.UTF_8.name()));
            // 服务器
            String[] sa3 = sa2[0].split(":");
            setServer(sa3[0]);
            // 端口
            int qmIndex = sa3[1].indexOf("?");
            if(qmIndex>-1){
                String portStr = sa3[1].substring(0,qmIndex);
                setPort(Integer.parseInt(portStr));
            }else {
                setPort(Integer.parseInt(sa3[1]));
            }
        } catch (UnsupportedEncodingException e) {
            log.error("trojan remark decode err ",e);
            throw new MyException("trojan remark decode err : "+link);
        }
    }
}
