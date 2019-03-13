/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.commons.lang3.math;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Test cases for the {@link Fraction} class
 */
public class FractionTest {
    private static final int SKIP = 500;// 53


    // --------------------------------------------------------------------------
    @Test
    public void testConstants() {
        Assertions.assertEquals(0, Fraction.ZERO.getNumerator());
        Assertions.assertEquals(1, Fraction.ZERO.getDenominator());
        Assertions.assertEquals(1, Fraction.ONE.getNumerator());
        Assertions.assertEquals(1, Fraction.ONE.getDenominator());
        Assertions.assertEquals(1, Fraction.ONE_HALF.getNumerator());
        Assertions.assertEquals(2, Fraction.ONE_HALF.getDenominator());
        Assertions.assertEquals(1, Fraction.ONE_THIRD.getNumerator());
        Assertions.assertEquals(3, Fraction.ONE_THIRD.getDenominator());
        Assertions.assertEquals(2, Fraction.TWO_THIRDS.getNumerator());
        Assertions.assertEquals(3, Fraction.TWO_THIRDS.getDenominator());
        Assertions.assertEquals(1, Fraction.ONE_QUARTER.getNumerator());
        Assertions.assertEquals(4, Fraction.ONE_QUARTER.getDenominator());
        Assertions.assertEquals(2, Fraction.TWO_QUARTERS.getNumerator());
        Assertions.assertEquals(4, Fraction.TWO_QUARTERS.getDenominator());
        Assertions.assertEquals(3, Fraction.THREE_QUARTERS.getNumerator());
        Assertions.assertEquals(4, Fraction.THREE_QUARTERS.getDenominator());
        Assertions.assertEquals(1, Fraction.ONE_FIFTH.getNumerator());
        Assertions.assertEquals(5, Fraction.ONE_FIFTH.getDenominator());
        Assertions.assertEquals(2, Fraction.TWO_FIFTHS.getNumerator());
        Assertions.assertEquals(5, Fraction.TWO_FIFTHS.getDenominator());
        Assertions.assertEquals(3, Fraction.THREE_FIFTHS.getNumerator());
        Assertions.assertEquals(5, Fraction.THREE_FIFTHS.getDenominator());
        Assertions.assertEquals(4, Fraction.FOUR_FIFTHS.getNumerator());
        Assertions.assertEquals(5, Fraction.FOUR_FIFTHS.getDenominator());
    }

    @Test
    public void testFactory_int_int() {
        Fraction f = null;
        // zero
        f = Fraction.getFraction(0, 1);
        Assertions.assertEquals(0, f.getNumerator());
        Assertions.assertEquals(1, f.getDenominator());
        f = Fraction.getFraction(0, 2);
        Assertions.assertEquals(0, f.getNumerator());
        Assertions.assertEquals(2, f.getDenominator());
        // normal
        f = Fraction.getFraction(1, 1);
        Assertions.assertEquals(1, f.getNumerator());
        Assertions.assertEquals(1, f.getDenominator());
        f = Fraction.getFraction(2, 1);
        Assertions.assertEquals(2, f.getNumerator());
        Assertions.assertEquals(1, f.getDenominator());
        f = Fraction.getFraction(23, 345);
        Assertions.assertEquals(23, f.getNumerator());
        Assertions.assertEquals(345, f.getDenominator());
        // improper
        f = Fraction.getFraction(22, 7);
        Assertions.assertEquals(22, f.getNumerator());
        Assertions.assertEquals(7, f.getDenominator());
        // negatives
        f = Fraction.getFraction((-6), 10);
        Assertions.assertEquals((-6), f.getNumerator());
        Assertions.assertEquals(10, f.getDenominator());
        f = Fraction.getFraction(6, (-10));
        Assertions.assertEquals((-6), f.getNumerator());
        Assertions.assertEquals(10, f.getDenominator());
        f = Fraction.getFraction((-6), (-10));
        Assertions.assertEquals(6, f.getNumerator());
        Assertions.assertEquals(10, f.getDenominator());
        // zero denominator
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction(1, 0));
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction(2, 0));
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction((-3), 0));
        // very large: can't represent as unsimplified fraction, although
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction(4, Integer.MIN_VALUE));
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction(1, Integer.MIN_VALUE));
    }

    @Test
    public void testFactory_int_int_int() {
        Fraction f = null;
        // zero
        f = Fraction.getFraction(0, 0, 2);
        Assertions.assertEquals(0, f.getNumerator());
        Assertions.assertEquals(2, f.getDenominator());
        f = Fraction.getFraction(2, 0, 2);
        Assertions.assertEquals(4, f.getNumerator());
        Assertions.assertEquals(2, f.getDenominator());
        f = Fraction.getFraction(0, 1, 2);
        Assertions.assertEquals(1, f.getNumerator());
        Assertions.assertEquals(2, f.getDenominator());
        // normal
        f = Fraction.getFraction(1, 1, 2);
        Assertions.assertEquals(3, f.getNumerator());
        Assertions.assertEquals(2, f.getDenominator());
        // negatives
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction(1, (-6), (-10)));
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction(1, (-6), (-10)));
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction(1, (-6), (-10)));
        // negative whole
        f = Fraction.getFraction((-1), 6, 10);
        Assertions.assertEquals((-16), f.getNumerator());
        Assertions.assertEquals(10, f.getDenominator());
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction((-1), (-6), 10));
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction((-1), 6, (-10)));
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction((-1), (-6), (-10)));
        // zero denominator
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction(0, 1, 0));
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction(1, 2, 0));
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction((-1), (-3), 0));
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction(Integer.MAX_VALUE, 1, 2));
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction((-(Integer.MAX_VALUE)), 1, 2));
        // very large
        f = Fraction.getFraction((-1), 0, Integer.MAX_VALUE);
        Assertions.assertEquals((-(Integer.MAX_VALUE)), f.getNumerator());
        Assertions.assertEquals(Integer.MAX_VALUE, f.getDenominator());
        // negative denominators not allowed in this constructor.
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction(0, 4, Integer.MIN_VALUE));
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction(1, 1, Integer.MAX_VALUE));
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction((-1), 2, Integer.MAX_VALUE));
    }

    @Test
    public void testReducedFactory_int_int() {
        Fraction f = null;
        // zero
        f = Fraction.getReducedFraction(0, 1);
        Assertions.assertEquals(0, f.getNumerator());
        Assertions.assertEquals(1, f.getDenominator());
        // normal
        f = Fraction.getReducedFraction(1, 1);
        Assertions.assertEquals(1, f.getNumerator());
        Assertions.assertEquals(1, f.getDenominator());
        f = Fraction.getReducedFraction(2, 1);
        Assertions.assertEquals(2, f.getNumerator());
        Assertions.assertEquals(1, f.getDenominator());
        // improper
        f = Fraction.getReducedFraction(22, 7);
        Assertions.assertEquals(22, f.getNumerator());
        Assertions.assertEquals(7, f.getDenominator());
        // negatives
        f = Fraction.getReducedFraction((-6), 10);
        Assertions.assertEquals((-3), f.getNumerator());
        Assertions.assertEquals(5, f.getDenominator());
        f = Fraction.getReducedFraction(6, (-10));
        Assertions.assertEquals((-3), f.getNumerator());
        Assertions.assertEquals(5, f.getDenominator());
        f = Fraction.getReducedFraction((-6), (-10));
        Assertions.assertEquals(3, f.getNumerator());
        Assertions.assertEquals(5, f.getDenominator());
        // zero denominator
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getReducedFraction(1, 0));
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getReducedFraction(2, 0));
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getReducedFraction((-3), 0));
        // reduced
        f = Fraction.getReducedFraction(0, 2);
        Assertions.assertEquals(0, f.getNumerator());
        Assertions.assertEquals(1, f.getDenominator());
        f = Fraction.getReducedFraction(2, 2);
        Assertions.assertEquals(1, f.getNumerator());
        Assertions.assertEquals(1, f.getDenominator());
        f = Fraction.getReducedFraction(2, 4);
        Assertions.assertEquals(1, f.getNumerator());
        Assertions.assertEquals(2, f.getDenominator());
        f = Fraction.getReducedFraction(15, 10);
        Assertions.assertEquals(3, f.getNumerator());
        Assertions.assertEquals(2, f.getDenominator());
        f = Fraction.getReducedFraction(121, 22);
        Assertions.assertEquals(11, f.getNumerator());
        Assertions.assertEquals(2, f.getDenominator());
        // Extreme values
        // OK, can reduce before negating
        f = Fraction.getReducedFraction((-2), Integer.MIN_VALUE);
        Assertions.assertEquals(1, f.getNumerator());
        Assertions.assertEquals((-((Integer.MIN_VALUE) / 2)), f.getDenominator());
        // Can't reduce, negation will throw
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getReducedFraction((-7), Integer.MIN_VALUE));
        // LANG-662
        f = Fraction.getReducedFraction(Integer.MIN_VALUE, 2);
        Assertions.assertEquals(((Integer.MIN_VALUE) / 2), f.getNumerator());
        Assertions.assertEquals(1, f.getDenominator());
    }

    @Test
    public void testFactory_double() {
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction(Double.NaN));
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction(Double.POSITIVE_INFINITY));
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction(Double.NEGATIVE_INFINITY));
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction((((double) (Integer.MAX_VALUE)) + 1)));
        // zero
        Fraction f = Fraction.getFraction(0.0);
        Assertions.assertEquals(0, f.getNumerator());
        Assertions.assertEquals(1, f.getDenominator());
        // one
        f = Fraction.getFraction(1.0);
        Assertions.assertEquals(1, f.getNumerator());
        Assertions.assertEquals(1, f.getDenominator());
        // one half
        f = Fraction.getFraction(0.5);
        Assertions.assertEquals(1, f.getNumerator());
        Assertions.assertEquals(2, f.getDenominator());
        // negative
        f = Fraction.getFraction((-0.875));
        Assertions.assertEquals((-7), f.getNumerator());
        Assertions.assertEquals(8, f.getDenominator());
        // over 1
        f = Fraction.getFraction(1.25);
        Assertions.assertEquals(5, f.getNumerator());
        Assertions.assertEquals(4, f.getDenominator());
        // two thirds
        f = Fraction.getFraction(0.66666);
        Assertions.assertEquals(2, f.getNumerator());
        Assertions.assertEquals(3, f.getDenominator());
        // small
        f = Fraction.getFraction((1.0 / 10001.0));
        Assertions.assertEquals(0, f.getNumerator());
        Assertions.assertEquals(1, f.getDenominator());
        // normal
        Fraction f2 = null;
        for (int i = 1; i <= 100; i++) {
            // denominator
            for (int j = 1; j <= i; j++) {
                // numerator
                f = Fraction.getFraction((((double) (j)) / ((double) (i))));
                f2 = Fraction.getReducedFraction(j, i);
                Assertions.assertEquals(f2.getNumerator(), f.getNumerator());
                Assertions.assertEquals(f2.getDenominator(), f.getDenominator());
            }
        }
        // save time by skipping some tests!  (
        for (int i = 1001; i <= 10000; i += FractionTest.SKIP) {
            // denominator
            for (int j = 1; j <= i; j++) {
                // numerator
                f = Fraction.getFraction((((double) (j)) / ((double) (i))));
                f2 = Fraction.getReducedFraction(j, i);
                Assertions.assertEquals(f2.getNumerator(), f.getNumerator());
                Assertions.assertEquals(f2.getDenominator(), f.getDenominator());
            }
        }
    }

    @Test
    public void testFactory_String() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Fraction.getFraction(null));
    }

    @Test
    public void testFactory_String_double() {
        Fraction f = null;
        f = Fraction.getFraction("0.0");
        Assertions.assertEquals(0, f.getNumerator());
        Assertions.assertEquals(1, f.getDenominator());
        f = Fraction.getFraction("0.2");
        Assertions.assertEquals(1, f.getNumerator());
        Assertions.assertEquals(5, f.getDenominator());
        f = Fraction.getFraction("0.5");
        Assertions.assertEquals(1, f.getNumerator());
        Assertions.assertEquals(2, f.getDenominator());
        f = Fraction.getFraction("0.66666");
        Assertions.assertEquals(2, f.getNumerator());
        Assertions.assertEquals(3, f.getDenominator());
        Assertions.assertThrows(NumberFormatException.class, () -> Fraction.getFraction("2.3R"));
        Assertions.assertThrows(NumberFormatException.class, () -> Fraction.getFraction("2147483648"));// too big

        Assertions.assertThrows(NumberFormatException.class, () -> Fraction.getFraction("."));
    }

    @Test
    public void testFactory_String_proper() {
        Fraction f = null;
        f = Fraction.getFraction("0 0/1");
        Assertions.assertEquals(0, f.getNumerator());
        Assertions.assertEquals(1, f.getDenominator());
        f = Fraction.getFraction("1 1/5");
        Assertions.assertEquals(6, f.getNumerator());
        Assertions.assertEquals(5, f.getDenominator());
        f = Fraction.getFraction("7 1/2");
        Assertions.assertEquals(15, f.getNumerator());
        Assertions.assertEquals(2, f.getDenominator());
        f = Fraction.getFraction("1 2/4");
        Assertions.assertEquals(6, f.getNumerator());
        Assertions.assertEquals(4, f.getDenominator());
        f = Fraction.getFraction("-7 1/2");
        Assertions.assertEquals((-15), f.getNumerator());
        Assertions.assertEquals(2, f.getDenominator());
        f = Fraction.getFraction("-1 2/4");
        Assertions.assertEquals((-6), f.getNumerator());
        Assertions.assertEquals(4, f.getDenominator());
        Assertions.assertThrows(NumberFormatException.class, () -> Fraction.getFraction("2 3"));
        Assertions.assertThrows(NumberFormatException.class, () -> Fraction.getFraction("a 3"));
        Assertions.assertThrows(NumberFormatException.class, () -> Fraction.getFraction("2 b/4"));
        Assertions.assertThrows(NumberFormatException.class, () -> Fraction.getFraction("2 "));
        Assertions.assertThrows(NumberFormatException.class, () -> Fraction.getFraction(" 3"));
        Assertions.assertThrows(NumberFormatException.class, () -> Fraction.getFraction(" "));
    }

    @Test
    public void testFactory_String_improper() {
        Fraction f = null;
        f = Fraction.getFraction("0/1");
        Assertions.assertEquals(0, f.getNumerator());
        Assertions.assertEquals(1, f.getDenominator());
        f = Fraction.getFraction("1/5");
        Assertions.assertEquals(1, f.getNumerator());
        Assertions.assertEquals(5, f.getDenominator());
        f = Fraction.getFraction("1/2");
        Assertions.assertEquals(1, f.getNumerator());
        Assertions.assertEquals(2, f.getDenominator());
        f = Fraction.getFraction("2/3");
        Assertions.assertEquals(2, f.getNumerator());
        Assertions.assertEquals(3, f.getDenominator());
        f = Fraction.getFraction("7/3");
        Assertions.assertEquals(7, f.getNumerator());
        Assertions.assertEquals(3, f.getDenominator());
        f = Fraction.getFraction("2/4");
        Assertions.assertEquals(2, f.getNumerator());
        Assertions.assertEquals(4, f.getDenominator());
        Assertions.assertThrows(NumberFormatException.class, () -> Fraction.getFraction("2/d"));
        Assertions.assertThrows(NumberFormatException.class, () -> Fraction.getFraction("2e/3"));
        Assertions.assertThrows(NumberFormatException.class, () -> Fraction.getFraction("2/"));
        Assertions.assertThrows(NumberFormatException.class, () -> Fraction.getFraction("/"));
    }

    @Test
    public void testGets() {
        Fraction f = null;
        f = Fraction.getFraction(3, 5, 6);
        Assertions.assertEquals(23, f.getNumerator());
        Assertions.assertEquals(3, f.getProperWhole());
        Assertions.assertEquals(5, f.getProperNumerator());
        Assertions.assertEquals(6, f.getDenominator());
        f = Fraction.getFraction((-3), 5, 6);
        Assertions.assertEquals((-23), f.getNumerator());
        Assertions.assertEquals((-3), f.getProperWhole());
        Assertions.assertEquals(5, f.getProperNumerator());
        Assertions.assertEquals(6, f.getDenominator());
        f = Fraction.getFraction(Integer.MIN_VALUE, 0, 1);
        Assertions.assertEquals(Integer.MIN_VALUE, f.getNumerator());
        Assertions.assertEquals(Integer.MIN_VALUE, f.getProperWhole());
        Assertions.assertEquals(0, f.getProperNumerator());
        Assertions.assertEquals(1, f.getDenominator());
    }

    @Test
    public void testConversions() {
        Fraction f = null;
        f = Fraction.getFraction(3, 7, 8);
        Assertions.assertEquals(3, f.intValue());
        Assertions.assertEquals(3L, f.longValue());
        Assertions.assertEquals(3.875F, f.floatValue(), 1.0E-5F);
        Assertions.assertEquals(3.875, f.doubleValue(), 1.0E-5);
    }

    @Test
    public void testReduce() {
        Fraction f = null;
        f = Fraction.getFraction(50, 75);
        Fraction result = f.reduce();
        Assertions.assertEquals(2, result.getNumerator());
        Assertions.assertEquals(3, result.getDenominator());
        f = Fraction.getFraction((-2), (-3));
        result = f.reduce();
        Assertions.assertEquals(2, result.getNumerator());
        Assertions.assertEquals(3, result.getDenominator());
        f = Fraction.getFraction(2, (-3));
        result = f.reduce();
        Assertions.assertEquals((-2), result.getNumerator());
        Assertions.assertEquals(3, result.getDenominator());
        f = Fraction.getFraction((-2), 3);
        result = f.reduce();
        Assertions.assertEquals((-2), result.getNumerator());
        Assertions.assertEquals(3, result.getDenominator());
        Assertions.assertSame(f, result);
        f = Fraction.getFraction(2, 3);
        result = f.reduce();
        Assertions.assertEquals(2, result.getNumerator());
        Assertions.assertEquals(3, result.getDenominator());
        Assertions.assertSame(f, result);
        f = Fraction.getFraction(0, 1);
        result = f.reduce();
        Assertions.assertEquals(0, result.getNumerator());
        Assertions.assertEquals(1, result.getDenominator());
        Assertions.assertSame(f, result);
        f = Fraction.getFraction(0, 100);
        result = f.reduce();
        Assertions.assertEquals(0, result.getNumerator());
        Assertions.assertEquals(1, result.getDenominator());
        Assertions.assertSame(result, Fraction.ZERO);
        f = Fraction.getFraction(Integer.MIN_VALUE, 2);
        result = f.reduce();
        Assertions.assertEquals(((Integer.MIN_VALUE) / 2), result.getNumerator());
        Assertions.assertEquals(1, result.getDenominator());
    }

    @Test
    public void testInvert() {
        Fraction f = null;
        f = Fraction.getFraction(50, 75);
        f = f.invert();
        Assertions.assertEquals(75, f.getNumerator());
        Assertions.assertEquals(50, f.getDenominator());
        f = Fraction.getFraction(4, 3);
        f = f.invert();
        Assertions.assertEquals(3, f.getNumerator());
        Assertions.assertEquals(4, f.getDenominator());
        f = Fraction.getFraction((-15), 47);
        f = f.invert();
        Assertions.assertEquals((-47), f.getNumerator());
        Assertions.assertEquals(15, f.getDenominator());
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction(0, 3).invert());
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction(Integer.MIN_VALUE, 1).invert());
        f = Fraction.getFraction(Integer.MAX_VALUE, 1);
        f = f.invert();
        Assertions.assertEquals(1, f.getNumerator());
        Assertions.assertEquals(Integer.MAX_VALUE, f.getDenominator());
    }

    @Test
    public void testNegate() {
        Fraction f = null;
        f = Fraction.getFraction(50, 75);
        f = f.negate();
        Assertions.assertEquals((-50), f.getNumerator());
        Assertions.assertEquals(75, f.getDenominator());
        f = Fraction.getFraction((-50), 75);
        f = f.negate();
        Assertions.assertEquals(50, f.getNumerator());
        Assertions.assertEquals(75, f.getDenominator());
        // large values
        f = Fraction.getFraction(((Integer.MAX_VALUE) - 1), Integer.MAX_VALUE);
        f = f.negate();
        Assertions.assertEquals(((Integer.MIN_VALUE) + 2), f.getNumerator());
        Assertions.assertEquals(Integer.MAX_VALUE, f.getDenominator());
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction(Integer.MIN_VALUE, 1).negate());
    }

    @Test
    public void testAbs() {
        Fraction f = null;
        f = Fraction.getFraction(50, 75);
        f = f.abs();
        Assertions.assertEquals(50, f.getNumerator());
        Assertions.assertEquals(75, f.getDenominator());
        f = Fraction.getFraction((-50), 75);
        f = f.abs();
        Assertions.assertEquals(50, f.getNumerator());
        Assertions.assertEquals(75, f.getDenominator());
        f = Fraction.getFraction(Integer.MAX_VALUE, 1);
        f = f.abs();
        Assertions.assertEquals(Integer.MAX_VALUE, f.getNumerator());
        Assertions.assertEquals(1, f.getDenominator());
        f = Fraction.getFraction(Integer.MAX_VALUE, (-1));
        f = f.abs();
        Assertions.assertEquals(Integer.MAX_VALUE, f.getNumerator());
        Assertions.assertEquals(1, f.getDenominator());
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction(Integer.MIN_VALUE, 1).abs());
    }

    @Test
    public void testPow() {
        Fraction f = null;
        f = Fraction.getFraction(3, 5);
        Assertions.assertEquals(Fraction.ONE, f.pow(0));
        f = Fraction.getFraction(3, 5);
        Assertions.assertSame(f, f.pow(1));
        Assertions.assertEquals(f, f.pow(1));
        f = Fraction.getFraction(3, 5);
        f = f.pow(2);
        Assertions.assertEquals(9, f.getNumerator());
        Assertions.assertEquals(25, f.getDenominator());
        f = Fraction.getFraction(3, 5);
        f = f.pow(3);
        Assertions.assertEquals(27, f.getNumerator());
        Assertions.assertEquals(125, f.getDenominator());
        f = Fraction.getFraction(3, 5);
        f = f.pow((-1));
        Assertions.assertEquals(5, f.getNumerator());
        Assertions.assertEquals(3, f.getDenominator());
        f = Fraction.getFraction(3, 5);
        f = f.pow((-2));
        Assertions.assertEquals(25, f.getNumerator());
        Assertions.assertEquals(9, f.getDenominator());
        // check unreduced fractions stay that way.
        f = Fraction.getFraction(6, 10);
        Assertions.assertEquals(Fraction.ONE, f.pow(0));
        f = Fraction.getFraction(6, 10);
        Assertions.assertEquals(f, f.pow(1));
        Assertions.assertNotEquals(f.pow(1), Fraction.getFraction(3, 5));
        f = Fraction.getFraction(6, 10);
        f = f.pow(2);
        Assertions.assertEquals(9, f.getNumerator());
        Assertions.assertEquals(25, f.getDenominator());
        f = Fraction.getFraction(6, 10);
        f = f.pow(3);
        Assertions.assertEquals(27, f.getNumerator());
        Assertions.assertEquals(125, f.getDenominator());
        f = Fraction.getFraction(6, 10);
        f = f.pow((-1));
        Assertions.assertEquals(10, f.getNumerator());
        Assertions.assertEquals(6, f.getDenominator());
        f = Fraction.getFraction(6, 10);
        f = f.pow((-2));
        Assertions.assertEquals(25, f.getNumerator());
        Assertions.assertEquals(9, f.getDenominator());
        // zero to any positive power is still zero.
        f = Fraction.getFraction(0, 1231);
        f = f.pow(1);
        Assertions.assertEquals(0, f.compareTo(Fraction.ZERO));
        Assertions.assertEquals(0, f.getNumerator());
        Assertions.assertEquals(1231, f.getDenominator());
        f = f.pow(2);
        Assertions.assertEquals(0, f.compareTo(Fraction.ZERO));
        Assertions.assertEquals(0, f.getNumerator());
        Assertions.assertEquals(1, f.getDenominator());
        // zero to negative powers should throw an exception
        final Fraction fr = f;
        Assertions.assertThrows(ArithmeticException.class, () -> fr.pow((-1)));
        Assertions.assertThrows(ArithmeticException.class, () -> fr.pow(Integer.MIN_VALUE));
        // one to any power is still one.
        f = Fraction.getFraction(1, 1);
        f = f.pow(0);
        Assertions.assertEquals(f, Fraction.ONE);
        f = f.pow(1);
        Assertions.assertEquals(f, Fraction.ONE);
        f = f.pow((-1));
        Assertions.assertEquals(f, Fraction.ONE);
        f = f.pow(Integer.MAX_VALUE);
        Assertions.assertEquals(f, Fraction.ONE);
        f = f.pow(Integer.MIN_VALUE);
        Assertions.assertEquals(f, Fraction.ONE);
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction(Integer.MAX_VALUE, 1).pow(2));
        // Numerator growing too negative during the pow operation.
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction(Integer.MIN_VALUE, 1).pow(3));
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction(65536, 1).pow(2));
    }

    @Test
    public void testAdd() {
        Fraction f = null;
        Fraction f1 = null;
        Fraction f2 = null;
        f1 = Fraction.getFraction(3, 5);
        f2 = Fraction.getFraction(1, 5);
        f = f1.add(f2);
        Assertions.assertEquals(4, f.getNumerator());
        Assertions.assertEquals(5, f.getDenominator());
        f1 = Fraction.getFraction(3, 5);
        f2 = Fraction.getFraction(2, 5);
        f = f1.add(f2);
        Assertions.assertEquals(1, f.getNumerator());
        Assertions.assertEquals(1, f.getDenominator());
        f1 = Fraction.getFraction(3, 5);
        f2 = Fraction.getFraction(3, 5);
        f = f1.add(f2);
        Assertions.assertEquals(6, f.getNumerator());
        Assertions.assertEquals(5, f.getDenominator());
        f1 = Fraction.getFraction(3, 5);
        f2 = Fraction.getFraction((-4), 5);
        f = f1.add(f2);
        Assertions.assertEquals((-1), f.getNumerator());
        Assertions.assertEquals(5, f.getDenominator());
        f1 = Fraction.getFraction(((Integer.MAX_VALUE) - 1), 1);
        f2 = Fraction.ONE;
        f = f1.add(f2);
        Assertions.assertEquals(Integer.MAX_VALUE, f.getNumerator());
        Assertions.assertEquals(1, f.getDenominator());
        f1 = Fraction.getFraction(3, 5);
        f2 = Fraction.getFraction(1, 2);
        f = f1.add(f2);
        Assertions.assertEquals(11, f.getNumerator());
        Assertions.assertEquals(10, f.getDenominator());
        f1 = Fraction.getFraction(3, 8);
        f2 = Fraction.getFraction(1, 6);
        f = f1.add(f2);
        Assertions.assertEquals(13, f.getNumerator());
        Assertions.assertEquals(24, f.getDenominator());
        f1 = Fraction.getFraction(0, 5);
        f2 = Fraction.getFraction(1, 5);
        f = f1.add(f2);
        Assertions.assertSame(f2, f);
        f = f2.add(f1);
        Assertions.assertSame(f2, f);
        f1 = Fraction.getFraction((-1), (((13 * 13) * 2) * 2));
        f2 = Fraction.getFraction((-2), ((13 * 17) * 2));
        final Fraction fr = f1.add(f2);
        Assertions.assertEquals(((((13 * 13) * 17) * 2) * 2), fr.getDenominator());
        Assertions.assertEquals(((-17) - ((2 * 13) * 2)), fr.getNumerator());
        Assertions.assertThrows(IllegalArgumentException.class, () -> fr.add(null));
        // if this fraction is added naively, it will overflow.
        // check that it doesn't.
        f1 = Fraction.getFraction(1, (32768 * 3));
        f2 = Fraction.getFraction(1, 59049);
        f = f1.add(f2);
        Assertions.assertEquals(52451, f.getNumerator());
        Assertions.assertEquals(1934917632, f.getDenominator());
        f1 = Fraction.getFraction(Integer.MIN_VALUE, 3);
        f2 = Fraction.ONE_THIRD;
        f = f1.add(f2);
        Assertions.assertEquals(((Integer.MIN_VALUE) + 1), f.getNumerator());
        Assertions.assertEquals(3, f.getDenominator());
        f1 = Fraction.getFraction(((Integer.MAX_VALUE) - 1), 1);
        f2 = Fraction.ONE;
        f = f1.add(f2);
        Assertions.assertEquals(Integer.MAX_VALUE, f.getNumerator());
        Assertions.assertEquals(1, f.getDenominator());
        final Fraction overflower = f;
        Assertions.assertThrows(ArithmeticException.class, () -> overflower.add(Fraction.ONE));// should overflow

        // denominator should not be a multiple of 2 or 3 to trigger overflow
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction(Integer.MIN_VALUE, 5).add(Fraction.getFraction((-1), 5)));
        final Fraction maxValue = Fraction.getFraction((-(Integer.MAX_VALUE)), 1);
        Assertions.assertThrows(ArithmeticException.class, () -> maxValue.add(maxValue));
        final Fraction negativeMaxValue = Fraction.getFraction((-(Integer.MAX_VALUE)), 1);
        Assertions.assertThrows(ArithmeticException.class, () -> negativeMaxValue.add(negativeMaxValue));
        final Fraction f3 = Fraction.getFraction(3, 327680);
        final Fraction f4 = Fraction.getFraction(2, 59049);
        Assertions.assertThrows(ArithmeticException.class, () -> f3.add(f4));// should overflow

    }

    @Test
    public void testSubtract() {
        Fraction f = null;
        Fraction f1 = null;
        Fraction f2 = null;
        f1 = Fraction.getFraction(3, 5);
        f2 = Fraction.getFraction(1, 5);
        f = f1.subtract(f2);
        Assertions.assertEquals(2, f.getNumerator());
        Assertions.assertEquals(5, f.getDenominator());
        f1 = Fraction.getFraction(7, 5);
        f2 = Fraction.getFraction(2, 5);
        f = f1.subtract(f2);
        Assertions.assertEquals(1, f.getNumerator());
        Assertions.assertEquals(1, f.getDenominator());
        f1 = Fraction.getFraction(3, 5);
        f2 = Fraction.getFraction(3, 5);
        f = f1.subtract(f2);
        Assertions.assertEquals(0, f.getNumerator());
        Assertions.assertEquals(1, f.getDenominator());
        f1 = Fraction.getFraction(3, 5);
        f2 = Fraction.getFraction((-4), 5);
        f = f1.subtract(f2);
        Assertions.assertEquals(7, f.getNumerator());
        Assertions.assertEquals(5, f.getDenominator());
        f1 = Fraction.getFraction(0, 5);
        f2 = Fraction.getFraction(4, 5);
        f = f1.subtract(f2);
        Assertions.assertEquals((-4), f.getNumerator());
        Assertions.assertEquals(5, f.getDenominator());
        f1 = Fraction.getFraction(0, 5);
        f2 = Fraction.getFraction((-4), 5);
        f = f1.subtract(f2);
        Assertions.assertEquals(4, f.getNumerator());
        Assertions.assertEquals(5, f.getDenominator());
        f1 = Fraction.getFraction(3, 5);
        f2 = Fraction.getFraction(1, 2);
        f = f1.subtract(f2);
        Assertions.assertEquals(1, f.getNumerator());
        Assertions.assertEquals(10, f.getDenominator());
        f1 = Fraction.getFraction(0, 5);
        f2 = Fraction.getFraction(1, 5);
        f = f2.subtract(f1);
        Assertions.assertSame(f2, f);
        final Fraction fr = f;
        Assertions.assertThrows(IllegalArgumentException.class, () -> fr.subtract(null));
        // if this fraction is subtracted naively, it will overflow.
        // check that it doesn't.
        f1 = Fraction.getFraction(1, (32768 * 3));
        f2 = Fraction.getFraction(1, 59049);
        f = f1.subtract(f2);
        Assertions.assertEquals((-13085), f.getNumerator());
        Assertions.assertEquals(1934917632, f.getDenominator());
        f1 = Fraction.getFraction(Integer.MIN_VALUE, 3);
        f2 = Fraction.ONE_THIRD.negate();
        f = f1.subtract(f2);
        Assertions.assertEquals(((Integer.MIN_VALUE) + 1), f.getNumerator());
        Assertions.assertEquals(3, f.getDenominator());
        f1 = Fraction.getFraction(Integer.MAX_VALUE, 1);
        f2 = Fraction.ONE;
        f = f1.subtract(f2);
        Assertions.assertEquals(((Integer.MAX_VALUE) - 1), f.getNumerator());
        Assertions.assertEquals(1, f.getDenominator());
        // Should overflow
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction(1, Integer.MAX_VALUE).subtract(Fraction.getFraction(1, ((Integer.MAX_VALUE) - 1))));
        f = f1.subtract(f2);
        // denominator should not be a multiple of 2 or 3 to trigger overflow
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction(Integer.MIN_VALUE, 5).subtract(Fraction.getFraction(1, 5)));
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction(Integer.MIN_VALUE, 1).subtract(Fraction.ONE));
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction(Integer.MAX_VALUE, 1).subtract(Fraction.ONE.negate()));
        // Should overflow
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction(3, 327680).subtract(Fraction.getFraction(2, 59049)));
    }

    @Test
    public void testMultiply() {
        Fraction f = null;
        Fraction f1 = null;
        Fraction f2 = null;
        f1 = Fraction.getFraction(3, 5);
        f2 = Fraction.getFraction(2, 5);
        f = f1.multiplyBy(f2);
        Assertions.assertEquals(6, f.getNumerator());
        Assertions.assertEquals(25, f.getDenominator());
        f1 = Fraction.getFraction(6, 10);
        f2 = Fraction.getFraction(6, 10);
        f = f1.multiplyBy(f2);
        Assertions.assertEquals(9, f.getNumerator());
        Assertions.assertEquals(25, f.getDenominator());
        f = f.multiplyBy(f2);
        Assertions.assertEquals(27, f.getNumerator());
        Assertions.assertEquals(125, f.getDenominator());
        f1 = Fraction.getFraction(3, 5);
        f2 = Fraction.getFraction((-2), 5);
        f = f1.multiplyBy(f2);
        Assertions.assertEquals((-6), f.getNumerator());
        Assertions.assertEquals(25, f.getDenominator());
        f1 = Fraction.getFraction((-3), 5);
        f2 = Fraction.getFraction((-2), 5);
        f = f1.multiplyBy(f2);
        Assertions.assertEquals(6, f.getNumerator());
        Assertions.assertEquals(25, f.getDenominator());
        f1 = Fraction.getFraction(0, 5);
        f2 = Fraction.getFraction(2, 7);
        f = f1.multiplyBy(f2);
        Assertions.assertSame(Fraction.ZERO, f);
        f1 = Fraction.getFraction(2, 7);
        f2 = Fraction.ONE;
        f = f1.multiplyBy(f2);
        Assertions.assertEquals(2, f.getNumerator());
        Assertions.assertEquals(7, f.getDenominator());
        f1 = Fraction.getFraction(Integer.MAX_VALUE, 1);
        f2 = Fraction.getFraction(Integer.MIN_VALUE, Integer.MAX_VALUE);
        f = f1.multiplyBy(f2);
        Assertions.assertEquals(Integer.MIN_VALUE, f.getNumerator());
        Assertions.assertEquals(1, f.getDenominator());
        final Fraction fr = f;
        Assertions.assertThrows(IllegalArgumentException.class, () -> fr.multiplyBy(null));
        final Fraction fr1 = Fraction.getFraction(1, Integer.MAX_VALUE);
        Assertions.assertThrows(ArithmeticException.class, () -> fr1.multiplyBy(fr1));
        final Fraction fr2 = Fraction.getFraction(1, (-(Integer.MAX_VALUE)));
        Assertions.assertThrows(ArithmeticException.class, () -> fr2.multiplyBy(fr2));
    }

    @Test
    public void testDivide() {
        Fraction f = null;
        Fraction f1 = null;
        Fraction f2 = null;
        f1 = Fraction.getFraction(3, 5);
        f2 = Fraction.getFraction(2, 5);
        f = f1.divideBy(f2);
        Assertions.assertEquals(3, f.getNumerator());
        Assertions.assertEquals(2, f.getDenominator());
        Assertions.assertThrows(ArithmeticException.class, () -> Fraction.getFraction(3, 5).divideBy(Fraction.ZERO));
        f1 = Fraction.getFraction(0, 5);
        f2 = Fraction.getFraction(2, 7);
        f = f1.divideBy(f2);
        Assertions.assertSame(Fraction.ZERO, f);
        f1 = Fraction.getFraction(2, 7);
        f2 = Fraction.ONE;
        f = f1.divideBy(f2);
        Assertions.assertEquals(2, f.getNumerator());
        Assertions.assertEquals(7, f.getDenominator());
        f1 = Fraction.getFraction(1, Integer.MAX_VALUE);
        f = f1.divideBy(f1);
        Assertions.assertEquals(1, f.getNumerator());
        Assertions.assertEquals(1, f.getDenominator());
        f1 = Fraction.getFraction(Integer.MIN_VALUE, Integer.MAX_VALUE);
        f2 = Fraction.getFraction(1, Integer.MAX_VALUE);
        final Fraction fr = f1.divideBy(f2);
        Assertions.assertEquals(Integer.MIN_VALUE, fr.getNumerator());
        Assertions.assertEquals(1, fr.getDenominator());
        Assertions.assertThrows(IllegalArgumentException.class, () -> fr.divideBy(null));
        final Fraction smallest = Fraction.getFraction(1, Integer.MAX_VALUE);
        Assertions.assertThrows(ArithmeticException.class, () -> smallest.divideBy(smallest.invert()));// Should overflow

        final Fraction negative = Fraction.getFraction(1, (-(Integer.MAX_VALUE)));
        Assertions.assertThrows(ArithmeticException.class, () -> negative.divideBy(negative.invert()));// Should overflow

    }

    @Test
    public void testEquals() {
        Fraction f1 = null;
        Fraction f2 = null;
        f1 = Fraction.getFraction(3, 5);
        Assertions.assertNotEquals(null, f1);
        Assertions.assertNotEquals(f1, new Object());
        Assertions.assertNotEquals(f1, Integer.valueOf(6));
        f1 = Fraction.getFraction(3, 5);
        f2 = Fraction.getFraction(2, 5);
        Assertions.assertNotEquals(f1, f2);
        Assertions.assertEquals(f1, f1);
        Assertions.assertEquals(f2, f2);
        f2 = Fraction.getFraction(3, 5);
        Assertions.assertEquals(f1, f2);
        f2 = Fraction.getFraction(6, 10);
        Assertions.assertNotEquals(f1, f2);
    }

    @Test
    public void testHashCode() {
        final Fraction f1 = Fraction.getFraction(3, 5);
        Fraction f2 = Fraction.getFraction(3, 5);
        Assertions.assertEquals(f1.hashCode(), f2.hashCode());
        f2 = Fraction.getFraction(2, 5);
        Assertions.assertTrue(((f1.hashCode()) != (f2.hashCode())));
        f2 = Fraction.getFraction(6, 10);
        Assertions.assertTrue(((f1.hashCode()) != (f2.hashCode())));
    }

    @Test
    public void testCompareTo() {
        Fraction f1 = null;
        Fraction f2 = null;
        f1 = Fraction.getFraction(3, 5);
        Assertions.assertEquals(0, f1.compareTo(f1));
        final Fraction fr = f1;
        Assertions.assertThrows(NullPointerException.class, () -> fr.compareTo(null));
        f2 = Fraction.getFraction(2, 5);
        Assertions.assertTrue(((f1.compareTo(f2)) > 0));
        Assertions.assertEquals(0, f2.compareTo(f2));
        f2 = Fraction.getFraction(4, 5);
        Assertions.assertTrue(((f1.compareTo(f2)) < 0));
        Assertions.assertEquals(0, f2.compareTo(f2));
        f2 = Fraction.getFraction(3, 5);
        Assertions.assertEquals(0, f1.compareTo(f2));
        Assertions.assertEquals(0, f2.compareTo(f2));
        f2 = Fraction.getFraction(6, 10);
        Assertions.assertEquals(0, f1.compareTo(f2));
        Assertions.assertEquals(0, f2.compareTo(f2));
        f2 = Fraction.getFraction((-1), 1, Integer.MAX_VALUE);
        Assertions.assertTrue(((f1.compareTo(f2)) > 0));
        Assertions.assertEquals(0, f2.compareTo(f2));
    }

    @Test
    public void testToString() {
        Fraction f = null;
        f = Fraction.getFraction(3, 5);
        final String str = f.toString();
        Assertions.assertEquals("3/5", str);
        Assertions.assertSame(str, f.toString());
        f = Fraction.getFraction(7, 5);
        Assertions.assertEquals("7/5", f.toString());
        f = Fraction.getFraction(4, 2);
        Assertions.assertEquals("4/2", f.toString());
        f = Fraction.getFraction(0, 2);
        Assertions.assertEquals("0/2", f.toString());
        f = Fraction.getFraction(2, 2);
        Assertions.assertEquals("2/2", f.toString());
        f = Fraction.getFraction(Integer.MIN_VALUE, 0, 1);
        Assertions.assertEquals("-2147483648/1", f.toString());
        f = Fraction.getFraction((-1), 1, Integer.MAX_VALUE);
        Assertions.assertEquals("-2147483648/2147483647", f.toString());
    }

    @Test
    public void testToProperString() {
        Fraction f = null;
        f = Fraction.getFraction(3, 5);
        final String str = f.toProperString();
        Assertions.assertEquals("3/5", str);
        Assertions.assertSame(str, f.toProperString());
        f = Fraction.getFraction(7, 5);
        Assertions.assertEquals("1 2/5", f.toProperString());
        f = Fraction.getFraction(14, 10);
        Assertions.assertEquals("1 4/10", f.toProperString());
        f = Fraction.getFraction(4, 2);
        Assertions.assertEquals("2", f.toProperString());
        f = Fraction.getFraction(0, 2);
        Assertions.assertEquals("0", f.toProperString());
        f = Fraction.getFraction(2, 2);
        Assertions.assertEquals("1", f.toProperString());
        f = Fraction.getFraction((-7), 5);
        Assertions.assertEquals("-1 2/5", f.toProperString());
        f = Fraction.getFraction(Integer.MIN_VALUE, 0, 1);
        Assertions.assertEquals("-2147483648", f.toProperString());
        f = Fraction.getFraction((-1), 1, Integer.MAX_VALUE);
        Assertions.assertEquals("-1 1/2147483647", f.toProperString());
        Assertions.assertEquals("-1", Fraction.getFraction((-1)).toProperString());
    }
}

