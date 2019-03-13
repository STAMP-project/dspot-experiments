/**
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.java.lang;


import java.lang.reflect.Method;
import junit.framework.TestCase;

import static java.lang.Character.UnicodeBlock.ANCIENT_GREEK_MUSICAL_NOTATION;
import static java.lang.Character.UnicodeBlock.BASIC_LATIN;
import static java.lang.Character.UnicodeBlock.COMBINING_MARKS_FOR_SYMBOLS;
import static java.lang.Character.UnicodeBlock.CYPRIOT_SYLLABARY;
import static java.lang.Character.UnicodeBlock.CYRILLIC_SUPPLEMENTARY;
import static java.lang.Character.UnicodeBlock.GREEK;
import static java.lang.Character.UnicodeBlock.HANGUL_JAMO;
import static java.lang.Character.UnicodeBlock.HANGUL_JAMO_EXTENDED_B;
import static java.lang.Character.UnicodeBlock.MANDAIC;
import static java.lang.Character.UnicodeBlock.NKO;
import static java.lang.Character.UnicodeBlock.SAMARITAN;
import static java.lang.Character.UnicodeBlock.SUNDANESE;
import static java.lang.Character.UnicodeBlock.VARIATION_SELECTORS_SUPPLEMENT;
import static java.lang.Character.UnicodeBlock.forName;
import static java.lang.Character.UnicodeBlock.of;


public class CharacterTest extends TestCase {
    public void test_valueOfC() {
        // The JLS requires caching for chars between "\u0000 to \u007f":
        // http://java.sun.com/docs/books/jls/third_edition/html/conversions.html#5.1.7
        // Harmony caches 0-512 and tests for this behavior, so we suppress that test and use this.
        for (char c = '\u0000'; c <= '\u007f'; ++c) {
            Character e = new Character(c);
            Character a = Character.valueOf(c);
            TestCase.assertEquals(e, a);
            TestCase.assertSame(Character.valueOf(c), Character.valueOf(c));
        }
        for (int c = '\u0080'; c <= (Character.MAX_VALUE); ++c) {
            TestCase.assertEquals(new Character(((char) (c))), Character.valueOf(((char) (c))));
        }
    }

    public void test_isBmpCodePoint() throws Exception {
        TestCase.assertTrue(Character.isBmpCodePoint(0));
        TestCase.assertTrue(Character.isBmpCodePoint(1638));
        TestCase.assertTrue(Character.isBmpCodePoint(65535));
        TestCase.assertFalse(Character.isBmpCodePoint(65536));
        TestCase.assertFalse(Character.isBmpCodePoint((-1)));
        TestCase.assertFalse(Character.isBmpCodePoint(Integer.MAX_VALUE));
        TestCase.assertFalse(Character.isBmpCodePoint(Integer.MIN_VALUE));
    }

    public void test_isSurrogate() throws Exception {
        TestCase.assertFalse(Character.isSurrogate('\u0000'));
        TestCase.assertFalse(Character.isSurrogate('\u0666'));
        TestCase.assertFalse(Character.isSurrogate(((char) ((Character.MIN_SURROGATE) - 1))));
        for (char ch = Character.MIN_SURROGATE; ch <= (Character.MAX_SURROGATE); ++ch) {
            TestCase.assertTrue(Character.isSurrogate(ch));
        }
        TestCase.assertFalse(Character.isSurrogate(((char) ((Character.MAX_SURROGATE) + 1))));
    }

    public void test_highSurrogate() throws Exception {
        // The behavior for non-supplementary code points (like these two) is undefined.
        // These are the obvious results if you don't do anything special.
        TestCase.assertEquals(55232, Character.highSurrogate(0));
        TestCase.assertEquals(55233, Character.highSurrogate(1638));
        // These two tests must pass, though.
        TestCase.assertEquals(55296, Character.highSurrogate(65536));
        TestCase.assertEquals(56319, Character.highSurrogate(1114111));
    }

    public void test_lowSurrogate() throws Exception {
        // The behavior for non-supplementary code points (like these two) is undefined.
        // These are the obvious results if you don't do anything special.
        TestCase.assertEquals(56320, Character.lowSurrogate(0));
        TestCase.assertEquals(56934, Character.lowSurrogate(1638));
        // These two tests must pass, though.
        TestCase.assertEquals(56320, Character.lowSurrogate(65536));
        TestCase.assertEquals(57343, Character.lowSurrogate(1114111));
    }

    public void test_getName() throws Exception {
        // Character.getName requires the corresponding ICU data.
        // Changed from "NULL" and "BELL" by Unicode 49.2
        TestCase.assertEquals("<control-0000>", Character.getName(0));
        TestCase.assertEquals("<control-0007>", Character.getName(7));
        TestCase.assertEquals("LATIN SMALL LETTER L", Character.getName('l'));
        // This changed name from Unicode 1.0. Used to be "OPENING...".
        TestCase.assertEquals("LEFT CURLY BRACKET", Character.getName('{'));
        TestCase.assertEquals("ARABIC-INDIC DIGIT SIX", Character.getName(1638));
        TestCase.assertEquals("LINEAR B SYLLABLE B008 A", Character.getName(65536));
        // Some private use code points.
        TestCase.assertEquals("PRIVATE USE AREA E000", Character.getName(57344));
        TestCase.assertEquals("SUPPLEMENTARY PRIVATE USE AREA A F0000", Character.getName(983040));
        // An unassigned code point.
        TestCase.assertNull(Character.getName(1114111));
        try {
            Character.getName((-1));
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Character.getName(Integer.MAX_VALUE);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Character.getName(Integer.MIN_VALUE);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void test_compare() throws Exception {
        TestCase.assertEquals(0, Character.compare('a', 'a'));
        TestCase.assertTrue(((Character.compare('a', 'b')) < 0));
        TestCase.assertTrue(((Character.compare('b', 'a')) > 0));
    }

    public void test_UnicodeBlock_all() throws Exception {
        for (int i = 0; i <= 1048576; ++i) {
            of(i);
        }
    }

    public void test_UnicodeBlock_of() throws Exception {
        TestCase.assertEquals(BASIC_LATIN, of(1));
        TestCase.assertEquals(HANGUL_JAMO, of(4352));
        TestCase.assertEquals(CYPRIOT_SYLLABARY, of(67584));
        TestCase.assertEquals(VARIATION_SELECTORS_SUPPLEMENT, of(917760));
        // Unicode 4.1.
        TestCase.assertEquals(ANCIENT_GREEK_MUSICAL_NOTATION, of(119296));
        // Unicode 5.0.
        TestCase.assertEquals(NKO, of(1984));
        // Unicode 5.1.
        TestCase.assertEquals(SUNDANESE, of(7040));
        // Unicode 5.2.
        TestCase.assertEquals(SAMARITAN, of(2048));
        // Unicode 6.0.
        TestCase.assertEquals(MANDAIC, of(2112));
    }

    public void test_UnicodeBlock_forName() throws Exception {
        // No negative tests here because icu4c is more lenient than the RI;
        // we'd allow "basic-latin", and "hangul jamo extended b", for example.
        TestCase.assertEquals(BASIC_LATIN, forName("basic latin"));
        TestCase.assertEquals(BASIC_LATIN, forName("BaSiC LaTiN"));
        TestCase.assertEquals(BASIC_LATIN, forName("BasicLatin"));
        TestCase.assertEquals(BASIC_LATIN, forName("BASIC_LATIN"));
        TestCase.assertEquals(BASIC_LATIN, forName("basic_LATIN"));
        TestCase.assertEquals(HANGUL_JAMO_EXTENDED_B, forName("HANGUL_JAMO_EXTENDED_B"));
        TestCase.assertEquals(HANGUL_JAMO_EXTENDED_B, forName("HANGUL JAMO EXTENDED-B"));
        // Failure cases.
        try {
            forName(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        try {
            forName("this unicode block does not exist");
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        // Renamed blocks.
        TestCase.assertEquals(GREEK, forName("Greek"));
        TestCase.assertEquals(GREEK, forName("Greek And Coptic"));
        TestCase.assertEquals(COMBINING_MARKS_FOR_SYMBOLS, forName("Combining Marks For Symbols"));
        TestCase.assertEquals(COMBINING_MARKS_FOR_SYMBOLS, forName("Combining Diacritical Marks For Symbols"));
        TestCase.assertEquals(COMBINING_MARKS_FOR_SYMBOLS, forName("COMBINING_MARKS_FOR_SYMBOLS"));
        TestCase.assertEquals(COMBINING_MARKS_FOR_SYMBOLS, forName("Combining Marks for Symbols"));
        TestCase.assertEquals(COMBINING_MARKS_FOR_SYMBOLS, forName("CombiningMarksforSymbols"));
        TestCase.assertEquals(CYRILLIC_SUPPLEMENTARY, forName("Cyrillic Supplementary"));
        TestCase.assertEquals(CYRILLIC_SUPPLEMENTARY, forName("Cyrillic Supplement"));
    }

    public void test_isAlphabetic() throws Exception {
        TestCase.assertTrue(Character.isAlphabetic('A'));
        TestCase.assertTrue(Character.isAlphabetic('a'));
        TestCase.assertFalse(Character.isAlphabetic('1'));
        TestCase.assertTrue(Character.isAlphabetic(4412));// Hangul j

    }

    public void test_isIdeographic() throws Exception {
        TestCase.assertFalse(Character.isIdeographic('A'));
        TestCase.assertFalse(Character.isIdeographic('a'));
        TestCase.assertFalse(Character.isIdeographic('1'));
        TestCase.assertFalse(Character.isIdeographic(4412));// Hangul j

        TestCase.assertTrue(Character.isIdeographic(19893));
        TestCase.assertTrue(Character.isIdeographic(194969));
        TestCase.assertFalse(Character.isIdeographic(12185));// Kangxi radical shell

    }

    // http://b/9690863
    public void test_isDigit_against_icu4c() throws Exception {
        Method m = Character.class.getDeclaredMethod(("isDigit" + "Impl"), int.class);
        m.setAccessible(true);
        for (int i = 0; i <= 65535; ++i) {
            TestCase.assertEquals(m.invoke(null, i), Character.isDigit(i));
        }
    }

    // http://b/9690863
    public void test_isIdentifierIgnorable_against_icu4c() throws Exception {
        Method m = Character.class.getDeclaredMethod(("isIdentifierIgnorable" + "Impl"), int.class);
        m.setAccessible(true);
        for (int i = 0; i <= 65535; ++i) {
            TestCase.assertEquals(m.invoke(null, i), Character.isIdentifierIgnorable(i));
        }
    }

    // http://b/9690863
    public void test_isLetter_against_icu4c() throws Exception {
        Method m = Character.class.getDeclaredMethod(("isLetter" + "Impl"), int.class);
        m.setAccessible(true);
        for (int i = 0; i <= 65535; ++i) {
            TestCase.assertEquals(m.invoke(null, i), Character.isLetter(i));
        }
    }

    // http://b/9690863
    public void test_isLetterOrDigit_against_icu4c() throws Exception {
        Method m = Character.class.getDeclaredMethod(("isLetterOrDigit" + "Impl"), int.class);
        m.setAccessible(true);
        for (int i = 0; i <= 65535; ++i) {
            TestCase.assertEquals(m.invoke(null, i), Character.isLetterOrDigit(i));
        }
    }

    // http://b/9690863
    public void test_isLowerCase_against_icu4c() throws Exception {
        Method m = Character.class.getDeclaredMethod(("isLowerCase" + "Impl"), int.class);
        m.setAccessible(true);
        for (int i = 0; i <= 65535; ++i) {
            TestCase.assertEquals(m.invoke(null, i), Character.isLowerCase(i));
        }
    }

    // http://b/9690863
    public void test_isSpaceChar_against_icu4c() throws Exception {
        Method m = Character.class.getDeclaredMethod(("isSpaceChar" + "Impl"), int.class);
        m.setAccessible(true);
        for (int i = 0; i <= 65535; ++i) {
            if (((Boolean) (m.invoke(null, i))) != (Character.isSpaceChar(i)))
                System.out.println(i);

        }
    }

    // http://b/9690863
    public void test_isUpperCase_against_icu4c() throws Exception {
        Method m = Character.class.getDeclaredMethod(("isUpperCase" + "Impl"), int.class);
        m.setAccessible(true);
        for (int i = 0; i <= 65535; ++i) {
            TestCase.assertEquals(m.invoke(null, i), Character.isUpperCase(i));
        }
    }

    // http://b/9690863
    public void test_isWhitespace_against_icu4c() throws Exception {
        Method m = Character.class.getDeclaredMethod(("isWhitespace" + "Impl"), int.class);
        m.setAccessible(true);
        for (int i = 0; i <= 65535; ++i) {
            TestCase.assertEquals(m.invoke(null, i), Character.isWhitespace(i));
        }
    }
}

