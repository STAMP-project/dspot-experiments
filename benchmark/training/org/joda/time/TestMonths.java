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


import Months.EIGHT;
import Months.ELEVEN;
import Months.FIVE;
import Months.FOUR;
import Months.MAX_VALUE;
import Months.MIN_VALUE;
import Months.NINE;
import Months.ONE;
import Months.SEVEN;
import Months.SIX;
import Months.TEN;
import Months.THREE;
import Months.TWELVE;
import Months.TWO;
import Months.ZERO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import junit.framework.TestCase;

import static Months.THREE;


/**
 * This class is a Junit unit test for Months.
 *
 * @author Stephen Colebourne
 */
public class TestMonths extends TestCase {
    // Test in 2002/03 as time zones are more well known
    // (before the late 90's they were all over the place)
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    public TestMonths(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testConstants() {
        TestCase.assertEquals(0, ZERO.getMonths());
        TestCase.assertEquals(1, ONE.getMonths());
        TestCase.assertEquals(2, TWO.getMonths());
        TestCase.assertEquals(3, THREE.getMonths());
        TestCase.assertEquals(4, FOUR.getMonths());
        TestCase.assertEquals(5, FIVE.getMonths());
        TestCase.assertEquals(6, SIX.getMonths());
        TestCase.assertEquals(7, SEVEN.getMonths());
        TestCase.assertEquals(8, EIGHT.getMonths());
        TestCase.assertEquals(9, NINE.getMonths());
        TestCase.assertEquals(10, TEN.getMonths());
        TestCase.assertEquals(11, ELEVEN.getMonths());
        TestCase.assertEquals(12, TWELVE.getMonths());
        TestCase.assertEquals(Integer.MAX_VALUE, MAX_VALUE.getMonths());
        TestCase.assertEquals(Integer.MIN_VALUE, MIN_VALUE.getMonths());
    }

    // -----------------------------------------------------------------------
    public void testFactory_months_int() {
        TestCase.assertSame(ZERO, Months.months(0));
        TestCase.assertSame(ONE, Months.months(1));
        TestCase.assertSame(TWO, Months.months(2));
        TestCase.assertSame(THREE, Months.months(3));
        TestCase.assertSame(FOUR, Months.months(4));
        TestCase.assertSame(FIVE, Months.months(5));
        TestCase.assertSame(SIX, Months.months(6));
        TestCase.assertSame(SEVEN, Months.months(7));
        TestCase.assertSame(EIGHT, Months.months(8));
        TestCase.assertSame(NINE, Months.months(9));
        TestCase.assertSame(TEN, Months.months(10));
        TestCase.assertSame(ELEVEN, Months.months(11));
        TestCase.assertSame(TWELVE, Months.months(12));
        TestCase.assertSame(MAX_VALUE, Months.months(Integer.MAX_VALUE));
        TestCase.assertSame(MIN_VALUE, Months.months(Integer.MIN_VALUE));
        TestCase.assertEquals((-1), Months.months((-1)).getMonths());
        TestCase.assertEquals(13, Months.months(13).getMonths());
    }

    // -----------------------------------------------------------------------
    public void testFactory_monthsBetween_RInstant() {
        DateTime start = new DateTime(2006, 6, 9, 12, 0, 0, 0, TestMonths.PARIS);
        DateTime end1 = new DateTime(2006, 9, 9, 12, 0, 0, 0, TestMonths.PARIS);
        DateTime end2 = new DateTime(2006, 12, 9, 12, 0, 0, 0, TestMonths.PARIS);
        TestCase.assertEquals(3, Months.monthsBetween(start, end1).getMonths());
        TestCase.assertEquals(0, Months.monthsBetween(start, start).getMonths());
        TestCase.assertEquals(0, Months.monthsBetween(end1, end1).getMonths());
        TestCase.assertEquals((-3), Months.monthsBetween(end1, start).getMonths());
        TestCase.assertEquals(6, Months.monthsBetween(start, end2).getMonths());
    }

    public void testFactory_monthsBetween_RInstant_LocalDate_EndMonth() {
        TestCase.assertEquals(0, Months.monthsBetween(new DateTime(2006, 1, 31, 0, 0, 0, TestMonths.PARIS), new DateTime(2006, 2, 27, 0, 0, 0, TestMonths.PARIS)).getMonths());
        TestCase.assertEquals(1, Months.monthsBetween(new DateTime(2006, 1, 28, 0, 0, 0, TestMonths.PARIS), new DateTime(2006, 2, 28, 0, 0, 0, TestMonths.PARIS)).getMonths());
        TestCase.assertEquals(1, Months.monthsBetween(new DateTime(2006, 1, 29, 0, 0, 0, TestMonths.PARIS), new DateTime(2006, 2, 28, 0, 0, 0, TestMonths.PARIS)).getMonths());
        TestCase.assertEquals(1, Months.monthsBetween(new DateTime(2006, 1, 30, 0, 0, 0, TestMonths.PARIS), new DateTime(2006, 2, 28, 0, 0, 0, TestMonths.PARIS)).getMonths());
        TestCase.assertEquals(1, Months.monthsBetween(new DateTime(2006, 1, 31, 0, 0, 0, TestMonths.PARIS), new DateTime(2006, 2, 28, 0, 0, 0, TestMonths.PARIS)).getMonths());
        TestCase.assertEquals(1, Months.monthsBetween(new DateTime(2006, 1, 31, 0, 0, 0, TestMonths.PARIS), new DateTime(2006, 3, 1, 0, 0, 0, TestMonths.PARIS)).getMonths());
    }

    public void testFactory_monthsBetween_RPartial_LocalDate_EndMonth() {
        TestCase.assertEquals(0, Months.monthsBetween(new LocalDate(2006, 1, 31), new LocalDate(2006, 2, 27)).getMonths());
        TestCase.assertEquals(1, Months.monthsBetween(new LocalDate(2006, 1, 28), new LocalDate(2006, 2, 28)).getMonths());
        TestCase.assertEquals(1, Months.monthsBetween(new LocalDate(2006, 1, 29), new LocalDate(2006, 2, 28)).getMonths());
        TestCase.assertEquals(1, Months.monthsBetween(new LocalDate(2006, 1, 30), new LocalDate(2006, 2, 28)).getMonths());
        TestCase.assertEquals(1, Months.monthsBetween(new LocalDate(2006, 1, 31), new LocalDate(2006, 2, 28)).getMonths());
        TestCase.assertEquals(1, Months.monthsBetween(new LocalDate(2006, 1, 31), new LocalDate(2006, 3, 1)).getMonths());
    }

    public void testFactory_monthsBetween_RPartial_YearMonth() {
        YearMonth start1 = new YearMonth(2011, 1);
        for (int i = 0; i < 6; i++) {
            YearMonth start2 = new YearMonth((2011 + i), 1);
            YearMonth end = new YearMonth((2011 + i), 3);
            TestCase.assertEquals(((i * 12) + 2), Months.monthsBetween(start1, end).getMonths());
            TestCase.assertEquals(2, Months.monthsBetween(start2, end).getMonths());
        }
    }

    public void testFactory_monthsBetween_RPartial_MonthDay() {
        MonthDay start = new MonthDay(2, 1);
        MonthDay end1 = new MonthDay(2, 28);
        MonthDay end2 = new MonthDay(2, 29);
        MonthDay end3 = new MonthDay(3, 1);
        TestCase.assertEquals(0, Months.monthsBetween(start, end1).getMonths());
        TestCase.assertEquals(0, Months.monthsBetween(start, end2).getMonths());
        TestCase.assertEquals(1, Months.monthsBetween(start, end3).getMonths());
        TestCase.assertEquals(0, Months.monthsBetween(end1, start).getMonths());
        TestCase.assertEquals(0, Months.monthsBetween(end2, start).getMonths());
        TestCase.assertEquals((-1), Months.monthsBetween(end3, start).getMonths());
    }

    // -------------------------------------------------------------------------
    public void testFactory_monthsIn_RInterval() {
        DateTime start = new DateTime(2006, 6, 9, 12, 0, 0, 0, TestMonths.PARIS);
        DateTime end1 = new DateTime(2006, 9, 9, 12, 0, 0, 0, TestMonths.PARIS);
        DateTime end2 = new DateTime(2006, 12, 9, 12, 0, 0, 0, TestMonths.PARIS);
        TestCase.assertEquals(0, Months.monthsIn(((ReadableInterval) (null))).getMonths());
        TestCase.assertEquals(3, Months.monthsIn(new Interval(start, end1)).getMonths());
        TestCase.assertEquals(0, Months.monthsIn(new Interval(start, start)).getMonths());
        TestCase.assertEquals(0, Months.monthsIn(new Interval(end1, end1)).getMonths());
        TestCase.assertEquals(6, Months.monthsIn(new Interval(start, end2)).getMonths());
    }

    public void testFactory_parseMonths_String() {
        TestCase.assertEquals(0, Months.parseMonths(((String) (null))).getMonths());
        TestCase.assertEquals(0, Months.parseMonths("P0M").getMonths());
        TestCase.assertEquals(1, Months.parseMonths("P1M").getMonths());
        TestCase.assertEquals((-3), Months.parseMonths("P-3M").getMonths());
        TestCase.assertEquals(2, Months.parseMonths("P0Y2M").getMonths());
        TestCase.assertEquals(2, Months.parseMonths("P2MT0H0M").getMonths());
        try {
            Months.parseMonths("P1Y1D");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            Months.parseMonths("P1MT1H");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    public void testGetMethods() {
        Months test = Months.months(20);
        TestCase.assertEquals(20, test.getMonths());
    }

    public void testGetFieldType() {
        Months test = Months.months(20);
        TestCase.assertEquals(DurationFieldType.months(), test.getFieldType());
    }

    public void testGetPeriodType() {
        Months test = Months.months(20);
        TestCase.assertEquals(PeriodType.months(), test.getPeriodType());
    }

    // -----------------------------------------------------------------------
    public void testIsGreaterThan() {
        TestCase.assertEquals(true, THREE.isGreaterThan(TWO));
        TestCase.assertEquals(false, THREE.isGreaterThan(THREE));
        TestCase.assertEquals(false, TWO.isGreaterThan(THREE));
        TestCase.assertEquals(true, ONE.isGreaterThan(null));
        TestCase.assertEquals(false, Months.months((-1)).isGreaterThan(null));
    }

    public void testIsLessThan() {
        TestCase.assertEquals(false, THREE.isLessThan(TWO));
        TestCase.assertEquals(false, THREE.isLessThan(THREE));
        TestCase.assertEquals(true, TWO.isLessThan(THREE));
        TestCase.assertEquals(false, ONE.isLessThan(null));
        TestCase.assertEquals(true, Months.months((-1)).isLessThan(null));
    }

    // -----------------------------------------------------------------------
    public void testToString() {
        Months test = Months.months(20);
        TestCase.assertEquals("P20M", test.toString());
        test = Months.months((-20));
        TestCase.assertEquals("P-20M", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testSerialization() throws Exception {
        Months test = THREE;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Months result = ((Months) (ois.readObject()));
        ois.close();
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testPlus_int() {
        Months test2 = Months.months(2);
        Months result = test2.plus(3);
        TestCase.assertEquals(2, test2.getMonths());
        TestCase.assertEquals(5, result.getMonths());
        TestCase.assertEquals(1, ONE.plus(0).getMonths());
        try {
            MAX_VALUE.plus(1);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testPlus_Months() {
        Months test2 = Months.months(2);
        Months test3 = Months.months(3);
        Months result = test2.plus(test3);
        TestCase.assertEquals(2, test2.getMonths());
        TestCase.assertEquals(3, test3.getMonths());
        TestCase.assertEquals(5, result.getMonths());
        TestCase.assertEquals(1, ONE.plus(ZERO).getMonths());
        TestCase.assertEquals(1, ONE.plus(((Months) (null))).getMonths());
        try {
            MAX_VALUE.plus(ONE);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testMinus_int() {
        Months test2 = Months.months(2);
        Months result = test2.minus(3);
        TestCase.assertEquals(2, test2.getMonths());
        TestCase.assertEquals((-1), result.getMonths());
        TestCase.assertEquals(1, ONE.minus(0).getMonths());
        try {
            MIN_VALUE.minus(1);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testMinus_Months() {
        Months test2 = Months.months(2);
        Months test3 = Months.months(3);
        Months result = test2.minus(test3);
        TestCase.assertEquals(2, test2.getMonths());
        TestCase.assertEquals(3, test3.getMonths());
        TestCase.assertEquals((-1), result.getMonths());
        TestCase.assertEquals(1, ONE.minus(ZERO).getMonths());
        TestCase.assertEquals(1, ONE.minus(((Months) (null))).getMonths());
        try {
            MIN_VALUE.minus(ONE);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testMultipliedBy_int() {
        Months test = Months.months(2);
        TestCase.assertEquals(6, test.multipliedBy(3).getMonths());
        TestCase.assertEquals(2, test.getMonths());
        TestCase.assertEquals((-6), test.multipliedBy((-3)).getMonths());
        TestCase.assertSame(test, test.multipliedBy(1));
        Months halfMax = Months.months((((Integer.MAX_VALUE) / 2) + 1));
        try {
            halfMax.multipliedBy(2);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testDividedBy_int() {
        Months test = Months.months(12);
        TestCase.assertEquals(6, test.dividedBy(2).getMonths());
        TestCase.assertEquals(12, test.getMonths());
        TestCase.assertEquals(4, test.dividedBy(3).getMonths());
        TestCase.assertEquals(3, test.dividedBy(4).getMonths());
        TestCase.assertEquals(2, test.dividedBy(5).getMonths());
        TestCase.assertEquals(2, test.dividedBy(6).getMonths());
        TestCase.assertSame(test, test.dividedBy(1));
        try {
            ONE.dividedBy(0);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testNegated() {
        Months test = Months.months(12);
        TestCase.assertEquals((-12), test.negated().getMonths());
        TestCase.assertEquals(12, test.getMonths());
        try {
            MIN_VALUE.negated();
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    public void testAddToLocalDate() {
        Months test = Months.months(3);
        LocalDate date = new LocalDate(2006, 6, 1);
        LocalDate expected = new LocalDate(2006, 9, 1);
        TestCase.assertEquals(expected, date.plus(test));
    }
}

