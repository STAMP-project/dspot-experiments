/**
 * Copyright 2017-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atomix.core.map;


import io.atomix.core.AbstractPrimitiveTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@code AtomixCounterMap}.
 */
public class AtomicCounterMapTest extends AbstractPrimitiveTest {
    /**
     * Tests basic counter map operations.
     */
    @Test
    public void testBasicCounterMapOperations() throws Throwable {
        AtomicCounterMap<String> map = atomix().<String>atomicCounterMapBuilder("testBasicCounterMapOperationMap").withProtocol(protocol()).build();
        Assert.assertTrue(map.isEmpty());
        Assert.assertEquals(0, map.size());
        Assert.assertEquals(0, map.put("foo", 2));
        Assert.assertEquals(3, map.incrementAndGet("foo"));
        Assert.assertEquals(3, map.getAndIncrement("foo"));
        Assert.assertEquals(4, map.get("foo"));
        Assert.assertEquals(4, map.getAndDecrement("foo"));
        Assert.assertEquals(2, map.decrementAndGet("foo"));
        Assert.assertEquals(1, map.size());
        Assert.assertFalse(map.isEmpty());
        map.clear();
        Assert.assertTrue(map.isEmpty());
        Assert.assertEquals(0, map.size());
        Assert.assertEquals(0, map.get("foo"));
        Assert.assertEquals(1, map.incrementAndGet("bar"));
        Assert.assertEquals(3, map.addAndGet("bar", 2));
        Assert.assertEquals(3, map.getAndAdd("bar", 3));
        Assert.assertEquals(6, map.get("bar"));
        Assert.assertEquals(6, map.putIfAbsent("bar", 1));
        Assert.assertTrue(map.replace("bar", 6, 1));
        Assert.assertFalse(map.replace("bar", 6, 1));
        Assert.assertEquals(1, map.size());
        Assert.assertEquals(1, map.remove("bar"));
        Assert.assertEquals(0, map.remove("bar"));
        Assert.assertEquals(0, map.size());
        Assert.assertEquals(0, map.put("baz", 3));
        Assert.assertFalse(map.remove("baz", 2));
        Assert.assertEquals(3, map.put("baz", 2));
        Assert.assertTrue(map.remove("baz", 2));
        Assert.assertTrue(map.isEmpty());
        Assert.assertTrue(map.replace("baz", 0, 5));
        Assert.assertEquals(5, map.get("baz"));
    }
}

