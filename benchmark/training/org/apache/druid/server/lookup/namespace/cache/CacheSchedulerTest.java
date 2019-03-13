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
package org.apache.druid.server.lookup.namespace.cache;


import CacheScheduler.Entry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nullable;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.java.util.common.concurrent.Execs;
import org.apache.druid.java.util.common.lifecycle.Lifecycle;
import org.apache.druid.query.lookup.namespace.UriExtractionNamespace;
import org.apache.druid.query.lookup.namespace.UriExtractionNamespaceTest;
import org.apache.druid.server.lookup.namespace.NamespaceExtractionConfig;
import org.apache.druid.server.metrics.NoopServiceEmitter;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 */
@RunWith(Parameterized.class)
public class CacheSchedulerTest {
    public static final Function<Lifecycle, NamespaceExtractionCacheManager> CREATE_ON_HEAP_CACHE_MANAGER = new Function<Lifecycle, NamespaceExtractionCacheManager>() {
        @Nullable
        @Override
        public NamespaceExtractionCacheManager apply(@Nullable
        Lifecycle lifecycle) {
            return new OnHeapNamespaceExtractionCacheManager(lifecycle, new NoopServiceEmitter(), new NamespaceExtractionConfig());
        }
    };

    public static final Function<Lifecycle, NamespaceExtractionCacheManager> CREATE_OFF_HEAP_CACHE_MANAGER = new Function<Lifecycle, NamespaceExtractionCacheManager>() {
        @Nullable
        @Override
        public NamespaceExtractionCacheManager apply(@Nullable
        Lifecycle lifecycle) {
            return new OffHeapNamespaceExtractionCacheManager(lifecycle, new NoopServiceEmitter(), new NamespaceExtractionConfig());
        }
    };

    private static final String KEY = "foo";

    private static final String VALUE = "bar";

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private final Function<Lifecycle, NamespaceExtractionCacheManager> createCacheManager;

    private Lifecycle lifecycle;

    private NamespaceExtractionCacheManager cacheManager;

    private CacheScheduler scheduler;

    private File tmpFile;

    public CacheSchedulerTest(Function<Lifecycle, NamespaceExtractionCacheManager> createCacheManager) {
        this.createCacheManager = createCacheManager;
    }

    @Test(timeout = 60000L)
    public void testSimpleSubmission() throws InterruptedException {
        UriExtractionNamespace namespace = new UriExtractionNamespace(tmpFile.toURI(), null, null, new UriExtractionNamespace.ObjectMapperFlatDataParser(UriExtractionNamespaceTest.registerTypes(new ObjectMapper())), new Period(0), null);
        CacheScheduler.Entry entry = scheduler.schedule(namespace);
        CacheSchedulerTest.waitFor(entry);
        Map<String, String> cache = entry.getCache();
        Assert.assertNull(cache.put("key", "val"));
        Assert.assertEquals("val", cache.get("key"));
    }

    @Test(timeout = 60000L)
    public void testPeriodicUpdatesScheduled() throws InterruptedException {
        final int repeatCount = 5;
        final long delay = 5;
        try {
            final UriExtractionNamespace namespace = getUriExtractionNamespace(delay);
            final long start = System.currentTimeMillis();
            try (CacheScheduler.Entry entry = scheduler.schedule(namespace)) {
                Assert.assertFalse(entry.getUpdaterFuture().isDone());
                Assert.assertFalse(entry.getUpdaterFuture().isCancelled());
                entry.awaitTotalUpdates(repeatCount);
                long minEnd = start + ((repeatCount - 1) * delay);
                long end = System.currentTimeMillis();
                Assert.assertTrue(StringUtils.format("Didn't wait long enough between runs. Expected more than %d was %d", (minEnd - start), (end - start)), (minEnd <= end));
            }
        } finally {
            lifecycle.stop();
            cacheManager.waitForServiceToEnd(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        }
        checkNoMoreRunning();
    }

    // This is very fast when run locally. Speed on Travis completely depends on noisy neighbors.
    @Test(timeout = 60000L)
    public void testConcurrentAddDelete() throws InterruptedException {
        final int threads = 10;
        final int deletesPerThread = 5;
        ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Execs.multiThreaded(threads, "concurrentTestingPool-%s"));
        final CountDownLatch latch = new CountDownLatch(threads);
        Collection<ListenableFuture<?>> futures = new ArrayList<>();
        for (int i = 0; i < threads; ++i) {
            futures.add(executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        latch.countDown();
                        if (!(latch.await(5, TimeUnit.SECONDS))) {
                            throw new RuntimeException(new TimeoutException("Took too long to wait for more tasks"));
                        }
                        for (int j = 0; j < deletesPerThread; ++j) {
                            try {
                                testDelete();
                            } catch (Exception e) {
                                throw Throwables.propagate(e);
                            }
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw Throwables.propagate(e);
                    }
                }
            }));
        }
        // Create an all-encompassing exception if any of them failed
        final Collection<Exception> exceptions = new ArrayList<>();
        try {
            for (ListenableFuture<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw e;
                } catch (Exception e) {
                    exceptions.add(e);
                }
            }
            if (!(exceptions.isEmpty())) {
                final RuntimeException e = new RuntimeException("Futures failed");
                for (Exception ex : exceptions) {
                    e.addSuppressed(ex);
                }
            }
        } finally {
            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        }
        checkNoMoreRunning();
    }

    @Test(timeout = 60000L)
    public void testSimpleDelete() throws InterruptedException {
        testDelete();
    }

    @Test(timeout = 60000L)
    public void testShutdown() throws InterruptedException {
        final long period = 5L;
        try {
            final UriExtractionNamespace namespace = getUriExtractionNamespace(period);
            try (CacheScheduler.Entry entry = scheduler.schedule(namespace)) {
                final Future<?> future = entry.getUpdaterFuture();
                entry.awaitNextUpdates(1);
                Assert.assertFalse(future.isCancelled());
                Assert.assertFalse(future.isDone());
                final long prior = scheduler.updatesStarted();
                entry.awaitNextUpdates(1);
                Assert.assertTrue(((scheduler.updatesStarted()) > prior));
            }
        } finally {
            lifecycle.stop();
        }
        while (!(cacheManager.waitForServiceToEnd(1000, TimeUnit.MILLISECONDS))) {
            // keep waiting
        } 
        checkNoMoreRunning();
        Assert.assertTrue(cacheManager.scheduledExecutorService().isShutdown());
        Assert.assertTrue(cacheManager.scheduledExecutorService().isTerminated());
    }

    @Test(timeout = 60000L)
    public void testRunCount() throws InterruptedException {
        final int numWaits = 5;
        try {
            final UriExtractionNamespace namespace = getUriExtractionNamespace(((long) (5)));
            try (CacheScheduler.Entry entry = scheduler.schedule(namespace)) {
                final Future<?> future = entry.getUpdaterFuture();
                entry.awaitNextUpdates(numWaits);
                Assert.assertFalse(future.isDone());
            }
        } finally {
            lifecycle.stop();
        }
        while (!(cacheManager.waitForServiceToEnd(1000, TimeUnit.MILLISECONDS))) {
            // keep waiting
        } 
        Assert.assertTrue(((scheduler.updatesStarted()) >= numWaits));
        checkNoMoreRunning();
    }

    /**
     * Tests that even if entry.close() wasn't called, the scheduled task is cancelled when the entry becomes
     * unreachable.
     */
    @Test(timeout = 60000L)
    public void testEntryCloseForgotten() throws InterruptedException {
        scheduleDanglingEntry();
        Assert.assertEquals(1, scheduler.getActiveEntries());
        while ((scheduler.getActiveEntries()) > 0) {
            System.gc();
            Thread.sleep(1000);
        } 
        Assert.assertEquals(0, scheduler.getActiveEntries());
    }
}

