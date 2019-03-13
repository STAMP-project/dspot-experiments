/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.datanode.fsdataset.impl;


import java.io.IOException;
import java.util.EnumSet;
import java.util.concurrent.TimeoutException;
import org.apache.hadoop.fs.CreateFlag;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.StorageType;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSOutputStream;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.server.datanode.DataNodeTestUtils;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsDatasetSpi;
import org.apache.hadoop.test.GenericTestUtils;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


/**
 * Verify that locked memory is used correctly when writing to replicas in
 * memory
 */
public class TestLazyPersistLockedMemory extends LazyPersistTestCase {
    /**
     * RAM disk present but locked memory is set to zero. Placement should
     * fall back to disk.
     */
    @Test
    public void testWithNoLockedMemory() throws IOException, InterruptedException, TimeoutException {
        getClusterBuilder().setNumDatanodes(1).setMaxLockedMemory(0).build();
        final String METHOD_NAME = GenericTestUtils.getMethodName();
        Path path = new Path((("/" + METHOD_NAME) + ".dat"));
        makeTestFile(path, LazyPersistTestCase.BLOCK_SIZE, true);
        ensureFileReplicasOnStorageType(path, StorageType.DEFAULT);
    }

    @Test
    public void testReservation() throws IOException, InterruptedException, TimeoutException {
        getClusterBuilder().setNumDatanodes(1).setMaxLockedMemory(LazyPersistTestCase.BLOCK_SIZE).build();
        final String METHOD_NAME = GenericTestUtils.getMethodName();
        final FsDatasetSpi<?> fsd = cluster.getDataNodes().get(0).getFSDataset();
        // Create a file and ensure the replica in RAM_DISK uses locked memory.
        Path path = new Path((("/" + METHOD_NAME) + ".dat"));
        makeTestFile(path, LazyPersistTestCase.BLOCK_SIZE, true);
        ensureFileReplicasOnStorageType(path, StorageType.RAM_DISK);
        Assert.assertThat(fsd.getCacheUsed(), Is.is(((long) (LazyPersistTestCase.BLOCK_SIZE))));
    }

    @Test
    public void testReleaseOnFileDeletion() throws IOException, InterruptedException, TimeoutException {
        getClusterBuilder().setNumDatanodes(1).setMaxLockedMemory(LazyPersistTestCase.BLOCK_SIZE).build();
        final String METHOD_NAME = GenericTestUtils.getMethodName();
        final FsDatasetSpi<?> fsd = cluster.getDataNodes().get(0).getFSDataset();
        Path path = new Path((("/" + METHOD_NAME) + ".dat"));
        makeTestFile(path, LazyPersistTestCase.BLOCK_SIZE, true);
        ensureFileReplicasOnStorageType(path, StorageType.RAM_DISK);
        Assert.assertThat(fsd.getCacheUsed(), Is.is(((long) (LazyPersistTestCase.BLOCK_SIZE))));
        // Delete the file and ensure that the locked memory is released.
        fs.delete(path, false);
        DataNodeTestUtils.triggerBlockReport(cluster.getDataNodes().get(0));
        waitForLockedBytesUsed(fsd, 0);
    }

    /**
     * Verify that locked RAM is released when blocks are evicted from RAM disk.
     */
    @Test
    public void testReleaseOnEviction() throws Exception {
        getClusterBuilder().setNumDatanodes(1).setMaxLockedMemory(LazyPersistTestCase.BLOCK_SIZE).setRamDiskReplicaCapacity((((LazyPersistTestCase.BLOCK_SIZE) * 2) - 1)).build();
        final String METHOD_NAME = GenericTestUtils.getMethodName();
        final FsDatasetImpl fsd = ((FsDatasetImpl) (cluster.getDataNodes().get(0).getFSDataset()));
        Path path1 = new Path((("/" + METHOD_NAME) + ".01.dat"));
        makeTestFile(path1, LazyPersistTestCase.BLOCK_SIZE, true);
        Assert.assertThat(fsd.getCacheUsed(), Is.is(((long) (LazyPersistTestCase.BLOCK_SIZE))));
        // Wait until the replica is written to persistent storage.
        waitForMetric("RamDiskBlocksLazyPersisted", 1);
        // Trigger eviction and verify locked bytes were released.
        fsd.evictLazyPersistBlocks(Long.MAX_VALUE);
        verifyRamDiskJMXMetric("RamDiskBlocksEvicted", 1);
        waitForLockedBytesUsed(fsd, 0);
    }

    /**
     * Verify that locked bytes are correctly updated when a block is finalized
     * at less than its max length.
     */
    @Test
    public void testShortBlockFinalized() throws IOException, InterruptedException, TimeoutException {
        getClusterBuilder().setNumDatanodes(1).build();
        final String METHOD_NAME = GenericTestUtils.getMethodName();
        final FsDatasetSpi<?> fsd = cluster.getDataNodes().get(0).getFSDataset();
        Path path = new Path((("/" + METHOD_NAME) + ".dat"));
        makeTestFile(path, 1, true);
        Assert.assertThat(fsd.getCacheUsed(), Is.is(osPageSize));
        // Delete the file and ensure locked RAM usage goes to zero.
        fs.delete(path, false);
        waitForLockedBytesUsed(fsd, 0);
    }

    /**
     * Verify that locked bytes are correctly updated when the client goes
     * away unexpectedly during a write.
     */
    @Test
    public void testWritePipelineFailure() throws IOException, InterruptedException, TimeoutException {
        getClusterBuilder().setNumDatanodes(1).build();
        final String METHOD_NAME = GenericTestUtils.getMethodName();
        final FsDatasetSpi<?> fsd = cluster.getDataNodes().get(0).getFSDataset();
        Path path = new Path((("/" + METHOD_NAME) + ".dat"));
        EnumSet<CreateFlag> createFlags = EnumSet.of(CreateFlag.CREATE, CreateFlag.LAZY_PERSIST);
        // Write 1 byte to the file and kill the writer.
        final FSDataOutputStream fos = fs.create(path, FsPermission.getFileDefault(), createFlags, LazyPersistTestCase.BUFFER_LENGTH, LazyPersistTestCase.REPL_FACTOR, LazyPersistTestCase.BLOCK_SIZE, null);
        fos.write(new byte[1]);
        fos.hsync();
        DFSTestUtil.abortStream(((DFSOutputStream) (fos.getWrappedStream())));
        waitForLockedBytesUsed(fsd, osPageSize);
        // Delete the file and ensure locked RAM goes to zero.
        fs.delete(path, false);
        DataNodeTestUtils.triggerBlockReport(cluster.getDataNodes().get(0));
        waitForLockedBytesUsed(fsd, 0);
    }
}

