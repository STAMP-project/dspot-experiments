/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.harmony.tests.java.lang;


import junit.framework.TestCase;


public class FloatTest extends TestCase {
    private static final int[] rawBitsFor3_4eN38To38 = new int[]{ 20530288, 48731532, 76594551, 104131797, 132261899, 160258311, 187739976, 215800602, 243927280, 271354669, 299347448, 327601339, 354975721, 382902244, 411280366, 438602986, 466464805, 494964247, 522236317, 550034948, 578491781, 605875571, 633612496, 661992195, 689520610, 717197275, 745501649, 773171299, 800789116, 829019930, 856827505, 884387853, 912546832, 940489098, 967993324, 996082152, 1024155953, 1051605373, 1079625692, 1107827946, 1135223844, 1163177261, 1191504956, 1218848587, 1246736670, 1275186867, 1302479455, 1330303735, 1358792629, 1386116305, 1413878277, 1442289287, 1469758996, 1497460121, 1525795072, 1553407392, 1581049096, 1609309770, 1637061358, 1664645033, 1692833172, 1720720764, 1748247772, 1776365074, 1804385483, 1831857150, 1859905278, 1888055391, 1915473014, 1943453588, 1971730364, 1999095212, 2027009815, 2055410286, 2082723594, 2110573772, 2139095039 };

    private static final String[] expectedStringFor3_4eN38To38 = new String[]{ "3.4028235E-38", "3.4028235E-37", "3.4028233E-36", "3.4028234E-35", "3.4028236E-34", "3.4028236E-33", "3.4028234E-32", "3.4028234E-31", "3.4028233E-30", "3.4028236E-29", "3.4028235E-28", "3.4028235E-27", "3.4028233E-26", "3.4028235E-25", "3.4028233E-24", "3.4028235E-23", "3.4028236E-22", "3.4028235E-21", "3.4028236E-20", "3.4028236E-19", "3.4028236E-18", "3.4028235E-17", "3.4028236E-16", "3.4028234E-15", "3.4028234E-14", "3.4028235E-13", "3.4028234E-12", "3.4028235E-11", "3.4028236E-10", "3.4028234E-9", "3.4028236E-8", "3.4028236E-7", "3.4028235E-6", "3.4028235E-5", "3.4028233E-4", "0.0034028236", "0.034028236", "0.34028235", "3.4028234", "34.028236", "340.28235", "3402.8235", "34028.234", "340282.34", "3402823.5", "3.4028236E7", "3.40282336E8", "3.40282342E9", "3.40282348E10", "3.40282343E11", "3.40282337E12", "3.40282353E13", "3.4028234E14", "3.4028234E15", "3.40282356E16", "3.40282356E17", "3.40282356E18", "3.4028236E19", "3.4028235E20", "3.4028233E21", "3.4028235E22", "3.4028233E23", "3.4028236E24", "3.4028234E25", "3.4028233E26", "3.4028234E27", "3.4028235E28", "3.4028236E29", "3.4028233E30", "3.4028235E31", "3.4028233E32", "3.4028236E33", "3.4028236E34", "3.4028234E35", "3.4028236E36", "3.4028235E37", "3.4028235E38" };

    private static final int[] rawBitsFor1_17eN38To38 = new int[]{ -2139095040, -2111832064, -2084044800, -2055602176, -2028191744, -2000465920, -1972100096, -1944545664, -1916879840, -1888589016, -1860893959, -1833286729, -1805069147, -1777236761, -1749686751, -1721540695, -1693574198, -1666080068, -1638003861, -1609906397, -1582466836, -1554458841, -1526233480, -1498847210, -1470905828, -1442555567, -1415221338, -1387345009, -1358872775, -1331589368, -1303776566, -1275302020, -1247951442, -1220200679, -1191803681, -1164307700, -1136617522, -1108296254, -1080658279, -1053027264, -1024779952, -997003310, -969430074, -941254984, -913342925, -885826112, -857721553, -829677250, -802215539, -774179856, -746006410, -718598508, -690630087, -662330525, -634975172, -607072437, -578649713, -551345677, -523507088, -495000309, -467710169, -439934223, -411505747, -384068788, -356354017, -328002009, -300421672, -272766641, -244489310, -216768955, -189172265, -160967860, -133110768, -105571052, -77437863, -49447241, -21963163 };

    private static final String[] expectedStringFor1_17eN38To38 = new String[]{ "-1.17549435E-38", "-1.1754944E-37", "-1.17549435E-36", "-1.17549435E-35", "-1.1754944E-34", "-1.17549435E-33", "-1.17549435E-32", "-1.1754944E-31", "-1.17549435E-30", "-1.17549435E-29", "-1.1754944E-28", "-1.1754943E-27", "-1.17549435E-26", "-1.1754943E-25", "-1.1754944E-24", "-1.1754943E-23", "-1.1754944E-22", "-1.1754943E-21", "-1.1754943E-20", "-1.1754943E-19", "-1.1754944E-18", "-1.1754944E-17", "-1.1754943E-16", "-1.1754943E-15", "-1.1754944E-14", "-1.1754943E-13", "-1.1754944E-12", "-1.1754943E-11", "-1.1754943E-10", "-1.1754944E-9", "-1.1754944E-8", "-1.1754943E-7", "-1.1754944E-6", "-1.1754943E-5", "-1.1754943E-4", "-0.0011754944", "-0.011754943", "-0.117549434", "-1.1754943", "-11.754944", "-117.54944", "-1175.4944", "-11754.943", "-117549.44", "-1175494.4", "-1.1754944E7", "-1.17549432E8", "-1.1754944E9", "-1.17549435E10", "-1.17549433E11", "-1.17549433E12", "-1.17549438E13", "-1.17549438E14", "-1.1754943E15", "-1.17549432E16", "-1.17549432E17", "-1.17549434E18", "-1.1754944E19", "-1.1754944E20", "-1.1754943E21", "-1.1754943E22", "-1.1754944E23", "-1.17549434E24", "-1.1754943E25", "-1.1754943E26", "-1.17549434E27", "-1.1754943E28", "-1.1754944E29", "-1.1754943E30", "-1.1754943E31", "-1.1754944E32", "-1.1754943E33", "-1.1754944E34", "-1.1754944E35", "-1.1754944E36", "-1.1754943E37", "-1.1754943E38" };

    /**
     * java.lang.Float#Float(float)
     */
    public void test_ConstructorF() {
        // Test for method java.lang.Float(float)
        Float f = new Float(900.89F);
        TestCase.assertTrue("Created incorrect float", ((f.floatValue()) == 900.89F));
    }

    /**
     * java.lang.Float#Float(java.lang.String)
     */
    public void test_ConstructorLjava_lang_String() {
        // Test for method java.lang.Float(java.lang.String)
        Float f = new Float("900.89");
        TestCase.assertTrue("Created incorrect Float", ((f.floatValue()) == 900.89F));
    }

    /**
     * java.lang.Float#byteValue()
     */
    public void test_byteValue() {
        // Test for method byte java.lang.Float.byteValue()
        Float f = new Float(0.46874F);
        Float f2 = new Float(90.8F);
        TestCase.assertTrue("Returned incorrect byte value", (((f.byteValue()) == 0) && ((f2.byteValue()) == 90)));
    }

    /**
     * java.lang.Float#compareTo(java.lang.Float)
     * java.lang.Float#compare(float, float)
     */
    public void test_compare() {
        if (System.getProperty("os.arch").equals("armv7")) {
            return;
        }
        float[] values = new float[]{ Float.NEGATIVE_INFINITY, -(Float.MAX_VALUE), -2.0F, -(Float.MIN_VALUE), -0.0F, 0.0F, Float.MIN_VALUE, 2.0F, Float.MAX_VALUE, Float.POSITIVE_INFINITY, Float.NaN };
        for (int i = 0; i < (values.length); i++) {
            float f1 = values[i];
            TestCase.assertTrue(("compare() should be equal: " + f1), ((Float.compare(f1, f1)) == 0));
            Float F1 = new Float(f1);
            TestCase.assertTrue(("compareTo() should be equal: " + f1), ((F1.compareTo(F1)) == 0));
            for (int j = i + 1; j < (values.length); j++) {
                float f2 = values[j];
                TestCase.assertTrue(((("compare() " + f1) + " should be less ") + f2), ((Float.compare(f1, f2)) == (-1)));
                TestCase.assertTrue(((("compare() " + f2) + " should be greater ") + f1), ((Float.compare(f2, f1)) == 1));
                Float F2 = new Float(f2);
                TestCase.assertTrue(((("compareTo() " + f1) + " should be less ") + f2), ((F1.compareTo(F2)) == (-1)));
                TestCase.assertTrue(((("compareTo() " + f2) + " should be greater ") + f1), ((F2.compareTo(F1)) == 1));
            }
        }
        try {
            new Float(0.0F).compareTo(null);
            TestCase.fail("No NPE");
        } catch (NullPointerException e) {
        }
    }

    /**
     * java.lang.Float#doubleValue()
     */
    public void test_doubleValue() {
        // Test for method double java.lang.Float.doubleValue()
        TestCase.assertTrue("Incorrect double value returned", ((Math.abs(((new Float(1000000.0F).doubleValue()) - 999999.999))) < 1));
    }

    /**
     * java.lang.Float#floatToIntBits(float)
     */
    public void test_floatToIntBitsF() {
        float f = 9876.234F;
        int bits = Float.floatToIntBits(f);
        float r = Float.intBitsToFloat(bits);
        TestCase.assertTrue("Incorrect intBits returned", (f == r));
    }

    /**
     * java.lang.Float#floatToRawIntBits(float)
     */
    public void test_floatToRawIntBitsF() {
        int i = 2143290578;
        float f = Float.intBitsToFloat(i);
        TestCase.assertTrue("Wrong raw bits", ((Float.floatToRawIntBits(f)) == i));
    }

    /**
     * java.lang.Float#floatValue()
     */
    public void test_floatValue() {
        // Test for method float java.lang.Float.floatValue()
        Float f = new Float(87.657F);
        Float f2 = new Float((-0.876F));
        TestCase.assertTrue("Returned incorrect floatValue", (((f.floatValue()) == 87.657F) && ((f2.floatValue()) == (-0.876F))));
    }

    /**
     * java.lang.Float#hashCode()
     */
    public void test_hashCode() {
        // Test for method int java.lang.Float.hashCode()
        Float f = new Float(1908.8785F);
        TestCase.assertTrue("Returned invalid hash code for 1908.8786f", ((f.hashCode()) == (Float.floatToIntBits(1908.8785F))));
        f = new Float((-1.112F));
        TestCase.assertTrue("Returned invalid hash code for -1.112", ((f.hashCode()) == (Float.floatToIntBits((-1.112F)))));
        f = new Float(0.0F);
        TestCase.assertTrue("Returned invalid hash code for 0", ((f.hashCode()) == (Float.floatToIntBits(0.0F))));
    }

    /**
     * java.lang.Float#intBitsToFloat(int)
     */
    public void test_intBitsToFloatI() {
        float f = 9876.234F;
        int bits = Float.floatToIntBits(f);
        float r = Float.intBitsToFloat(bits);
        TestCase.assertEquals("Incorrect intBits returned", f, r, 0.0F);
    }

    /**
     * java.lang.Float#intValue()
     */
    public void test_intValue() {
        // Test for method int java.lang.Float.intValue()
        Float f = new Float(0.46874F);
        Float f2 = new Float(90.8F);
        TestCase.assertTrue("Returned incorrect int value", (((f.intValue()) == 0) && ((f2.intValue()) == 90)));
    }

    /**
     * java.lang.Float#isInfinite()
     */
    public void test_isInfinite() {
        // Test for method boolean java.lang.Float.isInfinite()
        TestCase.assertTrue("Infinity check failed", (((new Float(Float.POSITIVE_INFINITY).isInfinite()) && (new Float(Float.NEGATIVE_INFINITY).isInfinite())) && (!(new Float(0.13131414F).isInfinite()))));
    }

    /**
     * java.lang.Float#isInfinite(float)
     */
    public void test_isInfiniteF() {
        // Test for method boolean java.lang.Float.isInfinite(float)
        TestCase.assertTrue(Float.isInfinite(Float.POSITIVE_INFINITY));
        TestCase.assertTrue(Float.isInfinite(Float.NEGATIVE_INFINITY));
        TestCase.assertFalse(Float.isInfinite(Float.MAX_VALUE));
        TestCase.assertFalse(Float.isInfinite(Float.MIN_VALUE));
        TestCase.assertFalse(Float.isInfinite(Float.NaN));
        TestCase.assertFalse(Float.isInfinite(1.0F));
    }

    /**
     * java.lang.Float#isFinite(float)
     */
    public void test_isFiniteF() {
        // Test for method boolean java.lang.Float.isInfinite(float)
        TestCase.assertFalse(Float.isFinite(Float.POSITIVE_INFINITY));
        TestCase.assertFalse(Float.isFinite(Float.NEGATIVE_INFINITY));
        TestCase.assertTrue(Float.isFinite(Float.MAX_VALUE));
        TestCase.assertTrue(Float.isFinite(Float.MIN_VALUE));
        TestCase.assertFalse(Float.isFinite(Float.NaN));
        TestCase.assertTrue(Float.isFinite(1.0F));
    }

    /**
     * java.lang.Float#isNaN()
     */
    public void test_isNaN() {
        // Test for method boolean java.lang.Float.isNaN()
        TestCase.assertTrue("NAN check failed", ((new Float(Float.NaN).isNaN()) && (!(new Float(1.0F).isNaN()))));
    }

    /**
     * java.lang.Float#isNaN(float)
     */
    public void test_isNaNF() {
        // Test for method boolean java.lang.Float.isNaN(float)
        TestCase.assertTrue("NaN check failed", ((Float.isNaN(Float.NaN)) && (!(Float.isNaN(12.09F)))));
    }

    /**
     * java.lang.Float#longValue()
     */
    public void test_longValue() {
        // Test for method long java.lang.Float.longValue()
        Float f = new Float(0.46874F);
        Float f2 = new Float(90.8F);
        TestCase.assertTrue("Returned incorrect long value", (((f.longValue()) == 0) && ((f2.longValue()) == 90)));
    }

    /**
     * java.lang.Float#parseFloat(java.lang.String)
     */
    public void test_parseFloatLjava_lang_String() {
        if (System.getProperty("os.arch").equals("armv7")) {
            return;
        }
        TestCase.assertEquals("Incorrect float returned, expected zero.", 0.0, Float.parseFloat("7.0064923216240853546186479164495e-46"), 0.0);
        TestCase.assertEquals("Incorrect float returned, expected minimum float.", Float.MIN_VALUE, Float.parseFloat("7.0064923216240853546186479164496e-46"), 0.0);
        doTestCompareRawBits("0.000000000000000000000000000000000000011754942807573642917278829910357665133228589927589904276829631184250030649651730385585324256680905818939208984375", 8388608, "1.17549435E-38");
        doTestCompareRawBits("0.00000000000000000000000000000000000001175494280757364291727882991035766513322858992758990427682963118425003064965173038558532425668090581893920898437499999f", 8388607, "1.1754942E-38");
        /* Test a set of regular floats with exponents from -38 to +38 */
        for (int i = 38; i > 3; i--) {
            String testString;
            testString = "3.4028234663852886e-" + i;
            doTestCompareRawBits(testString, FloatTest.rawBitsFor3_4eN38To38[(38 - i)], FloatTest.expectedStringFor3_4eN38To38[(38 - i)]);
        }
        doTestCompareRawBits("3.4028234663852886e-3", FloatTest.rawBitsFor3_4eN38To38[(38 - 3)], FloatTest.expectedStringFor3_4eN38To38[(38 - 3)]);
        doTestCompareRawBits("3.4028234663852886e-2", FloatTest.rawBitsFor3_4eN38To38[(38 - 2)], FloatTest.expectedStringFor3_4eN38To38[(38 - 2)]);
        doTestCompareRawBits("3.4028234663852886e-1", FloatTest.rawBitsFor3_4eN38To38[(38 - 1)], FloatTest.expectedStringFor3_4eN38To38[(38 - 1)]);
        doTestCompareRawBits("3.4028234663852886e-0", FloatTest.rawBitsFor3_4eN38To38[(38 - 0)], FloatTest.expectedStringFor3_4eN38To38[(38 - 0)]);
        doTestCompareRawBits("3.4028234663852886e+1", FloatTest.rawBitsFor3_4eN38To38[(38 + 1)], FloatTest.expectedStringFor3_4eN38To38[(38 + 1)]);
        doTestCompareRawBits("3.4028234663852886e+2", FloatTest.rawBitsFor3_4eN38To38[(38 + 2)], FloatTest.expectedStringFor3_4eN38To38[(38 + 2)]);
        doTestCompareRawBits("3.4028234663852886e+3", FloatTest.rawBitsFor3_4eN38To38[(38 + 3)], FloatTest.expectedStringFor3_4eN38To38[(38 + 3)]);
        doTestCompareRawBits("3.4028234663852886e+4", FloatTest.rawBitsFor3_4eN38To38[(38 + 4)], FloatTest.expectedStringFor3_4eN38To38[(38 + 4)]);
        doTestCompareRawBits("3.4028234663852886e+5", FloatTest.rawBitsFor3_4eN38To38[(38 + 5)], FloatTest.expectedStringFor3_4eN38To38[(38 + 5)]);
        doTestCompareRawBits("3.4028234663852886e+6", FloatTest.rawBitsFor3_4eN38To38[(38 + 6)], FloatTest.expectedStringFor3_4eN38To38[(38 + 6)]);
        for (int i = 7; i < 39; i++) {
            String testString;
            testString = "3.4028234663852886e+" + i;
            doTestCompareRawBits(testString, FloatTest.rawBitsFor3_4eN38To38[(38 + i)], FloatTest.expectedStringFor3_4eN38To38[(38 + i)]);
        }
        /* Test another set of regular floats with exponents from -38 to +38 */
        for (int i = 38; i > 3; i--) {
            String testString;
            testString = "-1.1754943508222875e-" + i;
            doTestCompareRawBits(testString, FloatTest.rawBitsFor1_17eN38To38[(38 - i)], FloatTest.expectedStringFor1_17eN38To38[(38 - i)]);
        }
        doTestCompareRawBits("-1.1754943508222875e-3", FloatTest.rawBitsFor1_17eN38To38[(38 - 3)], FloatTest.expectedStringFor1_17eN38To38[(38 - 3)]);
        doTestCompareRawBits("-1.1754943508222875e-2", FloatTest.rawBitsFor1_17eN38To38[(38 - 2)], FloatTest.expectedStringFor1_17eN38To38[(38 - 2)]);
        doTestCompareRawBits("-1.1754943508222875e-1", FloatTest.rawBitsFor1_17eN38To38[(38 - 1)], FloatTest.expectedStringFor1_17eN38To38[(38 - 1)]);
        doTestCompareRawBits("-1.1754943508222875e-0", FloatTest.rawBitsFor1_17eN38To38[(38 - 0)], FloatTest.expectedStringFor1_17eN38To38[(38 - 0)]);
        doTestCompareRawBits("-1.1754943508222875e+1", FloatTest.rawBitsFor1_17eN38To38[(38 + 1)], FloatTest.expectedStringFor1_17eN38To38[(38 + 1)]);
        doTestCompareRawBits("-1.1754943508222875e+2", FloatTest.rawBitsFor1_17eN38To38[(38 + 2)], FloatTest.expectedStringFor1_17eN38To38[(38 + 2)]);
        doTestCompareRawBits("-1.1754943508222875e+3", FloatTest.rawBitsFor1_17eN38To38[(38 + 3)], FloatTest.expectedStringFor1_17eN38To38[(38 + 3)]);
        doTestCompareRawBits("-1.1754943508222875e+4", FloatTest.rawBitsFor1_17eN38To38[(38 + 4)], FloatTest.expectedStringFor1_17eN38To38[(38 + 4)]);
        doTestCompareRawBits("-1.1754943508222875e+5", FloatTest.rawBitsFor1_17eN38To38[(38 + 5)], FloatTest.expectedStringFor1_17eN38To38[(38 + 5)]);
        doTestCompareRawBits("-1.1754943508222875e+6", FloatTest.rawBitsFor1_17eN38To38[(38 + 6)], FloatTest.expectedStringFor1_17eN38To38[(38 + 6)]);
        for (int i = 7; i < 39; i++) {
            String testString;
            testString = "-1.1754943508222875e+" + i;
            doTestCompareRawBits(testString, FloatTest.rawBitsFor1_17eN38To38[(38 + i)], FloatTest.expectedStringFor1_17eN38To38[(38 + i)]);
        }
        /* Test denormalized floats (floats with exponents <= -38 */
        doTestCompareRawBits("1.1012984643248170E-45", 1, "1.4E-45");
        doTestCompareRawBits("-1.1012984643248170E-45", -2147483647, "-1.4E-45");
        doTestCompareRawBits("1.0E-45", 1, "1.4E-45");
        doTestCompareRawBits("-1.0E-45", -2147483647, "-1.4E-45");
        doTestCompareRawBits("0.9E-45", 1, "1.4E-45");
        doTestCompareRawBits("-0.9E-45", -2147483647, "-1.4E-45");
        doTestCompareRawBits("4.203895392974451e-45", 3, "4.2E-45");
        doTestCompareRawBits("-4.203895392974451e-45", -2147483645, "-4.2E-45");
        doTestCompareRawBits("0.004E-45", 0, "0.0");
        doTestCompareRawBits("-0.004E-45", -2147483648, "-0.0");
        /* Test for large floats close to and greater than 3.4028235E38 and
        -3.4028235E38
         */
        doTestCompareRawBits("1.2E+38", 2125762130, "1.2E38");
        doTestCompareRawBits("-1.2E+38", -21721518, "-1.2E38");
        doTestCompareRawBits("3.2E+38", 2138095042, "3.2E38");
        doTestCompareRawBits("-3.2E+38", -9388606, "-3.2E38");
        doTestCompareRawBits("3.4E+38", 2139081118, "3.4E38");
        doTestCompareRawBits("-3.4E+38", -8402530, "-3.4E38");
        doTestCompareRawBits("3.4028234663852886E+38", 2139095039, "3.4028235E38");
        doTestCompareRawBits("-3.4028234663852886E+38", -8388609, "-3.4028235E38");
        doTestCompareRawBits("3.405E+38", 2139095040, "Infinity");
        doTestCompareRawBits("-3.405E+38", -8388608, "-Infinity");
        doTestCompareRawBits("3.41E+38", 2139095040, "Infinity");
        doTestCompareRawBits("-3.41E+38", -8388608, "-Infinity");
        doTestCompareRawBits("3.42E+38", 2139095040, "Infinity");
        doTestCompareRawBits("-3.42E+38", -8388608, "-Infinity");
        doTestCompareRawBits("1.0E+39", 2139095040, "Infinity");
        doTestCompareRawBits("-1.0E+39", -8388608, "-Infinity");
    }

    /**
     * java.lang.Float#parseFloat(java.lang.String)
     */
    public void test_parseFloat_LString_Unusual() {
        float actual;
        actual = Float.parseFloat("0x00000000000000000000000000000000000000000.0000000000000000000000000000000000000p0000000000000000000000000000000000");
        TestCase.assertEquals("Returned incorrect value", 0.0F, actual, 0.0F);
        actual = Float.parseFloat("+0Xfffff.fffffffffffffffffffffffffffffffp+99F");
        TestCase.assertEquals("Returned incorrect value", 6.64614E35F, actual, 0.0F);
        actual = Float.parseFloat("-0X.123456789abcdefp+99f");
        TestCase.assertEquals("Returned incorrect value", (-4.5072022E28F), actual, 0.0F);
        actual = Float.parseFloat("-0X123456789abcdef.p+1f");
        TestCase.assertEquals("Returned incorrect value", (-1.63971062E17F), actual, 0.0F);
        actual = Float.parseFloat("-0X000000000000000000000000000001abcdef.0000000000000000000000000001abefp00000000000000000000000000000000000000000004f");
        TestCase.assertEquals("Returned incorrect value", (-4.48585472E8F), actual, 0.0F);
        actual = Float.parseFloat("0X0.00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001234p600f");
        TestCase.assertEquals("Returned incorrect value", 5.907252E33F, actual, 0.0F);
        actual = Float.parseFloat("0x1.p9223372036854775807");
        TestCase.assertEquals("Returned incorrect value", Float.POSITIVE_INFINITY, actual, 0.0F);
        actual = Float.parseFloat("0x1.p9223372036854775808");
        TestCase.assertEquals("Returned incorrect value", Float.POSITIVE_INFINITY, actual, 0.0F);
        actual = Float.parseFloat("0x10.p9223372036854775808");
        TestCase.assertEquals("Returned incorrect value", Float.POSITIVE_INFINITY, actual, 0.0F);
        actual = Float.parseFloat("0xabcd.ffffffffp+2000");
        TestCase.assertEquals("Returned incorrect value", Float.POSITIVE_INFINITY, actual, 0.0F);
        actual = Float.parseFloat("0x1.p-9223372036854775808");
        TestCase.assertEquals("Returned incorrect value", 0.0F, actual, 0.0F);
        actual = Float.parseFloat("0x1.p-9223372036854775809");
        TestCase.assertEquals("Returned incorrect value", 0.0F, actual, 0.0F);
        actual = Float.parseFloat("0x.1p-9223372036854775809");
        TestCase.assertEquals("Returned incorrect value", 0.0F, actual, 0.0F);
    }

    /**
     * java.lang.Float#parseFloat(java.lang.String)
     */
    public void test_parseFloat_LString_NormalPositiveExponent() {
        int[] expecteds = new int[]{ 965845684, 1137443399, 1200656393, 1254146057, 1309708293, 1363197957, 1415108613, 1468302338, 1519423490, 1571334146, 1623244802, 1674365954, 1726276610, 1777397762, 1829012481, 1880133633, 1930860033, 1988167696, 2038696000, 2089420960, 2139095040, 2139095040, 2139095040, 2139095040, 2139095040 };
        for (int i = 0; i < (expecteds.length); i++) {
            int part = i * 6;
            String inputString = (((("0x" + part) + ".") + part) + "0123456789abcdefp") + part;
            float actual = Float.parseFloat(inputString);
            float expected = Float.intBitsToFloat(expecteds[i]);
            String expectedString = Integer.toHexString(Float.floatToIntBits(expected));
            String actualString = Integer.toHexString(Float.floatToIntBits(actual));
            String errorMsg = ((((((i + "th input string is:<") + inputString) + ">.The expected result should be:<") + expectedString) + ">, but was: <") + actualString) + ">. ";
            TestCase.assertEquals(errorMsg, expected, actual, 0.0F);
        }
    }

    /**
     * java.lang.Float#parseFloat(java.lang.String)
     */
    public void test_parseFloat_LString_NormalNegativeExponent() {
        int[] expecteds = new int[]{ 965845684, 1030619719, 983605257, 931431429, 874553349, 819254277, 763659266, 705860098, 648850434, 591840770, 534831106, 477031938, 419726337, 361861377, 303601665, 251824168, 193530000, 135039176, 76745008, 18450840, 163880, 1308, 11, 0, 0 };
        for (int i = 0; i < (expecteds.length); i++) {
            int part = i * 7;
            String inputString = (((("0x" + part) + ".") + part) + "0123456789abcdefp-") + part;
            float actual = Float.parseFloat(inputString);
            float expected = Float.intBitsToFloat(expecteds[i]);
            String expectedString = Integer.toHexString(Float.floatToIntBits(expected));
            String actualString = Integer.toHexString(Float.floatToIntBits(actual));
            String errorMsg = ((((((i + "th input string is:<") + inputString) + ">.The expected result should be:<") + expectedString) + ">, but was: <") + actualString) + ">. ";
            TestCase.assertEquals(errorMsg, expected, actual, 0.0F);
        }
    }

    /**
     * java.lang.Float#parseFloat(java.lang.String)
     */
    public void test_parseFloat_LString_MaxNormalBoundary() {
        int[] expecteds = new int[]{ 2139095039, 2139095039, 2139095039, 2139095040, 2139095040, 2139095040, -8388609, -8388609, -8388609, -8388608, -8388608, -8388608 };
        String[] inputs = new String[]{ "0x1.fffffep127", "0x1.fffffe000000000000000000000000000000000000000000000001p127", "0x1.fffffeffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffp127", "0x1.ffffffp127", "0x1.ffffff000000000000000000000000000000000000000000000001p127", "0x1.ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffp127", "-0x1.fffffep127", "-0x1.fffffe000000000000000000000000000000000000000000000001p127", "-0x1.fffffeffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffp127", "-0x1.ffffffp127", "-0x1.ffffff000000000000000000000000000000000000000000000001p127", "-0x1.ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffp127" };
        for (int i = 0; i < (inputs.length); i++) {
            float actual = Float.parseFloat(inputs[i]);
            float expected = Float.intBitsToFloat(expecteds[i]);
            String expectedString = Integer.toHexString(Float.floatToIntBits(expected));
            String actualString = Integer.toHexString(Float.floatToIntBits(actual));
            String errorMsg = ((((((i + "th input string is:<") + (inputs[i])) + ">.The expected result should be:<") + expectedString) + ">, but was: <") + actualString) + ">. ";
            TestCase.assertEquals(errorMsg, expected, actual, 0.0F);
        }
    }

    /**
     * java.lang.Float#parseFloat(java.lang.String)
     */
    public void test_parseFloat_LString_MinNormalBoundary() {
        int[] expecteds = new int[]{ 8388608, 8388608, 8388608, 8388608, 8388609, 8388609, -2139095040, -2139095040, -2139095040, -2139095040, -2139095039, -2139095039 };
        String[] inputs = new String[]{ "0x1.0p-126", "0x1.00000000000000000000000000000000000000000000001p-126", "0x1.000000ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffp-126", "0x1.000001p-126", "0x1.000001000000000000000000000000000000000000000001p-126", "0x1.000001fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffp-126", "-0x1.0p-126", "-0x1.00000000000000000000000000000000000000000000001p-126", "-0x1.000000ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffp-126", "-0x1.000001p-126", "-0x1.000001000000000000000000000000000000000000000001p-126", "-0x1.000001fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffp-126" };
        for (int i = 0; i < (inputs.length); i++) {
            float actual = Float.parseFloat(inputs[i]);
            float expected = Float.intBitsToFloat(expecteds[i]);
            String expectedString = Integer.toHexString(Float.floatToIntBits(expected));
            String actualString = Integer.toHexString(Float.floatToIntBits(actual));
            String errorMsg = ((((((i + "th input string is:<") + (inputs[i])) + ">.The expected result should be:<") + expectedString) + ">, but was: <") + actualString) + ">. ";
            TestCase.assertEquals(errorMsg, expected, actual, 0.0F);
        }
    }

    /**
     * java.lang.Float#parseFloat(java.lang.String)
     */
    public void test_parseFloat_LString_MaxSubNormalBoundary() {
        int[] expecteds = new int[]{ 8388607, 8388607, 8388607, 8388608, 8388608, 8388608, -2139095041, -2139095041, -2139095041, -2139095040, -2139095040, -2139095040 };
        String[] inputs = new String[]{ "0x0.fffffep-126", "0x0.fffffe000000000000000000000000000000000000000000000000000001p-126", "0x0.fffffefffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffp-126", "0x0.ffffffp-126", "0x0.ffffff0000000000000000000000000000000000000000000000000000001p-126", "0x0.ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffp-126", "-0x0.fffffep-126", "-0x0.fffffe000000000000000000000000000000000000000000000000000001p-126", "-0x0.fffffefffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffp-126", "-0x0.ffffffp-126", "-0x0.ffffff0000000000000000000000000000000000000000000000000000001p-126", "-0x0.ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffp-126" };
        for (int i = 0; i < (inputs.length); i++) {
            float actual = Float.parseFloat(inputs[i]);
            float expected = Float.intBitsToFloat(expecteds[i]);
            String expectedString = Integer.toHexString(Float.floatToIntBits(expected));
            String actualString = Integer.toHexString(Float.floatToIntBits(actual));
            String errorMsg = ((((((i + "th input string is:<") + (inputs[i])) + ">.The expected result should be:<") + expectedString) + ">, but was: <") + actualString) + ">. ";
            TestCase.assertEquals(errorMsg, expected, actual, 0.0F);
        }
    }

    /**
     * java.lang.Float#parseFloat(java.lang.String)
     */
    public void test_parseFloat_LString_MinSubNormalBoundary() {
        int[] expecteds = new int[]{ 1, 1, 1, 2, 2, 2, -2147483647, -2147483647, -2147483647, -2147483646, -2147483646, -2147483646 };
        String[] inputs = new String[]{ "0x0.000002p-126", "0x0.00000200000000000000000000000000000000000001p-126", "0x0.000002ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffp-126", "0x0.000003p-126", "0x0.000003000000000000000000000000000000000000001p-126", "0x0.000003ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffp-126", "-0x0.000002p-126", "-0x0.00000200000000000000000000000000000000000001p-126", "-0x0.000002ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffp-126", "-0x0.000003p-126", "-0x0.000003000000000000000000000000000000000000001p-126", "-0x0.000003ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffp-126" };
        for (int i = 0; i < (inputs.length); i++) {
            float actual = Float.parseFloat(inputs[i]);
            float expected = Float.intBitsToFloat(expecteds[i]);
            String expectedString = Integer.toHexString(Float.floatToIntBits(expected));
            String actualString = Integer.toHexString(Float.floatToIntBits(actual));
            String errorMsg = ((((((i + "th input string is:<") + (inputs[i])) + ">.The expected result should be:<") + expectedString) + ">, but was: <") + actualString) + ">. ";
            TestCase.assertEquals(errorMsg, expected, actual, 0.0F);
        }
    }

    /**
     * java.lang.Float#parseFloat(java.lang.String)
     */
    public void test_parseFloat_LString_ZeroBoundary() {
        int[] expecteds = new int[]{ 0, 0, 0, 0, 1, 1, -2147483648, -2147483648, -2147483648, -2147483648, -2147483647, -2147483647 };
        String[] inputs = new String[]{ "0x0.000000000000000p-126", "0x0.000000000000000000000000000000000000000000000001p-126", "0x0.000000fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffp-126", "0x0.000001p-126", "0x0.000001000000000000000000000000000000000000000001p-126", "0x0.000001fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffp-126", "-0x0.000000000000000p-126", "-0x0.000000000000000000000000000000000000000000000001p-126", "-0x0.000000fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffp-126", "-0x0.000001p-126", "-0x0.000001000000000000000000000000000000000000000001p-126", "-0x0.000001fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffp-126" };
        for (int i = 0; i < (inputs.length); i++) {
            float actual = Float.parseFloat(inputs[i]);
            float expected = Float.intBitsToFloat(expecteds[i]);
            String expectedString = Integer.toHexString(Float.floatToIntBits(expected));
            String actualString = Integer.toHexString(Float.floatToIntBits(actual));
            String errorMsg = ((((((i + "th input string is:<") + (inputs[i])) + ">.The expected result should be:<") + expectedString) + ">, but was: <") + actualString) + ">. ";
            TestCase.assertEquals(errorMsg, expected, actual, 0.0F);
        }
    }

    /**
     * java.lang.Float#parseFloat(java.lang.String)
     */
    public void test_parseFloat_LString_Harmony6261() {
        // The specification adhering result should have one less sigificant digit, as that is
        // enough to uniquely identify this floating from its neighbors. We accept the extra
        // resolution here for now.
        // http://b/26140673
        float f = new Float("2147483648");
        // assertEquals("2.1474836E9", Float.toString(f));
        TestCase.assertTrue(Float.toString(f).matches("2.14748365?E9"));
        doTestCompareRawBits("123456790528.000000000000000f", 1374024905, "1.2345679E11");
        doTestCompareRawBits("8589934592", 1342177280, "8.5899346E9");
        doTestCompareRawBits("8606711808", 1342193664, "8.606712E9");
    }

    /**
     * java.lang.Float#shortValue()
     */
    public void test_shortValue() {
        // Test for method short java.lang.Float.shortValue()
        Float f = new Float(0.46874F);
        Float f2 = new Float(90.8F);
        TestCase.assertTrue("Returned incorrect short value", (((f.shortValue()) == 0) && ((f2.shortValue()) == 90)));
    }

    /**
     * java.lang.Float#toString()
     */
    public void test_toString() {
        // Test for method java.lang.String java.lang.Float.toString()
        test_toString(12.90898F, "12.90898");
        test_toString(1.7014118E38F, "1.7014118E38");
        test_toString(1.0E19F, "1.0E19");
        test_toString(1.0E-36F, "1.0E-36");
        test_toString(1.0E-38F, "1.0E-38");
    }

    /**
     * java.lang.Float#toString(float)
     */
    public void test_toStringF() {
        // Test for method java.lang.String java.lang.Float.toString(float)
        float ff;
        String answer;
        ff = 12.90898F;
        answer = "12.90898";
        TestCase.assertTrue(((("Incorrect String representation want " + answer) + ", got ") + (Float.toString(ff))), Float.toString(ff).equals(answer));
        ff = Float.MAX_VALUE;
        answer = "3.4028235E38";
        TestCase.assertTrue(((("Incorrect String representation want " + answer) + ", got ") + (Float.toString(ff))), Float.toString(ff).equals(answer));
    }

    /**
     * java.lang.Float#valueOf(java.lang.String)
     */
    public void test_valueOfLjava_lang_String() {
        // Test for method java.lang.Float
        // java.lang.Float.valueOf(java.lang.String)
        Float wanted = new Float(432.1235F);
        Float got = Float.valueOf("432.1235");
        TestCase.assertTrue(((("Incorrect float returned--wanted: " + wanted) + " but got: ") + got), got.equals(wanted));
        wanted = new Float(0.0F);
        got = Float.valueOf("0");
        TestCase.assertTrue(((("Incorrect float returned--wanted: " + wanted) + " but got: ") + got), got.equals(wanted));
        wanted = new Float((-1212.3232F));
        got = Float.valueOf("-1212.3232");
        TestCase.assertTrue(((("Incorrect float returned--wanted: " + wanted) + " but got: ") + got), got.equals(wanted));
        try {
            Float.valueOf(null);
            TestCase.fail("Expected Float.valueOf(null) to throw NPE.");
        } catch (NullPointerException ex) {
            // expected
        }
        try {
            Float.valueOf("");
            TestCase.fail("Expected Float.valueOf(\"\") to throw NFE");
        } catch (NumberFormatException e) {
            // expected
        }
        Float posZero = Float.valueOf("+0.0");
        Float negZero = Float.valueOf("-0.0");
        TestCase.assertFalse("Floattest0", posZero.equals(negZero));
        TestCase.assertTrue("Floattest1", (0.0F == (-0.0F)));
        // Tests for float values by name.
        Float expectedNaN = new Float(Float.NaN);
        Float posNaN = Float.valueOf("NaN");
        TestCase.assertTrue("Floattest2", posNaN.equals(expectedNaN));
        Float posNaNSigned = Float.valueOf("+NaN");
        TestCase.assertTrue("Floattest3", posNaNSigned.equals(expectedNaN));
        Float negNaNSigned = Float.valueOf("-NaN");
        TestCase.assertTrue("Floattest4", negNaNSigned.equals(expectedNaN));
        Float posInfinite = Float.valueOf("Infinity");
        TestCase.assertTrue("Floattest5", posInfinite.equals(new Float(Float.POSITIVE_INFINITY)));
        Float posInfiniteSigned = Float.valueOf("+Infinity");
        TestCase.assertTrue("Floattest6", posInfiniteSigned.equals(new Float(Float.POSITIVE_INFINITY)));
        Float negInfiniteSigned = Float.valueOf("-Infinity");
        TestCase.assertTrue("Floattest7", negInfiniteSigned.equals(new Float(Float.NEGATIVE_INFINITY)));
        // test HARMONY-6641
        posInfinite = Float.valueOf("320.0E+2147483647");
        TestCase.assertEquals("Floattest8", Float.POSITIVE_INFINITY, posInfinite);
        negZero = Float.valueOf("-1.4E-2147483314");
        TestCase.assertEquals("Floattest9", (-0.0F), negZero);
    }

    /**
     * java.lang.Float#compareTo(java.lang.Float)
     * java.lang.Float#compare(float, float)
     */
    public void test_compareToLjava_lang_Float() {
        if (System.getProperty("os.arch").equals("armv7")) {
            return;
        }
        // A selection of float values in ascending order.
        float[] values = new float[]{ Float.NEGATIVE_INFINITY, -(Float.MAX_VALUE), -2.0F, -(Float.MIN_VALUE), -0.0F, 0.0F, Float.MIN_VALUE, 2.0F, Float.MAX_VALUE, Float.POSITIVE_INFINITY, Float.NaN };
        for (int i = 0; i < (values.length); i++) {
            float f1 = values[i];
            // Test that each value compares equal to itself; and each object is
            // equal to another object
            // like itself
            TestCase.assertTrue(("Assert 0: compare() should be equal: " + f1), ((Float.compare(f1, f1)) == 0));
            Float objFloat = new Float(f1);
            TestCase.assertTrue(("Assert 1: compareTo() should be equal: " + objFloat), ((objFloat.compareTo(objFloat)) == 0));
            // Test that the Float-defined order is respected
            for (int j = i + 1; j < (values.length); j++) {
                float f2 = values[j];
                TestCase.assertTrue(((("Assert 2: compare() " + f1) + " should be less ") + f2), ((Float.compare(f1, f2)) == (-1)));
                TestCase.assertTrue(((("Assert 3: compare() " + f2) + " should be greater ") + f1), ((Float.compare(f2, f1)) == 1));
                Float F2 = new Float(f2);
                TestCase.assertTrue(((("Assert 4: compareTo() " + f1) + " should be less ") + f2), ((objFloat.compareTo(F2)) == (-1)));
                TestCase.assertTrue(((("Assert 5: compareTo() " + f2) + " should be greater ") + f1), ((F2.compareTo(objFloat)) == 1));
            }
        }
    }

    /**
     * java.lang.Float#equals(java.lang.Object)
     */
    public void test_equalsLjava_lang_Object() {
        Float f1 = new Float(8765.432F);
        Float f2 = new Float(8765.432F);
        Float f3 = new Float((-1.0F));
        TestCase.assertTrue("Assert 0: Equality test failed", ((f1.equals(f2)) && (!(f1.equals(f3)))));
        TestCase.assertTrue("Assert 1: NaN should not be == Nan", ((Float.NaN) != (Float.NaN)));
        TestCase.assertTrue("Assert 2: NaN should not be == Nan", new Float(Float.NaN).equals(new Float(Float.NaN)));
        TestCase.assertTrue("Assert 3: -0f should be == 0f", (0.0F == (-0.0F)));
        TestCase.assertTrue("Assert 4: -0f should not be equals() 0f", (!(new Float(0.0F).equals(new Float((-0.0F))))));
        f1 = new Float(1098.576F);
        f2 = new Float(1098.576F);
        f3 = new Float(1.0F);
        TestCase.assertTrue("Equality test failed", ((f1.equals(f2)) && (!(f1.equals(f3)))));
        TestCase.assertTrue("NaN should not be == Nan", ((Float.NaN) != (Float.NaN)));
        TestCase.assertTrue("NaN should not be == Nan", new Float(Float.NaN).equals(new Float(Float.NaN)));
        TestCase.assertTrue("-0f should be == 0f", (0.0F == (-0.0F)));
        TestCase.assertTrue("-0f should not be equals() 0f", (!(new Float(0.0F).equals(new Float((-0.0F))))));
    }

    /**
     * java.lang.Float#toHexString(float)
     */
    public void test_toHexStringF() {
        // the follow values comes from the Float Javadoc/Spec
        TestCase.assertEquals("0x0.0p0", Float.toHexString(0.0F));
        TestCase.assertEquals("-0x0.0p0", Float.toHexString((-0.0F)));
        TestCase.assertEquals("0x1.0p0", Float.toHexString(1.0F));
        TestCase.assertEquals("-0x1.0p0", Float.toHexString((-1.0F)));
        TestCase.assertEquals("0x1.0p1", Float.toHexString(2.0F));
        TestCase.assertEquals("0x1.8p1", Float.toHexString(3.0F));
        TestCase.assertEquals("0x1.0p-1", Float.toHexString(0.5F));
        TestCase.assertEquals("0x1.0p-2", Float.toHexString(0.25F));
        TestCase.assertEquals("0x1.fffffep127", Float.toHexString(Float.MAX_VALUE));
        TestCase.assertEquals("0x0.000002p-126", Float.toHexString(Float.MIN_VALUE));
        // test edge cases
        TestCase.assertEquals("NaN", Float.toHexString(Float.NaN));
        TestCase.assertEquals("-Infinity", Float.toHexString(Float.NEGATIVE_INFINITY));
        TestCase.assertEquals("Infinity", Float.toHexString(Float.POSITIVE_INFINITY));
        // test various numbers
        TestCase.assertEquals("-0x1.da8p6", Float.toHexString((-118.625F)));
        TestCase.assertEquals("0x1.295788p23", Float.toHexString(9743300.0F));
        TestCase.assertEquals("0x1.295788p23", Float.toHexString(9743300.0F));
        TestCase.assertEquals("0x1.295788p23", Float.toHexString(9743300.0F));
        TestCase.assertEquals("0x1.700d1p33", Float.toHexString(1.23497431E10F));
        // test HARMONY-2132
        TestCase.assertEquals("0x1.01p10", Float.toHexString(1028.0F));
    }

    /**
     * java.lang.Float#valueOf(float)
     */
    public void test_valueOfF() {
        TestCase.assertEquals(new Float(Float.MIN_VALUE), Float.valueOf(Float.MIN_VALUE));
        TestCase.assertEquals(new Float(Float.MAX_VALUE), Float.valueOf(Float.MAX_VALUE));
        TestCase.assertEquals(new Float(0), Float.valueOf(0));
        int s = -128;
        while (s < 128) {
            TestCase.assertEquals(new Float(s), Float.valueOf(s));
            TestCase.assertEquals(new Float((s + 0.1F)), Float.valueOf((s + 0.1F)));
            TestCase.assertEquals(Float.valueOf((s + 0.1F)), Float.valueOf((s + 0.1F)));
            s++;
        } 
    }

    /**
     * {@link java.lang.Float#MAX_EXPONENT}
     *
     * @since 1.6
     */
    public void test_MAX_EXPONENT() {
        TestCase.assertTrue("Wrong value of java.lang.Float.MAX_EXPONENT", ((Float.MAX_EXPONENT) == 127));
        TestCase.assertTrue("Wrong value of java.lang.Float.MAX_EXPONENT", ((Float.MAX_EXPONENT) == (Math.getExponent(Float.MAX_VALUE))));
    }

    /**
     * {@link java.lang.Float#MIN_EXPONENT}
     *
     * @since 1.6
     */
    public void test_MIN_EXPONENT() {
        TestCase.assertTrue("Wrong value of java.lang.Float.MIN_EXPONENT", ((Float.MIN_EXPONENT) == (-126)));
        TestCase.assertTrue("Wrong value of java.lang.Float.MIN_EXPONENT", ((Float.MIN_EXPONENT) == (Math.getExponent(Float.MIN_NORMAL))));
    }

    /**
     * {@link java.lang.Float#MIN_NORMAL}
     *
     * @since 1.6
     */
    public void test_MIN_NORMAL() {
        TestCase.assertTrue("Wrong value of java.lang.Float.MIN_NORMAL", ((Float.MIN_NORMAL) == 1.17549435E-38F));
        TestCase.assertTrue("Wrong value of java.lang.Float.MIN_NORMAL", ((Float.MIN_NORMAL) == (Float.intBitsToFloat(8388608))));
        TestCase.assertTrue("Wrong value of java.lang.Float.MIN_NORMAL", ((Float.MIN_NORMAL) == 1.17549435E-38F));
    }
}

