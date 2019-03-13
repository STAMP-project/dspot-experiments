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
package com.hazelcast.client.map;


import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.EntryView;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MapEvent;
import com.hazelcast.map.MapInterceptor;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.SqlPredicate;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.test.annotation.SlowTest;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientMapBasicTest extends AbstractClientMapTest {
    @Test
    public void testClientGetMap() {
        Assert.assertNotNull(AbstractClientMapTest.client.getMap(randomString()));
    }

    @Test
    public void testGetName() {
        String mapName = randomString();
        IMap<String, String> map = AbstractClientMapTest.client.getMap(mapName);
        Assert.assertEquals(mapName, map.getName());
    }

    @Test
    public void testSize_whenEmpty() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        Assert.assertEquals(0, map.size());
    }

    @Test
    public void testSize() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        map.put("key", "val");
        Assert.assertEquals(1, map.size());
    }

    @Test
    public void testSize_withMultiKeyPuts() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String oldValue = "oldValue";
        String newValue = "Val";
        map.put(key, oldValue);
        map.put(key, newValue);
        Assert.assertEquals(1, map.size());
    }

    @Test
    public void testSetTtlReturnsTrue() {
        final IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        map.put("key", "value");
        Assert.assertTrue(map.setTtl("key", 10, TimeUnit.SECONDS));
    }

    @Test
    public void testSetTtlReturnsFalse_whenKeyDoesNotExist() {
        final IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        Assert.assertFalse(map.setTtl("key", 10, TimeUnit.SECONDS));
    }

    @Test
    public void testSetTtlReturnsFalse_whenKeyIsAlreadyExpired() {
        final IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        map.put("key", "value", 1, TimeUnit.SECONDS);
        sleepAtLeastSeconds(5);
        Assert.assertFalse(map.setTtl("key", 10, TimeUnit.SECONDS));
    }

    @Test
    public void testAlterTTLOfAnEternalKey() {
        final IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        map.put("key", "value");
        map.setTtl("key", 1000, TimeUnit.MILLISECONDS);
        sleepAtLeastMillis(1000);
        Assert.assertNull(map.get("key"));
    }

    @Test
    @Category(SlowTest.class)
    public void testExtendTTLOfAKeyBeforeItExpires() {
        final IMap<String, String> map = AbstractClientMapTest.client.getMap("testSetTTLExtend");
        map.put("key", "value", 10, TimeUnit.SECONDS);
        sleepAtLeastMillis(TimeUnit.SECONDS.toMillis(1));
        // Make the entry eternal
        map.setTtl("key", 0, TimeUnit.DAYS);
        sleepAtLeastMillis(TimeUnit.SECONDS.toMillis(15));
        Assert.assertEquals("value", map.get("key"));
    }

    @Test
    public void testSetTtlConfiguresMapPolicyIfTTLIsNegative() {
        final IMap<String, String> map = AbstractClientMapTest.client.getMap("mapWithTTL");
        map.put("tempKey", "tempValue", 10, TimeUnit.SECONDS);
        map.setTtl("tempKey", (-1), TimeUnit.SECONDS);
        sleepAtLeastMillis(1000);
        Assert.assertNull(map.get("tempKey"));
    }

    @Test
    public void testIsEmpty_whenEmpty() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        Assert.assertTrue(map.isEmpty());
    }

    @Test
    public void testIsEmpty_whenNotEmpty() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        map.put("key", "val");
        Assert.assertFalse(map.isEmpty());
    }

    @Test
    public void testIsEmpty_afterPutRemove() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "key";
        map.put(key, "val");
        map.remove(key);
        Assert.assertTrue(map.isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void testPut_whenKeyNull() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String val = "Val";
        map.put(null, val);
    }

    @Test(expected = NullPointerException.class)
    public void testPut_whenValueNull() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        map.put(key, null);
    }

    @Test
    public void testPut() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "Val";
        String result = map.put(key, value);
        Assert.assertNull(result);
        Assert.assertEquals(value, map.get(key));
    }

    @Test
    public void testPut_whenKeyExists() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String oldValue = "oldValue";
        String newValue = "Val";
        map.put(key, oldValue);
        String result = map.put(key, newValue);
        Assert.assertEquals(oldValue, result);
        Assert.assertEquals(newValue, map.get(key));
    }

    @Test
    public void testPutTTL() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "Value";
        String result = map.put(key, value, 5, TimeUnit.MINUTES);
        Assert.assertNull(result);
        Assert.assertEquals(value, map.get(key));
    }

    @Test
    public void testPutTTL_whenKeyExists() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String oldValue = "oldValue";
        String newValue = "Val";
        map.put(key, oldValue);
        String result = map.put(key, newValue, 5, TimeUnit.MINUTES);
        Assert.assertEquals(oldValue, result);
        Assert.assertEquals(newValue, map.get(key));
    }

    @Test
    public void testPutTTL_AfterExpire() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "Value";
        String result = map.put(key, value, 1, TimeUnit.SECONDS);
        Assert.assertNull(result);
        sleepSeconds(2);
        Assert.assertNull(map.get(key));
    }

    @Test
    public void testPutTTL_AfterExpireWhenKeyExists() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String oldValue = "oldValue";
        String newValue = "Val";
        map.put(key, oldValue);
        String result = map.put(key, newValue, 1, TimeUnit.SECONDS);
        Assert.assertEquals(oldValue, result);
        sleepSeconds(2);
        Assert.assertNull(map.get(key));
    }

    @Test
    public void testPutAsync() throws Exception {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "Val";
        Future result = map.putAsync(key, value);
        Assert.assertNull(result.get());
        Assert.assertEquals(value, map.get(key));
    }

    @Test
    public void testPutAsync_whenKeyExists() throws Exception {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String oldValue = "oldValue";
        String newValue = "Val";
        map.put(key, oldValue);
        Future result = map.putAsync(key, newValue);
        Assert.assertEquals(oldValue, result.get());
        Assert.assertEquals(newValue, map.get(key));
    }

    @Test(expected = NullPointerException.class)
    public void testPutAsync_withKeyNull() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String val = "Val";
        map.putAsync(null, val);
    }

    @Test(expected = NullPointerException.class)
    public void testPutAsync_withValueNull() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "key";
        map.putAsync(key, null);
    }

    @Test
    public void testPutAsyncTTL() throws Exception {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "Val";
        Future result = map.putAsync(key, value, 5, TimeUnit.MINUTES);
        Assert.assertNull(result.get());
        Assert.assertEquals(value, map.get(key));
    }

    @Test
    public void testPutAsyncTTL_whenKeyExists() throws Exception {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String oldValue = "oldValue";
        String newValue = "Val";
        map.put(key, oldValue);
        Future result = map.putAsync(key, newValue, 5, TimeUnit.MINUTES);
        Assert.assertEquals(oldValue, result.get());
        Assert.assertEquals(newValue, map.get(key));
    }

    @Test
    public void testPutAsyncTTL_afterExpire() throws Exception {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "Val";
        Future result = map.putAsync(key, value, 1, TimeUnit.SECONDS);
        Assert.assertNull(result.get());
        sleepSeconds(2);
        Assert.assertNull(map.get(key));
    }

    @Test
    public void testPutAsyncTTL_afterExpireWhenKeyExists() throws Exception {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String oldValue = "oldValue";
        String newValue = "Val";
        map.put(key, oldValue);
        Future result = map.putAsync(key, newValue, 1, TimeUnit.SECONDS);
        Assert.assertEquals(oldValue, result.get());
        sleepSeconds(2);
        Assert.assertNull(map.get(key));
    }

    @Test
    public void testSetAsync() throws Exception {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "Val";
        Future<Void> result = map.setAsync(key, value);
        result.get();
        Assert.assertEquals(value, map.get(key));
    }

    @Test(expected = NullPointerException.class)
    public void testSetAsync_withKeyNull() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String val = "Val";
        map.setAsync(null, val);
    }

    @Test(expected = NullPointerException.class)
    public void testSetAsync_withValueNull() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "key";
        map.setAsync(key, null);
    }

    @Test
    public void testSetAsyncTTL() throws Exception {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "Val";
        Future<Void> result = map.setAsync(key, value, 5, TimeUnit.MINUTES);
        result.get();
        Assert.assertEquals(value, map.get(key));
    }

    @Test
    public void testSetAsyncTTL_afterExpire() throws Exception {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "Val";
        final CountDownLatch latch = new CountDownLatch(1);
        map.addEntryListener(new com.hazelcast.map.listener.EntryEvictedListener<String, String>() {
            public void entryEvicted(EntryEvent<String, String> event) {
                latch.countDown();
            }
        }, true);
        Future<Void> result = map.setAsync(key, value, 1, TimeUnit.SECONDS);
        result.get();
        assertOpenEventually(latch);
        Assert.assertNull(map.get(key));
    }

    @Test
    public void testSetAsyncTTL_afterExpireWhenKeyExists() throws Exception {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String oldValue = "oldValue";
        String newValue = "Val";
        final CountDownLatch latch = new CountDownLatch(1);
        map.addEntryListener(new com.hazelcast.map.listener.EntryEvictedListener<String, String>() {
            public void entryEvicted(EntryEvent<String, String> event) {
                latch.countDown();
            }
        }, true);
        map.set(key, oldValue);
        Future<Void> result = map.setAsync(key, newValue, 1, TimeUnit.SECONDS);
        result.get();
        assertOpenEventually(latch);
        Assert.assertNull(map.get(key));
    }

    @Test
    public void testTryPut_whenNotLocked() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "value";
        boolean result = map.tryPut(key, value, 1, TimeUnit.SECONDS);
        Assert.assertTrue(result);
        Assert.assertEquals(value, map.get(key));
    }

    @Test
    public void testTryPut_whenKeyPresentAndNotLocked() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String oldValue = "oldValue";
        String newValue = "Val";
        map.put(key, oldValue);
        boolean result = map.tryPut(key, newValue, 1, TimeUnit.SECONDS);
        Assert.assertTrue(result);
        Assert.assertEquals(newValue, map.get(key));
    }

    @Test(expected = NullPointerException.class)
    public void testPutIfAbsent_whenKeyNull() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String value = "Value";
        map.putIfAbsent(null, value);
    }

    @Test(expected = NullPointerException.class)
    public void testPutIfAbsent_whenValueNull() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "key";
        map.putIfAbsent(key, null);
    }

    @Test
    public void testPutIfAbsent() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "Value";
        String result = map.putIfAbsent(key, value);
        Assert.assertNull(result);
        Assert.assertEquals(value, map.get(key));
    }

    @Test
    public void testPutIfAbsent_whenKeyPresent() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "Value";
        map.put(key, value);
        String result = map.putIfAbsent(key, value);
        Assert.assertEquals(value, result);
        Assert.assertEquals(value, map.get(key));
    }

    @Test
    public void testPutIfAbsentNewValue_whenKeyPresent() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "Value";
        String newValue = "newValue";
        map.put(key, value);
        String result = map.putIfAbsent(key, newValue);
        Assert.assertEquals(value, result);
        Assert.assertEquals(value, map.get(key));
    }

    @Test
    public void testPutIfAbsentTTL() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "Value";
        String result = map.putIfAbsent(key, value, 5, TimeUnit.MINUTES);
        Assert.assertNull(result);
        Assert.assertEquals(value, map.get(key));
    }

    @Test
    public void testPutIfAbsentTTL_whenExpire() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "Value";
        String result = map.putIfAbsent(key, value, 1, TimeUnit.SECONDS);
        sleepSeconds(2);
        Assert.assertNull(result);
        Assert.assertNull(map.get(key));
    }

    @Test
    public void testPutIfAbsentTTL_whenKeyPresentAfterExpire() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "Value";
        map.put(key, value);
        String result = map.putIfAbsent(key, value, 1, TimeUnit.SECONDS);
        Assert.assertEquals(value, result);
        Assert.assertEquals(value, map.get(key));
    }

    @Test
    public void testPutIfAbsentTTL_whenKeyPresent() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "Value";
        map.put(key, value);
        String result = map.putIfAbsent(key, value, 5, TimeUnit.MINUTES);
        Assert.assertEquals(value, result);
        Assert.assertEquals(value, map.get(key));
    }

    @Test
    public void testPutIfAbsentNewValueTTL_whenKeyPresent() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "Value";
        String newValue = "newValue";
        map.put(key, value);
        String result = map.putIfAbsent(key, newValue, 5, TimeUnit.MINUTES);
        Assert.assertEquals(value, result);
        Assert.assertEquals(value, map.get(key));
    }

    @Test
    public void testClear_whenEmpty() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        map.clear();
        Assert.assertTrue(map.isEmpty());
    }

    @Test
    public void testClear() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "Value";
        map.put(key, value);
        map.clear();
        Assert.assertTrue(map.isEmpty());
    }

    @Test
    public void testContainsKey_whenKeyAbsent() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        Assert.assertFalse(map.containsKey("NOT_THERE"));
    }

    @Test(expected = NullPointerException.class)
    public void testContainsKey_whenKeyNull() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        map.containsKey(null);
    }

    @Test
    public void testContainsKey_whenKeyPresent() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "key";
        map.put(key, "val");
        Assert.assertTrue(map.containsKey(key));
    }

    @Test
    public void testContainsValue_whenValueAbsent() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        Assert.assertFalse(map.containsValue("NOT_THERE"));
    }

    @Test(expected = NullPointerException.class)
    public void testContainsValue_whenValueNull() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        map.containsValue(null);
    }

    @Test
    public void testContainsValue_whenValuePresent() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "key";
        String value = "value";
        map.put(key, value);
        Assert.assertTrue(map.containsValue(value));
    }

    @Test
    public void testContainsValue_whenMultiValuePresent() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String value = "value";
        map.put("key1", value);
        map.put("key2", value);
        Assert.assertTrue(map.containsValue(value));
    }

    @Test
    public void testGet_whenKeyPresent() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String val = "Val";
        map.put(key, val);
        Assert.assertEquals(val, map.get(key));
    }

    @Test
    public void testGet_whenKeyAbsent() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        Assert.assertNull(map.get("NOT_THERE"));
    }

    @Test(expected = NullPointerException.class)
    public void testGet_whenKeyNull() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        map.get(null);
    }

    @Test
    public void testGetAsync_whenKeyPresent() throws Exception {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String val = "Val";
        map.put(key, val);
        Future result = map.getAsync(key);
        Assert.assertEquals(val, result.get());
    }

    @Test
    public void testGetAsync_whenKeyAbsent() throws Exception {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        Future result = map.getAsync("NOT_THERE");
        Assert.assertNull(result.get());
    }

    @Test(expected = NullPointerException.class)
    public void testGetAsync_whenKeyNull() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        map.getAsync(null);
    }

    @Test
    public void testMapSet() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String val = "Val";
        map.set(key, val);
        Assert.assertEquals(val, map.get(key));
    }

    @Test
    public void testMapSet_whenKeyPresent() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String oldValue = "Val";
        String newValue = "newValue";
        map.set(key, oldValue);
        map.set(key, newValue);
        Assert.assertEquals(newValue, map.get(key));
    }

    @Test
    public void testMapSetTtl() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String val = "Val";
        map.set(key, val, 5, TimeUnit.MINUTES);
        Assert.assertEquals(val, map.get(key));
    }

    @Test
    public void testMapSetTtl_whenExpired() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String val = "Val";
        map.set(key, val, 1, TimeUnit.SECONDS);
        sleepSeconds(2);
        Assert.assertNull(map.get(key));
    }

    @Test
    public void testMapSetTtl_whenReplacingKeyAndExpired() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String newValue = "newValue";
        String oldValue = "oldvalue";
        map.set(key, oldValue);
        map.set(key, newValue, 1, TimeUnit.SECONDS);
        sleepSeconds(2);
        Assert.assertNull(map.get(key));
    }

    @Test
    public void testRemove_WhenKeyAbsent() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        Assert.assertNull(map.remove("NOT_THERE"));
    }

    @Test(expected = NullPointerException.class)
    public void testRemove_WhenKeyNull() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        Assert.assertNull(map.remove(null));
    }

    @Test
    public void testRemove_WhenKeyPresent() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "value";
        map.put(key, value);
        Assert.assertEquals(value, map.remove(key));
        Assert.assertNull(map.get(key));
    }

    @Test
    public void testRemoveKeyValue_WhenPresent() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "value";
        map.put(key, value);
        Assert.assertTrue(map.remove(key, value));
        Assert.assertNull(map.get(key));
    }

    @Test
    public void testRemoveKeyValue_WhenValueAbsent() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "value";
        map.put(key, value);
        Assert.assertFalse(map.remove(key, "NOT_THERE"));
        Assert.assertEquals(value, map.get(key));
    }

    @Test
    public void testRemoveKeyValue_WhenKeyAbsent() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "value";
        map.put(key, value);
        Assert.assertFalse(map.remove("NOT_THERE", value));
    }

    @Test
    public void testRemoveAsync() throws Exception {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "value";
        map.put(key, value);
        Future result = map.removeAsync(key);
        Assert.assertEquals(value, result.get());
        Assert.assertNull(map.get(key));
    }

    @Test
    public void testRemoveAsync_whenKeyNotPresent() throws Exception {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        Future result = map.removeAsync("NOT_THERE");
        Assert.assertNull(result.get());
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveAsync_whenKeyNull() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        map.removeAsync(null);
    }

    @Test
    public void testTryRemove_WhenKeyPresentAndNotLocked() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "value";
        map.put(key, value);
        boolean result = map.tryRemove(key, 1, TimeUnit.SECONDS);
        Assert.assertTrue(result);
        Assert.assertNull(map.get(key));
    }

    @Test
    public void testTryRemove_WhenKeyAbsentAndNotLocked() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        boolean result = map.tryRemove(key, 1, TimeUnit.SECONDS);
        Assert.assertFalse(result);
    }

    @Test(expected = NullPointerException.class)
    public void testDelete_whenKeyNull() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        map.delete(null);
    }

    @Test
    public void testDelete_whenKeyPresent() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "value";
        map.put(key, value);
        map.delete(key);
        Assert.assertEquals(0, map.size());
    }

    @Test
    public void testDelete_whenKeyAbsent() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "value";
        map.put(key, value);
        map.delete("NOT_THERE");
        Assert.assertEquals(1, map.size());
    }

    @Test
    public void testEvict_whenKeyAbsent() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        boolean result = map.evict("NOT_THERE");
        Assert.assertFalse(result);
    }

    @Test(expected = NullPointerException.class)
    public void testEvict_whenKeyNull() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        map.evict(null);
    }

    @Test
    public void testEvict() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "value";
        map.put(key, value);
        boolean result = map.evict(key);
        Assert.assertTrue(result);
        Assert.assertNull(map.get(key));
    }

    @Test
    public void testPutAll() {
        int max = 100;
        IMap<Integer, Integer> map = AbstractClientMapTest.client.getMap(randomString());
        Map<Integer, Integer> expected = new HashMap<Integer, Integer>();
        for (int i = 0; i < max; i++) {
            expected.put(i, i);
        }
        map.putAll(expected);
        Assert.assertEquals(max, map.size());
        for (Integer key : expected.keySet()) {
            Integer value = map.get(key);
            Integer expectedValue = expected.get(key);
            Assert.assertEquals(expectedValue, value);
        }
    }

    @Test
    public void testGetAll() {
        int max = 100;
        IMap<Integer, Integer> map = AbstractClientMapTest.client.getMap(randomString());
        Map<Integer, Integer> expected = new HashMap<Integer, Integer>();
        for (int i = 0; i < max; i++) {
            map.put(i, i);
            expected.put(i, i);
        }
        Map<Integer, Integer> result = map.getAll(expected.keySet());
        for (Integer key : expected.keySet()) {
            Integer value = result.get(key);
            Integer expectedValue = expected.get(key);
            Assert.assertEquals(expectedValue, value);
        }
    }

    @Test
    public void testGetAll_whenMapEmpty() {
        int max = 10;
        IMap<Integer, Integer> map = AbstractClientMapTest.client.getMap(randomString());
        Map<Integer, Integer> expected = new HashMap<Integer, Integer>();
        for (int i = 0; i < max; i++) {
            expected.put(i, i);
        }
        Map<Integer, Integer> result = map.getAll(expected.keySet());
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testReplace_whenKeyValueAbsent() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "value";
        Assert.assertNull(map.replace(key, value));
        Assert.assertNull(map.get(key));
    }

    @Test
    public void testReplace() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String oldValue = "value";
        String newValue = "NewValue";
        map.put(key, oldValue);
        String result = map.replace(key, newValue);
        Assert.assertEquals(oldValue, result);
        Assert.assertEquals(newValue, map.get(key));
    }

    @Test
    public void testReplaceKeyValue() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "value";
        String newValue = "NewValue";
        map.put(key, value);
        boolean result = map.replace(key, value, newValue);
        Assert.assertTrue(result);
        Assert.assertEquals(newValue, map.get(key));
    }

    @Test
    public void testReplaceKeyValue_whenValueAbsent() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "value";
        String newValue = "NewValue";
        map.put(key, value);
        boolean result = map.replace(key, "NOT_THERE", newValue);
        Assert.assertFalse(result);
        Assert.assertEquals(value, map.get(key));
    }

    @Test
    public void testPutTransient() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "value";
        map.putTransient(key, value, 5, TimeUnit.MINUTES);
        Assert.assertEquals(value, map.get(key));
    }

    @Test
    public void testPutTransient_whenExpire() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "value";
        map.putTransient(key, value, 1, TimeUnit.SECONDS);
        sleepSeconds(2);
        Assert.assertNull(map.get(key));
    }

    @Test
    public void testPutTransient_whenKeyPresent() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String oldValue = "oldValue";
        String newValue = "newValue";
        map.put(key, oldValue);
        map.putTransient(key, newValue, 5, TimeUnit.MINUTES);
        Assert.assertEquals(newValue, map.get(key));
    }

    @Test
    public void testPutTransient_whenKeyPresentAfterExpire() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String oldValue = "oldValue";
        String newValue = "newValue";
        map.put(key, oldValue);
        map.putTransient(key, newValue, 1, TimeUnit.SECONDS);
        sleepSeconds(2);
        Assert.assertNull(map.get(key));
    }

    @Test
    public void testGetEntryView_whenKeyAbsent() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        EntryView view = map.getEntryView("NOT_THERE");
        Assert.assertNull(view);
    }

    @Test
    public void testGetEntryView() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "Value";
        map.put(key, value);
        EntryView view = map.getEntryView(key);
        Assert.assertEquals(key, view.getKey());
        Assert.assertEquals(value, view.getValue());
        Assert.assertEquals(((Long) (Long.MAX_VALUE)), view.getMaxIdle());
        Assert.assertEquals(Long.MAX_VALUE, view.getTtl());
    }

    @Test
    public void testGetEntryView_withExpirationSettings() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "Value";
        long maxIdleMins = 33;
        long ttlMins = 20;
        map.put(key, value, ttlMins, TimeUnit.MINUTES, maxIdleMins, TimeUnit.MINUTES);
        EntryView view = map.getEntryView(key);
        Assert.assertEquals(key, view.getKey());
        Assert.assertEquals(value, view.getValue());
        Assert.assertEquals(((Long) (TimeUnit.MINUTES.toMillis(maxIdleMins))), view.getMaxIdle());
        Assert.assertEquals(TimeUnit.MINUTES.toMillis(ttlMins), view.getTtl());
    }

    @Test
    public void testGetEntryView_withExpirationSettings_infMaxIdle() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        String key = "Key";
        String value = "Value";
        long ttlMins = 20;
        map.put(key, value, ttlMins, TimeUnit.MINUTES, 0, TimeUnit.MILLISECONDS);
        EntryView view = map.getEntryView(key);
        Assert.assertEquals(key, view.getKey());
        Assert.assertEquals(value, view.getValue());
        Assert.assertEquals(((Long) (Long.MAX_VALUE)), view.getMaxIdle());
        Assert.assertEquals(TimeUnit.MINUTES.toMillis(ttlMins), view.getTtl());
    }

    @Test
    public void testGetEntryView_withExpirationSettings_mapDefault() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap("mapWithMaxIdle");
        String key = "Key";
        String value = "Value";
        long ttlMins = 20;
        map.put(key, value, ttlMins, TimeUnit.MINUTES, (-1), TimeUnit.SECONDS);
        EntryView view = map.getEntryView(key);
        Assert.assertEquals(key, view.getKey());
        Assert.assertEquals(value, view.getValue());
        Assert.assertEquals(Long.valueOf(11000), view.getMaxIdle());
        Assert.assertEquals(TimeUnit.MINUTES.toMillis(ttlMins), view.getTtl());
    }

    @Test
    public void testKeySet_whenEmpty() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        Set keySet = map.keySet();
        Assert.assertTrue(keySet.isEmpty());
    }

    @Test
    public void testKeySet() {
        int max = 81;
        IMap<Integer, String> map = AbstractClientMapTest.client.getMap(randomString());
        Set<Integer> expected = new TreeSet<Integer>();
        for (int key = 0; key < max; key++) {
            String value = key + "value";
            expected.add(key);
            map.put(key, value);
        }
        Set<Integer> keySet = map.keySet();
        Assert.assertEquals(expected, keySet);
    }

    @Test
    public void testKeySet_withPredicate() {
        int max = 44;
        IMap<Integer, String> map = AbstractClientMapTest.client.getMap(randomString());
        Set<Integer> expected = new TreeSet<Integer>();
        for (int key = 0; key < max; key++) {
            String value = key + "value";
            map.put(key, value);
        }
        expected.add(4);
        Set<Integer> keySet = map.keySet(new SqlPredicate("this == 4value"));
        Assert.assertEquals(expected, keySet);
    }

    @Test
    public void testValues_whenEmpty() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        Collection values = map.values();
        Assert.assertTrue(values.isEmpty());
    }

    @Test
    public void testValues() {
        int max = 23;
        IMap<Integer, String> map = AbstractClientMapTest.client.getMap(randomString());
        Set<String> expected = new TreeSet<String>();
        for (int key = 0; key < max; key++) {
            String value = key + "value";
            expected.add(value);
            map.put(key, value);
        }
        Collection<String> collection = map.values();
        Set<String> resultSet = new TreeSet<String>(collection);
        Assert.assertEquals(expected, resultSet);
    }

    @Test
    public void testValues_withPredicate() {
        int max = 27;
        IMap<Integer, String> map = AbstractClientMapTest.client.getMap(randomString());
        Set<String> expected = new TreeSet<String>();
        for (int key = 0; key < max; key++) {
            String value = key + "value";
            map.put(key, value);
        }
        expected.add("4value");
        Collection<String> collection = map.values(new SqlPredicate("this == 4value"));
        Set<String> resultSet = new TreeSet<String>(collection);
        Assert.assertEquals(expected, resultSet);
    }

    @Test
    public void testEntrySet_whenEmpty() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        Set<Map.Entry<String, String>> entrySet = map.entrySet();
        Assert.assertTrue(entrySet.isEmpty());
    }

    @Test
    public void testEntrySet() {
        int max = 34;
        IMap<Integer, String> map = AbstractClientMapTest.client.getMap(randomString());
        Map<Integer, String> expected = new HashMap<Integer, String>();
        for (int key = 0; key < max; key++) {
            String value = key + "value";
            expected.put(key, value);
            map.put(key, value);
        }
        Set<Map.Entry<Integer, String>> entrySet = map.entrySet();
        for (Map.Entry<Integer, String> entry : entrySet) {
            Integer key = entry.getKey();
            String value = entry.getValue();
            String expectedValue = expected.get(key);
            Assert.assertEquals(expectedValue, value);
        }
    }

    @Test
    public void github_11489_verifyNoFailingCastOnValue() throws Exception {
        IMap<Integer, Integer> test = AbstractClientMapTest.client.getMap("github_11489");
        for (int i = 0; i < 1000; i++) {
            test.put(i, i);
        }
        Collection<Integer> values = test.values(new ClientMapBasicTest.TestPagingPredicate(100));
        Type genericSuperClass = values.getClass().getGenericSuperclass();
        Type actualType = ((ParameterizedType) (genericSuperClass)).getActualTypeArguments()[0];
        // Raw class is expected. ParameterizedType-s cause troubles to Jackson serializer.
        assertInstanceOf(Class.class, actualType);
    }

    private static class TestPagingPredicate extends PagingPredicate {
        TestPagingPredicate(int pageSize) {
            super(pageSize);
        }

        @Override
        public boolean apply(Map.Entry mapEntry) {
            return true;
        }
    }

    @Test
    public void testEntrySet_withPredicate() {
        int max = 44;
        IMap<Integer, String> map = AbstractClientMapTest.client.getMap(randomString());
        for (int key = 0; key < max; key++) {
            String value = key + "value";
            map.put(key, value);
        }
        Set<Map.Entry<Integer, String>> entrySet = map.entrySet(new SqlPredicate("this == 1value"));
        Assert.assertEquals(1, entrySet.size());
        Map.Entry<Integer, String> entry = entrySet.iterator().next();
        Assert.assertEquals(1, ((int) (entry.getKey())));
        Assert.assertEquals("1value", entry.getValue());
    }

    @Test
    public void testMapStatistics_withClientOperations() {
        String mapName = randomString();
        IMap<Integer, Integer> member1Map = AbstractClientMapTest.member1.getMap(mapName);
        member1Map.addInterceptor(new ClientMapBasicTest.DelayGetRemoveMapInterceptor());
        LocalMapStats stats1 = member1Map.getLocalMapStats();
        LocalMapStats stats2 = AbstractClientMapTest.member2.getMap(mapName).getLocalMapStats();
        IMap<Integer, Integer> map = AbstractClientMapTest.client.getMap(mapName);
        int operationCount = 1123;
        for (int i = 0; i < operationCount; i++) {
            map.put(i, i);
            map.get(i);
            map.remove(i);
        }
        Assert.assertEquals(((("put count: stats1" + stats1) + " stats2:") + stats2), operationCount, ((stats1.getPutOperationCount()) + (stats2.getPutOperationCount())));
        Assert.assertEquals(((("get count : stats1" + stats1) + " stats2:") + stats2), operationCount, ((stats1.getGetOperationCount()) + (stats2.getGetOperationCount())));
        Assert.assertEquals(((("remove count : stats1" + stats1) + " stats2:") + stats2), operationCount, ((stats1.getRemoveOperationCount()) + (stats2.getRemoveOperationCount())));
        Assert.assertTrue(((("put latency : stats1" + stats1) + " stats2:") + stats2), (0 < ((stats1.getTotalPutLatency()) + (stats2.getTotalPutLatency()))));
        Assert.assertTrue(((("get latency : stats1" + stats1) + " stats2:") + stats2), (0 < ((stats1.getTotalGetLatency()) + (stats2.getTotalGetLatency()))));
        Assert.assertTrue(((("remove latency : stats1" + stats1) + " stats2:") + stats2), (0 < ((stats1.getTotalRemoveLatency()) + (stats2.getTotalRemoveLatency()))));
    }

    @Test(expected = UnsupportedOperationException.class)
    @SuppressWarnings("deprecation")
    public void testAddLocalEntryListener() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        map.addLocalEntryListener(new ClientMapBasicTest.EmptyEntryListener());
    }

    @Test(expected = UnsupportedOperationException.class)
    @SuppressWarnings("deprecation")
    public void testAddLocalEntryListener_WithPredicate() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        map.addLocalEntryListener(new ClientMapBasicTest.EmptyEntryListener(), new ClientMapBasicTest.FalsePredicate(), true);
    }

    @Test(expected = UnsupportedOperationException.class)
    @SuppressWarnings("deprecation")
    public void testAddLocalEntryListener_WithPredicateAndKey() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        map.addLocalEntryListener(new ClientMapBasicTest.EmptyEntryListener(), new ClientMapBasicTest.FalsePredicate(), "Key", true);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testLocalKeySet() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        map.localKeySet();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testLocalKeySet_WithPredicate() {
        IMap<String, String> map = AbstractClientMapTest.client.getMap(randomString());
        map.localKeySet(new ClientMapBasicTest.FalsePredicate());
    }

    private static class DelayGetRemoveMapInterceptor implements MapInterceptor , Serializable {
        @Override
        public Object interceptGet(Object value) {
            sleepMillis(1);
            return value;
        }

        @Override
        public void afterGet(Object value) {
        }

        @Override
        public Object interceptPut(Object oldValue, Object newValue) {
            sleepMillis(1);
            return newValue;
        }

        @Override
        public void afterPut(Object value) {
        }

        @Override
        public Object interceptRemove(Object removedValue) {
            sleepMillis(1);
            return removedValue;
        }

        @Override
        public void afterRemove(Object value) {
        }
    }

    private static class EmptyEntryListener implements EntryListener<String, String> {
        public void entryAdded(EntryEvent event) {
        }

        public void entryRemoved(EntryEvent event) {
        }

        public void entryUpdated(EntryEvent event) {
        }

        public void entryEvicted(EntryEvent event) {
        }

        public void mapEvicted(MapEvent event) {
        }

        public void mapCleared(MapEvent event) {
        }
    }

    private static class FalsePredicate implements Predicate<String, String> {
        public boolean apply(Map.Entry mapEntry) {
            return false;
        }
    }
}

