package com.fishercoder;


import _804.Solution1;
import junit.framework.TestCase;
import org.junit.Test;


public class _804Test {
    private static Solution1 solution1;

    private static String[] words;

    @Test
    public void test1() {
        _804Test.words = new String[]{ "gin", "zen", "gig", "msg" };
        TestCase.assertEquals(2, _804Test.solution1.uniqueMorseRepresentations(_804Test.words));
    }
}

