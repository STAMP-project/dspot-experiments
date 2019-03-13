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
package org.apache.flink.core.memory;


import java.nio.ByteBuffer;
import org.junit.Test;


/**
 * Tests for undersized {@link HeapMemorySegment} and {@link HybridMemorySegment} (in both heap and
 * off-heap modes).
 */
public class MemorySegmentUndersizedTest {
    @Test
    public void testZeroSizeHeapSegment() {
        MemorySegment segment = new HeapMemorySegment(new byte[0]);
        MemorySegmentUndersizedTest.testZeroSizeBuffer(segment);
        MemorySegmentUndersizedTest.testSegmentWithSizeLargerZero(segment);
    }

    @Test
    public void testZeroSizeHeapHybridSegment() {
        MemorySegment segment = new HybridMemorySegment(new byte[0]);
        MemorySegmentUndersizedTest.testZeroSizeBuffer(segment);
        MemorySegmentUndersizedTest.testSegmentWithSizeLargerZero(segment);
    }

    @Test
    public void testZeroSizeOffHeapHybridSegment() {
        MemorySegment segment = new HybridMemorySegment(ByteBuffer.allocateDirect(0));
        MemorySegmentUndersizedTest.testZeroSizeBuffer(segment);
        MemorySegmentUndersizedTest.testSegmentWithSizeLargerZero(segment);
    }

    @Test
    public void testSizeOneHeapSegment() {
        MemorySegmentUndersizedTest.testSegmentWithSizeLargerZero(new HeapMemorySegment(new byte[1]));
    }

    @Test
    public void testSizeOneHeapHybridSegment() {
        MemorySegmentUndersizedTest.testSegmentWithSizeLargerZero(new HybridMemorySegment(new byte[1]));
    }

    @Test
    public void testSizeOneOffHeapHybridSegment() {
        MemorySegmentUndersizedTest.testSegmentWithSizeLargerZero(new HybridMemorySegment(ByteBuffer.allocateDirect(1)));
    }
}

