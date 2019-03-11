/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.processor.internals;


import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.TopologyWrapper;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.TimestampExtractor;
import org.apache.kafka.streams.processor.To;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.apache.kafka.test.MockProcessorSupplier;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


public class ProcessorTopologyTest {
    private static final Serializer<String> STRING_SERIALIZER = new StringSerializer();

    private static final Deserializer<String> STRING_DESERIALIZER = new StringDeserializer();

    private static final String INPUT_TOPIC_1 = "input-topic-1";

    private static final String INPUT_TOPIC_2 = "input-topic-2";

    private static final String OUTPUT_TOPIC_1 = "output-topic-1";

    private static final String OUTPUT_TOPIC_2 = "output-topic-2";

    private static final String THROUGH_TOPIC_1 = "through-topic-1";

    private static final Header HEADER = new RecordHeader("key", "value".getBytes());

    private static final Headers HEADERS = new org.apache.kafka.common.header.internals.RecordHeaders(new Header[]{ ProcessorTopologyTest.HEADER });

    private final TopologyWrapper topology = new TopologyWrapper();

    private final MockProcessorSupplier mockProcessorSupplier = new MockProcessorSupplier();

    private final ConsumerRecordFactory<String, String> recordFactory = new ConsumerRecordFactory(ProcessorTopologyTest.STRING_SERIALIZER, ProcessorTopologyTest.STRING_SERIALIZER, 0L);

    private TopologyTestDriver driver;

    private final Properties props = new Properties();

    @Test
    public void testTopologyMetadata() {
        addSource("source-1", "topic-1");
        topology.addSource("source-2", "topic-2", "topic-3");
        topology.addProcessor("processor-1", new MockProcessorSupplier(), "source-1");
        addProcessor("processor-2", new MockProcessorSupplier(), "source-1", "source-2");
        topology.addSink("sink-1", "topic-3", "processor-1");
        addSink("sink-2", "topic-4", "processor-1", "processor-2");
        final ProcessorTopology processorTopology = topology.getInternalBuilder("X").build();
        Assert.assertEquals(6, processorTopology.processors().size());
        Assert.assertEquals(2, processorTopology.sources().size());
        Assert.assertEquals(3, processorTopology.sourceTopics().size());
        Assert.assertNotNull(processorTopology.source("topic-1"));
        Assert.assertNotNull(processorTopology.source("topic-2"));
        Assert.assertNotNull(processorTopology.source("topic-3"));
        Assert.assertEquals(processorTopology.source("topic-2"), processorTopology.source("topic-3"));
    }

    @Test
    public void testDrivingSimpleTopology() {
        final int partition = 10;
        driver = new TopologyTestDriver(createSimpleTopology(partition), props);
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key1", "value1"));
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key1", "value1", partition);
        assertNoOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_2);
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key2", "value2"));
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key2", "value2", partition);
        assertNoOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_2);
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key3", "value3"));
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key4", "value4"));
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key5", "value5"));
        assertNoOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_2);
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key3", "value3", partition);
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key4", "value4", partition);
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key5", "value5", partition);
    }

    @Test
    public void testDrivingMultiplexingTopology() {
        driver = new TopologyTestDriver(createMultiplexingTopology(), props);
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key1", "value1"));
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key1", "value1(1)");
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_2, "key1", "value1(2)");
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key2", "value2"));
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key2", "value2(1)");
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_2, "key2", "value2(2)");
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key3", "value3"));
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key4", "value4"));
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key5", "value5"));
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key3", "value3(1)");
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key4", "value4(1)");
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key5", "value5(1)");
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_2, "key3", "value3(2)");
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_2, "key4", "value4(2)");
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_2, "key5", "value5(2)");
    }

    @Test
    public void testDrivingMultiplexByNameTopology() {
        driver = new TopologyTestDriver(createMultiplexByNameTopology(), props);
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key1", "value1"));
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key1", "value1(1)");
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_2, "key1", "value1(2)");
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key2", "value2"));
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key2", "value2(1)");
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_2, "key2", "value2(2)");
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key3", "value3"));
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key4", "value4"));
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key5", "value5"));
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key3", "value3(1)");
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key4", "value4(1)");
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key5", "value5(1)");
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_2, "key3", "value3(2)");
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_2, "key4", "value4(2)");
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_2, "key5", "value5(2)");
    }

    @Test
    public void testDrivingStatefulTopology() {
        final String storeName = "entries";
        driver = new TopologyTestDriver(createStatefulTopology(storeName), props);
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key1", "value1"));
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key2", "value2"));
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key3", "value3"));
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key1", "value4"));
        assertNoOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1);
        final KeyValueStore<String, String> store = driver.getKeyValueStore(storeName);
        Assert.assertEquals("value4", store.get("key1"));
        Assert.assertEquals("value2", store.get("key2"));
        Assert.assertEquals("value3", store.get("key3"));
        Assert.assertNull(store.get("key4"));
    }

    @Test
    public void shouldDriveGlobalStore() {
        final String storeName = "my-store";
        final String global = "global";
        final String topic = "topic";
        topology.addGlobalStore(Stores.keyValueStoreBuilder(Stores.inMemoryKeyValueStore(storeName), Serdes.String(), Serdes.String()).withLoggingDisabled(), global, ProcessorTopologyTest.STRING_DESERIALIZER, ProcessorTopologyTest.STRING_DESERIALIZER, topic, "processor", define(new ProcessorTopologyTest.StatefulProcessor(storeName)));
        driver = new TopologyTestDriver(topology, props);
        final KeyValueStore<String, String> globalStore = driver.getKeyValueStore(storeName);
        driver.pipeInput(recordFactory.create(topic, "key1", "value1"));
        driver.pipeInput(recordFactory.create(topic, "key2", "value2"));
        Assert.assertEquals("value1", globalStore.get("key1"));
        Assert.assertEquals("value2", globalStore.get("key2"));
    }

    @Test
    public void testDrivingSimpleMultiSourceTopology() {
        final int partition = 10;
        driver = new TopologyTestDriver(createSimpleMultiSourceTopology(partition), props);
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key1", "value1"));
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key1", "value1", partition);
        assertNoOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_2);
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_2, "key2", "value2"));
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_2, "key2", "value2", partition);
        assertNoOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1);
    }

    @Test
    public void testDrivingForwardToSourceTopology() {
        driver = new TopologyTestDriver(createForwardToSourceTopology(), props);
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key1", "value1"));
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key2", "value2"));
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key3", "value3"));
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_2, "key1", "value1");
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_2, "key2", "value2");
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_2, "key3", "value3");
    }

    @Test
    public void testDrivingInternalRepartitioningTopology() {
        driver = new TopologyTestDriver(createInternalRepartitioningTopology(), props);
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key1", "value1"));
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key2", "value2"));
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key3", "value3"));
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key1", "value1");
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key2", "value2");
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key3", "value3");
    }

    @Test
    public void testDrivingInternalRepartitioningForwardingTimestampTopology() {
        driver = new TopologyTestDriver(createInternalRepartitioningWithValueTimestampTopology(), props);
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key1", "value1@1000"));
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key2", "value2@2000"));
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key3", "value3@3000"));
        MatcherAssert.assertThat(driver.readOutput(ProcessorTopologyTest.OUTPUT_TOPIC_1, ProcessorTopologyTest.STRING_DESERIALIZER, ProcessorTopologyTest.STRING_DESERIALIZER), CoreMatchers.equalTo(new org.apache.kafka.clients.producer.ProducerRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, null, 1000L, "key1", "value1")));
        MatcherAssert.assertThat(driver.readOutput(ProcessorTopologyTest.OUTPUT_TOPIC_1, ProcessorTopologyTest.STRING_DESERIALIZER, ProcessorTopologyTest.STRING_DESERIALIZER), CoreMatchers.equalTo(new org.apache.kafka.clients.producer.ProducerRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, null, 2000L, "key2", "value2")));
        MatcherAssert.assertThat(driver.readOutput(ProcessorTopologyTest.OUTPUT_TOPIC_1, ProcessorTopologyTest.STRING_DESERIALIZER, ProcessorTopologyTest.STRING_DESERIALIZER), CoreMatchers.equalTo(new org.apache.kafka.clients.producer.ProducerRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, null, 3000L, "key3", "value3")));
    }

    @Test
    public void shouldCreateStringWithSourceAndTopics() {
        topology.addSource("source", "topic1", "topic2");
        final ProcessorTopology processorTopology = topology.getInternalBuilder().build();
        final String result = processorTopology.toString();
        MatcherAssert.assertThat(result, CoreMatchers.containsString("source:\n\t\ttopics:\t\t[topic1, topic2]\n"));
    }

    @Test
    public void shouldCreateStringWithMultipleSourcesAndTopics() {
        topology.addSource("source", "topic1", "topic2");
        topology.addSource("source2", "t", "t1", "t2");
        final ProcessorTopology processorTopology = topology.getInternalBuilder().build();
        final String result = processorTopology.toString();
        MatcherAssert.assertThat(result, CoreMatchers.containsString("source:\n\t\ttopics:\t\t[topic1, topic2]\n"));
        MatcherAssert.assertThat(result, CoreMatchers.containsString("source2:\n\t\ttopics:\t\t[t, t1, t2]\n"));
    }

    @Test
    public void shouldCreateStringWithProcessors() {
        addSource("source", "t").addProcessor("processor", mockProcessorSupplier, "source").addProcessor("other", mockProcessorSupplier, "source");
        final ProcessorTopology processorTopology = topology.getInternalBuilder().build();
        final String result = processorTopology.toString();
        MatcherAssert.assertThat(result, CoreMatchers.containsString("\t\tchildren:\t[processor, other]"));
        MatcherAssert.assertThat(result, CoreMatchers.containsString("processor:\n"));
        MatcherAssert.assertThat(result, CoreMatchers.containsString("other:\n"));
    }

    @Test
    public void shouldRecursivelyPrintChildren() {
        addSource("source", "t").addProcessor("processor", mockProcessorSupplier, "source").addProcessor("child-one", mockProcessorSupplier, "processor").addProcessor("child-one-one", mockProcessorSupplier, "child-one").addProcessor("child-two", mockProcessorSupplier, "processor").addProcessor("child-two-one", mockProcessorSupplier, "child-two");
        final String result = topology.getInternalBuilder().build().toString();
        MatcherAssert.assertThat(result, CoreMatchers.containsString("child-one:\n\t\tchildren:\t[child-one-one]"));
        MatcherAssert.assertThat(result, CoreMatchers.containsString("child-two:\n\t\tchildren:\t[child-two-one]"));
    }

    @Test
    public void shouldConsiderTimeStamps() {
        final int partition = 10;
        driver = new TopologyTestDriver(createSimpleTopology(partition), props);
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key1", "value1", 10L));
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key2", "value2", 20L));
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key3", "value3", 30L));
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key1", "value1", partition, 10L);
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key2", "value2", partition, 20L);
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key3", "value3", partition, 30L);
    }

    @Test
    public void shouldConsiderModifiedTimeStamps() {
        final int partition = 10;
        driver = new TopologyTestDriver(createTimestampTopology(partition), props);
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key1", "value1", 10L));
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key2", "value2", 20L));
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key3", "value3", 30L));
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key1", "value1", partition, 20L);
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key2", "value2", partition, 30L);
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key3", "value3", partition, 40L);
    }

    @Test
    public void shouldConsiderHeaders() {
        final int partition = 10;
        driver = new TopologyTestDriver(createSimpleTopology(partition), props);
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key1", "value1", ProcessorTopologyTest.HEADERS, 10L));
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key2", "value2", ProcessorTopologyTest.HEADERS, 20L));
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key3", "value3", ProcessorTopologyTest.HEADERS, 30L));
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key1", "value1", ProcessorTopologyTest.HEADERS, partition, 10L);
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key2", "value2", ProcessorTopologyTest.HEADERS, partition, 20L);
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key3", "value3", ProcessorTopologyTest.HEADERS, partition, 30L);
    }

    @Test
    public void shouldAddHeaders() {
        driver = new TopologyTestDriver(createAddHeaderTopology(), props);
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key1", "value1", 10L));
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key2", "value2", 20L));
        driver.pipeInput(recordFactory.create(ProcessorTopologyTest.INPUT_TOPIC_1, "key3", "value3", 30L));
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key1", "value1", ProcessorTopologyTest.HEADERS, 10L);
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key2", "value2", ProcessorTopologyTest.HEADERS, 20L);
        assertNextOutputRecord(ProcessorTopologyTest.OUTPUT_TOPIC_1, "key3", "value3", ProcessorTopologyTest.HEADERS, 30L);
    }

    @Test
    public void statelessTopologyShouldNotHavePersistentStore() {
        final TopologyWrapper topology = new TopologyWrapper();
        final ProcessorTopology processorTopology = topology.getInternalBuilder("anyAppId").build();
        Assert.assertFalse(processorTopology.hasPersistentLocalStore());
        Assert.assertFalse(processorTopology.hasPersistentGlobalStore());
    }

    @Test
    public void inMemoryStoreShouldNotResultInPersistentLocalStore() {
        final ProcessorTopology processorTopology = createLocalStoreTopology(Stores.inMemoryKeyValueStore("my-store"));
        Assert.assertFalse(processorTopology.hasPersistentLocalStore());
    }

    @Test
    public void persistentLocalStoreShouldBeDetected() {
        final ProcessorTopology processorTopology = createLocalStoreTopology(Stores.persistentKeyValueStore("my-store"));
        Assert.assertTrue(processorTopology.hasPersistentLocalStore());
    }

    @Test
    public void inMemoryStoreShouldNotResultInPersistentGlobalStore() {
        final ProcessorTopology processorTopology = createGlobalStoreTopology(Stores.inMemoryKeyValueStore("my-store"));
        Assert.assertFalse(processorTopology.hasPersistentGlobalStore());
    }

    @Test
    public void persistentGlobalStoreShouldBeDetected() {
        final ProcessorTopology processorTopology = createGlobalStoreTopology(Stores.persistentKeyValueStore("my-store"));
        Assert.assertTrue(processorTopology.hasPersistentGlobalStore());
    }

    /**
     * A processor that simply forwards all messages to all children.
     */
    protected static class ForwardingProcessor extends AbstractProcessor<String, String> {
        @Override
        public void process(final String key, final String value) {
            context().forward(key, value);
        }
    }

    /**
     * A processor that simply forwards all messages to all children with advanced timestamps.
     */
    protected static class TimestampProcessor extends AbstractProcessor<String, String> {
        @Override
        public void process(final String key, final String value) {
            context().forward(key, value, To.all().withTimestamp(((context().timestamp()) + 10)));
        }
    }

    protected static class AddHeaderProcessor extends AbstractProcessor<String, String> {
        @Override
        public void process(final String key, final String value) {
            context().headers().add(ProcessorTopologyTest.HEADER);
            context().forward(key, value);
        }
    }

    /**
     * A processor that removes custom timestamp information from messages and forwards modified messages to each child.
     * A message contains custom timestamp information if the value is in ".*@[0-9]+" format.
     */
    protected static class ValueTimestampProcessor extends AbstractProcessor<String, String> {
        @Override
        public void process(final String key, final String value) {
            context().forward(key, value.split("@")[0]);
        }
    }

    /**
     * A processor that forwards slightly-modified messages to each child.
     */
    protected static class MultiplexingProcessor extends AbstractProcessor<String, String> {
        private final int numChildren;

        MultiplexingProcessor(final int numChildren) {
            this.numChildren = numChildren;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void process(final String key, final String value) {
            for (int i = 0; i != (numChildren); ++i) {
                context().forward(key, (((value + "(") + (i + 1)) + ")"), i);
            }
        }
    }

    /**
     * A processor that forwards slightly-modified messages to each named child.
     * Note: the children are assumed to be named "sink{child number}", e.g., sink1, or sink2, etc.
     */
    protected static class MultiplexByNameProcessor extends AbstractProcessor<String, String> {
        private final int numChildren;

        MultiplexByNameProcessor(final int numChildren) {
            this.numChildren = numChildren;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void process(final String key, final String value) {
            for (int i = 0; i != (numChildren); ++i) {
                context().forward(key, (((value + "(") + (i + 1)) + ")"), ("sink" + i));
            }
        }
    }

    /**
     * A processor that stores each key-value pair in an in-memory key-value store registered with the context.
     */
    protected static class StatefulProcessor extends AbstractProcessor<String, String> {
        private KeyValueStore<String, String> store;

        private final String storeName;

        StatefulProcessor(final String storeName) {
            this.storeName = storeName;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void init(final ProcessorContext context) {
            super.init(context);
            store = ((KeyValueStore<String, String>) (context.getStateStore(storeName)));
        }

        @Override
        public void process(final String key, final String value) {
            store.put(key, value);
        }
    }

    /**
     * A custom timestamp extractor that extracts the timestamp from the record's value if the value is in ".*@[0-9]+"
     * format. Otherwise, it returns the record's timestamp or the default timestamp if the record's timestamp is negative.
     */
    public static class CustomTimestampExtractor implements TimestampExtractor {
        private static final long DEFAULT_TIMESTAMP = 1000L;

        @Override
        public long extract(final ConsumerRecord<Object, Object> record, final long previousTimestamp) {
            if (record.value().toString().matches(".*@[0-9]+")) {
                return Long.parseLong(record.value().toString().split("@")[1]);
            }
            if ((record.timestamp()) >= 0L) {
                return record.timestamp();
            }
            return ProcessorTopologyTest.CustomTimestampExtractor.DEFAULT_TIMESTAMP;
        }
    }
}

