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
package org.springframework.boot.actuate.autoconfigure.metrics.cache;


import io.micrometer.core.instrument.MeterRegistry;
import org.junit.Test;
import org.springframework.boot.actuate.autoconfigure.metrics.test.MetricsRun;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link CacheMetricsAutoConfiguration}.
 *
 * @author Stephane Nicoll
 */
public class CacheMetricsAutoConfigurationTests {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().with(MetricsRun.simple()).withUserConfiguration(CacheMetricsAutoConfigurationTests.CachingConfiguration.class).withConfiguration(AutoConfigurations.of(CacheAutoConfiguration.class, CacheMetricsAutoConfiguration.class));

    @Test
    public void autoConfiguredCacheManagerIsInstrumented() {
        this.contextRunner.withPropertyValues("spring.cache.type=caffeine", "spring.cache.cache-names=cache1,cache2").run(( context) -> {
            MeterRegistry registry = context.getBean(.class);
            registry.get("cache.gets").tags("name", "cache1").tags("cacheManager", "cacheManager").meter();
            registry.get("cache.gets").tags("name", "cache2").tags("cacheManager", "cacheManager").meter();
        });
    }

    @Test
    public void autoConfiguredNonSupportedCacheManagerIsIgnored() {
        this.contextRunner.withPropertyValues("spring.cache.type=simple", "spring.cache.cache-names=cache1,cache2").run(( context) -> {
            MeterRegistry registry = context.getBean(.class);
            assertThat(registry.find("cache.gets").tags("name", "cache1").tags("cacheManager", "cacheManager").meter()).isNull();
            assertThat(registry.find("cache.gets").tags("name", "cache2").tags("cacheManager", "cacheManager").meter()).isNull();
        });
    }

    @Test
    public void cacheInstrumentationCanBeDisabled() {
        this.contextRunner.withPropertyValues("management.metrics.enable.cache=false", "spring.cache.type=caffeine", "spring.cache.cache-names=cache1").run(( context) -> {
            MeterRegistry registry = context.getBean(.class);
            assertThat(registry.find("cache.requests").tags("name", "cache1").tags("cacheManager", "cacheManager").meter()).isNull();
        });
    }

    @Configuration
    @EnableCaching
    static class CachingConfiguration {}
}

