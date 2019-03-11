package com.fishercoder;


import _247.Solution1;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class _247Test {
    private static Solution1 solution1;

    private static List<String> expected;

    @Test
    public void test1() {
        _247Test.expected = List.of(List, "11", "69", "88", "96");
        Assert.assertEquals(_247Test.expected, _247Test.solution1.findStrobogrammatic(2));
    }
}

