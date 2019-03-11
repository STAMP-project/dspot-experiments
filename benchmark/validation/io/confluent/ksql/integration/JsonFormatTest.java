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
package io.confluent.ksql.integration;


import KafkaTopicClient.TopicCleanupPolicy.COMPACT;
import KafkaTopicClient.TopicCleanupPolicy.DELETE;
import io.confluent.common.utils.IntegrationTest;
import io.confluent.ksql.GenericRow;
import io.confluent.ksql.engine.KsqlEngine;
import io.confluent.ksql.logging.processing.ProcessingLogContext;
import io.confluent.ksql.metastore.MetaStore;
import io.confluent.ksql.query.QueryId;
import io.confluent.ksql.services.KafkaTopicClient;
import io.confluent.ksql.services.ServiceContext;
import io.confluent.ksql.test.util.EmbeddedSingleNodeKafkaCluster;
import io.confluent.ksql.util.KsqlConfig;
import io.confluent.ksql.util.SchemaUtil;
import io.confluent.ksql.util.TopicConsumer;
import io.confluent.ksql.util.TopicProducer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.test.TestUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ IntegrationTest.class })
public class JsonFormatTest {
    private static final String inputTopic = "orders_topic";

    private static final String inputStream = "ORDERS";

    private static final String usersTopic = "users_topic";

    private static final String usersTable = "USERS";

    private static final String messageLogTopic = "log_topic";

    private static final String messageLogStream = "message_log";

    private static final AtomicInteger COUNTER = new AtomicInteger();

    @ClassRule
    public static final EmbeddedSingleNodeKafkaCluster CLUSTER = EmbeddedSingleNodeKafkaCluster.build();

    private MetaStore metaStore;

    private KsqlConfig ksqlConfig;

    private KsqlEngine ksqlEngine;

    private ServiceContext serviceContext;

    private ProcessingLogContext processingLogContext;

    private final TopicProducer topicProducer = new TopicProducer(JsonFormatTest.CLUSTER);

    private final TopicConsumer topicConsumer = new TopicConsumer(JsonFormatTest.CLUSTER);

    private QueryId queryId;

    private KafkaTopicClient topicClient;

    private String streamName;

    @Test
    public void testSinkProperties() throws Exception {
        final int resultPartitionCount = 3;
        final String queryString = String.format(("CREATE STREAM %s WITH (PARTITIONS = %d) AS SELECT * " + "FROM %s;"), streamName, resultPartitionCount, JsonFormatTest.inputStream);
        executePersistentQuery(queryString);
        TestUtils.waitForCondition(() -> topicClient.isTopicExists(streamName), "Wait for async topic creation");
        MatcherAssert.assertThat(topicClient.describeTopic(streamName).partitions(), Matchers.hasSize(3));
        MatcherAssert.assertThat(topicClient.getTopicCleanupPolicy(streamName), CoreMatchers.equalTo(DELETE));
    }

    @Test
    public void testTableSinkCleanupProperty() throws Exception {
        final String queryString = String.format(("CREATE TABLE %s AS SELECT * " + "FROM %s;"), streamName, JsonFormatTest.usersTable);
        executePersistentQuery(queryString);
        TestUtils.waitForCondition(() -> topicClient.isTopicExists(streamName), "Wait for async topic creation");
        MatcherAssert.assertThat(topicClient.getTopicCleanupPolicy(streamName), CoreMatchers.equalTo(COMPACT));
    }

    @Test
    public void testJsonStreamExtractor() {
        final String queryString = String.format(("CREATE STREAM %s AS SELECT EXTRACTJSONFIELD" + ("(message, '$.log.cloud') " + "FROM %s;")), streamName, JsonFormatTest.messageLogStream);
        executePersistentQuery(queryString);
        final Schema resultSchema = SchemaUtil.removeImplicitRowTimeRowKeyFromSchema(metaStore.getSource(streamName).getSchema());
        final Map<String, GenericRow> expectedResults = new HashMap<>();
        expectedResults.put("1", new GenericRow(Collections.singletonList("aws")));
        final Map<String, GenericRow> results = readNormalResults(streamName, resultSchema, expectedResults.size());
        MatcherAssert.assertThat(results, CoreMatchers.equalTo(expectedResults));
    }

    @Test
    public void testJsonStreamExtractorNested() {
        final String queryString = String.format(("CREATE STREAM %s AS SELECT EXTRACTJSONFIELD" + ("(message, '$.log.logs[0].entry') " + "FROM %s;")), streamName, JsonFormatTest.messageLogStream);
        executePersistentQuery(queryString);
        final Schema resultSchema = SchemaUtil.removeImplicitRowTimeRowKeyFromSchema(metaStore.getSource(streamName).getSchema());
        final Map<String, GenericRow> expectedResults = new HashMap<>();
        expectedResults.put("1", new GenericRow(Collections.singletonList("first")));
        final Map<String, GenericRow> results = readNormalResults(streamName, resultSchema, expectedResults.size());
        MatcherAssert.assertThat(results, CoreMatchers.equalTo(expectedResults));
    }
}

