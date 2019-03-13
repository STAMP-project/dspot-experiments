package com.querydsl.collections;


import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import java.math.BigDecimal;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class NumberTest {
    @Test
    public void sum() throws Exception {
        NumberPath<BigDecimal> num = Expressions.numberPath(BigDecimal.class, "num");
        CollQuery<?> query = CollQueryFactory.from(num, Arrays.asList(new BigDecimal("1.6"), new BigDecimal("1.3")));
        Assert.assertEquals(new BigDecimal("2.9"), query.select(num.sum()).fetchOne());
    }
}

