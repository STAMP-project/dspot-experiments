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
package org.apache.hadoop.hdfs.qjournal.server;


import DFSConfigKeys.DFS_JOURNALNODE_EDITS_DIR_KEY;
import DFSConfigKeys.DFS_JOURNALNODE_ENABLE_SYNC_KEY;
import DFSConfigKeys.DFS_JOURNALNODE_HTTP_ADDRESS_KEY;
import HdfsServerConstants.NAMENODE_LAYOUT_VERSION;
import NameNodeLayoutVersion.CURRENT_LAYOUT_VERSION;
import com.google.common.base.Charsets;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DFSUtilClient;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.qjournal.QJMTestUtil;
import org.apache.hadoop.hdfs.qjournal.client.IPCLoggerChannel;
import org.apache.hadoop.hdfs.qjournal.protocol.QJournalProtocolProtos.NewEpochResponseProto;
import org.apache.hadoop.hdfs.qjournal.protocol.QJournalProtocolProtos.PrepareRecoveryResponseProto;
import org.apache.hadoop.hdfs.server.protocol.NamespaceInfo;
import org.apache.hadoop.metrics2.MetricsRecordBuilder;
import org.apache.hadoop.metrics2.lib.DefaultMetricsSystem;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.MetricsAsserts;
import org.apache.hadoop.test.PathUtils;
import org.apache.hadoop.util.Shell;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockito.Mockito;


public class TestJournalNode {
    private static final NamespaceInfo FAKE_NSINFO = new NamespaceInfo(12345, "mycluster", "my-bp", 0L);

    @Rule
    public TestName testName = new TestName();

    private static final File TEST_BUILD_DATA = PathUtils.getTestDir(TestJournalNode.class);

    private JournalNode jn;

    private Journal journal;

    private final Configuration conf = new Configuration();

    private IPCLoggerChannel ch;

    private String journalId;

    static {
        // Avoid an error when we double-initialize JvmMetrics
        DefaultMetricsSystem.setMiniClusterMode(true);
    }

    @Test(timeout = 100000)
    public void testJournalDirPerNameSpace() {
        Collection<String> nameServiceIds = DFSUtilClient.getNameServiceIds(conf);
        setupStaticHostResolution(2, "journalnode");
        for (String nsId : nameServiceIds) {
            String jid = "test-journalid-" + nsId;
            Journal nsJournal = jn.getJournal(jid);
            JNStorage journalStorage = nsJournal.getStorage();
            File editsDir = new File((((((((MiniDFSCluster.getBaseDirectory()) + (File.separator)) + "TestJournalNode") + (File.separator)) + nsId) + (File.separator)) + jid));
            Assert.assertEquals(editsDir.toString(), journalStorage.getRoot().toString());
        }
    }

    @Test(timeout = 100000)
    public void testJournalCommonDirAcrossNameSpace() {
        Collection<String> nameServiceIds = DFSUtilClient.getNameServiceIds(conf);
        setupStaticHostResolution(2, "journalnode");
        for (String nsId : nameServiceIds) {
            String jid = "test-journalid-" + nsId;
            Journal nsJournal = jn.getJournal(jid);
            JNStorage journalStorage = nsJournal.getStorage();
            File editsDir = new File((((((MiniDFSCluster.getBaseDirectory()) + (File.separator)) + "TestJournalNode") + (File.separator)) + jid));
            Assert.assertEquals(editsDir.toString(), journalStorage.getRoot().toString());
        }
    }

    @Test(timeout = 100000)
    public void testJournalDefaultDirForOneNameSpace() {
        Collection<String> nameServiceIds = DFSUtilClient.getNameServiceIds(conf);
        setupStaticHostResolution(2, "journalnode");
        String jid = "test-journalid-ns1";
        Journal nsJournal = jn.getJournal(jid);
        JNStorage journalStorage = nsJournal.getStorage();
        File editsDir = new File((((((((MiniDFSCluster.getBaseDirectory()) + (File.separator)) + "TestJournalNode") + (File.separator)) + "ns1") + (File.separator)) + jid));
        Assert.assertEquals(editsDir.toString(), journalStorage.getRoot().toString());
        jid = "test-journalid-ns2";
        nsJournal = jn.getJournal(jid);
        journalStorage = nsJournal.getStorage();
        editsDir = new File((((DFSConfigKeys.DFS_JOURNALNODE_EDITS_DIR_DEFAULT) + (File.separator)) + jid));
        Assert.assertEquals(editsDir.toString(), journalStorage.getRoot().toString());
    }

    @Test(timeout = 100000)
    public void testJournal() throws Exception {
        MetricsRecordBuilder metrics = MetricsAsserts.getMetrics(journal.getMetrics().getName());
        MetricsAsserts.assertCounter("BatchesWritten", 0L, metrics);
        MetricsAsserts.assertCounter("BatchesWrittenWhileLagging", 0L, metrics);
        MetricsAsserts.assertGauge("CurrentLagTxns", 0L, metrics);
        MetricsAsserts.assertGauge("LastJournalTimestamp", 0L, metrics);
        long beginTimestamp = System.currentTimeMillis();
        IPCLoggerChannel ch = new IPCLoggerChannel(conf, TestJournalNode.FAKE_NSINFO, journalId, jn.getBoundIpcAddress());
        ch.newEpoch(1).get();
        ch.setEpoch(1);
        ch.startLogSegment(1, CURRENT_LAYOUT_VERSION).get();
        ch.sendEdits(1L, 1, 1, "hello".getBytes(Charsets.UTF_8)).get();
        metrics = MetricsAsserts.getMetrics(journal.getMetrics().getName());
        MetricsAsserts.assertCounter("BatchesWritten", 1L, metrics);
        MetricsAsserts.assertCounter("BatchesWrittenWhileLagging", 0L, metrics);
        MetricsAsserts.assertGauge("CurrentLagTxns", 0L, metrics);
        long lastJournalTimestamp = MetricsAsserts.getLongGauge("LastJournalTimestamp", metrics);
        Assert.assertTrue((lastJournalTimestamp > beginTimestamp));
        beginTimestamp = lastJournalTimestamp;
        ch.setCommittedTxId(100L);
        ch.sendEdits(1L, 2, 1, "goodbye".getBytes(Charsets.UTF_8)).get();
        metrics = MetricsAsserts.getMetrics(journal.getMetrics().getName());
        MetricsAsserts.assertCounter("BatchesWritten", 2L, metrics);
        MetricsAsserts.assertCounter("BatchesWrittenWhileLagging", 1L, metrics);
        MetricsAsserts.assertGauge("CurrentLagTxns", 98L, metrics);
        lastJournalTimestamp = MetricsAsserts.getLongGauge("LastJournalTimestamp", metrics);
        Assert.assertTrue((lastJournalTimestamp > beginTimestamp));
    }

    @Test(timeout = 100000)
    public void testReturnsSegmentInfoAtEpochTransition() throws Exception {
        ch.newEpoch(1).get();
        ch.setEpoch(1);
        ch.startLogSegment(1, CURRENT_LAYOUT_VERSION).get();
        ch.sendEdits(1L, 1, 2, QJMTestUtil.createTxnData(1, 2)).get();
        // Switch to a new epoch without closing earlier segment
        NewEpochResponseProto response = ch.newEpoch(2).get();
        ch.setEpoch(2);
        Assert.assertEquals(1, response.getLastSegmentTxId());
        ch.finalizeLogSegment(1, 2).get();
        // Switch to a new epoch after just closing the earlier segment.
        response = ch.newEpoch(3).get();
        ch.setEpoch(3);
        Assert.assertEquals(1, response.getLastSegmentTxId());
        // Start a segment but don't write anything, check newEpoch segment info
        ch.startLogSegment(3, CURRENT_LAYOUT_VERSION).get();
        response = ch.newEpoch(4).get();
        ch.setEpoch(4);
        // Because the new segment is empty, it is equivalent to not having
        // started writing it. Hence, we should return the prior segment txid.
        Assert.assertEquals(1, response.getLastSegmentTxId());
    }

    @Test(timeout = 100000)
    public void testHttpServer() throws Exception {
        String urlRoot = jn.getHttpServerURI();
        // Check default servlets.
        String pageContents = DFSTestUtil.urlGet(new URL((urlRoot + "/jmx")));
        Assert.assertTrue(("Bad contents: " + pageContents), pageContents.contains("Hadoop:service=JournalNode,name=JvmMetrics"));
        // Create some edits on server side
        byte[] EDITS_DATA = QJMTestUtil.createTxnData(1, 3);
        IPCLoggerChannel ch = new IPCLoggerChannel(conf, TestJournalNode.FAKE_NSINFO, journalId, jn.getBoundIpcAddress());
        ch.newEpoch(1).get();
        ch.setEpoch(1);
        ch.startLogSegment(1, CURRENT_LAYOUT_VERSION).get();
        ch.sendEdits(1L, 1, 3, EDITS_DATA).get();
        ch.finalizeLogSegment(1, 3).get();
        // Attempt to retrieve via HTTP, ensure we get the data back
        // including the header we expected
        byte[] retrievedViaHttp = DFSTestUtil.urlGetBytes(new URL(((urlRoot + "/getJournal?segmentTxId=1&jid=") + (journalId))));
        byte[] expected = // layout flags section
        Bytes.concat(Ints.toByteArray(NAMENODE_LAYOUT_VERSION), new byte[]{ 0, 0, 0, 0 }, EDITS_DATA);
        Assert.assertArrayEquals(expected, retrievedViaHttp);
        // Attempt to fetch a non-existent file, check that we get an
        // error status code
        URL badUrl = new URL(((urlRoot + "/getJournal?segmentTxId=12345&jid=") + (journalId)));
        HttpURLConnection connection = ((HttpURLConnection) (badUrl.openConnection()));
        try {
            Assert.assertEquals(404, connection.getResponseCode());
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Test that the JournalNode performs correctly as a Paxos
     * <em>Acceptor</em> process.
     */
    @Test(timeout = 100000)
    public void testAcceptRecoveryBehavior() throws Exception {
        // We need to run newEpoch() first, or else we have no way to distinguish
        // different proposals for the same decision.
        try {
            ch.prepareRecovery(1L).get();
            Assert.fail("Did not throw IllegalState when trying to run paxos without an epoch");
        } catch (ExecutionException ise) {
            GenericTestUtils.assertExceptionContains("bad epoch", ise);
        }
        ch.newEpoch(1).get();
        ch.setEpoch(1);
        // prepare() with no previously accepted value and no logs present
        PrepareRecoveryResponseProto prep = ch.prepareRecovery(1L).get();
        System.err.println(("Prep: " + prep));
        Assert.assertFalse(prep.hasAcceptedInEpoch());
        Assert.assertFalse(prep.hasSegmentState());
        // Make a log segment, and prepare again -- this time should see the
        // segment existing.
        ch.startLogSegment(1L, CURRENT_LAYOUT_VERSION).get();
        ch.sendEdits(1L, 1L, 1, QJMTestUtil.createTxnData(1, 1)).get();
        prep = ch.prepareRecovery(1L).get();
        System.err.println(("Prep: " + prep));
        Assert.assertFalse(prep.hasAcceptedInEpoch());
        Assert.assertTrue(prep.hasSegmentState());
        // accept() should save the accepted value in persistent storage
        ch.acceptRecovery(prep.getSegmentState(), new URL("file:///dev/null")).get();
        // So another prepare() call from a new epoch would return this value
        ch.newEpoch(2);
        ch.setEpoch(2);
        prep = ch.prepareRecovery(1L).get();
        Assert.assertEquals(1L, prep.getAcceptedInEpoch());
        Assert.assertEquals(1L, prep.getSegmentState().getEndTxId());
        // A prepare() or accept() call from an earlier epoch should now be rejected
        ch.setEpoch(1);
        try {
            ch.prepareRecovery(1L).get();
            Assert.fail("prepare from earlier epoch not rejected");
        } catch (ExecutionException ioe) {
            GenericTestUtils.assertExceptionContains("epoch 1 is less than the last promised epoch 2", ioe);
        }
        try {
            ch.acceptRecovery(prep.getSegmentState(), new URL("file:///dev/null")).get();
            Assert.fail("accept from earlier epoch not rejected");
        } catch (ExecutionException ioe) {
            GenericTestUtils.assertExceptionContains("epoch 1 is less than the last promised epoch 2", ioe);
        }
    }

    @Test(timeout = 100000)
    public void testFailToStartWithBadConfig() throws Exception {
        Configuration conf = new Configuration();
        conf.set(DFS_JOURNALNODE_EDITS_DIR_KEY, "non-absolute-path");
        conf.set(DFS_JOURNALNODE_HTTP_ADDRESS_KEY, "0.0.0.0:0");
        TestJournalNode.assertJNFailsToStart(conf, "should be an absolute path");
        // Existing file which is not a directory
        File existingFile = new File(TestJournalNode.TEST_BUILD_DATA, "testjournalnodefile");
        Assert.assertTrue(existingFile.createNewFile());
        try {
            conf.set(DFS_JOURNALNODE_EDITS_DIR_KEY, existingFile.getAbsolutePath());
            TestJournalNode.assertJNFailsToStart(conf, "Not a directory");
        } finally {
            existingFile.delete();
        }
        // Directory which cannot be created
        conf.set(DFS_JOURNALNODE_EDITS_DIR_KEY, (Shell.WINDOWS ? "\\\\cannotBeCreated" : "/proc/does-not-exist"));
        TestJournalNode.assertJNFailsToStart(conf, "Cannot create directory");
    }

    /**
     * Simple test of how fast the code path is to write edits.
     * This isn't a true unit test, but can be run manually to
     * check performance.
     *
     * At the time of development, this test ran in ~4sec on an
     * SSD-enabled laptop (1.8ms/batch).
     */
    @Test(timeout = 100000)
    public void testPerformance() throws Exception {
        doPerfTest(8192, 1024);// 8MB

    }

    /**
     * Test case to check if JournalNode exits cleanly when httpserver or rpc
     * server fails to start. Call to JournalNode start should fail with bind
     * exception as the port is in use by the JN started in @Before routine
     */
    @Test
    public void testJournalNodeStartupFailsCleanly() {
        JournalNode jNode = Mockito.spy(new JournalNode());
        try {
            jNode.setConf(conf);
            jNode.start();
            Assert.fail("Should throw bind exception");
        } catch (Exception e) {
            GenericTestUtils.assertExceptionContains("java.net.BindException: Port in use", e);
        }
        Mockito.verify(jNode).stop(1);
    }

    @Test
    public void testJournalNodeSyncerNotStartWhenSyncDisabled() throws IOException {
        // JournalSyncer will not be started, as journalsync is not enabled
        conf.setBoolean(DFS_JOURNALNODE_ENABLE_SYNC_KEY, false);
        jn.getOrCreateJournal(journalId);
        Assert.assertEquals(false, jn.getJournalSyncerStatus(journalId));
        Assert.assertEquals(false, jn.getJournal(journalId).getTriedJournalSyncerStartedwithnsId());
        // Trying by passing nameserviceId still journalnodesyncer should not start
        // IstriedJournalSyncerStartWithnsId should also be false
        jn.getOrCreateJournal(journalId, "mycluster");
        Assert.assertEquals(false, jn.getJournalSyncerStatus(journalId));
        Assert.assertEquals(false, jn.getJournal(journalId).getTriedJournalSyncerStartedwithnsId());
    }

    @Test
    public void testJournalNodeSyncerNotStartWhenSyncEnabledIncorrectURI() throws IOException {
        // JournalSyncer will not be started,
        // as shared edits hostnames are not resolved
        jn.getOrCreateJournal(journalId);
        Assert.assertEquals(false, jn.getJournalSyncerStatus(journalId));
        Assert.assertEquals(false, jn.getJournal(journalId).getTriedJournalSyncerStartedwithnsId());
        // Trying by passing nameserviceId, now
        // IstriedJournalSyncerStartWithnsId should be set
        // but journalnode syncer will not be started,
        // as hostnames are not resolved
        jn.getOrCreateJournal(journalId, "mycluster");
        Assert.assertEquals(false, jn.getJournalSyncerStatus(journalId));
        Assert.assertEquals(true, jn.getJournal(journalId).getTriedJournalSyncerStartedwithnsId());
    }

    @Test
    public void testJournalNodeSyncerNotStartWhenSyncEnabled() throws IOException {
        // JournalSyncer will not be started,
        // as shared edits hostnames are not resolved
        jn.getOrCreateJournal(journalId);
        Assert.assertEquals(false, jn.getJournalSyncerStatus(journalId));
        Assert.assertEquals(false, jn.getJournal(journalId).getTriedJournalSyncerStartedwithnsId());
        // Trying by passing nameserviceId and resolve hostnames
        // now IstriedJournalSyncerStartWithnsId should be set
        // and also journalnode syncer will also be started
        setupStaticHostResolution(2, "jn");
        jn.getOrCreateJournal(journalId, "mycluster");
        Assert.assertEquals(true, jn.getJournalSyncerStatus(journalId));
        Assert.assertEquals(true, jn.getJournal(journalId).getTriedJournalSyncerStartedwithnsId());
    }

    @Test
    public void testJournalNodeSyncwithFederationTypeConfigWithNameServiceId() throws IOException {
        // JournalSyncer will not be started, as nameserviceId passed is null,
        // but configured shared edits dir is appended with nameserviceId
        setupStaticHostResolution(2, "journalnode");
        jn.getOrCreateJournal(journalId);
        Assert.assertEquals(false, jn.getJournalSyncerStatus(journalId));
        Assert.assertEquals(false, jn.getJournal(journalId).getTriedJournalSyncerStartedwithnsId());
        // Trying by passing nameserviceId and resolve hostnames
        // now IstriedJournalSyncerStartWithnsId should be set
        // and also journalnode syncer will also be started
        jn.getOrCreateJournal(journalId, "ns1");
        Assert.assertEquals(true, jn.getJournalSyncerStatus(journalId));
        Assert.assertEquals(true, jn.getJournal(journalId).getTriedJournalSyncerStartedwithnsId());
    }

    @Test
    public void testJournalNodeSyncwithFederationTypeConfigWithNamenodeId() throws IOException {
        // JournalSyncer will not be started, as nameserviceId passed is null,
        // but configured shared edits dir is appended with nameserviceId +
        // namenodeId
        setupStaticHostResolution(2, "journalnode");
        jn.getOrCreateJournal(journalId);
        Assert.assertEquals(false, jn.getJournalSyncerStatus(journalId));
        Assert.assertEquals(false, jn.getJournal(journalId).getTriedJournalSyncerStartedwithnsId());
        // Trying by passing nameserviceId and resolve hostnames
        // now IstriedJournalSyncerStartWithnsId should be set
        // and also journalnode syncer will also be started
        jn.getOrCreateJournal(journalId, "ns1");
        Assert.assertEquals(true, jn.getJournalSyncerStatus(journalId));
        Assert.assertEquals(true, jn.getJournal(journalId).getTriedJournalSyncerStartedwithnsId());
    }

    @Test
    public void testJournalNodeSyncwithFederationTypeIncorrectConfigWithNamenodeId() throws IOException {
        // JournalSyncer will not be started, as nameserviceId passed is null,
        // but configured shared edits dir is appended with nameserviceId +
        // namenodeId
        setupStaticHostResolution(2, "journalnode");
        jn.getOrCreateJournal(journalId);
        Assert.assertEquals(false, jn.getJournalSyncerStatus(journalId));
        Assert.assertEquals(false, jn.getJournal(journalId).getTriedJournalSyncerStartedwithnsId());
        // Trying by passing nameserviceId and resolve hostnames
        // now IstriedJournalSyncerStartWithnsId should  be set
        // and  journalnode syncer will not  be started
        // as for each nnId, different shared Edits dir value is configured
        jn.getOrCreateJournal(journalId, "ns1");
        Assert.assertEquals(false, jn.getJournalSyncerStatus(journalId));
        Assert.assertEquals(true, jn.getJournal(journalId).getTriedJournalSyncerStartedwithnsId());
    }
}

