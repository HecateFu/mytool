package org.fcx.mytool.dao;

import org.fcx.mytool.dao.mapper.FundMapper;
import org.fcx.mytool.entity.security.Fund;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest("spring.profiles.active=dev")
public class MysqlTest {
    @Autowired
    FundMapper dao;

    @Test
    public void fundDaoTest () {
        String raw = "160623,鹏华证券保险分级,PHZQBXFJ,2020-07-09,1,1.9850,,25.6347,34.4435,37.4552,23.0152,29.7640,64.9971,31.7759,21.2946,119.1410,2014-05-05,1,,1.20%,0.60%,5,0.60%,5,17.0541";
        Fund f = new Fund(raw);
        int i = dao.insert(f);
        System.out.println(i);
    }
}
