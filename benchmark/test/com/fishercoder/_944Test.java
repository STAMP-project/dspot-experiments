package com.fishercoder;


import _944.Solution1;
import junit.framework.Assert;
import org.junit.Test;


public class _944Test {
    private static Solution1 solution1;

    private static String[] A;

    @Test
    public void test1() {
        _944Test.A = new String[]{ "cba", "daf", "ghi" };
        Assert.assertEquals(1, _944Test.solution1.minDeletionSize(_944Test.A));
    }

    @Test
    public void test2() {
        _944Test.A = new String[]{ "a", "b" };
        Assert.assertEquals(0, _944Test.solution1.minDeletionSize(_944Test.A));
    }

    @Test
    public void test3() {
        _944Test.A = new String[]{ "zyx", "wvu", "tsr" };
        Assert.assertEquals(3, _944Test.solution1.minDeletionSize(_944Test.A));
    }
}

