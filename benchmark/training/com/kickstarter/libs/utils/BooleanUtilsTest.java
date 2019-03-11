package com.kickstarter.libs.utils;


import junit.framework.TestCase;


public class BooleanUtilsTest extends TestCase {
    public void testIsTrue() {
        TestCase.assertTrue(BooleanUtils.isTrue(true));
        TestCase.assertFalse(BooleanUtils.isTrue(false));
        TestCase.assertFalse(BooleanUtils.isTrue(null));
    }

    public void testIsIntTrue() {
        TestCase.assertTrue(BooleanUtils.isIntTrue(1));
        TestCase.assertFalse(BooleanUtils.isIntTrue(0));
        TestCase.assertFalse(BooleanUtils.isIntTrue(0));
    }

    public void testIsFalse() {
        TestCase.assertFalse(BooleanUtils.isFalse(true));
        TestCase.assertTrue(BooleanUtils.isFalse(false));
        TestCase.assertTrue(BooleanUtils.isFalse(null));
    }
}

