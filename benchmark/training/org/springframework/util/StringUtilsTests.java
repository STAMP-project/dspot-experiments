/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.util;


import java.util.Locale;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rick Evans
 */
public class StringUtilsTests {
    @Test
    public void testHasTextBlank() {
        String blank = "          ";
        Assert.assertEquals(false, StringUtils.hasText(blank));
    }

    @Test
    public void testHasTextNullEmpty() {
        Assert.assertEquals(false, StringUtils.hasText(null));
        Assert.assertEquals(false, StringUtils.hasText(""));
    }

    @Test
    public void testHasTextValid() {
        Assert.assertEquals(true, StringUtils.hasText("t"));
    }

    @Test
    public void testContainsWhitespace() {
        Assert.assertFalse(StringUtils.containsWhitespace(null));
        Assert.assertFalse(StringUtils.containsWhitespace(""));
        Assert.assertFalse(StringUtils.containsWhitespace("a"));
        Assert.assertFalse(StringUtils.containsWhitespace("abc"));
        Assert.assertTrue(StringUtils.containsWhitespace(" "));
        Assert.assertTrue(StringUtils.containsWhitespace(" a"));
        Assert.assertTrue(StringUtils.containsWhitespace("abc "));
        Assert.assertTrue(StringUtils.containsWhitespace("a b"));
        Assert.assertTrue(StringUtils.containsWhitespace("a  b"));
    }

    @Test
    public void testTrimWhitespace() {
        Assert.assertEquals(null, StringUtils.trimWhitespace(null));
        Assert.assertEquals("", StringUtils.trimWhitespace(""));
        Assert.assertEquals("", StringUtils.trimWhitespace(" "));
        Assert.assertEquals("", StringUtils.trimWhitespace("\t"));
        Assert.assertEquals("a", StringUtils.trimWhitespace(" a"));
        Assert.assertEquals("a", StringUtils.trimWhitespace("a "));
        Assert.assertEquals("a", StringUtils.trimWhitespace(" a "));
        Assert.assertEquals("a b", StringUtils.trimWhitespace(" a b "));
        Assert.assertEquals("a b  c", StringUtils.trimWhitespace(" a b  c "));
    }

    @Test
    public void testTrimAllWhitespace() {
        Assert.assertEquals("", StringUtils.trimAllWhitespace(""));
        Assert.assertEquals("", StringUtils.trimAllWhitespace(" "));
        Assert.assertEquals("", StringUtils.trimAllWhitespace("\t"));
        Assert.assertEquals("a", StringUtils.trimAllWhitespace(" a"));
        Assert.assertEquals("a", StringUtils.trimAllWhitespace("a "));
        Assert.assertEquals("a", StringUtils.trimAllWhitespace(" a "));
        Assert.assertEquals("ab", StringUtils.trimAllWhitespace(" a b "));
        Assert.assertEquals("abc", StringUtils.trimAllWhitespace(" a b  c "));
    }

    @Test
    public void testTrimLeadingWhitespace() {
        Assert.assertEquals(null, StringUtils.trimLeadingWhitespace(null));
        Assert.assertEquals("", StringUtils.trimLeadingWhitespace(""));
        Assert.assertEquals("", StringUtils.trimLeadingWhitespace(" "));
        Assert.assertEquals("", StringUtils.trimLeadingWhitespace("\t"));
        Assert.assertEquals("a", StringUtils.trimLeadingWhitespace(" a"));
        Assert.assertEquals("a ", StringUtils.trimLeadingWhitespace("a "));
        Assert.assertEquals("a ", StringUtils.trimLeadingWhitespace(" a "));
        Assert.assertEquals("a b ", StringUtils.trimLeadingWhitespace(" a b "));
        Assert.assertEquals("a b  c ", StringUtils.trimLeadingWhitespace(" a b  c "));
    }

    @Test
    public void testTrimTrailingWhitespace() {
        Assert.assertEquals(null, StringUtils.trimTrailingWhitespace(null));
        Assert.assertEquals("", StringUtils.trimTrailingWhitespace(""));
        Assert.assertEquals("", StringUtils.trimTrailingWhitespace(" "));
        Assert.assertEquals("", StringUtils.trimTrailingWhitespace("\t"));
        Assert.assertEquals("a", StringUtils.trimTrailingWhitespace("a "));
        Assert.assertEquals(" a", StringUtils.trimTrailingWhitespace(" a"));
        Assert.assertEquals(" a", StringUtils.trimTrailingWhitespace(" a "));
        Assert.assertEquals(" a b", StringUtils.trimTrailingWhitespace(" a b "));
        Assert.assertEquals(" a b  c", StringUtils.trimTrailingWhitespace(" a b  c "));
    }

    @Test
    public void testTrimLeadingCharacter() {
        Assert.assertEquals(null, StringUtils.trimLeadingCharacter(null, ' '));
        Assert.assertEquals("", StringUtils.trimLeadingCharacter("", ' '));
        Assert.assertEquals("", StringUtils.trimLeadingCharacter(" ", ' '));
        Assert.assertEquals("\t", StringUtils.trimLeadingCharacter("\t", ' '));
        Assert.assertEquals("a", StringUtils.trimLeadingCharacter(" a", ' '));
        Assert.assertEquals("a ", StringUtils.trimLeadingCharacter("a ", ' '));
        Assert.assertEquals("a ", StringUtils.trimLeadingCharacter(" a ", ' '));
        Assert.assertEquals("a b ", StringUtils.trimLeadingCharacter(" a b ", ' '));
        Assert.assertEquals("a b  c ", StringUtils.trimLeadingCharacter(" a b  c ", ' '));
    }

    @Test
    public void testTrimTrailingCharacter() {
        Assert.assertEquals(null, StringUtils.trimTrailingCharacter(null, ' '));
        Assert.assertEquals("", StringUtils.trimTrailingCharacter("", ' '));
        Assert.assertEquals("", StringUtils.trimTrailingCharacter(" ", ' '));
        Assert.assertEquals("\t", StringUtils.trimTrailingCharacter("\t", ' '));
        Assert.assertEquals("a", StringUtils.trimTrailingCharacter("a ", ' '));
        Assert.assertEquals(" a", StringUtils.trimTrailingCharacter(" a", ' '));
        Assert.assertEquals(" a", StringUtils.trimTrailingCharacter(" a ", ' '));
        Assert.assertEquals(" a b", StringUtils.trimTrailingCharacter(" a b ", ' '));
        Assert.assertEquals(" a b  c", StringUtils.trimTrailingCharacter(" a b  c ", ' '));
    }

    @Test
    public void testStartsWithIgnoreCase() {
        String prefix = "fOo";
        Assert.assertTrue(StringUtils.startsWithIgnoreCase("foo", prefix));
        Assert.assertTrue(StringUtils.startsWithIgnoreCase("Foo", prefix));
        Assert.assertTrue(StringUtils.startsWithIgnoreCase("foobar", prefix));
        Assert.assertTrue(StringUtils.startsWithIgnoreCase("foobarbar", prefix));
        Assert.assertTrue(StringUtils.startsWithIgnoreCase("Foobar", prefix));
        Assert.assertTrue(StringUtils.startsWithIgnoreCase("FoobarBar", prefix));
        Assert.assertTrue(StringUtils.startsWithIgnoreCase("foObar", prefix));
        Assert.assertTrue(StringUtils.startsWithIgnoreCase("FOObar", prefix));
        Assert.assertTrue(StringUtils.startsWithIgnoreCase("fOobar", prefix));
        Assert.assertFalse(StringUtils.startsWithIgnoreCase(null, prefix));
        Assert.assertFalse(StringUtils.startsWithIgnoreCase("fOobar", null));
        Assert.assertFalse(StringUtils.startsWithIgnoreCase("b", prefix));
        Assert.assertFalse(StringUtils.startsWithIgnoreCase("barfoo", prefix));
        Assert.assertFalse(StringUtils.startsWithIgnoreCase("barfoobar", prefix));
    }

    @Test
    public void testEndsWithIgnoreCase() {
        String suffix = "fOo";
        Assert.assertTrue(StringUtils.endsWithIgnoreCase("foo", suffix));
        Assert.assertTrue(StringUtils.endsWithIgnoreCase("Foo", suffix));
        Assert.assertTrue(StringUtils.endsWithIgnoreCase("barfoo", suffix));
        Assert.assertTrue(StringUtils.endsWithIgnoreCase("barbarfoo", suffix));
        Assert.assertTrue(StringUtils.endsWithIgnoreCase("barFoo", suffix));
        Assert.assertTrue(StringUtils.endsWithIgnoreCase("barBarFoo", suffix));
        Assert.assertTrue(StringUtils.endsWithIgnoreCase("barfoO", suffix));
        Assert.assertTrue(StringUtils.endsWithIgnoreCase("barFOO", suffix));
        Assert.assertTrue(StringUtils.endsWithIgnoreCase("barfOo", suffix));
        Assert.assertFalse(StringUtils.endsWithIgnoreCase(null, suffix));
        Assert.assertFalse(StringUtils.endsWithIgnoreCase("barfOo", null));
        Assert.assertFalse(StringUtils.endsWithIgnoreCase("b", suffix));
        Assert.assertFalse(StringUtils.endsWithIgnoreCase("foobar", suffix));
        Assert.assertFalse(StringUtils.endsWithIgnoreCase("barfoobar", suffix));
    }

    @Test
    public void testSubstringMatch() {
        Assert.assertTrue(StringUtils.substringMatch("foo", 0, "foo"));
        Assert.assertTrue(StringUtils.substringMatch("foo", 1, "oo"));
        Assert.assertTrue(StringUtils.substringMatch("foo", 2, "o"));
        Assert.assertFalse(StringUtils.substringMatch("foo", 0, "fOo"));
        Assert.assertFalse(StringUtils.substringMatch("foo", 1, "fOo"));
        Assert.assertFalse(StringUtils.substringMatch("foo", 2, "fOo"));
        Assert.assertFalse(StringUtils.substringMatch("foo", 3, "fOo"));
        Assert.assertFalse(StringUtils.substringMatch("foo", 1, "Oo"));
        Assert.assertFalse(StringUtils.substringMatch("foo", 2, "Oo"));
        Assert.assertFalse(StringUtils.substringMatch("foo", 3, "Oo"));
        Assert.assertFalse(StringUtils.substringMatch("foo", 2, "O"));
        Assert.assertFalse(StringUtils.substringMatch("foo", 3, "O"));
    }

    @Test
    public void testCountOccurrencesOf() {
        Assert.assertTrue("nullx2 = 0", ((StringUtils.countOccurrencesOf(null, null)) == 0));
        Assert.assertTrue("null string = 0", ((StringUtils.countOccurrencesOf("s", null)) == 0));
        Assert.assertTrue("null substring = 0", ((StringUtils.countOccurrencesOf(null, "s")) == 0));
        String s = "erowoiueoiur";
        Assert.assertTrue("not found = 0", ((StringUtils.countOccurrencesOf(s, "WERWER")) == 0));
        Assert.assertTrue("not found char = 0", ((StringUtils.countOccurrencesOf(s, "x")) == 0));
        Assert.assertTrue("not found ws = 0", ((StringUtils.countOccurrencesOf(s, " ")) == 0));
        Assert.assertTrue("not found empty string = 0", ((StringUtils.countOccurrencesOf(s, "")) == 0));
        Assert.assertTrue("found char=2", ((StringUtils.countOccurrencesOf(s, "e")) == 2));
        Assert.assertTrue("found substring=2", ((StringUtils.countOccurrencesOf(s, "oi")) == 2));
        Assert.assertTrue("found substring=2", ((StringUtils.countOccurrencesOf(s, "oiu")) == 2));
        Assert.assertTrue("found substring=3", ((StringUtils.countOccurrencesOf(s, "oiur")) == 1));
        Assert.assertTrue("test last", ((StringUtils.countOccurrencesOf(s, "r")) == 2));
    }

    @Test
    public void testReplace() {
        String inString = "a6AazAaa77abaa";
        String oldPattern = "aa";
        String newPattern = "foo";
        // Simple replace
        String s = StringUtils.replace(inString, oldPattern, newPattern);
        Assert.assertTrue("Replace 1 worked", s.equals("a6AazAfoo77abfoo"));
        // Non match: no change
        s = StringUtils.replace(inString, "qwoeiruqopwieurpoqwieur", newPattern);
        Assert.assertSame("Replace non-matched is returned as-is", inString, s);
        // Null new pattern: should ignore
        s = StringUtils.replace(inString, oldPattern, null);
        Assert.assertSame("Replace non-matched is returned as-is", inString, s);
        // Null old pattern: should ignore
        s = StringUtils.replace(inString, null, newPattern);
        Assert.assertSame("Replace non-matched is returned as-is", inString, s);
    }

    @Test
    public void testDelete() {
        String inString = "The quick brown fox jumped over the lazy dog";
        String noThe = StringUtils.delete(inString, "the");
        Assert.assertTrue((("Result has no the [" + noThe) + "]"), noThe.equals("The quick brown fox jumped over  lazy dog"));
        String nohe = StringUtils.delete(inString, "he");
        Assert.assertTrue((("Result has no he [" + nohe) + "]"), nohe.equals("T quick brown fox jumped over t lazy dog"));
        String nosp = StringUtils.delete(inString, " ");
        Assert.assertTrue("Result has no spaces", nosp.equals("Thequickbrownfoxjumpedoverthelazydog"));
        String killEnd = StringUtils.delete(inString, "dog");
        Assert.assertTrue("Result has no dog", killEnd.equals("The quick brown fox jumped over the lazy "));
        String mismatch = StringUtils.delete(inString, "dxxcxcxog");
        Assert.assertTrue("Result is unchanged", mismatch.equals(inString));
        String nochange = StringUtils.delete(inString, "");
        Assert.assertTrue("Result is unchanged", nochange.equals(inString));
    }

    @Test
    public void testDeleteAny() {
        String inString = "Able was I ere I saw Elba";
        String res = StringUtils.deleteAny(inString, "I");
        Assert.assertTrue((("Result has no Is [" + res) + "]"), res.equals("Able was  ere  saw Elba"));
        res = StringUtils.deleteAny(inString, "AeEba!");
        Assert.assertTrue((("Result has no Is [" + res) + "]"), res.equals("l ws I r I sw l"));
        String mismatch = StringUtils.deleteAny(inString, "#@$#$^");
        Assert.assertTrue("Result is unchanged", mismatch.equals(inString));
        String whitespace = "This is\n\n\n    \t   a messagy string with whitespace\n";
        Assert.assertTrue("Has CR", whitespace.contains("\n"));
        Assert.assertTrue("Has tab", whitespace.contains("\t"));
        Assert.assertTrue("Has  sp", whitespace.contains(" "));
        String cleaned = StringUtils.deleteAny(whitespace, "\n\t ");
        Assert.assertTrue("Has no CR", (!(cleaned.contains("\n"))));
        Assert.assertTrue("Has no tab", (!(cleaned.contains("\t"))));
        Assert.assertTrue("Has no sp", (!(cleaned.contains(" "))));
        Assert.assertTrue("Still has chars", ((cleaned.length()) > 10));
    }

    @Test
    public void testQuote() {
        Assert.assertEquals("'myString'", StringUtils.quote("myString"));
        Assert.assertEquals("''", StringUtils.quote(""));
        Assert.assertNull(StringUtils.quote(null));
    }

    @Test
    public void testQuoteIfString() {
        Assert.assertEquals("'myString'", StringUtils.quoteIfString("myString"));
        Assert.assertEquals("''", StringUtils.quoteIfString(""));
        Assert.assertEquals(Integer.valueOf(5), StringUtils.quoteIfString(5));
        Assert.assertNull(StringUtils.quoteIfString(null));
    }

    @Test
    public void testUnqualify() {
        String qualified = "i.am.not.unqualified";
        Assert.assertEquals("unqualified", StringUtils.unqualify(qualified));
    }

    @Test
    public void testCapitalize() {
        String capitalized = "i am not capitalized";
        Assert.assertEquals("I am not capitalized", StringUtils.capitalize(capitalized));
    }

    @Test
    public void testUncapitalize() {
        String capitalized = "I am capitalized";
        Assert.assertEquals("i am capitalized", StringUtils.uncapitalize(capitalized));
    }

    @Test
    public void testGetFilename() {
        Assert.assertEquals(null, StringUtils.getFilename(null));
        Assert.assertEquals("", StringUtils.getFilename(""));
        Assert.assertEquals("myfile", StringUtils.getFilename("myfile"));
        Assert.assertEquals("myfile", StringUtils.getFilename("mypath/myfile"));
        Assert.assertEquals("myfile.", StringUtils.getFilename("myfile."));
        Assert.assertEquals("myfile.", StringUtils.getFilename("mypath/myfile."));
        Assert.assertEquals("myfile.txt", StringUtils.getFilename("myfile.txt"));
        Assert.assertEquals("myfile.txt", StringUtils.getFilename("mypath/myfile.txt"));
    }

    @Test
    public void testGetFilenameExtension() {
        Assert.assertEquals(null, StringUtils.getFilenameExtension(null));
        Assert.assertEquals(null, StringUtils.getFilenameExtension(""));
        Assert.assertEquals(null, StringUtils.getFilenameExtension("myfile"));
        Assert.assertEquals(null, StringUtils.getFilenameExtension("myPath/myfile"));
        Assert.assertEquals(null, StringUtils.getFilenameExtension("/home/user/.m2/settings/myfile"));
        Assert.assertEquals("", StringUtils.getFilenameExtension("myfile."));
        Assert.assertEquals("", StringUtils.getFilenameExtension("myPath/myfile."));
        Assert.assertEquals("txt", StringUtils.getFilenameExtension("myfile.txt"));
        Assert.assertEquals("txt", StringUtils.getFilenameExtension("mypath/myfile.txt"));
        Assert.assertEquals("txt", StringUtils.getFilenameExtension("/home/user/.m2/settings/myfile.txt"));
    }

    @Test
    public void testStripFilenameExtension() {
        Assert.assertEquals("", StringUtils.stripFilenameExtension(""));
        Assert.assertEquals("myfile", StringUtils.stripFilenameExtension("myfile"));
        Assert.assertEquals("myfile", StringUtils.stripFilenameExtension("myfile."));
        Assert.assertEquals("myfile", StringUtils.stripFilenameExtension("myfile.txt"));
        Assert.assertEquals("mypath/myfile", StringUtils.stripFilenameExtension("mypath/myfile"));
        Assert.assertEquals("mypath/myfile", StringUtils.stripFilenameExtension("mypath/myfile."));
        Assert.assertEquals("mypath/myfile", StringUtils.stripFilenameExtension("mypath/myfile.txt"));
        Assert.assertEquals("/home/user/.m2/settings/myfile", StringUtils.stripFilenameExtension("/home/user/.m2/settings/myfile"));
        Assert.assertEquals("/home/user/.m2/settings/myfile", StringUtils.stripFilenameExtension("/home/user/.m2/settings/myfile."));
        Assert.assertEquals("/home/user/.m2/settings/myfile", StringUtils.stripFilenameExtension("/home/user/.m2/settings/myfile.txt"));
    }

    @Test
    public void testCleanPath() {
        Assert.assertEquals("mypath/myfile", StringUtils.cleanPath("mypath/myfile"));
        Assert.assertEquals("mypath/myfile", StringUtils.cleanPath("mypath\\myfile"));
        Assert.assertEquals("mypath/myfile", StringUtils.cleanPath("mypath/../mypath/myfile"));
        Assert.assertEquals("mypath/myfile", StringUtils.cleanPath("mypath/myfile/../../mypath/myfile"));
        Assert.assertEquals("../mypath/myfile", StringUtils.cleanPath("../mypath/myfile"));
        Assert.assertEquals("../mypath/myfile", StringUtils.cleanPath("../mypath/../mypath/myfile"));
        Assert.assertEquals("../mypath/myfile", StringUtils.cleanPath("mypath/../../mypath/myfile"));
        Assert.assertEquals("/../mypath/myfile", StringUtils.cleanPath("/../mypath/myfile"));
        Assert.assertEquals("/mypath/myfile", StringUtils.cleanPath("/a/:b/../../mypath/myfile"));
        Assert.assertEquals("/", StringUtils.cleanPath("/"));
        Assert.assertEquals("/", StringUtils.cleanPath("/mypath/../"));
        Assert.assertEquals("", StringUtils.cleanPath("mypath/.."));
        Assert.assertEquals("", StringUtils.cleanPath("mypath/../."));
        Assert.assertEquals("./", StringUtils.cleanPath("mypath/../"));
        Assert.assertEquals("./", StringUtils.cleanPath("././"));
        Assert.assertEquals("./", StringUtils.cleanPath("./"));
        Assert.assertEquals("../", StringUtils.cleanPath("../"));
        Assert.assertEquals("../", StringUtils.cleanPath("./../"));
        Assert.assertEquals("../", StringUtils.cleanPath(".././"));
        Assert.assertEquals("file:/", StringUtils.cleanPath("file:/"));
        Assert.assertEquals("file:/", StringUtils.cleanPath("file:/mypath/../"));
        Assert.assertEquals("file:", StringUtils.cleanPath("file:mypath/.."));
        Assert.assertEquals("file:", StringUtils.cleanPath("file:mypath/../."));
        Assert.assertEquals("file:./", StringUtils.cleanPath("file:mypath/../"));
        Assert.assertEquals("file:./", StringUtils.cleanPath("file:././"));
        Assert.assertEquals("file:./", StringUtils.cleanPath("file:./"));
        Assert.assertEquals("file:../", StringUtils.cleanPath("file:../"));
        Assert.assertEquals("file:../", StringUtils.cleanPath("file:./../"));
        Assert.assertEquals("file:../", StringUtils.cleanPath("file:.././"));
        Assert.assertEquals("file:///c:/path/the%20file.txt", StringUtils.cleanPath("file:///c:/some/../path/the%20file.txt"));
    }

    @Test
    public void testPathEquals() {
        Assert.assertTrue("Must be true for the same strings", StringUtils.pathEquals("/dummy1/dummy2/dummy3", "/dummy1/dummy2/dummy3"));
        Assert.assertTrue("Must be true for the same win strings", StringUtils.pathEquals("C:\\dummy1\\dummy2\\dummy3", "C:\\dummy1\\dummy2\\dummy3"));
        Assert.assertTrue("Must be true for one top path on 1", StringUtils.pathEquals("/dummy1/bin/../dummy2/dummy3", "/dummy1/dummy2/dummy3"));
        Assert.assertTrue("Must be true for one win top path on 2", StringUtils.pathEquals("C:\\dummy1\\dummy2\\dummy3", "C:\\dummy1\\bin\\..\\dummy2\\dummy3"));
        Assert.assertTrue("Must be true for two top paths on 1", StringUtils.pathEquals("/dummy1/bin/../dummy2/bin/../dummy3", "/dummy1/dummy2/dummy3"));
        Assert.assertTrue("Must be true for two win top paths on 2", StringUtils.pathEquals("C:\\dummy1\\dummy2\\dummy3", "C:\\dummy1\\bin\\..\\dummy2\\bin\\..\\dummy3"));
        Assert.assertTrue("Must be true for double top paths on 1", StringUtils.pathEquals("/dummy1/bin/tmp/../../dummy2/dummy3", "/dummy1/dummy2/dummy3"));
        Assert.assertTrue("Must be true for double top paths on 2 with similarity", StringUtils.pathEquals("/dummy1/dummy2/dummy3", "/dummy1/dum/dum/../../dummy2/dummy3"));
        Assert.assertTrue("Must be true for current paths", StringUtils.pathEquals("./dummy1/dummy2/dummy3", "dummy1/dum/./dum/../../dummy2/dummy3"));
        Assert.assertFalse("Must be false for relative/absolute paths", StringUtils.pathEquals("./dummy1/dummy2/dummy3", "/dummy1/dum/./dum/../../dummy2/dummy3"));
        Assert.assertFalse("Must be false for different strings", StringUtils.pathEquals("/dummy1/dummy2/dummy3", "/dummy1/dummy4/dummy3"));
        Assert.assertFalse("Must be false for one false path on 1", StringUtils.pathEquals("/dummy1/bin/tmp/../dummy2/dummy3", "/dummy1/dummy2/dummy3"));
        Assert.assertFalse("Must be false for one false win top path on 2", StringUtils.pathEquals("C:\\dummy1\\dummy2\\dummy3", "C:\\dummy1\\bin\\tmp\\..\\dummy2\\dummy3"));
        Assert.assertFalse("Must be false for top path on 1 + difference", StringUtils.pathEquals("/dummy1/bin/../dummy2/dummy3", "/dummy1/dummy2/dummy4"));
    }

    @Test
    public void testConcatenateStringArrays() {
        String[] input1 = new String[]{ "myString2" };
        String[] input2 = new String[]{ "myString1", "myString2" };
        String[] result = StringUtils.concatenateStringArrays(input1, input2);
        Assert.assertEquals(3, result.length);
        Assert.assertEquals("myString2", result[0]);
        Assert.assertEquals("myString1", result[1]);
        Assert.assertEquals("myString2", result[2]);
        Assert.assertArrayEquals(input1, StringUtils.concatenateStringArrays(input1, null));
        Assert.assertArrayEquals(input2, StringUtils.concatenateStringArrays(null, input2));
        Assert.assertNull(StringUtils.concatenateStringArrays(null, null));
    }

    @Test
    @Deprecated
    public void testMergeStringArrays() {
        String[] input1 = new String[]{ "myString2" };
        String[] input2 = new String[]{ "myString1", "myString2" };
        String[] result = StringUtils.mergeStringArrays(input1, input2);
        Assert.assertEquals(2, result.length);
        Assert.assertEquals("myString2", result[0]);
        Assert.assertEquals("myString1", result[1]);
        Assert.assertArrayEquals(input1, StringUtils.mergeStringArrays(input1, null));
        Assert.assertArrayEquals(input2, StringUtils.mergeStringArrays(null, input2));
        Assert.assertNull(StringUtils.mergeStringArrays(null, null));
    }

    @Test
    public void testSortStringArray() {
        String[] input = new String[]{ "myString2" };
        input = StringUtils.addStringToArray(input, "myString1");
        Assert.assertEquals("myString2", input[0]);
        Assert.assertEquals("myString1", input[1]);
        StringUtils.sortStringArray(input);
        Assert.assertEquals("myString1", input[0]);
        Assert.assertEquals("myString2", input[1]);
    }

    @Test
    public void testRemoveDuplicateStrings() {
        String[] input = new String[]{ "myString2", "myString1", "myString2" };
        input = StringUtils.removeDuplicateStrings(input);
        Assert.assertEquals("myString2", input[0]);
        Assert.assertEquals("myString1", input[1]);
    }

    @Test
    public void testSplitArrayElementsIntoProperties() {
        String[] input = new String[]{ "key1=value1 ", "key2 =\"value2\"" };
        Properties result = StringUtils.splitArrayElementsIntoProperties(input, "=");
        Assert.assertEquals("value1", result.getProperty("key1"));
        Assert.assertEquals("\"value2\"", result.getProperty("key2"));
    }

    @Test
    public void testSplitArrayElementsIntoPropertiesAndDeletedChars() {
        String[] input = new String[]{ "key1=value1 ", "key2 =\"value2\"" };
        Properties result = StringUtils.splitArrayElementsIntoProperties(input, "=", "\"");
        Assert.assertEquals("value1", result.getProperty("key1"));
        Assert.assertEquals("value2", result.getProperty("key2"));
    }

    @Test
    public void testTokenizeToStringArray() {
        String[] sa = StringUtils.tokenizeToStringArray("a,b , ,c", ",");
        Assert.assertEquals(3, sa.length);
        Assert.assertTrue("components are correct", (((sa[0].equals("a")) && (sa[1].equals("b"))) && (sa[2].equals("c"))));
    }

    @Test
    public void testTokenizeToStringArrayWithNotIgnoreEmptyTokens() {
        String[] sa = StringUtils.tokenizeToStringArray("a,b , ,c", ",", true, false);
        Assert.assertEquals(4, sa.length);
        Assert.assertTrue("components are correct", ((((sa[0].equals("a")) && (sa[1].equals("b"))) && (sa[2].equals(""))) && (sa[3].equals("c"))));
    }

    @Test
    public void testTokenizeToStringArrayWithNotTrimTokens() {
        String[] sa = StringUtils.tokenizeToStringArray("a,b ,c", ",", false, true);
        Assert.assertEquals(3, sa.length);
        Assert.assertTrue("components are correct", (((sa[0].equals("a")) && (sa[1].equals("b "))) && (sa[2].equals("c"))));
    }

    @Test
    public void testCommaDelimitedListToStringArrayWithNullProducesEmptyArray() {
        String[] sa = StringUtils.commaDelimitedListToStringArray(null);
        Assert.assertTrue("String array isn't null with null input", (sa != null));
        Assert.assertTrue("String array length == 0 with null input", ((sa.length) == 0));
    }

    @Test
    public void testCommaDelimitedListToStringArrayWithEmptyStringProducesEmptyArray() {
        String[] sa = StringUtils.commaDelimitedListToStringArray("");
        Assert.assertTrue("String array isn't null with null input", (sa != null));
        Assert.assertTrue("String array length == 0 with null input", ((sa.length) == 0));
    }

    @Test
    public void testDelimitedListToStringArrayWithComma() {
        String[] sa = StringUtils.delimitedListToStringArray("a,b", ",");
        Assert.assertEquals(2, sa.length);
        Assert.assertEquals("a", sa[0]);
        Assert.assertEquals("b", sa[1]);
    }

    @Test
    public void testDelimitedListToStringArrayWithSemicolon() {
        String[] sa = StringUtils.delimitedListToStringArray("a;b", ";");
        Assert.assertEquals(2, sa.length);
        Assert.assertEquals("a", sa[0]);
        Assert.assertEquals("b", sa[1]);
    }

    @Test
    public void testDelimitedListToStringArrayWithEmptyString() {
        String[] sa = StringUtils.delimitedListToStringArray("a,b", "");
        Assert.assertEquals(3, sa.length);
        Assert.assertEquals("a", sa[0]);
        Assert.assertEquals(",", sa[1]);
        Assert.assertEquals("b", sa[2]);
    }

    @Test
    public void testDelimitedListToStringArrayWithNullDelimiter() {
        String[] sa = StringUtils.delimitedListToStringArray("a,b", null);
        Assert.assertEquals(1, sa.length);
        Assert.assertEquals("a,b", sa[0]);
    }

    @Test
    public void testCommaDelimitedListToStringArrayMatchWords() {
        // Could read these from files
        String[] sa = new String[]{ "foo", "bar", "big" };
        doTestCommaDelimitedListToStringArrayLegalMatch(sa);
        doTestStringArrayReverseTransformationMatches(sa);
        sa = new String[]{ "a", "b", "c" };
        doTestCommaDelimitedListToStringArrayLegalMatch(sa);
        doTestStringArrayReverseTransformationMatches(sa);
        // Test same words
        sa = new String[]{ "AA", "AA", "AA", "AA", "AA" };
        doTestCommaDelimitedListToStringArrayLegalMatch(sa);
        doTestStringArrayReverseTransformationMatches(sa);
    }

    @Test
    public void testCommaDelimitedListToStringArraySingleString() {
        // Could read these from files
        String s = "woeirqupoiewuropqiewuorpqiwueopriquwopeiurqopwieur";
        String[] sa = StringUtils.commaDelimitedListToStringArray(s);
        Assert.assertTrue("Found one String with no delimiters", ((sa.length) == 1));
        Assert.assertTrue("Single array entry matches input String with no delimiters", sa[0].equals(s));
    }

    @Test
    public void testCommaDelimitedListToStringArrayWithOtherPunctuation() {
        // Could read these from files
        String[] sa = new String[]{ "xcvwert4456346&*.", "///", ".!", ".", ";" };
        doTestCommaDelimitedListToStringArrayLegalMatch(sa);
    }

    /**
     * We expect to see the empty Strings in the output.
     */
    @Test
    public void testCommaDelimitedListToStringArrayEmptyStrings() {
        // Could read these from files
        String[] sa = StringUtils.commaDelimitedListToStringArray("a,,b");
        Assert.assertEquals("a,,b produces array length 3", 3, sa.length);
        Assert.assertTrue("components are correct", (((sa[0].equals("a")) && (sa[1].equals(""))) && (sa[2].equals("b"))));
        sa = new String[]{ "", "", "a", "" };
        doTestCommaDelimitedListToStringArrayLegalMatch(sa);
    }

    @Test
    public void testParseLocaleStringSunnyDay() {
        Locale expectedLocale = Locale.UK;
        Locale locale = StringUtils.parseLocaleString(expectedLocale.toString());
        Assert.assertNotNull("When given a bona-fide Locale string, must not return null.", locale);
        Assert.assertEquals(expectedLocale, locale);
    }

    @Test
    public void testParseLocaleStringWithMalformedLocaleString() {
        Locale locale = StringUtils.parseLocaleString("_banjo_on_my_knee");
        Assert.assertNotNull("When given a malformed Locale string, must not return null.", locale);
    }

    @Test
    public void testParseLocaleStringWithEmptyLocaleStringYieldsNullLocale() {
        Locale locale = StringUtils.parseLocaleString("");
        Assert.assertNull("When given an empty Locale string, must return null.", locale);
    }

    // SPR-8637
    @Test
    public void testParseLocaleWithMultiSpecialCharactersInVariant() {
        String variant = "proper-northern";
        String localeString = "en_GB_" + variant;
        Locale locale = StringUtils.parseLocaleString(localeString);
        Assert.assertEquals("Multi-valued variant portion of the Locale not extracted correctly.", variant, locale.getVariant());
    }

    // SPR-3671
    @Test
    public void testParseLocaleWithMultiValuedVariant() {
        String variant = "proper_northern";
        String localeString = "en_GB_" + variant;
        Locale locale = StringUtils.parseLocaleString(localeString);
        Assert.assertEquals("Multi-valued variant portion of the Locale not extracted correctly.", variant, locale.getVariant());
    }

    // SPR-3671
    @Test
    public void testParseLocaleWithMultiValuedVariantUsingSpacesAsSeparators() {
        String variant = "proper northern";
        String localeString = "en GB " + variant;
        Locale locale = StringUtils.parseLocaleString(localeString);
        Assert.assertEquals("Multi-valued variant portion of the Locale not extracted correctly.", variant, locale.getVariant());
    }

    // SPR-3671
    @Test
    public void testParseLocaleWithMultiValuedVariantUsingMixtureOfUnderscoresAndSpacesAsSeparators() {
        String variant = "proper northern";
        String localeString = "en_GB_" + variant;
        Locale locale = StringUtils.parseLocaleString(localeString);
        Assert.assertEquals("Multi-valued variant portion of the Locale not extracted correctly.", variant, locale.getVariant());
    }

    // SPR-3671
    @Test
    public void testParseLocaleWithMultiValuedVariantUsingSpacesAsSeparatorsWithLotsOfLeadingWhitespace() {
        String variant = "proper northern";
        String localeString = "en GB            " + variant;// lots of whitespace

        Locale locale = StringUtils.parseLocaleString(localeString);
        Assert.assertEquals("Multi-valued variant portion of the Locale not extracted correctly.", variant, locale.getVariant());
    }

    // SPR-3671
    @Test
    public void testParseLocaleWithMultiValuedVariantUsingUnderscoresAsSeparatorsWithLotsOfLeadingWhitespace() {
        String variant = "proper_northern";
        String localeString = "en_GB_____" + variant;// lots of underscores

        Locale locale = StringUtils.parseLocaleString(localeString);
        Assert.assertEquals("Multi-valued variant portion of the Locale not extracted correctly.", variant, locale.getVariant());
    }

    // SPR-7779
    @Test
    public void testParseLocaleWithInvalidCharacters() {
        try {
            StringUtils.parseLocaleString("%0D%0AContent-length:30%0D%0A%0D%0A%3Cscript%3Ealert%28123%29%3C/script%3E");
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    // SPR-9420
    @Test
    public void testParseLocaleWithSameLowercaseTokenForLanguageAndCountry() {
        Assert.assertEquals("tr_TR", StringUtils.parseLocaleString("tr_tr").toString());
        Assert.assertEquals("bg_BG_vnt", StringUtils.parseLocaleString("bg_bg_vnt").toString());
    }

    // SPR-11806
    @Test
    public void testParseLocaleWithVariantContainingCountryCode() {
        String variant = "GBtest";
        String localeString = "en_GB_" + variant;
        Locale locale = StringUtils.parseLocaleString(localeString);
        Assert.assertEquals("Variant containing country code not extracted correctly", variant, locale.getVariant());
    }

    // SPR-14718, SPR-7598
    @Test
    public void testParseJava7Variant() {
        Assert.assertEquals("sr__#LATN", StringUtils.parseLocaleString("sr__#LATN").toString());
    }

    // SPR-16651
    @Test
    public void testAvailableLocalesWithLocaleString() {
        for (Locale locale : Locale.getAvailableLocales()) {
            Locale parsedLocale = StringUtils.parseLocaleString(locale.toString());
            if (parsedLocale == null) {
                Assert.assertEquals("", locale.getLanguage());
            } else {
                Assert.assertEquals(parsedLocale.toString(), locale.toString());
            }
        }
    }

    // SPR-16651
    @Test
    public void testAvailableLocalesWithLanguageTag() {
        for (Locale locale : Locale.getAvailableLocales()) {
            Locale parsedLocale = StringUtils.parseLocale(locale.toLanguageTag());
            if (parsedLocale == null) {
                Assert.assertEquals("", locale.getLanguage());
            } else {
                Assert.assertEquals(parsedLocale.toLanguageTag(), locale.toLanguageTag());
            }
        }
    }
}

