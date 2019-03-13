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


import Minutes.MAX_VALUE;
import Minutes.MIN_VALUE;
import Minutes.ONE;
import Minutes.THREE;
import Minutes.TWO;
import Minutes.ZERO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import junit.framework.TestCase;

import static DateTimeConstants.MILLIS_PER_MINUTE;
import static Minutes.THREE;


/**
 * This class is a Junit unit test for Minutes.
 *
 * @author Stephen Colebourne
 */
public class TestMinutes extends TestCase {
    // Test in 2002/03 as time zones are more well known
    // (before the late 90's they were all over the place)
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    public TestMinutes(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testConstants() {
        TestCase.assertEquals(0, ZERO.getMinutes());
        TestCase.assertEquals(1, ONE.getMinutes());
        TestCase.assertEquals(2, TWO.getMinutes());
        TestCase.assertEquals(3, THREE.getMinutes());
        TestCase.assertEquals(Integer.MAX_VALUE, MAX_VALUE.getMinutes());
        TestCase.assertEquals(Integer.MIN_VALUE, MIN_VALUE.getMinutes());
    }

    // -----------------------------------------------------------------------
    public void testFactory_minutes_int() {
        TestCase.assertSame(ZERO, Minutes.minutes(0));
        TestCase.assertSame(ONE, Minutes.minutes(1));
        TestCase.assertSame(TWO, Minutes.minutes(2));
        TestCase.assertSame(THREE, Minutes.minutes(3));
        TestCase.assertSame(MAX_VALUE, Minutes.minutes(Integer.MAX_VALUE));
        TestCase.assertSame(MIN_VALUE, Minutes.minutes(Integer.MIN_VALUE));
        TestCase.assertEquals((-1), Minutes.minutes((-1)).getMinutes());
        TestCase.assertEquals(4, Minutes.minutes(4).getMinutes());
    }

    // -----------------------------------------------------------------------
    public void testFactory_minutesBetween_RInstant() {
        DateTime start = new DateTime(2006, 6, 9, 12, 3, 0, 0, TestMinutes.PARIS);
        DateTime end1 = new DateTime(2006, 6, 9, 12, 6, 0, 0, TestMinutes.PARIS);
        DateTime end2 = new DateTime(2006, 6, 9, 12, 9, 0, 0, TestMinutes.PARIS);
        TestCase.assertEquals(3, Minutes.minutesBetween(start, end1).getMinutes());
        TestCase.assertEquals(0, Minutes.minutesBetween(start, start).getMinutes());
        TestCase.assertEquals(0, Minutes.minutesBetween(end1, end1).getMinutes());
        TestCase.assertEquals((-3), Minutes.minutesBetween(end1, start).getMinutes());
        TestCase.assertEquals(6, Minutes.minutesBetween(start, end2).getMinutes());
    }

    public void testFactory_minutesBetween_RPartial() {
        LocalTime start = new LocalTime(12, 3);
        LocalTime end1 = new LocalTime(12, 6);
        @SuppressWarnings("deprecation")
        TimeOfDay end2 = new TimeOfDay(12, 9);
        TestCase.assertEquals(3, Minutes.minutesBetween(start, end1).getMinutes());
        TestCase.assertEquals(0, Minutes.minutesBetween(start, start).getMinutes());
        TestCase.assertEquals(0, Minutes.minutesBetween(end1, end1).getMinutes());
        TestCase.assertEquals((-3), Minutes.minutesBetween(end1, start).getMinutes());
        TestCase.assertEquals(6, Minutes.minutesBetween(start, end2).getMinutes());
    }

    public void testFactory_minutesIn_RInterval() {
        DateTime start = new DateTime(2006, 6, 9, 12, 3, 0, 0, TestMinutes.PARIS);
        DateTime end1 = new DateTime(2006, 6, 9, 12, 6, 0, 0, TestMinutes.PARIS);
        DateTime end2 = new DateTime(2006, 6, 9, 12, 9, 0, 0, TestMinutes.PARIS);
        TestCase.assertEquals(0, Minutes.minutesIn(((ReadableInterval) (null))).getMinutes());
        TestCase.assertEquals(3, Minutes.minutesIn(new Interval(start, end1)).getMinutes());
        TestCase.assertEquals(0, Minutes.minutesIn(new Interval(start, start)).getMinutes());
        TestCase.assertEquals(0, Minutes.minutesIn(new Interval(end1, end1)).getMinutes());
        TestCase.assertEquals(6, Minutes.minutesIn(new Interval(start, end2)).getMinutes());
    }

    public void testFactory_standardMinutesIn_RPeriod() {
        TestCase.assertEquals(0, Minutes.standardMinutesIn(((ReadablePeriod) (null))).getMinutes());
        TestCase.assertEquals(0, Minutes.standardMinutesIn(Period.ZERO).getMinutes());
        TestCase.assertEquals(1, Minutes.standardMinutesIn(new Period(0, 0, 0, 0, 0, 1, 0, 0)).getMinutes());
        TestCase.assertEquals(123, Minutes.standardMinutesIn(Period.minutes(123)).getMinutes());
        TestCase.assertEquals((-987), Minutes.standardMinutesIn(Period.minutes((-987))).getMinutes());
        TestCase.assertEquals(1, Minutes.standardMinutesIn(Period.seconds(119)).getMinutes());
        TestCase.assertEquals(2, Minutes.standardMinutesIn(Period.seconds(120)).getMinutes());
        TestCase.assertEquals(2, Minutes.standardMinutesIn(Period.seconds(121)).getMinutes());
        TestCase.assertEquals(120, Minutes.standardMinutesIn(Period.hours(2)).getMinutes());
        try {
            Minutes.standardMinutesIn(Period.months(1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testFactory_parseMinutes_String() {
        TestCase.assertEquals(0, Minutes.parseMinutes(((String) (null))).getMinutes());
        TestCase.assertEquals(0, Minutes.parseMinutes("PT0M").getMinutes());
        TestCase.assertEquals(1, Minutes.parseMinutes("PT1M").getMinutes());
        TestCase.assertEquals((-3), Minutes.parseMinutes("PT-3M").getMinutes());
        TestCase.assertEquals(2, Minutes.parseMinutes("P0Y0M0DT2M").getMinutes());
        TestCase.assertEquals(2, Minutes.parseMinutes("PT0H2M").getMinutes());
        try {
            Minutes.parseMinutes("P1Y1D");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            Minutes.parseMinutes("P1DT1M");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    public void testGetMethods() {
        Minutes test = Minutes.minutes(20);
        TestCase.assertEquals(20, test.getMinutes());
    }

    public void testGetFieldType() {
        Minutes test = Minutes.minutes(20);
        TestCase.assertEquals(DurationFieldType.minutes(), test.getFieldType());
    }

    public void testGetPeriodType() {
        Minutes test = Minutes.minutes(20);
        TestCase.assertEquals(PeriodType.minutes(), test.getPeriodType());
    }

    // -----------------------------------------------------------------------
    public void testIsGreaterThan() {
        TestCase.assertEquals(true, THREE.isGreaterThan(TWO));
        TestCase.assertEquals(false, THREE.isGreaterThan(THREE));
        TestCase.assertEquals(false, TWO.isGreaterThan(THREE));
        TestCase.assertEquals(true, ONE.isGreaterThan(null));
        TestCase.assertEquals(false, Minutes.minutes((-1)).isGreaterThan(null));
    }

    public void testIsLessThan() {
        TestCase.assertEquals(false, THREE.isLessThan(TWO));
        TestCase.assertEquals(false, THREE.isLessThan(THREE));
        TestCase.assertEquals(true, TWO.isLessThan(THREE));
        TestCase.assertEquals(false, ONE.isLessThan(null));
        TestCase.assertEquals(true, Minutes.minutes((-1)).isLessThan(null));
    }

    // -----------------------------------------------------------------------
    public void testToString() {
        Minutes test = Minutes.minutes(20);
        TestCase.assertEquals("PT20M", test.toString());
        test = Minutes.minutes((-20));
        TestCase.assertEquals("PT-20M", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testSerialization() throws Exception {
        Minutes test = THREE;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Minutes result = ((Minutes) (ois.readObject()));
        ois.close();
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testToStandardWeeks() {
        Minutes test = Minutes.minutes((((60 * 24) * 7) * 2));
        Weeks expected = Weeks.weeks(2);
        TestCase.assertEquals(expected, test.toStandardWeeks());
    }

    public void testToStandardDays() {
        Minutes test = Minutes.minutes(((60 * 24) * 2));
        Days expected = Days.days(2);
        TestCase.assertEquals(expected, test.toStandardDays());
    }

    public void testToStandardHours() {
        Minutes test = Minutes.minutes((3 * 60));
        Hours expected = Hours.hours(3);
        TestCase.assertEquals(expected, test.toStandardHours());
    }

    public void testToStandardSeconds() {
        Minutes test = Minutes.minutes(3);
        Seconds expected = Seconds.seconds((3 * 60));
        TestCase.assertEquals(expected, test.toStandardSeconds());
        try {
            MAX_VALUE.toStandardSeconds();
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testToStandardDuration() {
        Minutes test = Minutes.minutes(20);
        Duration expected = new Duration((20L * (MILLIS_PER_MINUTE)));
        TestCase.assertEquals(expected, test.toStandardDuration());
        expected = new Duration((((long) (Integer.MAX_VALUE)) * (MILLIS_PER_MINUTE)));
        TestCase.assertEquals(expected, MAX_VALUE.toStandardDuration());
    }

    // -----------------------------------------------------------------------
    public void testPlus_int() {
        Minutes test2 = Minutes.minutes(2);
        Minutes result = test2.plus(3);
        TestCase.assertEquals(2, test2.getMinutes());
        TestCase.assertEquals(5, result.getMinutes());
        TestCase.assertEquals(1, ONE.plus(0).getMinutes());
        try {
            MAX_VALUE.plus(1);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testPlus_Minutes() {
        Minutes test2 = Minutes.minutes(2);
        Minutes test3 = Minutes.minutes(3);
        Minutes result = test2.plus(test3);
        TestCase.assertEquals(2, test2.getMinutes());
        TestCase.assertEquals(3, test3.getMinutes());
        TestCase.assertEquals(5, result.getMinutes());
        TestCase.assertEquals(1, ONE.plus(ZERO).getMinutes());
        TestCase.assertEquals(1, ONE.plus(((Minutes) (null))).getMinutes());
        try {
            MAX_VALUE.plus(ONE);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testMinus_int() {
        Minutes test2 = Minutes.minutes(2);
        Minutes result = test2.minus(3);
        TestCase.assertEquals(2, test2.getMinutes());
        TestCase.assertEquals((-1), result.getMinutes());
        TestCase.assertEquals(1, ONE.minus(0).getMinutes());
        try {
            MIN_VALUE.minus(1);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testMinus_Minutes() {
        Minutes test2 = Minutes.minutes(2);
        Minutes test3 = Minutes.minutes(3);
        Minutes result = test2.minus(test3);
        TestCase.assertEquals(2, test2.getMinutes());
        TestCase.assertEquals(3, test3.getMinutes());
        TestCase.assertEquals((-1), result.getMinutes());
        TestCase.assertEquals(1, ONE.minus(ZERO).getMinutes());
        TestCase.assertEquals(1, ONE.minus(((Minutes) (null))).getMinutes());
        try {
            MIN_VALUE.minus(ONE);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testMultipliedBy_int() {
        Minutes test = Minutes.minutes(2);
        TestCase.assertEquals(6, test.multipliedBy(3).getMinutes());
        TestCase.assertEquals(2, test.getMinutes());
        TestCase.assertEquals((-6), test.multipliedBy((-3)).getMinutes());
        TestCase.assertSame(test, test.multipliedBy(1));
        Minutes halfMax = Minutes.minutes((((Integer.MAX_VALUE) / 2) + 1));
        try {
            halfMax.multipliedBy(2);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testDividedBy_int() {
        Minutes test = Minutes.minutes(12);
        TestCase.assertEquals(6, test.dividedBy(2).getMinutes());
        TestCase.assertEquals(12, test.getMinutes());
        TestCase.assertEquals(4, test.dividedBy(3).getMinutes());
        TestCase.assertEquals(3, test.dividedBy(4).getMinutes());
        TestCase.assertEquals(2, test.dividedBy(5).getMinutes());
        TestCase.assertEquals(2, test.dividedBy(6).getMinutes());
        TestCase.assertSame(test, test.dividedBy(1));
        try {
            ONE.dividedBy(0);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testNegated() {
        Minutes test = Minutes.minutes(12);
        TestCase.assertEquals((-12), test.negated().getMinutes());
        TestCase.assertEquals(12, test.getMinutes());
        try {
            MIN_VALUE.negated();
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    public void testAddToLocalDate() {
        Minutes test = Minutes.minutes(26);
        LocalDateTime date = new LocalDateTime(2006, 6, 1, 0, 0, 0, 0);
        LocalDateTime expected = new LocalDateTime(2006, 6, 1, 0, 26, 0, 0);
        TestCase.assertEquals(expected, date.plus(test));
    }
}

