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
package io.confluent.ksql.rest.entity;


import ConsumerCollector.CONSUMER_TOTAL_MESSAGES;
import StreamsErrorCollector.CONSUMER_FAILED_MESSAGES;
import io.confluent.ksql.metastore.StructuredDataSource;
import io.confluent.ksql.metrics.ConsumerCollector;
import io.confluent.ksql.metrics.StreamsErrorCollector;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class SourceDescriptionTest {
    private static final String CLIENT_ID = "client";

    private static final String APP_ID = "test-app";

    private ConsumerCollector consumerCollector;

    @Test
    public void shouldReturnStatsBasedOnKafkaTopic() {
        // Given:
        final String kafkaTopicName = "kafka";
        final StructuredDataSource dataSource = buildDataSource(kafkaTopicName);
        consumerCollector.onConsume(buildRecords(kafkaTopicName));
        StreamsErrorCollector.recordError(SourceDescriptionTest.APP_ID, kafkaTopicName);
        // When
        final SourceDescription sourceDescription = new SourceDescription(dataSource, true, "json", Collections.emptyList(), Collections.emptyList(), null);
        // Then:
        Assert.assertThat(sourceDescription.getStatistics(), CoreMatchers.containsString(CONSUMER_TOTAL_MESSAGES));
        Assert.assertThat(sourceDescription.getErrorStats(), CoreMatchers.containsString(CONSUMER_FAILED_MESSAGES));
    }
}

