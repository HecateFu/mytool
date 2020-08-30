package org.fcx.mytool.entity.proxy.clash;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.fcx.mytool.exception.MyException;

@Getter
@Setter
@ToString
public class Proxy {
    private String name;
    private String server;
    private int port;
    private String type;
    public Proxy(String type) {
        this.type = type;
    }

    public static Proxy factory(String link) {
        if(link.startsWith("ssr")){
            return new SSR(link);
        } else if(link.startsWith("vmess")){
            return new Vmess(link);
        } else if(link.startsWith("ss")){
            return new SS(link);
        } else {
            throw new MyException("链接格式不正确："+link);
        }
    }
}
