/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.sink.hive;


import HiveDelimitedTextSerializer.ALIAS;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import junit.framework.Assert;
import org.apache.flume.Channel;
import org.apache.flume.ChannelException;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Transaction;
import org.apache.flume.channel.BasicTransactionSemantics;
import org.apache.flume.event.SimpleEvent;
import org.apache.flume.instrumentation.SinkCounter;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.txn.TxnDbUtil;
import org.apache.hadoop.hive.ql.CommandNeedRetryException;
import org.apache.hadoop.hive.ql.Driver;
import org.apache.hadoop.hive.ql.session.SessionState;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestHiveSink {
    // 1)  partitioned table
    static final String dbName = "testing";

    static final String tblName = "alerts";

    public static final String PART1_NAME = "continent";

    public static final String PART2_NAME = "country";

    public static final String[] partNames = new String[]{ TestHiveSink.PART1_NAME, TestHiveSink.PART2_NAME };

    private static final String COL1 = "id";

    private static final String COL2 = "msg";

    final String[] colNames = new String[]{ TestHiveSink.COL1, TestHiveSink.COL2 };

    private String[] colTypes = new String[]{ "int", "string" };

    private static final String PART1_VALUE = "Asia";

    private static final String PART2_VALUE = "India";

    private final ArrayList<String> partitionVals;

    // 2) un-partitioned table
    static final String dbName2 = "testing2";

    static final String tblName2 = "alerts2";

    final String[] colNames2 = new String[]{ TestHiveSink.COL1, TestHiveSink.COL2 };

    private String[] colTypes2 = new String[]{ "int", "string" };

    HiveSink sink = new HiveSink();

    private final HiveConf conf;

    private final Driver driver;

    final String metaStoreURI;

    @Rule
    public TemporaryFolder dbFolder = new TemporaryFolder();

    private static final Logger LOG = LoggerFactory.getLogger(HiveSink.class);

    public TestHiveSink() throws Exception {
        partitionVals = new ArrayList<String>(2);
        partitionVals.add(TestHiveSink.PART1_VALUE);
        partitionVals.add(TestHiveSink.PART2_VALUE);
        metaStoreURI = "null";
        conf = new HiveConf(this.getClass());
        TestUtil.setConfValues(conf);
        // 1) prepare hive
        TxnDbUtil.cleanDb();
        TxnDbUtil.prepDb();
        // 2) Setup Hive client
        SessionState.start(new org.apache.hadoop.hive.cli.CliSessionState(conf));
        driver = new Driver(conf);
    }

    @Test
    public void testSingleWriterSimplePartitionedTable() throws Exception {
        testSingleWriter(true, TestHiveSink.dbName, TestHiveSink.tblName, null);
    }

    @Test
    public void testSingleWriterSimpleUnPartitionedTable() throws Exception {
        TestUtil.dropDB(conf, TestHiveSink.dbName2);
        String dbLocation = (dbFolder.newFolder(TestHiveSink.dbName2).getCanonicalPath()) + ".db";
        dbLocation = dbLocation.replaceAll("\\\\", "/");// for windows paths

        TestUtil.createDbAndTable(driver, TestHiveSink.dbName2, TestHiveSink.tblName2, null, colNames2, colTypes2, null, dbLocation);
        try {
            testSingleWriter(false, TestHiveSink.dbName2, TestHiveSink.tblName2, null);
        } finally {
            TestUtil.dropDB(conf, TestHiveSink.dbName2);
        }
    }

    @Test
    public void testSingleWriterUseHeaders() throws Exception {
        String[] colNames = new String[]{ TestHiveSink.COL1, TestHiveSink.COL2 };
        String PART1_NAME = "country";
        String PART2_NAME = "hour";
        String[] partNames = new String[]{ PART1_NAME, PART2_NAME };
        List<String> partitionVals = null;
        String PART1_VALUE = ("%{" + PART1_NAME) + "}";
        String PART2_VALUE = "%y-%m-%d-%k";
        partitionVals = new ArrayList<String>(2);
        partitionVals.add(PART1_VALUE);
        partitionVals.add(PART2_VALUE);
        String tblName = "hourlydata";
        TestUtil.dropDB(conf, TestHiveSink.dbName2);
        String dbLocation = (dbFolder.newFolder(TestHiveSink.dbName2).getCanonicalPath()) + ".db";
        dbLocation = dbLocation.replaceAll("\\\\", "/");// for windows paths

        TestUtil.createDbAndTable(driver, TestHiveSink.dbName2, tblName, partitionVals, colNames, colTypes, partNames, dbLocation);
        int totalRecords = 4;
        int batchSize = 2;
        int batchCount = totalRecords / batchSize;
        Context context = new Context();
        context.put("hive.metastore", metaStoreURI);
        context.put("hive.database", TestHiveSink.dbName2);
        context.put("hive.table", tblName);
        context.put("hive.partition", ((PART1_VALUE + ",") + PART2_VALUE));
        context.put("autoCreatePartitions", "true");
        context.put("useLocalTimeStamp", "false");
        context.put("batchSize", ("" + batchSize));
        context.put("serializer", ALIAS);
        context.put("serializer.fieldnames", ((((TestHiveSink.COL1) + ",,") + (TestHiveSink.COL2)) + ","));
        context.put("heartBeatInterval", "0");
        Channel channel = TestHiveSink.startSink(sink, context);
        Calendar eventDate = Calendar.getInstance();
        List<String> bodies = Lists.newArrayList();
        // push events in two batches - two per batch. each batch is diff hour
        Transaction txn = channel.getTransaction();
        txn.begin();
        for (int j = 1; j <= totalRecords; j++) {
            Event event = new SimpleEvent();
            String body = j + ",blah,This is a log message,other stuff";
            event.setBody(body.getBytes());
            eventDate.clear();
            eventDate.set(2014, 3, 3, (j % batchCount), 1);// yy mm dd hh mm

            event.getHeaders().put("timestamp", String.valueOf(eventDate.getTimeInMillis()));
            event.getHeaders().put(PART1_NAME, "Asia");
            bodies.add(body);
            channel.put(event);
        }
        // execute sink to process the events
        txn.commit();
        txn.close();
        checkRecordCountInTable(0, TestHiveSink.dbName2, tblName);
        for (int i = 0; i < batchCount; i++) {
            sink.process();
        }
        checkRecordCountInTable(totalRecords, TestHiveSink.dbName2, tblName);
        sink.stop();
        // verify counters
        SinkCounter counter = sink.getCounter();
        Assert.assertEquals(2, counter.getConnectionCreatedCount());
        Assert.assertEquals(2, counter.getConnectionClosedCount());
        Assert.assertEquals(2, counter.getBatchCompleteCount());
        Assert.assertEquals(0, counter.getBatchEmptyCount());
        Assert.assertEquals(0, counter.getConnectionFailedCount());
        Assert.assertEquals(4, counter.getEventDrainAttemptCount());
        Assert.assertEquals(4, counter.getEventDrainSuccessCount());
    }

    @Test
    public void testHeartBeat() throws IOException, EventDeliveryException, CommandNeedRetryException {
        int batchSize = 2;
        int batchCount = 3;
        int totalRecords = batchCount * batchSize;
        Context context = new Context();
        context.put("hive.metastore", metaStoreURI);
        context.put("hive.database", TestHiveSink.dbName);
        context.put("hive.table", TestHiveSink.tblName);
        context.put("hive.partition", (((TestHiveSink.PART1_VALUE) + ",") + (TestHiveSink.PART2_VALUE)));
        context.put("autoCreatePartitions", "true");
        context.put("batchSize", ("" + batchSize));
        context.put("serializer", ALIAS);
        context.put("serializer.fieldnames", ((((TestHiveSink.COL1) + ",,") + (TestHiveSink.COL2)) + ","));
        context.put("hive.txnsPerBatchAsk", "20");
        context.put("heartBeatInterval", "3");// heartbeat in seconds

        Channel channel = TestHiveSink.startSink(sink, context);
        List<String> bodies = Lists.newArrayList();
        // push the events in two batches
        for (int i = 0; i < batchCount; i++) {
            Transaction txn = channel.getTransaction();
            txn.begin();
            for (int j = 1; j <= batchSize; j++) {
                Event event = new SimpleEvent();
                String body = (i * j) + ",blah,This is a log message,other stuff";
                event.setBody(body.getBytes());
                bodies.add(body);
                channel.put(event);
            }
            // execute sink to process the events
            txn.commit();
            txn.close();
            sink.process();
            sleep(3000);// allow heartbeat to happen

        }
        sink.stop();
        checkRecordCountInTable(totalRecords, TestHiveSink.dbName, TestHiveSink.tblName);
    }

    @Test
    public void testJsonSerializer() throws Exception {
        int batchSize = 2;
        int batchCount = 2;
        int totalRecords = batchCount * batchSize;
        Context context = new Context();
        context.put("hive.metastore", metaStoreURI);
        context.put("hive.database", TestHiveSink.dbName);
        context.put("hive.table", TestHiveSink.tblName);
        context.put("hive.partition", (((TestHiveSink.PART1_VALUE) + ",") + (TestHiveSink.PART2_VALUE)));
        context.put("autoCreatePartitions", "true");
        context.put("batchSize", ("" + batchSize));
        context.put("serializer", HiveJsonSerializer.ALIAS);
        context.put("serializer.fieldnames", ((((TestHiveSink.COL1) + ",,") + (TestHiveSink.COL2)) + ","));
        context.put("heartBeatInterval", "0");
        Channel channel = TestHiveSink.startSink(sink, context);
        List<String> bodies = Lists.newArrayList();
        // push the events in two batches
        for (int i = 0; i < batchCount; i++) {
            Transaction txn = channel.getTransaction();
            txn.begin();
            for (int j = 1; j <= batchSize; j++) {
                Event event = new SimpleEvent();
                String body = "{\"id\" : 1, \"msg\" : \"using json serializer\"}";
                event.setBody(body.getBytes());
                bodies.add(body);
                channel.put(event);
            }
            // execute sink to process the events
            txn.commit();
            txn.close();
            sink.process();
        }
        checkRecordCountInTable(totalRecords, TestHiveSink.dbName, TestHiveSink.tblName);
        sink.stop();
        checkRecordCountInTable(totalRecords, TestHiveSink.dbName, TestHiveSink.tblName);
    }

    @Test
    public void testErrorCounter() throws Exception {
        Channel channel = Mockito.mock(Channel.class);
        Mockito.when(channel.take()).thenThrow(new ChannelException("dummy"));
        Transaction transaction = Mockito.mock(BasicTransactionSemantics.class);
        Mockito.when(channel.getTransaction()).thenReturn(transaction);
        try {
            testSingleWriter(true, TestHiveSink.dbName, TestHiveSink.tblName, channel);
        } catch (EventDeliveryException e) {
            // Expected exception
        }
        SinkCounter sinkCounter = ((SinkCounter) (Whitebox.getInternalState(sink, "sinkCounter")));
        Assert.assertEquals(1, sinkCounter.getChannelReadFail());
    }
}

