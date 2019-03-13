/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.io.network.buffer;


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.function.ToIntFunction;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link BufferBuilder}.
 */
public class BufferBuilderAndConsumerTest {
    private static final int BUFFER_INT_SIZE = 10;

    private static final int BUFFER_SIZE = (BufferBuilderAndConsumerTest.BUFFER_INT_SIZE) * (Integer.BYTES);

    @Test
    public void referenceCounting() {
        BufferBuilder bufferBuilder = BufferBuilderAndConsumerTest.createBufferBuilder();
        BufferConsumer bufferConsumer = bufferBuilder.createBufferConsumer();
        Assert.assertEquals((3 * (Integer.BYTES)), bufferBuilder.appendAndCommit(BufferBuilderAndConsumerTest.toByteBuffer(1, 2, 3)));
        Buffer buffer = bufferConsumer.build();
        Assert.assertFalse(buffer.isRecycled());
        buffer.recycleBuffer();
        Assert.assertFalse(buffer.isRecycled());
        bufferConsumer.close();
        Assert.assertTrue(buffer.isRecycled());
    }

    @Test
    public void append() {
        BufferBuilder bufferBuilder = BufferBuilderAndConsumerTest.createBufferBuilder();
        int[] intsToWrite = new int[]{ 0, 1, 2, 3, 42 };
        ByteBuffer bytesToWrite = BufferBuilderAndConsumerTest.toByteBuffer(intsToWrite);
        Assert.assertEquals(bytesToWrite.limit(), bufferBuilder.appendAndCommit(bytesToWrite));
        Assert.assertEquals(bytesToWrite.limit(), bytesToWrite.position());
        Assert.assertFalse(bufferBuilder.isFull());
        BufferBuilderAndConsumerTest.assertContent(bufferBuilder.createBufferConsumer(), intsToWrite);
    }

    @Test
    public void multipleAppends() {
        BufferBuilder bufferBuilder = BufferBuilderAndConsumerTest.createBufferBuilder();
        BufferConsumer bufferConsumer = bufferBuilder.createBufferConsumer();
        bufferBuilder.appendAndCommit(BufferBuilderAndConsumerTest.toByteBuffer(0, 1));
        bufferBuilder.appendAndCommit(BufferBuilderAndConsumerTest.toByteBuffer(2));
        bufferBuilder.appendAndCommit(BufferBuilderAndConsumerTest.toByteBuffer(3, 42));
        BufferBuilderAndConsumerTest.assertContent(bufferConsumer, 0, 1, 2, 3, 42);
    }

    @Test
    public void multipleNotCommittedAppends() {
        BufferBuilder bufferBuilder = BufferBuilderAndConsumerTest.createBufferBuilder();
        BufferConsumer bufferConsumer = bufferBuilder.createBufferConsumer();
        bufferBuilder.append(BufferBuilderAndConsumerTest.toByteBuffer(0, 1));
        bufferBuilder.append(BufferBuilderAndConsumerTest.toByteBuffer(2));
        bufferBuilder.append(BufferBuilderAndConsumerTest.toByteBuffer(3, 42));
        BufferBuilderAndConsumerTest.assertContent(bufferConsumer);
        bufferBuilder.commit();
        BufferBuilderAndConsumerTest.assertContent(bufferConsumer, 0, 1, 2, 3, 42);
    }

    @Test
    public void appendOverSize() {
        BufferBuilder bufferBuilder = BufferBuilderAndConsumerTest.createBufferBuilder();
        BufferConsumer bufferConsumer = bufferBuilder.createBufferConsumer();
        ByteBuffer bytesToWrite = BufferBuilderAndConsumerTest.toByteBuffer(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 42);
        Assert.assertEquals(BufferBuilderAndConsumerTest.BUFFER_SIZE, bufferBuilder.appendAndCommit(bytesToWrite));
        Assert.assertTrue(bufferBuilder.isFull());
        BufferBuilderAndConsumerTest.assertContent(bufferConsumer, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        bufferBuilder = BufferBuilderAndConsumerTest.createBufferBuilder();
        bufferConsumer = bufferBuilder.createBufferConsumer();
        Assert.assertEquals(Integer.BYTES, bufferBuilder.appendAndCommit(bytesToWrite));
        Assert.assertFalse(bufferBuilder.isFull());
        BufferBuilderAndConsumerTest.assertContent(bufferConsumer, 42);
    }

    @Test(expected = IllegalStateException.class)
    public void creatingBufferConsumerTwice() {
        BufferBuilder bufferBuilder = BufferBuilderAndConsumerTest.createBufferBuilder();
        bufferBuilder.createBufferConsumer();
        bufferBuilder.createBufferConsumer();
    }

    @Test
    public void copy() {
        BufferBuilder bufferBuilder = BufferBuilderAndConsumerTest.createBufferBuilder();
        BufferConsumer bufferConsumer1 = bufferBuilder.createBufferConsumer();
        bufferBuilder.appendAndCommit(BufferBuilderAndConsumerTest.toByteBuffer(0, 1));
        BufferConsumer bufferConsumer2 = bufferConsumer1.copy();
        bufferBuilder.appendAndCommit(BufferBuilderAndConsumerTest.toByteBuffer(2));
        BufferBuilderAndConsumerTest.assertContent(bufferConsumer1, 0, 1, 2);
        BufferBuilderAndConsumerTest.assertContent(bufferConsumer2, 0, 1, 2);
        BufferConsumer bufferConsumer3 = bufferConsumer1.copy();
        bufferBuilder.appendAndCommit(BufferBuilderAndConsumerTest.toByteBuffer(3, 42));
        BufferConsumer bufferConsumer4 = bufferConsumer1.copy();
        BufferBuilderAndConsumerTest.assertContent(bufferConsumer1, 3, 42);
        BufferBuilderAndConsumerTest.assertContent(bufferConsumer2, 3, 42);
        BufferBuilderAndConsumerTest.assertContent(bufferConsumer3, 3, 42);
        BufferBuilderAndConsumerTest.assertContent(bufferConsumer4, 3, 42);
    }

    @Test
    public void buildEmptyBuffer() {
        Buffer buffer = BufferBuilderTestUtils.buildSingleBuffer(BufferBuilderAndConsumerTest.createBufferBuilder());
        Assert.assertEquals(0, buffer.getSize());
        BufferBuilderAndConsumerTest.assertContent(buffer);
    }

    @Test
    public void buildingBufferMultipleTimes() {
        BufferBuilder bufferBuilder = BufferBuilderAndConsumerTest.createBufferBuilder();
        try (BufferConsumer bufferConsumer = bufferBuilder.createBufferConsumer()) {
            bufferBuilder.appendAndCommit(BufferBuilderAndConsumerTest.toByteBuffer(0, 1));
            bufferBuilder.appendAndCommit(BufferBuilderAndConsumerTest.toByteBuffer(2));
            BufferBuilderAndConsumerTest.assertContent(bufferConsumer, 0, 1, 2);
            bufferBuilder.appendAndCommit(BufferBuilderAndConsumerTest.toByteBuffer(3, 42));
            bufferBuilder.appendAndCommit(BufferBuilderAndConsumerTest.toByteBuffer(44));
            BufferBuilderAndConsumerTest.assertContent(bufferConsumer, 3, 42, 44);
            ArrayList<Integer> originalValues = new ArrayList<>();
            while (!(bufferBuilder.isFull())) {
                bufferBuilder.appendAndCommit(BufferBuilderAndConsumerTest.toByteBuffer(1337));
                originalValues.add(1337);
            } 
            BufferBuilderAndConsumerTest.assertContent(bufferConsumer, originalValues.stream().mapToInt(Integer::intValue).toArray());
        }
    }

    @Test
    public void emptyIsFinished() {
        BufferBuilderAndConsumerTest.testIsFinished(0);
    }

    @Test
    public void partiallyFullIsFinished() {
        BufferBuilderAndConsumerTest.testIsFinished(((BufferBuilderAndConsumerTest.BUFFER_INT_SIZE) / 2));
    }

    @Test
    public void fullIsFinished() {
        BufferBuilderAndConsumerTest.testIsFinished(BufferBuilderAndConsumerTest.BUFFER_INT_SIZE);
    }
}

