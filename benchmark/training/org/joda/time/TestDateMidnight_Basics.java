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
package org.joda.time;


import DateTimeConstants.BC;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import junit.framework.TestCase;
import org.joda.time.base.AbstractInstant;
import org.joda.time.chrono.BuddhistChronology;
import org.joda.time.chrono.CopticChronology;
import org.joda.time.chrono.GJChronology;
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.field.UnsupportedDateTimeField;
import org.joda.time.field.UnsupportedDurationField;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static DateTimeConstants.MILLIS_PER_DAY;
import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeConstants.MILLIS_PER_MINUTE;


/**
 * This class is a Junit unit test for DateMidnight.
 *
 * @author Stephen Colebourne
 */
@SuppressWarnings("deprecation")
public class TestDateMidnight_Basics extends TestCase {
    // Test in 2002/03 as time zones are more well known
    // (before the late 90's they were all over the place)
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final DateTimeZone NEWYORK = DateTimeZone.forID("America/New_York");

    // the default time zone is set to LONDON in setUp()
    // we have to hard code LONDON here (instead of ISOChronology.getInstance() etc.)
    // as TestAll sets up a different time zone for better all-round testing
    private static final ISOChronology ISO_DEFAULT = ISOChronology.getInstance(TestDateMidnight_Basics.LONDON);

    private static final ISOChronology ISO_PARIS = ISOChronology.getInstance(TestDateMidnight_Basics.PARIS);

    private static final GJChronology GJ_DEFAULT = GJChronology.getInstance(TestDateMidnight_Basics.LONDON);

    private static final GregorianChronology GREGORIAN_DEFAULT = GregorianChronology.getInstance(TestDateMidnight_Basics.LONDON);

    private static final GregorianChronology GREGORIAN_PARIS = GregorianChronology.getInstance(TestDateMidnight_Basics.PARIS);

    private static final BuddhistChronology BUDDHIST_DEFAULT = BuddhistChronology.getInstance(TestDateMidnight_Basics.LONDON);

    private static final CopticChronology COPTIC_DEFAULT = CopticChronology.getInstance(TestDateMidnight_Basics.LONDON);

    long y2002days = ((((((((((((((((((((((((((((((365 + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365;

    long y2003days = (((((((((((((((((((((((((((((((365 + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365;

    // 2002-06-09
    private long TEST_TIME_NOW_UTC = ((((((((y2002days) + 31L) + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (MILLIS_PER_DAY);

    private long TEST_TIME_NOW_LONDON = (TEST_TIME_NOW_UTC) - (MILLIS_PER_HOUR);

    // private long TEST_TIME_NOW_PARIS =
    // TEST_TIME_NOW_UTC - 2*DateTimeConstants.MILLIS_PER_HOUR;
    // 2002-04-05
    private long TEST_TIME1_UTC = ((((((((y2002days) + 31L) + 28L) + 31L) + 5L) - 1L) * (MILLIS_PER_DAY)) + (12L * (MILLIS_PER_HOUR))) + (24L * (MILLIS_PER_MINUTE));

    private long TEST_TIME1_LONDON = (((((((y2002days) + 31L) + 28L) + 31L) + 5L) - 1L) * (MILLIS_PER_DAY)) - (MILLIS_PER_HOUR);

    private long TEST_TIME1_PARIS = (((((((y2002days) + 31L) + 28L) + 31L) + 5L) - 1L) * (MILLIS_PER_DAY)) - (2 * (MILLIS_PER_HOUR));

    // 2003-05-06
    private long TEST_TIME2_UTC = (((((((((y2003days) + 31L) + 28L) + 31L) + 30L) + 6L) - 1L) * (MILLIS_PER_DAY)) + (14L * (MILLIS_PER_HOUR))) + (28L * (MILLIS_PER_MINUTE));

    private long TEST_TIME2_LONDON = ((((((((y2003days) + 31L) + 28L) + 31L) + 30L) + 6L) - 1L) * (MILLIS_PER_DAY)) - (MILLIS_PER_HOUR);

    private long TEST_TIME2_PARIS = ((((((((y2003days) + 31L) + 28L) + 31L) + 30L) + 6L) - 1L) * (MILLIS_PER_DAY)) - (2 * (MILLIS_PER_HOUR));

    private DateTimeZone originalDateTimeZone = null;

    private TimeZone originalTimeZone = null;

    private Locale originalLocale = null;

    public TestDateMidnight_Basics(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testTest() {
        TestCase.assertEquals("2002-06-09T00:00:00.000Z", new Instant(TEST_TIME_NOW_UTC).toString());
        TestCase.assertEquals("2002-04-05T12:24:00.000Z", new Instant(TEST_TIME1_UTC).toString());
        TestCase.assertEquals("2003-05-06T14:28:00.000Z", new Instant(TEST_TIME2_UTC).toString());
    }

    // -----------------------------------------------------------------------
    public void testGet_DateTimeField() {
        DateMidnight test = new DateMidnight();
        TestCase.assertEquals(1, test.get(TestDateMidnight_Basics.ISO_DEFAULT.era()));
        TestCase.assertEquals(20, test.get(TestDateMidnight_Basics.ISO_DEFAULT.centuryOfEra()));
        TestCase.assertEquals(2, test.get(TestDateMidnight_Basics.ISO_DEFAULT.yearOfCentury()));
        TestCase.assertEquals(2002, test.get(TestDateMidnight_Basics.ISO_DEFAULT.yearOfEra()));
        TestCase.assertEquals(2002, test.get(TestDateMidnight_Basics.ISO_DEFAULT.year()));
        TestCase.assertEquals(6, test.get(TestDateMidnight_Basics.ISO_DEFAULT.monthOfYear()));
        TestCase.assertEquals(9, test.get(TestDateMidnight_Basics.ISO_DEFAULT.dayOfMonth()));
        TestCase.assertEquals(2002, test.get(TestDateMidnight_Basics.ISO_DEFAULT.weekyear()));
        TestCase.assertEquals(23, test.get(TestDateMidnight_Basics.ISO_DEFAULT.weekOfWeekyear()));
        TestCase.assertEquals(7, test.get(TestDateMidnight_Basics.ISO_DEFAULT.dayOfWeek()));
        TestCase.assertEquals(160, test.get(TestDateMidnight_Basics.ISO_DEFAULT.dayOfYear()));
        TestCase.assertEquals(0, test.get(TestDateMidnight_Basics.ISO_DEFAULT.halfdayOfDay()));
        TestCase.assertEquals(0, test.get(TestDateMidnight_Basics.ISO_DEFAULT.hourOfHalfday()));
        TestCase.assertEquals(24, test.get(TestDateMidnight_Basics.ISO_DEFAULT.clockhourOfDay()));
        TestCase.assertEquals(12, test.get(TestDateMidnight_Basics.ISO_DEFAULT.clockhourOfHalfday()));
        TestCase.assertEquals(0, test.get(TestDateMidnight_Basics.ISO_DEFAULT.hourOfDay()));
        TestCase.assertEquals(0, test.get(TestDateMidnight_Basics.ISO_DEFAULT.minuteOfHour()));
        TestCase.assertEquals(0, test.get(TestDateMidnight_Basics.ISO_DEFAULT.minuteOfDay()));
        TestCase.assertEquals(0, test.get(TestDateMidnight_Basics.ISO_DEFAULT.secondOfMinute()));
        TestCase.assertEquals(0, test.get(TestDateMidnight_Basics.ISO_DEFAULT.secondOfDay()));
        TestCase.assertEquals(0, test.get(TestDateMidnight_Basics.ISO_DEFAULT.millisOfSecond()));
        TestCase.assertEquals(0, test.get(TestDateMidnight_Basics.ISO_DEFAULT.millisOfDay()));
        try {
            test.get(((DateTimeField) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testGet_DateTimeFieldType() {
        DateMidnight test = new DateMidnight();
        TestCase.assertEquals(1, test.get(DateTimeFieldType.era()));
        TestCase.assertEquals(20, test.get(DateTimeFieldType.centuryOfEra()));
        TestCase.assertEquals(2, test.get(DateTimeFieldType.yearOfCentury()));
        TestCase.assertEquals(2002, test.get(DateTimeFieldType.yearOfEra()));
        TestCase.assertEquals(2002, test.get(DateTimeFieldType.year()));
        TestCase.assertEquals(6, test.get(DateTimeFieldType.monthOfYear()));
        TestCase.assertEquals(9, test.get(DateTimeFieldType.dayOfMonth()));
        TestCase.assertEquals(2002, test.get(DateTimeFieldType.weekyear()));
        TestCase.assertEquals(23, test.get(DateTimeFieldType.weekOfWeekyear()));
        TestCase.assertEquals(7, test.get(DateTimeFieldType.dayOfWeek()));
        TestCase.assertEquals(160, test.get(DateTimeFieldType.dayOfYear()));
        TestCase.assertEquals(0, test.get(DateTimeFieldType.halfdayOfDay()));
        TestCase.assertEquals(0, test.get(DateTimeFieldType.hourOfHalfday()));
        TestCase.assertEquals(24, test.get(DateTimeFieldType.clockhourOfDay()));
        TestCase.assertEquals(12, test.get(DateTimeFieldType.clockhourOfHalfday()));
        TestCase.assertEquals(0, test.get(DateTimeFieldType.hourOfDay()));
        TestCase.assertEquals(0, test.get(DateTimeFieldType.minuteOfHour()));
        TestCase.assertEquals(0, test.get(DateTimeFieldType.minuteOfDay()));
        TestCase.assertEquals(0, test.get(DateTimeFieldType.secondOfMinute()));
        TestCase.assertEquals(0, test.get(DateTimeFieldType.secondOfDay()));
        TestCase.assertEquals(0, test.get(DateTimeFieldType.millisOfSecond()));
        TestCase.assertEquals(0, test.get(DateTimeFieldType.millisOfDay()));
        try {
            test.get(((DateTimeFieldType) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testGetters() {
        DateMidnight test = new DateMidnight();
        TestCase.assertEquals(TestDateMidnight_Basics.ISO_DEFAULT, test.getChronology());
        TestCase.assertEquals(TestDateMidnight_Basics.LONDON, test.getZone());
        TestCase.assertEquals(TEST_TIME_NOW_LONDON, test.getMillis());
        TestCase.assertEquals(1, test.getEra());
        TestCase.assertEquals(20, test.getCenturyOfEra());
        TestCase.assertEquals(2, test.getYearOfCentury());
        TestCase.assertEquals(2002, test.getYearOfEra());
        TestCase.assertEquals(2002, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        TestCase.assertEquals(2002, test.getWeekyear());
        TestCase.assertEquals(23, test.getWeekOfWeekyear());
        TestCase.assertEquals(7, test.getDayOfWeek());
        TestCase.assertEquals(160, test.getDayOfYear());
        TestCase.assertEquals(0, test.getHourOfDay());
        TestCase.assertEquals(0, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getMinuteOfDay());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getSecondOfDay());
        TestCase.assertEquals(0, test.getMillisOfSecond());
        TestCase.assertEquals(0, test.getMillisOfDay());
    }

    public void testWithers() {
        DateMidnight test = new DateMidnight(1970, 6, 9, TestDateMidnight_Basics.GJ_DEFAULT);
        check(test.withYear(2000), 2000, 6, 9);
        check(test.withMonthOfYear(2), 1970, 2, 9);
        check(test.withDayOfMonth(2), 1970, 6, 2);
        check(test.withDayOfYear(6), 1970, 1, 6);
        check(test.withDayOfWeek(6), 1970, 6, 13);
        check(test.withWeekOfWeekyear(6), 1970, 2, 3);
        check(test.withWeekyear(1971), 1971, 6, 15);
        check(test.withYearOfCentury(60), 1960, 6, 9);
        check(test.withCenturyOfEra(21), 2070, 6, 9);
        check(test.withYearOfEra(1066), 1066, 6, 9);
        check(test.withEra(BC), (-1970), 6, 9);
        try {
            test.withMonthOfYear(0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            test.withMonthOfYear(13);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testEqualsHashCode() {
        DateMidnight test1 = new DateMidnight(TEST_TIME1_UTC);
        DateMidnight test2 = new DateMidnight(TEST_TIME1_UTC);
        TestCase.assertEquals(true, test1.equals(test2));
        TestCase.assertEquals(true, test2.equals(test1));
        TestCase.assertEquals(true, test1.equals(test1));
        TestCase.assertEquals(true, test2.equals(test2));
        TestCase.assertEquals(true, ((test1.hashCode()) == (test2.hashCode())));
        TestCase.assertEquals(true, ((test1.hashCode()) == (test1.hashCode())));
        TestCase.assertEquals(true, ((test2.hashCode()) == (test2.hashCode())));
        DateMidnight test3 = new DateMidnight(TEST_TIME2_UTC);
        TestCase.assertEquals(false, test1.equals(test3));
        TestCase.assertEquals(false, test2.equals(test3));
        TestCase.assertEquals(false, test3.equals(test1));
        TestCase.assertEquals(false, test3.equals(test2));
        TestCase.assertEquals(false, ((test1.hashCode()) == (test3.hashCode())));
        TestCase.assertEquals(false, ((test2.hashCode()) == (test3.hashCode())));
        TestCase.assertEquals(false, test1.equals("Hello"));
        TestCase.assertEquals(true, test1.equals(new TestDateMidnight_Basics.MockInstant()));
        TestCase.assertEquals(false, test1.equals(new DateMidnight(TEST_TIME1_UTC, TestDateMidnight_Basics.GREGORIAN_DEFAULT)));
    }

    class MockInstant extends AbstractInstant {
        public String toString() {
            return null;
        }

        public long getMillis() {
            return TEST_TIME1_LONDON;
        }

        public Chronology getChronology() {
            return TestDateMidnight_Basics.ISO_DEFAULT;
        }
    }

    public void testCompareTo() {
        DateMidnight test1 = new DateMidnight(TEST_TIME1_UTC);
        DateMidnight test1a = new DateMidnight(TEST_TIME1_UTC);
        TestCase.assertEquals(0, test1.compareTo(test1a));
        TestCase.assertEquals(0, test1a.compareTo(test1));
        TestCase.assertEquals(0, test1.compareTo(test1));
        TestCase.assertEquals(0, test1a.compareTo(test1a));
        DateMidnight test2 = new DateMidnight(TEST_TIME2_UTC);
        TestCase.assertEquals((-1), test1.compareTo(test2));
        TestCase.assertEquals((+1), test2.compareTo(test1));
        DateMidnight test3 = new DateMidnight(TEST_TIME2_UTC, TestDateMidnight_Basics.GREGORIAN_PARIS);
        TestCase.assertEquals((-1), test1.compareTo(test3));
        TestCase.assertEquals((+1), test3.compareTo(test1));
        TestCase.assertEquals((-1), test3.compareTo(test2));// midnight paris before london

        TestCase.assertEquals((+1), test2.compareTo(new TestDateMidnight_Basics.MockInstant()));
        TestCase.assertEquals(0, test1.compareTo(new TestDateMidnight_Basics.MockInstant()));
        try {
            test1.compareTo(null);
            TestCase.fail();
        } catch (NullPointerException ex) {
        }
        // try {
        // test1.compareTo(new Date());
        // fail();
        // } catch (ClassCastException ex) {}
    }

    public void testIsEqual() {
        DateMidnight test1 = new DateMidnight(TEST_TIME1_UTC);
        DateMidnight test1a = new DateMidnight(TEST_TIME1_UTC);
        TestCase.assertEquals(true, test1.isEqual(test1a));
        TestCase.assertEquals(true, test1a.isEqual(test1));
        TestCase.assertEquals(true, test1.isEqual(test1));
        TestCase.assertEquals(true, test1a.isEqual(test1a));
        DateMidnight test2 = new DateMidnight(TEST_TIME2_UTC);
        TestCase.assertEquals(false, test1.isEqual(test2));
        TestCase.assertEquals(false, test2.isEqual(test1));
        DateMidnight test3 = new DateMidnight(TEST_TIME2_UTC, TestDateMidnight_Basics.GREGORIAN_PARIS);
        TestCase.assertEquals(false, test1.isEqual(test3));
        TestCase.assertEquals(false, test3.isEqual(test1));
        TestCase.assertEquals(false, test3.isEqual(test2));// midnight paris before london

        TestCase.assertEquals(false, test2.isEqual(new TestDateMidnight_Basics.MockInstant()));
        TestCase.assertEquals(true, test1.isEqual(new TestDateMidnight_Basics.MockInstant()));
        TestCase.assertEquals(false, isEqual(null));
        TestCase.assertEquals(true, isEqual(null));
        TestCase.assertEquals(false, isEqual(null));
        TestCase.assertEquals(false, new DateMidnight(2004, 6, 9).isEqual(new DateTime(2004, 6, 8, 23, 59, 59, 999)));
        TestCase.assertEquals(true, new DateMidnight(2004, 6, 9).isEqual(new DateTime(2004, 6, 9, 0, 0, 0, 0)));
        TestCase.assertEquals(false, new DateMidnight(2004, 6, 9).isEqual(new DateTime(2004, 6, 9, 0, 0, 0, 1)));
    }

    public void testIsBefore() {
        DateMidnight test1 = new DateMidnight(TEST_TIME1_UTC);
        DateMidnight test1a = new DateMidnight(TEST_TIME1_UTC);
        TestCase.assertEquals(false, test1.isBefore(test1a));
        TestCase.assertEquals(false, test1a.isBefore(test1));
        TestCase.assertEquals(false, test1.isBefore(test1));
        TestCase.assertEquals(false, test1a.isBefore(test1a));
        DateMidnight test2 = new DateMidnight(TEST_TIME2_UTC);
        TestCase.assertEquals(true, test1.isBefore(test2));
        TestCase.assertEquals(false, test2.isBefore(test1));
        DateMidnight test3 = new DateMidnight(TEST_TIME2_UTC, TestDateMidnight_Basics.GREGORIAN_PARIS);
        TestCase.assertEquals(true, test1.isBefore(test3));
        TestCase.assertEquals(false, test3.isBefore(test1));
        TestCase.assertEquals(true, test3.isBefore(test2));// midnight paris before london

        TestCase.assertEquals(false, test2.isBefore(new TestDateMidnight_Basics.MockInstant()));
        TestCase.assertEquals(false, test1.isBefore(new TestDateMidnight_Basics.MockInstant()));
        TestCase.assertEquals(false, isBefore(null));
        TestCase.assertEquals(false, isBefore(null));
        TestCase.assertEquals(true, isBefore(null));
        TestCase.assertEquals(false, new DateMidnight(2004, 6, 9).isBefore(new DateTime(2004, 6, 8, 23, 59, 59, 999)));
        TestCase.assertEquals(false, new DateMidnight(2004, 6, 9).isBefore(new DateTime(2004, 6, 9, 0, 0, 0, 0)));
        TestCase.assertEquals(true, new DateMidnight(2004, 6, 9).isBefore(new DateTime(2004, 6, 9, 0, 0, 0, 1)));
    }

    public void testIsAfter() {
        DateMidnight test1 = new DateMidnight(TEST_TIME1_UTC);
        DateMidnight test1a = new DateMidnight(TEST_TIME1_UTC);
        TestCase.assertEquals(false, test1.isAfter(test1a));
        TestCase.assertEquals(false, test1a.isAfter(test1));
        TestCase.assertEquals(false, test1.isAfter(test1));
        TestCase.assertEquals(false, test1a.isAfter(test1a));
        DateMidnight test2 = new DateMidnight(TEST_TIME2_UTC);
        TestCase.assertEquals(false, test1.isAfter(test2));
        TestCase.assertEquals(true, test2.isAfter(test1));
        DateMidnight test3 = new DateMidnight(TEST_TIME2_UTC, TestDateMidnight_Basics.GREGORIAN_PARIS);
        TestCase.assertEquals(false, test1.isAfter(test3));
        TestCase.assertEquals(true, test3.isAfter(test1));
        TestCase.assertEquals(false, test3.isAfter(test2));// midnight paris before london

        TestCase.assertEquals(true, test2.isAfter(new TestDateMidnight_Basics.MockInstant()));
        TestCase.assertEquals(false, test1.isAfter(new TestDateMidnight_Basics.MockInstant()));
        TestCase.assertEquals(true, isAfter(null));
        TestCase.assertEquals(false, isAfter(null));
        TestCase.assertEquals(false, isAfter(null));
        TestCase.assertEquals(true, new DateMidnight(2004, 6, 9).isAfter(new DateTime(2004, 6, 8, 23, 59, 59, 999)));
        TestCase.assertEquals(false, new DateMidnight(2004, 6, 9).isAfter(new DateTime(2004, 6, 9, 0, 0, 0, 0)));
        TestCase.assertEquals(false, new DateMidnight(2004, 6, 9).isAfter(new DateTime(2004, 6, 9, 0, 0, 0, 1)));
    }

    // -----------------------------------------------------------------------
    public void testSerialization() throws Exception {
        DateMidnight test = new DateMidnight(TEST_TIME_NOW_UTC);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        DateMidnight result = ((DateMidnight) (ois.readObject()));
        ois.close();
        TestCase.assertEquals(test, result);
    }

    // -----------------------------------------------------------------------
    public void testToString() {
        DateMidnight test = new DateMidnight(TEST_TIME_NOW_UTC);
        TestCase.assertEquals("2002-06-09T00:00:00.000+01:00", test.toString());
        test = new DateMidnight(TEST_TIME_NOW_UTC, TestDateMidnight_Basics.PARIS);
        TestCase.assertEquals("2002-06-09T00:00:00.000+02:00", test.toString());
        test = new DateMidnight(TEST_TIME_NOW_UTC, TestDateMidnight_Basics.NEWYORK);
        TestCase.assertEquals("2002-06-08T00:00:00.000-04:00", test.toString());// the 8th

    }

    public void testToString_String() {
        DateMidnight test = new DateMidnight(TEST_TIME_NOW_UTC);
        TestCase.assertEquals("2002 00", test.toString("yyyy HH"));
        TestCase.assertEquals("2002-06-09T00:00:00.000+01:00", test.toString(((String) (null))));
    }

    public void testToString_String_String() {
        DateMidnight test = new DateMidnight(TEST_TIME_NOW_UTC);
        TestCase.assertEquals("Sun 9/6", test.toString("EEE d/M", Locale.ENGLISH));
        TestCase.assertEquals("dim. 9/6", test.toString("EEE d/M", Locale.FRENCH));
        TestCase.assertEquals("2002-06-09T00:00:00.000+01:00", test.toString(null, Locale.ENGLISH));
        TestCase.assertEquals("Sun 9/6", test.toString("EEE d/M", null));
        TestCase.assertEquals("2002-06-09T00:00:00.000+01:00", test.toString(null, null));
    }

    public void testToString_DTFormatter() {
        DateMidnight test = new DateMidnight(TEST_TIME_NOW_UTC);
        TestCase.assertEquals("2002 00", test.toString(DateTimeFormat.forPattern("yyyy HH")));
        TestCase.assertEquals("2002-06-09T00:00:00.000+01:00", test.toString(((DateTimeFormatter) (null))));
    }

    // -----------------------------------------------------------------------
    public void testToInstant() {
        DateMidnight test = new DateMidnight(TEST_TIME1_UTC);
        Instant result = test.toInstant();
        TestCase.assertEquals(TEST_TIME1_LONDON, result.getMillis());
    }

    public void testToDateTime() {
        DateMidnight test = new DateMidnight(TEST_TIME1_UTC, TestDateMidnight_Basics.PARIS);
        DateTime result = test.toDateTime();
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TEST_TIME1_PARIS, result.getMillis());
        TestCase.assertEquals(TestDateMidnight_Basics.PARIS, result.getZone());
    }

    public void testToDateTimeISO() {
        DateMidnight test = new DateMidnight(TEST_TIME1_UTC, TestDateMidnight_Basics.PARIS);
        DateTime result = test.toDateTimeISO();
        TestCase.assertSame(DateTime.class, result.getClass());
        TestCase.assertSame(ISOChronology.class, result.getChronology().getClass());
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateMidnight_Basics.ISO_PARIS, result.getChronology());
    }

    public void testToDateTime_DateTimeZone() {
        DateMidnight test = new DateMidnight(TEST_TIME1_UTC);
        DateTime result = test.toDateTime(TestDateMidnight_Basics.LONDON);
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TEST_TIME1_LONDON, result.getMillis());
        TestCase.assertEquals(TestDateMidnight_Basics.LONDON, result.getZone());
        test = new DateMidnight(TEST_TIME1_UTC);
        result = test.toDateTime(TestDateMidnight_Basics.PARIS);
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TEST_TIME1_LONDON, result.getMillis());
        TestCase.assertEquals(TestDateMidnight_Basics.PARIS, result.getZone());
        test = new DateMidnight(TEST_TIME1_UTC, TestDateMidnight_Basics.PARIS);
        result = test.toDateTime(((DateTimeZone) (null)));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TEST_TIME1_PARIS, result.getMillis());
        TestCase.assertEquals(TestDateMidnight_Basics.LONDON, result.getZone());
        test = new DateMidnight(TEST_TIME1_UTC);
        result = test.toDateTime(((DateTimeZone) (null)));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TEST_TIME1_LONDON, result.getMillis());
        TestCase.assertEquals(TestDateMidnight_Basics.LONDON, result.getZone());
    }

    public void testToDateTime_Chronology() {
        DateMidnight test = new DateMidnight(TEST_TIME1_UTC);
        DateTime result = test.toDateTime(TestDateMidnight_Basics.ISO_DEFAULT);
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TEST_TIME1_LONDON, result.getMillis());
        TestCase.assertEquals(TestDateMidnight_Basics.LONDON, result.getZone());
        test = new DateMidnight(TEST_TIME1_UTC);
        result = test.toDateTime(TestDateMidnight_Basics.GREGORIAN_PARIS);
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TEST_TIME1_LONDON, result.getMillis());
        TestCase.assertEquals(TestDateMidnight_Basics.GREGORIAN_PARIS, result.getChronology());
        test = new DateMidnight(TEST_TIME1_UTC, TestDateMidnight_Basics.GREGORIAN_PARIS);
        result = test.toDateTime(((Chronology) (null)));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TEST_TIME1_PARIS, result.getMillis());
        TestCase.assertEquals(TestDateMidnight_Basics.ISO_DEFAULT, result.getChronology());
        test = new DateMidnight(TEST_TIME1_UTC);
        result = test.toDateTime(((Chronology) (null)));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TEST_TIME1_LONDON, result.getMillis());
        TestCase.assertEquals(TestDateMidnight_Basics.ISO_DEFAULT, result.getChronology());
    }

    public void testToMutableDateTime() {
        DateMidnight test = new DateMidnight(TEST_TIME1_UTC, TestDateMidnight_Basics.PARIS);
        MutableDateTime result = test.toMutableDateTime();
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateMidnight_Basics.ISO_PARIS, result.getChronology());
    }

    public void testToMutableDateTimeISO() {
        DateMidnight test = new DateMidnight(TEST_TIME1_UTC, TestDateMidnight_Basics.PARIS);
        MutableDateTime result = test.toMutableDateTimeISO();
        TestCase.assertSame(MutableDateTime.class, result.getClass());
        TestCase.assertSame(ISOChronology.class, result.getChronology().getClass());
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateMidnight_Basics.ISO_PARIS, result.getChronology());
    }

    public void testToMutableDateTime_DateTimeZone() {
        DateMidnight test = new DateMidnight(TEST_TIME1_UTC);
        MutableDateTime result = test.toMutableDateTime(TestDateMidnight_Basics.LONDON);
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateMidnight_Basics.ISO_DEFAULT, result.getChronology());
        test = new DateMidnight(TEST_TIME1_UTC);
        result = test.toMutableDateTime(TestDateMidnight_Basics.PARIS);
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateMidnight_Basics.ISO_PARIS, result.getChronology());
        test = new DateMidnight(TEST_TIME1_UTC, TestDateMidnight_Basics.PARIS);
        result = test.toMutableDateTime(((DateTimeZone) (null)));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateMidnight_Basics.ISO_DEFAULT, result.getChronology());
        test = new DateMidnight(TEST_TIME1_UTC);
        result = test.toMutableDateTime(((DateTimeZone) (null)));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateMidnight_Basics.ISO_DEFAULT, result.getChronology());
    }

    public void testToMutableDateTime_Chronology() {
        DateMidnight test = new DateMidnight(TEST_TIME1_UTC);
        MutableDateTime result = test.toMutableDateTime(TestDateMidnight_Basics.ISO_DEFAULT);
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateMidnight_Basics.ISO_DEFAULT, result.getChronology());
        test = new DateMidnight(TEST_TIME1_UTC);
        result = test.toMutableDateTime(TestDateMidnight_Basics.GREGORIAN_PARIS);
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateMidnight_Basics.GREGORIAN_PARIS, result.getChronology());
        test = new DateMidnight(TEST_TIME1_UTC, TestDateMidnight_Basics.GREGORIAN_PARIS);
        result = test.toMutableDateTime(((Chronology) (null)));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateMidnight_Basics.ISO_DEFAULT, result.getChronology());
        test = new DateMidnight(TEST_TIME1_UTC);
        result = test.toMutableDateTime(((Chronology) (null)));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateMidnight_Basics.ISO_DEFAULT, result.getChronology());
    }

    public void testToDate() {
        DateMidnight test = new DateMidnight(TEST_TIME1_UTC);
        Date result = test.toDate();
        TestCase.assertEquals(test.getMillis(), result.getTime());
    }

    public void testToCalendar_Locale() {
        DateMidnight test = new DateMidnight(TEST_TIME1_UTC);
        Calendar result = test.toCalendar(null);
        TestCase.assertEquals(test.getMillis(), result.getTime().getTime());
        TestCase.assertEquals(TimeZone.getTimeZone("Europe/London"), result.getTimeZone());
        test = new DateMidnight(TEST_TIME1_UTC, TestDateMidnight_Basics.PARIS);
        result = test.toCalendar(null);
        TestCase.assertEquals(test.getMillis(), result.getTime().getTime());
        TestCase.assertEquals(TimeZone.getTimeZone("Europe/Paris"), result.getTimeZone());
        test = new DateMidnight(TEST_TIME1_UTC, TestDateMidnight_Basics.PARIS);
        result = test.toCalendar(Locale.UK);
        TestCase.assertEquals(test.getMillis(), result.getTime().getTime());
        TestCase.assertEquals(TimeZone.getTimeZone("Europe/Paris"), result.getTimeZone());
    }

    public void testToGregorianCalendar() {
        DateMidnight test = new DateMidnight(TEST_TIME1_UTC);
        GregorianCalendar result = test.toGregorianCalendar();
        TestCase.assertEquals(test.getMillis(), result.getTime().getTime());
        TestCase.assertEquals(TimeZone.getTimeZone("Europe/London"), result.getTimeZone());
        test = new DateMidnight(TEST_TIME1_UTC, TestDateMidnight_Basics.PARIS);
        result = test.toGregorianCalendar();
        TestCase.assertEquals(test.getMillis(), result.getTime().getTime());
        TestCase.assertEquals(TimeZone.getTimeZone("Europe/Paris"), result.getTimeZone());
    }

    // -----------------------------------------------------------------------
    public void testToYearMonthDay() {
        DateMidnight base = new DateMidnight(TEST_TIME1_UTC, TestDateMidnight_Basics.COPTIC_DEFAULT);
        YearMonthDay test = base.toYearMonthDay();
        TestCase.assertEquals(new YearMonthDay(TEST_TIME1_UTC, TestDateMidnight_Basics.COPTIC_DEFAULT), test);
    }

    public void testToLocalDate() {
        DateMidnight base = new DateMidnight(TEST_TIME1_UTC, TestDateMidnight_Basics.COPTIC_DEFAULT);
        LocalDate test = base.toLocalDate();
        TestCase.assertEquals(new LocalDate(TEST_TIME1_UTC, TestDateMidnight_Basics.COPTIC_DEFAULT), test);
    }

    public void testToInterval() {
        DateMidnight base = new DateMidnight(TEST_TIME1_UTC, TestDateMidnight_Basics.COPTIC_DEFAULT);
        Interval test = base.toInterval();
        DateMidnight end = base.plus(Period.days(1));
        TestCase.assertEquals(new Interval(base, end), test);
    }

    // -----------------------------------------------------------------------
    public void testWithMillis_long() {
        DateMidnight test = new DateMidnight(TEST_TIME1_UTC);
        DateMidnight result = test.withMillis(TEST_TIME2_UTC);
        TestCase.assertEquals(TEST_TIME2_LONDON, result.getMillis());
        TestCase.assertEquals(test.getChronology(), result.getChronology());
        test = new DateMidnight(TEST_TIME1_UTC, TestDateMidnight_Basics.GREGORIAN_PARIS);
        result = test.withMillis(TEST_TIME2_UTC);
        TestCase.assertEquals(TEST_TIME2_PARIS, result.getMillis());
        TestCase.assertEquals(test.getChronology(), result.getChronology());
        test = new DateMidnight(TEST_TIME1_UTC);
        result = test.withMillis(TEST_TIME1_UTC);
        TestCase.assertSame(test, result);
    }

    public void testWithChronology_Chronology() {
        DateMidnight test = new DateMidnight(TEST_TIME1_UTC);
        DateMidnight result = test.withChronology(TestDateMidnight_Basics.GREGORIAN_PARIS);
        TestCase.assertEquals(TEST_TIME1_LONDON, test.getMillis());
        TestCase.assertEquals(TEST_TIME1_PARIS, result.getMillis());
        TestCase.assertEquals(TestDateMidnight_Basics.GREGORIAN_PARIS, result.getChronology());
        test = new DateMidnight(TEST_TIME1_UTC, TestDateMidnight_Basics.GREGORIAN_PARIS);
        result = test.withChronology(null);
        TestCase.assertEquals(TEST_TIME1_PARIS, test.getMillis());
        // midnight Paris is previous day in London
        TestCase.assertEquals(((TEST_TIME1_LONDON) - (MILLIS_PER_DAY)), result.getMillis());
        TestCase.assertEquals(TestDateMidnight_Basics.ISO_DEFAULT, result.getChronology());
        test = new DateMidnight(TEST_TIME1_UTC);
        result = test.withChronology(null);
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateMidnight_Basics.ISO_DEFAULT, result.getChronology());
        test = new DateMidnight(TEST_TIME1_UTC);
        result = test.withChronology(TestDateMidnight_Basics.ISO_DEFAULT);
        TestCase.assertSame(test, result);
    }

    public void testWithZoneRetainFields_DateTimeZone() {
        DateMidnight test = new DateMidnight(TEST_TIME1_UTC);
        DateMidnight result = test.withZoneRetainFields(TestDateMidnight_Basics.PARIS);
        TestCase.assertEquals(TEST_TIME1_LONDON, test.getMillis());
        TestCase.assertEquals(TEST_TIME1_PARIS, result.getMillis());
        TestCase.assertEquals(TestDateMidnight_Basics.ISO_PARIS, result.getChronology());
        test = new DateMidnight(TEST_TIME1_UTC, TestDateMidnight_Basics.GREGORIAN_PARIS);
        result = test.withZoneRetainFields(null);
        TestCase.assertEquals(TEST_TIME1_PARIS, test.getMillis());
        TestCase.assertEquals(TEST_TIME1_LONDON, result.getMillis());
        TestCase.assertEquals(TestDateMidnight_Basics.GREGORIAN_DEFAULT, result.getChronology());
        test = new DateMidnight(TEST_TIME1_UTC);
        result = test.withZoneRetainFields(TestDateMidnight_Basics.LONDON);
        TestCase.assertSame(test, result);
        test = new DateMidnight(TEST_TIME1_UTC);
        result = test.withZoneRetainFields(null);
        TestCase.assertSame(test, result);
        test = new DateMidnight(TEST_TIME1_UTC, new MockNullZoneChronology());
        result = test.withZoneRetainFields(TestDateMidnight_Basics.LONDON);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testWithFields_RPartial() {
        DateMidnight test = new DateMidnight(2004, 5, 6);
        DateMidnight result = test.withFields(new YearMonthDay(2003, 4, 5));
        DateMidnight expected = new DateMidnight(2003, 4, 5);
        TestCase.assertEquals(expected, result);
        test = new DateMidnight(TEST_TIME1_UTC);
        result = test.withFields(null);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testWithField1() {
        DateMidnight test = new DateMidnight(2004, 6, 9);
        DateMidnight result = test.withField(DateTimeFieldType.year(), 2006);
        TestCase.assertEquals(new DateMidnight(2004, 6, 9), test);
        TestCase.assertEquals(new DateMidnight(2006, 6, 9), result);
    }

    public void testWithField2() {
        DateMidnight test = new DateMidnight(2004, 6, 9);
        try {
            test.withField(null, 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testWithFieldAdded1() {
        DateMidnight test = new DateMidnight(2004, 6, 9);
        DateMidnight result = test.withFieldAdded(DurationFieldType.years(), 6);
        TestCase.assertEquals(new DateMidnight(2004, 6, 9), test);
        TestCase.assertEquals(new DateMidnight(2010, 6, 9), result);
    }

    public void testWithFieldAdded2() {
        DateMidnight test = new DateMidnight(2004, 6, 9);
        try {
            test.withFieldAdded(null, 0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithFieldAdded3() {
        DateMidnight test = new DateMidnight(2004, 6, 9);
        try {
            test.withFieldAdded(null, 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithFieldAdded4() {
        DateMidnight test = new DateMidnight(2004, 6, 9);
        DateMidnight result = test.withFieldAdded(DurationFieldType.years(), 0);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testWithDurationAdded_long_int() {
        DateMidnight test = new DateMidnight(TEST_TIME1_UTC, TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        DateMidnight result = test.withDurationAdded(123456789L, 1);
        DateMidnight expected = new DateMidnight(((test.getMillis()) + 123456789L), TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.withDurationAdded(123456789L, 0);
        TestCase.assertSame(test, result);
        result = test.withDurationAdded(123456789L, 2);
        expected = new DateMidnight(((test.getMillis()) + (2L * 123456789L)), TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.withDurationAdded(123456789L, (-3));
        expected = new DateMidnight(((test.getMillis()) - (3L * 123456789L)), TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
    }

    // -----------------------------------------------------------------------
    public void testWithDurationAdded_RD_int() {
        DateMidnight test = new DateMidnight(TEST_TIME1_UTC, TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        DateMidnight result = test.withDurationAdded(new Duration(123456789L), 1);
        DateMidnight expected = new DateMidnight(((test.getMillis()) + 123456789L), TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.withDurationAdded(null, 1);
        TestCase.assertSame(test, result);
        result = test.withDurationAdded(new Duration(123456789L), 0);
        TestCase.assertSame(test, result);
        result = test.withDurationAdded(new Duration(123456789L), 2);
        expected = new DateMidnight(((test.getMillis()) + (2L * 123456789L)), TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.withDurationAdded(new Duration(123456789L), (-3));
        expected = new DateMidnight(((test.getMillis()) - (3L * 123456789L)), TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
    }

    // -----------------------------------------------------------------------
    public void testWithDurationAdded_RP_int() {
        DateMidnight test = new DateMidnight(2002, 5, 3, TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        DateMidnight result = test.withPeriodAdded(new Period(1, 2, 3, 4, 5, 6, 7, 8), 1);
        DateMidnight expected = new DateMidnight(2003, 7, 28, TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.withPeriodAdded(null, 1);
        TestCase.assertSame(test, result);
        result = test.withPeriodAdded(new Period(1, 2, 3, 4, 5, 6, 7, 8), 0);
        TestCase.assertSame(test, result);
        result = test.withPeriodAdded(new Period(1, 2, 0, 4, 5, 6, 7, 8), 3);
        expected = new DateMidnight(2005, 11, 15, TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.withPeriodAdded(new Period(1, 2, 0, 1, 1, 2, 3, 4), (-1));
        expected = new DateMidnight(2001, 3, 1, TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
    }

    // -----------------------------------------------------------------------
    public void testPlus_long() {
        DateMidnight test = new DateMidnight(TEST_TIME1_UTC, TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        DateMidnight result = test.plus(123456789L);
        DateMidnight expected = new DateMidnight(((test.getMillis()) + 123456789L), TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
    }

    public void testPlus_RD() {
        DateMidnight test = new DateMidnight(TEST_TIME1_UTC, TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        DateMidnight result = test.plus(new Duration(123456789L));
        DateMidnight expected = new DateMidnight(((test.getMillis()) + 123456789L), TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.plus(((ReadableDuration) (null)));
        TestCase.assertSame(test, result);
    }

    public void testPlus_RP() {
        DateMidnight test = new DateMidnight(2002, 5, 3, TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        DateMidnight result = test.plus(new Period(1, 2, 3, 4, 5, 6, 7, 8));
        DateMidnight expected = new DateMidnight(2003, 7, 28, TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.plus(((ReadablePeriod) (null)));
        TestCase.assertSame(test, result);
    }

    public void testPlusYears_int() {
        DateMidnight test = new DateMidnight(2002, 5, 3, TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        DateMidnight result = test.plusYears(1);
        DateMidnight expected = new DateMidnight(2003, 5, 3, TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.plusYears(0);
        TestCase.assertSame(test, result);
    }

    public void testPlusMonths_int() {
        DateMidnight test = new DateMidnight(2002, 5, 3, TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        DateMidnight result = test.plusMonths(1);
        DateMidnight expected = new DateMidnight(2002, 6, 3, TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.plusMonths(0);
        TestCase.assertSame(test, result);
    }

    public void testPlusWeeks_int() {
        DateMidnight test = new DateMidnight(2002, 5, 3, TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        DateMidnight result = test.plusWeeks(1);
        DateMidnight expected = new DateMidnight(2002, 5, 10, TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.plusWeeks(0);
        TestCase.assertSame(test, result);
    }

    public void testPlusDays_int() {
        DateMidnight test = new DateMidnight(2002, 5, 3, TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        DateMidnight result = test.plusDays(1);
        DateMidnight expected = new DateMidnight(2002, 5, 4, TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.plusDays(0);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testMinus_long() {
        DateMidnight test = new DateMidnight(TEST_TIME1_UTC, TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        DateMidnight result = test.minus(123456789L);
        DateMidnight expected = new DateMidnight(((test.getMillis()) - 123456789L), TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
    }

    public void testMinus_RD() {
        DateMidnight test = new DateMidnight(TEST_TIME1_UTC, TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        DateMidnight result = test.minus(new Duration(123456789L));
        DateMidnight expected = new DateMidnight(((test.getMillis()) - 123456789L), TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.minus(((ReadableDuration) (null)));
        TestCase.assertSame(test, result);
    }

    public void testMinus_RP() {
        DateMidnight test = new DateMidnight(2002, 5, 3, TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        DateMidnight result = test.minus(new Period(1, 1, 1, 1, 1, 1, 1, 1));
        DateMidnight expected = new DateMidnight(2001, 3, 25, TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.minus(((ReadablePeriod) (null)));
        TestCase.assertSame(test, result);
    }

    public void testMinusYears_int() {
        DateMidnight test = new DateMidnight(2002, 5, 3, TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        DateMidnight result = test.minusYears(1);
        DateMidnight expected = new DateMidnight(2001, 5, 3, TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.minusYears(0);
        TestCase.assertSame(test, result);
    }

    public void testMinusMonths_int() {
        DateMidnight test = new DateMidnight(2002, 5, 3, TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        DateMidnight result = test.minusMonths(1);
        DateMidnight expected = new DateMidnight(2002, 4, 3, TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.minusMonths(0);
        TestCase.assertSame(test, result);
    }

    public void testMinusWeeks_int() {
        DateMidnight test = new DateMidnight(2002, 5, 3, TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        DateMidnight result = test.minusWeeks(1);
        DateMidnight expected = new DateMidnight(2002, 4, 26, TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.minusWeeks(0);
        TestCase.assertSame(test, result);
    }

    public void testMinusDays_int() {
        DateMidnight test = new DateMidnight(2002, 5, 3, TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        DateMidnight result = test.minusDays(1);
        DateMidnight expected = new DateMidnight(2002, 5, 2, TestDateMidnight_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.minusDays(0);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testProperty() {
        DateMidnight test = new DateMidnight();
        TestCase.assertEquals(test.year(), test.property(DateTimeFieldType.year()));
        TestCase.assertEquals(test.dayOfWeek(), test.property(DateTimeFieldType.dayOfWeek()));
        TestCase.assertEquals(test.weekOfWeekyear(), test.property(DateTimeFieldType.weekOfWeekyear()));
        TestCase.assertEquals(test.property(DateTimeFieldType.millisOfSecond()), test.property(DateTimeFieldType.millisOfSecond()));
        DateTimeFieldType bad = new DateTimeFieldType("bad") {
            private static final long serialVersionUID = 1L;

            public DurationFieldType getDurationType() {
                return DurationFieldType.weeks();
            }

            public DurationFieldType getRangeDurationType() {
                return null;
            }

            public DateTimeField getField(Chronology chronology) {
                return UnsupportedDateTimeField.getInstance(this, UnsupportedDurationField.getInstance(getDurationType()));
            }
        };
        try {
            test.property(bad);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            test.property(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }
}

