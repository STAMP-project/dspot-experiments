/**
 * Copyright Terracotta, Inc.
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
package org.ehcache.impl.internal.store.heap.bytesized;


import java.util.function.Function;
import org.ehcache.impl.internal.store.heap.OnHeapStore;
import org.junit.Test;


/**
 *
 *
 * @author Abhilash
 */
public class OversizeMappingTest {
    private static final String KEY = "key";

    private static final String VALUE = "value";

    private static final String OVER_SIZED_VALUE = new String(new byte[1000]);

    @Test
    public void testPut() throws Exception {
        OnHeapStore<String, String> store = newStore();
        store.put(OversizeMappingTest.KEY, OversizeMappingTest.OVER_SIZED_VALUE);
        OversizeMappingTest.assertNullMapping(store);
        store.put(OversizeMappingTest.KEY, OversizeMappingTest.VALUE);
        OversizeMappingTest.assertNotNullMapping(store);
    }

    @Test
    public void testPutIfAbsent() throws Exception {
        OnHeapStore<String, String> store = newStore();
        store.putIfAbsent(OversizeMappingTest.KEY, OversizeMappingTest.OVER_SIZED_VALUE, ( b) -> {
        });
        OversizeMappingTest.assertNullMapping(store);
    }

    @Test
    public void testReplace() throws Exception {
        OnHeapStore<String, String> store = newStore();
        store.put(OversizeMappingTest.KEY, OversizeMappingTest.VALUE);
        store.replace(OversizeMappingTest.KEY, OversizeMappingTest.OVER_SIZED_VALUE);
        OversizeMappingTest.assertNullMapping(store);
    }

    @Test
    public void testThreeArgReplace() throws Exception {
        OnHeapStore<String, String> store = newStore();
        store.put(OversizeMappingTest.KEY, OversizeMappingTest.VALUE);
        store.replace(OversizeMappingTest.KEY, OversizeMappingTest.VALUE, OversizeMappingTest.OVER_SIZED_VALUE);
        OversizeMappingTest.assertNullMapping(store);
    }

    @Test
    public void testCompute() throws Exception {
        OnHeapStore<String, String> store = newStore();
        store.getAndCompute(OversizeMappingTest.KEY, ( a, b) -> OVER_SIZED_VALUE);
        OversizeMappingTest.assertNullMapping(store);
        store.getAndCompute(OversizeMappingTest.KEY, ( a, b) -> VALUE);
        OversizeMappingTest.assertNotNullMapping(store);
        store.getAndCompute(OversizeMappingTest.KEY, ( a, b) -> OVER_SIZED_VALUE);
        OversizeMappingTest.assertNullMapping(store);
    }

    @Test
    public void testComputeIfAbsent() throws Exception {
        OnHeapStore<String, String> store = newStore();
        store.computeIfAbsent(OversizeMappingTest.KEY, ( a) -> OversizeMappingTest.OVER_SIZED_VALUE);
        OversizeMappingTest.assertNullMapping(store);
        store.put(OversizeMappingTest.KEY, OversizeMappingTest.VALUE);
        store.computeIfAbsent(OversizeMappingTest.KEY, ( a) -> OversizeMappingTest.OVER_SIZED_VALUE);
        OversizeMappingTest.assertNotNullMapping(store);
    }
}

