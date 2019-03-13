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
package io.aeron;


import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.agrona.concurrent.UnsafeBuffer;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class BufferBuilderTest {
    private final BufferBuilder bufferBuilder = new BufferBuilder();

    @Test
    public void shouldInitialiseToDefaultValues() {
        Assert.assertThat(bufferBuilder.capacity(), CoreMatchers.is(0));
        Assert.assertThat(bufferBuilder.buffer().capacity(), CoreMatchers.is(0));
        Assert.assertThat(bufferBuilder.limit(), CoreMatchers.is(0));
    }

    @Test
    public void shouldGrowDirectBuffer() {
        final BufferBuilder builder = new BufferBuilder(0, true);
        Assert.assertThat(builder.capacity(), CoreMatchers.is(0));
        Assert.assertThat(builder.buffer().capacity(), CoreMatchers.is(0));
        Assert.assertThat(builder.limit(), CoreMatchers.is(0));
        final int appendedLength = 10;
        final UnsafeBuffer srcBuffer = new UnsafeBuffer(new byte[appendedLength]);
        builder.append(srcBuffer, 0, srcBuffer.capacity());
        Assert.assertThat(builder.capacity(), CoreMatchers.is(BufferBuilderUtil.MIN_ALLOCATED_CAPACITY));
        Assert.assertThat(builder.buffer().capacity(), CoreMatchers.is(BufferBuilderUtil.MIN_ALLOCATED_CAPACITY));
        Assert.assertThat(builder.limit(), CoreMatchers.is(appendedLength));
    }

    @Test
    public void shouldAppendNothingForZeroLength() {
        final UnsafeBuffer srcBuffer = new UnsafeBuffer(new byte[BufferBuilderUtil.MIN_ALLOCATED_CAPACITY]);
        bufferBuilder.append(srcBuffer, 0, 0);
        Assert.assertThat(bufferBuilder.limit(), CoreMatchers.is(0));
    }

    @Test
    public void shouldGrowToMultipleOfInitialCapacity() {
        final int srcCapacity = (BufferBuilderUtil.MIN_ALLOCATED_CAPACITY) * 5;
        final UnsafeBuffer srcBuffer = new UnsafeBuffer(new byte[srcCapacity]);
        bufferBuilder.append(srcBuffer, 0, srcBuffer.capacity());
        Assert.assertThat(bufferBuilder.limit(), CoreMatchers.is(srcCapacity));
        Assert.assertThat(bufferBuilder.capacity(), Matchers.greaterThanOrEqualTo(srcCapacity));
    }

    @Test
    public void shouldAppendThenReset() {
        final UnsafeBuffer srcBuffer = new UnsafeBuffer(new byte[BufferBuilderUtil.MIN_ALLOCATED_CAPACITY]);
        bufferBuilder.append(srcBuffer, 0, srcBuffer.capacity());
        Assert.assertThat(bufferBuilder.limit(), CoreMatchers.is(srcBuffer.capacity()));
        bufferBuilder.reset();
        Assert.assertThat(bufferBuilder.limit(), CoreMatchers.is(0));
    }

    @Test
    public void shouldAppendOneBufferWithoutResizing() {
        final UnsafeBuffer srcBuffer = new UnsafeBuffer(new byte[BufferBuilderUtil.MIN_ALLOCATED_CAPACITY]);
        final byte[] bytes = "Hello World".getBytes(StandardCharsets.UTF_8);
        srcBuffer.putBytes(0, bytes, 0, bytes.length);
        bufferBuilder.append(srcBuffer, 0, bytes.length);
        final byte[] temp = new byte[bytes.length];
        bufferBuilder.buffer().getBytes(0, temp, 0, bytes.length);
        Assert.assertThat(bufferBuilder.limit(), CoreMatchers.is(bytes.length));
        Assert.assertThat(bufferBuilder.capacity(), CoreMatchers.is(BufferBuilderUtil.MIN_ALLOCATED_CAPACITY));
        Assert.assertArrayEquals(temp, bytes);
    }

    @Test
    public void shouldAppendTwoBuffersWithoutResizing() {
        final UnsafeBuffer srcBuffer = new UnsafeBuffer(new byte[BufferBuilderUtil.MIN_ALLOCATED_CAPACITY]);
        final byte[] bytes = "1111111122222222".getBytes(StandardCharsets.UTF_8);
        srcBuffer.putBytes(0, bytes, 0, bytes.length);
        bufferBuilder.append(srcBuffer, 0, ((bytes.length) / 2));
        bufferBuilder.append(srcBuffer, ((bytes.length) / 2), ((bytes.length) / 2));
        final byte[] temp = new byte[bytes.length];
        bufferBuilder.buffer().getBytes(0, temp, 0, bytes.length);
        Assert.assertThat(bufferBuilder.limit(), CoreMatchers.is(bytes.length));
        Assert.assertThat(bufferBuilder.capacity(), CoreMatchers.is(BufferBuilderUtil.MIN_ALLOCATED_CAPACITY));
        Assert.assertArrayEquals(temp, bytes);
    }

    @Test
    public void shouldFillBufferWithoutResizing() {
        final int bufferLength = 128;
        final byte[] buffer = new byte[bufferLength];
        Arrays.fill(buffer, ((byte) (7)));
        final UnsafeBuffer srcBuffer = new UnsafeBuffer(buffer);
        final BufferBuilder bufferBuilder = new BufferBuilder(bufferLength);
        bufferBuilder.append(srcBuffer, 0, bufferLength);
        final byte[] temp = new byte[bufferLength];
        bufferBuilder.buffer().getBytes(0, temp, 0, bufferLength);
        Assert.assertThat(bufferBuilder.limit(), CoreMatchers.is(bufferLength));
        Assert.assertThat(bufferBuilder.capacity(), CoreMatchers.is(bufferLength));
        Assert.assertArrayEquals(temp, buffer);
    }

    @Test
    public void shouldResizeWhenBufferJustDoesNotFit() {
        final int bufferLength = 128;
        final byte[] buffer = new byte[bufferLength + 1];
        Arrays.fill(buffer, ((byte) (7)));
        final UnsafeBuffer srcBuffer = new UnsafeBuffer(buffer);
        final BufferBuilder bufferBuilder = new BufferBuilder(bufferLength);
        bufferBuilder.append(srcBuffer, 0, buffer.length);
        final byte[] temp = new byte[buffer.length];
        bufferBuilder.buffer().getBytes(0, temp, 0, buffer.length);
        Assert.assertThat(bufferBuilder.limit(), CoreMatchers.is(buffer.length));
        Assert.assertThat(bufferBuilder.capacity(), Matchers.greaterThan(bufferLength));
        Assert.assertArrayEquals(temp, buffer);
    }

    @Test
    public void shouldAppendTwoBuffersAndResize() {
        final int bufferLength = 128;
        final byte[] buffer = new byte[bufferLength];
        final int firstLength = (buffer.length) / 4;
        final int secondLength = (buffer.length) / 2;
        Arrays.fill(buffer, 0, (firstLength + secondLength), ((byte) (7)));
        final UnsafeBuffer srcBuffer = new UnsafeBuffer(buffer);
        final BufferBuilder bufferBuilder = new BufferBuilder((bufferLength / 2));
        bufferBuilder.append(srcBuffer, 0, firstLength);
        bufferBuilder.append(srcBuffer, firstLength, secondLength);
        final byte[] temp = new byte[buffer.length];
        bufferBuilder.buffer().getBytes(0, temp, 0, (secondLength + firstLength));
        Assert.assertThat(bufferBuilder.limit(), CoreMatchers.is((firstLength + secondLength)));
        Assert.assertThat(bufferBuilder.capacity(), Matchers.greaterThanOrEqualTo((firstLength + secondLength)));
        Assert.assertArrayEquals(temp, buffer);
    }

    @Test
    public void shouldCompactBufferToLowerLimit() {
        final int bufferLength = (BufferBuilderUtil.MIN_ALLOCATED_CAPACITY) / 2;
        final byte[] buffer = new byte[bufferLength];
        final UnsafeBuffer srcBuffer = new UnsafeBuffer(buffer);
        final BufferBuilder bufferBuilder = new BufferBuilder();
        final int bufferCount = 5;
        for (int i = 0; i < bufferCount; i++) {
            bufferBuilder.append(srcBuffer, 0, buffer.length);
        }
        final int expectedLimit = (buffer.length) * bufferCount;
        Assert.assertThat(bufferBuilder.limit(), CoreMatchers.is(expectedLimit));
        final int expandedCapacity = bufferBuilder.capacity();
        Assert.assertThat(expandedCapacity, Matchers.greaterThan(expectedLimit));
        bufferBuilder.reset();
        bufferBuilder.append(srcBuffer, 0, buffer.length);
        bufferBuilder.append(srcBuffer, 0, buffer.length);
        bufferBuilder.append(srcBuffer, 0, buffer.length);
        bufferBuilder.compact();
        Assert.assertThat(bufferBuilder.limit(), CoreMatchers.is(((buffer.length) * 3)));
        Assert.assertThat(bufferBuilder.capacity(), Matchers.lessThan(expandedCapacity));
    }
}

