/**
 * Copyright 2009-2012 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.cache;


import java.util.HashSet;
import java.util.Set;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.junit.Assert;
import org.junit.Test;


public class BaseCacheTest {
    @Test
    public void shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes() {
        PerpetualCache cache = new PerpetualCache("test_cache");
        Assert.assertTrue(cache.equals(cache));
        Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.SynchronizedCache(cache)));
        Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.SerializedCache(cache)));
        Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.LoggingCache(cache)));
        Assert.assertTrue(cache.equals(new org.apache.ibatis.cache.decorators.ScheduledCache(cache)));
        Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.SynchronizedCache(cache).hashCode());
        Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.SerializedCache(cache).hashCode());
        Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.LoggingCache(cache).hashCode());
        Assert.assertEquals(cache.hashCode(), new org.apache.ibatis.cache.decorators.ScheduledCache(cache).hashCode());
        Set<Cache> caches = new HashSet<Cache>();
        caches.add(cache);
        caches.add(new org.apache.ibatis.cache.decorators.SynchronizedCache(cache));
        caches.add(new org.apache.ibatis.cache.decorators.SerializedCache(cache));
        caches.add(new org.apache.ibatis.cache.decorators.LoggingCache(cache));
        caches.add(new org.apache.ibatis.cache.decorators.ScheduledCache(cache));
        Assert.assertEquals(1, caches.size());
    }
}

