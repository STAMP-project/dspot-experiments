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


import BKException.Code.InterruptedException;
import BKException.Code.LedgerFencedException;
import DLSN.InvalidDLSN;
import LedgerHandle.INVALID_ENTRY_ID;
import com.twitter.distributedlog.exceptions.BKTransmitException;
import com.twitter.distributedlog.exceptions.EndOfStreamException;
import com.twitter.distributedlog.exceptions.WriteCancelledException;
import com.twitter.distributedlog.exceptions.WriteException;
import com.twitter.distributedlog.impl.BKLogSegmentEntryWriter;
import com.twitter.distributedlog.lock.ZKDistributedLock;
import com.twitter.distributedlog.util.FutureUtils;
import com.twitter.distributedlog.util.OrderedScheduler;
import com.twitter.util.Await;
import com.twitter.util.Future;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.apache.bookkeeper.client.LedgerHandle;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import static DLSN.InvalidDLSN;


/**
 * Test Case for BookKeeper Based Log Segment Writer
 */
public class TestBKLogSegmentWriter extends TestDistributedLogBase {
    @Rule
    public TestName runtime = new TestName();

    private OrderedScheduler scheduler;

    private OrderedScheduler lockStateExecutor;

    private ZooKeeperClient zkc;

    private ZooKeeperClient zkc0;

    private BookKeeperClient bkc;

    /**
     * Close a segment log writer should flush buffered data.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testCloseShouldFlush() throws Exception {
        DistributedLogConfiguration confLocal = newLocalConf();
        confLocal.setImmediateFlushEnabled(false);
        confLocal.setOutputBufferSize(Integer.MAX_VALUE);
        confLocal.setPeriodicFlushFrequencyMilliSeconds(0);
        ZKDistributedLock lock = createLock(("/test/lock-" + (runtime.getMethodName())), zkc, true);
        BKLogSegmentWriter writer = createLogSegmentWriter(confLocal, 0L, (-1L), lock);
        // Use another lock to wait for writer releasing lock
        ZKDistributedLock lock0 = createLock(("/test/lock-" + (runtime.getMethodName())), zkc0, false);
        Future<ZKDistributedLock> lockFuture0 = lock0.asyncAcquire();
        // add 10 records
        int numRecords = 10;
        List<Future<DLSN>> futureList = new ArrayList<Future<DLSN>>(numRecords);
        for (int i = 0; i < numRecords; i++) {
            futureList.add(writer.asyncWrite(DLMTestUtil.getLogRecordInstance(i)));
        }
        Assert.assertEquals(("Last tx id should be " + (numRecords - 1)), (numRecords - 1), writer.getLastTxId());
        Assert.assertEquals("Last acked tx id should be -1", (-1L), writer.getLastTxIdAcknowledged());
        Assert.assertEquals(("Last DLSN should be " + (InvalidDLSN)), InvalidDLSN, writer.getLastDLSN());
        Assert.assertEquals(("Position should be " + numRecords), 10, writer.getPositionWithinLogSegment());
        // close the writer should flush buffered data and release lock
        closeWriterAndLock(writer, lock);
        Await.result(lockFuture0);
        lock0.checkOwnership();
        Assert.assertEquals(("Last tx id should still be " + (numRecords - 1)), (numRecords - 1), writer.getLastTxId());
        Assert.assertEquals(("Last acked tx id should become " + (numRecords - 1)), (numRecords - 1), writer.getLastTxIdAcknowledged());
        Assert.assertEquals(("Position should still be " + numRecords), 10, writer.getPositionWithinLogSegment());
        List<DLSN> dlsns = Await.result(Future.collect(futureList));
        Assert.assertEquals("All records should be written", numRecords, dlsns.size());
        for (int i = 0; i < numRecords; i++) {
            DLSN dlsn = dlsns.get(i);
            Assert.assertEquals("Incorrent ledger sequence number", 0L, dlsn.getLogSegmentSequenceNo());
            Assert.assertEquals("Incorrent entry id", 0L, dlsn.getEntryId());
            Assert.assertEquals("Inconsistent slot id", i, dlsn.getSlotId());
        }
        Assert.assertEquals(("Last DLSN should be " + (dlsns.get(((dlsns.size()) - 1)))), dlsns.get(((dlsns.size()) - 1)), writer.getLastDLSN());
        LedgerHandle lh = getLedgerHandle(writer);
        LedgerHandle readLh = openLedgerNoRecovery(lh);
        Assert.assertTrue((("Ledger " + (lh.getId())) + " should be closed"), readLh.isClosed());
        Assert.assertEquals(("There should be two entries in ledger " + (lh.getId())), 1L, readLh.getLastAddConfirmed());
    }

    /**
     * Abort a segment log writer should just abort pending writes and not flush buffered data.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testAbortShouldNotFlush() throws Exception {
        DistributedLogConfiguration confLocal = newLocalConf();
        confLocal.setImmediateFlushEnabled(false);
        confLocal.setOutputBufferSize(Integer.MAX_VALUE);
        confLocal.setPeriodicFlushFrequencyMilliSeconds(0);
        ZKDistributedLock lock = createLock(("/test/lock-" + (runtime.getMethodName())), zkc, true);
        BKLogSegmentWriter writer = createLogSegmentWriter(confLocal, 0L, (-1L), lock);
        // Use another lock to wait for writer releasing lock
        ZKDistributedLock lock0 = createLock(("/test/lock-" + (runtime.getMethodName())), zkc0, false);
        Future<ZKDistributedLock> lockFuture0 = lock0.asyncAcquire();
        // add 10 records
        int numRecords = 10;
        List<Future<DLSN>> futureList = new ArrayList<Future<DLSN>>(numRecords);
        for (int i = 0; i < numRecords; i++) {
            futureList.add(writer.asyncWrite(DLMTestUtil.getLogRecordInstance(i)));
        }
        Assert.assertEquals(("Last tx id should be " + (numRecords - 1)), (numRecords - 1), writer.getLastTxId());
        Assert.assertEquals("Last acked tx id should be -1", (-1L), writer.getLastTxIdAcknowledged());
        Assert.assertEquals(("Last DLSN should be " + (InvalidDLSN)), InvalidDLSN, writer.getLastDLSN());
        Assert.assertEquals(("Position should be " + numRecords), 10, writer.getPositionWithinLogSegment());
        // close the writer should flush buffered data and release lock
        abortWriterAndLock(writer, lock);
        Await.result(lockFuture0);
        lock0.checkOwnership();
        Assert.assertEquals(("Last tx id should still be " + (numRecords - 1)), (numRecords - 1), writer.getLastTxId());
        Assert.assertEquals(("Last acked tx id should still be " + (numRecords - 1)), (-1L), writer.getLastTxIdAcknowledged());
        Assert.assertEquals(("Last DLSN should still be " + (InvalidDLSN)), InvalidDLSN, writer.getLastDLSN());
        Assert.assertEquals(("Position should still be " + numRecords), 10, writer.getPositionWithinLogSegment());
        for (int i = 0; i < numRecords; i++) {
            try {
                Await.result(futureList.get(i));
                Assert.fail((("Should be aborted record " + i) + " with transmit exception"));
            } catch (WriteCancelledException wce) {
                Assert.assertTrue((("Record " + i) + " should be aborted because of ledger fenced"), ((wce.getCause()) instanceof BKTransmitException));
                BKTransmitException bkte = ((BKTransmitException) (wce.getCause()));
                Assert.assertEquals((("Record " + i) + " should be aborted"), InterruptedException, bkte.getBKResultCode());
            }
        }
        // check no entries were written
        LedgerHandle lh = getLedgerHandle(writer);
        LedgerHandle readLh = openLedgerNoRecovery(lh);
        Assert.assertTrue((("Ledger " + (lh.getId())) + " should not be closed"), readLh.isClosed());
        Assert.assertEquals(("There should be no entries in ledger " + (lh.getId())), INVALID_ENTRY_ID, readLh.getLastAddConfirmed());
    }

    /**
     * Close a log segment writer that already detect ledger fenced, should not flush buffered data.
     * And should throw exception on closing.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testCloseShouldNotFlushIfLedgerFenced() throws Exception {
        testCloseShouldNotFlushIfInErrorState(LedgerFencedException);
    }

    /**
     * Close the writer when ledger is fenced: it should release the lock, fail on flushing data and throw exception
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testCloseShouldFailIfLedgerFenced() throws Exception {
        DistributedLogConfiguration confLocal = newLocalConf();
        confLocal.setImmediateFlushEnabled(false);
        confLocal.setOutputBufferSize(Integer.MAX_VALUE);
        confLocal.setPeriodicFlushFrequencyMilliSeconds(0);
        ZKDistributedLock lock = createLock(("/test/lock-" + (runtime.getMethodName())), zkc, true);
        BKLogSegmentWriter writer = createLogSegmentWriter(confLocal, 0L, (-1L), lock);
        // Use another lock to wait for writer releasing lock
        ZKDistributedLock lock0 = createLock(("/test/lock-" + (runtime.getMethodName())), zkc0, false);
        Future<ZKDistributedLock> lockFuture0 = lock0.asyncAcquire();
        // add 10 records
        int numRecords = 10;
        List<Future<DLSN>> futureList = new ArrayList<Future<DLSN>>(numRecords);
        for (int i = 0; i < numRecords; i++) {
            futureList.add(writer.asyncWrite(DLMTestUtil.getLogRecordInstance(i)));
        }
        Assert.assertEquals(("Last tx id should be " + (numRecords - 1)), (numRecords - 1), writer.getLastTxId());
        Assert.assertEquals("Last acked tx id should be -1", (-1L), writer.getLastTxIdAcknowledged());
        Assert.assertEquals(("Last DLSN should be " + (InvalidDLSN)), InvalidDLSN, writer.getLastDLSN());
        Assert.assertEquals(("Position should be " + numRecords), 10, writer.getPositionWithinLogSegment());
        // fence the ledger
        fenceLedger(getLedgerHandle(writer));
        // close the writer: it should release the lock, fail on flushing data and throw exception
        try {
            closeWriterAndLock(writer, lock);
            Assert.fail("Close a log segment writer when ledger is fenced should throw exception");
        } catch (BKTransmitException bkte) {
            Assert.assertEquals("Inconsistent rc is thrown", LedgerFencedException, bkte.getBKResultCode());
        }
        Await.result(lockFuture0);
        lock0.checkOwnership();
        Assert.assertEquals(("Last tx id should still be " + (numRecords - 1)), (numRecords - 1), writer.getLastTxId());
        Assert.assertEquals(("Last acked tx id should still be " + (numRecords - 1)), (-1L), writer.getLastTxIdAcknowledged());
        Assert.assertEquals(("Last DLSN should still be " + (InvalidDLSN)), InvalidDLSN, writer.getLastDLSN());
        Assert.assertEquals(("Position should still be " + numRecords), 10, writer.getPositionWithinLogSegment());
        for (int i = 0; i < numRecords; i++) {
            try {
                Await.result(futureList.get(i));
                Assert.fail((("Should be aborted record " + i) + " with transmit exception"));
            } catch (BKTransmitException bkte) {
                Assert.assertEquals((("Record " + i) + " should be aborted"), LedgerFencedException, bkte.getBKResultCode());
            }
        }
        // check no entries were written
        LedgerHandle lh = getLedgerHandle(writer);
        LedgerHandle readLh = openLedgerNoRecovery(lh);
        Assert.assertTrue((("Ledger " + (lh.getId())) + " should be closed"), readLh.isClosed());
        Assert.assertEquals(("There should be no entries in ledger " + (lh.getId())), INVALID_ENTRY_ID, readLh.getLastAddConfirmed());
    }

    /**
     * Abort should wait for outstanding transmits to be completed and cancel buffered data.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testAbortShouldFailAllWrites() throws Exception {
        DistributedLogConfiguration confLocal = newLocalConf();
        confLocal.setImmediateFlushEnabled(false);
        confLocal.setOutputBufferSize(Integer.MAX_VALUE);
        confLocal.setPeriodicFlushFrequencyMilliSeconds(0);
        ZKDistributedLock lock = createLock(("/test/lock-" + (runtime.getMethodName())), zkc, true);
        BKLogSegmentWriter writer = createLogSegmentWriter(confLocal, 0L, (-1L), lock);
        // Use another lock to wait for writer releasing lock
        ZKDistributedLock lock0 = createLock(("/test/lock-" + (runtime.getMethodName())), zkc0, false);
        Future<ZKDistributedLock> lockFuture0 = lock0.asyncAcquire();
        // add 10 records
        int numRecords = 10;
        List<Future<DLSN>> futureList = new ArrayList<Future<DLSN>>(numRecords);
        for (int i = 0; i < numRecords; i++) {
            futureList.add(writer.asyncWrite(DLMTestUtil.getLogRecordInstance(i)));
        }
        Assert.assertEquals(("Last tx id should be " + (numRecords - 1)), (numRecords - 1), writer.getLastTxId());
        Assert.assertEquals("Last acked tx id should be -1", (-1L), writer.getLastTxIdAcknowledged());
        Assert.assertEquals(("Last DLSN should be " + (InvalidDLSN)), InvalidDLSN, writer.getLastDLSN());
        Assert.assertEquals(("Position should be " + numRecords), numRecords, writer.getPositionWithinLogSegment());
        final CountDownLatch deferLatch = new CountDownLatch(1);
        writer.getFuturePool().apply(new scala.runtime.AbstractFunction0<Object>() {
            @Override
            public Object apply() {
                try {
                    deferLatch.await();
                } catch (InterruptedException e) {
                    TestDistributedLogBase.LOG.warn("Interrupted on deferring completion : ", e);
                }
                return null;
            }
        });
        // transmit the buffered data
        FutureUtils.result(writer.flush());
        // add another 10 records
        List<Future<DLSN>> anotherFutureList = new ArrayList<Future<DLSN>>(numRecords);
        for (int i = numRecords; i < (2 * numRecords); i++) {
            anotherFutureList.add(writer.asyncWrite(DLMTestUtil.getLogRecordInstance(i)));
        }
        Assert.assertEquals(("Last tx id should become " + ((2 * numRecords) - 1)), ((2 * numRecords) - 1), writer.getLastTxId());
        Assert.assertEquals(("Last acked tx id should become " + (numRecords - 1)), ((long) (numRecords - 1)), writer.getLastTxIdAcknowledged());
        Assert.assertEquals(("Last DLSN should still be " + (InvalidDLSN)), InvalidDLSN, writer.getLastDLSN());
        Assert.assertEquals(("Position should become " + (2 * numRecords)), (2 * numRecords), writer.getPositionWithinLogSegment());
        // abort the writer: it waits for outstanding transmits and abort buffered data
        abortWriterAndLock(writer, lock);
        Await.result(lockFuture0);
        lock0.checkOwnership();
        // release defer latch so completion would go through
        deferLatch.countDown();
        List<DLSN> dlsns = Await.result(Future.collect(futureList));
        Assert.assertEquals("All first 10 records should be written", numRecords, dlsns.size());
        for (int i = 0; i < numRecords; i++) {
            DLSN dlsn = dlsns.get(i);
            Assert.assertEquals("Incorrent ledger sequence number", 0L, dlsn.getLogSegmentSequenceNo());
            Assert.assertEquals("Incorrent entry id", 0L, dlsn.getEntryId());
            Assert.assertEquals("Inconsistent slot id", i, dlsn.getSlotId());
        }
        for (int i = 0; i < numRecords; i++) {
            try {
                Await.result(anotherFutureList.get(i));
                Assert.fail((("Should be aborted record " + (numRecords + i)) + " with transmit exception"));
            } catch (WriteCancelledException wce) {
                // writes should be cancelled.
            }
        }
        Assert.assertEquals(("Last tx id should still be " + ((2 * numRecords) - 1)), ((2 * numRecords) - 1), writer.getLastTxId());
        Assert.assertEquals(("Last acked tx id should be still " + (numRecords - 1)), ((long) (numRecords - 1)), writer.getLastTxIdAcknowledged());
        Assert.assertEquals(("Last DLSN should become " + (futureList.get(((futureList.size()) - 1)))), dlsns.get(((futureList.size()) - 1)), writer.getLastDLSN());
        Assert.assertEquals(("Position should become " + (2 * numRecords)), (2 * numRecords), writer.getPositionWithinLogSegment());
        // check only 1 entry were written
        LedgerHandle lh = getLedgerHandle(writer);
        LedgerHandle readLh = openLedgerNoRecovery(lh);
        Assert.assertTrue((("Ledger " + (lh.getId())) + " should not be closed"), readLh.isClosed());
        Assert.assertEquals(("Only one entry is written for ledger " + (lh.getId())), 0L, lh.getLastAddPushed());
        Assert.assertEquals(("Only one entry is written for ledger " + (lh.getId())), 0L, readLh.getLastAddConfirmed());
    }

    /**
     * Log Segment Writer should only update last tx id only for user records.
     */
    @Test(timeout = 60000)
    public void testUpdateLastTxIdForUserRecords() throws Exception {
        DistributedLogConfiguration confLocal = newLocalConf();
        confLocal.setImmediateFlushEnabled(false);
        confLocal.setOutputBufferSize(Integer.MAX_VALUE);
        confLocal.setPeriodicFlushFrequencyMilliSeconds(0);
        ZKDistributedLock lock = createLock(("/test/lock-" + (runtime.getMethodName())), zkc, true);
        BKLogSegmentWriter writer = createLogSegmentWriter(confLocal, 0L, (-1L), lock);
        // add 10 records
        int numRecords = 10;
        List<Future<DLSN>> futureList = new ArrayList<Future<DLSN>>(numRecords);
        for (int i = 0; i < numRecords; i++) {
            futureList.add(writer.asyncWrite(DLMTestUtil.getLogRecordInstance(i)));
        }
        LogRecord controlRecord = DLMTestUtil.getLogRecordInstance(9999L);
        controlRecord.setControl();
        futureList.add(writer.asyncWrite(controlRecord));
        Assert.assertEquals(("Last tx id should be " + (numRecords - 1)), (numRecords - 1), writer.getLastTxId());
        Assert.assertEquals(("Last DLSN should be " + (InvalidDLSN)), InvalidDLSN, writer.getLastDLSN());
        Assert.assertEquals(("Position should be " + numRecords), numRecords, writer.getPositionWithinLogSegment());
        // close the writer to flush the output buffer
        closeWriterAndLock(writer, lock);
        List<DLSN> dlsns = Await.result(Future.collect(futureList));
        Assert.assertEquals("All 11 records should be written", (numRecords + 1), dlsns.size());
        for (int i = 0; i < numRecords; i++) {
            DLSN dlsn = dlsns.get(i);
            Assert.assertEquals("Incorrent ledger sequence number", 0L, dlsn.getLogSegmentSequenceNo());
            Assert.assertEquals("Incorrent entry id", 0L, dlsn.getEntryId());
            Assert.assertEquals("Inconsistent slot id", i, dlsn.getSlotId());
        }
        DLSN dlsn = dlsns.get(numRecords);
        Assert.assertEquals("Incorrent ledger sequence number", 0L, dlsn.getLogSegmentSequenceNo());
        Assert.assertEquals("Incorrent entry id", 1L, dlsn.getEntryId());
        Assert.assertEquals("Inconsistent slot id", 0L, dlsn.getSlotId());
        Assert.assertEquals(("Last tx id should be " + (numRecords - 1)), (numRecords - 1), writer.getLastTxId());
        Assert.assertEquals(("Last acked tx id should be " + (numRecords - 1)), (numRecords - 1), writer.getLastTxIdAcknowledged());
        Assert.assertEquals(("Position should be " + numRecords), numRecords, writer.getPositionWithinLogSegment());
        Assert.assertEquals(("Last DLSN should be " + dlsn), dlsns.get((numRecords - 1)), writer.getLastDLSN());
    }

    /**
     * Non durable write should fail if writer is closed.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testNondurableWriteAfterWriterIsClosed() throws Exception {
        DistributedLogConfiguration confLocal = newLocalConf();
        confLocal.setImmediateFlushEnabled(false);
        confLocal.setOutputBufferSize(Integer.MAX_VALUE);
        confLocal.setPeriodicFlushFrequencyMilliSeconds(0);
        confLocal.setDurableWriteEnabled(false);
        ZKDistributedLock lock = createLock(("/test/lock-" + (runtime.getMethodName())), zkc, true);
        BKLogSegmentWriter writer = createLogSegmentWriter(confLocal, 0L, (-1L), lock);
        // close the writer
        closeWriterAndLock(writer, lock);
        FutureUtils.result(writer.asyncClose());
        try {
            Await.result(writer.asyncWrite(DLMTestUtil.getLogRecordInstance(1)));
            Assert.fail("Should fail the write if the writer is closed");
        } catch (WriteException we) {
            // expected
        }
    }

    /**
     * Non durable write should fail if writer is marked as end of stream.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testNondurableWriteAfterEndOfStream() throws Exception {
        DistributedLogConfiguration confLocal = newLocalConf();
        confLocal.setImmediateFlushEnabled(false);
        confLocal.setOutputBufferSize(Integer.MAX_VALUE);
        confLocal.setPeriodicFlushFrequencyMilliSeconds(0);
        confLocal.setDurableWriteEnabled(false);
        ZKDistributedLock lock = createLock(("/test/lock-" + (runtime.getMethodName())), zkc, true);
        BKLogSegmentWriter writer = createLogSegmentWriter(confLocal, 0L, (-1L), lock);
        FutureUtils.result(writer.markEndOfStream());
        try {
            Await.result(writer.asyncWrite(DLMTestUtil.getLogRecordInstance(1)));
            Assert.fail("Should fail the write if the writer is marked as end of stream");
        } catch (EndOfStreamException we) {
            // expected
        }
        closeWriterAndLock(writer, lock);
    }

    /**
     * Non durable write should fail if the log segment is fenced.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testNondurableWriteAfterLedgerIsFenced() throws Exception {
        DistributedLogConfiguration confLocal = newLocalConf();
        confLocal.setImmediateFlushEnabled(false);
        confLocal.setOutputBufferSize(Integer.MAX_VALUE);
        confLocal.setPeriodicFlushFrequencyMilliSeconds(0);
        confLocal.setDurableWriteEnabled(false);
        ZKDistributedLock lock = createLock(("/test/lock-" + (runtime.getMethodName())), zkc, true);
        BKLogSegmentWriter writer = createLogSegmentWriter(confLocal, 0L, (-1L), lock);
        // fence the ledger
        fenceLedger(getLedgerHandle(writer));
        LogRecord record = DLMTestUtil.getLogRecordInstance(1);
        record.setControl();
        try {
            Await.result(writer.asyncWrite(record));
            Assert.fail("Should fail the writer if the log segment is already fenced");
        } catch (BKTransmitException bkte) {
            // expected
            Assert.assertEquals(LedgerFencedException, bkte.getBKResultCode());
        }
        try {
            Await.result(writer.asyncWrite(DLMTestUtil.getLogRecordInstance(2)));
            Assert.fail("Should fail the writer if the log segment is already fenced");
        } catch (WriteException we) {
            // expected
        }
        abortWriterAndLock(writer, lock);
    }

    /**
     * Non durable write should fail if writer is marked as end of stream.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testNondurableWrite() throws Exception {
        DistributedLogConfiguration confLocal = newLocalConf();
        confLocal.setImmediateFlushEnabled(false);
        confLocal.setOutputBufferSize(Integer.MAX_VALUE);
        confLocal.setPeriodicFlushFrequencyMilliSeconds(0);
        confLocal.setDurableWriteEnabled(false);
        ZKDistributedLock lock = createLock(("/test/lock-" + (runtime.getMethodName())), zkc, true);
        BKLogSegmentWriter writer = createLogSegmentWriter(confLocal, 0L, (-1L), lock);
        Assert.assertEquals(InvalidDLSN, Await.result(writer.asyncWrite(DLMTestUtil.getLogRecordInstance(2))));
        Assert.assertEquals((-1L), ((BKLogSegmentEntryWriter) (writer.getEntryWriter())).getLedgerHandle().getLastAddPushed());
        closeWriterAndLock(writer, lock);
    }
}

