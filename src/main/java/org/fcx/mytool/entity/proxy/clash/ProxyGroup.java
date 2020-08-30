package org.fcx.mytool.entity.proxy.clash;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProxyGroup {
    private String name;
    private String type;
    private List<String> proxies;
    private String url;
    private Integer interval;
    public ProxyGroup(String name,String type,List<String> proxies){
        this.name = name;
        this.type = type;
        this.proxies = proxies;
    }
}
