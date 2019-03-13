package com.fishercoder;


import _331.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _331Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(true, _331Test.solution1.isValidSerialization("9,3,4,#,#,1,#,#,2,#,6,#,#"));
    }

    @Test
    public void test2() {
        Assert.assertEquals(false, _331Test.solution1.isValidSerialization("1,#"));
    }

    @Test
    public void test3() {
        Assert.assertEquals(false, _331Test.solution1.isValidSerialization("9,#,#,1"));
    }

    @Test
    public void test4() {
        Assert.assertEquals(false, _331Test.solution1.isValidSerialization("1"));
    }

    @Test
    public void test5() {
        Assert.assertEquals(false, _331Test.solution1.isValidSerialization("#,7,6,9,#,#,#"));
    }
}

