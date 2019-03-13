/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package libcore.java.util.regex;


import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import junit.framework.TestCase;


public class OldMatcherTest extends TestCase {
    String[] groupPatterns = new String[]{ "(a|b)*aabb", "((a)|b)*aabb", "((a|b)*)a(abb)", "(((a)|(b))*)aabb", "(((a)|(b))*)aa(b)b", "(((a)|(b))*)a(a(b)b)" };

    public void testAppendReplacement() {
        Pattern pat = Pattern.compile("XX");
        Matcher m = pat.matcher("Today is XX-XX-XX ...");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; m.find(); i++) {
            m.appendReplacement(sb, new Integer(((i * 10) + i)).toString());
        }
        m.appendTail(sb);
        TestCase.assertEquals("Today is 0-11-22 ...", sb.toString());
        pat = Pattern.compile("cat");
        m = pat.matcher("one-cat-two-cats-in-the-yard");
        sb = new StringBuffer();
        Throwable t = null;
        m.find();
        try {
            m.appendReplacement(null, "dog");
        } catch (NullPointerException e) {
            t = e;
        }
        TestCase.assertNotNull(t);
        t = null;
        m.find();
        try {
            m.appendReplacement(sb, null);
        } catch (NullPointerException e) {
            t = e;
        }
        TestCase.assertNotNull(t);
    }

    public void test_resetLjava_lang_String() {
        String testPattern = "(abb)";
        String testString1 = "babbabbcccabbabbabbabbabb";
        String testString2 = "cddcddcddcddcddbbbb";
        Pattern pat = Pattern.compile(testPattern);
        Matcher mat = pat.matcher(testString1);
        while (mat.find());
        TestCase.assertEquals("Reset should return itself 1", mat, mat.reset(testString2));
        TestCase.assertFalse("After reset matcher should not find pattern in given input", mat.find());
        TestCase.assertEquals("Reset should return itself 2", mat, mat.reset(testString1));
        TestCase.assertTrue("After reset matcher should find pattern in given input", mat.find());
    }

    public void testAppendTail() {
        Pattern p = Pattern.compile("cat");
        Matcher m = p.matcher("one-cat-two-cats-in-the-yard");
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "dog");
        } 
        m.appendTail(sb);
        TestCase.assertEquals("one-dog-two-dogs-in-the-yard", sb.toString());
        p = Pattern.compile("cat|yard");
        m = p.matcher("one-cat-two-cats-in-the-yard");
        sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "dog");
        } 
        TestCase.assertEquals("one-dog-two-dogs-in-the-dog", sb.toString());
        m.appendTail(sb);
        TestCase.assertEquals("one-dog-two-dogs-in-the-dog", sb.toString());
        p = Pattern.compile("cat");
        m = p.matcher("one-cat-two-cats-in-the-yard");
        sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "dog");
        } 
        Throwable t = null;
        try {
            m.appendTail(null);
        } catch (NullPointerException e) {
            t = e;
        }
        TestCase.assertNotNull(t);
    }

    public void test_reset() {
        String testPattern = "(abb)";
        String testString = "babbabbcccabbabbabbabbabb";
        Pattern pat = Pattern.compile(testPattern);
        Matcher mat = pat.matcher(testString);
        while (mat.find());
        TestCase.assertEquals("Reset should return itself", mat, mat.reset());
        TestCase.assertTrue("After reset matcher should find pattern in given input", mat.find());
    }

    public void test_hasAnchoringBounds() {
        String testPattern = "abb";
        String testString = "abb";
        Pattern pat = Pattern.compile(testPattern);
        Matcher mat = pat.matcher(testString);
        TestCase.assertTrue("Matcher uses anchoring bound by default", mat.hasAnchoringBounds());
        Matcher mu = mat.useAnchoringBounds(true);
        TestCase.assertTrue("Incorrect value of anchoring bounds", mu.hasAnchoringBounds());
        mu = mat.useAnchoringBounds(false);
        TestCase.assertFalse("Incorrect value of anchoring bounds", mu.hasAnchoringBounds());
    }

    public void test_hasTransparentBounds() {
        String testPattern = "abb";
        String testString = "ab\nb";
        Pattern pat = Pattern.compile(testPattern);
        Matcher mat = pat.matcher(testString);
        TestCase.assertFalse("Matcher uses opaque bounds by default", mat.hasTransparentBounds());
        Matcher mu = mat.useTransparentBounds(true);
        TestCase.assertTrue("Incorrect value of anchoring bounds", mu.hasTransparentBounds());
        mu = mat.useTransparentBounds(false);
        TestCase.assertFalse("Incorrect value of anchoring bounds", mu.hasTransparentBounds());
    }

    public void test_startI() {
        String testPattern = "(((abb)a)(bb))";
        String testString = "cccabbabbabbabbabb";
        Pattern pat = Pattern.compile(testPattern);
        Matcher mat = pat.matcher(testString);
        int start = 3;
        int end = 6;
        int i;
        int j;
        for (j = 0; j < 3; j++) {
            while (mat.find(((start + j) - 2))) {
                for (i = 0; i < 4; i++) {
                    TestCase.assertEquals(((("Start is wrong for group " + i) + " :") + (mat.group(i))), start, mat.start(i));
                }
                TestCase.assertEquals(((("Start is wrong for group " + i) + " :") + (mat.group(i))), (start + 4), mat.start(i));
                start = end;
                end += 3;
            } 
        }
    }

    public void test_endI() {
        String testPattern = "(((abb)a)(bb))";
        String testString = "cccabbabbabbabbabb";
        Pattern pat = Pattern.compile(testPattern);
        Matcher mat = pat.matcher(testString);
        int start = 3;
        int end = 6;
        int i;
        int j;
        for (j = 0; j < 3; j++) {
            while (mat.find(((start + j) - 2))) {
                for (i = 0; i < 4; i++) {
                    TestCase.assertEquals(((("End is wrong for group " + i) + " :") + (mat.group(i))), (start + (mat.group(i).length())), mat.end(i));
                }
                TestCase.assertEquals(((("End is wrong for group " + i) + " :") + (mat.group(i))), ((start + 4) + (mat.group(i).length())), mat.end(i));
                start = end;
                end += 3;
            } 
        }
    }

    public void test_lookingAt() {
        String testPattern = "(((abb)a)(bb))";
        String testString1 = "babbabbcccabbabbabbabbabb";
        String testString2 = "abbabb";
        Pattern pat = Pattern.compile(testPattern);
        Matcher mat1 = pat.matcher(testString1);
        Matcher mat2 = pat.matcher(testString2);
        TestCase.assertFalse("Should not find given pattern in 1 string", mat1.lookingAt());
        mat1.region(1, 10);
        TestCase.assertTrue("Should find given pattern in region of string", mat1.lookingAt());
        TestCase.assertTrue("Should find given pattern in 2 string", mat2.lookingAt());
    }

    public void test_findI() {
        String testPattern = "(abb)";
        String testString = "cccabbabbabbabbabb";
        Pattern pat = Pattern.compile(testPattern);
        Matcher mat = pat.matcher(testString);
        int start = 3;
        int end = 6;
        int j;
        for (j = 0; j < 3; j++) {
            while (mat.find(((start + j) - 2))) {
                TestCase.assertEquals(start, mat.start(1));
                TestCase.assertEquals(end, mat.end(1));
                start = end;
                end += 3;
            } 
            start = 6;
            end = 9;
        }
        testPattern = "(\\d{1,3})";
        testString = "aaaa123456789045";
        Pattern pat2 = Pattern.compile(testPattern);
        Matcher mat2 = pat2.matcher(testString);
        start = 4;
        int length = 3;
        for (j = 0; j < length; j++) {
            for (int i = 4 + j; i < ((testString.length()) - length); i += length) {
                mat2.find(i);
                TestCase.assertEquals(testString.substring(i, (i + length)), mat2.group(1));
            }
        }
        String string3 = "Brave new world";
        Pattern pat3 = Pattern.compile("new");
        Matcher mat3 = pat3.matcher(string3);
        // find(int) throws for out of range indexes.
        try {
            mat3.find((-1));
            TestCase.fail();
        } catch (IndexOutOfBoundsException expected) {
        }
        TestCase.assertFalse(mat3.find(string3.length()));
        try {
            mat3.find(((string3.length()) + 1));
            TestCase.fail();
        } catch (IndexOutOfBoundsException expected) {
        }
        TestCase.assertTrue(mat3.find(6));
        TestCase.assertFalse(mat3.find(7));
        mat3.region(7, 10);
        TestCase.assertFalse(mat3.find());// No "new" in the region.

        TestCase.assertTrue(mat3.find(3));// find(int) ignores the region.

        TestCase.assertTrue(mat3.find(6));// find(int) ignores the region.

        TestCase.assertFalse(mat3.find(7));// No "new" >= 7.

        mat3.region(1, 4);
        TestCase.assertFalse(mat3.find());// No "new" in the region.

        TestCase.assertTrue(mat3.find(5));// find(int) ignores the region.

    }

    public void testSEOLsymbols() {
        Pattern pat = Pattern.compile("^a\\(bb\\[$");
        Matcher mat = pat.matcher("a(bb[");
        TestCase.assertTrue(mat.matches());
    }

    public void test_start() {
        String testPattern = "(abb)";
        String testString = "cccabbabbabbabbabb";
        Pattern pat = Pattern.compile(testPattern);
        Matcher mat = pat.matcher(testString);
        int start = 3;
        int end = 6;
        int j;
        for (j = 0; j < 3; j++) {
            while (mat.find()) {
                TestCase.assertEquals("Start is wrong", start, mat.start());
                start = end;
                end += 3;
            } 
        }
    }

    public void test_end() {
        String testPattern = "(abb)";
        String testString = "cccabbabbabbabbabb";
        Pattern pat = Pattern.compile(testPattern);
        Matcher mat = pat.matcher(testString);
        int start = 3;
        int end = 6;
        int j;
        for (j = 0; j < 3; j++) {
            while (mat.find()) {
                TestCase.assertEquals("Start is wrong", end, mat.end());
                start = end;
                end += 3;
            } 
        }
    }

    public void testGroupCount() {
        for (int i = 0; i < (groupPatterns.length); i++) {
            Pattern test = Pattern.compile(groupPatterns[i]);
            Matcher mat = test.matcher("ababababbaaabb");
            mat.matches();
            TestCase.assertEquals((i + 1), mat.groupCount());
        }
    }

    public void testRegion() {
        Pattern p = Pattern.compile("abba");
        Matcher m = p.matcher("Gabba gabba hey");
        m.region(0, 15);
        TestCase.assertTrue(m.find());
        TestCase.assertTrue(m.find());
        TestCase.assertFalse(m.find());
        m.region(5, 15);
        TestCase.assertTrue(m.find());
        TestCase.assertFalse(m.find());
        m.region(10, 15);
        TestCase.assertFalse(m.find());
        Throwable t = null;
        try {
            m.region((-1), 15);
        } catch (IndexOutOfBoundsException e) {
            t = e;
        }
        TestCase.assertNotNull(t);
        t = null;
        try {
            m.region(0, 16);
        } catch (IndexOutOfBoundsException e) {
            t = e;
        }
        TestCase.assertNotNull(t);
    }

    public void testMatchesURI() {
        Pattern pat = Pattern.compile("^(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?");
        Matcher mat = pat.matcher(("file:/c:/workspace/api/build.win32/classes/META-INF/" + "services/javax.xml.parsers.DocumentBuilderFactory"));
        TestCase.assertTrue(mat.matches());
    }

    public void testQuoteReplacement() {
        TestCase.assertEquals("\\$dollar and slash\\\\", Matcher.quoteReplacement("$dollar and slash\\"));
    }

    public void testUnicode() {
        TestCase.assertTrue(Pattern.compile("\\x61a").matcher("aa").matches());
        // assertTrue(Pattern.matches("\\u0061a", "aa"));
        TestCase.assertTrue(Pattern.compile("\\0141a").matcher("aa").matches());
        TestCase.assertTrue(Pattern.compile("\\0777").matcher("?7").matches());
    }

    public void testUnicodeCategory() {
        TestCase.assertTrue(Pattern.compile("\\p{Ll}").matcher("k").matches());// Unicode lower case

        TestCase.assertTrue(Pattern.compile("\\P{Ll}").matcher("K").matches());// Unicode non-lower

        // case
        TestCase.assertTrue(Pattern.compile("\\p{Lu}").matcher("K").matches());// Unicode upper case

        TestCase.assertTrue(Pattern.compile("\\P{Lu}").matcher("k").matches());// Unicode non-upper

        // case
        // combinations
        TestCase.assertTrue(Pattern.compile("[\\p{L}&&[^\\p{Lu}]]").matcher("k").matches());
        TestCase.assertTrue(Pattern.compile("[\\p{L}&&[^\\p{Ll}]]").matcher("K").matches());
        TestCase.assertFalse(Pattern.compile("[\\p{L}&&[^\\p{Lu}]]").matcher("K").matches());
        TestCase.assertFalse(Pattern.compile("[\\p{L}&&[^\\p{Ll}]]").matcher("k").matches());
        // category/character combinations
        TestCase.assertFalse(Pattern.compile("[\\p{L}&&[^a-z]]").matcher("k").matches());
        TestCase.assertTrue(Pattern.compile("[\\p{L}&&[^a-z]]").matcher("K").matches());
        TestCase.assertTrue(Pattern.compile("[\\p{Lu}a-z]").matcher("k").matches());
        TestCase.assertTrue(Pattern.compile("[a-z\\p{Lu}]").matcher("k").matches());
        TestCase.assertFalse(Pattern.compile("[\\p{Lu}a-d]").matcher("k").matches());
        TestCase.assertTrue(Pattern.compile("[a-d\\p{Lu}]").matcher("K").matches());
        // assertTrue(Pattern.matches("[\\p{L}&&[^\\p{Lu}&&[^K]]]", "K"));
        TestCase.assertFalse(Pattern.compile("[\\p{L}&&[^\\p{Lu}&&[^G]]]").matcher("K").matches());
    }

    public void test_regionStart() {
        String testPattern = "(abb)";
        String testString = "cccabbabbabbabbabb";
        Pattern pat = Pattern.compile(testPattern);
        Matcher mat = pat.matcher(testString);
        TestCase.assertEquals("Region sould start from 0 position", 0, mat.regionStart());
        mat.region(1, 10);
        TestCase.assertEquals("Region sould start from 1 position after setting new region", 1, mat.regionStart());
        mat.reset();
        TestCase.assertEquals("Region sould start from 0 position after reset", 0, mat.regionStart());
    }

    public void test_regionEnd() {
        String testPattern = "(abb)";
        String testString = "cccabbabbabbabbabb";
        Pattern pat = Pattern.compile(testPattern);
        Matcher mat = pat.matcher(testString);
        TestCase.assertEquals("Region end value should be equal to string length", testString.length(), mat.regionEnd());
        mat.region(1, 10);
        TestCase.assertEquals("Region end value should be equal to 10 after setting new region", 10, mat.regionEnd());
        mat.reset();
        TestCase.assertEquals("Region end value should be equal to string length after reset", testString.length(), mat.regionEnd());
    }

    public void test_toMatchResult() {
        String testPattern = "(((abb)a)(bb))";
        String testString = "babbabbcccabbabbabbabbabb";
        Pattern pat = Pattern.compile(testPattern);
        Matcher mat = pat.matcher(testString);
        mat.region(1, 7);
        TestCase.assertTrue("matcher should find pattern in given region", mat.matches());
        TestCase.assertEquals("matched section should start from 1 position", 1, mat.toMatchResult().start());
        TestCase.assertEquals("matched section for 2 group should start from 1 position", 1, mat.toMatchResult().start(2));
        TestCase.assertEquals("matched section for whole pattern should end on 7 position", 7, mat.toMatchResult().end());
        TestCase.assertEquals("matched section for 3 group should end at 4 position", 4, mat.toMatchResult().end(3));
        TestCase.assertEquals("group not matched", "abbabb", mat.toMatchResult().group());
        TestCase.assertEquals("3 group not matched", "abb", mat.toMatchResult().group(3));
        TestCase.assertEquals("Total number of groups does not matched with given pattern", 4, mat.toMatchResult().groupCount());
    }

    public void test_usePatternLjava_util_regex_Pattern() {
        String testPattern1 = "(((abb)a)(bb))";
        String testPattern2 = "(abbabb)";
        String testPattern3 = "(babb)";
        String testString = "babbabbcccabbabbabbabbabb";
        Pattern pat = Pattern.compile(testPattern1);
        Matcher mat = pat.matcher(testString);
        mat.region(1, 7);
        TestCase.assertTrue("matcher should find pattern in given region in case of groupe in pattern", mat.matches());
        TestCase.assertEquals("", mat, mat.usePattern(Pattern.compile(testPattern2)));
        TestCase.assertTrue("matcher should find pattern in given region", mat.matches());
        TestCase.assertEquals("", mat, mat.usePattern(Pattern.compile(testPattern3)));
        TestCase.assertFalse("matcher should not find pattern in given region", mat.matches());
    }

    public void test_anchoringBounds() {
        String testPattern = "^ro$";
        String testString = "android";
        Pattern pat = Pattern.compile(testPattern);
        Matcher mat = pat.matcher(testString);
        mat.region(2, 5);
        mat.useAnchoringBounds(false);
        TestCase.assertFalse("Shouldn't find pattern with non-anchoring bounds", mat.find(0));
        mat.region(2, 5);
        mat.useAnchoringBounds(true);
        TestCase.assertFalse("Should find pattern with anchoring bounds", mat.find(0));
    }

    public void test_transparentBounds() {
        String testPattern = "and(?=roid)";
        String testString = "android";
        Pattern pat = Pattern.compile(testPattern);
        Matcher mat = pat.matcher(testString);
        mat.region(0, 3);
        mat.useTransparentBounds(false);
        TestCase.assertFalse("Shouldn't find pattern with opaque bounds", mat.matches());
        mat.useTransparentBounds(true);
        TestCase.assertTrue("Should find pattern transparent bounds", mat.matches());// ***

        testPattern = "and(?!roid)";
        testString = "android";
        pat = Pattern.compile(testPattern);
        mat = pat.matcher(testString);
        mat.region(0, 3);
        mat.useTransparentBounds(false);
        TestCase.assertTrue("Should find pattern with opaque bounds", mat.matches());
        mat.useTransparentBounds(true);
        TestCase.assertFalse("Shouldn't find pattern transparent bounds", mat.matches());// ***

    }

    public void test_hitEnd() {
        String testPattern = "abb";
        String testString = "babbabbcccabbabbabbabbabb";
        Pattern pat = Pattern.compile(testPattern);
        Matcher mat = pat.matcher(testString);
        while (mat.find()) {
            TestCase.assertFalse("hitEnd should return false during parsing input", mat.hitEnd());
        } 
        TestCase.assertTrue("hitEnd should return true after finding last match", mat.hitEnd());// ***

    }

    public void test_requireEnd() {
        String testPattern = "bba";
        String testString = "abbbbba";
        Pattern pat = Pattern.compile(testPattern);
        Matcher mat = pat.matcher(testString);
        TestCase.assertTrue(mat.find());
        TestCase.assertFalse(mat.requireEnd());
        testPattern = "bba$";
        testString = "abbbbba";
        pat = Pattern.compile(testPattern);
        mat = pat.matcher(testString);
        TestCase.assertTrue(mat.find());
        TestCase.assertTrue(mat.requireEnd());
    }

    /* Regression test for HARMONY-674 */
    public void testPatternMatcher() throws Exception {
        Pattern pattern = Pattern.compile("(?:\\d+)(?:pt)");
        TestCase.assertTrue(pattern.matcher("14pt").matches());
    }

    public void testUnicodeCharacterClasses() throws Exception {
        // http://code.google.com/p/android/issues/detail?id=21176
        // We use the Unicode TR-18 definitions: http://www.unicode.org/reports/tr18/#Compatibility_Properties
        TestCase.assertTrue("\u0666".matches("\\d"));// ARABIC-INDIC DIGIT SIX

        TestCase.assertFalse("\u0666".matches("\\D"));// ARABIC-INDIC DIGIT SIX

        TestCase.assertTrue("\u1680".matches("\\s"));// OGHAM SPACE MARK

        TestCase.assertFalse("\u1680".matches("\\S"));// OGHAM SPACE MARK

        TestCase.assertTrue("\u00ea".matches("\\w"));// LATIN SMALL LETTER E WITH CIRCUMFLEX

        TestCase.assertFalse("\u00ea".matches("\\W"));// LATIN SMALL LETTER E WITH CIRCUMFLEX

    }

    // http://code.google.com/p/android/issues/detail?id=41143
    public void testConcurrentMatcherAccess() throws Exception {
        final Pattern p = Pattern.compile("(^|\\W)([a-z])");
        final Matcher m = p.matcher("");
        ArrayList<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < 10; ++i) {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    for (int i = 0; i < 4096; ++i) {
                        String s = "some example text";
                        m.reset(s);
                        try {
                            StringBuffer sb = new StringBuffer(s.length());
                            while (m.find()) {
                                m.appendReplacement(sb, ((m.group(1)) + (m.group(2))));
                            } 
                            m.appendTail(sb);
                        } catch (Exception expected) {
                            // This code is inherently unsafe and crazy;
                            // we're just trying to provoke native crashes!
                        }
                    }
                }
            });
            threads.add(t);
        }
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
    }

    // https://code.google.com/p/android/issues/detail?id=33040
    public void test33040() throws Exception {
        Pattern p = Pattern.compile("ma");
        // replaceFirst resets the region; apparently, this was broken in Android 1.6.
        String result = p.matcher("mama").region(2, 4).replaceFirst("mi");
        TestCase.assertEquals("mima", result);
    }
}

