package com.fishercoder;


import _131.Solution1;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class _131Test {
    private static Solution1 solution1;

    private static List<List<String>> expected;

    @Test
    public void test1() {
        _131Test.expected = new ArrayList();
        _131Test.expected.add(Arrays.asList("a", "a", "b"));
        _131Test.expected.add(Arrays.asList("aa", "b"));
        Assert.assertEquals(_131Test.expected, _131Test.solution1.partition("aab"));
    }
}

