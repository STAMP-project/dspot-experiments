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
 * Various tests with freed memory segments for {@link HeapMemorySegment} and {@link HybridMemorySegment} (in both heap and off-heap modes).
 */
public class OperationsOnFreedSegmentTest {
    private static final int PAGE_SIZE = ((int) (((Math.random()) * 10000) + 1000));

    @Test
    public void testSingleSegmentOperationsHeapSegment() throws Exception {
        testOpsOnFreedSegment(new HeapMemorySegment(new byte[OperationsOnFreedSegmentTest.PAGE_SIZE]));
        testOpsOnFreedSegment(new HybridMemorySegment(new byte[OperationsOnFreedSegmentTest.PAGE_SIZE]));
        testOpsOnFreedSegment(new HybridMemorySegment(ByteBuffer.allocateDirect(OperationsOnFreedSegmentTest.PAGE_SIZE)));
    }

    @Test
    public void testCompare() {
        MemorySegment aliveHeap = new HeapMemorySegment(new byte[OperationsOnFreedSegmentTest.PAGE_SIZE]);
        MemorySegment aliveHybridHeap = new HybridMemorySegment(new byte[OperationsOnFreedSegmentTest.PAGE_SIZE]);
        MemorySegment aliveHybridOffHeap = new HybridMemorySegment(ByteBuffer.allocateDirect(OperationsOnFreedSegmentTest.PAGE_SIZE));
        MemorySegment freedHeap = new HeapMemorySegment(new byte[OperationsOnFreedSegmentTest.PAGE_SIZE]);
        MemorySegment freedHybridHeap = new HybridMemorySegment(new byte[OperationsOnFreedSegmentTest.PAGE_SIZE]);
        MemorySegment freedHybridOffHeap = new HybridMemorySegment(ByteBuffer.allocateDirect(OperationsOnFreedSegmentTest.PAGE_SIZE));
        freedHeap.free();
        freedHybridHeap.free();
        freedHybridOffHeap.free();
        MemorySegment[] alive = new MemorySegment[]{ aliveHeap, aliveHybridHeap, aliveHybridOffHeap };
        MemorySegment[] free = new MemorySegment[]{ freedHeap, freedHybridHeap, freedHybridOffHeap };
        // alive with free
        for (MemorySegment seg1 : alive) {
            for (MemorySegment seg2 : free) {
                testCompare(seg1, seg2);
            }
        }
        // free with alive
        for (MemorySegment seg1 : free) {
            for (MemorySegment seg2 : alive) {
                testCompare(seg1, seg2);
            }
        }
        // free with free
        for (MemorySegment seg1 : free) {
            for (MemorySegment seg2 : free) {
                testCompare(seg1, seg2);
            }
        }
    }

    @Test
    public void testCopyTo() {
        MemorySegment aliveHeap = new HeapMemorySegment(new byte[OperationsOnFreedSegmentTest.PAGE_SIZE]);
        MemorySegment aliveHybridHeap = new HybridMemorySegment(new byte[OperationsOnFreedSegmentTest.PAGE_SIZE]);
        MemorySegment aliveHybridOffHeap = new HybridMemorySegment(ByteBuffer.allocateDirect(OperationsOnFreedSegmentTest.PAGE_SIZE));
        MemorySegment freedHeap = new HeapMemorySegment(new byte[OperationsOnFreedSegmentTest.PAGE_SIZE]);
        MemorySegment freedHybridHeap = new HybridMemorySegment(new byte[OperationsOnFreedSegmentTest.PAGE_SIZE]);
        MemorySegment freedHybridOffHeap = new HybridMemorySegment(ByteBuffer.allocateDirect(OperationsOnFreedSegmentTest.PAGE_SIZE));
        freedHeap.free();
        freedHybridHeap.free();
        freedHybridOffHeap.free();
        MemorySegment[] alive = new MemorySegment[]{ aliveHeap, aliveHybridHeap, aliveHybridOffHeap };
        MemorySegment[] free = new MemorySegment[]{ freedHeap, freedHybridHeap, freedHybridOffHeap };
        // alive with free
        for (MemorySegment seg1 : alive) {
            for (MemorySegment seg2 : free) {
                testCopy(seg1, seg2);
            }
        }
        // free with alive
        for (MemorySegment seg1 : free) {
            for (MemorySegment seg2 : alive) {
                testCopy(seg1, seg2);
            }
        }
        // free with free
        for (MemorySegment seg1 : free) {
            for (MemorySegment seg2 : free) {
                testCopy(seg1, seg2);
            }
        }
    }

    @Test
    public void testSwap() {
        MemorySegment aliveHeap = new HeapMemorySegment(new byte[OperationsOnFreedSegmentTest.PAGE_SIZE]);
        MemorySegment aliveHybridHeap = new HybridMemorySegment(new byte[OperationsOnFreedSegmentTest.PAGE_SIZE]);
        MemorySegment aliveHybridOffHeap = new HybridMemorySegment(ByteBuffer.allocateDirect(OperationsOnFreedSegmentTest.PAGE_SIZE));
        MemorySegment freedHeap = new HeapMemorySegment(new byte[OperationsOnFreedSegmentTest.PAGE_SIZE]);
        MemorySegment freedHybridHeap = new HybridMemorySegment(new byte[OperationsOnFreedSegmentTest.PAGE_SIZE]);
        MemorySegment freedHybridOffHeap = new HybridMemorySegment(ByteBuffer.allocateDirect(OperationsOnFreedSegmentTest.PAGE_SIZE));
        freedHeap.free();
        freedHybridHeap.free();
        freedHybridOffHeap.free();
        MemorySegment[] alive = new MemorySegment[]{ aliveHeap, aliveHybridHeap, aliveHybridOffHeap };
        MemorySegment[] free = new MemorySegment[]{ freedHeap, freedHybridHeap, freedHybridOffHeap };
        // alive with free
        for (MemorySegment seg1 : alive) {
            for (MemorySegment seg2 : free) {
                testSwap(seg1, seg2);
            }
        }
        // free with alive
        for (MemorySegment seg1 : free) {
            for (MemorySegment seg2 : alive) {
                testSwap(seg1, seg2);
            }
        }
        // free with free
        for (MemorySegment seg1 : free) {
            for (MemorySegment seg2 : free) {
                testSwap(seg1, seg2);
            }
        }
    }
}

