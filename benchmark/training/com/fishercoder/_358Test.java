package com.fishercoder;


import _358.Solution1;
import org.junit.Test;


/**
 * Created by stevesun on 6/8/17.
 */
public class _358Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        System.out.println(_358Test.solution1.rearrangeString("aabbcc", 3));
    }

    @Test
    public void test2() {
        System.out.println(_358Test.solution1.rearrangeString("aaabc", 3));
    }

    @Test
    public void test3() {
        System.out.println(_358Test.solution1.rearrangeString("aaadbbcc", 2));
    }
}

