package com.fishercoder;


import _402.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _402Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals("1219", _402Test.solution1.removeKdigits("1432219", 3));
    }
}

