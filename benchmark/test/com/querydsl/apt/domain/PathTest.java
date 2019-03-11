package com.querydsl.apt.domain;


import QPath.path;
import org.junit.Assert;
import org.junit.Test;


public class PathTest {
    @Test
    public void test() {
        Assert.assertEquals(Path.class, path.getType());
    }
}

