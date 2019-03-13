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
package org.apache.dubbo.common.utils;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class LRUCacheTest {
    @Test
    public void testCache() throws Exception {
        LRUCache<String, Integer> cache = new LRUCache<String, Integer>(3);
        cache.put("one", 1);
        cache.put("two", 2);
        cache.put("three", 3);
        MatcherAssert.assertThat(cache.get("one"), Matchers.equalTo(1));
        MatcherAssert.assertThat(cache.get("two"), Matchers.equalTo(2));
        MatcherAssert.assertThat(cache.get("three"), Matchers.equalTo(3));
        MatcherAssert.assertThat(cache.size(), Matchers.equalTo(3));
        cache.put("four", 4);
        MatcherAssert.assertThat(cache.size(), Matchers.equalTo(3));
        Assertions.assertFalse(cache.containsKey("one"));
        Assertions.assertTrue(cache.containsKey("two"));
        Assertions.assertTrue(cache.containsKey("three"));
        Assertions.assertTrue(cache.containsKey("four"));
        cache.remove("four");
        MatcherAssert.assertThat(cache.size(), Matchers.equalTo(2));
        cache.put("five", 5);
        Assertions.assertFalse(cache.containsKey("four"));
        Assertions.assertTrue(cache.containsKey("five"));
        Assertions.assertTrue(cache.containsKey("two"));
        Assertions.assertTrue(cache.containsKey("three"));
        MatcherAssert.assertThat(cache.size(), Matchers.equalTo(3));
    }

    @Test
    public void testCapacity() throws Exception {
        LRUCache<String, Integer> cache = new LRUCache<String, Integer>();
        MatcherAssert.assertThat(cache.getMaxCapacity(), Matchers.equalTo(1000));
        cache.setMaxCapacity(10);
        MatcherAssert.assertThat(cache.getMaxCapacity(), Matchers.equalTo(10));
    }
}

