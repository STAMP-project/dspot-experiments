/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Unit tests {@link org.apache.commons.lang3.StringUtils} - IsX methods
 */
public class StringUtilsIsTest {
    @Test
    public void testIsAlpha() {
        Assertions.assertFalse(StringUtils.isAlpha(null));
        Assertions.assertFalse(StringUtils.isAlpha(""));
        Assertions.assertFalse(StringUtils.isAlpha(" "));
        Assertions.assertTrue(StringUtils.isAlpha("a"));
        Assertions.assertTrue(StringUtils.isAlpha("A"));
        Assertions.assertTrue(StringUtils.isAlpha("kgKgKgKgkgkGkjkjlJlOKLgHdGdHgl"));
        Assertions.assertFalse(StringUtils.isAlpha("ham kso"));
        Assertions.assertFalse(StringUtils.isAlpha("1"));
        Assertions.assertFalse(StringUtils.isAlpha("hkHKHik6iUGHKJgU7tUJgKJGI87GIkug"));
        Assertions.assertFalse(StringUtils.isAlpha("_"));
        Assertions.assertFalse(StringUtils.isAlpha("hkHKHik*khbkuh"));
    }

    @Test
    public void testIsAlphanumeric() {
        Assertions.assertFalse(StringUtils.isAlphanumeric(null));
        Assertions.assertFalse(StringUtils.isAlphanumeric(""));
        Assertions.assertFalse(StringUtils.isAlphanumeric(" "));
        Assertions.assertTrue(StringUtils.isAlphanumeric("a"));
        Assertions.assertTrue(StringUtils.isAlphanumeric("A"));
        Assertions.assertTrue(StringUtils.isAlphanumeric("kgKgKgKgkgkGkjkjlJlOKLgHdGdHgl"));
        Assertions.assertFalse(StringUtils.isAlphanumeric("ham kso"));
        Assertions.assertTrue(StringUtils.isAlphanumeric("1"));
        Assertions.assertTrue(StringUtils.isAlphanumeric("hkHKHik6iUGHKJgU7tUJgKJGI87GIkug"));
        Assertions.assertFalse(StringUtils.isAlphanumeric("_"));
        Assertions.assertFalse(StringUtils.isAlphanumeric("hkHKHik*khbkuh"));
    }

    @Test
    public void testIsWhitespace() {
        Assertions.assertFalse(StringUtils.isWhitespace(null));
        Assertions.assertTrue(StringUtils.isWhitespace(""));
        Assertions.assertTrue(StringUtils.isWhitespace(" "));
        Assertions.assertTrue(StringUtils.isWhitespace("\t \n \t"));
        Assertions.assertFalse(StringUtils.isWhitespace("\t aa\n \t"));
        Assertions.assertTrue(StringUtils.isWhitespace(" "));
        Assertions.assertFalse(StringUtils.isWhitespace(" a "));
        Assertions.assertFalse(StringUtils.isWhitespace("a  "));
        Assertions.assertFalse(StringUtils.isWhitespace("  a"));
        Assertions.assertFalse(StringUtils.isWhitespace("aba"));
        Assertions.assertTrue(StringUtils.isWhitespace(StringUtilsTest.WHITESPACE));
        Assertions.assertFalse(StringUtils.isWhitespace(StringUtilsTest.NON_WHITESPACE));
    }

    @Test
    public void testIsAlphaspace() {
        Assertions.assertFalse(StringUtils.isAlphaSpace(null));
        Assertions.assertTrue(StringUtils.isAlphaSpace(""));
        Assertions.assertTrue(StringUtils.isAlphaSpace(" "));
        Assertions.assertTrue(StringUtils.isAlphaSpace("a"));
        Assertions.assertTrue(StringUtils.isAlphaSpace("A"));
        Assertions.assertTrue(StringUtils.isAlphaSpace("kgKgKgKgkgkGkjkjlJlOKLgHdGdHgl"));
        Assertions.assertTrue(StringUtils.isAlphaSpace("ham kso"));
        Assertions.assertFalse(StringUtils.isAlphaSpace("1"));
        Assertions.assertFalse(StringUtils.isAlphaSpace("hkHKHik6iUGHKJgU7tUJgKJGI87GIkug"));
        Assertions.assertFalse(StringUtils.isAlphaSpace("_"));
        Assertions.assertFalse(StringUtils.isAlphaSpace("hkHKHik*khbkuh"));
    }

    @Test
    public void testIsAlphanumericSpace() {
        Assertions.assertFalse(StringUtils.isAlphanumericSpace(null));
        Assertions.assertTrue(StringUtils.isAlphanumericSpace(""));
        Assertions.assertTrue(StringUtils.isAlphanumericSpace(" "));
        Assertions.assertTrue(StringUtils.isAlphanumericSpace("a"));
        Assertions.assertTrue(StringUtils.isAlphanumericSpace("A"));
        Assertions.assertTrue(StringUtils.isAlphanumericSpace("kgKgKgKgkgkGkjkjlJlOKLgHdGdHgl"));
        Assertions.assertTrue(StringUtils.isAlphanumericSpace("ham kso"));
        Assertions.assertTrue(StringUtils.isAlphanumericSpace("1"));
        Assertions.assertTrue(StringUtils.isAlphanumericSpace("hkHKHik6iUGHKJgU7tUJgKJGI87GIkug"));
        Assertions.assertFalse(StringUtils.isAlphanumericSpace("_"));
        Assertions.assertFalse(StringUtils.isAlphanumericSpace("hkHKHik*khbkuh"));
    }

    @Test
    public void testIsAsciiPrintable_String() {
        Assertions.assertFalse(StringUtils.isAsciiPrintable(null));
        Assertions.assertTrue(StringUtils.isAsciiPrintable(""));
        Assertions.assertTrue(StringUtils.isAsciiPrintable(" "));
        Assertions.assertTrue(StringUtils.isAsciiPrintable("a"));
        Assertions.assertTrue(StringUtils.isAsciiPrintable("A"));
        Assertions.assertTrue(StringUtils.isAsciiPrintable("1"));
        Assertions.assertTrue(StringUtils.isAsciiPrintable("Ceki"));
        Assertions.assertTrue(StringUtils.isAsciiPrintable("!ab2c~"));
        Assertions.assertTrue(StringUtils.isAsciiPrintable("1000"));
        Assertions.assertTrue(StringUtils.isAsciiPrintable("10 00"));
        Assertions.assertFalse(StringUtils.isAsciiPrintable("10\t00"));
        Assertions.assertTrue(StringUtils.isAsciiPrintable("10.00"));
        Assertions.assertTrue(StringUtils.isAsciiPrintable("10,00"));
        Assertions.assertTrue(StringUtils.isAsciiPrintable("!ab-c~"));
        Assertions.assertTrue(StringUtils.isAsciiPrintable("hkHK=Hik6i?UGH_KJgU7.tUJgKJ*GI87GI,kug"));
        Assertions.assertTrue(StringUtils.isAsciiPrintable(" "));
        Assertions.assertTrue(StringUtils.isAsciiPrintable("!"));
        Assertions.assertTrue(StringUtils.isAsciiPrintable("~"));
        Assertions.assertFalse(StringUtils.isAsciiPrintable("\u007f"));
        Assertions.assertTrue(StringUtils.isAsciiPrintable("G?lc?"));
        Assertions.assertTrue(StringUtils.isAsciiPrintable("=?iso-8859-1?Q?G=FClc=FC?="));
        Assertions.assertFalse(StringUtils.isAsciiPrintable("G\u00fclc\u00fc"));
    }

    @Test
    public void testIsNumeric() {
        Assertions.assertFalse(StringUtils.isNumeric(null));
        Assertions.assertFalse(StringUtils.isNumeric(""));
        Assertions.assertFalse(StringUtils.isNumeric(" "));
        Assertions.assertFalse(StringUtils.isNumeric("a"));
        Assertions.assertFalse(StringUtils.isNumeric("A"));
        Assertions.assertFalse(StringUtils.isNumeric("kgKgKgKgkgkGkjkjlJlOKLgHdGdHgl"));
        Assertions.assertFalse(StringUtils.isNumeric("ham kso"));
        Assertions.assertTrue(StringUtils.isNumeric("1"));
        Assertions.assertTrue(StringUtils.isNumeric("1000"));
        Assertions.assertTrue(StringUtils.isNumeric("\u0967\u0968\u0969"));
        Assertions.assertFalse(StringUtils.isNumeric("\u0967\u0968 \u0969"));
        Assertions.assertFalse(StringUtils.isNumeric("2.3"));
        Assertions.assertFalse(StringUtils.isNumeric("10 00"));
        Assertions.assertFalse(StringUtils.isNumeric("hkHKHik6iUGHKJgU7tUJgKJGI87GIkug"));
        Assertions.assertFalse(StringUtils.isNumeric("_"));
        Assertions.assertFalse(StringUtils.isNumeric("hkHKHik*khbkuh"));
        Assertions.assertFalse(StringUtils.isNumeric("+123"));
        Assertions.assertFalse(StringUtils.isNumeric("-123"));
    }

    @Test
    public void testIsNumericSpace() {
        Assertions.assertFalse(StringUtils.isNumericSpace(null));
        Assertions.assertTrue(StringUtils.isNumericSpace(""));
        Assertions.assertTrue(StringUtils.isNumericSpace(" "));
        Assertions.assertFalse(StringUtils.isNumericSpace("a"));
        Assertions.assertFalse(StringUtils.isNumericSpace("A"));
        Assertions.assertFalse(StringUtils.isNumericSpace("kgKgKgKgkgkGkjkjlJlOKLgHdGdHgl"));
        Assertions.assertFalse(StringUtils.isNumericSpace("ham kso"));
        Assertions.assertTrue(StringUtils.isNumericSpace("1"));
        Assertions.assertTrue(StringUtils.isNumericSpace("1000"));
        Assertions.assertFalse(StringUtils.isNumericSpace("2.3"));
        Assertions.assertTrue(StringUtils.isNumericSpace("10 00"));
        Assertions.assertTrue(StringUtils.isNumericSpace("\u0967\u0968\u0969"));
        Assertions.assertTrue(StringUtils.isNumericSpace("\u0967\u0968 \u0969"));
        Assertions.assertFalse(StringUtils.isNumericSpace("hkHKHik6iUGHKJgU7tUJgKJGI87GIkug"));
        Assertions.assertFalse(StringUtils.isNumericSpace("_"));
        Assertions.assertFalse(StringUtils.isNumericSpace("hkHKHik*khbkuh"));
    }
}

