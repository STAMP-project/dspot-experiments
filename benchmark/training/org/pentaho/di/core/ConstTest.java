/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.core;


import Const.ROUND_HALF_CEILING;
import Const.ReleaseType;
import SystemUtils.IS_OS_LINUX;
import SystemUtils.IS_OS_MAC_OSX;
import SystemUtils.IS_OS_WINDOWS;
import ValueMetaInterface.TRIM_TYPE_BOTH;
import ValueMetaInterface.TRIM_TYPE_LEFT;
import ValueMetaInterface.TRIM_TYPE_NONE;
import ValueMetaInterface.TRIM_TYPE_RIGHT;
import java.math.BigDecimal;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.SystemUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;

import static Const.FILE_SEPARATOR;


/**
 * Test class for the basic functionality of Const.
 */
public class ConstTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    private static String DELIMITER1 = ",";

    private static String DELIMITER2 = "</newpage>";

    private static String ENCLOSURE1 = "\"";

    private static String ENCLOSURE2 = "html";

    /**
     * Test initCap. Regression test for PDI-1338: "javascript initcap() can't deal correctly with special non-ASCII
     * chars".
     */
    @Test
    public void testInitCap() {
        ConstTest.assertEquals("Sven", Const.initCap("Sven"));
        ConstTest.assertEquals("Matt", Const.initCap("MATT"));
        ConstTest.assertEquals("Sven Boden", Const.initCap("sven boden"));
        ConstTest.assertEquals("Sven  Boden ", Const.initCap("sven  boden "));
        ConstTest.assertEquals("Sven Boden Was Here", Const.initCap("sven boden was here"));
        // Here the original code failed as it saw the "o umlaut" as non-ASCII, and would
        // assume it needed to start a new word here.
        ConstTest.assertEquals("K\u00f6nnen", Const.initCap("k\u00f6nnen"));
    }

    /**
     * Test sortString().
     */
    @Test
    public void testSortStrings() {
        String[] arr1 = new String[]{ "Red", "Blue", "Black", "Black", "Green" };
        String[] arr2 = new String[]{ "aaa", "zzz", "yyy", "sss", "ttt", "t" };
        String[] arr3 = new String[]{ "A", "B", "C", "D" };
        String[] results = Const.sortStrings(arr1);
        Assert.assertTrue(isArraySorted(arr1));
        Assert.assertTrue(isArraySorted(results));
        results = Const.sortStrings(arr2);
        Assert.assertTrue(isArraySorted(arr2));
        Assert.assertTrue(isArraySorted(results));
        results = Const.sortStrings(arr3);
        Assert.assertTrue(isArraySorted(arr3));
        Assert.assertTrue(isArraySorted(results));
    }

    @Test
    public void testIsEmpty() {
        Assert.assertTrue(Const.isEmpty(((String) (null))));
        Assert.assertTrue(Const.isEmpty(""));
        Assert.assertFalse(Const.isEmpty("test"));
    }

    @Test
    public void testIsEmptyStringArray() {
        Assert.assertTrue(Const.isEmpty(((String[]) (null))));
        Assert.assertTrue(Const.isEmpty(new String[]{  }));
        Assert.assertFalse(Const.isEmpty(new String[]{ "test" }));
    }

    @Test
    public void testIsEmptyObjectArray() {
        Assert.assertTrue(Const.isEmpty(((Object[]) (null))));
        Assert.assertTrue(Const.isEmpty(new Object[]{  }));
        Assert.assertFalse(Const.isEmpty(new Object[]{ "test" }));
    }

    @Test
    public void testIsEmptyList() {
        Assert.assertTrue(Const.isEmpty(((List) (null))));
        Assert.assertTrue(Const.isEmpty(new ArrayList()));
        Assert.assertFalse(Const.isEmpty(Arrays.asList("test", 1)));
    }

    @Test
    public void testIsEmptyStringBuffer() {
        Assert.assertTrue(Const.isEmpty(((StringBuffer) (null))));
        Assert.assertTrue(Const.isEmpty(new StringBuffer("")));
        Assert.assertFalse(Const.isEmpty(new StringBuffer("test")));
    }

    @Test
    public void testIsEmptyStringBuilder() {
        Assert.assertTrue(Const.isEmpty(((StringBuilder) (null))));
        Assert.assertTrue(Const.isEmpty(new StringBuilder("")));
        Assert.assertFalse(Const.isEmpty(new StringBuilder("test")));
    }

    @Test
    public void testNVL() {
        Assert.assertNull(Const.NVL(null, null));
        ConstTest.assertEquals("test", Const.NVL("test", "test1"));
        ConstTest.assertEquals("test", Const.NVL("test", null));
        ConstTest.assertEquals("test1", Const.NVL(null, "test1"));
    }

    @Test
    public void testNullToEmpty_NVL() {
        ConstTest.assertEquals(Const.NVL(null, ""), Const.nullToEmpty(null));
        ConstTest.assertEquals(Const.NVL("", ""), Const.nullToEmpty(""));
        ConstTest.assertEquals(Const.NVL("xpto", ""), Const.nullToEmpty("xpto"));
    }

    @Test
    public void testNrSpacesBefore() {
        try {
            Const.nrSpacesBefore(null);
            Assert.fail("Expected NullPointerException");
        } catch (NullPointerException ex) {
            // Ignore
        }
        ConstTest.assertEquals(0, Const.nrSpacesBefore(""));
        ConstTest.assertEquals(1, Const.nrSpacesBefore(" "));
        ConstTest.assertEquals(3, Const.nrSpacesBefore("   "));
        ConstTest.assertEquals(0, Const.nrSpacesBefore("test"));
        ConstTest.assertEquals(0, Const.nrSpacesBefore("test  "));
        ConstTest.assertEquals(3, Const.nrSpacesBefore("   test"));
        ConstTest.assertEquals(4, Const.nrSpacesBefore("    test  "));
    }

    @Test
    public void testNrSpacesAfter() {
        try {
            Const.nrSpacesAfter(null);
            Assert.fail("Expected NullPointerException");
        } catch (NullPointerException ex) {
            // Ignore
        }
        ConstTest.assertEquals(0, Const.nrSpacesAfter(""));
        ConstTest.assertEquals(1, Const.nrSpacesAfter(" "));
        ConstTest.assertEquals(3, Const.nrSpacesAfter("   "));
        ConstTest.assertEquals(0, Const.nrSpacesAfter("test"));
        ConstTest.assertEquals(2, Const.nrSpacesAfter("test  "));
        ConstTest.assertEquals(0, Const.nrSpacesAfter("   test"));
        ConstTest.assertEquals(2, Const.nrSpacesAfter("    test  "));
    }

    @Test
    public void testLtrim() {
        ConstTest.assertEquals(null, Const.ltrim(null));
        ConstTest.assertEquals("", Const.ltrim(""));
        ConstTest.assertEquals("", Const.ltrim("  "));
        ConstTest.assertEquals("test ", Const.ltrim("test "));
        ConstTest.assertEquals("test ", Const.ltrim("  test "));
    }

    @Test
    public void testRtrim() {
        ConstTest.assertEquals(null, Const.rtrim(null));
        ConstTest.assertEquals("", Const.rtrim(""));
        ConstTest.assertEquals("", Const.rtrim("  "));
        ConstTest.assertEquals("test", Const.rtrim("test "));
        ConstTest.assertEquals("test ", Const.ltrim("  test "));
    }

    @Test
    public void testTrim() {
        ConstTest.assertEquals(null, Const.trim(null));
        ConstTest.assertEquals("", Const.trim(""));
        ConstTest.assertEquals("", Const.trim("  "));
        ConstTest.assertEquals("test", Const.trim("test "));
        ConstTest.assertEquals("test", Const.trim("  test "));
    }

    @Test
    public void testOnlySpaces() {
        try {
            Const.onlySpaces(null);
            Assert.fail("Expected NullPointerException");
        } catch (NullPointerException ex) {
            // Ignore
        }
        ConstTest.assertEquals(true, Const.onlySpaces(""));
        ConstTest.assertEquals(true, Const.onlySpaces("  "));
        ConstTest.assertEquals(false, Const.onlySpaces("   test "));
    }

    /**
     * Test splitString with String separator.
     */
    @Test
    public void testSplitString() {
        ConstTest.assertEquals(0, Const.splitString("", ";").length);
        ConstTest.assertEquals(0, Const.splitString(null, ";").length);
        String[] a = Const.splitString(";", ";");
        ConstTest.assertEquals(1, a.length);
        ConstTest.assertEquals("", a[0]);
        a = Const.splitString("a;b;c;d", ";");
        ConstTest.assertEquals(4, a.length);
        ConstTest.assertEquals("a", a[0]);
        ConstTest.assertEquals("b", a[1]);
        ConstTest.assertEquals("c", a[2]);
        ConstTest.assertEquals("d", a[3]);
        a = Const.splitString("a;b;c;d;", ";");
        ConstTest.assertEquals(4, a.length);
        ConstTest.assertEquals("a", a[0]);
        ConstTest.assertEquals("b", a[1]);
        ConstTest.assertEquals("c", a[2]);
        ConstTest.assertEquals("d", a[3]);
        a = Const.splitString("AACCAADAaAADD", "AA");
        ConstTest.assertEquals(4, a.length);
        ConstTest.assertEquals("", a[0]);
        ConstTest.assertEquals("CC", a[1]);
        ConstTest.assertEquals("DA", a[2]);
        ConstTest.assertEquals("ADD", a[3]);
        a = Const.splitString("CCAABBAA", "AA");
        ConstTest.assertEquals(2, a.length);
        ConstTest.assertEquals("CC", a[0]);
        ConstTest.assertEquals("BB", a[1]);
    }

    /**
     * Test splitString with char separator.
     */
    @Test
    public void testSplitStringChar() {
        ConstTest.assertEquals(0, Const.splitString("", ';').length);
        ConstTest.assertEquals(0, Const.splitString(null, ';').length);
        String[] a = Const.splitString(";", ';');
        ConstTest.assertEquals(1, a.length);
        ConstTest.assertEquals("", a[0]);
        a = Const.splitString("a;b;c;d", ';');
        ConstTest.assertEquals(4, a.length);
        ConstTest.assertEquals("a", a[0]);
        ConstTest.assertEquals("b", a[1]);
        ConstTest.assertEquals("c", a[2]);
        ConstTest.assertEquals("d", a[3]);
        a = Const.splitString("a;b;c;d;", ';');
        ConstTest.assertEquals(4, a.length);
        ConstTest.assertEquals("a", a[0]);
        ConstTest.assertEquals("b", a[1]);
        ConstTest.assertEquals("c", a[2]);
        ConstTest.assertEquals("d", a[3]);
        a = Const.splitString(";CC;DA;ADD", ';');
        ConstTest.assertEquals(4, a.length);
        ConstTest.assertEquals("", a[0]);
        ConstTest.assertEquals("CC", a[1]);
        ConstTest.assertEquals("DA", a[2]);
        ConstTest.assertEquals("ADD", a[3]);
        a = Const.splitString("CC;BB;", ';');
        ConstTest.assertEquals(2, a.length);
        ConstTest.assertEquals("CC", a[0]);
        ConstTest.assertEquals("BB", a[1]);
    }

    /**
     * Test splitString with delimiter and enclosure
     */
    @Test
    public void testSplitStringNullWithDelimiterNullAndEnclosureNull() {
        String[] result = Const.splitString(null, null, null);
        Assert.assertNull(result);
    }

    @Test
    public void testSplitStringNullWithDelimiterNullAndEnclosureNullRemoveEnclosure() {
        String[] result = Const.splitString(null, null, null, true);
        Assert.assertNull(result);
    }

    @Test
    public void testSplitStringWithDelimiterNullAndEnclosureNull() {
        String stringToSplit = "Hello, world";
        String[] result = Const.splitString(stringToSplit, null, null);
        assertSplit(result, stringToSplit);
    }

    @Test
    public void testSplitStringWithDelimiterNullAndEnclosureNullRemoveEnclosure() {
        String stringToSplit = "Hello, world";
        String[] result = Const.splitString(stringToSplit, null, null, true);
        assertSplit(result, stringToSplit);
    }

    @Test
    public void testSplitStringWithDelimiterAndEnclosureNull() {
        String mask = "Hello%s world";
        String[] chunks = new String[]{ "Hello", " world" };
        String stringToSplit = String.format(mask, ConstTest.DELIMITER1);
        String[] result = Const.splitString(stringToSplit, ConstTest.DELIMITER1, null);
        assertSplit(result, chunks);
    }

    @Test
    public void testSplitStringWithDelimiterAndEnclosureNullMultiChar() {
        String mask = "Hello%s world";
        String[] chunks = new String[]{ "Hello", " world" };
        String stringToSplit = String.format(mask, ConstTest.DELIMITER2);
        String[] result = Const.splitString(stringToSplit, ConstTest.DELIMITER2, null);
        assertSplit(result, chunks);
    }

    @Test
    public void testSplitStringWithDelimiterAndEnclosureNullRemoveEnclosure() {
        String mask = "Hello%s world";
        String[] chunks = new String[]{ "Hello", " world" };
        String stringToSplit = String.format(mask, ConstTest.DELIMITER1);
        String[] result = Const.splitString(stringToSplit, ConstTest.DELIMITER1, null, true);
        assertSplit(result, chunks);
    }

    @Test
    public void testSplitStringWithDelimiterAndEnclosureNullMultiCharRemoveEnclosure() {
        String mask = "Hello%s world";
        String[] chunks = new String[]{ "Hello", " world" };
        String stringToSplit = String.format(mask, ConstTest.DELIMITER2);
        String[] result = Const.splitString(stringToSplit, ConstTest.DELIMITER2, null, true);
        assertSplit(result, chunks);
    }

    @Test
    public void testSplitStringWithDelimiterAndEmptyEnclosure() {
        String mask = "Hello%s world";
        String[] chunks = new String[]{ "Hello", " world" };
        String stringToSplit = String.format(mask, ConstTest.DELIMITER1);
        String[] result = Const.splitString(stringToSplit, ConstTest.DELIMITER1, "");
        assertSplit(result, chunks);
    }

    @Test
    public void testSplitStringWithDelimiterAndEmptyEnclosureMultiChar() {
        String mask = "Hello%s world";
        String[] chunks = new String[]{ "Hello", " world" };
        String stringToSplit = String.format(mask, ConstTest.DELIMITER2);
        String[] result = Const.splitString(stringToSplit, ConstTest.DELIMITER2, "");
        assertSplit(result, chunks);
    }

    @Test
    public void testSplitStringWithDelimiterAndEmptyEnclosureRemoveEnclosure() {
        String mask = "Hello%s world";
        String[] chunks = new String[]{ "Hello", " world" };
        String stringToSplit = String.format(mask, ConstTest.DELIMITER1);
        String[] result = Const.splitString(stringToSplit, ConstTest.DELIMITER1, "", true);
        assertSplit(result, chunks);
    }

    @Test
    public void testSplitStringWithDelimiterAndEmptyEnclosureMultiCharRemoveEnclosure() {
        String mask = "Hello%s world";
        String[] chunks = new String[]{ "Hello", " world" };
        String stringToSplit = String.format(mask, ConstTest.DELIMITER2);
        String[] result = Const.splitString(stringToSplit, ConstTest.DELIMITER2, "", true);
        assertSplit(result, chunks);
    }

    @Test
    public void testSplitStringWithDelimiterAndQuoteEnclosure1() {
        // "Hello, world"
        String mask = "%sHello%s world%s";
        String stringToSplit = String.format(mask, ConstTest.ENCLOSURE1, ConstTest.DELIMITER1, ConstTest.ENCLOSURE1);
        String[] result = Const.splitString(stringToSplit, ConstTest.DELIMITER1, ConstTest.ENCLOSURE1);
        assertSplit(result, stringToSplit);
    }

    @Test
    public void testSplitStringWithDelimiterAndQuoteEnclosureMultiChar1() {
        // "Hello, world"
        String mask = "%sHello%s world%s";
        String stringToSplit = String.format(mask, ConstTest.ENCLOSURE2, ConstTest.DELIMITER2, ConstTest.ENCLOSURE2);
        String[] result = Const.splitString(stringToSplit, ConstTest.DELIMITER2, ConstTest.ENCLOSURE2);
        assertSplit(result, stringToSplit);
    }

    @Test
    public void testSplitStringWithDelimiterAndQuoteEnclosureRemoveEnclosure1() {
        // "Hello, world"
        String mask = "%sHello%s world%s";
        String[] chunks1 = new String[]{ ("Hello" + (ConstTest.DELIMITER1)) + " world" };
        String stringToSplit = String.format(mask, ConstTest.ENCLOSURE1, ConstTest.DELIMITER1, ConstTest.ENCLOSURE1);
        String[] result = Const.splitString(stringToSplit, ConstTest.DELIMITER1, ConstTest.ENCLOSURE1, true);
        assertSplit(result, chunks1);
    }

    @Test
    public void testSplitStringWithDelimiterAndQuoteEnclosureMultiCharRemoveEnclosure1() {
        // "Hello, world"
        String mask = "%sHello%s world%s";
        String[] chunks2 = new String[]{ ("Hello" + (ConstTest.DELIMITER2)) + " world" };
        String stringToSplit = String.format(mask, ConstTest.ENCLOSURE2, ConstTest.DELIMITER2, ConstTest.ENCLOSURE2);
        String[] result = Const.splitString(stringToSplit, ConstTest.DELIMITER2, ConstTest.ENCLOSURE2, true);
        assertSplit(result, chunks2);
    }

    @Test
    public void testSplitStringWithDelimiterAndQuoteEnclosure2() {
        testSplitStringWithDelimiterAndQuoteEnclosure2(ConstTest.ENCLOSURE1, ConstTest.DELIMITER1);
    }

    @Test
    public void testSplitStringWithDelimiterAndQuoteEnclosureMultiChar2() {
        testSplitStringWithDelimiterAndQuoteEnclosure2(ConstTest.ENCLOSURE2, ConstTest.DELIMITER2);
    }

    @Test
    public void testSplitStringWithDelimiterAndQuoteEnclosureRemoveEnclosure2() {
        testSplitStringWithDelimiterAndQuoteEnclosureRemoveEnclosure2(ConstTest.ENCLOSURE1, ConstTest.DELIMITER1);
    }

    @Test
    public void testSplitStringWithDelimiterAndQuoteEnclosureMultiCharRemoveEnclosure2() {
        testSplitStringWithDelimiterAndQuoteEnclosureRemoveEnclosure2(ConstTest.ENCLOSURE2, ConstTest.DELIMITER2);
    }

    @Test
    public void testSplitStringWithDelimiterAndQuoteEnclosure3() {
        testSplitStringWithDelimiterAndQuoteEnclosure3(ConstTest.ENCLOSURE1, ConstTest.DELIMITER1);
    }

    @Test
    public void testSplitStringWithDelimiterAndQuoteEnclosureMultiChar3() {
        testSplitStringWithDelimiterAndQuoteEnclosure3(ConstTest.ENCLOSURE2, ConstTest.DELIMITER2);
    }

    @Test
    public void testSplitStringWithDelimiterAndQuoteEnclosureRemovesEnclosure3() {
        testSplitStringWithDelimiterAndQuoteEnclosureRemovesEnclosure3(ConstTest.ENCLOSURE1, ConstTest.DELIMITER1);
    }

    @Test
    public void testSplitStringWithDelimiterAndQuoteEnclosureMultiCharRemovesEnclosure3() {
        testSplitStringWithDelimiterAndQuoteEnclosureRemovesEnclosure3(ConstTest.ENCLOSURE2, ConstTest.DELIMITER2);
    }

    @Test
    public void testSplitStringWithDifferentDelimiterAndEnclosure() {
        // Try a different delimiter and enclosure
        String[] result = Const.splitString("a;'b;c;d';'e,f';'g';h", ";", "'");
        Assert.assertNotNull(result);
        ConstTest.assertEquals(5, result.length);
        ConstTest.assertEquals("a", result[0]);
        ConstTest.assertEquals("'b;c;d'", result[1]);
        ConstTest.assertEquals("'e,f'", result[2]);
        ConstTest.assertEquals("'g'", result[3]);
        ConstTest.assertEquals("h", result[4]);
        // Check for null and empty as the last split
        result = Const.splitString("a;b;c;", ";", null);
        Assert.assertNotNull(result);
        ConstTest.assertEquals(3, result.length);
        result = Const.splitString("a;b;c;''", ";", "'");
        Assert.assertNotNull(result);
        ConstTest.assertEquals(4, result.length);
    }

    @Test
    public void testSplitStringWithMultipleCharacterDelimiterAndEnclosure() {
        // Check for multiple-character strings
        String[] result = Const.splitString("html this is a web page html</newpage>html and so is this html", "</newpage>", "html");
        Assert.assertNotNull(result);
        ConstTest.assertEquals(2, result.length);
        ConstTest.assertEquals("html this is a web page html", result[0]);
        ConstTest.assertEquals("html and so is this html", result[1]);
    }

    @Test
    public void testSplitStringRemoveEnclosureNested1() {
        testSplitStringRemoveEnclosureNested1(ConstTest.ENCLOSURE1, ConstTest.DELIMITER1);
    }

    @Test
    public void testSplitStringRemoveEnclosureNestedMultiChar1() {
        testSplitStringRemoveEnclosureNested1(ConstTest.ENCLOSURE2, ConstTest.DELIMITER2);
    }

    @Test
    public void testSplitStringRemoveEnclosureNested2() {
        testSplitStringRemoveEnclosureNested(ConstTest.ENCLOSURE1, ConstTest.DELIMITER1);
    }

    @Test
    public void testSplitStringRemoveEnclosureNestedMultiChar2() {
        testSplitStringRemoveEnclosureNested(ConstTest.ENCLOSURE2, ConstTest.DELIMITER2);
    }

    /**
     * Test splitString with delimiter and enclosure
     */
    @Test
    public void testSplitStringWithEscaping() {
        String[] result;
        result = Const.splitString(null, null, null);
        Assert.assertNull(result);
        result = Const.splitString("Hello, world", null, null);
        Assert.assertNotNull(result);
        ConstTest.assertEquals(result.length, 1);
        ConstTest.assertEquals(result[0], "Hello, world");
        result = Const.splitString("Hello\\, world,Hello\\, planet,Hello\\, 3rd rock", ',', true);
        Assert.assertNotNull(result);
        ConstTest.assertEquals(result.length, 3);
        ConstTest.assertEquals(result[0], "Hello\\, world");
        ConstTest.assertEquals(result[1], "Hello\\, planet");
        ConstTest.assertEquals(result[2], "Hello\\, 3rd rock");
    }

    /**
     * Test splitPath.
     */
    @Test
    public void testSplitPath() {
        String[] a = Const.splitPath("", "/");
        ConstTest.assertEquals(0, a.length);
        a = Const.splitPath(null, "/");
        ConstTest.assertEquals(0, a.length);
        a = Const.splitPath("/", "/");
        ConstTest.assertEquals(0, a.length);
        a = Const.splitPath("/level1", "/");
        ConstTest.assertEquals(1, a.length);
        ConstTest.assertEquals("level1", a[0]);
        a = Const.splitPath("level1", "/");
        ConstTest.assertEquals(1, a.length);
        ConstTest.assertEquals("level1", a[0]);
        a = Const.splitPath("/level1/level2", "/");
        ConstTest.assertEquals(2, a.length);
        ConstTest.assertEquals("level1", a[0]);
        ConstTest.assertEquals("level2", a[1]);
        a = Const.splitPath("level1/level2", "/");
        ConstTest.assertEquals(2, a.length);
        ConstTest.assertEquals("level1", a[0]);
        ConstTest.assertEquals("level2", a[1]);
        a = Const.splitPath("/level1/level2/lvl3", "/");
        ConstTest.assertEquals(3, a.length);
        ConstTest.assertEquals("level1", a[0]);
        ConstTest.assertEquals("level2", a[1]);
        ConstTest.assertEquals("lvl3", a[2]);
        a = Const.splitPath("level1/level2/lvl3", "/");
        ConstTest.assertEquals(3, a.length);
        ConstTest.assertEquals("level1", a[0]);
        ConstTest.assertEquals("level2", a[1]);
        ConstTest.assertEquals("lvl3", a[2]);
    }

    @Test
    public void testRound_BigDecimal() {
        ConstTest.assertEquals(new BigDecimal("1.0"), Const.round(new BigDecimal("1.0"), 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("1.0"), Const.round(new BigDecimal("1.0"), 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("1.0"), Const.round(new BigDecimal("1.0"), 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("1.0"), Const.round(new BigDecimal("1.0"), 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("1.0"), Const.round(new BigDecimal("1.0"), 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("1.0"), Const.round(new BigDecimal("1.0"), 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("1.0"), Const.round(new BigDecimal("1.0"), 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("1.0"), Const.round(new BigDecimal("1.0"), 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("1.2"), 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("1.0"), Const.round(new BigDecimal("1.2"), 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("1.2"), 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("1.0"), Const.round(new BigDecimal("1.2"), 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("1.0"), Const.round(new BigDecimal("1.2"), 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("1.0"), Const.round(new BigDecimal("1.2"), 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("1.0"), Const.round(new BigDecimal("1.2"), 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("1.0"), Const.round(new BigDecimal("1.2"), 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("1.5"), 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("1.0"), Const.round(new BigDecimal("1.5"), 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("1.5"), 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("1.0"), Const.round(new BigDecimal("1.5"), 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("1.5"), 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("1.0"), Const.round(new BigDecimal("1.5"), 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("1.5"), 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("1.5"), 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("1.7"), 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("1.0"), Const.round(new BigDecimal("1.7"), 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("1.7"), 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("1.0"), Const.round(new BigDecimal("1.7"), 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("1.7"), 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("1.7"), 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("1.7"), 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("1.7"), 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("2.0"), 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("2.0"), 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("2.0"), 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("2.0"), 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("2.0"), 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("2.0"), 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("2.0"), 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("2.0"), 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("3.0"), Const.round(new BigDecimal("2.2"), 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("2.2"), 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("3.0"), Const.round(new BigDecimal("2.2"), 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("2.2"), 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("2.2"), 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("2.2"), 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("2.2"), 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("2.2"), 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("3.0"), Const.round(new BigDecimal("2.5"), 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("2.5"), 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("3.0"), Const.round(new BigDecimal("2.5"), 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("2.5"), 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("3.0"), Const.round(new BigDecimal("2.5"), 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("2.5"), 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("2.5"), 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("3.0"), Const.round(new BigDecimal("2.5"), 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("3.0"), Const.round(new BigDecimal("2.7"), 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("2.7"), 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("3.0"), Const.round(new BigDecimal("2.7"), 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("2.0"), Const.round(new BigDecimal("2.7"), 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("3.0"), Const.round(new BigDecimal("2.7"), 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("3.0"), Const.round(new BigDecimal("2.7"), 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("3.0"), Const.round(new BigDecimal("2.7"), 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("3.0"), Const.round(new BigDecimal("2.7"), 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("-1.0"), Const.round(new BigDecimal("-1.0"), 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("-1.0"), Const.round(new BigDecimal("-1.0"), 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("-1.0"), Const.round(new BigDecimal("-1.0"), 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("-1.0"), Const.round(new BigDecimal("-1.0"), 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("-1.0"), Const.round(new BigDecimal("-1.0"), 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("-1.0"), Const.round(new BigDecimal("-1.0"), 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("-1.0"), Const.round(new BigDecimal("-1.0"), 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("-1.0"), Const.round(new BigDecimal("-1.0"), 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-1.2"), 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("-1.0"), Const.round(new BigDecimal("-1.2"), 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("-1.0"), Const.round(new BigDecimal("-1.2"), 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-1.2"), 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("-1.0"), Const.round(new BigDecimal("-1.2"), 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("-1.0"), Const.round(new BigDecimal("-1.2"), 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("-1.0"), Const.round(new BigDecimal("-1.2"), 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("-1.0"), Const.round(new BigDecimal("-1.2"), 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-1.5"), 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("-1.0"), Const.round(new BigDecimal("-1.5"), 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("-1.0"), Const.round(new BigDecimal("-1.5"), 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-1.5"), 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-1.5"), 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("-1.0"), Const.round(new BigDecimal("-1.5"), 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-1.5"), 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("-1.0"), Const.round(new BigDecimal("-1.5"), 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-1.7"), 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("-1.0"), Const.round(new BigDecimal("-1.7"), 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("-1.0"), Const.round(new BigDecimal("-1.7"), 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-1.7"), 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-1.7"), 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-1.7"), 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-1.7"), 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-1.7"), 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-2.0"), 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-2.0"), 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-2.0"), 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-2.0"), 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-2.0"), 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-2.0"), 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-2.0"), 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-2.0"), 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("-3.0"), Const.round(new BigDecimal("-2.2"), 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-2.2"), 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-2.2"), 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("-3.0"), Const.round(new BigDecimal("-2.2"), 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-2.2"), 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-2.2"), 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-2.2"), 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-2.2"), 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("-3.0"), Const.round(new BigDecimal("-2.5"), 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-2.5"), 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-2.5"), 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("-3.0"), Const.round(new BigDecimal("-2.5"), 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("-3.0"), Const.round(new BigDecimal("-2.5"), 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-2.5"), 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-2.5"), 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-2.5"), 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("-3.0"), Const.round(new BigDecimal("-2.7"), 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-2.7"), 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("-2.0"), Const.round(new BigDecimal("-2.7"), 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("-3.0"), Const.round(new BigDecimal("-2.7"), 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("-3.0"), Const.round(new BigDecimal("-2.7"), 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("-3.0"), Const.round(new BigDecimal("-2.7"), 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("-3.0"), Const.round(new BigDecimal("-2.7"), 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("-3.0"), Const.round(new BigDecimal("-2.7"), 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("0.010"), Const.round(new BigDecimal("0.010"), 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("0.010"), Const.round(new BigDecimal("0.010"), 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("0.010"), Const.round(new BigDecimal("0.010"), 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("0.010"), Const.round(new BigDecimal("0.010"), 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("0.010"), Const.round(new BigDecimal("0.010"), 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("0.010"), Const.round(new BigDecimal("0.010"), 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("0.010"), Const.round(new BigDecimal("0.010"), 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("0.010"), Const.round(new BigDecimal("0.010"), 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.012"), 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("0.010"), Const.round(new BigDecimal("0.012"), 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.012"), 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("0.010"), Const.round(new BigDecimal("0.012"), 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("0.010"), Const.round(new BigDecimal("0.012"), 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("0.010"), Const.round(new BigDecimal("0.012"), 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("0.010"), Const.round(new BigDecimal("0.012"), 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("0.010"), Const.round(new BigDecimal("0.012"), 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.015"), 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("0.010"), Const.round(new BigDecimal("0.015"), 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.015"), 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("0.010"), Const.round(new BigDecimal("0.015"), 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.015"), 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("0.010"), Const.round(new BigDecimal("0.015"), 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.015"), 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.015"), 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.017"), 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("0.010"), Const.round(new BigDecimal("0.017"), 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.017"), 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("0.010"), Const.round(new BigDecimal("0.017"), 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.017"), 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.017"), 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.017"), 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.017"), 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.020"), 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.020"), 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.020"), 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.020"), 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.020"), 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.020"), 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.020"), 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.020"), 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("0.030"), Const.round(new BigDecimal("0.022"), 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.022"), 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("0.030"), Const.round(new BigDecimal("0.022"), 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.022"), 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.022"), 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.022"), 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.022"), 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.022"), 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("0.030"), Const.round(new BigDecimal("0.025"), 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.025"), 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("0.030"), Const.round(new BigDecimal("0.025"), 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.025"), 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("0.030"), Const.round(new BigDecimal("0.025"), 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.025"), 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.025"), 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("0.030"), Const.round(new BigDecimal("0.025"), 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("0.030"), Const.round(new BigDecimal("0.027"), 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.027"), 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("0.030"), Const.round(new BigDecimal("0.027"), 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("0.020"), Const.round(new BigDecimal("0.027"), 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("0.030"), Const.round(new BigDecimal("0.027"), 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("0.030"), Const.round(new BigDecimal("0.027"), 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("0.030"), Const.round(new BigDecimal("0.027"), 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("0.030"), Const.round(new BigDecimal("0.027"), 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("-0.010"), Const.round(new BigDecimal("-0.010"), 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("-0.010"), Const.round(new BigDecimal("-0.010"), 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("-0.010"), Const.round(new BigDecimal("-0.010"), 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("-0.010"), Const.round(new BigDecimal("-0.010"), 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("-0.010"), Const.round(new BigDecimal("-0.010"), 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("-0.010"), Const.round(new BigDecimal("-0.010"), 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("-0.010"), Const.round(new BigDecimal("-0.010"), 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("-0.010"), Const.round(new BigDecimal("-0.010"), 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.012"), 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("-0.010"), Const.round(new BigDecimal("-0.012"), 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("-0.010"), Const.round(new BigDecimal("-0.012"), 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.012"), 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("-0.010"), Const.round(new BigDecimal("-0.012"), 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("-0.010"), Const.round(new BigDecimal("-0.012"), 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("-0.010"), Const.round(new BigDecimal("-0.012"), 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("-0.010"), Const.round(new BigDecimal("-0.012"), 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.015"), 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("-0.010"), Const.round(new BigDecimal("-0.015"), 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("-0.010"), Const.round(new BigDecimal("-0.015"), 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.015"), 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.015"), 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("-0.010"), Const.round(new BigDecimal("-0.015"), 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.015"), 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("-0.010"), Const.round(new BigDecimal("-0.015"), 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.017"), 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("-0.010"), Const.round(new BigDecimal("-0.017"), 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("-0.010"), Const.round(new BigDecimal("-0.017"), 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.017"), 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.017"), 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.017"), 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.017"), 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.017"), 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.020"), 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.020"), 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.020"), 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.020"), 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.020"), 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.020"), 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.020"), 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.020"), 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("-0.030"), Const.round(new BigDecimal("-0.022"), 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.022"), 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.022"), 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("-0.030"), Const.round(new BigDecimal("-0.022"), 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.022"), 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.022"), 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.022"), 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.022"), 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("-0.030"), Const.round(new BigDecimal("-0.025"), 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.025"), 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.025"), 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("-0.030"), Const.round(new BigDecimal("-0.025"), 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("-0.030"), Const.round(new BigDecimal("-0.025"), 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.025"), 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.025"), 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.025"), 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("-0.030"), Const.round(new BigDecimal("-0.027"), 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.027"), 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("-0.020"), Const.round(new BigDecimal("-0.027"), 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("-0.030"), Const.round(new BigDecimal("-0.027"), 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("-0.030"), Const.round(new BigDecimal("-0.027"), 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("-0.030"), Const.round(new BigDecimal("-0.027"), 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("-0.030"), Const.round(new BigDecimal("-0.027"), 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("-0.030"), Const.round(new BigDecimal("-0.027"), 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("100.0"), Const.round(new BigDecimal("100.0"), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("100.0"), Const.round(new BigDecimal("100.0"), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("100.0"), Const.round(new BigDecimal("100.0"), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("100.0"), Const.round(new BigDecimal("100.0"), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("100.0"), Const.round(new BigDecimal("100.0"), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("100.0"), Const.round(new BigDecimal("100.0"), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("100.0"), Const.round(new BigDecimal("100.0"), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("100.0"), Const.round(new BigDecimal("100.0"), (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("120.0"), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("100.0"), Const.round(new BigDecimal("120.0"), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("120.0"), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("100.0"), Const.round(new BigDecimal("120.0"), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("100.0"), Const.round(new BigDecimal("120.0"), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("100.0"), Const.round(new BigDecimal("120.0"), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("100.0"), Const.round(new BigDecimal("120.0"), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("100.0"), Const.round(new BigDecimal("120.0"), (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("150.0"), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("100.0"), Const.round(new BigDecimal("150.0"), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("150.0"), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("100.0"), Const.round(new BigDecimal("150.0"), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("150.0"), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("100.0"), Const.round(new BigDecimal("150.0"), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("150.0"), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("150.0"), (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("170.0"), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("100.0"), Const.round(new BigDecimal("170.0"), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("170.0"), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("100.0"), Const.round(new BigDecimal("170.0"), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("170.0"), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("170.0"), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("170.0"), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("170.0"), (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("200.0"), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("200.0"), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("200.0"), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("200.0"), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("200.0"), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("200.0"), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("200.0"), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("200.0"), (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("300.0"), Const.round(new BigDecimal("220.0"), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("220.0"), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("300.0"), Const.round(new BigDecimal("220.0"), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("220.0"), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("220.0"), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("220.0"), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("220.0"), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("220.0"), (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("300.0"), Const.round(new BigDecimal("250.0"), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("250.0"), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("300.0"), Const.round(new BigDecimal("250.0"), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("250.0"), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("300.0"), Const.round(new BigDecimal("250.0"), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("250.0"), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("250.0"), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("300.0"), Const.round(new BigDecimal("250.0"), (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("300.0"), Const.round(new BigDecimal("270.0"), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("270.0"), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("300.0"), Const.round(new BigDecimal("270.0"), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("200.0"), Const.round(new BigDecimal("270.0"), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("300.0"), Const.round(new BigDecimal("270.0"), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("300.0"), Const.round(new BigDecimal("270.0"), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("300.0"), Const.round(new BigDecimal("270.0"), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("300.0"), Const.round(new BigDecimal("270.0"), (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("-100.0"), Const.round(new BigDecimal("-100.0"), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("-100.0"), Const.round(new BigDecimal("-100.0"), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("-100.0"), Const.round(new BigDecimal("-100.0"), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("-100.0"), Const.round(new BigDecimal("-100.0"), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("-100.0"), Const.round(new BigDecimal("-100.0"), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("-100.0"), Const.round(new BigDecimal("-100.0"), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("-100.0"), Const.round(new BigDecimal("-100.0"), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("-100.0"), Const.round(new BigDecimal("-100.0"), (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-120.0"), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("-100.0"), Const.round(new BigDecimal("-120.0"), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("-100.0"), Const.round(new BigDecimal("-120.0"), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-120.0"), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("-100.0"), Const.round(new BigDecimal("-120.0"), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("-100.0"), Const.round(new BigDecimal("-120.0"), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("-100.0"), Const.round(new BigDecimal("-120.0"), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("-100.0"), Const.round(new BigDecimal("-120.0"), (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-150.0"), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("-100.0"), Const.round(new BigDecimal("-150.0"), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("-100.0"), Const.round(new BigDecimal("-150.0"), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-150.0"), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-150.0"), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("-100.0"), Const.round(new BigDecimal("-150.0"), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-150.0"), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("-100.0"), Const.round(new BigDecimal("-150.0"), (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-170.0"), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("-100.0"), Const.round(new BigDecimal("-170.0"), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("-100.0"), Const.round(new BigDecimal("-170.0"), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-170.0"), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-170.0"), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-170.0"), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-170.0"), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-170.0"), (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-200.0"), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-200.0"), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-200.0"), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-200.0"), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-200.0"), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-200.0"), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-200.0"), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-200.0"), (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("-300.0"), Const.round(new BigDecimal("-220.0"), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-220.0"), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-220.0"), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("-300.0"), Const.round(new BigDecimal("-220.0"), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-220.0"), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-220.0"), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-220.0"), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-220.0"), (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("-300.0"), Const.round(new BigDecimal("-250.0"), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-250.0"), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-250.0"), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("-300.0"), Const.round(new BigDecimal("-250.0"), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("-300.0"), Const.round(new BigDecimal("-250.0"), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-250.0"), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-250.0"), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-250.0"), (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals(new BigDecimal("-300.0"), Const.round(new BigDecimal("-270.0"), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-270.0"), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(new BigDecimal("-200.0"), Const.round(new BigDecimal("-270.0"), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(new BigDecimal("-300.0"), Const.round(new BigDecimal("-270.0"), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(new BigDecimal("-300.0"), Const.round(new BigDecimal("-270.0"), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(new BigDecimal("-300.0"), Const.round(new BigDecimal("-270.0"), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(new BigDecimal("-300.0"), Const.round(new BigDecimal("-270.0"), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(new BigDecimal("-300.0"), Const.round(new BigDecimal("-270.0"), (-2), ROUND_HALF_CEILING));
    }

    @Test
    public void testRound() {
        ConstTest.assertEquals(1.0, Const.round(1.0, 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(1.0, Const.round(1.0, 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(1.0, Const.round(1.0, 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(1.0, Const.round(1.0, 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(1.0, Const.round(1.0, 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(1.0, Const.round(1.0, 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(1.0, Const.round(1.0, 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(1.0, Const.round(1.0, 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals(2.0, Const.round(1.2, 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(1.0, Const.round(1.2, 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(2.0, Const.round(1.2, 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(1.0, Const.round(1.2, 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(1.0, Const.round(1.2, 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(1.0, Const.round(1.2, 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(1.0, Const.round(1.2, 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(1.0, Const.round(1.2, 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals(2.0, Const.round(1.5, 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(1.0, Const.round(1.5, 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(2.0, Const.round(1.5, 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(1.0, Const.round(1.5, 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(2.0, Const.round(1.5, 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(1.0, Const.round(1.5, 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(2.0, Const.round(1.5, 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(2.0, Const.round(1.5, 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals(2.0, Const.round(1.7, 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(1.0, Const.round(1.7, 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(2.0, Const.round(1.7, 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(1.0, Const.round(1.7, 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(2.0, Const.round(1.7, 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(2.0, Const.round(1.7, 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(2.0, Const.round(1.7, 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(2.0, Const.round(1.7, 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals(2.0, Const.round(2.0, 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(2.0, Const.round(2.0, 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(2.0, Const.round(2.0, 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(2.0, Const.round(2.0, 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(2.0, Const.round(2.0, 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(2.0, Const.round(2.0, 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(2.0, Const.round(2.0, 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(2.0, Const.round(2.0, 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals(3.0, Const.round(2.2, 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(2.0, Const.round(2.2, 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(3.0, Const.round(2.2, 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(2.0, Const.round(2.2, 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(2.0, Const.round(2.2, 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(2.0, Const.round(2.2, 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(2.0, Const.round(2.2, 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(2.0, Const.round(2.2, 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals(3.0, Const.round(2.5, 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(2.0, Const.round(2.5, 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(3.0, Const.round(2.5, 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(2.0, Const.round(2.5, 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(3.0, Const.round(2.5, 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(2.0, Const.round(2.5, 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(2.0, Const.round(2.5, 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(3.0, Const.round(2.5, 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals(3.0, Const.round(2.7, 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(2.0, Const.round(2.7, 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(3.0, Const.round(2.7, 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(2.0, Const.round(2.7, 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(3.0, Const.round(2.7, 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(3.0, Const.round(2.7, 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(3.0, Const.round(2.7, 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(3.0, Const.round(2.7, 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals((-1.0), Const.round((-1.0), 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-1.0), Const.round((-1.0), 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-1.0), Const.round((-1.0), 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-1.0), Const.round((-1.0), 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-1.0), Const.round((-1.0), 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-1.0), Const.round((-1.0), 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-1.0), Const.round((-1.0), 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-1.0), Const.round((-1.0), 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals((-2.0), Const.round((-1.2), 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-1.0), Const.round((-1.2), 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-1.0), Const.round((-1.2), 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-2.0), Const.round((-1.2), 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-1.0), Const.round((-1.2), 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-1.0), Const.round((-1.2), 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-1.0), Const.round((-1.2), 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-1.0), Const.round((-1.2), 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals((-2.0), Const.round((-1.5), 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-1.0), Const.round((-1.5), 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-1.0), Const.round((-1.5), 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-2.0), Const.round((-1.5), 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-2.0), Const.round((-1.5), 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-1.0), Const.round((-1.5), 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-2.0), Const.round((-1.5), 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-1.0), Const.round((-1.5), 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals((-2.0), Const.round((-1.7), 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-1.0), Const.round((-1.7), 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-1.0), Const.round((-1.7), 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-2.0), Const.round((-1.7), 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-2.0), Const.round((-1.7), 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-2.0), Const.round((-1.7), 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-2.0), Const.round((-1.7), 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-2.0), Const.round((-1.7), 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals((-2.0), Const.round((-2.0), 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-2.0), Const.round((-2.0), 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-2.0), Const.round((-2.0), 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-2.0), Const.round((-2.0), 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-2.0), Const.round((-2.0), 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-2.0), Const.round((-2.0), 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-2.0), Const.round((-2.0), 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-2.0), Const.round((-2.0), 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals((-3.0), Const.round((-2.2), 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-2.0), Const.round((-2.2), 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-2.0), Const.round((-2.2), 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-3.0), Const.round((-2.2), 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-2.0), Const.round((-2.2), 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-2.0), Const.round((-2.2), 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-2.0), Const.round((-2.2), 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-2.0), Const.round((-2.2), 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals((-3.0), Const.round((-2.5), 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-2.0), Const.round((-2.5), 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-2.0), Const.round((-2.5), 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-3.0), Const.round((-2.5), 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-3.0), Const.round((-2.5), 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-2.0), Const.round((-2.5), 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-2.0), Const.round((-2.5), 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-2.0), Const.round((-2.5), 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals((-3.0), Const.round((-2.7), 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-2.0), Const.round((-2.7), 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-2.0), Const.round((-2.7), 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-3.0), Const.round((-2.7), 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-3.0), Const.round((-2.7), 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-3.0), Const.round((-2.7), 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-3.0), Const.round((-2.7), 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-3.0), Const.round((-2.7), 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals(0.01, Const.round(0.01, 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(0.01, Const.round(0.01, 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(0.01, Const.round(0.01, 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(0.01, Const.round(0.01, 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(0.01, Const.round(0.01, 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(0.01, Const.round(0.01, 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(0.01, Const.round(0.01, 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(0.01, Const.round(0.01, 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals(0.02, Const.round(0.012, 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(0.01, Const.round(0.012, 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(0.02, Const.round(0.012, 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(0.01, Const.round(0.012, 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(0.01, Const.round(0.012, 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(0.01, Const.round(0.012, 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(0.01, Const.round(0.012, 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(0.01, Const.round(0.012, 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals(0.02, Const.round(0.015, 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(0.01, Const.round(0.015, 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(0.02, Const.round(0.015, 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(0.01, Const.round(0.015, 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(0.02, Const.round(0.015, 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(0.01, Const.round(0.015, 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(0.02, Const.round(0.015, 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(0.02, Const.round(0.015, 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals(0.02, Const.round(0.017, 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(0.01, Const.round(0.017, 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(0.02, Const.round(0.017, 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(0.01, Const.round(0.017, 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(0.02, Const.round(0.017, 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(0.02, Const.round(0.017, 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(0.02, Const.round(0.017, 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(0.02, Const.round(0.017, 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals(0.02, Const.round(0.02, 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(0.02, Const.round(0.02, 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(0.02, Const.round(0.02, 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(0.02, Const.round(0.02, 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(0.02, Const.round(0.02, 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(0.02, Const.round(0.02, 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(0.02, Const.round(0.02, 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(0.02, Const.round(0.02, 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals(0.03, Const.round(0.022, 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(0.02, Const.round(0.022, 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(0.03, Const.round(0.022, 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(0.02, Const.round(0.022, 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(0.02, Const.round(0.022, 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(0.02, Const.round(0.022, 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(0.02, Const.round(0.022, 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(0.02, Const.round(0.022, 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals(0.03, Const.round(0.025, 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(0.02, Const.round(0.025, 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(0.03, Const.round(0.025, 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(0.02, Const.round(0.025, 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(0.03, Const.round(0.025, 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(0.02, Const.round(0.025, 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(0.02, Const.round(0.025, 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(0.03, Const.round(0.025, 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals(0.03, Const.round(0.027, 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(0.02, Const.round(0.027, 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(0.03, Const.round(0.027, 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(0.02, Const.round(0.027, 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(0.03, Const.round(0.027, 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(0.03, Const.round(0.027, 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(0.03, Const.round(0.027, 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(0.03, Const.round(0.027, 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals((-0.01), Const.round((-0.01), 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-0.01), Const.round((-0.01), 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-0.01), Const.round((-0.01), 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-0.01), Const.round((-0.01), 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-0.01), Const.round((-0.01), 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-0.01), Const.round((-0.01), 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-0.01), Const.round((-0.01), 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-0.01), Const.round((-0.01), 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals((-0.02), Const.round((-0.012), 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-0.01), Const.round((-0.012), 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-0.01), Const.round((-0.012), 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-0.02), Const.round((-0.012), 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-0.01), Const.round((-0.012), 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-0.01), Const.round((-0.012), 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-0.01), Const.round((-0.012), 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-0.01), Const.round((-0.012), 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals((-0.02), Const.round((-0.015), 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-0.01), Const.round((-0.015), 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-0.01), Const.round((-0.015), 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-0.02), Const.round((-0.015), 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-0.02), Const.round((-0.015), 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-0.01), Const.round((-0.015), 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-0.02), Const.round((-0.015), 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-0.01), Const.round((-0.015), 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals((-0.02), Const.round((-0.017), 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-0.01), Const.round((-0.017), 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-0.01), Const.round((-0.017), 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-0.02), Const.round((-0.017), 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-0.02), Const.round((-0.017), 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-0.02), Const.round((-0.017), 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-0.02), Const.round((-0.017), 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-0.02), Const.round((-0.017), 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals((-0.02), Const.round((-0.02), 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-0.02), Const.round((-0.02), 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-0.02), Const.round((-0.02), 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-0.02), Const.round((-0.02), 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-0.02), Const.round((-0.02), 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-0.02), Const.round((-0.02), 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-0.02), Const.round((-0.02), 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-0.02), Const.round((-0.02), 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals((-0.03), Const.round((-0.022), 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-0.02), Const.round((-0.022), 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-0.02), Const.round((-0.022), 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-0.03), Const.round((-0.022), 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-0.02), Const.round((-0.022), 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-0.02), Const.round((-0.022), 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-0.02), Const.round((-0.022), 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-0.02), Const.round((-0.022), 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals((-0.03), Const.round((-0.025), 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-0.02), Const.round((-0.025), 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-0.02), Const.round((-0.025), 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-0.03), Const.round((-0.025), 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-0.03), Const.round((-0.025), 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-0.02), Const.round((-0.025), 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-0.02), Const.round((-0.025), 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-0.02), Const.round((-0.025), 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals((-0.03), Const.round((-0.027), 2, BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-0.02), Const.round((-0.027), 2, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-0.02), Const.round((-0.027), 2, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-0.03), Const.round((-0.027), 2, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-0.03), Const.round((-0.027), 2, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-0.03), Const.round((-0.027), 2, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-0.03), Const.round((-0.027), 2, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-0.03), Const.round((-0.027), 2, ROUND_HALF_CEILING));
        ConstTest.assertEquals(100.0, Const.round(100.0, (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(100.0, Const.round(100.0, (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(100.0, Const.round(100.0, (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(100.0, Const.round(100.0, (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(100.0, Const.round(100.0, (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(100.0, Const.round(100.0, (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(100.0, Const.round(100.0, (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(100.0, Const.round(100.0, (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals(200.0, Const.round(120.0, (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(100.0, Const.round(120.0, (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(200.0, Const.round(120.0, (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(100.0, Const.round(120.0, (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(100.0, Const.round(120.0, (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(100.0, Const.round(120.0, (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(100.0, Const.round(120.0, (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(100.0, Const.round(120.0, (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals(200.0, Const.round(150.0, (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(100.0, Const.round(150.0, (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(200.0, Const.round(150.0, (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(100.0, Const.round(150.0, (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(200.0, Const.round(150.0, (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(100.0, Const.round(150.0, (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(200.0, Const.round(150.0, (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(200.0, Const.round(150.0, (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals(200.0, Const.round(170.0, (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(100.0, Const.round(170.0, (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(200.0, Const.round(170.0, (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(100.0, Const.round(170.0, (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(200.0, Const.round(170.0, (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(200.0, Const.round(170.0, (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(200.0, Const.round(170.0, (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(200.0, Const.round(170.0, (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals(200.0, Const.round(200.0, (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(200.0, Const.round(200.0, (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(200.0, Const.round(200.0, (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(200.0, Const.round(200.0, (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(200.0, Const.round(200.0, (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(200.0, Const.round(200.0, (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(200.0, Const.round(200.0, (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(200.0, Const.round(200.0, (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals(300.0, Const.round(220.0, (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(200.0, Const.round(220.0, (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(300.0, Const.round(220.0, (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(200.0, Const.round(220.0, (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(200.0, Const.round(220.0, (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(200.0, Const.round(220.0, (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(200.0, Const.round(220.0, (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(200.0, Const.round(220.0, (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals(300.0, Const.round(250.0, (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(200.0, Const.round(250.0, (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(300.0, Const.round(250.0, (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(200.0, Const.round(250.0, (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(300.0, Const.round(250.0, (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(200.0, Const.round(250.0, (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(200.0, Const.round(250.0, (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(300.0, Const.round(250.0, (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals(300.0, Const.round(270.0, (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(200.0, Const.round(270.0, (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(300.0, Const.round(270.0, (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(200.0, Const.round(270.0, (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(300.0, Const.round(270.0, (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(300.0, Const.round(270.0, (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(300.0, Const.round(270.0, (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(300.0, Const.round(270.0, (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals((-100.0), Const.round((-100.0), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-100.0), Const.round((-100.0), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-100.0), Const.round((-100.0), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-100.0), Const.round((-100.0), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-100.0), Const.round((-100.0), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-100.0), Const.round((-100.0), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-100.0), Const.round((-100.0), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-100.0), Const.round((-100.0), (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals((-200.0), Const.round((-120.0), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-100.0), Const.round((-120.0), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-100.0), Const.round((-120.0), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-200.0), Const.round((-120.0), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-100.0), Const.round((-120.0), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-100.0), Const.round((-120.0), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-100.0), Const.round((-120.0), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-100.0), Const.round((-120.0), (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals((-200.0), Const.round((-150.0), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-100.0), Const.round((-150.0), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-100.0), Const.round((-150.0), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-200.0), Const.round((-150.0), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-200.0), Const.round((-150.0), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-100.0), Const.round((-150.0), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-200.0), Const.round((-150.0), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-100.0), Const.round((-150.0), (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals((-200.0), Const.round((-170.0), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-100.0), Const.round((-170.0), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-100.0), Const.round((-170.0), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-200.0), Const.round((-170.0), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-200.0), Const.round((-170.0), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-200.0), Const.round((-170.0), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-200.0), Const.round((-170.0), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-200.0), Const.round((-170.0), (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals((-200.0), Const.round((-200.0), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-200.0), Const.round((-200.0), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-200.0), Const.round((-200.0), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-200.0), Const.round((-200.0), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-200.0), Const.round((-200.0), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-200.0), Const.round((-200.0), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-200.0), Const.round((-200.0), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-200.0), Const.round((-200.0), (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals((-300.0), Const.round((-220.0), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-200.0), Const.round((-220.0), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-200.0), Const.round((-220.0), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-300.0), Const.round((-220.0), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-200.0), Const.round((-220.0), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-200.0), Const.round((-220.0), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-200.0), Const.round((-220.0), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-200.0), Const.round((-220.0), (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals((-300.0), Const.round((-250.0), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-200.0), Const.round((-250.0), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-200.0), Const.round((-250.0), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-300.0), Const.round((-250.0), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-300.0), Const.round((-250.0), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-200.0), Const.round((-250.0), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-200.0), Const.round((-250.0), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-200.0), Const.round((-250.0), (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals((-300.0), Const.round((-270.0), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-200.0), Const.round((-270.0), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-200.0), Const.round((-270.0), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-300.0), Const.round((-270.0), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-300.0), Const.round((-270.0), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-300.0), Const.round((-270.0), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-300.0), Const.round((-270.0), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-300.0), Const.round((-270.0), (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals(Double.NaN, Const.round(Double.NaN, 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(Double.NEGATIVE_INFINITY, Const.round(Double.NEGATIVE_INFINITY, 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(Double.POSITIVE_INFINITY, Const.round(Double.POSITIVE_INFINITY, 0, BigDecimal.ROUND_UP));
    }

    @Test
    public void testRound_Long() {
        ConstTest.assertEquals(1L, Const.round(1L, 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(1L, Const.round(1L, 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(1L, Const.round(1L, 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(1L, Const.round(1L, 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(1L, Const.round(1L, 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(1L, Const.round(1L, 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(1L, Const.round(1L, 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(1L, Const.round(1L, 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals(2L, Const.round(2L, 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals(2L, Const.round(2L, 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(2L, Const.round(2L, 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(2L, Const.round(2L, 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(2L, Const.round(2L, 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(2L, Const.round(2L, 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(2L, Const.round(2L, 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(2L, Const.round(2L, 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals((-1L), Const.round((-1L), 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-1L), Const.round((-1L), 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-1L), Const.round((-1L), 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-1L), Const.round((-1L), 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-1L), Const.round((-1L), 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-1L), Const.round((-1L), 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-1L), Const.round((-1L), 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-1L), Const.round((-1L), 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals((-2L), Const.round((-2L), 0, BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-2L), Const.round((-2L), 0, BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-2L), Const.round((-2L), 0, BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-2L), Const.round((-2L), 0, BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-2L), Const.round((-2L), 0, BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-2L), Const.round((-2L), 0, BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-2L), Const.round((-2L), 0, BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-2L), Const.round((-2L), 0, ROUND_HALF_CEILING));
        ConstTest.assertEquals(100L, Const.round(100L, (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(100L, Const.round(100L, (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(100L, Const.round(100L, (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(100L, Const.round(100L, (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(100L, Const.round(100L, (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(100L, Const.round(100L, (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(100L, Const.round(100L, (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(100L, Const.round(100L, (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals(200L, Const.round(120L, (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(100L, Const.round(120L, (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(200L, Const.round(120L, (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(100L, Const.round(120L, (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(100L, Const.round(120L, (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(100L, Const.round(120L, (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(100L, Const.round(120L, (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(100L, Const.round(120L, (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals(200L, Const.round(150L, (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(100L, Const.round(150L, (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(200L, Const.round(150L, (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(100L, Const.round(150L, (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(200L, Const.round(150L, (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(100L, Const.round(150L, (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(200L, Const.round(150L, (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(200L, Const.round(150L, (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals(200L, Const.round(170L, (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(100L, Const.round(170L, (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(200L, Const.round(170L, (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(100L, Const.round(170L, (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(200L, Const.round(170L, (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(200L, Const.round(170L, (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(200L, Const.round(170L, (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(200L, Const.round(170L, (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals(200L, Const.round(200L, (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(200L, Const.round(200L, (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(200L, Const.round(200L, (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(200L, Const.round(200L, (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(200L, Const.round(200L, (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(200L, Const.round(200L, (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(200L, Const.round(200L, (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(200L, Const.round(200L, (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals(300L, Const.round(220L, (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(200L, Const.round(220L, (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(300L, Const.round(220L, (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(200L, Const.round(220L, (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(200L, Const.round(220L, (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(200L, Const.round(220L, (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(200L, Const.round(220L, (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(200L, Const.round(220L, (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals(300L, Const.round(250L, (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(200L, Const.round(250L, (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(300L, Const.round(250L, (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(200L, Const.round(250L, (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(300L, Const.round(250L, (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(200L, Const.round(250L, (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(200L, Const.round(250L, (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(300L, Const.round(250L, (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals(300L, Const.round(270L, (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals(200L, Const.round(270L, (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals(300L, Const.round(270L, (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals(200L, Const.round(270L, (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals(300L, Const.round(270L, (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals(300L, Const.round(270L, (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals(300L, Const.round(270L, (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals(300L, Const.round(270L, (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals((-100L), Const.round((-100L), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-100L), Const.round((-100L), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-100L), Const.round((-100L), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-100L), Const.round((-100L), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-100L), Const.round((-100L), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-100L), Const.round((-100L), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-100L), Const.round((-100L), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-100L), Const.round((-100L), (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals((-200L), Const.round((-120L), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-100L), Const.round((-120L), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-100L), Const.round((-120L), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-200L), Const.round((-120L), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-100L), Const.round((-120L), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-100L), Const.round((-120L), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-100L), Const.round((-120L), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-100L), Const.round((-120L), (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals((-200L), Const.round((-150L), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-100L), Const.round((-150L), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-100L), Const.round((-150L), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-200L), Const.round((-150L), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-200L), Const.round((-150L), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-100L), Const.round((-150L), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-200L), Const.round((-150L), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-100L), Const.round((-150L), (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals((-200L), Const.round((-170L), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-100L), Const.round((-170L), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-100L), Const.round((-170L), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-200L), Const.round((-170L), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-200L), Const.round((-170L), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-200L), Const.round((-170L), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-200L), Const.round((-170L), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-200L), Const.round((-170L), (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals((-200L), Const.round((-200L), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-200L), Const.round((-200L), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-200L), Const.round((-200L), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-200L), Const.round((-200L), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-200L), Const.round((-200L), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-200L), Const.round((-200L), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-200L), Const.round((-200L), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-200L), Const.round((-200L), (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals((-300L), Const.round((-220L), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-200L), Const.round((-220L), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-200L), Const.round((-220L), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-300L), Const.round((-220L), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-200L), Const.round((-220L), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-200L), Const.round((-220L), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-200L), Const.round((-220L), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-200L), Const.round((-220L), (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals((-300L), Const.round((-250L), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-200L), Const.round((-250L), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-200L), Const.round((-250L), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-300L), Const.round((-250L), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-300L), Const.round((-250L), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-200L), Const.round((-250L), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-200L), Const.round((-250L), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-200L), Const.round((-250L), (-2), ROUND_HALF_CEILING));
        ConstTest.assertEquals((-300L), Const.round((-270L), (-2), BigDecimal.ROUND_UP));
        ConstTest.assertEquals((-200L), Const.round((-270L), (-2), BigDecimal.ROUND_DOWN));
        ConstTest.assertEquals((-200L), Const.round((-270L), (-2), BigDecimal.ROUND_CEILING));
        ConstTest.assertEquals((-300L), Const.round((-270L), (-2), BigDecimal.ROUND_FLOOR));
        ConstTest.assertEquals((-300L), Const.round((-270L), (-2), BigDecimal.ROUND_HALF_UP));
        ConstTest.assertEquals((-300L), Const.round((-270L), (-2), BigDecimal.ROUND_HALF_DOWN));
        ConstTest.assertEquals((-300L), Const.round((-270L), (-2), BigDecimal.ROUND_HALF_EVEN));
        ConstTest.assertEquals((-300L), Const.round((-270L), (-2), ROUND_HALF_CEILING));
    }

    @Test
    public void testToInt() {
        ConstTest.assertEquals(123, Const.toInt("123", (-12)));
        ConstTest.assertEquals((-12), Const.toInt("123f", (-12)));
    }

    @Test
    public void testToLong() {
        ConstTest.assertEquals(1447252914241L, Const.toLong("1447252914241", (-12)));
        ConstTest.assertEquals((-1447252914241L), Const.toLong("1447252914241L", (-1447252914241L)));
    }

    @Test
    public void testToDouble() {
        Assert.assertEquals(123.45, Const.toDouble("123.45", (-12.34)), 1.0E-15);
        Assert.assertEquals((-12.34), Const.toDouble("123asd", (-12.34)), 1.0E-15);
    }

    @Test
    public void testRightPad() {
        final String s = "Pad me baby one more time";
        ConstTest.assertEquals("     ", Const.rightPad(((String) (null)), 5));
        ConstTest.assertEquals("Pad", Const.rightPad(s, 3));
        final StringBuffer sb = new StringBuffer(s);
        ConstTest.assertEquals((s + "   "), Const.rightPad(sb, 28));
        ConstTest.assertEquals("Pad me baby", Const.rightPad(sb, 11));
        final StringBuilder sb2 = new StringBuilder(s);
        ConstTest.assertEquals((s + "   "), Const.rightPad(sb2, 28));
        ConstTest.assertEquals("Pad me baby", Const.rightPad(sb2, 11));
    }

    @Test
    public void testReplace() {
        final String source = "A journey of a thousand miles never begins";
        ConstTest.assertEquals("A journey of a thousand miles begins with a single step", Const.replace(source, "never begins", "begins with a single step"));
        ConstTest.assertEquals(source, Const.replace(source, "evil", "good"));
        ConstTest.assertEquals("short", Const.replace("short", "long pattern", "replacement"));
        ConstTest.assertEquals("", Const.replace("", "anything", "something"));
        ConstTest.assertEquals(null, Const.replace(null, "test", "junk"));
        ConstTest.assertEquals(null, Const.replace("test", null, "junk"));
        ConstTest.assertEquals(null, Const.replace("test", "junk", null));
    }

    @Test
    public void testRepl() {
        String source = "A journey of a thousand miles never begins";
        StringBuffer sb = new StringBuffer(source);
        Const.repl(sb, "never begins", "begins with a single step");
        ConstTest.assertEquals("A journey of a thousand miles begins with a single step", sb.toString());
        sb = new StringBuffer(source);
        Const.repl(sb, "evil", "good");
        ConstTest.assertEquals(source, sb.toString());
        sb = new StringBuffer("short");
        Const.repl(sb, "long pattern", "replacement");
        ConstTest.assertEquals("short", sb.toString());
        sb = new StringBuffer("");
        Const.repl(sb, "anything", "something");
        ConstTest.assertEquals("", sb.toString());
        sb = new StringBuffer("Replace what looks like a regex '[a-z1-3*+]' with '$1'");
        Const.repl(sb, "[a-z1-3*+]", "$1");
        ConstTest.assertEquals("Replace what looks like a regex '$1' with '$1'", sb.toString());
        // StringBuilder version
        StringBuilder sb2 = new StringBuilder(source);
        Const.repl(sb2, "never begins", "begins with a single step");
        ConstTest.assertEquals("A journey of a thousand miles begins with a single step", sb2.toString());
        sb2 = new StringBuilder(source);
        Const.repl(sb2, "evil", "good");
        ConstTest.assertEquals(source, sb2.toString());
        sb2 = new StringBuilder("short");
        Const.repl(sb2, "long pattern", "replacement");
        ConstTest.assertEquals("short", sb2.toString());
        sb2 = new StringBuilder("");
        Const.repl(sb2, "anything", "something");
        ConstTest.assertEquals("", sb2.toString());
        sb2 = new StringBuilder("Replace what looks like a regex '[a-z1-3*+]' with '$1'");
        Const.repl(sb2, "[a-z1-3*+]", "$1");
        ConstTest.assertEquals("Replace what looks like a regex '$1' with '$1'", sb2.toString());
        sb2 = new StringBuilder("JUNK");
        Const.repl(sb2, null, "wibble");
        ConstTest.assertEquals("JUNK", sb2.toString());
        Const.repl(sb2, "JUNK", null);
    }

    @Test
    public void testGetOS() {
        final String key = "os.name";
        final String os = System.getProperty(key);
        System.setProperty(key, "BeOS");
        ConstTest.assertEquals("BeOS", Const.getOS());
        System.setProperty(key, os);
    }

    @Test
    public void testQuoteCharByOS() {
        ConstTest.assertEquals((SystemUtils.IS_OS_WINDOWS ? "\"" : "'"), Const.getQuoteCharByOS());
    }

    @Test
    public void testOptionallyQuoteStringByOS() {
        ConstTest.assertEquals((((Const.getQuoteCharByOS()) + "Quote me") + (Const.getQuoteCharByOS())), Const.optionallyQuoteStringByOS("Quote me"));
        ConstTest.assertEquals((((Const.getQuoteCharByOS()) + "Quote=me") + (Const.getQuoteCharByOS())), Const.optionallyQuoteStringByOS("Quote=me"));
        ConstTest.assertEquals("Quoteme", Const.optionallyQuoteStringByOS("Quoteme"));
        ConstTest.assertEquals((("Quote" + (Const.getQuoteCharByOS())) + "me"), Const.optionallyQuoteStringByOS((("Quote" + (Const.getQuoteCharByOS())) + "me")));
    }

    @Test
    public void testIsWindows() {
        ConstTest.assertEquals(IS_OS_WINDOWS, Const.isWindows());
    }

    @Test
    public void testIsLinux() {
        ConstTest.assertEquals(IS_OS_LINUX, Const.isLinux());
    }

    @Test
    public void testIsOSX() {
        ConstTest.assertEquals(IS_OS_MAC_OSX, Const.isOSX());
    }

    @Test
    public void testIsKDE() {
        final String kdeVersion = System.getenv("KDE_SESSION_VERSION");
        ConstTest.assertEquals(((kdeVersion != null) && (!(kdeVersion.isEmpty()))), Const.isKDE());
    }

    @Test
    public void testGetHostName() {
        Assert.assertFalse(Const.getHostname().isEmpty());
    }

    @Test
    public void testGetHostnameReal() {
        doWithModifiedSystemProperty("KETTLE_SYSTEM_HOSTNAME", "MyHost", new Runnable() {
            @Override
            public void run() {
                ConstTest.assertEquals("MyHost", Const.getHostnameReal());
            }
        });
    }

    @Test
    public void testReplEnv() {
        Assert.assertNull(Const.replEnv(((String) (null))));
        System.setProperty("testProp", "testValue");
        ConstTest.assertEquals("Value for testProp property is testValue.", Const.replEnv("Value for testProp property is %%testProp%%."));
        ConstTest.assertEquals("Value for testProp property is testValue.", Const.replEnv(new String[]{ "Value for testProp property is %%testProp%%." })[0]);
    }

    @Test
    public void testNullToEmpty() {
        ConstTest.assertEquals("", Const.nullToEmpty(null));
        ConstTest.assertEquals("value", Const.nullToEmpty("value"));
    }

    @Test
    public void testIndexOfString() {
        ConstTest.assertEquals((-1), Const.indexOfString(null, ((String[]) (null))));
        ConstTest.assertEquals((-1), Const.indexOfString(null, new String[]{  }));
        ConstTest.assertEquals(1, Const.indexOfString("bar", new String[]{ "foo", "bar" }));
        ConstTest.assertEquals((-1), Const.indexOfString("baz", new String[]{ "foo", "bar" }));
        ConstTest.assertEquals((-1), Const.indexOfString(null, ((List<String>) (null))));
        ConstTest.assertEquals(1, Const.indexOfString("bar", Arrays.asList("foo", "bar")));
        ConstTest.assertEquals((-1), Const.indexOfString("baz", Arrays.asList("foo", "bar")));
    }

    @Test
    public void testIndexsOfStrings() {
        Assert.assertArrayEquals(new int[]{ 2, 1, -1 }, Const.indexsOfStrings(new String[]{ "foo", "bar", "qux" }, new String[]{ "baz", "bar", "foo" }));
    }

    @Test
    public void testIndexsOfFoundStrings() {
        Assert.assertArrayEquals(new int[]{ 2, 1 }, Const.indexsOfFoundStrings(new String[]{ "qux", "foo", "bar" }, new String[]{ "baz", "bar", "foo" }));
    }

    @Test
    public void testGetDistinctStrings() {
        Assert.assertNull(Const.getDistinctStrings(null));
        Assert.assertTrue(((Const.getDistinctStrings(new String[]{  }).length) == 0));
        Assert.assertArrayEquals(new String[]{ "bar", "foo" }, Const.getDistinctStrings(new String[]{ "foo", "bar", "foo", "bar" }));
    }

    @Test
    public void testStackTracker() {
        Assert.assertTrue(Const.getStackTracker(new Exception()).contains(((((getClass().getName()) + ".testStackTracker(") + (getClass().getSimpleName())) + ".java:")));
    }

    @Test
    public void testGetCustomStackTrace() {
        Assert.assertTrue(Const.getCustomStackTrace(new Exception()).contains(((((getClass().getName()) + ".testGetCustomStackTrace(") + (getClass().getSimpleName())) + ".java:")));
    }

    @Test
    public void testCreateNewClassLoader() throws KettleException {
        ClassLoader cl = Const.createNewClassLoader();
        Assert.assertTrue(((cl instanceof URLClassLoader) && ((((URLClassLoader) (cl)).getURLs().length) == 0)));
    }

    @Test
    public void testCreateByteArray() {
        Assert.assertTrue(((Const.createByteArray(5).length) == 5));
    }

    @Test
    public void testCreateFilename() {
        ConstTest.assertEquals((("dir" + (FILE_SEPARATOR)) + "file__1.ext"), Const.createFilename(("dir" + (FILE_SEPARATOR)), "File\t~ 1", ".ext"));
        ConstTest.assertEquals((("dir" + (FILE_SEPARATOR)) + "file__1.ext"), Const.createFilename("dir", "File\t~ 1", ".ext"));
    }

    @Test
    public void testCreateName() {
        Assert.assertNull(Const.createName(null));
        ConstTest.assertEquals("test - trans", Const.createName((("transformations" + (FILE_SEPARATOR)) + "test\t~- trans.ktr")));
    }

    @Test
    public void testFilenameOnly() {
        Assert.assertNull(Const.filenameOnly(null));
        Assert.assertTrue(Const.filenameOnly("").isEmpty());
        ConstTest.assertEquals("file.txt", Const.filenameOnly((("dir" + (FILE_SEPARATOR)) + "file.txt")));
        ConstTest.assertEquals("file.txt", Const.filenameOnly("file.txt"));
    }

    @Test
    public void testGetDateFormats() {
        final String[] formats = Const.getDateFormats();
        Assert.assertTrue(((formats.length) > 0));
        for (String format : formats) {
            Assert.assertTrue(((format != null) && (!(format.isEmpty()))));
        }
    }

    @Test
    public void testGetNumberFormats() {
        final String[] formats = Const.getNumberFormats();
        Assert.assertTrue(((formats.length) > 0));
        for (String format : formats) {
            Assert.assertTrue(((format != null) && (!(format.isEmpty()))));
        }
    }

    @Test
    public void testGetConversionFormats() {
        final List<String> dateFormats = Arrays.asList(Const.getDateFormats());
        final List<String> numberFormats = Arrays.asList(Const.getNumberFormats());
        final List<String> conversionFormats = Arrays.asList(Const.getConversionFormats());
        ConstTest.assertEquals(((dateFormats.size()) + (numberFormats.size())), conversionFormats.size());
        Assert.assertTrue(conversionFormats.containsAll(dateFormats));
        Assert.assertTrue(conversionFormats.containsAll(numberFormats));
    }

    @Test
    public void testGetTransformationAndJobFilterNames() {
        List<String> filters = Arrays.asList(Const.getTransformationAndJobFilterNames());
        Assert.assertTrue(((filters.size()) == 5));
        for (String filter : filters) {
            Assert.assertFalse(filter.isEmpty());
        }
    }

    @Test
    public void testGetTransformationFilterNames() {
        List<String> filters = Arrays.asList(Const.getTransformationFilterNames());
        Assert.assertTrue(((filters.size()) == 3));
        for (String filter : filters) {
            Assert.assertFalse(filter.isEmpty());
        }
    }

    @Test
    public void testGetJobFilterNames() {
        List<String> filters = Arrays.asList(Const.getJobFilterNames());
        Assert.assertTrue(((filters.size()) == 3));
        for (String filter : filters) {
            Assert.assertFalse(filter.isEmpty());
        }
    }

    @Test
    public void testNanoTime() {
        Assert.assertTrue(String.valueOf(Const.nanoTime()).endsWith("000"));
    }

    @Test
    public void testTrimToType() {
        final String source = " trim me hard ";
        ConstTest.assertEquals("trim me hard", Const.trimToType(source, TRIM_TYPE_BOTH));
        ConstTest.assertEquals("trim me hard ", Const.trimToType(source, TRIM_TYPE_LEFT));
        ConstTest.assertEquals(" trim me hard", Const.trimToType(source, TRIM_TYPE_RIGHT));
        ConstTest.assertEquals(source, Const.trimToType(source, TRIM_TYPE_NONE));
    }

    @Test
    public void testSafeAppendDirectory() {
        final String expected = ("dir" + (FILE_SEPARATOR)) + "file";
        ConstTest.assertEquals(expected, Const.safeAppendDirectory("dir", "file"));
        ConstTest.assertEquals(expected, Const.safeAppendDirectory(("dir" + (FILE_SEPARATOR)), "file"));
        ConstTest.assertEquals(expected, Const.safeAppendDirectory("dir", ((FILE_SEPARATOR) + "file")));
        ConstTest.assertEquals(expected, Const.safeAppendDirectory(("dir" + (FILE_SEPARATOR)), ((FILE_SEPARATOR) + "file")));
    }

    @Test
    public void testGetEmptyPaddedStrings() {
        final String[] strings = Const.getEmptyPaddedStrings();
        for (int i = 0; i < 250; i++) {
            ConstTest.assertEquals(i, strings[i].length());
        }
    }

    @Test
    public void testGetPercentageFreeMemory() {
        Assert.assertTrue(((Const.getPercentageFreeMemory()) > 0));
    }

    @Test
    public void testRemoveDigits() {
        Assert.assertNull(Const.removeDigits(null));
        ConstTest.assertEquals("foobar", Const.removeDigits("123foo456bar789"));
    }

    @Test
    public void testGetDigitsOnly() {
        Assert.assertNull(Const.removeDigits(null));
        ConstTest.assertEquals("123456789", Const.getDigitsOnly("123foo456bar789"));
    }

    @Test
    public void testRemoveTimeFromDate() {
        final Date date = Const.removeTimeFromDate(new Date());
        ConstTest.assertEquals(0, date.getHours());
        ConstTest.assertEquals(0, date.getMinutes());
        ConstTest.assertEquals(0, date.getSeconds());
    }

    @Test
    public void testEscapeUnescapeXML() {
        final String xml = "<xml xmlns:test=\"http://test\">";
        final String escaped = "&lt;xml xmlns:test=&quot;http://test&quot;&gt;";
        Assert.assertNull(Const.escapeXML(null));
        Assert.assertNull(Const.unEscapeXml(null));
        ConstTest.assertEquals(escaped, Const.escapeXML(xml));
        ConstTest.assertEquals(xml, Const.unEscapeXml(escaped));
    }

    @Test
    public void testEscapeUnescapeHtml() {
        final String html = "<td>";
        final String escaped = "&lt;td&gt;";
        Assert.assertNull(Const.escapeHtml(null));
        Assert.assertNull(Const.unEscapeHtml(null));
        ConstTest.assertEquals(escaped, Const.escapeHtml(html));
        ConstTest.assertEquals(html, Const.unEscapeHtml(escaped));
    }

    @Test
    public void testEscapeSQL() {
        ConstTest.assertEquals("SELECT ''Let''s rock!'' FROM dual", Const.escapeSQL("SELECT 'Let's rock!' FROM dual"));
    }

    @Test
    public void testRemoveCRLF() {
        ConstTest.assertEquals("foo\tbar", Const.removeCRLF("foo\r\n\tbar"));
        ConstTest.assertEquals("", Const.removeCRLF(""));
        ConstTest.assertEquals("", Const.removeCRLF(null));
        ConstTest.assertEquals("", Const.removeCRLF("\r\n"));
        ConstTest.assertEquals("This is a test of the emergency broadcast system", Const.removeCRLF("This \r\nis \ra \ntest \rof \n\rthe \r\nemergency \rbroadcast \nsystem\r\n"));
    }

    @Test
    public void testRemoveCR() {
        ConstTest.assertEquals("foo\n\tbar", Const.removeCR("foo\r\n\tbar"));
        ConstTest.assertEquals("", Const.removeCR(""));
        ConstTest.assertEquals("", Const.removeCR(null));
        ConstTest.assertEquals("", Const.removeCR("\r"));
        ConstTest.assertEquals("\n\n", Const.removeCR("\n\r\n"));
        ConstTest.assertEquals("This \nis a \ntest of \nthe \nemergency broadcast \nsystem\n", Const.removeCR("This \r\nis \ra \ntest \rof \n\rthe \r\nemergency \rbroadcast \nsystem\r\n"));
    }

    @Test
    public void testRemoveLF() {
        ConstTest.assertEquals("foo\r\tbar", Const.removeLF("foo\r\n\tbar"));
        ConstTest.assertEquals("", Const.removeLF(""));
        ConstTest.assertEquals("", Const.removeLF(null));
        ConstTest.assertEquals("", Const.removeLF("\n"));
        ConstTest.assertEquals("\r\r", Const.removeLF("\r\n\r"));
        ConstTest.assertEquals("This \ris \ra test \rof \rthe \remergency \rbroadcast system\r", Const.removeLF("This \r\nis \ra \ntest \rof \n\rthe \r\nemergency \rbroadcast \nsystem\r\n"));
    }

    @Test
    public void testRemoveTAB() {
        ConstTest.assertEquals("foo\r\nbar", Const.removeTAB("foo\r\n\tbar"));
        ConstTest.assertEquals("", Const.removeTAB(""));
        ConstTest.assertEquals("", Const.removeTAB(null));
        ConstTest.assertEquals("", Const.removeTAB("\t"));
        ConstTest.assertEquals("\r", Const.removeTAB("\t\r\t"));
        ConstTest.assertEquals("Thisisatest", Const.removeTAB("\tThis\tis\ta\ttest"));
    }

    @Test
    public void testAddTimeToDate() throws Exception {
        final Date date = new Date(1447252914241L);
        Assert.assertNull(Const.addTimeToDate(null, null, null));
        ConstTest.assertEquals(date, Const.addTimeToDate(date, null, null));
        ConstTest.assertEquals(1447256637241L, Const.addTimeToDate(date, "01:02:03", "HH:mm:ss").getTime());
    }

    @Test
    public void testGetOccurenceString() {
        ConstTest.assertEquals(0, Const.getOccurenceString("", ""));
        ConstTest.assertEquals(0, Const.getOccurenceString("foo bar bazfoo", "cat"));
        ConstTest.assertEquals(2, Const.getOccurenceString("foo bar bazfoo", "foo"));
    }

    @Test
    public void testGetAvailableFontNames() {
        Assert.assertTrue(((Const.GetAvailableFontNames().length) > 0));
    }

    @Test
    public void testGetKettlePropertiesFileHeader() {
        Assert.assertFalse(Const.getKettlePropertiesFileHeader().isEmpty());
    }

    @Test
    public void testProtectXMLCDATA() {
        ConstTest.assertEquals(null, Const.protectXMLCDATA(null));
        ConstTest.assertEquals("", Const.protectXMLCDATA(""));
        ConstTest.assertEquals("<![CDATA[foo]]>", Const.protectXMLCDATA("foo"));
    }

    @Test
    public void testGetOcuranceString() {
        ConstTest.assertEquals(0, Const.getOcuranceString("", ""));
        ConstTest.assertEquals(0, Const.getOcuranceString("foo bar bazfoo", "cat"));
        ConstTest.assertEquals(2, Const.getOcuranceString("foo bar bazfoo", "foo"));
    }

    @Test
    public void testEscapeXml() {
        final String xml = "<xml xmlns:test=\"http://test\">";
        final String escaped = "&lt;xml xmlns:test=&quot;http://test&quot;&gt;";
        Assert.assertNull(Const.escapeXml(null));
        ConstTest.assertEquals(escaped, Const.escapeXml(xml));
    }

    @Test
    public void testLpad() {
        final String s = "pad me";
        ConstTest.assertEquals(s, Const.Lpad(s, "-", 0));
        ConstTest.assertEquals(s, Const.Lpad(s, "-", 3));
        ConstTest.assertEquals(("--" + s), Const.Lpad(s, "-", 8));
        // add in some edge cases
        ConstTest.assertEquals(s, Const.Lpad(s, null, 15));// No NPE

        ConstTest.assertEquals(s, Const.Lpad(s, "", 15));
        ConstTest.assertEquals(s, Const.Lpad(s, "*", 5));
        ConstTest.assertEquals(null, Const.Lpad(null, "*", 15));
        ConstTest.assertEquals("****Test", Const.Lpad("Test", "**********", 8));
        ConstTest.assertEquals("*Test", Const.Lpad("Test", "**", 5));
        ConstTest.assertEquals("****", Const.Lpad("", "*", 4));
    }

    @Test
    public void testRpad() {
        final String s = "pad me";
        ConstTest.assertEquals(s, Const.Rpad(s, "-", 0));
        ConstTest.assertEquals(s, Const.Rpad(s, "-", 3));
        ConstTest.assertEquals((s + "--"), Const.Rpad(s, "-", 8));
        // add in some edge cases
        ConstTest.assertEquals(s, Const.Rpad(s, null, 15));// No NPE

        ConstTest.assertEquals(s, Const.Rpad(s, "", 15));
        ConstTest.assertEquals(s, Const.Rpad(s, "*", 5));
        ConstTest.assertEquals(null, Const.Rpad(null, "*", 15));
        ConstTest.assertEquals("Test****", Const.Rpad("Test", "**********", 8));
        ConstTest.assertEquals("Test*", Const.Rpad("Test", "**", 5));
        ConstTest.assertEquals("****", Const.Rpad("", "*", 4));
    }

    @Test
    public void testClassIsOrExtends() {
        Assert.assertFalse(Const.classIsOrExtends(Object.class, Object.class));
        Assert.assertTrue(Const.classIsOrExtends(String.class, String.class));
        Assert.assertTrue(Const.classIsOrExtends(ArrayList.class, ArrayList.class));
    }

    @Test
    public void testReleaseType() {
        for (Const.ReleaseType type : ReleaseType.values()) {
            Assert.assertFalse(type.getMessage().isEmpty());
        }
    }
}

