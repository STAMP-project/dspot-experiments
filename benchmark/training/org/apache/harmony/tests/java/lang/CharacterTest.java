/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.harmony.tests.java.lang;


import java.util.Arrays;
import junit.framework.TestCase;


public class CharacterTest extends TestCase {
    // TODO(tball): uncomment tests of greater than 64k code points when iOS supports them.
    public void test_isValidCodePointI() {
        TestCase.assertFalse(Character.isValidCodePoint((-1)));
        TestCase.assertTrue(Character.isValidCodePoint(0));
        TestCase.assertTrue(Character.isValidCodePoint(1));
        TestCase.assertFalse(Character.isValidCodePoint(Integer.MAX_VALUE));
        for (int c = '\u0000'; c <= 1114111; c++) {
            TestCase.assertTrue(Character.isValidCodePoint(c));
        }
        TestCase.assertFalse(Character.isValidCodePoint((1114111 + 1)));
    }

    public void test_isSupplementaryCodePointI() {
        TestCase.assertFalse(Character.isSupplementaryCodePoint((-1)));
        for (int c = '\u0000'; c <= '\uffff'; c++) {
            TestCase.assertFalse(Character.isSupplementaryCodePoint(c));
        }
        for (int c = 65535 + 1; c <= 1114111; c++) {
            TestCase.assertTrue(Character.isSupplementaryCodePoint(c));
        }
        TestCase.assertFalse(Character.isSupplementaryCodePoint((1114111 + 1)));
    }

    public void test_isHighSurrogateC() {
        // (\uD800-\uDBFF)
        TestCase.assertFalse(Character.isHighSurrogate(((char) ('\ud800' - 1))));
        for (int c = '\ud800'; c <= '\udbff'; c++) {
            TestCase.assertTrue(Character.isHighSurrogate(((char) (c))));
        }
        TestCase.assertFalse(Character.isHighSurrogate(((char) ('\udbff' + 1))));
        TestCase.assertFalse(Character.isHighSurrogate('\uffff'));
    }

    public void test_isLowSurrogateC() {
        // (\uDC00-\uDFFF)
        TestCase.assertFalse(Character.isLowSurrogate(((char) ('\udc00' - 1))));
        for (int c = '\udc00'; c <= '\udfff'; c++) {
            TestCase.assertTrue(Character.isLowSurrogate(((char) (c))));
        }
        TestCase.assertFalse(Character.isLowSurrogate(((char) ('\udfff' + 1))));
    }

    public void test_isSurrogatePairCC() {
        TestCase.assertFalse(Character.isSurrogatePair('\u0000', '\u0000'));
        TestCase.assertFalse(Character.isSurrogatePair('\u0000', '\udc00'));
        TestCase.assertTrue(Character.isSurrogatePair('\ud800', '\udc00'));
        TestCase.assertTrue(Character.isSurrogatePair('\ud800', '\udfff'));
        TestCase.assertTrue(Character.isSurrogatePair('\udbff', '\udfff'));
        TestCase.assertFalse(Character.isSurrogatePair('\udbff', '\uf000'));
    }

    public void test_charCountI() {
        for (int c = '\u0000'; c <= '\uffff'; c++) {
            TestCase.assertEquals(1, Character.charCount(c));
        }
        for (int c = 65535 + 1; c <= 1114111; c++) {
            TestCase.assertEquals(2, Character.charCount(c));
        }
        // invalid code points work in this method
        TestCase.assertEquals(2, Character.charCount(Integer.MAX_VALUE));
    }

    public void test_toCodePointCC() {
        int result = Character.toCodePoint('\ud800', '\udc00');
        TestCase.assertEquals(65536, result);
        result = Character.toCodePoint('\ud800', '\udc01');
        TestCase.assertEquals(65537, result);
        result = Character.toCodePoint('\ud801', '\udc01');
        TestCase.assertEquals(66561, result);
        result = Character.toCodePoint('\udbff', '\udfff');
        TestCase.assertEquals(1114111, result);
    }

    public void test_codePointAt$CI() {
        TestCase.assertEquals('a', Character.codePointAt("abc".toCharArray(), 0));
        TestCase.assertEquals('b', Character.codePointAt("abc".toCharArray(), 1));
        TestCase.assertEquals('c', Character.codePointAt("abc".toCharArray(), 2));
        TestCase.assertEquals(65536, Character.codePointAt("\ud800\udc00".toCharArray(), 0));
        TestCase.assertEquals('\udc00', Character.codePointAt("\ud800\udc00".toCharArray(), 1));
        try {
            Character.codePointAt(((char[]) (null)), 0);
            TestCase.fail("No NPE.");
        } catch (NullPointerException e) {
        }
        try {
            Character.codePointAt("abc".toCharArray(), (-1));
            TestCase.fail("No IOOBE, negative index.");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            Character.codePointAt("abc".toCharArray(), 4);
            TestCase.fail("No IOOBE, index too large.");
        } catch (IndexOutOfBoundsException e) {
        }
    }

    public void test_codePointAt$CII() {
        TestCase.assertEquals('a', Character.codePointAt("abc".toCharArray(), 0, 3));
        TestCase.assertEquals('b', Character.codePointAt("abc".toCharArray(), 1, 3));
        TestCase.assertEquals('c', Character.codePointAt("abc".toCharArray(), 2, 3));
        TestCase.assertEquals(65536, Character.codePointAt("\ud800\udc00".toCharArray(), 0, 2));
        TestCase.assertEquals('\udc00', Character.codePointAt("\ud800\udc00".toCharArray(), 1, 2));
        TestCase.assertEquals('\ud800', Character.codePointAt("\ud800\udc00".toCharArray(), 0, 1));
        try {
            Character.codePointAt(null, 0, 1);
            TestCase.fail("No NPE.");
        } catch (NullPointerException e) {
        }
        try {
            Character.codePointAt("abc".toCharArray(), (-1), 3);
            TestCase.fail("No IOOBE, negative index.");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            Character.codePointAt("abc".toCharArray(), 4, 3);
            TestCase.fail("No IOOBE, index too large.");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            Character.codePointAt("abc".toCharArray(), 2, 1);
            TestCase.fail("No IOOBE, index larger than limit.");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            Character.codePointAt("abc".toCharArray(), 2, (-1));
            TestCase.fail("No IOOBE, limit is negative.");
        } catch (IndexOutOfBoundsException e) {
        }
    }

    public void test_codePointBefore$CI() {
        TestCase.assertEquals('a', Character.codePointBefore("abc".toCharArray(), 1));
        TestCase.assertEquals('b', Character.codePointBefore("abc".toCharArray(), 2));
        TestCase.assertEquals('c', Character.codePointBefore("abc".toCharArray(), 3));
        TestCase.assertEquals(65536, Character.codePointBefore("\ud800\udc00".toCharArray(), 2));
        TestCase.assertEquals('\ud800', Character.codePointBefore("\ud800\udc00".toCharArray(), 1));
        try {
            Character.codePointBefore(((char[]) (null)), 0);
            TestCase.fail("No NPE.");
        } catch (NullPointerException e) {
        }
        try {
            Character.codePointBefore("abc".toCharArray(), (-1));
            TestCase.fail("No IOOBE, negative index.");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            Character.codePointBefore("abc".toCharArray(), 4);
            TestCase.fail("No IOOBE, index too large.");
        } catch (IndexOutOfBoundsException e) {
        }
    }

    public void test_codePointBefore$CII() {
        TestCase.assertEquals('a', Character.codePointBefore("abc".toCharArray(), 1, 0));
        TestCase.assertEquals('b', Character.codePointBefore("abc".toCharArray(), 2, 0));
        TestCase.assertEquals('c', Character.codePointBefore("abc".toCharArray(), 3, 0));
        TestCase.assertEquals(65536, Character.codePointBefore("\ud800\udc00".toCharArray(), 2, 0));
        TestCase.assertEquals('\udc00', Character.codePointBefore("\ud800\udc00".toCharArray(), 2, 1));
        TestCase.assertEquals('\ud800', Character.codePointBefore("\ud800\udc00".toCharArray(), 1, 0));
        try {
            Character.codePointBefore(null, 1, 0);
            TestCase.fail("No NPE.");
        } catch (NullPointerException e) {
        }
        try {
            Character.codePointBefore("abc".toCharArray(), 0, 1);
            TestCase.fail("No IOOBE, index less than start.");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            Character.codePointBefore("abc".toCharArray(), 4, 0);
            TestCase.fail("No IOOBE, index larger than length.");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            Character.codePointBefore("abc".toCharArray(), 2, (-1));
            TestCase.fail("No IOOBE, start is negative.");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            Character.codePointBefore("abc".toCharArray(), 2, 4);
            TestCase.fail("No IOOBE, start larger than length.");
        } catch (IndexOutOfBoundsException e) {
        }
    }

    public void test_toCharsI$CI() {
        char[] dst = new char[2];
        int result = Character.toChars(65536, dst, 0);
        TestCase.assertEquals(2, result);
        TestCase.assertTrue(Arrays.equals(new char[]{ '\ud800', '\udc00' }, dst));
        result = Character.toChars(65537, dst, 0);
        TestCase.assertEquals(2, result);
        TestCase.assertTrue(Arrays.equals(new char[]{ '\ud800', '\udc01' }, dst));
        result = Character.toChars(66561, dst, 0);
        TestCase.assertEquals(2, result);
        TestCase.assertTrue(Arrays.equals(new char[]{ '\ud801', '\udc01' }, dst));
        result = Character.toChars(1114111, dst, 0);
        TestCase.assertEquals(2, result);
        TestCase.assertTrue(Arrays.equals(new char[]{ '\udbff', '\udfff' }, dst));
        try {
            Character.toChars(Integer.MAX_VALUE, new char[2], 0);
            TestCase.fail("No IAE, invalid code point.");
        } catch (IllegalArgumentException e) {
        }
        try {
            Character.toChars('a', null, 0);
            TestCase.fail("No NPE, null char[].");
        } catch (NullPointerException e) {
        }
        try {
            Character.toChars('a', new char[1], (-1));
            TestCase.fail("No IOOBE, negative index.");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            Character.toChars('a', new char[1], 1);
            TestCase.fail("No IOOBE, index equal to length.");
        } catch (IndexOutOfBoundsException e) {
        }
    }

    public void test_toCharsI() {
        TestCase.assertTrue(Arrays.equals(new char[]{ '\ud800', '\udc00' }, Character.toChars(65536)));
        TestCase.assertTrue(Arrays.equals(new char[]{ '\ud800', '\udc01' }, Character.toChars(65537)));
        TestCase.assertTrue(Arrays.equals(new char[]{ '\ud801', '\udc01' }, Character.toChars(66561)));
        TestCase.assertTrue(Arrays.equals(new char[]{ '\udbff', '\udfff' }, Character.toChars(1114111)));
        try {
            Character.toChars(Integer.MAX_VALUE);
            TestCase.fail("No IAE, invalid code point.");
        } catch (IllegalArgumentException e) {
        }
    }

    public void test_codePointCountLjava_lang_CharSequenceII() {
        TestCase.assertEquals(1, Character.codePointCount("\ud800\udc00", 0, 2));
        TestCase.assertEquals(1, Character.codePointCount("\ud800\udc01", 0, 2));
        TestCase.assertEquals(1, Character.codePointCount("\ud801\udc01", 0, 2));
        TestCase.assertEquals(1, Character.codePointCount("\udbff\udfff", 0, 2));
        TestCase.assertEquals(3, Character.codePointCount("a\ud800\udc00b", 0, 4));
        TestCase.assertEquals(4, Character.codePointCount("a\ud800\udc00b\ud800", 0, 5));
        try {
            Character.codePointCount(((CharSequence) (null)), 0, 1);
            TestCase.fail("No NPE, null char sequence.");
        } catch (NullPointerException e) {
        }
        try {
            Character.codePointCount("abc", (-1), 1);
            TestCase.fail("No IOOBE, negative start.");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            Character.codePointCount("abc", 0, 4);
            TestCase.fail("No IOOBE, end greater than length.");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            Character.codePointCount("abc", 2, 1);
            TestCase.fail("No IOOBE, end greater than start.");
        } catch (IndexOutOfBoundsException e) {
        }
    }

    public void test_offsetByCodePointsLjava_lang_CharSequenceII() {
        int result = Character.offsetByCodePoints("a\ud800\udc00b", 0, 2);
        TestCase.assertEquals(3, result);
        result = Character.offsetByCodePoints("abcd", 3, (-1));
        TestCase.assertEquals(2, result);
        result = Character.offsetByCodePoints("a\ud800\udc00b", 0, 3);
        TestCase.assertEquals(4, result);
        result = Character.offsetByCodePoints("a\ud800\udc00b", 3, (-1));
        TestCase.assertEquals(1, result);
        result = Character.offsetByCodePoints("a\ud800\udc00b", 3, 0);
        TestCase.assertEquals(3, result);
        result = Character.offsetByCodePoints("\ud800\udc00bc", 3, 0);
        TestCase.assertEquals(3, result);
        result = Character.offsetByCodePoints("a\udc00bc", 3, (-1));
        TestCase.assertEquals(2, result);
        result = Character.offsetByCodePoints("a\ud800bc", 3, (-1));
        TestCase.assertEquals(2, result);
        try {
            Character.offsetByCodePoints(null, 0, 1);
            TestCase.fail();
        } catch (NullPointerException e) {
        }
        try {
            Character.offsetByCodePoints("abc", (-1), 1);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            Character.offsetByCodePoints("abc", 4, 1);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            Character.offsetByCodePoints("abc", 1, 3);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            Character.offsetByCodePoints("abc", 1, (-2));
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
        }
    }

    public void test_offsetByCodePoints$CIIII() {
        int result = Character.offsetByCodePoints("a\ud800\udc00b".toCharArray(), 0, 4, 0, 2);
        TestCase.assertEquals(3, result);
        result = Character.offsetByCodePoints("a\ud800\udc00b".toCharArray(), 0, 4, 0, 3);
        TestCase.assertEquals(4, result);
        result = Character.offsetByCodePoints("a\ud800\udc00b\ud800c".toCharArray(), 0, 5, 0, 3);
        TestCase.assertEquals(4, result);
        result = Character.offsetByCodePoints("abcd".toCharArray(), 0, 4, 3, (-1));
        TestCase.assertEquals(2, result);
        result = Character.offsetByCodePoints("abcd".toCharArray(), 1, 2, 3, (-2));
        TestCase.assertEquals(1, result);
        result = Character.offsetByCodePoints("a\ud800\udc00b".toCharArray(), 0, 4, 3, (-1));
        TestCase.assertEquals(1, result);
        result = Character.offsetByCodePoints("a\ud800\udc00b".toCharArray(), 0, 2, 2, (-1));
        TestCase.assertEquals(1, result);
        result = Character.offsetByCodePoints("a\ud800\udc00b".toCharArray(), 0, 4, 3, 0);
        TestCase.assertEquals(3, result);
        result = Character.offsetByCodePoints("\ud800\udc00bc".toCharArray(), 0, 4, 3, 0);
        TestCase.assertEquals(3, result);
        result = Character.offsetByCodePoints("a\udc00bc".toCharArray(), 0, 4, 3, (-1));
        TestCase.assertEquals(2, result);
        result = Character.offsetByCodePoints("a\ud800bc".toCharArray(), 0, 4, 3, (-1));
        TestCase.assertEquals(2, result);
        try {
            Character.offsetByCodePoints(null, 0, 4, 1, 1);
            TestCase.fail();
        } catch (NullPointerException e) {
        }
        try {
            Character.offsetByCodePoints("abcd".toCharArray(), (-1), 4, 1, 1);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            Character.offsetByCodePoints("abcd".toCharArray(), 0, (-1), 1, 1);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            Character.offsetByCodePoints("abcd".toCharArray(), 2, 4, 1, 1);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            Character.offsetByCodePoints("abcd".toCharArray(), 1, 3, 0, 1);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            Character.offsetByCodePoints("abcd".toCharArray(), 1, 1, 3, 1);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            Character.offsetByCodePoints("abc".toCharArray(), 0, 3, 1, 3);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            Character.offsetByCodePoints("abc".toCharArray(), 0, 2, 1, 2);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            Character.offsetByCodePoints("abc".toCharArray(), 1, 3, 1, (-2));
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
        }
    }

    /**
     * java.lang.Character#compareTo(Character)
     */
    public void test_compareToLjava_lang_Byte() {
        final Character min = new Character(Character.MIN_VALUE);
        final Character mid = new Character(((char) ((Character.MAX_VALUE) / 2)));
        final Character max = new Character(Character.MAX_VALUE);
        TestCase.assertTrue(((max.compareTo(max)) == 0));
        TestCase.assertTrue(((min.compareTo(min)) == 0));
        TestCase.assertTrue(((mid.compareTo(mid)) == 0));
        TestCase.assertTrue(((max.compareTo(mid)) > 0));
        TestCase.assertTrue(((max.compareTo(min)) > 0));
        TestCase.assertTrue(((mid.compareTo(max)) < 0));
        TestCase.assertTrue(((mid.compareTo(min)) > 0));
        TestCase.assertTrue(((min.compareTo(mid)) < 0));
        TestCase.assertTrue(((min.compareTo(max)) < 0));
        try {
            min.compareTo(null);
            TestCase.fail("No NPE");
        } catch (NullPointerException e) {
        }
    }

    public void test_codePointAt_Invalid() {
        try {
            Character.codePointAt(null, 6, 4);
            TestCase.fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            Character.codePointAt(null, 4, 6);
            TestCase.fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            Character.codePointAt(null, 0, 0);
            TestCase.fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    /**
     * java.lang.Character#Character(char)
     */
    public void test_ConstructorC() {
        TestCase.assertEquals("Constructor failed", 'T', new Character('T').charValue());
    }

    /**
     * java.lang.Character#charValue()
     */
    public void test_charValue() {
        TestCase.assertEquals("Incorrect char value returned", 'T', new Character('T').charValue());
    }

    /**
     * java.lang.Character#compareTo(java.lang.Character)
     */
    public void test_compareToLjava_lang_Character() {
        Character c = new Character('c');
        Character x = new Character('c');
        Character y = new Character('b');
        Character z = new Character('d');
        TestCase.assertEquals("Returned false for same Character", 0, c.compareTo(c));
        TestCase.assertEquals("Returned false for identical Character", 0, c.compareTo(x));
        TestCase.assertTrue("Returned other than less than for lesser char", ((c.compareTo(y)) > 0));
        TestCase.assertTrue("Returned other than greater than for greater char", ((c.compareTo(z)) < 0));
    }

    /**
     * java.lang.Character#digit(char, int)
     */
    public void test_digitCI() {
        TestCase.assertEquals("Returned incorrect digit", 1, Character.digit('1', 10));
        TestCase.assertEquals("Returned incorrect digit", 15, Character.digit('F', 16));
    }

    /**
     * java.lang.Character#digit(int, int)
     */
    public void test_digit_II() {
        TestCase.assertEquals(1, Character.digit(((int) ('1')), 10));
        TestCase.assertEquals(15, Character.digit(((int) ('F')), 16));
        TestCase.assertEquals((-1), Character.digit(0, 37));
        TestCase.assertEquals((-1), Character.digit(69, 10));
        TestCase.assertEquals(10, Character.digit(65, 20));
        TestCase.assertEquals(10, Character.digit(97, 20));
        TestCase.assertEquals((-1), Character.digit(1114112, 20));
    }

    /**
     * java.lang.Character#equals(java.lang.Object)
     */
    public void test_equalsLjava_lang_Object() {
        // Test for method boolean java.lang.Character.equals(java.lang.Object)
        TestCase.assertTrue("Equality test failed", new Character('A').equals(new Character('A')));
        TestCase.assertFalse("Equality test failed", new Character('A').equals(new Character('a')));
    }

    /**
     * java.lang.Character#forDigit(int, int)
     */
    public void test_forDigitII() {
        char[] hexChars = new char[]{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        for (int i = 0; i < (hexChars.length); i++) {
            TestCase.assertTrue(("Returned incorrect char for " + (Integer.toString(i))), ((Character.forDigit(i, hexChars.length)) == (hexChars[i])));
        }
        char[] decimalChars = new char[]{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
        for (int i = 0; i < (decimalChars.length); i++) {
            TestCase.assertTrue(("Returned incorrect char for " + (Integer.toString(i))), ((Character.forDigit(i, decimalChars.length)) == (decimalChars[i])));
        }
    }

    /**
     * java.lang.Character#getNumericValue(char)
     */
    public void test_getNumericValueC() {
        TestCase.assertEquals("Returned incorrect numeric value 1", 1, Character.getNumericValue('1'));
        TestCase.assertEquals("Returned incorrect numeric value 2", 15, Character.getNumericValue('F'));
        TestCase.assertEquals("Returned incorrect numeric value 3", (-1), Character.getNumericValue('\u221e'));
        // assertEquals("Returned incorrect numeric value 4", -2, Character
        // .getNumericValue('\u00be'));
        // assertEquals("Returned incorrect numeric value 5", 10000, Character
        // .getNumericValue('\u2182'));
        // assertEquals("Returned incorrect numeric value 6", 2, Character
        // .getNumericValue('\uff12'));
    }

    /**
     * java.lang.Character#getNumericValue(int)
     */
    public void test_getNumericValue_I() {
        TestCase.assertEquals(1, Character.getNumericValue(((int) ('1'))));
        TestCase.assertEquals(15, Character.getNumericValue(((int) ('F'))));
        TestCase.assertEquals((-1), Character.getNumericValue(((int) ('\u221e'))));
        // assertEquals(-2, Character.getNumericValue((int) '\u00be'));
        // assertEquals(10000, Character.getNumericValue((int) '\u2182'));
        // assertEquals(2, Character.getNumericValue((int) '\uff12'));
        TestCase.assertEquals((-1), Character.getNumericValue(65535));
        // assertEquals(0, Character.getNumericValue(0x1D7CE));
        // assertEquals(0, Character.getNumericValue(0x1D7D8));
        // assertEquals(-1, Character.getNumericValue(0x2F800));
        // assertEquals(-1, Character.getNumericValue(0x10FFFD));
        // assertEquals(-1, Character.getNumericValue(0x110000));
        // assertEquals(50, Character.getNumericValue(0x216C));
        TestCase.assertEquals(10, Character.getNumericValue(65));
        TestCase.assertEquals(35, Character.getNumericValue(90));
        TestCase.assertEquals(10, Character.getNumericValue(97));
        TestCase.assertEquals(35, Character.getNumericValue(122));
        TestCase.assertEquals(10, Character.getNumericValue(65313));
        // FIXME depends on ICU4J
        // assertEquals(35, Character.getNumericValue(0xFF3A));
        TestCase.assertEquals(10, Character.getNumericValue(65345));
        TestCase.assertEquals(35, Character.getNumericValue(65370));
    }

    /**
     * java.lang.Character#getType(char)
     */
    public void test_getTypeC() {
        TestCase.assertTrue("Returned incorrect type for: \n", ((Character.getType('\n')) == (Character.CONTROL)));
        TestCase.assertTrue("Returned incorrect type for: 1", ((Character.getType('1')) == (Character.DECIMAL_DIGIT_NUMBER)));
        TestCase.assertTrue("Returned incorrect type for: ' '", ((Character.getType(' ')) == (Character.SPACE_SEPARATOR)));
        TestCase.assertTrue("Returned incorrect type for: a", ((Character.getType('a')) == (Character.LOWERCASE_LETTER)));
        TestCase.assertTrue("Returned incorrect type for: A", ((Character.getType('A')) == (Character.UPPERCASE_LETTER)));
        TestCase.assertTrue("Returned incorrect type for: <", ((Character.getType('<')) == (Character.MATH_SYMBOL)));
        TestCase.assertTrue("Returned incorrect type for: ;", ((Character.getType(';')) == (Character.OTHER_PUNCTUATION)));
        TestCase.assertTrue("Returned incorrect type for: _", ((Character.getType('_')) == (Character.CONNECTOR_PUNCTUATION)));
        TestCase.assertTrue("Returned incorrect type for: $", ((Character.getType('$')) == (Character.CURRENCY_SYMBOL)));
        TestCase.assertTrue("Returned incorrect type for: \u2029", ((Character.getType('\u2029')) == (Character.PARAGRAPH_SEPARATOR)));
        TestCase.assertEquals("Wrong constant for FORMAT", 16, Character.FORMAT);
        TestCase.assertEquals("Wrong constant for PRIVATE_USE", 18, Character.PRIVATE_USE);
    }

    /**
     * java.lang.Character#getType(int)
     */
    public void test_getType_I() {
        TestCase.assertTrue(((Character.getType(((int) ('\n')))) == (Character.CONTROL)));
        TestCase.assertTrue(((Character.getType(((int) ('1')))) == (Character.DECIMAL_DIGIT_NUMBER)));
        TestCase.assertTrue(((Character.getType(((int) (' ')))) == (Character.SPACE_SEPARATOR)));
        TestCase.assertTrue(((Character.getType(((int) ('a')))) == (Character.LOWERCASE_LETTER)));
        TestCase.assertTrue(((Character.getType(((int) ('A')))) == (Character.UPPERCASE_LETTER)));
        TestCase.assertTrue(((Character.getType(((int) ('<')))) == (Character.MATH_SYMBOL)));
        TestCase.assertTrue(((Character.getType(((int) (';')))) == (Character.OTHER_PUNCTUATION)));
        TestCase.assertTrue(((Character.getType(((int) ('_')))) == (Character.CONNECTOR_PUNCTUATION)));
        TestCase.assertTrue(((Character.getType(((int) ('$')))) == (Character.CURRENCY_SYMBOL)));
        TestCase.assertTrue(((Character.getType(((int) ('\u2029')))) == (Character.PARAGRAPH_SEPARATOR)));
        TestCase.assertTrue(((Character.getType(40959)) == (Character.UNASSIGNED)));
        TestCase.assertTrue(((Character.getType(196608)) == (Character.UNASSIGNED)));
        TestCase.assertTrue(((Character.getType(1114112)) == (Character.UNASSIGNED)));
        TestCase.assertTrue(((Character.getType(65)) == (Character.UPPERCASE_LETTER)));
        TestCase.assertTrue(((Character.getType(66560)) == (Character.UPPERCASE_LETTER)));
        TestCase.assertTrue(((Character.getType(97)) == (Character.LOWERCASE_LETTER)));
        TestCase.assertTrue(((Character.getType(66600)) == (Character.LOWERCASE_LETTER)));
        TestCase.assertTrue(((Character.getType(453)) == (Character.TITLECASE_LETTER)));
        TestCase.assertTrue(((Character.getType(8188)) == (Character.TITLECASE_LETTER)));
        TestCase.assertTrue(((Character.getType(688)) == (Character.MODIFIER_LETTER)));
        TestCase.assertTrue(((Character.getType(65439)) == (Character.MODIFIER_LETTER)));
        TestCase.assertTrue(((Character.getType(443)) == (Character.OTHER_LETTER)));
        TestCase.assertTrue(((Character.getType(194696)) == (Character.OTHER_LETTER)));
        TestCase.assertTrue(((Character.getType(3970)) == (Character.NON_SPACING_MARK)));
        TestCase.assertTrue(((Character.getType(119168)) == (Character.NON_SPACING_MARK)));
        TestCase.assertTrue(((Character.getType(1160)) == (Character.ENCLOSING_MARK)));
        TestCase.assertTrue(((Character.getType(8414)) == (Character.ENCLOSING_MARK)));
        TestCase.assertTrue(((Character.getType(6456)) == (Character.COMBINING_SPACING_MARK)));
        TestCase.assertTrue(((Character.getType(119141)) == (Character.COMBINING_SPACING_MARK)));
        TestCase.assertTrue(((Character.getType(6477)) == (Character.DECIMAL_DIGIT_NUMBER)));
        TestCase.assertTrue(((Character.getType(120782)) == (Character.DECIMAL_DIGIT_NUMBER)));
        TestCase.assertTrue(((Character.getType(8544)) == (Character.LETTER_NUMBER)));
        TestCase.assertTrue(((Character.getType(66378)) == (Character.LETTER_NUMBER)));
        TestCase.assertTrue(((Character.getType(178)) == (Character.OTHER_NUMBER)));
        TestCase.assertTrue(((Character.getType(65824)) == (Character.OTHER_NUMBER)));
        TestCase.assertTrue(((Character.getType(32)) == (Character.SPACE_SEPARATOR)));
        TestCase.assertTrue(((Character.getType(12288)) == (Character.SPACE_SEPARATOR)));
        TestCase.assertTrue(((Character.getType(8232)) == (Character.LINE_SEPARATOR)));
        TestCase.assertTrue(((Character.getType(8233)) == (Character.PARAGRAPH_SEPARATOR)));
        TestCase.assertTrue(((Character.getType(0)) == (Character.CONTROL)));
        TestCase.assertTrue(((Character.getType(159)) == (Character.CONTROL)));
        TestCase.assertTrue(((Character.getType(173)) == (Character.FORMAT)));
        TestCase.assertTrue(((Character.getType(917631)) == (Character.FORMAT)));
        TestCase.assertTrue(((Character.getType(57344)) == (Character.PRIVATE_USE)));
        TestCase.assertTrue(((Character.getType(1114109)) == (Character.PRIVATE_USE)));
        TestCase.assertTrue(((Character.getType(55296)) == (Character.SURROGATE)));
        TestCase.assertTrue(((Character.getType(57343)) == (Character.SURROGATE)));
        TestCase.assertTrue(((Character.getType(65073)) == (Character.DASH_PUNCTUATION)));
        TestCase.assertTrue(((Character.getType(65293)) == (Character.DASH_PUNCTUATION)));
        TestCase.assertTrue(((Character.getType(40)) == (Character.START_PUNCTUATION)));
        TestCase.assertTrue(((Character.getType(65378)) == (Character.START_PUNCTUATION)));
        TestCase.assertTrue(((Character.getType(41)) == (Character.END_PUNCTUATION)));
        TestCase.assertTrue(((Character.getType(65379)) == (Character.END_PUNCTUATION)));
        TestCase.assertTrue(((Character.getType(95)) == (Character.CONNECTOR_PUNCTUATION)));
        TestCase.assertTrue(((Character.getType(65343)) == (Character.CONNECTOR_PUNCTUATION)));
        TestCase.assertTrue(((Character.getType(8244)) == (Character.OTHER_PUNCTUATION)));
        TestCase.assertTrue(((Character.getType(66463)) == (Character.OTHER_PUNCTUATION)));
        TestCase.assertTrue(((Character.getType(43)) == (Character.MATH_SYMBOL)));
        TestCase.assertTrue(((Character.getType(120513)) == (Character.MATH_SYMBOL)));
        TestCase.assertTrue(((Character.getType(36)) == (Character.CURRENCY_SYMBOL)));
        TestCase.assertTrue(((Character.getType(65510)) == (Character.CURRENCY_SYMBOL)));
        TestCase.assertTrue(((Character.getType(94)) == (Character.MODIFIER_SYMBOL)));
        TestCase.assertTrue(((Character.getType(65507)) == (Character.MODIFIER_SYMBOL)));
        TestCase.assertTrue(((Character.getType(166)) == (Character.OTHER_SYMBOL)));
        TestCase.assertTrue(((Character.getType(119638)) == (Character.OTHER_SYMBOL)));
        TestCase.assertTrue(((Character.getType(171)) == (Character.INITIAL_QUOTE_PUNCTUATION)));
        TestCase.assertTrue(((Character.getType(8249)) == (Character.INITIAL_QUOTE_PUNCTUATION)));
        TestCase.assertTrue(((Character.getType(187)) == (Character.FINAL_QUOTE_PUNCTUATION)));
        TestCase.assertTrue(((Character.getType(8250)) == (Character.FINAL_QUOTE_PUNCTUATION)));
    }

    /**
     * java.lang.Character#hashCode()
     */
    public void test_hashCode() {
        TestCase.assertEquals("Incorrect hash returned", 89, new Character('Y').hashCode());
    }

    /**
     * java.lang.Character#isDefined(char)
     */
    public void test_isDefinedC() {
        TestCase.assertTrue("Defined character returned false", Character.isDefined('v'));
        TestCase.assertTrue("Defined character returned false", Character.isDefined('\u6039'));
    }

    /**
     * java.lang.Character#isDefined(int)
     */
    public void test_isDefined_I() {
        TestCase.assertTrue(Character.isDefined(((int) ('v'))));
        TestCase.assertTrue(Character.isDefined(((int) ('\u6039'))));
        TestCase.assertTrue(Character.isDefined(66304));
        TestCase.assertFalse(Character.isDefined(196608));
        TestCase.assertFalse(Character.isDefined(262143));
        TestCase.assertFalse(Character.isDefined(1114112));
    }

    /**
     * java.lang.Character#isDigit(char)
     */
    public void test_isDigitC() {
        TestCase.assertTrue("Digit returned false", Character.isDigit('1'));
        TestCase.assertFalse("Non-Digit returned false", Character.isDigit('A'));
    }

    /**
     * java.lang.Character#isDigit(int)
     */
    public void test_isDigit_I() {
        TestCase.assertTrue(Character.isDigit(((int) ('1'))));
        TestCase.assertFalse(Character.isDigit(((int) ('A'))));
        TestCase.assertTrue(Character.isDigit(48));
        TestCase.assertTrue(Character.isDigit(53));
        TestCase.assertTrue(Character.isDigit(57));
        TestCase.assertTrue(Character.isDigit(1632));
        TestCase.assertTrue(Character.isDigit(1637));
        TestCase.assertTrue(Character.isDigit(1641));
        TestCase.assertTrue(Character.isDigit(1776));
        TestCase.assertTrue(Character.isDigit(1781));
        TestCase.assertTrue(Character.isDigit(1785));
        TestCase.assertTrue(Character.isDigit(2406));
        TestCase.assertTrue(Character.isDigit(2410));
        TestCase.assertTrue(Character.isDigit(2415));
        TestCase.assertTrue(Character.isDigit(65296));
        TestCase.assertTrue(Character.isDigit(65301));
        TestCase.assertTrue(Character.isDigit(65305));
        TestCase.assertTrue(Character.isDigit(120782));
        TestCase.assertTrue(Character.isDigit(120792));
        TestCase.assertFalse(Character.isDigit(194560));
        TestCase.assertFalse(Character.isDigit(1114109));
        TestCase.assertFalse(Character.isDigit(1114112));
    }

    /**
     * java.lang.Character#isIdentifierIgnorable(char)
     */
    public void test_isIdentifierIgnorableC() {
        TestCase.assertTrue("Ignorable whitespace returned false", Character.isIdentifierIgnorable('\u0007'));
        TestCase.assertTrue("Ignorable non - whitespace  control returned false", Character.isIdentifierIgnorable('\u000f'));
        TestCase.assertTrue("Ignorable join control returned false", Character.isIdentifierIgnorable('\u200e'));
        // the spec is wrong, and our implementation is correct
        TestCase.assertTrue("Ignorable bidi control returned false", Character.isIdentifierIgnorable('\u202b'));
        TestCase.assertTrue("Ignorable format control returned false", Character.isIdentifierIgnorable('\u206c'));
        TestCase.assertTrue("Ignorable zero-width no-break returned false", Character.isIdentifierIgnorable('\ufeff'));
        TestCase.assertFalse("Non-Ignorable returned true", Character.isIdentifierIgnorable('e'));
    }

    /**
     * java.lang.Character#isIdentifierIgnorable(int)
     */
    public void test_isIdentifierIgnorable_I() {
        TestCase.assertTrue(Character.isIdentifierIgnorable(0));
        TestCase.assertTrue(Character.isIdentifierIgnorable(4));
        TestCase.assertTrue(Character.isIdentifierIgnorable(8));
        TestCase.assertTrue(Character.isIdentifierIgnorable(14));
        TestCase.assertTrue(Character.isIdentifierIgnorable(19));
        TestCase.assertTrue(Character.isIdentifierIgnorable(27));
        TestCase.assertTrue(Character.isIdentifierIgnorable(127));
        TestCase.assertTrue(Character.isIdentifierIgnorable(143));
        TestCase.assertTrue(Character.isIdentifierIgnorable(159));
        TestCase.assertTrue(Character.isIdentifierIgnorable(8235));
        TestCase.assertTrue(Character.isIdentifierIgnorable(8300));
        TestCase.assertTrue(Character.isIdentifierIgnorable(65279));
        TestCase.assertFalse(Character.isIdentifierIgnorable(101));
        TestCase.assertTrue(Character.isIdentifierIgnorable(119155));
        TestCase.assertFalse(Character.isIdentifierIgnorable(1114109));
        TestCase.assertFalse(Character.isIdentifierIgnorable(1114112));
    }

    /**
     * java.lang.Character#isMirrored(char)
     */
    public void test_isMirrored_C() {
        TestCase.assertTrue(Character.isMirrored('('));
        TestCase.assertFalse(Character.isMirrored('\uffff'));
    }

    /**
     * java.lang.Character#isMirrored(int)
     */
    public void test_isMirrored_I() {
        TestCase.assertTrue(Character.isMirrored(40));
        TestCase.assertFalse(Character.isMirrored(65535));
        TestCase.assertFalse(Character.isMirrored(1114112));
    }

    /**
     * java.lang.Character#isISOControl(char)
     */
    public void test_isISOControlC() {
        // Test for method boolean java.lang.Character.isISOControl(char)
        for (int i = 0; i < 32; i++) {
            TestCase.assertTrue("ISOConstrol char returned false", Character.isISOControl(((char) (i))));
        }
        for (int i = 127; i < 160; i++) {
            TestCase.assertTrue("ISOConstrol char returned false", Character.isISOControl(((char) (i))));
        }
    }

    /**
     * java.lang.Character#isISOControl(int)
     */
    public void test_isISOControlI() {
        // Test for method boolean java.lang.Character.isISOControl(char)
        for (int i = 0; i < 32; i++) {
            TestCase.assertTrue("ISOConstrol char returned false", Character.isISOControl(i));
        }
        for (int i = 127; i < 160; i++) {
            TestCase.assertTrue("ISOConstrol char returned false", Character.isISOControl(i));
        }
        for (int i = 160; i < 260; i++) {
            TestCase.assertFalse("Not ISOConstrol char returned true", Character.isISOControl(i));
        }
    }

    /**
     * java.lang.Character#isJavaIdentifierPart(char)
     */
    public void test_isJavaIdentifierPartC() {
        TestCase.assertTrue("letter returned false", Character.isJavaIdentifierPart('l'));
        TestCase.assertTrue("currency returned false", Character.isJavaIdentifierPart('$'));
        TestCase.assertTrue("digit returned false", Character.isJavaIdentifierPart('9'));
        TestCase.assertTrue("connecting char returned false", Character.isJavaIdentifierPart('_'));
        TestCase.assertTrue("ignorable control returned false", Character.isJavaIdentifierPart('\u200b'));
        TestCase.assertFalse("semi returned true", Character.isJavaIdentifierPart(';'));
    }

    /**
     * java.lang.Character#isJavaIdentifierPart(int)
     */
    public void test_isJavaIdentifierPart_I() {
        TestCase.assertTrue(Character.isJavaIdentifierPart(((int) ('l'))));
        TestCase.assertTrue(Character.isJavaIdentifierPart(((int) ('$'))));
        TestCase.assertTrue(Character.isJavaIdentifierPart(((int) ('9'))));
        TestCase.assertTrue(Character.isJavaIdentifierPart(((int) ('_'))));
        TestCase.assertFalse(Character.isJavaIdentifierPart(((int) (';'))));
        TestCase.assertTrue(Character.isJavaIdentifierPart(65));
        TestCase.assertTrue(Character.isJavaIdentifierPart(66560));
        TestCase.assertTrue(Character.isJavaIdentifierPart(97));
        TestCase.assertTrue(Character.isJavaIdentifierPart(66600));
        TestCase.assertTrue(Character.isJavaIdentifierPart(453));
        TestCase.assertTrue(Character.isJavaIdentifierPart(8188));
        TestCase.assertTrue(Character.isJavaIdentifierPart(688));
        TestCase.assertTrue(Character.isJavaIdentifierPart(65439));
        TestCase.assertTrue(Character.isJavaIdentifierPart(443));
        TestCase.assertTrue(Character.isJavaIdentifierPart(194696));
        TestCase.assertTrue(Character.isJavaIdentifierPart(36));
        TestCase.assertTrue(Character.isJavaIdentifierPart(65510));
        TestCase.assertTrue(Character.isJavaIdentifierPart(95));
        TestCase.assertTrue(Character.isJavaIdentifierPart(65343));
        TestCase.assertTrue(Character.isJavaIdentifierPart(6477));
        TestCase.assertTrue(Character.isJavaIdentifierPart(120782));
        TestCase.assertTrue(Character.isJavaIdentifierPart(8544));
        TestCase.assertTrue(Character.isJavaIdentifierPart(66378));
        TestCase.assertTrue(Character.isJavaIdentifierPart(3970));
        TestCase.assertTrue(Character.isJavaIdentifierPart(119168));
        TestCase.assertTrue(Character.isJavaIdentifierPart(0));
        TestCase.assertTrue(Character.isJavaIdentifierPart(8));
        TestCase.assertTrue(Character.isJavaIdentifierPart(14));
        TestCase.assertTrue(Character.isJavaIdentifierPart(27));
        TestCase.assertTrue(Character.isJavaIdentifierPart(127));
        TestCase.assertTrue(Character.isJavaIdentifierPart(159));
        TestCase.assertTrue(Character.isJavaIdentifierPart(173));
        TestCase.assertTrue(Character.isJavaIdentifierPart(917631));
        TestCase.assertTrue(Character.isJavaIdentifierPart(8203));
    }

    /**
     * java.lang.Character#isJavaIdentifierStart(char)
     */
    public void test_isJavaIdentifierStartC() {
        TestCase.assertTrue("letter returned false", Character.isJavaIdentifierStart('l'));
        TestCase.assertTrue("currency returned false", Character.isJavaIdentifierStart('$'));
        TestCase.assertTrue("connecting char returned false", Character.isJavaIdentifierStart('_'));
        TestCase.assertFalse("digit returned true", Character.isJavaIdentifierStart('9'));
        TestCase.assertFalse("ignorable control returned true", Character.isJavaIdentifierStart('\u200b'));
        TestCase.assertFalse("semi returned true", Character.isJavaIdentifierStart(';'));
    }

    /**
     * java.lang.Character#isJavaIdentifierStart(int)
     */
    public void test_isJavaIdentifierStart_I() {
        TestCase.assertTrue(Character.isJavaIdentifierStart(((int) ('l'))));
        TestCase.assertTrue(Character.isJavaIdentifierStart(((int) ('$'))));
        TestCase.assertTrue(Character.isJavaIdentifierStart(((int) ('_'))));
        TestCase.assertFalse(Character.isJavaIdentifierStart(((int) ('9'))));
        TestCase.assertFalse(Character.isJavaIdentifierStart(((int) ('\u200b'))));
        TestCase.assertFalse(Character.isJavaIdentifierStart(((int) (';'))));
        TestCase.assertTrue(Character.isJavaIdentifierStart(65));
        TestCase.assertTrue(Character.isJavaIdentifierStart(66560));
        TestCase.assertTrue(Character.isJavaIdentifierStart(97));
        TestCase.assertTrue(Character.isJavaIdentifierStart(66600));
        TestCase.assertTrue(Character.isJavaIdentifierStart(453));
        TestCase.assertTrue(Character.isJavaIdentifierStart(8188));
        TestCase.assertTrue(Character.isJavaIdentifierStart(688));
        TestCase.assertTrue(Character.isJavaIdentifierStart(65439));
        TestCase.assertTrue(Character.isJavaIdentifierStart(443));
        TestCase.assertTrue(Character.isJavaIdentifierStart(194696));
        TestCase.assertTrue(Character.isJavaIdentifierPart(36));
        TestCase.assertTrue(Character.isJavaIdentifierPart(65510));
        TestCase.assertTrue(Character.isJavaIdentifierPart(95));
        TestCase.assertTrue(Character.isJavaIdentifierPart(65343));
        TestCase.assertTrue(Character.isJavaIdentifierPart(8544));
        TestCase.assertTrue(Character.isJavaIdentifierPart(66378));
        TestCase.assertFalse(Character.isJavaIdentifierPart(1114112));
    }

    /**
     * java.lang.Character#isLetter(char)
     */
    public void test_isLetterC() {
        TestCase.assertTrue("Letter returned false", Character.isLetter('L'));
        TestCase.assertFalse("Non-Letter returned true", Character.isLetter('9'));
    }

    /**
     * java.lang.Character#isLetter(int)
     */
    public void test_isLetter_I() {
        TestCase.assertTrue(Character.isLetter(((int) ('L'))));
        TestCase.assertFalse(Character.isLetter(((int) ('9'))));
        TestCase.assertTrue(Character.isLetter(8105));
        TestCase.assertTrue(Character.isLetter(119808));
        TestCase.assertTrue(Character.isLetter(120354));
        TestCase.assertTrue(Character.isLetter(65536));
        TestCase.assertFalse(Character.isLetter(65836));
        TestCase.assertFalse(Character.isLetter(1114112));
    }

    /**
     * java.lang.Character#isLetterOrDigit(char)
     */
    public void test_isLetterOrDigitC() {
        TestCase.assertTrue("Digit returned false", Character.isLetterOrDigit('9'));
        TestCase.assertTrue("Letter returned false", Character.isLetterOrDigit('K'));
        TestCase.assertFalse("Control returned true", Character.isLetterOrDigit('\n'));
        TestCase.assertFalse("Punctuation returned true", Character.isLetterOrDigit('?'));
    }

    /**
     * java.lang.Character#isLetterOrDigit(int)
     */
    public void test_isLetterOrDigit_I() {
        TestCase.assertTrue(Character.isLetterOrDigit(((int) ('9'))));
        TestCase.assertTrue(Character.isLetterOrDigit(((int) ('K'))));
        TestCase.assertFalse(Character.isLetterOrDigit(((int) ('\n'))));
        TestCase.assertFalse(Character.isLetterOrDigit(((int) ('?'))));
        TestCase.assertTrue(Character.isLetterOrDigit(8105));
        TestCase.assertTrue(Character.isLetterOrDigit(119808));
        TestCase.assertTrue(Character.isLetterOrDigit(120354));
        TestCase.assertTrue(Character.isLetterOrDigit(65536));
        TestCase.assertTrue(Character.isLetterOrDigit(120782));
        TestCase.assertTrue(Character.isLetterOrDigit(120792));
        TestCase.assertFalse(Character.isLetterOrDigit(1114109));
        TestCase.assertFalse(Character.isLetterOrDigit(65836));
        TestCase.assertFalse(Character.isLetterOrDigit(1114112));
    }

    /**
     * java.lang.Character#isLowerCase(char)
     */
    public void test_isLowerCaseC() {
        TestCase.assertTrue("lower returned false", Character.isLowerCase('a'));
        TestCase.assertFalse("upper returned true", Character.isLowerCase('T'));
    }

    /**
     * java.lang.Character#isLowerCase(int)
     */
    public void test_isLowerCase_I() {
        TestCase.assertTrue(Character.isLowerCase(((int) ('a'))));
        TestCase.assertFalse(Character.isLowerCase(((int) ('T'))));
        // assertTrue(Character.isLowerCase(0x10428));
        // assertTrue(Character.isLowerCase(0x1D4EA));
        TestCase.assertFalse(Character.isLowerCase(120068));
        TestCase.assertFalse(Character.isLowerCase(196608));
        TestCase.assertFalse(Character.isLowerCase(1114112));
    }

    /**
     * java.lang.Character#isSpaceChar(char)
     */
    public void test_isSpaceCharC() {
        TestCase.assertTrue("space returned false", Character.isSpaceChar(' '));
        TestCase.assertFalse("non-space returned true", Character.isSpaceChar('\n'));
    }

    /**
     * java.lang.Character#isSpaceChar(int)
     */
    public void test_isSpaceChar_I() {
        TestCase.assertTrue(Character.isSpaceChar(((int) (' '))));
        TestCase.assertFalse(Character.isSpaceChar(((int) ('\n'))));
        TestCase.assertTrue(Character.isSpaceChar(8192));
        TestCase.assertTrue(Character.isSpaceChar(8202));
        TestCase.assertTrue(Character.isSpaceChar(8232));
        TestCase.assertTrue(Character.isSpaceChar(8233));
        TestCase.assertFalse(Character.isSpaceChar(1114112));
    }

    /**
     * java.lang.Character#isTitleCase(char)
     */
    public void test_isTitleCaseC() {
        char[] tChars = new char[]{ ((char) (453)), ((char) (456)), ((char) (459)), ((char) (498)), ((char) (8072)), ((char) (8073)), ((char) (8074)), ((char) (8075)), ((char) (8076)), ((char) (8077)), ((char) (8078)), ((char) (8079)), ((char) (8088)), ((char) (8089)), ((char) (8090)), ((char) (8091)), ((char) (8092)), ((char) (8093)), ((char) (8094)), ((char) (8095)), ((char) (8104)), ((char) (8105)), ((char) (8106)), ((char) (8107)), ((char) (8108)), ((char) (8109)), ((char) (8110)), ((char) (8111)), ((char) (8124)), ((char) (8140)), ((char) (8188)) };
        byte tnum = 0;
        for (char c = 0; c < 65535; c++) {
            if (Character.isTitleCase(c)) {
                tnum++;
                int i;
                for (i = 0; i < (tChars.length); i++) {
                    if ((tChars[i]) == c) {
                        i = (tChars.length) + 1;
                    }
                }
                if (i < (tChars.length)) {
                    TestCase.fail("Non Title Case char returned true");
                }
            }
        }
        TestCase.assertTrue("Failed to find all Title Case chars", (tnum == (tChars.length)));
    }

    /**
     * java.lang.Character#isTitleCase(int)
     */
    public void test_isTitleCase_I() {
        // all the titlecase characters
        int[] titleCaseCharacters = new int[]{ 453, 456, 459, 498, 8072, 8073, 8074, 8075, 8076, 8077, 8078, 8079, 8088, 8089, 8090, 8091, 8092, 8093, 8094, 8095, 8104, 8105, 8106, 8107, 8108, 8109, 8110, 8111, 8124, 8140, 8188 };
        for (int titleCaseCharacter : titleCaseCharacters) {
            TestCase.assertTrue(Character.isTitleCase(titleCaseCharacter));
        }
        TestCase.assertFalse(Character.isTitleCase(1114112));
    }

    /**
     * java.lang.Character#isUnicodeIdentifierPart(char)
     */
    public void test_isUnicodeIdentifierPartC() {
        TestCase.assertTrue("'a' returned false", Character.isUnicodeIdentifierPart('a'));
        TestCase.assertTrue("'2' returned false", Character.isUnicodeIdentifierPart('2'));
        TestCase.assertFalse("'+' returned true", Character.isUnicodeIdentifierPart('+'));
    }

    /**
     * java.lang.Character#isUnicodeIdentifierPart(int)
     */
    public void test_isUnicodeIdentifierPart_I() {
        TestCase.assertTrue(Character.isUnicodeIdentifierPart(((int) ('a'))));
        TestCase.assertTrue(Character.isUnicodeIdentifierPart(((int) ('2'))));
        TestCase.assertFalse(Character.isUnicodeIdentifierPart(((int) ('+'))));
        TestCase.assertTrue(Character.isUnicodeIdentifierPart(8105));
        TestCase.assertTrue(Character.isUnicodeIdentifierPart(119808));
        TestCase.assertTrue(Character.isUnicodeIdentifierPart(120354));
        TestCase.assertTrue(Character.isUnicodeIdentifierPart(65536));
        TestCase.assertTrue(Character.isUnicodeIdentifierPart(48));
        TestCase.assertTrue(Character.isUnicodeIdentifierPart(53));
        TestCase.assertTrue(Character.isUnicodeIdentifierPart(57));
        TestCase.assertTrue(Character.isUnicodeIdentifierPart(1632));
        TestCase.assertTrue(Character.isUnicodeIdentifierPart(1637));
        TestCase.assertTrue(Character.isUnicodeIdentifierPart(1641));
        TestCase.assertTrue(Character.isUnicodeIdentifierPart(1776));
        TestCase.assertTrue(Character.isUnicodeIdentifierPart(1781));
        TestCase.assertTrue(Character.isUnicodeIdentifierPart(1785));
        TestCase.assertTrue(Character.isUnicodeIdentifierPart(2406));
        TestCase.assertTrue(Character.isUnicodeIdentifierPart(2410));
        TestCase.assertTrue(Character.isUnicodeIdentifierPart(2415));
        TestCase.assertTrue(Character.isUnicodeIdentifierPart(65296));
        TestCase.assertTrue(Character.isUnicodeIdentifierPart(65301));
        TestCase.assertTrue(Character.isUnicodeIdentifierPart(65305));
        TestCase.assertTrue(Character.isUnicodeIdentifierPart(120782));
        TestCase.assertTrue(Character.isUnicodeIdentifierPart(120792));
        TestCase.assertTrue(Character.isUnicodeIdentifierPart(5870));
        TestCase.assertTrue(Character.isUnicodeIdentifierPart(65075));
        TestCase.assertTrue(Character.isUnicodeIdentifierPart(65296));
        TestCase.assertTrue(Character.isUnicodeIdentifierPart(119141));
        TestCase.assertTrue(Character.isUnicodeIdentifierPart(119143));
        TestCase.assertTrue(Character.isUnicodeIdentifierPart(119155));
        TestCase.assertFalse(Character.isUnicodeIdentifierPart(1114111));
        TestCase.assertFalse(Character.isUnicodeIdentifierPart(1114112));
    }

    /**
     * java.lang.Character#isUnicodeIdentifierStart(char)
     */
    public void test_isUnicodeIdentifierStartC() {
        TestCase.assertTrue("'a' returned false", Character.isUnicodeIdentifierStart('a'));
        TestCase.assertFalse("'2' returned true", Character.isUnicodeIdentifierStart('2'));
        TestCase.assertFalse("'+' returned true", Character.isUnicodeIdentifierStart('+'));
    }

    /**
     * java.lang.Character#isUnicodeIdentifierStart(int)
     */
    public void test_isUnicodeIdentifierStart_I() {
        TestCase.assertTrue(Character.isUnicodeIdentifierStart(((int) ('a'))));
        TestCase.assertFalse(Character.isUnicodeIdentifierStart(((int) ('2'))));
        TestCase.assertFalse(Character.isUnicodeIdentifierStart(((int) ('+'))));
        TestCase.assertTrue(Character.isUnicodeIdentifierStart(8105));
        TestCase.assertTrue(Character.isUnicodeIdentifierStart(119808));
        TestCase.assertTrue(Character.isUnicodeIdentifierStart(120354));
        TestCase.assertTrue(Character.isUnicodeIdentifierStart(65536));
        TestCase.assertTrue(Character.isUnicodeIdentifierStart(5870));
        // number is not a valid start of a Unicode identifier
        TestCase.assertFalse(Character.isUnicodeIdentifierStart(48));
        TestCase.assertFalse(Character.isUnicodeIdentifierStart(57));
        TestCase.assertFalse(Character.isUnicodeIdentifierStart(1632));
        TestCase.assertFalse(Character.isUnicodeIdentifierStart(1641));
        TestCase.assertFalse(Character.isUnicodeIdentifierStart(1776));
        TestCase.assertFalse(Character.isUnicodeIdentifierStart(1785));
        TestCase.assertFalse(Character.isUnicodeIdentifierPart(1114111));
        TestCase.assertFalse(Character.isUnicodeIdentifierPart(1114112));
    }

    /**
     * java.lang.Character#isUpperCase(char)
     */
    public void test_isUpperCaseC() {
        TestCase.assertFalse("Incorrect case value", Character.isUpperCase('t'));
        TestCase.assertTrue("Incorrect case value", Character.isUpperCase('T'));
    }

    /**
     * java.lang.Character#isUpperCase(int)
     */
    public void test_isUpperCase_I() {
        TestCase.assertFalse(Character.isUpperCase(((int) ('t'))));
        TestCase.assertTrue(Character.isUpperCase(((int) ('T'))));
        TestCase.assertTrue(Character.isUpperCase(120068));
        TestCase.assertTrue(Character.isUpperCase(120328));
        TestCase.assertFalse(Character.isUpperCase(120406));
        TestCase.assertFalse(Character.isUpperCase(1114109));
        TestCase.assertFalse(Character.isUpperCase(1114112));
    }

    /**
     * java.lang.Character#isWhitespace(char)
     */
    public void test_isWhitespaceC() {
        TestCase.assertTrue("space returned false", Character.isWhitespace('\n'));
        TestCase.assertFalse("non-space returned true", Character.isWhitespace('T'));
    }

    /**
     * java.lang.Character#isWhitespace(int)
     */
    public void test_isWhitespace_I() {
        TestCase.assertTrue(Character.isWhitespace(((int) ('\n'))));
        TestCase.assertFalse(Character.isWhitespace(((int) ('T'))));
        TestCase.assertTrue(Character.isWhitespace(9));
        TestCase.assertTrue(Character.isWhitespace(10));
        TestCase.assertTrue(Character.isWhitespace(11));
        TestCase.assertTrue(Character.isWhitespace(12));
        TestCase.assertTrue(Character.isWhitespace(13));
        TestCase.assertTrue(Character.isWhitespace(28));
        TestCase.assertTrue(Character.isWhitespace(29));
        TestCase.assertTrue(Character.isWhitespace(31));
        TestCase.assertTrue(Character.isWhitespace(30));
        TestCase.assertTrue(Character.isWhitespace(8192));
        TestCase.assertTrue(Character.isWhitespace(8202));
        TestCase.assertTrue(Character.isWhitespace(8232));
        TestCase.assertTrue(Character.isWhitespace(8233));
        TestCase.assertFalse(Character.isWhitespace(160));
        TestCase.assertFalse(Character.isWhitespace(8239));
        TestCase.assertFalse(Character.isWhitespace(1114112));
        TestCase.assertFalse(Character.isWhitespace(65279));
        // FIXME depend on ICU4J
        // assertFalse(Character.isWhitespace(0x2007));
    }

    /**
     * java.lang.Character#reverseBytes(char)
     */
    public void test_reverseBytesC() {
        char[] original = new char[]{ 0, 16, 170, 45056, 52224, 43981, 65450 };
        char[] reversed = new char[]{ 0, 4096, 43520, 176, 204, 52651, 43775 };
        TestCase.assertTrue("Test self check", ((original.length) == (reversed.length)));
        for (int i = 0; i < (original.length); i++) {
            char origChar = original[i];
            char reversedChar = reversed[i];
            char origReversed = Character.reverseBytes(origChar);
            TestCase.assertTrue(((("java.lang.Character.reverseBytes failed: orig char=" + (Integer.toHexString(origChar))) + ", reversed char=") + (Integer.toHexString(origReversed))), (reversedChar == origReversed));
        }
    }

    /**
     * java.lang.Character#toLowerCase(char)
     */
    public void test_toLowerCaseC() {
        TestCase.assertEquals("Failed to change case", 't', Character.toLowerCase('T'));
    }

    /**
     * java.lang.Character#toLowerCase(int)
     */
    public void test_toLowerCase_I() {
        TestCase.assertEquals('t', Character.toLowerCase(((int) ('T'))));
        TestCase.assertEquals(66600, Character.toLowerCase(66560));
        TestCase.assertEquals(66600, Character.toLowerCase(66600));
        TestCase.assertEquals(120068, Character.toLowerCase(120068));
        TestCase.assertEquals(1114109, Character.toLowerCase(1114109));
        TestCase.assertEquals(1114112, Character.toLowerCase(1114112));
    }

    /**
     * java.lang.Character#toString()
     */
    public void test_toString() {
        TestCase.assertEquals("Incorrect String returned", "T", new Character('T').toString());
    }

    /**
     * java.lang.Character#toTitleCase(char)
     */
    public void test_toTitleCaseC() {
        TestCase.assertEquals("Incorrect title case for a", 'A', Character.toTitleCase('a'));
        TestCase.assertEquals("Incorrect title case for A", 'A', Character.toTitleCase('A'));
        TestCase.assertEquals("Incorrect title case for 1", '1', Character.toTitleCase('1'));
    }

    /**
     * java.lang.Character#toTitleCase(int)
     */
    public void test_toTitleCase_I() {
        TestCase.assertEquals('A', Character.toTitleCase(((int) ('a'))));
        TestCase.assertEquals('A', Character.toTitleCase(((int) ('A'))));
        TestCase.assertEquals('1', Character.toTitleCase(((int) ('1'))));
        TestCase.assertEquals(66560, Character.toTitleCase(66600));
        TestCase.assertEquals(66560, Character.toTitleCase(66560));
        TestCase.assertEquals(1114111, Character.toTitleCase(1114111));
        TestCase.assertEquals(1114112, Character.toTitleCase(1114112));
    }

    /**
     * java.lang.Character#toUpperCase(char)
     */
    public void test_toUpperCaseC() {
        // Test for method char java.lang.Character.toUpperCase(char)
        TestCase.assertEquals("Incorrect upper case for a", 'A', Character.toUpperCase('a'));
        TestCase.assertEquals("Incorrect upper case for A", 'A', Character.toUpperCase('A'));
        TestCase.assertEquals("Incorrect upper case for 1", '1', Character.toUpperCase('1'));
    }

    /**
     * java.lang.Character#toUpperCase(int)
     */
    public void test_toUpperCase_I() {
        TestCase.assertEquals('A', Character.toUpperCase(((int) ('a'))));
        TestCase.assertEquals('A', Character.toUpperCase(((int) ('A'))));
        TestCase.assertEquals('1', Character.toUpperCase(((int) ('1'))));
        TestCase.assertEquals(66560, Character.toUpperCase(66600));
        TestCase.assertEquals(66560, Character.toUpperCase(66560));
        TestCase.assertEquals(1114111, Character.toUpperCase(1114111));
        TestCase.assertEquals(1114112, Character.toUpperCase(1114112));
    }

    /**
     * java.lang.Character#getDirectionality(int)
     */
    public void test_isDirectionaliy_I() {
        TestCase.assertEquals(Character.DIRECTIONALITY_UNDEFINED, Character.getDirectionality(65534));
        TestCase.assertEquals(Character.DIRECTIONALITY_UNDEFINED, Character.getDirectionality(196608));
        TestCase.assertEquals(Character.DIRECTIONALITY_UNDEFINED, Character.getDirectionality(1114112));
        TestCase.assertEquals(Character.DIRECTIONALITY_UNDEFINED, Character.getDirectionality((-1)));
        TestCase.assertEquals(Character.DIRECTIONALITY_LEFT_TO_RIGHT, Character.getDirectionality(65));
        TestCase.assertEquals(Character.DIRECTIONALITY_LEFT_TO_RIGHT, Character.getDirectionality(65536));
        TestCase.assertEquals(Character.DIRECTIONALITY_LEFT_TO_RIGHT, Character.getDirectionality(66729));
        TestCase.assertEquals(Character.DIRECTIONALITY_RIGHT_TO_LEFT, Character.getDirectionality(64335));
        TestCase.assertEquals(Character.DIRECTIONALITY_RIGHT_TO_LEFT, Character.getDirectionality(67640));
        // Unicode standard 5.1 changed category of unicode point 0x0600 from AL to AN
        TestCase.assertEquals(Character.DIRECTIONALITY_ARABIC_NUMBER, Character.getDirectionality(1536));
        TestCase.assertEquals(Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC, Character.getDirectionality(65276));
        TestCase.assertEquals(Character.DIRECTIONALITY_EUROPEAN_NUMBER, Character.getDirectionality(8304));
        TestCase.assertEquals(Character.DIRECTIONALITY_EUROPEAN_NUMBER, Character.getDirectionality(120831));
        TestCase.assertEquals(Character.DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR, Character.getDirectionality(43));
        TestCase.assertEquals(Character.DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR, Character.getDirectionality(65291));
        TestCase.assertEquals(Character.DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR, Character.getDirectionality(35));
        TestCase.assertEquals(Character.DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR, Character.getDirectionality(6107));
        TestCase.assertEquals(Character.DIRECTIONALITY_ARABIC_NUMBER, Character.getDirectionality(1632));
        TestCase.assertEquals(Character.DIRECTIONALITY_ARABIC_NUMBER, Character.getDirectionality(1644));
        TestCase.assertEquals(Character.DIRECTIONALITY_COMMON_NUMBER_SEPARATOR, Character.getDirectionality(44));
        TestCase.assertEquals(Character.DIRECTIONALITY_COMMON_NUMBER_SEPARATOR, Character.getDirectionality(65306));
        TestCase.assertEquals(Character.DIRECTIONALITY_NONSPACING_MARK, Character.getDirectionality(6094));
        TestCase.assertEquals(Character.DIRECTIONALITY_NONSPACING_MARK, Character.getDirectionality(917979));
        TestCase.assertEquals(Character.DIRECTIONALITY_BOUNDARY_NEUTRAL, Character.getDirectionality(0));
        TestCase.assertEquals(Character.DIRECTIONALITY_BOUNDARY_NEUTRAL, Character.getDirectionality(917631));
        TestCase.assertEquals(Character.DIRECTIONALITY_PARAGRAPH_SEPARATOR, Character.getDirectionality(10));
        TestCase.assertEquals(Character.DIRECTIONALITY_PARAGRAPH_SEPARATOR, Character.getDirectionality(8233));
        TestCase.assertEquals(Character.DIRECTIONALITY_SEGMENT_SEPARATOR, Character.getDirectionality(9));
        TestCase.assertEquals(Character.DIRECTIONALITY_SEGMENT_SEPARATOR, Character.getDirectionality(31));
        TestCase.assertEquals(Character.DIRECTIONALITY_WHITESPACE, Character.getDirectionality(32));
        TestCase.assertEquals(Character.DIRECTIONALITY_WHITESPACE, Character.getDirectionality(12288));
        TestCase.assertEquals(Character.DIRECTIONALITY_OTHER_NEUTRALS, Character.getDirectionality(12272));
        TestCase.assertEquals(Character.DIRECTIONALITY_OTHER_NEUTRALS, Character.getDirectionality(119638));
        TestCase.assertEquals(Character.DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING, Character.getDirectionality(8234));
        TestCase.assertEquals(Character.DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE, Character.getDirectionality(8237));
        TestCase.assertEquals(Character.DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING, Character.getDirectionality(8235));
        TestCase.assertEquals(Character.DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE, Character.getDirectionality(8238));
        TestCase.assertEquals(Character.DIRECTIONALITY_POP_DIRECTIONAL_FORMAT, Character.getDirectionality(8236));
    }
}

