package com.kickstarter.libs.utils;


import com.kickstarter.KSRobolectricTestCase;
import junit.framework.TestCase;
import org.junit.Test;


public class StringUtilsTest extends KSRobolectricTestCase {
    @Test
    public void testIsEmail() {
        TestCase.assertTrue(StringUtils.isEmail("hello@kickstarter.com"));
        TestCase.assertFalse(StringUtils.isEmail("hello@kickstarter"));
    }

    @Test
    public void testIsEmpty() {
        TestCase.assertTrue(StringUtils.isEmpty(""));
        TestCase.assertTrue(StringUtils.isEmpty(" "));
        TestCase.assertTrue(StringUtils.isEmpty("     "));
        TestCase.assertTrue(StringUtils.isEmpty(null));
        TestCase.assertFalse(StringUtils.isEmpty("a"));
        TestCase.assertFalse(StringUtils.isEmpty(" a "));
    }

    @Test
    public void testIsPresent() {
        TestCase.assertFalse(StringUtils.isPresent(""));
        TestCase.assertFalse(StringUtils.isPresent(" "));
        TestCase.assertFalse(StringUtils.isPresent("     "));
        TestCase.assertFalse(StringUtils.isPresent(null));
        TestCase.assertTrue(StringUtils.isPresent("a"));
        TestCase.assertTrue(StringUtils.isPresent(" a "));
    }

    @Test
    public void testSentenceCase() {
        TestCase.assertEquals("", StringUtils.sentenceCase(""));
        TestCase.assertEquals("A", StringUtils.sentenceCase("a"));
        TestCase.assertEquals("Apple", StringUtils.sentenceCase("APPLE"));
        TestCase.assertEquals("Apple", StringUtils.sentenceCase("APple"));
        TestCase.assertEquals("Apple", StringUtils.sentenceCase("apple"));
        TestCase.assertEquals("Snapple apple", StringUtils.sentenceCase("Snapple Apple"));
        TestCase.assertEquals("Snapple apple snapple", StringUtils.sentenceCase("Snapple Apple Snapple"));
    }
}

