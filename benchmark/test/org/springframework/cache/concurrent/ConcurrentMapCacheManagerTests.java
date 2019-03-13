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
package org.springframework.cache.concurrent;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;


/**
 *
 *
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 */
public class ConcurrentMapCacheManagerTests {
    @Test
    public void testDynamicMode() {
        CacheManager cm = new ConcurrentMapCacheManager();
        Cache cache1 = cm.getCache("c1");
        Assert.assertTrue((cache1 instanceof ConcurrentMapCache));
        Cache cache1again = cm.getCache("c1");
        Assert.assertSame(cache1again, cache1);
        Cache cache2 = cm.getCache("c2");
        Assert.assertTrue((cache2 instanceof ConcurrentMapCache));
        Cache cache2again = cm.getCache("c2");
        Assert.assertSame(cache2again, cache2);
        Cache cache3 = cm.getCache("c3");
        Assert.assertTrue((cache3 instanceof ConcurrentMapCache));
        Cache cache3again = cm.getCache("c3");
        Assert.assertSame(cache3again, cache3);
        cache1.put("key1", "value1");
        Assert.assertEquals("value1", cache1.get("key1").get());
        cache1.put("key2", 2);
        Assert.assertEquals(2, cache1.get("key2").get());
        cache1.put("key3", null);
        Assert.assertNull(cache1.get("key3").get());
        cache1.put("key3", null);
        Assert.assertNull(cache1.get("key3").get());
        cache1.evict("key3");
        Assert.assertNull(cache1.get("key3"));
        Assert.assertEquals("value1", cache1.putIfAbsent("key1", "value1x").get());
        Assert.assertEquals("value1", cache1.get("key1").get());
        Assert.assertEquals(2, cache1.putIfAbsent("key2", 2.1).get());
        Assert.assertNull(cache1.putIfAbsent("key3", null));
        Assert.assertNull(cache1.get("key3").get());
        Assert.assertNull(cache1.putIfAbsent("key3", null).get());
        Assert.assertNull(cache1.get("key3").get());
        cache1.evict("key3");
        Assert.assertNull(cache1.get("key3"));
    }

    @Test
    public void testStaticMode() {
        ConcurrentMapCacheManager cm = new ConcurrentMapCacheManager("c1", "c2");
        Cache cache1 = cm.getCache("c1");
        Assert.assertTrue((cache1 instanceof ConcurrentMapCache));
        Cache cache1again = cm.getCache("c1");
        Assert.assertSame(cache1again, cache1);
        Cache cache2 = cm.getCache("c2");
        Assert.assertTrue((cache2 instanceof ConcurrentMapCache));
        Cache cache2again = cm.getCache("c2");
        Assert.assertSame(cache2again, cache2);
        Cache cache3 = cm.getCache("c3");
        Assert.assertNull(cache3);
        cache1.put("key1", "value1");
        Assert.assertEquals("value1", cache1.get("key1").get());
        cache1.put("key2", 2);
        Assert.assertEquals(2, cache1.get("key2").get());
        cache1.put("key3", null);
        Assert.assertNull(cache1.get("key3").get());
        cache1.evict("key3");
        Assert.assertNull(cache1.get("key3"));
        cm.setAllowNullValues(false);
        Cache cache1x = cm.getCache("c1");
        Assert.assertTrue((cache1x instanceof ConcurrentMapCache));
        Assert.assertTrue((cache1x != cache1));
        Cache cache2x = cm.getCache("c2");
        Assert.assertTrue((cache2x instanceof ConcurrentMapCache));
        Assert.assertTrue((cache2x != cache2));
        Cache cache3x = cm.getCache("c3");
        Assert.assertNull(cache3x);
        cache1x.put("key1", "value1");
        Assert.assertEquals("value1", cache1x.get("key1").get());
        cache1x.put("key2", 2);
        Assert.assertEquals(2, cache1x.get("key2").get());
        cm.setAllowNullValues(true);
        Cache cache1y = cm.getCache("c1");
        cache1y.put("key3", null);
        Assert.assertNull(cache1y.get("key3").get());
        cache1y.evict("key3");
        Assert.assertNull(cache1y.get("key3"));
    }

    @Test
    public void testChangeStoreByValue() {
        ConcurrentMapCacheManager cm = new ConcurrentMapCacheManager("c1", "c2");
        Assert.assertFalse(cm.isStoreByValue());
        Cache cache1 = cm.getCache("c1");
        Assert.assertTrue((cache1 instanceof ConcurrentMapCache));
        Assert.assertFalse(isStoreByValue());
        cache1.put("key", "value");
        cm.setStoreByValue(true);
        Assert.assertTrue(cm.isStoreByValue());
        Cache cache1x = cm.getCache("c1");
        Assert.assertTrue((cache1x instanceof ConcurrentMapCache));
        Assert.assertTrue((cache1x != cache1));
        Assert.assertNull(cache1x.get("key"));
    }
}

