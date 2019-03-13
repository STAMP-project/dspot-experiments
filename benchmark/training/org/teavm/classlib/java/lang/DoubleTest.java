/**
 * Copyright 2014 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.lang;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class DoubleTest {
    @Test
    public void parsed() {
        Assert.assertEquals(23, Double.parseDouble("23"), 1.0E-12);
        Assert.assertEquals(23, Double.parseDouble("23.0"), 1.0E-12);
        Assert.assertEquals(23, Double.parseDouble("23E0"), 1.0E-12);
        Assert.assertEquals(23, Double.parseDouble("2.30000E1"), 1.0E-12);
        Assert.assertEquals(23, Double.parseDouble("0.23E2"), 1.0E-12);
        Assert.assertEquals(23, Double.parseDouble("0.000023E6"), 1.0E-12);
        Assert.assertEquals(23, Double.parseDouble("00230000e-4"), 1.0E-12);
        Assert.assertEquals(23, Double.parseDouble("2300000000000000000000e-20"), 1.0E-12);
        Assert.assertEquals(23, Double.parseDouble("2300000000000000000000e-20"), 1.0E-12);
        Assert.assertEquals(23, Double.parseDouble("23."), 1.0E-12);
        Assert.assertEquals(0.1, Double.parseDouble("0.1"), 0.001);
        Assert.assertEquals(0.1, Double.parseDouble(".1"), 0.001);
    }

    @Test
    public void negativeParsed() {
        Assert.assertEquals((-23), Double.parseDouble("-23"), 1.0E-12);
    }

    @Test
    public void zeroParsed() {
        Assert.assertEquals(0, Double.parseDouble("0.0"), 1.0E-12);
        Assert.assertEquals(0, Double.parseDouble("23E-8000"), 1.0E-12);
        Assert.assertEquals(0, Double.parseDouble("00000"), 1.0E-12);
        Assert.assertEquals(0, Double.parseDouble("00000.0000"), 1.0E-12);
    }

    @Test
    public void longBitsExtracted() {
        Assert.assertEquals(4747414503197162718L, Double.doubleToLongBits(2.443359172835555E9));
    }

    @Test
    public void longBitsExtracted2() {
        Assert.assertEquals((4603174215131657667L >>> 3), ((Double.doubleToLongBits(0.555)) >>> 3));
    }

    @Test
    public void subNormalLongBitsExtracted() {
        Assert.assertEquals(371390594270L, Double.doubleToLongBits(1.834913338174E-312));
    }

    @Test
    public void longBitsPacked() {
        Assert.assertEquals(2.443359172835555E9, Double.longBitsToDouble(4747414503197162718L), 1.9073486328125E-6);
    }

    @Test
    public void subNormalLongBitsPacked() {
        Assert.assertEquals(1.834913338174E-312, Double.longBitsToDouble(371390594270L), 1.9073486328125E-6);
    }

    @Test
    public void hexStringBuilt() {
        Assert.assertEquals("0x1.23456789abcdep31", Double.toHexString(2.443359172835555E9));
        Assert.assertEquals("0x1.0p0", Double.toHexString(1));
        Assert.assertEquals("-0x1.0p0", Double.toHexString((-1)));
        Assert.assertEquals("0x1.0p1", Double.toHexString(2));
        Assert.assertEquals("0x1.8p1", Double.toHexString(3));
        Assert.assertEquals("0x1.0p-1", Double.toHexString(0.5));
        Assert.assertEquals("0x1.0p-2", Double.toHexString(0.25));
        Assert.assertEquals("0x1.0p-1022", Double.toHexString(2.2250738585072014E-308));
        Assert.assertEquals("0x0.8p-1022", Double.toHexString(1.1125369292536007E-308));
        Assert.assertEquals("0x0.001p-1022", Double.toHexString(5.43230922487E-312));
    }

    @Test
    public void compares() {
        Assert.assertTrue(((Double.compare(10, 5)) > 0));
        Assert.assertTrue(((Double.compare(5, 10)) < 0));
        Assert.assertTrue(((Double.compare(5, 5)) == 0));
    }
}

