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
public class FloatTest {
    @Test
    public void parsed() {
        Assert.assertEquals(23, Float.parseFloat("23"), 1.0E-12F);
        Assert.assertEquals(23, Float.parseFloat("23.0"), 1.0E-12F);
        Assert.assertEquals(23, Float.parseFloat("23E0"), 1.0E-12F);
        Assert.assertEquals(23, Float.parseFloat("2.30000E1"), 1.0E-12F);
        Assert.assertEquals(23, Float.parseFloat("0.23E2"), 1.0E-12F);
        Assert.assertEquals(23, Float.parseFloat("0.000023E6"), 1.0E-12F);
        Assert.assertEquals(23, Float.parseFloat("00230000e-4"), 1.0E-12F);
        Assert.assertEquals(23, Float.parseFloat("2300000000000000000000e-20"), 1.0E-12F);
        Assert.assertEquals(23, Float.parseFloat("2300000000000000000000e-20"), 1.0E-12F);
        Assert.assertEquals(23, Float.parseFloat("2300000000000000000000e-20"), 1.0E-12F);
        Assert.assertEquals(23, Float.parseFloat("23."), 1.0E-12F);
        Assert.assertEquals(0.1F, Float.parseFloat("0.1"), 0.001F);
        Assert.assertEquals(0.1F, Float.parseFloat(".1"), 0.001F);
    }

    @Test
    public void negativeParsed() {
        Assert.assertEquals((-23), Float.parseFloat("-23"), 1.0E-12F);
    }

    @Test
    public void zeroParsed() {
        Assert.assertEquals(0, Float.parseFloat("0.0"), 1.0E-12F);
        Assert.assertEquals(0, Float.parseFloat("23E-8000"), 1.0E-12F);
        Assert.assertEquals(0, Float.parseFloat("00000"), 1.0E-12F);
        Assert.assertEquals(0, Float.parseFloat("00000.0000"), 1.0E-12F);
    }

    @Test
    public void floatBitsExtracted() {
        Assert.assertEquals(1167172276, Float.floatToIntBits(4660.338F));
    }

    @Test
    public void floatBitsExtracted2() {
        Assert.assertEquals(8388608, Float.floatToIntBits(((float) (Math.pow(2, (-126))))));
    }

    @Test
    public void subNormalFloatBitsExtracted() {
        Assert.assertEquals(146, Float.floatToIntBits(2.05E-43F));
    }

    @Test
    public void floatBitsPacked() {
        Assert.assertEquals(4660.338F, Float.intBitsToFloat(1167172276), 1.0E7);
    }

    @Test
    public void subNormalFloatBitsPacked() {
        Assert.assertEquals(2.05E-43F, Float.intBitsToFloat(146), 9.4039548065783E-38);
    }

    @Test
    public void hexStringBuilt() {
        Assert.assertEquals("0x1.23456p17", Float.toHexString(149130.75F));
        Assert.assertEquals("0x1.0p0", Float.toHexString(1));
        Assert.assertEquals("-0x1.0p0", Float.toHexString((-1)));
        Assert.assertEquals("0x1.0p1", Float.toHexString(2));
        Assert.assertEquals("0x1.8p1", Float.toHexString(3));
        Assert.assertEquals("0x1.0p-1", Float.toHexString(0.5F));
        Assert.assertEquals("0x1.0p-2", Float.toHexString(0.25F));
        Assert.assertEquals("0x1.0p-126", Float.toHexString(((float) (Math.pow(2, (-126))))));
        Assert.assertEquals("0x0.001p-126", Float.toHexString(2.87E-42F));
    }

    @Test
    public void compares() {
        Assert.assertTrue(((Float.compare(10, 5)) > 0));
        Assert.assertTrue(((Float.compare(5, 10)) < 0));
        Assert.assertTrue(((Float.compare(5, 5)) == 0));
    }
}

