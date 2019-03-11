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
package org.axonframework.test.aggregate;


import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Allard Buijze
 */
public class FixtureTest_StateStorage {
    private FixtureConfiguration<FixtureTest_StateStorage.StateStoredAggregate> fixture;

    @Test
    public void testCreateStateStoredAggregate() {
        fixture.givenState(() -> new org.axonframework.test.aggregate.StateStoredAggregate("id", "message")).when(new FixtureTest_StateStorage.SetMessageCommand("id", "message2")).expectEvents(new FixtureTest_StateStorage.StubDomainEvent()).expectState(( aggregate) -> assertEquals("message2", aggregate.getMessage()));
    }

    @Test
    public void testEmittedEventsFromExpectStateAreNotStored() {
        fixture.givenState(() -> new org.axonframework.test.aggregate.StateStoredAggregate("id", "message")).when(new FixtureTest_StateStorage.SetMessageCommand("id", "message2")).expectEvents(new FixtureTest_StateStorage.StubDomainEvent()).expectState(( aggregate) -> {
            apply(new org.axonframework.test.aggregate.StubDomainEvent());
            assertEquals("message2", aggregate.getMessage());
        }).expectEvents(new FixtureTest_StateStorage.StubDomainEvent()).expectState(Assert::assertNotNull);
    }

    @Test
    public void testCreateStateStoredAggregate_ErrorInChanges() {
        ResultValidator<FixtureTest_StateStorage.StateStoredAggregate> result = fixture.givenState(() -> new org.axonframework.test.aggregate.StateStoredAggregate("id", "message")).when(new FixtureTest_StateStorage.ErrorCommand("id", "message2")).expectException(CoreMatchers.any(Exception.class)).expectNoEvents();
        try {
            result.expectState(( aggregate) -> assertEquals("message2", aggregate.getMessage()));
            Assert.fail("Expected an exception");
        } catch (IllegalStateException e) {
            Assert.assertTrue(("Wrong message: " + (e.getMessage())), e.getMessage().contains("Unit of Work"));
            Assert.assertTrue(("Wrong message: " + (e.getMessage())), e.getMessage().contains("rolled back"));
        }
    }

    private static class InitializeCommand {
        private final String id;

        private final String message;

        private InitializeCommand(String id, String message) {
            this.id = id;
            this.message = message;
        }

        public String getId() {
            return id;
        }

        public String getMessage() {
            return message;
        }
    }

    private static class SetMessageCommand {
        @TargetAggregateIdentifier
        private final String id;

        private final String message;

        private SetMessageCommand(String id, String message) {
            this.id = id;
            this.message = message;
        }

        public String getId() {
            return id;
        }

        public String getMessage() {
            return message;
        }
    }

    private static class ErrorCommand {
        @TargetAggregateIdentifier
        private final String id;

        private final String message;

        private ErrorCommand(String id, String message) {
            this.id = id;
            this.message = message;
        }

        public String getId() {
            return id;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class StateStoredAggregate {
        @AggregateIdentifier
        private String id;

        private String message;

        public StateStoredAggregate(String id, String message) {
            this.id = id;
            this.message = message;
        }

        @CommandHandler
        public StateStoredAggregate(FixtureTest_StateStorage.InitializeCommand cmd) {
            this.id = cmd.getId();
            apply(new FixtureTest_StateStorage.StubDomainEvent());
        }

        @CommandHandler
        public void handle(FixtureTest_StateStorage.SetMessageCommand cmd) {
            this.message = cmd.getMessage();
            apply(new FixtureTest_StateStorage.StubDomainEvent());
        }

        @CommandHandler
        public void handle(FixtureTest_StateStorage.ErrorCommand cmd) {
            this.message = cmd.getMessage();
            apply(new FixtureTest_StateStorage.StubDomainEvent());
            throw new RuntimeException("Stub");
        }

        public String getMessage() {
            return message;
        }
    }

    private static class StubDomainEvent {
        public StubDomainEvent() {
        }
    }
}

