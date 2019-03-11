/**
 * Copyright 2015 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
package org.teavm.classlib.java.util.regex;


import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


@SuppressWarnings("nls")
@RunWith(TeaVMTestRunner.class)
public class MatcherTest {
    String[] testPatterns = new String[]{ "(a|b)*abb", "(1*2*3*4*)*567", "(a|b|c|d)*aab", "(1|2|3|4|5|6|7|8|9|0)(1|2|3|4|5|6|7|8|9|0)*", "(abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ)*", "(a|b)*(a|b)*A(a|b)*lice.*", "(a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z)(a|b|c|d|e|f|g|h|" + "i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z)*(1|2|3|4|5|6|7|8|9|0)*|while|for|struct|if|do" };

    String[] groupPatterns = new String[]{ "(a|b)*aabb", "((a)|b)*aabb", "((a|b)*)a(abb)", "(((a)|(b))*)aabb", "(((a)|(b))*)aa(b)b", "(((a)|(b))*)a(a(b)b)" };

    @Test
    public void testRegionsIntInt() {
        Pattern p = Pattern.compile("x*");
        Matcher m = p.matcher("axxxxxa");
        Assert.assertFalse(m.matches());
        m.region(1, 6);
        Assert.assertEquals(1, m.regionStart());
        Assert.assertEquals(6, m.regionEnd());
        Assert.assertTrue(m.matches());
        try {
            m.region(1, 0);
            Assert.fail("expected an IOOBE");
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        try {
            m.region((-1), 2);
            Assert.fail("expected an IOOBE");
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        try {
            m.region(10, 11);
            Assert.fail("expected an IOOBE");
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        try {
            m.region(1, 10);
            Assert.fail("expected an IOOBE");
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
    }

    @Test
    public void testAppendReplacement() {
        Pattern pat = Pattern.compile("XX");
        Matcher m = pat.matcher("Today is XX-XX-XX ...");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; m.find(); i++) {
            m.appendReplacement(sb, new Integer(((i * 10) + i)).toString());
        }
        m.appendTail(sb);
        Assert.assertEquals("Today is 0-11-22 ...", sb.toString());
    }

    @Test
    public void testAppendReplacementRef() {
        Pattern p = Pattern.compile("xx (rur|\\$)");
        Matcher m = p.matcher("xx $ equals to xx rur.");
        StringBuffer sb = new StringBuffer();
        for (int i = 1; m.find(); i *= 30) {
            String rep = (new Integer(i).toString()) + " $1";
            m.appendReplacement(sb, rep);
        }
        m.appendTail(sb);
        Assert.assertEquals("1 $ equals to 30 rur.", sb.toString());
    }

    @Test
    public void testReplaceAll() {
        String input = "aabfooaabfooabfoob";
        String pattern = "a*b";
        Pattern pat = Pattern.compile(pattern);
        Matcher mat = pat.matcher(input);
        Assert.assertEquals("-foo-foo-foo-", mat.replaceAll("-"));
    }

    @Test
    public void testResetCharSequence() {
        Pattern p = Pattern.compile("abcd");
        Matcher m = p.matcher("abcd");
        Assert.assertTrue(m.matches());
        m.reset("efgh");
        Assert.assertFalse(m.matches());
        try {
            m.reset(null);
            Assert.fail("expected a NPE");
        } catch (NullPointerException e) {
            // ok
        }
    }

    @Test
    public void testAppendSlashes() {
        Pattern p = Pattern.compile("\\\\");
        Matcher m = p.matcher("one\\cat\\two\\cats\\in\\the\\yard");
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "\\\\");
        } 
        m.appendTail(sb);
        Assert.assertEquals("one\\cat\\two\\cats\\in\\the\\yard", sb.toString());
    }

    @Test
    public void testReplaceFirst() {
        String input = "zzzdogzzzdogzzz";
        String pattern = "dog";
        Pattern pat = Pattern.compile(pattern);
        Matcher mat = pat.matcher(input);
        Assert.assertEquals("zzzcatzzzdogzzz", mat.replaceFirst("cat"));
    }

    @Test
    public void testPattern() {
        for (String element : testPatterns) {
            Pattern test = Pattern.compile(element);
            Assert.assertEquals(test, test.matcher("aaa").pattern());
        }
        for (String element : testPatterns) {
            Assert.assertEquals(element, Pattern.compile(element).matcher("aaa").pattern().toString());
        }
    }

    @Test
    public void testGroupint() {
        String positiveTestString = "ababababbaaabb";
        // test IndexOutOfBoundsException
        // //
        for (int i = 0; i < (groupPatterns.length); i++) {
            Pattern test = Pattern.compile(groupPatterns[i]);
            Matcher mat = test.matcher(positiveTestString);
            mat.matches();
            try {
                // groupPattern <index + 1> equals to number of groups
                // of the specified pattern
                // //
                mat.group((i + 2));
                Assert.fail("IndexOutBoundsException expected");
                mat.group((i + 100));
                Assert.fail("IndexOutBoundsException expected");
                mat.group((-1));
                Assert.fail("IndexOutBoundsException expected");
                mat.group((-100));
                Assert.fail("IndexOutBoundsException expected");
            } catch (IndexOutOfBoundsException iobe) {
                // ok
            }
        }
        String[][] groupResults = new String[][]{ new String[]{ "a" }, new String[]{ "a", "a" }, new String[]{ "ababababba", "a", "abb" }, new String[]{ "ababababba", "a", "a", "b" }, new String[]{ "ababababba", "a", "a", "b", "b" }, new String[]{ "ababababba", "a", "a", "b", "abb", "b" } };
        for (int i = 0; i < (groupPatterns.length); i++) {
            Pattern test = Pattern.compile(groupPatterns[i]);
            Matcher mat = test.matcher(positiveTestString);
            mat.matches();
            for (int j = 0; j < (groupResults[i].length); j++) {
                Assert.assertEquals(((("i: " + i) + " j: ") + j), groupResults[i][j], mat.group((j + 1)));
            }
        }
    }

    @Test
    public void testGroup() {
        String positiveTestString = "ababababbaaabb";
        String negativeTestString = "gjhfgdsjfhgcbv";
        for (String element : groupPatterns) {
            Pattern test = Pattern.compile(element);
            Matcher mat = test.matcher(positiveTestString);
            mat.matches();
            // test result
            Assert.assertEquals(positiveTestString, mat.group());
            // test equal to group(0) result
            Assert.assertEquals(mat.group(0), mat.group());
        }
        for (String element : groupPatterns) {
            Pattern test = Pattern.compile(element);
            Matcher mat = test.matcher(negativeTestString);
            mat.matches();
            try {
                mat.group();
                Assert.fail("IllegalStateException expected for <false> matches result");
            } catch (IllegalStateException ise) {
                // ok
            }
        }
    }

    @Test
    public void testGroupPossessive() {
        Pattern pat = Pattern.compile("((a)|(b))++c");
        Matcher mat = pat.matcher("aac");
        mat.matches();
        Assert.assertEquals("a", mat.group(1));
    }

    @Test
    public void testMatchesMisc() {
        String[][] posSeq = new String[][]{ new String[]{ "abb", "ababb", "abababbababb", "abababbababbabababbbbbabb" }, new String[]{ "213567", "12324567", "1234567", "213213567", "21312312312567", "444444567" }, new String[]{ "abcdaab", "aab", "abaab", "cdaab", "acbdadcbaab" }, new String[]{ "213234567", "3458", "0987654", "7689546432", "0398576", "98432", "5" }, new String[]{ "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" }, new String[]{ "ababbaAabababblice", "ababbaAliceababab", "ababbAabliceaaa", "abbbAbbbliceaaa", "Alice" }, new String[]{ "a123", "bnxnvgds156", "for", "while", "if", "struct" } };
        for (int i = 0; i < (testPatterns.length); i++) {
            Pattern pat = Pattern.compile(testPatterns[i]);
            for (int j = 0; j < (posSeq[i].length); j++) {
                Matcher mat = pat.matcher(posSeq[i][j]);
                Assert.assertTrue(((("Incorrect match: " + (testPatterns[i])) + " vs ") + (posSeq[i][j])), mat.matches());
            }
        }
    }

    @Test
    public void testMatchesQuantifiers() {
        String[] testPatternsSingles = new String[]{ "a{5}", "a{2,4}", "a{3,}" };
        String[] testPatternsMultiple = new String[]{ "((a)|(b)){1,2}abb", "((a)|(b)){2,4}", "((a)|(b)){3,}" };
        String[][] stringSingles = new String[][]{ new String[]{ "aaaaa", "aaa" }, new String[]{ "aa", "a", "aaa", "aaaaaa", "aaaa", "aaaaa" }, new String[]{ "aaa", "a", "aaaa", "aa" } };
        String[][] stringMultiples = new String[][]{ new String[]{ "ababb", "aba" }, new String[]{ "ab", "b", "bab", "ababa", "abba", "abababbb" }, new String[]{ "aba", "b", "abaa", "ba" } };
        for (int i = 0; i < (testPatternsSingles.length); i++) {
            Pattern pat = Pattern.compile(testPatternsSingles[i]);
            for (int j = 0; j < ((stringSingles.length) / 2); j++) {
                Assert.assertTrue(((("Match expected, but failed: " + (pat.pattern())) + " : ") + (stringSingles[i][j])), pat.matcher(stringSingles[i][(j * 2)]).matches());
                Assert.assertFalse(((("Match failure expected, but match succeed: " + (pat.pattern())) + " : ") + (stringSingles[i][((j * 2) + 1)])), pat.matcher(stringSingles[i][((j * 2) + 1)]).matches());
            }
        }
        for (int i = 0; i < (testPatternsMultiple.length); i++) {
            Pattern pat = Pattern.compile(testPatternsMultiple[i]);
            for (int j = 0; j < ((stringMultiples.length) / 2); j++) {
                Assert.assertTrue(((("Match expected, but failed: " + (pat.pattern())) + " : ") + (stringMultiples[i][j])), pat.matcher(stringMultiples[i][(j * 2)]).matches());
                Assert.assertFalse(((("Match failure expected, but match succeed: " + (pat.pattern())) + " : ") + (stringMultiples[i][((j * 2) + 1)])), pat.matcher(stringMultiples[i][((j * 2) + 1)]).matches());
            }
        }
    }

    @Test
    public void testQuantVsGroup() {
        String patternString = "(d{1,3})((a|c)*)(d{1,3})((a|c)*)(d{1,3})";
        String testString = "dacaacaacaaddaaacaacaaddd";
        Pattern pat = Pattern.compile(patternString);
        Matcher mat = pat.matcher(testString);
        mat.matches();
        Assert.assertEquals("dacaacaacaaddaaacaacaaddd", mat.group());
        Assert.assertEquals("d", mat.group(1));
        Assert.assertEquals("acaacaacaa", mat.group(2));
        Assert.assertEquals("dd", mat.group(4));
        Assert.assertEquals("aaacaacaa", mat.group(5));
        Assert.assertEquals("ddd", mat.group(7));
    }

    @Test
    public void testSEOLsymbols() {
        Pattern pat = Pattern.compile("^a\\(bb\\[$");
        Matcher mat = pat.matcher("a(bb[");
        Assert.assertTrue(mat.matches());
    }

    @Test
    public void testGroupCount() {
        for (int i = 0; i < (groupPatterns.length); i++) {
            Pattern test = Pattern.compile(groupPatterns[i]);
            Matcher mat = test.matcher("ababababbaaabb");
            mat.matches();
            Assert.assertEquals((i + 1), mat.groupCount());
        }
    }

    @Test
    public void testRelactantQuantifiers() {
        Pattern pat = Pattern.compile("(ab*)*b");
        Matcher mat = pat.matcher("abbbb");
        if (mat.matches()) {
            Assert.assertEquals("abbb", mat.group(1));
        } else {
            Assert.fail("Match expected: (ab*)*b vs abbbb");
        }
    }

    @Test
    public void testEnhancedFind() {
        String input = "foob";
        String pattern = "a*b";
        Pattern pat = Pattern.compile(pattern);
        Matcher mat = pat.matcher(input);
        mat.find();
        Assert.assertEquals("b", mat.group());
    }

    @Test
    public void testPosCompositeGroup() {
        String[] posExamples = new String[]{ "aabbcc", "aacc", "bbaabbcc" };
        String[] negExamples = new String[]{ "aabb", "bb", "bbaabb" };
        Pattern posPat = Pattern.compile("(aa|bb){1,3}+cc");
        Pattern negPat = Pattern.compile("(aa|bb){1,3}+bb");
        Matcher mat;
        for (String element : posExamples) {
            mat = posPat.matcher(element);
            Assert.assertTrue(mat.matches());
        }
        for (String element : negExamples) {
            mat = negPat.matcher(element);
            Assert.assertFalse(mat.matches());
        }
        Assert.assertTrue(Pattern.matches("(aa|bb){1,3}+bb", "aabbaabb"));
    }

    @Test
    public void testPosAltGroup() {
        String[] posExamples = new String[]{ "aacc", "bbcc", "cc" };
        String[] negExamples = new String[]{ "bb", "aa" };
        Pattern posPat = Pattern.compile("(aa|bb)?+cc");
        Pattern negPat = Pattern.compile("(aa|bb)?+bb");
        Matcher mat;
        for (String element : posExamples) {
            mat = posPat.matcher(element);
            Assert.assertTrue((((posPat.toString()) + " vs: ") + element), mat.matches());
        }
        for (String element : negExamples) {
            mat = negPat.matcher(element);
            Assert.assertFalse(mat.matches());
        }
        Assert.assertTrue(Pattern.matches("(aa|bb)?+bb", "aabb"));
    }

    @Test
    public void testRelCompGroup() {
        Matcher mat;
        Pattern pat;
        String res = "";
        for (int i = 0; i < 4; i++) {
            pat = Pattern.compile((("((aa|bb){" + i) + ",3}?).*cc"));
            mat = pat.matcher("aaaaaacc");
            Assert.assertTrue((((pat.toString()) + " vs: ") + "aaaaaacc"), mat.matches());
            Assert.assertEquals(res, mat.group(1));
            res += "aa";
        }
    }

    @Test
    public void testRelAltGroup() {
        Matcher mat;
        Pattern pat;
        pat = Pattern.compile("((aa|bb)??).*cc");
        mat = pat.matcher("aacc");
        Assert.assertTrue((((pat.toString()) + " vs: ") + "aacc"), mat.matches());
        Assert.assertEquals("", mat.group(1));
        pat = Pattern.compile("((aa|bb)??)cc");
        mat = pat.matcher("aacc");
        Assert.assertTrue((((pat.toString()) + " vs: ") + "aacc"), mat.matches());
        Assert.assertEquals("aa", mat.group(1));
    }

    @Test
    public void testIgnoreCase() {
        Pattern pat = Pattern.compile("(aa|bb)*", Pattern.CASE_INSENSITIVE);
        Matcher mat = pat.matcher("aAbb");
        Assert.assertTrue(mat.matches());
        pat = Pattern.compile("(a|b|c|d|e)*", Pattern.CASE_INSENSITIVE);
        mat = pat.matcher("aAebbAEaEdebbedEccEdebbedEaedaebEbdCCdbBDcdcdADa");
        Assert.assertTrue(mat.matches());
        pat = Pattern.compile("[a-e]*", Pattern.CASE_INSENSITIVE);
        mat = pat.matcher("aAebbAEaEdebbedEccEdebbedEaedaebEbdCCdbBDcdcdADa");
        Assert.assertTrue(mat.matches());
    }

    @Test
    public void testQuoteReplacement() {
        Assert.assertEquals("\\\\aaCC\\$1", Matcher.quoteReplacement("\\aaCC$1"));
    }

    @Test
    public void testOverFlow() {
        Pattern tp = Pattern.compile("(a*)*");
        Matcher tm = tp.matcher("aaa");
        Assert.assertTrue(tm.matches());
        Assert.assertEquals("", tm.group(1));
        Assert.assertTrue(Pattern.matches("(1+)\\1+", "11"));
        Assert.assertTrue(Pattern.matches("(1+)(2*)\\2+", "11"));
        Pattern pat = Pattern.compile("(1+)\\1*");
        Matcher mat = pat.matcher("11");
        Assert.assertTrue(mat.matches());
        Assert.assertEquals("11", mat.group(1));
        pat = Pattern.compile("((1+)|(2+))(\\2+)");
        mat = pat.matcher("11");
        Assert.assertTrue(mat.matches());
        Assert.assertEquals("1", mat.group(2));
        Assert.assertEquals("1", mat.group(1));
        Assert.assertEquals("1", mat.group(4));
        Assert.assertNull(mat.group(3));
    }

    @Test
    public void testUnicode() {
        Assert.assertTrue(Pattern.matches("\\x61a", "aa"));
        Assert.assertTrue(Pattern.matches("\\u0061a", "aa"));
        Assert.assertTrue(Pattern.matches("\\0141a", "aa"));
        Assert.assertTrue(Pattern.matches("\\0777", "?7"));
    }

    @Test
    public void testUnicodeCategory() {
        Assert.assertTrue(Pattern.matches("\\p{Ll}", "k"));// Unicode lower case

        Assert.assertTrue(Pattern.matches("\\P{Ll}", "K"));// Unicode non-lower

        // case
        Assert.assertTrue(Pattern.matches("\\p{Lu}", "K"));// Unicode upper case

        Assert.assertTrue(Pattern.matches("\\P{Lu}", "k"));// Unicode non-upper

        // case
        // combinations
        Assert.assertTrue(Pattern.matches("[\\p{L}&&[^\\p{Lu}]]", "k"));
        Assert.assertTrue(Pattern.matches("[\\p{L}&&[^\\p{Ll}]]", "K"));
        Assert.assertFalse(Pattern.matches("[\\p{L}&&[^\\p{Lu}]]", "K"));
        Assert.assertFalse(Pattern.matches("[\\p{L}&&[^\\p{Ll}]]", "k"));
        // category/character combinations
        Assert.assertFalse(Pattern.matches("[\\p{L}&&[^a-z]]", "k"));
        Assert.assertTrue(Pattern.matches("[\\p{L}&&[^a-z]]", "K"));
        Assert.assertTrue(Pattern.matches("[\\p{Lu}a-z]", "k"));
        Assert.assertTrue(Pattern.matches("[a-z\\p{Lu}]", "k"));
        Assert.assertFalse(Pattern.matches("[\\p{Lu}a-d]", "k"));
        Assert.assertTrue(Pattern.matches("[a-d\\p{Lu}]", "K"));
        // assertTrue(Pattern.matches("[\\p{L}&&[^\\p{Lu}&&[^K]]]", "K"));
        Assert.assertFalse(Pattern.matches("[\\p{L}&&[^\\p{Lu}&&[^G]]]", "K"));
    }

    @Test
    public void testSplitEmpty() {
        Pattern pat = Pattern.compile("");
        String[] s = pat.split("", (-1));
        Assert.assertEquals(1, s.length);
        Assert.assertEquals("", s[0]);
    }

    @Test
    public void testFindDollar() {
        Matcher mat = Pattern.compile("a$").matcher("a\n");
        Assert.assertTrue(mat.find());
        Assert.assertEquals("a", mat.group());
    }

    @Test
    public void testMatchesRegionChanged() {
        // Regression for HARMONY-610
        String input = " word ";
        Pattern pattern = Pattern.compile("\\w+");
        Matcher matcher = pattern.matcher(input);
        matcher.region(1, 5);
        Assert.assertTrue(matcher.matches());
    }

    @Test
    public void testFindRegionChanged() {
        // Regression for HARMONY-625
        Pattern pattern = Pattern.compile("(?s).*");
        Matcher matcher = pattern.matcher("abcde");
        matcher.find();
        Assert.assertEquals("abcde", matcher.group());
        matcher = pattern.matcher("abcde");
        matcher.region(0, 2);
        matcher.find();
        Assert.assertEquals("ab", matcher.group());
    }

    @Test
    public void testFindRegionChanged2() {
        // Regression for HARMONY-713
        Pattern pattern = Pattern.compile("c");
        String inputStr = "aabb.c";
        Matcher matcher = pattern.matcher(inputStr);
        matcher.region(0, 3);
        Assert.assertFalse(matcher.find());
    }

    @Test
    public void testPatternMatcher() throws Exception {
        Pattern pattern = Pattern.compile("(?:\\d+)(?:pt)");
        Assert.assertTrue(pattern.matcher("14pt").matches());
    }

    @Test
    public void test3360() {
        String str = "!\"#%&\'(),-./";
        Pattern p = Pattern.compile("\\s");
        Matcher m = p.matcher(str);
        Assert.assertFalse(m.find());
    }

    @Test
    public void testGeneralPunctuationCategory() {
        String[] s = new String[]{ ",", "!", "\"", "#", "%", "&", "'", "(", ")", "-", ".", "/" };
        String regexp = "\\p{P}";
        for (int i = 0; i < (s.length); i++) {
            Pattern pattern = Pattern.compile(regexp);
            Matcher matcher = pattern.matcher(s[i]);
            Assert.assertTrue(matcher.find());
        }
    }

    @Test
    public void testHitEndAfterFind() {
        hitEndTest(true, "#01.0", "r((ege)|(geg))x", "regexx", false);
        hitEndTest(true, "#01.1", "r((ege)|(geg))x", "regex", false);
        hitEndTest(true, "#01.2", "r((ege)|(geg))x", "rege", true);
        hitEndTest(true, "#01.2", "r((ege)|(geg))x", "xregexx", false);
        hitEndTest(true, "#02.0", "regex", "rexreger", true);
        hitEndTest(true, "#02.1", "regex", "raxregexr", false);
        String floatRegex = getHexFloatRegex();
        hitEndTest(true, "#03.0", floatRegex, Double.toHexString((-1.234)), true);
        hitEndTest(true, "#03.1", floatRegex, (("1 ABC" + (Double.toHexString(Double.NaN))) + "buhuhu"), false);
        hitEndTest(true, "#03.2", floatRegex, ((Double.toHexString((-0.0))) + "--"), false);
        hitEndTest(true, "#03.3", floatRegex, (("--" + (Double.toHexString(Double.MIN_VALUE))) + "--"), false);
        hitEndTest(true, "#04.0", "(\\d+) fish (\\d+) fish (\\w+) fish (\\d+)", "1 fish 2 fish red fish 5", true);
        hitEndTest(true, "#04.1", "(\\d+) fish (\\d+) fish (\\w+) fish (\\d+)", "----1 fish 2 fish red fish 5----", false);
    }

    @Test
    public void testToString() {
        String result = Pattern.compile("(\\d{1,3})").matcher("aaaa123456789045").toString();
        Assert.assertTrue("The result doesn't contain pattern info", result.contains("(\\d{1,3})"));
    }
}

