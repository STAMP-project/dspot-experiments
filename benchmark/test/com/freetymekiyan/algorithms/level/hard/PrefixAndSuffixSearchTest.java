package com.freetymekiyan.algorithms.level.hard;


import PrefixAndSuffixSearch.WordFilter;
import org.junit.Assert;
import org.junit.Test;


public class PrefixAndSuffixSearchTest {
    @Test
    public void testWordFilterExample() {
        String[] words = new String[]{ "apple" };
        PrefixAndSuffixSearch.WordFilter w = new PrefixAndSuffixSearch.WordFilter(words);
        Assert.assertEquals(0, w.f("a", "e"));
        Assert.assertEquals((-1), w.f("b", ""));
        Assert.assertEquals(0, w.f("", ""));
        Assert.assertEquals(0, w.f("", "le"));
        words = new String[]{ "abbbababbb", "baaabbabbb", "abababbaaa", "abbbbbbbba", "bbbaabbbaa", "ababbaabaa", "baaaaabbbb", "babbabbabb", "ababaababb", "bbabbababa" };
        String[][] inputs = new String[][]{ new String[]{ "", "abaa" }, new String[]{ "babbab", "" }, new String[]{ "ab", "baaa" }, new String[]{ "baaabba", "b" }, new String[]{ "abab", "abbaabaa" }, new String[]{ "", "aa" }, new String[]{ "", "bba" }, new String[]{ "", "baaaaabbbb" }, new String[]{ "ba", "aabbbb" }, new String[]{ "baaa", "aabbabbb" } };
        int[] expected = new int[]{ 5, 7, 2, 1, 5, 5, 3, 6, 6, 1 };
        w = new PrefixAndSuffixSearch.WordFilter(words);
        for (int i = 0; i < (inputs.length); i++) {
            Assert.assertEquals(expected[i], w.f(inputs[i][0], inputs[i][1]));
        }
    }
}

