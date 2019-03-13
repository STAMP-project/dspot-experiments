/**
 * Copyright 2017 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.collector.util;


import com.google.common.util.concurrent.AtomicLongMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author emeroad
 */
public class AtomicLongMapTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testIncrement() throws Exception {
        AtomicLongMap<String> cache = AtomicLongMap.create();
        cache.addAndGet("a", 1L);
        cache.addAndGet("a", 2L);
        cache.addAndGet("b", 5L);
        Map<String, Long> remove = AtomicLongMapUtils.remove(cache);
        Assert.assertEquals(((long) (remove.get("a"))), 3L);
        Assert.assertEquals(((long) (remove.get("b"))), 5L);
        cache.addAndGet("a", 1L);
        Map<String, Long> remove2 = AtomicLongMapUtils.remove(cache);
        Assert.assertEquals(((long) (remove2.get("a"))), 1L);
    }

    @Test
    public void testIntegerMax() throws Exception {
        AtomicLongMap<String> cache = AtomicLongMap.create();
        cache.addAndGet("a", 1L);
        cache.addAndGet("a", 2L);
        cache.addAndGet("b", 5L);
    }

    @Test
    public void testIntegerMin() throws Exception {
        AtomicLongMap<String> cache = AtomicLongMap.create();
        cache.addAndGet("a", 1L);
        cache.addAndGet("a", 2L);
        cache.addAndGet("b", 5L);
    }
}

