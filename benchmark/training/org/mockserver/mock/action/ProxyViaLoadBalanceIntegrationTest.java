package org.mockserver.mock.action;


import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.HttpHeaderNames;
import java.net.InetSocketAddress;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.client.NettyHttpClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpResponse;
import org.mockserver.verify.VerificationTimes;


/**
 *
 *
 * @author jamesdbloom
 */
public class ProxyViaLoadBalanceIntegrationTest {
    private static ClientAndServer clientAndServer;

    private static ClientAndServer loadBalancerClient;

    private static EventLoopGroup clientEventLoopGroup = new NioEventLoopGroup();

    private static NettyHttpClient httpClient = new NettyHttpClient(ProxyViaLoadBalanceIntegrationTest.clientEventLoopGroup, null);

    @Test
    public void shouldNotForwardRequestWithInvalidHostHead() throws Exception {
        // when
        Future<HttpResponse> responseSettableFuture = ProxyViaLoadBalanceIntegrationTest.httpClient.sendRequest(request().withPath("/some_path").withHeader(HttpHeaderNames.HOST.toString(), ("localhost:" + (ProxyViaLoadBalanceIntegrationTest.loadBalancerClient.getLocalPort()))), new InetSocketAddress(ProxyViaLoadBalanceIntegrationTest.loadBalancerClient.getLocalPort()));
        // then - returns 404
        MatcherAssert.assertThat(responseSettableFuture.get(10, TimeUnit.SECONDS).getStatusCode(), Is.is(404));
        // and - logs hide proxied request
        MatcherAssert.assertThat(ProxyViaLoadBalanceIntegrationTest.clientAndServer.retrieveLogMessagesArray(null)[2], CoreMatchers.containsString(((((((("no expectation for:" + (NEW_LINE)) + (NEW_LINE)) + "\t{") + (NEW_LINE)) + "\t  \"method\" : \"GET\",") + (NEW_LINE)) + "\t  \"path\" : \"/some_path\",")));
        MatcherAssert.assertThat(ProxyViaLoadBalanceIntegrationTest.clientAndServer.retrieveLogMessagesArray(null)[2], CoreMatchers.containsString(((((((((((" returning response:" + (NEW_LINE)) + (NEW_LINE)) + "\t{") + (NEW_LINE)) + "\t  \"statusCode\" : 404,") + (NEW_LINE)) + "\t  \"reasonPhrase\" : \"Not Found\"") + (NEW_LINE)) + "\t}") + (NEW_LINE))));
        MatcherAssert.assertThat(ProxyViaLoadBalanceIntegrationTest.loadBalancerClient.retrieveLogMessagesArray(null).length, Is.is(5));
    }

    @Test
    public void shouldVerifyReceivedRequests() throws Exception {
        // given
        Future<HttpResponse> responseSettableFuture = ProxyViaLoadBalanceIntegrationTest.httpClient.sendRequest(request().withPath("/some_path").withHeader(HttpHeaderNames.HOST.toString(), ("localhost:" + (ProxyViaLoadBalanceIntegrationTest.loadBalancerClient.getLocalPort()))), new InetSocketAddress(ProxyViaLoadBalanceIntegrationTest.loadBalancerClient.getLocalPort()));
        // then
        MatcherAssert.assertThat(responseSettableFuture.get(10, TimeUnit.SECONDS).getStatusCode(), Is.is(404));
        // then
        ProxyViaLoadBalanceIntegrationTest.clientAndServer.verify(request().withPath("/some_path"));
        ProxyViaLoadBalanceIntegrationTest.clientAndServer.verify(request().withPath("/some_path"), VerificationTimes.exactly(1));
    }
}

