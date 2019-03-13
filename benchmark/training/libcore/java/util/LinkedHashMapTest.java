/**
 * Copyright (C) 2016 The Android Open Source Project
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
 * limitations under the License
 */
package libcore.java.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import junit.framework.TestCase;


public class LinkedHashMapTest extends TestCase {
    public void test_getOrDefault() {
        /* acceptsNullKey */
        /* acceptsNullValue */
        /* getAcceptsAnyObject */
        MapDefaultMethodTester.test_getOrDefault(new LinkedHashMap<>(), true, true, true);
        // Test for access order
        Map<String, String> m = new LinkedHashMap<String, String>(8, 0.75F, true);
        m.put("key", "value");
        m.put("key1", "value1");
        m.put("key2", "value2");
        m.getOrDefault("key1", "value");
        Map.Entry<String, String> newest = null;
        for (Map.Entry<String, String> e : m.entrySet()) {
            newest = e;
        }
        TestCase.assertEquals("key1", newest.getKey());
        TestCase.assertEquals("value1", newest.getValue());
    }

    public void test_forEach() {
        MapDefaultMethodTester.test_forEach(new LinkedHashMap<>());
    }

    public void test_putIfAbsent() {
        /* acceptsNullKey */
        /* acceptsNullValue */
        MapDefaultMethodTester.test_putIfAbsent(new LinkedHashMap<>(), true, true);
        // Test for access order
        Map<String, String> m = new LinkedHashMap<String, String>(8, 0.75F, true);
        m.putIfAbsent("key", "value");
        m.putIfAbsent("key1", "value1");
        m.putIfAbsent("key2", "value2");
        Map.Entry<String, String> newest = null;
        for (Map.Entry<String, String> e : m.entrySet()) {
            newest = e;
        }
        TestCase.assertEquals("key2", newest.getKey());
        TestCase.assertEquals("value2", newest.getValue());
        // for existed key
        m.putIfAbsent("key1", "value1");
        for (Map.Entry<String, String> e : m.entrySet()) {
            newest = e;
        }
        TestCase.assertEquals("key1", newest.getKey());
        TestCase.assertEquals("value1", newest.getValue());
    }

    public void test_remove() {
        /* acceptsNullKey */
        /* acceptsNullValue */
        MapDefaultMethodTester.test_remove(new LinkedHashMap<>(), true, true);
    }

    public void test_replace$K$V$V() {
        /* acceptsNullKey */
        /* acceptsNullValue */
        MapDefaultMethodTester.test_replace$K$V$V(new LinkedHashMap<>(), true, true);
        // Test for access order
        Map<String, String> m = /* accessOrder */
        new LinkedHashMap<>(8, 0.75F, true);
        m.put("key", "value");
        m.put("key1", "value1");
        m.put("key2", "value2");
        m.replace("key1", "value1", "value2");
        Map.Entry<String, String> newest = null;
        for (Map.Entry<String, String> e : m.entrySet()) {
            newest = e;
        }
        TestCase.assertEquals("key1", newest.getKey());
        TestCase.assertEquals("value2", newest.getValue());
        // for wrong pair of key and value, last accessed node should
        // not change
        m.replace("key2", "value1", "value3");
        for (Map.Entry<String, String> e : m.entrySet()) {
            newest = e;
        }
        TestCase.assertEquals("key1", newest.getKey());
        TestCase.assertEquals("value2", newest.getValue());
    }

    public void test_replace$K$V() {
        /* acceptsNullKey */
        /* acceptsNullValue */
        MapDefaultMethodTester.test_replace$K$V(new LinkedHashMap<>(), true, true);
        // Test for access order
        Map<String, String> m = /* accessOrder */
        new LinkedHashMap<>(8, 0.75F, true);
        m.put("key", "value");
        m.put("key1", "value1");
        m.put("key2", "value2");
        m.replace("key1", "value2");
        Map.Entry<String, String> newest = null;
        for (Map.Entry<String, String> e : m.entrySet()) {
            newest = e;
        }
        TestCase.assertEquals("key1", newest.getKey());
        TestCase.assertEquals("value2", newest.getValue());
    }

    public void test_computeIfAbsent() {
        /* acceptsNullKey */
        /* acceptsNullValue */
        MapDefaultMethodTester.test_computeIfAbsent(new LinkedHashMap<>(), true, true);
        // Test for access order
        Map<String, String> m = /* accessOrder */
        new LinkedHashMap<>(8, 0.75F, true);
        m.put("key", "value");
        m.put("key1", "value1");
        m.put("key2", "value2");
        m.computeIfAbsent("key1", ( k) -> "value3");
        Map.Entry<String, String> newest = null;
        for (Map.Entry<String, String> e : m.entrySet()) {
            newest = e;
        }
        TestCase.assertEquals("key1", newest.getKey());
        TestCase.assertEquals("value1", newest.getValue());
        // When value is absent
        m.computeIfAbsent("key4", ( k) -> "value3");
        newest = null;
        for (Map.Entry<String, String> e : m.entrySet()) {
            newest = e;
        }
        TestCase.assertEquals("key4", newest.getKey());
        TestCase.assertEquals("value3", newest.getValue());
    }

    public void test_computeIfPresent() {
        /* acceptsNullKey */
        MapDefaultMethodTester.test_computeIfPresent(new LinkedHashMap<>(), true);
        // Test for access order
        Map<String, String> m = /* accessOrder */
        new LinkedHashMap<>(8, 0.75F, true);
        m.put("key", "value");
        m.put("key1", "value1");
        m.put("key2", "value2");
        m.computeIfPresent("key1", ( k, v) -> "value3");
        Map.Entry<String, String> newest = null;
        for (Map.Entry<String, String> e : m.entrySet()) {
            newest = e;
        }
        TestCase.assertEquals("key1", newest.getKey());
        TestCase.assertEquals("value3", newest.getValue());
    }

    public void test_compute() {
        /* acceptsNullKey */
        MapDefaultMethodTester.test_compute(new LinkedHashMap<>(), true);
        // Test for access order
        Map<String, String> m = /* accessOrder */
        new LinkedHashMap<>(8, 0.75F, true);
        m.put("key", "value");
        m.put("key1", "value1");
        m.put("key2", "value2");
        m.compute("key1", ( k, v) -> "value3");
        Map.Entry<String, String> newest = null;
        for (Map.Entry<String, String> e : m.entrySet()) {
            newest = e;
        }
        TestCase.assertEquals("key1", newest.getKey());
        TestCase.assertEquals("value3", newest.getValue());
        m.compute("key4", ( k, v) -> "value4");
        newest = null;
        for (Map.Entry<String, String> e : m.entrySet()) {
            newest = e;
        }
        TestCase.assertEquals("key4", newest.getKey());
        TestCase.assertEquals("value4", newest.getValue());
    }

    public void test_merge() {
        /* acceptsNullKey */
        MapDefaultMethodTester.test_merge(new LinkedHashMap<>(), true);
        // Test for access order
        Map<String, String> m = /* accessOrder */
        new LinkedHashMap<>(8, 0.75F, true);
        m.put("key", "value");
        m.put("key1", "value1");
        m.put("key2", "value2");
        m.merge("key1", "value3", ( k, v) -> "value3");
        Map.Entry<String, String> newest = null;
        for (Map.Entry<String, String> e : m.entrySet()) {
            newest = e;
        }
        TestCase.assertEquals("key1", newest.getKey());
        TestCase.assertEquals("value3", newest.getValue());
    }

    // This tests the behaviour is consistent with the RI.
    // This behaviour is NOT consistent with earlier Android releases up to
    // and including Android N, see http://b/27929722
    public void test_removeEldestEntry() {
        final AtomicBoolean removeEldestEntryReturnValue = new AtomicBoolean(false);
        final AtomicInteger removeEldestEntryCallCount = new AtomicInteger(0);
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>() {
            @Override
            protected boolean removeEldestEntry(Entry eldest) {
                int size = size();
                TestCase.assertEquals(size, LinkedHashMapTest.iterableSize(entrySet()));
                TestCase.assertEquals(size, LinkedHashMapTest.iterableSize(keySet()));
                TestCase.assertEquals(size, LinkedHashMapTest.iterableSize(values()));
                TestCase.assertEquals(size, ((removeEldestEntryCallCount.get()) + 1));
                removeEldestEntryCallCount.incrementAndGet();
                return removeEldestEntryReturnValue.get();
            }
        };
        TestCase.assertEquals(0, removeEldestEntryCallCount.get());
        m.put("foo", "bar");
        TestCase.assertEquals(1, removeEldestEntryCallCount.get());
        m.put("baz", "quux");
        TestCase.assertEquals(2, removeEldestEntryCallCount.get());
        removeEldestEntryReturnValue.set(true);
        m.put("foob", "faab");
        TestCase.assertEquals(3, removeEldestEntryCallCount.get());
        TestCase.assertEquals(2, m.size());
        TestCase.assertFalse(m.containsKey("foo"));
    }

    public void test_replaceAll() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("one", "1");
        map.put("two", "2");
        map.put("three", "3");
        map.replaceAll(( k, v) -> k + v);
        TestCase.assertEquals("one1", map.get("one"));
        TestCase.assertEquals("two2", map.get("two"));
        TestCase.assertEquals("three3", map.get("three"));
        TestCase.assertEquals(3, map.size());
        try {
            map.replaceAll(( k, v) -> {
                map.put("foo1", v);
                return v;
            });
            TestCase.fail();
        } catch (ConcurrentModificationException expected) {
        }
        try {
            map.replaceAll(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void test_eldest_empty() {
        LinkedHashMap<String, String> emptyMap = LinkedHashMapTest.createMap();
        TestCase.assertNull(LinkedHashMapTest.eldest(emptyMap));
    }

    public void test_eldest_nonempty() {
        LinkedHashMapTest.assertEntry("key", "value", LinkedHashMapTest.eldest(LinkedHashMapTest.createMap("key", "value")));
        LinkedHashMapTest.assertEntry("A", "1", LinkedHashMapTest.eldest(LinkedHashMapTest.createMap("A", "1", "B", "2", "C", "3")));
        LinkedHashMapTest.assertEntry("A", "4", LinkedHashMapTest.eldest(LinkedHashMapTest.createMap("A", "1", "B", "2", "C", "3", "A", "4")));
        LinkedHashMapTest.assertEntry("A", "4", LinkedHashMapTest.eldest(LinkedHashMapTest.createMap("A", "1", "B", "2", "C", "3", "A", "4", "D", "5")));
    }

    public void test_eldest_compatibleWithIterationOrder() {
        check_eldest_comparibleWithIterationOrder(LinkedHashMapTest.createMap());
        check_eldest_comparibleWithIterationOrder(LinkedHashMapTest.createMap("key", "value"));
        check_eldest_comparibleWithIterationOrder(LinkedHashMapTest.createMap("A", "1", "B", "2"));
        check_eldest_comparibleWithIterationOrder(LinkedHashMapTest.createMap("A", "1", "B", "2", "A", "3"));
        check_eldest_comparibleWithIterationOrder(LinkedHashMapTest.createMap("A", "1", "A", "2", "A", "3"));
        Random random = new Random(31337);// arbitrary

        LinkedHashMap<String, String> m = new LinkedHashMap<>();
        for (int i = 0; i < 8000; i++) {
            m.put(String.valueOf(random.nextInt(4000)), String.valueOf(random.nextDouble()));
        }
        check_eldest_comparibleWithIterationOrder(m);
    }

    /**
     * Check that {@code LinkedHashMap.Entry} compiles and refers to
     * {@link java.util.Map.Entry}, which is required for source
     * compatibility with earlier versions of Android.
     */
    public void test_entryCompatibility_compiletime() {
        TestCase.assertEquals(Map.Entry.class, LinkedHashMap.Entry.class);
    }

    /**
     * Checks that there is no nested class named 'Entry' in LinkedHashMap.
     * If {@link #test_entryCompatibility_compiletime()} passes but
     * this test fails, then the test was probably compiled against a
     * version of LinkedHashMap that does not have a nested Entry class,
     * but run against a version that does.
     */
    public void test_entryCompatibility_runtime() {
        String forbiddenClassName = "java.util.LinkedHashMap$Entry";
        try {
            Class.forName(forbiddenClassName);
            TestCase.fail((("Class " + forbiddenClassName) + " should not exist"));
        } catch (ClassNotFoundException expected) {
        }
    }

    public void test_spliterator_keySet() {
        Map<String, Integer> m = new LinkedHashMap<>();
        m.put("a", 1);
        m.put("b", 2);
        m.put("c", 3);
        m.put("d", 4);
        m.put("e", 5);
        m.put("f", 6);
        m.put("g", 7);
        m.put("h", 8);
        m.put("i", 9);
        m.put("j", 10);
        ArrayList<String> expectedKeys = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j"));
        Set<String> keys = m.keySet();
        SpliteratorTester.runBasicIterationTests(keys.spliterator(), expectedKeys);
        SpliteratorTester.runBasicSplitTests(keys, expectedKeys);
        SpliteratorTester.testSpliteratorNPE(keys.spliterator());
        SpliteratorTester.runOrderedTests(keys);
        SpliteratorTester.runSizedTests(keys.spliterator(), 10);
        SpliteratorTester.runSubSizedTests(keys.spliterator(), 10);
        TestCase.assertEquals(((((Spliterator.DISTINCT) | (Spliterator.ORDERED)) | (Spliterator.SIZED)) | (Spliterator.SUBSIZED)), keys.spliterator().characteristics());
        SpliteratorTester.assertSupportsTrySplit(keys);
    }

    public void test_spliterator_values() {
        Map<String, Integer> m = new LinkedHashMap<>();
        m.put("a", 1);
        m.put("b", 2);
        m.put("c", 3);
        m.put("d", 4);
        m.put("e", 5);
        m.put("f", 6);
        m.put("g", 7);
        m.put("h", 8);
        m.put("i", 9);
        m.put("j", 10);
        ArrayList<Integer> expectedValues = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        Collection<Integer> values = m.values();
        SpliteratorTester.runBasicIterationTests(values.spliterator(), expectedValues);
        SpliteratorTester.runBasicSplitTests(values, expectedValues);
        SpliteratorTester.testSpliteratorNPE(values.spliterator());
        SpliteratorTester.runOrderedTests(values);
        SpliteratorTester.runSizedTests(values, 10);
        SpliteratorTester.runSubSizedTests(values, 10);
        TestCase.assertEquals((((Spliterator.ORDERED) | (Spliterator.SIZED)) | (Spliterator.SUBSIZED)), values.spliterator().characteristics());
        SpliteratorTester.assertSupportsTrySplit(values);
    }

    public void test_spliterator_entrySet() {
        MapDefaultMethodTester.test_entrySet_spliterator_unordered(new LinkedHashMap<>());
        Map<String, Integer> m = new LinkedHashMap<>(Collections.singletonMap("key", 23));
        TestCase.assertEquals(((((Spliterator.DISTINCT) | (Spliterator.ORDERED)) | (Spliterator.SIZED)) | (Spliterator.SUBSIZED)), m.entrySet().spliterator().characteristics());
    }
}

