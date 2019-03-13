/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.salesforce;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.camel.AsyncProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.component.salesforce.api.dto.PlatformEvent;
import org.apache.camel.component.salesforce.internal.streaming.SubscriptionHelper;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.common.HashMapMessage;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class SalesforceConsumerTest {
    public static class AccountUpdates {
        @JsonProperty("Id")
        String id;

        @JsonProperty("Name")
        String name;

        @JsonProperty("Phone")
        String phone;

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SalesforceConsumerTest.AccountUpdates)) {
                return false;
            }
            final SalesforceConsumerTest.AccountUpdates other = ((SalesforceConsumerTest.AccountUpdates) (obj));
            return ((Objects.equals(id, other.id)) && (Objects.equals(name, other.name))) && (Objects.equals(phone, other.phone));
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, phone);
        }
    }

    static final SubscriptionHelper NOT_USED = null;

    SalesforceEndpointConfig configuration = new SalesforceEndpointConfig();

    SalesforceEndpoint endpoint = Mockito.mock(SalesforceEndpoint.class);

    Exchange exchange = Mockito.mock(Exchange.class);

    Message in = Mockito.mock(Message.class);

    AsyncProcessor processor = Mockito.mock(AsyncProcessor.class);

    Message pushTopicMessage;

    @Test
    public void shouldProcessMappedPayloadPushTopicMessages() throws Exception {
        Mockito.when(endpoint.getTopicName()).thenReturn("AccountUpdates");
        configuration.setSObjectClass(SalesforceConsumerTest.AccountUpdates.class.getName());
        final SalesforceConsumer consumer = new SalesforceConsumer(endpoint, processor, SalesforceConsumerTest.NOT_USED);
        consumer.processMessage(Mockito.mock(ClientSessionChannel.class), pushTopicMessage);
        final SalesforceConsumerTest.AccountUpdates accountUpdates = new SalesforceConsumerTest.AccountUpdates();
        accountUpdates.phone = "(415) 555-1212";
        accountUpdates.id = "001D000000KneakIAB";
        accountUpdates.name = "Blackbeard";
        Mockito.verify(in).setBody(accountUpdates);
        Mockito.verify(in).setHeader("CamelSalesforceEventType", "created");
        Mockito.verify(in).setHeader("CamelSalesforceCreatedDate", "2016-09-16T19:45:27.454Z");
        Mockito.verify(in).setHeader("CamelSalesforceReplayId", 1L);
        Mockito.verify(in).setHeader("CamelSalesforceTopicName", "AccountUpdates");
        Mockito.verify(in).setHeader("CamelSalesforceChannel", "/topic/AccountUpdates");
        Mockito.verify(in).setHeader("CamelSalesforceClientId", "lxdl9o32njygi1gj47kgfaga4k");
        Mockito.verify(processor).process(ArgumentMatchers.same(exchange), ArgumentMatchers.any());
    }

    @Test
    public void shouldProcessPlatformEvents() throws Exception {
        Mockito.when(endpoint.getTopicName()).thenReturn("/event/TestEvent__e");
        final Message message = new HashMapMessage();
        final Map<String, Object> data = new HashMap<>();
        data.put("schema", "30H2pgzuWcF844p26Ityvg");
        final Map<String, Object> payload = new HashMap<>();
        payload.put("Test_Field__c", "abc");
        payload.put("CreatedById", "00541000002WYFpAAO");
        payload.put("CreatedDate", "2018-07-06T12:41:04Z");
        data.put("payload", payload);
        data.put("event", Collections.singletonMap("replayId", 4L));
        message.put("data", data);
        message.put("channel", "/event/TestEvent__e");
        final SalesforceConsumer consumer = new SalesforceConsumer(endpoint, processor, SalesforceConsumerTest.NOT_USED);
        consumer.processMessage(Mockito.mock(ClientSessionChannel.class), message);
        final ZonedDateTime created = ZonedDateTime.parse("2018-07-06T12:41:04Z");
        final PlatformEvent event = new PlatformEvent(created, "00541000002WYFpAAO");
        event.set("Test_Field__c", "abc");
        Mockito.verify(in).setBody(event);
        Mockito.verify(in).setHeader("CamelSalesforceCreatedDate", created);
        Mockito.verify(in).setHeader("CamelSalesforceReplayId", 4L);
        Mockito.verify(in).setHeader("CamelSalesforceChannel", "/event/TestEvent__e");
        Mockito.verify(in).setHeader("CamelSalesforceEventType", "TestEvent__e");
        Mockito.verify(in).setHeader("CamelSalesforcePlatformEventSchema", "30H2pgzuWcF844p26Ityvg");
        Mockito.verify(processor).process(ArgumentMatchers.same(exchange), ArgumentMatchers.any());
        Mockito.verifyNoMoreInteractions(in, processor);
    }

    @Test
    public void shouldProcessPushTopicMessages() throws Exception {
        Mockito.when(endpoint.getTopicName()).thenReturn("AccountUpdates");
        final SalesforceConsumer consumer = new SalesforceConsumer(endpoint, processor, SalesforceConsumerTest.NOT_USED);
        consumer.processMessage(Mockito.mock(ClientSessionChannel.class), pushTopicMessage);
        @SuppressWarnings("unchecked")
        final Object sobject = ((Map<String, Object>) (pushTopicMessage.get("data"))).get("sobject");
        Mockito.verify(in).setBody(sobject);
        Mockito.verify(in).setHeader("CamelSalesforceEventType", "created");
        Mockito.verify(in).setHeader("CamelSalesforceCreatedDate", "2016-09-16T19:45:27.454Z");
        Mockito.verify(in).setHeader("CamelSalesforceReplayId", 1L);
        Mockito.verify(in).setHeader("CamelSalesforceTopicName", "AccountUpdates");
        Mockito.verify(in).setHeader("CamelSalesforceChannel", "/topic/AccountUpdates");
        Mockito.verify(in).setHeader("CamelSalesforceClientId", "lxdl9o32njygi1gj47kgfaga4k");
        Mockito.verify(processor).process(ArgumentMatchers.same(exchange), ArgumentMatchers.any());
    }

    @Test
    public void shouldProcessRawPayloadPushTopicMessages() throws Exception {
        Mockito.when(endpoint.getTopicName()).thenReturn("AccountUpdates");
        configuration.setRawPayload(true);
        final SalesforceConsumer consumer = new SalesforceConsumer(endpoint, processor, SalesforceConsumerTest.NOT_USED);
        consumer.processMessage(Mockito.mock(ClientSessionChannel.class), pushTopicMessage);
        Mockito.verify(in).setBody("{\"Phone\":\"(415) 555-1212\",\"Id\":\"001D000000KneakIAB\",\"Name\":\"Blackbeard\"}");
        Mockito.verify(in).setHeader("CamelSalesforceEventType", "created");
        Mockito.verify(in).setHeader("CamelSalesforceCreatedDate", "2016-09-16T19:45:27.454Z");
        Mockito.verify(in).setHeader("CamelSalesforceReplayId", 1L);
        Mockito.verify(in).setHeader("CamelSalesforceTopicName", "AccountUpdates");
        Mockito.verify(in).setHeader("CamelSalesforceChannel", "/topic/AccountUpdates");
        Mockito.verify(in).setHeader("CamelSalesforceClientId", "lxdl9o32njygi1gj47kgfaga4k");
        Mockito.verify(processor).process(ArgumentMatchers.same(exchange), ArgumentMatchers.any());
    }

    @Test
    public void shouldProcessRawPlatformEvents() throws Exception {
        Mockito.when(endpoint.getTopicName()).thenReturn("/event/TestEvent__e");
        configuration.setRawPayload(true);
        final Message message = new HashMapMessage();
        final Map<String, Object> data = new HashMap<>();
        data.put("schema", "30H2pgzuWcF844p26Ityvg");
        final Map<String, Object> payload = new HashMap<>();
        payload.put("Test_Field__c", "abc");
        payload.put("CreatedById", "00541000002WYFpAAO");
        payload.put("CreatedDate", "2018-07-06T12:41:04Z");
        data.put("payload", payload);
        data.put("event", Collections.singletonMap("replayId", 4L));
        message.put("data", data);
        message.put("channel", "/event/TestEvent__e");
        final SalesforceConsumer consumer = new SalesforceConsumer(endpoint, processor, SalesforceConsumerTest.NOT_USED);
        consumer.processMessage(Mockito.mock(ClientSessionChannel.class), message);
        Mockito.verify(in).setBody(message);
        Mockito.verify(in).setHeader("CamelSalesforceCreatedDate", ZonedDateTime.parse("2018-07-06T12:41:04Z"));
        Mockito.verify(in).setHeader("CamelSalesforceReplayId", 4L);
        Mockito.verify(in).setHeader("CamelSalesforceChannel", "/event/TestEvent__e");
        Mockito.verify(in).setHeader("CamelSalesforceEventType", "TestEvent__e");
        Mockito.verify(in).setHeader("CamelSalesforcePlatformEventSchema", "30H2pgzuWcF844p26Ityvg");
        Mockito.verify(processor).process(ArgumentMatchers.same(exchange), ArgumentMatchers.any());
        Mockito.verifyNoMoreInteractions(in, processor);
    }
}

