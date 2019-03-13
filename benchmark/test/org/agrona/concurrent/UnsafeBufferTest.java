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
package org.agrona.concurrent;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;


@RunWith(Theories.class)
public class UnsafeBufferTest {
    private static final byte VALUE = 42;

    private static final int INDEX = 1;

    private static final int ADJUSTMENT_OFFSET = 3;

    private final byte[] wibbleBytes = "Wibble".getBytes(StandardCharsets.US_ASCII);

    private final byte[] wobbleBytes = "Wobble".getBytes(StandardCharsets.US_ASCII);

    private final byte[] wibbleBytes2 = "Wibble2".getBytes(StandardCharsets.US_ASCII);

    @Test
    public void shouldEqualOnInstance() {
        final UnsafeBuffer wibbleBuffer = new UnsafeBuffer(wibbleBytes);
        Assert.assertThat(wibbleBuffer, CoreMatchers.is(wibbleBuffer));
    }

    @Test
    public void shouldEqualOnContent() {
        final UnsafeBuffer wibbleBufferOne = new UnsafeBuffer(wibbleBytes);
        final UnsafeBuffer wibbleBufferTwo = new UnsafeBuffer(wibbleBytes.clone());
        Assert.assertThat(wibbleBufferOne, CoreMatchers.is(wibbleBufferTwo));
    }

    @Test
    public void shouldNotEqual() {
        final UnsafeBuffer wibbleBuffer = new UnsafeBuffer(wibbleBytes);
        final UnsafeBuffer wobbleBuffer = new UnsafeBuffer(wobbleBytes);
        Assert.assertThat(wibbleBuffer, CoreMatchers.is(CoreMatchers.not(wobbleBuffer)));
    }

    @Test
    public void shouldEqualOnHashCode() {
        final UnsafeBuffer wibbleBufferOne = new UnsafeBuffer(wibbleBytes);
        final UnsafeBuffer wibbleBufferTwo = new UnsafeBuffer(wibbleBytes.clone());
        Assert.assertThat(wibbleBufferOne.hashCode(), CoreMatchers.is(wibbleBufferTwo.hashCode()));
    }

    @Test
    public void shouldEqualOnCompareContents() {
        final UnsafeBuffer wibbleBufferOne = new UnsafeBuffer(wibbleBytes);
        final UnsafeBuffer wibbleBufferTwo = new UnsafeBuffer(wibbleBytes.clone());
        Assert.assertThat(wibbleBufferOne.compareTo(wibbleBufferTwo), CoreMatchers.is(0));
    }

    @Test
    public void shouldCompareLessThanOnContents() {
        final UnsafeBuffer wibbleBuffer = new UnsafeBuffer(wibbleBytes);
        final UnsafeBuffer wobbleBuffer = new UnsafeBuffer(wobbleBytes);
        Assert.assertThat(wibbleBuffer.compareTo(wobbleBuffer), CoreMatchers.is(Matchers.lessThan(0)));
    }

    @Test
    public void shouldCompareGreaterThanOnContents() {
        final UnsafeBuffer wibbleBuffer = new UnsafeBuffer(wibbleBytes);
        final UnsafeBuffer wobbleBuffer = new UnsafeBuffer(wobbleBytes);
        Assert.assertThat(wobbleBuffer.compareTo(wibbleBuffer), CoreMatchers.is(Matchers.greaterThan(0)));
    }

    @Test
    public void shouldCompareLessThanOnContentsOfDifferingCapacity() {
        final UnsafeBuffer wibbleBuffer = new UnsafeBuffer(wibbleBytes);
        final UnsafeBuffer wibbleBuffer2 = new UnsafeBuffer(wibbleBytes2);
        Assert.assertThat(wibbleBuffer.compareTo(wibbleBuffer2), CoreMatchers.is(Matchers.lessThan(0)));
    }

    @Test
    public void shouldExposePositionAtWhichByteArrayGetsWrapped() {
        final UnsafeBuffer wibbleBuffer = new UnsafeBuffer(wibbleBytes, UnsafeBufferTest.ADJUSTMENT_OFFSET, ((wibbleBytes.length) - (UnsafeBufferTest.ADJUSTMENT_OFFSET)));
        wibbleBuffer.putByte(0, UnsafeBufferTest.VALUE);
        Assert.assertEquals(UnsafeBufferTest.VALUE, wibbleBytes[wibbleBuffer.wrapAdjustment()]);
    }

    @Test
    public void shouldExposePositionAtWhichHeapByteBufferGetsWrapped() {
        final ByteBuffer wibbleByteBuffer = ByteBuffer.wrap(wibbleBytes);
        shouldExposePositionAtWhichByteBufferGetsWrapped(wibbleByteBuffer);
    }

    @Test
    public void shouldExposePositionAtWhichDirectByteBufferGetsWrapped() {
        final ByteBuffer wibbleByteBuffer = ByteBuffer.allocateDirect(wibbleBytes.length);
        shouldExposePositionAtWhichByteBufferGetsWrapped(wibbleByteBuffer);
    }

    @Test
    public void shouldGetIntegerValuesAtSpecifiedOffset() {
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[128]);
        putAscii(buffer, "123");
        final int value = buffer.parseNaturalIntAscii(UnsafeBufferTest.INDEX, 3);
        Assert.assertEquals(123, value);
    }

    @Test
    public void shouldDecodeNegativeIntegers() {
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[128]);
        putAscii(buffer, "-1");
        final int value = buffer.parseIntAscii(UnsafeBufferTest.INDEX, 2);
        Assert.assertEquals((-1), value);
    }

    @Test
    public void shouldWriteIntZero() {
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[128]);
        final int length = buffer.putIntAscii(UnsafeBufferTest.INDEX, 0);
        Assert.assertEquals(1, length);
        assertContainsString(buffer, "0", 1);
    }

    @Test
    public void shouldWritePositiveIntValues() {
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[128]);
        final int length = buffer.putIntAscii(UnsafeBufferTest.INDEX, 123);
        Assert.assertEquals(3, length);
        assertContainsString(buffer, "123", 3);
    }

    @Test
    public void shouldWriteNegativeIntValues() {
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[128]);
        final int length = buffer.putIntAscii(UnsafeBufferTest.INDEX, (-123));
        Assert.assertEquals(4, length);
        assertContainsString(buffer, "-123", 4);
    }

    @Test
    public void shouldWriteMaxIntValue() {
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[128]);
        final int length = buffer.putIntAscii(UnsafeBufferTest.INDEX, Integer.MAX_VALUE);
        assertContainsString(buffer, String.valueOf(Integer.MAX_VALUE), length);
    }

    @Test
    public void shouldWriteMinIntValue() {
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[128]);
        final int length = buffer.putIntAscii(UnsafeBufferTest.INDEX, Integer.MIN_VALUE);
        assertContainsString(buffer, String.valueOf(Integer.MIN_VALUE), length);
    }

    @Test
    public void shouldSkipArrayContentPrintout() throws Exception {
        final Field settingField = UnsafeBuffer.class.getDeclaredField("SHOULD_PRINT_ARRAY_CONTENT");
        settingField.setAccessible(true);
        final Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(settingField, ((settingField.getModifiers()) & (~(Modifier.FINAL))));
        final byte[] backingArray = new byte[10];
        final UnsafeBuffer unsafeBuffer = new UnsafeBuffer(backingArray);
        settingField.set(null, true);
        Assert.assertTrue(unsafeBuffer.toString().contains(Arrays.toString(backingArray)));
        settingField.set(null, false);
        Assert.assertFalse(unsafeBuffer.toString().contains(Arrays.toString(backingArray)));
    }
}

