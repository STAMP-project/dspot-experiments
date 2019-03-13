package org.mockserver.client.netty;


import EchoServer.Error;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.HttpHeaderValues;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.AnyOf;
import org.hamcrest.core.Is;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockserver.echo.http.EchoServer;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.StringBody;
import org.mockserver.socket.PortFactory;


public class NettyHttpClientErrorHandlingTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private static EventLoopGroup clientEventLoopGroup = new NioEventLoopGroup();

    @Test
    public void shouldThrowSocketCommunicationExceptionForConnectException() throws Exception {
        // then
        int freePort = PortFactory.findFreePort();
        exception.expect(ExecutionException.class);
        exception.expectMessage(AnyOf.anyOf(Matchers.containsString(("Connection refused: /127.0.0.1:" + freePort)), Matchers.containsString(("Connection refused: no further information: /127.0.0.1:" + freePort)), Matchers.containsString("Channel closed before valid response")));
        // when
        new org.mockserver.client.NettyHttpClient(NettyHttpClientErrorHandlingTest.clientEventLoopGroup, null).sendRequest(HttpRequest.request().withHeader(HOST.toString(), ("127.0.0.1:" + freePort))).get(10, TimeUnit.SECONDS);
    }

    @Test
    public void shouldHandleConnectionClosure() throws Exception {
        // given
        EchoServer echoServer = new EchoServer(true, Error.CLOSE_CONNECTION);
        try {
            // then
            exception.expect(ExecutionException.class);
            exception.expectMessage(AnyOf.anyOf(Matchers.containsString("Exception caught before valid response has been received"), Matchers.containsString("Channel set as inactive before valid response has been received")));
            // when
            new org.mockserver.client.NettyHttpClient(NettyHttpClientErrorHandlingTest.clientEventLoopGroup, null).sendRequest(HttpRequest.request().withSecure(true).withHeader(HOST.toString(), ("127.0.0.1:" + (echoServer.getPort())))).get(10, TimeUnit.SECONDS);
        } finally {
            stopQuietly(echoServer);
        }
    }

    @Test
    public void shouldHandleSmallerContentLengthHeader() throws Exception {
        // given
        EchoServer echoServer = new EchoServer(true, Error.SMALLER_CONTENT_LENGTH);
        try {
            // when
            InetSocketAddress socket = new InetSocketAddress("127.0.0.1", echoServer.getPort());
            HttpResponse httpResponse = new org.mockserver.client.NettyHttpClient(NettyHttpClientErrorHandlingTest.clientEventLoopGroup, null).sendRequest(HttpRequest.request().withBody(StringBody.exact("this is an example body")).withSecure(true), socket).get(10, TimeUnit.SECONDS);
            // then
            MatcherAssert.assertThat(httpResponse, Is.is(HttpResponse.response().withStatusCode(200).withReasonPhrase("OK").withHeader(Header.header(CONTENT_LENGTH.toString(), (("this is an example body".length()) / 2))).withHeader(Header.header(CONNECTION.toString(), HttpHeaderValues.KEEP_ALIVE.toString())).withHeader(Header.header(ACCEPT_ENCODING.toString(), (((GZIP.toString()) + ",") + (DEFLATE.toString())))).withBody(StringBody.exact("this is an "))));
        } finally {
            stopQuietly(echoServer);
        }
    }
}

