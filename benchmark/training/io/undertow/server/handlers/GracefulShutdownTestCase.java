/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.server.handlers;


import StatusCodes.OK;
import StatusCodes.SERVICE_UNAVAILABLE;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class GracefulShutdownTestCase {
    static final AtomicReference<CountDownLatch> latch1 = new AtomicReference<>();

    static final AtomicReference<CountDownLatch> latch2 = new AtomicReference<>();

    private static GracefulShutdownHandler shutdown;

    @Test
    public void simpleGracefulShutdownTestCase() throws IOException, InterruptedException {
        HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/path"));
        TestHttpClient client = new TestHttpClient();
        try {
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            HttpClientUtils.readResponse(result);
            GracefulShutdownTestCase.shutdown.shutdown();
            result = client.execute(get);
            Assert.assertEquals(SERVICE_UNAVAILABLE, result.getStatusLine().getStatusCode());
            HttpClientUtils.readResponse(result);
            GracefulShutdownTestCase.shutdown.start();
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            HttpClientUtils.readResponse(result);
            CountDownLatch latch = new CountDownLatch(1);
            GracefulShutdownTestCase.latch2.set(latch);
            GracefulShutdownTestCase.latch1.set(new CountDownLatch(1));
            Thread t = new Thread(new GracefulShutdownTestCase.RequestTask());
            t.start();
            GracefulShutdownTestCase.latch1.get().await();
            GracefulShutdownTestCase.shutdown.shutdown();
            Assert.assertFalse(GracefulShutdownTestCase.shutdown.awaitShutdown(10));
            latch.countDown();
            Assert.assertTrue(GracefulShutdownTestCase.shutdown.awaitShutdown(10000));
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void gracefulShutdownListenerTestCase() throws IOException, InterruptedException {
        HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/path"));
        TestHttpClient client = new TestHttpClient();
        try {
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            HttpClientUtils.readResponse(result);
            GracefulShutdownTestCase.shutdown.shutdown();
            result = client.execute(get);
            Assert.assertEquals(SERVICE_UNAVAILABLE, result.getStatusLine().getStatusCode());
            HttpClientUtils.readResponse(result);
            GracefulShutdownTestCase.shutdown.start();
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            HttpClientUtils.readResponse(result);
            CountDownLatch latch = new CountDownLatch(1);
            GracefulShutdownTestCase.latch2.set(latch);
            GracefulShutdownTestCase.latch1.set(new CountDownLatch(1));
            Thread t = new Thread(new GracefulShutdownTestCase.RequestTask());
            t.start();
            GracefulShutdownTestCase.latch1.get().await();
            GracefulShutdownTestCase.ShutdownListener listener = new GracefulShutdownTestCase.ShutdownListener();
            GracefulShutdownTestCase.shutdown.shutdown();
            GracefulShutdownTestCase.shutdown.addShutdownListener(listener);
            Assert.assertFalse(listener.invoked);
            latch.countDown();
            long end = (System.currentTimeMillis()) + 5000;
            while ((!(listener.invoked)) && ((System.currentTimeMillis()) < end)) {
                Thread.sleep(10);
            } 
            Assert.assertTrue(listener.invoked);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    private class ShutdownListener implements GracefulShutdownHandler.ShutdownListener {
        private volatile boolean invoked = false;

        @Override
        public synchronized void shutdown(boolean successful) {
            invoked = true;
        }
    }

    private final class RequestTask implements Runnable {
        @Override
        public void run() {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/path"));
            TestHttpClient client = new TestHttpClient();
            try {
                HttpResponse result = client.execute(get);
                Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
                HttpClientUtils.readResponse(result);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                client.getConnectionManager().shutdown();
            }
        }
    }
}

