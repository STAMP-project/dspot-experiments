/**
 * Copyright 2001-2005 Stephen Colebourne
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
package org.joda.time.convert;


import StringConverter.INSTANCE;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Locale;
import junit.framework.TestCase;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableInterval;
import org.joda.time.MutablePeriod;
import org.joda.time.PeriodType;
import org.joda.time.TimeOfDay;
import org.joda.time.chrono.BuddhistChronology;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.chrono.JulianChronology;


/**
 * This class is a Junit unit test for StringConverter.
 *
 * @author Stephen Colebourne
 */
public class TestStringConverter extends TestCase {
    private static final DateTimeZone ONE_HOUR = DateTimeZone.forOffsetHours(1);

    private static final DateTimeZone SIX = DateTimeZone.forOffsetHours(6);

    private static final DateTimeZone SEVEN = DateTimeZone.forOffsetHours(7);

    private static final DateTimeZone EIGHT = DateTimeZone.forOffsetHours(8);

    private static final DateTimeZone UTC = DateTimeZone.UTC;

    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final Chronology ISO_EIGHT = ISOChronology.getInstance(TestStringConverter.EIGHT);

    private static final Chronology ISO_PARIS = ISOChronology.getInstance(TestStringConverter.PARIS);

    private static final Chronology ISO_LONDON = ISOChronology.getInstance(TestStringConverter.LONDON);

    private static Chronology ISO;

    private static Chronology JULIAN;

    private DateTimeZone zone = null;

    private Locale locale = null;

    public TestStringConverter(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testSingleton() throws Exception {
        Class cls = StringConverter.class;
        TestCase.assertEquals(false, Modifier.isPublic(cls.getModifiers()));
        TestCase.assertEquals(false, Modifier.isProtected(cls.getModifiers()));
        TestCase.assertEquals(false, Modifier.isPrivate(cls.getModifiers()));
        Constructor con = cls.getDeclaredConstructor(((Class[]) (null)));
        TestCase.assertEquals(1, cls.getDeclaredConstructors().length);
        TestCase.assertEquals(true, Modifier.isProtected(con.getModifiers()));
        Field fld = cls.getDeclaredField("INSTANCE");
        TestCase.assertEquals(false, Modifier.isPublic(fld.getModifiers()));
        TestCase.assertEquals(false, Modifier.isProtected(fld.getModifiers()));
        TestCase.assertEquals(false, Modifier.isPrivate(fld.getModifiers()));
    }

    // -----------------------------------------------------------------------
    public void testSupportedType() throws Exception {
        TestCase.assertEquals(String.class, INSTANCE.getSupportedType());
    }

    // -----------------------------------------------------------------------
    public void testGetInstantMillis_Object() throws Exception {
        DateTime dt = new DateTime(2004, 6, 9, 12, 24, 48, 501, TestStringConverter.EIGHT);
        TestCase.assertEquals(dt.getMillis(), INSTANCE.getInstantMillis("2004-06-09T12:24:48.501+08:00", TestStringConverter.ISO_EIGHT));
        dt = new DateTime(2004, 1, 1, 0, 0, 0, 0, TestStringConverter.EIGHT);
        TestCase.assertEquals(dt.getMillis(), INSTANCE.getInstantMillis("2004T+08:00", TestStringConverter.ISO_EIGHT));
        dt = new DateTime(2004, 6, 1, 0, 0, 0, 0, TestStringConverter.EIGHT);
        TestCase.assertEquals(dt.getMillis(), INSTANCE.getInstantMillis("2004-06T+08:00", TestStringConverter.ISO_EIGHT));
        dt = new DateTime(2004, 6, 9, 0, 0, 0, 0, TestStringConverter.EIGHT);
        TestCase.assertEquals(dt.getMillis(), INSTANCE.getInstantMillis("2004-06-09T+08:00", TestStringConverter.ISO_EIGHT));
        dt = new DateTime(2004, 6, 9, 0, 0, 0, 0, TestStringConverter.EIGHT);
        TestCase.assertEquals(dt.getMillis(), INSTANCE.getInstantMillis("2004-161T+08:00", TestStringConverter.ISO_EIGHT));
        dt = new DateTime(2004, 6, 9, 0, 0, 0, 0, TestStringConverter.EIGHT);
        TestCase.assertEquals(dt.getMillis(), INSTANCE.getInstantMillis("2004-W24-3T+08:00", TestStringConverter.ISO_EIGHT));
        dt = new DateTime(2004, 6, 7, 0, 0, 0, 0, TestStringConverter.EIGHT);
        TestCase.assertEquals(dt.getMillis(), INSTANCE.getInstantMillis("2004-W24T+08:00", TestStringConverter.ISO_EIGHT));
        dt = new DateTime(2004, 6, 9, 12, 0, 0, 0, TestStringConverter.EIGHT);
        TestCase.assertEquals(dt.getMillis(), INSTANCE.getInstantMillis("2004-06-09T12+08:00", TestStringConverter.ISO_EIGHT));
        dt = new DateTime(2004, 6, 9, 12, 24, 0, 0, TestStringConverter.EIGHT);
        TestCase.assertEquals(dt.getMillis(), INSTANCE.getInstantMillis("2004-06-09T12:24+08:00", TestStringConverter.ISO_EIGHT));
        dt = new DateTime(2004, 6, 9, 12, 24, 48, 0, TestStringConverter.EIGHT);
        TestCase.assertEquals(dt.getMillis(), INSTANCE.getInstantMillis("2004-06-09T12:24:48+08:00", TestStringConverter.ISO_EIGHT));
        dt = new DateTime(2004, 6, 9, 12, 30, 0, 0, TestStringConverter.EIGHT);
        TestCase.assertEquals(dt.getMillis(), INSTANCE.getInstantMillis("2004-06-09T12.5+08:00", TestStringConverter.ISO_EIGHT));
        dt = new DateTime(2004, 6, 9, 12, 24, 30, 0, TestStringConverter.EIGHT);
        TestCase.assertEquals(dt.getMillis(), INSTANCE.getInstantMillis("2004-06-09T12:24.5+08:00", TestStringConverter.ISO_EIGHT));
        dt = new DateTime(2004, 6, 9, 12, 24, 48, 500, TestStringConverter.EIGHT);
        TestCase.assertEquals(dt.getMillis(), INSTANCE.getInstantMillis("2004-06-09T12:24:48.5+08:00", TestStringConverter.ISO_EIGHT));
        dt = new DateTime(2004, 6, 9, 12, 24, 48, 501);
        TestCase.assertEquals(dt.getMillis(), INSTANCE.getInstantMillis("2004-06-09T12:24:48.501", TestStringConverter.ISO));
    }

    public void testGetInstantMillis_Object_Zone() throws Exception {
        DateTime dt = new DateTime(2004, 6, 9, 12, 24, 48, 501, TestStringConverter.PARIS);
        TestCase.assertEquals(dt.getMillis(), INSTANCE.getInstantMillis("2004-06-09T12:24:48.501+02:00", TestStringConverter.ISO_PARIS));
        dt = new DateTime(2004, 6, 9, 12, 24, 48, 501, TestStringConverter.PARIS);
        TestCase.assertEquals(dt.getMillis(), INSTANCE.getInstantMillis("2004-06-09T12:24:48.501", TestStringConverter.ISO_PARIS));
        dt = new DateTime(2004, 6, 9, 12, 24, 48, 501, TestStringConverter.LONDON);
        TestCase.assertEquals(dt.getMillis(), INSTANCE.getInstantMillis("2004-06-09T12:24:48.501+01:00", TestStringConverter.ISO_LONDON));
        dt = new DateTime(2004, 6, 9, 12, 24, 48, 501, TestStringConverter.LONDON);
        TestCase.assertEquals(dt.getMillis(), INSTANCE.getInstantMillis("2004-06-09T12:24:48.501", TestStringConverter.ISO_LONDON));
    }

    public void testGetInstantMillis_Object_Chronology() throws Exception {
        DateTime dt = new DateTime(2004, 6, 9, 12, 24, 48, 501, JulianChronology.getInstance(TestStringConverter.LONDON));
        TestCase.assertEquals(dt.getMillis(), INSTANCE.getInstantMillis("2004-06-09T12:24:48.501+01:00", TestStringConverter.JULIAN));
    }

    public void testGetInstantMillisInvalid() {
        try {
            INSTANCE.getInstantMillis("", ((Chronology) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            INSTANCE.getInstantMillis("X", ((Chronology) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testGetChronology_Object_Zone() throws Exception {
        TestCase.assertEquals(ISOChronology.getInstance(TestStringConverter.PARIS), INSTANCE.getChronology("2004-06-09T12:24:48.501+01:00", TestStringConverter.PARIS));
        TestCase.assertEquals(ISOChronology.getInstance(TestStringConverter.PARIS), INSTANCE.getChronology("2004-06-09T12:24:48.501", TestStringConverter.PARIS));
        TestCase.assertEquals(ISOChronology.getInstance(TestStringConverter.LONDON), INSTANCE.getChronology("2004-06-09T12:24:48.501+01:00", ((DateTimeZone) (null))));
        TestCase.assertEquals(ISOChronology.getInstance(TestStringConverter.LONDON), INSTANCE.getChronology("2004-06-09T12:24:48.501", ((DateTimeZone) (null))));
    }

    public void testGetChronology_Object_Chronology() throws Exception {
        TestCase.assertEquals(JulianChronology.getInstance(TestStringConverter.LONDON), INSTANCE.getChronology("2004-06-09T12:24:48.501+01:00", TestStringConverter.JULIAN));
        TestCase.assertEquals(JulianChronology.getInstance(TestStringConverter.LONDON), INSTANCE.getChronology("2004-06-09T12:24:48.501", TestStringConverter.JULIAN));
        TestCase.assertEquals(ISOChronology.getInstance(TestStringConverter.LONDON), INSTANCE.getChronology("2004-06-09T12:24:48.501+01:00", ((Chronology) (null))));
        TestCase.assertEquals(ISOChronology.getInstance(TestStringConverter.LONDON), INSTANCE.getChronology("2004-06-09T12:24:48.501", ((Chronology) (null))));
    }

    // -----------------------------------------------------------------------
    public void testGetPartialValues() throws Exception {
        TimeOfDay tod = new TimeOfDay();
        int[] expected = new int[]{ 3, 4, 5, 6 };
        int[] actual = INSTANCE.getPartialValues(tod, "T03:04:05.006", ISOChronology.getInstance());
        TestCase.assertEquals(true, Arrays.equals(expected, actual));
    }

    // -----------------------------------------------------------------------
    public void testGetDateTime() throws Exception {
        DateTime base = new DateTime(2004, 6, 9, 12, 24, 48, 501, TestStringConverter.PARIS);
        DateTime test = new DateTime(base.toString(), TestStringConverter.PARIS);
        TestCase.assertEquals(base, test);
    }

    public void testGetDateTime1() throws Exception {
        DateTime test = new DateTime("2004-06-09T12:24:48.501+01:00");
        TestCase.assertEquals(2004, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        TestCase.assertEquals(12, test.getHourOfDay());
        TestCase.assertEquals(24, test.getMinuteOfHour());
        TestCase.assertEquals(48, test.getSecondOfMinute());
        TestCase.assertEquals(501, test.getMillisOfSecond());
        TestCase.assertEquals(TestStringConverter.LONDON, test.getZone());
    }

    public void testGetDateTime2() throws Exception {
        DateTime test = new DateTime("2004-06-09T12:24:48.501");
        TestCase.assertEquals(2004, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        TestCase.assertEquals(12, test.getHourOfDay());
        TestCase.assertEquals(24, test.getMinuteOfHour());
        TestCase.assertEquals(48, test.getSecondOfMinute());
        TestCase.assertEquals(501, test.getMillisOfSecond());
        TestCase.assertEquals(TestStringConverter.LONDON, test.getZone());
    }

    public void testGetDateTime3() throws Exception {
        DateTime test = new DateTime("2004-06-09T12:24:48.501+02:00", TestStringConverter.PARIS);
        TestCase.assertEquals(2004, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        TestCase.assertEquals(12, test.getHourOfDay());
        TestCase.assertEquals(24, test.getMinuteOfHour());
        TestCase.assertEquals(48, test.getSecondOfMinute());
        TestCase.assertEquals(501, test.getMillisOfSecond());
        TestCase.assertEquals(TestStringConverter.PARIS, test.getZone());
    }

    public void testGetDateTime4() throws Exception {
        DateTime test = new DateTime("2004-06-09T12:24:48.501", TestStringConverter.PARIS);
        TestCase.assertEquals(2004, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        TestCase.assertEquals(12, test.getHourOfDay());
        TestCase.assertEquals(24, test.getMinuteOfHour());
        TestCase.assertEquals(48, test.getSecondOfMinute());
        TestCase.assertEquals(501, test.getMillisOfSecond());
        TestCase.assertEquals(TestStringConverter.PARIS, test.getZone());
    }

    public void testGetDateTime5() throws Exception {
        DateTime test = new DateTime("2004-06-09T12:24:48.501+02:00", JulianChronology.getInstance(TestStringConverter.PARIS));
        TestCase.assertEquals(2004, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        TestCase.assertEquals(12, test.getHourOfDay());
        TestCase.assertEquals(24, test.getMinuteOfHour());
        TestCase.assertEquals(48, test.getSecondOfMinute());
        TestCase.assertEquals(501, test.getMillisOfSecond());
        TestCase.assertEquals(TestStringConverter.PARIS, test.getZone());
    }

    public void testGetDateTime6() throws Exception {
        DateTime test = new DateTime("2004-06-09T12:24:48.501", JulianChronology.getInstance(TestStringConverter.PARIS));
        TestCase.assertEquals(2004, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        TestCase.assertEquals(12, test.getHourOfDay());
        TestCase.assertEquals(24, test.getMinuteOfHour());
        TestCase.assertEquals(48, test.getSecondOfMinute());
        TestCase.assertEquals(501, test.getMillisOfSecond());
        TestCase.assertEquals(TestStringConverter.PARIS, test.getZone());
    }

    // -----------------------------------------------------------------------
    public void testGetDurationMillis_Object1() throws Exception {
        long millis = INSTANCE.getDurationMillis("PT12.345S");
        TestCase.assertEquals(12345, millis);
        millis = INSTANCE.getDurationMillis("pt12.345s");
        TestCase.assertEquals(12345, millis);
        millis = INSTANCE.getDurationMillis("pt12s");
        TestCase.assertEquals(12000, millis);
        millis = INSTANCE.getDurationMillis("pt12.s");
        TestCase.assertEquals(12000, millis);
        millis = INSTANCE.getDurationMillis("pt-12.32s");
        TestCase.assertEquals((-12320), millis);
        millis = INSTANCE.getDurationMillis("pt-0.32s");
        TestCase.assertEquals((-320), millis);
        millis = INSTANCE.getDurationMillis("pt-0.0s");
        TestCase.assertEquals(0, millis);
        millis = INSTANCE.getDurationMillis("pt0.0s");
        TestCase.assertEquals(0, millis);
        millis = INSTANCE.getDurationMillis("pt12.3456s");
        TestCase.assertEquals(12345, millis);
    }

    public void testGetDurationMillis_Object2() throws Exception {
        try {
            INSTANCE.getDurationMillis("P2Y6M9DXYZ");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            INSTANCE.getDurationMillis("PTS");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            INSTANCE.getDurationMillis("XT0S");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            INSTANCE.getDurationMillis("PX0S");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            INSTANCE.getDurationMillis("PT0X");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            INSTANCE.getDurationMillis("PTXS");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            INSTANCE.getDurationMillis("PT0.0.0S");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            INSTANCE.getDurationMillis("PT0-00S");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            INSTANCE.getDurationMillis("PT-.001S");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testGetPeriodType_Object() throws Exception {
        TestCase.assertEquals(PeriodType.standard(), INSTANCE.getPeriodType("P2Y6M9D"));
    }

    public void testSetIntoPeriod_Object1() throws Exception {
        MutablePeriod m = new MutablePeriod(PeriodType.yearMonthDayTime());
        INSTANCE.setInto(m, "P2Y6M9DT12H24M48S", null);
        TestCase.assertEquals(2, m.getYears());
        TestCase.assertEquals(6, m.getMonths());
        TestCase.assertEquals(9, m.getDays());
        TestCase.assertEquals(12, m.getHours());
        TestCase.assertEquals(24, m.getMinutes());
        TestCase.assertEquals(48, m.getSeconds());
        TestCase.assertEquals(0, m.getMillis());
    }

    public void testSetIntoPeriod_Object2() throws Exception {
        MutablePeriod m = new MutablePeriod(PeriodType.yearWeekDayTime());
        INSTANCE.setInto(m, "P2Y4W3DT12H24M48S", null);
        TestCase.assertEquals(2, m.getYears());
        TestCase.assertEquals(4, m.getWeeks());
        TestCase.assertEquals(3, m.getDays());
        TestCase.assertEquals(12, m.getHours());
        TestCase.assertEquals(24, m.getMinutes());
        TestCase.assertEquals(48, m.getSeconds());
        TestCase.assertEquals(0, m.getMillis());
    }

    public void testSetIntoPeriod_Object3() throws Exception {
        MutablePeriod m = new MutablePeriod(PeriodType.yearWeekDayTime());
        INSTANCE.setInto(m, "P2Y4W3DT12H24M48.034S", null);
        TestCase.assertEquals(2, m.getYears());
        TestCase.assertEquals(4, m.getWeeks());
        TestCase.assertEquals(3, m.getDays());
        TestCase.assertEquals(12, m.getHours());
        TestCase.assertEquals(24, m.getMinutes());
        TestCase.assertEquals(48, m.getSeconds());
        TestCase.assertEquals(34, m.getMillis());
    }

    public void testSetIntoPeriod_Object4() throws Exception {
        MutablePeriod m = new MutablePeriod(PeriodType.yearWeekDayTime());
        INSTANCE.setInto(m, "P2Y4W3DT12H24M.056S", null);
        TestCase.assertEquals(2, m.getYears());
        TestCase.assertEquals(4, m.getWeeks());
        TestCase.assertEquals(3, m.getDays());
        TestCase.assertEquals(12, m.getHours());
        TestCase.assertEquals(24, m.getMinutes());
        TestCase.assertEquals(0, m.getSeconds());
        TestCase.assertEquals(56, m.getMillis());
    }

    public void testSetIntoPeriod_Object5() throws Exception {
        MutablePeriod m = new MutablePeriod(PeriodType.yearWeekDayTime());
        INSTANCE.setInto(m, "P2Y4W3DT12H24M56.S", null);
        TestCase.assertEquals(2, m.getYears());
        TestCase.assertEquals(4, m.getWeeks());
        TestCase.assertEquals(3, m.getDays());
        TestCase.assertEquals(12, m.getHours());
        TestCase.assertEquals(24, m.getMinutes());
        TestCase.assertEquals(56, m.getSeconds());
        TestCase.assertEquals(0, m.getMillis());
    }

    public void testSetIntoPeriod_Object6() throws Exception {
        MutablePeriod m = new MutablePeriod(PeriodType.yearWeekDayTime());
        INSTANCE.setInto(m, "P2Y4W3DT12H24M56.1234567S", null);
        TestCase.assertEquals(2, m.getYears());
        TestCase.assertEquals(4, m.getWeeks());
        TestCase.assertEquals(3, m.getDays());
        TestCase.assertEquals(12, m.getHours());
        TestCase.assertEquals(24, m.getMinutes());
        TestCase.assertEquals(56, m.getSeconds());
        TestCase.assertEquals(123, m.getMillis());
    }

    public void testSetIntoPeriod_Object7() throws Exception {
        MutablePeriod m = new MutablePeriod(1, 0, 1, 1, 1, 1, 1, 1, PeriodType.yearWeekDayTime());
        INSTANCE.setInto(m, "P2Y4W3D", null);
        TestCase.assertEquals(2, m.getYears());
        TestCase.assertEquals(4, m.getWeeks());
        TestCase.assertEquals(3, m.getDays());
        TestCase.assertEquals(0, m.getHours());
        TestCase.assertEquals(0, m.getMinutes());
        TestCase.assertEquals(0, m.getSeconds());
        TestCase.assertEquals(0, m.getMillis());
    }

    public void testSetIntoPeriod_Object8() throws Exception {
        MutablePeriod m = new MutablePeriod();
        try {
            INSTANCE.setInto(m, "", null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            INSTANCE.setInto(m, "PXY", null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            INSTANCE.setInto(m, "PT0SXY", null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            INSTANCE.setInto(m, "P2Y4W3DT12H24M48SX", null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testIsReadableInterval_Object_Chronology() throws Exception {
        TestCase.assertEquals(false, INSTANCE.isReadableInterval("", null));
    }

    public void testSetIntoInterval_Object_Chronology1() throws Exception {
        MutableInterval m = new MutableInterval((-1000L), 1000L);
        INSTANCE.setInto(m, "2004-06-09/P1Y2M", null);
        TestCase.assertEquals(new DateTime(2004, 6, 9, 0, 0, 0, 0), m.getStart());
        TestCase.assertEquals(new DateTime(2005, 8, 9, 0, 0, 0, 0), m.getEnd());
        TestCase.assertEquals(ISOChronology.getInstance(), m.getChronology());
    }

    public void testSetIntoInterval_Object_Chronology2() throws Exception {
        MutableInterval m = new MutableInterval((-1000L), 1000L);
        INSTANCE.setInto(m, "P1Y2M/2004-06-09", null);
        TestCase.assertEquals(new DateTime(2003, 4, 9, 0, 0, 0, 0), m.getStart());
        TestCase.assertEquals(new DateTime(2004, 6, 9, 0, 0, 0, 0), m.getEnd());
        TestCase.assertEquals(ISOChronology.getInstance(), m.getChronology());
    }

    public void testSetIntoInterval_Object_Chronology3() throws Exception {
        MutableInterval m = new MutableInterval((-1000L), 1000L);
        INSTANCE.setInto(m, "2003-08-09/2004-06-09", null);
        TestCase.assertEquals(new DateTime(2003, 8, 9, 0, 0, 0, 0), m.getStart());
        TestCase.assertEquals(new DateTime(2004, 6, 9, 0, 0, 0, 0), m.getEnd());
        TestCase.assertEquals(ISOChronology.getInstance(), m.getChronology());
    }

    public void testSetIntoInterval_Object_Chronology4() throws Exception {
        MutableInterval m = new MutableInterval((-1000L), 1000L);
        INSTANCE.setInto(m, "2004-06-09T+06:00/P1Y2M", null);
        TestCase.assertEquals(withChronology(null), m.getStart());
        TestCase.assertEquals(withChronology(null), m.getEnd());
        TestCase.assertEquals(ISOChronology.getInstance(), m.getChronology());
    }

    public void testSetIntoInterval_Object_Chronology5() throws Exception {
        MutableInterval m = new MutableInterval((-1000L), 1000L);
        INSTANCE.setInto(m, "P1Y2M/2004-06-09T+06:00", null);
        TestCase.assertEquals(withChronology(null), m.getStart());
        TestCase.assertEquals(withChronology(null), m.getEnd());
        TestCase.assertEquals(ISOChronology.getInstance(), m.getChronology());
    }

    public void testSetIntoInterval_Object_Chronology6() throws Exception {
        MutableInterval m = new MutableInterval((-1000L), 1000L);
        INSTANCE.setInto(m, "2003-08-09T+06:00/2004-06-09T+07:00", null);
        TestCase.assertEquals(withChronology(null), m.getStart());
        TestCase.assertEquals(withChronology(null), m.getEnd());
        TestCase.assertEquals(ISOChronology.getInstance(), m.getChronology());
    }

    public void testSetIntoInterval_Object_Chronology7() throws Exception {
        MutableInterval m = new MutableInterval((-1000L), 1000L);
        INSTANCE.setInto(m, "2003-08-09/2004-06-09", BuddhistChronology.getInstance());
        TestCase.assertEquals(new DateTime(2003, 8, 9, 0, 0, 0, 0, BuddhistChronology.getInstance()), m.getStart());
        TestCase.assertEquals(new DateTime(2004, 6, 9, 0, 0, 0, 0, BuddhistChronology.getInstance()), m.getEnd());
        TestCase.assertEquals(BuddhistChronology.getInstance(), m.getChronology());
    }

    public void testSetIntoInterval_Object_Chronology8() throws Exception {
        MutableInterval m = new MutableInterval((-1000L), 1000L);
        INSTANCE.setInto(m, "2003-08-09T+06:00/2004-06-09T+07:00", BuddhistChronology.getInstance(TestStringConverter.EIGHT));
        TestCase.assertEquals(new DateTime(2003, 8, 9, 0, 0, 0, 0, BuddhistChronology.getInstance(TestStringConverter.SIX)).withZone(TestStringConverter.EIGHT), m.getStart());
        TestCase.assertEquals(new DateTime(2004, 6, 9, 0, 0, 0, 0, BuddhistChronology.getInstance(TestStringConverter.SEVEN)).withZone(TestStringConverter.EIGHT), m.getEnd());
        TestCase.assertEquals(BuddhistChronology.getInstance(TestStringConverter.EIGHT), m.getChronology());
    }

    public void testSetIntoIntervalEx_Object_Chronology1() throws Exception {
        MutableInterval m = new MutableInterval((-1000L), 1000L);
        try {
            INSTANCE.setInto(m, "", null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testSetIntoIntervalEx_Object_Chronology2() throws Exception {
        MutableInterval m = new MutableInterval((-1000L), 1000L);
        try {
            INSTANCE.setInto(m, "/", null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testSetIntoIntervalEx_Object_Chronology3() throws Exception {
        MutableInterval m = new MutableInterval((-1000L), 1000L);
        try {
            INSTANCE.setInto(m, "P1Y/", null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testSetIntoIntervalEx_Object_Chronology4() throws Exception {
        MutableInterval m = new MutableInterval((-1000L), 1000L);
        try {
            INSTANCE.setInto(m, "/P1Y", null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testSetIntoIntervalEx_Object_Chronology5() throws Exception {
        MutableInterval m = new MutableInterval((-1000L), 1000L);
        try {
            INSTANCE.setInto(m, "P1Y/P2Y", null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testToString() {
        TestCase.assertEquals("Converter[java.lang.String]", INSTANCE.toString());
    }
}

