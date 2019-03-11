/**
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.entitySystem;


import com.google.common.collect.Lists;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.terasology.engine.SimpleUri;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.internal.PojoEntityManager;
import org.terasology.entitySystem.event.AbstractConsumableEvent;
import org.terasology.entitySystem.event.Event;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.event.internal.EventReceiver;
import org.terasology.entitySystem.event.internal.EventSystemImpl;
import org.terasology.entitySystem.metadata.ComponentLibrary;
import org.terasology.entitySystem.stubs.IntegerComponent;
import org.terasology.entitySystem.stubs.StringComponent;
import org.terasology.entitySystem.systems.BaseComponentSystem;


/**
 *
 */
public class PojoEventSystemTests {
    ComponentLibrary compLibrary;

    EventSystemImpl eventSystem;

    PojoEntityManager entityManager;

    EntityRef entity;

    @Test
    public void testSendEventToEntity() {
        StringComponent component = entity.addComponent(new StringComponent());
        PojoEventSystemTests.TestEventHandler handler = new PojoEventSystemTests.TestEventHandler();
        eventSystem.registerEventHandler(handler);
        PojoEventSystemTests.TestEvent event = new PojoEventSystemTests.TestEvent();
        entity.send(event);
        Assert.assertEquals(1, handler.receivedList.size());
        Assert.assertEquals(event, handler.receivedList.get(0).event);
        Assert.assertEquals(entity, handler.receivedList.get(0).entity);
    }

    @Test
    public void testSendEventToEntityWithMultipleComponents() {
        StringComponent stringComponent = entity.addComponent(new StringComponent());
        IntegerComponent intComponent = entity.addComponent(new IntegerComponent());
        PojoEventSystemTests.TestEventHandler handler = new PojoEventSystemTests.TestEventHandler();
        eventSystem.registerEventHandler(handler);
        PojoEventSystemTests.TestEvent event = new PojoEventSystemTests.TestEvent();
        entity.send(event);
        Assert.assertEquals(2, handler.receivedList.size());
        for (PojoEventSystemTests.TestEventHandler.Received received : handler.receivedList) {
            Assert.assertEquals(event, received.event);
            Assert.assertEquals(entity, received.entity);
        }
    }

    @Test
    public void testSendEventToEntityComponent() {
        StringComponent component = entity.addComponent(new StringComponent());
        IntegerComponent intComponent = entity.addComponent(new IntegerComponent());
        PojoEventSystemTests.TestEventHandler handler = new PojoEventSystemTests.TestEventHandler();
        eventSystem.registerEventHandler(handler);
        PojoEventSystemTests.TestEvent event = new PojoEventSystemTests.TestEvent();
        eventSystem.send(entity, event, intComponent);
        Assert.assertEquals(1, handler.receivedList.size());
        Assert.assertEquals(event, handler.receivedList.get(0).event);
        Assert.assertEquals(entity, handler.receivedList.get(0).entity);
    }

    @Test
    public void testNoReceiveEventWhenMissingComponents() {
        StringComponent component = entity.addComponent(new StringComponent());
        PojoEventSystemTests.TestCompoundComponentEventHandler handler = new PojoEventSystemTests.TestCompoundComponentEventHandler();
        eventSystem.registerEventHandler(handler);
        PojoEventSystemTests.TestEvent event = new PojoEventSystemTests.TestEvent();
        eventSystem.send(entity, event);
        Assert.assertEquals(0, handler.receivedList.size());
    }

    @Test
    public void testReceiveEventRequiringMultipleComponents() {
        StringComponent stringComponent = entity.addComponent(new StringComponent());
        IntegerComponent intComponent = entity.addComponent(new IntegerComponent());
        PojoEventSystemTests.TestCompoundComponentEventHandler handler = new PojoEventSystemTests.TestCompoundComponentEventHandler();
        eventSystem.registerEventHandler(handler);
        PojoEventSystemTests.TestEvent event = new PojoEventSystemTests.TestEvent();
        eventSystem.send(entity, event);
        Assert.assertEquals(1, handler.receivedList.size());
        Assert.assertEquals(event, handler.receivedList.get(0).event);
        Assert.assertEquals(entity, handler.receivedList.get(0).entity);
    }

    @Test
    public void testPriorityAndCancel() {
        StringComponent stringComponent = entity.addComponent(new StringComponent());
        PojoEventSystemTests.TestEventHandler handlerNormal = new PojoEventSystemTests.TestEventHandler();
        PojoEventSystemTests.TestHighPriorityEventHandler handlerHigh = new PojoEventSystemTests.TestHighPriorityEventHandler();
        handlerHigh.cancel = true;
        eventSystem.registerEventHandler(handlerNormal);
        eventSystem.registerEventHandler(handlerHigh);
        PojoEventSystemTests.TestEvent event = new PojoEventSystemTests.TestEvent();
        eventSystem.send(entity, event);
        Assert.assertEquals(1, handlerHigh.receivedList.size());
        Assert.assertEquals(0, handlerNormal.receivedList.size());
    }

    @Test
    public void testChildEvent() {
        entity.addComponent(new IntegerComponent());
        PojoEventSystemTests.TestEventHandler handler = new PojoEventSystemTests.TestEventHandler();
        eventSystem.registerEvent(new SimpleUri("test:childEvent"), PojoEventSystemTests.TestChildEvent.class);
        eventSystem.registerEventHandler(handler);
        PojoEventSystemTests.TestChildEvent event = new PojoEventSystemTests.TestChildEvent();
        eventSystem.send(entity, event);
        Assert.assertEquals(1, handler.childEventReceived.size());
        Assert.assertEquals(1, handler.receivedList.size());
    }

    @Test
    public void testChildEventReceivedByUnfilteredHandler() {
        entity.addComponent(new IntegerComponent());
        PojoEventSystemTests.TestEventHandler handler = new PojoEventSystemTests.TestEventHandler();
        eventSystem.registerEvent(new SimpleUri("test:childEvent"), PojoEventSystemTests.TestChildEvent.class);
        eventSystem.registerEventHandler(handler);
        PojoEventSystemTests.TestChildEvent event = new PojoEventSystemTests.TestChildEvent();
        eventSystem.send(entity, event);
        Assert.assertEquals(1, handler.unfilteredEvents.size());
    }

    @Test
    public void testEventReceiverRegistration() {
        PojoEventSystemTests.TestEventReceiver receiver = new PojoEventSystemTests.TestEventReceiver();
        eventSystem.registerEventReceiver(receiver, PojoEventSystemTests.TestEvent.class);
        entity.send(new PojoEventSystemTests.TestEvent());
        Assert.assertEquals(1, receiver.eventList.size());
        eventSystem.unregisterEventReceiver(receiver, PojoEventSystemTests.TestEvent.class);
        entity.send(new PojoEventSystemTests.TestEvent());
        Assert.assertEquals(1, receiver.eventList.size());
    }

    private static class TestEvent extends AbstractConsumableEvent {}

    public static class TestChildEvent extends PojoEventSystemTests.TestEvent {}

    public static class TestEventHandler extends BaseComponentSystem {
        List<PojoEventSystemTests.TestEventHandler.Received> receivedList = Lists.newArrayList();

        List<PojoEventSystemTests.TestEventHandler.Received> childEventReceived = Lists.newArrayList();

        List<PojoEventSystemTests.TestEventHandler.Received> unfilteredEvents = Lists.newArrayList();

        @ReceiveEvent(components = StringComponent.class)
        public void handleStringEvent(PojoEventSystemTests.TestEvent event, EntityRef entity) {
            receivedList.add(new PojoEventSystemTests.TestEventHandler.Received(event, entity));
        }

        @ReceiveEvent(components = IntegerComponent.class)
        public void handleIntegerEvent(PojoEventSystemTests.TestEvent event, EntityRef entity) {
            receivedList.add(new PojoEventSystemTests.TestEventHandler.Received(event, entity));
        }

        @ReceiveEvent(components = IntegerComponent.class)
        public void handleChildEvent(PojoEventSystemTests.TestChildEvent event, EntityRef entity) {
            childEventReceived.add(new PojoEventSystemTests.TestEventHandler.Received(event, entity));
        }

        @ReceiveEvent
        public void handleUnfilteredTestEvent(PojoEventSystemTests.TestEvent event, EntityRef entity) {
            unfilteredEvents.add(new PojoEventSystemTests.TestEventHandler.Received(event, entity));
        }

        public void initialise() {
        }

        @Override
        public void shutdown() {
        }

        public static class Received {
            PojoEventSystemTests.TestEvent event;

            EntityRef entity;

            public Received(PojoEventSystemTests.TestEvent event, EntityRef entity) {
                this.event = event;
                this.entity = entity;
            }
        }
    }

    public static class TestHighPriorityEventHandler extends BaseComponentSystem {
        public boolean cancel;

        List<PojoEventSystemTests.TestHighPriorityEventHandler.Received> receivedList = Lists.newArrayList();

        @ReceiveEvent(components = StringComponent.class, priority = EventPriority.PRIORITY_HIGH)
        public void handleStringEvent(PojoEventSystemTests.TestEvent event, EntityRef entity) {
            receivedList.add(new PojoEventSystemTests.TestHighPriorityEventHandler.Received(event, entity));
            if (cancel) {
                consume();
            }
        }

        @ReceiveEvent(components = IntegerComponent.class, priority = EventPriority.PRIORITY_HIGH)
        public void handleIntegerEvent(PojoEventSystemTests.TestEvent event, EntityRef entity) {
            receivedList.add(new PojoEventSystemTests.TestHighPriorityEventHandler.Received(event, entity));
        }

        public void initialise() {
        }

        @Override
        public void shutdown() {
        }

        public static class Received {
            PojoEventSystemTests.TestEvent event;

            EntityRef entity;

            public Received(PojoEventSystemTests.TestEvent event, EntityRef entity) {
                this.event = event;
                this.entity = entity;
            }
        }
    }

    public static class TestCompoundComponentEventHandler extends BaseComponentSystem {
        List<PojoEventSystemTests.TestCompoundComponentEventHandler.Received> receivedList = Lists.newArrayList();

        @ReceiveEvent(components = { StringComponent.class, IntegerComponent.class })
        public void handleStringEvent(PojoEventSystemTests.TestEvent event, EntityRef entity) {
            receivedList.add(new PojoEventSystemTests.TestCompoundComponentEventHandler.Received(event, entity));
        }

        public void initialise() {
        }

        @Override
        public void shutdown() {
        }

        public static class Received {
            PojoEventSystemTests.TestEvent event;

            EntityRef entity;

            public Received(PojoEventSystemTests.TestEvent event, EntityRef entity) {
                this.event = event;
                this.entity = entity;
            }
        }
    }

    public static class TestEventReceiver implements EventReceiver<PojoEventSystemTests.TestEvent> {
        List<Event> eventList = Lists.newArrayList();

        @Override
        public void onEvent(PojoEventSystemTests.TestEvent event, EntityRef entity) {
            eventList.add(event);
        }
    }
}

