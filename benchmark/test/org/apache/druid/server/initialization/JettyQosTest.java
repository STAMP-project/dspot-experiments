/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.server.initialization;


import com.google.common.base.Throwables;
import com.google.common.util.concurrent.ListenableFuture;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.druid.java.util.common.concurrent.Execs;
import org.apache.druid.java.util.http.client.HttpClient;
import org.apache.druid.java.util.http.client.response.StatusResponseHandler;
import org.apache.druid.java.util.http.client.response.StatusResponseHolder;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.junit.Assert;
import org.junit.Test;


public class JettyQosTest extends BaseJettyTest {
    @Test
    public void testNumThreads() {
        // Just make sure the injector stuff for this test is actually working.
        Assert.assertEquals(10, getMaxThreads());
    }

    @Test(timeout = 120000L)
    public void testQoS() throws Exception {
        final int fastThreads = 20;
        final int slowThreads = 15;
        final int slowRequestsPerThread = 5;
        final int fastRequestsPerThread = 200;
        final HttpClient fastClient = new BaseJettyTest.ClientHolder(fastThreads).getClient();
        final HttpClient slowClient = new BaseJettyTest.ClientHolder(slowThreads).getClient();
        final ExecutorService fastPool = Execs.multiThreaded(fastThreads, "fast-%d");
        final ExecutorService slowPool = Execs.multiThreaded(slowThreads, "slow-%d");
        final CountDownLatch latch = new CountDownLatch((fastThreads * fastRequestsPerThread));
        final AtomicLong fastCount = new AtomicLong();
        final AtomicLong slowCount = new AtomicLong();
        final AtomicLong fastElapsed = new AtomicLong();
        final AtomicLong slowElapsed = new AtomicLong();
        for (int i = 0; i < slowThreads; i++) {
            slowPool.submit(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < slowRequestsPerThread; i++) {
                        long startTime = System.currentTimeMillis();
                        try {
                            ListenableFuture<StatusResponseHolder> go = slowClient.go(new org.apache.druid.java.util.http.client.Request(HttpMethod.GET, new URL((("http://localhost:" + (port)) + "/slow/hello"))), new StatusResponseHandler(Charset.defaultCharset()));
                            go.get();
                            slowCount.incrementAndGet();
                            slowElapsed.addAndGet(((System.currentTimeMillis()) - startTime));
                        } catch (InterruptedException e) {
                            // BE COOL
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw Throwables.propagate(e);
                        }
                    }
                }
            });
        }
        // wait for jetty server pool to completely fill up
        while ((server.getThreadPool().getIdleThreads()) != 0) {
            Thread.sleep(25);
        } 
        for (int i = 0; i < fastThreads; i++) {
            fastPool.submit(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < fastRequestsPerThread; i++) {
                        long startTime = System.currentTimeMillis();
                        try {
                            ListenableFuture<StatusResponseHolder> go = fastClient.go(new org.apache.druid.java.util.http.client.Request(HttpMethod.GET, new URL((("http://localhost:" + (port)) + "/default"))), new StatusResponseHandler(Charset.defaultCharset()));
                            go.get();
                            fastCount.incrementAndGet();
                            fastElapsed.addAndGet(((System.currentTimeMillis()) - startTime));
                            latch.countDown();
                        } catch (InterruptedException e) {
                            // BE COOL
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw Throwables.propagate(e);
                        }
                    }
                }
            });
        }
        // Wait for all fast requests to be served
        latch.await();
        slowPool.shutdownNow();
        fastPool.shutdown();
        // check that fast requests finished quickly
        Assert.assertTrue((((fastElapsed.get()) / (fastCount.get())) < 500));
    }
}

