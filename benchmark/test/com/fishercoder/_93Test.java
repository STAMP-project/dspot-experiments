package com.fishercoder;


import _93.Solution1;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;


public class _93Test {
    private static Solution1 solution1;

    private static List<String> expected;

    private static String s;

    @Test
    public void test1() {
        _93Test.s = "25525511135";
        _93Test.expected.add("255.255.11.135");
        _93Test.expected.add("255.255.111.35");
        Assert.assertEquals(_93Test.expected, _93Test.solution1.restoreIpAddresses(_93Test.s));
    }
}

