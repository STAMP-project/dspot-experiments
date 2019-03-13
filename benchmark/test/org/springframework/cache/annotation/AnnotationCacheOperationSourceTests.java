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
package org.springframework.cache.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.cache.interceptor.CacheEvictOperation;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.cache.interceptor.CacheableOperation;
import org.springframework.core.annotation.AliasFor;


/**
 *
 *
 * @author Costin Leau
 * @author Stephane Nicoll
 * @author Sam Brannen
 */
public class AnnotationCacheOperationSourceTests {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private final AnnotationCacheOperationSource source = new AnnotationCacheOperationSource();

    @Test
    public void singularAnnotation() {
        Collection<CacheOperation> ops = getOps(AnnotationCacheOperationSourceTests.AnnotatedClass.class, "singular", 1);
        Assert.assertTrue(((ops.iterator().next()) instanceof CacheableOperation));
    }

    @Test
    public void multipleAnnotation() {
        Collection<CacheOperation> ops = getOps(AnnotationCacheOperationSourceTests.AnnotatedClass.class, "multiple", 2);
        Iterator<CacheOperation> it = ops.iterator();
        Assert.assertTrue(((it.next()) instanceof CacheableOperation));
        Assert.assertTrue(((it.next()) instanceof CacheEvictOperation));
    }

    @Test
    public void caching() {
        Collection<CacheOperation> ops = getOps(AnnotationCacheOperationSourceTests.AnnotatedClass.class, "caching", 2);
        Iterator<CacheOperation> it = ops.iterator();
        Assert.assertTrue(((it.next()) instanceof CacheableOperation));
        Assert.assertTrue(((it.next()) instanceof CacheEvictOperation));
    }

    @Test
    public void emptyCaching() {
        getOps(AnnotationCacheOperationSourceTests.AnnotatedClass.class, "emptyCaching", 0);
    }

    @Test
    public void singularStereotype() {
        Collection<CacheOperation> ops = getOps(AnnotationCacheOperationSourceTests.AnnotatedClass.class, "singleStereotype", 1);
        Assert.assertTrue(((ops.iterator().next()) instanceof CacheEvictOperation));
    }

    @Test
    public void multipleStereotypes() {
        Collection<CacheOperation> ops = getOps(AnnotationCacheOperationSourceTests.AnnotatedClass.class, "multipleStereotype", 3);
        Iterator<CacheOperation> it = ops.iterator();
        Assert.assertTrue(((it.next()) instanceof CacheableOperation));
        CacheOperation next = it.next();
        Assert.assertTrue((next instanceof CacheEvictOperation));
        Assert.assertTrue(next.getCacheNames().contains("foo"));
        next = it.next();
        Assert.assertTrue((next instanceof CacheEvictOperation));
        Assert.assertTrue(next.getCacheNames().contains("bar"));
    }

    @Test
    public void singleComposedAnnotation() {
        Collection<CacheOperation> ops = getOps(AnnotationCacheOperationSourceTests.AnnotatedClass.class, "singleComposed", 2);
        Iterator<CacheOperation> it = ops.iterator();
        CacheOperation cacheOperation = it.next();
        Assert.assertThat(cacheOperation, CoreMatchers.instanceOf(CacheableOperation.class));
        Assert.assertThat(cacheOperation.getCacheNames(), CoreMatchers.equalTo(Collections.singleton("directly declared")));
        Assert.assertThat(cacheOperation.getKey(), CoreMatchers.equalTo(""));
        cacheOperation = it.next();
        Assert.assertThat(cacheOperation, CoreMatchers.instanceOf(CacheableOperation.class));
        Assert.assertThat(cacheOperation.getCacheNames(), CoreMatchers.equalTo(Collections.singleton("composedCache")));
        Assert.assertThat(cacheOperation.getKey(), CoreMatchers.equalTo("composedKey"));
    }

    @Test
    public void multipleComposedAnnotations() {
        Collection<CacheOperation> ops = getOps(AnnotationCacheOperationSourceTests.AnnotatedClass.class, "multipleComposed", 4);
        Iterator<CacheOperation> it = ops.iterator();
        CacheOperation cacheOperation = it.next();
        Assert.assertThat(cacheOperation, CoreMatchers.instanceOf(CacheableOperation.class));
        Assert.assertThat(cacheOperation.getCacheNames(), CoreMatchers.equalTo(Collections.singleton("directly declared")));
        Assert.assertThat(cacheOperation.getKey(), CoreMatchers.equalTo(""));
        cacheOperation = it.next();
        Assert.assertThat(cacheOperation, CoreMatchers.instanceOf(CacheableOperation.class));
        Assert.assertThat(cacheOperation.getCacheNames(), CoreMatchers.equalTo(Collections.singleton("composedCache")));
        Assert.assertThat(cacheOperation.getKey(), CoreMatchers.equalTo("composedKey"));
        cacheOperation = it.next();
        Assert.assertThat(cacheOperation, CoreMatchers.instanceOf(CacheableOperation.class));
        Assert.assertThat(cacheOperation.getCacheNames(), CoreMatchers.equalTo(Collections.singleton("foo")));
        Assert.assertThat(cacheOperation.getKey(), CoreMatchers.equalTo(""));
        cacheOperation = it.next();
        Assert.assertThat(cacheOperation, CoreMatchers.instanceOf(CacheEvictOperation.class));
        Assert.assertThat(cacheOperation.getCacheNames(), CoreMatchers.equalTo(Collections.singleton("composedCacheEvict")));
        Assert.assertThat(cacheOperation.getKey(), CoreMatchers.equalTo("composedEvictionKey"));
    }

    @Test
    public void customKeyGenerator() {
        Collection<CacheOperation> ops = getOps(AnnotationCacheOperationSourceTests.AnnotatedClass.class, "customKeyGenerator", 1);
        CacheOperation cacheOperation = ops.iterator().next();
        Assert.assertEquals("Custom key generator not set", "custom", cacheOperation.getKeyGenerator());
    }

    @Test
    public void customKeyGeneratorInherited() {
        Collection<CacheOperation> ops = getOps(AnnotationCacheOperationSourceTests.AnnotatedClass.class, "customKeyGeneratorInherited", 1);
        CacheOperation cacheOperation = ops.iterator().next();
        Assert.assertEquals("Custom key generator not set", "custom", cacheOperation.getKeyGenerator());
    }

    @Test
    public void keyAndKeyGeneratorCannotBeSetTogether() {
        this.exception.expect(IllegalStateException.class);
        getOps(AnnotationCacheOperationSourceTests.AnnotatedClass.class, "invalidKeyAndKeyGeneratorSet");
    }

    @Test
    public void customCacheManager() {
        Collection<CacheOperation> ops = getOps(AnnotationCacheOperationSourceTests.AnnotatedClass.class, "customCacheManager", 1);
        CacheOperation cacheOperation = ops.iterator().next();
        Assert.assertEquals("Custom cache manager not set", "custom", cacheOperation.getCacheManager());
    }

    @Test
    public void customCacheManagerInherited() {
        Collection<CacheOperation> ops = getOps(AnnotationCacheOperationSourceTests.AnnotatedClass.class, "customCacheManagerInherited", 1);
        CacheOperation cacheOperation = ops.iterator().next();
        Assert.assertEquals("Custom cache manager not set", "custom", cacheOperation.getCacheManager());
    }

    @Test
    public void customCacheResolver() {
        Collection<CacheOperation> ops = getOps(AnnotationCacheOperationSourceTests.AnnotatedClass.class, "customCacheResolver", 1);
        CacheOperation cacheOperation = ops.iterator().next();
        Assert.assertEquals("Custom cache resolver not set", "custom", cacheOperation.getCacheResolver());
    }

    @Test
    public void customCacheResolverInherited() {
        Collection<CacheOperation> ops = getOps(AnnotationCacheOperationSourceTests.AnnotatedClass.class, "customCacheResolverInherited", 1);
        CacheOperation cacheOperation = ops.iterator().next();
        Assert.assertEquals("Custom cache resolver not set", "custom", cacheOperation.getCacheResolver());
    }

    @Test
    public void cacheResolverAndCacheManagerCannotBeSetTogether() {
        this.exception.expect(IllegalStateException.class);
        getOps(AnnotationCacheOperationSourceTests.AnnotatedClass.class, "invalidCacheResolverAndCacheManagerSet");
    }

    @Test
    public void fullClassLevelWithCustomCacheName() {
        Collection<CacheOperation> ops = getOps(AnnotationCacheOperationSourceTests.AnnotatedClassWithFullDefault.class, "methodLevelCacheName", 1);
        CacheOperation cacheOperation = ops.iterator().next();
        assertSharedConfig(cacheOperation, "classKeyGenerator", "", "classCacheResolver", "custom");
    }

    @Test
    public void fullClassLevelWithCustomKeyManager() {
        Collection<CacheOperation> ops = getOps(AnnotationCacheOperationSourceTests.AnnotatedClassWithFullDefault.class, "methodLevelKeyGenerator", 1);
        CacheOperation cacheOperation = ops.iterator().next();
        assertSharedConfig(cacheOperation, "custom", "", "classCacheResolver", "classCacheName");
    }

    @Test
    public void fullClassLevelWithCustomCacheManager() {
        Collection<CacheOperation> ops = getOps(AnnotationCacheOperationSourceTests.AnnotatedClassWithFullDefault.class, "methodLevelCacheManager", 1);
        CacheOperation cacheOperation = ops.iterator().next();
        assertSharedConfig(cacheOperation, "classKeyGenerator", "custom", "", "classCacheName");
    }

    @Test
    public void fullClassLevelWithCustomCacheResolver() {
        Collection<CacheOperation> ops = getOps(AnnotationCacheOperationSourceTests.AnnotatedClassWithFullDefault.class, "methodLevelCacheResolver", 1);
        CacheOperation cacheOperation = ops.iterator().next();
        assertSharedConfig(cacheOperation, "classKeyGenerator", "", "custom", "classCacheName");
    }

    @Test
    public void validateNoCacheIsValid() {
        // Valid as a CacheResolver might return the cache names to use with other info
        Collection<CacheOperation> ops = getOps(AnnotationCacheOperationSourceTests.AnnotatedClass.class, "noCacheNameSpecified");
        CacheOperation cacheOperation = ops.iterator().next();
        Assert.assertNotNull("cache names set must not be null", cacheOperation.getCacheNames());
        Assert.assertEquals("no cache names specified", 0, cacheOperation.getCacheNames().size());
    }

    @Test
    public void customClassLevelWithCustomCacheName() {
        Collection<CacheOperation> ops = getOps(AnnotationCacheOperationSourceTests.AnnotatedClassWithCustomDefault.class, "methodLevelCacheName", 1);
        CacheOperation cacheOperation = ops.iterator().next();
        assertSharedConfig(cacheOperation, "classKeyGenerator", "", "classCacheResolver", "custom");
    }

    @Test
    public void severalCacheConfigUseClosest() {
        Collection<CacheOperation> ops = getOps(AnnotationCacheOperationSourceTests.MultipleCacheConfig.class, "multipleCacheConfig");
        CacheOperation cacheOperation = ops.iterator().next();
        assertSharedConfig(cacheOperation, "", "", "", "myCache");
    }

    @Test
    public void cacheConfigFromInterface() {
        Collection<CacheOperation> ops = getOps(AnnotationCacheOperationSourceTests.InterfaceCacheConfig.class, "interfaceCacheConfig");
        CacheOperation cacheOperation = ops.iterator().next();
        assertSharedConfig(cacheOperation, "", "", "", "myCache");
    }

    @Test
    public void cacheAnnotationOverride() {
        Collection<CacheOperation> ops = getOps(AnnotationCacheOperationSourceTests.InterfaceCacheConfig.class, "interfaceCacheableOverride");
        Assert.assertSame(1, ops.size());
        CacheOperation cacheOperation = ops.iterator().next();
        Assert.assertTrue((cacheOperation instanceof CacheableOperation));
    }

    @Test
    public void partialClassLevelWithCustomCacheManager() {
        Collection<CacheOperation> ops = getOps(AnnotationCacheOperationSourceTests.AnnotatedClassWithSomeDefault.class, "methodLevelCacheManager", 1);
        CacheOperation cacheOperation = ops.iterator().next();
        assertSharedConfig(cacheOperation, "classKeyGenerator", "custom", "", "classCacheName");
    }

    @Test
    public void partialClassLevelWithCustomCacheResolver() {
        Collection<CacheOperation> ops = getOps(AnnotationCacheOperationSourceTests.AnnotatedClassWithSomeDefault.class, "methodLevelCacheResolver", 1);
        CacheOperation cacheOperation = ops.iterator().next();
        assertSharedConfig(cacheOperation, "classKeyGenerator", "", "custom", "classCacheName");
    }

    @Test
    public void partialClassLevelWithNoCustomization() {
        Collection<CacheOperation> ops = getOps(AnnotationCacheOperationSourceTests.AnnotatedClassWithSomeDefault.class, "noCustomization", 1);
        CacheOperation cacheOperation = ops.iterator().next();
        assertSharedConfig(cacheOperation, "classKeyGenerator", "classCacheManager", "", "classCacheName");
    }

    private static class AnnotatedClass {
        @Cacheable("test")
        public void singular() {
        }

        @CacheEvict("test")
        @Cacheable("test")
        public void multiple() {
        }

        @Caching(cacheable = @Cacheable("test"), evict = @CacheEvict("test"))
        public void caching() {
        }

        @Caching
        public void emptyCaching() {
        }

        @Cacheable(cacheNames = "test", keyGenerator = "custom")
        public void customKeyGenerator() {
        }

        @Cacheable(cacheNames = "test", cacheManager = "custom")
        public void customCacheManager() {
        }

        @Cacheable(cacheNames = "test", cacheResolver = "custom")
        public void customCacheResolver() {
        }

        @AnnotationCacheOperationSourceTests.EvictFoo
        public void singleStereotype() {
        }

        @AnnotationCacheOperationSourceTests.EvictFoo
        @AnnotationCacheOperationSourceTests.CacheableFoo
        @AnnotationCacheOperationSourceTests.EvictBar
        public void multipleStereotype() {
        }

        @Cacheable("directly declared")
        @AnnotationCacheOperationSourceTests.ComposedCacheable(cacheNames = "composedCache", key = "composedKey")
        public void singleComposed() {
        }

        @Cacheable("directly declared")
        @AnnotationCacheOperationSourceTests.ComposedCacheable(cacheNames = "composedCache", key = "composedKey")
        @AnnotationCacheOperationSourceTests.CacheableFoo
        @AnnotationCacheOperationSourceTests.ComposedCacheEvict(cacheNames = "composedCacheEvict", key = "composedEvictionKey")
        public void multipleComposed() {
        }

        @Caching(cacheable = { @Cacheable(cacheNames = "test", key = "a"), @Cacheable(cacheNames = "test", key = "b") })
        public void multipleCaching() {
        }

        @AnnotationCacheOperationSourceTests.CacheableFooCustomKeyGenerator
        public void customKeyGeneratorInherited() {
        }

        @Cacheable(cacheNames = "test", key = "#root.methodName", keyGenerator = "custom")
        public void invalidKeyAndKeyGeneratorSet() {
        }

        @AnnotationCacheOperationSourceTests.CacheableFooCustomCacheManager
        public void customCacheManagerInherited() {
        }

        @AnnotationCacheOperationSourceTests.CacheableFooCustomCacheResolver
        public void customCacheResolverInherited() {
        }

        @Cacheable(cacheNames = "test", cacheManager = "custom", cacheResolver = "custom")
        public void invalidCacheResolverAndCacheManagerSet() {
        }

        // cache name can be inherited from CacheConfig. There's none here
        @Cacheable
        public void noCacheNameSpecified() {
        }
    }

    @CacheConfig(cacheNames = "classCacheName", keyGenerator = "classKeyGenerator", cacheManager = "classCacheManager", cacheResolver = "classCacheResolver")
    private static class AnnotatedClassWithFullDefault {
        @Cacheable("custom")
        public void methodLevelCacheName() {
        }

        @Cacheable(keyGenerator = "custom")
        public void methodLevelKeyGenerator() {
        }

        @Cacheable(cacheManager = "custom")
        public void methodLevelCacheManager() {
        }

        @Cacheable(cacheResolver = "custom")
        public void methodLevelCacheResolver() {
        }
    }

    @AnnotationCacheOperationSourceTests.CacheConfigFoo
    private static class AnnotatedClassWithCustomDefault {
        @Cacheable("custom")
        public void methodLevelCacheName() {
        }
    }

    @CacheConfig(cacheNames = "classCacheName", keyGenerator = "classKeyGenerator", cacheManager = "classCacheManager")
    private static class AnnotatedClassWithSomeDefault {
        @Cacheable(cacheManager = "custom")
        public void methodLevelCacheManager() {
        }

        @Cacheable(cacheResolver = "custom")
        public void methodLevelCacheResolver() {
        }

        @Cacheable
        public void noCustomization() {
        }
    }

    // multiple sources
    @AnnotationCacheOperationSourceTests.CacheConfigFoo
    @CacheConfig(cacheNames = "myCache")
    private static class MultipleCacheConfig {
        @Cacheable
        public void multipleCacheConfig() {
        }
    }

    @CacheConfig(cacheNames = "myCache")
    private interface CacheConfigIfc {
        @Cacheable
        void interfaceCacheConfig();

        @CachePut
        void interfaceCacheableOverride();
    }

    private static class InterfaceCacheConfig implements AnnotationCacheOperationSourceTests.CacheConfigIfc {
        @Override
        public void interfaceCacheConfig() {
        }

        @Override
        @Cacheable
        public void interfaceCacheableOverride() {
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Cacheable("foo")
    public @interface CacheableFoo {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Cacheable(cacheNames = "foo", keyGenerator = "custom")
    public @interface CacheableFooCustomKeyGenerator {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Cacheable(cacheNames = "foo", cacheManager = "custom")
    public @interface CacheableFooCustomCacheManager {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Cacheable(cacheNames = "foo", cacheResolver = "custom")
    public @interface CacheableFooCustomCacheResolver {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @CacheEvict("foo")
    public @interface EvictFoo {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @CacheEvict("bar")
    public @interface EvictBar {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @CacheConfig(keyGenerator = "classKeyGenerator", cacheManager = "classCacheManager", cacheResolver = "classCacheResolver")
    public @interface CacheConfigFoo {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Cacheable(cacheNames = "shadowed cache name", key = "shadowed key")
    @interface ComposedCacheable {
        @AliasFor(annotation = Cacheable.class)
        String[] value() default {  };

        @AliasFor(annotation = Cacheable.class)
        String[] cacheNames() default {  };

        @AliasFor(annotation = Cacheable.class)
        String key() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.TYPE })
    @CacheEvict(cacheNames = "shadowed cache name", key = "shadowed key")
    @interface ComposedCacheEvict {
        @AliasFor(annotation = CacheEvict.class)
        String[] value() default {  };

        @AliasFor(annotation = CacheEvict.class)
        String[] cacheNames() default {  };

        @AliasFor(annotation = CacheEvict.class)
        String key() default "";
    }
}

