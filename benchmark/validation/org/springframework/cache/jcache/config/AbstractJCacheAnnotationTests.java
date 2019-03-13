/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.cache.jcache.config;


import Cache.ValueWrapper;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;


/**
 *
 *
 * @author Stephane Nicoll
 */
public abstract class AbstractJCacheAnnotationTests {
    public static final String DEFAULT_CACHE = "default";

    public static final String EXCEPTION_CACHE = "exception";

    @Rule
    public final TestName name = new TestName();

    protected ApplicationContext ctx;

    private JCacheableService<?> service;

    private CacheManager cacheManager;

    @Test
    public void cache() {
        String keyItem = name.getMethodName();
        Object first = service.cache(keyItem);
        Object second = service.cache(keyItem);
        Assert.assertSame(first, second);
    }

    @Test
    public void cacheNull() {
        Cache cache = getCache(AbstractJCacheAnnotationTests.DEFAULT_CACHE);
        String keyItem = name.getMethodName();
        Assert.assertNull(cache.get(keyItem));
        Object first = service.cacheNull(keyItem);
        Object second = service.cacheNull(keyItem);
        Assert.assertSame(first, second);
        Cache.ValueWrapper wrapper = cache.get(keyItem);
        Assert.assertNotNull(wrapper);
        Assert.assertSame(first, wrapper.get());
        Assert.assertNull("Cached value should be null", wrapper.get());
    }

    @Test
    public void cacheException() {
        String keyItem = name.getMethodName();
        Cache cache = getCache(AbstractJCacheAnnotationTests.EXCEPTION_CACHE);
        Object key = createKey(keyItem);
        Assert.assertNull(cache.get(key));
        try {
            service.cacheWithException(keyItem, true);
            Assert.fail("Should have thrown an exception");
        } catch (UnsupportedOperationException e) {
            // This is what we expect
        }
        Cache.ValueWrapper result = cache.get(key);
        Assert.assertNotNull(result);
        Assert.assertEquals(UnsupportedOperationException.class, result.get().getClass());
    }

    @Test
    public void cacheExceptionVetoed() {
        String keyItem = name.getMethodName();
        Cache cache = getCache(AbstractJCacheAnnotationTests.EXCEPTION_CACHE);
        Object key = createKey(keyItem);
        Assert.assertNull(cache.get(key));
        try {
            service.cacheWithException(keyItem, false);
            Assert.fail("Should have thrown an exception");
        } catch (NullPointerException e) {
            // This is what we expect
        }
        Assert.assertNull(cache.get(key));
    }

    @Test
    public void cacheCheckedException() {
        String keyItem = name.getMethodName();
        Cache cache = getCache(AbstractJCacheAnnotationTests.EXCEPTION_CACHE);
        Object key = createKey(keyItem);
        Assert.assertNull(cache.get(key));
        try {
            service.cacheWithCheckedException(keyItem, true);
            Assert.fail("Should have thrown an exception");
        } catch (IOException e) {
            // This is what we expect
        }
        Cache.ValueWrapper result = cache.get(key);
        Assert.assertNotNull(result);
        Assert.assertEquals(IOException.class, result.get().getClass());
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void cacheExceptionRewriteCallStack() {
        final String keyItem = name.getMethodName();
        UnsupportedOperationException first = null;
        long ref = service.exceptionInvocations();
        try {
            service.cacheWithException(keyItem, true);
            Assert.fail("Should have thrown an exception");
        } catch (UnsupportedOperationException e) {
            first = e;
        }
        // Sanity check, this particular call has called the service
        Assert.assertEquals("First call should not have been cached", (ref + 1), service.exceptionInvocations());
        UnsupportedOperationException second = methodInCallStack(keyItem);
        // Sanity check, this particular call has *not* called the service
        Assert.assertEquals("Second call should have been cached", (ref + 1), service.exceptionInvocations());
        Assert.assertEquals(first.getCause(), second.getCause());
        Assert.assertEquals(first.getMessage(), second.getMessage());
        Assert.assertFalse("Original stack must not contain any reference to methodInCallStack", contain(first, AbstractJCacheAnnotationTests.class.getName(), "methodInCallStack"));
        Assert.assertTrue("Cached stack should have been rewritten with a reference to  methodInCallStack", contain(second, AbstractJCacheAnnotationTests.class.getName(), "methodInCallStack"));
    }

    @Test
    public void cacheAlwaysInvoke() {
        String keyItem = name.getMethodName();
        Object first = service.cacheAlwaysInvoke(keyItem);
        Object second = service.cacheAlwaysInvoke(keyItem);
        Assert.assertNotSame(first, second);
    }

    @Test
    public void cacheWithPartialKey() {
        String keyItem = name.getMethodName();
        Object first = service.cacheWithPartialKey(keyItem, true);
        Object second = service.cacheWithPartialKey(keyItem, false);
        Assert.assertSame(first, second);// second argument not used, see config

    }

    @Test
    public void cacheWithCustomCacheResolver() {
        String keyItem = name.getMethodName();
        Cache cache = getCache(AbstractJCacheAnnotationTests.DEFAULT_CACHE);
        Object key = createKey(keyItem);
        service.cacheWithCustomCacheResolver(keyItem);
        Assert.assertNull(cache.get(key));// Cache in mock cache

    }

    @Test
    public void cacheWithCustomKeyGenerator() {
        String keyItem = name.getMethodName();
        Cache cache = getCache(AbstractJCacheAnnotationTests.DEFAULT_CACHE);
        Object key = createKey(keyItem);
        service.cacheWithCustomKeyGenerator(keyItem, "ignored");
        Assert.assertNull(cache.get(key));
    }

    @Test
    public void put() {
        String keyItem = name.getMethodName();
        Cache cache = getCache(AbstractJCacheAnnotationTests.DEFAULT_CACHE);
        Object key = createKey(keyItem);
        Object value = new Object();
        Assert.assertNull(cache.get(key));
        service.put(keyItem, value);
        Cache.ValueWrapper result = cache.get(key);
        Assert.assertNotNull(result);
        Assert.assertEquals(value, result.get());
    }

    @Test
    public void putWithException() {
        String keyItem = name.getMethodName();
        Cache cache = getCache(AbstractJCacheAnnotationTests.DEFAULT_CACHE);
        Object key = createKey(keyItem);
        Object value = new Object();
        Assert.assertNull(cache.get(key));
        try {
            service.putWithException(keyItem, value, true);
            Assert.fail("Should have thrown an exception");
        } catch (UnsupportedOperationException e) {
            // This is what we expect
        }
        Cache.ValueWrapper result = cache.get(key);
        Assert.assertNotNull(result);
        Assert.assertEquals(value, result.get());
    }

    @Test
    public void putWithExceptionVetoPut() {
        String keyItem = name.getMethodName();
        Cache cache = getCache(AbstractJCacheAnnotationTests.DEFAULT_CACHE);
        Object key = createKey(keyItem);
        Object value = new Object();
        Assert.assertNull(cache.get(key));
        try {
            service.putWithException(keyItem, value, false);
            Assert.fail("Should have thrown an exception");
        } catch (NullPointerException e) {
            // This is what we expect
        }
        Assert.assertNull(cache.get(key));
    }

    @Test
    public void earlyPut() {
        String keyItem = name.getMethodName();
        Cache cache = getCache(AbstractJCacheAnnotationTests.DEFAULT_CACHE);
        Object key = createKey(keyItem);
        Object value = new Object();
        Assert.assertNull(cache.get(key));
        service.earlyPut(keyItem, value);
        Cache.ValueWrapper result = cache.get(key);
        Assert.assertNotNull(result);
        Assert.assertEquals(value, result.get());
    }

    @Test
    public void earlyPutWithException() {
        String keyItem = name.getMethodName();
        Cache cache = getCache(AbstractJCacheAnnotationTests.DEFAULT_CACHE);
        Object key = createKey(keyItem);
        Object value = new Object();
        Assert.assertNull(cache.get(key));
        try {
            service.earlyPutWithException(keyItem, value, true);
            Assert.fail("Should have thrown an exception");
        } catch (UnsupportedOperationException e) {
            // This is what we expect
        }
        Cache.ValueWrapper result = cache.get(key);
        Assert.assertNotNull(result);
        Assert.assertEquals(value, result.get());
    }

    @Test
    public void earlyPutWithExceptionVetoPut() {
        String keyItem = name.getMethodName();
        Cache cache = getCache(AbstractJCacheAnnotationTests.DEFAULT_CACHE);
        Object key = createKey(keyItem);
        Object value = new Object();
        Assert.assertNull(cache.get(key));
        try {
            service.earlyPutWithException(keyItem, value, false);
            Assert.fail("Should have thrown an exception");
        } catch (NullPointerException e) {
            // This is what we expect
        }
        // This will be cached anyway as the earlyPut has updated the cache before
        Cache.ValueWrapper result = cache.get(key);
        Assert.assertNotNull(result);
        Assert.assertEquals(value, result.get());
    }

    @Test
    public void remove() {
        String keyItem = name.getMethodName();
        Cache cache = getCache(AbstractJCacheAnnotationTests.DEFAULT_CACHE);
        Object key = createKey(keyItem);
        Object value = new Object();
        cache.put(key, value);
        service.remove(keyItem);
        Assert.assertNull(cache.get(key));
    }

    @Test
    public void removeWithException() {
        String keyItem = name.getMethodName();
        Cache cache = getCache(AbstractJCacheAnnotationTests.DEFAULT_CACHE);
        Object key = createKey(keyItem);
        Object value = new Object();
        cache.put(key, value);
        try {
            service.removeWithException(keyItem, true);
            Assert.fail("Should have thrown an exception");
        } catch (UnsupportedOperationException e) {
            // This is what we expect
        }
        Assert.assertNull(cache.get(key));
    }

    @Test
    public void removeWithExceptionVetoRemove() {
        String keyItem = name.getMethodName();
        Cache cache = getCache(AbstractJCacheAnnotationTests.DEFAULT_CACHE);
        Object key = createKey(keyItem);
        Object value = new Object();
        cache.put(key, value);
        try {
            service.removeWithException(keyItem, false);
            Assert.fail("Should have thrown an exception");
        } catch (NullPointerException e) {
            // This is what we expect
        }
        Cache.ValueWrapper wrapper = cache.get(key);
        Assert.assertNotNull(wrapper);
        Assert.assertEquals(value, wrapper.get());
    }

    @Test
    public void earlyRemove() {
        String keyItem = name.getMethodName();
        Cache cache = getCache(AbstractJCacheAnnotationTests.DEFAULT_CACHE);
        Object key = createKey(keyItem);
        Object value = new Object();
        cache.put(key, value);
        service.earlyRemove(keyItem);
        Assert.assertNull(cache.get(key));
    }

    @Test
    public void earlyRemoveWithException() {
        String keyItem = name.getMethodName();
        Cache cache = getCache(AbstractJCacheAnnotationTests.DEFAULT_CACHE);
        Object key = createKey(keyItem);
        Object value = new Object();
        cache.put(key, value);
        try {
            service.earlyRemoveWithException(keyItem, true);
            Assert.fail("Should have thrown an exception");
        } catch (UnsupportedOperationException e) {
            // This is what we expect
        }
        Assert.assertNull(cache.get(key));
    }

    @Test
    public void earlyRemoveWithExceptionVetoRemove() {
        String keyItem = name.getMethodName();
        Cache cache = getCache(AbstractJCacheAnnotationTests.DEFAULT_CACHE);
        Object key = createKey(keyItem);
        Object value = new Object();
        cache.put(key, value);
        try {
            service.earlyRemoveWithException(keyItem, false);
            Assert.fail("Should have thrown an exception");
        } catch (NullPointerException e) {
            // This is what we expect
        }
        // This will be remove anyway as the earlyRemove has removed the cache before
        Assert.assertNull(cache.get(key));
    }

    @Test
    public void removeAll() {
        Cache cache = getCache(AbstractJCacheAnnotationTests.DEFAULT_CACHE);
        Object key = createKey(name.getMethodName());
        cache.put(key, new Object());
        service.removeAll();
        Assert.assertTrue(isEmpty(cache));
    }

    @Test
    public void removeAllWithException() {
        Cache cache = getCache(AbstractJCacheAnnotationTests.DEFAULT_CACHE);
        Object key = createKey(name.getMethodName());
        cache.put(key, new Object());
        try {
            service.removeAllWithException(true);
            Assert.fail("Should have thrown an exception");
        } catch (UnsupportedOperationException e) {
            // This is what we expect
        }
        Assert.assertTrue(isEmpty(cache));
    }

    @Test
    public void removeAllWithExceptionVetoRemove() {
        Cache cache = getCache(AbstractJCacheAnnotationTests.DEFAULT_CACHE);
        Object key = createKey(name.getMethodName());
        cache.put(key, new Object());
        try {
            service.removeAllWithException(false);
            Assert.fail("Should have thrown an exception");
        } catch (NullPointerException e) {
            // This is what we expect
        }
        Assert.assertNotNull(cache.get(key));
    }

    @Test
    public void earlyRemoveAll() {
        Cache cache = getCache(AbstractJCacheAnnotationTests.DEFAULT_CACHE);
        Object key = createKey(name.getMethodName());
        cache.put(key, new Object());
        service.earlyRemoveAll();
        Assert.assertTrue(isEmpty(cache));
    }

    @Test
    public void earlyRemoveAllWithException() {
        Cache cache = getCache(AbstractJCacheAnnotationTests.DEFAULT_CACHE);
        Object key = createKey(name.getMethodName());
        cache.put(key, new Object());
        try {
            service.earlyRemoveAllWithException(true);
            Assert.fail("Should have thrown an exception");
        } catch (UnsupportedOperationException e) {
            // This is what we expect
        }
        Assert.assertTrue(isEmpty(cache));
    }

    @Test
    public void earlyRemoveAllWithExceptionVetoRemove() {
        Cache cache = getCache(AbstractJCacheAnnotationTests.DEFAULT_CACHE);
        Object key = createKey(name.getMethodName());
        cache.put(key, new Object());
        try {
            service.earlyRemoveAllWithException(false);
            Assert.fail("Should have thrown an exception");
        } catch (NullPointerException e) {
            // This is what we expect
        }
        // This will be remove anyway as the earlyRemove has removed the cache before
        Assert.assertTrue(isEmpty(cache));
    }
}

