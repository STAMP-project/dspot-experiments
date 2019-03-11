package com.fishercoder;


import _1002.Solution1;
import com.fishercoder.common.utils.CommonUtils;
import org.junit.Test;


public class _1002Test {
    private static Solution1 solution1;

    private static String[] A;

    @Test
    public void test1() {
        _1002Test.A = new String[]{ "bella", "label", "roller" };
        CommonUtils.print(_1002Test.solution1.commonChars(_1002Test.A));
    }

    @Test
    public void test2() {
        _1002Test.A = new String[]{ "cool", "lock", "cook" };
        CommonUtils.print(_1002Test.solution1.commonChars(_1002Test.A));
    }
}

