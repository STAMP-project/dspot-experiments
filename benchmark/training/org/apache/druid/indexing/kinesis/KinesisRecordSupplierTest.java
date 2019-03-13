/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.indexing.kinesis;


import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.DescribeStreamResult;
import com.amazonaws.services.kinesis.model.GetRecordsResult;
import com.amazonaws.services.kinesis.model.GetShardIteratorResult;
import com.amazonaws.services.kinesis.model.Record;
import com.amazonaws.services.kinesis.model.Shard;
import com.amazonaws.services.kinesis.model.StreamDescription;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.druid.indexing.seekablestream.common.OrderedPartitionableRecord;
import org.apache.druid.indexing.seekablestream.common.StreamPartition;
import org.apache.druid.java.util.common.ISE;
import org.apache.druid.java.util.common.StringUtils;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Test;


public class KinesisRecordSupplierTest extends EasyMockSupport {
    private static final String stream = "stream";

    private static long poll_timeout_millis = 2000;

    private static int recordsPerFetch;

    private static String shardId1 = "1";

    private static String shardId0 = "0";

    private static String shard1Iterator = "1";

    private static String shard0Iterator = "0";

    private static AmazonKinesis kinesis;

    private static DescribeStreamResult describeStreamResult;

    private static GetShardIteratorResult getShardIteratorResult0;

    private static GetShardIteratorResult getShardIteratorResult1;

    private static GetRecordsResult getRecordsResult0;

    private static GetRecordsResult getRecordsResult1;

    private static StreamDescription streamDescription;

    private static Shard shard0;

    private static Shard shard1;

    private static KinesisRecordSupplier recordSupplier;

    private static List<Record> shard1Records = ImmutableList.of(new Record().withData(KinesisRecordSupplierTest.jb("2011", "d", "y", "10", "20.0", "1.0")).withSequenceNumber("0"), new Record().withData(KinesisRecordSupplierTest.jb("2011", "e", "y", "10", "20.0", "1.0")).withSequenceNumber("1"), new Record().withData(KinesisRecordSupplierTest.jb("246140482-04-24T15:36:27.903Z", "x", "z", "10", "20.0", "1.0")).withSequenceNumber("2"), new Record().withData(ByteBuffer.wrap(StringUtils.toUtf8("unparseable"))).withSequenceNumber("3"), new Record().withData(ByteBuffer.wrap(StringUtils.toUtf8("unparseable2"))).withSequenceNumber("4"), new Record().withData(ByteBuffer.wrap(StringUtils.toUtf8("{}"))).withSequenceNumber("5"), new Record().withData(KinesisRecordSupplierTest.jb("2013", "f", "y", "10", "20.0", "1.0")).withSequenceNumber("6"), new Record().withData(KinesisRecordSupplierTest.jb("2049", "f", "y", "notanumber", "20.0", "1.0")).withSequenceNumber("7"), new Record().withData(KinesisRecordSupplierTest.jb("2012", "g", "y", "10", "20.0", "1.0")).withSequenceNumber("8"), new Record().withData(KinesisRecordSupplierTest.jb("2011", "h", "y", "10", "20.0", "1.0")).withSequenceNumber("9"));

    private static List<Record> shard0Records = ImmutableList.of(new Record().withData(KinesisRecordSupplierTest.jb("2008", "a", "y", "10", "20.0", "1.0")).withSequenceNumber("0"), new Record().withData(KinesisRecordSupplierTest.jb("2009", "b", "y", "10", "20.0", "1.0")).withSequenceNumber("1"));

    private static List<Object> allRecords = ImmutableList.builder().addAll(KinesisRecordSupplierTest.shard0Records.stream().map(( x) -> new OrderedPartitionableRecord<>(stream, KinesisRecordSupplierTest.shardId0, x.getSequenceNumber(), Collections.singletonList(toByteArray(x.getData())))).collect(Collectors.toList())).addAll(KinesisRecordSupplierTest.shard1Records.stream().map(( x) -> new OrderedPartitionableRecord<>(stream, KinesisRecordSupplierTest.shardId1, x.getSequenceNumber(), Collections.singletonList(toByteArray(x.getData())))).collect(Collectors.toList())).build();

    @Test
    public void testSupplierSetup() {
        Capture<String> captured = Capture.newInstance();
        expect(KinesisRecordSupplierTest.kinesis.describeStream(capture(captured))).andReturn(KinesisRecordSupplierTest.describeStreamResult).once();
        expect(KinesisRecordSupplierTest.describeStreamResult.getStreamDescription()).andReturn(KinesisRecordSupplierTest.streamDescription).once();
        expect(KinesisRecordSupplierTest.streamDescription.getShards()).andReturn(ImmutableList.of(KinesisRecordSupplierTest.shard0, KinesisRecordSupplierTest.shard1)).once();
        expect(KinesisRecordSupplierTest.shard0.getShardId()).andReturn(KinesisRecordSupplierTest.shardId0).once();
        expect(KinesisRecordSupplierTest.shard1.getShardId()).andReturn(KinesisRecordSupplierTest.shardId1).once();
        replayAll();
        Set<StreamPartition<String>> partitions = ImmutableSet.of(StreamPartition.of(KinesisRecordSupplierTest.stream, KinesisRecordSupplierTest.shardId0), StreamPartition.of(KinesisRecordSupplierTest.stream, KinesisRecordSupplierTest.shardId1));
        KinesisRecordSupplierTest.recordSupplier = new KinesisRecordSupplier(KinesisRecordSupplierTest.kinesis, KinesisRecordSupplierTest.recordsPerFetch, 0, 2, false, 100, 5000, 5000, 60000, 5);
        Assert.assertTrue(KinesisRecordSupplierTest.recordSupplier.getAssignment().isEmpty());
        KinesisRecordSupplierTest.recordSupplier.assign(partitions);
        Assert.assertEquals(partitions, KinesisRecordSupplierTest.recordSupplier.getAssignment());
        Assert.assertEquals(ImmutableSet.of(KinesisRecordSupplierTest.shardId1, KinesisRecordSupplierTest.shardId0), KinesisRecordSupplierTest.recordSupplier.getPartitionIds(KinesisRecordSupplierTest.stream));
        Assert.assertEquals(Collections.emptyList(), KinesisRecordSupplierTest.recordSupplier.poll(100));
        verifyAll();
        Assert.assertEquals(KinesisRecordSupplierTest.stream, captured.getValue());
    }

    @Test
    public void testPoll() throws InterruptedException {
        KinesisRecordSupplierTest.recordsPerFetch = 100;
        expect(KinesisRecordSupplierTest.kinesis.getShardIterator(anyObject(), eq(KinesisRecordSupplierTest.shardId0), anyString(), anyString())).andReturn(KinesisRecordSupplierTest.getShardIteratorResult0).anyTimes();
        expect(KinesisRecordSupplierTest.kinesis.getShardIterator(anyObject(), eq(KinesisRecordSupplierTest.shardId1), anyString(), anyString())).andReturn(KinesisRecordSupplierTest.getShardIteratorResult1).anyTimes();
        expect(KinesisRecordSupplierTest.getShardIteratorResult0.getShardIterator()).andReturn(KinesisRecordSupplierTest.shard0Iterator).anyTimes();
        expect(KinesisRecordSupplierTest.getShardIteratorResult1.getShardIterator()).andReturn(KinesisRecordSupplierTest.shard1Iterator).anyTimes();
        expect(KinesisRecordSupplierTest.kinesis.getRecords(KinesisRecordSupplierTest.generateGetRecordsReq(KinesisRecordSupplierTest.shard0Iterator, KinesisRecordSupplierTest.recordsPerFetch))).andReturn(KinesisRecordSupplierTest.getRecordsResult0).anyTimes();
        expect(KinesisRecordSupplierTest.kinesis.getRecords(KinesisRecordSupplierTest.generateGetRecordsReq(KinesisRecordSupplierTest.shard1Iterator, KinesisRecordSupplierTest.recordsPerFetch))).andReturn(KinesisRecordSupplierTest.getRecordsResult1).anyTimes();
        expect(KinesisRecordSupplierTest.getRecordsResult0.getRecords()).andReturn(KinesisRecordSupplierTest.shard0Records).once();
        expect(KinesisRecordSupplierTest.getRecordsResult1.getRecords()).andReturn(KinesisRecordSupplierTest.shard1Records).once();
        expect(KinesisRecordSupplierTest.getRecordsResult0.getNextShardIterator()).andReturn(null).anyTimes();
        expect(KinesisRecordSupplierTest.getRecordsResult1.getNextShardIterator()).andReturn(null).anyTimes();
        replayAll();
        Set<StreamPartition<String>> partitions = ImmutableSet.of(StreamPartition.of(KinesisRecordSupplierTest.stream, KinesisRecordSupplierTest.shardId0), StreamPartition.of(KinesisRecordSupplierTest.stream, KinesisRecordSupplierTest.shardId1));
        KinesisRecordSupplierTest.recordSupplier = new KinesisRecordSupplier(KinesisRecordSupplierTest.kinesis, KinesisRecordSupplierTest.recordsPerFetch, 0, 2, false, 100, 5000, 5000, 60000, 100);
        KinesisRecordSupplierTest.recordSupplier.assign(partitions);
        KinesisRecordSupplierTest.recordSupplier.seekToEarliest(partitions);
        KinesisRecordSupplierTest.recordSupplier.start();
        while ((KinesisRecordSupplierTest.recordSupplier.bufferSize()) < 12) {
            Thread.sleep(100);
        } 
        List<OrderedPartitionableRecord<String, String>> polledRecords = KinesisRecordSupplierTest.cleanRecords(KinesisRecordSupplierTest.recordSupplier.poll(KinesisRecordSupplierTest.poll_timeout_millis));
        verifyAll();
        Assert.assertEquals(partitions, KinesisRecordSupplierTest.recordSupplier.getAssignment());
        Assert.assertTrue(polledRecords.containsAll(KinesisRecordSupplierTest.allRecords));
    }

    @Test
    public void testSeek() throws InterruptedException {
        KinesisRecordSupplierTest.recordsPerFetch = 100;
        expect(KinesisRecordSupplierTest.kinesis.getShardIterator(anyObject(), eq(KinesisRecordSupplierTest.shardId0), anyString(), anyString())).andReturn(KinesisRecordSupplierTest.getShardIteratorResult0).anyTimes();
        expect(KinesisRecordSupplierTest.kinesis.getShardIterator(anyObject(), eq(KinesisRecordSupplierTest.shardId1), anyString(), anyString())).andReturn(KinesisRecordSupplierTest.getShardIteratorResult1).anyTimes();
        expect(KinesisRecordSupplierTest.getShardIteratorResult0.getShardIterator()).andReturn(KinesisRecordSupplierTest.shard0Iterator).anyTimes();
        expect(KinesisRecordSupplierTest.getShardIteratorResult1.getShardIterator()).andReturn(KinesisRecordSupplierTest.shard1Iterator).anyTimes();
        expect(KinesisRecordSupplierTest.kinesis.getRecords(KinesisRecordSupplierTest.generateGetRecordsReq(KinesisRecordSupplierTest.shard0Iterator, KinesisRecordSupplierTest.recordsPerFetch))).andReturn(KinesisRecordSupplierTest.getRecordsResult0).anyTimes();
        expect(KinesisRecordSupplierTest.kinesis.getRecords(KinesisRecordSupplierTest.generateGetRecordsReq(KinesisRecordSupplierTest.shard1Iterator, KinesisRecordSupplierTest.recordsPerFetch))).andReturn(KinesisRecordSupplierTest.getRecordsResult1).anyTimes();
        expect(KinesisRecordSupplierTest.getRecordsResult0.getRecords()).andReturn(KinesisRecordSupplierTest.shard0Records.subList(1, KinesisRecordSupplierTest.shard0Records.size())).once();
        expect(KinesisRecordSupplierTest.getRecordsResult1.getRecords()).andReturn(KinesisRecordSupplierTest.shard1Records.subList(2, KinesisRecordSupplierTest.shard1Records.size())).once();
        expect(KinesisRecordSupplierTest.getRecordsResult0.getNextShardIterator()).andReturn(null).anyTimes();
        expect(KinesisRecordSupplierTest.getRecordsResult1.getNextShardIterator()).andReturn(null).anyTimes();
        replayAll();
        StreamPartition<String> shard0Partition = StreamPartition.of(KinesisRecordSupplierTest.stream, KinesisRecordSupplierTest.shardId0);
        StreamPartition<String> shard1Partition = StreamPartition.of(KinesisRecordSupplierTest.stream, KinesisRecordSupplierTest.shardId1);
        Set<StreamPartition<String>> partitions = ImmutableSet.of(shard0Partition, shard1Partition);
        KinesisRecordSupplierTest.recordSupplier = new KinesisRecordSupplier(KinesisRecordSupplierTest.kinesis, KinesisRecordSupplierTest.recordsPerFetch, 0, 2, false, 100, 5000, 5000, 60000, 100);
        KinesisRecordSupplierTest.recordSupplier.assign(partitions);
        KinesisRecordSupplierTest.recordSupplier.seek(shard1Partition, KinesisRecordSupplierTest.shard1Records.get(2).getSequenceNumber());
        KinesisRecordSupplierTest.recordSupplier.seek(shard0Partition, KinesisRecordSupplierTest.shard0Records.get(1).getSequenceNumber());
        KinesisRecordSupplierTest.recordSupplier.start();
        for (int i = 0; (i < 10) && ((KinesisRecordSupplierTest.recordSupplier.bufferSize()) < 9); i++) {
            Thread.sleep(100);
        }
        List<OrderedPartitionableRecord<String, String>> polledRecords = KinesisRecordSupplierTest.cleanRecords(KinesisRecordSupplierTest.recordSupplier.poll(KinesisRecordSupplierTest.poll_timeout_millis));
        verifyAll();
        Assert.assertEquals(9, polledRecords.size());
        Assert.assertTrue(polledRecords.containsAll(KinesisRecordSupplierTest.allRecords.subList(4, 12)));
        Assert.assertTrue(polledRecords.containsAll(KinesisRecordSupplierTest.allRecords.subList(1, 2)));
    }

    @Test
    public void testSeekToLatest() throws InterruptedException {
        KinesisRecordSupplierTest.recordsPerFetch = 100;
        expect(KinesisRecordSupplierTest.kinesis.getShardIterator(anyObject(), eq(KinesisRecordSupplierTest.shardId0), anyString(), anyString())).andReturn(KinesisRecordSupplierTest.getShardIteratorResult0).anyTimes();
        expect(KinesisRecordSupplierTest.kinesis.getShardIterator(anyObject(), eq(KinesisRecordSupplierTest.shardId1), anyString(), anyString())).andReturn(KinesisRecordSupplierTest.getShardIteratorResult1).anyTimes();
        expect(KinesisRecordSupplierTest.getShardIteratorResult0.getShardIterator()).andReturn(null).once();
        expect(KinesisRecordSupplierTest.getShardIteratorResult1.getShardIterator()).andReturn(null).once();
        replayAll();
        StreamPartition<String> shard0 = StreamPartition.of(KinesisRecordSupplierTest.stream, KinesisRecordSupplierTest.shardId0);
        StreamPartition<String> shard1 = StreamPartition.of(KinesisRecordSupplierTest.stream, KinesisRecordSupplierTest.shardId1);
        Set<StreamPartition<String>> partitions = ImmutableSet.of(shard0, shard1);
        KinesisRecordSupplierTest.recordSupplier = new KinesisRecordSupplier(KinesisRecordSupplierTest.kinesis, KinesisRecordSupplierTest.recordsPerFetch, 0, 2, false, 100, 5000, 5000, 60000, 100);
        KinesisRecordSupplierTest.recordSupplier.assign(partitions);
        KinesisRecordSupplierTest.recordSupplier.seekToLatest(partitions);
        KinesisRecordSupplierTest.recordSupplier.start();
        for (int i = 0; (i < 10) && ((KinesisRecordSupplierTest.recordSupplier.bufferSize()) < 2); i++) {
            Thread.sleep(100);
        }
        Assert.assertEquals(Collections.emptyList(), KinesisRecordSupplierTest.cleanRecords(KinesisRecordSupplierTest.recordSupplier.poll(KinesisRecordSupplierTest.poll_timeout_millis)));
        verifyAll();
    }

    @Test(expected = ISE.class)
    public void testSeekUnassigned() throws InterruptedException {
        StreamPartition<String> shard0 = StreamPartition.of(KinesisRecordSupplierTest.stream, KinesisRecordSupplierTest.shardId0);
        StreamPartition<String> shard1 = StreamPartition.of(KinesisRecordSupplierTest.stream, KinesisRecordSupplierTest.shardId1);
        Set<StreamPartition<String>> partitions = ImmutableSet.of(shard1);
        KinesisRecordSupplierTest.recordSupplier = new KinesisRecordSupplier(KinesisRecordSupplierTest.kinesis, 1, 0, 2, false, 100, 5000, 5000, 60000, 5);
        KinesisRecordSupplierTest.recordSupplier.assign(partitions);
        KinesisRecordSupplierTest.recordSupplier.seekToEarliest(Collections.singleton(shard0));
    }

    @Test
    public void testPollAfterSeek() throws InterruptedException {
        // tests that after doing a seek, the now invalid records in buffer is cleaned up properly
        KinesisRecordSupplierTest.recordsPerFetch = 100;
        expect(KinesisRecordSupplierTest.kinesis.getShardIterator(anyObject(), eq(KinesisRecordSupplierTest.shardId1), anyString(), eq("5"))).andReturn(KinesisRecordSupplierTest.getShardIteratorResult1).once();
        expect(KinesisRecordSupplierTest.kinesis.getShardIterator(anyObject(), eq(KinesisRecordSupplierTest.shardId1), anyString(), eq("7"))).andReturn(KinesisRecordSupplierTest.getShardIteratorResult0).once();
        expect(KinesisRecordSupplierTest.getShardIteratorResult1.getShardIterator()).andReturn(KinesisRecordSupplierTest.shard1Iterator).once();
        expect(KinesisRecordSupplierTest.getShardIteratorResult0.getShardIterator()).andReturn(KinesisRecordSupplierTest.shard0Iterator).once();
        expect(KinesisRecordSupplierTest.kinesis.getRecords(KinesisRecordSupplierTest.generateGetRecordsReq(KinesisRecordSupplierTest.shard1Iterator, KinesisRecordSupplierTest.recordsPerFetch))).andReturn(KinesisRecordSupplierTest.getRecordsResult1).once();
        expect(KinesisRecordSupplierTest.kinesis.getRecords(KinesisRecordSupplierTest.generateGetRecordsReq(KinesisRecordSupplierTest.shard0Iterator, KinesisRecordSupplierTest.recordsPerFetch))).andReturn(KinesisRecordSupplierTest.getRecordsResult0).once();
        expect(KinesisRecordSupplierTest.getRecordsResult1.getRecords()).andReturn(KinesisRecordSupplierTest.shard1Records.subList(5, KinesisRecordSupplierTest.shard1Records.size())).once();
        expect(KinesisRecordSupplierTest.getRecordsResult0.getRecords()).andReturn(KinesisRecordSupplierTest.shard1Records.subList(7, KinesisRecordSupplierTest.shard1Records.size())).once();
        expect(KinesisRecordSupplierTest.getRecordsResult1.getNextShardIterator()).andReturn(null).anyTimes();
        expect(KinesisRecordSupplierTest.getRecordsResult0.getNextShardIterator()).andReturn(null).anyTimes();
        replayAll();
        Set<StreamPartition<String>> partitions = ImmutableSet.of(StreamPartition.of(KinesisRecordSupplierTest.stream, KinesisRecordSupplierTest.shardId1));
        KinesisRecordSupplierTest.recordSupplier = new KinesisRecordSupplier(KinesisRecordSupplierTest.kinesis, KinesisRecordSupplierTest.recordsPerFetch, 0, 2, false, 100, 5000, 5000, 60000, 1);
        KinesisRecordSupplierTest.recordSupplier.assign(partitions);
        KinesisRecordSupplierTest.recordSupplier.seek(StreamPartition.of(KinesisRecordSupplierTest.stream, KinesisRecordSupplierTest.shardId1), "5");
        KinesisRecordSupplierTest.recordSupplier.start();
        for (int i = 0; (i < 10) && ((KinesisRecordSupplierTest.recordSupplier.bufferSize()) < 6); i++) {
            Thread.sleep(100);
        }
        OrderedPartitionableRecord<String, String> firstRecord = KinesisRecordSupplierTest.recordSupplier.poll(KinesisRecordSupplierTest.poll_timeout_millis).get(0);
        Assert.assertEquals(KinesisRecordSupplierTest.allRecords.get(7), firstRecord);
        KinesisRecordSupplierTest.recordSupplier.seek(StreamPartition.of(KinesisRecordSupplierTest.stream, KinesisRecordSupplierTest.shardId1), "7");
        KinesisRecordSupplierTest.recordSupplier.start();
        while ((KinesisRecordSupplierTest.recordSupplier.bufferSize()) < 4) {
            Thread.sleep(100);
        } 
        OrderedPartitionableRecord<String, String> record2 = KinesisRecordSupplierTest.recordSupplier.poll(KinesisRecordSupplierTest.poll_timeout_millis).get(0);
        Assert.assertEquals(KinesisRecordSupplierTest.allRecords.get(9), record2);
        verifyAll();
    }

    @Test
    public void testPollDeaggregate() throws InterruptedException {
        KinesisRecordSupplierTest.recordsPerFetch = 100;
        expect(KinesisRecordSupplierTest.kinesis.getShardIterator(anyObject(), eq(KinesisRecordSupplierTest.shardId0), anyString(), anyString())).andReturn(KinesisRecordSupplierTest.getShardIteratorResult0).anyTimes();
        expect(KinesisRecordSupplierTest.kinesis.getShardIterator(anyObject(), eq(KinesisRecordSupplierTest.shardId1), anyString(), anyString())).andReturn(KinesisRecordSupplierTest.getShardIteratorResult1).anyTimes();
        expect(KinesisRecordSupplierTest.getShardIteratorResult0.getShardIterator()).andReturn(KinesisRecordSupplierTest.shard0Iterator).anyTimes();
        expect(KinesisRecordSupplierTest.getShardIteratorResult1.getShardIterator()).andReturn(KinesisRecordSupplierTest.shard1Iterator).anyTimes();
        expect(KinesisRecordSupplierTest.kinesis.getRecords(KinesisRecordSupplierTest.generateGetRecordsReq(KinesisRecordSupplierTest.shard0Iterator, KinesisRecordSupplierTest.recordsPerFetch))).andReturn(KinesisRecordSupplierTest.getRecordsResult0).anyTimes();
        expect(KinesisRecordSupplierTest.kinesis.getRecords(KinesisRecordSupplierTest.generateGetRecordsReq(KinesisRecordSupplierTest.shard1Iterator, KinesisRecordSupplierTest.recordsPerFetch))).andReturn(KinesisRecordSupplierTest.getRecordsResult1).anyTimes();
        expect(KinesisRecordSupplierTest.getRecordsResult0.getRecords()).andReturn(KinesisRecordSupplierTest.shard0Records).once();
        expect(KinesisRecordSupplierTest.getRecordsResult1.getRecords()).andReturn(KinesisRecordSupplierTest.shard1Records).once();
        expect(KinesisRecordSupplierTest.getRecordsResult0.getNextShardIterator()).andReturn(null).anyTimes();
        expect(KinesisRecordSupplierTest.getRecordsResult1.getNextShardIterator()).andReturn(null).anyTimes();
        replayAll();
        Set<StreamPartition<String>> partitions = ImmutableSet.of(StreamPartition.of(KinesisRecordSupplierTest.stream, KinesisRecordSupplierTest.shardId0), StreamPartition.of(KinesisRecordSupplierTest.stream, KinesisRecordSupplierTest.shardId1));
        KinesisRecordSupplierTest.recordSupplier = new KinesisRecordSupplier(KinesisRecordSupplierTest.kinesis, KinesisRecordSupplierTest.recordsPerFetch, 0, 2, true, 100, 5000, 5000, 60000, 100);
        KinesisRecordSupplierTest.recordSupplier.assign(partitions);
        KinesisRecordSupplierTest.recordSupplier.seekToEarliest(partitions);
        KinesisRecordSupplierTest.recordSupplier.start();
        for (int i = 0; (i < 10) && ((KinesisRecordSupplierTest.recordSupplier.bufferSize()) < 12); i++) {
            Thread.sleep(100);
        }
        List<OrderedPartitionableRecord<String, String>> polledRecords = KinesisRecordSupplierTest.cleanRecords(KinesisRecordSupplierTest.recordSupplier.poll(KinesisRecordSupplierTest.poll_timeout_millis));
        verifyAll();
        Assert.assertEquals(partitions, KinesisRecordSupplierTest.recordSupplier.getAssignment());
        Assert.assertTrue(polledRecords.containsAll(KinesisRecordSupplierTest.allRecords));
    }
}

