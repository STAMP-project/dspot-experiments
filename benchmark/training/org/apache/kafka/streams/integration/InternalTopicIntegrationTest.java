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


import StreamsConfig.APPLICATION_ID_CONFIG;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import kafka.log.LogConfig;
import kafka.utils.MockTime;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.integration.utils.EmbeddedKafkaCluster;
import org.apache.kafka.streams.integration.utils.IntegrationTestUtils;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.processor.internals.ProcessorStateManager;
import org.apache.kafka.streams.state.WindowStore;
import org.apache.kafka.test.IntegrationTest;
import org.apache.kafka.test.MockMapper;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Tests related to internal topics in streams
 */
@Category({ IntegrationTest.class })
public class InternalTopicIntegrationTest {
    @ClassRule
    public static final EmbeddedKafkaCluster CLUSTER = new EmbeddedKafkaCluster(1);

    private static final String APP_ID = "internal-topics-integration-test";

    private static final String DEFAULT_INPUT_TOPIC = "inputTopic";

    private final MockTime mockTime = InternalTopicIntegrationTest.CLUSTER.time;

    private Properties streamsProp;

    @Test
    public void shouldCompactTopicsForKeyValueStoreChangelogs() throws Exception {
        final String appID = (InternalTopicIntegrationTest.APP_ID) + "-compact";
        streamsProp.put(APPLICATION_ID_CONFIG, appID);
        // 
        // Step 1: Configure and start a simple word count topology
        // 
        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, String> textLines = builder.stream(InternalTopicIntegrationTest.DEFAULT_INPUT_TOPIC);
        textLines.flatMapValues(( value) -> Arrays.asList(value.toLowerCase(Locale.getDefault()).split("\\W+"))).groupBy(MockMapper.selectValueMapper()).count(Materialized.as("Counts"));
        final KafkaStreams streams = new KafkaStreams(builder.build(), streamsProp);
        streams.start();
        // 
        // Step 2: Produce some input data to the input topic.
        // 
        produceData(Arrays.asList("hello", "world", "world", "hello world"));
        // 
        // Step 3: Verify the state changelog topics are compact
        // 
        IntegrationTestUtils.waitForCompletion(streams, 2, 30000);
        streams.close();
        final Properties changelogProps = getTopicProperties(ProcessorStateManager.storeChangelogTopic(appID, "Counts"));
        Assert.assertEquals(LogConfig.Compact(), changelogProps.getProperty(LogConfig.CleanupPolicyProp()));
        final Properties repartitionProps = getTopicProperties((appID + "-Counts-repartition"));
        Assert.assertEquals(LogConfig.Delete(), repartitionProps.getProperty(LogConfig.CleanupPolicyProp()));
        Assert.assertEquals(5, repartitionProps.size());
    }

    @Test
    public void shouldCompactAndDeleteTopicsForWindowStoreChangelogs() throws Exception {
        final String appID = (InternalTopicIntegrationTest.APP_ID) + "-compact-delete";
        streamsProp.put(APPLICATION_ID_CONFIG, appID);
        // 
        // Step 1: Configure and start a simple word count topology
        // 
        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, String> textLines = builder.stream(InternalTopicIntegrationTest.DEFAULT_INPUT_TOPIC);
        final int durationMs = 2000;
        textLines.flatMapValues(( value) -> Arrays.asList(value.toLowerCase(Locale.getDefault()).split("\\W+"))).groupBy(MockMapper.selectValueMapper()).windowedBy(TimeWindows.of(Duration.ofSeconds(1L)).grace(Duration.ofMillis(0L))).count(Materialized.<String, Long, WindowStore<Bytes, byte[]>>as("CountWindows").withRetention(Duration.ofSeconds(2L)));
        final KafkaStreams streams = new KafkaStreams(builder.build(), streamsProp);
        streams.start();
        // 
        // Step 2: Produce some input data to the input topic.
        // 
        produceData(Arrays.asList("hello", "world", "world", "hello world"));
        // 
        // Step 3: Verify the state changelog topics are compact
        // 
        IntegrationTestUtils.waitForCompletion(streams, 2, 30000);
        streams.close();
        final Properties properties = getTopicProperties(ProcessorStateManager.storeChangelogTopic(appID, "CountWindows"));
        final List<String> policies = Arrays.asList(properties.getProperty(LogConfig.CleanupPolicyProp()).split(","));
        Assert.assertEquals(2, policies.size());
        Assert.assertTrue(policies.contains(LogConfig.Compact()));
        Assert.assertTrue(policies.contains(LogConfig.Delete()));
        // retention should be 1 day + the window duration
        final long retention = (TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)) + durationMs;
        Assert.assertEquals(retention, Long.parseLong(properties.getProperty(LogConfig.RetentionMsProp())));
        final Properties repartitionProps = getTopicProperties((appID + "-CountWindows-repartition"));
        Assert.assertEquals(LogConfig.Delete(), repartitionProps.getProperty(LogConfig.CleanupPolicyProp()));
        Assert.assertEquals(5, repartitionProps.size());
    }
}

