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
package libcore.java.lang;


import junit.framework.TestCase;


public class OldAndroidMathTest extends TestCase {
    private static final double HYP = Math.sqrt(2.0);

    private static final double OPP = 1.0;

    private static final double ADJ = 1.0;

    /* Required to make previous preprocessor flags work - do not remove */
    int unused = 0;

    public void testAbsD() {
        // Test for method double java.lang.Math.abs(double)
        TestCase.assertTrue("Incorrect double abs value", ((Math.abs((-1908.8976))) == 1908.8976));
        TestCase.assertTrue("Incorrect double abs value", ((Math.abs(1908.8976)) == 1908.8976));
    }

    public void testAbsF() {
        // Test for method float java.lang.Math.abs(float)
        TestCase.assertTrue("Incorrect float abs value", ((Math.abs((-1908.8976F))) == 1908.8976F));
        TestCase.assertTrue("Incorrect float abs value", ((Math.abs(1908.8976F)) == 1908.8976F));
    }

    public void testAbsI() {
        // Test for method int java.lang.Math.abs(int)
        TestCase.assertTrue("Incorrect int abs value", ((Math.abs((-1908897))) == 1908897));
        TestCase.assertTrue("Incorrect int abs value", ((Math.abs(1908897)) == 1908897));
    }

    public void testAbsJ() {
        // Test for method long java.lang.Math.abs(long)
        TestCase.assertTrue("Incorrect long abs value", ((Math.abs((-19088976000089L))) == 19088976000089L));
        TestCase.assertTrue("Incorrect long abs value", ((Math.abs(19088976000089L)) == 19088976000089L));
    }

    public void testAcosD() {
        // Test for method double java.lang.Math.acos(double)
        double r = Math.cos(Math.acos(((OldAndroidMathTest.ADJ) / (OldAndroidMathTest.HYP))));
        long lr = Double.doubleToLongBits(r);
        long t = Double.doubleToLongBits(((OldAndroidMathTest.ADJ) / (OldAndroidMathTest.HYP)));
        TestCase.assertTrue("Returned incorrect arc cosine", (((lr == t) || ((lr + 1) == t)) || ((lr - 1) == t)));
    }

    public void testAsinD() {
        // Test for method double java.lang.Math.asin(double)
        double r = Math.sin(Math.asin(((OldAndroidMathTest.OPP) / (OldAndroidMathTest.HYP))));
        long lr = Double.doubleToLongBits(r);
        long t = Double.doubleToLongBits(((OldAndroidMathTest.OPP) / (OldAndroidMathTest.HYP)));
        TestCase.assertTrue("Returned incorrect arc sine", (((lr == t) || ((lr + 1) == t)) || ((lr - 1) == t)));
    }

    public void testAtanD() {
        // Test for method double java.lang.Math.atan(double)
        double answer = Math.tan(Math.atan(1.0));
        TestCase.assertTrue(("Returned incorrect arc tangent: " + answer), ((answer <= 1.0) && (answer >= 0.9999999999999998)));
    }

    public void testAtan2DD() {
        // Test for method double java.lang.Math.atan2(double, double)
        double answer = Math.atan(Math.tan(1.0));
        TestCase.assertTrue(("Returned incorrect arc tangent: " + answer), ((answer <= 1.0) && (answer >= 0.9999999999999998)));
    }

    public void testCbrtD() {
        // Test for special situations
        TestCase.assertTrue("Should return Double.NaN", Double.isNaN(Math.cbrt(Double.NaN)));
        OldAndroidMathTest.assertEquals("Should return Double.POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.cbrt(Double.POSITIVE_INFINITY), 0.0);
        OldAndroidMathTest.assertEquals("Should return Double.NEGATIVE_INFINITY", Double.NEGATIVE_INFINITY, Math.cbrt(Double.NEGATIVE_INFINITY), 0.0);
        TestCase.assertEquals(Double.doubleToLongBits(0.0), Double.doubleToLongBits(Math.cbrt(0.0)));
        TestCase.assertEquals(Double.doubleToLongBits((+0.0)), Double.doubleToLongBits(Math.cbrt((+0.0))));
        TestCase.assertEquals(Double.doubleToLongBits((-0.0)), Double.doubleToLongBits(Math.cbrt((-0.0))));
        OldAndroidMathTest.assertEquals("Should return 3.0", 3.0, Math.cbrt(27.0), 0.0);
        OldAndroidMathTest.assertEquals("Should return 23.111993172558684", 23.111993172558684, Math.cbrt(12345.6), 0.0);
        OldAndroidMathTest.assertEquals("Should return 5.643803094122362E102", 5.643803094122362E102, Math.cbrt(Double.MAX_VALUE), 0.0);
        OldAndroidMathTest.assertEquals("Should return 0.01", 0.01, Math.cbrt(1.0E-6), 0.0);
        OldAndroidMathTest.assertEquals("Should return -3.0", (-3.0), Math.cbrt((-27.0)), 0.0);
        OldAndroidMathTest.assertEquals("Should return -23.111993172558684", (-23.111993172558684), Math.cbrt((-12345.6)), 0.0);
        OldAndroidMathTest.assertEquals("Should return 1.7031839360032603E-108", 1.7031839360032603E-108, Math.cbrt(Double.MIN_VALUE), 0.0);
        OldAndroidMathTest.assertEquals("Should return -0.01", (-0.01), Math.cbrt((-1.0E-6)), 0.0);
    }

    public void testCeilD() {
        // Test for method double java.lang.Math.ceil(double)
        OldAndroidMathTest.assertEquals("Incorrect ceiling for double", 79, Math.ceil(78.89), 0);
        OldAndroidMathTest.assertEquals("Incorrect ceiling for double", (-78), Math.ceil((-78.89)), 0);
    }

    public void testCosD() {
        // Test for method double java.lang.Math.cos(double)
        OldAndroidMathTest.assertEquals("Incorrect answer", 1.0, Math.cos(0), 0.0);
        OldAndroidMathTest.assertEquals("Incorrect answer", 0.5403023058681398, Math.cos(1), 0.0);
    }

    public void testCoshD() {
        // Test for special situations
        TestCase.assertTrue(Double.isNaN(Math.cosh(Double.NaN)));
        OldAndroidMathTest.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.cosh(Double.POSITIVE_INFINITY), 0.0);
        OldAndroidMathTest.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.cosh(Double.NEGATIVE_INFINITY), 0.0);
        OldAndroidMathTest.assertEquals("Should return 1.0", 1.0, Math.cosh((+0.0)), 0.0);
        OldAndroidMathTest.assertEquals("Should return 1.0", 1.0, Math.cosh((-0.0)), 0.0);
        OldAndroidMathTest.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.cosh(1234.56), 0.0);
        OldAndroidMathTest.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.cosh((-1234.56)), 0.0);
        OldAndroidMathTest.assertEquals("Should return 1.0000000000005", 1.0000000000005, Math.cosh(1.0E-6), 0.0);
        OldAndroidMathTest.assertEquals("Should return 1.0000000000005", 1.0000000000005, Math.cosh((-1.0E-6)), 0.0);
        OldAndroidMathTest.assertEquals("Should return 5.212214351945598", 5.212214351945598, Math.cosh(2.33482), 0.0);
        OldAndroidMathTest.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.cosh(Double.MAX_VALUE), 0.0);
        OldAndroidMathTest.assertEquals("Should return 1.0", 1.0, Math.cosh(Double.MIN_VALUE), 0.0);
    }

    public void testExpD() {
        // Test for method double java.lang.Math.exp(double)
        TestCase.assertTrue("Incorrect answer returned for simple power", ((Math.abs(((Math.exp(4.0)) - ((((Math.E) * (Math.E)) * (Math.E)) * (Math.E))))) < 0.1));
        TestCase.assertTrue("Incorrect answer returned for larger power", ((Math.log(((Math.abs(Math.exp(5.5))) - 5.5))) < 10.0));
    }

    public void testExpm1D() {
        // Test for special cases
        TestCase.assertTrue("Should return NaN", Double.isNaN(Math.expm1(Double.NaN)));
        OldAndroidMathTest.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.expm1(Double.POSITIVE_INFINITY), 0.0);
        OldAndroidMathTest.assertEquals("Should return -1.0", (-1.0), Math.expm1(Double.NEGATIVE_INFINITY), 0.0);
        TestCase.assertEquals(Double.doubleToLongBits(0.0), Double.doubleToLongBits(Math.expm1(0.0)));
        TestCase.assertEquals(Double.doubleToLongBits((+0.0)), Double.doubleToLongBits(Math.expm1((+0.0))));
        TestCase.assertEquals(Double.doubleToLongBits((-0.0)), Double.doubleToLongBits(Math.expm1((-0.0))));
        OldAndroidMathTest.assertEquals("Should return -9.999950000166666E-6", (-9.999950000166666E-6), Math.expm1((-1.0E-5)), 0.0);
        OldAndroidMathTest.assertEquals("Should return 1.0145103074469635E60", 1.0145103074469635E60, Math.expm1(138.16951162), 0.0);
        OldAndroidMathTest.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.expm1(1.2345678912345679E26), 0.0);
        OldAndroidMathTest.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.expm1(Double.MAX_VALUE), 0.0);
        OldAndroidMathTest.assertEquals("Should return MIN_VALUE", Double.MIN_VALUE, Math.expm1(Double.MIN_VALUE), 0.0);
    }

    public void testFloorD() {
        // Test for method double java.lang.Math.floor(double)
        OldAndroidMathTest.assertEquals("Incorrect floor for double", 78, Math.floor(78.89), 0);
        OldAndroidMathTest.assertEquals("Incorrect floor for double", (-79), Math.floor((-78.89)), 0);
    }

    public void testHypotDD() {
        // Test for special cases
        OldAndroidMathTest.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.hypot(Double.POSITIVE_INFINITY, 1.0), 0.0);
        OldAndroidMathTest.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.hypot(Double.NEGATIVE_INFINITY, 123.324), 0.0);
        OldAndroidMathTest.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.hypot((-758.2587), Double.POSITIVE_INFINITY), 0.0);
        OldAndroidMathTest.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.hypot(5687.21, Double.NEGATIVE_INFINITY), 0.0);
        OldAndroidMathTest.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.hypot(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY), 0.0);
        OldAndroidMathTest.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.hypot(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), 0.0);
        TestCase.assertTrue("Should be NaN", Double.isNaN(Math.hypot(Double.NaN, 2342301.89843)));
        TestCase.assertTrue("Should be NaN", Double.isNaN(Math.hypot((-345.268), Double.NaN)));
        OldAndroidMathTest.assertEquals("Should return 2396424.905416697", 2396424.905416697, Math.hypot(12322.12, (-2396393.2258)), 0.0);
        OldAndroidMathTest.assertEquals("Should return 138.16958070558556", 138.16958070558556, Math.hypot((-138.16951162), 0.13817035864), 0.0);
        OldAndroidMathTest.assertEquals("Should return 1.7976931348623157E308", 1.7976931348623157E308, Math.hypot(Double.MAX_VALUE, 211370.35), 0.0);
        OldAndroidMathTest.assertEquals("Should return 5413.7185", 5413.7185, Math.hypot((-5413.7185), Double.MIN_VALUE), 0.0);
    }

    public void testIEEEremainderDD() {
        // Test for method double java.lang.Math.IEEEremainder(double, double)
        OldAndroidMathTest.assertEquals("Incorrect remainder returned", 0.0, Math.IEEEremainder(1.0, 1.0), 0.0);
        TestCase.assertTrue("Incorrect remainder returned", (((Math.IEEEremainder(1.32, 89.765)) >= 0.014705063220631647) || ((Math.IEEEremainder(1.32, 89.765)) >= 0.01470506322063165)));
    }

    public void testLogD() {
        // Test for method double java.lang.Math.log(double)
        for (double d = 10; d >= (-10); d -= 0.5) {
            double answer = Math.log(Math.exp(d));
            TestCase.assertTrue(((("Answer does not equal expected answer for d = " + d) + " answer = ") + answer), ((Math.abs((answer - d))) <= (Math.abs((d * 1.0E-8)))));
        }
    }

    public void testLog1pD() {
        // Test for special cases
        TestCase.assertTrue("Should return NaN", Double.isNaN(Math.log1p(Double.NaN)));
        TestCase.assertTrue("Should return NaN", Double.isNaN(Math.log1p((-32.0482175))));
        OldAndroidMathTest.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.log1p(Double.POSITIVE_INFINITY), 0.0);
        TestCase.assertEquals(Double.doubleToLongBits(0.0), Double.doubleToLongBits(Math.log1p(0.0)));
        TestCase.assertEquals(Double.doubleToLongBits((+0.0)), Double.doubleToLongBits(Math.log1p((+0.0))));
        TestCase.assertEquals(Double.doubleToLongBits((-0.0)), Double.doubleToLongBits(Math.log1p((-0.0))));
        OldAndroidMathTest.assertEquals("Should return -0.2941782295312541", (-0.2941782295312541), Math.log1p((-0.254856327)), 0.0);
        OldAndroidMathTest.assertEquals("Should return 7.368050685564151", 7.368050685564151, Math.log1p(1583.542), 0.0);
        OldAndroidMathTest.assertEquals("Should return 0.4633708685409921", 0.4633708685409921, Math.log1p(0.5894227), 0.0);
        OldAndroidMathTest.assertEquals("Should return 709.782712893384", 709.782712893384, Math.log1p(Double.MAX_VALUE), 0.0);
        OldAndroidMathTest.assertEquals("Should return Double.MIN_VALUE", Double.MIN_VALUE, Math.log1p(Double.MIN_VALUE), 0.0);
    }

    public void testMaxDD() {
        // Test for method double java.lang.Math.max(double, double)
        OldAndroidMathTest.assertEquals("Incorrect double max value", 1908897.6000089, Math.max((-1908897.6000089), 1908897.6000089), 0.0);
        OldAndroidMathTest.assertEquals("Incorrect double max value", 1908897.6000089, Math.max(2.0, 1908897.6000089), 0.0);
        OldAndroidMathTest.assertEquals("Incorrect double max value", (-2.0), Math.max((-2.0), (-1908897.6000089)), 0.0);
    }

    public void testMaxFF() {
        // Test for method float java.lang.Math.max(float, float)
        TestCase.assertTrue("Incorrect float max value", ((Math.max((-1908897.6F), 1908897.6F)) == 1908897.6F));
        TestCase.assertTrue("Incorrect float max value", ((Math.max(2.0F, 1908897.6F)) == 1908897.6F));
        TestCase.assertTrue("Incorrect float max value", ((Math.max((-2.0F), (-1908897.6F))) == (-2.0F)));
    }

    public void testMaxII() {
        // Test for method int java.lang.Math.max(int, int)
        TestCase.assertEquals("Incorrect int max value", 19088976, Math.max((-19088976), 19088976));
        TestCase.assertEquals("Incorrect int max value", 19088976, Math.max(20, 19088976));
        TestCase.assertEquals("Incorrect int max value", (-20), Math.max((-20), (-19088976)));
    }

    public void testMaxJJ() {
        // Test for method long java.lang.Math.max(long, long)
        TestCase.assertEquals("Incorrect long max value", 19088976000089L, Math.max((-19088976000089L), 19088976000089L));
        TestCase.assertEquals("Incorrect long max value", 19088976000089L, Math.max(20, 19088976000089L));
        TestCase.assertEquals("Incorrect long max value", (-20), Math.max((-20), (-19088976000089L)));
    }

    public void testMinDD() {
        // Test for method double java.lang.Math.min(double, double)
        OldAndroidMathTest.assertEquals("Incorrect double min value", (-1908897.6000089), Math.min((-1908897.6000089), 1908897.6000089), 0.0);
        OldAndroidMathTest.assertEquals("Incorrect double min value", 2.0, Math.min(2.0, 1908897.6000089), 0.0);
        OldAndroidMathTest.assertEquals("Incorrect double min value", (-1908897.6000089), Math.min((-2.0), (-1908897.6000089)), 0.0);
    }

    public void testMinFF() {
        // Test for method float java.lang.Math.min(float, float)
        TestCase.assertTrue("Incorrect float min value", ((Math.min((-1908897.6F), 1908897.6F)) == (-1908897.6F)));
        TestCase.assertTrue("Incorrect float min value", ((Math.min(2.0F, 1908897.6F)) == 2.0F));
        TestCase.assertTrue("Incorrect float min value", ((Math.min((-2.0F), (-1908897.6F))) == (-1908897.6F)));
    }

    public void testMinII() {
        // Test for method int java.lang.Math.min(int, int)
        TestCase.assertEquals("Incorrect int min value", (-19088976), Math.min((-19088976), 19088976));
        TestCase.assertEquals("Incorrect int min value", 20, Math.min(20, 19088976));
        TestCase.assertEquals("Incorrect int min value", (-19088976), Math.min((-20), (-19088976)));
    }

    public void testMinJJ() {
        // Test for method long java.lang.Math.min(long, long)
        TestCase.assertEquals("Incorrect long min value", (-19088976000089L), Math.min((-19088976000089L), 19088976000089L));
        TestCase.assertEquals("Incorrect long min value", 20, Math.min(20, 19088976000089L));
        TestCase.assertEquals("Incorrect long min value", (-19088976000089L), Math.min((-20), (-19088976000089L)));
    }

    public void testPowDD() {
        // Test for method double java.lang.Math.pow(double, double)
        TestCase.assertTrue("pow returned incorrect value", (((long) (Math.pow(2, 8))) == 256L));
        TestCase.assertTrue("pow returned incorrect value", ((Math.pow(2, (-8))) == 0.00390625));
        OldAndroidMathTest.assertEquals("Incorrect root returned1", 2, Math.sqrt(Math.pow(Math.sqrt(2), 4)), 0);
    }

    public void testRintD() {
        // Test for method double java.lang.Math.rint(double)
        OldAndroidMathTest.assertEquals("Failed to round properly - up to odd", 3.0, Math.rint(2.9), 0.0);
        TestCase.assertTrue("Failed to round properly - NaN", Double.isNaN(Math.rint(Double.NaN)));
        OldAndroidMathTest.assertEquals("Failed to round properly down  to even", 2.0, Math.rint(2.1), 0.0);
        TestCase.assertTrue((("Failed to round properly " + 2.5) + " to even"), ((Math.rint(2.5)) == 2.0));
    }

    public void testRoundD() {
        // Test for method long java.lang.Math.round(double)
        TestCase.assertEquals("Incorrect rounding of a float", (-91), Math.round((-90.89)));
    }

    public void testRoundF() {
        // Test for method int java.lang.Math.round(float)
        TestCase.assertEquals("Incorrect rounding of a float", (-91), Math.round((-90.89F)));
    }

    public void testSignumD() {
        TestCase.assertTrue(Double.isNaN(Math.signum(Double.NaN)));
        TestCase.assertTrue(Double.isNaN(Math.signum(Double.NaN)));
        TestCase.assertEquals(Double.doubleToLongBits(0.0), Double.doubleToLongBits(Math.signum(0.0)));
        TestCase.assertEquals(Double.doubleToLongBits((+0.0)), Double.doubleToLongBits(Math.signum((+0.0))));
        TestCase.assertEquals(Double.doubleToLongBits((-0.0)), Double.doubleToLongBits(Math.signum((-0.0))));
        TestCase.assertEquals(1.0, Math.signum(253681.2187962), 0.0);
        TestCase.assertEquals((-1.0), Math.signum((-1.2587469356E8)), 0.0);
        TestCase.assertEquals(1.0, Math.signum(1.2587E-308), 0.0);
        TestCase.assertEquals((-1.0), Math.signum((-1.2587E-308)), 0.0);
        TestCase.assertEquals(1.0, Math.signum(Double.MAX_VALUE), 0.0);
        TestCase.assertEquals(1.0, Math.signum(Double.MIN_VALUE), 0.0);
        TestCase.assertEquals((-1.0), Math.signum((-(Double.MAX_VALUE))), 0.0);
        TestCase.assertEquals((-1.0), Math.signum((-(Double.MIN_VALUE))), 0.0);
        TestCase.assertEquals(1.0, Math.signum(Double.POSITIVE_INFINITY), 0.0);
        TestCase.assertEquals((-1.0), Math.signum(Double.NEGATIVE_INFINITY), 0.0);
    }

    public void testSignumF() {
        TestCase.assertTrue(Float.isNaN(Math.signum(Float.NaN)));
        TestCase.assertEquals(Float.floatToIntBits(0.0F), Float.floatToIntBits(Math.signum(0.0F)));
        TestCase.assertEquals(Float.floatToIntBits((+0.0F)), Float.floatToIntBits(Math.signum((+0.0F))));
        TestCase.assertEquals(Float.floatToIntBits((-0.0F)), Float.floatToIntBits(Math.signum((-0.0F))));
        TestCase.assertEquals(1.0F, Math.signum(253681.22F), 0.0F);
        TestCase.assertEquals((-1.0F), Math.signum((-1.25874696E8F)), 0.0F);
        TestCase.assertEquals(1.0F, Math.signum(1.2587E-11F), 0.0F);
        TestCase.assertEquals((-1.0F), Math.signum((-1.2587E-11F)), 0.0F);
        TestCase.assertEquals(1.0F, Math.signum(Float.MAX_VALUE), 0.0F);
        TestCase.assertEquals(1.0F, Math.signum(Float.MIN_VALUE), 0.0F);
        TestCase.assertEquals((-1.0F), Math.signum((-(Float.MAX_VALUE))), 0.0F);
        TestCase.assertEquals((-1.0F), Math.signum((-(Float.MIN_VALUE))), 0.0F);
        TestCase.assertEquals(1.0F, Math.signum(Float.POSITIVE_INFINITY), 0.0F);
        TestCase.assertEquals((-1.0F), Math.signum(Float.NEGATIVE_INFINITY), 0.0F);
    }

    public void testSinD() {
        // Test for method double java.lang.Math.sin(double)
        OldAndroidMathTest.assertEquals("Incorrect answer", 0.0, Math.sin(0), 0.0);
        OldAndroidMathTest.assertEquals("Incorrect answer", 0.8414709848078965, Math.sin(1), 0.0);
    }

    public void testSinhD() {
        // Test for special situations
        TestCase.assertTrue("Should return NaN", Double.isNaN(Math.sinh(Double.NaN)));
        OldAndroidMathTest.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.sinh(Double.POSITIVE_INFINITY), 0.0);
        OldAndroidMathTest.assertEquals("Should return NEGATIVE_INFINITY", Double.NEGATIVE_INFINITY, Math.sinh(Double.NEGATIVE_INFINITY), 0.0);
        TestCase.assertEquals(Double.doubleToLongBits(0.0), Double.doubleToLongBits(Math.sinh(0.0)));
        TestCase.assertEquals(Double.doubleToLongBits((+0.0)), Double.doubleToLongBits(Math.sinh((+0.0))));
        TestCase.assertEquals(Double.doubleToLongBits((-0.0)), Double.doubleToLongBits(Math.sinh((-0.0))));
        OldAndroidMathTest.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.sinh(1234.56), 0.0);
        OldAndroidMathTest.assertEquals("Should return NEGATIVE_INFINITY", Double.NEGATIVE_INFINITY, Math.sinh((-1234.56)), 0.0);
        OldAndroidMathTest.assertEquals("Should return 1.0000000000001666E-6", 1.0000000000001666E-6, Math.sinh(1.0E-6), 0.0);
        OldAndroidMathTest.assertEquals("Should return -1.0000000000001666E-6", (-1.0000000000001666E-6), Math.sinh((-1.0E-6)), 0.0);
        OldAndroidMathTest.assertEquals("Should return 5.115386441963859", 5.115386441963859, Math.sinh(2.33482), 0.0);
        OldAndroidMathTest.assertEquals("Should return POSITIVE_INFINITY", Double.POSITIVE_INFINITY, Math.sinh(Double.MAX_VALUE), 0.0);
        OldAndroidMathTest.assertEquals("Should return 4.9E-324", 4.9E-324, Math.sinh(Double.MIN_VALUE), 0.0);
    }

    public void testSqrtD() {
        // Test for method double java.lang.Math.sqrt(double)
        OldAndroidMathTest.assertEquals("Incorrect root returned2", 7, Math.sqrt(49), 0);
    }

    public void testTanD() {
        // Test for method double java.lang.Math.tan(double)
        OldAndroidMathTest.assertEquals("Incorrect answer", 0.0, Math.tan(0), 0.0);
        OldAndroidMathTest.assertEquals("Incorrect answer", 1.5574077246549023, Math.tan(1), 0.0);
    }

    public void testTanhD() {
        // Test for special situations
        TestCase.assertTrue("Should return NaN", Double.isNaN(Math.tanh(Double.NaN)));
        OldAndroidMathTest.assertEquals("Should return +1.0", (+1.0), Math.tanh(Double.POSITIVE_INFINITY), 0.0);
        OldAndroidMathTest.assertEquals("Should return -1.0", (-1.0), Math.tanh(Double.NEGATIVE_INFINITY), 0.0);
        TestCase.assertEquals(Double.doubleToLongBits(0.0), Double.doubleToLongBits(Math.tanh(0.0)));
        TestCase.assertEquals(Double.doubleToLongBits((+0.0)), Double.doubleToLongBits(Math.tanh((+0.0))));
        TestCase.assertEquals(Double.doubleToLongBits((-0.0)), Double.doubleToLongBits(Math.tanh((-0.0))));
        OldAndroidMathTest.assertEquals("Should return 1.0", 1.0, Math.tanh(1234.56), 0.0);
        OldAndroidMathTest.assertEquals("Should return -1.0", (-1.0), Math.tanh((-1234.56)), 0.0);
        OldAndroidMathTest.assertEquals("Should return 9.999999999996666E-7", 9.999999999996666E-7, Math.tanh(1.0E-6), 0.0);
        OldAndroidMathTest.assertEquals("Should return 0.981422884124941", 0.981422884124941, Math.tanh(2.33482), 0.0);
        OldAndroidMathTest.assertEquals("Should return 1.0", 1.0, Math.tanh(Double.MAX_VALUE), 0.0);
        OldAndroidMathTest.assertEquals("Should return 4.9E-324", 4.9E-324, Math.tanh(Double.MIN_VALUE), 0.0);
    }

    public void testRandom() {
        // There isn't a place for these tests so just stick them here
        TestCase.assertEquals("Wrong value E", 4613303445314885481L, Double.doubleToLongBits(Math.E));
        TestCase.assertEquals("Wrong value PI", 4614256656552045848L, Double.doubleToLongBits(Math.PI));
        for (int i = 500; i >= 0; i--) {
            double d = Math.random();
            TestCase.assertTrue(("Generated number is out of range: " + d), ((d >= 0.0) && (d < 1.0)));
        }
    }

    public void testToRadiansD() {
        for (double d = 500; d >= 0; d -= 1.0) {
            double converted = Math.toDegrees(Math.toRadians(d));
            TestCase.assertTrue(("Converted number not equal to original. d = " + d), ((converted >= (d * 0.99999999)) && (converted <= (d * 1.00000001))));
        }
    }

    public void testToDegreesD() {
        for (double d = 500; d >= 0; d -= 1.0) {
            double converted = Math.toRadians(Math.toDegrees(d));
            TestCase.assertTrue(("Converted number not equal to original. d = " + d), ((converted >= (d * 0.99999999)) && (converted <= (d * 1.00000001))));
        }
    }
}

