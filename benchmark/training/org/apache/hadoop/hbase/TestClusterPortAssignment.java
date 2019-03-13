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
package org.apache.hadoop.hbase;


import HConstants.MASTER_INFO_PORT;
import HConstants.MASTER_PORT;
import HConstants.REGIONSERVER_INFO_PORT;
import HConstants.REGIONSERVER_PORT;
import LocalHBaseCluster.ASSIGN_RANDOM_PORTS;
import java.net.BindException;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(MediumTests.class)
public class TestClusterPortAssignment {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestClusterPortAssignment.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final Logger LOG = LoggerFactory.getLogger(TestClusterPortAssignment.class);

    /**
     * Check that we can start an HBase cluster specifying a custom set of
     * RPC and infoserver ports.
     */
    @Test
    public void testClusterPortAssignment() throws Exception {
        boolean retry = false;
        do {
            int masterPort = HBaseTestingUtility.randomFreePort();
            int masterInfoPort = HBaseTestingUtility.randomFreePort();
            int rsPort = HBaseTestingUtility.randomFreePort();
            int rsInfoPort = HBaseTestingUtility.randomFreePort();
            TestClusterPortAssignment.TEST_UTIL.getConfiguration().setBoolean(ASSIGN_RANDOM_PORTS, false);
            TestClusterPortAssignment.TEST_UTIL.getConfiguration().setInt(MASTER_PORT, masterPort);
            TestClusterPortAssignment.TEST_UTIL.getConfiguration().setInt(MASTER_INFO_PORT, masterInfoPort);
            TestClusterPortAssignment.TEST_UTIL.getConfiguration().setInt(REGIONSERVER_PORT, rsPort);
            TestClusterPortAssignment.TEST_UTIL.getConfiguration().setInt(REGIONSERVER_INFO_PORT, rsInfoPort);
            try {
                MiniHBaseCluster cluster = TestClusterPortAssignment.TEST_UTIL.startMiniCluster();
                Assert.assertTrue("Cluster failed to come up", cluster.waitForActiveAndReadyMaster(30000));
                retry = false;
                Assert.assertEquals("Master RPC port is incorrect", masterPort, cluster.getMaster().getRpcServer().getListenerAddress().getPort());
                Assert.assertEquals("Master info port is incorrect", masterInfoPort, cluster.getMaster().getInfoServer().getPort());
                Assert.assertEquals("RS RPC port is incorrect", rsPort, cluster.getRegionServer(0).getRpcServer().getListenerAddress().getPort());
                Assert.assertEquals("RS info port is incorrect", rsInfoPort, cluster.getRegionServer(0).getInfoServer().getPort());
            } catch (BindException e) {
                TestClusterPortAssignment.LOG.info("Failed to bind, need to retry", e);
                retry = true;
            } finally {
                TestClusterPortAssignment.TEST_UTIL.shutdownMiniCluster();
            }
        } while (retry );
    }
}

