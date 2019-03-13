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
package org.apache.calcite.rex;


import RexDigestIncludeType.ALWAYS;
import SqlCollation.IMPLICIT;
import SqlTypeName.ANY;
import SqlTypeName.BOOLEAN;
import SqlTypeName.DATE;
import SqlTypeName.DECIMAL;
import SqlTypeName.INTEGER;
import SqlTypeName.TIME;
import SqlTypeName.TIMESTAMP;
import SqlTypeName.TIMESTAMP_WITH_LOCAL_TIME_ZONE;
import SqlTypeName.VARCHAR;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.TimeZone;
import org.apache.calcite.avatica.util.ByteString;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.sql.SqlCollation;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.util.DateString;
import org.apache.calcite.util.NlsString;
import org.apache.calcite.util.TimeString;
import org.apache.calcite.util.TimestampString;
import org.apache.calcite.util.TimestampWithTimeZoneString;
import org.apache.calcite.util.Util;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for {@link RexBuilder}.
 */
public class RexBuilderTest {
    /**
     * Test RexBuilder.ensureType()
     */
    @Test
    public void testEnsureTypeWithAny() {
        final RelDataTypeFactory typeFactory = new org.apache.calcite.sql.type.SqlTypeFactoryImpl(RelDataTypeSystem.DEFAULT);
        RexBuilder builder = new RexBuilder(typeFactory);
        RexNode node = new RexLiteral(Boolean.TRUE, typeFactory.createSqlType(BOOLEAN), SqlTypeName.BOOLEAN);
        RexNode ensuredNode = builder.ensureType(typeFactory.createSqlType(ANY), node, true);
        Assert.assertEquals(node, ensuredNode);
    }

    /**
     * Test RexBuilder.ensureType()
     */
    @Test
    public void testEnsureTypeWithItself() {
        final RelDataTypeFactory typeFactory = new org.apache.calcite.sql.type.SqlTypeFactoryImpl(RelDataTypeSystem.DEFAULT);
        RexBuilder builder = new RexBuilder(typeFactory);
        RexNode node = new RexLiteral(Boolean.TRUE, typeFactory.createSqlType(BOOLEAN), SqlTypeName.BOOLEAN);
        RexNode ensuredNode = builder.ensureType(typeFactory.createSqlType(BOOLEAN), node, true);
        Assert.assertEquals(node, ensuredNode);
    }

    /**
     * Test RexBuilder.ensureType()
     */
    @Test
    public void testEnsureTypeWithDifference() {
        final RelDataTypeFactory typeFactory = new org.apache.calcite.sql.type.SqlTypeFactoryImpl(RelDataTypeSystem.DEFAULT);
        RexBuilder builder = new RexBuilder(typeFactory);
        RexNode node = new RexLiteral(Boolean.TRUE, typeFactory.createSqlType(BOOLEAN), SqlTypeName.BOOLEAN);
        RexNode ensuredNode = builder.ensureType(typeFactory.createSqlType(INTEGER), node, true);
        Assert.assertNotEquals(node, ensuredNode);
        Assert.assertEquals(ensuredNode.getType(), typeFactory.createSqlType(INTEGER));
    }

    private static final long MOON = -14159025000L;

    private static final int MOON_DAY = -164;

    private static final int MOON_TIME = 10575000;

    /**
     * Tests {@link RexBuilder#makeTimestampLiteral(TimestampString, int)}.
     */
    @Test
    public void testTimestampLiteral() {
        final RelDataTypeFactory typeFactory = new org.apache.calcite.sql.type.SqlTypeFactoryImpl(RelDataTypeSystem.DEFAULT);
        final RelDataType timestampType = typeFactory.createSqlType(TIMESTAMP);
        final RelDataType timestampType3 = typeFactory.createSqlType(TIMESTAMP, 3);
        final RelDataType timestampType9 = typeFactory.createSqlType(TIMESTAMP, 9);
        final RelDataType timestampType18 = typeFactory.createSqlType(TIMESTAMP, 18);
        final RexBuilder builder = new RexBuilder(typeFactory);
        // Old way: provide a Calendar
        final Calendar calendar = Util.calendar();
        calendar.set(1969, Calendar.JULY, 21, 2, 56, 15);// one small step

        calendar.set(Calendar.MILLISECOND, 0);
        checkTimestamp(builder.makeLiteral(calendar, timestampType, false));
        // Old way #2: Provide a Long
        checkTimestamp(builder.makeLiteral(RexBuilderTest.MOON, timestampType, false));
        // The new way
        final TimestampString ts = new TimestampString(1969, 7, 21, 2, 56, 15);
        checkTimestamp(builder.makeLiteral(ts, timestampType, false));
        // Now with milliseconds
        final TimestampString ts2 = ts.withMillis(56);
        Assert.assertThat(ts2.toString(), Is.is("1969-07-21 02:56:15.056"));
        final RexNode literal2 = builder.makeLiteral(ts2, timestampType3, false);
        Assert.assertThat(getValueAs(TimestampString.class).toString(), Is.is("1969-07-21 02:56:15.056"));
        // Now with nanoseconds
        final TimestampString ts3 = ts.withNanos(56);
        final RexNode literal3 = builder.makeLiteral(ts3, timestampType9, false);
        Assert.assertThat(getValueAs(TimestampString.class).toString(), Is.is("1969-07-21 02:56:15"));
        final TimestampString ts3b = ts.withNanos(2345678);
        final RexNode literal3b = builder.makeLiteral(ts3b, timestampType9, false);
        Assert.assertThat(getValueAs(TimestampString.class).toString(), Is.is("1969-07-21 02:56:15.002"));
        // Now with a very long fraction
        final TimestampString ts4 = ts.withFraction("102030405060708090102");
        final RexNode literal4 = builder.makeLiteral(ts4, timestampType18, false);
        Assert.assertThat(getValueAs(TimestampString.class).toString(), Is.is("1969-07-21 02:56:15.102"));
        // toString
        Assert.assertThat(ts2.round(1).toString(), Is.is("1969-07-21 02:56:15"));
        Assert.assertThat(ts2.round(2).toString(), Is.is("1969-07-21 02:56:15.05"));
        Assert.assertThat(ts2.round(3).toString(), Is.is("1969-07-21 02:56:15.056"));
        Assert.assertThat(ts2.round(4).toString(), Is.is("1969-07-21 02:56:15.056"));
        Assert.assertThat(ts2.toString(6), Is.is("1969-07-21 02:56:15.056000"));
        Assert.assertThat(ts2.toString(1), Is.is("1969-07-21 02:56:15.0"));
        Assert.assertThat(ts2.toString(0), Is.is("1969-07-21 02:56:15"));
        Assert.assertThat(ts2.round(0).toString(), Is.is("1969-07-21 02:56:15"));
        Assert.assertThat(ts2.round(0).toString(0), Is.is("1969-07-21 02:56:15"));
        Assert.assertThat(ts2.round(0).toString(1), Is.is("1969-07-21 02:56:15.0"));
        Assert.assertThat(ts2.round(0).toString(2), Is.is("1969-07-21 02:56:15.00"));
        Assert.assertThat(TimestampString.fromMillisSinceEpoch(1456513560123L).toString(), Is.is("2016-02-26 19:06:00.123"));
    }

    /**
     * Tests
     * {@link RexBuilder#makeTimestampWithLocalTimeZoneLiteral(TimestampString, int)}.
     */
    @Test
    public void testTimestampWithLocalTimeZoneLiteral() {
        final RelDataTypeFactory typeFactory = new org.apache.calcite.sql.type.SqlTypeFactoryImpl(RelDataTypeSystem.DEFAULT);
        final RelDataType timestampType = typeFactory.createSqlType(TIMESTAMP_WITH_LOCAL_TIME_ZONE);
        final RelDataType timestampType3 = typeFactory.createSqlType(TIMESTAMP_WITH_LOCAL_TIME_ZONE, 3);
        final RelDataType timestampType9 = typeFactory.createSqlType(TIMESTAMP_WITH_LOCAL_TIME_ZONE, 9);
        final RelDataType timestampType18 = typeFactory.createSqlType(TIMESTAMP_WITH_LOCAL_TIME_ZONE, 18);
        final RexBuilder builder = new RexBuilder(typeFactory);
        // The new way
        final TimestampWithTimeZoneString ts = new TimestampWithTimeZoneString(1969, 7, 21, 2, 56, 15, TimeZone.getTimeZone("PST").getID());
        checkTimestampWithLocalTimeZone(builder.makeLiteral(ts.getLocalTimestampString(), timestampType, false));
        // Now with milliseconds
        final TimestampWithTimeZoneString ts2 = ts.withMillis(56);
        Assert.assertThat(ts2.toString(), Is.is("1969-07-21 02:56:15.056 PST"));
        final RexNode literal2 = builder.makeLiteral(ts2.getLocalTimestampString(), timestampType3, false);
        Assert.assertThat(getValue().toString(), Is.is("1969-07-21 02:56:15.056"));
        // Now with nanoseconds
        final TimestampWithTimeZoneString ts3 = ts.withNanos(56);
        final RexNode literal3 = builder.makeLiteral(ts3.getLocalTimestampString(), timestampType9, false);
        Assert.assertThat(getValueAs(TimestampString.class).toString(), Is.is("1969-07-21 02:56:15"));
        final TimestampWithTimeZoneString ts3b = ts.withNanos(2345678);
        final RexNode literal3b = builder.makeLiteral(ts3b.getLocalTimestampString(), timestampType9, false);
        Assert.assertThat(getValueAs(TimestampString.class).toString(), Is.is("1969-07-21 02:56:15.002"));
        // Now with a very long fraction
        final TimestampWithTimeZoneString ts4 = ts.withFraction("102030405060708090102");
        final RexNode literal4 = builder.makeLiteral(ts4.getLocalTimestampString(), timestampType18, false);
        Assert.assertThat(getValueAs(TimestampString.class).toString(), Is.is("1969-07-21 02:56:15.102"));
        // toString
        Assert.assertThat(ts2.round(1).toString(), Is.is("1969-07-21 02:56:15 PST"));
        Assert.assertThat(ts2.round(2).toString(), Is.is("1969-07-21 02:56:15.05 PST"));
        Assert.assertThat(ts2.round(3).toString(), Is.is("1969-07-21 02:56:15.056 PST"));
        Assert.assertThat(ts2.round(4).toString(), Is.is("1969-07-21 02:56:15.056 PST"));
        Assert.assertThat(ts2.toString(6), Is.is("1969-07-21 02:56:15.056000 PST"));
        Assert.assertThat(ts2.toString(1), Is.is("1969-07-21 02:56:15.0 PST"));
        Assert.assertThat(ts2.toString(0), Is.is("1969-07-21 02:56:15 PST"));
        Assert.assertThat(ts2.round(0).toString(), Is.is("1969-07-21 02:56:15 PST"));
        Assert.assertThat(ts2.round(0).toString(0), Is.is("1969-07-21 02:56:15 PST"));
        Assert.assertThat(ts2.round(0).toString(1), Is.is("1969-07-21 02:56:15.0 PST"));
        Assert.assertThat(ts2.round(0).toString(2), Is.is("1969-07-21 02:56:15.00 PST"));
    }

    /**
     * Tests {@link RexBuilder#makeTimeLiteral(TimeString, int)}.
     */
    @Test
    public void testTimeLiteral() {
        final RelDataTypeFactory typeFactory = new org.apache.calcite.sql.type.SqlTypeFactoryImpl(RelDataTypeSystem.DEFAULT);
        RelDataType timeType = typeFactory.createSqlType(TIME);
        final RelDataType timeType3 = typeFactory.createSqlType(TIME, 3);
        final RelDataType timeType9 = typeFactory.createSqlType(TIME, 9);
        final RelDataType timeType18 = typeFactory.createSqlType(TIME, 18);
        final RexBuilder builder = new RexBuilder(typeFactory);
        // Old way: provide a Calendar
        final Calendar calendar = Util.calendar();
        calendar.set(1969, Calendar.JULY, 21, 2, 56, 15);// one small step

        calendar.set(Calendar.MILLISECOND, 0);
        checkTime(builder.makeLiteral(calendar, timeType, false));
        // Old way #2: Provide a Long
        checkTime(builder.makeLiteral(RexBuilderTest.MOON_TIME, timeType, false));
        // The new way
        final TimeString t = new TimeString(2, 56, 15);
        Assert.assertThat(t.getMillisOfDay(), Is.is(10575000));
        checkTime(builder.makeLiteral(t, timeType, false));
        // Now with milliseconds
        final TimeString t2 = t.withMillis(56);
        Assert.assertThat(t2.getMillisOfDay(), Is.is(10575056));
        Assert.assertThat(t2.toString(), Is.is("02:56:15.056"));
        final RexNode literal2 = builder.makeLiteral(t2, timeType3, false);
        Assert.assertThat(getValueAs(TimeString.class).toString(), Is.is("02:56:15.056"));
        // Now with nanoseconds
        final TimeString t3 = t.withNanos(2345678);
        Assert.assertThat(t3.getMillisOfDay(), Is.is(10575002));
        final RexNode literal3 = builder.makeLiteral(t3, timeType9, false);
        Assert.assertThat(getValueAs(TimeString.class).toString(), Is.is("02:56:15.002"));
        // Now with a very long fraction
        final TimeString t4 = t.withFraction("102030405060708090102");
        Assert.assertThat(t4.getMillisOfDay(), Is.is(10575102));
        final RexNode literal4 = builder.makeLiteral(t4, timeType18, false);
        Assert.assertThat(getValueAs(TimeString.class).toString(), Is.is("02:56:15.102"));
        // toString
        Assert.assertThat(t2.round(1).toString(), Is.is("02:56:15"));
        Assert.assertThat(t2.round(2).toString(), Is.is("02:56:15.05"));
        Assert.assertThat(t2.round(3).toString(), Is.is("02:56:15.056"));
        Assert.assertThat(t2.round(4).toString(), Is.is("02:56:15.056"));
        Assert.assertThat(t2.toString(6), Is.is("02:56:15.056000"));
        Assert.assertThat(t2.toString(1), Is.is("02:56:15.0"));
        Assert.assertThat(t2.toString(0), Is.is("02:56:15"));
        Assert.assertThat(t2.round(0).toString(), Is.is("02:56:15"));
        Assert.assertThat(t2.round(0).toString(0), Is.is("02:56:15"));
        Assert.assertThat(t2.round(0).toString(1), Is.is("02:56:15.0"));
        Assert.assertThat(t2.round(0).toString(2), Is.is("02:56:15.00"));
        Assert.assertThat(TimeString.fromMillisOfDay(53560123).toString(), Is.is("14:52:40.123"));
    }

    /**
     * Tests {@link RexBuilder#makeDateLiteral(DateString)}.
     */
    @Test
    public void testDateLiteral() {
        final RelDataTypeFactory typeFactory = new org.apache.calcite.sql.type.SqlTypeFactoryImpl(RelDataTypeSystem.DEFAULT);
        RelDataType dateType = typeFactory.createSqlType(DATE);
        final RexBuilder builder = new RexBuilder(typeFactory);
        // Old way: provide a Calendar
        final Calendar calendar = Util.calendar();
        calendar.set(1969, Calendar.JULY, 21);// one small step

        calendar.set(Calendar.MILLISECOND, 0);
        checkDate(builder.makeLiteral(calendar, dateType, false));
        // Old way #2: Provide in Integer
        checkDate(builder.makeLiteral(RexBuilderTest.MOON_DAY, dateType, false));
        // The new way
        final DateString d = new DateString(1969, 7, 21);
        checkDate(builder.makeLiteral(d, dateType, false));
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-2306">[CALCITE-2306]
     * AssertionError in {@link RexLiteral#getValue3} with null literal of type
     * DECIMAL</a>.
     */
    @Test
    public void testDecimalLiteral() {
        final RelDataTypeFactory typeFactory = new org.apache.calcite.sql.type.SqlTypeFactoryImpl(RelDataTypeSystem.DEFAULT);
        final RelDataType type = typeFactory.createSqlType(DECIMAL);
        final RexBuilder builder = new RexBuilder(typeFactory);
        final RexLiteral literal = builder.makeExactLiteral(null, type);
        Assert.assertThat(literal.getValue3(), CoreMatchers.nullValue());
    }

    /**
     * Tests {@link DateString} year range.
     */
    @Test
    public void testDateStringYearError() {
        try {
            final DateString dateString = new DateString(11969, 7, 21);
            Assert.fail(("expected exception, got " + dateString));
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Year out of range: [11969]"));
        }
        try {
            final DateString dateString = new DateString("12345-01-23");
            Assert.fail(("expected exception, got " + dateString));
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Invalid date format: [12345-01-23]"));
        }
    }

    /**
     * Tests {@link DateString} month range.
     */
    @Test
    public void testDateStringMonthError() {
        try {
            final DateString dateString = new DateString(1969, 27, 21);
            Assert.fail(("expected exception, got " + dateString));
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Month out of range: [27]"));
        }
        try {
            final DateString dateString = new DateString("1234-13-02");
            Assert.fail(("expected exception, got " + dateString));
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Month out of range: [13]"));
        }
    }

    /**
     * Tests {@link DateString} day range.
     */
    @Test
    public void testDateStringDayError() {
        try {
            final DateString dateString = new DateString(1969, 7, 41);
            Assert.fail(("expected exception, got " + dateString));
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Day out of range: [41]"));
        }
        try {
            final DateString dateString = new DateString("1234-01-32");
            Assert.fail(("expected exception, got " + dateString));
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Day out of range: [32]"));
        }
        // We don't worry about the number of days in a month. 30 is in range.
        final DateString dateString = new DateString("1234-02-30");
        Assert.assertThat(dateString, CoreMatchers.notNullValue());
    }

    /**
     * Tests {@link TimeString} hour range.
     */
    @Test
    public void testTimeStringHourError() {
        try {
            final TimeString timeString = new TimeString(111, 34, 56);
            Assert.fail(("expected exception, got " + timeString));
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Hour out of range: [111]"));
        }
        try {
            final TimeString timeString = new TimeString("24:00:00");
            Assert.fail(("expected exception, got " + timeString));
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Hour out of range: [24]"));
        }
        try {
            final TimeString timeString = new TimeString("24:00");
            Assert.fail(("expected exception, got " + timeString));
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Invalid time format: [24:00]"));
        }
    }

    /**
     * Tests {@link TimeString} minute range.
     */
    @Test
    public void testTimeStringMinuteError() {
        try {
            final TimeString timeString = new TimeString(12, 334, 56);
            Assert.fail(("expected exception, got " + timeString));
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Minute out of range: [334]"));
        }
        try {
            final TimeString timeString = new TimeString("12:60:23");
            Assert.fail(("expected exception, got " + timeString));
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Minute out of range: [60]"));
        }
    }

    /**
     * Tests {@link TimeString} second range.
     */
    @Test
    public void testTimeStringSecondError() {
        try {
            final TimeString timeString = new TimeString(12, 34, 567);
            Assert.fail(("expected exception, got " + timeString));
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Second out of range: [567]"));
        }
        try {
            final TimeString timeString = new TimeString(12, 34, (-4));
            Assert.fail(("expected exception, got " + timeString));
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Second out of range: [-4]"));
        }
        try {
            final TimeString timeString = new TimeString("12:34:60");
            Assert.fail(("expected exception, got " + timeString));
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Second out of range: [60]"));
        }
    }

    /**
     * Test string literal encoding.
     */
    @Test
    public void testStringLiteral() {
        final RelDataTypeFactory typeFactory = new org.apache.calcite.sql.type.SqlTypeFactoryImpl(RelDataTypeSystem.DEFAULT);
        final RelDataType varchar = typeFactory.createSqlType(VARCHAR);
        final RexBuilder builder = new RexBuilder(typeFactory);
        final NlsString latin1 = new NlsString("foobar", "LATIN1", SqlCollation.IMPLICIT);
        final NlsString utf8 = new NlsString("foobar", "UTF8", SqlCollation.IMPLICIT);
        RexNode literal = builder.makePreciseStringLiteral("foobar");
        Assert.assertEquals("'foobar'", literal.toString());
        literal = builder.makePreciseStringLiteral(new ByteString(new byte[]{ 'f', 'o', 'o', 'b', 'a', 'r' }), "UTF8", IMPLICIT);
        Assert.assertEquals("_UTF8'foobar'", literal.toString());
        Assert.assertEquals("_UTF8\'foobar\':CHAR(6) CHARACTER SET \"UTF-8\"", ((RexLiteral) (literal)).computeDigest(ALWAYS));
        literal = builder.makePreciseStringLiteral(new ByteString("\u82f1\u56fd".getBytes(StandardCharsets.UTF_8)), "UTF8", IMPLICIT);
        Assert.assertEquals("_UTF8\'\u82f1\u56fd\'", literal.toString());
        // Test again to check decode cache.
        literal = builder.makePreciseStringLiteral(new ByteString("\u82f1".getBytes(StandardCharsets.UTF_8)), "UTF8", IMPLICIT);
        Assert.assertEquals("_UTF8\'\u82f1\'", literal.toString());
        try {
            literal = builder.makePreciseStringLiteral(new ByteString("\u82f1\u56fd".getBytes(StandardCharsets.UTF_8)), "GB2312", IMPLICIT);
            Assert.fail(("expected exception, got " + literal));
        } catch (RuntimeException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Failed to encode"));
        }
        literal = builder.makeLiteral(latin1, varchar, false);
        Assert.assertEquals("_LATIN1'foobar'", literal.toString());
        literal = builder.makeLiteral(utf8, varchar, false);
        Assert.assertEquals("_UTF8'foobar'", literal.toString());
    }

    /**
     * Tests {@link RexBuilder#makeExactLiteral(java.math.BigDecimal)}.
     */
    @Test
    public void testBigDecimalLiteral() {
        final RelDataTypeFactory typeFactory = new org.apache.calcite.sql.type.SqlTypeFactoryImpl(RelDataTypeSystem.DEFAULT);
        final RexBuilder builder = new RexBuilder(typeFactory);
        checkBigDecimalLiteral(builder, "25");
        checkBigDecimalLiteral(builder, "9.9");
        checkBigDecimalLiteral(builder, "0");
        checkBigDecimalLiteral(builder, "-75.5");
        checkBigDecimalLiteral(builder, "10000000");
        checkBigDecimalLiteral(builder, "100000.111111111111111111");
        checkBigDecimalLiteral(builder, "-100000.111111111111111111");
        checkBigDecimalLiteral(builder, "73786976294838206464");// 2^66

        checkBigDecimalLiteral(builder, "-73786976294838206464");
    }
}

/**
 * End RexBuilderTest.java
 */
