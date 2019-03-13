/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.journal;


import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.internal.journal.EventJournalInitialSubscriberState;
import com.hazelcast.ringbuffer.ReadResultSet;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.util.function.Function;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;

import static com.hazelcast.journal.EventJournalEventAdapter.EventType.ADDED;
import static com.hazelcast.journal.EventJournalEventAdapter.EventType.LOADED;
import static com.hazelcast.journal.EventJournalEventAdapter.EventType.REMOVED;
import static com.hazelcast.journal.EventJournalEventAdapter.EventType.UPDATED;


/**
 * Base class for implementing data-structure specific basic event journal test.
 *
 * @param <EJ_TYPE>
 * 		the type of the event journal event
 */
public abstract class AbstractEventJournalBasicTest<EJ_TYPE> extends HazelcastTestSupport {
    private static final Random RANDOM = new Random();

    protected HazelcastInstance[] instances;

    private int partitionId;

    private TruePredicate<EJ_TYPE> TRUE_PREDICATE = new TruePredicate<EJ_TYPE>();

    private Function<EJ_TYPE, EJ_TYPE> IDENTITY_FUNCTION = new IdentityFunction<EJ_TYPE>();

    private boolean loadAllPublishesAdded = false;

    /**
     * Tests that event journal read operations parked on different partitions
     * can be woken up independently.
     */
    @Test
    public void unparkReadOperation() {
        init();
        final EventJournalTestContext<String, Integer, EJ_TYPE> context = createContext();
        assertEventJournalSize(context.dataAdapter, 0);
        final String key = randomPartitionKey();
        final Integer value = AbstractEventJournalBasicTest.RANDOM.nextInt();
        final CountDownLatch latch = new CountDownLatch(1);
        final ExecutionCallback<ReadResultSet<EJ_TYPE>> ec = addEventExecutionCallback(context, key, value, latch);
        readFromEventJournal(context.dataAdapter, 0, 100, partitionId, TRUE_PREDICATE, IDENTITY_FUNCTION).andThen(ec);
        readFromEventJournal(context.dataAdapter, 0, 100, ((partitionId) + 1), TRUE_PREDICATE, IDENTITY_FUNCTION).andThen(ec);
        readFromEventJournal(context.dataAdapter, 0, 100, ((partitionId) + 2), TRUE_PREDICATE, IDENTITY_FUNCTION).andThen(ec);
        readFromEventJournal(context.dataAdapter, 0, 100, ((partitionId) + 3), TRUE_PREDICATE, IDENTITY_FUNCTION).andThen(ec);
        readFromEventJournal(context.dataAdapter, 0, 100, ((partitionId) + 4), TRUE_PREDICATE, IDENTITY_FUNCTION).andThen(ec);
        context.dataAdapter.put(key, value);
        HazelcastTestSupport.assertOpenEventually(latch, 30);
        assertEventJournalSize(context.dataAdapter, 1);
    }

    @Test
    public void readManyFromEventJournalShouldNotBlock_whenHitsStale() throws InterruptedException, ExecutionException {
        init();
        final EventJournalTestContext<String, Integer, EJ_TYPE> context = createContext();
        assertEventJournalSize(context.dataAdapter, 0);
        final CountDownLatch latch = new CountDownLatch(1);
        final ExecutionCallback<ReadResultSet<EJ_TYPE>> ec = new ExecutionCallback<ReadResultSet<EJ_TYPE>>() {
            @Override
            public void onResponse(ReadResultSet<EJ_TYPE> response) {
                latch.countDown();
            }

            @Override
            public void onFailure(Throwable t) {
            }
        };
        Thread consumer = new Thread(new Runnable() {
            @Override
            public void run() {
                readFromEventJournal(context.dataAdapter, 0, 10, partitionId, TRUE_PREDICATE, IDENTITY_FUNCTION).andThen(ec);
            }
        });
        consumer.start();
        Map<String, Integer> addMap = new HashMap<String, Integer>();
        for (int i = 0; i < 501; i++) {
            addMap.put(randomPartitionKey(), AbstractEventJournalBasicTest.RANDOM.nextInt());
        }
        context.dataAdapter.putAll(addMap);
        HazelcastTestSupport.assertOpenEventually(latch, 30);
    }

    @Test
    public void receiveAddedEventsWhenPut() throws Exception {
        init();
        final EventJournalTestContext<String, Integer, EJ_TYPE> context = createContext();
        final int count = 100;
        assertEventJournalSize(context.dataAdapter, 0);
        for (int i = 0; i < count; i++) {
            context.dataAdapter.put(randomPartitionKey(), i);
        }
        assertEventJournalSize(context.dataAdapter, count);
        final ReadResultSet<EJ_TYPE> events = getAllEvents(context.dataAdapter, null, null);
        Assert.assertEquals(count, events.size());
        final HashMap<String, Integer> received = new HashMap<String, Integer>();
        final EventJournalEventAdapter<String, Integer, EJ_TYPE> journalAdapter = context.eventJournalAdapter;
        for (EJ_TYPE e : events) {
            Assert.assertEquals(ADDED, journalAdapter.getType(e));
            Assert.assertNull(journalAdapter.getOldValue(e));
            received.put(journalAdapter.getKey(e), journalAdapter.getNewValue(e));
        }
        Assert.assertEquals(context.dataAdapter.entrySet(), received.entrySet());
    }

    @Test
    public void receiveLoadedEventsWhenLoad() throws Exception {
        init();
        final EventJournalTestContext<String, String, EJ_TYPE> context = createContext();
        final int count = 100;
        assertEventJournalSize(context.dataAdapter, 0);
        for (int i = 0; i < count; i++) {
            context.dataAdapter.load(randomPartitionKey());
        }
        assertEventJournalSize(context.dataAdapter, count);
        final ReadResultSet<EJ_TYPE> events = getAllEvents(context.dataAdapter, null, null);
        Assert.assertEquals(count, events.size());
        final HashMap<String, String> received = new HashMap<String, String>();
        final EventJournalEventAdapter<String, String, EJ_TYPE> journalAdapter = context.eventJournalAdapter;
        for (EJ_TYPE e : events) {
            Assert.assertEquals(LOADED, journalAdapter.getType(e));
            Assert.assertNull(journalAdapter.getOldValue(e));
            received.put(journalAdapter.getKey(e), journalAdapter.getNewValue(e));
        }
        Assert.assertEquals(context.dataAdapter.entrySet(), received.entrySet());
    }

    @Test
    public void receiveLoadedEventsWhenLoadAll() throws Exception {
        testLoadAll(LOADED);
    }

    @Test
    public void receiveAddedEventsWhenLoadAll() throws Exception {
        testLoadAll(ADDED);
    }

    @Test
    public void receiveExpirationEventsWhenPutWithTtl() {
        init();
        final EventJournalTestContext<String, Integer, EJ_TYPE> context = createContext();
        final EventJournalDataStructureAdapter<String, Integer, EJ_TYPE> adapter = context.dataAdapter;
        testExpiration(context, adapter, new com.hazelcast.util.function.BiConsumer<String, Integer>() {
            @Override
            public void accept(String k, Integer v) {
                adapter.put(k, v, 1, TimeUnit.SECONDS);
            }
        });
    }

    @Test
    public void receiveExpirationEventsWhenPutOnExpiringStructure() {
        init();
        final EventJournalTestContext<String, Integer, EJ_TYPE> context = createContext();
        final EventJournalDataStructureAdapter<String, Integer, EJ_TYPE> adapter = context.dataAdapterWithExpiration;
        testExpiration(context, adapter, new com.hazelcast.util.function.BiConsumer<String, Integer>() {
            @Override
            public void accept(String k, Integer i) {
                adapter.put(k, i);
            }
        });
    }

    @Test
    public void receiveRemoveEventsWhenRemove() throws Exception {
        init();
        final EventJournalTestContext<String, Integer, EJ_TYPE> context = createContext();
        final int count = 100;
        assertEventJournalSize(context.dataAdapter, 0);
        final Map<String, Integer> initialMap = AbstractEventJournalBasicTest.createHashMap(count);
        for (int v = 0; v < count; v++) {
            final String k = randomPartitionKey();
            context.dataAdapter.put(k, v);
            initialMap.put(k, v);
        }
        assertEventJournalSize(context.dataAdapter, count);
        for (Map.Entry<String, Integer> e : context.dataAdapter.entrySet()) {
            context.dataAdapter.remove(e.getKey());
        }
        final HashMap<String, Integer> added = new HashMap<String, Integer>(count);
        final HashMap<String, Integer> removed = new HashMap<String, Integer>(count);
        final EventJournalEventAdapter<String, Integer, EJ_TYPE> journalAdapter = context.eventJournalAdapter;
        for (EJ_TYPE e : getAllEvents(context.dataAdapter, TRUE_PREDICATE, IDENTITY_FUNCTION)) {
            switch (journalAdapter.getType(e)) {
                case ADDED :
                    added.put(journalAdapter.getKey(e), journalAdapter.getNewValue(e));
                    break;
                case REMOVED :
                    removed.put(journalAdapter.getKey(e), journalAdapter.getOldValue(e));
                    break;
            }
        }
        Assert.assertEquals(0, context.dataAdapter.size());
        assertEventJournalSize(context.dataAdapter, (count * 2));
        Assert.assertEquals(initialMap, added);
        Assert.assertEquals(initialMap, removed);
    }

    @Test
    public void receiveUpdateEventsOnMapPut() throws Exception {
        init();
        final EventJournalTestContext<String, Integer, EJ_TYPE> context = createContext();
        final int count = 100;
        assertEventJournalSize(context.dataAdapter, 0);
        final Map<String, Integer> initialMap = AbstractEventJournalBasicTest.createHashMap(count);
        for (int v = 0; v < count; v++) {
            final String k = randomPartitionKey();
            context.dataAdapter.put(k, v);
            initialMap.put(k, v);
        }
        assertEventJournalSize(context.dataAdapter, count);
        for (Map.Entry<String, Integer> e : context.dataAdapter.entrySet()) {
            final String key = e.getKey();
            final Integer newVal = (initialMap.get(key)) + 100;
            context.dataAdapter.put(key, newVal);
        }
        assertEventJournalSize(context.dataAdapter, (count * 2));
        final Map<String, Integer> updatedFrom = AbstractEventJournalBasicTest.createHashMap(count);
        final Map<String, Integer> updatedTo = AbstractEventJournalBasicTest.createHashMap(count);
        final EventJournalEventAdapter<String, Integer, EJ_TYPE> journalAdapter = context.eventJournalAdapter;
        for (EJ_TYPE e : getAllEvents(context.dataAdapter, TRUE_PREDICATE, IDENTITY_FUNCTION)) {
            switch (journalAdapter.getType(e)) {
                case UPDATED :
                    updatedFrom.put(journalAdapter.getKey(e), journalAdapter.getOldValue(e));
                    updatedTo.put(journalAdapter.getKey(e), journalAdapter.getNewValue(e));
                    break;
            }
        }
        Assert.assertEquals(initialMap, updatedFrom);
        Assert.assertEquals(context.dataAdapter.entrySet(), updatedTo.entrySet());
    }

    @Test
    public void testPredicates() throws Exception {
        init();
        final EventJournalTestContext<String, Integer, EJ_TYPE> context = createContext();
        final int count = 50;
        assertEventJournalSize(context.dataAdapter, 0);
        for (int i = 0; i < count; i++) {
            context.dataAdapter.put(randomPartitionKey(), i);
        }
        assertEventJournalSize(context.dataAdapter, count);
        final Map<String, Integer> evenMap = AbstractEventJournalBasicTest.createHashMap((count / 2));
        final Map<String, Integer> oddMap = AbstractEventJournalBasicTest.createHashMap((count / 2));
        final EventJournalEventAdapter<String, Integer, EJ_TYPE> journalAdapter = context.eventJournalAdapter;
        final NewValueParityPredicate<EJ_TYPE> evenPredicate = new NewValueParityPredicate<EJ_TYPE>(0, journalAdapter);
        final NewValueParityPredicate<EJ_TYPE> oddPredicate = new NewValueParityPredicate<EJ_TYPE>(1, journalAdapter);
        for (EJ_TYPE e : getAllEvents(context.dataAdapter, evenPredicate, IDENTITY_FUNCTION)) {
            Assert.assertEquals(ADDED, journalAdapter.getType(e));
            evenMap.put(journalAdapter.getKey(e), journalAdapter.getNewValue(e));
        }
        for (EJ_TYPE e : getAllEvents(context.dataAdapter, oddPredicate, IDENTITY_FUNCTION)) {
            Assert.assertEquals(ADDED, journalAdapter.getType(e));
            oddMap.put(journalAdapter.getKey(e), journalAdapter.getNewValue(e));
        }
        Assert.assertEquals((count / 2), evenMap.size());
        Assert.assertEquals((count / 2), oddMap.size());
        for (Map.Entry<String, Integer> e : evenMap.entrySet()) {
            final Integer v = e.getValue();
            Assert.assertTrue(((v % 2) == 0));
            Assert.assertEquals(context.dataAdapter.get(e.getKey()), v);
        }
        for (Map.Entry<String, Integer> e : oddMap.entrySet()) {
            final Integer v = e.getValue();
            Assert.assertTrue(((v % 2) == 1));
            Assert.assertEquals(context.dataAdapter.get(e.getKey()), v);
        }
    }

    @Test
    public void testProjection() throws Exception {
        init();
        final EventJournalTestContext<String, Integer, EJ_TYPE> context = createContext();
        final int count = 50;
        assertEventJournalSize(context.dataAdapter, 0);
        for (int i = 0; i < count; i++) {
            context.dataAdapter.put(randomPartitionKey(), i);
        }
        assertEventJournalSize(context.dataAdapter, count);
        final ReadResultSet<Integer> resultSet = getAllEvents(context.dataAdapter, TRUE_PREDICATE, new NewValueIncrementingFunction<EJ_TYPE>(100, context.eventJournalAdapter));
        final ArrayList<Integer> ints = new ArrayList<Integer>(count);
        for (Integer i : resultSet) {
            ints.add(i);
        }
        Assert.assertEquals(count, ints.size());
        final Set<Map.Entry<String, Integer>> entries = context.dataAdapter.entrySet();
        for (Map.Entry<String, Integer> e : entries) {
            Assert.assertTrue(ints.contains(((e.getValue()) + 100)));
        }
    }

    @Test
    public void skipEventsWhenFallenBehind() throws Exception {
        init();
        final EventJournalTestContext<String, Integer, EJ_TYPE> context = createContext();
        final int count = 1000;
        assertEventJournalSize(context.dataAdapter, 0);
        for (int i = 0; i < count; i++) {
            context.dataAdapter.put(randomPartitionKey(), i);
        }
        final EventJournalInitialSubscriberState state = subscribeToEventJournal(context.dataAdapter, partitionId);
        Assert.assertEquals(500, state.getOldestSequence());
        Assert.assertEquals(999, state.getNewestSequence());
        assertEventJournalSize(context.dataAdapter, 500);
        final int startSequence = 0;
        final ReadResultSet<EJ_TYPE> resultSet = readFromEventJournal(context.dataAdapter, startSequence, 1, partitionId, TRUE_PREDICATE, IDENTITY_FUNCTION).get();
        Assert.assertEquals(1, resultSet.size());
        Assert.assertEquals(1, resultSet.readCount());
        Assert.assertNotEquals((startSequence + (resultSet.readCount())), resultSet.getNextSequenceToReadFrom());
        Assert.assertEquals(501, resultSet.getNextSequenceToReadFrom());
        final long lostCount = ((resultSet.getNextSequenceToReadFrom()) - (resultSet.readCount())) - startSequence;
        Assert.assertEquals(500, lostCount);
    }

    @Test
    public void nextSequenceProceedsWhenReadFromEventJournalWhileMinSizeIsZero() throws Exception {
        init();
        final EventJournalTestContext<String, Integer, EJ_TYPE> context = createContext();
        final int count = 1000;
        assertEventJournalSize(context.dataAdapter, 0);
        for (int i = 0; i < count; i++) {
            context.dataAdapter.put(randomPartitionKey(), i);
        }
        final EventJournalInitialSubscriberState state = subscribeToEventJournal(context.dataAdapter, partitionId);
        Assert.assertEquals(500, state.getOldestSequence());
        Assert.assertEquals(999, state.getNewestSequence());
        assertEventJournalSize(context.dataAdapter, 500);
        final int startSequence = 0;
        final ReadResultSet<EJ_TYPE> resultSet = readFromEventJournal(context.dataAdapter, startSequence, 1, 0, partitionId, TRUE_PREDICATE, IDENTITY_FUNCTION).get();
        Assert.assertEquals(1, resultSet.size());
        Assert.assertEquals(1, resultSet.readCount());
        Assert.assertNotEquals((startSequence + (resultSet.readCount())), resultSet.getNextSequenceToReadFrom());
        Assert.assertEquals(501, resultSet.getNextSequenceToReadFrom());
        final long lostCount = ((resultSet.getNextSequenceToReadFrom()) - (resultSet.readCount())) - startSequence;
        Assert.assertEquals(500, lostCount);
    }

    @Test
    public void allowReadingWithFutureSeq() throws Exception {
        init();
        final EventJournalTestContext<String, Integer, EJ_TYPE> context = createContext();
        final EventJournalInitialSubscriberState state = subscribeToEventJournal(context.dataAdapter, partitionId);
        Assert.assertEquals(0, state.getOldestSequence());
        Assert.assertEquals((-1), state.getNewestSequence());
        assertEventJournalSize(context.dataAdapter, 0);
        final String key = randomPartitionKey();
        final Integer value = AbstractEventJournalBasicTest.RANDOM.nextInt();
        final CountDownLatch latch = new CountDownLatch(1);
        final int startSequence = 1;
        final ExecutionCallback<ReadResultSet<EJ_TYPE>> callback = new ExecutionCallback<ReadResultSet<EJ_TYPE>>() {
            @Override
            public void onResponse(ReadResultSet<EJ_TYPE> response) {
                Assert.assertEquals(1, response.size());
                final EventJournalEventAdapter<String, Integer, EJ_TYPE> journalAdapter = context.eventJournalAdapter;
                final EJ_TYPE e = response.get(0);
                Assert.assertEquals(ADDED, journalAdapter.getType(e));
                Assert.assertEquals(key, journalAdapter.getKey(e));
                Assert.assertEquals(value, journalAdapter.getNewValue(e));
                Assert.assertNotEquals((startSequence + (response.readCount())), response.getNextSequenceToReadFrom());
                Assert.assertEquals(1, response.getNextSequenceToReadFrom());
                latch.countDown();
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        };
        readFromEventJournal(context.dataAdapter, startSequence, 1, partitionId, TRUE_PREDICATE, IDENTITY_FUNCTION).andThen(callback);
        context.dataAdapter.put(key, value);
        HazelcastTestSupport.assertOpenEventually(latch, 30);
        assertEventJournalSize(context.dataAdapter, 1);
    }
}

