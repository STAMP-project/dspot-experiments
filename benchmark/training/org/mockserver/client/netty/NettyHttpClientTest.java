package org.mockserver.client.netty;


import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.HttpHeaderValues;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockserver.client.NettyHttpClient;
import org.mockserver.echo.http.EchoServer;
import org.mockserver.model.Cookie;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.StringBody;


public class NettyHttpClientTest {
    private static EchoServer echoServer;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private static EventLoopGroup clientEventLoopGroup = new NioEventLoopGroup();

    @Test
    public void shouldSendBasicRequest() throws Exception {
        // given
        NettyHttpClient nettyHttpClient = new NettyHttpClient(NettyHttpClientTest.clientEventLoopGroup, null);
        // when
        HttpResponse httpResponse = nettyHttpClient.sendRequest(HttpRequest.request().withHeader("Host", ("0.0.0.0:" + (NettyHttpClientTest.echoServer.getPort())))).get(10, TimeUnit.SECONDS);
        // then
        MatcherAssert.assertThat(httpResponse, Is.is(HttpResponse.response().withStatusCode(200).withReasonPhrase("OK").withHeader(Header.header(HOST.toString(), ("0.0.0.0:" + (NettyHttpClientTest.echoServer.getPort())))).withHeader(Header.header(ACCEPT_ENCODING.toString(), (((GZIP.toString()) + ",") + (DEFLATE.toString())))).withHeader(Header.header(CONNECTION.toString(), HttpHeaderValues.KEEP_ALIVE.toString())).withHeader(Header.header(CONTENT_LENGTH.toString(), 0))));
    }

    @Test
    public void shouldSendBasicRequestToAnotherIpAndPort() throws Exception {
        // given
        NettyHttpClient nettyHttpClient = new NettyHttpClient(NettyHttpClientTest.clientEventLoopGroup, null);
        // when
        HttpResponse httpResponse = nettyHttpClient.sendRequest(HttpRequest.request().withHeader("Host", "www.google.com"), new InetSocketAddress("0.0.0.0", NettyHttpClientTest.echoServer.getPort())).get(10, TimeUnit.SECONDS);
        // then
        MatcherAssert.assertThat(httpResponse, Is.is(HttpResponse.response().withStatusCode(200).withReasonPhrase("OK").withHeader(Header.header(HOST.toString(), "www.google.com")).withHeader(Header.header(ACCEPT_ENCODING.toString(), (((GZIP.toString()) + ",") + (DEFLATE.toString())))).withHeader(Header.header(CONNECTION.toString(), HttpHeaderValues.KEEP_ALIVE.toString())).withHeader(Header.header(CONTENT_LENGTH.toString(), 0))));
    }

    @Test
    public void shouldSendBasicRequestToAnotherIpAndPortWithNoHostHeader() throws Exception {
        // given
        NettyHttpClient nettyHttpClient = new NettyHttpClient(NettyHttpClientTest.clientEventLoopGroup, null);
        // when
        HttpResponse httpResponse = nettyHttpClient.sendRequest(HttpRequest.request(), new InetSocketAddress("0.0.0.0", NettyHttpClientTest.echoServer.getPort())).get(10, TimeUnit.SECONDS);
        // then
        MatcherAssert.assertThat(httpResponse, Is.is(HttpResponse.response().withStatusCode(200).withReasonPhrase("OK").withHeader(Header.header(ACCEPT_ENCODING.toString(), (((GZIP.toString()) + ",") + (DEFLATE.toString())))).withHeader(Header.header(CONNECTION.toString(), HttpHeaderValues.KEEP_ALIVE.toString())).withHeader(Header.header(CONTENT_LENGTH.toString(), 0))));
    }

    @Test
    public void shouldSendComplexRequest() throws Exception {
        // given
        NettyHttpClient nettyHttpClient = new NettyHttpClient(NettyHttpClientTest.clientEventLoopGroup, null);
        // when
        HttpResponse httpResponse = nettyHttpClient.sendRequest(HttpRequest.request().withHeader("Host", ("0.0.0.0:" + (NettyHttpClientTest.echoServer.getPort()))).withHeader(Header.header("some_header_name", "some_header_value")).withHeader(Header.header("another_header_name", "first_header_value", "second_header_value")).withCookie(Cookie.cookie("some_cookie_name", "some_cookie_value")).withCookie(Cookie.cookie("another_cookie_name", "another_cookie_value")).withBody(StringBody.exact("this is an example body"))).get(10, TimeUnit.SECONDS);
        // then
        MatcherAssert.assertThat(httpResponse, Is.is(HttpResponse.response().withStatusCode(200).withReasonPhrase("OK").withHeader(Header.header(HOST.toString(), ("0.0.0.0:" + (NettyHttpClientTest.echoServer.getPort())))).withHeader(Header.header(CONTENT_LENGTH.toString(), "this is an example body".length())).withHeader(Header.header(ACCEPT_ENCODING.toString(), (((GZIP.toString()) + ",") + (DEFLATE.toString())))).withHeader(Header.header(CONNECTION.toString(), HttpHeaderValues.KEEP_ALIVE.toString())).withHeader(Header.header(COOKIE.toString(), "some_cookie_name=some_cookie_value; another_cookie_name=another_cookie_value")).withHeader(Header.header("some_header_name", "some_header_value")).withHeader(Header.header("another_header_name", "first_header_value", "second_header_value")).withCookie(Cookie.cookie("some_cookie_name", "some_cookie_value")).withCookie(Cookie.cookie("another_cookie_name", "another_cookie_value")).withBody(StringBody.exact("this is an example body"))));
    }
}

