package com.fishercoder;


import _811.Solution1;
import com.fishercoder.common.utils.CommonUtils;
import org.junit.Test;


public class _811Test {
    private static Solution1 solution1;

    private static String[] cpdomains;

    @Test
    public void test1() {
        _811Test.cpdomains = new String[]{ "9001 discuss.leetcode.com" };
        CommonUtils.print(_811Test.solution1.subdomainVisits(_811Test.cpdomains));
    }

    @Test
    public void test2() {
        _811Test.cpdomains = new String[]{ "900 google.mail.com", "50 yahoo.com", "1 intel.mail.com", "5 wiki.org" };
        CommonUtils.print(_811Test.solution1.subdomainVisits(_811Test.cpdomains));
    }
}

