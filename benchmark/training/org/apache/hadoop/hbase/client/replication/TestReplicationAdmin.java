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
package org.apache.hadoop.hbase.client.replication;


import SyncReplicationState.ACTIVE;
import SyncReplicationState.DOWNGRADE_ACTIVE;
import SyncReplicationState.NONE;
import SyncReplicationState.STANDBY;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.ReplicationPeerNotFoundException;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.replication.DummyReplicationEndpoint;
import org.apache.hadoop.hbase.replication.ReplicationException;
import org.apache.hadoop.hbase.replication.ReplicationPeerConfig;
import org.apache.hadoop.hbase.replication.ReplicationPeerConfigBuilder;
import org.apache.hadoop.hbase.replication.ReplicationPeerDescription;
import org.apache.hadoop.hbase.replication.ReplicationQueueStorage;
import org.apache.hadoop.hbase.replication.ReplicationStorageFactory;
import org.apache.hadoop.hbase.replication.ReplicationUtils;
import org.apache.hadoop.hbase.replication.TestReplicationEndpoint;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Unit testing of ReplicationAdmin
 */
@Category({ MediumTests.class, ClientTests.class })
public class TestReplicationAdmin {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestReplicationAdmin.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestReplicationAdmin.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private final String ID_ONE = "1";

    private final String KEY_ONE = "127.0.0.1:2181:/hbase";

    private final String ID_SECOND = "2";

    private final String KEY_SECOND = "127.0.0.1:2181:/hbase2";

    private static ReplicationAdmin admin;

    private static Admin hbaseAdmin;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testConcurrentPeerOperations() throws Exception {
        int threadNum = 5;
        AtomicLong successCount = new AtomicLong(0);
        // Test concurrent add peer operation
        Thread[] addPeers = new Thread[threadNum];
        for (int i = 0; i < threadNum; i++) {
            addPeers[i] = new Thread(() -> {
                try {
                    TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_ONE, ReplicationPeerConfig.newBuilder().setClusterKey(KEY_ONE).build());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    TestReplicationAdmin.LOG.debug("Got exception when add replication peer", e);
                }
            });
            addPeers[i].start();
        }
        for (Thread addPeer : addPeers) {
            addPeer.join();
        }
        Assert.assertEquals(1, successCount.get());
        // Test concurrent remove peer operation
        successCount.set(0);
        Thread[] removePeers = new Thread[threadNum];
        for (int i = 0; i < threadNum; i++) {
            removePeers[i] = new Thread(() -> {
                try {
                    TestReplicationAdmin.hbaseAdmin.removeReplicationPeer(ID_ONE);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    TestReplicationAdmin.LOG.debug("Got exception when remove replication peer", e);
                }
            });
            removePeers[i].start();
        }
        for (Thread removePeer : removePeers) {
            removePeer.join();
        }
        Assert.assertEquals(1, successCount.get());
        // Test concurrent add peer operation again
        successCount.set(0);
        addPeers = new Thread[threadNum];
        for (int i = 0; i < threadNum; i++) {
            addPeers[i] = new Thread(() -> {
                try {
                    TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_ONE, ReplicationPeerConfig.newBuilder().setClusterKey(KEY_ONE).build());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    TestReplicationAdmin.LOG.debug("Got exception when add replication peer", e);
                }
            });
            addPeers[i].start();
        }
        for (Thread addPeer : addPeers) {
            addPeer.join();
        }
        Assert.assertEquals(1, successCount.get());
    }

    @Test
    public void testAddInvalidPeer() {
        ReplicationPeerConfigBuilder builder = ReplicationPeerConfig.newBuilder();
        builder.setClusterKey(KEY_ONE);
        try {
            String invalidPeerId = "1-2";
            TestReplicationAdmin.hbaseAdmin.addReplicationPeer(invalidPeerId, builder.build());
            Assert.fail((("Should fail as the peer id: " + invalidPeerId) + " is invalid"));
        } catch (Exception e) {
            // OK
        }
        try {
            String invalidClusterKey = "2181:/hbase";
            builder.setClusterKey(invalidClusterKey);
            TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_ONE, builder.build());
            Assert.fail((("Should fail as the peer cluster key: " + invalidClusterKey) + " is invalid"));
        } catch (Exception e) {
            // OK
        }
    }

    /**
     * Simple testing of adding and removing peers, basically shows that
     * all interactions with ZK work
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAddRemovePeer() throws Exception {
        ReplicationPeerConfigBuilder rpc1 = ReplicationPeerConfig.newBuilder();
        rpc1.setClusterKey(KEY_ONE);
        ReplicationPeerConfigBuilder rpc2 = ReplicationPeerConfig.newBuilder();
        rpc2.setClusterKey(KEY_SECOND);
        // Add a valid peer
        TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_ONE, rpc1.build());
        // try adding the same (fails)
        try {
            TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_ONE, rpc1.build());
        } catch (Exception e) {
            // OK!
        }
        Assert.assertEquals(1, TestReplicationAdmin.hbaseAdmin.listReplicationPeers().size());
        // Try to remove an inexisting peer
        try {
            TestReplicationAdmin.hbaseAdmin.removeReplicationPeer(ID_SECOND);
            Assert.fail();
        } catch (Exception e) {
            // OK!
        }
        Assert.assertEquals(1, TestReplicationAdmin.hbaseAdmin.listReplicationPeers().size());
        // Add a second since multi-slave is supported
        try {
            TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_SECOND, rpc2.build());
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertEquals(2, TestReplicationAdmin.hbaseAdmin.listReplicationPeers().size());
        // Remove the first peer we added
        TestReplicationAdmin.hbaseAdmin.removeReplicationPeer(ID_ONE);
        Assert.assertEquals(1, TestReplicationAdmin.hbaseAdmin.listReplicationPeers().size());
        TestReplicationAdmin.hbaseAdmin.removeReplicationPeer(ID_SECOND);
        Assert.assertEquals(0, TestReplicationAdmin.hbaseAdmin.listReplicationPeers().size());
    }

    @Test
    public void testRemovePeerWithNonDAState() throws Exception {
        TableName tableName = TableName.valueOf(name.getMethodName());
        TestReplicationAdmin.TEST_UTIL.createTable(tableName, Bytes.toBytes("family"));
        ReplicationPeerConfigBuilder builder = ReplicationPeerConfig.newBuilder();
        Path rootDir = TestReplicationAdmin.TEST_UTIL.getDataTestDirOnTestFS("remoteWAL");
        TestReplicationAdmin.TEST_UTIL.getTestFileSystem().mkdirs(new Path(rootDir, ID_ONE));
        builder.setClusterKey(KEY_ONE);
        builder.setRemoteWALDir(rootDir.makeQualified(TestReplicationAdmin.TEST_UTIL.getTestFileSystem().getUri(), TestReplicationAdmin.TEST_UTIL.getTestFileSystem().getWorkingDirectory()).toString());
        builder.setReplicateAllUserTables(false);
        Map<TableName, List<String>> tableCfs = new HashMap<>();
        tableCfs.put(tableName, new ArrayList());
        builder.setTableCFsMap(tableCfs);
        TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_ONE, builder.build());
        Assert.assertEquals(DOWNGRADE_ACTIVE, TestReplicationAdmin.hbaseAdmin.getReplicationPeerSyncReplicationState(ID_ONE));
        // Transit sync replication state to ACTIVE.
        TestReplicationAdmin.hbaseAdmin.transitReplicationPeerSyncReplicationState(ID_ONE, ACTIVE);
        Assert.assertEquals(ACTIVE, TestReplicationAdmin.hbaseAdmin.getReplicationPeerSyncReplicationState(ID_ONE));
        try {
            TestReplicationAdmin.hbaseAdmin.removeReplicationPeer(ID_ONE);
            Assert.fail("Can't remove a synchronous replication peer with state=ACTIVE");
        } catch (IOException e) {
            // OK
        }
        // Transit sync replication state to DA
        TestReplicationAdmin.hbaseAdmin.transitReplicationPeerSyncReplicationState(ID_ONE, DOWNGRADE_ACTIVE);
        Assert.assertEquals(DOWNGRADE_ACTIVE, TestReplicationAdmin.hbaseAdmin.getReplicationPeerSyncReplicationState(ID_ONE));
        // Transit sync replication state to STANDBY
        TestReplicationAdmin.hbaseAdmin.transitReplicationPeerSyncReplicationState(ID_ONE, STANDBY);
        Assert.assertEquals(STANDBY, TestReplicationAdmin.hbaseAdmin.getReplicationPeerSyncReplicationState(ID_ONE));
        try {
            TestReplicationAdmin.hbaseAdmin.removeReplicationPeer(ID_ONE);
            Assert.fail("Can't remove a synchronous replication peer with state=STANDBY");
        } catch (IOException e) {
            // OK
        }
        // Transit sync replication state to DA
        TestReplicationAdmin.hbaseAdmin.transitReplicationPeerSyncReplicationState(ID_ONE, DOWNGRADE_ACTIVE);
        Assert.assertEquals(DOWNGRADE_ACTIVE, TestReplicationAdmin.hbaseAdmin.getReplicationPeerSyncReplicationState(ID_ONE));
        TestReplicationAdmin.hbaseAdmin.removeReplicationPeer(ID_ONE);
        Assert.assertEquals(0, TestReplicationAdmin.hbaseAdmin.listReplicationPeers().size());
    }

    @Test
    public void testAddPeerWithState() throws Exception {
        ReplicationPeerConfig rpc1 = new ReplicationPeerConfig();
        rpc1.setClusterKey(KEY_ONE);
        TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_ONE, rpc1, true);
        Assert.assertTrue(TestReplicationAdmin.hbaseAdmin.listReplicationPeers(Pattern.compile(ID_ONE)).get(0).isEnabled());
        TestReplicationAdmin.hbaseAdmin.removeReplicationPeer(ID_ONE);
        ReplicationPeerConfig rpc2 = new ReplicationPeerConfig();
        rpc2.setClusterKey(KEY_SECOND);
        TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_SECOND, rpc2, false);
        Assert.assertFalse(TestReplicationAdmin.hbaseAdmin.listReplicationPeers(Pattern.compile(ID_SECOND)).get(0).isEnabled());
        TestReplicationAdmin.hbaseAdmin.removeReplicationPeer(ID_SECOND);
    }

    /**
     * Tests that the peer configuration used by ReplicationAdmin contains all
     * the peer's properties.
     */
    @Test
    public void testPeerConfig() throws Exception {
        ReplicationPeerConfig config = new ReplicationPeerConfig();
        config.setClusterKey(KEY_ONE);
        config.getConfiguration().put("key1", "value1");
        config.getConfiguration().put("key2", "value2");
        TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_ONE, config);
        List<ReplicationPeerDescription> peers = TestReplicationAdmin.hbaseAdmin.listReplicationPeers();
        Assert.assertEquals(1, peers.size());
        ReplicationPeerDescription peerOne = peers.get(0);
        Assert.assertNotNull(peerOne);
        Assert.assertEquals("value1", peerOne.getPeerConfig().getConfiguration().get("key1"));
        Assert.assertEquals("value2", peerOne.getPeerConfig().getConfiguration().get("key2"));
        TestReplicationAdmin.hbaseAdmin.removeReplicationPeer(ID_ONE);
    }

    @Test
    public void testAddPeerWithUnDeletedQueues() throws Exception {
        ReplicationPeerConfig rpc1 = new ReplicationPeerConfig();
        rpc1.setClusterKey(KEY_ONE);
        ReplicationPeerConfig rpc2 = new ReplicationPeerConfig();
        rpc2.setClusterKey(KEY_SECOND);
        Configuration conf = TestReplicationAdmin.TEST_UTIL.getConfiguration();
        ReplicationQueueStorage queueStorage = ReplicationStorageFactory.getReplicationQueueStorage(getZooKeeperWatcher(), conf);
        ServerName serverName = ServerName.valueOf("server1", 8000, 1234);
        // add queue for ID_ONE
        queueStorage.addWAL(serverName, ID_ONE, "file1");
        try {
            TestReplicationAdmin.admin.addPeer(ID_ONE, rpc1, null);
            Assert.fail();
        } catch (Exception e) {
            // OK!
        }
        queueStorage.removeQueue(serverName, ID_ONE);
        Assert.assertEquals(0, queueStorage.getAllQueues(serverName).size());
        // add recovered queue for ID_ONE
        queueStorage.addWAL(serverName, ((ID_ONE) + "-server2"), "file1");
        try {
            TestReplicationAdmin.admin.addPeer(ID_ONE, rpc2, null);
            Assert.fail();
        } catch (Exception e) {
            // OK!
        }
    }

    /**
     * basic checks that when we add a peer that it is enabled, and that we can disable
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEnableDisable() throws Exception {
        ReplicationPeerConfig rpc1 = new ReplicationPeerConfig();
        rpc1.setClusterKey(KEY_ONE);
        TestReplicationAdmin.admin.addPeer(ID_ONE, rpc1, null);
        Assert.assertEquals(1, TestReplicationAdmin.admin.getPeersCount());
        Assert.assertTrue(TestReplicationAdmin.admin.getPeerState(ID_ONE));
        TestReplicationAdmin.admin.disablePeer(ID_ONE);
        Assert.assertFalse(TestReplicationAdmin.admin.getPeerState(ID_ONE));
        try {
            TestReplicationAdmin.admin.getPeerState(ID_SECOND);
        } catch (ReplicationPeerNotFoundException e) {
            // OK!
        }
        TestReplicationAdmin.admin.removePeer(ID_ONE);
    }

    @Test
    public void testAppendPeerTableCFs() throws Exception {
        ReplicationPeerConfig rpc = new ReplicationPeerConfig();
        rpc.setClusterKey(KEY_ONE);
        final TableName tableName1 = TableName.valueOf(((name.getMethodName()) + "t1"));
        final TableName tableName2 = TableName.valueOf(((name.getMethodName()) + "t2"));
        final TableName tableName3 = TableName.valueOf(((name.getMethodName()) + "t3"));
        final TableName tableName4 = TableName.valueOf(((name.getMethodName()) + "t4"));
        final TableName tableName5 = TableName.valueOf(((name.getMethodName()) + "t5"));
        final TableName tableName6 = TableName.valueOf(((name.getMethodName()) + "t6"));
        // Add a valid peer
        TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_ONE, rpc);
        // Update peer config, not replicate all user tables
        rpc = TestReplicationAdmin.hbaseAdmin.getReplicationPeerConfig(ID_ONE);
        rpc.setReplicateAllUserTables(false);
        TestReplicationAdmin.hbaseAdmin.updateReplicationPeerConfig(ID_ONE, rpc);
        Map<TableName, List<String>> tableCFs = new HashMap<>();
        tableCFs.put(tableName1, null);
        TestReplicationAdmin.admin.appendPeerTableCFs(ID_ONE, tableCFs);
        Map<TableName, List<String>> result = ReplicationPeerConfigUtil.parseTableCFsFromConfig(TestReplicationAdmin.admin.getPeerTableCFs(ID_ONE));
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(true, result.containsKey(tableName1));
        Assert.assertNull(result.get(tableName1));
        // append table t2 to replication
        tableCFs.clear();
        tableCFs.put(tableName2, null);
        TestReplicationAdmin.admin.appendPeerTableCFs(ID_ONE, tableCFs);
        result = ReplicationPeerConfigUtil.parseTableCFsFromConfig(TestReplicationAdmin.admin.getPeerTableCFs(ID_ONE));
        Assert.assertEquals(2, result.size());
        Assert.assertTrue("Should contain t1", result.containsKey(tableName1));
        Assert.assertTrue("Should contain t2", result.containsKey(tableName2));
        Assert.assertNull(result.get(tableName1));
        Assert.assertNull(result.get(tableName2));
        // append table column family: f1 of t3 to replication
        tableCFs.clear();
        tableCFs.put(tableName3, new ArrayList());
        tableCFs.get(tableName3).add("f1");
        TestReplicationAdmin.admin.appendPeerTableCFs(ID_ONE, tableCFs);
        result = ReplicationPeerConfigUtil.parseTableCFsFromConfig(TestReplicationAdmin.admin.getPeerTableCFs(ID_ONE));
        Assert.assertEquals(3, result.size());
        Assert.assertTrue("Should contain t1", result.containsKey(tableName1));
        Assert.assertTrue("Should contain t2", result.containsKey(tableName2));
        Assert.assertTrue("Should contain t3", result.containsKey(tableName3));
        Assert.assertNull(result.get(tableName1));
        Assert.assertNull(result.get(tableName2));
        Assert.assertEquals(1, result.get(tableName3).size());
        Assert.assertEquals("f1", result.get(tableName3).get(0));
        tableCFs.clear();
        tableCFs.put(tableName4, new ArrayList());
        tableCFs.get(tableName4).add("f1");
        tableCFs.get(tableName4).add("f2");
        TestReplicationAdmin.admin.appendPeerTableCFs(ID_ONE, tableCFs);
        result = ReplicationPeerConfigUtil.parseTableCFsFromConfig(TestReplicationAdmin.admin.getPeerTableCFs(ID_ONE));
        Assert.assertEquals(4, result.size());
        Assert.assertTrue("Should contain t1", result.containsKey(tableName1));
        Assert.assertTrue("Should contain t2", result.containsKey(tableName2));
        Assert.assertTrue("Should contain t3", result.containsKey(tableName3));
        Assert.assertTrue("Should contain t4", result.containsKey(tableName4));
        Assert.assertNull(result.get(tableName1));
        Assert.assertNull(result.get(tableName2));
        Assert.assertEquals(1, result.get(tableName3).size());
        Assert.assertEquals("f1", result.get(tableName3).get(0));
        Assert.assertEquals(2, result.get(tableName4).size());
        Assert.assertEquals("f1", result.get(tableName4).get(0));
        Assert.assertEquals("f2", result.get(tableName4).get(1));
        // append "table5" => [], then append "table5" => ["f1"]
        tableCFs.clear();
        tableCFs.put(tableName5, new ArrayList());
        TestReplicationAdmin.admin.appendPeerTableCFs(ID_ONE, tableCFs);
        tableCFs.clear();
        tableCFs.put(tableName5, new ArrayList());
        tableCFs.get(tableName5).add("f1");
        TestReplicationAdmin.admin.appendPeerTableCFs(ID_ONE, tableCFs);
        result = ReplicationPeerConfigUtil.parseTableCFsFromConfig(TestReplicationAdmin.admin.getPeerTableCFs(ID_ONE));
        Assert.assertEquals(5, result.size());
        Assert.assertTrue("Should contain t5", result.containsKey(tableName5));
        // null means replication all cfs of tab5
        Assert.assertNull(result.get(tableName5));
        // append "table6" => ["f1"], then append "table6" => []
        tableCFs.clear();
        tableCFs.put(tableName6, new ArrayList());
        tableCFs.get(tableName6).add("f1");
        TestReplicationAdmin.admin.appendPeerTableCFs(ID_ONE, tableCFs);
        tableCFs.clear();
        tableCFs.put(tableName6, new ArrayList());
        TestReplicationAdmin.admin.appendPeerTableCFs(ID_ONE, tableCFs);
        result = ReplicationPeerConfigUtil.parseTableCFsFromConfig(TestReplicationAdmin.admin.getPeerTableCFs(ID_ONE));
        Assert.assertEquals(6, result.size());
        Assert.assertTrue("Should contain t6", result.containsKey(tableName6));
        // null means replication all cfs of tab6
        Assert.assertNull(result.get(tableName6));
        TestReplicationAdmin.admin.removePeer(ID_ONE);
    }

    @Test
    public void testRemovePeerTableCFs() throws Exception {
        ReplicationPeerConfig rpc = new ReplicationPeerConfig();
        rpc.setClusterKey(KEY_ONE);
        final TableName tableName1 = TableName.valueOf(((name.getMethodName()) + "t1"));
        final TableName tableName2 = TableName.valueOf(((name.getMethodName()) + "t2"));
        final TableName tableName3 = TableName.valueOf(((name.getMethodName()) + "t3"));
        final TableName tableName4 = TableName.valueOf(((name.getMethodName()) + "t4"));
        // Add a valid peer
        TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_ONE, rpc);
        // Update peer config, not replicate all user tables
        rpc = TestReplicationAdmin.hbaseAdmin.getReplicationPeerConfig(ID_ONE);
        rpc.setReplicateAllUserTables(false);
        TestReplicationAdmin.hbaseAdmin.updateReplicationPeerConfig(ID_ONE, rpc);
        Map<TableName, List<String>> tableCFs = new HashMap<>();
        try {
            tableCFs.put(tableName3, null);
            TestReplicationAdmin.admin.removePeerTableCFs(ID_ONE, tableCFs);
            Assert.assertTrue(false);
        } catch (ReplicationException e) {
        }
        Assert.assertNull(TestReplicationAdmin.admin.getPeerTableCFs(ID_ONE));
        tableCFs.clear();
        tableCFs.put(tableName1, null);
        tableCFs.put(tableName2, new ArrayList());
        tableCFs.get(tableName2).add("cf1");
        TestReplicationAdmin.admin.setPeerTableCFs(ID_ONE, tableCFs);
        try {
            tableCFs.clear();
            tableCFs.put(tableName3, null);
            TestReplicationAdmin.admin.removePeerTableCFs(ID_ONE, tableCFs);
            Assert.assertTrue(false);
        } catch (ReplicationException e) {
        }
        Map<TableName, List<String>> result = ReplicationPeerConfigUtil.parseTableCFsFromConfig(TestReplicationAdmin.admin.getPeerTableCFs(ID_ONE));
        Assert.assertEquals(2, result.size());
        Assert.assertTrue("Should contain t1", result.containsKey(tableName1));
        Assert.assertTrue("Should contain t2", result.containsKey(tableName2));
        Assert.assertNull(result.get(tableName1));
        Assert.assertEquals(1, result.get(tableName2).size());
        Assert.assertEquals("cf1", result.get(tableName2).get(0));
        try {
            tableCFs.clear();
            tableCFs.put(tableName1, new ArrayList());
            tableCFs.get(tableName1).add("f1");
            TestReplicationAdmin.admin.removePeerTableCFs(ID_ONE, tableCFs);
            Assert.assertTrue(false);
        } catch (ReplicationException e) {
        }
        tableCFs.clear();
        tableCFs.put(tableName1, null);
        TestReplicationAdmin.admin.removePeerTableCFs(ID_ONE, tableCFs);
        result = ReplicationPeerConfigUtil.parseTableCFsFromConfig(TestReplicationAdmin.admin.getPeerTableCFs(ID_ONE));
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(1, result.get(tableName2).size());
        Assert.assertEquals("cf1", result.get(tableName2).get(0));
        try {
            tableCFs.clear();
            tableCFs.put(tableName2, null);
            TestReplicationAdmin.admin.removePeerTableCFs(ID_ONE, tableCFs);
            Assert.fail();
        } catch (ReplicationException e) {
        }
        tableCFs.clear();
        tableCFs.put(tableName2, new ArrayList());
        tableCFs.get(tableName2).add("cf1");
        TestReplicationAdmin.admin.removePeerTableCFs(ID_ONE, tableCFs);
        Assert.assertNull(TestReplicationAdmin.admin.getPeerTableCFs(ID_ONE));
        tableCFs.clear();
        tableCFs.put(tableName4, new ArrayList());
        TestReplicationAdmin.admin.setPeerTableCFs(ID_ONE, tableCFs);
        TestReplicationAdmin.admin.removePeerTableCFs(ID_ONE, tableCFs);
        Assert.assertNull(TestReplicationAdmin.admin.getPeerTableCFs(ID_ONE));
        TestReplicationAdmin.admin.removePeer(ID_ONE);
    }

    @Test
    public void testSetPeerNamespaces() throws Exception {
        String ns1 = "ns1";
        String ns2 = "ns2";
        ReplicationPeerConfig rpc = new ReplicationPeerConfig();
        rpc.setClusterKey(KEY_ONE);
        TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_ONE, rpc);
        rpc = TestReplicationAdmin.hbaseAdmin.getReplicationPeerConfig(ID_ONE);
        rpc.setReplicateAllUserTables(false);
        TestReplicationAdmin.hbaseAdmin.updateReplicationPeerConfig(ID_ONE, rpc);
        rpc = TestReplicationAdmin.hbaseAdmin.getReplicationPeerConfig(ID_ONE);
        Set<String> namespaces = new HashSet<>();
        namespaces.add(ns1);
        namespaces.add(ns2);
        rpc.setNamespaces(namespaces);
        TestReplicationAdmin.hbaseAdmin.updateReplicationPeerConfig(ID_ONE, rpc);
        namespaces = TestReplicationAdmin.hbaseAdmin.getReplicationPeerConfig(ID_ONE).getNamespaces();
        Assert.assertEquals(2, namespaces.size());
        Assert.assertTrue(namespaces.contains(ns1));
        Assert.assertTrue(namespaces.contains(ns2));
        rpc = TestReplicationAdmin.hbaseAdmin.getReplicationPeerConfig(ID_ONE);
        namespaces = new HashSet<>();
        namespaces.add(ns1);
        rpc.setNamespaces(namespaces);
        TestReplicationAdmin.hbaseAdmin.updateReplicationPeerConfig(ID_ONE, rpc);
        namespaces = TestReplicationAdmin.hbaseAdmin.getReplicationPeerConfig(ID_ONE).getNamespaces();
        Assert.assertEquals(1, namespaces.size());
        Assert.assertTrue(namespaces.contains(ns1));
        TestReplicationAdmin.hbaseAdmin.removeReplicationPeer(ID_ONE);
    }

    @Test
    public void testSetReplicateAllUserTables() throws Exception {
        ReplicationPeerConfig rpc = new ReplicationPeerConfig();
        rpc.setClusterKey(KEY_ONE);
        TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_ONE, rpc);
        rpc = TestReplicationAdmin.hbaseAdmin.getReplicationPeerConfig(ID_ONE);
        Assert.assertTrue(rpc.replicateAllUserTables());
        rpc.setReplicateAllUserTables(false);
        TestReplicationAdmin.hbaseAdmin.updateReplicationPeerConfig(ID_ONE, rpc);
        rpc = TestReplicationAdmin.hbaseAdmin.getReplicationPeerConfig(ID_ONE);
        Assert.assertFalse(rpc.replicateAllUserTables());
        rpc.setReplicateAllUserTables(true);
        TestReplicationAdmin.hbaseAdmin.updateReplicationPeerConfig(ID_ONE, rpc);
        rpc = TestReplicationAdmin.hbaseAdmin.getReplicationPeerConfig(ID_ONE);
        Assert.assertTrue(rpc.replicateAllUserTables());
        TestReplicationAdmin.hbaseAdmin.removeReplicationPeer(ID_ONE);
    }

    @Test
    public void testPeerExcludeNamespaces() throws Exception {
        String ns1 = "ns1";
        String ns2 = "ns2";
        ReplicationPeerConfig rpc = new ReplicationPeerConfig();
        rpc.setClusterKey(KEY_ONE);
        TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_ONE, rpc);
        rpc = TestReplicationAdmin.hbaseAdmin.getReplicationPeerConfig(ID_ONE);
        Assert.assertTrue(rpc.replicateAllUserTables());
        Set<String> namespaces = new HashSet<String>();
        namespaces.add(ns1);
        namespaces.add(ns2);
        rpc.setExcludeNamespaces(namespaces);
        TestReplicationAdmin.hbaseAdmin.updateReplicationPeerConfig(ID_ONE, rpc);
        namespaces = TestReplicationAdmin.hbaseAdmin.getReplicationPeerConfig(ID_ONE).getExcludeNamespaces();
        Assert.assertEquals(2, namespaces.size());
        Assert.assertTrue(namespaces.contains(ns1));
        Assert.assertTrue(namespaces.contains(ns2));
        rpc = TestReplicationAdmin.hbaseAdmin.getReplicationPeerConfig(ID_ONE);
        namespaces = new HashSet<String>();
        namespaces.add(ns1);
        rpc.setExcludeNamespaces(namespaces);
        TestReplicationAdmin.hbaseAdmin.updateReplicationPeerConfig(ID_ONE, rpc);
        namespaces = TestReplicationAdmin.hbaseAdmin.getReplicationPeerConfig(ID_ONE).getExcludeNamespaces();
        Assert.assertEquals(1, namespaces.size());
        Assert.assertTrue(namespaces.contains(ns1));
        TestReplicationAdmin.hbaseAdmin.removeReplicationPeer(ID_ONE);
    }

    @Test
    public void testPeerExcludeTableCFs() throws Exception {
        ReplicationPeerConfig rpc = new ReplicationPeerConfig();
        rpc.setClusterKey(KEY_ONE);
        TableName tab1 = TableName.valueOf("t1");
        TableName tab2 = TableName.valueOf("t2");
        TableName tab3 = TableName.valueOf("t3");
        TableName tab4 = TableName.valueOf("t4");
        // Add a valid peer
        TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_ONE, rpc);
        rpc = TestReplicationAdmin.hbaseAdmin.getReplicationPeerConfig(ID_ONE);
        Assert.assertTrue(rpc.replicateAllUserTables());
        Map<TableName, List<String>> tableCFs = new HashMap<TableName, List<String>>();
        tableCFs.put(tab1, null);
        rpc.setExcludeTableCFsMap(tableCFs);
        TestReplicationAdmin.hbaseAdmin.updateReplicationPeerConfig(ID_ONE, rpc);
        Map<TableName, List<String>> result = TestReplicationAdmin.hbaseAdmin.getReplicationPeerConfig(ID_ONE).getExcludeTableCFsMap();
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(true, result.containsKey(tab1));
        Assert.assertNull(result.get(tab1));
        tableCFs.put(tab2, new ArrayList<String>());
        tableCFs.get(tab2).add("f1");
        rpc.setExcludeTableCFsMap(tableCFs);
        TestReplicationAdmin.hbaseAdmin.updateReplicationPeerConfig(ID_ONE, rpc);
        result = TestReplicationAdmin.hbaseAdmin.getReplicationPeerConfig(ID_ONE).getExcludeTableCFsMap();
        Assert.assertEquals(2, result.size());
        Assert.assertTrue("Should contain t1", result.containsKey(tab1));
        Assert.assertTrue("Should contain t2", result.containsKey(tab2));
        Assert.assertNull(result.get(tab1));
        Assert.assertEquals(1, result.get(tab2).size());
        Assert.assertEquals("f1", result.get(tab2).get(0));
        tableCFs.clear();
        tableCFs.put(tab3, new ArrayList<String>());
        tableCFs.put(tab4, new ArrayList<String>());
        tableCFs.get(tab4).add("f1");
        tableCFs.get(tab4).add("f2");
        rpc.setExcludeTableCFsMap(tableCFs);
        TestReplicationAdmin.hbaseAdmin.updateReplicationPeerConfig(ID_ONE, rpc);
        result = TestReplicationAdmin.hbaseAdmin.getReplicationPeerConfig(ID_ONE).getExcludeTableCFsMap();
        Assert.assertEquals(2, result.size());
        Assert.assertTrue("Should contain t3", result.containsKey(tab3));
        Assert.assertTrue("Should contain t4", result.containsKey(tab4));
        Assert.assertNull(result.get(tab3));
        Assert.assertEquals(2, result.get(tab4).size());
        Assert.assertEquals("f1", result.get(tab4).get(0));
        Assert.assertEquals("f2", result.get(tab4).get(1));
        TestReplicationAdmin.hbaseAdmin.removeReplicationPeer(ID_ONE);
    }

    @Test
    public void testPeerConfigConflict() throws Exception {
        // Default replicate_all flag is true
        ReplicationPeerConfig rpc = new ReplicationPeerConfig();
        rpc.setClusterKey(KEY_ONE);
        String ns1 = "ns1";
        Set<String> namespaces = new HashSet<String>();
        namespaces.add(ns1);
        TableName tab1 = TableName.valueOf("ns2:tabl");
        Map<TableName, List<String>> tableCfs = new HashMap<TableName, List<String>>();
        tableCfs.put(tab1, new ArrayList<String>());
        try {
            rpc.setNamespaces(namespaces);
            TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_ONE, rpc);
            Assert.fail(("Should throw Exception." + " When replicate all flag is true, no need to config namespaces"));
        } catch (IOException e) {
            // OK
            rpc.setNamespaces(null);
        }
        try {
            rpc.setTableCFsMap(tableCfs);
            TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_ONE, rpc);
            Assert.fail(("Should throw Exception." + " When replicate all flag is true, no need to config table-cfs"));
        } catch (IOException e) {
            // OK
            rpc.setTableCFsMap(null);
        }
        // Set replicate_all flag to true
        rpc.setReplicateAllUserTables(false);
        try {
            rpc.setExcludeNamespaces(namespaces);
            TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_ONE, rpc);
            Assert.fail(("Should throw Exception." + " When replicate all flag is false, no need to config exclude namespaces"));
        } catch (IOException e) {
            // OK
            rpc.setExcludeNamespaces(null);
        }
        try {
            rpc.setExcludeTableCFsMap(tableCfs);
            TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_ONE, rpc);
            Assert.fail(("Should throw Exception." + " When replicate all flag is false, no need to config exclude table-cfs"));
        } catch (IOException e) {
            // OK
            rpc.setExcludeTableCFsMap(null);
        }
        rpc.setNamespaces(namespaces);
        rpc.setTableCFsMap(tableCfs);
        // OK to add a new peer which replicate_all flag is false and with namespaces, table-cfs config
        TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_ONE, rpc);
        // Default replicate_all flag is true
        ReplicationPeerConfig rpc2 = new ReplicationPeerConfig();
        rpc2.setClusterKey(KEY_SECOND);
        rpc2.setExcludeNamespaces(namespaces);
        rpc2.setExcludeTableCFsMap(tableCfs);
        // OK to add a new peer which replicate_all flag is true and with exclude namespaces, exclude
        // table-cfs config
        TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_SECOND, rpc2);
        TestReplicationAdmin.hbaseAdmin.removeReplicationPeer(ID_ONE);
        TestReplicationAdmin.hbaseAdmin.removeReplicationPeer(ID_SECOND);
    }

    @Test
    public void testNamespacesAndTableCfsConfigConflict() throws Exception {
        String ns1 = "ns1";
        String ns2 = "ns2";
        final TableName tableName1 = TableName.valueOf(((ns1 + ":") + (name.getMethodName())));
        final TableName tableName2 = TableName.valueOf((((ns2 + ":") + (name.getMethodName())) + "2"));
        ReplicationPeerConfig rpc = new ReplicationPeerConfig();
        rpc.setClusterKey(KEY_ONE);
        rpc.setReplicateAllUserTables(false);
        TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_ONE, rpc);
        rpc = TestReplicationAdmin.hbaseAdmin.getReplicationPeerConfig(ID_ONE);
        Set<String> namespaces = new HashSet<String>();
        namespaces.add(ns1);
        rpc.setNamespaces(namespaces);
        TestReplicationAdmin.hbaseAdmin.updateReplicationPeerConfig(ID_ONE, rpc);
        rpc = TestReplicationAdmin.hbaseAdmin.getReplicationPeerConfig(ID_ONE);
        try {
            Map<TableName, List<String>> tableCfs = new HashMap<>();
            tableCfs.put(tableName1, new ArrayList());
            rpc.setTableCFsMap(tableCfs);
            TestReplicationAdmin.hbaseAdmin.updateReplicationPeerConfig(ID_ONE, rpc);
            Assert.fail((((("Should throw ReplicationException" + " Because table ") + tableName1) + " conflict with namespace ") + ns1));
        } catch (Exception e) {
            // OK
        }
        rpc = TestReplicationAdmin.hbaseAdmin.getReplicationPeerConfig(ID_ONE);
        Map<TableName, List<String>> tableCfs = new HashMap<>();
        tableCfs.put(tableName2, new ArrayList());
        rpc.setTableCFsMap(tableCfs);
        TestReplicationAdmin.hbaseAdmin.updateReplicationPeerConfig(ID_ONE, rpc);
        rpc = TestReplicationAdmin.hbaseAdmin.getReplicationPeerConfig(ID_ONE);
        try {
            namespaces.clear();
            namespaces.add(ns2);
            rpc.setNamespaces(namespaces);
            TestReplicationAdmin.hbaseAdmin.updateReplicationPeerConfig(ID_ONE, rpc);
            Assert.fail((((("Should throw ReplicationException" + " Because namespace ") + ns2) + " conflict with table ") + tableName2));
        } catch (Exception e) {
            // OK
        }
        ReplicationPeerConfig rpc2 = new ReplicationPeerConfig();
        rpc2.setClusterKey(KEY_SECOND);
        TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_SECOND, rpc2);
        rpc2 = TestReplicationAdmin.hbaseAdmin.getReplicationPeerConfig(ID_SECOND);
        Set<String> excludeNamespaces = new HashSet<String>();
        excludeNamespaces.add(ns1);
        rpc2.setExcludeNamespaces(excludeNamespaces);
        TestReplicationAdmin.hbaseAdmin.updateReplicationPeerConfig(ID_SECOND, rpc2);
        rpc2 = TestReplicationAdmin.hbaseAdmin.getReplicationPeerConfig(ID_SECOND);
        try {
            Map<TableName, List<String>> excludeTableCfs = new HashMap<>();
            excludeTableCfs.put(tableName1, new ArrayList());
            rpc2.setExcludeTableCFsMap(excludeTableCfs);
            TestReplicationAdmin.hbaseAdmin.updateReplicationPeerConfig(ID_SECOND, rpc2);
            Assert.fail((((("Should throw ReplicationException" + " Because exclude table ") + tableName1) + " conflict with exclude namespace ") + ns1));
        } catch (Exception e) {
            // OK
        }
        rpc2 = TestReplicationAdmin.hbaseAdmin.getReplicationPeerConfig(ID_SECOND);
        Map<TableName, List<String>> excludeTableCfs = new HashMap<>();
        excludeTableCfs.put(tableName2, new ArrayList());
        rpc2.setExcludeTableCFsMap(excludeTableCfs);
        TestReplicationAdmin.hbaseAdmin.updateReplicationPeerConfig(ID_SECOND, rpc2);
        rpc2 = TestReplicationAdmin.hbaseAdmin.getReplicationPeerConfig(ID_SECOND);
        try {
            namespaces.clear();
            namespaces.add(ns2);
            rpc2.setNamespaces(namespaces);
            TestReplicationAdmin.hbaseAdmin.updateReplicationPeerConfig(ID_SECOND, rpc2);
            Assert.fail((((("Should throw ReplicationException" + " Because exclude namespace ") + ns2) + " conflict with exclude table ") + tableName2));
        } catch (Exception e) {
            // OK
        }
        TestReplicationAdmin.hbaseAdmin.removeReplicationPeer(ID_ONE);
        TestReplicationAdmin.hbaseAdmin.removeReplicationPeer(ID_SECOND);
    }

    @Test
    public void testPeerBandwidth() throws Exception {
        ReplicationPeerConfig rpc = new ReplicationPeerConfig();
        rpc.setClusterKey(KEY_ONE);
        TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_ONE, rpc);
        rpc = TestReplicationAdmin.admin.getPeerConfig(ID_ONE);
        Assert.assertEquals(0, rpc.getBandwidth());
        rpc.setBandwidth(2097152);
        TestReplicationAdmin.admin.updatePeerConfig(ID_ONE, rpc);
        Assert.assertEquals(2097152, TestReplicationAdmin.admin.getPeerConfig(ID_ONE).getBandwidth());
        TestReplicationAdmin.admin.removePeer(ID_ONE);
    }

    @Test
    public void testPeerClusterKey() throws Exception {
        ReplicationPeerConfigBuilder builder = ReplicationPeerConfig.newBuilder();
        builder.setClusterKey(KEY_ONE);
        TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_ONE, builder.build());
        try {
            builder.setClusterKey(KEY_SECOND);
            TestReplicationAdmin.hbaseAdmin.updateReplicationPeerConfig(ID_ONE, builder.build());
            Assert.fail("Change cluster key on an existing peer is not allowed");
        } catch (Exception e) {
            // OK
        }
    }

    @Test
    public void testPeerReplicationEndpointImpl() throws Exception {
        ReplicationPeerConfigBuilder builder = ReplicationPeerConfig.newBuilder();
        builder.setClusterKey(KEY_ONE);
        builder.setReplicationEndpointImpl(DummyReplicationEndpoint.class.getName());
        TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_ONE, builder.build());
        try {
            builder.setReplicationEndpointImpl(TestReplicationEndpoint.InterClusterReplicationEndpointForTest.class.getName());
            TestReplicationAdmin.hbaseAdmin.updateReplicationPeerConfig(ID_ONE, builder.build());
            Assert.fail("Change replication endpoint implementation class on an existing peer is not allowed");
        } catch (Exception e) {
            // OK
        }
        try {
            builder = ReplicationPeerConfig.newBuilder();
            builder.setClusterKey(KEY_ONE);
            TestReplicationAdmin.hbaseAdmin.updateReplicationPeerConfig(ID_ONE, builder.build());
            Assert.fail("Change replication endpoint implementation class on an existing peer is not allowed");
        } catch (Exception e) {
            // OK
        }
        builder = ReplicationPeerConfig.newBuilder();
        builder.setClusterKey(KEY_SECOND);
        TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_SECOND, builder.build());
        try {
            builder.setReplicationEndpointImpl(DummyReplicationEndpoint.class.getName());
            TestReplicationAdmin.hbaseAdmin.updateReplicationPeerConfig(ID_SECOND, builder.build());
            Assert.fail("Change replication endpoint implementation class on an existing peer is not allowed");
        } catch (Exception e) {
            // OK
        }
    }

    @Test
    public void testPeerRemoteWALDir() throws Exception {
        TableName tableName = TableName.valueOf(name.getMethodName());
        String rootDir = "hdfs://srv1:9999/hbase";
        ReplicationPeerConfigBuilder builder = ReplicationPeerConfig.newBuilder();
        builder.setClusterKey(KEY_ONE);
        TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_ONE, builder.build());
        ReplicationPeerConfig rpc = TestReplicationAdmin.hbaseAdmin.getReplicationPeerConfig(ID_ONE);
        Assert.assertNull(rpc.getRemoteWALDir());
        builder.setRemoteWALDir("hdfs://srv2:8888/hbase");
        try {
            TestReplicationAdmin.hbaseAdmin.updateReplicationPeerConfig(ID_ONE, builder.build());
            Assert.fail("Change remote wal dir is not allowed");
        } catch (Exception e) {
            // OK
            TestReplicationAdmin.LOG.info("Expected error:", e);
        }
        builder = ReplicationPeerConfig.newBuilder();
        builder.setClusterKey(KEY_SECOND);
        builder.setRemoteWALDir("whatever");
        try {
            TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_SECOND, builder.build());
            Assert.fail("Only support replicated table config for sync replication");
        } catch (Exception e) {
            // OK
            TestReplicationAdmin.LOG.info("Expected error:", e);
        }
        builder.setReplicateAllUserTables(false);
        Set<String> namespaces = new HashSet<String>();
        namespaces.add("ns1");
        builder.setNamespaces(namespaces);
        try {
            TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_SECOND, builder.build());
            Assert.fail("Only support replicated table config for sync replication");
        } catch (Exception e) {
            // OK
            TestReplicationAdmin.LOG.info("Expected error:", e);
        }
        builder.setNamespaces(null);
        try {
            TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_SECOND, builder.build());
            Assert.fail("Only support replicated table config for sync replication, and tables can't be empty");
        } catch (Exception e) {
            // OK
            TestReplicationAdmin.LOG.info("Expected error:", e);
        }
        Map<TableName, List<String>> tableCfs = new HashMap<>();
        tableCfs.put(tableName, Arrays.asList("cf1"));
        builder.setTableCFsMap(tableCfs);
        try {
            TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_SECOND, builder.build());
            Assert.fail("Only support replicated table config for sync replication");
        } catch (Exception e) {
            // OK
            TestReplicationAdmin.LOG.info("Expected error:", e);
        }
        tableCfs = new HashMap();
        tableCfs.put(tableName, new ArrayList());
        builder.setTableCFsMap(tableCfs);
        try {
            TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_SECOND, builder.build());
            Assert.fail("The remote WAL dir must be absolute");
        } catch (Exception e) {
            // OK
            TestReplicationAdmin.LOG.info("Expected error:", e);
        }
        builder.setRemoteWALDir("/hbase/remoteWALs");
        try {
            TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_SECOND, builder.build());
            Assert.fail("The remote WAL dir must be qualified");
        } catch (Exception e) {
            // OK
            TestReplicationAdmin.LOG.info("Expected error:", e);
        }
        builder.setRemoteWALDir(rootDir);
        TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_SECOND, builder.build());
        rpc = TestReplicationAdmin.hbaseAdmin.getReplicationPeerConfig(ID_SECOND);
        Assert.assertEquals(rootDir, rpc.getRemoteWALDir());
        try {
            builder.setRemoteWALDir("hdfs://srv2:8888/hbase");
            TestReplicationAdmin.hbaseAdmin.updateReplicationPeerConfig(ID_SECOND, builder.build());
            Assert.fail("Change remote wal dir is not allowed");
        } catch (Exception e) {
            // OK
            TestReplicationAdmin.LOG.info("Expected error:", e);
        }
        try {
            builder.setRemoteWALDir(null);
            TestReplicationAdmin.hbaseAdmin.updateReplicationPeerConfig(ID_SECOND, builder.build());
            Assert.fail("Change remote wal dir is not allowed");
        } catch (Exception e) {
            // OK
            TestReplicationAdmin.LOG.info("Expected error:", e);
        }
        try {
            builder = ReplicationPeerConfig.newBuilder(rpc);
            tableCfs = new HashMap();
            tableCfs.put(TableName.valueOf(("ns1:" + (name.getMethodName()))), new ArrayList());
            builder.setTableCFsMap(tableCfs);
            TestReplicationAdmin.hbaseAdmin.updateReplicationPeerConfig(ID_SECOND, builder.build());
            Assert.fail("Change replicated table config on an existing synchronous peer is not allowed");
        } catch (Exception e) {
            // OK
            TestReplicationAdmin.LOG.info("Expected error:", e);
        }
    }

    @Test
    public void testTransitSyncReplicationPeerState() throws Exception {
        TableName tableName = TableName.valueOf(name.getMethodName());
        TestReplicationAdmin.TEST_UTIL.createTable(tableName, Bytes.toBytes("family"));
        ReplicationPeerConfigBuilder builder = ReplicationPeerConfig.newBuilder();
        builder.setClusterKey(KEY_ONE);
        builder.setReplicateAllUserTables(false);
        TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_ONE, builder.build());
        Assert.assertEquals(NONE, TestReplicationAdmin.hbaseAdmin.getReplicationPeerSyncReplicationState(ID_ONE));
        try {
            TestReplicationAdmin.hbaseAdmin.transitReplicationPeerSyncReplicationState(ID_ONE, DOWNGRADE_ACTIVE);
            Assert.fail("Can't transit sync replication state if replication peer don't config remote wal dir");
        } catch (Exception e) {
            // OK
            TestReplicationAdmin.LOG.info("Expected error:", e);
        }
        Path rootDir = TestReplicationAdmin.TEST_UTIL.getDataTestDirOnTestFS("remoteWAL");
        builder = ReplicationPeerConfig.newBuilder();
        builder.setClusterKey(KEY_SECOND);
        builder.setRemoteWALDir(rootDir.makeQualified(TestReplicationAdmin.TEST_UTIL.getTestFileSystem().getUri(), TestReplicationAdmin.TEST_UTIL.getTestFileSystem().getWorkingDirectory()).toString());
        builder.setReplicateAllUserTables(false);
        Map<TableName, List<String>> tableCfs = new HashMap<>();
        tableCfs.put(tableName, new ArrayList());
        builder.setTableCFsMap(tableCfs);
        TestReplicationAdmin.hbaseAdmin.addReplicationPeer(ID_SECOND, builder.build());
        Assert.assertEquals(DOWNGRADE_ACTIVE, TestReplicationAdmin.hbaseAdmin.getReplicationPeerSyncReplicationState(ID_SECOND));
        // Disable and enable peer don't affect SyncReplicationState
        TestReplicationAdmin.hbaseAdmin.disableReplicationPeer(ID_SECOND);
        Assert.assertEquals(DOWNGRADE_ACTIVE, TestReplicationAdmin.hbaseAdmin.getReplicationPeerSyncReplicationState(ID_SECOND));
        TestReplicationAdmin.hbaseAdmin.enableReplicationPeer(ID_SECOND);
        Assert.assertEquals(DOWNGRADE_ACTIVE, TestReplicationAdmin.hbaseAdmin.getReplicationPeerSyncReplicationState(ID_SECOND));
        try {
            TestReplicationAdmin.hbaseAdmin.transitReplicationPeerSyncReplicationState(ID_SECOND, ACTIVE);
            Assert.fail("Can't transit sync replication state to ACTIVE if remote wal dir does not exist");
        } catch (Exception e) {
            // OK
            TestReplicationAdmin.LOG.info("Expected error:", e);
        }
        TestReplicationAdmin.TEST_UTIL.getTestFileSystem().mkdirs(ReplicationUtils.getPeerRemoteWALDir(rootDir, ID_SECOND));
        TestReplicationAdmin.hbaseAdmin.transitReplicationPeerSyncReplicationState(ID_SECOND, ACTIVE);
        Assert.assertEquals(ACTIVE, TestReplicationAdmin.hbaseAdmin.getReplicationPeerSyncReplicationState(ID_SECOND));
        TestReplicationAdmin.hbaseAdmin.transitReplicationPeerSyncReplicationState(ID_SECOND, STANDBY);
        Assert.assertEquals(STANDBY, TestReplicationAdmin.hbaseAdmin.getReplicationPeerSyncReplicationState(ID_SECOND));
        TestReplicationAdmin.hbaseAdmin.transitReplicationPeerSyncReplicationState(ID_SECOND, DOWNGRADE_ACTIVE);
        Assert.assertEquals(DOWNGRADE_ACTIVE, TestReplicationAdmin.hbaseAdmin.getReplicationPeerSyncReplicationState(ID_SECOND));
        TestReplicationAdmin.hbaseAdmin.transitReplicationPeerSyncReplicationState(ID_SECOND, ACTIVE);
        Assert.assertEquals(ACTIVE, TestReplicationAdmin.hbaseAdmin.getReplicationPeerSyncReplicationState(ID_SECOND));
        TestReplicationAdmin.hbaseAdmin.transitReplicationPeerSyncReplicationState(ID_SECOND, DOWNGRADE_ACTIVE);
        Assert.assertEquals(DOWNGRADE_ACTIVE, TestReplicationAdmin.hbaseAdmin.getReplicationPeerSyncReplicationState(ID_SECOND));
        TestReplicationAdmin.hbaseAdmin.transitReplicationPeerSyncReplicationState(ID_SECOND, STANDBY);
        Assert.assertEquals(STANDBY, TestReplicationAdmin.hbaseAdmin.getReplicationPeerSyncReplicationState(ID_SECOND));
        try {
            TestReplicationAdmin.hbaseAdmin.transitReplicationPeerSyncReplicationState(ID_SECOND, ACTIVE);
            Assert.fail("Can't transit sync replication state from STANDBY to ACTIVE");
        } catch (Exception e) {
            // OK
            TestReplicationAdmin.LOG.info("Expected error:", e);
        }
        TestReplicationAdmin.hbaseAdmin.transitReplicationPeerSyncReplicationState(ID_SECOND, DOWNGRADE_ACTIVE);
        Assert.assertEquals(DOWNGRADE_ACTIVE, TestReplicationAdmin.hbaseAdmin.getReplicationPeerSyncReplicationState(ID_SECOND));
        TestReplicationAdmin.hbaseAdmin.removeReplicationPeer(ID_ONE);
        TestReplicationAdmin.hbaseAdmin.removeReplicationPeer(ID_SECOND);
        Assert.assertEquals(0, TestReplicationAdmin.hbaseAdmin.listReplicationPeers().size());
    }
}

