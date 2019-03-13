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
package org.axonframework.spring.messaging;


import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.EventMessage;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created on 04/02/17.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultEventMessageConverterTest {
    private EventMessageConverter eventMessageConverter = new DefaultEventMessageConverter();

    @Test
    public void given_generic_event_message_when_converting_twice_then_resulting_event_should_be_the_same() {
        Instant instant = Instant.EPOCH;
        String id = UUID.randomUUID().toString();
        Map<String, Object> metaData = new HashMap<>();
        metaData.put("number", 100);
        metaData.put("string", "world");
        DefaultEventMessageConverterTest.EventPayload payload = new DefaultEventMessageConverterTest.EventPayload("hello");
        EventMessage<DefaultEventMessageConverterTest.EventPayload> axonMessage = new org.axonframework.eventhandling.GenericEventMessage(id, payload, metaData, instant);
        EventMessage<DefaultEventMessageConverterTest.EventPayload> convertedAxonMessage = eventMessageConverter.convertFromInboundMessage(eventMessageConverter.convertToOutboundMessage(axonMessage));
        Assert.assertEquals(instant, convertedAxonMessage.getTimestamp());
        Assert.assertEquals(100, convertedAxonMessage.getMetaData().get("number"));
        Assert.assertEquals("world", convertedAxonMessage.getMetaData().get("string"));
        Assert.assertEquals("hello", convertedAxonMessage.getPayload().name);
        Assert.assertEquals(id, convertedAxonMessage.getIdentifier());
    }

    @Test
    public void given_domain_event_message_when_converting_twice_then_resulting_event_should_be_the_same() {
        Instant instant = Instant.EPOCH;
        String id = UUID.randomUUID().toString();
        String aggId = UUID.randomUUID().toString();
        Map<String, Object> metaData = new HashMap<>();
        metaData.put("number", 100);
        metaData.put("string", "world");
        DefaultEventMessageConverterTest.EventPayload payload = new DefaultEventMessageConverterTest.EventPayload("hello");
        EventMessage<DefaultEventMessageConverterTest.EventPayload> axonMessage = new org.axonframework.eventhandling.GenericDomainEventMessage("foo", aggId, 1, payload, metaData, id, instant);
        EventMessage<DefaultEventMessageConverterTest.EventPayload> convertedAxonMessage = eventMessageConverter.convertFromInboundMessage(eventMessageConverter.convertToOutboundMessage(axonMessage));
        Assert.assertTrue((convertedAxonMessage instanceof DomainEventMessage));
        DomainEventMessage<DefaultEventMessageConverterTest.EventPayload> convertDomainMessage = ((DomainEventMessage<DefaultEventMessageConverterTest.EventPayload>) (convertedAxonMessage));
        Assert.assertEquals(instant, convertDomainMessage.getTimestamp());
        Assert.assertEquals(100, convertDomainMessage.getMetaData().get("number"));
        Assert.assertEquals("world", convertDomainMessage.getMetaData().get("string"));
        Assert.assertEquals("hello", convertDomainMessage.getPayload().name);
        Assert.assertEquals(id, convertDomainMessage.getIdentifier());
        Assert.assertEquals("foo", convertDomainMessage.getType());
        Assert.assertEquals(aggId, convertDomainMessage.getAggregateIdentifier());
        Assert.assertEquals(1, convertDomainMessage.getSequenceNumber());
    }

    private class EventPayload {
        private final String name;

        EventPayload(String name) {
            this.name = name;
        }
    }
}

