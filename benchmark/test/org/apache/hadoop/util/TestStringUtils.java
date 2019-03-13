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
package org.apache.hadoop.util;


import TraditionalBinaryPrefix.KILO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.hadoop.test.UnitTestcaseTimeLimit;
import org.junit.Assert;
import org.junit.Test;

import static StringUtils.SHELL_ENV_VAR_PATTERN;
import static StringUtils.WIN_ENV_VAR_PATTERN;
import static TraditionalBinaryPrefix.long2String;
import static TraditionalBinaryPrefix.string2long;


public class TestStringUtils extends UnitTestcaseTimeLimit {
    private static final String NULL_STR = null;

    private static final String EMPTY_STR = "";

    private static final String STR_WO_SPECIAL_CHARS = "AB";

    private static final String STR_WITH_COMMA = "A,B";

    private static final String ESCAPED_STR_WITH_COMMA = "A\\,B";

    private static final String STR_WITH_ESCAPE = "AB\\";

    private static final String ESCAPED_STR_WITH_ESCAPE = "AB\\\\";

    private static final String STR_WITH_BOTH2 = ",A\\,,B\\\\,";

    private static final String ESCAPED_STR_WITH_BOTH2 = "\\,A\\\\\\,\\,B\\\\\\\\\\,";

    private static final FastDateFormat FAST_DATE_FORMAT = FastDateFormat.getInstance("d-MMM-yyyy HH:mm:ss");

    @Test(timeout = 30000)
    public void testEscapeString() throws Exception {
        Assert.assertEquals(TestStringUtils.NULL_STR, StringUtils.escapeString(TestStringUtils.NULL_STR));
        Assert.assertEquals(TestStringUtils.EMPTY_STR, StringUtils.escapeString(TestStringUtils.EMPTY_STR));
        Assert.assertEquals(TestStringUtils.STR_WO_SPECIAL_CHARS, StringUtils.escapeString(TestStringUtils.STR_WO_SPECIAL_CHARS));
        Assert.assertEquals(TestStringUtils.ESCAPED_STR_WITH_COMMA, StringUtils.escapeString(TestStringUtils.STR_WITH_COMMA));
        Assert.assertEquals(TestStringUtils.ESCAPED_STR_WITH_ESCAPE, StringUtils.escapeString(TestStringUtils.STR_WITH_ESCAPE));
        Assert.assertEquals(TestStringUtils.ESCAPED_STR_WITH_BOTH2, StringUtils.escapeString(TestStringUtils.STR_WITH_BOTH2));
    }

    @Test(timeout = 30000)
    public void testSplit() throws Exception {
        Assert.assertEquals(TestStringUtils.NULL_STR, StringUtils.split(TestStringUtils.NULL_STR));
        String[] splits = StringUtils.split(TestStringUtils.EMPTY_STR);
        Assert.assertEquals(0, splits.length);
        splits = StringUtils.split(",,");
        Assert.assertEquals(0, splits.length);
        splits = StringUtils.split(TestStringUtils.STR_WO_SPECIAL_CHARS);
        Assert.assertEquals(1, splits.length);
        Assert.assertEquals(TestStringUtils.STR_WO_SPECIAL_CHARS, splits[0]);
        splits = StringUtils.split(TestStringUtils.STR_WITH_COMMA);
        Assert.assertEquals(2, splits.length);
        Assert.assertEquals("A", splits[0]);
        Assert.assertEquals("B", splits[1]);
        splits = StringUtils.split(TestStringUtils.ESCAPED_STR_WITH_COMMA);
        Assert.assertEquals(1, splits.length);
        Assert.assertEquals(TestStringUtils.ESCAPED_STR_WITH_COMMA, splits[0]);
        splits = StringUtils.split(TestStringUtils.STR_WITH_ESCAPE);
        Assert.assertEquals(1, splits.length);
        Assert.assertEquals(TestStringUtils.STR_WITH_ESCAPE, splits[0]);
        splits = StringUtils.split(TestStringUtils.STR_WITH_BOTH2);
        Assert.assertEquals(3, splits.length);
        Assert.assertEquals(TestStringUtils.EMPTY_STR, splits[0]);
        Assert.assertEquals("A\\,", splits[1]);
        Assert.assertEquals("B\\\\", splits[2]);
        splits = StringUtils.split(TestStringUtils.ESCAPED_STR_WITH_BOTH2);
        Assert.assertEquals(1, splits.length);
        Assert.assertEquals(TestStringUtils.ESCAPED_STR_WITH_BOTH2, splits[0]);
    }

    @Test(timeout = 30000)
    public void testSimpleSplit() throws Exception {
        final String[] TO_TEST = new String[]{ "a/b/c", "a/b/c////", "///a/b/c", "", "/", "////" };
        for (String testSubject : TO_TEST) {
            Assert.assertArrayEquals((("Testing '" + testSubject) + "'"), testSubject.split("/"), StringUtils.split(testSubject, '/'));
        }
    }

    @Test(timeout = 30000)
    public void testUnescapeString() throws Exception {
        Assert.assertEquals(TestStringUtils.NULL_STR, StringUtils.unEscapeString(TestStringUtils.NULL_STR));
        Assert.assertEquals(TestStringUtils.EMPTY_STR, StringUtils.unEscapeString(TestStringUtils.EMPTY_STR));
        Assert.assertEquals(TestStringUtils.STR_WO_SPECIAL_CHARS, StringUtils.unEscapeString(TestStringUtils.STR_WO_SPECIAL_CHARS));
        try {
            StringUtils.unEscapeString(TestStringUtils.STR_WITH_COMMA);
            Assert.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        Assert.assertEquals(TestStringUtils.STR_WITH_COMMA, StringUtils.unEscapeString(TestStringUtils.ESCAPED_STR_WITH_COMMA));
        try {
            StringUtils.unEscapeString(TestStringUtils.STR_WITH_ESCAPE);
            Assert.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        Assert.assertEquals(TestStringUtils.STR_WITH_ESCAPE, StringUtils.unEscapeString(TestStringUtils.ESCAPED_STR_WITH_ESCAPE));
        try {
            StringUtils.unEscapeString(TestStringUtils.STR_WITH_BOTH2);
            Assert.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        Assert.assertEquals(TestStringUtils.STR_WITH_BOTH2, StringUtils.unEscapeString(TestStringUtils.ESCAPED_STR_WITH_BOTH2));
    }

    @Test(timeout = 30000)
    public void testTraditionalBinaryPrefix() throws Exception {
        // test string2long(..)
        String[] symbol = new String[]{ "k", "m", "g", "t", "p", "e" };
        long m = 1024;
        for (String s : symbol) {
            Assert.assertEquals(0, string2long((0 + s)));
            Assert.assertEquals(m, string2long((1 + s)));
            m *= 1024;
        }
        Assert.assertEquals(0L, string2long("0"));
        Assert.assertEquals(1024L, string2long("1k"));
        Assert.assertEquals((-1024L), string2long("-1k"));
        Assert.assertEquals(1259520L, string2long("1230K"));
        Assert.assertEquals((-1259520L), string2long("-1230K"));
        Assert.assertEquals(104857600L, string2long("100m"));
        Assert.assertEquals((-104857600L), string2long("-100M"));
        Assert.assertEquals(956703965184L, string2long("891g"));
        Assert.assertEquals((-956703965184L), string2long("-891G"));
        Assert.assertEquals(501377302265856L, string2long("456t"));
        Assert.assertEquals((-501377302265856L), string2long("-456T"));
        Assert.assertEquals(11258999068426240L, string2long("10p"));
        Assert.assertEquals((-11258999068426240L), string2long("-10P"));
        Assert.assertEquals(1152921504606846976L, string2long("1e"));
        Assert.assertEquals((-1152921504606846976L), string2long("-1E"));
        String tooLargeNumStr = "10e";
        try {
            string2long(tooLargeNumStr);
            Assert.fail((("Test passed for a number " + tooLargeNumStr) + " too large"));
        } catch (IllegalArgumentException e) {
            Assert.assertEquals((tooLargeNumStr + " does not fit in a Long"), e.getMessage());
        }
        String tooSmallNumStr = "-10e";
        try {
            string2long(tooSmallNumStr);
            Assert.fail((("Test passed for a number " + tooSmallNumStr) + " too small"));
        } catch (IllegalArgumentException e) {
            Assert.assertEquals((tooSmallNumStr + " does not fit in a Long"), e.getMessage());
        }
        String invalidFormatNumStr = "10kb";
        char invalidPrefix = 'b';
        try {
            string2long(invalidFormatNumStr);
            Assert.fail((("Test passed for a number " + invalidFormatNumStr) + " has invalid format"));
        } catch (IllegalArgumentException e) {
            Assert.assertEquals((((("Invalid size prefix '" + invalidPrefix) + "' in '") + invalidFormatNumStr) + "'. Allowed prefixes are k, m, g, t, p, e(case insensitive)"), e.getMessage());
        }
        // test long2string(..)
        Assert.assertEquals("0", TraditionalBinaryPrefix.long2String(0, null, 2));
        for (int decimalPlace = 0; decimalPlace < 2; decimalPlace++) {
            for (int n = 1; n < (KILO.value); n++) {
                Assert.assertEquals((n + ""), TraditionalBinaryPrefix.long2String(n, null, decimalPlace));
                Assert.assertEquals(((-n) + ""), TraditionalBinaryPrefix.long2String((-n), null, decimalPlace));
            }
            Assert.assertEquals("1 K", long2String((1L << 10), null, decimalPlace));
            Assert.assertEquals("-1 K", long2String(((-1L) << 10), null, decimalPlace));
        }
        Assert.assertEquals("8.00 E", long2String(Long.MAX_VALUE, null, 2));
        Assert.assertEquals("8.00 E", long2String(((Long.MAX_VALUE) - 1), null, 2));
        Assert.assertEquals("-8 E", long2String(Long.MIN_VALUE, null, 2));
        Assert.assertEquals("-8.00 E", long2String(((Long.MIN_VALUE) + 1), null, 2));
        final String[] zeros = new String[]{ " ", ".0 ", ".00 " };
        for (int decimalPlace = 0; decimalPlace < (zeros.length); decimalPlace++) {
            final String trailingZeros = zeros[decimalPlace];
            for (int e = 11; e < ((Long.SIZE) - 1); e++) {
                final org.apache.hadoop.util.StringUtils.TraditionalBinaryPrefix p = org.apache.hadoop.util.StringUtils.TraditionalBinaryPrefix.values()[((e / 10) - 1)];
                {
                    // n = 2^e
                    final long n = 1L << e;
                    final String expected = ((n / (p.value)) + " ") + (p.symbol);
                    Assert.assertEquals(("n=" + n), expected, long2String(n, null, 2));
                }
                {
                    // n = 2^e + 1
                    final long n = (1L << e) + 1;
                    final String expected = ((n / (p.value)) + trailingZeros) + (p.symbol);
                    Assert.assertEquals(("n=" + n), expected, long2String(n, null, decimalPlace));
                }
                {
                    // n = 2^e - 1
                    final long n = (1L << e) - 1;
                    final String expected = (((n + 1) / (p.value)) + trailingZeros) + (p.symbol);
                    Assert.assertEquals(("n=" + n), expected, long2String(n, null, decimalPlace));
                }
            }
        }
        Assert.assertEquals("1.50 K", long2String((3L << 9), null, 2));
        Assert.assertEquals("1.5 K", long2String((3L << 9), null, 1));
        Assert.assertEquals("1.50 M", long2String((3L << 19), null, 2));
        Assert.assertEquals("2 M", long2String((3L << 19), null, 0));
        Assert.assertEquals("3 G", long2String((3L << 30), null, 2));
        // test byteDesc(..)
        Assert.assertEquals("0 B", StringUtils.byteDesc(0));
        Assert.assertEquals("-100 B", StringUtils.byteDesc((-100)));
        Assert.assertEquals("1 KB", StringUtils.byteDesc(1024));
        Assert.assertEquals("1.50 KB", StringUtils.byteDesc((3L << 9)));
        Assert.assertEquals("1.50 MB", StringUtils.byteDesc((3L << 19)));
        Assert.assertEquals("3 GB", StringUtils.byteDesc((3L << 30)));
        // test formatPercent(..)
        Assert.assertEquals("10%", StringUtils.formatPercent(0.1, 0));
        Assert.assertEquals("10.0%", StringUtils.formatPercent(0.1, 1));
        Assert.assertEquals("10.00%", StringUtils.formatPercent(0.1, 2));
        Assert.assertEquals("1%", StringUtils.formatPercent(0.00543, 0));
        Assert.assertEquals("0.5%", StringUtils.formatPercent(0.00543, 1));
        Assert.assertEquals("0.54%", StringUtils.formatPercent(0.00543, 2));
        Assert.assertEquals("0.543%", StringUtils.formatPercent(0.00543, 3));
        Assert.assertEquals("0.5430%", StringUtils.formatPercent(0.00543, 4));
    }

    @Test(timeout = 30000)
    public void testJoin() {
        List<String> s = new ArrayList<String>();
        s.add("a");
        s.add("b");
        s.add("c");
        Assert.assertEquals("", StringUtils.join(":", s.subList(0, 0)));
        Assert.assertEquals("a", StringUtils.join(":", s.subList(0, 1)));
        Assert.assertEquals("", StringUtils.join(':', s.subList(0, 0)));
        Assert.assertEquals("a", StringUtils.join(':', s.subList(0, 1)));
        Assert.assertEquals("a:b", StringUtils.join(":", s.subList(0, 2)));
        Assert.assertEquals("a:b:c", StringUtils.join(":", s.subList(0, 3)));
        Assert.assertEquals("a:b", StringUtils.join(':', s.subList(0, 2)));
        Assert.assertEquals("a:b:c", StringUtils.join(':', s.subList(0, 3)));
    }

    @Test(timeout = 30000)
    public void testGetTrimmedStrings() throws Exception {
        String compactDirList = "/spindle1/hdfs,/spindle2/hdfs,/spindle3/hdfs";
        String spacedDirList = "/spindle1/hdfs, /spindle2/hdfs, /spindle3/hdfs";
        String pathologicalDirList1 = " /spindle1/hdfs  ,  /spindle2/hdfs ,/spindle3/hdfs ";
        String pathologicalDirList2 = " /spindle1/hdfs  ,  /spindle2/hdfs ,/spindle3/hdfs , ";
        String emptyList1 = "";
        String emptyList2 = "   ";
        String[] expectedArray = new String[]{ "/spindle1/hdfs", "/spindle2/hdfs", "/spindle3/hdfs" };
        String[] emptyArray = new String[]{  };
        Assert.assertArrayEquals(expectedArray, StringUtils.getTrimmedStrings(compactDirList));
        Assert.assertArrayEquals(expectedArray, StringUtils.getTrimmedStrings(spacedDirList));
        Assert.assertArrayEquals(expectedArray, StringUtils.getTrimmedStrings(pathologicalDirList1));
        Assert.assertArrayEquals(expectedArray, StringUtils.getTrimmedStrings(pathologicalDirList2));
        Assert.assertArrayEquals(emptyArray, StringUtils.getTrimmedStrings(emptyList1));
        String[] estring = StringUtils.getTrimmedStrings(emptyList2);
        Assert.assertArrayEquals(emptyArray, estring);
    }

    @Test(timeout = 30000)
    public void testCamelize() {
        // common use cases
        Assert.assertEquals("Map", StringUtils.camelize("MAP"));
        Assert.assertEquals("JobSetup", StringUtils.camelize("JOB_SETUP"));
        Assert.assertEquals("SomeStuff", StringUtils.camelize("some_stuff"));
        // sanity checks for ascii alphabet against unexpected locale issues.
        Assert.assertEquals("Aa", StringUtils.camelize("aA"));
        Assert.assertEquals("Bb", StringUtils.camelize("bB"));
        Assert.assertEquals("Cc", StringUtils.camelize("cC"));
        Assert.assertEquals("Dd", StringUtils.camelize("dD"));
        Assert.assertEquals("Ee", StringUtils.camelize("eE"));
        Assert.assertEquals("Ff", StringUtils.camelize("fF"));
        Assert.assertEquals("Gg", StringUtils.camelize("gG"));
        Assert.assertEquals("Hh", StringUtils.camelize("hH"));
        Assert.assertEquals("Ii", StringUtils.camelize("iI"));
        Assert.assertEquals("Jj", StringUtils.camelize("jJ"));
        Assert.assertEquals("Kk", StringUtils.camelize("kK"));
        Assert.assertEquals("Ll", StringUtils.camelize("lL"));
        Assert.assertEquals("Mm", StringUtils.camelize("mM"));
        Assert.assertEquals("Nn", StringUtils.camelize("nN"));
        Assert.assertEquals("Oo", StringUtils.camelize("oO"));
        Assert.assertEquals("Pp", StringUtils.camelize("pP"));
        Assert.assertEquals("Qq", StringUtils.camelize("qQ"));
        Assert.assertEquals("Rr", StringUtils.camelize("rR"));
        Assert.assertEquals("Ss", StringUtils.camelize("sS"));
        Assert.assertEquals("Tt", StringUtils.camelize("tT"));
        Assert.assertEquals("Uu", StringUtils.camelize("uU"));
        Assert.assertEquals("Vv", StringUtils.camelize("vV"));
        Assert.assertEquals("Ww", StringUtils.camelize("wW"));
        Assert.assertEquals("Xx", StringUtils.camelize("xX"));
        Assert.assertEquals("Yy", StringUtils.camelize("yY"));
        Assert.assertEquals("Zz", StringUtils.camelize("zZ"));
    }

    @Test(timeout = 30000)
    public void testStringToURI() {
        String[] str = new String[]{ "file://" };
        try {
            StringUtils.stringToURI(str);
            Assert.fail("Ignoring URISyntaxException while creating URI from string file://");
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("Failed to create uri for file://", iae.getMessage());
        }
    }

    @Test(timeout = 30000)
    public void testSimpleHostName() {
        Assert.assertEquals("Should return hostname when FQDN is specified", "hadoop01", StringUtils.simpleHostname("hadoop01.domain.com"));
        Assert.assertEquals("Should return hostname when only hostname is specified", "hadoop01", StringUtils.simpleHostname("hadoop01"));
        Assert.assertEquals("Should not truncate when IP address is passed", "10.10.5.68", StringUtils.simpleHostname("10.10.5.68"));
    }

    @Test(timeout = 5000)
    public void testReplaceTokensShellEnvVars() {
        Pattern pattern = SHELL_ENV_VAR_PATTERN;
        Map<String, String> replacements = new HashMap<String, String>();
        replacements.put("FOO", "one");
        replacements.put("BAZ", "two");
        replacements.put("NUMBERS123", "one-two-three");
        replacements.put("UNDER_SCORES", "___");
        Assert.assertEquals("one", StringUtils.replaceTokens("$FOO", pattern, replacements));
        Assert.assertEquals("two", StringUtils.replaceTokens("$BAZ", pattern, replacements));
        Assert.assertEquals("", StringUtils.replaceTokens("$BAR", pattern, replacements));
        Assert.assertEquals("", StringUtils.replaceTokens("", pattern, replacements));
        Assert.assertEquals("one-two-three", StringUtils.replaceTokens("$NUMBERS123", pattern, replacements));
        Assert.assertEquals("___", StringUtils.replaceTokens("$UNDER_SCORES", pattern, replacements));
    }

    @Test(timeout = 5000)
    public void testReplaceTokensWinEnvVars() {
        Pattern pattern = WIN_ENV_VAR_PATTERN;
        Map<String, String> replacements = new HashMap<String, String>();
        replacements.put("foo", "zoo");
        replacements.put("baz", "zaz");
        Assert.assertEquals("zoo", StringUtils.replaceTokens("%foo%", pattern, replacements));
        Assert.assertEquals("zaz", StringUtils.replaceTokens("%baz%", pattern, replacements));
        Assert.assertEquals("", StringUtils.replaceTokens("%bar%", pattern, replacements));
        Assert.assertEquals("", StringUtils.replaceTokens("", pattern, replacements));
        Assert.assertEquals("zoo__zaz", StringUtils.replaceTokens("%foo%_%bar%_%baz%", pattern, replacements));
        Assert.assertEquals("begin zoo__zaz end", StringUtils.replaceTokens("begin %foo%_%bar%_%baz% end", pattern, replacements));
    }

    @Test
    public void testGetUniqueNonEmptyTrimmedStrings() {
        final String TO_SPLIT = ",foo, bar,baz,,blah,blah,bar,";
        Collection<String> col = StringUtils.getTrimmedStringCollection(TO_SPLIT);
        Assert.assertEquals(4, col.size());
        Assert.assertTrue(col.containsAll(Arrays.asList(new String[]{ "foo", "bar", "baz", "blah" })));
    }

    @Test
    public void testLowerAndUpperStrings() {
        Locale defaultLocale = Locale.getDefault();
        try {
            Locale.setDefault(new Locale("tr", "TR"));
            String upperStr = "TITLE";
            String lowerStr = "title";
            // Confirming TR locale.
            Assert.assertNotEquals(lowerStr, upperStr.toLowerCase());
            Assert.assertNotEquals(upperStr, lowerStr.toUpperCase());
            // This should be true regardless of locale.
            Assert.assertEquals(lowerStr, StringUtils.toLowerCase(upperStr));
            Assert.assertEquals(upperStr, StringUtils.toUpperCase(lowerStr));
            Assert.assertTrue(StringUtils.equalsIgnoreCase(upperStr, lowerStr));
        } finally {
            Locale.setDefault(defaultLocale);
        }
    }

    // Multithreaded Test GetFormattedTimeWithDiff()
    @Test
    public void testGetFormattedTimeWithDiff() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(16);
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(10);
        for (int i = 0; i < 10; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        cyclicBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        // Ignored
                    }
                    final long end = System.currentTimeMillis();
                    final long start = end - 30000;
                    String formattedTime1 = StringUtils.getFormattedTimeWithDiff(TestStringUtils.FAST_DATE_FORMAT, start, end);
                    String formattedTime2 = StringUtils.getFormattedTimeWithDiff(TestStringUtils.FAST_DATE_FORMAT, start, end);
                    Assert.assertTrue(("Method returned inconsistent results indicative of" + " a race condition"), formattedTime1.equals(formattedTime2));
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(50, TimeUnit.SECONDS);
    }

    @Test
    public void testFormatTimeSortable() {
        long timeDiff = 523452311;
        String timeDiffStr = "99hrs, 59mins, 59sec";
        Assert.assertEquals("Incorrect time diff string returned", timeDiffStr, StringUtils.formatTimeSortable(timeDiff));
    }

    @Test
    public void testIsAlpha() {
        Assert.assertTrue("Reported hello as non-alpha string", StringUtils.isAlpha("hello"));
        Assert.assertFalse("Reported hello1 as alpha string", StringUtils.isAlpha("hello1"));
    }

    @Test
    public void testEscapeHTML() {
        String htmlStr = "<p>Hello. How are you?</p>";
        String escapedStr = "&lt;p&gt;Hello. How are you?&lt;/p&gt;";
        Assert.assertEquals("Incorrect escaped HTML string returned", escapedStr, StringUtils.escapeHTML(htmlStr));
    }

    @Test
    public void testCreateStartupShutdownMessage() {
        // pass null args and method must still return a string beginning with
        // "STARTUP_MSG"
        String msg = StringUtils.createStartupShutdownMessage(this.getClass().getName(), "test.host", null);
        Assert.assertTrue(msg.startsWith("STARTUP_MSG:"));
    }
}

