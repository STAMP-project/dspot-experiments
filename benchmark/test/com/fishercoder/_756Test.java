package com.fishercoder;


import _756.Solution1;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class _756Test {
    private static Solution1 solution1;

    private static List<String> allowed;

    @Test
    public void test1() {
        _756Test.allowed = Arrays.asList("XYD", "YZE", "DEA", "FFF");
        Assert.assertEquals(true, _756Test.solution1.pyramidTransition("XYZ", _756Test.allowed));
    }

    @Test
    public void test2() {
        _756Test.allowed = Arrays.asList("XXX", "XXY", "XYX", "XYY", "YXZ");
        Assert.assertEquals(false, _756Test.solution1.pyramidTransition("XXYX", _756Test.allowed));
    }

    @Test
    public void test3() {
        _756Test.allowed = Arrays.asList("BCE", "BCF", "ABA", "CDA", "AEG", "FAG", "GGG");
        Assert.assertEquals(false, _756Test.solution1.pyramidTransition("ABCD", _756Test.allowed));
    }
}

