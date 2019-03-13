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


import CompressionCodec.Type.NONE;
import NullStatsLogger.INSTANCE;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.twitter.distributedlog.Entry.Reader;
import com.twitter.distributedlog.Entry.Writer;
import com.twitter.distributedlog.exceptions.LogRecordTooLongException;
import com.twitter.distributedlog.io.Buffer;
import com.twitter.util.Await;
import com.twitter.util.Future;
import java.nio.ByteBuffer;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test Case of {@link Entry}
 */
public class TestEntry {
    @Test(timeout = 20000)
    public void testEmptyRecordSet() throws Exception {
        Writer writer = Entry.newEntry("test-empty-record-set", 1024, true, NONE, INSTANCE);
        Assert.assertEquals("zero bytes", 0, writer.getNumBytes());
        Assert.assertEquals("zero records", 0, writer.getNumRecords());
        Buffer buffer = writer.getBuffer();
        Entry recordSet = Entry.newBuilder().setData(buffer.getData(), 0, buffer.size()).setLogSegmentInfo(1L, 0L).setEntryId(0L).build();
        Reader reader = recordSet.reader();
        Assert.assertNull("Empty record set should return null", reader.nextRecord());
    }

    @Test(timeout = 20000)
    public void testWriteTooLongRecord() throws Exception {
        Writer writer = Entry.newEntry("test-write-too-long-record", 1024, false, NONE, INSTANCE);
        Assert.assertEquals("zero bytes", 0, writer.getNumBytes());
        Assert.assertEquals("zero records", 0, writer.getNumRecords());
        LogRecord largeRecord = new LogRecord(1L, new byte[(LogRecord.MAX_LOGRECORD_SIZE) + 1]);
        try {
            writer.writeRecord(largeRecord, new com.twitter.util.Promise<DLSN>());
            Assert.fail("Should fail on writing large record");
        } catch (LogRecordTooLongException lrtle) {
            // expected
        }
        Assert.assertEquals("zero bytes", 0, writer.getNumBytes());
        Assert.assertEquals("zero records", 0, writer.getNumRecords());
        Buffer buffer = writer.getBuffer();
        Assert.assertEquals("zero bytes", 0, buffer.size());
    }

    @Test(timeout = 20000)
    public void testWriteRecords() throws Exception {
        Writer writer = Entry.newEntry("test-write-records", 1024, true, NONE, INSTANCE);
        Assert.assertEquals("zero bytes", 0, writer.getNumBytes());
        Assert.assertEquals("zero records", 0, writer.getNumRecords());
        List<Future<DLSN>> writePromiseList = Lists.newArrayList();
        // write first 5 records
        for (int i = 0; i < 5; i++) {
            LogRecord record = new LogRecord(i, ("record-" + i).getBytes(Charsets.UTF_8));
            record.setPositionWithinLogSegment(i);
            com.twitter.util.Promise<DLSN> writePromise = new com.twitter.util.Promise<DLSN>();
            writer.writeRecord(record, writePromise);
            writePromiseList.add(writePromise);
            Assert.assertEquals(((i + 1) + " records"), (i + 1), writer.getNumRecords());
        }
        // write large record
        LogRecord largeRecord = new LogRecord(1L, new byte[(LogRecord.MAX_LOGRECORD_SIZE) + 1]);
        try {
            writer.writeRecord(largeRecord, new com.twitter.util.Promise<DLSN>());
            Assert.fail("Should fail on writing large record");
        } catch (LogRecordTooLongException lrtle) {
            // expected
        }
        Assert.assertEquals("5 records", 5, writer.getNumRecords());
        // write another 5 records
        for (int i = 0; i < 5; i++) {
            LogRecord record = new LogRecord((i + 5), ("record-" + (i + 5)).getBytes(Charsets.UTF_8));
            record.setPositionWithinLogSegment((i + 5));
            com.twitter.util.Promise<DLSN> writePromise = new com.twitter.util.Promise<DLSN>();
            writer.writeRecord(record, writePromise);
            writePromiseList.add(writePromise);
            Assert.assertEquals(((i + 6) + " records"), (i + 6), writer.getNumRecords());
        }
        Buffer buffer = writer.getBuffer();
        // Test transmit complete
        writer.completeTransmit(1L, 1L);
        List<DLSN> writeResults = Await.result(Future.collect(writePromiseList));
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(new DLSN(1L, 1L, i), writeResults.get(i));
        }
        // Test reading from buffer
        Entry recordSet = Entry.newBuilder().setData(buffer.getData(), 0, buffer.size()).setLogSegmentInfo(1L, 1L).setEntryId(0L).build();
        Reader reader = recordSet.reader();
        LogRecordWithDLSN record = reader.nextRecord();
        int numReads = 0;
        long expectedTxid = 0L;
        while (null != record) {
            Assert.assertEquals(expectedTxid, record.getTransactionId());
            Assert.assertEquals(expectedTxid, record.getSequenceId());
            Assert.assertEquals(new DLSN(1L, 0L, expectedTxid), record.getDlsn());
            ++numReads;
            ++expectedTxid;
            record = reader.nextRecord();
        } 
        Assert.assertEquals(10, numReads);
    }

    @Test(timeout = 20000)
    public void testWriteRecordSet() throws Exception {
        Writer writer = Entry.newEntry("test-write-recordset", 1024, true, NONE, INSTANCE);
        Assert.assertEquals("zero bytes", 0, writer.getNumBytes());
        Assert.assertEquals("zero records", 0, writer.getNumRecords());
        List<Future<DLSN>> writePromiseList = Lists.newArrayList();
        // write first 5 records
        for (int i = 0; i < 5; i++) {
            LogRecord record = new LogRecord(i, ("record-" + i).getBytes(Charsets.UTF_8));
            record.setPositionWithinLogSegment(i);
            com.twitter.util.Promise<DLSN> writePromise = new com.twitter.util.Promise<DLSN>();
            writer.writeRecord(record, writePromise);
            writePromiseList.add(writePromise);
            Assert.assertEquals(((i + 1) + " records"), (i + 1), writer.getNumRecords());
        }
        final LogRecordSet.Writer recordSetWriter = LogRecordSet.newWriter(1024, NONE);
        List<Future<DLSN>> recordSetPromiseList = Lists.newArrayList();
        // write another 5 records as a batch
        for (int i = 0; i < 5; i++) {
            ByteBuffer record = ByteBuffer.wrap(("record-" + (i + 5)).getBytes(Charsets.UTF_8));
            com.twitter.util.Promise<DLSN> writePromise = new com.twitter.util.Promise<DLSN>();
            recordSetWriter.writeRecord(record, writePromise);
            recordSetPromiseList.add(writePromise);
            Assert.assertEquals(((i + 1) + " records"), (i + 1), recordSetWriter.getNumRecords());
        }
        final ByteBuffer recordSetBuffer = recordSetWriter.getBuffer();
        byte[] data = new byte[recordSetBuffer.remaining()];
        recordSetBuffer.get(data);
        LogRecord setRecord = new LogRecord(5L, data);
        setRecord.setPositionWithinLogSegment(5);
        setRecord.setRecordSet();
        com.twitter.util.Promise<DLSN> writePromise = new com.twitter.util.Promise<DLSN>();
        writePromise.addEventListener(new com.twitter.util.FutureEventListener<DLSN>() {
            @Override
            public void onSuccess(DLSN dlsn) {
                recordSetWriter.completeTransmit(dlsn.getLogSegmentSequenceNo(), dlsn.getEntryId(), dlsn.getSlotId());
            }

            @Override
            public void onFailure(Throwable cause) {
                recordSetWriter.abortTransmit(cause);
            }
        });
        writer.writeRecord(setRecord, writePromise);
        writePromiseList.add(writePromise);
        // write last 5 records
        for (int i = 0; i < 5; i++) {
            LogRecord record = new LogRecord((i + 10), ("record-" + (i + 10)).getBytes(Charsets.UTF_8));
            record.setPositionWithinLogSegment((i + 10));
            writePromise = new com.twitter.util.Promise<DLSN>();
            writer.writeRecord(record, writePromise);
            writePromiseList.add(writePromise);
            Assert.assertEquals(((i + 11) + " records"), (i + 11), writer.getNumRecords());
        }
        Buffer buffer = writer.getBuffer();
        // Test transmit complete
        writer.completeTransmit(1L, 1L);
        List<DLSN> writeResults = Await.result(Future.collect(writePromiseList));
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals(new DLSN(1L, 1L, i), writeResults.get(i));
        }
        Assert.assertEquals(new DLSN(1L, 1L, 5), writeResults.get(5));
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals(new DLSN(1L, 1L, (10 + i)), writeResults.get((6 + i)));
        }
        List<DLSN> recordSetWriteResults = Await.result(Future.collect(recordSetPromiseList));
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals(new DLSN(1L, 1L, (5 + i)), recordSetWriteResults.get(i));
        }
        // Test reading from buffer
        verifyReadResult(buffer, 1L, 1L, 1L, true, new DLSN(1L, 1L, 2L), 3, 5, 5, new DLSN(1L, 1L, 2L), 2L);
        verifyReadResult(buffer, 1L, 1L, 1L, true, new DLSN(1L, 1L, 7L), 0, 3, 5, new DLSN(1L, 1L, 7L), 7L);
        verifyReadResult(buffer, 1L, 1L, 1L, true, new DLSN(1L, 1L, 12L), 0, 0, 3, new DLSN(1L, 1L, 12L), 12L);
        verifyReadResult(buffer, 1L, 1L, 1L, false, new DLSN(1L, 1L, 2L), 3, 5, 5, new DLSN(1L, 1L, 2L), 2L);
        verifyReadResult(buffer, 1L, 1L, 1L, false, new DLSN(1L, 1L, 7L), 0, 3, 5, new DLSN(1L, 1L, 7L), 7L);
        verifyReadResult(buffer, 1L, 1L, 1L, false, new DLSN(1L, 1L, 12L), 0, 0, 3, new DLSN(1L, 1L, 12L), 12L);
    }
}

