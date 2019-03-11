package com.fishercoder;


import _49.Solution1;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Test;


public class _49Test {
    private static Solution1 solution1;

    private static String[] words;

    private static List<List<String>> expected;

    private static List<List<String>> actual;

    @Test
    public void test1() {
        _49Test.words = new String[]{ "eat", "tea", "tan", "ate", "nat", "bat" };
        _49Test.expected = new ArrayList<>();
        _49Test.expected.add(Arrays.asList("ate", "eat", "tea"));
        _49Test.expected.add(Arrays.asList("nat", "tan"));
        _49Test.expected.add(Arrays.asList("bat"));
        _49Test.actual = _49Test.solution1.groupAnagrams(_49Test.words);
        TestCase.assertEquals(_49Test.expected.size(), _49Test.actual.size());
        TestCase.assertEquals(_49Test.expected.containsAll(_49Test.actual), _49Test.actual.containsAll(_49Test.expected));
    }
}

