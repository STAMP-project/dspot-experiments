/**
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.it.server;


import SleepService.Iface;
import com.linecorp.armeria.common.ClosedSessionException;
import com.linecorp.armeria.common.RequestContext;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.thrift.THttpService;
import com.linecorp.armeria.service.test.thrift.main.SleepService;
import com.linecorp.armeria.service.test.thrift.main.SleepService.AsyncIface;
import com.linecorp.armeria.testing.server.ServerRule;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


public class GracefulShutdownIntegrationTest {
    @ClassRule
    public static final ServerRule server = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            sb.gracefulShutdownTimeout(1000L, 2000L);
            sb.defaultRequestTimeoutMillis(0);// Disable RequestTimeoutException.

            sb.service("/sleep", THttpService.of(((AsyncIface) (( milliseconds, resultHandler) -> RequestContext.current().eventLoop().schedule(() -> resultHandler.onComplete(milliseconds), milliseconds, MILLISECONDS)))));
        }
    };

    private static long baselineNanos;

    @Test(timeout = 20000L)
    public void testBaseline() throws Exception {
        final long baselineNanos = GracefulShutdownIntegrationTest.baselineNanos();
        // Measure the time taken for stopping the server after handling a single request.
        GracefulShutdownIntegrationTest.server.start();
        final SleepService.Iface client = GracefulShutdownIntegrationTest.newClient();
        client.sleep(0);
        final long startTime = System.nanoTime();
        GracefulShutdownIntegrationTest.server.stop().join();
        final long stopTime = System.nanoTime();
        // .. which should be on par with the baseline.
        assertThat((stopTime - startTime)).isBetween((baselineNanos - (TimeUnit.MILLISECONDS.toNanos(400))), (baselineNanos + (TimeUnit.MILLISECONDS.toNanos(400))));
    }

    @Test(timeout = 20000L)
    public void waitsForRequestToComplete() throws Exception {
        final long baselineNanos = GracefulShutdownIntegrationTest.baselineNanos();
        GracefulShutdownIntegrationTest.server.start();
        final SleepService.Iface client = GracefulShutdownIntegrationTest.newClient();
        final AtomicBoolean completed = new AtomicBoolean(false);
        final CountDownLatch latch = new CountDownLatch(1);
        CompletableFuture.runAsync(() -> {
            try {
                latch.countDown();
                client.sleep(500L);
                completed.set(true);
            } catch (Throwable t) {
                Assert.fail(("Shouldn't happen: " + t));
            }
        });
        // Wait for the latch to make sure the request has been sent before shutting down.
        latch.await();
        final long startTime = System.nanoTime();
        GracefulShutdownIntegrationTest.server.stop().join();
        final long stopTime = System.nanoTime();
        Assert.assertTrue(completed.get());
        // Should take 500 more milliseconds than the baseline.
        assertThat((stopTime - startTime)).isBetween((baselineNanos + (TimeUnit.MILLISECONDS.toNanos(100))), (baselineNanos + (TimeUnit.MILLISECONDS.toNanos(900))));
    }

    @Test(timeout = 20000L)
    public void interruptsSlowRequests() throws Exception {
        final long baselineNanos = GracefulShutdownIntegrationTest.baselineNanos();
        GracefulShutdownIntegrationTest.server.start();
        final SleepService.Iface client = GracefulShutdownIntegrationTest.newClient();
        final AtomicBoolean completed = new AtomicBoolean(false);
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        CompletableFuture.runAsync(() -> {
            try {
                latch1.countDown();
                client.sleep(30000L);
                completed.set(true);
            } catch (ClosedSessionException expected) {
                latch2.countDown();
            } catch (Throwable t) {
                Assert.fail(("Shouldn't happen: " + t));
            }
        });
        // Wait for the latch to make sure the request has been sent before shutting down.
        latch1.await();
        final long startTime = System.nanoTime();
        GracefulShutdownIntegrationTest.server.stop().join();
        Assert.assertFalse(completed.get());
        // 'client.sleep()' must fail immediately when the server closes the connection.
        latch2.await();
        // Should take 1 more second than the baseline, because the long sleep will trigger shutdown timeout.
        final long stopTime = System.nanoTime();
        assertThat((stopTime - startTime)).isBetween((baselineNanos + (TimeUnit.MILLISECONDS.toNanos(600))), (baselineNanos + (TimeUnit.MILLISECONDS.toNanos(1400))));
    }

    @Test(timeout = 20000)
    public void testHardTimeout() throws Exception {
        final long baselineNanos = GracefulShutdownIntegrationTest.baselineNanos();
        final Server server = GracefulShutdownIntegrationTest.server.start();
        // Keep sending a request after shutdown starts so that the hard limit is reached.
        final SleepService.Iface client = GracefulShutdownIntegrationTest.newClient();
        final CompletableFuture<Long> stopFuture = CompletableFuture.supplyAsync(() -> {
            final long startTime = System.nanoTime();
            server.stop().join();
            final long stopTime = System.nanoTime();
            return stopTime - startTime;
        });
        for (int i = 0; i < 50; i++) {
            try {
                client.sleep(100);
            } catch (Exception e) {
                // Server has been shut down
                break;
            }
        }
        // Should take 1 more second than the baseline, because the requests will extend the quiet period
        // until the shutdown timeout is triggered.
        assertThat(stopFuture.join()).isBetween((baselineNanos + (TimeUnit.MILLISECONDS.toNanos(600))), (baselineNanos + (TimeUnit.MILLISECONDS.toNanos(1400))));
    }
}

