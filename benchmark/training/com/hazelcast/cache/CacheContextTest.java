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
package com.hazelcast.cache;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import javax.cache.event.CacheEntryCreatedListener;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.spi.CachingProvider;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CacheContextTest extends HazelcastTestSupport {
    private static final String CACHE_NAME = "MyCache";

    private static final String CACHE_NAME_WITH_PREFIX = "/hz/" + (CacheContextTest.CACHE_NAME);

    protected HazelcastInstance driverInstance;

    protected HazelcastInstance hazelcastInstance1;

    protected HazelcastInstance hazelcastInstance2;

    protected CachingProvider provider;

    @Test
    public void cacheEntryListenerCountIncreasedAfterRegisterAndDecreasedAfterDeregister() {
        cacheEntryListenerCountIncreasedAndDecreasedCorrectly(CacheContextTest.DecreaseType.DEREGISTER);
    }

    @Test
    public void cacheEntryListenerCountIncreasedAfterRegisterAndDecreasedAfterShutdown() {
        cacheEntryListenerCountIncreasedAndDecreasedCorrectly(CacheContextTest.DecreaseType.SHUTDOWN);
    }

    @Test
    public void cacheEntryListenerCountIncreasedAfterRegisterAndDecreasedAfterTerminate() {
        cacheEntryListenerCountIncreasedAndDecreasedCorrectly(CacheContextTest.DecreaseType.TERMINATE);
    }

    protected enum DecreaseType {

        DEREGISTER,
        SHUTDOWN,
        TERMINATE;}

    public static class TestListener implements Serializable , CacheEntryCreatedListener<String, String> {
        @Override
        public void onCreated(Iterable<CacheEntryEvent<? extends String, ? extends String>> cacheEntryEvents) throws CacheEntryListenerException {
        }
    }
}

