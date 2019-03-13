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
package org.apache.hadoop.yarn.server.resourcemanager.recovery;


import CommonConfigurationKeys.ZK_ACL;
import CommonConfigurationKeys.ZK_ADDRESS;
import CommonConfigurationKeys.ZK_AUTH;
import CommonConfigurationKeys.ZK_NUM_RETRIES;
import CommonConfigurationKeys.ZK_RETRY_INTERVAL_MS;
import CommonConfigurationKeys.ZK_TIMEOUT_MS;
import YarnConfiguration.ZK_RM_STATE_STORE_PARENT_PATH;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.curator.test.TestingServer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ZKUtil;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestZKRMStateStoreZKClientConnections {
    private static final Logger LOG = LoggerFactory.getLogger(TestZKRMStateStoreZKClientConnections.class);

    private static final int ZK_TIMEOUT_MS = 1000;

    private static final String DIGEST_USER_PASS = "test-user:test-password";

    private static final String TEST_AUTH_GOOD = "digest:" + (TestZKRMStateStoreZKClientConnections.DIGEST_USER_PASS);

    private static final String DIGEST_USER_HASH;

    static {
        try {
            DIGEST_USER_HASH = DigestAuthenticationProvider.generateDigest(TestZKRMStateStoreZKClientConnections.DIGEST_USER_PASS);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String TEST_ACL = ("digest:" + (TestZKRMStateStoreZKClientConnections.DIGEST_USER_HASH)) + ":rwcda";

    private TestingServer testingServer;

    class TestZKClient {
        ZKRMStateStore store;

        protected class TestZKRMStateStore extends ZKRMStateStore {
            public TestZKRMStateStore(Configuration conf, String workingZnode) throws Exception {
                setResourceManager(new ResourceManager());
                init(conf);
                start();
                Assert.assertTrue(znodeWorkingPath.equals(workingZnode));
            }
        }

        public RMStateStore getRMStateStore(Configuration conf) throws Exception {
            String workingZnode = "/Test";
            conf.set(ZK_ADDRESS, testingServer.getConnectString());
            conf.set(ZK_RM_STATE_STORE_PARENT_PATH, workingZnode);
            this.store = new TestZKRMStateStoreZKClientConnections.TestZKClient.TestZKRMStateStore(conf, workingZnode);
            return this.store;
        }
    }

    @Test(timeout = 20000)
    public void testZKClientRetry() throws Exception {
        TestZKRMStateStoreZKClientConnections.TestZKClient zkClientTester = new TestZKRMStateStoreZKClientConnections.TestZKClient();
        final String path = "/test";
        YarnConfiguration conf = new YarnConfiguration();
        conf.setInt(CommonConfigurationKeys.ZK_TIMEOUT_MS, TestZKRMStateStoreZKClientConnections.ZK_TIMEOUT_MS);
        conf.setLong(ZK_RETRY_INTERVAL_MS, 100);
        final ZKRMStateStore store = ((ZKRMStateStore) (zkClientTester.getRMStateStore(conf)));
        RMStateStoreTestBase.TestDispatcher dispatcher = new RMStateStoreTestBase.TestDispatcher();
        store.setRMDispatcher(dispatcher);
        final AtomicBoolean assertionFailedInThread = new AtomicBoolean(false);
        testingServer.stop();
        Thread clientThread = new Thread() {
            @Override
            public void run() {
                try {
                    store.getData(path);
                } catch (Exception e) {
                    e.printStackTrace();
                    assertionFailedInThread.set(true);
                }
            }
        };
        Thread.sleep(2000);
        testingServer.start();
        clientThread.join();
        Assert.assertFalse(assertionFailedInThread.get());
    }

    @Test(timeout = 20000)
    public void testSetZKAcl() {
        TestZKRMStateStoreZKClientConnections.TestZKClient zkClientTester = new TestZKRMStateStoreZKClientConnections.TestZKClient();
        YarnConfiguration conf = new YarnConfiguration();
        conf.set(ZK_ACL, "world:anyone:rwca");
        try {
            zkClientTester.store.delete(zkClientTester.store.znodeWorkingPath);
            Assert.fail("Shouldn't be able to delete path");
        } catch (Exception e) {
            /* expected behavior */
        }
    }

    @Test(timeout = 20000)
    public void testInvalidZKAclConfiguration() {
        TestZKRMStateStoreZKClientConnections.TestZKClient zkClientTester = new TestZKRMStateStoreZKClientConnections.TestZKClient();
        YarnConfiguration conf = new YarnConfiguration();
        conf.set(ZK_ACL, "randomstring&*");
        try {
            zkClientTester.getRMStateStore(conf);
            Assert.fail("ZKRMStateStore created with bad ACL");
        } catch (ZKUtil bafe) {
            // expected behavior
        } catch (Exception e) {
            String error = "Incorrect exception on BadAclFormat";
            TestZKRMStateStoreZKClientConnections.LOG.error(error, e);
            Assert.fail(error);
        }
    }

    @Test
    public void testZKAuths() throws Exception {
        TestZKRMStateStoreZKClientConnections.TestZKClient zkClientTester = new TestZKRMStateStoreZKClientConnections.TestZKClient();
        YarnConfiguration conf = new YarnConfiguration();
        conf.setInt(ZK_NUM_RETRIES, 1);
        conf.setInt(CommonConfigurationKeys.ZK_TIMEOUT_MS, TestZKRMStateStoreZKClientConnections.ZK_TIMEOUT_MS);
        conf.set(ZK_ACL, TestZKRMStateStoreZKClientConnections.TEST_ACL);
        conf.set(ZK_AUTH, TestZKRMStateStoreZKClientConnections.TEST_AUTH_GOOD);
        zkClientTester.getRMStateStore(conf);
    }
}

