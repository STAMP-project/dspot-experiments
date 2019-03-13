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
package org.apache.camel.util;


import java.util.List;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for StringHelper
 */
public class StringHelperTest extends Assert {
    @Test
    public void testSimpleSanitized() {
        String out = StringHelper.sanitize("hello");
        Assert.assertTrue("Should not contain : ", ((out.indexOf(':')) == (-1)));
        Assert.assertTrue("Should not contain . ", ((out.indexOf('.')) == (-1)));
    }

    @Test
    public void testNotFileFriendlySimpleSanitized() {
        String out = StringHelper.sanitize("c:\\helloworld");
        Assert.assertTrue("Should not contain : ", ((out.indexOf(':')) == (-1)));
        Assert.assertTrue("Should not contain . ", ((out.indexOf('.')) == (-1)));
    }

    @Test
    public void testSimpleCRLF() {
        String out = StringHelper.removeCRLF("hello");
        Assert.assertEquals("hello", out);
        Assert.assertTrue("Should not contain : ", (!(out.contains("\r"))));
        Assert.assertTrue("Should not contain : ", (!(out.contains("\n"))));
        out = StringHelper.removeCRLF("hello\r\n");
        Assert.assertEquals("hello", out);
        Assert.assertTrue("Should not contain : ", (!(out.contains("\r"))));
        Assert.assertTrue("Should not contain : ", (!(out.contains("\n"))));
        out = StringHelper.removeCRLF("\r\nhe\r\nllo\n");
        Assert.assertEquals("hello", out);
        Assert.assertTrue("Should not contain : ", (!(out.contains("\r"))));
        Assert.assertTrue("Should not contain : ", (!(out.contains("\n"))));
        out = StringHelper.removeCRLF(("hello" + (System.lineSeparator())));
        Assert.assertEquals("hello", out);
        Assert.assertTrue("Should not contain : ", (!(out.contains(System.lineSeparator()))));
    }

    @Test
    public void testCountChar() {
        Assert.assertEquals(0, StringHelper.countChar("Hello World", 'x'));
        Assert.assertEquals(1, StringHelper.countChar("Hello World", 'e'));
        Assert.assertEquals(3, StringHelper.countChar("Hello World", 'l'));
        Assert.assertEquals(1, StringHelper.countChar("Hello World", ' '));
        Assert.assertEquals(0, StringHelper.countChar("", ' '));
        Assert.assertEquals(0, StringHelper.countChar(null, ' '));
    }

    @Test
    public void testRemoveQuotes() throws Exception {
        Assert.assertEquals("Hello World", StringHelper.removeQuotes("Hello World"));
        Assert.assertEquals("", StringHelper.removeQuotes(""));
        Assert.assertEquals(null, StringHelper.removeQuotes(null));
        Assert.assertEquals(" ", StringHelper.removeQuotes(" "));
        Assert.assertEquals("foo", StringHelper.removeQuotes("'foo'"));
        Assert.assertEquals("foo", StringHelper.removeQuotes("'foo"));
        Assert.assertEquals("foo", StringHelper.removeQuotes("foo'"));
        Assert.assertEquals("foo", StringHelper.removeQuotes("\"foo\""));
        Assert.assertEquals("foo", StringHelper.removeQuotes("\"foo"));
        Assert.assertEquals("foo", StringHelper.removeQuotes("foo\""));
        Assert.assertEquals("foo", StringHelper.removeQuotes("\'foo\""));
    }

    @Test
    public void testRemoveLeadingAndEndingQuotes() throws Exception {
        Assert.assertEquals(null, StringHelper.removeLeadingAndEndingQuotes(null));
        Assert.assertEquals("", StringHelper.removeLeadingAndEndingQuotes(""));
        Assert.assertEquals(" ", StringHelper.removeLeadingAndEndingQuotes(" "));
        Assert.assertEquals("Hello World", StringHelper.removeLeadingAndEndingQuotes("Hello World"));
        Assert.assertEquals("Hello World", StringHelper.removeLeadingAndEndingQuotes("'Hello World'"));
        Assert.assertEquals("Hello World", StringHelper.removeLeadingAndEndingQuotes("\"Hello World\""));
        Assert.assertEquals("Hello 'Camel'", StringHelper.removeLeadingAndEndingQuotes("Hello 'Camel'"));
    }

    @Test
    public void testHasUpper() throws Exception {
        Assert.assertEquals(false, StringHelper.hasUpperCase(null));
        Assert.assertEquals(false, StringHelper.hasUpperCase(""));
        Assert.assertEquals(false, StringHelper.hasUpperCase(" "));
        Assert.assertEquals(false, StringHelper.hasUpperCase("com.foo"));
        Assert.assertEquals(false, StringHelper.hasUpperCase("com.foo.123"));
        Assert.assertEquals(true, StringHelper.hasUpperCase("com.foo.MyClass"));
        Assert.assertEquals(true, StringHelper.hasUpperCase("com.foo.My"));
        // Note, this is not a FQN
        Assert.assertEquals(true, StringHelper.hasUpperCase("com.foo.subA"));
    }

    @Test
    public void testIsClassName() throws Exception {
        Assert.assertEquals(false, StringHelper.isClassName(null));
        Assert.assertEquals(false, StringHelper.isClassName(""));
        Assert.assertEquals(false, StringHelper.isClassName(" "));
        Assert.assertEquals(false, StringHelper.isClassName("com.foo"));
        Assert.assertEquals(false, StringHelper.isClassName("com.foo.123"));
        Assert.assertEquals(true, StringHelper.isClassName("com.foo.MyClass"));
        Assert.assertEquals(true, StringHelper.isClassName("com.foo.My"));
        // Note, this is not a FQN
        Assert.assertEquals(false, StringHelper.isClassName("com.foo.subA"));
    }

    @Test
    public void testHasStartToken() throws Exception {
        Assert.assertEquals(false, StringHelper.hasStartToken(null, null));
        Assert.assertEquals(false, StringHelper.hasStartToken(null, "simple"));
        Assert.assertEquals(false, StringHelper.hasStartToken("", null));
        Assert.assertEquals(false, StringHelper.hasStartToken("", "simple"));
        Assert.assertEquals(false, StringHelper.hasStartToken("Hello World", null));
        Assert.assertEquals(false, StringHelper.hasStartToken("Hello World", "simple"));
        Assert.assertEquals(false, StringHelper.hasStartToken("${body}", null));
        Assert.assertEquals(true, StringHelper.hasStartToken("${body}", "simple"));
        Assert.assertEquals(true, StringHelper.hasStartToken("$simple{body}", "simple"));
        Assert.assertEquals(false, StringHelper.hasStartToken("${body}", null));
        Assert.assertEquals(false, StringHelper.hasStartToken("${body}", "foo"));
        // $foo{ is valid because its foo language
        Assert.assertEquals(true, StringHelper.hasStartToken("$foo{body}", "foo"));
    }

    @Test
    public void testIsQuoted() throws Exception {
        Assert.assertEquals(false, StringHelper.isQuoted(null));
        Assert.assertEquals(false, StringHelper.isQuoted(""));
        Assert.assertEquals(false, StringHelper.isQuoted(" "));
        Assert.assertEquals(false, StringHelper.isQuoted("abc"));
        Assert.assertEquals(false, StringHelper.isQuoted("abc'"));
        Assert.assertEquals(false, StringHelper.isQuoted("\"abc\'"));
        Assert.assertEquals(false, StringHelper.isQuoted("abc\""));
        Assert.assertEquals(false, StringHelper.isQuoted("\'abc\""));
        Assert.assertEquals(false, StringHelper.isQuoted("\"abc\'"));
        Assert.assertEquals(false, StringHelper.isQuoted("abc'def'"));
        Assert.assertEquals(false, StringHelper.isQuoted("abc'def'ghi"));
        Assert.assertEquals(false, StringHelper.isQuoted("'def'ghi"));
        Assert.assertEquals(true, StringHelper.isQuoted("'abc'"));
        Assert.assertEquals(true, StringHelper.isQuoted("\"abc\""));
    }

    @Test
    public void testReplaceAll() throws Exception {
        Assert.assertEquals("", StringHelper.replaceAll("", "", ""));
        Assert.assertEquals(null, StringHelper.replaceAll(null, "", ""));
        Assert.assertEquals("foobar", StringHelper.replaceAll("foobar", "###", "DOT"));
        Assert.assertEquals("foobar", StringHelper.replaceAll("foo.bar", ".", ""));
        Assert.assertEquals("fooDOTbar", StringHelper.replaceAll("foo.bar", ".", "DOT"));
        Assert.assertEquals("fooDOTbar", StringHelper.replaceAll("foo###bar", "###", "DOT"));
        Assert.assertEquals("foobar", StringHelper.replaceAll("foo###bar", "###", ""));
        Assert.assertEquals("fooDOTbarDOTbaz", StringHelper.replaceAll("foo.bar.baz", ".", "DOT"));
        Assert.assertEquals("fooDOTbarDOTbazDOT", StringHelper.replaceAll("foo.bar.baz.", ".", "DOT"));
        Assert.assertEquals("DOTfooDOTbarDOTbazDOT", StringHelper.replaceAll(".foo.bar.baz.", ".", "DOT"));
        Assert.assertEquals("fooDOT", StringHelper.replaceAll("foo.", ".", "DOT"));
    }

    @Test
    public void testRemoveInitialCharacters() throws Exception {
        Assert.assertEquals(StringHelper.removeStartingCharacters("foo", '/'), "foo");
        Assert.assertEquals(StringHelper.removeStartingCharacters("/foo", '/'), "foo");
        Assert.assertEquals(StringHelper.removeStartingCharacters("//foo", '/'), "foo");
    }

    @Test
    public void testBefore() {
        Assert.assertEquals("Hello ", StringHelper.before("Hello World", "World"));
        Assert.assertEquals("Hello ", StringHelper.before("Hello World Again", "World"));
        Assert.assertEquals(null, StringHelper.before("Hello Again", "Foo"));
        Assert.assertTrue(StringHelper.before("mykey:ignore", ":", "mykey"::equals).orElse(false));
        Assert.assertFalse(StringHelper.before("ignore:ignore", ":", "mykey"::equals).orElse(false));
    }

    @Test
    public void testAfter() {
        Assert.assertEquals(" World", StringHelper.after("Hello World", "Hello"));
        Assert.assertEquals(" World Again", StringHelper.after("Hello World Again", "Hello"));
        Assert.assertEquals(null, StringHelper.after("Hello Again", "Foo"));
        Assert.assertTrue(StringHelper.after("ignore:mykey", ":", "mykey"::equals).orElse(false));
        Assert.assertFalse(StringHelper.after("ignore:ignore", ":", "mykey"::equals).orElse(false));
    }

    @Test
    public void testBetween() {
        Assert.assertEquals("foo bar", StringHelper.between("Hello 'foo bar' how are you", "'", "'"));
        Assert.assertEquals("foo bar", StringHelper.between("Hello ${foo bar} how are you", "${", "}"));
        Assert.assertEquals(null, StringHelper.between("Hello ${foo bar} how are you", "'", "'"));
        Assert.assertTrue(StringHelper.between("begin:mykey:end", "begin:", ":end", "mykey"::equals).orElse(false));
        Assert.assertFalse(StringHelper.between("begin:ignore:end", "begin:", ":end", "mykey"::equals).orElse(false));
    }

    @Test
    public void testBetweenOuterPair() {
        Assert.assertEquals("bar(baz)123", StringHelper.betweenOuterPair("foo(bar(baz)123)", '(', ')'));
        Assert.assertEquals(null, StringHelper.betweenOuterPair("foo(bar(baz)123))", '(', ')'));
        Assert.assertEquals(null, StringHelper.betweenOuterPair("foo(bar(baz123", '(', ')'));
        Assert.assertEquals(null, StringHelper.betweenOuterPair("foo)bar)baz123", '(', ')'));
        Assert.assertEquals("bar", StringHelper.betweenOuterPair("foo(bar)baz123", '(', ')'));
        Assert.assertEquals("'bar', 'baz()123', 123", StringHelper.betweenOuterPair("foo('bar', 'baz()123', 123)", '(', ')'));
        Assert.assertTrue(StringHelper.betweenOuterPair("foo(bar)baz123", '(', ')', "bar"::equals).orElse(false));
        Assert.assertFalse(StringHelper.betweenOuterPair("foo[bar)baz123", '(', ')', "bar"::equals).orElse(false));
    }

    @Test
    public void testIsJavaIdentifier() {
        Assert.assertEquals(true, StringHelper.isJavaIdentifier("foo"));
        Assert.assertEquals(false, StringHelper.isJavaIdentifier("foo.bar"));
        Assert.assertEquals(false, StringHelper.isJavaIdentifier(""));
        Assert.assertEquals(false, StringHelper.isJavaIdentifier(null));
    }

    @Test
    public void testNormalizeClassName() {
        Assert.assertEquals("Should get the right class name", "my.package-info", StringHelper.normalizeClassName("my.package-info"));
        Assert.assertEquals("Should get the right class name", "Integer[]", StringHelper.normalizeClassName("Integer[] \r"));
        Assert.assertEquals("Should get the right class name", "Hello_World", StringHelper.normalizeClassName("Hello_World"));
        Assert.assertEquals("Should get the right class name", "", StringHelper.normalizeClassName("////"));
    }

    @Test
    public void testChangedLines() {
        String oldText = "Hello\nWorld\nHow are you";
        String newText = "Hello\nWorld\nHow are you";
        List<Integer> changed = StringHelper.changedLines(oldText, newText);
        Assert.assertEquals(0, changed.size());
        oldText = "Hello\nWorld\nHow are you";
        newText = "Hello\nWorld\nHow are you today";
        changed = StringHelper.changedLines(oldText, newText);
        Assert.assertEquals(1, changed.size());
        Assert.assertEquals(2, changed.get(0).intValue());
        oldText = "Hello\nWorld\nHow are you";
        newText = "Hello\nCamel\nHow are you today";
        changed = StringHelper.changedLines(oldText, newText);
        Assert.assertEquals(2, changed.size());
        Assert.assertEquals(1, changed.get(0).intValue());
        Assert.assertEquals(2, changed.get(1).intValue());
        oldText = "Hello\nWorld\nHow are you";
        newText = "Hello\nWorld\nHow are you today\nand tomorrow";
        changed = StringHelper.changedLines(oldText, newText);
        Assert.assertEquals(2, changed.size());
        Assert.assertEquals(2, changed.get(0).intValue());
        Assert.assertEquals(3, changed.get(1).intValue());
    }

    @Test
    public void testTrimToNull() {
        Assert.assertEquals(StringHelper.trimToNull("abc"), "abc");
        Assert.assertEquals(StringHelper.trimToNull(" abc"), "abc");
        Assert.assertEquals(StringHelper.trimToNull(" abc "), "abc");
        Assert.assertNull(StringHelper.trimToNull(" "));
        Assert.assertNull(StringHelper.trimToNull("\t"));
        Assert.assertNull(StringHelper.trimToNull(" \t "));
        Assert.assertNull(StringHelper.trimToNull(""));
    }

    @Test
    public void testHumanReadableBytes() {
        Assert.assertEquals("0 B", StringHelper.humanReadableBytes(Locale.ENGLISH, 0));
        Assert.assertEquals("32 B", StringHelper.humanReadableBytes(Locale.ENGLISH, 32));
        Assert.assertEquals("1.0 KB", StringHelper.humanReadableBytes(Locale.ENGLISH, 1024));
        Assert.assertEquals("1.7 KB", StringHelper.humanReadableBytes(Locale.ENGLISH, 1730));
        Assert.assertEquals("108.0 KB", StringHelper.humanReadableBytes(Locale.ENGLISH, 110592));
        Assert.assertEquals("6.8 MB", StringHelper.humanReadableBytes(Locale.ENGLISH, 7077888));
        Assert.assertEquals("432.0 MB", StringHelper.humanReadableBytes(Locale.ENGLISH, 452984832));
        Assert.assertEquals("27.0 GB", StringHelper.humanReadableBytes(Locale.ENGLISH, 28991029248L));
        Assert.assertEquals("1.7 TB", StringHelper.humanReadableBytes(Locale.ENGLISH, 1855425871872L));
    }

    @Test
    public void testHumanReadableBytesNullLocale() {
        Assert.assertEquals("1.3 KB", StringHelper.humanReadableBytes(null, 1280));
    }

    @Test
    public void testHumanReadableBytesDefaultLocale() {
        Assert.assertNotNull(StringHelper.humanReadableBytes(110592));
    }

    @Test
    public void testCapitalizeDash() {
        Assert.assertEquals(null, StringHelper.dashToCamelCase(null));
        Assert.assertEquals("", StringHelper.dashToCamelCase(""));
        Assert.assertEquals("hello", StringHelper.dashToCamelCase("hello"));
        Assert.assertEquals("helloGreat", StringHelper.dashToCamelCase("helloGreat"));
        Assert.assertEquals("helloGreat", StringHelper.dashToCamelCase("hello-great"));
        Assert.assertEquals("helloGreatWorld", StringHelper.dashToCamelCase("hello-great-world"));
    }
}

