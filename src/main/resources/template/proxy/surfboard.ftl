#!MANAGED-CONFIG ${updateUrl} interval=86400 strict=false
[General]
skip-proxy = 127.0.0.1, 192.168.0.0/16, 10.0.0.0/8, 172.16.0.0/12, 100.64.0.0/10, localhost, *.local
dns-server = system, 114.114.114.114, 8.8.8.8:53
test-timeout = 5
internet-test-url = http://bing.com
proxy-test-url = http://www.gstatic.com/generate_204

[Proxy]
<#list proxies as proxy>
    <#if !proxy.name?contains("=")>
        <#if proxy.type == "ss">
${proxy?counter} ${proxy.name} = ${proxy.type}, ${proxy.server}, ${proxy.port?c}, ${proxy.cipher}, ${proxy.password}, https://raw.githubusercontent.com/ConnersHua/SSEncrypt/master/SSEncrypt.module, <#if proxy.plugin??> obfs = ${proxy.pluginOpts.mode}, obfs-host = ${proxy.pluginOpts.host},</#if> tfo = false, udp-relay = true
        </#if>
        <#if proxy.type == "vmess">
${proxy?counter} ${proxy.name} = ${proxy.type}, ${proxy.server}, ${proxy.port?c}, username = ${proxy.uuid}, tls = ${proxy.tls?c}, <#if proxy.network == "ws">ws=true, ws-path=${proxy.wsPath}, ws-headers=<#list proxy.wsHeaders as k,v>${k}:${v}<#sep>|</#list>,</#if> tfo = false, udp-relay=true
        </#if>
        <#if proxy.type == "ssr" && proxy.protocol = "origin" && proxy.obfs = "plain">
${proxy?counter} ${proxy.name} = custom, ${proxy.server}, ${proxy.port?c}, ${proxy.cipher}, ${proxy.password}, https://raw.githubusercontent.com/ConnersHua/SSEncrypt/master/SSEncrypt.module, <#if proxy.plugin??> obfs = ${proxy.pluginOpts.mode}, obfs-host = ${proxy.pluginOpts.host},</#if> tfo = false, udp-relay = true
        </#if>
        <#if proxy.type == "socks5">
${proxy?counter} ${proxy.name} = ${proxy.type}, ${proxy.server}, ${proxy.port?c}
        </#if>
    </#if>
</#list>

[Proxy Group]
Proxy = select, fallback-auto, <#list proxies as proxy><#if !proxy.name?contains("=")><#if proxy.type != "ssr" ||(proxy.type == "ssr" && proxy.protocol = "origin" && proxy.obfs = "plain")>${proxy?counter} ${proxy.name},</#if></#if></#list> DIRECT
fallback-auto = fallback,<#list proxies as proxy><#if !proxy.name?contains("=")><#if proxy.type != "ssr" ||(proxy.type == "ssr" && proxy.protocol = "origin" && proxy.obfs = "plain")>${proxy?counter} ${proxy.name},</#if></#if></#list> url = http://www.gstatic.com/generate_204, interval = 300
Direct = select, DIRECT, Proxy, REJECT
Ad = select, REJECT, DIRECT, Proxy
Other = select, Proxy, DIRECT, REJECT

[Rule]
<#import "proxy_rules.ftl" as pr>
<#import "direct_rules.ftl" as dr>
<#import "final_rules.ftl" as fr>
<#list pr.rules as r>
${r}
</#list>
<#list dr.rules as r>
${r}
</#list>
<#list fr.rules as r>
${r}
</#list>
FINAL,Other,dns-failed

[Host]
