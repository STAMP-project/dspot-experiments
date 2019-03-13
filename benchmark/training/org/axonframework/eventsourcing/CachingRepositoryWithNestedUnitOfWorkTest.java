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
package org.axonframework.eventsourcing;


import NoCache.INSTANCE;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.axonframework.common.caching.Cache;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.EventMessageHandler;
import org.axonframework.eventhandling.GenericDomainEventMessage;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.unitofwork.DefaultUnitOfWork;
import org.axonframework.messaging.unitofwork.UnitOfWork;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.junit.Test;


/**
 * Minimal test cases triggering an issue with the NestedUnitOfWork and the CachingEventSourcingRepository, see <a
 * href="http://issues.axonframework.org/youtrack/issue/AXON-180">AXON-180</a>.
 * <p/>
 * The caching event sourcing repository loads aggregates from a cache first, from the event store second. It places
 * aggregates into the cache via a postCommit listener registered with the unit of work.
 * <p/>
 * Committing a unit of work may result in additional units of work being created -- typically as part of the 'publish
 * event' phase: <ol> <li>A Command is dispatched. <li>A UOW is created, started. <li>The command completes.
 * Aggregate(s) have been loaded and events have been applied. <li>The UOW is comitted: <ol> <li>The aggregate is
 * saved.
 * <li>Events are published on the event bus. <li>Inner UOWs are comitted. <li>AfterCommit listeners are notified.
 * </ol>
 * </ol>
 * <p/>
 * When the events are published, an @EventHandler may dispatch an additional command, which creates its own UOW
 * following above cycle.
 * <p/>
 * When UnitsOfWork are nested, it's possible to create a situation where one UnitOfWork completes the "innerCommit"
 * (and notifies afterCommit listeners) while subsequent units of work have yet to be created.
 * <p/>
 * That means that the CachingEventSourcingRepository's afterCommit listener will place the aggregate (from this UOW)
 * into the cache, exposing it to subsequent UOWs. The state of the aggregate in any UOW is not guaranteed to be
 * up-to-date. Depending on how UOWs are nested, it may be 'behind' by several events;
 * <p/>
 * Any subsequent UOW (after an aggregate was added to the cache) works on potentially stale data. This manifests
 * itself
 * primarily by events being assigned duplicate sequence numbers. The {@link org.axonframework.eventsourcing.eventstore.jpa.JpaEventStorageEngine}
 * detects this and throws an
 * exception noting that an 'identical' entity has already been persisted.
 * <p/>
 * <p/>
 * <h2>Possible solutions and workarounds contemplated include:</h2>
 * <p/>
 * <ul> <li>Workaround: Disable Caching. Aggregates are always loaded from the event store for each UOW. <li>Defer
 * 'afterCommit' listener notification until the parent UOW completes. <li>Prevent nesting of UOWs more than one level
 * -- Attach all new UOWs to the 'root' UOW and let it manage event publication while watching for newly created UOWs.
 * <li>Place Aggregates in the cache immediately. Improves performance by avoiding multiple loads of an aggregate (once
 * per UOW) and ensures that UOWs work on the same, up-to-date instance of the aggregate. Not desirable with a
 * distributed cache w/o global locking. <li>Maintain a 'Session' of aggregates used in a UOW -- similar to adding
 * aggregates to the cache immediately, but explicitly limiting the scope/visibility to a UOW (or group of UOWs).
 * Similar idea to a JPA/Hibernate session: provide quick and canonical access to aggregates already touched somewhere
 * in this UOW. </ul>
 *
 * @author patrickh
 */
public class CachingRepositoryWithNestedUnitOfWorkTest {
    private final List<String> events = new ArrayList<>();

    private CachingEventSourcingRepository<CachingRepositoryWithNestedUnitOfWorkTest.TestAggregate> repository;

    private Cache realCache;

    private AggregateFactory<CachingRepositoryWithNestedUnitOfWorkTest.TestAggregate> aggregateFactory;

    private EventStore eventStore;

    @Test
    public void testWithoutCache() throws Exception {
        repository = CachingEventSourcingRepository.builder(CachingRepositoryWithNestedUnitOfWorkTest.TestAggregate.class).aggregateFactory(aggregateFactory).eventStore(eventStore).cache(INSTANCE).build();
        executeComplexScenario("ComplexWithoutCache");
    }

    @Test
    public void testWithCache() throws Exception {
        repository = CachingEventSourcingRepository.builder(CachingRepositoryWithNestedUnitOfWorkTest.TestAggregate.class).aggregateFactory(aggregateFactory).eventStore(eventStore).cache(realCache).build();
        executeComplexScenario("ComplexWithCache");
    }

    @Test
    public void testMinimalScenarioWithoutCache() throws Exception {
        repository = CachingEventSourcingRepository.builder(CachingRepositoryWithNestedUnitOfWorkTest.TestAggregate.class).aggregateFactory(aggregateFactory).eventStore(eventStore).cache(INSTANCE).build();
        testMinimalScenario("MinimalScenarioWithoutCache");
    }

    @Test
    public void testMinimalScenarioWithCache() throws Exception {
        repository = CachingEventSourcingRepository.builder(CachingRepositoryWithNestedUnitOfWorkTest.TestAggregate.class).aggregateFactory(aggregateFactory).eventStore(eventStore).cache(realCache).build();
        testMinimalScenario("MinimalScenarioWithCache");
    }

    /**
     * Capture information about events that are published
     */
    private static final class LoggingEventHandler implements EventMessageHandler {
        private final List<String> events;

        private LoggingEventHandler(List<String> events) {
            this.events = events;
        }

        @SuppressWarnings("rawtypes")
        @Override
        public Object handle(EventMessage event) {
            GenericDomainEventMessage e = ((GenericDomainEventMessage) (event));
            String str = // 
            // 
            // 
            // 
            // 
            String.format("%d - %s(%s) ID %s %s", e.getSequenceNumber(), e.getPayloadType().getSimpleName(), e.getAggregateIdentifier(), e.getIdentifier(), e.getPayload());
            events.add(str);
            return null;
        }
    }

    /* Domain Model */
    public static class AggregateCreatedEvent implements Serializable {
        @AggregateIdentifier
        private final String id;

        public AggregateCreatedEvent(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return ((((getClass().getSimpleName()) + "@") + (Integer.toHexString(hashCode()))) + ": ") + (id);
        }
    }

    public static class AggregateUpdatedEvent implements Serializable {
        @AggregateIdentifier
        private final String id;

        private final String token;

        public AggregateUpdatedEvent(String id, String token) {
            this.id = id;
            this.token = token;
        }

        @Override
        public String toString() {
            return ((((((getClass().getSimpleName()) + "@") + (Integer.toHexString(hashCode()))) + ": ") + (id)) + "/") + (token);
        }
    }

    public static class TestAggregate implements Serializable {
        @AggregateIdentifier
        public String id;

        public Set<String> tokens = new HashSet<>();

        @SuppressWarnings("unused")
        private TestAggregate() {
        }

        public TestAggregate(String id) {
            apply(new CachingRepositoryWithNestedUnitOfWorkTest.AggregateCreatedEvent(id));
        }

        public void update(String token) {
            apply(new CachingRepositoryWithNestedUnitOfWorkTest.AggregateUpdatedEvent(id, token));
        }

        @EventSourcingHandler
        private void created(CachingRepositoryWithNestedUnitOfWorkTest.AggregateCreatedEvent event) {
            this.id = event.id;
        }

        @EventSourcingHandler
        private void updated(CachingRepositoryWithNestedUnitOfWorkTest.AggregateUpdatedEvent event) {
            tokens.add(event.token);
        }
    }

    /**
     * Simulate event on bus -> command handler -> subsequent command (w/ unit of work)
     */
    private final class CommandExecutingEventHandler implements EventMessageHandler {
        private final String token;

        private final String previousToken;

        private final boolean commit;

        private CommandExecutingEventHandler(String token, String previousToken, boolean commit) {
            this.token = token;
            this.previousToken = previousToken;
            this.commit = commit;
        }

        @Override
        public Object handle(EventMessage<?> event) {
            Object payload = event.getPayload();
            if (((previousToken) == null) && (payload instanceof CachingRepositoryWithNestedUnitOfWorkTest.AggregateCreatedEvent)) {
                CachingRepositoryWithNestedUnitOfWorkTest.AggregateCreatedEvent created = ((CachingRepositoryWithNestedUnitOfWorkTest.AggregateCreatedEvent) (payload));
                UnitOfWork<EventMessage<?>> nested = DefaultUnitOfWork.startAndGet(event);
                nested.execute(() -> {
                    Aggregate<org.axonframework.eventsourcing.TestAggregate> aggregate = org.axonframework.eventsourcing.repository.load(created.id);
                    aggregate.execute(( r) -> r.update(token));
                });
            }
            if (((previousToken) != null) && (payload instanceof CachingRepositoryWithNestedUnitOfWorkTest.AggregateUpdatedEvent)) {
                CachingRepositoryWithNestedUnitOfWorkTest.AggregateUpdatedEvent updated = ((CachingRepositoryWithNestedUnitOfWorkTest.AggregateUpdatedEvent) (payload));
                if (updated.token.equals(previousToken)) {
                    UnitOfWork<EventMessage<?>> nested = DefaultUnitOfWork.startAndGet(event);
                    if (commit) {
                        nested.execute(() -> {
                            Aggregate<org.axonframework.eventsourcing.TestAggregate> aggregate = org.axonframework.eventsourcing.repository.load(updated.id);
                            aggregate.execute(( r) -> r.update(token));
                        });
                    } else {
                        try {
                            org.axonframework.modelling.command.Aggregate<CachingRepositoryWithNestedUnitOfWorkTest.TestAggregate> aggregate = repository.load(updated.id);
                            aggregate.execute(( r) -> r.update(token));
                        } finally {
                            nested.rollback();
                        }
                    }
                }
            }
            return null;
        }
    }
}

