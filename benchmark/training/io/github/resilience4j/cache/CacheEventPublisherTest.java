/**
 * Copyright 2016 Robert Winkler
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.github.resilience4j.cache;


import Cache.EventPublisher;
import io.vavr.CheckedFunction1;
import javax.cache.Cache;
import javax.xml.ws.WebServiceException;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.slf4j.Logger;


public class CacheEventPublisherTest {
    private Cache<String, String> cache;

    private Logger logger;

    @Test
    public void shouldReturnTheSameConsumer() {
        Cache<String, String> cacheContext = Cache.of(cache);
        Cache.EventPublisher eventPublisher = cacheContext.getEventPublisher();
        Cache.EventPublisher eventPublisher2 = cacheContext.getEventPublisher();
        assertThat(eventPublisher).isEqualTo(eventPublisher2);
    }

    @Test
    public void shouldConsumeOnCacheHitEvent() throws Throwable {
        // Given the cache does not contain the key
        BDDMockito.given(cache.get("testKey")).willReturn("Hello world");
        Cache<String, String> cacheContext = Cache.of(cache);
        cacheContext.getEventPublisher().onCacheHit(( event) -> logger.info(event.getEventType().toString()));
        CheckedFunction1<String, String> cachedFunction = Cache.decorateCheckedSupplier(cacheContext, () -> "Hello world");
        String value = cachedFunction.apply("testKey");
        assertThat(value).isEqualTo("Hello world");
        BDDMockito.then(logger).should(Mockito.times(1)).info("CACHE_HIT");
    }

    @Test
    public void shouldConsumeOnCacheMissEvent() throws Throwable {
        // Given the cache does not contain the key
        BDDMockito.given(cache.get("testKey")).willReturn(null);
        Cache<String, String> cacheContext = Cache.of(cache);
        cacheContext.getEventPublisher().onCacheMiss(( event) -> logger.info(event.getEventType().toString()));
        CheckedFunction1<String, String> cachedFunction = Cache.decorateCheckedSupplier(cacheContext, () -> "Hello world");
        String value = cachedFunction.apply("testKey");
        assertThat(value).isEqualTo("Hello world");
        BDDMockito.then(logger).should(Mockito.times(1)).info("CACHE_MISS");
    }

    @Test
    public void shouldConsumeOnErrorEvent() throws Throwable {
        // Given the cache does not contain the key
        BDDMockito.given(cache.get("testKey")).willThrow(new WebServiceException("BLA"));
        Cache<String, String> cacheContext = Cache.of(cache);
        cacheContext.getEventPublisher().onError(( event) -> logger.info(event.getEventType().toString()));
        CheckedFunction1<String, String> cachedFunction = Cache.decorateCheckedSupplier(cacheContext, () -> "Hello world");
        String value = cachedFunction.apply("testKey");
        assertThat(value).isEqualTo("Hello world");
        BDDMockito.then(logger).should(Mockito.times(1)).info("ERROR");
    }
}

