package com.fishercoder;


import _942.Solution1;
import com.fishercoder.common.utils.CommonUtils;
import org.junit.Test;


public class _942Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        CommonUtils.printArray(_942Test.solution1.diStringMatch("IDID"));
    }

    @Test
    public void test2() {
        CommonUtils.printArray(_942Test.solution1.diStringMatch("III"));
    }

    @Test
    public void test3() {
        CommonUtils.printArray(_942Test.solution1.diStringMatch("DDI"));
    }
}

