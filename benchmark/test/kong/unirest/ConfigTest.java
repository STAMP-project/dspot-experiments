/**
 * The MIT License
 *
 * Copyright for portions of unirest-java are held by Kong Inc (c) 2013.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package kong.unirest;


import Config.DEFAULT_CONNECT_TIMEOUT;
import Config.DEFAULT_MAX_CONNECTIONS;
import Config.DEFAULT_MAX_PER_ROUTE;
import Config.DEFAULT_SOCKET_TIMEOUT;
import java.io.IOException;
import kong.unirest.apache.AsyncIdleConnectionMonitorThread;
import kong.unirest.apache.SyncIdleConnectionMonitorThread;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.nio.client.HttpAsyncClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ConfigTest {
    @Mock
    private CloseableHttpClient httpc;

    @Mock
    private PoolingHttpClientConnectionManager clientManager;

    @Mock
    private SyncIdleConnectionMonitorThread connMonitor;

    @Mock
    private CloseableHttpAsyncClient asyncClient;

    @Mock
    private AsyncIdleConnectionMonitorThread asyncMonitor;

    @Mock
    private PoolingNHttpClientConnectionManager manager;

    @InjectMocks
    private Config config;

    @Test
    public void shouldKeepConnectionTimeOutDefault() {
        Assert.assertEquals(DEFAULT_CONNECT_TIMEOUT, config.getConnectionTimeout());
    }

    @Test
    public void shouldKeepSocketTimeoutDefault() {
        Assert.assertEquals(DEFAULT_SOCKET_TIMEOUT, config.getSocketTimeout());
    }

    @Test
    public void shouldKeepMaxTotalDefault() {
        Assert.assertEquals(DEFAULT_MAX_CONNECTIONS, config.getMaxConnections());
    }

    @Test
    public void shouldKeepMaxPerRouteDefault() {
        Assert.assertEquals(DEFAULT_MAX_PER_ROUTE, config.getMaxPerRoutes());
    }

    @Test
    public void onceTheConfigIsRunningYouCannotChangeConfig() {
        config.httpClient(Mockito.mock(HttpClient.class));
        config.asyncClient(Mockito.mock(HttpAsyncClient.class));
        TestUtil.assertException(() -> config.socketTimeout(533), UnirestConfigException.class, ("Http Clients are already built in order to build a new config execute Unirest.config().reset() " + ("before changing settings. \n" + "This should be done rarely.")));
        Unirest.shutDown();
    }

    @Test
    public void willNotRebuildIfNotClosableAsyncClient() {
        HttpAsyncClient c = Mockito.mock(HttpAsyncClient.class);
        config.asyncClient(c);
        Assert.assertSame(c, config.getAsyncClient().getClient());
        Assert.assertSame(c, config.getAsyncClient().getClient());
    }

    @Test
    public void willRebuildIfEmpty() {
        Assert.assertSame(config.getAsyncClient(), config.getAsyncClient());
    }

    @Test
    public void willRebuildIfClosableAndStopped() {
        CloseableHttpAsyncClient c = Mockito.mock(CloseableHttpAsyncClient.class);
        Mockito.when(c.isRunning()).thenReturn(false);
        config.asyncClient(c);
        Assert.assertNotSame(c, config.getAsyncClient());
    }

    @Test
    public void testShutdown() throws IOException {
        Mockito.when(asyncClient.isRunning()).thenReturn(true);
        Unirest.config().httpClient(new kong.unirest.apache.ApacheClient(httpc, null, clientManager, connMonitor)).asyncClient(new kong.unirest.apache.ApacheAsyncClient(asyncClient, null, manager, asyncMonitor));
        Unirest.shutDown();
        Mockito.verify(httpc).close();
        Mockito.verify(clientManager).close();
        Mockito.verify(connMonitor).interrupt();
        Mockito.verify(asyncClient).close();
        Mockito.verify(asyncMonitor).interrupt();
    }

    @Test
    public void willPowerThroughErrors() throws IOException {
        Mockito.when(asyncClient.isRunning()).thenReturn(true);
        Mockito.doThrow(new IOException("1")).when(httpc).close();
        Mockito.doThrow(new RuntimeException("2")).when(clientManager).close();
        Mockito.doThrow(new RuntimeException("3")).when(connMonitor).interrupt();
        Mockito.doThrow(new IOException("4")).when(asyncClient).close();
        Mockito.doThrow(new RuntimeException("5")).when(asyncMonitor).interrupt();
        Unirest.config().httpClient(new kong.unirest.apache.ApacheClient(httpc, null, clientManager, connMonitor)).asyncClient(new kong.unirest.apache.ApacheAsyncClient(asyncClient, null, manager, asyncMonitor));
        TestUtil.assertException(Unirest::shutDown, UnirestException.class, ("java.io.IOException 1\n" + ((("java.lang.RuntimeException 2\n" + "java.lang.RuntimeException 3\n") + "java.io.IOException 4\n") + "java.lang.RuntimeException 5")));
        Mockito.verify(httpc).close();
        Mockito.verify(clientManager).close();
        Mockito.verify(connMonitor).interrupt();
        Mockito.verify(asyncClient).close();
        Mockito.verify(asyncMonitor).interrupt();
    }

    @Test
    public void doesNotBombOnNullOptions() throws IOException {
        Mockito.when(asyncClient.isRunning()).thenReturn(true);
        Unirest.config().httpClient(new kong.unirest.apache.ApacheClient(httpc, null, null, null)).asyncClient(new kong.unirest.apache.ApacheAsyncClient(asyncClient, null, null, null));
        Unirest.shutDown();
        Mockito.verify(httpc).close();
        Mockito.verify(asyncClient).close();
    }

    @Test
    public void ifTheNextAsyncClientThatIsReturnedIsAlsoOffThrowAnException() {
        AsyncClient c = Mockito.mock(AsyncClient.class);
        Mockito.when(c.isRunning()).thenReturn(false);
        config.asyncClient(( g) -> c);
        TestUtil.assertException(() -> config.getAsyncClient(), UnirestConfigException.class, "Attempted to get a new async client but it was not started. Please ensure it is");
    }

    @Test
    public void willNotRebuildIfRunning() {
        CloseableHttpAsyncClient c = Mockito.mock(CloseableHttpAsyncClient.class);
        Mockito.when(c.isRunning()).thenReturn(true);
        config.asyncClient(c);
        Assert.assertSame(c, config.getAsyncClient().getClient());
    }

    @Test
    public void provideYourOwnClientBuilder() {
        Client cli = Mockito.mock(Client.class);
        config.httpClient(( c) -> cli);
        Assert.assertSame(cli, config.getClient());
    }

    @Test
    public void canDisableGZipencoding() {
        Assert.assertTrue(config.isRequestCompressionOn());
        config.requestCompression(false);
        Assert.assertFalse(config.isRequestCompressionOn());
    }

    @Test
    public void canDisableAuthRetry() {
        Assert.assertTrue(config.isAutomaticRetries());
        config.automaticRetries(false);
        Assert.assertFalse(config.isAutomaticRetries());
    }

    @Test
    public void provideYourOwnAsyncClientBuilder() {
        AsyncClient cli = Mockito.mock(AsyncClient.class);
        Mockito.when(cli.isRunning()).thenReturn(true);
        config.asyncClient(( c) -> cli);
        Assert.assertSame(cli, config.getAsyncClient());
    }

    @Test
    public void canSetProxyViaSetter() {
        config.proxy(new Proxy("localhost", 8080, "ryan", "password"));
        assertProxy("localhost", 8080, "ryan", "password");
        config.proxy("local2", 8888);
        assertProxy("local2", 8888, null, null);
        config.proxy("local3", 7777, "barb", "12345");
        assertProxy("local3", 7777, "barb", "12345");
    }
}

