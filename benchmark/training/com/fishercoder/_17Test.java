package com.fishercoder;


import _17.Solution1;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class _17Test {
    private static Solution1 solution1;

    private static String digits;

    private static List<String> expected;

    private static List<String> actual;

    @Test
    public void test1() {
        _17Test.digits = "2";
        _17Test.actual = _17Test.solution1.letterCombinations(_17Test.digits);
        _17Test.expected = new ArrayList<>(Arrays.asList("a", "b", "c"));
        Assert.assertEquals(_17Test.expected, _17Test.actual);
    }

    @Test
    public void test2() {
        _17Test.digits = "23";
        _17Test.actual = _17Test.solution1.letterCombinations(_17Test.digits);
        _17Test.expected = new ArrayList<>(Arrays.asList("ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf"));
        /**
         * order doesn't matter, so we check like below
         */
        Assert.assertTrue(((_17Test.expected.containsAll(_17Test.actual)) && (_17Test.actual.containsAll(_17Test.expected))));
    }
}

