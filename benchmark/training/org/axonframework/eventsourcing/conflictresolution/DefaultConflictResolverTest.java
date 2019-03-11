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
package org.axonframework.eventsourcing.conflictresolution;


import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.utils.EventStoreTestUtils;
import org.axonframework.modelling.command.ConflictingModificationException;
import org.junit.Assert;
import org.junit.Test;


public class DefaultConflictResolverTest {
    private EventStore eventStore;

    private DefaultConflictResolver subject;

    @Test(expected = ConflictingModificationException.class)
    public void testDetectConflicts() {
        subject = new DefaultConflictResolver(eventStore, EventStoreTestUtils.AGGREGATE, 5, 9);
        subject.detectConflicts(Conflicts.payloadTypeOf(String.class));
    }

    @Test
    public void testDetectNoConflictsWhenPredicateDoesNotMatch() {
        subject = new DefaultConflictResolver(eventStore, EventStoreTestUtils.AGGREGATE, 5, 9);
        subject.detectConflicts(Conflicts.payloadTypeOf(Long.class));
    }

    @Test
    public void testDetectNoConflictsWithoutUnseenEvents() {
        subject = new DefaultConflictResolver(eventStore, EventStoreTestUtils.AGGREGATE, 5, 5);
        subject.detectConflicts(Conflicts.payloadTypeOf(String.class));
    }

    @Test(expected = ConflictingModificationException.class)
    public void testEnsureConflictsResolvedThrowsExceptionWithoutRegisteredConflicts() {
        subject = new DefaultConflictResolver(eventStore, EventStoreTestUtils.AGGREGATE, 5, 9);
        subject.ensureConflictsResolved();
    }

    @Test
    public void testEnsureConflictsResolvedDoesNothingWithRegisteredConflicts() {
        subject = new DefaultConflictResolver(eventStore, EventStoreTestUtils.AGGREGATE, 5, 9);
        subject.detectConflicts(Conflicts.payloadMatching(Long.class::isInstance));
        subject.ensureConflictsResolved();
    }

    @Test
    public void testConflictingEventsAreAvailableInExceptionBuilder() {
        subject = new DefaultConflictResolver(eventStore, EventStoreTestUtils.AGGREGATE, 5, 9);
        try {
            subject.detectConflicts(Conflicts.payloadTypeOf(String.class), ( c) -> new ConflictingModificationException(("" + (c.unexpectedEvents().size()))));
            Assert.fail("Expected exception");
        } catch (ConflictingModificationException e) {
            Assert.assertEquals("4", e.getMessage());
        }
    }

    @Test
    public void testConflictResolverProvidingNullExceptionIgnoresConflict() {
        subject = new DefaultConflictResolver(eventStore, EventStoreTestUtils.AGGREGATE, 5, 9);
        subject.detectConflicts(Conflicts.payloadTypeOf(String.class), ( c) -> null);
    }
}

