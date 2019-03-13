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


import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.core.events.NullStoreEventDispatcher;
import org.ehcache.core.spi.store.Store;
import org.ehcache.core.spi.time.SystemTimeSource;
import org.ehcache.impl.copy.IdentityCopier;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;
import org.ehcache.impl.internal.sizeof.NoopSizeOfEngine;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Ludovic Orban
 */
public class OnHeapStoreBulkMethodsTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testBulkComputeFunctionGetsValuesOfEntries() throws Exception {
        @SuppressWarnings("rawtypes")
        Store.Configuration config = Mockito.mock(Store.Configuration.class);
        Mockito.when(config.getExpiry()).thenReturn(ExpiryPolicyBuilder.noExpiration());
        Mockito.when(config.getKeyType()).thenReturn(Number.class);
        Mockito.when(config.getValueType()).thenReturn(Number.class);
        Mockito.when(config.getResourcePools()).thenReturn(ResourcePoolsBuilder.newResourcePoolsBuilder().heap(Long.MAX_VALUE, EntryUnit.ENTRIES).build());
        OnHeapStore<Number, Number> store = new OnHeapStore(config, SystemTimeSource.INSTANCE, IdentityCopier.identityCopier(), IdentityCopier.identityCopier(), new NoopSizeOfEngine(), NullStoreEventDispatcher.nullStoreEventDispatcher());
        store.put(1, 2);
        store.put(2, 3);
        store.put(3, 4);
        Map<Number, Store.ValueHolder<Number>> result = store.bulkCompute(new HashSet<Number>(Arrays.asList(1, 2, 3, 4, 5, 6)), ( entries) -> {
            Map<Number, Number> newValues = new HashMap<>();
            for (Map.Entry<? extends Number, ? extends Number> entry : entries) {
                final Number currentValue = entry.getValue();
                if (currentValue == null) {
                    if (entry.getKey().equals(4)) {
                        newValues.put(entry.getKey(), null);
                    } else {
                        newValues.put(entry.getKey(), 0);
                    }
                } else {
                    newValues.put(entry.getKey(), ((currentValue.intValue()) * 2));
                }
            }
            return newValues.entrySet();
        });
        ConcurrentMap<Number, Number> check = new ConcurrentHashMap<>();
        check.put(1, 4);
        check.put(2, 6);
        check.put(3, 8);
        check.put(4, 0);
        check.put(5, 0);
        check.put(6, 0);
        Assert.assertThat(result.get(1).get(), Matchers.is(check.get(1)));
        Assert.assertThat(result.get(2).get(), Matchers.is(check.get(2)));
        Assert.assertThat(result.get(3).get(), Matchers.is(check.get(3)));
        Assert.assertThat(result.get(4), Matchers.nullValue());
        Assert.assertThat(result.get(5).get(), Matchers.is(check.get(5)));
        Assert.assertThat(result.get(6).get(), Matchers.is(check.get(6)));
        for (Number key : check.keySet()) {
            final Store.ValueHolder<Number> holder = store.get(key);
            if (holder != null) {
                check.remove(key, holder.get());
            }
        }
        Assert.assertThat(check.size(), Matchers.is(1));
        Assert.assertThat(check.containsKey(4), Matchers.is(true));
    }

    @Test
    public void testBulkComputeHappyPath() throws Exception {
        OnHeapStore<Number, CharSequence> store = newStore();
        store.put(1, "one");
        Map<Number, Store.ValueHolder<CharSequence>> result = store.bulkCompute(new HashSet<Number>(Arrays.asList(1, 2)), ( entries) -> {
            Map<Number, CharSequence> newValues = new HashMap<>();
            for (Map.Entry<? extends Number, ? extends CharSequence> entry : entries) {
                if ((entry.getKey().intValue()) == 1) {
                    newValues.put(entry.getKey(), "un");
                } else
                    if ((entry.getKey().intValue()) == 2) {
                        newValues.put(entry.getKey(), "deux");
                    }

            }
            return newValues.entrySet();
        });
        Assert.assertThat(result.size(), Matchers.is(2));
        Assert.assertThat(result.get(1).get(), Matchers.equalTo("un"));
        Assert.assertThat(result.get(2).get(), Matchers.equalTo("deux"));
        Assert.assertThat(store.get(1).get(), Matchers.equalTo("un"));
        Assert.assertThat(store.get(2).get(), Matchers.equalTo("deux"));
    }

    @Test
    public void testBulkComputeStoreRemovesValueWhenFunctionReturnsNullMappings() throws Exception {
        Store.Configuration<Number, CharSequence> configuration = mockStoreConfig();
        @SuppressWarnings("unchecked")
        OnHeapStore<Number, CharSequence> store = new OnHeapStore<>(configuration, SystemTimeSource.INSTANCE, IdentityCopier.identityCopier(), IdentityCopier.identityCopier(), new NoopSizeOfEngine(), NullStoreEventDispatcher.nullStoreEventDispatcher());
        store.put(1, "one");
        store.put(2, "two");
        store.put(3, "three");
        Map<Number, Store.ValueHolder<CharSequence>> result = store.bulkCompute(new HashSet<Number>(Arrays.asList(2, 1, 5)), ( entries) -> {
            Map<Number, CharSequence> newValues = new HashMap<>();
            for (Map.Entry<? extends Number, ? extends CharSequence> entry : entries) {
                newValues.put(entry.getKey(), null);
            }
            return newValues.entrySet();
        });
        Assert.assertThat(result.size(), Matchers.is(3));
        Assert.assertThat(store.get(1), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(store.get(2), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(store.get(3).get(), Matchers.equalTo("three"));
        Assert.assertThat(store.get(5), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testBulkComputeRemoveNullValueEntriesFromFunctionReturn() throws Exception {
        OnHeapStore<Number, CharSequence> store = newStore();
        store.put(1, "one");
        store.put(2, "two");
        store.put(3, "three");
        Map<Number, Store.ValueHolder<CharSequence>> result = store.bulkCompute(new HashSet<Number>(Arrays.asList(1, 2, 3)), ( entries) -> {
            Map<Number, CharSequence> result1 = new HashMap<>();
            for (Map.Entry<? extends Number, ? extends CharSequence> entry : entries) {
                if (entry.getKey().equals(1)) {
                    result1.put(entry.getKey(), null);
                } else
                    if (entry.getKey().equals(3)) {
                        result1.put(entry.getKey(), null);
                    } else {
                        result1.put(entry.getKey(), entry.getValue());
                    }

            }
            return result1.entrySet();
        });
        Assert.assertThat(result.size(), Matchers.is(3));
        Assert.assertThat(result.get(1), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(result.get(2).get(), Matchers.equalTo("two"));
        Assert.assertThat(result.get(3), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(store.get(1), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(store.get(2).get(), Matchers.equalTo("two"));
        Assert.assertThat(store.get(3), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testBulkComputeIfAbsentFunctionDoesNotGetPresentKeys() throws Exception {
        OnHeapStore<Number, CharSequence> store = newStore();
        store.put(1, "one");
        store.put(2, "two");
        store.put(3, "three");
        Map<Number, Store.ValueHolder<CharSequence>> result = store.bulkComputeIfAbsent(new HashSet<Number>(Arrays.asList(1, 2, 3, 4, 5, 6)), ( keys) -> {
            Map<Number, CharSequence> result1 = new HashMap<>();
            for (Number key : keys) {
                if (key.equals(1)) {
                    Assert.fail();
                } else
                    if (key.equals(2)) {
                        Assert.fail();
                    } else
                        if (key.equals(3)) {
                            Assert.fail();
                        } else {
                            result1.put(key, null);
                        }


            }
            return result1.entrySet();
        });
        Assert.assertThat(result.size(), Matchers.is(6));
        Assert.assertThat(result.get(1).get(), Matchers.equalTo("one"));
        Assert.assertThat(result.get(2).get(), Matchers.equalTo("two"));
        Assert.assertThat(result.get(3).get(), Matchers.equalTo("three"));
        Assert.assertThat(result.get(4), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(result.get(5), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(result.get(6), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(store.get(1).get(), Matchers.equalTo("one"));
        Assert.assertThat(store.get(2).get(), Matchers.equalTo("two"));
        Assert.assertThat(store.get(3).get(), Matchers.equalTo("three"));
        Assert.assertThat(store.get(4), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(store.get(5), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(store.get(6), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testBulkComputeIfAbsentDoesNotOverridePresentKeys() throws Exception {
        OnHeapStore<Number, CharSequence> store = newStore();
        store.put(1, "one");
        store.put(2, "two");
        store.put(3, "three");
        Map<Number, Store.ValueHolder<CharSequence>> result = store.bulkComputeIfAbsent(new HashSet<Number>(Arrays.asList(1, 2, 3, 4, 5, 6)), ( numbers) -> {
            Map<Number, CharSequence> result1 = new HashMap<>();
            for (Number key : numbers) {
                if (key.equals(4)) {
                    result1.put(key, "quatre");
                } else
                    if (key.equals(5)) {
                        result1.put(key, "cinq");
                    } else
                        if (key.equals(6)) {
                            result1.put(key, "six");
                        }


            }
            return result1.entrySet();
        });
        Assert.assertThat(result.size(), Matchers.is(6));
        Assert.assertThat(result.get(1).get(), Matchers.equalTo("one"));
        Assert.assertThat(result.get(2).get(), Matchers.equalTo("two"));
        Assert.assertThat(result.get(3).get(), Matchers.equalTo("three"));
        Assert.assertThat(result.get(4).get(), Matchers.equalTo("quatre"));
        Assert.assertThat(result.get(5).get(), Matchers.equalTo("cinq"));
        Assert.assertThat(result.get(6).get(), Matchers.equalTo("six"));
        Assert.assertThat(store.get(1).get(), Matchers.equalTo("one"));
        Assert.assertThat(store.get(2).get(), Matchers.equalTo("two"));
        Assert.assertThat(store.get(3).get(), Matchers.equalTo("three"));
        Assert.assertThat(store.get(4).get(), Matchers.equalTo("quatre"));
        Assert.assertThat(store.get(5).get(), Matchers.equalTo("cinq"));
        Assert.assertThat(store.get(6).get(), Matchers.equalTo("six"));
    }

    @Test
    public void testBulkComputeIfAbsentDoNothingOnNullValues() throws Exception {
        OnHeapStore<Number, CharSequence> store = newStore();
        store.put(1, "one");
        store.put(2, "two");
        store.put(3, "three");
        Map<Number, Store.ValueHolder<CharSequence>> result = store.bulkComputeIfAbsent(new HashSet<Number>(Arrays.asList(2, 1, 5)), ( numbers) -> {
            Map<Number, CharSequence> result1 = new HashMap<>();
            for (Number key : numbers) {
                // 5 is a missing key, so it's the only key that is going passed to the function
                if (key.equals(5)) {
                    result1.put(key, null);
                }
            }
            Set<Number> numbersSet = new HashSet<>();
            for (Number number : numbers) {
                numbersSet.add(number);
            }
            Assert.assertThat(numbersSet.size(), Matchers.is(1));
            Assert.assertThat(numbersSet.iterator().next(), Matchers.<Number>equalTo(5));
            return result1.entrySet();
        });
        Assert.assertThat(result.size(), Matchers.is(3));
        Assert.assertThat(result.get(2).get(), Matchers.<CharSequence>equalTo("two"));
        Assert.assertThat(result.get(1).get(), Matchers.<CharSequence>equalTo("one"));
        Assert.assertThat(result.get(5), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(store.get(1).get(), Matchers.<CharSequence>equalTo("one"));
        Assert.assertThat(store.get(2).get(), Matchers.<CharSequence>equalTo("two"));
        Assert.assertThat(store.get(3).get(), Matchers.<CharSequence>equalTo("three"));
        Assert.assertThat(store.get(5), Matchers.is(Matchers.nullValue()));
    }
}

