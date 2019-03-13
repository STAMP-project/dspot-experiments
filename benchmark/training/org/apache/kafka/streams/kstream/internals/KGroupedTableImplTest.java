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


import java.util.Map;
import java.util.Properties;
import org.apache.kafka.common.errors.InvalidTopicException;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KGroupedTable;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.test.MockAggregator;
import org.apache.kafka.test.MockInitializer;
import org.apache.kafka.test.MockMapper;
import org.apache.kafka.test.MockReducer;
import org.apache.kafka.test.StreamsTestUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


public class KGroupedTableImplTest {
    private final StreamsBuilder builder = new StreamsBuilder();

    private static final String INVALID_STORE_NAME = "~foo bar~";

    private KGroupedTable<String, String> groupedTable;

    private final Properties props = StreamsTestUtils.getStreamsConfig(Serdes.String(), Serdes.Integer());

    private final String topic = "input";

    @Test(expected = InvalidTopicException.class)
    public void shouldNotAllowInvalidStoreNameOnAggregate() {
        groupedTable.aggregate(MockInitializer.STRING_INIT, MockAggregator.TOSTRING_ADDER, MockAggregator.TOSTRING_REMOVER, Materialized.as(KGroupedTableImplTest.INVALID_STORE_NAME));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullInitializerOnAggregate() {
        groupedTable.aggregate(null, MockAggregator.TOSTRING_ADDER, MockAggregator.TOSTRING_REMOVER, Materialized.as("store"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullAdderOnAggregate() {
        groupedTable.aggregate(MockInitializer.STRING_INIT, null, MockAggregator.TOSTRING_REMOVER, Materialized.as("store"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullSubtractorOnAggregate() {
        groupedTable.aggregate(MockInitializer.STRING_INIT, MockAggregator.TOSTRING_ADDER, null, Materialized.as("store"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullAdderOnReduce() {
        groupedTable.reduce(null, MockReducer.STRING_REMOVER, Materialized.as("store"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullSubtractorOnReduce() {
        groupedTable.reduce(MockReducer.STRING_ADDER, null, Materialized.as("store"));
    }

    @Test(expected = InvalidTopicException.class)
    public void shouldNotAllowInvalidStoreNameOnReduce() {
        groupedTable.reduce(MockReducer.STRING_ADDER, MockReducer.STRING_REMOVER, Materialized.as(KGroupedTableImplTest.INVALID_STORE_NAME));
    }

    @Test
    public void shouldReduce() {
        final KeyValueMapper<String, Number, KeyValue<String, Integer>> intProjection = ( key, value) -> KeyValue.pair(key, value.intValue());
        final KTable<String, Integer> reduced = builder.table(topic, Consumed.with(Serdes.String(), Serdes.Double()), Materialized.<String, Double, KeyValueStore<Bytes, byte[]>>as("store").withKeySerde(Serdes.String()).withValueSerde(Serdes.Double())).groupBy(intProjection).reduce(MockReducer.INTEGER_ADDER, MockReducer.INTEGER_SUBTRACTOR, Materialized.as("reduced"));
        final Map<String, Integer> results = getReducedResults(reduced);
        try (final TopologyTestDriver driver = new TopologyTestDriver(builder.build(), props)) {
            assertReduced(results, topic, driver);
            Assert.assertEquals(reduced.queryableStoreName(), "reduced");
        }
    }

    @Test
    public void shouldReduceWithInternalStoreName() {
        final KeyValueMapper<String, Number, KeyValue<String, Integer>> intProjection = ( key, value) -> KeyValue.pair(key, value.intValue());
        final KTable<String, Integer> reduced = builder.table(topic, Consumed.with(Serdes.String(), Serdes.Double()), Materialized.<String, Double, KeyValueStore<Bytes, byte[]>>as("store").withKeySerde(Serdes.String()).withValueSerde(Serdes.Double())).groupBy(intProjection).reduce(MockReducer.INTEGER_ADDER, MockReducer.INTEGER_SUBTRACTOR);
        final Map<String, Integer> results = getReducedResults(reduced);
        try (final TopologyTestDriver driver = new TopologyTestDriver(builder.build(), props)) {
            assertReduced(results, topic, driver);
            Assert.assertNull(reduced.queryableStoreName());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldReduceAndMaterializeResults() {
        final KeyValueMapper<String, Number, KeyValue<String, Integer>> intProjection = ( key, value) -> KeyValue.pair(key, value.intValue());
        final KTable<String, Integer> reduced = builder.table(topic, Consumed.with(Serdes.String(), Serdes.Double())).groupBy(intProjection).reduce(MockReducer.INTEGER_ADDER, MockReducer.INTEGER_SUBTRACTOR, Materialized.<String, Integer, KeyValueStore<Bytes, byte[]>>as("reduce").withKeySerde(Serdes.String()).withValueSerde(Serdes.Integer()));
        final Map<String, Integer> results = getReducedResults(reduced);
        try (final TopologyTestDriver driver = new TopologyTestDriver(builder.build(), props)) {
            assertReduced(results, topic, driver);
            final KeyValueStore<String, Integer> reduce = driver.getKeyValueStore("reduce");
            MatcherAssert.assertThat(reduce.get("A"), CoreMatchers.equalTo(5));
            MatcherAssert.assertThat(reduce.get("B"), CoreMatchers.equalTo(6));
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldCountAndMaterializeResults() {
        builder.table(topic, Consumed.with(Serdes.String(), Serdes.String())).groupBy(MockMapper.selectValueKeyValueMapper(), Grouped.with(Serdes.String(), Serdes.String())).count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("count").withKeySerde(Serdes.String()).withValueSerde(Serdes.Long()));
        try (final TopologyTestDriver driver = new TopologyTestDriver(builder.build(), props)) {
            processData(topic, driver);
            final KeyValueStore<String, Long> counts = driver.getKeyValueStore("count");
            MatcherAssert.assertThat(counts.get("1"), CoreMatchers.equalTo(3L));
            MatcherAssert.assertThat(counts.get("2"), CoreMatchers.equalTo(2L));
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldAggregateAndMaterializeResults() {
        builder.table(topic, Consumed.with(Serdes.String(), Serdes.String())).groupBy(MockMapper.selectValueKeyValueMapper(), Grouped.with(Serdes.String(), Serdes.String())).aggregate(MockInitializer.STRING_INIT, MockAggregator.TOSTRING_ADDER, MockAggregator.TOSTRING_REMOVER, Materialized.<String, String, KeyValueStore<Bytes, byte[]>>as("aggregate").withValueSerde(Serdes.String()).withKeySerde(Serdes.String()));
        try (final TopologyTestDriver driver = new TopologyTestDriver(builder.build(), props)) {
            processData(topic, driver);
            final KeyValueStore<String, String> aggregate = driver.getKeyValueStore("aggregate");
            MatcherAssert.assertThat(aggregate.get("1"), CoreMatchers.equalTo("0+1+1+1"));
            MatcherAssert.assertThat(aggregate.get("2"), CoreMatchers.equalTo("0+2+2"));
        }
    }

    @SuppressWarnings("unchecked")
    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointOnCountWhenMaterializedIsNull() {
        groupedTable.count(((Materialized) (null)));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerOnReduceWhenMaterializedIsNull() {
        groupedTable.reduce(MockReducer.STRING_ADDER, MockReducer.STRING_REMOVER, ((Materialized) (null)));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerOnReduceWhenAdderIsNull() {
        groupedTable.reduce(null, MockReducer.STRING_REMOVER, Materialized.as("store"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerOnReduceWhenSubtractorIsNull() {
        groupedTable.reduce(MockReducer.STRING_ADDER, null, Materialized.as("store"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerOnAggregateWhenInitializerIsNull() {
        groupedTable.aggregate(null, MockAggregator.TOSTRING_ADDER, MockAggregator.TOSTRING_REMOVER, Materialized.as("store"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerOnAggregateWhenAdderIsNull() {
        groupedTable.aggregate(MockInitializer.STRING_INIT, null, MockAggregator.TOSTRING_REMOVER, Materialized.as("store"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerOnAggregateWhenSubtractorIsNull() {
        groupedTable.aggregate(MockInitializer.STRING_INIT, MockAggregator.TOSTRING_ADDER, null, Materialized.as("store"));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerOnAggregateWhenMaterializedIsNull() {
        groupedTable.aggregate(MockInitializer.STRING_INIT, MockAggregator.TOSTRING_ADDER, MockAggregator.TOSTRING_REMOVER, ((Materialized) (null)));
    }
}

