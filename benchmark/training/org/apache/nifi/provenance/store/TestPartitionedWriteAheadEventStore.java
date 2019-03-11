/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.provenance.store;


import EventAuthorizer.GRANT_ALL;
import EventTransformer.EMPTY_TRANSFORMER;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.nifi.authorization.AccessDeniedException;
import org.apache.nifi.events.EventReporter;
import org.apache.nifi.provenance.ProvenanceEventRecord;
import org.apache.nifi.provenance.RepositoryConfiguration;
import org.apache.nifi.provenance.authorization.EventAuthorizer;
import org.apache.nifi.provenance.serialization.RecordReaders;
import org.apache.nifi.provenance.serialization.RecordWriters;
import org.apache.nifi.provenance.serialization.StorageSummary;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


public class TestPartitionedWriteAheadEventStore {
    private static final RecordWriterFactory writerFactory = ( file, idGen, compress, createToc) -> RecordWriters.newSchemaRecordWriter(file, idGen, compress, createToc);

    private static final RecordReaderFactory readerFactory = ( file, logs, maxChars) -> RecordReaders.newRecordReader(file, logs, maxChars);

    private final AtomicLong idGenerator = new AtomicLong(0L);

    @Rule
    public TestName testName = new TestName();

    @Test
    public void testSingleWriteThenRead() throws IOException {
        final PartitionedWriteAheadEventStore store = new PartitionedWriteAheadEventStore(createConfig(), TestPartitionedWriteAheadEventStore.writerFactory, TestPartitionedWriteAheadEventStore.readerFactory, EventReporter.NO_OP, new EventFileManager());
        store.initialize();
        Assert.assertEquals((-1), store.getMaxEventId());
        final ProvenanceEventRecord event1 = createEvent();
        final StorageResult result = store.addEvents(Collections.singleton(event1));
        final StorageSummary summary = result.getStorageLocations().values().iterator().next();
        final long eventId = summary.getEventId();
        final ProvenanceEventRecord eventWithId = addId(event1, eventId);
        Assert.assertEquals(0, store.getMaxEventId());
        final ProvenanceEventRecord read = store.getEvent(eventId).get();
        Assert.assertEquals(eventWithId, read);
    }

    @Test
    public void testMultipleWritesThenReads() throws IOException {
        final PartitionedWriteAheadEventStore store = new PartitionedWriteAheadEventStore(createConfig(), TestPartitionedWriteAheadEventStore.writerFactory, TestPartitionedWriteAheadEventStore.readerFactory, EventReporter.NO_OP, new EventFileManager());
        store.initialize();
        Assert.assertEquals((-1), store.getMaxEventId());
        final int numEvents = 20;
        final List<ProvenanceEventRecord> events = new ArrayList<>(numEvents);
        for (int i = 0; i < numEvents; i++) {
            final ProvenanceEventRecord event = createEvent();
            store.addEvents(Collections.singleton(event));
            Assert.assertEquals(i, store.getMaxEventId());
            events.add(event);
        }
        for (int i = 0; i < numEvents; i++) {
            final ProvenanceEventRecord read = store.getEvent(i).get();
            Assert.assertEquals(events.get(i), read);
        }
    }

    @Test
    public void testMultipleWritesThenGetAllInSingleRead() throws IOException {
        final PartitionedWriteAheadEventStore store = new PartitionedWriteAheadEventStore(createConfig(), TestPartitionedWriteAheadEventStore.writerFactory, TestPartitionedWriteAheadEventStore.readerFactory, EventReporter.NO_OP, new EventFileManager());
        store.initialize();
        Assert.assertEquals((-1), store.getMaxEventId());
        final int numEvents = 20;
        final List<ProvenanceEventRecord> events = new ArrayList<>(numEvents);
        for (int i = 0; i < numEvents; i++) {
            final ProvenanceEventRecord event = createEvent();
            store.addEvents(Collections.singleton(event));
            Assert.assertEquals(i, store.getMaxEventId());
            events.add(event);
        }
        List<ProvenanceEventRecord> eventsRead = store.getEvents(0L, numEvents, null, EMPTY_TRANSFORMER);
        Assert.assertNotNull(eventsRead);
        Assert.assertEquals(numEvents, eventsRead.size());
        for (int i = 0; i < numEvents; i++) {
            final ProvenanceEventRecord read = eventsRead.get(i);
            Assert.assertEquals(events.get(i), read);
        }
        eventsRead = store.getEvents((-1000), 1000, null, EMPTY_TRANSFORMER);
        Assert.assertNotNull(eventsRead);
        Assert.assertTrue(eventsRead.isEmpty());
        eventsRead = store.getEvents(10, 0, null, EMPTY_TRANSFORMER);
        Assert.assertNotNull(eventsRead);
        Assert.assertTrue(eventsRead.isEmpty());
        eventsRead = store.getEvents(10, 1, null, EMPTY_TRANSFORMER);
        Assert.assertNotNull(eventsRead);
        Assert.assertFalse(eventsRead.isEmpty());
        Assert.assertEquals(1, eventsRead.size());
        Assert.assertEquals(events.get(10), eventsRead.get(0));
        eventsRead = store.getEvents(20, 1000, null, EMPTY_TRANSFORMER);
        Assert.assertNotNull(eventsRead);
        Assert.assertTrue(eventsRead.isEmpty());
    }

    @Test
    public void testGetSize() throws IOException {
        final PartitionedWriteAheadEventStore store = new PartitionedWriteAheadEventStore(createConfig(), TestPartitionedWriteAheadEventStore.writerFactory, TestPartitionedWriteAheadEventStore.readerFactory, EventReporter.NO_OP, new EventFileManager());
        store.initialize();
        long storeSize = 0L;
        final int numEvents = 20;
        for (int i = 0; i < numEvents; i++) {
            final ProvenanceEventRecord event = createEvent();
            store.addEvents(Collections.singleton(event));
            final long newSize = store.getSize();
            Assert.assertTrue((newSize > storeSize));
            storeSize = newSize;
        }
    }

    @Test
    public void testMaxEventIdRestored() throws IOException {
        final RepositoryConfiguration config = createConfig();
        final PartitionedWriteAheadEventStore store = new PartitionedWriteAheadEventStore(config, TestPartitionedWriteAheadEventStore.writerFactory, TestPartitionedWriteAheadEventStore.readerFactory, EventReporter.NO_OP, new EventFileManager());
        store.initialize();
        final int numEvents = 20;
        for (int i = 0; i < numEvents; i++) {
            final ProvenanceEventRecord event = createEvent();
            store.addEvents(Collections.singleton(event));
        }
        Assert.assertEquals(19, store.getMaxEventId());
        store.close();
        final PartitionedWriteAheadEventStore recoveredStore = new PartitionedWriteAheadEventStore(config, TestPartitionedWriteAheadEventStore.writerFactory, TestPartitionedWriteAheadEventStore.readerFactory, EventReporter.NO_OP, new EventFileManager());
        recoveredStore.initialize();
        Assert.assertEquals(19, recoveredStore.getMaxEventId());
    }

    @Test
    public void testGetEvent() throws IOException {
        final RepositoryConfiguration config = createConfig();
        final PartitionedWriteAheadEventStore store = new PartitionedWriteAheadEventStore(config, TestPartitionedWriteAheadEventStore.writerFactory, TestPartitionedWriteAheadEventStore.readerFactory, EventReporter.NO_OP, new EventFileManager());
        store.initialize();
        final int numEvents = 20;
        final List<ProvenanceEventRecord> events = new ArrayList<>(numEvents);
        for (int i = 0; i < numEvents; i++) {
            final ProvenanceEventRecord event = createEvent();
            store.addEvents(Collections.singleton(event));
            events.add(event);
        }
        // Ensure that each event is retrieved successfully.
        for (int i = 0; i < numEvents; i++) {
            final ProvenanceEventRecord event = store.getEvent(i).get();
            Assert.assertEquals(events.get(i), event);
        }
        Assert.assertFalse(store.getEvent((-1L)).isPresent());
        Assert.assertFalse(store.getEvent(20L).isPresent());
    }

    @Test
    public void testGetEventsWithMinIdAndCount() throws IOException {
        final RepositoryConfiguration config = createConfig();
        config.setMaxEventFileCount(100);
        final PartitionedWriteAheadEventStore store = new PartitionedWriteAheadEventStore(config, TestPartitionedWriteAheadEventStore.writerFactory, TestPartitionedWriteAheadEventStore.readerFactory, EventReporter.NO_OP, new EventFileManager());
        store.initialize();
        final int numEvents = 50000;
        final List<ProvenanceEventRecord> events = new ArrayList<>(numEvents);
        for (int i = 0; i < numEvents; i++) {
            final ProvenanceEventRecord event = createEvent();
            store.addEvents(Collections.singleton(event));
            if (i < 1000) {
                events.add(event);
            }
        }
        Assert.assertTrue(store.getEvents((-1000L), 1000).isEmpty());
        Assert.assertEquals(events, store.getEvents(0, events.size()));
        Assert.assertEquals(events, store.getEvents((-30), events.size()));
        Assert.assertEquals(events.subList(10, events.size()), store.getEvents(10L, ((events.size()) - 10)));
        Assert.assertTrue(store.getEvents(numEvents, 100).isEmpty());
    }

    @Test
    public void testGetEventsWithMinIdAndCountWithAuthorizer() throws IOException {
        final RepositoryConfiguration config = createConfig();
        final PartitionedWriteAheadEventStore store = new PartitionedWriteAheadEventStore(config, TestPartitionedWriteAheadEventStore.writerFactory, TestPartitionedWriteAheadEventStore.readerFactory, EventReporter.NO_OP, new EventFileManager());
        store.initialize();
        final int numEvents = 20;
        final List<ProvenanceEventRecord> events = new ArrayList<>(numEvents);
        for (int i = 0; i < numEvents; i++) {
            final ProvenanceEventRecord event = createEvent();
            store.addEvents(Collections.singleton(event));
            events.add(event);
        }
        final EventAuthorizer allowEventNumberedEventIds = new EventAuthorizer() {
            @Override
            public boolean isAuthorized(final ProvenanceEventRecord event) {
                return ((event.getEventId()) % 2) == 0L;
            }

            @Override
            public void authorize(ProvenanceEventRecord event) throws AccessDeniedException {
                if (!(isAuthorized(event))) {
                    throw new AccessDeniedException();
                }
            }
        };
        final List<ProvenanceEventRecord> storedEvents = store.getEvents(0, 20, allowEventNumberedEventIds, EMPTY_TRANSFORMER);
        Assert.assertEquals((numEvents / 2), storedEvents.size());
        for (int i = 0; i < (storedEvents.size()); i++) {
            Assert.assertEquals(events.get((i * 2)), storedEvents.get(i));
        }
    }

    @Test
    public void testGetEventsWithStartOffsetAndCountWithNothingAuthorized() throws IOException {
        final RepositoryConfiguration config = createConfig();
        final PartitionedWriteAheadEventStore store = new PartitionedWriteAheadEventStore(config, TestPartitionedWriteAheadEventStore.writerFactory, TestPartitionedWriteAheadEventStore.readerFactory, EventReporter.NO_OP, new EventFileManager());
        store.initialize();
        final int numEvents = 20;
        final List<ProvenanceEventRecord> events = new ArrayList<>(numEvents);
        for (int i = 0; i < numEvents; i++) {
            final ProvenanceEventRecord event = createEvent();
            store.addEvents(Collections.singleton(event));
            events.add(event);
        }
        final EventAuthorizer allowEventNumberedEventIds = EventAuthorizer.DENY_ALL;
        final List<ProvenanceEventRecord> storedEvents = store.getEvents(0, 20, allowEventNumberedEventIds, EMPTY_TRANSFORMER);
        Assert.assertTrue(storedEvents.isEmpty());
    }

    @Test
    public void testGetSpecificEventIds() throws IOException {
        final RepositoryConfiguration config = createConfig();
        final PartitionedWriteAheadEventStore store = new PartitionedWriteAheadEventStore(config, TestPartitionedWriteAheadEventStore.writerFactory, TestPartitionedWriteAheadEventStore.readerFactory, EventReporter.NO_OP, new EventFileManager());
        store.initialize();
        final int numEvents = 20;
        final List<ProvenanceEventRecord> events = new ArrayList<>(numEvents);
        for (int i = 0; i < numEvents; i++) {
            final ProvenanceEventRecord event = createEvent();
            store.addEvents(Collections.singleton(event));
            events.add(event);
        }
        final EventAuthorizer allowEvenNumberedEventIds = new EventAuthorizer() {
            @Override
            public boolean isAuthorized(final ProvenanceEventRecord event) {
                return ((event.getEventId()) % 2) == 0L;
            }

            @Override
            public void authorize(ProvenanceEventRecord event) throws AccessDeniedException {
                if (!(isAuthorized(event))) {
                    throw new AccessDeniedException();
                }
            }
        };
        final List<Long> evenEventIds = new ArrayList<>();
        final List<Long> oddEventIds = new ArrayList<>();
        final List<Long> allEventIds = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            final Long id = Long.valueOf(i);
            allEventIds.add(id);
            if ((i % 2) == 0) {
                evenEventIds.add(id);
            } else {
                oddEventIds.add(id);
            }
        }
        final List<ProvenanceEventRecord> storedEvents = store.getEvents(evenEventIds, allowEvenNumberedEventIds, EMPTY_TRANSFORMER);
        Assert.assertEquals((numEvents / 2), storedEvents.size());
        for (int i = 0; i < (storedEvents.size()); i++) {
            Assert.assertEquals(events.get((i * 2)), storedEvents.get(i));
        }
        Assert.assertTrue(store.getEvents(oddEventIds, allowEvenNumberedEventIds, EMPTY_TRANSFORMER).isEmpty());
        final List<ProvenanceEventRecord> allStoredEvents = store.getEvents(allEventIds, GRANT_ALL, EMPTY_TRANSFORMER);
        Assert.assertEquals(events, allStoredEvents);
    }

    @Test
    public void testWriteAfterRecoveringRepo() throws IOException {
        final RepositoryConfiguration config = createConfig();
        final PartitionedWriteAheadEventStore store = new PartitionedWriteAheadEventStore(config, TestPartitionedWriteAheadEventStore.writerFactory, TestPartitionedWriteAheadEventStore.readerFactory, EventReporter.NO_OP, new EventFileManager());
        store.initialize();
        for (int i = 0; i < 4; i++) {
            store.addEvents(Collections.singleton(createEvent()));
        }
        store.close();
        final PartitionedWriteAheadEventStore recoveredStore = new PartitionedWriteAheadEventStore(config, TestPartitionedWriteAheadEventStore.writerFactory, TestPartitionedWriteAheadEventStore.readerFactory, EventReporter.NO_OP, new EventFileManager());
        recoveredStore.initialize();
        List<ProvenanceEventRecord> recoveredEvents = recoveredStore.getEvents(0, 10);
        Assert.assertEquals(4, recoveredEvents.size());
        // ensure that we can still write to the store
        for (int i = 0; i < 4; i++) {
            recoveredStore.addEvents(Collections.singleton(createEvent()));
        }
        recoveredEvents = recoveredStore.getEvents(0, 10);
        Assert.assertEquals(8, recoveredEvents.size());
        for (int i = 0; i < 8; i++) {
            Assert.assertEquals(i, recoveredEvents.get(i).getEventId());
        }
    }
}

