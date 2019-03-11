package com.fishercoder;


import _187.Solution1;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class _187Test {
    private static Solution1 solution1;

    private static String s;

    private static List<String> expected;

    private static List<String> actual;

    @Test
    public void test1() {
        _187Test.s = "AAAAAAAAAAA";
        System.out.println(_187Test.s.length());
        _187Test.actual = _187Test.solution1.findRepeatedDnaSequences(_187Test.s);
        _187Test.expected = new ArrayList<>(Arrays.asList("AAAAAAAAAA"));
        System.out.println(_187Test.expected.get(0).length());
        Assert.assertEquals(_187Test.expected, _187Test.actual);
    }
}

