package com.fishercoder;


import _51.Solution1;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;


public class _51Test {
    private static Solution1 solution1;

    private static List<List<String>> expected;

    private static List<List<String>> actual;

    private static int n;

    @Test
    public void test1() {
        _51Test.n = 4;
        _51Test.expected = new ArrayList<>();
        _51Test.expected.add(Arrays.asList("..Q.", "Q...", "...Q", ".Q.."));
        _51Test.expected.add(Arrays.asList(".Q..", "...Q", "Q...", "..Q."));
        _51Test.actual = _51Test.solution1.solveNQueens(_51Test.n);
        Assert.assertEquals(_51Test.expected, _51Test.actual);
    }
}

