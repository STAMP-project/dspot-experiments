package org.mockserver.client;


import ClearType.LOG;
import Format.JAVA;
import Format.JSON;
import RetrieveType.ACTIVE_EXPECTATIONS;
import RetrieveType.RECORDED_EXPECTATIONS;
import RetrieveType.REQUESTS;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockserver.echo.http.EchoServer;
import org.mockserver.filters.MockServerEventLog;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.matchers.TimeToLive;
import org.mockserver.mock.Expectation;
import org.mockserver.serialization.java.ExpectationToJavaSerializer;
import org.mockserver.serialization.java.HttpRequestToJavaSerializer;
import org.mockserver.verify.VerificationTimes;


/**
 *
 *
 * @author jamesdbloom
 */
public class MockServerClientIntegrationTest {
    private static MockServerClient mockServerClient;

    private static EchoServer echoServer;

    private static MockServerEventLog logFilter;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldSetupExpectationWithResponse() {
        // given
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201));
        // when
        MockServerClientIntegrationTest.mockServerClient.when(request().withPath("/some_path").withBody(new StringBody("some_request_body"))).respond(response().withBody("some_response_body").withHeaders(new Header("responseName", "responseValue")));
        // then
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/expectation").withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("connection", "keep-alive"), new Header("content-type", "text/plain; charset=utf-8")).withSecure(false).withKeepAlive(true).withBody(new StringBody(((((((((((((((((((((((((((((((((((("" + "{") + (NEW_LINE)) + "  \"httpRequest\" : {") + (NEW_LINE)) + "    \"path\" : \"/some_path\",") + (NEW_LINE)) + "    \"body\" : \"some_request_body\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"httpResponse\" : {") + (NEW_LINE)) + "    \"headers\" : {") + (NEW_LINE)) + "      \"responseName\" : [ \"responseValue\" ]") + (NEW_LINE)) + "    },") + (NEW_LINE)) + "    \"body\" : \"some_response_body\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"times\" : {") + (NEW_LINE)) + "    \"unlimited\" : true") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"timeToLive\" : {") + (NEW_LINE)) + "    \"unlimited\" : true") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}")))));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldSetupExpectationWithResponseTemplate() {
        // given
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201));
        // when
        MockServerClientIntegrationTest.mockServerClient.when(request().withPath("/some_path").withBody(new StringBody("some_request_body"))).respond(template(HttpTemplate.TemplateType.VELOCITY).withTemplate("some_response_template"));
        // then
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/expectation").withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("connection", "keep-alive"), new Header("content-type", "text/plain; charset=utf-8")).withSecure(false).withKeepAlive(true).withBody(new StringBody(((((((((((((((((((((((((((((((("" + "{") + (NEW_LINE)) + "  \"httpRequest\" : {") + (NEW_LINE)) + "    \"path\" : \"/some_path\",") + (NEW_LINE)) + "    \"body\" : \"some_request_body\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"httpResponseTemplate\" : {") + (NEW_LINE)) + "    \"template\" : \"some_response_template\",") + (NEW_LINE)) + "    \"templateType\" : \"VELOCITY\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"times\" : {") + (NEW_LINE)) + "    \"unlimited\" : true") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"timeToLive\" : {") + (NEW_LINE)) + "    \"unlimited\" : true") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}")))));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldSetupExpectationWithResponseClassCallback() {
        // given
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201));
        // when
        MockServerClientIntegrationTest.mockServerClient.when(request().withPath("/some_path").withBody(new StringBody("some_request_body"))).respond(callback().withCallbackClass("some_class"));
        // then
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/expectation").withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("connection", "keep-alive"), new Header("content-type", "text/plain; charset=utf-8")).withSecure(false).withKeepAlive(true).withBody(new StringBody(((((((((((((((((((((((((((((("" + "{") + (NEW_LINE)) + "  \"httpRequest\" : {") + (NEW_LINE)) + "    \"path\" : \"/some_path\",") + (NEW_LINE)) + "    \"body\" : \"some_request_body\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"httpResponseClassCallback\" : {") + (NEW_LINE)) + "    \"callbackClass\" : \"some_class\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"times\" : {") + (NEW_LINE)) + "    \"unlimited\" : true") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"timeToLive\" : {") + (NEW_LINE)) + "    \"unlimited\" : true") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}")))));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldSetupExpectationWithResponseObjectCallback() {
        // given
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201));
        // when
        MockServerClientIntegrationTest.mockServerClient.when(request().withPath("/some_path").withBody(new StringBody("some_request_body"))).respond(callback().withCallbackClass("some_class"));
        // then
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/expectation").withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("connection", "keep-alive"), new Header("content-type", "text/plain; charset=utf-8")).withSecure(false).withKeepAlive(true).withBody(new StringBody(((((((((((((((((((((((((((((("" + "{") + (NEW_LINE)) + "  \"httpRequest\" : {") + (NEW_LINE)) + "    \"path\" : \"/some_path\",") + (NEW_LINE)) + "    \"body\" : \"some_request_body\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"httpResponseClassCallback\" : {") + (NEW_LINE)) + "    \"callbackClass\" : \"some_class\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"times\" : {") + (NEW_LINE)) + "    \"unlimited\" : true") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"timeToLive\" : {") + (NEW_LINE)) + "    \"unlimited\" : true") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}")))));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldSetupExpectationWithForward() {
        // given
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201));
        // when
        forward(forward().withHost("some_host").withPort(9090).withScheme(Scheme.HTTPS));
        // then
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/expectation").withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("connection", "keep-alive"), new Header("content-type", "text/plain; charset=utf-8")).withSecure(false).withKeepAlive(true).withBody(new StringBody(((((((((((((((((((((((((((((((((("" + "{") + (NEW_LINE)) + "  \"httpRequest\" : {") + (NEW_LINE)) + "    \"path\" : \"/some_path\",") + (NEW_LINE)) + "    \"body\" : \"some_request_body\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"httpForward\" : {") + (NEW_LINE)) + "    \"host\" : \"some_host\",") + (NEW_LINE)) + "    \"port\" : 9090,") + (NEW_LINE)) + "    \"scheme\" : \"HTTPS\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"times\" : {") + (NEW_LINE)) + "    \"unlimited\" : true") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"timeToLive\" : {") + (NEW_LINE)) + "    \"unlimited\" : true") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}")))));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldSetupExpectationWithForwardTemplate() {
        // given
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201));
        // when
        forward(template(HttpTemplate.TemplateType.VELOCITY).withTemplate("some_response_template"));
        // then
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/expectation").withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("connection", "keep-alive"), new Header("content-type", "text/plain; charset=utf-8")).withSecure(false).withKeepAlive(true).withBody(new StringBody(((((((((((((((((((((((((((((((("" + "{") + (NEW_LINE)) + "  \"httpRequest\" : {") + (NEW_LINE)) + "    \"path\" : \"/some_path\",") + (NEW_LINE)) + "    \"body\" : \"some_request_body\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"httpForwardTemplate\" : {") + (NEW_LINE)) + "    \"template\" : \"some_response_template\",") + (NEW_LINE)) + "    \"templateType\" : \"VELOCITY\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"times\" : {") + (NEW_LINE)) + "    \"unlimited\" : true") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"timeToLive\" : {") + (NEW_LINE)) + "    \"unlimited\" : true") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}")))));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldSetupExpectationWithForwardClassCallback() {
        // given
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201));
        // when
        forward(callback().withCallbackClass("some_class"));
        // then
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/expectation").withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("connection", "keep-alive"), new Header("content-type", "text/plain; charset=utf-8")).withSecure(false).withKeepAlive(true).withBody(new StringBody(((((((((((((((((((((((((((((("" + "{") + (NEW_LINE)) + "  \"httpRequest\" : {") + (NEW_LINE)) + "    \"path\" : \"/some_path\",") + (NEW_LINE)) + "    \"body\" : \"some_request_body\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"httpForwardClassCallback\" : {") + (NEW_LINE)) + "    \"callbackClass\" : \"some_class\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"times\" : {") + (NEW_LINE)) + "    \"unlimited\" : true") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"timeToLive\" : {") + (NEW_LINE)) + "    \"unlimited\" : true") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}")))));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldSetupExpectationWithForwardObjectCallback() {
        // given
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201));
        // when
        forward(callback().withCallbackClass("some_class"));
        // then
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/expectation").withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("connection", "keep-alive"), new Header("content-type", "text/plain; charset=utf-8")).withSecure(false).withKeepAlive(true).withBody(new StringBody(((((((((((((((((((((((((((((("" + "{") + (NEW_LINE)) + "  \"httpRequest\" : {") + (NEW_LINE)) + "    \"path\" : \"/some_path\",") + (NEW_LINE)) + "    \"body\" : \"some_request_body\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"httpForwardClassCallback\" : {") + (NEW_LINE)) + "    \"callbackClass\" : \"some_class\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"times\" : {") + (NEW_LINE)) + "    \"unlimited\" : true") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"timeToLive\" : {") + (NEW_LINE)) + "    \"unlimited\" : true") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}")))));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldSetupExpectationWithOverrideForwardedRequest() {
        // given
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201));
        // when
        forward(forwardOverriddenRequest(request().withHeader("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))).withBody("some_override_body")));
        // then
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/expectation").withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("connection", "keep-alive"), new Header("content-type", "text/plain; charset=utf-8")).withSecure(false).withKeepAlive(true).withBody(new StringBody(((((((((((((((((((((((((((((((((((((((((("" + "{") + (NEW_LINE)) + "  \"httpRequest\" : {") + (NEW_LINE)) + "    \"path\" : \"/some_path\",") + (NEW_LINE)) + "    \"body\" : \"some_request_body\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"httpOverrideForwardedRequest\" : {") + (NEW_LINE)) + "    \"httpRequest\" : {") + (NEW_LINE)) + "      \"headers\" : {") + (NEW_LINE)) + "        \"host\" : [ \"localhost:") + (MockServerClientIntegrationTest.echoServer.getPort())) + "\" ]") + (NEW_LINE)) + "      },") + (NEW_LINE)) + "      \"body\" : \"some_override_body\"") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"times\" : {") + (NEW_LINE)) + "    \"unlimited\" : true") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"timeToLive\" : {") + (NEW_LINE)) + "    \"unlimited\" : true") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}")))));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldSetupExpectationWithError() {
        // given
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201));
        // when
        error(error().withDropConnection(true).withResponseBytes("silly_bytes".getBytes(StandardCharsets.UTF_8)));
        // then
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/expectation").withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("connection", "keep-alive"), new Header("content-type", "text/plain; charset=utf-8")).withSecure(false).withKeepAlive(true).withBody(new StringBody(((((((((((((((((((((((((((((((("" + "{") + (NEW_LINE)) + "  \"httpRequest\" : {") + (NEW_LINE)) + "    \"path\" : \"/some_path\",") + (NEW_LINE)) + "    \"body\" : \"some_request_body\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"httpError\" : {") + (NEW_LINE)) + "    \"dropConnection\" : true,") + (NEW_LINE)) + "    \"responseBytes\" : \"c2lsbHlfYnl0ZXM=\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"times\" : {") + (NEW_LINE)) + "    \"unlimited\" : true") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"timeToLive\" : {") + (NEW_LINE)) + "    \"unlimited\" : true") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}")))));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldSendExpectationRequestWithExactTimes() {
        // given
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201));
        // when
        MockServerClientIntegrationTest.mockServerClient.when(request().withPath("/some_path").withBody(new StringBody("some_request_body")), exactly(3)).respond(response().withBody("some_response_body").withHeaders(new Header("responseName", "responseValue")));
        // then
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/expectation").withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("connection", "keep-alive"), new Header("content-type", "text/plain; charset=utf-8")).withSecure(false).withKeepAlive(true).withBody(new StringBody(((((((((((((((((((((((((((((((((((("" + "{") + (NEW_LINE)) + "  \"httpRequest\" : {") + (NEW_LINE)) + "    \"path\" : \"/some_path\",") + (NEW_LINE)) + "    \"body\" : \"some_request_body\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"httpResponse\" : {") + (NEW_LINE)) + "    \"headers\" : {") + (NEW_LINE)) + "      \"responseName\" : [ \"responseValue\" ]") + (NEW_LINE)) + "    },") + (NEW_LINE)) + "    \"body\" : \"some_response_body\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"times\" : {") + (NEW_LINE)) + "    \"remainingTimes\" : 3") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"timeToLive\" : {") + (NEW_LINE)) + "    \"unlimited\" : true") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}")))));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldSendStopRequest() {
        // given
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201));
        // when
        MockServerClientIntegrationTest.mockServerClient.stop();
        // then
        String result = MockServerClientIntegrationTest.logFilter.verify(verificationSequence().withRequests(request().withMethod("PUT").withPath("/mockserver/stop").withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("content-length", "0"), new Header("connection", "keep-alive")).withSecure(false).withKeepAlive(true), request().withMethod("PUT").withPath("/mockserver/status").withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("content-length", "0"), new Header("connection", "keep-alive")).withSecure(false).withKeepAlive(true)));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldQueryRunningStatus() {
        // given
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(200));
        // when
        boolean isRunning = MockServerClientIntegrationTest.mockServerClient.isRunning();
        // then
        MatcherAssert.assertThat(isRunning, Is.is(true));
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/status").withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("content-length", "0"), new Header("connection", "keep-alive")).withSecure(false).withKeepAlive(true)));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldQueryRunningStatusWhenNotRunning() {
        // given
        int numberOfRetries = 11;
        HttpResponse[] httpResponses = new HttpResponse[numberOfRetries];
        Arrays.fill(httpResponses, response().withStatusCode(404));
        MockServerClientIntegrationTest.echoServer.withNextResponse(httpResponses);
        // when
        boolean isRunning = MockServerClientIntegrationTest.mockServerClient.isRunning();
        // then
        MatcherAssert.assertThat(isRunning, Is.is(false));
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(numberOfRetries));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/status").withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("content-length", "0"), new Header("connection", "keep-alive")).withSecure(false).withKeepAlive(true)));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldSendResetRequest() {
        // given
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201));
        // when
        MockServerClientIntegrationTest.mockServerClient.reset();
        // then
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/reset").withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("content-length", "0"), new Header("connection", "keep-alive")).withSecure(false).withKeepAlive(true)));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldSendClearRequest() {
        // given
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201));
        // when
        MockServerClientIntegrationTest.mockServerClient.clear(request().withPath("/some_path").withBody(new StringBody("some_request_body")));
        // then
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/clear").withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("connection", "keep-alive"), new Header("content-type", "text/plain; charset=utf-8")).withSecure(false).withKeepAlive(true).withBody(new StringBody(((((((("" + "{") + (NEW_LINE)) + "  \"path\" : \"/some_path\",") + (NEW_LINE)) + "  \"body\" : \"some_request_body\"") + (NEW_LINE)) + "}")))));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldSendClearRequestWithType() {
        // given
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201));
        // when
        MockServerClientIntegrationTest.mockServerClient.clear(request().withPath("/some_path").withBody(new StringBody("some_request_body")), LOG);
        // then
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/clear").withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("connection", "keep-alive"), new Header("content-type", "text/plain; charset=utf-8")).withSecure(false).withKeepAlive(true).withBody(new StringBody(((((((("" + "{") + (NEW_LINE)) + "  \"path\" : \"/some_path\",") + (NEW_LINE)) + "  \"body\" : \"some_request_body\"") + (NEW_LINE)) + "}")))));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldSendClearRequestForNullRequest() {
        // given
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201));
        // when
        MockServerClientIntegrationTest.mockServerClient.clear(null);
        // then
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/clear").withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("content-length", "0"), new Header("connection", "keep-alive"), new Header("content-type", "text/plain; charset=utf-8")).withSecure(false).withKeepAlive(true)));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldRetrieveRequests() {
        // given
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201).withBody(new StringBody(new org.mockserver.serialization.HttpRequestSerializer(new MockServerLogger()).serialize(Arrays.asList(request("/some_request_one"), request("/some_request_two"))))));
        // when
        HttpRequest[] actualResponse = MockServerClientIntegrationTest.mockServerClient.retrieveRecordedRequests(request().withPath("/some_path").withBody(new StringBody("some_request_body")));
        // then
        MatcherAssert.assertThat(Arrays.asList(actualResponse), IsCollectionContaining.hasItems(request("/some_request_one"), request("/some_request_two")));
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/retrieve").withQueryStringParameter("type", REQUESTS.name()).withQueryStringParameter("format", JSON.name()).withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("connection", "keep-alive"), new Header("content-type", "text/plain; charset=utf-8")).withSecure(false).withKeepAlive(true).withBody(new StringBody((((((("{" + (NEW_LINE)) + "  \"path\" : \"/some_path\",") + (NEW_LINE)) + "  \"body\" : \"some_request_body\"") + (NEW_LINE)) + "}")))));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldRetrieveRequestsWithNullRequest() {
        // given
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201).withBody(new StringBody(new org.mockserver.serialization.HttpRequestSerializer(new MockServerLogger()).serialize(Arrays.asList(request("/some_request_one"), request("/some_request_two"))))));
        // when
        HttpRequest[] actualResponse = MockServerClientIntegrationTest.mockServerClient.retrieveRecordedRequests(null);
        // then
        MatcherAssert.assertThat(Arrays.asList(actualResponse), IsCollectionContaining.hasItems(request("/some_request_one"), request("/some_request_two")));
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/retrieve").withQueryStringParameter("type", REQUESTS.name()).withQueryStringParameter("format", JSON.name()).withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("content-length", "0"), new Header("connection", "keep-alive"), new Header("content-type", "text/plain; charset=utf-8")).withSecure(false).withKeepAlive(true)));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldRetrieveRequestsAsJson() {
        // given
        String serializedRequests = new org.mockserver.serialization.HttpRequestSerializer(new MockServerLogger()).serialize(Arrays.asList(request("/some_request_one"), request("/some_request_two")));
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201).withBody(new StringBody(serializedRequests)));
        // when
        String recordedResponse = MockServerClientIntegrationTest.mockServerClient.retrieveRecordedRequests(request().withPath("/some_path").withBody(new StringBody("some_request_body")), JSON);
        // then
        MatcherAssert.assertThat(recordedResponse, Is.is(serializedRequests));
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/retrieve").withQueryStringParameter("type", REQUESTS.name()).withQueryStringParameter("format", JSON.name()).withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("connection", "keep-alive"), new Header("content-type", "text/plain; charset=utf-8")).withSecure(false).withKeepAlive(true).withBody(new StringBody((((((("{" + (NEW_LINE)) + "  \"path\" : \"/some_path\",") + (NEW_LINE)) + "  \"body\" : \"some_request_body\"") + (NEW_LINE)) + "}")))));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldRetrieveRequestsAsJava() {
        // given
        String serializedRequest = new HttpRequestToJavaSerializer().serialize(Arrays.asList(request("/some_request_one"), request("/some_request_two")));
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201).withBody(new StringBody(serializedRequest)));
        // when
        String actualResponse = MockServerClientIntegrationTest.mockServerClient.retrieveRecordedRequests(request().withPath("/some_path").withBody(new StringBody("some_request_body")), JAVA);
        // then
        MatcherAssert.assertThat(actualResponse, Is.is(serializedRequest));
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/retrieve").withQueryStringParameter("type", REQUESTS.name()).withQueryStringParameter("format", JAVA.name()).withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("connection", "keep-alive"), new Header("content-type", "text/plain; charset=utf-8")).withSecure(false).withKeepAlive(true).withBody(new StringBody((((((("{" + (NEW_LINE)) + "  \"path\" : \"/some_path\",") + (NEW_LINE)) + "  \"body\" : \"some_request_body\"") + (NEW_LINE)) + "}")))));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldRetrieveActiveExpectations() {
        // given
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201).withBody(new StringBody(new org.mockserver.serialization.ExpectationSerializer(new MockServerLogger()).serialize(new Expectation(request("/some_request_one"), unlimited(), TimeToLive.unlimited()).thenRespond(response()), new Expectation(request("/some_request_two"), unlimited(), TimeToLive.unlimited()).thenRespond(response())))));
        // when
        Expectation[] actualResponse = MockServerClientIntegrationTest.mockServerClient.retrieveActiveExpectations(request().withPath("/some_path").withBody(new StringBody("some_request_body")));
        // then
        MatcherAssert.assertThat(Arrays.asList(actualResponse), IsCollectionContaining.hasItems(new Expectation(request("/some_request_one"), unlimited(), TimeToLive.unlimited()).thenRespond(response()), new Expectation(request("/some_request_two"), unlimited(), TimeToLive.unlimited()).thenRespond(response())));
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/retrieve").withQueryStringParameter("type", ACTIVE_EXPECTATIONS.name()).withQueryStringParameter("format", JSON.name()).withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("connection", "keep-alive"), new Header("content-type", "text/plain; charset=utf-8")).withSecure(false).withKeepAlive(true).withBody(new StringBody((((((("{" + (NEW_LINE)) + "  \"path\" : \"/some_path\",") + (NEW_LINE)) + "  \"body\" : \"some_request_body\"") + (NEW_LINE)) + "}")))));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldRetrieveActiveExpectationsWithNullRequest() {
        // given
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201).withBody(new StringBody(new org.mockserver.serialization.ExpectationSerializer(new MockServerLogger()).serialize(new Expectation(request("/some_request_one"), unlimited(), TimeToLive.unlimited()).thenRespond(response()), new Expectation(request("/some_request_two"), unlimited(), TimeToLive.unlimited()).thenRespond(response())))));
        // when
        Expectation[] actualResponse = MockServerClientIntegrationTest.mockServerClient.retrieveActiveExpectations(null);
        // then
        MatcherAssert.assertThat(Arrays.asList(actualResponse), IsCollectionContaining.hasItems(new Expectation(request("/some_request_one"), unlimited(), TimeToLive.unlimited()).thenRespond(response()), new Expectation(request("/some_request_two"), unlimited(), TimeToLive.unlimited()).thenRespond(response())));
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/retrieve").withQueryStringParameter("type", ACTIVE_EXPECTATIONS.name()).withQueryStringParameter("format", JSON.name()).withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("content-length", "0"), new Header("connection", "keep-alive"), new Header("content-type", "text/plain; charset=utf-8")).withSecure(false).withKeepAlive(true)));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldRetrieveActiveExpectationsAsJson() {
        // given
        String serializeExpectations = new org.mockserver.serialization.ExpectationSerializer(new MockServerLogger()).serialize(new Expectation(request("/some_request_one"), unlimited(), TimeToLive.unlimited()).thenRespond(response()), new Expectation(request("/some_request_two"), unlimited(), TimeToLive.unlimited()).thenRespond(response()));
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201).withBody(new StringBody(serializeExpectations)));
        // when
        String actualResponse = MockServerClientIntegrationTest.mockServerClient.retrieveActiveExpectations(request().withPath("/some_path").withBody(new StringBody("some_request_body")), JSON);
        // then
        MatcherAssert.assertThat(actualResponse, Is.is(serializeExpectations));
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/retrieve").withQueryStringParameter("type", ACTIVE_EXPECTATIONS.name()).withQueryStringParameter("format", JSON.name()).withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("connection", "keep-alive"), new Header("content-type", "text/plain; charset=utf-8")).withSecure(false).withKeepAlive(true).withBody(new StringBody((((((("{" + (NEW_LINE)) + "  \"path\" : \"/some_path\",") + (NEW_LINE)) + "  \"body\" : \"some_request_body\"") + (NEW_LINE)) + "}")))));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldRetrieveActiveExpectationsAsJava() {
        // given
        String serializedExpectations = new ExpectationToJavaSerializer().serialize(Arrays.asList(new Expectation(request("/some_request_one"), unlimited(), TimeToLive.unlimited()).thenRespond(response()), new Expectation(request("/some_request_two"), unlimited(), TimeToLive.unlimited()).thenRespond(response())));
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201).withBody(new StringBody(serializedExpectations)));
        // when
        String actualResponse = MockServerClientIntegrationTest.mockServerClient.retrieveActiveExpectations(request().withPath("/some_path").withBody(new StringBody("some_request_body")), JAVA);
        // then
        MatcherAssert.assertThat(actualResponse, Is.is(serializedExpectations));
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/retrieve").withQueryStringParameter("type", ACTIVE_EXPECTATIONS.name()).withQueryStringParameter("format", JAVA.name()).withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("connection", "keep-alive"), new Header("content-type", "text/plain; charset=utf-8")).withSecure(false).withKeepAlive(true).withBody(new StringBody((((((("{" + (NEW_LINE)) + "  \"path\" : \"/some_path\",") + (NEW_LINE)) + "  \"body\" : \"some_request_body\"") + (NEW_LINE)) + "}")))));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldRetrieveRecordedExpectations() {
        // given
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201).withBody(new StringBody(new org.mockserver.serialization.ExpectationSerializer(new MockServerLogger()).serialize(new Expectation(request("/some_request_one"), unlimited(), TimeToLive.unlimited()).thenRespond(response()), new Expectation(request("/some_request_two"), unlimited(), TimeToLive.unlimited()).thenRespond(response())))));
        // when
        Expectation[] actualResponse = MockServerClientIntegrationTest.mockServerClient.retrieveRecordedExpectations(request().withPath("/some_path").withBody(new StringBody("some_request_body")));
        // then
        MatcherAssert.assertThat(Arrays.asList(actualResponse), IsCollectionContaining.hasItems(new Expectation(request("/some_request_one"), unlimited(), TimeToLive.unlimited()).thenRespond(response()), new Expectation(request("/some_request_two"), unlimited(), TimeToLive.unlimited()).thenRespond(response())));
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/retrieve").withQueryStringParameter("type", RECORDED_EXPECTATIONS.name()).withQueryStringParameter("format", JSON.name()).withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("connection", "keep-alive"), new Header("content-type", "text/plain; charset=utf-8")).withSecure(false).withKeepAlive(true).withBody(new StringBody((((((("{" + (NEW_LINE)) + "  \"path\" : \"/some_path\",") + (NEW_LINE)) + "  \"body\" : \"some_request_body\"") + (NEW_LINE)) + "}")))));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldRetrieveRecordedExpectationsWithNullRequest() {
        // given
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201).withBody(new StringBody(new org.mockserver.serialization.ExpectationSerializer(new MockServerLogger()).serialize(new Expectation(request("/some_request_one"), unlimited(), TimeToLive.unlimited()).thenRespond(response()), new Expectation(request("/some_request_two"), unlimited(), TimeToLive.unlimited()).thenRespond(response())))));
        // when
        Expectation[] actualResponse = MockServerClientIntegrationTest.mockServerClient.retrieveRecordedExpectations(null);
        // then
        MatcherAssert.assertThat(Arrays.asList(actualResponse), IsCollectionContaining.hasItems(new Expectation(request("/some_request_one"), unlimited(), TimeToLive.unlimited()).thenRespond(response()), new Expectation(request("/some_request_two"), unlimited(), TimeToLive.unlimited()).thenRespond(response())));
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/retrieve").withQueryStringParameter("type", RECORDED_EXPECTATIONS.name()).withQueryStringParameter("format", JSON.name()).withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("content-length", "0"), new Header("connection", "keep-alive"), new Header("content-type", "text/plain; charset=utf-8")).withSecure(false).withKeepAlive(true)));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldRetrieveRecordedExpectationsAsJson() {
        // given
        String serializeExpectations = new org.mockserver.serialization.ExpectationSerializer(new MockServerLogger()).serialize(new Expectation(request("/some_request_one"), unlimited(), TimeToLive.unlimited()).thenRespond(response()), new Expectation(request("/some_request_two"), unlimited(), TimeToLive.unlimited()).thenRespond(response()));
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201).withBody(new StringBody(serializeExpectations)));
        // when
        String actualResponse = MockServerClientIntegrationTest.mockServerClient.retrieveRecordedExpectations(request().withPath("/some_path").withBody(new StringBody("some_request_body")), JSON);
        // then
        MatcherAssert.assertThat(actualResponse, Is.is(serializeExpectations));
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/retrieve").withQueryStringParameter("type", RECORDED_EXPECTATIONS.name()).withQueryStringParameter("format", JSON.name()).withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("connection", "keep-alive"), new Header("content-type", "text/plain; charset=utf-8")).withSecure(false).withKeepAlive(true).withBody(new StringBody((((((("{" + (NEW_LINE)) + "  \"path\" : \"/some_path\",") + (NEW_LINE)) + "  \"body\" : \"some_request_body\"") + (NEW_LINE)) + "}")))));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldRetrieveRecordedExpectationsAsJava() {
        // given
        String serializedExpectations = new ExpectationToJavaSerializer().serialize(Arrays.asList(new Expectation(request("/some_request_one"), unlimited(), TimeToLive.unlimited()).thenRespond(response()), new Expectation(request("/some_request_two"), unlimited(), TimeToLive.unlimited()).thenRespond(response())));
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201).withBody(new StringBody(serializedExpectations)));
        // when
        String actualResponse = MockServerClientIntegrationTest.mockServerClient.retrieveRecordedExpectations(request().withPath("/some_path").withBody(new StringBody("some_request_body")), JAVA);
        // then
        MatcherAssert.assertThat(actualResponse, Is.is(serializedExpectations));
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/retrieve").withQueryStringParameter("type", RECORDED_EXPECTATIONS.name()).withQueryStringParameter("format", JAVA.name()).withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("connection", "keep-alive"), new Header("content-type", "text/plain; charset=utf-8")).withSecure(false).withKeepAlive(true).withBody(new StringBody((((((("{" + (NEW_LINE)) + "  \"path\" : \"/some_path\",") + (NEW_LINE)) + "  \"body\" : \"some_request_body\"") + (NEW_LINE)) + "}")))));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldVerifySingleRequestNoVerificationTimes() {
        // given
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201));
        // when
        MockServerClientIntegrationTest.mockServerClient.verify(request().withPath("/some_path").withBody(new StringBody("some_request_body")));
        // then
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/verifySequence").withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("connection", "keep-alive"), new Header("content-type", "text/plain; charset=utf-8")).withSecure(false).withKeepAlive(true).withBody(new StringBody(((((((((((("" + "{") + (NEW_LINE)) + "  \"httpRequests\" : [ {") + (NEW_LINE)) + "    \"path\" : \"/some_path\",") + (NEW_LINE)) + "    \"body\" : \"some_request_body\"") + (NEW_LINE)) + "  } ]") + (NEW_LINE)) + "}")))));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldVerifyMultipleRequestsNoVerificationTimes() {
        // given
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201));
        // when
        MockServerClientIntegrationTest.mockServerClient.verify(request().withPath("/some_path").withBody(new StringBody("some_request_body")), request().withPath("/some_path").withBody(new StringBody("some_request_body")));
        // then
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/verifySequence").withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("connection", "keep-alive"), new Header("content-type", "text/plain; charset=utf-8")).withSecure(false).withKeepAlive(true).withBody(new StringBody(((((((((((((((((("" + "{") + (NEW_LINE)) + "  \"httpRequests\" : [ {") + (NEW_LINE)) + "    \"path\" : \"/some_path\",") + (NEW_LINE)) + "    \"body\" : \"some_request_body\"") + (NEW_LINE)) + "  }, {") + (NEW_LINE)) + "    \"path\" : \"/some_path\",") + (NEW_LINE)) + "    \"body\" : \"some_request_body\"") + (NEW_LINE)) + "  } ]") + (NEW_LINE)) + "}")))));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }

    @Test
    public void shouldVerifySingleRequestOnce() {
        // given
        MockServerClientIntegrationTest.echoServer.withNextResponse(response().withStatusCode(201));
        // when
        MockServerClientIntegrationTest.mockServerClient.verify(request().withPath("/some_path").withBody(new StringBody("some_request_body")), VerificationTimes.once());
        // then
        MatcherAssert.assertThat(MockServerClientIntegrationTest.logFilter.retrieveRequests(request()).size(), Is.is(1));
        String result = MockServerClientIntegrationTest.logFilter.verify(verification().withRequest(request().withMethod("PUT").withPath("/mockserver/verify").withHeaders(new Header("host", ("localhost:" + (MockServerClientIntegrationTest.echoServer.getPort()))), new Header("accept-encoding", "gzip,deflate"), new Header("connection", "keep-alive"), new Header("content-type", "text/plain; charset=utf-8")).withSecure(false).withKeepAlive(true).withBody(new StringBody(((((((((((((((((((("" + "{") + (NEW_LINE)) + "  \"httpRequest\" : {") + (NEW_LINE)) + "    \"path\" : \"/some_path\",") + (NEW_LINE)) + "    \"body\" : \"some_request_body\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"times\" : {") + (NEW_LINE)) + "    \"atLeast\" : 1,") + (NEW_LINE)) + "    \"atMost\" : 1") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}")))));
        if ((result != null) && (!(result.isEmpty()))) {
            throw new AssertionError(result);
        }
    }
}

