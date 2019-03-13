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


import DLSN.InitialDLSN;
import LogSegmentMetadataVersion.VERSION_V4_ENVELOPED_ENTRIES.value;
import com.twitter.util.Await;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test Cases related to sequence ids.
 */
public class TestSequenceID extends TestDistributedLogBase {
    static final Logger logger = LoggerFactory.getLogger(TestSequenceID.class);

    @Test(timeout = 60000)
    public void testCompleteV4LogSegmentAsV4() throws Exception {
        completeSingleInprogressSegment(value, value);
    }

    @Test(timeout = 60000)
    public void testCompleteV4LogSegmentAsV5() throws Exception {
        completeSingleInprogressSegment(value, LogSegmentMetadataVersion.VERSION_V5_SEQUENCE_ID.value);
    }

    @Test(timeout = 60000)
    public void testCompleteV5LogSegmentAsV4() throws Exception {
        completeSingleInprogressSegment(LogSegmentMetadataVersion.VERSION_V5_SEQUENCE_ID.value, value);
    }

    @Test(timeout = 60000)
    public void testCompleteV5LogSegmentAsV5() throws Exception {
        completeSingleInprogressSegment(LogSegmentMetadataVersion.VERSION_V5_SEQUENCE_ID.value, LogSegmentMetadataVersion.VERSION_V5_SEQUENCE_ID.value);
    }

    @Test(timeout = 60000)
    public void testSequenceID() throws Exception {
        DistributedLogConfiguration confLocalv4 = new DistributedLogConfiguration();
        confLocalv4.addConfiguration(TestDistributedLogBase.conf);
        confLocalv4.setImmediateFlushEnabled(true);
        confLocalv4.setOutputBufferSize(0);
        confLocalv4.setDLLedgerMetadataLayoutVersion(value);
        String name = "distrlog-sequence-id";
        BKDistributedLogManager readDLM = ((BKDistributedLogManager) (createNewDLM(TestDistributedLogBase.conf, name)));
        AsyncLogReader reader = null;
        final LinkedBlockingQueue<LogRecordWithDLSN> readRecords = new LinkedBlockingQueue<LogRecordWithDLSN>();
        BKDistributedLogManager dlm = ((BKDistributedLogManager) (createNewDLM(confLocalv4, name)));
        long txId = 0L;
        for (int i = 0; i < 3; i++) {
            BKAsyncLogWriter writer = dlm.startAsyncLogSegmentNonPartitioned();
            for (int j = 0; j < 2; j++) {
                Await.result(writer.write(DLMTestUtil.getLogRecordInstance((txId++))));
                if (null == reader) {
                    reader = readDLM.getAsyncLogReader(InitialDLSN);
                    final AsyncLogReader r = reader;
                    reader.readNext().addEventListener(new com.twitter.util.FutureEventListener<LogRecordWithDLSN>() {
                        @Override
                        public void onSuccess(LogRecordWithDLSN record) {
                            readRecords.add(record);
                            r.readNext().addEventListener(this);
                        }

                        @Override
                        public void onFailure(Throwable cause) {
                            TestSequenceID.logger.error("Encountered exception on reading next : ", cause);
                        }
                    });
                }
            }
            writer.closeAndComplete();
        }
        BKAsyncLogWriter writer = dlm.startAsyncLogSegmentNonPartitioned();
        Await.result(writer.write(DLMTestUtil.getLogRecordInstance((txId++))));
        List<LogSegmentMetadata> segments = dlm.getLogSegments();
        Assert.assertEquals(4, segments.size());
        for (int i = 0; i < 3; i++) {
            Assert.assertFalse(segments.get(i).isInProgress());
            Assert.assertTrue(((segments.get(i).getStartSequenceId()) < 0));
        }
        Assert.assertTrue(segments.get(3).isInProgress());
        Assert.assertTrue(((segments.get(3).getStartSequenceId()) < 0));
        dlm.close();
        // simulate upgrading from v4 -> v5
        DistributedLogConfiguration confLocalv5 = new DistributedLogConfiguration();
        confLocalv5.addConfiguration(TestDistributedLogBase.conf);
        confLocalv5.setImmediateFlushEnabled(true);
        confLocalv5.setOutputBufferSize(0);
        confLocalv5.setDLLedgerMetadataLayoutVersion(LogSegmentMetadataVersion.VERSION_V5_SEQUENCE_ID.value);
        BKDistributedLogManager dlmv5 = ((BKDistributedLogManager) (createNewDLM(confLocalv5, name)));
        for (int i = 0; i < 3; i++) {
            BKAsyncLogWriter writerv5 = dlmv5.startAsyncLogSegmentNonPartitioned();
            for (int j = 0; j < 2; j++) {
                Await.result(writerv5.write(DLMTestUtil.getLogRecordInstance((txId++))));
            }
            writerv5.closeAndComplete();
        }
        BKAsyncLogWriter writerv5 = dlmv5.startAsyncLogSegmentNonPartitioned();
        Await.result(writerv5.write(DLMTestUtil.getLogRecordInstance((txId++))));
        List<LogSegmentMetadata> segmentsv5 = dlmv5.getLogSegments();
        Assert.assertEquals(8, segmentsv5.size());
        Assert.assertFalse(segmentsv5.get(3).isInProgress());
        Assert.assertTrue(((segmentsv5.get(3).getStartSequenceId()) < 0));
        long startSequenceId = 0L;
        for (int i = 4; i < 7; i++) {
            Assert.assertFalse(segmentsv5.get(i).isInProgress());
            Assert.assertEquals(startSequenceId, segmentsv5.get(i).getStartSequenceId());
            startSequenceId += 2L;
        }
        Assert.assertTrue(segmentsv5.get(7).isInProgress());
        Assert.assertEquals(startSequenceId, segmentsv5.get(7).getStartSequenceId());
        dlmv5.close();
        // rollback from v5 to v4
        BKDistributedLogManager dlmv4 = ((BKDistributedLogManager) (createNewDLM(confLocalv4, name)));
        for (int i = 0; i < 3; i++) {
            BKAsyncLogWriter writerv4 = dlmv4.startAsyncLogSegmentNonPartitioned();
            for (int j = 0; j < 2; j++) {
                Await.result(writerv4.write(DLMTestUtil.getLogRecordInstance((txId++))));
            }
            writerv4.closeAndComplete();
        }
        List<LogSegmentMetadata> segmentsv4 = dlmv4.getLogSegments();
        Assert.assertEquals(11, segmentsv4.size());
        for (int i = 7; i < 11; i++) {
            Assert.assertFalse(segmentsv4.get(i).isInProgress());
            Assert.assertTrue(((segmentsv4.get(i).getStartSequenceId()) < 0));
        }
        dlmv4.close();
        // wait until readers read all records
        while ((readRecords.size()) < txId) {
            Thread.sleep(100);
        } 
        Assert.assertEquals(txId, readRecords.size());
        long sequenceId = Long.MIN_VALUE;
        for (LogRecordWithDLSN record : readRecords) {
            if ((record.getDlsn().getLogSegmentSequenceNo()) <= 4) {
                Assert.assertTrue(((record.getSequenceId()) < 0));
                Assert.assertTrue(((record.getSequenceId()) > sequenceId));
                sequenceId = record.getSequenceId();
            } else
                if ((record.getDlsn().getLogSegmentSequenceNo()) <= 7) {
                    if (sequenceId < 0L) {
                        sequenceId = 0L;
                    }
                    Assert.assertEquals(sequenceId, record.getSequenceId());
                    ++sequenceId;
                } else
                    if ((record.getDlsn().getLogSegmentSequenceNo()) >= 9) {
                        if (sequenceId > 0) {
                            sequenceId = Long.MIN_VALUE;
                        }
                        Assert.assertTrue(((record.getSequenceId()) < 0));
                        Assert.assertTrue(((record.getSequenceId()) > sequenceId));
                        sequenceId = record.getSequenceId();
                    }


        }
        readDLM.close();
    }
}

