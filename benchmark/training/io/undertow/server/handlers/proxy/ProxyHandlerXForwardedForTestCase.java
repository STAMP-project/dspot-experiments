package io.undertow.server.handlers.proxy;


import Headers.X_FORWARDED_FOR;
import Headers.X_FORWARDED_HOST;
import Headers.X_FORWARDED_PORT;
import Headers.X_FORWARDED_PROTO;
import StatusCodes.OK;
import io.undertow.Undertow;
import io.undertow.protocols.ssl.UndertowXnioSsl;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpOneOnly;
import io.undertow.testutils.ProxyIgnore;
import io.undertow.testutils.TestHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Created by ivannagy on 8/26/14.
 */
@RunWith(DefaultServer.class)
@HttpOneOnly
@ProxyIgnore
public class ProxyHandlerXForwardedForTestCase {
    protected static Undertow server;

    protected static int port;

    protected static int sslPort;

    protected static int handlerPort;

    protected static UndertowXnioSsl ssl;

    @Test
    public void testXForwarded() throws Exception {
        ProxyHandlerXForwardedForTestCase.setProxyHandler(false, false);
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/x-forwarded"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            Assert.assertEquals(ProxyHandlerXForwardedForTestCase.port, Integer.parseInt(result.getFirstHeader(X_FORWARDED_PORT.toString()).getValue()));
            Assert.assertEquals("http", result.getFirstHeader(X_FORWARDED_PROTO.toString()).getValue());
            Assert.assertEquals("localhost", result.getFirstHeader(X_FORWARDED_HOST.toString()).getValue());
            Assert.assertEquals(DefaultServer.getDefaultServerAddress().getAddress().getHostAddress(), result.getFirstHeader(X_FORWARDED_FOR.toString()).getValue());
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testXForwardedSsl() throws Exception {
        ProxyHandlerXForwardedForTestCase.setProxyHandler(false, false);
        TestHttpClient client = new TestHttpClient();
        try {
            client.setSSLContext(DefaultServer.getClientSSLContext());
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerSSLAddress()) + "/x-forwarded"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            Assert.assertEquals(ProxyHandlerXForwardedForTestCase.sslPort, Integer.parseInt(result.getFirstHeader(X_FORWARDED_PORT.toString()).getValue()));
            Assert.assertEquals("https", result.getFirstHeader(X_FORWARDED_PROTO.toString()).getValue());
            Assert.assertEquals("localhost", result.getFirstHeader(X_FORWARDED_HOST.toString()).getValue());
            Assert.assertEquals(DefaultServer.getDefaultServerAddress().getAddress().getHostAddress(), result.getFirstHeader(X_FORWARDED_FOR.toString()).getValue());
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testReuseXForwarded() throws Exception {
        ProxyHandlerXForwardedForTestCase.setProxyHandler(false, true);
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/x-forwarded"));
            get.addHeader(X_FORWARDED_FOR.toString(), "50.168.245.32");
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            Assert.assertEquals(ProxyHandlerXForwardedForTestCase.port, Integer.parseInt(result.getFirstHeader(X_FORWARDED_PORT.toString()).getValue()));
            Assert.assertEquals("http", result.getFirstHeader(X_FORWARDED_PROTO.toString()).getValue());
            Assert.assertEquals("localhost", result.getFirstHeader(X_FORWARDED_HOST.toString()).getValue());
            Assert.assertEquals(("50.168.245.32," + (DefaultServer.getDefaultServerAddress().getAddress().getHostAddress())), result.getFirstHeader(X_FORWARDED_FOR.toString()).getValue());
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testReqriteHostHeader() throws Exception {
        ProxyHandlerXForwardedForTestCase.setProxyHandler(true, false);
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/x-forwarded"));
            get.addHeader(X_FORWARDED_FOR.toString(), "50.168.245.32");
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            Assert.assertEquals(ProxyHandlerXForwardedForTestCase.port, Integer.parseInt(result.getFirstHeader(X_FORWARDED_PORT.toString()).getValue()));
            Assert.assertEquals("http", result.getFirstHeader(X_FORWARDED_PROTO.toString()).getValue());
            Assert.assertEquals(String.format("localhost:%d", ProxyHandlerXForwardedForTestCase.port), result.getFirstHeader(X_FORWARDED_HOST.toString()).getValue());
            Assert.assertEquals(DefaultServer.getDefaultServerAddress().getAddress().getHostAddress(), result.getFirstHeader(X_FORWARDED_FOR.toString()).getValue());
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    protected static final class XForwardedHandler implements HttpHandler {
        @Override
        public void handleRequest(final HttpServerExchange exchange) throws Exception {
            // Copy the X-Fowarded* headers into the response
            if (exchange.getRequestHeaders().contains(X_FORWARDED_FOR))
                exchange.getResponseHeaders().put(X_FORWARDED_FOR, exchange.getRequestHeaders().getFirst(X_FORWARDED_FOR));

            if (exchange.getRequestHeaders().contains(X_FORWARDED_PROTO))
                exchange.getResponseHeaders().put(X_FORWARDED_PROTO, exchange.getRequestHeaders().getFirst(X_FORWARDED_PROTO));

            if (exchange.getRequestHeaders().contains(X_FORWARDED_HOST))
                exchange.getResponseHeaders().put(X_FORWARDED_HOST, exchange.getRequestHeaders().getFirst(X_FORWARDED_HOST));

            if (exchange.getRequestHeaders().contains(X_FORWARDED_PORT))
                exchange.getResponseHeaders().put(X_FORWARDED_PORT, exchange.getRequestHeaders().getFirst(X_FORWARDED_PORT));

        }
    }
}

