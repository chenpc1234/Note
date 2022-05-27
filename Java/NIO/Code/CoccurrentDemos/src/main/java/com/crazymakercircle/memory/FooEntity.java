package com.crazymakercircle.memory;

import com.crazymakercircle.util.RandomUtil;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class FooEntity {

    String name = "张三";
    int age = RandomUtil.randInMod(99);
    Date birthdate = new Date();
    BigDecimal price = new BigDecimal(0.0);
}