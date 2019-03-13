/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2006 Daniel Naber (http://www.danielnaber.de)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool.tools;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.FakeLanguage;
import org.languagetool.Language;
import org.languagetool.TestTools;


/**
 *
 *
 * @author Daniel Naber
 */
public class StringToolsTest {
    @Test
    public void testAssureSet() {
        try {
            StringTools.assureSet("", "varName");
            Assert.fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            StringTools.assureSet(" \t", "varName");
            Assert.fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            StringTools.assureSet(null, "varName");
            Assert.fail();
        } catch (NullPointerException ignored) {
        }
        StringTools.assureSet("foo", "varName");
    }

    @Test
    public void testReadStream() throws IOException {
        String content = StringTools.readStream(new FileInputStream("src/test/resources/testinput.txt"), "utf-8");
        Assert.assertEquals("one\ntwo\n\u00f6\u00e4\u00fc\u00df\n\u0219\u021b\u00ee\u00e2\u0103\u0218\u021a\u00ce\u00c2\u0102\n", content);
    }

    @Test
    public void testIsAllUppercase() {
        Assert.assertTrue(StringTools.isAllUppercase("A"));
        Assert.assertTrue(StringTools.isAllUppercase("ABC"));
        Assert.assertTrue(StringTools.isAllUppercase("ASV-EDR"));
        Assert.assertTrue(StringTools.isAllUppercase("ASV-???"));
        Assert.assertTrue(StringTools.isAllUppercase(""));
        Assert.assertFalse(StringTools.isAllUppercase("?"));
        Assert.assertFalse(StringTools.isAllUppercase("AAAAAAAAAAAAq"));
        Assert.assertFalse(StringTools.isAllUppercase("a"));
        Assert.assertFalse(StringTools.isAllUppercase("abc"));
    }

    @Test
    public void testIsMixedCase() {
        Assert.assertTrue(StringTools.isMixedCase("AbC"));
        Assert.assertTrue(StringTools.isMixedCase("MixedCase"));
        Assert.assertTrue(StringTools.isMixedCase("iPod"));
        Assert.assertTrue(StringTools.isMixedCase("AbCdE"));
        Assert.assertFalse(StringTools.isMixedCase(""));
        Assert.assertFalse(StringTools.isMixedCase("ABC"));
        Assert.assertFalse(StringTools.isMixedCase("abc"));
        Assert.assertFalse(StringTools.isMixedCase("!"));
        Assert.assertFalse(StringTools.isMixedCase("Word"));
    }

    @Test
    public void testIsCapitalizedWord() {
        Assert.assertTrue(StringTools.isCapitalizedWord("Abc"));
        Assert.assertTrue(StringTools.isCapitalizedWord("Uppercase"));
        Assert.assertTrue(StringTools.isCapitalizedWord("Ipod"));
        Assert.assertFalse(StringTools.isCapitalizedWord(""));
        Assert.assertFalse(StringTools.isCapitalizedWord("ABC"));
        Assert.assertFalse(StringTools.isCapitalizedWord("abc"));
        Assert.assertFalse(StringTools.isCapitalizedWord("!"));
        Assert.assertFalse(StringTools.isCapitalizedWord("wOrD"));
    }

    @Test
    public void testStartsWithUppercase() {
        Assert.assertTrue(StringTools.startsWithUppercase("A"));
        Assert.assertTrue(StringTools.startsWithUppercase("??"));
        Assert.assertFalse(StringTools.startsWithUppercase(""));
        Assert.assertFalse(StringTools.startsWithUppercase("?"));
        Assert.assertFalse(StringTools.startsWithUppercase("-"));
    }

    @Test
    public void testUppercaseFirstChar() {
        Assert.assertEquals(null, StringTools.uppercaseFirstChar(null));
        Assert.assertEquals("", StringTools.uppercaseFirstChar(""));
        Assert.assertEquals("A", StringTools.uppercaseFirstChar("A"));
        Assert.assertEquals("???", StringTools.uppercaseFirstChar("???"));
        Assert.assertEquals("?a", StringTools.uppercaseFirstChar("?a"));
        Assert.assertEquals("'Test'", StringTools.uppercaseFirstChar("'test'"));
        Assert.assertEquals("''Test", StringTools.uppercaseFirstChar("''test"));
        Assert.assertEquals("''T", StringTools.uppercaseFirstChar("''t"));
        Assert.assertEquals("'''", StringTools.uppercaseFirstChar("'''"));
    }

    @Test
    public void testLowercaseFirstChar() {
        Assert.assertEquals(null, StringTools.lowercaseFirstChar(null));
        Assert.assertEquals("", StringTools.lowercaseFirstChar(""));
        Assert.assertEquals("a", StringTools.lowercaseFirstChar("A"));
        Assert.assertEquals("???", StringTools.lowercaseFirstChar("???"));
        Assert.assertEquals("?a", StringTools.lowercaseFirstChar("?a"));
        Assert.assertEquals("'test'", StringTools.lowercaseFirstChar("'Test'"));
        Assert.assertEquals("''test", StringTools.lowercaseFirstChar("''Test"));
        Assert.assertEquals("''t", StringTools.lowercaseFirstChar("''T"));
        Assert.assertEquals("'''", StringTools.lowercaseFirstChar("'''"));
    }

    @Test
    public void testReaderToString() throws IOException {
        String str = StringTools.readerToString(new StringReader("bla\n\u00f6\u00e4\u00fc"));
        Assert.assertEquals("bla\n\u00f6\u00e4\u00fc", str);
        StringBuilder longStr = new StringBuilder();
        for (int i = 0; i < 4000; i++) {
            longStr.append("x");
        }
        longStr.append("1234567");
        Assert.assertEquals(4007, longStr.length());
        String str2 = StringTools.readerToString(new StringReader(longStr.toString()));
        Assert.assertEquals(longStr.toString(), str2);
    }

    @Test
    public void testEscapeXMLandHTML() {
        Assert.assertEquals("foo bar", StringTools.escapeXML("foo bar"));
        Assert.assertEquals("!?&quot;&lt;&gt;&amp;&amp;", StringTools.escapeXML("!\u00e4\"<>&&"));
        Assert.assertEquals("!?&quot;&lt;&gt;&amp;&amp;", StringTools.escapeHTML("!\u00e4\"<>&&"));
    }

    @Test
    public void testListToString() {
        List<String> list = new ArrayList<>();
        list.add("foo");
        list.add("bar");
        list.add(",");
        Assert.assertEquals("foo,bar,,", String.join(",", list));
        Assert.assertEquals("foo\tbar\t,", String.join("\t", list));
    }

    @Test
    public void testTrimWhitespace() {
        try {
            Assert.assertEquals(null, StringTools.trimWhitespace(null));
            Assert.fail();
        } catch (NullPointerException ignored) {
        }
        Assert.assertEquals("", StringTools.trimWhitespace(""));
        Assert.assertEquals("", StringTools.trimWhitespace(" "));
        Assert.assertEquals("XXY", StringTools.trimWhitespace(" \nXX\t Y"));
        Assert.assertEquals("XXY", StringTools.trimWhitespace(" \r\nXX\t Y"));
        Assert.assertEquals("word", StringTools.trimWhitespace("word"));
        // only one space in the middle of the word is significant:
        Assert.assertEquals("1 234,56", StringTools.trimWhitespace("1 234,56"));
        Assert.assertEquals("1234,56", StringTools.trimWhitespace("1  234,56"));
    }

    @Test
    public void testAddSpace() {
        Language demoLanguage = TestTools.getDemoLanguage();
        Assert.assertEquals(" ", StringTools.addSpace("word", demoLanguage));
        Assert.assertEquals("", StringTools.addSpace(",", demoLanguage));
        Assert.assertEquals("", StringTools.addSpace(",", demoLanguage));
        Assert.assertEquals("", StringTools.addSpace(",", demoLanguage));
        Assert.assertEquals("", StringTools.addSpace(".", new FakeLanguage("fr")));
        Assert.assertEquals("", StringTools.addSpace(".", new FakeLanguage("de")));
        Assert.assertEquals(" ", StringTools.addSpace("!", new FakeLanguage("fr")));
        Assert.assertEquals("", StringTools.addSpace("!", new FakeLanguage("de")));
    }

    @Test
    public void testIsWhitespace() {
        Assert.assertEquals(true, StringTools.isWhitespace("  "));
        Assert.assertEquals(true, StringTools.isWhitespace("\t"));
        Assert.assertEquals(true, StringTools.isWhitespace("\u2002"));
        // non-breaking space is also a whitespace
        Assert.assertEquals(true, StringTools.isWhitespace("\u00a0"));
        Assert.assertEquals(false, StringTools.isWhitespace("abc"));
        // non-breaking OOo field
        Assert.assertEquals(false, StringTools.isWhitespace("\\u02"));
        Assert.assertEquals(false, StringTools.isWhitespace("\u0001"));
        // narrow nbsp:
        Assert.assertEquals(true, StringTools.isWhitespace("\u202f"));
    }

    @Test
    public void testIsPositiveNumber() {
        Assert.assertEquals(true, StringTools.isPositiveNumber('3'));
        Assert.assertEquals(false, StringTools.isPositiveNumber('a'));
    }

    @Test
    public void testIsEmpty() {
        Assert.assertEquals(true, StringTools.isEmpty(""));
        Assert.assertEquals(true, StringTools.isEmpty(null));
        Assert.assertEquals(false, StringTools.isEmpty("a"));
    }

    @Test
    public void testFilterXML() {
        Assert.assertEquals("test", StringTools.filterXML("test"));
        Assert.assertEquals("<<test>>", StringTools.filterXML("<<test>>"));
        Assert.assertEquals("test", StringTools.filterXML("<b>test</b>"));
        Assert.assertEquals("A sentence with a test", StringTools.filterXML("A sentence with a <em>test</em>"));
    }

    @Test
    public void testAsString() {
        Assert.assertNull(StringTools.asString(null));
        Assert.assertEquals("foo!", "foo!");
    }
}

