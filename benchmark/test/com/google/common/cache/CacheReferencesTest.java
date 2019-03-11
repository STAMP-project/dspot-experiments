/**
 * Copyright (C) 2011 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.common.cache;


import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.lang.ref.WeakReference;
import junit.framework.TestCase;


/**
 * Tests of basic {@link LoadingCache} operations with all possible combinations of key & value
 * strengths.
 *
 * @author mike nonemacher
 */
public class CacheReferencesTest extends TestCase {
    private static final CacheLoader<CacheReferencesTest.Key, String> KEY_TO_STRING_LOADER = new CacheLoader<CacheReferencesTest.Key, String>() {
        @Override
        public String load(CacheReferencesTest.Key key) {
            return key.toString();
        }
    };

    public void testContainsKeyAndValue() {
        for (LoadingCache<CacheReferencesTest.Key, String> cache : caches()) {
            // maintain strong refs so these won't be collected, regardless of cache's key/value strength
            CacheReferencesTest.Key key = new CacheReferencesTest.Key(1);
            String value = key.toString();
            TestCase.assertSame(value, cache.getUnchecked(key));
            TestCase.assertTrue(cache.asMap().containsKey(key));
            TestCase.assertTrue(cache.asMap().containsValue(value));
            TestCase.assertEquals(1, cache.size());
        }
    }

    public void testClear() {
        for (LoadingCache<CacheReferencesTest.Key, String> cache : caches()) {
            CacheReferencesTest.Key key = new CacheReferencesTest.Key(1);
            String value = key.toString();
            TestCase.assertSame(value, cache.getUnchecked(key));
            TestCase.assertFalse(cache.asMap().isEmpty());
            cache.invalidateAll();
            TestCase.assertEquals(0, cache.size());
            TestCase.assertTrue(cache.asMap().isEmpty());
            TestCase.assertFalse(cache.asMap().containsKey(key));
            TestCase.assertFalse(cache.asMap().containsValue(value));
        }
    }

    public void testKeySetEntrySetValues() {
        for (LoadingCache<CacheReferencesTest.Key, String> cache : caches()) {
            CacheReferencesTest.Key key1 = new CacheReferencesTest.Key(1);
            String value1 = key1.toString();
            CacheReferencesTest.Key key2 = new CacheReferencesTest.Key(2);
            String value2 = key2.toString();
            TestCase.assertSame(value1, cache.getUnchecked(key1));
            TestCase.assertSame(value2, cache.getUnchecked(key2));
            TestCase.assertEquals(ImmutableSet.of(key1, key2), cache.asMap().keySet());
            assertThat(cache.asMap().values()).containsExactly(value1, value2);
            TestCase.assertEquals(ImmutableSet.of(Maps.immutableEntry(key1, value1), Maps.immutableEntry(key2, value2)), cache.asMap().entrySet());
        }
    }

    public void testInvalidate() {
        for (LoadingCache<CacheReferencesTest.Key, String> cache : caches()) {
            CacheReferencesTest.Key key1 = new CacheReferencesTest.Key(1);
            String value1 = key1.toString();
            CacheReferencesTest.Key key2 = new CacheReferencesTest.Key(2);
            String value2 = key2.toString();
            TestCase.assertSame(value1, cache.getUnchecked(key1));
            TestCase.assertSame(value2, cache.getUnchecked(key2));
            cache.invalidate(key1);
            TestCase.assertFalse(cache.asMap().containsKey(key1));
            TestCase.assertTrue(cache.asMap().containsKey(key2));
            TestCase.assertEquals(1, cache.size());
            TestCase.assertEquals(ImmutableSet.of(key2), cache.asMap().keySet());
            assertThat(cache.asMap().values()).contains(value2);
            TestCase.assertEquals(ImmutableSet.of(Maps.immutableEntry(key2, value2)), cache.asMap().entrySet());
        }
    }

    // A simple type whose .toString() will return the same value each time, but without maintaining
    // a strong reference to that value.
    static class Key {
        private final int value;

        private WeakReference<String> toString;

        Key(int value) {
            this.value = value;
        }

        @Override
        public synchronized String toString() {
            String s;
            if ((toString) != null) {
                s = toString.get();
                if (s != null) {
                    return s;
                }
            }
            s = Integer.toString(value);
            toString = new WeakReference<String>(s);
            return s;
        }
    }
}

