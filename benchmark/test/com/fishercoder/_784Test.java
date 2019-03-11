package com.fishercoder;


import _784.Solution1;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class _784Test {
    private static Solution1 solution1;

    private static List<String> expected;

    @Test
    public void test1() {
        _784Test.expected = Arrays.asList("a1b2", "a1B2", "A1b2", "A1B2");
        Assert.assertEquals(_784Test.expected, _784Test.solution1.letterCasePermutation("a1b2"));
    }

    @Test
    public void test2() {
        _784Test.expected = Arrays.asList("3z4", "3Z4");
        Assert.assertEquals(_784Test.expected, _784Test.solution1.letterCasePermutation("3z4"));
    }

    @Test
    public void test3() {
        _784Test.expected = Arrays.asList("12345");
        Assert.assertEquals(_784Test.expected, _784Test.solution1.letterCasePermutation("12345"));
    }
}

