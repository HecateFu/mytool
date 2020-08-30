package org.fcx.mytool.entity.security;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.fcx.mytool.exception.MyException;
import org.fcx.mytool.util.MyUtil;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.function.Function;

@Getter
@Setter
@ToString
public class Fund {
    // 基金代码 0
    private String code;
    // 基金名称 1
    private String name;
    // 净值日期 3
    private Date priceDate;
    // 单位净值 4
    private BigDecimal unitPrice;
    // 累计净值 5
    private BigDecimal cumulativePrice;
    // 日涨幅 6
    private BigDecimal dRate;
    // 近1周涨幅 7
    private BigDecimal wRate;
    // 近1月涨幅 8
    private BigDecimal m1Rate;
    // 近3月涨幅 9
    private BigDecimal m3Rate;
    // 近6月涨幅 10
    private BigDecimal m6Rate;
    // 近1年涨幅 11
    private BigDecimal y1Rate;
    // 近2年涨幅 12
    private BigDecimal y2Rate;
    // 近3年涨幅 13
    private BigDecimal y3Rate;
    // 今年来涨幅 14
    private BigDecimal yRate;
    // 成以来涨幅 15
    private BigDecimal stRate;
    // 成立日期 16
    private Date setDate;
    // 自定义周期涨幅 18
    private BigDecimal pRate;

    public Fund(String raw) {
        Function<String,BigDecimal> ifBlank = s -> StringUtils.isEmpty(s)?new BigDecimal("0"):new BigDecimal(s);
        String[] properties = raw.split(",",-1);
        if(properties.length < 25) {
            throw new MyException("基金字符串格式不正确，创建 Fund 对象异常："+raw);
        }
        code = properties[0];
        name = properties[1];
        priceDate = MyUtil.strToDate(properties[3]);
        unitPrice = ifBlank.apply(properties[4]);
        cumulativePrice = ifBlank.apply(properties[5]);
        dRate = ifBlank.apply(properties[6]);
        wRate = ifBlank.apply(properties[7]);
        m1Rate = ifBlank.apply(properties[8]);
        m3Rate = ifBlank.apply(properties[9]);
        m6Rate = ifBlank.apply(properties[10]);
        y1Rate = ifBlank.apply(properties[11]);
        y2Rate = ifBlank.apply(properties[12]);
        y3Rate = ifBlank.apply(properties[13]);
        yRate = ifBlank.apply(properties[14]);
        stRate = ifBlank.apply(properties[15]);
        setDate = MyUtil.strToDate(properties[16]);
        pRate = ifBlank.apply(properties[18]);
    }

    public Fund() {
    }
}
