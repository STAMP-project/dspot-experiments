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


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.ksql.engine.KsqlEngine;
import io.confluent.ksql.metastore.MetaStore;
import io.confluent.ksql.services.KafkaTopicClient;
import io.confluent.ksql.services.ServiceContext;
import io.confluent.ksql.util.KsqlConfig;
import io.confluent.ksql.util.KsqlException;
import io.confluent.ksql.util.PersistentQueryMetadata;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ClusterTerminatorTest {
    private static final String MANAGED_TOPIC_1 = "MANAGED_TOPIC_1";

    private static final String MANAGED_TOPIC_2 = "MANAGED_TOPIC_2";

    private static final List<String> MANAGED_TOPICS = ImmutableList.of(ClusterTerminatorTest.MANAGED_TOPIC_1, ClusterTerminatorTest.MANAGED_TOPIC_2);

    private static final String SOURCE_SUFFIX = "_source";

    @Mock
    private KsqlConfig ksqlConfig;

    @Mock
    private KsqlEngine ksqlEngine;

    @Mock
    private KafkaTopicClient kafkaTopicClient;

    @Mock
    private PersistentQueryMetadata persistentQuery0;

    @Mock
    private PersistentQueryMetadata persistentQuery1;

    @Mock
    private MetaStore metaStore;

    @Mock
    private ServiceContext serviceContext;

    @Mock
    private SchemaRegistryClient schemaRegistryClient;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ClusterTerminator clusterTerminator;

    @Test
    public void shouldClosePersistentQueries() {
        // When:
        clusterTerminator.terminateCluster(Collections.emptyList());
        // Then:
        Mockito.verify(persistentQuery0).close();
        Mockito.verify(persistentQuery1).close();
    }

    @Test
    public void shouldCloseTheEngineAfterTerminatingPersistentQueries() {
        // When:
        clusterTerminator.terminateCluster(Collections.emptyList());
        // Then:
        final InOrder inOrder = Mockito.inOrder(persistentQuery0, ksqlEngine);
        inOrder.verify(persistentQuery0).close();
        inOrder.verify(ksqlEngine).close();
    }

    @Test
    public void shouldClosePersistentQueriesBeforeDeletingTopics() {
        // Given:
        givenTopicsExistInKafka("topic1");
        givenSinkTopicsExistInMetastore("topic1");
        // When:
        clusterTerminator.terminateCluster(Collections.singletonList("topic1"));
        // Then:
        final InOrder inOrder = Mockito.inOrder(kafkaTopicClient, persistentQuery0);
        inOrder.verify(persistentQuery0).close();
        inOrder.verify(kafkaTopicClient).deleteTopics(Collections.singletonList("topic1"));
    }

    @Test
    public void shouldDeleteTopicListWithExplicitTopicName() {
        // Given:
        givenTopicsExistInKafka("K_Foo");
        givenSinkTopicsExistInMetastore("K_Foo");
        // When:
        clusterTerminator.terminateCluster(ImmutableList.of("K_Foo"));
        // Then:
        Mockito.verify(kafkaTopicClient).deleteTopics(Collections.singletonList("K_Foo"));
    }

    @Test
    public void shouldCleanUpSchemasForExplicitTopicList() throws Exception {
        // Given:
        givenTopicsExistInKafka("K_Foo");
        givenSinkTopicsExistInMetastore("K_Foo");
        givenTopicsUseAvroSerdes("K_Foo");
        givenSchemasForTopicsExistInSchemaRegistry("K_Foo");
        // When:
        clusterTerminator.terminateCluster(ImmutableList.of("K_Foo"));
        // Then:
        verifySchemaDeletedForTopics("K_Foo");
    }

    @Test
    public void shouldOnlyDeleteExistingTopics() {
        // Given:
        givenTopicsExistInKafka("K_Bar");
        givenSinkTopicsExistInMetastore("K_Foo", "K_Bar");
        // When:
        clusterTerminator.terminateCluster(ImmutableList.of("K_Foo", "K_Bar"));
        // Then:
        Mockito.verify(kafkaTopicClient).deleteTopics(ImmutableList.of("K_Bar"));
    }

    @Test
    public void shouldCleanUpSchemaEvenIfTopicDoesNotExist() throws Exception {
        // Given:
        givenTopicsExistInKafka("K_Bar");
        givenSinkTopicsExistInMetastore("K_Foo", "K_Bar");
        givenTopicsUseAvroSerdes("K_Foo", "K_Bar");
        givenSchemasForTopicsExistInSchemaRegistry("K_Foo", "K_Bar");
        // When:
        clusterTerminator.terminateCluster(ImmutableList.of("K_Foo", "K_Bar"));
        // Then:
        verifySchemaDeletedForTopics("K_Foo", "K_Bar");
    }

    @Test
    public void shouldNotCleanUpSchemaIfSchemaDoesNotExist() throws Exception {
        // Given:
        givenTopicsExistInKafka("K_Foo", "K_Bar");
        givenSinkTopicsExistInMetastore("K_Foo", "K_Bar");
        givenTopicsUseAvroSerdes("K_Foo", "K_Bar");
        givenSchemasForTopicsExistInSchemaRegistry("K_Bar");
        // When:
        clusterTerminator.terminateCluster(ImmutableList.of("K_Foo", "K_Bar"));
        // Then:
        verifySchemaDeletedForTopics("K_Bar");
        verifySchemaNotDeletedForTopic("K_Foo");
    }

    @Test
    public void shouldNotDeleteNonSinkTopic() {
        // Given:
        givenTopicsExistInKafka("bar");
        givenNonSinkTopicsExistInMetastore("bar");
        // When:
        clusterTerminator.terminateCluster(ImmutableList.of("bar"));
        // Then:
        Mockito.verify(kafkaTopicClient, Mockito.never()).deleteTopics(Collections.singletonList("bar"));
    }

    @Test
    public void shouldNotCleanUpSchemaForNonSinkTopic() throws Exception {
        // Given:
        givenTopicsExistInKafka("bar");
        givenNonSinkTopicsExistInMetastore("bar");
        givenTopicsUseAvroSerdes("bar");
        givenSchemasForTopicsExistInSchemaRegistry("bar");
        // When:
        clusterTerminator.terminateCluster(ImmutableList.of("bar"));
        // Then:
        verifySchemaNotDeletedForTopic("bar");
    }

    @Test
    public void shouldNotDeleteNonMatchingCaseSensitiveTopics() {
        // Given:
        givenTopicsExistInKafka("K_FOO");
        givenSinkTopicsExistInMetastore("K_FOO");
        // When:
        clusterTerminator.terminateCluster(ImmutableList.of("K_Foo"));
        // Then:
        Mockito.verify(kafkaTopicClient, Mockito.times(2)).deleteTopics(Collections.emptyList());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldDeleteTopicListWithPattern() {
        // Given:
        givenTopicsExistInKafka("K_Fo", "K_Foo", "K_Fooo", "NotMatched");
        givenSinkTopicsExistInMetastore("K_Fo", "K_Foo", "K_Fooo", "NotMatched");
        final ArgumentCaptor<Collection> argumentCaptor = ArgumentCaptor.forClass(Collection.class);
        // When:
        clusterTerminator.terminateCluster(ImmutableList.of("K_Fo.*"));
        // Then:
        Mockito.verify(kafkaTopicClient, Mockito.times(2)).deleteTopics(argumentCaptor.capture());
        final Set<String> expectedArgs = ImmutableSet.of("K_Foo", "K_Fooo", "K_Fo");
        MatcherAssert.assertThat(argumentCaptor.getAllValues().get(0).size(), CoreMatchers.equalTo(expectedArgs.size()));
        Assert.assertTrue(expectedArgs.containsAll(argumentCaptor.getAllValues().get(0)));
    }

    @Test
    public void shouldCleanUpSchemasForTopicListWithPattern() throws Exception {
        // Given:
        givenTopicsExistInKafka("K_Fo", "K_Foo", "K_Fooo", "NotMatched");
        givenSinkTopicsExistInMetastore("K_Fo", "K_Foo", "K_Fooo", "NotMatched");
        givenTopicsUseAvroSerdes("K_Fo", "K_Foo", "K_Fooo", "NotMatched");
        givenSchemasForTopicsExistInSchemaRegistry("K_Fo", "K_Foo", "K_Fooo", "NotMatched");
        // When:
        clusterTerminator.terminateCluster(ImmutableList.of("K_Fo.*"));
        // Then:
        verifySchemaDeletedForTopics("K_Foo", "K_Fooo", "K_Fo");
        verifySchemaNotDeletedForTopic("NotMatched");
    }

    @Test
    public void shouldRemoveNonExistentTopicsOnEachDeleteAttempt() {
        // Given:
        givenSinkTopicsExistInMetastore("Foo", "Bar");
        Mockito.when(kafkaTopicClient.listTopicNames()).thenReturn(ImmutableSet.of("Other", "Foo", "Bar")).thenReturn(ImmutableSet.of("Other", "Bar")).thenReturn(ImmutableSet.of("Other"));
        Mockito.doThrow(KsqlException.class).when(kafkaTopicClient).deleteTopics(ArgumentMatchers.any());
        Mockito.doNothing().when(kafkaTopicClient).deleteTopics(Collections.emptyList());
        // When:
        clusterTerminator.terminateCluster(ImmutableList.of("Foo", "Bar"));
        // Then:
        final InOrder inOrder = Mockito.inOrder(kafkaTopicClient);
        inOrder.verify(kafkaTopicClient).deleteTopics(ImmutableList.of("Bar", "Foo"));
        inOrder.verify(kafkaTopicClient).deleteTopics(ImmutableList.of("Bar"));
        inOrder.verify(kafkaTopicClient).deleteTopics(ImmutableList.of());
    }

    @Test
    public void shouldThrowIfCouldNotDeleteTopicListWithPattern() {
        // Given:
        givenTopicsExistInKafka("K_Foo");
        givenSinkTopicsExistInMetastore("K_Foo");
        Mockito.doThrow(KsqlException.class).doThrow(KsqlException.class).doThrow(KsqlException.class).doThrow(KsqlException.class).doThrow(KsqlException.class).when(kafkaTopicClient).deleteTopics(Collections.singletonList("K_Foo"));
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Exception while deleting topics: K_Foo");
        // When:
        clusterTerminator.terminateCluster(ImmutableList.of("K_Fo*"));
    }

    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
    @Test
    public void shouldDeleteManagedTopics() {
        // Given:
        givenTopicsExistInKafka(ClusterTerminatorTest.MANAGED_TOPIC_1, ClusterTerminatorTest.MANAGED_TOPIC_2);
        // When:
        clusterTerminator.terminateCluster(Collections.emptyList());
        // Then:
        final InOrder inOrder = Mockito.inOrder(kafkaTopicClient, ksqlEngine);
        inOrder.verify(kafkaTopicClient).listTopicNames();
        inOrder.verify(kafkaTopicClient).deleteTopics(ClusterTerminatorTest.MANAGED_TOPICS);
    }

    @Test
    public void shouldThrowIfCannotDeleteManagedTopic() {
        // Given:
        givenTopicsExistInKafka(ClusterTerminatorTest.MANAGED_TOPIC_1, ClusterTerminatorTest.MANAGED_TOPIC_2);
        Mockito.doThrow(KsqlException.class).doThrow(KsqlException.class).doThrow(KsqlException.class).doThrow(KsqlException.class).doThrow(KsqlException.class).when(kafkaTopicClient).deleteTopics(ClusterTerminatorTest.MANAGED_TOPICS);
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Exception while deleting topics: MANAGED_TOPIC_1, MANAGED_TOPIC_2");
        // When:
        clusterTerminator.terminateCluster(Collections.emptyList());
    }

    @Test
    public void shouldNotThrowIfCannotCleanUpSchema() throws Exception {
        // Given:
        givenTopicsExistInKafka("K_Foo", "K_Bar");
        givenSinkTopicsExistInMetastore("K_Foo", "K_Bar");
        givenTopicsUseAvroSerdes("K_Foo", "K_Bar");
        givenSchemasForTopicsExistInSchemaRegistry("K_Foo", "K_Bar");
        Mockito.when(schemaRegistryClient.deleteSubject(ArgumentMatchers.startsWith("K_Foo"))).thenThrow(new RestClientException("bad", 404, 40401));
        // When:
        clusterTerminator.terminateCluster(ImmutableList.of("K_Foo", "K_Bar"));
        // Then:
        verifySchemaDeletedForTopics("K_Bar");
    }

    @Test
    public void shouldNotCleanUpSchemaForNonAvroTopic() throws Exception {
        // Given:
        givenTopicsExistInKafka("K_Foo");
        givenSinkTopicsExistInMetastore("K_Foo");
        givenSchemasForTopicsExistInSchemaRegistry("K_Foo");
        // When:
        clusterTerminator.terminateCluster(ImmutableList.of("K_Foo"));
        // Then:
        verifySchemaNotDeletedForTopic("K_Foo");
    }
}

