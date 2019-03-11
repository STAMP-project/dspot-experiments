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
package com.twitter.distributedlog.util;


import LogSegmentMetadataVersion.VERSION_V1_ORIGINAL.value;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.twitter.distributedlog.LogSegmentMetadata;
import com.twitter.distributedlog.exceptions.UnexpectedException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test Case for {@link DLUtils}
 */
public class TestDLUtils {
    @Test(timeout = 60000)
    public void testFindLogSegmentNotLessThanTxnId() throws Exception {
        long txnId = 999L;
        // empty list
        List<LogSegmentMetadata> emptyList = Lists.newArrayList();
        Assert.assertEquals((-1), DLUtils.findLogSegmentNotLessThanTxnId(emptyList, txnId));
        // list that all segment's txn id is larger than txn-id-to-search
        List<LogSegmentMetadata> list1 = Lists.newArrayList(TestDLUtils.completedLogSegment(1L, 1000L, 2000L));
        Assert.assertEquals((-1), DLUtils.findLogSegmentNotLessThanTxnId(list1, txnId));
        List<LogSegmentMetadata> list2 = Lists.newArrayList(TestDLUtils.inprogressLogSegment(1L, 1000L));
        Assert.assertEquals((-1), DLUtils.findLogSegmentNotLessThanTxnId(list2, txnId));
        // the first log segment whose first txn id is less than txn-id-to-search
        List<LogSegmentMetadata> list3 = Lists.newArrayList(TestDLUtils.completedLogSegment(1L, 0L, 99L), TestDLUtils.completedLogSegment(2L, 1000L, 2000L));
        Assert.assertEquals(1, DLUtils.findLogSegmentNotLessThanTxnId(list3, txnId));
        List<LogSegmentMetadata> list4 = Lists.newArrayList(TestDLUtils.completedLogSegment(1L, 0L, 990L), TestDLUtils.completedLogSegment(2L, 1000L, 2000L));
        Assert.assertEquals(1, DLUtils.findLogSegmentNotLessThanTxnId(list4, txnId));
        List<LogSegmentMetadata> list5 = Lists.newArrayList(TestDLUtils.inprogressLogSegment(1L, 0L), TestDLUtils.inprogressLogSegment(2L, 1000L));
        Assert.assertEquals(0, DLUtils.findLogSegmentNotLessThanTxnId(list5, txnId));
        // list that all segment's txn id is less than txn-id-to-search
        List<LogSegmentMetadata> list6_0 = Lists.newArrayList(TestDLUtils.completedLogSegment(1L, 100L, 200L));
        Assert.assertEquals(0, DLUtils.findLogSegmentNotLessThanTxnId(list6_0, txnId));
        List<LogSegmentMetadata> list6_1 = Lists.newArrayList(TestDLUtils.completedLogSegment(1L, 100L, 199L), TestDLUtils.completedLogSegment(2L, 200L, 299L));
        Assert.assertEquals(1, DLUtils.findLogSegmentNotLessThanTxnId(list6_1, txnId));
        List<LogSegmentMetadata> list7 = Lists.newArrayList(TestDLUtils.inprogressLogSegment(1L, 100L));
        Assert.assertEquals(0, DLUtils.findLogSegmentNotLessThanTxnId(list7, txnId));
        // list that first segment's first txn id equals to txn-id-to-search
        List<LogSegmentMetadata> list8 = Lists.newArrayList(TestDLUtils.completedLogSegment(1L, 999L, 2000L));
        Assert.assertEquals(0, DLUtils.findLogSegmentNotLessThanTxnId(list8, txnId));
        List<LogSegmentMetadata> list9 = Lists.newArrayList(TestDLUtils.inprogressLogSegment(1L, 999L));
        Assert.assertEquals(0, DLUtils.findLogSegmentNotLessThanTxnId(list9, txnId));
        List<LogSegmentMetadata> list10 = Lists.newArrayList(TestDLUtils.completedLogSegment(1L, 0L, 999L), TestDLUtils.completedLogSegment(2L, 999L, 2000L));
        Assert.assertEquals(0, DLUtils.findLogSegmentNotLessThanTxnId(list10, txnId));
        List<LogSegmentMetadata> list11 = Lists.newArrayList(TestDLUtils.completedLogSegment(1L, 0L, 99L), TestDLUtils.completedLogSegment(2L, 999L, 2000L));
        Assert.assertEquals(1, DLUtils.findLogSegmentNotLessThanTxnId(list11, txnId));
        List<LogSegmentMetadata> list12 = Lists.newArrayList(TestDLUtils.inprogressLogSegment(1L, 0L), TestDLUtils.inprogressLogSegment(2L, 999L));
        Assert.assertEquals(1, DLUtils.findLogSegmentNotLessThanTxnId(list12, txnId));
    }

    @Test(timeout = 60000)
    public void testNextLogSegmentSequenceNumber() throws Exception {
        List<LogSegmentMetadata> v1List = Lists.newArrayList(TestDLUtils.completedLogSegment(2L, 100L, 199L, value), TestDLUtils.completedLogSegment(1L, 0L, 99L, value));
        Assert.assertNull(DLUtils.nextLogSegmentSequenceNumber(v1List));
        List<LogSegmentMetadata> afterV1List = Lists.newArrayList(TestDLUtils.completedLogSegment(2L, 100L, 199L), TestDLUtils.completedLogSegment(1L, 0L, 99L));
        Assert.assertEquals(((Long) (3L)), DLUtils.nextLogSegmentSequenceNumber(afterV1List));
        List<LogSegmentMetadata> mixList1 = Lists.newArrayList(TestDLUtils.completedLogSegment(2L, 100L, 199L, value), TestDLUtils.completedLogSegment(1L, 0L, 99L));
        Assert.assertEquals(((Long) (3L)), DLUtils.nextLogSegmentSequenceNumber(mixList1));
        List<LogSegmentMetadata> mixList2 = Lists.newArrayList(TestDLUtils.completedLogSegment(2L, 100L, 199L), TestDLUtils.completedLogSegment(1L, 0L, 99L, value));
        Assert.assertEquals(((Long) (3L)), DLUtils.nextLogSegmentSequenceNumber(mixList2));
    }

    @Test(timeout = 60000, expected = UnexpectedException.class)
    public void testUnexpectedExceptionOnComputeStartSequenceId() throws Exception {
        List<LogSegmentMetadata> segments = Lists.newArrayList(TestDLUtils.inprogressLogSegment(3L, 201L), TestDLUtils.inprogressLogSegment(2L, 101L), TestDLUtils.completedLogSegment(1L, 1L, 100L).mutator().setStartSequenceId(1L).build());
        DLUtils.computeStartSequenceId(segments, segments.get(0));
    }

    @Test(timeout = 60000)
    public void testComputeStartSequenceIdOnEmptyList() throws Exception {
        List<LogSegmentMetadata> emptyList = Lists.newArrayList();
        Assert.assertEquals(0L, DLUtils.computeStartSequenceId(emptyList, TestDLUtils.inprogressLogSegment(1L, 1L)));
    }

    @Test(timeout = 60000)
    public void testComputeStartSequenceIdOnLowerSequenceNumberSegment() throws Exception {
        List<LogSegmentMetadata> segments = Lists.newArrayList(TestDLUtils.completedLogSegment(3L, 201L, 300L).mutator().setStartSequenceId(201L).build(), TestDLUtils.completedLogSegment(2L, 101L, 200L).mutator().setStartSequenceId(101L).build());
        Assert.assertEquals(0L, DLUtils.computeStartSequenceId(segments, TestDLUtils.inprogressLogSegment(1L, 1L)));
    }

    @Test(timeout = 60000)
    public void testComputeStartSequenceIdOnHigherSequenceNumberSegment() throws Exception {
        List<LogSegmentMetadata> segments = Lists.newArrayList(TestDLUtils.completedLogSegment(3L, 201L, 300L).mutator().setStartSequenceId(201L).build(), TestDLUtils.completedLogSegment(2L, 101L, 200L).mutator().setStartSequenceId(101L).build());
        Assert.assertEquals(0L, DLUtils.computeStartSequenceId(segments, TestDLUtils.inprogressLogSegment(5L, 401L)));
    }

    @Test(timeout = 60000)
    public void testComputeStartSequenceId() throws Exception {
        List<LogSegmentMetadata> segments = Lists.newArrayList(TestDLUtils.completedLogSegment(3L, 201L, 300L).mutator().setStartSequenceId(201L).setRecordCount(100).build(), TestDLUtils.completedLogSegment(2L, 101L, 200L).mutator().setStartSequenceId(101L).setRecordCount(100).build());
        Assert.assertEquals(301L, DLUtils.computeStartSequenceId(segments, TestDLUtils.inprogressLogSegment(4L, 301L)));
    }

    @Test(timeout = 60000)
    public void testSerDeLogSegmentSequenceNumber() throws Exception {
        long sn = 123456L;
        byte[] snData = Long.toString(sn).getBytes(Charsets.UTF_8);
        Assert.assertEquals("Deserialization should succeed", sn, DLUtils.deserializeLogSegmentSequenceNumber(snData));
        Assert.assertArrayEquals("Serialization should succeed", snData, DLUtils.serializeLogSegmentSequenceNumber(sn));
    }

    @Test(timeout = 60000, expected = NumberFormatException.class)
    public void testDeserilizeInvalidLSSN() throws Exception {
        byte[] corruptedData = "corrupted-lssn".getBytes(Charsets.UTF_8);
        DLUtils.deserializeLogSegmentSequenceNumber(corruptedData);
    }

    @Test(timeout = 60000)
    public void testSerDeLogRecordTxnId() throws Exception {
        long txnId = 123456L;
        byte[] txnData = Long.toString(txnId).getBytes(Charsets.UTF_8);
        Assert.assertEquals("Deserialization should succeed", txnId, DLUtils.deserializeTransactionId(txnData));
        Assert.assertArrayEquals("Serialization should succeed", txnData, DLUtils.serializeTransactionId(txnId));
    }

    @Test(timeout = 60000, expected = NumberFormatException.class)
    public void testDeserilizeInvalidLogRecordTxnId() throws Exception {
        byte[] corruptedData = "corrupted-txn-id".getBytes(Charsets.UTF_8);
        DLUtils.deserializeTransactionId(corruptedData);
    }

    @Test(timeout = 60000)
    public void testSerDeLedgerId() throws Exception {
        long ledgerId = 123456L;
        byte[] ledgerIdData = Long.toString(ledgerId).getBytes(Charsets.UTF_8);
        Assert.assertEquals("Deserialization should succeed", ledgerId, DLUtils.bytes2LedgerId(ledgerIdData));
        Assert.assertArrayEquals("Serialization should succeed", ledgerIdData, DLUtils.ledgerId2Bytes(ledgerId));
    }

    @Test(timeout = 60000, expected = NumberFormatException.class)
    public void testDeserializeInvalidLedgerId() throws Exception {
        byte[] corruptedData = "corrupted-ledger-id".getBytes(Charsets.UTF_8);
        DLUtils.bytes2LedgerId(corruptedData);
    }
}

