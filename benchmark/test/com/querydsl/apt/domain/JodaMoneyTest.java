package com.querydsl.apt.domain;


import QMoney.money;
import com.querydsl.core.types.dsl.NumberExpression;
import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;


public class JodaMoneyTest {
    @Test
    public void test() {
        NumberExpression<BigDecimal> sum = money.sum();
        Assert.assertNotNull(sum);
    }
}

