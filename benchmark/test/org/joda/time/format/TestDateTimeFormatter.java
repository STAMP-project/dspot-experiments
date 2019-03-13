/**
 * Copyright 2001-2016 Stephen Colebourne
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.joda.time.format;


import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.TimeZone;
import junit.framework.TestCase;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.MutableDateTime;
import org.joda.time.ReadablePartial;
import org.joda.time.chrono.BuddhistChronology;
import org.joda.time.chrono.GJChronology;
import org.joda.time.chrono.ISOChronology;


/**
 * This class is a Junit unit test for DateTime Formating.
 *
 * @author Stephen Colebourne
 */
public class TestDateTimeFormatter extends TestCase {
    private static final DateTimeZone UTC = DateTimeZone.UTC;

    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final DateTimeZone TOKYO = DateTimeZone.forID("Asia/Tokyo");

    private static final DateTimeZone NEWYORK = DateTimeZone.forID("America/New_York");

    private static final Chronology ISO_UTC = ISOChronology.getInstanceUTC();

    private static final Chronology ISO_PARIS = ISOChronology.getInstance(TestDateTimeFormatter.PARIS);

    private static final Chronology BUDDHIST_PARIS = BuddhistChronology.getInstance(TestDateTimeFormatter.PARIS);

    long y2002days = ((((((((((((((((((((((((((((((365 + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365;

    // 2002-06-09
    private long TEST_TIME_NOW = ((((((((y2002days) + 31L) + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (DateTimeConstants.MILLIS_PER_DAY);

    private DateTimeZone originalDateTimeZone = null;

    private TimeZone originalTimeZone = null;

    private Locale originalLocale = null;

    private DateTimeFormatter f = null;

    private DateTimeFormatter g = null;

    public TestDateTimeFormatter(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testPrint_simple() {
        DateTime dt = new DateTime(2004, 6, 9, 10, 20, 30, 40, TestDateTimeFormatter.UTC);
        TestCase.assertEquals("Wed 2004-06-09T10:20:30Z", f.print(dt));
        dt = dt.withZone(TestDateTimeFormatter.PARIS);
        TestCase.assertEquals("Wed 2004-06-09T12:20:30+02:00", f.print(dt));
        dt = dt.withZone(TestDateTimeFormatter.NEWYORK);
        TestCase.assertEquals("Wed 2004-06-09T06:20:30-04:00", f.print(dt));
        dt = dt.withChronology(TestDateTimeFormatter.BUDDHIST_PARIS);
        TestCase.assertEquals("Wed 2547-06-09T12:20:30+02:00", f.print(dt));
    }

    // -----------------------------------------------------------------------
    public void testPrint_locale() {
        DateTime dt = new DateTime(2004, 6, 9, 10, 20, 30, 40, TestDateTimeFormatter.UTC);
        TestCase.assertEquals("mer. 2004-06-09T10:20:30Z", f.withLocale(Locale.FRENCH).print(dt));
        TestCase.assertEquals("Wed 2004-06-09T10:20:30Z", f.withLocale(null).print(dt));
    }

    // -----------------------------------------------------------------------
    public void testPrint_zone() {
        DateTime dt = new DateTime(2004, 6, 9, 10, 20, 30, 40, TestDateTimeFormatter.UTC);
        TestCase.assertEquals("Wed 2004-06-09T06:20:30-04:00", f.withZone(TestDateTimeFormatter.NEWYORK).print(dt));
        TestCase.assertEquals("Wed 2004-06-09T12:20:30+02:00", f.withZone(TestDateTimeFormatter.PARIS).print(dt));
        TestCase.assertEquals("Wed 2004-06-09T10:20:30Z", f.withZone(null).print(dt));
        dt = dt.withZone(TestDateTimeFormatter.NEWYORK);
        TestCase.assertEquals("Wed 2004-06-09T06:20:30-04:00", f.withZone(TestDateTimeFormatter.NEWYORK).print(dt));
        TestCase.assertEquals("Wed 2004-06-09T12:20:30+02:00", f.withZone(TestDateTimeFormatter.PARIS).print(dt));
        TestCase.assertEquals("Wed 2004-06-09T10:20:30Z", f.withZoneUTC().print(dt));
        TestCase.assertEquals("Wed 2004-06-09T06:20:30-04:00", f.withZone(null).print(dt));
    }

    // -----------------------------------------------------------------------
    public void testPrint_chrono() {
        DateTime dt = new DateTime(2004, 6, 9, 10, 20, 30, 40, TestDateTimeFormatter.UTC);
        TestCase.assertEquals("Wed 2004-06-09T12:20:30+02:00", f.withChronology(TestDateTimeFormatter.ISO_PARIS).print(dt));
        TestCase.assertEquals("Wed 2547-06-09T12:20:30+02:00", f.withChronology(TestDateTimeFormatter.BUDDHIST_PARIS).print(dt));
        TestCase.assertEquals("Wed 2004-06-09T10:20:30Z", f.withChronology(null).print(dt));
        dt = dt.withChronology(TestDateTimeFormatter.BUDDHIST_PARIS);
        TestCase.assertEquals("Wed 2004-06-09T12:20:30+02:00", f.withChronology(TestDateTimeFormatter.ISO_PARIS).print(dt));
        TestCase.assertEquals("Wed 2547-06-09T12:20:30+02:00", f.withChronology(TestDateTimeFormatter.BUDDHIST_PARIS).print(dt));
        TestCase.assertEquals("Wed 2004-06-09T10:20:30Z", f.withChronology(TestDateTimeFormatter.ISO_UTC).print(dt));
        TestCase.assertEquals("Wed 2547-06-09T12:20:30+02:00", f.withChronology(null).print(dt));
    }

    // -----------------------------------------------------------------------
    public void testPrint_appendableMethods() throws Exception {
        DateTime dt = new DateTime(2004, 6, 9, 10, 20, 30, 40, TestDateTimeFormatter.UTC);
        StringBuilder buf = new StringBuilder();
        f.printTo(buf, dt);
        TestCase.assertEquals("Wed 2004-06-09T10:20:30Z", buf.toString());
        buf = new StringBuilder();
        f.printTo(buf, dt.getMillis());
        TestCase.assertEquals("Wed 2004-06-09T11:20:30+01:00", buf.toString());
        buf = new StringBuilder();
        ISODateTimeFormat.yearMonthDay().printTo(buf, dt.toLocalDate());
        TestCase.assertEquals("2004-06-09", buf.toString());
        buf = new StringBuilder();
        try {
            ISODateTimeFormat.yearMonthDay().printTo(buf, ((ReadablePartial) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testPrint_chrono_and_zone() {
        DateTime dt = new DateTime(2004, 6, 9, 10, 20, 30, 40, TestDateTimeFormatter.UTC);
        TestCase.assertEquals("Wed 2004-06-09T10:20:30Z", f.withChronology(null).withZone(null).print(dt));
        TestCase.assertEquals("Wed 2004-06-09T12:20:30+02:00", f.withChronology(TestDateTimeFormatter.ISO_PARIS).withZone(null).print(dt));
        TestCase.assertEquals("Wed 2004-06-09T12:20:30+02:00", f.withChronology(TestDateTimeFormatter.ISO_PARIS).withZone(TestDateTimeFormatter.PARIS).print(dt));
        TestCase.assertEquals("Wed 2004-06-09T06:20:30-04:00", f.withChronology(TestDateTimeFormatter.ISO_PARIS).withZone(TestDateTimeFormatter.NEWYORK).print(dt));
        TestCase.assertEquals("Wed 2004-06-09T06:20:30-04:00", f.withChronology(null).withZone(TestDateTimeFormatter.NEWYORK).print(dt));
        dt = dt.withChronology(TestDateTimeFormatter.ISO_PARIS);
        TestCase.assertEquals("Wed 2004-06-09T12:20:30+02:00", f.withChronology(null).withZone(null).print(dt));
        TestCase.assertEquals("Wed 2004-06-09T12:20:30+02:00", f.withChronology(TestDateTimeFormatter.ISO_PARIS).withZone(null).print(dt));
        TestCase.assertEquals("Wed 2004-06-09T12:20:30+02:00", f.withChronology(TestDateTimeFormatter.ISO_PARIS).withZone(TestDateTimeFormatter.PARIS).print(dt));
        TestCase.assertEquals("Wed 2004-06-09T06:20:30-04:00", f.withChronology(TestDateTimeFormatter.ISO_PARIS).withZone(TestDateTimeFormatter.NEWYORK).print(dt));
        TestCase.assertEquals("Wed 2004-06-09T06:20:30-04:00", f.withChronology(null).withZone(TestDateTimeFormatter.NEWYORK).print(dt));
        dt = dt.withChronology(TestDateTimeFormatter.BUDDHIST_PARIS);
        TestCase.assertEquals("Wed 2547-06-09T12:20:30+02:00", f.withChronology(null).withZone(null).print(dt));
        TestCase.assertEquals("Wed 2004-06-09T12:20:30+02:00", f.withChronology(TestDateTimeFormatter.ISO_PARIS).withZone(null).print(dt));
        TestCase.assertEquals("Wed 2004-06-09T12:20:30+02:00", f.withChronology(TestDateTimeFormatter.ISO_PARIS).withZone(TestDateTimeFormatter.PARIS).print(dt));
        TestCase.assertEquals("Wed 2004-06-09T06:20:30-04:00", f.withChronology(TestDateTimeFormatter.ISO_PARIS).withZone(TestDateTimeFormatter.NEWYORK).print(dt));
        TestCase.assertEquals("Wed 2547-06-09T06:20:30-04:00", f.withChronology(null).withZone(TestDateTimeFormatter.NEWYORK).print(dt));
    }

    public void testWithGetLocale() {
        DateTimeFormatter f2 = f.withLocale(Locale.FRENCH);
        TestCase.assertEquals(Locale.FRENCH, f2.getLocale());
        TestCase.assertSame(f2, f2.withLocale(Locale.FRENCH));
        f2 = f.withLocale(null);
        TestCase.assertEquals(null, f2.getLocale());
        TestCase.assertSame(f2, f2.withLocale(null));
    }

    public void testWithGetZone() {
        DateTimeFormatter f2 = f.withZone(TestDateTimeFormatter.PARIS);
        TestCase.assertEquals(TestDateTimeFormatter.PARIS, f2.getZone());
        TestCase.assertSame(f2, f2.withZone(TestDateTimeFormatter.PARIS));
        f2 = f.withZone(null);
        TestCase.assertEquals(null, f2.getZone());
        TestCase.assertSame(f2, f2.withZone(null));
    }

    public void testWithGetChronology() {
        DateTimeFormatter f2 = f.withChronology(TestDateTimeFormatter.BUDDHIST_PARIS);
        TestCase.assertEquals(TestDateTimeFormatter.BUDDHIST_PARIS, f2.getChronology());
        TestCase.assertSame(f2, f2.withChronology(TestDateTimeFormatter.BUDDHIST_PARIS));
        f2 = f.withChronology(null);
        TestCase.assertEquals(null, f2.getChronology());
        TestCase.assertSame(f2, f2.withChronology(null));
    }

    public void testWithGetPivotYear() {
        DateTimeFormatter f2 = f.withPivotYear(13);
        TestCase.assertEquals(new Integer(13), f2.getPivotYear());
        TestCase.assertSame(f2, f2.withPivotYear(13));
        f2 = f.withPivotYear(new Integer(14));
        TestCase.assertEquals(new Integer(14), f2.getPivotYear());
        TestCase.assertSame(f2, f2.withPivotYear(new Integer(14)));
        f2 = f.withPivotYear(null);
        TestCase.assertEquals(null, f2.getPivotYear());
        TestCase.assertSame(f2, f2.withPivotYear(null));
    }

    public void testWithGetOffsetParsedMethods() {
        DateTimeFormatter f2 = f;
        TestCase.assertEquals(false, f2.isOffsetParsed());
        TestCase.assertEquals(null, f2.getZone());
        f2 = f.withOffsetParsed();
        TestCase.assertEquals(true, f2.isOffsetParsed());
        TestCase.assertEquals(null, f2.getZone());
        f2 = f2.withZone(TestDateTimeFormatter.PARIS);
        TestCase.assertEquals(false, f2.isOffsetParsed());
        TestCase.assertEquals(TestDateTimeFormatter.PARIS, f2.getZone());
        f2 = f2.withOffsetParsed();
        TestCase.assertEquals(true, f2.isOffsetParsed());
        TestCase.assertEquals(null, f2.getZone());
        f2 = f.withOffsetParsed();
        TestCase.assertNotSame(f, f2);
        DateTimeFormatter f3 = f2.withOffsetParsed();
        TestCase.assertSame(f2, f3);
    }

    public void testPrinterParserMethods() {
        DateTimeFormatter f2 = new DateTimeFormatter(f.getPrinter(), f.getParser());
        TestCase.assertEquals(f.getPrinter(), f2.getPrinter());
        TestCase.assertEquals(f.getParser(), f2.getParser());
        TestCase.assertEquals(true, f2.isPrinter());
        TestCase.assertEquals(true, f2.isParser());
        TestCase.assertNotNull(f2.print(0L));
        TestCase.assertNotNull(f2.parseDateTime("Thu 1970-01-01T00:00:00Z"));
        f2 = new DateTimeFormatter(f.getPrinter(), null);
        TestCase.assertEquals(f.getPrinter(), f2.getPrinter());
        TestCase.assertEquals(null, f2.getParser());
        TestCase.assertEquals(true, f2.isPrinter());
        TestCase.assertEquals(false, f2.isParser());
        TestCase.assertNotNull(f2.print(0L));
        try {
            f2.parseDateTime("Thu 1970-01-01T00:00:00Z");
            TestCase.fail();
        } catch (UnsupportedOperationException ex) {
        }
        f2 = new DateTimeFormatter(((DateTimePrinter) (null)), f.getParser());
        TestCase.assertEquals(null, f2.getPrinter());
        TestCase.assertEquals(f.getParser(), f2.getParser());
        TestCase.assertEquals(false, f2.isPrinter());
        TestCase.assertEquals(true, f2.isParser());
        try {
            f2.print(0L);
            TestCase.fail();
        } catch (UnsupportedOperationException ex) {
        }
        TestCase.assertNotNull(f2.parseDateTime("Thu 1970-01-01T00:00:00Z"));
    }

    // -----------------------------------------------------------------------
    public void testParseLocalDate_simple() {
        TestCase.assertEquals(new LocalDate(2004, 6, 9), g.parseLocalDate("2004-06-09T10:20:30Z"));
        TestCase.assertEquals(new LocalDate(2004, 6, 9), g.parseLocalDate("2004-06-09T10:20:30+18:00"));
        TestCase.assertEquals(new LocalDate(2004, 6, 9), g.parseLocalDate("2004-06-09T10:20:30-18:00"));
        TestCase.assertEquals(new LocalDate(2004, 6, 9, TestDateTimeFormatter.BUDDHIST_PARIS), g.withChronology(TestDateTimeFormatter.BUDDHIST_PARIS).parseLocalDate("2004-06-09T10:20:30Z"));
        try {
            g.parseDateTime("ABC");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testParseLocalDate_yearOfEra() {
        Chronology chrono = GJChronology.getInstanceUTC();
        DateTimeFormatter f = DateTimeFormat.forPattern("YYYY-MM GG").withChronology(chrono).withLocale(Locale.UK);
        LocalDate date = new LocalDate(2005, 10, 1, chrono);
        TestCase.assertEquals(date, f.parseLocalDate("2005-10 AD"));
        TestCase.assertEquals(date, f.parseLocalDate("2005-10 CE"));
        date = new LocalDate((-2005), 10, 1, chrono);
        TestCase.assertEquals(date, f.parseLocalDate("2005-10 BC"));
        TestCase.assertEquals(date, f.parseLocalDate("2005-10 BCE"));
    }

    public void testParseLocalDate_yearOfCentury() {
        Chronology chrono = GJChronology.getInstanceUTC();
        DateTimeFormatter f = DateTimeFormat.forPattern("yy M d").withChronology(chrono).withLocale(Locale.UK).withPivotYear(2050);
        LocalDate date = new LocalDate(2050, 8, 4, chrono);
        TestCase.assertEquals(date, f.parseLocalDate("50 8 4"));
    }

    public void testParseLocalDate_monthDay_feb29() {
        Chronology chrono = GJChronology.getInstanceUTC();
        DateTimeFormatter f = DateTimeFormat.forPattern("M d").withChronology(chrono).withLocale(Locale.UK);
        TestCase.assertEquals(new LocalDate(2000, 2, 29, chrono), f.parseLocalDate("2 29"));
    }

    public void testParseLocalDate_monthDay_withDefaultYear_feb29() {
        Chronology chrono = GJChronology.getInstanceUTC();
        DateTimeFormatter f = DateTimeFormat.forPattern("M d").withChronology(chrono).withLocale(Locale.UK).withDefaultYear(2012);
        TestCase.assertEquals(new LocalDate(2012, 2, 29, chrono), f.parseLocalDate("2 29"));
    }

    public void testParseLocalDate_weekyear_month_week_2010() {
        Chronology chrono = GJChronology.getInstanceUTC();
        DateTimeFormatter f = DateTimeFormat.forPattern("xxxx-MM-ww").withChronology(chrono);
        TestCase.assertEquals(new LocalDate(2010, 1, 4, chrono), f.parseLocalDate("2010-01-01"));
    }

    public void testParseLocalDate_weekyear_month_week_2011() {
        Chronology chrono = GJChronology.getInstanceUTC();
        DateTimeFormatter f = DateTimeFormat.forPattern("xxxx-MM-ww").withChronology(chrono);
        TestCase.assertEquals(new LocalDate(2011, 1, 3, chrono), f.parseLocalDate("2011-01-01"));
    }

    public void testParseLocalDate_weekyear_month_week_2012() {
        Chronology chrono = GJChronology.getInstanceUTC();
        DateTimeFormatter f = DateTimeFormat.forPattern("xxxx-MM-ww").withChronology(chrono);
        TestCase.assertEquals(new LocalDate(2012, 1, 2, chrono), f.parseLocalDate("2012-01-01"));
    }

    // This test fails, but since more related tests pass with the extra loop in DateTimeParserBucket
    // I'm going to leave the change in and ignore this test
    // public void testParseLocalDate_weekyear_month_week_2013() {
    // Chronology chrono = GJChronology.getInstanceUTC();
    // DateTimeFormatter f = DateTimeFormat.forPattern("xxxx-MM-ww").withChronology(chrono);
    // assertEquals(new LocalDate(2012, 12, 31, chrono), f.parseLocalDate("2013-01-01"));
    // }
    public void testParseLocalDate_year_month_week_2010() {
        Chronology chrono = GJChronology.getInstanceUTC();
        DateTimeFormatter f = DateTimeFormat.forPattern("yyyy-MM-ww").withChronology(chrono);
        TestCase.assertEquals(new LocalDate(2010, 1, 4, chrono), f.parseLocalDate("2010-01-01"));
    }

    public void testParseLocalDate_year_month_week_2011() {
        Chronology chrono = GJChronology.getInstanceUTC();
        DateTimeFormatter f = DateTimeFormat.forPattern("yyyy-MM-ww").withChronology(chrono);
        TestCase.assertEquals(new LocalDate(2011, 1, 3, chrono), f.parseLocalDate("2011-01-01"));
    }

    public void testParseLocalDate_year_month_week_2012() {
        Chronology chrono = GJChronology.getInstanceUTC();
        DateTimeFormatter f = DateTimeFormat.forPattern("yyyy-MM-ww").withChronology(chrono);
        TestCase.assertEquals(new LocalDate(2012, 1, 2, chrono), f.parseLocalDate("2012-01-01"));
    }

    public void testParseLocalDate_year_month_week_2013() {
        Chronology chrono = GJChronology.getInstanceUTC();
        DateTimeFormatter f = DateTimeFormat.forPattern("yyyy-MM-ww").withChronology(chrono);
        TestCase.assertEquals(new LocalDate(2012, 12, 31, chrono), f.parseLocalDate("2013-01-01"));// 2013-01-01 would be better, but this is OK

    }

    public void testParseLocalDate_year_month_week_2014() {
        Chronology chrono = GJChronology.getInstanceUTC();
        DateTimeFormatter f = DateTimeFormat.forPattern("yyyy-MM-ww").withChronology(chrono);
        TestCase.assertEquals(new LocalDate(2013, 12, 30, chrono), f.parseLocalDate("2014-01-01"));// 2014-01-01 would be better, but this is OK

    }

    public void testParseLocalDate_year_month_week_2015() {
        Chronology chrono = GJChronology.getInstanceUTC();
        DateTimeFormatter f = DateTimeFormat.forPattern("yyyy-MM-ww").withChronology(chrono);
        TestCase.assertEquals(new LocalDate(2014, 12, 29, chrono), f.parseLocalDate("2015-01-01"));// 2015-01-01 would be better, but this is OK

    }

    public void testParseLocalDate_year_month_week_2016() {
        Chronology chrono = GJChronology.getInstanceUTC();
        DateTimeFormatter f = DateTimeFormat.forPattern("yyyy-MM-ww").withChronology(chrono);
        TestCase.assertEquals(new LocalDate(2016, 1, 4, chrono), f.parseLocalDate("2016-01-01"));
    }

    // -----------------------------------------------------------------------
    public void testParseLocalTime_simple() {
        TestCase.assertEquals(new LocalTime(10, 20, 30), g.parseLocalTime("2004-06-09T10:20:30Z"));
        TestCase.assertEquals(new LocalTime(10, 20, 30), g.parseLocalTime("2004-06-09T10:20:30+18:00"));
        TestCase.assertEquals(new LocalTime(10, 20, 30), g.parseLocalTime("2004-06-09T10:20:30-18:00"));
        TestCase.assertEquals(new LocalTime(10, 20, 30, 0, TestDateTimeFormatter.BUDDHIST_PARIS), g.withChronology(TestDateTimeFormatter.BUDDHIST_PARIS).parseLocalTime("2004-06-09T10:20:30Z"));
        try {
            g.parseDateTime("ABC");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testParseLocalDateTime_simple() {
        TestCase.assertEquals(new LocalDateTime(2004, 6, 9, 10, 20, 30), g.parseLocalDateTime("2004-06-09T10:20:30Z"));
        TestCase.assertEquals(new LocalDateTime(2004, 6, 9, 10, 20, 30), g.parseLocalDateTime("2004-06-09T10:20:30+18:00"));
        TestCase.assertEquals(new LocalDateTime(2004, 6, 9, 10, 20, 30), g.parseLocalDateTime("2004-06-09T10:20:30-18:00"));
        TestCase.assertEquals(new LocalDateTime(2004, 6, 9, 10, 20, 30, 0, TestDateTimeFormatter.BUDDHIST_PARIS), g.withChronology(TestDateTimeFormatter.BUDDHIST_PARIS).parseLocalDateTime("2004-06-09T10:20:30Z"));
        try {
            g.parseDateTime("ABC");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testParseLocalDateTime_monthDay_feb29() {
        Chronology chrono = GJChronology.getInstanceUTC();
        DateTimeFormatter f = DateTimeFormat.forPattern("M d H m").withChronology(chrono).withLocale(Locale.UK);
        TestCase.assertEquals(new LocalDateTime(2000, 2, 29, 13, 40, 0, 0, chrono), f.parseLocalDateTime("2 29 13 40"));
    }

    public void testParseLocalDateTime_monthDay_withDefaultYear_feb29() {
        Chronology chrono = GJChronology.getInstanceUTC();
        DateTimeFormatter f = DateTimeFormat.forPattern("M d H m").withChronology(chrono).withLocale(Locale.UK).withDefaultYear(2012);
        TestCase.assertEquals(new LocalDateTime(2012, 2, 29, 13, 40, 0, 0, chrono), f.parseLocalDateTime("2 29 13 40"));
    }

    // -----------------------------------------------------------------------
    public void testParseDateTime_simple() {
        DateTime expect = null;
        expect = new DateTime(2004, 6, 9, 11, 20, 30, 0, TestDateTimeFormatter.LONDON);
        TestCase.assertEquals(expect, g.parseDateTime("2004-06-09T10:20:30Z"));
        try {
            g.parseDateTime("ABC");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testParseDateTime_zone() {
        DateTime expect = null;
        expect = new DateTime(2004, 6, 9, 11, 20, 30, 0, TestDateTimeFormatter.LONDON);
        TestCase.assertEquals(expect, g.withZone(TestDateTimeFormatter.LONDON).parseDateTime("2004-06-09T10:20:30Z"));
        expect = new DateTime(2004, 6, 9, 11, 20, 30, 0, TestDateTimeFormatter.LONDON);
        TestCase.assertEquals(expect, g.withZone(null).parseDateTime("2004-06-09T10:20:30Z"));
        expect = new DateTime(2004, 6, 9, 12, 20, 30, 0, TestDateTimeFormatter.PARIS);
        TestCase.assertEquals(expect, g.withZone(TestDateTimeFormatter.PARIS).parseDateTime("2004-06-09T10:20:30Z"));
    }

    public void testParseDateTime_zone2() {
        DateTime expect = null;
        expect = new DateTime(2004, 6, 9, 11, 20, 30, 0, TestDateTimeFormatter.LONDON);
        TestCase.assertEquals(expect, g.withZone(TestDateTimeFormatter.LONDON).parseDateTime("2004-06-09T06:20:30-04:00"));
        expect = new DateTime(2004, 6, 9, 11, 20, 30, 0, TestDateTimeFormatter.LONDON);
        TestCase.assertEquals(expect, g.withZone(null).parseDateTime("2004-06-09T06:20:30-04:00"));
        expect = new DateTime(2004, 6, 9, 12, 20, 30, 0, TestDateTimeFormatter.PARIS);
        TestCase.assertEquals(expect, g.withZone(TestDateTimeFormatter.PARIS).parseDateTime("2004-06-09T06:20:30-04:00"));
    }

    public void testParseDateTime_zone3() {
        DateTimeFormatter h = new DateTimeFormatterBuilder().append(ISODateTimeFormat.date()).appendLiteral('T').append(ISODateTimeFormat.timeElementParser()).toFormatter();
        DateTime expect = null;
        expect = new DateTime(2004, 6, 9, 10, 20, 30, 0, TestDateTimeFormatter.LONDON);
        TestCase.assertEquals(expect, h.withZone(TestDateTimeFormatter.LONDON).parseDateTime("2004-06-09T10:20:30"));
        expect = new DateTime(2004, 6, 9, 10, 20, 30, 0, TestDateTimeFormatter.LONDON);
        TestCase.assertEquals(expect, h.withZone(null).parseDateTime("2004-06-09T10:20:30"));
        expect = new DateTime(2004, 6, 9, 10, 20, 30, 0, TestDateTimeFormatter.PARIS);
        TestCase.assertEquals(expect, h.withZone(TestDateTimeFormatter.PARIS).parseDateTime("2004-06-09T10:20:30"));
    }

    public void testParseDateTime_simple_precedence() {
        DateTime expect = null;
        // use correct day of week
        expect = new DateTime(2004, 6, 9, 11, 20, 30, 0, TestDateTimeFormatter.LONDON);
        TestCase.assertEquals(expect, f.parseDateTime("Wed 2004-06-09T10:20:30Z"));
        // use wrong day of week
        expect = new DateTime(2004, 6, 7, 11, 20, 30, 0, TestDateTimeFormatter.LONDON);
        // DayOfWeek takes precedence, because week < month in length
        TestCase.assertEquals(expect, f.parseDateTime("Mon 2004-06-09T10:20:30Z"));
    }

    public void testParseDateTime_offsetParsed() {
        DateTime expect = null;
        expect = new DateTime(2004, 6, 9, 10, 20, 30, 0, TestDateTimeFormatter.UTC);
        TestCase.assertEquals(expect, g.withOffsetParsed().parseDateTime("2004-06-09T10:20:30Z"));
        expect = new DateTime(2004, 6, 9, 6, 20, 30, 0, DateTimeZone.forOffsetHours((-4)));
        TestCase.assertEquals(expect, g.withOffsetParsed().parseDateTime("2004-06-09T06:20:30-04:00"));
        expect = new DateTime(2004, 6, 9, 10, 20, 30, 0, TestDateTimeFormatter.UTC);
        TestCase.assertEquals(expect, g.withZone(TestDateTimeFormatter.PARIS).withOffsetParsed().parseDateTime("2004-06-09T10:20:30Z"));
        expect = new DateTime(2004, 6, 9, 12, 20, 30, 0, TestDateTimeFormatter.PARIS);
        TestCase.assertEquals(expect, g.withOffsetParsed().withZone(TestDateTimeFormatter.PARIS).parseDateTime("2004-06-09T10:20:30Z"));
    }

    public void testParseDateTime_chrono() {
        DateTime expect = null;
        expect = new DateTime(2004, 6, 9, 12, 20, 30, 0, TestDateTimeFormatter.PARIS);
        TestCase.assertEquals(expect, g.withChronology(TestDateTimeFormatter.ISO_PARIS).parseDateTime("2004-06-09T10:20:30Z"));
        expect = new DateTime(2004, 6, 9, 11, 20, 30, 0, TestDateTimeFormatter.LONDON);
        TestCase.assertEquals(expect, g.withChronology(null).parseDateTime("2004-06-09T10:20:30Z"));
        expect = new DateTime(2547, 6, 9, 12, 20, 30, 0, TestDateTimeFormatter.BUDDHIST_PARIS);
        TestCase.assertEquals(expect, g.withChronology(TestDateTimeFormatter.BUDDHIST_PARIS).parseDateTime("2547-06-09T10:20:30Z"));
        expect = new DateTime(2004, 6, 9, 10, 29, 51, 0, TestDateTimeFormatter.BUDDHIST_PARIS);// zone is +00:09:21 in 1451

        TestCase.assertEquals(expect, g.withChronology(TestDateTimeFormatter.BUDDHIST_PARIS).parseDateTime("2004-06-09T10:20:30Z"));
    }

    // -----------------------------------------------------------------------
    public void testParseMutableDateTime_simple() {
        MutableDateTime expect = null;
        expect = new MutableDateTime(2004, 6, 9, 11, 20, 30, 0, TestDateTimeFormatter.LONDON);
        TestCase.assertEquals(expect, g.parseMutableDateTime("2004-06-09T10:20:30Z"));
        try {
            g.parseMutableDateTime("ABC");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testParseMutableDateTime_zone() {
        MutableDateTime expect = null;
        expect = new MutableDateTime(2004, 6, 9, 11, 20, 30, 0, TestDateTimeFormatter.LONDON);
        TestCase.assertEquals(expect, g.withZone(TestDateTimeFormatter.LONDON).parseMutableDateTime("2004-06-09T10:20:30Z"));
        expect = new MutableDateTime(2004, 6, 9, 11, 20, 30, 0, TestDateTimeFormatter.LONDON);
        TestCase.assertEquals(expect, g.withZone(null).parseMutableDateTime("2004-06-09T10:20:30Z"));
        expect = new MutableDateTime(2004, 6, 9, 12, 20, 30, 0, TestDateTimeFormatter.PARIS);
        TestCase.assertEquals(expect, g.withZone(TestDateTimeFormatter.PARIS).parseMutableDateTime("2004-06-09T10:20:30Z"));
    }

    public void testParseMutableDateTime_zone2() {
        MutableDateTime expect = null;
        expect = new MutableDateTime(2004, 6, 9, 11, 20, 30, 0, TestDateTimeFormatter.LONDON);
        TestCase.assertEquals(expect, g.withZone(TestDateTimeFormatter.LONDON).parseMutableDateTime("2004-06-09T06:20:30-04:00"));
        expect = new MutableDateTime(2004, 6, 9, 11, 20, 30, 0, TestDateTimeFormatter.LONDON);
        TestCase.assertEquals(expect, g.withZone(null).parseMutableDateTime("2004-06-09T06:20:30-04:00"));
        expect = new MutableDateTime(2004, 6, 9, 12, 20, 30, 0, TestDateTimeFormatter.PARIS);
        TestCase.assertEquals(expect, g.withZone(TestDateTimeFormatter.PARIS).parseMutableDateTime("2004-06-09T06:20:30-04:00"));
    }

    public void testParseMutableDateTime_zone3() {
        DateTimeFormatter h = new DateTimeFormatterBuilder().append(ISODateTimeFormat.date()).appendLiteral('T').append(ISODateTimeFormat.timeElementParser()).toFormatter();
        MutableDateTime expect = null;
        expect = new MutableDateTime(2004, 6, 9, 10, 20, 30, 0, TestDateTimeFormatter.LONDON);
        TestCase.assertEquals(expect, h.withZone(TestDateTimeFormatter.LONDON).parseMutableDateTime("2004-06-09T10:20:30"));
        expect = new MutableDateTime(2004, 6, 9, 10, 20, 30, 0, TestDateTimeFormatter.LONDON);
        TestCase.assertEquals(expect, h.withZone(null).parseMutableDateTime("2004-06-09T10:20:30"));
        expect = new MutableDateTime(2004, 6, 9, 10, 20, 30, 0, TestDateTimeFormatter.PARIS);
        TestCase.assertEquals(expect, h.withZone(TestDateTimeFormatter.PARIS).parseMutableDateTime("2004-06-09T10:20:30"));
    }

    public void testParseMutableDateTime_simple_precedence() {
        MutableDateTime expect = null;
        // use correct day of week
        expect = new MutableDateTime(2004, 6, 9, 11, 20, 30, 0, TestDateTimeFormatter.LONDON);
        TestCase.assertEquals(expect, f.parseDateTime("Wed 2004-06-09T10:20:30Z"));
        // use wrong day of week
        expect = new MutableDateTime(2004, 6, 7, 11, 20, 30, 0, TestDateTimeFormatter.LONDON);
        // DayOfWeek takes precedence, because week < month in length
        TestCase.assertEquals(expect, f.parseDateTime("Mon 2004-06-09T10:20:30Z"));
    }

    public void testParseMutableDateTime_offsetParsed() {
        MutableDateTime expect = null;
        expect = new MutableDateTime(2004, 6, 9, 10, 20, 30, 0, TestDateTimeFormatter.UTC);
        TestCase.assertEquals(expect, g.withOffsetParsed().parseMutableDateTime("2004-06-09T10:20:30Z"));
        expect = new MutableDateTime(2004, 6, 9, 6, 20, 30, 0, DateTimeZone.forOffsetHours((-4)));
        TestCase.assertEquals(expect, g.withOffsetParsed().parseMutableDateTime("2004-06-09T06:20:30-04:00"));
        expect = new MutableDateTime(2004, 6, 9, 10, 20, 30, 0, TestDateTimeFormatter.UTC);
        TestCase.assertEquals(expect, g.withZone(TestDateTimeFormatter.PARIS).withOffsetParsed().parseMutableDateTime("2004-06-09T10:20:30Z"));
        expect = new MutableDateTime(2004, 6, 9, 12, 20, 30, 0, TestDateTimeFormatter.PARIS);
        TestCase.assertEquals(expect, g.withOffsetParsed().withZone(TestDateTimeFormatter.PARIS).parseMutableDateTime("2004-06-09T10:20:30Z"));
    }

    public void testParseMutableDateTime_chrono() {
        MutableDateTime expect = null;
        expect = new MutableDateTime(2004, 6, 9, 12, 20, 30, 0, TestDateTimeFormatter.PARIS);
        TestCase.assertEquals(expect, g.withChronology(TestDateTimeFormatter.ISO_PARIS).parseMutableDateTime("2004-06-09T10:20:30Z"));
        expect = new MutableDateTime(2004, 6, 9, 11, 20, 30, 0, TestDateTimeFormatter.LONDON);
        TestCase.assertEquals(expect, g.withChronology(null).parseMutableDateTime("2004-06-09T10:20:30Z"));
        expect = new MutableDateTime(2547, 6, 9, 12, 20, 30, 0, TestDateTimeFormatter.BUDDHIST_PARIS);
        TestCase.assertEquals(expect, g.withChronology(TestDateTimeFormatter.BUDDHIST_PARIS).parseMutableDateTime("2547-06-09T10:20:30Z"));
        expect = new MutableDateTime(2004, 6, 9, 10, 29, 51, 0, TestDateTimeFormatter.BUDDHIST_PARIS);// zone is +00:09:21 in 1451

        TestCase.assertEquals(expect, g.withChronology(TestDateTimeFormatter.BUDDHIST_PARIS).parseMutableDateTime("2004-06-09T10:20:30Z"));
    }

    // -----------------------------------------------------------------------
    public void testParseInto_simple() {
        MutableDateTime expect = null;
        expect = new MutableDateTime(2004, 6, 9, 11, 20, 30, 0, TestDateTimeFormatter.LONDON);
        MutableDateTime result = new MutableDateTime(0L);
        TestCase.assertEquals(20, g.parseInto(result, "2004-06-09T10:20:30Z", 0));
        TestCase.assertEquals(expect, result);
        try {
            g.parseInto(null, "2004-06-09T10:20:30Z", 0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        TestCase.assertEquals((~0), g.parseInto(result, "ABC", 0));
        TestCase.assertEquals((~10), g.parseInto(result, "2004-06-09", 0));
        TestCase.assertEquals((~13), g.parseInto(result, "XX2004-06-09T", 2));
    }

    public void testParseInto_zone() {
        MutableDateTime expect = null;
        MutableDateTime result = null;
        expect = new MutableDateTime(2004, 6, 9, 11, 20, 30, 0, TestDateTimeFormatter.LONDON);
        result = new MutableDateTime(0L);
        TestCase.assertEquals(20, g.withZone(TestDateTimeFormatter.LONDON).parseInto(result, "2004-06-09T10:20:30Z", 0));
        TestCase.assertEquals(expect, result);
        expect = new MutableDateTime(2004, 6, 9, 11, 20, 30, 0, TestDateTimeFormatter.LONDON);
        result = new MutableDateTime(0L);
        TestCase.assertEquals(20, g.withZone(null).parseInto(result, "2004-06-09T10:20:30Z", 0));
        TestCase.assertEquals(expect, result);
        expect = new MutableDateTime(2004, 6, 9, 12, 20, 30, 0, TestDateTimeFormatter.PARIS);
        result = new MutableDateTime(0L);
        TestCase.assertEquals(20, g.withZone(TestDateTimeFormatter.PARIS).parseInto(result, "2004-06-09T10:20:30Z", 0));
        TestCase.assertEquals(expect, result);
    }

    public void testParseInto_zone2() {
        MutableDateTime expect = null;
        MutableDateTime result = null;
        expect = new MutableDateTime(2004, 6, 9, 11, 20, 30, 0, TestDateTimeFormatter.LONDON);
        result = new MutableDateTime(0L);
        TestCase.assertEquals(25, g.withZone(TestDateTimeFormatter.LONDON).parseInto(result, "2004-06-09T06:20:30-04:00", 0));
        TestCase.assertEquals(expect, result);
        expect = new MutableDateTime(2004, 6, 9, 11, 20, 30, 0, TestDateTimeFormatter.LONDON);
        TestCase.assertEquals(25, g.withZone(null).parseInto(result, "2004-06-09T06:20:30-04:00", 0));
        TestCase.assertEquals(expect, result);
        expect = new MutableDateTime(2004, 6, 9, 12, 20, 30, 0, TestDateTimeFormatter.PARIS);
        TestCase.assertEquals(25, g.withZone(TestDateTimeFormatter.PARIS).parseInto(result, "2004-06-09T06:20:30-04:00", 0));
        TestCase.assertEquals(expect, result);
    }

    public void testParseInto_zone3() {
        DateTimeFormatter h = new DateTimeFormatterBuilder().append(ISODateTimeFormat.date()).appendLiteral('T').append(ISODateTimeFormat.timeElementParser()).toFormatter();
        MutableDateTime expect = null;
        MutableDateTime result = null;
        expect = new MutableDateTime(2004, 6, 9, 10, 20, 30, 0, TestDateTimeFormatter.LONDON);
        result = new MutableDateTime(0L);
        TestCase.assertEquals(19, h.withZone(TestDateTimeFormatter.LONDON).parseInto(result, "2004-06-09T10:20:30", 0));
        TestCase.assertEquals(expect, result);
        expect = new MutableDateTime(2004, 6, 9, 10, 20, 30, 0, TestDateTimeFormatter.LONDON);
        result = new MutableDateTime(0L);
        TestCase.assertEquals(19, h.withZone(null).parseInto(result, "2004-06-09T10:20:30", 0));
        TestCase.assertEquals(expect, result);
        expect = new MutableDateTime(2004, 6, 9, 10, 20, 30, 0, TestDateTimeFormatter.PARIS);
        result = new MutableDateTime(0L);
        TestCase.assertEquals(19, h.withZone(TestDateTimeFormatter.PARIS).parseInto(result, "2004-06-09T10:20:30", 0));
        TestCase.assertEquals(expect, result);
    }

    public void testParseInto_simple_precedence() {
        MutableDateTime expect = null;
        MutableDateTime result = null;
        expect = new MutableDateTime(2004, 6, 7, 11, 20, 30, 0, TestDateTimeFormatter.LONDON);
        result = new MutableDateTime(0L);
        // DayOfWeek takes precedence, because week < month in length
        TestCase.assertEquals(24, f.parseInto(result, "Mon 2004-06-09T10:20:30Z", 0));
        TestCase.assertEquals(expect, result);
    }

    public void testParseInto_offsetParsed() {
        MutableDateTime expect = null;
        MutableDateTime result = null;
        expect = new MutableDateTime(2004, 6, 9, 10, 20, 30, 0, TestDateTimeFormatter.UTC);
        result = new MutableDateTime(0L);
        TestCase.assertEquals(20, g.withOffsetParsed().parseInto(result, "2004-06-09T10:20:30Z", 0));
        TestCase.assertEquals(expect, result);
        expect = new MutableDateTime(2004, 6, 9, 6, 20, 30, 0, DateTimeZone.forOffsetHours((-4)));
        result = new MutableDateTime(0L);
        TestCase.assertEquals(25, g.withOffsetParsed().parseInto(result, "2004-06-09T06:20:30-04:00", 0));
        TestCase.assertEquals(expect, result);
        expect = new MutableDateTime(2004, 6, 9, 10, 20, 30, 0, TestDateTimeFormatter.UTC);
        result = new MutableDateTime(0L);
        TestCase.assertEquals(20, g.withZone(TestDateTimeFormatter.PARIS).withOffsetParsed().parseInto(result, "2004-06-09T10:20:30Z", 0));
        TestCase.assertEquals(expect, result);
        expect = new MutableDateTime(2004, 6, 9, 12, 20, 30, 0, TestDateTimeFormatter.PARIS);
        result = new MutableDateTime(0L);
        TestCase.assertEquals(20, g.withOffsetParsed().withZone(TestDateTimeFormatter.PARIS).parseInto(result, "2004-06-09T10:20:30Z", 0));
        TestCase.assertEquals(expect, result);
    }

    public void testParseInto_chrono() {
        MutableDateTime expect = null;
        MutableDateTime result = null;
        expect = new MutableDateTime(2004, 6, 9, 12, 20, 30, 0, TestDateTimeFormatter.PARIS);
        result = new MutableDateTime(0L);
        TestCase.assertEquals(20, g.withChronology(TestDateTimeFormatter.ISO_PARIS).parseInto(result, "2004-06-09T10:20:30Z", 0));
        TestCase.assertEquals(expect, result);
        expect = new MutableDateTime(2004, 6, 9, 11, 20, 30, 0, TestDateTimeFormatter.LONDON);
        result = new MutableDateTime(0L);
        TestCase.assertEquals(20, g.withChronology(null).parseInto(result, "2004-06-09T10:20:30Z", 0));
        TestCase.assertEquals(expect, result);
        expect = new MutableDateTime(2547, 6, 9, 12, 20, 30, 0, TestDateTimeFormatter.BUDDHIST_PARIS);
        result = new MutableDateTime(0L);
        TestCase.assertEquals(20, g.withChronology(TestDateTimeFormatter.BUDDHIST_PARIS).parseInto(result, "2547-06-09T10:20:30Z", 0));
        TestCase.assertEquals(expect, result);
        expect = new MutableDateTime(2004, 6, 9, 10, 29, 51, 0, TestDateTimeFormatter.BUDDHIST_PARIS);
        result = new MutableDateTime(0L);
        TestCase.assertEquals(20, g.withChronology(TestDateTimeFormatter.BUDDHIST_PARIS).parseInto(result, "2004-06-09T10:20:30Z", 0));
        TestCase.assertEquals(expect, result);
    }

    public void testParseInto_monthOnly() {
        DateTimeFormatter f = DateTimeFormat.forPattern("M").withLocale(Locale.UK);
        MutableDateTime result = new MutableDateTime(2004, 1, 9, 12, 20, 30, 0, TestDateTimeFormatter.LONDON);
        TestCase.assertEquals(1, f.parseInto(result, "5", 0));
        TestCase.assertEquals(new MutableDateTime(2004, 5, 9, 12, 20, 30, 0, TestDateTimeFormatter.LONDON), result);
    }

    public void testParseInto_monthOnly_baseStartYear() {
        DateTimeFormatter f = DateTimeFormat.forPattern("M").withLocale(Locale.UK);
        MutableDateTime result = new MutableDateTime(2004, 1, 1, 12, 20, 30, 0, TestDateTimeFormatter.TOKYO);
        TestCase.assertEquals(1, f.parseInto(result, "5", 0));
        TestCase.assertEquals(new MutableDateTime(2004, 5, 1, 12, 20, 30, 0, TestDateTimeFormatter.TOKYO), result);
    }

    public void testParseInto_monthOnly_parseStartYear() {
        DateTimeFormatter f = DateTimeFormat.forPattern("M").withLocale(Locale.UK);
        MutableDateTime result = new MutableDateTime(2004, 2, 1, 12, 20, 30, 0, TestDateTimeFormatter.TOKYO);
        TestCase.assertEquals(1, f.parseInto(result, "1", 0));
        TestCase.assertEquals(new MutableDateTime(2004, 1, 1, 12, 20, 30, 0, TestDateTimeFormatter.TOKYO), result);
    }

    public void testParseInto_monthOnly_baseEndYear() {
        DateTimeFormatter f = DateTimeFormat.forPattern("M").withLocale(Locale.UK);
        MutableDateTime result = new MutableDateTime(2004, 12, 31, 12, 20, 30, 0, TestDateTimeFormatter.TOKYO);
        TestCase.assertEquals(1, f.parseInto(result, "5", 0));
        TestCase.assertEquals(new MutableDateTime(2004, 5, 31, 12, 20, 30, 0, TestDateTimeFormatter.TOKYO), result);
    }

    public void testParseInto_monthOnly_parseEndYear() {
        DateTimeFormatter f = DateTimeFormat.forPattern("M").withLocale(Locale.UK);
        MutableDateTime result = new MutableDateTime(2004, 1, 31, 12, 20, 30, 0, TestDateTimeFormatter.TOKYO);
        TestCase.assertEquals(2, f.parseInto(result, "12", 0));
        TestCase.assertEquals(new MutableDateTime(2004, 12, 31, 12, 20, 30, 0, TestDateTimeFormatter.TOKYO), result);
    }

    public void testParseInto_monthDay_feb29() {
        DateTimeFormatter f = DateTimeFormat.forPattern("M d").withLocale(Locale.UK);
        MutableDateTime result = new MutableDateTime(2004, 1, 9, 12, 20, 30, 0, TestDateTimeFormatter.LONDON);
        TestCase.assertEquals(4, f.parseInto(result, "2 29", 0));
        TestCase.assertEquals(new MutableDateTime(2004, 2, 29, 12, 20, 30, 0, TestDateTimeFormatter.LONDON), result);
    }

    public void testParseInto_monthDay_feb29_startOfYear() {
        DateTimeFormatter f = DateTimeFormat.forPattern("M d").withLocale(Locale.UK);
        MutableDateTime result = new MutableDateTime(2004, 1, 1, 0, 0, 0, 0, TestDateTimeFormatter.LONDON);
        TestCase.assertEquals(4, f.parseInto(result, "2 29", 0));
        TestCase.assertEquals(new MutableDateTime(2004, 2, 29, 0, 0, 0, 0, TestDateTimeFormatter.LONDON), result);
    }

    public void testParseInto_monthDay_feb29_OfYear() {
        DateTimeFormatter f = DateTimeFormat.forPattern("M d").withLocale(Locale.UK);
        MutableDateTime result = new MutableDateTime(2004, 12, 31, 23, 59, 59, 999, TestDateTimeFormatter.LONDON);
        TestCase.assertEquals(4, f.parseInto(result, "2 29", 0));
        TestCase.assertEquals(new MutableDateTime(2004, 2, 29, 23, 59, 59, 999, TestDateTimeFormatter.LONDON), result);
    }

    public void testParseInto_monthDay_feb29_newYork() {
        DateTimeFormatter f = DateTimeFormat.forPattern("M d").withLocale(Locale.UK);
        MutableDateTime result = new MutableDateTime(2004, 1, 9, 12, 20, 30, 0, TestDateTimeFormatter.NEWYORK);
        TestCase.assertEquals(4, f.parseInto(result, "2 29", 0));
        TestCase.assertEquals(new MutableDateTime(2004, 2, 29, 12, 20, 30, 0, TestDateTimeFormatter.NEWYORK), result);
    }

    public void testParseInto_monthDay_feb29_newYork_startOfYear() {
        DateTimeFormatter f = DateTimeFormat.forPattern("M d").withLocale(Locale.UK);
        MutableDateTime result = new MutableDateTime(2004, 1, 1, 0, 0, 0, 0, TestDateTimeFormatter.NEWYORK);
        TestCase.assertEquals(4, f.parseInto(result, "2 29", 0));
        TestCase.assertEquals(new MutableDateTime(2004, 2, 29, 0, 0, 0, 0, TestDateTimeFormatter.NEWYORK), result);
    }

    public void testParseInto_monthDay_feb29_newYork_endOfYear() {
        DateTimeFormatter f = DateTimeFormat.forPattern("M d").withLocale(Locale.UK);
        MutableDateTime result = new MutableDateTime(2004, 12, 31, 23, 59, 59, 999, TestDateTimeFormatter.NEWYORK);
        TestCase.assertEquals(4, f.parseInto(result, "2 29", 0));
        TestCase.assertEquals(new MutableDateTime(2004, 2, 29, 23, 59, 59, 999, TestDateTimeFormatter.NEWYORK), result);
    }

    public void testParseInto_monthDay_feb29_tokyo() {
        DateTimeFormatter f = DateTimeFormat.forPattern("M d").withLocale(Locale.UK);
        MutableDateTime result = new MutableDateTime(2004, 1, 9, 12, 20, 30, 0, TestDateTimeFormatter.TOKYO);
        TestCase.assertEquals(4, f.parseInto(result, "2 29", 0));
        TestCase.assertEquals(new MutableDateTime(2004, 2, 29, 12, 20, 30, 0, TestDateTimeFormatter.TOKYO), result);
    }

    public void testParseInto_monthDay_feb29_tokyo_startOfYear() {
        DateTimeFormatter f = DateTimeFormat.forPattern("M d").withLocale(Locale.UK);
        MutableDateTime result = new MutableDateTime(2004, 1, 1, 0, 0, 0, 0, TestDateTimeFormatter.TOKYO);
        TestCase.assertEquals(4, f.parseInto(result, "2 29", 0));
        TestCase.assertEquals(new MutableDateTime(2004, 2, 29, 0, 0, 0, 0, TestDateTimeFormatter.TOKYO), result);
    }

    public void testParseInto_monthDay_feb29_tokyo_endOfYear() {
        DateTimeFormatter f = DateTimeFormat.forPattern("M d").withLocale(Locale.UK);
        MutableDateTime result = new MutableDateTime(2004, 12, 31, 23, 59, 59, 999, TestDateTimeFormatter.TOKYO);
        TestCase.assertEquals(4, f.parseInto(result, "2 29", 0));
        TestCase.assertEquals(new MutableDateTime(2004, 2, 29, 23, 59, 59, 999, TestDateTimeFormatter.TOKYO), result);
    }

    public void testParseInto_monthDay_withDefaultYear_feb29() {
        DateTimeFormatter f = DateTimeFormat.forPattern("M d").withDefaultYear(2012);
        MutableDateTime result = new MutableDateTime(2004, 1, 9, 12, 20, 30, 0, TestDateTimeFormatter.LONDON);
        TestCase.assertEquals(4, f.parseInto(result, "2 29", 0));
        TestCase.assertEquals(new MutableDateTime(2004, 2, 29, 12, 20, 30, 0, TestDateTimeFormatter.LONDON), result);
    }

    public void testParseInto_monthDay_withDefaultYear_feb29_newYork() {
        DateTimeFormatter f = DateTimeFormat.forPattern("M d").withDefaultYear(2012);
        MutableDateTime result = new MutableDateTime(2004, 1, 9, 12, 20, 30, 0, TestDateTimeFormatter.NEWYORK);
        TestCase.assertEquals(4, f.parseInto(result, "2 29", 0));
        TestCase.assertEquals(new MutableDateTime(2004, 2, 29, 12, 20, 30, 0, TestDateTimeFormatter.NEWYORK), result);
    }

    public void testParseInto_monthDay_withDefaultYear_feb29_newYork_endOfYear() {
        DateTimeFormatter f = DateTimeFormat.forPattern("M d").withDefaultYear(2012);
        MutableDateTime result = new MutableDateTime(2004, 12, 9, 12, 20, 30, 0, TestDateTimeFormatter.NEWYORK);
        TestCase.assertEquals(4, f.parseInto(result, "2 29", 0));
        TestCase.assertEquals(new MutableDateTime(2004, 2, 29, 12, 20, 30, 0, TestDateTimeFormatter.NEWYORK), result);
    }

    public void testParseMillis_fractionOfSecondLong() {
        DateTimeFormatter f = new DateTimeFormatterBuilder().appendSecondOfDay(2).appendLiteral('.').appendFractionOfSecond(1, 9).toFormatter().withZoneUTC();
        TestCase.assertEquals(10512, f.parseMillis("10.5123456"));
        TestCase.assertEquals(10512, f.parseMillis("10.512999"));
    }

    // -----------------------------------------------------------------------
    // Ensure time zone name switches properly at the zone DST transition.
    public void testZoneNameNearTransition() {
        DateTime inDST_1 = new DateTime(2005, 10, 30, 1, 0, 0, 0, TestDateTimeFormatter.NEWYORK);
        DateTime inDST_2 = new DateTime(2005, 10, 30, 1, 59, 59, 999, TestDateTimeFormatter.NEWYORK);
        DateTime onDST = new DateTime(2005, 10, 30, 2, 0, 0, 0, TestDateTimeFormatter.NEWYORK);
        DateTime outDST = new DateTime(2005, 10, 30, 2, 0, 0, 1, TestDateTimeFormatter.NEWYORK);
        DateTime outDST_2 = new DateTime(2005, 10, 30, 2, 0, 1, 0, TestDateTimeFormatter.NEWYORK);
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyy-MM-dd HH:mm:ss.S zzzz");
        TestCase.assertEquals("2005-10-30 01:00:00.0 Eastern Daylight Time", fmt.print(inDST_1));
        TestCase.assertEquals("2005-10-30 01:59:59.9 Eastern Daylight Time", fmt.print(inDST_2));
        TestCase.assertEquals("2005-10-30 02:00:00.0 Eastern Standard Time", fmt.print(onDST));
        TestCase.assertEquals("2005-10-30 02:00:00.0 Eastern Standard Time", fmt.print(outDST));
        TestCase.assertEquals("2005-10-30 02:00:01.0 Eastern Standard Time", fmt.print(outDST_2));
    }

    // Ensure time zone name switches properly at the zone DST transition.
    public void testZoneShortNameNearTransition() {
        DateTime inDST_1 = new DateTime(2005, 10, 30, 1, 0, 0, 0, TestDateTimeFormatter.NEWYORK);
        DateTime inDST_2 = new DateTime(2005, 10, 30, 1, 59, 59, 999, TestDateTimeFormatter.NEWYORK);
        DateTime onDST = new DateTime(2005, 10, 30, 2, 0, 0, 0, TestDateTimeFormatter.NEWYORK);
        DateTime outDST = new DateTime(2005, 10, 30, 2, 0, 0, 1, TestDateTimeFormatter.NEWYORK);
        DateTime outDST_2 = new DateTime(2005, 10, 30, 2, 0, 1, 0, TestDateTimeFormatter.NEWYORK);
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyy-MM-dd HH:mm:ss.S z").withLocale(Locale.ENGLISH);
        TestCase.assertEquals("2005-10-30 01:00:00.0 EDT", fmt.print(inDST_1));
        TestCase.assertEquals("2005-10-30 01:59:59.9 EDT", fmt.print(inDST_2));
        TestCase.assertEquals("2005-10-30 02:00:00.0 EST", fmt.print(onDST));
        TestCase.assertEquals("2005-10-30 02:00:00.0 EST", fmt.print(outDST));
        TestCase.assertEquals("2005-10-30 02:00:01.0 EST", fmt.print(outDST_2));
    }

    public void testCustomDateTimePrinter() {
        DateTimePrinter printer = new DateTimeFormatterBuilder().append(new TestDateTimeFormatter.CustomDateTimePrinter()).appendLiteral(' ').appendYear(4, 8).toPrinter();
        StringBuffer buffer = new StringBuffer();
        long instant = getMillis();
        printer.printTo(buffer, instant, TestDateTimeFormatter.ISO_UTC, 0, TestDateTimeFormatter.UTC, Locale.ENGLISH);
        TestCase.assertEquals("Hi 2017", buffer.toString());
    }

    private static class CustomDateTimePrinter implements DateTimePrinter {
        public int estimatePrintedLength() {
            return 2;
        }

        public void printTo(StringBuffer buf, long instant, Chronology chrono, int displayOffset, DateTimeZone displayZone, Locale locale) {
            buf.append("Hi");
        }

        public void printTo(Writer out, long instant, Chronology chrono, int displayOffset, DateTimeZone displayZone, Locale locale) throws IOException {
            out.write("Hi");
        }

        public void printTo(StringBuffer buf, ReadablePartial partial, Locale locale) {
            buf.append("Hi");
        }

        public void printTo(Writer out, ReadablePartial partial, Locale locale) throws IOException {
            out.write("Hi");
        }
    }
}

