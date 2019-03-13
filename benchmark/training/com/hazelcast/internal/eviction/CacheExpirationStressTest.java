/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.internal.eviction;


import com.hazelcast.cache.impl.HazelcastServerCachingProvider;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.OverridePropertyRule;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.NightlyTest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.cache.Cache;
import javax.cache.event.CacheEntryExpiredListener;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(NightlyTest.class)
public class CacheExpirationStressTest extends HazelcastTestSupport {
    @Rule
    public final OverridePropertyRule overrideTaskSecondsRule = OverridePropertyRule.set(PROP_TASK_PERIOD_SECONDS, "2");

    protected final String cacheName = "test";

    protected String cacheNameWithPrefix;

    private static final int CLUSTER_SIZE = 5;

    private static final int KEY_RANGE = 100000;

    private HazelcastInstance[] instances = new HazelcastInstance[CacheExpirationStressTest.CLUSTER_SIZE];

    private TestHazelcastInstanceFactory factory;

    private Random random = new Random();

    private final AtomicBoolean done = new AtomicBoolean();

    private final int DURATION_SECONDS = 60;

    @Test
    public void test() throws InterruptedException {
        List<Thread> list = new ArrayList<Thread>();
        for (int i = 0; i < (CacheExpirationStressTest.CLUSTER_SIZE); i++) {
            CacheConfig cacheConfig = getCacheConfig();
            Cache cache = HazelcastServerCachingProvider.createCachingProvider(instances[i]).getCacheManager().createCache(cacheName, cacheConfig);
            cacheNameWithPrefix = cache.getName();
            list.add(new Thread(new CacheExpirationStressTest.TestRunner(cache, done)));
        }
        for (Thread thread : list) {
            thread.start();
        }
        HazelcastTestSupport.sleepAtLeastSeconds(DURATION_SECONDS);
        done.set(true);
        for (Thread thread : list) {
            thread.join();
        }
        assertRecords(instances);
    }

    class TestRunner implements Runnable {
        private Cache cache;

        private AtomicBoolean done;

        private CacheEntryExpiredListener listener;

        TestRunner(Cache cache, AtomicBoolean done) {
            this.cache = cache;
            this.done = done;
        }

        @Override
        public void run() {
            while (!(done.get())) {
                doOp(cache);
            } 
        }
    }
}

