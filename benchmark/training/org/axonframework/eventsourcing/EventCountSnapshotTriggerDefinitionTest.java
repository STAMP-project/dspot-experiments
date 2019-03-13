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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.axonframework.eventhandling.GenericDomainEventMessage;
import org.axonframework.messaging.MetaData;
import org.axonframework.messaging.unitofwork.CurrentUnitOfWork;
import org.axonframework.messaging.unitofwork.UnitOfWork;
import org.axonframework.modelling.command.Aggregate;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Allard Buijze
 */
public class EventCountSnapshotTriggerDefinitionTest {
    private EventCountSnapshotTriggerDefinition testSubject;

    private Snapshotter mockSnapshotter;

    private String aggregateIdentifier;

    private Aggregate<?> aggregate;

    private UnitOfWork<?> unitOfWork;

    @Test
    public void testSnapshotterTriggeredOnUnitOfWorkCommit() {
        SnapshotTrigger trigger = testSubject.prepareTrigger(aggregate.rootType());
        GenericDomainEventMessage<String> msg = new GenericDomainEventMessage("type", aggregateIdentifier, ((long) (0)), "Mock contents", MetaData.emptyInstance());
        trigger.eventHandled(msg);
        trigger.eventHandled(msg);
        trigger.eventHandled(msg);
        trigger.eventHandled(msg);
        Mockito.verify(mockSnapshotter, Mockito.never()).scheduleSnapshot(aggregate.rootType(), aggregateIdentifier);
        CurrentUnitOfWork.commit();
        Mockito.verify(mockSnapshotter).scheduleSnapshot(aggregate.rootType(), aggregateIdentifier);
    }

    @Test
    public void testSnapshotterNotTriggered() {
        SnapshotTrigger trigger = testSubject.prepareTrigger(aggregate.rootType());
        GenericDomainEventMessage<String> msg = new GenericDomainEventMessage("type", aggregateIdentifier, ((long) (0)), "Mock contents", MetaData.emptyInstance());
        trigger.eventHandled(msg);
        trigger.eventHandled(msg);
        trigger.eventHandled(msg);
        Mockito.verify(mockSnapshotter, Mockito.never()).scheduleSnapshot(aggregate.getClass(), aggregateIdentifier);
        CurrentUnitOfWork.commit();
        Mockito.verify(mockSnapshotter, Mockito.never()).scheduleSnapshot(aggregate.getClass(), aggregateIdentifier);
    }

    @Test
    public void testCounterDoesNotResetWhenSerialized() throws IOException, ClassNotFoundException {
        SnapshotTrigger trigger = testSubject.prepareTrigger(aggregate.rootType());
        GenericDomainEventMessage<String> msg = new GenericDomainEventMessage("type", aggregateIdentifier, ((long) (0)), "Mock contents", MetaData.emptyInstance());
        trigger.eventHandled(msg);
        trigger.eventHandled(msg);
        trigger.eventHandled(msg);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(trigger);
        trigger = ((SnapshotTrigger) (new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray())).readObject()));
        testSubject.reconfigure(aggregate.rootType(), trigger);
        // this triggers the snapshot
        trigger.eventHandled(msg);
        Mockito.verify(mockSnapshotter, Mockito.never()).scheduleSnapshot(aggregate.rootType(), aggregateIdentifier);
        CurrentUnitOfWork.commit();
        Mockito.verify(mockSnapshotter).scheduleSnapshot(aggregate.rootType(), aggregateIdentifier);
    }
}

