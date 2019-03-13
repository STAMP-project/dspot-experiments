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
package org.apache.camel.util;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class CaseInsensitiveMapTest extends Assert {
    @Test
    public void testLookupCaseAgnostic() {
        Map<String, Object> map = new CaseInsensitiveMap();
        Assert.assertNull(map.get("foo"));
        map.put("foo", "cheese");
        Assert.assertEquals("cheese", map.get("foo"));
        Assert.assertEquals("cheese", map.get("Foo"));
        Assert.assertEquals("cheese", map.get("FOO"));
    }

    @Test
    public void testLookupCaseAgnosticAddHeader() {
        Map<String, Object> map = new CaseInsensitiveMap();
        Assert.assertNull(map.get("foo"));
        map.put("foo", "cheese");
        Assert.assertEquals("cheese", map.get("foo"));
        Assert.assertEquals("cheese", map.get("Foo"));
        Assert.assertEquals("cheese", map.get("FOO"));
        Assert.assertNull(map.get("unknown"));
        map.put("bar", "beer");
        Assert.assertEquals("beer", map.get("bar"));
        Assert.assertEquals("beer", map.get("Bar"));
        Assert.assertEquals("beer", map.get("BAR"));
        Assert.assertNull(map.get("unknown"));
    }

    @Test
    public void testLookupCaseAgnosticAddHeader2() {
        Map<String, Object> map = new CaseInsensitiveMap();
        Assert.assertNull(map.get("foo"));
        map.put("foo", "cheese");
        Assert.assertEquals("cheese", map.get("FOO"));
        Assert.assertEquals("cheese", map.get("foo"));
        Assert.assertEquals("cheese", map.get("Foo"));
        Assert.assertNull(map.get("unknown"));
        map.put("bar", "beer");
        Assert.assertEquals("beer", map.get("BAR"));
        Assert.assertEquals("beer", map.get("bar"));
        Assert.assertEquals("beer", map.get("Bar"));
        Assert.assertNull(map.get("unknown"));
    }

    @Test
    public void testLookupCaseAgnosticAddHeaderRemoveHeader() {
        Map<String, Object> map = new CaseInsensitiveMap();
        Assert.assertNull(map.get("foo"));
        map.put("foo", "cheese");
        Assert.assertEquals("cheese", map.get("foo"));
        Assert.assertEquals("cheese", map.get("Foo"));
        Assert.assertEquals("cheese", map.get("FOO"));
        Assert.assertNull(map.get("unknown"));
        map.put("bar", "beer");
        Assert.assertEquals("beer", map.get("bar"));
        Assert.assertEquals("beer", map.get("Bar"));
        Assert.assertEquals("beer", map.get("BAR"));
        Assert.assertNull(map.get("unknown"));
        map.remove("bar");
        Assert.assertNull(map.get("bar"));
        Assert.assertNull(map.get("unknown"));
    }

    @Test
    public void testSetWithDifferentCase() {
        Map<String, Object> map = new CaseInsensitiveMap();
        Assert.assertNull(map.get("foo"));
        map.put("foo", "cheese");
        map.put("Foo", "bar");
        Assert.assertEquals("bar", map.get("FOO"));
        Assert.assertEquals("bar", map.get("foo"));
        Assert.assertEquals("bar", map.get("Foo"));
    }

    @Test
    public void testRemoveWithDifferentCase() {
        Map<String, Object> map = new CaseInsensitiveMap();
        Assert.assertNull(map.get("foo"));
        map.put("foo", "cheese");
        map.put("Foo", "bar");
        Assert.assertEquals("bar", map.get("FOO"));
        Assert.assertEquals("bar", map.get("foo"));
        Assert.assertEquals("bar", map.get("Foo"));
        map.remove("FOO");
        Assert.assertEquals(null, map.get("foo"));
        Assert.assertEquals(null, map.get("Foo"));
        Assert.assertEquals(null, map.get("FOO"));
        Assert.assertTrue(map.isEmpty());
    }

    @Test
    public void testPutAll() {
        Map<String, Object> map = new CaseInsensitiveMap();
        Assert.assertNull(map.get("foo"));
        Map<String, Object> other = new CaseInsensitiveMap();
        other.put("Foo", "cheese");
        other.put("bar", 123);
        map.putAll(other);
        Assert.assertEquals("cheese", map.get("FOO"));
        Assert.assertEquals("cheese", map.get("foo"));
        Assert.assertEquals("cheese", map.get("Foo"));
        Assert.assertEquals(123, map.get("BAR"));
        Assert.assertEquals(123, map.get("bar"));
        Assert.assertEquals(123, map.get("BaR"));
        // key case should be preserved
        Map<String, Object> keys = new HashMap<>();
        keys.putAll(map);
        Assert.assertEquals("cheese", keys.get("Foo"));
        Assert.assertNull(keys.get("foo"));
        Assert.assertNull(keys.get("FOO"));
        Assert.assertEquals(123, keys.get("bar"));
        Assert.assertNull(keys.get("Bar"));
        Assert.assertNull(keys.get("BAR"));
    }

    @Test
    public void testPutAllOther() {
        Map<String, Object> map = new CaseInsensitiveMap();
        Assert.assertNull(map.get("foo"));
        Map<String, Object> other = new HashMap<>();
        other.put("Foo", "cheese");
        other.put("bar", 123);
        map.putAll(other);
        Assert.assertEquals("cheese", map.get("FOO"));
        Assert.assertEquals("cheese", map.get("foo"));
        Assert.assertEquals("cheese", map.get("Foo"));
        Assert.assertEquals(123, map.get("BAR"));
        Assert.assertEquals(123, map.get("bar"));
        Assert.assertEquals(123, map.get("BaR"));
    }

    @Test
    public void testPutAllEmpty() {
        Map<String, Object> map = new CaseInsensitiveMap();
        map.put("foo", "cheese");
        Map<String, Object> other = new HashMap<>();
        map.putAll(other);
        Assert.assertEquals("cheese", map.get("FOO"));
        Assert.assertEquals("cheese", map.get("foo"));
        Assert.assertEquals("cheese", map.get("Foo"));
        Assert.assertEquals(1, map.size());
    }

    @Test
    public void testConstructFromOther() {
        Map<String, Object> other = new HashMap<>();
        other.put("Foo", "cheese");
        other.put("bar", 123);
        Map<String, Object> map = new CaseInsensitiveMap(other);
        Assert.assertEquals("cheese", map.get("FOO"));
        Assert.assertEquals("cheese", map.get("foo"));
        Assert.assertEquals("cheese", map.get("Foo"));
        Assert.assertEquals(123, map.get("BAR"));
        Assert.assertEquals(123, map.get("bar"));
        Assert.assertEquals(123, map.get("BaR"));
    }

    @Test
    public void testKeySet() {
        Map<String, Object> map = new CaseInsensitiveMap();
        map.put("Foo", "cheese");
        map.put("BAR", 123);
        map.put("baZ", "beer");
        Set<String> keys = map.keySet();
        // we should be able to lookup no matter what case
        Assert.assertTrue(keys.contains("Foo"));
        Assert.assertTrue(keys.contains("foo"));
        Assert.assertTrue(keys.contains("FOO"));
        Assert.assertTrue(keys.contains("BAR"));
        Assert.assertTrue(keys.contains("bar"));
        Assert.assertTrue(keys.contains("Bar"));
        Assert.assertTrue(keys.contains("baZ"));
        Assert.assertTrue(keys.contains("baz"));
        Assert.assertTrue(keys.contains("Baz"));
        Assert.assertTrue(keys.contains("BAZ"));
    }

    @Test
    public void testRetainKeysCopyToAnotherMap() {
        Map<String, Object> map = new CaseInsensitiveMap();
        map.put("Foo", "cheese");
        map.put("BAR", 123);
        map.put("baZ", "beer");
        Map<String, Object> other = new HashMap<>(map);
        // we should retain the cases of the original keys
        // when its copied to another map
        Assert.assertTrue(other.containsKey("Foo"));
        Assert.assertFalse(other.containsKey("foo"));
        Assert.assertFalse(other.containsKey("FOO"));
        Assert.assertTrue(other.containsKey("BAR"));
        Assert.assertFalse(other.containsKey("bar"));
        Assert.assertFalse(other.containsKey("Bar"));
        Assert.assertTrue(other.containsKey("baZ"));
        Assert.assertFalse(other.containsKey("baz"));
        Assert.assertFalse(other.containsKey("Baz"));
        Assert.assertFalse(other.containsKey("BAZ"));
    }

    @Test
    public void testValues() {
        Map<String, Object> map = new CaseInsensitiveMap();
        map.put("Foo", "cheese");
        map.put("BAR", "123");
        map.put("baZ", "Beer");
        Iterator<Object> it = map.values().iterator();
        // should be String values
        Assert.assertEquals("String", it.next().getClass().getSimpleName());
        Assert.assertEquals("String", it.next().getClass().getSimpleName());
        Assert.assertEquals("String", it.next().getClass().getSimpleName());
        Collection<Object> values = map.values();
        Assert.assertEquals(3, values.size());
        Assert.assertTrue(values.contains("cheese"));
        Assert.assertTrue(values.contains("123"));
        Assert.assertTrue(values.contains("Beer"));
    }

    @Test
    public void testRomeks() {
        Map<String, Object> map = new CaseInsensitiveMap();
        map.put("foo", "cheese");
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("cheese", map.get("fOo"));
        Assert.assertEquals(true, map.containsKey("foo"));
        Assert.assertEquals(true, map.containsKey("FOO"));
        Assert.assertEquals(true, map.keySet().contains("FOO"));
        Assert.assertEquals(true, map.keySet().contains("FoO"));
        Assert.assertEquals(true, map.keySet().contains("Foo"));
        Assert.assertEquals(true, map.keySet().contains("foo"));
        Assert.assertEquals(true, map.keySet().contains("fOO"));
        map.put("FOO", "cake");
        Assert.assertEquals(1, map.size());
        Assert.assertEquals(true, map.containsKey("foo"));
        Assert.assertEquals(true, map.containsKey("FOO"));
        Assert.assertEquals("cake", map.get("fOo"));
    }

    @Test
    public void testRomeksUsingRegularHashMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("foo", "cheese");
        Assert.assertEquals(1, map.size());
        Assert.assertEquals(null, map.get("fOo"));
        Assert.assertEquals(true, map.containsKey("foo"));
        Assert.assertEquals(false, map.containsKey("FOO"));
        Assert.assertEquals(false, map.keySet().contains("FOO"));
        map.put("FOO", "cake");
        Assert.assertEquals(2, map.size());
        Assert.assertEquals(true, map.containsKey("foo"));
        Assert.assertEquals(true, map.containsKey("FOO"));
        Assert.assertEquals(null, map.get("fOo"));
        Assert.assertEquals("cheese", map.get("foo"));
        Assert.assertEquals("cake", map.get("FOO"));
    }

    @Test
    public void testRomeksTransferredToHashMapAfterwards() {
        Map<String, Object> map = new CaseInsensitiveMap();
        map.put("Foo", "cheese");
        map.put("FOO", "cake");
        Assert.assertEquals(1, map.size());
        Assert.assertEquals(true, map.containsKey("foo"));
        Assert.assertEquals(true, map.containsKey("FOO"));
        Map<String, Object> other = new HashMap<>(map);
        Assert.assertEquals(false, other.containsKey("foo"));
        Assert.assertEquals(false, other.containsKey("FOO"));
        // CaseInsensitiveMap preserves the original keys, which would be the 1st key we put
        Assert.assertEquals(true, other.containsKey("Foo"));
        Assert.assertEquals(1, other.size());
    }

    @Test
    public void testSerialization() throws Exception {
        CaseInsensitiveMap testMap = new CaseInsensitiveMap();
        testMap.put("key", "value");
        // force entry set to be created which could cause the map to be non serializable
        testMap.entrySet();
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        ObjectOutputStream objStream = new ObjectOutputStream(bStream);
        objStream.writeObject(testMap);
        ObjectInputStream inStream = new ObjectInputStream(new ByteArrayInputStream(bStream.toByteArray()));
        CaseInsensitiveMap testMapCopy = ((CaseInsensitiveMap) (inStream.readObject()));
        Assert.assertTrue(testMapCopy.containsKey("key"));
    }

    @Test
    public void testCopyToAnotherMapPreserveKeyCaseEntrySet() {
        Map<String, Object> map = new CaseInsensitiveMap();
        map.put("Foo", "cheese");
        map.put("BAR", "cake");
        Assert.assertEquals(2, map.size());
        Assert.assertEquals(true, map.containsKey("foo"));
        Assert.assertEquals(true, map.containsKey("bar"));
        Map<String, Object> other = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            other.put(key, value);
        }
        Assert.assertEquals(false, other.containsKey("foo"));
        Assert.assertEquals(true, other.containsKey("Foo"));
        Assert.assertEquals(false, other.containsKey("bar"));
        Assert.assertEquals(true, other.containsKey("BAR"));
        Assert.assertEquals(2, other.size());
    }

    @Test
    public void testCopyToAnotherMapPreserveKeyCasePutAll() {
        Map<String, Object> map = new CaseInsensitiveMap();
        map.put("Foo", "cheese");
        map.put("BAR", "cake");
        Assert.assertEquals(2, map.size());
        Assert.assertEquals(true, map.containsKey("foo"));
        Assert.assertEquals(true, map.containsKey("bar"));
        Map<String, Object> other = new HashMap<>();
        other.putAll(map);
        Assert.assertEquals(false, other.containsKey("foo"));
        Assert.assertEquals(true, other.containsKey("Foo"));
        Assert.assertEquals(false, other.containsKey("bar"));
        Assert.assertEquals(true, other.containsKey("BAR"));
        Assert.assertEquals(2, other.size());
    }

    @Test
    public void testCopyToAnotherMapPreserveKeyCaseCtr() {
        Map<String, Object> map = new CaseInsensitiveMap();
        map.put("Foo", "cheese");
        map.put("BAR", "cake");
        Assert.assertEquals(2, map.size());
        Assert.assertEquals(true, map.containsKey("foo"));
        Assert.assertEquals(true, map.containsKey("bar"));
        Map<String, Object> other = new HashMap<>(map);
        Assert.assertEquals(false, other.containsKey("foo"));
        Assert.assertEquals(true, other.containsKey("Foo"));
        Assert.assertEquals(false, other.containsKey("bar"));
        Assert.assertEquals(true, other.containsKey("BAR"));
        Assert.assertEquals(2, other.size());
    }

    @Test
    public void testCopyToAnotherMapPreserveKeyKeySet() {
        Map<String, Object> map = new CaseInsensitiveMap();
        map.put("Foo", "cheese");
        map.put("BAR", "cake");
        Assert.assertEquals(2, map.size());
        Assert.assertEquals(true, map.containsKey("foo"));
        Assert.assertEquals(true, map.containsKey("bar"));
        Map<String, Object> other = new HashMap<>();
        for (String key : map.keySet()) {
            Object value = map.get(key);
            other.put(key, value);
        }
        // the original case of the keys should be preserved
        Assert.assertEquals(false, other.containsKey("foo"));
        Assert.assertEquals(true, other.containsKey("Foo"));
        Assert.assertEquals(false, other.containsKey("bar"));
        Assert.assertEquals(true, other.containsKey("BAR"));
        Assert.assertEquals(2, other.size());
    }

    @Test
    public void testConcurrent() throws Exception {
        ExecutorService service = Executors.newFixedThreadPool(5);
        final CountDownLatch latch = new CountDownLatch(1000);
        final Map<String, Object> map = new CaseInsensitiveMap();
        // do some stuff concurrently
        for (int i = 0; i < 1000; i++) {
            final int count = i;
            service.submit(new Runnable() {
                public void run() {
                    Map<String, Object> foo = new CaseInsensitiveMap();
                    foo.put(("counter" + count), count);
                    foo.put("foo", 123);
                    foo.put("bar", 456);
                    foo.put("cake", "cheese");
                    // copy foo to map as map is a shared resource
                    synchronized(map) {
                        map.putAll(foo);
                    }
                    latch.countDown();
                }
            });
        }
        Assert.assertTrue(latch.await(10, TimeUnit.SECONDS));
        Assert.assertEquals(1003, map.size());
        Assert.assertEquals(true, map.containsKey("counter0"));
        Assert.assertEquals(true, map.containsKey("counter500"));
        Assert.assertEquals(true, map.containsKey("counter999"));
        Assert.assertEquals(123, map.get("FOO"));
        Assert.assertEquals(456, map.get("Bar"));
        Assert.assertEquals("cheese", map.get("cAKe"));
        service.shutdownNow();
    }
}

