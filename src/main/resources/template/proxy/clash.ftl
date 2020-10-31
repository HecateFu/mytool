mixed-port: 7890
allow-lan: true
mode: rule
log-level: info
external-controller: '127.0.0.1:9090'
secret: ''

proxies:
<#list proxies as proxy>
  <#if proxy.type == "vmess">
  - {name: '${proxy?counter} ${proxy.name}', server: ${proxy.server}, port: ${proxy.port?c}, type: ${proxy.type}, udp: true, uuid: ${proxy.uuid}, alterId: ${proxy.alterId?c}, cipher: auto, tls: ${proxy.tls?c}<#if proxy.network??>, network: ${proxy.network}</#if><#if proxy.wsPath??>, ws-path: ${proxy.wsPath}</#if><#if proxy.wsHeaders??>, ws-headers: {<#list proxy.wsHeaders as k,v>${k}: ${v}<#sep>,</#list>}</#if>}
  <#elseif proxy.type == "ss" && proxy.cipher != "rc4">
  - {name: '${proxy?counter} ${proxy.name}', server: ${proxy.server}, port: ${proxy.port?c}, type: ${proxy.type}, udp: true, cipher: ${proxy.cipher}, password: ${proxy.password}<#if proxy.plugin??>, plugin: ${proxy.plugin}, plugin-opts: {<#list proxy.pluginOpts as k,v>${k}: ${v}<#sep>,</#list>}</#if>}
  <#elseif proxy.type == "ssr" && proxy.cipher != "none" && proxy.cipher != "rc4" && proxy.cipher != "chacha20">
  - {name: '${proxy?counter} ${proxy.name}', server: ${proxy.server}, port: ${proxy.port?c}, type: ${proxy.type}, udp: true, cipher: ${proxy.cipher}, password: ${proxy.password}, protocol: ${proxy.protocol}, obfs: ${proxy.obfs}<#if proxy.protocolParam??>, protocol-param: ${proxy.protocolParam}</#if><#if proxy.obfsParam??>, obfs-param: ${proxy.obfsParam}</#if>}
  <#elseif proxy.type == "trojan">
  - {name: '${proxy?counter} ${proxy.name}', server: ${proxy.server}, port: ${proxy.port?c}, type: ${proxy.type}, udp: true, password: ${proxy.password}}
  </#if>
</#list>
proxy-groups:
  - name: Proxy
    type: select
    proxies:
      - fallback-auto
      <#list proxies as proxy>
      <#if (proxy.type == "ssr" && proxy.cipher != "none" && proxy.cipher != "rc4" && proxy.cipher != "chacha20") || (proxy.type == "ss" && proxy.cipher != "rc4") || (proxy.type != "ssr" && proxy.type != "ss")>
      - '${proxy?counter} ${proxy.name}'
      </#if>
      </#list>
  - name: fallback-auto
    type: fallback
    url: http://www.gstatic.com/generate_204
    interval: 300
    proxies:
      <#list proxies as proxy>
      <#if (proxy.type == "ssr" && proxy.cipher != "none" && proxy.cipher != "rc4" && proxy.cipher != "chacha20") || (proxy.type == "ss" && proxy.cipher != "rc4") || (proxy.type != "ssr" && proxy.type != "ss")>
      - '${proxy?counter} ${proxy.name}'
      </#if>
      </#list>
  - name: Direct
    type: select
    proxies:
      - DIRECT
      - Proxy
      - REJECT
  - name: Ad
    type: select
    proxies:
      - REJECT
      - DIRECT
      - Proxy
  - name: Other
    type: select
    proxies:
      - Proxy
      - DIRECT
      - REJECT
rules:
<#import "proxy_rules.ftl" as pr>
<#import "direct_rules.ftl" as dr>
<#import "final_rules.ftl" as fr>
<#list pr.rules as r>
  - ${r}
</#list>
<#list dr.rules as r>
  - ${r}
</#list>
<#list fr.rules as r>
  - ${r}
</#list>
  - MATCH,Other