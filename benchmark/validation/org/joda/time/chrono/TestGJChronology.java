/**
 * Copyright 2001-2014 Stephen Colebourne
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
package org.joda.time.chrono;


import DateTimeZone.UTC;
import java.util.Locale;
import java.util.TimeZone;
import junit.framework.TestCase;
import org.joda.time.Chronology;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationFieldType;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.Instant;
import org.joda.time.Period;
import org.joda.time.TimeOfDay;
import org.joda.time.YearMonthDay;


/**
 * This class is a Junit unit test for GJChronology.
 *
 * @author Stephen Colebourne
 */
@SuppressWarnings("deprecation")
public class TestGJChronology extends TestCase {
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final DateTimeZone TOKYO = DateTimeZone.forID("Asia/Tokyo");

    long y2002days = ((((((((((((((((((((((((((((((365 + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365;

    // 2002-06-09
    private long TEST_TIME_NOW = ((((((((y2002days) + 31L) + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (DateTimeConstants.MILLIS_PER_DAY);

    private DateTimeZone originalDateTimeZone = null;

    private TimeZone originalTimeZone = null;

    private Locale originalLocale = null;

    public TestGJChronology(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testFactoryUTC() {
        TestCase.assertEquals(UTC, GJChronology.getInstanceUTC().getZone());
        TestCase.assertSame(GJChronology.class, GJChronology.getInstanceUTC().getClass());
    }

    public void testFactory() {
        TestCase.assertEquals(TestGJChronology.LONDON, GJChronology.getInstance().getZone());
        TestCase.assertSame(GJChronology.class, GJChronology.getInstance().getClass());
    }

    public void testFactory_Zone() {
        TestCase.assertEquals(TestGJChronology.TOKYO, GJChronology.getInstance(TestGJChronology.TOKYO).getZone());
        TestCase.assertEquals(TestGJChronology.PARIS, GJChronology.getInstance(TestGJChronology.PARIS).getZone());
        TestCase.assertEquals(TestGJChronology.LONDON, GJChronology.getInstance(null).getZone());
        TestCase.assertSame(GJChronology.class, GJChronology.getInstance(TestGJChronology.TOKYO).getClass());
    }

    public void testFactory_Zone_long_int() {
        GJChronology chrono = GJChronology.getInstance(TestGJChronology.TOKYO, 0L, 2);
        TestCase.assertEquals(TestGJChronology.TOKYO, chrono.getZone());
        TestCase.assertEquals(new Instant(0L), chrono.getGregorianCutover());
        TestCase.assertEquals(2, chrono.getMinimumDaysInFirstWeek());
        TestCase.assertSame(GJChronology.class, GJChronology.getInstance(TestGJChronology.TOKYO, 0L, 2).getClass());
        try {
            GJChronology.getInstance(TestGJChronology.TOKYO, 0L, 0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            GJChronology.getInstance(TestGJChronology.TOKYO, 0L, 8);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testFactory_Zone_RI() {
        GJChronology chrono = GJChronology.getInstance(TestGJChronology.TOKYO, new Instant(0L));
        TestCase.assertEquals(TestGJChronology.TOKYO, chrono.getZone());
        TestCase.assertEquals(new Instant(0L), chrono.getGregorianCutover());
        TestCase.assertSame(GJChronology.class, GJChronology.getInstance(TestGJChronology.TOKYO, new Instant(0L)).getClass());
        DateTime cutover = new DateTime(1582, 10, 15, 0, 0, 0, 0, DateTimeZone.UTC);
        chrono = GJChronology.getInstance(TestGJChronology.TOKYO, null);
        TestCase.assertEquals(TestGJChronology.TOKYO, chrono.getZone());
        TestCase.assertEquals(cutover.toInstant(), chrono.getGregorianCutover());
    }

    public void testFactory_Zone_RI_int() {
        GJChronology chrono = GJChronology.getInstance(TestGJChronology.TOKYO, new Instant(0L), 2);
        TestCase.assertEquals(TestGJChronology.TOKYO, chrono.getZone());
        TestCase.assertEquals(new Instant(0L), chrono.getGregorianCutover());
        TestCase.assertEquals(2, chrono.getMinimumDaysInFirstWeek());
        TestCase.assertSame(GJChronology.class, GJChronology.getInstance(TestGJChronology.TOKYO, new Instant(0L), 2).getClass());
        DateTime cutover = new DateTime(1582, 10, 15, 0, 0, 0, 0, DateTimeZone.UTC);
        chrono = GJChronology.getInstance(TestGJChronology.TOKYO, null, 2);
        TestCase.assertEquals(TestGJChronology.TOKYO, chrono.getZone());
        TestCase.assertEquals(cutover.toInstant(), chrono.getGregorianCutover());
        TestCase.assertEquals(2, chrono.getMinimumDaysInFirstWeek());
        try {
            GJChronology.getInstance(TestGJChronology.TOKYO, new Instant(0L), 0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            GJChronology.getInstance(TestGJChronology.TOKYO, new Instant(0L), 8);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testEquality() {
        TestCase.assertSame(GJChronology.getInstance(TestGJChronology.TOKYO), GJChronology.getInstance(TestGJChronology.TOKYO));
        TestCase.assertSame(GJChronology.getInstance(TestGJChronology.LONDON), GJChronology.getInstance(TestGJChronology.LONDON));
        TestCase.assertSame(GJChronology.getInstance(TestGJChronology.PARIS), GJChronology.getInstance(TestGJChronology.PARIS));
        TestCase.assertSame(GJChronology.getInstanceUTC(), GJChronology.getInstanceUTC());
        TestCase.assertSame(GJChronology.getInstance(), GJChronology.getInstance(TestGJChronology.LONDON));
    }

    public void testWithUTC() {
        TestCase.assertSame(GJChronology.getInstanceUTC(), GJChronology.getInstance(TestGJChronology.LONDON).withUTC());
        TestCase.assertSame(GJChronology.getInstanceUTC(), GJChronology.getInstance(TestGJChronology.TOKYO).withUTC());
        TestCase.assertSame(GJChronology.getInstanceUTC(), GJChronology.getInstanceUTC().withUTC());
        TestCase.assertSame(GJChronology.getInstanceUTC(), GJChronology.getInstance().withUTC());
    }

    public void testWithZone() {
        TestCase.assertSame(GJChronology.getInstance(TestGJChronology.TOKYO), GJChronology.getInstance(TestGJChronology.TOKYO).withZone(TestGJChronology.TOKYO));
        TestCase.assertSame(GJChronology.getInstance(TestGJChronology.LONDON), GJChronology.getInstance(TestGJChronology.TOKYO).withZone(TestGJChronology.LONDON));
        TestCase.assertSame(GJChronology.getInstance(TestGJChronology.PARIS), GJChronology.getInstance(TestGJChronology.TOKYO).withZone(TestGJChronology.PARIS));
        TestCase.assertSame(GJChronology.getInstance(TestGJChronology.LONDON), GJChronology.getInstance(TestGJChronology.TOKYO).withZone(null));
        TestCase.assertSame(GJChronology.getInstance(TestGJChronology.PARIS), GJChronology.getInstance().withZone(TestGJChronology.PARIS));
        TestCase.assertSame(GJChronology.getInstance(TestGJChronology.PARIS), GJChronology.getInstanceUTC().withZone(TestGJChronology.PARIS));
    }

    public void testToString() {
        TestCase.assertEquals("GJChronology[Europe/London]", GJChronology.getInstance(TestGJChronology.LONDON).toString());
        TestCase.assertEquals("GJChronology[Asia/Tokyo]", GJChronology.getInstance(TestGJChronology.TOKYO).toString());
        TestCase.assertEquals("GJChronology[Europe/London]", GJChronology.getInstance().toString());
        TestCase.assertEquals("GJChronology[UTC]", GJChronology.getInstanceUTC().toString());
        TestCase.assertEquals("GJChronology[UTC,cutover=1970-01-01]", GJChronology.getInstance(UTC, 0L, 4).toString());
        TestCase.assertEquals("GJChronology[UTC,cutover=1970-01-01T00:00:00.001Z,mdfw=2]", GJChronology.getInstance(UTC, 1L, 2).toString());
    }

    // -----------------------------------------------------------------------
    public void testDurationFields() {
        final GJChronology gj = GJChronology.getInstance();
        TestCase.assertEquals("eras", gj.eras().getName());
        TestCase.assertEquals("centuries", gj.centuries().getName());
        TestCase.assertEquals("years", gj.years().getName());
        TestCase.assertEquals("weekyears", gj.weekyears().getName());
        TestCase.assertEquals("months", gj.months().getName());
        TestCase.assertEquals("weeks", gj.weeks().getName());
        TestCase.assertEquals("halfdays", gj.halfdays().getName());
        TestCase.assertEquals("days", gj.days().getName());
        TestCase.assertEquals("hours", gj.hours().getName());
        TestCase.assertEquals("minutes", gj.minutes().getName());
        TestCase.assertEquals("seconds", gj.seconds().getName());
        TestCase.assertEquals("millis", gj.millis().getName());
        TestCase.assertEquals(false, gj.eras().isSupported());
        TestCase.assertEquals(true, gj.centuries().isSupported());
        TestCase.assertEquals(true, gj.years().isSupported());
        TestCase.assertEquals(true, gj.weekyears().isSupported());
        TestCase.assertEquals(true, gj.months().isSupported());
        TestCase.assertEquals(true, gj.weeks().isSupported());
        TestCase.assertEquals(true, gj.days().isSupported());
        TestCase.assertEquals(true, gj.halfdays().isSupported());
        TestCase.assertEquals(true, gj.hours().isSupported());
        TestCase.assertEquals(true, gj.minutes().isSupported());
        TestCase.assertEquals(true, gj.seconds().isSupported());
        TestCase.assertEquals(true, gj.millis().isSupported());
        TestCase.assertEquals(false, gj.centuries().isPrecise());
        TestCase.assertEquals(false, gj.years().isPrecise());
        TestCase.assertEquals(false, gj.weekyears().isPrecise());
        TestCase.assertEquals(false, gj.months().isPrecise());
        TestCase.assertEquals(false, gj.weeks().isPrecise());
        TestCase.assertEquals(false, gj.days().isPrecise());
        TestCase.assertEquals(false, gj.halfdays().isPrecise());
        TestCase.assertEquals(true, gj.hours().isPrecise());
        TestCase.assertEquals(true, gj.minutes().isPrecise());
        TestCase.assertEquals(true, gj.seconds().isPrecise());
        TestCase.assertEquals(true, gj.millis().isPrecise());
        final GJChronology gjUTC = GJChronology.getInstanceUTC();
        TestCase.assertEquals(false, gjUTC.centuries().isPrecise());
        TestCase.assertEquals(false, gjUTC.years().isPrecise());
        TestCase.assertEquals(false, gjUTC.weekyears().isPrecise());
        TestCase.assertEquals(false, gjUTC.months().isPrecise());
        TestCase.assertEquals(true, gjUTC.weeks().isPrecise());
        TestCase.assertEquals(true, gjUTC.days().isPrecise());
        TestCase.assertEquals(true, gjUTC.halfdays().isPrecise());
        TestCase.assertEquals(true, gjUTC.hours().isPrecise());
        TestCase.assertEquals(true, gjUTC.minutes().isPrecise());
        TestCase.assertEquals(true, gjUTC.seconds().isPrecise());
        TestCase.assertEquals(true, gjUTC.millis().isPrecise());
        final DateTimeZone gmt = DateTimeZone.forID("Etc/GMT");
        final GJChronology gjGMT = GJChronology.getInstance(gmt);
        TestCase.assertEquals(false, gjGMT.centuries().isPrecise());
        TestCase.assertEquals(false, gjGMT.years().isPrecise());
        TestCase.assertEquals(false, gjGMT.weekyears().isPrecise());
        TestCase.assertEquals(false, gjGMT.months().isPrecise());
        TestCase.assertEquals(true, gjGMT.weeks().isPrecise());
        TestCase.assertEquals(true, gjGMT.days().isPrecise());
        TestCase.assertEquals(true, gjGMT.halfdays().isPrecise());
        TestCase.assertEquals(true, gjGMT.hours().isPrecise());
        TestCase.assertEquals(true, gjGMT.minutes().isPrecise());
        TestCase.assertEquals(true, gjGMT.seconds().isPrecise());
        TestCase.assertEquals(true, gjGMT.millis().isPrecise());
    }

    public void testDateFields() {
        final GJChronology gj = GJChronology.getInstance();
        TestCase.assertEquals("era", gj.era().getName());
        TestCase.assertEquals("centuryOfEra", gj.centuryOfEra().getName());
        TestCase.assertEquals("yearOfCentury", gj.yearOfCentury().getName());
        TestCase.assertEquals("yearOfEra", gj.yearOfEra().getName());
        TestCase.assertEquals("year", gj.year().getName());
        TestCase.assertEquals("monthOfYear", gj.monthOfYear().getName());
        TestCase.assertEquals("weekyearOfCentury", gj.weekyearOfCentury().getName());
        TestCase.assertEquals("weekyear", gj.weekyear().getName());
        TestCase.assertEquals("weekOfWeekyear", gj.weekOfWeekyear().getName());
        TestCase.assertEquals("dayOfYear", gj.dayOfYear().getName());
        TestCase.assertEquals("dayOfMonth", gj.dayOfMonth().getName());
        TestCase.assertEquals("dayOfWeek", gj.dayOfWeek().getName());
        TestCase.assertEquals(true, gj.era().isSupported());
        TestCase.assertEquals(true, gj.centuryOfEra().isSupported());
        TestCase.assertEquals(true, gj.yearOfCentury().isSupported());
        TestCase.assertEquals(true, gj.yearOfEra().isSupported());
        TestCase.assertEquals(true, gj.year().isSupported());
        TestCase.assertEquals(true, gj.monthOfYear().isSupported());
        TestCase.assertEquals(true, gj.weekyearOfCentury().isSupported());
        TestCase.assertEquals(true, gj.weekyear().isSupported());
        TestCase.assertEquals(true, gj.weekOfWeekyear().isSupported());
        TestCase.assertEquals(true, gj.dayOfYear().isSupported());
        TestCase.assertEquals(true, gj.dayOfMonth().isSupported());
        TestCase.assertEquals(true, gj.dayOfWeek().isSupported());
        TestCase.assertEquals(gj.eras(), gj.era().getDurationField());
        TestCase.assertEquals(gj.centuries(), gj.centuryOfEra().getDurationField());
        TestCase.assertEquals(gj.years(), gj.yearOfCentury().getDurationField());
        TestCase.assertEquals(gj.years(), gj.yearOfEra().getDurationField());
        TestCase.assertEquals(gj.years(), gj.year().getDurationField());
        TestCase.assertEquals(gj.months(), gj.monthOfYear().getDurationField());
        TestCase.assertEquals(gj.weekyears(), gj.weekyearOfCentury().getDurationField());
        TestCase.assertEquals(gj.weekyears(), gj.weekyear().getDurationField());
        TestCase.assertEquals(gj.weeks(), gj.weekOfWeekyear().getDurationField());
        TestCase.assertEquals(gj.days(), gj.dayOfYear().getDurationField());
        TestCase.assertEquals(gj.days(), gj.dayOfMonth().getDurationField());
        TestCase.assertEquals(gj.days(), gj.dayOfWeek().getDurationField());
        TestCase.assertEquals(null, gj.era().getRangeDurationField());
        TestCase.assertEquals(gj.eras(), gj.centuryOfEra().getRangeDurationField());
        TestCase.assertEquals(gj.centuries(), gj.yearOfCentury().getRangeDurationField());
        TestCase.assertEquals(gj.eras(), gj.yearOfEra().getRangeDurationField());
        TestCase.assertEquals(null, gj.year().getRangeDurationField());
        TestCase.assertEquals(gj.years(), gj.monthOfYear().getRangeDurationField());
        TestCase.assertEquals(gj.centuries(), gj.weekyearOfCentury().getRangeDurationField());
        TestCase.assertEquals(null, gj.weekyear().getRangeDurationField());
        TestCase.assertEquals(gj.weekyears(), gj.weekOfWeekyear().getRangeDurationField());
        TestCase.assertEquals(gj.years(), gj.dayOfYear().getRangeDurationField());
        TestCase.assertEquals(gj.months(), gj.dayOfMonth().getRangeDurationField());
        TestCase.assertEquals(gj.weeks(), gj.dayOfWeek().getRangeDurationField());
    }

    public void testTimeFields() {
        final GJChronology gj = GJChronology.getInstance();
        TestCase.assertEquals("halfdayOfDay", gj.halfdayOfDay().getName());
        TestCase.assertEquals("clockhourOfHalfday", gj.clockhourOfHalfday().getName());
        TestCase.assertEquals("hourOfHalfday", gj.hourOfHalfday().getName());
        TestCase.assertEquals("clockhourOfDay", gj.clockhourOfDay().getName());
        TestCase.assertEquals("hourOfDay", gj.hourOfDay().getName());
        TestCase.assertEquals("minuteOfDay", gj.minuteOfDay().getName());
        TestCase.assertEquals("minuteOfHour", gj.minuteOfHour().getName());
        TestCase.assertEquals("secondOfDay", gj.secondOfDay().getName());
        TestCase.assertEquals("secondOfMinute", gj.secondOfMinute().getName());
        TestCase.assertEquals("millisOfDay", gj.millisOfDay().getName());
        TestCase.assertEquals("millisOfSecond", gj.millisOfSecond().getName());
        TestCase.assertEquals(true, gj.halfdayOfDay().isSupported());
        TestCase.assertEquals(true, gj.clockhourOfHalfday().isSupported());
        TestCase.assertEquals(true, gj.hourOfHalfday().isSupported());
        TestCase.assertEquals(true, gj.clockhourOfDay().isSupported());
        TestCase.assertEquals(true, gj.hourOfDay().isSupported());
        TestCase.assertEquals(true, gj.minuteOfDay().isSupported());
        TestCase.assertEquals(true, gj.minuteOfHour().isSupported());
        TestCase.assertEquals(true, gj.secondOfDay().isSupported());
        TestCase.assertEquals(true, gj.secondOfMinute().isSupported());
        TestCase.assertEquals(true, gj.millisOfDay().isSupported());
        TestCase.assertEquals(true, gj.millisOfSecond().isSupported());
    }

    public void testIllegalDates() {
        try {
            new DateTime(1582, 10, 5, 0, 0, 0, 0, GJChronology.getInstance(UTC));
            TestCase.fail("Constructed illegal date");
        } catch (IllegalArgumentException e) {
            /* good */
        }
        try {
            new DateTime(1582, 10, 14, 0, 0, 0, 0, GJChronology.getInstance(UTC));
            TestCase.fail("Constructed illegal date");
        } catch (IllegalArgumentException e) {
            /* good */
        }
    }

    public void testParseEquivalence() {
        testParse("1581-01-01T01:23:45.678", 1581, 1, 1, 1, 23, 45, 678);
        testParse("1581-06-30", 1581, 6, 30, 0, 0, 0, 0);
        testParse("1582-01-01T01:23:45.678", 1582, 1, 1, 1, 23, 45, 678);
        testParse("1582-06-30T01:23:45.678", 1582, 6, 30, 1, 23, 45, 678);
        testParse("1582-10-04", 1582, 10, 4, 0, 0, 0, 0);
        testParse("1582-10-15", 1582, 10, 15, 0, 0, 0, 0);
        testParse("1582-12-31", 1582, 12, 31, 0, 0, 0, 0);
        testParse("1583-12-31", 1583, 12, 31, 0, 0, 0, 0);
    }

    public void testCutoverAddYears() {
        testAdd("1582-01-01", DurationFieldType.years(), 1, "1583-01-01");
        testAdd("1582-02-15", DurationFieldType.years(), 1, "1583-02-15");
        testAdd("1582-02-28", DurationFieldType.years(), 1, "1583-02-28");
        testAdd("1582-03-01", DurationFieldType.years(), 1, "1583-03-01");
        testAdd("1582-09-30", DurationFieldType.years(), 1, "1583-09-30");
        testAdd("1582-10-01", DurationFieldType.years(), 1, "1583-10-01");
        testAdd("1582-10-04", DurationFieldType.years(), 1, "1583-10-04");
        testAdd("1582-10-15", DurationFieldType.years(), 1, "1583-10-15");
        testAdd("1582-10-16", DurationFieldType.years(), 1, "1583-10-16");
        // Leap years...
        testAdd("1580-01-01", DurationFieldType.years(), 4, "1584-01-01");
        testAdd("1580-02-29", DurationFieldType.years(), 4, "1584-02-29");
        testAdd("1580-10-01", DurationFieldType.years(), 4, "1584-10-01");
        testAdd("1580-10-10", DurationFieldType.years(), 4, "1584-10-10");
        testAdd("1580-10-15", DurationFieldType.years(), 4, "1584-10-15");
        testAdd("1580-12-31", DurationFieldType.years(), 4, "1584-12-31");
    }

    public void testCutoverAddWeekyears() {
        testAdd("1582-W01-1", DurationFieldType.weekyears(), 1, "1583-W01-1");
        testAdd("1582-W39-1", DurationFieldType.weekyears(), 1, "1583-W39-1");
        testAdd("1583-W45-1", DurationFieldType.weekyears(), 1, "1584-W45-1");
        // This test fails, but I'm not sure if its worth fixing. The date
        // falls after the cutover, but in the cutover year. The add operation
        // is performed completely within the gregorian calendar, with no
        // crossing of the cutover. As a result, no special correction is
        // applied. Since the full gregorian year of 1582 has a different week
        // numbers than the full julian year of 1582, the week number is off by
        // one after the addition.
        // 
        // testAdd("1582-W42-1", DurationFieldType.weekyears(), 1, "1583-W42-1");
        // Leap years...
        testAdd("1580-W01-1", DurationFieldType.weekyears(), 4, "1584-W01-1");
        testAdd("1580-W30-7", DurationFieldType.weekyears(), 4, "1584-W30-7");
        testAdd("1580-W50-7", DurationFieldType.weekyears(), 4, "1584-W50-7");
    }

    public void testCutoverAddMonths() {
        testAdd("1582-01-01", DurationFieldType.months(), 1, "1582-02-01");
        testAdd("1582-01-01", DurationFieldType.months(), 6, "1582-07-01");
        testAdd("1582-01-01", DurationFieldType.months(), 12, "1583-01-01");
        testAdd("1582-11-15", DurationFieldType.months(), 1, "1582-12-15");
        testAdd("1582-09-04", DurationFieldType.months(), 2, "1582-11-04");
        testAdd("1582-09-05", DurationFieldType.months(), 2, "1582-11-05");
        testAdd("1582-09-10", DurationFieldType.months(), 2, "1582-11-10");
        testAdd("1582-09-15", DurationFieldType.months(), 2, "1582-11-15");
        // Leap years...
        testAdd("1580-01-01", DurationFieldType.months(), 48, "1584-01-01");
        testAdd("1580-02-29", DurationFieldType.months(), 48, "1584-02-29");
        testAdd("1580-10-01", DurationFieldType.months(), 48, "1584-10-01");
        testAdd("1580-10-10", DurationFieldType.months(), 48, "1584-10-10");
        testAdd("1580-10-15", DurationFieldType.months(), 48, "1584-10-15");
        testAdd("1580-12-31", DurationFieldType.months(), 48, "1584-12-31");
    }

    public void testCutoverAddWeeks() {
        testAdd("1582-01-01", DurationFieldType.weeks(), 1, "1582-01-08");
        testAdd("1583-01-01", DurationFieldType.weeks(), 1, "1583-01-08");
        // Weeks are precise, and so cutover is not ignored.
        testAdd("1582-10-01", DurationFieldType.weeks(), 2, "1582-10-25");
        testAdd("1582-W01-1", DurationFieldType.weeks(), 51, "1583-W01-1");
    }

    public void testCutoverAddDays() {
        testAdd("1582-10-03", DurationFieldType.days(), 1, "1582-10-04");
        testAdd("1582-10-04", DurationFieldType.days(), 1, "1582-10-15");
        testAdd("1582-10-15", DurationFieldType.days(), 1, "1582-10-16");
        testAdd("1582-09-30", DurationFieldType.days(), 10, "1582-10-20");
        testAdd("1582-10-04", DurationFieldType.days(), 10, "1582-10-24");
        testAdd("1582-10-15", DurationFieldType.days(), 10, "1582-10-25");
    }

    public void testYearEndAddDays() {
        testAdd("1582-11-05", DurationFieldType.days(), 28, "1582-12-03");
        testAdd("1582-12-05", DurationFieldType.days(), 28, "1583-01-02");
        testAdd("2005-11-05", DurationFieldType.days(), 28, "2005-12-03");
        testAdd("2005-12-05", DurationFieldType.days(), 28, "2006-01-02");
    }

    public void testSubtractDays() {
        // This is a test for a bug in version 1.0. The dayOfMonth range
        // duration field did not match the monthOfYear duration field. This
        // caused an exception to be thrown when subtracting days.
        DateTime dt = new DateTime(1112306400000L, GJChronology.getInstance(DateTimeZone.forID("Europe/Berlin")));
        YearMonthDay ymd = dt.toYearMonthDay();
        while ((ymd.toDateTimeAtMidnight().getDayOfWeek()) != (DateTimeConstants.MONDAY)) {
            ymd = ymd.minus(Period.days(1));
        } 
    }

    public void testTimeOfDayAdd() {
        TimeOfDay start = new TimeOfDay(12, 30, GJChronology.getInstance());
        TimeOfDay end = new TimeOfDay(10, 30, GJChronology.getInstance());
        TestCase.assertEquals(end, start.plusHours(22));
        TestCase.assertEquals(start, end.minusHours(22));
        TestCase.assertEquals(end, start.plusMinutes((22 * 60)));
        TestCase.assertEquals(start, end.minusMinutes((22 * 60)));
    }

    public void testMaximumValue() {
        DateMidnight dt = new DateMidnight(1570, 1, 1, GJChronology.getInstance());
        while ((dt.getYear()) < 1590) {
            dt = dt.plusDays(1);
            YearMonthDay ymd = dt.toYearMonthDay();
            TestCase.assertEquals(dt.year().getMaximumValue(), ymd.year().getMaximumValue());
            TestCase.assertEquals(dt.monthOfYear().getMaximumValue(), ymd.monthOfYear().getMaximumValue());
            TestCase.assertEquals(dt.dayOfMonth().getMaximumValue(), ymd.dayOfMonth().getMaximumValue());
        } 
    }

    public void testPartialGetAsText() {
        GJChronology chrono = GJChronology.getInstance(TestGJChronology.TOKYO);
        TestCase.assertEquals("January", monthOfYear().getAsText());
        TestCase.assertEquals("Jan", monthOfYear().getAsShortText());
    }

    public void testLeapYearRulesConstruction() {
        // 1500 not leap in Gregorian, but is leap in Julian
        DateMidnight dt = new DateMidnight(1500, 2, 29, GJChronology.getInstanceUTC());
        TestCase.assertEquals(dt.getYear(), 1500);
        TestCase.assertEquals(dt.getMonthOfYear(), 2);
        TestCase.assertEquals(dt.getDayOfMonth(), 29);
    }

    public void testLeapYearRulesConstructionInvalid() {
        // 1500 not leap in Gregorian, but is leap in Julian
        try {
            new DateMidnight(1500, 2, 30, GJChronology.getInstanceUTC());
            TestCase.fail();
        } catch (IllegalFieldValueException ex) {
            // good
        }
    }

    public void testLeap_28feb() {
        Chronology chrono = GJChronology.getInstance();
        DateTime dt = new DateTime(2012, 2, 28, 0, 0, chrono);
        TestCase.assertEquals(true, dt.year().isLeap());
        TestCase.assertEquals(true, dt.monthOfYear().isLeap());
        TestCase.assertEquals(false, dt.dayOfMonth().isLeap());
        TestCase.assertEquals(false, dt.dayOfYear().isLeap());
    }

    public void testLeap_29feb() {
        Chronology chrono = GJChronology.getInstance();
        DateTime dt = new DateTime(2012, 2, 29, 0, 0, chrono);
        TestCase.assertEquals(true, dt.year().isLeap());
        TestCase.assertEquals(true, dt.monthOfYear().isLeap());
        TestCase.assertEquals(true, dt.dayOfMonth().isLeap());
        TestCase.assertEquals(true, dt.dayOfYear().isLeap());
    }
}

