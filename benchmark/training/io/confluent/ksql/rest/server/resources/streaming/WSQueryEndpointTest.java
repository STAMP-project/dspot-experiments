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
package io.confluent.ksql.rest.server.resources.streaming;


import CloseCodes.CANNOT_ACCEPT;
import CloseCodes.TRY_AGAIN_LATER;
import KsqlConfig.KSQL_SERVICE_ID_CONFIG;
import Versions.KSQL_V1_WS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.ksql.engine.KsqlEngine;
import io.confluent.ksql.parser.KsqlParser.PreparedStatement;
import io.confluent.ksql.parser.tree.Query;
import io.confluent.ksql.parser.tree.QueryBody;
import io.confluent.ksql.rest.entity.KsqlRequest;
import io.confluent.ksql.rest.entity.Versions;
import io.confluent.ksql.rest.server.StatementParser;
import io.confluent.ksql.rest.server.computation.CommandQueue;
import io.confluent.ksql.rest.server.resources.streaming.WSQueryEndpoint.PrintTopicPublisher;
import io.confluent.ksql.rest.server.resources.streaming.WSQueryEndpoint.QueryPublisher;
import io.confluent.ksql.services.KafkaTopicClient;
import io.confluent.ksql.services.ServiceContext;
import io.confluent.ksql.util.KsqlConfig;
import io.confluent.ksql.version.metrics.ActivenessRegistrar;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import javax.websocket.CloseReason;
import javax.websocket.Session;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@SuppressWarnings({ "unchecked", "SameParameterValue" })
@RunWith(MockitoJUnitRunner.class)
public class WSQueryEndpointTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final KsqlRequest VALID_REQUEST = new KsqlRequest("test-sql", ImmutableMap.of(KSQL_SERVICE_ID_CONFIG, "test-id"), null);

    private static final KsqlRequest ANOTHER_REQUEST = new KsqlRequest("other-sql", ImmutableMap.of(), null);

    private static final long SEQUENCE_NUMBER = 2L;

    private static final KsqlRequest REQUEST_WITHOUT_SEQUENCE_NUMBER = WSQueryEndpointTest.VALID_REQUEST;

    private static final KsqlRequest REQUEST_WITH_SEQUENCE_NUMBER = new KsqlRequest("test-sql", ImmutableMap.of(KSQL_SERVICE_ID_CONFIG, "test-id"), WSQueryEndpointTest.SEQUENCE_NUMBER);

    private static final String VALID_VERSION = Versions.KSQL_V1_WS;

    private static final String[] NO_VERSION_PROPERTY = null;

    private static final KsqlRequest[] NO_REQUEST_PROPERTY = ((KsqlRequest[]) (null));

    private static final Duration COMMAND_QUEUE_CATCHUP_TIMEOUT = Duration.ofMillis(5000L);

    @Mock
    private KsqlConfig ksqlConfig;

    @Mock
    private KsqlEngine ksqlEngine;

    @Mock
    private ServiceContext serviceContext;

    @Mock
    private SchemaRegistryClient schemaRegistryClient;

    @Mock
    private KafkaTopicClient topicClient;

    @Mock
    private StatementParser statementParser;

    @Mock
    private ListeningScheduledExecutorService exec;

    @Mock
    private Session session;

    @Mock
    private QueryBody queryBody;

    @Mock
    private CommandQueue commandQueue;

    @Mock
    private QueryPublisher queryPublisher;

    @Mock
    private PrintTopicPublisher topicPublisher;

    @Mock
    private ActivenessRegistrar activenessRegistrar;

    @Captor
    private ArgumentCaptor<CloseReason> closeReasonCaptor;

    private Query query;

    private WSQueryEndpoint wsQueryEndpoint;

    @Test
    public void shouldReturnErrorIfClusterWasTerminated() throws Exception {
        // Given:
        Mockito.when(ksqlEngine.isAcceptingStatements()).thenReturn(false);
        // When:
        wsQueryEndpoint.onOpen(session, null);
        // Then:
        verifyClosedWithReason("The cluster has been terminated. No new request will be accepted.", CANNOT_ACCEPT);
    }

    @Test
    public void shouldReturnErrorOnBadVersion() throws Exception {
        // Given:
        givenVersions("bad-version");
        // When:
        wsQueryEndpoint.onOpen(session, null);
        // Then:
        verifyClosedWithReason("Received invalid api version: [bad-version]", CANNOT_ACCEPT);
    }

    @Test
    public void shouldReturnErrorOnMultipleVersions() throws Exception {
        // Given:
        givenVersions(KSQL_V1_WS, "2");
        // When:
        wsQueryEndpoint.onOpen(session, null);
        // Then:
        verifyClosedWithReason("Received multiple api versions: [1, 2]", CANNOT_ACCEPT);
    }

    @Test
    public void shouldAcceptNoVersion() throws IOException {
        // Given:
        givenVersions(WSQueryEndpointTest.NO_VERSION_PROPERTY);
        // When:
        wsQueryEndpoint.onOpen(session, null);
        // Then:
        Mockito.verify(session, Mockito.never()).close(ArgumentMatchers.any());
    }

    @Test
    public void shouldAcceptEmptyVersions() throws IOException {
        // Given:
        /* empty version */
        givenVersions();
        // When:
        wsQueryEndpoint.onOpen(session, null);
        // Then:
        Mockito.verify(session, Mockito.never()).close(ArgumentMatchers.any());
    }

    @Test
    public void shouldAcceptExplicitVersion() throws IOException {
        // Given:
        givenVersions(KSQL_V1_WS);
        // When:
        wsQueryEndpoint.onOpen(session, null);
        // Then:
        Mockito.verify(session, Mockito.never()).close(ArgumentMatchers.any());
    }

    @Test
    public void shouldReturnErrorOnNoRequest() throws Exception {
        // Given:
        givenRequests(WSQueryEndpointTest.NO_REQUEST_PROPERTY);
        // When:
        wsQueryEndpoint.onOpen(session, null);
        // Then:
        verifyClosedWithReason("Error parsing request: missing request parameter", CANNOT_ACCEPT);
    }

    @Test
    public void shouldReturnErrorOnEmptyRequests() throws Exception {
        // Given:
        /* None */
        givenRequests();
        // When:
        wsQueryEndpoint.onOpen(session, null);
        // Then:
        verifyClosedWithReason("Error parsing request: missing request parameter", CANNOT_ACCEPT);
    }

    @Test
    public void shouldParseStatementText() {
        // Given:
        givenVersions(WSQueryEndpointTest.VALID_VERSION);
        // When:
        wsQueryEndpoint.onOpen(session, null);
        // Then:
        Mockito.verify(statementParser).parseSingleStatement(WSQueryEndpointTest.VALID_REQUEST.getKsql());
    }

    @Test
    public void shouldWeirdlyIgnoreAllButTheLastRequest() {
        // Given:
        givenRequests(WSQueryEndpointTest.ANOTHER_REQUEST, WSQueryEndpointTest.VALID_REQUEST);
        // When:
        wsQueryEndpoint.onOpen(session, null);
        // Then:
        Mockito.verify(statementParser).parseSingleStatement(WSQueryEndpointTest.VALID_REQUEST.getKsql());
    }

    @Test
    public void shouldReturnErrorOnBadStatement() throws Exception {
        // Given:
        Mockito.when(statementParser.parseSingleStatement(ArgumentMatchers.anyString())).thenThrow(new RuntimeException("Boom!"));
        // When:
        wsQueryEndpoint.onOpen(session, null);
        // Then:
        verifyClosedWithReason("Error parsing query: Boom!", CANNOT_ACCEPT);
    }

    @Test
    public void shouldReturnErrorOnInvalidStreamProperty() throws Exception {
        // Given:
        final String jsonRequest = "{" + ((("\"ksql\":\"sql\"," + "\"streamsProperties\":{") + "\"unknown-property\":true") + "}}");
        givenRequest(jsonRequest);
        // When:
        wsQueryEndpoint.onOpen(session, null);
        // Then:
        verifyClosedWithReason("Error parsing request: Failed to set 'unknown-property' to 'true'", CANNOT_ACCEPT);
    }

    @Test
    public void shouldHandleQuery() {
        // Given:
        givenRequestIs(query);
        // When:
        wsQueryEndpoint.onOpen(session, null);
        // Then:
        Mockito.verify(queryPublisher).start(ArgumentMatchers.eq(ksqlConfig), ArgumentMatchers.eq(ksqlEngine), ArgumentMatchers.eq(exec), ArgumentMatchers.eq(PreparedStatement.of(WSQueryEndpointTest.VALID_REQUEST.getKsql(), query)), ArgumentMatchers.eq(WSQueryEndpointTest.VALID_REQUEST.getStreamsProperties()), ArgumentMatchers.any());
    }

    @Test
    public void shouldHandlePrintTopic() {
        // Given:
        givenRequestIs(StreamingTestUtils.printTopic("bob", true, null, null));
        Mockito.when(topicClient.isTopicExists("bob")).thenReturn(true);
        Mockito.when(ksqlConfig.getKsqlStreamConfigProps()).thenReturn(ImmutableMap.of("this", "that"));
        // When:
        wsQueryEndpoint.onOpen(session, null);
        // Then:
        Mockito.verify(topicPublisher).start(ArgumentMatchers.eq(exec), ArgumentMatchers.eq(schemaRegistryClient), ArgumentMatchers.eq(ImmutableMap.of("this", "that")), ArgumentMatchers.eq(StreamingTestUtils.printTopic("bob", true, null, null)), ArgumentMatchers.any());
    }

    @Test
    public void shouldReturnErrorIfTopicDoesNotExist() throws Exception {
        // Given:
        givenRequestIs(StreamingTestUtils.printTopic("bob", true, null, null));
        Mockito.when(topicClient.isTopicExists("bob")).thenReturn(false);
        // When:
        wsQueryEndpoint.onOpen(session, null);
        // Then:
        verifyClosedWithReason("Topic does not exist, or KSQL does not have permission to list the topic: bob", CANNOT_ACCEPT);
    }

    @Test
    public void shouldNotWaitIfNoSequenceNumberSpecified() throws Exception {
        // Given:
        givenRequest(WSQueryEndpointTest.REQUEST_WITHOUT_SEQUENCE_NUMBER);
        // When:
        wsQueryEndpoint.onOpen(session, null);
        // Then:
        Mockito.verify(commandQueue, Mockito.never()).ensureConsumedPast(ArgumentMatchers.anyLong(), ArgumentMatchers.any());
    }

    @Test
    public void shouldWaitIfSequenceNumberSpecified() throws Exception {
        // Given:
        givenRequest(WSQueryEndpointTest.REQUEST_WITH_SEQUENCE_NUMBER);
        // When:
        wsQueryEndpoint.onOpen(session, null);
        // Then:
        Mockito.verify(commandQueue).ensureConsumedPast(ArgumentMatchers.eq(WSQueryEndpointTest.SEQUENCE_NUMBER), ArgumentMatchers.any());
    }

    @Test
    public void shouldReturnErrorIfCommandQueueCatchupTimeout() throws Exception {
        // Given:
        givenRequest(WSQueryEndpointTest.REQUEST_WITH_SEQUENCE_NUMBER);
        Mockito.doThrow(new TimeoutException("yikes")).when(commandQueue).ensureConsumedPast(ArgumentMatchers.eq(WSQueryEndpointTest.SEQUENCE_NUMBER), ArgumentMatchers.any());
        // When:
        wsQueryEndpoint.onOpen(session, null);
        // Then:
        verifyClosedWithReason("yikes", TRY_AGAIN_LATER);
        Mockito.verify(statementParser, Mockito.never()).parseSingleStatement(ArgumentMatchers.any());
    }

    @Test
    public void shouldUpdateTheLastRequestTime() {
        // Given:
        // When:
        wsQueryEndpoint.onOpen(session, null);
        // Then:
        Mockito.verify(activenessRegistrar).updateLastRequestTime();
    }
}

