package com.fishercoder;


import _71.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _71Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals("/home", _71Test.solution1.simplifyPath("/home/"));
    }

    @Test
    public void test2() {
        Assert.assertEquals("/c", _71Test.solution1.simplifyPath("/a/./b/../../c/"));
    }
}

