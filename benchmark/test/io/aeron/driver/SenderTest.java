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
import HeaderFlyweight.CURRENT_VERSION;
import HeaderFlyweight.HDR_TYPE_DATA;
import HeaderFlyweight.HDR_TYPE_SETUP;
import SetupFlyweight.HEADER_LENGTH;
import io.aeron.driver.buffer.RawLog;
import io.aeron.driver.media.ControlTransportPoller;
import io.aeron.driver.media.UdpChannel;
import io.aeron.driver.status.SystemCounters;
import io.aeron.logbuffer.HeaderWriter;
import io.aeron.logbuffer.LogBufferDescriptor;
import io.aeron.logbuffer.TermAppender;
import io.aeron.protocol.DataHeaderFlyweight;
import io.aeron.protocol.SetupFlyweight;
import io.aeron.protocol.StatusMessageFlyweight;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Queue;
import org.agrona.concurrent.OneToOneConcurrentArrayQueue;
import org.agrona.concurrent.UnsafeBuffer;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static Configuration.CMD_QUEUE_CAPACITY;
import static Configuration.PUBLICATION_HEARTBEAT_TIMEOUT_NS;
import static Configuration.PUBLICATION_SETUP_TIMEOUT_NS;


public class SenderTest {
    private static final int TERM_BUFFER_LENGTH = LogBufferDescriptor.TERM_MIN_LENGTH;

    private static final int MAX_FRAME_LENGTH = 1024;

    private static final int SESSION_ID = 1;

    private static final int STREAM_ID = 2;

    private static final int INITIAL_TERM_ID = 3;

    private static final byte[] PAYLOAD = "Payload is here!".getBytes();

    private static final UnsafeBuffer HEADER = DataHeaderFlyweight.createDefaultHeader(SenderTest.SESSION_ID, SenderTest.STREAM_ID, SenderTest.INITIAL_TERM_ID);

    private static final int FRAME_LENGTH = (SenderTest.HEADER.capacity()) + (SenderTest.PAYLOAD.length);

    private static final int ALIGNED_FRAME_LENGTH = align(SenderTest.FRAME_LENGTH, FRAME_ALIGNMENT);

    private final ControlTransportPoller mockTransportPoller = Mockito.mock(ControlTransportPoller.class);

    private final RawLog rawLog = LogBufferHelper.newTestLogBuffers(SenderTest.TERM_BUFFER_LENGTH);

    private TermAppender[] termAppenders;

    private NetworkPublication publication;

    private Sender sender;

    private final FlowControl flowControl = Mockito.spy(new UnicastFlowControl());

    private final RetransmitHandler mockRetransmitHandler = Mockito.mock(RetransmitHandler.class);

    private long currentTimestamp = 0;

    private final Queue<ByteBuffer> receivedFrames = new ArrayDeque<>();

    private final UdpChannel udpChannel = UdpChannel.parse("aeron:udp?endpoint=localhost:40123");

    private final InetSocketAddress rcvAddress = udpChannel.remoteData();

    private final DataHeaderFlyweight dataHeader = new DataHeaderFlyweight();

    private final SetupFlyweight setupHeader = new SetupFlyweight();

    private final SystemCounters mockSystemCounters = Mockito.mock(SystemCounters.class);

    private final OneToOneConcurrentArrayQueue<Runnable> senderCommandQueue = new OneToOneConcurrentArrayQueue(CMD_QUEUE_CAPACITY);

    private final HeaderWriter headerWriter = HeaderWriter.newInstance(SenderTest.HEADER);

    private final Answer<Integer> saveByteBufferAnswer = ( invocation) -> {
        final Object[] args = invocation.getArguments();
        final ByteBuffer buffer = ((ByteBuffer) (args[0]));
        final int length = (buffer.limit()) - (buffer.position());
        receivedFrames.add(ByteBuffer.allocateDirect(length).put(buffer));
        // we don't pass on the args, so don't reset buffer.position() back
        return length;
    };

    @Test
    public void shouldSendSetupFrameOnChannelWhenTimeoutWithoutStatusMessage() {
        sender.doWork();
        MatcherAssert.assertThat(receivedFrames.size(), Is.is(1));
        currentTimestamp += (PUBLICATION_SETUP_TIMEOUT_NS) - 1;
        sender.doWork();
        MatcherAssert.assertThat(receivedFrames.size(), Is.is(1));
        currentTimestamp += 10;
        sender.doWork();
        MatcherAssert.assertThat(receivedFrames.size(), Is.is(2));
        setupHeader.wrap(new UnsafeBuffer(receivedFrames.remove()));
        MatcherAssert.assertThat(setupHeader.frameLength(), Is.is(HEADER_LENGTH));
        MatcherAssert.assertThat(setupHeader.initialTermId(), Is.is(SenderTest.INITIAL_TERM_ID));
        MatcherAssert.assertThat(setupHeader.activeTermId(), Is.is(SenderTest.INITIAL_TERM_ID));
        MatcherAssert.assertThat(setupHeader.streamId(), Is.is(SenderTest.STREAM_ID));
        MatcherAssert.assertThat(setupHeader.sessionId(), Is.is(SenderTest.SESSION_ID));
        MatcherAssert.assertThat(setupHeader.headerType(), Is.is(HDR_TYPE_SETUP));
        MatcherAssert.assertThat(setupHeader.flags(), Is.is(((short) (0))));
        MatcherAssert.assertThat(setupHeader.version(), Is.is(((short) (CURRENT_VERSION))));
    }

    @Test
    public void shouldSendMultipleSetupFramesOnChannelWhenTimeoutWithoutStatusMessage() {
        sender.doWork();
        MatcherAssert.assertThat(receivedFrames.size(), Is.is(1));
        currentTimestamp += (PUBLICATION_SETUP_TIMEOUT_NS) - 1;
        sender.doWork();
        currentTimestamp += 10;
        sender.doWork();
        MatcherAssert.assertThat(receivedFrames.size(), Is.is(2));
    }

    @Test
    public void shouldNotSendSetupFrameAfterReceivingStatusMessage() {
        final StatusMessageFlyweight msg = Mockito.mock(StatusMessageFlyweight.class);
        Mockito.when(msg.consumptionTermId()).thenReturn(SenderTest.INITIAL_TERM_ID);
        Mockito.when(msg.consumptionTermOffset()).thenReturn(0);
        Mockito.when(msg.receiverWindowLength()).thenReturn(0);
        publication.onStatusMessage(msg, rcvAddress);
        // publication.senderPositionLimit(flowControl.onStatusMessage(msg, rcvAddress, ));
        sender.doWork();
        MatcherAssert.assertThat(receivedFrames.size(), Is.is(1));
        receivedFrames.remove();
        currentTimestamp += (PUBLICATION_SETUP_TIMEOUT_NS) + 10;
        sender.doWork();
        MatcherAssert.assertThat(receivedFrames.size(), Is.is(1));
        dataHeader.wrap(receivedFrames.remove());
        MatcherAssert.assertThat(dataHeader.headerType(), Is.is(HDR_TYPE_DATA));// heartbeat

        MatcherAssert.assertThat(dataHeader.frameLength(), Is.is(0));
        MatcherAssert.assertThat(dataHeader.termOffset(), Is.is(offsetOfMessage(1)));
    }

    @Test
    public void shouldSendSetupFrameAfterReceivingStatusMessageWithSetupBit() {
        final StatusMessageFlyweight msg = Mockito.mock(StatusMessageFlyweight.class);
        Mockito.when(msg.consumptionTermId()).thenReturn(SenderTest.INITIAL_TERM_ID);
        Mockito.when(msg.consumptionTermOffset()).thenReturn(0);
        Mockito.when(msg.receiverWindowLength()).thenReturn(SenderTest.ALIGNED_FRAME_LENGTH);
        publication.onStatusMessage(msg, rcvAddress);
        final UnsafeBuffer buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(SenderTest.PAYLOAD.length));
        buffer.putBytes(0, SenderTest.PAYLOAD);
        termAppenders[0].appendUnfragmentedMessage(headerWriter, buffer, 0, SenderTest.PAYLOAD.length, null, SenderTest.INITIAL_TERM_ID);
        sender.doWork();
        MatcherAssert.assertThat(receivedFrames.size(), Is.is(2));// setup then data

        receivedFrames.remove();
        receivedFrames.remove();
        publication.triggerSendSetupFrame();
        sender.doWork();
        MatcherAssert.assertThat(receivedFrames.size(), Is.is(0));// setup has been sent already, have to wait

        currentTimestamp += (PUBLICATION_SETUP_TIMEOUT_NS) + 10;
        sender.doWork();
        MatcherAssert.assertThat(receivedFrames.size(), Is.is(1));
        setupHeader.wrap(new UnsafeBuffer(receivedFrames.remove()));
        MatcherAssert.assertThat(setupHeader.headerType(), Is.is(HDR_TYPE_SETUP));
    }

    @Test
    public void shouldBeAbleToSendOnChannel() {
        final StatusMessageFlyweight msg = Mockito.mock(StatusMessageFlyweight.class);
        Mockito.when(msg.consumptionTermId()).thenReturn(SenderTest.INITIAL_TERM_ID);
        Mockito.when(msg.consumptionTermOffset()).thenReturn(0);
        Mockito.when(msg.receiverWindowLength()).thenReturn(SenderTest.ALIGNED_FRAME_LENGTH);
        publication.onStatusMessage(msg, rcvAddress);
        final UnsafeBuffer buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(SenderTest.PAYLOAD.length));
        buffer.putBytes(0, SenderTest.PAYLOAD);
        termAppenders[0].appendUnfragmentedMessage(headerWriter, buffer, 0, SenderTest.PAYLOAD.length, null, SenderTest.INITIAL_TERM_ID);
        sender.doWork();
        MatcherAssert.assertThat(receivedFrames.size(), Is.is(2));
        setupHeader.wrap(new UnsafeBuffer(receivedFrames.remove()));
        MatcherAssert.assertThat(setupHeader.headerType(), Is.is(HDR_TYPE_SETUP));
        dataHeader.wrap(new UnsafeBuffer(receivedFrames.remove()));
        MatcherAssert.assertThat(dataHeader.frameLength(), Is.is(SenderTest.FRAME_LENGTH));
        MatcherAssert.assertThat(dataHeader.termId(), Is.is(SenderTest.INITIAL_TERM_ID));
        MatcherAssert.assertThat(dataHeader.streamId(), Is.is(SenderTest.STREAM_ID));
        MatcherAssert.assertThat(dataHeader.sessionId(), Is.is(SenderTest.SESSION_ID));
        MatcherAssert.assertThat(dataHeader.termOffset(), Is.is(offsetOfMessage(1)));
        MatcherAssert.assertThat(dataHeader.headerType(), Is.is(HDR_TYPE_DATA));
        MatcherAssert.assertThat(dataHeader.flags(), Is.is(BEGIN_AND_END_FLAGS));
        MatcherAssert.assertThat(dataHeader.version(), Is.is(((short) (CURRENT_VERSION))));
    }

    @Test
    public void shouldBeAbleToSendOnChannelTwice() {
        final StatusMessageFlyweight msg = Mockito.mock(StatusMessageFlyweight.class);
        Mockito.when(msg.consumptionTermId()).thenReturn(SenderTest.INITIAL_TERM_ID);
        Mockito.when(msg.consumptionTermOffset()).thenReturn(0);
        Mockito.when(msg.receiverWindowLength()).thenReturn((2 * (SenderTest.ALIGNED_FRAME_LENGTH)));
        publication.onStatusMessage(msg, rcvAddress);
        final UnsafeBuffer buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(SenderTest.PAYLOAD.length));
        buffer.putBytes(0, SenderTest.PAYLOAD);
        termAppenders[0].appendUnfragmentedMessage(headerWriter, buffer, 0, SenderTest.PAYLOAD.length, null, SenderTest.INITIAL_TERM_ID);
        sender.doWork();
        termAppenders[0].appendUnfragmentedMessage(headerWriter, buffer, 0, SenderTest.PAYLOAD.length, null, SenderTest.INITIAL_TERM_ID);
        sender.doWork();
        MatcherAssert.assertThat(receivedFrames.size(), Is.is(3));
        setupHeader.wrap(new UnsafeBuffer(receivedFrames.remove()));
        MatcherAssert.assertThat(setupHeader.headerType(), Is.is(HDR_TYPE_SETUP));
        dataHeader.wrap(new UnsafeBuffer(receivedFrames.remove()));
        MatcherAssert.assertThat(dataHeader.frameLength(), Is.is(SenderTest.FRAME_LENGTH));
        MatcherAssert.assertThat(dataHeader.termId(), Is.is(SenderTest.INITIAL_TERM_ID));
        MatcherAssert.assertThat(dataHeader.streamId(), Is.is(SenderTest.STREAM_ID));
        MatcherAssert.assertThat(dataHeader.sessionId(), Is.is(SenderTest.SESSION_ID));
        MatcherAssert.assertThat(dataHeader.termOffset(), Is.is(offsetOfMessage(1)));
        MatcherAssert.assertThat(dataHeader.headerType(), Is.is(HDR_TYPE_DATA));
        MatcherAssert.assertThat(dataHeader.flags(), Is.is(BEGIN_AND_END_FLAGS));
        MatcherAssert.assertThat(dataHeader.version(), Is.is(((short) (CURRENT_VERSION))));
        dataHeader.wrap(new UnsafeBuffer(receivedFrames.remove()));
        MatcherAssert.assertThat(dataHeader.frameLength(), Is.is(SenderTest.FRAME_LENGTH));
        MatcherAssert.assertThat(dataHeader.termId(), Is.is(SenderTest.INITIAL_TERM_ID));
        MatcherAssert.assertThat(dataHeader.streamId(), Is.is(SenderTest.STREAM_ID));
        MatcherAssert.assertThat(dataHeader.sessionId(), Is.is(SenderTest.SESSION_ID));
        MatcherAssert.assertThat(dataHeader.termOffset(), Is.is(offsetOfMessage(2)));
        MatcherAssert.assertThat(dataHeader.headerType(), Is.is(HDR_TYPE_DATA));
        MatcherAssert.assertThat(dataHeader.flags(), Is.is(BEGIN_AND_END_FLAGS));
        MatcherAssert.assertThat(dataHeader.version(), Is.is(((short) (CURRENT_VERSION))));
    }

    @Test
    public void shouldNotSendUntilStatusMessageReceived() {
        final UnsafeBuffer buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(SenderTest.PAYLOAD.length));
        buffer.putBytes(0, SenderTest.PAYLOAD);
        termAppenders[0].appendUnfragmentedMessage(headerWriter, buffer, 0, SenderTest.PAYLOAD.length, null, SenderTest.INITIAL_TERM_ID);
        sender.doWork();
        MatcherAssert.assertThat(receivedFrames.size(), Is.is(1));
        setupHeader.wrap(receivedFrames.remove());
        MatcherAssert.assertThat(setupHeader.headerType(), Is.is(HDR_TYPE_SETUP));
        final StatusMessageFlyweight msg = Mockito.mock(StatusMessageFlyweight.class);
        Mockito.when(msg.consumptionTermId()).thenReturn(SenderTest.INITIAL_TERM_ID);
        Mockito.when(msg.consumptionTermOffset()).thenReturn(0);
        Mockito.when(msg.receiverWindowLength()).thenReturn(SenderTest.ALIGNED_FRAME_LENGTH);
        publication.onStatusMessage(msg, rcvAddress);
        sender.doWork();
        MatcherAssert.assertThat(receivedFrames.size(), Is.is(1));
        dataHeader.wrap(new UnsafeBuffer(receivedFrames.remove()));
        MatcherAssert.assertThat(dataHeader.frameLength(), Is.is(SenderTest.FRAME_LENGTH));
        MatcherAssert.assertThat(dataHeader.termId(), Is.is(SenderTest.INITIAL_TERM_ID));
        MatcherAssert.assertThat(dataHeader.streamId(), Is.is(SenderTest.STREAM_ID));
        MatcherAssert.assertThat(dataHeader.sessionId(), Is.is(SenderTest.SESSION_ID));
        MatcherAssert.assertThat(dataHeader.termOffset(), Is.is(offsetOfMessage(1)));
        MatcherAssert.assertThat(dataHeader.headerType(), Is.is(HDR_TYPE_DATA));
        MatcherAssert.assertThat(dataHeader.flags(), Is.is(BEGIN_AND_END_FLAGS));
        MatcherAssert.assertThat(dataHeader.version(), Is.is(((short) (CURRENT_VERSION))));
    }

    @Test
    public void shouldNotBeAbleToSendAfterUsingUpYourWindow() {
        final UnsafeBuffer buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(SenderTest.PAYLOAD.length));
        buffer.putBytes(0, SenderTest.PAYLOAD);
        termAppenders[0].appendUnfragmentedMessage(headerWriter, buffer, 0, SenderTest.PAYLOAD.length, null, SenderTest.INITIAL_TERM_ID);
        final StatusMessageFlyweight msg = Mockito.mock(StatusMessageFlyweight.class);
        Mockito.when(msg.consumptionTermId()).thenReturn(SenderTest.INITIAL_TERM_ID);
        Mockito.when(msg.consumptionTermOffset()).thenReturn(0);
        Mockito.when(msg.receiverWindowLength()).thenReturn(SenderTest.ALIGNED_FRAME_LENGTH);
        publication.onStatusMessage(msg, rcvAddress);
        sender.doWork();
        MatcherAssert.assertThat(receivedFrames.size(), Is.is(2));
        receivedFrames.remove();
        // skip setup
        dataHeader.wrap(new UnsafeBuffer(receivedFrames.remove()));
        MatcherAssert.assertThat(dataHeader.frameLength(), Is.is(SenderTest.FRAME_LENGTH));
        MatcherAssert.assertThat(dataHeader.termId(), Is.is(SenderTest.INITIAL_TERM_ID));
        MatcherAssert.assertThat(dataHeader.streamId(), Is.is(SenderTest.STREAM_ID));
        MatcherAssert.assertThat(dataHeader.sessionId(), Is.is(SenderTest.SESSION_ID));
        MatcherAssert.assertThat(dataHeader.termOffset(), Is.is(offsetOfMessage(1)));
        MatcherAssert.assertThat(dataHeader.headerType(), Is.is(HDR_TYPE_DATA));
        MatcherAssert.assertThat(dataHeader.flags(), Is.is(BEGIN_AND_END_FLAGS));
        MatcherAssert.assertThat(dataHeader.version(), Is.is(((short) (CURRENT_VERSION))));
        termAppenders[0].appendUnfragmentedMessage(headerWriter, buffer, 0, SenderTest.PAYLOAD.length, null, SenderTest.INITIAL_TERM_ID);
        sender.doWork();
        MatcherAssert.assertThat(receivedFrames.size(), Is.is(0));
    }

    @Test
    public void shouldSendLastDataFrameAsHeartbeatWhenIdle() {
        final StatusMessageFlyweight msg = Mockito.mock(StatusMessageFlyweight.class);
        Mockito.when(msg.consumptionTermId()).thenReturn(SenderTest.INITIAL_TERM_ID);
        Mockito.when(msg.consumptionTermOffset()).thenReturn(0);
        Mockito.when(msg.receiverWindowLength()).thenReturn(SenderTest.ALIGNED_FRAME_LENGTH);
        publication.onStatusMessage(msg, rcvAddress);
        final UnsafeBuffer buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(SenderTest.PAYLOAD.length));
        buffer.putBytes(0, SenderTest.PAYLOAD);
        termAppenders[0].appendUnfragmentedMessage(headerWriter, buffer, 0, SenderTest.PAYLOAD.length, null, SenderTest.INITIAL_TERM_ID);
        sender.doWork();
        MatcherAssert.assertThat(receivedFrames.size(), Is.is(2));// should send ticks

        receivedFrames.remove();
        // skip setup & data frame
        receivedFrames.remove();
        currentTimestamp += (PUBLICATION_HEARTBEAT_TIMEOUT_NS) - 1;
        sender.doWork();
        MatcherAssert.assertThat(receivedFrames.size(), Is.is(0));// should not send yet

        currentTimestamp += 10;
        sender.doWork();
        MatcherAssert.assertThat(receivedFrames.size(), Matchers.greaterThanOrEqualTo(1));// should send ticks

        dataHeader.wrap(receivedFrames.remove());
        MatcherAssert.assertThat(dataHeader.frameLength(), Is.is(0));
        MatcherAssert.assertThat(dataHeader.termOffset(), Is.is(offsetOfMessage(2)));
    }

    @Test
    public void shouldSendMultipleDataFramesAsHeartbeatsWhenIdle() {
        final StatusMessageFlyweight msg = Mockito.mock(StatusMessageFlyweight.class);
        Mockito.when(msg.consumptionTermId()).thenReturn(SenderTest.INITIAL_TERM_ID);
        Mockito.when(msg.consumptionTermOffset()).thenReturn(0);
        Mockito.when(msg.receiverWindowLength()).thenReturn(SenderTest.ALIGNED_FRAME_LENGTH);
        publication.onStatusMessage(msg, rcvAddress);
        final UnsafeBuffer buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(SenderTest.PAYLOAD.length));
        buffer.putBytes(0, SenderTest.PAYLOAD);
        termAppenders[0].appendUnfragmentedMessage(headerWriter, buffer, 0, SenderTest.PAYLOAD.length, null, SenderTest.INITIAL_TERM_ID);
        sender.doWork();
        MatcherAssert.assertThat(receivedFrames.size(), Is.is(2));// should send ticks

        receivedFrames.remove();
        receivedFrames.remove();
        // skip setup & data frame
        currentTimestamp += (PUBLICATION_HEARTBEAT_TIMEOUT_NS) - 1;
        sender.doWork();
        MatcherAssert.assertThat(receivedFrames.size(), Is.is(0));// should not send yet

        currentTimestamp += 10;
        sender.doWork();
        MatcherAssert.assertThat(receivedFrames.size(), Matchers.greaterThanOrEqualTo(1));// should send ticks

        dataHeader.wrap(receivedFrames.remove());
        MatcherAssert.assertThat(dataHeader.frameLength(), Is.is(0));
        MatcherAssert.assertThat(dataHeader.termOffset(), Is.is(offsetOfMessage(2)));
        currentTimestamp += (PUBLICATION_HEARTBEAT_TIMEOUT_NS) - 1;
        sender.doWork();
        MatcherAssert.assertThat(receivedFrames.size(), Is.is(0));// should not send yet

        currentTimestamp += 10;
        sender.doWork();
        MatcherAssert.assertThat(receivedFrames.size(), Matchers.greaterThanOrEqualTo(1));// should send ticks

        dataHeader.wrap(receivedFrames.remove());
        MatcherAssert.assertThat(dataHeader.frameLength(), Is.is(0));
        MatcherAssert.assertThat(dataHeader.termOffset(), Is.is(offsetOfMessage(2)));
    }
}

