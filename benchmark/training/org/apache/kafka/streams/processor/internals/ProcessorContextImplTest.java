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
package org.apache.kafka.streams.processor.internals;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.SessionStore;
import org.apache.kafka.streams.state.TimestampedWindowStore;
import org.apache.kafka.streams.state.ValueAndTimestamp;
import org.apache.kafka.streams.state.WindowStore;
import org.apache.kafka.streams.state.WindowStoreIterator;
import org.junit.Assert;
import org.junit.Test;


public class ProcessorContextImplTest {
    private ProcessorContextImpl context;

    private static final String KEY = "key";

    private static final long VALUE = 42L;

    private static final ValueAndTimestamp<Long> VALUE_AND_TIMESTAMP = ValueAndTimestamp.make(42L, 21L);

    private static final String STORE_NAME = "underlying-store";

    private boolean flushExecuted;

    private boolean putExecuted;

    private boolean putWithTimestampExecuted;

    private boolean putIfAbsentExecuted;

    private boolean putAllExecuted;

    private boolean deleteExecuted;

    private boolean removeExecuted;

    private KeyValueIterator<String, Long> rangeIter;

    private KeyValueIterator<String, Long> allIter;

    private final List<KeyValueIterator<Windowed<String>, Long>> iters = new ArrayList<>(7);

    private final List<KeyValueIterator<Windowed<String>, ValueAndTimestamp<Long>>> timestampedIters = new ArrayList<>(7);

    private WindowStoreIterator windowStoreIter;

    @Test
    public void globalKeyValueStoreShouldBeReadOnly() {
        doTest("GlobalKeyValueStore", ((Consumer<KeyValueStore<String, Long>>) (( store) -> {
            verifyStoreCannotBeInitializedOrClosed(store);
            checkThrowsUnsupportedOperation(store::flush, "flush()");
            checkThrowsUnsupportedOperation(() -> store.put("1", 1L), "put()");
            checkThrowsUnsupportedOperation(() -> store.putIfAbsent("1", 1L), "putIfAbsent()");
            checkThrowsUnsupportedOperation(() -> store.putAll(Collections.emptyList()), "putAll()");
            checkThrowsUnsupportedOperation(() -> store.delete("1"), "delete()");
            Assert.assertEquals(((Long) (ProcessorContextImplTest.VALUE)), store.get(ProcessorContextImplTest.KEY));
            Assert.assertEquals(rangeIter, store.range("one", "two"));
            Assert.assertEquals(allIter, store.all());
            Assert.assertEquals(ProcessorContextImplTest.VALUE, store.approximateNumEntries());
        })));
    }

    @Test
    public void globalWindowStoreShouldBeReadOnly() {
        doTest("GlobalWindowStore", ((Consumer<WindowStore<String, Long>>) (( store) -> {
            verifyStoreCannotBeInitializedOrClosed(store);
            checkThrowsUnsupportedOperation(store::flush, "flush()");
            checkThrowsUnsupportedOperation(() -> store.put("1", 1L, 1L), "put()");
            checkThrowsUnsupportedOperation(() -> store.put("1", 1L), "put()");
            Assert.assertEquals(iters.get(0), store.fetchAll(0L, 0L));
            Assert.assertEquals(windowStoreIter, store.fetch(ProcessorContextImplTest.KEY, 0L, 1L));
            Assert.assertEquals(iters.get(1), store.fetch(ProcessorContextImplTest.KEY, ProcessorContextImplTest.KEY, 0L, 1L));
            Assert.assertEquals(((Long) (ProcessorContextImplTest.VALUE)), store.fetch(ProcessorContextImplTest.KEY, 1L));
            Assert.assertEquals(iters.get(2), store.all());
        })));
    }

    @Test
    public void globalTimestampedWindowStoreShouldBeReadOnly() {
        doTest("GlobalTimestampedWindowStore", ((Consumer<TimestampedWindowStore<String, Long>>) (( store) -> {
            verifyStoreCannotBeInitializedOrClosed(store);
            checkThrowsUnsupportedOperation(store::flush, "flush()");
            checkThrowsUnsupportedOperation(() -> store.put("1", ValueAndTimestamp.make(1L, 1L), 1L), "put() [with timestamp]");
            checkThrowsUnsupportedOperation(() -> store.put("1", ValueAndTimestamp.make(1L, 1L)), "put() [no timestamp]");
            Assert.assertEquals(timestampedIters.get(0), store.fetchAll(0L, 0L));
            Assert.assertEquals(windowStoreIter, store.fetch(ProcessorContextImplTest.KEY, 0L, 1L));
            Assert.assertEquals(timestampedIters.get(1), store.fetch(ProcessorContextImplTest.KEY, ProcessorContextImplTest.KEY, 0L, 1L));
            Assert.assertEquals(ProcessorContextImplTest.VALUE_AND_TIMESTAMP, store.fetch(ProcessorContextImplTest.KEY, 1L));
            Assert.assertEquals(timestampedIters.get(2), store.all());
        })));
    }

    @Test
    public void globalSessionStoreShouldBeReadOnly() {
        doTest("GlobalSessionStore", ((Consumer<SessionStore<String, Long>>) (( store) -> {
            verifyStoreCannotBeInitializedOrClosed(store);
            checkThrowsUnsupportedOperation(store::flush, "flush()");
            checkThrowsUnsupportedOperation(() -> store.remove(null), "remove()");
            checkThrowsUnsupportedOperation(() -> store.put(null, null), "put()");
            Assert.assertEquals(iters.get(3), store.findSessions(ProcessorContextImplTest.KEY, 1L, 2L));
            Assert.assertEquals(iters.get(4), store.findSessions(ProcessorContextImplTest.KEY, ProcessorContextImplTest.KEY, 1L, 2L));
            Assert.assertEquals(iters.get(5), store.fetch(ProcessorContextImplTest.KEY));
            Assert.assertEquals(iters.get(6), store.fetch(ProcessorContextImplTest.KEY, ProcessorContextImplTest.KEY));
        })));
    }

    @Test
    public void localKeyValueStoreShouldNotAllowInitOrClose() {
        doTest("LocalKeyValueStore", ((Consumer<KeyValueStore<String, Long>>) (( store) -> {
            verifyStoreCannotBeInitializedOrClosed(store);
            store.flush();
            Assert.assertTrue(flushExecuted);
            store.put("1", 1L);
            Assert.assertTrue(putExecuted);
            store.putIfAbsent("1", 1L);
            Assert.assertTrue(putIfAbsentExecuted);
            store.putAll(Collections.emptyList());
            Assert.assertTrue(putAllExecuted);
            store.delete("1");
            Assert.assertTrue(deleteExecuted);
            Assert.assertEquals(((Long) (ProcessorContextImplTest.VALUE)), store.get(ProcessorContextImplTest.KEY));
            Assert.assertEquals(rangeIter, store.range("one", "two"));
            Assert.assertEquals(allIter, store.all());
            Assert.assertEquals(ProcessorContextImplTest.VALUE, store.approximateNumEntries());
        })));
    }

    @Test
    public void localWindowStoreShouldNotAllowInitOrClose() {
        doTest("LocalWindowStore", ((Consumer<WindowStore<String, Long>>) (( store) -> {
            verifyStoreCannotBeInitializedOrClosed(store);
            store.flush();
            Assert.assertTrue(flushExecuted);
            store.put("1", 1L);
            Assert.assertTrue(putExecuted);
            Assert.assertEquals(iters.get(0), store.fetchAll(0L, 0L));
            Assert.assertEquals(windowStoreIter, store.fetch(ProcessorContextImplTest.KEY, 0L, 1L));
            Assert.assertEquals(iters.get(1), store.fetch(ProcessorContextImplTest.KEY, ProcessorContextImplTest.KEY, 0L, 1L));
            Assert.assertEquals(((Long) (ProcessorContextImplTest.VALUE)), store.fetch(ProcessorContextImplTest.KEY, 1L));
            Assert.assertEquals(iters.get(2), store.all());
        })));
    }

    @Test
    public void localTimestampedWindowStoreShouldNotAllowInitOrClose() {
        doTest("LocalTimestampedWindowStore", ((Consumer<TimestampedWindowStore<String, Long>>) (( store) -> {
            verifyStoreCannotBeInitializedOrClosed(store);
            store.flush();
            Assert.assertTrue(flushExecuted);
            store.put("1", ValueAndTimestamp.make(1L, 1L));
            Assert.assertTrue(putExecuted);
            store.put("1", ValueAndTimestamp.make(1L, 1L), 1L);
            Assert.assertTrue(putWithTimestampExecuted);
            Assert.assertEquals(timestampedIters.get(0), store.fetchAll(0L, 0L));
            Assert.assertEquals(windowStoreIter, store.fetch(ProcessorContextImplTest.KEY, 0L, 1L));
            Assert.assertEquals(timestampedIters.get(1), store.fetch(ProcessorContextImplTest.KEY, ProcessorContextImplTest.KEY, 0L, 1L));
            Assert.assertEquals(ProcessorContextImplTest.VALUE_AND_TIMESTAMP, store.fetch(ProcessorContextImplTest.KEY, 1L));
            Assert.assertEquals(timestampedIters.get(2), store.all());
        })));
    }

    @Test
    public void localSessionStoreShouldNotAllowInitOrClose() {
        doTest("LocalSessionStore", ((Consumer<SessionStore<String, Long>>) (( store) -> {
            verifyStoreCannotBeInitializedOrClosed(store);
            store.flush();
            Assert.assertTrue(flushExecuted);
            store.remove(null);
            Assert.assertTrue(removeExecuted);
            store.put(null, null);
            Assert.assertTrue(putExecuted);
            Assert.assertEquals(iters.get(3), store.findSessions(ProcessorContextImplTest.KEY, 1L, 2L));
            Assert.assertEquals(iters.get(4), store.findSessions(ProcessorContextImplTest.KEY, ProcessorContextImplTest.KEY, 1L, 2L));
            Assert.assertEquals(iters.get(5), store.fetch(ProcessorContextImplTest.KEY));
            Assert.assertEquals(iters.get(6), store.fetch(ProcessorContextImplTest.KEY, ProcessorContextImplTest.KEY));
        })));
    }
}

