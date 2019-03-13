/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.vector;


import MinorType.VARCHAR;
import NullableVarCharVector.Mutator;
import org.apache.drill.common.config.DrillConfig;
import org.apache.drill.common.types.Types;
import org.apache.drill.exec.memory.BufferAllocator;
import org.apache.drill.exec.memory.RootAllocatorFactory;
import org.apache.drill.exec.record.MaterializedField;
import org.apache.drill.exec.record.TransferPair;
import org.apache.drill.exec.vector.NullableVarCharVector.Accessor;
import org.junit.Assert;
import org.junit.Test;


public class TestSplitAndTransfer {
    @Test
    public void test() throws Exception {
        final DrillConfig drillConfig = DrillConfig.create();
        final BufferAllocator allocator = RootAllocatorFactory.newRoot(drillConfig);
        final MaterializedField field = MaterializedField.create("field", Types.optional(VARCHAR));
        final NullableVarCharVector varCharVector = new NullableVarCharVector(field, allocator);
        varCharVector.allocateNew(10000, 1000);
        final int valueCount = 500;
        final String[] compareArray = new String[valueCount];
        final NullableVarCharVector.Mutator mutator = varCharVector.getMutator();
        for (int i = 0; i < valueCount; i += 3) {
            final String s = String.format("%010d", i);
            mutator.set(i, s.getBytes());
            compareArray[i] = s;
        }
        mutator.setValueCount(valueCount);
        final TransferPair tp = varCharVector.getTransferPair(allocator);
        final NullableVarCharVector newVarCharVector = ((NullableVarCharVector) (tp.getTo()));
        final Accessor accessor = newVarCharVector.getAccessor();
        final int[][] startLengths = new int[][]{ new int[]{ 0, 201 }, new int[]{ 201, 200 }, new int[]{ 401, 99 } };
        for (final int[] startLength : startLengths) {
            final int start = startLength[0];
            final int length = startLength[1];
            tp.splitAndTransfer(start, length);
            newVarCharVector.getMutator().setValueCount(length);
            for (int i = 0; i < length; i++) {
                final boolean expectedSet = ((start + i) % 3) == 0;
                if (expectedSet) {
                    final byte[] expectedValue = compareArray[(start + i)].getBytes();
                    Assert.assertFalse(accessor.isNull(i));
                    Assert.assertArrayEquals(expectedValue, accessor.get(i));
                } else {
                    Assert.assertTrue(accessor.isNull(i));
                }
            }
            newVarCharVector.clear();
        }
        varCharVector.close();
        allocator.close();
    }

    /**
     * BitVector tests
     */
    enum TestBitPattern {

        ZERO,
        ONE,
        ALTERNATING,
        RANDOM;}

    @Test
    public void testBitVectorUnalignedStart() throws Exception {
        testBitVectorImpl(16, new int[][]{ new int[]{ 2, 4 } }, TestSplitAndTransfer.TestBitPattern.RANDOM);
        testBitVectorImpl(16, new int[][]{ new int[]{ 2, 4 } }, TestSplitAndTransfer.TestBitPattern.ONE);
        testBitVectorImpl(16, new int[][]{ new int[]{ 2, 4 } }, TestSplitAndTransfer.TestBitPattern.ZERO);
        testBitVectorImpl(16, new int[][]{ new int[]{ 2, 4 } }, TestSplitAndTransfer.TestBitPattern.ALTERNATING);
        testBitVectorImpl(4096, new int[][]{ new int[]{ 4092, 4 } }, TestSplitAndTransfer.TestBitPattern.ONE);
        testBitVectorImpl(4096, new int[][]{ new int[]{ 4092, 4 } }, TestSplitAndTransfer.TestBitPattern.ZERO);
        testBitVectorImpl(4096, new int[][]{ new int[]{ 4092, 4 } }, TestSplitAndTransfer.TestBitPattern.ALTERNATING);
        testBitVectorImpl(4096, new int[][]{ new int[]{ 4092, 4 } }, TestSplitAndTransfer.TestBitPattern.RANDOM);
        testBitVectorImpl(4096, new int[][]{ new int[]{ 1020, 8 } }, TestSplitAndTransfer.TestBitPattern.ONE);
        testBitVectorImpl(4096, new int[][]{ new int[]{ 1020, 8 } }, TestSplitAndTransfer.TestBitPattern.ZERO);
        testBitVectorImpl(4096, new int[][]{ new int[]{ 1020, 8 } }, TestSplitAndTransfer.TestBitPattern.ALTERNATING);
        testBitVectorImpl(4096, new int[][]{ new int[]{ 1020, 8 } }, TestSplitAndTransfer.TestBitPattern.RANDOM);
        testBitVectorImpl(24, new int[][]{ new int[]{ 5, 17 } }, TestSplitAndTransfer.TestBitPattern.ONE);
        testBitVectorImpl(24, new int[][]{ new int[]{ 5, 17 } }, TestSplitAndTransfer.TestBitPattern.ZERO);
        testBitVectorImpl(24, new int[][]{ new int[]{ 5, 17 } }, TestSplitAndTransfer.TestBitPattern.ALTERNATING);
        testBitVectorImpl(24, new int[][]{ new int[]{ 5, 17 } }, TestSplitAndTransfer.TestBitPattern.RANDOM);
        testBitVectorImpl(3443, new int[][]{ new int[]{ 0, 2047 }, new int[]{ 2047, 1396 } }, TestSplitAndTransfer.TestBitPattern.ZERO);
        testBitVectorImpl(3443, new int[][]{ new int[]{ 0, 2047 }, new int[]{ 2047, 1396 } }, TestSplitAndTransfer.TestBitPattern.ONE);
        testBitVectorImpl(3443, new int[][]{ new int[]{ 0, 2047 }, new int[]{ 2047, 1396 } }, TestSplitAndTransfer.TestBitPattern.ALTERNATING);
        testBitVectorImpl(3443, new int[][]{ new int[]{ 0, 2047 }, new int[]{ 2047, 1396 } }, TestSplitAndTransfer.TestBitPattern.RANDOM);
        testBitVectorImpl(3447, new int[][]{ new int[]{ 0, 2047 }, new int[]{ 2047, 1400 } }, TestSplitAndTransfer.TestBitPattern.ZERO);
        testBitVectorImpl(3447, new int[][]{ new int[]{ 0, 2047 }, new int[]{ 2047, 1400 } }, TestSplitAndTransfer.TestBitPattern.ONE);
        testBitVectorImpl(3447, new int[][]{ new int[]{ 0, 2047 }, new int[]{ 2047, 1400 } }, TestSplitAndTransfer.TestBitPattern.ALTERNATING);
        testBitVectorImpl(3447, new int[][]{ new int[]{ 0, 2047 }, new int[]{ 2047, 1400 } }, TestSplitAndTransfer.TestBitPattern.RANDOM);
    }

    @Test
    public void testBitVectorAlignedStart() throws Exception {
        testBitVectorImpl(32, new int[][]{ new int[]{ 0, 4 } }, TestSplitAndTransfer.TestBitPattern.RANDOM);
        testBitVectorImpl(32, new int[][]{ new int[]{ 0, 4 } }, TestSplitAndTransfer.TestBitPattern.ONE);
        testBitVectorImpl(32, new int[][]{ new int[]{ 0, 4 } }, TestSplitAndTransfer.TestBitPattern.ZERO);
        testBitVectorImpl(32, new int[][]{ new int[]{ 0, 4 } }, TestSplitAndTransfer.TestBitPattern.ALTERNATING);
        testBitVectorImpl(32, new int[][]{ new int[]{ 0, 8 } }, TestSplitAndTransfer.TestBitPattern.ONE);
        testBitVectorImpl(32, new int[][]{ new int[]{ 0, 8 } }, TestSplitAndTransfer.TestBitPattern.ZERO);
        testBitVectorImpl(32, new int[][]{ new int[]{ 0, 8 } }, TestSplitAndTransfer.TestBitPattern.ALTERNATING);
        testBitVectorImpl(32, new int[][]{ new int[]{ 0, 8 } }, TestSplitAndTransfer.TestBitPattern.RANDOM);
        testBitVectorImpl(24, new int[][]{ new int[]{ 0, 17 } }, TestSplitAndTransfer.TestBitPattern.ONE);
        testBitVectorImpl(24, new int[][]{ new int[]{ 0, 17 } }, TestSplitAndTransfer.TestBitPattern.ZERO);
        testBitVectorImpl(24, new int[][]{ new int[]{ 0, 17 } }, TestSplitAndTransfer.TestBitPattern.ALTERNATING);
        testBitVectorImpl(24, new int[][]{ new int[]{ 0, 17 } }, TestSplitAndTransfer.TestBitPattern.RANDOM);
        testBitVectorImpl(3444, new int[][]{ new int[]{ 0, 2048 }, new int[]{ 2048, 1396 } }, TestSplitAndTransfer.TestBitPattern.ZERO);
        testBitVectorImpl(3444, new int[][]{ new int[]{ 0, 2048 }, new int[]{ 2048, 1396 } }, TestSplitAndTransfer.TestBitPattern.ONE);
        testBitVectorImpl(3444, new int[][]{ new int[]{ 0, 2048 }, new int[]{ 2048, 1396 } }, TestSplitAndTransfer.TestBitPattern.ALTERNATING);
        testBitVectorImpl(3444, new int[][]{ new int[]{ 0, 2048 }, new int[]{ 2048, 1396 } }, TestSplitAndTransfer.TestBitPattern.RANDOM);
        testBitVectorImpl(3448, new int[][]{ new int[]{ 0, 2048 }, new int[]{ 2048, 1400 } }, TestSplitAndTransfer.TestBitPattern.ZERO);
        testBitVectorImpl(3448, new int[][]{ new int[]{ 0, 2048 }, new int[]{ 2048, 1400 } }, TestSplitAndTransfer.TestBitPattern.ONE);
        testBitVectorImpl(3448, new int[][]{ new int[]{ 0, 2048 }, new int[]{ 2048, 1400 } }, TestSplitAndTransfer.TestBitPattern.ALTERNATING);
        testBitVectorImpl(3448, new int[][]{ new int[]{ 0, 2048 }, new int[]{ 2048, 1400 } }, TestSplitAndTransfer.TestBitPattern.RANDOM);
    }
}

