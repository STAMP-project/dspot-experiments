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


import NetworkPublication.State.ACTIVE;
import NetworkPublication.State.CLOSING;
import NetworkPublication.State.DRAINING;
import NetworkPublication.State.LINGER;
import io.aeron.CommonContext;
import io.aeron.DriverProxy;
import io.aeron.ErrorCode;
import io.aeron.driver.buffer.RawLog;
import io.aeron.driver.buffer.RawLogFactory;
import io.aeron.driver.media.ReceiveChannelEndpoint;
import io.aeron.driver.media.UdpChannel;
import io.aeron.logbuffer.HeaderWriter;
import io.aeron.logbuffer.LogBufferDescriptor;
import io.aeron.logbuffer.TermAppender;
import io.aeron.protocol.StatusMessageFlyweight;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.function.BooleanSupplier;
import java.util.function.LongConsumer;
import org.agrona.ErrorHandler;
import org.agrona.concurrent.ringbuffer.RingBuffer;
import org.agrona.concurrent.status.AtomicCounter;
import org.agrona.concurrent.status.CountersManager;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static io.aeron.driver.Configuration.Configuration.TERM_BUFFER_LENGTH_DEFAULT;


public class DriverConductorTest {
    private static final String CHANNEL_4000 = "aeron:udp?endpoint=localhost:4000";

    private static final String CHANNEL_4001 = "aeron:udp?endpoint=localhost:4001";

    private static final String CHANNEL_4002 = "aeron:udp?endpoint=localhost:4002";

    private static final String CHANNEL_4003 = "aeron:udp?endpoint=localhost:4003";

    private static final String CHANNEL_4004 = "aeron:udp?endpoint=localhost:4004";

    private static final String CHANNEL_4000_TAG_ID_1 = "aeron:udp?endpoint=localhost:4000|tags=1001";

    private static final String CHANNEL_TAG_ID_1 = "aeron:udp?tags=1001";

    private static final String CHANNEL_SUB_CONTROL_MODE_MANUAL = "aeron:udp?control-mode=manual";

    private static final String CHANNEL_IPC = "aeron:ipc";

    private static final String INVALID_URI = "aeron:udp://";

    private static final String COUNTER_LABEL = "counter label";

    private static final int SESSION_ID = 100;

    private static final int STREAM_ID_1 = 10;

    private static final int STREAM_ID_2 = 20;

    private static final int STREAM_ID_3 = 30;

    private static final int STREAM_ID_4 = 40;

    private static final int TERM_BUFFER_LENGTH = TERM_BUFFER_LENGTH_DEFAULT;

    private static final int BUFFER_LENGTH = 16 * 1024;

    private static final int COUNTER_TYPE_ID = 101;

    private static final int COUNTER_KEY_OFFSET = 0;

    private static final int COUNTER_KEY_LENGTH = 12;

    private static final int COUNTER_LABEL_OFFSET = (DriverConductorTest.COUNTER_KEY_OFFSET) + (DriverConductorTest.COUNTER_KEY_LENGTH);

    private static final int COUNTER_LABEL_LENGTH = DriverConductorTest.COUNTER_LABEL.length();

    private static final long CLIENT_LIVENESS_TIMEOUT_NS = clientLivenessTimeoutNs();

    private static final long PUBLICATION_LINGER_TIMEOUT_NS = publicationLingerTimeoutNs();

    private static final int MTU_LENGTH = Configuration.Configuration.mtuLength();

    private final ByteBuffer conductorBuffer = ByteBuffer.allocateDirect(CONDUCTOR_BUFFER_LENGTH_DEFAULT);

    private final UnsafeBuffer counterKeyAndLabel = new UnsafeBuffer(new byte[DriverConductorTest.BUFFER_LENGTH]);

    private final RawLogFactory mockRawLogFactory = Mockito.mock(RawLogFactory.class);

    private final RingBuffer toDriverCommands = new org.agrona.concurrent.ringbuffer.ManyToOneRingBuffer(new UnsafeBuffer(conductorBuffer));

    private final ClientProxy mockClientProxy = Mockito.mock(ClientProxy.class);

    private final ErrorHandler mockErrorHandler = Mockito.mock(ErrorHandler.class);

    private final AtomicCounter mockErrorCounter = Mockito.mock(AtomicCounter.class);

    private final SenderProxy senderProxy = Mockito.mock(SenderProxy.class);

    private final ReceiverProxy receiverProxy = Mockito.mock(ReceiverProxy.class);

    private final DriverConductorProxy driverConductorProxy = Mockito.mock(DriverConductorProxy.class);

    private long currentTimeMs;

    private final EpochClock epochClock = () -> currentTimeMs;

    private long currentTimeNs;

    private final NanoClock nanoClock = () -> currentTimeNs;

    private CountersManager spyCountersManager;

    private DriverProxy driverProxy;

    private DriverConductor driverConductor;

    private final Answer<Void> closeChannelEndpointAnswer = ( invocation) -> {
        final Object[] args = invocation.getArguments();
        final ReceiveChannelEndpoint channelEndpoint = ((ReceiveChannelEndpoint) (args[0]));
        channelEndpoint.close();
        return null;
    };

    @Test
    public void shouldBeAbleToAddSinglePublication() {
        driverProxy.addPublication(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        Mockito.verify(senderProxy).registerSendChannelEndpoint(ArgumentMatchers.any());
        final ArgumentCaptor<NetworkPublication> captor = ArgumentCaptor.forClass(NetworkPublication.class);
        Mockito.verify(senderProxy, Mockito.times(1)).newNetworkPublication(captor.capture());
        final NetworkPublication publication = captor.getValue();
        Assert.assertThat(publication.streamId(), Matchers.is(DriverConductorTest.STREAM_ID_1));
        Mockito.verify(mockClientProxy).onPublicationReady(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.anyInt(), ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(false));
    }

    @Test
    public void shouldBeAbleToAddPublicationForReplay() {
        final int mtu = 1024 * 8;
        final int termLength = 128 * 1024;
        final int initialTermId = 7;
        final int termId = 11;
        final int termOffset = 64;
        final String params = (((((((("|mtu=" + mtu) + "|term-length=") + termLength) + "|init-term-id=") + initialTermId) + "|term-id=") + termId) + "|term-offset=") + termOffset;
        Mockito.when(mockRawLogFactory.newNetworkPublication(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(termLength), ArgumentMatchers.anyBoolean())).thenReturn(LogBufferHelper.newTestLogBuffers(termLength));
        driverProxy.addExclusivePublication(((DriverConductorTest.CHANNEL_4000) + params), DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        Mockito.verify(senderProxy).registerSendChannelEndpoint(ArgumentMatchers.any());
        final ArgumentCaptor<NetworkPublication> captor = ArgumentCaptor.forClass(NetworkPublication.class);
        Mockito.verify(senderProxy, Mockito.times(1)).newNetworkPublication(captor.capture());
        final NetworkPublication publication = captor.getValue();
        Assert.assertThat(publication.streamId(), Matchers.is(DriverConductorTest.STREAM_ID_1));
        Assert.assertThat(publication.mtuLength(), Matchers.is(mtu));
        final long expectedPosition = (termLength * (termId - initialTermId)) + termOffset;
        Assert.assertThat(publication.producerPosition(), Matchers.is(expectedPosition));
        Assert.assertThat(publication.consumerPosition(), Matchers.is(expectedPosition));
        Mockito.verify(mockClientProxy).onPublicationReady(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.anyInt(), ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(true));
    }

    @Test
    public void shouldBeAbleToAddIpcPublicationForReplay() {
        final int termLength = 128 * 1024;
        final int initialTermId = 7;
        final int termId = 11;
        final int termOffset = 64;
        final String params = (((((("?term-length=" + termLength) + "|init-term-id=") + initialTermId) + "|term-id=") + termId) + "|term-offset=") + termOffset;
        Mockito.when(mockRawLogFactory.newIpcPublication(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(termLength), ArgumentMatchers.anyBoolean())).thenReturn(LogBufferHelper.newTestLogBuffers(termLength));
        driverProxy.addExclusivePublication(((DriverConductorTest.CHANNEL_IPC) + params), DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(mockClientProxy).onPublicationReady(ArgumentMatchers.anyLong(), captor.capture(), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.anyInt(), ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(true));
        final long registrationId = captor.getValue();
        final IpcPublication publication = driverConductor.getIpcPublication(registrationId);
        Assert.assertThat(publication.streamId(), Matchers.is(DriverConductorTest.STREAM_ID_1));
        final long expectedPosition = (termLength * (termId - initialTermId)) + termOffset;
        Assert.assertThat(publication.producerPosition(), Matchers.is(expectedPosition));
        Assert.assertThat(publication.consumerPosition(), Matchers.is(expectedPosition));
    }

    @Test
    public void shouldBeAbleToAddSingleSubscription() {
        final long id = driverProxy.addSubscription(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        Mockito.verify(receiverProxy).registerReceiveChannelEndpoint(ArgumentMatchers.any());
        Mockito.verify(receiverProxy).addSubscription(ArgumentMatchers.any(), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1));
        Mockito.verify(mockClientProxy).onSubscriptionReady(ArgumentMatchers.eq(id), ArgumentMatchers.anyInt());
        Assert.assertNotNull(driverConductor.receiverChannelEndpoint(UdpChannel.parse(DriverConductorTest.CHANNEL_4000)));
    }

    @Test
    public void shouldBeAbleToAddAndRemoveSingleSubscription() {
        final long id = driverProxy.addSubscription(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        driverProxy.removeSubscription(id);
        driverConductor.doWork();
        Assert.assertNull(driverConductor.receiverChannelEndpoint(UdpChannel.parse(DriverConductorTest.CHANNEL_4000)));
    }

    @Test
    public void shouldBeAbleToAddMultipleStreams() {
        driverProxy.addPublication(DriverConductorTest.CHANNEL_4001, DriverConductorTest.STREAM_ID_1);
        driverProxy.addPublication(DriverConductorTest.CHANNEL_4002, DriverConductorTest.STREAM_ID_2);
        driverProxy.addPublication(DriverConductorTest.CHANNEL_4003, DriverConductorTest.STREAM_ID_3);
        driverProxy.addPublication(DriverConductorTest.CHANNEL_4004, DriverConductorTest.STREAM_ID_4);
        driverConductor.doWork();
        Mockito.verify(senderProxy, Mockito.times(4)).newNetworkPublication(ArgumentMatchers.any());
    }

    @Test
    public void shouldBeAbleToRemoveSingleStream() {
        final long id = driverProxy.addPublication(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        driverProxy.removePublication(id);
        doWorkUntil(() -> (((DriverConductorTest.CLIENT_LIVENESS_TIMEOUT_NS) + ((DriverConductorTest.PUBLICATION_LINGER_TIMEOUT_NS) * 2)) - (nanoClock.nanoTime())) < 0);
        Mockito.verify(senderProxy).removeNetworkPublication(ArgumentMatchers.any());
        Assert.assertNull(driverConductor.senderChannelEndpoint(UdpChannel.parse(DriverConductorTest.CHANNEL_4000)));
    }

    @Test
    public void shouldBeAbleToRemoveMultipleStreams() {
        final long id1 = driverProxy.addPublication(DriverConductorTest.CHANNEL_4001, DriverConductorTest.STREAM_ID_1);
        final long id2 = driverProxy.addPublication(DriverConductorTest.CHANNEL_4002, DriverConductorTest.STREAM_ID_2);
        final long id3 = driverProxy.addPublication(DriverConductorTest.CHANNEL_4003, DriverConductorTest.STREAM_ID_3);
        final long id4 = driverProxy.addPublication(DriverConductorTest.CHANNEL_4004, DriverConductorTest.STREAM_ID_4);
        driverProxy.removePublication(id1);
        driverProxy.removePublication(id2);
        driverProxy.removePublication(id3);
        driverProxy.removePublication(id4);
        doWorkUntil(() -> ((((DriverConductorTest.CLIENT_LIVENESS_TIMEOUT_NS) * 2) + ((DriverConductorTest.PUBLICATION_LINGER_TIMEOUT_NS) * 2)) - (nanoClock.nanoTime())) <= 0);
        Mockito.verify(senderProxy, Mockito.times(4)).removeNetworkPublication(ArgumentMatchers.any());
    }

    @Test
    public void shouldKeepSubscriptionMediaEndpointUponRemovalOfAllButOneSubscriber() {
        final UdpChannel udpChannel = UdpChannel.parse(DriverConductorTest.CHANNEL_4000);
        final long id1 = driverProxy.addSubscription(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        final long id2 = driverProxy.addSubscription(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_2);
        driverProxy.addSubscription(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_3);
        driverConductor.doWork();
        final ReceiveChannelEndpoint channelEndpoint = driverConductor.receiverChannelEndpoint(udpChannel);
        Assert.assertNotNull(channelEndpoint);
        Assert.assertThat(channelEndpoint.streamCount(), Matchers.is(3));
        driverProxy.removeSubscription(id1);
        driverProxy.removeSubscription(id2);
        driverConductor.doWork();
        Assert.assertNotNull(driverConductor.receiverChannelEndpoint(udpChannel));
        Assert.assertThat(channelEndpoint.streamCount(), Matchers.is(1));
    }

    @Test
    public void shouldOnlyRemoveSubscriptionMediaEndpointUponRemovalOfAllSubscribers() {
        final UdpChannel udpChannel = UdpChannel.parse(DriverConductorTest.CHANNEL_4000);
        final long id1 = driverProxy.addSubscription(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        final long id2 = driverProxy.addSubscription(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_2);
        final long id3 = driverProxy.addSubscription(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_3);
        driverConductor.doWork();
        final ReceiveChannelEndpoint channelEndpoint = driverConductor.receiverChannelEndpoint(udpChannel);
        Assert.assertNotNull(channelEndpoint);
        Assert.assertThat(channelEndpoint.streamCount(), Matchers.is(3));
        driverProxy.removeSubscription(id2);
        driverProxy.removeSubscription(id3);
        driverConductor.doWork();
        Assert.assertNotNull(driverConductor.receiverChannelEndpoint(udpChannel));
        Assert.assertThat(channelEndpoint.streamCount(), Matchers.is(1));
        driverProxy.removeSubscription(id1);
        driverConductor.doWork();
        Assert.assertNull(driverConductor.receiverChannelEndpoint(udpChannel));
    }

    @Test
    public void shouldErrorOnRemovePublicationOnUnknownRegistrationId() {
        final long id = driverProxy.addPublication(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        driverProxy.removePublication((id + 1));
        driverConductor.doWork();
        final InOrder inOrder = Mockito.inOrder(senderProxy, mockClientProxy);
        inOrder.verify(senderProxy).newNetworkPublication(ArgumentMatchers.any());
        inOrder.verify(mockClientProxy).onPublicationReady(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(id), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.anyInt(), ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(false));
        inOrder.verify(mockClientProxy).onError(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(UNKNOWN_PUBLICATION), ArgumentMatchers.anyString());
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(mockErrorCounter).increment();
        Mockito.verify(mockErrorHandler).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void shouldAddPublicationWithMtu() {
        final int mtuLength = 4096;
        final String mtuParam = (("|" + (CommonContext.MTU_LENGTH_PARAM_NAME)) + "=") + mtuLength;
        driverProxy.addPublication(((DriverConductorTest.CHANNEL_4000) + mtuParam), DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final ArgumentCaptor<NetworkPublication> argumentCaptor = ArgumentCaptor.forClass(NetworkPublication.class);
        Mockito.verify(senderProxy).newNetworkPublication(argumentCaptor.capture());
        Assert.assertThat(argumentCaptor.getValue().mtuLength(), Matchers.is(mtuLength));
    }

    @Test
    public void shouldErrorOnRemoveSubscriptionOnUnknownRegistrationId() {
        final long id1 = driverProxy.addSubscription(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        driverProxy.removeSubscription((id1 + 100));
        driverConductor.doWork();
        final InOrder inOrder = Mockito.inOrder(receiverProxy, mockClientProxy);
        inOrder.verify(receiverProxy).addSubscription(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        inOrder.verify(mockClientProxy).onSubscriptionReady(ArgumentMatchers.eq(id1), ArgumentMatchers.anyInt());
        inOrder.verify(mockClientProxy).onError(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(UNKNOWN_SUBSCRIPTION), ArgumentMatchers.anyString());
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(mockErrorHandler).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void shouldErrorOnAddSubscriptionWithInvalidChannel() {
        driverProxy.addSubscription(DriverConductorTest.INVALID_URI, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        driverConductor.doWork();
        Mockito.verify(senderProxy, Mockito.never()).newNetworkPublication(ArgumentMatchers.any());
        Mockito.verify(mockClientProxy).onError(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(INVALID_CHANNEL), ArgumentMatchers.anyString());
        Mockito.verify(mockClientProxy, Mockito.never()).operationSucceeded(ArgumentMatchers.anyLong());
        Mockito.verify(mockErrorCounter).increment();
        Mockito.verify(mockErrorHandler).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void shouldTimeoutPublication() {
        driverProxy.addPublication(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final ArgumentCaptor<NetworkPublication> captor = ArgumentCaptor.forClass(NetworkPublication.class);
        Mockito.verify(senderProxy, Mockito.times(1)).newNetworkPublication(captor.capture());
        final NetworkPublication publication = captor.getValue();
        doWorkUntil(() -> (((DriverConductorTest.CLIENT_LIVENESS_TIMEOUT_NS) + ((DriverConductorTest.PUBLICATION_LINGER_TIMEOUT_NS) * 2)) - (nanoClock.nanoTime())) <= 0);
        Mockito.verify(mockClientProxy, Mockito.times(1)).onClientTimeout(driverProxy.clientId());
        Mockito.verify(senderProxy).removeNetworkPublication(ArgumentMatchers.eq(publication));
        Assert.assertNull(driverConductor.senderChannelEndpoint(UdpChannel.parse(DriverConductorTest.CHANNEL_4000)));
    }

    @Test
    public void shouldNotTimeoutPublicationOnKeepAlive() {
        driverProxy.addPublication(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final ArgumentCaptor<NetworkPublication> captor = ArgumentCaptor.forClass(NetworkPublication.class);
        Mockito.verify(senderProxy, Mockito.times(1)).newNetworkPublication(captor.capture());
        final NetworkPublication publication = captor.getValue();
        doWorkUntil(() -> (((DriverConductorTest.CLIENT_LIVENESS_TIMEOUT_NS) / 2) - (nanoClock.nanoTime())) <= 0);
        driverProxy.sendClientKeepalive();
        doWorkUntil(() -> (((DriverConductorTest.CLIENT_LIVENESS_TIMEOUT_NS) + 1000) - (nanoClock.nanoTime())) <= 0);
        driverProxy.sendClientKeepalive();
        doWorkUntil(() -> (nanoClock.nanoTime()) >= ((DriverConductorTest.CLIENT_LIVENESS_TIMEOUT_NS) * 2));
        Mockito.verify(senderProxy, Mockito.never()).removeNetworkPublication(ArgumentMatchers.eq(publication));
    }

    @Test
    public void shouldTimeoutPublicationWithNoKeepaliveButNotDrained() {
        driverProxy.addPublication(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final ArgumentCaptor<NetworkPublication> captor = ArgumentCaptor.forClass(NetworkPublication.class);
        Mockito.verify(senderProxy, Mockito.times(1)).newNetworkPublication(captor.capture());
        final NetworkPublication publication = captor.getValue();
        final int termId = 101;
        final int index = LogBufferDescriptor.indexByTerm(termId, termId);
        final RawLog rawLog = publication.rawLog();
        LogBufferDescriptor.rawTail(rawLog.metaData(), index, LogBufferDescriptor.packTail(termId, 0));
        final TermAppender appender = new TermAppender(rawLog.termBuffers()[index], rawLog.metaData(), index);
        final UnsafeBuffer srcBuffer = new UnsafeBuffer(new byte[256]);
        final HeaderWriter headerWriter = HeaderWriter.newInstance(createDefaultHeader(DriverConductorTest.SESSION_ID, DriverConductorTest.STREAM_ID_1, termId));
        final StatusMessageFlyweight msg = Mockito.mock(StatusMessageFlyweight.class);
        Mockito.when(msg.consumptionTermId()).thenReturn(termId);
        Mockito.when(msg.consumptionTermOffset()).thenReturn(0);
        Mockito.when(msg.receiverWindowLength()).thenReturn(10);
        publication.onStatusMessage(msg, new InetSocketAddress("localhost", 4059));
        appender.appendUnfragmentedMessage(headerWriter, srcBuffer, 0, 256, null, termId);
        Assert.assertThat(publication.state(), Matchers.is(ACTIVE));
        doWorkUntil(() -> (nanoClock.nanoTime()) >= ((DriverConductorTest.CLIENT_LIVENESS_TIMEOUT_NS) * 2), ( timeNs) -> {
            publication.onStatusMessage(msg, new InetSocketAddress("localhost", 4059));
            publication.updateHasReceivers(timeNs);
        });
        Assert.assertThat(publication.state(), Matchers.anyOf(Matchers.is(DRAINING), Matchers.is(LINGER)));
        final long endTime = ((nanoClock.nanoTime()) + (publicationConnectionTimeoutNs())) + (timerIntervalNs());
        doWorkUntil(() -> (nanoClock.nanoTime()) >= endTime, publication::updateHasReceivers);
        Assert.assertThat(publication.state(), Matchers.anyOf(Matchers.is(LINGER), Matchers.is(CLOSING)));
        currentTimeNs += (timerIntervalNs()) + (DriverConductorTest.PUBLICATION_LINGER_TIMEOUT_NS);
        driverConductor.doWork();
        Assert.assertThat(publication.state(), Matchers.is(CLOSING));
        Mockito.verify(senderProxy).removeNetworkPublication(ArgumentMatchers.eq(publication));
        Assert.assertNull(driverConductor.senderChannelEndpoint(UdpChannel.parse(DriverConductorTest.CHANNEL_4000)));
    }

    @Test
    public void shouldTimeoutSubscription() {
        driverProxy.addSubscription(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final ReceiveChannelEndpoint receiveChannelEndpoint = driverConductor.receiverChannelEndpoint(UdpChannel.parse(DriverConductorTest.CHANNEL_4000));
        Assert.assertNotNull(receiveChannelEndpoint);
        Mockito.verify(receiverProxy).addSubscription(ArgumentMatchers.eq(receiveChannelEndpoint), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1));
        doWorkUntil(() -> (nanoClock.nanoTime()) >= ((DriverConductorTest.CLIENT_LIVENESS_TIMEOUT_NS) * 2));
        Mockito.verify(mockClientProxy, Mockito.times(1)).onClientTimeout(driverProxy.clientId());
        Mockito.verify(receiverProxy, Mockito.times(1)).removeSubscription(ArgumentMatchers.eq(receiveChannelEndpoint), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1));
        Assert.assertNull(driverConductor.receiverChannelEndpoint(UdpChannel.parse(DriverConductorTest.CHANNEL_4000)));
    }

    @Test
    public void shouldNotTimeoutSubscriptionOnKeepAlive() {
        driverProxy.addSubscription(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final ReceiveChannelEndpoint receiveChannelEndpoint = driverConductor.receiverChannelEndpoint(UdpChannel.parse(DriverConductorTest.CHANNEL_4000));
        Assert.assertNotNull(receiveChannelEndpoint);
        Mockito.verify(receiverProxy).addSubscription(ArgumentMatchers.eq(receiveChannelEndpoint), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1));
        doWorkUntil(() -> (nanoClock.nanoTime()) >= (DriverConductorTest.CLIENT_LIVENESS_TIMEOUT_NS));
        driverProxy.sendClientKeepalive();
        doWorkUntil(() -> (nanoClock.nanoTime()) >= ((DriverConductorTest.CLIENT_LIVENESS_TIMEOUT_NS) + 1000));
        driverProxy.sendClientKeepalive();
        doWorkUntil(() -> (nanoClock.nanoTime()) >= ((DriverConductorTest.CLIENT_LIVENESS_TIMEOUT_NS) * 2));
        Mockito.verify(receiverProxy, Mockito.never()).removeSubscription(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        Assert.assertNotNull(driverConductor.receiverChannelEndpoint(UdpChannel.parse(DriverConductorTest.CHANNEL_4000)));
    }

    @Test
    public void shouldCreateImageOnSubscription() {
        final InetSocketAddress sourceAddress = new InetSocketAddress("localhost", 4400);
        final int initialTermId = 1;
        final int activeTermId = 2;
        final int termOffset = 100;
        driverProxy.addSubscription(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final ReceiveChannelEndpoint receiveChannelEndpoint = driverConductor.receiverChannelEndpoint(UdpChannel.parse(DriverConductorTest.CHANNEL_4000));
        Assert.assertNotNull(receiveChannelEndpoint);
        receiveChannelEndpoint.openChannel(driverConductorProxy);
        driverConductor.onCreatePublicationImage(DriverConductorTest.SESSION_ID, DriverConductorTest.STREAM_ID_1, initialTermId, activeTermId, termOffset, DriverConductorTest.TERM_BUFFER_LENGTH, DriverConductorTest.MTU_LENGTH, 0, Mockito.mock(InetSocketAddress.class), sourceAddress, receiveChannelEndpoint);
        final ArgumentCaptor<PublicationImage> captor = ArgumentCaptor.forClass(PublicationImage.class);
        Mockito.verify(receiverProxy).newPublicationImage(ArgumentMatchers.eq(receiveChannelEndpoint), captor.capture());
        final PublicationImage publicationImage = captor.getValue();
        Assert.assertThat(publicationImage.sessionId(), Matchers.is(DriverConductorTest.SESSION_ID));
        Assert.assertThat(publicationImage.streamId(), Matchers.is(DriverConductorTest.STREAM_ID_1));
        Mockito.verify(mockClientProxy).onAvailableImage(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.eq(DriverConductorTest.SESSION_ID), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    public void shouldNotCreateImageOnUnknownSubscription() {
        final InetSocketAddress sourceAddress = new InetSocketAddress("localhost", 4400);
        driverProxy.addSubscription(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final ReceiveChannelEndpoint receiveChannelEndpoint = driverConductor.receiverChannelEndpoint(UdpChannel.parse(DriverConductorTest.CHANNEL_4000));
        Assert.assertNotNull(receiveChannelEndpoint);
        receiveChannelEndpoint.openChannel(driverConductorProxy);
        driverConductor.onCreatePublicationImage(DriverConductorTest.SESSION_ID, DriverConductorTest.STREAM_ID_2, 1, 1, 0, DriverConductorTest.TERM_BUFFER_LENGTH, DriverConductorTest.MTU_LENGTH, 0, Mockito.mock(InetSocketAddress.class), sourceAddress, receiveChannelEndpoint);
        Mockito.verify(receiverProxy, Mockito.never()).newPublicationImage(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(mockClientProxy, Mockito.never()).onAvailableImage(ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    public void shouldSignalInactiveImageWhenImageTimesOut() {
        final InetSocketAddress sourceAddress = new InetSocketAddress("localhost", 4400);
        final long subId = driverProxy.addSubscription(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final ReceiveChannelEndpoint receiveChannelEndpoint = driverConductor.receiverChannelEndpoint(UdpChannel.parse(DriverConductorTest.CHANNEL_4000));
        Assert.assertNotNull(receiveChannelEndpoint);
        receiveChannelEndpoint.openChannel(driverConductorProxy);
        driverConductor.onCreatePublicationImage(DriverConductorTest.SESSION_ID, DriverConductorTest.STREAM_ID_1, 1, 1, 0, DriverConductorTest.TERM_BUFFER_LENGTH, DriverConductorTest.MTU_LENGTH, 0, Mockito.mock(InetSocketAddress.class), sourceAddress, receiveChannelEndpoint);
        final ArgumentCaptor<PublicationImage> captor = ArgumentCaptor.forClass(PublicationImage.class);
        Mockito.verify(receiverProxy).newPublicationImage(ArgumentMatchers.eq(receiveChannelEndpoint), captor.capture());
        final PublicationImage publicationImage = captor.getValue();
        publicationImage.activate();
        publicationImage.ifActiveGoInactive();
        doWorkUntil(() -> (nanoClock.nanoTime()) >= ((imageLivenessTimeoutNs()) + 1000));
        Mockito.verify(mockClientProxy).onUnavailableImage(ArgumentMatchers.eq(publicationImage.correlationId()), ArgumentMatchers.eq(subId), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.anyString());
    }

    @Test
    public void shouldAlwaysGiveNetworkPublicationCorrelationIdToClientCallbacks() {
        final InetSocketAddress sourceAddress = new InetSocketAddress("localhost", 4400);
        final long subId1 = driverProxy.addSubscription(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final ReceiveChannelEndpoint receiveChannelEndpoint = driverConductor.receiverChannelEndpoint(UdpChannel.parse(DriverConductorTest.CHANNEL_4000));
        Assert.assertNotNull(receiveChannelEndpoint);
        receiveChannelEndpoint.openChannel(driverConductorProxy);
        driverConductor.onCreatePublicationImage(DriverConductorTest.SESSION_ID, DriverConductorTest.STREAM_ID_1, 1, 1, 0, DriverConductorTest.TERM_BUFFER_LENGTH, DriverConductorTest.MTU_LENGTH, 0, Mockito.mock(InetSocketAddress.class), sourceAddress, receiveChannelEndpoint);
        final ArgumentCaptor<PublicationImage> captor = ArgumentCaptor.forClass(PublicationImage.class);
        Mockito.verify(receiverProxy).newPublicationImage(ArgumentMatchers.eq(receiveChannelEndpoint), captor.capture());
        final PublicationImage publicationImage = captor.getValue();
        publicationImage.activate();
        final long subId2 = driverProxy.addSubscription(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        publicationImage.ifActiveGoInactive();
        doWorkUntil(() -> (nanoClock.nanoTime()) >= ((imageLivenessTimeoutNs()) + 1000));
        final InOrder inOrder = Mockito.inOrder(mockClientProxy);
        inOrder.verify(mockClientProxy, Mockito.times(2)).onAvailableImage(ArgumentMatchers.eq(publicationImage.correlationId()), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.eq(DriverConductorTest.SESSION_ID), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        inOrder.verify(mockClientProxy, Mockito.times(1)).onUnavailableImage(ArgumentMatchers.eq(publicationImage.correlationId()), ArgumentMatchers.eq(subId1), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.anyString());
        inOrder.verify(mockClientProxy, Mockito.times(1)).onUnavailableImage(ArgumentMatchers.eq(publicationImage.correlationId()), ArgumentMatchers.eq(subId2), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.anyString());
    }

    @Test
    public void shouldNotSendAvailableImageWhileImageNotActiveOnAddSubscription() {
        final InetSocketAddress sourceAddress = new InetSocketAddress("localhost", 4400);
        final long subOneId = driverProxy.addSubscription(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final ReceiveChannelEndpoint receiveChannelEndpoint = driverConductor.receiverChannelEndpoint(UdpChannel.parse(DriverConductorTest.CHANNEL_4000));
        Assert.assertNotNull(receiveChannelEndpoint);
        receiveChannelEndpoint.openChannel(driverConductorProxy);
        driverConductor.onCreatePublicationImage(DriverConductorTest.SESSION_ID, DriverConductorTest.STREAM_ID_1, 1, 1, 0, DriverConductorTest.TERM_BUFFER_LENGTH, DriverConductorTest.MTU_LENGTH, 0, Mockito.mock(InetSocketAddress.class), sourceAddress, receiveChannelEndpoint);
        final ArgumentCaptor<PublicationImage> captor = ArgumentCaptor.forClass(PublicationImage.class);
        Mockito.verify(receiverProxy).newPublicationImage(ArgumentMatchers.eq(receiveChannelEndpoint), captor.capture());
        final PublicationImage publicationImage = captor.getValue();
        publicationImage.activate();
        publicationImage.ifActiveGoInactive();
        doWorkUntil(() -> (nanoClock.nanoTime()) >= ((imageLivenessTimeoutNs()) / 2));
        driverProxy.sendClientKeepalive();
        doWorkUntil(() -> (nanoClock.nanoTime()) >= ((imageLivenessTimeoutNs()) + 1000));
        final long subTwoId = driverProxy.addSubscription(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final InOrder inOrder = Mockito.inOrder(mockClientProxy);
        inOrder.verify(mockClientProxy, Mockito.times(1)).onSubscriptionReady(ArgumentMatchers.eq(subOneId), ArgumentMatchers.anyInt());
        inOrder.verify(mockClientProxy, Mockito.times(1)).onAvailableImage(ArgumentMatchers.eq(publicationImage.correlationId()), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.eq(DriverConductorTest.SESSION_ID), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        inOrder.verify(mockClientProxy, Mockito.times(1)).onUnavailableImage(ArgumentMatchers.eq(publicationImage.correlationId()), ArgumentMatchers.eq(subOneId), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.anyString());
        inOrder.verify(mockClientProxy, Mockito.times(1)).onSubscriptionReady(ArgumentMatchers.eq(subTwoId), ArgumentMatchers.anyInt());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void shouldBeAbleToAddSingleIpcPublication() {
        final long id = driverProxy.addPublication(DriverConductorTest.CHANNEL_IPC, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        Assert.assertNotNull(driverConductor.getSharedIpcPublication(DriverConductorTest.STREAM_ID_1));
        Mockito.verify(mockClientProxy).onPublicationReady(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(id), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.anyInt(), ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(false));
    }

    @Test
    public void shouldBeAbleToAddIpcPublicationThenSubscription() {
        final long idPub = driverProxy.addPublication(DriverConductorTest.CHANNEL_IPC, DriverConductorTest.STREAM_ID_1);
        final long idSub = driverProxy.addSubscription(DriverConductorTest.CHANNEL_IPC, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final IpcPublication ipcPublication = driverConductor.getSharedIpcPublication(DriverConductorTest.STREAM_ID_1);
        Assert.assertNotNull(ipcPublication);
        final InOrder inOrder = Mockito.inOrder(mockClientProxy);
        inOrder.verify(mockClientProxy).onPublicationReady(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(idPub), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.anyInt(), ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(false));
        inOrder.verify(mockClientProxy).onSubscriptionReady(ArgumentMatchers.eq(idSub), ArgumentMatchers.anyInt());
        inOrder.verify(mockClientProxy).onAvailableImage(ArgumentMatchers.eq(ipcPublication.registrationId()), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.eq(ipcPublication.sessionId()), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(ipcPublication.rawLog().fileName()), ArgumentMatchers.anyString());
    }

    @Test
    public void shouldBeAbleToAddThenRemoveTheAddIpcPublicationWithExistingSubscription() {
        final long idSub = driverProxy.addSubscription(DriverConductorTest.CHANNEL_IPC, DriverConductorTest.STREAM_ID_1);
        final long idPubOne = driverProxy.addPublication(DriverConductorTest.CHANNEL_IPC, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final IpcPublication ipcPublicationOne = driverConductor.getSharedIpcPublication(DriverConductorTest.STREAM_ID_1);
        Assert.assertNotNull(ipcPublicationOne);
        final long idPubOneRemove = driverProxy.removePublication(idPubOne);
        driverConductor.doWork();
        final long idPubTwo = driverProxy.addPublication(DriverConductorTest.CHANNEL_IPC, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final IpcPublication ipcPublicationTwo = driverConductor.getSharedIpcPublication(DriverConductorTest.STREAM_ID_1);
        Assert.assertNotNull(ipcPublicationTwo);
        final InOrder inOrder = Mockito.inOrder(mockClientProxy);
        inOrder.verify(mockClientProxy).onSubscriptionReady(ArgumentMatchers.eq(idSub), ArgumentMatchers.anyInt());
        inOrder.verify(mockClientProxy).onPublicationReady(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(idPubOne), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.anyInt(), ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(false));
        inOrder.verify(mockClientProxy).onAvailableImage(ArgumentMatchers.eq(ipcPublicationOne.registrationId()), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.eq(ipcPublicationOne.sessionId()), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(ipcPublicationOne.rawLog().fileName()), ArgumentMatchers.anyString());
        inOrder.verify(mockClientProxy).operationSucceeded(ArgumentMatchers.eq(idPubOneRemove));
        inOrder.verify(mockClientProxy).onPublicationReady(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(idPubTwo), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.anyInt(), ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(false));
        inOrder.verify(mockClientProxy).onAvailableImage(ArgumentMatchers.eq(ipcPublicationTwo.registrationId()), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.eq(ipcPublicationTwo.sessionId()), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(ipcPublicationTwo.rawLog().fileName()), ArgumentMatchers.anyString());
    }

    @Test
    public void shouldBeAbleToAddSubscriptionThenIpcPublication() {
        final long idSub = driverProxy.addSubscription(DriverConductorTest.CHANNEL_IPC, DriverConductorTest.STREAM_ID_1);
        final long idPub = driverProxy.addPublication(DriverConductorTest.CHANNEL_IPC, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final IpcPublication ipcPublication = driverConductor.getSharedIpcPublication(DriverConductorTest.STREAM_ID_1);
        Assert.assertNotNull(ipcPublication);
        final InOrder inOrder = Mockito.inOrder(mockClientProxy);
        inOrder.verify(mockClientProxy).onSubscriptionReady(ArgumentMatchers.eq(idSub), ArgumentMatchers.anyInt());
        inOrder.verify(mockClientProxy).onPublicationReady(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(idPub), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.anyInt(), ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(false));
        inOrder.verify(mockClientProxy).onAvailableImage(ArgumentMatchers.eq(ipcPublication.registrationId()), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.eq(ipcPublication.sessionId()), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(ipcPublication.rawLog().fileName()), ArgumentMatchers.anyString());
    }

    @Test
    public void shouldBeAbleToAddAndRemoveIpcPublication() {
        final long idAdd = driverProxy.addPublication(DriverConductorTest.CHANNEL_IPC, DriverConductorTest.STREAM_ID_1);
        driverProxy.removePublication(idAdd);
        doWorkUntil(() -> (nanoClock.nanoTime()) >= (DriverConductorTest.CLIENT_LIVENESS_TIMEOUT_NS));
        final IpcPublication ipcPublication = driverConductor.getSharedIpcPublication(DriverConductorTest.STREAM_ID_1);
        Assert.assertNull(ipcPublication);
    }

    @Test
    public void shouldBeAbleToAddAndRemoveSubscriptionToIpcPublication() {
        final long idAdd = driverProxy.addSubscription(DriverConductorTest.CHANNEL_IPC, DriverConductorTest.STREAM_ID_1);
        driverProxy.removeSubscription(idAdd);
        doWorkUntil(() -> (nanoClock.nanoTime()) >= (DriverConductorTest.CLIENT_LIVENESS_TIMEOUT_NS));
        final IpcPublication ipcPublication = driverConductor.getSharedIpcPublication(DriverConductorTest.STREAM_ID_1);
        Assert.assertNull(ipcPublication);
    }

    @Test
    public void shouldBeAbleToAddAndRemoveTwoIpcPublications() {
        final long idAdd1 = driverProxy.addPublication(DriverConductorTest.CHANNEL_IPC, DriverConductorTest.STREAM_ID_1);
        final long idAdd2 = driverProxy.addPublication(DriverConductorTest.CHANNEL_IPC, DriverConductorTest.STREAM_ID_1);
        driverProxy.removePublication(idAdd1);
        driverConductor.doWork();
        IpcPublication ipcPublication = driverConductor.getSharedIpcPublication(DriverConductorTest.STREAM_ID_1);
        Assert.assertNotNull(ipcPublication);
        driverProxy.removePublication(idAdd2);
        doWorkUntil(() -> ((DriverConductorTest.CLIENT_LIVENESS_TIMEOUT_NS) - (nanoClock.nanoTime())) <= 0);
        ipcPublication = driverConductor.getSharedIpcPublication(DriverConductorTest.STREAM_ID_1);
        Assert.assertNull(ipcPublication);
    }

    @Test
    public void shouldBeAbleToAddAndRemoveIpcPublicationAndSubscription() {
        final long idAdd1 = driverProxy.addSubscription(DriverConductorTest.CHANNEL_IPC, DriverConductorTest.STREAM_ID_1);
        final long idAdd2 = driverProxy.addPublication(DriverConductorTest.CHANNEL_IPC, DriverConductorTest.STREAM_ID_1);
        driverProxy.removeSubscription(idAdd1);
        driverConductor.doWork();
        IpcPublication ipcPublication = driverConductor.getSharedIpcPublication(DriverConductorTest.STREAM_ID_1);
        Assert.assertNotNull(ipcPublication);
        driverProxy.removePublication(idAdd2);
        doWorkUntil(() -> ((DriverConductorTest.CLIENT_LIVENESS_TIMEOUT_NS) - (nanoClock.nanoTime())) <= 0);
        ipcPublication = driverConductor.getSharedIpcPublication(DriverConductorTest.STREAM_ID_1);
        Assert.assertNull(ipcPublication);
    }

    @Test
    public void shouldTimeoutIpcPublication() {
        driverProxy.addPublication(DriverConductorTest.CHANNEL_IPC, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        IpcPublication ipcPublication = driverConductor.getSharedIpcPublication(DriverConductorTest.STREAM_ID_1);
        Assert.assertNotNull(ipcPublication);
        doWorkUntil(() -> (((DriverConductorTest.CLIENT_LIVENESS_TIMEOUT_NS) * 2) - (nanoClock.nanoTime())) <= 0);
        ipcPublication = driverConductor.getSharedIpcPublication(DriverConductorTest.STREAM_ID_1);
        Assert.assertNull(ipcPublication);
    }

    @Test
    public void shouldNotTimeoutIpcPublicationWithKeepalive() {
        driverProxy.addPublication(DriverConductorTest.CHANNEL_IPC, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        IpcPublication ipcPublication = driverConductor.getSharedIpcPublication(DriverConductorTest.STREAM_ID_1);
        Assert.assertNotNull(ipcPublication);
        doWorkUntil(() -> ((DriverConductorTest.CLIENT_LIVENESS_TIMEOUT_NS) - (nanoClock.nanoTime())) <= 0);
        driverProxy.sendClientKeepalive();
        doWorkUntil(() -> ((DriverConductorTest.CLIENT_LIVENESS_TIMEOUT_NS) - (nanoClock.nanoTime())) <= 0);
        ipcPublication = driverConductor.getSharedIpcPublication(DriverConductorTest.STREAM_ID_1);
        Assert.assertNotNull(ipcPublication);
    }

    @Test
    public void shouldBeAbleToAddSingleSpy() {
        final long id = driverProxy.addSubscription(DriverConductorTest.spyForChannel(DriverConductorTest.CHANNEL_4000), DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        Mockito.verify(receiverProxy, Mockito.never()).registerReceiveChannelEndpoint(ArgumentMatchers.any());
        Mockito.verify(receiverProxy, Mockito.never()).addSubscription(ArgumentMatchers.any(), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1));
        Mockito.verify(mockClientProxy).onSubscriptionReady(ArgumentMatchers.eq(id), ArgumentMatchers.anyInt());
        Assert.assertNull(driverConductor.receiverChannelEndpoint(UdpChannel.parse(DriverConductorTest.CHANNEL_4000)));
    }

    @Test
    public void shouldBeAbleToAddNetworkPublicationThenSingleSpy() {
        driverProxy.addPublication(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        final long idSpy = driverProxy.addSubscription(DriverConductorTest.spyForChannel(DriverConductorTest.CHANNEL_4000), DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final ArgumentCaptor<NetworkPublication> captor = ArgumentCaptor.forClass(NetworkPublication.class);
        Mockito.verify(senderProxy, Mockito.times(1)).newNetworkPublication(captor.capture());
        final NetworkPublication publication = captor.getValue();
        Assert.assertTrue(publication.hasSpies());
        final InOrder inOrder = Mockito.inOrder(mockClientProxy);
        inOrder.verify(mockClientProxy).onSubscriptionReady(ArgumentMatchers.eq(idSpy), ArgumentMatchers.anyInt());
        inOrder.verify(mockClientProxy).onAvailableImage(ArgumentMatchers.eq(DriverConductorTest.networkPublicationCorrelationId(publication)), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.eq(publication.sessionId()), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(publication.rawLog().fileName()), ArgumentMatchers.anyString());
    }

    @Test
    public void shouldBeAbleToAddSingleSpyThenNetworkPublication() {
        final long idSpy = driverProxy.addSubscription(DriverConductorTest.spyForChannel(DriverConductorTest.CHANNEL_4000), DriverConductorTest.STREAM_ID_1);
        driverProxy.addPublication(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final ArgumentCaptor<NetworkPublication> captor = ArgumentCaptor.forClass(NetworkPublication.class);
        Mockito.verify(senderProxy, Mockito.times(1)).newNetworkPublication(captor.capture());
        final NetworkPublication publication = captor.getValue();
        Assert.assertTrue(publication.hasSpies());
        final InOrder inOrder = Mockito.inOrder(mockClientProxy);
        inOrder.verify(mockClientProxy).onSubscriptionReady(ArgumentMatchers.eq(idSpy), ArgumentMatchers.anyInt());
        inOrder.verify(mockClientProxy).onAvailableImage(ArgumentMatchers.eq(DriverConductorTest.networkPublicationCorrelationId(publication)), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.eq(publication.sessionId()), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(publication.rawLog().fileName()), ArgumentMatchers.anyString());
    }

    @Test
    public void shouldBeAbleToAddNetworkPublicationThenSingleSpyThenRemoveSpy() {
        driverProxy.addPublication(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        final long idSpy = driverProxy.addSubscription(DriverConductorTest.spyForChannel(DriverConductorTest.CHANNEL_4000), DriverConductorTest.STREAM_ID_1);
        driverProxy.removeSubscription(idSpy);
        driverConductor.doWork();
        final ArgumentCaptor<NetworkPublication> captor = ArgumentCaptor.forClass(NetworkPublication.class);
        Mockito.verify(senderProxy, Mockito.times(1)).newNetworkPublication(captor.capture());
        final NetworkPublication publication = captor.getValue();
        Assert.assertFalse(publication.hasSpies());
    }

    @Test
    public void shouldTimeoutSpy() {
        driverProxy.addPublication(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        driverProxy.addSubscription(DriverConductorTest.spyForChannel(DriverConductorTest.CHANNEL_4000), DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final ArgumentCaptor<NetworkPublication> captor = ArgumentCaptor.forClass(NetworkPublication.class);
        Mockito.verify(senderProxy, Mockito.times(1)).newNetworkPublication(captor.capture());
        final NetworkPublication publication = captor.getValue();
        Assert.assertTrue(publication.hasSpies());
        doWorkUntil(() -> (((DriverConductorTest.CLIENT_LIVENESS_TIMEOUT_NS) * 2) - (nanoClock.nanoTime())) <= 0);
        Assert.assertFalse(publication.hasSpies());
    }

    @Test
    public void shouldNotTimeoutSpyWithKeepalive() {
        driverProxy.addPublication(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        driverProxy.addSubscription(DriverConductorTest.spyForChannel(DriverConductorTest.CHANNEL_4000), DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final ArgumentCaptor<NetworkPublication> captor = ArgumentCaptor.forClass(NetworkPublication.class);
        Mockito.verify(senderProxy, Mockito.times(1)).newNetworkPublication(captor.capture());
        final NetworkPublication publication = captor.getValue();
        Assert.assertTrue(publication.hasSpies());
        doWorkUntil(() -> ((DriverConductorTest.CLIENT_LIVENESS_TIMEOUT_NS) - (nanoClock.nanoTime())) <= 0);
        driverProxy.sendClientKeepalive();
        doWorkUntil(() -> ((DriverConductorTest.CLIENT_LIVENESS_TIMEOUT_NS) - (nanoClock.nanoTime())) <= 0);
        Assert.assertTrue(publication.hasSpies());
    }

    @Test
    public void shouldTimeoutNetworkPublicationWithSpy() {
        final long clientId = toDriverCommands.nextCorrelationId();
        final DriverProxy spyDriverProxy = new DriverProxy(toDriverCommands, clientId);
        driverProxy.addPublication(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        final long subId = spyDriverProxy.addSubscription(DriverConductorTest.spyForChannel(DriverConductorTest.CHANNEL_4000), DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final ArgumentCaptor<NetworkPublication> captor = ArgumentCaptor.forClass(NetworkPublication.class);
        Mockito.verify(senderProxy, Mockito.times(1)).newNetworkPublication(captor.capture());
        final NetworkPublication publication = captor.getValue();
        doWorkUntil(() -> (((DriverConductorTest.CLIENT_LIVENESS_TIMEOUT_NS) / 2) - (nanoClock.nanoTime())) <= 0);
        spyDriverProxy.sendClientKeepalive();
        doWorkUntil(() -> (((DriverConductorTest.CLIENT_LIVENESS_TIMEOUT_NS) + 1000) - (nanoClock.nanoTime())) <= 0);
        spyDriverProxy.sendClientKeepalive();
        doWorkUntil(() -> (((DriverConductorTest.CLIENT_LIVENESS_TIMEOUT_NS) * 2) - (nanoClock.nanoTime())) <= 0);
        Mockito.verify(mockClientProxy).onUnavailableImage(ArgumentMatchers.eq(DriverConductorTest.networkPublicationCorrelationId(publication)), ArgumentMatchers.eq(subId), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.anyString());
    }

    @Test
    public void shouldOnlyCloseSendChannelEndpointOnceWithMultiplePublications() {
        final long id1 = driverProxy.addPublication(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        final long id2 = driverProxy.addPublication(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_2);
        driverProxy.removePublication(id1);
        driverProxy.removePublication(id2);
        doWorkUntil(() -> {
            driverProxy.sendClientKeepalive();
            return (((DriverConductorTest.PUBLICATION_LINGER_TIMEOUT_NS) * 2) - (nanoClock.nanoTime())) <= 0;
        });
        Mockito.verify(senderProxy, Mockito.times(1)).closeSendChannelEndpoint(ArgumentMatchers.any());
    }

    @Test
    public void shouldOnlyCloseReceiveChannelEndpointOnceWithMultipleSubscriptions() {
        final long id1 = driverProxy.addSubscription(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        final long id2 = driverProxy.addSubscription(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_2);
        driverProxy.removeSubscription(id1);
        driverProxy.removeSubscription(id2);
        doWorkUntil(() -> {
            driverProxy.sendClientKeepalive();
            return (((DriverConductorTest.CLIENT_LIVENESS_TIMEOUT_NS) * 2) - (nanoClock.nanoTime())) <= 0;
        });
        Mockito.verify(receiverProxy, Mockito.times(1)).closeReceiveChannelEndpoint(ArgumentMatchers.any());
    }

    @Test
    public void shouldErrorWhenConflictingUnreliableSubscriptionAdded() {
        driverProxy.addSubscription(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final long id2 = driverProxy.addSubscription(((DriverConductorTest.CHANNEL_4000) + "|reliable=false"), DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        Mockito.verify(mockClientProxy).onError(ArgumentMatchers.eq(id2), ArgumentMatchers.any(ErrorCode.class), ArgumentMatchers.anyString());
    }

    @Test
    public void shouldErrorWhenConflictingDefaultReliableSubscriptionAdded() {
        driverProxy.addSubscription(((DriverConductorTest.CHANNEL_4000) + "|reliable=false"), DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final long id2 = driverProxy.addSubscription(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        Mockito.verify(mockClientProxy).onError(ArgumentMatchers.eq(id2), ArgumentMatchers.any(ErrorCode.class), ArgumentMatchers.anyString());
    }

    @Test
    public void shouldErrorWhenConflictingReliableSubscriptionAdded() {
        driverProxy.addSubscription(((DriverConductorTest.CHANNEL_4000) + "|reliable=false"), DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final long id2 = driverProxy.addSubscription(((DriverConductorTest.CHANNEL_4000) + "|reliable=true"), DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        Mockito.verify(mockClientProxy).onError(ArgumentMatchers.eq(id2), ArgumentMatchers.any(ErrorCode.class), ArgumentMatchers.anyString());
    }

    @Test
    public void shouldAddSingleCounter() {
        final long registrationId = driverProxy.addCounter(DriverConductorTest.COUNTER_TYPE_ID, counterKeyAndLabel, DriverConductorTest.COUNTER_KEY_OFFSET, DriverConductorTest.COUNTER_KEY_LENGTH, counterKeyAndLabel, DriverConductorTest.COUNTER_LABEL_OFFSET, DriverConductorTest.COUNTER_LABEL_LENGTH);
        driverConductor.doWork();
        Mockito.verify(mockClientProxy).onCounterReady(ArgumentMatchers.eq(registrationId), ArgumentMatchers.anyInt());
        Mockito.verify(spyCountersManager).newCounter(ArgumentMatchers.eq(DriverConductorTest.COUNTER_TYPE_ID), ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(DriverConductorTest.COUNTER_KEY_LENGTH), ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(DriverConductorTest.COUNTER_LABEL_LENGTH));
    }

    @Test
    public void shouldRemoveSingleCounter() {
        final long registrationId = driverProxy.addCounter(DriverConductorTest.COUNTER_TYPE_ID, counterKeyAndLabel, DriverConductorTest.COUNTER_KEY_OFFSET, DriverConductorTest.COUNTER_KEY_LENGTH, counterKeyAndLabel, DriverConductorTest.COUNTER_LABEL_OFFSET, DriverConductorTest.COUNTER_LABEL_LENGTH);
        driverConductor.doWork();
        final long removeCorrelationId = driverProxy.removeCounter(registrationId);
        driverConductor.doWork();
        final ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        final InOrder inOrder = Mockito.inOrder(mockClientProxy);
        inOrder.verify(mockClientProxy).onCounterReady(ArgumentMatchers.eq(registrationId), captor.capture());
        inOrder.verify(mockClientProxy).operationSucceeded(removeCorrelationId);
        Mockito.verify(spyCountersManager).free(captor.getValue());
    }

    @Test
    public void shouldRemoveCounterOnClientTimeout() {
        final long registrationId = driverProxy.addCounter(DriverConductorTest.COUNTER_TYPE_ID, counterKeyAndLabel, DriverConductorTest.COUNTER_KEY_OFFSET, DriverConductorTest.COUNTER_KEY_LENGTH, counterKeyAndLabel, DriverConductorTest.COUNTER_LABEL_OFFSET, DriverConductorTest.COUNTER_LABEL_LENGTH);
        driverConductor.doWork();
        final ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(mockClientProxy).onCounterReady(ArgumentMatchers.eq(registrationId), captor.capture());
        doWorkUntil(() -> (((DriverConductorTest.CLIENT_LIVENESS_TIMEOUT_NS) * 2) - (nanoClock.nanoTime())) <= 0);
        Mockito.verify(spyCountersManager).free(captor.getValue());
    }

    @Test
    public void shouldNotRemoveCounterOnClientKeepalive() {
        final long registrationId = driverProxy.addCounter(DriverConductorTest.COUNTER_TYPE_ID, counterKeyAndLabel, DriverConductorTest.COUNTER_KEY_OFFSET, DriverConductorTest.COUNTER_KEY_LENGTH, counterKeyAndLabel, DriverConductorTest.COUNTER_LABEL_OFFSET, DriverConductorTest.COUNTER_LABEL_LENGTH);
        driverConductor.doWork();
        final ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(mockClientProxy).onCounterReady(ArgumentMatchers.eq(registrationId), captor.capture());
        doWorkUntil(() -> {
            driverProxy.sendClientKeepalive();
            return (((DriverConductorTest.CLIENT_LIVENESS_TIMEOUT_NS) * 2) - (nanoClock.nanoTime())) <= 0;
        });
        Mockito.verify(spyCountersManager, Mockito.never()).free(captor.getValue());
    }

    @Test
    public void shouldInformClientsOfRemovedCounter() {
        final long registrationId = driverProxy.addCounter(DriverConductorTest.COUNTER_TYPE_ID, counterKeyAndLabel, DriverConductorTest.COUNTER_KEY_OFFSET, DriverConductorTest.COUNTER_KEY_LENGTH, counterKeyAndLabel, DriverConductorTest.COUNTER_LABEL_OFFSET, DriverConductorTest.COUNTER_LABEL_LENGTH);
        driverConductor.doWork();
        final long removeCorrelationId = driverProxy.removeCounter(registrationId);
        driverConductor.doWork();
        final ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        final InOrder inOrder = Mockito.inOrder(mockClientProxy);
        inOrder.verify(mockClientProxy).onCounterReady(ArgumentMatchers.eq(registrationId), captor.capture());
        inOrder.verify(mockClientProxy).operationSucceeded(removeCorrelationId);
        inOrder.verify(mockClientProxy).onUnavailableCounter(ArgumentMatchers.eq(registrationId), captor.capture());
        Mockito.verify(spyCountersManager).free(captor.getValue());
    }

    @Test
    public void shouldAddPublicationWithSessionId() {
        final int sessionId = 4096;
        final String sessionIdParam = (("|" + (CommonContext.SESSION_ID_PARAM_NAME)) + "=") + sessionId;
        driverProxy.addPublication(((DriverConductorTest.CHANNEL_4000) + sessionIdParam), DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final ArgumentCaptor<NetworkPublication> argumentCaptor = ArgumentCaptor.forClass(NetworkPublication.class);
        Mockito.verify(senderProxy).newNetworkPublication(argumentCaptor.capture());
        Assert.assertThat(argumentCaptor.getValue().sessionId(), Matchers.is(sessionId));
    }

    @Test
    public void shouldAddExclusivePublicationWithSessionId() {
        final int sessionId = 4096;
        final String sessionIdParam = (("|" + (CommonContext.SESSION_ID_PARAM_NAME)) + "=") + sessionId;
        driverProxy.addExclusivePublication(((DriverConductorTest.CHANNEL_4000) + sessionIdParam), DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final ArgumentCaptor<NetworkPublication> argumentCaptor = ArgumentCaptor.forClass(NetworkPublication.class);
        Mockito.verify(senderProxy).newNetworkPublication(argumentCaptor.capture());
        Assert.assertThat(argumentCaptor.getValue().sessionId(), Matchers.is(sessionId));
    }

    @Test
    public void shouldAddPublicationWithSameSessionId() {
        driverProxy.addPublication(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final ArgumentCaptor<NetworkPublication> argumentCaptor = ArgumentCaptor.forClass(NetworkPublication.class);
        Mockito.verify(senderProxy).newNetworkPublication(argumentCaptor.capture());
        final int sessionId = argumentCaptor.getValue().sessionId();
        final String sessionIdParam = (("|" + (CommonContext.SESSION_ID_PARAM_NAME)) + "=") + sessionId;
        driverProxy.addPublication(((DriverConductorTest.CHANNEL_4000) + sessionIdParam), DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        Mockito.verify(mockClientProxy, Mockito.times(2)).onPublicationReady(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.eq(sessionId), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(false));
    }

    @Test
    public void shouldAddExclusivePublicationWithSameSessionId() {
        driverProxy.addPublication(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final ArgumentCaptor<NetworkPublication> argumentCaptor = ArgumentCaptor.forClass(NetworkPublication.class);
        Mockito.verify(senderProxy).newNetworkPublication(argumentCaptor.capture());
        final int sessionId = argumentCaptor.getValue().sessionId();
        final String sessionIdParam = (("|" + (CommonContext.SESSION_ID_PARAM_NAME)) + "=") + (sessionId + 1);
        driverProxy.addExclusivePublication(((DriverConductorTest.CHANNEL_4000) + sessionIdParam), DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        Mockito.verify(mockClientProxy).onPublicationReady(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.eq(sessionId), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(false));
        Mockito.verify(mockClientProxy).onPublicationReady(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.eq((sessionId + 1)), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(true));
    }

    @Test
    public void shouldErrorOnAddPublicationWithNonEqualSessionId() {
        driverProxy.addPublication(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final ArgumentCaptor<NetworkPublication> argumentCaptor = ArgumentCaptor.forClass(NetworkPublication.class);
        Mockito.verify(senderProxy).newNetworkPublication(argumentCaptor.capture());
        final String sessionIdParam = (("|" + (CommonContext.SESSION_ID_PARAM_NAME)) + "=") + ((argumentCaptor.getValue().sessionId()) + 1);
        final long correlationId = driverProxy.addPublication(((DriverConductorTest.CHANNEL_4000) + sessionIdParam), DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        Mockito.verify(mockClientProxy).onError(ArgumentMatchers.eq(correlationId), ArgumentMatchers.eq(GENERIC_ERROR), ArgumentMatchers.anyString());
        Mockito.verify(mockErrorCounter).increment();
        Mockito.verify(mockErrorHandler).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void shouldErrorOnAddPublicationWithClashingSessionId() {
        driverProxy.addPublication(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final ArgumentCaptor<NetworkPublication> argumentCaptor = ArgumentCaptor.forClass(NetworkPublication.class);
        Mockito.verify(senderProxy).newNetworkPublication(argumentCaptor.capture());
        final String sessionIdParam = (("|" + (CommonContext.SESSION_ID_PARAM_NAME)) + "=") + (argumentCaptor.getValue().sessionId());
        final long correlationId = driverProxy.addExclusivePublication(((DriverConductorTest.CHANNEL_4000) + sessionIdParam), DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        Mockito.verify(mockClientProxy).onError(ArgumentMatchers.eq(correlationId), ArgumentMatchers.eq(GENERIC_ERROR), ArgumentMatchers.anyString());
        Mockito.verify(mockErrorCounter).increment();
        Mockito.verify(mockErrorHandler).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void shouldAvoidAssigningClashingSessionIdOnAddPublication() {
        driverProxy.addPublication(DriverConductorTest.CHANNEL_4000, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final ArgumentCaptor<NetworkPublication> argumentCaptor = ArgumentCaptor.forClass(NetworkPublication.class);
        Mockito.verify(senderProxy).newNetworkPublication(argumentCaptor.capture());
        final int sessionId = argumentCaptor.getValue().sessionId();
        final String sessionIdParam = (("|" + (CommonContext.SESSION_ID_PARAM_NAME)) + "=") + (sessionId + 1);
        driverProxy.addExclusivePublication(((DriverConductorTest.CHANNEL_4000) + sessionIdParam), DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final long correlationId = driverProxy.addPublication(DriverConductorTest.CHANNEL_4000, ((DriverConductorTest.STREAM_ID_1) + 1));
        driverConductor.doWork();
        Mockito.verify(mockClientProxy).onPublicationReady(ArgumentMatchers.eq(correlationId), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(((DriverConductorTest.STREAM_ID_1) + 1)), ArgumentMatchers.eq((sessionId + 2)), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(false));
    }

    @Test
    public void shouldAddIpcPublicationThenSubscriptionWithSessionId() {
        final int sessionId = -4097;
        final String sessionIdParam = (("?" + (CommonContext.SESSION_ID_PARAM_NAME)) + "=") + sessionId;
        final String channelIpcAndSessionId = (DriverConductorTest.CHANNEL_IPC) + sessionIdParam;
        driverProxy.addPublication(channelIpcAndSessionId, DriverConductorTest.STREAM_ID_1);
        driverProxy.addSubscription(channelIpcAndSessionId, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final IpcPublication ipcPublication = driverConductor.getSharedIpcPublication(DriverConductorTest.STREAM_ID_1);
        Assert.assertNotNull(ipcPublication);
        Mockito.verify(mockClientProxy).onAvailableImage(ArgumentMatchers.eq(ipcPublication.registrationId()), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.eq(ipcPublication.sessionId()), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(ipcPublication.rawLog().fileName()), ArgumentMatchers.anyString());
    }

    @Test
    public void shouldAddIpcSubscriptionThenPublicationWithSessionId() {
        final int sessionId = -4097;
        final String sessionIdParam = (("?" + (CommonContext.SESSION_ID_PARAM_NAME)) + "=") + sessionId;
        final String channelIpcAndSessionId = (DriverConductorTest.CHANNEL_IPC) + sessionIdParam;
        driverProxy.addSubscription(channelIpcAndSessionId, DriverConductorTest.STREAM_ID_1);
        driverProxy.addPublication(channelIpcAndSessionId, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final IpcPublication ipcPublication = driverConductor.getSharedIpcPublication(DriverConductorTest.STREAM_ID_1);
        Assert.assertNotNull(ipcPublication);
        Mockito.verify(mockClientProxy).onAvailableImage(ArgumentMatchers.eq(ipcPublication.registrationId()), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.eq(ipcPublication.sessionId()), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(ipcPublication.rawLog().fileName()), ArgumentMatchers.anyString());
    }

    @Test
    public void shouldNotAddIpcPublicationThenSubscriptionWithDifferentSessionId() {
        final int sessionIdPub = -4097;
        final int sessionIdSub = -4098;
        final String sessionIdPubParam = (("?" + (CommonContext.SESSION_ID_PARAM_NAME)) + "=") + sessionIdPub;
        final String sessionIdSubParam = (("?" + (CommonContext.SESSION_ID_PARAM_NAME)) + "=") + sessionIdSub;
        driverProxy.addPublication(((DriverConductorTest.CHANNEL_IPC) + sessionIdPubParam), DriverConductorTest.STREAM_ID_1);
        driverProxy.addSubscription(((DriverConductorTest.CHANNEL_IPC) + sessionIdSubParam), DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final IpcPublication ipcPublication = driverConductor.getSharedIpcPublication(DriverConductorTest.STREAM_ID_1);
        Assert.assertNotNull(ipcPublication);
        Mockito.verify(mockClientProxy, Mockito.never()).onAvailableImage(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    public void shouldNotAddIpcSubscriptionThenPublicationWithDifferentSessionId() {
        final int sessionIdPub = -4097;
        final int sessionIdSub = -4098;
        final String sessionIdPubParam = (("?" + (CommonContext.SESSION_ID_PARAM_NAME)) + "=") + sessionIdPub;
        final String sessionIdSubParam = (("?" + (CommonContext.SESSION_ID_PARAM_NAME)) + "=") + sessionIdSub;
        driverProxy.addSubscription(((DriverConductorTest.CHANNEL_IPC) + sessionIdSubParam), DriverConductorTest.STREAM_ID_1);
        driverProxy.addPublication(((DriverConductorTest.CHANNEL_IPC) + sessionIdPubParam), DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final IpcPublication ipcPublication = driverConductor.getSharedIpcPublication(DriverConductorTest.STREAM_ID_1);
        Assert.assertNotNull(ipcPublication);
        Mockito.verify(mockClientProxy, Mockito.never()).onAvailableImage(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    public void shouldAddNetworkPublicationThenSingleSpyWithSameSessionId() {
        final int sessionId = -4097;
        final String sessionIdParam = (("|" + (CommonContext.SESSION_ID_PARAM_NAME)) + "=") + sessionId;
        driverProxy.addPublication(((DriverConductorTest.CHANNEL_4000) + sessionIdParam), DriverConductorTest.STREAM_ID_1);
        driverProxy.addSubscription(DriverConductorTest.spyForChannel(((DriverConductorTest.CHANNEL_4000) + sessionIdParam)), DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final ArgumentCaptor<NetworkPublication> captor = ArgumentCaptor.forClass(NetworkPublication.class);
        Mockito.verify(senderProxy, Mockito.times(1)).newNetworkPublication(captor.capture());
        final NetworkPublication publication = captor.getValue();
        Assert.assertTrue(publication.hasSpies());
        Mockito.verify(mockClientProxy).onAvailableImage(ArgumentMatchers.eq(DriverConductorTest.networkPublicationCorrelationId(publication)), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.eq(publication.sessionId()), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(publication.rawLog().fileName()), ArgumentMatchers.anyString());
    }

    @Test
    public void shouldNotAddNetworkPublicationThenSingleSpyWithDifferentSessionId() {
        final int sessionIdPub = -4097;
        final int sessionIdSub = -4098;
        final String sessionIdPubParam = (("|" + (CommonContext.SESSION_ID_PARAM_NAME)) + "=") + sessionIdPub;
        final String sessionIdSubParam = (("|" + (CommonContext.SESSION_ID_PARAM_NAME)) + "=") + sessionIdSub;
        driverProxy.addPublication(((DriverConductorTest.CHANNEL_4000) + sessionIdPubParam), DriverConductorTest.STREAM_ID_1);
        driverProxy.addSubscription(DriverConductorTest.spyForChannel(((DriverConductorTest.CHANNEL_4000) + sessionIdSubParam)), DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final ArgumentCaptor<NetworkPublication> captor = ArgumentCaptor.forClass(NetworkPublication.class);
        Mockito.verify(senderProxy, Mockito.times(1)).newNetworkPublication(captor.capture());
        final NetworkPublication publication = captor.getValue();
        Assert.assertFalse(publication.hasSpies());
        Mockito.verify(mockClientProxy, Mockito.never()).onAvailableImage(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    public void shouldAddSingleSpyThenNetworkPublicationWithSameSessionId() {
        final int sessionId = -4097;
        final String sessionIdParam = (("|" + (CommonContext.SESSION_ID_PARAM_NAME)) + "=") + sessionId;
        driverProxy.addSubscription(DriverConductorTest.spyForChannel(((DriverConductorTest.CHANNEL_4000) + sessionIdParam)), DriverConductorTest.STREAM_ID_1);
        driverProxy.addPublication(((DriverConductorTest.CHANNEL_4000) + sessionIdParam), DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final ArgumentCaptor<NetworkPublication> captor = ArgumentCaptor.forClass(NetworkPublication.class);
        Mockito.verify(senderProxy, Mockito.times(1)).newNetworkPublication(captor.capture());
        final NetworkPublication publication = captor.getValue();
        Assert.assertTrue(publication.hasSpies());
        Mockito.verify(mockClientProxy).onAvailableImage(ArgumentMatchers.eq(DriverConductorTest.networkPublicationCorrelationId(publication)), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.eq(publication.sessionId()), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(publication.rawLog().fileName()), ArgumentMatchers.anyString());
    }

    @Test
    public void shouldNotAddSingleSpyThenNetworkPublicationWithDifferentSessionId() {
        final int sessionIdPub = -4097;
        final int sessionIdSub = -4098;
        final String sessionIdPubParam = (("|" + (CommonContext.SESSION_ID_PARAM_NAME)) + "=") + sessionIdPub;
        final String sessionIdSubParam = (("|" + (CommonContext.SESSION_ID_PARAM_NAME)) + "=") + sessionIdSub;
        driverProxy.addSubscription(DriverConductorTest.spyForChannel(((DriverConductorTest.CHANNEL_4000) + sessionIdSubParam)), DriverConductorTest.STREAM_ID_1);
        driverProxy.addPublication(((DriverConductorTest.CHANNEL_4000) + sessionIdPubParam), DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        final ArgumentCaptor<NetworkPublication> captor = ArgumentCaptor.forClass(NetworkPublication.class);
        Mockito.verify(senderProxy, Mockito.times(1)).newNetworkPublication(captor.capture());
        final NetworkPublication publication = captor.getValue();
        Assert.assertFalse(publication.hasSpies());
        Mockito.verify(mockClientProxy, Mockito.never()).onAvailableImage(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(DriverConductorTest.STREAM_ID_1), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    public void shouldUseExistingChannelEndpointOnAddPublciationWithSameTagIdAndSameStreamId() {
        final long id1 = driverProxy.addPublication(DriverConductorTest.CHANNEL_4000_TAG_ID_1, DriverConductorTest.STREAM_ID_1);
        final long id2 = driverProxy.addPublication(DriverConductorTest.CHANNEL_TAG_ID_1, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        Mockito.verify(mockErrorHandler, Mockito.never()).onError(ArgumentMatchers.any());
        Mockito.verify(senderProxy).registerSendChannelEndpoint(ArgumentMatchers.any());
        Mockito.verify(senderProxy).newNetworkPublication(ArgumentMatchers.any());
        driverProxy.removePublication(id1);
        driverProxy.removePublication(id2);
        doWorkUntil(() -> ((((DriverConductorTest.PUBLICATION_LINGER_TIMEOUT_NS) * 2) + ((DriverConductorTest.CLIENT_LIVENESS_TIMEOUT_NS) * 2)) - (nanoClock.nanoTime())) <= 0);
        Mockito.verify(senderProxy).closeSendChannelEndpoint(ArgumentMatchers.any());
    }

    @Test
    public void shouldUseExistingChannelEndpointOnAddPublicationWithSameTagIdDifferentStreamId() {
        final long id1 = driverProxy.addPublication(DriverConductorTest.CHANNEL_4000_TAG_ID_1, DriverConductorTest.STREAM_ID_1);
        final long id2 = driverProxy.addPublication(DriverConductorTest.CHANNEL_TAG_ID_1, DriverConductorTest.STREAM_ID_2);
        driverConductor.doWork();
        Mockito.verify(mockErrorHandler, Mockito.never()).onError(ArgumentMatchers.any());
        Mockito.verify(senderProxy).registerSendChannelEndpoint(ArgumentMatchers.any());
        Mockito.verify(senderProxy, Mockito.times(2)).newNetworkPublication(ArgumentMatchers.any());
        driverProxy.removePublication(id1);
        driverProxy.removePublication(id2);
        doWorkUntil(() -> ((((DriverConductorTest.PUBLICATION_LINGER_TIMEOUT_NS) * 2) + ((DriverConductorTest.CLIENT_LIVENESS_TIMEOUT_NS) * 2)) - (nanoClock.nanoTime())) <= 0);
        Mockito.verify(senderProxy).closeSendChannelEndpoint(ArgumentMatchers.any());
    }

    @Test
    public void shouldUseExistingChannelEndpointOnAddSubscriptionWithSameTagId() {
        final long id1 = driverProxy.addSubscription(DriverConductorTest.CHANNEL_4000_TAG_ID_1, DriverConductorTest.STREAM_ID_1);
        final long id2 = driverProxy.addSubscription(DriverConductorTest.CHANNEL_TAG_ID_1, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        Mockito.verify(mockErrorHandler, Mockito.never()).onError(ArgumentMatchers.any());
        Mockito.verify(receiverProxy).registerReceiveChannelEndpoint(ArgumentMatchers.any());
        driverProxy.removeSubscription(id1);
        driverProxy.removeSubscription(id2);
        driverConductor.doWork();
        Mockito.verify(receiverProxy).closeReceiveChannelEndpoint(ArgumentMatchers.any());
    }

    @Test
    public void shouldUseUniqueChannelEndpointOnAddSubscriptionWithNoDistinguishingCharacteristics() {
        final long id1 = driverProxy.addSubscription(DriverConductorTest.CHANNEL_SUB_CONTROL_MODE_MANUAL, DriverConductorTest.STREAM_ID_1);
        final long id2 = driverProxy.addSubscription(DriverConductorTest.CHANNEL_SUB_CONTROL_MODE_MANUAL, DriverConductorTest.STREAM_ID_1);
        driverConductor.doWork();
        Mockito.verify(receiverProxy, Mockito.times(2)).registerReceiveChannelEndpoint(ArgumentMatchers.any());
        driverProxy.removeSubscription(id1);
        driverProxy.removeSubscription(id2);
        driverConductor.doWork();
        Mockito.verify(receiverProxy, Mockito.times(2)).closeReceiveChannelEndpoint(ArgumentMatchers.any());
        Mockito.verify(mockErrorHandler, Mockito.never()).onError(ArgumentMatchers.any());
    }
}

