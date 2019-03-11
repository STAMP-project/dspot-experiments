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
package org.apache.flume.sink.hbase;


import HBaseSinkConfigurationConstants.ZK_QUORUM;
import HBaseSinkConfigurationConstants.ZK_ZNODE_PARENT;
import HConstants.ZOOKEEPER_ZNODE_PARENT;
import com.google.common.primitives.Longs;
import java.lang.management.OperatingSystemMXBean;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.FlumeException;
import org.apache.flume.Sink.Status;
import org.apache.flume.Transaction;
import org.apache.flume.channel.MemoryChannel;
import org.apache.flume.conf.Configurables;
import org.apache.flume.event.EventBuilder;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.zookeeper.ZKConfig;
import org.junit.Assert;
import org.junit.Test;


public class TestAsyncHBaseSink {
    private static HBaseTestingUtility testUtility = new HBaseTestingUtility();

    private static String tableName = "TestHbaseSink";

    private static String columnFamily = "TestColumnFamily";

    private static String inColumn = "iCol";

    private static String plCol = "pCol";

    private static Context ctx = new Context();

    private static String valBase = "testing hbase sink: jham";

    private boolean deleteTable = true;

    private static OperatingSystemMXBean os;

    @Test
    public void testOneEventWithDefaults() throws Exception {
        Map<String, String> ctxMap = new HashMap<String, String>();
        ctxMap.put("table", TestAsyncHBaseSink.tableName);
        ctxMap.put("columnFamily", TestAsyncHBaseSink.columnFamily);
        ctxMap.put("serializer", "org.apache.flume.sink.hbase.SimpleAsyncHbaseEventSerializer");
        ctxMap.put("keep-alive", "0");
        ctxMap.put("timeout", "10000");
        Context tmpctx = new Context();
        tmpctx.putAll(ctxMap);
        TestAsyncHBaseSink.testUtility.createTable(TestAsyncHBaseSink.tableName.getBytes(), TestAsyncHBaseSink.columnFamily.getBytes());
        deleteTable = true;
        AsyncHBaseSink sink = new AsyncHBaseSink(TestAsyncHBaseSink.testUtility.getConfiguration());
        Configurables.configure(sink, tmpctx);
        Channel channel = new MemoryChannel();
        Configurables.configure(channel, tmpctx);
        sink.setChannel(channel);
        sink.start();
        Transaction tx = channel.getTransaction();
        tx.begin();
        Event e = EventBuilder.withBody(Bytes.toBytes(TestAsyncHBaseSink.valBase));
        channel.put(e);
        tx.commit();
        tx.close();
        Assert.assertFalse(sink.isConfNull());
        sink.process();
        sink.stop();
        HTable table = new HTable(TestAsyncHBaseSink.testUtility.getConfiguration(), TestAsyncHBaseSink.tableName);
        byte[][] results = getResults(table, 1);
        byte[] out = results[0];
        Assert.assertArrayEquals(e.getBody(), out);
        out = results[1];
        Assert.assertArrayEquals(Longs.toByteArray(1), out);
    }

    @Test
    public void testOneEvent() throws Exception {
        TestAsyncHBaseSink.testUtility.createTable(TestAsyncHBaseSink.tableName.getBytes(), TestAsyncHBaseSink.columnFamily.getBytes());
        deleteTable = true;
        AsyncHBaseSink sink = new AsyncHBaseSink(TestAsyncHBaseSink.testUtility.getConfiguration());
        Configurables.configure(sink, TestAsyncHBaseSink.ctx);
        Channel channel = new MemoryChannel();
        Configurables.configure(channel, TestAsyncHBaseSink.ctx);
        sink.setChannel(channel);
        sink.start();
        Transaction tx = channel.getTransaction();
        tx.begin();
        Event e = EventBuilder.withBody(Bytes.toBytes(TestAsyncHBaseSink.valBase));
        channel.put(e);
        tx.commit();
        tx.close();
        Assert.assertFalse(sink.isConfNull());
        sink.process();
        sink.stop();
        HTable table = new HTable(TestAsyncHBaseSink.testUtility.getConfiguration(), TestAsyncHBaseSink.tableName);
        byte[][] results = getResults(table, 1);
        byte[] out = results[0];
        Assert.assertArrayEquals(e.getBody(), out);
        out = results[1];
        Assert.assertArrayEquals(Longs.toByteArray(1), out);
    }

    @Test
    public void testThreeEvents() throws Exception {
        TestAsyncHBaseSink.testUtility.createTable(TestAsyncHBaseSink.tableName.getBytes(), TestAsyncHBaseSink.columnFamily.getBytes());
        deleteTable = true;
        AsyncHBaseSink sink = new AsyncHBaseSink(TestAsyncHBaseSink.testUtility.getConfiguration());
        Configurables.configure(sink, TestAsyncHBaseSink.ctx);
        Channel channel = new MemoryChannel();
        Configurables.configure(channel, TestAsyncHBaseSink.ctx);
        sink.setChannel(channel);
        sink.start();
        Transaction tx = channel.getTransaction();
        tx.begin();
        for (int i = 0; i < 3; i++) {
            Event e = EventBuilder.withBody(Bytes.toBytes((((TestAsyncHBaseSink.valBase) + "-") + i)));
            channel.put(e);
        }
        tx.commit();
        tx.close();
        Assert.assertFalse(sink.isConfNull());
        sink.process();
        sink.stop();
        HTable table = new HTable(TestAsyncHBaseSink.testUtility.getConfiguration(), TestAsyncHBaseSink.tableName);
        byte[][] results = getResults(table, 3);
        byte[] out;
        int found = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (Arrays.equals(results[j], Bytes.toBytes((((TestAsyncHBaseSink.valBase) + "-") + i)))) {
                    found++;
                    break;
                }
            }
        }
        Assert.assertEquals(3, found);
        out = results[3];
        Assert.assertArrayEquals(Longs.toByteArray(3), out);
    }

    // This will without FLUME-1842's timeout fix - but with FLUME-1842's testing
    // oriented changes to the callback classes and using single threaded executor
    // for tests.
    @Test(expected = EventDeliveryException.class)
    public void testTimeOut() throws Exception {
        TestAsyncHBaseSink.testUtility.createTable(TestAsyncHBaseSink.tableName.getBytes(), TestAsyncHBaseSink.columnFamily.getBytes());
        deleteTable = true;
        AsyncHBaseSink sink = new AsyncHBaseSink(TestAsyncHBaseSink.testUtility.getConfiguration(), true, false);
        Configurables.configure(sink, TestAsyncHBaseSink.ctx);
        Channel channel = new MemoryChannel();
        Configurables.configure(channel, TestAsyncHBaseSink.ctx);
        sink.setChannel(channel);
        channel.start();
        sink.start();
        Transaction tx = channel.getTransaction();
        tx.begin();
        for (int i = 0; i < 3; i++) {
            Event e = EventBuilder.withBody(Bytes.toBytes((((TestAsyncHBaseSink.valBase) + "-") + i)));
            channel.put(e);
        }
        tx.commit();
        tx.close();
        Assert.assertFalse(sink.isConfNull());
        sink.process();
        Assert.fail();
    }

    @Test
    public void testMultipleBatches() throws Exception {
        TestAsyncHBaseSink.testUtility.createTable(TestAsyncHBaseSink.tableName.getBytes(), TestAsyncHBaseSink.columnFamily.getBytes());
        deleteTable = true;
        TestAsyncHBaseSink.ctx.put("batchSize", "2");
        AsyncHBaseSink sink = new AsyncHBaseSink(TestAsyncHBaseSink.testUtility.getConfiguration());
        Configurables.configure(sink, TestAsyncHBaseSink.ctx);
        // Reset the context to a higher batchSize
        TestAsyncHBaseSink.ctx.put("batchSize", "100");
        Channel channel = new MemoryChannel();
        Configurables.configure(channel, TestAsyncHBaseSink.ctx);
        sink.setChannel(channel);
        sink.start();
        Transaction tx = channel.getTransaction();
        tx.begin();
        for (int i = 0; i < 3; i++) {
            Event e = EventBuilder.withBody(Bytes.toBytes((((TestAsyncHBaseSink.valBase) + "-") + i)));
            channel.put(e);
        }
        tx.commit();
        tx.close();
        int count = 0;
        Status status = Status.READY;
        while (status != (Status.BACKOFF)) {
            count++;
            status = sink.process();
        } 
        Assert.assertFalse(sink.isConfNull());
        sink.stop();
        Assert.assertEquals(2, count);
        HTable table = new HTable(TestAsyncHBaseSink.testUtility.getConfiguration(), TestAsyncHBaseSink.tableName);
        byte[][] results = getResults(table, 3);
        byte[] out;
        int found = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (Arrays.equals(results[j], Bytes.toBytes((((TestAsyncHBaseSink.valBase) + "-") + i)))) {
                    found++;
                    break;
                }
            }
        }
        Assert.assertEquals(3, found);
        out = results[3];
        Assert.assertArrayEquals(Longs.toByteArray(3), out);
    }

    @Test
    public void testMultipleBatchesBatchIncrementsWithCoalescing() throws Exception {
        doTestMultipleBatchesBatchIncrements(true);
    }

    @Test
    public void testMultipleBatchesBatchIncrementsNoCoalescing() throws Exception {
        doTestMultipleBatchesBatchIncrements(false);
    }

    @Test
    public void testWithoutConfigurationObject() throws Exception {
        TestAsyncHBaseSink.testUtility.createTable(TestAsyncHBaseSink.tableName.getBytes(), TestAsyncHBaseSink.columnFamily.getBytes());
        deleteTable = true;
        TestAsyncHBaseSink.ctx.put("batchSize", "2");
        TestAsyncHBaseSink.ctx.put(ZK_QUORUM, ZKConfig.getZKQuorumServersString(TestAsyncHBaseSink.testUtility.getConfiguration()));
        TestAsyncHBaseSink.ctx.put(ZK_ZNODE_PARENT, TestAsyncHBaseSink.testUtility.getConfiguration().get(ZOOKEEPER_ZNODE_PARENT));
        AsyncHBaseSink sink = new AsyncHBaseSink();
        Configurables.configure(sink, TestAsyncHBaseSink.ctx);
        // Reset context to values usable by other tests.
        TestAsyncHBaseSink.ctx.put(ZK_QUORUM, null);
        TestAsyncHBaseSink.ctx.put(ZK_ZNODE_PARENT, null);
        TestAsyncHBaseSink.ctx.put("batchSize", "100");
        Channel channel = new MemoryChannel();
        Configurables.configure(channel, TestAsyncHBaseSink.ctx);
        sink.setChannel(channel);
        sink.start();
        Transaction tx = channel.getTransaction();
        tx.begin();
        for (int i = 0; i < 3; i++) {
            Event e = EventBuilder.withBody(Bytes.toBytes((((TestAsyncHBaseSink.valBase) + "-") + i)));
            channel.put(e);
        }
        tx.commit();
        tx.close();
        int count = 0;
        Status status = Status.READY;
        while (status != (Status.BACKOFF)) {
            count++;
            status = sink.process();
        } 
        /* Make sure that the configuration was picked up from the context itself
        and not from a configuration object which was created by the sink.
         */
        Assert.assertTrue(sink.isConfNull());
        sink.stop();
        Assert.assertEquals(2, count);
        HTable table = new HTable(TestAsyncHBaseSink.testUtility.getConfiguration(), TestAsyncHBaseSink.tableName);
        byte[][] results = getResults(table, 3);
        byte[] out;
        int found = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (Arrays.equals(results[j], Bytes.toBytes((((TestAsyncHBaseSink.valBase) + "-") + i)))) {
                    found++;
                    break;
                }
            }
        }
        Assert.assertEquals(3, found);
        out = results[3];
        Assert.assertArrayEquals(Longs.toByteArray(3), out);
    }

    @Test(expected = FlumeException.class)
    public void testMissingTable() throws Exception {
        deleteTable = false;
        TestAsyncHBaseSink.ctx.put("batchSize", "2");
        AsyncHBaseSink sink = new AsyncHBaseSink(TestAsyncHBaseSink.testUtility.getConfiguration());
        Configurables.configure(sink, TestAsyncHBaseSink.ctx);
        // Reset the context to a higher batchSize
        TestAsyncHBaseSink.ctx.put("batchSize", "100");
        Channel channel = new MemoryChannel();
        Configurables.configure(channel, TestAsyncHBaseSink.ctx);
        sink.setChannel(channel);
        sink.start();
        Transaction tx = channel.getTransaction();
        tx.begin();
        for (int i = 0; i < 3; i++) {
            Event e = EventBuilder.withBody(Bytes.toBytes((((TestAsyncHBaseSink.valBase) + "-") + i)));
            channel.put(e);
        }
        tx.commit();
        tx.close();
        sink.process();
        Assert.assertFalse(sink.isConfNull());
        HTable table = new HTable(TestAsyncHBaseSink.testUtility.getConfiguration(), TestAsyncHBaseSink.tableName);
        byte[][] results = getResults(table, 2);
        byte[] out;
        int found = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if (Arrays.equals(results[j], Bytes.toBytes((((TestAsyncHBaseSink.valBase) + "-") + i)))) {
                    found++;
                    break;
                }
            }
        }
        Assert.assertEquals(2, found);
        out = results[2];
        Assert.assertArrayEquals(Longs.toByteArray(2), out);
        sink.process();
        sink.stop();
    }

    /* Before the fix for FLUME-2738, consistently File Descriptors were leaked with at least
    > 10 FDs being leaked for every single shutdown-reinitialize routine
    If there is a leak, then the increase in FDs should be way higher than
    50 and if there is no leak, there should not be any substantial increase in
    FDs. This is over a set of 10 shutdown-reinitialize runs
    This test makes sure that there is no File Descriptor leak, by continuously
    failing transactions and shutting down and reinitializing the client every time
    and this test will fail if a leak is detected
     */
    @Test
    public void testFDLeakOnShutdown() throws Exception {
        if ((getOpenFileDescriptorCount()) < 0) {
            return;
        }
        TestAsyncHBaseSink.testUtility.createTable(TestAsyncHBaseSink.tableName.getBytes(), TestAsyncHBaseSink.columnFamily.getBytes());
        deleteTable = true;
        AsyncHBaseSink sink = new AsyncHBaseSink(TestAsyncHBaseSink.testUtility.getConfiguration(), true, false);
        TestAsyncHBaseSink.ctx.put("maxConsecutiveFails", "1");
        Configurables.configure(sink, TestAsyncHBaseSink.ctx);
        Channel channel = new MemoryChannel();
        Configurables.configure(channel, TestAsyncHBaseSink.ctx);
        sink.setChannel(channel);
        channel.start();
        sink.start();
        Transaction tx = channel.getTransaction();
        tx.begin();
        for (int i = 0; i < 3; i++) {
            Event e = EventBuilder.withBody(Bytes.toBytes((((TestAsyncHBaseSink.valBase) + "-") + i)));
            channel.put(e);
        }
        tx.commit();
        tx.close();
        Assert.assertFalse(sink.isConfNull());
        long initialFDCount = getOpenFileDescriptorCount();
        // Since the isTimeOutTest is set to true, transaction will fail
        // with EventDeliveryException
        for (int i = 0; i < 10; i++) {
            try {
                sink.process();
            } catch (EventDeliveryException ex) {
            }
        }
        long increaseInFD = (getOpenFileDescriptorCount()) - initialFDCount;
        Assert.assertTrue(((("File Descriptor leak detected. FDs have increased by " + increaseInFD) + " from an initial FD count of ") + initialFDCount), (increaseInFD < 50));
    }
}

