/**
 * Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
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
package org.springframework.security.cas.authentication;


import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import org.junit.Test;


/**
 * Tests {@link EhCacheBasedTicketCache}.
 *
 * @author Ben Alex
 */
public class EhCacheBasedTicketCacheTests extends AbstractStatelessTicketCacheTests {
    private static CacheManager cacheManager;

    @Test
    public void testCacheOperation() throws Exception {
        EhCacheBasedTicketCache cache = new EhCacheBasedTicketCache();
        cache.setCache(EhCacheBasedTicketCacheTests.cacheManager.getCache("castickets"));
        cache.afterPropertiesSet();
        final CasAuthenticationToken token = getToken();
        // Check it gets stored in the cache
        cache.putTicketInCache(token);
        assertThat(cache.getByTicketId("ST-0-ER94xMJmn6pha35CQRoZ")).isEqualTo(token);
        // Check it gets removed from the cache
        cache.removeTicketFromCache(getToken());
        assertThat(cache.getByTicketId("ST-0-ER94xMJmn6pha35CQRoZ")).isNull();
        // Check it doesn't return values for null or unknown service tickets
        assertThat(cache.getByTicketId(null)).isNull();
        assertThat(cache.getByTicketId("UNKNOWN_SERVICE_TICKET")).isNull();
    }

    @Test
    public void testStartupDetectsMissingCache() throws Exception {
        EhCacheBasedTicketCache cache = new EhCacheBasedTicketCache();
        try {
            cache.afterPropertiesSet();
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        Ehcache myCache = EhCacheBasedTicketCacheTests.cacheManager.getCache("castickets");
        cache.setCache(myCache);
        assertThat(cache.getCache()).isEqualTo(myCache);
    }
}

