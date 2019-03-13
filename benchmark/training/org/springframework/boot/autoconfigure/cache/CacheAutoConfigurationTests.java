/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.autoconfigure.cache;


import Status.STATUS_ALIVE;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.bucket.BucketManager;
import com.couchbase.client.spring.cache.CouchbaseCache;
import com.couchbase.client.spring.cache.CouchbaseCacheManager;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CaffeineSpec;
import com.hazelcast.cache.HazelcastCachingProvider;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.expiry.CreatedExpiryPolicy;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.jcache.embedded.JCachingProvider;
import org.infinispan.spring.provider.SpringEmbeddedCacheManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.cache.support.MockCachingProvider;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import org.springframework.boot.testsupport.runner.classpath.ClassPathExclusions;
import org.springframework.boot.testsupport.runner.classpath.ModifiedClassPathRunner;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.javax.cache.CacheManager;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;


/**
 * Tests for {@link CacheAutoConfiguration}.
 *
 * @author Stephane Nicoll
 * @author Edd? Mel?ndez
 * @author Mark Paluch
 * @author Ryon Day
 */
@RunWith(ModifiedClassPathRunner.class)
@ClassPathExclusions("hazelcast-client-*.jar")
public class CacheAutoConfigurationTests extends AbstractCacheAutoConfigurationTests {
    @Test
    public void noEnableCaching() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.EmptyConfiguration.class).run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void cacheManagerBackOff() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.CustomCacheManagerConfiguration.class).run(( context) -> assertThat(getCacheManager(context, .class).getCacheNames()).containsOnly("custom1"));
    }

    @Test
    public void cacheManagerFromSupportBackOff() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.CustomCacheManagerFromSupportConfiguration.class).run(( context) -> assertThat(getCacheManager(context, .class).getCacheNames()).containsOnly("custom1"));
    }

    @Test
    public void cacheResolverFromSupportBackOff() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.CustomCacheResolverFromSupportConfiguration.class).run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void customCacheResolverCanBeDefined() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.SpecificCacheResolverConfiguration.class).withPropertyValues("spring.cache.type=simple").run(( context) -> {
            getCacheManager(context, .class);
            assertThat(context).hasSingleBean(.class);
        });
    }

    @Test
    public void notSupportedCachingMode() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.DefaultCacheConfiguration.class).withPropertyValues("spring.cache.type=foobar").run(( context) -> assertThat(context).getFailure().isInstanceOf(.class).hasMessageContaining("Failed to bind properties under 'spring.cache.type'"));
    }

    @Test
    public void simpleCacheExplicit() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.DefaultCacheConfiguration.class).withPropertyValues("spring.cache.type=simple").run(( context) -> assertThat(getCacheManager(context, .class).getCacheNames()).isEmpty());
    }

    @Test
    public void simpleCacheWithCustomizers() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.DefaultCacheAndCustomizersConfiguration.class).withPropertyValues(("spring.cache.type=" + "simple")).run(verifyCustomizers("allCacheManagerCustomizer", "simpleCacheManagerCustomizer"));
    }

    @Test
    public void simpleCacheExplicitWithCacheNames() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.DefaultCacheConfiguration.class).withPropertyValues("spring.cache.type=simple", "spring.cache.cacheNames[0]=foo", "spring.cache.cacheNames[1]=bar").run(( context) -> {
            ConcurrentMapCacheManager cacheManager = getCacheManager(context, .class);
            assertThat(cacheManager.getCacheNames()).containsOnly("foo", "bar");
        });
    }

    @Test
    public void genericCacheWithCaches() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.GenericCacheConfiguration.class).run(( context) -> {
            SimpleCacheManager cacheManager = getCacheManager(context, .class);
            assertThat(cacheManager.getCache("first")).isEqualTo(context.getBean("firstCache"));
            assertThat(cacheManager.getCache("second")).isEqualTo(context.getBean("secondCache"));
            assertThat(cacheManager.getCacheNames()).hasSize(2);
        });
    }

    @Test
    public void genericCacheExplicit() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.DefaultCacheConfiguration.class).withPropertyValues("spring.cache.type=generic").run(( context) -> assertThat(context).getFailure().isInstanceOf(.class).hasMessageContaining("No cache manager could be auto-configured").hasMessageContaining("GENERIC"));
    }

    @Test
    public void genericCacheWithCustomizers() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.GenericCacheAndCustomizersConfiguration.class).withPropertyValues(("spring.cache.type=" + "generic")).run(verifyCustomizers("allCacheManagerCustomizer", "genericCacheManagerCustomizer"));
    }

    @Test
    public void genericCacheExplicitWithCaches() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.GenericCacheConfiguration.class).withPropertyValues("spring.cache.type=generic").run(( context) -> {
            SimpleCacheManager cacheManager = getCacheManager(context, .class);
            assertThat(cacheManager.getCache("first")).isEqualTo(context.getBean("firstCache"));
            assertThat(cacheManager.getCache("second")).isEqualTo(context.getBean("secondCache"));
            assertThat(cacheManager.getCacheNames()).hasSize(2);
        });
    }

    @Test
    public void couchbaseCacheExplicit() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.CouchbaseCacheConfiguration.class).withPropertyValues("spring.cache.type=couchbase").run(( context) -> {
            CouchbaseCacheManager cacheManager = getCacheManager(context, .class);
            assertThat(cacheManager.getCacheNames()).isEmpty();
        });
    }

    @Test
    public void couchbaseCacheWithCustomizers() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.CouchbaseCacheAndCustomizersConfiguration.class).withPropertyValues(("spring.cache.type=" + "couchbase")).run(verifyCustomizers("allCacheManagerCustomizer", "couchbaseCacheManagerCustomizer"));
    }

    @Test
    public void couchbaseCacheExplicitWithCaches() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.CouchbaseCacheConfiguration.class).withPropertyValues("spring.cache.type=couchbase", "spring.cache.cacheNames[0]=foo", "spring.cache.cacheNames[1]=bar").run(( context) -> {
            CouchbaseCacheManager cacheManager = getCacheManager(context, .class);
            assertThat(cacheManager.getCacheNames()).containsOnly("foo", "bar");
            Cache cache = cacheManager.getCache("foo");
            assertThat(cache).isInstanceOf(.class);
            assertThat(((CouchbaseCache) (cache)).getTtl()).isEqualTo(0);
            assertThat(((CouchbaseCache) (cache)).getNativeCache()).isEqualTo(context.getBean("bucket"));
        });
    }

    @Test
    public void couchbaseCacheExplicitWithTtl() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.CouchbaseCacheConfiguration.class).withPropertyValues("spring.cache.type=couchbase", "spring.cache.cacheNames=foo,bar", "spring.cache.couchbase.expiration=2000").run(( context) -> {
            CouchbaseCacheManager cacheManager = getCacheManager(context, .class);
            assertThat(cacheManager.getCacheNames()).containsOnly("foo", "bar");
            Cache cache = cacheManager.getCache("foo");
            assertThat(cache).isInstanceOf(.class);
            assertThat(((CouchbaseCache) (cache)).getTtl()).isEqualTo(2);
            assertThat(((CouchbaseCache) (cache)).getNativeCache()).isEqualTo(context.getBean("bucket"));
        });
    }

    @Test
    public void redisCacheExplicit() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.RedisConfiguration.class).withPropertyValues("spring.cache.type=redis", "spring.cache.redis.time-to-live=15000", "spring.cache.redis.cacheNullValues=false", "spring.cache.redis.keyPrefix=prefix", "spring.cache.redis.useKeyPrefix=true").run(( context) -> {
            RedisCacheManager cacheManager = getCacheManager(context, .class);
            assertThat(cacheManager.getCacheNames()).isEmpty();
            RedisCacheConfiguration redisCacheConfiguration = getDefaultRedisCacheConfiguration(cacheManager);
            assertThat(redisCacheConfiguration.getTtl()).isEqualTo(java.time.Duration.ofSeconds(15));
            assertThat(redisCacheConfiguration.getAllowCacheNullValues()).isFalse();
            assertThat(redisCacheConfiguration.getKeyPrefixFor("keyName")).isEqualTo("prefix");
            assertThat(redisCacheConfiguration.usePrefix()).isTrue();
        });
    }

    @Test
    public void redisCacheWithRedisCacheConfiguration() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.RedisWithCacheConfigurationConfiguration.class).withPropertyValues("spring.cache.type=redis", "spring.cache.redis.time-to-live=15000", "spring.cache.redis.keyPrefix=foo").run(( context) -> {
            RedisCacheManager cacheManager = getCacheManager(context, .class);
            assertThat(cacheManager.getCacheNames()).isEmpty();
            RedisCacheConfiguration redisCacheConfiguration = getDefaultRedisCacheConfiguration(cacheManager);
            assertThat(redisCacheConfiguration.getTtl()).isEqualTo(java.time.Duration.ofSeconds(30));
            assertThat(redisCacheConfiguration.getKeyPrefixFor("")).isEqualTo("bar");
        });
    }

    @Test
    public void redisCacheWithCustomizers() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.RedisWithCustomizersConfiguration.class).withPropertyValues(("spring.cache.type=" + "redis")).run(verifyCustomizers("allCacheManagerCustomizer", "redisCacheManagerCustomizer"));
    }

    @Test
    public void redisCacheExplicitWithCaches() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.RedisConfiguration.class).withPropertyValues("spring.cache.type=redis", "spring.cache.cacheNames[0]=foo", "spring.cache.cacheNames[1]=bar").run(( context) -> {
            RedisCacheManager cacheManager = getCacheManager(context, .class);
            assertThat(cacheManager.getCacheNames()).containsOnly("foo", "bar");
            RedisCacheConfiguration redisCacheConfiguration = getDefaultRedisCacheConfiguration(cacheManager);
            assertThat(redisCacheConfiguration.getTtl()).isEqualTo(java.time.Duration.ofMinutes(0));
            assertThat(redisCacheConfiguration.getAllowCacheNullValues()).isTrue();
            assertThat(redisCacheConfiguration.getKeyPrefixFor("test")).isEqualTo("test::");
            assertThat(redisCacheConfiguration.usePrefix()).isTrue();
        });
    }

    @Test
    public void noOpCacheExplicit() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.DefaultCacheConfiguration.class).withPropertyValues("spring.cache.type=none").run(( context) -> {
            NoOpCacheManager cacheManager = getCacheManager(context, .class);
            assertThat(cacheManager.getCacheNames()).isEmpty();
        });
    }

    @Test
    public void jCacheCacheNoProviderExplicit() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.DefaultCacheConfiguration.class).withPropertyValues("spring.cache.type=jcache").run(( context) -> assertThat(context).getFailure().isInstanceOf(.class).hasMessageContaining("No cache manager could be auto-configured").hasMessageContaining("JCACHE"));
    }

    @Test
    public void jCacheCacheWithProvider() {
        String cachingProviderFqn = MockCachingProvider.class.getName();
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.DefaultCacheConfiguration.class).withPropertyValues("spring.cache.type=jcache", ("spring.cache.jcache.provider=" + cachingProviderFqn)).run(( context) -> {
            JCacheCacheManager cacheManager = getCacheManager(context, .class);
            assertThat(cacheManager.getCacheNames()).isEmpty();
            assertThat(context.getBean(.class)).isEqualTo(cacheManager.getCacheManager());
        });
    }

    @Test
    public void jCacheCacheWithCaches() {
        String cachingProviderFqn = MockCachingProvider.class.getName();
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.DefaultCacheConfiguration.class).withPropertyValues("spring.cache.type=jcache", ("spring.cache.jcache.provider=" + cachingProviderFqn), "spring.cache.cacheNames[0]=foo", "spring.cache.cacheNames[1]=bar").run(( context) -> {
            JCacheCacheManager cacheManager = getCacheManager(context, .class);
            assertThat(cacheManager.getCacheNames()).containsOnly("foo", "bar");
        });
    }

    @Test
    public void jCacheCacheWithCachesAndCustomConfig() {
        String cachingProviderFqn = MockCachingProvider.class.getName();
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.JCacheCustomConfiguration.class).withPropertyValues("spring.cache.type=jcache", ("spring.cache.jcache.provider=" + cachingProviderFqn), "spring.cache.cacheNames[0]=one", "spring.cache.cacheNames[1]=two").run(( context) -> {
            JCacheCacheManager cacheManager = getCacheManager(context, .class);
            assertThat(cacheManager.getCacheNames()).containsOnly("one", "two");
            CompleteConfiguration<?, ?> defaultCacheConfiguration = context.getBean(.class);
            verify(cacheManager.getCacheManager()).createCache("one", defaultCacheConfiguration);
            verify(cacheManager.getCacheManager()).createCache("two", defaultCacheConfiguration);
        });
    }

    @Test
    public void jCacheCacheWithExistingJCacheManager() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.JCacheCustomCacheManager.class).withPropertyValues("spring.cache.type=jcache").run(( context) -> {
            JCacheCacheManager cacheManager = getCacheManager(context, .class);
            assertThat(cacheManager.getCacheManager()).isEqualTo(context.getBean("customJCacheCacheManager"));
        });
    }

    @Test
    public void jCacheCacheWithUnknownProvider() {
        String wrongCachingProviderClassName = "org.acme.FooBar";
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.DefaultCacheConfiguration.class).withPropertyValues("spring.cache.type=jcache", ("spring.cache.jcache.provider=" + wrongCachingProviderClassName)).run(( context) -> assertThat(context).getFailure().isInstanceOf(.class).hasMessageContaining(wrongCachingProviderClassName));
    }

    @Test
    public void jCacheCacheWithConfig() {
        String cachingProviderFqn = MockCachingProvider.class.getName();
        String configLocation = "org/springframework/boot/autoconfigure/hazelcast/hazelcast-specific.xml";
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.JCacheCustomConfiguration.class).withPropertyValues("spring.cache.type=jcache", ("spring.cache.jcache.provider=" + cachingProviderFqn), ("spring.cache.jcache.config=" + configLocation)).run(( context) -> {
            JCacheCacheManager cacheManager = getCacheManager(context, .class);
            Resource configResource = new ClassPathResource(configLocation);
            assertThat(cacheManager.getCacheManager().getURI()).isEqualTo(configResource.getURI());
        });
    }

    @Test
    public void jCacheCacheWithWrongConfig() {
        String cachingProviderFqn = MockCachingProvider.class.getName();
        String configLocation = "org/springframework/boot/autoconfigure/cache/does-not-exist.xml";
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.JCacheCustomConfiguration.class).withPropertyValues("spring.cache.type=jcache", ("spring.cache.jcache.provider=" + cachingProviderFqn), ("spring.cache.jcache.config=" + configLocation)).run(( context) -> assertThat(context).getFailure().isInstanceOf(.class).hasMessageContaining("does not exist").hasMessageContaining(configLocation));
    }

    @Test
    public void jCacheCacheUseBeanClassLoader() {
        String cachingProviderFqn = MockCachingProvider.class.getName();
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.DefaultCacheConfiguration.class).withPropertyValues("spring.cache.type=jcache", ("spring.cache.jcache.provider=" + cachingProviderFqn)).run(( context) -> {
            JCacheCacheManager cacheManager = getCacheManager(context, .class);
            assertThat(cacheManager.getCacheManager().getClassLoader()).isEqualTo(context.getClassLoader());
        });
    }

    @Test
    public void hazelcastCacheExplicit() {
        this.contextRunner.withConfiguration(AutoConfigurations.of(HazelcastAutoConfiguration.class)).withUserConfiguration(CacheAutoConfigurationTests.DefaultCacheConfiguration.class).withPropertyValues("spring.cache.type=hazelcast").run(( context) -> {
            HazelcastCacheManager cacheManager = getCacheManager(context, .class);
            // NOTE: the hazelcast implementation knows about a cache in a lazy
            // manner.
            cacheManager.getCache("defaultCache");
            assertThat(cacheManager.getCacheNames()).containsOnly("defaultCache");
            assertThat(context.getBean(.class)).isEqualTo(cacheManager.getHazelcastInstance());
        });
    }

    @Test
    public void hazelcastCacheWithCustomizers() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.HazelcastCacheAndCustomizersConfiguration.class).withPropertyValues(("spring.cache.type=" + "hazelcast")).run(verifyCustomizers("allCacheManagerCustomizer", "hazelcastCacheManagerCustomizer"));
    }

    @Test
    public void hazelcastCacheWithExistingHazelcastInstance() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.HazelcastCustomHazelcastInstance.class).withPropertyValues("spring.cache.type=hazelcast").run(( context) -> {
            HazelcastCacheManager cacheManager = getCacheManager(context, .class);
            assertThat(cacheManager.getHazelcastInstance()).isEqualTo(context.getBean("customHazelcastInstance"));
        });
    }

    @Test
    public void hazelcastCacheWithHazelcastAutoConfiguration() {
        String hazelcastConfig = "org/springframework/boot/autoconfigure/hazelcast/hazelcast-specific.xml";
        this.contextRunner.withConfiguration(AutoConfigurations.of(HazelcastAutoConfiguration.class)).withUserConfiguration(CacheAutoConfigurationTests.DefaultCacheConfiguration.class).withPropertyValues("spring.cache.type=hazelcast", ("spring.hazelcast.config=" + hazelcastConfig)).run(( context) -> {
            HazelcastCacheManager cacheManager = getCacheManager(context, .class);
            HazelcastInstance hazelcastInstance = context.getBean(.class);
            assertThat(cacheManager.getHazelcastInstance()).isSameAs(hazelcastInstance);
            assertThat(hazelcastInstance.getConfig().getConfigurationFile()).isEqualTo(new ClassPathResource(hazelcastConfig).getFile());
            assertThat(cacheManager.getCache("foobar")).isNotNull();
            assertThat(cacheManager.getCacheNames()).containsOnly("foobar");
        });
    }

    @Test
    public void hazelcastAsJCacheWithCaches() {
        String cachingProviderFqn = HazelcastCachingProvider.class.getName();
        try {
            this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.DefaultCacheConfiguration.class).withPropertyValues("spring.cache.type=jcache", ("spring.cache.jcache.provider=" + cachingProviderFqn), "spring.cache.cacheNames[0]=foo", "spring.cache.cacheNames[1]=bar").run(( context) -> {
                JCacheCacheManager cacheManager = getCacheManager(context, .class);
                assertThat(cacheManager.getCacheNames()).containsOnly("foo", "bar");
                assertThat(Hazelcast.getAllHazelcastInstances()).hasSize(1);
            });
        } finally {
            Caching.getCachingProvider(cachingProviderFqn).close();
        }
    }

    @Test
    public void hazelcastAsJCacheWithConfig() {
        String cachingProviderFqn = HazelcastCachingProvider.class.getName();
        try {
            String configLocation = "org/springframework/boot/autoconfigure/hazelcast/hazelcast-specific.xml";
            this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.DefaultCacheConfiguration.class).withPropertyValues("spring.cache.type=jcache", ("spring.cache.jcache.provider=" + cachingProviderFqn), ("spring.cache.jcache.config=" + configLocation)).run(( context) -> {
                JCacheCacheManager cacheManager = getCacheManager(context, .class);
                Resource configResource = new ClassPathResource(configLocation);
                assertThat(cacheManager.getCacheManager().getURI()).isEqualTo(configResource.getURI());
                assertThat(Hazelcast.getAllHazelcastInstances()).hasSize(1);
            });
        } finally {
            Caching.getCachingProvider(cachingProviderFqn).close();
        }
    }

    @Test
    public void hazelcastAsJCacheWithExistingHazelcastInstance() {
        String cachingProviderFqn = HazelcastCachingProvider.class.getName();
        this.contextRunner.withConfiguration(AutoConfigurations.of(HazelcastAutoConfiguration.class)).withUserConfiguration(CacheAutoConfigurationTests.DefaultCacheConfiguration.class).withPropertyValues("spring.cache.type=jcache", ("spring.cache.jcache.provider=" + cachingProviderFqn)).run(( context) -> {
            JCacheCacheManager cacheManager = getCacheManager(context, .class);
            CacheManager jCacheManager = cacheManager.getCacheManager();
            assertThat(jCacheManager).isInstanceOf(.class);
            assertThat(context).hasSingleBean(.class);
            HazelcastInstance hazelcastInstance = context.getBean(.class);
            assertThat(((com.hazelcast.cache.HazelcastCacheManager) (jCacheManager)).getHazelcastInstance()).isSameAs(hazelcastInstance);
            assertThat(hazelcastInstance.getName()).isEqualTo("default-instance");
            assertThat(Hazelcast.getAllHazelcastInstances()).hasSize(1);
        });
    }

    @Test
    public void infinispanCacheWithConfig() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.DefaultCacheConfiguration.class).withPropertyValues("spring.cache.type=infinispan", "spring.cache.infinispan.config=infinispan.xml").run(( context) -> {
            SpringEmbeddedCacheManager cacheManager = getCacheManager(context, .class);
            assertThat(cacheManager.getCacheNames()).contains("foo", "bar");
        });
    }

    @Test
    public void infinispanCacheWithCustomizers() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.DefaultCacheAndCustomizersConfiguration.class).withPropertyValues(("spring.cache.type=" + "infinispan")).run(verifyCustomizers("allCacheManagerCustomizer", "infinispanCacheManagerCustomizer"));
    }

    @Test
    public void infinispanCacheWithCaches() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.DefaultCacheConfiguration.class).withPropertyValues("spring.cache.type=infinispan", "spring.cache.cacheNames[0]=foo", "spring.cache.cacheNames[1]=bar").run(( context) -> assertThat(getCacheManager(context, .class).getCacheNames()).containsOnly("foo", "bar"));
    }

    @Test
    public void infinispanCacheWithCachesAndCustomConfig() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.InfinispanCustomConfiguration.class).withPropertyValues("spring.cache.type=infinispan", "spring.cache.cacheNames[0]=foo", "spring.cache.cacheNames[1]=bar").run(( context) -> {
            assertThat(getCacheManager(context, .class).getCacheNames()).containsOnly("foo", "bar");
            verify(context.getBean(.class), times(2)).build();
        });
    }

    @Test
    public void infinispanAsJCacheWithCaches() {
        String cachingProviderClassName = JCachingProvider.class.getName();
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.DefaultCacheConfiguration.class).withPropertyValues("spring.cache.type=jcache", ("spring.cache.jcache.provider=" + cachingProviderClassName), "spring.cache.cacheNames[0]=foo", "spring.cache.cacheNames[1]=bar").run(( context) -> assertThat(getCacheManager(context, .class).getCacheNames()).containsOnly("foo", "bar"));
    }

    @Test
    public void infinispanAsJCacheWithConfig() {
        String cachingProviderClassName = JCachingProvider.class.getName();
        String configLocation = "infinispan.xml";
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.DefaultCacheConfiguration.class).withPropertyValues("spring.cache.type=jcache", ("spring.cache.jcache.provider=" + cachingProviderClassName), ("spring.cache.jcache.config=" + configLocation)).run(( context) -> {
            Resource configResource = new ClassPathResource(configLocation);
            assertThat(getCacheManager(context, .class).getCacheManager().getURI()).isEqualTo(configResource.getURI());
        });
    }

    @Test
    public void jCacheCacheWithCachesAndCustomizer() {
        String cachingProviderClassName = HazelcastCachingProvider.class.getName();
        try {
            this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.JCacheWithCustomizerConfiguration.class).withPropertyValues("spring.cache.type=jcache", ("spring.cache.jcache.provider=" + cachingProviderClassName), "spring.cache.cacheNames[0]=foo", "spring.cache.cacheNames[1]=bar").run(( context) -> assertThat(getCacheManager(context, .class).getCacheNames()).containsOnly("foo", "custom1"));
        } finally {
            Caching.getCachingProvider(cachingProviderClassName).close();
        }
    }

    @Test
    public void caffeineCacheWithExplicitCaches() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.DefaultCacheConfiguration.class).withPropertyValues("spring.cache.type=caffeine", "spring.cache.cacheNames=foo").run(( context) -> {
            CaffeineCacheManager manager = getCacheManager(context, .class);
            assertThat(manager.getCacheNames()).containsOnly("foo");
            Cache foo = manager.getCache("foo");
            foo.get("1");
            // See next tests: no spec given so stats should be disabled
            assertThat(((CaffeineCache) (foo)).getNativeCache().stats().missCount()).isEqualTo(0L);
        });
    }

    @Test
    public void caffeineCacheWithCustomizers() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.DefaultCacheAndCustomizersConfiguration.class).withPropertyValues(("spring.cache.type=" + "caffeine")).run(verifyCustomizers("allCacheManagerCustomizer", "caffeineCacheManagerCustomizer"));
    }

    @Test
    public void caffeineCacheWithExplicitCacheBuilder() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.CaffeineCacheBuilderConfiguration.class).withPropertyValues("spring.cache.type=caffeine", "spring.cache.cacheNames=foo,bar").run(this::validateCaffeineCacheWithStats);
    }

    @Test
    public void caffeineCacheExplicitWithSpec() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.CaffeineCacheSpecConfiguration.class).withPropertyValues("spring.cache.type=caffeine", "spring.cache.cacheNames[0]=foo", "spring.cache.cacheNames[1]=bar").run(this::validateCaffeineCacheWithStats);
    }

    @Test
    public void caffeineCacheExplicitWithSpecString() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.DefaultCacheConfiguration.class).withPropertyValues("spring.cache.type=caffeine", "spring.cache.caffeine.spec=recordStats", "spring.cache.cacheNames[0]=foo", "spring.cache.cacheNames[1]=bar").run(this::validateCaffeineCacheWithStats);
    }

    @Test
    public void autoConfiguredCacheManagerCanBeSwapped() {
        this.contextRunner.withUserConfiguration(CacheAutoConfigurationTests.CacheManagerPostProcessorConfiguration.class).withPropertyValues("spring.cache.type=caffeine").run(( context) -> {
            getCacheManager(context, .class);
            org.springframework.boot.autoconfigure.cache.CacheManagerPostProcessor postProcessor = context.getBean(.class);
            assertThat(postProcessor.cacheManagers).hasSize(1);
            assertThat(postProcessor.cacheManagers.get(0)).isInstanceOf(.class);
        });
    }

    @Configuration
    static class EmptyConfiguration {}

    @Configuration
    @EnableCaching
    static class DefaultCacheConfiguration {}

    @Configuration
    @EnableCaching
    @Import(AbstractCacheAutoConfigurationTests.CacheManagerCustomizersConfiguration.class)
    static class DefaultCacheAndCustomizersConfiguration {}

    @Configuration
    @EnableCaching
    static class GenericCacheConfiguration {
        @Bean
        public Cache firstCache() {
            return new ConcurrentMapCache("first");
        }

        @Bean
        public Cache secondCache() {
            return new ConcurrentMapCache("second");
        }
    }

    @Configuration
    @Import({ CacheAutoConfigurationTests.GenericCacheConfiguration.class, AbstractCacheAutoConfigurationTests.CacheManagerCustomizersConfiguration.class })
    static class GenericCacheAndCustomizersConfiguration {}

    @Configuration
    @EnableCaching
    @Import({ HazelcastAutoConfiguration.class, AbstractCacheAutoConfigurationTests.CacheManagerCustomizersConfiguration.class })
    static class HazelcastCacheAndCustomizersConfiguration {}

    @Configuration
    @EnableCaching
    static class CouchbaseCacheConfiguration {
        @Bean
        public Bucket bucket() {
            BucketManager bucketManager = Mockito.mock(BucketManager.class);
            Bucket bucket = Mockito.mock(Bucket.class);
            BDDMockito.given(bucket.bucketManager()).willReturn(bucketManager);
            return bucket;
        }
    }

    @Configuration
    @Import({ CacheAutoConfigurationTests.CouchbaseCacheConfiguration.class, AbstractCacheAutoConfigurationTests.CacheManagerCustomizersConfiguration.class })
    static class CouchbaseCacheAndCustomizersConfiguration {}

    @Configuration
    @EnableCaching
    static class RedisConfiguration {
        @Bean
        public RedisConnectionFactory redisConnectionFactory() {
            return Mockito.mock(RedisConnectionFactory.class);
        }
    }

    @Configuration
    @Import(CacheAutoConfigurationTests.RedisConfiguration.class)
    static class RedisWithCacheConfigurationConfiguration {
        @Bean
        public RedisCacheConfiguration customRedisCacheConfiguration() {
            return org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(30)).prefixKeysWith("bar");
        }
    }

    @Configuration
    @Import({ CacheAutoConfigurationTests.RedisConfiguration.class, AbstractCacheAutoConfigurationTests.CacheManagerCustomizersConfiguration.class })
    static class RedisWithCustomizersConfiguration {}

    @Configuration
    @EnableCaching
    static class JCacheCustomConfiguration {
        @Bean
        public javax.cache.configuration.CompleteConfiguration<?, ?> defaultCacheConfiguration() {
            return Mockito.mock(javax.cache.configuration.CompleteConfiguration.class);
        }
    }

    @Configuration
    @EnableCaching
    static class JCacheCustomCacheManager {
        @Bean
        public org.springframework.cache.CacheManager customJCacheCacheManager() {
            javax.cache.CacheManager cacheManager = Mockito.mock(org.springframework.cache.CacheManager.class);
            BDDMockito.given(cacheManager.getCacheNames()).willReturn(Collections.emptyList());
            return cacheManager;
        }
    }

    @Configuration
    @EnableCaching
    static class JCacheWithCustomizerConfiguration {
        @Bean
        JCacheManagerCustomizer myCustomizer() {
            return ( cacheManager) -> {
                MutableConfiguration<?, ?> config = new MutableConfiguration<>();
                config.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.TEN_MINUTES));
                config.setStatisticsEnabled(true);
                cacheManager.createCache("custom1", config);
                cacheManager.destroyCache("bar");
            };
        }
    }

    @Configuration
    @EnableCaching
    static class EhCacheCustomCacheManager {
        @Bean
        public org.springframework.cache.CacheManager customEhCacheCacheManager() {
            net.sf.ehcache.CacheManager cacheManager = Mockito.mock(org.springframework.cache.CacheManager.class);
            BDDMockito.given(cacheManager.getStatus()).willReturn(STATUS_ALIVE);
            BDDMockito.given(cacheManager.getCacheNames()).willReturn(new String[0]);
            return cacheManager;
        }
    }

    @Configuration
    @EnableCaching
    static class HazelcastCustomHazelcastInstance {
        @Bean
        public HazelcastInstance customHazelcastInstance() {
            return Mockito.mock(HazelcastInstance.class);
        }
    }

    @Configuration
    @EnableCaching
    static class InfinispanCustomConfiguration {
        @Bean
        public ConfigurationBuilder configurationBuilder() {
            ConfigurationBuilder builder = Mockito.mock(ConfigurationBuilder.class);
            BDDMockito.given(builder.build()).willReturn(new ConfigurationBuilder().build());
            return builder;
        }
    }

    @Configuration
    @EnableCaching
    static class CustomCacheManagerConfiguration {
        @Bean
        public org.springframework.cache.CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("custom1");
        }
    }

    @Configuration
    @EnableCaching
    static class CustomCacheManagerFromSupportConfiguration extends CachingConfigurerSupport {
        // The @Bean annotation is important, see CachingConfigurerSupport Javadoc
        @Override
        @Bean
        public org.springframework.cache.CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("custom1");
        }
    }

    @Configuration
    @EnableCaching
    static class CustomCacheResolverFromSupportConfiguration extends CachingConfigurerSupport {
        // The @Bean annotation is important, see CachingConfigurerSupport Javadoc
        @Override
        @Bean
        public CacheResolver cacheResolver() {
            return ( context) -> Collections.singleton(mock(.class));
        }
    }

    @Configuration
    @EnableCaching
    static class SpecificCacheResolverConfiguration {
        @Bean
        public CacheResolver myCacheResolver() {
            return Mockito.mock(CacheResolver.class);
        }
    }

    @Configuration
    @EnableCaching
    static class CaffeineCacheBuilderConfiguration {
        @Bean
        Caffeine<Object, Object> cacheBuilder() {
            return Caffeine.newBuilder().recordStats();
        }
    }

    @Configuration
    @EnableCaching
    static class CaffeineCacheSpecConfiguration {
        @Bean
        CaffeineSpec caffeineSpec() {
            return CaffeineSpec.parse("recordStats");
        }
    }

    @Configuration
    @EnableCaching
    static class CacheManagerPostProcessorConfiguration {
        @Bean
        public static BeanPostProcessor cacheManagerBeanPostProcessor() {
            return new CacheAutoConfigurationTests.CacheManagerPostProcessor();
        }
    }

    private static class CacheManagerPostProcessor implements BeanPostProcessor {
        private final List<org.springframework.cache.CacheManager> cacheManagers = new ArrayList<>();

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) {
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) {
            if (bean instanceof org.springframework.cache.CacheManager) {
                this.cacheManagers.add(((org.springframework.cache.CacheManager) (bean)));
                return new SimpleCacheManager();
            }
            return bean;
        }
    }
}

