/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.cache.jcache.interceptor;


import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.cache.jcache.AbstractJCacheTests;
import org.springframework.util.ReflectionUtils;


/**
 *
 *
 * @author Stephane Nicoll
 */
public class JCacheInterceptorTests extends AbstractJCacheTests {
    private final CacheOperationInvoker dummyInvoker = new JCacheInterceptorTests.DummyInvoker(null);

    @Test
    public void severalCachesNotSupported() {
        JCacheInterceptor interceptor = createInterceptor(createOperationSource(cacheManager, new org.springframework.cache.interceptor.NamedCacheResolver(cacheManager, "default", "simpleCache"), defaultExceptionCacheResolver, defaultKeyGenerator));
        AnnotatedJCacheableService service = new AnnotatedJCacheableService(cacheManager.getCache("default"));
        Method m = ReflectionUtils.findMethod(AnnotatedJCacheableService.class, "cache", String.class);
        try {
            interceptor.execute(dummyInvoker, service, m, new Object[]{ "myId" });
        } catch (IllegalStateException ex) {
            Assert.assertTrue(ex.getMessage().contains("JSR-107 only supports a single cache"));
        } catch (Throwable ex) {
            Assert.fail(("Unexpected: " + ex));
        }
    }

    @Test
    public void noCacheCouldBeResolved() {
        JCacheInterceptor interceptor = createInterceptor(// Returns empty list
        createOperationSource(cacheManager, new org.springframework.cache.interceptor.NamedCacheResolver(cacheManager), defaultExceptionCacheResolver, defaultKeyGenerator));
        AnnotatedJCacheableService service = new AnnotatedJCacheableService(cacheManager.getCache("default"));
        Method m = ReflectionUtils.findMethod(AnnotatedJCacheableService.class, "cache", String.class);
        try {
            interceptor.execute(dummyInvoker, service, m, new Object[]{ "myId" });
        } catch (IllegalStateException ex) {
            Assert.assertTrue(ex.getMessage().contains("Cache could not have been resolved for"));
        } catch (Throwable ex) {
            Assert.fail(("Unexpected: " + ex));
        }
    }

    @Test
    public void cacheManagerMandatoryIfCacheResolverNotSet() {
        thrown.expect(IllegalStateException.class);
        createOperationSource(null, null, null, defaultKeyGenerator);
    }

    @Test
    public void cacheManagerOptionalIfCacheResolversSet() {
        createOperationSource(null, defaultCacheResolver, defaultExceptionCacheResolver, defaultKeyGenerator);
    }

    @Test
    public void cacheResultReturnsProperType() throws Throwable {
        JCacheInterceptor interceptor = createInterceptor(createOperationSource(cacheManager, defaultCacheResolver, defaultExceptionCacheResolver, defaultKeyGenerator));
        AnnotatedJCacheableService service = new AnnotatedJCacheableService(cacheManager.getCache("default"));
        Method method = ReflectionUtils.findMethod(AnnotatedJCacheableService.class, "cache", String.class);
        CacheOperationInvoker invoker = new JCacheInterceptorTests.DummyInvoker(0L);
        Object execute = interceptor.execute(invoker, service, method, new Object[]{ "myId" });
        Assert.assertNotNull("result cannot be null.", execute);
        Assert.assertEquals("Wrong result type", Long.class, execute.getClass());
        Assert.assertEquals("Wrong result", 0L, execute);
    }

    private static class DummyInvoker implements CacheOperationInvoker {
        private final Object result;

        private DummyInvoker(Object result) {
            this.result = result;
        }

        @Override
        public Object invoke() throws ThrowableWrapper {
            return result;
        }
    }
}

