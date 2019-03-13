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
package org.apache.hadoop.hdfs.server.datanode;


import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.hadoop.conf.ReconfigurationException;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test to reconfigure some parameters for DataNode without restart
 */
public class TestDataNodeReconfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(TestBlockRecovery.class);

    private static final String DATA_DIR = (MiniDFSCluster.getBaseDirectory()) + "data";

    private static final InetSocketAddress NN_ADDR = new InetSocketAddress("localhost", 5020);

    private final int NUM_NAME_NODE = 1;

    private final int NUM_DATA_NODE = 10;

    private MiniDFSCluster cluster;

    @Test
    public void testMaxConcurrentMoversReconfiguration() throws IOException, ReconfigurationException {
        int maxConcurrentMovers = 10;
        for (int i = 0; i < (NUM_DATA_NODE); i++) {
            DataNode dn = cluster.getDataNodes().get(i);
            // try invalid values
            try {
                dn.reconfigureProperty(DFSConfigKeys.DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY, "text");
                Assert.fail("ReconfigurationException expected");
            } catch (ReconfigurationException expected) {
                Assert.assertTrue("expecting NumberFormatException", ((expected.getCause()) instanceof NumberFormatException));
            }
            try {
                dn.reconfigureProperty(DFSConfigKeys.DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY, String.valueOf((-1)));
                Assert.fail("ReconfigurationException expected");
            } catch (ReconfigurationException expected) {
                Assert.assertTrue("expecting IllegalArgumentException", ((expected.getCause()) instanceof IllegalArgumentException));
            }
            try {
                dn.reconfigureProperty(DFSConfigKeys.DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY, String.valueOf(0));
                Assert.fail("ReconfigurationException expected");
            } catch (ReconfigurationException expected) {
                Assert.assertTrue("expecting IllegalArgumentException", ((expected.getCause()) instanceof IllegalArgumentException));
            }
            // change properties
            dn.reconfigureProperty(DFSConfigKeys.DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY, String.valueOf(maxConcurrentMovers));
            // verify change
            Assert.assertEquals(String.format("%s has wrong value", DFSConfigKeys.DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY), maxConcurrentMovers, dn.xserver.balanceThrottler.getMaxConcurrentMovers());
            Assert.assertEquals(String.format("%s has wrong value", DFSConfigKeys.DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY), maxConcurrentMovers, Integer.parseInt(dn.getConf().get(DFSConfigKeys.DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY)));
            // revert to default
            dn.reconfigureProperty(DFSConfigKeys.DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY, null);
            // verify default
            Assert.assertEquals(String.format("%s has wrong value", DFSConfigKeys.DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY), DFSConfigKeys.DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_DEFAULT, dn.xserver.balanceThrottler.getMaxConcurrentMovers());
            Assert.assertEquals(String.format("expect %s is not configured", DFSConfigKeys.DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY), null, dn.getConf().get(DFSConfigKeys.DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY));
        }
    }

    @Test
    public void testAcquireWithMaxConcurrentMoversGreaterThanDefault() throws IOException, ReconfigurationException {
        final DataNode[] dns = createDNsForTest(1);
        try {
            testAcquireOnMaxConcurrentMoversReconfiguration(dns[0], 10);
        } finally {
            dns[0].shutdown();
        }
    }

    @Test
    public void testAcquireWithMaxConcurrentMoversLessThanDefault() throws IOException, ReconfigurationException {
        final DataNode[] dns = createDNsForTest(1);
        try {
            testAcquireOnMaxConcurrentMoversReconfiguration(dns[0], 3);
        } finally {
            dns[0].shutdown();
        }
    }

    /**
     * Simulates a scenario where the DataNode has been reconfigured with fewer
     * mover threads, but all of the current treads are busy and therefore the
     * DataNode is unable to honor this request within a reasonable amount of
     * time. The DataNode eventually gives up and returns a flag indicating that
     * the request was not honored.
     */
    @Test
    public void testFailedDecreaseConcurrentMovers() throws IOException, ReconfigurationException {
        final DataNode[] dns = createDNsForTest(1);
        final DataNode dataNode = dns[0];
        try {
            // Set the current max to 2
            dataNode.xserver.updateBalancerMaxConcurrentMovers(2);
            // Simulate grabbing 2 threads
            dataNode.xserver.balanceThrottler.acquire();
            dataNode.xserver.balanceThrottler.acquire();
            dataNode.xserver.setMaxReconfigureWaitTime(1);
            // Attempt to set new maximum to 1
            final boolean success = dataNode.xserver.updateBalancerMaxConcurrentMovers(1);
            Assert.assertFalse(success);
        } finally {
            dataNode.shutdown();
        }
    }

    /**
     * Test with invalid configuration.
     */
    @Test(expected = ReconfigurationException.class)
    public void testFailedDecreaseConcurrentMoversReconfiguration() throws IOException, ReconfigurationException {
        final DataNode[] dns = createDNsForTest(1);
        final DataNode dataNode = dns[0];
        try {
            // Set the current max to 2
            dataNode.xserver.updateBalancerMaxConcurrentMovers(2);
            // Simulate grabbing 2 threads
            dataNode.xserver.balanceThrottler.acquire();
            dataNode.xserver.balanceThrottler.acquire();
            dataNode.xserver.setMaxReconfigureWaitTime(1);
            // Now try reconfigure maximum downwards with threads released
            dataNode.reconfigurePropertyImpl(DFSConfigKeys.DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY, "1");
        } catch (ReconfigurationException e) {
            Assert.assertEquals(DFSConfigKeys.DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY, e.getProperty());
            Assert.assertEquals("1", e.getNewValue());
            throw e;
        } finally {
            dataNode.shutdown();
        }
    }
}

