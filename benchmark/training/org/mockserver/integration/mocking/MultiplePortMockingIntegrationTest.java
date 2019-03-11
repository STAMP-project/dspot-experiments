package org.mockserver.integration.mocking;


import com.google.common.base.Joiner;
import com.google.common.net.MediaType;
import io.netty.handler.codec.http.HttpHeaderNames;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.integration.server.AbstractBasicMockingIntegrationTest;
import org.mockserver.socket.PortFactory;


/**
 *
 *
 * @author jamesdbloom
 */
public class MultiplePortMockingIntegrationTest extends AbstractBasicMockingIntegrationTest {
    private static Integer[] severHttpPort;

    private final Random random = new Random();

    @Test
    public void shouldReturnStatus() {
        // then
        // - in http
        Assert.assertEquals(response().withStatusCode(OK_200.code()).withReasonPhrase(OK_200.reasonPhrase()).withHeader(HttpHeaderNames.CONTENT_TYPE.toString(), "application/json; charset=utf-8").withBody((((((("{" + (NEW_LINE)) + "  \"ports\" : [ ") + (Joiner.on(", ").join(MultiplePortMockingIntegrationTest.severHttpPort))) + " ]") + (NEW_LINE)) + "}"), MediaType.JSON_UTF_8), makeRequest(request().withPath(calculatePath("mockserver/status")).withMethod("PUT"), headersToIgnore));
        // - in https
        Assert.assertEquals(response().withStatusCode(OK_200.code()).withReasonPhrase(OK_200.reasonPhrase()).withHeader(HttpHeaderNames.CONTENT_TYPE.toString(), "application/json; charset=utf-8").withBody((((((("{" + (NEW_LINE)) + "  \"ports\" : [ ") + (Joiner.on(", ").join(MultiplePortMockingIntegrationTest.severHttpPort))) + " ]") + (NEW_LINE)) + "}"), MediaType.JSON_UTF_8), makeRequest(request().withSecure(true).withPath(calculatePath("mockserver/status")).withMethod("PUT"), headersToIgnore));
    }

    @Test
    public void shouldBindToNewSocket() {
        // given
        int firstNewPort = PortFactory.findFreePort();
        int secondNewPort = PortFactory.findFreePort();
        // then
        // - in http
        Assert.assertEquals(response().withStatusCode(OK_200.code()).withReasonPhrase(OK_200.reasonPhrase()).withHeader(HttpHeaderNames.CONTENT_TYPE.toString(), "application/json; charset=utf-8").withBody((((((("{" + (NEW_LINE)) + "  \"ports\" : [ ") + firstNewPort) + " ]") + (NEW_LINE)) + "}"), MediaType.JSON_UTF_8), makeRequest(request().withPath(calculatePath("mockserver/bind")).withMethod("PUT").withBody((((((("{" + (NEW_LINE)) + "  \"ports\" : [ ") + firstNewPort) + " ]") + (NEW_LINE)) + "}")), headersToIgnore));
        Assert.assertEquals(response().withStatusCode(OK_200.code()).withReasonPhrase(OK_200.reasonPhrase()).withHeader(HttpHeaderNames.CONTENT_TYPE.toString(), "application/json; charset=utf-8").withBody((((((((("{" + (NEW_LINE)) + "  \"ports\" : [ ") + (Joiner.on(", ").join(MultiplePortMockingIntegrationTest.severHttpPort))) + ", ") + firstNewPort) + " ]") + (NEW_LINE)) + "}"), MediaType.JSON_UTF_8), makeRequest(request().withPath(calculatePath("mockserver/status")).withMethod("PUT"), headersToIgnore));
        // - in https
        Assert.assertEquals(response().withStatusCode(OK_200.code()).withReasonPhrase(OK_200.reasonPhrase()).withHeader(HttpHeaderNames.CONTENT_TYPE.toString(), "application/json; charset=utf-8").withBody((((((("{" + (NEW_LINE)) + "  \"ports\" : [ ") + secondNewPort) + " ]") + (NEW_LINE)) + "}"), MediaType.JSON_UTF_8), makeRequest(request().withSecure(true).withPath(calculatePath("mockserver/bind")).withMethod("PUT").withBody((((((("{" + (NEW_LINE)) + "  \"ports\" : [ ") + secondNewPort) + " ]") + (NEW_LINE)) + "}")), headersToIgnore));
        Assert.assertEquals(response().withStatusCode(OK_200.code()).withReasonPhrase(OK_200.reasonPhrase()).withHeader(HttpHeaderNames.CONTENT_TYPE.toString(), "application/json; charset=utf-8").withBody((((((((((("{" + (NEW_LINE)) + "  \"ports\" : [ ") + (Joiner.on(", ").join(MultiplePortMockingIntegrationTest.severHttpPort))) + ", ") + firstNewPort) + ", ") + secondNewPort) + " ]") + (NEW_LINE)) + "}"), MediaType.JSON_UTF_8), makeRequest(request().withSecure(true).withPath(calculatePath("mockserver/status")).withMethod("PUT").withBody((((((("{" + (NEW_LINE)) + "  \"ports\" : [ ") + firstNewPort) + " ]") + (NEW_LINE)) + "}")), headersToIgnore));
    }
}

