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
package org.springframework.cache.aspectj;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.cache.CacheManager;
import org.springframework.cache.CacheTestUtils;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.config.AnnotatedClassCacheableService;
import org.springframework.cache.config.CacheableService;
import org.springframework.cache.config.DefaultCacheableService;
import org.springframework.cache.config.SomeCustomKeyGenerator;
import org.springframework.cache.config.SomeKeyGenerator;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 *
 *
 * @author Stephane Nicoll
 */
public class AspectJEnableCachingIsolatedTests {
    private ConfigurableApplicationContext ctx;

    @Test
    public void testKeyStrategy() {
        load(AspectJEnableCachingIsolatedTests.EnableCachingConfig.class);
        AnnotationCacheAspect aspect = this.ctx.getBean(AnnotationCacheAspect.class);
        Assert.assertSame(this.ctx.getBean("keyGenerator", KeyGenerator.class), aspect.getKeyGenerator());
    }

    @Test
    public void testCacheErrorHandler() {
        load(AspectJEnableCachingIsolatedTests.EnableCachingConfig.class);
        AnnotationCacheAspect aspect = this.ctx.getBean(AnnotationCacheAspect.class);
        Assert.assertSame(this.ctx.getBean("errorHandler", CacheErrorHandler.class), aspect.getErrorHandler());
    }

    // --- local tests -------
    @Test
    public void singleCacheManagerBean() {
        load(AspectJEnableCachingIsolatedTests.SingleCacheManagerConfig.class);
    }

    @Test
    public void multipleCacheManagerBeans() {
        try {
            load(AspectJEnableCachingIsolatedTests.MultiCacheManagerConfig.class);
        } catch (IllegalStateException ex) {
            Assert.assertTrue(ex.getMessage().contains("bean of type CacheManager"));
        }
    }

    @Test
    public void multipleCacheManagerBeans_implementsCachingConfigurer() {
        load(AspectJEnableCachingIsolatedTests.MultiCacheManagerConfigurer.class);// does not throw

    }

    @Test
    public void multipleCachingConfigurers() {
        try {
            load(AspectJEnableCachingIsolatedTests.MultiCacheManagerConfigurer.class, AspectJEnableCachingIsolatedTests.EnableCachingConfig.class);
        } catch (BeanCreationException ex) {
            Throwable root = ex.getRootCause();
            Assert.assertTrue((root instanceof IllegalStateException));
            Assert.assertTrue(ex.getMessage().contains("implementations of CachingConfigurer"));
        }
    }

    @Test
    public void noCacheManagerBeans() {
        try {
            load(AspectJEnableCachingIsolatedTests.EmptyConfig.class);
        } catch (IllegalStateException ex) {
            Assert.assertTrue(ex.getMessage().contains("no bean of type CacheManager"));
        }
    }

    @Test
    public void bothSetOnlyResolverIsUsed() {
        load(AspectJEnableCachingIsolatedTests.FullCachingConfig.class);
        AnnotationCacheAspect aspect = this.ctx.getBean(AnnotationCacheAspect.class);
        Assert.assertSame(this.ctx.getBean("cacheResolver"), aspect.getCacheResolver());
        Assert.assertSame(this.ctx.getBean("keyGenerator"), aspect.getKeyGenerator());
    }

    @Configuration
    @EnableCaching(mode = AdviceMode.ASPECTJ)
    static class EnableCachingConfig extends CachingConfigurerSupport {
        @Override
        @Bean
        public CacheManager cacheManager() {
            return CacheTestUtils.createSimpleCacheManager("testCache", "primary", "secondary");
        }

        @Bean
        public CacheableService<?> service() {
            return new DefaultCacheableService();
        }

        @Bean
        public CacheableService<?> classService() {
            return new AnnotatedClassCacheableService();
        }

        @Override
        @Bean
        public KeyGenerator keyGenerator() {
            return new SomeKeyGenerator();
        }

        @Override
        @Bean
        public CacheErrorHandler errorHandler() {
            return new SimpleCacheErrorHandler();
        }

        @Bean
        public KeyGenerator customKeyGenerator() {
            return new SomeCustomKeyGenerator();
        }

        @Bean
        public CacheManager customCacheManager() {
            return CacheTestUtils.createSimpleCacheManager("testCache");
        }
    }

    @Configuration
    @EnableCaching(mode = AdviceMode.ASPECTJ)
    static class EmptyConfig {}

    @Configuration
    @EnableCaching(mode = AdviceMode.ASPECTJ)
    static class SingleCacheManagerConfig {
        @Bean
        public CacheManager cm1() {
            return new NoOpCacheManager();
        }
    }

    @Configuration
    @EnableCaching(mode = AdviceMode.ASPECTJ)
    static class MultiCacheManagerConfig {
        @Bean
        public CacheManager cm1() {
            return new NoOpCacheManager();
        }

        @Bean
        public CacheManager cm2() {
            return new NoOpCacheManager();
        }
    }

    @Configuration
    @EnableCaching(mode = AdviceMode.ASPECTJ)
    static class MultiCacheManagerConfigurer extends CachingConfigurerSupport {
        @Bean
        public CacheManager cm1() {
            return new NoOpCacheManager();
        }

        @Bean
        public CacheManager cm2() {
            return new NoOpCacheManager();
        }

        @Override
        public CacheManager cacheManager() {
            return cm1();
        }

        @Override
        public KeyGenerator keyGenerator() {
            return null;
        }
    }

    @Configuration
    @EnableCaching(mode = AdviceMode.ASPECTJ)
    static class EmptyConfigSupportConfig extends CachingConfigurerSupport {
        @Bean
        public CacheManager cm() {
            return new NoOpCacheManager();
        }
    }

    @Configuration
    @EnableCaching(mode = AdviceMode.ASPECTJ)
    static class FullCachingConfig extends CachingConfigurerSupport {
        @Override
        @Bean
        public CacheManager cacheManager() {
            return new NoOpCacheManager();
        }

        @Override
        @Bean
        public KeyGenerator keyGenerator() {
            return new SomeKeyGenerator();
        }

        @Override
        @Bean
        public CacheResolver cacheResolver() {
            return new org.springframework.cache.interceptor.NamedCacheResolver(cacheManager(), "foo");
        }
    }
}

