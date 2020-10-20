package org.fcx.mytool.entity.proxy.clash;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.fcx.mytool.exception.MyException;

import java.util.Objects;

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
        } else if (link.startsWith("trojan")) {
            return new Trojan(link);
        } else {
            throw new MyException("链接格式不正确："+link);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proxy proxy = (Proxy) o;
        return port == proxy.port &&
                server.equals(proxy.server) &&
                type.equals(proxy.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(server, port, type);
    }
}
