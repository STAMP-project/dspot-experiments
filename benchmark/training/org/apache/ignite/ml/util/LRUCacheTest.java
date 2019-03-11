/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.ml.util;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link LRUCache}.
 */
public class LRUCacheTest {
    /**
     *
     */
    @Test
    public void testSize() {
        LRUCache<Integer, Integer> cache = new LRUCache(10);
        for (int i = 0; i < 100; i++)
            cache.put(i, i);

        Assert.assertEquals(10, cache.size());
    }

    /**
     *
     */
    @Test
    public void testValues() {
        LRUCache<Integer, Integer> cache = new LRUCache(10);
        for (int i = 0; i < 100; i++) {
            cache.get(0);
            cache.put(i, i);
        }
        Assert.assertTrue(cache.containsKey(0));
        for (int i = 91; i < 100; i++)
            Assert.assertTrue(cache.containsKey(i));

    }

    /**
     *
     */
    @Test
    public void testExpirationListener() {
        List<Integer> expired = new ArrayList<>();
        LRUCache<Integer, Integer> cache = new LRUCache(10, expired::add);
        for (int i = 0; i < 100; i++)
            cache.put(i, i);

        for (int i = 0; i < 90; i++)
            Assert.assertEquals(i, expired.get(i).longValue());

    }
}

