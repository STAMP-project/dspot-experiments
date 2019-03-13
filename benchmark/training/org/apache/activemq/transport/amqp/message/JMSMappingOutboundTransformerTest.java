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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.activemq.command.ActiveMQStreamMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.qpid.proton.amqp.Binary;
import org.apache.qpid.proton.amqp.messaging.AmqpSequence;
import org.apache.qpid.proton.amqp.messaging.AmqpValue;
import org.apache.qpid.proton.amqp.messaging.Data;
import org.apache.qpid.proton.message.Message;
import org.junit.Assert;
import org.junit.Test;


public class JMSMappingOutboundTransformerTest {
    private final UUID TEST_OBJECT_VALUE = UUID.fromString("fee14b62-09e0-4ac6-a4c3-4206c630d844");

    private final String TEST_ADDRESS = "queue://testAddress";

    // ----- no-body Message type tests ---------------------------------------//
    @Test
    public void testConvertMessageToAmqpMessageWithNoBody() throws Exception {
        ActiveMQMessage outbound = createMessage();
        outbound.onSend();
        outbound.storeContent();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNull(amqp.getBody());
    }

    @Test
    public void testConvertTextMessageToAmqpMessageWithNoBodyOriginalEncodingWasNull() throws Exception {
        ActiveMQTextMessage outbound = createTextMessage();
        outbound.setShortProperty(AmqpMessageSupport.JMS_AMQP_ORIGINAL_ENCODING, AmqpMessageSupport.AMQP_NULL);
        outbound.onSend();
        outbound.storeContent();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNull(amqp.getBody());
    }

    // ----- BytesMessage type tests ---------------------------------------//
    @Test
    public void testConvertEmptyBytesMessageToAmqpMessageWithDataBody() throws Exception {
        ActiveMQBytesMessage outbound = createBytesMessage();
        outbound.onSend();
        outbound.storeContent();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNotNull(amqp.getBody());
        Assert.assertTrue(((amqp.getBody()) instanceof Data));
        Assert.assertTrue(((getValue()) instanceof Binary));
        Assert.assertEquals(0, getLength());
    }

    @Test
    public void testConvertUncompressedBytesMessageToAmqpMessageWithDataBody() throws Exception {
        byte[] expectedPayload = new byte[]{ 8, 16, 24, 32 };
        ActiveMQBytesMessage outbound = createBytesMessage();
        outbound.writeBytes(expectedPayload);
        outbound.storeContent();
        outbound.onSend();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNotNull(amqp.getBody());
        Assert.assertTrue(((amqp.getBody()) instanceof Data));
        Assert.assertTrue(((getValue()) instanceof Binary));
        Assert.assertEquals(4, getLength());
        Binary amqpData = ((Data) (amqp.getBody())).getValue();
        Binary inputData = new Binary(expectedPayload);
        Assert.assertTrue(inputData.equals(amqpData));
    }

    @Test
    public void testConvertCompressedBytesMessageToAmqpMessageWithDataBody() throws Exception {
        byte[] expectedPayload = new byte[]{ 8, 16, 24, 32 };
        ActiveMQBytesMessage outbound = createBytesMessage(true);
        outbound.writeBytes(expectedPayload);
        outbound.storeContent();
        outbound.onSend();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNotNull(amqp.getBody());
        Assert.assertTrue(((amqp.getBody()) instanceof Data));
        Assert.assertTrue(((getValue()) instanceof Binary));
        Assert.assertEquals(4, getLength());
        Binary amqpData = ((Data) (amqp.getBody())).getValue();
        Binary inputData = new Binary(expectedPayload);
        Assert.assertTrue(inputData.equals(amqpData));
    }

    @Test
    public void testConvertEmptyBytesMessageToAmqpMessageWithAmqpValueBody() throws Exception {
        ActiveMQBytesMessage outbound = createBytesMessage();
        outbound.setShortProperty(AmqpMessageSupport.JMS_AMQP_ORIGINAL_ENCODING, AmqpMessageSupport.AMQP_VALUE_BINARY);
        outbound.onSend();
        outbound.storeContent();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNotNull(amqp.getBody());
        Assert.assertTrue(((amqp.getBody()) instanceof AmqpValue));
        Assert.assertTrue(((getValue()) instanceof Binary));
        Assert.assertEquals(0, getLength());
    }

    @Test
    public void testConvertUncompressedBytesMessageToAmqpMessageWithAmqpValueBody() throws Exception {
        byte[] expectedPayload = new byte[]{ 8, 16, 24, 32 };
        ActiveMQBytesMessage outbound = createBytesMessage();
        outbound.setShortProperty(AmqpMessageSupport.JMS_AMQP_ORIGINAL_ENCODING, AmqpMessageSupport.AMQP_VALUE_BINARY);
        outbound.writeBytes(expectedPayload);
        outbound.storeContent();
        outbound.onSend();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNotNull(amqp.getBody());
        Assert.assertTrue(((amqp.getBody()) instanceof AmqpValue));
        Assert.assertTrue(((getValue()) instanceof Binary));
        Assert.assertEquals(4, getLength());
        Binary amqpData = ((Binary) (getValue()));
        Binary inputData = new Binary(expectedPayload);
        Assert.assertTrue(inputData.equals(amqpData));
    }

    @Test
    public void testConvertCompressedBytesMessageToAmqpMessageWithAmqpValueBody() throws Exception {
        byte[] expectedPayload = new byte[]{ 8, 16, 24, 32 };
        ActiveMQBytesMessage outbound = createBytesMessage(true);
        outbound.setShortProperty(AmqpMessageSupport.JMS_AMQP_ORIGINAL_ENCODING, AmqpMessageSupport.AMQP_VALUE_BINARY);
        outbound.writeBytes(expectedPayload);
        outbound.storeContent();
        outbound.onSend();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNotNull(amqp.getBody());
        Assert.assertTrue(((amqp.getBody()) instanceof AmqpValue));
        Assert.assertTrue(((getValue()) instanceof Binary));
        Assert.assertEquals(4, getLength());
        Binary amqpData = ((Binary) (getValue()));
        Binary inputData = new Binary(expectedPayload);
        Assert.assertTrue(inputData.equals(amqpData));
    }

    // ----- MapMessage type tests --------------------------------------------//
    @Test
    public void testConvertMapMessageToAmqpMessageWithNoBody() throws Exception {
        ActiveMQMapMessage outbound = createMapMessage();
        outbound.onSend();
        outbound.storeContent();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNotNull(amqp.getBody());
        Assert.assertTrue(((amqp.getBody()) instanceof AmqpValue));
        Assert.assertTrue(((getValue()) instanceof Map));
    }

    @Test
    public void testConvertMapMessageToAmqpMessageWithByteArrayValueInBody() throws Exception {
        final byte[] byteArray = new byte[]{ 1, 2, 3, 4, 5 };
        ActiveMQMapMessage outbound = createMapMessage();
        outbound.setBytes("bytes", byteArray);
        outbound.onSend();
        outbound.storeContent();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNotNull(amqp.getBody());
        Assert.assertTrue(((amqp.getBody()) instanceof AmqpValue));
        Assert.assertTrue(((getValue()) instanceof Map));
        @SuppressWarnings("unchecked")
        Map<Object, Object> amqpMap = ((Map<Object, Object>) (getValue()));
        Assert.assertEquals(1, amqpMap.size());
        Binary readByteArray = ((Binary) (amqpMap.get("bytes")));
        Assert.assertNotNull(readByteArray);
    }

    @Test
    public void testConvertMapMessageToAmqpMessage() throws Exception {
        ActiveMQMapMessage outbound = createMapMessage();
        outbound.setString("property-1", "string");
        outbound.setInt("property-2", 1);
        outbound.setBoolean("property-3", true);
        outbound.onSend();
        outbound.storeContent();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNotNull(amqp.getBody());
        Assert.assertTrue(((amqp.getBody()) instanceof AmqpValue));
        Assert.assertTrue(((getValue()) instanceof Map));
        @SuppressWarnings("unchecked")
        Map<Object, Object> amqpMap = ((Map<Object, Object>) (getValue()));
        Assert.assertEquals(3, amqpMap.size());
        Assert.assertTrue("string".equals(amqpMap.get("property-1")));
    }

    @Test
    public void testConvertCompressedMapMessageToAmqpMessage() throws Exception {
        ActiveMQMapMessage outbound = createMapMessage(true);
        outbound.setString("property-1", "string");
        outbound.setInt("property-2", 1);
        outbound.setBoolean("property-3", true);
        outbound.onSend();
        outbound.storeContent();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNotNull(amqp.getBody());
        Assert.assertTrue(((amqp.getBody()) instanceof AmqpValue));
        Assert.assertTrue(((getValue()) instanceof Map));
        @SuppressWarnings("unchecked")
        Map<Object, Object> amqpMap = ((Map<Object, Object>) (getValue()));
        Assert.assertEquals(3, amqpMap.size());
        Assert.assertTrue("string".equals(amqpMap.get("property-1")));
    }

    // ----- StreamMessage type tests -----------------------------------------//
    @Test
    public void testConvertStreamMessageToAmqpMessageWithAmqpValueBody() throws Exception {
        ActiveMQStreamMessage outbound = createStreamMessage();
        outbound.onSend();
        outbound.storeContent();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNotNull(amqp.getBody());
        Assert.assertTrue(((amqp.getBody()) instanceof AmqpValue));
        Assert.assertTrue(((getValue()) instanceof List));
    }

    @Test
    public void testConvertStreamMessageToAmqpMessageWithAmqpSequencey() throws Exception {
        ActiveMQStreamMessage outbound = createStreamMessage();
        outbound.setShortProperty(AmqpMessageSupport.JMS_AMQP_ORIGINAL_ENCODING, AmqpMessageSupport.AMQP_SEQUENCE);
        outbound.onSend();
        outbound.storeContent();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNotNull(amqp.getBody());
        Assert.assertTrue(((amqp.getBody()) instanceof AmqpSequence));
        Assert.assertTrue(((getValue()) instanceof List));
    }

    @Test
    public void testConvertCompressedStreamMessageToAmqpMessageWithAmqpValueBody() throws Exception {
        ActiveMQStreamMessage outbound = createStreamMessage(true);
        outbound.writeBoolean(false);
        outbound.writeString("test");
        outbound.onSend();
        outbound.storeContent();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNotNull(amqp.getBody());
        Assert.assertTrue(((amqp.getBody()) instanceof AmqpValue));
        Assert.assertTrue(((getValue()) instanceof List));
        @SuppressWarnings("unchecked")
        List<Object> amqpList = ((List<Object>) (getValue()));
        Assert.assertEquals(2, amqpList.size());
    }

    @Test
    public void testConvertCompressedStreamMessageToAmqpMessageWithAmqpSequencey() throws Exception {
        ActiveMQStreamMessage outbound = createStreamMessage(true);
        outbound.setShortProperty(AmqpMessageSupport.JMS_AMQP_ORIGINAL_ENCODING, AmqpMessageSupport.AMQP_SEQUENCE);
        outbound.writeBoolean(false);
        outbound.writeString("test");
        outbound.onSend();
        outbound.storeContent();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNotNull(amqp.getBody());
        Assert.assertTrue(((amqp.getBody()) instanceof AmqpSequence));
        Assert.assertTrue(((getValue()) instanceof List));
        @SuppressWarnings("unchecked")
        List<Object> amqpList = ((AmqpSequence) (amqp.getBody())).getValue();
        Assert.assertEquals(2, amqpList.size());
    }

    // ----- ObjectMessage type tests -----------------------------------------//
    @Test
    public void testConvertEmptyObjectMessageToAmqpMessageWithDataBody() throws Exception {
        ActiveMQObjectMessage outbound = createObjectMessage();
        outbound.onSend();
        outbound.storeContent();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNotNull(amqp.getBody());
        Assert.assertTrue(((amqp.getBody()) instanceof Data));
        Assert.assertEquals(0, getLength());
    }

    @Test
    public void testConvertEmptyObjectMessageToAmqpMessageUnknownEncodingGetsDataSection() throws Exception {
        ActiveMQObjectMessage outbound = createObjectMessage();
        outbound.setShortProperty(AmqpMessageSupport.JMS_AMQP_ORIGINAL_ENCODING, AmqpMessageSupport.AMQP_UNKNOWN);
        outbound.onSend();
        outbound.storeContent();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNotNull(amqp.getBody());
        Assert.assertTrue(((amqp.getBody()) instanceof Data));
        Assert.assertEquals(0, getLength());
    }

    @Test
    public void testConvertEmptyObjectMessageToAmqpMessageWithAmqpValueBody() throws Exception {
        ActiveMQObjectMessage outbound = createObjectMessage();
        outbound.setShortProperty(AmqpMessageSupport.JMS_AMQP_ORIGINAL_ENCODING, AmqpMessageSupport.AMQP_VALUE_BINARY);
        outbound.onSend();
        outbound.storeContent();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNotNull(amqp.getBody());
        Assert.assertTrue(((amqp.getBody()) instanceof AmqpValue));
        Assert.assertTrue(((getValue()) instanceof Binary));
        Assert.assertEquals(0, getLength());
    }

    @Test
    public void testConvertObjectMessageToAmqpMessageWithDataBody() throws Exception {
        ActiveMQObjectMessage outbound = createObjectMessage(TEST_OBJECT_VALUE);
        outbound.onSend();
        outbound.storeContent();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNotNull(amqp.getBody());
        Assert.assertTrue(((amqp.getBody()) instanceof Data));
        Assert.assertFalse((0 == (getLength())));
        Object value = deserialize(getArray());
        Assert.assertNotNull(value);
        Assert.assertTrue((value instanceof UUID));
    }

    @Test
    public void testConvertObjectMessageToAmqpMessageUnknownEncodingGetsDataSection() throws Exception {
        ActiveMQObjectMessage outbound = createObjectMessage(TEST_OBJECT_VALUE);
        outbound.setShortProperty(AmqpMessageSupport.JMS_AMQP_ORIGINAL_ENCODING, AmqpMessageSupport.AMQP_UNKNOWN);
        outbound.onSend();
        outbound.storeContent();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNotNull(amqp.getBody());
        Assert.assertTrue(((amqp.getBody()) instanceof Data));
        Assert.assertFalse((0 == (getLength())));
        Object value = deserialize(getArray());
        Assert.assertNotNull(value);
        Assert.assertTrue((value instanceof UUID));
    }

    @Test
    public void testConvertObjectMessageToAmqpMessageWithAmqpValueBody() throws Exception {
        ActiveMQObjectMessage outbound = createObjectMessage(TEST_OBJECT_VALUE);
        outbound.setShortProperty(AmqpMessageSupport.JMS_AMQP_ORIGINAL_ENCODING, AmqpMessageSupport.AMQP_VALUE_BINARY);
        outbound.onSend();
        outbound.storeContent();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNotNull(amqp.getBody());
        Assert.assertTrue(((amqp.getBody()) instanceof AmqpValue));
        Assert.assertTrue(((getValue()) instanceof Binary));
        Assert.assertFalse((0 == (getLength())));
        Object value = deserialize(getArray());
        Assert.assertNotNull(value);
        Assert.assertTrue((value instanceof UUID));
    }

    @Test
    public void testConvertCompressedObjectMessageToAmqpMessageWithDataBody() throws Exception {
        ActiveMQObjectMessage outbound = createObjectMessage(TEST_OBJECT_VALUE, true);
        outbound.onSend();
        outbound.storeContent();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNotNull(amqp.getBody());
        Assert.assertTrue(((amqp.getBody()) instanceof Data));
        Assert.assertFalse((0 == (getLength())));
        Object value = deserialize(getArray());
        Assert.assertNotNull(value);
        Assert.assertTrue((value instanceof UUID));
    }

    @Test
    public void testConvertCompressedObjectMessageToAmqpMessageUnknownEncodingGetsDataSection() throws Exception {
        ActiveMQObjectMessage outbound = createObjectMessage(TEST_OBJECT_VALUE, true);
        outbound.setShortProperty(AmqpMessageSupport.JMS_AMQP_ORIGINAL_ENCODING, AmqpMessageSupport.AMQP_UNKNOWN);
        outbound.onSend();
        outbound.storeContent();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNotNull(amqp.getBody());
        Assert.assertTrue(((amqp.getBody()) instanceof Data));
        Assert.assertFalse((0 == (getLength())));
        Object value = deserialize(getArray());
        Assert.assertNotNull(value);
        Assert.assertTrue((value instanceof UUID));
    }

    @Test
    public void testConvertCompressedObjectMessageToAmqpMessageWithAmqpValueBody() throws Exception {
        ActiveMQObjectMessage outbound = createObjectMessage(TEST_OBJECT_VALUE, true);
        outbound.setShortProperty(AmqpMessageSupport.JMS_AMQP_ORIGINAL_ENCODING, AmqpMessageSupport.AMQP_VALUE_BINARY);
        outbound.onSend();
        outbound.storeContent();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNotNull(amqp.getBody());
        Assert.assertTrue(((amqp.getBody()) instanceof AmqpValue));
        Assert.assertTrue(((getValue()) instanceof Binary));
        Assert.assertFalse((0 == (getLength())));
        Object value = deserialize(getArray());
        Assert.assertNotNull(value);
        Assert.assertTrue((value instanceof UUID));
    }

    // ----- TextMessage type tests -------------------------------------------//
    @Test
    public void testConvertTextMessageToAmqpMessageWithNoBody() throws Exception {
        ActiveMQTextMessage outbound = createTextMessage();
        outbound.onSend();
        outbound.storeContent();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNotNull(amqp.getBody());
        Assert.assertTrue(((amqp.getBody()) instanceof AmqpValue));
        Assert.assertNull(getValue());
    }

    @Test
    public void testConvertTextMessageCreatesBodyUsingOriginalEncodingWithDataSection() throws Exception {
        String contentString = "myTextMessageContent";
        ActiveMQTextMessage outbound = createTextMessage(contentString);
        outbound.setShortProperty(AmqpMessageSupport.JMS_AMQP_ORIGINAL_ENCODING, AmqpMessageSupport.AMQP_DATA);
        outbound.onSend();
        outbound.storeContent();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNotNull(amqp.getBody());
        Assert.assertTrue(((amqp.getBody()) instanceof Data));
        Assert.assertTrue(((getValue()) instanceof Binary));
        Binary data = ((Data) (amqp.getBody())).getValue();
        String contents = new String(data.getArray(), data.getArrayOffset(), data.getLength(), StandardCharsets.UTF_8);
        Assert.assertEquals(contentString, contents);
    }

    @Test
    public void testConvertTextMessageContentNotStoredCreatesBodyUsingOriginalEncodingWithDataSection() throws Exception {
        String contentString = "myTextMessageContent";
        ActiveMQTextMessage outbound = createTextMessage(contentString);
        outbound.setShortProperty(AmqpMessageSupport.JMS_AMQP_ORIGINAL_ENCODING, AmqpMessageSupport.AMQP_DATA);
        outbound.onSend();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNotNull(amqp.getBody());
        Assert.assertTrue(((amqp.getBody()) instanceof Data));
        Assert.assertTrue(((getValue()) instanceof Binary));
        Binary data = ((Data) (amqp.getBody())).getValue();
        String contents = new String(data.getArray(), data.getArrayOffset(), data.getLength(), StandardCharsets.UTF_8);
        Assert.assertEquals(contentString, contents);
    }

    @Test
    public void testConvertTextMessageCreatesAmqpValueStringBody() throws Exception {
        String contentString = "myTextMessageContent";
        ActiveMQTextMessage outbound = createTextMessage(contentString);
        outbound.onSend();
        outbound.storeContent();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNotNull(amqp.getBody());
        Assert.assertTrue(((amqp.getBody()) instanceof AmqpValue));
        Assert.assertEquals(contentString, getValue());
    }

    @Test
    public void testConvertTextMessageContentNotStoredCreatesAmqpValueStringBody() throws Exception {
        String contentString = "myTextMessageContent";
        ActiveMQTextMessage outbound = createTextMessage(contentString);
        outbound.onSend();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNotNull(amqp.getBody());
        Assert.assertTrue(((amqp.getBody()) instanceof AmqpValue));
        Assert.assertEquals(contentString, getValue());
    }

    @Test
    public void testConvertCompressedTextMessageCreatesDataSectionBody() throws Exception {
        String contentString = "myTextMessageContent";
        ActiveMQTextMessage outbound = createTextMessage(contentString, true);
        outbound.setShortProperty(AmqpMessageSupport.JMS_AMQP_ORIGINAL_ENCODING, AmqpMessageSupport.AMQP_DATA);
        outbound.onSend();
        outbound.storeContent();
        JMSMappingOutboundTransformer transformer = new JMSMappingOutboundTransformer();
        EncodedMessage encoded = transformer.transform(outbound);
        Assert.assertNotNull(encoded);
        Message amqp = encoded.decode();
        Assert.assertNotNull(amqp.getBody());
        Assert.assertTrue(((amqp.getBody()) instanceof Data));
        Assert.assertTrue(((getValue()) instanceof Binary));
        Binary data = ((Data) (amqp.getBody())).getValue();
        String contents = new String(data.getArray(), data.getArrayOffset(), data.getLength(), StandardCharsets.UTF_8);
        Assert.assertEquals(contentString, contents);
    }

    // ----- Test JMSDestination Handling -------------------------------------//
    @Test
    public void testConvertMessageWithJMSDestinationNull() throws Exception {
        doTestConvertMessageWithJMSDestination(null, null);
    }

    @Test
    public void testConvertMessageWithJMSDestinationQueue() throws Exception {
        doTestConvertMessageWithJMSDestination(createMockDestination(JMSMappingOutboundTransformer.QUEUE_TYPE), JMSMappingOutboundTransformer.QUEUE_TYPE);
    }

    @Test
    public void testConvertMessageWithJMSDestinationTemporaryQueue() throws Exception {
        doTestConvertMessageWithJMSDestination(createMockDestination(JMSMappingOutboundTransformer.TEMP_QUEUE_TYPE), JMSMappingOutboundTransformer.TEMP_QUEUE_TYPE);
    }

    @Test
    public void testConvertMessageWithJMSDestinationTopic() throws Exception {
        doTestConvertMessageWithJMSDestination(createMockDestination(JMSMappingOutboundTransformer.TOPIC_TYPE), JMSMappingOutboundTransformer.TOPIC_TYPE);
    }

    @Test
    public void testConvertMessageWithJMSDestinationTemporaryTopic() throws Exception {
        doTestConvertMessageWithJMSDestination(createMockDestination(JMSMappingOutboundTransformer.TEMP_TOPIC_TYPE), JMSMappingOutboundTransformer.TEMP_TOPIC_TYPE);
    }

    // ----- Test JMSReplyTo Handling -----------------------------------------//
    @Test
    public void testConvertMessageWithJMSReplyToNull() throws Exception {
        doTestConvertMessageWithJMSReplyTo(null, null);
    }

    @Test
    public void testConvertMessageWithJMSReplyToQueue() throws Exception {
        doTestConvertMessageWithJMSReplyTo(createMockDestination(JMSMappingOutboundTransformer.QUEUE_TYPE), JMSMappingOutboundTransformer.QUEUE_TYPE);
    }

    @Test
    public void testConvertMessageWithJMSReplyToTemporaryQueue() throws Exception {
        doTestConvertMessageWithJMSReplyTo(createMockDestination(JMSMappingOutboundTransformer.TEMP_QUEUE_TYPE), JMSMappingOutboundTransformer.TEMP_QUEUE_TYPE);
    }

    @Test
    public void testConvertMessageWithJMSReplyToTopic() throws Exception {
        doTestConvertMessageWithJMSReplyTo(createMockDestination(JMSMappingOutboundTransformer.TOPIC_TYPE), JMSMappingOutboundTransformer.TOPIC_TYPE);
    }

    @Test
    public void testConvertMessageWithJMSReplyToTemporaryTopic() throws Exception {
        doTestConvertMessageWithJMSReplyTo(createMockDestination(JMSMappingOutboundTransformer.TEMP_TOPIC_TYPE), JMSMappingOutboundTransformer.TEMP_TOPIC_TYPE);
    }
}

