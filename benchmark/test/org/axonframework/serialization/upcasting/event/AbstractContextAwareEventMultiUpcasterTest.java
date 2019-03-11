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
package org.axonframework.serialization.upcasting.event;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import junit.framework.TestCase;
import org.axonframework.eventhandling.EventData;
import org.axonframework.eventhandling.GenericDomainEventMessage;
import org.axonframework.messaging.MetaData;
import org.axonframework.serialization.SerializedType;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.SimpleSerializedType;
import org.axonframework.serialization.upcasting.Upcaster;
import org.axonframework.utils.SecondStubEvent;
import org.axonframework.utils.StubDomainEvent;
import org.axonframework.utils.TestDomainEventEntry;
import org.axonframework.utils.ThirdStubEvent;
import org.junit.Test;


/* This test class should only assert whether the context map is created, filled with data and if that data is used to
upcast an event.
The other upcaster regularities are already asserted by the EventMultiUpcasterTest and can thus be skipped.
 */
public class AbstractContextAwareEventMultiUpcasterTest {
    private Upcaster<IntermediateEventRepresentation> upcaster;

    private Serializer serializer;

    private String expectedNewString;

    private Integer expectedNewInteger;

    private List<Boolean> expectedNewBooleans;

    @Test
    public void testUpcastsAddsContextValueFromFirstEvent() {
        int expectedNumberOfEvents = 4;
        String expectedContextEventString = "oldName";
        Integer expectedContextEventNumber = 1;
        String expectedRevisionNumber = "1";
        String expectedNewString = (this.expectedNewString) + (AbstractContextAwareEventMultiUpcasterTest.StubContextAwareEventMultiUpcaster.CONTEXT_FIELD_VALUE);
        MetaData testMetaData = MetaData.with("key", "value");
        GenericDomainEventMessage<SecondStubEvent> firstTestEventMessage = new GenericDomainEventMessage("test", "aggregateId", 0, new SecondStubEvent(expectedContextEventString, expectedContextEventNumber), testMetaData);
        EventData<?> firstTestEventData = new TestDomainEventEntry(firstTestEventMessage, serializer);
        InitialEventRepresentation firstTestRepresentation = new InitialEventRepresentation(firstTestEventData, serializer);
        GenericDomainEventMessage<StubDomainEvent> secondTestEventMessage = new GenericDomainEventMessage("test", "aggregateId", 0, new StubDomainEvent("oldName"), testMetaData);
        EventData<?> secondTestEventData = new TestDomainEventEntry(secondTestEventMessage, serializer);
        InitialEventRepresentation secondTestRepresentation = new InitialEventRepresentation(secondTestEventData, serializer);
        Stream<IntermediateEventRepresentation> testEventRepresentationStream = Stream.of(firstTestRepresentation, secondTestRepresentation);
        List<IntermediateEventRepresentation> result = upcaster.upcast(testEventRepresentationStream).collect(Collectors.toList());
        TestCase.assertEquals(expectedNumberOfEvents, result.size());
        IntermediateEventRepresentation firstEventResult = result.get(0);
        TestCase.assertNull(firstEventResult.getType().getRevision());
        TestCase.assertEquals(firstTestEventData.getEventIdentifier(), firstEventResult.getMessageIdentifier());
        TestCase.assertEquals(firstTestEventData.getTimestamp(), firstEventResult.getTimestamp());
        TestCase.assertEquals(testMetaData, firstEventResult.getMetaData().getObject());
        SecondStubEvent contextEvent = serializer.deserialize(firstEventResult.getData());
        TestCase.assertEquals(expectedContextEventString, contextEvent.getName());
        TestCase.assertEquals(expectedContextEventNumber, contextEvent.getNumber());
        IntermediateEventRepresentation secondEventResult = result.get(1);
        TestCase.assertEquals(expectedRevisionNumber, secondEventResult.getType().getRevision());
        TestCase.assertEquals(secondTestEventData.getEventIdentifier(), secondEventResult.getMessageIdentifier());
        TestCase.assertEquals(secondTestEventData.getTimestamp(), secondEventResult.getTimestamp());
        TestCase.assertEquals(testMetaData, secondEventResult.getMetaData().getObject());
        StubDomainEvent firstUpcastedEvent = serializer.deserialize(secondEventResult.getData());
        TestCase.assertEquals(expectedNewString, firstUpcastedEvent.getName());
        IntermediateEventRepresentation thirdEventResult = result.get(2);
        TestCase.assertNull(thirdEventResult.getType().getRevision());
        TestCase.assertEquals(secondTestEventData.getEventIdentifier(), thirdEventResult.getMessageIdentifier());
        TestCase.assertEquals(secondTestEventData.getTimestamp(), thirdEventResult.getTimestamp());
        TestCase.assertEquals(testMetaData, thirdEventResult.getMetaData().getObject());
        SecondStubEvent secondUpcastedEvent = serializer.deserialize(thirdEventResult.getData());
        TestCase.assertEquals(expectedNewString, secondUpcastedEvent.getName());
        TestCase.assertEquals(expectedNewInteger, secondUpcastedEvent.getNumber());
        IntermediateEventRepresentation fourthEventResult = result.get(3);
        TestCase.assertNull(fourthEventResult.getType().getRevision());
        TestCase.assertEquals(secondTestEventData.getEventIdentifier(), fourthEventResult.getMessageIdentifier());
        TestCase.assertEquals(secondTestEventData.getTimestamp(), fourthEventResult.getTimestamp());
        TestCase.assertEquals(testMetaData, fourthEventResult.getMetaData().getObject());
        ThirdStubEvent thirdUpcastedEvent = serializer.deserialize(fourthEventResult.getData());
        TestCase.assertEquals(expectedNewString, thirdUpcastedEvent.getName());
        TestCase.assertEquals(expectedNewInteger, thirdUpcastedEvent.getNumber());
        TestCase.assertEquals(expectedNewBooleans, thirdUpcastedEvent.getTruths());
    }

    private static class StubContextAwareEventMultiUpcaster extends ContextAwareEventMultiUpcaster<Map<Object, Object>> {
        private static final String CONTEXT_FIELD_KEY = "ContextField";

        static final String CONTEXT_FIELD_VALUE = "ContextAdded";

        private final SerializedType contextType = new SimpleSerializedType(SecondStubEvent.class.getName(), null);

        private final SerializedType targetType = new SimpleSerializedType(StubDomainEvent.class.getName(), null);

        private final String newStringValue;

        private final Integer newIntegerValue;

        private final List<Boolean> newBooleanValues;

        private StubContextAwareEventMultiUpcaster(String newStringValue, Integer newIntegerValue, List<Boolean> newBooleanValues) {
            this.newStringValue = newStringValue;
            this.newIntegerValue = newIntegerValue;
            this.newBooleanValues = newBooleanValues;
        }

        @Override
        protected boolean canUpcast(IntermediateEventRepresentation intermediateRepresentation, Map<Object, Object> context) {
            return (isType(intermediateRepresentation.getType(), targetType)) || (isType(intermediateRepresentation.getType(), contextType));
        }

        private boolean isType(SerializedType foundType, SerializedType expectedType) {
            return foundType.equals(expectedType);
        }

        @Override
        protected Stream<IntermediateEventRepresentation> doUpcast(IntermediateEventRepresentation ir, Map<Object, Object> context) {
            if (isContextEvent(ir)) {
                context.put(AbstractContextAwareEventMultiUpcasterTest.StubContextAwareEventMultiUpcaster.CONTEXT_FIELD_KEY, AbstractContextAwareEventMultiUpcasterTest.StubContextAwareEventMultiUpcaster.CONTEXT_FIELD_VALUE);
                return Stream.of(ir);
            }
            return Stream.of(ir.upcastPayload(new SimpleSerializedType(targetType.getName(), "1"), JsonNode.class, ( jsonNode) -> doUpcast(jsonNode, context)), ir.upcastPayload(new SimpleSerializedType(SecondStubEvent.class.getName(), null), JsonNode.class, ( jsonNode) -> doUpcastTwo(jsonNode, context)), ir.upcastPayload(new SimpleSerializedType(ThirdStubEvent.class.getName(), null), JsonNode.class, ( jsonNode) -> doUpcastThree(jsonNode, context)));
        }

        private boolean isContextEvent(IntermediateEventRepresentation intermediateRepresentation) {
            return isType(intermediateRepresentation.getType(), contextType);
        }

        @Override
        protected Map<Object, Object> buildContext() {
            return new HashMap<>();
        }

        private JsonNode doUpcast(JsonNode eventJsonNode, Map<Object, Object> context) {
            if (!(eventJsonNode.isObject())) {
                return eventJsonNode;
            }
            ObjectNode eventObjectNode = ((ObjectNode) (eventJsonNode));
            eventObjectNode.set("name", new TextNode(((newStringValue) + (context.get(AbstractContextAwareEventMultiUpcasterTest.StubContextAwareEventMultiUpcaster.CONTEXT_FIELD_KEY)))));
            return eventObjectNode;
        }

        private JsonNode doUpcastTwo(JsonNode eventJsonNode, Map<Object, Object> context) {
            if (!(eventJsonNode.isObject())) {
                return eventJsonNode;
            }
            ObjectNode eventObjectNode = ((ObjectNode) (eventJsonNode));
            eventObjectNode.set("name", new TextNode(((newStringValue) + (context.get(AbstractContextAwareEventMultiUpcasterTest.StubContextAwareEventMultiUpcaster.CONTEXT_FIELD_KEY)))));
            eventObjectNode.set("number", new IntNode(newIntegerValue));
            return eventJsonNode;
        }

        private JsonNode doUpcastThree(JsonNode eventJsonNode, Map<Object, Object> context) {
            if (!(eventJsonNode.isObject())) {
                return eventJsonNode;
            }
            ObjectNode eventObjectNode = ((ObjectNode) (eventJsonNode));
            eventObjectNode.set("name", new TextNode(((newStringValue) + (context.get(AbstractContextAwareEventMultiUpcasterTest.StubContextAwareEventMultiUpcaster.CONTEXT_FIELD_KEY)))));
            eventObjectNode.set("number", new IntNode(newIntegerValue));
            ArrayNode truthsArrayNode = eventObjectNode.withArray("truths");
            newBooleanValues.forEach(truthsArrayNode::add);
            return eventJsonNode;
        }
    }
}

