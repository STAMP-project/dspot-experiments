package org.mockserver.mock.action;


import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.HttpHeaderNames;
import java.net.InetSocketAddress;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.client.NettyHttpClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpResponse;


/**
 *
 *
 * @author jamesdbloom
 */
public class OverridePreFlightRequestIntegrationTest {
    private static ClientAndServer clientAndServer;

    private static EventLoopGroup clientEventLoopGroup = new NioEventLoopGroup();

    private static NettyHttpClient httpClient = new NettyHttpClient(OverridePreFlightRequestIntegrationTest.clientEventLoopGroup, null);

    @Test
    public void shouldReturnDefaultPreFlightResponse() throws Exception {
        // when
        Future<HttpResponse> responseSettableFuture = OverridePreFlightRequestIntegrationTest.httpClient.sendRequest(request().withMethod("OPTIONS").withPath("/expectation").withHeader("Access-Control-Request-Method", "PUT").withHeader(HttpHeaderNames.HOST.toString(), ("localhost:" + (OverridePreFlightRequestIntegrationTest.clientAndServer.getLocalPort()))).withHeader("Origin", "http://127.0.0.1:1234"), new InetSocketAddress(OverridePreFlightRequestIntegrationTest.clientAndServer.getLocalPort()));
        // then
        HttpResponse response = responseSettableFuture.get(10, TimeUnit.SECONDS);
        MatcherAssert.assertThat(response.getStatusCode(), Is.is(200));
        MatcherAssert.assertThat(response.getHeader("access-control-allow-origin"), containsInAnyOrder("*"));
        MatcherAssert.assertThat(response.getHeader("access-control-allow-methods"), containsInAnyOrder("CONNECT, DELETE, GET, HEAD, OPTIONS, POST, PUT, PATCH, TRACE"));
        MatcherAssert.assertThat(response.getHeader("access-control-allow-headers"), containsInAnyOrder("Allow, Content-Encoding, Content-Length, Content-Type, ETag, Expires, Last-Modified, Location, Server, Vary, Authorization"));
        MatcherAssert.assertThat(response.getHeader("access-control-expose-headers"), containsInAnyOrder("Allow, Content-Encoding, Content-Length, Content-Type, ETag, Expires, Last-Modified, Location, Server, Vary, Authorization"));
        MatcherAssert.assertThat(response.getHeader("access-control-max-age"), containsInAnyOrder("300"));
        MatcherAssert.assertThat(response.getHeader("x-cors"), containsInAnyOrder("MockServer CORS support enabled by default, to disable ConfigurationProperties.enableCORSForAPI(false) or -Dmockserver.enableCORSForAPI=false"));
        MatcherAssert.assertThat(response.getFirstHeader("version"), not(isEmptyString()));
    }

    @Test
    public void shouldReturnOveriddenPreFlightResponse() throws Exception {
        // given
        OverridePreFlightRequestIntegrationTest.clientAndServer.when(request().withMethod("OPTIONS")).respond(response().withHeader("access-control-allow-origin", "*").withHeader("access-control-allow-methods", "CONNECT, DELETE, GET, HEAD, OPTIONS, POST, PUT, PATCH, TRACE").withHeader("access-control-allow-headers", "Allow, Content-Encoding, Content-Length, Content-Type, ETag, Expires, Last-Modified, Location, Server, Vary, Authorization, Authorization").withHeader("access-control-expose-headers", "Allow, Content-Encoding, Content-Length, Content-Type, ETag, Expires, Last-Modified, Location, Server, Vary, Authorization, Authorization").withHeader("access-control-max-age", "300"));
        // when
        Future<HttpResponse> responseSettableFuture = OverridePreFlightRequestIntegrationTest.httpClient.sendRequest(request().withMethod("OPTIONS").withPath("/expectation").withHeader("Access-Control-Request-Method", "PUT").withHeader(HttpHeaderNames.HOST.toString(), ("localhost:" + (OverridePreFlightRequestIntegrationTest.clientAndServer.getLocalPort()))).withHeader("Origin", "http://127.0.0.1:1234"), new InetSocketAddress(OverridePreFlightRequestIntegrationTest.clientAndServer.getLocalPort()));
        // then
        HttpResponse response = responseSettableFuture.get(10, TimeUnit.SECONDS);
        MatcherAssert.assertThat(response.getStatusCode(), Is.is(200));
        MatcherAssert.assertThat(response.getHeader("access-control-allow-origin"), containsInAnyOrder("*"));
        MatcherAssert.assertThat(response.getHeader("access-control-allow-methods"), containsInAnyOrder("CONNECT, DELETE, GET, HEAD, OPTIONS, POST, PUT, PATCH, TRACE"));
        MatcherAssert.assertThat(response.getHeader("access-control-allow-headers"), containsInAnyOrder("Allow, Content-Encoding, Content-Length, Content-Type, ETag, Expires, Last-Modified, Location, Server, Vary, Authorization, Authorization"));
        MatcherAssert.assertThat(response.getHeader("access-control-expose-headers"), containsInAnyOrder("Allow, Content-Encoding, Content-Length, Content-Type, ETag, Expires, Last-Modified, Location, Server, Vary, Authorization, Authorization"));
        MatcherAssert.assertThat(response.getHeader("access-control-max-age"), containsInAnyOrder("300"));
        MatcherAssert.assertThat(response.getFirstHeader("x-cors"), isEmptyString());
        MatcherAssert.assertThat(response.getFirstHeader("version"), isEmptyString());
    }
}

