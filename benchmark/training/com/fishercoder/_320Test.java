package com.fishercoder;


import _320.Solution1;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class _320Test {
    private static Solution1 solution1;

    private static List<String> expected;

    private static List<String> actual;

    private static String word;

    @Test
    public void test1() {
        _320Test.word = "word";
        _320Test.expected = Arrays.asList("word", "1ord", "w1rd", "wo1d", "wor1", "2rd", "w2d", "wo2", "1o1d", "1or1", "w1r1", "1o2", "2r1", "3d", "w3", "4");
        _320Test.actual = _320Test.solution1.generateAbbreviations(_320Test.word);
        Assert.assertTrue(((_320Test.expected.containsAll(_320Test.actual)) && (_320Test.actual.containsAll(_320Test.expected))));
    }
}

