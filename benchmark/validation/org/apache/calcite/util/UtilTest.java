/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.util;


import CalciteSqlDialect.DEFAULT;
import Litmus.IGNORE;
import Litmus.THROW;
import Spaces.MAX;
import Static.RESOURCE;
import TryThreadLocal.Memo;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.management.MemoryType;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import java.util.RandomAccess;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.function.Function;
import org.apache.calcite.avatica.AvaticaUtils;
import org.apache.calcite.avatica.util.Spaces;
import org.apache.calcite.examples.RelBuilderExample;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.linq4j.Linq4j;
import org.apache.calcite.linq4j.Ord;
import org.apache.calcite.runtime.ConsList;
import org.apache.calcite.runtime.FlatLists;
import org.apache.calcite.runtime.Resources;
import org.apache.calcite.runtime.SqlFunctions;
import org.apache.calcite.sql.SqlCollation;
import org.apache.calcite.sql.dialect.CalciteSqlDialect;
import org.apache.calcite.sql.util.SqlBuilder;
import org.apache.calcite.sql.util.SqlString;
import org.apache.calcite.test.DiffTestCase;
import org.apache.calcite.test.Matchers;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link Util} and other classes in this package.
 */
public class UtilTest {
    // ~ Constructors -----------------------------------------------------------
    public UtilTest() {
    }

    @Test
    public void testPrintEquals() {
        assertPrintEquals("\"x\"", "x", true);
    }

    @Test
    public void testPrintEquals2() {
        assertPrintEquals("\"x\"", "x", false);
    }

    @Test
    public void testPrintEquals3() {
        assertPrintEquals("null", null, true);
    }

    @Test
    public void testPrintEquals4() {
        assertPrintEquals("", null, false);
    }

    @Test
    public void testPrintEquals5() {
        assertPrintEquals("\"\\\\\\\"\\r\\n\"", "\\\"\r\n", true);
    }

    @Test
    public void testScientificNotation() {
        BigDecimal bd;
        bd = new BigDecimal("0.001234");
        TestUtil.assertEqualsVerbose("1.234E-3", Util.toScientificNotation(bd));
        bd = new BigDecimal("0.001");
        TestUtil.assertEqualsVerbose("1E-3", Util.toScientificNotation(bd));
        bd = new BigDecimal("-0.001");
        TestUtil.assertEqualsVerbose("-1E-3", Util.toScientificNotation(bd));
        bd = new BigDecimal("1");
        TestUtil.assertEqualsVerbose("1E0", Util.toScientificNotation(bd));
        bd = new BigDecimal("-1");
        TestUtil.assertEqualsVerbose("-1E0", Util.toScientificNotation(bd));
        bd = new BigDecimal("1.0");
        TestUtil.assertEqualsVerbose("1.0E0", Util.toScientificNotation(bd));
        bd = new BigDecimal("12345");
        TestUtil.assertEqualsVerbose("1.2345E4", Util.toScientificNotation(bd));
        bd = new BigDecimal("12345.00");
        TestUtil.assertEqualsVerbose("1.234500E4", Util.toScientificNotation(bd));
        bd = new BigDecimal("12345.001");
        TestUtil.assertEqualsVerbose("1.2345001E4", Util.toScientificNotation(bd));
        // test truncate
        bd = new BigDecimal("1.23456789012345678901");
        TestUtil.assertEqualsVerbose("1.2345678901234567890E0", Util.toScientificNotation(bd));
        bd = new BigDecimal("-1.23456789012345678901");
        TestUtil.assertEqualsVerbose("-1.2345678901234567890E0", Util.toScientificNotation(bd));
    }

    @Test
    public void testToJavaId() throws UnsupportedEncodingException {
        Assert.assertEquals("ID$0$foo", Util.toJavaId("foo", 0));
        Assert.assertEquals("ID$0$foo_20_bar", Util.toJavaId("foo bar", 0));
        Assert.assertEquals("ID$0$foo__bar", Util.toJavaId("foo_bar", 0));
        Assert.assertEquals("ID$100$_30_bar", Util.toJavaId("0bar", 100));
        Assert.assertEquals("ID$0$foo0bar", Util.toJavaId("foo0bar", 0));
        Assert.assertEquals("ID$0$it_27_s_20_a_20_bird_2c__20_it_27_s_20_a_20_plane_21_", Util.toJavaId("it's a bird, it's a plane!", 0));
        // Try some funny non-ASCII charsets
        Assert.assertEquals("ID$0$_f6__cb__c4__ca__ae__c1__f9__cb_", Util.toJavaId("\u00f6\u00cb\u00c4\u00ca\u00ae\u00c1\u00f9\u00cb", 0));
        Assert.assertEquals("ID$0$_f6cb__c4ca__aec1__f9cb_", Util.toJavaId("\uf6cb\uc4ca\uaec1\uf9cb", 0));
        byte[] bytes1 = new byte[]{ 3, 12, 54, 23, 33, 23, 45, 21, 127, -34, -92, -113 };
        Assert.assertEquals("ID$0$_3__c_6_17__21__17__2d__15__7f__6cd9__fffd_", // CHECKSTYLE: IGNORE 0
        Util.toJavaId(new String(bytes1, "EUC-JP"), 0));
        byte[] bytes2 = new byte[]{ 64, 32, 43, -45, -23, 0, 43, 54, 119, -32, -56, -34 };
        Assert.assertEquals("ID$0$_30c__3617__2117__2d15__7fde__a48f_", // CHECKSTYLE: IGNORE 0
        Util.toJavaId(new String(bytes1, "UTF-16"), 0));
    }

    /**
     * Unit-test for {@link Util#tokenize(String, String)}.
     */
    @Test
    public void testTokenize() {
        final List<String> list = new ArrayList<>();
        for (String s : Util.tokenize("abc,de,f", ",")) {
            list.add(s);
        }
        Assert.assertThat(list.size(), CoreMatchers.is(3));
        Assert.assertThat(list.toString(), CoreMatchers.is("[abc, de, f]"));
    }

    /**
     * Unit-test for {@link BitString}.
     */
    @Test
    public void testBitString() {
        // Powers of two, minimal length.
        final BitString b0 = new BitString("", 0);
        final BitString b1 = new BitString("1", 1);
        final BitString b2 = new BitString("10", 2);
        final BitString b4 = new BitString("100", 3);
        final BitString b8 = new BitString("1000", 4);
        final BitString b16 = new BitString("10000", 5);
        final BitString b32 = new BitString("100000", 6);
        final BitString b64 = new BitString("1000000", 7);
        final BitString b128 = new BitString("10000000", 8);
        final BitString b256 = new BitString("100000000", 9);
        // other strings
        final BitString b0x1 = new BitString("", 1);
        final BitString b0x12 = new BitString("", 12);
        // conversion to hex strings
        Assert.assertEquals("", b0.toHexString());
        Assert.assertEquals("1", b1.toHexString());
        Assert.assertEquals("2", b2.toHexString());
        Assert.assertEquals("4", b4.toHexString());
        Assert.assertEquals("8", b8.toHexString());
        Assert.assertEquals("10", b16.toHexString());
        Assert.assertEquals("20", b32.toHexString());
        Assert.assertEquals("40", b64.toHexString());
        Assert.assertEquals("80", b128.toHexString());
        Assert.assertEquals("100", b256.toHexString());
        Assert.assertEquals("0", b0x1.toHexString());
        Assert.assertEquals("000", b0x12.toHexString());
        // to byte array
        assertByteArray("01", "1", 1);
        assertByteArray("01", "1", 5);
        assertByteArray("01", "1", 8);
        assertByteArray("00, 01", "1", 9);
        assertByteArray("", "", 0);
        assertByteArray("00", "0", 1);
        assertByteArray("00", "0000", 2);// bit count less than string

        assertByteArray("00", "000", 5);// bit count larger than string

        assertByteArray("00", "0", 8);// precisely 1 byte

        assertByteArray("00, 00", "00", 9);// just over 1 byte

        // from hex string
        UtilTest.assertReversible("");
        UtilTest.assertReversible("1");
        UtilTest.assertReversible("10");
        UtilTest.assertReversible("100");
        UtilTest.assertReversible("1000");
        UtilTest.assertReversible("10000");
        UtilTest.assertReversible("100000");
        UtilTest.assertReversible("1000000");
        UtilTest.assertReversible("10000000");
        UtilTest.assertReversible("100000000");
        UtilTest.assertReversible("01");
        UtilTest.assertReversible("001010");
        UtilTest.assertReversible("000000000100");
    }

    /**
     * Tests {@link org.apache.calcite.util.CastingList} and {@link Util#cast}.
     */
    @Test
    public void testCastingList() {
        final List<Number> numberList = new ArrayList<>();
        numberList.add(1);
        numberList.add(null);
        numberList.add(2);
        List<Integer> integerList = Util.cast(numberList, Integer.class);
        Assert.assertEquals(3, integerList.size());
        Assert.assertEquals(Integer.valueOf(2), integerList.get(2));
        // Nulls are OK.
        Assert.assertNull(integerList.get(1));
        // Can update the underlying list.
        integerList.set(1, 345);
        Assert.assertEquals(Integer.valueOf(345), integerList.get(1));
        integerList.set(1, null);
        Assert.assertNull(integerList.get(1));
        // Can add a member of the wrong type to the underlying list.
        numberList.add(3.1415);
        Assert.assertEquals(4, integerList.size());
        // Access a member which is of the wrong type.
        try {
            integerList.get(3);
            Assert.fail("expected exception");
        } catch (ClassCastException e) {
            // ok
        }
    }

    @Test
    public void testCons() {
        final List<String> abc0 = Arrays.asList("a", "b", "c");
        final List<String> abc = ConsList.of("a", ImmutableList.of("b", "c"));
        Assert.assertThat(abc.size(), CoreMatchers.is(3));
        Assert.assertThat(abc, CoreMatchers.is(abc0));
        final List<String> bc = Lists.newArrayList("b", "c");
        final List<String> abc2 = ConsList.of("a", bc);
        Assert.assertThat(abc2.size(), CoreMatchers.is(3));
        Assert.assertThat(abc2, CoreMatchers.is(abc0));
        bc.set(0, "z");
        Assert.assertThat(abc2, CoreMatchers.is(abc0));
        final List<String> bc3 = ConsList.of("b", Collections.singletonList("c"));
        final List<String> abc3 = ConsList.of("a", bc3);
        Assert.assertThat(abc3.size(), CoreMatchers.is(3));
        Assert.assertThat(abc3, CoreMatchers.is(abc0));
        Assert.assertThat(abc3.indexOf("b"), CoreMatchers.is(1));
        Assert.assertThat(abc3.indexOf("z"), CoreMatchers.is((-1)));
        Assert.assertThat(abc3.lastIndexOf("b"), CoreMatchers.is(1));
        Assert.assertThat(abc3.lastIndexOf("z"), CoreMatchers.is((-1)));
        Assert.assertThat(abc3.hashCode(), CoreMatchers.is(abc0.hashCode()));
        Assert.assertThat(abc3.get(0), CoreMatchers.is("a"));
        Assert.assertThat(abc3.get(1), CoreMatchers.is("b"));
        Assert.assertThat(abc3.get(2), CoreMatchers.is("c"));
        try {
            final String z = abc3.get(3);
            Assert.fail(("expected error, got " + z));
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        try {
            final String z = abc3.get((-3));
            Assert.fail(("expected error, got " + z));
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        try {
            final String z = abc3.get(30);
            Assert.fail(("expected error, got " + z));
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        final List<String> a = ConsList.of("a", ImmutableList.of());
        Assert.assertThat(a.size(), CoreMatchers.is(1));
        Assert.assertThat(a, CoreMatchers.is(Collections.singletonList("a")));
    }

    @Test
    public void testConsPerformance() {
        final int n = 2000000;
        final int start = 10;
        List<Integer> list = makeConsList(start, (n + start));
        Assert.assertThat(list.size(), CoreMatchers.is(n));
        Assert.assertThat(list.toString(), CoreMatchers.startsWith("[10, 11, 12, "));
        Assert.assertThat(list.contains(((n / 2) + start)), CoreMatchers.is(true));
        Assert.assertThat(list.contains(((n * 2) + start)), CoreMatchers.is(false));
        Assert.assertThat(list.indexOf(((n / 2) + start)), CoreMatchers.is((n / 2)));
        Assert.assertThat(list.containsAll(Arrays.asList((n - 1), (n - 10), (n / 2), start)), CoreMatchers.is(true));
        long total = 0;
        for (Integer i : list) {
            total += i - start;
        }
        Assert.assertThat(total, CoreMatchers.is(((((long) (n)) * (n - 1)) / 2)));
        final Object[] objects = list.toArray();
        Assert.assertThat(objects.length, CoreMatchers.is(n));
        final Integer[] integers = new Integer[n - 1];
        Assert.assertThat(integers.length, CoreMatchers.is((n - 1)));
        final Integer[] integers2 = list.toArray(integers);
        Assert.assertThat(integers2.length, CoreMatchers.is(n));
        Assert.assertThat(integers2[0], CoreMatchers.is(start));
        Assert.assertThat(integers2[((integers2.length) - 1)], CoreMatchers.is(((n + start) - 1)));
        final Integer[] integers3 = list.toArray(integers2);
        Assert.assertThat(integers2, CoreMatchers.sameInstance(integers3));
        final Integer[] integers4 = new Integer[n + 1];
        final Integer[] integers5 = list.toArray(integers4);
        Assert.assertThat(integers5, CoreMatchers.sameInstance(integers4));
        Assert.assertThat(integers5.length, CoreMatchers.is((n + 1)));
        Assert.assertThat(integers5[0], CoreMatchers.is(start));
        Assert.assertThat(integers5[(n - 1)], CoreMatchers.is(((n + start) - 1)));
        Assert.assertThat(integers5[n], CoreMatchers.nullValue());
        Assert.assertThat(list.hashCode(), CoreMatchers.is(Arrays.hashCode(integers3)));
        Assert.assertThat(list, CoreMatchers.is(Arrays.asList(integers3)));
        Assert.assertThat(list, CoreMatchers.is(list));
        Assert.assertThat(Arrays.asList(integers3), CoreMatchers.is(list));
    }

    @Test
    public void testIterableProperties() {
        Properties properties = new Properties();
        properties.put("foo", "george");
        properties.put("bar", "ringo");
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : Util.toMap(properties).entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            sb.append(";");
        }
        Assert.assertEquals("bar=ringo;foo=george;", sb.toString());
        Assert.assertEquals(2, Util.toMap(properties).entrySet().size());
        properties.put("nonString", 34);
        try {
            for (Map.Entry<String, String> e : Util.toMap(properties).entrySet()) {
                String s = e.getValue();
                Util.discard(s);
            }
            Assert.fail("expected exception");
        } catch (ClassCastException e) {
            // ok
        }
    }

    /**
     * Tests the difference engine, {@link DiffTestCase#diff}.
     */
    @Test
    public void testDiffLines() {
        String[] before = new String[]{ "Get a dose of her in jackboots and kilt", "She's killer-diller when she's dressed to the hilt", "She's the kind of a girl that makes The News of The World", "Yes you could say she was attractively built.", "Yeah yeah yeah." };
        String[] after = new String[]{ "Get a dose of her in jackboots and kilt", "(they call her \"Polythene Pam\")", "She's killer-diller when she's dressed to the hilt", "She's the kind of a girl that makes The Sunday Times", "seem more interesting.", "Yes you could say she was attractively built." };
        String diff = DiffTestCase.diffLines(Arrays.asList(before), Arrays.asList(after));
        Assert.assertThat(Util.toLinux(diff), CoreMatchers.equalTo(("1a2\n" + ((((((("> (they call her \"Polythene Pam\")\n" + "3c4,5\n") + "< She\'s the kind of a girl that makes The News of The World\n") + "---\n") + "> She\'s the kind of a girl that makes The Sunday Times\n") + "> seem more interesting.\n") + "5d6\n") + "< Yeah yeah yeah.\n"))));
    }

    /**
     * Tests the {@link Util#toPosix(TimeZone, boolean)} method.
     */
    @Test
    public void testPosixTimeZone() {
        // NOTE jvs 31-July-2007:  First two tests are disabled since
        // not everyone may have patched their system yet for recent
        // DST change.
        // Pacific Standard Time. Effective 2007, the local time changes from
        // PST to PDT at 02:00 LST to 03:00 LDT on the second Sunday in March
        // and returns at 02:00 LDT to 01:00 LST on the first Sunday in
        // November.
        if (false) {
            Assert.assertEquals("PST-8PDT,M3.2.0,M11.1.0", Util.toPosix(TimeZone.getTimeZone("PST"), false));
            Assert.assertEquals("PST-8PDT1,M3.2.0/2,M11.1.0/2", Util.toPosix(TimeZone.getTimeZone("PST"), true));
        }
        // Tokyo has +ve offset, no DST
        Assert.assertEquals("JST9", Util.toPosix(TimeZone.getTimeZone("Asia/Tokyo"), true));
        // Sydney, Australia lies ten hours east of GMT and makes a one hour
        // shift forward during daylight savings. Being located in the southern
        // hemisphere, daylight savings begins on the last Sunday in October at
        // 2am and ends on the last Sunday in March at 3am.
        // (Uses STANDARD_TIME time-transition mode.)
        // Because australia changed their daylight savings rules, some JVMs
        // have a different (older and incorrect) timezone settings for
        // Australia.  So we test for the older one first then do the
        // correct assert based upon what the toPosix method returns
        String posixTime = Util.toPosix(TimeZone.getTimeZone("Australia/Sydney"), true);
        if (posixTime.equals("EST10EST1,M10.5.0/2,M3.5.0/3")) {
            // very old JVMs without the fix
            Assert.assertEquals("EST10EST1,M10.5.0/2,M3.5.0/3", posixTime);
        } else
            if (posixTime.equals("EST10EST1,M10.1.0/2,M4.1.0/3")) {
                // old JVMs without the fix
                Assert.assertEquals("EST10EST1,M10.1.0/2,M4.1.0/3", posixTime);
            } else {
                // newer JVMs with the fix
                Assert.assertEquals("AEST10AEDT1,M10.1.0/2,M4.1.0/3", posixTime);
            }

        // Paris, France. (Uses UTC_TIME time-transition mode.)
        Assert.assertEquals("CET1CEST1,M3.5.0/2,M10.5.0/3", Util.toPosix(TimeZone.getTimeZone("Europe/Paris"), true));
        Assert.assertEquals("UTC0", Util.toPosix(TimeZone.getTimeZone("UTC"), true));
    }

    /**
     * Tests the methods {@link Util#enumConstants(Class)} and
     * {@link Util#enumVal(Class, String)}.
     */
    @Test
    public void testEnumConstants() {
        final Map<String, MemoryType> memoryTypeMap = Util.enumConstants(MemoryType.class);
        Assert.assertEquals(2, memoryTypeMap.size());
        Assert.assertEquals(MemoryType.HEAP, memoryTypeMap.get("HEAP"));
        Assert.assertEquals(MemoryType.NON_HEAP, memoryTypeMap.get("NON_HEAP"));
        try {
            memoryTypeMap.put("FOO", null);
            Assert.fail("expected exception");
        } catch (UnsupportedOperationException e) {
            // expected: map is immutable
        }
        Assert.assertEquals("HEAP", Util.enumVal(MemoryType.class, "HEAP").name());
        Assert.assertNull(Util.enumVal(MemoryType.class, "heap"));
        Assert.assertNull(Util.enumVal(MemoryType.class, "nonexistent"));
    }

    /**
     * Tests SQL builders.
     */
    @Test
    public void testSqlBuilder() {
        final SqlBuilder buf = new SqlBuilder(CalciteSqlDialect.DEFAULT);
        Assert.assertEquals(0, buf.length());
        buf.append("select ");
        Assert.assertEquals("select ", buf.getSql());
        buf.identifier("x");
        Assert.assertEquals("select \"x\"", buf.getSql());
        buf.append(", ");
        buf.identifier("y", "a b");
        Assert.assertEquals("select \"x\", \"y\".\"a b\"", buf.getSql());
        final SqlString sqlString = buf.toSqlString();
        Assert.assertEquals(DEFAULT, sqlString.getDialect());
        Assert.assertEquals(buf.getSql(), sqlString.getSql());
        Assert.assertTrue(((buf.getSql().length()) > 0));
        Assert.assertEquals(buf.getSqlAndClear(), sqlString.getSql());
        Assert.assertEquals(0, buf.length());
        buf.clear();
        Assert.assertEquals(0, buf.length());
        buf.literal("can't get no satisfaction");
        Assert.assertEquals("'can''t get no satisfaction'", buf.getSqlAndClear());
        buf.literal(new Timestamp(0));
        Assert.assertEquals("TIMESTAMP '1970-01-01 00:00:00'", buf.getSqlAndClear());
        buf.clear();
        Assert.assertEquals(0, buf.length());
        buf.append("hello world");
        Assert.assertEquals(2, buf.indexOf("l"));
        Assert.assertEquals((-1), buf.indexOf("z"));
        Assert.assertEquals(9, buf.indexOf("l", 5));
    }

    /**
     * Unit test for {@link org.apache.calcite.util.CompositeList}.
     */
    @Test
    public void testCompositeList() {
        // Made up of zero lists
        // noinspection unchecked
        List<String> list = CompositeList.of(new List[0]);
        Assert.assertEquals(0, list.size());
        Assert.assertTrue(list.isEmpty());
        try {
            final String s = list.get(0);
            Assert.fail(("expected error, got " + s));
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        Assert.assertFalse(list.listIterator().hasNext());
        List<String> listEmpty = Collections.emptyList();
        List<String> listAbc = Arrays.asList("a", "b", "c");
        List<String> listEmpty2 = new ArrayList<>();
        // Made up of three lists, two of which are empty
        list = CompositeList.of(listEmpty, listAbc, listEmpty2);
        Assert.assertEquals(3, list.size());
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals("a", list.get(0));
        Assert.assertEquals("c", list.get(2));
        try {
            final String s = list.get(3);
            Assert.fail(("expected error, got " + s));
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        try {
            final String s = list.set(0, "z");
            Assert.fail(("expected error, got " + s));
        } catch (UnsupportedOperationException e) {
            // ok
        }
        // Iterator
        final Iterator<String> iterator = list.iterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals("a", iterator.next());
        Assert.assertEquals("b", iterator.next());
        Assert.assertTrue(iterator.hasNext());
        try {
            iterator.remove();
            Assert.fail("expected error");
        } catch (UnsupportedOperationException e) {
            // ok
        }
        Assert.assertEquals("c", iterator.next());
        Assert.assertFalse(iterator.hasNext());
        // Extend one of the backing lists, and list grows.
        listEmpty2.add("zz");
        Assert.assertEquals(4, list.size());
        Assert.assertEquals("zz", list.get(3));
        // Syntactic sugar 'of' method
        String ss = "";
        for (String s : CompositeList.of(list, list)) {
            ss += s;
        }
        Assert.assertEquals("abczzabczz", ss);
    }

    /**
     * Unit test for {@link Template}.
     */
    @Test
    public void testTemplate() {
        // Regular java message format.
        Assert.assertThat(new MessageFormat("Hello, {0}, what a nice {1}.", Locale.ROOT).format(new Object[]{ "world", "day" }), CoreMatchers.is("Hello, world, what a nice day."));
        // Our extended message format. First, just strings.
        final HashMap<Object, Object> map = new HashMap<>();
        map.put("person", "world");
        map.put("time", "day");
        Assert.assertEquals("Hello, world, what a nice day.", Template.formatByName("Hello, {person}, what a nice {time}.", map));
        // String and an integer.
        final Template template = Template.of("Happy {age,number,#.00}th birthday, {person}!");
        map.clear();
        map.put("person", "Ringo");
        map.put("age", 64.5);
        Assert.assertEquals("Happy 64.50th birthday, Ringo!", template.format(map));
        // Missing parameters evaluate to null.
        map.remove("person");
        Assert.assertEquals("Happy 64.50th birthday, null!", template.format(map));
        // Specify parameter by Integer ordinal.
        map.clear();
        map.put(1, "Ringo");
        map.put("0", 64.5);
        Assert.assertEquals("Happy 64.50th birthday, Ringo!", template.format(map));
        // Too many parameters supplied.
        map.put("lastName", "Starr");
        map.put("homeTown", "Liverpool");
        Assert.assertEquals("Happy 64.50th birthday, Ringo!", template.format(map));
        // Get parameter names. In order of appearance.
        Assert.assertEquals(Arrays.asList("age", "person"), template.getParameterNames());
        // No parameters; doubled single quotes; quoted braces.
        final Template template2 = Template.of("Don''t expand 'this {brace}'.");
        Assert.assertEquals(Collections.<String>emptyList(), template2.getParameterNames());
        Assert.assertEquals("Don't expand this {brace}.", template2.format(Collections.emptyMap()));
        // Empty template.
        Assert.assertEquals("", Template.formatByName("", map));
    }

    /**
     * Unit test for {@link Util#parseLocale(String)} method.
     */
    @Test
    public void testParseLocale() {
        Locale[] locales = new Locale[]{ Locale.CANADA, Locale.CANADA_FRENCH, Locale.getDefault(), Locale.US, Locale.TRADITIONAL_CHINESE, Locale.ROOT };
        for (Locale locale : locales) {
            Assert.assertEquals(locale, Util.parseLocale(locale.toString()));
        }
        // Example locale names in Locale.toString() javadoc.
        String[] localeNames = new String[]{ "en", "de_DE", "_GB", "en_US_WIN", "de__POSIX", "fr__MAC" };
        for (String localeName : localeNames) {
            Assert.assertEquals(localeName, Util.parseLocale(localeName).toString());
        }
    }

    @Test
    public void testSpaces() {
        Assert.assertEquals("", Spaces.of(0));
        Assert.assertEquals(" ", Spaces.of(1));
        Assert.assertEquals(" ", Spaces.of(1));
        Assert.assertEquals("         ", Spaces.of(9));
        Assert.assertEquals("     ", Spaces.of(5));
        Assert.assertEquals(1000, Spaces.of(1000).length());
    }

    @Test
    public void testSpaceString() {
        Assert.assertThat(Spaces.sequence(0).toString(), CoreMatchers.equalTo(""));
        Assert.assertThat(Spaces.sequence(1).toString(), CoreMatchers.equalTo(" "));
        Assert.assertThat(Spaces.sequence(9).toString(), CoreMatchers.equalTo("         "));
        Assert.assertThat(Spaces.sequence(5).toString(), CoreMatchers.equalTo("     "));
        String s = new StringBuilder().append("xx").append(MAX, 0, 100).toString();
        Assert.assertThat(s.length(), CoreMatchers.equalTo(102));
        // this would blow memory if the string were materialized... check that it
        // is not
        Assert.assertThat(Spaces.sequence(1000000000).length(), CoreMatchers.equalTo(1000000000));
        final StringWriter sw = new StringWriter();
        Spaces.append(sw, 4);
        Assert.assertThat(sw.toString(), CoreMatchers.equalTo("    "));
        final StringBuilder buf = new StringBuilder();
        Spaces.append(buf, 4);
        Assert.assertThat(buf.toString(), CoreMatchers.equalTo("    "));
        Assert.assertThat(Spaces.padLeft("xy", 5), CoreMatchers.equalTo("   xy"));
        Assert.assertThat(Spaces.padLeft("abcde", 5), CoreMatchers.equalTo("abcde"));
        Assert.assertThat(Spaces.padLeft("abcdef", 5), CoreMatchers.equalTo("abcdef"));
        Assert.assertThat(Spaces.padRight("xy", 5), CoreMatchers.equalTo("xy   "));
        Assert.assertThat(Spaces.padRight("abcde", 5), CoreMatchers.equalTo("abcde"));
        Assert.assertThat(Spaces.padRight("abcdef", 5), CoreMatchers.equalTo("abcdef"));
    }

    /**
     * Unit test for {@link Pair#zip(java.util.List, java.util.List)}.
     */
    @Test
    public void testPairZip() {
        List<String> strings = Arrays.asList("paul", "george", "john", "ringo");
        List<Integer> integers = Arrays.asList(1942, 1943, 1940);
        List<Pair<String, Integer>> zip = Pair.zip(strings, integers);
        Assert.assertEquals(3, zip.size());
        Assert.assertEquals("paul:1942", (((zip.get(0).left) + ":") + (zip.get(0).right)));
        Assert.assertEquals("john", zip.get(2).left);
        int x = 0;
        for (Pair<String, Integer> pair : zip) {
            x += pair.right;
        }
        Assert.assertEquals(5825, x);
    }

    /**
     * Unit test for {@link Pair#adjacents(Iterable)}.
     */
    @Test
    public void testPairAdjacents() {
        List<String> strings = Arrays.asList("a", "b", "c");
        List<String> result = new ArrayList<>();
        for (Pair<String, String> pair : Pair.adjacents(strings)) {
            result.add(pair.toString());
        }
        Assert.assertThat(result.toString(), CoreMatchers.equalTo("[<a, b>, <b, c>]"));
        // empty source yields empty result
        Assert.assertThat(Pair.adjacents(ImmutableList.of()).iterator().hasNext(), CoreMatchers.is(false));
        // source with 1 element yields empty result
        Assert.assertThat(Pair.adjacents(ImmutableList.of("a")).iterator().hasNext(), CoreMatchers.is(false));
        // source with 100 elements yields result with 99 elements;
        // null elements are ok
        Assert.assertThat(Iterables.size(Pair.adjacents(Collections.nCopies(100, null))), CoreMatchers.equalTo(99));
    }

    /**
     * Unit test for {@link Pair#firstAnd(Iterable)}.
     */
    @Test
    public void testPairFirstAnd() {
        List<String> strings = Arrays.asList("a", "b", "c");
        List<String> result = new ArrayList<>();
        for (Pair<String, String> pair : Pair.firstAnd(strings)) {
            result.add(pair.toString());
        }
        Assert.assertThat(result.toString(), CoreMatchers.equalTo("[<a, b>, <a, c>]"));
        // empty source yields empty result
        Assert.assertThat(Pair.firstAnd(ImmutableList.of()).iterator().hasNext(), CoreMatchers.is(false));
        // source with 1 element yields empty result
        Assert.assertThat(Pair.firstAnd(ImmutableList.of("a")).iterator().hasNext(), CoreMatchers.is(false));
        // source with 100 elements yields result with 99 elements;
        // null elements are ok
        Assert.assertThat(Iterables.size(Pair.firstAnd(Collections.nCopies(100, null))), CoreMatchers.equalTo(99));
    }

    /**
     * Unit test for {@link Util#quotientList(java.util.List, int, int)}
     * and {@link Util#pairs(List)}.
     */
    @Test
    public void testQuotientList() {
        List<String> beatles = Arrays.asList("john", "paul", "george", "ringo");
        final List list0 = Util.quotientList(beatles, 3, 0);
        Assert.assertEquals(2, list0.size());
        Assert.assertEquals("john", list0.get(0));
        Assert.assertEquals("ringo", list0.get(1));
        final List list1 = Util.quotientList(beatles, 3, 1);
        Assert.assertEquals(1, list1.size());
        Assert.assertEquals("paul", list1.get(0));
        final List list2 = Util.quotientList(beatles, 3, 2);
        Assert.assertEquals(1, list2.size());
        Assert.assertEquals("george", list2.get(0));
        try {
            final List listBad = Util.quotientList(beatles, 3, 4);
            Assert.fail(("Expected error, got " + listBad));
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            final List listBad = Util.quotientList(beatles, 3, 3);
            Assert.fail(("Expected error, got " + listBad));
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            final List listBad = Util.quotientList(beatles, 0, 0);
            Assert.fail(("Expected error, got " + listBad));
        } catch (IllegalArgumentException e) {
            // ok
        }
        // empty
        final List<String> empty = Collections.emptyList();
        final List<String> list3 = Util.quotientList(empty, 7, 2);
        Assert.assertEquals(0, list3.size());
        // shorter than n
        final List list4 = Util.quotientList(beatles, 10, 0);
        Assert.assertEquals(1, list4.size());
        Assert.assertEquals("john", list4.get(0));
        final List list5 = Util.quotientList(beatles, 10, 5);
        Assert.assertEquals(0, list5.size());
        final List<Pair<String, String>> list6 = Util.pairs(beatles);
        Assert.assertThat(list6.size(), CoreMatchers.is(2));
        Assert.assertThat(list6.get(0).left, CoreMatchers.is("john"));
        Assert.assertThat(list6.get(0).right, CoreMatchers.is("paul"));
        Assert.assertThat(list6.get(1).left, CoreMatchers.is("george"));
        Assert.assertThat(list6.get(1).right, CoreMatchers.is("ringo"));
        final List<Pair<String, String>> list7 = Util.pairs(empty);
        Assert.assertThat(list7.size(), CoreMatchers.is(0));
    }

    @Test
    public void testImmutableIntList() {
        final ImmutableIntList list = ImmutableIntList.of();
        Assert.assertEquals(0, list.size());
        Assert.assertEquals(list, Collections.<Integer>emptyList());
        Assert.assertThat(list.toString(), CoreMatchers.equalTo("[]"));
        Assert.assertThat(BitSets.of(list), CoreMatchers.equalTo(new BitSet()));
        final ImmutableIntList list2 = ImmutableIntList.of(1, 3, 5);
        Assert.assertEquals(3, list2.size());
        Assert.assertEquals("[1, 3, 5]", list2.toString());
        Assert.assertEquals(list2.hashCode(), Arrays.asList(1, 3, 5).hashCode());
        Integer[] integers = list2.toArray(new Integer[3]);
        Assert.assertEquals(1, ((int) (integers[0])));
        Assert.assertEquals(3, ((int) (integers[1])));
        Assert.assertEquals(5, ((int) (integers[2])));
        // noinspection EqualsWithItself
        Assert.assertThat(list.equals(list), CoreMatchers.is(true));
        Assert.assertThat(list.equals(list2), CoreMatchers.is(false));
        Assert.assertThat(list2.equals(list), CoreMatchers.is(false));
        // noinspection EqualsWithItself
        Assert.assertThat(list2.equals(list2), CoreMatchers.is(true));
        Assert.assertThat(list2.appendAll(Collections.emptyList()), CoreMatchers.sameInstance(list2));
        Assert.assertThat(list2.appendAll(list), CoreMatchers.sameInstance(list2));
        // noinspection CollectionAddedToSelf
        Assert.assertThat(list2.appendAll(list2), CoreMatchers.is(Arrays.asList(1, 3, 5, 1, 3, 5)));
    }

    /**
     * Unit test for {@link IntegerIntervalSet}.
     */
    @Test
    public void testIntegerIntervalSet() {
        checkIntegerIntervalSet("1,5", 1, 5);
        // empty
        checkIntegerIntervalSet("");
        // empty due to exclusions
        checkIntegerIntervalSet("2,4,-1-5");
        // open range
        checkIntegerIntervalSet("1-6,-3-5,4,9", 1, 2, 4, 6, 9);
        // repeats
        checkIntegerIntervalSet("1,3,1,2-4,-2,-4", 1, 3);
    }

    /**
     * Tests that flat lists behave like regular lists in terms of equals
     * and hashCode.
     */
    @Test
    public void testFlatList() {
        final List<String> emp = FlatLists.of();
        final List<String> emp0 = Collections.emptyList();
        Assert.assertEquals(emp, emp0);
        Assert.assertEquals(emp.hashCode(), emp0.hashCode());
        final List<String> ab = FlatLists.of("A", "B");
        final List<String> ab0 = Arrays.asList("A", "B");
        Assert.assertEquals(ab, ab0);
        Assert.assertEquals(ab.hashCode(), ab0.hashCode());
        final List<String> abc = FlatLists.of("A", "B", "C");
        final List<String> abc0 = Arrays.asList("A", "B", "C");
        Assert.assertEquals(abc, abc0);
        Assert.assertEquals(abc.hashCode(), abc0.hashCode());
        final List<Object> abc1 = FlatLists.of(((Object) ("A")), "B", "C");
        Assert.assertEquals(abc1, abc0);
        Assert.assertEquals(abc, abc0);
        Assert.assertEquals(abc1.hashCode(), abc0.hashCode());
        final List<String> an = FlatLists.of("A", null);
        final List<String> an0 = Arrays.asList("A", null);
        Assert.assertEquals(an, an0);
        Assert.assertEquals(an.hashCode(), an0.hashCode());
        final List<String> anb = FlatLists.of("A", null, "B");
        final List<String> anb0 = Arrays.asList("A", null, "B");
        Assert.assertEquals(anb, anb0);
        Assert.assertEquals(anb.hashCode(), anb0.hashCode());
        Assert.assertEquals((anb + ".indexOf(null)"), 1, anb.indexOf(null));
        Assert.assertEquals((anb + ".lastIndexOf(null)"), 1, anb.lastIndexOf(null));
        Assert.assertEquals((anb + ".indexOf(B)"), 2, anb.indexOf("B"));
        Assert.assertEquals((anb + ".lastIndexOf(A)"), 0, anb.lastIndexOf("A"));
        Assert.assertEquals((anb + ".indexOf(Z)"), (-1), anb.indexOf("Z"));
        Assert.assertEquals((anb + ".lastIndexOf(Z)"), (-1), anb.lastIndexOf("Z"));
        // Comparisons
        Assert.assertThat(emp, CoreMatchers.instanceOf(Comparable.class));
        Assert.assertThat(ab, CoreMatchers.instanceOf(Comparable.class));
        @SuppressWarnings("unchecked")
        final Comparable<List> cemp = ((Comparable) (emp));
        @SuppressWarnings("unchecked")
        final Comparable<List> cab = ((Comparable) (ab));
        Assert.assertThat(cemp.compareTo(emp), CoreMatchers.is(0));
        Assert.assertThat(((cemp.compareTo(ab)) < 0), CoreMatchers.is(true));
        Assert.assertThat(cab.compareTo(ab), CoreMatchers.is(0));
        Assert.assertThat(((cab.compareTo(emp)) > 0), CoreMatchers.is(true));
        Assert.assertThat(((cab.compareTo(anb)) > 0), CoreMatchers.is(true));
    }

    @Test
    public void testFlatList2() {
        checkFlatList(0);
        checkFlatList(1);
        checkFlatList(2);
        checkFlatList(3);
        checkFlatList(4);
        checkFlatList(5);
        checkFlatList(6);
        checkFlatList(7);
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-2287">[CALCITE-2287]
     * FlatList.equals throws StackOverflowError</a>.
     */
    @Test
    public void testFlat34Equals() {
        List f3list = FlatLists.of(1, 2, 3);
        List f4list = FlatLists.of(1, 2, 3, 4);
        Assert.assertThat(f3list.equals(f4list), CoreMatchers.is(false));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFlatListN() {
        List<List<Object>> list = new ArrayList<>();
        list.add(FlatLists.of());
        list.add(FlatLists.<Object>copyOf());
        list.add(FlatLists.of("A"));
        list.add(FlatLists.copyOf(((Object) ("A"))));
        list.add(FlatLists.of("A", "B"));
        list.add(FlatLists.of(((Object) ("A")), "B"));
        list.add(Lists.newArrayList(Util.last(list)));
        list.add(FlatLists.of("A", null));
        list.add(Lists.newArrayList(Util.last(list)));
        list.add(FlatLists.of("A", "B", "C"));
        list.add(Lists.newArrayList(Util.last(list)));
        list.add(FlatLists.copyOf(((Object) ("A")), "B", "C"));
        list.add(FlatLists.of("A", null, "C"));
        list.add(FlatLists.of("A", "B", "C", "D"));
        list.add(Lists.newArrayList(Util.last(list)));
        list.add(FlatLists.copyOf(((Object) ("A")), "B", "C", "D"));
        list.add(FlatLists.of("A", null, "C", "D"));
        list.add(Lists.newArrayList(Util.last(list)));
        list.add(FlatLists.of("A", "B", "C", "D", "E"));
        list.add(Lists.newArrayList(Util.last(list)));
        list.add(FlatLists.copyOf(((Object) ("A")), "B", "C", "D", "E"));
        list.add(FlatLists.of("A", null, "C", "D", "E"));
        list.add(FlatLists.of("A", "B", "C", "D", "E", "F"));
        list.add(FlatLists.copyOf(((Object) ("A")), "B", "C", "D", "E", "F"));
        list.add(FlatLists.of("A", null, "C", "D", "E", "F"));
        list.add(((List) (FlatLists.of(((Comparable) ("A")), "B", "C", "D", "E", "F", "G"))));
        list.add(FlatLists.copyOf(((Object) ("A")), "B", "C", "D", "E", "F", "G"));
        list.add(Lists.newArrayList(Util.last(list)));
        list.add(((List) (FlatLists.of(((Comparable) ("A")), null, "C", "D", "E", "F", "G"))));
        list.add(Lists.newArrayList(Util.last(list)));
        for (int i = 0; i < (list.size()); i++) {
            final List<Object> outer = list.get(i);
            for (List<Object> inner : list) {
                if (inner.toString().equals("[A, B, C,D]")) {
                    System.out.println(1);
                }
                boolean strEq = outer.toString().equals(inner.toString());
                Assert.assertThat((((outer.toString()) + "=") + (inner.toString())), outer.equals(inner), CoreMatchers.is(strEq));
            }
        }
    }

    @Test
    public void testFlatListProduct() {
        final List<Enumerator<List<String>>> list = new ArrayList<>();
        list.add(Linq4j.enumerator(l2(l1("a"), l1("b"))));
        list.add(Linq4j.enumerator(l3(l2("x", "p"), l2("y", "q"), l2("z", "r"))));
        final Enumerable<FlatLists.ComparableList<String>> product = SqlFunctions.product(list, 3, false);
        int n = 0;
        FlatLists.ComparableList<String> previous = FlatLists.of();
        for (FlatLists.ComparableList<String> strings : product) {
            if ((n++) == 1) {
                Assert.assertThat(strings.size(), CoreMatchers.is(3));
                Assert.assertThat(strings.get(0), CoreMatchers.is("a"));
                Assert.assertThat(strings.get(1), CoreMatchers.is("y"));
                Assert.assertThat(strings.get(2), CoreMatchers.is("q"));
            }
            if (previous != null) {
                Assert.assertTrue(((previous.compareTo(strings)) < 0));
            }
            previous = strings;
        }
        Assert.assertThat(n, CoreMatchers.is(6));
    }

    /**
     * Unit test for {@link AvaticaUtils#toCamelCase(String)}.
     */
    @Test
    public void testToCamelCase() {
        Assert.assertEquals("myJdbcDriver", AvaticaUtils.toCamelCase("MY_JDBC_DRIVER"));
        Assert.assertEquals("myJdbcDriver", AvaticaUtils.toCamelCase("MY_JDBC__DRIVER"));
        Assert.assertEquals("myJdbcDriver", AvaticaUtils.toCamelCase("my_jdbc_driver"));
        Assert.assertEquals("abCdefGHij", AvaticaUtils.toCamelCase("ab_cdEf_g_Hij"));
        Assert.assertEquals("JdbcDriver", AvaticaUtils.toCamelCase("_JDBC_DRIVER"));
        Assert.assertEquals("", AvaticaUtils.toCamelCase("_"));
        Assert.assertEquals("", AvaticaUtils.toCamelCase(""));
    }

    /**
     * Unit test for {@link AvaticaUtils#camelToUpper(String)}.
     */
    @Test
    public void testCamelToUpper() {
        Assert.assertEquals("MY_JDBC_DRIVER", AvaticaUtils.camelToUpper("myJdbcDriver"));
        Assert.assertEquals("MY_J_D_B_C_DRIVER", AvaticaUtils.camelToUpper("myJDBCDriver"));
        Assert.assertEquals("AB_CDEF_G_HIJ", AvaticaUtils.camelToUpper("abCdefGHij"));
        Assert.assertEquals("_JDBC_DRIVER", AvaticaUtils.camelToUpper("JdbcDriver"));
        Assert.assertEquals("", AvaticaUtils.camelToUpper(""));
    }

    /**
     * Unit test for {@link Util#isDistinct(java.util.List)}.
     */
    @Test
    public void testDistinct() {
        Assert.assertTrue(Util.isDistinct(Collections.emptyList()));
        Assert.assertTrue(Util.isDistinct(Arrays.asList("a")));
        Assert.assertTrue(Util.isDistinct(Arrays.asList("a", "b", "c")));
        Assert.assertFalse(Util.isDistinct(Arrays.asList("a", "b", "a")));
        Assert.assertTrue(Util.isDistinct(Arrays.asList("a", "b", null)));
        Assert.assertFalse(Util.isDistinct(Arrays.asList("a", null, "b", null)));
    }

    /**
     * Unit test for
     * {@link Util#intersects(java.util.Collection, java.util.Collection)}.
     */
    @Test
    public void testIntersects() {
        final List<String> empty = Collections.emptyList();
        final List<String> listA = Collections.singletonList("a");
        final List<String> listC = Collections.singletonList("c");
        final List<String> listD = Collections.singletonList("d");
        final List<String> listAbc = Arrays.asList("a", "b", "c");
        Assert.assertThat(Util.intersects(empty, listA), CoreMatchers.is(false));
        Assert.assertThat(Util.intersects(empty, empty), CoreMatchers.is(false));
        Assert.assertThat(Util.intersects(listA, listAbc), CoreMatchers.is(true));
        Assert.assertThat(Util.intersects(listAbc, listAbc), CoreMatchers.is(true));
        Assert.assertThat(Util.intersects(listAbc, listC), CoreMatchers.is(true));
        Assert.assertThat(Util.intersects(listAbc, listD), CoreMatchers.is(false));
        Assert.assertThat(Util.intersects(listC, listD), CoreMatchers.is(false));
    }

    /**
     * Unit test for {@link org.apache.calcite.util.JsonBuilder}.
     */
    @Test
    public void testJsonBuilder() {
        JsonBuilder builder = new JsonBuilder();
        Map<String, Object> map = builder.map();
        map.put("foo", 1);
        map.put("baz", true);
        map.put("bar", "can't");
        List<Object> list = builder.list();
        map.put("list", list);
        list.add(2);
        list.add(3);
        list.add(builder.list());
        list.add(builder.map());
        list.add(null);
        map.put("nullValue", null);
        Assert.assertEquals(("{\n" + ((((((((((("  \"foo\": 1,\n" + "  \"baz\": true,\n") + "  \"bar\": \"can\'t\",\n") + "  \"list\": [\n") + "    2,\n") + "    3,\n") + "    [],\n") + "    {},\n") + "    null\n") + "  ],\n") + "  \"nullValue\": null\n") + "}")), builder.toJsonString(map));
    }

    @Test
    public void testCompositeMap() {
        String[] beatles = new String[]{ "john", "paul", "george", "ringo" };
        Map<String, Integer> beatleMap = new LinkedHashMap<String, Integer>();
        for (String beatle : beatles) {
            beatleMap.put(beatle, beatle.length());
        }
        CompositeMap<String, Integer> map = CompositeMap.of(beatleMap);
        checkCompositeMap(beatles, map);
        map = CompositeMap.of(beatleMap, Collections.emptyMap());
        checkCompositeMap(beatles, map);
        map = CompositeMap.of(Collections.emptyMap(), beatleMap);
        checkCompositeMap(beatles, map);
        map = CompositeMap.of(beatleMap, beatleMap);
        checkCompositeMap(beatles, map);
        final Map<String, Integer> founderMap = new LinkedHashMap<String, Integer>();
        founderMap.put("ben", 1706);
        founderMap.put("george", 1732);
        founderMap.put("thomas", 1743);
        map = CompositeMap.of(beatleMap, founderMap);
        Assert.assertThat(map.isEmpty(), CoreMatchers.equalTo(false));
        Assert.assertThat(map.size(), CoreMatchers.equalTo(6));
        Assert.assertThat(map.keySet().size(), CoreMatchers.equalTo(6));
        Assert.assertThat(map.entrySet().size(), CoreMatchers.equalTo(6));
        Assert.assertThat(map.values().size(), CoreMatchers.equalTo(6));
        Assert.assertThat(map.containsKey("john"), CoreMatchers.equalTo(true));
        Assert.assertThat(map.containsKey("george"), CoreMatchers.equalTo(true));
        Assert.assertThat(map.containsKey("ben"), CoreMatchers.equalTo(true));
        Assert.assertThat(map.containsKey("andrew"), CoreMatchers.equalTo(false));
        Assert.assertThat(map.get("ben"), CoreMatchers.equalTo(1706));
        Assert.assertThat(map.get("george"), CoreMatchers.equalTo(6));// use value from first map

        Assert.assertThat(map.values().contains(1743), CoreMatchers.equalTo(true));
        Assert.assertThat(map.values().contains(1732), CoreMatchers.equalTo(false));// masked

        Assert.assertThat(map.values().contains(1999), CoreMatchers.equalTo(false));
    }

    /**
     * Tests {@link Util#commaList(java.util.List)}.
     */
    @Test
    public void testCommaList() {
        try {
            String s = Util.commaList(null);
            Assert.fail(("expected NPE, got " + s));
        } catch (NullPointerException e) {
            // ok
        }
        Assert.assertThat(Util.commaList(ImmutableList.of()), CoreMatchers.equalTo(""));
        Assert.assertThat(Util.commaList(ImmutableList.of(1)), CoreMatchers.equalTo("1"));
        Assert.assertThat(Util.commaList(ImmutableList.of(2, 3)), CoreMatchers.equalTo("2, 3"));
        Assert.assertThat(Util.commaList(Arrays.asList(2, null, 3)), CoreMatchers.equalTo("2, null, 3"));
    }

    /**
     * Unit test for {@link Util#firstDuplicate(java.util.List)}.
     */
    @Test
    public void testFirstDuplicate() {
        Assert.assertThat(Util.firstDuplicate(ImmutableList.of()), CoreMatchers.equalTo((-1)));
        Assert.assertThat(Util.firstDuplicate(ImmutableList.of(5)), CoreMatchers.equalTo((-1)));
        Assert.assertThat(Util.firstDuplicate(ImmutableList.of(5, 6)), CoreMatchers.equalTo((-1)));
        Assert.assertThat(Util.firstDuplicate(ImmutableList.of(5, 6, 5)), CoreMatchers.equalTo(2));
        Assert.assertThat(Util.firstDuplicate(ImmutableList.of(5, 5, 6)), CoreMatchers.equalTo(1));
        Assert.assertThat(Util.firstDuplicate(ImmutableList.of(5, 5, 6, 5)), CoreMatchers.equalTo(1));
        // list longer than 15, the threshold where we move to set-based algorithm
        Assert.assertThat(Util.firstDuplicate(ImmutableList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 3, 19, 3, 21)), CoreMatchers.equalTo(18));
    }

    /**
     * Benchmark for {@link Util#isDistinct}. Has determined that map-based
     * implementation is better than nested loops implementation if list is larger
     * than about 15.
     */
    @Test
    public void testIsDistinctBenchmark() {
        // Run a much quicker form of the test during regular testing.
        final int limit = (Benchmark.enabled()) ? 1000000 : 10;
        final int zMax = 100;
        for (int i = 0; i < 30; i++) {
            final int size = i;
            new Benchmark((("isDistinct " + i) + " (set)"), ( statistician) -> {
                final Random random = new Random(0);
                final List<List<Integer>> lists = new ArrayList<List<Integer>>();
                for (int z = 0; z < zMax; z++) {
                    final List<Integer> list = new ArrayList<Integer>();
                    for (int k = 0; k < size; k++) {
                        list.add(random.nextInt((size * size)));
                    }
                    lists.add(list);
                }
                long nanos = System.nanoTime();
                int n = 0;
                for (int j = 0; j < limit; j++) {
                    n += Util.firstDuplicate(lists.get((j % zMax)));
                }
                statistician.record(nanos);
                Util.discard(n);
                return null;
            }, 5).run();
        }
    }

    /**
     * Unit test for {@link Util#distinctList(List)}
     * and {@link Util#distinctList(Iterable)}.
     */
    @Test
    public void testDistinctList() {
        Assert.assertThat(Util.distinctList(Arrays.asList(1, 2)), CoreMatchers.is(Arrays.asList(1, 2)));
        Assert.assertThat(Util.distinctList(Arrays.asList(1, 2, 1)), CoreMatchers.is(Arrays.asList(1, 2)));
        try {
            List<Object> o = Util.distinctList(null);
            Assert.fail(("expected exception, got " + o));
        } catch (NullPointerException ignore) {
        }
        final List<Integer> empty = ImmutableList.of();
        Assert.assertThat(Util.distinctList(empty), CoreMatchers.sameInstance(empty));
        final Iterable<Integer> emptyIterable = empty;
        Assert.assertThat(Util.distinctList(emptyIterable), CoreMatchers.sameInstance(emptyIterable));
        final List<Integer> empty2 = ImmutableList.of();
        Assert.assertThat(Util.distinctList(empty2), CoreMatchers.sameInstance(empty2));
        final List<String> abc = ImmutableList.of("a", "b", "c");
        Assert.assertThat(Util.distinctList(abc), CoreMatchers.sameInstance(abc));
        final List<String> a = ImmutableList.of("a");
        Assert.assertThat(Util.distinctList(a), CoreMatchers.sameInstance(a));
        final List<String> cbca = ImmutableList.of("c", "b", "c", "a");
        Assert.assertThat(Util.distinctList(cbca), CoreMatchers.not(CoreMatchers.sameInstance(cbca)));
        Assert.assertThat(Util.distinctList(cbca), CoreMatchers.is(Arrays.asList("c", "b", "a")));
        final Collection<String> cbcaC = new LinkedHashSet<>(cbca);
        Assert.assertThat(Util.distinctList(cbcaC), CoreMatchers.not(CoreMatchers.sameInstance(cbca)));
        Assert.assertThat(Util.distinctList(cbcaC), CoreMatchers.is(Arrays.asList("c", "b", "a")));
    }

    /**
     * Unit test for {@link Utilities#hashCode(double)}.
     */
    @Test
    public void testHash() {
        checkHash(0.0);
        checkHash(1.0);
        checkHash((-2.5));
        checkHash((10.0 / 3.0));
        checkHash(Double.NEGATIVE_INFINITY);
        checkHash(Double.POSITIVE_INFINITY);
        checkHash(Double.MAX_VALUE);
        checkHash(Double.MIN_VALUE);
    }

    /**
     * Unit test for {@link Util#startsWith}.
     */
    @Test
    public void testStartsWithList() {
        Assert.assertThat(Util.startsWith(list("x"), list()), CoreMatchers.is(true));
        Assert.assertThat(Util.startsWith(list("x"), list("x")), CoreMatchers.is(true));
        Assert.assertThat(Util.startsWith(list("x"), list("y")), CoreMatchers.is(false));
        Assert.assertThat(Util.startsWith(list("x"), list("x", "y")), CoreMatchers.is(false));
        Assert.assertThat(Util.startsWith(list("x", "y"), list("x")), CoreMatchers.is(true));
        Assert.assertThat(Util.startsWith(list(), list()), CoreMatchers.is(true));
        Assert.assertThat(Util.startsWith(list(), list("x")), CoreMatchers.is(false));
    }

    @Test
    public void testResources() {
        Resources.validate(RESOURCE);
        checkResourceMethodNames(RESOURCE);
    }

    /**
     * Tests that sorted sets behave the way we expect.
     */
    @Test
    public void testSortedSet() {
        final TreeSet<String> treeSet = new TreeSet<String>();
        Collections.addAll(treeSet, "foo", "bar", "fOo", "FOO", "pug");
        Assert.assertThat(treeSet.size(), CoreMatchers.equalTo(5));
        final TreeSet<String> treeSet2 = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        treeSet2.addAll(treeSet);
        Assert.assertThat(treeSet2.size(), CoreMatchers.equalTo(3));
        final Comparator<String> comparator = ( o1, o2) -> {
            String u1 = o1.toUpperCase(Locale.ROOT);
            String u2 = o2.toUpperCase(Locale.ROOT);
            int c = u1.compareTo(u2);
            if (c == 0) {
                c = o1.compareTo(o2);
            }
            return c;
        };
        final TreeSet<String> treeSet3 = new TreeSet<String>(comparator);
        treeSet3.addAll(treeSet);
        Assert.assertThat(treeSet3.size(), CoreMatchers.equalTo(5));
        Assert.assertThat(checkNav(treeSet3, "foo").size(), CoreMatchers.equalTo(3));
        Assert.assertThat(checkNav(treeSet3, "FOO").size(), CoreMatchers.equalTo(3));
        Assert.assertThat(checkNav(treeSet3, "FoO").size(), CoreMatchers.equalTo(3));
        Assert.assertThat(checkNav(treeSet3, "BAR").size(), CoreMatchers.equalTo(1));
    }

    /**
     * Test for {@link org.apache.calcite.util.ImmutableNullableList}.
     */
    @Test
    public void testImmutableNullableList() {
        final List<String> arrayList = Arrays.asList("a", null, "c");
        final List<String> list = ImmutableNullableList.copyOf(arrayList);
        Assert.assertThat(list.size(), CoreMatchers.equalTo(arrayList.size()));
        Assert.assertThat(list, CoreMatchers.equalTo(arrayList));
        Assert.assertThat(list.hashCode(), CoreMatchers.equalTo(arrayList.hashCode()));
        Assert.assertThat(list.toString(), CoreMatchers.equalTo(arrayList.toString()));
        String z = "";
        for (String s : list) {
            z += s;
        }
        Assert.assertThat(z, CoreMatchers.equalTo("anullc"));
        // changes to array list do not affect copy
        arrayList.set(0, "z");
        Assert.assertThat(arrayList.get(0), CoreMatchers.equalTo("z"));
        Assert.assertThat(list.get(0), CoreMatchers.equalTo("a"));
        try {
            boolean b = list.add("z");
            Assert.fail(("expected error, got " + b));
        } catch (UnsupportedOperationException e) {
            // ok
        }
        try {
            String b = list.set(1, "z");
            Assert.fail(("expected error, got " + b));
        } catch (UnsupportedOperationException e) {
            // ok
        }
        // empty list uses ImmutableList
        Assert.assertThat(ImmutableNullableList.copyOf(Collections.emptyList()), CoreMatchers.isA(((Class) (ImmutableList.class))));
        // list with no nulls uses ImmutableList
        final List<String> abcList = Arrays.asList("a", "b", "c");
        Assert.assertThat(ImmutableNullableList.copyOf(abcList), CoreMatchers.isA(((Class) (ImmutableList.class))));
        // list with no nulls uses ImmutableList
        final Iterable<String> abc = abcList::iterator;
        Assert.assertThat(ImmutableNullableList.copyOf(abc), CoreMatchers.isA(((Class) (ImmutableList.class))));
        Assert.assertThat(ImmutableNullableList.copyOf(abc), CoreMatchers.equalTo(abcList));
        // list with no nulls uses ImmutableList
        final List<String> ab0cList = Arrays.asList("a", "b", null, "c");
        final Iterable<String> ab0c = ab0cList::iterator;
        Assert.assertThat(ImmutableNullableList.copyOf(ab0c), CoreMatchers.not(CoreMatchers.isA(((Class) (ImmutableList.class)))));
        Assert.assertThat(ImmutableNullableList.copyOf(ab0c), CoreMatchers.equalTo(ab0cList));
    }

    /**
     * Test for {@link org.apache.calcite.util.UnmodifiableArrayList}.
     */
    @Test
    public void testUnmodifiableArrayList() {
        final String[] strings = new String[]{ "a", null, "c" };
        final List<String> arrayList = Arrays.asList(strings);
        final List<String> list = UnmodifiableArrayList.of(strings);
        Assert.assertThat(list.size(), CoreMatchers.equalTo(arrayList.size()));
        Assert.assertThat(list, CoreMatchers.equalTo(arrayList));
        Assert.assertThat(list.hashCode(), CoreMatchers.equalTo(arrayList.hashCode()));
        Assert.assertThat(list.toString(), CoreMatchers.equalTo(arrayList.toString()));
        String z = "";
        for (String s : list) {
            z += s;
        }
        Assert.assertThat(z, CoreMatchers.equalTo("anullc"));
        // changes to array list do affect copy
        arrayList.set(0, "z");
        Assert.assertThat(arrayList.get(0), CoreMatchers.equalTo("z"));
        Assert.assertThat(list.get(0), CoreMatchers.equalTo("z"));
        try {
            boolean b = list.add("z");
            Assert.fail(("expected error, got " + b));
        } catch (UnsupportedOperationException e) {
            // ok
        }
        try {
            String b = list.set(1, "z");
            Assert.fail(("expected error, got " + b));
        } catch (UnsupportedOperationException e) {
            // ok
        }
    }

    /**
     * Test for {@link org.apache.calcite.util.ImmutableNullableList.Builder}.
     */
    @Test
    public void testImmutableNullableListBuilder() {
        final ImmutableNullableList.Builder<String> builder = ImmutableNullableList.builder();
        builder.add("a").add(((String) (null))).add("c");
        final List<String> arrayList = Arrays.asList("a", null, "c");
        final List<String> list = builder.build();
        Assert.assertThat(arrayList.equals(list), CoreMatchers.is(true));
    }

    @Test
    public void testHuman() {
        Assert.assertThat(Util.human(0.0), CoreMatchers.equalTo("0"));
        Assert.assertThat(Util.human(1.0), CoreMatchers.equalTo("1"));
        Assert.assertThat(Util.human(19.0), CoreMatchers.equalTo("19"));
        Assert.assertThat(Util.human(198.0), CoreMatchers.equalTo("198"));
        Assert.assertThat(Util.human(1000.0), CoreMatchers.equalTo("1.00K"));
        Assert.assertThat(Util.human(1002.0), CoreMatchers.equalTo("1.00K"));
        Assert.assertThat(Util.human(1009.0), CoreMatchers.equalTo("1.01K"));
        Assert.assertThat(Util.human(1234.0), CoreMatchers.equalTo("1.23K"));
        Assert.assertThat(Util.human(1987.0), CoreMatchers.equalTo("1.99K"));
        Assert.assertThat(Util.human(1999.0), CoreMatchers.equalTo("2.00K"));
        Assert.assertThat(Util.human(86837.2), CoreMatchers.equalTo("86.8K"));
        Assert.assertThat(Util.human(868372.8), CoreMatchers.equalTo("868K"));
        Assert.assertThat(Util.human(1009000.0), CoreMatchers.equalTo("1.01M"));
        Assert.assertThat(Util.human(1999999.0), CoreMatchers.equalTo("2.00M"));
        Assert.assertThat(Util.human(1.009E9), CoreMatchers.equalTo("1.01G"));
        Assert.assertThat(Util.human(1.999999E9), CoreMatchers.equalTo("2.00G"));
        Assert.assertThat(Util.human((-1.0)), CoreMatchers.equalTo("-1"));
        Assert.assertThat(Util.human((-19.0)), CoreMatchers.equalTo("-19"));
        Assert.assertThat(Util.human((-198.0)), CoreMatchers.equalTo("-198"));
        Assert.assertThat(Util.human((-1.999999E9)), CoreMatchers.equalTo("-2.00G"));
        // not ideal - should use m (milli) and u (micro)
        Assert.assertThat(Util.human(0.18), CoreMatchers.equalTo("0.18"));
        Assert.assertThat(Util.human(0.018), CoreMatchers.equalTo("0.018"));
        Assert.assertThat(Util.human(0.0018), CoreMatchers.equalTo("0.0018"));
        Assert.assertThat(Util.human(1.8E-4), CoreMatchers.equalTo("1.8E-4"));
        Assert.assertThat(Util.human(1.8E-5), CoreMatchers.equalTo("1.8E-5"));
        Assert.assertThat(Util.human(1.8E-6), CoreMatchers.equalTo("1.8E-6"));
        // bad - should round to 3 digits
        Assert.assertThat(Util.human(0.181111), CoreMatchers.equalTo("0.181111"));
        Assert.assertThat(Util.human(0.0181111), CoreMatchers.equalTo("0.0181111"));
        Assert.assertThat(Util.human(0.00181111), CoreMatchers.equalTo("0.00181111"));
        Assert.assertThat(Util.human(1.81111E-4), CoreMatchers.equalTo("1.81111E-4"));
        Assert.assertThat(Util.human(1.81111E-5), CoreMatchers.equalTo("1.81111E-5"));
        Assert.assertThat(Util.human(1.81111E-6), CoreMatchers.equalTo("1.81111E-6"));
    }

    /**
     * Tests {@link Util#immutableCopy(Iterable)}.
     */
    @Test
    public void testImmutableCopy() {
        final List<Integer> list3 = Arrays.asList(1, 2, 3);
        final List<Integer> immutableList3 = ImmutableList.copyOf(list3);
        final List<Integer> list0 = Arrays.asList();
        final List<Integer> immutableList0 = ImmutableList.copyOf(list0);
        final List<Integer> list1 = Arrays.asList(1);
        final List<Integer> immutableList1 = ImmutableList.copyOf(list1);
        final List<List<Integer>> list301 = Arrays.asList(list3, list0, list1);
        final List<List<Integer>> immutableList301 = Util.immutableCopy(list301);
        Assert.assertThat(immutableList301.size(), CoreMatchers.is(3));
        Assert.assertThat(immutableList301, CoreMatchers.is(list301));
        Assert.assertThat(immutableList301, CoreMatchers.not(CoreMatchers.sameInstance(list301)));
        for (List<Integer> list : immutableList301) {
            Assert.assertThat(list, CoreMatchers.isA(((Class) (ImmutableList.class))));
        }
        // if you copy the copy, you get the same instance
        final List<List<Integer>> immutableList301b = Util.immutableCopy(immutableList301);
        Assert.assertThat(immutableList301b, CoreMatchers.sameInstance(immutableList301));
        Assert.assertThat(immutableList301b, CoreMatchers.not(CoreMatchers.sameInstance(list301)));
        // if the elements of the list are immutable lists, they are not copied
        final List<List<Integer>> list301c = Arrays.asList(immutableList3, immutableList0, immutableList1);
        final List<List<Integer>> list301d = Util.immutableCopy(list301c);
        Assert.assertThat(list301d.size(), CoreMatchers.is(3));
        Assert.assertThat(list301d, CoreMatchers.is(list301));
        Assert.assertThat(list301d, CoreMatchers.not(CoreMatchers.sameInstance(list301)));
        Assert.assertThat(list301d.get(0), CoreMatchers.sameInstance(immutableList3));
        Assert.assertThat(list301d.get(1), CoreMatchers.sameInstance(immutableList0));
        Assert.assertThat(list301d.get(2), CoreMatchers.sameInstance(immutableList1));
    }

    @Test
    public void testAsIndexView() {
        final List<String> values = Lists.newArrayList("abCde", "X", "y");
        final Map<String, String> map = Util.asIndexMapJ(values, ( input) -> input.toUpperCase(Locale.ROOT));
        Assert.assertThat(map.size(), CoreMatchers.equalTo(values.size()));
        Assert.assertThat(map.get("X"), CoreMatchers.equalTo("X"));
        Assert.assertThat(map.get("Y"), CoreMatchers.equalTo("y"));
        Assert.assertThat(map.get("y"), CoreMatchers.is(((String) (null))));
        Assert.assertThat(map.get("ABCDE"), CoreMatchers.equalTo("abCde"));
        // If you change the values collection, the map changes.
        values.remove(1);
        Assert.assertThat(map.size(), CoreMatchers.equalTo(values.size()));
        Assert.assertThat(map.get("X"), CoreMatchers.is(((String) (null))));
        Assert.assertThat(map.get("Y"), CoreMatchers.equalTo("y"));
    }

    @Test
    public void testRelBuilderExample() {
        new RelBuilderExample(false).runAllExamples();
    }

    @Test
    public void testOrdReverse() {
        checkOrdReverse(Ord.reverse(Arrays.asList("a", "b", "c")));
        checkOrdReverse(Ord.reverse("a", "b", "c"));
        Assert.assertThat(Ord.reverse(ImmutableList.<String>of()).iterator().hasNext(), CoreMatchers.is(false));
        Assert.assertThat(Ord.reverse().iterator().hasNext(), CoreMatchers.is(false));
    }

    /**
     * Tests {@link org.apache.calcite.util.ReflectUtil#getParameterName}.
     */
    @Test
    public void testParameterName() throws NoSuchMethodException {
        final Method method = UtilTest.class.getMethod("foo", int.class, int.class);
        Assert.assertThat(ReflectUtil.getParameterName(method, 0), CoreMatchers.is("arg0"));
        Assert.assertThat(ReflectUtil.getParameterName(method, 1), CoreMatchers.is("j"));
    }

    @Test
    public void testListToString() {
        checkListToString("x");
        checkListToString("");
        checkListToString();
        checkListToString("ab", "c", "");
        checkListToString("ab", "c", "", "de");
        checkListToString("ab", "c.");
        checkListToString("ab", "c.d");
        checkListToString("ab", ".d");
        checkListToString(".ab", "d");
        checkListToString(".a", "d");
        checkListToString("a.", "d");
    }

    /**
     * Tests {@link org.apache.calcite.util.TryThreadLocal}.
     *
     * <p>TryThreadLocal was introduced to fix
     * <a href="https://issues.apache.org/jira/browse/CALCITE-915">[CALCITE-915]
     * Tests do not unset ThreadLocal values on exit</a>.
     */
    @Test
    public void testTryThreadLocal() {
        final TryThreadLocal<String> local1 = TryThreadLocal.of("foo");
        Assert.assertThat(local1.get(), CoreMatchers.is("foo"));
        TryThreadLocal.Memo memo1 = local1.push("bar");
        Assert.assertThat(local1.get(), CoreMatchers.is("bar"));
        local1.set("baz");
        Assert.assertThat(local1.get(), CoreMatchers.is("baz"));
        memo1.close();
        Assert.assertThat(local1.get(), CoreMatchers.is("foo"));
        final TryThreadLocal<String> local2 = TryThreadLocal.of(null);
        Assert.assertThat(local2.get(), CoreMatchers.nullValue());
        TryThreadLocal.Memo memo2 = local2.push("a");
        Assert.assertThat(local2.get(), CoreMatchers.is("a"));
        local2.set("b");
        Assert.assertThat(local2.get(), CoreMatchers.is("b"));
        TryThreadLocal.Memo memo2B = local2.push(null);
        Assert.assertThat(local2.get(), CoreMatchers.nullValue());
        memo2B.close();
        Assert.assertThat(local2.get(), CoreMatchers.is("b"));
        memo2.close();
        Assert.assertThat(local2.get(), CoreMatchers.nullValue());
        local2.set("x");
        try (TryThreadLocal.Memo ignore = local2.push("y")) {
            Assert.assertThat(local2.get(), CoreMatchers.is("y"));
            local2.set("z");
        }
        Assert.assertThat(local2.get(), CoreMatchers.is("x"));
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-1264">[CALCITE-1264]
     * Litmus argument interpolation</a>.
     */
    @Test
    public void testLitmus() {
        boolean b = checkLitmus(2, THROW);
        Assert.assertThat(b, CoreMatchers.is(true));
        b = checkLitmus(2, IGNORE);
        Assert.assertThat(b, CoreMatchers.is(true));
        try {
            b = checkLitmus((-1), THROW);
            Assert.fail(("expected fail, got " + b));
        } catch (AssertionError e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.is("-1 is less than 0"));
        }
        b = checkLitmus((-1), IGNORE);
        Assert.assertThat(b, CoreMatchers.is(false));
    }

    /**
     * Unit test for {@link org.apache.calcite.util.NameSet}.
     */
    @Test
    public void testNameSet() {
        final NameSet names = new NameSet();
        Assert.assertThat(names.contains("foo", true), CoreMatchers.is(false));
        Assert.assertThat(names.contains("foo", false), CoreMatchers.is(false));
        names.add("baz");
        Assert.assertThat(names.contains("foo", true), CoreMatchers.is(false));
        Assert.assertThat(names.contains("foo", false), CoreMatchers.is(false));
        Assert.assertThat(names.contains("baz", true), CoreMatchers.is(true));
        Assert.assertThat(names.contains("baz", false), CoreMatchers.is(true));
        Assert.assertThat(names.contains("BAZ", true), CoreMatchers.is(false));
        Assert.assertThat(names.contains("BAZ", false), CoreMatchers.is(true));
        Assert.assertThat(names.contains("bAz", false), CoreMatchers.is(true));
        Assert.assertThat(names.range("baz", true).size(), CoreMatchers.is(1));
        Assert.assertThat(names.range("baz", false).size(), CoreMatchers.is(1));
        Assert.assertThat(names.range("BAZ", true).size(), CoreMatchers.is(0));
        Assert.assertThat(names.range("BaZ", true).size(), CoreMatchers.is(0));
        Assert.assertThat(names.range("BaZ", false).size(), CoreMatchers.is(1));
        Assert.assertThat(names.range("BAZ", false).size(), CoreMatchers.is(1));
        Assert.assertThat(names.contains("bAzinga", false), CoreMatchers.is(false));
        Assert.assertThat(names.range("bAzinga", true).size(), CoreMatchers.is(0));
        Assert.assertThat(names.range("bAzinga", false).size(), CoreMatchers.is(0));
        Assert.assertThat(names.contains("zoo", true), CoreMatchers.is(false));
        Assert.assertThat(names.contains("zoo", false), CoreMatchers.is(false));
        Assert.assertThat(names.range("zoo", true).size(), CoreMatchers.is(0));
        Assert.assertThat(Iterables.size(names.iterable()), CoreMatchers.is(1));
        names.add("Baz");
        names.add("Abcde");
        names.add("WOMBAT");
        names.add("Zymurgy");
        Assert.assertThat(names.toString(), CoreMatchers.is("[Abcde, Baz, baz, WOMBAT, Zymurgy]"));
        Assert.assertThat(Iterables.size(names.iterable()), CoreMatchers.is(5));
        Assert.assertThat(names.range("baz", false).size(), CoreMatchers.is(2));
        Assert.assertThat(names.range("baz", true).size(), CoreMatchers.is(1));
        Assert.assertThat(names.range("BAZ", true).size(), CoreMatchers.is(0));
        Assert.assertThat(names.range("Baz", true).size(), CoreMatchers.is(1));
        Assert.assertThat(names.contains("baz", true), CoreMatchers.is(true));
        Assert.assertThat(names.contains("baz", false), CoreMatchers.is(true));
        Assert.assertThat(names.contains("BAZ", true), CoreMatchers.is(false));
        Assert.assertThat(names.contains("BAZ", false), CoreMatchers.is(true));
        Assert.assertThat(names.contains("abcde", true), CoreMatchers.is(false));
        Assert.assertThat(names.contains("abcde", false), CoreMatchers.is(true));
        Assert.assertThat(names.contains("ABCDE", true), CoreMatchers.is(false));
        Assert.assertThat(names.contains("ABCDE", false), CoreMatchers.is(true));
        Assert.assertThat(names.contains("wombat", true), CoreMatchers.is(false));
        Assert.assertThat(names.contains("wombat", false), CoreMatchers.is(true));
        Assert.assertThat(names.contains("womBat", true), CoreMatchers.is(false));
        Assert.assertThat(names.contains("womBat", false), CoreMatchers.is(true));
        Assert.assertThat(names.contains("WOMBAT", true), CoreMatchers.is(true));
        Assert.assertThat(names.contains("WOMBAT", false), CoreMatchers.is(true));
        Assert.assertThat(names.contains("zyMurgy", true), CoreMatchers.is(false));
        Assert.assertThat(names.contains("zyMurgy", false), CoreMatchers.is(true));
        // [CALCITE-2481] NameSet assumes lowercase characters have greater codes
        // which does not hold for certain characters
        checkCase0("a");
        checkCase0("\u00b5");// "?"

    }

    /**
     * Unit test for {@link org.apache.calcite.util.NameMap}.
     */
    @Test
    public void testNameMap() {
        final NameMap<Integer> map = new NameMap();
        Assert.assertThat(map.containsKey("foo", true), CoreMatchers.is(false));
        Assert.assertThat(map.containsKey("foo", false), CoreMatchers.is(false));
        map.put("baz", 0);
        Assert.assertThat(map.containsKey("foo", true), CoreMatchers.is(false));
        Assert.assertThat(map.containsKey("foo", false), CoreMatchers.is(false));
        Assert.assertThat(map.containsKey("baz", true), CoreMatchers.is(true));
        Assert.assertThat(map.containsKey("baz", false), CoreMatchers.is(true));
        Assert.assertThat(map.containsKey("BAZ", true), CoreMatchers.is(false));
        Assert.assertThat(map.containsKey("BAZ", false), CoreMatchers.is(true));
        Assert.assertThat(map.containsKey("bAz", false), CoreMatchers.is(true));
        Assert.assertThat(map.range("baz", true).size(), CoreMatchers.is(1));
        Assert.assertThat(map.range("baz", false).size(), CoreMatchers.is(1));
        Assert.assertThat(map.range("BAZ", true).size(), CoreMatchers.is(0));
        Assert.assertThat(map.range("BaZ", true).size(), CoreMatchers.is(0));
        Assert.assertThat(map.range("BaZ", false).size(), CoreMatchers.is(1));
        Assert.assertThat(map.range("BAZ", false).size(), CoreMatchers.is(1));
        Assert.assertThat(map.containsKey("bAzinga", false), CoreMatchers.is(false));
        Assert.assertThat(map.range("bAzinga", true).size(), CoreMatchers.is(0));
        Assert.assertThat(map.range("bAzinga", false).size(), CoreMatchers.is(0));
        Assert.assertThat(map.containsKey("zoo", true), CoreMatchers.is(false));
        Assert.assertThat(map.containsKey("zoo", false), CoreMatchers.is(false));
        Assert.assertThat(map.range("zoo", true).size(), CoreMatchers.is(0));
        Assert.assertThat(map.map().size(), CoreMatchers.is(1));
        map.put("Baz", 1);
        map.put("Abcde", 2);
        map.put("WOMBAT", 4);
        map.put("Zymurgy", 3);
        Assert.assertThat(map.toString(), CoreMatchers.is("{Abcde=2, Baz=1, baz=0, WOMBAT=4, Zymurgy=3}"));
        Assert.assertThat(map.map().size(), CoreMatchers.is(5));
        Assert.assertThat(map.map().entrySet().size(), CoreMatchers.is(5));
        Assert.assertThat(map.map().keySet().size(), CoreMatchers.is(5));
        Assert.assertThat(map.range("baz", false).size(), CoreMatchers.is(2));
        Assert.assertThat(map.range("baz", true).size(), CoreMatchers.is(1));
        Assert.assertThat(map.range("BAZ", true).size(), CoreMatchers.is(0));
        Assert.assertThat(map.range("Baz", true).size(), CoreMatchers.is(1));
        Assert.assertThat(map.containsKey("baz", true), CoreMatchers.is(true));
        Assert.assertThat(map.containsKey("baz", false), CoreMatchers.is(true));
        Assert.assertThat(map.containsKey("BAZ", true), CoreMatchers.is(false));
        Assert.assertThat(map.containsKey("BAZ", false), CoreMatchers.is(true));
        Assert.assertThat(map.containsKey("abcde", true), CoreMatchers.is(false));
        Assert.assertThat(map.containsKey("abcde", false), CoreMatchers.is(true));
        Assert.assertThat(map.containsKey("ABCDE", true), CoreMatchers.is(false));
        Assert.assertThat(map.containsKey("ABCDE", false), CoreMatchers.is(true));
        Assert.assertThat(map.containsKey("wombat", true), CoreMatchers.is(false));
        Assert.assertThat(map.containsKey("wombat", false), CoreMatchers.is(true));
        Assert.assertThat(map.containsKey("womBat", false), CoreMatchers.is(true));
        Assert.assertThat(map.containsKey("zyMurgy", true), CoreMatchers.is(false));
        Assert.assertThat(map.containsKey("zyMurgy", false), CoreMatchers.is(true));
    }

    /**
     * Unit test for {@link org.apache.calcite.util.NameMultimap}.
     */
    @Test
    public void testNameMultimap() {
        final NameMultimap<Integer> map = new NameMultimap();
        Assert.assertThat(map.containsKey("foo", true), CoreMatchers.is(false));
        Assert.assertThat(map.containsKey("foo", false), CoreMatchers.is(false));
        map.put("baz", 0);
        map.put("baz", 0);
        map.put("BAz", 0);
        Assert.assertThat(map.containsKey("foo", true), CoreMatchers.is(false));
        Assert.assertThat(map.containsKey("foo", false), CoreMatchers.is(false));
        Assert.assertThat(map.containsKey("baz", true), CoreMatchers.is(true));
        Assert.assertThat(map.containsKey("baz", false), CoreMatchers.is(true));
        Assert.assertThat(map.containsKey("BAZ", true), CoreMatchers.is(false));
        Assert.assertThat(map.containsKey("BAZ", false), CoreMatchers.is(true));
        Assert.assertThat(map.containsKey("bAz", false), CoreMatchers.is(true));
        Assert.assertThat(map.range("baz", true).size(), CoreMatchers.is(2));
        Assert.assertThat(map.range("baz", false).size(), CoreMatchers.is(3));
        Assert.assertThat(map.range("BAZ", true).size(), CoreMatchers.is(0));
        Assert.assertThat(map.range("BaZ", true).size(), CoreMatchers.is(0));
        Assert.assertThat(map.range("BaZ", false).size(), CoreMatchers.is(3));
        Assert.assertThat(map.range("BAZ", false).size(), CoreMatchers.is(3));
        Assert.assertThat(map.containsKey("bAzinga", false), CoreMatchers.is(false));
        Assert.assertThat(map.range("bAzinga", true).size(), CoreMatchers.is(0));
        Assert.assertThat(map.range("bAzinga", false).size(), CoreMatchers.is(0));
        Assert.assertThat(map.containsKey("zoo", true), CoreMatchers.is(false));
        Assert.assertThat(map.containsKey("zoo", false), CoreMatchers.is(false));
        Assert.assertThat(map.range("zoo", true).size(), CoreMatchers.is(0));
        Assert.assertThat(map.map().size(), CoreMatchers.is(2));
        map.put("Baz", 1);
        map.put("Abcde", 2);
        map.put("WOMBAT", 4);
        map.put("Zymurgy", 3);
        final String expected = "{Abcde=[2], BAz=[0], Baz=[1], baz=[0, 0]," + " WOMBAT=[4], Zymurgy=[3]}";
        Assert.assertThat(map.toString(), CoreMatchers.is(expected));
        Assert.assertThat(map.map().size(), CoreMatchers.is(6));
        Assert.assertThat(map.map().entrySet().size(), CoreMatchers.is(6));
        Assert.assertThat(map.map().keySet().size(), CoreMatchers.is(6));
        Assert.assertThat(map.range("baz", false).size(), CoreMatchers.is(4));
        Assert.assertThat(map.range("baz", true).size(), CoreMatchers.is(2));
        Assert.assertThat(map.range("BAZ", true).size(), CoreMatchers.is(0));
        Assert.assertThat(map.range("Baz", true).size(), CoreMatchers.is(1));
        Assert.assertThat(map.containsKey("baz", true), CoreMatchers.is(true));
        Assert.assertThat(map.containsKey("baz", false), CoreMatchers.is(true));
        Assert.assertThat(map.containsKey("BAZ", true), CoreMatchers.is(false));
        Assert.assertThat(map.containsKey("BAZ", false), CoreMatchers.is(true));
        Assert.assertThat(map.containsKey("abcde", true), CoreMatchers.is(false));
        Assert.assertThat(map.containsKey("abcde", false), CoreMatchers.is(true));
        Assert.assertThat(map.containsKey("ABCDE", true), CoreMatchers.is(false));
        Assert.assertThat(map.containsKey("ABCDE", false), CoreMatchers.is(true));
        Assert.assertThat(map.containsKey("wombat", true), CoreMatchers.is(false));
        Assert.assertThat(map.containsKey("wombat", false), CoreMatchers.is(true));
        Assert.assertThat(map.containsKey("womBat", false), CoreMatchers.is(true));
        Assert.assertThat(map.containsKey("zyMurgy", true), CoreMatchers.is(false));
        Assert.assertThat(map.containsKey("zyMurgy", false), CoreMatchers.is(true));
    }

    @Test
    public void testNlsStringClone() {
        final NlsString s = new NlsString("foo", "LATIN1", SqlCollation.IMPLICIT);
        Assert.assertThat(s.toString(), CoreMatchers.is("_LATIN1'foo'"));
        final Object s2 = s.clone();
        Assert.assertThat(s2, CoreMatchers.instanceOf(NlsString.class));
        Assert.assertThat(s2, CoreMatchers.not(CoreMatchers.sameInstance(((Object) (s)))));
        Assert.assertThat(s2.toString(), CoreMatchers.is(s.toString()));
    }

    @Test
    public void testXmlOutput() {
        final StringWriter w = new StringWriter();
        final XmlOutput o = new XmlOutput(w);
        o.beginBeginTag("root");
        o.attribute("a1", "v1");
        o.attribute("a2", null);
        o.endBeginTag("root");
        o.beginTag("someText", null);
        o.content(("line 1 followed by empty line\n" + (("\n" + "line 3 with windows line ending\r\n") + "line 4 with no ending")));
        o.endTag("someText");
        o.endTag("root");
        final String s = w.toString();
        final String expected = "" + ((((((("<root a1=\"v1\">\n" + "\t<someText>\n") + "\t\t\tline 1 followed by empty line\n") + "\t\t\t\n") + "\t\t\tline 3 with windows line ending\n") + "\t\t\tline 4 with no ending\n") + "\t</someText>\n") + "</root>\n");
        Assert.assertThat(Util.toLinux(s), CoreMatchers.is(expected));
    }

    /**
     * Unit test for {@link Matchers#compose}.
     */
    @Test
    public void testComposeMatcher() {
        Assert.assertThat("x", CoreMatchers.is("x"));
        Assert.assertThat(CoreMatchers.is("x").matches("x"), CoreMatchers.is(true));
        Assert.assertThat(CoreMatchers.is("X").matches("x"), CoreMatchers.is(false));
        final Function<String, String> toUpper = ( s) -> s.toUpperCase(Locale.ROOT);
        Assert.assertThat(Matchers.compose(CoreMatchers.is("A"), toUpper).matches("a"), CoreMatchers.is(true));
        Assert.assertThat(Matchers.compose(CoreMatchers.is("A"), toUpper).matches("A"), CoreMatchers.is(true));
        Assert.assertThat(Matchers.compose(CoreMatchers.is("a"), toUpper).matches("A"), CoreMatchers.is(false));
        Assert.assertThat(UtilTest.describe(Matchers.compose(CoreMatchers.is("a"), toUpper)), CoreMatchers.is("is \"a\""));
        Assert.assertThat(UtilTest.mismatchDescription(Matchers.compose(CoreMatchers.is("a"), toUpper), "A"), CoreMatchers.is("was \"A\""));
    }

    /**
     * Unit test for {@link Matchers#isLinux}.
     */
    @Test
    public void testIsLinux() {
        Assert.assertThat("xy", Matchers.isLinux("xy"));
        Assert.assertThat("x\ny", Matchers.isLinux("x\ny"));
        Assert.assertThat("x\r\ny", Matchers.isLinux("x\ny"));
        Assert.assertThat(Matchers.isLinux("x").matches("x"), CoreMatchers.is(true));
        Assert.assertThat(Matchers.isLinux("X").matches("x"), CoreMatchers.is(false));
        Assert.assertThat(UtilTest.mismatchDescription(Matchers.isLinux("X"), "x"), CoreMatchers.is("was \"x\""));
        Assert.assertThat(UtilTest.describe(Matchers.isLinux("X")), CoreMatchers.is("is \"X\""));
        Assert.assertThat(Matchers.isLinux("x\ny").matches("x\ny"), CoreMatchers.is(true));
        Assert.assertThat(Matchers.isLinux("x\ny").matches("x\r\ny"), CoreMatchers.is(true));
        // \n\r is not a valid windows line ending
        Assert.assertThat(Matchers.isLinux("x\ny").matches("x\n\ry"), CoreMatchers.is(false));
        Assert.assertThat(Matchers.isLinux("x\ny").matches("x\n\ryz"), CoreMatchers.is(false));
        // left-hand side must be linux or will never match
        Assert.assertThat(Matchers.isLinux("x\r\ny").matches("x\r\ny"), CoreMatchers.is(false));
        Assert.assertThat(Matchers.isLinux("x\r\ny").matches("x\ny"), CoreMatchers.is(false));
    }

    /**
     * Tests {@link Util#transform(List, java.util.function.Function)}.
     */
    @Test
    public void testTransform() {
        final List<String> beatles = Arrays.asList("John", "Paul", "George", "Ringo");
        final List<String> empty = Collections.emptyList();
        Assert.assertThat(Util.transform(beatles, ( s) -> s.toUpperCase(Locale.ROOT)), CoreMatchers.is(Arrays.asList("JOHN", "PAUL", "GEORGE", "RINGO")));
        Assert.assertThat(Util.transform(empty, ( s) -> s.toUpperCase(Locale.ROOT)), CoreMatchers.is(empty));
        Assert.assertThat(Util.transform(beatles, String::length), CoreMatchers.is(Arrays.asList(4, 4, 6, 5)));
        Assert.assertThat(Util.transform(beatles, String::length), CoreMatchers.instanceOf(RandomAccess.class));
        final List<String> beatles2 = new LinkedList<>(beatles);
        Assert.assertThat(Util.transform(beatles2, String::length), CoreMatchers.not(CoreMatchers.instanceOf(RandomAccess.class)));
    }

    /**
     * Tests {@link Util#filter(Iterable, java.util.function.Predicate)}.
     */
    @Test
    public void testFilter() {
        final List<String> beatles = Arrays.asList("John", "Paul", "George", "Ringo");
        final List<String> empty = Collections.emptyList();
        final List<String> nullBeatles = Arrays.asList("John", "Paul", null, "Ringo");
        Assert.assertThat(Util.filter(beatles, ( s) -> (s.length()) == 4), UtilTest.isIterable(Arrays.asList("John", "Paul")));
        Assert.assertThat(Util.filter(empty, ( s) -> (s.length()) == 4), UtilTest.isIterable(empty));
        Assert.assertThat(Util.filter(empty, ( s) -> false), UtilTest.isIterable(empty));
        Assert.assertThat(Util.filter(empty, ( s) -> true), UtilTest.isIterable(empty));
        Assert.assertThat(Util.filter(beatles, ( s) -> false), UtilTest.isIterable(empty));
        Assert.assertThat(Util.filter(beatles, ( s) -> true), UtilTest.isIterable(beatles));
        Assert.assertThat(Util.filter(nullBeatles, ( s) -> false), UtilTest.isIterable(empty));
        Assert.assertThat(Util.filter(nullBeatles, ( s) -> true), UtilTest.isIterable(nullBeatles));
        Assert.assertThat(Util.filter(nullBeatles, Objects::isNull), UtilTest.isIterable(Collections.singletonList(null)));
        Assert.assertThat(Util.filter(nullBeatles, Objects::nonNull), UtilTest.isIterable(Arrays.asList("John", "Paul", "Ringo")));
    }

    @Test
    public void testEquivalenceSet() {
        final EquivalenceSet<String> c = new EquivalenceSet();
        Assert.assertThat(c.size(), CoreMatchers.is(0));
        Assert.assertThat(c.classCount(), CoreMatchers.is(0));
        c.add("abc");
        Assert.assertThat(c.size(), CoreMatchers.is(1));
        Assert.assertThat(c.classCount(), CoreMatchers.is(1));
        c.add("Abc");
        Assert.assertThat(c.size(), CoreMatchers.is(2));
        Assert.assertThat(c.classCount(), CoreMatchers.is(2));
        Assert.assertThat(c.areEquivalent("abc", "Abc"), CoreMatchers.is(false));
        Assert.assertThat(c.areEquivalent("abc", "abc"), CoreMatchers.is(true));
        Assert.assertThat(c.areEquivalent("abc", "ABC"), CoreMatchers.is(false));
        c.equiv("abc", "ABC");
        Assert.assertThat(c.size(), CoreMatchers.is(3));
        Assert.assertThat(c.classCount(), CoreMatchers.is(2));
        Assert.assertThat(c.areEquivalent("abc", "ABC"), CoreMatchers.is(true));
        Assert.assertThat(c.areEquivalent("ABC", "abc"), CoreMatchers.is(true));
        Assert.assertThat(c.areEquivalent("abc", "abc"), CoreMatchers.is(true));
        Assert.assertThat(c.areEquivalent("abc", "Abc"), CoreMatchers.is(false));
        c.equiv("Abc", "ABC");
        Assert.assertThat(c.size(), CoreMatchers.is(3));
        Assert.assertThat(c.classCount(), CoreMatchers.is(1));
        Assert.assertThat(c.areEquivalent("abc", "Abc"), CoreMatchers.is(true));
        c.add("de");
        c.equiv("fg", "fG");
        Assert.assertThat(c.size(), CoreMatchers.is(6));
        Assert.assertThat(c.classCount(), CoreMatchers.is(3));
        final SortedMap<String, SortedSet<String>> map = c.map();
        Assert.assertThat(map.toString(), CoreMatchers.is("{ABC=[ABC, Abc, abc], de=[de], fG=[fG, fg]}"));
        c.clear();
        Assert.assertThat(c.size(), CoreMatchers.is(0));
        Assert.assertThat(c.classCount(), CoreMatchers.is(0));
    }
}

/**
 * End UtilTest.java
 */
