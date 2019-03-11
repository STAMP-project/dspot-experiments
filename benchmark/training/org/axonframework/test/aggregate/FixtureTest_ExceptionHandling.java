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
import org.axonframework.commandhandling.NoHandlerForCommandException;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventsourcing.eventstore.EventStoreException;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.axonframework.test.FixtureExecutionException;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Test;


public class FixtureTest_ExceptionHandling {
    private final FixtureConfiguration<FixtureTest_ExceptionHandling.MyAggregate> fixture = new AggregateTestFixture(FixtureTest_ExceptionHandling.MyAggregate.class);

    @Test
    public void testCreateAggregate() {
        fixture.givenCommands().when(new FixtureTest_ExceptionHandling.CreateMyAggregateCommand("14")).expectEvents(new FixtureTest_ExceptionHandling.MyAggregateCreatedEvent("14"));
    }

    @Test
    public void givenUnknownCommand() {
        try {
            fixture.givenCommands(new FixtureTest_ExceptionHandling.CreateMyAggregateCommand("14"), new FixtureTest_ExceptionHandling.UnknownCommand("14"));
            Assert.fail("Expected FixtureExecutionException");
        } catch (FixtureExecutionException fee) {
            Assert.assertEquals(NoHandlerForCommandException.class, fee.getCause().getClass());
        }
    }

    @Test
    public void testWhenExceptionTriggeringCommand() {
        fixture.givenCommands(new FixtureTest_ExceptionHandling.CreateMyAggregateCommand("14")).when(new FixtureTest_ExceptionHandling.ExceptionTriggeringCommand("14")).expectException(RuntimeException.class);
    }

    @Test(expected = RuntimeException.class)
    public void testGivenExceptionTriggeringCommand() {
        fixture.givenCommands(new FixtureTest_ExceptionHandling.CreateMyAggregateCommand("14"), new FixtureTest_ExceptionHandling.ExceptionTriggeringCommand("14"));
    }

    @Test
    public void testGivenCommandWithInvalidIdentifier() {
        fixture.givenCommands(new FixtureTest_ExceptionHandling.CreateMyAggregateCommand("1")).when(new FixtureTest_ExceptionHandling.ValidMyAggregateCommand("2")).expectException(EventStoreException.class);
    }

    @Test
    public void testExceptionMessageCheck() {
        fixture.givenCommands(new FixtureTest_ExceptionHandling.CreateMyAggregateCommand("1")).when(new FixtureTest_ExceptionHandling.ValidMyAggregateCommand("2")).expectException(EventStoreException.class).expectExceptionMessage("You probably want to use aggregateIdentifier() on your fixture to get the aggregate identifier to use");
    }

    @Test
    public void testExceptionMessageCheckWithMatcher() {
        fixture.givenCommands(new FixtureTest_ExceptionHandling.CreateMyAggregateCommand("1")).when(new FixtureTest_ExceptionHandling.ValidMyAggregateCommand("2")).expectException(EventStoreException.class).expectExceptionMessage(StringContains.containsString("You"));
    }

    @Test(expected = FixtureExecutionException.class)
    public void testWhenCommandWithInvalidIdentifier() {
        fixture.givenCommands(new FixtureTest_ExceptionHandling.CreateMyAggregateCommand("1"), new FixtureTest_ExceptionHandling.ValidMyAggregateCommand("2"));
    }

    private abstract static class AbstractMyAggregateCommand {
        @TargetAggregateIdentifier
        public final String id;

        protected AbstractMyAggregateCommand(String id) {
            this.id = id;
        }
    }

    private static class CreateMyAggregateCommand extends FixtureTest_ExceptionHandling.AbstractMyAggregateCommand {
        protected CreateMyAggregateCommand(String id) {
            super(id);
        }
    }

    private static class ExceptionTriggeringCommand extends FixtureTest_ExceptionHandling.AbstractMyAggregateCommand {
        protected ExceptionTriggeringCommand(String id) {
            super(id);
        }
    }

    private static class ValidMyAggregateCommand extends FixtureTest_ExceptionHandling.AbstractMyAggregateCommand {
        protected ValidMyAggregateCommand(String id) {
            super(id);
        }
    }

    private static class UnknownCommand extends FixtureTest_ExceptionHandling.AbstractMyAggregateCommand {
        protected UnknownCommand(String id) {
            super(id);
        }
    }

    private static class MyAggregateCreatedEvent {
        public final String id;

        public MyAggregateCreatedEvent(String id) {
            this.id = id;
        }
    }

    private static class MyAggregate {
        @AggregateIdentifier
        String id;

        private MyAggregate() {
        }

        @CommandHandler
        public MyAggregate(FixtureTest_ExceptionHandling.CreateMyAggregateCommand cmd) {
            apply(new FixtureTest_ExceptionHandling.MyAggregateCreatedEvent(cmd.id));
        }

        @CommandHandler
        public void handle(FixtureTest_ExceptionHandling.ValidMyAggregateCommand cmd) {
            /* no-op */
        }

        @CommandHandler
        public void handle(FixtureTest_ExceptionHandling.ExceptionTriggeringCommand cmd) {
            throw new RuntimeException("Error");
        }

        @EventHandler
        private void on(FixtureTest_ExceptionHandling.MyAggregateCreatedEvent event) {
            this.id = event.id;
        }
    }
}

