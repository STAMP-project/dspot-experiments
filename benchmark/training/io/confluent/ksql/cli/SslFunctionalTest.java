/**
 * Copyright 2019 Confluent Inc.
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
package io.confluent.ksql.cli;


import Code.OK;
import RestConfig.LISTENERS_CONFIG;
import com.google.common.net.UrlEscapers;
import io.confluent.common.utils.IntegrationTest;
import io.confluent.ksql.rest.client.exception.KsqlRestClientException;
import io.confluent.ksql.test.util.EmbeddedSingleNodeKafkaCluster;
import io.confluent.ksql.test.util.TestKsqlRestApp;
import io.confluent.ksql.test.util.secure.ServerKeyStore;
import io.confluent.ksql.util.OrderDataProvider;
import java.io.EOFException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.ssl.SSLHandshakeException;
import javax.ws.rs.ProcessingException;
import org.eclipse.jetty.http.HttpStatus.Code;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.internal.matchers.ThrowableCauseMatcher;
import org.junit.internal.matchers.ThrowableMessageMatcher;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;


@Category({ IntegrationTest.class })
public class SslFunctionalTest {
    private static final String TOPIC_NAME = new OrderDataProvider().topicName();

    private static final String JSON_KSQL_REQUEST = UrlEscapers.urlFormParameterEscaper().escape((((("{" + " \"ksql\": \"PRINT ") + (SslFunctionalTest.TOPIC_NAME)) + " FROM BEGINNING;\"") + "}"));

    private static final EmbeddedSingleNodeKafkaCluster CLUSTER = EmbeddedSingleNodeKafkaCluster.newBuilder().build();

    private static final TestKsqlRestApp REST_APP = TestKsqlRestApp.builder(SslFunctionalTest.CLUSTER::bootstrapServers).withProperties(ServerKeyStore.keyStoreProps()).withProperty(LISTENERS_CONFIG, "https://localhost:0").build();

    @ClassRule
    public static final RuleChain CHAIN = RuleChain.outerRule(SslFunctionalTest.CLUSTER).around(SslFunctionalTest.REST_APP);

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private Map<String, String> clientProps;

    private SslContextFactory sslContextFactory;

    @Test
    public void shouldNotBeAbleToUseCliIfClientDoesNotTrustServerCert() {
        // Then:
        expectedException.expect(KsqlRestClientException.class);
        expectedException.expectCause(Matchers.is(Matchers.instanceOf(ProcessingException.class)));
        expectedException.expectCause(ThrowableCauseMatcher.hasCause(Matchers.is(Matchers.instanceOf(SSLHandshakeException.class))));
        // When:
        canMakeCliRequest();
    }

    @Test
    public void shouldBeAbleToUseCliOverHttps() {
        // Given:
        givenTrustStoreConfigured();
        // When:
        final Code result = canMakeCliRequest();
        // Then:
        MatcherAssert.assertThat(result, Matchers.is(OK));
    }

    @Test
    public void shouldNotBeAbleToUseWssIfClientDoesNotTrustServerCert() throws Exception {
        // Then:
        expectedException.expect(// Occasionally, get EOF exception.
        Matchers.either(Matchers.both(ThrowableCauseMatcher.hasCause(ThrowableCauseMatcher.hasCause(ThrowableMessageMatcher.hasMessage(Matchers.containsString("unable to find valid certification path to requested target"))))).and(Matchers.instanceOf(SSLHandshakeException.class))).or(Matchers.instanceOf(EOFException.class)));
        // When:
        makeWsRequest();
    }

    @Test
    public void shouldBeAbleToUseWss() throws Exception {
        // Given:
        givenTrustStoreConfigured();
        // When:
        makeWsRequest();
        // Then: did not throw.
    }

    @WebSocket
    public static class WebSocketListener {
        private Session session;

        final CountDownLatch latch = new CountDownLatch(1);

        final AtomicReference<Throwable> error = new AtomicReference<>();

        // Invoked via reflection
        @SuppressWarnings("unused")
        @OnWebSocketConnect
        public void onConnect(final Session session) {
            this.session = session;
        }

        // Invoked via reflection
        @SuppressWarnings("unused")
        @OnWebSocketError
        public void onError(final Throwable t) {
            error.compareAndSet(null, t);
            closeSilently();
            latch.countDown();
        }

        // Invoked via reflection
        @SuppressWarnings("unused")
        @OnWebSocketMessage
        public void onMessage(String msg) {
            closeSilently();
            latch.countDown();
        }

        private void closeSilently() {
            try {
                if ((session) != null) {
                    session.close();
                }
            } catch (final Exception e) {
                // meh
            }
        }
    }
}

