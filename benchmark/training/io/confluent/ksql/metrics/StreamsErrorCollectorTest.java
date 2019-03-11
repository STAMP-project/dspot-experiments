/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.metrics;


import io.confluent.ksql.metrics.TopicSensors.Stat;
import java.util.Map;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class StreamsErrorCollectorTest {
    private static final String TOPIC_NAME = "test-topic";

    private static final String APPLICATION_ID_PREFIX = "test-app-id-";

    private static int appCounter;

    private String applicationId;

    @Test
    public void shouldCountStreamsErrors() {
        // When:
        final int nmsgs = 3;
        IntStream.range(0, nmsgs).forEach(( i) -> StreamsErrorCollector.recordError(applicationId, StreamsErrorCollectorTest.TOPIC_NAME));
        // Then:
        Assert.assertThat(MetricCollectors.aggregateStat(StreamsErrorCollector.CONSUMER_FAILED_MESSAGES, true), Matchers.equalTo((1.0 * nmsgs)));
    }

    @Test
    public void shouldComputeErrorRate() {
        // When:
        final int nmsgs = 3;
        IntStream.range(0, nmsgs).forEach(( i) -> StreamsErrorCollector.recordError(applicationId, StreamsErrorCollectorTest.TOPIC_NAME));
        // Then:
        Assert.assertThat(MetricCollectors.aggregateStat(StreamsErrorCollector.CONSUMER_FAILED_MESSAGES_PER_SEC, true), Matchers.greaterThan(0.0));
    }

    @Test
    public void shouldComputeTopicLevelErrorStats() {
        // Given:
        final String otherTopic = "other-topic";
        final int nmsgs = 3;
        // When:
        IntStream.range(0, nmsgs).forEach(( i) -> StreamsErrorCollector.recordError(applicationId, StreamsErrorCollectorTest.TOPIC_NAME));
        IntStream.range(0, (nmsgs + 1)).forEach(( i) -> StreamsErrorCollector.recordError(applicationId, otherTopic));
        // Then:
        final Map<String, Stat> stats = MetricCollectors.getStatsFor(StreamsErrorCollectorTest.TOPIC_NAME, true);
        Assert.assertThat(stats.keySet(), Matchers.hasItem(StreamsErrorCollector.CONSUMER_FAILED_MESSAGES));
        Assert.assertThat(stats.get(StreamsErrorCollector.CONSUMER_FAILED_MESSAGES).getValue(), Matchers.equalTo((nmsgs * 1.0)));
        final Map<String, Stat> otherStats = MetricCollectors.getStatsFor(otherTopic, true);
        Assert.assertThat(otherStats.keySet(), Matchers.hasItem(StreamsErrorCollector.CONSUMER_FAILED_MESSAGES));
        Assert.assertThat(otherStats.get(StreamsErrorCollector.CONSUMER_FAILED_MESSAGES).getValue(), Matchers.equalTo(((nmsgs + 1) * 1.0)));
    }

    @Test
    public void shouldComputeIndependentErrorStatsForQuery() {
        // Given:
        final String otherAppId = StreamsErrorCollectorTest.buildApplicationId();
        final String otherTopicId = "other-topic-id";
        final int nmsgs = 3;
        IntStream.range(0, nmsgs).forEach(( i) -> StreamsErrorCollector.recordError(applicationId, StreamsErrorCollectorTest.TOPIC_NAME));
        IntStream.range(0, (nmsgs + 1)).forEach(( i) -> StreamsErrorCollector.recordError(otherAppId, otherTopicId));
        // When:
        StreamsErrorCollector.notifyApplicationClose(otherAppId);
        // Then:
        Assert.assertThat(MetricCollectors.aggregateStat(StreamsErrorCollector.CONSUMER_FAILED_MESSAGES, true), Matchers.equalTo((nmsgs * 1.0)));
    }
}

