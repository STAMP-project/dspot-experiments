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
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationFieldType;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.Partial;
import org.joda.time.TimeOfDay;
import org.joda.time.YearMonthDay;


/**
 * This class is a Junit unit test for ISOChronology.
 *
 * @author Stephen Colebourne
 */
@SuppressWarnings("deprecation")
public class TestISOChronology extends TestCase {
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final DateTimeZone TOKYO = DateTimeZone.forID("Asia/Tokyo");

    long y2002days = ((((((((((((((((((((((((((((((365 + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365;

    // 2002-06-09
    private long TEST_TIME_NOW = ((((((((y2002days) + 31L) + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (DateTimeConstants.MILLIS_PER_DAY);

    private DateTimeZone originalDateTimeZone = null;

    private TimeZone originalTimeZone = null;

    private Locale originalLocale = null;

    public TestISOChronology(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testFactoryUTC() {
        TestCase.assertEquals(UTC, ISOChronology.getInstanceUTC().getZone());
        TestCase.assertSame(ISOChronology.class, ISOChronology.getInstanceUTC().getClass());
    }

    public void testFactory() {
        TestCase.assertEquals(TestISOChronology.LONDON, ISOChronology.getInstance().getZone());
        TestCase.assertSame(ISOChronology.class, ISOChronology.getInstance().getClass());
    }

    public void testFactory_Zone() {
        TestCase.assertEquals(TestISOChronology.TOKYO, ISOChronology.getInstance(TestISOChronology.TOKYO).getZone());
        TestCase.assertEquals(TestISOChronology.PARIS, ISOChronology.getInstance(TestISOChronology.PARIS).getZone());
        TestCase.assertEquals(TestISOChronology.LONDON, ISOChronology.getInstance(null).getZone());
        TestCase.assertSame(ISOChronology.class, ISOChronology.getInstance(TestISOChronology.TOKYO).getClass());
    }

    // -----------------------------------------------------------------------
    public void testEquality() {
        TestCase.assertSame(ISOChronology.getInstance(TestISOChronology.TOKYO), ISOChronology.getInstance(TestISOChronology.TOKYO));
        TestCase.assertSame(ISOChronology.getInstance(TestISOChronology.LONDON), ISOChronology.getInstance(TestISOChronology.LONDON));
        TestCase.assertSame(ISOChronology.getInstance(TestISOChronology.PARIS), ISOChronology.getInstance(TestISOChronology.PARIS));
        TestCase.assertSame(ISOChronology.getInstanceUTC(), ISOChronology.getInstanceUTC());
        TestCase.assertSame(ISOChronology.getInstance(), ISOChronology.getInstance(TestISOChronology.LONDON));
    }

    public void testWithUTC() {
        TestCase.assertSame(ISOChronology.getInstanceUTC(), ISOChronology.getInstance(TestISOChronology.LONDON).withUTC());
        TestCase.assertSame(ISOChronology.getInstanceUTC(), ISOChronology.getInstance(TestISOChronology.TOKYO).withUTC());
        TestCase.assertSame(ISOChronology.getInstanceUTC(), ISOChronology.getInstanceUTC().withUTC());
        TestCase.assertSame(ISOChronology.getInstanceUTC(), ISOChronology.getInstance().withUTC());
    }

    public void testWithZone() {
        TestCase.assertSame(ISOChronology.getInstance(TestISOChronology.TOKYO), ISOChronology.getInstance(TestISOChronology.TOKYO).withZone(TestISOChronology.TOKYO));
        TestCase.assertSame(ISOChronology.getInstance(TestISOChronology.LONDON), ISOChronology.getInstance(TestISOChronology.TOKYO).withZone(TestISOChronology.LONDON));
        TestCase.assertSame(ISOChronology.getInstance(TestISOChronology.PARIS), ISOChronology.getInstance(TestISOChronology.TOKYO).withZone(TestISOChronology.PARIS));
        TestCase.assertSame(ISOChronology.getInstance(TestISOChronology.LONDON), ISOChronology.getInstance(TestISOChronology.TOKYO).withZone(null));
        TestCase.assertSame(ISOChronology.getInstance(TestISOChronology.PARIS), ISOChronology.getInstance().withZone(TestISOChronology.PARIS));
        TestCase.assertSame(ISOChronology.getInstance(TestISOChronology.PARIS), ISOChronology.getInstanceUTC().withZone(TestISOChronology.PARIS));
    }

    public void testToString() {
        TestCase.assertEquals("ISOChronology[Europe/London]", ISOChronology.getInstance(TestISOChronology.LONDON).toString());
        TestCase.assertEquals("ISOChronology[Asia/Tokyo]", ISOChronology.getInstance(TestISOChronology.TOKYO).toString());
        TestCase.assertEquals("ISOChronology[Europe/London]", ISOChronology.getInstance().toString());
        TestCase.assertEquals("ISOChronology[UTC]", ISOChronology.getInstanceUTC().toString());
    }

    // -----------------------------------------------------------------------
    public void testDurationFields() {
        final ISOChronology iso = ISOChronology.getInstance();
        TestCase.assertEquals("eras", iso.eras().getName());
        TestCase.assertEquals("centuries", iso.centuries().getName());
        TestCase.assertEquals("years", iso.years().getName());
        TestCase.assertEquals("weekyears", iso.weekyears().getName());
        TestCase.assertEquals("months", iso.months().getName());
        TestCase.assertEquals("weeks", iso.weeks().getName());
        TestCase.assertEquals("days", iso.days().getName());
        TestCase.assertEquals("halfdays", iso.halfdays().getName());
        TestCase.assertEquals("hours", iso.hours().getName());
        TestCase.assertEquals("minutes", iso.minutes().getName());
        TestCase.assertEquals("seconds", iso.seconds().getName());
        TestCase.assertEquals("millis", iso.millis().getName());
        TestCase.assertEquals(false, iso.eras().isSupported());
        TestCase.assertEquals(true, iso.centuries().isSupported());
        TestCase.assertEquals(true, iso.years().isSupported());
        TestCase.assertEquals(true, iso.weekyears().isSupported());
        TestCase.assertEquals(true, iso.months().isSupported());
        TestCase.assertEquals(true, iso.weeks().isSupported());
        TestCase.assertEquals(true, iso.days().isSupported());
        TestCase.assertEquals(true, iso.halfdays().isSupported());
        TestCase.assertEquals(true, iso.hours().isSupported());
        TestCase.assertEquals(true, iso.minutes().isSupported());
        TestCase.assertEquals(true, iso.seconds().isSupported());
        TestCase.assertEquals(true, iso.millis().isSupported());
        TestCase.assertEquals(false, iso.centuries().isPrecise());
        TestCase.assertEquals(false, iso.years().isPrecise());
        TestCase.assertEquals(false, iso.weekyears().isPrecise());
        TestCase.assertEquals(false, iso.months().isPrecise());
        TestCase.assertEquals(false, iso.weeks().isPrecise());
        TestCase.assertEquals(false, iso.days().isPrecise());
        TestCase.assertEquals(false, iso.halfdays().isPrecise());
        TestCase.assertEquals(true, iso.hours().isPrecise());
        TestCase.assertEquals(true, iso.minutes().isPrecise());
        TestCase.assertEquals(true, iso.seconds().isPrecise());
        TestCase.assertEquals(true, iso.millis().isPrecise());
        final ISOChronology isoUTC = ISOChronology.getInstanceUTC();
        TestCase.assertEquals(false, isoUTC.centuries().isPrecise());
        TestCase.assertEquals(false, isoUTC.years().isPrecise());
        TestCase.assertEquals(false, isoUTC.weekyears().isPrecise());
        TestCase.assertEquals(false, isoUTC.months().isPrecise());
        TestCase.assertEquals(true, isoUTC.weeks().isPrecise());
        TestCase.assertEquals(true, isoUTC.days().isPrecise());
        TestCase.assertEquals(true, isoUTC.halfdays().isPrecise());
        TestCase.assertEquals(true, isoUTC.hours().isPrecise());
        TestCase.assertEquals(true, isoUTC.minutes().isPrecise());
        TestCase.assertEquals(true, isoUTC.seconds().isPrecise());
        TestCase.assertEquals(true, isoUTC.millis().isPrecise());
        final DateTimeZone gmt = DateTimeZone.forID("Etc/GMT");
        final ISOChronology isoGMT = ISOChronology.getInstance(gmt);
        TestCase.assertEquals(false, isoGMT.centuries().isPrecise());
        TestCase.assertEquals(false, isoGMT.years().isPrecise());
        TestCase.assertEquals(false, isoGMT.weekyears().isPrecise());
        TestCase.assertEquals(false, isoGMT.months().isPrecise());
        TestCase.assertEquals(true, isoGMT.weeks().isPrecise());
        TestCase.assertEquals(true, isoGMT.days().isPrecise());
        TestCase.assertEquals(true, isoGMT.halfdays().isPrecise());
        TestCase.assertEquals(true, isoGMT.hours().isPrecise());
        TestCase.assertEquals(true, isoGMT.minutes().isPrecise());
        TestCase.assertEquals(true, isoGMT.seconds().isPrecise());
        TestCase.assertEquals(true, isoGMT.millis().isPrecise());
        final DateTimeZone offset = DateTimeZone.forOffsetHours(1);
        final ISOChronology isoOffset1 = ISOChronology.getInstance(offset);
        TestCase.assertEquals(false, isoOffset1.centuries().isPrecise());
        TestCase.assertEquals(false, isoOffset1.years().isPrecise());
        TestCase.assertEquals(false, isoOffset1.weekyears().isPrecise());
        TestCase.assertEquals(false, isoOffset1.months().isPrecise());
        TestCase.assertEquals(true, isoOffset1.weeks().isPrecise());
        TestCase.assertEquals(true, isoOffset1.days().isPrecise());
        TestCase.assertEquals(true, isoOffset1.halfdays().isPrecise());
        TestCase.assertEquals(true, isoOffset1.hours().isPrecise());
        TestCase.assertEquals(true, isoOffset1.minutes().isPrecise());
        TestCase.assertEquals(true, isoOffset1.seconds().isPrecise());
        TestCase.assertEquals(true, isoOffset1.millis().isPrecise());
    }

    public void testDateFields() {
        final ISOChronology iso = ISOChronology.getInstance();
        TestCase.assertEquals("era", iso.era().getName());
        TestCase.assertEquals("centuryOfEra", iso.centuryOfEra().getName());
        TestCase.assertEquals("yearOfCentury", iso.yearOfCentury().getName());
        TestCase.assertEquals("yearOfEra", iso.yearOfEra().getName());
        TestCase.assertEquals("year", iso.year().getName());
        TestCase.assertEquals("monthOfYear", iso.monthOfYear().getName());
        TestCase.assertEquals("weekyearOfCentury", iso.weekyearOfCentury().getName());
        TestCase.assertEquals("weekyear", iso.weekyear().getName());
        TestCase.assertEquals("weekOfWeekyear", iso.weekOfWeekyear().getName());
        TestCase.assertEquals("dayOfYear", iso.dayOfYear().getName());
        TestCase.assertEquals("dayOfMonth", iso.dayOfMonth().getName());
        TestCase.assertEquals("dayOfWeek", iso.dayOfWeek().getName());
        TestCase.assertEquals(true, iso.era().isSupported());
        TestCase.assertEquals(true, iso.centuryOfEra().isSupported());
        TestCase.assertEquals(true, iso.yearOfCentury().isSupported());
        TestCase.assertEquals(true, iso.yearOfEra().isSupported());
        TestCase.assertEquals(true, iso.year().isSupported());
        TestCase.assertEquals(true, iso.monthOfYear().isSupported());
        TestCase.assertEquals(true, iso.weekyearOfCentury().isSupported());
        TestCase.assertEquals(true, iso.weekyear().isSupported());
        TestCase.assertEquals(true, iso.weekOfWeekyear().isSupported());
        TestCase.assertEquals(true, iso.dayOfYear().isSupported());
        TestCase.assertEquals(true, iso.dayOfMonth().isSupported());
        TestCase.assertEquals(true, iso.dayOfWeek().isSupported());
        TestCase.assertEquals(iso.eras(), iso.era().getDurationField());
        TestCase.assertEquals(iso.centuries(), iso.centuryOfEra().getDurationField());
        TestCase.assertEquals(iso.years(), iso.yearOfCentury().getDurationField());
        TestCase.assertEquals(iso.years(), iso.yearOfEra().getDurationField());
        TestCase.assertEquals(iso.years(), iso.year().getDurationField());
        TestCase.assertEquals(iso.months(), iso.monthOfYear().getDurationField());
        TestCase.assertEquals(iso.weekyears(), iso.weekyearOfCentury().getDurationField());
        TestCase.assertEquals(iso.weekyears(), iso.weekyear().getDurationField());
        TestCase.assertEquals(iso.weeks(), iso.weekOfWeekyear().getDurationField());
        TestCase.assertEquals(iso.days(), iso.dayOfYear().getDurationField());
        TestCase.assertEquals(iso.days(), iso.dayOfMonth().getDurationField());
        TestCase.assertEquals(iso.days(), iso.dayOfWeek().getDurationField());
        TestCase.assertEquals(null, iso.era().getRangeDurationField());
        TestCase.assertEquals(iso.eras(), iso.centuryOfEra().getRangeDurationField());
        TestCase.assertEquals(iso.centuries(), iso.yearOfCentury().getRangeDurationField());
        TestCase.assertEquals(iso.eras(), iso.yearOfEra().getRangeDurationField());
        TestCase.assertEquals(null, iso.year().getRangeDurationField());
        TestCase.assertEquals(iso.years(), iso.monthOfYear().getRangeDurationField());
        TestCase.assertEquals(iso.centuries(), iso.weekyearOfCentury().getRangeDurationField());
        TestCase.assertEquals(null, iso.weekyear().getRangeDurationField());
        TestCase.assertEquals(iso.weekyears(), iso.weekOfWeekyear().getRangeDurationField());
        TestCase.assertEquals(iso.years(), iso.dayOfYear().getRangeDurationField());
        TestCase.assertEquals(iso.months(), iso.dayOfMonth().getRangeDurationField());
        TestCase.assertEquals(iso.weeks(), iso.dayOfWeek().getRangeDurationField());
    }

    public void testTimeFields() {
        final ISOChronology iso = ISOChronology.getInstance();
        TestCase.assertEquals("halfdayOfDay", iso.halfdayOfDay().getName());
        TestCase.assertEquals("clockhourOfHalfday", iso.clockhourOfHalfday().getName());
        TestCase.assertEquals("hourOfHalfday", iso.hourOfHalfday().getName());
        TestCase.assertEquals("clockhourOfDay", iso.clockhourOfDay().getName());
        TestCase.assertEquals("hourOfDay", iso.hourOfDay().getName());
        TestCase.assertEquals("minuteOfDay", iso.minuteOfDay().getName());
        TestCase.assertEquals("minuteOfHour", iso.minuteOfHour().getName());
        TestCase.assertEquals("secondOfDay", iso.secondOfDay().getName());
        TestCase.assertEquals("secondOfMinute", iso.secondOfMinute().getName());
        TestCase.assertEquals("millisOfDay", iso.millisOfDay().getName());
        TestCase.assertEquals("millisOfSecond", iso.millisOfSecond().getName());
        TestCase.assertEquals(true, iso.halfdayOfDay().isSupported());
        TestCase.assertEquals(true, iso.clockhourOfHalfday().isSupported());
        TestCase.assertEquals(true, iso.hourOfHalfday().isSupported());
        TestCase.assertEquals(true, iso.clockhourOfDay().isSupported());
        TestCase.assertEquals(true, iso.hourOfDay().isSupported());
        TestCase.assertEquals(true, iso.minuteOfDay().isSupported());
        TestCase.assertEquals(true, iso.minuteOfHour().isSupported());
        TestCase.assertEquals(true, iso.secondOfDay().isSupported());
        TestCase.assertEquals(true, iso.secondOfMinute().isSupported());
        TestCase.assertEquals(true, iso.millisOfDay().isSupported());
        TestCase.assertEquals(true, iso.millisOfSecond().isSupported());
    }

    public void testMaxYear() {
        final ISOChronology chrono = ISOChronology.getInstanceUTC();
        final int maxYear = chrono.year().getMaximumValue();
        DateTime start = new DateTime(maxYear, 1, 1, 0, 0, 0, 0, chrono);
        DateTime end = new DateTime(maxYear, 12, 31, 23, 59, 59, 999, chrono);
        TestCase.assertTrue(((start.getMillis()) > 0));
        TestCase.assertTrue(((end.getMillis()) > (start.getMillis())));
        TestCase.assertEquals(maxYear, start.getYear());
        TestCase.assertEquals(maxYear, end.getYear());
        long delta = (end.getMillis()) - (start.getMillis());
        long expectedDelta = ((start.year().isLeap() ? 366L : 365L) * (DateTimeConstants.MILLIS_PER_DAY)) - 1;
        TestCase.assertEquals(expectedDelta, delta);
        TestCase.assertEquals(start, new DateTime((maxYear + "-01-01T00:00:00.000Z"), chrono));
        TestCase.assertEquals(end, new DateTime((maxYear + "-12-31T23:59:59.999Z"), chrono));
        try {
            start.plusYears(1);
            TestCase.fail();
        } catch (IllegalFieldValueException e) {
        }
        try {
            end.plusYears(1);
            TestCase.fail();
        } catch (IllegalFieldValueException e) {
        }
        TestCase.assertEquals((maxYear + 1), chrono.year().get(Long.MAX_VALUE));
    }

    public void testMinYear() {
        final ISOChronology chrono = ISOChronology.getInstanceUTC();
        final int minYear = chrono.year().getMinimumValue();
        DateTime start = new DateTime(minYear, 1, 1, 0, 0, 0, 0, chrono);
        DateTime end = new DateTime(minYear, 12, 31, 23, 59, 59, 999, chrono);
        TestCase.assertTrue(((start.getMillis()) < 0));
        TestCase.assertTrue(((end.getMillis()) > (start.getMillis())));
        TestCase.assertEquals(minYear, start.getYear());
        TestCase.assertEquals(minYear, end.getYear());
        long delta = (end.getMillis()) - (start.getMillis());
        long expectedDelta = ((start.year().isLeap() ? 366L : 365L) * (DateTimeConstants.MILLIS_PER_DAY)) - 1;
        TestCase.assertEquals(expectedDelta, delta);
        TestCase.assertEquals(start, new DateTime((minYear + "-01-01T00:00:00.000Z"), chrono));
        TestCase.assertEquals(end, new DateTime((minYear + "-12-31T23:59:59.999Z"), chrono));
        try {
            start.minusYears(1);
            TestCase.fail();
        } catch (IllegalFieldValueException e) {
        }
        try {
            end.minusYears(1);
            TestCase.fail();
        } catch (IllegalFieldValueException e) {
        }
        TestCase.assertEquals((minYear - 1), chrono.year().get(Long.MIN_VALUE));
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
        testAdd("1580-01-01", DurationFieldType.years(), 4, "1584-01-01");
        testAdd("1580-02-29", DurationFieldType.years(), 4, "1584-02-29");
        testAdd("1580-10-01", DurationFieldType.years(), 4, "1584-10-01");
        testAdd("1580-10-10", DurationFieldType.years(), 4, "1584-10-10");
        testAdd("1580-10-15", DurationFieldType.years(), 4, "1584-10-15");
        testAdd("1580-12-31", DurationFieldType.years(), 4, "1584-12-31");
    }

    public void testAddMonths() {
        testAdd("1582-01-01", DurationFieldType.months(), 1, "1582-02-01");
        testAdd("1582-01-01", DurationFieldType.months(), 6, "1582-07-01");
        testAdd("1582-01-01", DurationFieldType.months(), 12, "1583-01-01");
        testAdd("1582-11-15", DurationFieldType.months(), 1, "1582-12-15");
        testAdd("1582-09-04", DurationFieldType.months(), 2, "1582-11-04");
        testAdd("1582-09-05", DurationFieldType.months(), 2, "1582-11-05");
        testAdd("1582-09-10", DurationFieldType.months(), 2, "1582-11-10");
        testAdd("1582-09-15", DurationFieldType.months(), 2, "1582-11-15");
        testAdd("1580-01-01", DurationFieldType.months(), 48, "1584-01-01");
        testAdd("1580-02-29", DurationFieldType.months(), 48, "1584-02-29");
        testAdd("1580-10-01", DurationFieldType.months(), 48, "1584-10-01");
        testAdd("1580-10-10", DurationFieldType.months(), 48, "1584-10-10");
        testAdd("1580-10-15", DurationFieldType.months(), 48, "1584-10-15");
        testAdd("1580-12-31", DurationFieldType.months(), 48, "1584-12-31");
    }

    public void testTimeOfDayAdd() {
        TimeOfDay start = new TimeOfDay(12, 30);
        TimeOfDay end = new TimeOfDay(10, 30);
        TestCase.assertEquals(end, start.plusHours(22));
        TestCase.assertEquals(start, end.minusHours(22));
        TestCase.assertEquals(end, start.plusMinutes((22 * 60)));
        TestCase.assertEquals(start, end.minusMinutes((22 * 60)));
    }

    public void testPartialDayOfYearAdd() {
        Partial start = new Partial().with(DateTimeFieldType.year(), 2000).with(DateTimeFieldType.dayOfYear(), 366);
        Partial end = new Partial().with(DateTimeFieldType.year(), 2004).with(DateTimeFieldType.dayOfYear(), 366);
        TestCase.assertEquals(end, start.withFieldAdded(DurationFieldType.days(), (((365 + 365) + 365) + 366)));
        TestCase.assertEquals(start, end.withFieldAdded(DurationFieldType.days(), (-(((365 + 365) + 365) + 366))));
    }

    public void testMaximumValue() {
        DateMidnight dt = new DateMidnight(1570, 1, 1);
        while ((dt.getYear()) < 1590) {
            dt = dt.plusDays(1);
            YearMonthDay ymd = dt.toYearMonthDay();
            TestCase.assertEquals(dt.year().getMaximumValue(), ymd.year().getMaximumValue());
            TestCase.assertEquals(dt.monthOfYear().getMaximumValue(), ymd.monthOfYear().getMaximumValue());
            TestCase.assertEquals(dt.dayOfMonth().getMaximumValue(), ymd.dayOfMonth().getMaximumValue());
        } 
    }

    public void testLeap_28feb() {
        Chronology chrono = ISOChronology.getInstance();
        DateTime dt = new DateTime(2012, 2, 28, 0, 0, chrono);
        TestCase.assertEquals(true, dt.year().isLeap());
        TestCase.assertEquals(true, dt.monthOfYear().isLeap());
        TestCase.assertEquals(false, dt.dayOfMonth().isLeap());
        TestCase.assertEquals(false, dt.dayOfYear().isLeap());
    }

    public void testLeap_29feb() {
        Chronology chrono = ISOChronology.getInstance();
        DateTime dt = new DateTime(2012, 2, 29, 0, 0, chrono);
        TestCase.assertEquals(true, dt.year().isLeap());
        TestCase.assertEquals(true, dt.monthOfYear().isLeap());
        TestCase.assertEquals(true, dt.dayOfMonth().isLeap());
        TestCase.assertEquals(true, dt.dayOfYear().isLeap());
    }
}

