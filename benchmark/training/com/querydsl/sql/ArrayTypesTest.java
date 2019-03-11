package com.querydsl.sql;


import org.junit.Assert;
import org.junit.Test;


public class ArrayTypesTest {
    private Configuration configuration;

    @Test
    public void test() {
        Assert.assertEquals(Integer[].class, getJavaType("_integer"));
        Assert.assertEquals(Integer[].class, getJavaType("integer[]"));
        Assert.assertEquals(Integer[].class, getJavaType("INTEGER ARRAY"));
    }
}

