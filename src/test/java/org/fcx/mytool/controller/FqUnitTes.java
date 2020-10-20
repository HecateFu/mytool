package org.fcx.mytool.controller;

import org.fcx.mytool.entity.proxy.clash.Proxy;
import org.junit.Test;

import java.time.LocalTime;

public class FqUnitTes {
    @Test
    public void trojanTest() {
        String l0 = "trojan://95d2d17d-e234-4fb6-8fab-0ad50e3561ba@bigsur.tk:443#%E4%B8%B4%E6%97%B65+%E9%9F%A9%E5%9B%BD8+%E7%94%B5%E6%8A%A5%E9%A2%91%E9%81%93%EF%BC%9A%EF%BC%9Ahttps%3A%2F%2Ft.me%2Fcnhumanright99";
        Proxy proxy0 = Proxy.factory(l0);
        System.out.println(proxy0);
    }
    @Test
    public void localTimeTest() {
        System.out.println(LocalTime.now().getMinute());
        System.out.println(LocalTime.now().getMinute()>5&&LocalTime.now().getMinute()<55);
    }
}
