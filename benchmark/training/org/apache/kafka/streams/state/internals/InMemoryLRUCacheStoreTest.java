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


import java.util.Arrays;
import java.util.List;
import org.apache.kafka.streams.KeyValue;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


public class InMemoryLRUCacheStoreTest extends AbstractKeyValueStoreTest {
    @Test
    public void shouldPutAllKeyValuePairs() {
        final List<KeyValue<Integer, String>> kvPairs = Arrays.asList(KeyValue.pair(1, "1"), KeyValue.pair(2, "2"), KeyValue.pair(3, "3"));
        store.putAll(kvPairs);
        MatcherAssert.assertThat(store.approximateNumEntries(), CoreMatchers.equalTo(3L));
        for (final KeyValue<Integer, String> kvPair : kvPairs) {
            MatcherAssert.assertThat(store.get(kvPair.key), CoreMatchers.equalTo(kvPair.value));
        }
    }

    @Test
    public void shouldUpdateValuesForExistingKeysOnPutAll() {
        final List<KeyValue<Integer, String>> kvPairs = Arrays.asList(KeyValue.pair(1, "1"), KeyValue.pair(2, "2"), KeyValue.pair(3, "3"));
        store.putAll(kvPairs);
        final List<KeyValue<Integer, String>> updatedKvPairs = Arrays.asList(KeyValue.pair(1, "ONE"), KeyValue.pair(2, "TWO"), KeyValue.pair(3, "THREE"));
        store.putAll(updatedKvPairs);
        MatcherAssert.assertThat(store.approximateNumEntries(), CoreMatchers.equalTo(3L));
        for (final KeyValue<Integer, String> kvPair : updatedKvPairs) {
            MatcherAssert.assertThat(store.get(kvPair.key), CoreMatchers.equalTo(kvPair.value));
        }
    }

    @Test
    public void testEvict() {
        // Create the test driver ...
        store.put(0, "zero");
        store.put(1, "one");
        store.put(2, "two");
        store.put(3, "three");
        store.put(4, "four");
        store.put(5, "five");
        store.put(6, "six");
        store.put(7, "seven");
        store.put(8, "eight");
        store.put(9, "nine");
        Assert.assertEquals(10, driver.sizeOf(store));
        store.put(10, "ten");
        store.flush();
        Assert.assertEquals(10, driver.sizeOf(store));
        Assert.assertTrue(driver.flushedEntryRemoved(0));
        Assert.assertEquals(1, driver.numFlushedEntryRemoved());
        store.delete(1);
        store.flush();
        Assert.assertEquals(9, driver.sizeOf(store));
        Assert.assertTrue(driver.flushedEntryRemoved(0));
        Assert.assertTrue(driver.flushedEntryRemoved(1));
        Assert.assertEquals(2, driver.numFlushedEntryRemoved());
        store.put(11, "eleven");
        store.flush();
        Assert.assertEquals(10, driver.sizeOf(store));
        Assert.assertEquals(2, driver.numFlushedEntryRemoved());
        store.put(2, "two-again");
        store.flush();
        Assert.assertEquals(10, driver.sizeOf(store));
        Assert.assertEquals(2, driver.numFlushedEntryRemoved());
        store.put(12, "twelve");
        store.flush();
        Assert.assertEquals(10, driver.sizeOf(store));
        Assert.assertTrue(driver.flushedEntryRemoved(0));
        Assert.assertTrue(driver.flushedEntryRemoved(1));
        Assert.assertTrue(driver.flushedEntryRemoved(3));
        Assert.assertEquals(3, driver.numFlushedEntryRemoved());
    }

    @Test
    public void testRestoreEvict() {
        store.close();
        // Add any entries that will be restored to any store
        // that uses the driver's context ...
        driver.addEntryToRestoreLog(0, "zero");
        driver.addEntryToRestoreLog(1, "one");
        driver.addEntryToRestoreLog(2, "two");
        driver.addEntryToRestoreLog(3, "three");
        driver.addEntryToRestoreLog(4, "four");
        driver.addEntryToRestoreLog(5, "five");
        driver.addEntryToRestoreLog(6, "fix");
        driver.addEntryToRestoreLog(7, "seven");
        driver.addEntryToRestoreLog(8, "eight");
        driver.addEntryToRestoreLog(9, "nine");
        driver.addEntryToRestoreLog(10, "ten");
        // Create the store, which should register with the context and automatically
        // receive the restore entries ...
        store = createKeyValueStore(driver.context());
        context.restore(store.name(), driver.restoredEntries());
        // Verify that the store's changelog does not get more appends ...
        Assert.assertEquals(0, driver.numFlushedEntryStored());
        Assert.assertEquals(0, driver.numFlushedEntryRemoved());
        // and there are no other entries ...
        Assert.assertEquals(10, driver.sizeOf(store));
    }
}

