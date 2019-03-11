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


import Days.FIVE;
import Days.FOUR;
import Days.MAX_VALUE;
import Days.MIN_VALUE;
import Days.ONE;
import Days.SEVEN;
import Days.SIX;
import Days.THREE;
import Days.TWO;
import Days.ZERO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import junit.framework.TestCase;

import static DateTimeConstants.MILLIS_PER_DAY;
import static Days.SEVEN;


/**
 * This class is a Junit unit test for Days.
 *
 * @author Stephen Colebourne
 */
public class TestDays extends TestCase {
    // Test in 2002/03 as time zones are more well known
    // (before the late 90's they were all over the place)
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    public TestDays(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testConstants() {
        TestCase.assertEquals(0, ZERO.getDays());
        TestCase.assertEquals(1, ONE.getDays());
        TestCase.assertEquals(2, TWO.getDays());
        TestCase.assertEquals(3, THREE.getDays());
        TestCase.assertEquals(4, FOUR.getDays());
        TestCase.assertEquals(5, FIVE.getDays());
        TestCase.assertEquals(6, SIX.getDays());
        TestCase.assertEquals(7, SEVEN.getDays());
        TestCase.assertEquals(Integer.MAX_VALUE, MAX_VALUE.getDays());
        TestCase.assertEquals(Integer.MIN_VALUE, MIN_VALUE.getDays());
    }

    // -----------------------------------------------------------------------
    public void testFactory_days_int() {
        TestCase.assertSame(ZERO, Days.days(0));
        TestCase.assertSame(ONE, Days.days(1));
        TestCase.assertSame(TWO, Days.days(2));
        TestCase.assertSame(THREE, Days.days(3));
        TestCase.assertSame(FOUR, Days.days(4));
        TestCase.assertSame(FIVE, Days.days(5));
        TestCase.assertSame(SIX, Days.days(6));
        TestCase.assertSame(SEVEN, Days.days(7));
        TestCase.assertSame(MAX_VALUE, Days.days(Integer.MAX_VALUE));
        TestCase.assertSame(MIN_VALUE, Days.days(Integer.MIN_VALUE));
        TestCase.assertEquals((-1), Days.days((-1)).getDays());
        TestCase.assertEquals(8, Days.days(8).getDays());
    }

    // -----------------------------------------------------------------------
    public void testFactory_daysBetween_RInstant() {
        DateTime start = new DateTime(2006, 6, 9, 12, 0, 0, 0, TestDays.PARIS);
        DateTime end1 = new DateTime(2006, 6, 12, 12, 0, 0, 0, TestDays.PARIS);
        DateTime end2 = new DateTime(2006, 6, 15, 18, 0, 0, 0, TestDays.PARIS);
        TestCase.assertEquals(3, Days.daysBetween(start, end1).getDays());
        TestCase.assertEquals(0, Days.daysBetween(start, start).getDays());
        TestCase.assertEquals(0, Days.daysBetween(end1, end1).getDays());
        TestCase.assertEquals((-3), Days.daysBetween(end1, start).getDays());
        TestCase.assertEquals(6, Days.daysBetween(start, end2).getDays());
    }

    public void testFactory_daysBetween_RPartial_YearMonth() {
        YearMonth start1 = new YearMonth(2011, 1);
        YearMonth start2 = new YearMonth(2012, 1);
        YearMonth end1 = new YearMonth(2011, 3);
        YearMonth end2 = new YearMonth(2012, 3);
        TestCase.assertEquals(59, Days.daysBetween(start1, end1).getDays());
        TestCase.assertEquals(60, Days.daysBetween(start2, end2).getDays());
        TestCase.assertEquals((-59), Days.daysBetween(end1, start1).getDays());
        TestCase.assertEquals((-60), Days.daysBetween(end2, start2).getDays());
    }

    public void testFactory_daysBetween_RPartial_MonthDay() {
        MonthDay start1 = new MonthDay(2, 1);
        MonthDay start2 = new MonthDay(2, 28);
        MonthDay end1 = new MonthDay(2, 28);
        MonthDay end2 = new MonthDay(2, 29);
        TestCase.assertEquals(27, Days.daysBetween(start1, end1).getDays());
        TestCase.assertEquals(28, Days.daysBetween(start1, end2).getDays());
        TestCase.assertEquals(0, Days.daysBetween(start2, end1).getDays());
        TestCase.assertEquals(1, Days.daysBetween(start2, end2).getDays());
        TestCase.assertEquals((-27), Days.daysBetween(end1, start1).getDays());
        TestCase.assertEquals((-28), Days.daysBetween(end2, start1).getDays());
        TestCase.assertEquals(0, Days.daysBetween(end1, start2).getDays());
        TestCase.assertEquals((-1), Days.daysBetween(end2, start2).getDays());
    }

    // -----------------------------------------------------------------------
    public void testFactory_daysIn_RInterval() {
        DateTime start = new DateTime(2006, 6, 9, 12, 0, 0, 0, TestDays.PARIS);
        DateTime end1 = new DateTime(2006, 6, 12, 12, 0, 0, 0, TestDays.PARIS);
        DateTime end2 = new DateTime(2006, 6, 15, 18, 0, 0, 0, TestDays.PARIS);
        TestCase.assertEquals(0, Days.daysIn(((ReadableInterval) (null))).getDays());
        TestCase.assertEquals(3, Days.daysIn(new Interval(start, end1)).getDays());
        TestCase.assertEquals(0, Days.daysIn(new Interval(start, start)).getDays());
        TestCase.assertEquals(0, Days.daysIn(new Interval(end1, end1)).getDays());
        TestCase.assertEquals(6, Days.daysIn(new Interval(start, end2)).getDays());
    }

    // -----------------------------------------------------------------------
    public void testFactory_standardDaysIn_RPeriod() {
        TestCase.assertEquals(0, Days.standardDaysIn(((ReadablePeriod) (null))).getDays());
        TestCase.assertEquals(0, Days.standardDaysIn(Period.ZERO).getDays());
        TestCase.assertEquals(1, Days.standardDaysIn(new Period(0, 0, 0, 1, 0, 0, 0, 0)).getDays());
        TestCase.assertEquals(123, Days.standardDaysIn(Period.days(123)).getDays());
        TestCase.assertEquals((-987), Days.standardDaysIn(Period.days((-987))).getDays());
        TestCase.assertEquals(1, Days.standardDaysIn(Period.hours(47)).getDays());
        TestCase.assertEquals(2, Days.standardDaysIn(Period.hours(48)).getDays());
        TestCase.assertEquals(2, Days.standardDaysIn(Period.hours(49)).getDays());
        TestCase.assertEquals(14, Days.standardDaysIn(Period.weeks(2)).getDays());
        try {
            Days.standardDaysIn(Period.months(1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testFactory_parseDays_String() {
        TestCase.assertEquals(0, Days.parseDays(((String) (null))).getDays());
        TestCase.assertEquals(0, Days.parseDays("P0D").getDays());
        TestCase.assertEquals(1, Days.parseDays("P1D").getDays());
        TestCase.assertEquals((-3), Days.parseDays("P-3D").getDays());
        TestCase.assertEquals(2, Days.parseDays("P0Y0M2D").getDays());
        TestCase.assertEquals(2, Days.parseDays("P2DT0H0M").getDays());
        try {
            Days.parseDays("P1Y1D");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            Days.parseDays("P1DT1H");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    public void testGetMethods() {
        Days test = Days.days(20);
        TestCase.assertEquals(20, test.getDays());
    }

    public void testGetFieldType() {
        Days test = Days.days(20);
        TestCase.assertEquals(DurationFieldType.days(), test.getFieldType());
    }

    public void testGetPeriodType() {
        Days test = Days.days(20);
        TestCase.assertEquals(PeriodType.days(), test.getPeriodType());
    }

    // -----------------------------------------------------------------------
    public void testIsGreaterThan() {
        TestCase.assertEquals(true, THREE.isGreaterThan(TWO));
        TestCase.assertEquals(false, THREE.isGreaterThan(THREE));
        TestCase.assertEquals(false, TWO.isGreaterThan(THREE));
        TestCase.assertEquals(true, ONE.isGreaterThan(null));
        TestCase.assertEquals(false, Days.days((-1)).isGreaterThan(null));
    }

    public void testIsLessThan() {
        TestCase.assertEquals(false, THREE.isLessThan(TWO));
        TestCase.assertEquals(false, THREE.isLessThan(THREE));
        TestCase.assertEquals(true, TWO.isLessThan(THREE));
        TestCase.assertEquals(false, ONE.isLessThan(null));
        TestCase.assertEquals(true, Days.days((-1)).isLessThan(null));
    }

    // -----------------------------------------------------------------------
    public void testToString() {
        Days test = Days.days(20);
        TestCase.assertEquals("P20D", test.toString());
        test = Days.days((-20));
        TestCase.assertEquals("P-20D", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testSerialization() throws Exception {
        Days test = SEVEN;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Days result = ((Days) (ois.readObject()));
        ois.close();
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testToStandardWeeks() {
        Days test = Days.days(14);
        Weeks expected = Weeks.weeks(2);
        TestCase.assertEquals(expected, test.toStandardWeeks());
    }

    public void testToStandardHours() {
        Days test = Days.days(2);
        Hours expected = Hours.hours((2 * 24));
        TestCase.assertEquals(expected, test.toStandardHours());
        try {
            MAX_VALUE.toStandardHours();
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testToStandardMinutes() {
        Days test = Days.days(2);
        Minutes expected = Minutes.minutes(((2 * 24) * 60));
        TestCase.assertEquals(expected, test.toStandardMinutes());
        try {
            MAX_VALUE.toStandardMinutes();
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testToStandardSeconds() {
        Days test = Days.days(2);
        Seconds expected = Seconds.seconds((((2 * 24) * 60) * 60));
        TestCase.assertEquals(expected, test.toStandardSeconds());
        try {
            MAX_VALUE.toStandardSeconds();
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testToStandardDuration() {
        Days test = Days.days(20);
        Duration expected = new Duration((20L * (MILLIS_PER_DAY)));
        TestCase.assertEquals(expected, test.toStandardDuration());
        expected = new Duration((((long) (Integer.MAX_VALUE)) * (MILLIS_PER_DAY)));
        TestCase.assertEquals(expected, MAX_VALUE.toStandardDuration());
    }

    // -----------------------------------------------------------------------
    public void testPlus_int() {
        Days test2 = Days.days(2);
        Days result = test2.plus(3);
        TestCase.assertEquals(2, test2.getDays());
        TestCase.assertEquals(5, result.getDays());
        TestCase.assertEquals(1, ONE.plus(0).getDays());
        try {
            MAX_VALUE.plus(1);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testPlus_Days() {
        Days test2 = Days.days(2);
        Days test3 = Days.days(3);
        Days result = test2.plus(test3);
        TestCase.assertEquals(2, test2.getDays());
        TestCase.assertEquals(3, test3.getDays());
        TestCase.assertEquals(5, result.getDays());
        TestCase.assertEquals(1, ONE.plus(ZERO).getDays());
        TestCase.assertEquals(1, ONE.plus(((Days) (null))).getDays());
        try {
            MAX_VALUE.plus(ONE);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testMinus_int() {
        Days test2 = Days.days(2);
        Days result = test2.minus(3);
        TestCase.assertEquals(2, test2.getDays());
        TestCase.assertEquals((-1), result.getDays());
        TestCase.assertEquals(1, ONE.minus(0).getDays());
        try {
            MIN_VALUE.minus(1);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testMinus_Days() {
        Days test2 = Days.days(2);
        Days test3 = Days.days(3);
        Days result = test2.minus(test3);
        TestCase.assertEquals(2, test2.getDays());
        TestCase.assertEquals(3, test3.getDays());
        TestCase.assertEquals((-1), result.getDays());
        TestCase.assertEquals(1, ONE.minus(ZERO).getDays());
        TestCase.assertEquals(1, ONE.minus(((Days) (null))).getDays());
        try {
            MIN_VALUE.minus(ONE);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testMultipliedBy_int() {
        Days test = Days.days(2);
        TestCase.assertEquals(6, test.multipliedBy(3).getDays());
        TestCase.assertEquals(2, test.getDays());
        TestCase.assertEquals((-6), test.multipliedBy((-3)).getDays());
        TestCase.assertSame(test, test.multipliedBy(1));
        Days halfMax = Days.days((((Integer.MAX_VALUE) / 2) + 1));
        try {
            halfMax.multipliedBy(2);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testDividedBy_int() {
        Days test = Days.days(12);
        TestCase.assertEquals(6, test.dividedBy(2).getDays());
        TestCase.assertEquals(12, test.getDays());
        TestCase.assertEquals(4, test.dividedBy(3).getDays());
        TestCase.assertEquals(3, test.dividedBy(4).getDays());
        TestCase.assertEquals(2, test.dividedBy(5).getDays());
        TestCase.assertEquals(2, test.dividedBy(6).getDays());
        TestCase.assertSame(test, test.dividedBy(1));
        try {
            ONE.dividedBy(0);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testNegated() {
        Days test = Days.days(12);
        TestCase.assertEquals((-12), test.negated().getDays());
        TestCase.assertEquals(12, test.getDays());
        try {
            MIN_VALUE.negated();
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    public void testAddToLocalDate() {
        Days test = Days.days(20);
        LocalDate date = new LocalDate(2006, 6, 1);
        LocalDate expected = new LocalDate(2006, 6, 21);
        TestCase.assertEquals(expected, date.plus(test));
    }
}

