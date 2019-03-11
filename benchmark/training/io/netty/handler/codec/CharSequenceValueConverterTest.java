/**
 * Copyright 2018 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License, version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.netty.handler.codec;


import AsciiString.EMPTY_STRING;
import io.netty.util.AsciiString;
import org.junit.Assert;
import org.junit.Test;

import static CharSequenceValueConverter.INSTANCE;


public class CharSequenceValueConverterTest {
    private final CharSequenceValueConverter converter = INSTANCE;

    @Test
    public void testBoolean() {
        Assert.assertTrue(converter.convertToBoolean(converter.convertBoolean(true)));
        Assert.assertFalse(converter.convertToBoolean(converter.convertBoolean(false)));
    }

    @Test
    public void testByteFromAsciiString() {
        Assert.assertEquals(127, converter.convertToByte(AsciiString.of("127")));
    }

    @Test(expected = NumberFormatException.class)
    public void testByteFromEmptyAsciiString() {
        converter.convertToByte(EMPTY_STRING);
    }

    @Test
    public void testByte() {
        Assert.assertEquals(Byte.MAX_VALUE, converter.convertToByte(converter.convertByte(Byte.MAX_VALUE)));
    }

    @Test
    public void testChar() {
        Assert.assertEquals(Character.MAX_VALUE, converter.convertToChar(converter.convertChar(Character.MAX_VALUE)));
    }

    @Test
    public void testDouble() {
        Assert.assertEquals(Double.MAX_VALUE, converter.convertToDouble(converter.convertDouble(Double.MAX_VALUE)), 0);
    }

    @Test
    public void testFloat() {
        Assert.assertEquals(Float.MAX_VALUE, converter.convertToFloat(converter.convertFloat(Float.MAX_VALUE)), 0);
    }

    @Test
    public void testInt() {
        Assert.assertEquals(Integer.MAX_VALUE, converter.convertToInt(converter.convertInt(Integer.MAX_VALUE)));
    }

    @Test
    public void testShort() {
        Assert.assertEquals(Short.MAX_VALUE, converter.convertToShort(converter.convertShort(Short.MAX_VALUE)));
    }

    @Test
    public void testLong() {
        Assert.assertEquals(Long.MAX_VALUE, converter.convertToLong(converter.convertLong(Long.MAX_VALUE)));
    }

    @Test
    public void testTimeMillis() {
        // Zero out the millis as this is what the convert is doing as well.
        long millis = ((System.currentTimeMillis()) / 1000) * 1000;
        Assert.assertEquals(millis, converter.convertToTimeMillis(converter.convertTimeMillis(millis)));
    }
}

