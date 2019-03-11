/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.apache.storm.hive.bolt;


import HiveConf.ConfVars.METASTOREURIS;
import HiveWriter.CommitFailure;
import HiveWriter.ConnectFailure;
import HiveWriter.TxnBatchFailure;
import HiveWriter.TxnFailure;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.txn.TxnDbUtil;
import org.apache.hadoop.hive.serde.serdeConstants;
import org.apache.hive.hcatalog.streaming.HiveEndPoint;
import org.apache.storm.Config;
import org.apache.storm.hive.bolt.mapper.DelimitedRecordHiveMapper;
import org.apache.storm.hive.bolt.mapper.JsonRecordHiveMapper;
import org.apache.storm.hive.common.HiveOptions;
import org.apache.storm.hive.common.HiveWriter;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.utils.MockTupleHelpers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestHiveBolt {
    static final String dbName = "testdb";

    static final String tblName = "test_table";

    static final String dbName1 = "testdb1";

    static final String tblName1 = "test_table1";

    static final String PART1_NAME = "city";

    static final String PART2_NAME = "state";

    static final String[] partNames = new String[]{ TestHiveBolt.PART1_NAME, TestHiveBolt.PART2_NAME };

    private static final String COL1 = "id";

    private static final String COL2 = "msg";

    private static final Logger LOG = LoggerFactory.getLogger(HiveBolt.class);

    final String partitionVals = "sunnyvale,ca";

    final String[] colNames = new String[]{ TestHiveBolt.COL1, TestHiveBolt.COL2 };

    final String[] colNames1 = new String[]{ TestHiveBolt.COL2, TestHiveBolt.COL1 };

    final String metaStoreURI;

    private final HiveConf conf;

    private String[] colTypes = new String[]{ serdeConstants.INT_TYPE_NAME, serdeConstants.STRING_TYPE_NAME };

    private Config config = new Config();

    private TestHiveBolt.TestingHiveBolt bolt;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private OutputCollector collector;

    public TestHiveBolt() throws Exception {
        // metaStoreURI = "jdbc:derby:;databaseName="+System.getProperty("java.io.tmpdir") +"metastore_db;create=true";
        metaStoreURI = null;
        conf = HiveSetupUtil.getHiveConf();
        TxnDbUtil.setConfValues(conf);
        if ((metaStoreURI) != null) {
            conf.setVar(METASTOREURIS, metaStoreURI);
        }
    }

    @Test
    public void testWithByteArrayIdandMessage() throws Exception {
        DelimitedRecordHiveMapper mapper = new DelimitedRecordHiveMapper().withColumnFields(new Fields(colNames)).withPartitionFields(new Fields(TestHiveBolt.partNames));
        HiveOptions hiveOptions = withTxnsPerBatch(2).withBatchSize(2);
        bolt = new TestHiveBolt.TestingHiveBolt(hiveOptions);
        bolt.prepare(config, null, collector);
        Integer id = 100;
        String msg = "test-123";
        String city = "sunnyvale";
        String state = "ca";
        Set<Tuple> tupleSet = new HashSet<Tuple>();
        for (int i = 0; i < 4; i++) {
            Tuple tuple = generateTestTuple(id, msg, city, state);
            bolt.execute(tuple);
            tupleSet.add(tuple);
        }
        List<String> partVals = Lists.newArrayList(city, state);
        for (Tuple t : tupleSet) {
            Mockito.verify(collector).ack(t);
        }
        Assert.assertEquals(4, bolt.getRecordWritten(partVals).size());
        cleanup();
    }

    @Test
    public void testWithoutPartitions() throws Exception {
        DelimitedRecordHiveMapper mapper = new DelimitedRecordHiveMapper().withColumnFields(new Fields(colNames));
        HiveOptions hiveOptions = withTxnsPerBatch(2).withBatchSize(2).withAutoCreatePartitions(false);
        bolt = new TestHiveBolt.TestingHiveBolt(hiveOptions);
        bolt.prepare(config, null, collector);
        Integer id = 100;
        String msg = "test-123";
        String city = "sunnyvale";
        String state = "ca";
        Set<Tuple> tupleSet = new HashSet<Tuple>();
        for (int i = 0; i < 4; i++) {
            Tuple tuple = generateTestTuple(id, msg, city, state);
            bolt.execute(tuple);
            tupleSet.add(tuple);
        }
        List<String> partVals = Collections.emptyList();
        for (Tuple t : tupleSet) {
            Mockito.verify(collector).ack(t);
        }
        List<byte[]> recordWritten = bolt.getRecordWritten(partVals);
        Assert.assertNotNull(recordWritten);
        Assert.assertEquals(4, recordWritten.size());
        cleanup();
    }

    @Test
    public void testWithTimeformat() throws Exception {
        String timeFormat = "yyyy/MM/dd";
        DelimitedRecordHiveMapper mapper = new DelimitedRecordHiveMapper().withColumnFields(new Fields(colNames)).withTimeAsPartitionField(timeFormat);
        HiveOptions hiveOptions = withTxnsPerBatch(2).withBatchSize(1).withMaxOpenConnections(1);
        bolt = new TestHiveBolt.TestingHiveBolt(hiveOptions);
        bolt.prepare(config, null, collector);
        Integer id = 100;
        String msg = "test-123";
        Date d = new Date();
        SimpleDateFormat parseDate = new SimpleDateFormat(timeFormat);
        String today = parseDate.format(d.getTime());
        List<Tuple> tuples = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Tuple tuple = generateTestTuple(id, msg, null, null);
            tuples.add(tuple);
            bolt.execute(tuple);
        }
        for (Tuple t : tuples) {
            Mockito.verify(collector).ack(t);
        }
        List<String> partVals = Lists.newArrayList(today);
        List<byte[]> recordsWritten = bolt.getRecordWritten(partVals);
        Assert.assertNotNull(recordsWritten);
        Assert.assertEquals(2, recordsWritten.size());
        byte[] mapped = generateDelimiteredRecord(Lists.newArrayList(id, msg), mapper.getFieldDelimiter());
        for (byte[] record : recordsWritten) {
            Assert.assertArrayEquals(mapped, record);
        }
        cleanup();
    }

    @Test
    public void testData() throws Exception {
        DelimitedRecordHiveMapper mapper = new DelimitedRecordHiveMapper().withColumnFields(new Fields(colNames)).withPartitionFields(new Fields(TestHiveBolt.partNames));
        HiveOptions hiveOptions = withTxnsPerBatch(2).withBatchSize(1);
        bolt = new TestHiveBolt.TestingHiveBolt(hiveOptions);
        bolt.prepare(config, null, new OutputCollector(collector));
        Integer id = 1;
        String msg = "SJC";
        String city = "Sunnyvale";
        String state = "CA";
        Tuple tuple1 = generateTestTuple(id, msg, city, state);
        bolt.execute(tuple1);
        Mockito.verify(collector).ack(tuple1);
        List<String> partVals = Lists.newArrayList(city, state);
        List<byte[]> recordsWritten = bolt.getRecordWritten(partVals);
        Assert.assertNotNull(recordsWritten);
        Assert.assertEquals(1, recordsWritten.size());
        byte[] mapped = generateDelimiteredRecord(Lists.newArrayList(id, msg), mapper.getFieldDelimiter());
        Assert.assertArrayEquals(mapped, recordsWritten.get(0));
        cleanup();
    }

    @Test
    public void testJsonWriter() throws Exception {
        // json record doesn't need columns to be in the same order
        // as table in hive.
        JsonRecordHiveMapper mapper = new JsonRecordHiveMapper().withColumnFields(new Fields(colNames1)).withPartitionFields(new Fields(TestHiveBolt.partNames));
        HiveOptions hiveOptions = withTxnsPerBatch(2).withBatchSize(1);
        bolt = new TestHiveBolt.TestingHiveBolt(hiveOptions);
        bolt.prepare(config, null, collector);
        Integer id = 1;
        String msg = "SJC";
        String city = "Sunnyvale";
        String state = "CA";
        Tuple tuple1 = generateTestTuple(id, msg, city, state);
        bolt.execute(tuple1);
        Mockito.verify(collector).ack(tuple1);
        List<String> partVals = Lists.newArrayList(city, state);
        List<byte[]> recordsWritten = bolt.getRecordWritten(partVals);
        Assert.assertNotNull(recordsWritten);
        Assert.assertEquals(1, recordsWritten.size());
        byte[] written = recordsWritten.get(0);
        Map<String, ?> writtenMap = objectMapper.readValue(new String(written), new TypeReference<Map<String, ?>>() {});
        Map<String, Object> expected = new HashMap<>();
        expected.put(TestHiveBolt.COL1, id);
        expected.put(TestHiveBolt.COL2, msg);
        Assert.assertEquals(expected, writtenMap);
        cleanup();
    }

    @Test
    public void testNoAcksUntilFlushed() {
        JsonRecordHiveMapper mapper = new JsonRecordHiveMapper().withColumnFields(new Fields(colNames1)).withPartitionFields(new Fields(TestHiveBolt.partNames));
        HiveOptions hiveOptions = withTxnsPerBatch(2).withBatchSize(2);
        bolt = new TestHiveBolt.TestingHiveBolt(hiveOptions);
        bolt.prepare(config, null, new OutputCollector(collector));
        Tuple tuple1 = generateTestTuple(1, "SJC", "Sunnyvale", "CA");
        Tuple tuple2 = generateTestTuple(2, "SFO", "San Jose", "CA");
        bolt.execute(tuple1);
        Mockito.verifyZeroInteractions(collector);
        bolt.execute(tuple2);
        Mockito.verify(collector).ack(tuple1);
        Mockito.verify(collector).ack(tuple2);
        cleanup();
    }

    @Test
    public void testNoAcksIfFlushFails() throws Exception {
        JsonRecordHiveMapper mapper = new JsonRecordHiveMapper().withColumnFields(new Fields(colNames1)).withPartitionFields(new Fields(TestHiveBolt.partNames));
        HiveOptions hiveOptions = withTxnsPerBatch(2).withBatchSize(2);
        HiveBolt failingBolt = new TestHiveBolt.FlushFailureHiveBolt(hiveOptions);
        failingBolt.prepare(config, null, new OutputCollector(collector));
        Tuple tuple1 = generateTestTuple(1, "SJC", "Sunnyvale", "CA");
        Tuple tuple2 = generateTestTuple(2, "SFO", "San Jose", "CA");
        failingBolt.execute(tuple1);
        failingBolt.execute(tuple2);
        Mockito.verify(collector, Mockito.never()).ack(tuple1);
        Mockito.verify(collector, Mockito.never()).ack(tuple2);
        failingBolt.cleanup();
    }

    @Test
    public void testTickTuple() {
        JsonRecordHiveMapper mapper = new JsonRecordHiveMapper().withColumnFields(new Fields(colNames1)).withPartitionFields(new Fields(TestHiveBolt.partNames));
        HiveOptions hiveOptions = withTxnsPerBatch(2).withBatchSize(2);
        bolt = new TestHiveBolt.TestingHiveBolt(hiveOptions);
        bolt.prepare(config, null, new OutputCollector(collector));
        Tuple tuple1 = generateTestTuple(1, "SJC", "Sunnyvale", "CA");
        Tuple tuple2 = generateTestTuple(2, "SFO", "San Jose", "CA");
        bolt.execute(tuple1);
        // The tick should cause tuple1 to be ack'd
        Tuple mockTick = MockTupleHelpers.mockTickTuple();
        bolt.execute(mockTick);
        Mockito.verify(collector).ack(tuple1);
        // The second tuple should NOT be ack'd because the batch should be cleared and this will be
        // the first transaction in the new batch
        bolt.execute(tuple2);
        Mockito.verify(collector, Mockito.never()).ack(tuple2);
        cleanup();
    }

    @Test
    public void testNoTickEmptyBatches() throws Exception {
        JsonRecordHiveMapper mapper = new JsonRecordHiveMapper().withColumnFields(new Fields(colNames1)).withPartitionFields(new Fields(TestHiveBolt.partNames));
        HiveOptions hiveOptions = withTxnsPerBatch(2).withBatchSize(2);
        bolt = new TestHiveBolt.TestingHiveBolt(hiveOptions);
        bolt.prepare(config, null, new OutputCollector(collector));
        // The tick should NOT cause any acks since the batch was empty except for acking itself
        Tuple mockTick = MockTupleHelpers.mockTickTuple();
        bolt.execute(mockTick);
        Mockito.verifyZeroInteractions(collector);
        cleanup();
    }

    @Test
    public void testMultiPartitionTuples() throws Exception {
        DelimitedRecordHiveMapper mapper = new DelimitedRecordHiveMapper().withColumnFields(new Fields(colNames)).withPartitionFields(new Fields(TestHiveBolt.partNames));
        HiveOptions hiveOptions = withTxnsPerBatch(10).withBatchSize(10);
        bolt = new TestHiveBolt.TestingHiveBolt(hiveOptions);
        bolt.prepare(config, null, new OutputCollector(collector));
        Integer id = 1;
        String msg = "test";
        String city = "San Jose";
        String state = "CA";
        List<Tuple> tuples = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Tuple tuple = generateTestTuple(id, msg, city, state);
            tuples.add(tuple);
            bolt.execute(tuple);
        }
        for (Tuple t : tuples) {
            Mockito.verify(collector).ack(t);
        }
        List<String> partVals = Lists.newArrayList(city, state);
        List<byte[]> recordsWritten = bolt.getRecordWritten(partVals);
        Assert.assertNotNull(recordsWritten);
        Assert.assertEquals(100, recordsWritten.size());
        byte[] mapped = generateDelimiteredRecord(Lists.newArrayList(id, msg), mapper.getFieldDelimiter());
        for (byte[] record : recordsWritten) {
            Assert.assertArrayEquals(mapped, record);
        }
        cleanup();
    }

    private static class TestingHiveBolt extends HiveBolt {
        protected Map<List<String>, List<byte[]>> partitionValuesToWrittenRecords = new HashMap<>();

        public TestingHiveBolt(HiveOptions options) {
            super(options);
        }

        @Override
        HiveWriter getOrCreateWriter(final HiveEndPoint endPoint) throws ConnectFailure, InterruptedException {
            HiveWriter writer = allWriters.get(endPoint);
            if (writer == null) {
                // always provide mocked HiveWriter
                writer = Mockito.mock(HiveWriter.class);
                try {
                    Mockito.doAnswer(new Answer<Void>() {
                        @Override
                        public Void answer(InvocationOnMock invocation) throws Throwable {
                            Object[] arguments = invocation.getArguments();
                            List<String> partitionVals = endPoint.partitionVals;
                            List<byte[]> writtenRecords = partitionValuesToWrittenRecords.get(partitionVals);
                            if (writtenRecords == null) {
                                writtenRecords = new ArrayList<>();
                                partitionValuesToWrittenRecords.put(partitionVals, writtenRecords);
                            }
                            writtenRecords.add(((byte[]) (arguments[0])));
                            return null;
                        }
                    }).when(writer).write(ArgumentMatchers.any(byte[].class));
                } catch (Exception exc) {
                    throw new RuntimeException(exc);
                }
            }
            return writer;
        }

        public Map<List<String>, List<byte[]>> getPartitionValuesToWrittenRecords() {
            return partitionValuesToWrittenRecords;
        }

        public List<byte[]> getRecordWritten(List<String> partitionValues) {
            return partitionValuesToWrittenRecords.get(partitionValues);
        }
    }

    private static class FlushFailureHiveBolt extends TestHiveBolt.TestingHiveBolt {
        public FlushFailureHiveBolt(HiveOptions options) {
            super(options);
        }

        @Override
        void flushAllWriters(boolean rollToNext) throws CommitFailure, TxnBatchFailure, TxnFailure, InterruptedException {
            if (rollToNext) {
                throw new InterruptedException();
            } else {
                super.flushAllWriters(false);
            }
        }
    }
}

