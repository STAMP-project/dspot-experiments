package com.kickstarter.libs.utils;


import junit.framework.TestCase;


public class IntegerUtilsTest extends TestCase {
    public void testIsNonZero() {
        TestCase.assertTrue(IntegerUtils.isNonZero(1));
        TestCase.assertTrue(IntegerUtils.isNonZero((-1)));
        TestCase.assertFalse(IntegerUtils.isNonZero(0));
        TestCase.assertFalse(IntegerUtils.isNonZero(null));
    }

    public void testIsZero() {
        TestCase.assertFalse(IntegerUtils.isZero(1));
        TestCase.assertFalse(IntegerUtils.isZero((-1)));
        TestCase.assertTrue(IntegerUtils.isZero(0));
        TestCase.assertFalse(IntegerUtils.isZero(null));
    }

    public void testIntValueOrZero() {
        TestCase.assertEquals(5, IntegerUtils.intValueOrZero(5));
        TestCase.assertEquals(0, IntegerUtils.intValueOrZero(null));
    }
}

