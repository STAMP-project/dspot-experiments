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


import io.aeron.driver.media.ReceiveChannelEndpoint;
import io.aeron.logbuffer.LogBufferDescriptor;
import io.aeron.protocol.DataHeaderFlyweight;
import io.aeron.protocol.SetupFlyweight;
import java.net.InetSocketAddress;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class DataPacketDispatcherTest {
    private static final long CORRELATION_ID_1 = 101;

    private static final long CORRELATION_ID_2 = 102;

    private static final int STREAM_ID = 10;

    private static final int INITIAL_TERM_ID = 3;

    private static final int ACTIVE_TERM_ID = 3;

    private static final int SESSION_ID = 1;

    private static final int TERM_OFFSET = 0;

    private static final int LENGTH = (DataHeaderFlyweight.HEADER_LENGTH) + 100;

    private static final int MTU_LENGTH = 1024;

    private static final int TERM_LENGTH = LogBufferDescriptor.TERM_MIN_LENGTH;

    private static final InetSocketAddress SRC_ADDRESS = new InetSocketAddress("localhost", 4510);

    private final DriverConductorProxy mockConductorProxy = Mockito.mock(DriverConductorProxy.class);

    private final Receiver mockReceiver = Mockito.mock(Receiver.class);

    private final DataPacketDispatcher dispatcher = new DataPacketDispatcher(mockConductorProxy, mockReceiver);

    private final DataHeaderFlyweight mockHeader = Mockito.mock(DataHeaderFlyweight.class);

    private final SetupFlyweight mockSetupHeader = Mockito.mock(SetupFlyweight.class);

    private final UnsafeBuffer mockBuffer = Mockito.mock(UnsafeBuffer.class);

    private final PublicationImage mockImage = Mockito.mock(PublicationImage.class);

    private final ReceiveChannelEndpoint mockChannelEndpoint = Mockito.mock(ReceiveChannelEndpoint.class);

    @Test
    public void shouldElicitSetupMessageWhenDataArrivesForSubscriptionWithoutImage() {
        dispatcher.addSubscription(DataPacketDispatcherTest.STREAM_ID);
        dispatcher.onDataPacket(mockChannelEndpoint, mockHeader, mockBuffer, DataPacketDispatcherTest.LENGTH, DataPacketDispatcherTest.SRC_ADDRESS, 0);
        Mockito.verify(mockImage, Mockito.never()).insertPacket(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any());
        Mockito.verify(mockChannelEndpoint).sendSetupElicitingStatusMessage(0, DataPacketDispatcherTest.SRC_ADDRESS, DataPacketDispatcherTest.SESSION_ID, DataPacketDispatcherTest.STREAM_ID);
        Mockito.verify(mockReceiver).addPendingSetupMessage(DataPacketDispatcherTest.SESSION_ID, DataPacketDispatcherTest.STREAM_ID, 0, mockChannelEndpoint, false, DataPacketDispatcherTest.SRC_ADDRESS);
    }

    @Test
    public void shouldOnlyElicitSetupMessageOnceWhenDataArrivesForSubscriptionWithoutImage() {
        dispatcher.addSubscription(DataPacketDispatcherTest.STREAM_ID);
        dispatcher.onDataPacket(mockChannelEndpoint, mockHeader, mockBuffer, DataPacketDispatcherTest.LENGTH, DataPacketDispatcherTest.SRC_ADDRESS, 0);
        dispatcher.onDataPacket(mockChannelEndpoint, mockHeader, mockBuffer, DataPacketDispatcherTest.LENGTH, DataPacketDispatcherTest.SRC_ADDRESS, 0);
        dispatcher.onDataPacket(mockChannelEndpoint, mockHeader, mockBuffer, DataPacketDispatcherTest.LENGTH, DataPacketDispatcherTest.SRC_ADDRESS, 0);
        Mockito.verify(mockImage, Mockito.never()).insertPacket(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any());
        Mockito.verify(mockChannelEndpoint).sendSetupElicitingStatusMessage(0, DataPacketDispatcherTest.SRC_ADDRESS, DataPacketDispatcherTest.SESSION_ID, DataPacketDispatcherTest.STREAM_ID);
        Mockito.verify(mockReceiver).addPendingSetupMessage(DataPacketDispatcherTest.SESSION_ID, DataPacketDispatcherTest.STREAM_ID, 0, mockChannelEndpoint, false, DataPacketDispatcherTest.SRC_ADDRESS);
    }

    @Test
    public void shouldElicitSetupMessageAgainWhenDataArrivesForSubscriptionWithoutImageAfterRemovePendingSetup() {
        dispatcher.addSubscription(DataPacketDispatcherTest.STREAM_ID);
        dispatcher.onDataPacket(mockChannelEndpoint, mockHeader, mockBuffer, DataPacketDispatcherTest.LENGTH, DataPacketDispatcherTest.SRC_ADDRESS, 0);
        dispatcher.onDataPacket(mockChannelEndpoint, mockHeader, mockBuffer, DataPacketDispatcherTest.LENGTH, DataPacketDispatcherTest.SRC_ADDRESS, 0);
        dispatcher.removePendingSetup(DataPacketDispatcherTest.SESSION_ID, DataPacketDispatcherTest.STREAM_ID);
        dispatcher.onDataPacket(mockChannelEndpoint, mockHeader, mockBuffer, DataPacketDispatcherTest.LENGTH, DataPacketDispatcherTest.SRC_ADDRESS, 0);
        dispatcher.onDataPacket(mockChannelEndpoint, mockHeader, mockBuffer, DataPacketDispatcherTest.LENGTH, DataPacketDispatcherTest.SRC_ADDRESS, 0);
        Mockito.verify(mockImage, Mockito.never()).insertPacket(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any());
        Mockito.verify(mockChannelEndpoint, Mockito.times(2)).sendSetupElicitingStatusMessage(0, DataPacketDispatcherTest.SRC_ADDRESS, DataPacketDispatcherTest.SESSION_ID, DataPacketDispatcherTest.STREAM_ID);
        Mockito.verify(mockReceiver, Mockito.times(2)).addPendingSetupMessage(DataPacketDispatcherTest.SESSION_ID, DataPacketDispatcherTest.STREAM_ID, 0, mockChannelEndpoint, false, DataPacketDispatcherTest.SRC_ADDRESS);
    }

    @Test
    public void shouldRequestCreateImageUponReceivingSetup() {
        dispatcher.addSubscription(DataPacketDispatcherTest.STREAM_ID);
        dispatcher.onSetupMessage(mockChannelEndpoint, mockSetupHeader, DataPacketDispatcherTest.SRC_ADDRESS, 0);
        Mockito.verify(mockConductorProxy).createPublicationImage(DataPacketDispatcherTest.SESSION_ID, DataPacketDispatcherTest.STREAM_ID, DataPacketDispatcherTest.INITIAL_TERM_ID, DataPacketDispatcherTest.ACTIVE_TERM_ID, DataPacketDispatcherTest.TERM_OFFSET, DataPacketDispatcherTest.TERM_LENGTH, DataPacketDispatcherTest.MTU_LENGTH, 0, DataPacketDispatcherTest.SRC_ADDRESS, DataPacketDispatcherTest.SRC_ADDRESS, mockChannelEndpoint);
    }

    @Test
    public void shouldOnlyRequestCreateImageOnceUponReceivingSetup() {
        dispatcher.addSubscription(DataPacketDispatcherTest.STREAM_ID);
        dispatcher.onSetupMessage(mockChannelEndpoint, mockSetupHeader, DataPacketDispatcherTest.SRC_ADDRESS, 0);
        dispatcher.onSetupMessage(mockChannelEndpoint, mockSetupHeader, DataPacketDispatcherTest.SRC_ADDRESS, 0);
        dispatcher.onSetupMessage(mockChannelEndpoint, mockSetupHeader, DataPacketDispatcherTest.SRC_ADDRESS, 0);
        Mockito.verify(mockConductorProxy).createPublicationImage(DataPacketDispatcherTest.SESSION_ID, DataPacketDispatcherTest.STREAM_ID, DataPacketDispatcherTest.INITIAL_TERM_ID, DataPacketDispatcherTest.ACTIVE_TERM_ID, DataPacketDispatcherTest.TERM_OFFSET, DataPacketDispatcherTest.TERM_LENGTH, DataPacketDispatcherTest.MTU_LENGTH, 0, DataPacketDispatcherTest.SRC_ADDRESS, DataPacketDispatcherTest.SRC_ADDRESS, mockChannelEndpoint);
    }

    @Test
    public void shouldNotRequestCreateImageOnceUponReceivingSetupAfterImageAdded() {
        dispatcher.addSubscription(DataPacketDispatcherTest.STREAM_ID);
        dispatcher.addPublicationImage(mockImage);
        dispatcher.onSetupMessage(mockChannelEndpoint, mockSetupHeader, DataPacketDispatcherTest.SRC_ADDRESS, 0);
        Mockito.verifyZeroInteractions(mockConductorProxy);
    }

    @Test
    public void shouldSetImageInactiveOnRemoveSubscription() {
        dispatcher.addSubscription(DataPacketDispatcherTest.STREAM_ID);
        dispatcher.addPublicationImage(mockImage);
        dispatcher.removeSubscription(DataPacketDispatcherTest.STREAM_ID);
        Mockito.verify(mockImage).ifActiveGoInactive();
    }

    @Test
    public void shouldSetImageInactiveOnRemoveImage() {
        dispatcher.addSubscription(DataPacketDispatcherTest.STREAM_ID);
        dispatcher.addPublicationImage(mockImage);
        dispatcher.removePublicationImage(mockImage);
        Mockito.verify(mockImage).ifActiveGoInactive();
    }

    @Test
    public void shouldIgnoreDataAndSetupAfterImageRemoved() {
        dispatcher.addSubscription(DataPacketDispatcherTest.STREAM_ID);
        dispatcher.addPublicationImage(mockImage);
        dispatcher.removePublicationImage(mockImage);
        dispatcher.onDataPacket(mockChannelEndpoint, mockHeader, mockBuffer, DataPacketDispatcherTest.LENGTH, DataPacketDispatcherTest.SRC_ADDRESS, 0);
        dispatcher.onSetupMessage(mockChannelEndpoint, mockSetupHeader, DataPacketDispatcherTest.SRC_ADDRESS, 0);
        Mockito.verifyZeroInteractions(mockConductorProxy);
        Mockito.verifyZeroInteractions(mockReceiver);
    }

    @Test
    public void shouldNotIgnoreDataAndSetupAfterImageRemovedAndCoolDownRemoved() {
        dispatcher.addSubscription(DataPacketDispatcherTest.STREAM_ID);
        dispatcher.addPublicationImage(mockImage);
        dispatcher.removePublicationImage(mockImage);
        dispatcher.removeCoolDown(DataPacketDispatcherTest.SESSION_ID, DataPacketDispatcherTest.STREAM_ID);
        dispatcher.onDataPacket(mockChannelEndpoint, mockHeader, mockBuffer, DataPacketDispatcherTest.LENGTH, DataPacketDispatcherTest.SRC_ADDRESS, 0);
        dispatcher.onSetupMessage(mockChannelEndpoint, mockSetupHeader, DataPacketDispatcherTest.SRC_ADDRESS, 0);
        Mockito.verify(mockImage, Mockito.never()).insertPacket(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any());
        final InOrder inOrder = Mockito.inOrder(mockChannelEndpoint, mockReceiver, mockConductorProxy);
        inOrder.verify(mockChannelEndpoint).sendSetupElicitingStatusMessage(0, DataPacketDispatcherTest.SRC_ADDRESS, DataPacketDispatcherTest.SESSION_ID, DataPacketDispatcherTest.STREAM_ID);
        inOrder.verify(mockReceiver).addPendingSetupMessage(DataPacketDispatcherTest.SESSION_ID, DataPacketDispatcherTest.STREAM_ID, 0, mockChannelEndpoint, false, DataPacketDispatcherTest.SRC_ADDRESS);
        inOrder.verify(mockConductorProxy).createPublicationImage(DataPacketDispatcherTest.SESSION_ID, DataPacketDispatcherTest.STREAM_ID, DataPacketDispatcherTest.INITIAL_TERM_ID, DataPacketDispatcherTest.ACTIVE_TERM_ID, DataPacketDispatcherTest.TERM_OFFSET, DataPacketDispatcherTest.TERM_LENGTH, DataPacketDispatcherTest.MTU_LENGTH, 0, DataPacketDispatcherTest.SRC_ADDRESS, DataPacketDispatcherTest.SRC_ADDRESS, mockChannelEndpoint);
    }

    @Test
    public void shouldDispatchDataToCorrectImage() {
        dispatcher.addSubscription(DataPacketDispatcherTest.STREAM_ID);
        dispatcher.addPublicationImage(mockImage);
        dispatcher.onDataPacket(mockChannelEndpoint, mockHeader, mockBuffer, DataPacketDispatcherTest.LENGTH, DataPacketDispatcherTest.SRC_ADDRESS, 0);
        Mockito.verify(mockImage).activate();
        Mockito.verify(mockImage).insertPacket(DataPacketDispatcherTest.ACTIVE_TERM_ID, DataPacketDispatcherTest.TERM_OFFSET, mockBuffer, DataPacketDispatcherTest.LENGTH, 0, DataPacketDispatcherTest.SRC_ADDRESS);
    }

    @Test
    public void shouldNotRemoveNewPublicationImageFromOldRemovePublicationImageAfterRemoveSubscription() {
        final PublicationImage mockImage1 = Mockito.mock(PublicationImage.class);
        final PublicationImage mockImage2 = Mockito.mock(PublicationImage.class);
        Mockito.when(mockImage1.sessionId()).thenReturn(DataPacketDispatcherTest.SESSION_ID);
        Mockito.when(mockImage1.streamId()).thenReturn(DataPacketDispatcherTest.STREAM_ID);
        Mockito.when(mockImage1.correlationId()).thenReturn(DataPacketDispatcherTest.CORRELATION_ID_1);
        Mockito.when(mockImage2.sessionId()).thenReturn(DataPacketDispatcherTest.SESSION_ID);
        Mockito.when(mockImage2.streamId()).thenReturn(DataPacketDispatcherTest.STREAM_ID);
        Mockito.when(mockImage2.correlationId()).thenReturn(DataPacketDispatcherTest.CORRELATION_ID_2);
        dispatcher.addSubscription(DataPacketDispatcherTest.STREAM_ID);
        dispatcher.addPublicationImage(mockImage1);
        dispatcher.removeSubscription(DataPacketDispatcherTest.STREAM_ID);
        dispatcher.addSubscription(DataPacketDispatcherTest.STREAM_ID);
        dispatcher.addPublicationImage(mockImage2);
        dispatcher.removePublicationImage(mockImage1);
        dispatcher.onDataPacket(mockChannelEndpoint, mockHeader, mockBuffer, DataPacketDispatcherTest.LENGTH, DataPacketDispatcherTest.SRC_ADDRESS, 0);
        Mockito.verify(mockImage1, Mockito.never()).insertPacket(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any());
        Mockito.verify(mockImage2).insertPacket(DataPacketDispatcherTest.ACTIVE_TERM_ID, DataPacketDispatcherTest.TERM_OFFSET, mockBuffer, DataPacketDispatcherTest.LENGTH, 0, DataPacketDispatcherTest.SRC_ADDRESS);
    }

    @Test
    public void shouldRemoveSessionSpecificSubscriptionWithoutAny() {
        dispatcher.addSubscription(DataPacketDispatcherTest.STREAM_ID, DataPacketDispatcherTest.SESSION_ID);
        dispatcher.removeSubscription(DataPacketDispatcherTest.STREAM_ID, DataPacketDispatcherTest.SESSION_ID);
    }
}

