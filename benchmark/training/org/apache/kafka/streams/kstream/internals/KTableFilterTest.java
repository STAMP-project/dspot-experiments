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


import java.util.Properties;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Predicate;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.apache.kafka.test.MockMapper;
import org.apache.kafka.test.MockReducer;
import org.apache.kafka.test.StreamsTestUtils;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("unchecked")
public class KTableFilterTest {
    private final Consumed<String, Integer> consumed = Consumed.with(Serdes.String(), Serdes.Integer());

    private final ConsumerRecordFactory<String, Integer> recordFactory = new ConsumerRecordFactory(new StringSerializer(), new IntegerSerializer());

    private final Properties props = StreamsTestUtils.getStreamsConfig(Serdes.String(), Serdes.Integer());

    private final Predicate<String, Integer> predicate = ( key, value) -> (value % 2) == 0;

    @Test
    public void shouldPassThroughWithoutMaterialization() {
        final StreamsBuilder builder = new StreamsBuilder();
        final String topic1 = "topic1";
        final KTable<String, Integer> table1 = builder.table(topic1, consumed);
        final KTable<String, Integer> table2 = table1.filter(predicate);
        final KTable<String, Integer> table3 = table1.filterNot(predicate);
        Assert.assertNull(table1.queryableStoreName());
        Assert.assertNull(table2.queryableStoreName());
        Assert.assertNull(table3.queryableStoreName());
        doTestKTable(builder, table2, table3, topic1);
    }

    @Test
    public void shouldPassThroughOnMaterialization() {
        final StreamsBuilder builder = new StreamsBuilder();
        final String topic1 = "topic1";
        final KTable<String, Integer> table1 = builder.table(topic1, consumed);
        final KTable<String, Integer> table2 = table1.filter(predicate, Materialized.as("store2"));
        final KTable<String, Integer> table3 = table1.filterNot(predicate);
        Assert.assertNull(table1.queryableStoreName());
        Assert.assertEquals("store2", table2.queryableStoreName());
        Assert.assertNull(table3.queryableStoreName());
        doTestKTable(builder, table2, table3, topic1);
    }

    @Test
    public void shouldGetValuesOnMaterialization() {
        final StreamsBuilder builder = new StreamsBuilder();
        final String topic1 = "topic1";
        final KTableImpl<String, Integer, Integer> table1 = ((KTableImpl<String, Integer, Integer>) (builder.table(topic1, consumed)));
        final KTableImpl<String, Integer, Integer> table2 = ((KTableImpl<String, Integer, Integer>) (table1.filter(predicate, Materialized.as("store2"))));
        final KTableImpl<String, Integer, Integer> table3 = ((KTableImpl<String, Integer, Integer>) (table1.filterNot(predicate, Materialized.as("store3"))));
        final KTableImpl<String, Integer, Integer> table4 = ((KTableImpl<String, Integer, Integer>) (table1.filterNot(predicate)));
        Assert.assertNull(table1.queryableStoreName());
        Assert.assertEquals("store2", table2.queryableStoreName());
        Assert.assertEquals("store3", table3.queryableStoreName());
        Assert.assertNull(table4.queryableStoreName());
        doTestValueGetter(builder, table2, table3, topic1);
    }

    @Test
    public void shouldNotSendOldValuesWithoutMaterialization() {
        final StreamsBuilder builder = new StreamsBuilder();
        final String topic1 = "topic1";
        final KTableImpl<String, Integer, Integer> table1 = ((KTableImpl<String, Integer, Integer>) (builder.table(topic1, consumed)));
        final KTableImpl<String, Integer, Integer> table2 = ((KTableImpl<String, Integer, Integer>) (table1.filter(predicate)));
        doTestNotSendingOldValue(builder, table1, table2, topic1);
    }

    @Test
    public void shouldNotSendOldValuesOnMaterialization() {
        final StreamsBuilder builder = new StreamsBuilder();
        final String topic1 = "topic1";
        final KTableImpl<String, Integer, Integer> table1 = ((KTableImpl<String, Integer, Integer>) (builder.table(topic1, consumed)));
        final KTableImpl<String, Integer, Integer> table2 = ((KTableImpl<String, Integer, Integer>) (table1.filter(predicate, Materialized.as("store2"))));
        doTestNotSendingOldValue(builder, table1, table2, topic1);
    }

    @Test
    public void shouldSendOldValuesWhenEnabledWithoutMaterialization() {
        final StreamsBuilder builder = new StreamsBuilder();
        final String topic1 = "topic1";
        final KTableImpl<String, Integer, Integer> table1 = ((KTableImpl<String, Integer, Integer>) (builder.table(topic1, consumed)));
        final KTableImpl<String, Integer, Integer> table2 = ((KTableImpl<String, Integer, Integer>) (table1.filter(predicate)));
        doTestSendingOldValue(builder, table1, table2, topic1);
    }

    @Test
    public void shouldSendOldValuesWhenEnabledOnMaterialization() {
        final StreamsBuilder builder = new StreamsBuilder();
        final String topic1 = "topic1";
        final KTableImpl<String, Integer, Integer> table1 = ((KTableImpl<String, Integer, Integer>) (builder.table(topic1, consumed)));
        final KTableImpl<String, Integer, Integer> table2 = ((KTableImpl<String, Integer, Integer>) (table1.filter(predicate, Materialized.as("store2"))));
        doTestSendingOldValue(builder, table1, table2, topic1);
    }

    @Test
    public void shouldSkipNullToRepartitionWithoutMaterialization() {
        // Do not explicitly set enableSendingOldValues. Let a further downstream stateful operator trigger it instead.
        final StreamsBuilder builder = new StreamsBuilder();
        final String topic1 = "topic1";
        final Consumed<String, String> consumed = Consumed.with(Serdes.String(), Serdes.String());
        final KTableImpl<String, String, String> table1 = ((KTableImpl<String, String, String>) (builder.table(topic1, consumed)));
        final KTableImpl<String, String, String> table2 = ((KTableImpl<String, String, String>) (table1.filter(( key, value) -> value.equalsIgnoreCase("accept")).groupBy(MockMapper.noOpKeyValueMapper()).reduce(MockReducer.STRING_ADDER, MockReducer.STRING_REMOVER)));
        doTestSkipNullOnMaterialization(builder, table1, table2, topic1);
    }

    @Test
    public void shouldSkipNullToRepartitionOnMaterialization() {
        // Do not explicitly set enableSendingOldValues. Let a further downstream stateful operator trigger it instead.
        final StreamsBuilder builder = new StreamsBuilder();
        final String topic1 = "topic1";
        final Consumed<String, String> consumed = Consumed.with(Serdes.String(), Serdes.String());
        final KTableImpl<String, String, String> table1 = ((KTableImpl<String, String, String>) (builder.table(topic1, consumed)));
        final KTableImpl<String, String, String> table2 = ((KTableImpl<String, String, String>) (table1.filter(( key, value) -> value.equalsIgnoreCase("accept"), Materialized.as("store2")).groupBy(MockMapper.noOpKeyValueMapper()).reduce(MockReducer.STRING_ADDER, MockReducer.STRING_REMOVER, Materialized.as("mock-result"))));
        doTestSkipNullOnMaterialization(builder, table1, table2, topic1);
    }

    @Test
    public void testTypeVariance() {
        final Predicate<Number, Object> numberKeyPredicate = ( key, value) -> false;
        new StreamsBuilder().<Integer, String>table("empty").filter(numberKeyPredicate).filterNot(numberKeyPredicate).toStream().to("nirvana");
    }
}

