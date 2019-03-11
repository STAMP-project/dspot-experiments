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
package org.ehcache.docs;


import java.time.Duration;
import java.util.function.Supplier;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.ExpiryPolicy;
import org.ehcache.internal.TestTimeSource;
import org.junit.Test;


public class Ehcache3 {
    private TestTimeSource timeSource = new TestTimeSource(System.currentTimeMillis());

    @Test
    public void ehcache3Expiry() throws Exception {
        // tag::CustomExpiryEhcache3[]
        CacheManager cacheManager = initCacheManager();
        CacheConfigurationBuilder<Long, String> configuration = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(100)).withExpiry(new ExpiryPolicy<Long, String>() {
            // <1>
            @Override
            public Duration getExpiryForCreation(Long key, String value) {
                return getTimeToLiveDuration(key, value);// <2>

            }

            @Override
            public Duration getExpiryForAccess(Long key, Supplier<? extends String> value) {
                return null;// Keeping the existing expiry

            }

            @Override
            public Duration getExpiryForUpdate(Long key, Supplier<? extends String> oldValue, String newValue) {
                return null;// Keeping the existing expiry

            }
        });
        cacheManager.createCache("cache", configuration);
        Cache<Long, String> cache = cacheManager.getCache("cache", Long.class, String.class);
        cache.put(10L, "Hello");
        System.out.println(cache.get(10L));
        sleep(2100);// <3>

        // Now the returned value should be null, as mapping is expired.
        System.out.println(cache.get(10L));
        // end::CustomExpiryEhcache3[]
    }
}

