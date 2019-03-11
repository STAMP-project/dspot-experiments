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
package org.apache.kafka.streams.state;


import java.time.Duration;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.state.internals.InMemoryKeyValueStore;
import org.apache.kafka.streams.state.internals.MemoryNavigableLRUCache;
import org.apache.kafka.streams.state.internals.RocksDBSessionStore;
import org.apache.kafka.streams.state.internals.RocksDBStore;
import org.apache.kafka.streams.state.internals.RocksDBTimestampedStore;
import org.apache.kafka.streams.state.internals.RocksDBWindowStore;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsInstanceOf;
import org.hamcrest.core.IsNot;
import org.junit.Test;


public class StoresTest {
    @Test(expected = NullPointerException.class)
    public void shouldThrowIfPersistentKeyValueStoreStoreNameIsNull() {
        Stores.persistentKeyValueStore(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowIfIMemoryKeyValueStoreStoreNameIsNull() {
        // noinspection ResultOfMethodCallIgnored
        Stores.inMemoryKeyValueStore(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowIfILruMapStoreNameIsNull() {
        Stores.lruMap(null, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfILruMapStoreCapacityIsNegative() {
        Stores.lruMap("anyName", (-1));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowIfIPersistentWindowStoreStoreNameIsNull() {
        Stores.persistentWindowStore(null, Duration.ZERO, Duration.ZERO, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfIPersistentWindowStoreRetentionPeriodIsNegative() {
        Stores.persistentWindowStore("anyName", Duration.ofMillis((-1L)), Duration.ZERO, false);
    }

    @Deprecated
    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfIPersistentWindowStoreIfNumberOfSegmentsSmallerThanOne() {
        Stores.persistentWindowStore("anyName", 0L, 1, 0L, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfIPersistentWindowStoreIfWindowSizeIsNegative() {
        Stores.persistentWindowStore("anyName", Duration.ofMillis(0L), Duration.ofMillis((-1L)), false);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowIfIPersistentSessionStoreStoreNameIsNull() {
        Stores.persistentSessionStore(null, Duration.ofMillis(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfIPersistentSessionStoreRetentionPeriodIsNegative() {
        Stores.persistentSessionStore("anyName", Duration.ofMillis((-1)));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowIfSupplierIsNullForWindowStoreBuilder() {
        Stores.windowStoreBuilder(null, Serdes.ByteArray(), Serdes.ByteArray());
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowIfSupplierIsNullForKeyValueStoreBuilder() {
        Stores.keyValueStoreBuilder(null, Serdes.ByteArray(), Serdes.ByteArray());
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowIfSupplierIsNullForSessionStoreBuilder() {
        Stores.sessionStoreBuilder(null, Serdes.ByteArray(), Serdes.ByteArray());
    }

    @Test
    public void shouldCreateInMemoryKeyValueStore() {
        MatcherAssert.assertThat(Stores.inMemoryKeyValueStore("memory").get(), IsInstanceOf.instanceOf(InMemoryKeyValueStore.class));
    }

    @Test
    public void shouldCreateMemoryNavigableCache() {
        MatcherAssert.assertThat(Stores.lruMap("map", 10).get(), IsInstanceOf.instanceOf(MemoryNavigableLRUCache.class));
    }

    @Test
    public void shouldCreateRocksDbStore() {
        MatcherAssert.assertThat(Stores.persistentKeyValueStore("store").get(), CoreMatchers.allOf(IsNot.not(IsInstanceOf.instanceOf(RocksDBTimestampedStore.class)), IsInstanceOf.instanceOf(RocksDBStore.class)));
    }

    @Test
    public void shouldCreateRocksDbWindowStore() {
        MatcherAssert.assertThat(Stores.persistentWindowStore("store", Duration.ofMillis(1L), Duration.ofMillis(1L), false).get(), IsInstanceOf.instanceOf(RocksDBWindowStore.class));
    }

    @Test
    public void shouldCreateRocksDbSessionStore() {
        MatcherAssert.assertThat(Stores.persistentSessionStore("store", Duration.ofMillis(1)).get(), IsInstanceOf.instanceOf(RocksDBSessionStore.class));
    }

    @Test
    public void shouldBuildWindowStore() {
        final WindowStore<String, String> store = Stores.windowStoreBuilder(Stores.persistentWindowStore("store", Duration.ofMillis(3L), Duration.ofMillis(3L), true), Serdes.String(), Serdes.String()).build();
        MatcherAssert.assertThat(store, IsNot.not(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldBuildKeyValueStore() {
        final KeyValueStore<String, String> store = Stores.keyValueStoreBuilder(Stores.persistentKeyValueStore("name"), Serdes.String(), Serdes.String()).build();
        MatcherAssert.assertThat(store, IsNot.not(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldBuildSessionStore() {
        final SessionStore<String, String> store = Stores.sessionStoreBuilder(Stores.persistentSessionStore("name", Duration.ofMillis(10)), Serdes.String(), Serdes.String()).build();
        MatcherAssert.assertThat(store, IsNot.not(CoreMatchers.nullValue()));
    }
}

