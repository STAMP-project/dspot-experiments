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
package org.apache.hadoop.hdfs.server.datanode.fsdataset.impl;


import java.io.IOException;
import java.util.concurrent.TimeoutException;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.StorageType;
import org.apache.hadoop.hdfs.ClientContext;
import org.apache.hadoop.hdfs.client.HdfsDataInputStream;
import org.apache.hadoop.test.GenericTestUtils;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Test Lazy persist behavior with short-circuit reads. These tests
 * will be run on Linux only with Native IO enabled. The tests fake
 * RAM_DISK storage using local disk.
 */
public class TestScrLazyPersistFiles extends LazyPersistTestCase {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Read in-memory block with Short Circuit Read
     * Note: the test uses faked RAM_DISK from physical disk.
     */
    @Test
    public void testRamDiskShortCircuitRead() throws IOException, InterruptedException, TimeoutException {
        getClusterBuilder().setUseScr(true).build();
        final String METHOD_NAME = GenericTestUtils.getMethodName();
        final int SEED = 1027565;
        Path path = new Path((("/" + METHOD_NAME) + ".dat"));
        // Create a file and wait till it is persisted.
        makeRandomTestFile(path, LazyPersistTestCase.BLOCK_SIZE, true, SEED);
        ensureFileReplicasOnStorageType(path, StorageType.RAM_DISK);
        waitForMetric("RamDiskBlocksLazyPersisted", 1);
        HdfsDataInputStream fis = ((HdfsDataInputStream) (fs.open(path)));
        // Verify SCR read counters
        try {
            byte[] buf = new byte[LazyPersistTestCase.BUFFER_LENGTH];
            fis.read(0, buf, 0, LazyPersistTestCase.BUFFER_LENGTH);
            Assert.assertEquals(LazyPersistTestCase.BUFFER_LENGTH, fis.getReadStatistics().getTotalBytesRead());
            Assert.assertEquals(LazyPersistTestCase.BUFFER_LENGTH, fis.getReadStatistics().getTotalShortCircuitBytesRead());
        } finally {
            fis.close();
            fis = null;
        }
    }

    /**
     * Eviction of lazy persisted blocks with Short Circuit Read handle open
     * Note: the test uses faked RAM_DISK from physical disk.
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void tesScrDuringEviction() throws Exception {
        getClusterBuilder().setUseScr(true).build();
        final String METHOD_NAME = GenericTestUtils.getMethodName();
        Path path1 = new Path((("/" + METHOD_NAME) + ".01.dat"));
        // Create a file and wait till it is persisted.
        makeTestFile(path1, LazyPersistTestCase.BLOCK_SIZE, true);
        ensureFileReplicasOnStorageType(path1, StorageType.RAM_DISK);
        waitForMetric("RamDiskBlocksLazyPersisted", 1);
        HdfsDataInputStream fis = ((HdfsDataInputStream) (fs.open(path1)));
        try {
            // Keep and open read handle to path1 while creating path2
            byte[] buf = new byte[LazyPersistTestCase.BUFFER_LENGTH];
            fis.read(0, buf, 0, LazyPersistTestCase.BUFFER_LENGTH);
            triggerEviction(cluster.getDataNodes().get(0));
            // Ensure path1 is still readable from the open SCR handle.
            fis.read(0, buf, 0, LazyPersistTestCase.BUFFER_LENGTH);
            Assert.assertThat(fis.getReadStatistics().getTotalBytesRead(), Is.is((((long) (2)) * (LazyPersistTestCase.BUFFER_LENGTH))));
            Assert.assertThat(fis.getReadStatistics().getTotalShortCircuitBytesRead(), Is.is((((long) (2)) * (LazyPersistTestCase.BUFFER_LENGTH))));
        } finally {
            IOUtils.closeQuietly(fis);
        }
    }

    @Test
    public void testScrAfterEviction() throws IOException, InterruptedException, TimeoutException {
        getClusterBuilder().setUseScr(true).setUseLegacyBlockReaderLocal(false).build();
        doShortCircuitReadAfterEvictionTest();
    }

    @Test
    public void testLegacyScrAfterEviction() throws IOException, InterruptedException, TimeoutException {
        getClusterBuilder().setUseScr(true).setUseLegacyBlockReaderLocal(true).build();
        doShortCircuitReadAfterEvictionTest();
        // In the implementation of legacy short-circuit reads, any failure is
        // trapped silently, reverts back to a remote read, and also disables all
        // subsequent legacy short-circuit reads in the ClientContext.
        // Assert that it didn't get disabled.
        ClientContext clientContext = client.getClientContext();
        Assert.assertFalse(clientContext.getDisableLegacyBlockReaderLocal());
    }

    @Test
    public void testScrBlockFileCorruption() throws IOException, InterruptedException, TimeoutException {
        getClusterBuilder().setUseScr(true).setUseLegacyBlockReaderLocal(false).build();
        doShortCircuitReadBlockFileCorruptionTest();
    }

    @Test
    public void testLegacyScrBlockFileCorruption() throws IOException, InterruptedException, TimeoutException {
        getClusterBuilder().setUseScr(true).setUseLegacyBlockReaderLocal(true).build();
        doShortCircuitReadBlockFileCorruptionTest();
    }

    @Test
    public void testScrMetaFileCorruption() throws IOException, InterruptedException, TimeoutException {
        getClusterBuilder().setUseScr(true).setUseLegacyBlockReaderLocal(false).build();
        doShortCircuitReadMetaFileCorruptionTest();
    }

    @Test
    public void testLegacyScrMetaFileCorruption() throws IOException, InterruptedException, TimeoutException {
        getClusterBuilder().setUseScr(true).setUseLegacyBlockReaderLocal(true).build();
        doShortCircuitReadMetaFileCorruptionTest();
    }
}

