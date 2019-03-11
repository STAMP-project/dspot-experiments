package com.fishercoder;


import _763.Solution1;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class _763Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(Arrays.asList(9, 7, 8), _763Test.solution1.partitionLabels("ababcbacadefegdehijhklij"));
    }

    @Test
    public void test2() {
        Assert.assertEquals(Arrays.asList(9, 7, 8), _763Test.solution1.partitionLabels("ababcbacadefegdehijhklij"));
    }
}

