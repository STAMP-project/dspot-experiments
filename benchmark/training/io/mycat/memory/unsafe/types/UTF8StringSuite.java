/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mycat.memory.unsafe.types;


import UTF8String.EMPTY_UTF8;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

import static com.google.common.collect.ImmutableMap.of;


public class UTF8StringSuite {
    @Test
    public void basicTest() throws UnsupportedEncodingException {
        UTF8StringSuite.checkBasic("", 0);
        UTF8StringSuite.checkBasic("hello", 5);
        UTF8StringSuite.checkBasic("? ? ? ?", 7);
    }

    @Test
    public void emptyStringTest() {
        Assert.assertEquals(EMPTY_UTF8, UTF8String.fromString(""));
        Assert.assertEquals(EMPTY_UTF8, UTF8String.fromBytes(new byte[0]));
        Assert.assertEquals(0, EMPTY_UTF8.numChars());
        Assert.assertEquals(0, EMPTY_UTF8.numBytes());
    }

    @Test
    public void prefix() {
        Assert.assertTrue((((UTF8String.fromString("a").getPrefix()) - (UTF8String.fromString("b").getPrefix())) < 0));
        Assert.assertTrue((((UTF8String.fromString("ab").getPrefix()) - (UTF8String.fromString("b").getPrefix())) < 0));
        Assert.assertTrue((((UTF8String.fromString("abbbbbbbbbbbasdf").getPrefix()) - (UTF8String.fromString("bbbbbbbbbbbbasdf").getPrefix())) < 0));
        Assert.assertTrue((((UTF8String.fromString("").getPrefix()) - (UTF8String.fromString("a").getPrefix())) < 0));
        Assert.assertTrue((((UTF8String.fromString("??").getPrefix()) - (UTF8String.fromString("??").getPrefix())) > 0));
        byte[] buf1 = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        byte[] buf2 = new byte[]{ 1, 2, 3 };
        UTF8String str1 = UTF8String.fromBytes(buf1, 0, 3);
        UTF8String str2 = UTF8String.fromBytes(buf1, 0, 8);
        UTF8String str3 = UTF8String.fromBytes(buf2);
        Assert.assertTrue((((str1.getPrefix()) - (str2.getPrefix())) < 0));
        Assert.assertEquals(str1.getPrefix(), str3.getPrefix());
    }

    @Test
    public void compareTo() {
        Assert.assertTrue(((UTF8String.fromString("").compareTo(UTF8String.fromString("a"))) < 0));
        Assert.assertTrue(((UTF8String.fromString("abc").compareTo(UTF8String.fromString("ABC"))) > 0));
        Assert.assertTrue(((UTF8String.fromString("abc0").compareTo(UTF8String.fromString("abc"))) > 0));
        Assert.assertTrue(((UTF8String.fromString("abcabcabc").compareTo(UTF8String.fromString("abcabcabc"))) == 0));
        Assert.assertTrue(((UTF8String.fromString("aBcabcabc").compareTo(UTF8String.fromString("Abcabcabc"))) > 0));
        Assert.assertTrue(((UTF8String.fromString("Abcabcabc").compareTo(UTF8String.fromString("abcabcabC"))) < 0));
        Assert.assertTrue(((UTF8String.fromString("abcabcabc").compareTo(UTF8String.fromString("abcabcabC"))) > 0));
        Assert.assertTrue(((UTF8String.fromString("abc").compareTo(UTF8String.fromString("??"))) < 0));
        Assert.assertTrue(((UTF8String.fromString("??").compareTo(UTF8String.fromString("??"))) > 0));
        Assert.assertTrue(((UTF8String.fromString("??123").compareTo(UTF8String.fromString("??122"))) > 0));
    }

    @Test
    public void upperAndLower() {
        UTF8StringSuite.testUpperandLower("", "");
        UTF8StringSuite.testUpperandLower("0123456", "0123456");
        UTF8StringSuite.testUpperandLower("ABCXYZ", "abcxyz");
        UTF8StringSuite.testUpperandLower("??????", "??????");
        UTF8StringSuite.testUpperandLower("???? ????", "???? ????");
    }

    @Test
    public void titleCase() {
        Assert.assertEquals(UTF8String.fromString(""), UTF8String.fromString("").toTitleCase());
        Assert.assertEquals(UTF8String.fromString("Ab Bc Cd"), UTF8String.fromString("ab bc cd").toTitleCase());
        Assert.assertEquals(UTF8String.fromString("? ? ? ? ? ?"), UTF8String.fromString("? ? ? ? ? ?").toTitleCase());
        Assert.assertEquals(UTF8String.fromString("???? ????"), UTF8String.fromString("???? ????").toTitleCase());
    }

    @Test
    public void concatTest() {
        Assert.assertEquals(EMPTY_UTF8, UTF8String.concat());
        Assert.assertNull(UTF8String.concat(((UTF8String) (null))));
        Assert.assertEquals(EMPTY_UTF8, UTF8String.concat(EMPTY_UTF8));
        Assert.assertEquals(UTF8String.fromString("ab"), UTF8String.concat(UTF8String.fromString("ab")));
        Assert.assertEquals(UTF8String.fromString("ab"), UTF8String.concat(UTF8String.fromString("a"), UTF8String.fromString("b")));
        Assert.assertEquals(UTF8String.fromString("abc"), UTF8String.concat(UTF8String.fromString("a"), UTF8String.fromString("b"), UTF8String.fromString("c")));
        Assert.assertNull(UTF8String.concat(UTF8String.fromString("a"), null, UTF8String.fromString("c")));
        Assert.assertNull(UTF8String.concat(UTF8String.fromString("a"), null, null));
        Assert.assertNull(UTF8String.concat(null, null, null));
        Assert.assertEquals(UTF8String.fromString("????"), UTF8String.concat(UTF8String.fromString("??"), UTF8String.fromString("??")));
    }

    @Test
    public void concatWsTest() {
        // Returns null if the separator is null
        Assert.assertNull(UTF8String.concatWs(null, ((UTF8String) (null))));
        Assert.assertNull(UTF8String.concatWs(null, UTF8String.fromString("a")));
        // If separator is null, concatWs should skip all null inputs and never return null.
        UTF8String sep = UTF8String.fromString("??");
        Assert.assertEquals(EMPTY_UTF8, UTF8String.concatWs(sep, EMPTY_UTF8));
        Assert.assertEquals(UTF8String.fromString("ab"), UTF8String.concatWs(sep, UTF8String.fromString("ab")));
        Assert.assertEquals(UTF8String.fromString("a??b"), UTF8String.concatWs(sep, UTF8String.fromString("a"), UTF8String.fromString("b")));
        Assert.assertEquals(UTF8String.fromString("a??b??c"), UTF8String.concatWs(sep, UTF8String.fromString("a"), UTF8String.fromString("b"), UTF8String.fromString("c")));
        Assert.assertEquals(UTF8String.fromString("a??c"), UTF8String.concatWs(sep, UTF8String.fromString("a"), null, UTF8String.fromString("c")));
        Assert.assertEquals(UTF8String.fromString("a"), UTF8String.concatWs(sep, UTF8String.fromString("a"), null, null));
        Assert.assertEquals(EMPTY_UTF8, UTF8String.concatWs(sep, null, null, null));
        Assert.assertEquals(UTF8String.fromString("??????"), UTF8String.concatWs(sep, UTF8String.fromString("??"), UTF8String.fromString("??")));
    }

    @Test
    public void contains() {
        Assert.assertTrue(EMPTY_UTF8.contains(EMPTY_UTF8));
        Assert.assertTrue(UTF8String.fromString("hello").contains(UTF8String.fromString("ello")));
        Assert.assertFalse(UTF8String.fromString("hello").contains(UTF8String.fromString("vello")));
        Assert.assertFalse(UTF8String.fromString("hello").contains(UTF8String.fromString("hellooo")));
        Assert.assertTrue(UTF8String.fromString("????").contains(UTF8String.fromString("???")));
        Assert.assertFalse(UTF8String.fromString("????").contains(UTF8String.fromString("??")));
        Assert.assertFalse(UTF8String.fromString("????").contains(UTF8String.fromString("?????")));
    }

    @Test
    public void startsWith() {
        Assert.assertTrue(EMPTY_UTF8.startsWith(EMPTY_UTF8));
        Assert.assertTrue(UTF8String.fromString("hello").startsWith(UTF8String.fromString("hell")));
        Assert.assertFalse(UTF8String.fromString("hello").startsWith(UTF8String.fromString("ell")));
        Assert.assertFalse(UTF8String.fromString("hello").startsWith(UTF8String.fromString("hellooo")));
        Assert.assertTrue(UTF8String.fromString("????").startsWith(UTF8String.fromString("??")));
        Assert.assertFalse(UTF8String.fromString("????").startsWith(UTF8String.fromString("?")));
        Assert.assertFalse(UTF8String.fromString("????").startsWith(UTF8String.fromString("?????")));
    }

    @Test
    public void endsWith() {
        Assert.assertTrue(EMPTY_UTF8.endsWith(EMPTY_UTF8));
        Assert.assertTrue(UTF8String.fromString("hello").endsWith(UTF8String.fromString("ello")));
        Assert.assertFalse(UTF8String.fromString("hello").endsWith(UTF8String.fromString("ellov")));
        Assert.assertFalse(UTF8String.fromString("hello").endsWith(UTF8String.fromString("hhhello")));
        Assert.assertTrue(UTF8String.fromString("????").endsWith(UTF8String.fromString("??")));
        Assert.assertFalse(UTF8String.fromString("????").endsWith(UTF8String.fromString("?")));
        Assert.assertFalse(UTF8String.fromString("????").endsWith(UTF8String.fromString("??????")));
    }

    @Test
    public void substring() {
        Assert.assertEquals(EMPTY_UTF8, UTF8String.fromString("hello").substring(0, 0));
        Assert.assertEquals(UTF8String.fromString("el"), UTF8String.fromString("hello").substring(1, 3));
        Assert.assertEquals(UTF8String.fromString("?"), UTF8String.fromString("????").substring(0, 1));
        Assert.assertEquals(UTF8String.fromString("??"), UTF8String.fromString("????").substring(1, 3));
        Assert.assertEquals(UTF8String.fromString("?"), UTF8String.fromString("????").substring(3, 5));
        Assert.assertEquals(UTF8String.fromString("??"), UTF8String.fromString("??").substring(0, 2));
    }

    @Test
    public void trims() {
        Assert.assertEquals(UTF8String.fromString("hello"), UTF8String.fromString("  hello ").trim());
        Assert.assertEquals(UTF8String.fromString("hello "), UTF8String.fromString("  hello ").trimLeft());
        Assert.assertEquals(UTF8String.fromString("  hello"), UTF8String.fromString("  hello ").trimRight());
        Assert.assertEquals(EMPTY_UTF8, UTF8String.fromString("  ").trim());
        Assert.assertEquals(EMPTY_UTF8, UTF8String.fromString("  ").trimLeft());
        Assert.assertEquals(EMPTY_UTF8, UTF8String.fromString("  ").trimRight());
        Assert.assertEquals(UTF8String.fromString("????"), UTF8String.fromString("  ???? ").trim());
        Assert.assertEquals(UTF8String.fromString("???? "), UTF8String.fromString("  ???? ").trimLeft());
        Assert.assertEquals(UTF8String.fromString("  ????"), UTF8String.fromString("  ???? ").trimRight());
        Assert.assertEquals(UTF8String.fromString("????"), UTF8String.fromString("????").trim());
        Assert.assertEquals(UTF8String.fromString("????"), UTF8String.fromString("????").trimLeft());
        Assert.assertEquals(UTF8String.fromString("????"), UTF8String.fromString("????").trimRight());
    }

    @Test
    public void indexOf() {
        Assert.assertEquals(0, EMPTY_UTF8.indexOf(EMPTY_UTF8, 0));
        Assert.assertEquals((-1), EMPTY_UTF8.indexOf(UTF8String.fromString("l"), 0));
        Assert.assertEquals(0, UTF8String.fromString("hello").indexOf(EMPTY_UTF8, 0));
        Assert.assertEquals(2, UTF8String.fromString("hello").indexOf(UTF8String.fromString("l"), 0));
        Assert.assertEquals(3, UTF8String.fromString("hello").indexOf(UTF8String.fromString("l"), 3));
        Assert.assertEquals((-1), UTF8String.fromString("hello").indexOf(UTF8String.fromString("a"), 0));
        Assert.assertEquals(2, UTF8String.fromString("hello").indexOf(UTF8String.fromString("ll"), 0));
        Assert.assertEquals((-1), UTF8String.fromString("hello").indexOf(UTF8String.fromString("ll"), 4));
        Assert.assertEquals(1, UTF8String.fromString("????").indexOf(UTF8String.fromString("??"), 0));
        Assert.assertEquals((-1), UTF8String.fromString("????").indexOf(UTF8String.fromString("?"), 3));
        Assert.assertEquals(0, UTF8String.fromString("????").indexOf(UTF8String.fromString("?"), 0));
        Assert.assertEquals(3, UTF8String.fromString("????").indexOf(UTF8String.fromString("?"), 0));
    }

    @Test
    public void substring_index() {
        Assert.assertEquals(UTF8String.fromString("www.apache.org"), UTF8String.fromString("www.apache.org").subStringIndex(UTF8String.fromString("."), 3));
        Assert.assertEquals(UTF8String.fromString("www.apache"), UTF8String.fromString("www.apache.org").subStringIndex(UTF8String.fromString("."), 2));
        Assert.assertEquals(UTF8String.fromString("www"), UTF8String.fromString("www.apache.org").subStringIndex(UTF8String.fromString("."), 1));
        Assert.assertEquals(UTF8String.fromString(""), UTF8String.fromString("www.apache.org").subStringIndex(UTF8String.fromString("."), 0));
        Assert.assertEquals(UTF8String.fromString("org"), UTF8String.fromString("www.apache.org").subStringIndex(UTF8String.fromString("."), (-1)));
        Assert.assertEquals(UTF8String.fromString("apache.org"), UTF8String.fromString("www.apache.org").subStringIndex(UTF8String.fromString("."), (-2)));
        Assert.assertEquals(UTF8String.fromString("www.apache.org"), UTF8String.fromString("www.apache.org").subStringIndex(UTF8String.fromString("."), (-3)));
        // str is empty string
        Assert.assertEquals(UTF8String.fromString(""), UTF8String.fromString("").subStringIndex(UTF8String.fromString("."), 1));
        // empty string delim
        Assert.assertEquals(UTF8String.fromString(""), UTF8String.fromString("www.apache.org").subStringIndex(UTF8String.fromString(""), 1));
        // delim does not exist in str
        Assert.assertEquals(UTF8String.fromString("www.apache.org"), UTF8String.fromString("www.apache.org").subStringIndex(UTF8String.fromString("#"), 2));
        // delim is 2 chars
        Assert.assertEquals(UTF8String.fromString("www||apache"), UTF8String.fromString("www||apache||org").subStringIndex(UTF8String.fromString("||"), 2));
        Assert.assertEquals(UTF8String.fromString("apache||org"), UTF8String.fromString("www||apache||org").subStringIndex(UTF8String.fromString("||"), (-2)));
        // non ascii chars
        Assert.assertEquals(UTF8String.fromString("?????"), UTF8String.fromString("????????").subStringIndex(UTF8String.fromString("?"), 2));
        // overlapped delim
        Assert.assertEquals(UTF8String.fromString("||"), UTF8String.fromString("||||||").subStringIndex(UTF8String.fromString("|||"), 3));
        Assert.assertEquals(UTF8String.fromString("|||"), UTF8String.fromString("||||||").subStringIndex(UTF8String.fromString("|||"), (-4)));
    }

    @Test
    public void reverse() {
        Assert.assertEquals(UTF8String.fromString("olleh"), UTF8String.fromString("hello").reverse());
        Assert.assertEquals(EMPTY_UTF8, EMPTY_UTF8.reverse());
        Assert.assertEquals(UTF8String.fromString("???"), UTF8String.fromString("???").reverse());
        Assert.assertEquals(UTF8String.fromString("??? olleh"), UTF8String.fromString("hello ???").reverse());
    }

    @Test
    public void repeat() {
        Assert.assertEquals(UTF8String.fromString("?d?d?d?d?d"), UTF8String.fromString("?d").repeat(5));
        Assert.assertEquals(UTF8String.fromString("?d"), UTF8String.fromString("?d").repeat(1));
        Assert.assertEquals(EMPTY_UTF8, UTF8String.fromString("?d").repeat((-1)));
    }

    @Test
    public void pad() {
        Assert.assertEquals(UTF8String.fromString("hel"), UTF8String.fromString("hello").lpad(3, UTF8String.fromString("????")));
        Assert.assertEquals(UTF8String.fromString("hello"), UTF8String.fromString("hello").lpad(5, UTF8String.fromString("????")));
        Assert.assertEquals(UTF8String.fromString("?hello"), UTF8String.fromString("hello").lpad(6, UTF8String.fromString("????")));
        Assert.assertEquals(UTF8String.fromString("???????hello"), UTF8String.fromString("hello").lpad(12, UTF8String.fromString("????")));
        Assert.assertEquals(UTF8String.fromString("?????hello"), UTF8String.fromString("hello").lpad(10, UTF8String.fromString("?????")));
        Assert.assertEquals(UTF8String.fromString("???????"), EMPTY_UTF8.lpad(7, UTF8String.fromString("?????")));
        Assert.assertEquals(UTF8String.fromString("hel"), UTF8String.fromString("hello").rpad(3, UTF8String.fromString("????")));
        Assert.assertEquals(UTF8String.fromString("hello"), UTF8String.fromString("hello").rpad(5, UTF8String.fromString("????")));
        Assert.assertEquals(UTF8String.fromString("hello?"), UTF8String.fromString("hello").rpad(6, UTF8String.fromString("????")));
        Assert.assertEquals(UTF8String.fromString("hello???????"), UTF8String.fromString("hello").rpad(12, UTF8String.fromString("????")));
        Assert.assertEquals(UTF8String.fromString("hello?????"), UTF8String.fromString("hello").rpad(10, UTF8String.fromString("?????")));
        Assert.assertEquals(UTF8String.fromString("???????"), EMPTY_UTF8.rpad(7, UTF8String.fromString("?????")));
        Assert.assertEquals(UTF8String.fromString("???"), UTF8String.fromString("????").lpad(3, UTF8String.fromString("????")));
        Assert.assertEquals(UTF8String.fromString("?????"), UTF8String.fromString("????").lpad(5, UTF8String.fromString("????")));
        Assert.assertEquals(UTF8String.fromString("??????"), UTF8String.fromString("????").lpad(6, UTF8String.fromString("????")));
        Assert.assertEquals(UTF8String.fromString("??????"), UTF8String.fromString("????").lpad(6, UTF8String.fromString("???")));
        Assert.assertEquals(UTF8String.fromString("???????"), UTF8String.fromString("????").lpad(7, UTF8String.fromString("???")));
        Assert.assertEquals(UTF8String.fromString("????????????"), UTF8String.fromString("????").lpad(12, UTF8String.fromString("???")));
        Assert.assertEquals(UTF8String.fromString("???"), UTF8String.fromString("????").rpad(3, UTF8String.fromString("????")));
        Assert.assertEquals(UTF8String.fromString("?????"), UTF8String.fromString("????").rpad(5, UTF8String.fromString("????")));
        Assert.assertEquals(UTF8String.fromString("??????"), UTF8String.fromString("????").rpad(6, UTF8String.fromString("????")));
        Assert.assertEquals(UTF8String.fromString("??????"), UTF8String.fromString("????").rpad(6, UTF8String.fromString("???")));
        Assert.assertEquals(UTF8String.fromString("???????"), UTF8String.fromString("????").rpad(7, UTF8String.fromString("???")));
        Assert.assertEquals(UTF8String.fromString("????????????"), UTF8String.fromString("????").rpad(12, UTF8String.fromString("???")));
        Assert.assertEquals(EMPTY_UTF8, UTF8String.fromString("????").lpad((-10), UTF8String.fromString("???")));
        Assert.assertEquals(EMPTY_UTF8, UTF8String.fromString("????").lpad((-10), EMPTY_UTF8));
        Assert.assertEquals(UTF8String.fromString("????"), UTF8String.fromString("????").lpad(5, EMPTY_UTF8));
        Assert.assertEquals(UTF8String.fromString("???"), UTF8String.fromString("????").lpad(3, EMPTY_UTF8));
        Assert.assertEquals(EMPTY_UTF8, EMPTY_UTF8.lpad(3, EMPTY_UTF8));
        Assert.assertEquals(EMPTY_UTF8, UTF8String.fromString("????").rpad((-10), UTF8String.fromString("???")));
        Assert.assertEquals(EMPTY_UTF8, UTF8String.fromString("????").rpad((-10), EMPTY_UTF8));
        Assert.assertEquals(UTF8String.fromString("????"), UTF8String.fromString("????").rpad(5, EMPTY_UTF8));
        Assert.assertEquals(UTF8String.fromString("???"), UTF8String.fromString("????").rpad(3, EMPTY_UTF8));
        Assert.assertEquals(EMPTY_UTF8, EMPTY_UTF8.rpad(3, EMPTY_UTF8));
    }

    @Test
    public void substringSQL() {
        UTF8String e = UTF8String.fromString("example");
        Assert.assertEquals(e.substringSQL(0, 2), UTF8String.fromString("ex"));
        Assert.assertEquals(e.substringSQL(1, 2), UTF8String.fromString("ex"));
        Assert.assertEquals(e.substringSQL(0, 7), UTF8String.fromString("example"));
        Assert.assertEquals(e.substringSQL(1, 2), UTF8String.fromString("ex"));
        Assert.assertEquals(e.substringSQL(0, 100), UTF8String.fromString("example"));
        Assert.assertEquals(e.substringSQL(1, 100), UTF8String.fromString("example"));
        Assert.assertEquals(e.substringSQL(2, 2), UTF8String.fromString("xa"));
        Assert.assertEquals(e.substringSQL(1, 6), UTF8String.fromString("exampl"));
        Assert.assertEquals(e.substringSQL(2, 100), UTF8String.fromString("xample"));
        Assert.assertEquals(e.substringSQL(0, 0), UTF8String.fromString(""));
        Assert.assertEquals(e.substringSQL(100, 4), EMPTY_UTF8);
        Assert.assertEquals(e.substringSQL(0, Integer.MAX_VALUE), UTF8String.fromString("example"));
        Assert.assertEquals(e.substringSQL(1, Integer.MAX_VALUE), UTF8String.fromString("example"));
        Assert.assertEquals(e.substringSQL(2, Integer.MAX_VALUE), UTF8String.fromString("xample"));
    }

    @Test
    public void split() {
        Assert.assertTrue(Arrays.equals(UTF8String.fromString("ab,def,ghi").split(UTF8String.fromString(","), (-1)), new UTF8String[]{ UTF8String.fromString("ab"), UTF8String.fromString("def"), UTF8String.fromString("ghi") }));
        Assert.assertTrue(Arrays.equals(UTF8String.fromString("ab,def,ghi").split(UTF8String.fromString(","), 2), new UTF8String[]{ UTF8String.fromString("ab"), UTF8String.fromString("def,ghi") }));
        Assert.assertTrue(Arrays.equals(UTF8String.fromString("ab,def,ghi").split(UTF8String.fromString(","), 2), new UTF8String[]{ UTF8String.fromString("ab"), UTF8String.fromString("def,ghi") }));
    }

    @Test
    public void levenshteinDistance() {
        Assert.assertEquals(0, EMPTY_UTF8.levenshteinDistance(EMPTY_UTF8));
        Assert.assertEquals(1, EMPTY_UTF8.levenshteinDistance(UTF8String.fromString("a")));
        Assert.assertEquals(7, UTF8String.fromString("aaapppp").levenshteinDistance(EMPTY_UTF8));
        Assert.assertEquals(1, UTF8String.fromString("frog").levenshteinDistance(UTF8String.fromString("fog")));
        Assert.assertEquals(3, UTF8String.fromString("fly").levenshteinDistance(UTF8String.fromString("ant")));
        Assert.assertEquals(7, UTF8String.fromString("elephant").levenshteinDistance(UTF8String.fromString("hippo")));
        Assert.assertEquals(7, UTF8String.fromString("hippo").levenshteinDistance(UTF8String.fromString("elephant")));
        Assert.assertEquals(8, UTF8String.fromString("hippo").levenshteinDistance(UTF8String.fromString("zzzzzzzz")));
        Assert.assertEquals(1, UTF8String.fromString("hello").levenshteinDistance(UTF8String.fromString("hallo")));
        Assert.assertEquals(4, UTF8String.fromString("????").levenshteinDistance(UTF8String.fromString("?a?b")));
    }

    @Test
    public void translate() {
        Assert.assertEquals(UTF8String.fromString("1a2s3ae"), UTF8String.fromString("translate").translate(com.google.common.collect.ImmutableMap.of('r', '1', 'n', '2', 'l', '3', 't', '\u0000')));
        Assert.assertEquals(UTF8String.fromString("translate"), UTF8String.fromString("translate").translate(new java.util.HashMap<Character, Character>()));
        Assert.assertEquals(UTF8String.fromString("asae"), UTF8String.fromString("translate").translate(com.google.common.collect.ImmutableMap.of('r', '\u0000', 'n', '\u0000', 'l', '\u0000', 't', '\u0000')));
        Assert.assertEquals(UTF8String.fromString("aa?b"), UTF8String.fromString("????").translate(of('?', 'a', '?', 'b')));
    }

    @Test
    public void createBlankString() {
        Assert.assertEquals(UTF8String.fromString(" "), UTF8String.blankString(1));
        Assert.assertEquals(UTF8String.fromString("  "), UTF8String.blankString(2));
        Assert.assertEquals(UTF8String.fromString("   "), UTF8String.blankString(3));
        Assert.assertEquals(UTF8String.fromString(""), UTF8String.blankString(0));
    }

    @Test
    public void findInSet() {
        Assert.assertEquals(1, UTF8String.fromString("ab").findInSet(UTF8String.fromString("ab")));
        Assert.assertEquals(2, UTF8String.fromString("a,b").findInSet(UTF8String.fromString("b")));
        Assert.assertEquals(3, UTF8String.fromString("abc,b,ab,c,def").findInSet(UTF8String.fromString("ab")));
        Assert.assertEquals(1, UTF8String.fromString("ab,abc,b,ab,c,def").findInSet(UTF8String.fromString("ab")));
        Assert.assertEquals(4, UTF8String.fromString(",,,ab,abc,b,ab,c,def").findInSet(UTF8String.fromString("ab")));
        Assert.assertEquals(1, UTF8String.fromString(",ab,abc,b,ab,c,def").findInSet(UTF8String.fromString("")));
        Assert.assertEquals(4, UTF8String.fromString("????,abc,b,ab,c,def").findInSet(UTF8String.fromString("ab")));
        Assert.assertEquals(6, UTF8String.fromString("????,abc,b,ab,c,def").findInSet(UTF8String.fromString("def")));
    }

    @Test
    public void soundex() {
        Assert.assertEquals(UTF8String.fromString("Robert").soundex(), UTF8String.fromString("R163"));
        Assert.assertEquals(UTF8String.fromString("Rupert").soundex(), UTF8String.fromString("R163"));
        Assert.assertEquals(UTF8String.fromString("Rubin").soundex(), UTF8String.fromString("R150"));
        Assert.assertEquals(UTF8String.fromString("Ashcraft").soundex(), UTF8String.fromString("A261"));
        Assert.assertEquals(UTF8String.fromString("Ashcroft").soundex(), UTF8String.fromString("A261"));
        Assert.assertEquals(UTF8String.fromString("Burroughs").soundex(), UTF8String.fromString("B620"));
        Assert.assertEquals(UTF8String.fromString("Burrows").soundex(), UTF8String.fromString("B620"));
        Assert.assertEquals(UTF8String.fromString("Ekzampul").soundex(), UTF8String.fromString("E251"));
        Assert.assertEquals(UTF8String.fromString("Example").soundex(), UTF8String.fromString("E251"));
        Assert.assertEquals(UTF8String.fromString("Ellery").soundex(), UTF8String.fromString("E460"));
        Assert.assertEquals(UTF8String.fromString("Euler").soundex(), UTF8String.fromString("E460"));
        Assert.assertEquals(UTF8String.fromString("Ghosh").soundex(), UTF8String.fromString("G200"));
        Assert.assertEquals(UTF8String.fromString("Gauss").soundex(), UTF8String.fromString("G200"));
        Assert.assertEquals(UTF8String.fromString("Gutierrez").soundex(), UTF8String.fromString("G362"));
        Assert.assertEquals(UTF8String.fromString("Heilbronn").soundex(), UTF8String.fromString("H416"));
        Assert.assertEquals(UTF8String.fromString("Hilbert").soundex(), UTF8String.fromString("H416"));
        Assert.assertEquals(UTF8String.fromString("Jackson").soundex(), UTF8String.fromString("J250"));
        Assert.assertEquals(UTF8String.fromString("Kant").soundex(), UTF8String.fromString("K530"));
        Assert.assertEquals(UTF8String.fromString("Knuth").soundex(), UTF8String.fromString("K530"));
        Assert.assertEquals(UTF8String.fromString("Lee").soundex(), UTF8String.fromString("L000"));
        Assert.assertEquals(UTF8String.fromString("Lukasiewicz").soundex(), UTF8String.fromString("L222"));
        Assert.assertEquals(UTF8String.fromString("Lissajous").soundex(), UTF8String.fromString("L222"));
        Assert.assertEquals(UTF8String.fromString("Ladd").soundex(), UTF8String.fromString("L300"));
        Assert.assertEquals(UTF8String.fromString("Lloyd").soundex(), UTF8String.fromString("L300"));
        Assert.assertEquals(UTF8String.fromString("Moses").soundex(), UTF8String.fromString("M220"));
        Assert.assertEquals(UTF8String.fromString("O'Hara").soundex(), UTF8String.fromString("O600"));
        Assert.assertEquals(UTF8String.fromString("Pfister").soundex(), UTF8String.fromString("P236"));
        Assert.assertEquals(UTF8String.fromString("Rubin").soundex(), UTF8String.fromString("R150"));
        Assert.assertEquals(UTF8String.fromString("Robert").soundex(), UTF8String.fromString("R163"));
        Assert.assertEquals(UTF8String.fromString("Rupert").soundex(), UTF8String.fromString("R163"));
        Assert.assertEquals(UTF8String.fromString("Soundex").soundex(), UTF8String.fromString("S532"));
        Assert.assertEquals(UTF8String.fromString("Sownteks").soundex(), UTF8String.fromString("S532"));
        Assert.assertEquals(UTF8String.fromString("Tymczak").soundex(), UTF8String.fromString("T522"));
        Assert.assertEquals(UTF8String.fromString("VanDeusen").soundex(), UTF8String.fromString("V532"));
        Assert.assertEquals(UTF8String.fromString("Washington").soundex(), UTF8String.fromString("W252"));
        Assert.assertEquals(UTF8String.fromString("Wheaton").soundex(), UTF8String.fromString("W350"));
        Assert.assertEquals(UTF8String.fromString("a").soundex(), UTF8String.fromString("A000"));
        Assert.assertEquals(UTF8String.fromString("ab").soundex(), UTF8String.fromString("A100"));
        Assert.assertEquals(UTF8String.fromString("abc").soundex(), UTF8String.fromString("A120"));
        Assert.assertEquals(UTF8String.fromString("abcd").soundex(), UTF8String.fromString("A123"));
        Assert.assertEquals(UTF8String.fromString("").soundex(), UTF8String.fromString(""));
        Assert.assertEquals(UTF8String.fromString("123").soundex(), UTF8String.fromString("123"));
        Assert.assertEquals(UTF8String.fromString("????").soundex(), UTF8String.fromString("????"));
    }
}

