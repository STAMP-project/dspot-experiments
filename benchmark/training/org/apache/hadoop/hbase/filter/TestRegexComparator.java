/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.filter;


import java.util.regex.Pattern;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.filter.RegexStringComparator.EngineType;
import org.apache.hadoop.hbase.testclassification.FilterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ FilterTests.class, SmallTests.class })
public class TestRegexComparator {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegexComparator.class);

    @Test
    public void testSerialization() throws Exception {
        // Default engine is the Java engine
        RegexStringComparator a = new RegexStringComparator("a|b");
        RegexStringComparator b = RegexStringComparator.parseFrom(a.toByteArray());
        Assert.assertTrue(a.areSerializedFieldsEqual(b));
        Assert.assertTrue(((b.getEngine()) instanceof RegexStringComparator.JavaRegexEngine));
        // joni engine
        a = new RegexStringComparator("a|b", EngineType.JONI);
        b = RegexStringComparator.parseFrom(a.toByteArray());
        Assert.assertTrue(a.areSerializedFieldsEqual(b));
        Assert.assertTrue(((b.getEngine()) instanceof RegexStringComparator.JoniRegexEngine));
    }

    @Test
    public void testJavaEngine() throws Exception {
        for (TestRegexComparator.TestCase t : TestRegexComparator.TEST_CASES) {
            boolean result = (new RegexStringComparator(t.regex, t.flags, EngineType.JAVA).compareTo(Bytes.toBytes(t.haystack))) == 0;
            Assert.assertEquals((((("Regex '" + (t.regex)) + "' failed test '") + (t.haystack)) + "'"), result, t.expected);
        }
    }

    @Test
    public void testJoniEngine() throws Exception {
        for (TestRegexComparator.TestCase t : TestRegexComparator.TEST_CASES) {
            boolean result = (new RegexStringComparator(t.regex, t.flags, EngineType.JONI).compareTo(Bytes.toBytes(t.haystack))) == 0;
            Assert.assertEquals((((("Regex '" + (t.regex)) + "' failed test '") + (t.haystack)) + "'"), result, t.expected);
        }
    }

    private static class TestCase {
        String regex;

        String haystack;

        int flags;

        boolean expected;

        public TestCase(String regex, String haystack, boolean expected) {
            this(regex, Pattern.DOTALL, haystack, expected);
        }

        public TestCase(String regex, int flags, String haystack, boolean expected) {
            this.regex = regex;
            this.flags = flags;
            this.haystack = haystack;
            this.expected = expected;
        }
    }

    // These are a subset of the regex tests from OpenJDK 7
    private static TestRegexComparator.TestCase[] TEST_CASES = new TestRegexComparator.TestCase[]{ new TestRegexComparator.TestCase("a|b", "a", true), new TestRegexComparator.TestCase("a|b", "b", true), new TestRegexComparator.TestCase("a|b", Pattern.CASE_INSENSITIVE, "A", true), new TestRegexComparator.TestCase("a|b", Pattern.CASE_INSENSITIVE, "B", true), new TestRegexComparator.TestCase("a|b", "z", false), new TestRegexComparator.TestCase("a|b|cd", "cd", true), new TestRegexComparator.TestCase("z(a|ac)b", "zacb", true), new TestRegexComparator.TestCase("[abc]+", "ababab", true), new TestRegexComparator.TestCase("[abc]+", "defg", false), new TestRegexComparator.TestCase("[abc]+[def]+[ghi]+", "zzzaaddggzzz", true), new TestRegexComparator.TestCase("[a-\\u4444]+", "za-9z", true), new TestRegexComparator.TestCase("[^abc]+", "ababab", false), new TestRegexComparator.TestCase("[^abc]+", "aaabbbcccdefg", true), new TestRegexComparator.TestCase("[abc^b]", "b", true), new TestRegexComparator.TestCase("[abc[def]]", "b", true), new TestRegexComparator.TestCase("[abc[def]]", "e", true), new TestRegexComparator.TestCase("[a-c[d-f[g-i]]]", "h", true), new TestRegexComparator.TestCase("[a-c[d-f[g-i]]m]", "m", true), new TestRegexComparator.TestCase("[a-c&&[d-f]]", "a", false), new TestRegexComparator.TestCase("[a-c&&[d-f]]", "z", false), new TestRegexComparator.TestCase("[a-m&&m-z&&a-c]", "m", false), new TestRegexComparator.TestCase("[a-m&&m-z&&a-z]", "m", true), new TestRegexComparator.TestCase("[[a-m]&&[^a-c]]", "a", false), new TestRegexComparator.TestCase("[[a-m]&&[^a-c]]", "d", true), new TestRegexComparator.TestCase("[[a-c][d-f]&&abc[def]]", "e", true), new TestRegexComparator.TestCase("[[a-c]&&[b-d]&&[c-e]]", "c", true), new TestRegexComparator.TestCase("[[a-c]&&[b-d][c-e]&&[u-z]]", "c", false), new TestRegexComparator.TestCase("[[a]&&[b][c][a]&&[^d]]", "a", true), new TestRegexComparator.TestCase("[[a]&&[b][c][a]&&[^d]]", "d", false), new TestRegexComparator.TestCase("[[[a-d]&&[c-f]]&&[c]&&c&&[cde]]", "c", true), new TestRegexComparator.TestCase("[x[[wz]abc&&bcd[z]]&&[u-z]]", "z", true), new TestRegexComparator.TestCase("a.c.+", "a#c%&", true), new TestRegexComparator.TestCase("ab.", "ab\n", true), new TestRegexComparator.TestCase("(?s)ab.", "ab\n", true), new TestRegexComparator.TestCase("ab\\wc", "abcc", true), new TestRegexComparator.TestCase("\\W\\w\\W", "#r#", true), new TestRegexComparator.TestCase("\\W\\w\\W", "rrrr#ggg", false), new TestRegexComparator.TestCase("abc[\\sdef]*", "abc  def", true), new TestRegexComparator.TestCase("abc[\\sy-z]*", "abc y z", true), new TestRegexComparator.TestCase("abc[a-d\\sm-p]*", "abcaa mn  p", true), new TestRegexComparator.TestCase("\\s\\s\\s", "blah  err", false), new TestRegexComparator.TestCase("\\S\\S\\s", "blah  err", true), new TestRegexComparator.TestCase("ab\\dc", "ab9c", true), new TestRegexComparator.TestCase("\\d\\d\\d", "blah45", false), new TestRegexComparator.TestCase("^abc", "abcdef", true), new TestRegexComparator.TestCase("^abc", "bcdabc", false), new TestRegexComparator.TestCase("^(a)?a", "a", true), new TestRegexComparator.TestCase("^(aa(bb)?)+$", "aabbaa", true), new TestRegexComparator.TestCase("((a|b)?b)+", "b", true), new TestRegexComparator.TestCase("^(a(b)?)+$", "aba", true), new TestRegexComparator.TestCase("^(a(b(c)?)?)?abc", "abc", true), new TestRegexComparator.TestCase("^(a(b(c))).*", "abc", true), new TestRegexComparator.TestCase("a?b", "aaaab", true), new TestRegexComparator.TestCase("a?b", "aaacc", false), new TestRegexComparator.TestCase("a??b", "aaaab", true), new TestRegexComparator.TestCase("a??b", "aaacc", false), new TestRegexComparator.TestCase("a?+b", "aaaab", true), new TestRegexComparator.TestCase("a?+b", "aaacc", false), new TestRegexComparator.TestCase("a+b", "aaaab", true), new TestRegexComparator.TestCase("a+b", "aaacc", false), new TestRegexComparator.TestCase("a+?b", "aaaab", true), new TestRegexComparator.TestCase("a+?b", "aaacc", false), new TestRegexComparator.TestCase("a++b", "aaaab", true), new TestRegexComparator.TestCase("a++b", "aaacc", false), new TestRegexComparator.TestCase("a{2,3}", "a", false), new TestRegexComparator.TestCase("a{2,3}", "aa", true), new TestRegexComparator.TestCase("a{2,3}", "aaa", true), new TestRegexComparator.TestCase("a{3,}", "zzzaaaazzz", true), new TestRegexComparator.TestCase("a{3,}", "zzzaazzz", false), new TestRegexComparator.TestCase("abc(?=d)", "zzzabcd", true), new TestRegexComparator.TestCase("abc(?=d)", "zzzabced", false), new TestRegexComparator.TestCase("abc(?!d)", "zzabcd", false), new TestRegexComparator.TestCase("abc(?!d)", "zzabced", true), new TestRegexComparator.TestCase("\\w(?<=a)", "###abc###", true), new TestRegexComparator.TestCase("\\w(?<=a)", "###ert###", false), new TestRegexComparator.TestCase("(?<!a)c", "bc", true), new TestRegexComparator.TestCase("(?<!a)c", "ac", false), new TestRegexComparator.TestCase("(a+b)+", "ababab", true), new TestRegexComparator.TestCase("(a+b)+", "accccd", false), new TestRegexComparator.TestCase("(ab)+", "ababab", true), new TestRegexComparator.TestCase("(ab)+", "accccd", false), new TestRegexComparator.TestCase("(ab)(cd*)", "zzzabczzz", true), new TestRegexComparator.TestCase("abc(d)*abc", "abcdddddabc", true), new TestRegexComparator.TestCase("a*b", "aaaab", true), new TestRegexComparator.TestCase("a*b", "b", true), new TestRegexComparator.TestCase("a*b", "aaaac", false), new TestRegexComparator.TestCase(".*?b", "aaaab", true), new TestRegexComparator.TestCase("a*+b", "aaaab", true), new TestRegexComparator.TestCase("a*+b", "b", true), new TestRegexComparator.TestCase("a*+b", "aaaac", false), new TestRegexComparator.TestCase("(?i)foobar", "fOobAr", true), new TestRegexComparator.TestCase("f(?i)oobar", "fOobAr", true), new TestRegexComparator.TestCase("f(?i)oobar", "FOobAr", false), new TestRegexComparator.TestCase("foo(?i)bar", "fOobAr", false), new TestRegexComparator.TestCase("(?i)foo[bar]+", "foObAr", true), new TestRegexComparator.TestCase("(?i)foo[a-r]+", "foObAr", true), new TestRegexComparator.TestCase("abc(?x)blah", "abcblah", true), new TestRegexComparator.TestCase("abc(?x)  blah", "abcblah", true), new TestRegexComparator.TestCase("abc(?x)  blah  blech", "abcblahblech", true), new TestRegexComparator.TestCase("[\\n-#]", "!", true), new TestRegexComparator.TestCase("[\\n-#]", "-", false), new TestRegexComparator.TestCase("[\\043]+", "blahblah#blech", true), new TestRegexComparator.TestCase("[\\042-\\044]+", "blahblah#blech", true), new TestRegexComparator.TestCase("[\\u1234-\\u1236]", "blahblah\u1235blech", true), new TestRegexComparator.TestCase("[^#]*", "blahblah#blech", true), new TestRegexComparator.TestCase("(|f)?+", "foo", true) };
}

