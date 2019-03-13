/**
 * Copyright 2017 LinkedIn Corp. All rights reserved.
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
 */
package com.github.ambry.store;


import StoreErrorCodes.Initialization_Error;
import com.codahale.metrics.MetricRegistry;
import com.github.ambry.utils.TestUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link DiskSpaceAllocator} for expected behavior.
 */
public class DiskSpaceAllocatorTest {
    private static final StorageManagerMetrics METRICS = new StorageManagerMetrics(new MetricRegistry());

    private int requiredSwapSegmentsPerSize = 0;

    private final File allocatedFileDir;

    private final File reserveFileDir;

    private DiskSpaceAllocator alloc;

    private ExecutorService exec;

    public DiskSpaceAllocatorTest() throws Exception {
        File tempDir = StoreTestUtils.createTempDirectory("disk-space-allocator-test");
        reserveFileDir = new File(tempDir, "reserve-files");
        allocatedFileDir = new File(tempDir, "allocated-files");
        if (!(allocatedFileDir.mkdir())) {
            throw new IOException("Could not create directory for allocated files");
        }
    }

    /**
     * Test behavior when segments are allocated before the pool is fully initialized.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void allocateBeforeInitializeTest() throws Exception {
        alloc = constructAllocator();
        File f1 = allocateAndVerify("file1", 50);
        File f2 = allocateAndVerify("file2", 20);
        File f3 = allocateAndVerify("file3", 20);
        // free one file before initializing pool
        freeAndVerify(f3, 20);
        // expect the pool to still be empty
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState());
        alloc.initializePool(Arrays.asList(new DiskSpaceRequirements(50, 2, 0), new DiskSpaceRequirements(21, 1, 0)));
        // return files that were allocated before initialization to the pool
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState().add(50, 2).add(21, 1));
        freeAndVerify(f1, 50);
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState().add(50, 3).add(21, 1));
        freeAndVerify(f2, 20);
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState().add(50, 3).add(20, 1).add(21, 1));
        // allocate and free file from initialized pool
        File f4 = allocateAndVerify("file4", 50);
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState().add(50, 2).add(20, 1).add(21, 1));
        freeAndVerify(f4, 50);
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState().add(50, 3).add(20, 1).add(21, 1));
    }

    /**
     * Test a large number of concurrent alloc/free operations
     *
     * @throws Exception
     * 		
     */
    @Test
    public void concurrencyTest() throws Exception {
        alloc = constructAllocator();
        List<DiskSpaceRequirements> requirementsList = new ArrayList<>();
        requirementsList.add(new DiskSpaceRequirements(10, 500, 1));
        requirementsList.add(new DiskSpaceRequirements(5, 251, 0));
        alloc.initializePool(requirementsList);
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState().add(10, 500).add(5, 251));
        exec = Executors.newCachedThreadPool();
        // allocate all files in pool
        runConcurrencyTest(requirementsList, true, false);
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState().add(10, 0).add(5, 0));
        // free all files from last run
        runConcurrencyTest(requirementsList, false, true);
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState().add(10, 500).add(5, 251));
        // allocate and free all files
        runConcurrencyTest(requirementsList, true, true);
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState().add(10, 500).add(5, 251));
        // allocate all files
        runConcurrencyTest(requirementsList, true, false);
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState().add(10, 0).add(5, 0));
    }

    /**
     * Test allocation of file sizes that are not in the pool after the pool has been initialized.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void sizeNotInPoolTest() throws Exception {
        alloc = constructAllocator();
        alloc.initializePool(Arrays.asList(new DiskSpaceRequirements(50, 2, 0), new DiskSpaceRequirements(21, 1, 0)));
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState().add(50, 2).add(21, 1));
        File f1 = allocateAndVerify("file1", 25);
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState().add(50, 2).add(21, 1));
        freeAndVerify(f1, 25);
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState().add(50, 2).add(21, 1).add(25, 1));
        // try checking out same file again
        File f2 = allocateAndVerify("file2", 25);
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState().add(50, 2).add(21, 1).add(25, 0));
        freeAndVerify(f2, 25);
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState().add(50, 2).add(21, 1).add(25, 1));
    }

    /**
     * This tests various cases where the allocator is restarted and disk space requirements change.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void subsequentStartupTest() throws Exception {
        requiredSwapSegmentsPerSize = 1;
        alloc = constructAllocator();
        List<DiskSpaceRequirements> requirementsList = new ArrayList<>();
        requirementsList.add(new DiskSpaceRequirements(5, 6, 0));
        requirementsList.add(new DiskSpaceRequirements(3, 7, 0));
        requirementsList.add(new DiskSpaceRequirements(5, 4, 0));
        requirementsList.add(new DiskSpaceRequirements(6, 3, 1));
        alloc.initializePool(requirementsList);
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState().add(3, 8).add(5, 11).add(6, 3));
        File f1 = allocateAndVerify("file1", 5);
        File f2 = allocateAndVerify("file2", 3);
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState().add(3, 7).add(5, 10).add(6, 3));
        // second startup, test freeing old files after initialization
        alloc = constructAllocator();
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState().add(3, 7).add(5, 10).add(6, 3));
        alloc.initializePool(requirementsList);
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState().add(3, 8).add(5, 11).add(6, 3));
        freeAndVerify(f1, 5);
        freeAndVerify(f2, 3);
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState().add(3, 9).add(5, 12).add(6, 3));
        // third startup, test allocating files before initialization and changing disk space requirements when initializing
        alloc = constructAllocator();
        f1 = allocateAndVerify("file1", 6);
        f2 = allocateAndVerify("file2", 6);
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState().add(3, 9).add(5, 12).add(6, 1));
        // files freed before init should be discarded, not returned to the pool
        freeAndVerify(f1, 6);
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState().add(3, 9).add(5, 12).add(6, 1));
        requirementsList.clear();
        requirementsList.add(new DiskSpaceRequirements(3, 20, 1));
        requirementsList.add(new DiskSpaceRequirements(5, 6, 1));
        alloc.initializePool(requirementsList);
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState().add(3, 20).add(5, 6));
        freeAndVerify(f2, 6);
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState().add(3, 20).add(5, 6).add(6, 1));
    }

    /**
     * Test various swap segment usage combinations
     *
     * @throws Exception
     * 		
     */
    @Test
    public void swapSegmentRequirementTest() throws Exception {
        requiredSwapSegmentsPerSize = 4;
        alloc = constructAllocator();
        List<DiskSpaceRequirements> requirementsList = new ArrayList<>();
        requirementsList.add(new DiskSpaceRequirements(3, 7, 0));
        requirementsList.add(new DiskSpaceRequirements(5, 6, 1));
        requirementsList.add(new DiskSpaceRequirements(5, 4, 2));
        requirementsList.add(new DiskSpaceRequirements(6, 3, 1));
        // This should result in 0 swap segments b/c 0 > 4 - 10
        requirementsList.add(new DiskSpaceRequirements(7, 3, 10));
        Collections.shuffle(requirementsList);
        alloc.initializePool(requirementsList);
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState().add(3, 11).add(5, 11).add(6, 6).add(7, 3));
        // test reinitialization with different requirements, and num swap used for each size
        requiredSwapSegmentsPerSize = 3;
        alloc = constructAllocator();
        requirementsList.clear();
        requirementsList.add(new DiskSpaceRequirements(3, 2, 0));
        requirementsList.add(new DiskSpaceRequirements(3, 2, 0));
        requirementsList.add(new DiskSpaceRequirements(5, 6, 1));
        requirementsList.add(new DiskSpaceRequirements(5, 4, 2));
        requirementsList.add(new DiskSpaceRequirements(7, 3, 1));
        Collections.shuffle(requirementsList);
        alloc.initializePool(requirementsList);
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState().add(3, 7).add(5, 10).add(7, 5));
    }

    /**
     * This tests the case where we cannot create a reserve file directory, because a normal file at that path already
     * exists.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void invalidReserveFileDirTest() throws Exception {
        Assert.assertTrue("Could not create file", reserveFileDir.createNewFile());
        alloc = constructAllocator();
        // Should still allow allocation of non-pooled files before init.
        File f1 = allocateAndVerify("file1", 5);
        freeAndVerify(f1, 5);
        Assert.assertFalse("Reserve file dir should not have been created if a file already exists at that path", reserveFileDir.isDirectory());
        try {
            alloc.initializePool(Collections.emptyList());
            Assert.fail("Expected StoreException");
        } catch (StoreException e) {
            Assert.assertEquals("Wrong error code", Initialization_Error, e.getErrorCode());
        }
        // Should still allow allocation of non-pooled files even after init failure.
        File f2 = allocateAndVerify("file1", 5);
        freeAndVerify(f2, 5);
        Assert.assertFalse("Reserve file dir should not have been created if a file already exists at that path", reserveFileDir.isDirectory());
    }

    /**
     * Exercises different failure cases for {@link DiskSpaceAllocator#initializePool(Collection)}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void initFailureTest() throws Exception {
        // build a pool with files that we can modify permissions on to induce failures
        alloc = constructAllocator();
        alloc.initializePool(Collections.singletonList(new DiskSpaceRequirements(50, 2, 0)));
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState().add(50, 2));
        // test a failure while deleting an unneeded directory
        runInitFailureTest(reserveFileDir, false);
        // test a failure while deleting an unneeded individual file
        File fileSizeDir = new File(reserveFileDir, DiskSpaceAllocator.generateFileSizeDirName(50));
        runInitFailureTest(fileSizeDir, false, new DiskSpaceRequirements(50, 1, 0));
        // test that an inventory failure during DSA construction results in an exception thrown by initializePool
        runInitFailureTest(reserveFileDir, true);
    }

    /**
     * Test situations where the allocator should throw exceptions during allocate and free calls.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void allocateAndFreeFailureTest() throws Exception {
        alloc = constructAllocator();
        alloc.initializePool(Collections.singletonList(new DiskSpaceRequirements(50, 1, 0)));
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState().add(50, 1));
        // test when a destination file already exists
        File f1 = new File(allocatedFileDir, "f1");
        Assert.assertTrue("Could not create file", f1.createNewFile());
        TestUtils.assertException(IOException.class, () -> alloc.allocate(f1, 50), null);
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState().add(50, 1));
        // test returning a file that does not exist
        File f2 = new File(allocatedFileDir, "f2");
        TestUtils.assertException(IOException.class, () -> alloc.free(f2, 50), null);
        verifyPoolState(new DiskSpaceAllocatorTest.ExpectedState().add(50, 1));
    }

    /**
     * Test the allocator with pooling disabled. The reserve file pool should never be created
     */
    @Test
    public void unpooledAllocatorTest() throws Exception {
        alloc = constructUnpooledAllocator();
        verifyPoolState(null);
        File f1 = allocateAndVerify("file1", 20);
        verifyPoolState(null);
        freeAndVerify(f1, 20);
        verifyPoolState(null);
        // initializing should be a no-op
        alloc.initializePool(Collections.singletonList(new DiskSpaceRequirements(20, 1, 0)));
        verifyPoolState(null);
        File f2 = allocateAndVerify("file1", 20);
        verifyPoolState(null);
        freeAndVerify(f1, 20);
        verifyPoolState(null);
    }

    /**
     * Contains a map that describes the number of reserve files for each file size. Used with {@link #verifyPoolState}
     */
    static class ExpectedState {
        final Map<Long, Integer> map = new HashMap<>();

        /**
         * Update the map with an entry for a certain file size.
         *
         * @param size
         * 		the file size.
         * @param count
         * 		the expected number of files
         * @return {@code this}
         */
        DiskSpaceAllocatorTest.ExpectedState add(long size, int count) {
            map.put(size, count);
            return this;
        }
    }
}

