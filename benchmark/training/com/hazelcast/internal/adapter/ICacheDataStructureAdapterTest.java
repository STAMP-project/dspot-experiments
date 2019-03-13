/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.internal.adapter;


import TruePredicate.INSTANCE;
import com.hazelcast.cache.HazelcastExpiryPolicy;
import com.hazelcast.cache.ICache;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.processor.EntryProcessorResult;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ICacheDataStructureAdapterTest extends HazelcastTestSupport {
    private ICache<Integer, String> cache;

    private ICache<Integer, String> cacheWithLoader;

    private ICacheDataStructureAdapter<Integer, String> adapter;

    private ICacheDataStructureAdapter<Integer, String> adapterWithLoader;

    @Test
    public void testSize() {
        cache.put(23, "foo");
        cache.put(42, "bar");
        Assert.assertEquals(2, adapter.size());
    }

    @Test
    public void testGet() {
        cache.put(42, "foobar");
        String result = adapter.get(42);
        Assert.assertEquals("foobar", result);
    }

    @Test
    public void testGetAsync() throws Exception {
        cache.put(42, "foobar");
        Future<String> future = adapter.getAsync(42);
        String result = future.get();
        Assert.assertEquals("foobar", result);
    }

    @Test
    public void testSet() {
        adapter.set(23, "test");
        Assert.assertEquals("test", cache.get(23));
    }

    @Test
    public void testSetAsync() throws Exception {
        cache.put(42, "oldValue");
        ICompletableFuture<Void> future = adapter.setAsync(42, "newValue");
        Void oldValue = future.get();
        Assert.assertNull(oldValue);
        Assert.assertEquals("newValue", cache.get(42));
    }

    @Test(expected = MethodNotAvailableException.class)
    public void testSetAsyncWithTtl() {
        adapter.setAsync(42, "value", 1, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testSetAsyncWithExpiryPolicy() throws Exception {
        ExpiryPolicy expiryPolicy = new HazelcastExpiryPolicy(1000, 1, 1, TimeUnit.MILLISECONDS);
        adapter.setAsync(42, "value", expiryPolicy).get();
        String value = cache.get(42);
        if (value != null) {
            Assert.assertEquals("value", value);
            HazelcastTestSupport.sleepMillis(1100);
            Assert.assertNull(cache.get(42));
        }
    }

    @Test
    public void testPut() {
        cache.put(42, "oldValue");
        String oldValue = adapter.put(42, "newValue");
        Assert.assertEquals("oldValue", oldValue);
        Assert.assertEquals("newValue", cache.get(42));
    }

    @Test
    public void testPutAsync() throws Exception {
        cache.put(42, "oldValue");
        ICompletableFuture<String> future = adapter.putAsync(42, "newValue");
        String oldValue = future.get();
        Assert.assertEquals("oldValue", oldValue);
        Assert.assertEquals("newValue", cache.get(42));
    }

    @Test(expected = MethodNotAvailableException.class)
    public void testPutAsyncWithTtl() {
        adapter.putAsync(42, "value", 1, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testPutAsyncWithExpiryPolicy() throws Exception {
        cache.put(42, "oldValue");
        ExpiryPolicy expiryPolicy = new HazelcastExpiryPolicy(1000, 1, 1, TimeUnit.MILLISECONDS);
        ICompletableFuture<String> future = adapter.putAsync(42, "newValue", expiryPolicy);
        String oldValue = future.get();
        String newValue = cache.get(42);
        Assert.assertEquals("oldValue", oldValue);
        if (newValue != null) {
            Assert.assertEquals("newValue", newValue);
            HazelcastTestSupport.sleepMillis(1100);
            Assert.assertNull(cache.get(42));
        }
    }

    @Test(expected = MethodNotAvailableException.class)
    public void testPutTransient() {
        adapter.putTransient(42, "value", 1, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testPutIfAbsent() {
        cache.put(42, "oldValue");
        Assert.assertTrue(adapter.putIfAbsent(23, "newValue"));
        Assert.assertFalse(adapter.putIfAbsent(42, "newValue"));
        Assert.assertEquals("newValue", cache.get(23));
        Assert.assertEquals("oldValue", cache.get(42));
    }

    @Test
    public void testPutIfAbsentAsync() throws Exception {
        cache.put(42, "oldValue");
        Assert.assertTrue(adapter.putIfAbsentAsync(23, "newValue").get());
        Assert.assertFalse(adapter.putIfAbsentAsync(42, "newValue").get());
        Assert.assertEquals("newValue", cache.get(23));
        Assert.assertEquals("oldValue", cache.get(42));
    }

    @Test
    public void testReplace() {
        cache.put(42, "oldValue");
        String oldValue = adapter.replace(42, "newValue");
        Assert.assertEquals("oldValue", oldValue);
        Assert.assertEquals("newValue", cache.get(42));
    }

    @Test
    public void testReplaceWithOldValue() {
        cache.put(42, "oldValue");
        Assert.assertFalse(adapter.replace(42, "foobar", "newValue"));
        Assert.assertTrue(adapter.replace(42, "oldValue", "newValue"));
        Assert.assertEquals("newValue", cache.get(42));
    }

    @Test
    public void testRemove() {
        cache.put(23, "value-23");
        Assert.assertTrue(cache.containsKey(23));
        Assert.assertEquals("value-23", adapter.remove(23));
        Assert.assertFalse(cache.containsKey(23));
    }

    @Test
    public void testRemoveWithOldValue() {
        cache.put(23, "value-23");
        Assert.assertTrue(cache.containsKey(23));
        Assert.assertFalse(adapter.remove(23, "foobar"));
        Assert.assertTrue(adapter.remove(23, "value-23"));
        Assert.assertFalse(cache.containsKey(23));
    }

    @Test
    public void testRemoveAsync() throws Exception {
        cache.put(23, "value-23");
        Assert.assertTrue(cache.containsKey(23));
        String value = adapter.removeAsync(23).get();
        Assert.assertEquals("value-23", value);
        Assert.assertFalse(cache.containsKey(23));
    }

    @Test
    public void testDelete() {
        cache.put(23, "value-23");
        Assert.assertTrue(cache.containsKey(23));
        adapter.delete(23);
        Assert.assertFalse(cache.containsKey(23));
    }

    @Test
    public void testDeleteAsync() throws Exception {
        cache.put(23, "value-23");
        Assert.assertTrue(cache.containsKey(23));
        adapter.deleteAsync(23).get();
        Assert.assertFalse(cache.containsKey(23));
    }

    @Test(expected = MethodNotAvailableException.class)
    public void testEvict() {
        adapter.evict(23);
    }

    @Test
    public void testInvoke() {
        cache.put(23, "value-23");
        cache.put(42, "value-42");
        String result = adapter.invoke(23, new ICacheReplaceEntryProcessor(), "value", "newValue");
        Assert.assertEquals("newValue-23", result);
        Assert.assertEquals("newValue-23", cache.get(23));
        Assert.assertEquals("value-42", cache.get(42));
    }

    @Test(expected = MethodNotAvailableException.class)
    public void testExecuteOnKey() {
        adapter.executeOnKey(23, new IMapReplaceEntryProcessor("value", "newValue"));
    }

    @Test(expected = MethodNotAvailableException.class)
    public void testExecuteOnKeys() {
        Set<Integer> keys = new HashSet<Integer>(Collections.singleton(23));
        adapter.executeOnKeys(keys, new IMapReplaceEntryProcessor("value", "newValue"));
    }

    @Test(expected = MethodNotAvailableException.class)
    public void testExecuteOnEntries() {
        adapter.executeOnEntries(new IMapReplaceEntryProcessor("value", "newValue"));
    }

    @Test(expected = MethodNotAvailableException.class)
    public void testExecuteOnEntriesWithPredicate() {
        adapter.executeOnEntries(new IMapReplaceEntryProcessor("value", "newValue"), INSTANCE);
    }

    @Test
    public void testContainsKey() {
        cache.put(23, "value-23");
        Assert.assertTrue(adapter.containsKey(23));
        Assert.assertFalse(adapter.containsKey(42));
    }

    @Test(expected = MethodNotAvailableException.class)
    public void testLoadAll() {
        adapterWithLoader.loadAll(true);
    }

    @Test(expected = MethodNotAvailableException.class)
    public void testLoadAllWithKeys() {
        adapterWithLoader.loadAll(Collections.<Integer>emptySet(), true);
    }

    @Test
    public void testLoadAllWithListener() {
        ICacheCompletionListener listener = new ICacheCompletionListener();
        cacheWithLoader.put(23, "value-23");
        adapterWithLoader.loadAll(Collections.singleton(23), true, listener);
        listener.await();
        Assert.assertEquals("newValue-23", cacheWithLoader.get(23));
    }

    @Test
    public void testGetAll() {
        cache.put(23, "value-23");
        cache.put(42, "value-42");
        Map<Integer, String> expectedResult = new HashMap<Integer, String>();
        expectedResult.put(23, "value-23");
        expectedResult.put(42, "value-42");
        Map<Integer, String> result = adapter.getAll(expectedResult.keySet());
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void testPutAll() {
        Map<Integer, String> expectedResult = new HashMap<Integer, String>();
        expectedResult.put(23, "value-23");
        expectedResult.put(42, "value-42");
        adapter.putAll(expectedResult);
        Assert.assertEquals(expectedResult.size(), cache.size());
        for (Integer key : expectedResult.keySet()) {
            Assert.assertTrue(cache.containsKey(key));
        }
    }

    @Test
    public void testRemoveAll() {
        cache.put(23, "value-23");
        cache.put(42, "value-42");
        adapter.removeAll();
        Assert.assertEquals(0, cache.size());
    }

    @Test
    public void testRemoveAllWithKeys() {
        cache.put(23, "value-23");
        cache.put(42, "value-42");
        adapter.removeAll(Collections.singleton(42));
        Assert.assertEquals(1, cache.size());
        Assert.assertTrue(cache.containsKey(23));
        Assert.assertFalse(cache.containsKey(42));
    }

    @Test(expected = MethodNotAvailableException.class)
    public void testEvictAll() {
        adapter.evictAll();
    }

    @Test
    public void testInvokeAll() {
        cache.put(23, "value-23");
        cache.put(42, "value-42");
        cache.put(65, "value-65");
        Set<Integer> keys = new HashSet<Integer>(Arrays.asList(23, 65, 88));
        Map<Integer, EntryProcessorResult<String>> resultMap = adapter.invokeAll(keys, new ICacheReplaceEntryProcessor(), "value", "newValue");
        Assert.assertEquals(2, resultMap.size());
        Assert.assertEquals("newValue-23", resultMap.get(23).get());
        Assert.assertEquals("newValue-65", resultMap.get(65).get());
        Assert.assertEquals("newValue-23", cache.get(23));
        Assert.assertEquals("value-42", cache.get(42));
        Assert.assertEquals("newValue-65", cache.get(65));
        Assert.assertNull(cache.get(88));
    }

    @Test
    public void testClear() {
        cache.put(23, "foobar");
        adapter.clear();
        Assert.assertEquals(0, cache.size());
    }

    @Test
    public void testClose() {
        adapter.close();
        Assert.assertTrue(cache.isClosed());
    }

    @Test
    public void testDestroy() {
        adapter.destroy();
        Assert.assertTrue(cache.isDestroyed());
    }

    @Test(expected = MethodNotAvailableException.class)
    public void testGetLocalMapStats() {
        adapter.getLocalMapStats();
    }
}

