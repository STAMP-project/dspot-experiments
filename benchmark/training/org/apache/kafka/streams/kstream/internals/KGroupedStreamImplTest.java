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
package org.apache.kafka.streams.kstream.internals;


import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.errors.InvalidTopicException;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.SessionWindows;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.kstream.Windows;
import org.apache.kafka.streams.processor.internals.testutil.LogCaptureAppender;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.SessionStore;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.apache.kafka.test.MockAggregator;
import org.apache.kafka.test.MockInitializer;
import org.apache.kafka.test.MockReducer;
import org.apache.kafka.test.StreamsTestUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("unchecked")
public class KGroupedStreamImplTest {
    private static final String TOPIC = "topic";

    private static final String INVALID_STORE_NAME = "~foo bar~";

    private final StreamsBuilder builder = new StreamsBuilder();

    private KGroupedStream<String, String> groupedStream;

    private final ConsumerRecordFactory<String, String> recordFactory = new ConsumerRecordFactory(new StringSerializer(), new StringSerializer());

    private final Properties props = StreamsTestUtils.getStreamsConfig(Serdes.String(), Serdes.String());

    @Test(expected = NullPointerException.class)
    public void shouldNotHaveNullReducerOnReduce() {
        groupedStream.reduce(null);
    }

    @Test(expected = InvalidTopicException.class)
    public void shouldNotHaveInvalidStoreNameOnReduce() {
        groupedStream.reduce(MockReducer.STRING_ADDER, Materialized.as(KGroupedStreamImplTest.INVALID_STORE_NAME));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotHaveNullReducerWithWindowedReduce() {
        groupedStream.windowedBy(TimeWindows.of(Duration.ofMillis(10))).reduce(null, Materialized.as("store"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotHaveNullWindowsWithWindowedReduce() {
        groupedStream.windowedBy(((Windows) (null)));
    }

    @Test(expected = InvalidTopicException.class)
    public void shouldNotHaveInvalidStoreNameWithWindowedReduce() {
        groupedStream.windowedBy(TimeWindows.of(Duration.ofMillis(10))).reduce(MockReducer.STRING_ADDER, Materialized.as(KGroupedStreamImplTest.INVALID_STORE_NAME));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotHaveNullInitializerOnAggregate() {
        groupedStream.aggregate(null, MockAggregator.TOSTRING_ADDER, Materialized.as("store"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotHaveNullAdderOnAggregate() {
        groupedStream.aggregate(MockInitializer.STRING_INIT, null, Materialized.as("store"));
    }

    @Test(expected = InvalidTopicException.class)
    public void shouldNotHaveInvalidStoreNameOnAggregate() {
        groupedStream.aggregate(MockInitializer.STRING_INIT, MockAggregator.TOSTRING_ADDER, Materialized.as(KGroupedStreamImplTest.INVALID_STORE_NAME));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotHaveNullInitializerOnWindowedAggregate() {
        groupedStream.windowedBy(TimeWindows.of(Duration.ofMillis(10))).aggregate(null, MockAggregator.TOSTRING_ADDER, Materialized.as("store"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotHaveNullAdderOnWindowedAggregate() {
        groupedStream.windowedBy(TimeWindows.of(Duration.ofMillis(10))).aggregate(MockInitializer.STRING_INIT, null, Materialized.as("store"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotHaveNullWindowsOnWindowedAggregate() {
        groupedStream.windowedBy(((Windows) (null)));
    }

    @Test(expected = InvalidTopicException.class)
    public void shouldNotHaveInvalidStoreNameOnWindowedAggregate() {
        groupedStream.windowedBy(TimeWindows.of(Duration.ofMillis(10))).aggregate(MockInitializer.STRING_INIT, MockAggregator.TOSTRING_ADDER, Materialized.as(KGroupedStreamImplTest.INVALID_STORE_NAME));
    }

    @Test
    public void shouldAggregateSessionWindows() {
        final Map<Windowed<String>, Integer> results = new HashMap<>();
        final KTable<Windowed<String>, Integer> table = groupedStream.windowedBy(SessionWindows.with(Duration.ofMillis(30))).aggregate(() -> 0, ( aggKey, value, aggregate) -> aggregate + 1, ( aggKey, aggOne, aggTwo) -> aggOne + aggTwo, Materialized.<String, Integer, SessionStore<Bytes, byte[]>>as("session-store").withValueSerde(Serdes.Integer()));
        table.toStream().foreach(results::put);
        doAggregateSessionWindows(results);
        Assert.assertEquals(table.queryableStoreName(), "session-store");
    }

    @Test
    public void shouldAggregateSessionWindowsWithInternalStoreName() {
        final Map<Windowed<String>, Integer> results = new HashMap<>();
        final KTable<Windowed<String>, Integer> table = groupedStream.windowedBy(SessionWindows.with(Duration.ofMillis(30))).aggregate(() -> 0, ( aggKey, value, aggregate) -> aggregate + 1, ( aggKey, aggOne, aggTwo) -> aggOne + aggTwo, Materialized.with(null, Serdes.Integer()));
        table.toStream().foreach(results::put);
        doAggregateSessionWindows(results);
    }

    @Test
    public void shouldCountSessionWindows() {
        final Map<Windowed<String>, Long> results = new HashMap<>();
        final KTable<Windowed<String>, Long> table = groupedStream.windowedBy(SessionWindows.with(Duration.ofMillis(30))).count(Materialized.as("session-store"));
        table.toStream().foreach(results::put);
        doCountSessionWindows(results);
        Assert.assertEquals(table.queryableStoreName(), "session-store");
    }

    @Test
    public void shouldCountSessionWindowsWithInternalStoreName() {
        final Map<Windowed<String>, Long> results = new HashMap<>();
        final KTable<Windowed<String>, Long> table = groupedStream.windowedBy(SessionWindows.with(Duration.ofMillis(30))).count();
        table.toStream().foreach(results::put);
        doCountSessionWindows(results);
        Assert.assertNull(table.queryableStoreName());
    }

    @Test
    public void shouldReduceSessionWindows() {
        final Map<Windowed<String>, String> results = new HashMap<>();
        final KTable<Windowed<String>, String> table = groupedStream.windowedBy(SessionWindows.with(Duration.ofMillis(30))).reduce(( value1, value2) -> (value1 + ":") + value2, Materialized.as("session-store"));
        table.toStream().foreach(results::put);
        doReduceSessionWindows(results);
        Assert.assertEquals(table.queryableStoreName(), "session-store");
    }

    @Test
    public void shouldReduceSessionWindowsWithInternalStoreName() {
        final Map<Windowed<String>, String> results = new HashMap<>();
        final KTable<Windowed<String>, String> table = groupedStream.windowedBy(SessionWindows.with(Duration.ofMillis(30))).reduce(( value1, value2) -> (value1 + ":") + value2);
        table.toStream().foreach(results::put);
        doReduceSessionWindows(results);
        Assert.assertNull(table.queryableStoreName());
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullReducerWhenReducingSessionWindows() {
        groupedStream.windowedBy(SessionWindows.with(Duration.ofMillis(30))).reduce(null, Materialized.as("store"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullSessionWindowsReducingSessionWindows() {
        groupedStream.windowedBy(((SessionWindows) (null)));
    }

    @Test(expected = InvalidTopicException.class)
    public void shouldNotAcceptInvalidStoreNameWhenReducingSessionWindows() {
        groupedStream.windowedBy(SessionWindows.with(Duration.ofMillis(30))).reduce(MockReducer.STRING_ADDER, Materialized.as(KGroupedStreamImplTest.INVALID_STORE_NAME));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullStateStoreSupplierWhenReducingSessionWindows() {
        groupedStream.windowedBy(SessionWindows.with(Duration.ofMillis(30))).reduce(null, Materialized.<String, String, SessionStore<Bytes, byte[]>>as(null));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullInitializerWhenAggregatingSessionWindows() {
        groupedStream.windowedBy(SessionWindows.with(Duration.ofMillis(30))).aggregate(null, MockAggregator.TOSTRING_ADDER, ( aggKey, aggOne, aggTwo) -> null, Materialized.as("storeName"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullAggregatorWhenAggregatingSessionWindows() {
        groupedStream.windowedBy(SessionWindows.with(Duration.ofMillis(30))).aggregate(MockInitializer.STRING_INIT, null, ( aggKey, aggOne, aggTwo) -> null, Materialized.as("storeName"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullSessionMergerWhenAggregatingSessionWindows() {
        groupedStream.windowedBy(SessionWindows.with(Duration.ofMillis(30))).aggregate(MockInitializer.STRING_INIT, MockAggregator.TOSTRING_ADDER, null, Materialized.as("storeName"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullSessionWindowsWhenAggregatingSessionWindows() {
        groupedStream.windowedBy(((SessionWindows) (null)));
    }

    @Test
    public void shouldAcceptNullStoreNameWhenAggregatingSessionWindows() {
        groupedStream.windowedBy(SessionWindows.with(Duration.ofMillis(10))).aggregate(MockInitializer.STRING_INIT, MockAggregator.TOSTRING_ADDER, ( aggKey, aggOne, aggTwo) -> null, Materialized.with(Serdes.String(), Serdes.String()));
    }

    @Test(expected = InvalidTopicException.class)
    public void shouldNotAcceptInvalidStoreNameWhenAggregatingSessionWindows() {
        groupedStream.windowedBy(SessionWindows.with(Duration.ofMillis(10))).aggregate(MockInitializer.STRING_INIT, MockAggregator.TOSTRING_ADDER, ( aggKey, aggOne, aggTwo) -> null, Materialized.as(KGroupedStreamImplTest.INVALID_STORE_NAME));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerOnReduceWhenMaterializedIsNull() {
        groupedStream.reduce(MockReducer.STRING_ADDER, ((Materialized) (null)));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerOnAggregateWhenMaterializedIsNull() {
        groupedStream.aggregate(MockInitializer.STRING_INIT, MockAggregator.TOSTRING_ADDER, ((Materialized) (null)));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerOnCountWhenMaterializedIsNull() {
        groupedStream.count(((Materialized) (null)));
    }

    @Test
    public void shouldCountAndMaterializeResults() {
        groupedStream.count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("count").withKeySerde(Serdes.String()));
        try (final TopologyTestDriver driver = new TopologyTestDriver(builder.build(), props)) {
            processData(driver);
            final KeyValueStore<String, Long> count = driver.getKeyValueStore("count");
            MatcherAssert.assertThat(count.get("1"), CoreMatchers.equalTo(3L));
            MatcherAssert.assertThat(count.get("2"), CoreMatchers.equalTo(1L));
            MatcherAssert.assertThat(count.get("3"), CoreMatchers.equalTo(2L));
        }
    }

    @Test
    public void shouldLogAndMeasureSkipsInAggregate() {
        groupedStream.count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("count").withKeySerde(Serdes.String()));
        final LogCaptureAppender appender = LogCaptureAppender.createAndRegister();
        try (final TopologyTestDriver driver = new TopologyTestDriver(builder.build(), props)) {
            processData(driver);
            LogCaptureAppender.unregister(appender);
            final Map<MetricName, ? extends Metric> metrics = driver.metrics();
            Assert.assertEquals(1.0, StreamsTestUtils.getMetricByName(metrics, "skipped-records-total", "stream-metrics").metricValue());
            Assert.assertNotEquals(0.0, StreamsTestUtils.getMetricByName(metrics, "skipped-records-rate", "stream-metrics").metricValue());
            MatcherAssert.assertThat(appender.getMessages(), CoreMatchers.hasItem("Skipping record due to null key or value. key=[3] value=[null] topic=[topic] partition=[0] offset=[6]"));
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldReduceAndMaterializeResults() {
        groupedStream.reduce(MockReducer.STRING_ADDER, Materialized.<String, String, KeyValueStore<Bytes, byte[]>>as("reduce").withKeySerde(Serdes.String()).withValueSerde(Serdes.String()));
        try (final TopologyTestDriver driver = new TopologyTestDriver(builder.build(), props)) {
            processData(driver);
            final KeyValueStore<String, String> reduced = driver.getKeyValueStore("reduce");
            MatcherAssert.assertThat(reduced.get("1"), CoreMatchers.equalTo("A+C+D"));
            MatcherAssert.assertThat(reduced.get("2"), CoreMatchers.equalTo("B"));
            MatcherAssert.assertThat(reduced.get("3"), CoreMatchers.equalTo("E+F"));
        }
    }

    @Test
    public void shouldLogAndMeasureSkipsInReduce() {
        groupedStream.reduce(MockReducer.STRING_ADDER, Materialized.<String, String, KeyValueStore<Bytes, byte[]>>as("reduce").withKeySerde(Serdes.String()).withValueSerde(Serdes.String()));
        final LogCaptureAppender appender = LogCaptureAppender.createAndRegister();
        try (final TopologyTestDriver driver = new TopologyTestDriver(builder.build(), props)) {
            processData(driver);
            LogCaptureAppender.unregister(appender);
            final Map<MetricName, ? extends Metric> metrics = driver.metrics();
            Assert.assertEquals(1.0, StreamsTestUtils.getMetricByName(metrics, "skipped-records-total", "stream-metrics").metricValue());
            Assert.assertNotEquals(0.0, StreamsTestUtils.getMetricByName(metrics, "skipped-records-rate", "stream-metrics").metricValue());
            MatcherAssert.assertThat(appender.getMessages(), CoreMatchers.hasItem("Skipping record due to null key or value. key=[3] value=[null] topic=[topic] partition=[0] offset=[6]"));
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldAggregateAndMaterializeResults() {
        groupedStream.aggregate(MockInitializer.STRING_INIT, MockAggregator.TOSTRING_ADDER, Materialized.<String, String, KeyValueStore<Bytes, byte[]>>as("aggregate").withKeySerde(Serdes.String()).withValueSerde(Serdes.String()));
        try (final TopologyTestDriver driver = new TopologyTestDriver(builder.build(), props)) {
            processData(driver);
            final KeyValueStore<String, String> aggregate = driver.getKeyValueStore("aggregate");
            MatcherAssert.assertThat(aggregate.get("1"), CoreMatchers.equalTo("0+A+C+D"));
            MatcherAssert.assertThat(aggregate.get("2"), CoreMatchers.equalTo("0+B"));
            MatcherAssert.assertThat(aggregate.get("3"), CoreMatchers.equalTo("0+E+F"));
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldAggregateWithDefaultSerdes() {
        final Map<String, String> results = new HashMap<>();
        groupedStream.aggregate(MockInitializer.STRING_INIT, MockAggregator.TOSTRING_ADDER).toStream().foreach(results::put);
        try (final TopologyTestDriver driver = new TopologyTestDriver(builder.build(), props)) {
            processData(driver);
            MatcherAssert.assertThat(results.get("1"), CoreMatchers.equalTo("0+A+C+D"));
            MatcherAssert.assertThat(results.get("2"), CoreMatchers.equalTo("0+B"));
            MatcherAssert.assertThat(results.get("3"), CoreMatchers.equalTo("0+E+F"));
        }
    }

    @Test
    public void shouldCountWindowed() {
        final List<KeyValue<Windowed<String>, Long>> results = new ArrayList<>();
        groupedStream.windowedBy(TimeWindows.of(Duration.ofMillis(500L))).count(Materialized.as("aggregate-by-key-windowed")).toStream().foreach(( key, value) -> results.add(KeyValue.pair(key, value)));
        doCountWindowed(results);
    }

    @Test
    public void shouldCountWindowedWithInternalStoreName() {
        final List<KeyValue<Windowed<String>, Long>> results = new ArrayList<>();
        groupedStream.windowedBy(TimeWindows.of(Duration.ofMillis(500L))).count().toStream().foreach(( key, value) -> results.add(KeyValue.pair(key, value)));
        doCountWindowed(results);
    }
}

