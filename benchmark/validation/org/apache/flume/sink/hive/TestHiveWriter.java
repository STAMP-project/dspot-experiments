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


import HiveConf.ConfVars.METASTOREURIS;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import junit.framework.Assert;
import org.apache.flume.Context;
import org.apache.flume.event.SimpleEvent;
import org.apache.flume.instrumentation.SinkCounter;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.Driver;
import org.apache.hadoop.hive.ql.session.SessionState;
import org.apache.hive.hcatalog.streaming.HiveEndPoint;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class TestHiveWriter {
    static final String dbName = "testing";

    static final String tblName = "alerts";

    public static final String PART1_NAME = "continent";

    public static final String PART2_NAME = "country";

    public static final String[] partNames = new String[]{ TestHiveWriter.PART1_NAME, TestHiveWriter.PART2_NAME };

    private static final String COL1 = "id";

    private static final String COL2 = "msg";

    final String[] colNames = new String[]{ TestHiveWriter.COL1, TestHiveWriter.COL2 };

    private String[] colTypes = new String[]{ "int", "string" };

    private static final String PART1_VALUE = "Asia";

    private static final String PART2_VALUE = "India";

    private final ArrayList<String> partVals;

    private final String metaStoreURI;

    private HiveDelimitedTextSerializer serializer;

    private final HiveConf conf;

    private ExecutorService callTimeoutPool;

    int timeout = 10000;// msec


    @Rule
    public TemporaryFolder dbFolder = new TemporaryFolder();

    private final Driver driver;

    public TestHiveWriter() throws Exception {
        partVals = new ArrayList<String>(2);
        partVals.add(TestHiveWriter.PART1_VALUE);
        partVals.add(TestHiveWriter.PART2_VALUE);
        metaStoreURI = null;
        int callTimeoutPoolSize = 1;
        callTimeoutPool = Executors.newFixedThreadPool(callTimeoutPoolSize, new ThreadFactoryBuilder().setNameFormat("hiveWriterTest").build());
        // 1) Start metastore
        conf = new HiveConf(this.getClass());
        TestUtil.setConfValues(conf);
        if ((metaStoreURI) != null) {
            conf.setVar(METASTOREURIS, metaStoreURI);
        }
        // 2) Setup Hive client
        SessionState.start(new org.apache.hadoop.hive.cli.CliSessionState(conf));
        driver = new Driver(conf);
    }

    @Test
    public void testInstantiate() throws Exception {
        HiveEndPoint endPoint = new HiveEndPoint(metaStoreURI, TestHiveWriter.dbName, TestHiveWriter.tblName, partVals);
        SinkCounter sinkCounter = new SinkCounter(this.getClass().getName());
        HiveWriter writer = new HiveWriter(endPoint, 10, true, timeout, callTimeoutPool, "flumetest", serializer, sinkCounter);
        writer.close();
    }

    @Test
    public void testWriteBasic() throws Exception {
        HiveEndPoint endPoint = new HiveEndPoint(metaStoreURI, TestHiveWriter.dbName, TestHiveWriter.tblName, partVals);
        SinkCounter sinkCounter = new SinkCounter(this.getClass().getName());
        HiveWriter writer = new HiveWriter(endPoint, 10, true, timeout, callTimeoutPool, "flumetest", serializer, sinkCounter);
        writeEvents(writer, 3);
        writer.flush(false);
        writer.close();
        checkRecordCountInTable(3);
    }

    @Test
    public void testWriteMultiFlush() throws Exception {
        HiveEndPoint endPoint = new HiveEndPoint(metaStoreURI, TestHiveWriter.dbName, TestHiveWriter.tblName, partVals);
        SinkCounter sinkCounter = new SinkCounter(this.getClass().getName());
        HiveWriter writer = new HiveWriter(endPoint, 10, true, timeout, callTimeoutPool, "flumetest", serializer, sinkCounter);
        checkRecordCountInTable(0);
        SimpleEvent event = new SimpleEvent();
        String REC1 = "1,xyz,Hello world,abc";
        event.setBody(REC1.getBytes());
        writer.write(event);
        checkRecordCountInTable(0);
        writer.flush(true);
        checkRecordCountInTable(1);
        String REC2 = "2,xyz,Hello world,abc";
        event.setBody(REC2.getBytes());
        writer.write(event);
        checkRecordCountInTable(1);
        writer.flush(true);
        checkRecordCountInTable(2);
        String REC3 = "3,xyz,Hello world,abc";
        event.setBody(REC3.getBytes());
        writer.write(event);
        writer.flush(true);
        checkRecordCountInTable(3);
        writer.close();
        checkRecordCountInTable(3);
    }

    @Test
    public void testTxnBatchConsumption() throws Exception {
        // get a small txn batch and consume it, then roll to new batch, very
        // the number of remaining txns to ensure Txns are not accidentally skipped
        HiveEndPoint endPoint = new HiveEndPoint(metaStoreURI, TestHiveWriter.dbName, TestHiveWriter.tblName, partVals);
        SinkCounter sinkCounter = new SinkCounter(this.getClass().getName());
        int txnPerBatch = 3;
        HiveWriter writer = new HiveWriter(endPoint, txnPerBatch, true, timeout, callTimeoutPool, "flumetest", serializer, sinkCounter);
        Assert.assertEquals(writer.getRemainingTxns(), 2);
        writer.flush(true);
        Assert.assertEquals(writer.getRemainingTxns(), 1);
        writer.flush(true);
        Assert.assertEquals(writer.getRemainingTxns(), 0);
        writer.flush(true);
        // flip over to next batch
        Assert.assertEquals(writer.getRemainingTxns(), 2);
        writer.flush(true);
        Assert.assertEquals(writer.getRemainingTxns(), 1);
        writer.close();
    }

    /**
     * Sets up input fields to have same order as table columns,
     * Also sets the separator on serde to be same as i/p field separator
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testInOrderWrite() throws Exception {
        HiveEndPoint endPoint = new HiveEndPoint(metaStoreURI, TestHiveWriter.dbName, TestHiveWriter.tblName, partVals);
        SinkCounter sinkCounter = new SinkCounter(this.getClass().getName());
        int timeout = 5000;// msec

        HiveDelimitedTextSerializer serializer2 = new HiveDelimitedTextSerializer();
        Context ctx = new Context();
        ctx.put("serializer.fieldnames", (((TestHiveWriter.COL1) + ",") + (TestHiveWriter.COL2)));
        ctx.put("serializer.serdeSeparator", ",");
        serializer2.configure(ctx);
        HiveWriter writer = new HiveWriter(endPoint, 10, true, timeout, callTimeoutPool, "flumetest", serializer2, sinkCounter);
        SimpleEvent event = new SimpleEvent();
        event.setBody("1,Hello world 1".getBytes());
        writer.write(event);
        event.setBody("2,Hello world 2".getBytes());
        writer.write(event);
        event.setBody("3,Hello world 3".getBytes());
        writer.write(event);
        writer.flush(false);
        writer.close();
    }

    @Test
    public void testSerdeSeparatorCharParsing() throws Exception {
        HiveEndPoint endPoint = new HiveEndPoint(metaStoreURI, TestHiveWriter.dbName, TestHiveWriter.tblName, partVals);
        SinkCounter sinkCounter = new SinkCounter(this.getClass().getName());
        int timeout = 10000;// msec

        // 1)  single character serdeSeparator
        HiveDelimitedTextSerializer serializer1 = new HiveDelimitedTextSerializer();
        Context ctx = new Context();
        ctx.put("serializer.fieldnames", (((TestHiveWriter.COL1) + ",") + (TestHiveWriter.COL2)));
        ctx.put("serializer.serdeSeparator", ",");
        serializer1.configure(ctx);
        // show not throw
        // 2) special character as serdeSeparator
        HiveDelimitedTextSerializer serializer2 = new HiveDelimitedTextSerializer();
        ctx = new Context();
        ctx.put("serializer.fieldnames", (((TestHiveWriter.COL1) + ",") + (TestHiveWriter.COL2)));
        ctx.put("serializer.serdeSeparator", "\'\t\'");
        serializer2.configure(ctx);
        // show not throw
        // 2) bad spec as serdeSeparator
        HiveDelimitedTextSerializer serializer3 = new HiveDelimitedTextSerializer();
        ctx = new Context();
        ctx.put("serializer.fieldnames", (((TestHiveWriter.COL1) + ",") + (TestHiveWriter.COL2)));
        ctx.put("serializer.serdeSeparator", "ab");
        try {
            serializer3.configure(ctx);
            Assert.assertTrue("Bad serdeSeparator character was accepted", false);
        } catch (Exception e) {
            // expect an exception
        }
    }

    @Test
    public void testSecondWriterBeforeFirstCommits() throws Exception {
        // here we open a new writer while the first is still writing (not committed)
        HiveEndPoint endPoint1 = new HiveEndPoint(metaStoreURI, TestHiveWriter.dbName, TestHiveWriter.tblName, partVals);
        ArrayList<String> partVals2 = new ArrayList<String>(2);
        partVals2.add(TestHiveWriter.PART1_VALUE);
        partVals2.add("Nepal");
        HiveEndPoint endPoint2 = new HiveEndPoint(metaStoreURI, TestHiveWriter.dbName, TestHiveWriter.tblName, partVals2);
        SinkCounter sinkCounter1 = new SinkCounter(this.getClass().getName());
        SinkCounter sinkCounter2 = new SinkCounter(this.getClass().getName());
        HiveWriter writer1 = new HiveWriter(endPoint1, 10, true, timeout, callTimeoutPool, "flumetest", serializer, sinkCounter1);
        writeEvents(writer1, 3);
        HiveWriter writer2 = new HiveWriter(endPoint2, 10, true, timeout, callTimeoutPool, "flumetest", serializer, sinkCounter2);
        writeEvents(writer2, 3);
        writer2.flush(false);// commit

        writer1.flush(false);// commit

        writer1.close();
        writer2.close();
    }

    @Test
    public void testSecondWriterAfterFirstCommits() throws Exception {
        // here we open a new writer after the first writer has committed one txn
        HiveEndPoint endPoint1 = new HiveEndPoint(metaStoreURI, TestHiveWriter.dbName, TestHiveWriter.tblName, partVals);
        ArrayList<String> partVals2 = new ArrayList<String>(2);
        partVals2.add(TestHiveWriter.PART1_VALUE);
        partVals2.add("Nepal");
        HiveEndPoint endPoint2 = new HiveEndPoint(metaStoreURI, TestHiveWriter.dbName, TestHiveWriter.tblName, partVals2);
        SinkCounter sinkCounter1 = new SinkCounter(this.getClass().getName());
        SinkCounter sinkCounter2 = new SinkCounter(this.getClass().getName());
        HiveWriter writer1 = new HiveWriter(endPoint1, 10, true, timeout, callTimeoutPool, "flumetest", serializer, sinkCounter1);
        writeEvents(writer1, 3);
        writer1.flush(false);// commit

        HiveWriter writer2 = new HiveWriter(endPoint2, 10, true, timeout, callTimeoutPool, "flumetest", serializer, sinkCounter2);
        writeEvents(writer2, 3);
        writer2.flush(false);// commit

        writer1.close();
        writer2.close();
    }
}

