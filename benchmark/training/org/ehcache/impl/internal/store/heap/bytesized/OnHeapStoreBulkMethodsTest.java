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
package org.ehcache.impl.internal.store.heap.bytesized;


import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.events.NullStoreEventDispatcher;
import org.ehcache.core.spi.store.Store;
import org.ehcache.core.spi.time.SystemTimeSource;
import org.ehcache.impl.copy.IdentityCopier;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;
import org.ehcache.impl.internal.sizeof.DefaultSizeOfEngine;
import org.ehcache.impl.internal.store.heap.OnHeapStore;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class OnHeapStoreBulkMethodsTest extends org.ehcache.impl.internal.store.heap.OnHeapStoreBulkMethodsTest {
    @SuppressWarnings("unchecked")
    @Test
    public void testBulkComputeFunctionGetsValuesOfEntries() throws Exception {
        @SuppressWarnings("rawtypes")
        Store.Configuration config = Mockito.mock(Store.Configuration.class);
        Mockito.when(config.getExpiry()).thenReturn(ExpiryPolicyBuilder.noExpiration());
        Mockito.when(config.getKeyType()).thenReturn(Number.class);
        Mockito.when(config.getValueType()).thenReturn(Number.class);
        Mockito.when(config.getResourcePools()).thenReturn(ResourcePoolsBuilder.newResourcePoolsBuilder().heap(100, MemoryUnit.KB).build());
        Store.Configuration<Number, Number> configuration = config;
        OnHeapStore<Number, Number> store = new OnHeapStore<>(configuration, SystemTimeSource.INSTANCE, IdentityCopier.identityCopier(), IdentityCopier.identityCopier(), new DefaultSizeOfEngine(Long.MAX_VALUE, Long.MAX_VALUE), NullStoreEventDispatcher.<Number, Number>nullStoreEventDispatcher());
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
}

