package com.fishercoder;


import _800.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _800Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals("#11ee66", _800Test.solution1.similarRGB("#09f166"));
    }
}

