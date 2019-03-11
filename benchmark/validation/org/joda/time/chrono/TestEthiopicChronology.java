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


import DateTimeConstants.WEDNESDAY;
import DateTimeZone.UTC;
import EthiopicChronology.EE;
import java.util.Locale;
import java.util.TimeZone;
import junit.framework.TestCase;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTime.Property;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationField;
import org.joda.time.DurationFieldType;


/**
 * This class is a Junit unit test for EthiopicChronology.
 *
 * @author Stephen Colebourne
 */
public class TestEthiopicChronology extends TestCase {
    private static final int MILLIS_PER_DAY = DateTimeConstants.MILLIS_PER_DAY;

    private static long SKIP = 1 * (TestEthiopicChronology.MILLIS_PER_DAY);

    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final DateTimeZone TOKYO = DateTimeZone.forID("Asia/Tokyo");

    private static final Chronology ETHIOPIC_UTC = EthiopicChronology.getInstanceUTC();

    private static final Chronology JULIAN_UTC = JulianChronology.getInstanceUTC();

    private static final Chronology ISO_UTC = ISOChronology.getInstanceUTC();

    long y2002days = ((((((((((((((((((((((((((((((365 + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365;

    // 2002-06-09
    private long TEST_TIME_NOW = ((((((((y2002days) + 31L) + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (TestEthiopicChronology.MILLIS_PER_DAY);

    private DateTimeZone originalDateTimeZone = null;

    private TimeZone originalTimeZone = null;

    private Locale originalLocale = null;

    public TestEthiopicChronology(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testFactoryUTC() {
        TestCase.assertEquals(UTC, EthiopicChronology.getInstanceUTC().getZone());
        TestCase.assertSame(EthiopicChronology.class, EthiopicChronology.getInstanceUTC().getClass());
    }

    public void testFactory() {
        TestCase.assertEquals(TestEthiopicChronology.LONDON, EthiopicChronology.getInstance().getZone());
        TestCase.assertSame(EthiopicChronology.class, EthiopicChronology.getInstance().getClass());
    }

    public void testFactory_Zone() {
        TestCase.assertEquals(TestEthiopicChronology.TOKYO, EthiopicChronology.getInstance(TestEthiopicChronology.TOKYO).getZone());
        TestCase.assertEquals(TestEthiopicChronology.PARIS, EthiopicChronology.getInstance(TestEthiopicChronology.PARIS).getZone());
        TestCase.assertEquals(TestEthiopicChronology.LONDON, EthiopicChronology.getInstance(null).getZone());
        TestCase.assertSame(EthiopicChronology.class, EthiopicChronology.getInstance(TestEthiopicChronology.TOKYO).getClass());
    }

    // -----------------------------------------------------------------------
    public void testEquality() {
        TestCase.assertSame(EthiopicChronology.getInstance(TestEthiopicChronology.TOKYO), EthiopicChronology.getInstance(TestEthiopicChronology.TOKYO));
        TestCase.assertSame(EthiopicChronology.getInstance(TestEthiopicChronology.LONDON), EthiopicChronology.getInstance(TestEthiopicChronology.LONDON));
        TestCase.assertSame(EthiopicChronology.getInstance(TestEthiopicChronology.PARIS), EthiopicChronology.getInstance(TestEthiopicChronology.PARIS));
        TestCase.assertSame(EthiopicChronology.getInstanceUTC(), EthiopicChronology.getInstanceUTC());
        TestCase.assertSame(EthiopicChronology.getInstance(), EthiopicChronology.getInstance(TestEthiopicChronology.LONDON));
    }

    public void testWithUTC() {
        TestCase.assertSame(EthiopicChronology.getInstanceUTC(), EthiopicChronology.getInstance(TestEthiopicChronology.LONDON).withUTC());
        TestCase.assertSame(EthiopicChronology.getInstanceUTC(), EthiopicChronology.getInstance(TestEthiopicChronology.TOKYO).withUTC());
        TestCase.assertSame(EthiopicChronology.getInstanceUTC(), EthiopicChronology.getInstanceUTC().withUTC());
        TestCase.assertSame(EthiopicChronology.getInstanceUTC(), EthiopicChronology.getInstance().withUTC());
    }

    public void testWithZone() {
        TestCase.assertSame(EthiopicChronology.getInstance(TestEthiopicChronology.TOKYO), EthiopicChronology.getInstance(TestEthiopicChronology.TOKYO).withZone(TestEthiopicChronology.TOKYO));
        TestCase.assertSame(EthiopicChronology.getInstance(TestEthiopicChronology.LONDON), EthiopicChronology.getInstance(TestEthiopicChronology.TOKYO).withZone(TestEthiopicChronology.LONDON));
        TestCase.assertSame(EthiopicChronology.getInstance(TestEthiopicChronology.PARIS), EthiopicChronology.getInstance(TestEthiopicChronology.TOKYO).withZone(TestEthiopicChronology.PARIS));
        TestCase.assertSame(EthiopicChronology.getInstance(TestEthiopicChronology.LONDON), EthiopicChronology.getInstance(TestEthiopicChronology.TOKYO).withZone(null));
        TestCase.assertSame(EthiopicChronology.getInstance(TestEthiopicChronology.PARIS), EthiopicChronology.getInstance().withZone(TestEthiopicChronology.PARIS));
        TestCase.assertSame(EthiopicChronology.getInstance(TestEthiopicChronology.PARIS), EthiopicChronology.getInstanceUTC().withZone(TestEthiopicChronology.PARIS));
    }

    public void testToString() {
        TestCase.assertEquals("EthiopicChronology[Europe/London]", EthiopicChronology.getInstance(TestEthiopicChronology.LONDON).toString());
        TestCase.assertEquals("EthiopicChronology[Asia/Tokyo]", EthiopicChronology.getInstance(TestEthiopicChronology.TOKYO).toString());
        TestCase.assertEquals("EthiopicChronology[Europe/London]", EthiopicChronology.getInstance().toString());
        TestCase.assertEquals("EthiopicChronology[UTC]", EthiopicChronology.getInstanceUTC().toString());
    }

    // -----------------------------------------------------------------------
    public void testDurationFields() {
        final EthiopicChronology ethiopic = EthiopicChronology.getInstance();
        TestCase.assertEquals("eras", ethiopic.eras().getName());
        TestCase.assertEquals("centuries", ethiopic.centuries().getName());
        TestCase.assertEquals("years", ethiopic.years().getName());
        TestCase.assertEquals("weekyears", ethiopic.weekyears().getName());
        TestCase.assertEquals("months", ethiopic.months().getName());
        TestCase.assertEquals("weeks", ethiopic.weeks().getName());
        TestCase.assertEquals("days", ethiopic.days().getName());
        TestCase.assertEquals("halfdays", ethiopic.halfdays().getName());
        TestCase.assertEquals("hours", ethiopic.hours().getName());
        TestCase.assertEquals("minutes", ethiopic.minutes().getName());
        TestCase.assertEquals("seconds", ethiopic.seconds().getName());
        TestCase.assertEquals("millis", ethiopic.millis().getName());
        TestCase.assertEquals(false, ethiopic.eras().isSupported());
        TestCase.assertEquals(true, ethiopic.centuries().isSupported());
        TestCase.assertEquals(true, ethiopic.years().isSupported());
        TestCase.assertEquals(true, ethiopic.weekyears().isSupported());
        TestCase.assertEquals(true, ethiopic.months().isSupported());
        TestCase.assertEquals(true, ethiopic.weeks().isSupported());
        TestCase.assertEquals(true, ethiopic.days().isSupported());
        TestCase.assertEquals(true, ethiopic.halfdays().isSupported());
        TestCase.assertEquals(true, ethiopic.hours().isSupported());
        TestCase.assertEquals(true, ethiopic.minutes().isSupported());
        TestCase.assertEquals(true, ethiopic.seconds().isSupported());
        TestCase.assertEquals(true, ethiopic.millis().isSupported());
        TestCase.assertEquals(false, ethiopic.centuries().isPrecise());
        TestCase.assertEquals(false, ethiopic.years().isPrecise());
        TestCase.assertEquals(false, ethiopic.weekyears().isPrecise());
        TestCase.assertEquals(false, ethiopic.months().isPrecise());
        TestCase.assertEquals(false, ethiopic.weeks().isPrecise());
        TestCase.assertEquals(false, ethiopic.days().isPrecise());
        TestCase.assertEquals(false, ethiopic.halfdays().isPrecise());
        TestCase.assertEquals(true, ethiopic.hours().isPrecise());
        TestCase.assertEquals(true, ethiopic.minutes().isPrecise());
        TestCase.assertEquals(true, ethiopic.seconds().isPrecise());
        TestCase.assertEquals(true, ethiopic.millis().isPrecise());
        final EthiopicChronology ethiopicUTC = EthiopicChronology.getInstanceUTC();
        TestCase.assertEquals(false, ethiopicUTC.centuries().isPrecise());
        TestCase.assertEquals(false, ethiopicUTC.years().isPrecise());
        TestCase.assertEquals(false, ethiopicUTC.weekyears().isPrecise());
        TestCase.assertEquals(false, ethiopicUTC.months().isPrecise());
        TestCase.assertEquals(true, ethiopicUTC.weeks().isPrecise());
        TestCase.assertEquals(true, ethiopicUTC.days().isPrecise());
        TestCase.assertEquals(true, ethiopicUTC.halfdays().isPrecise());
        TestCase.assertEquals(true, ethiopicUTC.hours().isPrecise());
        TestCase.assertEquals(true, ethiopicUTC.minutes().isPrecise());
        TestCase.assertEquals(true, ethiopicUTC.seconds().isPrecise());
        TestCase.assertEquals(true, ethiopicUTC.millis().isPrecise());
        final DateTimeZone gmt = DateTimeZone.forID("Etc/GMT");
        final EthiopicChronology ethiopicGMT = EthiopicChronology.getInstance(gmt);
        TestCase.assertEquals(false, ethiopicGMT.centuries().isPrecise());
        TestCase.assertEquals(false, ethiopicGMT.years().isPrecise());
        TestCase.assertEquals(false, ethiopicGMT.weekyears().isPrecise());
        TestCase.assertEquals(false, ethiopicGMT.months().isPrecise());
        TestCase.assertEquals(true, ethiopicGMT.weeks().isPrecise());
        TestCase.assertEquals(true, ethiopicGMT.days().isPrecise());
        TestCase.assertEquals(true, ethiopicGMT.halfdays().isPrecise());
        TestCase.assertEquals(true, ethiopicGMT.hours().isPrecise());
        TestCase.assertEquals(true, ethiopicGMT.minutes().isPrecise());
        TestCase.assertEquals(true, ethiopicGMT.seconds().isPrecise());
        TestCase.assertEquals(true, ethiopicGMT.millis().isPrecise());
    }

    public void testDateFields() {
        final EthiopicChronology ethiopic = EthiopicChronology.getInstance();
        TestCase.assertEquals("era", ethiopic.era().getName());
        TestCase.assertEquals("centuryOfEra", ethiopic.centuryOfEra().getName());
        TestCase.assertEquals("yearOfCentury", ethiopic.yearOfCentury().getName());
        TestCase.assertEquals("yearOfEra", ethiopic.yearOfEra().getName());
        TestCase.assertEquals("year", ethiopic.year().getName());
        TestCase.assertEquals("monthOfYear", ethiopic.monthOfYear().getName());
        TestCase.assertEquals("weekyearOfCentury", ethiopic.weekyearOfCentury().getName());
        TestCase.assertEquals("weekyear", ethiopic.weekyear().getName());
        TestCase.assertEquals("weekOfWeekyear", ethiopic.weekOfWeekyear().getName());
        TestCase.assertEquals("dayOfYear", ethiopic.dayOfYear().getName());
        TestCase.assertEquals("dayOfMonth", ethiopic.dayOfMonth().getName());
        TestCase.assertEquals("dayOfWeek", ethiopic.dayOfWeek().getName());
        TestCase.assertEquals(true, ethiopic.era().isSupported());
        TestCase.assertEquals(true, ethiopic.centuryOfEra().isSupported());
        TestCase.assertEquals(true, ethiopic.yearOfCentury().isSupported());
        TestCase.assertEquals(true, ethiopic.yearOfEra().isSupported());
        TestCase.assertEquals(true, ethiopic.year().isSupported());
        TestCase.assertEquals(true, ethiopic.monthOfYear().isSupported());
        TestCase.assertEquals(true, ethiopic.weekyearOfCentury().isSupported());
        TestCase.assertEquals(true, ethiopic.weekyear().isSupported());
        TestCase.assertEquals(true, ethiopic.weekOfWeekyear().isSupported());
        TestCase.assertEquals(true, ethiopic.dayOfYear().isSupported());
        TestCase.assertEquals(true, ethiopic.dayOfMonth().isSupported());
        TestCase.assertEquals(true, ethiopic.dayOfWeek().isSupported());
        TestCase.assertEquals(ethiopic.eras(), ethiopic.era().getDurationField());
        TestCase.assertEquals(ethiopic.centuries(), ethiopic.centuryOfEra().getDurationField());
        TestCase.assertEquals(ethiopic.years(), ethiopic.yearOfCentury().getDurationField());
        TestCase.assertEquals(ethiopic.years(), ethiopic.yearOfEra().getDurationField());
        TestCase.assertEquals(ethiopic.years(), ethiopic.year().getDurationField());
        TestCase.assertEquals(ethiopic.months(), ethiopic.monthOfYear().getDurationField());
        TestCase.assertEquals(ethiopic.weekyears(), ethiopic.weekyearOfCentury().getDurationField());
        TestCase.assertEquals(ethiopic.weekyears(), ethiopic.weekyear().getDurationField());
        TestCase.assertEquals(ethiopic.weeks(), ethiopic.weekOfWeekyear().getDurationField());
        TestCase.assertEquals(ethiopic.days(), ethiopic.dayOfYear().getDurationField());
        TestCase.assertEquals(ethiopic.days(), ethiopic.dayOfMonth().getDurationField());
        TestCase.assertEquals(ethiopic.days(), ethiopic.dayOfWeek().getDurationField());
        TestCase.assertEquals(null, ethiopic.era().getRangeDurationField());
        TestCase.assertEquals(ethiopic.eras(), ethiopic.centuryOfEra().getRangeDurationField());
        TestCase.assertEquals(ethiopic.centuries(), ethiopic.yearOfCentury().getRangeDurationField());
        TestCase.assertEquals(ethiopic.eras(), ethiopic.yearOfEra().getRangeDurationField());
        TestCase.assertEquals(null, ethiopic.year().getRangeDurationField());
        TestCase.assertEquals(ethiopic.years(), ethiopic.monthOfYear().getRangeDurationField());
        TestCase.assertEquals(ethiopic.centuries(), ethiopic.weekyearOfCentury().getRangeDurationField());
        TestCase.assertEquals(null, ethiopic.weekyear().getRangeDurationField());
        TestCase.assertEquals(ethiopic.weekyears(), ethiopic.weekOfWeekyear().getRangeDurationField());
        TestCase.assertEquals(ethiopic.years(), ethiopic.dayOfYear().getRangeDurationField());
        TestCase.assertEquals(ethiopic.months(), ethiopic.dayOfMonth().getRangeDurationField());
        TestCase.assertEquals(ethiopic.weeks(), ethiopic.dayOfWeek().getRangeDurationField());
    }

    public void testTimeFields() {
        final EthiopicChronology ethiopic = EthiopicChronology.getInstance();
        TestCase.assertEquals("halfdayOfDay", ethiopic.halfdayOfDay().getName());
        TestCase.assertEquals("clockhourOfHalfday", ethiopic.clockhourOfHalfday().getName());
        TestCase.assertEquals("hourOfHalfday", ethiopic.hourOfHalfday().getName());
        TestCase.assertEquals("clockhourOfDay", ethiopic.clockhourOfDay().getName());
        TestCase.assertEquals("hourOfDay", ethiopic.hourOfDay().getName());
        TestCase.assertEquals("minuteOfDay", ethiopic.minuteOfDay().getName());
        TestCase.assertEquals("minuteOfHour", ethiopic.minuteOfHour().getName());
        TestCase.assertEquals("secondOfDay", ethiopic.secondOfDay().getName());
        TestCase.assertEquals("secondOfMinute", ethiopic.secondOfMinute().getName());
        TestCase.assertEquals("millisOfDay", ethiopic.millisOfDay().getName());
        TestCase.assertEquals("millisOfSecond", ethiopic.millisOfSecond().getName());
        TestCase.assertEquals(true, ethiopic.halfdayOfDay().isSupported());
        TestCase.assertEquals(true, ethiopic.clockhourOfHalfday().isSupported());
        TestCase.assertEquals(true, ethiopic.hourOfHalfday().isSupported());
        TestCase.assertEquals(true, ethiopic.clockhourOfDay().isSupported());
        TestCase.assertEquals(true, ethiopic.hourOfDay().isSupported());
        TestCase.assertEquals(true, ethiopic.minuteOfDay().isSupported());
        TestCase.assertEquals(true, ethiopic.minuteOfHour().isSupported());
        TestCase.assertEquals(true, ethiopic.secondOfDay().isSupported());
        TestCase.assertEquals(true, ethiopic.secondOfMinute().isSupported());
        TestCase.assertEquals(true, ethiopic.millisOfDay().isSupported());
        TestCase.assertEquals(true, ethiopic.millisOfSecond().isSupported());
    }

    // -----------------------------------------------------------------------
    public void testEpoch() {
        DateTime epoch = new DateTime(1, 1, 1, 0, 0, 0, 0, TestEthiopicChronology.ETHIOPIC_UTC);
        TestCase.assertEquals(new DateTime(8, 8, 29, 0, 0, 0, 0, TestEthiopicChronology.JULIAN_UTC), epoch.withChronology(TestEthiopicChronology.JULIAN_UTC));
    }

    public void testEra() {
        TestCase.assertEquals(1, EE);
        try {
            new DateTime((-1), 13, 5, 0, 0, 0, 0, TestEthiopicChronology.ETHIOPIC_UTC);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    /**
     * Tests era, year, monthOfYear, dayOfMonth and dayOfWeek.
     */
    public void testCalendar() {
        if (TestAll.FAST) {
            return;
        }
        System.out.println("\nTestEthiopicChronology.testCalendar");
        DateTime epoch = new DateTime(1, 1, 1, 0, 0, 0, 0, TestEthiopicChronology.ETHIOPIC_UTC);
        long millis = epoch.getMillis();
        long end = getMillis();
        DateTimeField dayOfWeek = TestEthiopicChronology.ETHIOPIC_UTC.dayOfWeek();
        DateTimeField dayOfYear = TestEthiopicChronology.ETHIOPIC_UTC.dayOfYear();
        DateTimeField dayOfMonth = TestEthiopicChronology.ETHIOPIC_UTC.dayOfMonth();
        DateTimeField monthOfYear = TestEthiopicChronology.ETHIOPIC_UTC.monthOfYear();
        DateTimeField year = TestEthiopicChronology.ETHIOPIC_UTC.year();
        DateTimeField yearOfEra = TestEthiopicChronology.ETHIOPIC_UTC.yearOfEra();
        DateTimeField era = TestEthiopicChronology.ETHIOPIC_UTC.era();
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
            int monthLen = dayOfMonth.getMaximumValue(millis);
            if ((monthValue < 1) || (monthValue > 13)) {
                TestCase.fail(("Bad month: " + millis));
            }
            // test era
            TestCase.assertEquals(1, era.get(millis));
            TestCase.assertEquals("EE", era.getAsText(millis));
            TestCase.assertEquals("EE", era.getAsShortText(millis));
            // test date
            TestCase.assertEquals(expectedYear, yearValue);
            TestCase.assertEquals(expectedYear, yearOfEraValue);
            TestCase.assertEquals(expectedMonth, monthValue);
            TestCase.assertEquals(expectedDay, dayValue);
            TestCase.assertEquals(expectedDOW, dowValue);
            TestCase.assertEquals(expectedDOY, doyValue);
            // test leap year
            TestCase.assertEquals(((yearValue % 4) == 3), year.isLeap(millis));
            // test month length
            if (monthValue == 13) {
                TestCase.assertEquals(((yearValue % 4) == 3), monthOfYear.isLeap(millis));
                if ((yearValue % 4) == 3) {
                    TestCase.assertEquals(6, monthLen);
                } else {
                    TestCase.assertEquals(5, monthLen);
                }
            } else {
                TestCase.assertEquals(30, monthLen);
            }
            // recalculate date
            expectedDOW = (((expectedDOW + 1) - 1) % 7) + 1;
            expectedDay++;
            expectedDOY++;
            if ((expectedDay == 31) && (expectedMonth < 13)) {
                expectedDay = 1;
                expectedMonth++;
            } else
                if (expectedMonth == 13) {
                    if (((expectedYear % 4) == 3) && (expectedDay == 7)) {
                        expectedDay = 1;
                        expectedMonth = 1;
                        expectedYear++;
                        expectedDOY = 1;
                    } else
                        if (((expectedYear % 4) != 3) && (expectedDay == 6)) {
                            expectedDay = 1;
                            expectedMonth = 1;
                            expectedYear++;
                            expectedDOY = 1;
                        }

                }

            millis += TestEthiopicChronology.SKIP;
        } 
    }

    public void testSampleDate() {
        DateTime dt = new DateTime(2004, 6, 9, 0, 0, 0, 0, TestEthiopicChronology.ISO_UTC).withChronology(TestEthiopicChronology.ETHIOPIC_UTC);
        TestCase.assertEquals(EE, dt.getEra());
        TestCase.assertEquals(20, dt.getCenturyOfEra());// TODO confirm

        TestCase.assertEquals(96, dt.getYearOfCentury());
        TestCase.assertEquals(1996, dt.getYearOfEra());
        TestCase.assertEquals(1996, dt.getYear());
        Property fld = dt.year();
        TestCase.assertEquals(false, fld.isLeap());
        TestCase.assertEquals(0, fld.getLeapAmount());
        TestCase.assertEquals(DurationFieldType.days(), fld.getLeapDurationField().getType());
        TestCase.assertEquals(new DateTime(1997, 10, 2, 0, 0, 0, 0, TestEthiopicChronology.ETHIOPIC_UTC), fld.addToCopy(1));
        TestCase.assertEquals(10, dt.getMonthOfYear());
        fld = dt.monthOfYear();
        TestCase.assertEquals(false, fld.isLeap());
        TestCase.assertEquals(0, fld.getLeapAmount());
        TestCase.assertEquals(DurationFieldType.days(), fld.getLeapDurationField().getType());
        TestCase.assertEquals(1, fld.getMinimumValue());
        TestCase.assertEquals(1, fld.getMinimumValueOverall());
        TestCase.assertEquals(13, fld.getMaximumValue());
        TestCase.assertEquals(13, fld.getMaximumValueOverall());
        TestCase.assertEquals(new DateTime(1997, 1, 2, 0, 0, 0, 0, TestEthiopicChronology.ETHIOPIC_UTC), fld.addToCopy(4));
        TestCase.assertEquals(new DateTime(1996, 1, 2, 0, 0, 0, 0, TestEthiopicChronology.ETHIOPIC_UTC), fld.addWrapFieldToCopy(4));
        TestCase.assertEquals(2, dt.getDayOfMonth());
        fld = dt.dayOfMonth();
        TestCase.assertEquals(false, fld.isLeap());
        TestCase.assertEquals(0, fld.getLeapAmount());
        TestCase.assertEquals(null, fld.getLeapDurationField());
        TestCase.assertEquals(1, fld.getMinimumValue());
        TestCase.assertEquals(1, fld.getMinimumValueOverall());
        TestCase.assertEquals(30, fld.getMaximumValue());
        TestCase.assertEquals(30, fld.getMaximumValueOverall());
        TestCase.assertEquals(new DateTime(1996, 10, 3, 0, 0, 0, 0, TestEthiopicChronology.ETHIOPIC_UTC), fld.addToCopy(1));
        TestCase.assertEquals(WEDNESDAY, dt.getDayOfWeek());
        fld = dt.dayOfWeek();
        TestCase.assertEquals(false, fld.isLeap());
        TestCase.assertEquals(0, fld.getLeapAmount());
        TestCase.assertEquals(null, fld.getLeapDurationField());
        TestCase.assertEquals(1, fld.getMinimumValue());
        TestCase.assertEquals(1, fld.getMinimumValueOverall());
        TestCase.assertEquals(7, fld.getMaximumValue());
        TestCase.assertEquals(7, fld.getMaximumValueOverall());
        TestCase.assertEquals(new DateTime(1996, 10, 3, 0, 0, 0, 0, TestEthiopicChronology.ETHIOPIC_UTC), fld.addToCopy(1));
        TestCase.assertEquals(((9 * 30) + 2), dt.getDayOfYear());
        fld = dt.dayOfYear();
        TestCase.assertEquals(false, fld.isLeap());
        TestCase.assertEquals(0, fld.getLeapAmount());
        TestCase.assertEquals(null, fld.getLeapDurationField());
        TestCase.assertEquals(1, fld.getMinimumValue());
        TestCase.assertEquals(1, fld.getMinimumValueOverall());
        TestCase.assertEquals(365, fld.getMaximumValue());
        TestCase.assertEquals(366, fld.getMaximumValueOverall());
        TestCase.assertEquals(new DateTime(1996, 10, 3, 0, 0, 0, 0, TestEthiopicChronology.ETHIOPIC_UTC), fld.addToCopy(1));
        TestCase.assertEquals(0, dt.getHourOfDay());
        TestCase.assertEquals(0, dt.getMinuteOfHour());
        TestCase.assertEquals(0, dt.getSecondOfMinute());
        TestCase.assertEquals(0, dt.getMillisOfSecond());
    }

    public void testSampleDateWithZone() {
        DateTime dt = new DateTime(2004, 6, 9, 12, 0, 0, 0, TestEthiopicChronology.PARIS).withChronology(TestEthiopicChronology.ETHIOPIC_UTC);
        TestCase.assertEquals(EE, dt.getEra());
        TestCase.assertEquals(1996, dt.getYear());
        TestCase.assertEquals(1996, dt.getYearOfEra());
        TestCase.assertEquals(10, dt.getMonthOfYear());
        TestCase.assertEquals(2, dt.getDayOfMonth());
        TestCase.assertEquals(10, dt.getHourOfDay());// PARIS is UTC+2 in summer (12-2=10)

        TestCase.assertEquals(0, dt.getMinuteOfHour());
        TestCase.assertEquals(0, dt.getSecondOfMinute());
        TestCase.assertEquals(0, dt.getMillisOfSecond());
    }

    public void testDurationYear() {
        // Leap 1999, NotLeap 1996,97,98
        DateTime dt96 = new DateTime(1996, 10, 2, 0, 0, 0, 0, TestEthiopicChronology.ETHIOPIC_UTC);
        DateTime dt97 = new DateTime(1997, 10, 2, 0, 0, 0, 0, TestEthiopicChronology.ETHIOPIC_UTC);
        DateTime dt98 = new DateTime(1998, 10, 2, 0, 0, 0, 0, TestEthiopicChronology.ETHIOPIC_UTC);
        DateTime dt99 = new DateTime(1999, 10, 2, 0, 0, 0, 0, TestEthiopicChronology.ETHIOPIC_UTC);
        DateTime dt00 = new DateTime(2000, 10, 2, 0, 0, 0, 0, TestEthiopicChronology.ETHIOPIC_UTC);
        DurationField fld = dt96.year().getDurationField();
        TestCase.assertEquals(TestEthiopicChronology.ETHIOPIC_UTC.years(), fld);
        TestCase.assertEquals(((1L * 365L) * (TestEthiopicChronology.MILLIS_PER_DAY)), fld.getMillis(1, dt96.getMillis()));
        TestCase.assertEquals(((2L * 365L) * (TestEthiopicChronology.MILLIS_PER_DAY)), fld.getMillis(2, dt96.getMillis()));
        TestCase.assertEquals(((3L * 365L) * (TestEthiopicChronology.MILLIS_PER_DAY)), fld.getMillis(3, dt96.getMillis()));
        TestCase.assertEquals((((4L * 365L) + 1L) * (TestEthiopicChronology.MILLIS_PER_DAY)), fld.getMillis(4, dt96.getMillis()));
        TestCase.assertEquals(((((4L * 365L) + 1L) * (TestEthiopicChronology.MILLIS_PER_DAY)) / 4), fld.getMillis(1));
        TestCase.assertEquals(((((4L * 365L) + 1L) * (TestEthiopicChronology.MILLIS_PER_DAY)) / 2), fld.getMillis(2));
        TestCase.assertEquals(((1L * 365L) * (TestEthiopicChronology.MILLIS_PER_DAY)), fld.getMillis(1L, dt96.getMillis()));
        TestCase.assertEquals(((2L * 365L) * (TestEthiopicChronology.MILLIS_PER_DAY)), fld.getMillis(2L, dt96.getMillis()));
        TestCase.assertEquals(((3L * 365L) * (TestEthiopicChronology.MILLIS_PER_DAY)), fld.getMillis(3L, dt96.getMillis()));
        TestCase.assertEquals((((4L * 365L) + 1L) * (TestEthiopicChronology.MILLIS_PER_DAY)), fld.getMillis(4L, dt96.getMillis()));
        TestCase.assertEquals(((((4L * 365L) + 1L) * (TestEthiopicChronology.MILLIS_PER_DAY)) / 4), fld.getMillis(1L));
        TestCase.assertEquals(((((4L * 365L) + 1L) * (TestEthiopicChronology.MILLIS_PER_DAY)) / 2), fld.getMillis(2L));
        TestCase.assertEquals(((((4L * 365L) + 1L) * (TestEthiopicChronology.MILLIS_PER_DAY)) / 4), fld.getUnitMillis());
        TestCase.assertEquals(0, fld.getValue((((1L * 365L) * (TestEthiopicChronology.MILLIS_PER_DAY)) - 1L), dt96.getMillis()));
        TestCase.assertEquals(1, fld.getValue(((1L * 365L) * (TestEthiopicChronology.MILLIS_PER_DAY)), dt96.getMillis()));
        TestCase.assertEquals(1, fld.getValue((((1L * 365L) * (TestEthiopicChronology.MILLIS_PER_DAY)) + 1L), dt96.getMillis()));
        TestCase.assertEquals(1, fld.getValue((((2L * 365L) * (TestEthiopicChronology.MILLIS_PER_DAY)) - 1L), dt96.getMillis()));
        TestCase.assertEquals(2, fld.getValue(((2L * 365L) * (TestEthiopicChronology.MILLIS_PER_DAY)), dt96.getMillis()));
        TestCase.assertEquals(2, fld.getValue((((2L * 365L) * (TestEthiopicChronology.MILLIS_PER_DAY)) + 1L), dt96.getMillis()));
        TestCase.assertEquals(2, fld.getValue((((3L * 365L) * (TestEthiopicChronology.MILLIS_PER_DAY)) - 1L), dt96.getMillis()));
        TestCase.assertEquals(3, fld.getValue(((3L * 365L) * (TestEthiopicChronology.MILLIS_PER_DAY)), dt96.getMillis()));
        TestCase.assertEquals(3, fld.getValue((((3L * 365L) * (TestEthiopicChronology.MILLIS_PER_DAY)) + 1L), dt96.getMillis()));
        TestCase.assertEquals(3, fld.getValue(((((4L * 365L) + 1L) * (TestEthiopicChronology.MILLIS_PER_DAY)) - 1L), dt96.getMillis()));
        TestCase.assertEquals(4, fld.getValue((((4L * 365L) + 1L) * (TestEthiopicChronology.MILLIS_PER_DAY)), dt96.getMillis()));
        TestCase.assertEquals(4, fld.getValue(((((4L * 365L) + 1L) * (TestEthiopicChronology.MILLIS_PER_DAY)) + 1L), dt96.getMillis()));
        TestCase.assertEquals(dt97.getMillis(), fld.add(dt96.getMillis(), 1));
        TestCase.assertEquals(dt98.getMillis(), fld.add(dt96.getMillis(), 2));
        TestCase.assertEquals(dt99.getMillis(), fld.add(dt96.getMillis(), 3));
        TestCase.assertEquals(dt00.getMillis(), fld.add(dt96.getMillis(), 4));
        TestCase.assertEquals(dt97.getMillis(), fld.add(dt96.getMillis(), 1L));
        TestCase.assertEquals(dt98.getMillis(), fld.add(dt96.getMillis(), 2L));
        TestCase.assertEquals(dt99.getMillis(), fld.add(dt96.getMillis(), 3L));
        TestCase.assertEquals(dt00.getMillis(), fld.add(dt96.getMillis(), 4L));
    }

    public void testDurationMonth() {
        // Leap 1999, NotLeap 1996,97,98
        DateTime dt11 = new DateTime(1999, 11, 2, 0, 0, 0, 0, TestEthiopicChronology.ETHIOPIC_UTC);
        DateTime dt12 = new DateTime(1999, 12, 2, 0, 0, 0, 0, TestEthiopicChronology.ETHIOPIC_UTC);
        DateTime dt13 = new DateTime(1999, 13, 2, 0, 0, 0, 0, TestEthiopicChronology.ETHIOPIC_UTC);
        DateTime dt01 = new DateTime(2000, 1, 2, 0, 0, 0, 0, TestEthiopicChronology.ETHIOPIC_UTC);
        DurationField fld = dt11.monthOfYear().getDurationField();
        TestCase.assertEquals(TestEthiopicChronology.ETHIOPIC_UTC.months(), fld);
        TestCase.assertEquals(((1L * 30L) * (TestEthiopicChronology.MILLIS_PER_DAY)), fld.getMillis(1, dt11.getMillis()));
        TestCase.assertEquals(((2L * 30L) * (TestEthiopicChronology.MILLIS_PER_DAY)), fld.getMillis(2, dt11.getMillis()));
        TestCase.assertEquals((((2L * 30L) + 6L) * (TestEthiopicChronology.MILLIS_PER_DAY)), fld.getMillis(3, dt11.getMillis()));
        TestCase.assertEquals((((3L * 30L) + 6L) * (TestEthiopicChronology.MILLIS_PER_DAY)), fld.getMillis(4, dt11.getMillis()));
        TestCase.assertEquals(((1L * 30L) * (TestEthiopicChronology.MILLIS_PER_DAY)), fld.getMillis(1));
        TestCase.assertEquals(((2L * 30L) * (TestEthiopicChronology.MILLIS_PER_DAY)), fld.getMillis(2));
        TestCase.assertEquals(((13L * 30L) * (TestEthiopicChronology.MILLIS_PER_DAY)), fld.getMillis(13));
        TestCase.assertEquals(((1L * 30L) * (TestEthiopicChronology.MILLIS_PER_DAY)), fld.getMillis(1L, dt11.getMillis()));
        TestCase.assertEquals(((2L * 30L) * (TestEthiopicChronology.MILLIS_PER_DAY)), fld.getMillis(2L, dt11.getMillis()));
        TestCase.assertEquals((((2L * 30L) + 6L) * (TestEthiopicChronology.MILLIS_PER_DAY)), fld.getMillis(3L, dt11.getMillis()));
        TestCase.assertEquals((((3L * 30L) + 6L) * (TestEthiopicChronology.MILLIS_PER_DAY)), fld.getMillis(4L, dt11.getMillis()));
        TestCase.assertEquals(((1L * 30L) * (TestEthiopicChronology.MILLIS_PER_DAY)), fld.getMillis(1L));
        TestCase.assertEquals(((2L * 30L) * (TestEthiopicChronology.MILLIS_PER_DAY)), fld.getMillis(2L));
        TestCase.assertEquals(((13L * 30L) * (TestEthiopicChronology.MILLIS_PER_DAY)), fld.getMillis(13L));
        TestCase.assertEquals(0, fld.getValue((((1L * 30L) * (TestEthiopicChronology.MILLIS_PER_DAY)) - 1L), dt11.getMillis()));
        TestCase.assertEquals(1, fld.getValue(((1L * 30L) * (TestEthiopicChronology.MILLIS_PER_DAY)), dt11.getMillis()));
        TestCase.assertEquals(1, fld.getValue((((1L * 30L) * (TestEthiopicChronology.MILLIS_PER_DAY)) + 1L), dt11.getMillis()));
        TestCase.assertEquals(1, fld.getValue((((2L * 30L) * (TestEthiopicChronology.MILLIS_PER_DAY)) - 1L), dt11.getMillis()));
        TestCase.assertEquals(2, fld.getValue(((2L * 30L) * (TestEthiopicChronology.MILLIS_PER_DAY)), dt11.getMillis()));
        TestCase.assertEquals(2, fld.getValue((((2L * 30L) * (TestEthiopicChronology.MILLIS_PER_DAY)) + 1L), dt11.getMillis()));
        TestCase.assertEquals(2, fld.getValue(((((2L * 30L) + 6L) * (TestEthiopicChronology.MILLIS_PER_DAY)) - 1L), dt11.getMillis()));
        TestCase.assertEquals(3, fld.getValue((((2L * 30L) + 6L) * (TestEthiopicChronology.MILLIS_PER_DAY)), dt11.getMillis()));
        TestCase.assertEquals(3, fld.getValue(((((2L * 30L) + 6L) * (TestEthiopicChronology.MILLIS_PER_DAY)) + 1L), dt11.getMillis()));
        TestCase.assertEquals(3, fld.getValue(((((3L * 30L) + 6L) * (TestEthiopicChronology.MILLIS_PER_DAY)) - 1L), dt11.getMillis()));
        TestCase.assertEquals(4, fld.getValue((((3L * 30L) + 6L) * (TestEthiopicChronology.MILLIS_PER_DAY)), dt11.getMillis()));
        TestCase.assertEquals(4, fld.getValue(((((3L * 30L) + 6L) * (TestEthiopicChronology.MILLIS_PER_DAY)) + 1L), dt11.getMillis()));
        TestCase.assertEquals(dt12.getMillis(), fld.add(dt11.getMillis(), 1));
        TestCase.assertEquals(dt13.getMillis(), fld.add(dt11.getMillis(), 2));
        TestCase.assertEquals(dt01.getMillis(), fld.add(dt11.getMillis(), 3));
        TestCase.assertEquals(dt12.getMillis(), fld.add(dt11.getMillis(), 1L));
        TestCase.assertEquals(dt13.getMillis(), fld.add(dt11.getMillis(), 2L));
        TestCase.assertEquals(dt01.getMillis(), fld.add(dt11.getMillis(), 3L));
    }

    public void testLeap_5_13() {
        Chronology chrono = EthiopicChronology.getInstance();
        DateTime dt = new DateTime(3, 13, 5, 0, 0, chrono);
        TestCase.assertEquals(true, dt.year().isLeap());
        TestCase.assertEquals(true, dt.monthOfYear().isLeap());
        TestCase.assertEquals(false, dt.dayOfMonth().isLeap());
        TestCase.assertEquals(false, dt.dayOfYear().isLeap());
    }

    public void testLeap_6_13() {
        Chronology chrono = EthiopicChronology.getInstance();
        DateTime dt = new DateTime(3, 13, 6, 0, 0, chrono);
        TestCase.assertEquals(true, dt.year().isLeap());
        TestCase.assertEquals(true, dt.monthOfYear().isLeap());
        TestCase.assertEquals(true, dt.dayOfMonth().isLeap());
        TestCase.assertEquals(true, dt.dayOfYear().isLeap());
    }
}

