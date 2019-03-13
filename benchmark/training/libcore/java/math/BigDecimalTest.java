/**
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.java.math;


import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import junit.framework.TestCase;


public final class BigDecimalTest extends TestCase {
    public void testGetPrecision() {
        assertPrecision(1, "0");
        assertPrecision(1, "0.9");
        assertPrecision(16, "0.9999999999999999");
        assertPrecision(16, "9999999999999999");
        assertPrecision(19, "1000000000000000000");
        assertPrecision(19, "1000000000000000001");
        assertPrecision(19, "-1000000000000000001");
        assertPrecision(19, "-1000000000000000000");
        String tenNines = "9999999999";
        String fiftyNines = (((tenNines + tenNines) + tenNines) + tenNines) + tenNines;
        assertPrecision(10, ("0." + tenNines));
        assertPrecision(50, ("0." + fiftyNines));
        assertPrecision(250, ((((("0." + fiftyNines) + fiftyNines) + fiftyNines) + fiftyNines) + fiftyNines));
        assertPrecision(10, tenNines);
        assertPrecision(50, fiftyNines);
        assertPrecision(250, ((((fiftyNines + fiftyNines) + fiftyNines) + fiftyNines) + fiftyNines));
        // test these special cases because we know precision() uses longs internally
        String maxLong = Long.toString(Long.MAX_VALUE);
        assertPrecision(maxLong.length(), maxLong);
        String minLong = Long.toString(Long.MIN_VALUE);
        assertPrecision(((minLong.length()) - 1), minLong);
    }

    public void testRound() {
        BigDecimal bigDecimal = new BigDecimal("0.999999999999999");
        BigDecimal rounded = bigDecimal.round(new MathContext(2, RoundingMode.FLOOR));
        TestCase.assertEquals("0.99", rounded.toString());
    }

    // https://code.google.com/p/android/issues/detail?id=43480
    public void testPrecisionFromString() {
        BigDecimal a = new BigDecimal("-0.011111111111111111111");
        BigDecimal b = a.multiply(BigDecimal.ONE);
        TestCase.assertEquals("-0.011111111111111111111", a.toString());
        TestCase.assertEquals("-0.011111111111111111111", b.toString());
        TestCase.assertEquals(20, a.precision());
        TestCase.assertEquals(20, b.precision());
        TestCase.assertEquals(21, a.scale());
        TestCase.assertEquals(21, b.scale());
        TestCase.assertEquals("-11111111111111111111", a.unscaledValue().toString());
        TestCase.assertEquals("-11111111111111111111", b.unscaledValue().toString());
        TestCase.assertEquals(a, b);
        TestCase.assertEquals(b, a);
        TestCase.assertEquals(0, a.subtract(b).signum());
        TestCase.assertEquals(0, a.compareTo(b));
    }

    // https://code.google.com/p/android/issues/detail?id=54580
    public void test54580() {
        BigDecimal a = new BigDecimal("1.200002");
        TestCase.assertEquals("1.200002", a.toPlainString());
        TestCase.assertEquals("1.20", a.abs(new MathContext(3, RoundingMode.HALF_UP)).toPlainString());
        TestCase.assertEquals("1.200002", a.toPlainString());
    }
}

