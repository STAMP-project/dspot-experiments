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


import java.util.Map;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.RocksDBConfigSetter;
import org.junit.Assert;
import org.junit.Test;
import org.rocksdb.Options;


public class RocksDBKeyValueStoreTest extends AbstractKeyValueStoreTest {
    public static class TheRocksDbConfigSetter implements RocksDBConfigSetter {
        static boolean called = false;

        @Override
        public void setConfig(final String storeName, final Options options, final Map<String, Object> configs) {
            RocksDBKeyValueStoreTest.TheRocksDbConfigSetter.called = true;
        }
    }

    @Test
    public void shouldUseCustomRocksDbConfigSetter() {
        Assert.assertTrue(RocksDBKeyValueStoreTest.TheRocksDbConfigSetter.called);
    }

    @Test
    public void shouldPerformRangeQueriesWithCachingDisabled() {
        context.setTime(1L);
        store.put(1, "hi");
        store.put(2, "goodbye");
        final KeyValueIterator<Integer, String> range = store.range(1, 2);
        Assert.assertEquals("hi", range.next().value);
        Assert.assertEquals("goodbye", range.next().value);
        Assert.assertFalse(range.hasNext());
    }

    @Test
    public void shouldPerformAllQueriesWithCachingDisabled() {
        context.setTime(1L);
        store.put(1, "hi");
        store.put(2, "goodbye");
        final KeyValueIterator<Integer, String> range = store.all();
        Assert.assertEquals("hi", range.next().value);
        Assert.assertEquals("goodbye", range.next().value);
        Assert.assertFalse(range.hasNext());
    }

    @Test
    public void shouldCloseOpenIteratorsWhenStoreClosedAndThrowInvalidStateStoreOnHasNextAndNext() {
        context.setTime(1L);
        store.put(1, "hi");
        store.put(2, "goodbye");
        final KeyValueIterator<Integer, String> iteratorOne = store.range(1, 5);
        final KeyValueIterator<Integer, String> iteratorTwo = store.range(1, 4);
        Assert.assertTrue(iteratorOne.hasNext());
        Assert.assertTrue(iteratorTwo.hasNext());
        store.close();
        try {
            iteratorOne.hasNext();
            Assert.fail("should have thrown InvalidStateStoreException on closed store");
        } catch (final InvalidStateStoreException e) {
            // ok
        }
        try {
            iteratorOne.next();
            Assert.fail("should have thrown InvalidStateStoreException on closed store");
        } catch (final InvalidStateStoreException e) {
            // ok
        }
        try {
            iteratorTwo.hasNext();
            Assert.fail("should have thrown InvalidStateStoreException on closed store");
        } catch (final InvalidStateStoreException e) {
            // ok
        }
        try {
            iteratorTwo.next();
            Assert.fail("should have thrown InvalidStateStoreException on closed store");
        } catch (final InvalidStateStoreException e) {
            // ok
        }
    }
}

