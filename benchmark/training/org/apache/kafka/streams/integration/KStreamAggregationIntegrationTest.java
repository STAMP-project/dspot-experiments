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


import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import kafka.utils.MockTime;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.integration.utils.EmbeddedKafkaCluster;
import org.apache.kafka.streams.integration.utils.IntegrationTestUtils;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Initializer;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Reducer;
import org.apache.kafka.streams.kstream.SessionWindows;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.kstream.WindowedSerdes;
import org.apache.kafka.streams.kstream.internals.SessionWindow;
import org.apache.kafka.streams.kstream.internals.TimeWindow;
import org.apache.kafka.streams.kstream.internals.UnlimitedWindow;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlySessionStore;
import org.apache.kafka.test.IntegrationTest;
import org.apache.kafka.test.TestUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static java.time.Instant.ofEpochMilli;


@SuppressWarnings("unchecked")
@Category({ IntegrationTest.class })
public class KStreamAggregationIntegrationTest {
    private static final int NUM_BROKERS = 1;

    @ClassRule
    public static final EmbeddedKafkaCluster CLUSTER = new EmbeddedKafkaCluster(KStreamAggregationIntegrationTest.NUM_BROKERS);

    private static volatile int testNo = 0;

    private final MockTime mockTime = KStreamAggregationIntegrationTest.CLUSTER.time;

    private StreamsBuilder builder;

    private Properties streamsConfiguration;

    private KafkaStreams kafkaStreams;

    private String streamOneInput;

    private String userSessionsStream = "user-sessions";

    private String outputTopic;

    private KGroupedStream<String, String> groupedStream;

    private Reducer<String> reducer;

    private Initializer<Integer> initializer;

    private Aggregator<String, String, Integer> aggregator;

    private KStream<Integer, String> stream;

    @Test
    public void shouldReduce() throws Exception {
        produceMessages(mockTime.milliseconds());
        groupedStream.reduce(reducer, Materialized.<String, String, KeyValueStore<Bytes, byte[]>>as("reduce-by-key")).toStream().to(outputTopic, Produced.with(Serdes.String(), Serdes.String()));
        startStreams();
        produceMessages(mockTime.milliseconds());
        final List<KeyValue<String, String>> results = receiveMessages(new StringDeserializer(), new StringDeserializer(), 10);
        Collections.sort(results, KStreamAggregationIntegrationTest::compare);
        MatcherAssert.assertThat(results, Is.is(Arrays.asList(KeyValue.pair("A", "A"), KeyValue.pair("A", "A:A"), KeyValue.pair("B", "B"), KeyValue.pair("B", "B:B"), KeyValue.pair("C", "C"), KeyValue.pair("C", "C:C"), KeyValue.pair("D", "D"), KeyValue.pair("D", "D:D"), KeyValue.pair("E", "E"), KeyValue.pair("E", "E:E"))));
    }

    @Test
    public void shouldReduceWindowed() throws Exception {
        final long firstBatchTimestamp = mockTime.milliseconds();
        mockTime.sleep(1000);
        produceMessages(firstBatchTimestamp);
        final long secondBatchTimestamp = mockTime.milliseconds();
        produceMessages(secondBatchTimestamp);
        produceMessages(secondBatchTimestamp);
        final Serde<Windowed<String>> windowedSerde = WindowedSerdes.timeWindowedSerdeFrom(String.class);
        groupedStream.windowedBy(TimeWindows.of(Duration.ofMillis(500L))).reduce(reducer).toStream().to(outputTopic, Produced.with(windowedSerde, Serdes.String()));
        startStreams();
        final List<KeyValue<Windowed<String>, String>> windowedOutput = receiveMessages(new org.apache.kafka.streams.kstream.TimeWindowedDeserializer(), new StringDeserializer(), String.class, 15);
        // read from ConsoleConsumer
        final String resultFromConsoleConsumer = readWindowedKeyedMessagesViaConsoleConsumer(new org.apache.kafka.streams.kstream.TimeWindowedDeserializer<String>(), new StringDeserializer(), String.class, 15, false);
        final Comparator<KeyValue<Windowed<String>, String>> comparator = Comparator.comparing((KeyValue<Windowed<String>, String> o) -> o.key.key()).thenComparing(( o) -> o.value);
        Collections.sort(windowedOutput, comparator);
        final long firstBatchWindow = (firstBatchTimestamp / 500) * 500;
        final long secondBatchWindow = (secondBatchTimestamp / 500) * 500;
        final List<KeyValue<Windowed<String>, String>> expectResult = Arrays.asList(new KeyValue(new Windowed("A", new TimeWindow(firstBatchWindow, Long.MAX_VALUE)), "A"), new KeyValue(new Windowed("A", new TimeWindow(secondBatchWindow, Long.MAX_VALUE)), "A"), new KeyValue(new Windowed("A", new TimeWindow(secondBatchWindow, Long.MAX_VALUE)), "A:A"), new KeyValue(new Windowed("B", new TimeWindow(firstBatchWindow, Long.MAX_VALUE)), "B"), new KeyValue(new Windowed("B", new TimeWindow(secondBatchWindow, Long.MAX_VALUE)), "B"), new KeyValue(new Windowed("B", new TimeWindow(secondBatchWindow, Long.MAX_VALUE)), "B:B"), new KeyValue(new Windowed("C", new TimeWindow(firstBatchWindow, Long.MAX_VALUE)), "C"), new KeyValue(new Windowed("C", new TimeWindow(secondBatchWindow, Long.MAX_VALUE)), "C"), new KeyValue(new Windowed("C", new TimeWindow(secondBatchWindow, Long.MAX_VALUE)), "C:C"), new KeyValue(new Windowed("D", new TimeWindow(firstBatchWindow, Long.MAX_VALUE)), "D"), new KeyValue(new Windowed("D", new TimeWindow(secondBatchWindow, Long.MAX_VALUE)), "D"), new KeyValue(new Windowed("D", new TimeWindow(secondBatchWindow, Long.MAX_VALUE)), "D:D"), new KeyValue(new Windowed("E", new TimeWindow(firstBatchWindow, Long.MAX_VALUE)), "E"), new KeyValue(new Windowed("E", new TimeWindow(secondBatchWindow, Long.MAX_VALUE)), "E"), new KeyValue(new Windowed("E", new TimeWindow(secondBatchWindow, Long.MAX_VALUE)), "E:E"));
        MatcherAssert.assertThat(windowedOutput, Is.is(expectResult));
        final Set<String> expectResultString = new java.util.HashSet(expectResult.size());
        for (final KeyValue<Windowed<String>, String> eachRecord : expectResult) {
            expectResultString.add(eachRecord.toString());
        }
        // check every message is contained in the expect result
        final String[] allRecords = resultFromConsoleConsumer.split("\n");
        for (final String record : allRecords) {
            Assert.assertTrue(expectResultString.contains((("KeyValue(" + record) + ")")));
        }
    }

    @Test
    public void shouldAggregate() throws Exception {
        produceMessages(mockTime.milliseconds());
        groupedStream.aggregate(initializer, aggregator, Materialized.<String, Integer, KeyValueStore<Bytes, byte[]>>as("aggregate-by-selected-key")).toStream().to(outputTopic, Produced.with(Serdes.String(), Serdes.Integer()));
        startStreams();
        produceMessages(mockTime.milliseconds());
        final List<KeyValue<String, Integer>> results = receiveMessages(new StringDeserializer(), new IntegerDeserializer(), 10);
        Collections.sort(results, KStreamAggregationIntegrationTest::compare);
        MatcherAssert.assertThat(results, Is.is(Arrays.asList(KeyValue.pair("A", 1), KeyValue.pair("A", 2), KeyValue.pair("B", 1), KeyValue.pair("B", 2), KeyValue.pair("C", 1), KeyValue.pair("C", 2), KeyValue.pair("D", 1), KeyValue.pair("D", 2), KeyValue.pair("E", 1), KeyValue.pair("E", 2))));
    }

    @Test
    public void shouldAggregateWindowed() throws Exception {
        final long firstTimestamp = mockTime.milliseconds();
        mockTime.sleep(1000);
        produceMessages(firstTimestamp);
        final long secondTimestamp = mockTime.milliseconds();
        produceMessages(secondTimestamp);
        produceMessages(secondTimestamp);
        final Serde<Windowed<String>> windowedSerde = WindowedSerdes.timeWindowedSerdeFrom(String.class);
        groupedStream.windowedBy(TimeWindows.of(Duration.ofMillis(500L))).aggregate(initializer, aggregator, Materialized.with(null, Serdes.Integer())).toStream().to(outputTopic, Produced.with(windowedSerde, Serdes.Integer()));
        startStreams();
        final List<KeyValue<Windowed<String>, KeyValue<Integer, Long>>> windowedMessages = receiveMessagesWithTimestamp(new org.apache.kafka.streams.kstream.TimeWindowedDeserializer(), new IntegerDeserializer(), String.class, 15);
        // read from ConsoleConsumer
        final String resultFromConsoleConsumer = readWindowedKeyedMessagesViaConsoleConsumer(new org.apache.kafka.streams.kstream.TimeWindowedDeserializer<String>(), new IntegerDeserializer(), String.class, 15, true);
        final Comparator<KeyValue<Windowed<String>, KeyValue<Integer, Long>>> comparator = Comparator.comparing((KeyValue<Windowed<String>, KeyValue<Integer, Long>> o) -> o.key.key()).thenComparingInt(( o) -> o.value.key);
        Collections.sort(windowedMessages, comparator);
        final long firstWindow = (firstTimestamp / 500) * 500;
        final long secondWindow = (secondTimestamp / 500) * 500;
        final List<KeyValue<Windowed<String>, KeyValue<Integer, Long>>> expectResult = Arrays.asList(new KeyValue(new Windowed("A", new TimeWindow(firstWindow, Long.MAX_VALUE)), KeyValue.pair(1, firstTimestamp)), new KeyValue(new Windowed("A", new TimeWindow(secondWindow, Long.MAX_VALUE)), KeyValue.pair(1, secondTimestamp)), new KeyValue(new Windowed("A", new TimeWindow(secondWindow, Long.MAX_VALUE)), KeyValue.pair(2, secondTimestamp)), new KeyValue(new Windowed("B", new TimeWindow(firstWindow, Long.MAX_VALUE)), KeyValue.pair(1, firstTimestamp)), new KeyValue(new Windowed("B", new TimeWindow(secondWindow, Long.MAX_VALUE)), KeyValue.pair(1, secondTimestamp)), new KeyValue(new Windowed("B", new TimeWindow(secondWindow, Long.MAX_VALUE)), KeyValue.pair(2, secondTimestamp)), new KeyValue(new Windowed("C", new TimeWindow(firstWindow, Long.MAX_VALUE)), KeyValue.pair(1, firstTimestamp)), new KeyValue(new Windowed("C", new TimeWindow(secondWindow, Long.MAX_VALUE)), KeyValue.pair(1, secondTimestamp)), new KeyValue(new Windowed("C", new TimeWindow(secondWindow, Long.MAX_VALUE)), KeyValue.pair(2, secondTimestamp)), new KeyValue(new Windowed("D", new TimeWindow(firstWindow, Long.MAX_VALUE)), KeyValue.pair(1, firstTimestamp)), new KeyValue(new Windowed("D", new TimeWindow(secondWindow, Long.MAX_VALUE)), KeyValue.pair(1, secondTimestamp)), new KeyValue(new Windowed("D", new TimeWindow(secondWindow, Long.MAX_VALUE)), KeyValue.pair(2, secondTimestamp)), new KeyValue(new Windowed("E", new TimeWindow(firstWindow, Long.MAX_VALUE)), KeyValue.pair(1, firstTimestamp)), new KeyValue(new Windowed("E", new TimeWindow(secondWindow, Long.MAX_VALUE)), KeyValue.pair(1, secondTimestamp)), new KeyValue(new Windowed("E", new TimeWindow(secondWindow, Long.MAX_VALUE)), KeyValue.pair(2, secondTimestamp)));
        MatcherAssert.assertThat(windowedMessages, Is.is(expectResult));
        final Set<String> expectResultString = new java.util.HashSet(expectResult.size());
        for (final KeyValue<Windowed<String>, KeyValue<Integer, Long>> eachRecord : expectResult) {
            expectResultString.add(((((("CreateTime:" + (eachRecord.value.value)) + ", ") + (eachRecord.key.toString())) + ", ") + (eachRecord.value.key)));
        }
        // check every message is contained in the expect result
        final String[] allRecords = resultFromConsoleConsumer.split("\n");
        for (final String record : allRecords) {
            Assert.assertTrue(expectResultString.contains(record));
        }
    }

    @Test
    public void shouldCount() throws Exception {
        produceMessages(mockTime.milliseconds());
        groupedStream.count(Materialized.as("count-by-key")).toStream().to(outputTopic, Produced.with(Serdes.String(), Serdes.Long()));
        shouldCountHelper();
    }

    @Test
    public void shouldCountWithInternalStore() throws Exception {
        produceMessages(mockTime.milliseconds());
        groupedStream.count().toStream().to(outputTopic, Produced.with(Serdes.String(), Serdes.Long()));
        shouldCountHelper();
    }

    @Test
    public void shouldGroupByKey() throws Exception {
        final long timestamp = mockTime.milliseconds();
        produceMessages(timestamp);
        produceMessages(timestamp);
        stream.groupByKey(Grouped.with(Serdes.Integer(), Serdes.String())).windowedBy(TimeWindows.of(Duration.ofMillis(500L))).count().toStream(( windowedKey, value) -> ((windowedKey.key()) + "@") + (windowedKey.window().start())).to(outputTopic, Produced.with(Serdes.String(), Serdes.Long()));
        startStreams();
        final List<KeyValue<String, Long>> results = receiveMessages(new StringDeserializer(), new LongDeserializer(), 10);
        Collections.sort(results, KStreamAggregationIntegrationTest::compare);
        final long window = (timestamp / 500) * 500;
        MatcherAssert.assertThat(results, Is.is(Arrays.asList(KeyValue.pair(("1@" + window), 1L), KeyValue.pair(("1@" + window), 2L), KeyValue.pair(("2@" + window), 1L), KeyValue.pair(("2@" + window), 2L), KeyValue.pair(("3@" + window), 1L), KeyValue.pair(("3@" + window), 2L), KeyValue.pair(("4@" + window), 1L), KeyValue.pair(("4@" + window), 2L), KeyValue.pair(("5@" + window), 1L), KeyValue.pair(("5@" + window), 2L))));
    }

    @Test
    public void shouldCountSessionWindows() throws Exception {
        final long sessionGap = (5 * 60) * 1000L;
        final long t1 = (mockTime.milliseconds()) - (TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS));
        final List<KeyValue<String, String>> t1Messages = Arrays.asList(new KeyValue("bob", "start"), new KeyValue("penny", "start"), new KeyValue("jo", "pause"), new KeyValue("emily", "pause"));
        IntegrationTestUtils.produceKeyValuesSynchronouslyWithTimestamp(userSessionsStream, t1Messages, TestUtils.producerConfig(KStreamAggregationIntegrationTest.CLUSTER.bootstrapServers(), StringSerializer.class, StringSerializer.class, new Properties()), t1);
        final long t2 = t1 + (sessionGap / 2);
        IntegrationTestUtils.produceKeyValuesSynchronouslyWithTimestamp(userSessionsStream, Collections.singletonList(new KeyValue("emily", "resume")), TestUtils.producerConfig(KStreamAggregationIntegrationTest.CLUSTER.bootstrapServers(), StringSerializer.class, StringSerializer.class, new Properties()), t2);
        final long t3 = (t1 + sessionGap) + 1;
        IntegrationTestUtils.produceKeyValuesSynchronouslyWithTimestamp(userSessionsStream, Arrays.asList(new KeyValue("bob", "pause"), new KeyValue("penny", "stop")), TestUtils.producerConfig(KStreamAggregationIntegrationTest.CLUSTER.bootstrapServers(), StringSerializer.class, StringSerializer.class, new Properties()), t3);
        final long t4 = t3 + (sessionGap / 2);
        IntegrationTestUtils.produceKeyValuesSynchronouslyWithTimestamp(userSessionsStream, // bobs session continues
        // jo's starts new session
        Arrays.asList(new KeyValue("bob", "resume"), new KeyValue("jo", "resume")), TestUtils.producerConfig(KStreamAggregationIntegrationTest.CLUSTER.bootstrapServers(), StringSerializer.class, StringSerializer.class, new Properties()), t4);
        final Map<Windowed<String>, KeyValue<Long, Long>> results = new HashMap<>();
        final CountDownLatch latch = new CountDownLatch(11);
        builder.stream(userSessionsStream, Consumed.with(Serdes.String(), Serdes.String())).groupByKey(Grouped.with(Serdes.String(), Serdes.String())).windowedBy(SessionWindows.with(Duration.ofMillis(sessionGap))).count().toStream().transform(() -> new Transformer<Windowed<String>, Long, KeyValue<Object, Object>>() {
            private ProcessorContext context;

            @Override
            public void init(final ProcessorContext context) {
                this.context = context;
            }

            @Override
            public KeyValue<Object, Object> transform(final Windowed<String> key, final Long value) {
                results.put(key, KeyValue.pair(value, context.timestamp()));
                latch.countDown();
                return null;
            }

            @Override
            public void close() {
            }
        });
        startStreams();
        latch.await(30, TimeUnit.SECONDS);
        MatcherAssert.assertThat(results.get(new Windowed("bob", new SessionWindow(t1, t1))), CoreMatchers.equalTo(KeyValue.pair(1L, t1)));
        MatcherAssert.assertThat(results.get(new Windowed("penny", new SessionWindow(t1, t1))), CoreMatchers.equalTo(KeyValue.pair(1L, t1)));
        MatcherAssert.assertThat(results.get(new Windowed("jo", new SessionWindow(t1, t1))), CoreMatchers.equalTo(KeyValue.pair(1L, t1)));
        MatcherAssert.assertThat(results.get(new Windowed("jo", new SessionWindow(t4, t4))), CoreMatchers.equalTo(KeyValue.pair(1L, t4)));
        MatcherAssert.assertThat(results.get(new Windowed("emily", new SessionWindow(t1, t2))), CoreMatchers.equalTo(KeyValue.pair(2L, t2)));
        MatcherAssert.assertThat(results.get(new Windowed("bob", new SessionWindow(t3, t4))), CoreMatchers.equalTo(KeyValue.pair(2L, t4)));
        MatcherAssert.assertThat(results.get(new Windowed("penny", new SessionWindow(t3, t3))), CoreMatchers.equalTo(KeyValue.pair(1L, t3)));
    }

    @Test
    public void shouldReduceSessionWindows() throws Exception {
        final long sessionGap = 1000L;// something to do with time

        final long t1 = mockTime.milliseconds();
        final List<KeyValue<String, String>> t1Messages = Arrays.asList(new KeyValue("bob", "start"), new KeyValue("penny", "start"), new KeyValue("jo", "pause"), new KeyValue("emily", "pause"));
        IntegrationTestUtils.produceKeyValuesSynchronouslyWithTimestamp(userSessionsStream, t1Messages, TestUtils.producerConfig(KStreamAggregationIntegrationTest.CLUSTER.bootstrapServers(), StringSerializer.class, StringSerializer.class, new Properties()), t1);
        final long t2 = t1 + (sessionGap / 2);
        IntegrationTestUtils.produceKeyValuesSynchronouslyWithTimestamp(userSessionsStream, Collections.singletonList(new KeyValue("emily", "resume")), TestUtils.producerConfig(KStreamAggregationIntegrationTest.CLUSTER.bootstrapServers(), StringSerializer.class, StringSerializer.class, new Properties()), t2);
        final long t3 = (t1 + sessionGap) + 1;
        IntegrationTestUtils.produceKeyValuesSynchronouslyWithTimestamp(userSessionsStream, Arrays.asList(new KeyValue("bob", "pause"), new KeyValue("penny", "stop")), TestUtils.producerConfig(KStreamAggregationIntegrationTest.CLUSTER.bootstrapServers(), StringSerializer.class, StringSerializer.class, new Properties()), t3);
        final long t4 = t3 + (sessionGap / 2);
        IntegrationTestUtils.produceKeyValuesSynchronouslyWithTimestamp(userSessionsStream, // bobs session continues
        // jo's starts new session
        Arrays.asList(new KeyValue("bob", "resume"), new KeyValue("jo", "resume")), TestUtils.producerConfig(KStreamAggregationIntegrationTest.CLUSTER.bootstrapServers(), StringSerializer.class, StringSerializer.class, new Properties()), t4);
        final Map<Windowed<String>, String> results = new HashMap<>();
        final CountDownLatch latch = new CountDownLatch(11);
        final String userSessionsStore = "UserSessionsStore";
        builder.stream(userSessionsStream, Consumed.with(Serdes.String(), Serdes.String())).groupByKey(Grouped.with(Serdes.String(), Serdes.String())).windowedBy(SessionWindows.with(Duration.ofMillis(sessionGap))).reduce(( value1, value2) -> (value1 + ":") + value2, Materialized.as(userSessionsStore)).toStream().foreach(( key, value) -> {
            results.put(key, value);
            latch.countDown();
        });
        startStreams();
        latch.await(30, TimeUnit.SECONDS);
        final ReadOnlySessionStore<String, String> sessionStore = kafkaStreams.store(userSessionsStore, QueryableStoreTypes.sessionStore());
        // verify correct data received
        MatcherAssert.assertThat(results.get(new Windowed("bob", new SessionWindow(t1, t1))), CoreMatchers.equalTo("start"));
        MatcherAssert.assertThat(results.get(new Windowed("penny", new SessionWindow(t1, t1))), CoreMatchers.equalTo("start"));
        MatcherAssert.assertThat(results.get(new Windowed("jo", new SessionWindow(t1, t1))), CoreMatchers.equalTo("pause"));
        MatcherAssert.assertThat(results.get(new Windowed("jo", new SessionWindow(t4, t4))), CoreMatchers.equalTo("resume"));
        MatcherAssert.assertThat(results.get(new Windowed("emily", new SessionWindow(t1, t2))), CoreMatchers.equalTo("pause:resume"));
        MatcherAssert.assertThat(results.get(new Windowed("bob", new SessionWindow(t3, t4))), CoreMatchers.equalTo("pause:resume"));
        MatcherAssert.assertThat(results.get(new Windowed("penny", new SessionWindow(t3, t3))), CoreMatchers.equalTo("stop"));
        // verify can query data via IQ
        final KeyValueIterator<Windowed<String>, String> bob = sessionStore.fetch("bob");
        MatcherAssert.assertThat(bob.next(), CoreMatchers.equalTo(KeyValue.pair(new Windowed("bob", new SessionWindow(t1, t1)), "start")));
        MatcherAssert.assertThat(bob.next(), CoreMatchers.equalTo(KeyValue.pair(new Windowed("bob", new SessionWindow(t3, t4)), "pause:resume")));
        Assert.assertFalse(bob.hasNext());
    }

    @Test
    public void shouldCountUnlimitedWindows() throws Exception {
        final long startTime = ((mockTime.milliseconds()) - (TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS))) + 1;
        final long incrementTime = Duration.ofDays(1).toMillis();
        final long t1 = (mockTime.milliseconds()) - (TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS));
        final List<KeyValue<String, String>> t1Messages = Arrays.asList(new KeyValue("bob", "start"), new KeyValue("penny", "start"), new KeyValue("jo", "pause"), new KeyValue("emily", "pause"));
        final Properties producerConfig = TestUtils.producerConfig(KStreamAggregationIntegrationTest.CLUSTER.bootstrapServers(), StringSerializer.class, StringSerializer.class, new Properties());
        IntegrationTestUtils.produceKeyValuesSynchronouslyWithTimestamp(userSessionsStream, t1Messages, producerConfig, t1);
        final long t2 = t1 + incrementTime;
        IntegrationTestUtils.produceKeyValuesSynchronouslyWithTimestamp(userSessionsStream, Collections.singletonList(new KeyValue("emily", "resume")), producerConfig, t2);
        final long t3 = t2 + incrementTime;
        IntegrationTestUtils.produceKeyValuesSynchronouslyWithTimestamp(userSessionsStream, Arrays.asList(new KeyValue("bob", "pause"), new KeyValue("penny", "stop")), producerConfig, t3);
        final long t4 = t3 + incrementTime;
        IntegrationTestUtils.produceKeyValuesSynchronouslyWithTimestamp(userSessionsStream, // bobs session continues
        // jo's starts new session
        Arrays.asList(new KeyValue("bob", "resume"), new KeyValue("jo", "resume")), producerConfig, t4);
        final Map<Windowed<String>, KeyValue<Long, Long>> results = new HashMap<>();
        final CountDownLatch latch = new CountDownLatch(5);
        builder.stream(userSessionsStream, Consumed.with(Serdes.String(), Serdes.String())).groupByKey(Grouped.with(Serdes.String(), Serdes.String())).windowedBy(org.apache.kafka.streams.kstream.UnlimitedWindows.of().startOn(ofEpochMilli(startTime))).count().toStream().transform(() -> new Transformer<Windowed<String>, Long, KeyValue<Object, Object>>() {
            private ProcessorContext context;

            @Override
            public void init(final ProcessorContext context) {
                this.context = context;
            }

            @Override
            public KeyValue<Object, Object> transform(final Windowed<String> key, final Long value) {
                results.put(key, KeyValue.pair(value, context.timestamp()));
                latch.countDown();
                return null;
            }

            @Override
            public void close() {
            }
        });
        startStreams();
        Assert.assertTrue(latch.await(30, TimeUnit.SECONDS));
        MatcherAssert.assertThat(results.get(new Windowed("bob", new UnlimitedWindow(startTime))), CoreMatchers.equalTo(KeyValue.pair(2L, t4)));
        MatcherAssert.assertThat(results.get(new Windowed("penny", new UnlimitedWindow(startTime))), CoreMatchers.equalTo(KeyValue.pair(1L, t3)));
        MatcherAssert.assertThat(results.get(new Windowed("jo", new UnlimitedWindow(startTime))), CoreMatchers.equalTo(KeyValue.pair(1L, t4)));
        MatcherAssert.assertThat(results.get(new Windowed("emily", new UnlimitedWindow(startTime))), CoreMatchers.equalTo(KeyValue.pair(1L, t2)));
    }
}

