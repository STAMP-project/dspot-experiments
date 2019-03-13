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
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.StorageType;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Test;


public class TestLazyPersistReplicaRecovery extends LazyPersistTestCase {
    @Test
    public void testDnRestartWithSavedReplicas() throws IOException, InterruptedException, TimeoutException {
        getClusterBuilder().build();
        final String METHOD_NAME = GenericTestUtils.getMethodName();
        Path path1 = new Path((("/" + METHOD_NAME) + ".01.dat"));
        makeTestFile(path1, LazyPersistTestCase.BLOCK_SIZE, true);
        ensureFileReplicasOnStorageType(path1, StorageType.RAM_DISK);
        // Sleep for a short time to allow the lazy writer thread to do its job.
        // However the block replica should not be evicted from RAM_DISK yet.
        Thread.sleep(((3 * (LazyPersistTestCase.LAZY_WRITER_INTERVAL_SEC)) * 1000));
        ensureFileReplicasOnStorageType(path1, StorageType.RAM_DISK);
        LazyPersistTestCase.LOG.info("Restarting the DataNode");
        cluster.restartDataNode(0, true);
        cluster.waitActive();
        triggerBlockReport();
        // Ensure that the replica is now on persistent storage.
        ensureFileReplicasOnStorageType(path1, StorageType.DEFAULT);
    }

    @Test
    public void testDnRestartWithUnsavedReplicas() throws IOException, InterruptedException, TimeoutException {
        getClusterBuilder().build();
        FsDatasetTestUtil.stopLazyWriter(cluster.getDataNodes().get(0));
        final String METHOD_NAME = GenericTestUtils.getMethodName();
        Path path1 = new Path((("/" + METHOD_NAME) + ".01.dat"));
        makeTestFile(path1, LazyPersistTestCase.BLOCK_SIZE, true);
        ensureFileReplicasOnStorageType(path1, StorageType.RAM_DISK);
        LazyPersistTestCase.LOG.info("Restarting the DataNode");
        cluster.restartDataNode(0, true);
        cluster.waitActive();
        // Ensure that the replica is still on transient storage.
        ensureFileReplicasOnStorageType(path1, StorageType.RAM_DISK);
    }
}

