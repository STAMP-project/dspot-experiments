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
package org.apache.drill.exec.physical.impl.join;


import HashJoinMemoryCalculator.PartitionStatSet;
import HashJoinMemoryCalculatorImpl.PostBuildCalculationsImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.drill.common.map.CaseInsensitiveMap;
import org.apache.drill.shaded.guava.com.google.common.base.Preconditions;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import static HashTableSizeCalculatorConservativeImpl.HASHTABLE_DOUBLING_FACTOR;


public class TestPostBuildCalculationsImpl {
    @Test
    public void testProbeTooBig() {
        final int minProbeRecordsPerBatch = 10;
        final int computedProbeRecordsPerBatch = PostBuildCalculationsImpl.computeProbeRecordsPerBatch(100, 2, 100, minProbeRecordsPerBatch, 70, 40, 200);
        Assert.assertEquals(minProbeRecordsPerBatch, computedProbeRecordsPerBatch);
    }

    @Test
    public void testComputedShouldBeMin() {
        final int minProbeRecordsPerBatch = 10;
        final int computedProbeRecordsPerBatch = PostBuildCalculationsImpl.computeProbeRecordsPerBatch(100, 2, 100, minProbeRecordsPerBatch, 50, 40, 200);
        Assert.assertEquals(minProbeRecordsPerBatch, computedProbeRecordsPerBatch);
    }

    @Test
    public void testComputedProbeRecordsPerBatch() {
        final int minProbeRecordsPerBatch = 10;
        final int computedProbeRecordsPerBatch = PostBuildCalculationsImpl.computeProbeRecordsPerBatch(200, 2, 100, minProbeRecordsPerBatch, 50, 50, 200);
        Assert.assertEquals(25, computedProbeRecordsPerBatch);
    }

    @Test
    public void testComputedProbeRecordsPerBatchRoundUp() {
        final int minProbeRecordsPerBatch = 10;
        final int computedProbeRecordsPerBatch = PostBuildCalculationsImpl.computeProbeRecordsPerBatch(200, 2, 100, minProbeRecordsPerBatch, 50, 51, 199);
        Assert.assertEquals(25, computedProbeRecordsPerBatch);
    }

    @Test(expected = IllegalStateException.class)
    public void testHasProbeDataButProbeEmpty() {
        final Map<String, Long> keySizes = CaseInsensitiveMap.newHashMap();
        final PartitionStatImpl partition1 = new PartitionStatImpl();
        final PartitionStatImpl partition2 = new PartitionStatImpl();
        final HashJoinMemoryCalculator.PartitionStatSet buildPartitionStatSet = new HashJoinMemoryCalculator.PartitionStatSet(partition1, partition2);
        final int recordsPerPartitionBatchBuild = 10;
        addBatches(partition1, recordsPerPartitionBatchBuild, 10, 4);
        addBatches(partition2, recordsPerPartitionBatchBuild, 10, 4);
        final double fragmentationFactor = 2.0;
        final double safetyFactor = 1.5;
        final int maxBatchNumRecordsProbe = 3;
        final int recordsPerPartitionBatchProbe = 5;
        final long partitionProbeBatchSize = 15;
        final long maxProbeBatchSize = 60;
        final HashJoinMemoryCalculatorImpl.PostBuildCalculationsImpl calc = // memoryAvailable
        // maxOutputBatchSize
        // buildPartitionStatSet
        // keySizes
        // hashTableSizeCalculator
        // hashJoinHelperSizeCalculator
        // fragmentationFactor
        // safetyFactor
        // loadFactor
        new HashJoinMemoryCalculatorImpl.PostBuildCalculationsImpl(true, new TestPostBuildCalculationsImpl.ConditionalMockBatchSizePredictor(Lists.newArrayList(maxBatchNumRecordsProbe, recordsPerPartitionBatchProbe), Lists.newArrayList(maxProbeBatchSize, partitionProbeBatchSize), true), 290, 20, maxBatchNumRecordsProbe, recordsPerPartitionBatchProbe, buildPartitionStatSet, keySizes, new TestPostBuildCalculationsImpl.MockHashTableSizeCalculator(10), new TestPostBuildCalculationsImpl.MockHashJoinHelperSizeCalculator(10), fragmentationFactor, safetyFactor, 0.75, false, false);// reserveHash

        calc.initialize(true);
    }

    @Test
    public void testProbeEmpty() {
        final Map<String, Long> keySizes = CaseInsensitiveMap.newHashMap();
        final PartitionStatImpl partition1 = new PartitionStatImpl();
        final PartitionStatImpl partition2 = new PartitionStatImpl();
        final HashJoinMemoryCalculator.PartitionStatSet buildPartitionStatSet = new HashJoinMemoryCalculator.PartitionStatSet(partition1, partition2);
        final int recordsPerPartitionBatchBuild = 10;
        addBatches(partition1, recordsPerPartitionBatchBuild, 10, 4);
        addBatches(partition2, recordsPerPartitionBatchBuild, 10, 4);
        final double fragmentationFactor = 2.0;
        final double safetyFactor = 1.5;
        final int maxBatchNumRecordsProbe = 3;
        final int recordsPerPartitionBatchProbe = 5;
        final long partitionProbeBatchSize = 40;
        final long maxProbeBatchSize = 10000;
        final HashJoinMemoryCalculatorImpl.PostBuildCalculationsImpl calc = new HashJoinMemoryCalculatorImpl.PostBuildCalculationsImpl(true, new TestPostBuildCalculationsImpl.ConditionalMockBatchSizePredictor(), 50, 1000, maxBatchNumRecordsProbe, recordsPerPartitionBatchProbe, buildPartitionStatSet, keySizes, new TestPostBuildCalculationsImpl.MockHashTableSizeCalculator(10), new TestPostBuildCalculationsImpl.MockHashJoinHelperSizeCalculator(10), fragmentationFactor, safetyFactor, 0.75, true, false);
        calc.initialize(true);
        Assert.assertFalse(calc.shouldSpill());
        Assert.assertFalse(calc.shouldSpill());
    }

    @Test
    public void testHasNoProbeDataButProbeNonEmptyFirstCycle() {
        testHasNoProbeDataButProbeNonEmptyHelper(true);
    }

    @Test
    public void testHasNoProbeDataButProbeNonEmptyNotFirstCycle() {
        testHasNoProbeDataButProbeNonEmptyHelper(false);
    }

    @Test
    public void testProbingAndPartitioningBuildAllInMemoryNoSpillFirstCycle() {
        testProbingAndPartitioningBuildAllInMemoryNoSpillHelper(true);
    }

    @Test
    public void testProbingAndPartitioningBuildAllInMemoryNoSpillNotFirstCycle() {
        testProbingAndPartitioningBuildAllInMemoryNoSpillHelper(false);
    }

    @Test
    public void testProbingAndPartitioningBuildAllInMemorySpillFirstCycle() {
        testProbingAndPartitioningBuildAllInMemorySpillHelper(true);
    }

    @Test
    public void testProbingAndPartitioningBuildAllInMemorySpillNotFirstCycle() {
        testProbingAndPartitioningBuildAllInMemorySpillHelper(false);
    }

    @Test
    public void testProbingAndPartitioningBuildAllInMemoryNoSpillWithHashFirstCycle() {
        testProbingAndPartitioningBuildAllInMemoryNoSpillWithHashHelper(true);
    }

    @Test
    public void testProbingAndPartitioningBuildAllInMemoryNoSpillWithHashNotFirstCycle() {
        testProbingAndPartitioningBuildAllInMemoryNoSpillWithHashHelper(false);
    }

    @Test
    public void testProbingAndPartitioningBuildAllInMemoryWithSpillFirstCycle() {
        testProbingAndPartitioningBuildAllInMemoryWithSpillHelper(true);
    }

    @Test
    public void testProbingAndPartitioningBuildAllInMemoryWithSpillNotFirstCycle() {
        testProbingAndPartitioningBuildAllInMemoryWithSpillHelper(false);
    }

    @Test
    public void testProbingAndPartitioningBuildSomeInMemoryFirstCycle() {
        testProbingAndPartitioningBuildSomeInMemoryHelper(true);
    }

    @Test
    public void testProbingAndPartitioningBuildSomeInMemoryNotFirstCycle() {
        testProbingAndPartitioningBuildSomeInMemoryHelper(false);
    }

    @Test
    public void testProbingAndPartitioningBuildNoneInMemoryFirstCycle() {
        testProbingAndPartitioningBuildNoneInMemoryHelper(true);
    }

    @Test
    public void testProbingAndPartitioningBuildNoneInMemoryNotFirstCycle() {
        testProbingAndPartitioningBuildNoneInMemoryHelper(false);
    }

    // Make sure I don't fail
    @Test
    public void testMakeDebugString() {
        final Map<String, Long> keySizes = CaseInsensitiveMap.newHashMap();
        final PartitionStatImpl partition1 = new PartitionStatImpl();
        final PartitionStatImpl partition2 = new PartitionStatImpl();
        final PartitionStatImpl partition3 = new PartitionStatImpl();
        final PartitionStatImpl partition4 = new PartitionStatImpl();
        final HashJoinMemoryCalculator.PartitionStatSet buildPartitionStatSet = new HashJoinMemoryCalculator.PartitionStatSet(partition1, partition2, partition3, partition4);
        final int recordsPerPartitionBatchBuild = 10;
        partition1.spill();
        partition2.spill();
        addBatches(partition3, recordsPerPartitionBatchBuild, 10, 4);
        addBatches(partition4, recordsPerPartitionBatchBuild, 10, 4);
        final double fragmentationFactor = 2.0;
        final double safetyFactor = 1.5;
        final long hashTableSize = 10;
        final long hashJoinHelperSize = 10;
        final int maxBatchNumRecordsProbe = 3;
        final int recordsPerPartitionBatchProbe = 5;
        final long partitionProbeBatchSize = 15;
        final long maxProbeBatchSize = 60;
        HashJoinMemoryCalculatorImpl.PostBuildCalculationsImpl calc = new HashJoinMemoryCalculatorImpl.PostBuildCalculationsImpl(true, new TestPostBuildCalculationsImpl.ConditionalMockBatchSizePredictor(Lists.newArrayList(maxBatchNumRecordsProbe, recordsPerPartitionBatchProbe), Lists.newArrayList(maxProbeBatchSize, partitionProbeBatchSize), true), 230, 20, maxBatchNumRecordsProbe, recordsPerPartitionBatchProbe, buildPartitionStatSet, keySizes, new TestPostBuildCalculationsImpl.MockHashTableSizeCalculator(hashTableSize), new TestPostBuildCalculationsImpl.MockHashJoinHelperSizeCalculator(hashJoinHelperSize), fragmentationFactor, safetyFactor, 0.75, false, false);
        calc.initialize(false);
    }

    public static class MockHashTableSizeCalculator implements HashTableSizeCalculator {
        private final long size;

        public MockHashTableSizeCalculator(final long size) {
            this.size = size;
        }

        @Override
        public long calculateSize(HashJoinMemoryCalculator.PartitionStat partitionStat, Map<String, Long> keySizes, double loadFactor, double safetyFactor, double fragmentationFactor) {
            return size;
        }

        @Override
        public double getDoublingFactor() {
            return HASHTABLE_DOUBLING_FACTOR;
        }

        @Override
        public String getType() {
            return null;
        }
    }

    public static class MockHashJoinHelperSizeCalculator implements HashJoinHelperSizeCalculator {
        private final long size;

        public MockHashJoinHelperSizeCalculator(final long size) {
            this.size = size;
        }

        @Override
        public long calculateSize(HashJoinMemoryCalculator.PartitionStat partitionStat, double fragmentationFactor) {
            return size;
        }
    }

    public static class ConditionalMockBatchSizePredictor implements BatchSizePredictor {
        private final List<Integer> recordsPerBatch;

        private final List<Long> batchSize;

        private boolean hasData;

        private boolean updateable;

        public ConditionalMockBatchSizePredictor() {
            recordsPerBatch = new ArrayList<>();
            batchSize = new ArrayList<>();
            hasData = false;
            updateable = true;
        }

        public ConditionalMockBatchSizePredictor(final List<Integer> recordsPerBatch, final List<Long> batchSize, final boolean hasData) {
            this.recordsPerBatch = Preconditions.checkNotNull(recordsPerBatch);
            this.batchSize = Preconditions.checkNotNull(batchSize);
            Preconditions.checkArgument(((recordsPerBatch.size()) == (batchSize.size())));
            this.hasData = hasData;
            updateable = true;
        }

        @Override
        public long getBatchSize() {
            return batchSize.get(0);
        }

        @Override
        public int getNumRecords() {
            return 0;
        }

        @Override
        public boolean hadDataLastTime() {
            return hasData;
        }

        @Override
        public void updateStats() {
            Preconditions.checkState(updateable);
            updateable = false;
            hasData = true;
        }

        @Override
        public long predictBatchSize(int desiredNumRecords, boolean reserveHash) {
            Preconditions.checkState(hasData);
            for (int index = 0; index < (recordsPerBatch.size()); index++) {
                if (desiredNumRecords == (recordsPerBatch.get(index))) {
                    return batchSize.get(index);
                }
            }
            throw new IllegalArgumentException();
        }
    }
}

