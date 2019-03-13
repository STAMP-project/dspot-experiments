package com.querydsl.collections;


import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import java.math.BigDecimal;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class BigDecimalTest {
    @Test
    public void arithmetic() {
        NumberPath<BigDecimal> num = Expressions.numberPath(BigDecimal.class, "num");
        CollQuery<?> query = CollQueryFactory.from(num, Arrays.asList(BigDecimal.ONE, BigDecimal.valueOf(2)));
        Assert.assertEquals(Arrays.asList(BigDecimal.valueOf(11), BigDecimal.valueOf(12)), query.select(num.add(BigDecimal.TEN)).fetch());
        Assert.assertEquals(Arrays.asList(BigDecimal.valueOf((-9)), BigDecimal.valueOf((-8))), query.select(num.subtract(BigDecimal.TEN)).fetch());
        Assert.assertEquals(Arrays.asList(BigDecimal.valueOf(10), BigDecimal.valueOf(20)), query.select(num.multiply(BigDecimal.TEN)).fetch());
        Assert.assertEquals(Arrays.asList(new BigDecimal("0.1"), new BigDecimal("0.2")), query.select(num.divide(BigDecimal.TEN)).fetch());
    }
}

