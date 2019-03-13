package io.undertow.server.handlers.proxy;


import StatusCodes.OK;
import io.undertow.Undertow;
import io.undertow.server.ServerConnection;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.ProxyIgnore;
import io.undertow.testutils.TestHttpClient;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(DefaultServer.class)
@ProxyIgnore
public class LoadBalancerConnectionPoolingTestCase {
    public static final int TTL = 2000;

    private static Undertow undertow;

    private static final Set<ServerConnection> activeConnections = Collections.newSetFromMap(new ConcurrentHashMap<>());

    static final String host = DefaultServer.getHostAddress("default");

    static int port = DefaultServer.getHostPort("default");

    @Test
    public void shouldReduceConnectionPool() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        PoolingClientConnectionManager conman = new PoolingClientConnectionManager();
        conman.setDefaultMaxPerRoute(20);
        final TestHttpClient client = new TestHttpClient(conman);
        int requests = 20;
        final CountDownLatch latch = new CountDownLatch(requests);
        long ttlStartExpire = (LoadBalancerConnectionPoolingTestCase.TTL) + (System.currentTimeMillis());
        try {
            for (int i = 0; i < requests; ++i) {
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        HttpGet get = new HttpGet(((("http://" + (LoadBalancerConnectionPoolingTestCase.host)) + ":") + ((LoadBalancerConnectionPoolingTestCase.port) + 1)));
                        try {
                            HttpResponse response = client.execute(get);
                            Assert.assertEquals(OK, response.getStatusLine().getStatusCode());
                            HttpClientUtils.readResponse(response);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } finally {
                            latch.countDown();
                        }
                    }
                });
            }
            if (!(latch.await(2000, TimeUnit.MILLISECONDS))) {
                Assert.fail();
            }
        } finally {
            client.getConnectionManager().shutdown();
            executorService.shutdown();
        }
        if ((LoadBalancerConnectionPoolingTestCase.activeConnections.size()) != 1) {
            // if the test is slow this line could be hit after the expire time
            // uncommon, but we guard against it to prevent intermittent failures
            if ((System.currentTimeMillis()) < ttlStartExpire) {
                Assert.fail("there should still be a connection");
            }
        }
        long end = (System.currentTimeMillis()) + ((LoadBalancerConnectionPoolingTestCase.TTL) * 3);
        while ((!(LoadBalancerConnectionPoolingTestCase.activeConnections.isEmpty())) && ((System.currentTimeMillis()) < end)) {
            Thread.sleep(100);
        } 
        Assert.assertEquals(0, LoadBalancerConnectionPoolingTestCase.activeConnections.size());
    }
}

