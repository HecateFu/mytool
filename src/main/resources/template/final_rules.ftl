<#assign rules = [
<#-- IPV4的本地和局域网网段 https://github.com/ACL4SSR/ACL4SSR/blob/master/Clash/LocalAreaNetwork.list -->
"DOMAIN-SUFFIX,local,DIRECT",
"IP-CIDR,127.0.0.0/8,DIRECT",
"IP-CIDR,172.16.0.0/12,DIRECT",
"IP-CIDR,192.168.0.0/16,DIRECT",
"IP-CIDR,10.0.0.0/8,DIRECT",
"IP-CIDR,100.64.0.0/10,DIRECT",
<#-- 我的虚拟机 -->
"IP-CIDR,34.82.54.2/32,DIRECT",
"GEOIP,CN,Direct"]>