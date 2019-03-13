/**
 * Copyright (C) 2014 Markus Junginger, greenrobot (http://greenrobot.de)
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
package org.greenrobot.essentials;


import ObjectCache.ReferenceType;
import ObjectCache.ReferenceType.SOFT;
import ObjectCache.ReferenceType.STRONG;
import ObjectCache.ReferenceType.WEAK;
import org.junit.Assert;
import org.junit.Test;


public class ObjectCacheTest {
    @Test
    public void testBasics() {
        doTestBasics(SOFT);
        doTestBasics(STRONG);
        doTestBasics(WEAK);
    }

    @Test
    public void testMaxSize() {
        ObjectCache<String, String> cache = createCacheWith4Entries(0);
        cache.put("5", "e");
        Assert.assertEquals(4, cache.size());
        Assert.assertNull(cache.get("1"));
        Assert.assertEquals(cache.get("5"), "e");
        Assert.assertEquals(1, cache.getCountEvicted());
    }

    @Test
    public void testEvictToTargetSize() {
        ObjectCache<String, String> cache = createCacheWith4Entries(0);
        cache.evictToTargetSize(2);
        Assert.assertEquals(2, cache.size());
        Assert.assertEquals(cache.get("3"), "c");
        Assert.assertEquals(cache.get("4"), "d");
        cache.evictToTargetSize(0);
        Assert.assertEquals(0, cache.size());
    }

    @Test
    public void testExpired() throws InterruptedException {
        ObjectCache<String, String> cache = new ObjectCache(ReferenceType.STRONG, 4, 1);
        cache.put("1", "a");
        Thread.sleep(3);
        Assert.assertNull(cache.get("1"));
        Assert.assertEquals(0, cache.size());
        Assert.assertEquals(1, cache.getCountExpired());
    }

    @Test
    public void testCleanUpObsoleteEntries() throws InterruptedException {
        // Use more than one entry to detect ConcurrentModificationException
        ObjectCache<String, String> cache = createCacheWith4Entries(1);
        Thread.sleep(3);
        cache.checkCleanUpObsoleteEntries();
        Assert.assertEquals(0, cache.size());
        Assert.assertEquals(4, cache.getCountExpired());
    }

    @Test
    public void testNotExpired() throws InterruptedException {
        ObjectCache<String, String> cache = new ObjectCache(ReferenceType.STRONG, 4, 1000);
        cache.put("1", "a");
        Thread.sleep(3);
        Assert.assertEquals(cache.get("1"), "a");
        Assert.assertEquals(0, cache.getCountExpired());
    }
}

