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
package com.twitter.distributedlog;


import DistributedLogConstants.UNASSIGNED_LOGSEGMENT_SEQNO;
import com.google.common.base.Charsets;
import com.twitter.distributedlog.exceptions.DLIllegalStateException;
import com.twitter.distributedlog.exceptions.UnexpectedException;
import com.twitter.distributedlog.namespace.DistributedLogNamespace;
import com.twitter.distributedlog.util.DLUtils;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestLogSegmentsZK extends TestDistributedLogBase {
    static Logger LOG = LoggerFactory.getLogger(TestLogSegmentsZK.class);

    @Rule
    public TestName testName = new TestName();

    /**
     * Create Log Segment for an pre-create stream. No max ledger sequence number recorded.
     */
    @Test(timeout = 60000)
    public void testCreateLogSegmentOnPrecreatedStream() throws Exception {
        URI uri = createURI();
        String streamName = testName.getMethodName();
        DistributedLogConfiguration conf = new DistributedLogConfiguration().setLockTimeout(99999).setOutputBufferSize(0).setImmediateFlushEnabled(true).setEnableLedgerAllocatorPool(true).setLedgerAllocatorPoolName("test");
        BKDistributedLogNamespace namespace = BKDistributedLogNamespace.newBuilder().conf(conf).uri(uri).build();
        namespace.createLog(streamName);
        MaxLogSegmentSequenceNo max1 = TestLogSegmentsZK.getMaxLogSegmentSequenceNo(namespace.getSharedWriterZKCForDL(), uri, streamName, conf);
        Assert.assertEquals(UNASSIGNED_LOGSEGMENT_SEQNO, max1.getSequenceNumber());
        DistributedLogManager dlm = namespace.openLog(streamName);
        final int numSegments = 3;
        for (int i = 0; i < numSegments; i++) {
            BKSyncLogWriter out = ((BKSyncLogWriter) (dlm.startLogSegmentNonPartitioned()));
            out.write(DLMTestUtil.getLogRecordInstance(i));
            out.closeAndComplete();
        }
        MaxLogSegmentSequenceNo max2 = TestLogSegmentsZK.getMaxLogSegmentSequenceNo(namespace.getSharedWriterZKCForDL(), uri, streamName, conf);
        Assert.assertEquals(3, max2.getSequenceNumber());
        dlm.close();
        namespace.close();
    }

    /**
     * Create Log Segment when no max sequence number recorded in /ledgers. e.g. old version.
     */
    @Test(timeout = 60000)
    public void testCreateLogSegmentMissingMaxSequenceNumber() throws Exception {
        URI uri = createURI();
        String streamName = testName.getMethodName();
        DistributedLogConfiguration conf = new DistributedLogConfiguration().setLockTimeout(99999).setOutputBufferSize(0).setImmediateFlushEnabled(true).setEnableLedgerAllocatorPool(true).setLedgerAllocatorPoolName("test");
        BKDistributedLogNamespace namespace = BKDistributedLogNamespace.newBuilder().conf(conf).uri(uri).build();
        namespace.createLog(streamName);
        MaxLogSegmentSequenceNo max1 = TestLogSegmentsZK.getMaxLogSegmentSequenceNo(namespace.getSharedWriterZKCForDL(), uri, streamName, conf);
        Assert.assertEquals(UNASSIGNED_LOGSEGMENT_SEQNO, max1.getSequenceNumber());
        DistributedLogManager dlm = namespace.openLog(streamName);
        final int numSegments = 3;
        for (int i = 0; i < numSegments; i++) {
            BKSyncLogWriter out = ((BKSyncLogWriter) (dlm.startLogSegmentNonPartitioned()));
            out.write(DLMTestUtil.getLogRecordInstance(i));
            out.closeAndComplete();
        }
        MaxLogSegmentSequenceNo max2 = TestLogSegmentsZK.getMaxLogSegmentSequenceNo(namespace.getSharedWriterZKCForDL(), uri, streamName, conf);
        Assert.assertEquals(3, max2.getSequenceNumber());
        // nuke the max ledger sequence number
        TestLogSegmentsZK.updateMaxLogSegmentSequenceNo(namespace.getSharedWriterZKCForDL(), uri, streamName, conf, new byte[0]);
        DistributedLogManager dlm1 = namespace.openLog(streamName);
        try {
            dlm1.startLogSegmentNonPartitioned();
            Assert.fail("Should fail with unexpected exceptions");
        } catch (UnexpectedException ue) {
            // expected
        } finally {
            dlm1.close();
        }
        // invalid max ledger sequence number
        TestLogSegmentsZK.updateMaxLogSegmentSequenceNo(namespace.getSharedWriterZKCForDL(), uri, streamName, conf, "invalid-max".getBytes(Charsets.UTF_8));
        DistributedLogManager dlm2 = namespace.openLog(streamName);
        try {
            dlm2.startLogSegmentNonPartitioned();
            Assert.fail("Should fail with unexpected exceptions");
        } catch (UnexpectedException ue) {
            // expected
        } finally {
            dlm2.close();
        }
        dlm.close();
        namespace.close();
    }

    /**
     * Create Log Segment while max sequence number isn't match with list of log segments.
     */
    @Test(timeout = 60000)
    public void testCreateLogSegmentUnmatchMaxSequenceNumber() throws Exception {
        URI uri = createURI();
        String streamName = testName.getMethodName();
        DistributedLogConfiguration conf = new DistributedLogConfiguration().setLockTimeout(99999).setOutputBufferSize(0).setImmediateFlushEnabled(true).setEnableLedgerAllocatorPool(true).setLedgerAllocatorPoolName("test");
        BKDistributedLogNamespace namespace = BKDistributedLogNamespace.newBuilder().conf(conf).uri(uri).build();
        namespace.createLog(streamName);
        MaxLogSegmentSequenceNo max1 = TestLogSegmentsZK.getMaxLogSegmentSequenceNo(namespace.getSharedWriterZKCForDL(), uri, streamName, conf);
        Assert.assertEquals(UNASSIGNED_LOGSEGMENT_SEQNO, max1.getSequenceNumber());
        DistributedLogManager dlm = namespace.openLog(streamName);
        final int numSegments = 3;
        for (int i = 0; i < numSegments; i++) {
            BKSyncLogWriter out = ((BKSyncLogWriter) (dlm.startLogSegmentNonPartitioned()));
            out.write(DLMTestUtil.getLogRecordInstance(i));
            out.closeAndComplete();
        }
        MaxLogSegmentSequenceNo max2 = TestLogSegmentsZK.getMaxLogSegmentSequenceNo(namespace.getSharedWriterZKCForDL(), uri, streamName, conf);
        Assert.assertEquals(3, max2.getSequenceNumber());
        // update the max ledger sequence number
        TestLogSegmentsZK.updateMaxLogSegmentSequenceNo(namespace.getSharedWriterZKCForDL(), uri, streamName, conf, DLUtils.serializeLogSegmentSequenceNumber(99));
        DistributedLogManager dlm1 = namespace.openLog(streamName);
        try {
            BKSyncLogWriter out1 = ((BKSyncLogWriter) (dlm1.startLogSegmentNonPartitioned()));
            out1.write(DLMTestUtil.getLogRecordInstance((numSegments + 1)));
            out1.closeAndComplete();
            Assert.fail("Should fail creating new log segment when encountered unmatch max ledger sequence number");
        } catch (DLIllegalStateException lse) {
            // expected
        } finally {
            dlm1.close();
        }
        DistributedLogManager dlm2 = namespace.openLog(streamName);
        List<LogSegmentMetadata> segments = dlm2.getLogSegments();
        try {
            Assert.assertEquals(3, segments.size());
            Assert.assertEquals(1L, segments.get(0).getLogSegmentSequenceNumber());
            Assert.assertEquals(2L, segments.get(1).getLogSegmentSequenceNumber());
            Assert.assertEquals(3L, segments.get(2).getLogSegmentSequenceNumber());
        } finally {
            dlm2.close();
        }
        dlm.close();
        namespace.close();
    }

    @Test(timeout = 60000)
    public void testCompleteLogSegmentConflicts() throws Exception {
        URI uri = createURI();
        String streamName = testName.getMethodName();
        DistributedLogConfiguration conf = new DistributedLogConfiguration().setLockTimeout(99999).setOutputBufferSize(0).setImmediateFlushEnabled(true).setEnableLedgerAllocatorPool(true).setLedgerAllocatorPoolName("test");
        DistributedLogNamespace namespace = com.twitter.distributedlog.namespace.DistributedLogNamespaceBuilder.newBuilder().conf(conf).uri(uri).build();
        namespace.createLog(streamName);
        DistributedLogManager dlm1 = namespace.openLog(streamName);
        DistributedLogManager dlm2 = namespace.openLog(streamName);
        // dlm1 is writing
        BKSyncLogWriter out1 = ((BKSyncLogWriter) (dlm1.startLogSegmentNonPartitioned()));
        out1.write(DLMTestUtil.getLogRecordInstance(1));
        // before out1 complete, out2 is in on recovery
        // it completed the log segments which bump the version of /ledgers znode
        BKAsyncLogWriter out2 = ((BKAsyncLogWriter) (dlm2.startAsyncLogSegmentNonPartitioned()));
        try {
            out1.closeAndComplete();
            Assert.fail("Should fail closeAndComplete since other people already completed it.");
        } catch (IOException ioe) {
        }
    }
}

