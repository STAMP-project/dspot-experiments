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
package org.ehcache.impl.internal.store.tiering;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.ehcache.config.ResourcePool;
import org.ehcache.config.ResourcePools;
import org.ehcache.config.ResourceType;
import org.ehcache.config.SizedResourcePool;
import org.ehcache.core.exceptions.StorePassThroughException;
import org.ehcache.core.spi.ServiceLocator;
import org.ehcache.core.spi.service.DiskResourceService;
import org.ehcache.core.spi.store.Store;
import org.ehcache.core.spi.store.tiering.AuthoritativeTier;
import org.ehcache.core.spi.store.tiering.CachingTier;
import org.ehcache.impl.internal.store.heap.OnHeapStore;
import org.ehcache.impl.internal.store.offheap.OffHeapStore;
import org.ehcache.spi.resilience.StoreAccessException;
import org.ehcache.spi.service.Service;
import org.ehcache.spi.service.ServiceProvider;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.ehcache.config.ResourceType.Core.DISK;
import static org.ehcache.config.ResourceType.Core.HEAP;
import static org.ehcache.config.ResourceType.Core.OFFHEAP;
import static org.ehcache.core.spi.store.Store.RemoveStatus.KEY_MISSING;
import static org.ehcache.core.spi.store.Store.RemoveStatus.REMOVED;
import static org.ehcache.core.spi.store.Store.ReplaceStatus.HIT;
import static org.ehcache.core.spi.store.Store.ReplaceStatus.MISS_NOT_PRESENT;


/**
 * Tests for {@link TieredStore}.
 */
public class TieredStoreTest {
    @Mock
    private CachingTier<Number, CharSequence> numberCachingTier;

    @Mock
    private AuthoritativeTier<Number, CharSequence> numberAuthoritativeTier;

    @Mock
    private CachingTier<String, String> stringCachingTier;

    @Mock
    private AuthoritativeTier<String, String> stringAuthoritativeTier;

    @Test
    @SuppressWarnings("unchecked")
    public void testGetHitsCachingTier() throws Exception {
        Mockito.when(numberCachingTier.getOrComputeIfAbsent(ArgumentMatchers.eq(1), ArgumentMatchers.any(Function.class))).thenReturn(newValueHolder("one"));
        TieredStore<Number, CharSequence> tieredStore = new TieredStore<>(numberCachingTier, numberAuthoritativeTier);
        MatcherAssert.assertThat(tieredStore.get(1).get(), Matchers.equalTo("one"));
        Mockito.verify(numberAuthoritativeTier, Mockito.times(0)).getAndFault(ArgumentMatchers.any(Number.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetHitsAuthoritativeTier() throws Exception {
        Store.ValueHolder<CharSequence> valueHolder = newValueHolder("one");
        Mockito.when(numberAuthoritativeTier.getAndFault(ArgumentMatchers.eq(1))).thenReturn(valueHolder);
        Mockito.when(numberCachingTier.getOrComputeIfAbsent(ArgumentMatchers.any(Number.class), ArgumentMatchers.any(Function.class))).then(((Answer<Store.ValueHolder<CharSequence>>) (( invocation) -> {
            Number key = ((Number) (invocation.getArguments()[0]));
            Function<Number, Store.ValueHolder<CharSequence>> function = ((Function<Number, Store.ValueHolder<CharSequence>>) (invocation.getArguments()[1]));
            return function.apply(key);
        })));
        TieredStore<Number, CharSequence> tieredStore = new TieredStore<>(numberCachingTier, numberAuthoritativeTier);
        MatcherAssert.assertThat(tieredStore.get(1).get(), Matchers.equalTo("one"));
        Mockito.verify(numberCachingTier, Mockito.times(1)).getOrComputeIfAbsent(ArgumentMatchers.eq(1), ArgumentMatchers.any(Function.class));
        Mockito.verify(numberAuthoritativeTier, Mockito.times(1)).getAndFault(ArgumentMatchers.any(Number.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetMisses() throws Exception {
        Mockito.when(numberAuthoritativeTier.getAndFault(ArgumentMatchers.eq(1))).thenReturn(null);
        Mockito.when(numberCachingTier.getOrComputeIfAbsent(ArgumentMatchers.any(Number.class), ArgumentMatchers.any(Function.class))).then(((Answer<Store.ValueHolder<CharSequence>>) (( invocation) -> {
            Number key = ((Number) (invocation.getArguments()[0]));
            Function<Number, Store.ValueHolder<CharSequence>> function = ((Function<Number, Store.ValueHolder<CharSequence>>) (invocation.getArguments()[1]));
            return function.apply(key);
        })));
        TieredStore<Number, CharSequence> tieredStore = new TieredStore<>(numberCachingTier, numberAuthoritativeTier);
        MatcherAssert.assertThat(tieredStore.get(1), Is.is(Matchers.nullValue()));
        Mockito.verify(numberCachingTier, Mockito.times(1)).getOrComputeIfAbsent(ArgumentMatchers.eq(1), ArgumentMatchers.any(Function.class));
        Mockito.verify(numberAuthoritativeTier, Mockito.times(1)).getAndFault(ArgumentMatchers.any(Number.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetThrowsRuntimeException() throws Exception {
        RuntimeException error = new RuntimeException();
        Mockito.when(numberCachingTier.getOrComputeIfAbsent(ArgumentMatchers.any(Number.class), ArgumentMatchers.any(Function.class))).thenThrow(new StoreAccessException(error));
        TieredStore<Number, CharSequence> tieredStore = new TieredStore<>(numberCachingTier, numberAuthoritativeTier);
        try {
            tieredStore.get(1);
            Assert.fail("We should get an Error");
        } catch (RuntimeException e) {
            Assert.assertSame(error, e);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetThrowsError() throws Exception {
        Error error = new Error();
        Mockito.when(numberCachingTier.getOrComputeIfAbsent(ArgumentMatchers.any(Number.class), ArgumentMatchers.any(Function.class))).thenThrow(new StoreAccessException(error));
        TieredStore<Number, CharSequence> tieredStore = new TieredStore<>(numberCachingTier, numberAuthoritativeTier);
        try {
            tieredStore.get(1);
            Assert.fail("We should get an Error");
        } catch (Error e) {
            Assert.assertSame(error, e);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetThrowsException() throws Exception {
        Exception error = new Exception();
        Mockito.when(numberCachingTier.getOrComputeIfAbsent(ArgumentMatchers.any(Number.class), ArgumentMatchers.any(Function.class))).thenThrow(new StoreAccessException(error));
        TieredStore<Number, CharSequence> tieredStore = new TieredStore<>(numberCachingTier, numberAuthoritativeTier);
        try {
            tieredStore.get(1);
            Assert.fail("We should get an Error");
        } catch (RuntimeException e) {
            Assert.assertSame(error, e.getCause());
            Assert.assertEquals("Unexpected checked exception wrapped in StoreAccessException", e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetThrowsPassthrough() throws Exception {
        StoreAccessException error = new StoreAccessException("inner");
        Mockito.when(numberCachingTier.getOrComputeIfAbsent(ArgumentMatchers.any(Number.class), ArgumentMatchers.any(Function.class))).thenThrow(new StoreAccessException(new StorePassThroughException(error)));
        TieredStore<Number, CharSequence> tieredStore = new TieredStore<>(numberCachingTier, numberAuthoritativeTier);
        try {
            tieredStore.get(1);
            Assert.fail("We should get an Error");
        } catch (StoreAccessException e) {
            Assert.assertSame(error, e);
        }
    }

    @Test
    public void testPut() throws Exception {
        TieredStore<Number, CharSequence> tieredStore = new TieredStore<>(numberCachingTier, numberAuthoritativeTier);
        tieredStore.put(1, "one");
        Mockito.verify(numberCachingTier, Mockito.times(1)).invalidate(ArgumentMatchers.eq(1));
        Mockito.verify(numberAuthoritativeTier, Mockito.times(1)).put(ArgumentMatchers.eq(1), ArgumentMatchers.eq("one"));
    }

    @Test
    public void testPutIfAbsent_whenAbsent() throws Exception {
        TieredStore<Number, CharSequence> tieredStore = new TieredStore<>(numberCachingTier, numberAuthoritativeTier);
        MatcherAssert.assertThat(tieredStore.putIfAbsent(1, "one", ( b) -> {
        }), Is.is(Matchers.nullValue()));
        Mockito.verify(numberCachingTier, Mockito.times(1)).invalidate(ArgumentMatchers.eq(1));
        Mockito.verify(numberAuthoritativeTier, Mockito.times(1)).putIfAbsent(ArgumentMatchers.eq(1), ArgumentMatchers.eq("one"), ArgumentMatchers.any());
    }

    @Test
    public void testPutIfAbsent_whenPresent() throws Exception {
        Consumer<Boolean> booleanConsumer = ( b) -> {
        };
        Mockito.when(numberAuthoritativeTier.putIfAbsent(1, "one", booleanConsumer)).thenReturn(newValueHolder("un"));
        TieredStore<Number, CharSequence> tieredStore = new TieredStore<>(numberCachingTier, numberAuthoritativeTier);
        MatcherAssert.assertThat(tieredStore.putIfAbsent(1, "one", booleanConsumer).get(), Matchers.<CharSequence>equalTo("un"));
        Mockito.verify(numberCachingTier, Mockito.times(1)).invalidate(1);
        Mockito.verify(numberAuthoritativeTier, Mockito.times(1)).putIfAbsent(1, "one", booleanConsumer);
    }

    @Test
    public void testRemove() throws Exception {
        TieredStore<Number, CharSequence> tieredStore = new TieredStore<>(numberCachingTier, numberAuthoritativeTier);
        tieredStore.remove(1);
        Mockito.verify(numberCachingTier, Mockito.times(1)).invalidate(ArgumentMatchers.eq(1));
        Mockito.verify(numberAuthoritativeTier, Mockito.times(1)).remove(ArgumentMatchers.eq(1));
    }

    @Test
    public void testRemove2Args_removes() throws Exception {
        Mockito.when(numberAuthoritativeTier.remove(ArgumentMatchers.eq(1), ArgumentMatchers.eq("one"))).thenReturn(REMOVED);
        TieredStore<Number, CharSequence> tieredStore = new TieredStore<>(numberCachingTier, numberAuthoritativeTier);
        MatcherAssert.assertThat(tieredStore.remove(1, "one"), Is.is(REMOVED));
        Mockito.verify(numberCachingTier, Mockito.times(1)).invalidate(ArgumentMatchers.eq(1));
        Mockito.verify(numberAuthoritativeTier, Mockito.times(1)).remove(ArgumentMatchers.eq(1), ArgumentMatchers.eq("one"));
    }

    @Test
    public void testRemove2Args_doesNotRemove() throws Exception {
        Mockito.when(numberAuthoritativeTier.remove(ArgumentMatchers.eq(1), ArgumentMatchers.eq("one"))).thenReturn(KEY_MISSING);
        TieredStore<Number, CharSequence> tieredStore = new TieredStore<>(numberCachingTier, numberAuthoritativeTier);
        MatcherAssert.assertThat(tieredStore.remove(1, "one"), Is.is(KEY_MISSING));
        Mockito.verify(numberCachingTier).invalidate(ArgumentMatchers.any(Number.class));
        Mockito.verify(numberAuthoritativeTier, Mockito.times(1)).remove(ArgumentMatchers.eq(1), ArgumentMatchers.eq("one"));
    }

    @Test
    public void testReplace2Args_replaces() throws Exception {
        Mockito.when(numberAuthoritativeTier.replace(ArgumentMatchers.eq(1), ArgumentMatchers.eq("one"))).thenReturn(newValueHolder("un"));
        TieredStore<Number, CharSequence> tieredStore = new TieredStore<>(numberCachingTier, numberAuthoritativeTier);
        MatcherAssert.assertThat(tieredStore.replace(1, "one").get(), Matchers.<CharSequence>equalTo("un"));
        Mockito.verify(numberCachingTier, Mockito.times(1)).invalidate(ArgumentMatchers.eq(1));
        Mockito.verify(numberAuthoritativeTier, Mockito.times(1)).replace(ArgumentMatchers.eq(1), ArgumentMatchers.eq("one"));
    }

    @Test
    public void testReplace2Args_doesNotReplace() throws Exception {
        Mockito.when(numberAuthoritativeTier.replace(ArgumentMatchers.eq(1), ArgumentMatchers.eq("one"))).thenReturn(null);
        TieredStore<Number, CharSequence> tieredStore = new TieredStore<>(numberCachingTier, numberAuthoritativeTier);
        MatcherAssert.assertThat(tieredStore.replace(1, "one"), Is.is(Matchers.nullValue()));
        Mockito.verify(numberCachingTier).invalidate(ArgumentMatchers.any(Number.class));
        Mockito.verify(numberAuthoritativeTier, Mockito.times(1)).replace(ArgumentMatchers.eq(1), ArgumentMatchers.eq("one"));
    }

    @Test
    public void testReplace3Args_replaces() throws Exception {
        Mockito.when(numberAuthoritativeTier.replace(ArgumentMatchers.eq(1), ArgumentMatchers.eq("un"), ArgumentMatchers.eq("one"))).thenReturn(HIT);
        TieredStore<Number, CharSequence> tieredStore = new TieredStore<>(numberCachingTier, numberAuthoritativeTier);
        MatcherAssert.assertThat(tieredStore.replace(1, "un", "one"), Is.is(HIT));
        Mockito.verify(numberCachingTier, Mockito.times(1)).invalidate(ArgumentMatchers.eq(1));
        Mockito.verify(numberAuthoritativeTier, Mockito.times(1)).replace(ArgumentMatchers.eq(1), ArgumentMatchers.eq("un"), ArgumentMatchers.eq("one"));
    }

    @Test
    public void testReplace3Args_doesNotReplace() throws Exception {
        Mockito.when(numberAuthoritativeTier.replace(ArgumentMatchers.eq(1), ArgumentMatchers.eq("un"), ArgumentMatchers.eq("one"))).thenReturn(MISS_NOT_PRESENT);
        TieredStore<Number, CharSequence> tieredStore = new TieredStore<>(numberCachingTier, numberAuthoritativeTier);
        MatcherAssert.assertThat(tieredStore.replace(1, "un", "one"), Is.is(MISS_NOT_PRESENT));
        Mockito.verify(numberCachingTier).invalidate(ArgumentMatchers.any(Number.class));
        Mockito.verify(numberAuthoritativeTier, Mockito.times(1)).replace(ArgumentMatchers.eq(1), ArgumentMatchers.eq("un"), ArgumentMatchers.eq("one"));
    }

    @Test
    public void testClear() throws Exception {
        TieredStore<Number, CharSequence> tieredStore = new TieredStore<>(numberCachingTier, numberAuthoritativeTier);
        tieredStore.clear();
        Mockito.verify(numberCachingTier, Mockito.times(1)).clear();
        Mockito.verify(numberAuthoritativeTier, Mockito.times(1)).clear();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCompute2Args() throws Exception {
        Mockito.when(numberAuthoritativeTier.getAndCompute(ArgumentMatchers.any(Number.class), ArgumentMatchers.any(BiFunction.class))).then(((Answer<Store.ValueHolder<CharSequence>>) (( invocation) -> {
            Number key = ((Number) (invocation.getArguments()[0]));
            BiFunction<Number, CharSequence, CharSequence> function = ((BiFunction<Number, CharSequence, CharSequence>) (invocation.getArguments()[1]));
            return newValueHolder(function.apply(key, null));
        })));
        TieredStore<Number, CharSequence> tieredStore = new TieredStore<>(numberCachingTier, numberAuthoritativeTier);
        MatcherAssert.assertThat(tieredStore.getAndCompute(1, ( number, charSequence) -> "one").get(), Matchers.<CharSequence>equalTo("one"));
        Mockito.verify(numberCachingTier, Mockito.times(1)).invalidate(ArgumentMatchers.any(Number.class));
        Mockito.verify(numberAuthoritativeTier, Mockito.times(1)).getAndCompute(ArgumentMatchers.eq(1), ArgumentMatchers.any(BiFunction.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCompute3Args() throws Exception {
        Mockito.when(numberAuthoritativeTier.computeAndGet(ArgumentMatchers.any(Number.class), ArgumentMatchers.any(BiFunction.class), ArgumentMatchers.any(Supplier.class), ArgumentMatchers.any(Supplier.class))).then(((Answer<Store.ValueHolder<CharSequence>>) (( invocation) -> {
            Number key = ((Number) (invocation.getArguments()[0]));
            BiFunction<Number, CharSequence, CharSequence> function = ((BiFunction<Number, CharSequence, CharSequence>) (invocation.getArguments()[1]));
            return newValueHolder(function.apply(key, null));
        })));
        TieredStore<Number, CharSequence> tieredStore = new TieredStore<>(numberCachingTier, numberAuthoritativeTier);
        MatcherAssert.assertThat(tieredStore.computeAndGet(1, ( number, charSequence) -> "one", () -> true, () -> false).get(), Matchers.<CharSequence>equalTo("one"));
        Mockito.verify(numberCachingTier, Mockito.times(1)).invalidate(ArgumentMatchers.any(Number.class));
        Mockito.verify(numberAuthoritativeTier, Mockito.times(1)).computeAndGet(ArgumentMatchers.eq(1), ArgumentMatchers.any(BiFunction.class), ArgumentMatchers.any(Supplier.class), ArgumentMatchers.any(Supplier.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testComputeIfAbsent_computes() throws Exception {
        Mockito.when(numberCachingTier.getOrComputeIfAbsent(ArgumentMatchers.any(Number.class), ArgumentMatchers.any(Function.class))).thenAnswer(((Answer<Store.ValueHolder<CharSequence>>) (( invocation) -> {
            Number key = ((Number) (invocation.getArguments()[0]));
            Function<Number, Store.ValueHolder<CharSequence>> function = ((Function<Number, Store.ValueHolder<CharSequence>>) (invocation.getArguments()[1]));
            return function.apply(key);
        })));
        Mockito.when(numberAuthoritativeTier.computeIfAbsentAndFault(ArgumentMatchers.any(Number.class), ArgumentMatchers.any(Function.class))).thenAnswer(((Answer<Store.ValueHolder<CharSequence>>) (( invocation) -> {
            Number key = ((Number) (invocation.getArguments()[0]));
            Function<Number, CharSequence> function = ((Function<Number, CharSequence>) (invocation.getArguments()[1]));
            return newValueHolder(function.apply(key));
        })));
        TieredStore<Number, CharSequence> tieredStore = new TieredStore<>(numberCachingTier, numberAuthoritativeTier);
        MatcherAssert.assertThat(tieredStore.computeIfAbsent(1, ( number) -> "one").get(), Matchers.<CharSequence>equalTo("one"));
        Mockito.verify(numberCachingTier, Mockito.times(1)).getOrComputeIfAbsent(ArgumentMatchers.eq(1), ArgumentMatchers.any(Function.class));
        Mockito.verify(numberAuthoritativeTier, Mockito.times(1)).computeIfAbsentAndFault(ArgumentMatchers.eq(1), ArgumentMatchers.any(Function.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testComputeIfAbsent_doesNotCompute() throws Exception {
        final Store.ValueHolder<CharSequence> valueHolder = newValueHolder("one");
        Mockito.when(numberCachingTier.getOrComputeIfAbsent(ArgumentMatchers.any(Number.class), ArgumentMatchers.any(Function.class))).thenAnswer(((Answer<Store.ValueHolder<CharSequence>>) (( invocation) -> valueHolder)));
        TieredStore<Number, CharSequence> tieredStore = new TieredStore<>(numberCachingTier, numberAuthoritativeTier);
        MatcherAssert.assertThat(tieredStore.computeIfAbsent(1, ( number) -> "one").get(), Matchers.<CharSequence>equalTo("one"));
        Mockito.verify(numberCachingTier, Mockito.times(1)).getOrComputeIfAbsent(ArgumentMatchers.eq(1), ArgumentMatchers.any(Function.class));
        Mockito.verify(numberAuthoritativeTier, Mockito.times(0)).computeIfAbsentAndFault(ArgumentMatchers.eq(1), ArgumentMatchers.any(Function.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testComputeIfAbsentThrowsError() throws Exception {
        Error error = new Error();
        Mockito.when(numberCachingTier.getOrComputeIfAbsent(ArgumentMatchers.any(Number.class), ArgumentMatchers.any(Function.class))).thenThrow(new StoreAccessException(error));
        TieredStore<Number, CharSequence> tieredStore = new TieredStore<>(numberCachingTier, numberAuthoritativeTier);
        try {
            tieredStore.computeIfAbsent(1, ( n) -> null);
            Assert.fail("We should get an Error");
        } catch (Error e) {
            Assert.assertSame(error, e);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBulkCompute2Args() throws Exception {
        Mockito.when(numberAuthoritativeTier.bulkCompute(ArgumentMatchers.any(Set.class), ArgumentMatchers.any(Function.class))).thenAnswer(((Answer<Map<Number, Store.ValueHolder<CharSequence>>>) (( invocation) -> {
            Set<Number> keys = ((Set) (invocation.getArguments()[0]));
            Function<Iterable<? extends Map.Entry<? extends Number, ? extends CharSequence>>, Iterable<? extends Map.Entry<? extends Number, ? extends CharSequence>>> function = ((Function<Iterable<? extends Map.Entry<? extends Number, ? extends CharSequence>>, Iterable<? extends Map.Entry<? extends Number, ? extends CharSequence>>>) (invocation.getArguments()[1]));
            List<Map.Entry<? extends Number, ? extends CharSequence>> functionArg = new ArrayList<>();
            for (Number key : keys) {
                functionArg.add(newMapEntry(key, null));
            }
            Iterable<? extends Map.Entry<? extends Number, ? extends CharSequence>> functionResult = function.apply(functionArg);
            Map<Number, Store.ValueHolder<CharSequence>> result = new HashMap<>();
            for (Map.Entry<? extends Number, ? extends CharSequence> entry : functionResult) {
                result.put(entry.getKey(), newValueHolder(entry.getValue()));
            }
            return result;
        })));
        TieredStore<Number, CharSequence> tieredStore = new TieredStore<>(numberCachingTier, numberAuthoritativeTier);
        Map<Number, Store.ValueHolder<CharSequence>> result = tieredStore.bulkCompute(new HashSet<Number>(Arrays.asList(1, 2, 3)), ( entries) -> new ArrayList<>(Arrays.asList(newMapEntry(1, "one"), newMapEntry(2, "two"), newMapEntry(3, "three"))));
        MatcherAssert.assertThat(result.size(), Is.is(3));
        MatcherAssert.assertThat(result.get(1).get(), Matchers.<CharSequence>equalTo("one"));
        MatcherAssert.assertThat(result.get(2).get(), Matchers.<CharSequence>equalTo("two"));
        MatcherAssert.assertThat(result.get(3).get(), Matchers.<CharSequence>equalTo("three"));
        Mockito.verify(numberCachingTier, Mockito.times(1)).invalidate(1);
        Mockito.verify(numberCachingTier, Mockito.times(1)).invalidate(2);
        Mockito.verify(numberCachingTier, Mockito.times(1)).invalidate(3);
        Mockito.verify(numberAuthoritativeTier, Mockito.times(1)).bulkCompute(ArgumentMatchers.any(Set.class), ArgumentMatchers.any(Function.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBulkCompute3Args() throws Exception {
        Mockito.when(numberAuthoritativeTier.bulkCompute(ArgumentMatchers.any(Set.class), ArgumentMatchers.any(Function.class), ArgumentMatchers.any(Supplier.class))).thenAnswer(((Answer<Map<Number, Store.ValueHolder<CharSequence>>>) (( invocation) -> {
            Set<Number> keys = ((Set) (invocation.getArguments()[0]));
            Function<Iterable<? extends Map.Entry<? extends Number, ? extends CharSequence>>, Iterable<? extends Map.Entry<? extends Number, ? extends CharSequence>>> function = ((Function<Iterable<? extends Map.Entry<? extends Number, ? extends CharSequence>>, Iterable<? extends Map.Entry<? extends Number, ? extends CharSequence>>>) (invocation.getArguments()[1]));
            List<Map.Entry<? extends Number, ? extends CharSequence>> functionArg = new ArrayList<>();
            for (Number key : keys) {
                functionArg.add(newMapEntry(key, null));
            }
            Iterable<? extends Map.Entry<? extends Number, ? extends CharSequence>> functionResult = function.apply(functionArg);
            Map<Number, Store.ValueHolder<CharSequence>> result = new HashMap<>();
            for (Map.Entry<? extends Number, ? extends CharSequence> entry : functionResult) {
                result.put(entry.getKey(), newValueHolder(entry.getValue()));
            }
            return result;
        })));
        TieredStore<Number, CharSequence> tieredStore = new TieredStore<>(numberCachingTier, numberAuthoritativeTier);
        Map<Number, Store.ValueHolder<CharSequence>> result = tieredStore.bulkCompute(new HashSet<Number>(Arrays.asList(1, 2, 3)), ( entries) -> new ArrayList<>(Arrays.asList(newMapEntry(1, "one"), newMapEntry(2, "two"), newMapEntry(3, "three"))), () -> true);
        MatcherAssert.assertThat(result.size(), Is.is(3));
        MatcherAssert.assertThat(result.get(1).get(), Matchers.<CharSequence>equalTo("one"));
        MatcherAssert.assertThat(result.get(2).get(), Matchers.<CharSequence>equalTo("two"));
        MatcherAssert.assertThat(result.get(3).get(), Matchers.<CharSequence>equalTo("three"));
        Mockito.verify(numberCachingTier, Mockito.times(1)).invalidate(1);
        Mockito.verify(numberCachingTier, Mockito.times(1)).invalidate(2);
        Mockito.verify(numberCachingTier, Mockito.times(1)).invalidate(3);
        Mockito.verify(numberAuthoritativeTier, Mockito.times(1)).bulkCompute(ArgumentMatchers.any(Set.class), ArgumentMatchers.any(Function.class), ArgumentMatchers.any(Supplier.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBulkComputeIfAbsent() throws Exception {
        Mockito.when(numberAuthoritativeTier.bulkComputeIfAbsent(ArgumentMatchers.any(Set.class), ArgumentMatchers.any(Function.class))).thenAnswer(((Answer<Map<Number, Store.ValueHolder<CharSequence>>>) (( invocation) -> {
            Set<Number> keys = ((Set) (invocation.getArguments()[0]));
            Function<Iterable<? extends Map.Entry<? extends Number, ? extends CharSequence>>, Iterable<? extends Map.Entry<? extends Number, ? extends CharSequence>>> function = ((Function<Iterable<? extends Map.Entry<? extends Number, ? extends CharSequence>>, Iterable<? extends Map.Entry<? extends Number, ? extends CharSequence>>>) (invocation.getArguments()[1]));
            List<Map.Entry<? extends Number, ? extends CharSequence>> functionArg = new ArrayList<>();
            for (Number key : keys) {
                functionArg.add(newMapEntry(key, null));
            }
            Iterable<? extends Map.Entry<? extends Number, ? extends CharSequence>> functionResult = function.apply(functionArg);
            Map<Number, Store.ValueHolder<CharSequence>> result = new HashMap<>();
            for (Map.Entry<? extends Number, ? extends CharSequence> entry : functionResult) {
                result.put(entry.getKey(), newValueHolder(entry.getValue()));
            }
            return result;
        })));
        TieredStore<Number, CharSequence> tieredStore = new TieredStore<>(numberCachingTier, numberAuthoritativeTier);
        Map<Number, Store.ValueHolder<CharSequence>> result = tieredStore.bulkComputeIfAbsent(new HashSet<Number>(Arrays.asList(1, 2, 3)), ( numbers) -> Arrays.asList(newMapEntry(1, "one"), newMapEntry(2, "two"), newMapEntry(3, "three")));
        MatcherAssert.assertThat(result.size(), Is.is(3));
        MatcherAssert.assertThat(result.get(1).get(), Matchers.<CharSequence>equalTo("one"));
        MatcherAssert.assertThat(result.get(2).get(), Matchers.<CharSequence>equalTo("two"));
        MatcherAssert.assertThat(result.get(3).get(), Matchers.<CharSequence>equalTo("three"));
        Mockito.verify(numberCachingTier, Mockito.times(1)).invalidate(1);
        Mockito.verify(numberCachingTier, Mockito.times(1)).invalidate(2);
        Mockito.verify(numberCachingTier, Mockito.times(1)).invalidate(3);
        Mockito.verify(numberAuthoritativeTier, Mockito.times(1)).bulkComputeIfAbsent(ArgumentMatchers.any(Set.class), ArgumentMatchers.any(Function.class));
    }

    @Test
    public void CachingTierDoesNotSeeAnyOperationDuringClear() throws InterruptedException, BrokenBarrierException, StoreAccessException {
        final TieredStore<String, String> tieredStore = new TieredStore<>(stringCachingTier, stringAuthoritativeTier);
        final CyclicBarrier barrier = new CyclicBarrier(2);
        Mockito.doAnswer(((Answer<Void>) (( invocation) -> {
            barrier.await();
            barrier.await();
            return null;
        }))).when(stringAuthoritativeTier).clear();
        Thread t = new Thread(() -> {
            try {
                tieredStore.clear();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        t.start();
        barrier.await();
        tieredStore.get("foo");
        barrier.await();
        t.join();
        Mockito.verify(stringCachingTier, Mockito.never()).getOrComputeIfAbsent(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testReleaseStoreFlushes() throws Exception {
        TieredStore.Provider tieredStoreProvider = new TieredStore.Provider();
        ResourcePools resourcePools = Mockito.mock(ResourcePools.class);
        Mockito.when(resourcePools.getResourceTypeSet()).thenReturn(new HashSet<>(Arrays.asList(HEAP, OFFHEAP)));
        SizedResourcePool heapPool = Mockito.mock(SizedResourcePool.class);
        Mockito.when(heapPool.getType()).thenReturn(((ResourceType) (HEAP)));
        Mockito.when(resourcePools.getPoolForResource(HEAP)).thenReturn(heapPool);
        OnHeapStore.Provider onHeapStoreProvider = Mockito.mock(OnHeapStore.Provider.class);
        Set<ResourceType<?>> singleton = Collections.<ResourceType<?>>singleton(HEAP);
        Mockito.when(onHeapStoreProvider.rankCachingTier(ArgumentMatchers.eq(singleton), ArgumentMatchers.any(Collection.class))).thenReturn(1);
        Mockito.when(onHeapStoreProvider.createCachingTier(ArgumentMatchers.any(Store.Configuration.class), ArgumentMatchers.any())).thenReturn(stringCachingTier);
        SizedResourcePool offHeapPool = Mockito.mock(SizedResourcePool.class);
        Mockito.when(heapPool.getType()).thenReturn(((ResourceType) (OFFHEAP)));
        Mockito.when(resourcePools.getPoolForResource(OFFHEAP)).thenReturn(offHeapPool);
        OffHeapStore.Provider offHeapStoreProvider = Mockito.mock(OffHeapStore.Provider.class);
        Mockito.when(offHeapStoreProvider.rankAuthority(ArgumentMatchers.eq(OFFHEAP), ArgumentMatchers.any(Collection.class))).thenReturn(1);
        Mockito.when(offHeapStoreProvider.createAuthoritativeTier(ArgumentMatchers.any(Store.Configuration.class), ArgumentMatchers.any())).thenReturn(stringAuthoritativeTier);
        Store.Configuration<String, String> configuration = Mockito.mock(Store.Configuration.class);
        Mockito.when(configuration.getResourcePools()).thenReturn(resourcePools);
        Set<AuthoritativeTier.Provider> authorities = new HashSet<>();
        authorities.add(offHeapStoreProvider);
        Set<CachingTier.Provider> cachingTiers = new HashSet<>();
        cachingTiers.add(onHeapStoreProvider);
        ServiceProvider<Service> serviceProvider = Mockito.mock(ServiceProvider.class);
        Mockito.when(serviceProvider.getService(OnHeapStore.Provider.class)).thenReturn(onHeapStoreProvider);
        Mockito.when(serviceProvider.getService(OffHeapStore.Provider.class)).thenReturn(offHeapStoreProvider);
        Mockito.when(serviceProvider.getServicesOfType(AuthoritativeTier.Provider.class)).thenReturn(authorities);
        Mockito.when(serviceProvider.getServicesOfType(CachingTier.Provider.class)).thenReturn(cachingTiers);
        tieredStoreProvider.start(serviceProvider);
        final Store<String, String> tieredStore = tieredStoreProvider.createStore(configuration);
        tieredStoreProvider.initStore(tieredStore);
        tieredStoreProvider.releaseStore(tieredStore);
        Mockito.verify(onHeapStoreProvider, Mockito.times(1)).releaseCachingTier(ArgumentMatchers.any(CachingTier.class));
    }

    @Test
    public void testRank() throws Exception {
        TieredStore.Provider provider = new TieredStore.Provider();
        ServiceLocator serviceLocator = ServiceLocator.dependencySet().with(provider).with(Mockito.mock(DiskResourceService.class)).build();
        serviceLocator.startAllServices();
        assertRank(provider, 0, DISK);
        assertRank(provider, 0, HEAP);
        assertRank(provider, 0, OFFHEAP);
        assertRank(provider, 2, OFFHEAP, HEAP);
        assertRank(provider, 2, DISK, HEAP);
        assertRank(provider, 0, DISK, OFFHEAP);
        assertRank(provider, 3, DISK, OFFHEAP, HEAP);
        final ResourceType<ResourcePool> unmatchedResourceType = new ResourceType<ResourcePool>() {
            @Override
            public Class<ResourcePool> getResourcePoolClass() {
                return ResourcePool.class;
            }

            @Override
            public boolean isPersistable() {
                return true;
            }

            @Override
            public boolean requiresSerialization() {
                return true;
            }

            @Override
            public int getTierHeight() {
                return 10;
            }
        };
        assertRank(provider, 0, unmatchedResourceType);
        assertRank(provider, 0, DISK, OFFHEAP, HEAP, unmatchedResourceType);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetAuthoritativeTierProvider() {
        TieredStore.Provider provider = new TieredStore.Provider();
        ServiceProvider<Service> serviceProvider = Mockito.mock(ServiceProvider.class);
        provider.start(serviceProvider);
        AuthoritativeTier.Provider provider1 = Mockito.mock(AuthoritativeTier.Provider.class);
        Mockito.when(provider1.rankAuthority(ArgumentMatchers.any(ResourceType.class), ArgumentMatchers.any())).thenReturn(1);
        AuthoritativeTier.Provider provider2 = Mockito.mock(AuthoritativeTier.Provider.class);
        Mockito.when(provider2.rankAuthority(ArgumentMatchers.any(ResourceType.class), ArgumentMatchers.any())).thenReturn(2);
        Mockito.when(serviceProvider.getServicesOfType(AuthoritativeTier.Provider.class)).thenReturn(Arrays.asList(provider1, provider2));
        Assert.assertSame(provider.getAuthoritativeTierProvider(Mockito.mock(ResourceType.class), Collections.emptyList()), provider2);
    }
}

