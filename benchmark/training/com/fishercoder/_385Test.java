package com.fishercoder;


import _385.Solution1;
import org.junit.Test;


public class _385Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        _385Test.solution1.deserialize("324");
    }

    @Test
    public void test2() {
        _385Test.solution1.deserialize("[-1]");
    }

    @Test
    public void test3() {
        _385Test.solution1.deserialize("[]");
    }

    @Test
    public void test4() {
        _385Test.solution1.deserialize("[-1,-2]");
    }

    @Test
    public void test5() {
        _385Test.solution1.deserialize("[-1,-2,[-3,-4,[5,[6,[7,8]]]]]");
    }
}

