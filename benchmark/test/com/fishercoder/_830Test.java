package com.fishercoder;


import _830.Solution1;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class _830Test {
    private static Solution1 solution1;

    private static List<List<Integer>> expected;

    @Test
    public void test1() {
        _830Test.expected = new ArrayList<>();
        _830Test.expected.add(Arrays.asList(3, 6));
        Assert.assertEquals(_830Test.expected, _830Test.solution1.largeGroupPositions("abbxxxxzzy"));
    }

    @Test
    public void test2() {
        _830Test.expected = new ArrayList<>();
        Assert.assertEquals(_830Test.expected, _830Test.solution1.largeGroupPositions("abc"));
    }

    @Test
    public void test3() {
        _830Test.expected = new ArrayList<>();
        _830Test.expected.add(Arrays.asList(3, 5));
        _830Test.expected.add(Arrays.asList(6, 9));
        _830Test.expected.add(Arrays.asList(12, 14));
        Assert.assertEquals(_830Test.expected, _830Test.solution1.largeGroupPositions("abcdddeeeeaabbbcd"));
    }
}

