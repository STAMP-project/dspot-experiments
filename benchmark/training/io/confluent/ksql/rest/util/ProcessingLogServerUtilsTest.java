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
package io.confluent.ksql.rest.util;


import KsqlConfig.KSQL_SERVICE_ID_CONFIG;
import ProcessingLogConfig.STREAM_AUTO_CREATE;
import ProcessingLogConfig.STREAM_NAME;
import ProcessingLogConfig.TOPIC_AUTO_CREATE;
import ProcessingLogConfig.TOPIC_NAME;
import ProcessingLogConfig.TOPIC_PARTITIONS;
import ProcessingLogConfig.TOPIC_REPLICATION_FACTOR;
import com.google.common.collect.ImmutableMap;
import io.confluent.ksql.engine.KsqlEngine;
import io.confluent.ksql.engine.KsqlEngineTestUtil;
import io.confluent.ksql.function.InternalFunctionRegistry;
import io.confluent.ksql.logging.processing.ProcessingLogConfig;
import io.confluent.ksql.metastore.MutableMetaStore;
import io.confluent.ksql.parser.KsqlParser.PreparedStatement;
import io.confluent.ksql.parser.SqlFormatter;
import io.confluent.ksql.services.KafkaTopicClient;
import io.confluent.ksql.services.ServiceContext;
import io.confluent.ksql.services.TestServiceContext;
import io.confluent.ksql.util.KsqlConfig;
import java.util.Collections;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ProcessingLogServerUtilsTest {
    private static final String STREAM = "PROCESSING_LOG_STREAM";

    private static final String TOPIC = "processing_log_topic";

    private static final String CLUSTER_ID = "ksql_cluster.";

    private static final int PARTITIONS = 10;

    private static final short REPLICAS = 3;

    private static final String DEFAULT_TOPIC = (ProcessingLogServerUtilsTest.CLUSTER_ID) + (ProcessingLogConfig.TOPIC_NAME_DEFAULT_SUFFIX);

    private final ServiceContext serviceContext = TestServiceContext.create();

    private final KafkaTopicClient spyTopicClient = Mockito.spy(serviceContext.getTopicClient());

    private final MutableMetaStore metaStore = new io.confluent.ksql.metastore.MetaStoreImpl(new InternalFunctionRegistry());

    private final KsqlEngine ksqlEngine = KsqlEngineTestUtil.createKsqlEngine(serviceContext, metaStore);

    private final ProcessingLogConfig config = new ProcessingLogConfig(ImmutableMap.of(TOPIC_AUTO_CREATE, true, TOPIC_NAME, ProcessingLogServerUtilsTest.TOPIC, TOPIC_PARTITIONS, ProcessingLogServerUtilsTest.PARTITIONS, TOPIC_REPLICATION_FACTOR, ProcessingLogServerUtilsTest.REPLICAS, STREAM_NAME, ProcessingLogServerUtilsTest.STREAM));

    private final KsqlConfig ksqlConfig = new KsqlConfig(ImmutableMap.of(KSQL_SERVICE_ID_CONFIG, ProcessingLogServerUtilsTest.CLUSTER_ID));

    @Mock
    private KafkaTopicClient mockTopicClient;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldBuildCorrectStreamCreateDDL() {
        // Given:
        serviceContext.getTopicClient().createTopic(ProcessingLogServerUtilsTest.TOPIC, 1, ((short) (1)));
        // When:
        final PreparedStatement<?> statement = ProcessingLogServerUtils.processingLogStreamCreateStatement(config, ksqlConfig);
        // Then:
        Assert.assertThat(statement.getStatementText(), Matchers.equalTo(SqlFormatter.formatSql(statement.getStatement())));
        ksqlEngine.execute(statement, ksqlConfig, Collections.emptyMap());
        assertLogStream(ProcessingLogServerUtilsTest.TOPIC);
    }

    @Test
    public void shouldBuildCorrectStreamCreateDDLWithDefaultTopicName() {
        // Given:
        serviceContext.getTopicClient().createTopic(ProcessingLogServerUtilsTest.DEFAULT_TOPIC, 1, ((short) (1)));
        // When:
        final PreparedStatement<?> statement = ProcessingLogServerUtils.processingLogStreamCreateStatement(new ProcessingLogConfig(ImmutableMap.of(STREAM_AUTO_CREATE, true, STREAM_NAME, ProcessingLogServerUtilsTest.STREAM)), ksqlConfig);
        // Then:
        Assert.assertThat(statement.getStatementText(), Matchers.equalTo(SqlFormatter.formatSql(statement.getStatement())));
        ksqlEngine.execute(statement, ksqlConfig, Collections.emptyMap());
        assertLogStream(ProcessingLogServerUtilsTest.DEFAULT_TOPIC);
    }

    @Test
    public void shouldNotCreateLogTopicIfNotConfigured() {
        // Given:
        final ProcessingLogConfig config = new ProcessingLogConfig(ImmutableMap.of(TOPIC_AUTO_CREATE, false));
        // When:
        final Optional<String> createdTopic = ProcessingLogServerUtils.maybeCreateProcessingLogTopic(spyTopicClient, config, ksqlConfig);
        // Then:
        Assert.assertThat(createdTopic.isPresent(), Matchers.is(false));
        Mockito.verifyZeroInteractions(spyTopicClient);
    }

    @Test
    public void shouldThrowOnUnexpectedKafkaClientError() {
        Mockito.doThrow(new RuntimeException("bad")).when(mockTopicClient).createTopic(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyShort());
        expectedException.expectMessage("bad");
        expectedException.expect(RuntimeException.class);
        ProcessingLogServerUtils.maybeCreateProcessingLogTopic(mockTopicClient, config, ksqlConfig);
    }

    @Test
    public void shouldCreateProcessingLogTopic() {
        // When:
        final Optional<String> createdTopic = ProcessingLogServerUtils.maybeCreateProcessingLogTopic(mockTopicClient, config, ksqlConfig);
        // Then:
        Assert.assertThat(createdTopic.isPresent(), Matchers.is(true));
        Assert.assertThat(createdTopic.get(), Matchers.equalTo(ProcessingLogServerUtilsTest.TOPIC));
        Mockito.verify(mockTopicClient).createTopic(ProcessingLogServerUtilsTest.TOPIC, ProcessingLogServerUtilsTest.PARTITIONS, ProcessingLogServerUtilsTest.REPLICAS);
    }

    @Test
    public void shouldCreateProcessingLogTopicWithCorrectDefaultName() {
        // Given:
        final ProcessingLogConfig config = new ProcessingLogConfig(ImmutableMap.of(TOPIC_AUTO_CREATE, true, TOPIC_PARTITIONS, ProcessingLogServerUtilsTest.PARTITIONS, TOPIC_REPLICATION_FACTOR, ProcessingLogServerUtilsTest.REPLICAS));
        // When:
        final Optional<String> createdTopic = ProcessingLogServerUtils.maybeCreateProcessingLogTopic(mockTopicClient, config, ksqlConfig);
        // Then:
        Assert.assertThat(createdTopic.isPresent(), Matchers.is(true));
        Assert.assertThat(createdTopic.get(), Matchers.equalTo(ProcessingLogServerUtilsTest.DEFAULT_TOPIC));
        Mockito.verify(mockTopicClient).createTopic(ProcessingLogServerUtilsTest.DEFAULT_TOPIC, ProcessingLogServerUtilsTest.PARTITIONS, ProcessingLogServerUtilsTest.REPLICAS);
    }
}

