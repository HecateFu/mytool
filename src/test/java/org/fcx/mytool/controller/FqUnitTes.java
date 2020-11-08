package org.fcx.mytool.controller;

import org.fcx.mytool.entity.proxy.clash.Proxy;
import org.junit.Test;

import java.time.LocalTime;

public class FqUnitTes {
    @Test
    public void trojanTest() {
        String l0 = "ss://YWVzLTI1Ni1nY206eHBRd3lWNFc1RmRBNk5NQU5KSng3M1VTQDIuNTguMjQxLjI3OjM4MDMz/#shadowsocks%2F2.58.241.27%3A38033-iwcqe";
        Proxy proxy0 = Proxy.factory(l0);
        System.out.println(proxy0);
    }
    @Test
    public void localTimeTest() {
        System.out.println(LocalTime.now().getMinute());
        System.out.println(LocalTime.now().getMinute()>5&&LocalTime.now().getMinute()<55);
    }
}
