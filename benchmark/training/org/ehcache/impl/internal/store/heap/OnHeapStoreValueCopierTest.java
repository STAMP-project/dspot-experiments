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
 * OnHeapStoreValueCopierTest
 */
@RunWith(Parameterized.class)
public class OnHeapStoreValueCopierTest {
    private static final Long KEY = 42L;

    public static final OnHeapStoreValueCopierTest.Value VALUE = new OnHeapStoreValueCopierTest.Value("TheAnswer");

    public static final Supplier<Boolean> NOT_REPLACE_EQUAL = () -> false;

    public static final Supplier<Boolean> REPLACE_EQUAL = () -> true;

    @Parameterized.Parameter(0)
    public boolean copyForRead;

    @Parameterized.Parameter(1)
    public boolean copyForWrite;

    private OnHeapStore<Long, OnHeapStoreValueCopierTest.Value> store;

    @Test
    public void testPutAndGet() throws StoreAccessException {
        store.put(OnHeapStoreValueCopierTest.KEY, OnHeapStoreValueCopierTest.VALUE);
        Store.ValueHolder<OnHeapStoreValueCopierTest.Value> firstStoreValue = store.get(OnHeapStoreValueCopierTest.KEY);
        Store.ValueHolder<OnHeapStoreValueCopierTest.Value> secondStoreValue = store.get(OnHeapStoreValueCopierTest.KEY);
        compareValues(OnHeapStoreValueCopierTest.VALUE, firstStoreValue.get());
        compareValues(OnHeapStoreValueCopierTest.VALUE, secondStoreValue.get());
        compareReadValues(firstStoreValue.get(), secondStoreValue.get());
    }

    @Test
    public void testGetAndCompute() throws StoreAccessException {
        store.put(OnHeapStoreValueCopierTest.KEY, OnHeapStoreValueCopierTest.VALUE);
        Store.ValueHolder<OnHeapStoreValueCopierTest.Value> computedVal = store.getAndCompute(OnHeapStoreValueCopierTest.KEY, ( aLong, value) -> VALUE);
        Store.ValueHolder<OnHeapStoreValueCopierTest.Value> oldValue = store.get(OnHeapStoreValueCopierTest.KEY);
        store.getAndCompute(OnHeapStoreValueCopierTest.KEY, ( aLong, value) -> {
            compareReadValues(value, oldValue.get());
            return value;
        });
        compareValues(OnHeapStoreValueCopierTest.VALUE, computedVal.get());
    }

    @Test
    public void testComputeWithoutReplaceEqual() throws StoreAccessException {
        final Store.ValueHolder<OnHeapStoreValueCopierTest.Value> firstValue = store.computeAndGet(OnHeapStoreValueCopierTest.KEY, ( aLong, value) -> VALUE, OnHeapStoreValueCopierTest.NOT_REPLACE_EQUAL, () -> false);
        store.computeAndGet(OnHeapStoreValueCopierTest.KEY, ( aLong, value) -> {
            compareReadValues(value, firstValue.get());
            return value;
        }, OnHeapStoreValueCopierTest.NOT_REPLACE_EQUAL, () -> false);
        compareValues(OnHeapStoreValueCopierTest.VALUE, firstValue.get());
    }

    @Test
    public void testComputeWithReplaceEqual() throws StoreAccessException {
        final Store.ValueHolder<OnHeapStoreValueCopierTest.Value> firstValue = store.computeAndGet(OnHeapStoreValueCopierTest.KEY, ( aLong, value) -> VALUE, OnHeapStoreValueCopierTest.REPLACE_EQUAL, () -> false);
        store.computeAndGet(OnHeapStoreValueCopierTest.KEY, ( aLong, value) -> {
            compareReadValues(value, firstValue.get());
            return value;
        }, OnHeapStoreValueCopierTest.REPLACE_EQUAL, () -> false);
        compareValues(OnHeapStoreValueCopierTest.VALUE, firstValue.get());
    }

    @Test
    public void testComputeIfAbsent() throws StoreAccessException {
        Store.ValueHolder<OnHeapStoreValueCopierTest.Value> computedValue = store.computeIfAbsent(OnHeapStoreValueCopierTest.KEY, ( aLong) -> OnHeapStoreValueCopierTest.VALUE);
        Store.ValueHolder<OnHeapStoreValueCopierTest.Value> secondComputedValue = store.computeIfAbsent(OnHeapStoreValueCopierTest.KEY, ( aLong) -> {
            Assert.fail("There should have been a mapping");
            return null;
        });
        compareValues(OnHeapStoreValueCopierTest.VALUE, computedValue.get());
        compareReadValues(computedValue.get(), secondComputedValue.get());
    }

    @Test
    public void testBulkCompute() throws StoreAccessException {
        final Map<Long, Store.ValueHolder<OnHeapStoreValueCopierTest.Value>> results = store.bulkCompute(Collections.singleton(OnHeapStoreValueCopierTest.KEY), ( entries) -> Collections.singletonMap(OnHeapStoreValueCopierTest.KEY, OnHeapStoreValueCopierTest.VALUE).entrySet());
        store.bulkCompute(Collections.singleton(OnHeapStoreValueCopierTest.KEY), ( entries) -> {
            compareReadValues(results.get(OnHeapStoreValueCopierTest.KEY).get(), entries.iterator().next().getValue());
            return entries;
        });
        compareValues(OnHeapStoreValueCopierTest.VALUE, results.get(OnHeapStoreValueCopierTest.KEY).get());
    }

    @Test
    public void testBulkComputeWithoutReplaceEqual() throws StoreAccessException {
        final Map<Long, Store.ValueHolder<OnHeapStoreValueCopierTest.Value>> results = store.bulkCompute(Collections.singleton(OnHeapStoreValueCopierTest.KEY), ( entries) -> Collections.singletonMap(OnHeapStoreValueCopierTest.KEY, OnHeapStoreValueCopierTest.VALUE).entrySet(), OnHeapStoreValueCopierTest.NOT_REPLACE_EQUAL);
        store.bulkCompute(Collections.singleton(OnHeapStoreValueCopierTest.KEY), ( entries) -> {
            compareReadValues(results.get(OnHeapStoreValueCopierTest.KEY).get(), entries.iterator().next().getValue());
            return entries;
        }, OnHeapStoreValueCopierTest.NOT_REPLACE_EQUAL);
        compareValues(OnHeapStoreValueCopierTest.VALUE, results.get(OnHeapStoreValueCopierTest.KEY).get());
    }

    @Test
    public void testBulkComputeWithReplaceEqual() throws StoreAccessException {
        final Map<Long, Store.ValueHolder<OnHeapStoreValueCopierTest.Value>> results = store.bulkCompute(Collections.singleton(OnHeapStoreValueCopierTest.KEY), ( entries) -> Collections.singletonMap(OnHeapStoreValueCopierTest.KEY, OnHeapStoreValueCopierTest.VALUE).entrySet(), OnHeapStoreValueCopierTest.REPLACE_EQUAL);
        store.bulkCompute(Collections.singleton(OnHeapStoreValueCopierTest.KEY), ( entries) -> {
            compareReadValues(results.get(OnHeapStoreValueCopierTest.KEY).get(), entries.iterator().next().getValue());
            return entries;
        }, OnHeapStoreValueCopierTest.REPLACE_EQUAL);
        compareValues(OnHeapStoreValueCopierTest.VALUE, results.get(OnHeapStoreValueCopierTest.KEY).get());
    }

    @Test
    public void testBulkComputeIfAbsent() throws StoreAccessException {
        Map<Long, Store.ValueHolder<OnHeapStoreValueCopierTest.Value>> results = store.bulkComputeIfAbsent(Collections.singleton(OnHeapStoreValueCopierTest.KEY), ( longs) -> Collections.singletonMap(OnHeapStoreValueCopierTest.KEY, OnHeapStoreValueCopierTest.VALUE).entrySet());
        Map<Long, Store.ValueHolder<OnHeapStoreValueCopierTest.Value>> secondResults = store.bulkComputeIfAbsent(Collections.singleton(OnHeapStoreValueCopierTest.KEY), ( longs) -> {
            Assert.fail("There should have been a mapping!");
            return null;
        });
        compareValues(OnHeapStoreValueCopierTest.VALUE, results.get(OnHeapStoreValueCopierTest.KEY).get());
        compareReadValues(results.get(OnHeapStoreValueCopierTest.KEY).get(), secondResults.get(OnHeapStoreValueCopierTest.KEY).get());
    }

    @Test
    public void testIterator() throws StoreAccessException {
        store.put(OnHeapStoreValueCopierTest.KEY, OnHeapStoreValueCopierTest.VALUE);
        Store.Iterator<Cache.Entry<Long, Store.ValueHolder<OnHeapStoreValueCopierTest.Value>>> iterator = store.iterator();
        Assert.assertThat(iterator.hasNext(), Matchers.is(true));
        while (iterator.hasNext()) {
            Cache.Entry<Long, Store.ValueHolder<OnHeapStoreValueCopierTest.Value>> entry = iterator.next();
            compareValues(entry.getValue().get(), OnHeapStoreValueCopierTest.VALUE);
        } 
    }

    public static final class Value {
        String state;

        public Value(String state) {
            this.state = state;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            OnHeapStoreValueCopierTest.Value value = ((OnHeapStoreValueCopierTest.Value) (o));
            return state.equals(value.state);
        }

        @Override
        public int hashCode() {
            return state.hashCode();
        }
    }
}

