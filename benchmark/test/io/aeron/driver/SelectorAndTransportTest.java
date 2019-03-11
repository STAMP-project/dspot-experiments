/**
 * Copyright 2014-2019 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.aeron.driver;


import DataHeaderFlyweight.BEGIN_AND_END_FLAGS;
import FrameDescriptor.FRAME_ALIGNMENT;
import HeaderFlyweight.CURRENT_VERSION;
import HeaderFlyweight.HDR_TYPE_DATA;
import HeaderFlyweight.HDR_TYPE_SM;
import MediaDriver.Context;
import StatusMessageFlyweight.HEADER_LENGTH;
import io.aeron.driver.status.SystemCounters;
import io.aeron.protocol.DataHeaderFlyweight;
import io.aeron.protocol.StatusMessageFlyweight;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import org.agrona.BitUtil;
import org.agrona.collections.MutableInteger;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.concurrent.status.AtomicCounter;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class SelectorAndTransportTest {
    private static final int RCV_PORT = 40123;

    private static final int SRC_PORT = 40124;

    private static final int SESSION_ID = -559038737;

    private static final int STREAM_ID = 1144201745;

    private static final int TERM_ID = -1719109786;

    private static final int FRAME_LENGTH = 24;

    private static final UdpChannel SRC_DST = UdpChannel.parse(((("aeron:udp?interface=localhost:" + (SelectorAndTransportTest.SRC_PORT)) + "|endpoint=localhost:") + (SelectorAndTransportTest.RCV_PORT)));

    private static final UdpChannel RCV_DST = UdpChannel.parse(("aeron:udp?endpoint=localhost:" + (SelectorAndTransportTest.RCV_PORT)));

    private final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(256);

    private final UnsafeBuffer buffer = new UnsafeBuffer(byteBuffer);

    private final DataHeaderFlyweight encodeDataHeader = new DataHeaderFlyweight();

    private final StatusMessageFlyweight statusMessage = new StatusMessageFlyweight();

    private final InetSocketAddress rcvRemoteAddress = new InetSocketAddress("localhost", SelectorAndTransportTest.SRC_PORT);

    private final SystemCounters mockSystemCounters = Mockito.mock(SystemCounters.class);

    private final AtomicCounter mockStatusMessagesReceivedCounter = Mockito.mock(AtomicCounter.class);

    private final AtomicCounter mockSendStatusIndicator = Mockito.mock(AtomicCounter.class);

    private final AtomicCounter mockReceiveStatusIndicator = Mockito.mock(AtomicCounter.class);

    private final DataPacketDispatcher mockDispatcher = Mockito.mock(DataPacketDispatcher.class);

    private final NetworkPublication mockPublication = Mockito.mock(NetworkPublication.class);

    private final DataTransportPoller dataTransportPoller = new DataTransportPoller();

    private final ControlTransportPoller controlTransportPoller = new ControlTransportPoller();

    private SendChannelEndpoint sendChannelEndpoint;

    private ReceiveChannelEndpoint receiveChannelEndpoint;

    private final Context context = new MediaDriver.Context();

    @Test(timeout = 1000)
    public void shouldHandleBasicSetupAndTearDown() {
        receiveChannelEndpoint = new ReceiveChannelEndpoint(SelectorAndTransportTest.RCV_DST, mockDispatcher, mockReceiveStatusIndicator, context);
        sendChannelEndpoint = new SendChannelEndpoint(SelectorAndTransportTest.SRC_DST, mockSendStatusIndicator, context);
        receiveChannelEndpoint.openDatagramChannel(mockReceiveStatusIndicator);
        receiveChannelEndpoint.registerForRead(dataTransportPoller);
        sendChannelEndpoint.openDatagramChannel(mockSendStatusIndicator);
        sendChannelEndpoint.registerForRead(controlTransportPoller);
        processLoop(dataTransportPoller, 5);
    }

    @Test(timeout = 1000)
    public void shouldSendEmptyDataFrameUnicastFromSourceToReceiver() {
        final MutableInteger dataHeadersReceived = new MutableInteger(0);
        Mockito.doAnswer(( invocation) -> {
            (dataHeadersReceived.value)++;
            return null;
        }).when(mockDispatcher).onDataPacket(ArgumentMatchers.any(ReceiveChannelEndpoint.class), ArgumentMatchers.any(DataHeaderFlyweight.class), ArgumentMatchers.any(UnsafeBuffer.class), ArgumentMatchers.anyInt(), ArgumentMatchers.any(InetSocketAddress.class), ArgumentMatchers.anyInt());
        receiveChannelEndpoint = new ReceiveChannelEndpoint(SelectorAndTransportTest.RCV_DST, mockDispatcher, mockReceiveStatusIndicator, context);
        sendChannelEndpoint = new SendChannelEndpoint(SelectorAndTransportTest.SRC_DST, mockSendStatusIndicator, context);
        receiveChannelEndpoint.openDatagramChannel(mockReceiveStatusIndicator);
        receiveChannelEndpoint.registerForRead(dataTransportPoller);
        sendChannelEndpoint.openDatagramChannel(mockSendStatusIndicator);
        sendChannelEndpoint.registerForRead(controlTransportPoller);
        encodeDataHeader.wrap(buffer);
        encodeDataHeader.version(CURRENT_VERSION).flags(BEGIN_AND_END_FLAGS).headerType(HDR_TYPE_DATA).frameLength(SelectorAndTransportTest.FRAME_LENGTH);
        encodeDataHeader.sessionId(SelectorAndTransportTest.SESSION_ID).streamId(SelectorAndTransportTest.STREAM_ID).termId(SelectorAndTransportTest.TERM_ID);
        byteBuffer.position(0).limit(SelectorAndTransportTest.FRAME_LENGTH);
        processLoop(dataTransportPoller, 5);
        sendChannelEndpoint.send(byteBuffer);
        while ((dataHeadersReceived.get()) < 1) {
            processLoop(dataTransportPoller, 1);
        } 
        Assert.assertThat(dataHeadersReceived.get(), Is.is(1));
    }

    @Test(timeout = 1000)
    public void shouldSendMultipleDataFramesPerDatagramUnicastFromSourceToReceiver() {
        final MutableInteger dataHeadersReceived = new MutableInteger(0);
        Mockito.doAnswer(( invocation) -> {
            (dataHeadersReceived.value)++;
            return null;
        }).when(mockDispatcher).onDataPacket(ArgumentMatchers.any(ReceiveChannelEndpoint.class), ArgumentMatchers.any(DataHeaderFlyweight.class), ArgumentMatchers.any(UnsafeBuffer.class), ArgumentMatchers.anyInt(), ArgumentMatchers.any(InetSocketAddress.class), ArgumentMatchers.anyInt());
        receiveChannelEndpoint = new ReceiveChannelEndpoint(SelectorAndTransportTest.RCV_DST, mockDispatcher, mockReceiveStatusIndicator, context);
        sendChannelEndpoint = new SendChannelEndpoint(SelectorAndTransportTest.SRC_DST, mockSendStatusIndicator, context);
        receiveChannelEndpoint.openDatagramChannel(mockReceiveStatusIndicator);
        receiveChannelEndpoint.registerForRead(dataTransportPoller);
        sendChannelEndpoint.openDatagramChannel(mockSendStatusIndicator);
        sendChannelEndpoint.registerForRead(controlTransportPoller);
        encodeDataHeader.wrap(buffer);
        encodeDataHeader.version(CURRENT_VERSION).flags(BEGIN_AND_END_FLAGS).headerType(HDR_TYPE_DATA).frameLength(SelectorAndTransportTest.FRAME_LENGTH);
        encodeDataHeader.sessionId(SelectorAndTransportTest.SESSION_ID).streamId(SelectorAndTransportTest.STREAM_ID).termId(SelectorAndTransportTest.TERM_ID);
        final int alignedFrameLength = BitUtil.align(SelectorAndTransportTest.FRAME_LENGTH, FRAME_ALIGNMENT);
        encodeDataHeader.wrap(buffer, alignedFrameLength, ((buffer.capacity()) - alignedFrameLength));
        encodeDataHeader.version(CURRENT_VERSION).flags(BEGIN_AND_END_FLAGS).headerType(HDR_TYPE_DATA).frameLength(24);
        encodeDataHeader.sessionId(SelectorAndTransportTest.SESSION_ID).streamId(SelectorAndTransportTest.STREAM_ID).termId(SelectorAndTransportTest.TERM_ID);
        byteBuffer.position(0).limit((2 * alignedFrameLength));
        processLoop(dataTransportPoller, 5);
        sendChannelEndpoint.send(byteBuffer);
        while ((dataHeadersReceived.get()) < 1) {
            processLoop(dataTransportPoller, 1);
        } 
        Assert.assertThat(dataHeadersReceived.get(), Is.is(1));
    }

    @Test(timeout = 1000)
    public void shouldHandleSmFrameFromReceiverToSender() {
        final MutableInteger controlMessagesReceived = new MutableInteger(0);
        Mockito.doAnswer(( invocation) -> {
            (controlMessagesReceived.value)++;
            return null;
        }).when(mockPublication).onStatusMessage(ArgumentMatchers.any(), ArgumentMatchers.any());
        receiveChannelEndpoint = new ReceiveChannelEndpoint(SelectorAndTransportTest.RCV_DST, mockDispatcher, mockReceiveStatusIndicator, context);
        sendChannelEndpoint = new SendChannelEndpoint(SelectorAndTransportTest.SRC_DST, mockSendStatusIndicator, context);
        sendChannelEndpoint.registerForSend(mockPublication);
        receiveChannelEndpoint.openDatagramChannel(mockReceiveStatusIndicator);
        receiveChannelEndpoint.registerForRead(dataTransportPoller);
        sendChannelEndpoint.openDatagramChannel(mockSendStatusIndicator);
        sendChannelEndpoint.registerForRead(controlTransportPoller);
        statusMessage.wrap(buffer);
        statusMessage.streamId(SelectorAndTransportTest.STREAM_ID).sessionId(SelectorAndTransportTest.SESSION_ID).consumptionTermId(SelectorAndTransportTest.TERM_ID).receiverWindowLength(1000).consumptionTermOffset(0).version(CURRENT_VERSION).flags(((short) (0))).headerType(HDR_TYPE_SM).frameLength(HEADER_LENGTH);
        byteBuffer.position(0).limit(statusMessage.frameLength());
        processLoop(dataTransportPoller, 5);
        receiveChannelEndpoint.sendTo(byteBuffer, rcvRemoteAddress);
        while ((controlMessagesReceived.get()) < 1) {
            processLoop(controlTransportPoller, 1);
        } 
        Mockito.verify(mockStatusMessagesReceivedCounter, Mockito.times(1)).incrementOrdered();
    }
}

