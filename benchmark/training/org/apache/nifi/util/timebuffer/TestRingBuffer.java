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
package org.apache.nifi.util.timebuffer;


import IterationDirection.BACKWARD;
import IterationDirection.FORWARD;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.nifi.util.RingBuffer;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class TestRingBuffer {
    @Test
    public void testGetNewestElement() {
        final RingBuffer<Integer> ringBuffer = new RingBuffer(10);
        for (int i = 0; i < 11; i++) {
            ringBuffer.add(i);
            Assert.assertEquals(i, ringBuffer.getNewestElement().intValue());
        }
    }

    @Test
    public void testAsList() {
        final RingBuffer<Integer> ringBuffer = new RingBuffer(10);
        final List<Integer> emptyList = ringBuffer.asList();
        Assert.assertTrue(emptyList.isEmpty());
        for (int i = 0; i < 3; i++) {
            ringBuffer.add(i);
        }
        List<Integer> list = ringBuffer.asList();
        Assert.assertEquals(3, list.size());
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(Integer.valueOf(i), list.get(i));
        }
        for (int i = 3; i < 10; i++) {
            ringBuffer.add(i);
        }
        list = ringBuffer.asList();
        Assert.assertEquals(10, list.size());
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(Integer.valueOf(i), list.get(i));
        }
    }

    @Test
    public void testIterateForwards() {
        final RingBuffer<Integer> ringBuffer = new RingBuffer(10);
        final int[] values = new int[]{ 3, 5, 20, 7 };
        for (final int v : values) {
            ringBuffer.add(v);
        }
        final AtomicInteger countHolder = new AtomicInteger(0);
        ringBuffer.forEach(new org.apache.nifi.util.RingBuffer.ForEachEvaluator<Integer>() {
            int counter = 0;

            @Override
            public boolean evaluate(final Integer value) {
                final int expected = values[((counter)++)];
                countHolder.incrementAndGet();
                Assert.assertEquals(expected, value.intValue());
                return true;
            }
        }, FORWARD);
        Assert.assertEquals(4, countHolder.get());
    }

    @Test
    public void testIterateForwardsAfterFull() {
        final RingBuffer<Integer> ringBuffer = new RingBuffer(10);
        for (int i = 0; i < 12; i++) {
            ringBuffer.add(i);
        }
        final int[] values = new int[]{ 3, 5, 20, 7 };
        for (final int v : values) {
            ringBuffer.add(v);
        }
        ringBuffer.forEach(new org.apache.nifi.util.RingBuffer.ForEachEvaluator<Integer>() {
            int counter = 0;

            @Override
            public boolean evaluate(final Integer value) {
                if ((counter) < 6) {
                    Assert.assertEquals(((counter) + 6), value.intValue());
                } else {
                    final int expected = values[((counter) - 6)];
                    Assert.assertEquals(expected, value.intValue());
                }
                (counter)++;
                return true;
            }
        }, FORWARD);
    }

    @Test
    public void testIterateBackwards() {
        final RingBuffer<Integer> ringBuffer = new RingBuffer(10);
        final int[] values = new int[]{ 3, 5, 20, 7 };
        for (final int v : values) {
            ringBuffer.add(v);
        }
        final AtomicInteger countHolder = new AtomicInteger(0);
        ringBuffer.forEach(new org.apache.nifi.util.RingBuffer.ForEachEvaluator<Integer>() {
            int counter = 0;

            @Override
            public boolean evaluate(final Integer value) {
                final int index = ((values.length) - 1) - (counter);
                final int expected = values[index];
                countHolder.incrementAndGet();
                Assert.assertEquals(expected, value.intValue());
                (counter)++;
                return true;
            }
        }, BACKWARD);
        Assert.assertEquals(4, countHolder.get());
    }

    @Test
    public void testIterateBackwardsAfterFull() {
        final RingBuffer<Integer> ringBuffer = new RingBuffer(10);
        for (int i = 0; i < 12; i++) {
            ringBuffer.add(i);
        }
        final int[] values = new int[]{ 3, 5, 20, 7 };
        for (final int v : values) {
            ringBuffer.add(v);
        }
        ringBuffer.forEach(new org.apache.nifi.util.RingBuffer.ForEachEvaluator<Integer>() {
            int counter = 0;

            @Override
            public boolean evaluate(final Integer value) {
                if ((counter) < (values.length)) {
                    final int index = ((values.length) - 1) - (counter);
                    final int expected = values[index];
                    Assert.assertEquals(expected, value.intValue());
                    (counter)++;
                }
                return true;
            }
        }, BACKWARD);
    }
}

