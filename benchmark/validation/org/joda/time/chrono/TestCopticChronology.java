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


import CopticChronology.AM;
import DateTimeConstants.WEDNESDAY;
import DateTimeZone.UTC;
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
 * This class is a Junit unit test for CopticChronology.
 *
 * @author Stephen Colebourne
 */
public class TestCopticChronology extends TestCase {
    private static final int MILLIS_PER_DAY = DateTimeConstants.MILLIS_PER_DAY;

    private static long SKIP = 1 * (TestCopticChronology.MILLIS_PER_DAY);

    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final DateTimeZone TOKYO = DateTimeZone.forID("Asia/Tokyo");

    private static final Chronology COPTIC_UTC = CopticChronology.getInstanceUTC();

    private static final Chronology JULIAN_UTC = JulianChronology.getInstanceUTC();

    private static final Chronology ISO_UTC = ISOChronology.getInstanceUTC();

    long y2002days = ((((((((((((((((((((((((((((((365 + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365;

    // 2002-06-09
    private long TEST_TIME_NOW = ((((((((y2002days) + 31L) + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (TestCopticChronology.MILLIS_PER_DAY);

    private DateTimeZone originalDateTimeZone = null;

    private TimeZone originalTimeZone = null;

    private Locale originalLocale = null;

    public TestCopticChronology(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testFactoryUTC() {
        TestCase.assertEquals(UTC, CopticChronology.getInstanceUTC().getZone());
        TestCase.assertSame(CopticChronology.class, CopticChronology.getInstanceUTC().getClass());
    }

    public void testFactory() {
        TestCase.assertEquals(TestCopticChronology.LONDON, CopticChronology.getInstance().getZone());
        TestCase.assertSame(CopticChronology.class, CopticChronology.getInstance().getClass());
    }

    public void testFactory_Zone() {
        TestCase.assertEquals(TestCopticChronology.TOKYO, CopticChronology.getInstance(TestCopticChronology.TOKYO).getZone());
        TestCase.assertEquals(TestCopticChronology.PARIS, CopticChronology.getInstance(TestCopticChronology.PARIS).getZone());
        TestCase.assertEquals(TestCopticChronology.LONDON, CopticChronology.getInstance(null).getZone());
        TestCase.assertSame(CopticChronology.class, CopticChronology.getInstance(TestCopticChronology.TOKYO).getClass());
    }

    // -----------------------------------------------------------------------
    public void testEquality() {
        TestCase.assertSame(CopticChronology.getInstance(TestCopticChronology.TOKYO), CopticChronology.getInstance(TestCopticChronology.TOKYO));
        TestCase.assertSame(CopticChronology.getInstance(TestCopticChronology.LONDON), CopticChronology.getInstance(TestCopticChronology.LONDON));
        TestCase.assertSame(CopticChronology.getInstance(TestCopticChronology.PARIS), CopticChronology.getInstance(TestCopticChronology.PARIS));
        TestCase.assertSame(CopticChronology.getInstanceUTC(), CopticChronology.getInstanceUTC());
        TestCase.assertSame(CopticChronology.getInstance(), CopticChronology.getInstance(TestCopticChronology.LONDON));
    }

    public void testWithUTC() {
        TestCase.assertSame(CopticChronology.getInstanceUTC(), CopticChronology.getInstance(TestCopticChronology.LONDON).withUTC());
        TestCase.assertSame(CopticChronology.getInstanceUTC(), CopticChronology.getInstance(TestCopticChronology.TOKYO).withUTC());
        TestCase.assertSame(CopticChronology.getInstanceUTC(), CopticChronology.getInstanceUTC().withUTC());
        TestCase.assertSame(CopticChronology.getInstanceUTC(), CopticChronology.getInstance().withUTC());
    }

    public void testWithZone() {
        TestCase.assertSame(CopticChronology.getInstance(TestCopticChronology.TOKYO), CopticChronology.getInstance(TestCopticChronology.TOKYO).withZone(TestCopticChronology.TOKYO));
        TestCase.assertSame(CopticChronology.getInstance(TestCopticChronology.LONDON), CopticChronology.getInstance(TestCopticChronology.TOKYO).withZone(TestCopticChronology.LONDON));
        TestCase.assertSame(CopticChronology.getInstance(TestCopticChronology.PARIS), CopticChronology.getInstance(TestCopticChronology.TOKYO).withZone(TestCopticChronology.PARIS));
        TestCase.assertSame(CopticChronology.getInstance(TestCopticChronology.LONDON), CopticChronology.getInstance(TestCopticChronology.TOKYO).withZone(null));
        TestCase.assertSame(CopticChronology.getInstance(TestCopticChronology.PARIS), CopticChronology.getInstance().withZone(TestCopticChronology.PARIS));
        TestCase.assertSame(CopticChronology.getInstance(TestCopticChronology.PARIS), CopticChronology.getInstanceUTC().withZone(TestCopticChronology.PARIS));
    }

    public void testToString() {
        TestCase.assertEquals("CopticChronology[Europe/London]", CopticChronology.getInstance(TestCopticChronology.LONDON).toString());
        TestCase.assertEquals("CopticChronology[Asia/Tokyo]", CopticChronology.getInstance(TestCopticChronology.TOKYO).toString());
        TestCase.assertEquals("CopticChronology[Europe/London]", CopticChronology.getInstance().toString());
        TestCase.assertEquals("CopticChronology[UTC]", CopticChronology.getInstanceUTC().toString());
    }

    // -----------------------------------------------------------------------
    public void testDurationFields() {
        final CopticChronology coptic = CopticChronology.getInstance();
        TestCase.assertEquals("eras", coptic.eras().getName());
        TestCase.assertEquals("centuries", coptic.centuries().getName());
        TestCase.assertEquals("years", coptic.years().getName());
        TestCase.assertEquals("weekyears", coptic.weekyears().getName());
        TestCase.assertEquals("months", coptic.months().getName());
        TestCase.assertEquals("weeks", coptic.weeks().getName());
        TestCase.assertEquals("days", coptic.days().getName());
        TestCase.assertEquals("halfdays", coptic.halfdays().getName());
        TestCase.assertEquals("hours", coptic.hours().getName());
        TestCase.assertEquals("minutes", coptic.minutes().getName());
        TestCase.assertEquals("seconds", coptic.seconds().getName());
        TestCase.assertEquals("millis", coptic.millis().getName());
        TestCase.assertEquals(false, coptic.eras().isSupported());
        TestCase.assertEquals(true, coptic.centuries().isSupported());
        TestCase.assertEquals(true, coptic.years().isSupported());
        TestCase.assertEquals(true, coptic.weekyears().isSupported());
        TestCase.assertEquals(true, coptic.months().isSupported());
        TestCase.assertEquals(true, coptic.weeks().isSupported());
        TestCase.assertEquals(true, coptic.days().isSupported());
        TestCase.assertEquals(true, coptic.halfdays().isSupported());
        TestCase.assertEquals(true, coptic.hours().isSupported());
        TestCase.assertEquals(true, coptic.minutes().isSupported());
        TestCase.assertEquals(true, coptic.seconds().isSupported());
        TestCase.assertEquals(true, coptic.millis().isSupported());
        TestCase.assertEquals(false, coptic.centuries().isPrecise());
        TestCase.assertEquals(false, coptic.years().isPrecise());
        TestCase.assertEquals(false, coptic.weekyears().isPrecise());
        TestCase.assertEquals(false, coptic.months().isPrecise());
        TestCase.assertEquals(false, coptic.weeks().isPrecise());
        TestCase.assertEquals(false, coptic.days().isPrecise());
        TestCase.assertEquals(false, coptic.halfdays().isPrecise());
        TestCase.assertEquals(true, coptic.hours().isPrecise());
        TestCase.assertEquals(true, coptic.minutes().isPrecise());
        TestCase.assertEquals(true, coptic.seconds().isPrecise());
        TestCase.assertEquals(true, coptic.millis().isPrecise());
        final CopticChronology copticUTC = CopticChronology.getInstanceUTC();
        TestCase.assertEquals(false, copticUTC.centuries().isPrecise());
        TestCase.assertEquals(false, copticUTC.years().isPrecise());
        TestCase.assertEquals(false, copticUTC.weekyears().isPrecise());
        TestCase.assertEquals(false, copticUTC.months().isPrecise());
        TestCase.assertEquals(true, copticUTC.weeks().isPrecise());
        TestCase.assertEquals(true, copticUTC.days().isPrecise());
        TestCase.assertEquals(true, copticUTC.halfdays().isPrecise());
        TestCase.assertEquals(true, copticUTC.hours().isPrecise());
        TestCase.assertEquals(true, copticUTC.minutes().isPrecise());
        TestCase.assertEquals(true, copticUTC.seconds().isPrecise());
        TestCase.assertEquals(true, copticUTC.millis().isPrecise());
        final DateTimeZone gmt = DateTimeZone.forID("Etc/GMT");
        final CopticChronology copticGMT = CopticChronology.getInstance(gmt);
        TestCase.assertEquals(false, copticGMT.centuries().isPrecise());
        TestCase.assertEquals(false, copticGMT.years().isPrecise());
        TestCase.assertEquals(false, copticGMT.weekyears().isPrecise());
        TestCase.assertEquals(false, copticGMT.months().isPrecise());
        TestCase.assertEquals(true, copticGMT.weeks().isPrecise());
        TestCase.assertEquals(true, copticGMT.days().isPrecise());
        TestCase.assertEquals(true, copticGMT.halfdays().isPrecise());
        TestCase.assertEquals(true, copticGMT.hours().isPrecise());
        TestCase.assertEquals(true, copticGMT.minutes().isPrecise());
        TestCase.assertEquals(true, copticGMT.seconds().isPrecise());
        TestCase.assertEquals(true, copticGMT.millis().isPrecise());
    }

    public void testDateFields() {
        final CopticChronology coptic = CopticChronology.getInstance();
        TestCase.assertEquals("era", coptic.era().getName());
        TestCase.assertEquals("centuryOfEra", coptic.centuryOfEra().getName());
        TestCase.assertEquals("yearOfCentury", coptic.yearOfCentury().getName());
        TestCase.assertEquals("yearOfEra", coptic.yearOfEra().getName());
        TestCase.assertEquals("year", coptic.year().getName());
        TestCase.assertEquals("monthOfYear", coptic.monthOfYear().getName());
        TestCase.assertEquals("weekyearOfCentury", coptic.weekyearOfCentury().getName());
        TestCase.assertEquals("weekyear", coptic.weekyear().getName());
        TestCase.assertEquals("weekOfWeekyear", coptic.weekOfWeekyear().getName());
        TestCase.assertEquals("dayOfYear", coptic.dayOfYear().getName());
        TestCase.assertEquals("dayOfMonth", coptic.dayOfMonth().getName());
        TestCase.assertEquals("dayOfWeek", coptic.dayOfWeek().getName());
        TestCase.assertEquals(true, coptic.era().isSupported());
        TestCase.assertEquals(true, coptic.centuryOfEra().isSupported());
        TestCase.assertEquals(true, coptic.yearOfCentury().isSupported());
        TestCase.assertEquals(true, coptic.yearOfEra().isSupported());
        TestCase.assertEquals(true, coptic.year().isSupported());
        TestCase.assertEquals(true, coptic.monthOfYear().isSupported());
        TestCase.assertEquals(true, coptic.weekyearOfCentury().isSupported());
        TestCase.assertEquals(true, coptic.weekyear().isSupported());
        TestCase.assertEquals(true, coptic.weekOfWeekyear().isSupported());
        TestCase.assertEquals(true, coptic.dayOfYear().isSupported());
        TestCase.assertEquals(true, coptic.dayOfMonth().isSupported());
        TestCase.assertEquals(true, coptic.dayOfWeek().isSupported());
        TestCase.assertEquals(coptic.eras(), coptic.era().getDurationField());
        TestCase.assertEquals(coptic.centuries(), coptic.centuryOfEra().getDurationField());
        TestCase.assertEquals(coptic.years(), coptic.yearOfCentury().getDurationField());
        TestCase.assertEquals(coptic.years(), coptic.yearOfEra().getDurationField());
        TestCase.assertEquals(coptic.years(), coptic.year().getDurationField());
        TestCase.assertEquals(coptic.months(), coptic.monthOfYear().getDurationField());
        TestCase.assertEquals(coptic.weekyears(), coptic.weekyearOfCentury().getDurationField());
        TestCase.assertEquals(coptic.weekyears(), coptic.weekyear().getDurationField());
        TestCase.assertEquals(coptic.weeks(), coptic.weekOfWeekyear().getDurationField());
        TestCase.assertEquals(coptic.days(), coptic.dayOfYear().getDurationField());
        TestCase.assertEquals(coptic.days(), coptic.dayOfMonth().getDurationField());
        TestCase.assertEquals(coptic.days(), coptic.dayOfWeek().getDurationField());
        TestCase.assertEquals(null, coptic.era().getRangeDurationField());
        TestCase.assertEquals(coptic.eras(), coptic.centuryOfEra().getRangeDurationField());
        TestCase.assertEquals(coptic.centuries(), coptic.yearOfCentury().getRangeDurationField());
        TestCase.assertEquals(coptic.eras(), coptic.yearOfEra().getRangeDurationField());
        TestCase.assertEquals(null, coptic.year().getRangeDurationField());
        TestCase.assertEquals(coptic.years(), coptic.monthOfYear().getRangeDurationField());
        TestCase.assertEquals(coptic.centuries(), coptic.weekyearOfCentury().getRangeDurationField());
        TestCase.assertEquals(null, coptic.weekyear().getRangeDurationField());
        TestCase.assertEquals(coptic.weekyears(), coptic.weekOfWeekyear().getRangeDurationField());
        TestCase.assertEquals(coptic.years(), coptic.dayOfYear().getRangeDurationField());
        TestCase.assertEquals(coptic.months(), coptic.dayOfMonth().getRangeDurationField());
        TestCase.assertEquals(coptic.weeks(), coptic.dayOfWeek().getRangeDurationField());
    }

    public void testTimeFields() {
        final CopticChronology coptic = CopticChronology.getInstance();
        TestCase.assertEquals("halfdayOfDay", coptic.halfdayOfDay().getName());
        TestCase.assertEquals("clockhourOfHalfday", coptic.clockhourOfHalfday().getName());
        TestCase.assertEquals("hourOfHalfday", coptic.hourOfHalfday().getName());
        TestCase.assertEquals("clockhourOfDay", coptic.clockhourOfDay().getName());
        TestCase.assertEquals("hourOfDay", coptic.hourOfDay().getName());
        TestCase.assertEquals("minuteOfDay", coptic.minuteOfDay().getName());
        TestCase.assertEquals("minuteOfHour", coptic.minuteOfHour().getName());
        TestCase.assertEquals("secondOfDay", coptic.secondOfDay().getName());
        TestCase.assertEquals("secondOfMinute", coptic.secondOfMinute().getName());
        TestCase.assertEquals("millisOfDay", coptic.millisOfDay().getName());
        TestCase.assertEquals("millisOfSecond", coptic.millisOfSecond().getName());
        TestCase.assertEquals(true, coptic.halfdayOfDay().isSupported());
        TestCase.assertEquals(true, coptic.clockhourOfHalfday().isSupported());
        TestCase.assertEquals(true, coptic.hourOfHalfday().isSupported());
        TestCase.assertEquals(true, coptic.clockhourOfDay().isSupported());
        TestCase.assertEquals(true, coptic.hourOfDay().isSupported());
        TestCase.assertEquals(true, coptic.minuteOfDay().isSupported());
        TestCase.assertEquals(true, coptic.minuteOfHour().isSupported());
        TestCase.assertEquals(true, coptic.secondOfDay().isSupported());
        TestCase.assertEquals(true, coptic.secondOfMinute().isSupported());
        TestCase.assertEquals(true, coptic.millisOfDay().isSupported());
        TestCase.assertEquals(true, coptic.millisOfSecond().isSupported());
    }

    // -----------------------------------------------------------------------
    public void testEpoch() {
        DateTime epoch = new DateTime(1, 1, 1, 0, 0, 0, 0, TestCopticChronology.COPTIC_UTC);
        TestCase.assertEquals(new DateTime(284, 8, 29, 0, 0, 0, 0, TestCopticChronology.JULIAN_UTC), epoch.withChronology(TestCopticChronology.JULIAN_UTC));
    }

    public void testEra() {
        TestCase.assertEquals(1, AM);
        try {
            new DateTime((-1), 13, 5, 0, 0, 0, 0, TestCopticChronology.COPTIC_UTC);
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
        System.out.println("\nTestCopticChronology.testCalendar");
        DateTime epoch = new DateTime(1, 1, 1, 0, 0, 0, 0, TestCopticChronology.COPTIC_UTC);
        long millis = epoch.getMillis();
        long end = getMillis();
        DateTimeField dayOfWeek = TestCopticChronology.COPTIC_UTC.dayOfWeek();
        DateTimeField dayOfYear = TestCopticChronology.COPTIC_UTC.dayOfYear();
        DateTimeField dayOfMonth = TestCopticChronology.COPTIC_UTC.dayOfMonth();
        DateTimeField monthOfYear = TestCopticChronology.COPTIC_UTC.monthOfYear();
        DateTimeField year = TestCopticChronology.COPTIC_UTC.year();
        DateTimeField yearOfEra = TestCopticChronology.COPTIC_UTC.yearOfEra();
        DateTimeField era = TestCopticChronology.COPTIC_UTC.era();
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
            TestCase.assertEquals("AM", era.getAsText(millis));
            TestCase.assertEquals("AM", era.getAsShortText(millis));
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

            millis += TestCopticChronology.SKIP;
        } 
    }

    public void testSampleDate() {
        DateTime dt = new DateTime(2004, 6, 9, 0, 0, 0, 0, TestCopticChronology.ISO_UTC).withChronology(TestCopticChronology.COPTIC_UTC);
        TestCase.assertEquals(AM, dt.getEra());
        TestCase.assertEquals(18, dt.getCenturyOfEra());// TODO confirm

        TestCase.assertEquals(20, dt.getYearOfCentury());
        TestCase.assertEquals(1720, dt.getYearOfEra());
        TestCase.assertEquals(1720, dt.getYear());
        Property fld = dt.year();
        TestCase.assertEquals(false, fld.isLeap());
        TestCase.assertEquals(0, fld.getLeapAmount());
        TestCase.assertEquals(DurationFieldType.days(), fld.getLeapDurationField().getType());
        TestCase.assertEquals(new DateTime(1721, 10, 2, 0, 0, 0, 0, TestCopticChronology.COPTIC_UTC), fld.addToCopy(1));
        TestCase.assertEquals(10, dt.getMonthOfYear());
        fld = dt.monthOfYear();
        TestCase.assertEquals(false, fld.isLeap());
        TestCase.assertEquals(0, fld.getLeapAmount());
        TestCase.assertEquals(DurationFieldType.days(), fld.getLeapDurationField().getType());
        TestCase.assertEquals(1, fld.getMinimumValue());
        TestCase.assertEquals(1, fld.getMinimumValueOverall());
        TestCase.assertEquals(13, fld.getMaximumValue());
        TestCase.assertEquals(13, fld.getMaximumValueOverall());
        TestCase.assertEquals(new DateTime(1721, 1, 2, 0, 0, 0, 0, TestCopticChronology.COPTIC_UTC), fld.addToCopy(4));
        TestCase.assertEquals(new DateTime(1720, 1, 2, 0, 0, 0, 0, TestCopticChronology.COPTIC_UTC), fld.addWrapFieldToCopy(4));
        TestCase.assertEquals(2, dt.getDayOfMonth());
        fld = dt.dayOfMonth();
        TestCase.assertEquals(false, fld.isLeap());
        TestCase.assertEquals(0, fld.getLeapAmount());
        TestCase.assertEquals(null, fld.getLeapDurationField());
        TestCase.assertEquals(1, fld.getMinimumValue());
        TestCase.assertEquals(1, fld.getMinimumValueOverall());
        TestCase.assertEquals(30, fld.getMaximumValue());
        TestCase.assertEquals(30, fld.getMaximumValueOverall());
        TestCase.assertEquals(new DateTime(1720, 10, 3, 0, 0, 0, 0, TestCopticChronology.COPTIC_UTC), fld.addToCopy(1));
        TestCase.assertEquals(WEDNESDAY, dt.getDayOfWeek());
        fld = dt.dayOfWeek();
        TestCase.assertEquals(false, fld.isLeap());
        TestCase.assertEquals(0, fld.getLeapAmount());
        TestCase.assertEquals(null, fld.getLeapDurationField());
        TestCase.assertEquals(1, fld.getMinimumValue());
        TestCase.assertEquals(1, fld.getMinimumValueOverall());
        TestCase.assertEquals(7, fld.getMaximumValue());
        TestCase.assertEquals(7, fld.getMaximumValueOverall());
        TestCase.assertEquals(new DateTime(1720, 10, 3, 0, 0, 0, 0, TestCopticChronology.COPTIC_UTC), fld.addToCopy(1));
        TestCase.assertEquals(((9 * 30) + 2), dt.getDayOfYear());
        fld = dt.dayOfYear();
        TestCase.assertEquals(false, fld.isLeap());
        TestCase.assertEquals(0, fld.getLeapAmount());
        TestCase.assertEquals(null, fld.getLeapDurationField());
        TestCase.assertEquals(1, fld.getMinimumValue());
        TestCase.assertEquals(1, fld.getMinimumValueOverall());
        TestCase.assertEquals(365, fld.getMaximumValue());
        TestCase.assertEquals(366, fld.getMaximumValueOverall());
        TestCase.assertEquals(new DateTime(1720, 10, 3, 0, 0, 0, 0, TestCopticChronology.COPTIC_UTC), fld.addToCopy(1));
        TestCase.assertEquals(0, dt.getHourOfDay());
        TestCase.assertEquals(0, dt.getMinuteOfHour());
        TestCase.assertEquals(0, dt.getSecondOfMinute());
        TestCase.assertEquals(0, dt.getMillisOfSecond());
    }

    public void testSampleDateWithZone() {
        DateTime dt = new DateTime(2004, 6, 9, 12, 0, 0, 0, TestCopticChronology.PARIS).withChronology(TestCopticChronology.COPTIC_UTC);
        TestCase.assertEquals(AM, dt.getEra());
        TestCase.assertEquals(1720, dt.getYear());
        TestCase.assertEquals(1720, dt.getYearOfEra());
        TestCase.assertEquals(10, dt.getMonthOfYear());
        TestCase.assertEquals(2, dt.getDayOfMonth());
        TestCase.assertEquals(10, dt.getHourOfDay());// PARIS is UTC+2 in summer (12-2=10)

        TestCase.assertEquals(0, dt.getMinuteOfHour());
        TestCase.assertEquals(0, dt.getSecondOfMinute());
        TestCase.assertEquals(0, dt.getMillisOfSecond());
    }

    public void testDurationYear() {
        // Leap 1723
        DateTime dt20 = new DateTime(1720, 10, 2, 0, 0, 0, 0, TestCopticChronology.COPTIC_UTC);
        DateTime dt21 = new DateTime(1721, 10, 2, 0, 0, 0, 0, TestCopticChronology.COPTIC_UTC);
        DateTime dt22 = new DateTime(1722, 10, 2, 0, 0, 0, 0, TestCopticChronology.COPTIC_UTC);
        DateTime dt23 = new DateTime(1723, 10, 2, 0, 0, 0, 0, TestCopticChronology.COPTIC_UTC);
        DateTime dt24 = new DateTime(1724, 10, 2, 0, 0, 0, 0, TestCopticChronology.COPTIC_UTC);
        DurationField fld = dt20.year().getDurationField();
        TestCase.assertEquals(TestCopticChronology.COPTIC_UTC.years(), fld);
        TestCase.assertEquals(((1L * 365L) * (TestCopticChronology.MILLIS_PER_DAY)), fld.getMillis(1, dt20.getMillis()));
        TestCase.assertEquals(((2L * 365L) * (TestCopticChronology.MILLIS_PER_DAY)), fld.getMillis(2, dt20.getMillis()));
        TestCase.assertEquals(((3L * 365L) * (TestCopticChronology.MILLIS_PER_DAY)), fld.getMillis(3, dt20.getMillis()));
        TestCase.assertEquals((((4L * 365L) + 1L) * (TestCopticChronology.MILLIS_PER_DAY)), fld.getMillis(4, dt20.getMillis()));
        TestCase.assertEquals(((((4L * 365L) + 1L) * (TestCopticChronology.MILLIS_PER_DAY)) / 4), fld.getMillis(1));
        TestCase.assertEquals(((((4L * 365L) + 1L) * (TestCopticChronology.MILLIS_PER_DAY)) / 2), fld.getMillis(2));
        TestCase.assertEquals(((1L * 365L) * (TestCopticChronology.MILLIS_PER_DAY)), fld.getMillis(1L, dt20.getMillis()));
        TestCase.assertEquals(((2L * 365L) * (TestCopticChronology.MILLIS_PER_DAY)), fld.getMillis(2L, dt20.getMillis()));
        TestCase.assertEquals(((3L * 365L) * (TestCopticChronology.MILLIS_PER_DAY)), fld.getMillis(3L, dt20.getMillis()));
        TestCase.assertEquals((((4L * 365L) + 1L) * (TestCopticChronology.MILLIS_PER_DAY)), fld.getMillis(4L, dt20.getMillis()));
        TestCase.assertEquals(((((4L * 365L) + 1L) * (TestCopticChronology.MILLIS_PER_DAY)) / 4), fld.getMillis(1L));
        TestCase.assertEquals(((((4L * 365L) + 1L) * (TestCopticChronology.MILLIS_PER_DAY)) / 2), fld.getMillis(2L));
        TestCase.assertEquals(((((4L * 365L) + 1L) * (TestCopticChronology.MILLIS_PER_DAY)) / 4), fld.getUnitMillis());
        TestCase.assertEquals(0, fld.getValue((((1L * 365L) * (TestCopticChronology.MILLIS_PER_DAY)) - 1L), dt20.getMillis()));
        TestCase.assertEquals(1, fld.getValue(((1L * 365L) * (TestCopticChronology.MILLIS_PER_DAY)), dt20.getMillis()));
        TestCase.assertEquals(1, fld.getValue((((1L * 365L) * (TestCopticChronology.MILLIS_PER_DAY)) + 1L), dt20.getMillis()));
        TestCase.assertEquals(1, fld.getValue((((2L * 365L) * (TestCopticChronology.MILLIS_PER_DAY)) - 1L), dt20.getMillis()));
        TestCase.assertEquals(2, fld.getValue(((2L * 365L) * (TestCopticChronology.MILLIS_PER_DAY)), dt20.getMillis()));
        TestCase.assertEquals(2, fld.getValue((((2L * 365L) * (TestCopticChronology.MILLIS_PER_DAY)) + 1L), dt20.getMillis()));
        TestCase.assertEquals(2, fld.getValue((((3L * 365L) * (TestCopticChronology.MILLIS_PER_DAY)) - 1L), dt20.getMillis()));
        TestCase.assertEquals(3, fld.getValue(((3L * 365L) * (TestCopticChronology.MILLIS_PER_DAY)), dt20.getMillis()));
        TestCase.assertEquals(3, fld.getValue((((3L * 365L) * (TestCopticChronology.MILLIS_PER_DAY)) + 1L), dt20.getMillis()));
        TestCase.assertEquals(3, fld.getValue(((((4L * 365L) + 1L) * (TestCopticChronology.MILLIS_PER_DAY)) - 1L), dt20.getMillis()));
        TestCase.assertEquals(4, fld.getValue((((4L * 365L) + 1L) * (TestCopticChronology.MILLIS_PER_DAY)), dt20.getMillis()));
        TestCase.assertEquals(4, fld.getValue(((((4L * 365L) + 1L) * (TestCopticChronology.MILLIS_PER_DAY)) + 1L), dt20.getMillis()));
        TestCase.assertEquals(dt21.getMillis(), fld.add(dt20.getMillis(), 1));
        TestCase.assertEquals(dt22.getMillis(), fld.add(dt20.getMillis(), 2));
        TestCase.assertEquals(dt23.getMillis(), fld.add(dt20.getMillis(), 3));
        TestCase.assertEquals(dt24.getMillis(), fld.add(dt20.getMillis(), 4));
        TestCase.assertEquals(dt21.getMillis(), fld.add(dt20.getMillis(), 1L));
        TestCase.assertEquals(dt22.getMillis(), fld.add(dt20.getMillis(), 2L));
        TestCase.assertEquals(dt23.getMillis(), fld.add(dt20.getMillis(), 3L));
        TestCase.assertEquals(dt24.getMillis(), fld.add(dt20.getMillis(), 4L));
    }

    public void testDurationMonth() {
        // Leap 1723
        DateTime dt11 = new DateTime(1723, 11, 2, 0, 0, 0, 0, TestCopticChronology.COPTIC_UTC);
        DateTime dt12 = new DateTime(1723, 12, 2, 0, 0, 0, 0, TestCopticChronology.COPTIC_UTC);
        DateTime dt13 = new DateTime(1723, 13, 2, 0, 0, 0, 0, TestCopticChronology.COPTIC_UTC);
        DateTime dt01 = new DateTime(1724, 1, 2, 0, 0, 0, 0, TestCopticChronology.COPTIC_UTC);
        DurationField fld = dt11.monthOfYear().getDurationField();
        TestCase.assertEquals(TestCopticChronology.COPTIC_UTC.months(), fld);
        TestCase.assertEquals(((1L * 30L) * (TestCopticChronology.MILLIS_PER_DAY)), fld.getMillis(1, dt11.getMillis()));
        TestCase.assertEquals(((2L * 30L) * (TestCopticChronology.MILLIS_PER_DAY)), fld.getMillis(2, dt11.getMillis()));
        TestCase.assertEquals((((2L * 30L) + 6L) * (TestCopticChronology.MILLIS_PER_DAY)), fld.getMillis(3, dt11.getMillis()));
        TestCase.assertEquals((((3L * 30L) + 6L) * (TestCopticChronology.MILLIS_PER_DAY)), fld.getMillis(4, dt11.getMillis()));
        TestCase.assertEquals(((1L * 30L) * (TestCopticChronology.MILLIS_PER_DAY)), fld.getMillis(1));
        TestCase.assertEquals(((2L * 30L) * (TestCopticChronology.MILLIS_PER_DAY)), fld.getMillis(2));
        TestCase.assertEquals(((13L * 30L) * (TestCopticChronology.MILLIS_PER_DAY)), fld.getMillis(13));
        TestCase.assertEquals(((1L * 30L) * (TestCopticChronology.MILLIS_PER_DAY)), fld.getMillis(1L, dt11.getMillis()));
        TestCase.assertEquals(((2L * 30L) * (TestCopticChronology.MILLIS_PER_DAY)), fld.getMillis(2L, dt11.getMillis()));
        TestCase.assertEquals((((2L * 30L) + 6L) * (TestCopticChronology.MILLIS_PER_DAY)), fld.getMillis(3L, dt11.getMillis()));
        TestCase.assertEquals((((3L * 30L) + 6L) * (TestCopticChronology.MILLIS_PER_DAY)), fld.getMillis(4L, dt11.getMillis()));
        TestCase.assertEquals(((1L * 30L) * (TestCopticChronology.MILLIS_PER_DAY)), fld.getMillis(1L));
        TestCase.assertEquals(((2L * 30L) * (TestCopticChronology.MILLIS_PER_DAY)), fld.getMillis(2L));
        TestCase.assertEquals(((13L * 30L) * (TestCopticChronology.MILLIS_PER_DAY)), fld.getMillis(13L));
        TestCase.assertEquals(0, fld.getValue((((1L * 30L) * (TestCopticChronology.MILLIS_PER_DAY)) - 1L), dt11.getMillis()));
        TestCase.assertEquals(1, fld.getValue(((1L * 30L) * (TestCopticChronology.MILLIS_PER_DAY)), dt11.getMillis()));
        TestCase.assertEquals(1, fld.getValue((((1L * 30L) * (TestCopticChronology.MILLIS_PER_DAY)) + 1L), dt11.getMillis()));
        TestCase.assertEquals(1, fld.getValue((((2L * 30L) * (TestCopticChronology.MILLIS_PER_DAY)) - 1L), dt11.getMillis()));
        TestCase.assertEquals(2, fld.getValue(((2L * 30L) * (TestCopticChronology.MILLIS_PER_DAY)), dt11.getMillis()));
        TestCase.assertEquals(2, fld.getValue((((2L * 30L) * (TestCopticChronology.MILLIS_PER_DAY)) + 1L), dt11.getMillis()));
        TestCase.assertEquals(2, fld.getValue(((((2L * 30L) + 6L) * (TestCopticChronology.MILLIS_PER_DAY)) - 1L), dt11.getMillis()));
        TestCase.assertEquals(3, fld.getValue((((2L * 30L) + 6L) * (TestCopticChronology.MILLIS_PER_DAY)), dt11.getMillis()));
        TestCase.assertEquals(3, fld.getValue(((((2L * 30L) + 6L) * (TestCopticChronology.MILLIS_PER_DAY)) + 1L), dt11.getMillis()));
        TestCase.assertEquals(3, fld.getValue(((((3L * 30L) + 6L) * (TestCopticChronology.MILLIS_PER_DAY)) - 1L), dt11.getMillis()));
        TestCase.assertEquals(4, fld.getValue((((3L * 30L) + 6L) * (TestCopticChronology.MILLIS_PER_DAY)), dt11.getMillis()));
        TestCase.assertEquals(4, fld.getValue(((((3L * 30L) + 6L) * (TestCopticChronology.MILLIS_PER_DAY)) + 1L), dt11.getMillis()));
        TestCase.assertEquals(dt12.getMillis(), fld.add(dt11.getMillis(), 1));
        TestCase.assertEquals(dt13.getMillis(), fld.add(dt11.getMillis(), 2));
        TestCase.assertEquals(dt01.getMillis(), fld.add(dt11.getMillis(), 3));
        TestCase.assertEquals(dt12.getMillis(), fld.add(dt11.getMillis(), 1L));
        TestCase.assertEquals(dt13.getMillis(), fld.add(dt11.getMillis(), 2L));
        TestCase.assertEquals(dt01.getMillis(), fld.add(dt11.getMillis(), 3L));
    }

    public void testLeap_5_13() {
        Chronology chrono = CopticChronology.getInstance();
        DateTime dt = new DateTime(3, 13, 5, 0, 0, chrono);
        TestCase.assertEquals(true, dt.year().isLeap());
        TestCase.assertEquals(true, dt.monthOfYear().isLeap());
        TestCase.assertEquals(false, dt.dayOfMonth().isLeap());
        TestCase.assertEquals(false, dt.dayOfYear().isLeap());
    }

    public void testLeap_6_13() {
        Chronology chrono = CopticChronology.getInstance();
        DateTime dt = new DateTime(3, 13, 6, 0, 0, chrono);
        TestCase.assertEquals(true, dt.year().isLeap());
        TestCase.assertEquals(true, dt.monthOfYear().isLeap());
        TestCase.assertEquals(true, dt.dayOfMonth().isLeap());
        TestCase.assertEquals(true, dt.dayOfYear().isLeap());
    }
}

