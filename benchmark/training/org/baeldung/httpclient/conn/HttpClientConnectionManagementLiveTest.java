package org.baeldung.httpclient.conn;


import HttpCoreContext.HTTP_TARGET_HOST;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;


public class HttpClientConnectionManagementLiveTest {
    private static final String SERVER1 = "http://www.petrikainulainen.net/";

    private static final String SERVER7 = "http://www.baeldung.com/";

    private BasicHttpClientConnectionManager basicConnManager;

    private PoolingHttpClientConnectionManager poolingConnManager;

    private HttpClientContext context;

    private HttpRoute route;

    private HttpClientConnection conn1;

    private HttpClientConnection conn;

    private HttpClientConnection conn2;

    private CloseableHttpResponse response;

    private HttpGet get1;

    private HttpGet get2;

    private CloseableHttpClient client;

    // 2
    // 2.1 IN ARTCLE
    @Test
    public final void whenLowLevelConnectionIsEstablished_thenNoExceptions() throws IOException, InterruptedException, ExecutionException, HttpException {
        basicConnManager = new BasicHttpClientConnectionManager();
        final ConnectionRequest connRequest = basicConnManager.requestConnection(route, null);
        Assert.assertTrue(((connRequest.get(1000, TimeUnit.SECONDS)) != null));
    }

    // @Ignore
    // 2.2 IN ARTICLE
    @Test
    public final void whenOpeningLowLevelConnectionWithSocketTimeout_thenNoExceptions() throws IOException, InterruptedException, ExecutionException, HttpException {
        basicConnManager = new BasicHttpClientConnectionManager();
        context = HttpClientContext.create();
        final ConnectionRequest connRequest = basicConnManager.requestConnection(route, null);
        conn = connRequest.get(1000, TimeUnit.SECONDS);
        if (!(conn.isOpen())) {
            basicConnManager.connect(conn, route, 1000, context);
        }
        conn.setSocketTimeout(30000);
        Assert.assertTrue(((conn.getSocketTimeout()) == 30000));
        Assert.assertTrue(conn.isOpen());
    }

    // 3
    // Example 3.1.
    @Test
    public final void whenPollingConnectionManagerIsConfiguredOnHttpClient_thenNoExceptions() throws IOException, InterruptedException, ClientProtocolException {
        poolingConnManager = new PoolingHttpClientConnectionManager();
        client = HttpClients.custom().setConnectionManager(poolingConnManager).build();
        client.execute(get1);
        Assert.assertTrue(((poolingConnManager.getTotalStats().getLeased()) == 1));
    }

    // @Ignore
    // Example 3.2. TESTER VERSION
    /* tester */
    @Test
    public final void whenTwoConnectionsForTwoRequests_thenTwoConnectionsAreLeased() throws InterruptedException {
        poolingConnManager = new PoolingHttpClientConnectionManager();
        final CloseableHttpClient client1 = HttpClients.custom().setConnectionManager(poolingConnManager).build();
        final CloseableHttpClient client2 = HttpClients.custom().setConnectionManager(poolingConnManager).build();
        final TesterVersion_MultiHttpClientConnThread thread1 = new TesterVersion_MultiHttpClientConnThread(client1, get1, poolingConnManager);
        final TesterVersion_MultiHttpClientConnThread thread2 = new TesterVersion_MultiHttpClientConnThread(client2, get2, poolingConnManager);
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join(1000);
        Assert.assertTrue(((poolingConnManager.getTotalStats().getLeased()) == 2));
    }

    // @Ignore
    // Example 3.2. ARTICLE VERSION
    @Test
    public final void whenTwoConnectionsForTwoRequests_thenNoExceptions() throws InterruptedException {
        poolingConnManager = new PoolingHttpClientConnectionManager();
        final CloseableHttpClient client1 = HttpClients.custom().setConnectionManager(poolingConnManager).build();
        final CloseableHttpClient client2 = HttpClients.custom().setConnectionManager(poolingConnManager).build();
        final MultiHttpClientConnThread thread1 = new MultiHttpClientConnThread(client1, get1);
        final MultiHttpClientConnThread thread2 = new MultiHttpClientConnThread(client2, get2);
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
    }

    // 4
    // Example 4.1
    @Test
    public final void whenIncreasingConnectionPool_thenNoEceptions() {
        poolingConnManager = new PoolingHttpClientConnectionManager();
        poolingConnManager.setMaxTotal(5);
        poolingConnManager.setDefaultMaxPerRoute(4);
        final HttpHost localhost = new HttpHost("locahost", 80);
        poolingConnManager.setMaxPerRoute(new HttpRoute(localhost), 5);
    }

    // @Ignore
    // 4.2 Tester Version
    /* tester */
    @Test
    public final void whenExecutingSameRequestsInDifferentThreads_thenUseDefaultConnLimit() throws IOException, InterruptedException {
        poolingConnManager = new PoolingHttpClientConnectionManager();
        client = HttpClients.custom().setConnectionManager(poolingConnManager).build();
        final TesterVersion_MultiHttpClientConnThread thread1 = new TesterVersion_MultiHttpClientConnThread(client, new HttpGet("http://www.google.com"), poolingConnManager);
        final TesterVersion_MultiHttpClientConnThread thread2 = new TesterVersion_MultiHttpClientConnThread(client, new HttpGet("http://www.google.com"), poolingConnManager);
        final TesterVersion_MultiHttpClientConnThread thread3 = new TesterVersion_MultiHttpClientConnThread(client, new HttpGet("http://www.google.com"), poolingConnManager);
        thread1.start();
        thread2.start();
        thread3.start();
        thread1.join(10000);
        thread2.join(10000);
        thread3.join(10000);
    }

    // 4.2 Article version
    @Test
    public final void whenExecutingSameRequestsInDifferentThreads_thenExecuteReuqest() throws InterruptedException {
        final HttpGet get = new HttpGet("http://www.google.com");
        poolingConnManager = new PoolingHttpClientConnectionManager();
        client = HttpClients.custom().setConnectionManager(poolingConnManager).build();
        final MultiHttpClientConnThread thread1 = new MultiHttpClientConnThread(client, get);
        final MultiHttpClientConnThread thread2 = new MultiHttpClientConnThread(client, get);
        final MultiHttpClientConnThread thread3 = new MultiHttpClientConnThread(client, get);
        thread1.start();
        thread2.start();
        thread3.start();
        thread1.join();
        thread2.join();
        thread3.join();
    }

    // 5
    // @Ignore
    // 5.1
    @Test
    public final void whenCustomizingKeepAliveStrategy_thenNoExceptions() throws IOException, ClientProtocolException {
        final ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(final HttpResponse myResponse, final HttpContext myContext) {
                final HeaderElementIterator it = new BasicHeaderElementIterator(myResponse.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while (it.hasNext()) {
                    final HeaderElement he = it.nextElement();
                    final String param = he.getName();
                    final String value = he.getValue();
                    if ((value != null) && (param.equalsIgnoreCase("timeout"))) {
                        return (Long.parseLong(value)) * 1000;
                    }
                } 
                final HttpHost target = ((HttpHost) (myContext.getAttribute(HTTP_TARGET_HOST)));
                if ("localhost".equalsIgnoreCase(target.getHostName())) {
                    return 10 * 1000;
                } else {
                    return 5 * 1000;
                }
            }
        };
        client = HttpClients.custom().setKeepAliveStrategy(myStrategy).setConnectionManager(poolingConnManager).build();
        client.execute(get1);
        client.execute(get2);
    }

    // 6
    // @Ignore
    // 6.1
    @Test
    public final void givenBasicHttpClientConnManager_whenConnectionReuse_thenNoExceptions() throws IOException, InterruptedException, ExecutionException, HttpException {
        basicConnManager = new BasicHttpClientConnectionManager();
        context = HttpClientContext.create();
        final ConnectionRequest connRequest = basicConnManager.requestConnection(route, null);
        conn = connRequest.get(10, TimeUnit.SECONDS);
        basicConnManager.connect(conn, route, 1000, context);
        basicConnManager.routeComplete(conn, route, context);
        final HttpRequestExecutor exeRequest = new HttpRequestExecutor();
        context.setTargetHost(new HttpHost("http://httpbin.org", 80));
        final HttpGet get = new HttpGet("http://httpbin.org");
        exeRequest.execute(get, conn, context);
        conn.isResponseAvailable(1000);
        basicConnManager.releaseConnection(conn, null, 1, TimeUnit.SECONDS);
        // 
        client = HttpClients.custom().setConnectionManager(basicConnManager).build();
        client.execute(get);
    }

    // @Ignore
    // 6.2 TESTER VERSION
    /* tester */
    @Test
    public final void whenConnectionsNeededGreaterThanMaxTotal_thenReuseConnections() throws InterruptedException {
        poolingConnManager = new PoolingHttpClientConnectionManager();
        poolingConnManager.setDefaultMaxPerRoute(5);
        poolingConnManager.setMaxTotal(5);
        client = HttpClients.custom().setConnectionManager(poolingConnManager).build();
        final MultiHttpClientConnThread[] threads = new MultiHttpClientConnThread[10];
        int countConnMade = 0;
        for (int i = 0; i < (threads.length); i++) {
            threads[i] = new MultiHttpClientConnThread(client, get1, poolingConnManager);
        }
        for (final MultiHttpClientConnThread thread : threads) {
            thread.start();
        }
        for (final MultiHttpClientConnThread thread : threads) {
            thread.join(10000);
            countConnMade++;
            if (countConnMade == 0) {
                Assert.assertTrue(((thread.getLeasedConn()) == 5));
            }
        }
    }

    // 6.2 ARTICLE VERSION
    // @Ignore
    @Test
    public final void whenConnectionsNeededGreaterThanMaxTotal_thenLeaseMasTotalandReuse() throws InterruptedException {
        final HttpGet get = new HttpGet("http://echo.200please.com");
        poolingConnManager = new PoolingHttpClientConnectionManager();
        poolingConnManager.setDefaultMaxPerRoute(5);
        poolingConnManager.setMaxTotal(5);
        client = HttpClients.custom().setConnectionManager(poolingConnManager).build();
        final MultiHttpClientConnThread[] threads = new MultiHttpClientConnThread[10];
        for (int i = 0; i < (threads.length); i++) {
            threads[i] = new MultiHttpClientConnThread(client, get, poolingConnManager);
        }
        for (final MultiHttpClientConnThread thread : threads) {
            thread.start();
        }
        for (final MultiHttpClientConnThread thread : threads) {
            thread.join(10000);
        }
    }

    // 7
    // 7.1
    @Test
    public final void whenConfiguringTimeOut_thenNoExceptions() {
        route = new HttpRoute(new HttpHost("localhost", 80));
        poolingConnManager = new PoolingHttpClientConnectionManager();
        poolingConnManager.setSocketConfig(route.getTargetHost(), SocketConfig.custom().setSoTimeout(5000).build());
        Assert.assertTrue(((poolingConnManager.getSocketConfig(route.getTargetHost()).getSoTimeout()) == 5000));
    }

    // 8
    // @Ignore
    // 8.1
    @Test
    public final void whenHttpClientChecksStaleConns_thenNoExceptions() {
        poolingConnManager = new PoolingHttpClientConnectionManager();
        client = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setStaleConnectionCheckEnabled(true).build()).setConnectionManager(poolingConnManager).build();
    }

    // @Ignore
    // 8.2 ARTICLE VERSION
    @Test
    public final void whenCustomizedIdleConnMonitor_thenNoExceptions() throws IOException, InterruptedException {
        new HttpGet("http://google.com");
        poolingConnManager = new PoolingHttpClientConnectionManager();
        client = HttpClients.custom().setConnectionManager(poolingConnManager).build();
        final IdleConnectionMonitorThread staleMonitor = new IdleConnectionMonitorThread(poolingConnManager);
        staleMonitor.start();
        staleMonitor.join(1000);
    }

    // 9
    // @Ignore
    // 9.1
    @Test(expected = IllegalStateException.class)
    public final void whenClosingConnectionsandManager_thenCloseWithNoExceptions1() throws IOException, InterruptedException, ExecutionException, HttpException {
        poolingConnManager = new PoolingHttpClientConnectionManager();
        client = HttpClients.custom().setConnectionManager(poolingConnManager).build();
        final HttpGet get = new HttpGet("http://google.com");
        response = client.execute(get);
        EntityUtils.consume(response.getEntity());
        response.close();
        client.close();
        poolingConnManager.close();
        poolingConnManager.shutdown();
        client.execute(get);
        Assert.assertTrue(((response.getEntity()) == null));
    }
}

