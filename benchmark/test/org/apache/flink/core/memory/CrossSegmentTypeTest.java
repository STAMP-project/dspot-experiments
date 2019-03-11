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


import MemoryUtils.UNSAFE;
import java.nio.ByteBuffer;
import java.util.Random;
import org.junit.Test;


/**
 * Verifies interoperability between {@link HeapMemorySegment} and {@link HybridMemorySegment} (in
 * both heap and off-heap modes).
 */
public class CrossSegmentTypeTest {
    private static final long BYTE_ARRAY_BASE_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);

    private final int pageSize = 32 * 1024;

    // ------------------------------------------------------------------------
    @Test
    public void testCompareBytesMixedSegments() {
        MemorySegment[] segs1 = new MemorySegment[]{ new HeapMemorySegment(new byte[pageSize]), new HybridMemorySegment(new byte[pageSize]), new HybridMemorySegment(ByteBuffer.allocateDirect(pageSize)) };
        MemorySegment[] segs2 = new MemorySegment[]{ new HeapMemorySegment(new byte[pageSize]), new HybridMemorySegment(new byte[pageSize]), new HybridMemorySegment(ByteBuffer.allocateDirect(pageSize)) };
        Random rnd = new Random();
        for (MemorySegment seg1 : segs1) {
            for (MemorySegment seg2 : segs2) {
                testCompare(seg1, seg2, rnd);
            }
        }
    }

    @Test
    public void testSwapBytesMixedSegments() {
        final int halfPageSize = (pageSize) / 2;
        MemorySegment[] segs1 = new MemorySegment[]{ new HeapMemorySegment(new byte[pageSize]), new HybridMemorySegment(new byte[pageSize]), new HybridMemorySegment(ByteBuffer.allocateDirect(pageSize)) };
        MemorySegment[] segs2 = new MemorySegment[]{ new HeapMemorySegment(new byte[halfPageSize]), new HybridMemorySegment(new byte[halfPageSize]), new HybridMemorySegment(ByteBuffer.allocateDirect(halfPageSize)) };
        Random rnd = new Random();
        for (MemorySegment seg1 : segs1) {
            for (MemorySegment seg2 : segs2) {
                testSwap(seg1, seg2, rnd, halfPageSize);
            }
        }
    }

    @Test
    public void testCopyMixedSegments() {
        MemorySegment[] segs1 = new MemorySegment[]{ new HeapMemorySegment(new byte[pageSize]), new HybridMemorySegment(new byte[pageSize]), new HybridMemorySegment(ByteBuffer.allocateDirect(pageSize)) };
        MemorySegment[] segs2 = new MemorySegment[]{ new HeapMemorySegment(new byte[pageSize]), new HybridMemorySegment(new byte[pageSize]), new HybridMemorySegment(ByteBuffer.allocateDirect(pageSize)) };
        Random rnd = new Random();
        for (MemorySegment seg1 : segs1) {
            for (MemorySegment seg2 : segs2) {
                testCopy(seg1, seg2, rnd);
            }
        }
    }
}

