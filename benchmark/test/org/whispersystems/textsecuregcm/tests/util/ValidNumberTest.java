package org.whispersystems.textsecuregcm.tests.util;


import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.whispersystems.textsecuregcm.util.Util;


public class ValidNumberTest {
    @Test
    public void testValidE164() {
        TestCase.assertTrue(Util.isValidNumber("+14151231234"));
        TestCase.assertTrue(Util.isValidNumber("+71234567890"));
        TestCase.assertTrue(Util.isValidNumber("+447535742222"));
        TestCase.assertTrue(Util.isValidNumber("+4915174108888"));
    }

    @Test
    public void testInvalidE164() {
        Assert.assertFalse(Util.isValidNumber("+141512312341"));
        Assert.assertFalse(Util.isValidNumber("+712345678901"));
        Assert.assertFalse(Util.isValidNumber("+4475357422221"));
        Assert.assertFalse(Util.isValidNumber("+491517410888811111"));
    }

    @Test
    public void testNotE164() {
        Assert.assertFalse(Util.isValidNumber("+1 415 123 1234"));
        Assert.assertFalse(Util.isValidNumber("+1 (415) 123-1234"));
        Assert.assertFalse(Util.isValidNumber("+1 415)123-1234"));
        Assert.assertFalse(Util.isValidNumber("71234567890"));
        Assert.assertFalse(Util.isValidNumber("001447535742222"));
        Assert.assertFalse(Util.isValidNumber(" +14151231234"));
        Assert.assertFalse(Util.isValidNumber("+1415123123a"));
    }

    @Test
    public void testShortRegions() {
        TestCase.assertTrue(Util.isValidNumber("+298123456"));
        TestCase.assertTrue(Util.isValidNumber("+299123456"));
        TestCase.assertTrue(Util.isValidNumber("+376123456"));
        TestCase.assertTrue(Util.isValidNumber("+68512345"));
        TestCase.assertTrue(Util.isValidNumber("+689123456"));
    }
}

