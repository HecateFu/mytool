package org.fcx.mytool.entity.proxy.clash;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.fcx.mytool.exception.MyException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
        String raw = link.substring(9);
        // 密码
        String[] sa1 = raw.split("@");
        this.password = sa1[0];
        // 节点名称
        String[] sa2 = sa1[1].split("#");
        try {
            setName(URLDecoder.decode(sa2[1], StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            throw new MyException("unsupport trojan link format : "+sa2[1]);
        }
        // 服务器
        String[] sa3 = sa2[0].split(":");
        setServer(sa3[0]);
        // 端口
        setPort(Integer.parseInt(sa3[1]));
    }
}
