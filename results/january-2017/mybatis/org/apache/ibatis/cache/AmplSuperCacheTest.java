/**
 * Copyright 2009-2015 the original author or authors.
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


public class AmplSuperCacheTest {
    @org.junit.Test
    public void shouldDemonstrate5LevelSuperCacheHandlesLotsOfEntriesWithoutCrashing() {
        final int N = 100000;
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("default");
        cache = new org.apache.ibatis.cache.decorators.LruCache(cache);
        cache = new org.apache.ibatis.cache.decorators.FifoCache(cache);
        cache = new org.apache.ibatis.cache.decorators.SoftCache(cache);
        cache = new org.apache.ibatis.cache.decorators.WeakCache(cache);
        cache = new org.apache.ibatis.cache.decorators.ScheduledCache(cache);
        cache = new org.apache.ibatis.cache.decorators.SerializedCache(cache);
        // cache = new LoggingCache(cache);
        cache = new org.apache.ibatis.cache.decorators.SynchronizedCache(cache);
        cache = new org.apache.ibatis.cache.decorators.TransactionalCache(cache);
        for (int i = 0; i < N; i++) {
            cache.putObject(i, i);
            ((org.apache.ibatis.cache.decorators.TransactionalCache) (cache)).commit();
            java.lang.Object o = cache.getObject(i);
            org.junit.Assert.assertTrue(((o == null) || (i == ((java.lang.Integer) (o)))));
        }
        org.junit.Assert.assertTrue(((cache.getSize()) < N));
    }
}

