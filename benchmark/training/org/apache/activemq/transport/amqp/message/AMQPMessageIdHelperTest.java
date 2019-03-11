/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.activemq.transport.amqp.message;


import java.util.UUID;
import org.apache.activemq.transport.amqp.AmqpProtocolException;
import org.apache.qpid.proton.amqp.Binary;
import org.apache.qpid.proton.amqp.UnsignedLong;
import org.junit.Assert;
import org.junit.Test;

import static AMQPMessageIdHelper.AMQP_BINARY_PREFIX;
import static AMQPMessageIdHelper.AMQP_STRING_PREFIX;
import static AMQPMessageIdHelper.AMQP_ULONG_PREFIX;
import static AMQPMessageIdHelper.AMQP_UUID_PREFIX;


public class AMQPMessageIdHelperTest {
    private AMQPMessageIdHelper messageIdHelper;

    /**
     * Test that {@link AMQPMessageIdHelper#toBaseMessageIdString(Object)}
     * returns null if given null
     */
    @Test
    public void testToBaseMessageIdStringWithNull() {
        String nullString = null;
        Assert.assertNull("null string should have been returned", messageIdHelper.toBaseMessageIdString(nullString));
    }

    /**
     * Test that {@link AMQPMessageIdHelper#toBaseMessageIdString(Object)}
     * throws an IAE if given an unexpected object type.
     */
    @Test
    public void testToBaseMessageIdStringThrowsIAEWithUnexpectedType() {
        try {
            messageIdHelper.toBaseMessageIdString(new Object());
            Assert.fail("expected exception not thrown");
        } catch (IllegalArgumentException iae) {
            // expected
        }
    }

    /**
     * Test that {@link AMQPMessageIdHelper#toBaseMessageIdString(Object)}
     * returns the given basic string unchanged
     */
    @Test
    public void testToBaseMessageIdStringWithString() {
        String stringMessageId = "myIdString";
        String baseMessageIdString = messageIdHelper.toBaseMessageIdString(stringMessageId);
        Assert.assertNotNull("null string should not have been returned", baseMessageIdString);
        Assert.assertEquals("expected base id string was not returned", stringMessageId, baseMessageIdString);
    }

    /**
     * Test that {@link AMQPMessageIdHelper#toBaseMessageIdString(Object)}
     * returns a string indicating an AMQP encoded string, when the given string
     * happens to already begin with the
     * {@link AMQPMessageIdHelper#AMQP_UUID_PREFIX}.
     */
    @Test
    public void testToBaseMessageIdStringWithStringBeginningWithEncodingPrefixForUUID() {
        String uuidStringMessageId = (AMQP_UUID_PREFIX) + (UUID.randomUUID());
        String expected = (AMQP_STRING_PREFIX) + uuidStringMessageId;
        String baseMessageIdString = messageIdHelper.toBaseMessageIdString(uuidStringMessageId);
        Assert.assertNotNull("null string should not have been returned", baseMessageIdString);
        Assert.assertEquals("expected base id string was not returned", expected, baseMessageIdString);
    }

    /**
     * Test that {@link AMQPMessageIdHelper#toBaseMessageIdString(Object)}
     * returns a string indicating an AMQP encoded string, when the given string
     * happens to already begin with the
     * {@link AMQPMessageIdHelper#AMQP_ULONG_PREFIX}.
     */
    @Test
    public void testToBaseMessageIdStringWithStringBeginningWithEncodingPrefixForLong() {
        String longStringMessageId = (AMQP_ULONG_PREFIX) + (Long.valueOf(123456789L));
        String expected = (AMQP_STRING_PREFIX) + longStringMessageId;
        String baseMessageIdString = messageIdHelper.toBaseMessageIdString(longStringMessageId);
        Assert.assertNotNull("null string should not have been returned", baseMessageIdString);
        Assert.assertEquals("expected base id string was not returned", expected, baseMessageIdString);
    }

    /**
     * Test that {@link AMQPMessageIdHelper#toBaseMessageIdString(Object)}
     * returns a string indicating an AMQP encoded string, when the given string
     * happens to already begin with the
     * {@link AMQPMessageIdHelper#AMQP_BINARY_PREFIX}.
     */
    @Test
    public void testToBaseMessageIdStringWithStringBeginningWithEncodingPrefixForBinary() {
        String binaryStringMessageId = (AMQP_BINARY_PREFIX) + "0123456789ABCDEF";
        String expected = (AMQP_STRING_PREFIX) + binaryStringMessageId;
        String baseMessageIdString = messageIdHelper.toBaseMessageIdString(binaryStringMessageId);
        Assert.assertNotNull("null string should not have been returned", baseMessageIdString);
        Assert.assertEquals("expected base id string was not returned", expected, baseMessageIdString);
    }

    /**
     * Test that {@link AMQPMessageIdHelper#toBaseMessageIdString(Object)}
     * returns a string indicating an AMQP encoded string (effectively twice),
     * when the given string happens to already begin with the
     * {@link AMQPMessageIdHelper#AMQP_STRING_PREFIX}.
     */
    @Test
    public void testToBaseMessageIdStringWithStringBeginningWithEncodingPrefixForString() {
        String stringMessageId = (AMQP_STRING_PREFIX) + "myStringId";
        String expected = (AMQP_STRING_PREFIX) + stringMessageId;
        String baseMessageIdString = messageIdHelper.toBaseMessageIdString(stringMessageId);
        Assert.assertNotNull("null string should not have been returned", baseMessageIdString);
        Assert.assertEquals("expected base id string was not returned", expected, baseMessageIdString);
    }

    /**
     * Test that {@link AMQPMessageIdHelper#toBaseMessageIdString(Object)}
     * returns a string indicating an AMQP encoded UUID when given a UUID
     * object.
     */
    @Test
    public void testToBaseMessageIdStringWithUUID() {
        UUID uuidMessageId = UUID.randomUUID();
        String expected = (AMQP_UUID_PREFIX) + (uuidMessageId.toString());
        String baseMessageIdString = messageIdHelper.toBaseMessageIdString(uuidMessageId);
        Assert.assertNotNull("null string should not have been returned", baseMessageIdString);
        Assert.assertEquals("expected base id string was not returned", expected, baseMessageIdString);
    }

    /**
     * Test that {@link AMQPMessageIdHelper#toBaseMessageIdString(Object)}
     * returns a string indicating an AMQP encoded ulong when given a
     * UnsignedLong object.
     */
    @Test
    public void testToBaseMessageIdStringWithUnsignedLong() {
        UnsignedLong uLongMessageId = UnsignedLong.valueOf(123456789L);
        String expected = (AMQP_ULONG_PREFIX) + (uLongMessageId.toString());
        String baseMessageIdString = messageIdHelper.toBaseMessageIdString(uLongMessageId);
        Assert.assertNotNull("null string should not have been returned", baseMessageIdString);
        Assert.assertEquals("expected base id string was not returned", expected, baseMessageIdString);
    }

    /**
     * Test that {@link AMQPMessageIdHelper#toBaseMessageIdString(Object)}
     * returns a string indicating an AMQP encoded binary when given a Binary
     * object.
     */
    @Test
    public void testToBaseMessageIdStringWithBinary() {
        byte[] bytes = new byte[]{ ((byte) (0)), ((byte) (171)), ((byte) (9)), ((byte) (255)) };
        Binary binary = new Binary(bytes);
        String expected = (AMQP_BINARY_PREFIX) + "00AB09FF";
        String baseMessageIdString = messageIdHelper.toBaseMessageIdString(binary);
        Assert.assertNotNull("null string should not have been returned", baseMessageIdString);
        Assert.assertEquals("expected base id string was not returned", expected, baseMessageIdString);
    }

    /**
     * Test that {@link AMQPMessageIdHelper#toIdObject(String)} returns an
     * UnsignedLong when given a string indicating an encoded AMQP ulong id.
     *
     * @throws Exception
     * 		if an error occurs during the test.
     */
    @Test
    public void testToIdObjectWithEncodedUlong() throws Exception {
        UnsignedLong longId = UnsignedLong.valueOf(123456789L);
        String provided = (AMQP_ULONG_PREFIX) + "123456789";
        Object idObject = messageIdHelper.toIdObject(provided);
        Assert.assertNotNull("null object should not have been returned", idObject);
        Assert.assertEquals("expected id object was not returned", longId, idObject);
    }

    /**
     * Test that {@link AMQPMessageIdHelper#toIdObject(String)} returns a Binary
     * when given a string indicating an encoded AMQP binary id, using upper
     * case hex characters
     *
     * @throws Exception
     * 		if an error occurs during the test.
     */
    @Test
    public void testToIdObjectWithEncodedBinaryUppercaseHexString() throws Exception {
        byte[] bytes = new byte[]{ ((byte) (0)), ((byte) (171)), ((byte) (9)), ((byte) (255)) };
        Binary binaryId = new Binary(bytes);
        String provided = (AMQP_BINARY_PREFIX) + "00AB09FF";
        Object idObject = messageIdHelper.toIdObject(provided);
        Assert.assertNotNull("null object should not have been returned", idObject);
        Assert.assertEquals("expected id object was not returned", binaryId, idObject);
    }

    /**
     * Test that {@link AMQPMessageIdHelper#toIdObject(String)} returns null
     * when given null.
     *
     * @throws Exception
     * 		if an error occurs during the test.
     */
    @Test
    public void testToIdObjectWithNull() throws Exception {
        Assert.assertNull("null object should have been returned", messageIdHelper.toIdObject(null));
    }

    /**
     * Test that {@link AMQPMessageIdHelper#toIdObject(String)} returns a Binary
     * when given a string indicating an encoded AMQP binary id, using lower
     * case hex characters.
     *
     * @throws Exception
     * 		if an error occurs during the test.
     */
    @Test
    public void testToIdObjectWithEncodedBinaryLowercaseHexString() throws Exception {
        byte[] bytes = new byte[]{ ((byte) (0)), ((byte) (171)), ((byte) (9)), ((byte) (255)) };
        Binary binaryId = new Binary(bytes);
        String provided = (AMQP_BINARY_PREFIX) + "00ab09ff";
        Object idObject = messageIdHelper.toIdObject(provided);
        Assert.assertNotNull("null object should not have been returned", idObject);
        Assert.assertEquals("expected id object was not returned", binaryId, idObject);
    }

    /**
     * Test that {@link AMQPMessageIdHelper#toIdObject(String)} returns a UUID
     * when given a string indicating an encoded AMQP uuid id.
     *
     * @throws Exception
     * 		if an error occurs during the test.
     */
    @Test
    public void testToIdObjectWithEncodedUuid() throws Exception {
        UUID uuid = UUID.randomUUID();
        String provided = (AMQP_UUID_PREFIX) + (uuid.toString());
        Object idObject = messageIdHelper.toIdObject(provided);
        Assert.assertNotNull("null object should not have been returned", idObject);
        Assert.assertEquals("expected id object was not returned", uuid, idObject);
    }

    /**
     * Test that {@link AMQPMessageIdHelper#toIdObject(String)} returns a string
     * when given a string without any type encoding prefix.
     *
     * @throws Exception
     * 		if an error occurs during the test.
     */
    @Test
    public void testToIdObjectWithStringContainingNoEncodingPrefix() throws Exception {
        String stringId = "myStringId";
        Object idObject = messageIdHelper.toIdObject(stringId);
        Assert.assertNotNull("null object should not have been returned", idObject);
        Assert.assertEquals("expected id object was not returned", stringId, idObject);
    }

    /**
     * Test that {@link AMQPMessageIdHelper#toIdObject(String)} returns the
     * remainder of the provided string after removing the
     * {@link AMQPMessageIdHelper#AMQP_STRING_PREFIX} prefix.
     *
     * @throws Exception
     * 		if an error occurs during the test.
     */
    @Test
    public void testToIdObjectWithStringContainingStringEncodingPrefix() throws Exception {
        String suffix = "myStringSuffix";
        String stringId = (AMQP_STRING_PREFIX) + suffix;
        Object idObject = messageIdHelper.toIdObject(stringId);
        Assert.assertNotNull("null object should not have been returned", idObject);
        Assert.assertEquals("expected id object was not returned", suffix, idObject);
    }

    /**
     * Test that when given a string with with the
     * {@link AMQPMessageIdHelper#AMQP_STRING_PREFIX} prefix and then
     * additionally the {@link AMQPMessageIdHelper#AMQP_UUID_PREFIX}, the
     * {@link AMQPMessageIdHelper#toIdObject(String)} method returns the
     * remainder of the provided string after removing the
     * {@link AMQPMessageIdHelper#AMQP_STRING_PREFIX} prefix.
     *
     * @throws Exception
     * 		if an error occurs during the test.
     */
    @Test
    public void testToIdObjectWithStringContainingStringEncodingPrefixAndThenUuidPrefix() throws Exception {
        String encodedUuidString = (AMQP_UUID_PREFIX) + (UUID.randomUUID().toString());
        String stringId = (AMQP_STRING_PREFIX) + encodedUuidString;
        Object idObject = messageIdHelper.toIdObject(stringId);
        Assert.assertNotNull("null object should not have been returned", idObject);
        Assert.assertEquals("expected id object was not returned", encodedUuidString, idObject);
    }

    /**
     * Test that {@link AMQPMessageIdHelper#toIdObject(String)} throws an
     * {@link IdConversionException} when presented with an encoded binary hex
     * string of uneven length (after the prefix) that thus can't be converted
     * due to each byte using 2 characters
     */
    @Test
    public void testToIdObjectWithStringContainingBinaryHexThrowsWithUnevenLengthString() {
        String unevenHead = (AMQP_BINARY_PREFIX) + "123";
        try {
            messageIdHelper.toIdObject(unevenHead);
            Assert.fail("expected exception was not thrown");
        } catch (AmqpProtocolException ex) {
            // expected
        }
    }

    /**
     * Test that {@link AMQPMessageIdHelper#toIdObject(String)} throws an
     * {@link IdConversionException} when presented with an encoded binary hex
     * string (after the prefix) that contains characters other than 0-9 and A-F
     * and a-f, and thus can't be converted
     */
    @Test
    public void testToIdObjectWithStringContainingBinaryHexThrowsWithNonHexCharacters() {
        // char before '0'
        char nonHexChar = '/';
        String nonHexString = ((AMQP_BINARY_PREFIX) + nonHexChar) + nonHexChar;
        try {
            messageIdHelper.toIdObject(nonHexString);
            Assert.fail("expected exception was not thrown");
        } catch (AmqpProtocolException ex) {
            // expected
        }
        // char after '9', before 'A'
        nonHexChar = ':';
        nonHexString = ((AMQP_BINARY_PREFIX) + nonHexChar) + nonHexChar;
        try {
            messageIdHelper.toIdObject(nonHexString);
            Assert.fail("expected exception was not thrown");
        } catch (AmqpProtocolException ex) {
            // expected
        }
        // char after 'F', before 'a'
        nonHexChar = 'G';
        nonHexString = ((AMQP_BINARY_PREFIX) + nonHexChar) + nonHexChar;
        try {
            messageIdHelper.toIdObject(nonHexString);
            Assert.fail("expected exception was not thrown");
        } catch (AmqpProtocolException ex) {
            // expected
        }
        // char after 'f'
        nonHexChar = 'g';
        nonHexString = ((AMQP_BINARY_PREFIX) + nonHexChar) + nonHexChar;
        try {
            messageIdHelper.toIdObject(nonHexString);
            Assert.fail("expected exception was not thrown");
        } catch (AmqpProtocolException ex) {
            // expected
        }
    }
}

