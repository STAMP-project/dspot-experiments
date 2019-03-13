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


import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Calendar;
import java.util.TimeZone;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * TestCase for DurationFormatUtils.
 */
public class DurationFormatUtilsTest {
    // -----------------------------------------------------------------------
    @Test
    public void testConstructor() {
        Assertions.assertNotNull(new DurationFormatUtils());
        final Constructor<?>[] cons = DurationFormatUtils.class.getDeclaredConstructors();
        Assertions.assertEquals(1, cons.length);
        Assertions.assertTrue(Modifier.isPublic(cons[0].getModifiers()));
        Assertions.assertTrue(Modifier.isPublic(DurationFormatUtils.class.getModifiers()));
        Assertions.assertFalse(Modifier.isFinal(DurationFormatUtils.class.getModifiers()));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testFormatDurationWords() {
        String text = null;
        text = DurationFormatUtils.formatDurationWords((50 * 1000), true, false);
        Assertions.assertEquals("50 seconds", text);
        text = DurationFormatUtils.formatDurationWords((65 * 1000), true, false);
        Assertions.assertEquals("1 minute 5 seconds", text);
        text = DurationFormatUtils.formatDurationWords((120 * 1000), true, false);
        Assertions.assertEquals("2 minutes 0 seconds", text);
        text = DurationFormatUtils.formatDurationWords((121 * 1000), true, false);
        Assertions.assertEquals("2 minutes 1 second", text);
        text = DurationFormatUtils.formatDurationWords(((72 * 60) * 1000), true, false);
        Assertions.assertEquals("1 hour 12 minutes 0 seconds", text);
        text = DurationFormatUtils.formatDurationWords((((24 * 60) * 60) * 1000), true, false);
        Assertions.assertEquals("1 day 0 hours 0 minutes 0 seconds", text);
        text = DurationFormatUtils.formatDurationWords((50 * 1000), true, true);
        Assertions.assertEquals("50 seconds", text);
        text = DurationFormatUtils.formatDurationWords((65 * 1000), true, true);
        Assertions.assertEquals("1 minute 5 seconds", text);
        text = DurationFormatUtils.formatDurationWords((120 * 1000), true, true);
        Assertions.assertEquals("2 minutes", text);
        text = DurationFormatUtils.formatDurationWords((121 * 1000), true, true);
        Assertions.assertEquals("2 minutes 1 second", text);
        text = DurationFormatUtils.formatDurationWords(((72 * 60) * 1000), true, true);
        Assertions.assertEquals("1 hour 12 minutes", text);
        text = DurationFormatUtils.formatDurationWords((((24 * 60) * 60) * 1000), true, true);
        Assertions.assertEquals("1 day", text);
        text = DurationFormatUtils.formatDurationWords((50 * 1000), false, true);
        Assertions.assertEquals("0 days 0 hours 0 minutes 50 seconds", text);
        text = DurationFormatUtils.formatDurationWords((65 * 1000), false, true);
        Assertions.assertEquals("0 days 0 hours 1 minute 5 seconds", text);
        text = DurationFormatUtils.formatDurationWords((120 * 1000), false, true);
        Assertions.assertEquals("0 days 0 hours 2 minutes", text);
        text = DurationFormatUtils.formatDurationWords((121 * 1000), false, true);
        Assertions.assertEquals("0 days 0 hours 2 minutes 1 second", text);
        text = DurationFormatUtils.formatDurationWords(((72 * 60) * 1000), false, true);
        Assertions.assertEquals("0 days 1 hour 12 minutes", text);
        text = DurationFormatUtils.formatDurationWords((((24 * 60) * 60) * 1000), false, true);
        Assertions.assertEquals("1 day", text);
        text = DurationFormatUtils.formatDurationWords((50 * 1000), false, false);
        Assertions.assertEquals("0 days 0 hours 0 minutes 50 seconds", text);
        text = DurationFormatUtils.formatDurationWords((65 * 1000), false, false);
        Assertions.assertEquals("0 days 0 hours 1 minute 5 seconds", text);
        text = DurationFormatUtils.formatDurationWords((120 * 1000), false, false);
        Assertions.assertEquals("0 days 0 hours 2 minutes 0 seconds", text);
        text = DurationFormatUtils.formatDurationWords((121 * 1000), false, false);
        Assertions.assertEquals("0 days 0 hours 2 minutes 1 second", text);
        text = DurationFormatUtils.formatDurationWords(((72 * 60) * 1000), false, false);
        Assertions.assertEquals("0 days 1 hour 12 minutes 0 seconds", text);
        text = DurationFormatUtils.formatDurationWords(((((24 * 60) * 60) * 1000) + ((72 * 60) * 1000)), false, false);
        Assertions.assertEquals("1 day 1 hour 12 minutes 0 seconds", text);
        text = DurationFormatUtils.formatDurationWords((((((2 * 24) * 60) * 60) * 1000) + ((72 * 60) * 1000)), false, false);
        Assertions.assertEquals("2 days 1 hour 12 minutes 0 seconds", text);
        for (int i = 2; i < 31; i++) {
            text = DurationFormatUtils.formatDurationWords(((((i * 24) * 60) * 60) * 1000L), false, false);
            Assertions.assertEquals((i + " days 0 hours 0 minutes 0 seconds"), text);
        }
    }

    /**
     * Tests that "1 &lt;unit&gt;s" gets converted to "1 &lt;unit&gt;" but that "11 &lt;unit&gt;s" is left alone.
     */
    @Test
    public void testFormatDurationPluralWords() {
        final long oneSecond = 1000;
        final long oneMinute = oneSecond * 60;
        final long oneHour = oneMinute * 60;
        final long oneDay = oneHour * 24;
        String text = null;
        text = DurationFormatUtils.formatDurationWords(oneSecond, false, false);
        Assertions.assertEquals("0 days 0 hours 0 minutes 1 second", text);
        text = DurationFormatUtils.formatDurationWords((oneSecond * 2), false, false);
        Assertions.assertEquals("0 days 0 hours 0 minutes 2 seconds", text);
        text = DurationFormatUtils.formatDurationWords((oneSecond * 11), false, false);
        Assertions.assertEquals("0 days 0 hours 0 minutes 11 seconds", text);
        text = DurationFormatUtils.formatDurationWords(oneMinute, false, false);
        Assertions.assertEquals("0 days 0 hours 1 minute 0 seconds", text);
        text = DurationFormatUtils.formatDurationWords((oneMinute * 2), false, false);
        Assertions.assertEquals("0 days 0 hours 2 minutes 0 seconds", text);
        text = DurationFormatUtils.formatDurationWords((oneMinute * 11), false, false);
        Assertions.assertEquals("0 days 0 hours 11 minutes 0 seconds", text);
        text = DurationFormatUtils.formatDurationWords((oneMinute + oneSecond), false, false);
        Assertions.assertEquals("0 days 0 hours 1 minute 1 second", text);
        text = DurationFormatUtils.formatDurationWords(oneHour, false, false);
        Assertions.assertEquals("0 days 1 hour 0 minutes 0 seconds", text);
        text = DurationFormatUtils.formatDurationWords((oneHour * 2), false, false);
        Assertions.assertEquals("0 days 2 hours 0 minutes 0 seconds", text);
        text = DurationFormatUtils.formatDurationWords((oneHour * 11), false, false);
        Assertions.assertEquals("0 days 11 hours 0 minutes 0 seconds", text);
        text = DurationFormatUtils.formatDurationWords(((oneHour + oneMinute) + oneSecond), false, false);
        Assertions.assertEquals("0 days 1 hour 1 minute 1 second", text);
        text = DurationFormatUtils.formatDurationWords(oneDay, false, false);
        Assertions.assertEquals("1 day 0 hours 0 minutes 0 seconds", text);
        text = DurationFormatUtils.formatDurationWords((oneDay * 2), false, false);
        Assertions.assertEquals("2 days 0 hours 0 minutes 0 seconds", text);
        text = DurationFormatUtils.formatDurationWords((oneDay * 11), false, false);
        Assertions.assertEquals("11 days 0 hours 0 minutes 0 seconds", text);
        text = DurationFormatUtils.formatDurationWords((((oneDay + oneHour) + oneMinute) + oneSecond), false, false);
        Assertions.assertEquals("1 day 1 hour 1 minute 1 second", text);
    }

    @Test
    public void testFormatNegativeDurationWords() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> DurationFormatUtils.formatDurationWords((-5000), true, true));
    }

    @Test
    public void testFormatDurationHMS() {
        long time = 0;
        Assertions.assertEquals("00:00:00.000", DurationFormatUtils.formatDurationHMS(time));
        time = 1;
        Assertions.assertEquals("00:00:00.001", DurationFormatUtils.formatDurationHMS(time));
        time = 15;
        Assertions.assertEquals("00:00:00.015", DurationFormatUtils.formatDurationHMS(time));
        time = 165;
        Assertions.assertEquals("00:00:00.165", DurationFormatUtils.formatDurationHMS(time));
        time = 1675;
        Assertions.assertEquals("00:00:01.675", DurationFormatUtils.formatDurationHMS(time));
        time = 13465;
        Assertions.assertEquals("00:00:13.465", DurationFormatUtils.formatDurationHMS(time));
        time = 72789;
        Assertions.assertEquals("00:01:12.789", DurationFormatUtils.formatDurationHMS(time));
        time = 12789 + (32 * 60000);
        Assertions.assertEquals("00:32:12.789", DurationFormatUtils.formatDurationHMS(time));
        time = 12789 + (62 * 60000);
        Assertions.assertEquals("01:02:12.789", DurationFormatUtils.formatDurationHMS(time));
    }

    @Test
    public void testFormatNegativeDurationHMS() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> DurationFormatUtils.formatDurationHMS((-5000)));
    }

    @Test
    public void testFormatDurationISO() {
        Assertions.assertEquals("P0Y0M0DT0H0M0.000S", DurationFormatUtils.formatDurationISO(0L));
        Assertions.assertEquals("P0Y0M0DT0H0M0.001S", DurationFormatUtils.formatDurationISO(1L));
        Assertions.assertEquals("P0Y0M0DT0H0M0.010S", DurationFormatUtils.formatDurationISO(10L));
        Assertions.assertEquals("P0Y0M0DT0H0M0.100S", DurationFormatUtils.formatDurationISO(100L));
        Assertions.assertEquals("P0Y0M0DT0H1M15.321S", DurationFormatUtils.formatDurationISO(75321L));
    }

    @Test
    public void testFormatNegativeDurationISO() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> DurationFormatUtils.formatDurationISO((-5000)));
    }

    @Test
    public void testFormatDuration() {
        long duration = 0;
        Assertions.assertEquals("0", DurationFormatUtils.formatDuration(duration, "y"));
        Assertions.assertEquals("0", DurationFormatUtils.formatDuration(duration, "M"));
        Assertions.assertEquals("0", DurationFormatUtils.formatDuration(duration, "d"));
        Assertions.assertEquals("0", DurationFormatUtils.formatDuration(duration, "H"));
        Assertions.assertEquals("0", DurationFormatUtils.formatDuration(duration, "m"));
        Assertions.assertEquals("0", DurationFormatUtils.formatDuration(duration, "s"));
        Assertions.assertEquals("0", DurationFormatUtils.formatDuration(duration, "S"));
        Assertions.assertEquals("0000", DurationFormatUtils.formatDuration(duration, "SSSS"));
        Assertions.assertEquals("0000", DurationFormatUtils.formatDuration(duration, "yyyy"));
        Assertions.assertEquals("0000", DurationFormatUtils.formatDuration(duration, "yyMM"));
        duration = 60 * 1000;
        Assertions.assertEquals("0", DurationFormatUtils.formatDuration(duration, "y"));
        Assertions.assertEquals("0", DurationFormatUtils.formatDuration(duration, "M"));
        Assertions.assertEquals("0", DurationFormatUtils.formatDuration(duration, "d"));
        Assertions.assertEquals("0", DurationFormatUtils.formatDuration(duration, "H"));
        Assertions.assertEquals("1", DurationFormatUtils.formatDuration(duration, "m"));
        Assertions.assertEquals("60", DurationFormatUtils.formatDuration(duration, "s"));
        Assertions.assertEquals("60000", DurationFormatUtils.formatDuration(duration, "S"));
        Assertions.assertEquals("01:00", DurationFormatUtils.formatDuration(duration, "mm:ss"));
        final Calendar base = Calendar.getInstance();
        base.set(2000, Calendar.JANUARY, 1, 0, 0, 0);
        base.set(Calendar.MILLISECOND, 0);
        final Calendar cal = Calendar.getInstance();
        cal.set(2003, Calendar.FEBRUARY, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        duration = (cal.getTime().getTime()) - (base.getTime().getTime());// duration from 2000-01-01 to cal

        // don't use 1970 in test as time zones were less reliable in 1970 than now
        // remember that duration formatting ignores time zones, working on strict hour lengths
        final int days = ((366 + 365) + 365) + 31;
        Assertions.assertEquals(("0 0 " + days), DurationFormatUtils.formatDuration(duration, "y M d"));
    }

    @Test
    public void testFormatNegativeDuration() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> DurationFormatUtils.formatDuration((-5000), "S", true));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testFormatPeriodISO() {
        final TimeZone timeZone = TimeZone.getTimeZone("GMT-3");
        final Calendar base = Calendar.getInstance(timeZone);
        base.set(1970, Calendar.JANUARY, 1, 0, 0, 0);
        base.set(Calendar.MILLISECOND, 0);
        final Calendar cal = Calendar.getInstance(timeZone);
        cal.set(2002, Calendar.FEBRUARY, 23, 9, 11, 12);
        cal.set(Calendar.MILLISECOND, 1);
        String text;
        // repeat a test from testDateTimeISO to compare extended and not extended.
        text = DateFormatUtils.format(cal, DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern(), timeZone);
        Assertions.assertEquals("2002-02-23T09:11:12-03:00", text);
        // test fixture is the same as above, but now with extended format.
        text = DurationFormatUtils.formatPeriod(base.getTime().getTime(), cal.getTime().getTime(), DurationFormatUtils.ISO_EXTENDED_FORMAT_PATTERN, false, timeZone);
        Assertions.assertEquals("P32Y1M22DT9H11M12.001S", text);
        // test fixture from example in http://www.w3.org/TR/xmlschema-2/#duration
        cal.set(1971, Calendar.FEBRUARY, 3, 10, 30, 0);
        cal.set(Calendar.MILLISECOND, 0);
        text = DurationFormatUtils.formatPeriod(base.getTime().getTime(), cal.getTime().getTime(), DurationFormatUtils.ISO_EXTENDED_FORMAT_PATTERN, false, timeZone);
        Assertions.assertEquals("P1Y1M2DT10H30M0.000S", text);
        // want a way to say 'don't print the seconds in format()' or other fields for that matter:
        // assertEquals("P1Y2M3DT10H30M", text);
    }

    @Test
    public void testFormatPeriodISOStartGreaterEnd() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> DurationFormatUtils.formatPeriodISO(5000, 2000));
    }

    @Test
    public void testFormatPeriodISOMethod() {
        Assertions.assertEquals("P0Y0M0DT0H0M0.000S", DurationFormatUtils.formatPeriodISO(0L, 0L));
        Assertions.assertEquals("P0Y0M0DT0H0M1.000S", DurationFormatUtils.formatPeriodISO(0L, 1000L));
        Assertions.assertEquals("P0Y0M0DT0H1M1.000S", DurationFormatUtils.formatPeriodISO(0L, 61000L));
    }

    @Test
    public void testFormatPeriod() {
        final Calendar cal1970 = Calendar.getInstance();
        cal1970.set(1970, Calendar.JANUARY, 1, 0, 0, 0);
        cal1970.set(Calendar.MILLISECOND, 0);
        final long time1970 = cal1970.getTime().getTime();
        Assertions.assertEquals("0", DurationFormatUtils.formatPeriod(time1970, time1970, "y"));
        Assertions.assertEquals("0", DurationFormatUtils.formatPeriod(time1970, time1970, "M"));
        Assertions.assertEquals("0", DurationFormatUtils.formatPeriod(time1970, time1970, "d"));
        Assertions.assertEquals("0", DurationFormatUtils.formatPeriod(time1970, time1970, "H"));
        Assertions.assertEquals("0", DurationFormatUtils.formatPeriod(time1970, time1970, "m"));
        Assertions.assertEquals("0", DurationFormatUtils.formatPeriod(time1970, time1970, "s"));
        Assertions.assertEquals("0", DurationFormatUtils.formatPeriod(time1970, time1970, "S"));
        Assertions.assertEquals("0000", DurationFormatUtils.formatPeriod(time1970, time1970, "SSSS"));
        Assertions.assertEquals("0000", DurationFormatUtils.formatPeriod(time1970, time1970, "yyyy"));
        Assertions.assertEquals("0000", DurationFormatUtils.formatPeriod(time1970, time1970, "yyMM"));
        long time = time1970 + (60 * 1000);
        Assertions.assertEquals("0", DurationFormatUtils.formatPeriod(time1970, time, "y"));
        Assertions.assertEquals("0", DurationFormatUtils.formatPeriod(time1970, time, "M"));
        Assertions.assertEquals("0", DurationFormatUtils.formatPeriod(time1970, time, "d"));
        Assertions.assertEquals("0", DurationFormatUtils.formatPeriod(time1970, time, "H"));
        Assertions.assertEquals("1", DurationFormatUtils.formatPeriod(time1970, time, "m"));
        Assertions.assertEquals("60", DurationFormatUtils.formatPeriod(time1970, time, "s"));
        Assertions.assertEquals("60000", DurationFormatUtils.formatPeriod(time1970, time, "S"));
        Assertions.assertEquals("01:00", DurationFormatUtils.formatPeriod(time1970, time, "mm:ss"));
        final Calendar cal = Calendar.getInstance();
        cal.set(1973, Calendar.JULY, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        time = cal.getTime().getTime();
        Assertions.assertEquals("36", DurationFormatUtils.formatPeriod(time1970, time, "yM"));
        Assertions.assertEquals("3 years 6 months", DurationFormatUtils.formatPeriod(time1970, time, "y' years 'M' months'"));
        Assertions.assertEquals("03/06", DurationFormatUtils.formatPeriod(time1970, time, "yy/MM"));
        cal.set(1973, Calendar.NOVEMBER, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        time = cal.getTime().getTime();
        Assertions.assertEquals("310", DurationFormatUtils.formatPeriod(time1970, time, "yM"));
        Assertions.assertEquals("3 years 10 months", DurationFormatUtils.formatPeriod(time1970, time, "y' years 'M' months'"));
        Assertions.assertEquals("03/10", DurationFormatUtils.formatPeriod(time1970, time, "yy/MM"));
        cal.set(1974, Calendar.JANUARY, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        time = cal.getTime().getTime();
        Assertions.assertEquals("40", DurationFormatUtils.formatPeriod(time1970, time, "yM"));
        Assertions.assertEquals("4 years 0 months", DurationFormatUtils.formatPeriod(time1970, time, "y' years 'M' months'"));
        Assertions.assertEquals("04/00", DurationFormatUtils.formatPeriod(time1970, time, "yy/MM"));
        Assertions.assertEquals("48", DurationFormatUtils.formatPeriod(time1970, time, "M"));
        Assertions.assertEquals("48", DurationFormatUtils.formatPeriod(time1970, time, "MM"));
        Assertions.assertEquals("048", DurationFormatUtils.formatPeriod(time1970, time, "MMM"));
    }

    @Test
    public void testFormatPeriodeStartGreaterEnd() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> DurationFormatUtils.formatPeriod(5000, 2500, "yy/MM"));
    }

    @Test
    public void testLexx() {
        // tests each constant
        Assertions.assertArrayEquals(new DurationFormatUtils.Token[]{ new DurationFormatUtils.Token(DurationFormatUtils.y, 1), new DurationFormatUtils.Token(DurationFormatUtils.M, 1), new DurationFormatUtils.Token(DurationFormatUtils.d, 1), new DurationFormatUtils.Token(DurationFormatUtils.H, 1), new DurationFormatUtils.Token(DurationFormatUtils.m, 1), new DurationFormatUtils.Token(DurationFormatUtils.s, 1), new DurationFormatUtils.Token(DurationFormatUtils.S, 1) }, DurationFormatUtils.lexx("yMdHmsS"));
        // tests the ISO 8601-like
        Assertions.assertArrayEquals(new DurationFormatUtils.Token[]{ new DurationFormatUtils.Token(DurationFormatUtils.H, 2), new DurationFormatUtils.Token(new StringBuilder(":"), 1), new DurationFormatUtils.Token(DurationFormatUtils.m, 2), new DurationFormatUtils.Token(new StringBuilder(":"), 1), new DurationFormatUtils.Token(DurationFormatUtils.s, 2), new DurationFormatUtils.Token(new StringBuilder("."), 1), new DurationFormatUtils.Token(DurationFormatUtils.S, 3) }, DurationFormatUtils.lexx("HH:mm:ss.SSS"));
        // test the iso extended format
        Assertions.assertArrayEquals(new DurationFormatUtils.Token[]{ new DurationFormatUtils.Token(new StringBuilder("P"), 1), new DurationFormatUtils.Token(DurationFormatUtils.y, 4), new DurationFormatUtils.Token(new StringBuilder("Y"), 1), new DurationFormatUtils.Token(DurationFormatUtils.M, 1), new DurationFormatUtils.Token(new StringBuilder("M"), 1), new DurationFormatUtils.Token(DurationFormatUtils.d, 1), new DurationFormatUtils.Token(new StringBuilder("DT"), 1), new DurationFormatUtils.Token(DurationFormatUtils.H, 1), new DurationFormatUtils.Token(new StringBuilder("H"), 1), new DurationFormatUtils.Token(DurationFormatUtils.m, 1), new DurationFormatUtils.Token(new StringBuilder("M"), 1), new DurationFormatUtils.Token(DurationFormatUtils.s, 1), new DurationFormatUtils.Token(new StringBuilder("."), 1), new DurationFormatUtils.Token(DurationFormatUtils.S, 3), new DurationFormatUtils.Token(new StringBuilder("S"), 1) }, DurationFormatUtils.lexx(DurationFormatUtils.ISO_EXTENDED_FORMAT_PATTERN));
        // test failures in equals
        final DurationFormatUtils.Token token = new DurationFormatUtils.Token(DurationFormatUtils.y, 4);
        Assertions.assertNotEquals(token, new Object(), "Token equal to non-Token class. ");
        Assertions.assertNotEquals(token, new DurationFormatUtils.Token(new Object()), "Token equal to Token with wrong value class. ");
        Assertions.assertNotEquals(token, new DurationFormatUtils.Token(DurationFormatUtils.y, 1), "Token equal to Token with different count. ");
        final DurationFormatUtils.Token numToken = new DurationFormatUtils.Token(Integer.valueOf(1), 4);
        Assertions.assertEquals(numToken, numToken, "Token with Number value not equal to itself. ");
    }

    // http://issues.apache.org/bugzilla/show_bug.cgi?id=38401
    @Test
    public void testBugzilla38401() {
        assertEqualDuration("0000/00/30 16:00:00 000", new int[]{ 2006, 0, 26, 18, 47, 34 }, new int[]{ 2006, 1, 26, 10, 47, 34 }, "yyyy/MM/dd HH:mm:ss SSS");
    }

    // https://issues.apache.org/jira/browse/LANG-281
    @Test
    public void testJiraLang281() {
        assertEqualDuration("09", new int[]{ 2005, 11, 31, 0, 0, 0 }, new int[]{ 2006, 9, 6, 0, 0, 0 }, "MM");
    }

    @Test
    public void testLANG815() {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(2012, Calendar.JULY, 30, 0, 0, 0);
        final long startMillis = calendar.getTimeInMillis();
        calendar.set(2012, Calendar.SEPTEMBER, 8);
        final long endMillis = calendar.getTimeInMillis();
        Assertions.assertEquals("1 9", DurationFormatUtils.formatPeriod(startMillis, endMillis, "M d"));
    }

    // Testing the under a day range in DurationFormatUtils.formatPeriod
    @Test
    public void testLowDurations() {
        for (int hr = 0; hr < 24; hr++) {
            for (int min = 0; min < 60; min++) {
                for (int sec = 0; sec < 60; sec++) {
                    assertEqualDuration(((((hr + ":") + min) + ":") + sec), new int[]{ 2000, 0, 1, 0, 0, 0, 0 }, new int[]{ 2000, 0, 1, hr, min, sec }, "H:m:s");
                }
            }
        }
    }

    // Attempting to test edge cases in DurationFormatUtils.formatPeriod
    @Test
    public void testEdgeDurations() {
        // This test case must use a time zone without DST
        TimeZone.setDefault(FastTimeZone.getGmtTimeZone());
        assertEqualDuration("01", new int[]{ 2006, 0, 15, 0, 0, 0 }, new int[]{ 2006, 2, 10, 0, 0, 0 }, "MM");
        assertEqualDuration("12", new int[]{ 2005, 0, 15, 0, 0, 0 }, new int[]{ 2006, 0, 15, 0, 0, 0 }, "MM");
        assertEqualDuration("12", new int[]{ 2005, 0, 15, 0, 0, 0 }, new int[]{ 2006, 0, 16, 0, 0, 0 }, "MM");
        assertEqualDuration("11", new int[]{ 2005, 0, 15, 0, 0, 0 }, new int[]{ 2006, 0, 14, 0, 0, 0 }, "MM");
        assertEqualDuration("01 26", new int[]{ 2006, 0, 15, 0, 0, 0 }, new int[]{ 2006, 2, 10, 0, 0, 0 }, "MM dd");
        assertEqualDuration("54", new int[]{ 2006, 0, 15, 0, 0, 0 }, new int[]{ 2006, 2, 10, 0, 0, 0 }, "dd");
        assertEqualDuration("09 12", new int[]{ 2006, 1, 20, 0, 0, 0 }, new int[]{ 2006, 11, 4, 0, 0, 0 }, "MM dd");
        assertEqualDuration("287", new int[]{ 2006, 1, 20, 0, 0, 0 }, new int[]{ 2006, 11, 4, 0, 0, 0 }, "dd");
        assertEqualDuration("11 30", new int[]{ 2006, 0, 2, 0, 0, 0 }, new int[]{ 2007, 0, 1, 0, 0, 0 }, "MM dd");
        assertEqualDuration("364", new int[]{ 2006, 0, 2, 0, 0, 0 }, new int[]{ 2007, 0, 1, 0, 0, 0 }, "dd");
        assertEqualDuration("12 00", new int[]{ 2006, 0, 1, 0, 0, 0 }, new int[]{ 2007, 0, 1, 0, 0, 0 }, "MM dd");
        assertEqualDuration("365", new int[]{ 2006, 0, 1, 0, 0, 0 }, new int[]{ 2007, 0, 1, 0, 0, 0 }, "dd");
        assertEqualDuration("31", new int[]{ 2006, 0, 1, 0, 0, 0 }, new int[]{ 2006, 1, 1, 0, 0, 0 }, "dd");
        assertEqualDuration("92", new int[]{ 2005, 9, 1, 0, 0, 0 }, new int[]{ 2006, 0, 1, 0, 0, 0 }, "dd");
        assertEqualDuration("77", new int[]{ 2005, 9, 16, 0, 0, 0 }, new int[]{ 2006, 0, 1, 0, 0, 0 }, "dd");
        // test month larger in start than end
        assertEqualDuration("136", new int[]{ 2005, 9, 16, 0, 0, 0 }, new int[]{ 2006, 2, 1, 0, 0, 0 }, "dd");
        // test when start in leap year
        assertEqualDuration("136", new int[]{ 2004, 9, 16, 0, 0, 0 }, new int[]{ 2005, 2, 1, 0, 0, 0 }, "dd");
        // test when end in leap year
        assertEqualDuration("137", new int[]{ 2003, 9, 16, 0, 0, 0 }, new int[]{ 2004, 2, 1, 0, 0, 0 }, "dd");
        // test when end in leap year but less than end of feb
        assertEqualDuration("135", new int[]{ 2003, 9, 16, 0, 0, 0 }, new int[]{ 2004, 1, 28, 0, 0, 0 }, "dd");
        assertEqualDuration("364", new int[]{ 2007, 0, 2, 0, 0, 0 }, new int[]{ 2008, 0, 1, 0, 0, 0 }, "dd");
        assertEqualDuration("729", new int[]{ 2006, 0, 2, 0, 0, 0 }, new int[]{ 2008, 0, 1, 0, 0, 0 }, "dd");
        assertEqualDuration("365", new int[]{ 2007, 2, 2, 0, 0, 0 }, new int[]{ 2008, 2, 1, 0, 0, 0 }, "dd");
        assertEqualDuration("333", new int[]{ 2007, 1, 2, 0, 0, 0 }, new int[]{ 2008, 0, 1, 0, 0, 0 }, "dd");
        assertEqualDuration("28", new int[]{ 2008, 1, 2, 0, 0, 0 }, new int[]{ 2008, 2, 1, 0, 0, 0 }, "dd");
        assertEqualDuration("393", new int[]{ 2007, 1, 2, 0, 0, 0 }, new int[]{ 2008, 2, 1, 0, 0, 0 }, "dd");
        assertEqualDuration("369", new int[]{ 2004, 0, 29, 0, 0, 0 }, new int[]{ 2005, 1, 1, 0, 0, 0 }, "dd");
        assertEqualDuration("338", new int[]{ 2004, 1, 29, 0, 0, 0 }, new int[]{ 2005, 1, 1, 0, 0, 0 }, "dd");
        assertEqualDuration("28", new int[]{ 2004, 2, 8, 0, 0, 0 }, new int[]{ 2004, 3, 5, 0, 0, 0 }, "dd");
        assertEqualDuration("48", new int[]{ 1992, 1, 29, 0, 0, 0 }, new int[]{ 1996, 1, 29, 0, 0, 0 }, "M");
        // this seems odd - and will fail if I throw it in as a brute force
        // below as it expects the answer to be 12. It's a tricky edge case
        assertEqualDuration("11", new int[]{ 1996, 1, 29, 0, 0, 0 }, new int[]{ 1997, 1, 28, 0, 0, 0 }, "M");
        // again - this seems odd
        assertEqualDuration("11 28", new int[]{ 1996, 1, 29, 0, 0, 0 }, new int[]{ 1997, 1, 28, 0, 0, 0 }, "M d");
    }

    @Test
    public void testLANG984() {
        // Long durations
        Assertions.assertEquals("0", DurationFormatUtils.formatDuration(0, "S"));
        Assertions.assertEquals(Integer.toString(Integer.MAX_VALUE), DurationFormatUtils.formatDuration(Integer.MAX_VALUE, "S"));
        long maxIntPlus = Integer.MAX_VALUE;
        maxIntPlus++;
        Assertions.assertEquals(Long.toString(maxIntPlus), DurationFormatUtils.formatDuration(maxIntPlus, "S"));
        Assertions.assertEquals(Long.toString(Long.MAX_VALUE), DurationFormatUtils.formatDuration(Long.MAX_VALUE, "S"));
    }

    @Test
    public void testLANG982() {
        // More than 3 millisecond digits following a second
        Assertions.assertEquals("61.999", DurationFormatUtils.formatDuration(61999, "s.S"));
        Assertions.assertEquals("1 1999", DurationFormatUtils.formatDuration(61999, "m S"));
        Assertions.assertEquals("61.999", DurationFormatUtils.formatDuration(61999, "s.SSS"));
        Assertions.assertEquals("1 1999", DurationFormatUtils.formatDuration(61999, "m SSS"));
        Assertions.assertEquals("61.0999", DurationFormatUtils.formatDuration(61999, "s.SSSS"));
        Assertions.assertEquals("1 1999", DurationFormatUtils.formatDuration(61999, "m SSSS"));
        Assertions.assertEquals("61.00999", DurationFormatUtils.formatDuration(61999, "s.SSSSS"));
        Assertions.assertEquals("1 01999", DurationFormatUtils.formatDuration(61999, "m SSSSS"));
    }

    @Test
    public void testDurationsByBruteForce() {
        bruteForce(2006, 0, 1, "d", Calendar.DAY_OF_MONTH);
        bruteForce(2006, 0, 2, "d", Calendar.DAY_OF_MONTH);
        bruteForce(2007, 1, 2, "d", Calendar.DAY_OF_MONTH);
        bruteForce(2004, 1, 29, "d", Calendar.DAY_OF_MONTH);
        bruteForce(1996, 1, 29, "d", Calendar.DAY_OF_MONTH);
        bruteForce(1969, 1, 28, "M", Calendar.MONTH);// tests for 48 years

        // bruteForce(1996, 1, 29, "M", Calendar.MONTH);  // this will fail
    }

    @Test
    public void testLANG981() {
        // unmatched quote char in lexx
        Assertions.assertThrows(IllegalArgumentException.class, () -> DurationFormatUtils.lexx("'yMdHms''S"));
    }

    private static final int FOUR_YEARS = (365 * 3) + 366;
}

