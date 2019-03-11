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
public class IntegerTest {
    @Test
    public void parsesInteger() {
        Assert.assertEquals(0, Integer.parseInt("0", 10));
        Assert.assertEquals(473, Integer.parseInt("473", 10));
        Assert.assertEquals(42, Integer.parseInt("+42", 10));
        Assert.assertEquals(0, Integer.parseInt("-0", 10));
        Assert.assertEquals((-255), Integer.parseInt("-FF", 16));
        Assert.assertEquals(102, Integer.parseInt("1100110", 2));
        Assert.assertEquals(2147483647, Integer.parseInt("2147483647", 10));
        Assert.assertEquals(411787, Integer.parseInt("Kona", 27));
    }

    @Test
    public void parsesMinInteger() {
        Assert.assertEquals(-2147483648, Integer.parseInt("-2147483648", 10));
        Assert.assertEquals(-2147483648, Integer.parseInt("-80000000", 16));
    }

    @Test(expected = NumberFormatException.class)
    public void rejectsTooBigInteger() {
        Integer.parseInt("2147483648", 10);
    }

    @Test(expected = NumberFormatException.class)
    public void rejectsIntegerWithDigitsOutOfRadix() {
        Integer.parseInt("99", 8);
    }

    @Test
    public void writesInteger() {
        Assert.assertEquals("473", Integer.toString(473, 10));
        Assert.assertEquals("-ff", Integer.toString((-255), 16));
        Assert.assertEquals("kona", Integer.toString(411787, 27));
    }

    @Test
    public void writesSingleDigitInteger() {
        Assert.assertEquals("a", Integer.toString(10, 16));
    }

    @Test
    public void decodes() {
        Assert.assertEquals(Integer.valueOf(123), Integer.decode("123"));
        Assert.assertEquals(Integer.valueOf(83), Integer.decode("0123"));
        Assert.assertEquals(Integer.valueOf(255), Integer.decode("0xFF"));
        Assert.assertEquals(Integer.valueOf(65535), Integer.decode("+0xFFFF"));
        Assert.assertEquals(Integer.valueOf((-255)), Integer.decode("-0xFF"));
        Assert.assertEquals(Integer.valueOf(2748), Integer.decode("+#ABC"));
    }

    @Test
    public void numberOfLeadingZerosComputed() {
        Assert.assertEquals(1, Integer.numberOfLeadingZeros(1073741824));
        Assert.assertEquals(1, Integer.numberOfLeadingZeros(1073742115));
        Assert.assertEquals(1, Integer.numberOfLeadingZeros(2147483647));
        Assert.assertEquals(31, Integer.numberOfLeadingZeros(1));
        Assert.assertEquals(30, Integer.numberOfLeadingZeros(2));
        Assert.assertEquals(30, Integer.numberOfLeadingZeros(3));
        Assert.assertEquals(0, Integer.numberOfLeadingZeros(-2147483648));
        Assert.assertEquals(0, Integer.numberOfLeadingZeros(-2147483357));
        Assert.assertEquals(0, Integer.numberOfLeadingZeros(-1));
        Assert.assertEquals(32, Integer.numberOfLeadingZeros(0));
    }

    @Test
    public void numberOfTrailingZerosComputed() {
        Assert.assertEquals(1, Integer.numberOfTrailingZeros(-2));
        Assert.assertEquals(1, Integer.numberOfTrailingZeros(1073741826));
        Assert.assertEquals(1, Integer.numberOfTrailingZeros(2));
        Assert.assertEquals(31, Integer.numberOfTrailingZeros(-2147483648));
        Assert.assertEquals(30, Integer.numberOfTrailingZeros(1073741824));
        Assert.assertEquals(30, Integer.numberOfTrailingZeros(-1073741824));
        Assert.assertEquals(0, Integer.numberOfTrailingZeros(1));
        Assert.assertEquals(0, Integer.numberOfTrailingZeros(305135619));
        Assert.assertEquals(0, Integer.numberOfTrailingZeros(-1));
        Assert.assertEquals(32, Integer.numberOfTrailingZeros(0));
    }

    @Test
    public void bitsCounted() {
        Assert.assertEquals(0, Integer.bitCount(0));
        Assert.assertEquals(1, Integer.bitCount(1));
        Assert.assertEquals(1, Integer.bitCount(1024));
        Assert.assertEquals(1, Integer.bitCount(-2147483648));
        Assert.assertEquals(8, Integer.bitCount(286331153));
        Assert.assertEquals(8, Integer.bitCount(808464432));
        Assert.assertEquals(8, Integer.bitCount(-16777216));
        Assert.assertEquals(8, Integer.bitCount(255));
        Assert.assertEquals(32, Integer.bitCount(-1));
        Assert.assertEquals(13, Integer.bitCount(384111));
    }

    @Test
    public void bitsReversed() {
        Assert.assertEquals(0, Integer.reverse(0));
        Assert.assertEquals(-2147483648, Integer.reverse(1));
        Assert.assertEquals(2097152, Integer.reverse(1024));
        Assert.assertEquals(1, Integer.reverse(-2147483648));
        Assert.assertEquals(-2004318072, Integer.reverse(286331153));
        Assert.assertEquals(202116108, Integer.reverse(808464432));
        Assert.assertEquals(255, Integer.reverse(-16777216));
        Assert.assertEquals(-16777216, Integer.reverse(255));
        Assert.assertEquals(-1, Integer.reverse(-1));
        Assert.assertEquals(-163864576, Integer.reverse(384111));
    }

    @Test
    public void compares() {
        Assert.assertTrue(((Integer.compare(10, 5)) > 0));
        Assert.assertTrue(((Integer.compare(5, 10)) < 0));
        Assert.assertTrue(((Integer.compare(5, 5)) == 0));
        Assert.assertTrue(((Integer.compare(Integer.MAX_VALUE, Integer.MIN_VALUE)) > 0));
        Assert.assertTrue(((Integer.compare(Integer.MIN_VALUE, Integer.MAX_VALUE)) < 0));
    }

    @Test
    public void getFromSystemProperty() {
        System.setProperty("test.foo", "23");
        System.setProperty("test.bar", "q");
        Assert.assertEquals(((Object) (23)), Integer.getInteger("test.foo"));
        Assert.assertNull(Integer.getInteger("test.bar"));
        Assert.assertNull(Integer.getInteger("test.baz"));
        Assert.assertNull(Integer.getInteger(null));
    }

    @Test
    public void toHex() {
        Assert.assertEquals("0", Integer.toHexString(0));
        Assert.assertEquals("1", Integer.toHexString(1));
        Assert.assertEquals("a", Integer.toHexString(10));
        Assert.assertEquals("11", Integer.toHexString(17));
        Assert.assertEquals("ff", Integer.toHexString(255));
        Assert.assertEquals("ffffffff", Integer.toHexString((-1)));
    }
}

