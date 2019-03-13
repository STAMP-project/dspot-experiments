/**
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.mqtt;


import MqttMessageType.DISCONNECT;
import MqttMessageType.PINGREQ;
import MqttMessageType.PINGRESP;
import MqttMessageType.PUBACK;
import MqttMessageType.PUBCOMP;
import MqttMessageType.PUBREC;
import MqttMessageType.PUBREL;
import MqttMessageType.UNSUBACK;
import MqttQoS.FAILURE;
import MqttVersion.MQTT_3_1;
import MqttVersion.MQTT_3_1_1;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static MqttMessageType.SUBACK;
import static MqttQoS.AT_MOST_ONCE;


/**
 * Unit tests for MqttEncoder and MqttDecoder.
 */
public class MqttCodecTest {
    private static final String CLIENT_ID = "RANDOM_TEST_CLIENT";

    private static final String WILL_TOPIC = "/my_will";

    private static final String WILL_MESSAGE = "gone";

    private static final String USER_NAME = "happy_user";

    private static final String PASSWORD = "123_or_no_pwd";

    private static final int KEEP_ALIVE_SECONDS = 600;

    private static final ByteBufAllocator ALLOCATOR = new UnpooledByteBufAllocator(false);

    @Mock
    private final ChannelHandlerContext ctx = Mockito.mock(ChannelHandlerContext.class);

    @Mock
    private final Channel channel = Mockito.mock(Channel.class);

    private final MqttDecoder mqttDecoder = new MqttDecoder();

    /**
     * MqttDecoder with an unrealistic max payload size of 1 byte.
     */
    private final MqttDecoder mqttDecoderLimitedMessageSize = new MqttDecoder(1);

    @Test
    public void testConnectMessageForMqtt31() throws Exception {
        final MqttConnectMessage message = MqttCodecTest.createConnectMessage(MQTT_3_1);
        ByteBuf byteBuf = MqttEncoder.doEncode(MqttCodecTest.ALLOCATOR, message);
        final List<Object> out = new LinkedList<Object>();
        mqttDecoder.decode(ctx, byteBuf, out);
        Assert.assertEquals(("Expected one object but got " + (out.size())), 1, out.size());
        final MqttConnectMessage decodedMessage = ((MqttConnectMessage) (out.get(0)));
        MqttCodecTest.validateFixedHeaders(message.fixedHeader(), decodedMessage.fixedHeader());
        MqttCodecTest.validateConnectVariableHeader(message.variableHeader(), decodedMessage.variableHeader());
        MqttCodecTest.validateConnectPayload(message.payload(), decodedMessage.payload());
    }

    @Test
    public void testConnectMessageForMqtt311() throws Exception {
        final MqttConnectMessage message = MqttCodecTest.createConnectMessage(MQTT_3_1_1);
        ByteBuf byteBuf = MqttEncoder.doEncode(MqttCodecTest.ALLOCATOR, message);
        final List<Object> out = new LinkedList<Object>();
        mqttDecoder.decode(ctx, byteBuf, out);
        Assert.assertEquals(("Expected one object but got " + (out.size())), 1, out.size());
        final MqttConnectMessage decodedMessage = ((MqttConnectMessage) (out.get(0)));
        MqttCodecTest.validateFixedHeaders(message.fixedHeader(), decodedMessage.fixedHeader());
        MqttCodecTest.validateConnectVariableHeader(message.variableHeader(), decodedMessage.variableHeader());
        MqttCodecTest.validateConnectPayload(message.payload(), decodedMessage.payload());
    }

    @Test
    public void testConnectMessageWithNonZeroReservedFlagForMqtt311() throws Exception {
        final MqttConnectMessage message = MqttCodecTest.createConnectMessage(MQTT_3_1_1);
        ByteBuf byteBuf = MqttEncoder.doEncode(MqttCodecTest.ALLOCATOR, message);
        try {
            // Set the reserved flag in the CONNECT Packet to 1
            byteBuf.setByte(9, ((byteBuf.getByte(9)) | 1));
            final List<Object> out = new LinkedList<Object>();
            mqttDecoder.decode(ctx, byteBuf, out);
            Assert.assertEquals(("Expected one object but got " + (out.size())), 1, out.size());
            final MqttMessage decodedMessage = ((MqttMessage) (out.get(0)));
            Assert.assertTrue(decodedMessage.decoderResult().isFailure());
            Throwable cause = decodedMessage.decoderResult().cause();
            Assert.assertTrue((cause instanceof DecoderException));
            Assert.assertEquals("non-zero reserved flag", cause.getMessage());
        } finally {
            byteBuf.release();
        }
    }

    @Test
    public void testConnectMessageNoPassword() throws Exception {
        final MqttConnectMessage message = MqttCodecTest.createConnectMessage(MQTT_3_1_1, null, MqttCodecTest.PASSWORD);
        try {
            ByteBuf byteBuf = MqttEncoder.doEncode(MqttCodecTest.ALLOCATOR, message);
        } catch (Exception cause) {
            Assert.assertTrue((cause instanceof DecoderException));
        }
    }

    @Test
    public void testConnAckMessage() throws Exception {
        final MqttConnAckMessage message = MqttCodecTest.createConnAckMessage();
        ByteBuf byteBuf = MqttEncoder.doEncode(MqttCodecTest.ALLOCATOR, message);
        final List<Object> out = new LinkedList<Object>();
        mqttDecoder.decode(ctx, byteBuf, out);
        Assert.assertEquals(("Expected one object but got " + (out.size())), 1, out.size());
        final MqttConnAckMessage decodedMessage = ((MqttConnAckMessage) (out.get(0)));
        MqttCodecTest.validateFixedHeaders(message.fixedHeader(), decodedMessage.fixedHeader());
        MqttCodecTest.validateConnAckVariableHeader(message.variableHeader(), decodedMessage.variableHeader());
    }

    @Test
    public void testPublishMessage() throws Exception {
        final MqttPublishMessage message = MqttCodecTest.createPublishMessage();
        ByteBuf byteBuf = MqttEncoder.doEncode(MqttCodecTest.ALLOCATOR, message);
        final List<Object> out = new LinkedList<Object>();
        mqttDecoder.decode(ctx, byteBuf, out);
        Assert.assertEquals(("Expected one object but got " + (out.size())), 1, out.size());
        final MqttPublishMessage decodedMessage = ((MqttPublishMessage) (out.get(0)));
        MqttCodecTest.validateFixedHeaders(message.fixedHeader(), decodedMessage.fixedHeader());
        MqttCodecTest.validatePublishVariableHeader(message.variableHeader(), decodedMessage.variableHeader());
        MqttCodecTest.validatePublishPayload(message.payload(), decodedMessage.payload());
    }

    @Test
    public void testPubAckMessage() throws Exception {
        testMessageWithOnlyFixedHeaderAndMessageIdVariableHeader(PUBACK);
    }

    @Test
    public void testPubRecMessage() throws Exception {
        testMessageWithOnlyFixedHeaderAndMessageIdVariableHeader(PUBREC);
    }

    @Test
    public void testPubRelMessage() throws Exception {
        testMessageWithOnlyFixedHeaderAndMessageIdVariableHeader(PUBREL);
    }

    @Test
    public void testPubCompMessage() throws Exception {
        testMessageWithOnlyFixedHeaderAndMessageIdVariableHeader(PUBCOMP);
    }

    @Test
    public void testSubscribeMessage() throws Exception {
        final MqttSubscribeMessage message = MqttCodecTest.createSubscribeMessage();
        ByteBuf byteBuf = MqttEncoder.doEncode(MqttCodecTest.ALLOCATOR, message);
        final List<Object> out = new LinkedList<Object>();
        mqttDecoder.decode(ctx, byteBuf, out);
        Assert.assertEquals(("Expected one object but got " + (out.size())), 1, out.size());
        final MqttSubscribeMessage decodedMessage = ((MqttSubscribeMessage) (out.get(0)));
        MqttCodecTest.validateFixedHeaders(message.fixedHeader(), decodedMessage.fixedHeader());
        MqttCodecTest.validateMessageIdVariableHeader(message.variableHeader(), decodedMessage.variableHeader());
        MqttCodecTest.validateSubscribePayload(message.payload(), decodedMessage.payload());
    }

    @Test
    public void testSubAckMessage() throws Exception {
        final MqttSubAckMessage message = MqttCodecTest.createSubAckMessage();
        ByteBuf byteBuf = MqttEncoder.doEncode(MqttCodecTest.ALLOCATOR, message);
        final List<Object> out = new LinkedList<Object>();
        mqttDecoder.decode(ctx, byteBuf, out);
        Assert.assertEquals(("Expected one object but got " + (out.size())), 1, out.size());
        final MqttSubAckMessage decodedMessage = ((MqttSubAckMessage) (out.get(0)));
        MqttCodecTest.validateFixedHeaders(message.fixedHeader(), decodedMessage.fixedHeader());
        MqttCodecTest.validateMessageIdVariableHeader(message.variableHeader(), decodedMessage.variableHeader());
        MqttCodecTest.validateSubAckPayload(message.payload(), decodedMessage.payload());
    }

    @Test
    public void testSubAckMessageWithFailureInPayload() throws Exception {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(SUBACK, false, AT_MOST_ONCE, false, 0);
        MqttMessageIdVariableHeader mqttMessageIdVariableHeader = MqttMessageIdVariableHeader.from(12345);
        MqttSubAckPayload mqttSubAckPayload = new MqttSubAckPayload(FAILURE.value());
        MqttSubAckMessage message = new MqttSubAckMessage(mqttFixedHeader, mqttMessageIdVariableHeader, mqttSubAckPayload);
        ByteBuf byteBuf = MqttEncoder.doEncode(MqttCodecTest.ALLOCATOR, message);
        List<Object> out = new LinkedList<Object>();
        mqttDecoder.decode(ctx, byteBuf, out);
        Assert.assertEquals(("Expected one object but got " + (out.size())), 1, out.size());
        MqttSubAckMessage decodedMessage = ((MqttSubAckMessage) (out.get(0)));
        MqttCodecTest.validateFixedHeaders(message.fixedHeader(), decodedMessage.fixedHeader());
        MqttCodecTest.validateMessageIdVariableHeader(message.variableHeader(), decodedMessage.variableHeader());
        MqttCodecTest.validateSubAckPayload(message.payload(), decodedMessage.payload());
        Assert.assertEquals(1, decodedMessage.payload().grantedQoSLevels().size());
        Assert.assertEquals(FAILURE, MqttQoS.valueOf(decodedMessage.payload().grantedQoSLevels().get(0)));
    }

    @Test
    public void testUnSubscribeMessage() throws Exception {
        final MqttUnsubscribeMessage message = MqttCodecTest.createUnsubscribeMessage();
        ByteBuf byteBuf = MqttEncoder.doEncode(MqttCodecTest.ALLOCATOR, message);
        final List<Object> out = new LinkedList<Object>();
        mqttDecoder.decode(ctx, byteBuf, out);
        Assert.assertEquals(("Expected one object but got " + (out.size())), 1, out.size());
        final MqttUnsubscribeMessage decodedMessage = ((MqttUnsubscribeMessage) (out.get(0)));
        MqttCodecTest.validateFixedHeaders(message.fixedHeader(), decodedMessage.fixedHeader());
        MqttCodecTest.validateMessageIdVariableHeader(message.variableHeader(), decodedMessage.variableHeader());
        MqttCodecTest.validateUnsubscribePayload(message.payload(), decodedMessage.payload());
    }

    @Test
    public void testUnsubAckMessage() throws Exception {
        testMessageWithOnlyFixedHeaderAndMessageIdVariableHeader(UNSUBACK);
    }

    @Test
    public void testPingReqMessage() throws Exception {
        testMessageWithOnlyFixedHeader(PINGREQ);
    }

    @Test
    public void testPingRespMessage() throws Exception {
        testMessageWithOnlyFixedHeader(PINGRESP);
    }

    @Test
    public void testDisconnectMessage() throws Exception {
        testMessageWithOnlyFixedHeader(DISCONNECT);
    }

    @Test
    public void testUnknownMessageType() throws Exception {
        final MqttMessage message = MqttCodecTest.createMessageWithFixedHeader(PINGREQ);
        ByteBuf byteBuf = MqttEncoder.doEncode(MqttCodecTest.ALLOCATOR, message);
        try {
            // setting an invalid message type (15, reserved and forbidden by MQTT 3.1.1 spec)
            byteBuf.setByte(0, 240);
            final List<Object> out = new LinkedList<Object>();
            mqttDecoder.decode(ctx, byteBuf, out);
            Assert.assertEquals(("Expected one object but got " + (out.size())), 1, out.size());
            final MqttMessage decodedMessage = ((MqttMessage) (out.get(0)));
            Assert.assertTrue(decodedMessage.decoderResult().isFailure());
            Throwable cause = decodedMessage.decoderResult().cause();
            Assert.assertTrue((cause instanceof IllegalArgumentException));
            Assert.assertEquals("unknown message type: 15", cause.getMessage());
        } finally {
            byteBuf.release();
        }
    }

    @Test
    public void testConnectMessageForMqtt31TooLarge() throws Exception {
        final MqttConnectMessage message = MqttCodecTest.createConnectMessage(MQTT_3_1);
        ByteBuf byteBuf = MqttEncoder.doEncode(MqttCodecTest.ALLOCATOR, message);
        try {
            final List<Object> out = new LinkedList<Object>();
            mqttDecoderLimitedMessageSize.decode(ctx, byteBuf, out);
            Assert.assertEquals(("Expected one object but got " + (out.size())), 1, out.size());
            final MqttMessage decodedMessage = ((MqttMessage) (out.get(0)));
            MqttCodecTest.validateFixedHeaders(message.fixedHeader(), decodedMessage.fixedHeader());
            MqttCodecTest.validateConnectVariableHeader(message.variableHeader(), ((MqttConnectVariableHeader) (decodedMessage.variableHeader())));
            MqttCodecTest.validateDecoderExceptionTooLargeMessage(decodedMessage);
        } finally {
            byteBuf.release();
        }
    }

    @Test
    public void testConnectMessageForMqtt311TooLarge() throws Exception {
        final MqttConnectMessage message = MqttCodecTest.createConnectMessage(MQTT_3_1_1);
        ByteBuf byteBuf = MqttEncoder.doEncode(MqttCodecTest.ALLOCATOR, message);
        try {
            final List<Object> out = new LinkedList<Object>();
            mqttDecoderLimitedMessageSize.decode(ctx, byteBuf, out);
            Assert.assertEquals(("Expected one object but got " + (out.size())), 1, out.size());
            final MqttMessage decodedMessage = ((MqttMessage) (out.get(0)));
            MqttCodecTest.validateFixedHeaders(message.fixedHeader(), decodedMessage.fixedHeader());
            MqttCodecTest.validateConnectVariableHeader(message.variableHeader(), ((MqttConnectVariableHeader) (decodedMessage.variableHeader())));
            MqttCodecTest.validateDecoderExceptionTooLargeMessage(decodedMessage);
        } finally {
            byteBuf.release();
        }
    }

    @Test
    public void testConnAckMessageTooLarge() throws Exception {
        final MqttConnAckMessage message = MqttCodecTest.createConnAckMessage();
        ByteBuf byteBuf = MqttEncoder.doEncode(MqttCodecTest.ALLOCATOR, message);
        try {
            final List<Object> out = new LinkedList<Object>();
            mqttDecoderLimitedMessageSize.decode(ctx, byteBuf, out);
            Assert.assertEquals(("Expected one object but got " + (out.size())), 1, out.size());
            final MqttMessage decodedMessage = ((MqttMessage) (out.get(0)));
            MqttCodecTest.validateFixedHeaders(message.fixedHeader(), decodedMessage.fixedHeader());
            MqttCodecTest.validateDecoderExceptionTooLargeMessage(decodedMessage);
        } finally {
            byteBuf.release();
        }
    }

    @Test
    public void testPublishMessageTooLarge() throws Exception {
        final MqttPublishMessage message = MqttCodecTest.createPublishMessage();
        ByteBuf byteBuf = MqttEncoder.doEncode(MqttCodecTest.ALLOCATOR, message);
        try {
            final List<Object> out = new LinkedList<Object>();
            mqttDecoderLimitedMessageSize.decode(ctx, byteBuf, out);
            Assert.assertEquals(("Expected one object but got " + (out.size())), 1, out.size());
            final MqttMessage decodedMessage = ((MqttMessage) (out.get(0)));
            MqttCodecTest.validateFixedHeaders(message.fixedHeader(), decodedMessage.fixedHeader());
            MqttCodecTest.validatePublishVariableHeader(message.variableHeader(), ((MqttPublishVariableHeader) (decodedMessage.variableHeader())));
            MqttCodecTest.validateDecoderExceptionTooLargeMessage(decodedMessage);
        } finally {
            byteBuf.release();
        }
    }

    @Test
    public void testSubscribeMessageTooLarge() throws Exception {
        final MqttSubscribeMessage message = MqttCodecTest.createSubscribeMessage();
        ByteBuf byteBuf = MqttEncoder.doEncode(MqttCodecTest.ALLOCATOR, message);
        try {
            final List<Object> out = new LinkedList<Object>();
            mqttDecoderLimitedMessageSize.decode(ctx, byteBuf, out);
            Assert.assertEquals(("Expected one object but got " + (out.size())), 1, out.size());
            final MqttMessage decodedMessage = ((MqttMessage) (out.get(0)));
            MqttCodecTest.validateFixedHeaders(message.fixedHeader(), decodedMessage.fixedHeader());
            MqttCodecTest.validateMessageIdVariableHeader(message.variableHeader(), ((MqttMessageIdVariableHeader) (decodedMessage.variableHeader())));
            MqttCodecTest.validateDecoderExceptionTooLargeMessage(decodedMessage);
        } finally {
            byteBuf.release();
        }
    }

    @Test
    public void testSubAckMessageTooLarge() throws Exception {
        final MqttSubAckMessage message = MqttCodecTest.createSubAckMessage();
        ByteBuf byteBuf = MqttEncoder.doEncode(MqttCodecTest.ALLOCATOR, message);
        try {
            final List<Object> out = new LinkedList<Object>();
            mqttDecoderLimitedMessageSize.decode(ctx, byteBuf, out);
            Assert.assertEquals(("Expected one object but got " + (out.size())), 1, out.size());
            final MqttMessage decodedMessage = ((MqttMessage) (out.get(0)));
            MqttCodecTest.validateFixedHeaders(message.fixedHeader(), decodedMessage.fixedHeader());
            MqttCodecTest.validateMessageIdVariableHeader(message.variableHeader(), ((MqttMessageIdVariableHeader) (decodedMessage.variableHeader())));
            MqttCodecTest.validateDecoderExceptionTooLargeMessage(decodedMessage);
        } finally {
            byteBuf.release();
        }
    }

    @Test
    public void testUnSubscribeMessageTooLarge() throws Exception {
        final MqttUnsubscribeMessage message = MqttCodecTest.createUnsubscribeMessage();
        ByteBuf byteBuf = MqttEncoder.doEncode(MqttCodecTest.ALLOCATOR, message);
        try {
            final List<Object> out = new LinkedList<Object>();
            mqttDecoderLimitedMessageSize.decode(ctx, byteBuf, out);
            Assert.assertEquals(("Expected one object but got " + (out.size())), 1, out.size());
            final MqttMessage decodedMessage = ((MqttMessage) (out.get(0)));
            MqttCodecTest.validateFixedHeaders(message.fixedHeader(), decodedMessage.fixedHeader());
            MqttCodecTest.validateMessageIdVariableHeader(message.variableHeader(), ((MqttMessageIdVariableHeader) (decodedMessage.variableHeader())));
            MqttCodecTest.validateDecoderExceptionTooLargeMessage(decodedMessage);
        } finally {
            byteBuf.release();
        }
    }
}

