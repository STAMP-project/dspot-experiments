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
package com.twitter.distributedlog.impl.metadata;


import com.google.common.collect.Lists;
import com.twitter.distributedlog.DLMTestUtil;
import com.twitter.distributedlog.DistributedLogManager;
import com.twitter.distributedlog.ZooKeeperClient;
import com.twitter.distributedlog.ZooKeeperClusterTestCase;
import com.twitter.distributedlog.exceptions.LogNotFoundException;
import com.twitter.distributedlog.metadata.BKDLConfig;
import com.twitter.distributedlog.metadata.DLMetadata;
import com.twitter.distributedlog.namespace.DistributedLogNamespace;
import com.twitter.distributedlog.util.FutureUtils;
import java.net.URI;
import java.util.List;
import org.apache.bookkeeper.versioning.Versioned;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


/**
 * Test {@link ZKLogMetadataForWriter}
 */
public class TestZKLogMetadataForWriter extends ZooKeeperClusterTestCase {
    private static final int sessionTimeoutMs = 30000;

    @Rule
    public TestName testName = new TestName();

    private ZooKeeperClient zkc;

    @Test(timeout = 60000)
    public void testCheckLogMetadataPathsWithAllocator() throws Exception {
        String logRootPath = "/" + (testName.getMethodName());
        List<Versioned<byte[]>> metadatas = FutureUtils.result(ZKLogMetadataForWriter.checkLogMetadataPaths(zkc.get(), logRootPath, true));
        Assert.assertEquals("Should have 8 paths", 8, metadatas.size());
        for (Versioned<byte[]> path : metadatas.subList(2, metadatas.size())) {
            Assert.assertNull(path.getValue());
            Assert.assertNull(path.getVersion());
        }
    }

    @Test(timeout = 60000)
    public void testCheckLogMetadataPathsWithoutAllocator() throws Exception {
        String logRootPath = "/" + (testName.getMethodName());
        List<Versioned<byte[]>> metadatas = FutureUtils.result(ZKLogMetadataForWriter.checkLogMetadataPaths(zkc.get(), logRootPath, false));
        Assert.assertEquals("Should have 7 paths", 7, metadatas.size());
        for (Versioned<byte[]> path : metadatas.subList(2, metadatas.size())) {
            Assert.assertNull(path.getValue());
            Assert.assertNull(path.getVersion());
        }
    }

    @Test(timeout = 60000)
    public void testCreateLogMetadataMissingLogSegmentsPath() throws Exception {
        URI uri = DLMTestUtil.createDLMURI(ZooKeeperClusterTestCase.zkPort, "");
        String logName = testName.getMethodName();
        String logIdentifier = "<default>";
        String logRootPath = getLogRootPath(uri, logName, logIdentifier);
        List<String> pathsToDelete = Lists.newArrayList((logRootPath + (LOGSEGMENTS_PATH)));
        testCreateLogMetadataWithMissingPaths(uri, logName, logIdentifier, pathsToDelete, false, true);
    }

    @Test(timeout = 60000)
    public void testCreateLogMetadataMissingMaxTxIdPath() throws Exception {
        URI uri = DLMTestUtil.createDLMURI(ZooKeeperClusterTestCase.zkPort, "");
        String logName = testName.getMethodName();
        String logIdentifier = "<default>";
        String logRootPath = getLogRootPath(uri, logName, logIdentifier);
        List<String> pathsToDelete = Lists.newArrayList((logRootPath + (MAX_TXID_PATH)));
        testCreateLogMetadataWithMissingPaths(uri, logName, logIdentifier, pathsToDelete, false, true);
    }

    @Test(timeout = 60000)
    public void testCreateLogMetadataMissingLockPath() throws Exception {
        URI uri = DLMTestUtil.createDLMURI(ZooKeeperClusterTestCase.zkPort, "");
        String logName = testName.getMethodName();
        String logIdentifier = "<default>";
        String logRootPath = getLogRootPath(uri, logName, logIdentifier);
        List<String> pathsToDelete = Lists.newArrayList((logRootPath + (LOCK_PATH)));
        testCreateLogMetadataWithMissingPaths(uri, logName, logIdentifier, pathsToDelete, false, true);
    }

    @Test(timeout = 60000)
    public void testCreateLogMetadataMissingReadLockPath() throws Exception {
        URI uri = DLMTestUtil.createDLMURI(ZooKeeperClusterTestCase.zkPort, "");
        String logName = testName.getMethodName();
        String logIdentifier = "<default>";
        String logRootPath = getLogRootPath(uri, logName, logIdentifier);
        List<String> pathsToDelete = Lists.newArrayList((logRootPath + (READ_LOCK_PATH)));
        testCreateLogMetadataWithMissingPaths(uri, logName, logIdentifier, pathsToDelete, false, true);
    }

    @Test(timeout = 60000)
    public void testCreateLogMetadataMissingVersionPath() throws Exception {
        URI uri = DLMTestUtil.createDLMURI(ZooKeeperClusterTestCase.zkPort, "");
        String logName = testName.getMethodName();
        String logIdentifier = "<default>";
        String logRootPath = getLogRootPath(uri, logName, logIdentifier);
        List<String> pathsToDelete = Lists.newArrayList((logRootPath + (VERSION_PATH)));
        testCreateLogMetadataWithMissingPaths(uri, logName, logIdentifier, pathsToDelete, false, true);
    }

    @Test(timeout = 60000)
    public void testCreateLogMetadataMissingAllocatorPath() throws Exception {
        URI uri = DLMTestUtil.createDLMURI(ZooKeeperClusterTestCase.zkPort, "");
        String logName = testName.getMethodName();
        String logIdentifier = "<default>";
        String logRootPath = getLogRootPath(uri, logName, logIdentifier);
        List<String> pathsToDelete = Lists.newArrayList((logRootPath + (ALLOCATION_PATH)));
        testCreateLogMetadataWithMissingPaths(uri, logName, logIdentifier, pathsToDelete, true, true);
    }

    @Test(timeout = 60000)
    public void testCreateLogMetadataMissingAllPath() throws Exception {
        URI uri = DLMTestUtil.createDLMURI(ZooKeeperClusterTestCase.zkPort, "");
        String logName = testName.getMethodName();
        String logIdentifier = "<default>";
        String logRootPath = getLogRootPath(uri, logName, logIdentifier);
        List<String> pathsToDelete = Lists.newArrayList((logRootPath + (LOGSEGMENTS_PATH)), (logRootPath + (MAX_TXID_PATH)), (logRootPath + (LOCK_PATH)), (logRootPath + (READ_LOCK_PATH)), (logRootPath + (VERSION_PATH)), (logRootPath + (ALLOCATION_PATH)));
        testCreateLogMetadataWithMissingPaths(uri, logName, logIdentifier, pathsToDelete, true, true);
    }

    @Test(timeout = 60000)
    public void testCreateLogMetadataOnExistedLog() throws Exception {
        URI uri = DLMTestUtil.createDLMURI(ZooKeeperClusterTestCase.zkPort, "");
        String logName = testName.getMethodName();
        String logIdentifier = "<default>";
        List<String> pathsToDelete = Lists.newArrayList();
        testCreateLogMetadataWithMissingPaths(uri, logName, logIdentifier, pathsToDelete, true, true);
    }

    @Test(timeout = 60000)
    public void testCreateLogMetadata() throws Exception {
        URI uri = DLMTestUtil.createDLMURI(ZooKeeperClusterTestCase.zkPort, "");
        String logName = testName.getMethodName();
        String logIdentifier = "<default>";
        List<String> pathsToDelete = Lists.newArrayList();
        testCreateLogMetadataWithMissingPaths(uri, logName, logIdentifier, pathsToDelete, true, false);
    }

    @Test(timeout = 60000, expected = LogNotFoundException.class)
    public void testCreateLogMetadataWithCreateIfNotExistsSetToFalse() throws Exception {
        URI uri = DLMTestUtil.createDLMURI(ZooKeeperClusterTestCase.zkPort, "");
        String logName = testName.getMethodName();
        String logIdentifier = "<default>";
        FutureUtils.result(ZKLogMetadataForWriter.of(uri, logName, logIdentifier, zkc.get(), zkc.getDefaultACL(), true, false));
    }

    @Test(timeout = 60000)
    public void testCreateLogMetadataWithCustomMetadata() throws Exception {
        URI uri = DLMTestUtil.createDLMURI(ZooKeeperClusterTestCase.zkPort, "");
        String logName = testName.getMethodName();
        String logIdentifier = "<default>";
        List<String> pathsToDelete = Lists.newArrayList();
        DLMetadata.create(new BKDLConfig(ZooKeeperClusterTestCase.zkServers, "/ledgers")).update(uri);
        DistributedLogNamespace namespace = com.twitter.distributedlog.namespace.DistributedLogNamespaceBuilder.newBuilder().conf(new com.twitter.distributedlog.DistributedLogConfiguration()).uri(uri).build();
        DistributedLogManager dlm = namespace.openLog(logName);
        dlm.createOrUpdateMetadata(logName.getBytes("UTF-8"));
        dlm.close();
        testCreateLogMetadataWithMissingPaths(uri, logName, logIdentifier, pathsToDelete, true, false);
    }
}

