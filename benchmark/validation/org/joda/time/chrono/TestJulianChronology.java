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
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;


/**
 * This class is a Junit unit test for JulianChronology.
 *
 * @author Stephen Colebourne
 */
public class TestJulianChronology extends TestCase {
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final DateTimeZone TOKYO = DateTimeZone.forID("Asia/Tokyo");

    long y2002days = ((((((((((((((((((((((((((((((365 + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365;

    // 2002-06-09
    private long TEST_TIME_NOW = ((((((((y2002days) + 31L) + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (DateTimeConstants.MILLIS_PER_DAY);

    private DateTimeZone originalDateTimeZone = null;

    private TimeZone originalTimeZone = null;

    private Locale originalLocale = null;

    public TestJulianChronology(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testFactoryUTC() {
        TestCase.assertEquals(UTC, JulianChronology.getInstanceUTC().getZone());
        TestCase.assertSame(JulianChronology.class, JulianChronology.getInstanceUTC().getClass());
    }

    public void testFactory() {
        TestCase.assertEquals(TestJulianChronology.LONDON, JulianChronology.getInstance().getZone());
        TestCase.assertSame(JulianChronology.class, JulianChronology.getInstance().getClass());
    }

    public void testFactory_Zone() {
        TestCase.assertEquals(TestJulianChronology.TOKYO, JulianChronology.getInstance(TestJulianChronology.TOKYO).getZone());
        TestCase.assertEquals(TestJulianChronology.PARIS, JulianChronology.getInstance(TestJulianChronology.PARIS).getZone());
        TestCase.assertEquals(TestJulianChronology.LONDON, JulianChronology.getInstance(null).getZone());
        TestCase.assertSame(JulianChronology.class, JulianChronology.getInstance(TestJulianChronology.TOKYO).getClass());
    }

    public void testFactory_Zone_int() {
        JulianChronology chrono = JulianChronology.getInstance(TestJulianChronology.TOKYO, 2);
        TestCase.assertEquals(TestJulianChronology.TOKYO, chrono.getZone());
        TestCase.assertEquals(2, chrono.getMinimumDaysInFirstWeek());
        try {
            JulianChronology.getInstance(TestJulianChronology.TOKYO, 0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            JulianChronology.getInstance(TestJulianChronology.TOKYO, 8);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testEquality() {
        TestCase.assertSame(JulianChronology.getInstance(TestJulianChronology.TOKYO), JulianChronology.getInstance(TestJulianChronology.TOKYO));
        TestCase.assertSame(JulianChronology.getInstance(TestJulianChronology.LONDON), JulianChronology.getInstance(TestJulianChronology.LONDON));
        TestCase.assertSame(JulianChronology.getInstance(TestJulianChronology.PARIS), JulianChronology.getInstance(TestJulianChronology.PARIS));
        TestCase.assertSame(JulianChronology.getInstanceUTC(), JulianChronology.getInstanceUTC());
        TestCase.assertSame(JulianChronology.getInstance(), JulianChronology.getInstance(TestJulianChronology.LONDON));
    }

    public void testWithUTC() {
        TestCase.assertSame(JulianChronology.getInstanceUTC(), JulianChronology.getInstance(TestJulianChronology.LONDON).withUTC());
        TestCase.assertSame(JulianChronology.getInstanceUTC(), JulianChronology.getInstance(TestJulianChronology.TOKYO).withUTC());
        TestCase.assertSame(JulianChronology.getInstanceUTC(), JulianChronology.getInstanceUTC().withUTC());
        TestCase.assertSame(JulianChronology.getInstanceUTC(), JulianChronology.getInstance().withUTC());
    }

    public void testWithZone() {
        TestCase.assertSame(JulianChronology.getInstance(TestJulianChronology.TOKYO), JulianChronology.getInstance(TestJulianChronology.TOKYO).withZone(TestJulianChronology.TOKYO));
        TestCase.assertSame(JulianChronology.getInstance(TestJulianChronology.LONDON), JulianChronology.getInstance(TestJulianChronology.TOKYO).withZone(TestJulianChronology.LONDON));
        TestCase.assertSame(JulianChronology.getInstance(TestJulianChronology.PARIS), JulianChronology.getInstance(TestJulianChronology.TOKYO).withZone(TestJulianChronology.PARIS));
        TestCase.assertSame(JulianChronology.getInstance(TestJulianChronology.LONDON), JulianChronology.getInstance(TestJulianChronology.TOKYO).withZone(null));
        TestCase.assertSame(JulianChronology.getInstance(TestJulianChronology.PARIS), JulianChronology.getInstance().withZone(TestJulianChronology.PARIS));
        TestCase.assertSame(JulianChronology.getInstance(TestJulianChronology.PARIS), JulianChronology.getInstanceUTC().withZone(TestJulianChronology.PARIS));
    }

    public void testToString() {
        TestCase.assertEquals("JulianChronology[Europe/London]", JulianChronology.getInstance(TestJulianChronology.LONDON).toString());
        TestCase.assertEquals("JulianChronology[Asia/Tokyo]", JulianChronology.getInstance(TestJulianChronology.TOKYO).toString());
        TestCase.assertEquals("JulianChronology[Europe/London]", JulianChronology.getInstance().toString());
        TestCase.assertEquals("JulianChronology[UTC]", JulianChronology.getInstanceUTC().toString());
        TestCase.assertEquals("JulianChronology[UTC,mdfw=2]", JulianChronology.getInstance(UTC, 2).toString());
    }

    // -----------------------------------------------------------------------
    public void testDurationFields() {
        final JulianChronology julian = JulianChronology.getInstance();
        TestCase.assertEquals("eras", julian.eras().getName());
        TestCase.assertEquals("centuries", julian.centuries().getName());
        TestCase.assertEquals("years", julian.years().getName());
        TestCase.assertEquals("weekyears", julian.weekyears().getName());
        TestCase.assertEquals("months", julian.months().getName());
        TestCase.assertEquals("weeks", julian.weeks().getName());
        TestCase.assertEquals("days", julian.days().getName());
        TestCase.assertEquals("halfdays", julian.halfdays().getName());
        TestCase.assertEquals("hours", julian.hours().getName());
        TestCase.assertEquals("minutes", julian.minutes().getName());
        TestCase.assertEquals("seconds", julian.seconds().getName());
        TestCase.assertEquals("millis", julian.millis().getName());
        TestCase.assertEquals(false, julian.eras().isSupported());
        TestCase.assertEquals(true, julian.centuries().isSupported());
        TestCase.assertEquals(true, julian.years().isSupported());
        TestCase.assertEquals(true, julian.weekyears().isSupported());
        TestCase.assertEquals(true, julian.months().isSupported());
        TestCase.assertEquals(true, julian.weeks().isSupported());
        TestCase.assertEquals(true, julian.days().isSupported());
        TestCase.assertEquals(true, julian.halfdays().isSupported());
        TestCase.assertEquals(true, julian.hours().isSupported());
        TestCase.assertEquals(true, julian.minutes().isSupported());
        TestCase.assertEquals(true, julian.seconds().isSupported());
        TestCase.assertEquals(true, julian.millis().isSupported());
        TestCase.assertEquals(false, julian.centuries().isPrecise());
        TestCase.assertEquals(false, julian.years().isPrecise());
        TestCase.assertEquals(false, julian.weekyears().isPrecise());
        TestCase.assertEquals(false, julian.months().isPrecise());
        TestCase.assertEquals(false, julian.weeks().isPrecise());
        TestCase.assertEquals(false, julian.days().isPrecise());
        TestCase.assertEquals(false, julian.halfdays().isPrecise());
        TestCase.assertEquals(true, julian.hours().isPrecise());
        TestCase.assertEquals(true, julian.minutes().isPrecise());
        TestCase.assertEquals(true, julian.seconds().isPrecise());
        TestCase.assertEquals(true, julian.millis().isPrecise());
        final JulianChronology julianUTC = JulianChronology.getInstanceUTC();
        TestCase.assertEquals(false, julianUTC.centuries().isPrecise());
        TestCase.assertEquals(false, julianUTC.years().isPrecise());
        TestCase.assertEquals(false, julianUTC.weekyears().isPrecise());
        TestCase.assertEquals(false, julianUTC.months().isPrecise());
        TestCase.assertEquals(true, julianUTC.weeks().isPrecise());
        TestCase.assertEquals(true, julianUTC.days().isPrecise());
        TestCase.assertEquals(true, julianUTC.halfdays().isPrecise());
        TestCase.assertEquals(true, julianUTC.hours().isPrecise());
        TestCase.assertEquals(true, julianUTC.minutes().isPrecise());
        TestCase.assertEquals(true, julianUTC.seconds().isPrecise());
        TestCase.assertEquals(true, julianUTC.millis().isPrecise());
        final DateTimeZone gmt = DateTimeZone.forID("Etc/GMT");
        final JulianChronology julianGMT = JulianChronology.getInstance(gmt);
        TestCase.assertEquals(false, julianGMT.centuries().isPrecise());
        TestCase.assertEquals(false, julianGMT.years().isPrecise());
        TestCase.assertEquals(false, julianGMT.weekyears().isPrecise());
        TestCase.assertEquals(false, julianGMT.months().isPrecise());
        TestCase.assertEquals(true, julianGMT.weeks().isPrecise());
        TestCase.assertEquals(true, julianGMT.days().isPrecise());
        TestCase.assertEquals(true, julianGMT.halfdays().isPrecise());
        TestCase.assertEquals(true, julianGMT.hours().isPrecise());
        TestCase.assertEquals(true, julianGMT.minutes().isPrecise());
        TestCase.assertEquals(true, julianGMT.seconds().isPrecise());
        TestCase.assertEquals(true, julianGMT.millis().isPrecise());
    }

    public void testDateFields() {
        final JulianChronology julian = JulianChronology.getInstance();
        TestCase.assertEquals("era", julian.era().getName());
        TestCase.assertEquals("centuryOfEra", julian.centuryOfEra().getName());
        TestCase.assertEquals("yearOfCentury", julian.yearOfCentury().getName());
        TestCase.assertEquals("yearOfEra", julian.yearOfEra().getName());
        TestCase.assertEquals("year", julian.year().getName());
        TestCase.assertEquals("monthOfYear", julian.monthOfYear().getName());
        TestCase.assertEquals("weekyearOfCentury", julian.weekyearOfCentury().getName());
        TestCase.assertEquals("weekyear", julian.weekyear().getName());
        TestCase.assertEquals("weekOfWeekyear", julian.weekOfWeekyear().getName());
        TestCase.assertEquals("dayOfYear", julian.dayOfYear().getName());
        TestCase.assertEquals("dayOfMonth", julian.dayOfMonth().getName());
        TestCase.assertEquals("dayOfWeek", julian.dayOfWeek().getName());
        TestCase.assertEquals(true, julian.era().isSupported());
        TestCase.assertEquals(true, julian.centuryOfEra().isSupported());
        TestCase.assertEquals(true, julian.yearOfCentury().isSupported());
        TestCase.assertEquals(true, julian.yearOfEra().isSupported());
        TestCase.assertEquals(true, julian.year().isSupported());
        TestCase.assertEquals(true, julian.monthOfYear().isSupported());
        TestCase.assertEquals(true, julian.weekyearOfCentury().isSupported());
        TestCase.assertEquals(true, julian.weekyear().isSupported());
        TestCase.assertEquals(true, julian.weekOfWeekyear().isSupported());
        TestCase.assertEquals(true, julian.dayOfYear().isSupported());
        TestCase.assertEquals(true, julian.dayOfMonth().isSupported());
        TestCase.assertEquals(true, julian.dayOfWeek().isSupported());
        TestCase.assertEquals(julian.eras(), julian.era().getDurationField());
        TestCase.assertEquals(julian.centuries(), julian.centuryOfEra().getDurationField());
        TestCase.assertEquals(julian.years(), julian.yearOfCentury().getDurationField());
        TestCase.assertEquals(julian.years(), julian.yearOfEra().getDurationField());
        TestCase.assertEquals(julian.years(), julian.year().getDurationField());
        TestCase.assertEquals(julian.months(), julian.monthOfYear().getDurationField());
        TestCase.assertEquals(julian.weekyears(), julian.weekyearOfCentury().getDurationField());
        TestCase.assertEquals(julian.weekyears(), julian.weekyear().getDurationField());
        TestCase.assertEquals(julian.weeks(), julian.weekOfWeekyear().getDurationField());
        TestCase.assertEquals(julian.days(), julian.dayOfYear().getDurationField());
        TestCase.assertEquals(julian.days(), julian.dayOfMonth().getDurationField());
        TestCase.assertEquals(julian.days(), julian.dayOfWeek().getDurationField());
        TestCase.assertEquals(null, julian.era().getRangeDurationField());
        TestCase.assertEquals(julian.eras(), julian.centuryOfEra().getRangeDurationField());
        TestCase.assertEquals(julian.centuries(), julian.yearOfCentury().getRangeDurationField());
        TestCase.assertEquals(julian.eras(), julian.yearOfEra().getRangeDurationField());
        TestCase.assertEquals(null, julian.year().getRangeDurationField());
        TestCase.assertEquals(julian.years(), julian.monthOfYear().getRangeDurationField());
        TestCase.assertEquals(julian.centuries(), julian.weekyearOfCentury().getRangeDurationField());
        TestCase.assertEquals(null, julian.weekyear().getRangeDurationField());
        TestCase.assertEquals(julian.weekyears(), julian.weekOfWeekyear().getRangeDurationField());
        TestCase.assertEquals(julian.years(), julian.dayOfYear().getRangeDurationField());
        TestCase.assertEquals(julian.months(), julian.dayOfMonth().getRangeDurationField());
        TestCase.assertEquals(julian.weeks(), julian.dayOfWeek().getRangeDurationField());
    }

    public void testTimeFields() {
        final JulianChronology julian = JulianChronology.getInstance();
        TestCase.assertEquals("halfdayOfDay", julian.halfdayOfDay().getName());
        TestCase.assertEquals("clockhourOfHalfday", julian.clockhourOfHalfday().getName());
        TestCase.assertEquals("hourOfHalfday", julian.hourOfHalfday().getName());
        TestCase.assertEquals("clockhourOfDay", julian.clockhourOfDay().getName());
        TestCase.assertEquals("hourOfDay", julian.hourOfDay().getName());
        TestCase.assertEquals("minuteOfDay", julian.minuteOfDay().getName());
        TestCase.assertEquals("minuteOfHour", julian.minuteOfHour().getName());
        TestCase.assertEquals("secondOfDay", julian.secondOfDay().getName());
        TestCase.assertEquals("secondOfMinute", julian.secondOfMinute().getName());
        TestCase.assertEquals("millisOfDay", julian.millisOfDay().getName());
        TestCase.assertEquals("millisOfSecond", julian.millisOfSecond().getName());
        TestCase.assertEquals(true, julian.halfdayOfDay().isSupported());
        TestCase.assertEquals(true, julian.clockhourOfHalfday().isSupported());
        TestCase.assertEquals(true, julian.hourOfHalfday().isSupported());
        TestCase.assertEquals(true, julian.clockhourOfDay().isSupported());
        TestCase.assertEquals(true, julian.hourOfDay().isSupported());
        TestCase.assertEquals(true, julian.minuteOfDay().isSupported());
        TestCase.assertEquals(true, julian.minuteOfHour().isSupported());
        TestCase.assertEquals(true, julian.secondOfDay().isSupported());
        TestCase.assertEquals(true, julian.secondOfMinute().isSupported());
        TestCase.assertEquals(true, julian.millisOfDay().isSupported());
        TestCase.assertEquals(true, julian.millisOfSecond().isSupported());
    }

    public void testLeap_28feb() {
        Chronology chrono = JulianChronology.getInstance();
        DateTime dt = new DateTime(2012, 2, 28, 0, 0, chrono);
        TestCase.assertEquals(true, dt.year().isLeap());
        TestCase.assertEquals(true, dt.monthOfYear().isLeap());
        TestCase.assertEquals(false, dt.dayOfMonth().isLeap());
        TestCase.assertEquals(false, dt.dayOfYear().isLeap());
    }

    public void testLeap_29feb() {
        Chronology chrono = JulianChronology.getInstance();
        DateTime dt = new DateTime(2012, 2, 29, 0, 0, chrono);
        TestCase.assertEquals(true, dt.year().isLeap());
        TestCase.assertEquals(true, dt.monthOfYear().isLeap());
        TestCase.assertEquals(true, dt.dayOfMonth().isLeap());
        TestCase.assertEquals(true, dt.dayOfYear().isLeap());
    }
}

