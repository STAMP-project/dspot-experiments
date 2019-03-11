package com.fishercoder;


import _819.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _819Test {
    private static Solution1 solution1;

    private static String[] banned;

    @Test
    public void test1() {
        _819Test.banned = new String[]{ "hit" };
        Assert.assertEquals("ball", _819Test.solution1.mostCommonWord("Bob hit a ball, the hit BALL flew far after it was hit.", _819Test.banned));
    }
}

