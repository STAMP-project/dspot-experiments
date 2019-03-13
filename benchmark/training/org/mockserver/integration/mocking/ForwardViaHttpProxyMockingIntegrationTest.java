package org.mockserver.integration.mocking;


import HttpStatusCode.NOT_FOUND_404;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.integration.server.AbstractMockingIntegrationTestBase;
import org.mockserver.mockserver.MockServer;


/**
 *
 *
 * @author jamesdbloom
 */
public class ForwardViaHttpProxyMockingIntegrationTest extends AbstractMockingIntegrationTestBase {
    private static MockServer mockServer;

    private static MockServer proxy;

    @Test
    public void shouldForwardRequestInHTTP() {
        // when
        forward(forward().withHost("127.0.0.1").withPort(insecureEchoServer.getPort()));
        // then
        Assert.assertEquals(response().withStatusCode(OK_200.code()).withReasonPhrase(OK_200.reasonPhrase()).withHeaders(header("x-test", "test_headers_and_body"), header("x-forwarded-by", "MockServer")).withBody("an_example_body_http"), makeRequest(request().withPath(calculatePath("echo")).withMethod("POST").withHeaders(header("Host", ("127.0.0.1:" + (insecureEchoServer.getPort()))), header("x-test", "test_headers_and_body")).withBody("an_example_body_http"), headersToIgnore));
    }

    @Test
    public void shouldForwardOverriddenRequest() {
        // when
        forward(forwardOverriddenRequest(request().withHeader("Host", ("localhost:" + (insecureEchoServer.getPort()))).withBody("some_overridden_body")).withDelay(TimeUnit.MILLISECONDS, 10));
        forward(forwardOverriddenRequest(request().withHeader("Host", ("localhost:" + (insecureEchoServer.getPort()))).withBody("some_overridden_body")).withDelay(TimeUnit.MILLISECONDS, 10));
        // then
        Assert.assertEquals(response().withStatusCode(OK_200.code()).withReasonPhrase(OK_200.reasonPhrase()).withHeaders(header("x-test", "test_headers_and_body"), header("x-forwarded-by", "MockServer")).withBody("some_overridden_body"), makeRequest(request().withPath(calculatePath("echo")).withMethod("POST").withHeaders(header("x-test", "test_headers_and_body")).withBody("an_example_body_http"), headersToIgnore));
    }

    @Test
    public void shouldCallbackForForwardToSpecifiedClassWithPrecannedResponse() {
        // when
        forward(callback().withCallbackClass("org.mockserver.integration.callback.PrecannedTestExpectationForwardCallback"));
        // then
        Assert.assertEquals(response().withStatusCode(OK_200.code()).withReasonPhrase(OK_200.reasonPhrase()).withHeaders(header("x-test", "test_headers_and_body"), header("x-forwarded-by", "MockServer")).withBody("some_overridden_body"), makeRequest(request().withPath(calculatePath("echo")).withMethod("POST").withHeaders(header("x-test", "test_headers_and_body"), header("x-echo-server-port", insecureEchoServer.getPort())).withBody("an_example_body_http"), headersToIgnore));
    }

    @Test
    public void shouldForwardTemplateInVelocity() {
        // when
        forward(template(HttpTemplate.TemplateType.VELOCITY, (((((((((((((((((((((("{" + (NEW_LINE)) + "    \'path\' : \"/somePath\",") + (NEW_LINE)) + "    'headers' : [ {") + (NEW_LINE)) + "        \'name\' : \"Host\",") + (NEW_LINE)) + "        \'values\' : [ \"127.0.0.1:") + (insecureEchoServer.getPort())) + "\" ]") + (NEW_LINE)) + "    }, {") + (NEW_LINE)) + "        \'name\' : \"x-test\",") + (NEW_LINE)) + "        \'values\' : [ \"$!request.headers[\'x-test\'][0]\" ]") + (NEW_LINE)) + "    } ],") + (NEW_LINE)) + "    \'body\': \"{\'name\': \'value\'}\"") + (NEW_LINE)) + "}")).withDelay(TimeUnit.MILLISECONDS, 10));
        // then
        Assert.assertEquals(response().withStatusCode(OK_200.code()).withReasonPhrase(OK_200.reasonPhrase()).withHeaders(header("x-test", "test_headers_and_body"), header("x-forwarded-by", "MockServer")).withBody("{'name': 'value'}"), makeRequest(request().withPath(calculatePath("echo")).withMethod("POST").withHeaders(header("x-test", "test_headers_and_body")).withBody("an_example_body_http"), headersToIgnore));
    }

    @Test
    public void shouldAllowSimultaneousForwardAndResponseExpectations() {
        // when
        forward(forwardOverriddenRequest(request().withHeader("Host", ("localhost:" + (insecureEchoServer.getPort()))).withBody("some_overridden_body")).withDelay(TimeUnit.MILLISECONDS, 10));
        mockServerClient.when(request().withPath(calculatePath("test_headers_and_body")), once()).respond(response().withBody("some_body"));
        // then
        // - forward
        Assert.assertEquals(response().withStatusCode(OK_200.code()).withReasonPhrase(OK_200.reasonPhrase()).withHeaders(header("x-test", "test_headers_and_body"), header("x-forwarded-by", "MockServer")).withBody("some_overridden_body"), makeRequest(request().withPath(calculatePath("echo")).withMethod("POST").withHeaders(header("x-test", "test_headers_and_body")).withBody("an_example_body"), headersToIgnore));
        // - respond
        Assert.assertEquals(response().withStatusCode(OK_200.code()).withReasonPhrase(OK_200.reasonPhrase()).withBody("some_body"), makeRequest(request().withPath(calculatePath("test_headers_and_body")), headersToIgnore));
        // - no response or forward
        Assert.assertEquals(response().withStatusCode(NOT_FOUND_404.code()).withReasonPhrase(NOT_FOUND_404.reasonPhrase()), makeRequest(request().withPath(calculatePath("test_headers_and_body")), headersToIgnore));
    }
}

