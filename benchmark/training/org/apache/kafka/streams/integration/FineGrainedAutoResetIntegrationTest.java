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


import ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import ConsumerConfig.METADATA_MAX_AGE_CONFIG;
import StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG;
import StreamsConfig.COMMIT_INTERVAL_MS_CONFIG;
import Topology.AutoOffsetReset.EARLIEST;
import Topology.AutoOffsetReset.LATEST;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import kafka.utils.MockTime;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.TopologyException;
import org.apache.kafka.streams.integration.utils.EmbeddedKafkaCluster;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.test.IntegrationTest;
import org.apache.kafka.test.StreamsTestUtils;
import org.apache.kafka.test.TestUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ IntegrationTest.class })
public class FineGrainedAutoResetIntegrationTest {
    private static final int NUM_BROKERS = 1;

    private static final String DEFAULT_OUTPUT_TOPIC = "outputTopic";

    private static final String OUTPUT_TOPIC_0 = "outputTopic_0";

    private static final String OUTPUT_TOPIC_1 = "outputTopic_1";

    private static final String OUTPUT_TOPIC_2 = "outputTopic_2";

    @ClassRule
    public static final EmbeddedKafkaCluster CLUSTER = new EmbeddedKafkaCluster(FineGrainedAutoResetIntegrationTest.NUM_BROKERS);

    private final MockTime mockTime = FineGrainedAutoResetIntegrationTest.CLUSTER.time;

    private static final String TOPIC_1_0 = "topic-1_0";

    private static final String TOPIC_2_0 = "topic-2_0";

    private static final String TOPIC_A_0 = "topic-A_0";

    private static final String TOPIC_C_0 = "topic-C_0";

    private static final String TOPIC_Y_0 = "topic-Y_0";

    private static final String TOPIC_Z_0 = "topic-Z_0";

    private static final String TOPIC_1_1 = "topic-1_1";

    private static final String TOPIC_2_1 = "topic-2_1";

    private static final String TOPIC_A_1 = "topic-A_1";

    private static final String TOPIC_C_1 = "topic-C_1";

    private static final String TOPIC_Y_1 = "topic-Y_1";

    private static final String TOPIC_Z_1 = "topic-Z_1";

    private static final String TOPIC_1_2 = "topic-1_2";

    private static final String TOPIC_2_2 = "topic-2_2";

    private static final String TOPIC_A_2 = "topic-A_2";

    private static final String TOPIC_C_2 = "topic-C_2";

    private static final String TOPIC_Y_2 = "topic-Y_2";

    private static final String TOPIC_Z_2 = "topic-Z_2";

    private static final String NOOP = "noop";

    private final Serde<String> stringSerde = Serdes.String();

    private static final String STRING_SERDE_CLASSNAME = Serdes.String().getClass().getName();

    private Properties streamsConfiguration;

    private final String topic1TestMessage = "topic-1 test";

    private final String topic2TestMessage = "topic-2 test";

    private final String topicATestMessage = "topic-A test";

    private final String topicCTestMessage = "topic-C test";

    private final String topicYTestMessage = "topic-Y test";

    private final String topicZTestMessage = "topic-Z test";

    @Test
    public void shouldOnlyReadRecordsWhereEarliestSpecifiedWithNoCommittedOffsetsWithGlobalAutoOffsetResetLatest() throws Exception {
        streamsConfiguration.put(StreamsConfig.consumerPrefix(AUTO_OFFSET_RESET_CONFIG), "latest");
        final List<String> expectedReceivedValues = Arrays.asList(topic1TestMessage, topic2TestMessage);
        shouldOnlyReadForEarliest("_0", FineGrainedAutoResetIntegrationTest.TOPIC_1_0, FineGrainedAutoResetIntegrationTest.TOPIC_2_0, FineGrainedAutoResetIntegrationTest.TOPIC_A_0, FineGrainedAutoResetIntegrationTest.TOPIC_C_0, FineGrainedAutoResetIntegrationTest.TOPIC_Y_0, FineGrainedAutoResetIntegrationTest.TOPIC_Z_0, FineGrainedAutoResetIntegrationTest.OUTPUT_TOPIC_0, expectedReceivedValues);
    }

    @Test
    public void shouldOnlyReadRecordsWhereEarliestSpecifiedWithNoCommittedOffsetsWithDefaultGlobalAutoOffsetResetEarliest() throws Exception {
        final List<String> expectedReceivedValues = Arrays.asList(topic1TestMessage, topic2TestMessage, topicYTestMessage, topicZTestMessage);
        shouldOnlyReadForEarliest("_1", FineGrainedAutoResetIntegrationTest.TOPIC_1_1, FineGrainedAutoResetIntegrationTest.TOPIC_2_1, FineGrainedAutoResetIntegrationTest.TOPIC_A_1, FineGrainedAutoResetIntegrationTest.TOPIC_C_1, FineGrainedAutoResetIntegrationTest.TOPIC_Y_1, FineGrainedAutoResetIntegrationTest.TOPIC_Z_1, FineGrainedAutoResetIntegrationTest.OUTPUT_TOPIC_1, expectedReceivedValues);
    }

    @Test
    public void shouldOnlyReadRecordsWhereEarliestSpecifiedWithInvalidCommittedOffsets() throws Exception {
        commitInvalidOffsets();
        final List<String> expectedReceivedValues = Arrays.asList(topic1TestMessage, topic2TestMessage, topicYTestMessage, topicZTestMessage);
        shouldOnlyReadForEarliest("_2", FineGrainedAutoResetIntegrationTest.TOPIC_1_2, FineGrainedAutoResetIntegrationTest.TOPIC_2_2, FineGrainedAutoResetIntegrationTest.TOPIC_A_2, FineGrainedAutoResetIntegrationTest.TOPIC_C_2, FineGrainedAutoResetIntegrationTest.TOPIC_Y_2, FineGrainedAutoResetIntegrationTest.TOPIC_Z_2, FineGrainedAutoResetIntegrationTest.OUTPUT_TOPIC_2, expectedReceivedValues);
    }

    @Test
    public void shouldThrowExceptionOverlappingPattern() {
        final StreamsBuilder builder = new StreamsBuilder();
        // NOTE this would realistically get caught when building topology, the test is for completeness
        builder.stream(Pattern.compile("topic-[A-D]_1"), Consumed.with(EARLIEST));
        try {
            builder.stream(Pattern.compile("topic-[A-D]_1"), Consumed.with(LATEST));
            builder.build();
            Assert.fail("Should have thrown TopologyException");
        } catch (final TopologyException expected) {
            // do nothing
        }
    }

    @Test
    public void shouldThrowExceptionOverlappingTopic() {
        final StreamsBuilder builder = new StreamsBuilder();
        // NOTE this would realistically get caught when building topology, the test is for completeness
        builder.stream(Pattern.compile("topic-[A-D]_1"), Consumed.with(EARLIEST));
        try {
            builder.stream(Arrays.asList(FineGrainedAutoResetIntegrationTest.TOPIC_A_1, FineGrainedAutoResetIntegrationTest.TOPIC_Z_1), Consumed.with(LATEST));
            builder.build();
            Assert.fail("Should have thrown TopologyException");
        } catch (final TopologyException expected) {
            // do nothing
        }
    }

    @Test
    public void shouldThrowStreamsExceptionNoResetSpecified() throws InterruptedException {
        final Properties props = new Properties();
        props.put(CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        props.put(COMMIT_INTERVAL_MS_CONFIG, 100);
        props.put(METADATA_MAX_AGE_CONFIG, "1000");
        props.put(AUTO_OFFSET_RESET_CONFIG, "none");
        final Properties localConfig = StreamsTestUtils.getStreamsConfig("testAutoOffsetWithNone", FineGrainedAutoResetIntegrationTest.CLUSTER.bootstrapServers(), FineGrainedAutoResetIntegrationTest.STRING_SERDE_CLASSNAME, FineGrainedAutoResetIntegrationTest.STRING_SERDE_CLASSNAME, props);
        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, String> exceptionStream = builder.stream(FineGrainedAutoResetIntegrationTest.NOOP);
        exceptionStream.to(FineGrainedAutoResetIntegrationTest.DEFAULT_OUTPUT_TOPIC, Produced.with(stringSerde, stringSerde));
        final KafkaStreams streams = new KafkaStreams(builder.build(), localConfig);
        final FineGrainedAutoResetIntegrationTest.TestingUncaughtExceptionHandler uncaughtExceptionHandler = new FineGrainedAutoResetIntegrationTest.TestingUncaughtExceptionHandler();
        streams.setUncaughtExceptionHandler(uncaughtExceptionHandler);
        streams.start();
        TestUtils.waitForCondition(() -> uncaughtExceptionHandler.correctExceptionThrown, "The expected NoOffsetForPartitionException was never thrown");
        streams.close();
    }

    private static final class TestingUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        boolean correctExceptionThrown = false;

        @Override
        public void uncaughtException(final Thread t, final Throwable e) {
            MatcherAssert.assertThat(e.getClass().getSimpleName(), CoreMatchers.is("StreamsException"));
            MatcherAssert.assertThat(e.getCause().getClass().getSimpleName(), CoreMatchers.is("NoOffsetForPartitionException"));
            correctExceptionThrown = true;
        }
    }
}

