package com.fishercoder;


import _692.Solution1;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class _692Test {
    private static Solution1 solution1;

    private static String[] words;

    private static List<String> expected;

    private static List<String> actual;

    @Test
    public void test1() {
        _692Test.words = new String[]{ "i", "love", "leetcode", "i", "love", "coding" };
        _692Test.actual = _692Test.solution1.topKFrequent(_692Test.words, 2);
        _692Test.expected = new ArrayList<>(Arrays.asList("i", "love"));
        Assert.assertEquals(_692Test.expected, _692Test.actual);
    }
}

