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
package org.apache.commons.lang3.text;


import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Unit tests for WordUtils class.
 */
@Deprecated
public class WordUtilsTest {
    // -----------------------------------------------------------------------
    @Test
    public void testConstructor() {
        Assertions.assertNotNull(new WordUtils());
        final Constructor<?>[] cons = WordUtils.class.getDeclaredConstructors();
        Assertions.assertEquals(1, cons.length);
        Assertions.assertTrue(Modifier.isPublic(cons[0].getModifiers()));
        Assertions.assertTrue(Modifier.isPublic(WordUtils.class.getModifiers()));
        Assertions.assertFalse(Modifier.isFinal(WordUtils.class.getModifiers()));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testWrap_StringInt() {
        Assertions.assertNull(WordUtils.wrap(null, 20));
        Assertions.assertNull(WordUtils.wrap(null, (-1)));
        Assertions.assertEquals("", WordUtils.wrap("", 20));
        Assertions.assertEquals("", WordUtils.wrap("", (-1)));
        // normal
        final String systemNewLine = System.lineSeparator();
        String input = "Here is one line of text that is going to be wrapped after 20 columns.";
        String expected = ((((("Here is one line of" + systemNewLine) + "text that is going") + systemNewLine) + "to be wrapped after") + systemNewLine) + "20 columns.";
        Assertions.assertEquals(expected, WordUtils.wrap(input, 20));
        // long word at end
        input = "Click here to jump to the commons website - http://commons.apache.org";
        expected = ((((("Click here to jump" + systemNewLine) + "to the commons") + systemNewLine) + "website -") + systemNewLine) + "http://commons.apache.org";
        Assertions.assertEquals(expected, WordUtils.wrap(input, 20));
        // long word in middle
        input = "Click here, http://commons.apache.org, to jump to the commons website";
        expected = ((((("Click here," + systemNewLine) + "http://commons.apache.org,") + systemNewLine) + "to jump to the") + systemNewLine) + "commons website";
        Assertions.assertEquals(expected, WordUtils.wrap(input, 20));
        // leading spaces on a new line are stripped
        // trailing spaces are not stripped
        input = "word1             word2                        word3";
        expected = ((("word1  " + systemNewLine) + "word2  ") + systemNewLine) + "word3";
        Assertions.assertEquals(expected, WordUtils.wrap(input, 7));
    }

    @Test
    public void testWrap_StringIntStringBoolean() {
        Assertions.assertNull(WordUtils.wrap(null, 20, "\n", false));
        Assertions.assertNull(WordUtils.wrap(null, 20, "\n", true));
        Assertions.assertNull(WordUtils.wrap(null, 20, null, true));
        Assertions.assertNull(WordUtils.wrap(null, 20, null, false));
        Assertions.assertNull(WordUtils.wrap(null, (-1), null, true));
        Assertions.assertNull(WordUtils.wrap(null, (-1), null, false));
        Assertions.assertEquals("", WordUtils.wrap("", 20, "\n", false));
        Assertions.assertEquals("", WordUtils.wrap("", 20, "\n", true));
        Assertions.assertEquals("", WordUtils.wrap("", 20, null, false));
        Assertions.assertEquals("", WordUtils.wrap("", 20, null, true));
        Assertions.assertEquals("", WordUtils.wrap("", (-1), null, false));
        Assertions.assertEquals("", WordUtils.wrap("", (-1), null, true));
        // normal
        String input = "Here is one line of text that is going to be wrapped after 20 columns.";
        String expected = "Here is one line of\ntext that is going\nto be wrapped after\n20 columns.";
        Assertions.assertEquals(expected, WordUtils.wrap(input, 20, "\n", false));
        Assertions.assertEquals(expected, WordUtils.wrap(input, 20, "\n", true));
        // unusual newline char
        input = "Here is one line of text that is going to be wrapped after 20 columns.";
        expected = "Here is one line of<br />text that is going<br />to be wrapped after<br />20 columns.";
        Assertions.assertEquals(expected, WordUtils.wrap(input, 20, "<br />", false));
        Assertions.assertEquals(expected, WordUtils.wrap(input, 20, "<br />", true));
        // short line length
        input = "Here is one line";
        expected = "Here\nis one\nline";
        Assertions.assertEquals(expected, WordUtils.wrap(input, 6, "\n", false));
        expected = "Here\nis\none\nline";
        Assertions.assertEquals(expected, WordUtils.wrap(input, 2, "\n", false));
        Assertions.assertEquals(expected, WordUtils.wrap(input, (-1), "\n", false));
        // system newline char
        final String systemNewLine = System.lineSeparator();
        input = "Here is one line of text that is going to be wrapped after 20 columns.";
        expected = ((((("Here is one line of" + systemNewLine) + "text that is going") + systemNewLine) + "to be wrapped after") + systemNewLine) + "20 columns.";
        Assertions.assertEquals(expected, WordUtils.wrap(input, 20, null, false));
        Assertions.assertEquals(expected, WordUtils.wrap(input, 20, null, true));
        // with extra spaces
        input = " Here:  is  one  line  of  text  that  is  going  to  be  wrapped  after  20  columns.";
        expected = "Here:  is  one  line\nof  text  that  is \ngoing  to  be \nwrapped  after  20 \ncolumns.";
        Assertions.assertEquals(expected, WordUtils.wrap(input, 20, "\n", false));
        Assertions.assertEquals(expected, WordUtils.wrap(input, 20, "\n", true));
        // with tab
        input = "Here is\tone line of text that is going to be wrapped after 20 columns.";
        expected = "Here is\tone line of\ntext that is going\nto be wrapped after\n20 columns.";
        Assertions.assertEquals(expected, WordUtils.wrap(input, 20, "\n", false));
        Assertions.assertEquals(expected, WordUtils.wrap(input, 20, "\n", true));
        // with tab at wrapColumn
        input = "Here is one line of\ttext that is going to be wrapped after 20 columns.";
        expected = "Here is one line\nof\ttext that is\ngoing to be wrapped\nafter 20 columns.";
        Assertions.assertEquals(expected, WordUtils.wrap(input, 20, "\n", false));
        Assertions.assertEquals(expected, WordUtils.wrap(input, 20, "\n", true));
        // difference because of long word
        input = "Click here to jump to the commons website - http://commons.apache.org";
        expected = "Click here to jump\nto the commons\nwebsite -\nhttp://commons.apache.org";
        Assertions.assertEquals(expected, WordUtils.wrap(input, 20, "\n", false));
        expected = "Click here to jump\nto the commons\nwebsite -\nhttp://commons.apach\ne.org";
        Assertions.assertEquals(expected, WordUtils.wrap(input, 20, "\n", true));
        // difference because of long word in middle
        input = "Click here, http://commons.apache.org, to jump to the commons website";
        expected = "Click here,\nhttp://commons.apache.org,\nto jump to the\ncommons website";
        Assertions.assertEquals(expected, WordUtils.wrap(input, 20, "\n", false));
        expected = "Click here,\nhttp://commons.apach\ne.org, to jump to\nthe commons website";
        Assertions.assertEquals(expected, WordUtils.wrap(input, 20, "\n", true));
    }

    @Test
    public void testWrap_StringIntStringBooleanString() {
        // no changes test
        String input = "flammable/inflammable";
        String expected = "flammable/inflammable";
        Assertions.assertEquals(expected, WordUtils.wrap(input, 30, "\n", false, "/"));
        // wrap on / and small width
        expected = "flammable\ninflammable";
        Assertions.assertEquals(expected, WordUtils.wrap(input, 2, "\n", false, "/"));
        // wrap long words on / 1
        expected = "flammable\ninflammab\nle";
        Assertions.assertEquals(expected, WordUtils.wrap(input, 9, "\n", true, "/"));
        // wrap long words on / 2
        expected = "flammable\ninflammable";
        Assertions.assertEquals(expected, WordUtils.wrap(input, 15, "\n", true, "/"));
        // wrap long words on / 3
        input = "flammableinflammable";
        expected = "flammableinflam\nmable";
        Assertions.assertEquals(expected, WordUtils.wrap(input, 15, "\n", true, "/"));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testCapitalize_String() {
        Assertions.assertNull(WordUtils.capitalize(null));
        Assertions.assertEquals("", WordUtils.capitalize(""));
        Assertions.assertEquals("  ", WordUtils.capitalize("  "));
        Assertions.assertEquals("I", WordUtils.capitalize("I"));
        Assertions.assertEquals("I", WordUtils.capitalize("i"));
        Assertions.assertEquals("I Am Here 123", WordUtils.capitalize("i am here 123"));
        Assertions.assertEquals("I Am Here 123", WordUtils.capitalize("I Am Here 123"));
        Assertions.assertEquals("I Am HERE 123", WordUtils.capitalize("i am HERE 123"));
        Assertions.assertEquals("I AM HERE 123", WordUtils.capitalize("I AM HERE 123"));
    }

    @Test
    public void testCapitalizeWithDelimiters_String() {
        Assertions.assertNull(WordUtils.capitalize(null, null));
        Assertions.assertEquals("", WordUtils.capitalize("", new char[0]));
        Assertions.assertEquals("  ", WordUtils.capitalize("  ", new char[0]));
        char[] chars = new char[]{ '-', '+', ' ', '@' };
        Assertions.assertEquals("I", WordUtils.capitalize("I", chars));
        Assertions.assertEquals("I", WordUtils.capitalize("i", chars));
        Assertions.assertEquals("I-Am Here+123", WordUtils.capitalize("i-am here+123", chars));
        Assertions.assertEquals("I Am+Here-123", WordUtils.capitalize("I Am+Here-123", chars));
        Assertions.assertEquals("I+Am-HERE 123", WordUtils.capitalize("i+am-HERE 123", chars));
        Assertions.assertEquals("I-AM HERE+123", WordUtils.capitalize("I-AM HERE+123", chars));
        chars = new char[]{ '.' };
        Assertions.assertEquals("I aM.Fine", WordUtils.capitalize("i aM.fine", chars));
        Assertions.assertEquals("I Am.fine", WordUtils.capitalize("i am.fine", null));
    }

    @Test
    public void testCapitalizeFully_String() {
        Assertions.assertNull(WordUtils.capitalizeFully(null));
        Assertions.assertEquals("", WordUtils.capitalizeFully(""));
        Assertions.assertEquals("  ", WordUtils.capitalizeFully("  "));
        Assertions.assertEquals("I", WordUtils.capitalizeFully("I"));
        Assertions.assertEquals("I", WordUtils.capitalizeFully("i"));
        Assertions.assertEquals("I Am Here 123", WordUtils.capitalizeFully("i am here 123"));
        Assertions.assertEquals("I Am Here 123", WordUtils.capitalizeFully("I Am Here 123"));
        Assertions.assertEquals("I Am Here 123", WordUtils.capitalizeFully("i am HERE 123"));
        Assertions.assertEquals("I Am Here 123", WordUtils.capitalizeFully("I AM HERE 123"));
    }

    @Test
    public void testCapitalizeFullyWithDelimiters_String() {
        Assertions.assertNull(WordUtils.capitalizeFully(null, null));
        Assertions.assertEquals("", WordUtils.capitalizeFully("", new char[0]));
        Assertions.assertEquals("  ", WordUtils.capitalizeFully("  ", new char[0]));
        char[] chars = new char[]{ '-', '+', ' ', '@' };
        Assertions.assertEquals("I", WordUtils.capitalizeFully("I", chars));
        Assertions.assertEquals("I", WordUtils.capitalizeFully("i", chars));
        Assertions.assertEquals("I-Am Here+123", WordUtils.capitalizeFully("i-am here+123", chars));
        Assertions.assertEquals("I Am+Here-123", WordUtils.capitalizeFully("I Am+Here-123", chars));
        Assertions.assertEquals("I+Am-Here 123", WordUtils.capitalizeFully("i+am-HERE 123", chars));
        Assertions.assertEquals("I-Am Here+123", WordUtils.capitalizeFully("I-AM HERE+123", chars));
        chars = new char[]{ '.' };
        Assertions.assertEquals("I am.Fine", WordUtils.capitalizeFully("i aM.fine", chars));
        Assertions.assertEquals("I Am.fine", WordUtils.capitalizeFully("i am.fine", null));
    }

    @Test
    public void testContainsAllWords_StringString() {
        Assertions.assertFalse(WordUtils.containsAllWords(null, ((String) (null))));
        Assertions.assertFalse(WordUtils.containsAllWords(null, ""));
        Assertions.assertFalse(WordUtils.containsAllWords(null, "ab"));
        Assertions.assertFalse(WordUtils.containsAllWords("", ((String) (null))));
        Assertions.assertFalse(WordUtils.containsAllWords("", ""));
        Assertions.assertFalse(WordUtils.containsAllWords("", "ab"));
        Assertions.assertFalse(WordUtils.containsAllWords("foo", ((String) (null))));
        Assertions.assertFalse(WordUtils.containsAllWords("bar", ""));
        Assertions.assertFalse(WordUtils.containsAllWords("zzabyycdxx", "by"));
        Assertions.assertTrue(WordUtils.containsAllWords("lorem ipsum dolor sit amet", "ipsum", "lorem", "dolor"));
        Assertions.assertFalse(WordUtils.containsAllWords("lorem ipsum dolor sit amet", "ipsum", null, "lorem", "dolor"));
        Assertions.assertFalse(WordUtils.containsAllWords("lorem ipsum null dolor sit amet", "ipsum", null, "lorem", "dolor"));
        Assertions.assertFalse(WordUtils.containsAllWords("ab", "b"));
        Assertions.assertFalse(WordUtils.containsAllWords("ab", "z"));
    }

    @Test
    public void testUncapitalize_String() {
        Assertions.assertNull(WordUtils.uncapitalize(null));
        Assertions.assertEquals("", WordUtils.uncapitalize(""));
        Assertions.assertEquals("  ", WordUtils.uncapitalize("  "));
        Assertions.assertEquals("i", WordUtils.uncapitalize("I"));
        Assertions.assertEquals("i", WordUtils.uncapitalize("i"));
        Assertions.assertEquals("i am here 123", WordUtils.uncapitalize("i am here 123"));
        Assertions.assertEquals("i am here 123", WordUtils.uncapitalize("I Am Here 123"));
        Assertions.assertEquals("i am hERE 123", WordUtils.uncapitalize("i am HERE 123"));
        Assertions.assertEquals("i aM hERE 123", WordUtils.uncapitalize("I AM HERE 123"));
    }

    @Test
    public void testUncapitalizeWithDelimiters_String() {
        Assertions.assertNull(WordUtils.uncapitalize(null, null));
        Assertions.assertEquals("", WordUtils.uncapitalize("", new char[0]));
        Assertions.assertEquals("  ", WordUtils.uncapitalize("  ", new char[0]));
        char[] chars = new char[]{ '-', '+', ' ', '@' };
        Assertions.assertEquals("i", WordUtils.uncapitalize("I", chars));
        Assertions.assertEquals("i", WordUtils.uncapitalize("i", chars));
        Assertions.assertEquals("i am-here+123", WordUtils.uncapitalize("i am-here+123", chars));
        Assertions.assertEquals("i+am here-123", WordUtils.uncapitalize("I+Am Here-123", chars));
        Assertions.assertEquals("i-am+hERE 123", WordUtils.uncapitalize("i-am+HERE 123", chars));
        Assertions.assertEquals("i aM-hERE+123", WordUtils.uncapitalize("I AM-HERE+123", chars));
        chars = new char[]{ '.' };
        Assertions.assertEquals("i AM.fINE", WordUtils.uncapitalize("I AM.FINE", chars));
        Assertions.assertEquals("i aM.FINE", WordUtils.uncapitalize("I AM.FINE", null));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testInitials_String() {
        Assertions.assertNull(WordUtils.initials(null));
        Assertions.assertEquals("", WordUtils.initials(""));
        Assertions.assertEquals("", WordUtils.initials("  "));
        Assertions.assertEquals("I", WordUtils.initials("I"));
        Assertions.assertEquals("i", WordUtils.initials("i"));
        Assertions.assertEquals("BJL", WordUtils.initials("Ben John Lee"));
        Assertions.assertEquals("BJL", WordUtils.initials("   Ben \n   John\tLee\t"));
        Assertions.assertEquals("BJ", WordUtils.initials("Ben J.Lee"));
        Assertions.assertEquals("BJ.L", WordUtils.initials(" Ben   John  . Lee"));
        Assertions.assertEquals("iah1", WordUtils.initials("i am here 123"));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testInitials_String_charArray() {
        char[] array = null;
        Assertions.assertNull(WordUtils.initials(null, array));
        Assertions.assertEquals("", WordUtils.initials("", array));
        Assertions.assertEquals("", WordUtils.initials("  ", array));
        Assertions.assertEquals("I", WordUtils.initials("I", array));
        Assertions.assertEquals("i", WordUtils.initials("i", array));
        Assertions.assertEquals("S", WordUtils.initials("SJC", array));
        Assertions.assertEquals("BJL", WordUtils.initials("Ben John Lee", array));
        Assertions.assertEquals("BJL", WordUtils.initials("   Ben \n   John\tLee\t", array));
        Assertions.assertEquals("BJ", WordUtils.initials("Ben J.Lee", array));
        Assertions.assertEquals("BJ.L", WordUtils.initials(" Ben   John  . Lee", array));
        Assertions.assertEquals("KO", WordUtils.initials("Kay O'Murphy", array));
        Assertions.assertEquals("iah1", WordUtils.initials("i am here 123", array));
        array = new char[0];
        Assertions.assertNull(WordUtils.initials(null, array));
        Assertions.assertEquals("", WordUtils.initials("", array));
        Assertions.assertEquals("", WordUtils.initials("  ", array));
        Assertions.assertEquals("", WordUtils.initials("I", array));
        Assertions.assertEquals("", WordUtils.initials("i", array));
        Assertions.assertEquals("", WordUtils.initials("SJC", array));
        Assertions.assertEquals("", WordUtils.initials("Ben John Lee", array));
        Assertions.assertEquals("", WordUtils.initials("   Ben \n   John\tLee\t", array));
        Assertions.assertEquals("", WordUtils.initials("Ben J.Lee", array));
        Assertions.assertEquals("", WordUtils.initials(" Ben   John  . Lee", array));
        Assertions.assertEquals("", WordUtils.initials("Kay O'Murphy", array));
        Assertions.assertEquals("", WordUtils.initials("i am here 123", array));
        array = " ".toCharArray();
        Assertions.assertNull(WordUtils.initials(null, array));
        Assertions.assertEquals("", WordUtils.initials("", array));
        Assertions.assertEquals("", WordUtils.initials("  ", array));
        Assertions.assertEquals("I", WordUtils.initials("I", array));
        Assertions.assertEquals("i", WordUtils.initials("i", array));
        Assertions.assertEquals("S", WordUtils.initials("SJC", array));
        Assertions.assertEquals("BJL", WordUtils.initials("Ben John Lee", array));
        Assertions.assertEquals("BJ", WordUtils.initials("Ben J.Lee", array));
        Assertions.assertEquals("B\nJ", WordUtils.initials("   Ben \n   John\tLee\t", array));
        Assertions.assertEquals("BJ.L", WordUtils.initials(" Ben   John  . Lee", array));
        Assertions.assertEquals("KO", WordUtils.initials("Kay O'Murphy", array));
        Assertions.assertEquals("iah1", WordUtils.initials("i am here 123", array));
        array = " .".toCharArray();
        Assertions.assertNull(WordUtils.initials(null, array));
        Assertions.assertEquals("", WordUtils.initials("", array));
        Assertions.assertEquals("", WordUtils.initials("  ", array));
        Assertions.assertEquals("I", WordUtils.initials("I", array));
        Assertions.assertEquals("i", WordUtils.initials("i", array));
        Assertions.assertEquals("S", WordUtils.initials("SJC", array));
        Assertions.assertEquals("BJL", WordUtils.initials("Ben John Lee", array));
        Assertions.assertEquals("BJL", WordUtils.initials("Ben J.Lee", array));
        Assertions.assertEquals("BJL", WordUtils.initials(" Ben   John  . Lee", array));
        Assertions.assertEquals("KO", WordUtils.initials("Kay O'Murphy", array));
        Assertions.assertEquals("iah1", WordUtils.initials("i am here 123", array));
        array = " .'".toCharArray();
        Assertions.assertNull(WordUtils.initials(null, array));
        Assertions.assertEquals("", WordUtils.initials("", array));
        Assertions.assertEquals("", WordUtils.initials("  ", array));
        Assertions.assertEquals("I", WordUtils.initials("I", array));
        Assertions.assertEquals("i", WordUtils.initials("i", array));
        Assertions.assertEquals("S", WordUtils.initials("SJC", array));
        Assertions.assertEquals("BJL", WordUtils.initials("Ben John Lee", array));
        Assertions.assertEquals("BJL", WordUtils.initials("Ben J.Lee", array));
        Assertions.assertEquals("BJL", WordUtils.initials(" Ben   John  . Lee", array));
        Assertions.assertEquals("KOM", WordUtils.initials("Kay O'Murphy", array));
        Assertions.assertEquals("iah1", WordUtils.initials("i am here 123", array));
        array = "SIJo1".toCharArray();
        Assertions.assertNull(WordUtils.initials(null, array));
        Assertions.assertEquals("", WordUtils.initials("", array));
        Assertions.assertEquals(" ", WordUtils.initials("  ", array));
        Assertions.assertEquals("", WordUtils.initials("I", array));
        Assertions.assertEquals("i", WordUtils.initials("i", array));
        Assertions.assertEquals("C", WordUtils.initials("SJC", array));
        Assertions.assertEquals("Bh", WordUtils.initials("Ben John Lee", array));
        Assertions.assertEquals("B.", WordUtils.initials("Ben J.Lee", array));
        Assertions.assertEquals(" h", WordUtils.initials(" Ben   John  . Lee", array));
        Assertions.assertEquals("K", WordUtils.initials("Kay O'Murphy", array));
        Assertions.assertEquals("i2", WordUtils.initials("i am here 123", array));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testSwapCase_String() {
        Assertions.assertNull(WordUtils.swapCase(null));
        Assertions.assertEquals("", WordUtils.swapCase(""));
        Assertions.assertEquals("  ", WordUtils.swapCase("  "));
        Assertions.assertEquals("i", WordUtils.swapCase("I"));
        Assertions.assertEquals("I", WordUtils.swapCase("i"));
        Assertions.assertEquals("I AM HERE 123", WordUtils.swapCase("i am here 123"));
        Assertions.assertEquals("i aM hERE 123", WordUtils.swapCase("I Am Here 123"));
        Assertions.assertEquals("I AM here 123", WordUtils.swapCase("i am HERE 123"));
        Assertions.assertEquals("i am here 123", WordUtils.swapCase("I AM HERE 123"));
        final String test = "This String contains a TitleCase character: \u01c8";
        final String expect = "tHIS sTRING CONTAINS A tITLEcASE CHARACTER: \u01c9";
        Assertions.assertEquals(expect, WordUtils.swapCase(test));
    }

    @Test
    public void testLANG1292() {
        // Prior to fix, this was throwing StringIndexOutOfBoundsException
        WordUtils.wrap(("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa " + ("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa " + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")), 70);
    }

    @Test
    public void testLANG1397() {
        // Prior to fix, this was throwing StringIndexOutOfBoundsException
        WordUtils.wrap(("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa " + ("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa " + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")), Integer.MAX_VALUE);
    }
}

