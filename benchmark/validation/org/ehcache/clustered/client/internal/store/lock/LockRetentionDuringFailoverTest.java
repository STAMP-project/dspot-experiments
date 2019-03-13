/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.clustered.client.internal.store.lock;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.ehcache.Cache;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.terracotta.passthrough.PassthroughClusterControl;


public class LockRetentionDuringFailoverTest {
    private static final String STRIPENAME = "stripe";

    private static final String STRIPE_URI = "passthrough://" + (LockRetentionDuringFailoverTest.STRIPENAME);

    private PassthroughClusterControl clusterControl;

    private CountDownLatch latch;

    private LockRetentionDuringFailoverTest.LatchedLoaderWriter loaderWriter;

    private Cache<Long, String> cache;

    @Test
    public void testLockRetentionDuringFailover() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Future<?> putFuture = executorService.submit(() -> cache.put(1L, "one"));
        clusterControl.terminateActive();
        clusterControl.waitForActive();
        Assert.assertThat(loaderWriter.backingMap.isEmpty(), Matchers.is(true));
        latch.countDown();
        putFuture.get();
        Assert.assertThat(loaderWriter.backingMap.get(1L), Matchers.is("one"));
    }

    private static class LatchedLoaderWriter implements CacheLoaderWriter<Long, String> {
        ConcurrentHashMap<Long, String> backingMap = new ConcurrentHashMap<>();

        private final CountDownLatch latch;

        LatchedLoaderWriter(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public String load(Long key) throws Exception {
            latch.await();
            return backingMap.get(key);
        }

        @Override
        public void write(Long key, String value) throws Exception {
            latch.await();
            backingMap.put(key, value);
        }

        @Override
        public void delete(Long key) throws Exception {
            latch.await();
            backingMap.remove(key);
        }
    }
}

