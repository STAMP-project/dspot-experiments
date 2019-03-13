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
package org.joda.time.field;


import java.math.RoundingMode;
import junit.framework.TestCase;


/**
 *
 *
 * @author Brian S O'Neill
 */
public class TestFieldUtils extends TestCase {
    public TestFieldUtils(String name) {
        super(name);
    }

    public void testSafeAddInt() {
        TestCase.assertEquals(0, FieldUtils.safeAdd(0, 0));
        TestCase.assertEquals(5, FieldUtils.safeAdd(2, 3));
        TestCase.assertEquals((-1), FieldUtils.safeAdd(2, (-3)));
        TestCase.assertEquals(1, FieldUtils.safeAdd((-2), 3));
        TestCase.assertEquals((-5), FieldUtils.safeAdd((-2), (-3)));
        TestCase.assertEquals(((Integer.MAX_VALUE) - 1), FieldUtils.safeAdd(Integer.MAX_VALUE, (-1)));
        TestCase.assertEquals(((Integer.MIN_VALUE) + 1), FieldUtils.safeAdd(Integer.MIN_VALUE, 1));
        TestCase.assertEquals((-1), FieldUtils.safeAdd(Integer.MIN_VALUE, Integer.MAX_VALUE));
        TestCase.assertEquals((-1), FieldUtils.safeAdd(Integer.MAX_VALUE, Integer.MIN_VALUE));
        try {
            FieldUtils.safeAdd(Integer.MAX_VALUE, 1);
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
        try {
            FieldUtils.safeAdd(Integer.MAX_VALUE, 100);
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
        try {
            FieldUtils.safeAdd(Integer.MAX_VALUE, Integer.MAX_VALUE);
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
        try {
            FieldUtils.safeAdd(Integer.MIN_VALUE, (-1));
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
        try {
            FieldUtils.safeAdd(Integer.MIN_VALUE, (-100));
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
        try {
            FieldUtils.safeAdd(Integer.MIN_VALUE, Integer.MIN_VALUE);
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
    }

    public void testSafeAddLong() {
        TestCase.assertEquals(0L, FieldUtils.safeAdd(0L, 0L));
        TestCase.assertEquals(5L, FieldUtils.safeAdd(2L, 3L));
        TestCase.assertEquals((-1L), FieldUtils.safeAdd(2L, (-3L)));
        TestCase.assertEquals(1L, FieldUtils.safeAdd((-2L), 3L));
        TestCase.assertEquals((-5L), FieldUtils.safeAdd((-2L), (-3L)));
        TestCase.assertEquals(((Long.MAX_VALUE) - 1), FieldUtils.safeAdd(Long.MAX_VALUE, (-1L)));
        TestCase.assertEquals(((Long.MIN_VALUE) + 1), FieldUtils.safeAdd(Long.MIN_VALUE, 1L));
        TestCase.assertEquals((-1), FieldUtils.safeAdd(Long.MIN_VALUE, Long.MAX_VALUE));
        TestCase.assertEquals((-1), FieldUtils.safeAdd(Long.MAX_VALUE, Long.MIN_VALUE));
        try {
            FieldUtils.safeAdd(Long.MAX_VALUE, 1L);
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
        try {
            FieldUtils.safeAdd(Long.MAX_VALUE, 100L);
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
        try {
            FieldUtils.safeAdd(Long.MAX_VALUE, Long.MAX_VALUE);
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
        try {
            FieldUtils.safeAdd(Long.MIN_VALUE, (-1L));
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
        try {
            FieldUtils.safeAdd(Long.MIN_VALUE, (-100L));
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
        try {
            FieldUtils.safeAdd(Long.MIN_VALUE, Long.MIN_VALUE);
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
    }

    public void testSafeSubtractLong() {
        TestCase.assertEquals(0L, FieldUtils.safeSubtract(0L, 0L));
        TestCase.assertEquals((-1L), FieldUtils.safeSubtract(2L, 3L));
        TestCase.assertEquals(5L, FieldUtils.safeSubtract(2L, (-3L)));
        TestCase.assertEquals((-5L), FieldUtils.safeSubtract((-2L), 3L));
        TestCase.assertEquals(1L, FieldUtils.safeSubtract((-2L), (-3L)));
        TestCase.assertEquals(((Long.MAX_VALUE) - 1), FieldUtils.safeSubtract(Long.MAX_VALUE, 1L));
        TestCase.assertEquals(((Long.MIN_VALUE) + 1), FieldUtils.safeSubtract(Long.MIN_VALUE, (-1L)));
        TestCase.assertEquals(0, FieldUtils.safeSubtract(Long.MIN_VALUE, Long.MIN_VALUE));
        TestCase.assertEquals(0, FieldUtils.safeSubtract(Long.MAX_VALUE, Long.MAX_VALUE));
        try {
            FieldUtils.safeSubtract(Long.MIN_VALUE, 1L);
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
        try {
            FieldUtils.safeSubtract(Long.MIN_VALUE, 100L);
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
        try {
            FieldUtils.safeSubtract(Long.MIN_VALUE, Long.MAX_VALUE);
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
        try {
            FieldUtils.safeSubtract(Long.MAX_VALUE, (-1L));
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
        try {
            FieldUtils.safeSubtract(Long.MAX_VALUE, (-100L));
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
        try {
            FieldUtils.safeSubtract(Long.MAX_VALUE, Long.MIN_VALUE);
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
    }

    // -----------------------------------------------------------------------
    public void testSafeMultiplyLongLong() {
        TestCase.assertEquals(0L, FieldUtils.safeMultiply(0L, 0L));
        TestCase.assertEquals(1L, FieldUtils.safeMultiply(1L, 1L));
        TestCase.assertEquals(3L, FieldUtils.safeMultiply(1L, 3L));
        TestCase.assertEquals(3L, FieldUtils.safeMultiply(3L, 1L));
        TestCase.assertEquals(6L, FieldUtils.safeMultiply(2L, 3L));
        TestCase.assertEquals((-6L), FieldUtils.safeMultiply(2L, (-3L)));
        TestCase.assertEquals((-6L), FieldUtils.safeMultiply((-2L), 3L));
        TestCase.assertEquals(6L, FieldUtils.safeMultiply((-2L), (-3L)));
        TestCase.assertEquals(Long.MAX_VALUE, FieldUtils.safeMultiply(Long.MAX_VALUE, 1L));
        TestCase.assertEquals(Long.MIN_VALUE, FieldUtils.safeMultiply(Long.MIN_VALUE, 1L));
        TestCase.assertEquals((-(Long.MAX_VALUE)), FieldUtils.safeMultiply(Long.MAX_VALUE, (-1L)));
        try {
            FieldUtils.safeMultiply(Long.MIN_VALUE, (-1L));
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
        try {
            FieldUtils.safeMultiply((-1L), Long.MIN_VALUE);
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
        try {
            FieldUtils.safeMultiply(Long.MIN_VALUE, 100L);
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
        try {
            FieldUtils.safeMultiply(Long.MIN_VALUE, Long.MAX_VALUE);
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
        try {
            FieldUtils.safeMultiply(Long.MAX_VALUE, Long.MIN_VALUE);
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
    }

    // -----------------------------------------------------------------------
    public void testSafeMultiplyLongInt() {
        TestCase.assertEquals(0L, FieldUtils.safeMultiply(0L, 0));
        TestCase.assertEquals(1L, FieldUtils.safeMultiply(1L, 1));
        TestCase.assertEquals(3L, FieldUtils.safeMultiply(1L, 3));
        TestCase.assertEquals(3L, FieldUtils.safeMultiply(3L, 1));
        TestCase.assertEquals(6L, FieldUtils.safeMultiply(2L, 3));
        TestCase.assertEquals((-6L), FieldUtils.safeMultiply(2L, (-3)));
        TestCase.assertEquals((-6L), FieldUtils.safeMultiply((-2L), 3));
        TestCase.assertEquals(6L, FieldUtils.safeMultiply((-2L), (-3)));
        TestCase.assertEquals(((-1L) * (Integer.MIN_VALUE)), FieldUtils.safeMultiply((-1L), Integer.MIN_VALUE));
        TestCase.assertEquals(Long.MAX_VALUE, FieldUtils.safeMultiply(Long.MAX_VALUE, 1));
        TestCase.assertEquals(Long.MIN_VALUE, FieldUtils.safeMultiply(Long.MIN_VALUE, 1));
        TestCase.assertEquals((-(Long.MAX_VALUE)), FieldUtils.safeMultiply(Long.MAX_VALUE, (-1)));
        try {
            FieldUtils.safeMultiply(Long.MIN_VALUE, (-1));
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
        try {
            FieldUtils.safeMultiply(Long.MIN_VALUE, 100);
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
        try {
            FieldUtils.safeMultiply(Long.MIN_VALUE, Integer.MAX_VALUE);
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
        try {
            FieldUtils.safeMultiply(Long.MAX_VALUE, Integer.MIN_VALUE);
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
    }

    // -----------------------------------------------------------------------
    public void testSafeDivideLongLong() {
        TestCase.assertEquals(1L, FieldUtils.safeDivide(1L, 1L));
        TestCase.assertEquals(1L, FieldUtils.safeDivide(3L, 3L));
        TestCase.assertEquals(0L, FieldUtils.safeDivide(1L, 3L));
        TestCase.assertEquals(3L, FieldUtils.safeDivide(3L, 1L));
        TestCase.assertEquals(1L, FieldUtils.safeDivide(5L, 3L));
        TestCase.assertEquals((-1L), FieldUtils.safeDivide(5L, (-3L)));
        TestCase.assertEquals((-1L), FieldUtils.safeDivide((-5L), 3L));
        TestCase.assertEquals(1L, FieldUtils.safeDivide((-5L), (-3L)));
        TestCase.assertEquals(2L, FieldUtils.safeDivide(6L, 3L));
        TestCase.assertEquals((-2L), FieldUtils.safeDivide(6L, (-3L)));
        TestCase.assertEquals((-2L), FieldUtils.safeDivide((-6L), 3L));
        TestCase.assertEquals(2L, FieldUtils.safeDivide((-6L), (-3L)));
        TestCase.assertEquals(2L, FieldUtils.safeDivide(7L, 3L));
        TestCase.assertEquals((-2L), FieldUtils.safeDivide(7L, (-3L)));
        TestCase.assertEquals((-2L), FieldUtils.safeDivide((-7L), 3L));
        TestCase.assertEquals(2L, FieldUtils.safeDivide((-7L), (-3L)));
        TestCase.assertEquals(Long.MAX_VALUE, FieldUtils.safeDivide(Long.MAX_VALUE, 1L));
        TestCase.assertEquals(Long.MIN_VALUE, FieldUtils.safeDivide(Long.MIN_VALUE, 1L));
        TestCase.assertEquals((-(Long.MAX_VALUE)), FieldUtils.safeDivide(Long.MAX_VALUE, (-1L)));
        try {
            FieldUtils.safeDivide(Long.MIN_VALUE, (-1L));
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
        try {
            FieldUtils.safeDivide(1L, 0L);
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
    }

    // -----------------------------------------------------------------------
    public void testSafeDivideRoundingModeLong() {
        TestCase.assertEquals(3L, FieldUtils.safeDivide(15L, 5L, RoundingMode.UNNECESSARY));
        TestCase.assertEquals(59L, FieldUtils.safeDivide(179L, 3L, RoundingMode.FLOOR));
        TestCase.assertEquals(60L, FieldUtils.safeDivide(179L, 3L, RoundingMode.CEILING));
        TestCase.assertEquals(60L, FieldUtils.safeDivide(179L, 3L, RoundingMode.HALF_UP));
        TestCase.assertEquals((-60L), FieldUtils.safeDivide((-179L), 3L, RoundingMode.HALF_UP));
        TestCase.assertEquals(60L, FieldUtils.safeDivide(179L, 3L, RoundingMode.HALF_DOWN));
        TestCase.assertEquals((-60L), FieldUtils.safeDivide((-179L), 3L, RoundingMode.HALF_DOWN));
        TestCase.assertEquals(Long.MAX_VALUE, FieldUtils.safeDivide(Long.MAX_VALUE, 1L, RoundingMode.UNNECESSARY));
        TestCase.assertEquals(Long.MIN_VALUE, FieldUtils.safeDivide(Long.MIN_VALUE, 1L, RoundingMode.UNNECESSARY));
        TestCase.assertEquals((-(Long.MAX_VALUE)), FieldUtils.safeDivide(Long.MAX_VALUE, (-1L), RoundingMode.UNNECESSARY));
        try {
            FieldUtils.safeDivide(Long.MIN_VALUE, (-1L), RoundingMode.UNNECESSARY);
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
        try {
            FieldUtils.safeDivide(1L, 0L, RoundingMode.UNNECESSARY);
            TestCase.fail();
        } catch (ArithmeticException e) {
        }
    }
}

