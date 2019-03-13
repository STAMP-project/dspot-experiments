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
package org.apache.hadoop.hbase.security.token;


import java.util.concurrent.CountDownLatch;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Abortable;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.SecurityTests;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;
import org.apache.hadoop.hbase.zookeeper.ZKWatcher;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test the synchronization of token authentication master keys through
 * ZKSecretWatcher
 */
@Category({ SecurityTests.class, LargeTests.class })
public class TestZKSecretWatcher {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestZKSecretWatcher.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestZKSecretWatcher.class);

    private static HBaseTestingUtility TEST_UTIL;

    private static AuthenticationTokenSecretManager KEY_MASTER;

    private static TestZKSecretWatcher.AuthenticationTokenSecretManagerForTest KEY_SLAVE;

    private static AuthenticationTokenSecretManager KEY_SLAVE2;

    private static AuthenticationTokenSecretManager KEY_SLAVE3;

    private static class MockAbortable implements Abortable {
        private boolean abort;

        @Override
        public void abort(String reason, Throwable e) {
            TestZKSecretWatcher.LOG.info(("Aborting: " + reason), e);
            abort = true;
        }

        @Override
        public boolean isAborted() {
            return abort;
        }
    }

    // We subclass AuthenticationTokenSecretManager so that testKeyUpdate can receive
    // notification on the removal of keyId
    private static class AuthenticationTokenSecretManagerForTest extends AuthenticationTokenSecretManager {
        private CountDownLatch latch = new CountDownLatch(1);

        public AuthenticationTokenSecretManagerForTest(Configuration conf, ZKWatcher zk, String serverName, long keyUpdateInterval, long tokenMaxLifetime) {
            super(conf, zk, serverName, keyUpdateInterval, tokenMaxLifetime);
        }

        @Override
        synchronized boolean removeKey(Integer keyId) {
            boolean b = super.removeKey(keyId);
            if (b) {
                latch.countDown();
            }
            return b;
        }

        CountDownLatch getLatch() {
            return latch;
        }
    }

    @Test
    public void testKeyUpdate() throws Exception {
        // sanity check
        Assert.assertTrue(TestZKSecretWatcher.KEY_MASTER.isMaster());
        Assert.assertFalse(isMaster());
        int maxKeyId = 0;
        TestZKSecretWatcher.KEY_MASTER.rollCurrentKey();
        AuthenticationKey key1 = TestZKSecretWatcher.KEY_MASTER.getCurrentKey();
        Assert.assertNotNull(key1);
        TestZKSecretWatcher.LOG.debug(("Master current key: " + (key1.getKeyId())));
        // wait for slave to update
        Thread.sleep(1000);
        AuthenticationKey slaveCurrent = getCurrentKey();
        Assert.assertNotNull(slaveCurrent);
        Assert.assertEquals(key1, slaveCurrent);
        TestZKSecretWatcher.LOG.debug(("Slave current key: " + (slaveCurrent.getKeyId())));
        // generate two more keys then expire the original
        TestZKSecretWatcher.KEY_MASTER.rollCurrentKey();
        AuthenticationKey key2 = TestZKSecretWatcher.KEY_MASTER.getCurrentKey();
        TestZKSecretWatcher.LOG.debug(("Master new current key: " + (key2.getKeyId())));
        TestZKSecretWatcher.KEY_MASTER.rollCurrentKey();
        AuthenticationKey key3 = TestZKSecretWatcher.KEY_MASTER.getCurrentKey();
        TestZKSecretWatcher.LOG.debug(("Master new current key: " + (key3.getKeyId())));
        // force expire the original key
        key1.setExpiration(((EnvironmentEdgeManager.currentTime()) - 1000));
        TestZKSecretWatcher.KEY_MASTER.removeExpiredKeys();
        // verify removed from master
        Assert.assertNull(TestZKSecretWatcher.KEY_MASTER.getKey(key1.getKeyId()));
        // wait for slave to catch up
        TestZKSecretWatcher.KEY_SLAVE.getLatch().await();
        // make sure the slave has both new keys
        AuthenticationKey slave2 = TestZKSecretWatcher.KEY_SLAVE.getKey(key2.getKeyId());
        Assert.assertNotNull(slave2);
        Assert.assertEquals(key2, slave2);
        AuthenticationKey slave3 = TestZKSecretWatcher.KEY_SLAVE.getKey(key3.getKeyId());
        Assert.assertNotNull(slave3);
        Assert.assertEquals(key3, slave3);
        slaveCurrent = TestZKSecretWatcher.KEY_SLAVE.getCurrentKey();
        Assert.assertEquals(key3, slaveCurrent);
        TestZKSecretWatcher.LOG.debug(("Slave current key: " + (slaveCurrent.getKeyId())));
        // verify that the expired key has been removed
        Assert.assertNull(TestZKSecretWatcher.KEY_SLAVE.getKey(key1.getKeyId()));
        // bring up a new slave
        Configuration conf = TestZKSecretWatcher.TEST_UTIL.getConfiguration();
        ZKWatcher zk = TestZKSecretWatcher.newZK(conf, "server3", new TestZKSecretWatcher.MockAbortable());
        TestZKSecretWatcher.KEY_SLAVE2 = new AuthenticationTokenSecretManager(conf, zk, "server3", ((60 * 60) * 1000), (60 * 1000));
        TestZKSecretWatcher.KEY_SLAVE2.start();
        Thread.sleep(1000);
        // verify the new slave has current keys (and not expired)
        slave2 = TestZKSecretWatcher.KEY_SLAVE2.getKey(key2.getKeyId());
        Assert.assertNotNull(slave2);
        Assert.assertEquals(key2, slave2);
        slave3 = TestZKSecretWatcher.KEY_SLAVE2.getKey(key3.getKeyId());
        Assert.assertNotNull(slave3);
        Assert.assertEquals(key3, slave3);
        slaveCurrent = TestZKSecretWatcher.KEY_SLAVE2.getCurrentKey();
        Assert.assertEquals(key3, slaveCurrent);
        Assert.assertNull(TestZKSecretWatcher.KEY_SLAVE2.getKey(key1.getKeyId()));
        // test leader failover
        TestZKSecretWatcher.KEY_MASTER.stop();
        // wait for master to stop
        Thread.sleep(1000);
        Assert.assertFalse(TestZKSecretWatcher.KEY_MASTER.isMaster());
        // check for a new master
        AuthenticationTokenSecretManager[] mgrs = new AuthenticationTokenSecretManager[]{ TestZKSecretWatcher.KEY_SLAVE, TestZKSecretWatcher.KEY_SLAVE2 };
        AuthenticationTokenSecretManager newMaster = null;
        int tries = 0;
        while ((newMaster == null) && ((tries++) < 5)) {
            for (AuthenticationTokenSecretManager mgr : mgrs) {
                if (mgr.isMaster()) {
                    newMaster = mgr;
                    break;
                }
            }
            if (newMaster == null) {
                Thread.sleep(500);
            }
        } 
        Assert.assertNotNull(newMaster);
        AuthenticationKey current = newMaster.getCurrentKey();
        // new master will immediately roll the current key, so it's current may be greater
        Assert.assertTrue(((current.getKeyId()) >= (slaveCurrent.getKeyId())));
        TestZKSecretWatcher.LOG.debug(("New master, current key: " + (current.getKeyId())));
        // roll the current key again on new master and verify the key ID increments
        newMaster.rollCurrentKey();
        AuthenticationKey newCurrent = newMaster.getCurrentKey();
        TestZKSecretWatcher.LOG.debug(("New master, rolled new current key: " + (newCurrent.getKeyId())));
        Assert.assertTrue(((newCurrent.getKeyId()) > (current.getKeyId())));
        // add another slave
        ZKWatcher zk3 = TestZKSecretWatcher.newZK(conf, "server4", new TestZKSecretWatcher.MockAbortable());
        TestZKSecretWatcher.KEY_SLAVE3 = new AuthenticationTokenSecretManager(conf, zk3, "server4", ((60 * 60) * 1000), (60 * 1000));
        TestZKSecretWatcher.KEY_SLAVE3.start();
        Thread.sleep(5000);
        // check master failover again
        newMaster.stop();
        // wait for master to stop
        Thread.sleep(5000);
        Assert.assertFalse(newMaster.isMaster());
        // check for a new master
        mgrs = new AuthenticationTokenSecretManager[]{ TestZKSecretWatcher.KEY_SLAVE, TestZKSecretWatcher.KEY_SLAVE2, TestZKSecretWatcher.KEY_SLAVE3 };
        newMaster = null;
        tries = 0;
        while ((newMaster == null) && ((tries++) < 5)) {
            for (AuthenticationTokenSecretManager mgr : mgrs) {
                if (mgr.isMaster()) {
                    newMaster = mgr;
                    break;
                }
            }
            if (newMaster == null) {
                Thread.sleep(500);
            }
        } 
        Assert.assertNotNull(newMaster);
        AuthenticationKey current2 = newMaster.getCurrentKey();
        // new master will immediately roll the current key, so it's current may be greater
        Assert.assertTrue(((current2.getKeyId()) >= (newCurrent.getKeyId())));
        TestZKSecretWatcher.LOG.debug(("New master 2, current key: " + (current2.getKeyId())));
        // roll the current key again on new master and verify the key ID increments
        newMaster.rollCurrentKey();
        AuthenticationKey newCurrent2 = newMaster.getCurrentKey();
        TestZKSecretWatcher.LOG.debug(("New master 2, rolled new current key: " + (newCurrent2.getKeyId())));
        Assert.assertTrue(((newCurrent2.getKeyId()) > (current2.getKeyId())));
    }
}

