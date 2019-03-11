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
package org.ehcache.impl.internal.concurrent;


import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;
import org.ehcache.config.Eviction;
import org.ehcache.config.EvictionAdvisor;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Alex Snaps
 */
public class ConcurrentHashMapTest {
    @Test
    public void testRemoveAllWithHash() throws Exception {
        final int totalCount = 10037;
        ConcurrentHashMap<Comparable<?>, String> map = new ConcurrentHashMap<>();
        int lastHash = 0;
        for (int i = 0; i < totalCount; i++) {
            String o = Integer.toString(i);
            lastHash = o.hashCode();
            map.put(o, ("val#" + i));
        }
        Collection<Map.Entry<Comparable<?>, String>> removed = map.removeAllWithHash(lastHash);
        Assert.assertThat(removed.size(), greaterThan(0));
        Assert.assertThat(((map.size()) + (removed.size())), CoreMatchers.is(totalCount));
        assertRemovedEntriesAreRemoved(map, removed);
    }

    @Test
    public void testRemoveAllWithHashUsingBadHashes() throws Exception {
        final int totalCount = 10037;
        ConcurrentHashMap<Comparable<?>, String> map = new ConcurrentHashMap<>();
        for (int i = 0; i < totalCount; i++) {
            ConcurrentHashMapTest.BadHashKey o = new ConcurrentHashMapTest.BadHashKey(i);
            map.put(o, ("val#" + i));
        }
        Collection<Map.Entry<Comparable<?>, String>> removed = map.removeAllWithHash(ConcurrentHashMapTest.BadHashKey.HASH_CODE);
        Assert.assertThat(removed.size(), CoreMatchers.is(totalCount));
        Assert.assertThat(map.size(), CoreMatchers.is(0));
        assertRemovedEntriesAreRemoved(map, removed);
    }

    static class BadHashKey implements Comparable<ConcurrentHashMapTest.BadHashKey> {
        static final int HASH_CODE = 42;

        private final int value;

        public BadHashKey(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "BadHashKey#" + (value);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ConcurrentHashMapTest.BadHashKey) {
                return (((ConcurrentHashMapTest.BadHashKey) (obj)).value) == (value);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return ConcurrentHashMapTest.BadHashKey.HASH_CODE;
        }

        @Override
        public int compareTo(ConcurrentHashMapTest.BadHashKey o) {
            return (value) < (o.value) ? -1 : (value) > (o.value) ? 1 : 0;
        }
    }

    @Test
    public void testRandomSampleOnEmptyMap() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        Assert.assertThat(map.getEvictionCandidate(new Random(), 1, null, Eviction.noAdvice()), IsNull.nullValue());
    }

    @Test
    public void testEmptyRandomSample() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        map.put("foo", "bar");
        Assert.assertThat(map.getEvictionCandidate(new Random(), 0, null, Eviction.noAdvice()), IsNull.nullValue());
    }

    @Test
    public void testOversizedRandomSample() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        map.put("foo", "bar");
        Map.Entry<String, String> candidate = map.getEvictionCandidate(new Random(), 2, null, Eviction.noAdvice());
        Assert.assertThat(candidate.getKey(), CoreMatchers.is("foo"));
        Assert.assertThat(candidate.getValue(), CoreMatchers.is("bar"));
    }

    @Test
    public void testUndersizedRandomSample() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        for (int i = 0; i < 1000; i++) {
            map.put(Integer.toString(i), Integer.toString(i));
        }
        Map.Entry<String, String> candidate = map.getEvictionCandidate(new Random(), 2, ( t, t1) -> 0, Eviction.noAdvice());
        Assert.assertThat(candidate, IsNull.notNullValue());
    }

    @Test
    public void testFullyAdvisedAgainstEvictionRandomSample() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        for (int i = 0; i < 1000; i++) {
            map.put(Integer.toString(i), Integer.toString(i));
        }
        Map.Entry<String, String> candidate = map.getEvictionCandidate(new Random(), 2, null, ( key, value) -> true);
        Assert.assertThat(candidate, IsNull.nullValue());
    }

    @Test
    public void testSelectivelyAdvisedAgainstEvictionRandomSample() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        for (int i = 0; i < 1000; i++) {
            map.put(Integer.toString(i), Integer.toString(i));
        }
        Map.Entry<String, String> candidate = map.getEvictionCandidate(new Random(), 20, ( t, t1) -> 0, ( key, value) -> (key.length()) > 1);
        Assert.assertThat(candidate.getKey().length(), CoreMatchers.is(1));
    }

    @Test
    public void testReplaceWithWeirdBehavior() {
        ConcurrentHashMap<String, ConcurrentHashMapTest.Element> elementMap = new ConcurrentHashMap<>();
        final ConcurrentHashMapTest.Element initialElement = new ConcurrentHashMapTest.Element("key", "foo");
        elementMap.put("key", initialElement);
        Assert.assertThat(elementMap.replace("key", initialElement, new ConcurrentHashMapTest.Element("key", "foo")), CoreMatchers.is(true));
        Assert.assertThat(elementMap.replace("key", initialElement, new ConcurrentHashMapTest.Element("key", "foo")), CoreMatchers.is(false));
        ConcurrentHashMap<String, String> stringMap = new ConcurrentHashMap<>();
        final String initialString = "foo";
        stringMap.put("key", initialString);
        Assert.assertThat(stringMap.replace("key", initialString, new String(initialString)), CoreMatchers.is(true));
        Assert.assertThat(stringMap.replace("key", initialString, new String(initialString)), CoreMatchers.is(true));
    }

    @Test
    public void testUsesObjectIdentityForElementsOnly() {
        final String key = "ourKey";
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
        String value = new String("key");
        String valueAgain = new String("key");
        map.put(key, value);
        Assert.assertThat(map.replace(key, valueAgain, valueAgain), CoreMatchers.is(true));
        Assert.assertThat(map.replace(key, value, valueAgain), CoreMatchers.is(true));
        ConcurrentHashMapTest.Element elementValue = new ConcurrentHashMapTest.Element(key, value);
        ConcurrentHashMapTest.Element elementValueAgain = new ConcurrentHashMapTest.Element(key, value);
        map.put(key, elementValue);
        Assert.assertThat(map.replace(key, elementValueAgain, elementValue), CoreMatchers.is(false));
        Assert.assertThat(map.replace(key, elementValue, elementValueAgain), CoreMatchers.is(true));
        Assert.assertThat(map.replace(key, elementValue, elementValueAgain), CoreMatchers.is(false));
        Assert.assertThat(map.replace(key, elementValueAgain, elementValue), CoreMatchers.is(true));
    }

    static class Element {
        @SuppressWarnings("unused")
        private final Object key;

        @SuppressWarnings("unused")
        private final Object value;

        Element(final Object key, final Object value) {
            this.key = key;
            this.value = value;
        }
    }
}

