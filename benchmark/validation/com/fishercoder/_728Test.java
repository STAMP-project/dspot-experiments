package com.fishercoder;


import _728.Solution1;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class _728Test {
    private static Solution1 solution1;

    private static List<Integer> expected;

    @Test
    public void test1() {
        _728Test.expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 12, 15, 22);
        Assert.assertEquals(_728Test.expected, _728Test.solution1.selfDividingNumbers(1, 22));
    }
}

