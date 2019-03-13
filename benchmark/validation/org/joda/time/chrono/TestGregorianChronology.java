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
import org.joda.time.YearMonthDay;


/**
 * This class is a Junit unit test for GregorianChronology.
 *
 * @author Stephen Colebourne
 */
@SuppressWarnings("deprecation")
public class TestGregorianChronology extends TestCase {
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final DateTimeZone TOKYO = DateTimeZone.forID("Asia/Tokyo");

    long y2002days = ((((((((((((((((((((((((((((((365 + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365;

    // 2002-06-09
    private long TEST_TIME_NOW = ((((((((y2002days) + 31L) + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (DateTimeConstants.MILLIS_PER_DAY);

    private DateTimeZone originalDateTimeZone = null;

    private TimeZone originalTimeZone = null;

    private Locale originalLocale = null;

    public TestGregorianChronology(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testFactoryUTC() {
        TestCase.assertEquals(UTC, GregorianChronology.getInstanceUTC().getZone());
        TestCase.assertSame(GregorianChronology.class, GregorianChronology.getInstanceUTC().getClass());
    }

    public void testFactory() {
        TestCase.assertEquals(TestGregorianChronology.LONDON, GregorianChronology.getInstance().getZone());
        TestCase.assertSame(GregorianChronology.class, GregorianChronology.getInstance().getClass());
    }

    public void testFactory_Zone() {
        TestCase.assertEquals(TestGregorianChronology.TOKYO, GregorianChronology.getInstance(TestGregorianChronology.TOKYO).getZone());
        TestCase.assertEquals(TestGregorianChronology.PARIS, GregorianChronology.getInstance(TestGregorianChronology.PARIS).getZone());
        TestCase.assertEquals(TestGregorianChronology.LONDON, GregorianChronology.getInstance(null).getZone());
        TestCase.assertSame(GregorianChronology.class, GregorianChronology.getInstance(TestGregorianChronology.TOKYO).getClass());
    }

    public void testFactory_Zone_int() {
        GregorianChronology chrono = GregorianChronology.getInstance(TestGregorianChronology.TOKYO, 2);
        TestCase.assertEquals(TestGregorianChronology.TOKYO, chrono.getZone());
        TestCase.assertEquals(2, chrono.getMinimumDaysInFirstWeek());
        try {
            GregorianChronology.getInstance(TestGregorianChronology.TOKYO, 0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            GregorianChronology.getInstance(TestGregorianChronology.TOKYO, 8);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testEquality() {
        TestCase.assertSame(GregorianChronology.getInstance(TestGregorianChronology.TOKYO), GregorianChronology.getInstance(TestGregorianChronology.TOKYO));
        TestCase.assertSame(GregorianChronology.getInstance(TestGregorianChronology.LONDON), GregorianChronology.getInstance(TestGregorianChronology.LONDON));
        TestCase.assertSame(GregorianChronology.getInstance(TestGregorianChronology.PARIS), GregorianChronology.getInstance(TestGregorianChronology.PARIS));
        TestCase.assertSame(GregorianChronology.getInstanceUTC(), GregorianChronology.getInstanceUTC());
        TestCase.assertSame(GregorianChronology.getInstance(), GregorianChronology.getInstance(TestGregorianChronology.LONDON));
    }

    public void testWithUTC() {
        TestCase.assertSame(GregorianChronology.getInstanceUTC(), GregorianChronology.getInstance(TestGregorianChronology.LONDON).withUTC());
        TestCase.assertSame(GregorianChronology.getInstanceUTC(), GregorianChronology.getInstance(TestGregorianChronology.TOKYO).withUTC());
        TestCase.assertSame(GregorianChronology.getInstanceUTC(), GregorianChronology.getInstanceUTC().withUTC());
        TestCase.assertSame(GregorianChronology.getInstanceUTC(), GregorianChronology.getInstance().withUTC());
    }

    public void testWithZone() {
        TestCase.assertSame(GregorianChronology.getInstance(TestGregorianChronology.TOKYO), GregorianChronology.getInstance(TestGregorianChronology.TOKYO).withZone(TestGregorianChronology.TOKYO));
        TestCase.assertSame(GregorianChronology.getInstance(TestGregorianChronology.LONDON), GregorianChronology.getInstance(TestGregorianChronology.TOKYO).withZone(TestGregorianChronology.LONDON));
        TestCase.assertSame(GregorianChronology.getInstance(TestGregorianChronology.PARIS), GregorianChronology.getInstance(TestGregorianChronology.TOKYO).withZone(TestGregorianChronology.PARIS));
        TestCase.assertSame(GregorianChronology.getInstance(TestGregorianChronology.LONDON), GregorianChronology.getInstance(TestGregorianChronology.TOKYO).withZone(null));
        TestCase.assertSame(GregorianChronology.getInstance(TestGregorianChronology.PARIS), GregorianChronology.getInstance().withZone(TestGregorianChronology.PARIS));
        TestCase.assertSame(GregorianChronology.getInstance(TestGregorianChronology.PARIS), GregorianChronology.getInstanceUTC().withZone(TestGregorianChronology.PARIS));
    }

    public void testToString() {
        TestCase.assertEquals("GregorianChronology[Europe/London]", GregorianChronology.getInstance(TestGregorianChronology.LONDON).toString());
        TestCase.assertEquals("GregorianChronology[Asia/Tokyo]", GregorianChronology.getInstance(TestGregorianChronology.TOKYO).toString());
        TestCase.assertEquals("GregorianChronology[Europe/London]", GregorianChronology.getInstance().toString());
        TestCase.assertEquals("GregorianChronology[UTC]", GregorianChronology.getInstanceUTC().toString());
        TestCase.assertEquals("GregorianChronology[UTC,mdfw=2]", GregorianChronology.getInstance(UTC, 2).toString());
    }

    // -----------------------------------------------------------------------
    public void testDurationFields() {
        final GregorianChronology greg = GregorianChronology.getInstance();
        TestCase.assertEquals("eras", greg.eras().getName());
        TestCase.assertEquals("centuries", greg.centuries().getName());
        TestCase.assertEquals("years", greg.years().getName());
        TestCase.assertEquals("weekyears", greg.weekyears().getName());
        TestCase.assertEquals("months", greg.months().getName());
        TestCase.assertEquals("weeks", greg.weeks().getName());
        TestCase.assertEquals("days", greg.days().getName());
        TestCase.assertEquals("halfdays", greg.halfdays().getName());
        TestCase.assertEquals("hours", greg.hours().getName());
        TestCase.assertEquals("minutes", greg.minutes().getName());
        TestCase.assertEquals("seconds", greg.seconds().getName());
        TestCase.assertEquals("millis", greg.millis().getName());
        TestCase.assertEquals(false, greg.eras().isSupported());
        TestCase.assertEquals(true, greg.centuries().isSupported());
        TestCase.assertEquals(true, greg.years().isSupported());
        TestCase.assertEquals(true, greg.weekyears().isSupported());
        TestCase.assertEquals(true, greg.months().isSupported());
        TestCase.assertEquals(true, greg.weeks().isSupported());
        TestCase.assertEquals(true, greg.days().isSupported());
        TestCase.assertEquals(true, greg.halfdays().isSupported());
        TestCase.assertEquals(true, greg.hours().isSupported());
        TestCase.assertEquals(true, greg.minutes().isSupported());
        TestCase.assertEquals(true, greg.seconds().isSupported());
        TestCase.assertEquals(true, greg.millis().isSupported());
        TestCase.assertEquals(false, greg.centuries().isPrecise());
        TestCase.assertEquals(false, greg.years().isPrecise());
        TestCase.assertEquals(false, greg.weekyears().isPrecise());
        TestCase.assertEquals(false, greg.months().isPrecise());
        TestCase.assertEquals(false, greg.weeks().isPrecise());
        TestCase.assertEquals(false, greg.days().isPrecise());
        TestCase.assertEquals(false, greg.halfdays().isPrecise());
        TestCase.assertEquals(true, greg.hours().isPrecise());
        TestCase.assertEquals(true, greg.minutes().isPrecise());
        TestCase.assertEquals(true, greg.seconds().isPrecise());
        TestCase.assertEquals(true, greg.millis().isPrecise());
        final GregorianChronology gregUTC = GregorianChronology.getInstanceUTC();
        TestCase.assertEquals(false, gregUTC.centuries().isPrecise());
        TestCase.assertEquals(false, gregUTC.years().isPrecise());
        TestCase.assertEquals(false, gregUTC.weekyears().isPrecise());
        TestCase.assertEquals(false, gregUTC.months().isPrecise());
        TestCase.assertEquals(true, gregUTC.weeks().isPrecise());
        TestCase.assertEquals(true, gregUTC.days().isPrecise());
        TestCase.assertEquals(true, gregUTC.halfdays().isPrecise());
        TestCase.assertEquals(true, gregUTC.hours().isPrecise());
        TestCase.assertEquals(true, gregUTC.minutes().isPrecise());
        TestCase.assertEquals(true, gregUTC.seconds().isPrecise());
        TestCase.assertEquals(true, gregUTC.millis().isPrecise());
        final DateTimeZone gmt = DateTimeZone.forID("Etc/GMT");
        final GregorianChronology gregGMT = GregorianChronology.getInstance(gmt);
        TestCase.assertEquals(false, gregGMT.centuries().isPrecise());
        TestCase.assertEquals(false, gregGMT.years().isPrecise());
        TestCase.assertEquals(false, gregGMT.weekyears().isPrecise());
        TestCase.assertEquals(false, gregGMT.months().isPrecise());
        TestCase.assertEquals(true, gregGMT.weeks().isPrecise());
        TestCase.assertEquals(true, gregGMT.days().isPrecise());
        TestCase.assertEquals(true, gregGMT.halfdays().isPrecise());
        TestCase.assertEquals(true, gregGMT.hours().isPrecise());
        TestCase.assertEquals(true, gregGMT.minutes().isPrecise());
        TestCase.assertEquals(true, gregGMT.seconds().isPrecise());
        TestCase.assertEquals(true, gregGMT.millis().isPrecise());
    }

    public void testDateFields() {
        final GregorianChronology greg = GregorianChronology.getInstance();
        TestCase.assertEquals("era", greg.era().getName());
        TestCase.assertEquals("centuryOfEra", greg.centuryOfEra().getName());
        TestCase.assertEquals("yearOfCentury", greg.yearOfCentury().getName());
        TestCase.assertEquals("yearOfEra", greg.yearOfEra().getName());
        TestCase.assertEquals("year", greg.year().getName());
        TestCase.assertEquals("monthOfYear", greg.monthOfYear().getName());
        TestCase.assertEquals("weekyearOfCentury", greg.weekyearOfCentury().getName());
        TestCase.assertEquals("weekyear", greg.weekyear().getName());
        TestCase.assertEquals("weekOfWeekyear", greg.weekOfWeekyear().getName());
        TestCase.assertEquals("dayOfYear", greg.dayOfYear().getName());
        TestCase.assertEquals("dayOfMonth", greg.dayOfMonth().getName());
        TestCase.assertEquals("dayOfWeek", greg.dayOfWeek().getName());
        TestCase.assertEquals(true, greg.era().isSupported());
        TestCase.assertEquals(true, greg.centuryOfEra().isSupported());
        TestCase.assertEquals(true, greg.yearOfCentury().isSupported());
        TestCase.assertEquals(true, greg.yearOfEra().isSupported());
        TestCase.assertEquals(true, greg.year().isSupported());
        TestCase.assertEquals(true, greg.monthOfYear().isSupported());
        TestCase.assertEquals(true, greg.weekyearOfCentury().isSupported());
        TestCase.assertEquals(true, greg.weekyear().isSupported());
        TestCase.assertEquals(true, greg.weekOfWeekyear().isSupported());
        TestCase.assertEquals(true, greg.dayOfYear().isSupported());
        TestCase.assertEquals(true, greg.dayOfMonth().isSupported());
        TestCase.assertEquals(true, greg.dayOfWeek().isSupported());
        TestCase.assertEquals(greg.eras(), greg.era().getDurationField());
        TestCase.assertEquals(greg.centuries(), greg.centuryOfEra().getDurationField());
        TestCase.assertEquals(greg.years(), greg.yearOfCentury().getDurationField());
        TestCase.assertEquals(greg.years(), greg.yearOfEra().getDurationField());
        TestCase.assertEquals(greg.years(), greg.year().getDurationField());
        TestCase.assertEquals(greg.months(), greg.monthOfYear().getDurationField());
        TestCase.assertEquals(greg.weekyears(), greg.weekyearOfCentury().getDurationField());
        TestCase.assertEquals(greg.weekyears(), greg.weekyear().getDurationField());
        TestCase.assertEquals(greg.weeks(), greg.weekOfWeekyear().getDurationField());
        TestCase.assertEquals(greg.days(), greg.dayOfYear().getDurationField());
        TestCase.assertEquals(greg.days(), greg.dayOfMonth().getDurationField());
        TestCase.assertEquals(greg.days(), greg.dayOfWeek().getDurationField());
        TestCase.assertEquals(null, greg.era().getRangeDurationField());
        TestCase.assertEquals(greg.eras(), greg.centuryOfEra().getRangeDurationField());
        TestCase.assertEquals(greg.centuries(), greg.yearOfCentury().getRangeDurationField());
        TestCase.assertEquals(greg.eras(), greg.yearOfEra().getRangeDurationField());
        TestCase.assertEquals(null, greg.year().getRangeDurationField());
        TestCase.assertEquals(greg.years(), greg.monthOfYear().getRangeDurationField());
        TestCase.assertEquals(greg.centuries(), greg.weekyearOfCentury().getRangeDurationField());
        TestCase.assertEquals(null, greg.weekyear().getRangeDurationField());
        TestCase.assertEquals(greg.weekyears(), greg.weekOfWeekyear().getRangeDurationField());
        TestCase.assertEquals(greg.years(), greg.dayOfYear().getRangeDurationField());
        TestCase.assertEquals(greg.months(), greg.dayOfMonth().getRangeDurationField());
        TestCase.assertEquals(greg.weeks(), greg.dayOfWeek().getRangeDurationField());
    }

    public void testTimeFields() {
        final GregorianChronology greg = GregorianChronology.getInstance();
        TestCase.assertEquals("halfdayOfDay", greg.halfdayOfDay().getName());
        TestCase.assertEquals("clockhourOfHalfday", greg.clockhourOfHalfday().getName());
        TestCase.assertEquals("hourOfHalfday", greg.hourOfHalfday().getName());
        TestCase.assertEquals("clockhourOfDay", greg.clockhourOfDay().getName());
        TestCase.assertEquals("hourOfDay", greg.hourOfDay().getName());
        TestCase.assertEquals("minuteOfDay", greg.minuteOfDay().getName());
        TestCase.assertEquals("minuteOfHour", greg.minuteOfHour().getName());
        TestCase.assertEquals("secondOfDay", greg.secondOfDay().getName());
        TestCase.assertEquals("secondOfMinute", greg.secondOfMinute().getName());
        TestCase.assertEquals("millisOfDay", greg.millisOfDay().getName());
        TestCase.assertEquals("millisOfSecond", greg.millisOfSecond().getName());
        TestCase.assertEquals(true, greg.halfdayOfDay().isSupported());
        TestCase.assertEquals(true, greg.clockhourOfHalfday().isSupported());
        TestCase.assertEquals(true, greg.hourOfHalfday().isSupported());
        TestCase.assertEquals(true, greg.clockhourOfDay().isSupported());
        TestCase.assertEquals(true, greg.hourOfDay().isSupported());
        TestCase.assertEquals(true, greg.minuteOfDay().isSupported());
        TestCase.assertEquals(true, greg.minuteOfHour().isSupported());
        TestCase.assertEquals(true, greg.secondOfDay().isSupported());
        TestCase.assertEquals(true, greg.secondOfMinute().isSupported());
        TestCase.assertEquals(true, greg.millisOfDay().isSupported());
        TestCase.assertEquals(true, greg.millisOfSecond().isSupported());
    }

    public void testMaximumValue() {
        YearMonthDay ymd1 = new YearMonthDay(1999, DateTimeConstants.FEBRUARY, 1);
        DateMidnight dm1 = new DateMidnight(1999, DateTimeConstants.FEBRUARY, 1);
        Chronology chrono = GregorianChronology.getInstance();
        TestCase.assertEquals(28, chrono.dayOfMonth().getMaximumValue(ymd1));
        TestCase.assertEquals(28, chrono.dayOfMonth().getMaximumValue(dm1.getMillis()));
    }

    public void testLeap_28feb() {
        Chronology chrono = GregorianChronology.getInstance();
        DateTime dt = new DateTime(2012, 2, 28, 0, 0, chrono);
        TestCase.assertEquals(true, dt.year().isLeap());
        TestCase.assertEquals(true, dt.monthOfYear().isLeap());
        TestCase.assertEquals(false, dt.dayOfMonth().isLeap());
        TestCase.assertEquals(false, dt.dayOfYear().isLeap());
    }

    public void testLeap_29feb() {
        Chronology chrono = GregorianChronology.getInstance();
        DateTime dt = new DateTime(2012, 2, 29, 0, 0, chrono);
        TestCase.assertEquals(true, dt.year().isLeap());
        TestCase.assertEquals(true, dt.monthOfYear().isLeap());
        TestCase.assertEquals(true, dt.dayOfMonth().isLeap());
        TestCase.assertEquals(true, dt.dayOfYear().isLeap());
    }
}

