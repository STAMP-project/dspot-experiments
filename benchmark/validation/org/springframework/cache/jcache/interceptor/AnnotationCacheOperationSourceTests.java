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
import java.util.Comparator;
import javax.cache.annotation.CacheDefaults;
import javax.cache.annotation.CacheRemove;
import javax.cache.annotation.CacheRemoveAll;
import javax.cache.annotation.CacheResult;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.cache.jcache.AbstractJCacheTests;
import org.springframework.cache.jcache.support.TestableCacheKeyGenerator;
import org.springframework.cache.jcache.support.TestableCacheResolver;
import org.springframework.cache.jcache.support.TestableCacheResolverFactory;
import org.springframework.util.ReflectionUtils;


/**
 *
 *
 * @author Stephane Nicoll
 */
public class AnnotationCacheOperationSourceTests extends AbstractJCacheTests {
    private final DefaultJCacheOperationSource source = new DefaultJCacheOperationSource();

    private final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    @Test
    public void cache() {
        CacheResultOperation op = getDefaultCacheOperation(CacheResultOperation.class, String.class);
        assertDefaults(op);
        Assert.assertNull("Exception caching not enabled so resolver should not be set", op.getExceptionCacheResolver());
    }

    @Test
    public void cacheWithException() {
        CacheResultOperation op = getDefaultCacheOperation(CacheResultOperation.class, String.class, boolean.class);
        assertDefaults(op);
        Assert.assertEquals(defaultExceptionCacheResolver, op.getExceptionCacheResolver());
        Assert.assertEquals("exception", op.getExceptionCacheName());
    }

    @Test
    public void put() {
        CachePutOperation op = getDefaultCacheOperation(CachePutOperation.class, String.class, Object.class);
        assertDefaults(op);
    }

    @Test
    public void remove() {
        CacheRemoveOperation op = getDefaultCacheOperation(CacheRemoveOperation.class, String.class);
        assertDefaults(op);
    }

    @Test
    public void removeAll() {
        CacheRemoveAllOperation op = getDefaultCacheOperation(CacheRemoveAllOperation.class);
        Assert.assertEquals(defaultCacheResolver, op.getCacheResolver());
    }

    @Test
    public void noAnnotation() {
        Assert.assertNull(getCacheOperation(AnnotatedJCacheableService.class, name.getMethodName()));
    }

    @Test
    public void multiAnnotations() {
        thrown.expect(IllegalStateException.class);
        getCacheOperation(AnnotationCacheOperationSourceTests.InvalidCases.class, name.getMethodName());
    }

    @Test
    public void defaultCacheNameWithCandidate() {
        Method method = ReflectionUtils.findMethod(Object.class, "toString");
        Assert.assertEquals("foo", source.determineCacheName(method, null, "foo"));
    }

    @Test
    public void defaultCacheNameWithDefaults() {
        Method method = ReflectionUtils.findMethod(Object.class, "toString");
        CacheDefaults mock = Mockito.mock(CacheDefaults.class);
        BDDMockito.given(mock.cacheName()).willReturn("");
        Assert.assertEquals("java.lang.Object.toString()", source.determineCacheName(method, mock, ""));
    }

    @Test
    public void defaultCacheNameNoDefaults() {
        Method method = ReflectionUtils.findMethod(Object.class, "toString");
        Assert.assertEquals("java.lang.Object.toString()", source.determineCacheName(method, null, ""));
    }

    @Test
    public void defaultCacheNameWithParameters() {
        Method method = ReflectionUtils.findMethod(Comparator.class, "compare", Object.class, Object.class);
        Assert.assertEquals("java.util.Comparator.compare(java.lang.Object,java.lang.Object)", source.determineCacheName(method, null, ""));
    }

    @Test
    public void customCacheResolver() {
        CacheResultOperation operation = getCacheOperation(CacheResultOperation.class, AnnotationCacheOperationSourceTests.CustomService.class, name.getMethodName(), Long.class);
        assertJCacheResolver(operation.getCacheResolver(), TestableCacheResolver.class);
        assertJCacheResolver(operation.getExceptionCacheResolver(), null);
        Assert.assertEquals(KeyGeneratorAdapter.class, operation.getKeyGenerator().getClass());
        Assert.assertEquals(defaultKeyGenerator, getTarget());
    }

    @Test
    public void customKeyGenerator() {
        CacheResultOperation operation = getCacheOperation(CacheResultOperation.class, AnnotationCacheOperationSourceTests.CustomService.class, name.getMethodName(), Long.class);
        Assert.assertEquals(defaultCacheResolver, operation.getCacheResolver());
        Assert.assertNull(operation.getExceptionCacheResolver());
        assertCacheKeyGenerator(operation.getKeyGenerator(), TestableCacheKeyGenerator.class);
    }

    @Test
    public void customKeyGeneratorSpringBean() {
        TestableCacheKeyGenerator bean = new TestableCacheKeyGenerator();
        beanFactory.registerSingleton("fooBar", bean);
        CacheResultOperation operation = getCacheOperation(CacheResultOperation.class, AnnotationCacheOperationSourceTests.CustomService.class, name.getMethodName(), Long.class);
        Assert.assertEquals(defaultCacheResolver, operation.getCacheResolver());
        Assert.assertNull(operation.getExceptionCacheResolver());
        KeyGeneratorAdapter adapter = ((KeyGeneratorAdapter) (operation.getKeyGenerator()));
        Assert.assertSame(bean, adapter.getTarget());// take bean from context

    }

    @Test
    public void customKeyGeneratorAndCacheResolver() {
        CacheResultOperation operation = getCacheOperation(CacheResultOperation.class, AnnotationCacheOperationSourceTests.CustomServiceWithDefaults.class, name.getMethodName(), Long.class);
        assertJCacheResolver(operation.getCacheResolver(), TestableCacheResolver.class);
        assertJCacheResolver(operation.getExceptionCacheResolver(), null);
        assertCacheKeyGenerator(operation.getKeyGenerator(), TestableCacheKeyGenerator.class);
    }

    @Test
    public void customKeyGeneratorAndCacheResolverWithExceptionName() {
        CacheResultOperation operation = getCacheOperation(CacheResultOperation.class, AnnotationCacheOperationSourceTests.CustomServiceWithDefaults.class, name.getMethodName(), Long.class);
        assertJCacheResolver(operation.getCacheResolver(), TestableCacheResolver.class);
        assertJCacheResolver(operation.getExceptionCacheResolver(), TestableCacheResolver.class);
        assertCacheKeyGenerator(operation.getKeyGenerator(), TestableCacheKeyGenerator.class);
    }

    static class CustomService {
        @CacheResult(cacheKeyGenerator = TestableCacheKeyGenerator.class)
        public Object customKeyGenerator(Long id) {
            return null;
        }

        @CacheResult(cacheKeyGenerator = TestableCacheKeyGenerator.class)
        public Object customKeyGeneratorSpringBean(Long id) {
            return null;
        }

        @CacheResult(cacheResolverFactory = TestableCacheResolverFactory.class)
        public Object customCacheResolver(Long id) {
            return null;
        }
    }

    @CacheDefaults(cacheResolverFactory = TestableCacheResolverFactory.class, cacheKeyGenerator = TestableCacheKeyGenerator.class)
    static class CustomServiceWithDefaults {
        @CacheResult
        public Object customKeyGeneratorAndCacheResolver(Long id) {
            return null;
        }

        @CacheResult(exceptionCacheName = "exception")
        public Object customKeyGeneratorAndCacheResolverWithExceptionName(Long id) {
            return null;
        }
    }

    static class InvalidCases {
        @CacheRemove
        @CacheRemoveAll
        public void multiAnnotations() {
        }
    }
}

