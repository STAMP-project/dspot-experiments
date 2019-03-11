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
package com.twitter.distributedlog.util;


import CreateMode.PERSISTENT;
import ZooDefs.Ids.OPEN_ACL_UNSAFE;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.twitter.distributedlog.ZooKeeperClient;
import com.twitter.distributedlog.ZooKeeperClusterTestCase;
import java.util.concurrent.CountDownLatch;
import org.apache.bookkeeper.versioning.Versioned;
import org.apache.zookeeper.AsyncCallback;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test Utils
 */
public class TestUtils extends ZooKeeperClusterTestCase {
    private static final int sessionTimeoutMs = 30000;

    private ZooKeeperClient zkc;

    @Test(timeout = 60000)
    public void testZkAsyncCreateFulPathOptimisticRecursive() throws Exception {
        String path1 = "/a/b/c/d";
        Optional<String> parentPathShouldNotCreate = Optional.absent();
        final CountDownLatch doneLatch1 = new CountDownLatch(1);
        Utils.zkAsyncCreateFullPathOptimisticRecursive(zkc, path1, parentPathShouldNotCreate, new byte[0], OPEN_ACL_UNSAFE, PERSISTENT, new AsyncCallback.StringCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, String name) {
                doneLatch1.countDown();
            }
        }, null);
        doneLatch1.await();
        Assert.assertNotNull(zkc.get().exists(path1, false));
        String path2 = "/a/b/c/d/e/f/g";
        parentPathShouldNotCreate = Optional.of("/a/b/c/d/e");
        final CountDownLatch doneLatch2 = new CountDownLatch(1);
        Utils.zkAsyncCreateFullPathOptimisticRecursive(zkc, path2, parentPathShouldNotCreate, new byte[0], OPEN_ACL_UNSAFE, PERSISTENT, new AsyncCallback.StringCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, String name) {
                doneLatch2.countDown();
            }
        }, null);
        doneLatch2.await();
        Assert.assertNull(zkc.get().exists("/a/b/c/d/e", false));
        Assert.assertNull(zkc.get().exists("/a/b/c/d/e/f", false));
        Assert.assertNull(zkc.get().exists("/a/b/c/d/e/f/g", false));
        parentPathShouldNotCreate = Optional.of("/a/b");
        final CountDownLatch doneLatch3 = new CountDownLatch(1);
        Utils.zkAsyncCreateFullPathOptimisticRecursive(zkc, path2, parentPathShouldNotCreate, new byte[0], OPEN_ACL_UNSAFE, PERSISTENT, new AsyncCallback.StringCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, String name) {
                doneLatch3.countDown();
            }
        }, null);
        doneLatch3.await();
        Assert.assertNotNull(zkc.get().exists(path2, false));
    }

    @Test(timeout = 60000)
    public void testZkGetData() throws Exception {
        String path1 = "/zk-get-data/non-existent-path";
        Versioned<byte[]> data = FutureUtils.result(Utils.zkGetData(zkc.get(), path1, false));
        Assert.assertNull("No data should return from non-existent-path", data.getValue());
        Assert.assertNull("No version should return from non-existent-path", data.getVersion());
        String path2 = "/zk-get-data/path2";
        byte[] rawData = "test-data".getBytes(Charsets.UTF_8);
        FutureUtils.result(Utils.zkAsyncCreateFullPathOptimistic(zkc, path2, rawData, zkc.getDefaultACL(), PERSISTENT));
        data = FutureUtils.result(Utils.zkGetData(zkc.get(), path2, false));
        Assert.assertArrayEquals("Data should return as written", rawData, data.getValue());
        Assert.assertEquals("Version should be zero", 0, getZnodeVersion());
    }
}

