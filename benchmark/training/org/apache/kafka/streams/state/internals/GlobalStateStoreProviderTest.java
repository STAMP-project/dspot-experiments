/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.state.internals;


import java.util.Collections;
import java.util.List;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.processor.StateStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.test.NoOpReadOnlyStore;
import org.junit.Assert;
import org.junit.Test;


public class GlobalStateStoreProviderTest {
    @Test
    public void shouldReturnSingleItemListIfStoreExists() {
        final GlobalStateStoreProvider provider = new GlobalStateStoreProvider(Collections.<String, StateStore>singletonMap("global", new NoOpReadOnlyStore()));
        final List<ReadOnlyKeyValueStore<Object, Object>> stores = provider.stores("global", QueryableStoreTypes.keyValueStore());
        Assert.assertEquals(stores.size(), 1);
    }

    @Test
    public void shouldReturnEmptyItemListIfStoreDoesntExist() {
        final GlobalStateStoreProvider provider = new GlobalStateStoreProvider(Collections.<String, StateStore>emptyMap());
        final List<ReadOnlyKeyValueStore<Object, Object>> stores = provider.stores("global", QueryableStoreTypes.keyValueStore());
        Assert.assertTrue(stores.isEmpty());
    }

    @Test(expected = InvalidStateStoreException.class)
    public void shouldThrowExceptionIfStoreIsntOpen() {
        final NoOpReadOnlyStore<Object, Object> store = new NoOpReadOnlyStore<>();
        store.close();
        final GlobalStateStoreProvider provider = new GlobalStateStoreProvider(Collections.<String, StateStore>singletonMap("global", store));
        provider.stores("global", QueryableStoreTypes.keyValueStore());
    }
}

