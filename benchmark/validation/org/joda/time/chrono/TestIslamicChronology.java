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


import DateTimeConstants.MONDAY;
import DateTimeConstants.SATURDAY;
import DateTimeConstants.TUESDAY;
import DateTimeZone.UTC;
import IslamicChronology.AH;
import IslamicChronology.LEAP_YEAR_15_BASED;
import IslamicChronology.LEAP_YEAR_16_BASED;
import IslamicChronology.LEAP_YEAR_HABASH_AL_HASIB;
import IslamicChronology.LEAP_YEAR_INDIAN;
import java.util.Locale;
import java.util.TimeZone;
import junit.framework.TestCase;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTime.Property;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationFieldType;


/**
 * This class is a Junit unit test for IslamicChronology.
 *
 * @author Stephen Colebourne
 */
public class TestIslamicChronology extends TestCase {
    private static long SKIP = 1 * (DateTimeConstants.MILLIS_PER_DAY);

    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final DateTimeZone TOKYO = DateTimeZone.forID("Asia/Tokyo");

    private static final Chronology ISLAMIC_UTC = IslamicChronology.getInstanceUTC();

    private static final Chronology JULIAN_UTC = JulianChronology.getInstanceUTC();

    private static final Chronology ISO_UTC = ISOChronology.getInstanceUTC();

    long y2002days = ((((((((((((((((((((((((((((((365 + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365;

    // 2002-06-09
    private long TEST_TIME_NOW = ((((((((y2002days) + 31L) + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (DateTimeConstants.MILLIS_PER_DAY);

    private DateTimeZone originalDateTimeZone = null;

    private TimeZone originalTimeZone = null;

    private Locale originalLocale = null;

    public TestIslamicChronology(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testFactoryUTC() {
        TestCase.assertEquals(UTC, IslamicChronology.getInstanceUTC().getZone());
        TestCase.assertSame(IslamicChronology.class, IslamicChronology.getInstanceUTC().getClass());
    }

    public void testFactory() {
        TestCase.assertEquals(TestIslamicChronology.LONDON, IslamicChronology.getInstance().getZone());
        TestCase.assertSame(IslamicChronology.class, IslamicChronology.getInstance().getClass());
    }

    public void testFactory_Zone() {
        TestCase.assertEquals(TestIslamicChronology.TOKYO, IslamicChronology.getInstance(TestIslamicChronology.TOKYO).getZone());
        TestCase.assertEquals(TestIslamicChronology.PARIS, IslamicChronology.getInstance(TestIslamicChronology.PARIS).getZone());
        TestCase.assertEquals(TestIslamicChronology.LONDON, IslamicChronology.getInstance(null).getZone());
        TestCase.assertSame(IslamicChronology.class, IslamicChronology.getInstance(TestIslamicChronology.TOKYO).getClass());
    }

    // -----------------------------------------------------------------------
    public void testEquality() {
        TestCase.assertSame(IslamicChronology.getInstance(TestIslamicChronology.TOKYO), IslamicChronology.getInstance(TestIslamicChronology.TOKYO));
        TestCase.assertSame(IslamicChronology.getInstance(TestIslamicChronology.LONDON), IslamicChronology.getInstance(TestIslamicChronology.LONDON));
        TestCase.assertSame(IslamicChronology.getInstance(TestIslamicChronology.PARIS), IslamicChronology.getInstance(TestIslamicChronology.PARIS));
        TestCase.assertSame(IslamicChronology.getInstanceUTC(), IslamicChronology.getInstanceUTC());
        TestCase.assertSame(IslamicChronology.getInstance(), IslamicChronology.getInstance(TestIslamicChronology.LONDON));
    }

    public void testWithUTC() {
        TestCase.assertSame(IslamicChronology.getInstanceUTC(), IslamicChronology.getInstance(TestIslamicChronology.LONDON).withUTC());
        TestCase.assertSame(IslamicChronology.getInstanceUTC(), IslamicChronology.getInstance(TestIslamicChronology.TOKYO).withUTC());
        TestCase.assertSame(IslamicChronology.getInstanceUTC(), IslamicChronology.getInstanceUTC().withUTC());
        TestCase.assertSame(IslamicChronology.getInstanceUTC(), IslamicChronology.getInstance().withUTC());
    }

    public void testWithZone() {
        TestCase.assertSame(IslamicChronology.getInstance(TestIslamicChronology.TOKYO), IslamicChronology.getInstance(TestIslamicChronology.TOKYO).withZone(TestIslamicChronology.TOKYO));
        TestCase.assertSame(IslamicChronology.getInstance(TestIslamicChronology.LONDON), IslamicChronology.getInstance(TestIslamicChronology.TOKYO).withZone(TestIslamicChronology.LONDON));
        TestCase.assertSame(IslamicChronology.getInstance(TestIslamicChronology.PARIS), IslamicChronology.getInstance(TestIslamicChronology.TOKYO).withZone(TestIslamicChronology.PARIS));
        TestCase.assertSame(IslamicChronology.getInstance(TestIslamicChronology.LONDON), IslamicChronology.getInstance(TestIslamicChronology.TOKYO).withZone(null));
        TestCase.assertSame(IslamicChronology.getInstance(TestIslamicChronology.PARIS), IslamicChronology.getInstance().withZone(TestIslamicChronology.PARIS));
        TestCase.assertSame(IslamicChronology.getInstance(TestIslamicChronology.PARIS), IslamicChronology.getInstanceUTC().withZone(TestIslamicChronology.PARIS));
    }

    public void testToString() {
        TestCase.assertEquals("IslamicChronology[Europe/London]", IslamicChronology.getInstance(TestIslamicChronology.LONDON).toString());
        TestCase.assertEquals("IslamicChronology[Asia/Tokyo]", IslamicChronology.getInstance(TestIslamicChronology.TOKYO).toString());
        TestCase.assertEquals("IslamicChronology[Europe/London]", IslamicChronology.getInstance().toString());
        TestCase.assertEquals("IslamicChronology[UTC]", IslamicChronology.getInstanceUTC().toString());
    }

    // -----------------------------------------------------------------------
    public void testDurationFields() {
        final IslamicChronology islamic = IslamicChronology.getInstance();
        TestCase.assertEquals("eras", islamic.eras().getName());
        TestCase.assertEquals("centuries", islamic.centuries().getName());
        TestCase.assertEquals("years", islamic.years().getName());
        TestCase.assertEquals("weekyears", islamic.weekyears().getName());
        TestCase.assertEquals("months", islamic.months().getName());
        TestCase.assertEquals("weeks", islamic.weeks().getName());
        TestCase.assertEquals("days", islamic.days().getName());
        TestCase.assertEquals("halfdays", islamic.halfdays().getName());
        TestCase.assertEquals("hours", islamic.hours().getName());
        TestCase.assertEquals("minutes", islamic.minutes().getName());
        TestCase.assertEquals("seconds", islamic.seconds().getName());
        TestCase.assertEquals("millis", islamic.millis().getName());
        TestCase.assertEquals(false, islamic.eras().isSupported());
        TestCase.assertEquals(true, islamic.centuries().isSupported());
        TestCase.assertEquals(true, islamic.years().isSupported());
        TestCase.assertEquals(true, islamic.weekyears().isSupported());
        TestCase.assertEquals(true, islamic.months().isSupported());
        TestCase.assertEquals(true, islamic.weeks().isSupported());
        TestCase.assertEquals(true, islamic.days().isSupported());
        TestCase.assertEquals(true, islamic.halfdays().isSupported());
        TestCase.assertEquals(true, islamic.hours().isSupported());
        TestCase.assertEquals(true, islamic.minutes().isSupported());
        TestCase.assertEquals(true, islamic.seconds().isSupported());
        TestCase.assertEquals(true, islamic.millis().isSupported());
        TestCase.assertEquals(false, islamic.centuries().isPrecise());
        TestCase.assertEquals(false, islamic.years().isPrecise());
        TestCase.assertEquals(false, islamic.weekyears().isPrecise());
        TestCase.assertEquals(false, islamic.months().isPrecise());
        TestCase.assertEquals(false, islamic.weeks().isPrecise());
        TestCase.assertEquals(false, islamic.days().isPrecise());
        TestCase.assertEquals(false, islamic.halfdays().isPrecise());
        TestCase.assertEquals(true, islamic.hours().isPrecise());
        TestCase.assertEquals(true, islamic.minutes().isPrecise());
        TestCase.assertEquals(true, islamic.seconds().isPrecise());
        TestCase.assertEquals(true, islamic.millis().isPrecise());
        final IslamicChronology islamicUTC = IslamicChronology.getInstanceUTC();
        TestCase.assertEquals(false, islamicUTC.centuries().isPrecise());
        TestCase.assertEquals(false, islamicUTC.years().isPrecise());
        TestCase.assertEquals(false, islamicUTC.weekyears().isPrecise());
        TestCase.assertEquals(false, islamicUTC.months().isPrecise());
        TestCase.assertEquals(true, islamicUTC.weeks().isPrecise());
        TestCase.assertEquals(true, islamicUTC.days().isPrecise());
        TestCase.assertEquals(true, islamicUTC.halfdays().isPrecise());
        TestCase.assertEquals(true, islamicUTC.hours().isPrecise());
        TestCase.assertEquals(true, islamicUTC.minutes().isPrecise());
        TestCase.assertEquals(true, islamicUTC.seconds().isPrecise());
        TestCase.assertEquals(true, islamicUTC.millis().isPrecise());
        final DateTimeZone gmt = DateTimeZone.forID("Etc/GMT");
        final IslamicChronology islamicGMT = IslamicChronology.getInstance(gmt);
        TestCase.assertEquals(false, islamicGMT.centuries().isPrecise());
        TestCase.assertEquals(false, islamicGMT.years().isPrecise());
        TestCase.assertEquals(false, islamicGMT.weekyears().isPrecise());
        TestCase.assertEquals(false, islamicGMT.months().isPrecise());
        TestCase.assertEquals(true, islamicGMT.weeks().isPrecise());
        TestCase.assertEquals(true, islamicGMT.days().isPrecise());
        TestCase.assertEquals(true, islamicGMT.halfdays().isPrecise());
        TestCase.assertEquals(true, islamicGMT.hours().isPrecise());
        TestCase.assertEquals(true, islamicGMT.minutes().isPrecise());
        TestCase.assertEquals(true, islamicGMT.seconds().isPrecise());
        TestCase.assertEquals(true, islamicGMT.millis().isPrecise());
    }

    public void testDateFields() {
        final IslamicChronology islamic = IslamicChronology.getInstance();
        TestCase.assertEquals("era", islamic.era().getName());
        TestCase.assertEquals("centuryOfEra", islamic.centuryOfEra().getName());
        TestCase.assertEquals("yearOfCentury", islamic.yearOfCentury().getName());
        TestCase.assertEquals("yearOfEra", islamic.yearOfEra().getName());
        TestCase.assertEquals("year", islamic.year().getName());
        TestCase.assertEquals("monthOfYear", islamic.monthOfYear().getName());
        TestCase.assertEquals("weekyearOfCentury", islamic.weekyearOfCentury().getName());
        TestCase.assertEquals("weekyear", islamic.weekyear().getName());
        TestCase.assertEquals("weekOfWeekyear", islamic.weekOfWeekyear().getName());
        TestCase.assertEquals("dayOfYear", islamic.dayOfYear().getName());
        TestCase.assertEquals("dayOfMonth", islamic.dayOfMonth().getName());
        TestCase.assertEquals("dayOfWeek", islamic.dayOfWeek().getName());
        TestCase.assertEquals(true, islamic.era().isSupported());
        TestCase.assertEquals(true, islamic.centuryOfEra().isSupported());
        TestCase.assertEquals(true, islamic.yearOfCentury().isSupported());
        TestCase.assertEquals(true, islamic.yearOfEra().isSupported());
        TestCase.assertEquals(true, islamic.year().isSupported());
        TestCase.assertEquals(true, islamic.monthOfYear().isSupported());
        TestCase.assertEquals(true, islamic.weekyearOfCentury().isSupported());
        TestCase.assertEquals(true, islamic.weekyear().isSupported());
        TestCase.assertEquals(true, islamic.weekOfWeekyear().isSupported());
        TestCase.assertEquals(true, islamic.dayOfYear().isSupported());
        TestCase.assertEquals(true, islamic.dayOfMonth().isSupported());
        TestCase.assertEquals(true, islamic.dayOfWeek().isSupported());
        TestCase.assertEquals(islamic.eras(), islamic.era().getDurationField());
        TestCase.assertEquals(islamic.centuries(), islamic.centuryOfEra().getDurationField());
        TestCase.assertEquals(islamic.years(), islamic.yearOfCentury().getDurationField());
        TestCase.assertEquals(islamic.years(), islamic.yearOfEra().getDurationField());
        TestCase.assertEquals(islamic.years(), islamic.year().getDurationField());
        TestCase.assertEquals(islamic.months(), islamic.monthOfYear().getDurationField());
        TestCase.assertEquals(islamic.weekyears(), islamic.weekyearOfCentury().getDurationField());
        TestCase.assertEquals(islamic.weekyears(), islamic.weekyear().getDurationField());
        TestCase.assertEquals(islamic.weeks(), islamic.weekOfWeekyear().getDurationField());
        TestCase.assertEquals(islamic.days(), islamic.dayOfYear().getDurationField());
        TestCase.assertEquals(islamic.days(), islamic.dayOfMonth().getDurationField());
        TestCase.assertEquals(islamic.days(), islamic.dayOfWeek().getDurationField());
        TestCase.assertEquals(null, islamic.era().getRangeDurationField());
        TestCase.assertEquals(islamic.eras(), islamic.centuryOfEra().getRangeDurationField());
        TestCase.assertEquals(islamic.centuries(), islamic.yearOfCentury().getRangeDurationField());
        TestCase.assertEquals(islamic.eras(), islamic.yearOfEra().getRangeDurationField());
        TestCase.assertEquals(null, islamic.year().getRangeDurationField());
        TestCase.assertEquals(islamic.years(), islamic.monthOfYear().getRangeDurationField());
        TestCase.assertEquals(islamic.centuries(), islamic.weekyearOfCentury().getRangeDurationField());
        TestCase.assertEquals(null, islamic.weekyear().getRangeDurationField());
        TestCase.assertEquals(islamic.weekyears(), islamic.weekOfWeekyear().getRangeDurationField());
        TestCase.assertEquals(islamic.years(), islamic.dayOfYear().getRangeDurationField());
        TestCase.assertEquals(islamic.months(), islamic.dayOfMonth().getRangeDurationField());
        TestCase.assertEquals(islamic.weeks(), islamic.dayOfWeek().getRangeDurationField());
    }

    public void testTimeFields() {
        final IslamicChronology islamic = IslamicChronology.getInstance();
        TestCase.assertEquals("halfdayOfDay", islamic.halfdayOfDay().getName());
        TestCase.assertEquals("clockhourOfHalfday", islamic.clockhourOfHalfday().getName());
        TestCase.assertEquals("hourOfHalfday", islamic.hourOfHalfday().getName());
        TestCase.assertEquals("clockhourOfDay", islamic.clockhourOfDay().getName());
        TestCase.assertEquals("hourOfDay", islamic.hourOfDay().getName());
        TestCase.assertEquals("minuteOfDay", islamic.minuteOfDay().getName());
        TestCase.assertEquals("minuteOfHour", islamic.minuteOfHour().getName());
        TestCase.assertEquals("secondOfDay", islamic.secondOfDay().getName());
        TestCase.assertEquals("secondOfMinute", islamic.secondOfMinute().getName());
        TestCase.assertEquals("millisOfDay", islamic.millisOfDay().getName());
        TestCase.assertEquals("millisOfSecond", islamic.millisOfSecond().getName());
        TestCase.assertEquals(true, islamic.halfdayOfDay().isSupported());
        TestCase.assertEquals(true, islamic.clockhourOfHalfday().isSupported());
        TestCase.assertEquals(true, islamic.hourOfHalfday().isSupported());
        TestCase.assertEquals(true, islamic.clockhourOfDay().isSupported());
        TestCase.assertEquals(true, islamic.hourOfDay().isSupported());
        TestCase.assertEquals(true, islamic.minuteOfDay().isSupported());
        TestCase.assertEquals(true, islamic.minuteOfHour().isSupported());
        TestCase.assertEquals(true, islamic.secondOfDay().isSupported());
        TestCase.assertEquals(true, islamic.secondOfMinute().isSupported());
        TestCase.assertEquals(true, islamic.millisOfDay().isSupported());
        TestCase.assertEquals(true, islamic.millisOfSecond().isSupported());
    }

    // -----------------------------------------------------------------------
    public void testEpoch() {
        DateTime epoch = new DateTime(1, 1, 1, 0, 0, 0, 0, TestIslamicChronology.ISLAMIC_UTC);
        DateTime expectedEpoch = new DateTime(622, 7, 16, 0, 0, 0, 0, TestIslamicChronology.JULIAN_UTC);
        TestCase.assertEquals(expectedEpoch.getMillis(), epoch.getMillis());
    }

    public void testEra() {
        TestCase.assertEquals(1, AH);
        try {
            new DateTime((-1), 13, 5, 0, 0, 0, 0, TestIslamicChronology.ISLAMIC_UTC);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testFieldConstructor() {
        DateTime date = new DateTime(1364, 12, 6, 0, 0, 0, 0, TestIslamicChronology.ISLAMIC_UTC);
        DateTime expectedDate = new DateTime(1945, 11, 12, 0, 0, 0, 0, TestIslamicChronology.ISO_UTC);
        TestCase.assertEquals(expectedDate.getMillis(), date.getMillis());
    }

    // -----------------------------------------------------------------------
    /**
     * Tests era, year, monthOfYear, dayOfMonth and dayOfWeek.
     */
    public void testCalendar() {
        if (TestAll.FAST) {
            return;
        }
        System.out.println("\nTestIslamicChronology.testCalendar");
        DateTime epoch = new DateTime(1, 1, 1, 0, 0, 0, 0, TestIslamicChronology.ISLAMIC_UTC);
        long millis = epoch.getMillis();
        long end = getMillis();
        DateTimeField dayOfWeek = TestIslamicChronology.ISLAMIC_UTC.dayOfWeek();
        DateTimeField dayOfYear = TestIslamicChronology.ISLAMIC_UTC.dayOfYear();
        DateTimeField dayOfMonth = TestIslamicChronology.ISLAMIC_UTC.dayOfMonth();
        DateTimeField monthOfYear = TestIslamicChronology.ISLAMIC_UTC.monthOfYear();
        DateTimeField year = TestIslamicChronology.ISLAMIC_UTC.year();
        DateTimeField yearOfEra = TestIslamicChronology.ISLAMIC_UTC.yearOfEra();
        DateTimeField era = TestIslamicChronology.ISLAMIC_UTC.era();
        int expectedDOW = getDayOfWeek();
        int expectedDOY = 1;
        int expectedDay = 1;
        int expectedMonth = 1;
        int expectedYear = 1;
        while (millis < end) {
            int dowValue = dayOfWeek.get(millis);
            int doyValue = dayOfYear.get(millis);
            int dayValue = dayOfMonth.get(millis);
            int monthValue = monthOfYear.get(millis);
            int yearValue = year.get(millis);
            int yearOfEraValue = yearOfEra.get(millis);
            int dayOfYearLen = dayOfYear.getMaximumValue(millis);
            int monthLen = dayOfMonth.getMaximumValue(millis);
            if ((monthValue < 1) || (monthValue > 12)) {
                TestCase.fail(("Bad month: " + millis));
            }
            // test era
            TestCase.assertEquals(1, era.get(millis));
            TestCase.assertEquals("AH", era.getAsText(millis));
            TestCase.assertEquals("AH", era.getAsShortText(millis));
            // test date
            TestCase.assertEquals(expectedDOY, doyValue);
            TestCase.assertEquals(expectedMonth, monthValue);
            TestCase.assertEquals(expectedDay, dayValue);
            TestCase.assertEquals(expectedDOW, dowValue);
            TestCase.assertEquals(expectedYear, yearValue);
            TestCase.assertEquals(expectedYear, yearOfEraValue);
            // test leap year
            boolean leap = (((11 * yearValue) + 14) % 30) < 11;
            TestCase.assertEquals(leap, year.isLeap(millis));
            // test month length
            switch (monthValue) {
                case 1 :
                case 3 :
                case 5 :
                case 7 :
                case 9 :
                case 11 :
                    TestCase.assertEquals(30, monthLen);
                    break;
                case 2 :
                case 4 :
                case 6 :
                case 8 :
                case 10 :
                    TestCase.assertEquals(29, monthLen);
                    break;
                case 12 :
                    TestCase.assertEquals((leap ? 30 : 29), monthLen);
                    break;
            }
            // test year length
            TestCase.assertEquals((leap ? 355 : 354), dayOfYearLen);
            // recalculate date
            expectedDOW = (((expectedDOW + 1) - 1) % 7) + 1;
            expectedDay++;
            expectedDOY++;
            if (expectedDay > monthLen) {
                expectedDay = 1;
                expectedMonth++;
                if (expectedMonth == 13) {
                    expectedMonth = 1;
                    expectedDOY = 1;
                    expectedYear++;
                }
            }
            millis += TestIslamicChronology.SKIP;
        } 
    }

    public void testSampleDate1() {
        DateTime dt = new DateTime(1945, 11, 12, 0, 0, 0, 0, TestIslamicChronology.ISO_UTC);
        dt = dt.withChronology(TestIslamicChronology.ISLAMIC_UTC);
        TestCase.assertEquals(AH, dt.getEra());
        TestCase.assertEquals(14, dt.getCenturyOfEra());// TODO confirm

        TestCase.assertEquals(64, dt.getYearOfCentury());
        TestCase.assertEquals(1364, dt.getYearOfEra());
        TestCase.assertEquals(1364, dt.getYear());
        Property fld = dt.year();
        TestCase.assertEquals(false, fld.isLeap());
        TestCase.assertEquals(0, fld.getLeapAmount());
        TestCase.assertEquals(DurationFieldType.days(), fld.getLeapDurationField().getType());
        TestCase.assertEquals(new DateTime(1365, 12, 6, 0, 0, 0, 0, TestIslamicChronology.ISLAMIC_UTC), fld.addToCopy(1));
        TestCase.assertEquals(12, dt.getMonthOfYear());
        fld = dt.monthOfYear();
        TestCase.assertEquals(false, fld.isLeap());
        TestCase.assertEquals(0, fld.getLeapAmount());
        TestCase.assertEquals(DurationFieldType.days(), fld.getLeapDurationField().getType());
        TestCase.assertEquals(1, fld.getMinimumValue());
        TestCase.assertEquals(1, fld.getMinimumValueOverall());
        TestCase.assertEquals(12, fld.getMaximumValue());
        TestCase.assertEquals(12, fld.getMaximumValueOverall());
        TestCase.assertEquals(new DateTime(1365, 1, 6, 0, 0, 0, 0, TestIslamicChronology.ISLAMIC_UTC), fld.addToCopy(1));
        TestCase.assertEquals(new DateTime(1364, 1, 6, 0, 0, 0, 0, TestIslamicChronology.ISLAMIC_UTC), fld.addWrapFieldToCopy(1));
        TestCase.assertEquals(6, dt.getDayOfMonth());
        fld = dt.dayOfMonth();
        TestCase.assertEquals(false, fld.isLeap());
        TestCase.assertEquals(0, fld.getLeapAmount());
        TestCase.assertEquals(null, fld.getLeapDurationField());
        TestCase.assertEquals(1, fld.getMinimumValue());
        TestCase.assertEquals(1, fld.getMinimumValueOverall());
        TestCase.assertEquals(29, fld.getMaximumValue());
        TestCase.assertEquals(30, fld.getMaximumValueOverall());
        TestCase.assertEquals(new DateTime(1364, 12, 7, 0, 0, 0, 0, TestIslamicChronology.ISLAMIC_UTC), fld.addToCopy(1));
        TestCase.assertEquals(MONDAY, dt.getDayOfWeek());
        fld = dt.dayOfWeek();
        TestCase.assertEquals(false, fld.isLeap());
        TestCase.assertEquals(0, fld.getLeapAmount());
        TestCase.assertEquals(null, fld.getLeapDurationField());
        TestCase.assertEquals(1, fld.getMinimumValue());
        TestCase.assertEquals(1, fld.getMinimumValueOverall());
        TestCase.assertEquals(7, fld.getMaximumValue());
        TestCase.assertEquals(7, fld.getMaximumValueOverall());
        TestCase.assertEquals(new DateTime(1364, 12, 7, 0, 0, 0, 0, TestIslamicChronology.ISLAMIC_UTC), fld.addToCopy(1));
        TestCase.assertEquals((((6 * 30) + (5 * 29)) + 6), dt.getDayOfYear());
        fld = dt.dayOfYear();
        TestCase.assertEquals(false, fld.isLeap());
        TestCase.assertEquals(0, fld.getLeapAmount());
        TestCase.assertEquals(null, fld.getLeapDurationField());
        TestCase.assertEquals(1, fld.getMinimumValue());
        TestCase.assertEquals(1, fld.getMinimumValueOverall());
        TestCase.assertEquals(354, fld.getMaximumValue());
        TestCase.assertEquals(355, fld.getMaximumValueOverall());
        TestCase.assertEquals(new DateTime(1364, 12, 7, 0, 0, 0, 0, TestIslamicChronology.ISLAMIC_UTC), fld.addToCopy(1));
        TestCase.assertEquals(0, dt.getHourOfDay());
        TestCase.assertEquals(0, dt.getMinuteOfHour());
        TestCase.assertEquals(0, dt.getSecondOfMinute());
        TestCase.assertEquals(0, dt.getMillisOfSecond());
    }

    public void testSampleDate2() {
        DateTime dt = new DateTime(2005, 11, 26, 0, 0, 0, 0, TestIslamicChronology.ISO_UTC);
        dt = dt.withChronology(TestIslamicChronology.ISLAMIC_UTC);
        TestCase.assertEquals(AH, dt.getEra());
        TestCase.assertEquals(15, dt.getCenturyOfEra());// TODO confirm

        TestCase.assertEquals(26, dt.getYearOfCentury());
        TestCase.assertEquals(1426, dt.getYearOfEra());
        TestCase.assertEquals(1426, dt.getYear());
        Property fld = dt.year();
        TestCase.assertEquals(true, fld.isLeap());
        TestCase.assertEquals(1, fld.getLeapAmount());
        TestCase.assertEquals(DurationFieldType.days(), fld.getLeapDurationField().getType());
        TestCase.assertEquals(10, dt.getMonthOfYear());
        fld = dt.monthOfYear();
        TestCase.assertEquals(false, fld.isLeap());
        TestCase.assertEquals(0, fld.getLeapAmount());
        TestCase.assertEquals(DurationFieldType.days(), fld.getLeapDurationField().getType());
        TestCase.assertEquals(1, fld.getMinimumValue());
        TestCase.assertEquals(1, fld.getMinimumValueOverall());
        TestCase.assertEquals(12, fld.getMaximumValue());
        TestCase.assertEquals(12, fld.getMaximumValueOverall());
        TestCase.assertEquals(24, dt.getDayOfMonth());
        fld = dt.dayOfMonth();
        TestCase.assertEquals(false, fld.isLeap());
        TestCase.assertEquals(0, fld.getLeapAmount());
        TestCase.assertEquals(null, fld.getLeapDurationField());
        TestCase.assertEquals(1, fld.getMinimumValue());
        TestCase.assertEquals(1, fld.getMinimumValueOverall());
        TestCase.assertEquals(29, fld.getMaximumValue());
        TestCase.assertEquals(30, fld.getMaximumValueOverall());
        TestCase.assertEquals(SATURDAY, dt.getDayOfWeek());
        fld = dt.dayOfWeek();
        TestCase.assertEquals(false, fld.isLeap());
        TestCase.assertEquals(0, fld.getLeapAmount());
        TestCase.assertEquals(null, fld.getLeapDurationField());
        TestCase.assertEquals(1, fld.getMinimumValue());
        TestCase.assertEquals(1, fld.getMinimumValueOverall());
        TestCase.assertEquals(7, fld.getMaximumValue());
        TestCase.assertEquals(7, fld.getMaximumValueOverall());
        TestCase.assertEquals((((5 * 30) + (4 * 29)) + 24), dt.getDayOfYear());
        fld = dt.dayOfYear();
        TestCase.assertEquals(false, fld.isLeap());
        TestCase.assertEquals(0, fld.getLeapAmount());
        TestCase.assertEquals(null, fld.getLeapDurationField());
        TestCase.assertEquals(1, fld.getMinimumValue());
        TestCase.assertEquals(1, fld.getMinimumValueOverall());
        TestCase.assertEquals(355, fld.getMaximumValue());
        TestCase.assertEquals(355, fld.getMaximumValueOverall());
        TestCase.assertEquals(0, dt.getHourOfDay());
        TestCase.assertEquals(0, dt.getMinuteOfHour());
        TestCase.assertEquals(0, dt.getSecondOfMinute());
        TestCase.assertEquals(0, dt.getMillisOfSecond());
    }

    public void testSampleDate3() {
        DateTime dt = new DateTime(1426, 12, 24, 0, 0, 0, 0, TestIslamicChronology.ISLAMIC_UTC);
        TestCase.assertEquals(AH, dt.getEra());
        TestCase.assertEquals(1426, dt.getYear());
        Property fld = dt.year();
        TestCase.assertEquals(true, fld.isLeap());
        TestCase.assertEquals(1, fld.getLeapAmount());
        TestCase.assertEquals(DurationFieldType.days(), fld.getLeapDurationField().getType());
        TestCase.assertEquals(12, dt.getMonthOfYear());
        fld = dt.monthOfYear();
        TestCase.assertEquals(true, fld.isLeap());
        TestCase.assertEquals(1, fld.getLeapAmount());
        TestCase.assertEquals(DurationFieldType.days(), fld.getLeapDurationField().getType());
        TestCase.assertEquals(1, fld.getMinimumValue());
        TestCase.assertEquals(1, fld.getMinimumValueOverall());
        TestCase.assertEquals(12, fld.getMaximumValue());
        TestCase.assertEquals(12, fld.getMaximumValueOverall());
        TestCase.assertEquals(24, dt.getDayOfMonth());
        fld = dt.dayOfMonth();
        TestCase.assertEquals(false, fld.isLeap());
        TestCase.assertEquals(0, fld.getLeapAmount());
        TestCase.assertEquals(null, fld.getLeapDurationField());
        TestCase.assertEquals(1, fld.getMinimumValue());
        TestCase.assertEquals(1, fld.getMinimumValueOverall());
        TestCase.assertEquals(30, fld.getMaximumValue());
        TestCase.assertEquals(30, fld.getMaximumValueOverall());
        TestCase.assertEquals(TUESDAY, dt.getDayOfWeek());
        fld = dt.dayOfWeek();
        TestCase.assertEquals(false, fld.isLeap());
        TestCase.assertEquals(0, fld.getLeapAmount());
        TestCase.assertEquals(null, fld.getLeapDurationField());
        TestCase.assertEquals(1, fld.getMinimumValue());
        TestCase.assertEquals(1, fld.getMinimumValueOverall());
        TestCase.assertEquals(7, fld.getMaximumValue());
        TestCase.assertEquals(7, fld.getMaximumValueOverall());
        TestCase.assertEquals((((6 * 30) + (5 * 29)) + 24), dt.getDayOfYear());
        fld = dt.dayOfYear();
        TestCase.assertEquals(false, fld.isLeap());
        TestCase.assertEquals(0, fld.getLeapAmount());
        TestCase.assertEquals(null, fld.getLeapDurationField());
        TestCase.assertEquals(1, fld.getMinimumValue());
        TestCase.assertEquals(1, fld.getMinimumValueOverall());
        TestCase.assertEquals(355, fld.getMaximumValue());
        TestCase.assertEquals(355, fld.getMaximumValueOverall());
        TestCase.assertEquals(0, dt.getHourOfDay());
        TestCase.assertEquals(0, dt.getMinuteOfHour());
        TestCase.assertEquals(0, dt.getSecondOfMinute());
        TestCase.assertEquals(0, dt.getMillisOfSecond());
    }

    public void testSampleDateWithZone() {
        DateTime dt = new DateTime(2005, 11, 26, 12, 0, 0, 0, TestIslamicChronology.PARIS).withChronology(TestIslamicChronology.ISLAMIC_UTC);
        TestCase.assertEquals(AH, dt.getEra());
        TestCase.assertEquals(1426, dt.getYear());
        TestCase.assertEquals(10, dt.getMonthOfYear());
        TestCase.assertEquals(24, dt.getDayOfMonth());
        TestCase.assertEquals(11, dt.getHourOfDay());// PARIS is UTC+1 in summer (12-1=11)

        TestCase.assertEquals(0, dt.getMinuteOfHour());
        TestCase.assertEquals(0, dt.getSecondOfMinute());
        TestCase.assertEquals(0, dt.getMillisOfSecond());
    }

    public void test15BasedLeapYear() {
        TestCase.assertEquals(false, LEAP_YEAR_15_BASED.isLeapYear(1));
        TestCase.assertEquals(true, LEAP_YEAR_15_BASED.isLeapYear(2));
        TestCase.assertEquals(false, LEAP_YEAR_15_BASED.isLeapYear(3));
        TestCase.assertEquals(false, LEAP_YEAR_15_BASED.isLeapYear(4));
        TestCase.assertEquals(true, LEAP_YEAR_15_BASED.isLeapYear(5));
        TestCase.assertEquals(false, LEAP_YEAR_15_BASED.isLeapYear(6));
        TestCase.assertEquals(true, LEAP_YEAR_15_BASED.isLeapYear(7));
        TestCase.assertEquals(false, LEAP_YEAR_15_BASED.isLeapYear(8));
        TestCase.assertEquals(false, LEAP_YEAR_15_BASED.isLeapYear(9));
        TestCase.assertEquals(true, LEAP_YEAR_15_BASED.isLeapYear(10));
        TestCase.assertEquals(false, LEAP_YEAR_15_BASED.isLeapYear(11));
        TestCase.assertEquals(false, LEAP_YEAR_15_BASED.isLeapYear(12));
        TestCase.assertEquals(true, LEAP_YEAR_15_BASED.isLeapYear(13));
        TestCase.assertEquals(false, LEAP_YEAR_15_BASED.isLeapYear(14));
        TestCase.assertEquals(true, LEAP_YEAR_15_BASED.isLeapYear(15));
        TestCase.assertEquals(false, LEAP_YEAR_15_BASED.isLeapYear(16));
        TestCase.assertEquals(false, LEAP_YEAR_15_BASED.isLeapYear(17));
        TestCase.assertEquals(true, LEAP_YEAR_15_BASED.isLeapYear(18));
        TestCase.assertEquals(false, LEAP_YEAR_15_BASED.isLeapYear(19));
        TestCase.assertEquals(false, LEAP_YEAR_15_BASED.isLeapYear(20));
        TestCase.assertEquals(true, LEAP_YEAR_15_BASED.isLeapYear(21));
        TestCase.assertEquals(false, LEAP_YEAR_15_BASED.isLeapYear(22));
        TestCase.assertEquals(false, LEAP_YEAR_15_BASED.isLeapYear(23));
        TestCase.assertEquals(true, LEAP_YEAR_15_BASED.isLeapYear(24));
        TestCase.assertEquals(false, LEAP_YEAR_15_BASED.isLeapYear(25));
        TestCase.assertEquals(true, LEAP_YEAR_15_BASED.isLeapYear(26));
        TestCase.assertEquals(false, LEAP_YEAR_15_BASED.isLeapYear(27));
        TestCase.assertEquals(false, LEAP_YEAR_15_BASED.isLeapYear(28));
        TestCase.assertEquals(true, LEAP_YEAR_15_BASED.isLeapYear(29));
        TestCase.assertEquals(false, LEAP_YEAR_15_BASED.isLeapYear(30));
    }

    public void test16BasedLeapYear() {
        TestCase.assertEquals(false, LEAP_YEAR_16_BASED.isLeapYear(1));
        TestCase.assertEquals(true, LEAP_YEAR_16_BASED.isLeapYear(2));
        TestCase.assertEquals(false, LEAP_YEAR_16_BASED.isLeapYear(3));
        TestCase.assertEquals(false, LEAP_YEAR_16_BASED.isLeapYear(4));
        TestCase.assertEquals(true, LEAP_YEAR_16_BASED.isLeapYear(5));
        TestCase.assertEquals(false, LEAP_YEAR_16_BASED.isLeapYear(6));
        TestCase.assertEquals(true, LEAP_YEAR_16_BASED.isLeapYear(7));
        TestCase.assertEquals(false, LEAP_YEAR_16_BASED.isLeapYear(8));
        TestCase.assertEquals(false, LEAP_YEAR_16_BASED.isLeapYear(9));
        TestCase.assertEquals(true, LEAP_YEAR_16_BASED.isLeapYear(10));
        TestCase.assertEquals(false, LEAP_YEAR_16_BASED.isLeapYear(11));
        TestCase.assertEquals(false, LEAP_YEAR_16_BASED.isLeapYear(12));
        TestCase.assertEquals(true, LEAP_YEAR_16_BASED.isLeapYear(13));
        TestCase.assertEquals(false, LEAP_YEAR_16_BASED.isLeapYear(14));
        TestCase.assertEquals(false, LEAP_YEAR_16_BASED.isLeapYear(15));
        TestCase.assertEquals(true, LEAP_YEAR_16_BASED.isLeapYear(16));
        TestCase.assertEquals(false, LEAP_YEAR_16_BASED.isLeapYear(17));
        TestCase.assertEquals(true, LEAP_YEAR_16_BASED.isLeapYear(18));
        TestCase.assertEquals(false, LEAP_YEAR_16_BASED.isLeapYear(19));
        TestCase.assertEquals(false, LEAP_YEAR_16_BASED.isLeapYear(20));
        TestCase.assertEquals(true, LEAP_YEAR_16_BASED.isLeapYear(21));
        TestCase.assertEquals(false, LEAP_YEAR_16_BASED.isLeapYear(22));
        TestCase.assertEquals(false, LEAP_YEAR_16_BASED.isLeapYear(23));
        TestCase.assertEquals(true, LEAP_YEAR_16_BASED.isLeapYear(24));
        TestCase.assertEquals(false, LEAP_YEAR_16_BASED.isLeapYear(25));
        TestCase.assertEquals(true, LEAP_YEAR_16_BASED.isLeapYear(26));
        TestCase.assertEquals(false, LEAP_YEAR_16_BASED.isLeapYear(27));
        TestCase.assertEquals(false, LEAP_YEAR_16_BASED.isLeapYear(28));
        TestCase.assertEquals(true, LEAP_YEAR_16_BASED.isLeapYear(29));
        TestCase.assertEquals(false, LEAP_YEAR_16_BASED.isLeapYear(30));
    }

    public void testIndianBasedLeapYear() {
        TestCase.assertEquals(false, LEAP_YEAR_INDIAN.isLeapYear(1));
        TestCase.assertEquals(true, LEAP_YEAR_INDIAN.isLeapYear(2));
        TestCase.assertEquals(false, LEAP_YEAR_INDIAN.isLeapYear(3));
        TestCase.assertEquals(false, LEAP_YEAR_INDIAN.isLeapYear(4));
        TestCase.assertEquals(true, LEAP_YEAR_INDIAN.isLeapYear(5));
        TestCase.assertEquals(false, LEAP_YEAR_INDIAN.isLeapYear(6));
        TestCase.assertEquals(false, LEAP_YEAR_INDIAN.isLeapYear(7));
        TestCase.assertEquals(true, LEAP_YEAR_INDIAN.isLeapYear(8));
        TestCase.assertEquals(false, LEAP_YEAR_INDIAN.isLeapYear(9));
        TestCase.assertEquals(true, LEAP_YEAR_INDIAN.isLeapYear(10));
        TestCase.assertEquals(false, LEAP_YEAR_INDIAN.isLeapYear(11));
        TestCase.assertEquals(false, LEAP_YEAR_INDIAN.isLeapYear(12));
        TestCase.assertEquals(true, LEAP_YEAR_INDIAN.isLeapYear(13));
        TestCase.assertEquals(false, LEAP_YEAR_INDIAN.isLeapYear(14));
        TestCase.assertEquals(false, LEAP_YEAR_INDIAN.isLeapYear(15));
        TestCase.assertEquals(true, LEAP_YEAR_INDIAN.isLeapYear(16));
        TestCase.assertEquals(false, LEAP_YEAR_INDIAN.isLeapYear(17));
        TestCase.assertEquals(false, LEAP_YEAR_INDIAN.isLeapYear(18));
        TestCase.assertEquals(true, LEAP_YEAR_INDIAN.isLeapYear(19));
        TestCase.assertEquals(false, LEAP_YEAR_INDIAN.isLeapYear(20));
        TestCase.assertEquals(true, LEAP_YEAR_INDIAN.isLeapYear(21));
        TestCase.assertEquals(false, LEAP_YEAR_INDIAN.isLeapYear(22));
        TestCase.assertEquals(false, LEAP_YEAR_INDIAN.isLeapYear(23));
        TestCase.assertEquals(true, LEAP_YEAR_INDIAN.isLeapYear(24));
        TestCase.assertEquals(false, LEAP_YEAR_INDIAN.isLeapYear(25));
        TestCase.assertEquals(false, LEAP_YEAR_INDIAN.isLeapYear(26));
        TestCase.assertEquals(true, LEAP_YEAR_INDIAN.isLeapYear(27));
        TestCase.assertEquals(false, LEAP_YEAR_INDIAN.isLeapYear(28));
        TestCase.assertEquals(true, LEAP_YEAR_INDIAN.isLeapYear(29));
        TestCase.assertEquals(false, LEAP_YEAR_INDIAN.isLeapYear(30));
    }

    public void testHabashAlHasibBasedLeapYear() {
        TestCase.assertEquals(false, LEAP_YEAR_HABASH_AL_HASIB.isLeapYear(1));
        TestCase.assertEquals(true, LEAP_YEAR_HABASH_AL_HASIB.isLeapYear(2));
        TestCase.assertEquals(false, LEAP_YEAR_HABASH_AL_HASIB.isLeapYear(3));
        TestCase.assertEquals(false, LEAP_YEAR_HABASH_AL_HASIB.isLeapYear(4));
        TestCase.assertEquals(true, LEAP_YEAR_HABASH_AL_HASIB.isLeapYear(5));
        TestCase.assertEquals(false, LEAP_YEAR_HABASH_AL_HASIB.isLeapYear(6));
        TestCase.assertEquals(false, LEAP_YEAR_HABASH_AL_HASIB.isLeapYear(7));
        TestCase.assertEquals(true, LEAP_YEAR_HABASH_AL_HASIB.isLeapYear(8));
        TestCase.assertEquals(false, LEAP_YEAR_HABASH_AL_HASIB.isLeapYear(9));
        TestCase.assertEquals(false, LEAP_YEAR_HABASH_AL_HASIB.isLeapYear(10));
        TestCase.assertEquals(true, LEAP_YEAR_HABASH_AL_HASIB.isLeapYear(11));
        TestCase.assertEquals(false, LEAP_YEAR_HABASH_AL_HASIB.isLeapYear(12));
        TestCase.assertEquals(true, LEAP_YEAR_HABASH_AL_HASIB.isLeapYear(13));
        TestCase.assertEquals(false, LEAP_YEAR_HABASH_AL_HASIB.isLeapYear(14));
        TestCase.assertEquals(false, LEAP_YEAR_HABASH_AL_HASIB.isLeapYear(15));
        TestCase.assertEquals(true, LEAP_YEAR_HABASH_AL_HASIB.isLeapYear(16));
        TestCase.assertEquals(false, LEAP_YEAR_HABASH_AL_HASIB.isLeapYear(17));
        TestCase.assertEquals(false, LEAP_YEAR_HABASH_AL_HASIB.isLeapYear(18));
        TestCase.assertEquals(true, LEAP_YEAR_HABASH_AL_HASIB.isLeapYear(19));
        TestCase.assertEquals(false, LEAP_YEAR_HABASH_AL_HASIB.isLeapYear(20));
        TestCase.assertEquals(true, LEAP_YEAR_HABASH_AL_HASIB.isLeapYear(21));
        TestCase.assertEquals(false, LEAP_YEAR_HABASH_AL_HASIB.isLeapYear(22));
        TestCase.assertEquals(false, LEAP_YEAR_HABASH_AL_HASIB.isLeapYear(23));
        TestCase.assertEquals(true, LEAP_YEAR_HABASH_AL_HASIB.isLeapYear(24));
        TestCase.assertEquals(false, LEAP_YEAR_HABASH_AL_HASIB.isLeapYear(25));
        TestCase.assertEquals(false, LEAP_YEAR_HABASH_AL_HASIB.isLeapYear(26));
        TestCase.assertEquals(true, LEAP_YEAR_HABASH_AL_HASIB.isLeapYear(27));
        TestCase.assertEquals(false, LEAP_YEAR_HABASH_AL_HASIB.isLeapYear(28));
        TestCase.assertEquals(false, LEAP_YEAR_HABASH_AL_HASIB.isLeapYear(29));
        TestCase.assertEquals(true, LEAP_YEAR_HABASH_AL_HASIB.isLeapYear(30));
    }
}

