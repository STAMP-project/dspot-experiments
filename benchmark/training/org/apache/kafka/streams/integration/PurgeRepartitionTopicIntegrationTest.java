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


import ConfigResource.Type;
import DescribeLogDirsResponse.LogDirInfo;
import DescribeLogDirsResponse.ReplicaInfo;
import TopicConfig.CLEANUP_POLICY_CONFIG;
import TopicConfig.CLEANUP_POLICY_DELETE;
import TopicConfig.FILE_DELETE_DELAY_MS_CONFIG;
import TopicConfig.SEGMENT_BYTES_CONFIG;
import TopicConfig.SEGMENT_MS_CONFIG;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.requests.DescribeLogDirsResponse;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.integration.utils.EmbeddedKafkaCluster;
import org.apache.kafka.streams.integration.utils.IntegrationTestUtils;
import org.apache.kafka.test.IntegrationTest;
import org.apache.kafka.test.TestCondition;
import org.apache.kafka.test.TestUtils;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ IntegrationTest.class })
public class PurgeRepartitionTopicIntegrationTest {
    private static final int NUM_BROKERS = 1;

    private static final String INPUT_TOPIC = "input-stream";

    private static final String APPLICATION_ID = "restore-test";

    private static final String REPARTITION_TOPIC = (PurgeRepartitionTopicIntegrationTest.APPLICATION_ID) + "-KSTREAM-AGGREGATE-STATE-STORE-0000000002-repartition";

    private static AdminClient adminClient;

    private static KafkaStreams kafkaStreams;

    private static final Integer PURGE_INTERVAL_MS = 10;

    private static final Integer PURGE_SEGMENT_BYTES = 2000;

    @ClassRule
    public static final EmbeddedKafkaCluster CLUSTER = new EmbeddedKafkaCluster(PurgeRepartitionTopicIntegrationTest.NUM_BROKERS, new Properties() {
        {
            put("log.retention.check.interval.ms", PurgeRepartitionTopicIntegrationTest.PURGE_INTERVAL_MS);
            put(FILE_DELETE_DELAY_MS_CONFIG, 0);
        }
    });

    private final Time time = PurgeRepartitionTopicIntegrationTest.CLUSTER.time;

    private class RepartitionTopicCreatedWithExpectedConfigs implements TestCondition {
        @Override
        public final boolean conditionMet() {
            try {
                final Set<String> topics = PurgeRepartitionTopicIntegrationTest.adminClient.listTopics().names().get();
                if (!(topics.contains(PurgeRepartitionTopicIntegrationTest.REPARTITION_TOPIC))) {
                    return false;
                }
            } catch (final Exception e) {
                return false;
            }
            try {
                final ConfigResource resource = new ConfigResource(Type.TOPIC, PurgeRepartitionTopicIntegrationTest.REPARTITION_TOPIC);
                final Config config = PurgeRepartitionTopicIntegrationTest.adminClient.describeConfigs(Collections.singleton(resource)).values().get(resource).get();
                return ((config.get(CLEANUP_POLICY_CONFIG).value().equals(CLEANUP_POLICY_DELETE)) && (config.get(SEGMENT_MS_CONFIG).value().equals(PurgeRepartitionTopicIntegrationTest.PURGE_INTERVAL_MS.toString()))) && (config.get(SEGMENT_BYTES_CONFIG).value().equals(PurgeRepartitionTopicIntegrationTest.PURGE_SEGMENT_BYTES.toString()));
            } catch (final Exception e) {
                return false;
            }
        }
    }

    private interface TopicSizeVerifier {
        boolean verify(long currentSize);
    }

    private class RepartitionTopicVerified implements TestCondition {
        private final PurgeRepartitionTopicIntegrationTest.TopicSizeVerifier verifier;

        RepartitionTopicVerified(final PurgeRepartitionTopicIntegrationTest.TopicSizeVerifier verifier) {
            this.verifier = verifier;
        }

        @Override
        public final boolean conditionMet() {
            time.sleep(PurgeRepartitionTopicIntegrationTest.PURGE_INTERVAL_MS);
            try {
                final Collection<DescribeLogDirsResponse.LogDirInfo> logDirInfo = PurgeRepartitionTopicIntegrationTest.adminClient.describeLogDirs(Collections.singleton(0)).values().get(0).get().values();
                for (final DescribeLogDirsResponse.LogDirInfo partitionInfo : logDirInfo) {
                    final DescribeLogDirsResponse.ReplicaInfo replicaInfo = partitionInfo.replicaInfos.get(new TopicPartition(PurgeRepartitionTopicIntegrationTest.REPARTITION_TOPIC, 0));
                    if ((replicaInfo != null) && (verifier.verify(replicaInfo.size))) {
                        return true;
                    }
                }
            } catch (final Exception e) {
                // swallow
            }
            return false;
        }
    }

    @Test
    public void shouldRestoreState() throws Exception {
        // produce some data to input topic
        final List<KeyValue<Integer, Integer>> messages = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            messages.add(new KeyValue(i, i));
        }
        IntegrationTestUtils.produceKeyValuesSynchronouslyWithTimestamp(PurgeRepartitionTopicIntegrationTest.INPUT_TOPIC, messages, TestUtils.producerConfig(PurgeRepartitionTopicIntegrationTest.CLUSTER.bootstrapServers(), IntegerSerializer.class, IntegerSerializer.class), time.milliseconds());
        PurgeRepartitionTopicIntegrationTest.kafkaStreams.start();
        TestUtils.waitForCondition(new PurgeRepartitionTopicIntegrationTest.RepartitionTopicCreatedWithExpectedConfigs(), 60000, (("Repartition topic " + (PurgeRepartitionTopicIntegrationTest.REPARTITION_TOPIC)) + " not created with the expected configs after 60000 ms."));
        TestUtils.waitForCondition(new PurgeRepartitionTopicIntegrationTest.RepartitionTopicVerified(( currentSize) -> currentSize > 0), 60000, (("Repartition topic " + (PurgeRepartitionTopicIntegrationTest.REPARTITION_TOPIC)) + " not received data after 60000 ms."));
        // we need long enough timeout to by-pass the log manager's InitialTaskDelayMs, which is hard-coded on server side
        TestUtils.waitForCondition(new PurgeRepartitionTopicIntegrationTest.RepartitionTopicVerified(( currentSize) -> currentSize <= (PurgeRepartitionTopicIntegrationTest.PURGE_SEGMENT_BYTES)), 60000, (("Repartition topic " + (PurgeRepartitionTopicIntegrationTest.REPARTITION_TOPIC)) + " not purged data after 60000 ms."));
    }
}

