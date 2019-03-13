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
package org.apache.hadoop.hdfs.qjournal.client;


import ByteString.EMPTY;
import NameNodeLayoutVersion.CURRENT_LAYOUT_VERSION;
import QuorumJournalManager.LOG;
import QuorumJournalManager.QJM_RPC_MAX_TXNS_DEFAULT;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.SettableFuture;
import java.io.IOException;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.qjournal.QJMTestUtil;
import org.apache.hadoop.hdfs.qjournal.protocol.QJournalProtocolProtos.GetJournaledEditsResponseProto;
import org.apache.hadoop.hdfs.server.namenode.EditLogInputStream;
import org.apache.hadoop.hdfs.server.namenode.EditLogOutputStream;
import org.apache.hadoop.hdfs.server.protocol.NamespaceInfo;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.event.Level;


/**
 * True unit tests for QuorumJournalManager
 */
public class TestQuorumJournalManagerUnit {
    static {
        GenericTestUtils.setLogLevel(LOG, Level.TRACE);
    }

    private static final NamespaceInfo FAKE_NSINFO = new NamespaceInfo(12345, "mycluster", "my-bp", 0L);

    private final Configuration conf = new Configuration();

    private List<AsyncLogger> spyLoggers;

    private QuorumJournalManager qjm;

    @Test
    public void testAllLoggersStartOk() throws Exception {
        TestQuorumJournalManagerUnit.futureReturns(null).when(spyLoggers.get(0)).startLogSegment(Mockito.anyLong(), Mockito.eq(CURRENT_LAYOUT_VERSION));
        TestQuorumJournalManagerUnit.futureReturns(null).when(spyLoggers.get(1)).startLogSegment(Mockito.anyLong(), Mockito.eq(CURRENT_LAYOUT_VERSION));
        TestQuorumJournalManagerUnit.futureReturns(null).when(spyLoggers.get(2)).startLogSegment(Mockito.anyLong(), Mockito.eq(CURRENT_LAYOUT_VERSION));
        qjm.startLogSegment(1, CURRENT_LAYOUT_VERSION);
    }

    @Test
    public void testQuorumOfLoggersStartOk() throws Exception {
        TestQuorumJournalManagerUnit.futureReturns(null).when(spyLoggers.get(0)).startLogSegment(Mockito.anyLong(), Mockito.eq(CURRENT_LAYOUT_VERSION));
        TestQuorumJournalManagerUnit.futureReturns(null).when(spyLoggers.get(1)).startLogSegment(Mockito.anyLong(), Mockito.eq(CURRENT_LAYOUT_VERSION));
        TestQuorumJournalManagerUnit.futureThrows(new IOException("logger failed")).when(spyLoggers.get(2)).startLogSegment(Mockito.anyLong(), Mockito.eq(CURRENT_LAYOUT_VERSION));
        qjm.startLogSegment(1, CURRENT_LAYOUT_VERSION);
    }

    @Test
    public void testQuorumOfLoggersFail() throws Exception {
        TestQuorumJournalManagerUnit.futureReturns(null).when(spyLoggers.get(0)).startLogSegment(Mockito.anyLong(), Mockito.eq(CURRENT_LAYOUT_VERSION));
        TestQuorumJournalManagerUnit.futureThrows(new IOException("logger failed")).when(spyLoggers.get(1)).startLogSegment(Mockito.anyLong(), Mockito.eq(CURRENT_LAYOUT_VERSION));
        TestQuorumJournalManagerUnit.futureThrows(new IOException("logger failed")).when(spyLoggers.get(2)).startLogSegment(Mockito.anyLong(), Mockito.eq(CURRENT_LAYOUT_VERSION));
        try {
            qjm.startLogSegment(1, CURRENT_LAYOUT_VERSION);
            Assert.fail("Did not throw when quorum failed");
        } catch (QuorumException qe) {
            GenericTestUtils.assertExceptionContains("logger failed", qe);
        }
    }

    @Test
    public void testQuorumOutputStreamReport() throws Exception {
        TestQuorumJournalManagerUnit.futureReturns(null).when(spyLoggers.get(0)).startLogSegment(Mockito.anyLong(), Mockito.eq(CURRENT_LAYOUT_VERSION));
        TestQuorumJournalManagerUnit.futureReturns(null).when(spyLoggers.get(1)).startLogSegment(Mockito.anyLong(), Mockito.eq(CURRENT_LAYOUT_VERSION));
        TestQuorumJournalManagerUnit.futureReturns(null).when(spyLoggers.get(2)).startLogSegment(Mockito.anyLong(), Mockito.eq(CURRENT_LAYOUT_VERSION));
        QuorumOutputStream os = ((QuorumOutputStream) (qjm.startLogSegment(1, CURRENT_LAYOUT_VERSION)));
        String report = os.generateReport();
        Assert.assertFalse("Report should be plain text", report.contains("<"));
    }

    @Test
    public void testWriteEdits() throws Exception {
        EditLogOutputStream stm = createLogSegment();
        QJMTestUtil.writeOp(stm, 1);
        QJMTestUtil.writeOp(stm, 2);
        stm.setReadyToFlush();
        QJMTestUtil.writeOp(stm, 3);
        // The flush should log txn 1-2
        TestQuorumJournalManagerUnit.futureReturns(null).when(spyLoggers.get(0)).sendEdits(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(1L), ArgumentMatchers.eq(2), Mockito.<byte[]>any());
        TestQuorumJournalManagerUnit.futureReturns(null).when(spyLoggers.get(1)).sendEdits(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(1L), ArgumentMatchers.eq(2), Mockito.<byte[]>any());
        TestQuorumJournalManagerUnit.futureReturns(null).when(spyLoggers.get(2)).sendEdits(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(1L), ArgumentMatchers.eq(2), Mockito.<byte[]>any());
        stm.flush();
        // Another flush should now log txn #3
        stm.setReadyToFlush();
        TestQuorumJournalManagerUnit.futureReturns(null).when(spyLoggers.get(0)).sendEdits(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(3L), ArgumentMatchers.eq(1), Mockito.<byte[]>any());
        TestQuorumJournalManagerUnit.futureReturns(null).when(spyLoggers.get(1)).sendEdits(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(3L), ArgumentMatchers.eq(1), Mockito.<byte[]>any());
        TestQuorumJournalManagerUnit.futureReturns(null).when(spyLoggers.get(2)).sendEdits(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(3L), ArgumentMatchers.eq(1), Mockito.<byte[]>any());
        stm.flush();
    }

    @Test
    public void testWriteEditsOneSlow() throws Exception {
        EditLogOutputStream stm = createLogSegment();
        QJMTestUtil.writeOp(stm, 1);
        stm.setReadyToFlush();
        // Make the first two logs respond immediately
        TestQuorumJournalManagerUnit.futureReturns(null).when(spyLoggers.get(0)).sendEdits(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(1L), ArgumentMatchers.eq(1), Mockito.<byte[]>any());
        TestQuorumJournalManagerUnit.futureReturns(null).when(spyLoggers.get(1)).sendEdits(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(1L), ArgumentMatchers.eq(1), Mockito.<byte[]>any());
        // And the third log not respond
        SettableFuture<Void> slowLog = SettableFuture.create();
        Mockito.doReturn(slowLog).when(spyLoggers.get(2)).sendEdits(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(1L), ArgumentMatchers.eq(1), Mockito.<byte[]>any());
        stm.flush();
        Mockito.verify(spyLoggers.get(0)).setCommittedTxId(1L);
    }

    @Test
    public void testReadRpcInputStreams() throws Exception {
        for (int jn = 0; jn < 3; jn++) {
            TestQuorumJournalManagerUnit.futureReturns(getJournaledEditsReponse(1, 3)).when(spyLoggers.get(jn)).getJournaledEdits(1, QJM_RPC_MAX_TXNS_DEFAULT);
        }
        List<EditLogInputStream> streams = Lists.newArrayList();
        qjm.selectInputStreams(streams, 1, true, true);
        Assert.assertEquals(1, streams.size());
        QJMTestUtil.verifyEdits(streams, 1, 3);
    }

    @Test
    public void testReadRpcMismatchedInputStreams() throws Exception {
        for (int jn = 0; jn < 3; jn++) {
            TestQuorumJournalManagerUnit.futureReturns(getJournaledEditsReponse(1, (jn + 1))).when(spyLoggers.get(jn)).getJournaledEdits(1, QJM_RPC_MAX_TXNS_DEFAULT);
        }
        List<EditLogInputStream> streams = Lists.newArrayList();
        qjm.selectInputStreams(streams, 1, true, true);
        Assert.assertEquals(1, streams.size());
        QJMTestUtil.verifyEdits(streams, 1, 2);
    }

    @Test
    public void testReadRpcInputStreamsOneSlow() throws Exception {
        for (int jn = 0; jn < 2; jn++) {
            TestQuorumJournalManagerUnit.futureReturns(getJournaledEditsReponse(1, (jn + 1))).when(spyLoggers.get(jn)).getJournaledEdits(1, QJM_RPC_MAX_TXNS_DEFAULT);
        }
        Mockito.doReturn(SettableFuture.create()).when(spyLoggers.get(2)).getJournaledEdits(1, QJM_RPC_MAX_TXNS_DEFAULT);
        List<EditLogInputStream> streams = Lists.newArrayList();
        qjm.selectInputStreams(streams, 1, true, true);
        Assert.assertEquals(1, streams.size());
        QJMTestUtil.verifyEdits(streams, 1, 1);
    }

    @Test
    public void testReadRpcInputStreamsOneException() throws Exception {
        for (int jn = 0; jn < 2; jn++) {
            TestQuorumJournalManagerUnit.futureReturns(getJournaledEditsReponse(1, (jn + 1))).when(spyLoggers.get(jn)).getJournaledEdits(1, QJM_RPC_MAX_TXNS_DEFAULT);
        }
        TestQuorumJournalManagerUnit.futureThrows(new IOException()).when(spyLoggers.get(2)).getJournaledEdits(1, QJM_RPC_MAX_TXNS_DEFAULT);
        List<EditLogInputStream> streams = Lists.newArrayList();
        qjm.selectInputStreams(streams, 1, true, true);
        Assert.assertEquals(1, streams.size());
        QJMTestUtil.verifyEdits(streams, 1, 1);
    }

    @Test
    public void testReadRpcInputStreamsNoNewEdits() throws Exception {
        for (int jn = 0; jn < 3; jn++) {
            TestQuorumJournalManagerUnit.futureReturns(GetJournaledEditsResponseProto.newBuilder().setTxnCount(0).setEditLog(EMPTY).build()).when(spyLoggers.get(jn)).getJournaledEdits(1, QJM_RPC_MAX_TXNS_DEFAULT);
        }
        List<EditLogInputStream> streams = Lists.newArrayList();
        qjm.selectInputStreams(streams, 1, true, true);
        Assert.assertEquals(0, streams.size());
    }
}

