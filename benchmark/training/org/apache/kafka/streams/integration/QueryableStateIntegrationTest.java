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
package org.apache.kafka.streams.integration;


import KafkaStreams.State.RUNNING;
import ProducerConfig.ACKS_CONFIG;
import ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;
import StreamsConfig.APPLICATION_SERVER_CONFIG;
import StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG;
import StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG;
import StreamsConfig.NUM_STREAM_THREADS_CONFIG;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import kafka.utils.MockTime;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KafkaStreamsTest;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.integration.utils.EmbeddedKafkaCluster;
import org.apache.kafka.streams.integration.utils.IntegrationTestUtils;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Predicate;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.apache.kafka.test.IntegrationTest;
import org.apache.kafka.test.TestCondition;
import org.apache.kafka.test.TestUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ IntegrationTest.class })
public class QueryableStateIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(QueryableStateIntegrationTest.class);

    private static final int NUM_BROKERS = 1;

    @ClassRule
    public static final EmbeddedKafkaCluster CLUSTER = new EmbeddedKafkaCluster(QueryableStateIntegrationTest.NUM_BROKERS);

    private static final int STREAM_THREE_PARTITIONS = 4;

    private final MockTime mockTime = QueryableStateIntegrationTest.CLUSTER.time;

    private String streamOne = "stream-one";

    private String streamTwo = "stream-two";

    private String streamThree = "stream-three";

    private String streamConcurrent = "stream-concurrent";

    private String outputTopic = "output";

    private String outputTopicConcurrent = "output-concurrent";

    private String outputTopicConcurrentWindowed = "output-concurrent-windowed";

    private String outputTopicThree = "output-three";

    // sufficiently large window size such that everything falls into 1 window
    private static final long WINDOW_SIZE = TimeUnit.MILLISECONDS.convert(2, TimeUnit.DAYS);

    private static final int STREAM_TWO_PARTITIONS = 2;

    private static final int NUM_REPLICAS = QueryableStateIntegrationTest.NUM_BROKERS;

    private Properties streamsConfiguration;

    private List<String> inputValues;

    private int numberOfWordsPerIteration = 0;

    private Set<String> inputValuesKeys;

    private KafkaStreams kafkaStreams;

    private Comparator<KeyValue<String, String>> stringComparator;

    private Comparator<KeyValue<String, Long>> stringLongComparator;

    private static int testNo = 0;

    private class StreamRunnable implements Runnable {
        private final KafkaStreams myStream;

        private boolean closed = false;

        private final KafkaStreamsTest.StateListenerStub stateListener = new KafkaStreamsTest.StateListenerStub();

        StreamRunnable(final String inputTopic, final String outputTopic, final String outputTopicWindowed, final String storeName, final String windowStoreName, final int queryPort) {
            final Properties props = ((Properties) (streamsConfiguration.clone()));
            props.put(APPLICATION_SERVER_CONFIG, ("localhost:" + queryPort));
            myStream = createCountStream(inputTopic, outputTopic, outputTopicWindowed, storeName, windowStoreName, props);
            myStream.setStateListener(stateListener);
        }

        @Override
        public void run() {
            myStream.start();
        }

        public void close() {
            if (!(closed)) {
                myStream.close();
                closed = true;
            }
        }

        public boolean isClosed() {
            return closed;
        }

        public final KafkaStreams getStream() {
            return myStream;
        }

        final KafkaStreamsTest.StateListenerStub getStateListener() {
            return stateListener;
        }
    }

    @Test
    public void queryOnRebalance() throws Exception {
        final int numThreads = QueryableStateIntegrationTest.STREAM_TWO_PARTITIONS;
        final QueryableStateIntegrationTest.StreamRunnable[] streamRunnables = new QueryableStateIntegrationTest.StreamRunnable[numThreads];
        final Thread[] streamThreads = new Thread[numThreads];
        final QueryableStateIntegrationTest.ProducerRunnable producerRunnable = new QueryableStateIntegrationTest.ProducerRunnable(streamThree, inputValues, 1);
        producerRunnable.run();
        // create stream threads
        final String storeName = "word-count-store";
        final String windowStoreName = "windowed-word-count-store";
        for (int i = 0; i < numThreads; i++) {
            streamRunnables[i] = new QueryableStateIntegrationTest.StreamRunnable(streamThree, outputTopicThree, outputTopicConcurrentWindowed, storeName, windowStoreName, i);
            streamThreads[i] = new Thread(streamRunnables[i]);
            streamThreads[i].start();
        }
        try {
            waitUntilAtLeastNumRecordProcessed(outputTopicThree, 1);
            for (int i = 0; i < numThreads; i++) {
                verifyAllKVKeys(streamRunnables, streamRunnables[i].getStream(), streamRunnables[i].getStateListener(), inputValuesKeys, ((storeName + "-") + (streamThree)));
                verifyAllWindowedKeys(streamRunnables, streamRunnables[i].getStream(), streamRunnables[i].getStateListener(), inputValuesKeys, ((windowStoreName + "-") + (streamThree)), 0L, QueryableStateIntegrationTest.WINDOW_SIZE);
                Assert.assertEquals(RUNNING, streamRunnables[i].getStream().state());
            }
            // kill N-1 threads
            for (int i = 1; i < numThreads; i++) {
                streamRunnables[i].close();
                streamThreads[i].interrupt();
                streamThreads[i].join();
            }
            // query from the remaining thread
            verifyAllKVKeys(streamRunnables, streamRunnables[0].getStream(), streamRunnables[0].getStateListener(), inputValuesKeys, ((storeName + "-") + (streamThree)));
            verifyAllWindowedKeys(streamRunnables, streamRunnables[0].getStream(), streamRunnables[0].getStateListener(), inputValuesKeys, ((windowStoreName + "-") + (streamThree)), 0L, QueryableStateIntegrationTest.WINDOW_SIZE);
            Assert.assertEquals(RUNNING, streamRunnables[0].getStream().state());
        } finally {
            for (int i = 0; i < numThreads; i++) {
                if (!(streamRunnables[i].isClosed())) {
                    streamRunnables[i].close();
                    streamThreads[i].interrupt();
                    streamThreads[i].join();
                }
            }
        }
    }

    @Test
    public void concurrentAccesses() throws Exception {
        final int numIterations = 500000;
        final String storeName = "word-count-store";
        final String windowStoreName = "windowed-word-count-store";
        final QueryableStateIntegrationTest.ProducerRunnable producerRunnable = new QueryableStateIntegrationTest.ProducerRunnable(streamConcurrent, inputValues, numIterations);
        final Thread producerThread = new Thread(producerRunnable);
        kafkaStreams = createCountStream(streamConcurrent, outputTopicConcurrent, outputTopicConcurrentWindowed, storeName, windowStoreName, streamsConfiguration);
        kafkaStreams.start();
        producerThread.start();
        try {
            waitUntilAtLeastNumRecordProcessed(outputTopicConcurrent, numberOfWordsPerIteration);
            waitUntilAtLeastNumRecordProcessed(outputTopicConcurrentWindowed, numberOfWordsPerIteration);
            final ReadOnlyKeyValueStore<String, Long> keyValueStore = kafkaStreams.store(((storeName + "-") + (streamConcurrent)), QueryableStoreTypes.keyValueStore());
            final ReadOnlyWindowStore<String, Long> windowStore = kafkaStreams.store(((windowStoreName + "-") + (streamConcurrent)), QueryableStoreTypes.windowStore());
            final Map<String, Long> expectedWindowState = new HashMap<>();
            final Map<String, Long> expectedCount = new HashMap<>();
            while ((producerRunnable.getCurrIteration()) < numIterations) {
                verifyGreaterOrEqual(inputValuesKeys.toArray(new String[0]), expectedWindowState, expectedCount, windowStore, keyValueStore, true);
            } 
        } finally {
            producerRunnable.shutdown();
            producerThread.interrupt();
            producerThread.join();
        }
    }

    @Test
    public void shouldBeAbleToQueryStateWithZeroSizedCache() throws Exception {
        verifyCanQueryState(0);
    }

    @Test
    public void shouldBeAbleToQueryStateWithNonZeroSizedCache() throws Exception {
        verifyCanQueryState(((10 * 1024) * 1024));
    }

    @Test
    public void shouldBeAbleToQueryFilterState() throws Exception {
        streamsConfiguration.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        streamsConfiguration.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Long().getClass());
        final StreamsBuilder builder = new StreamsBuilder();
        final String[] keys = new String[]{ "hello", "goodbye", "welcome", "go", "kafka" };
        final Set<KeyValue<String, Long>> batch1 = new java.util.HashSet(Arrays.asList(new KeyValue(keys[0], 1L), new KeyValue(keys[1], 1L), new KeyValue(keys[2], 3L), new KeyValue(keys[3], 5L), new KeyValue(keys[4], 2L)));
        final Set<KeyValue<String, Long>> expectedBatch1 = new java.util.HashSet(Collections.singleton(new KeyValue(keys[4], 2L)));
        IntegrationTestUtils.produceKeyValuesSynchronously(streamOne, batch1, TestUtils.producerConfig(QueryableStateIntegrationTest.CLUSTER.bootstrapServers(), StringSerializer.class, LongSerializer.class, new Properties()), mockTime);
        final Predicate<String, Long> filterPredicate = ( key, value) -> key.contains("kafka");
        final KTable<String, Long> t1 = builder.table(streamOne);
        final KTable<String, Long> t2 = t1.filter(filterPredicate, Materialized.as("queryFilter"));
        t1.filterNot(filterPredicate, Materialized.as("queryFilterNot"));
        t2.toStream().to(outputTopic);
        kafkaStreams = new KafkaStreams(builder.build(), streamsConfiguration);
        kafkaStreams.start();
        waitUntilAtLeastNumRecordProcessed(outputTopic, 1);
        final ReadOnlyKeyValueStore<String, Long> myFilterStore = kafkaStreams.store("queryFilter", QueryableStoreTypes.keyValueStore());
        final ReadOnlyKeyValueStore<String, Long> myFilterNotStore = kafkaStreams.store("queryFilterNot", QueryableStoreTypes.keyValueStore());
        for (final KeyValue<String, Long> expectedEntry : expectedBatch1) {
            TestUtils.waitForCondition(() -> expectedEntry.value.equals(myFilterStore.get(expectedEntry.key)), "Cannot get expected result");
        }
        for (final KeyValue<String, Long> batchEntry : batch1) {
            if (!(expectedBatch1.contains(batchEntry))) {
                TestUtils.waitForCondition(() -> (myFilterStore.get(batchEntry.key)) == null, "Cannot get null result");
            }
        }
        for (final KeyValue<String, Long> expectedEntry : expectedBatch1) {
            TestUtils.waitForCondition(() -> (myFilterNotStore.get(expectedEntry.key)) == null, "Cannot get null result");
        }
        for (final KeyValue<String, Long> batchEntry : batch1) {
            if (!(expectedBatch1.contains(batchEntry))) {
                TestUtils.waitForCondition(() -> batchEntry.value.equals(myFilterNotStore.get(batchEntry.key)), "Cannot get expected result");
            }
        }
    }

    @Test
    public void shouldBeAbleToQueryMapValuesState() throws Exception {
        streamsConfiguration.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        streamsConfiguration.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        final StreamsBuilder builder = new StreamsBuilder();
        final String[] keys = new String[]{ "hello", "goodbye", "welcome", "go", "kafka" };
        final Set<KeyValue<String, String>> batch1 = new java.util.HashSet(Arrays.asList(new KeyValue(keys[0], "1"), new KeyValue(keys[1], "1"), new KeyValue(keys[2], "3"), new KeyValue(keys[3], "5"), new KeyValue(keys[4], "2")));
        IntegrationTestUtils.produceKeyValuesSynchronously(streamOne, batch1, TestUtils.producerConfig(QueryableStateIntegrationTest.CLUSTER.bootstrapServers(), StringSerializer.class, StringSerializer.class, new Properties()), mockTime);
        final KTable<String, String> t1 = builder.table(streamOne);
        t1.mapValues(((ValueMapper<String, Long>) (Long::valueOf)), Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("queryMapValues").withValueSerde(Serdes.Long())).toStream().to(outputTopic, Produced.with(Serdes.String(), Serdes.Long()));
        kafkaStreams = new KafkaStreams(builder.build(), streamsConfiguration);
        kafkaStreams.start();
        waitUntilAtLeastNumRecordProcessed(outputTopic, 5);
        final ReadOnlyKeyValueStore<String, Long> myMapStore = kafkaStreams.store("queryMapValues", QueryableStoreTypes.keyValueStore());
        for (final KeyValue<String, String> batchEntry : batch1) {
            Assert.assertEquals(Long.valueOf(batchEntry.value), myMapStore.get(batchEntry.key));
        }
    }

    @Test
    public void shouldBeAbleToQueryMapValuesAfterFilterState() throws Exception {
        streamsConfiguration.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        streamsConfiguration.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        final StreamsBuilder builder = new StreamsBuilder();
        final String[] keys = new String[]{ "hello", "goodbye", "welcome", "go", "kafka" };
        final Set<KeyValue<String, String>> batch1 = new java.util.HashSet(Arrays.asList(new KeyValue(keys[0], "1"), new KeyValue(keys[1], "1"), new KeyValue(keys[2], "3"), new KeyValue(keys[3], "5"), new KeyValue(keys[4], "2")));
        final Set<KeyValue<String, Long>> expectedBatch1 = new java.util.HashSet(Collections.singleton(new KeyValue(keys[4], 2L)));
        IntegrationTestUtils.produceKeyValuesSynchronously(streamOne, batch1, TestUtils.producerConfig(QueryableStateIntegrationTest.CLUSTER.bootstrapServers(), StringSerializer.class, StringSerializer.class, new Properties()), mockTime);
        final Predicate<String, String> filterPredicate = ( key, value) -> key.contains("kafka");
        final KTable<String, String> t1 = builder.table(streamOne);
        final KTable<String, String> t2 = t1.filter(filterPredicate, Materialized.as("queryFilter"));
        final KTable<String, Long> t3 = t2.mapValues(((ValueMapper<String, Long>) (Long::valueOf)), Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("queryMapValues").withValueSerde(Serdes.Long()));
        t3.toStream().to(outputTopic, Produced.with(Serdes.String(), Serdes.Long()));
        kafkaStreams = new KafkaStreams(builder.build(), streamsConfiguration);
        kafkaStreams.start();
        waitUntilAtLeastNumRecordProcessed(outputTopic, 1);
        final ReadOnlyKeyValueStore<String, Long> myMapStore = kafkaStreams.store("queryMapValues", QueryableStoreTypes.keyValueStore());
        for (final KeyValue<String, Long> expectedEntry : expectedBatch1) {
            Assert.assertEquals(myMapStore.get(expectedEntry.key), expectedEntry.value);
        }
        for (final KeyValue<String, String> batchEntry : batch1) {
            final KeyValue<String, Long> batchEntryMapValue = new KeyValue(batchEntry.key, Long.valueOf(batchEntry.value));
            if (!(expectedBatch1.contains(batchEntryMapValue))) {
                Assert.assertNull(myMapStore.get(batchEntry.key));
            }
        }
    }

    @Test
    public void shouldNotMakeStoreAvailableUntilAllStoresAvailable() throws Exception {
        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, String> stream = builder.stream(streamThree);
        final String storeName = "count-by-key";
        stream.groupByKey().count(Materialized.as(storeName));
        kafkaStreams = new KafkaStreams(builder.build(), streamsConfiguration);
        kafkaStreams.start();
        final KeyValue<String, String> hello = KeyValue.pair("hello", "hello");
        IntegrationTestUtils.produceKeyValuesSynchronously(streamThree, Arrays.asList(hello, hello, hello, hello, hello, hello, hello, hello), TestUtils.producerConfig(QueryableStateIntegrationTest.CLUSTER.bootstrapServers(), StringSerializer.class, StringSerializer.class, new Properties()), mockTime);
        final int maxWaitMs = 30000;
        TestUtils.waitForCondition(new QueryableStateIntegrationTest.WaitForStore(storeName), maxWaitMs, ("waiting for store " + storeName));
        final ReadOnlyKeyValueStore<String, Long> store = kafkaStreams.store(storeName, QueryableStoreTypes.keyValueStore());
        TestUtils.waitForCondition(() -> new Long(8).equals(store.get("hello")), maxWaitMs, "wait for count to be 8");
        // close stream
        kafkaStreams.close();
        // start again
        kafkaStreams = new KafkaStreams(builder.build(), streamsConfiguration);
        kafkaStreams.start();
        // make sure we never get any value other than 8 for hello
        TestUtils.waitForCondition(() -> {
            try {
                assertEquals(Long.valueOf(8L), kafkaStreams.store(storeName, QueryableStoreTypes.<String, Long>keyValueStore()).get("hello"));
                return true;
            } catch (final  ise) {
                return false;
            }
        }, maxWaitMs, ("waiting for store " + storeName));
    }

    private class WaitForStore implements TestCondition {
        private final String storeName;

        WaitForStore(final String storeName) {
            this.storeName = storeName;
        }

        @Override
        public boolean conditionMet() {
            try {
                kafkaStreams.store(storeName, QueryableStoreTypes.<String, Long>keyValueStore());
                return true;
            } catch (final InvalidStateStoreException ise) {
                return false;
            }
        }
    }

    @Test
    public void shouldAllowToQueryAfterThreadDied() throws Exception {
        final AtomicBoolean beforeFailure = new AtomicBoolean(true);
        final AtomicBoolean failed = new AtomicBoolean(false);
        final String storeName = "store";
        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, String> input = builder.stream(streamOne);
        input.groupByKey().reduce(( value1, value2) -> {
            if ((value1.length()) > 1) {
                if (beforeFailure.compareAndSet(true, false)) {
                    throw new RuntimeException("Injected test exception");
                }
            }
            return value1 + value2;
        }, Materialized.as(storeName)).toStream().to(outputTopic);
        streamsConfiguration.put(NUM_STREAM_THREADS_CONFIG, 2);
        kafkaStreams = new KafkaStreams(builder.build(), streamsConfiguration);
        kafkaStreams.setUncaughtExceptionHandler(( t, e) -> failed.set(true));
        kafkaStreams.start();
        IntegrationTestUtils.produceKeyValuesSynchronously(streamOne, Arrays.asList(KeyValue.pair("a", "1"), KeyValue.pair("a", "2"), KeyValue.pair("b", "3"), KeyValue.pair("b", "4")), TestUtils.producerConfig(QueryableStateIntegrationTest.CLUSTER.bootstrapServers(), StringSerializer.class, StringSerializer.class, new Properties()), mockTime);
        final int maxWaitMs = 30000;
        TestUtils.waitForCondition(new QueryableStateIntegrationTest.WaitForStore(storeName), maxWaitMs, ("waiting for store " + storeName));
        final ReadOnlyKeyValueStore<String, String> store = kafkaStreams.store(storeName, QueryableStoreTypes.keyValueStore());
        TestUtils.waitForCondition(() -> ("12".equals(store.get("a"))) && ("34".equals(store.get("b"))), maxWaitMs, "wait for agg to be <a,12> and <b,34>");
        IntegrationTestUtils.produceKeyValuesSynchronously(streamOne, Collections.singleton(KeyValue.pair("a", "5")), TestUtils.producerConfig(QueryableStateIntegrationTest.CLUSTER.bootstrapServers(), StringSerializer.class, StringSerializer.class, new Properties()), mockTime);
        TestUtils.waitForCondition(failed::get, maxWaitMs, "wait for thread to fail");
        TestUtils.waitForCondition(new QueryableStateIntegrationTest.WaitForStore(storeName), maxWaitMs, ("waiting for store " + storeName));
        final ReadOnlyKeyValueStore<String, String> store2 = kafkaStreams.store(storeName, QueryableStoreTypes.keyValueStore());
        try {
            TestUtils.waitForCondition(() -> ((("125".equals(store2.get("a"))) || ("1225".equals(store2.get("a")))) || ("12125".equals(store2.get("a")))) && ((("34".equals(store2.get("b"))) || ("344".equals(store2.get("b")))) || ("3434".equals(store2.get("b")))), maxWaitMs, "wait for agg to be <a,125>||<a,1225>||<a,12125> and <b,34>||<b,344>||<b,3434>");
        } catch (final Throwable t) {
            throw new RuntimeException(((("Store content is a: " + (store2.get("a"))) + "; b: ") + (store2.get("b"))), t);
        }
    }

    /**
     * A class that periodically produces records in a separate thread
     */
    private class ProducerRunnable implements Runnable {
        private final String topic;

        private final List<String> inputValues;

        private final int numIterations;

        private int currIteration = 0;

        boolean shutdown = false;

        ProducerRunnable(final String topic, final List<String> inputValues, final int numIterations) {
            this.topic = topic;
            this.inputValues = inputValues;
            this.numIterations = numIterations;
        }

        private synchronized void incrementIteration() {
            (currIteration)++;
        }

        synchronized int getCurrIteration() {
            return currIteration;
        }

        synchronized void shutdown() {
            shutdown = true;
        }

        @Override
        public void run() {
            final Properties producerConfig = new Properties();
            producerConfig.put(BOOTSTRAP_SERVERS_CONFIG, QueryableStateIntegrationTest.CLUSTER.bootstrapServers());
            producerConfig.put(ACKS_CONFIG, "all");
            producerConfig.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            producerConfig.put(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            try (final KafkaProducer<String, String> producer = new KafkaProducer(producerConfig, new StringSerializer(), new StringSerializer())) {
                while (((getCurrIteration()) < (numIterations)) && (!(shutdown))) {
                    for (final String value : inputValues) {
                        producer.send(new org.apache.kafka.clients.producer.ProducerRecord(topic, value));
                    }
                    incrementIteration();
                } 
            }
        }
    }
}

