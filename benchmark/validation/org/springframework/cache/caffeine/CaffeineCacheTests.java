/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.cache.caffeine;


import Cache.ValueWrapper;
import com.github.benmanes.caffeine.cache.Cache;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.cache.AbstractValueAdaptingCacheTests;


/**
 *
 *
 * @author Ben Manes
 * @author Stephane Nicoll
 */
public class CaffeineCacheTests extends AbstractValueAdaptingCacheTests<CaffeineCache> {
    private Cache<Object, Object> nativeCache;

    private CaffeineCache cache;

    private CaffeineCache cacheNoNull;

    @Test
    public void testPutIfAbsentNullValue() throws Exception {
        CaffeineCache cache = getCache();
        Object key = new Object();
        Object value = null;
        Assert.assertNull(cache.get(key));
        Assert.assertNull(cache.putIfAbsent(key, value));
        Assert.assertEquals(value, cache.get(key).get());
        org.springframework.cache.Cache.ValueWrapper wrapper = cache.putIfAbsent(key, "anotherValue");
        Assert.assertNotNull(wrapper);// A value is set but is 'null'

        Assert.assertEquals(null, wrapper.get());
        Assert.assertEquals(value, cache.get(key).get());// not changed

    }
}

