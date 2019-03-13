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
package org.apache.drill.exec.physical.impl.xsort.managed;


import ExecConstants.EXTERNAL_SORT_BATCH_LIMIT;
import ExecConstants.EXTERNAL_SORT_MAX_MEMORY;
import ExecConstants.EXTERNAL_SORT_MERGE_LIMIT;
import ExecConstants.EXTERNAL_SORT_MSORT_MAX_BATCHSIZE;
import ExecConstants.EXTERNAL_SORT_SPILL_BATCH_SIZE;
import ExecConstants.EXTERNAL_SORT_SPILL_FILE_SIZE;
import ExecConstants.OUTPUT_BATCH_SIZE;
import ExternalSortBatch.Metric.MERGE_COUNT;
import ExternalSortBatch.Metric.MIN_BUFFER;
import ExternalSortBatch.Metric.PEAK_BATCHES_IN_MEMORY;
import ExternalSortBatch.Metric.SPILL_COUNT;
import ExternalSortBatch.Metric.SPILL_MB;
import MergeAction.MERGE;
import MergeAction.NONE;
import MergeAction.SPILL;
import SortConfig.DEFAULT_MERGE_LIMIT;
import SortConfig.MIN_MERGE_BATCH_SIZE;
import SortConfig.MIN_MERGE_LIMIT;
import SortConfig.MIN_SPILL_BATCH_SIZE;
import SortConfig.MIN_SPILL_FILE_SIZE;
import SortMemoryManager.PAYLOAD_FROM_BUFFER;
import org.apache.drill.categories.OperatorTest;
import org.apache.drill.exec.ops.FragmentContext;
import org.apache.drill.exec.ops.OperatorStats;
import org.apache.drill.exec.physical.impl.xsort.managed.SortMemoryManager.MergeTask;
import org.apache.drill.test.BaseDirTestWatcher;
import org.apache.drill.test.OperatorFixture;
import org.apache.drill.test.SubOperatorTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static SortConfig.MIN_MERGE_BATCH_SIZE;
import static SortConfig.MIN_MERGE_LIMIT;
import static SortConfig.MIN_SPILL_BATCH_SIZE;
import static SortConfig.MIN_SPILL_FILE_SIZE;


@Category(OperatorTest.class)
public class TestExternalSortInternals extends SubOperatorTest {
    private static final int ONE_MEG = 1024 * 1024;

    @Rule
    public final BaseDirTestWatcher watcher = new BaseDirTestWatcher();

    /**
     * Verify defaults configured in drill-override.conf.
     */
    @Test
    public void testConfigDefaults() {
        SortConfig sortConfig = new SortConfig(SubOperatorTest.fixture.getFragmentContext().getConfig(), SubOperatorTest.fixture.getOptionManager());
        // Zero means no artificial limit
        Assert.assertEquals(0, sortConfig.maxMemory());
        // Zero mapped to large number
        Assert.assertEquals(DEFAULT_MERGE_LIMIT, sortConfig.mergeLimit());
        // Default size: 256 MiB
        Assert.assertEquals((256 * (TestExternalSortInternals.ONE_MEG)), sortConfig.spillFileSize());
        // Default size: 1 MiB
        Assert.assertEquals(TestExternalSortInternals.ONE_MEG, sortConfig.spillBatchSize());
        // Default size: 16 MiB
        Assert.assertEquals((16 * (TestExternalSortInternals.ONE_MEG)), sortConfig.mergeBatchSize());
        // Default: unlimited
        Assert.assertEquals(Integer.MAX_VALUE, sortConfig.getBufferedBatchLimit());
        // Default: 64K
        Assert.assertEquals(Character.MAX_VALUE, sortConfig.getMSortBatchSize());
    }

    /**
     * Verify that the various constants do, in fact, map to the
     * expected properties, and that the properties are overridden.
     */
    @Test
    public void testConfigOverride() {
        // Verify the various HOCON ways of setting memory
        OperatorFixture.Builder builder = new OperatorFixture.Builder(watcher);
        builder.configBuilder().put(EXTERNAL_SORT_MAX_MEMORY, "2000K").put(EXTERNAL_SORT_MERGE_LIMIT, 10).put(EXTERNAL_SORT_SPILL_FILE_SIZE, "10M").put(EXTERNAL_SORT_SPILL_BATCH_SIZE, 500000).put(EXTERNAL_SORT_BATCH_LIMIT, 50).put(EXTERNAL_SORT_MSORT_MAX_BATCHSIZE, 10).build();
        FragmentContext fragmentContext = builder.build().getFragmentContext();
        fragmentContext.getOptions().setLocalOption(OUTPUT_BATCH_SIZE, 600000);
        SortConfig sortConfig = new SortConfig(fragmentContext.getConfig(), fragmentContext.getOptions());
        Assert.assertEquals((2000 * 1024), sortConfig.maxMemory());
        Assert.assertEquals(10, sortConfig.mergeLimit());
        Assert.assertEquals((10 * (TestExternalSortInternals.ONE_MEG)), sortConfig.spillFileSize());
        Assert.assertEquals(500000, sortConfig.spillBatchSize());
        Assert.assertEquals(600000, sortConfig.mergeBatchSize());
        Assert.assertEquals(50, sortConfig.getBufferedBatchLimit());
        Assert.assertEquals(10, sortConfig.getMSortBatchSize());
    }

    /**
     * Some properties have hard-coded limits. Verify these limits.
     */
    @Test
    public void testConfigLimits() {
        OperatorFixture.Builder builder = new OperatorFixture.Builder(watcher);
        builder.configBuilder().put(EXTERNAL_SORT_MERGE_LIMIT, ((MIN_MERGE_LIMIT) - 1)).put(EXTERNAL_SORT_SPILL_FILE_SIZE, ((MIN_SPILL_FILE_SIZE) - 1)).put(EXTERNAL_SORT_SPILL_BATCH_SIZE, ((MIN_SPILL_BATCH_SIZE) - 1)).put(EXTERNAL_SORT_BATCH_LIMIT, 1).put(EXTERNAL_SORT_MSORT_MAX_BATCHSIZE, 0).build();
        FragmentContext fragmentContext = builder.build().getFragmentContext();
        fragmentContext.getOptions().setLocalOption(OUTPUT_BATCH_SIZE, ((MIN_MERGE_BATCH_SIZE) - 1));
        SortConfig sortConfig = new SortConfig(fragmentContext.getConfig(), fragmentContext.getOptions());
        Assert.assertEquals(MIN_MERGE_LIMIT, sortConfig.mergeLimit());
        Assert.assertEquals(MIN_SPILL_FILE_SIZE, sortConfig.spillFileSize());
        Assert.assertEquals(MIN_SPILL_BATCH_SIZE, sortConfig.spillBatchSize());
        Assert.assertEquals(MIN_MERGE_BATCH_SIZE, sortConfig.mergeBatchSize());
        Assert.assertEquals(2, sortConfig.getBufferedBatchLimit());
        Assert.assertEquals(1, sortConfig.getMSortBatchSize());
    }

    @Test
    public void testMemoryManagerBasics() {
        SortConfig sortConfig = new SortConfig(SubOperatorTest.fixture.getFragmentContext().getConfig(), SubOperatorTest.fixture.getFragmentContext().getOptions());
        long memoryLimit = 70 * (TestExternalSortInternals.ONE_MEG);
        SortMemoryManager memManager = new SortMemoryManager(sortConfig, memoryLimit);
        // Basic setup
        Assert.assertEquals(sortConfig.spillBatchSize(), memManager.getPreferredSpillBatchSize());
        Assert.assertEquals(sortConfig.mergeBatchSize(), memManager.getPreferredMergeBatchSize());
        Assert.assertEquals(memoryLimit, memManager.getMemoryLimit());
        // Nice simple batch: 6 MB in size, 300 byte rows, vectors half full
        // so 10000 rows. Sizes chosen so that spill and merge batch record
        // stay below the limit of 64K.
        int rowWidth = 300;
        int rowCount = 10000;
        int batchSize = (rowWidth * rowCount) * 2;
        Assert.assertTrue(memManager.updateEstimates(batchSize, rowWidth, rowCount));
        verifyCalcs(sortConfig, memoryLimit, memManager, batchSize, rowWidth, rowCount);
        // Zero rows - no update
        Assert.assertFalse(memManager.updateEstimates(batchSize, rowWidth, 0));
        Assert.assertEquals(rowWidth, memManager.getRowWidth());
        Assert.assertEquals(batchSize, memManager.getInputBatchSize().dataSize);
        // Larger batch size, update batch size
        rowCount = 20000;
        batchSize = (rowWidth * rowCount) * 2;
        Assert.assertTrue(memManager.updateEstimates(batchSize, rowWidth, rowCount));
        verifyCalcs(sortConfig, memoryLimit, memManager, batchSize, rowWidth, rowCount);
        // Smaller batch size: no change
        rowCount = 5000;
        int lowBatchSize = (rowWidth * rowCount) * 2;
        Assert.assertFalse(memManager.updateEstimates(lowBatchSize, rowWidth, rowCount));
        Assert.assertEquals(rowWidth, memManager.getRowWidth());
        Assert.assertEquals(batchSize, memManager.getInputBatchSize().dataSize);
        // Different batch density, update batch size
        rowCount = 10000;
        batchSize = (rowWidth * rowCount) * 5;
        Assert.assertTrue(memManager.updateEstimates(batchSize, rowWidth, rowCount));
        verifyCalcs(sortConfig, memoryLimit, memManager, batchSize, rowWidth, rowCount);
        // Smaller row size, no update
        int lowRowWidth = 200;
        rowCount = 10000;
        lowBatchSize = (rowWidth * rowCount) * 2;
        Assert.assertFalse(memManager.updateEstimates(lowBatchSize, lowRowWidth, rowCount));
        Assert.assertEquals(rowWidth, memManager.getRowWidth());
        Assert.assertEquals(batchSize, memManager.getInputBatchSize().dataSize);
        // Larger row size, updates calcs
        rowWidth = 400;
        rowCount = 10000;
        lowBatchSize = (rowWidth * rowCount) * 2;
        Assert.assertTrue(memManager.updateEstimates(lowBatchSize, rowWidth, rowCount));
        verifyCalcs(sortConfig, memoryLimit, memManager, batchSize, rowWidth, rowCount);
        // EOF: very low density
        Assert.assertFalse(memManager.updateEstimates(lowBatchSize, rowWidth, 5));
        Assert.assertEquals(rowWidth, memManager.getRowWidth());
        Assert.assertEquals(batchSize, memManager.getInputBatchSize().dataSize);
    }

    @Test
    public void testSmallRows() {
        SortConfig sortConfig = new SortConfig(SubOperatorTest.fixture.getFragmentContext().getConfig(), SubOperatorTest.fixture.getFragmentContext().getOptions());
        long memoryLimit = 100 * (TestExternalSortInternals.ONE_MEG);
        SortMemoryManager memManager = new SortMemoryManager(sortConfig, memoryLimit);
        // Zero-length row, round to 10
        int rowWidth = 0;
        int rowCount = 10000;
        int batchSize = rowCount * 2;
        memManager.updateEstimates(batchSize, rowWidth, rowCount);
        Assert.assertEquals(10, memManager.getRowWidth());
        Assert.assertEquals(batchSize, memManager.getInputBatchSize().dataSize);
        // Truncate spill, merge batch row count
        Assert.assertEquals(Character.MAX_VALUE, memManager.getSpillBatchRowCount());
        Assert.assertEquals(Character.MAX_VALUE, memManager.getMergeBatchRowCount());
        // But leave batch sizes at their defaults
        Assert.assertEquals(sortConfig.spillBatchSize(), memManager.getPreferredSpillBatchSize());
        Assert.assertEquals(sortConfig.mergeBatchSize(), memManager.getPreferredMergeBatchSize());
        // Small, but non-zero, row
        rowWidth = 10;
        rowCount = 10000;
        batchSize = rowWidth * rowCount;
        memManager.updateEstimates(batchSize, rowWidth, rowCount);
        Assert.assertEquals(rowWidth, memManager.getRowWidth());
        Assert.assertEquals(batchSize, memManager.getInputBatchSize().dataSize);
        // Truncate spill, merge batch row count
        Assert.assertEquals(Character.MAX_VALUE, memManager.getSpillBatchRowCount());
        Assert.assertEquals(Character.MAX_VALUE, memManager.getMergeBatchRowCount());
        // But leave batch sizes at their defaults
        Assert.assertEquals(sortConfig.spillBatchSize(), memManager.getPreferredSpillBatchSize());
        Assert.assertEquals(sortConfig.mergeBatchSize(), memManager.getPreferredMergeBatchSize());
    }

    @Test
    public void testLowMemory() {
        SortConfig sortConfig = new SortConfig(SubOperatorTest.fixture.getFragmentContext().getConfig(), SubOperatorTest.fixture.getFragmentContext().getOptions());
        int memoryLimit = 10 * (TestExternalSortInternals.ONE_MEG);
        SortMemoryManager memManager = new SortMemoryManager(sortConfig, memoryLimit);
        // Tight squeeze, but can be made to work.
        // Input batch buffer size is a quarter of memory.
        int rowWidth = 1000;
        int batchSize = SortMemoryManager.multiply((memoryLimit / 4), PAYLOAD_FROM_BUFFER);
        int rowCount = batchSize / rowWidth;
        batchSize = rowCount * rowWidth;
        memManager.updateEstimates(batchSize, rowWidth, rowCount);
        Assert.assertEquals(rowWidth, memManager.getRowWidth());
        Assert.assertEquals(batchSize, memManager.getInputBatchSize().dataSize);
        Assert.assertFalse(memManager.mayOverflow());
        Assert.assertTrue(memManager.hasPerformanceWarning());
        // Spill, merge batches should be constrained
        int spillBatchSize = memManager.getSpillBatchSize().dataSize;
        Assert.assertTrue((spillBatchSize < (memManager.getPreferredSpillBatchSize())));
        Assert.assertTrue((spillBatchSize >= rowWidth));
        Assert.assertTrue((spillBatchSize <= (memoryLimit / 3)));
        Assert.assertTrue(((spillBatchSize + (2 * batchSize)) <= memoryLimit));
        Assert.assertTrue(((spillBatchSize / rowWidth) >= (memManager.getSpillBatchRowCount())));
        int mergeBatchSize = memManager.getMergeBatchSize().dataSize;
        Assert.assertTrue((mergeBatchSize < (memManager.getPreferredMergeBatchSize())));
        Assert.assertTrue((mergeBatchSize >= rowWidth));
        Assert.assertTrue(((mergeBatchSize + (2 * spillBatchSize)) <= memoryLimit));
        Assert.assertTrue(((mergeBatchSize / rowWidth) >= (memManager.getMergeBatchRowCount())));
        // Should spill after just two or three batches
        int inputBufferSize = memManager.getInputBatchSize().expectedBufferSize;
        Assert.assertFalse(memManager.isSpillNeeded(0, inputBufferSize));
        Assert.assertFalse(memManager.isSpillNeeded(batchSize, inputBufferSize));
        Assert.assertTrue(memManager.isSpillNeeded((3 * inputBufferSize), inputBufferSize));
    }

    @Test
    public void testLowerMemory() {
        SortConfig sortConfig = new SortConfig(SubOperatorTest.fixture.getFragmentContext().getConfig(), SubOperatorTest.fixture.getFragmentContext().getOptions());
        int memoryLimit = 10 * (TestExternalSortInternals.ONE_MEG);
        SortMemoryManager memManager = new SortMemoryManager(sortConfig, memoryLimit);
        // Tighter squeeze, but can be made to work.
        // Input batches are 3/8 of memory; two fill 3/4,
        // but small spill and merge batches allow progress.
        int rowWidth = 1000;
        int batchSize = SortMemoryManager.multiply(((memoryLimit * 3) / 8), PAYLOAD_FROM_BUFFER);
        int rowCount = batchSize / rowWidth;
        batchSize = rowCount * rowWidth;
        memManager.updateEstimates(batchSize, rowWidth, rowCount);
        Assert.assertEquals(rowWidth, memManager.getRowWidth());
        Assert.assertEquals(batchSize, memManager.getInputBatchSize().dataSize);
        Assert.assertFalse(memManager.mayOverflow());
        Assert.assertTrue(memManager.hasPerformanceWarning());
        // Spill, merge batches should be constrained
        int spillBatchSize = memManager.getSpillBatchSize().dataSize;
        Assert.assertTrue((spillBatchSize < (memManager.getPreferredSpillBatchSize())));
        Assert.assertTrue((spillBatchSize >= rowWidth));
        Assert.assertTrue((spillBatchSize <= (memoryLimit / 3)));
        Assert.assertTrue(((spillBatchSize + (2 * batchSize)) <= memoryLimit));
        Assert.assertTrue(((memManager.getSpillBatchRowCount()) >= 1));
        Assert.assertTrue(((spillBatchSize / rowWidth) >= (memManager.getSpillBatchRowCount())));
        int mergeBatchSize = memManager.getMergeBatchSize().dataSize;
        Assert.assertTrue((mergeBatchSize < (memManager.getPreferredMergeBatchSize())));
        Assert.assertTrue((mergeBatchSize >= rowWidth));
        Assert.assertTrue(((mergeBatchSize + (2 * spillBatchSize)) <= memoryLimit));
        Assert.assertTrue(((memManager.getMergeBatchRowCount()) > 1));
        Assert.assertTrue(((mergeBatchSize / rowWidth) >= (memManager.getMergeBatchRowCount())));
        // Should spill after just two batches
        int inputBufferSize = memManager.getInputBatchSize().expectedBufferSize;
        Assert.assertFalse(memManager.isSpillNeeded(0, inputBufferSize));
        Assert.assertFalse(memManager.isSpillNeeded(batchSize, inputBufferSize));
        Assert.assertTrue(memManager.isSpillNeeded((2 * inputBufferSize), inputBufferSize));
    }

    @Test
    public void testExtremeLowMemory() {
        SortConfig sortConfig = new SortConfig(SubOperatorTest.fixture.getFragmentContext().getConfig(), SubOperatorTest.fixture.getFragmentContext().getOptions());
        long memoryLimit = 10 * (TestExternalSortInternals.ONE_MEG);
        SortMemoryManager memManager = new SortMemoryManager(sortConfig, memoryLimit);
        // Jumbo row size, works with one row per batch. Minimum is to have two
        // input rows and a spill row, or two spill rows and a merge row.
        // Have to back off the exact size a bit to allow for internal fragmentation
        // in the merge and output batches.
        int rowWidth = ((int) ((memoryLimit / 3) / 2));
        int rowCount = 1;
        int batchSize = rowWidth;
        memManager.updateEstimates(batchSize, rowWidth, rowCount);
        Assert.assertEquals(rowWidth, memManager.getRowWidth());
        Assert.assertEquals(batchSize, memManager.getInputBatchSize().dataSize);
        Assert.assertFalse(memManager.mayOverflow());
        Assert.assertTrue(memManager.hasPerformanceWarning());
        int spillBatchSize = memManager.getSpillBatchSize().dataSize;
        Assert.assertTrue((spillBatchSize >= rowWidth));
        Assert.assertTrue((spillBatchSize <= (memoryLimit / 3)));
        Assert.assertTrue(((spillBatchSize + (2 * batchSize)) <= memoryLimit));
        Assert.assertEquals(1, memManager.getSpillBatchRowCount());
        int mergeBatchSize = memManager.getMergeBatchSize().dataSize;
        Assert.assertTrue((mergeBatchSize >= rowWidth));
        Assert.assertTrue(((mergeBatchSize + (2 * spillBatchSize)) <= memoryLimit));
        Assert.assertEquals(1, memManager.getMergeBatchRowCount());
        // Should spill after just two rows
        Assert.assertFalse(memManager.isSpillNeeded(0, batchSize));
        Assert.assertFalse(memManager.isSpillNeeded(batchSize, batchSize));
        Assert.assertTrue(memManager.isSpillNeeded((2 * batchSize), batchSize));
    }

    @Test
    public void testMemoryOverflow() {
        SortConfig sortConfig = new SortConfig(SubOperatorTest.fixture.getFragmentContext().getConfig(), SubOperatorTest.fixture.getFragmentContext().getOptions());
        long memoryLimit = 10 * (TestExternalSortInternals.ONE_MEG);
        SortMemoryManager memManager = new SortMemoryManager(sortConfig, memoryLimit);
        // In trouble now, can't fit even two input batches.
        // A better implementation would spill the first batch to a file,
        // leave it open, and append the second batch. Slicing each big input
        // batch into small spill batches will allow the sort to proceed as
        // long as it can hold a single input batch and single merge batch. But,
        // the current implementation requires all batches to be spilled are in
        // memory at the same time...
        int rowWidth = ((int) (memoryLimit / 2));
        int rowCount = 1;
        int batchSize = rowWidth;
        memManager.updateEstimates(batchSize, rowWidth, rowCount);
        Assert.assertTrue(memManager.mayOverflow());
    }

    @Test
    public void testConfigConstraints() {
        int memConstraint = 40 * (TestExternalSortInternals.ONE_MEG);
        int batchSizeConstraint = (TestExternalSortInternals.ONE_MEG) / 2;
        int mergeSizeConstraint = TestExternalSortInternals.ONE_MEG;
        OperatorFixture.Builder builder = new OperatorFixture.Builder(watcher);
        builder.configBuilder().put(EXTERNAL_SORT_MAX_MEMORY, memConstraint).put(EXTERNAL_SORT_SPILL_BATCH_SIZE, batchSizeConstraint).build();
        FragmentContext fragmentContext = builder.build().getFragmentContext();
        fragmentContext.getOptions().setLocalOption(OUTPUT_BATCH_SIZE, mergeSizeConstraint);
        SortConfig sortConfig = new SortConfig(fragmentContext.getConfig(), fragmentContext.getOptions());
        long memoryLimit = 50 * (TestExternalSortInternals.ONE_MEG);
        SortMemoryManager memManager = new SortMemoryManager(sortConfig, memoryLimit);
        Assert.assertEquals(batchSizeConstraint, memManager.getPreferredSpillBatchSize());
        Assert.assertEquals(mergeSizeConstraint, memManager.getPreferredMergeBatchSize());
        Assert.assertEquals(memConstraint, memManager.getMemoryLimit());
        int rowWidth = 300;
        int rowCount = 10000;
        int batchSize = (rowWidth * rowCount) * 2;
        memManager.updateEstimates(batchSize, rowWidth, rowCount);
        verifyCalcs(sortConfig, memConstraint, memManager, batchSize, rowWidth, rowCount);
    }

    @Test
    public void testMemoryDynamics() {
        SortConfig sortConfig = new SortConfig(SubOperatorTest.fixture.getFragmentContext().getConfig(), SubOperatorTest.fixture.getFragmentContext().getOptions());
        long memoryLimit = 50 * (TestExternalSortInternals.ONE_MEG);
        SortMemoryManager memManager = new SortMemoryManager(sortConfig, memoryLimit);
        int rowWidth = 300;
        int rowCount = 10000;
        int batchSize = (rowWidth * rowCount) * 2;
        memManager.updateEstimates(batchSize, rowWidth, rowCount);
        int spillBatchSize = memManager.getSpillBatchSize().dataSize;
        // Test various memory fill levels
        Assert.assertFalse(memManager.isSpillNeeded(0, batchSize));
        Assert.assertFalse(memManager.isSpillNeeded((2 * batchSize), batchSize));
        Assert.assertTrue(memManager.isSpillNeeded(((memoryLimit - spillBatchSize) + 1), batchSize));
        // Similar, but for an in-memory merge
        Assert.assertTrue(memManager.hasMemoryMergeCapacity((memoryLimit - (TestExternalSortInternals.ONE_MEG)), ((TestExternalSortInternals.ONE_MEG) - 1)));
        Assert.assertTrue(memManager.hasMemoryMergeCapacity((memoryLimit - (TestExternalSortInternals.ONE_MEG)), TestExternalSortInternals.ONE_MEG));
        Assert.assertFalse(memManager.hasMemoryMergeCapacity((memoryLimit - (TestExternalSortInternals.ONE_MEG)), ((TestExternalSortInternals.ONE_MEG) + 1)));
    }

    @Test
    public void testMergeCalcs() {
        // No artificial merge limit
        int mergeLimitConstraint = 100;
        OperatorFixture.Builder builder = new OperatorFixture.Builder(watcher);
        builder.configBuilder().put(EXTERNAL_SORT_MERGE_LIMIT, mergeLimitConstraint).build();
        FragmentContext fragmentContext = builder.build().getFragmentContext();
        SortConfig sortConfig = new SortConfig(fragmentContext.getConfig(), fragmentContext.getOptions());
        // Allow four spill batches, 8 MB each, plus one output of 16
        // Allow for internal fragmentation
        // 96 > (4 * 8 + 16) * 2
        long memoryLimit = 96 * (TestExternalSortInternals.ONE_MEG);
        SortMemoryManager memManager = new SortMemoryManager(sortConfig, memoryLimit);
        // Prime the estimates. Batch size is data size, not buffer size.
        int rowWidth = 300;
        int rowCount = 10000;
        int batchSize = (rowWidth * rowCount) * 2;
        memManager.updateEstimates(batchSize, rowWidth, rowCount);
        Assert.assertFalse(memManager.isLowMemory());
        int spillBatchBufferSize = memManager.getSpillBatchSize().maxBufferSize;
        int inputBatchBufferSize = memManager.getInputBatchSize().expectedBufferSize;
        // One in-mem batch, no merging.
        long allocMemory = inputBatchBufferSize;
        MergeTask task = memManager.consolidateBatches(allocMemory, 1, 0);
        Assert.assertEquals(NONE, task.action);
        // Many in-mem batches, just enough to merge
        int memBatches = ((int) ((memManager.getMergeMemoryLimit()) / inputBatchBufferSize));
        allocMemory = memBatches * inputBatchBufferSize;
        task = memManager.consolidateBatches(allocMemory, memBatches, 0);
        Assert.assertEquals(NONE, task.action);
        // Spills if no room to merge spilled and in-memory batches
        int spillCount = ((int) (Math.ceil((((memManager.getMergeMemoryLimit()) - allocMemory) / (1.0 * spillBatchBufferSize)))));
        Assert.assertTrue((spillCount >= 1));
        task = memManager.consolidateBatches(allocMemory, memBatches, spillCount);
        Assert.assertEquals(SPILL, task.action);
        // One more in-mem batch: now needs to spill
        memBatches++;
        allocMemory = memBatches * inputBatchBufferSize;
        task = memManager.consolidateBatches(allocMemory, memBatches, 0);
        Assert.assertEquals(SPILL, task.action);
        // No spill for various in-mem/spill run combinations
        long freeMem = (memManager.getMergeMemoryLimit()) - spillBatchBufferSize;
        memBatches = ((int) (freeMem / inputBatchBufferSize));
        allocMemory = memBatches * inputBatchBufferSize;
        task = memManager.consolidateBatches(allocMemory, memBatches, 1);
        Assert.assertEquals(NONE, task.action);
        freeMem = (memManager.getMergeMemoryLimit()) - (2 * spillBatchBufferSize);
        memBatches = ((int) (freeMem / inputBatchBufferSize));
        allocMemory = memBatches * inputBatchBufferSize;
        task = memManager.consolidateBatches(allocMemory, memBatches, 2);
        Assert.assertEquals(NONE, task.action);
        // No spill if no in-memory, only spill, and spill fits
        freeMem = memManager.getMergeMemoryLimit();
        int spillBatches = ((int) (freeMem / spillBatchBufferSize));
        task = memManager.consolidateBatches(0, 0, spillBatches);
        Assert.assertEquals(NONE, task.action);
        // One more and must merge
        task = memManager.consolidateBatches(0, 0, (spillBatches + 1));
        Assert.assertEquals(MERGE, task.action);
        Assert.assertEquals(2, task.count);
        // Two more and will merge more
        task = memManager.consolidateBatches(0, 0, (spillBatches + 2));
        Assert.assertEquals(MERGE, task.action);
        Assert.assertEquals(3, task.count);
        // If only one spilled run, and no in-memory batches,
        // skip merge.
        task = memManager.consolidateBatches(0, 0, 1);
        Assert.assertEquals(NONE, task.action);
        // Very large number of spilled runs. Limit to what fits in memory.
        task = memManager.consolidateBatches(0, 0, 1000);
        Assert.assertEquals(MERGE, task.action);
        Assert.assertTrue(((task.count) <= (((int) (memoryLimit / spillBatchBufferSize)) - 1)));
    }

    @Test
    public void testMergeCalcsExtreme() {
        SortConfig sortConfig = new SortConfig(SubOperatorTest.fixture.getFragmentContext().getConfig(), SubOperatorTest.fixture.getFragmentContext().getOptions());
        // Force odd situation in which the spill batch is larger
        // than memory. Won't actually run, but needed to test
        // odd merge case.
        long memoryLimit = (TestExternalSortInternals.ONE_MEG) / 2;
        SortMemoryManager memManager = new SortMemoryManager(sortConfig, memoryLimit);
        // Prime the estimates. Batch size is data size, not buffer size.
        int rowWidth = ((int) (memoryLimit));
        int rowCount = 1;
        int batchSize = rowWidth;
        memManager.updateEstimates(batchSize, rowWidth, rowCount);
        Assert.assertTrue(((memManager.getMergeMemoryLimit()) < rowWidth));
        // Only one spill batch, that batch is above the merge memory limit,
        // but nothing useful comes from merging.
        MergeTask task = memManager.consolidateBatches(0, 0, 1);
        Assert.assertEquals(NONE, task.action);
    }

    @Test
    public void testMergeLimit() {
        // Constrain merge width
        int mergeLimitConstraint = 5;
        OperatorFixture.Builder builder = new OperatorFixture.Builder(watcher);
        builder.configBuilder().put(EXTERNAL_SORT_MERGE_LIMIT, mergeLimitConstraint).build();
        FragmentContext fragmentContext = builder.build().getFragmentContext();
        SortConfig sortConfig = new SortConfig(fragmentContext.getConfig(), fragmentContext.getOptions());
        // Plenty of memory, memory will not be a limit
        long memoryLimit = 400 * (TestExternalSortInternals.ONE_MEG);
        SortMemoryManager memManager = new SortMemoryManager(sortConfig, memoryLimit);
        // Prime the estimates
        int rowWidth = 300;
        int rowCount = 10000;
        int batchSize = (rowWidth * rowCount) * 2;
        memManager.updateEstimates(batchSize, rowWidth, rowCount);
        // Pretend merge limit runs, additional in-memory batches
        int memBatchCount = 10;
        int spillRunCount = mergeLimitConstraint;
        long allocMemory = batchSize * memBatchCount;
        MergeTask task = memManager.consolidateBatches(allocMemory, memBatchCount, spillRunCount);
        Assert.assertEquals(SPILL, task.action);
        // too many to merge, spill
        task = memManager.consolidateBatches(allocMemory, 1, spillRunCount);
        Assert.assertEquals(SPILL, task.action);
        // One more runs than can merge in one go, intermediate merge
        task = memManager.consolidateBatches(0, 0, (spillRunCount + 1));
        Assert.assertEquals(MERGE, task.action);
        Assert.assertEquals(2, task.count);
        // Two more spill runs, merge three
        task = memManager.consolidateBatches(0, 0, (spillRunCount + 2));
        Assert.assertEquals(MERGE, task.action);
        Assert.assertEquals(3, task.count);
        // Way more than can merge, limit to the constraint
        task = memManager.consolidateBatches(0, 0, (spillRunCount * 3));
        Assert.assertEquals(MERGE, task.action);
        Assert.assertEquals(mergeLimitConstraint, task.count);
    }

    @Test
    public void testMetrics() {
        OperatorStats stats = new OperatorStats(100, 101, 0, SubOperatorTest.fixture.allocator());
        SortMetrics metrics = new SortMetrics(stats);
        // Input stats
        metrics.updateInputMetrics(100, 10000);
        Assert.assertEquals(1, metrics.getInputBatchCount());
        Assert.assertEquals(100, metrics.getInputRowCount());
        Assert.assertEquals(10000, metrics.getInputBytes());
        metrics.updateInputMetrics(200, 20000);
        Assert.assertEquals(2, metrics.getInputBatchCount());
        Assert.assertEquals(300, metrics.getInputRowCount());
        Assert.assertEquals(30000, metrics.getInputBytes());
        // Buffer memory
        Assert.assertEquals(0L, stats.getLongStat(MIN_BUFFER));
        metrics.updateMemory(1000000);
        Assert.assertEquals(1000000L, stats.getLongStat(MIN_BUFFER));
        metrics.updateMemory(2000000);
        Assert.assertEquals(1000000L, stats.getLongStat(MIN_BUFFER));
        metrics.updateMemory(100000);
        Assert.assertEquals(100000L, stats.getLongStat(MIN_BUFFER));
        // Peak batches
        Assert.assertEquals(0L, stats.getLongStat(PEAK_BATCHES_IN_MEMORY));
        metrics.updatePeakBatches(10);
        Assert.assertEquals(10L, stats.getLongStat(PEAK_BATCHES_IN_MEMORY));
        metrics.updatePeakBatches(1);
        Assert.assertEquals(10L, stats.getLongStat(PEAK_BATCHES_IN_MEMORY));
        metrics.updatePeakBatches(20);
        Assert.assertEquals(20L, stats.getLongStat(PEAK_BATCHES_IN_MEMORY));
        // Merge count
        Assert.assertEquals(0L, stats.getLongStat(MERGE_COUNT));
        metrics.incrMergeCount();
        Assert.assertEquals(1L, stats.getLongStat(MERGE_COUNT));
        metrics.incrMergeCount();
        Assert.assertEquals(2L, stats.getLongStat(MERGE_COUNT));
        // Spill count
        Assert.assertEquals(0L, stats.getLongStat(SPILL_COUNT));
        metrics.incrSpillCount();
        Assert.assertEquals(1L, stats.getLongStat(SPILL_COUNT));
        metrics.incrSpillCount();
        Assert.assertEquals(2L, stats.getLongStat(SPILL_COUNT));
        // Write bytes
        Assert.assertEquals(0L, stats.getLongStat(SPILL_MB));
        metrics.updateWriteBytes(((17 * (TestExternalSortInternals.ONE_MEG)) + (((TestExternalSortInternals.ONE_MEG) * 3) / 4)));
        Assert.assertEquals(17.75, stats.getDoubleStat(SPILL_MB), 0.01);
    }
}

