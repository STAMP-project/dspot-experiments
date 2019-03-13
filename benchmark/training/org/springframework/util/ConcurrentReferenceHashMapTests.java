/**
 * Copyright 2002-2018 the original author or authors.
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
 * limitations under the License.
 */
package org.springframework.util;


import Restructure.NEVER;
import Restructure.WHEN_NECESSARY;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap.Entry;
import org.springframework.util.ConcurrentReferenceHashMap.Reference;
import org.springframework.util.ConcurrentReferenceHashMap.Restructure;


/**
 * Tests for {@link ConcurrentReferenceHashMap}.
 *
 * @author Phillip Webb
 */
public class ConcurrentReferenceHashMapTests {
    private static final Comparator<? super String> NULL_SAFE_STRING_SORT = new org.springframework.util.comparator.NullSafeComparator<String>(new org.springframework.util.comparator.ComparableComparator<String>(), true);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ConcurrentReferenceHashMapTests.TestWeakConcurrentCache<Integer, String> map = new ConcurrentReferenceHashMapTests.TestWeakConcurrentCache<>();

    @Test
    public void shouldCreateWithDefaults() {
        ConcurrentReferenceHashMap<Integer, String> map = new ConcurrentReferenceHashMap();
        Assert.assertThat(map.getSegmentsSize(), is(16));
        Assert.assertThat(map.getSegment(0).getSize(), is(1));
        Assert.assertThat(map.getLoadFactor(), is(0.75F));
    }

    @Test
    public void shouldCreateWithInitialCapacity() {
        ConcurrentReferenceHashMap<Integer, String> map = new ConcurrentReferenceHashMap(32);
        Assert.assertThat(map.getSegmentsSize(), is(16));
        Assert.assertThat(map.getSegment(0).getSize(), is(2));
        Assert.assertThat(map.getLoadFactor(), is(0.75F));
    }

    @Test
    public void shouldCreateWithInitialCapacityAndLoadFactor() {
        ConcurrentReferenceHashMap<Integer, String> map = new ConcurrentReferenceHashMap(32, 0.5F);
        Assert.assertThat(map.getSegmentsSize(), is(16));
        Assert.assertThat(map.getSegment(0).getSize(), is(2));
        Assert.assertThat(map.getLoadFactor(), is(0.5F));
    }

    @Test
    public void shouldCreateWithInitialCapacityAndConcurrentLevel() {
        ConcurrentReferenceHashMap<Integer, String> map = new ConcurrentReferenceHashMap(16, 2);
        Assert.assertThat(map.getSegmentsSize(), is(2));
        Assert.assertThat(map.getSegment(0).getSize(), is(8));
        Assert.assertThat(map.getLoadFactor(), is(0.75F));
    }

    @Test
    public void shouldCreateFullyCustom() {
        ConcurrentReferenceHashMap<Integer, String> map = new ConcurrentReferenceHashMap(5, 0.5F, 3);
        // concurrencyLevel of 3 ends up as 4 (nearest power of 2)
        Assert.assertThat(map.getSegmentsSize(), is(4));
        // initialCapacity is 5/4 (rounded up, to nearest power of 2)
        Assert.assertThat(map.getSegment(0).getSize(), is(2));
        Assert.assertThat(map.getLoadFactor(), is(0.5F));
    }

    @Test
    public void shouldNeedNonNegativeInitialCapacity() {
        new ConcurrentReferenceHashMap<Integer, String>(0, 1);
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage("Initial capacity must not be negative");
        new ConcurrentReferenceHashMapTests.TestWeakConcurrentCache<Integer, String>((-1), 1);
    }

    @Test
    public void shouldNeedPositiveLoadFactor() {
        new ConcurrentReferenceHashMap<Integer, String>(0, 0.1F, 1);
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage("Load factor must be positive");
        new ConcurrentReferenceHashMapTests.TestWeakConcurrentCache<Integer, String>(0, 0.0F, 1);
    }

    @Test
    public void shouldNeedPositiveConcurrencyLevel() {
        new ConcurrentReferenceHashMap<Integer, String>(1, 1);
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage("Concurrency level must be positive");
        new ConcurrentReferenceHashMapTests.TestWeakConcurrentCache<Integer, String>(1, 0);
    }

    @Test
    public void shouldPutAndGet() {
        // NOTE we are using mock references so we don't need to worry about GC
        Assert.assertThat(this.map.size(), is(0));
        this.map.put(123, "123");
        Assert.assertThat(this.map.get(123), is("123"));
        Assert.assertThat(this.map.size(), is(1));
        this.map.put(123, "123b");
        Assert.assertThat(this.map.size(), is(1));
        this.map.put(123, null);
        Assert.assertThat(this.map.size(), is(1));
    }

    @Test
    public void shouldReplaceOnDoublePut() {
        this.map.put(123, "321");
        this.map.put(123, "123");
        Assert.assertThat(this.map.get(123), is("123"));
    }

    @Test
    public void shouldPutNullKey() {
        Assert.assertThat(this.map.get(null), is(nullValue()));
        Assert.assertThat(this.map.getOrDefault(null, "456"), is("456"));
        this.map.put(null, "123");
        Assert.assertThat(this.map.get(null), is("123"));
        Assert.assertThat(this.map.getOrDefault(null, "456"), is("123"));
    }

    @Test
    public void shouldPutNullValue() {
        Assert.assertThat(this.map.get(123), is(nullValue()));
        Assert.assertThat(this.map.getOrDefault(123, "456"), is("456"));
        this.map.put(123, "321");
        Assert.assertThat(this.map.get(123), is("321"));
        Assert.assertThat(this.map.getOrDefault(123, "456"), is("321"));
        this.map.put(123, null);
        Assert.assertThat(this.map.get(123), is(nullValue()));
        Assert.assertThat(this.map.getOrDefault(123, "456"), is(nullValue()));
    }

    @Test
    public void shouldGetWithNoItems() {
        Assert.assertThat(this.map.get(123), is(nullValue()));
    }

    @Test
    public void shouldApplySupplementalHash() {
        Integer key = 123;
        this.map.put(key, "123");
        Assert.assertThat(this.map.getSupplementalHash(), is(not(key.hashCode())));
        Assert.assertThat((((this.map.getSupplementalHash()) >> 30) & 255), is(not(0)));
    }

    @Test
    public void shouldGetFollowingNexts() {
        // Use loadFactor to disable resize
        this.map = new ConcurrentReferenceHashMapTests.TestWeakConcurrentCache<>(1, 10.0F, 1);
        this.map.put(1, "1");
        this.map.put(2, "2");
        this.map.put(3, "3");
        Assert.assertThat(this.map.getSegment(0).getSize(), is(1));
        Assert.assertThat(this.map.get(1), is("1"));
        Assert.assertThat(this.map.get(2), is("2"));
        Assert.assertThat(this.map.get(3), is("3"));
        Assert.assertThat(this.map.get(4), is(nullValue()));
    }

    @Test
    public void shouldResize() {
        this.map = new ConcurrentReferenceHashMapTests.TestWeakConcurrentCache<>(1, 0.75F, 1);
        this.map.put(1, "1");
        Assert.assertThat(this.map.getSegment(0).getSize(), is(1));
        Assert.assertThat(this.map.get(1), is("1"));
        this.map.put(2, "2");
        Assert.assertThat(this.map.getSegment(0).getSize(), is(2));
        Assert.assertThat(this.map.get(1), is("1"));
        Assert.assertThat(this.map.get(2), is("2"));
        this.map.put(3, "3");
        Assert.assertThat(this.map.getSegment(0).getSize(), is(4));
        Assert.assertThat(this.map.get(1), is("1"));
        Assert.assertThat(this.map.get(2), is("2"));
        Assert.assertThat(this.map.get(3), is("3"));
        this.map.put(4, "4");
        Assert.assertThat(this.map.getSegment(0).getSize(), is(8));
        Assert.assertThat(this.map.get(4), is("4"));
        // Putting again should not increase the count
        for (int i = 1; i <= 5; i++) {
            this.map.put(i, String.valueOf(i));
        }
        Assert.assertThat(this.map.getSegment(0).getSize(), is(8));
        Assert.assertThat(this.map.get(5), is("5"));
    }

    @Test
    public void shouldPurgeOnGet() {
        this.map = new ConcurrentReferenceHashMapTests.TestWeakConcurrentCache<>(1, 0.75F, 1);
        for (int i = 1; i <= 5; i++) {
            this.map.put(i, String.valueOf(i));
        }
        this.map.getMockReference(1, NEVER).queueForPurge();
        this.map.getMockReference(3, NEVER).queueForPurge();
        Assert.assertThat(this.map.getReference(1, WHEN_NECESSARY), is(nullValue()));
        Assert.assertThat(this.map.get(2), is("2"));
        Assert.assertThat(this.map.getReference(3, WHEN_NECESSARY), is(nullValue()));
        Assert.assertThat(this.map.get(4), is("4"));
        Assert.assertThat(this.map.get(5), is("5"));
    }

    @Test
    public void shouldPurgeOnPut() {
        this.map = new ConcurrentReferenceHashMapTests.TestWeakConcurrentCache<>(1, 0.75F, 1);
        for (int i = 1; i <= 5; i++) {
            this.map.put(i, String.valueOf(i));
        }
        this.map.getMockReference(1, NEVER).queueForPurge();
        this.map.getMockReference(3, NEVER).queueForPurge();
        this.map.put(1, "1");
        Assert.assertThat(this.map.get(1), is("1"));
        Assert.assertThat(this.map.get(2), is("2"));
        Assert.assertThat(this.map.getReference(3, WHEN_NECESSARY), is(nullValue()));
        Assert.assertThat(this.map.get(4), is("4"));
        Assert.assertThat(this.map.get(5), is("5"));
    }

    @Test
    public void shouldPutIfAbsent() {
        Assert.assertThat(this.map.putIfAbsent(123, "123"), is(nullValue()));
        Assert.assertThat(this.map.putIfAbsent(123, "123b"), is("123"));
        Assert.assertThat(this.map.get(123), is("123"));
    }

    @Test
    public void shouldPutIfAbsentWithNullValue() {
        Assert.assertThat(this.map.putIfAbsent(123, null), is(nullValue()));
        Assert.assertThat(this.map.putIfAbsent(123, "123"), is(nullValue()));
        Assert.assertThat(this.map.get(123), is(nullValue()));
    }

    @Test
    public void shouldPutIfAbsentWithNullKey() {
        Assert.assertThat(this.map.putIfAbsent(null, "123"), is(nullValue()));
        Assert.assertThat(this.map.putIfAbsent(null, "123b"), is("123"));
        Assert.assertThat(this.map.get(null), is("123"));
    }

    @Test
    public void shouldRemoveKeyAndValue() {
        this.map.put(123, "123");
        Assert.assertThat(this.map.remove(123, "456"), is(false));
        Assert.assertThat(this.map.get(123), is("123"));
        Assert.assertThat(this.map.remove(123, "123"), is(true));
        Assert.assertFalse(this.map.containsKey(123));
        Assert.assertThat(this.map.isEmpty(), is(true));
    }

    @Test
    public void shouldRemoveKeyAndValueWithExistingNull() {
        this.map.put(123, null);
        Assert.assertThat(this.map.remove(123, "456"), is(false));
        Assert.assertThat(this.map.get(123), is(nullValue()));
        Assert.assertThat(this.map.remove(123, null), is(true));
        Assert.assertFalse(this.map.containsKey(123));
        Assert.assertThat(this.map.isEmpty(), is(true));
    }

    @Test
    public void shouldReplaceOldValueWithNewValue() {
        this.map.put(123, "123");
        Assert.assertThat(this.map.replace(123, "456", "789"), is(false));
        Assert.assertThat(this.map.get(123), is("123"));
        Assert.assertThat(this.map.replace(123, "123", "789"), is(true));
        Assert.assertThat(this.map.get(123), is("789"));
    }

    @Test
    public void shouldReplaceOldNullValueWithNewValue() {
        this.map.put(123, null);
        Assert.assertThat(this.map.replace(123, "456", "789"), is(false));
        Assert.assertThat(this.map.get(123), is(nullValue()));
        Assert.assertThat(this.map.replace(123, null, "789"), is(true));
        Assert.assertThat(this.map.get(123), is("789"));
    }

    @Test
    public void shouldReplaceValue() {
        this.map.put(123, "123");
        Assert.assertThat(this.map.replace(123, "456"), is("123"));
        Assert.assertThat(this.map.get(123), is("456"));
    }

    @Test
    public void shouldReplaceNullValue() {
        this.map.put(123, null);
        Assert.assertThat(this.map.replace(123, "456"), is(nullValue()));
        Assert.assertThat(this.map.get(123), is("456"));
    }

    @Test
    public void shouldGetSize() {
        Assert.assertThat(this.map.size(), is(0));
        this.map.put(123, "123");
        this.map.put(123, null);
        this.map.put(456, "456");
        Assert.assertThat(this.map.size(), is(2));
    }

    @Test
    public void shouldSupportIsEmpty() {
        Assert.assertThat(this.map.isEmpty(), is(true));
        this.map.put(123, "123");
        this.map.put(123, null);
        this.map.put(456, "456");
        Assert.assertThat(this.map.isEmpty(), is(false));
    }

    @Test
    public void shouldContainKey() {
        Assert.assertThat(this.map.containsKey(123), is(false));
        Assert.assertThat(this.map.containsKey(456), is(false));
        this.map.put(123, "123");
        this.map.put(456, null);
        Assert.assertThat(this.map.containsKey(123), is(true));
        Assert.assertThat(this.map.containsKey(456), is(true));
    }

    @Test
    public void shouldContainValue() {
        Assert.assertThat(this.map.containsValue("123"), is(false));
        Assert.assertThat(this.map.containsValue(null), is(false));
        this.map.put(123, "123");
        this.map.put(456, null);
        Assert.assertThat(this.map.containsValue("123"), is(true));
        Assert.assertThat(this.map.containsValue(null), is(true));
    }

    @Test
    public void shouldRemoveWhenKeyIsInMap() {
        this.map.put(123, null);
        this.map.put(456, "456");
        this.map.put(null, "789");
        Assert.assertThat(this.map.remove(123), is(nullValue()));
        Assert.assertThat(this.map.remove(456), is("456"));
        Assert.assertThat(this.map.remove(null), is("789"));
        Assert.assertThat(this.map.isEmpty(), is(true));
    }

    @Test
    public void shouldRemoveWhenKeyIsNotInMap() {
        Assert.assertThat(this.map.remove(123), is(nullValue()));
        Assert.assertThat(this.map.remove(null), is(nullValue()));
        Assert.assertThat(this.map.isEmpty(), is(true));
    }

    @Test
    public void shouldPutAll() {
        Map<Integer, String> m = new HashMap<>();
        m.put(123, "123");
        m.put(456, null);
        m.put(null, "789");
        this.map.putAll(m);
        Assert.assertThat(this.map.size(), is(3));
        Assert.assertThat(this.map.get(123), is("123"));
        Assert.assertThat(this.map.get(456), is(nullValue()));
        Assert.assertThat(this.map.get(null), is("789"));
    }

    @Test
    public void shouldClear() {
        this.map.put(123, "123");
        this.map.put(456, null);
        this.map.put(null, "789");
        this.map.clear();
        Assert.assertThat(this.map.size(), is(0));
        Assert.assertThat(this.map.containsKey(123), is(false));
        Assert.assertThat(this.map.containsKey(456), is(false));
        Assert.assertThat(this.map.containsKey(null), is(false));
    }

    @Test
    public void shouldGetKeySet() {
        this.map.put(123, "123");
        this.map.put(456, null);
        this.map.put(null, "789");
        Set<Integer> expected = new HashSet<>();
        expected.add(123);
        expected.add(456);
        expected.add(null);
        Assert.assertThat(this.map.keySet(), is(expected));
    }

    @Test
    public void shouldGetValues() {
        this.map.put(123, "123");
        this.map.put(456, null);
        this.map.put(null, "789");
        List<String> actual = new ArrayList(this.map.values());
        List<String> expected = new ArrayList<>();
        expected.add("123");
        expected.add(null);
        expected.add("789");
        actual.sort(ConcurrentReferenceHashMapTests.NULL_SAFE_STRING_SORT);
        expected.sort(ConcurrentReferenceHashMapTests.NULL_SAFE_STRING_SORT);
        Assert.assertThat(actual, is(expected));
    }

    @Test
    public void shouldGetEntrySet() {
        this.map.put(123, "123");
        this.map.put(456, null);
        this.map.put(null, "789");
        HashMap<Integer, String> expected = new HashMap<>();
        expected.put(123, "123");
        expected.put(456, null);
        expected.put(null, "789");
        Assert.assertThat(this.map.entrySet(), is(expected.entrySet()));
    }

    @Test
    public void shouldGetEntrySetFollowingNext() {
        // Use loadFactor to disable resize
        this.map = new ConcurrentReferenceHashMapTests.TestWeakConcurrentCache<>(1, 10.0F, 1);
        this.map.put(1, "1");
        this.map.put(2, "2");
        this.map.put(3, "3");
        HashMap<Integer, String> expected = new HashMap<>();
        expected.put(1, "1");
        expected.put(2, "2");
        expected.put(3, "3");
        Assert.assertThat(this.map.entrySet(), is(expected.entrySet()));
    }

    @Test
    public void shouldRemoveViaEntrySet() {
        this.map.put(1, "1");
        this.map.put(2, "2");
        this.map.put(3, "3");
        Iterator<Map.Entry<Integer, String>> iterator = this.map.entrySet().iterator();
        iterator.next();
        iterator.next();
        iterator.remove();
        iterator.next();
        Assert.assertThat(iterator.hasNext(), is(false));
        Assert.assertThat(this.map.size(), is(2));
        Assert.assertThat(this.map.containsKey(2), is(false));
    }

    @Test
    public void shouldSetViaEntrySet() {
        this.map.put(1, "1");
        this.map.put(2, "2");
        this.map.put(3, "3");
        Iterator<Map.Entry<Integer, String>> iterator = this.map.entrySet().iterator();
        iterator.next();
        iterator.next().setValue("2b");
        iterator.next();
        Assert.assertThat(iterator.hasNext(), is(false));
        Assert.assertThat(this.map.size(), is(3));
        Assert.assertThat(this.map.get(2), is("2b"));
    }

    @Test
    public void shouldSupportNullReference() {
        // GC could happen during restructure so we must be able to create a reference for a null entry
        map.createReferenceManager().createReference(null, 1234, null);
    }

    private interface ValueFactory<V> {
        V newValue(int k);
    }

    private static class TestWeakConcurrentCache<K, V> extends ConcurrentReferenceHashMap<K, V> {
        private int supplementalHash;

        private final LinkedList<ConcurrentReferenceHashMapTests.MockReference<K, V>> queue = new LinkedList<>();

        private boolean disableTestHooks;

        public TestWeakConcurrentCache() {
            super();
        }

        public void setDisableTestHooks(boolean disableTestHooks) {
            this.disableTestHooks = disableTestHooks;
        }

        public TestWeakConcurrentCache(int initialCapacity, float loadFactor, int concurrencyLevel) {
            super(initialCapacity, loadFactor, concurrencyLevel);
        }

        public TestWeakConcurrentCache(int initialCapacity, int concurrencyLevel) {
            super(initialCapacity, concurrencyLevel);
        }

        @Override
        protected int getHash(@Nullable
        Object o) {
            if (this.disableTestHooks) {
                return super.getHash(o);
            }
            // For testing we want more control of the hash
            this.supplementalHash = super.getHash(o);
            return o == null ? 0 : o.hashCode();
        }

        public int getSupplementalHash() {
            return this.supplementalHash;
        }

        @Override
        protected ReferenceManager createReferenceManager() {
            return new ReferenceManager() {
                @Override
                public Reference<K, V> createReference(Entry<K, V> entry, int hash, @Nullable
                Reference<K, V> next) {
                    if (ConcurrentReferenceHashMapTests.TestWeakConcurrentCache.this.disableTestHooks) {
                        return super.createReference(entry, hash, next);
                    }
                    return new ConcurrentReferenceHashMapTests.MockReference(entry, hash, next, ConcurrentReferenceHashMapTests.TestWeakConcurrentCache.this.queue);
                }

                @Override
                public Reference<K, V> pollForPurge() {
                    if (ConcurrentReferenceHashMapTests.TestWeakConcurrentCache.this.disableTestHooks) {
                        return super.pollForPurge();
                    }
                    return ConcurrentReferenceHashMapTests.TestWeakConcurrentCache.this.queue.isEmpty() ? null : ConcurrentReferenceHashMapTests.TestWeakConcurrentCache.this.queue.removeFirst();
                }
            };
        }

        public ConcurrentReferenceHashMapTests.MockReference<K, V> getMockReference(K key, Restructure restructure) {
            return ((ConcurrentReferenceHashMapTests.MockReference<K, V>) (super.getReference(key, restructure)));
        }
    }

    private static class MockReference<K, V> implements Reference<K, V> {
        private final int hash;

        private Entry<K, V> entry;

        private final Reference<K, V> next;

        private final LinkedList<ConcurrentReferenceHashMapTests.MockReference<K, V>> queue;

        public MockReference(Entry<K, V> entry, int hash, Reference<K, V> next, LinkedList<ConcurrentReferenceHashMapTests.MockReference<K, V>> queue) {
            this.hash = hash;
            this.entry = entry;
            this.next = next;
            this.queue = queue;
        }

        @Override
        public Entry<K, V> get() {
            return this.entry;
        }

        @Override
        public int getHash() {
            return this.hash;
        }

        @Override
        public Reference<K, V> getNext() {
            return this.next;
        }

        @Override
        public void release() {
            this.queue.add(this);
            this.entry = null;
        }

        public void queueForPurge() {
            this.queue.add(this);
        }
    }
}

