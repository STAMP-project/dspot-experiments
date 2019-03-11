package org.mockserver.integration.mocking;


import Metrics.Name.WEBSOCKET_CALLBACK_CLIENT_COUNT;
import Metrics.Name.WEBSOCKET_CALLBACK_FORWARD_HANDLER_COUNT;
import Metrics.Name.WEBSOCKET_CALLBACK_RESPONSE_HANDLER_COUNT;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.integration.server.AbstractMockingIntegrationTestBase;
import org.mockserver.metrics.Metrics;
import org.mockserver.mock.action.ExpectationForwardCallback;
import org.mockserver.mock.action.ExpectationResponseCallback;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;


/**
 *
 *
 * @author jamesdbloom
 */
public class WebsocketCallbackRegistryIntegrationTest extends AbstractMockingIntegrationTestBase {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    // same JVM due to dynamic calls to static Metrics class
    @Test
    public void shouldRemoveWebsocketCallbackClientFromRegistryForClientReset() {
        // given
        Metrics.clear();
        final MockServerClient mockServerClient = new MockServerClient("localhost", getServerPort());
        mockServerClient.when(request()).respond(new ExpectationResponseCallback() {
            @Override
            public HttpResponse handle(HttpRequest httpRequest) {
                return response();
            }
        });
        // then
        Assert.assertThat(Metrics.get(WEBSOCKET_CALLBACK_CLIENT_COUNT), CoreMatchers.is(1));
        // when
        mockServerClient.reset();
        // then
        Assert.assertThat(Metrics.get(WEBSOCKET_CALLBACK_CLIENT_COUNT), CoreMatchers.is(0));
    }

    // same JVM due to dynamic calls to static Metrics class
    @Test
    public void shouldRemoveWebsocketCallbackClientFromRegistryForClientStop() {
        // given
        Metrics.clear();
        final MockServerClient mockServerClient = new ClientAndServer();
        mockServerClient.when(request()).respond(new ExpectationResponseCallback() {
            @Override
            public HttpResponse handle(HttpRequest httpRequest) {
                return response();
            }
        });
        try {
            // then
            Assert.assertThat(Metrics.get(WEBSOCKET_CALLBACK_CLIENT_COUNT), CoreMatchers.is(1));
            // when
            mockServerClient.stop();
            // then
            Assert.assertThat(Metrics.get(WEBSOCKET_CALLBACK_CLIENT_COUNT), CoreMatchers.is(0));
        } finally {
            mockServerClient.stop();
        }
    }

    // same JVM due to dynamic calls to static Metrics class
    @Test
    public void shouldRemoveWebsocketResponseHandlerFromRegistry() {
        // given
        Metrics.clear();
        mockServerClient.when(request().withPath(calculatePath("websocket_response_handler"))).respond(new ExpectationResponseCallback() {
            @Override
            public HttpResponse handle(HttpRequest httpRequest) {
                // then
                return response().withBody(((("websocket_response_handler_count_" + (Metrics.get(WEBSOCKET_CALLBACK_RESPONSE_HANDLER_COUNT))) + "_") + (Metrics.get(WEBSOCKET_CALLBACK_FORWARD_HANDLER_COUNT))));
            }
        });
        // then
        Assert.assertThat(Metrics.get(WEBSOCKET_CALLBACK_CLIENT_COUNT), CoreMatchers.is(1));
        // when
        Assert.assertEquals(response().withStatusCode(OK_200.code()).withReasonPhrase(OK_200.reasonPhrase()).withBody("websocket_response_handler_count_1_0"), makeRequest(request().withPath(calculatePath("websocket_response_handler")), headersToIgnore));
        // then
        Assert.assertThat(Metrics.get(WEBSOCKET_CALLBACK_RESPONSE_HANDLER_COUNT), CoreMatchers.is(0));
    }

    // same JVM due to dynamic calls to static Metrics class
    @Test
    public void shouldRemoveWebsocketForwardHandlerFromRegistry() {
        // given
        Metrics.clear();
        // when
        mockServerClient.when(request().withPath(calculatePath("websocket_forward_handler"))).forward(new ExpectationForwardCallback() {
            @Override
            public HttpRequest handle(HttpRequest httpRequest) {
                return request().withHeader("Host", ("localhost:" + (insecureEchoServer.getPort()))).withBody(((("websocket_forward_handler_count_" + (Metrics.get(WEBSOCKET_CALLBACK_FORWARD_HANDLER_COUNT))) + "_") + (Metrics.get(WEBSOCKET_CALLBACK_RESPONSE_HANDLER_COUNT))));
            }
        });
        // then
        Assert.assertThat(Metrics.get(WEBSOCKET_CALLBACK_CLIENT_COUNT), CoreMatchers.is(1));
        // when
        Assert.assertEquals(response().withStatusCode(OK_200.code()).withReasonPhrase(OK_200.reasonPhrase()).withBody("websocket_forward_handler_count_1_0"), makeRequest(request().withPath(calculatePath("websocket_forward_handler")), headersToIgnore));
        // then
        Assert.assertThat(Metrics.get(WEBSOCKET_CALLBACK_FORWARD_HANDLER_COUNT), CoreMatchers.is(0));
    }

    @Test
    public void shouldNotAllowUseOfWebsocketClientInsideCallback() {
        // when
        mockServerClient.when(request().withPath(calculatePath("prevent_reentrant_websocketclient_registration"))).respond(new ExpectationResponseCallback() {
            @Override
            public HttpResponse handle(HttpRequest httpRequest) {
                mockServerClient.when(request().withPath(calculatePath("reentrant_websocketclient_registration"))).respond(new ExpectationResponseCallback() {
                    @Override
                    public HttpResponse handle(HttpRequest httpRequest) {
                        // then
                        return response().withBody("reentrant_websocketclient_registration");
                    }
                });
                return response().withBody("prevent_reentrant_websocketclient_registration");
            }
        });
        // when
        Assert.assertEquals(response().withStatusCode(NOT_FOUND_404.code()).withReasonPhrase(NOT_FOUND_404.reasonPhrase()).withBody("It is not possible to re-use the same MockServerClient instance to register a new object callback while responding to an object callback, please use a separate instance of the MockServerClient inside a callback"), makeRequest(request().withPath(calculatePath("prevent_reentrant_websocketclient_registration")), headersToIgnore));
    }

    @Test
    public void shouldAllowUseOfSeparateWebsocketClientInsideCallback() {
        // when
        mockServerClient.when(request().withPath(calculatePath("prevent_reentrant_websocketclient_registration"))).respond(new ExpectationResponseCallback() {
            @Override
            public HttpResponse handle(HttpRequest httpRequest) {
                new MockServerClient("localhost", getServerPort()).when(request().withPath(calculatePath("reentrant_websocketclient_registration"))).respond(new ExpectationResponseCallback() {
                    @Override
                    public HttpResponse handle(HttpRequest httpRequest) {
                        // then
                        return response().withBody("reentrant_websocketclient_registration");
                    }
                });
                return response().withBody("prevent_reentrant_websocketclient_registration");
            }
        });
        // when
        Assert.assertEquals(response().withStatusCode(OK_200.code()).withReasonPhrase(OK_200.reasonPhrase()).withBody("prevent_reentrant_websocketclient_registration"), makeRequest(request().withPath(calculatePath("prevent_reentrant_websocketclient_registration")), headersToIgnore));
    }
}

