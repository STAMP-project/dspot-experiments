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
package org.apache.flume.sink.hbase2;


import HBase2Sink.DebugIncrementsCallback;
import HBase2SinkConfigurationConstants.CONFIG_COALESCE_INCREMENTS;
import HBase2SinkConfigurationConstants.CONFIG_SERIALIZER;
import HBase2SinkConfigurationConstants.ZK_QUORUM;
import HBase2SinkConfigurationConstants.ZK_ZNODE_PARENT;
import HConstants.DEFAULT_ZOOKEEPER_ZNODE_PARENT;
import HConstants.ZOOKEEPER_CLIENT_PORT;
import HConstants.ZOOKEEPER_QUORUM;
import HConstants.ZOOKEEPER_ZNODE_PARENT;
import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Longs;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import org.apache.flume.Channel;
import org.apache.flume.ChannelException;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.FlumeException;
import org.apache.flume.Sink.Status;
import org.apache.flume.Transaction;
import org.apache.flume.channel.MemoryChannel;
import org.apache.flume.conf.Configurables;
import org.apache.flume.conf.ConfigurationException;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.instrumentation.SinkCounter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.zookeeper.ZKConfig;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestHBase2Sink {
    private static final Logger logger = LoggerFactory.getLogger(TestHBase2Sink.class);

    private static final String tableName = "TestHbaseSink";

    private static final String columnFamily = "TestColumnFamily";

    private static final String inColumn = "iCol";

    private static final String plCol = "pCol";

    private static final String valBase = "testing hbase sink: jham";

    private static HBaseTestingUtility testUtility;

    private Configuration conf;

    @Test
    public void testOneEventWithDefaults() throws Exception {
        Context ctx = getContextWithoutIncrementHBaseSerializer();
        HBase2Sink sink = new HBase2Sink(conf);
        Configurables.configure(sink, ctx);
        Channel channel = new MemoryChannel();
        Configurables.configure(channel, new Context());
        sink.setChannel(channel);
        sink.start();
        Transaction tx = channel.getTransaction();
        tx.begin();
        Event e = EventBuilder.withBody(Bytes.toBytes(TestHBase2Sink.valBase));
        channel.put(e);
        tx.commit();
        tx.close();
        sink.process();
        sink.stop();
        try (Connection connection = ConnectionFactory.createConnection(conf);Table table = connection.getTable(TableName.valueOf(TestHBase2Sink.tableName))) {
            byte[][] results = getResults(table, 1);
            byte[] out = results[0];
            Assert.assertArrayEquals(e.getBody(), out);
            out = results[1];
            Assert.assertArrayEquals(Longs.toByteArray(1), out);
        }
    }

    @Test
    public void testOneEvent() throws Exception {
        Context ctx = getContextForSimpleHBase2EventSerializer();
        HBase2Sink sink = new HBase2Sink(conf);
        Configurables.configure(sink, ctx);
        Channel channel = new MemoryChannel();
        Configurables.configure(channel, new Context());
        sink.setChannel(channel);
        sink.start();
        Transaction tx = channel.getTransaction();
        tx.begin();
        Event e = EventBuilder.withBody(Bytes.toBytes(TestHBase2Sink.valBase));
        channel.put(e);
        tx.commit();
        tx.close();
        sink.process();
        sink.stop();
        try (Connection connection = ConnectionFactory.createConnection(conf);Table table = connection.getTable(TableName.valueOf(TestHBase2Sink.tableName))) {
            byte[][] results = getResults(table, 1);
            byte[] out = results[0];
            Assert.assertArrayEquals(e.getBody(), out);
            out = results[1];
            Assert.assertArrayEquals(Longs.toByteArray(1), out);
        }
    }

    @Test
    public void testThreeEvents() throws Exception {
        Context ctx = getContextForSimpleHBase2EventSerializer();
        ctx.put("batchSize", "3");
        HBase2Sink sink = new HBase2Sink(conf);
        Configurables.configure(sink, ctx);
        Channel channel = new MemoryChannel();
        Configurables.configure(channel, new Context());
        sink.setChannel(channel);
        sink.start();
        Transaction tx = channel.getTransaction();
        tx.begin();
        for (int i = 0; i < 3; i++) {
            Event e = EventBuilder.withBody(Bytes.toBytes((((TestHBase2Sink.valBase) + "-") + i)));
            channel.put(e);
        }
        tx.commit();
        tx.close();
        sink.process();
        sink.stop();
        try (Connection connection = ConnectionFactory.createConnection(conf);Table table = connection.getTable(TableName.valueOf(TestHBase2Sink.tableName))) {
            byte[][] results = getResults(table, 3);
            byte[] out;
            int found = 0;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (Arrays.equals(results[j], Bytes.toBytes((((TestHBase2Sink.valBase) + "-") + i)))) {
                        found++;
                        break;
                    }
                }
            }
            Assert.assertEquals(3, found);
            out = results[3];
            Assert.assertArrayEquals(Longs.toByteArray(3), out);
        }
    }

    @Test
    public void testMultipleBatches() throws Exception {
        Context ctx = getContextForSimpleHBase2EventSerializer();
        ctx.put("batchSize", "2");
        HBase2Sink sink = new HBase2Sink(conf);
        Configurables.configure(sink, ctx);
        // Reset the context to a higher batchSize
        ctx.put("batchSize", "100");
        Channel channel = new MemoryChannel();
        Configurables.configure(channel, new Context());
        sink.setChannel(channel);
        sink.start();
        Transaction tx = channel.getTransaction();
        tx.begin();
        for (int i = 0; i < 3; i++) {
            Event e = EventBuilder.withBody(Bytes.toBytes((((TestHBase2Sink.valBase) + "-") + i)));
            channel.put(e);
        }
        tx.commit();
        tx.close();
        int count = 0;
        while ((sink.process()) != (Status.BACKOFF)) {
            count++;
        } 
        sink.stop();
        Assert.assertEquals(2, count);
        try (Connection connection = ConnectionFactory.createConnection(conf)) {
            Table table = connection.getTable(TableName.valueOf(TestHBase2Sink.tableName));
            byte[][] results = getResults(table, 3);
            byte[] out;
            int found = 0;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (Arrays.equals(results[j], Bytes.toBytes((((TestHBase2Sink.valBase) + "-") + i)))) {
                        found++;
                        break;
                    }
                }
            }
            Assert.assertEquals(3, found);
            out = results[3];
            Assert.assertArrayEquals(Longs.toByteArray(3), out);
        }
    }

    @Test(expected = FlumeException.class)
    public void testMissingTable() throws Exception {
        TestHBase2Sink.logger.info("Running testMissingTable()");
        Context ctx = getContextForSimpleHBase2EventSerializer();
        // setUp() will create the table, so we delete it.
        TestHBase2Sink.logger.info("Deleting table {}", TestHBase2Sink.tableName);
        TestHBase2Sink.testUtility.deleteTable(TableName.valueOf(TestHBase2Sink.tableName));
        ctx.put("batchSize", "2");
        HBase2Sink sink = new HBase2Sink(conf);
        Configurables.configure(sink, ctx);
        // Reset the context to a higher batchSize
        ctx.put("batchSize", "100");
        Channel channel = new MemoryChannel();
        Configurables.configure(channel, new Context());
        sink.setChannel(channel);
        TestHBase2Sink.logger.info("Writing data into channel");
        Transaction tx = channel.getTransaction();
        tx.begin();
        for (int i = 0; i < 3; i++) {
            Event e = EventBuilder.withBody(Bytes.toBytes((((TestHBase2Sink.valBase) + "-") + i)));
            channel.put(e);
        }
        tx.commit();
        tx.close();
        TestHBase2Sink.logger.info("Starting sink and processing events");
        try {
            TestHBase2Sink.logger.info("Calling sink.start()");
            sink.start();// This method will throw.

            // We never get here, but we log in case the behavior changes.
            TestHBase2Sink.logger.error("Unexpected error: Calling sink.process()");
            sink.process();
            TestHBase2Sink.logger.error("Unexpected error: Calling sink.stop()");
            sink.stop();
        } finally {
            // Re-create the table so tearDown() doesn't throw.
            TestHBase2Sink.testUtility.createTable(TableName.valueOf(TestHBase2Sink.tableName), TestHBase2Sink.columnFamily.getBytes());
        }
        // FIXME: The test should never get here, the below code doesn't run.
        Assert.fail();
    }

    @Test
    public void testTransactionStateOnChannelException() throws Exception {
        Context ctx = getContextForSimpleHBase2EventSerializer();
        ctx.put("batchSize", "1");
        HBase2Sink sink = new HBase2Sink(conf);
        Configurables.configure(sink, ctx);
        // Reset the context to a higher batchSize
        Channel channel = Mockito.spy(new MemoryChannel());
        Configurables.configure(channel, new Context());
        sink.setChannel(channel);
        sink.start();
        Transaction tx = channel.getTransaction();
        tx.begin();
        Event e = EventBuilder.withBody(Bytes.toBytes((((TestHBase2Sink.valBase) + "-") + 0)));
        channel.put(e);
        tx.commit();
        tx.close();
        Mockito.doThrow(new ChannelException("Mock Exception")).when(channel).take();
        try {
            sink.process();
            Assert.fail("take() method should throw exception");
        } catch (ChannelException ex) {
            Assert.assertEquals("Mock Exception", ex.getMessage());
            SinkCounter sinkCounter = ((SinkCounter) (Whitebox.getInternalState(sink, "sinkCounter")));
            Assert.assertEquals(1, sinkCounter.getChannelReadFail());
        }
        Mockito.doReturn(e).when(channel).take();
        sink.process();
        sink.stop();
        try (Connection connection = ConnectionFactory.createConnection(conf);Table table = connection.getTable(TableName.valueOf(TestHBase2Sink.tableName))) {
            byte[][] results = getResults(table, 1);
            byte[] out = results[0];
            Assert.assertArrayEquals(e.getBody(), out);
            out = results[1];
            Assert.assertArrayEquals(Longs.toByteArray(1), out);
        }
    }

    @Test
    public void testTransactionStateOnSerializationException() throws Exception {
        Context ctx = getContextForSimpleHBase2EventSerializer();
        ctx.put("batchSize", "1");
        ctx.put(CONFIG_SERIALIZER, "org.apache.flume.sink.hbase2.MockSimpleHBase2EventSerializer");
        HBase2Sink sink = new HBase2Sink(conf);
        Configurables.configure(sink, ctx);
        // Reset the context to a higher batchSize
        ctx.put("batchSize", "100");
        Channel channel = new MemoryChannel();
        Configurables.configure(channel, new Context());
        sink.setChannel(channel);
        sink.start();
        Transaction tx = channel.getTransaction();
        tx.begin();
        Event e = EventBuilder.withBody(Bytes.toBytes((((TestHBase2Sink.valBase) + "-") + 0)));
        channel.put(e);
        tx.commit();
        tx.close();
        try {
            MockSimpleHBase2EventSerializer.throwException = true;
            sink.process();
            Assert.fail("FlumeException expected from serializer");
        } catch (FlumeException ex) {
            Assert.assertEquals("Exception for testing", ex.getMessage());
            SinkCounter sinkCounter = ((SinkCounter) (Whitebox.getInternalState(sink, "sinkCounter")));
            Assert.assertEquals(1, sinkCounter.getEventWriteFail());
        }
        MockSimpleHBase2EventSerializer.throwException = false;
        sink.process();
        sink.stop();
        try (Connection connection = ConnectionFactory.createConnection(conf);Table table = connection.getTable(TableName.valueOf(TestHBase2Sink.tableName))) {
            byte[][] results = getResults(table, 1);
            byte[] out = results[0];
            Assert.assertArrayEquals(e.getBody(), out);
            out = results[1];
            Assert.assertArrayEquals(Longs.toByteArray(1), out);
        }
    }

    @Test
    public void testWithoutConfigurationObject() throws Exception {
        Context ctx = getContextForSimpleHBase2EventSerializer();
        Context tmpContext = new Context(ctx.getParameters());
        tmpContext.put("batchSize", "2");
        tmpContext.put(ZK_QUORUM, ZKConfig.getZKQuorumServersString(conf));
        System.out.print(ctx.getString(ZK_QUORUM));
        tmpContext.put(ZK_ZNODE_PARENT, conf.get(ZOOKEEPER_ZNODE_PARENT, DEFAULT_ZOOKEEPER_ZNODE_PARENT));
        HBase2Sink sink = new HBase2Sink();
        Configurables.configure(sink, tmpContext);
        Channel channel = new MemoryChannel();
        Configurables.configure(channel, ctx);
        sink.setChannel(channel);
        sink.start();
        Transaction tx = channel.getTransaction();
        tx.begin();
        for (int i = 0; i < 3; i++) {
            Event e = EventBuilder.withBody(Bytes.toBytes((((TestHBase2Sink.valBase) + "-") + i)));
            channel.put(e);
        }
        tx.commit();
        tx.close();
        Status status = Status.READY;
        while (status != (Status.BACKOFF)) {
            status = sink.process();
        } 
        sink.stop();
        try (Connection connection = ConnectionFactory.createConnection(conf);Table table = connection.getTable(TableName.valueOf(TestHBase2Sink.tableName))) {
            byte[][] results = getResults(table, 3);
            byte[] out;
            int found = 0;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (Arrays.equals(results[j], Bytes.toBytes((((TestHBase2Sink.valBase) + "-") + i)))) {
                        found++;
                        break;
                    }
                }
            }
            Assert.assertEquals(3, found);
            out = results[3];
            Assert.assertArrayEquals(Longs.toByteArray(3), out);
        }
    }

    @Test
    public void testZKQuorum() throws Exception {
        Context ctx = getContextForSimpleHBase2EventSerializer();
        Context tmpContext = new Context(ctx.getParameters());
        String zkQuorum = "zk1.flume.apache.org:3342, zk2.flume.apache.org:3342, " + "zk3.flume.apache.org:3342";
        tmpContext.put("batchSize", "2");
        tmpContext.put(ZK_QUORUM, zkQuorum);
        tmpContext.put(ZK_ZNODE_PARENT, conf.get(ZOOKEEPER_ZNODE_PARENT, DEFAULT_ZOOKEEPER_ZNODE_PARENT));
        HBase2Sink sink = new HBase2Sink();
        Configurables.configure(sink, tmpContext);
        Assert.assertEquals(("zk1.flume.apache.org,zk2.flume.apache.org," + "zk3.flume.apache.org"), sink.getConfig().get(ZOOKEEPER_QUORUM));
        Assert.assertEquals(String.valueOf(3342), sink.getConfig().get(ZOOKEEPER_CLIENT_PORT));
    }

    @Test(expected = FlumeException.class)
    public void testZKQuorumIncorrectPorts() throws Exception {
        Context ctx = getContextForSimpleHBase2EventSerializer();
        Context tmpContext = new Context(ctx.getParameters());
        String zkQuorum = "zk1.flume.apache.org:3345, zk2.flume.apache.org:3342, " + "zk3.flume.apache.org:3342";
        tmpContext.put("batchSize", "2");
        tmpContext.put(ZK_QUORUM, zkQuorum);
        tmpContext.put(ZK_ZNODE_PARENT, conf.get(ZOOKEEPER_ZNODE_PARENT, DEFAULT_ZOOKEEPER_ZNODE_PARENT));
        HBase2Sink sink = new HBase2Sink();
        Configurables.configure(sink, tmpContext);
        Assert.fail();
    }

    @Test
    public void testCoalesce() throws EventDeliveryException {
        Context ctx = getContextForIncrementHBaseSerializer();
        ctx.put("batchSize", "100");
        ctx.put(CONFIG_COALESCE_INCREMENTS, String.valueOf(true));
        final Map<String, Long> expectedCounts = Maps.newHashMap();
        expectedCounts.put("r1:c1", 10L);
        expectedCounts.put("r1:c2", 20L);
        expectedCounts.put("r2:c1", 7L);
        expectedCounts.put("r2:c3", 63L);
        HBase2Sink.DebugIncrementsCallback cb = new TestHBase2Sink.CoalesceValidator(expectedCounts);
        HBase2Sink sink = new HBase2Sink(TestHBase2Sink.testUtility.getConfiguration(), cb);
        Configurables.configure(sink, ctx);
        Channel channel = createAndConfigureMemoryChannel(sink);
        List<Event> events = Lists.newLinkedList();
        generateEvents(events, expectedCounts);
        putEvents(channel, events);
        sink.start();
        sink.process();// Calls CoalesceValidator instance.

        sink.stop();
    }

    @Test(expected = AssertionError.class)
    public void negativeTestCoalesce() throws EventDeliveryException {
        Context ctx = getContextForIncrementHBaseSerializer();
        ctx.put("batchSize", "10");
        final Map<String, Long> expectedCounts = Maps.newHashMap();
        expectedCounts.put("r1:c1", 10L);
        HBase2Sink.DebugIncrementsCallback cb = new TestHBase2Sink.CoalesceValidator(expectedCounts);
        HBase2Sink sink = new HBase2Sink(TestHBase2Sink.testUtility.getConfiguration(), cb);
        Configurables.configure(sink, ctx);
        Channel channel = createAndConfigureMemoryChannel(sink);
        List<Event> events = Lists.newLinkedList();
        generateEvents(events, expectedCounts);
        putEvents(channel, events);
        sink.start();
        sink.process();// Calls CoalesceValidator instance.

        sink.stop();
    }

    @Test
    public void testBatchAware() throws EventDeliveryException {
        TestHBase2Sink.logger.info("Running testBatchAware()");
        Context ctx = getContextForIncrementHBaseSerializer();
        HBase2Sink sink = new HBase2Sink(TestHBase2Sink.testUtility.getConfiguration());
        Configurables.configure(sink, ctx);
        Channel channel = createAndConfigureMemoryChannel(sink);
        sink.start();
        int batchCount = 3;
        for (int i = 0; i < batchCount; i++) {
            sink.process();
        }
        sink.stop();
        Assert.assertEquals(batchCount, ((IncrementHBase2Serializer) (sink.getSerializer())).getNumBatchesStarted());
    }

    @Test(expected = ConfigurationException.class)
    public void testHBaseVersionCheck() throws Exception {
        Context ctx = getContextWithoutIncrementHBaseSerializer();
        HBase2Sink sink = Mockito.mock(HBase2Sink.class);
        Mockito.doCallRealMethod().when(sink).configure(ArgumentMatchers.any());
        Mockito.when(sink.getHBbaseVersionString()).thenReturn("1.0.0");
        Configurables.configure(sink, ctx);
    }

    @Test(expected = ConfigurationException.class)
    public void testHBaseVersionCheckNotANumber() throws Exception {
        Context ctx = getContextWithoutIncrementHBaseSerializer();
        HBase2Sink sink = Mockito.mock(HBase2Sink.class);
        Mockito.doCallRealMethod().when(sink).configure(ArgumentMatchers.any());
        Mockito.when(sink.getHBbaseVersionString()).thenReturn("Dummy text");
        Configurables.configure(sink, ctx);
    }

    /**
     * For testing that the rows coalesced, serialized by
     * {@link IncrementHBase2Serializer}, are of the expected batch size.
     */
    private static class CoalesceValidator implements HBase2Sink.DebugIncrementsCallback {
        private final Map<String, Long> expectedCounts;

        public CoalesceValidator(Map<String, Long> expectedCounts) {
            this.expectedCounts = expectedCounts;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onAfterCoalesce(Iterable<Increment> increments) {
            for (Increment inc : increments) {
                byte[] row = inc.getRow();
                Map<byte[], NavigableMap<byte[], Long>> families = null;
                try {
                    families = inc.getFamilyMapOfLongs();
                } catch (Exception e) {
                    Throwables.propagate(e);
                }
                assert families != null;
                for (byte[] family : families.keySet()) {
                    NavigableMap<byte[], Long> qualifiers = families.get(family);
                    for (Map.Entry<byte[], Long> entry : qualifiers.entrySet()) {
                        byte[] qualifier = entry.getKey();
                        Long count = entry.getValue();
                        String key = ((new String(row, Charsets.UTF_8)) + ':') + (new String(qualifier, Charsets.UTF_8));
                        Assert.assertEquals(("Expected counts don't match observed for " + key), expectedCounts.get(key), count);
                    }
                }
            }
        }
    }
}

