/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.cache;


import org.junit.Assert;
import org.junit.Test;


public class LRUCacheTest {
    @Test
    public void testPutGet() {
        Cache<String, String> cache = new LRUCache(4);
        cache.put("a", "b");
        cache.put("c", "d");
        cache.put("e", "f");
        cache.put("g", "h");
        Assert.assertEquals(4, cache.size());
        Assert.assertEquals("b", cache.get("a"));
        Assert.assertEquals("d", cache.get("c"));
        Assert.assertEquals("f", cache.get("e"));
        Assert.assertEquals("h", cache.get("g"));
    }

    @Test
    public void testRemove() {
        Cache<String, String> cache = new LRUCache(4);
        cache.put("a", "b");
        cache.put("c", "d");
        cache.put("e", "f");
        Assert.assertEquals(3, cache.size());
        Assert.assertEquals(true, cache.remove("a"));
        Assert.assertEquals(2, cache.size());
        Assert.assertNull(cache.get("a"));
        Assert.assertEquals("d", cache.get("c"));
        Assert.assertEquals("f", cache.get("e"));
        Assert.assertEquals(false, cache.remove("key-does-not-exist"));
        Assert.assertEquals(true, cache.remove("c"));
        Assert.assertEquals(1, cache.size());
        Assert.assertNull(cache.get("c"));
        Assert.assertEquals("f", cache.get("e"));
        Assert.assertEquals(true, cache.remove("e"));
        Assert.assertEquals(0, cache.size());
        Assert.assertNull(cache.get("e"));
    }

    @Test
    public void testEviction() {
        Cache<String, String> cache = new LRUCache(2);
        cache.put("a", "b");
        cache.put("c", "d");
        Assert.assertEquals(2, cache.size());
        cache.put("e", "f");
        Assert.assertEquals(2, cache.size());
        Assert.assertNull(cache.get("a"));
        Assert.assertEquals("d", cache.get("c"));
        Assert.assertEquals("f", cache.get("e"));
        // Validate correct access order eviction
        cache.get("c");
        cache.put("g", "h");
        Assert.assertEquals(2, cache.size());
        Assert.assertNull(cache.get("e"));
        Assert.assertEquals("d", cache.get("c"));
        Assert.assertEquals("h", cache.get("g"));
    }
}

