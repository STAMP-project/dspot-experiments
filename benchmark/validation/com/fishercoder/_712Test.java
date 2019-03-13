package com.fishercoder;


import _712.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _712Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(231, _712Test.solution1.minimumDeleteSum("sea", "eat"));
    }

    @Test
    public void test2() {
        Assert.assertEquals(403, _712Test.solution1.minimumDeleteSum("delete", "leet"));
    }
}

