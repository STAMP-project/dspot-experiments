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


import HConstants.EMPTY_END_ROW;
import HConstants.EMPTY_START_ROW;
import HConstants.REPLICATION_BULKLOAD_ENABLE_KEY;
import WALProtos.CompactionDescriptor;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.ChoreService;
import org.apache.hadoop.hbase.CoordinatedStateManager;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.Server;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ClusterConnection;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.regionserver.MultiVersionConcurrencyControl;
import org.apache.hadoop.hbase.replication.ReplicationFactory;
import org.apache.hadoop.hbase.replication.ReplicationPeer;
import org.apache.hadoop.hbase.replication.ReplicationPeerConfig;
import org.apache.hadoop.hbase.replication.ReplicationPeers;
import org.apache.hadoop.hbase.replication.ReplicationQueueStorage;
import org.apache.hadoop.hbase.replication.ReplicationSourceDummy;
import org.apache.hadoop.hbase.replication.ReplicationStorageFactory;
import org.apache.hadoop.hbase.replication.ReplicationUtils;
import org.apache.hadoop.hbase.replication.regionserver.ReplicationSourceManager.NodeFailoverWorker;
import org.apache.hadoop.hbase.shaded.protobuf.generated.WALProtos;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hadoop.hbase.wal.WAL;
import org.apache.hadoop.hbase.wal.WALEdit;
import org.apache.hadoop.hbase.wal.WALFactory;
import org.apache.hadoop.hbase.wal.WALKeyImpl;
import org.apache.hadoop.hbase.zookeeper.ZKWatcher;
import org.apache.hbase.thirdparty.com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An abstract class that tests ReplicationSourceManager. Classes that extend this class should
 * set up the proper config for this class and initialize the proper cluster using
 * HBaseTestingUtility.
 */
@Category({ ReplicationTests.class, MediumTests.class })
public abstract class TestReplicationSourceManager {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestReplicationSourceManager.class);

    protected static final Logger LOG = LoggerFactory.getLogger(TestReplicationSourceManager.class);

    protected static Configuration conf;

    protected static HBaseTestingUtility utility;

    protected static Replication replication;

    protected static ReplicationSourceManager manager;

    protected static ReplicationSourceManager managerOfCluster;

    protected static ZKWatcher zkw;

    protected static TableDescriptor htd;

    protected static RegionInfo hri;

    protected static final byte[] r1 = Bytes.toBytes("r1");

    protected static final byte[] r2 = Bytes.toBytes("r2");

    protected static final byte[] f1 = Bytes.toBytes("f1");

    protected static final byte[] f2 = Bytes.toBytes("f2");

    protected static final TableName test = TableName.valueOf("test");

    protected static final String slaveId = "1";

    protected static FileSystem fs;

    protected static Path oldLogDir;

    protected static Path logDir;

    protected static Path remoteLogDir;

    protected static CountDownLatch latch;

    protected static List<String> files = new ArrayList<>();

    protected static NavigableMap<byte[], Integer> scopes;

    @Rule
    public TestName testName = new TestName();

    @Test
    public void testLogRoll() throws Exception {
        long baseline = 1000;
        long time = baseline;
        MultiVersionConcurrencyControl mvcc = new MultiVersionConcurrencyControl();
        KeyValue kv = new KeyValue(TestReplicationSourceManager.r1, TestReplicationSourceManager.f1, TestReplicationSourceManager.r1);
        WALEdit edit = new WALEdit();
        edit.add(kv);
        WALFactory wals = new WALFactory(TestReplicationSourceManager.utility.getConfiguration(), URLEncoder.encode("regionserver:60020", "UTF8"));
        ReplicationSourceManager replicationManager = TestReplicationSourceManager.replication.getReplicationManager();
        wals.getWALProvider().addWALActionsListener(new ReplicationSourceWALActionListener(TestReplicationSourceManager.conf, replicationManager));
        final WAL wal = wals.getWAL(TestReplicationSourceManager.hri);
        TestReplicationSourceManager.manager.init();
        TableDescriptor htd = TableDescriptorBuilder.newBuilder(TableName.valueOf("tableame")).setColumnFamily(ColumnFamilyDescriptorBuilder.of(TestReplicationSourceManager.f1)).build();
        NavigableMap<byte[], Integer> scopes = new java.util.TreeMap(Bytes.BYTES_COMPARATOR);
        for (byte[] fam : htd.getColumnFamilyNames()) {
            scopes.put(fam, 0);
        }
        // Testing normal log rolling every 20
        for (long i = 1; i < 101; i++) {
            if ((i > 1) && ((i % 20) == 0)) {
                wal.rollWriter();
            }
            TestReplicationSourceManager.LOG.info(Long.toString(i));
            final long txid = wal.append(TestReplicationSourceManager.hri, new WALKeyImpl(TestReplicationSourceManager.hri.getEncodedNameAsBytes(), TestReplicationSourceManager.test, System.currentTimeMillis(), mvcc, scopes), edit, true);
            wal.sync(txid);
        }
        // Simulate a rapid insert that's followed
        // by a report that's still not totally complete (missing last one)
        TestReplicationSourceManager.LOG.info(((baseline + " and ") + time));
        baseline += 101;
        time = baseline;
        TestReplicationSourceManager.LOG.info(((baseline + " and ") + time));
        for (int i = 0; i < 3; i++) {
            wal.append(TestReplicationSourceManager.hri, new WALKeyImpl(TestReplicationSourceManager.hri.getEncodedNameAsBytes(), TestReplicationSourceManager.test, System.currentTimeMillis(), mvcc, scopes), edit, true);
        }
        wal.sync();
        int logNumber = 0;
        for (Map.Entry<String, NavigableSet<String>> entry : TestReplicationSourceManager.manager.getWALs().get(TestReplicationSourceManager.slaveId).entrySet()) {
            logNumber += entry.getValue().size();
        }
        Assert.assertEquals(6, logNumber);
        wal.rollWriter();
        ReplicationSourceInterface source = Mockito.mock(ReplicationSourceInterface.class);
        Mockito.when(source.getQueueId()).thenReturn("1");
        Mockito.when(source.isRecovered()).thenReturn(false);
        Mockito.when(source.isSyncReplication()).thenReturn(false);
        TestReplicationSourceManager.manager.logPositionAndCleanOldLogs(source, new WALEntryBatch(0, TestReplicationSourceManager.manager.getSources().get(0).getCurrentPath()));
        wal.append(TestReplicationSourceManager.hri, new WALKeyImpl(TestReplicationSourceManager.hri.getEncodedNameAsBytes(), TestReplicationSourceManager.test, System.currentTimeMillis(), mvcc, scopes), edit, true);
        wal.sync();
        Assert.assertEquals(1, TestReplicationSourceManager.manager.getWALs().size());
        // TODO Need a case with only 2 WALs and we only want to delete the first one
    }

    @Test
    public void testClaimQueues() throws Exception {
        Server server = new TestReplicationSourceManager.DummyServer("hostname0.example.org");
        ReplicationQueueStorage rq = ReplicationStorageFactory.getReplicationQueueStorage(server.getZooKeeper(), server.getConfiguration());
        // populate some znodes in the peer znode
        TestReplicationSourceManager.files.add("log1");
        TestReplicationSourceManager.files.add("log2");
        for (String file : TestReplicationSourceManager.files) {
            rq.addWAL(server.getServerName(), "1", file);
        }
        // create 3 DummyServers
        Server s1 = new TestReplicationSourceManager.DummyServer("dummyserver1.example.org");
        Server s2 = new TestReplicationSourceManager.DummyServer("dummyserver2.example.org");
        Server s3 = new TestReplicationSourceManager.DummyServer("dummyserver3.example.org");
        // create 3 DummyNodeFailoverWorkers
        TestReplicationSourceManager.DummyNodeFailoverWorker w1 = new TestReplicationSourceManager.DummyNodeFailoverWorker(server.getServerName(), s1);
        TestReplicationSourceManager.DummyNodeFailoverWorker w2 = new TestReplicationSourceManager.DummyNodeFailoverWorker(server.getServerName(), s2);
        TestReplicationSourceManager.DummyNodeFailoverWorker w3 = new TestReplicationSourceManager.DummyNodeFailoverWorker(server.getServerName(), s3);
        TestReplicationSourceManager.latch = new CountDownLatch(3);
        // start the threads
        w1.start();
        w2.start();
        w3.start();
        // make sure only one is successful
        int populatedMap = 0;
        // wait for result now... till all the workers are done.
        TestReplicationSourceManager.latch.await();
        populatedMap += ((w1.isLogZnodesMapPopulated()) + (w2.isLogZnodesMapPopulated())) + (w3.isLogZnodesMapPopulated());
        Assert.assertEquals(1, populatedMap);
        server.abort("", null);
    }

    @Test
    public void testCleanupFailoverQueues() throws Exception {
        Server server = new TestReplicationSourceManager.DummyServer("hostname1.example.org");
        ReplicationQueueStorage rq = ReplicationStorageFactory.getReplicationQueueStorage(server.getZooKeeper(), server.getConfiguration());
        // populate some znodes in the peer znode
        SortedSet<String> files = new TreeSet<>();
        String group = "testgroup";
        String file1 = ((group + ".") + (EnvironmentEdgeManager.currentTime())) + ".log1";
        String file2 = ((group + ".") + (EnvironmentEdgeManager.currentTime())) + ".log2";
        files.add(file1);
        files.add(file2);
        for (String file : files) {
            rq.addWAL(server.getServerName(), "1", file);
        }
        Server s1 = new TestReplicationSourceManager.DummyServer("dummyserver1.example.org");
        ReplicationPeers rp1 = ReplicationFactory.getReplicationPeers(s1.getZooKeeper(), s1.getConfiguration());
        rp1.init();
        NodeFailoverWorker w1 = TestReplicationSourceManager.manager.new NodeFailoverWorker(server.getServerName());
        w1.run();
        Assert.assertEquals(1, TestReplicationSourceManager.manager.getWalsByIdRecoveredQueues().size());
        String id = "1-" + (server.getServerName().getServerName());
        Assert.assertEquals(files, TestReplicationSourceManager.manager.getWalsByIdRecoveredQueues().get(id).get(group));
        ReplicationSourceInterface source = Mockito.mock(ReplicationSourceInterface.class);
        Mockito.when(source.getQueueId()).thenReturn(id);
        Mockito.when(source.isRecovered()).thenReturn(true);
        Mockito.when(source.isSyncReplication()).thenReturn(false);
        TestReplicationSourceManager.manager.cleanOldLogs(file2, false, source);
        // log1 should be deleted
        Assert.assertEquals(Sets.newHashSet(file2), TestReplicationSourceManager.manager.getWalsByIdRecoveredQueues().get(id).get(group));
    }

    @Test
    public void testCleanupUnknownPeerZNode() throws Exception {
        Server server = new TestReplicationSourceManager.DummyServer("hostname2.example.org");
        ReplicationQueueStorage rq = ReplicationStorageFactory.getReplicationQueueStorage(server.getZooKeeper(), server.getConfiguration());
        // populate some znodes in the peer znode
        // add log to an unknown peer
        String group = "testgroup";
        rq.addWAL(server.getServerName(), "2", (group + ".log1"));
        rq.addWAL(server.getServerName(), "2", (group + ".log2"));
        NodeFailoverWorker w1 = TestReplicationSourceManager.manager.new NodeFailoverWorker(server.getServerName());
        w1.run();
        // The log of the unknown peer should be removed from zk
        for (String peer : TestReplicationSourceManager.manager.getAllQueues()) {
            Assert.assertTrue(peer.startsWith("1"));
        }
    }

    /**
     * Test for HBASE-9038, Replication.scopeWALEdits would NPE if it wasn't filtering out the
     * compaction WALEdit.
     */
    @Test
    public void testCompactionWALEdits() throws Exception {
        TableName tableName = TableName.valueOf("testCompactionWALEdits");
        WALProtos.CompactionDescriptor compactionDescriptor = CompactionDescriptor.getDefaultInstance();
        RegionInfo hri = RegionInfoBuilder.newBuilder(tableName).setStartKey(EMPTY_START_ROW).setEndKey(EMPTY_END_ROW).build();
        WALEdit edit = WALEdit.createCompaction(hri, compactionDescriptor);
        ReplicationSourceWALActionListener.scopeWALEdits(new WALKeyImpl(), edit, TestReplicationSourceManager.conf);
    }

    @Test
    public void testBulkLoadWALEditsWithoutBulkLoadReplicationEnabled() throws Exception {
        NavigableMap<byte[], Integer> scope = new java.util.TreeMap(Bytes.BYTES_COMPARATOR);
        // 1. Get the bulk load wal edit event
        WALEdit logEdit = getBulkLoadWALEdit(scope);
        // 2. Create wal key
        WALKeyImpl logKey = new WALKeyImpl(scope);
        // 3. Get the scopes for the key
        ReplicationSourceWALActionListener.scopeWALEdits(logKey, logEdit, TestReplicationSourceManager.conf);
        // 4. Assert that no bulk load entry scopes are added if bulk load hfile replication is disabled
        Assert.assertNull("No bulk load entries scope should be added if bulk load replication is disabled.", logKey.getReplicationScopes());
    }

    @Test
    public void testBulkLoadWALEdits() throws Exception {
        // 1. Get the bulk load wal edit event
        NavigableMap<byte[], Integer> scope = new java.util.TreeMap(Bytes.BYTES_COMPARATOR);
        WALEdit logEdit = getBulkLoadWALEdit(scope);
        // 2. Create wal key
        WALKeyImpl logKey = new WALKeyImpl(scope);
        // 3. Enable bulk load hfile replication
        Configuration bulkLoadConf = HBaseConfiguration.create(TestReplicationSourceManager.conf);
        bulkLoadConf.setBoolean(REPLICATION_BULKLOAD_ENABLE_KEY, true);
        // 4. Get the scopes for the key
        ReplicationSourceWALActionListener.scopeWALEdits(logKey, logEdit, bulkLoadConf);
        NavigableMap<byte[], Integer> scopes = logKey.getReplicationScopes();
        // Assert family with replication scope global is present in the key scopes
        Assert.assertTrue("This family scope is set to global, should be part of replication key scopes.", scopes.containsKey(TestReplicationSourceManager.f1));
        // Assert family with replication scope local is not present in the key scopes
        Assert.assertFalse("This family scope is set to local, should not be part of replication key scopes", scopes.containsKey(TestReplicationSourceManager.f2));
    }

    /**
     * Test whether calling removePeer() on a ReplicationSourceManager that failed on initializing the
     * corresponding ReplicationSourceInterface correctly cleans up the corresponding
     * replication queue and ReplicationPeer.
     * See HBASE-16096.
     */
    @Test
    public void testPeerRemovalCleanup() throws Exception {
        String replicationSourceImplName = TestReplicationSourceManager.conf.get("replication.replicationsource.implementation");
        final String peerId = "FakePeer";
        final ReplicationPeerConfig peerConfig = ReplicationPeerConfig.newBuilder().setClusterKey((("localhost:" + (getZkCluster().getClientPort())) + ":/hbase")).build();
        try {
            TestReplicationSourceManager.DummyServer server = new TestReplicationSourceManager.DummyServer();
            ReplicationQueueStorage rq = ReplicationStorageFactory.getReplicationQueueStorage(server.getZooKeeper(), server.getConfiguration());
            // Purposely fail ReplicationSourceManager.addSource() by causing ReplicationSourceInterface
            // initialization to throw an exception.
            TestReplicationSourceManager.conf.set("replication.replicationsource.implementation", TestReplicationSourceManager.FailInitializeDummyReplicationSource.class.getName());
            TestReplicationSourceManager.manager.getReplicationPeers();
            // Set up the znode and ReplicationPeer for the fake peer
            // Don't wait for replication source to initialize, we know it won't.
            addPeerAndWait(peerId, peerConfig, false);
            // Sanity check
            Assert.assertNull(TestReplicationSourceManager.manager.getSource(peerId));
            // Create a replication queue for the fake peer
            rq.addWAL(server.getServerName(), peerId, "FakeFile");
            // Unregister peer, this should remove the peer and clear all queues associated with it
            // Need to wait for the ReplicationTracker to pick up the changes and notify listeners.
            removePeerAndWait(peerId);
            Assert.assertFalse(rq.getAllQueues(server.getServerName()).contains(peerId));
        } finally {
            TestReplicationSourceManager.conf.set("replication.replicationsource.implementation", replicationSourceImplName);
            removePeerAndWait(peerId);
        }
    }

    @Test
    public void testRemovePeerMetricsCleanup() throws Exception {
        final String peerId = "DummyPeer";
        final ReplicationPeerConfig peerConfig = ReplicationPeerConfig.newBuilder().setClusterKey((("localhost:" + (getZkCluster().getClientPort())) + ":/hbase")).build();
        try {
            MetricsReplicationSourceSource globalSource = TestReplicationSourceManager.getGlobalSource();
            final int globalLogQueueSizeInitial = globalSource.getSizeOfLogQueue();
            final long sizeOfLatestPath = TestReplicationSourceManager.getSizeOfLatestPath();
            addPeerAndWait(peerId, peerConfig, true);
            Assert.assertEquals((sizeOfLatestPath + globalLogQueueSizeInitial), globalSource.getSizeOfLogQueue());
            ReplicationSourceInterface source = TestReplicationSourceManager.manager.getSource(peerId);
            // Sanity check
            Assert.assertNotNull(source);
            final int sizeOfSingleLogQueue = source.getSourceMetrics().getSizeOfLogQueue();
            // Enqueue log and check if metrics updated
            source.enqueueLog(new Path("abc"));
            Assert.assertEquals((1 + sizeOfSingleLogQueue), source.getSourceMetrics().getSizeOfLogQueue());
            Assert.assertEquals(((source.getSourceMetrics().getSizeOfLogQueue()) + globalLogQueueSizeInitial), globalSource.getSizeOfLogQueue());
            // Removing the peer should reset the global metrics
            removePeerAndWait(peerId);
            Assert.assertEquals(globalLogQueueSizeInitial, globalSource.getSizeOfLogQueue());
            // Adding the same peer back again should reset the single source metrics
            addPeerAndWait(peerId, peerConfig, true);
            source = TestReplicationSourceManager.manager.getSource(peerId);
            Assert.assertNotNull(source);
            Assert.assertEquals(((source.getSourceMetrics().getSizeOfLogQueue()) + globalLogQueueSizeInitial), globalSource.getSizeOfLogQueue());
        } finally {
            removePeerAndWait(peerId);
        }
    }

    @Test
    public void testRemoveRemoteWALs() throws Exception {
        String peerId2 = (TestReplicationSourceManager.slaveId) + "_2";
        addPeerAndWait(peerId2, ReplicationPeerConfig.newBuilder().setClusterKey((("localhost:" + (getZkCluster().getClientPort())) + ":/hbase")).build(), true);
        try {
            // make sure that we can deal with files which does not exist
            String walNameNotExists = (("remoteWAL-12345-" + (TestReplicationSourceManager.slaveId)) + ".12345") + (ReplicationUtils.SYNC_WAL_SUFFIX);
            Path wal = new Path(TestReplicationSourceManager.logDir, walNameNotExists);
            TestReplicationSourceManager.manager.preLogRoll(wal);
            TestReplicationSourceManager.manager.postLogRoll(wal);
            Path remoteLogDirForPeer = new Path(TestReplicationSourceManager.remoteLogDir, TestReplicationSourceManager.slaveId);
            TestReplicationSourceManager.fs.mkdirs(remoteLogDirForPeer);
            String walName = (("remoteWAL-12345-" + (TestReplicationSourceManager.slaveId)) + ".23456") + (ReplicationUtils.SYNC_WAL_SUFFIX);
            Path remoteWAL = new Path(remoteLogDirForPeer, walName).makeQualified(TestReplicationSourceManager.fs.getUri(), TestReplicationSourceManager.fs.getWorkingDirectory());
            TestReplicationSourceManager.fs.create(remoteWAL).close();
            wal = new Path(TestReplicationSourceManager.logDir, walName);
            TestReplicationSourceManager.manager.preLogRoll(wal);
            TestReplicationSourceManager.manager.postLogRoll(wal);
            ReplicationSourceInterface source = mockReplicationSource(peerId2);
            TestReplicationSourceManager.manager.cleanOldLogs(walName, true, source);
            // still there if peer id does not match
            Assert.assertTrue(TestReplicationSourceManager.fs.exists(remoteWAL));
            source = mockReplicationSource(TestReplicationSourceManager.slaveId);
            TestReplicationSourceManager.manager.cleanOldLogs(walName, true, source);
            Assert.assertFalse(TestReplicationSourceManager.fs.exists(remoteWAL));
        } finally {
            removePeerAndWait(peerId2);
        }
    }

    @Test
    public void testSameWALPrefix() throws IOException {
        Set<String> latestWalsBefore = TestReplicationSourceManager.manager.getLastestPath().stream().map(Path::getName).collect(Collectors.toSet());
        String walName1 = "localhost,8080,12345-45678-Peer.34567";
        String walName2 = "localhost,8080,12345.56789";
        TestReplicationSourceManager.manager.preLogRoll(new Path(walName1));
        TestReplicationSourceManager.manager.preLogRoll(new Path(walName2));
        Set<String> latestWals = TestReplicationSourceManager.manager.getLastestPath().stream().map(Path::getName).filter(( n) -> !(latestWalsBefore.contains(n))).collect(Collectors.toSet());
        Assert.assertEquals(2, latestWals.size());
        Assert.assertTrue(latestWals.contains(walName1));
        Assert.assertTrue(latestWals.contains(walName2));
    }

    static class DummyNodeFailoverWorker extends Thread {
        private Map<String, Set<String>> logZnodesMap;

        Server server;

        private ServerName deadRS;

        ReplicationQueueStorage rq;

        public DummyNodeFailoverWorker(ServerName deadRS, Server s) throws Exception {
            this.deadRS = deadRS;
            this.server = s;
            this.rq = ReplicationStorageFactory.getReplicationQueueStorage(server.getZooKeeper(), server.getConfiguration());
        }

        @Override
        public void run() {
            try {
                logZnodesMap = new HashMap<>();
                List<String> queues = rq.getAllQueues(deadRS);
                for (String queue : queues) {
                    Pair<String, SortedSet<String>> pair = rq.claimQueue(deadRS, queue, server.getServerName());
                    if (pair != null) {
                        logZnodesMap.put(pair.getFirst(), pair.getSecond());
                    }
                }
                server.abort("Done with testing", null);
            } catch (Exception e) {
                TestReplicationSourceManager.LOG.error("Got exception while running NodeFailoverWorker", e);
            } finally {
                TestReplicationSourceManager.latch.countDown();
            }
        }

        /**
         *
         *
         * @return 1 when the map is not empty.
         */
        private int isLogZnodesMapPopulated() {
            Collection<Set<String>> sets = logZnodesMap.values();
            if ((sets.size()) > 1) {
                throw new RuntimeException(("unexpected size of logZnodesMap: " + (sets.size())));
            }
            if ((sets.size()) == 1) {
                Set<String> s = sets.iterator().next();
                for (String file : TestReplicationSourceManager.files) {
                    // at least one file was missing
                    if (!(s.contains(file))) {
                        return 0;
                    }
                }
                return 1;// we found all the files

            }
            return 0;
        }
    }

    static class FailInitializeDummyReplicationSource extends ReplicationSourceDummy {
        @Override
        public void init(Configuration conf, FileSystem fs, ReplicationSourceManager manager, ReplicationQueueStorage rq, ReplicationPeer rp, Server server, String peerClusterId, UUID clusterId, WALFileLengthProvider walFileLengthProvider, MetricsSource metrics) throws IOException {
            throw new IOException("Failing deliberately");
        }
    }

    static class DummyServer implements Server {
        String hostname;

        DummyServer() {
            hostname = "hostname.example.org";
        }

        DummyServer(String hostname) {
            this.hostname = hostname;
        }

        @Override
        public Configuration getConfiguration() {
            return TestReplicationSourceManager.conf;
        }

        @Override
        public ZKWatcher getZooKeeper() {
            return TestReplicationSourceManager.zkw;
        }

        @Override
        public CoordinatedStateManager getCoordinatedStateManager() {
            return null;
        }

        @Override
        public ClusterConnection getConnection() {
            return null;
        }

        @Override
        public ServerName getServerName() {
            return ServerName.valueOf(hostname, 1234, 1L);
        }

        @Override
        public void abort(String why, Throwable e) {
            // To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean isAborted() {
            return false;
        }

        @Override
        public void stop(String why) {
            // To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean isStopped() {
            return false;// To change body of implemented methods use File | Settings | File Templates.

        }

        @Override
        public ChoreService getChoreService() {
            return null;
        }

        @Override
        public ClusterConnection getClusterConnection() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public FileSystem getFileSystem() {
            return null;
        }

        @Override
        public boolean isStopping() {
            return false;
        }

        @Override
        public Connection createConnection(Configuration conf) throws IOException {
            return null;
        }
    }
}

