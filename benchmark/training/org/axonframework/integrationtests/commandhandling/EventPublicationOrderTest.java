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
package org.axonframework.integrationtests.commandhandling;


import java.util.UUID;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.GenericDomainEventMessage;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 *
 *
 * @author Allard Buijze
 * @author Nakul Mishra
 */
public class EventPublicationOrderTest {
    private CommandBus commandBus;

    private EventStore eventStore;

    @Test
    public void testPublicationOrderIsMaintained_AggregateAdded() {
        String aggregateId = UUID.randomUUID().toString();
        GenericDomainEventMessage<StubAggregateCreatedEvent> event = new GenericDomainEventMessage("test", aggregateId, 0, new StubAggregateCreatedEvent(aggregateId));
        Mockito.when(eventStore.readEvents(aggregateId)).thenReturn(DomainEventStream.of(event));
        Mockito.doAnswer(( invocation) -> {
            System.out.println(("Published event: " + (invocation.getArguments()[0].toString())));
            return Void.class;
        }).when(eventStore).publish(ArgumentMatchers.isA(EventMessage.class));
        commandBus.dispatch(asCommandMessage(new UpdateStubAggregateWithExtraEventCommand(aggregateId)));
        InOrder inOrder = Mockito.inOrder(eventStore, eventStore, eventStore);
        inOrder.verify(eventStore).publish(ArgumentMatchers.isA(DomainEventMessage.class));
        inOrder.verify(eventStore).publish(ArgumentMatchers.argThat(new EventPublicationOrderTest.NotADomainEventMatcher()));
        inOrder.verify(eventStore).publish(ArgumentMatchers.isA(DomainEventMessage.class));
    }

    private static class NotADomainEventMatcher implements ArgumentMatcher<EventMessage> {
        @Override
        public boolean matches(EventMessage o) {
            return !(o instanceof DomainEventMessage);
        }
    }
}

