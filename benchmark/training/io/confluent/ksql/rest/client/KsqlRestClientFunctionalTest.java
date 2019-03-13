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
package io.confluent.ksql.rest.client;


import CommandId.Action;
import CommandId.Type;
import CommandStatus.Status.ERROR;
import CommandStatus.Status.SUCCESS;
import Errors.ERROR_CODE_FORBIDDEN;
import Errors.ERROR_CODE_UNAUTHORIZED;
import Status.EXPECTATION_FAILED;
import Status.FORBIDDEN;
import Status.NOT_FOUND;
import Status.UNAUTHORIZED;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.confluent.ksql.GenericRow;
import io.confluent.ksql.rest.client.KsqlRestClient.QueryStream;
import io.confluent.ksql.rest.client.exception.KsqlRestClientException;
import io.confluent.ksql.rest.entity.CommandStatus;
import io.confluent.ksql.rest.entity.CommandStatuses;
import io.confluent.ksql.rest.entity.ExecutionPlan;
import io.confluent.ksql.rest.entity.KsqlEntityList;
import io.confluent.ksql.rest.entity.KsqlErrorMessage;
import io.confluent.ksql.rest.entity.ServerInfo;
import io.confluent.ksql.rest.entity.StreamedRow;
import io.confluent.ksql.rest.server.mock.MockApplication;
import io.confluent.ksql.rest.server.mock.MockStreamedQueryResource;
import io.confluent.ksql.rest.server.resources.Errors;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class KsqlRestClientFunctionalTest {
    private MockApplication mockApplication;

    private KsqlRestClient ksqlRestClient;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testKsqlResource() {
        final RestResponse<KsqlEntityList> results = ksqlRestClient.makeKsqlRequest("Test request", null);
        Assert.assertThat(results, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(results.isSuccessful(), Matchers.is(true));
        final KsqlEntityList ksqlEntityList = results.getResponse();
        Assert.assertThat(ksqlEntityList, Matchers.hasSize(1));
        Assert.assertThat(ksqlEntityList.get(0), Matchers.is(Matchers.instanceOf(ExecutionPlan.class)));
    }

    @Test
    public void testStreamRowFromServer() throws InterruptedException {
        // Given:
        final RestResponse<KsqlRestClient.QueryStream> queryResponse = ksqlRestClient.makeQueryRequest("Select *", null);
        final KsqlRestClientFunctionalTest.ReceiverThread receiver = new KsqlRestClientFunctionalTest.ReceiverThread(queryResponse);
        final MockStreamedQueryResource.TestStreamWriter writer = getResponseWriter();
        // When:
        writer.enq("hello");
        writer.enq("world");
        writer.enq("{\"row\":null,\"errorMessage\":null,\"finalMessage\":\"Limit Reached\"}");
        writer.finished();
        // Then:
        Assert.assertThat(receiver.getRows(), Matchers.contains(StreamedRow.row(new GenericRow(ImmutableList.of("hello"))), StreamedRow.row(new GenericRow(ImmutableList.of("world"))), StreamedRow.finalMessage("Limit Reached")));
    }

    @Test
    public void shouldHandleSlowResponsesFromServer() throws InterruptedException {
        // Given:
        givenResponsesDelayedBy(Duration.ofSeconds(3));
        final RestResponse<KsqlRestClient.QueryStream> queryResponse = ksqlRestClient.makeQueryRequest("Select *", null);
        final KsqlRestClientFunctionalTest.ReceiverThread receiver = new KsqlRestClientFunctionalTest.ReceiverThread(queryResponse);
        final MockStreamedQueryResource.TestStreamWriter writer = getResponseWriter();
        // When:
        writer.enq("hello");
        writer.enq("world");
        writer.enq("{\"row\":null,\"errorMessage\":null,\"finalMessage\":\"Limit Reached\"}");
        writer.finished();
        // Then:
        Assert.assertThat(receiver.getRows(), Matchers.contains(StreamedRow.row(new GenericRow(ImmutableList.of("hello"))), StreamedRow.row(new GenericRow(ImmutableList.of("world"))), StreamedRow.finalMessage("Limit Reached")));
    }

    @Test
    public void shouldReturnFalseFromHasNextIfClosedAsynchronously() throws Exception {
        // Given:
        final RestResponse<KsqlRestClient.QueryStream> queryResponse = ksqlRestClient.makeQueryRequest("Select *", null);
        final QueryStream stream = queryResponse.getResponse();
        final Thread closeThread = KsqlRestClientFunctionalTest.givenStreamWillCloseIn(Duration.ofMillis(500), stream);
        // When:
        final boolean result = stream.hasNext();
        // Then:
        Assert.assertThat(result, Matchers.is(false));
        closeThread.join(1000);
        Assert.assertThat("invalid test", closeThread.isAlive(), Matchers.is(false));
    }

    @Test
    public void testStatus() {
        // When:
        final RestResponse<CommandStatuses> response = ksqlRestClient.makeStatusRequest();
        // Then:
        Assert.assertThat(response, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(response.isSuccessful(), Matchers.is(true));
        Assert.assertThat(response.getResponse(), Matchers.is(new CommandStatuses(ImmutableMap.of(new io.confluent.ksql.rest.server.computation.CommandId(Type.TOPIC, "c1", Action.CREATE), SUCCESS, new io.confluent.ksql.rest.server.computation.CommandId(Type.TOPIC, "c2", Action.CREATE), ERROR))));
    }

    @Test
    public void shouldReturnStatusForSpecificCommand() {
        // When:
        final RestResponse<CommandStatus> response = ksqlRestClient.makeStatusRequest("TOPIC/c1/CREATE");
        // Then:
        Assert.assertThat(response, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(response.isSuccessful(), Matchers.is(true));
        Assert.assertThat(response.getResponse().getStatus(), Matchers.is(SUCCESS));
    }

    @Test(expected = KsqlRestClientException.class)
    public void shouldThrowOnInvalidServerAddress() {
        new KsqlRestClient("not-valid-address");
    }

    @Test
    public void shouldParseSingleServerAddress() throws Exception {
        final String singleServerAddress = "http://singleServer:8088";
        final URI singleServerURI = new URI(singleServerAddress);
        try (KsqlRestClient client = new KsqlRestClient(singleServerAddress)) {
            Assert.assertThat(client.getServerAddress(), Matchers.is(singleServerURI));
        }
    }

    @Test
    public void shouldParseMultipleServerAddresses() throws Exception {
        final String firstServerAddress = "http://firstServer:8088";
        final String multipleServerAddresses = firstServerAddress + ",http://secondServer:8088";
        final URI firstServerURI = new URI(firstServerAddress);
        try (KsqlRestClient client = new KsqlRestClient(multipleServerAddresses)) {
            Assert.assertThat(client.getServerAddress(), Matchers.is(firstServerURI));
        }
    }

    @Test
    public void shouldThrowIfAnyServerAddressIsInvalid() {
        expectedException.expect(KsqlRestClientException.class);
        expectedException.expectMessage("The supplied serverAddress is invalid: secondBuggyServer.8088");
        try (KsqlRestClient client = new KsqlRestClient("http://firstServer:8088,secondBuggyServer.8088")) {
            // Meh
        }
    }

    @Test
    public void shouldHandleNotFoundOnGetRequests() {
        // Given:
        givenServerWillReturn(NOT_FOUND);
        // When:
        final RestResponse<?> response = ksqlRestClient.getServerInfo();
        // Then:
        Assert.assertThat(response.getErrorMessage().getErrorCode(), Matchers.is(404));
        Assert.assertThat(response.getErrorMessage().getMessage(), Matchers.containsString(("Path not found. Path='/info'. " + "Check your ksql http url to make sure you are connecting to a ksql server.")));
    }

    @Test
    public void shouldHandleNotFoundOnPostRequests() {
        // Given:
        givenServerWillReturn(NOT_FOUND);
        // When:
        final RestResponse<?> response = ksqlRestClient.makeKsqlRequest("whateva", null);
        // Then:
        Assert.assertThat(response.getErrorMessage().getErrorCode(), Matchers.is(404));
        Assert.assertThat(response.getErrorMessage().getMessage(), Matchers.containsString(("Path not found. Path='ksql'. " + "Check your ksql http url to make sure you are connecting to a ksql server.")));
    }

    @Test
    public void shouldHandleUnauthorizedOnGetRequests() {
        // Given:
        givenServerWillReturn(UNAUTHORIZED);
        // When:
        final RestResponse<?> response = ksqlRestClient.getServerInfo();
        // Then:
        Assert.assertThat(response.getErrorMessage().getErrorCode(), Matchers.is(ERROR_CODE_UNAUTHORIZED));
        Assert.assertThat(response.getErrorMessage().getMessage(), Matchers.is("Could not authenticate successfully with the supplied credentials."));
    }

    @Test
    public void shouldHandleUnauthorizedOnPostRequests() {
        // Given:
        givenServerWillReturn(UNAUTHORIZED);
        // When:
        final RestResponse<?> response = ksqlRestClient.makeKsqlRequest("whateva", null);
        // Then:
        Assert.assertThat(response.getErrorMessage().getErrorCode(), Matchers.is(ERROR_CODE_UNAUTHORIZED));
        Assert.assertThat(response.getErrorMessage().getMessage(), Matchers.is("Could not authenticate successfully with the supplied credentials."));
    }

    @Test
    public void shouldHandleForbiddenOnGetRequests() {
        // Given:
        givenServerWillReturn(FORBIDDEN);
        // When:
        final RestResponse<?> response = ksqlRestClient.getServerInfo();
        // Then:
        Assert.assertThat(response.getErrorMessage().getErrorCode(), Matchers.is(ERROR_CODE_FORBIDDEN));
        Assert.assertThat(response.getErrorMessage().getMessage(), Matchers.is("You are forbidden from using this cluster."));
    }

    @Test
    public void shouldHandleForbiddenOnPostRequests() {
        // Given:
        givenServerWillReturn(FORBIDDEN);
        // When:
        final RestResponse<?> response = ksqlRestClient.makeKsqlRequest("whateva", null);
        // Then:
        Assert.assertThat(response.getErrorMessage().getErrorCode(), Matchers.is(ERROR_CODE_FORBIDDEN));
        Assert.assertThat(response.getErrorMessage().getMessage(), Matchers.is("You are forbidden from using this cluster."));
    }

    @Test
    public void shouldHandleErrorMessageOnGetRequests() {
        // Given:
        givenServerWillReturn(new KsqlErrorMessage(12300, "ouch", ImmutableList.of("s1", "s2")));
        // When:
        final RestResponse<?> response = ksqlRestClient.getServerInfo();
        // Then:
        Assert.assertThat(response.getErrorMessage().getErrorCode(), Matchers.is(12300));
        Assert.assertThat(response.getErrorMessage().getMessage(), Matchers.is("ouch"));
        Assert.assertThat(response.getErrorMessage().getStackTrace(), Matchers.is(ImmutableList.of("s1", "s2")));
    }

    @Test
    public void shouldHandleErrorMessageOnPostRequests() {
        // Given:
        givenServerWillReturn(new KsqlErrorMessage(12300, "ouch", ImmutableList.of("s1", "s2")));
        // When:
        final RestResponse<?> response = ksqlRestClient.makeKsqlRequest("whateva", null);
        // Then:
        Assert.assertThat(response.getErrorMessage().getErrorCode(), Matchers.is(12300));
        Assert.assertThat(response.getErrorMessage().getMessage(), Matchers.is("ouch"));
        Assert.assertThat(response.getErrorMessage().getStackTrace(), Matchers.is(ImmutableList.of("s1", "s2")));
    }

    @Test
    public void shouldHandleArbitraryErrorsOnGetRequests() {
        // Given:
        givenServerWillReturn(EXPECTATION_FAILED);
        // When:
        final RestResponse<?> response = ksqlRestClient.getServerInfo();
        // Then:
        Assert.assertThat(response.getErrorMessage().getErrorCode(), Matchers.is(Errors.toErrorCode(EXPECTATION_FAILED.getStatusCode())));
        Assert.assertThat(response.getErrorMessage().getMessage(), Matchers.is("The server returned an unexpected error: Expectation Failed"));
    }

    @Test
    public void shouldHandleArbitraryErrorsOnPostRequests() {
        // Given:
        givenServerWillReturn(EXPECTATION_FAILED);
        // When:
        final RestResponse<?> response = ksqlRestClient.makeKsqlRequest("whateva", null);
        // Then:
        Assert.assertThat(response.getErrorMessage().getErrorCode(), Matchers.is(Errors.toErrorCode(EXPECTATION_FAILED.getStatusCode())));
        Assert.assertThat(response.getErrorMessage().getMessage(), Matchers.is("The server returned an unexpected error: Expectation Failed"));
    }

    @Test
    public void shouldHandleSuccessOnGetRequests() {
        // Given:
        final ServerInfo expectedEntity = new ServerInfo("1", "cid", "sid");
        givenServerWillReturn(expectedEntity);
        // When:
        final RestResponse<ServerInfo> response = ksqlRestClient.getServerInfo();
        // Then:
        Assert.assertThat(response.get(), Matchers.is(expectedEntity));
    }

    @Test
    public void shouldHandleSuccessOnPostRequests() {
        // Given:
        final KsqlEntityList expectedEntity = new KsqlEntityList();
        givenServerWillReturn(expectedEntity);
        // When:
        final RestResponse<KsqlEntityList> response = ksqlRestClient.makeKsqlRequest("foo", null);
        // Then:
        Assert.assertThat(response.get(), Matchers.is(expectedEntity));
    }

    private static final class ReceiverThread {
        private final QueryStream queryStream;

        private final List<StreamedRow> rows = new CopyOnWriteArrayList<>();

        private final AtomicReference<Exception> exception = new AtomicReference<>();

        private final Thread thread;

        private ReceiverThread(final RestResponse<KsqlRestClient.QueryStream> queryResponse) {
            Assert.assertThat("not successful", queryResponse.isSuccessful(), Matchers.is(true));
            this.queryStream = queryResponse.getResponse();
            this.thread = new Thread(() -> {
                try {
                    while (queryStream.hasNext()) {
                        final StreamedRow row = queryStream.next();
                        rows.add(row);
                    } 
                } catch (final Exception e) {
                    exception.set(e);
                }
            }, "receiver-thread");
            thread.setDaemon(true);
            thread.start();
        }

        private List<StreamedRow> getRows() throws InterruptedException {
            thread.join(20000);
            Assert.assertThat("Receive thread still running", thread.isAlive(), Matchers.is(false));
            if ((exception.get()) != null) {
                throw new RuntimeException(exception.get());
            }
            return rows;
        }
    }
}

