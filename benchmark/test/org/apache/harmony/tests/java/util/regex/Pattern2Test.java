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
package org.apache.harmony.tests.java.util.regex;


import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import junit.framework.TestCase;


/**
 * Tests simple Pattern compilation and Matcher methods
 */
@SuppressWarnings("nls")
public class Pattern2Test extends TestCase {
    public void testSimpleMatch() throws PatternSyntaxException {
        Pattern p = Pattern.compile("foo.*");
        Matcher m1 = p.matcher("foo123");
        TestCase.assertTrue(m1.matches());
        TestCase.assertTrue(m1.find(0));
        TestCase.assertTrue(m1.lookingAt());
        Matcher m2 = p.matcher("fox");
        TestCase.assertFalse(m2.matches());
        TestCase.assertFalse(m2.find(0));
        TestCase.assertFalse(m2.lookingAt());
        TestCase.assertTrue(Pattern.matches("foo.*", "foo123"));
        TestCase.assertFalse(Pattern.matches("foo.*", "fox"));
        TestCase.assertFalse(Pattern.matches("bar", "foobar"));
        TestCase.assertTrue(Pattern.matches("", ""));
    }

    public void testCursors() {
        Pattern p;
        Matcher m;
        try {
            p = Pattern.compile("foo");
            m = p.matcher("foobar");
            TestCase.assertTrue(m.find());
            TestCase.assertEquals(0, m.start());
            TestCase.assertEquals(3, m.end());
            TestCase.assertFalse(m.find());
            // Note: also testing reset here
            m.reset();
            TestCase.assertTrue(m.find());
            TestCase.assertEquals(0, m.start());
            TestCase.assertEquals(3, m.end());
            TestCase.assertFalse(m.find());
            m.reset("barfoobar");
            TestCase.assertTrue(m.find());
            TestCase.assertEquals(3, m.start());
            TestCase.assertEquals(6, m.end());
            TestCase.assertFalse(m.find());
            m.reset("barfoo");
            TestCase.assertTrue(m.find());
            TestCase.assertEquals(3, m.start());
            TestCase.assertEquals(6, m.end());
            TestCase.assertFalse(m.find());
            m.reset("foobarfoobarfoo");
            TestCase.assertTrue(m.find());
            TestCase.assertEquals(0, m.start());
            TestCase.assertEquals(3, m.end());
            TestCase.assertTrue(m.find());
            TestCase.assertEquals(6, m.start());
            TestCase.assertEquals(9, m.end());
            TestCase.assertTrue(m.find());
            TestCase.assertEquals(12, m.start());
            TestCase.assertEquals(15, m.end());
            TestCase.assertFalse(m.find());
            TestCase.assertTrue(m.find(0));
            TestCase.assertEquals(0, m.start());
            TestCase.assertEquals(3, m.end());
            TestCase.assertTrue(m.find(4));
            TestCase.assertEquals(6, m.start());
            TestCase.assertEquals(9, m.end());
        } catch (PatternSyntaxException e) {
            System.out.println(e.getMessage());
            TestCase.fail();
        }
    }

    public void testGroups() throws PatternSyntaxException {
        Pattern p;
        Matcher m;
        p = Pattern.compile("(p[0-9]*)#?(q[0-9]*)");
        m = p.matcher("p1#q3p2q42p5p71p63#q888");
        TestCase.assertTrue(m.find());
        TestCase.assertEquals(0, m.start());
        TestCase.assertEquals(5, m.end());
        TestCase.assertEquals(2, m.groupCount());
        TestCase.assertEquals(0, m.start(0));
        TestCase.assertEquals(5, m.end(0));
        TestCase.assertEquals(0, m.start(1));
        TestCase.assertEquals(2, m.end(1));
        TestCase.assertEquals(3, m.start(2));
        TestCase.assertEquals(5, m.end(2));
        TestCase.assertEquals("p1#q3", m.group());
        TestCase.assertEquals("p1#q3", m.group(0));
        TestCase.assertEquals("p1", m.group(1));
        TestCase.assertEquals("q3", m.group(2));
        TestCase.assertTrue(m.find());
        TestCase.assertEquals(5, m.start());
        TestCase.assertEquals(10, m.end());
        TestCase.assertEquals(2, m.groupCount());
        TestCase.assertEquals(10, m.end(0));
        TestCase.assertEquals(5, m.start(1));
        TestCase.assertEquals(7, m.end(1));
        TestCase.assertEquals(7, m.start(2));
        TestCase.assertEquals(10, m.end(2));
        TestCase.assertEquals("p2q42", m.group());
        TestCase.assertEquals("p2q42", m.group(0));
        TestCase.assertEquals("p2", m.group(1));
        TestCase.assertEquals("q42", m.group(2));
        TestCase.assertTrue(m.find());
        TestCase.assertEquals(15, m.start());
        TestCase.assertEquals(23, m.end());
        TestCase.assertEquals(2, m.groupCount());
        TestCase.assertEquals(15, m.start(0));
        TestCase.assertEquals(23, m.end(0));
        TestCase.assertEquals(15, m.start(1));
        TestCase.assertEquals(18, m.end(1));
        TestCase.assertEquals(19, m.start(2));
        TestCase.assertEquals(23, m.end(2));
        TestCase.assertEquals("p63#q888", m.group());
        TestCase.assertEquals("p63#q888", m.group(0));
        TestCase.assertEquals("p63", m.group(1));
        TestCase.assertEquals("q888", m.group(2));
        TestCase.assertFalse(m.find());
    }

    public void testReplace() throws PatternSyntaxException {
        Pattern p;
        Matcher m;
        // Note: examples from book,
        // Hitchens, Ron, 2002, "Java NIO", O'Reilly, page 171
        p = Pattern.compile("a*b");
        m = p.matcher("aabfooaabfooabfoob");
        TestCase.assertTrue(m.replaceAll("-").equals("-foo-foo-foo-"));
        TestCase.assertTrue(m.replaceFirst("-").equals("-fooaabfooabfoob"));
        /* p = Pattern.compile ("\\p{Blank}");

        m = p.matcher ("fee fie foe fum"); assertTrue
        (m.replaceFirst("-").equals ("fee-fie foe fum")); assertTrue
        (m.replaceAll("-").equals ("fee-fie-foe-fum"));
         */
        p = Pattern.compile("([bB])yte");
        m = p.matcher("Byte for byte");
        TestCase.assertTrue(m.replaceFirst("$1ite").equals("Bite for byte"));
        TestCase.assertTrue(m.replaceAll("$1ite").equals("Bite for bite"));
        p = Pattern.compile("\\d\\d\\d\\d([- ])");
        m = p.matcher("card #1234-5678-1234");
        TestCase.assertTrue(m.replaceFirst("xxxx$1").equals("card #xxxx-5678-1234"));
        TestCase.assertTrue(m.replaceAll("xxxx$1").equals("card #xxxx-xxxx-1234"));
        p = Pattern.compile("(up|left)( *)(right|down)");
        m = p.matcher("left right, up down");
        TestCase.assertTrue(m.replaceFirst("$3$2$1").equals("right left, up down"));
        TestCase.assertTrue(m.replaceAll("$3$2$1").equals("right left, down up"));
        p = Pattern.compile("([CcPp][hl]e[ea]se)");
        m = p.matcher("I want cheese. Please.");
        TestCase.assertTrue(m.replaceFirst("<b> $1 </b>").equals("I want <b> cheese </b>. Please."));
        TestCase.assertTrue(m.replaceAll("<b> $1 </b>").equals("I want <b> cheese </b>. <b> Please </b>."));
    }

    public void testEscapes() throws PatternSyntaxException {
        Pattern p;
        Matcher m;
        // Test \\ sequence
        p = Pattern.compile("([a-z]+)\\\\([a-z]+);");
        m = p.matcher("fred\\ginger;abbott\\costello;jekell\\hyde;");
        TestCase.assertTrue(m.find());
        TestCase.assertEquals("fred", m.group(1));
        TestCase.assertEquals("ginger", m.group(2));
        TestCase.assertTrue(m.find());
        TestCase.assertEquals("abbott", m.group(1));
        TestCase.assertEquals("costello", m.group(2));
        TestCase.assertTrue(m.find());
        TestCase.assertEquals("jekell", m.group(1));
        TestCase.assertEquals("hyde", m.group(2));
        TestCase.assertFalse(m.find());
        // Test \n, \t, \r, \f, \e, \a sequences
        p = Pattern.compile("([a-z]+)[\\n\\t\\r\\f\\e\\a]+([a-z]+)");
        m = p.matcher("aa\nbb;cc\t\rdd;ee\f\u001bff;gg\n\u0007hh");
        TestCase.assertTrue(m.find());
        TestCase.assertEquals("aa", m.group(1));
        TestCase.assertEquals("bb", m.group(2));
        TestCase.assertTrue(m.find());
        TestCase.assertEquals("cc", m.group(1));
        TestCase.assertEquals("dd", m.group(2));
        TestCase.assertTrue(m.find());
        TestCase.assertEquals("ee", m.group(1));
        TestCase.assertEquals("ff", m.group(2));
        TestCase.assertTrue(m.find());
        TestCase.assertEquals("gg", m.group(1));
        TestCase.assertEquals("hh", m.group(2));
        TestCase.assertFalse(m.find());
        // Test \\u and \\x sequences
        p = Pattern.compile("([0-9]+)[\\u0020:\\x21];");
        m = p.matcher("11:;22 ;33-;44!;");
        TestCase.assertTrue(m.find());
        TestCase.assertEquals("11", m.group(1));
        TestCase.assertTrue(m.find());
        TestCase.assertEquals("22", m.group(1));
        TestCase.assertTrue(m.find());
        TestCase.assertEquals("44", m.group(1));
        TestCase.assertFalse(m.find());
        // Test invalid unicode sequences
        try {
            p = Pattern.compile("\\u");
            TestCase.fail("PatternSyntaxException expected");
        } catch (PatternSyntaxException e) {
        }
        try {
            p = Pattern.compile("\\u;");
            TestCase.fail("PatternSyntaxException expected");
        } catch (PatternSyntaxException e) {
        }
        try {
            p = Pattern.compile("\\u002");
            TestCase.fail("PatternSyntaxException expected");
        } catch (PatternSyntaxException e) {
        }
        try {
            p = Pattern.compile("\\u002;");
            TestCase.fail("PatternSyntaxException expected");
        } catch (PatternSyntaxException e) {
        }
        // Test invalid hex sequences
        try {
            p = Pattern.compile("\\x");
            TestCase.fail("PatternSyntaxException expected");
        } catch (PatternSyntaxException e) {
        }
        try {
            p = Pattern.compile("\\x;");
            TestCase.fail("PatternSyntaxException expected");
        } catch (PatternSyntaxException e) {
        }
        // icu4c allows 1 to 6 hex digits in \x escapes.
        p = Pattern.compile("\\xa");
        p = Pattern.compile("\\xab");
        p = Pattern.compile("\\xabc");
        p = Pattern.compile("\\xabcd");
        p = Pattern.compile("\\xabcde");
        p = Pattern.compile("\\xabcdef");
        // (Further digits would just be treated as characters after the escape.)
        try {
            p = Pattern.compile("\\xg");
            TestCase.fail();
        } catch (PatternSyntaxException expected) {
        }
        // Test \0 (octal) sequences (1, 2 and 3 digit)
        p = Pattern.compile("([0-9]+)[\\07\\040\\0160];");
        m = p.matcher("11\u0007;22:;33 ;44p;");
        TestCase.assertTrue(m.find());
        TestCase.assertEquals("11", m.group(1));
        TestCase.assertTrue(m.find());
        TestCase.assertEquals("33", m.group(1));
        TestCase.assertTrue(m.find());
        TestCase.assertEquals("44", m.group(1));
        TestCase.assertFalse(m.find());
        // Test invalid octal sequences
        try {
            p = Pattern.compile("\\08");
            TestCase.fail("PatternSyntaxException expected");
        } catch (PatternSyntaxException e) {
        }
        // originally contributed test did not check the result
        // TODO: check what RI does here
        // try {
        // p = Pattern.compile("\\0477");
        // fail("PatternSyntaxException expected");
        // } catch (PatternSyntaxException e) {
        // }
        try {
            p = Pattern.compile("\\0");
            TestCase.fail("PatternSyntaxException expected");
        } catch (PatternSyntaxException e) {
        }
        try {
            p = Pattern.compile("\\0;");
            TestCase.fail("PatternSyntaxException expected");
        } catch (PatternSyntaxException e) {
        }
        // Test \c (control character) sequence
        p = Pattern.compile("([0-9]+)[\\cA\\cB\\cC\\cD];");
        m = p.matcher("11\u0001;22:;33\u0002;44p;55\u0003;66\u0004;");
        TestCase.assertTrue(m.find());
        TestCase.assertEquals("11", m.group(1));
        TestCase.assertTrue(m.find());
        TestCase.assertEquals("33", m.group(1));
        TestCase.assertTrue(m.find());
        TestCase.assertEquals("55", m.group(1));
        TestCase.assertTrue(m.find());
        TestCase.assertEquals("66", m.group(1));
        TestCase.assertFalse(m.find());
        // More thorough control escape test
        // Ensure that each escape matches exactly the corresponding
        // character
        // code and no others (well, from 0-255 at least)
        int i;
        int j;
        for (i = 0; i < 26; i++) {
            p = Pattern.compile(("\\c" + (Character.toString(((char) ('A' + i))))));
            int match_char = -1;
            for (j = 0; j < 255; j++) {
                m = p.matcher(Character.toString(((char) (j))));
                if (m.matches()) {
                    TestCase.assertEquals((-1), match_char);
                    match_char = j;
                }
            }
            TestCase.assertTrue((match_char == (i + 1)));
        }
        // Test invalid control escapes
        // icu4c 50 accepts this pattern, and treats it as a literal.
        // try {
        p = Pattern.compile("\\c");
        TestCase.assertTrue(p.matcher("x\\cy").find());
        // fail(p.matcher("").toString());
        // } catch (PatternSyntaxException e) {
        // }
        // But \cH works.
        p = Pattern.compile("\\cH");
        TestCase.assertTrue(p.matcher("x\by").find());
        TestCase.assertFalse(p.matcher("x\\cHy").find());
        // originally contributed test did not check the result
        // TODO: check what RI does here
        // try {
        // p = Pattern.compile("\\c;");
        // fail("PatternSyntaxException expected");
        // } catch (PatternSyntaxException e) {
        // }
        // 
        // try {
        // p = Pattern.compile("\\ca;");
        // fail("PatternSyntaxException expected");
        // } catch (PatternSyntaxException e) {
        // }
        // 
        // try {
        // p = Pattern.compile("\\c4;");
        // fail("PatternSyntaxException expected");
        // } catch (PatternSyntaxException e) {
        // }
    }

    public void testCharacterClasses() throws PatternSyntaxException {
        Pattern p;
        Matcher m;
        // Test one character range
        p = Pattern.compile("[p].*[l]");
        m = p.matcher("paul");
        TestCase.assertTrue(m.matches());
        m = p.matcher("pool");
        TestCase.assertTrue(m.matches());
        m = p.matcher("pong");
        TestCase.assertFalse(m.matches());
        m = p.matcher("pl");
        TestCase.assertTrue(m.matches());
        // Test two character range
        p = Pattern.compile("[pm].*[lp]");
        m = p.matcher("prop");
        TestCase.assertTrue(m.matches());
        m = p.matcher("mall");
        TestCase.assertTrue(m.matches());
        m = p.matcher("pong");
        TestCase.assertFalse(m.matches());
        m = p.matcher("pill");
        TestCase.assertTrue(m.matches());
        // Test range including [ and ]
        p = Pattern.compile("[<\\[].*[\\]>]");
        m = p.matcher("<foo>");
        TestCase.assertTrue(m.matches());
        m = p.matcher("[bar]");
        TestCase.assertTrue(m.matches());
        m = p.matcher("{foobar]");
        TestCase.assertFalse(m.matches());
        m = p.matcher("<pill]");
        TestCase.assertTrue(m.matches());
        // Test range using ^
        p = Pattern.compile("[^bc][a-z]+[tr]");
        m = p.matcher("pat");
        TestCase.assertTrue(m.matches());
        m = p.matcher("liar");
        TestCase.assertTrue(m.matches());
        m = p.matcher("car");
        TestCase.assertFalse(m.matches());
        m = p.matcher("gnat");
        TestCase.assertTrue(m.matches());
        // Test character range using -
        p = Pattern.compile("[a-z]_+[a-zA-Z]-+[0-9p-z]");
        m = p.matcher("d__F-8");
        TestCase.assertTrue(m.matches());
        m = p.matcher("c_a-q");
        TestCase.assertTrue(m.matches());
        m = p.matcher("a__R-a");
        TestCase.assertFalse(m.matches());
        m = p.matcher("r_____d-----5");
        TestCase.assertTrue(m.matches());
        // Test range using unicode characters and unicode and hex escapes
        p = Pattern.compile("[\\u1234-\\u2345]_+[a-z]-+[\u0001-\\x11]");
        m = p.matcher("\u2000_q-\u0007");
        TestCase.assertTrue(m.matches());
        m = p.matcher("\u1234_z-\u0001");
        TestCase.assertTrue(m.matches());
        m = p.matcher("r_p-q");
        TestCase.assertFalse(m.matches());
        m = p.matcher("\u2345_____d-----\n");
        TestCase.assertTrue(m.matches());
        // Test ranges including the "-" character
        // "---" collides with icu4c's "--" operator, and likely to be user error anyway.
        if (false) {
            p = Pattern.compile("[\\*-/]_+[---]!+[--AP]");
            m = p.matcher("-_-!!A");
            TestCase.assertTrue(m.matches());
            m = p.matcher("+_-!!!-");
            TestCase.assertTrue(m.matches());
            m = p.matcher("!_-!@");
            TestCase.assertFalse(m.matches());
            m = p.matcher(",______-!!!!!!!P");
            TestCase.assertTrue(m.matches());
        }
        // Test nested ranges
        p = Pattern.compile("[pm[t]][a-z]+[[r]lp]");
        m = p.matcher("prop");
        TestCase.assertTrue(m.matches());
        m = p.matcher("tsar");
        TestCase.assertTrue(m.matches());
        m = p.matcher("pong");
        TestCase.assertFalse(m.matches());
        m = p.matcher("moor");
        TestCase.assertTrue(m.matches());
        // Test character class intersection with &&
        // TODO: figure out what x&&y or any class with a null intersection
        // set (like [[a-c]&&[d-f]]) might mean. It doesn't mean "match
        // nothing" and doesn't mean "match anything" so I'm stumped.
        p = Pattern.compile("[[a-p]&&[g-z]]+-+[[a-z]&&q]-+[x&&[a-z]]-+");
        m = p.matcher("h--q--x--");
        TestCase.assertTrue(m.matches());
        m = p.matcher("hog--q-x-");
        TestCase.assertTrue(m.matches());
        m = p.matcher("ape--q-x-");
        TestCase.assertFalse(m.matches());
        m = p.matcher("mop--q-x----");
        TestCase.assertTrue(m.matches());
        // Test error cases with &&
        // This is an RI bug that icu4c doesn't have.
        if (false) {
            p = Pattern.compile("[&&[xyz]]");
            m = p.matcher("&");
            // System.out.println(m.matches());
            m = p.matcher("x");
            // System.out.println(m.matches());
            m = p.matcher("y");
            // System.out.println(m.matches());
        }
        p = Pattern.compile("[[xyz]&[axy]]");
        m = p.matcher("x");
        // System.out.println(m.matches());
        m = p.matcher("z");
        // System.out.println(m.matches());
        m = p.matcher("&");
        // System.out.println(m.matches());
        p = Pattern.compile("[abc[123]&&[345]def]");
        m = p.matcher("a");
        // System.out.println(m.matches());
        // icu4c rightly considers a missing rhs to && a syntax error.
        if (false) {
            p = Pattern.compile("[[xyz]&&]");
        }
        p = Pattern.compile("[[abc]&]");
        try {
            p = Pattern.compile("[[abc]&&");
            TestCase.fail("PatternSyntaxException expected");
        } catch (PatternSyntaxException e) {
        }
        p = Pattern.compile("[[abc]\\&&[xyz]]");
        p = Pattern.compile("[[abc]&\\&[xyz]]");
        // Test 3-way intersection
        p = Pattern.compile("[[a-p]&&[g-z]&&[d-k]]");
        m = p.matcher("g");
        TestCase.assertTrue(m.matches());
        m = p.matcher("m");
        TestCase.assertFalse(m.matches());
        // Test nested intersection
        p = Pattern.compile("[[[a-p]&&[g-z]]&&[d-k]]");
        m = p.matcher("g");
        TestCase.assertTrue(m.matches());
        m = p.matcher("m");
        TestCase.assertFalse(m.matches());
        // Test character class subtraction with && and ^
        p = Pattern.compile("[[a-z]&&[^aeiou]][aeiou][[^xyz]&&[a-z]]");
        m = p.matcher("pop");
        TestCase.assertTrue(m.matches());
        m = p.matcher("tag");
        TestCase.assertTrue(m.matches());
        m = p.matcher("eat");
        TestCase.assertFalse(m.matches());
        m = p.matcher("tax");
        TestCase.assertFalse(m.matches());
        m = p.matcher("zip");
        TestCase.assertTrue(m.matches());
        // Test . (DOT), with and without DOTALL
        // Note: DOT not allowed in character classes
        p = Pattern.compile(".+/x.z");
        m = p.matcher("!$/xyz");
        TestCase.assertTrue(m.matches());
        m = p.matcher("%\n\r/x\nz");
        TestCase.assertFalse(m.matches());
        p = Pattern.compile(".+/x.z", Pattern.DOTALL);
        m = p.matcher("%\n\r/x\nz");
        TestCase.assertTrue(m.matches());
        // Test \d (digit)
        p = Pattern.compile("\\d+[a-z][\\dx]");
        m = p.matcher("42a6");
        TestCase.assertTrue(m.matches());
        m = p.matcher("21zx");
        TestCase.assertTrue(m.matches());
        m = p.matcher("ab6");
        TestCase.assertFalse(m.matches());
        m = p.matcher("56912f9");
        TestCase.assertTrue(m.matches());
        // Test \D (not a digit)
        p = Pattern.compile("\\D+[a-z]-[\\D3]");
        m = p.matcher("za-p");
        TestCase.assertTrue(m.matches());
        m = p.matcher("%!e-3");
        TestCase.assertTrue(m.matches());
        m = p.matcher("9a-x");
        TestCase.assertFalse(m.matches());
        m = p.matcher("\u1234pp\ny-3");
        TestCase.assertTrue(m.matches());
        // Test \s (whitespace)
        p = Pattern.compile("<[a-zA-Z]+\\s+[0-9]+[\\sx][^\\s]>");
        m = p.matcher("<cat \t1\fx>");
        TestCase.assertTrue(m.matches());
        m = p.matcher("<cat \t1\f >");
        TestCase.assertFalse(m.matches());
        m = p.matcher("xyz <foo\n\r22 5> <pp \t\n\f\r \u000b41x\u1234><pp \nx7\rc> zzz");
        TestCase.assertTrue(m.find());
        TestCase.assertTrue(m.find());
        TestCase.assertFalse(m.find());
        // Test \S (not whitespace)
        p = Pattern.compile("<[a-z] \\S[0-9][\\S\n]+[^\\S]221>");
        m = p.matcher("<f $0**\n** 221>");
        TestCase.assertTrue(m.matches());
        m = p.matcher("<x 441\t221>");
        TestCase.assertTrue(m.matches());
        m = p.matcher("<z \t9\ng 221>");
        TestCase.assertFalse(m.matches());
        m = p.matcher("<z 60\ngg\u1234\f221>");
        TestCase.assertTrue(m.matches());
        p = Pattern.compile("<[a-z] \\S[0-9][\\S\n]+[^\\S]221[\\S&&[^abc]]>");
        m = p.matcher("<f $0**\n** 221x>");
        TestCase.assertTrue(m.matches());
        m = p.matcher("<x 441\t221z>");
        TestCase.assertTrue(m.matches());
        m = p.matcher("<x 441\t221 >");
        TestCase.assertFalse(m.matches());
        m = p.matcher("<x 441\t221c>");
        TestCase.assertFalse(m.matches());
        m = p.matcher("<z \t9\ng 221x>");
        TestCase.assertFalse(m.matches());
        m = p.matcher("<z 60\ngg\u1234\f221\u0001>");
        TestCase.assertTrue(m.matches());
        // Test \w (ascii word)
        p = Pattern.compile("<\\w+\\s[0-9]+;[^\\w]\\w+/[\\w$]+;");
        m = p.matcher("<f1 99;!foo5/a$7;");
        TestCase.assertTrue(m.matches());
        m = p.matcher("<f$ 99;!foo5/a$7;");
        TestCase.assertFalse(m.matches());
        m = p.matcher("<abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_0123456789 99;!foo5/a$7;");
        TestCase.assertTrue(m.matches());
        // Test \W (not an ascii word)
        p = Pattern.compile("<\\W\\w+\\s[0-9]+;[\\W_][^\\W]+\\s[0-9]+;");
        m = p.matcher("<$foo3\n99;_bar\t0;");
        TestCase.assertTrue(m.matches());
        m = p.matcher("<hh 99;_g 0;");
        TestCase.assertFalse(m.matches());
        m = p.matcher("<*xx\t00;^zz\f11;");
        TestCase.assertTrue(m.matches());
        // Test x|y pattern
        // TODO
    }

    public void testPOSIXGroups() throws PatternSyntaxException {
        Pattern p;
        Matcher m;
        // Test POSIX groups using \p and \P (in the group and not in the group)
        // Groups are Lower, Upper, ASCII, Alpha, Digit, XDigit, Alnum, Punct,
        // Graph, Print, Blank, Space, Cntrl
        // Test \p{Lower}
        /* FIXME: Requires complex range processing p = Pattern.compile("<\\p{Lower}\\d\\P{Lower}:[\\p{Lower}Z]\\s[^\\P{Lower}]>");
        m = p.matcher("<a4P:g x>"); assertTrue(m.matches()); m = p.matcher("<p4%:Z\tq>");
        assertTrue(m.matches()); m = p.matcher("<A6#:e e>");
        assertFalse(m.matches());
         */
        p = Pattern.compile("\\p{Lower}+");
        m = p.matcher("abcdefghijklmnopqrstuvwxyz");
        TestCase.assertTrue(m.matches());
        // Invalid uses of \p{Lower}
        try {
            p = Pattern.compile("\\p");
            TestCase.fail("PatternSyntaxException expected");
        } catch (PatternSyntaxException e) {
        }
        try {
            p = Pattern.compile("\\p;");
            TestCase.fail("PatternSyntaxException expected");
        } catch (PatternSyntaxException e) {
        }
        try {
            p = Pattern.compile("\\p{");
            TestCase.fail("PatternSyntaxException expected");
        } catch (PatternSyntaxException e) {
        }
        try {
            p = Pattern.compile("\\p{;");
            TestCase.fail("PatternSyntaxException expected");
        } catch (PatternSyntaxException e) {
        }
        try {
            p = Pattern.compile("\\p{Lower");
            TestCase.fail("PatternSyntaxException expected");
        } catch (PatternSyntaxException e) {
        }
        try {
            p = Pattern.compile("\\p{Lower;");
            TestCase.fail("PatternSyntaxException expected");
        } catch (PatternSyntaxException e) {
        }
        // Test \p{Upper}
        /* FIXME: Requires complex range processing p = Pattern.compile("<\\p{Upper}\\d\\P{Upper}:[\\p{Upper}z]\\s[^\\P{Upper}]>");
        m = p.matcher("<A4p:G X>"); assertTrue(m.matches()); m = p.matcher("<P4%:z\tQ>");
        assertTrue(m.matches()); m = p.matcher("<a6#:E E>");
        assertFalse(m.matches());
         */
        p = Pattern.compile("\\p{Upper}+");
        m = p.matcher("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        TestCase.assertTrue(m.matches());
        // Invalid uses of \p{Upper}
        try {
            p = Pattern.compile("\\p{Upper");
            TestCase.fail("PatternSyntaxException expected");
        } catch (PatternSyntaxException e) {
        }
        try {
            p = Pattern.compile("\\p{Upper;");
            TestCase.fail("PatternSyntaxException expected");
        } catch (PatternSyntaxException e) {
        }
        // Test \p{ASCII}
        /* FIXME: Requires complex range processing p = Pattern.compile("<\\p{ASCII}\\d\\P{ASCII}:[\\p{ASCII}\u1234]\\s[^\\P{ASCII}]>");
        m = p.matcher("<A4\u0080:G X>"); assertTrue(m.matches()); m =
        p.matcher("<P4\u00ff:\u1234\t\n>"); assertTrue(m.matches()); m =
        p.matcher("<\u00846#:E E>"); assertFalse(m.matches())
         */
        int i;
        p = Pattern.compile("\\p{ASCII}");
        for (i = 0; i < 128; i++) {
            m = p.matcher(Character.toString(((char) (i))));
            TestCase.assertTrue(m.matches());
        }
        for (; i < 255; i++) {
            m = p.matcher(Character.toString(((char) (i))));
            TestCase.assertFalse(m.matches());
        }
        // Invalid uses of \p{ASCII}
        try {
            p = Pattern.compile("\\p{ASCII");
            TestCase.fail("PatternSyntaxException expected");
        } catch (PatternSyntaxException e) {
        }
        try {
            p = Pattern.compile("\\p{ASCII;");
            TestCase.fail("PatternSyntaxException expected");
        } catch (PatternSyntaxException e) {
        }
        // Test \p{Alpha}
        // TODO
        // Test \p{Digit}
        // TODO
        // Test \p{XDigit}
        // TODO
        // Test \p{Alnum}
        // TODO
        // Test \p{Punct}
        // TODO
        // Test \p{Graph}
        // TODO
        // Test \p{Print}
        // TODO
        // Test \p{Blank}
        // TODO
        // Test \p{Space}
        // TODO
        // Test \p{Cntrl}
        // TODO
    }

    public void testUnicodeBlocks() throws PatternSyntaxException {
        Pattern p;
        Matcher m;
        int i;
        int j;
        // Test Unicode blocks using \p and \P
        // FIXME:
        // Note that LatinExtended-B and ArabicPresentations-B are unrecognized
        // by the reference JDK.
        for (i = 0; i < (Pattern2Test.UBlocks.length); i++) {
            /* p = Pattern.compile("\\p{"+UBlocks[i].name+"}");

            if (UBlocks[i].low > 0) { m =
            p.matcher(Character.toString((char)(UBlocks[i].low-1)));
            assertFalse(m.matches()); } for (j=UBlocks[i].low; j <=
            UBlocks[i].high; j++) { m =
            p.matcher(Character.toString((char)j)); assertTrue(m.matches()); }
            if (UBlocks[i].high < 0xFFFF) { m =
            p.matcher(Character.toString((char)(UBlocks[i].high+1)));
            assertFalse(m.matches()); }

            p = Pattern.compile("\\P{"+UBlocks[i].name+"}");

            if (UBlocks[i].low > 0) { m =
            p.matcher(Character.toString((char)(UBlocks[i].low-1)));
            assertTrue(m.matches()); } for (j=UBlocks[i].low; j <
            UBlocks[i].high; j++) { m =
            p.matcher(Character.toString((char)j)); assertFalse(m.matches()); }
            if (UBlocks[i].high < 0xFFFF) { m =
            p.matcher(Character.toString((char)(UBlocks[i].high+1)));
            assertTrue(m.matches()); }
             */
            p = Pattern.compile((("\\p{In" + (Pattern2Test.UBlocks[i].name)) + "}"));
            if ((Pattern2Test.UBlocks[i].low) > 0) {
                m = p.matcher(Character.toString(((char) ((Pattern2Test.UBlocks[i].low) - 1))));
                TestCase.assertFalse(Pattern2Test.UBlocks[i].name, m.matches());
            }
            for (j = Pattern2Test.UBlocks[i].low; j <= (Pattern2Test.UBlocks[i].high); j++) {
                m = p.matcher(Character.toString(((char) (j))));
                TestCase.assertTrue(Pattern2Test.UBlocks[i].name, m.matches());
            }
            if ((Pattern2Test.UBlocks[i].high) < 65535) {
                m = p.matcher(Character.toString(((char) ((Pattern2Test.UBlocks[i].high) + 1))));
                TestCase.assertFalse(Pattern2Test.UBlocks[i].name, m.matches());
            }
            p = Pattern.compile((("\\P{In" + (Pattern2Test.UBlocks[i].name)) + "}"));
            if ((Pattern2Test.UBlocks[i].low) > 0) {
                m = p.matcher(Character.toString(((char) ((Pattern2Test.UBlocks[i].low) - 1))));
                TestCase.assertTrue(Pattern2Test.UBlocks[i].name, m.matches());
            }
            for (j = Pattern2Test.UBlocks[i].low; j < (Pattern2Test.UBlocks[i].high); j++) {
                m = p.matcher(Character.toString(((char) (j))));
                TestCase.assertFalse(Pattern2Test.UBlocks[i].name, m.matches());
            }
            if ((Pattern2Test.UBlocks[i].high) < 65535) {
                m = p.matcher(Character.toString(((char) ((Pattern2Test.UBlocks[i].high) + 1))));
                TestCase.assertTrue(Pattern2Test.UBlocks[i].name, m.matches());
            }
        }
    }

    public void testMisc() throws PatternSyntaxException {
        Pattern p;
        Matcher m;
        // Test (?>...)
        // TODO
        // Test (?onflags-offflags)
        // Valid flags are i,m,d,s,u,x
        // TODO
        // Test (?onflags-offflags:...)
        // TODO
        // Test \Q, \E
        p = Pattern.compile("[a-z]+;\\Q[a-z]+;\\Q(foo.*);\\E[0-9]+");
        m = p.matcher("abc;[a-z]+;\\Q(foo.*);411");
        TestCase.assertTrue(m.matches());
        m = p.matcher("abc;def;foo42;555");
        TestCase.assertFalse(m.matches());
        m = p.matcher("abc;\\Qdef;\\Qfoo99;\\E123");
        TestCase.assertFalse(m.matches());
        p = Pattern.compile("[a-z]+;(foo[0-9]-\\Q(...)\\E);[0-9]+");
        m = p.matcher("abc;foo5-(...);123");
        TestCase.assertTrue(m.matches());
        TestCase.assertEquals("foo5-(...)", m.group(1));
        m = p.matcher("abc;foo9-(xxx);789");
        TestCase.assertFalse(m.matches());
        p = Pattern.compile("[a-z]+;(bar[0-9]-[a-z\\Q$-\\E]+);[0-9]+");
        m = p.matcher("abc;bar0-def$-;123");
        TestCase.assertTrue(m.matches());
        // FIXME:
        // This should work the same as the pattern above but fails with the
        // the reference JDK
        p = Pattern.compile("[a-z]+;(bar[0-9]-[a-z\\Q-$\\E]+);[0-9]+");
        m = p.matcher("abc;bar0-def$-;123");
        // assertTrue(m.matches());
        // FIXME:
        // This should work too .. it looks as if just about anything that
        // has more
        // than one character between \Q and \E is broken in the the reference
        // JDK
        p = Pattern.compile("[a-z]+;(bar[0-9]-[a-z\\Q[0-9]\\E]+);[0-9]+");
        m = p.matcher("abc;bar0-def[99]-]0x[;123");
        // assertTrue(m.matches());
        // This is the same as above but with explicit escapes .. and this
        // does work
        // on the the reference JDK
        p = Pattern.compile("[a-z]+;(bar[0-9]-[a-z\\[0\\-9\\]]+);[0-9]+");
        m = p.matcher("abc;bar0-def[99]-]0x[;123");
        TestCase.assertTrue(m.matches());
        // Test #<comment text>
        // TODO
    }

    public void testCompile1() throws PatternSyntaxException {
        Pattern pattern = Pattern.compile("[0-9A-Za-z][0-9A-Za-z\\x2e\\x3a\\x2d\\x5f]*");
        String name = "iso-8859-1";
        TestCase.assertTrue(pattern.matcher(name).matches());
    }

    public void testCompile2() throws PatternSyntaxException {
        String findString = "\\Qimport\\E";
        Pattern pattern = Pattern.compile(findString, 0);
        Matcher matcher = pattern.matcher(new String("import a.A;\n\n import b.B;\nclass C {}"));
        TestCase.assertTrue(matcher.find(0));
    }

    public void testCompile3() throws PatternSyntaxException {
        Pattern p;
        Matcher m;
        p = Pattern.compile("a$");
        m = p.matcher("a\n");
        TestCase.assertTrue(m.find());
        TestCase.assertEquals("a", m.group());
        TestCase.assertFalse(m.find());
        p = Pattern.compile("(a$)");
        m = p.matcher("a\n");
        TestCase.assertTrue(m.find());
        TestCase.assertEquals("a", m.group());
        TestCase.assertEquals("a", m.group(1));
        TestCase.assertFalse(m.find());
        p = Pattern.compile("^.*$", Pattern.MULTILINE);
        m = p.matcher("a\n");
        TestCase.assertTrue(m.find());
        // System.out.println("["+m.group()+"]");
        TestCase.assertEquals("a", m.group());
        TestCase.assertFalse(m.find());
        m = p.matcher("a\nb\n");
        TestCase.assertTrue(m.find());
        // System.out.println("["+m.group()+"]");
        TestCase.assertEquals("a", m.group());
        TestCase.assertTrue(m.find());
        // System.out.println("["+m.group()+"]");
        TestCase.assertEquals("b", m.group());
        TestCase.assertFalse(m.find());
        m = p.matcher("a\nb");
        TestCase.assertTrue(m.find());
        // System.out.println("["+m.group()+"]");
        TestCase.assertEquals("a", m.group());
        TestCase.assertTrue(m.find());
        TestCase.assertEquals("b", m.group());
        TestCase.assertFalse(m.find());
        m = p.matcher("\naa\r\nbb\rcc\n\n");
        TestCase.assertTrue(m.find());
        // System.out.println("["+m.group()+"]");
        TestCase.assertTrue(m.group().equals(""));
        TestCase.assertTrue(m.find());
        // System.out.println("["+m.group()+"]");
        TestCase.assertEquals("aa", m.group());
        TestCase.assertTrue(m.find());
        // System.out.println("["+m.group()+"]");
        TestCase.assertEquals("bb", m.group());
        TestCase.assertTrue(m.find());
        // System.out.println("["+m.group()+"]");
        TestCase.assertEquals("cc", m.group());
        TestCase.assertTrue(m.find());
        // System.out.println("["+m.group()+"]");
        TestCase.assertTrue(m.group().equals(""));
        TestCase.assertFalse(m.find());
        m = p.matcher("a");
        TestCase.assertTrue(m.find());
        TestCase.assertEquals("a", m.group());
        TestCase.assertFalse(m.find());
        m = p.matcher("");
        // This differs from the RI behaviour but seems more correct.
        TestCase.assertTrue(m.find());
        TestCase.assertTrue(m.group().equals(""));
        TestCase.assertFalse(m.find());
        p = Pattern.compile("^.*$");
        m = p.matcher("");
        TestCase.assertTrue(m.find());
        TestCase.assertTrue(m.group().equals(""));
        TestCase.assertFalse(m.find());
    }

    public void testCompile4() throws PatternSyntaxException {
        String findString = "\\Qpublic\\E";
        StringBuffer text = new StringBuffer(("    public class Class {\n" + "    public class Class {"));
        Pattern pattern = Pattern.compile(findString, 0);
        Matcher matcher = pattern.matcher(text);
        boolean found = matcher.find();
        TestCase.assertTrue(found);
        TestCase.assertEquals(4, matcher.start());
        if (found) {
            // modify text
            text.delete(0, text.length());
            text.append("Text have been changed.");
            matcher.reset(text);
        }
        found = matcher.find();
        TestCase.assertFalse(found);
    }

    public void testCompile5() throws PatternSyntaxException {
        Pattern p = Pattern.compile("^[0-9]");
        String[] s = p.split("12", (-1));
        TestCase.assertEquals("", s[0]);
        TestCase.assertEquals("2", s[1]);
        TestCase.assertEquals(2, s.length);
    }

    // public void testCompile6() {
    // String regex = "[\\p{L}[\\p{Mn}[\\p{Pc}[\\p{Nd}[\\p{Nl}[\\p{Sc}]]]]]]+";
    // String regex = "[\\p{L}\\p{Mn}\\p{Pc}\\p{Nd}\\p{Nl}\\p{Sc}]+";
    // try {
    // Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
    // assertTrue(true);
    // } catch (PatternSyntaxException e) {
    // System.out.println(e.getMessage());
    // assertTrue(false);
    // }
    // }
    private static class UBInfo {
        public UBInfo(int low, int high, String name) {
            this.name = name;
            this.low = low;
            this.high = high;
        }

        public String name;

        public int low;

        public int high;
    }

    // A table representing the unicode categories
    // private static UBInfo[] UCategories = {
    // Lu
    // Ll
    // Lt
    // Lm
    // Lo
    // Mn
    // Mc
    // Me
    // Nd
    // Nl
    // No
    // Pc
    // Pd
    // Ps
    // Pe
    // Pi
    // Pf
    // Po
    // Sm
    // Sc
    // Sk
    // So
    // Zs
    // Zl
    // Zp
    // Cc
    // Cf
    // Cs
    // Co
    // Cn
    // };
    // A table representing the unicode character blocks
    private static Pattern2Test.UBInfo[] UBlocks = new Pattern2Test.UBInfo[]{ /* 0000; 007F; Basic Latin */
    new Pattern2Test.UBInfo(0, 127, "BasicLatin")// Character.UnicodeBlock.BASIC_LATIN
    , /* 0080; 00FF; Latin-1 Supplement */
    new Pattern2Test.UBInfo(128, 255, "Latin-1Supplement")// Character.UnicodeBlock.LATIN_1_SUPPLEMENT
    , /* 0100; 017F; Latin Extended-A */
    new Pattern2Test.UBInfo(256, 383, "LatinExtended-A")// Character.UnicodeBlock.LATIN_EXTENDED_A
    , /* 0180; 024F; Latin Extended-B */
    // new UBInfo (0x0180,0x024F,"InLatinExtended-B"), //
    // Character.UnicodeBlock.LATIN_EXTENDED_B
    /* 0250; 02AF; IPA Extensions */
    new Pattern2Test.UBInfo(592, 687, "IPAExtensions")// Character.UnicodeBlock.IPA_EXTENSIONS
    , /* 02B0; 02FF; Spacing Modifier Letters */
    new Pattern2Test.UBInfo(688, 767, "SpacingModifierLetters")// Character.UnicodeBlock.SPACING_MODIFIER_LETTERS
    , /* 0300; 036F; Combining Diacritical Marks */
    new Pattern2Test.UBInfo(768, 879, "CombiningDiacriticalMarks")// Character.UnicodeBlock.COMBINING_DIACRITICAL_MARKS
    , /* 0370; 03FF; Greek */
    new Pattern2Test.UBInfo(880, 1023, "Greek")// Character.UnicodeBlock.GREEK
    , /* 0400; 04FF; Cyrillic */
    new Pattern2Test.UBInfo(1024, 1279, "Cyrillic")// Character.UnicodeBlock.CYRILLIC
    , /* 0530; 058F; Armenian */
    new Pattern2Test.UBInfo(1328, 1423, "Armenian")// Character.UnicodeBlock.ARMENIAN
    , /* 0590; 05FF; Hebrew */
    new Pattern2Test.UBInfo(1424, 1535, "Hebrew")// Character.UnicodeBlock.HEBREW
    , /* 0600; 06FF; Arabic */
    new Pattern2Test.UBInfo(1536, 1791, "Arabic")// Character.UnicodeBlock.ARABIC
    , /* 0700; 074F; Syriac */
    new Pattern2Test.UBInfo(1792, 1871, "Syriac")// Character.UnicodeBlock.SYRIAC
    , /* 0780; 07BF; Thaana */
    new Pattern2Test.UBInfo(1920, 1983, "Thaana")// Character.UnicodeBlock.THAANA
    , /* 0900; 097F; Devanagari */
    new Pattern2Test.UBInfo(2304, 2431, "Devanagari")// Character.UnicodeBlock.DEVANAGARI
    , /* 0980; 09FF; Bengali */
    new Pattern2Test.UBInfo(2432, 2559, "Bengali")// Character.UnicodeBlock.BENGALI
    , /* 0A00; 0A7F; Gurmukhi */
    new Pattern2Test.UBInfo(2560, 2687, "Gurmukhi")// Character.UnicodeBlock.GURMUKHI
    , /* 0A80; 0AFF; Gujarati */
    new Pattern2Test.UBInfo(2688, 2815, "Gujarati")// Character.UnicodeBlock.GUJARATI
    , /* 0B00; 0B7F; Oriya */
    new Pattern2Test.UBInfo(2816, 2943, "Oriya")// Character.UnicodeBlock.ORIYA
    , /* 0B80; 0BFF; Tamil */
    new Pattern2Test.UBInfo(2944, 3071, "Tamil")// Character.UnicodeBlock.TAMIL
    , /* 0C00; 0C7F; Telugu */
    new Pattern2Test.UBInfo(3072, 3199, "Telugu")// Character.UnicodeBlock.TELUGU
    , /* 0C80; 0CFF; Kannada */
    new Pattern2Test.UBInfo(3200, 3327, "Kannada")// Character.UnicodeBlock.KANNADA
    , /* 0D00; 0D7F; Malayalam */
    new Pattern2Test.UBInfo(3328, 3455, "Malayalam")// Character.UnicodeBlock.MALAYALAM
    , /* 0D80; 0DFF; Sinhala */
    new Pattern2Test.UBInfo(3456, 3583, "Sinhala")// Character.UnicodeBlock.SINHALA
    , /* 0E00; 0E7F; Thai */
    new Pattern2Test.UBInfo(3584, 3711, "Thai")// Character.UnicodeBlock.THAI
    , /* 0E80; 0EFF; Lao */
    new Pattern2Test.UBInfo(3712, 3839, "Lao")// Character.UnicodeBlock.LAO
    , /* 0F00; 0FFF; Tibetan */
    new Pattern2Test.UBInfo(3840, 4095, "Tibetan")// Character.UnicodeBlock.TIBETAN
    , /* 1000; 109F; Myanmar */
    new Pattern2Test.UBInfo(4096, 4255, "Myanmar")// Character.UnicodeBlock.MYANMAR
    , /* 10A0; 10FF; Georgian */
    new Pattern2Test.UBInfo(4256, 4351, "Georgian")// Character.UnicodeBlock.GEORGIAN
    , /* 1100; 11FF; Hangul Jamo */
    new Pattern2Test.UBInfo(4352, 4607, "HangulJamo")// Character.UnicodeBlock.HANGUL_JAMO
    , /* 1200; 137F; Ethiopic */
    new Pattern2Test.UBInfo(4608, 4991, "Ethiopic")// Character.UnicodeBlock.ETHIOPIC
    , /* 13A0; 13FF; Cherokee */
    new Pattern2Test.UBInfo(5024, 5119, "Cherokee")// Character.UnicodeBlock.CHEROKEE
    , /* 1400; 167F; Unified Canadian Aboriginal Syllabics */
    new Pattern2Test.UBInfo(5120, 5759, "UnifiedCanadianAboriginalSyllabics")// Character.UnicodeBlock.UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS
    , /* 1680; 169F; Ogham */
    new Pattern2Test.UBInfo(5760, 5791, "Ogham")// Character.UnicodeBlock.OGHAM
    , /* 16A0; 16FF; Runic */
    new Pattern2Test.UBInfo(5792, 5887, "Runic")// Character.UnicodeBlock.RUNIC
    , /* 1780; 17FF; Khmer */
    new Pattern2Test.UBInfo(6016, 6143, "Khmer")// Character.UnicodeBlock.KHMER
    , /* 1800; 18AF; Mongolian */
    new Pattern2Test.UBInfo(6144, 6319, "Mongolian")// Character.UnicodeBlock.MONGOLIAN
    , /* 1E00; 1EFF; Latin Extended Additional */
    new Pattern2Test.UBInfo(7680, 7935, "LatinExtendedAdditional")// Character.UnicodeBlock.LATIN_EXTENDED_ADDITIONAL
    , /* 1F00; 1FFF; Greek Extended */
    new Pattern2Test.UBInfo(7936, 8191, "GreekExtended")// Character.UnicodeBlock.GREEK_EXTENDED
    , /* 2000; 206F; General Punctuation */
    new Pattern2Test.UBInfo(8192, 8303, "GeneralPunctuation")// Character.UnicodeBlock.GENERAL_PUNCTUATION
    , /* 2070; 209F; Superscripts and Subscripts */
    new Pattern2Test.UBInfo(8304, 8351, "SuperscriptsandSubscripts")// Character.UnicodeBlock.SUPERSCRIPTS_AND_SUBSCRIPTS
    , /* 20A0; 20CF; Currency Symbols */
    new Pattern2Test.UBInfo(8352, 8399, "CurrencySymbols")// Character.UnicodeBlock.CURRENCY_SYMBOLS
    , /* 20D0; 20FF; Combining Marks for Symbols */
    new Pattern2Test.UBInfo(8400, 8447, "CombiningMarksforSymbols")// Character.UnicodeBlock.COMBINING_MARKS_FOR_SYMBOLS
    , /* 2100; 214F; Letterlike Symbols */
    new Pattern2Test.UBInfo(8448, 8527, "LetterlikeSymbols")// Character.UnicodeBlock.LETTERLIKE_SYMBOLS
    , /* 2150; 218F; Number Forms */
    new Pattern2Test.UBInfo(8528, 8591, "NumberForms")// Character.UnicodeBlock.NUMBER_FORMS
    , /* 2190; 21FF; Arrows */
    new Pattern2Test.UBInfo(8592, 8703, "Arrows")// Character.UnicodeBlock.ARROWS
    , /* 2200; 22FF; Mathematical Operators */
    new Pattern2Test.UBInfo(8704, 8959, "MathematicalOperators")// Character.UnicodeBlock.MATHEMATICAL_OPERATORS
    , /* 2300; 23FF; Miscellaneous Technical */
    new Pattern2Test.UBInfo(8960, 9215, "MiscellaneousTechnical")// Character.UnicodeBlock.MISCELLANEOUS_TECHNICAL
    , /* 2400; 243F; Control Pictures */
    new Pattern2Test.UBInfo(9216, 9279, "ControlPictures")// Character.UnicodeBlock.CONTROL_PICTURES
    , /* 2440; 245F; Optical Character Recognition */
    new Pattern2Test.UBInfo(9280, 9311, "OpticalCharacterRecognition")// Character.UnicodeBlock.OPTICAL_CHARACTER_RECOGNITION
    , /* 2460; 24FF; Enclosed Alphanumerics */
    new Pattern2Test.UBInfo(9312, 9471, "EnclosedAlphanumerics")// Character.UnicodeBlock.ENCLOSED_ALPHANUMERICS
    , /* 2500; 257F; Box Drawing */
    new Pattern2Test.UBInfo(9472, 9599, "BoxDrawing")// Character.UnicodeBlock.BOX_DRAWING
    , /* 2580; 259F; Block Elements */
    new Pattern2Test.UBInfo(9600, 9631, "BlockElements")// Character.UnicodeBlock.BLOCK_ELEMENTS
    , /* 25A0; 25FF; Geometric Shapes */
    new Pattern2Test.UBInfo(9632, 9727, "GeometricShapes")// Character.UnicodeBlock.GEOMETRIC_SHAPES
    , /* 2600; 26FF; Miscellaneous Symbols */
    new Pattern2Test.UBInfo(9728, 9983, "MiscellaneousSymbols")// Character.UnicodeBlock.MISCELLANEOUS_SYMBOLS
    , /* 2700; 27BF; Dingbats */
    new Pattern2Test.UBInfo(9984, 10175, "Dingbats")// Character.UnicodeBlock.DINGBATS
    , /* 2800; 28FF; Braille Patterns */
    new Pattern2Test.UBInfo(10240, 10495, "BraillePatterns")// Character.UnicodeBlock.BRAILLE_PATTERNS
    , /* 2E80; 2EFF; CJK Radicals Supplement */
    new Pattern2Test.UBInfo(11904, 12031, "CJKRadicalsSupplement")// Character.UnicodeBlock.CJK_RADICALS_SUPPLEMENT
    , /* 2F00; 2FDF; Kangxi Radicals */
    new Pattern2Test.UBInfo(12032, 12255, "KangxiRadicals")// Character.UnicodeBlock.KANGXI_RADICALS
    , /* 2FF0; 2FFF; Ideographic Description Characters */
    new Pattern2Test.UBInfo(12272, 12287, "IdeographicDescriptionCharacters")// Character.UnicodeBlock.IDEOGRAPHIC_DESCRIPTION_CHARACTERS
    , /* 3000; 303F; CJK Symbols and Punctuation */
    new Pattern2Test.UBInfo(12288, 12351, "CJKSymbolsandPunctuation")// Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
    , /* 3040; 309F; Hiragana */
    new Pattern2Test.UBInfo(12352, 12447, "Hiragana")// Character.UnicodeBlock.HIRAGANA
    , /* 30A0; 30FF; Katakana */
    new Pattern2Test.UBInfo(12448, 12543, "Katakana")// Character.UnicodeBlock.KATAKANA
    , /* 3100; 312F; Bopomofo */
    new Pattern2Test.UBInfo(12544, 12591, "Bopomofo")// Character.UnicodeBlock.BOPOMOFO
    , /* 3130; 318F; Hangul Compatibility Jamo */
    new Pattern2Test.UBInfo(12592, 12687, "HangulCompatibilityJamo")// Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO
    , /* 3190; 319F; Kanbun */
    new Pattern2Test.UBInfo(12688, 12703, "Kanbun")// Character.UnicodeBlock.KANBUN
    , /* 31A0; 31BF; Bopomofo Extended */
    new Pattern2Test.UBInfo(12704, 12735, "BopomofoExtended")// Character.UnicodeBlock.BOPOMOFO_EXTENDED
    , /* 3200; 32FF; Enclosed CJK Letters and Months */
    new Pattern2Test.UBInfo(12800, 13055, "EnclosedCJKLettersandMonths")// Character.UnicodeBlock.ENCLOSED_CJK_LETTERS_AND_MONTHS
    , /* 3300; 33FF; CJK Compatibility */
    new Pattern2Test.UBInfo(13056, 13311, "CJKCompatibility")// Character.UnicodeBlock.CJK_COMPATIBILITY
    , /* 3400; 4DB5; CJK Unified Ideographs Extension A */
    new Pattern2Test.UBInfo(13312, 19903, "CJKUnifiedIdeographsExtensionA")// Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
    , /* 4E00; 9FFF; CJK Unified Ideographs */
    new Pattern2Test.UBInfo(19968, 40959, "CJKUnifiedIdeographs")// Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
    , /* A000; A48F; Yi Syllables */
    new Pattern2Test.UBInfo(40960, 42127, "YiSyllables")// Character.UnicodeBlock.YI_SYLLABLES
    , /* A490; A4CF; Yi Radicals */
    new Pattern2Test.UBInfo(42128, 42191, "YiRadicals")// Character.UnicodeBlock.YI_RADICALS
    , /* AC00; D7A3; Hangul Syllables */
    new Pattern2Test.UBInfo(44032, 55215, "HangulSyllables")// Character.UnicodeBlock.HANGUL_SYLLABLES
    , /* D800; DB7F; High Surrogates */
    /* DB80; DBFF; High Private Use Surrogates */
    /* DC00; DFFF; Low Surrogates */
    /* E000; F8FF; Private Use */
    /* F900; FAFF; CJK Compatibility Ideographs */
    new Pattern2Test.UBInfo(63744, 64255, "CJKCompatibilityIdeographs")// Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
    , /* FB00; FB4F; Alphabetic Presentation Forms */
    new Pattern2Test.UBInfo(64256, 64335, "AlphabeticPresentationForms")// Character.UnicodeBlock.ALPHABETIC_PRESENTATION_FORMS
    , /* FB50; FDFF; Arabic Presentation Forms-A */
    new Pattern2Test.UBInfo(64336, 65023, "ArabicPresentationForms-A")// Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_A
    , /* FE20; FE2F; Combining Half Marks */
    new Pattern2Test.UBInfo(65056, 65071, "CombiningHalfMarks")// Character.UnicodeBlock.COMBINING_HALF_MARKS
    , /* FE30; FE4F; CJK Compatibility Forms */
    new Pattern2Test.UBInfo(65072, 65103, "CJKCompatibilityForms")// Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS
    , /* FE50; FE6F; Small Form Variants */
    new Pattern2Test.UBInfo(65104, 65135, "SmallFormVariants")// Character.UnicodeBlock.SMALL_FORM_VARIANTS
    , /* FE70; FEFE; Arabic Presentation Forms-B */
    new Pattern2Test.UBInfo(65136, 65279, "ArabicPresentationForms-B")// Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_B
    , /* FF00; FFEF; Halfwidth and Fullwidth Forms */
    new Pattern2Test.UBInfo(65280, 65519, "HalfwidthandFullwidthForms")// Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
    , /* FFF0; FFFD; Specials */
    new Pattern2Test.UBInfo(65520, 65535, "Specials")// Character.UnicodeBlock.SPECIALS
     };
}

