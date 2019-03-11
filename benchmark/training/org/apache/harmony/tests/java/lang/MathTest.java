/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.harmony.tests.java.lang;


import junit.framework.TestCase;


public class MathTest extends TestCase {
    double HYP = Math.sqrt(2.0);

    double OPP = 1.0;

    double ADJ = 1.0;

    /* Required to make previous preprocessor flags work - do not remove */
    int unused = 0;

    /**
     * java.lang.Math#abs(double)
     */
    public void test_absD() {
        // Test for method double java.lang.Math.abs(double)
        TestCase.assertTrue("Incorrect double abs value", ((Math.abs((-1908.8976))) == 1908.8976));
        TestCase.assertTrue("Incorrect double abs value", ((Math.abs(1908.8976)) == 1908.8976));
    }

    /**
     * java.lang.Math#abs(float)
     */
    public void test_absF() {
        // Test for method float java.lang.Math.abs(float)
        TestCase.assertTrue("Incorrect float abs value", ((Math.abs((-1908.8976F))) == 1908.8976F));
        TestCase.assertTrue("Incorrect float abs value", ((Math.abs(1908.8976F)) == 1908.8976F));
    }

    /**
     * java.lang.Math#abs(int)
     */
    public void test_absI() {
        // Test for method int java.lang.Math.abs(int)
        TestCase.assertTrue("Incorrect int abs value", ((Math.abs((-1908897))) == 1908897));
        TestCase.assertTrue("Incorrect int abs value", ((Math.abs(1908897)) == 1908897));
    }

    /**
     * java.lang.Math#abs(long)
     */
    public void test_absJ() {
        // Test for method long java.lang.Math.abs(long)
        TestCase.assertTrue("Incorrect long abs value", ((Math.abs((-19088976000089L))) == 19088976000089L));
        TestCase.assertTrue("Incorrect long abs value", ((Math.abs(19088976000089L)) == 19088976000089L));
    }

    /**
     * java.lang.Math#acos(double)
     */
    public void test_acosD() {
        // Test for method double java.lang.Math.acos(double)
        double r = Math.cos(Math.acos(((ADJ) / (HYP))));
        long lr = Double.doubleToLongBits(r);
        long t = Double.doubleToLongBits(((ADJ) / (HYP)));
        TestCase.assertTrue("Returned incorrect arc cosine", (((lr == t) || ((lr + 1) == t)) || ((lr - 1) == t)));
    }

    /**
     * java.lang.Math#asin(double)
     */
    public void test_asinD() {
        // Test for method double java.lang.Math.asin(double)
        double r = Math.sin(Math.asin(((OPP) / (HYP))));
        long lr = Double.doubleToLongBits(r);
        long t = Double.doubleToLongBits(((OPP) / (HYP)));
        TestCase.assertTrue("Returned incorrect arc sine", (((lr == t) || ((lr + 1) == t)) || ((lr - 1) == t)));
    }

    /**
     * java.lang.Math#atan(double)
     */
    public void test_atanD() {
        // Test for method double java.lang.Math.atan(double)
        double answer = Math.tan(Math.atan(1.0));
        TestCase.assertTrue(("Returned incorrect arc tangent: " + answer), ((answer <= 1.0) && (answer >= 0.9999999999999998)));
    }

    /**
     * java.lang.Math#atan2(double, double)
     */
    public void test_atan2DD() {
        // Test for method double java.lang.Math.atan2(double, double)
        double answer = Math.atan(Math.tan(1.0));
        TestCase.assertTrue(("Returned incorrect arc tangent: " + answer), ((answer <= 1.0) && (answer >= 0.9999999999999998)));
    }

    /**
     * java.lang.Math#cbrt(double)
     */
    public void test_cbrt_D() {
        // Test for special situations
        TestCase.assertTrue(Double.isNaN(Math.cbrt(Double.NaN)));
        TestCase.assertEquals(Double.POSITIVE_INFINITY, Math.cbrt(Double.POSITIVE_INFINITY), 0.0);
        TestCase.assertEquals(Double.NEGATIVE_INFINITY, Math.cbrt(Double.NEGATIVE_INFINITY), 0.0);
        TestCase.assertEquals(Double.doubleToLongBits(0.0), Double.doubleToLongBits(Math.cbrt(0.0)));
        TestCase.assertEquals(Double.doubleToLongBits((+0.0)), Double.doubleToLongBits(Math.cbrt((+0.0))));
        TestCase.assertEquals(Double.doubleToLongBits((-0.0)), Double.doubleToLongBits(Math.cbrt((-0.0))));
        TestCase.assertEquals(3.0, Math.cbrt(27.0), 0.0);
        TestCase.assertEquals(23.111993172558684, Math.cbrt(12345.6), Math.ulp(23.111993172558684));
        TestCase.assertEquals(5.643803094122362E102, Math.cbrt(Double.MAX_VALUE), 0.0);
        TestCase.assertEquals(0.01, Math.cbrt(1.0E-6), 0.0);
        TestCase.assertEquals((-3.0), Math.cbrt((-27.0)), 0.0);
        TestCase.assertEquals((-23.111993172558684), Math.cbrt((-12345.6)), Math.ulp((-23.111993172558684)));
        TestCase.assertEquals(1.7031839360032603E-108, Math.cbrt(Double.MIN_VALUE), 0.0);
        TestCase.assertEquals((-0.01), Math.cbrt((-1.0E-6)), 0.0);
    }

    /**
     * java.lang.Math#ceil(double)
     */
    public void test_ceilD() {
        // Test for method double java.lang.Math.ceil(double)
        TestCase.assertEquals("Incorrect ceiling for double", 79, Math.ceil(78.89), 0);
        TestCase.assertEquals("Incorrect ceiling for double", (-78), Math.ceil((-78.89)), 0);
    }

    /**
     * cases for test_copySign_DD in MathTest/StrictMathTest
     */
    static final double[] COPYSIGN_DD_CASES = new double[]{ Double.POSITIVE_INFINITY, Double.MAX_VALUE, 3.4E302, 2.3, Double.MIN_NORMAL, (Double.MIN_NORMAL) / 2, Double.MIN_VALUE, +0.0, 0.0, -0.0, -(Double.MIN_VALUE), (-(Double.MIN_NORMAL)) / 2, -(Double.MIN_NORMAL), -4.5, -3.4E102, -(Double.MAX_VALUE), Double.NEGATIVE_INFINITY };

    /**
     * cases for test_copySign_FF in MathTest/StrictMathTest
     */
    static final float[] COPYSIGN_FF_CASES = new float[]{ Float.POSITIVE_INFINITY, Float.MAX_VALUE, 3.40000008E12F, 2.3F, Float.MIN_NORMAL, (Float.MIN_NORMAL) / 2, Float.MIN_VALUE, +0.0F, 0.0F, -0.0F, -(Float.MIN_VALUE), (-(Float.MIN_NORMAL)) / 2, -(Float.MIN_NORMAL), -4.5F, -5.6442E21F, -(Float.MAX_VALUE), Float.NEGATIVE_INFINITY };

    /**
     * java.lang.Math#cos(double)
     */
    public void test_cosD() {
        // Test for method double java.lang.Math.cos(double)
        TestCase.assertEquals("Incorrect answer", 1.0, Math.cos(0), 0.0);
        TestCase.assertEquals("Incorrect answer", 0.5403023058681398, Math.cos(1), 0.0);
    }

    /**
     * java.lang.Math#cosh(double)
     */
    public void test_cosh_D() {
        // Test for special situations
        TestCase.assertTrue(Double.isNaN(Math.cosh(Double.NaN)));
        TestCase.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.cosh(Double.POSITIVE_INFINITY), 0.0);
        TestCase.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.cosh(Double.NEGATIVE_INFINITY), 0.0);
        TestCase.assertEquals("Should return 1.0", 1.0, Math.cosh((+0.0)), 0.0);
        TestCase.assertEquals("Should return 1.0", 1.0, Math.cosh((-0.0)), 0.0);
        TestCase.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.cosh(1234.56), 0.0);
        TestCase.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.cosh((-1234.56)), 0.0);
        TestCase.assertEquals("Should return 1.0000000000005", 1.0000000000005, Math.cosh(1.0E-6), 0.0);
        TestCase.assertEquals("Should return 1.0000000000005", 1.0000000000005, Math.cosh((-1.0E-6)), 0.0);
        TestCase.assertEquals("Should return 5.212214351945598", 5.212214351945598, Math.cosh(2.33482), 0.0);
        TestCase.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.cosh(Double.MAX_VALUE), 0.0);
        TestCase.assertEquals("Should return 1.0", 1.0, Math.cosh(Double.MIN_VALUE), 0.0);
    }

    /**
     * java.lang.Math#exp(double)
     */
    public void test_expD() {
        // Test for method double java.lang.Math.exp(double)
        TestCase.assertTrue("Incorrect answer returned for simple power", ((Math.abs(((Math.exp(4.0)) - ((((Math.E) * (Math.E)) * (Math.E)) * (Math.E))))) < 0.1));
        TestCase.assertTrue("Incorrect answer returned for larger power", ((Math.log(((Math.abs(Math.exp(5.5))) - 5.5))) < 10.0));
    }

    /**
     * java.lang.Math#expm1(double)
     */
    public void test_expm1_D() {
        // Test for special cases
        TestCase.assertTrue("Should return NaN", Double.isNaN(Math.expm1(Double.NaN)));
        TestCase.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.expm1(Double.POSITIVE_INFINITY), 0.0);
        TestCase.assertEquals("Should return -1.0", (-1.0), Math.expm1(Double.NEGATIVE_INFINITY), 0.0);
        TestCase.assertEquals(Double.doubleToLongBits(0.0), Double.doubleToLongBits(Math.expm1(0.0)));
        TestCase.assertEquals(Double.doubleToLongBits((+0.0)), Double.doubleToLongBits(Math.expm1((+0.0))));
        TestCase.assertEquals(Double.doubleToLongBits((-0.0)), Double.doubleToLongBits(Math.expm1((-0.0))));
        TestCase.assertEquals("Should return -9.999950000166666E-6", (-9.999950000166666E-6), Math.expm1((-1.0E-5)), 0.0);
        TestCase.assertEquals("Should return 1.0145103074469635E60", 1.0145103074469635E60, Math.expm1(138.16951162), 0.0);
        TestCase.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.expm1(1.2345678912345679E26), 0.0);
        TestCase.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.expm1(Double.MAX_VALUE), 0.0);
        TestCase.assertEquals("Should return MIN_VALUE", Double.MIN_VALUE, Math.expm1(Double.MIN_VALUE), 0.0);
    }

    /**
     * java.lang.Math#floor(double)
     */
    public void test_floorD() {
        TestCase.assertEquals("Incorrect floor for int", 42, Math.floor(42), 0);
        TestCase.assertEquals("Incorrect floor for -int", (-2), Math.floor((-2)), 0);
        TestCase.assertEquals("Incorrect floor for zero", 0.0, Math.floor(0.0), 0);
        TestCase.assertEquals("Incorrect floor for +double", 78, Math.floor(78.89), 0);
        TestCase.assertEquals("Incorrect floor for -double", (-79), Math.floor((-78.89)), 0);
        TestCase.assertEquals("floor large +double", 3.7314645675925406E19, Math.floor(3.7314645675925406E19), 0);
        TestCase.assertEquals("floor large -double", (-8.173521839218E12), Math.floor((-8.173521839218E12)), 0);
        TestCase.assertEquals("floor small double", 0.0, Math.floor(1.11895241315E-102), 0);
        // Compare toString representations here since -0.0 = +0.0, and
        // NaN != NaN and we need to distinguish
        TestCase.assertEquals("Floor failed for NaN", Double.toString(Double.NaN), Double.toString(Math.floor(Double.NaN)));
        TestCase.assertEquals("Floor failed for +0.0", Double.toString((+0.0)), Double.toString(Math.floor((+0.0))));
        TestCase.assertEquals("Floor failed for -0.0", Double.toString((-0.0)), Double.toString(Math.floor((-0.0))));
        TestCase.assertEquals("Floor failed for +infinity", Double.toString(Double.POSITIVE_INFINITY), Double.toString(Math.floor(Double.POSITIVE_INFINITY)));
        TestCase.assertEquals("Floor failed for -infinity", Double.toString(Double.NEGATIVE_INFINITY), Double.toString(Math.floor(Double.NEGATIVE_INFINITY)));
    }

    /**
     * cases for test_getExponent_D in MathTest/StrictMathTest
     */
    static final double[] GETEXPONENT_D_CASES = new double[]{ Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.MAX_VALUE, -(Double.MAX_VALUE), 2.342E231, -2.342E231, 2800.0, -2800.0, 5.323, -5.323, 1.323, -1.323, 0.623, -0.623, 0.323, -0.323, (Double.MIN_NORMAL) * 24, (-(Double.MIN_NORMAL)) * 24, Double.MIN_NORMAL, -(Double.MIN_NORMAL), (Double.MIN_NORMAL) / 2, (-(Double.MIN_NORMAL)) / 2, Double.MIN_VALUE, -(Double.MIN_VALUE), +0.0, 0.0, -0.0, Double.NaN };

    /**
     * result for test_getExponent_D in MathTest/StrictMathTest
     */
    static final int[] GETEXPONENT_D_RESULTS = new int[]{ (Double.MAX_EXPONENT) + 1, (Double.MAX_EXPONENT) + 1, Double.MAX_EXPONENT, Double.MAX_EXPONENT, 768, 768, 11, 11, 2, 2, 0, 0, -1, -1, -2, -2, -1018, -1018, Double.MIN_EXPONENT, Double.MIN_EXPONENT, (Double.MIN_EXPONENT) - 1, (Double.MIN_EXPONENT) - 1, (Double.MIN_EXPONENT) - 1, (Double.MIN_EXPONENT) - 1, (Double.MIN_EXPONENT) - 1, (Double.MIN_EXPONENT) - 1, (Double.MIN_EXPONENT) - 1, (Double.MAX_EXPONENT) + 1 };

    /**
     * cases for test_getExponent_F in MathTest/StrictMathTest
     */
    static final float[] GETEXPONENT_F_CASES = new float[]{ Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.MAX_VALUE, -(Float.MAX_VALUE), 3.4256E23F, -3.4256E23F, 2800.0F, -2800.0F, 5.323F, -5.323F, 1.323F, -1.323F, 0.623F, -0.623F, 0.323F, -0.323F, (Float.MIN_NORMAL) * 24, (-(Float.MIN_NORMAL)) * 24, Float.MIN_NORMAL, -(Float.MIN_NORMAL), (Float.MIN_NORMAL) / 2, (-(Float.MIN_NORMAL)) / 2, Float.MIN_VALUE, -(Float.MIN_VALUE), +0.0F, 0.0F, -0.0F, Float.NaN, 1, (Float.MIN_NORMAL) * 1.5F };

    /**
     * result for test_getExponent_F in MathTest/StrictMathTest
     */
    static final int[] GETEXPONENT_F_RESULTS = new int[]{ (Float.MAX_EXPONENT) + 1, (Float.MAX_EXPONENT) + 1, Float.MAX_EXPONENT, Float.MAX_EXPONENT, 78, 78, 11, 11, 2, 2, 0, 0, -1, -1, -2, -2, -122, -122, Float.MIN_EXPONENT, Float.MIN_EXPONENT, (Float.MIN_EXPONENT) - 1, (Float.MIN_EXPONENT) - 1, (Float.MIN_EXPONENT) - 1, (Float.MIN_EXPONENT) - 1, (Float.MIN_EXPONENT) - 1, (Float.MIN_EXPONENT) - 1, (Float.MIN_EXPONENT) - 1, (Float.MAX_EXPONENT) + 1, 0, Float.MIN_EXPONENT };

    /**
     * java.lang.Math#hypot(double, double)
     */
    public void test_hypot_DD() {
        // Test for special cases
        TestCase.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.hypot(Double.POSITIVE_INFINITY, 1.0), 0.0);
        TestCase.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.hypot(Double.NEGATIVE_INFINITY, 123.324), 0.0);
        TestCase.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.hypot((-758.2587), Double.POSITIVE_INFINITY), 0.0);
        TestCase.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.hypot(5687.21, Double.NEGATIVE_INFINITY), 0.0);
        TestCase.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.hypot(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY), 0.0);
        TestCase.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.hypot(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), 0.0);
        TestCase.assertTrue("Should be NaN", Double.isNaN(Math.hypot(Double.NaN, 2342301.89843)));
        TestCase.assertTrue("Should be NaN", Double.isNaN(Math.hypot((-345.268), Double.NaN)));
        TestCase.assertEquals("Should return 2396424.905416697", 2396424.905416697, Math.hypot(12322.12, (-2396393.2258)), 0.0);
        TestCase.assertEquals("Should return 138.16958070558556", 138.16958070558556, Math.hypot((-138.16951162), 0.13817035864), 0.0);
        TestCase.assertEquals("Should return 1.7976931348623157E308", 1.7976931348623157E308, Math.hypot(Double.MAX_VALUE, 211370.35), 0.0);
        TestCase.assertEquals("Should return 5413.7185", 5413.7185, Math.hypot((-5413.7185), Double.MIN_VALUE), 0.0);
    }

    /**
     * java.lang.Math#IEEEremainder(double, double)
     */
    public void test_IEEEremainderDD() {
        // Test for method double java.lang.Math.IEEEremainder(double, double)
        TestCase.assertEquals("Incorrect remainder returned", 0.0, Math.IEEEremainder(1.0, 1.0), 0.0);
        TestCase.assertTrue("Incorrect remainder returned", (((Math.IEEEremainder(1.32, 89.765)) >= 0.014705063220631647) || ((Math.IEEEremainder(1.32, 89.765)) >= 0.01470506322063165)));
    }

    /**
     * java.lang.Math#log(double)
     */
    public void test_logD() {
        // Test for method double java.lang.Math.log(double)
        for (double d = 10; d >= (-10); d -= 0.5) {
            double answer = Math.log(Math.exp(d));
            TestCase.assertTrue(((("Answer does not equal expected answer for d = " + d) + " answer = ") + answer), ((Math.abs((answer - d))) <= (Math.abs((d * 1.0E-8)))));
        }
    }

    /**
     * java.lang.Math#log1p(double)
     */
    public void test_log1p_D() {
        // Test for special cases
        TestCase.assertTrue("Should return NaN", Double.isNaN(Math.log1p(Double.NaN)));
        TestCase.assertTrue("Should return NaN", Double.isNaN(Math.log1p((-32.0482175))));
        TestCase.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.log1p(Double.POSITIVE_INFINITY), 0.0);
        TestCase.assertEquals(Double.doubleToLongBits(0.0), Double.doubleToLongBits(Math.log1p(0.0)));
        TestCase.assertEquals(Double.doubleToLongBits((+0.0)), Double.doubleToLongBits(Math.log1p((+0.0))));
        TestCase.assertEquals(Double.doubleToLongBits((-0.0)), Double.doubleToLongBits(Math.log1p((-0.0))));
        TestCase.assertEquals("Should return -0.2941782295312541", (-0.2941782295312541), Math.log1p((-0.254856327)), 0.0);
        TestCase.assertEquals("Should return 7.368050685564151", 7.368050685564151, Math.log1p(1583.542), 0.0);
        TestCase.assertEquals("Should return 0.4633708685409921", 0.4633708685409921, Math.log1p(0.5894227), 0.0);
        TestCase.assertEquals("Should return 709.782712893384", 709.782712893384, Math.log1p(Double.MAX_VALUE), 0.0);
        TestCase.assertEquals("Should return Double.MIN_VALUE", Double.MIN_VALUE, Math.log1p(Double.MIN_VALUE), 0.0);
    }

    public void test_maxDD_Math() {
        /* use Math */
        MathTest.test_maxDD(true);
    }

    public void test_maxDD_Double() {
        /* use Math */
        MathTest.test_maxDD(false);
    }

    /**
     * java.lang.Math#max(float, float)
     */
    public void test_maxFF() {
        // Test for method float java.lang.Math.max(float, float)
        TestCase.assertTrue("Incorrect float max value", ((Math.max((-1908897.6F), 1908897.6F)) == 1908897.6F));
        TestCase.assertTrue("Incorrect float max value", ((Math.max(2.0F, 1908897.6F)) == 1908897.6F));
        TestCase.assertTrue("Incorrect float max value", ((Math.max((-2.0F), (-1908897.6F))) == (-2.0F)));
        // Compare toString representations here since -0.0 = +0.0, and
        // NaN != NaN and we need to distinguish
        TestCase.assertEquals("Max failed for NaN", Float.toString(Float.NaN), Float.toString(Math.max(Float.NaN, 42.0F)));
        TestCase.assertEquals("Max failed for NaN", Float.toString(Float.NaN), Float.toString(Math.max(42.0F, Float.NaN)));
        TestCase.assertEquals("Max failed for 0.0", Float.toString((+0.0F)), Float.toString(Math.max((+0.0F), (-0.0F))));
        TestCase.assertEquals("Max failed for 0.0", Float.toString((+0.0F)), Float.toString(Math.max((-0.0F), (+0.0F))));
        TestCase.assertEquals("Max failed for -0.0f", Float.toString((-0.0F)), Float.toString(Math.max((-0.0F), (-0.0F))));
        TestCase.assertEquals("Max failed for 0.0", Float.toString((+0.0F)), Float.toString(Math.max((+0.0F), (+0.0F))));
    }

    /**
     * java.lang.Math#max(int, int)
     */
    public void test_maxII() {
        // Test for method int java.lang.Math.max(int, int)
        TestCase.assertEquals("Incorrect int max value", 19088976, Math.max((-19088976), 19088976));
        TestCase.assertEquals("Incorrect int max value", 19088976, Math.max(20, 19088976));
        TestCase.assertEquals("Incorrect int max value", (-20), Math.max((-20), (-19088976)));
    }

    /**
     * java.lang.Math#max(long, long)
     */
    public void test_maxJJ() {
        // Test for method long java.lang.Math.max(long, long)
        TestCase.assertEquals("Incorrect long max value", 19088976000089L, Math.max((-19088976000089L), 19088976000089L));
        TestCase.assertEquals("Incorrect long max value", 19088976000089L, Math.max(20, 19088976000089L));
        TestCase.assertEquals("Incorrect long max value", (-20), Math.max((-20), (-19088976000089L)));
    }

    public void test_minDD_Math() {
        /* useMath */
        MathTest.test_minDD(true);
    }

    public void test_minDD_Double() {
        /* useMath */
        MathTest.test_minDD(false);
    }

    /**
     * java.lang.Math#min(float, float)
     */
    public void test_minFF() {
        // Test for method float java.lang.Math.min(float, float)
        TestCase.assertTrue("Incorrect float min value", ((Math.min((-1908897.6F), 1908897.6F)) == (-1908897.6F)));
        TestCase.assertTrue("Incorrect float min value", ((Math.min(2.0F, 1908897.6F)) == 2.0F));
        TestCase.assertTrue("Incorrect float min value", ((Math.min((-2.0F), (-1908897.6F))) == (-1908897.6F)));
        TestCase.assertEquals("Incorrect float min value", 1.0F, Math.min(1.0F, 1.0F));
        // Compare toString representations here since -0.0 = +0.0, and
        // NaN != NaN and we need to distinguish
        TestCase.assertEquals("Min failed for NaN", Float.toString(Float.NaN), Float.toString(Math.min(Float.NaN, 42.0F)));
        TestCase.assertEquals("Min failed for NaN", Float.toString(Float.NaN), Float.toString(Math.min(42.0F, Float.NaN)));
        TestCase.assertEquals("Min failed for -0.0", Float.toString((-0.0F)), Float.toString(Math.min((+0.0F), (-0.0F))));
        TestCase.assertEquals("Min failed for -0.0", Float.toString((-0.0F)), Float.toString(Math.min((-0.0F), (+0.0F))));
        TestCase.assertEquals("Min failed for -0.0f", Float.toString((-0.0F)), Float.toString(Math.min((-0.0F), (-0.0F))));
        TestCase.assertEquals("Min failed for 0.0", Float.toString((+0.0F)), Float.toString(Math.min((+0.0F), (+0.0F))));
    }

    /**
     * java.lang.Math#min(int, int)
     */
    public void test_minII() {
        // Test for method int java.lang.Math.min(int, int)
        TestCase.assertEquals("Incorrect int min value", (-19088976), Math.min((-19088976), 19088976));
        TestCase.assertEquals("Incorrect int min value", 20, Math.min(20, 19088976));
        TestCase.assertEquals("Incorrect int min value", (-19088976), Math.min((-20), (-19088976)));
    }

    /**
     * java.lang.Math#min(long, long)
     */
    public void test_minJJ() {
        // Test for method long java.lang.Math.min(long, long)
        TestCase.assertEquals("Incorrect long min value", (-19088976000089L), Math.min((-19088976000089L), 19088976000089L));
        TestCase.assertEquals("Incorrect long min value", 20, Math.min(20, 19088976000089L));
        TestCase.assertEquals("Incorrect long min value", (-19088976000089L), Math.min((-20), (-19088976000089L)));
    }

    /**
     * start number cases for test_nextAfter_DD in MathTest/StrictMathTest
     * NEXTAFTER_DD_START_CASES[i][0] is the start number
     * NEXTAFTER_DD_START_CASES[i][1] is the nextUp of start number
     * NEXTAFTER_DD_START_CASES[i][2] is the nextDown of start number
     */
    static final double[][] NEXTAFTER_DD_START_CASES = new double[][]{ new double[]{ 3.4, 3.4000000000000004, 3.3999999999999995 }, new double[]{ -3.4, -3.3999999999999995, -3.4000000000000004 }, new double[]{ 3.4233E109, 3.4233000000000005E109, 3.4232999999999996E109 }, new double[]{ -3.4233E109, -3.4232999999999996E109, -3.4233000000000005E109 }, new double[]{ +0.0, Double.MIN_VALUE, -(Double.MIN_VALUE) }, new double[]{ 0.0, Double.MIN_VALUE, -(Double.MIN_VALUE) }, new double[]{ -0.0, Double.MIN_VALUE, -(Double.MIN_VALUE) }, new double[]{ Double.MIN_VALUE, 1.0E-323, +0.0 }, new double[]{ -(Double.MIN_VALUE), -0.0, -1.0E-323 }, new double[]{ Double.MIN_NORMAL, 2.225073858507202E-308, 2.225073858507201E-308 }, new double[]{ -(Double.MIN_NORMAL), -2.225073858507201E-308, -2.225073858507202E-308 }, new double[]{ Double.MAX_VALUE, Double.POSITIVE_INFINITY, 1.7976931348623155E308 }, new double[]{ -(Double.MAX_VALUE), -1.7976931348623155E308, Double.NEGATIVE_INFINITY }, new double[]{ Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.MAX_VALUE }, new double[]{ Double.NEGATIVE_INFINITY, -(Double.MAX_VALUE), Double.NEGATIVE_INFINITY } };

    /**
     * direction number cases for test_nextAfter_DD/test_nextAfter_FD in
     * MathTest/StrictMathTest
     */
    static final double[] NEXTAFTER_DD_FD_DIRECTION_CASES = new double[]{ Double.POSITIVE_INFINITY, Double.MAX_VALUE, 8.8, 3.4, 1.4, Double.MIN_NORMAL, (Double.MIN_NORMAL) / 2, Double.MIN_VALUE, +0.0, 0.0, -0.0, -(Double.MIN_VALUE), (-(Double.MIN_NORMAL)) / 2, -(Double.MIN_NORMAL), -1.4, -3.4, -8.8, -(Double.MAX_VALUE), Double.NEGATIVE_INFINITY };

    /**
     * start number cases for test_nextAfter_FD in MathTest/StrictMathTest
     * NEXTAFTER_FD_START_CASES[i][0] is the start number
     * NEXTAFTER_FD_START_CASES[i][1] is the nextUp of start number
     * NEXTAFTER_FD_START_CASES[i][2] is the nextDown of start number
     */
    static final float[][] NEXTAFTER_FD_START_CASES = new float[][]{ new float[]{ 3.4F, 3.4000003F, 3.3999999F }, new float[]{ -3.4F, -3.3999999F, -3.4000003F }, new float[]{ 3.4233E19F, 3.4233002E19F, 3.4232998E19F }, new float[]{ -3.4233E19F, -3.4232998E19F, -3.4233002E19F }, new float[]{ +0.0F, Float.MIN_VALUE, -(Float.MIN_VALUE) }, new float[]{ 0.0F, Float.MIN_VALUE, -(Float.MIN_VALUE) }, new float[]{ -0.0F, Float.MIN_VALUE, -(Float.MIN_VALUE) }, new float[]{ Float.MIN_VALUE, 2.8E-45F, +0.0F }, new float[]{ -(Float.MIN_VALUE), -0.0F, -2.8E-45F }, new float[]{ Float.MIN_NORMAL, 1.1754945E-38F, 1.1754942E-38F }, new float[]{ -(Float.MIN_NORMAL), -1.1754942E-38F, -1.1754945E-38F }, new float[]{ Float.MAX_VALUE, Float.POSITIVE_INFINITY, 3.4028233E38F }, new float[]{ -(Float.MAX_VALUE), -3.4028233E38F, Float.NEGATIVE_INFINITY }, new float[]{ Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.MAX_VALUE }, new float[]{ Float.NEGATIVE_INFINITY, -(Float.MAX_VALUE), Float.NEGATIVE_INFINITY } };

    /**
     * java.lang.Math#pow(double, double)
     */
    public void test_powDD() {
        // Test for method double java.lang.Math.pow(double, double)
        double NZERO = longTodouble(((doubleTolong(0.0)) ^ -9223372036854775808L));
        double p1 = 1.0;
        double p2 = 2.0;
        double p3 = 3.0;
        double p4 = 4.0;
        double p5 = 5.0;
        double p6 = 6.0;
        double p7 = 7.0;
        double p8 = 8.0;
        double p9 = 9.0;
        double p10 = 10.0;
        double p11 = 11.0;
        double p12 = 12.0;
        double p13 = 13.0;
        double p14 = 14.0;
        double p15 = 15.0;
        double p16 = 16.0;
        double[] values = new double[]{ p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16 };
        for (int x = 0; x < (values.length); x++) {
            double dval = values[x];
            double nagateDval = negateDouble(dval);
            if (nagateDval == (Double.NaN)) {
                continue;
            }
            // If the second argument is positive or negative zero, then the
            // result is 1.0.
            TestCase.assertEquals((("Result should be Math.pow(" + dval) + ",-0.0)=+1.0"), 1.0, Math.pow(dval, NZERO));
            TestCase.assertEquals((("Result should be Math.pow(" + nagateDval) + ",-0.0)=+1.0"), 1.0, Math.pow(nagateDval, NZERO));
            TestCase.assertEquals((("Result should be Math.pow(" + dval) + ",+0.0)=+1.0"), 1.0, Math.pow(dval, (+0.0)));
            TestCase.assertEquals((("Result should be Math.pow(" + nagateDval) + ",+0.0)=+1.0"), 1.0, Math.pow(nagateDval, (+0.0)));
            // If the second argument is 1.0, then the result is the same as the
            // first argument.
            TestCase.assertEquals(((((("Result should be Math.pow(" + dval) + ",") + 1.0) + ")=") + dval), dval, Math.pow(dval, 1.0));
            TestCase.assertEquals(((((("Result should be Math.pow(" + nagateDval) + ",") + 1.0) + ")=") + nagateDval), nagateDval, Math.pow(nagateDval, 1.0));
            // If the second argument is NaN, then the result is NaN.
            TestCase.assertEquals(((((("Result should be Math.pow(" + nagateDval) + ",") + (Double.NaN)) + ")=") + (Double.NaN)), Double.NaN, Math.pow(nagateDval, Double.NaN));
            if (dval > 1) {
                // If the first argument is NaN and the second argument is
                // nonzero,
                // then the result is NaN.
                TestCase.assertEquals(((((("Result should be Math.pow(" + (Double.NaN)) + ",") + dval) + ")=") + (Double.NaN)), Double.NaN, Math.pow(Double.NaN, dval));
                TestCase.assertEquals(((((("Result should be Math.pow(" + (Double.NaN)) + ",") + nagateDval) + ")=") + (Double.NaN)), Double.NaN, Math.pow(Double.NaN, nagateDval));
                /* If the first argument is positive zero and the second
                argument is greater than zero, or the first argument is
                positive infinity and the second argument is less than zero,
                then the result is positive zero.
                 */
                TestCase.assertEquals(((((("Result should be Math.pow(" + 0.0) + ",") + dval) + ")=") + 0.0), (+0.0), Math.pow(0.0, dval));
                TestCase.assertEquals(((((("Result should be Math.pow(" + (Double.POSITIVE_INFINITY)) + ",") + nagateDval) + ")=") + 0.0), (+0.0), Math.pow(Double.POSITIVE_INFINITY, nagateDval));
                /* If the first argument is positive zero and the second
                argument is less than zero, or the first argument is positive
                infinity and the second argument is greater than zero, then
                the result is positive infinity.
                 */
                TestCase.assertEquals(((((("Result should be Math.pow(" + 0.0) + ",") + nagateDval) + ")=") + (Double.POSITIVE_INFINITY)), Double.POSITIVE_INFINITY, Math.pow(0.0, nagateDval));
                TestCase.assertEquals(((((("Result should be Math.pow(" + (Double.POSITIVE_INFINITY)) + ",") + dval) + ")=") + (Double.POSITIVE_INFINITY)), Double.POSITIVE_INFINITY, Math.pow(Double.POSITIVE_INFINITY, dval));
                // Not a finite odd integer
                if ((dval % 2) == 0) {
                    /* If the first argument is negative zero and the second
                    argument is greater than zero but not a finite odd
                    integer, or the first argument is negative infinity and
                    the second argument is less than zero but not a finite
                    odd integer, then the result is positive zero.
                     */
                    TestCase.assertEquals(((((("Result should be Math.pow(" + NZERO) + ",") + dval) + ")=") + 0.0), (+0.0), Math.pow(NZERO, dval));
                    TestCase.assertEquals(((((("Result should be Math.pow(" + (Double.NEGATIVE_INFINITY)) + ",") + nagateDval) + ")=") + 0.0), (+0.0), Math.pow(Double.NEGATIVE_INFINITY, nagateDval));
                    /* If the first argument is negative zero and the second
                    argument is less than zero but not a finite odd integer,
                    or the first argument is negative infinity and the second
                    argument is greater than zero but not a finite odd
                    integer, then the result is positive infinity.
                     */
                    TestCase.assertEquals(((((("Result should be Math.pow(" + NZERO) + ",") + nagateDval) + ")=") + (Double.POSITIVE_INFINITY)), Double.POSITIVE_INFINITY, Math.pow(NZERO, nagateDval));
                    TestCase.assertEquals(((((("Result should be Math.pow(" + (Double.NEGATIVE_INFINITY)) + ",") + dval) + ")=") + (Double.POSITIVE_INFINITY)), Double.POSITIVE_INFINITY, Math.pow(Double.NEGATIVE_INFINITY, dval));
                }
                // finite odd integer
                if ((dval % 2) != 0) {
                    /* If the first argument is negative zero and the second
                    argument is a positive finite odd integer, or the first
                    argument is negative infinity and the second argument is
                    a negative finite odd integer, then the result is
                    negative zero.
                     */
                    TestCase.assertEquals(((((("Result should be Math.pow(" + NZERO) + ",") + dval) + ")=") + NZERO), NZERO, Math.pow(NZERO, dval));
                    TestCase.assertEquals(((((("Result should be Math.pow(" + (Double.NEGATIVE_INFINITY)) + ",") + nagateDval) + ")=") + NZERO), NZERO, Math.pow(Double.NEGATIVE_INFINITY, nagateDval));
                    /* If the first argument is negative zero and the second
                    argument is a negative finite odd integer, or the first
                    argument is negative infinity and the second argument is
                    a positive finite odd integer then the result is negative
                    infinity.
                     */
                    TestCase.assertEquals(((((("Result should be Math.pow(" + NZERO) + ",") + nagateDval) + ")=") + (Double.NEGATIVE_INFINITY)), Double.NEGATIVE_INFINITY, Math.pow(NZERO, nagateDval));
                    TestCase.assertEquals(((((("Result should be Math.pow(" + (Double.NEGATIVE_INFINITY)) + ",") + dval) + ")=") + (Double.NEGATIVE_INFINITY)), Double.NEGATIVE_INFINITY, Math.pow(Double.NEGATIVE_INFINITY, dval));
                }
                /**
                 * 1. If the first argument is finite and less than zero if the
                 * second argument is a finite even integer, the result is equal
                 * to the result of raising the absolute value of the first
                 * argument to the power of the second argument
                 *
                 * 2. if the second argument is a finite odd integer, the result is equal to the
                 * negative of the result of raising the absolute value of the
                 * first argument to the power of the second argument
                 *
                 * 3. if the second argument is finite and not an integer, then the result
                 * is NaN.
                 */
                for (int j = 1; j < (values.length); j++) {
                    double jval = values[j];
                    if ((jval % 2.0) == 0.0) {
                        TestCase.assertEquals(((("" + nagateDval) + " ") + jval), Math.pow(dval, jval), Math.pow(nagateDval, jval));
                    } else {
                        TestCase.assertEquals(((("" + nagateDval) + " ") + jval), ((-1.0) * (Math.pow(dval, jval))), Math.pow(nagateDval, jval));
                    }
                    TestCase.assertEquals(Double.NaN, Math.pow(nagateDval, (jval / 0.5467)));
                    TestCase.assertEquals(Double.NaN, Math.pow(nagateDval, (((-1.0) * jval) / 0.5467)));
                }
            }
            if (dval > 1) {
                /* If the absolute value of the first argument is greater than 1
                and the second argument is positive infinity, or the absolute
                value of the first argument is less than 1 and the second
                argument is negative infinity, then the result is positive
                infinity.
                 */
                TestCase.assertEquals(((((("Result should be Math.pow(" + dval) + ",") + (Double.POSITIVE_INFINITY)) + ")=") + (Double.POSITIVE_INFINITY)), Double.POSITIVE_INFINITY, Math.pow(dval, Double.POSITIVE_INFINITY));
                TestCase.assertEquals(((((("Result should be Math.pow(" + nagateDval) + ",") + (Double.NEGATIVE_INFINITY)) + ")=") + (Double.POSITIVE_INFINITY)), Double.POSITIVE_INFINITY, Math.pow((-0.13456), Double.NEGATIVE_INFINITY));
                /* If the absolute value of the first argument is greater than 1
                and the second argument is negative infinity, or the absolute
                value of the first argument is less than 1 and the second
                argument is positive infinity, then the result is positive
                zero.
                 */
                TestCase.assertEquals((((("Result should be Math.pow(" + dval) + ",") + (Double.NEGATIVE_INFINITY)) + ")= +0.0"), (+0.0), Math.pow(dval, Double.NEGATIVE_INFINITY));
                TestCase.assertEquals((((("Result should be Math.pow(" + nagateDval) + ",") + (Double.POSITIVE_INFINITY)) + ")= +0.0"), (+0.0), Math.pow((-0.13456), Double.POSITIVE_INFINITY));
            }
            TestCase.assertEquals(((((("Result should be Math.pow(" + 0.0) + ",") + dval) + ")=") + 0.0), 0.0, Math.pow(0.0, dval));
            TestCase.assertEquals(((((("Result should be Math.pow(" + (Double.NaN)) + ",") + dval) + ")=") + (Double.NaN)), Double.NaN, Math.pow(Double.NaN, dval));
        }
        TestCase.assertTrue("pow returned incorrect value", (((long) (Math.pow(2, 8))) == 256L));
        TestCase.assertTrue("pow returned incorrect value", ((Math.pow(2, (-8))) == 0.00390625));
        TestCase.assertEquals("Incorrect root returned1", 2, Math.sqrt(Math.pow(Math.sqrt(2), 4)), 0);
        TestCase.assertEquals(Double.NEGATIVE_INFINITY, Math.pow((-10.0), 3.093403029238847E15));
        TestCase.assertEquals(Double.POSITIVE_INFINITY, Math.pow(10.0, 3.093403029238847E15));
    }

    /**
     * java.lang.Math#rint(double)
     */
    public void test_rintD() {
        // Test for method double java.lang.Math.rint(double)
        TestCase.assertEquals("Failed to round properly - up to odd", 3.0, Math.rint(2.9), 0.0);
        TestCase.assertTrue("Failed to round properly - NaN", Double.isNaN(Math.rint(Double.NaN)));
        TestCase.assertEquals("Failed to round properly down  to even", 2.0, Math.rint(2.1), 0.0);
        TestCase.assertTrue((("Failed to round properly " + 2.5) + " to even"), ((Math.rint(2.5)) == 2.0));
        TestCase.assertTrue(("Failed to round properly " + (+0.0)), ((Math.rint((+0.0))) == (+0.0)));
        TestCase.assertTrue(("Failed to round properly " + (-0.0)), ((Math.rint((-0.0))) == (-0.0)));
    }

    /**
     * java.lang.Math#round(double)
     */
    public void test_roundD() {
        // Test for method long java.lang.Math.round(double)
        TestCase.assertEquals("Incorrect rounding of a float", (-91), Math.round((-90.89)));
    }

    /**
     * java.lang.Math#round(float)
     */
    public void test_roundF() {
        // Test for method int java.lang.Math.round(float)
        TestCase.assertEquals("Incorrect rounding of a float", (-91), Math.round((-90.89F)));
    }

    /**
     * java.lang.Math#signum(double)
     */
    public void test_signum_D() {
        TestCase.assertTrue(Double.isNaN(Math.signum(Double.NaN)));
        TestCase.assertTrue(Double.isNaN(Math.signum(Double.NaN)));
        TestCase.assertEquals(Double.doubleToLongBits(0.0), Double.doubleToLongBits(Math.signum(0.0)));
        TestCase.assertEquals(Double.doubleToLongBits((+0.0)), Double.doubleToLongBits(Math.signum((+0.0))));
        TestCase.assertEquals(Double.doubleToLongBits((-0.0)), Double.doubleToLongBits(Math.signum((-0.0))));
        TestCase.assertEquals(1.0, Math.signum(253681.2187962), 0.0);
        TestCase.assertEquals((-1.0), Math.signum((-1.2587469356E8)), 0.0);
        if (!(System.getProperty("os.arch").equals("armv7"))) {
            TestCase.assertEquals(1.0, Math.signum(1.2587E-308), 0.0);
            TestCase.assertEquals((-1.0), Math.signum((-1.2587E-308)), 0.0);
        }
        TestCase.assertEquals(1.0, Math.signum(Double.MAX_VALUE), 0.0);
        if (!(System.getProperty("os.arch").equals("armv7"))) {
            TestCase.assertEquals(1.0, Math.signum(Double.MIN_VALUE), 0.0);
        }
        TestCase.assertEquals((-1.0), Math.signum((-(Double.MAX_VALUE))), 0.0);
        if (!(System.getProperty("os.arch").equals("armv7"))) {
            TestCase.assertEquals((-1.0), Math.signum((-(Double.MIN_VALUE))), 0.0);
        }
        TestCase.assertEquals(1.0, Math.signum(Double.POSITIVE_INFINITY), 0.0);
        TestCase.assertEquals((-1.0), Math.signum(Double.NEGATIVE_INFINITY), 0.0);
    }

    /**
     * java.lang.Math#signum(float)
     */
    public void test_signum_F() {
        TestCase.assertTrue(Float.isNaN(Math.signum(Float.NaN)));
        TestCase.assertEquals(Float.floatToIntBits(0.0F), Float.floatToIntBits(Math.signum(0.0F)));
        TestCase.assertEquals(Float.floatToIntBits((+0.0F)), Float.floatToIntBits(Math.signum((+0.0F))));
        TestCase.assertEquals(Float.floatToIntBits((-0.0F)), Float.floatToIntBits(Math.signum((-0.0F))));
        TestCase.assertEquals(1.0F, Math.signum(253681.22F), 0.0F);
        TestCase.assertEquals((-1.0F), Math.signum((-1.25874696E8F)), 0.0F);
        if (!(System.getProperty("os.arch").equals("armv7"))) {
            TestCase.assertEquals(1.0F, Math.signum(1.2587E-11F), 0.0F);
            TestCase.assertEquals((-1.0F), Math.signum((-1.2587E-11F)), 0.0F);
        }
        TestCase.assertEquals(1.0F, Math.signum(Float.MAX_VALUE), 0.0F);
        if (!(System.getProperty("os.arch").equals("armv7"))) {
            TestCase.assertEquals(1.0F, Math.signum(Float.MIN_VALUE), 0.0F);
        }
        TestCase.assertEquals((-1.0F), Math.signum((-(Float.MAX_VALUE))), 0.0F);
        if (!(System.getProperty("os.arch").equals("armv7"))) {
            TestCase.assertEquals((-1.0F), Math.signum((-(Float.MIN_VALUE))), 0.0F);
        }
        TestCase.assertEquals(1.0F, Math.signum(Float.POSITIVE_INFINITY), 0.0F);
        TestCase.assertEquals((-1.0F), Math.signum(Float.NEGATIVE_INFINITY), 0.0F);
    }

    /**
     * java.lang.Math#sin(double)
     */
    public void test_sinD() {
        // Test for method double java.lang.Math.sin(double)
        TestCase.assertEquals("Incorrect answer", 0.0, Math.sin(0), 0.0);
        TestCase.assertEquals("Incorrect answer", 0.8414709848078965, Math.sin(1), 0.0);
    }

    /**
     * java.lang.Math#sinh(double)
     */
    public void test_sinh_D() {
        // Test for special situations
        TestCase.assertTrue(Double.isNaN(Math.sinh(Double.NaN)));
        TestCase.assertEquals(Double.POSITIVE_INFINITY, Math.sinh(Double.POSITIVE_INFINITY), 0.0);
        TestCase.assertEquals(Double.NEGATIVE_INFINITY, Math.sinh(Double.NEGATIVE_INFINITY), 0.0);
        TestCase.assertEquals(Double.doubleToLongBits(0.0), Double.doubleToLongBits(Math.sinh(0.0)));
        TestCase.assertEquals(Double.doubleToLongBits((+0.0)), Double.doubleToLongBits(Math.sinh((+0.0))));
        TestCase.assertEquals(Double.doubleToLongBits((-0.0)), Double.doubleToLongBits(Math.sinh((-0.0))));
        TestCase.assertEquals(Double.POSITIVE_INFINITY, Math.sinh(1234.56), 0.0);
        TestCase.assertEquals(Double.NEGATIVE_INFINITY, Math.sinh((-1234.56)), 0.0);
        TestCase.assertEquals(1.0000000000001666E-6, Math.sinh(1.0E-6), 0.0);
        TestCase.assertEquals((-1.0000000000001666E-6), Math.sinh((-1.0E-6)), 0.0);
        TestCase.assertEquals(5.115386441963859, Math.sinh(2.33482), Math.ulp(5.115386441963859));
        TestCase.assertEquals(Double.POSITIVE_INFINITY, Math.sinh(Double.MAX_VALUE), 0.0);
        TestCase.assertEquals(4.9E-324, Math.sinh(Double.MIN_VALUE), 0.0);
    }

    /**
     * java.lang.Math#sqrt(double)
     */
    public void test_sqrtD() {
        // Test for method double java.lang.Math.sqrt(double)
        TestCase.assertEquals("Incorrect root returned2", 7, Math.sqrt(49), 0);
    }

    /**
     * java.lang.Math#tan(double)
     */
    public void test_tanD() {
        // Test for method double java.lang.Math.tan(double)
        TestCase.assertEquals("Incorrect answer", 0.0, Math.tan(0), 0.0);
        if (System.getProperty("os.arch").equals("armv7")) {
            // Relax the epsilon requirement for armv7.
            TestCase.assertEquals("Incorrect answer", 1.557407724654902, Math.tan(1), 1.0E-7);
        } else {
            TestCase.assertEquals("Incorrect answer", 1.557407724654902, Math.tan(1), 0.0);
        }
    }

    /**
     * java.lang.Math#tanh(double)
     */
    public void test_tanh_D() {
        // Test for special situations
        TestCase.assertTrue("Should return NaN", Double.isNaN(Math.tanh(Double.NaN)));
        TestCase.assertEquals("Should return +1.0", (+1.0), Math.tanh(Double.POSITIVE_INFINITY), 0.0);
        TestCase.assertEquals("Should return -1.0", (-1.0), Math.tanh(Double.NEGATIVE_INFINITY), 0.0);
        TestCase.assertEquals(Double.doubleToLongBits(0.0), Double.doubleToLongBits(Math.tanh(0.0)));
        TestCase.assertEquals(Double.doubleToLongBits((+0.0)), Double.doubleToLongBits(Math.tanh((+0.0))));
        TestCase.assertEquals(Double.doubleToLongBits((-0.0)), Double.doubleToLongBits(Math.tanh((-0.0))));
        TestCase.assertEquals("Should return 1.0", 1.0, Math.tanh(1234.56), 0.0);
        TestCase.assertEquals("Should return -1.0", (-1.0), Math.tanh((-1234.56)), 0.0);
        TestCase.assertEquals("Should return 9.999999999996666E-7", 9.999999999996666E-7, Math.tanh(1.0E-6), 0.0);
        TestCase.assertEquals("Should return 0.981422884124941", 0.981422884124941, Math.tanh(2.33482), 0.0);
        TestCase.assertEquals("Should return 1.0", 1.0, Math.tanh(Double.MAX_VALUE), 0.0);
        TestCase.assertEquals("Should return 4.9E-324", 4.9E-324, Math.tanh(Double.MIN_VALUE), 0.0);
    }

    /**
     * java.lang.Math#random()
     */
    public void test_random() {
        // There isn't a place for these tests so just stick them here
        TestCase.assertEquals("Wrong value E", 4613303445314885481L, Double.doubleToLongBits(Math.E));
        TestCase.assertEquals("Wrong value PI", 4614256656552045848L, Double.doubleToLongBits(Math.PI));
        for (int i = 500; i >= 0; i--) {
            double d = Math.random();
            TestCase.assertTrue(("Generated number is out of range: " + d), ((d >= 0.0) && (d < 1.0)));
        }
    }

    /**
     * java.lang.Math#toRadians(double)
     */
    public void test_toRadiansD() {
        for (double d = 500; d >= 0; d -= 1.0) {
            double converted = Math.toDegrees(Math.toRadians(d));
            TestCase.assertTrue(("Converted number not equal to original. d = " + d), ((converted >= (d * 0.99999999)) && (converted <= (d * 1.00000001))));
        }
    }

    /**
     * java.lang.Math#toDegrees(double)
     */
    public void test_toDegreesD() {
        for (double d = 500; d >= 0; d -= 1.0) {
            double converted = Math.toRadians(Math.toDegrees(d));
            TestCase.assertTrue(("Converted number not equal to original. d = " + d), ((converted >= (d * 0.99999999)) && (converted <= (d * 1.00000001))));
        }
    }

    /**
     * {@link java.lang.Math#shiftIntBits(int, int)}
     *
     * @since 1.6
     */
    public void test_shiftIntBits_II() {
        if (System.getProperty("os.arch").equals("armv7")) {
            return;
        }
        class Tuple {
            public int result;

            public int value;

            public int factor;

            public Tuple(int result, int value, int factor) {
                this.result = result;
                this.value = value;
                this.factor = factor;
            }
        }
        final Tuple[] TUPLES = // round to infinity
        new Tuple[]{ // sub-normal to sub-normal
        new Tuple(0, 1, (-1)), // round to even
        new Tuple(2, 3, (-1)), // round to even
        new Tuple(1, 5, (-3)), // round to infinity
        new Tuple(2, 13, (-3)), // round to infinity
        // normal to sub-normal
        new Tuple(2, 27262976, (-24)), // round to even
        new Tuple(4, 31457280, (-24)), // round to even
        new Tuple(3, 29884416, (-24)), // round to infinity
        new Tuple(4, 31981568, (-24)) }// round to infinity
        ;
        for (int i = 0; i < (TUPLES.length); ++i) {
            Tuple tuple = TUPLES[i];
            TestCase.assertEquals(tuple.result, Float.floatToIntBits(Math.scalb(Float.intBitsToFloat(tuple.value), tuple.factor)));
            TestCase.assertEquals(tuple.result, Float.floatToIntBits((-(Math.scalb((-(Float.intBitsToFloat(tuple.value))), tuple.factor)))));
        }
    }

    /**
     * {@link java.lang.Math#shiftLongBits(long, long)}
     * <p/>
     * Round result to nearest value on precision lost.
     *
     * @since 1.6
     */
    public void test_shiftLongBits_LL() {
        if (System.getProperty("os.arch").equals("armv7")) {
            return;
        }
        class Tuple {
            public long result;

            public long value;

            public int factor;

            public Tuple(long result, long value, int factor) {
                this.result = result;
                this.value = value;
                this.factor = factor;
            }
        }
        final Tuple[] TUPLES = new Tuple[]{ // sub-normal to sub-normal
        new Tuple(0L, 1L, (-1)), // round to even
        new Tuple(2L, 3L, (-1)), // round to even
        new Tuple(1L, 5L, (-3)), // round to infinity
        new Tuple(2L, 13L, (-3)), // round to infinity
        // normal to sub-normal
        new Tuple(2L, 14636698788954112L, (-53))// round to even
        , new Tuple(4L, 16888498602639360L, (-53))// round to even
        , new Tuple(3L, 14918173765664768L, (-53))// round to infinity
        , new Tuple(4L, 17169973579350016L, (-53))// round to infinity
         };
        for (int i = 0; i < (TUPLES.length); ++i) {
            Tuple tuple = TUPLES[i];
            TestCase.assertEquals(tuple.result, Double.doubleToLongBits(Math.scalb(Double.longBitsToDouble(tuple.value), tuple.factor)));
            TestCase.assertEquals(tuple.result, Double.doubleToLongBits((-(Math.scalb((-(Double.longBitsToDouble(tuple.value))), tuple.factor)))));
        }
    }
}

