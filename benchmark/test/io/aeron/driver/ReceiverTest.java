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


import HeaderFlyweight.HDR_TYPE_SM;
import SetupFlyweight.HEADER_LENGTH;
import io.aeron.driver.buffer.RawLog;
import io.aeron.driver.buffer.RawLogFactory;
import io.aeron.driver.buffer.UdpChannel;
import io.aeron.driver.buffer.UnsafeBuffer;
import io.aeron.driver.reports.LossReport;
import io.aeron.driver.status.SystemCounters;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import org.agrona.ErrorHandler;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static Configuration.CMD_QUEUE_CAPACITY;
import static Configuration.INITIAL_WINDOW_LENGTH_DEFAULT;
import static Configuration.STATUS_MESSAGE_TIMEOUT_DEFAULT_NS;
import static DataHeaderFlyweight.HEADER_LENGTH;


public class ReceiverTest {
    private static final int TERM_BUFFER_LENGTH = TERM_MIN_LENGTH;

    private static final int POSITION_BITS_TO_SHIFT = LogBufferDescriptor.positionBitsToShift(ReceiverTest.TERM_BUFFER_LENGTH);

    private static final String URI = "aeron:udp?endpoint=localhost:45678";

    private static final UdpChannel UDP_CHANNEL = UdpChannel.parse(ReceiverTest.URI);

    private static final long IMAGE_LIVENESS_TIMEOUT_NS = Configuration.imageLivenessTimeoutNs();

    private static final long CORRELATION_ID = 20;

    private static final int STREAM_ID = 10;

    private static final int INITIAL_TERM_ID = 3;

    private static final int ACTIVE_TERM_ID = 3;

    private static final int SESSION_ID = 1;

    private static final int INITIAL_TERM_OFFSET = 0;

    private static final int ACTIVE_INDEX = indexByTerm(ReceiverTest.ACTIVE_TERM_ID, ReceiverTest.ACTIVE_TERM_ID);

    private static final byte[] FAKE_PAYLOAD = "Hello there, message!".getBytes();

    private static final int INITIAL_WINDOW_LENGTH = INITIAL_WINDOW_LENGTH_DEFAULT;

    private static final long STATUS_MESSAGE_TIMEOUT = STATUS_MESSAGE_TIMEOUT_DEFAULT_NS;

    private static final InetSocketAddress SOURCE_ADDRESS = new InetSocketAddress("localhost", 45679);

    private static final ReadablePosition POSITION = Mockito.mock(ReadablePosition.class);

    private static final ReadablePosition[] POSITIONS = new ReadablePosition[]{ ReceiverTest.POSITION };

    private final FeedbackDelayGenerator mockFeedbackDelayGenerator = Mockito.mock(FeedbackDelayGenerator.class);

    private final DataTransportPoller mockDataTransportPoller = Mockito.mock(DataTransportPoller.class);

    private final ControlTransportPoller mockControlTransportPoller = Mockito.mock(ControlTransportPoller.class);

    private final SystemCounters mockSystemCounters = Mockito.mock(SystemCounters.class);

    private final RawLogFactory mockRawLogFactory = Mockito.mock(RawLogFactory.class);

    private final Position mockHighestReceivedPosition = Mockito.spy(new AtomicLongPosition());

    private final Position mockRebuildPosition = Mockito.spy(new AtomicLongPosition());

    private final Position mockSubscriberPosition = Mockito.mock(Position.class);

    private final ByteBuffer dataFrameBuffer = ByteBuffer.allocateDirect((2 * 1024));

    private final UnsafeBuffer dataBuffer = new UnsafeBuffer(dataFrameBuffer);

    private final ByteBuffer setupFrameBuffer = ByteBuffer.allocateDirect(HEADER_LENGTH);

    private final UnsafeBuffer setupBuffer = new UnsafeBuffer(setupFrameBuffer);

    private final ErrorHandler mockErrorHandler = Mockito.mock(ErrorHandler.class);

    private final DataHeaderFlyweight dataHeader = new DataHeaderFlyweight();

    private final StatusMessageFlyweight statusHeader = new StatusMessageFlyweight();

    private final SetupFlyweight setupHeader = new SetupFlyweight();

    private long currentTime = 0;

    private final NanoClock nanoClock = () -> currentTime;

    private final EpochClock epochClock = Mockito.mock(EpochClock.class);

    private final LossReport lossReport = Mockito.mock(LossReport.class);

    private final RawLog rawLog = LogBufferHelper.newTestLogBuffers(ReceiverTest.TERM_BUFFER_LENGTH);

    private final Header header = new Header(ReceiverTest.INITIAL_TERM_ID, ReceiverTest.TERM_BUFFER_LENGTH);

    private UnsafeBuffer[] termBuffers;

    private DatagramChannel senderChannel;

    private final InetSocketAddress senderAddress = new InetSocketAddress("localhost", 40123);

    private Receiver receiver;

    private ReceiverProxy receiverProxy;

    private final ManyToOneConcurrentArrayQueue<Runnable> toConductorQueue = new ManyToOneConcurrentArrayQueue(CMD_QUEUE_CAPACITY);

    private ReceiveChannelEndpoint receiveChannelEndpoint;

    private final CongestionControl congestionControl = Mockito.mock(CongestionControl.class);

    @Test(timeout = 10000)
    public void shouldCreateRcvTermAndSendSmOnSetup() throws Exception {
        receiverProxy.registerReceiveChannelEndpoint(receiveChannelEndpoint);
        receiverProxy.addSubscription(receiveChannelEndpoint, ReceiverTest.STREAM_ID);
        receiver.doWork();
        fillSetupFrame(setupHeader);
        receiveChannelEndpoint.onSetupMessage(setupHeader, setupBuffer, HEADER_LENGTH, senderAddress, 0);
        final PublicationImage image = new PublicationImage(ReceiverTest.CORRELATION_ID, ReceiverTest.IMAGE_LIVENESS_TIMEOUT_NS, receiveChannelEndpoint, 0, senderAddress, ReceiverTest.SESSION_ID, ReceiverTest.STREAM_ID, ReceiverTest.INITIAL_TERM_ID, ReceiverTest.ACTIVE_TERM_ID, ReceiverTest.INITIAL_TERM_OFFSET, rawLog, mockFeedbackDelayGenerator, ReceiverTest.POSITIONS, mockHighestReceivedPosition, mockRebuildPosition, nanoClock, nanoClock, epochClock, mockSystemCounters, ReceiverTest.SOURCE_ADDRESS, congestionControl, lossReport, true);
        final int messagesRead = toConductorQueue.drain(( e) -> {
            // pass in new term buffer from conductor, which should trigger SM
            receiverProxy.newPublicationImage(receiveChannelEndpoint, image);
        });
        MatcherAssert.assertThat(messagesRead, Is.is(1));
        receiver.doWork();
        image.trackRebuild(((currentTime) + (2 * (ReceiverTest.STATUS_MESSAGE_TIMEOUT))), ReceiverTest.STATUS_MESSAGE_TIMEOUT);
        image.sendPendingStatusMessage();
        final ByteBuffer rcvBuffer = ByteBuffer.allocateDirect(256);
        InetSocketAddress rcvAddress;
        do {
            rcvAddress = ((InetSocketAddress) (senderChannel.receive(rcvBuffer)));
        } while (null == rcvAddress );
        statusHeader.wrap(new UnsafeBuffer(rcvBuffer));
        Assert.assertNotNull(rcvAddress);
        MatcherAssert.assertThat(rcvAddress.getPort(), Is.is(ReceiverTest.UDP_CHANNEL.remoteData().getPort()));
        MatcherAssert.assertThat(statusHeader.headerType(), Is.is(HDR_TYPE_SM));
        MatcherAssert.assertThat(statusHeader.streamId(), Is.is(ReceiverTest.STREAM_ID));
        MatcherAssert.assertThat(statusHeader.sessionId(), Is.is(ReceiverTest.SESSION_ID));
        MatcherAssert.assertThat(statusHeader.consumptionTermId(), Is.is(ReceiverTest.ACTIVE_TERM_ID));
        MatcherAssert.assertThat(statusHeader.frameLength(), Is.is(StatusMessageFlyweight.HEADER_LENGTH));
    }

    @Test
    public void shouldInsertDataIntoLogAfterInitialExchange() {
        receiverProxy.registerReceiveChannelEndpoint(receiveChannelEndpoint);
        receiverProxy.addSubscription(receiveChannelEndpoint, ReceiverTest.STREAM_ID);
        receiver.doWork();
        fillSetupFrame(setupHeader);
        receiveChannelEndpoint.onSetupMessage(setupHeader, setupBuffer, HEADER_LENGTH, senderAddress, 0);
        final int commandsRead = toConductorQueue.drain(( e) -> {
            // pass in new term buffer from conductor, which should trigger SM
            final PublicationImage image = new PublicationImage(CORRELATION_ID, IMAGE_LIVENESS_TIMEOUT_NS, receiveChannelEndpoint, 0, senderAddress, SESSION_ID, STREAM_ID, INITIAL_TERM_ID, ACTIVE_TERM_ID, INITIAL_TERM_OFFSET, rawLog, mockFeedbackDelayGenerator, POSITIONS, mockHighestReceivedPosition, mockRebuildPosition, nanoClock, nanoClock, epochClock, mockSystemCounters, SOURCE_ADDRESS, congestionControl, lossReport, true);
            receiverProxy.newPublicationImage(receiveChannelEndpoint, image);
        });
        MatcherAssert.assertThat(commandsRead, Is.is(1));
        receiver.doWork();
        fillDataFrame(dataHeader, 0, ReceiverTest.FAKE_PAYLOAD);
        receiveChannelEndpoint.onDataPacket(dataHeader, dataBuffer, dataHeader.frameLength(), senderAddress, 0);
        final int readOutcome = TermReader.read(termBuffers[ReceiverTest.ACTIVE_INDEX], ReceiverTest.INITIAL_TERM_OFFSET, ( buffer, offset, length, header) -> {
            assertThat(header.type(), is(HeaderFlyweight.HDR_TYPE_DATA));
            assertThat(header.termId(), is(ACTIVE_TERM_ID));
            assertThat(header.streamId(), is(STREAM_ID));
            assertThat(header.sessionId(), is(SESSION_ID));
            assertThat(header.termOffset(), is(0));
            assertThat(header.frameLength(), is((DataHeaderFlyweight.HEADER_LENGTH + FAKE_PAYLOAD.length)));
        }, Integer.MAX_VALUE, header, mockErrorHandler, 0, mockSubscriberPosition);
        MatcherAssert.assertThat(readOutcome, Is.is(1));
    }

    @Test
    public void shouldNotOverwriteDataFrameWithHeartbeat() {
        receiverProxy.registerReceiveChannelEndpoint(receiveChannelEndpoint);
        receiverProxy.addSubscription(receiveChannelEndpoint, ReceiverTest.STREAM_ID);
        receiver.doWork();
        fillSetupFrame(setupHeader);
        receiveChannelEndpoint.onSetupMessage(setupHeader, setupBuffer, HEADER_LENGTH, senderAddress, 0);
        final int commandsRead = toConductorQueue.drain(( e) -> {
            // pass in new term buffer from conductor, which should trigger SM
            final PublicationImage image = new PublicationImage(CORRELATION_ID, IMAGE_LIVENESS_TIMEOUT_NS, receiveChannelEndpoint, 0, senderAddress, SESSION_ID, STREAM_ID, INITIAL_TERM_ID, ACTIVE_TERM_ID, INITIAL_TERM_OFFSET, rawLog, mockFeedbackDelayGenerator, POSITIONS, mockHighestReceivedPosition, mockRebuildPosition, nanoClock, nanoClock, epochClock, mockSystemCounters, SOURCE_ADDRESS, congestionControl, lossReport, true);
            receiverProxy.newPublicationImage(receiveChannelEndpoint, image);
        });
        MatcherAssert.assertThat(commandsRead, Is.is(1));
        receiver.doWork();
        fillDataFrame(dataHeader, 0, ReceiverTest.FAKE_PAYLOAD);// initial data frame

        receiveChannelEndpoint.onDataPacket(dataHeader, dataBuffer, dataHeader.frameLength(), senderAddress, 0);
        fillDataFrame(dataHeader, 0, ReceiverTest.FAKE_PAYLOAD);// heartbeat with same term offset

        receiveChannelEndpoint.onDataPacket(dataHeader, dataBuffer, dataHeader.frameLength(), senderAddress, 0);
        final int readOutcome = TermReader.read(termBuffers[ReceiverTest.ACTIVE_INDEX], ReceiverTest.INITIAL_TERM_OFFSET, ( buffer, offset, length, header) -> {
            assertThat(header.type(), is(HeaderFlyweight.HDR_TYPE_DATA));
            assertThat(header.termId(), is(ACTIVE_TERM_ID));
            assertThat(header.streamId(), is(STREAM_ID));
            assertThat(header.sessionId(), is(SESSION_ID));
            assertThat(header.termOffset(), is(0));
            assertThat(header.frameLength(), is((DataHeaderFlyweight.HEADER_LENGTH + FAKE_PAYLOAD.length)));
        }, Integer.MAX_VALUE, header, mockErrorHandler, 0, mockSubscriberPosition);
        MatcherAssert.assertThat(readOutcome, Is.is(1));
    }

    @Test
    public void shouldOverwriteHeartbeatWithDataFrame() {
        receiverProxy.registerReceiveChannelEndpoint(receiveChannelEndpoint);
        receiverProxy.addSubscription(receiveChannelEndpoint, ReceiverTest.STREAM_ID);
        receiver.doWork();
        fillSetupFrame(setupHeader);
        receiveChannelEndpoint.onSetupMessage(setupHeader, setupBuffer, HEADER_LENGTH, senderAddress, 0);
        final int commandsRead = toConductorQueue.drain(( e) -> {
            // pass in new term buffer from conductor, which should trigger SM
            final PublicationImage image = new PublicationImage(CORRELATION_ID, Configuration.imageLivenessTimeoutNs(), receiveChannelEndpoint, 0, senderAddress, SESSION_ID, STREAM_ID, INITIAL_TERM_ID, ACTIVE_TERM_ID, INITIAL_TERM_OFFSET, rawLog, mockFeedbackDelayGenerator, POSITIONS, mockHighestReceivedPosition, mockRebuildPosition, nanoClock, nanoClock, epochClock, mockSystemCounters, SOURCE_ADDRESS, congestionControl, lossReport, true);
            receiverProxy.newPublicationImage(receiveChannelEndpoint, image);
        });
        MatcherAssert.assertThat(commandsRead, Is.is(1));
        receiver.doWork();
        fillDataFrame(dataHeader, 0, ReceiverTest.FAKE_PAYLOAD);// heartbeat with same term offset

        receiveChannelEndpoint.onDataPacket(dataHeader, dataBuffer, dataHeader.frameLength(), senderAddress, 0);
        fillDataFrame(dataHeader, 0, ReceiverTest.FAKE_PAYLOAD);// initial data frame

        receiveChannelEndpoint.onDataPacket(dataHeader, dataBuffer, dataHeader.frameLength(), senderAddress, 0);
        final int readOutcome = TermReader.read(termBuffers[ReceiverTest.ACTIVE_INDEX], ReceiverTest.INITIAL_TERM_OFFSET, ( buffer, offset, length, header) -> {
            assertThat(header.type(), is(HeaderFlyweight.HDR_TYPE_DATA));
            assertThat(header.termId(), is(ACTIVE_TERM_ID));
            assertThat(header.streamId(), is(STREAM_ID));
            assertThat(header.sessionId(), is(SESSION_ID));
            assertThat(header.termOffset(), is(0));
            assertThat(header.frameLength(), is((DataHeaderFlyweight.HEADER_LENGTH + FAKE_PAYLOAD.length)));
        }, Integer.MAX_VALUE, header, mockErrorHandler, 0, mockSubscriberPosition);
        MatcherAssert.assertThat(readOutcome, Is.is(1));
    }

    @Test
    public void shouldHandleNonZeroTermOffsetCorrectly() {
        final int initialTermOffset = align(((ReceiverTest.TERM_BUFFER_LENGTH) / 16), FrameDescriptor.FRAME_ALIGNMENT);
        final int alignedDataFrameLength = align(((HEADER_LENGTH) + (ReceiverTest.FAKE_PAYLOAD.length)), FrameDescriptor.FRAME_ALIGNMENT);
        receiverProxy.registerReceiveChannelEndpoint(receiveChannelEndpoint);
        receiverProxy.addSubscription(receiveChannelEndpoint, ReceiverTest.STREAM_ID);
        receiver.doWork();
        fillSetupFrame(setupHeader, initialTermOffset);
        receiveChannelEndpoint.onSetupMessage(setupHeader, setupBuffer, HEADER_LENGTH, senderAddress, 0);
        final int commandsRead = toConductorQueue.drain(( e) -> {
            // pass in new term buffer from conductor, which should trigger SM
            final PublicationImage image = new PublicationImage(CORRELATION_ID, IMAGE_LIVENESS_TIMEOUT_NS, receiveChannelEndpoint, 0, senderAddress, SESSION_ID, STREAM_ID, INITIAL_TERM_ID, ACTIVE_TERM_ID, initialTermOffset, rawLog, mockFeedbackDelayGenerator, POSITIONS, mockHighestReceivedPosition, mockRebuildPosition, nanoClock, nanoClock, epochClock, mockSystemCounters, SOURCE_ADDRESS, congestionControl, lossReport, true);
            receiverProxy.newPublicationImage(receiveChannelEndpoint, image);
        });
        MatcherAssert.assertThat(commandsRead, Is.is(1));
        Mockito.verify(mockHighestReceivedPosition).setOrdered(initialTermOffset);
        receiver.doWork();
        fillDataFrame(dataHeader, initialTermOffset, ReceiverTest.FAKE_PAYLOAD);// initial data frame

        receiveChannelEndpoint.onDataPacket(dataHeader, dataBuffer, alignedDataFrameLength, senderAddress, 0);
        Mockito.verify(mockHighestReceivedPosition).setOrdered((initialTermOffset + alignedDataFrameLength));
        final int readOutcome = TermReader.read(termBuffers[ReceiverTest.ACTIVE_INDEX], initialTermOffset, ( buffer, offset, length, header) -> {
            assertThat(header.type(), is(HeaderFlyweight.HDR_TYPE_DATA));
            assertThat(header.termId(), is(ACTIVE_TERM_ID));
            assertThat(header.streamId(), is(STREAM_ID));
            assertThat(header.sessionId(), is(SESSION_ID));
            assertThat(header.termOffset(), is(initialTermOffset));
            assertThat(header.frameLength(), is((DataHeaderFlyweight.HEADER_LENGTH + FAKE_PAYLOAD.length)));
        }, Integer.MAX_VALUE, header, mockErrorHandler, 0, mockSubscriberPosition);
        MatcherAssert.assertThat(readOutcome, Is.is(1));
    }

    @Test
    public void shouldRemoveImageFromDispatcherWithNoActivity() {
        receiverProxy.registerReceiveChannelEndpoint(receiveChannelEndpoint);
        receiverProxy.addSubscription(receiveChannelEndpoint, ReceiverTest.STREAM_ID);
        receiver.doWork();
        fillSetupFrame(setupHeader);
        receiveChannelEndpoint.onSetupMessage(setupHeader, setupBuffer, HEADER_LENGTH, senderAddress, 0);
        final PublicationImage mockImage = Mockito.mock(PublicationImage.class);
        Mockito.when(mockImage.sessionId()).thenReturn(ReceiverTest.SESSION_ID);
        Mockito.when(mockImage.streamId()).thenReturn(ReceiverTest.STREAM_ID);
        Mockito.when(mockImage.hasActivityAndNotEndOfStream(ArgumentMatchers.anyLong())).thenReturn(false);
        receiver.onNewPublicationImage(receiveChannelEndpoint, mockImage);
        receiver.doWork();
        Mockito.verify(mockImage).removeFromDispatcher();
    }

    @Test
    public void shouldNotRemoveImageFromDispatcherOnRemoveSubscription() {
        receiverProxy.registerReceiveChannelEndpoint(receiveChannelEndpoint);
        receiverProxy.addSubscription(receiveChannelEndpoint, ReceiverTest.STREAM_ID);
        receiver.doWork();
        fillSetupFrame(setupHeader);
        receiveChannelEndpoint.onSetupMessage(setupHeader, setupBuffer, HEADER_LENGTH, senderAddress, 0);
        final PublicationImage mockImage = Mockito.mock(PublicationImage.class);
        Mockito.when(mockImage.sessionId()).thenReturn(ReceiverTest.SESSION_ID);
        Mockito.when(mockImage.streamId()).thenReturn(ReceiverTest.STREAM_ID);
        Mockito.when(mockImage.hasActivityAndNotEndOfStream(ArgumentMatchers.anyLong())).thenReturn(true);
        receiver.onNewPublicationImage(receiveChannelEndpoint, mockImage);
        receiver.onRemoveSubscription(receiveChannelEndpoint, ReceiverTest.STREAM_ID);
        receiver.doWork();
        Mockito.verify(mockImage).ifActiveGoInactive();
        Mockito.verify(mockImage, Mockito.never()).removeFromDispatcher();
    }
}

