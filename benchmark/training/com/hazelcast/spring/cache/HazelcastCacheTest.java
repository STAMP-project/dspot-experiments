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
package com.hazelcast.spring.cache;


import Cache.ValueRetrievalException;
import com.hazelcast.spring.CustomSpringJUnit4ClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;


/**
 * Tests for {@link HazelcastCache}.
 *
 * @author Stephane Nicoll
 */
@RunWith(CustomSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "simple-config.xml" })
@Category(QuickTest.class)
public class HazelcastCacheTest {
    @Autowired
    public CacheManager cacheManager;

    private Cache cache;

    @Test
    public void testCacheGetCallable() {
        doTestCacheGetCallable("test");
    }

    @Test
    public void testCacheGetCallableWithNull() {
        doTestCacheGetCallable(null);
    }

    @Test
    public void testCacheGetCallableNotInvokedWithHit() {
        doTestCacheGetCallableNotInvokedWithHit("existing");
    }

    @Test
    public void testCacheGetCallableNotInvokedWithHitNull() {
        doTestCacheGetCallableNotInvokedWithHit(null);
    }

    @Test
    public void testCacheGetCallableFail() {
        String key = createRandomKey();
        Assert.assertNull(cache.get(key));
        try {
            cache.get(key, new Callable<Object>() {
                @Override
                public Object call() {
                    throw new UnsupportedOperationException("Expected exception");
                }
            });
        } catch (Cache ex) {
            Assert.assertNotNull(ex.getCause());
            Assert.assertEquals(UnsupportedOperationException.class, ex.getCause().getClass());
        }
    }

    /**
     * Tests that a call to get with a Callable concurrently properly synchronize the invocations.
     */
    @Test
    public void testCacheGetSynchronized() throws Exception {
        final AtomicInteger counter = new AtomicInteger();
        final List<Object> results = new CopyOnWriteArrayList<Object>();
        final CountDownLatch latch = new CountDownLatch(10);
        final String key = createRandomKey();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    Integer value = cache.get(key, new Callable<Integer>() {
                        @Override
                        public Integer call() {
                            // make sure the thread will overlap
                            sleepMillis(50);
                            return counter.incrementAndGet();
                        }
                    });
                    results.add(value);
                } finally {
                    latch.countDown();
                }
            }
        };
        for (int i = 0; i < 10; i++) {
            new Thread(run).start();
        }
        latch.await();
        Assert.assertEquals(10, results.size());
        for (Object result : results) {
            Assert.assertThat(((Integer) (result)), Is.is(1));
        }
    }
}

