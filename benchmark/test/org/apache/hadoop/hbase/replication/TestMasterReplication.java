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
package org.apache.hadoop.hbase.replication;


import java.io.IOException;
import java.util.EnumSet;
import java.util.Optional;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.ClusterMetrics;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.ServerMetrics;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.Waiter;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.wal.WALEdit;
import org.apache.hadoop.hbase.zookeeper.MiniZooKeeperCluster;
import org.apache.hadoop.hbase.zookeeper.ZKUtil;
import org.apache.hadoop.hbase.zookeeper.ZKWatcher;
import org.apache.hadoop.hbase.zookeeper.ZNodePaths;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ ReplicationTests.class, LargeTests.class })
public class TestMasterReplication {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMasterReplication.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestMasterReplication.class);

    private Configuration baseConfiguration;

    private HBaseTestingUtility[] utilities;

    private Configuration[] configurations;

    private MiniZooKeeperCluster miniZK;

    private static final long SLEEP_TIME = 1000;

    private static final int NB_RETRIES = 120;

    private static final TableName tableName = TableName.valueOf("test");

    private static final byte[] famName = Bytes.toBytes("f");

    private static final byte[] famName1 = Bytes.toBytes("f1");

    private static final byte[] row = Bytes.toBytes("row");

    private static final byte[] row1 = Bytes.toBytes("row1");

    private static final byte[] row2 = Bytes.toBytes("row2");

    private static final byte[] row3 = Bytes.toBytes("row3");

    private static final byte[] row4 = Bytes.toBytes("row4");

    private static final byte[] noRepfamName = Bytes.toBytes("norep");

    private static final byte[] count = Bytes.toBytes("count");

    private static final byte[] put = Bytes.toBytes("put");

    private static final byte[] delete = Bytes.toBytes("delete");

    private TableDescriptor table;

    /**
     * It tests the replication scenario involving 0 -> 1 -> 0. It does it by
     * adding and deleting a row to a table in each cluster, checking if it's
     * replicated. It also tests that the puts and deletes are not replicated back
     * to the originating cluster.
     */
    @Test
    public void testCyclicReplication1() throws Exception {
        TestMasterReplication.LOG.info("testSimplePutDelete");
        int numClusters = 2;
        Table[] htables = null;
        try {
            htables = setUpClusterTablesAndPeers(numClusters);
            int[] expectedCounts = new int[]{ 2, 2 };
            // add rows to both clusters,
            // make sure they are both replication
            putAndWait(TestMasterReplication.row, TestMasterReplication.famName, htables[0], htables[1]);
            putAndWait(TestMasterReplication.row1, TestMasterReplication.famName, htables[1], htables[0]);
            validateCounts(htables, TestMasterReplication.put, expectedCounts);
            deleteAndWait(TestMasterReplication.row, htables[0], htables[1]);
            deleteAndWait(TestMasterReplication.row1, htables[1], htables[0]);
            validateCounts(htables, TestMasterReplication.delete, expectedCounts);
        } finally {
            close(htables);
            shutDownMiniClusters();
        }
    }

    /**
     * Tests the replication scenario 0 -> 0. By default
     * {@link BaseReplicationEndpoint#canReplicateToSameCluster()} returns false, so the
     * ReplicationSource should terminate, and no further logs should get enqueued
     */
    @Test
    public void testLoopedReplication() throws Exception {
        TestMasterReplication.LOG.info("testLoopedReplication");
        startMiniClusters(1);
        createTableOnClusters(table);
        addPeer("1", 0, 0);
        Thread.sleep(TestMasterReplication.SLEEP_TIME);
        // wait for source to terminate
        final ServerName rsName = utilities[0].getHBaseCluster().getRegionServer(0).getServerName();
        Waiter.waitFor(baseConfiguration, 10000, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                ClusterMetrics clusterStatus = utilities[0].getAdmin().getClusterMetrics(EnumSet.of(ClusterMetrics.Option.LIVE_SERVERS));
                ServerMetrics serverLoad = clusterStatus.getLiveServerMetrics().get(rsName);
                List<ReplicationLoadSource> replicationLoadSourceList = serverLoad.getReplicationLoadSourceList();
                return replicationLoadSourceList.isEmpty();
            }
        });
        Table[] htables = getHTablesOnClusters(TestMasterReplication.tableName);
        putAndWait(TestMasterReplication.row, TestMasterReplication.famName, htables[0], htables[0]);
        rollWALAndWait(utilities[0], table.getTableName(), TestMasterReplication.row);
        ZKWatcher zkw = getZooKeeperWatcher();
        String queuesZnode = ZNodePaths.joinZNode(zkw.getZNodePaths().baseZNode, ZNodePaths.joinZNode("replication", "rs"));
        java.util.List<String> listChildrenNoWatch = ZKUtil.listChildrenNoWatch(zkw, ZNodePaths.joinZNode(queuesZnode, rsName.toString()));
        Assert.assertEquals(0, listChildrenNoWatch.size());
    }

    /**
     * It tests the replication scenario involving 0 -> 1 -> 0. It does it by bulk loading a set of
     * HFiles to a table in each cluster, checking if it's replicated.
     */
    @Test
    public void testHFileCyclicReplication() throws Exception {
        TestMasterReplication.LOG.info("testHFileCyclicReplication");
        int numClusters = 2;
        Table[] htables = null;
        try {
            htables = setUpClusterTablesAndPeers(numClusters);
            // Load 100 rows for each hfile range in cluster '0' and validate whether its been replicated
            // to cluster '1'.
            byte[][][] hfileRanges = new byte[][][]{ new byte[][]{ Bytes.toBytes("aaaa"), Bytes.toBytes("cccc") }, new byte[][]{ Bytes.toBytes("ddd"), Bytes.toBytes("fff") } };
            int numOfRows = 100;
            int[] expectedCounts = new int[]{ (hfileRanges.length) * numOfRows, (hfileRanges.length) * numOfRows };
            loadAndValidateHFileReplication("testHFileCyclicReplication_01", 0, new int[]{ 1 }, TestMasterReplication.row, TestMasterReplication.famName, htables, hfileRanges, numOfRows, expectedCounts, true);
            // Load 200 rows for each hfile range in cluster '1' and validate whether its been replicated
            // to cluster '0'.
            hfileRanges = new byte[][][]{ new byte[][]{ Bytes.toBytes("gggg"), Bytes.toBytes("iiii") }, new byte[][]{ Bytes.toBytes("jjj"), Bytes.toBytes("lll") } };
            numOfRows = 200;
            int[] newExpectedCounts = new int[]{ ((hfileRanges.length) * numOfRows) + (expectedCounts[0]), ((hfileRanges.length) * numOfRows) + (expectedCounts[1]) };
            loadAndValidateHFileReplication("testHFileCyclicReplication_10", 1, new int[]{ 0 }, TestMasterReplication.row, TestMasterReplication.famName, htables, hfileRanges, numOfRows, newExpectedCounts, true);
        } finally {
            close(htables);
            shutDownMiniClusters();
        }
    }

    /**
     * Tests the cyclic replication scenario of 0 -> 1 -> 2 -> 0 by adding and deleting rows to a
     * table in each clusters and ensuring that the each of these clusters get the appropriate
     * mutations. It also tests the grouping scenario where a cluster needs to replicate the edits
     * originating from itself and also the edits that it received using replication from a different
     * cluster. The scenario is explained in HBASE-9158
     */
    @Test
    public void testCyclicReplication2() throws Exception {
        TestMasterReplication.LOG.info("testCyclicReplication2");
        int numClusters = 3;
        Table[] htables = null;
        try {
            startMiniClusters(numClusters);
            createTableOnClusters(table);
            // Test the replication scenario of 0 -> 1 -> 2 -> 0
            addPeer("1", 0, 1);
            addPeer("1", 1, 2);
            addPeer("1", 2, 0);
            htables = getHTablesOnClusters(TestMasterReplication.tableName);
            // put "row" and wait 'til it got around
            putAndWait(TestMasterReplication.row, TestMasterReplication.famName, htables[0], htables[2]);
            putAndWait(TestMasterReplication.row1, TestMasterReplication.famName, htables[1], htables[0]);
            putAndWait(TestMasterReplication.row2, TestMasterReplication.famName, htables[2], htables[1]);
            deleteAndWait(TestMasterReplication.row, htables[0], htables[2]);
            deleteAndWait(TestMasterReplication.row1, htables[1], htables[0]);
            deleteAndWait(TestMasterReplication.row2, htables[2], htables[1]);
            int[] expectedCounts = new int[]{ 3, 3, 3 };
            validateCounts(htables, TestMasterReplication.put, expectedCounts);
            validateCounts(htables, TestMasterReplication.delete, expectedCounts);
            // Test HBASE-9158
            disablePeer("1", 2);
            // we now have an edit that was replicated into cluster originating from
            // cluster 0
            putAndWait(TestMasterReplication.row3, TestMasterReplication.famName, htables[0], htables[1]);
            // now add a local edit to cluster 1
            htables[1].put(new Put(TestMasterReplication.row4).addColumn(TestMasterReplication.famName, TestMasterReplication.row4, TestMasterReplication.row4));
            // re-enable replication from cluster 2 to cluster 0
            enablePeer("1", 2);
            // without HBASE-9158 the edit for row4 would have been marked with
            // cluster 0's id
            // and hence not replicated to cluster 0
            wait(TestMasterReplication.row4, htables[0], false);
        } finally {
            close(htables);
            shutDownMiniClusters();
        }
    }

    /**
     * It tests the multi slave hfile replication scenario involving 0 -> 1, 2. It does it by bulk
     * loading a set of HFiles to a table in master cluster, checking if it's replicated in its peers.
     */
    @Test
    public void testHFileMultiSlaveReplication() throws Exception {
        TestMasterReplication.LOG.info("testHFileMultiSlaveReplication");
        int numClusters = 3;
        Table[] htables = null;
        try {
            startMiniClusters(numClusters);
            createTableOnClusters(table);
            // Add a slave, 0 -> 1
            addPeer("1", 0, 1);
            htables = getHTablesOnClusters(TestMasterReplication.tableName);
            // Load 100 rows for each hfile range in cluster '0' and validate whether its been replicated
            // to cluster '1'.
            byte[][][] hfileRanges = new byte[][][]{ new byte[][]{ Bytes.toBytes("mmmm"), Bytes.toBytes("oooo") }, new byte[][]{ Bytes.toBytes("ppp"), Bytes.toBytes("rrr") } };
            int numOfRows = 100;
            int[] expectedCounts = new int[]{ (hfileRanges.length) * numOfRows, (hfileRanges.length) * numOfRows };
            loadAndValidateHFileReplication("testHFileCyclicReplication_0", 0, new int[]{ 1 }, TestMasterReplication.row, TestMasterReplication.famName, htables, hfileRanges, numOfRows, expectedCounts, true);
            // Validate data is not replicated to cluster '2'.
            Assert.assertEquals(0, utilities[2].countRows(htables[2]));
            rollWALAndWait(utilities[0], htables[0].getName(), TestMasterReplication.row);
            // Add one more slave, 0 -> 2
            addPeer("2", 0, 2);
            // Load 200 rows for each hfile range in cluster '0' and validate whether its been replicated
            // to cluster '1' and '2'. Previous data should be replicated to cluster '2'.
            hfileRanges = new byte[][][]{ new byte[][]{ Bytes.toBytes("ssss"), Bytes.toBytes("uuuu") }, new byte[][]{ Bytes.toBytes("vvv"), Bytes.toBytes("xxx") } };
            numOfRows = 200;
            int[] newExpectedCounts = new int[]{ ((hfileRanges.length) * numOfRows) + (expectedCounts[0]), ((hfileRanges.length) * numOfRows) + (expectedCounts[1]), (hfileRanges.length) * numOfRows };
            loadAndValidateHFileReplication("testHFileCyclicReplication_1", 0, new int[]{ 1, 2 }, TestMasterReplication.row, TestMasterReplication.famName, htables, hfileRanges, numOfRows, newExpectedCounts, true);
        } finally {
            close(htables);
            shutDownMiniClusters();
        }
    }

    /**
     * It tests the bulk loaded hfile replication scenario to only explicitly specified table column
     * families. It does it by bulk loading a set of HFiles belonging to both the CFs of table and set
     * only one CF data to replicate.
     */
    @Test
    public void testHFileReplicationForConfiguredTableCfs() throws Exception {
        TestMasterReplication.LOG.info("testHFileReplicationForConfiguredTableCfs");
        int numClusters = 2;
        Table[] htables = null;
        try {
            startMiniClusters(numClusters);
            createTableOnClusters(table);
            htables = getHTablesOnClusters(TestMasterReplication.tableName);
            // Test the replication scenarios only 'f' is configured for table data replication not 'f1'
            addPeer("1", 0, 1, (((TestMasterReplication.tableName.getNameAsString()) + ":") + (Bytes.toString(TestMasterReplication.famName))));
            // Load 100 rows for each hfile range in cluster '0' for table CF 'f'
            byte[][][] hfileRanges = new byte[][][]{ new byte[][]{ Bytes.toBytes("aaaa"), Bytes.toBytes("cccc") }, new byte[][]{ Bytes.toBytes("ddd"), Bytes.toBytes("fff") } };
            int numOfRows = 100;
            int[] expectedCounts = new int[]{ (hfileRanges.length) * numOfRows, (hfileRanges.length) * numOfRows };
            loadAndValidateHFileReplication("load_f", 0, new int[]{ 1 }, TestMasterReplication.row, TestMasterReplication.famName, htables, hfileRanges, numOfRows, expectedCounts, true);
            // Load 100 rows for each hfile range in cluster '0' for table CF 'f1'
            hfileRanges = new byte[][][]{ new byte[][]{ Bytes.toBytes("gggg"), Bytes.toBytes("iiii") }, new byte[][]{ Bytes.toBytes("jjj"), Bytes.toBytes("lll") } };
            numOfRows = 100;
            int[] newExpectedCounts = new int[]{ ((hfileRanges.length) * numOfRows) + (expectedCounts[0]), expectedCounts[1] };
            loadAndValidateHFileReplication("load_f1", 0, new int[]{ 1 }, TestMasterReplication.row, TestMasterReplication.famName1, htables, hfileRanges, numOfRows, newExpectedCounts, false);
            // Validate data replication for CF 'f1'
            // Source cluster table should contain data for the families
            wait(0, htables[0], (((hfileRanges.length) * numOfRows) + (expectedCounts[0])));
            // Sleep for enough time so that the data is still not replicated for the CF which is not
            // configured for replication
            Thread.sleep((((TestMasterReplication.NB_RETRIES) / 2) * (TestMasterReplication.SLEEP_TIME)));
            // Peer cluster should have only configured CF data
            wait(1, htables[1], expectedCounts[1]);
        } finally {
            close(htables);
            shutDownMiniClusters();
        }
    }

    /**
     * Tests cyclic replication scenario of 0 -> 1 -> 2 -> 1.
     */
    @Test
    public void testCyclicReplication3() throws Exception {
        TestMasterReplication.LOG.info("testCyclicReplication2");
        int numClusters = 3;
        Table[] htables = null;
        try {
            startMiniClusters(numClusters);
            createTableOnClusters(table);
            // Test the replication scenario of 0 -> 1 -> 2 -> 1
            addPeer("1", 0, 1);
            addPeer("1", 1, 2);
            addPeer("1", 2, 1);
            htables = getHTablesOnClusters(TestMasterReplication.tableName);
            // put "row" and wait 'til it got around
            putAndWait(TestMasterReplication.row, TestMasterReplication.famName, htables[0], htables[2]);
            putAndWait(TestMasterReplication.row1, TestMasterReplication.famName, htables[1], htables[2]);
            putAndWait(TestMasterReplication.row2, TestMasterReplication.famName, htables[2], htables[1]);
            deleteAndWait(TestMasterReplication.row, htables[0], htables[2]);
            deleteAndWait(TestMasterReplication.row1, htables[1], htables[2]);
            deleteAndWait(TestMasterReplication.row2, htables[2], htables[1]);
            int[] expectedCounts = new int[]{ 1, 3, 3 };
            validateCounts(htables, TestMasterReplication.put, expectedCounts);
            validateCounts(htables, TestMasterReplication.delete, expectedCounts);
        } finally {
            close(htables);
            shutDownMiniClusters();
        }
    }

    /**
     * Use a coprocessor to count puts and deletes. as KVs would be replicated back with the same
     * timestamp there is otherwise no way to count them.
     */
    public static class CoprocessorCounter implements RegionCoprocessor , RegionObserver {
        private int nCount = 0;

        private int nDelete = 0;

        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void prePut(final ObserverContext<RegionCoprocessorEnvironment> e, final Put put, final WALEdit edit, final Durability durability) throws IOException {
            (nCount)++;
        }

        @Override
        public void postDelete(final ObserverContext<RegionCoprocessorEnvironment> c, final Delete delete, final WALEdit edit, final Durability durability) throws IOException {
            (nDelete)++;
        }

        @Override
        public void preGetOp(final ObserverContext<RegionCoprocessorEnvironment> c, final Get get, final java.util.List<Cell> result) throws IOException {
            if ((get.getAttribute("count")) != null) {
                result.clear();
                // order is important!
                result.add(new org.apache.hadoop.hbase.KeyValue(TestMasterReplication.count, TestMasterReplication.count, TestMasterReplication.delete, Bytes.toBytes(nDelete)));
                result.add(new org.apache.hadoop.hbase.KeyValue(TestMasterReplication.count, TestMasterReplication.count, TestMasterReplication.put, Bytes.toBytes(nCount)));
                c.bypass();
            }
        }
    }
}

