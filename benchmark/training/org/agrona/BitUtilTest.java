/**
 * Copyright 2014-2019 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.agrona;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class BitUtilTest {
    @Test
    public void shouldReturnNextPositivePowerOfTwo() {
        Assert.assertThat(findNextPositivePowerOfTwo(Integer.MIN_VALUE), CoreMatchers.is(Integer.MIN_VALUE));
        Assert.assertThat(findNextPositivePowerOfTwo(((Integer.MIN_VALUE) + 1)), CoreMatchers.is(1));
        Assert.assertThat(findNextPositivePowerOfTwo((-1)), CoreMatchers.is(1));
        Assert.assertThat(findNextPositivePowerOfTwo(0), CoreMatchers.is(1));
        Assert.assertThat(findNextPositivePowerOfTwo(1), CoreMatchers.is(1));
        Assert.assertThat(findNextPositivePowerOfTwo(2), CoreMatchers.is(2));
        Assert.assertThat(findNextPositivePowerOfTwo(3), CoreMatchers.is(4));
        Assert.assertThat(findNextPositivePowerOfTwo(4), CoreMatchers.is(4));
        Assert.assertThat(findNextPositivePowerOfTwo(31), CoreMatchers.is(32));
        Assert.assertThat(findNextPositivePowerOfTwo(32), CoreMatchers.is(32));
        Assert.assertThat(findNextPositivePowerOfTwo((1 << 30)), CoreMatchers.is((1 << 30)));
        Assert.assertThat(findNextPositivePowerOfTwo(((1 << 30) + 1)), CoreMatchers.is(Integer.MIN_VALUE));
    }

    @Test
    public void shouldAlignValueToNextMultipleOfAlignment() {
        final int alignment = CACHE_LINE_LENGTH;
        Assert.assertThat(align(0, alignment), CoreMatchers.is(0));
        Assert.assertThat(align(1, alignment), CoreMatchers.is(alignment));
        Assert.assertThat(align(alignment, alignment), CoreMatchers.is(alignment));
        Assert.assertThat(align((alignment + 1), alignment), CoreMatchers.is((alignment * 2)));
        final int remainder = (Integer.MAX_VALUE) % alignment;
        final int maxMultiple = (Integer.MAX_VALUE) - remainder;
        Assert.assertThat(align(maxMultiple, alignment), CoreMatchers.is(maxMultiple));
        Assert.assertThat(align(Integer.MAX_VALUE, alignment), CoreMatchers.is(Integer.MIN_VALUE));
    }

    @Test
    public void shouldConvertToHexCorrectly() {
        final byte[] buffer = new byte[]{ 1, 35, 69, 105, 120, ((byte) (188)), ((byte) (218)), ((byte) (239)), 95 };
        final byte[] converted = toHexByteArray(buffer);
        final String hexStr = toHex(buffer);
        Assert.assertThat(converted[0], CoreMatchers.is(((byte) ('0'))));
        Assert.assertThat(converted[1], CoreMatchers.is(((byte) ('1'))));
        Assert.assertThat(converted[2], CoreMatchers.is(((byte) ('2'))));
        Assert.assertThat(converted[3], CoreMatchers.is(((byte) ('3'))));
        Assert.assertThat(hexStr, CoreMatchers.is("0123456978bcdaef5f"));
    }

    @Test
    public void shouldDetectEvenAndOddNumbers() {
        Assert.assertTrue(BitUtil.BitUtil.isEven(0));
        Assert.assertTrue(BitUtil.BitUtil.isEven(2));
        Assert.assertTrue(BitUtil.BitUtil.isEven(Integer.MIN_VALUE));
        Assert.assertFalse(BitUtil.BitUtil.isEven(1));
        Assert.assertFalse(BitUtil.BitUtil.isEven((-1)));
        Assert.assertFalse(BitUtil.BitUtil.isEven(Integer.MAX_VALUE));
    }
}

