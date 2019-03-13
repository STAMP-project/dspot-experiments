/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.apache.storm.blobstore;


import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.curator.test.TestingServer;
import org.apache.storm.nimbus.NimbusInfo;
import org.apache.storm.shade.org.apache.curator.framework.CuratorFramework;
import org.apache.storm.shade.org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.storm.shade.org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.server.NIOServerCnxnFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for most of the testable utility methods
 *  and LocalFsBlobStoreSynchronizer class methods
 */
public class LocalFsBlobStoreSynchronizerTest {
    private URI base;

    private File baseFile;

    private static Map<String, Object> conf = new HashMap();

    private NIOServerCnxnFactory factory;

    @Test
    public void testBlobSynchronizerForKeysToDownload() {
        BlobStore store = initLocalFs();
        LocalFsBlobStoreSynchronizer sync = new LocalFsBlobStoreSynchronizer(store, LocalFsBlobStoreSynchronizerTest.conf);
        // test for keylist to download
        Set<String> zkSet = new HashSet<String>();
        zkSet.add("key1");
        Set<String> blobStoreSet = new HashSet<String>();
        blobStoreSet.add("key1");
        Set<String> resultSet = sync.getKeySetToDownload(blobStoreSet, zkSet);
        Assert.assertTrue("Not Empty", resultSet.isEmpty());
        zkSet.add("key1");
        blobStoreSet.add("key2");
        resultSet = sync.getKeySetToDownload(blobStoreSet, zkSet);
        Assert.assertTrue("Not Empty", resultSet.isEmpty());
        blobStoreSet.remove("key1");
        blobStoreSet.remove("key2");
        zkSet.add("key1");
        resultSet = sync.getKeySetToDownload(blobStoreSet, zkSet);
        Assert.assertTrue("Unexpected keys to download", (((resultSet.size()) == 1) && (resultSet.contains("key1"))));
    }

    @Test
    public void testGetLatestSequenceNumber() throws Exception {
        List<String> stateInfoList = new ArrayList<String>();
        stateInfoList.add("nimbus1:8000-2");
        stateInfoList.add("nimbus-1:8000-4");
        Assert.assertTrue("Failed to get the latest version", ((BlobStoreUtils.getLatestSequenceNumber(stateInfoList)) == 4));
    }

    @Test
    public void testNimbodesWithLatestVersionOfBlob() throws Exception {
        try (TestingServer server = new TestingServer();CuratorFramework zkClient = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3))) {
            zkClient.start();
            // Creating nimbus hosts containing latest version of blob
            zkClient.create().creatingParentContainersIfNeeded().forPath("/blobstore/key1/nimbus1:7800-1");
            zkClient.create().creatingParentContainersIfNeeded().forPath("/blobstore/key1/nimbus2:7800-2");
            Set<NimbusInfo> set = BlobStoreUtils.getNimbodesWithLatestSequenceNumberOfBlob(zkClient, "key1");
            Assert.assertEquals("Failed to get the correct nimbus hosts with latest blob version", set.iterator().next().getHost(), "nimbus2");
            zkClient.delete().deletingChildrenIfNeeded().forPath("/blobstore/key1/nimbus1:7800-1");
            zkClient.delete().deletingChildrenIfNeeded().forPath("/blobstore/key1/nimbus2:7800-2");
        }
    }

    @Test
    public void testNormalizeVersionInfo() throws Exception {
        BlobKeySequenceInfo info1 = BlobStoreUtils.normalizeNimbusHostPortSequenceNumberInfo("nimbus1:7800-1");
        Assert.assertTrue(info1.getNimbusHostPort().equals("nimbus1:7800"));
        Assert.assertTrue(info1.getSequenceNumber().equals("1"));
        BlobKeySequenceInfo info2 = BlobStoreUtils.normalizeNimbusHostPortSequenceNumberInfo("nimbus-1:7800-1");
        Assert.assertTrue(info2.getNimbusHostPort().equals("nimbus-1:7800"));
        Assert.assertTrue(info2.getSequenceNumber().equals("1"));
    }
}

