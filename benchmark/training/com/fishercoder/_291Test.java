package com.fishercoder;


import _291.Solution1;
import junit.framework.TestCase;
import org.junit.Test;


/**
 * Created by stevesun on 6/6/17.
 */
public class _291Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        TestCase.assertEquals(true, _291Test.solution1.wordPatternMatch("abab", "redblueredblue"));
    }
}

