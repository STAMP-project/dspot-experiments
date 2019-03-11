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
import org.joda.time.chrono.BaseChronology;
import org.joda.time.chrono.BuddhistChronology;
import org.joda.time.chrono.CopticChronology;
import org.joda.time.chrono.GJChronology;
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.field.UnsupportedDateTimeField;
import org.joda.time.field.UnsupportedDurationField;

import static DateTimeConstants.MILLIS_PER_DAY;
import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeConstants.MILLIS_PER_MINUTE;


/**
 * This class is a Junit unit test for DateTime.
 *
 * @author Stephen Colebourne
 */
public class TestDateTime_Basics extends TestCase {
    // Test in 2002/03 as time zones are more well known
    // (before the late 90's they were all over the place)
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    // the default time zone is set to LONDON in setUp()
    // we have to hard code LONDON here (instead of ISOChronology.getInstance() etc.)
    // as TestAll sets up a different time zone for better all-round testing
    private static final ISOChronology ISO_UTC = ISOChronology.getInstanceUTC();

    private static final ISOChronology ISO_DEFAULT = ISOChronology.getInstance(TestDateTime_Basics.LONDON);

    private static final ISOChronology ISO_PARIS = ISOChronology.getInstance(TestDateTime_Basics.PARIS);

    private static final GJChronology GJ_DEFAULT = GJChronology.getInstance(TestDateTime_Basics.LONDON);

    private static final GregorianChronology GREGORIAN_DEFAULT = GregorianChronology.getInstance(TestDateTime_Basics.LONDON);

    private static final GregorianChronology GREGORIAN_PARIS = GregorianChronology.getInstance(TestDateTime_Basics.PARIS);

    private static final BuddhistChronology BUDDHIST_UTC = BuddhistChronology.getInstanceUTC();

    private static final BuddhistChronology BUDDHIST_DEFAULT = BuddhistChronology.getInstance(TestDateTime_Basics.LONDON);

    private static final CopticChronology COPTIC_DEFAULT = CopticChronology.getInstance(TestDateTime_Basics.LONDON);

    long y2002days = ((((((((((((((((((((((((((((((365 + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365;

    long y2003days = (((((((((((((((((((((((((((((((365 + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365;

    // 2002-06-09
    private long TEST_TIME_NOW = ((((((((y2002days) + 31L) + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (MILLIS_PER_DAY);

    // 2002-04-05
    private long TEST_TIME1 = ((((((((y2002days) + 31L) + 28L) + 31L) + 5L) - 1L) * (MILLIS_PER_DAY)) + (12L * (MILLIS_PER_HOUR))) + (24L * (MILLIS_PER_MINUTE));

    // 2003-05-06
    private long TEST_TIME2 = (((((((((y2003days) + 31L) + 28L) + 31L) + 30L) + 6L) - 1L) * (MILLIS_PER_DAY)) + (14L * (MILLIS_PER_HOUR))) + (28L * (MILLIS_PER_MINUTE));

    private DateTimeZone originalDateTimeZone = null;

    private TimeZone originalTimeZone = null;

    private Locale originalLocale = null;

    public TestDateTime_Basics(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testTest() {
        TestCase.assertEquals("2002-06-09T00:00:00.000Z", new Instant(TEST_TIME_NOW).toString());
        TestCase.assertEquals("2002-04-05T12:24:00.000Z", new Instant(TEST_TIME1).toString());
        TestCase.assertEquals("2003-05-06T14:28:00.000Z", new Instant(TEST_TIME2).toString());
    }

    // -----------------------------------------------------------------------
    public void testGet_DateTimeField() {
        DateTime test = new DateTime();
        TestCase.assertEquals(1, test.get(TestDateTime_Basics.ISO_DEFAULT.era()));
        TestCase.assertEquals(20, test.get(TestDateTime_Basics.ISO_DEFAULT.centuryOfEra()));
        TestCase.assertEquals(2, test.get(TestDateTime_Basics.ISO_DEFAULT.yearOfCentury()));
        TestCase.assertEquals(2002, test.get(TestDateTime_Basics.ISO_DEFAULT.yearOfEra()));
        TestCase.assertEquals(2002, test.get(TestDateTime_Basics.ISO_DEFAULT.year()));
        TestCase.assertEquals(6, test.get(TestDateTime_Basics.ISO_DEFAULT.monthOfYear()));
        TestCase.assertEquals(9, test.get(TestDateTime_Basics.ISO_DEFAULT.dayOfMonth()));
        TestCase.assertEquals(2002, test.get(TestDateTime_Basics.ISO_DEFAULT.weekyear()));
        TestCase.assertEquals(23, test.get(TestDateTime_Basics.ISO_DEFAULT.weekOfWeekyear()));
        TestCase.assertEquals(7, test.get(TestDateTime_Basics.ISO_DEFAULT.dayOfWeek()));
        TestCase.assertEquals(160, test.get(TestDateTime_Basics.ISO_DEFAULT.dayOfYear()));
        TestCase.assertEquals(0, test.get(TestDateTime_Basics.ISO_DEFAULT.halfdayOfDay()));
        TestCase.assertEquals(1, test.get(TestDateTime_Basics.ISO_DEFAULT.hourOfHalfday()));
        TestCase.assertEquals(1, test.get(TestDateTime_Basics.ISO_DEFAULT.clockhourOfDay()));
        TestCase.assertEquals(1, test.get(TestDateTime_Basics.ISO_DEFAULT.clockhourOfHalfday()));
        TestCase.assertEquals(1, test.get(TestDateTime_Basics.ISO_DEFAULT.hourOfDay()));
        TestCase.assertEquals(0, test.get(TestDateTime_Basics.ISO_DEFAULT.minuteOfHour()));
        TestCase.assertEquals(60, test.get(TestDateTime_Basics.ISO_DEFAULT.minuteOfDay()));
        TestCase.assertEquals(0, test.get(TestDateTime_Basics.ISO_DEFAULT.secondOfMinute()));
        TestCase.assertEquals((60 * 60), test.get(TestDateTime_Basics.ISO_DEFAULT.secondOfDay()));
        TestCase.assertEquals(0, test.get(TestDateTime_Basics.ISO_DEFAULT.millisOfSecond()));
        TestCase.assertEquals(((60 * 60) * 1000), test.get(TestDateTime_Basics.ISO_DEFAULT.millisOfDay()));
        try {
            test.get(((DateTimeField) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testGet_DateTimeFieldType() {
        DateTime test = new DateTime();
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
        TestCase.assertEquals(1, test.get(DateTimeFieldType.hourOfHalfday()));
        TestCase.assertEquals(1, test.get(DateTimeFieldType.clockhourOfDay()));
        TestCase.assertEquals(1, test.get(DateTimeFieldType.clockhourOfHalfday()));
        TestCase.assertEquals(1, test.get(DateTimeFieldType.hourOfDay()));
        TestCase.assertEquals(0, test.get(DateTimeFieldType.minuteOfHour()));
        TestCase.assertEquals(60, test.get(DateTimeFieldType.minuteOfDay()));
        TestCase.assertEquals(0, test.get(DateTimeFieldType.secondOfMinute()));
        TestCase.assertEquals((60 * 60), test.get(DateTimeFieldType.secondOfDay()));
        TestCase.assertEquals(0, test.get(DateTimeFieldType.millisOfSecond()));
        TestCase.assertEquals(((60 * 60) * 1000), test.get(DateTimeFieldType.millisOfDay()));
        try {
            test.get(((DateTimeFieldType) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testIsSupported_DateTimeFieldType() {
        DateTime test = new DateTime();
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.era()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.centuryOfEra()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.yearOfCentury()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.yearOfEra()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.year()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.monthOfYear()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.dayOfMonth()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.weekyear()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.weekOfWeekyear()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.dayOfWeek()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.dayOfYear()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.halfdayOfDay()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.hourOfHalfday()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.clockhourOfDay()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.clockhourOfHalfday()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.hourOfDay()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.minuteOfHour()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.minuteOfDay()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.secondOfMinute()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.secondOfDay()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.millisOfSecond()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.millisOfDay()));
        TestCase.assertEquals(false, test.isSupported(null));
    }

    // -----------------------------------------------------------------------
    public void testGetters() {
        DateTime test = new DateTime();
        TestCase.assertEquals(TestDateTime_Basics.ISO_DEFAULT, test.getChronology());
        TestCase.assertEquals(TestDateTime_Basics.LONDON, test.getZone());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
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
        TestCase.assertEquals(1, test.getHourOfDay());
        TestCase.assertEquals(0, test.getMinuteOfHour());
        TestCase.assertEquals(60, test.getMinuteOfDay());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals((60 * 60), test.getSecondOfDay());
        TestCase.assertEquals(0, test.getMillisOfSecond());
        TestCase.assertEquals(((60 * 60) * 1000), test.getMillisOfDay());
    }

    public void testWithers() {
        DateTime test = new DateTime(1970, 6, 9, 10, 20, 30, 40, TestDateTime_Basics.GJ_DEFAULT);
        check(test.withYear(2000), 2000, 6, 9, 10, 20, 30, 40);
        check(test.withMonthOfYear(2), 1970, 2, 9, 10, 20, 30, 40);
        check(test.withDayOfMonth(2), 1970, 6, 2, 10, 20, 30, 40);
        check(test.withDayOfYear(6), 1970, 1, 6, 10, 20, 30, 40);
        check(test.withDayOfWeek(6), 1970, 6, 13, 10, 20, 30, 40);
        check(test.withWeekOfWeekyear(6), 1970, 2, 3, 10, 20, 30, 40);
        check(test.withWeekyear(1971), 1971, 6, 15, 10, 20, 30, 40);
        check(test.withYearOfCentury(60), 1960, 6, 9, 10, 20, 30, 40);
        check(test.withCenturyOfEra(21), 2070, 6, 9, 10, 20, 30, 40);
        check(test.withYearOfEra(1066), 1066, 6, 9, 10, 20, 30, 40);
        check(test.withEra(BC), (-1970), 6, 9, 10, 20, 30, 40);
        check(test.withHourOfDay(6), 1970, 6, 9, 6, 20, 30, 40);
        check(test.withMinuteOfHour(6), 1970, 6, 9, 10, 6, 30, 40);
        check(test.withSecondOfMinute(6), 1970, 6, 9, 10, 20, 6, 40);
        check(test.withMillisOfSecond(6), 1970, 6, 9, 10, 20, 30, 6);
        check(test.withMillisOfDay(61234), 1970, 6, 9, 0, 1, 1, 234);
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
        DateTime test1 = new DateTime(TEST_TIME1);
        DateTime test2 = new DateTime(TEST_TIME1);
        TestCase.assertEquals(true, test1.equals(test2));
        TestCase.assertEquals(true, test2.equals(test1));
        TestCase.assertEquals(true, test1.equals(test1));
        TestCase.assertEquals(true, test2.equals(test2));
        TestCase.assertEquals(true, ((test1.hashCode()) == (test2.hashCode())));
        TestCase.assertEquals(true, ((test1.hashCode()) == (test1.hashCode())));
        TestCase.assertEquals(true, ((test2.hashCode()) == (test2.hashCode())));
        DateTime test3 = new DateTime(TEST_TIME2);
        TestCase.assertEquals(false, test1.equals(test3));
        TestCase.assertEquals(false, test2.equals(test3));
        TestCase.assertEquals(false, test3.equals(test1));
        TestCase.assertEquals(false, test3.equals(test2));
        TestCase.assertEquals(false, ((test1.hashCode()) == (test3.hashCode())));
        TestCase.assertEquals(false, ((test2.hashCode()) == (test3.hashCode())));
        TestCase.assertEquals(false, test1.equals("Hello"));
        TestCase.assertEquals(true, test1.equals(new TestDateTime_Basics.MockInstant()));
        TestCase.assertEquals(false, test1.equals(new DateTime(TEST_TIME1, TestDateTime_Basics.GREGORIAN_DEFAULT)));
        TestCase.assertEquals(true, new DateTime(TEST_TIME1, new TestDateTime_Basics.MockEqualsChronology()).equals(new DateTime(TEST_TIME1, new TestDateTime_Basics.MockEqualsChronology())));
        TestCase.assertEquals(false, new DateTime(TEST_TIME1, new TestDateTime_Basics.MockEqualsChronology()).equals(new DateTime(TEST_TIME1, TestDateTime_Basics.ISO_DEFAULT)));
    }

    class MockInstant extends AbstractInstant {
        public String toString() {
            return null;
        }

        public long getMillis() {
            return TEST_TIME1;
        }

        public Chronology getChronology() {
            return TestDateTime_Basics.ISO_DEFAULT;
        }
    }

    class MockEqualsChronology extends BaseChronology {
        private static final long serialVersionUID = 1L;

        public boolean equals(Object obj) {
            return obj instanceof TestDateTime_Basics.MockEqualsChronology;
        }

        public DateTimeZone getZone() {
            return null;
        }

        public Chronology withUTC() {
            return this;
        }

        public Chronology withZone(DateTimeZone zone) {
            return this;
        }

        public String toString() {
            return "";
        }
    }

    public void testCompareTo() {
        DateTime test1 = new DateTime(TEST_TIME1);
        DateTime test1a = new DateTime(TEST_TIME1);
        TestCase.assertEquals(0, test1.compareTo(test1a));
        TestCase.assertEquals(0, test1a.compareTo(test1));
        TestCase.assertEquals(0, test1.compareTo(test1));
        TestCase.assertEquals(0, test1a.compareTo(test1a));
        DateTime test2 = new DateTime(TEST_TIME2);
        TestCase.assertEquals((-1), test1.compareTo(test2));
        TestCase.assertEquals((+1), test2.compareTo(test1));
        DateTime test3 = new DateTime(TEST_TIME2, TestDateTime_Basics.GREGORIAN_PARIS);
        TestCase.assertEquals((-1), test1.compareTo(test3));
        TestCase.assertEquals((+1), test3.compareTo(test1));
        TestCase.assertEquals(0, test3.compareTo(test2));
        TestCase.assertEquals((+1), test2.compareTo(new TestDateTime_Basics.MockInstant()));
        TestCase.assertEquals(0, test1.compareTo(new TestDateTime_Basics.MockInstant()));
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

    // -----------------------------------------------------------------------
    public void testIsEqual_long() {
        TestCase.assertEquals(false, new DateTime(TEST_TIME1).isEqual(TEST_TIME2));
        TestCase.assertEquals(true, new DateTime(TEST_TIME1).isEqual(TEST_TIME1));
        TestCase.assertEquals(false, new DateTime(TEST_TIME2).isEqual(TEST_TIME1));
    }

    public void testIsEqualNow() {
        TestCase.assertEquals(false, new DateTime(((TEST_TIME_NOW) - 1)).isEqualNow());
        TestCase.assertEquals(true, new DateTime(TEST_TIME_NOW).isEqualNow());
        TestCase.assertEquals(false, new DateTime(((TEST_TIME_NOW) + 1)).isEqualNow());
    }

    public void testIsEqual_RI() {
        DateTime test1 = new DateTime(TEST_TIME1);
        DateTime test1a = new DateTime(TEST_TIME1);
        TestCase.assertEquals(true, test1.isEqual(test1a));
        TestCase.assertEquals(true, test1a.isEqual(test1));
        TestCase.assertEquals(true, test1.isEqual(test1));
        TestCase.assertEquals(true, test1a.isEqual(test1a));
        DateTime test2 = new DateTime(TEST_TIME2);
        TestCase.assertEquals(false, test1.isEqual(test2));
        TestCase.assertEquals(false, test2.isEqual(test1));
        DateTime test3 = new DateTime(TEST_TIME2, TestDateTime_Basics.GREGORIAN_PARIS);
        TestCase.assertEquals(false, test1.isEqual(test3));
        TestCase.assertEquals(false, test3.isEqual(test1));
        TestCase.assertEquals(true, test3.isEqual(test2));
        TestCase.assertEquals(false, test2.isEqual(new TestDateTime_Basics.MockInstant()));
        TestCase.assertEquals(true, test1.isEqual(new TestDateTime_Basics.MockInstant()));
        TestCase.assertEquals(false, new DateTime(((TEST_TIME_NOW) + 1)).isEqual(null));
        TestCase.assertEquals(true, new DateTime(TEST_TIME_NOW).isEqual(null));
        TestCase.assertEquals(false, new DateTime(((TEST_TIME_NOW) - 1)).isEqual(null));
    }

    // -----------------------------------------------------------------------
    public void testIsBefore_long() {
        TestCase.assertEquals(true, new DateTime(TEST_TIME1).isBefore(TEST_TIME2));
        TestCase.assertEquals(false, new DateTime(TEST_TIME1).isBefore(TEST_TIME1));
        TestCase.assertEquals(false, new DateTime(TEST_TIME2).isBefore(TEST_TIME1));
    }

    public void testIsBeforeNow() {
        TestCase.assertEquals(true, new DateTime(((TEST_TIME_NOW) - 1)).isBeforeNow());
        TestCase.assertEquals(false, new DateTime(TEST_TIME_NOW).isBeforeNow());
        TestCase.assertEquals(false, new DateTime(((TEST_TIME_NOW) + 1)).isBeforeNow());
    }

    public void testIsBefore_RI() {
        DateTime test1 = new DateTime(TEST_TIME1);
        DateTime test1a = new DateTime(TEST_TIME1);
        TestCase.assertEquals(false, test1.isBefore(test1a));
        TestCase.assertEquals(false, test1a.isBefore(test1));
        TestCase.assertEquals(false, test1.isBefore(test1));
        TestCase.assertEquals(false, test1a.isBefore(test1a));
        DateTime test2 = new DateTime(TEST_TIME2);
        TestCase.assertEquals(true, test1.isBefore(test2));
        TestCase.assertEquals(false, test2.isBefore(test1));
        DateTime test3 = new DateTime(TEST_TIME2, TestDateTime_Basics.GREGORIAN_PARIS);
        TestCase.assertEquals(true, test1.isBefore(test3));
        TestCase.assertEquals(false, test3.isBefore(test1));
        TestCase.assertEquals(false, test3.isBefore(test2));
        TestCase.assertEquals(false, test2.isBefore(new TestDateTime_Basics.MockInstant()));
        TestCase.assertEquals(false, test1.isBefore(new TestDateTime_Basics.MockInstant()));
        TestCase.assertEquals(false, new DateTime(((TEST_TIME_NOW) + 1)).isBefore(null));
        TestCase.assertEquals(false, new DateTime(TEST_TIME_NOW).isBefore(null));
        TestCase.assertEquals(true, new DateTime(((TEST_TIME_NOW) - 1)).isBefore(null));
    }

    // -----------------------------------------------------------------------
    public void testIsAfter_long() {
        TestCase.assertEquals(false, new DateTime(TEST_TIME1).isAfter(TEST_TIME2));
        TestCase.assertEquals(false, new DateTime(TEST_TIME1).isAfter(TEST_TIME1));
        TestCase.assertEquals(true, new DateTime(TEST_TIME2).isAfter(TEST_TIME1));
    }

    public void testIsAfterNow() {
        TestCase.assertEquals(false, new DateTime(((TEST_TIME_NOW) - 1)).isAfterNow());
        TestCase.assertEquals(false, new DateTime(TEST_TIME_NOW).isAfterNow());
        TestCase.assertEquals(true, new DateTime(((TEST_TIME_NOW) + 1)).isAfterNow());
    }

    public void testIsAfter_RI() {
        DateTime test1 = new DateTime(TEST_TIME1);
        DateTime test1a = new DateTime(TEST_TIME1);
        TestCase.assertEquals(false, test1.isAfter(test1a));
        TestCase.assertEquals(false, test1a.isAfter(test1));
        TestCase.assertEquals(false, test1.isAfter(test1));
        TestCase.assertEquals(false, test1a.isAfter(test1a));
        DateTime test2 = new DateTime(TEST_TIME2);
        TestCase.assertEquals(false, test1.isAfter(test2));
        TestCase.assertEquals(true, test2.isAfter(test1));
        DateTime test3 = new DateTime(TEST_TIME2, TestDateTime_Basics.GREGORIAN_PARIS);
        TestCase.assertEquals(false, test1.isAfter(test3));
        TestCase.assertEquals(true, test3.isAfter(test1));
        TestCase.assertEquals(false, test3.isAfter(test2));
        TestCase.assertEquals(true, test2.isAfter(new TestDateTime_Basics.MockInstant()));
        TestCase.assertEquals(false, test1.isAfter(new TestDateTime_Basics.MockInstant()));
        TestCase.assertEquals(true, new DateTime(((TEST_TIME_NOW) + 1)).isAfter(null));
        TestCase.assertEquals(false, new DateTime(TEST_TIME_NOW).isAfter(null));
        TestCase.assertEquals(false, new DateTime(((TEST_TIME_NOW) - 1)).isAfter(null));
    }

    // -----------------------------------------------------------------------
    public void testSerialization() throws Exception {
        DateTime test = new DateTime(TEST_TIME_NOW);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        DateTime result = ((DateTime) (ois.readObject()));
        ois.close();
        TestCase.assertEquals(test, result);
    }

    // -----------------------------------------------------------------------
    public void testToString() {
        DateTime test = new DateTime(TEST_TIME_NOW);
        TestCase.assertEquals("2002-06-09T01:00:00.000+01:00", test.toString());
        test = new DateTime(TEST_TIME_NOW, TestDateTime_Basics.PARIS);
        TestCase.assertEquals("2002-06-09T02:00:00.000+02:00", test.toString());
    }

    public void testToString_String() {
        DateTime test = new DateTime(TEST_TIME_NOW);
        TestCase.assertEquals("2002 01", test.toString("yyyy HH"));
        TestCase.assertEquals("2002-06-09T01:00:00.000+01:00", test.toString(((String) (null))));
    }

    public void testToString_String_Locale() {
        DateTime test = new DateTime(TEST_TIME_NOW);
        TestCase.assertEquals("Sun 9/6", test.toString("EEE d/M", Locale.ENGLISH));
        TestCase.assertEquals("dim. 9/6", test.toString("EEE d/M", Locale.FRENCH));
        TestCase.assertEquals("2002-06-09T01:00:00.000+01:00", test.toString(null, Locale.ENGLISH));
        TestCase.assertEquals("Sun 9/6", test.toString("EEE d/M", null));
        TestCase.assertEquals("2002-06-09T01:00:00.000+01:00", test.toString(null, null));
    }

    // -----------------------------------------------------------------------
    public void testToInstant() {
        DateTime test = new DateTime(TEST_TIME1);
        Instant result = test.toInstant();
        TestCase.assertEquals(TEST_TIME1, result.getMillis());
    }

    public void testToDateTime() {
        DateTime test = new DateTime(TEST_TIME1);
        DateTime result = test.toDateTime();
        TestCase.assertSame(test, result);
    }

    public void testToDateTimeISO() {
        DateTime test = new DateTime(TEST_TIME1);
        DateTime result = test.toDateTimeISO();
        TestCase.assertSame(test, result);
        test = new DateTime(TEST_TIME1, TestDateTime_Basics.ISO_PARIS);
        result = test.toDateTimeISO();
        TestCase.assertSame(DateTime.class, result.getClass());
        TestCase.assertSame(ISOChronology.class, result.getChronology().getClass());
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateTime_Basics.ISO_PARIS, result.getChronology());
        TestCase.assertNotSame(test, result);
        test = new DateTime(TEST_TIME1, TestDateTime_Basics.BUDDHIST_DEFAULT);
        result = test.toDateTimeISO();
        TestCase.assertSame(DateTime.class, result.getClass());
        TestCase.assertSame(ISOChronology.class, result.getChronology().getClass());
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateTime_Basics.ISO_DEFAULT, result.getChronology());
        TestCase.assertNotSame(test, result);
        test = new DateTime(TEST_TIME1, new MockNullZoneChronology());
        result = test.toDateTimeISO();
        TestCase.assertSame(DateTime.class, result.getClass());
        TestCase.assertSame(ISOChronology.class, result.getChronology().getClass());
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateTime_Basics.ISO_DEFAULT, result.getChronology());
        TestCase.assertNotSame(test, result);
    }

    public void testToDateTime_DateTimeZone() {
        DateTime test = new DateTime(TEST_TIME1);
        DateTime result = test.toDateTime(TestDateTime_Basics.LONDON);
        TestCase.assertSame(test, result);
        test = new DateTime(TEST_TIME1);
        result = test.toDateTime(TestDateTime_Basics.PARIS);
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateTime_Basics.PARIS, result.getZone());
        test = new DateTime(TEST_TIME1, TestDateTime_Basics.PARIS);
        result = test.toDateTime(((DateTimeZone) (null)));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateTime_Basics.LONDON, result.getZone());
        test = new DateTime(TEST_TIME1);
        result = test.toDateTime(((DateTimeZone) (null)));
        TestCase.assertSame(test, result);
    }

    public void testToDateTime_Chronology() {
        DateTime test = new DateTime(TEST_TIME1);
        DateTime result = test.toDateTime(TestDateTime_Basics.ISO_DEFAULT);
        TestCase.assertSame(test, result);
        test = new DateTime(TEST_TIME1);
        result = test.toDateTime(TestDateTime_Basics.GREGORIAN_PARIS);
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateTime_Basics.GREGORIAN_PARIS, result.getChronology());
        test = new DateTime(TEST_TIME1, TestDateTime_Basics.GREGORIAN_PARIS);
        result = test.toDateTime(((Chronology) (null)));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateTime_Basics.ISO_DEFAULT, result.getChronology());
        test = new DateTime(TEST_TIME1);
        result = test.toDateTime(((Chronology) (null)));
        TestCase.assertSame(test, result);
    }

    public void testToMutableDateTime() {
        DateTime test = new DateTime(TEST_TIME1, TestDateTime_Basics.PARIS);
        MutableDateTime result = test.toMutableDateTime();
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateTime_Basics.ISO_PARIS, result.getChronology());
    }

    public void testToMutableDateTimeISO() {
        DateTime test = new DateTime(TEST_TIME1, TestDateTime_Basics.PARIS);
        MutableDateTime result = test.toMutableDateTimeISO();
        TestCase.assertSame(MutableDateTime.class, result.getClass());
        TestCase.assertSame(ISOChronology.class, result.getChronology().getClass());
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateTime_Basics.ISO_PARIS, result.getChronology());
    }

    public void testToMutableDateTime_DateTimeZone() {
        DateTime test = new DateTime(TEST_TIME1);
        MutableDateTime result = test.toMutableDateTime(TestDateTime_Basics.LONDON);
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateTime_Basics.ISO_DEFAULT, result.getChronology());
        test = new DateTime(TEST_TIME1);
        result = test.toMutableDateTime(TestDateTime_Basics.PARIS);
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateTime_Basics.ISO_PARIS, result.getChronology());
        test = new DateTime(TEST_TIME1, TestDateTime_Basics.PARIS);
        result = test.toMutableDateTime(((DateTimeZone) (null)));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateTime_Basics.ISO_DEFAULT, result.getChronology());
        test = new DateTime(TEST_TIME1);
        result = test.toMutableDateTime(((DateTimeZone) (null)));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateTime_Basics.ISO_DEFAULT, result.getChronology());
    }

    public void testToMutableDateTime_Chronology() {
        DateTime test = new DateTime(TEST_TIME1);
        MutableDateTime result = test.toMutableDateTime(TestDateTime_Basics.ISO_DEFAULT);
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateTime_Basics.ISO_DEFAULT, result.getChronology());
        test = new DateTime(TEST_TIME1);
        result = test.toMutableDateTime(TestDateTime_Basics.GREGORIAN_PARIS);
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateTime_Basics.GREGORIAN_PARIS, result.getChronology());
        test = new DateTime(TEST_TIME1, TestDateTime_Basics.GREGORIAN_PARIS);
        result = test.toMutableDateTime(((Chronology) (null)));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateTime_Basics.ISO_DEFAULT, result.getChronology());
        test = new DateTime(TEST_TIME1);
        result = test.toMutableDateTime(((Chronology) (null)));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateTime_Basics.ISO_DEFAULT, result.getChronology());
    }

    public void testToDate() {
        DateTime test = new DateTime(TEST_TIME1);
        Date result = test.toDate();
        TestCase.assertEquals(test.getMillis(), result.getTime());
    }

    public void testToCalendar_Locale() {
        DateTime test = new DateTime(TEST_TIME1);
        Calendar result = test.toCalendar(null);
        TestCase.assertEquals(test.getMillis(), result.getTime().getTime());
        TestCase.assertEquals(TimeZone.getTimeZone("Europe/London"), result.getTimeZone());
        test = new DateTime(TEST_TIME1, TestDateTime_Basics.PARIS);
        result = test.toCalendar(null);
        TestCase.assertEquals(test.getMillis(), result.getTime().getTime());
        TestCase.assertEquals(TimeZone.getTimeZone("Europe/Paris"), result.getTimeZone());
        test = new DateTime(TEST_TIME1, TestDateTime_Basics.PARIS);
        result = test.toCalendar(Locale.UK);
        TestCase.assertEquals(test.getMillis(), result.getTime().getTime());
        TestCase.assertEquals(TimeZone.getTimeZone("Europe/Paris"), result.getTimeZone());
    }

    public void testToGregorianCalendar() {
        DateTime test = new DateTime(TEST_TIME1);
        GregorianCalendar result = test.toGregorianCalendar();
        TestCase.assertEquals(test.getMillis(), result.getTime().getTime());
        TestCase.assertEquals(TimeZone.getTimeZone("Europe/London"), result.getTimeZone());
        test = new DateTime(TEST_TIME1, TestDateTime_Basics.PARIS);
        result = test.toGregorianCalendar();
        TestCase.assertEquals(test.getMillis(), result.getTime().getTime());
        TestCase.assertEquals(TimeZone.getTimeZone("Europe/Paris"), result.getTimeZone());
    }

    public void testToLocalDateTime() {
        DateTime base = new DateTime(TEST_TIME1, TestDateTime_Basics.COPTIC_DEFAULT);
        LocalDateTime test = base.toLocalDateTime();
        TestCase.assertEquals(new LocalDateTime(TEST_TIME1, TestDateTime_Basics.COPTIC_DEFAULT), test);
    }

    public void testToLocalDate() {
        DateTime base = new DateTime(TEST_TIME1, TestDateTime_Basics.COPTIC_DEFAULT);
        LocalDate test = base.toLocalDate();
        TestCase.assertEquals(new LocalDate(TEST_TIME1, TestDateTime_Basics.COPTIC_DEFAULT), test);
    }

    public void testToLocalTime() {
        DateTime base = new DateTime(TEST_TIME1, TestDateTime_Basics.COPTIC_DEFAULT);
        LocalTime test = base.toLocalTime();
        TestCase.assertEquals(new LocalTime(TEST_TIME1, TestDateTime_Basics.COPTIC_DEFAULT), test);
    }

    // -----------------------------------------------------------------------
    public void testWithMillis_long() {
        DateTime test = new DateTime(TEST_TIME1);
        DateTime result = test.withMillis(TEST_TIME2);
        TestCase.assertEquals(TEST_TIME2, result.getMillis());
        TestCase.assertEquals(test.getChronology(), result.getChronology());
        test = new DateTime(TEST_TIME1, TestDateTime_Basics.GREGORIAN_PARIS);
        result = test.withMillis(TEST_TIME2);
        TestCase.assertEquals(TEST_TIME2, result.getMillis());
        TestCase.assertEquals(test.getChronology(), result.getChronology());
        test = new DateTime(TEST_TIME1);
        result = test.withMillis(TEST_TIME1);
        TestCase.assertSame(test, result);
    }

    public void testWithChronology_Chronology() {
        DateTime test = new DateTime(TEST_TIME1);
        DateTime result = test.withChronology(TestDateTime_Basics.GREGORIAN_PARIS);
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateTime_Basics.GREGORIAN_PARIS, result.getChronology());
        test = new DateTime(TEST_TIME1, TestDateTime_Basics.GREGORIAN_PARIS);
        result = test.withChronology(null);
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateTime_Basics.ISO_DEFAULT, result.getChronology());
        test = new DateTime(TEST_TIME1);
        result = test.withChronology(null);
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateTime_Basics.ISO_DEFAULT, result.getChronology());
        test = new DateTime(TEST_TIME1);
        result = test.withChronology(TestDateTime_Basics.ISO_DEFAULT);
        TestCase.assertSame(test, result);
    }

    public void testWithZone_DateTimeZone() {
        DateTime test = new DateTime(TEST_TIME1);
        DateTime result = test.withZone(TestDateTime_Basics.PARIS);
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateTime_Basics.ISO_PARIS, result.getChronology());
        test = new DateTime(TEST_TIME1, TestDateTime_Basics.GREGORIAN_PARIS);
        result = test.withZone(null);
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestDateTime_Basics.GREGORIAN_DEFAULT, result.getChronology());
        test = new DateTime(TEST_TIME1);
        result = test.withZone(null);
        TestCase.assertSame(test, result);
    }

    public void testWithZoneRetainFields_DateTimeZone() {
        DateTime test = new DateTime(TEST_TIME1);
        DateTime result = test.withZoneRetainFields(TestDateTime_Basics.PARIS);
        TestCase.assertEquals(((test.getMillis()) - (MILLIS_PER_HOUR)), result.getMillis());
        TestCase.assertEquals(TestDateTime_Basics.ISO_PARIS, result.getChronology());
        test = new DateTime(TEST_TIME1);
        result = test.withZoneRetainFields(TestDateTime_Basics.LONDON);
        TestCase.assertSame(test, result);
        test = new DateTime(TEST_TIME1);
        result = test.withZoneRetainFields(null);
        TestCase.assertSame(test, result);
        test = new DateTime(TEST_TIME1, TestDateTime_Basics.GREGORIAN_PARIS);
        result = test.withZoneRetainFields(null);
        TestCase.assertEquals(((test.getMillis()) + (MILLIS_PER_HOUR)), result.getMillis());
        TestCase.assertEquals(TestDateTime_Basics.GREGORIAN_DEFAULT, result.getChronology());
        test = new DateTime(TEST_TIME1, new MockNullZoneChronology());
        result = test.withZoneRetainFields(TestDateTime_Basics.LONDON);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testWithDate_int_int_int() {
        DateTime test = new DateTime(2002, 4, 5, 1, 2, 3, 4, TestDateTime_Basics.ISO_UTC);
        DateTime result = test.withDate(2003, 5, 6);
        DateTime expected = new DateTime(2003, 5, 6, 1, 2, 3, 4, TestDateTime_Basics.ISO_UTC);
        TestCase.assertEquals(expected, result);
        test = new DateTime(TEST_TIME1);
        try {
            test.withDate(2003, 13, 1);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithDate_int_int_int_toDST1() {
        // 2010-03-28T02:55 is DST time, need to change to 03:55
        DateTime test = new DateTime(2015, 1, 10, 2, 55, 0, 0, TestDateTime_Basics.ISO_PARIS);
        DateTime result = test.withDate(2010, 3, 28);
        DateTime expected = new DateTime(2010, 3, 28, 3, 55, 0, 0, TestDateTime_Basics.ISO_PARIS);
        TestCase.assertEquals(expected, result);
    }

    public void testWithDate_int_int_int_toDST2() {
        // 2010-03-28T02:55 is DST time, need to change to 03:55
        DateTime test = new DateTime(2015, 1, 28, 2, 55, 0, 0, TestDateTime_Basics.ISO_PARIS);
        DateTime result = test.withDate(2010, 3, 28);
        DateTime expected = new DateTime(2010, 3, 28, 3, 55, 0, 0, TestDateTime_Basics.ISO_PARIS);
        TestCase.assertEquals(expected, result);
    }

    public void testWithDate_int_int_int_affectedByDST() {
        // 2010-03-28T02:55 is DST time, need to avoid time being changed to 03:55
        DateTime test = new DateTime(2015, 1, 28, 2, 55, 0, 0, TestDateTime_Basics.ISO_PARIS);
        DateTime result = test.withDate(2010, 3, 10);
        DateTime expected = new DateTime(2010, 3, 10, 2, 55, 0, 0, TestDateTime_Basics.ISO_PARIS);
        TestCase.assertEquals(expected, result);
    }

    public void testWithDate_LocalDate() {
        DateTime test = new DateTime(2002, 4, 5, 1, 2, 3, 4, TestDateTime_Basics.ISO_UTC);
        DateTime result = test.withDate(new LocalDate(2003, 5, 6));
        DateTime expected = new DateTime(2003, 5, 6, 1, 2, 3, 4, TestDateTime_Basics.ISO_UTC);
        TestCase.assertEquals(expected, result);
        test = new DateTime(TEST_TIME1);
        try {
            test.withDate(new LocalDate(2003, 13, 1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testWithTime_int_int_int_int() {
        DateTime test = new DateTime(((TEST_TIME1) - 12345L), TestDateTime_Basics.BUDDHIST_UTC);
        DateTime result = test.withTime(12, 24, 0, 0);
        TestCase.assertEquals(TEST_TIME1, result.getMillis());
        TestCase.assertEquals(TestDateTime_Basics.BUDDHIST_UTC, result.getChronology());
        test = new DateTime(TEST_TIME1);
        try {
            test.withTime(25, 1, 1, 1);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithTime_int_int_int_int_toDST() {
        // 2010-03-28T02:55 is DST time, need to change to 03:55
        DateTime test = new DateTime(2010, 3, 28, 0, 0, 0, 0, TestDateTime_Basics.ISO_PARIS);
        DateTime result = test.withTime(2, 55, 0, 0);
        DateTime expected = new DateTime(2010, 3, 28, 3, 55, 0, 0, TestDateTime_Basics.ISO_PARIS);
        TestCase.assertEquals(expected, result);
    }

    public void testWithTime_LocalTime() {
        DateTime test = new DateTime(((TEST_TIME1) - 12345L), TestDateTime_Basics.BUDDHIST_UTC);
        DateTime result = test.withTime(new LocalTime(12, 24, 0, 0));
        TestCase.assertEquals(TEST_TIME1, result.getMillis());
        TestCase.assertEquals(TestDateTime_Basics.BUDDHIST_UTC, result.getChronology());
        test = new DateTime(TEST_TIME1);
        try {
            test.withTime(new LocalTime(25, 1, 1, 1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithTimeAtStartOfDay() {
        DateTime test = new DateTime(2018, 10, 28, 0, 0, DateTimeZone.forID("Atlantic/Azores"));
        DateTime result = test.withTimeAtStartOfDay();
        TestCase.assertEquals(test, result);
    }

    // -----------------------------------------------------------------------
    public void testWithField1() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime result = test.withField(DateTimeFieldType.year(), 2006);
        TestCase.assertEquals(new DateTime(2004, 6, 9, 0, 0, 0, 0), test);
        TestCase.assertEquals(new DateTime(2006, 6, 9, 0, 0, 0, 0), result);
    }

    public void testWithField2() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        try {
            test.withField(null, 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testWithFieldAdded1() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime result = test.withFieldAdded(DurationFieldType.years(), 6);
        TestCase.assertEquals(new DateTime(2004, 6, 9, 0, 0, 0, 0), test);
        TestCase.assertEquals(new DateTime(2010, 6, 9, 0, 0, 0, 0), result);
    }

    public void testWithFieldAdded2() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        try {
            test.withFieldAdded(null, 0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithFieldAdded3() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        try {
            test.withFieldAdded(null, 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithFieldAdded4() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime result = test.withFieldAdded(DurationFieldType.years(), 0);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testWithDurationAdded_long_int() {
        DateTime test = new DateTime(TEST_TIME1, TestDateTime_Basics.BUDDHIST_DEFAULT);
        DateTime result = test.withDurationAdded(123456789L, 1);
        DateTime expected = new DateTime(((TEST_TIME1) + 123456789L), TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.withDurationAdded(123456789L, 0);
        TestCase.assertSame(test, result);
        result = test.withDurationAdded(123456789L, 2);
        expected = new DateTime(((TEST_TIME1) + (2L * 123456789L)), TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.withDurationAdded(123456789L, (-3));
        expected = new DateTime(((TEST_TIME1) - (3L * 123456789L)), TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
    }

    // -----------------------------------------------------------------------
    public void testWithDurationAdded_RD_int() {
        DateTime test = new DateTime(TEST_TIME1, TestDateTime_Basics.BUDDHIST_DEFAULT);
        DateTime result = test.withDurationAdded(new Duration(123456789L), 1);
        DateTime expected = new DateTime(((TEST_TIME1) + 123456789L), TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.withDurationAdded(null, 1);
        TestCase.assertSame(test, result);
        result = test.withDurationAdded(new Duration(123456789L), 0);
        TestCase.assertSame(test, result);
        result = test.withDurationAdded(new Duration(123456789L), 2);
        expected = new DateTime(((TEST_TIME1) + (2L * 123456789L)), TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.withDurationAdded(new Duration(123456789L), (-3));
        expected = new DateTime(((TEST_TIME1) - (3L * 123456789L)), TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
    }

    // -----------------------------------------------------------------------
    public void testWithDurationAdded_RP_int() {
        DateTime test = new DateTime(2002, 5, 3, 1, 2, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        DateTime result = test.withPeriodAdded(new Period(1, 2, 3, 4, 5, 6, 7, 8), 1);
        DateTime expected = new DateTime(2003, 7, 28, 6, 8, 10, 12, TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.withPeriodAdded(null, 1);
        TestCase.assertSame(test, result);
        result = test.withPeriodAdded(new Period(1, 2, 3, 4, 5, 6, 7, 8), 0);
        TestCase.assertSame(test, result);
        result = test.withPeriodAdded(new Period(1, 2, 0, 4, 5, 6, 7, 8), 3);
        expected = new DateTime(2005, 11, 15, 16, 20, 24, 28, TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.withPeriodAdded(new Period(1, 2, 0, 1, 1, 2, 3, 4), (-1));
        expected = new DateTime(2001, 3, 2, 0, 0, 0, 0, TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
    }

    // -----------------------------------------------------------------------
    public void testPlus_long() {
        DateTime test = new DateTime(TEST_TIME1, TestDateTime_Basics.BUDDHIST_DEFAULT);
        DateTime result = test.plus(123456789L);
        DateTime expected = new DateTime(((TEST_TIME1) + 123456789L), TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
    }

    public void testPlus_RD() {
        DateTime test = new DateTime(TEST_TIME1, TestDateTime_Basics.BUDDHIST_DEFAULT);
        DateTime result = test.plus(new Duration(123456789L));
        DateTime expected = new DateTime(((TEST_TIME1) + 123456789L), TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.plus(((ReadableDuration) (null)));
        TestCase.assertSame(test, result);
    }

    public void testPlus_RP() {
        DateTime test = new DateTime(2002, 5, 3, 1, 2, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        DateTime result = test.plus(new Period(1, 2, 3, 4, 5, 6, 7, 8));
        DateTime expected = new DateTime(2003, 7, 28, 6, 8, 10, 12, TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.plus(((ReadablePeriod) (null)));
        TestCase.assertSame(test, result);
    }

    public void testPlusYears_int() {
        DateTime test = new DateTime(2002, 5, 3, 1, 2, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        DateTime result = test.plusYears(1);
        DateTime expected = new DateTime(2003, 5, 3, 1, 2, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.plusYears(0);
        TestCase.assertSame(test, result);
    }

    public void testPlusMonths_int() {
        DateTime test = new DateTime(2002, 5, 3, 1, 2, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        DateTime result = test.plusMonths(1);
        DateTime expected = new DateTime(2002, 6, 3, 1, 2, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.plusMonths(0);
        TestCase.assertSame(test, result);
    }

    public void testPlusMonths_intMax() {
        DateTime test = new DateTime(2016, 2, 20, 1, 2, 3, 4, TestDateTime_Basics.ISO_UTC);
        TestCase.assertEquals(new DateTime(178958986, 7, 20, 1, 2, 3, 4, TestDateTime_Basics.ISO_UTC), test.plusMonths(((Integer.MAX_VALUE) - 2)));
        TestCase.assertEquals(new DateTime(178958986, 8, 20, 1, 2, 3, 4, TestDateTime_Basics.ISO_UTC), test.plusMonths(((Integer.MAX_VALUE) - 1)));
        TestCase.assertEquals(new DateTime(178958986, 9, 20, 1, 2, 3, 4, TestDateTime_Basics.ISO_UTC), test.plusMonths(Integer.MAX_VALUE));
        TestCase.assertEquals(new DateTime(178958986, 7, 20, 1, 2, 3, 4, TestDateTime_Basics.ISO_UTC), test.monthOfYear().addToCopy(((Integer.MAX_VALUE) - 2)));
        TestCase.assertEquals(new DateTime(178958986, 8, 20, 1, 2, 3, 4, TestDateTime_Basics.ISO_UTC), test.monthOfYear().addToCopy(((Integer.MAX_VALUE) - 1)));
        TestCase.assertEquals(new DateTime(178958986, 9, 20, 1, 2, 3, 4, TestDateTime_Basics.ISO_UTC), test.monthOfYear().addToCopy(Integer.MAX_VALUE));
    }

    public void testPlusMonths_intMin() {
        DateTime test = new DateTime(2016, 2, 20, 1, 2, 3, 4, TestDateTime_Basics.ISO_UTC);
        TestCase.assertEquals(new DateTime((-178954955), 8, 20, 1, 2, 3, 4, TestDateTime_Basics.ISO_UTC), test.plusMonths(((Integer.MIN_VALUE) + 2)));
        TestCase.assertEquals(new DateTime((-178954955), 7, 20, 1, 2, 3, 4, TestDateTime_Basics.ISO_UTC), test.plusMonths(((Integer.MIN_VALUE) + 1)));
        TestCase.assertEquals(new DateTime((-178954955), 6, 20, 1, 2, 3, 4, TestDateTime_Basics.ISO_UTC), test.plusMonths(Integer.MIN_VALUE));
        TestCase.assertEquals(new DateTime((-178954955), 8, 20, 1, 2, 3, 4, TestDateTime_Basics.ISO_UTC), test.monthOfYear().addToCopy(((Integer.MIN_VALUE) + 2)));
        TestCase.assertEquals(new DateTime((-178954955), 7, 20, 1, 2, 3, 4, TestDateTime_Basics.ISO_UTC), test.monthOfYear().addToCopy(((Integer.MIN_VALUE) + 1)));
        TestCase.assertEquals(new DateTime((-178954955), 6, 20, 1, 2, 3, 4, TestDateTime_Basics.ISO_UTC), test.monthOfYear().addToCopy(Integer.MIN_VALUE));
    }

    public void testPlusWeeks_int() {
        DateTime test = new DateTime(2002, 5, 3, 1, 2, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        DateTime result = test.plusWeeks(1);
        DateTime expected = new DateTime(2002, 5, 10, 1, 2, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.plusWeeks(0);
        TestCase.assertSame(test, result);
    }

    public void testPlusDays_int() {
        DateTime test = new DateTime(2002, 5, 3, 1, 2, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        DateTime result = test.plusDays(1);
        DateTime expected = new DateTime(2002, 5, 4, 1, 2, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.plusDays(0);
        TestCase.assertSame(test, result);
    }

    public void testPlusHours_int() {
        DateTime test = new DateTime(2002, 5, 3, 1, 2, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        DateTime result = test.plusHours(1);
        DateTime expected = new DateTime(2002, 5, 3, 2, 2, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.plusHours(0);
        TestCase.assertSame(test, result);
    }

    public void testPlusMinutes_int() {
        DateTime test = new DateTime(2002, 5, 3, 1, 2, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        DateTime result = test.plusMinutes(1);
        DateTime expected = new DateTime(2002, 5, 3, 1, 3, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.plusMinutes(0);
        TestCase.assertSame(test, result);
    }

    public void testPlusSeconds_int() {
        DateTime test = new DateTime(2002, 5, 3, 1, 2, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        DateTime result = test.plusSeconds(1);
        DateTime expected = new DateTime(2002, 5, 3, 1, 2, 4, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.plusSeconds(0);
        TestCase.assertSame(test, result);
    }

    public void testPlusMillis_int() {
        DateTime test = new DateTime(2002, 5, 3, 1, 2, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        DateTime result = test.plusMillis(1);
        DateTime expected = new DateTime(2002, 5, 3, 1, 2, 3, 5, TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.plusMillis(0);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testMinus_long() {
        DateTime test = new DateTime(TEST_TIME1, TestDateTime_Basics.BUDDHIST_DEFAULT);
        DateTime result = test.minus(123456789L);
        DateTime expected = new DateTime(((TEST_TIME1) - 123456789L), TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
    }

    public void testMinus_RD() {
        DateTime test = new DateTime(TEST_TIME1, TestDateTime_Basics.BUDDHIST_DEFAULT);
        DateTime result = test.minus(new Duration(123456789L));
        DateTime expected = new DateTime(((TEST_TIME1) - 123456789L), TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.minus(((ReadableDuration) (null)));
        TestCase.assertSame(test, result);
    }

    public void testMinus_RP() {
        DateTime test = new DateTime(2002, 5, 3, 1, 2, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        DateTime result = test.minus(new Period(1, 1, 1, 1, 1, 1, 1, 1));
        DateTime expected = new DateTime(2001, 3, 26, 0, 1, 2, 3, TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.minus(((ReadablePeriod) (null)));
        TestCase.assertSame(test, result);
    }

    public void testMinusYears_int() {
        DateTime test = new DateTime(2002, 5, 3, 1, 2, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        DateTime result = test.minusYears(1);
        DateTime expected = new DateTime(2001, 5, 3, 1, 2, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.minusYears(0);
        TestCase.assertSame(test, result);
    }

    public void testMinusMonths_int() {
        DateTime test = new DateTime(2002, 5, 3, 1, 2, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        DateTime result = test.minusMonths(1);
        DateTime expected = new DateTime(2002, 4, 3, 1, 2, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.minusMonths(0);
        TestCase.assertSame(test, result);
    }

    public void testMinusMonths_intMax() {
        DateTime test = new DateTime(2016, 2, 20, 1, 2, 3, 4, TestDateTime_Basics.ISO_UTC);
        TestCase.assertEquals(new DateTime((-178954955), 9, 20, 1, 2, 3, 4, TestDateTime_Basics.ISO_UTC), test.minusMonths(((Integer.MAX_VALUE) - 2)));
        TestCase.assertEquals(new DateTime((-178954955), 8, 20, 1, 2, 3, 4, TestDateTime_Basics.ISO_UTC), test.minusMonths(((Integer.MAX_VALUE) - 1)));
        TestCase.assertEquals(new DateTime((-178954955), 7, 20, 1, 2, 3, 4, TestDateTime_Basics.ISO_UTC), test.minusMonths(Integer.MAX_VALUE));
    }

    public void testMinusMonths_intMin() {
        DateTime test = new DateTime(2016, 2, 20, 1, 2, 3, 4, TestDateTime_Basics.ISO_UTC);
        TestCase.assertEquals(new DateTime(178958986, 8, 20, 1, 2, 3, 4, TestDateTime_Basics.ISO_UTC), test.minusMonths(((Integer.MIN_VALUE) + 2)));
        TestCase.assertEquals(new DateTime(178958986, 9, 20, 1, 2, 3, 4, TestDateTime_Basics.ISO_UTC), test.minusMonths(((Integer.MIN_VALUE) + 1)));
        TestCase.assertEquals(new DateTime(178958986, 10, 20, 1, 2, 3, 4, TestDateTime_Basics.ISO_UTC), test.minusMonths(Integer.MIN_VALUE));
    }

    public void testMinusWeeks_int() {
        DateTime test = new DateTime(2002, 5, 3, 1, 2, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        DateTime result = test.minusWeeks(1);
        DateTime expected = new DateTime(2002, 4, 26, 1, 2, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.minusWeeks(0);
        TestCase.assertSame(test, result);
    }

    public void testMinusDays_int() {
        DateTime test = new DateTime(2002, 5, 3, 1, 2, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        DateTime result = test.minusDays(1);
        DateTime expected = new DateTime(2002, 5, 2, 1, 2, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.minusDays(0);
        TestCase.assertSame(test, result);
    }

    public void testMinusHours_int() {
        DateTime test = new DateTime(2002, 5, 3, 1, 2, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        DateTime result = test.minusHours(1);
        DateTime expected = new DateTime(2002, 5, 3, 0, 2, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.minusHours(0);
        TestCase.assertSame(test, result);
    }

    public void testMinusMinutes_int() {
        DateTime test = new DateTime(2002, 5, 3, 1, 2, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        DateTime result = test.minusMinutes(1);
        DateTime expected = new DateTime(2002, 5, 3, 1, 1, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.minusMinutes(0);
        TestCase.assertSame(test, result);
    }

    public void testMinusSeconds_int() {
        DateTime test = new DateTime(2002, 5, 3, 1, 2, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        DateTime result = test.minusSeconds(1);
        DateTime expected = new DateTime(2002, 5, 3, 1, 2, 2, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.minusSeconds(0);
        TestCase.assertSame(test, result);
    }

    public void testMinusMillis_int() {
        DateTime test = new DateTime(2002, 5, 3, 1, 2, 3, 4, TestDateTime_Basics.BUDDHIST_DEFAULT);
        DateTime result = test.minusMillis(1);
        DateTime expected = new DateTime(2002, 5, 3, 1, 2, 3, 3, TestDateTime_Basics.BUDDHIST_DEFAULT);
        TestCase.assertEquals(expected, result);
        result = test.minusMillis(0);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testProperty() {
        DateTime test = new DateTime();
        TestCase.assertEquals(test.year(), test.property(DateTimeFieldType.year()));
        TestCase.assertEquals(test.dayOfWeek(), test.property(DateTimeFieldType.dayOfWeek()));
        TestCase.assertEquals(test.secondOfMinute(), test.property(DateTimeFieldType.secondOfMinute()));
        TestCase.assertEquals(test.millisOfSecond(), test.property(DateTimeFieldType.millisOfSecond()));
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

