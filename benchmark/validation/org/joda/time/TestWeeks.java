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


import Weeks.MAX_VALUE;
import Weeks.MIN_VALUE;
import Weeks.ONE;
import Weeks.THREE;
import Weeks.TWO;
import Weeks.ZERO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import junit.framework.TestCase;

import static DateTimeConstants.MILLIS_PER_WEEK;
import static Weeks.THREE;


/**
 * This class is a Junit unit test for Weeks.
 *
 * @author Stephen Colebourne
 */
public class TestWeeks extends TestCase {
    // Test in 2002/03 as time zones are more well known
    // (before the late 90's they were all over the place)
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    public TestWeeks(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testConstants() {
        TestCase.assertEquals(0, ZERO.getWeeks());
        TestCase.assertEquals(1, ONE.getWeeks());
        TestCase.assertEquals(2, TWO.getWeeks());
        TestCase.assertEquals(3, THREE.getWeeks());
        TestCase.assertEquals(Integer.MAX_VALUE, MAX_VALUE.getWeeks());
        TestCase.assertEquals(Integer.MIN_VALUE, MIN_VALUE.getWeeks());
    }

    // -----------------------------------------------------------------------
    public void testFactory_weeks_int() {
        TestCase.assertSame(ZERO, Weeks.weeks(0));
        TestCase.assertSame(ONE, Weeks.weeks(1));
        TestCase.assertSame(TWO, Weeks.weeks(2));
        TestCase.assertSame(THREE, Weeks.weeks(3));
        TestCase.assertSame(MAX_VALUE, Weeks.weeks(Integer.MAX_VALUE));
        TestCase.assertSame(MIN_VALUE, Weeks.weeks(Integer.MIN_VALUE));
        TestCase.assertEquals((-1), Weeks.weeks((-1)).getWeeks());
        TestCase.assertEquals(4, Weeks.weeks(4).getWeeks());
    }

    // -----------------------------------------------------------------------
    public void testFactory_weeksBetween_RInstant() {
        DateTime start = new DateTime(2006, 6, 9, 12, 0, 0, 0, TestWeeks.PARIS);
        DateTime end1 = new DateTime(2006, 6, 30, 12, 0, 0, 0, TestWeeks.PARIS);
        DateTime end2 = new DateTime(2006, 7, 21, 12, 0, 0, 0, TestWeeks.PARIS);
        TestCase.assertEquals(3, Weeks.weeksBetween(start, end1).getWeeks());
        TestCase.assertEquals(0, Weeks.weeksBetween(start, start).getWeeks());
        TestCase.assertEquals(0, Weeks.weeksBetween(end1, end1).getWeeks());
        TestCase.assertEquals((-3), Weeks.weeksBetween(end1, start).getWeeks());
        TestCase.assertEquals(6, Weeks.weeksBetween(start, end2).getWeeks());
    }

    public void testFactory_weeksIn_RInterval() {
        DateTime start = new DateTime(2006, 6, 9, 12, 0, 0, 0, TestWeeks.PARIS);
        DateTime end1 = new DateTime(2006, 6, 30, 12, 0, 0, 0, TestWeeks.PARIS);
        DateTime end2 = new DateTime(2006, 7, 21, 12, 0, 0, 0, TestWeeks.PARIS);
        TestCase.assertEquals(0, Weeks.weeksIn(((ReadableInterval) (null))).getWeeks());
        TestCase.assertEquals(3, Weeks.weeksIn(new Interval(start, end1)).getWeeks());
        TestCase.assertEquals(0, Weeks.weeksIn(new Interval(start, start)).getWeeks());
        TestCase.assertEquals(0, Weeks.weeksIn(new Interval(end1, end1)).getWeeks());
        TestCase.assertEquals(6, Weeks.weeksIn(new Interval(start, end2)).getWeeks());
    }

    public void testFactory_standardWeeksIn_RPeriod() {
        TestCase.assertEquals(0, Weeks.standardWeeksIn(((ReadablePeriod) (null))).getWeeks());
        TestCase.assertEquals(0, Weeks.standardWeeksIn(Period.ZERO).getWeeks());
        TestCase.assertEquals(1, Weeks.standardWeeksIn(new Period(0, 0, 1, 0, 0, 0, 0, 0)).getWeeks());
        TestCase.assertEquals(123, Weeks.standardWeeksIn(Period.weeks(123)).getWeeks());
        TestCase.assertEquals((-987), Weeks.standardWeeksIn(Period.weeks((-987))).getWeeks());
        TestCase.assertEquals(1, Weeks.standardWeeksIn(Period.days(13)).getWeeks());
        TestCase.assertEquals(2, Weeks.standardWeeksIn(Period.days(14)).getWeeks());
        TestCase.assertEquals(2, Weeks.standardWeeksIn(Period.days(15)).getWeeks());
        try {
            Weeks.standardWeeksIn(Period.months(1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testFactory_parseWeeks_String() {
        TestCase.assertEquals(0, Weeks.parseWeeks(((String) (null))).getWeeks());
        TestCase.assertEquals(0, Weeks.parseWeeks("P0W").getWeeks());
        TestCase.assertEquals(1, Weeks.parseWeeks("P1W").getWeeks());
        TestCase.assertEquals((-3), Weeks.parseWeeks("P-3W").getWeeks());
        TestCase.assertEquals(2, Weeks.parseWeeks("P0Y0M2W").getWeeks());
        TestCase.assertEquals(2, Weeks.parseWeeks("P2WT0H0M").getWeeks());
        try {
            Weeks.parseWeeks("P1Y1D");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            Weeks.parseWeeks("P1WT1H");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    public void testGetMethods() {
        Weeks test = Weeks.weeks(20);
        TestCase.assertEquals(20, test.getWeeks());
    }

    public void testGetFieldType() {
        Weeks test = Weeks.weeks(20);
        TestCase.assertEquals(DurationFieldType.weeks(), test.getFieldType());
    }

    public void testGetPeriodType() {
        Weeks test = Weeks.weeks(20);
        TestCase.assertEquals(PeriodType.weeks(), test.getPeriodType());
    }

    // -----------------------------------------------------------------------
    public void testIsGreaterThan() {
        TestCase.assertEquals(true, THREE.isGreaterThan(TWO));
        TestCase.assertEquals(false, THREE.isGreaterThan(THREE));
        TestCase.assertEquals(false, TWO.isGreaterThan(THREE));
        TestCase.assertEquals(true, ONE.isGreaterThan(null));
        TestCase.assertEquals(false, Weeks.weeks((-1)).isGreaterThan(null));
    }

    public void testIsLessThan() {
        TestCase.assertEquals(false, THREE.isLessThan(TWO));
        TestCase.assertEquals(false, THREE.isLessThan(THREE));
        TestCase.assertEquals(true, TWO.isLessThan(THREE));
        TestCase.assertEquals(false, ONE.isLessThan(null));
        TestCase.assertEquals(true, Weeks.weeks((-1)).isLessThan(null));
    }

    // -----------------------------------------------------------------------
    public void testToString() {
        Weeks test = Weeks.weeks(20);
        TestCase.assertEquals("P20W", test.toString());
        test = Weeks.weeks((-20));
        TestCase.assertEquals("P-20W", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testSerialization() throws Exception {
        Weeks test = THREE;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Weeks result = ((Weeks) (ois.readObject()));
        ois.close();
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testToStandardDays() {
        Weeks test = Weeks.weeks(2);
        Days expected = Days.days(14);
        TestCase.assertEquals(expected, test.toStandardDays());
        try {
            MAX_VALUE.toStandardDays();
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testToStandardHours() {
        Weeks test = Weeks.weeks(2);
        Hours expected = Hours.hours(((2 * 7) * 24));
        TestCase.assertEquals(expected, test.toStandardHours());
        try {
            MAX_VALUE.toStandardHours();
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testToStandardMinutes() {
        Weeks test = Weeks.weeks(2);
        Minutes expected = Minutes.minutes((((2 * 7) * 24) * 60));
        TestCase.assertEquals(expected, test.toStandardMinutes());
        try {
            MAX_VALUE.toStandardMinutes();
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testToStandardSeconds() {
        Weeks test = Weeks.weeks(2);
        Seconds expected = Seconds.seconds(((((2 * 7) * 24) * 60) * 60));
        TestCase.assertEquals(expected, test.toStandardSeconds());
        try {
            MAX_VALUE.toStandardSeconds();
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testToStandardDuration() {
        Weeks test = Weeks.weeks(20);
        Duration expected = new Duration((20L * (MILLIS_PER_WEEK)));
        TestCase.assertEquals(expected, test.toStandardDuration());
        expected = new Duration((((long) (Integer.MAX_VALUE)) * (MILLIS_PER_WEEK)));
        TestCase.assertEquals(expected, MAX_VALUE.toStandardDuration());
    }

    // -----------------------------------------------------------------------
    public void testPlus_int() {
        Weeks test2 = Weeks.weeks(2);
        Weeks result = test2.plus(3);
        TestCase.assertEquals(2, test2.getWeeks());
        TestCase.assertEquals(5, result.getWeeks());
        TestCase.assertEquals(1, ONE.plus(0).getWeeks());
        try {
            MAX_VALUE.plus(1);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testPlus_Weeks() {
        Weeks test2 = Weeks.weeks(2);
        Weeks test3 = Weeks.weeks(3);
        Weeks result = test2.plus(test3);
        TestCase.assertEquals(2, test2.getWeeks());
        TestCase.assertEquals(3, test3.getWeeks());
        TestCase.assertEquals(5, result.getWeeks());
        TestCase.assertEquals(1, ONE.plus(ZERO).getWeeks());
        TestCase.assertEquals(1, ONE.plus(((Weeks) (null))).getWeeks());
        try {
            MAX_VALUE.plus(ONE);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testMinus_int() {
        Weeks test2 = Weeks.weeks(2);
        Weeks result = test2.minus(3);
        TestCase.assertEquals(2, test2.getWeeks());
        TestCase.assertEquals((-1), result.getWeeks());
        TestCase.assertEquals(1, ONE.minus(0).getWeeks());
        try {
            MIN_VALUE.minus(1);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testMinus_Weeks() {
        Weeks test2 = Weeks.weeks(2);
        Weeks test3 = Weeks.weeks(3);
        Weeks result = test2.minus(test3);
        TestCase.assertEquals(2, test2.getWeeks());
        TestCase.assertEquals(3, test3.getWeeks());
        TestCase.assertEquals((-1), result.getWeeks());
        TestCase.assertEquals(1, ONE.minus(ZERO).getWeeks());
        TestCase.assertEquals(1, ONE.minus(((Weeks) (null))).getWeeks());
        try {
            MIN_VALUE.minus(ONE);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testMultipliedBy_int() {
        Weeks test = Weeks.weeks(2);
        TestCase.assertEquals(6, test.multipliedBy(3).getWeeks());
        TestCase.assertEquals(2, test.getWeeks());
        TestCase.assertEquals((-6), test.multipliedBy((-3)).getWeeks());
        TestCase.assertSame(test, test.multipliedBy(1));
        Weeks halfMax = Weeks.weeks((((Integer.MAX_VALUE) / 2) + 1));
        try {
            halfMax.multipliedBy(2);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testDividedBy_int() {
        Weeks test = Weeks.weeks(12);
        TestCase.assertEquals(6, test.dividedBy(2).getWeeks());
        TestCase.assertEquals(12, test.getWeeks());
        TestCase.assertEquals(4, test.dividedBy(3).getWeeks());
        TestCase.assertEquals(3, test.dividedBy(4).getWeeks());
        TestCase.assertEquals(2, test.dividedBy(5).getWeeks());
        TestCase.assertEquals(2, test.dividedBy(6).getWeeks());
        TestCase.assertSame(test, test.dividedBy(1));
        try {
            ONE.dividedBy(0);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testNegated() {
        Weeks test = Weeks.weeks(12);
        TestCase.assertEquals((-12), test.negated().getWeeks());
        TestCase.assertEquals(12, test.getWeeks());
        try {
            MIN_VALUE.negated();
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    public void testAddToLocalDate() {
        Weeks test = Weeks.weeks(3);
        LocalDate date = new LocalDate(2006, 6, 1);
        LocalDate expected = new LocalDate(2006, 6, 22);
        TestCase.assertEquals(expected, date.plus(test));
    }
}

