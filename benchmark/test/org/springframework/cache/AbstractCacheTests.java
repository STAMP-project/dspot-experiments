/**
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cache;


import Cache.ValueRetrievalException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 *
 *
 * @author Stephane Nicoll
 */
public abstract class AbstractCacheTests<T extends Cache> {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    protected static final String CACHE_NAME = "testCache";

    @Test
    public void testCacheName() throws Exception {
        Assert.assertEquals(AbstractCacheTests.CACHE_NAME, getName());
    }

    @Test
    public void testNativeCache() throws Exception {
        Assert.assertSame(getNativeCache(), getNativeCache());
    }

    @Test
    public void testCachePut() throws Exception {
        T cache = getCache();
        String key = createRandomKey();
        Object value = "george";
        Assert.assertNull(cache.get(key));
        Assert.assertNull(cache.get(key, String.class));
        Assert.assertNull(cache.get(key, Object.class));
        cache.put(key, value);
        Assert.assertEquals(value, cache.get(key).get());
        Assert.assertEquals(value, cache.get(key, String.class));
        Assert.assertEquals(value, cache.get(key, Object.class));
        Assert.assertEquals(value, cache.get(key, ((Class<?>) (null))));
        cache.put(key, null);
        Assert.assertNotNull(cache.get(key));
        Assert.assertNull(cache.get(key).get());
        Assert.assertNull(cache.get(key, String.class));
        Assert.assertNull(cache.get(key, Object.class));
    }

    @Test
    public void testCachePutIfAbsent() throws Exception {
        T cache = getCache();
        String key = createRandomKey();
        Object value = "initialValue";
        Assert.assertNull(cache.get(key));
        Assert.assertNull(cache.putIfAbsent(key, value));
        Assert.assertEquals(value, cache.get(key).get());
        Assert.assertEquals("initialValue", putIfAbsent(key, "anotherValue").get());
        Assert.assertEquals(value, cache.get(key).get());// not changed

    }

    @Test
    public void testCacheRemove() throws Exception {
        T cache = getCache();
        String key = createRandomKey();
        Object value = "george";
        Assert.assertNull(cache.get(key));
        cache.put(key, value);
    }

    @Test
    public void testCacheClear() throws Exception {
        T cache = getCache();
        Assert.assertNull(cache.get("enescu"));
        put("enescu", "george");
        Assert.assertNull(cache.get("vlaicu"));
        put("vlaicu", "aurel");
        clear();
        Assert.assertNull(cache.get("vlaicu"));
        Assert.assertNull(cache.get("enescu"));
    }

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
        T cache = getCache();
        String key = createRandomKey();
        Assert.assertNull(cache.get(key));
        try {
            get(key, () -> {
                throw new UnsupportedOperationException("Expected exception");
            });
        } catch (Cache ex) {
            Assert.assertNotNull(ex.getCause());
            Assert.assertEquals(UnsupportedOperationException.class, ex.getCause().getClass());
        }
    }

    /**
     * Test that a call to get with a Callable concurrently properly synchronize the
     * invocations.
     */
    @Test
    public void testCacheGetSynchronized() throws InterruptedException {
        T cache = getCache();
        final AtomicInteger counter = new AtomicInteger();
        final List<Object> results = new CopyOnWriteArrayList<>();
        final CountDownLatch latch = new CountDownLatch(10);
        String key = createRandomKey();
        Runnable run = () -> {
            try {
                Integer value = cache.get(key, () -> {
                    Thread.sleep(50);// make sure the thread will overlap

                    return counter.incrementAndGet();
                });
                results.add(value);
            } finally {
                latch.countDown();
            }
        };
        for (int i = 0; i < 10; i++) {
            new Thread(run).start();
        }
        latch.await();
        Assert.assertEquals(10, results.size());
        results.forEach(( r) -> Assert.assertThat(r, Is.is(1)));// Only one method got invoked

    }
}

