package com.sleekbyte.tailor.utils;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Tests for {@link CharFormatUtil}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CharFormatUtilTest {
    @Test
    public void testUpperCamelCaseInvalidWord() {
        Assert.assertFalse(CharFormatUtil.isUpperCamelCase("helloWorld"));
        Assert.assertFalse(CharFormatUtil.isUpperCamelCase(""));
        Assert.assertFalse(CharFormatUtil.isUpperCamelCase("Hello_World"));
        Assert.assertFalse(CharFormatUtil.isUpperCamelCase("1ello_world"));
        Assert.assertFalse(CharFormatUtil.isUpperCamelCase("!ello_world"));
    }

    @Test
    public void testUpperCamelCaseValidWord() {
        Assert.assertTrue(CharFormatUtil.isUpperCamelCase("HelloWorld"));
    }

    @Test
    public void testLowerCamelCaseInvalidWord() {
        Assert.assertFalse(CharFormatUtil.isLowerCamelCase("HelloWorld"));
        Assert.assertFalse(CharFormatUtil.isLowerCamelCase(""));
        Assert.assertFalse(CharFormatUtil.isLowerCamelCase("hello_World"));
        Assert.assertFalse(CharFormatUtil.isLowerCamelCase("1ello_world"));
        Assert.assertFalse(CharFormatUtil.isLowerCamelCase("$elloWorld"));
    }

    @Test
    public void testLowerCamelCaseValidWord() {
        Assert.assertTrue(CharFormatUtil.isLowerCamelCase("helloWorld"));
    }

    @Test
    public void testKPrefixedInvalidVariableNamesStartingWithK() {
        Assert.assertTrue(CharFormatUtil.isKPrefixed("KBadConstantName"));
        Assert.assertTrue(CharFormatUtil.isKPrefixed("kBadConstantName"));
    }

    @Test
    public void testKPrefixedValidVariableNamesStartingWithK() {
        Assert.assertFalse(CharFormatUtil.isKPrefixed("koalasEatKale"));
        Assert.assertFalse(CharFormatUtil.isKPrefixed("KoalasEatKale"));
        Assert.assertFalse(CharFormatUtil.isKPrefixed("k"));
    }

    @Test
    public void testKPrefixedVariableNamesNotInCamelCase() {
        Assert.assertFalse(CharFormatUtil.isKPrefixed("k_valid_because_not_camel_case"));
        Assert.assertFalse(CharFormatUtil.isKPrefixed("K_valid_because_not_camel_case"));
    }

    @Test
    public void testKPrefixedVariableNamesNotStartingWithK() {
        Assert.assertFalse(CharFormatUtil.isKPrefixed("validConstantName"));
        Assert.assertFalse(CharFormatUtil.isKPrefixed("AlsoValidConstantName"));
    }

    @Test
    public void testStartsWithAcronym() {
        // Names that start with acronyms
        Assert.assertTrue(CharFormatUtil.startsWithAcronym("AT"));
        Assert.assertTrue(CharFormatUtil.startsWithAcronym("URL"));
        Assert.assertTrue(CharFormatUtil.startsWithAcronym("XLnotification"));
        Assert.assertTrue(CharFormatUtil.startsWithAcronym("SHIELDprogrammeMARVEL"));
        // Single character names
        Assert.assertFalse(CharFormatUtil.startsWithAcronym("A"));
        Assert.assertFalse(CharFormatUtil.startsWithAcronym("2"));
        Assert.assertFalse(CharFormatUtil.startsWithAcronym("$"));
        // Names that contain special characters
        Assert.assertFalse(CharFormatUtil.startsWithAcronym("$HIELDprogrammeMARVEL"));
        Assert.assertFalse(CharFormatUtil.startsWithAcronym("SH!ELDprogrammeMARVEL"));
        // Names that do not start with acronyms
        Assert.assertFalse(CharFormatUtil.startsWithAcronym("uRL"));
        Assert.assertFalse(CharFormatUtil.startsWithAcronym("xURLS"));
        Assert.assertFalse(CharFormatUtil.startsWithAcronym("shieldPROGRAMMEmarvel"));
    }

    @Test
    public void testBacktickEscapedIdentifier() {
        // Backtick(s) are not part of the identifier
        Assert.assertTrue(CharFormatUtil.unescapeIdentifier("``").isEmpty());
        Assert.assertTrue(CharFormatUtil.unescapeIdentifier("").isEmpty());
        Assert.assertEquals("self", CharFormatUtil.unescapeIdentifier("`self`"));
        Assert.assertEquals("s", CharFormatUtil.unescapeIdentifier("`s`"));
        Assert.assertEquals("self", CharFormatUtil.unescapeIdentifier("`self`"));
        Assert.assertEquals("`self", CharFormatUtil.unescapeIdentifier("`self"));
        Assert.assertEquals("self`", CharFormatUtil.unescapeIdentifier("self`"));
        Assert.assertEquals("`self", CharFormatUtil.unescapeIdentifier("``self`"));
    }
}

