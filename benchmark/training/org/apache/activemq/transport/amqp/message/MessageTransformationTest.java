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
package org.apache.activemq.transport.amqp.message;


import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.transport.amqp.JMSInteroperabilityTest;
import org.apache.qpid.proton.Proton;
import org.apache.qpid.proton.amqp.Symbol;
import org.apache.qpid.proton.amqp.messaging.AmqpValue;
import org.apache.qpid.proton.amqp.messaging.ApplicationProperties;
import org.apache.qpid.proton.amqp.messaging.MessageAnnotations;
import org.apache.qpid.proton.amqp.messaging.Section;
import org.apache.qpid.proton.message.Message;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests some basic encode / decode functionality on the transformers.
 */
@RunWith(Parameterized.class)
public class MessageTransformationTest {
    protected static final Logger LOG = LoggerFactory.getLogger(JMSInteroperabilityTest.class);

    @Rule
    public TestName test = new TestName();

    private final String transformer;

    public MessageTransformationTest(String transformer) {
        this.transformer = transformer;
    }

    @Test
    public void testEncodeDecodeFidelity() throws Exception {
        Map<String, Object> applicationProperties = new HashMap<String, Object>();
        Map<Symbol, Object> messageAnnotations = new HashMap<Symbol, Object>();
        applicationProperties.put("property-1", "string");
        applicationProperties.put("property-2", 512);
        applicationProperties.put("property-3", true);
        messageAnnotations.put(Symbol.valueOf("x-opt-jms-msg-type"), 0);
        messageAnnotations.put(Symbol.valueOf("x-opt-jms-dest"), 0);
        Message incomingMessage = Proton.message();
        incomingMessage.setAddress("queue://test-queue");
        incomingMessage.setDeliveryCount(1);
        incomingMessage.setApplicationProperties(new ApplicationProperties(applicationProperties));
        incomingMessage.setMessageAnnotations(new MessageAnnotations(messageAnnotations));
        incomingMessage.setCreationTime(System.currentTimeMillis());
        incomingMessage.setContentType("text/plain");
        incomingMessage.setBody(new AmqpValue("String payload for AMQP message conversion performance testing."));
        EncodedMessage encoded = encode(incomingMessage);
        InboundTransformer inboundTransformer = getInboundTransformer();
        OutboundTransformer outboundTransformer = getOutboundTransformer();
        ActiveMQMessage outbound = inboundTransformer.transform(encoded);
        outbound.onSend();
        Message outboudMessage = outboundTransformer.transform(outbound).decode();
        // Test that message details are equal
        Assert.assertEquals(incomingMessage.getAddress(), outboudMessage.getAddress());
        Assert.assertEquals(incomingMessage.getDeliveryCount(), outboudMessage.getDeliveryCount());
        Assert.assertEquals(incomingMessage.getCreationTime(), outboudMessage.getCreationTime());
        Assert.assertEquals(incomingMessage.getContentType(), outboudMessage.getContentType());
        // Test Message annotations
        ApplicationProperties incomingApplicationProperties = incomingMessage.getApplicationProperties();
        ApplicationProperties outgoingApplicationProperties = outboudMessage.getApplicationProperties();
        Assert.assertEquals(incomingApplicationProperties.getValue(), outgoingApplicationProperties.getValue());
        // Test Message properties
        MessageAnnotations incomingMessageAnnotations = incomingMessage.getMessageAnnotations();
        MessageAnnotations outgoingMessageAnnotations = outboudMessage.getMessageAnnotations();
        Assert.assertEquals(incomingMessageAnnotations.getValue(), outgoingMessageAnnotations.getValue());
        // Test that bodies are equal
        Assert.assertTrue(((incomingMessage.getBody()) instanceof AmqpValue));
        Assert.assertTrue(((outboudMessage.getBody()) instanceof AmqpValue));
        AmqpValue incomingBody = ((AmqpValue) (incomingMessage.getBody()));
        AmqpValue outgoingBody = ((AmqpValue) (outboudMessage.getBody()));
        Assert.assertTrue(((incomingBody.getValue()) instanceof String));
        Assert.assertTrue(((outgoingBody.getValue()) instanceof String));
        Assert.assertEquals(incomingBody.getValue(), outgoingBody.getValue());
    }

    @Test
    public void testBodyOnlyEncodeDecode() throws Exception {
        Message incomingMessage = Proton.message();
        incomingMessage.setBody(new AmqpValue("String payload for AMQP message conversion performance testing."));
        EncodedMessage encoded = encode(incomingMessage);
        InboundTransformer inboundTransformer = getInboundTransformer();
        OutboundTransformer outboundTransformer = getOutboundTransformer();
        ActiveMQMessage intermediate = inboundTransformer.transform(encoded);
        intermediate.onSend();
        Message outboudMessage = outboundTransformer.transform(intermediate).decode();
        Assert.assertNull(outboudMessage.getHeader());
        Assert.assertNull(outboudMessage.getProperties());
    }

    @Test
    public void testPropertiesButNoHeadersEncodeDecode() throws Exception {
        Message incomingMessage = Proton.message();
        incomingMessage.setBody(new AmqpValue("String payload for AMQP message conversion performance testing."));
        incomingMessage.setMessageId("ID:SomeQualifier:0:0:1");
        EncodedMessage encoded = encode(incomingMessage);
        InboundTransformer inboundTransformer = getInboundTransformer();
        OutboundTransformer outboundTransformer = getOutboundTransformer();
        ActiveMQMessage intermediate = inboundTransformer.transform(encoded);
        intermediate.onSend();
        Message outboudMessage = outboundTransformer.transform(intermediate).decode();
        Assert.assertNull(outboudMessage.getHeader());
        Assert.assertNotNull(outboudMessage.getProperties());
    }

    @Test
    public void testHeaderButNoPropertiesEncodeDecode() throws Exception {
        Message incomingMessage = Proton.message();
        incomingMessage.setBody(new AmqpValue("String payload for AMQP message conversion performance testing."));
        incomingMessage.setDurable(true);
        EncodedMessage encoded = encode(incomingMessage);
        InboundTransformer inboundTransformer = getInboundTransformer();
        OutboundTransformer outboundTransformer = getOutboundTransformer();
        ActiveMQMessage intermediate = inboundTransformer.transform(encoded);
        intermediate.onSend();
        Message outboudMessage = outboundTransformer.transform(intermediate).decode();
        Assert.assertNotNull(outboudMessage.getHeader());
        Assert.assertNull(outboudMessage.getProperties());
    }

    @Test
    public void testMessageWithAmqpValueThatFailsJMSConversion() throws Exception {
        Message incomingMessage = Proton.message();
        incomingMessage.setBody(new AmqpValue(new Boolean(true)));
        EncodedMessage encoded = encode(incomingMessage);
        InboundTransformer inboundTransformer = getInboundTransformer();
        OutboundTransformer outboundTransformer = getOutboundTransformer();
        ActiveMQMessage intermediate = inboundTransformer.transform(encoded);
        intermediate.onSend();
        Message outboudMessage = outboundTransformer.transform(intermediate).decode();
        Section section = outboudMessage.getBody();
        Assert.assertNotNull(section);
        Assert.assertTrue((section instanceof AmqpValue));
        AmqpValue amqpValue = ((AmqpValue) (section));
        Assert.assertNotNull(amqpValue.getValue());
        Assert.assertTrue(((amqpValue.getValue()) instanceof Boolean));
        Assert.assertEquals(true, amqpValue.getValue());
    }

    @Test
    public void testComplexQpidJMSMessageEncodeDecode() throws Exception {
        Map<String, Object> applicationProperties = new HashMap<String, Object>();
        Map<Symbol, Object> messageAnnotations = new HashMap<Symbol, Object>();
        applicationProperties.put("property-1", "string-1");
        applicationProperties.put("property-2", 512);
        applicationProperties.put("property-3", true);
        applicationProperties.put("property-4", "string-2");
        applicationProperties.put("property-5", 512);
        applicationProperties.put("property-6", true);
        applicationProperties.put("property-7", "string-3");
        applicationProperties.put("property-8", 512);
        applicationProperties.put("property-9", true);
        messageAnnotations.put(Symbol.valueOf("x-opt-jms-msg-type"), 0);
        messageAnnotations.put(Symbol.valueOf("x-opt-jms-dest"), 0);
        messageAnnotations.put(Symbol.valueOf("x-opt-jms-reply-to"), 0);
        messageAnnotations.put(Symbol.valueOf("x-opt-delivery-delay"), 2000);
        Message message = Proton.message();
        // Header Values
        message.setPriority(((short) (9)));
        message.setDurable(true);
        message.setDeliveryCount(2);
        message.setTtl(5000);
        // Properties
        message.setMessageId("ID:SomeQualifier:0:0:1");
        message.setGroupId("Group-ID-1");
        message.setGroupSequence(15);
        message.setAddress("queue://test-queue");
        message.setReplyTo("queue://reply-queue");
        message.setCreationTime(System.currentTimeMillis());
        message.setContentType("text/plain");
        message.setCorrelationId("ID:SomeQualifier:0:7:9");
        message.setUserId("username".getBytes(StandardCharsets.UTF_8));
        // Application Properties / Message Annotations / Body
        message.setApplicationProperties(new ApplicationProperties(applicationProperties));
        message.setMessageAnnotations(new MessageAnnotations(messageAnnotations));
        message.setBody(new AmqpValue("String payload for AMQP message conversion performance testing."));
        EncodedMessage encoded = encode(message);
        InboundTransformer inboundTransformer = getInboundTransformer();
        OutboundTransformer outboundTransformer = getOutboundTransformer();
        ActiveMQMessage intermediate = inboundTransformer.transform(encoded);
        intermediate.onSend();
        Message outboudMessage = outboundTransformer.transform(intermediate).decode();
        Assert.assertNotNull(outboudMessage.getHeader());
        Assert.assertNotNull(outboudMessage.getProperties());
        Assert.assertNotNull(outboudMessage.getMessageAnnotations());
        Assert.assertNotNull(outboudMessage.getApplicationProperties());
        Assert.assertNull(outboudMessage.getDeliveryAnnotations());
        Assert.assertNull(outboudMessage.getFooter());
        Assert.assertEquals(9, outboudMessage.getApplicationProperties().getValue().size());
        Assert.assertEquals(4, outboudMessage.getMessageAnnotations().getValue().size());
    }
}

