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
package io.confluent.ksql.ddl.commands;


import DataSourceSerDe.AVRO;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.ksql.metastore.KsqlTopic;
import io.confluent.ksql.metastore.MutableMetaStore;
import io.confluent.ksql.metastore.StructuredDataSource;
import io.confluent.ksql.services.KafkaTopicClient;
import io.confluent.ksql.util.KsqlConstants;
import io.confluent.ksql.util.KsqlException;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DropSourceCommandTest {
    private static final String STREAM_NAME = "foo";

    private static final String TOPIC_NAME = "foo_topic";

    private static final boolean WITH_DELETE_TOPIC = true;

    private static final boolean WITHOUT_DELETE_TOPIC = false;

    private static final boolean IF_EXISTS = true;

    private static final boolean ALWAYS = false;

    @Mock
    private MutableMetaStore metaStore;

    @Mock
    private KafkaTopicClient kafkaTopicClient;

    @Mock
    private SchemaRegistryClient schemaRegistryClient;

    @Mock
    private StructuredDataSource dataSource;

    @Mock
    private KsqlTopic ksqlTopic;

    private DropSourceCommand dropSourceCommand;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldSucceedOnMissingSourceWithIfExists() {
        // Given:
        givenDropSourceCommand(DropSourceCommandTest.IF_EXISTS, DropSourceCommandTest.WITH_DELETE_TOPIC);
        givenSourceDoesNotExist();
        // When:
        final DdlCommandResult result = dropSourceCommand.run(metaStore);
        // Then:
        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.equalTo("Source foo does not exist."));
    }

    @Test
    public void shouldFailOnMissingSourceWithNoIfExists() {
        // Given:
        givenDropSourceCommand(DropSourceCommandTest.ALWAYS, DropSourceCommandTest.WITH_DELETE_TOPIC);
        givenSourceDoesNotExist();
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Source foo does not exist.");
        // When:
        dropSourceCommand.run(metaStore);
    }

    @Test
    public void shouldDeleteTopicIfDeleteTopicTrue() {
        // Given:
        givenDropSourceCommand(DropSourceCommandTest.ALWAYS, DropSourceCommandTest.WITH_DELETE_TOPIC);
        // When:
        dropSourceCommand.run(metaStore);
        // Then:
        Mockito.verify(kafkaTopicClient).deleteTopics(Collections.singletonList(DropSourceCommandTest.TOPIC_NAME));
    }

    @Test
    public void shouldNotDeleteTopicIfDeleteTopicFalse() {
        // Given:
        givenDropSourceCommand(DropSourceCommandTest.ALWAYS, DropSourceCommandTest.WITHOUT_DELETE_TOPIC);
        // When:
        dropSourceCommand.run(metaStore);
        // Then:
        Mockito.verify(kafkaTopicClient, Mockito.never()).deleteTopics(ArgumentMatchers.any());
    }

    @Test
    public void shouldCleanUpSchemaIfAvroTopic() throws Exception {
        // Given:
        Mockito.when(dataSource.isSerdeFormat(AVRO)).thenReturn(true);
        givenDropSourceCommand(DropSourceCommandTest.ALWAYS, DropSourceCommandTest.WITH_DELETE_TOPIC);
        // When:
        dropSourceCommand.run(metaStore);
        // Then:
        Mockito.verify(schemaRegistryClient).deleteSubject(((DropSourceCommandTest.STREAM_NAME) + (KsqlConstants.SCHEMA_REGISTRY_VALUE_SUFFIX)));
    }

    @Test
    public void shouldNotCleanUpSchemaIfNonAvroTopic() throws Exception {
        // Given:
        Mockito.when(dataSource.isSerdeFormat(AVRO)).thenReturn(false);
        givenDropSourceCommand(DropSourceCommandTest.ALWAYS, DropSourceCommandTest.WITH_DELETE_TOPIC);
        // When:
        dropSourceCommand.run(metaStore);
        // Then:
        Mockito.verify(schemaRegistryClient, Mockito.never()).deleteSubject(ArgumentMatchers.anyString());
    }
}

