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
package org.apache.hadoop.hbase.regionserver;


import java.util.concurrent.Semaphore;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.zookeeper.MasterAddressTracker;
import org.apache.hadoop.hbase.zookeeper.ZKListener;
import org.apache.hadoop.hbase.zookeeper.ZKWatcher;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ RegionServerTests.class, MediumTests.class })
public class TestMasterAddressTracker {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMasterAddressTracker.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestMasterAddressTracker.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    @Rule
    public TestName name = new TestName();

    @Test
    public void testDeleteIfEquals() throws Exception {
        final ServerName sn = ServerName.valueOf("localhost", 1234, System.currentTimeMillis());
        final MasterAddressTracker addressTracker = setupMasterTracker(sn, 1772);
        try {
            Assert.assertFalse("shouldn't have deleted wrong master server.", MasterAddressTracker.deleteIfEquals(addressTracker.getWatcher(), "some other string."));
        } finally {
            Assert.assertTrue("Couldn't clean up master", MasterAddressTracker.deleteIfEquals(addressTracker.getWatcher(), sn.toString()));
        }
    }

    /**
     * Unit tests that uses ZooKeeper but does not use the master-side methods
     * but rather acts directly on ZK.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMasterAddressTrackerFromZK() throws Exception {
        // Create the master node with a dummy address
        final int infoPort = 1235;
        final ServerName sn = ServerName.valueOf("localhost", 1234, System.currentTimeMillis());
        final MasterAddressTracker addressTracker = setupMasterTracker(sn, infoPort);
        try {
            Assert.assertTrue(addressTracker.hasMaster());
            ServerName pulledAddress = addressTracker.getMasterAddress();
            Assert.assertTrue(pulledAddress.equals(sn));
            Assert.assertEquals(infoPort, addressTracker.getMasterInfoPort());
        } finally {
            Assert.assertTrue("Couldn't clean up master", MasterAddressTracker.deleteIfEquals(addressTracker.getWatcher(), sn.toString()));
        }
    }

    @Test
    public void testParsingNull() throws Exception {
        Assert.assertNull("parse on null data should return null.", MasterAddressTracker.parse(null));
    }

    @Test
    public void testNoBackups() throws Exception {
        final ServerName sn = ServerName.valueOf("localhost", 1234, System.currentTimeMillis());
        final MasterAddressTracker addressTracker = setupMasterTracker(sn, 1772);
        try {
            Assert.assertEquals("Should receive 0 for backup not found.", 0, addressTracker.getBackupMasterInfoPort(ServerName.valueOf("doesnotexist.example.com", 1234, System.currentTimeMillis())));
        } finally {
            Assert.assertTrue("Couldn't clean up master", MasterAddressTracker.deleteIfEquals(addressTracker.getWatcher(), sn.toString()));
        }
    }

    @Test
    public void testNoMaster() throws Exception {
        final MasterAddressTracker addressTracker = setupMasterTracker(null, 1772);
        Assert.assertFalse(addressTracker.hasMaster());
        Assert.assertNull("should get null master when none active.", addressTracker.getMasterAddress());
        Assert.assertEquals("Should receive 0 for backup not found.", 0, addressTracker.getMasterInfoPort());
    }

    public static class NodeCreationListener extends ZKListener {
        private static final Logger LOG = LoggerFactory.getLogger(TestMasterAddressTracker.NodeCreationListener.class);

        private Semaphore lock;

        private String node;

        public NodeCreationListener(ZKWatcher watcher, String node) {
            super(watcher);
            lock = new Semaphore(0);
            this.node = node;
        }

        @Override
        public void nodeCreated(String path) {
            if (path.equals(node)) {
                TestMasterAddressTracker.NodeCreationListener.LOG.debug((("nodeCreated(" + path) + ")"));
                lock.release();
            }
        }

        public void waitForCreation() throws InterruptedException {
            lock.acquire();
        }
    }
}

