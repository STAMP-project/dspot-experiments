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
package org.springframework.boot.test.mock.mockito;


import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.SimpleCacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Test {@link MockBean} when mixed with Spring AOP.
 *
 * @author Phillip Webb
 * @see <a href="https://github.com/spring-projects/spring-boot/issues/5837">5837</a>
 */
@RunWith(SpringRunner.class)
public class MockBeanWithAopProxyTests {
    @MockBean
    private MockBeanWithAopProxyTests.DateService dateService;

    @Test
    public void verifyShouldUseProxyTarget() {
        BDDMockito.given(this.dateService.getDate(false)).willReturn(1L);
        Long d1 = this.dateService.getDate(false);
        assertThat(d1).isEqualTo(1L);
        BDDMockito.given(this.dateService.getDate(false)).willReturn(2L);
        Long d2 = this.dateService.getDate(false);
        assertThat(d2).isEqualTo(2L);
        Mockito.verify(this.dateService, Mockito.times(2)).getDate(false);
        Mockito.verify(this.dateService, Mockito.times(2)).getDate(ArgumentMatchers.eq(false));
        Mockito.verify(this.dateService, Mockito.times(2)).getDate(ArgumentMatchers.anyBoolean());
    }

    @Configuration
    @EnableCaching(proxyTargetClass = true)
    @Import(MockBeanWithAopProxyTests.DateService.class)
    static class Config {
        @Bean
        public CacheResolver cacheResolver(CacheManager cacheManager) {
            SimpleCacheResolver resolver = new SimpleCacheResolver();
            resolver.setCacheManager(cacheManager);
            return resolver;
        }

        @Bean
        public ConcurrentMapCacheManager cacheManager() {
            ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
            cacheManager.setCacheNames(Arrays.asList("test"));
            return cacheManager;
        }
    }

    @Service
    static class DateService {
        @Cacheable(cacheNames = "test")
        public Long getDate(boolean argument) {
            return System.nanoTime();
        }
    }
}

