package org.mockserver.integration.proxy.socks;


import HttpStatusCode.OK_200;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.echo.http.EchoServer;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.mockserver.MockServer;
import org.mockserver.socket.tls.KeyStoreFactory;

import static java.net.Proxy.Type.SOCKS;


/**
 *
 *
 * @author jamesdbloom
 */
public class NettyHttpProxySOCKSIntegrationTest {
    private static final MockServerLogger MOCK_SERVER_LOGGER = new MockServerLogger(NettyHttpProxySOCKSIntegrationTest.class);

    private static Integer mockServerPort;

    private static EchoServer insecureEchoServer;

    private static EchoServer secureEchoServer;

    private static MockServer httpProxy;

    private static MockServerClient mockServerClient;

    @Test
    public void shouldProxyRequestsUsingHttpClientViaSOCKSConfiguredForJVM() throws Exception {
        Assume.assumeThat("SOCKS5 is broken in JRE <9", System.getProperty("java.version"), Matchers.not(Matchers.anyOf(Matchers.startsWith("1.7."), Matchers.equalTo("1.7"), Matchers.startsWith("7."), Matchers.equalTo("7"), Matchers.startsWith("1.8."), Matchers.equalTo("1.8"), Matchers.startsWith("8."), Matchers.equalTo("8"))));
        ProxySelector defaultProxySelector = ProxySelector.getDefault();
        try {
            // given - SOCKS proxy JVM settings
            ProxySelector.setDefault(new ProxySelector() {
                @Override
                public List<Proxy> select(URI uri) {
                    return Collections.singletonList(new Proxy(SOCKS, new InetSocketAddress(System.getProperty("http.proxyHost"), Integer.parseInt(System.getProperty("http.proxyPort")))));
                }

                @Override
                public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
                    System.out.println((("Connection could not be established to proxy at socket [" + sa) + "]"));
                    ioe.printStackTrace();
                }
            });
            // and - an HTTP client
            HttpClient httpClient = HttpClientBuilder.create().setSslcontext(KeyStoreFactory.keyStoreFactory().sslContext()).build();
            // when
            HttpResponse response = httpClient.execute(new HttpHost("127.0.0.1", NettyHttpProxySOCKSIntegrationTest.insecureEchoServer.getPort(), "http"), new HttpGet("/"));
            // then
            MatcherAssert.assertThat(response.getStatusLine().getStatusCode(), Is.is(200));
            NettyHttpProxySOCKSIntegrationTest.mockServerClient.verify(request().withHeader("Host", (("127.0.0.1" + ":") + (NettyHttpProxySOCKSIntegrationTest.insecureEchoServer.getPort()))));
        } finally {
            ProxySelector.setDefault(defaultProxySelector);
        }
    }

    @Test
    public void shouldProxyRequestsUsingHttpClientViaSOCKSConfiguredForJVMToSecureServerPort() throws Exception {
        Assume.assumeThat("SOCKS5 is broken in JRE <9", System.getProperty("java.version"), Matchers.not(Matchers.anyOf(Matchers.startsWith("1.7."), Matchers.equalTo("1.7"), Matchers.startsWith("7."), Matchers.equalTo("7"), Matchers.startsWith("1.8."), Matchers.equalTo("1.8"), Matchers.startsWith("8."), Matchers.equalTo("8"))));
        ProxySelector defaultProxySelector = ProxySelector.getDefault();
        try {
            // given - SOCKS proxy JVM settings
            ProxySelector.setDefault(new ProxySelector() {
                @Override
                public List<Proxy> select(URI uri) {
                    return Collections.singletonList(new Proxy(SOCKS, new InetSocketAddress(System.getProperty("http.proxyHost"), Integer.parseInt(System.getProperty("http.proxyPort")))));
                }

                @Override
                public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
                    System.out.println((("Connection could not be established to proxy at socket [" + sa) + "]"));
                    ioe.printStackTrace();
                }
            });
            // and - an HTTP client
            HttpClient httpClient = HttpClientBuilder.create().setSslcontext(KeyStoreFactory.keyStoreFactory().sslContext()).build();
            // when
            HttpResponse response = httpClient.execute(new HttpHost("127.0.0.1", NettyHttpProxySOCKSIntegrationTest.secureEchoServer.getPort(), "https"), new HttpGet("/"));
            // then
            MatcherAssert.assertThat(response.getStatusLine().getStatusCode(), Is.is(200));
            NettyHttpProxySOCKSIntegrationTest.mockServerClient.verify(request().withHeader("Host", (("127.0.0.1" + ":") + (NettyHttpProxySOCKSIntegrationTest.secureEchoServer.getPort()))));
        } finally {
            ProxySelector.setDefault(defaultProxySelector);
        }
    }

    @Test
    public void shouldProxyRequestsUsingHttpClientViaSOCKS() throws Exception {
        Assume.assumeThat("SOCKS5 is broken in JRE <9", System.getProperty("java.version"), Matchers.not(Matchers.anyOf(Matchers.startsWith("1.7."), Matchers.equalTo("1.7"), Matchers.startsWith("7."), Matchers.equalTo("7"), Matchers.startsWith("1.8."), Matchers.equalTo("1.8"), Matchers.startsWith("8."), Matchers.equalTo("8"))));
        // given
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", new ConnectionSocketFactory() {
            public Socket createSocket(final HttpContext context) throws IOException {
                return new Socket(new Proxy(SOCKS, new InetSocketAddress(System.getProperty("http.proxyHost"), Integer.parseInt(System.getProperty("http.proxyPort")))));
            }

            public Socket connectSocket(final int connectTimeout, final Socket socket, final HttpHost host, final InetSocketAddress remoteAddress, final InetSocketAddress localAddress, final HttpContext context) throws IOException {
                Socket sock;
                if (socket != null) {
                    sock = socket;
                } else {
                    sock = createSocket(context);
                }
                if (localAddress != null) {
                    sock.bind(localAddress);
                }
                try {
                    sock.connect(remoteAddress, connectTimeout);
                } catch (SocketTimeoutException ex) {
                    throw new ConnectTimeoutException(ex, host, remoteAddress.getAddress());
                }
                return sock;
            }
        }).build();
        PoolingHttpClientConnectionManager clientConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        HttpClient httpClient = HttpClients.custom().setConnectionManager(clientConnectionManager).build();
        // when
        HttpPost request = new HttpPost(new URIBuilder().setScheme("http").setHost("localhost").setPort(NettyHttpProxySOCKSIntegrationTest.insecureEchoServer.getPort()).setPath("/test_headers_and_body").build());
        request.setEntity(new StringEntity("an_example_body"));
        HttpResponse response = httpClient.execute(request);
        // then
        Assert.assertEquals(OK_200.code(), response.getStatusLine().getStatusCode());
        Assert.assertEquals("an_example_body", new String(EntityUtils.toByteArray(response.getEntity()), StandardCharsets.UTF_8));
        // and
        NettyHttpProxySOCKSIntegrationTest.mockServerClient.verify(request().withPath("/test_headers_and_body").withBody("an_example_body"), exactly(1));
    }

    @Test
    public void shouldProxyRequestsUsingHttpClientViaSOCKSToSecureServerPort() throws Exception {
        Assume.assumeThat("SOCKS5 is broken in JRE <9", System.getProperty("java.version"), Matchers.not(Matchers.anyOf(Matchers.startsWith("1.7."), Matchers.equalTo("1.7"), Matchers.startsWith("7."), Matchers.equalTo("7"), Matchers.startsWith("1.8."), Matchers.equalTo("1.8"), Matchers.startsWith("8."), Matchers.equalTo("8"))));
        // given
        Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create().register("https", new ConnectionSocketFactory() {
            public Socket createSocket(final HttpContext context) throws IOException {
                return new Socket(new Proxy(SOCKS, new InetSocketAddress(System.getProperty("http.proxyHost"), Integer.parseInt(System.getProperty("http.proxyPort")))));
            }

            public Socket connectSocket(final int connectTimeout, final Socket socket, final HttpHost host, final InetSocketAddress remoteAddress, final InetSocketAddress localAddress, final HttpContext context) throws IOException {
                Socket sock;
                if (socket != null) {
                    sock = socket;
                } else {
                    sock = createSocket(context);
                }
                if (localAddress != null) {
                    sock.bind(localAddress);
                }
                try {
                    sock.connect(remoteAddress, connectTimeout);
                } catch (SocketTimeoutException ex) {
                    throw new ConnectTimeoutException(ex, host, remoteAddress.getAddress());
                }
                return sslSocketFactory().wrapSocket(sock);
            }
        }).build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(reg);
        HttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
        // when
        HttpPost request = new HttpPost(new URIBuilder().setScheme("https").setHost("localhost").setPort(NettyHttpProxySOCKSIntegrationTest.secureEchoServer.getPort()).setPath("/test_headers_and_body").build());
        request.setEntity(new StringEntity("an_example_body"));
        request.addHeader("Secure", "true");
        HttpResponse response = httpClient.execute(request);
        // then
        Assert.assertEquals(OK_200.code(), response.getStatusLine().getStatusCode());
        Assert.assertEquals("an_example_body", new String(EntityUtils.toByteArray(response.getEntity()), StandardCharsets.UTF_8));
        // and
        NettyHttpProxySOCKSIntegrationTest.mockServerClient.verify(request().withPath("/test_headers_and_body").withBody("an_example_body"), exactly(1));
    }

    @Test
    public void shouldProxyRequestsUsingRawSocketViaSOCKS() throws Exception {
        proxyRequestsUsingRawSocketViaSOCKS(false);
    }

    @Test
    public void shouldProxyRequestsUsingRawSecureSocketViaSOCKSToSecureServerPort() throws Exception {
        Assume.assumeThat("SOCKS5 is broken in JRE <9", System.getProperty("java.version"), Matchers.not(Matchers.anyOf(Matchers.startsWith("1.7."), Matchers.equalTo("1.7"), Matchers.startsWith("7."), Matchers.equalTo("7"), Matchers.startsWith("1.8."), Matchers.equalTo("1.8"), Matchers.startsWith("8."), Matchers.equalTo("8"))));
        proxyRequestsUsingRawSocketViaSOCKS(true);
    }
}

