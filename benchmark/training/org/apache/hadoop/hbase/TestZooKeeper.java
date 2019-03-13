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


import HConstants.CATALOG_FAMILY;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.master.balancer.SimpleLoadBalancer;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.zookeeper.ZKWatcher;
import org.apache.zookeeper.KeeperException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MiscTests.class, LargeTests.class })
public class TestZooKeeper {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestZooKeeper.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestZooKeeper.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    @Rule
    public TestName name = new TestName();

    @Test
    public void testRegionServerSessionExpired() throws Exception {
        TestZooKeeper.LOG.info(("Starting " + (name.getMethodName())));
        TestZooKeeper.TEST_UTIL.expireRegionServerSession(0);
        testSanity(name.getMethodName());
    }

    @Test
    public void testMasterSessionExpired() throws Exception {
        TestZooKeeper.LOG.info(("Starting " + (name.getMethodName())));
        TestZooKeeper.TEST_UTIL.expireMasterSession();
        testSanity(name.getMethodName());
    }

    /**
     * Master recovery when the znode already exists. Internally, this
     *  test differs from {@link #testMasterSessionExpired} because here
     *  the master znode will exist in ZK.
     */
    @Test
    public void testMasterZKSessionRecoveryFailure() throws Exception {
        TestZooKeeper.LOG.info(("Starting " + (name.getMethodName())));
        MiniHBaseCluster cluster = TestZooKeeper.TEST_UTIL.getHBaseCluster();
        HMaster m = cluster.getMaster();
        m.abort("Test recovery from zk session expired", new KeeperException.SessionExpiredException());
        Assert.assertTrue(m.isStopped());// Master doesn't recover any more

        testSanity(name.getMethodName());
    }

    /**
     * Tests that the master does not call retainAssignment after recovery from expired zookeeper
     * session. Without the HBASE-6046 fix master always tries to assign all the user regions by
     * calling retainAssignment.
     */
    @Test
    public void testRegionAssignmentAfterMasterRecoveryDueToZKExpiry() throws Exception {
        MiniHBaseCluster cluster = TestZooKeeper.TEST_UTIL.getHBaseCluster();
        cluster.startRegionServer();
        cluster.waitForActiveAndReadyMaster(10000);
        HMaster m = cluster.getMaster();
        final ZKWatcher zkw = m.getZooKeeper();
        // now the cluster is up. So assign some regions.
        try (Admin admin = TestZooKeeper.TEST_UTIL.getAdmin()) {
            byte[][] SPLIT_KEYS = new byte[][]{ Bytes.toBytes("a"), Bytes.toBytes("b"), Bytes.toBytes("c"), Bytes.toBytes("d"), Bytes.toBytes("e"), Bytes.toBytes("f"), Bytes.toBytes("g"), Bytes.toBytes("h"), Bytes.toBytes("i"), Bytes.toBytes("j") };
            TableDescriptor htd = TableDescriptorBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setColumnFamily(ColumnFamilyDescriptorBuilder.of(CATALOG_FAMILY)).build();
            admin.createTable(htd, SPLIT_KEYS);
            TestZooKeeper.TEST_UTIL.waitUntilNoRegionsInTransition(60000);
            m.getZooKeeper().close();
            TestZooKeeper.MockLoadBalancer.retainAssignCalled = false;
            final int expectedNumOfListeners = countPermanentListeners(zkw);
            m.abort("Test recovery from zk session expired", new KeeperException.SessionExpiredException());
            Assert.assertTrue(m.isStopped());// Master doesn't recover any more

            // The recovered master should not call retainAssignment, as it is not a
            // clean startup.
            Assert.assertFalse("Retain assignment should not be called", TestZooKeeper.MockLoadBalancer.retainAssignCalled);
            // number of listeners should be same as the value before master aborted
            // wait for new master is initialized
            cluster.waitForActiveAndReadyMaster(120000);
            final HMaster newMaster = cluster.getMasterThread().getMaster();
            Assert.assertEquals(expectedNumOfListeners, countPermanentListeners(newMaster.getZooKeeper()));
        }
    }

    /**
     * Tests whether the logs are split when master recovers from a expired zookeeper session and an
     * RS goes down.
     */
    @Test
    public void testLogSplittingAfterMasterRecoveryDueToZKExpiry() throws Exception {
        MiniHBaseCluster cluster = TestZooKeeper.TEST_UTIL.getHBaseCluster();
        cluster.startRegionServer();
        TableName tableName = TableName.valueOf(name.getMethodName());
        byte[] family = Bytes.toBytes("col");
        try (Admin admin = TestZooKeeper.TEST_UTIL.getAdmin()) {
            byte[][] SPLIT_KEYS = new byte[][]{ Bytes.toBytes("1"), Bytes.toBytes("2"), Bytes.toBytes("3"), Bytes.toBytes("4"), Bytes.toBytes("5") };
            TableDescriptor htd = TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(ColumnFamilyDescriptorBuilder.of(family)).build();
            admin.createTable(htd, SPLIT_KEYS);
        }
        TestZooKeeper.TEST_UTIL.waitUntilNoRegionsInTransition(60000);
        HMaster m = cluster.getMaster();
        try (Table table = TestZooKeeper.TEST_UTIL.getConnection().getTable(tableName)) {
            int numberOfPuts;
            for (numberOfPuts = 0; numberOfPuts < 6; numberOfPuts++) {
                Put p = new Put(Bytes.toBytes(numberOfPuts));
                p.addColumn(Bytes.toBytes("col"), Bytes.toBytes("ql"), Bytes.toBytes(("value" + numberOfPuts)));
                table.put(p);
            }
            m.abort("Test recovery from zk session expired", new KeeperException.SessionExpiredException());
            Assert.assertTrue(m.isStopped());// Master doesn't recover any more

            cluster.killRegionServer(TestZooKeeper.TEST_UTIL.getRSForFirstRegionInTable(tableName).getServerName());
            // Without patch for HBASE-6046 this test case will always timeout
            // with patch the test case should pass.
            int numberOfRows = 0;
            try (ResultScanner scanner = table.getScanner(new Scan())) {
                while ((scanner.next()) != null) {
                    numberOfRows++;
                } 
            }
            Assert.assertEquals("Number of rows should be equal to number of puts.", numberOfPuts, numberOfRows);
        }
    }

    static class MockLoadBalancer extends SimpleLoadBalancer {
        static boolean retainAssignCalled = false;

        @Override
        public Map<ServerName, List<RegionInfo>> retainAssignment(Map<RegionInfo, ServerName> regions, List<ServerName> servers) throws HBaseIOException {
            TestZooKeeper.MockLoadBalancer.retainAssignCalled = true;
            return super.retainAssignment(regions, servers);
        }
    }
}

