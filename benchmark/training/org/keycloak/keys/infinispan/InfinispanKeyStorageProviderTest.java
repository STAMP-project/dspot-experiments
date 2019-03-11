/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.keys.infinispan;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;
import org.infinispan.Cache;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.common.util.Time;
import org.keycloak.crypto.KeyWrapper;
import org.keycloak.keys.PublicKeyLoader;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class InfinispanKeyStorageProviderTest {
    private Map<String, AtomicInteger> counters = new ConcurrentHashMap<>();

    Cache<String, PublicKeysEntry> keys = getKeysCache();

    Map<String, FutureTask<PublicKeysEntry>> tasksInProgress = new ConcurrentHashMap<>();

    int minTimeBetweenRequests = 10;

    @Test
    public void testConcurrency() throws Exception {
        // Just one thread will execute the task
        List<Thread> threads = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(new InfinispanKeyStorageProviderTest.SampleWorker("model1"));
            threads.add(t);
        }
        startAndJoinAll(threads);
        Assert.assertEquals(counters.get("model1").get(), 1);
        threads.clear();
        // model1 won't be executed due to lastRequestTime. model2 will be executed just with one thread
        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(new InfinispanKeyStorageProviderTest.SampleWorker("model1"));
            threads.add(t);
        }
        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(new InfinispanKeyStorageProviderTest.SampleWorker("model2"));
            threads.add(t);
        }
        startAndJoinAll(threads);
        Assert.assertEquals(counters.get("model1").get(), 1);
        Assert.assertEquals(counters.get("model2").get(), 1);
        threads.clear();
        // Increase time offset
        Time.setOffset(20);
        // Time updated. So another thread should successfully run loader for both model1 and model2
        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(new InfinispanKeyStorageProviderTest.SampleWorker("model1"));
            threads.add(t);
        }
        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(new InfinispanKeyStorageProviderTest.SampleWorker("model2"));
            threads.add(t);
        }
        startAndJoinAll(threads);
        Assert.assertEquals(counters.get("model1").get(), 2);
        Assert.assertEquals(counters.get("model2").get(), 2);
        threads.clear();
    }

    private class SampleWorker implements Runnable {
        private final String modelKey;

        private SampleWorker(String modelKey) {
            this.modelKey = modelKey;
        }

        @Override
        public void run() {
            InfinispanPublicKeyStorageProvider provider = new InfinispanPublicKeyStorageProvider(null, keys, tasksInProgress, minTimeBetweenRequests);
            provider.getPublicKey(modelKey, "kid1", new InfinispanKeyStorageProviderTest.SampleLoader(modelKey));
        }
    }

    private class SampleLoader implements PublicKeyLoader {
        private final String modelKey;

        private SampleLoader(String modelKey) {
            this.modelKey = modelKey;
        }

        @Override
        public Map<String, KeyWrapper> loadKeys() throws Exception {
            counters.putIfAbsent(modelKey, new AtomicInteger(0));
            AtomicInteger currentCounter = counters.get(modelKey);
            currentCounter.incrementAndGet();
            return Collections.emptyMap();
        }
    }
}

