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
package org.apache.hadoop.hbase.replication.regionserver;


import java.io.IOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.ReplicationPeerNotFoundException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.replication.ReplicationAdmin;
import org.apache.hadoop.hbase.replication.ReplicationException;
import org.apache.hadoop.hbase.replication.ReplicationPeerConfig;
import org.apache.hadoop.hbase.testclassification.FlakeyTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.zookeeper.ZKConfig;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests RegionReplicaReplicationEndpoint class by setting up region replicas and verifying
 * async wal replication replays the edits to the secondary region in various scenarios.
 */
@Category({ FlakeyTests.class, LargeTests.class })
public class TestRegionReplicaReplicationEndpoint {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionReplicaReplicationEndpoint.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRegionReplicaReplicationEndpoint.class);

    private static final int NB_SERVERS = 2;

    private static final HBaseTestingUtility HTU = new HBaseTestingUtility();

    @Rule
    public TestName name = new TestName();

    @Test
    public void testRegionReplicaReplicationPeerIsCreated() throws IOException, ReplicationException {
        // create a table with region replicas. Check whether the replication peer is created
        // and replication started.
        ReplicationAdmin admin = new ReplicationAdmin(TestRegionReplicaReplicationEndpoint.HTU.getConfiguration());
        String peerId = "region_replica_replication";
        ReplicationPeerConfig peerConfig = null;
        try {
            peerConfig = admin.getPeerConfig(peerId);
        } catch (ReplicationPeerNotFoundException e) {
            TestRegionReplicaReplicationEndpoint.LOG.warn((("Region replica replication peer id=" + peerId) + " not exist"), e);
        }
        if (peerConfig != null) {
            admin.removePeer(peerId);
            peerConfig = null;
        }
        HTableDescriptor htd = TestRegionReplicaReplicationEndpoint.HTU.createTableDescriptor("testReplicationPeerIsCreated_no_region_replicas");
        TestRegionReplicaReplicationEndpoint.HTU.getAdmin().createTable(htd);
        try {
            peerConfig = admin.getPeerConfig(peerId);
            Assert.fail((("Should throw ReplicationException, because replication peer id=" + peerId) + " not exist"));
        } catch (ReplicationPeerNotFoundException e) {
        }
        Assert.assertNull(peerConfig);
        htd = TestRegionReplicaReplicationEndpoint.HTU.createTableDescriptor("testReplicationPeerIsCreated");
        htd.setRegionReplication(2);
        TestRegionReplicaReplicationEndpoint.HTU.getAdmin().createTable(htd);
        // assert peer configuration is correct
        peerConfig = admin.getPeerConfig(peerId);
        Assert.assertNotNull(peerConfig);
        Assert.assertEquals(peerConfig.getClusterKey(), ZKConfig.getZooKeeperClusterKey(TestRegionReplicaReplicationEndpoint.HTU.getConfiguration()));
        Assert.assertEquals(RegionReplicaReplicationEndpoint.class.getName(), peerConfig.getReplicationEndpointImpl());
        admin.close();
    }

    @Test
    public void testRegionReplicaReplicationPeerIsCreatedForModifyTable() throws Exception {
        // modify a table by adding region replicas. Check whether the replication peer is created
        // and replication started.
        ReplicationAdmin admin = new ReplicationAdmin(TestRegionReplicaReplicationEndpoint.HTU.getConfiguration());
        String peerId = "region_replica_replication";
        ReplicationPeerConfig peerConfig = null;
        try {
            peerConfig = admin.getPeerConfig(peerId);
        } catch (ReplicationPeerNotFoundException e) {
            TestRegionReplicaReplicationEndpoint.LOG.warn((("Region replica replication peer id=" + peerId) + " not exist"), e);
        }
        if (peerConfig != null) {
            admin.removePeer(peerId);
            peerConfig = null;
        }
        HTableDescriptor htd = TestRegionReplicaReplicationEndpoint.HTU.createTableDescriptor("testRegionReplicaReplicationPeerIsCreatedForModifyTable");
        TestRegionReplicaReplicationEndpoint.HTU.getAdmin().createTable(htd);
        // assert that replication peer is not created yet
        try {
            peerConfig = admin.getPeerConfig(peerId);
            Assert.fail((("Should throw ReplicationException, because replication peer id=" + peerId) + " not exist"));
        } catch (ReplicationPeerNotFoundException e) {
        }
        Assert.assertNull(peerConfig);
        TestRegionReplicaReplicationEndpoint.HTU.getAdmin().disableTable(htd.getTableName());
        htd.setRegionReplication(2);
        TestRegionReplicaReplicationEndpoint.HTU.getAdmin().modifyTable(htd.getTableName(), htd);
        TestRegionReplicaReplicationEndpoint.HTU.getAdmin().enableTable(htd.getTableName());
        // assert peer configuration is correct
        peerConfig = admin.getPeerConfig(peerId);
        Assert.assertNotNull(peerConfig);
        Assert.assertEquals(peerConfig.getClusterKey(), ZKConfig.getZooKeeperClusterKey(TestRegionReplicaReplicationEndpoint.HTU.getConfiguration()));
        Assert.assertEquals(RegionReplicaReplicationEndpoint.class.getName(), peerConfig.getReplicationEndpointImpl());
        admin.close();
    }

    @Test
    public void testRegionReplicaReplicationWith2Replicas() throws Exception {
        testRegionReplicaReplication(2);
    }

    @Test
    public void testRegionReplicaReplicationWith3Replicas() throws Exception {
        testRegionReplicaReplication(3);
    }

    @Test
    public void testRegionReplicaReplicationWith10Replicas() throws Exception {
        testRegionReplicaReplication(10);
    }

    @Test
    public void testRegionReplicaWithoutMemstoreReplication() throws Exception {
        int regionReplication = 3;
        final TableName tableName = TableName.valueOf(name.getMethodName());
        HTableDescriptor htd = TestRegionReplicaReplicationEndpoint.HTU.createTableDescriptor(tableName);
        htd.setRegionReplication(regionReplication);
        htd.setRegionMemstoreReplication(false);
        TestRegionReplicaReplicationEndpoint.HTU.getAdmin().createTable(htd);
        Connection connection = ConnectionFactory.createConnection(TestRegionReplicaReplicationEndpoint.HTU.getConfiguration());
        Table table = connection.getTable(tableName);
        try {
            // write data to the primary. The replicas should not receive the data
            final int STEP = 100;
            for (int i = 0; i < 3; ++i) {
                final int startRow = i * STEP;
                final int endRow = (i + 1) * STEP;
                TestRegionReplicaReplicationEndpoint.LOG.info(((("Writing data from " + startRow) + " to ") + endRow));
                TestRegionReplicaReplicationEndpoint.HTU.loadNumericRows(table, HBaseTestingUtility.fam1, startRow, endRow);
                verifyReplication(tableName, regionReplication, startRow, endRow, false);
                // Flush the table, now the data should show up in the replicas
                TestRegionReplicaReplicationEndpoint.LOG.info("flushing table");
                TestRegionReplicaReplicationEndpoint.HTU.flush(tableName);
                verifyReplication(tableName, regionReplication, 0, endRow, true);
            }
        } finally {
            table.close();
            connection.close();
        }
    }

    @Test
    public void testRegionReplicaReplicationForFlushAndCompaction() throws Exception {
        // Tests a table with region replication 3. Writes some data, and causes flushes and
        // compactions. Verifies that the data is readable from the replicas. Note that this
        // does not test whether the replicas actually pick up flushed files and apply compaction
        // to their stores
        int regionReplication = 3;
        final TableName tableName = TableName.valueOf(name.getMethodName());
        HTableDescriptor htd = TestRegionReplicaReplicationEndpoint.HTU.createTableDescriptor(tableName);
        htd.setRegionReplication(regionReplication);
        TestRegionReplicaReplicationEndpoint.HTU.getAdmin().createTable(htd);
        Connection connection = ConnectionFactory.createConnection(TestRegionReplicaReplicationEndpoint.HTU.getConfiguration());
        Table table = connection.getTable(tableName);
        try {
            // load the data to the table
            for (int i = 0; i < 6000; i += 1000) {
                TestRegionReplicaReplicationEndpoint.LOG.info(((("Writing data from " + i) + " to ") + (i + 1000)));
                TestRegionReplicaReplicationEndpoint.HTU.loadNumericRows(table, HBaseTestingUtility.fam1, i, (i + 1000));
                TestRegionReplicaReplicationEndpoint.LOG.info("flushing table");
                TestRegionReplicaReplicationEndpoint.HTU.flush(tableName);
                TestRegionReplicaReplicationEndpoint.LOG.info("compacting table");
                TestRegionReplicaReplicationEndpoint.HTU.compact(tableName, false);
            }
            verifyReplication(tableName, regionReplication, 0, 1000);
        } finally {
            table.close();
            connection.close();
        }
    }

    @Test
    public void testRegionReplicaReplicationIgnoresDisabledTables() throws Exception {
        testRegionReplicaReplicationIgnores(false, false);
    }

    @Test
    public void testRegionReplicaReplicationIgnoresDroppedTables() throws Exception {
        testRegionReplicaReplicationIgnores(true, false);
    }

    @Test
    public void testRegionReplicaReplicationIgnoresNonReplicatedTables() throws Exception {
        testRegionReplicaReplicationIgnores(false, true);
    }
}

