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
package org.springframework.cache.ehcache;


import TestGroup.LONG_RUNNING;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.cache.AbstractCacheTests;
import org.springframework.tests.Assume;


/**
 *
 *
 * @author Costin Leau
 * @author Stephane Nicoll
 * @author Juergen Hoeller
 */
public class EhCacheCacheTests extends AbstractCacheTests<EhCacheCache> {
    private CacheManager cacheManager;

    private Ehcache nativeCache;

    private EhCacheCache cache;

    @Test
    public void testExpiredElements() throws Exception {
        Assume.group(LONG_RUNNING);
        String key = "brancusi";
        String value = "constantin";
        Element brancusi = new Element(key, value);
        // ttl = 10s
        brancusi.setTimeToLive(3);
        nativeCache.put(brancusi);
        Assert.assertEquals(value, cache.get(key).get());
        // wait for the entry to expire
        Thread.sleep((5 * 1000));
        Assert.assertNull(cache.get(key));
    }
}

