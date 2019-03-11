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


import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import kafka.utils.MockTime;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.integration.utils.EmbeddedKafkaCluster;
import org.apache.kafka.streams.kstream.ForeachAction;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.test.IntegrationTest;
import org.apache.kafka.test.TestUtils;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ IntegrationTest.class })
public class GlobalKTableEOSIntegrationTest {
    private static final int NUM_BROKERS = 1;

    private static final Properties BROKER_CONFIG;

    static {
        BROKER_CONFIG = new Properties();
        GlobalKTableEOSIntegrationTest.BROKER_CONFIG.put("transaction.state.log.replication.factor", ((short) (1)));
        GlobalKTableEOSIntegrationTest.BROKER_CONFIG.put("transaction.state.log.min.isr", 1);
    }

    @ClassRule
    public static final EmbeddedKafkaCluster CLUSTER = new EmbeddedKafkaCluster(GlobalKTableEOSIntegrationTest.NUM_BROKERS, GlobalKTableEOSIntegrationTest.BROKER_CONFIG);

    private static volatile int testNo = 0;

    private final MockTime mockTime = GlobalKTableEOSIntegrationTest.CLUSTER.time;

    private final KeyValueMapper<String, Long, Long> keyMapper = ( key, value) -> value;

    private final ValueJoiner<Long, String, String> joiner = ( value1, value2) -> (value1 + "+") + value2;

    private final String globalStore = "globalStore";

    private final Map<String, String> results = new HashMap<>();

    private StreamsBuilder builder;

    private Properties streamsConfiguration;

    private KafkaStreams kafkaStreams;

    private String globalTableTopic;

    private String streamTopic;

    private GlobalKTable<Long, String> globalTable;

    private KStream<String, Long> stream;

    private ForeachAction<String, String> foreachAction;

    @Test
    public void shouldKStreamGlobalKTableLeftJoin() throws Exception {
        final KStream<String, String> streamTableJoin = stream.leftJoin(globalTable, keyMapper, joiner);
        streamTableJoin.foreach(foreachAction);
        produceInitialGlobalTableValues();
        startStreams();
        produceTopicValues(streamTopic);
        final Map<String, String> expected = new HashMap<>();
        expected.put("a", "1+A");
        expected.put("b", "2+B");
        expected.put("c", "3+C");
        expected.put("d", "4+D");
        expected.put("e", "5+null");
        TestUtils.waitForCondition(() -> results.equals(expected), 30000L, "waiting for initial values");
        produceGlobalTableValues();
        final ReadOnlyKeyValueStore<Long, String> replicatedStore = kafkaStreams.store(globalStore, QueryableStoreTypes.keyValueStore());
        TestUtils.waitForCondition(() -> "J".equals(replicatedStore.get(5L)), 30000, "waiting for data in replicated store");
        produceTopicValues(streamTopic);
        expected.put("a", "1+F");
        expected.put("b", "2+G");
        expected.put("c", "3+H");
        expected.put("d", "4+I");
        expected.put("e", "5+J");
        TestUtils.waitForCondition(() -> results.equals(expected), 30000L, "waiting for final values");
    }

    @Test
    public void shouldKStreamGlobalKTableJoin() throws Exception {
        final KStream<String, String> streamTableJoin = stream.join(globalTable, keyMapper, joiner);
        streamTableJoin.foreach(foreachAction);
        produceInitialGlobalTableValues();
        startStreams();
        produceTopicValues(streamTopic);
        final Map<String, String> expected = new HashMap<>();
        expected.put("a", "1+A");
        expected.put("b", "2+B");
        expected.put("c", "3+C");
        expected.put("d", "4+D");
        TestUtils.waitForCondition(() -> results.equals(expected), 30000L, "waiting for initial values");
        produceGlobalTableValues();
        final ReadOnlyKeyValueStore<Long, String> replicatedStore = kafkaStreams.store(globalStore, QueryableStoreTypes.keyValueStore());
        TestUtils.waitForCondition(() -> "J".equals(replicatedStore.get(5L)), 30000, "waiting for data in replicated store");
        produceTopicValues(streamTopic);
        expected.put("a", "1+F");
        expected.put("b", "2+G");
        expected.put("c", "3+H");
        expected.put("d", "4+I");
        expected.put("e", "5+J");
        TestUtils.waitForCondition(() -> results.equals(expected), 30000L, "waiting for final values");
    }

    @Test
    public void shouldRestoreTransactionalMessages() throws Exception {
        produceInitialGlobalTableValues();
        startStreams();
        final Map<Long, String> expected = new HashMap<>();
        expected.put(1L, "A");
        expected.put(2L, "B");
        expected.put(3L, "C");
        expected.put(4L, "D");
        TestUtils.waitForCondition(() -> {
            final ReadOnlyKeyValueStore<Long, String> store;
            try {
                store = kafkaStreams.store(globalStore, QueryableStoreTypes.keyValueStore());
            } catch (final  ex) {
                return false;
            }
            final Map<Long, String> result = new HashMap<>();
            final Iterator<KeyValue<Long, String>> it = store.all();
            while (it.hasNext()) {
                final KeyValue<Long, String> kv = it.next();
                result.put(kv.key, kv.value);
            } 
            return result.equals(expected);
        }, 30000L, "waiting for initial values");
    }

    @Test
    public void shouldNotRestoreAbortedMessages() throws Exception {
        produceAbortedMessages();
        produceInitialGlobalTableValues();
        produceAbortedMessages();
        startStreams();
        final Map<Long, String> expected = new HashMap<>();
        expected.put(1L, "A");
        expected.put(2L, "B");
        expected.put(3L, "C");
        expected.put(4L, "D");
        TestUtils.waitForCondition(() -> {
            final ReadOnlyKeyValueStore<Long, String> store;
            try {
                store = kafkaStreams.store(globalStore, QueryableStoreTypes.keyValueStore());
            } catch (final  ex) {
                return false;
            }
            final Map<Long, String> result = new HashMap<>();
            final Iterator<KeyValue<Long, String>> it = store.all();
            while (it.hasNext()) {
                final KeyValue<Long, String> kv = it.next();
                result.put(kv.key, kv.value);
            } 
            return result.equals(expected);
        }, 30000L, "waiting for initial values");
    }
}

