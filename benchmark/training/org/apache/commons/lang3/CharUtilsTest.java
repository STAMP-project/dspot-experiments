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


import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Unit tests {@link org.apache.commons.lang3.CharUtils}.
 */
public class CharUtilsTest {
    private static final char CHAR_COPY = '\u00a9';

    private static final Character CHARACTER_A = new Character('A');

    private static final Character CHARACTER_B = new Character('B');

    @Test
    public void testCompare() {
        Assertions.assertTrue(((CharUtils.compare('a', 'b')) < 0));
        Assertions.assertEquals(0, CharUtils.compare('c', 'c'));
        Assertions.assertTrue(((CharUtils.compare('c', 'a')) > 0));
    }

    @Test
    public void testConstructor() {
        Assertions.assertNotNull(new CharUtils());
        final Constructor<?>[] cons = CharUtils.class.getDeclaredConstructors();
        Assertions.assertEquals(1, cons.length);
        Assertions.assertTrue(Modifier.isPublic(cons[0].getModifiers()));
        Assertions.assertTrue(Modifier.isPublic(BooleanUtils.class.getModifiers()));
        Assertions.assertFalse(Modifier.isFinal(BooleanUtils.class.getModifiers()));
    }

    @Test
    public void testIsAscii_char() {
        Assertions.assertTrue(CharUtils.isAscii('a'));
        Assertions.assertTrue(CharUtils.isAscii('A'));
        Assertions.assertTrue(CharUtils.isAscii('3'));
        Assertions.assertTrue(CharUtils.isAscii('-'));
        Assertions.assertTrue(CharUtils.isAscii('\n'));
        Assertions.assertFalse(CharUtils.isAscii(CharUtilsTest.CHAR_COPY));
        for (int i = 0; i < 255; i++) {
            Assertions.assertEquals((i < 128), CharUtils.isAscii(((char) (i))));
        }
    }

    @Test
    public void testIsAsciiAlpha_char() {
        Assertions.assertTrue(CharUtils.isAsciiAlpha('a'));
        Assertions.assertTrue(CharUtils.isAsciiAlpha('A'));
        Assertions.assertFalse(CharUtils.isAsciiAlpha('3'));
        Assertions.assertFalse(CharUtils.isAsciiAlpha('-'));
        Assertions.assertFalse(CharUtils.isAsciiAlpha('\n'));
        Assertions.assertFalse(CharUtils.isAsciiAlpha(CharUtilsTest.CHAR_COPY));
        for (int i = 0; i < 196; i++) {
            if (((i >= 'A') && (i <= 'Z')) || ((i >= 'a') && (i <= 'z'))) {
                Assertions.assertTrue(CharUtils.isAsciiAlpha(((char) (i))));
            } else {
                Assertions.assertFalse(CharUtils.isAsciiAlpha(((char) (i))));
            }
        }
    }

    @Test
    public void testIsAsciiAlphaLower_char() {
        Assertions.assertTrue(CharUtils.isAsciiAlphaLower('a'));
        Assertions.assertFalse(CharUtils.isAsciiAlphaLower('A'));
        Assertions.assertFalse(CharUtils.isAsciiAlphaLower('3'));
        Assertions.assertFalse(CharUtils.isAsciiAlphaLower('-'));
        Assertions.assertFalse(CharUtils.isAsciiAlphaLower('\n'));
        Assertions.assertFalse(CharUtils.isAsciiAlphaLower(CharUtilsTest.CHAR_COPY));
        for (int i = 0; i < 196; i++) {
            if ((i >= 'a') && (i <= 'z')) {
                Assertions.assertTrue(CharUtils.isAsciiAlphaLower(((char) (i))));
            } else {
                Assertions.assertFalse(CharUtils.isAsciiAlphaLower(((char) (i))));
            }
        }
    }

    @Test
    public void testIsAsciiAlphanumeric_char() {
        Assertions.assertTrue(CharUtils.isAsciiAlphanumeric('a'));
        Assertions.assertTrue(CharUtils.isAsciiAlphanumeric('A'));
        Assertions.assertTrue(CharUtils.isAsciiAlphanumeric('3'));
        Assertions.assertFalse(CharUtils.isAsciiAlphanumeric('-'));
        Assertions.assertFalse(CharUtils.isAsciiAlphanumeric('\n'));
        Assertions.assertFalse(CharUtils.isAsciiAlphanumeric(CharUtilsTest.CHAR_COPY));
        for (int i = 0; i < 196; i++) {
            if ((((i >= 'A') && (i <= 'Z')) || ((i >= 'a') && (i <= 'z'))) || ((i >= '0') && (i <= '9'))) {
                Assertions.assertTrue(CharUtils.isAsciiAlphanumeric(((char) (i))));
            } else {
                Assertions.assertFalse(CharUtils.isAsciiAlphanumeric(((char) (i))));
            }
        }
    }

    @Test
    public void testIsAsciiAlphaUpper_char() {
        Assertions.assertFalse(CharUtils.isAsciiAlphaUpper('a'));
        Assertions.assertTrue(CharUtils.isAsciiAlphaUpper('A'));
        Assertions.assertFalse(CharUtils.isAsciiAlphaUpper('3'));
        Assertions.assertFalse(CharUtils.isAsciiAlphaUpper('-'));
        Assertions.assertFalse(CharUtils.isAsciiAlphaUpper('\n'));
        Assertions.assertFalse(CharUtils.isAsciiAlphaUpper(CharUtilsTest.CHAR_COPY));
        for (int i = 0; i < 196; i++) {
            if ((i >= 'A') && (i <= 'Z')) {
                Assertions.assertTrue(CharUtils.isAsciiAlphaUpper(((char) (i))));
            } else {
                Assertions.assertFalse(CharUtils.isAsciiAlphaUpper(((char) (i))));
            }
        }
    }

    @Test
    public void testIsAsciiControl_char() {
        Assertions.assertFalse(CharUtils.isAsciiControl('a'));
        Assertions.assertFalse(CharUtils.isAsciiControl('A'));
        Assertions.assertFalse(CharUtils.isAsciiControl('3'));
        Assertions.assertFalse(CharUtils.isAsciiControl('-'));
        Assertions.assertTrue(CharUtils.isAsciiControl('\n'));
        Assertions.assertFalse(CharUtils.isAsciiControl(CharUtilsTest.CHAR_COPY));
        for (int i = 0; i < 196; i++) {
            if ((i < 32) || (i == 127)) {
                Assertions.assertTrue(CharUtils.isAsciiControl(((char) (i))));
            } else {
                Assertions.assertFalse(CharUtils.isAsciiControl(((char) (i))));
            }
        }
    }

    @Test
    public void testIsAsciiNumeric_char() {
        Assertions.assertFalse(CharUtils.isAsciiNumeric('a'));
        Assertions.assertFalse(CharUtils.isAsciiNumeric('A'));
        Assertions.assertTrue(CharUtils.isAsciiNumeric('3'));
        Assertions.assertFalse(CharUtils.isAsciiNumeric('-'));
        Assertions.assertFalse(CharUtils.isAsciiNumeric('\n'));
        Assertions.assertFalse(CharUtils.isAsciiNumeric(CharUtilsTest.CHAR_COPY));
        for (int i = 0; i < 196; i++) {
            if ((i >= '0') && (i <= '9')) {
                Assertions.assertTrue(CharUtils.isAsciiNumeric(((char) (i))));
            } else {
                Assertions.assertFalse(CharUtils.isAsciiNumeric(((char) (i))));
            }
        }
    }

    @Test
    public void testIsAsciiPrintable_char() {
        Assertions.assertTrue(CharUtils.isAsciiPrintable('a'));
        Assertions.assertTrue(CharUtils.isAsciiPrintable('A'));
        Assertions.assertTrue(CharUtils.isAsciiPrintable('3'));
        Assertions.assertTrue(CharUtils.isAsciiPrintable('-'));
        Assertions.assertFalse(CharUtils.isAsciiPrintable('\n'));
        Assertions.assertFalse(CharUtils.isAsciiPrintable(CharUtilsTest.CHAR_COPY));
        for (int i = 0; i < 196; i++) {
            if ((i >= 32) && (i <= 126)) {
                Assertions.assertTrue(CharUtils.isAsciiPrintable(((char) (i))));
            } else {
                Assertions.assertFalse(CharUtils.isAsciiPrintable(((char) (i))));
            }
        }
    }

    @Test
    public void testToChar_Character() {
        Assertions.assertEquals('A', CharUtils.toChar(CharUtilsTest.CHARACTER_A));
        Assertions.assertEquals('B', CharUtils.toChar(CharUtilsTest.CHARACTER_B));
        Assertions.assertThrows(IllegalArgumentException.class, () -> CharUtils.toChar(((Character) (null))));
    }

    @Test
    public void testToChar_Character_char() {
        Assertions.assertEquals('A', CharUtils.toChar(CharUtilsTest.CHARACTER_A, 'X'));
        Assertions.assertEquals('B', CharUtils.toChar(CharUtilsTest.CHARACTER_B, 'X'));
        Assertions.assertEquals('X', CharUtils.toChar(((Character) (null)), 'X'));
    }

    @Test
    public void testToChar_String() {
        Assertions.assertEquals('A', CharUtils.toChar("A"));
        Assertions.assertEquals('B', CharUtils.toChar("BA"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> CharUtils.toChar(((String) (null))));
        Assertions.assertThrows(IllegalArgumentException.class, () -> CharUtils.toChar(""));
    }

    @Test
    public void testToChar_String_char() {
        Assertions.assertEquals('A', CharUtils.toChar("A", 'X'));
        Assertions.assertEquals('B', CharUtils.toChar("BA", 'X'));
        Assertions.assertEquals('X', CharUtils.toChar("", 'X'));
        Assertions.assertEquals('X', CharUtils.toChar(((String) (null)), 'X'));
    }

    // intentional test of deprecated method
    @SuppressWarnings("deprecation")
    @Test
    public void testToCharacterObject_char() {
        Assertions.assertEquals(new Character('a'), CharUtils.toCharacterObject('a'));
        Assertions.assertSame(CharUtils.toCharacterObject('a'), CharUtils.toCharacterObject('a'));
        for (int i = 0; i < 128; i++) {
            final Character ch = CharUtils.toCharacterObject(((char) (i)));
            final Character ch2 = CharUtils.toCharacterObject(((char) (i)));
            Assertions.assertSame(ch, ch2);
            Assertions.assertEquals(i, ch.charValue());
        }
        for (int i = 128; i < 196; i++) {
            final Character ch = CharUtils.toCharacterObject(((char) (i)));
            final Character ch2 = CharUtils.toCharacterObject(((char) (i)));
            Assertions.assertEquals(ch, ch2);
            Assertions.assertNotSame(ch, ch2);
            Assertions.assertEquals(i, ch.charValue());
            Assertions.assertEquals(i, ch2.charValue());
        }
        Assertions.assertSame(CharUtils.toCharacterObject("a"), CharUtils.toCharacterObject('a'));
    }

    @Test
    public void testToCharacterObject_String() {
        Assertions.assertNull(CharUtils.toCharacterObject(null));
        Assertions.assertNull(CharUtils.toCharacterObject(""));
        Assertions.assertEquals(new Character('a'), CharUtils.toCharacterObject("a"));
        Assertions.assertEquals(new Character('a'), CharUtils.toCharacterObject("abc"));
        Assertions.assertSame(CharUtils.toCharacterObject("a"), CharUtils.toCharacterObject("a"));
    }

    @Test
    public void testToIntValue_char() {
        Assertions.assertEquals(0, CharUtils.toIntValue('0'));
        Assertions.assertEquals(1, CharUtils.toIntValue('1'));
        Assertions.assertEquals(2, CharUtils.toIntValue('2'));
        Assertions.assertEquals(3, CharUtils.toIntValue('3'));
        Assertions.assertEquals(4, CharUtils.toIntValue('4'));
        Assertions.assertEquals(5, CharUtils.toIntValue('5'));
        Assertions.assertEquals(6, CharUtils.toIntValue('6'));
        Assertions.assertEquals(7, CharUtils.toIntValue('7'));
        Assertions.assertEquals(8, CharUtils.toIntValue('8'));
        Assertions.assertEquals(9, CharUtils.toIntValue('9'));
        Assertions.assertThrows(IllegalArgumentException.class, () -> CharUtils.toIntValue('a'));
    }

    @Test
    public void testToIntValue_char_int() {
        Assertions.assertEquals(0, CharUtils.toIntValue('0', (-1)));
        Assertions.assertEquals(3, CharUtils.toIntValue('3', (-1)));
        Assertions.assertEquals((-1), CharUtils.toIntValue('a', (-1)));
    }

    @Test
    public void testToIntValue_Character() {
        Assertions.assertEquals(0, CharUtils.toIntValue(new Character('0')));
        Assertions.assertEquals(3, CharUtils.toIntValue(new Character('3')));
        Assertions.assertThrows(IllegalArgumentException.class, () -> CharUtils.toIntValue(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> CharUtils.toIntValue(CharUtilsTest.CHARACTER_A));
    }

    @Test
    public void testToIntValue_Character_int() {
        Assertions.assertEquals(0, CharUtils.toIntValue(new Character('0'), (-1)));
        Assertions.assertEquals(3, CharUtils.toIntValue(new Character('3'), (-1)));
        Assertions.assertEquals((-1), CharUtils.toIntValue(new Character('A'), (-1)));
        Assertions.assertEquals((-1), CharUtils.toIntValue(null, (-1)));
    }

    @Test
    public void testToString_char() {
        Assertions.assertEquals("a", CharUtils.toString('a'));
        Assertions.assertSame(CharUtils.toString('a'), CharUtils.toString('a'));
        for (int i = 0; i < 128; i++) {
            final String str = CharUtils.toString(((char) (i)));
            final String str2 = CharUtils.toString(((char) (i)));
            Assertions.assertSame(str, str2);
            Assertions.assertEquals(1, str.length());
            Assertions.assertEquals(i, str.charAt(0));
        }
        for (int i = 128; i < 196; i++) {
            final String str = CharUtils.toString(((char) (i)));
            final String str2 = CharUtils.toString(((char) (i)));
            Assertions.assertEquals(str, str2);
            Assertions.assertNotSame(str, str2);
            Assertions.assertEquals(1, str.length());
            Assertions.assertEquals(i, str.charAt(0));
            Assertions.assertEquals(1, str2.length());
            Assertions.assertEquals(i, str2.charAt(0));
        }
    }

    @Test
    public void testToString_Character() {
        Assertions.assertNull(CharUtils.toString(null));
        Assertions.assertEquals("A", CharUtils.toString(CharUtilsTest.CHARACTER_A));
        Assertions.assertSame(CharUtils.toString(CharUtilsTest.CHARACTER_A), CharUtils.toString(CharUtilsTest.CHARACTER_A));
    }

    @Test
    public void testToUnicodeEscaped_char() {
        Assertions.assertEquals("\\u0041", CharUtils.unicodeEscaped('A'));
        Assertions.assertEquals("\\u004c", CharUtils.unicodeEscaped('L'));
        for (int i = 0; i < 196; i++) {
            final String str = CharUtils.unicodeEscaped(((char) (i)));
            Assertions.assertEquals(6, str.length());
            final int val = Integer.parseInt(str.substring(2), 16);
            Assertions.assertEquals(i, val);
        }
        Assertions.assertEquals("\\u0999", CharUtils.unicodeEscaped(((char) (2457))));
        Assertions.assertEquals("\\u1001", CharUtils.unicodeEscaped(((char) (4097))));
    }

    @Test
    public void testToUnicodeEscaped_Character() {
        Assertions.assertNull(CharUtils.unicodeEscaped(null));
        Assertions.assertEquals("\\u0041", CharUtils.unicodeEscaped(CharUtilsTest.CHARACTER_A));
    }
}

