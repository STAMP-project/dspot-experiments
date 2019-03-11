/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.coord.zk;


import java.util.List;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.test.TestingServer;
import org.apache.drill.categories.SecurityTest;
import org.apache.zookeeper.data.ACL;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Ignore("See DRILL-6823")
@Category(SecurityTest.class)
public class TestZKACL {
    private TestingServer server;

    private static final String cluster_config_znode = "test-cluster_config_znode";

    private static final byte[] cluster_config_data = "drill-node-1".getBytes();

    private static final String drill_zk_root = "drill-test-drill_zk_root";

    private static final String drill_cluster_name = "test-drillbits";

    private static final String drillClusterPath = (("/" + (TestZKACL.drill_zk_root)) + "/") + (TestZKACL.drill_cluster_name);

    private static final RetryPolicy retryPolicy = new RetryNTimes(1, 1000);

    private static final String drillUDFName = "test-udfs";

    private static final byte[] udf_data = "test-udf-1".getBytes();

    private static final String drillUDFPath = (("/" + (TestZKACL.drill_zk_root)) + "/") + (TestZKACL.drillUDFName);

    private static ACLProvider aclProviderDelegate;

    private static CuratorFramework client;

    /**
     * Test ACLs on znodes required to discover the cluster
     *
     * ZK libraries only supports one client instance per-machine per-server and it is cached.
     * This test will fail when run after other ZK tests that setup the client in a way that will cause this test to fail
     */
    @Test
    public void testClusterDiscoveryPaths() {
        try {
            String path = PathUtils.join(TestZKACL.drillClusterPath, TestZKACL.cluster_config_znode);
            TestZKACL.client.create().creatingParentsIfNeeded().forPath(path, TestZKACL.cluster_config_data);
            List<ACL> remoteACLs = TestZKACL.client.getACL().forPath(path);
            List<ACL> desiredACLs = ((ZKACLProviderDelegate) (TestZKACL.aclProviderDelegate)).aclProvider.getDrillAclForPath(TestZKACL.drillClusterPath);
            // Check the ACLs
            for (ACL remote : remoteACLs) {
                boolean found = false;
                for (ACL desired : desiredACLs) {
                    // desiredACL list is READ_ACL_UNSAFE (READ, WORLD_ANYONE) + CREATOR_ALL_ACL(ALL, AUTH)
                    // AUTH in CREATOR_ALL would translate to SASL, username. Hence the replacement
                    // Note: The username("testuser1") should match the username in java.security.auth.login.config
                    found = desired.toString().equals(remote.toString().replace("sasl", "auth").replace("testuser1", ""));
                    if (found) {
                        break;
                    }
                }
                Assert.assertTrue(found);
            }
            // check if the data can be read
            byte[] actual = TestZKACL.client.getData().forPath(path);
            Assert.assertArrayEquals("testClusterDiscoveryPaths data mismatch", TestZKACL.cluster_config_data, actual);
        } catch (Exception e) {
            throw new IllegalStateException("testClusterDiscoveryPaths failed");
        }
    }

    /**
     * Test ACLs on znodes other than ones required to discover the cluster
     *
     * ZK libraries only supports one client instance per-machine per-server and it is cached.
     * This test will fail when run after other ZK tests that setup the client in a way that will cause this test to fail
     */
    @Test
    public void testNonClusterDiscoveryPaths() {
        try {
            TestZKACL.client.create().creatingParentsIfNeeded().forPath(TestZKACL.drillUDFPath, TestZKACL.udf_data);
            List<ACL> remoteACLs = TestZKACL.client.getACL().forPath(TestZKACL.drillUDFPath);
            List<ACL> desiredACLs = ((ZKACLProviderDelegate) (TestZKACL.aclProviderDelegate)).aclProvider.getDrillAclForPath(TestZKACL.drillUDFPath);
            Assert.assertEquals(remoteACLs.size(), desiredACLs.size());
            for (ACL remote : remoteACLs) {
                boolean found = false;
                for (ACL desired : desiredACLs) {
                    // desiredACL list is READ_ACL_UNSAFE (READ, WORLD_ANYONE) + CREATOR_ALL_ACL(ALL, AUTH)
                    // AUTH in CREATOR_ALL would translate to SASL, username. Hence the replacement
                    // Note: The username("testuser1") should match the username in java.security.auth.login.config
                    found = desired.toString().equals(remote.toString().replace("sasl", "auth").replace("testuser1", ""));
                    if (found) {
                        break;
                    }
                }
                Assert.assertTrue(found);
            }
            // check if the data can be read
            byte[] actual = TestZKACL.client.getData().forPath(TestZKACL.drillUDFPath);
            Assert.assertArrayEquals("testNonClusterDiscoveryPaths data mismatch", TestZKACL.udf_data, actual);
        } catch (Exception e) {
            throw new IllegalStateException("testNonClusterDiscoveryPaths failed");
        }
    }
}

