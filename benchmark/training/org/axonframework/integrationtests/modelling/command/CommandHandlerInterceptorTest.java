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
package org.axonframework.integrationtests.modelling.command;


import java.util.Objects;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.common.AxonConfigurationException;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.InterceptorChain;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateMember;
import org.axonframework.modelling.command.CommandHandlerInterceptor;
import org.axonframework.modelling.command.EntityId;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Tests for annotated command handler interceptor on aggregate / child entities.
 *
 * @author Milan Savic
 */
@RunWith(MockitoJUnitRunner.class)
public class CommandHandlerInterceptorTest {
    private CommandGateway commandGateway;

    private EventStore eventStore;

    @SuppressWarnings("unchecked")
    @Test
    public void testInterceptor() {
        commandGateway.sendAndWait(asCommandMessage(new CommandHandlerInterceptorTest.CreateMyAggregateCommand("id")));
        String result = commandGateway.sendAndWait(asCommandMessage(new CommandHandlerInterceptorTest.UpdateMyAggregateStateCommand("id", "state")));
        ArgumentCaptor<EventMessage<?>> eventCaptor = ArgumentCaptor.forClass(EventMessage.class);
        Mockito.verify(eventStore, Mockito.times(3)).publish(eventCaptor.capture());
        Assert.assertEquals(new CommandHandlerInterceptorTest.MyAggregateCreatedEvent("id"), eventCaptor.getAllValues().get(0).getPayload());
        Assert.assertEquals(new CommandHandlerInterceptorTest.AnyCommandInterceptedEvent(CommandHandlerInterceptorTest.UpdateMyAggregateStateCommand.class.getName()), eventCaptor.getAllValues().get(1).getPayload());
        Assert.assertEquals(new CommandHandlerInterceptorTest.MyAggregateStateUpdatedEvent("id", "state intercepted"), eventCaptor.getAllValues().get(2).getPayload());
        Assert.assertEquals("aggregateUpdateResult", result);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInterceptorWithChainProceeding() {
        commandGateway.sendAndWait(asCommandMessage(new CommandHandlerInterceptorTest.CreateMyAggregateCommand("id")));
        commandGateway.sendAndWait(asCommandMessage(new CommandHandlerInterceptorTest.ClearMyAggregateStateCommand("id", true)));
        ArgumentCaptor<EventMessage<?>> eventCaptor = ArgumentCaptor.forClass(EventMessage.class);
        Mockito.verify(eventStore, Mockito.times(3)).publish(eventCaptor.capture());
        Assert.assertEquals(new CommandHandlerInterceptorTest.MyAggregateCreatedEvent("id"), eventCaptor.getAllValues().get(0).getPayload());
        Assert.assertEquals(new CommandHandlerInterceptorTest.AnyCommandInterceptedEvent(CommandHandlerInterceptorTest.ClearMyAggregateStateCommand.class.getName()), eventCaptor.getAllValues().get(1).getPayload());
        Assert.assertEquals(new CommandHandlerInterceptorTest.MyAggregateStateClearedEvent("id"), eventCaptor.getAllValues().get(2).getPayload());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInterceptorWithoutChainProceeding() {
        commandGateway.sendAndWait(asCommandMessage(new CommandHandlerInterceptorTest.CreateMyAggregateCommand("id")));
        commandGateway.sendAndWait(asCommandMessage(new CommandHandlerInterceptorTest.ClearMyAggregateStateCommand("id", false)));
        ArgumentCaptor<EventMessage<?>> eventCaptor = ArgumentCaptor.forClass(EventMessage.class);
        Mockito.verify(eventStore, Mockito.times(2)).publish(eventCaptor.capture());
        Assert.assertEquals(new CommandHandlerInterceptorTest.MyAggregateCreatedEvent("id"), eventCaptor.getAllValues().get(0).getPayload());
        Assert.assertEquals(new CommandHandlerInterceptorTest.MyAggregateStateNotClearedEvent("id"), eventCaptor.getAllValues().get(1).getPayload());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInterceptorWithNestedEntity() {
        commandGateway.sendAndWait(asCommandMessage(new CommandHandlerInterceptorTest.CreateMyAggregateCommand("id")));
        commandGateway.sendAndWait(asCommandMessage(new CommandHandlerInterceptorTest.MyNestedCommand("id", "state")));
        ArgumentCaptor<EventMessage<?>> eventCaptor = ArgumentCaptor.forClass(EventMessage.class);
        Mockito.verify(eventStore, Mockito.times(4)).publish(eventCaptor.capture());
        Assert.assertEquals(new CommandHandlerInterceptorTest.MyAggregateCreatedEvent("id"), eventCaptor.getAllValues().get(0).getPayload());
        Assert.assertEquals(new CommandHandlerInterceptorTest.AnyCommandMatchingPatternInterceptedEvent(CommandHandlerInterceptorTest.MyNestedCommand.class.getName()), eventCaptor.getAllValues().get(1).getPayload());
        Assert.assertEquals(new CommandHandlerInterceptorTest.AnyCommandInterceptedEvent(CommandHandlerInterceptorTest.MyNestedCommand.class.getName()), eventCaptor.getAllValues().get(2).getPayload());
        Assert.assertEquals(new CommandHandlerInterceptorTest.MyNestedEvent("id", "state intercepted"), eventCaptor.getAllValues().get(3).getPayload());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInterceptorWithNestedNestedEntity() {
        commandGateway.sendAndWait(asCommandMessage(new CommandHandlerInterceptorTest.CreateMyAggregateCommand("id")));
        commandGateway.sendAndWait(asCommandMessage(new CommandHandlerInterceptorTest.MyNestedNestedCommand("id", "state")));
        ArgumentCaptor<EventMessage<?>> eventCaptor = ArgumentCaptor.forClass(EventMessage.class);
        Mockito.verify(eventStore, Mockito.times(6)).publish(eventCaptor.capture());
        Assert.assertEquals(new CommandHandlerInterceptorTest.MyAggregateCreatedEvent("id"), eventCaptor.getAllValues().get(0).getPayload());
        Assert.assertEquals(new CommandHandlerInterceptorTest.AnyCommandMatchingPatternInterceptedEvent(CommandHandlerInterceptorTest.MyNestedNestedCommand.class.getName()), eventCaptor.getAllValues().get(1).getPayload());
        Assert.assertEquals(new CommandHandlerInterceptorTest.AnyCommandInterceptedEvent(CommandHandlerInterceptorTest.MyNestedNestedCommand.class.getName()), eventCaptor.getAllValues().get(2).getPayload());
        Assert.assertEquals(new CommandHandlerInterceptorTest.AnyCommandInterceptedEvent(("StaticNestedNested" + (CommandHandlerInterceptorTest.MyNestedNestedCommand.class.getName()))), eventCaptor.getAllValues().get(3).getPayload());
        Assert.assertEquals(new CommandHandlerInterceptorTest.AnyCommandInterceptedEvent(("NestedNested" + (CommandHandlerInterceptorTest.MyNestedNestedCommand.class.getName()))), eventCaptor.getAllValues().get(4).getPayload());
        Assert.assertEquals(new CommandHandlerInterceptorTest.MyNestedNestedEvent("id", "state parent intercepted intercepted"), eventCaptor.getAllValues().get(5).getPayload());
    }

    @Test(expected = AxonConfigurationException.class)
    public void testInterceptorWithNonVoidReturnType() {
        org.axonframework.eventsourcing.EventSourcingRepository.builder(CommandHandlerInterceptorTest.MyAggregateWithInterceptorReturningNonVoid.class).eventStore(eventStore).build();
    }

    @Test
    public void testInterceptorWithDeclaredChainAllowedToDeclareNonVoidReturnType() {
        org.axonframework.eventsourcing.EventSourcingRepository.builder(CommandHandlerInterceptorTest.MyAggregateWithDeclaredInterceptorChainInterceptorReturningNonVoid.class).eventStore(eventStore).build();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInterceptorThrowingAnException() {
        commandGateway.sendAndWait(asCommandMessage(new CommandHandlerInterceptorTest.CreateMyAggregateCommand("id")));
        try {
            commandGateway.sendAndWait(asCommandMessage(new CommandHandlerInterceptorTest.InterceptorThrowingCommand("id")));
            Assert.fail("Expected exception");
        } catch (CommandHandlerInterceptorTest.InterceptorException e) {
            // we are expecting this
        }
        ArgumentCaptor<EventMessage<?>> eventCaptor = ArgumentCaptor.forClass(EventMessage.class);
        Mockito.verify(eventStore, Mockito.times(1)).publish(eventCaptor.capture());
        Assert.assertEquals(new CommandHandlerInterceptorTest.MyAggregateCreatedEvent("id"), eventCaptor.getAllValues().get(0).getPayload());
    }

    private static class CreateMyAggregateCommand {
        private final String id;

        private CreateMyAggregateCommand(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    private static class UpdateMyAggregateStateCommand {
        @TargetAggregateIdentifier
        private final String id;

        private String state;

        private UpdateMyAggregateStateCommand(String id, String state) {
            this.id = id;
            this.state = state;
        }

        public String getId() {
            return id;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }

    private static class ClearMyAggregateStateCommand {
        @TargetAggregateIdentifier
        private final String id;

        private final boolean proceed;

        private ClearMyAggregateStateCommand(String id, boolean proceed) {
            this.id = id;
            this.proceed = proceed;
        }

        public String getId() {
            return id;
        }

        public boolean isProceed() {
            return proceed;
        }
    }

    private static class MyNestedCommand {
        @TargetAggregateIdentifier
        private final String id;

        private String state;

        private MyNestedCommand(String id, String state) {
            this.id = id;
            this.state = state;
        }

        public String getId() {
            return id;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }

    private static class MyNestedNestedCommand {
        @TargetAggregateIdentifier
        private final String id;

        private String state;

        private MyNestedNestedCommand(String id, String state) {
            this.id = id;
            this.state = state;
        }

        public String getId() {
            return id;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }

    public static class InterceptorThrowingCommand {
        @TargetAggregateIdentifier
        private final String id;

        public InterceptorThrowingCommand(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    private static class MyAggregateCreatedEvent {
        private final String id;

        private MyAggregateCreatedEvent(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            CommandHandlerInterceptorTest.MyAggregateCreatedEvent that = ((CommandHandlerInterceptorTest.MyAggregateCreatedEvent) (o));
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    private static class MyAggregateStateUpdatedEvent {
        @TargetAggregateIdentifier
        private final String id;

        private final String state;

        private MyAggregateStateUpdatedEvent(String id, String state) {
            this.id = id;
            this.state = state;
        }

        public String getId() {
            return id;
        }

        public String getState() {
            return state;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            CommandHandlerInterceptorTest.MyAggregateStateUpdatedEvent that = ((CommandHandlerInterceptorTest.MyAggregateStateUpdatedEvent) (o));
            return (Objects.equals(id, that.id)) && (Objects.equals(state, that.state));
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, state);
        }
    }

    private static class MyAggregateStateClearedEvent {
        private final String id;

        private MyAggregateStateClearedEvent(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            CommandHandlerInterceptorTest.MyAggregateStateClearedEvent that = ((CommandHandlerInterceptorTest.MyAggregateStateClearedEvent) (o));
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    private static class MyAggregateStateNotClearedEvent {
        private final String id;

        private MyAggregateStateNotClearedEvent(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            CommandHandlerInterceptorTest.MyAggregateStateNotClearedEvent that = ((CommandHandlerInterceptorTest.MyAggregateStateNotClearedEvent) (o));
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    private static class MyNestedEvent {
        private final String id;

        private final String state;

        private MyNestedEvent(String id, String state) {
            this.id = id;
            this.state = state;
        }

        public String getId() {
            return id;
        }

        public String getState() {
            return state;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            CommandHandlerInterceptorTest.MyNestedEvent that = ((CommandHandlerInterceptorTest.MyNestedEvent) (o));
            return (Objects.equals(id, that.id)) && (Objects.equals(state, that.state));
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, state);
        }
    }

    private static class MyNestedNestedEvent {
        private final String id;

        private final String state;

        private MyNestedNestedEvent(String id, String state) {
            this.id = id;
            this.state = state;
        }

        public String getId() {
            return id;
        }

        public String getState() {
            return state;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            CommandHandlerInterceptorTest.MyNestedNestedEvent that = ((CommandHandlerInterceptorTest.MyNestedNestedEvent) (o));
            return (Objects.equals(id, that.id)) && (Objects.equals(state, that.state));
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, state);
        }
    }

    private static class InterceptorThrewEvent {
        private final String id;

        private InterceptorThrewEvent(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            CommandHandlerInterceptorTest.InterceptorThrewEvent that = ((CommandHandlerInterceptorTest.InterceptorThrewEvent) (o));
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    private static class AnyCommandInterceptedEvent {
        private final String id;

        private AnyCommandInterceptedEvent(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            CommandHandlerInterceptorTest.AnyCommandInterceptedEvent that = ((CommandHandlerInterceptorTest.AnyCommandInterceptedEvent) (o));
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    private static class AnyCommandMatchingPatternInterceptedEvent {
        private final String id;

        private AnyCommandMatchingPatternInterceptedEvent(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            CommandHandlerInterceptorTest.AnyCommandMatchingPatternInterceptedEvent that = ((CommandHandlerInterceptorTest.AnyCommandMatchingPatternInterceptedEvent) (o));
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    @SuppressWarnings("unused")
    private static class MyAggregate {
        @AggregateIdentifier
        private String id;

        private String state;

        @AggregateMember
        private CommandHandlerInterceptorTest.MyNestedEntity myNestedEntity;

        public MyAggregate() {
            // do nothing
        }

        @CommandHandler
        public MyAggregate(CommandHandlerInterceptorTest.CreateMyAggregateCommand command) {
            apply(new CommandHandlerInterceptorTest.MyAggregateCreatedEvent(command.getId()));
        }

        @CommandHandlerInterceptor
        public void interceptAll(Object command) {
            apply(new CommandHandlerInterceptorTest.AnyCommandInterceptedEvent(command.getClass().getName()));
        }

        @CommandHandlerInterceptor(commandNamePattern = ".*Nested.*")
        public void interceptAllMatchingPattern(Object command, InterceptorChain interceptorChain) throws Exception {
            apply(new CommandHandlerInterceptorTest.AnyCommandMatchingPatternInterceptedEvent(command.getClass().getName()));
            interceptorChain.proceed();
        }

        @EventSourcingHandler
        public void on(CommandHandlerInterceptorTest.MyAggregateCreatedEvent event) {
            this.id = event.getId();
            myNestedEntity = new CommandHandlerInterceptorTest.MyNestedEntity(id);
        }

        @CommandHandlerInterceptor
        public void intercept(CommandHandlerInterceptorTest.UpdateMyAggregateStateCommand command) {
            command.setState(((command.getState()) + " intercepted"));
        }

        @CommandHandler
        public String handle(CommandHandlerInterceptorTest.UpdateMyAggregateStateCommand command) {
            apply(new CommandHandlerInterceptorTest.MyAggregateStateUpdatedEvent(command.getId(), command.getState()));
            return "aggregateUpdateResult";
        }

        @EventSourcingHandler
        public void on(CommandHandlerInterceptorTest.MyAggregateStateUpdatedEvent event) {
            this.state = event.getState();
        }

        @CommandHandlerInterceptor
        public void intercept(CommandHandlerInterceptorTest.ClearMyAggregateStateCommand command, InterceptorChain interceptorChain) throws Exception {
            if (command.isProceed()) {
                interceptorChain.proceed();
            } else {
                apply(new CommandHandlerInterceptorTest.MyAggregateStateNotClearedEvent(command.getId()));
            }
        }

        @CommandHandler
        public void handle(CommandHandlerInterceptorTest.ClearMyAggregateStateCommand command) {
            apply(new CommandHandlerInterceptorTest.MyAggregateStateClearedEvent(command.getId()));
        }

        @EventSourcingHandler
        public void on(CommandHandlerInterceptorTest.MyAggregateStateClearedEvent event) {
            this.state = "";
        }

        @EventSourcingHandler
        public void on(CommandHandlerInterceptorTest.MyAggregateStateNotClearedEvent event) {
            // do nothing
        }

        @CommandHandlerInterceptor
        public void interceptChildCommand(CommandHandlerInterceptorTest.MyNestedNestedCommand command) {
            command.setState(((command.getState()) + " parent intercepted"));
        }

        @CommandHandlerInterceptor
        public void intercept(CommandHandlerInterceptorTest.InterceptorThrowingCommand command) {
            throw new CommandHandlerInterceptorTest.InterceptorException();
        }

        @CommandHandler
        public void handle(CommandHandlerInterceptorTest.InterceptorThrowingCommand command) {
            apply(new CommandHandlerInterceptorTest.InterceptorThrewEvent(command.getId()));
        }

        @EventSourcingHandler
        public void on(CommandHandlerInterceptorTest.InterceptorThrewEvent event) {
            // do nothing
        }
    }

    @SuppressWarnings("unused")
    private static class MyNestedEntity {
        @EntityId
        private final String id;

        private String state;

        @AggregateMember
        private CommandHandlerInterceptorTest.MyNestedNestedEntity myNestedNestedEntity;

        private MyNestedEntity(String id) {
            this.id = id;
            myNestedNestedEntity = new CommandHandlerInterceptorTest.MyNestedNestedEntity(id);
        }

        @CommandHandlerInterceptor
        public void intercept(CommandHandlerInterceptorTest.MyNestedCommand command) {
            command.setState(((command.getState()) + " intercepted"));
        }

        @CommandHandler
        public void handle(CommandHandlerInterceptorTest.MyNestedCommand command) {
            apply(new CommandHandlerInterceptorTest.MyNestedEvent(command.getId(), command.getState()));
        }

        @EventSourcingHandler
        public void on(CommandHandlerInterceptorTest.MyNestedEvent event) {
            this.state = event.getState();
        }
    }

    @SuppressWarnings("unused")
    private static class MyNestedNestedEntity {
        @EntityId
        private final String id;

        private String state;

        @CommandHandlerInterceptor
        public static void interceptAll(Object command, InterceptorChain chain) throws Exception {
            apply(new CommandHandlerInterceptorTest.AnyCommandInterceptedEvent(("StaticNestedNested" + (command.getClass().getName()))));
            chain.proceed();
        }

        private MyNestedNestedEntity(String id) {
            this.id = id;
        }

        @CommandHandlerInterceptor
        public void interceptAll(Object command) {
            apply(new CommandHandlerInterceptorTest.AnyCommandInterceptedEvent(("NestedNested" + (command.getClass().getName()))));
        }

        @CommandHandlerInterceptor
        public void intercept(CommandHandlerInterceptorTest.MyNestedNestedCommand command) {
            command.setState(((command.getState()) + " intercepted"));
        }

        @CommandHandler
        public void handle(CommandHandlerInterceptorTest.MyNestedNestedCommand command) {
            apply(new CommandHandlerInterceptorTest.MyNestedNestedEvent(command.getId(), command.getState()));
        }

        @EventSourcingHandler
        public void on(CommandHandlerInterceptorTest.MyNestedNestedEvent event) {
            this.state = event.state;
        }
    }

    @SuppressWarnings("unused")
    private static class MyAggregateWithInterceptorReturningNonVoid {
        @CommandHandlerInterceptor
        public Object intercept() {
            return new Object();
        }
    }

    @SuppressWarnings("unused")
    private static class MyAggregateWithDeclaredInterceptorChainInterceptorReturningNonVoid {
        @CommandHandlerInterceptor
        public Object intercept(InterceptorChain chain) {
            return new Object();
        }
    }

    private static class InterceptorException extends RuntimeException {}
}

