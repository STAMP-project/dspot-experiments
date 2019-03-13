package com.fishercoder;


import _139.Solution1;
import _139.Solution2;
import _139.Solution3;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;


public class _139Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static Solution3 solution3;

    private static String s;

    private static List<String> wordDict;

    @Test
    public void test1() {
        _139Test.s = "leetcode";
        _139Test.wordDict = new ArrayList<>(Arrays.asList("leet", "code"));
        Assert.assertEquals(true, _139Test.solution1.wordBreak(_139Test.s, _139Test.wordDict));
        Assert.assertEquals(true, _139Test.solution2.wordBreak(_139Test.s, _139Test.wordDict));
        Assert.assertEquals(true, _139Test.solution3.wordBreak(_139Test.s, _139Test.wordDict));
    }
}

