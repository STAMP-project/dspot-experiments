package org.mockserver.server;


import com.google.common.net.MediaType;
import io.netty.handler.codec.http.HttpHeaderNames;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockserver.client.ClientException;
import org.mockserver.integration.server.AbstractExtendedSameJVMMockingIntegrationTest;
import org.mockserver.mock.action.ExpectationForwardCallback;
import org.mockserver.mock.action.ExpectationResponseCallback;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;


/**
 *
 *
 * @author jamesdbloom
 */
public abstract class AbstractExtendedDeployableWARMockingIntegrationTest extends AbstractExtendedSameJVMMockingIntegrationTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldReturnErrorResponseForExpectationWithConnectionOptions() {
        // given
        exception.expect(ClientException.class);
        exception.expectMessage(Matchers.containsString("ConnectionOptions is not supported by MockServer deployed as a WAR"));
        // when
        mockServerClient.when(request()).respond(response().withBody("some_long_body").withConnectionOptions(connectionOptions().withKeepAliveOverride(true).withContentLengthHeaderOverride(10)));
    }

    @Test
    public void shouldReturnErrorResponseForExpectationWithHttpError() {
        // given
        exception.expect(ClientException.class);
        exception.expectMessage(Matchers.containsString("HttpError is not supported by MockServer deployed as a WAR"));
        // when
        error(error().withDropConnection(true));
    }

    @Test
    public void shouldReturnErrorResponseForRespondByObjectCallback() {
        // given
        exception.expect(ClientException.class);
        exception.expectMessage(Matchers.containsString("ExpectationResponseCallback and ExpectationForwardCallback is not supported by MockServer deployed as a WAR"));
        // when
        mockServerClient.when(request().withPath(calculatePath("object_callback"))).respond(new ExpectationResponseCallback() {
            @Override
            public HttpResponse handle(HttpRequest httpRequest) {
                return response().withStatusCode(ACCEPTED_202.code()).withReasonPhrase(ACCEPTED_202.reasonPhrase()).withHeaders(header("x-object-callback", "test_object_callback_header")).withBody("an_object_callback_response");
            }
        });
    }

    @Test
    public void shouldReturnErrorResponseForForwardByObjectCallback() {
        // given
        exception.expect(ClientException.class);
        exception.expectMessage(Matchers.containsString("ExpectationResponseCallback and ExpectationForwardCallback is not supported by MockServer deployed as a WAR"));
        // when
        mockServerClient.when(request().withPath(calculatePath("echo"))).forward(new ExpectationForwardCallback() {
            @Override
            public HttpRequest handle(HttpRequest httpRequest) {
                return request().withBody("some_overridden_body").withSecure(httpRequest.isSecure());
            }
        });
    }

    @Test
    public void shouldCallbackForResponseToSpecifiedClassInTestClasspath() {
        // given
        TestClasspathTestExpectationResponseCallback.httpRequests.clear();
        TestClasspathTestExpectationResponseCallback.httpResponse = response().withStatusCode(ACCEPTED_202.code()).withReasonPhrase(ACCEPTED_202.reasonPhrase()).withHeaders(header("x-callback", "test_callback_header")).withBody("a_callback_response");
        // when
        mockServerClient.when(request().withPath(calculatePath("callback"))).respond(callback().withCallbackClass("org.mockserver.server.TestClasspathTestExpectationResponseCallback"));
        // then
        // - in http
        Assert.assertEquals(response().withStatusCode(ACCEPTED_202.code()).withReasonPhrase(ACCEPTED_202.reasonPhrase()).withHeaders(header("x-callback", "test_callback_header")).withBody("a_callback_response"), makeRequest(request().withPath(calculatePath("callback")).withMethod("POST").withHeaders(header("X-Test", "test_headers_and_body")).withBody("an_example_body_http"), headersToIgnore));
        Assert.assertEquals(TestClasspathTestExpectationResponseCallback.httpRequests.get(0).getBody().getValue(), "an_example_body_http");
        Assert.assertEquals(TestClasspathTestExpectationResponseCallback.httpRequests.get(0).getPath().getValue(), calculatePath("callback"));
        // - in https
        Assert.assertEquals(response().withStatusCode(ACCEPTED_202.code()).withReasonPhrase(ACCEPTED_202.reasonPhrase()).withHeaders(header("x-callback", "test_callback_header")).withBody("a_callback_response"), makeRequest(request().withSecure(true).withPath(calculatePath("callback")).withMethod("POST").withHeaders(header("X-Test", "test_headers_and_body")).withBody("an_example_body_https"), headersToIgnore));
        Assert.assertEquals(TestClasspathTestExpectationResponseCallback.httpRequests.get(1).getBody().getValue(), "an_example_body_https");
        Assert.assertEquals(TestClasspathTestExpectationResponseCallback.httpRequests.get(1).getPath().getValue(), calculatePath("callback"));
    }

    @Test
    public void shouldCallbackForForwardCallbackToSpecifiedClassInTestClasspath() {
        // given
        TestClasspathTestExpectationForwardCallback.httpRequests.clear();
        TestClasspathTestExpectationForwardCallback.httpRequestToReturn = request().withHeaders(header("x-callback", "test_callback_header"), header("Host", ("localhost:" + (insecureEchoServer.getPort())))).withBody("a_callback_forward");
        // when
        mockServerClient.when(request().withPath(calculatePath("callback"))).forward(callback().withCallbackClass("org.mockserver.server.TestClasspathTestExpectationForwardCallback"));
        // then
        // - in http
        Assert.assertEquals(response().withStatusCode(OK_200.code()).withReasonPhrase(OK_200.reasonPhrase()).withHeaders(header("x-callback", "test_callback_header")).withBody("a_callback_forward"), makeRequest(request().withPath(calculatePath("callback")).withMethod("POST").withHeaders(header("X-Test", "test_headers_and_body")).withBody("an_example_body_http"), headersToIgnore));
        Assert.assertEquals(TestClasspathTestExpectationForwardCallback.httpRequests.get(0).getBody().getValue(), "an_example_body_http");
        Assert.assertEquals(TestClasspathTestExpectationForwardCallback.httpRequests.get(0).getPath().getValue(), calculatePath("callback"));
        // - in https
        Assert.assertEquals(response().withStatusCode(OK_200.code()).withReasonPhrase(OK_200.reasonPhrase()).withHeaders(header("x-callback", "test_callback_header")).withBody("a_callback_forward"), makeRequest(request().withSecure(true).withPath(calculatePath("callback")).withMethod("POST").withHeaders(header("X-Test", "test_headers_and_body")).withBody("an_example_body_https"), headersToIgnore));
        Assert.assertEquals(TestClasspathTestExpectationForwardCallback.httpRequests.get(1).getBody().getValue(), "an_example_body_https");
        Assert.assertEquals(TestClasspathTestExpectationForwardCallback.httpRequests.get(1).getPath().getValue(), calculatePath("callback"));
    }

    @Test
    public void shouldReturnStatus() {
        // then
        // - in http
        Assert.assertEquals(response().withStatusCode(OK_200.code()).withReasonPhrase(OK_200.reasonPhrase()).withHeader(HttpHeaderNames.CONTENT_TYPE.toString(), "application/json; charset=utf-8").withBody((((((("{" + (NEW_LINE)) + "  \"ports\" : [ ") + (getServerPort())) + " ]") + (NEW_LINE)) + "}"), MediaType.JSON_UTF_8), makeRequest(request().withPath(calculatePath("mockserver/status")).withMethod("PUT"), headersToIgnore));
        // - in https
        Assert.assertEquals(response().withStatusCode(OK_200.code()).withReasonPhrase(OK_200.reasonPhrase()).withHeader(HttpHeaderNames.CONTENT_TYPE.toString(), "application/json; charset=utf-8").withBody((((((("{" + (NEW_LINE)) + "  \"ports\" : [ ") + (getServerSecurePort())) + " ]") + (NEW_LINE)) + "}"), MediaType.JSON_UTF_8), makeRequest(request().withSecure(true).withPath(calculatePath("mockserver/status")).withMethod("PUT"), headersToIgnore));
    }
}

