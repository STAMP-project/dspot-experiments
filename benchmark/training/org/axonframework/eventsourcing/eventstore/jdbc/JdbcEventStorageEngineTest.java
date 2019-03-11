/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.eventsourcing.eventstore.jdbc;


import NoOpEventUpcaster.INSTANCE;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.LongPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import junit.framework.TestCase;
import org.axonframework.common.jdbc.PersistenceExceptionResolver;
import org.axonframework.eventhandling.DomainEventData;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.GapAwareTrackingToken;
import org.axonframework.eventhandling.GenericEventMessage;
import org.axonframework.eventhandling.TrackedEventData;
import org.axonframework.eventhandling.TrackedEventMessage;
import org.axonframework.eventhandling.TrackingEventStream;
import org.axonframework.eventsourcing.eventstore.BatchingEventStorageEngineTest;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.utils.EventStoreTestUtils;
import org.axonframework.serialization.UnknownSerializedType;
import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;


/**
 *
 *
 * @author Rene de Waele
 */
public class JdbcEventStorageEngineTest extends BatchingEventStorageEngineTest {
    private JDBCDataSource dataSource;

    private PersistenceExceptionResolver defaultPersistenceExceptionResolver;

    private JdbcEventStorageEngine testSubject;

    @Test
    public void testStoreTwoExactSameSnapshots() {
        testSubject.storeSnapshot(EventStoreTestUtils.createEvent(1));
        testSubject.storeSnapshot(EventStoreTestUtils.createEvent(1));
    }

    @Test
    public void testLoadLastSequenceNumber() {
        String aggregateId = UUID.randomUUID().toString();
        testSubject.appendEvents(EventStoreTestUtils.createEvent(aggregateId, 0), EventStoreTestUtils.createEvent(aggregateId, 1));
        TestCase.assertEquals(1L, ((long) (testSubject.lastSequenceNumberFor(aggregateId).orElse((-1L)))));
        Assert.assertFalse(testSubject.lastSequenceNumberFor("inexistent").isPresent());
    }

    @Test
    @SuppressWarnings({ "JpaQlInspection", "OptionalGetWithoutIsPresent" })
    @DirtiesContext
    public void testCustomSchemaConfig() {
        setTestSubject((testSubject = createEngine(INSTANCE, defaultPersistenceExceptionResolver, EventSchema.builder().eventTable("CustomDomainEvent").payloadColumn("eventData").build(), String.class, new HsqlEventTableFactory() {
            @Override
            protected String payloadType() {
                return "LONGVARCHAR";
            }
        })));
        testStoreAndLoadEvents();
    }

    @Test
    public void testGapsForVeryOldEventsAreNotIncluded() throws SQLException {
        GenericEventMessage.clock = Clock.fixed(Clock.systemUTC().instant().minus(1, ChronoUnit.HOURS), Clock.systemUTC().getZone());
        testSubject.appendEvents(EventStoreTestUtils.createEvent((-1)), EventStoreTestUtils.createEvent(0));
        GenericEventMessage.clock = Clock.fixed(Clock.systemUTC().instant().minus(2, ChronoUnit.MINUTES), Clock.systemUTC().getZone());
        testSubject.appendEvents(EventStoreTestUtils.createEvent((-2)), EventStoreTestUtils.createEvent(1));
        GenericEventMessage.clock = Clock.fixed(Clock.systemUTC().instant().minus(50, ChronoUnit.SECONDS), Clock.systemUTC().getZone());
        testSubject.appendEvents(EventStoreTestUtils.createEvent((-3)), EventStoreTestUtils.createEvent(2));
        GenericEventMessage.clock = Clock.fixed(Clock.systemUTC().instant(), Clock.systemUTC().getZone());
        testSubject.appendEvents(EventStoreTestUtils.createEvent((-4)), EventStoreTestUtils.createEvent(3));
        try (Connection conn = dataSource.getConnection()) {
            conn.prepareStatement("DELETE FROM DomainEventEntry WHERE sequenceNumber < 0").executeUpdate();
        }
        testSubject.fetchTrackedEvents(null, 100).stream().map(( i) -> ((GapAwareTrackingToken) (i.trackingToken()))).forEach(( i) -> assertTrue(((i.getGaps().size()) <= 2)));
    }

    @DirtiesContext
    @Test
    public void testOldGapsAreRemovedFromProvidedTrackingToken() throws SQLException {
        testSubject.setGapTimeout(50001);
        testSubject.setGapCleaningThreshold(50);
        Instant now = Clock.systemUTC().instant();
        GenericEventMessage.clock = Clock.fixed(now.minus(1, ChronoUnit.HOURS), Clock.systemUTC().getZone());
        testSubject.appendEvents(EventStoreTestUtils.createEvent((-1)), EventStoreTestUtils.createEvent(0));// index 0 and 1

        GenericEventMessage.clock = Clock.fixed(now.minus(2, ChronoUnit.MINUTES), Clock.systemUTC().getZone());
        testSubject.appendEvents(EventStoreTestUtils.createEvent((-2)), EventStoreTestUtils.createEvent(1));// index 2 and 3

        GenericEventMessage.clock = Clock.fixed(now.minus(50, ChronoUnit.SECONDS), Clock.systemUTC().getZone());
        testSubject.appendEvents(EventStoreTestUtils.createEvent((-3)), EventStoreTestUtils.createEvent(2));// index 4 and 5

        GenericEventMessage.clock = Clock.fixed(now, Clock.systemUTC().getZone());
        testSubject.appendEvents(EventStoreTestUtils.createEvent((-4)), EventStoreTestUtils.createEvent(3));// index 6 and 7

        try (Connection conn = dataSource.getConnection()) {
            conn.prepareStatement("DELETE FROM DomainEventEntry WHERE sequenceNumber < 0").executeUpdate();
        }
        List<Long> gaps = LongStream.range((-50), 6).filter(( i) -> ((i != 1L) && (i != 3L)) && (i != 5)).boxed().collect(Collectors.toList());
        List<? extends TrackedEventData<?>> events = testSubject.fetchTrackedEvents(GapAwareTrackingToken.newInstance(6, gaps), 100);
        TestCase.assertEquals(1, events.size());
        TestCase.assertEquals(4L, ((long) (getGaps().first())));
    }

    @Test
    public void testEventsWithUnknownPayloadTypeDoNotResultInError() throws InterruptedException, SQLException {
        String expectedPayloadOne = "Payload3";
        String expectedPayloadTwo = "Payload4";
        int testBatchSize = 2;
        testSubject = createEngine(defaultPersistenceExceptionResolver, new EventSchema(), testBatchSize);
        EmbeddedEventStore testEventStore = EmbeddedEventStore.builder().storageEngine(testSubject).build();
        testSubject.appendEvents(EventStoreTestUtils.createEvent(EventStoreTestUtils.AGGREGATE, 1, "Payload1"), EventStoreTestUtils.createEvent(EventStoreTestUtils.AGGREGATE, 2, "Payload2"));
        // Update events which will be part of the first batch to an unknown payload type
        try (Connection conn = dataSource.getConnection()) {
            conn.prepareStatement("UPDATE DomainEventEntry e SET e.payloadType = 'unknown'").executeUpdate();
        }
        testSubject.appendEvents(EventStoreTestUtils.createEvent(EventStoreTestUtils.AGGREGATE, 3, expectedPayloadOne), EventStoreTestUtils.createEvent(EventStoreTestUtils.AGGREGATE, 4, expectedPayloadTwo));
        List<String> eventStorageEngineResult = testSubject.readEvents(null, false).filter(( m) -> (m.getPayload()) instanceof String).map(( m) -> ((String) (m.getPayload()))).collect(Collectors.toList());
        TestCase.assertEquals(Arrays.asList(expectedPayloadOne, expectedPayloadTwo), eventStorageEngineResult);
        TrackingEventStream eventStoreResult = testEventStore.openStream(null);
        Assert.assertTrue(eventStoreResult.hasNextAvailable());
        TestCase.assertEquals(UnknownSerializedType.class, eventStoreResult.nextAvailable().getPayloadType());
        TestCase.assertEquals(UnknownSerializedType.class, eventStoreResult.nextAvailable().getPayloadType());
        TestCase.assertEquals(expectedPayloadOne, eventStoreResult.nextAvailable().getPayload());
        TestCase.assertEquals(expectedPayloadTwo, eventStoreResult.nextAvailable().getPayload());
    }

    @Test
    public void testStreamCrossesConsecutiveGapsOfMoreThanBatchSuccessfully() throws SQLException {
        testSubject = createEngine(defaultPersistenceExceptionResolver, new EventSchema(), 10);
        testSubject.appendEvents(EventStoreTestUtils.createEvents(100));
        try (Connection conn = dataSource.getConnection()) {
            conn.prepareStatement("DELETE FROM DomainEventEntry WHERE globalIndex >= 20 and globalIndex < 40").executeUpdate();
        }
        Stream<? extends TrackedEventMessage<?>> actual = testSubject.readEvents(null, false);
        List<? extends TrackedEventMessage<?>> actualEvents = actual.collect(Collectors.toList());
        TestCase.assertEquals(80, actualEvents.size());
    }

    @Test
    public void testStreamDoesNotCrossExtendedGapWhenDisabled() throws SQLException {
        testSubject = JdbcEventStorageEngine.builder().upcasterChain(INSTANCE).batchSize(10).connectionProvider(dataSource::getConnection).transactionManager(NoTransactionManager.INSTANCE).schema(new EventSchema()).dataType(byte[].class).extendedGapCheckEnabled(false).build();
        try {
            Connection connection = dataSource.getConnection();
            connection.prepareStatement("DROP TABLE IF EXISTS DomainEventEntry").executeUpdate();
            connection.prepareStatement("DROP TABLE IF EXISTS SnapshotEventEntry").executeUpdate();
            testSubject.createSchema(HsqlEventTableFactory.INSTANCE);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        testSubject.appendEvents(EventStoreTestUtils.createEvents(100));
        try (Connection conn = dataSource.getConnection()) {
            conn.prepareStatement("DELETE FROM DomainEventEntry WHERE globalIndex >= 20 and globalIndex < 40").executeUpdate();
        }
        Stream<? extends TrackedEventMessage<?>> actual = testSubject.readEvents(null, false);
        List<? extends TrackedEventMessage<?>> actualEvents = actual.collect(Collectors.toList());
        TestCase.assertEquals(20, actualEvents.size());
    }

    @Test
    public void testStreamCrossesInitialConsecutiveGapsOfMoreThanBatchSuccessfully() throws SQLException {
        testSubject = createEngine(defaultPersistenceExceptionResolver, new EventSchema(), 10);
        testSubject.appendEvents(EventStoreTestUtils.createEvents(100));
        try (Connection conn = dataSource.getConnection()) {
            conn.prepareStatement("DELETE FROM DomainEventEntry WHERE globalIndex < 20").executeUpdate();
        }
        Stream<? extends TrackedEventMessage<?>> actual = testSubject.readEvents(null, false);
        List<? extends TrackedEventMessage<?>> actualEvents = actual.collect(Collectors.toList());
        TestCase.assertEquals(80, actualEvents.size());
    }

    @Test
    public void testLoadSnapshotIfMatchesPredicate() {
        Predicate<DomainEventData<?>> acceptAll = ( i) -> true;
        setTestSubject((testSubject = createEngine(acceptAll)));
        testSubject.storeSnapshot(EventStoreTestUtils.createEvent(1));
        Assert.assertTrue(testSubject.readSnapshot(EventStoreTestUtils.AGGREGATE).isPresent());
    }

    @Test
    public void testDoNotLoadSnapshotIfNotMatchingPredicate() {
        Predicate<DomainEventData<?>> rejectAll = ( i) -> false;
        setTestSubject((testSubject = createEngine(rejectAll)));
        testSubject.storeSnapshot(EventStoreTestUtils.createEvent(1));
        Assert.assertFalse(testSubject.readSnapshot(EventStoreTestUtils.AGGREGATE).isPresent());
    }

    @Test
    public void testReadEventsForAggregateReturnsTheCompleteStream() {
        testSubject = createEngine(defaultPersistenceExceptionResolver, new EventSchema(), 10);
        DomainEventMessage<String> testEventOne = EventStoreTestUtils.createEvent(0);
        DomainEventMessage<String> testEventTwo = EventStoreTestUtils.createEvent(1);
        DomainEventMessage<String> testEventThree = EventStoreTestUtils.createEvent(2);
        DomainEventMessage<String> testEventFour = EventStoreTestUtils.createEvent(3);
        DomainEventMessage<String> testEventFive = EventStoreTestUtils.createEvent(4);
        testSubject.appendEvents(testEventOne, testEventTwo, testEventThree, testEventFour, testEventFive);
        List<? extends DomainEventMessage<?>> result = testSubject.readEvents(EventStoreTestUtils.AGGREGATE, 0L).asStream().collect(Collectors.toList());
        TestCase.assertEquals(5, result.size());
        TestCase.assertEquals(0, getSequenceNumber());
        TestCase.assertEquals(1, getSequenceNumber());
        TestCase.assertEquals(2, getSequenceNumber());
        TestCase.assertEquals(3, getSequenceNumber());
        TestCase.assertEquals(4, getSequenceNumber());
    }

    @Test
    public void testReadEventsForAggregateWithGapsReturnsTheCompleteStream() {
        testSubject = createEngine(defaultPersistenceExceptionResolver, new EventSchema(), 10);
        DomainEventMessage<String> testEventOne = EventStoreTestUtils.createEvent(0);
        DomainEventMessage<String> testEventTwo = EventStoreTestUtils.createEvent(1);
        // Event with sequence number 2 is missing -> the gap
        DomainEventMessage<String> testEventFour = EventStoreTestUtils.createEvent(3);
        DomainEventMessage<String> testEventFive = EventStoreTestUtils.createEvent(4);
        testSubject.appendEvents(testEventOne, testEventTwo, testEventFour, testEventFive);
        List<? extends DomainEventMessage<?>> result = testSubject.readEvents(EventStoreTestUtils.AGGREGATE, 0L).asStream().collect(Collectors.toList());
        TestCase.assertEquals(4, result.size());
        TestCase.assertEquals(0, getSequenceNumber());
        TestCase.assertEquals(1, getSequenceNumber());
        TestCase.assertEquals(3, getSequenceNumber());
        TestCase.assertEquals(4, getSequenceNumber());
    }

    @Test
    public void testReadEventsForAggregateWithEventsExceedingOneBatchReturnsTheCompleteStream() {
        // Set batch size to 5, so that the number of events exceeds at least one batch
        int batchSize = 5;
        testSubject = createEngine(defaultPersistenceExceptionResolver, new EventSchema(), batchSize);
        DomainEventMessage<String> testEventOne = EventStoreTestUtils.createEvent(0);
        DomainEventMessage<String> testEventTwo = EventStoreTestUtils.createEvent(1);
        DomainEventMessage<String> testEventThree = EventStoreTestUtils.createEvent(2);
        DomainEventMessage<String> testEventFour = EventStoreTestUtils.createEvent(3);
        DomainEventMessage<String> testEventFive = EventStoreTestUtils.createEvent(4);
        DomainEventMessage<String> testEventSix = EventStoreTestUtils.createEvent(5);
        DomainEventMessage<String> testEventSeven = EventStoreTestUtils.createEvent(6);
        DomainEventMessage<String> testEventEight = EventStoreTestUtils.createEvent(7);
        testSubject.appendEvents(testEventOne, testEventTwo, testEventThree, testEventFour, testEventFive, testEventSix, testEventSeven, testEventEight);
        List<? extends DomainEventMessage<?>> result = testSubject.readEvents(EventStoreTestUtils.AGGREGATE, 0L).asStream().collect(Collectors.toList());
        TestCase.assertEquals(8, result.size());
        TestCase.assertEquals(0, getSequenceNumber());
        TestCase.assertEquals(1, getSequenceNumber());
        TestCase.assertEquals(2, getSequenceNumber());
        TestCase.assertEquals(3, getSequenceNumber());
        TestCase.assertEquals(4, getSequenceNumber());
        TestCase.assertEquals(5, getSequenceNumber());
        TestCase.assertEquals(6, getSequenceNumber());
        TestCase.assertEquals(7, getSequenceNumber());
    }

    @Test
    public void testReadEventsForAggregateWithEventsExceedingOneBatchAndGapsReturnsTheCompleteStream() {
        // Set batch size to 5, so that the number of events exceeds at least one batch
        int batchSize = 5;
        testSubject = createEngine(defaultPersistenceExceptionResolver, new EventSchema(), batchSize);
        DomainEventMessage<String> testEventOne = EventStoreTestUtils.createEvent(0);
        DomainEventMessage<String> testEventTwo = EventStoreTestUtils.createEvent(1);
        // Event with sequence number 2 is missing -> the gap
        DomainEventMessage<String> testEventFour = EventStoreTestUtils.createEvent(3);
        DomainEventMessage<String> testEventFive = EventStoreTestUtils.createEvent(4);
        DomainEventMessage<String> testEventSix = EventStoreTestUtils.createEvent(5);
        DomainEventMessage<String> testEventSeven = EventStoreTestUtils.createEvent(6);
        DomainEventMessage<String> testEventEight = EventStoreTestUtils.createEvent(7);
        testSubject.appendEvents(testEventOne, testEventTwo, testEventFour, testEventFive, testEventSix, testEventSeven, testEventEight);
        List<? extends DomainEventMessage<?>> result = testSubject.readEvents(EventStoreTestUtils.AGGREGATE, 0L).asStream().collect(Collectors.toList());
        TestCase.assertEquals(7, result.size());
        TestCase.assertEquals(0, getSequenceNumber());
        TestCase.assertEquals(1, getSequenceNumber());
        TestCase.assertEquals(3, getSequenceNumber());
        TestCase.assertEquals(4, getSequenceNumber());
        TestCase.assertEquals(5, getSequenceNumber());
        TestCase.assertEquals(6, getSequenceNumber());
        TestCase.assertEquals(7, getSequenceNumber());
    }
}

