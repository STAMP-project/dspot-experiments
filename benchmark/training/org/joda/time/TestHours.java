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


import Hours.EIGHT;
import Hours.FIVE;
import Hours.FOUR;
import Hours.MAX_VALUE;
import Hours.MIN_VALUE;
import Hours.ONE;
import Hours.SEVEN;
import Hours.SIX;
import Hours.THREE;
import Hours.TWO;
import Hours.ZERO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import junit.framework.TestCase;

import static DateTimeConstants.MILLIS_PER_HOUR;
import static Hours.SEVEN;


/**
 * This class is a Junit unit test for Hours.
 *
 * @author Stephen Colebourne
 */
public class TestHours extends TestCase {
    // Test in 2002/03 as time zones are more well known
    // (before the late 90's they were all over the place)
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    public TestHours(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testConstants() {
        TestCase.assertEquals(0, ZERO.getHours());
        TestCase.assertEquals(1, ONE.getHours());
        TestCase.assertEquals(2, TWO.getHours());
        TestCase.assertEquals(3, THREE.getHours());
        TestCase.assertEquals(4, FOUR.getHours());
        TestCase.assertEquals(5, FIVE.getHours());
        TestCase.assertEquals(6, SIX.getHours());
        TestCase.assertEquals(7, SEVEN.getHours());
        TestCase.assertEquals(8, EIGHT.getHours());
        TestCase.assertEquals(Integer.MAX_VALUE, MAX_VALUE.getHours());
        TestCase.assertEquals(Integer.MIN_VALUE, MIN_VALUE.getHours());
    }

    // -----------------------------------------------------------------------
    public void testFactory_hours_int() {
        TestCase.assertSame(ZERO, Hours.hours(0));
        TestCase.assertSame(ONE, Hours.hours(1));
        TestCase.assertSame(TWO, Hours.hours(2));
        TestCase.assertSame(THREE, Hours.hours(3));
        TestCase.assertSame(FOUR, Hours.hours(4));
        TestCase.assertSame(FIVE, Hours.hours(5));
        TestCase.assertSame(SIX, Hours.hours(6));
        TestCase.assertSame(SEVEN, Hours.hours(7));
        TestCase.assertSame(EIGHT, Hours.hours(8));
        TestCase.assertSame(MAX_VALUE, Hours.hours(Integer.MAX_VALUE));
        TestCase.assertSame(MIN_VALUE, Hours.hours(Integer.MIN_VALUE));
        TestCase.assertEquals((-1), Hours.hours((-1)).getHours());
        TestCase.assertEquals(9, Hours.hours(9).getHours());
    }

    // -----------------------------------------------------------------------
    public void testFactory_hoursBetween_RInstant() {
        DateTime start = new DateTime(2006, 6, 9, 12, 0, 0, 0, TestHours.PARIS);
        DateTime end1 = new DateTime(2006, 6, 9, 15, 0, 0, 0, TestHours.PARIS);
        DateTime end2 = new DateTime(2006, 6, 9, 18, 0, 0, 0, TestHours.PARIS);
        TestCase.assertEquals(3, Hours.hoursBetween(start, end1).getHours());
        TestCase.assertEquals(0, Hours.hoursBetween(start, start).getHours());
        TestCase.assertEquals(0, Hours.hoursBetween(end1, end1).getHours());
        TestCase.assertEquals((-3), Hours.hoursBetween(end1, start).getHours());
        TestCase.assertEquals(6, Hours.hoursBetween(start, end2).getHours());
    }

    public void testFactory_hoursBetween_RPartial() {
        LocalTime start = new LocalTime(12, 0);
        LocalTime end1 = new LocalTime(15, 0);
        @SuppressWarnings("deprecation")
        TimeOfDay end2 = new TimeOfDay(18, 0);
        TestCase.assertEquals(3, Hours.hoursBetween(start, end1).getHours());
        TestCase.assertEquals(0, Hours.hoursBetween(start, start).getHours());
        TestCase.assertEquals(0, Hours.hoursBetween(end1, end1).getHours());
        TestCase.assertEquals((-3), Hours.hoursBetween(end1, start).getHours());
        TestCase.assertEquals(6, Hours.hoursBetween(start, end2).getHours());
    }

    public void testFactory_hoursIn_RInterval() {
        DateTime start = new DateTime(2006, 6, 9, 12, 0, 0, 0, TestHours.PARIS);
        DateTime end1 = new DateTime(2006, 6, 9, 15, 0, 0, 0, TestHours.PARIS);
        DateTime end2 = new DateTime(2006, 6, 9, 18, 0, 0, 0, TestHours.PARIS);
        TestCase.assertEquals(0, Hours.hoursIn(((ReadableInterval) (null))).getHours());
        TestCase.assertEquals(3, Hours.hoursIn(new Interval(start, end1)).getHours());
        TestCase.assertEquals(0, Hours.hoursIn(new Interval(start, start)).getHours());
        TestCase.assertEquals(0, Hours.hoursIn(new Interval(end1, end1)).getHours());
        TestCase.assertEquals(6, Hours.hoursIn(new Interval(start, end2)).getHours());
    }

    public void testFactory_standardHoursIn_RPeriod() {
        TestCase.assertEquals(0, Hours.standardHoursIn(((ReadablePeriod) (null))).getHours());
        TestCase.assertEquals(0, Hours.standardHoursIn(Period.ZERO).getHours());
        TestCase.assertEquals(1, Hours.standardHoursIn(new Period(0, 0, 0, 0, 1, 0, 0, 0)).getHours());
        TestCase.assertEquals(123, Hours.standardHoursIn(Period.hours(123)).getHours());
        TestCase.assertEquals((-987), Hours.standardHoursIn(Period.hours((-987))).getHours());
        TestCase.assertEquals(1, Hours.standardHoursIn(Period.minutes(119)).getHours());
        TestCase.assertEquals(2, Hours.standardHoursIn(Period.minutes(120)).getHours());
        TestCase.assertEquals(2, Hours.standardHoursIn(Period.minutes(121)).getHours());
        TestCase.assertEquals(48, Hours.standardHoursIn(Period.days(2)).getHours());
        try {
            Hours.standardHoursIn(Period.months(1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testFactory_parseHours_String() {
        TestCase.assertEquals(0, Hours.parseHours(((String) (null))).getHours());
        TestCase.assertEquals(0, Hours.parseHours("PT0H").getHours());
        TestCase.assertEquals(1, Hours.parseHours("PT1H").getHours());
        TestCase.assertEquals((-3), Hours.parseHours("PT-3H").getHours());
        TestCase.assertEquals(2, Hours.parseHours("P0Y0M0DT2H").getHours());
        TestCase.assertEquals(2, Hours.parseHours("PT2H0M").getHours());
        try {
            Hours.parseHours("P1Y1D");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            Hours.parseHours("P1DT1H");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    public void testGetMethods() {
        Hours test = Hours.hours(20);
        TestCase.assertEquals(20, test.getHours());
    }

    public void testGetFieldType() {
        Hours test = Hours.hours(20);
        TestCase.assertEquals(DurationFieldType.hours(), test.getFieldType());
    }

    public void testGetPeriodType() {
        Hours test = Hours.hours(20);
        TestCase.assertEquals(PeriodType.hours(), test.getPeriodType());
    }

    // -----------------------------------------------------------------------
    public void testIsGreaterThan() {
        TestCase.assertEquals(true, THREE.isGreaterThan(TWO));
        TestCase.assertEquals(false, THREE.isGreaterThan(THREE));
        TestCase.assertEquals(false, TWO.isGreaterThan(THREE));
        TestCase.assertEquals(true, ONE.isGreaterThan(null));
        TestCase.assertEquals(false, Hours.hours((-1)).isGreaterThan(null));
    }

    public void testIsLessThan() {
        TestCase.assertEquals(false, THREE.isLessThan(TWO));
        TestCase.assertEquals(false, THREE.isLessThan(THREE));
        TestCase.assertEquals(true, TWO.isLessThan(THREE));
        TestCase.assertEquals(false, ONE.isLessThan(null));
        TestCase.assertEquals(true, Hours.hours((-1)).isLessThan(null));
    }

    // -----------------------------------------------------------------------
    public void testToString() {
        Hours test = Hours.hours(20);
        TestCase.assertEquals("PT20H", test.toString());
        test = Hours.hours((-20));
        TestCase.assertEquals("PT-20H", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testSerialization() throws Exception {
        Hours test = SEVEN;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Hours result = ((Hours) (ois.readObject()));
        ois.close();
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testToStandardWeeks() {
        Hours test = Hours.hours(((24 * 7) * 2));
        Weeks expected = Weeks.weeks(2);
        TestCase.assertEquals(expected, test.toStandardWeeks());
    }

    public void testToStandardDays() {
        Hours test = Hours.hours((24 * 2));
        Days expected = Days.days(2);
        TestCase.assertEquals(expected, test.toStandardDays());
    }

    public void testToStandardMinutes() {
        Hours test = Hours.hours(3);
        Minutes expected = Minutes.minutes((3 * 60));
        TestCase.assertEquals(expected, test.toStandardMinutes());
        try {
            MAX_VALUE.toStandardMinutes();
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testToStandardSeconds() {
        Hours test = Hours.hours(3);
        Seconds expected = Seconds.seconds(((3 * 60) * 60));
        TestCase.assertEquals(expected, test.toStandardSeconds());
        try {
            MAX_VALUE.toStandardSeconds();
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testToStandardDuration() {
        Hours test = Hours.hours(20);
        Duration expected = new Duration((20L * (MILLIS_PER_HOUR)));
        TestCase.assertEquals(expected, test.toStandardDuration());
        expected = new Duration((((long) (Integer.MAX_VALUE)) * (MILLIS_PER_HOUR)));
        TestCase.assertEquals(expected, MAX_VALUE.toStandardDuration());
    }

    // -----------------------------------------------------------------------
    public void testPlus_int() {
        Hours test2 = Hours.hours(2);
        Hours result = test2.plus(3);
        TestCase.assertEquals(2, test2.getHours());
        TestCase.assertEquals(5, result.getHours());
        TestCase.assertEquals(1, ONE.plus(0).getHours());
        try {
            MAX_VALUE.plus(1);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testPlus_Hours() {
        Hours test2 = Hours.hours(2);
        Hours test3 = Hours.hours(3);
        Hours result = test2.plus(test3);
        TestCase.assertEquals(2, test2.getHours());
        TestCase.assertEquals(3, test3.getHours());
        TestCase.assertEquals(5, result.getHours());
        TestCase.assertEquals(1, ONE.plus(ZERO).getHours());
        TestCase.assertEquals(1, ONE.plus(((Hours) (null))).getHours());
        try {
            MAX_VALUE.plus(ONE);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testMinus_int() {
        Hours test2 = Hours.hours(2);
        Hours result = test2.minus(3);
        TestCase.assertEquals(2, test2.getHours());
        TestCase.assertEquals((-1), result.getHours());
        TestCase.assertEquals(1, ONE.minus(0).getHours());
        try {
            MIN_VALUE.minus(1);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testMinus_Hours() {
        Hours test2 = Hours.hours(2);
        Hours test3 = Hours.hours(3);
        Hours result = test2.minus(test3);
        TestCase.assertEquals(2, test2.getHours());
        TestCase.assertEquals(3, test3.getHours());
        TestCase.assertEquals((-1), result.getHours());
        TestCase.assertEquals(1, ONE.minus(ZERO).getHours());
        TestCase.assertEquals(1, ONE.minus(((Hours) (null))).getHours());
        try {
            MIN_VALUE.minus(ONE);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testMultipliedBy_int() {
        Hours test = Hours.hours(2);
        TestCase.assertEquals(6, test.multipliedBy(3).getHours());
        TestCase.assertEquals(2, test.getHours());
        TestCase.assertEquals((-6), test.multipliedBy((-3)).getHours());
        TestCase.assertSame(test, test.multipliedBy(1));
        Hours halfMax = Hours.hours((((Integer.MAX_VALUE) / 2) + 1));
        try {
            halfMax.multipliedBy(2);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testDividedBy_int() {
        Hours test = Hours.hours(12);
        TestCase.assertEquals(6, test.dividedBy(2).getHours());
        TestCase.assertEquals(12, test.getHours());
        TestCase.assertEquals(4, test.dividedBy(3).getHours());
        TestCase.assertEquals(3, test.dividedBy(4).getHours());
        TestCase.assertEquals(2, test.dividedBy(5).getHours());
        TestCase.assertEquals(2, test.dividedBy(6).getHours());
        TestCase.assertSame(test, test.dividedBy(1));
        try {
            ONE.dividedBy(0);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testNegated() {
        Hours test = Hours.hours(12);
        TestCase.assertEquals((-12), test.negated().getHours());
        TestCase.assertEquals(12, test.getHours());
        try {
            MIN_VALUE.negated();
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    public void testAddToLocalDate() {
        Hours test = Hours.hours(26);
        LocalDateTime date = new LocalDateTime(2006, 6, 1, 0, 0, 0, 0);
        LocalDateTime expected = new LocalDateTime(2006, 6, 2, 2, 0, 0, 0);
        TestCase.assertEquals(expected, date.plus(test));
    }
}

