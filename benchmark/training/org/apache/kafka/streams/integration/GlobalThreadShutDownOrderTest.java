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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import kafka.utils.MockTime;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.integration.utils.EmbeddedKafkaCluster;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.test.IntegrationTest;
import org.apache.kafka.test.TestUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ IntegrationTest.class })
public class GlobalThreadShutDownOrderTest {
    private static final int NUM_BROKERS = 1;

    private static final Properties BROKER_CONFIG;

    static {
        BROKER_CONFIG = new Properties();
        GlobalThreadShutDownOrderTest.BROKER_CONFIG.put("transaction.state.log.replication.factor", ((short) (1)));
        GlobalThreadShutDownOrderTest.BROKER_CONFIG.put("transaction.state.log.min.isr", 1);
    }

    @ClassRule
    public static final EmbeddedKafkaCluster CLUSTER = new EmbeddedKafkaCluster(GlobalThreadShutDownOrderTest.NUM_BROKERS, GlobalThreadShutDownOrderTest.BROKER_CONFIG);

    private final MockTime mockTime = GlobalThreadShutDownOrderTest.CLUSTER.time;

    private final String globalStore = "globalStore";

    private StreamsBuilder builder;

    private Properties streamsConfiguration;

    private KafkaStreams kafkaStreams;

    private String globalStoreTopic;

    private String streamTopic;

    private final List<Long> retrievedValuesList = new ArrayList<>();

    private boolean firstRecordProcessed;

    @Test
    public void shouldFinishGlobalStoreOperationOnShutDown() throws Exception {
        kafkaStreams = new KafkaStreams(builder.build(), streamsConfiguration);
        populateTopics(globalStoreTopic);
        populateTopics(streamTopic);
        kafkaStreams.start();
        TestUtils.waitForCondition(() -> firstRecordProcessed, 30000, "Has not processed record within 30 seconds");
        kafkaStreams.close(Duration.ofSeconds(30));
        final List<Long> expectedRetrievedValues = Arrays.asList(1L, 2L, 3L, 4L);
        Assert.assertEquals(expectedRetrievedValues, retrievedValuesList);
    }

    private class GlobalStoreProcessor extends AbstractProcessor<String, Long> {
        private KeyValueStore<String, Long> store;

        private final String storeName;

        GlobalStoreProcessor(final String storeName) {
            this.storeName = storeName;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void init(final ProcessorContext context) {
            super.init(context);
            store = ((KeyValueStore<String, Long>) (context.getStateStore(storeName)));
        }

        @Override
        public void process(final String key, final Long value) {
            firstRecordProcessed = true;
        }

        @Override
        public void close() {
            final List<String> keys = Arrays.asList("A", "B", "C", "D");
            for (final String key : keys) {
                // need to simulate thread slow in closing
                Utils.sleep(1000);
                retrievedValuesList.add(store.get(key));
            }
        }
    }
}

