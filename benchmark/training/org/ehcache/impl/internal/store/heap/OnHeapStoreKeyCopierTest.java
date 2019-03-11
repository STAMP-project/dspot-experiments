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
package org.ehcache.impl.internal.store.heap;


import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import org.ehcache.Cache;
import org.ehcache.core.spi.store.Store;
import org.ehcache.spi.resilience.StoreAccessException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * OnHeapStoreKeyCopierTest
 */
@RunWith(Parameterized.class)
public class OnHeapStoreKeyCopierTest {
    private static final OnHeapStoreKeyCopierTest.Key KEY = new OnHeapStoreKeyCopierTest.Key("WHat?");

    public static final String VALUE = "TheAnswer";

    public static final Supplier<Boolean> NOT_REPLACE_EQUAL = () -> false;

    public static final Supplier<Boolean> REPLACE_EQUAL = () -> true;

    @Parameterized.Parameter(0)
    public boolean copyForRead;

    @Parameterized.Parameter(1)
    public boolean copyForWrite;

    private OnHeapStore<OnHeapStoreKeyCopierTest.Key, String> store;

    @Test
    public void testPutAndGet() throws StoreAccessException {
        OnHeapStoreKeyCopierTest.Key copyKey = new OnHeapStoreKeyCopierTest.Key(OnHeapStoreKeyCopierTest.KEY);
        store.put(copyKey, OnHeapStoreKeyCopierTest.VALUE);
        copyKey.state = "Different!";
        Store.ValueHolder<String> firstStoreValue = store.get(OnHeapStoreKeyCopierTest.KEY);
        Store.ValueHolder<String> secondStoreValue = store.get(copyKey);
        if (copyForWrite) {
            Assert.assertThat(firstStoreValue.get(), Matchers.is(OnHeapStoreKeyCopierTest.VALUE));
            Assert.assertThat(secondStoreValue, Matchers.nullValue());
        } else {
            Assert.assertThat(firstStoreValue, Matchers.nullValue());
            Assert.assertThat(secondStoreValue.get(), Matchers.is(OnHeapStoreKeyCopierTest.VALUE));
        }
    }

    @Test
    public void testCompute() throws StoreAccessException {
        final OnHeapStoreKeyCopierTest.Key copyKey = new OnHeapStoreKeyCopierTest.Key(OnHeapStoreKeyCopierTest.KEY);
        store.getAndCompute(copyKey, ( key, value) -> {
            assertThat(key, is(copyKey));
            return VALUE;
        });
        copyKey.state = "Different!";
        store.getAndCompute(copyKey, ( key, value) -> {
            if (copyForWrite) {
                assertThat(value, nullValue());
            } else {
                assertThat(value, is(VALUE));
                assertThat(key, is(copyKey));
                if (copyForRead) {
                    key.state = "Changed!";
                }
            }
            return value;
        });
        if (copyForRead) {
            Assert.assertThat(copyKey.state, Matchers.is("Different!"));
        }
    }

    @Test
    public void testComputeWithoutReplaceEqual() throws StoreAccessException {
        final OnHeapStoreKeyCopierTest.Key copyKey = new OnHeapStoreKeyCopierTest.Key(OnHeapStoreKeyCopierTest.KEY);
        store.computeAndGet(copyKey, ( key, value) -> {
            assertThat(key, is(copyKey));
            return VALUE;
        }, OnHeapStoreKeyCopierTest.NOT_REPLACE_EQUAL, () -> false);
        copyKey.state = "Different!";
        store.computeAndGet(copyKey, ( key, value) -> {
            if (copyForWrite) {
                assertThat(value, nullValue());
            } else {
                assertThat(value, is(VALUE));
                assertThat(key, is(copyKey));
                if (copyForRead) {
                    key.state = "Changed!";
                }
            }
            return value;
        }, OnHeapStoreKeyCopierTest.NOT_REPLACE_EQUAL, () -> false);
        if (copyForRead) {
            Assert.assertThat(copyKey.state, Matchers.is("Different!"));
        }
    }

    @Test
    public void testComputeWithReplaceEqual() throws StoreAccessException {
        final OnHeapStoreKeyCopierTest.Key copyKey = new OnHeapStoreKeyCopierTest.Key(OnHeapStoreKeyCopierTest.KEY);
        store.computeAndGet(copyKey, ( key, value) -> {
            assertThat(key, is(copyKey));
            return VALUE;
        }, OnHeapStoreKeyCopierTest.REPLACE_EQUAL, () -> false);
        copyKey.state = "Different!";
        store.computeAndGet(copyKey, ( key, value) -> {
            if (copyForWrite) {
                assertThat(value, nullValue());
            } else {
                assertThat(value, is(VALUE));
                assertThat(key, is(copyKey));
                if (copyForRead) {
                    key.state = "Changed!";
                }
            }
            return value;
        }, OnHeapStoreKeyCopierTest.REPLACE_EQUAL, () -> false);
        if (copyForRead) {
            Assert.assertThat(copyKey.state, Matchers.is("Different!"));
        }
    }

    @Test
    public void testIteration() throws StoreAccessException {
        store.put(OnHeapStoreKeyCopierTest.KEY, OnHeapStoreKeyCopierTest.VALUE);
        Store.Iterator<Cache.Entry<OnHeapStoreKeyCopierTest.Key, Store.ValueHolder<String>>> iterator = store.iterator();
        Assert.assertThat(iterator.hasNext(), Matchers.is(true));
        while (iterator.hasNext()) {
            Cache.Entry<OnHeapStoreKeyCopierTest.Key, Store.ValueHolder<String>> entry = iterator.next();
            if ((copyForRead) || (copyForWrite)) {
                Assert.assertThat(entry.getKey(), Matchers.not(Matchers.sameInstance(OnHeapStoreKeyCopierTest.KEY)));
            } else {
                Assert.assertThat(entry.getKey(), Matchers.sameInstance(OnHeapStoreKeyCopierTest.KEY));
            }
        } 
    }

    @Test
    public void testComputeIfAbsent() throws StoreAccessException {
        store.computeIfAbsent(OnHeapStoreKeyCopierTest.KEY, ( key) -> {
            if ((copyForRead) || (copyForWrite)) {
                Assert.assertThat(key, Matchers.not(Matchers.sameInstance(OnHeapStoreKeyCopierTest.KEY)));
            } else {
                Assert.assertThat(key, Matchers.sameInstance(OnHeapStoreKeyCopierTest.KEY));
            }
            return OnHeapStoreKeyCopierTest.VALUE;
        });
    }

    @Test
    public void testBulkCompute() throws StoreAccessException {
        final AtomicReference<OnHeapStoreKeyCopierTest.Key> keyRef = new AtomicReference<>();
        store.bulkCompute(Collections.singleton(OnHeapStoreKeyCopierTest.KEY), ( entries) -> {
            OnHeapStoreKeyCopierTest.Key key = entries.iterator().next().getKey();
            if ((copyForRead) || (copyForWrite)) {
                Assert.assertThat(key, Matchers.not(Matchers.sameInstance(OnHeapStoreKeyCopierTest.KEY)));
            } else {
                Assert.assertThat(key, Matchers.sameInstance(OnHeapStoreKeyCopierTest.KEY));
            }
            keyRef.set(key);
            return Collections.singletonMap(OnHeapStoreKeyCopierTest.KEY, OnHeapStoreKeyCopierTest.VALUE).entrySet();
        });
        store.bulkCompute(Collections.singleton(OnHeapStoreKeyCopierTest.KEY), ( entries) -> {
            if (copyForRead) {
                Assert.assertThat(entries.iterator().next().getKey(), Matchers.not(Matchers.sameInstance(keyRef.get())));
            }
            return Collections.singletonMap(OnHeapStoreKeyCopierTest.KEY, OnHeapStoreKeyCopierTest.VALUE).entrySet();
        });
    }

    @Test
    public void testBulkComputeWithoutReplaceEqual() throws StoreAccessException {
        store.bulkCompute(Collections.singleton(OnHeapStoreKeyCopierTest.KEY), ( entries) -> {
            if ((copyForRead) || (copyForWrite)) {
                Assert.assertThat(entries.iterator().next().getKey(), Matchers.not(Matchers.sameInstance(OnHeapStoreKeyCopierTest.KEY)));
            } else {
                Assert.assertThat(entries.iterator().next().getKey(), Matchers.sameInstance(OnHeapStoreKeyCopierTest.KEY));
            }
            return Collections.singletonMap(OnHeapStoreKeyCopierTest.KEY, OnHeapStoreKeyCopierTest.VALUE).entrySet();
        }, OnHeapStoreKeyCopierTest.NOT_REPLACE_EQUAL);
    }

    @Test
    public void testBulkComputeWithReplaceEqual() throws StoreAccessException {
        store.bulkCompute(Collections.singleton(OnHeapStoreKeyCopierTest.KEY), ( entries) -> {
            if ((copyForRead) || (copyForWrite)) {
                Assert.assertThat(entries.iterator().next().getKey(), Matchers.not(Matchers.sameInstance(OnHeapStoreKeyCopierTest.KEY)));
            } else {
                Assert.assertThat(entries.iterator().next().getKey(), Matchers.sameInstance(OnHeapStoreKeyCopierTest.KEY));
            }
            return Collections.singletonMap(OnHeapStoreKeyCopierTest.KEY, OnHeapStoreKeyCopierTest.VALUE).entrySet();
        }, OnHeapStoreKeyCopierTest.REPLACE_EQUAL);
    }

    @Test
    public void testBulkComputeIfAbsent() throws StoreAccessException {
        store.bulkComputeIfAbsent(Collections.singleton(OnHeapStoreKeyCopierTest.KEY), ( keys) -> {
            if ((copyForWrite) || (copyForRead)) {
                Assert.assertThat(keys.iterator().next(), Matchers.not(Matchers.sameInstance(OnHeapStoreKeyCopierTest.KEY)));
            } else {
                Assert.assertThat(keys.iterator().next(), Matchers.sameInstance(OnHeapStoreKeyCopierTest.KEY));
            }
            return Collections.singletonMap(OnHeapStoreKeyCopierTest.KEY, OnHeapStoreKeyCopierTest.VALUE).entrySet();
        });
    }

    public static final class Key {
        String state;

        final int hashCode;

        public Key(String state) {
            this.state = state;
            this.hashCode = state.hashCode();
        }

        public Key(OnHeapStoreKeyCopierTest.Key key) {
            this.state = key.state;
            this.hashCode = key.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            OnHeapStoreKeyCopierTest.Key value = ((OnHeapStoreKeyCopierTest.Key) (o));
            return state.equals(value.state);
        }

        @Override
        public int hashCode() {
            return hashCode;
        }
    }
}

