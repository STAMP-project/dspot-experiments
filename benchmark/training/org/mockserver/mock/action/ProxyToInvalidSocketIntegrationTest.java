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
import org.mockserver.socket.PortFactory;
import org.mockserver.verify.VerificationTimes;


/**
 *
 *
 * @author jamesdbloom
 */
public class ProxyToInvalidSocketIntegrationTest {
    private static ClientAndServer clientAndServer;

    private static EventLoopGroup clientEventLoopGroup = new NioEventLoopGroup();

    private static NettyHttpClient httpClient = new NettyHttpClient(ProxyToInvalidSocketIntegrationTest.clientEventLoopGroup, null);

    @Test
    public void shouldNotForwardRequestWithInvalidHostHead() throws Exception {
        // when
        Future<HttpResponse> responseSettableFuture = ProxyToInvalidSocketIntegrationTest.httpClient.sendRequest(request().withPath("/some_path").withHeader(HttpHeaderNames.HOST.toString(), ("localhost:" + (PortFactory.findFreePort()))), new InetSocketAddress(ProxyToInvalidSocketIntegrationTest.clientAndServer.getLocalPort()));
        // then
        MatcherAssert.assertThat(responseSettableFuture.get(10, TimeUnit.SECONDS).getStatusCode(), Is.is(404));
    }

    @Test
    public void shouldVerifyReceivedRequests() throws Exception {
        // given
        Future<HttpResponse> responseSettableFuture = ProxyToInvalidSocketIntegrationTest.httpClient.sendRequest(request().withPath("/some_path").withHeader(HttpHeaderNames.HOST.toString(), ("localhost:" + (PortFactory.findFreePort()))), new InetSocketAddress(ProxyToInvalidSocketIntegrationTest.clientAndServer.getLocalPort()));
        // then
        MatcherAssert.assertThat(responseSettableFuture.get(10, TimeUnit.SECONDS).getStatusCode(), Is.is(404));
        // then
        ProxyToInvalidSocketIntegrationTest.clientAndServer.verify(request().withPath("/some_path"));
        ProxyToInvalidSocketIntegrationTest.clientAndServer.verify(request().withPath("/some_path"), VerificationTimes.exactly(1));
    }
}

