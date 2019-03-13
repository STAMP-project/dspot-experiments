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
package org.apache.hadoop.hbase.zookeeper;


import CreateMode.PERSISTENT;
import ZooDefs.Ids.CREATOR_ALL_ACL;
import java.io.IOException;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Abortable;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseZKTestingUtility;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.ZKTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Threads;
import org.apache.hadoop.hbase.zookeeper.ZKUtil.ZKUtilOp;
import org.apache.hbase.thirdparty.com.google.common.collect.ImmutableList;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static EmptyWatcher.instance;


@Category({ ZKTests.class, MediumTests.class })
public class TestZKUtil {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestZKUtil.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestZKUtil.class);

    private static HBaseZKTestingUtility UTIL = new HBaseZKTestingUtility();

    private static ZKWatcher ZKW;

    /**
     * Create a znode with data
     */
    @Test
    public void testCreateWithParents() throws InterruptedException, KeeperException {
        byte[] expectedData = new byte[]{ 1, 2, 3 };
        ZKUtil.createWithParents(TestZKUtil.ZKW, "/l1/l2/l3/l4/testCreateWithParents", expectedData);
        byte[] data = ZKUtil.getData(TestZKUtil.ZKW, "/l1/l2/l3/l4/testCreateWithParents");
        Assert.assertTrue(Bytes.equals(expectedData, data));
        ZKUtil.deleteNodeRecursively(TestZKUtil.ZKW, "/l1");
        ZKUtil.createWithParents(TestZKUtil.ZKW, "/testCreateWithParents", expectedData);
        data = ZKUtil.getData(TestZKUtil.ZKW, "/testCreateWithParents");
        Assert.assertTrue(Bytes.equals(expectedData, data));
        ZKUtil.deleteNodeRecursively(TestZKUtil.ZKW, "/testCreateWithParents");
    }

    /**
     * Create a bunch of znodes in a hierarchy, try deleting one that has childs (it will fail), then
     * delete it recursively, then delete the last znode
     */
    @Test
    public void testZNodeDeletes() throws Exception {
        ZKUtil.createWithParents(TestZKUtil.ZKW, "/l1/l2/l3/l4");
        try {
            ZKUtil.deleteNode(TestZKUtil.ZKW, "/l1/l2");
            Assert.fail("We should not be able to delete if znode has childs");
        } catch (KeeperException ex) {
            Assert.assertNotNull(ZKUtil.getDataNoWatch(TestZKUtil.ZKW, "/l1/l2/l3/l4", null));
        }
        ZKUtil.deleteNodeRecursively(TestZKUtil.ZKW, "/l1/l2");
        // make sure it really is deleted
        Assert.assertNull(ZKUtil.getDataNoWatch(TestZKUtil.ZKW, "/l1/l2/l3/l4", null));
        // do the same delete again and make sure it doesn't crash
        ZKUtil.deleteNodeRecursively(TestZKUtil.ZKW, "/l1/l2");
        ZKUtil.deleteNode(TestZKUtil.ZKW, "/l1");
        Assert.assertNull(ZKUtil.getDataNoWatch(TestZKUtil.ZKW, "/l1/l2", null));
    }

    @Test
    public void testSetDataWithVersion() throws Exception {
        ZKUtil.createWithParents(TestZKUtil.ZKW, "/s1/s2/s3");
        int v0 = getZNodeDataVersion("/s1/s2/s3");
        Assert.assertEquals(0, v0);
        ZKUtil.setData(TestZKUtil.ZKW, "/s1/s2/s3", Bytes.toBytes(12L));
        int v1 = getZNodeDataVersion("/s1/s2/s3");
        Assert.assertEquals(1, v1);
        ZKUtil.multiOrSequential(TestZKUtil.ZKW, ImmutableList.of(ZKUtilOp.setData("/s1/s2/s3", Bytes.toBytes(13L), v1)), false);
        int v2 = getZNodeDataVersion("/s1/s2/s3");
        Assert.assertEquals(2, v2);
    }

    /**
     * A test for HBASE-3238
     *
     * @throws IOException
     * 		A connection attempt to zk failed
     * @throws InterruptedException
     * 		One of the non ZKUtil actions was interrupted
     * @throws KeeperException
     * 		Any of the zookeeper connections had a KeeperException
     */
    @Test
    public void testCreateSilentIsReallySilent() throws IOException, InterruptedException, KeeperException {
        Configuration c = getConfiguration();
        String aclZnode = "/aclRoot";
        String quorumServers = ZKConfig.getZKQuorumServersString(c);
        int sessionTimeout = 5 * 1000;// 5 seconds

        ZooKeeper zk = new ZooKeeper(quorumServers, sessionTimeout, instance);
        zk.addAuthInfo("digest", Bytes.toBytes("hbase:rox"));
        // Save the previous ACL
        Stat s = null;
        List<ACL> oldACL = null;
        while (true) {
            try {
                s = new Stat();
                oldACL = zk.getACL("/", s);
                break;
            } catch (KeeperException e) {
                switch (e.code()) {
                    case CONNECTIONLOSS :
                    case SESSIONEXPIRED :
                    case OPERATIONTIMEOUT :
                        TestZKUtil.LOG.warn("Possibly transient ZooKeeper exception", e);
                        Threads.sleep(100);
                        break;
                    default :
                        throw e;
                }
            }
        } 
        // I set this acl after the attempted creation of the cluster home node.
        // Add retries in case of retryable zk exceptions.
        while (true) {
            try {
                zk.setACL("/", CREATOR_ALL_ACL, (-1));
                break;
            } catch (KeeperException e) {
                switch (e.code()) {
                    case CONNECTIONLOSS :
                    case SESSIONEXPIRED :
                    case OPERATIONTIMEOUT :
                        TestZKUtil.LOG.warn(("Possibly transient ZooKeeper exception: " + e));
                        Threads.sleep(100);
                        break;
                    default :
                        throw e;
                }
            }
        } 
        while (true) {
            try {
                zk.create(aclZnode, null, CREATOR_ALL_ACL, PERSISTENT);
                break;
            } catch (KeeperException e) {
                switch (e.code()) {
                    case CONNECTIONLOSS :
                    case SESSIONEXPIRED :
                    case OPERATIONTIMEOUT :
                        TestZKUtil.LOG.warn(("Possibly transient ZooKeeper exception: " + e));
                        Threads.sleep(100);
                        break;
                    default :
                        throw e;
                }
            }
        } 
        zk.close();
        ZKUtil.createAndFailSilent(TestZKUtil.ZKW, aclZnode);
        // Restore the ACL
        ZooKeeper zk3 = new ZooKeeper(quorumServers, sessionTimeout, instance);
        zk3.addAuthInfo("digest", Bytes.toBytes("hbase:rox"));
        try {
            zk3.setACL("/", oldACL, (-1));
        } finally {
            zk3.close();
        }
    }

    /**
     * Test should not fail with NPE when getChildDataAndWatchForNewChildren invoked with wrongNode
     */
    @Test
    @SuppressWarnings("deprecation")
    public void testGetChildDataAndWatchForNewChildrenShouldNotThrowNPE() throws Exception {
        ZKUtil.getChildDataAndWatchForNewChildren(TestZKUtil.ZKW, "/wrongNode");
    }

    private static class WarnOnlyAbortable implements Abortable {
        @Override
        public void abort(String why, Throwable e) {
            TestZKUtil.LOG.warn(("ZKWatcher received abort, ignoring.  Reason: " + why));
            if (TestZKUtil.LOG.isDebugEnabled()) {
                TestZKUtil.LOG.debug(e.toString(), e);
            }
        }

        @Override
        public boolean isAborted() {
            return false;
        }
    }
}

