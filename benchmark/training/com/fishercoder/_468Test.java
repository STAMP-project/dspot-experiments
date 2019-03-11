package com.fishercoder;


import com.fishercoder.solutions._468;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by stevesun on 6/10/17.
 */
public class _468Test {
    private static _468 test;

    @Test
    public void test1() {
        Assert.assertEquals("IPv4", _468Test.test.validIPAddress("172.16.254.1"));
    }

    @Test
    public void test2() {
        Assert.assertEquals("IPv6", _468Test.test.validIPAddress("2001:0db8:85a3:0:0:8A2E:0370:7334"));
    }

    @Test
    public void test3() {
        Assert.assertEquals("Neither", _468Test.test.validIPAddress("2001:0db8:85a3::8A2E:0370:7334"));
    }

    @Test
    public void test4() {
        Assert.assertEquals("Neither", _468Test.test.validIPAddress("02001:0db8:85a3:0000:0000:8a2e:0370:7334"));
    }

    @Test
    public void test5() {
        Assert.assertEquals("Neither", _468Test.test.validIPAddress("256.256.256.256"));
    }

    @Test
    public void test6() {
        Assert.assertEquals("Neither", _468Test.test.validIPAddress("2001:0db8:85a3:0:0:8A2E:0370:7334:"));
    }

    @Test
    public void test7() {
        Assert.assertEquals("Neither", _468Test.test.validIPAddress("01.01.01.01"));
    }

    @Test
    public void test8() {
        Assert.assertEquals("Neither", _468Test.test.validIPAddress("00.0.0.0"));
    }

    @Test
    public void test9() {
        Assert.assertEquals("Neither", _468Test.test.validIPAddress("000.0.0.0"));
    }

    @Test
    public void test10() {
        Assert.assertEquals("Neither", _468Test.test.validIPAddress("0000.0.0.0"));
    }

    @Test
    public void test11() {
        Assert.assertEquals("IPv4", _468Test.test.validIPAddress("0.0.0.0"));
    }

    @Test
    public void test12() {
        Assert.assertEquals("Neither", _468Test.test.validIPAddress("1081:db8:85a3:01:-0:8A2E:0370:7334"));
    }

    @Test
    public void test13() {
        Assert.assertEquals("Neither", _468Test.test.validIPAddress("1081:db8:85a3:01:z:8A2E:0370:7334"));
    }
}

