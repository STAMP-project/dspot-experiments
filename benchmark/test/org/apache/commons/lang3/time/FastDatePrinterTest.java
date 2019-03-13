/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.time;


import java.io.Serializable;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junitpioneer.jupiter.DefaultLocale;
import org.junitpioneer.jupiter.DefaultTimeZone;


/**
 * Unit tests {@link org.apache.commons.lang3.time.FastDatePrinter}.
 *
 * @since 3.0
 */
public class FastDatePrinterTest {
    private static final String YYYY_MM_DD = "yyyy/MM/dd";

    private static final TimeZone NEW_YORK = TimeZone.getTimeZone("America/New_York");

    private static final TimeZone GMT = TimeZone.getTimeZone("GMT");

    private static final TimeZone INDIA = TimeZone.getTimeZone("Asia/Calcutta");

    private static final Locale SWEDEN = new Locale("sv", "SE");

    @DefaultLocale(language = "en", country = "US")
    @DefaultTimeZone("America/New_York")
    @Test
    public void testFormat() {
        final GregorianCalendar cal1 = new GregorianCalendar(2003, 0, 10, 15, 33, 20);
        final GregorianCalendar cal2 = new GregorianCalendar(2003, 6, 10, 9, 0, 0);
        final Date date1 = cal1.getTime();
        final Date date2 = cal2.getTime();
        final long millis1 = date1.getTime();
        final long millis2 = date2.getTime();
        DatePrinter fdf = getInstance("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Assertions.assertEquals(sdf.format(date1), fdf.format(date1));
        Assertions.assertEquals("2003-01-10T15:33:20", fdf.format(date1));
        Assertions.assertEquals("2003-01-10T15:33:20", fdf.format(cal1));
        Assertions.assertEquals("2003-01-10T15:33:20", fdf.format(millis1));
        Assertions.assertEquals("2003-07-10T09:00:00", fdf.format(date2));
        Assertions.assertEquals("2003-07-10T09:00:00", fdf.format(cal2));
        Assertions.assertEquals("2003-07-10T09:00:00", fdf.format(millis2));
        fdf = getInstance("Z");
        Assertions.assertEquals("-0500", fdf.format(date1));
        Assertions.assertEquals("-0500", fdf.format(cal1));
        Assertions.assertEquals("-0500", fdf.format(millis1));
        Assertions.assertEquals("-0400", fdf.format(date2));
        Assertions.assertEquals("-0400", fdf.format(cal2));
        Assertions.assertEquals("-0400", fdf.format(millis2));
        fdf = getInstance("ZZ");
        Assertions.assertEquals("-05:00", fdf.format(date1));
        Assertions.assertEquals("-05:00", fdf.format(cal1));
        Assertions.assertEquals("-05:00", fdf.format(millis1));
        Assertions.assertEquals("-04:00", fdf.format(date2));
        Assertions.assertEquals("-04:00", fdf.format(cal2));
        Assertions.assertEquals("-04:00", fdf.format(millis2));
        final String pattern = "GGGG GGG GG G yyyy yyy yy y MMMM MMM MM M" + " dddd ddd dd d DDDD DDD DD D EEEE EEE EE E aaaa aaa aa a zzzz zzz zz z";
        fdf = getInstance(pattern);
        sdf = new SimpleDateFormat(pattern);
        // SDF bug fix starting with Java 7
        Assertions.assertEquals(sdf.format(date1).replaceAll("2003 03 03 03", "2003 2003 03 2003"), fdf.format(date1));
        Assertions.assertEquals(sdf.format(date2).replaceAll("2003 03 03 03", "2003 2003 03 2003"), fdf.format(date2));
    }

    /**
     * Test case for {@link FastDateParser#FastDateParser(String, TimeZone, Locale)}.
     */
    @Test
    public void testShortDateStyleWithLocales() {
        final Locale usLocale = Locale.US;
        final Locale swedishLocale = new Locale("sv", "SE");
        final Calendar cal = Calendar.getInstance();
        cal.set(2004, Calendar.FEBRUARY, 3);
        DatePrinter fdf = getDateInstance(FastDateFormat.SHORT, usLocale);
        Assertions.assertEquals("2/3/04", fdf.format(cal));
        fdf = getDateInstance(FastDateFormat.SHORT, swedishLocale);
        Assertions.assertEquals("2004-02-03", fdf.format(cal));
    }

    /**
     * Tests that pre-1000AD years get padded with yyyy
     */
    @Test
    public void testLowYearPadding() {
        final Calendar cal = Calendar.getInstance();
        final DatePrinter format = getInstance(FastDatePrinterTest.YYYY_MM_DD);
        cal.set(1, Calendar.JANUARY, 1);
        Assertions.assertEquals("0001/01/01", format.format(cal));
        cal.set(10, Calendar.JANUARY, 1);
        Assertions.assertEquals("0010/01/01", format.format(cal));
        cal.set(100, Calendar.JANUARY, 1);
        Assertions.assertEquals("0100/01/01", format.format(cal));
        cal.set(999, Calendar.JANUARY, 1);
        Assertions.assertEquals("0999/01/01", format.format(cal));
    }

    /**
     * Show Bug #39410 is solved
     */
    @Test
    public void testMilleniumBug() {
        final Calendar cal = Calendar.getInstance();
        final DatePrinter format = getInstance("dd.MM.yyyy");
        cal.set(1000, Calendar.JANUARY, 1);
        Assertions.assertEquals("01.01.1000", format.format(cal));
    }

    /**
     * testLowYearPadding showed that the date was buggy
     * This test confirms it, getting 366 back as a date
     */
    @Test
    public void testSimpleDate() {
        final Calendar cal = Calendar.getInstance();
        final DatePrinter format = getInstance(FastDatePrinterTest.YYYY_MM_DD);
        cal.set(2004, Calendar.DECEMBER, 31);
        Assertions.assertEquals("2004/12/31", format.format(cal));
        cal.set(999, Calendar.DECEMBER, 31);
        Assertions.assertEquals("0999/12/31", format.format(cal));
        cal.set(1, Calendar.MARCH, 2);
        Assertions.assertEquals("0001/03/02", format.format(cal));
    }

    @Test
    public void testLang303() {
        final Calendar cal = Calendar.getInstance();
        cal.set(2004, Calendar.DECEMBER, 31);
        DatePrinter format = getInstance(FastDatePrinterTest.YYYY_MM_DD);
        final String output = format.format(cal);
        format = SerializationUtils.deserialize(SerializationUtils.serialize(((Serializable) (format))));
        Assertions.assertEquals(output, format.format(cal));
    }

    @Test
    public void testLang538() {
        // more commonly constructed with: cal = new GregorianCalendar(2009, 9, 16, 8, 42, 16)
        // for the unit test to work in any time zone, constructing with GMT-8 rather than default locale time zone
        final GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT-8"));
        cal.clear();
        cal.set(2009, Calendar.OCTOBER, 16, 8, 42, 16);
        final DatePrinter format = getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone("GMT"));
        Assertions.assertEquals("2009-10-16T16:42:16.000Z", format.format(cal.getTime()), "dateTime");
        Assertions.assertEquals("2009-10-16T16:42:16.000Z", format.format(cal), "dateTime");
    }

    @Test
    public void testLang645() {
        final Locale locale = new Locale("sv", "SE");
        final Calendar cal = Calendar.getInstance();
        cal.set(2010, Calendar.JANUARY, 1, 12, 0, 0);
        final Date d = cal.getTime();
        final DatePrinter fdf = getInstance("EEEE', week 'ww", locale);
        Assertions.assertEquals("fredag, week 53", fdf.format(d));
    }

    @Test
    public void testEquals() {
        final DatePrinter printer1 = getInstance(FastDatePrinterTest.YYYY_MM_DD);
        final DatePrinter printer2 = getInstance(FastDatePrinterTest.YYYY_MM_DD);
        Assertions.assertEquals(printer1, printer2);
        Assertions.assertEquals(printer1.hashCode(), printer2.hashCode());
        Assertions.assertNotEquals(printer1, new Object());
    }

    @Test
    public void testToStringContainsName() {
        final DatePrinter printer = getInstance(FastDatePrinterTest.YYYY_MM_DD);
        Assertions.assertTrue(printer.toString().startsWith("FastDate"));
    }

    @Test
    public void testPatternMatches() {
        final DatePrinter printer = getInstance(FastDatePrinterTest.YYYY_MM_DD);
        Assertions.assertEquals(FastDatePrinterTest.YYYY_MM_DD, printer.getPattern());
    }

    @Test
    public void testLocaleMatches() {
        final DatePrinter printer = getInstance(FastDatePrinterTest.YYYY_MM_DD, FastDatePrinterTest.SWEDEN);
        Assertions.assertEquals(FastDatePrinterTest.SWEDEN, printer.getLocale());
    }

    @Test
    public void testTimeZoneMatches() {
        final DatePrinter printer = getInstance(FastDatePrinterTest.YYYY_MM_DD, FastDatePrinterTest.NEW_YORK);
        Assertions.assertEquals(FastDatePrinterTest.NEW_YORK, printer.getTimeZone());
    }

    @DefaultTimeZone("UTC")
    @Test
    public void testTimeZoneAsZ() {
        final Calendar c = Calendar.getInstance(FastTimeZone.getGmtTimeZone());
        final FastDateFormat noColonFormat = FastDateFormat.getInstance("Z");
        Assertions.assertEquals("+0000", noColonFormat.format(c));
        final FastDateFormat isoFormat = FastDateFormat.getInstance("ZZ");
        Assertions.assertEquals("Z", isoFormat.format(c));
        final FastDateFormat colonFormat = FastDateFormat.getInstance("ZZZ");
        Assertions.assertEquals("+00:00", colonFormat.format(c));
    }

    @Test
    public void test1806Argument() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> getInstance("XXXX"));
    }

    private enum Expected1806 {

        India(FastDatePrinterTest.INDIA, "+05", "+0530", "+05:30"),
        Greenwich(FastDatePrinterTest.GMT, "Z", "Z", "Z"),
        NewYork(FastDatePrinterTest.NEW_YORK, "-05", "-0500", "-05:00");
        Expected1806(final TimeZone zone, final String one, final String two, final String three) {
            this.zone = zone;
            this.one = one;
            this.two = two;
            this.three = three;
        }

        final TimeZone zone;

        final String one;

        final String two;

        final String three;
    }

    @Test
    public void test1806() {
        for (final FastDatePrinterTest.Expected1806 trial : FastDatePrinterTest.Expected1806.values()) {
            final Calendar cal = FastDatePrinterTest.initializeCalendar(trial.zone);
            DatePrinter printer = getInstance("X", trial.zone);
            Assertions.assertEquals(trial.one, printer.format(cal));
            printer = getInstance("XX", trial.zone);
            Assertions.assertEquals(trial.two, printer.format(cal));
            printer = getInstance("XXX", trial.zone);
            Assertions.assertEquals(trial.three, printer.format(cal));
        }
    }

    @Test
    public void testLang1103() {
        final Calendar cal = Calendar.getInstance(FastDatePrinterTest.SWEDEN);
        cal.set(Calendar.DAY_OF_MONTH, 2);
        Assertions.assertEquals("2", getInstance("d", FastDatePrinterTest.SWEDEN).format(cal));
        Assertions.assertEquals("02", getInstance("dd", FastDatePrinterTest.SWEDEN).format(cal));
        Assertions.assertEquals("002", getInstance("ddd", FastDatePrinterTest.SWEDEN).format(cal));
        Assertions.assertEquals("0002", getInstance("dddd", FastDatePrinterTest.SWEDEN).format(cal));
        Assertions.assertEquals("00002", getInstance("ddddd", FastDatePrinterTest.SWEDEN).format(cal));
    }

    /**
     * According to LANG-916 (https://issues.apache.org/jira/browse/LANG-916),
     * the format method did contain a bug: it did not use the TimeZone data.
     *
     * This method test that the bug is fixed.
     */
    @Test
    public void testLang916() {
        final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
        cal.clear();
        cal.set(2009, 9, 16, 8, 42, 16);
        // calendar fast.
        {
            final String value = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss Z", TimeZone.getTimeZone("Europe/Paris")).format(cal);
            Assertions.assertEquals("2009-10-16T08:42:16 +0200", value, "calendar");
        }
        {
            final String value = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss Z", TimeZone.getTimeZone("Asia/Kolkata")).format(cal);
            Assertions.assertEquals("2009-10-16T12:12:16 +0530", value, "calendar");
        }
        {
            final String value = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss Z", TimeZone.getTimeZone("Europe/London")).format(cal);
            Assertions.assertEquals("2009-10-16T07:42:16 +0100", value, "calendar");
        }
    }

    @Test
    public void testHourFormats() {
        final Calendar calendar = Calendar.getInstance();
        calendar.clear();
        final DatePrinter printer = getInstance("K k H h");
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        Assertions.assertEquals("0 24 0 12", printer.format(calendar));
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        Assertions.assertEquals("0 12 12 12", printer.format(calendar));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        Assertions.assertEquals("11 23 23 11", printer.format(calendar));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testStringBufferOptions() {
        final DatePrinter format = getInstance("yyyy-MM-dd HH:mm:ss.SSS Z", TimeZone.getTimeZone("GMT"));
        final Calendar calendar = Calendar.getInstance();
        final StringBuffer sb = new StringBuffer();
        final String expected = format.format(calendar, sb, new FieldPosition(0)).toString();
        sb.setLength(0);
        Assertions.assertEquals(expected, format.format(calendar, sb).toString());
        sb.setLength(0);
        final Date date = calendar.getTime();
        Assertions.assertEquals(expected, format.format(date, sb, new FieldPosition(0)).toString());
        sb.setLength(0);
        Assertions.assertEquals(expected, format.format(date, sb).toString());
        sb.setLength(0);
        final long epoch = date.getTime();
        Assertions.assertEquals(expected, format.format(epoch, sb, new FieldPosition(0)).toString());
        sb.setLength(0);
        Assertions.assertEquals(expected, format.format(epoch, sb).toString());
    }

    @Test
    public void testAppendableOptions() {
        final DatePrinter format = getInstance("yyyy-MM-dd HH:mm:ss.SSS Z", TimeZone.getTimeZone("GMT"));
        final Calendar calendar = Calendar.getInstance();
        final StringBuilder sb = new StringBuilder();
        final String expected = format.format(calendar, sb).toString();
        sb.setLength(0);
        final Date date = calendar.getTime();
        Assertions.assertEquals(expected, format.format(date, sb).toString());
        sb.setLength(0);
        final long epoch = date.getTime();
        Assertions.assertEquals(expected, format.format(epoch, sb).toString());
    }

    @Test
    public void testDayNumberOfWeek() {
        final DatePrinter printer = getInstance("u");
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Assertions.assertEquals("1", printer.format(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        Assertions.assertEquals("6", printer.format(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        Assertions.assertEquals("7", printer.format(calendar.getTime()));
    }
}

