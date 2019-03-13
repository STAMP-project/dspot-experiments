/**
 * Copyright (C) 2008 The Android Open Source Project
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
package libcore.java.util.regex;


import java.util.regex.Matcher;
import java.util.regex.Pattern;
import junit.framework.TestCase;


public final class OldAndroidRegexTest extends TestCase {
    public void testMatches() throws Exception {
        /* Tests class Matcher */
        Pattern p = Pattern.compile("bcd");
        Matcher m = p.matcher("bcd");
        TestCase.assertTrue("Should match.", m.matches());
        /* Pattern in the middle */
        p = Pattern.compile("bcd");
        m = p.matcher("abcdefg");
        TestCase.assertFalse("Should not match.", m.matches());
        /* Pattern at the head */
        m = p.matcher("bcdefg");
        TestCase.assertFalse("Should not match.", m.matches());
        /* Pattern at the tail */
        m = p.matcher("abcd");
        TestCase.assertFalse("Should not match.", m.matches());
        /* Make sure matches() doesn't change after calls to find() */
        p = Pattern.compile(".*");
        m = p.matcher("abc");
        TestCase.assertTrue(m.matches());
        TestCase.assertTrue(m.find());
        TestCase.assertTrue(m.matches());
        p = Pattern.compile(".");
        m = p.matcher("abc");
        TestCase.assertFalse(m.matches());
        TestCase.assertTrue(m.find());
        TestCase.assertFalse(m.matches());
        /* Make sure matches() agrees after a reset() */
        m.reset("z");
        TestCase.assertTrue(m.matches());
        m.reset("xyz");
        TestCase.assertFalse(m.matches());
        /* Tests class Pattern */
        TestCase.assertFalse(("Erroneously matched partial string.  " + "See http://b/issue?id=754601"), Pattern.matches("er", "xer"));
        TestCase.assertFalse(("Erroneously matched partial string.  " + "See http://b/issue?id=754601"), Pattern.matches("xe", "xer"));
        TestCase.assertTrue("Generic regex should match.", Pattern.matches(".*", "bcd"));
        TestCase.assertTrue("Grouped regex should match.", Pattern.matches("(b(c(d)))", "bcd"));
        TestCase.assertTrue("Grouped regex should match.", Pattern.matches("(b)(c)(d)", "bcd"));
    }

    public void testGroupCount() throws Exception {
        Pattern p = Pattern.compile(("\\b(?:\\+?1)?" + ((((("(?:[ -\\.])?" + "\\(?(\\d{3})?\\)?") + "(?:[ -\\.\\/])?") + "(\\d{3})") + "(?:[ -\\.])?") + "(\\d{4})\\b")));
        Matcher m = p.matcher("1 (919) 555-1212");
        TestCase.assertEquals("groupCount is incorrect, see http://b/issue?id=759412", 3, m.groupCount());
    }

    public void testGroups() throws Exception {
        Pattern p = Pattern.compile("(b)([c|d])(z*)");
        Matcher m = p.matcher("abcdefg");
        /* Must call find() first, otherwise group*() are undefined. */
        TestCase.assertTrue(m.find());
        TestCase.assertEquals(3, m.groupCount());
        TestCase.assertEquals("bc", m.group(0));
        TestCase.assertEquals("b", m.group(1));
        TestCase.assertEquals("c", m.group(2));
        TestCase.assertEquals("", m.group(3));
    }

    public void testFind() throws Exception {
        Pattern p = Pattern.compile(".");
        Matcher m = p.matcher("abc");
        TestCase.assertTrue(m.find());
        TestCase.assertEquals("a", m.group(0));
        TestCase.assertTrue(m.find());
        TestCase.assertEquals("b", m.group(0));
        TestCase.assertTrue(m.find());
        TestCase.assertEquals("c", m.group(0));
        TestCase.assertFalse(m.find());
    }

    public void testReplaceAll() throws Exception {
        // Begins with non-matching text, ends with matching text
        Pattern p = Pattern.compile("a*b");
        Matcher m = p.matcher("fooaabfooaabfooabfoob");
        String r = m.replaceAll("-");
        TestCase.assertEquals("foo-foo-foo-foo-", r);
        // Begins with matching text, ends with non-matching text
        p = Pattern.compile("a*b");
        m = p.matcher("aabfooaabfooabfoobfoo");
        r = m.replaceAll("-");
        TestCase.assertEquals("-foo-foo-foo-foo", r);
    }

    public void testReplaceFirst() throws Exception {
        // Begins with non-matching text, ends with matching text
        Pattern p = Pattern.compile("a*b");
        Matcher m = p.matcher("fooaabfooaabfooabfoob");
        String r = m.replaceFirst("-");
        TestCase.assertEquals("foo-fooaabfooabfoob", r);
        // Begins with matching text, ends with non-matching text
        p = Pattern.compile("a*b");
        m = p.matcher("aabfooaabfooabfoobfoo");
        r = m.replaceFirst("-");
        TestCase.assertEquals("-fooaabfooabfoobfoo", r);
    }

    public void testSplit() throws Exception {
        Pattern p = Pattern.compile(":");
        String[] strings;
        strings = p.split("boo:and:foo");
        TestCase.assertEquals(3, strings.length);
        TestCase.assertEquals("boo", strings[0]);
        TestCase.assertEquals("and", strings[1]);
        TestCase.assertEquals("foo", strings[2]);
        strings = p.split("boo:and:foo", 2);
        TestCase.assertEquals(2, strings.length);
        TestCase.assertEquals("boo", strings[0]);
        TestCase.assertEquals("and:foo", strings[1]);
        strings = p.split("boo:and:foo", 5);
        TestCase.assertEquals(3, strings.length);
        TestCase.assertEquals("boo", strings[0]);
        TestCase.assertEquals("and", strings[1]);
        TestCase.assertEquals("foo", strings[2]);
        strings = p.split("boo:and:foo", (-2));
        TestCase.assertEquals(3, strings.length);
        TestCase.assertEquals("boo", strings[0]);
        TestCase.assertEquals("and", strings[1]);
        TestCase.assertEquals("foo", strings[2]);
        p = Pattern.compile("o");
        strings = p.split("boo:and:foo");
        TestCase.assertEquals(3, strings.length);
        TestCase.assertEquals("b", strings[0]);
        TestCase.assertEquals("", strings[1]);
        TestCase.assertEquals(":and:f", strings[2]);
        strings = p.split("boo:and:foo", 5);
        TestCase.assertEquals(5, strings.length);
        TestCase.assertEquals("b", strings[0]);
        TestCase.assertEquals("", strings[1]);
        TestCase.assertEquals(":and:f", strings[2]);
        TestCase.assertEquals("", strings[3]);
        TestCase.assertEquals("", strings[4]);
        strings = p.split("boo:and:foo", (-2));
        TestCase.assertEquals(5, strings.length);
        TestCase.assertEquals("b", strings[0]);
        TestCase.assertEquals("", strings[1]);
        TestCase.assertEquals(":and:f", strings[2]);
        TestCase.assertEquals("", strings[3]);
        TestCase.assertEquals("", strings[4]);
        strings = p.split("boo:and:foo", 0);
        TestCase.assertEquals(3, strings.length);
        TestCase.assertEquals("b", strings[0]);
        TestCase.assertEquals("", strings[1]);
        TestCase.assertEquals(":and:f", strings[2]);
    }

    // -------------------------------------------------------------------
    // Regression test for #1172774: Bug in Regex.java
    // Regression test for #1216887: Regular expression match is very slow
    public static final Pattern TOP_LEVEL_DOMAIN_PATTERN = Pattern.compile(("((aero|arpa|asia|a[cdefgilmnoqrstuwxz])" + ((((((((((((((((((((((("|(biz|b[abdefghijmnorstvwyz])" + "|(cat|com|coop|c[acdfghiklmnoruvxyz])") + "|d[ejkmoz]") + "|(edu|e[cegrstu])") + "|f[ijkmor]") + "|(gov|g[abdefghilmnpqrstuwy])") + "|h[kmnrtu]") + "|(info|int|i[delmnoqrst])") + "|(jobs|j[emop])") + "|k[eghimnrwyz]") + "|l[abcikrstuvy]") + "|(mil|mobi|museum|m[acdghklmnopqrstuvwxyz])") + "|(name|net|n[acefgilopruz])") + "|(org|om)") + "|(pro|p[aefghklmnrstwy])") + "|qa") + "|r[eouw]") + "|s[abcdeghijklmnortuvyz]") + "|(tel|travel|t[cdfghjklmnoprtvwz])") + "|u[agkmsyz]") + "|v[aceginu]") + "|w[fs]") + "|y[etu]") + "|z[amw])")));

    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(((("[\\+a-zA-Z0-9\\.\\_\\%\\-]+\\@" + (("((" + "[a-zA-Z0-9]\\.|") + "([a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9]\\.)+)")) + (OldAndroidRegexTest.TOP_LEVEL_DOMAIN_PATTERN)) + ")"));

    public void testMonsterRegexCorrectness() {
        TestCase.assertTrue(OldAndroidRegexTest.EMAIL_ADDRESS_PATTERN.matcher("a+b@gmail.com").matches());
    }

    public void testMonsterRegexPerformance() {
        long t0 = System.currentTimeMillis();
        Matcher m = OldAndroidRegexTest.EMAIL_ADDRESS_PATTERN.matcher("donot repeate@RC8jjjjjjjjjjjjjjj");
        TestCase.assertFalse(m.find());
        long t1 = System.currentTimeMillis();
        System.out.println((("RegEx performance test finished, took " + (t1 - t0)) + " ms."));
    }
}

