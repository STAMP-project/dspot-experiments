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


public class AmplScheduledCacheTest {
    @org.junit.Test
    public void shouldDemonstrateHowAllObjectsAreFlushedAfterBasedOnTime() throws java.lang.Exception {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("DefaultCache");
        cache = new org.apache.ibatis.cache.decorators.ScheduledCache(cache);
        ((org.apache.ibatis.cache.decorators.ScheduledCache) (cache)).setClearInterval(2500);
        cache = new org.apache.ibatis.cache.decorators.LoggingCache(cache);
        for (int i = 0; i < 100; i++) {
            cache.putObject(i, i);
            org.junit.Assert.assertEquals(i, cache.getObject(i));
        }
        java.lang.Thread.sleep(5000);
        org.junit.Assert.assertEquals(0, cache.getSize());
    }

    @org.junit.Test
    public void shouldFlushAllItemsOnDemand() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("DefaultCache");
        cache = new org.apache.ibatis.cache.decorators.ScheduledCache(cache);
        ((org.apache.ibatis.cache.decorators.ScheduledCache) (cache)).setClearInterval(60000);
        cache = new org.apache.ibatis.cache.decorators.LoggingCache(cache);
        for (int i = 0; i < 5; i++) {
            cache.putObject(i, i);
        }
        org.junit.Assert.assertNotNull(cache.getObject(0));
        org.junit.Assert.assertNotNull(cache.getObject(4));
        cache.clear();
        org.junit.Assert.assertNull(cache.getObject(0));
        org.junit.Assert.assertNull(cache.getObject(4));
    }

    @org.junit.Test
    public void shouldRemoveItemOnDemand() {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("DefaultCache");
        cache = new org.apache.ibatis.cache.decorators.ScheduledCache(cache);
        ((org.apache.ibatis.cache.decorators.ScheduledCache) (cache)).setClearInterval(60000);
        cache = new org.apache.ibatis.cache.decorators.LoggingCache(cache);
        cache.putObject(0, 0);
        org.junit.Assert.assertNotNull(cache.getObject(0));
        // AssertGenerator replace invocation
        java.lang.Object o_shouldRemoveItemOnDemand__11 = cache.removeObject(0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_shouldRemoveItemOnDemand__11, 0);
        org.junit.Assert.assertNull(cache.getObject(0));
    }

    /* amplification of org.apache.ibatis.cache.ScheduledCacheTest#shouldDemonstrateHowAllObjectsAreFlushedAfterBasedOnTime */
    @org.junit.Test
    public void shouldDemonstrateHowAllObjectsAreFlushedAfterBasedOnTime_literalMutation5() throws java.lang.Exception {
        org.apache.ibatis.cache.Cache cache = new org.apache.ibatis.cache.impl.PerpetualCache("GdhscbCS@!x*");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getId(), "GdhscbCS@!x*");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getReadWriteLock());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.apache.ibatis.cache.impl.PerpetualCache)cache).getSize(), 0);
        cache = new org.apache.ibatis.cache.decorators.ScheduledCache(cache);
        ((org.apache.ibatis.cache.decorators.ScheduledCache) (cache)).setClearInterval(2500);
        cache = new org.apache.ibatis.cache.decorators.LoggingCache(cache);
        for (int i = 0; i < 100; i++) {
            cache.putObject(i, i);
            org.junit.Assert.assertEquals(i, cache.getObject(i));
        }
        java.lang.Thread.sleep(5000);
        org.junit.Assert.assertEquals(0, cache.getSize());
    }
}

