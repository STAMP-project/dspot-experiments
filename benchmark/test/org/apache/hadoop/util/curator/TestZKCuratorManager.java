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
package org.apache.hadoop.util.curator;


import CommonConfigurationKeys.ZK_ACL_DEFAULT;
import CreateMode.PERSISTENT;
import ZKCuratorManager.SafeTransaction;
import java.util.Arrays;
import java.util.List;
import org.apache.curator.test.TestingServer;
import org.apache.hadoop.util.ZKUtil;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the manager for ZooKeeper Curator.
 */
public class TestZKCuratorManager {
    private TestingServer server;

    private ZKCuratorManager curator;

    @Test
    public void testReadWriteData() throws Exception {
        String testZNode = "/test";
        String expectedString = "testString";
        Assert.assertFalse(curator.exists(testZNode));
        curator.create(testZNode);
        Assert.assertTrue(curator.exists(testZNode));
        curator.setData(testZNode, expectedString, (-1));
        String testString = curator.getStringData("/test");
        Assert.assertEquals(expectedString, testString);
    }

    @Test
    public void testChildren() throws Exception {
        List<String> children = curator.getChildren("/");
        Assert.assertEquals(1, children.size());
        Assert.assertFalse(curator.exists("/node1"));
        curator.create("/node1");
        Assert.assertTrue(curator.exists("/node1"));
        Assert.assertFalse(curator.exists("/node2"));
        curator.create("/node2");
        Assert.assertTrue(curator.exists("/node2"));
        children = curator.getChildren("/");
        Assert.assertEquals(3, children.size());
        curator.delete("/node2");
        Assert.assertFalse(curator.exists("/node2"));
        children = curator.getChildren("/");
        Assert.assertEquals(2, children.size());
    }

    @Test
    public void testGetStringData() throws Exception {
        String node1 = "/node1";
        String node2 = "/node2";
        Assert.assertFalse(curator.exists(node1));
        curator.create(node1);
        Assert.assertNull(curator.getStringData(node1));
        byte[] setData = "setData".getBytes("UTF-8");
        curator.setData(node1, setData, (-1));
        Assert.assertEquals("setData", curator.getStringData(node1));
        Stat stat = new Stat();
        Assert.assertFalse(curator.exists(node2));
        curator.create(node2);
        Assert.assertNull(curator.getStringData(node2, stat));
        curator.setData(node2, setData, (-1));
        Assert.assertEquals("setData", curator.getStringData(node2, stat));
    }

    @Test
    public void testTransaction() throws Exception {
        List<ACL> zkAcl = ZKUtil.parseACLs(ZK_ACL_DEFAULT);
        String fencingNodePath = "/fencing";
        String node1 = "/node1";
        String node2 = "/node2";
        byte[] testData = "testData".getBytes("UTF-8");
        Assert.assertFalse(curator.exists(fencingNodePath));
        Assert.assertFalse(curator.exists(node1));
        Assert.assertFalse(curator.exists(node2));
        ZKCuratorManager.SafeTransaction txn = curator.createTransaction(zkAcl, fencingNodePath);
        txn.create(node1, testData, zkAcl, PERSISTENT);
        txn.create(node2, testData, zkAcl, PERSISTENT);
        Assert.assertFalse(curator.exists(fencingNodePath));
        Assert.assertFalse(curator.exists(node1));
        Assert.assertFalse(curator.exists(node2));
        txn.commit();
        Assert.assertFalse(curator.exists(fencingNodePath));
        Assert.assertTrue(curator.exists(node1));
        Assert.assertTrue(curator.exists(node2));
        Assert.assertTrue(Arrays.equals(testData, curator.getData(node1)));
        Assert.assertTrue(Arrays.equals(testData, curator.getData(node2)));
        byte[] setData = "setData".getBytes("UTF-8");
        txn = curator.createTransaction(zkAcl, fencingNodePath);
        txn.setData(node1, setData, (-1));
        txn.delete(node2);
        Assert.assertTrue(curator.exists(node2));
        Assert.assertTrue(Arrays.equals(testData, curator.getData(node1)));
        txn.commit();
        Assert.assertFalse(curator.exists(node2));
        Assert.assertTrue(Arrays.equals(setData, curator.getData(node1)));
    }
}

