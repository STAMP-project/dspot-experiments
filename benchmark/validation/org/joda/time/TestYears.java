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


import Years.MAX_VALUE;
import Years.MIN_VALUE;
import Years.ONE;
import Years.THREE;
import Years.TWO;
import Years.ZERO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import junit.framework.TestCase;

import static Years.THREE;


/**
 * This class is a Junit unit test for Years.
 *
 * @author Stephen Colebourne
 */
public class TestYears extends TestCase {
    // Test in 2002/03 as time zones are more well known
    // (before the late 90's they were all over the place)
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    public TestYears(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testConstants() {
        TestCase.assertEquals(0, ZERO.getYears());
        TestCase.assertEquals(1, ONE.getYears());
        TestCase.assertEquals(2, TWO.getYears());
        TestCase.assertEquals(3, THREE.getYears());
        TestCase.assertEquals(Integer.MAX_VALUE, MAX_VALUE.getYears());
        TestCase.assertEquals(Integer.MIN_VALUE, MIN_VALUE.getYears());
    }

    // -----------------------------------------------------------------------
    public void testFactory_years_int() {
        TestCase.assertSame(ZERO, Years.years(0));
        TestCase.assertSame(ONE, Years.years(1));
        TestCase.assertSame(TWO, Years.years(2));
        TestCase.assertSame(THREE, Years.years(3));
        TestCase.assertSame(MAX_VALUE, Years.years(Integer.MAX_VALUE));
        TestCase.assertSame(MIN_VALUE, Years.years(Integer.MIN_VALUE));
        TestCase.assertEquals((-1), Years.years((-1)).getYears());
        TestCase.assertEquals(4, Years.years(4).getYears());
    }

    // -----------------------------------------------------------------------
    public void testFactory_yearsBetween_RInstant() {
        DateTime start = new DateTime(2006, 6, 9, 12, 0, 0, 0, TestYears.PARIS);
        DateTime end1 = new DateTime(2009, 6, 9, 12, 0, 0, 0, TestYears.PARIS);
        DateTime end2 = new DateTime(2012, 6, 9, 12, 0, 0, 0, TestYears.PARIS);
        TestCase.assertEquals(3, Years.yearsBetween(start, end1).getYears());
        TestCase.assertEquals(0, Years.yearsBetween(start, start).getYears());
        TestCase.assertEquals(0, Years.yearsBetween(end1, end1).getYears());
        TestCase.assertEquals((-3), Years.yearsBetween(end1, start).getYears());
        TestCase.assertEquals(6, Years.yearsBetween(start, end2).getYears());
    }

    public void testFactory_yearsIn_RInterval() {
        DateTime start = new DateTime(2006, 6, 9, 12, 0, 0, 0, TestYears.PARIS);
        DateTime end1 = new DateTime(2009, 6, 9, 12, 0, 0, 0, TestYears.PARIS);
        DateTime end2 = new DateTime(2012, 6, 9, 12, 0, 0, 0, TestYears.PARIS);
        TestCase.assertEquals(0, Years.yearsIn(((ReadableInterval) (null))).getYears());
        TestCase.assertEquals(3, Years.yearsIn(new Interval(start, end1)).getYears());
        TestCase.assertEquals(0, Years.yearsIn(new Interval(start, start)).getYears());
        TestCase.assertEquals(0, Years.yearsIn(new Interval(end1, end1)).getYears());
        TestCase.assertEquals(6, Years.yearsIn(new Interval(start, end2)).getYears());
    }

    public void testFactory_parseYears_String() {
        TestCase.assertEquals(0, Years.parseYears(((String) (null))).getYears());
        TestCase.assertEquals(0, Years.parseYears("P0Y").getYears());
        TestCase.assertEquals(1, Years.parseYears("P1Y").getYears());
        TestCase.assertEquals((-3), Years.parseYears("P-3Y").getYears());
        TestCase.assertEquals(2, Years.parseYears("P2Y0M").getYears());
        TestCase.assertEquals(2, Years.parseYears("P2YT0H0M").getYears());
        try {
            Years.parseYears("P1M1D");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            Years.parseYears("P1YT1H");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    public void testGetMethods() {
        Years test = Years.years(20);
        TestCase.assertEquals(20, test.getYears());
    }

    public void testGetFieldType() {
        Years test = Years.years(20);
        TestCase.assertEquals(DurationFieldType.years(), test.getFieldType());
    }

    public void testGetPeriodType() {
        Years test = Years.years(20);
        TestCase.assertEquals(PeriodType.years(), test.getPeriodType());
    }

    // -----------------------------------------------------------------------
    public void testIsGreaterThan() {
        TestCase.assertEquals(true, THREE.isGreaterThan(TWO));
        TestCase.assertEquals(false, THREE.isGreaterThan(THREE));
        TestCase.assertEquals(false, TWO.isGreaterThan(THREE));
        TestCase.assertEquals(true, ONE.isGreaterThan(null));
        TestCase.assertEquals(false, Years.years((-1)).isGreaterThan(null));
    }

    public void testIsLessThan() {
        TestCase.assertEquals(false, THREE.isLessThan(TWO));
        TestCase.assertEquals(false, THREE.isLessThan(THREE));
        TestCase.assertEquals(true, TWO.isLessThan(THREE));
        TestCase.assertEquals(false, ONE.isLessThan(null));
        TestCase.assertEquals(true, Years.years((-1)).isLessThan(null));
    }

    // -----------------------------------------------------------------------
    public void testToString() {
        Years test = Years.years(20);
        TestCase.assertEquals("P20Y", test.toString());
        test = Years.years((-20));
        TestCase.assertEquals("P-20Y", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testSerialization() throws Exception {
        Years test = THREE;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Years result = ((Years) (ois.readObject()));
        ois.close();
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testPlus_int() {
        Years test2 = Years.years(2);
        Years result = test2.plus(3);
        TestCase.assertEquals(2, test2.getYears());
        TestCase.assertEquals(5, result.getYears());
        TestCase.assertEquals(1, ONE.plus(0).getYears());
        try {
            MAX_VALUE.plus(1);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testPlus_Years() {
        Years test2 = Years.years(2);
        Years test3 = Years.years(3);
        Years result = test2.plus(test3);
        TestCase.assertEquals(2, test2.getYears());
        TestCase.assertEquals(3, test3.getYears());
        TestCase.assertEquals(5, result.getYears());
        TestCase.assertEquals(1, ONE.plus(ZERO).getYears());
        TestCase.assertEquals(1, ONE.plus(((Years) (null))).getYears());
        try {
            MAX_VALUE.plus(ONE);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testMinus_int() {
        Years test2 = Years.years(2);
        Years result = test2.minus(3);
        TestCase.assertEquals(2, test2.getYears());
        TestCase.assertEquals((-1), result.getYears());
        TestCase.assertEquals(1, ONE.minus(0).getYears());
        try {
            MIN_VALUE.minus(1);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testMinus_Years() {
        Years test2 = Years.years(2);
        Years test3 = Years.years(3);
        Years result = test2.minus(test3);
        TestCase.assertEquals(2, test2.getYears());
        TestCase.assertEquals(3, test3.getYears());
        TestCase.assertEquals((-1), result.getYears());
        TestCase.assertEquals(1, ONE.minus(ZERO).getYears());
        TestCase.assertEquals(1, ONE.minus(((Years) (null))).getYears());
        try {
            MIN_VALUE.minus(ONE);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testMultipliedBy_int() {
        Years test = Years.years(2);
        TestCase.assertEquals(6, test.multipliedBy(3).getYears());
        TestCase.assertEquals(2, test.getYears());
        TestCase.assertEquals((-6), test.multipliedBy((-3)).getYears());
        TestCase.assertSame(test, test.multipliedBy(1));
        Years halfMax = Years.years((((Integer.MAX_VALUE) / 2) + 1));
        try {
            halfMax.multipliedBy(2);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testDividedBy_int() {
        Years test = Years.years(12);
        TestCase.assertEquals(6, test.dividedBy(2).getYears());
        TestCase.assertEquals(12, test.getYears());
        TestCase.assertEquals(4, test.dividedBy(3).getYears());
        TestCase.assertEquals(3, test.dividedBy(4).getYears());
        TestCase.assertEquals(2, test.dividedBy(5).getYears());
        TestCase.assertEquals(2, test.dividedBy(6).getYears());
        TestCase.assertSame(test, test.dividedBy(1));
        try {
            ONE.dividedBy(0);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testNegated() {
        Years test = Years.years(12);
        TestCase.assertEquals((-12), test.negated().getYears());
        TestCase.assertEquals(12, test.getYears());
        try {
            MIN_VALUE.negated();
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    public void testAddToLocalDate() {
        Years test = Years.years(3);
        LocalDate date = new LocalDate(2006, 6, 1);
        LocalDate expected = new LocalDate(2009, 6, 1);
        TestCase.assertEquals(expected, date.plus(test));
    }
}

