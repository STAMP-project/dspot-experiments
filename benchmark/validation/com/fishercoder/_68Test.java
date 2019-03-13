package com.fishercoder;


import _68.Solution1;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class _68Test {
    private static Solution1 solution1;

    private static String[] words;

    @Test
    public void test1() {
        _68Test.words = new String[]{ "This", "is", "a", "good", "test!", "\n", "What", "do", "you", "\n", "think?", "\n", "I", "think", "so", "too!" };
        Assert.assertEquals(Arrays.asList("This  is  a good", "test!  \n What do", "you \n think? \n I", "think so too!   "), _68Test.solution1.fullJustify(_68Test.words, 16));
    }

    @Test
    public void test2() {
        _68Test.words = new String[]{ "This", "is", "an", "example", "of", "text", "justification." };
        Assert.assertEquals(Arrays.asList("This    is    an", "example  of text", "justification.  "), _68Test.solution1.fullJustify(_68Test.words, 16));
    }
}

