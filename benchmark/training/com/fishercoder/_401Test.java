package com.fishercoder;


import _401.Solution1;
import java.util.Arrays;
import junit.framework.TestCase;
import org.junit.Test;


public class _401Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        TestCase.assertEquals(Arrays.asList("0:01", "0:02", "0:04", "0:08", "0:16", "0:32", "1:00", "2:00", "4:00", "8:00"), _401Test.solution1.readBinaryWatch(1));
    }
}

