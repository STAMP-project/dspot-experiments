/**
 * Copyright 2001-2013 Stephen Colebourne
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


import BuddhistChronology.BE;
import DateTimeZone.UTC;
import java.util.Locale;
import java.util.TimeZone;
import junit.framework.TestCase;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;


/**
 * This class is a Junit unit test for BuddhistChronology.
 *
 * @author Stephen Colebourne
 */
public class TestBuddhistChronology extends TestCase {
    private static int SKIP = 1 * (DateTimeConstants.MILLIS_PER_DAY);

    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final DateTimeZone TOKYO = DateTimeZone.forID("Asia/Tokyo");

    private static final Chronology BUDDHIST_UTC = BuddhistChronology.getInstanceUTC();

    private static final Chronology JULIAN_UTC = JulianChronology.getInstanceUTC();

    private static final Chronology GJ_UTC = GJChronology.getInstanceUTC();

    private static final Chronology ISO_UTC = ISOChronology.getInstanceUTC();

    long y2002days = ((((((((((((((((((((((((((((((365 + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365;

    // 2002-06-09
    private long TEST_TIME_NOW = ((((((((y2002days) + 31L) + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (DateTimeConstants.MILLIS_PER_DAY);

    private DateTimeZone originalDateTimeZone = null;

    private TimeZone originalTimeZone = null;

    private Locale originalLocale = null;

    public TestBuddhistChronology(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testFactoryUTC() {
        TestCase.assertEquals(UTC, BuddhistChronology.getInstanceUTC().getZone());
        TestCase.assertSame(BuddhistChronology.class, BuddhistChronology.getInstanceUTC().getClass());
    }

    public void testFactory() {
        TestCase.assertEquals(TestBuddhistChronology.LONDON, BuddhistChronology.getInstance().getZone());
        TestCase.assertSame(BuddhistChronology.class, BuddhistChronology.getInstance().getClass());
    }

    public void testFactory_Zone() {
        TestCase.assertEquals(TestBuddhistChronology.TOKYO, BuddhistChronology.getInstance(TestBuddhistChronology.TOKYO).getZone());
        TestCase.assertEquals(TestBuddhistChronology.PARIS, BuddhistChronology.getInstance(TestBuddhistChronology.PARIS).getZone());
        TestCase.assertEquals(TestBuddhistChronology.LONDON, BuddhistChronology.getInstance(null).getZone());
        TestCase.assertSame(BuddhistChronology.class, BuddhistChronology.getInstance(TestBuddhistChronology.TOKYO).getClass());
    }

    // -----------------------------------------------------------------------
    public void testEquality() {
        TestCase.assertSame(BuddhistChronology.getInstance(TestBuddhistChronology.TOKYO), BuddhistChronology.getInstance(TestBuddhistChronology.TOKYO));
        TestCase.assertSame(BuddhistChronology.getInstance(TestBuddhistChronology.LONDON), BuddhistChronology.getInstance(TestBuddhistChronology.LONDON));
        TestCase.assertSame(BuddhistChronology.getInstance(TestBuddhistChronology.PARIS), BuddhistChronology.getInstance(TestBuddhistChronology.PARIS));
        TestCase.assertSame(BuddhistChronology.getInstanceUTC(), BuddhistChronology.getInstanceUTC());
        TestCase.assertSame(BuddhistChronology.getInstance(), BuddhistChronology.getInstance(TestBuddhistChronology.LONDON));
    }

    public void testWithUTC() {
        TestCase.assertSame(BuddhistChronology.getInstanceUTC(), BuddhistChronology.getInstance(TestBuddhistChronology.LONDON).withUTC());
        TestCase.assertSame(BuddhistChronology.getInstanceUTC(), BuddhistChronology.getInstance(TestBuddhistChronology.TOKYO).withUTC());
        TestCase.assertSame(BuddhistChronology.getInstanceUTC(), BuddhistChronology.getInstanceUTC().withUTC());
        TestCase.assertSame(BuddhistChronology.getInstanceUTC(), BuddhistChronology.getInstance().withUTC());
    }

    public void testWithZone() {
        TestCase.assertSame(BuddhistChronology.getInstance(TestBuddhistChronology.TOKYO), BuddhistChronology.getInstance(TestBuddhistChronology.TOKYO).withZone(TestBuddhistChronology.TOKYO));
        TestCase.assertSame(BuddhistChronology.getInstance(TestBuddhistChronology.LONDON), BuddhistChronology.getInstance(TestBuddhistChronology.TOKYO).withZone(TestBuddhistChronology.LONDON));
        TestCase.assertSame(BuddhistChronology.getInstance(TestBuddhistChronology.PARIS), BuddhistChronology.getInstance(TestBuddhistChronology.TOKYO).withZone(TestBuddhistChronology.PARIS));
        TestCase.assertSame(BuddhistChronology.getInstance(TestBuddhistChronology.LONDON), BuddhistChronology.getInstance(TestBuddhistChronology.TOKYO).withZone(null));
        TestCase.assertSame(BuddhistChronology.getInstance(TestBuddhistChronology.PARIS), BuddhistChronology.getInstance().withZone(TestBuddhistChronology.PARIS));
        TestCase.assertSame(BuddhistChronology.getInstance(TestBuddhistChronology.PARIS), BuddhistChronology.getInstanceUTC().withZone(TestBuddhistChronology.PARIS));
    }

    public void testToString() {
        TestCase.assertEquals("BuddhistChronology[Europe/London]", BuddhistChronology.getInstance(TestBuddhistChronology.LONDON).toString());
        TestCase.assertEquals("BuddhistChronology[Asia/Tokyo]", BuddhistChronology.getInstance(TestBuddhistChronology.TOKYO).toString());
        TestCase.assertEquals("BuddhistChronology[Europe/London]", BuddhistChronology.getInstance().toString());
        TestCase.assertEquals("BuddhistChronology[UTC]", BuddhistChronology.getInstanceUTC().toString());
    }

    // -----------------------------------------------------------------------
    public void testDurationFields() {
        final BuddhistChronology buddhist = BuddhistChronology.getInstance();
        TestCase.assertEquals("eras", buddhist.eras().getName());
        TestCase.assertEquals("centuries", buddhist.centuries().getName());
        TestCase.assertEquals("years", buddhist.years().getName());
        TestCase.assertEquals("weekyears", buddhist.weekyears().getName());
        TestCase.assertEquals("months", buddhist.months().getName());
        TestCase.assertEquals("weeks", buddhist.weeks().getName());
        TestCase.assertEquals("days", buddhist.days().getName());
        TestCase.assertEquals("halfdays", GregorianChronology.getInstance().halfdays().getName());
        TestCase.assertEquals("hours", buddhist.hours().getName());
        TestCase.assertEquals("minutes", buddhist.minutes().getName());
        TestCase.assertEquals("seconds", buddhist.seconds().getName());
        TestCase.assertEquals("millis", buddhist.millis().getName());
        TestCase.assertEquals(false, buddhist.eras().isSupported());
        TestCase.assertEquals(true, buddhist.centuries().isSupported());
        TestCase.assertEquals(true, buddhist.years().isSupported());
        TestCase.assertEquals(true, buddhist.weekyears().isSupported());
        TestCase.assertEquals(true, buddhist.months().isSupported());
        TestCase.assertEquals(true, buddhist.weeks().isSupported());
        TestCase.assertEquals(true, buddhist.days().isSupported());
        TestCase.assertEquals(true, buddhist.halfdays().isSupported());
        TestCase.assertEquals(true, buddhist.hours().isSupported());
        TestCase.assertEquals(true, buddhist.minutes().isSupported());
        TestCase.assertEquals(true, buddhist.seconds().isSupported());
        TestCase.assertEquals(true, buddhist.millis().isSupported());
        TestCase.assertEquals(false, buddhist.centuries().isPrecise());
        TestCase.assertEquals(false, buddhist.years().isPrecise());
        TestCase.assertEquals(false, buddhist.weekyears().isPrecise());
        TestCase.assertEquals(false, buddhist.months().isPrecise());
        TestCase.assertEquals(false, buddhist.weeks().isPrecise());
        TestCase.assertEquals(false, buddhist.days().isPrecise());
        TestCase.assertEquals(false, buddhist.halfdays().isPrecise());
        TestCase.assertEquals(true, buddhist.hours().isPrecise());
        TestCase.assertEquals(true, buddhist.minutes().isPrecise());
        TestCase.assertEquals(true, buddhist.seconds().isPrecise());
        TestCase.assertEquals(true, buddhist.millis().isPrecise());
        final BuddhistChronology buddhistUTC = BuddhistChronology.getInstanceUTC();
        TestCase.assertEquals(false, buddhistUTC.centuries().isPrecise());
        TestCase.assertEquals(false, buddhistUTC.years().isPrecise());
        TestCase.assertEquals(false, buddhistUTC.weekyears().isPrecise());
        TestCase.assertEquals(false, buddhistUTC.months().isPrecise());
        TestCase.assertEquals(true, buddhistUTC.weeks().isPrecise());
        TestCase.assertEquals(true, buddhistUTC.days().isPrecise());
        TestCase.assertEquals(true, buddhistUTC.halfdays().isPrecise());
        TestCase.assertEquals(true, buddhistUTC.hours().isPrecise());
        TestCase.assertEquals(true, buddhistUTC.minutes().isPrecise());
        TestCase.assertEquals(true, buddhistUTC.seconds().isPrecise());
        TestCase.assertEquals(true, buddhistUTC.millis().isPrecise());
        final DateTimeZone gmt = DateTimeZone.forID("Etc/GMT");
        final BuddhistChronology buddhistGMT = BuddhistChronology.getInstance(gmt);
        TestCase.assertEquals(false, buddhistGMT.centuries().isPrecise());
        TestCase.assertEquals(false, buddhistGMT.years().isPrecise());
        TestCase.assertEquals(false, buddhistGMT.weekyears().isPrecise());
        TestCase.assertEquals(false, buddhistGMT.months().isPrecise());
        TestCase.assertEquals(true, buddhistGMT.weeks().isPrecise());
        TestCase.assertEquals(true, buddhistGMT.days().isPrecise());
        TestCase.assertEquals(true, buddhistGMT.halfdays().isPrecise());
        TestCase.assertEquals(true, buddhistGMT.hours().isPrecise());
        TestCase.assertEquals(true, buddhistGMT.minutes().isPrecise());
        TestCase.assertEquals(true, buddhistGMT.seconds().isPrecise());
        TestCase.assertEquals(true, buddhistGMT.millis().isPrecise());
    }

    public void testDateFields() {
        final BuddhistChronology buddhist = BuddhistChronology.getInstance();
        TestCase.assertEquals("era", buddhist.era().getName());
        TestCase.assertEquals("centuryOfEra", buddhist.centuryOfEra().getName());
        TestCase.assertEquals("yearOfCentury", buddhist.yearOfCentury().getName());
        TestCase.assertEquals("yearOfEra", buddhist.yearOfEra().getName());
        TestCase.assertEquals("year", buddhist.year().getName());
        TestCase.assertEquals("monthOfYear", buddhist.monthOfYear().getName());
        TestCase.assertEquals("weekyearOfCentury", buddhist.weekyearOfCentury().getName());
        TestCase.assertEquals("weekyear", buddhist.weekyear().getName());
        TestCase.assertEquals("weekOfWeekyear", buddhist.weekOfWeekyear().getName());
        TestCase.assertEquals("dayOfYear", buddhist.dayOfYear().getName());
        TestCase.assertEquals("dayOfMonth", buddhist.dayOfMonth().getName());
        TestCase.assertEquals("dayOfWeek", buddhist.dayOfWeek().getName());
        TestCase.assertEquals(true, buddhist.era().isSupported());
        TestCase.assertEquals(true, buddhist.centuryOfEra().isSupported());
        TestCase.assertEquals(true, buddhist.yearOfCentury().isSupported());
        TestCase.assertEquals(true, buddhist.yearOfEra().isSupported());
        TestCase.assertEquals(true, buddhist.year().isSupported());
        TestCase.assertEquals(true, buddhist.monthOfYear().isSupported());
        TestCase.assertEquals(true, buddhist.weekyearOfCentury().isSupported());
        TestCase.assertEquals(true, buddhist.weekyear().isSupported());
        TestCase.assertEquals(true, buddhist.weekOfWeekyear().isSupported());
        TestCase.assertEquals(true, buddhist.dayOfYear().isSupported());
        TestCase.assertEquals(true, buddhist.dayOfMonth().isSupported());
        TestCase.assertEquals(true, buddhist.dayOfWeek().isSupported());
        TestCase.assertEquals(buddhist.eras(), buddhist.era().getDurationField());
        TestCase.assertEquals(buddhist.centuries(), buddhist.centuryOfEra().getDurationField());
        TestCase.assertEquals(buddhist.years(), buddhist.yearOfCentury().getDurationField());
        TestCase.assertEquals(buddhist.years(), buddhist.yearOfEra().getDurationField());
        TestCase.assertEquals(buddhist.years(), buddhist.year().getDurationField());
        TestCase.assertEquals(buddhist.months(), buddhist.monthOfYear().getDurationField());
        TestCase.assertEquals(buddhist.weekyears(), buddhist.weekyearOfCentury().getDurationField());
        TestCase.assertEquals(buddhist.weekyears(), buddhist.weekyear().getDurationField());
        TestCase.assertEquals(buddhist.weeks(), buddhist.weekOfWeekyear().getDurationField());
        TestCase.assertEquals(buddhist.days(), buddhist.dayOfYear().getDurationField());
        TestCase.assertEquals(buddhist.days(), buddhist.dayOfMonth().getDurationField());
        TestCase.assertEquals(buddhist.days(), buddhist.dayOfWeek().getDurationField());
        TestCase.assertEquals(null, buddhist.era().getRangeDurationField());
        TestCase.assertEquals(buddhist.eras(), buddhist.centuryOfEra().getRangeDurationField());
        TestCase.assertEquals(buddhist.centuries(), buddhist.yearOfCentury().getRangeDurationField());
        TestCase.assertEquals(buddhist.eras(), buddhist.yearOfEra().getRangeDurationField());
        TestCase.assertEquals(null, buddhist.year().getRangeDurationField());
        TestCase.assertEquals(buddhist.years(), buddhist.monthOfYear().getRangeDurationField());
        TestCase.assertEquals(buddhist.centuries(), buddhist.weekyearOfCentury().getRangeDurationField());
        TestCase.assertEquals(null, buddhist.weekyear().getRangeDurationField());
        TestCase.assertEquals(buddhist.weekyears(), buddhist.weekOfWeekyear().getRangeDurationField());
        TestCase.assertEquals(buddhist.years(), buddhist.dayOfYear().getRangeDurationField());
        TestCase.assertEquals(buddhist.months(), buddhist.dayOfMonth().getRangeDurationField());
        TestCase.assertEquals(buddhist.weeks(), buddhist.dayOfWeek().getRangeDurationField());
    }

    public void testTimeFields() {
        final BuddhistChronology buddhist = BuddhistChronology.getInstance();
        TestCase.assertEquals("halfdayOfDay", buddhist.halfdayOfDay().getName());
        TestCase.assertEquals("clockhourOfHalfday", buddhist.clockhourOfHalfday().getName());
        TestCase.assertEquals("hourOfHalfday", buddhist.hourOfHalfday().getName());
        TestCase.assertEquals("clockhourOfDay", buddhist.clockhourOfDay().getName());
        TestCase.assertEquals("hourOfDay", buddhist.hourOfDay().getName());
        TestCase.assertEquals("minuteOfDay", buddhist.minuteOfDay().getName());
        TestCase.assertEquals("minuteOfHour", buddhist.minuteOfHour().getName());
        TestCase.assertEquals("secondOfDay", buddhist.secondOfDay().getName());
        TestCase.assertEquals("secondOfMinute", buddhist.secondOfMinute().getName());
        TestCase.assertEquals("millisOfDay", buddhist.millisOfDay().getName());
        TestCase.assertEquals("millisOfSecond", buddhist.millisOfSecond().getName());
        TestCase.assertEquals(true, buddhist.halfdayOfDay().isSupported());
        TestCase.assertEquals(true, buddhist.clockhourOfHalfday().isSupported());
        TestCase.assertEquals(true, buddhist.hourOfHalfday().isSupported());
        TestCase.assertEquals(true, buddhist.clockhourOfDay().isSupported());
        TestCase.assertEquals(true, buddhist.hourOfDay().isSupported());
        TestCase.assertEquals(true, buddhist.minuteOfDay().isSupported());
        TestCase.assertEquals(true, buddhist.minuteOfHour().isSupported());
        TestCase.assertEquals(true, buddhist.secondOfDay().isSupported());
        TestCase.assertEquals(true, buddhist.secondOfMinute().isSupported());
        TestCase.assertEquals(true, buddhist.millisOfDay().isSupported());
        TestCase.assertEquals(true, buddhist.millisOfSecond().isSupported());
    }

    // -----------------------------------------------------------------------
    public void testEpoch() {
        DateTime epoch = new DateTime(1, 1, 1, 0, 0, 0, 0, TestBuddhistChronology.BUDDHIST_UTC);
        TestCase.assertEquals(new DateTime((-543), 1, 1, 0, 0, 0, 0, TestBuddhistChronology.JULIAN_UTC), epoch.withChronology(TestBuddhistChronology.JULIAN_UTC));
    }

    public void testEra() {
        TestCase.assertEquals(1, BE);
        try {
            new DateTime((-1), 13, 5, 0, 0, 0, 0, TestBuddhistChronology.BUDDHIST_UTC);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testKeyYears() {
        DateTime bd = new DateTime(2513, 1, 1, 0, 0, 0, 0, TestBuddhistChronology.BUDDHIST_UTC);
        DateTime jd = new DateTime(1970, 1, 1, 0, 0, 0, 0, TestBuddhistChronology.GJ_UTC);
        TestCase.assertEquals(jd, bd.withChronology(TestBuddhistChronology.GJ_UTC));
        TestCase.assertEquals(2513, bd.getYear());
        TestCase.assertEquals(2513, bd.getYearOfEra());
        TestCase.assertEquals(2513, bd.plus(Period.weeks(1)).getWeekyear());
        bd = new DateTime(2126, 1, 1, 0, 0, 0, 0, TestBuddhistChronology.BUDDHIST_UTC);
        jd = new DateTime(1583, 1, 1, 0, 0, 0, 0, TestBuddhistChronology.GJ_UTC);
        TestCase.assertEquals(jd, bd.withChronology(TestBuddhistChronology.GJ_UTC));
        TestCase.assertEquals(2126, bd.getYear());
        TestCase.assertEquals(2126, bd.getYearOfEra());
        TestCase.assertEquals(2126, bd.plus(Period.weeks(1)).getWeekyear());
        bd = new DateTime(2125, 1, 1, 0, 0, 0, 0, TestBuddhistChronology.BUDDHIST_UTC);
        jd = new DateTime(1582, 1, 1, 0, 0, 0, 0, TestBuddhistChronology.GJ_UTC);
        TestCase.assertEquals(jd, bd.withChronology(TestBuddhistChronology.GJ_UTC));
        TestCase.assertEquals(2125, bd.getYear());
        TestCase.assertEquals(2125, bd.getYearOfEra());
        TestCase.assertEquals(2125, bd.plus(Period.weeks(1)).getWeekyear());
        bd = new DateTime(544, 1, 1, 0, 0, 0, 0, TestBuddhistChronology.BUDDHIST_UTC);
        jd = new DateTime(1, 1, 1, 0, 0, 0, 0, TestBuddhistChronology.GJ_UTC);
        TestCase.assertEquals(jd, bd.withChronology(TestBuddhistChronology.GJ_UTC));
        TestCase.assertEquals(544, bd.getYear());
        TestCase.assertEquals(544, bd.getYearOfEra());
        TestCase.assertEquals(544, bd.plus(Period.weeks(1)).getWeekyear());
        bd = new DateTime(543, 1, 1, 0, 0, 0, 0, TestBuddhistChronology.BUDDHIST_UTC);
        jd = new DateTime((-1), 1, 1, 0, 0, 0, 0, TestBuddhistChronology.GJ_UTC);
        TestCase.assertEquals(jd, bd.withChronology(TestBuddhistChronology.GJ_UTC));
        TestCase.assertEquals(543, bd.getYear());
        TestCase.assertEquals(543, bd.getYearOfEra());
        TestCase.assertEquals(543, bd.plus(Period.weeks(1)).getWeekyear());
        bd = new DateTime(1, 1, 1, 0, 0, 0, 0, TestBuddhistChronology.BUDDHIST_UTC);
        jd = new DateTime((-543), 1, 1, 0, 0, 0, 0, TestBuddhistChronology.GJ_UTC);
        TestCase.assertEquals(jd, bd.withChronology(TestBuddhistChronology.GJ_UTC));
        TestCase.assertEquals(1, bd.getYear());
        TestCase.assertEquals(1, bd.getYearOfEra());
        TestCase.assertEquals(1, bd.plus(Period.weeks(1)).getWeekyear());
    }

    public void testCalendar() {
        if (TestAll.FAST) {
            return;
        }
        System.out.println("\nTestBuddhistChronology.testCalendar");
        DateTime epoch = new DateTime(1, 1, 1, 0, 0, 0, 0, TestBuddhistChronology.BUDDHIST_UTC);
        long millis = epoch.getMillis();
        long end = getMillis();
        DateTimeField dayOfWeek = TestBuddhistChronology.BUDDHIST_UTC.dayOfWeek();
        DateTimeField weekOfWeekyear = TestBuddhistChronology.GJ_UTC.weekOfWeekyear();
        DateTimeField dayOfYear = TestBuddhistChronology.BUDDHIST_UTC.dayOfYear();
        DateTimeField dayOfMonth = TestBuddhistChronology.BUDDHIST_UTC.dayOfMonth();
        DateTimeField monthOfYear = TestBuddhistChronology.BUDDHIST_UTC.monthOfYear();
        DateTimeField year = TestBuddhistChronology.BUDDHIST_UTC.year();
        DateTimeField yearOfEra = TestBuddhistChronology.BUDDHIST_UTC.yearOfEra();
        DateTimeField era = TestBuddhistChronology.BUDDHIST_UTC.era();
        DateTimeField gjDayOfWeek = TestBuddhistChronology.GJ_UTC.dayOfWeek();
        DateTimeField gjWeekOfWeekyear = TestBuddhistChronology.GJ_UTC.weekOfWeekyear();
        DateTimeField gjDayOfYear = TestBuddhistChronology.GJ_UTC.dayOfYear();
        DateTimeField gjDayOfMonth = TestBuddhistChronology.GJ_UTC.dayOfMonth();
        DateTimeField gjMonthOfYear = TestBuddhistChronology.GJ_UTC.monthOfYear();
        DateTimeField gjYear = TestBuddhistChronology.GJ_UTC.year();
        while (millis < end) {
            TestCase.assertEquals(gjDayOfWeek.get(millis), dayOfWeek.get(millis));
            TestCase.assertEquals(gjDayOfYear.get(millis), dayOfYear.get(millis));
            TestCase.assertEquals(gjDayOfMonth.get(millis), dayOfMonth.get(millis));
            TestCase.assertEquals(gjMonthOfYear.get(millis), monthOfYear.get(millis));
            TestCase.assertEquals(gjWeekOfWeekyear.get(millis), weekOfWeekyear.get(millis));
            TestCase.assertEquals(1, era.get(millis));
            int yearValue = gjYear.get(millis);
            if (yearValue <= 0) {
                yearValue++;
            }
            yearValue += 543;
            TestCase.assertEquals(yearValue, year.get(millis));
            TestCase.assertEquals(yearValue, yearOfEra.get(millis));
            millis += TestBuddhistChronology.SKIP;
        } 
    }
}

