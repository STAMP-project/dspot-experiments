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
package org.ehcache.core;


import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.ehcache.core.spi.store.Store;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.hamcrest.MockitoHamcrest;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 *
 *
 * @author Ludovic Orban
 */
@SuppressWarnings("unchecked")
public class EhcacheBulkMethodsTest {
    @Test
    public void testPutAll() throws Exception {
        @SuppressWarnings("unchecked")
        Store<Number, CharSequence> store = Mockito.mock(Store.class);
        InternalCache<Number, CharSequence> ehcache = getCache(store);
        ehcache.init();
        Map<Number, CharSequence> map = new HashMap<>(3);
        map.put(1, "one");
        map.put(2, "two");
        map.put(3, "three");
        ehcache.putAll(map);
        Mockito.verify(store).bulkCompute(((Set<? extends Number>) (MockitoHamcrest.argThat(IsCollectionContaining.hasItems(((Number) (1)), 2, 3)))), ArgumentMatchers.any(Function.class));
    }

    @Test
    public void testGetAll() throws Exception {
        Store<Number, CharSequence> store = Mockito.mock(Store.class);
        Mockito.when(store.bulkComputeIfAbsent(((Set<? extends Number>) (MockitoHamcrest.argThat(IsCollectionContaining.hasItems(1, 2, 3)))), ArgumentMatchers.any(Function.class))).thenAnswer(( invocation) -> {
            Function<Iterable<? extends Number>, Iterable<? extends Map.Entry<? extends Number, ? extends CharSequence>>> function = ((Function<Iterable<? extends Number>, Iterable<? extends Map.Entry<? extends Number, ? extends CharSequence>>>) (invocation.getArguments()[1]));
            Set<? extends Number> keys = ((Set<? extends Number>) (invocation.getArguments()[0]));
            function.apply(keys);
            Map<Number, Store.ValueHolder<String>> map = new HashMap<>();
            map.put(1, null);
            map.put(2, null);
            map.put(3, EhcacheBulkMethodsTest.valueHolder("three"));
            return map;
        });
        InternalCache<Number, CharSequence> ehcache = getCache(store);
        ehcache.init();
        Map<Number, CharSequence> result = ehcache.getAll(new HashSet<Number>(Arrays.asList(1, 2, 3)));
        Assert.assertThat(result, Matchers.hasEntry(1, null));
        Assert.assertThat(result, Matchers.hasEntry(2, null));
        Assert.assertThat(result, Matchers.hasEntry(3, "three"));
        Mockito.verify(store).bulkComputeIfAbsent(((Set<? extends Number>) (MockitoHamcrest.argThat(IsCollectionContaining.hasItems(1, 2, 3)))), ArgumentMatchers.any(Function.class));
    }

    @Test
    public void testRemoveAll() throws Exception {
        Store<Number, CharSequence> store = Mockito.mock(Store.class);
        InternalCache<Number, CharSequence> ehcache = getCache(store);
        ehcache.init();
        ehcache.removeAll(new HashSet<Number>(Arrays.asList(1, 2, 3)));
        Mockito.verify(store).bulkCompute(((Set<? extends Number>) (MockitoHamcrest.argThat(IsCollectionContaining.hasItems(1, 2, 3)))), ArgumentMatchers.any(Function.class));
    }
}

