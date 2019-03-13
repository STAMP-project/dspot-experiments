/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.integration;


import java.time.Duration;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.core.spi.service.StatisticsService;
import org.ehcache.impl.internal.statistics.DefaultStatisticsService;
import org.ehcache.integration.statistics.AbstractCacheCalculationTest;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;


/**
 *
 *
 * @author Henri Tremblay
 */
@RunWith(Parameterized.class)
public class EhcacheBaseTest extends AbstractCacheCalculationTest {
    private CacheManager cacheManager;

    private Cache<Integer, String> cache;

    private final StatisticsService statisticsService = new DefaultStatisticsService();

    private final TestTimeSource timeSource = new TestTimeSource();

    public EhcacheBaseTest(ResourcePoolsBuilder poolBuilder) {
        super(poolBuilder);
    }

    @Test
    public void putIfAbsent_absent() {
        createCacheManager();
        cache = createCache();
        assertThat(cache.putIfAbsent(1, "a")).isNull();
        assertThat(cache.get(1)).isEqualTo("a");
        changesOf(1, 1, 1, 0);
    }

    @Test
    public void putIfAbsent_present() {
        createCacheManager();
        cache = createCache();
        cache.put(1, "a");
        assertThat(cache.putIfAbsent(1, "b")).isEqualTo("a");
        changesOf(1, 0, 1, 0);
    }

    @Test
    public void putIfAbsent_presentButExpired() {
        createCacheManager();
        CacheConfigurationBuilder<Integer, String> builder = baseConfig().withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMillis(10)));
        cache = createCache(builder);
        cache.put(1, "a");
        timeSource.advanceTime(15);
        assertThat(cache.putIfAbsent(1, "b")).isNull();
        assertThat(cache.get(1)).isEqualTo("b");
        changesOf(1, 1, 2, 0);
    }

    @Test
    public void putIfAbsent_absentPutNull() {
        createCacheManager();
        cache = createCache();
        assertThatNullPointerException().isThrownBy(() -> cache.putIfAbsent(1, null));
        changesOf(0, 0, 0, 0);
    }

    @Test
    public void putIfAbsentLoaderWriter_absentAndLoaded() throws Exception {
        createCacheManager();
        CacheLoaderWriter<Integer, String> loader = EhcacheBaseTest.mockLoader();
        Mockito.when(loader.load(1)).thenReturn("a");
        CacheConfigurationBuilder<Integer, String> builder = baseConfig().withLoaderWriter(loader);
        cache = createCache(builder);
        assertThat(cache.putIfAbsent(1, "b")).isEqualTo("a");
        assertThat(cache.get(1)).isEqualTo("a");
        changesOf(2, 0, 0, 0);
    }

    @Test
    public void putIfAbsentLoaderWriter_absentAndNotLoaded() throws Exception {
        createCacheManager();
        CacheLoaderWriter<Integer, String> loader = EhcacheBaseTest.mockLoader();
        Mockito.when(loader.load(1)).thenReturn(null);
        CacheConfigurationBuilder<Integer, String> builder = baseConfig().withLoaderWriter(loader);
        cache = createCache(builder);
        assertThat(cache.putIfAbsent(1, "b")).isNull();
        Mockito.verify(loader).write(1, "b");
        assertThat(cache.get(1)).isEqualTo("b");
        changesOf(1, 1, 1, 0);
    }

    @Test
    public void putIfAbsentLoaderWriter_present() throws Exception {
        createCacheManager();
        CacheLoaderWriter<Integer, String> loader = EhcacheBaseTest.mockLoader();
        CacheConfigurationBuilder<Integer, String> builder = baseConfig().withLoaderWriter(loader);
        cache = createCache(builder);
        cache.put(1, "a");
        assertThat(cache.putIfAbsent(1, "b")).isEqualTo("a");
        Mockito.verify(loader).write(1, "a");
        changesOf(1, 0, 1, 0);
    }

    @Test
    public void putIfAbsentLoaderWriter_presentButExpiredAndLoaded() throws Exception {
        createCacheManager();
        CacheLoaderWriter<Integer, String> loader = EhcacheBaseTest.mockLoader();
        Mockito.when(loader.load(1)).thenReturn("c");
        CacheConfigurationBuilder<Integer, String> builder = baseConfig().withLoaderWriter(loader).withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMillis(10)));
        cache = createCache(builder);
        cache.put(1, "a");
        timeSource.advanceTime(15);
        assertThat(cache.putIfAbsent(1, "b")).isEqualTo("c");
        Mockito.verify(loader).write(1, "a");
        changesOf(1, 0, 1, 0);
    }

    @Test
    public void putIfAbsentLoaderWriter_presentButExpiredAndNotLoaded() throws Exception {
        createCacheManager();
        CacheLoaderWriter<Integer, String> loader = EhcacheBaseTest.mockLoader();
        Mockito.when(loader.load(1)).thenReturn(null);
        CacheConfigurationBuilder<Integer, String> builder = baseConfig().withLoaderWriter(loader).withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMillis(10)));
        cache = createCache(builder);
        cache.put(1, "a");
        timeSource.advanceTime(15);
        assertThat(cache.putIfAbsent(1, "b")).isNull();
        Mockito.verify(loader).write(1, "b");
        changesOf(0, 1, 2, 0);
    }

    @Test
    public void putIfAbsentLoaderWriterNotAtomic_absent() throws Exception {
        createNotAtomicCacheManager();
        CacheLoaderWriter<Integer, String> loader = EhcacheBaseTest.mockLoader();
        CacheConfigurationBuilder<Integer, String> builder = baseConfig().withLoaderWriter(loader);
        cache = createCache(builder);
        assertThat(cache.putIfAbsent(1, "a")).isNull();
        Mockito.verify(loader).write(1, "a");
        assertThat(cache.get(1)).isEqualTo("a");
        changesOf(1, 1, 1, 0);
    }

    @Test
    public void putIfAbsentLoaderWriterNotAtomic_present() throws Exception {
        createNotAtomicCacheManager();
        CacheLoaderWriter<Integer, String> loader = EhcacheBaseTest.mockLoader();
        CacheConfigurationBuilder<Integer, String> builder = baseConfig().withLoaderWriter(loader);
        cache = createCache(builder);
        cache.put(1, "a");
        assertThat(cache.putIfAbsent(1, "b")).isEqualTo("a");
        Mockito.verify(loader).write(1, "a");
        Mockito.verifyNoMoreInteractions(loader);
        changesOf(1, 0, 1, 0);
    }
}

