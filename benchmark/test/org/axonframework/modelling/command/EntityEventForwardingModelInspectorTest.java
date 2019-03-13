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
package org.axonframework.modelling.command;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.modelling.command.inspection.AggregateModel;
import org.axonframework.modelling.command.inspection.AnnotatedAggregateMetaModelFactory;
import org.junit.Assert;
import org.junit.Test;


public class EntityEventForwardingModelInspectorTest {
    private static final String AGGREGATE_ID = "aggregateId";

    private static final String ENTITY_ID = "entityId";

    @Test
    public void testExpectEventsToBeRoutedToNoEntityForForwardModeSetToNone() {
        AggregateModel<EntityEventForwardingModelInspectorTest.SomeNoneEventForwardingEntityAggregate> inspector = AnnotatedAggregateMetaModelFactory.inspectAggregate(EntityEventForwardingModelInspectorTest.SomeNoneEventForwardingEntityAggregate.class);
        EntityEventForwardingModelInspectorTest.SomeNoneEventForwardingEntityAggregate target = new EntityEventForwardingModelInspectorTest.SomeNoneEventForwardingEntityAggregate();
        // Both called once, as the entity does not receive any events
        AtomicLong aggregatePayload = new AtomicLong();
        inspector.publish(asEventMessage(new EntityEventForwardingModelInspectorTest.EntityRoutedEvent(EntityEventForwardingModelInspectorTest.AGGREGATE_ID, aggregatePayload)), target);
        AtomicLong entityPayload = new AtomicLong();
        inspector.publish(asEventMessage(new EntityEventForwardingModelInspectorTest.EntityRoutedEvent(EntityEventForwardingModelInspectorTest.ENTITY_ID, entityPayload)), target);
        Assert.assertEquals(1L, aggregatePayload.get());
        Assert.assertEquals(1L, entityPayload.get());
    }

    @Test
    public void testExpectEventsToBeRoutedToRightEntityOnly() {
        AggregateModel<EntityEventForwardingModelInspectorTest.SomeEventForwardingEntityAggregate> inspector = AnnotatedAggregateMetaModelFactory.inspectAggregate(EntityEventForwardingModelInspectorTest.SomeEventForwardingEntityAggregate.class);
        EntityEventForwardingModelInspectorTest.SomeEventForwardingEntityAggregate target = new EntityEventForwardingModelInspectorTest.SomeEventForwardingEntityAggregate();
        // Called once
        AtomicLong aggregatePayload = new AtomicLong();
        inspector.publish(asEventMessage(new EntityEventForwardingModelInspectorTest.EntityRoutedEvent(EntityEventForwardingModelInspectorTest.AGGREGATE_ID, aggregatePayload)), target);
        // Called twice - by aggregate and entity
        AtomicLong entityPayload = new AtomicLong();
        inspector.publish(asEventMessage(new EntityEventForwardingModelInspectorTest.EntityRoutedEvent(EntityEventForwardingModelInspectorTest.ENTITY_ID, entityPayload)), target);
        Assert.assertEquals(1L, aggregatePayload.get());
        Assert.assertEquals(2L, entityPayload.get());
    }

    @Test
    public void testExpectEventsToBeRoutedToRightEntityOnlyWithSpecificRoutingKey() {
        AggregateModel<EntityEventForwardingModelInspectorTest.SomeEventForwardingEntityAggregateWithSpecificEventRoutingKey> inspector = AnnotatedAggregateMetaModelFactory.inspectAggregate(EntityEventForwardingModelInspectorTest.SomeEventForwardingEntityAggregateWithSpecificEventRoutingKey.class);
        EntityEventForwardingModelInspectorTest.SomeEventForwardingEntityAggregateWithSpecificEventRoutingKey target = new EntityEventForwardingModelInspectorTest.SomeEventForwardingEntityAggregateWithSpecificEventRoutingKey();
        // Called once
        AtomicLong aggregatePayload = new AtomicLong();
        inspector.publish(asEventMessage(new EntityEventForwardingModelInspectorTest.SomeOtherEntityRoutedEvent(EntityEventForwardingModelInspectorTest.AGGREGATE_ID, aggregatePayload)), target);
        // Called twice - by aggregate and entity
        AtomicLong entityPayload = new AtomicLong();
        inspector.publish(asEventMessage(new EntityEventForwardingModelInspectorTest.SomeOtherEntityRoutedEvent(EntityEventForwardingModelInspectorTest.ENTITY_ID, entityPayload)), target);
        Assert.assertEquals(1L, aggregatePayload.get());
        Assert.assertEquals(2L, entityPayload.get());
    }

    @Test
    public void testExpectEventsToBeRoutedToRightEntityOnlyForEntityCollection() {
        AggregateModel<EntityEventForwardingModelInspectorTest.SomeEventForwardingEntityCollectionAggregate> inspector = AnnotatedAggregateMetaModelFactory.inspectAggregate(EntityEventForwardingModelInspectorTest.SomeEventForwardingEntityCollectionAggregate.class);
        EntityEventForwardingModelInspectorTest.SomeEventForwardingEntityCollectionAggregate target = new EntityEventForwardingModelInspectorTest.SomeEventForwardingEntityCollectionAggregate();
        // All called once, as there is an event per entity only
        AtomicLong entityOnePayload = new AtomicLong();
        inspector.publish(asEventMessage(new EntityEventForwardingModelInspectorTest.EntityRoutedEvent("entityId1", entityOnePayload)), target);
        AtomicLong entityTwoPayload = new AtomicLong();
        inspector.publish(asEventMessage(new EntityEventForwardingModelInspectorTest.EntityRoutedEvent("entityId2", entityTwoPayload)), target);
        AtomicLong entityThreePayload = new AtomicLong();
        inspector.publish(asEventMessage(new EntityEventForwardingModelInspectorTest.EntityRoutedEvent("entityId3", entityThreePayload)), target);
        Assert.assertEquals(1L, entityOnePayload.get());
        Assert.assertEquals(1L, entityTwoPayload.get());
        Assert.assertEquals(1L, entityThreePayload.get());
    }

    @Test
    public void testExpectEventsToBeRoutedToRightEntityOnlyForEntityMap() {
        AggregateModel<EntityEventForwardingModelInspectorTest.SomeEventForwardingEntityMapAggregate> inspector = AnnotatedAggregateMetaModelFactory.inspectAggregate(EntityEventForwardingModelInspectorTest.SomeEventForwardingEntityMapAggregate.class);
        EntityEventForwardingModelInspectorTest.SomeEventForwardingEntityMapAggregate target = new EntityEventForwardingModelInspectorTest.SomeEventForwardingEntityMapAggregate();
        // All called once, as there is an event per entity only
        AtomicLong entityOnePayload = new AtomicLong();
        inspector.publish(asEventMessage(new EntityEventForwardingModelInspectorTest.EntityRoutedEvent("entityId1", entityOnePayload)), target);
        AtomicLong entityTwoPayload = new AtomicLong();
        inspector.publish(asEventMessage(new EntityEventForwardingModelInspectorTest.EntityRoutedEvent("entityId2", entityTwoPayload)), target);
        AtomicLong entityThreePayload = new AtomicLong();
        inspector.publish(asEventMessage(new EntityEventForwardingModelInspectorTest.EntityRoutedEvent("entityId3", entityThreePayload)), target);
        Assert.assertEquals(1L, entityOnePayload.get());
        Assert.assertEquals(1L, entityTwoPayload.get());
        Assert.assertEquals(1L, entityThreePayload.get());
    }

    private static class SomeNoneEventForwardingEntityAggregate {
        @AggregateIdentifier
        private String id = EntityEventForwardingModelInspectorTest.AGGREGATE_ID;

        @AggregateMember(eventForwardingMode = ForwardNone.class)
        private EntityEventForwardingModelInspectorTest.SomeEventForwardedEntity entity = new EntityEventForwardingModelInspectorTest.SomeEventForwardedEntity(EntityEventForwardingModelInspectorTest.ENTITY_ID);

        @EventHandler
        public void handle(EntityEventForwardingModelInspectorTest.EntityRoutedEvent event) {
            event.getValue().incrementAndGet();
        }
    }

    private static class SomeEventForwardingEntityAggregate {
        @AggregateIdentifier
        private String id = EntityEventForwardingModelInspectorTest.AGGREGATE_ID;

        @AggregateMember(eventForwardingMode = ForwardMatchingInstances.class)
        private EntityEventForwardingModelInspectorTest.SomeEventForwardedEntity entity = new EntityEventForwardingModelInspectorTest.SomeEventForwardedEntity(EntityEventForwardingModelInspectorTest.ENTITY_ID);

        @EventHandler
        public void handle(EntityEventForwardingModelInspectorTest.EntityRoutedEvent event) {
            event.getValue().incrementAndGet();
        }
    }

    private static class SomeEventForwardingEntityAggregateWithSpecificEventRoutingKey {
        @AggregateIdentifier
        private String id = EntityEventForwardingModelInspectorTest.AGGREGATE_ID;

        @AggregateMember(eventForwardingMode = ForwardMatchingInstances.class, routingKey = "someIdentifier")
        private EntityEventForwardingModelInspectorTest.SomeEventForwardedEntity entity = new EntityEventForwardingModelInspectorTest.SomeEventForwardedEntity(EntityEventForwardingModelInspectorTest.ENTITY_ID);

        @EventHandler
        public void handle(EntityEventForwardingModelInspectorTest.SomeOtherEntityRoutedEvent event) {
            event.getValue().incrementAndGet();
        }
    }

    private static class SomeEventForwardingEntityCollectionAggregate {
        @AggregateIdentifier
        private String id = EntityEventForwardingModelInspectorTest.AGGREGATE_ID;

        @AggregateMember(eventForwardingMode = ForwardMatchingInstances.class)
        private List<EntityEventForwardingModelInspectorTest.SomeEventForwardedEntity> entities;

        SomeEventForwardingEntityCollectionAggregate() {
            this.entities = new ArrayList<>();
            entities.add(new EntityEventForwardingModelInspectorTest.SomeEventForwardedEntity("entityId1"));
            entities.add(new EntityEventForwardingModelInspectorTest.SomeEventForwardedEntity("entityId2"));
            entities.add(new EntityEventForwardingModelInspectorTest.SomeEventForwardedEntity("entityId3"));
        }
    }

    private static class SomeEventForwardingEntityMapAggregate {
        @AggregateIdentifier
        private String id = EntityEventForwardingModelInspectorTest.AGGREGATE_ID;

        @AggregateMember(eventForwardingMode = ForwardMatchingInstances.class)
        private Map<String, EntityEventForwardingModelInspectorTest.SomeEventForwardedEntity> entities;

        SomeEventForwardingEntityMapAggregate() {
            this.entities = new HashMap<>();
            entities.put("entityId1", new EntityEventForwardingModelInspectorTest.SomeEventForwardedEntity("entityId1"));
            entities.put("entityId2", new EntityEventForwardingModelInspectorTest.SomeEventForwardedEntity("entityId2"));
            entities.put("entityId3", new EntityEventForwardingModelInspectorTest.SomeEventForwardedEntity("entityId3"));
        }
    }

    private static class SomeEventForwardedEntity {
        @EntityId
        private final String entityId;

        SomeEventForwardedEntity(String entityId) {
            this.entityId = entityId;
        }

        @EventHandler
        public void handle(EntityEventForwardingModelInspectorTest.EntityRoutedEvent event) {
            event.getValue().incrementAndGet();
        }
    }

    private static class EntityRoutedEvent {
        private final String entityId;

        private final AtomicLong value;

        private EntityRoutedEvent(String entityId, AtomicLong value) {
            this.entityId = entityId;
            this.value = value;
        }

        public String getEntityId() {
            return entityId;
        }

        public AtomicLong getValue() {
            return value;
        }
    }

    private static class SomeOtherEntityRoutedEvent extends EntityEventForwardingModelInspectorTest.EntityRoutedEvent {
        private final String someIdentifier;

        private SomeOtherEntityRoutedEvent(String someIdentifier, AtomicLong value) {
            super(someIdentifier, value);
            this.someIdentifier = someIdentifier;
        }

        public String getSomeIdentifier() {
            return someIdentifier;
        }
    }
}

