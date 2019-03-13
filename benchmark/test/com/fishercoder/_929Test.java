package com.fishercoder;


import _929.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _929Test {
    private static Solution1 solution1;

    private static String[] emails;

    @Test
    public void test1() {
        _929Test.emails = new String[]{ "test.email+alex@leetcode.com", "test.e.mail+bob.cathy@leetcode.com", "testemail+david@lee.tcode.com" };
        Assert.assertEquals(2, _929Test.solution1.numUniqueEmails(_929Test.emails));
    }
}

