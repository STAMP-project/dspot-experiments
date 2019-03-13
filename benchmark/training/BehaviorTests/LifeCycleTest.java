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
package BehaviorTests;


import com.github.paweladamski.httpclientmock.HttpClientMock;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import kong.unirest.apache.AsyncIdleConnectionMonitorThread;
import kong.unirest.apache.SyncIdleConnectionMonitorThread;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class LifeCycleTest extends BddTest {
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

    @Test
    public void settingACustomClient() {
        HttpClientMock httpClientMock = new HttpClientMock();
        httpClientMock.onGet("http://localhost/getme").doReturn(202, "Howdy Ho!");
        Unirest.config().httpClient(httpClientMock);
        HttpResponse<String> result = Unirest.get("http://localhost/getme").asString();
        Assert.assertEquals(202, result.getStatus());
        Assert.assertEquals("Howdy Ho!", result.getBody());
    }

    @Test
    public void settingClientAfterClientHasAlreadyBeenSet() {
        HttpClientMock httpClientMock = new HttpClientMock();
        httpClientMock.onGet("http://localhost/getme").doReturn(202, "Howdy Ho!");
        Assert.assertEquals(200, Unirest.get(MockServer.GET).asString().getStatus());
        Unirest.config().httpClient(httpClientMock);
        HttpResponse<String> result = Unirest.get("http://localhost/getme").asString();
        Assert.assertEquals(202, result.getStatus());
        Assert.assertEquals("Howdy Ho!", result.getBody());
    }

    @Test
    public void willNotShutdownInactiveAsyncClient() throws IOException {
        CloseableHttpAsyncClient asyncClient = Mockito.mock(CloseableHttpAsyncClient.class);
        Mockito.when(asyncClient.isRunning()).thenReturn(false);
        Unirest.config().asyncClient(asyncClient);
        Unirest.shutDown();
        Mockito.verify(asyncClient, Mockito.never()).close();
    }

    @Test
    public void canDetectIfSystemIsRunning() {
        Unirest.get(MockServer.GET).asEmpty();
        Assert.assertTrue(Unirest.isRunning());
        Unirest.shutDown();
        Assert.assertFalse(Unirest.isRunning());
        Unirest.get(MockServer.GET).asEmpty();
        Assert.assertTrue(Unirest.isRunning());
    }

    @Test
    public void willReinitIfLibraryIsUsedAfterShutdown() {
        Unirest.shutDown();
        Assert.assertFalse(Unirest.isRunning());
        Unirest.get(MockServer.GET).asEmpty();
        Assert.assertTrue(Unirest.isRunning());
    }

    @Test
    public void canGetTheCommonInstanceOfUnirest() {
        Assert.assertSame(Unirest.primaryInstance(), Unirest.primaryInstance());
        Assert.assertNotSame(Unirest.primaryInstance(), Unirest.spawnInstance());
        Assert.assertNotSame(Unirest.spawnInstance(), Unirest.spawnInstance());
    }

    @Test
    public void shouldReuseThreadPool() {
        int startingCount = ManagementFactory.getThreadMXBean().getThreadCount();
        IntStream.range(0, 100).forEach(( i) -> {
            Unirest.config().reset().getClient();
            Unirest.config().getAsyncClient();
        });
        Assert.assertThat(ManagementFactory.getThreadMXBean().getThreadCount(), CoreMatchers.is(Matchers.lessThan((startingCount + 10))));
    }

    @Test
    public void testUnirestInstanceIsShutdownWhenClosed() {
        UnirestInstance reference;
        try (UnirestInstance instance = new UnirestInstance(new Config().setDefaultHeader("foo", "bar"))) {
            reference = instance;
            Assert.assertEquals(1, reference.config().getDefaultHeaders().size());
            Assert.assertEquals("bar", reference.config().getDefaultHeaders().get("foo").get(0));
        }
        Assert.assertEquals(0, reference.config().getDefaultHeaders().size());
    }
}

