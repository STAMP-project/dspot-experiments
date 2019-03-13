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
package org.apache.calcite.test;


import ByteString.EMPTY;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.calcite.avatica.util.ByteString;
import org.apache.calcite.runtime.CalciteException;
import org.apache.calcite.runtime.SqlFunctions;
import org.apache.calcite.runtime.Utilities;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for the methods in {@link SqlFunctions} that implement SQL
 * functions.
 */
public class SqlFunctionsTest {
    @Test
    public void testCharLength() {
        Assert.assertEquals(3, SqlFunctions.charLength("xyz"));
    }

    @Test
    public void testConcat() {
        Assert.assertEquals("a bcd", SqlFunctions.concat("a b", "cd"));
        // The code generator will ensure that nulls are never passed in. If we
        // pass in null, it is treated like the string "null", as the following
        // tests show. Not the desired behavior for SQL.
        Assert.assertEquals("anull", SqlFunctions.concat("a", null));
        Assert.assertEquals("nullnull", SqlFunctions.concat(((String) (null)), null));
        Assert.assertEquals("nullb", SqlFunctions.concat(null, "b"));
    }

    @Test
    public void testLower() {
        Assert.assertEquals("a bcd iijk", SqlFunctions.lower("A bCd Iijk"));
    }

    @Test
    public void testUpper() {
        Assert.assertEquals("A BCD IIJK", SqlFunctions.upper("A bCd iIjk"));
    }

    @Test
    public void testInitcap() {
        Assert.assertEquals("Aa", SqlFunctions.initcap("aA"));
        Assert.assertEquals("Zz", SqlFunctions.initcap("zz"));
        Assert.assertEquals("Az", SqlFunctions.initcap("AZ"));
        Assert.assertEquals("Try A Little  ", SqlFunctions.initcap("tRy a littlE  "));
        Assert.assertEquals("Won'T It?No", SqlFunctions.initcap("won't it?no"));
        Assert.assertEquals("1a", SqlFunctions.initcap("1A"));
        Assert.assertEquals(" B0123b", SqlFunctions.initcap(" b0123B"));
    }

    @Test
    public void testLesser() {
        Assert.assertEquals("a", SqlFunctions.lesser("a", "bc"));
        Assert.assertEquals("ac", SqlFunctions.lesser("bc", "ac"));
        try {
            Object o = SqlFunctions.lesser("a", null);
            Assert.fail(("Expected NPE, got " + o));
        } catch (NullPointerException e) {
            // ok
        }
        Assert.assertEquals("a", SqlFunctions.lesser(null, "a"));
        Assert.assertNull(SqlFunctions.lesser(((String) (null)), null));
    }

    @Test
    public void testGreater() {
        Assert.assertEquals("bc", SqlFunctions.greater("a", "bc"));
        Assert.assertEquals("bc", SqlFunctions.greater("bc", "ac"));
        try {
            Object o = SqlFunctions.greater("a", null);
            Assert.fail(("Expected NPE, got " + o));
        } catch (NullPointerException e) {
            // ok
        }
        Assert.assertEquals("a", SqlFunctions.greater(null, "a"));
        Assert.assertNull(SqlFunctions.greater(((String) (null)), null));
    }

    /**
     * Test for {@link SqlFunctions#rtrim}.
     */
    @Test
    public void testRtrim() {
        Assert.assertEquals("", SqlFunctions.rtrim(""));
        Assert.assertEquals("", SqlFunctions.rtrim("    "));
        Assert.assertEquals("   x", SqlFunctions.rtrim("   x  "));
        Assert.assertEquals("   x", SqlFunctions.rtrim("   x "));
        Assert.assertEquals("   x y", SqlFunctions.rtrim("   x y "));
        Assert.assertEquals("   x", SqlFunctions.rtrim("   x"));
        Assert.assertEquals("x", SqlFunctions.rtrim("x"));
    }

    /**
     * Test for {@link SqlFunctions#ltrim}.
     */
    @Test
    public void testLtrim() {
        Assert.assertEquals("", SqlFunctions.ltrim(""));
        Assert.assertEquals("", SqlFunctions.ltrim("    "));
        Assert.assertEquals("x  ", SqlFunctions.ltrim("   x  "));
        Assert.assertEquals("x ", SqlFunctions.ltrim("   x "));
        Assert.assertEquals("x y ", SqlFunctions.ltrim("x y "));
        Assert.assertEquals("x", SqlFunctions.ltrim("   x"));
        Assert.assertEquals("x", SqlFunctions.ltrim("x"));
    }

    /**
     * Test for {@link SqlFunctions#trim}.
     */
    @Test
    public void testTrim() {
        Assert.assertEquals("", SqlFunctionsTest.trimSpacesBoth(""));
        Assert.assertEquals("", SqlFunctionsTest.trimSpacesBoth("    "));
        Assert.assertEquals("x", SqlFunctionsTest.trimSpacesBoth("   x  "));
        Assert.assertEquals("x", SqlFunctionsTest.trimSpacesBoth("   x "));
        Assert.assertEquals("x y", SqlFunctionsTest.trimSpacesBoth("   x y "));
        Assert.assertEquals("x", SqlFunctionsTest.trimSpacesBoth("   x"));
        Assert.assertEquals("x", SqlFunctionsTest.trimSpacesBoth("x"));
    }

    @Test
    public void testAddMonths() {
        checkAddMonths(2016, 1, 1, 2016, 2, 1, 1);
        checkAddMonths(2016, 1, 1, 2017, 1, 1, 12);
        checkAddMonths(2016, 1, 1, 2017, 2, 1, 13);
        checkAddMonths(2016, 1, 1, 2015, 1, 1, (-12));
        checkAddMonths(2016, 1, 1, 2018, 10, 1, 33);
        checkAddMonths(2016, 1, 31, 2016, 4, 30, 3);
        checkAddMonths(2016, 4, 30, 2016, 7, 30, 3);
        checkAddMonths(2016, 1, 31, 2016, 2, 29, 1);
        checkAddMonths(2016, 3, 31, 2016, 2, 29, (-1));
        checkAddMonths(2016, 3, 31, 2116, 3, 31, 1200);
        checkAddMonths(2016, 2, 28, 2116, 2, 28, 1200);
    }

    @Test
    public void testFloor() {
        checkFloor(0, 10, 0);
        checkFloor(27, 10, 20);
        checkFloor(30, 10, 30);
        checkFloor((-30), 10, (-30));
        checkFloor((-27), 10, (-30));
    }

    @Test
    public void testCeil() {
        checkCeil(0, 10, 0);
        checkCeil(27, 10, 30);
        checkCeil(30, 10, 30);
        checkCeil((-30), 10, (-30));
        checkCeil((-27), 10, (-20));
        checkCeil((-27), 1, (-27));
    }

    /**
     * Unit test for
     * {@link Utilities#compare(java.util.List, java.util.List)}.
     */
    @Test
    public void testCompare() {
        final List<String> ac = Arrays.asList("a", "c");
        final List<String> abc = Arrays.asList("a", "b", "c");
        final List<String> a = Collections.singletonList("a");
        final List<String> empty = Collections.emptyList();
        Assert.assertEquals(0, Utilities.compare(ac, ac));
        Assert.assertEquals(0, Utilities.compare(ac, new ArrayList(ac)));
        Assert.assertEquals((-1), Utilities.compare(a, ac));
        Assert.assertEquals((-1), Utilities.compare(empty, ac));
        Assert.assertEquals(1, Utilities.compare(ac, a));
        Assert.assertEquals(1, Utilities.compare(ac, abc));
        Assert.assertEquals(1, Utilities.compare(ac, empty));
        Assert.assertEquals(0, Utilities.compare(empty, empty));
    }

    @Test
    public void testTruncateLong() {
        Assert.assertEquals(12000L, SqlFunctions.truncate(12345L, 1000L));
        Assert.assertEquals(12000L, SqlFunctions.truncate(12000L, 1000L));
        Assert.assertEquals(12000L, SqlFunctions.truncate(12001L, 1000L));
        Assert.assertEquals(11000L, SqlFunctions.truncate(11999L, 1000L));
        Assert.assertEquals((-13000L), SqlFunctions.truncate((-12345L), 1000L));
        Assert.assertEquals((-12000L), SqlFunctions.truncate((-12000L), 1000L));
        Assert.assertEquals((-13000L), SqlFunctions.truncate((-12001L), 1000L));
        Assert.assertEquals((-12000L), SqlFunctions.truncate((-11999L), 1000L));
    }

    @Test
    public void testTruncateInt() {
        Assert.assertEquals(12000, SqlFunctions.truncate(12345, 1000));
        Assert.assertEquals(12000, SqlFunctions.truncate(12000, 1000));
        Assert.assertEquals(12000, SqlFunctions.truncate(12001, 1000));
        Assert.assertEquals(11000, SqlFunctions.truncate(11999, 1000));
        Assert.assertEquals((-13000), SqlFunctions.truncate((-12345), 1000));
        Assert.assertEquals((-12000), SqlFunctions.truncate((-12000), 1000));
        Assert.assertEquals((-13000), SqlFunctions.truncate((-12001), 1000));
        Assert.assertEquals((-12000), SqlFunctions.truncate((-11999), 1000));
        Assert.assertEquals(12000, SqlFunctions.round(12345, 1000));
        Assert.assertEquals(13000, SqlFunctions.round(12845, 1000));
        Assert.assertEquals((-12000), SqlFunctions.round((-12345), 1000));
        Assert.assertEquals((-13000), SqlFunctions.round((-12845), 1000));
    }

    @Test
    public void testSTruncateDouble() {
        Assert.assertEquals(12.345, SqlFunctions.struncate(12.345, 3), 0.001);
        Assert.assertEquals(12.34, SqlFunctions.struncate(12.345, 2), 0.001);
        Assert.assertEquals(12.3, SqlFunctions.struncate(12.345, 1), 0.001);
        Assert.assertEquals(12.0, SqlFunctions.struncate(12.999, 0), 0.001);
        Assert.assertEquals((-12.345), SqlFunctions.struncate((-12.345), 3), 0.001);
        Assert.assertEquals((-12.34), SqlFunctions.struncate((-12.345), 2), 0.001);
        Assert.assertEquals((-12.3), SqlFunctions.struncate((-12.345), 1), 0.001);
        Assert.assertEquals((-12.0), SqlFunctions.struncate((-12.999), 0), 0.001);
        Assert.assertEquals(12000.0, SqlFunctions.struncate(12345.0, (-3)), 0.001);
        Assert.assertEquals(12000.0, SqlFunctions.struncate(12000.0, (-3)), 0.001);
        Assert.assertEquals(12000.0, SqlFunctions.struncate(12001.0, (-3)), 0.001);
        Assert.assertEquals(10000.0, SqlFunctions.struncate(12000.0, (-4)), 0.001);
        Assert.assertEquals(0.0, SqlFunctions.struncate(12000.0, (-5)), 0.001);
        Assert.assertEquals(11000.0, SqlFunctions.struncate(11999.0, (-3)), 0.001);
        Assert.assertEquals((-12000.0), SqlFunctions.struncate((-12345.0), (-3)), 0.001);
        Assert.assertEquals((-12000.0), SqlFunctions.struncate((-12000.0), (-3)), 0.001);
        Assert.assertEquals((-11000.0), SqlFunctions.struncate((-11999.0), (-3)), 0.001);
        Assert.assertEquals((-10000.0), SqlFunctions.struncate((-12000.0), (-4)), 0.001);
        Assert.assertEquals(0.0, SqlFunctions.struncate((-12000.0), (-5)), 0.001);
    }

    @Test
    public void testSTruncateLong() {
        Assert.assertEquals(12000.0, SqlFunctions.struncate(12345L, (-3)), 0.001);
        Assert.assertEquals(12000.0, SqlFunctions.struncate(12000L, (-3)), 0.001);
        Assert.assertEquals(12000.0, SqlFunctions.struncate(12001L, (-3)), 0.001);
        Assert.assertEquals(10000.0, SqlFunctions.struncate(12000L, (-4)), 0.001);
        Assert.assertEquals(0.0, SqlFunctions.struncate(12000L, (-5)), 0.001);
        Assert.assertEquals(11000.0, SqlFunctions.struncate(11999L, (-3)), 0.001);
        Assert.assertEquals((-12000.0), SqlFunctions.struncate((-12345L), (-3)), 0.001);
        Assert.assertEquals((-12000.0), SqlFunctions.struncate((-12000L), (-3)), 0.001);
        Assert.assertEquals((-11000.0), SqlFunctions.struncate((-11999L), (-3)), 0.001);
        Assert.assertEquals((-10000.0), SqlFunctions.struncate((-12000L), (-4)), 0.001);
        Assert.assertEquals(0.0, SqlFunctions.struncate((-12000L), (-5)), 0.001);
    }

    @Test
    public void testSTruncateInt() {
        Assert.assertEquals(12000.0, SqlFunctions.struncate(12345, (-3)), 0.001);
        Assert.assertEquals(12000.0, SqlFunctions.struncate(12000, (-3)), 0.001);
        Assert.assertEquals(12000.0, SqlFunctions.struncate(12001, (-3)), 0.001);
        Assert.assertEquals(10000.0, SqlFunctions.struncate(12000, (-4)), 0.001);
        Assert.assertEquals(0.0, SqlFunctions.struncate(12000, (-5)), 0.001);
        Assert.assertEquals(11000.0, SqlFunctions.struncate(11999, (-3)), 0.001);
        Assert.assertEquals((-12000.0), SqlFunctions.struncate((-12345), (-3)), 0.001);
        Assert.assertEquals((-12000.0), SqlFunctions.struncate((-12000), (-3)), 0.001);
        Assert.assertEquals((-11000.0), SqlFunctions.struncate((-11999), (-3)), 0.001);
        Assert.assertEquals((-10000.0), SqlFunctions.struncate((-12000), (-4)), 0.001);
        Assert.assertEquals(0.0, SqlFunctions.struncate((-12000), (-5)), 0.001);
    }

    @Test
    public void testSRoundDouble() {
        Assert.assertEquals(12.345, SqlFunctions.sround(12.345, 3), 0.001);
        Assert.assertEquals(12.35, SqlFunctions.sround(12.345, 2), 0.001);
        Assert.assertEquals(12.3, SqlFunctions.sround(12.345, 1), 0.001);
        Assert.assertEquals(13.0, SqlFunctions.sround(12.999, 2), 0.001);
        Assert.assertEquals(13.0, SqlFunctions.sround(12.999, 1), 0.001);
        Assert.assertEquals(13.0, SqlFunctions.sround(12.999, 0), 0.001);
        Assert.assertEquals((-12.345), SqlFunctions.sround((-12.345), 3), 0.001);
        Assert.assertEquals((-12.35), SqlFunctions.sround((-12.345), 2), 0.001);
        Assert.assertEquals((-12.3), SqlFunctions.sround((-12.345), 1), 0.001);
        Assert.assertEquals((-13.0), SqlFunctions.sround((-12.999), 2), 0.001);
        Assert.assertEquals((-13.0), SqlFunctions.sround((-12.999), 1), 0.001);
        Assert.assertEquals((-13.0), SqlFunctions.sround((-12.999), 0), 0.001);
        Assert.assertEquals(12350.0, SqlFunctions.sround(12345.0, (-1)), 0.001);
        Assert.assertEquals(12300.0, SqlFunctions.sround(12345.0, (-2)), 0.001);
        Assert.assertEquals(12000.0, SqlFunctions.sround(12345.0, (-3)), 0.001);
        Assert.assertEquals(12000.0, SqlFunctions.sround(12000.0, (-3)), 0.001);
        Assert.assertEquals(12000.0, SqlFunctions.sround(12001.0, (-3)), 0.001);
        Assert.assertEquals(10000.0, SqlFunctions.sround(12000.0, (-4)), 0.001);
        Assert.assertEquals(0.0, SqlFunctions.sround(12000.0, (-5)), 0.001);
        Assert.assertEquals(12000.0, SqlFunctions.sround(11999.0, (-3)), 0.001);
        Assert.assertEquals((-12350.0), SqlFunctions.sround((-12345.0), (-1)), 0.001);
        Assert.assertEquals((-12300.0), SqlFunctions.sround((-12345.0), (-2)), 0.001);
        Assert.assertEquals((-12000.0), SqlFunctions.sround((-12345.0), (-3)), 0.001);
        Assert.assertEquals((-12000.0), SqlFunctions.sround((-12000.0), (-3)), 0.001);
        Assert.assertEquals((-12000.0), SqlFunctions.sround((-11999.0), (-3)), 0.001);
        Assert.assertEquals((-10000.0), SqlFunctions.sround((-12000.0), (-4)), 0.001);
        Assert.assertEquals(0.0, SqlFunctions.sround((-12000.0), (-5)), 0.001);
    }

    @Test
    public void testSRoundLong() {
        Assert.assertEquals(12350.0, SqlFunctions.sround(12345L, (-1)), 0.001);
        Assert.assertEquals(12300.0, SqlFunctions.sround(12345L, (-2)), 0.001);
        Assert.assertEquals(12000.0, SqlFunctions.sround(12345L, (-3)), 0.001);
        Assert.assertEquals(12000.0, SqlFunctions.sround(12000L, (-3)), 0.001);
        Assert.assertEquals(12000.0, SqlFunctions.sround(12001L, (-3)), 0.001);
        Assert.assertEquals(10000.0, SqlFunctions.sround(12000L, (-4)), 0.001);
        Assert.assertEquals(0.0, SqlFunctions.sround(12000L, (-5)), 0.001);
        Assert.assertEquals(12000.0, SqlFunctions.sround(11999L, (-3)), 0.001);
        Assert.assertEquals((-12350.0), SqlFunctions.sround((-12345L), (-1)), 0.001);
        Assert.assertEquals((-12300.0), SqlFunctions.sround((-12345L), (-2)), 0.001);
        Assert.assertEquals((-12000.0), SqlFunctions.sround((-12345L), (-3)), 0.001);
        Assert.assertEquals((-12000.0), SqlFunctions.sround((-12000L), (-3)), 0.001);
        Assert.assertEquals((-12000.0), SqlFunctions.sround((-11999L), (-3)), 0.001);
        Assert.assertEquals((-10000.0), SqlFunctions.sround((-12000L), (-4)), 0.001);
        Assert.assertEquals(0.0, SqlFunctions.sround((-12000L), (-5)), 0.001);
    }

    @Test
    public void testSRoundInt() {
        Assert.assertEquals(12350.0, SqlFunctions.sround(12345, (-1)), 0.001);
        Assert.assertEquals(12300.0, SqlFunctions.sround(12345, (-2)), 0.001);
        Assert.assertEquals(12000.0, SqlFunctions.sround(12345, (-3)), 0.001);
        Assert.assertEquals(12000.0, SqlFunctions.sround(12000, (-3)), 0.001);
        Assert.assertEquals(12000.0, SqlFunctions.sround(12001, (-3)), 0.001);
        Assert.assertEquals(10000.0, SqlFunctions.sround(12000, (-4)), 0.001);
        Assert.assertEquals(0.0, SqlFunctions.sround(12000, (-5)), 0.001);
        Assert.assertEquals(12000.0, SqlFunctions.sround(11999, (-3)), 0.001);
        Assert.assertEquals((-12350.0), SqlFunctions.sround((-12345), (-1)), 0.001);
        Assert.assertEquals((-12300.0), SqlFunctions.sround((-12345), (-2)), 0.001);
        Assert.assertEquals((-12000.0), SqlFunctions.sround((-12345), (-3)), 0.001);
        Assert.assertEquals((-12000.0), SqlFunctions.sround((-12000), (-3)), 0.001);
        Assert.assertEquals((-12000.0), SqlFunctions.sround((-11999), (-3)), 0.001);
        Assert.assertEquals((-10000.0), SqlFunctions.sround((-12000), (-4)), 0.001);
        Assert.assertEquals(0.0, SqlFunctions.sround((-12000), (-5)), 0.001);
    }

    @Test
    public void testByteString() {
        final byte[] bytes = new byte[]{ ((byte) (171)), ((byte) (255)) };
        final ByteString byteString = new ByteString(bytes);
        Assert.assertEquals(2, byteString.length());
        Assert.assertEquals("abff", byteString.toString());
        Assert.assertEquals("abff", byteString.toString(16));
        Assert.assertEquals("1010101111111111", byteString.toString(2));
        final ByteString emptyByteString = new ByteString(new byte[0]);
        Assert.assertEquals(0, emptyByteString.length());
        Assert.assertEquals("", emptyByteString.toString());
        Assert.assertEquals("", emptyByteString.toString(16));
        Assert.assertEquals("", emptyByteString.toString(2));
        Assert.assertEquals(emptyByteString, EMPTY);
        Assert.assertEquals("ff", byteString.substring(1, 2).toString());
        Assert.assertEquals("abff", byteString.substring(0, 2).toString());
        Assert.assertEquals("", byteString.substring(2, 2).toString());
        // Add empty string, get original string back
        Assert.assertSame(byteString.concat(emptyByteString), byteString);
        final ByteString byteString1 = new ByteString(new byte[]{ ((byte) (12)) });
        Assert.assertEquals("abff0c", byteString.concat(byteString1).toString());
        final byte[] bytes3 = new byte[]{ ((byte) (255)) };
        final ByteString byteString3 = new ByteString(bytes3);
        Assert.assertEquals(0, byteString.indexOf(emptyByteString));
        Assert.assertEquals((-1), byteString.indexOf(byteString1));
        Assert.assertEquals(1, byteString.indexOf(byteString3));
        Assert.assertEquals((-1), byteString3.indexOf(byteString));
        thereAndBack(bytes);
        thereAndBack(emptyByteString.getBytes());
        thereAndBack(new byte[]{ 10, 0, 29, -80 });
        Assert.assertThat(ByteString.of("ab12", 16).toString(16), CoreMatchers.equalTo("ab12"));
        Assert.assertThat(ByteString.of("AB0001DdeAD3", 16).toString(16), CoreMatchers.equalTo("ab0001ddead3"));
        Assert.assertThat(ByteString.of("", 16), CoreMatchers.equalTo(emptyByteString));
        try {
            ByteString x = ByteString.of("ABg0", 16);
            Assert.fail(("expected error, got " + x));
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.equalTo("invalid hex character: g"));
        }
        try {
            ByteString x = ByteString.of("ABC", 16);
            Assert.fail(("expected error, got " + x));
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.equalTo("hex string has odd length"));
        }
        final byte[] bytes4 = new byte[]{ 10, 0, 1, -80 };
        final ByteString byteString4 = new ByteString(bytes4);
        final byte[] bytes5 = new byte[]{ 10, 0, 1, 127 };
        final ByteString byteString5 = new ByteString(bytes5);
        final ByteString byteString6 = new ByteString(bytes4);
        Assert.assertThat(((byteString4.compareTo(byteString5)) > 0), CoreMatchers.is(true));
        Assert.assertThat(((byteString4.compareTo(byteString6)) == 0), CoreMatchers.is(true));
        Assert.assertThat(((byteString5.compareTo(byteString4)) < 0), CoreMatchers.is(true));
    }

    @Test
    public void testEqWithAny() {
        // Non-numeric same type equality check
        Assert.assertThat(SqlFunctions.eqAny("hello", "hello"), CoreMatchers.is(true));
        // Numeric types equality check
        Assert.assertThat(SqlFunctions.eqAny(1, 1L), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.eqAny(1, 1.0), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.eqAny(1L, 1.0), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.eqAny(new BigDecimal(1L), 1), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.eqAny(new BigDecimal(1L), 1L), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.eqAny(new BigDecimal(1L), 1.0), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.eqAny(new BigDecimal(1L), new BigDecimal(1.0)), CoreMatchers.is(true));
        // Non-numeric different type equality check
        Assert.assertThat(SqlFunctions.eqAny("2", 2), CoreMatchers.is(false));
    }

    @Test
    public void testNeWithAny() {
        // Non-numeric same type inequality check
        Assert.assertThat(SqlFunctions.neAny("hello", "world"), CoreMatchers.is(true));
        // Numeric types inequality check
        Assert.assertThat(SqlFunctions.neAny(1, 2L), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.neAny(1, 2.0), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.neAny(1L, 2.0), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.neAny(new BigDecimal(2L), 1), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.neAny(new BigDecimal(2L), 1L), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.neAny(new BigDecimal(2L), 1.0), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.neAny(new BigDecimal(2L), new BigDecimal(1.0)), CoreMatchers.is(true));
        // Non-numeric different type inequality check
        Assert.assertThat(SqlFunctions.neAny("2", 2), CoreMatchers.is(true));
    }

    @Test
    public void testLtWithAny() {
        // Non-numeric same type "less then" check
        Assert.assertThat(SqlFunctions.ltAny("apple", "banana"), CoreMatchers.is(true));
        // Numeric types "less than" check
        Assert.assertThat(SqlFunctions.ltAny(1, 2L), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.ltAny(1, 2.0), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.ltAny(1L, 2.0), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.ltAny(new BigDecimal(1L), 2), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.ltAny(new BigDecimal(1L), 2L), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.ltAny(new BigDecimal(1L), 2.0), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.ltAny(new BigDecimal(1L), new BigDecimal(2.0)), CoreMatchers.is(true));
        // Non-numeric different type but both implements Comparable
        // "less than" check
        try {
            Assert.assertThat(SqlFunctions.ltAny("1", 2L), CoreMatchers.is(false));
            Assert.fail("'lt' on non-numeric different type is not possible");
        } catch (CalciteException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.is(("Invalid types for comparison: class java.lang.String < " + "class java.lang.Long")));
        }
    }

    @Test
    public void testLeWithAny() {
        // Non-numeric same type "less or equal" check
        Assert.assertThat(SqlFunctions.leAny("apple", "banana"), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.leAny("apple", "apple"), CoreMatchers.is(true));
        // Numeric types "less or equal" check
        Assert.assertThat(SqlFunctions.leAny(1, 2L), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.leAny(1, 1L), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.leAny(1, 2.0), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.leAny(1, 1.0), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.leAny(1L, 2.0), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.leAny(1L, 1.0), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.leAny(new BigDecimal(1L), 2), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.leAny(new BigDecimal(1L), 1), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.leAny(new BigDecimal(1L), 2L), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.leAny(new BigDecimal(1L), 1L), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.leAny(new BigDecimal(1L), 2.0), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.leAny(new BigDecimal(1L), 1.0), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.leAny(new BigDecimal(1L), new BigDecimal(2.0)), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.leAny(new BigDecimal(1L), new BigDecimal(1.0)), CoreMatchers.is(true));
        // Non-numeric different type but both implements Comparable
        // "less or equal" check
        try {
            Assert.assertThat(SqlFunctions.leAny("2", 2L), CoreMatchers.is(false));
            Assert.fail("'le' on non-numeric different type is not possible");
        } catch (CalciteException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.is(("Invalid types for comparison: class java.lang.String <= " + "class java.lang.Long")));
        }
    }

    @Test
    public void testGtWithAny() {
        // Non-numeric same type "greater then" check
        Assert.assertThat(SqlFunctions.gtAny("banana", "apple"), CoreMatchers.is(true));
        // Numeric types "greater than" check
        Assert.assertThat(SqlFunctions.gtAny(2, 1L), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.gtAny(2, 1.0), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.gtAny(2L, 1.0), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.gtAny(new BigDecimal(2L), 1), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.gtAny(new BigDecimal(2L), 1L), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.gtAny(new BigDecimal(2L), 1.0), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.gtAny(new BigDecimal(2L), new BigDecimal(1.0)), CoreMatchers.is(true));
        // Non-numeric different type but both implements Comparable
        // "greater than" check
        try {
            Assert.assertThat(SqlFunctions.gtAny("2", 1L), CoreMatchers.is(false));
            Assert.fail("'gt' on non-numeric different type is not possible");
        } catch (CalciteException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.is(("Invalid types for comparison: class java.lang.String > " + "class java.lang.Long")));
        }
    }

    @Test
    public void testGeWithAny() {
        // Non-numeric same type "greater or equal" check
        Assert.assertThat(SqlFunctions.geAny("banana", "apple"), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.geAny("apple", "apple"), CoreMatchers.is(true));
        // Numeric types "greater or equal" check
        Assert.assertThat(SqlFunctions.geAny(2, 1L), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.geAny(1, 1L), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.geAny(2, 1.0), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.geAny(1, 1.0), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.geAny(2L, 1.0), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.geAny(1L, 1.0), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.geAny(new BigDecimal(2L), 1), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.geAny(new BigDecimal(1L), 1), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.geAny(new BigDecimal(2L), 1L), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.geAny(new BigDecimal(1L), 1L), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.geAny(new BigDecimal(2L), 1.0), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.geAny(new BigDecimal(1L), 1.0), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.geAny(new BigDecimal(2L), new BigDecimal(1.0)), CoreMatchers.is(true));
        Assert.assertThat(SqlFunctions.geAny(new BigDecimal(1L), new BigDecimal(1.0)), CoreMatchers.is(true));
        // Non-numeric different type but both implements Comparable
        // "greater or equal" check
        try {
            Assert.assertThat(SqlFunctions.geAny("2", 2L), CoreMatchers.is(false));
            Assert.fail("'ge' on non-numeric different type is not possible");
        } catch (CalciteException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.is(("Invalid types for comparison: class java.lang.String >= " + "class java.lang.Long")));
        }
    }

    @Test
    public void testPlusAny() {
        // null parameters
        Assert.assertNull(SqlFunctions.plusAny(null, null));
        Assert.assertNull(SqlFunctions.plusAny(null, 1));
        Assert.assertNull(SqlFunctions.plusAny(1, null));
        // Numeric types
        Assert.assertThat(SqlFunctions.plusAny(2, 1L), CoreMatchers.is(((Object) (new BigDecimal(3)))));
        Assert.assertThat(SqlFunctions.plusAny(2, 1.0), CoreMatchers.is(((Object) (new BigDecimal(3)))));
        Assert.assertThat(SqlFunctions.plusAny(2L, 1.0), CoreMatchers.is(((Object) (new BigDecimal(3)))));
        Assert.assertThat(SqlFunctions.plusAny(new BigDecimal(2L), 1), CoreMatchers.is(((Object) (new BigDecimal(3)))));
        Assert.assertThat(SqlFunctions.plusAny(new BigDecimal(2L), 1L), CoreMatchers.is(((Object) (new BigDecimal(3)))));
        Assert.assertThat(SqlFunctions.plusAny(new BigDecimal(2L), 1.0), CoreMatchers.is(((Object) (new BigDecimal(3)))));
        Assert.assertThat(SqlFunctions.plusAny(new BigDecimal(2L), new BigDecimal(1.0)), CoreMatchers.is(((Object) (new BigDecimal(3)))));
        // Non-numeric type
        try {
            SqlFunctions.plusAny("2", 2L);
            Assert.fail("'plus' on non-numeric type is not possible");
        } catch (CalciteException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.is(("Invalid types for arithmetic: class java.lang.String + " + "class java.lang.Long")));
        }
    }

    @Test
    public void testMinusAny() {
        // null parameters
        Assert.assertNull(SqlFunctions.minusAny(null, null));
        Assert.assertNull(SqlFunctions.minusAny(null, 1));
        Assert.assertNull(SqlFunctions.minusAny(1, null));
        // Numeric types
        Assert.assertThat(SqlFunctions.minusAny(2, 1L), CoreMatchers.is(((Object) (new BigDecimal(1)))));
        Assert.assertThat(SqlFunctions.minusAny(2, 1.0), CoreMatchers.is(((Object) (new BigDecimal(1)))));
        Assert.assertThat(SqlFunctions.minusAny(2L, 1.0), CoreMatchers.is(((Object) (new BigDecimal(1)))));
        Assert.assertThat(SqlFunctions.minusAny(new BigDecimal(2L), 1), CoreMatchers.is(((Object) (new BigDecimal(1)))));
        Assert.assertThat(SqlFunctions.minusAny(new BigDecimal(2L), 1L), CoreMatchers.is(((Object) (new BigDecimal(1)))));
        Assert.assertThat(SqlFunctions.minusAny(new BigDecimal(2L), 1.0), CoreMatchers.is(((Object) (new BigDecimal(1)))));
        Assert.assertThat(SqlFunctions.minusAny(new BigDecimal(2L), new BigDecimal(1.0)), CoreMatchers.is(((Object) (new BigDecimal(1)))));
        // Non-numeric type
        try {
            SqlFunctions.minusAny("2", 2L);
            Assert.fail("'minus' on non-numeric type is not possible");
        } catch (CalciteException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.is(("Invalid types for arithmetic: class java.lang.String - " + "class java.lang.Long")));
        }
    }

    @Test
    public void testMultiplyAny() {
        // null parameters
        Assert.assertNull(SqlFunctions.multiplyAny(null, null));
        Assert.assertNull(SqlFunctions.multiplyAny(null, 1));
        Assert.assertNull(SqlFunctions.multiplyAny(1, null));
        // Numeric types
        Assert.assertThat(SqlFunctions.multiplyAny(2, 1L), CoreMatchers.is(((Object) (new BigDecimal(2)))));
        Assert.assertThat(SqlFunctions.multiplyAny(2, 1.0), CoreMatchers.is(((Object) (new BigDecimal(2)))));
        Assert.assertThat(SqlFunctions.multiplyAny(2L, 1.0), CoreMatchers.is(((Object) (new BigDecimal(2)))));
        Assert.assertThat(SqlFunctions.multiplyAny(new BigDecimal(2L), 1), CoreMatchers.is(((Object) (new BigDecimal(2)))));
        Assert.assertThat(SqlFunctions.multiplyAny(new BigDecimal(2L), 1L), CoreMatchers.is(((Object) (new BigDecimal(2)))));
        Assert.assertThat(SqlFunctions.multiplyAny(new BigDecimal(2L), 1.0), CoreMatchers.is(((Object) (new BigDecimal(2)))));
        Assert.assertThat(SqlFunctions.multiplyAny(new BigDecimal(2L), new BigDecimal(1.0)), CoreMatchers.is(((Object) (new BigDecimal(2)))));
        // Non-numeric type
        try {
            SqlFunctions.multiplyAny("2", 2L);
            Assert.fail("'multiply' on non-numeric type is not possible");
        } catch (CalciteException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.is(("Invalid types for arithmetic: class java.lang.String * " + "class java.lang.Long")));
        }
    }

    @Test
    public void testDivideAny() {
        // null parameters
        Assert.assertNull(SqlFunctions.divideAny(null, null));
        Assert.assertNull(SqlFunctions.divideAny(null, 1));
        Assert.assertNull(SqlFunctions.divideAny(1, null));
        // Numeric types
        Assert.assertThat(SqlFunctions.divideAny(5, 2L), CoreMatchers.is(((Object) (new BigDecimal("2.5")))));
        Assert.assertThat(SqlFunctions.divideAny(5, 2.0), CoreMatchers.is(((Object) (new BigDecimal("2.5")))));
        Assert.assertThat(SqlFunctions.divideAny(5L, 2.0), CoreMatchers.is(((Object) (new BigDecimal("2.5")))));
        Assert.assertThat(SqlFunctions.divideAny(new BigDecimal(5L), 2), CoreMatchers.is(((Object) (new BigDecimal(2.5)))));
        Assert.assertThat(SqlFunctions.divideAny(new BigDecimal(5L), 2L), CoreMatchers.is(((Object) (new BigDecimal(2.5)))));
        Assert.assertThat(SqlFunctions.divideAny(new BigDecimal(5L), 2.0), CoreMatchers.is(((Object) (new BigDecimal(2.5)))));
        Assert.assertThat(SqlFunctions.divideAny(new BigDecimal(5L), new BigDecimal(2.0)), CoreMatchers.is(((Object) (new BigDecimal(2.5)))));
        // Non-numeric type
        try {
            SqlFunctions.divideAny("5", 2L);
            Assert.fail("'divide' on non-numeric type is not possible");
        } catch (CalciteException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.is(("Invalid types for arithmetic: class java.lang.String / " + "class java.lang.Long")));
        }
    }

    @Test
    public void testMultiset() {
        final List<String> abacee = Arrays.asList("a", "b", "a", "c", "e", "e");
        final List<String> adaa = Arrays.asList("a", "d", "a", "a");
        final List<String> addc = Arrays.asList("a", "d", "c", "d", "c");
        final List<String> z = Collections.emptyList();
        Assert.assertThat(SqlFunctions.multisetExceptAll(abacee, addc), CoreMatchers.is(Arrays.asList("b", "a", "e", "e")));
        Assert.assertThat(SqlFunctions.multisetExceptAll(abacee, z), CoreMatchers.is(abacee));
        Assert.assertThat(SqlFunctions.multisetExceptAll(z, z), CoreMatchers.is(z));
        Assert.assertThat(SqlFunctions.multisetExceptAll(z, addc), CoreMatchers.is(z));
        Assert.assertThat(SqlFunctions.multisetExceptDistinct(abacee, addc), CoreMatchers.is(Arrays.asList("b", "e")));
        Assert.assertThat(SqlFunctions.multisetExceptDistinct(abacee, z), CoreMatchers.is(Arrays.asList("a", "b", "c", "e")));
        Assert.assertThat(SqlFunctions.multisetExceptDistinct(z, z), CoreMatchers.is(z));
        Assert.assertThat(SqlFunctions.multisetExceptDistinct(z, addc), CoreMatchers.is(z));
        Assert.assertThat(SqlFunctions.multisetIntersectAll(abacee, addc), CoreMatchers.is(Arrays.asList("a", "c")));
        Assert.assertThat(SqlFunctions.multisetIntersectAll(abacee, adaa), CoreMatchers.is(Arrays.asList("a", "a")));
        Assert.assertThat(SqlFunctions.multisetIntersectAll(adaa, abacee), CoreMatchers.is(Arrays.asList("a", "a")));
        Assert.assertThat(SqlFunctions.multisetIntersectAll(abacee, z), CoreMatchers.is(z));
        Assert.assertThat(SqlFunctions.multisetIntersectAll(z, z), CoreMatchers.is(z));
        Assert.assertThat(SqlFunctions.multisetIntersectAll(z, addc), CoreMatchers.is(z));
        Assert.assertThat(SqlFunctions.multisetIntersectDistinct(abacee, addc), CoreMatchers.is(Arrays.asList("a", "c")));
        Assert.assertThat(SqlFunctions.multisetIntersectDistinct(abacee, adaa), CoreMatchers.is(Collections.singletonList("a")));
        Assert.assertThat(SqlFunctions.multisetIntersectDistinct(adaa, abacee), CoreMatchers.is(Collections.singletonList("a")));
        Assert.assertThat(SqlFunctions.multisetIntersectDistinct(abacee, z), CoreMatchers.is(z));
        Assert.assertThat(SqlFunctions.multisetIntersectDistinct(z, z), CoreMatchers.is(z));
        Assert.assertThat(SqlFunctions.multisetIntersectDistinct(z, addc), CoreMatchers.is(z));
        Assert.assertThat(SqlFunctions.multisetUnionAll(abacee, addc), CoreMatchers.is(Arrays.asList("a", "b", "a", "c", "e", "e", "a", "d", "c", "d", "c")));
        Assert.assertThat(SqlFunctions.multisetUnionAll(abacee, z), CoreMatchers.is(abacee));
        Assert.assertThat(SqlFunctions.multisetUnionAll(z, z), CoreMatchers.is(z));
        Assert.assertThat(SqlFunctions.multisetUnionAll(z, addc), CoreMatchers.is(addc));
        Assert.assertThat(SqlFunctions.multisetUnionDistinct(abacee, addc), CoreMatchers.is(Arrays.asList("a", "b", "c", "d", "e")));
        Assert.assertThat(SqlFunctions.multisetUnionDistinct(abacee, z), CoreMatchers.is(Arrays.asList("a", "b", "c", "e")));
        Assert.assertThat(SqlFunctions.multisetUnionDistinct(z, z), CoreMatchers.is(z));
        Assert.assertThat(SqlFunctions.multisetUnionDistinct(z, addc), CoreMatchers.is(Arrays.asList("a", "c", "d")));
    }
}

/**
 * End SqlFunctionsTest.java
 */
