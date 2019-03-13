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
package io.aeron;


import MediaDriver.Context;
import PreferredMulticastFlowControl.PREFERRED_ASF_BYTES;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import io.aeron.logbuffer.LogBufferDescriptor;
import io.aeron.protocol.DataHeaderFlyweight;
import java.io.File;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.IntConsumer;
import org.agrona.DirectBuffer;
import org.agrona.SystemUtil;
import org.agrona.collections.MutableInteger;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static Publication.NOT_CONNECTED;


/**
 * Tests requiring multiple embedded drivers for FlowControl strategies
 */
public class FlowControlStrategiesTest {
    private static final String MULTICAST_URI = "aeron:udp?endpoint=224.20.30.39:54326|interface=localhost";

    private static final int STREAM_ID = 1;

    private static final int TERM_BUFFER_LENGTH = LogBufferDescriptor.TERM_MIN_LENGTH;

    private static final int NUM_MESSAGES_PER_TERM = 64;

    private static final int MESSAGE_LENGTH = ((FlowControlStrategiesTest.TERM_BUFFER_LENGTH) / (FlowControlStrategiesTest.NUM_MESSAGES_PER_TERM)) - (DataHeaderFlyweight.HEADER_LENGTH);

    private static final String ROOT_DIR = (((SystemUtil.tmpDirName()) + "aeron-system-tests-") + (UUID.randomUUID().toString())) + (File.separator);

    private final Context driverAContext = new MediaDriver.Context();

    private final Context driverBContext = new MediaDriver.Context();

    private Aeron clientA;

    private Aeron clientB;

    private MediaDriver driverA;

    private MediaDriver driverB;

    private Publication publication;

    private Subscription subscriptionA;

    private Subscription subscriptionB;

    private final UnsafeBuffer buffer = new UnsafeBuffer(new byte[FlowControlStrategiesTest.MESSAGE_LENGTH]);

    private final FragmentHandler fragmentHandlerA = Mockito.mock(FragmentHandler.class);

    private final FragmentHandler fragmentHandlerB = Mockito.mock(FragmentHandler.class);

    @Test(timeout = 10000)
    public void shouldSpinUpAndShutdown() {
        launch();
        subscriptionA = clientA.addSubscription(FlowControlStrategiesTest.MULTICAST_URI, FlowControlStrategiesTest.STREAM_ID);
        subscriptionB = clientB.addSubscription(FlowControlStrategiesTest.MULTICAST_URI, FlowControlStrategiesTest.STREAM_ID);
        publication = clientA.addPublication(FlowControlStrategiesTest.MULTICAST_URI, FlowControlStrategiesTest.STREAM_ID);
        while ((!(subscriptionA.isConnected())) || (!(subscriptionB.isConnected()))) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
    }

    @Test(timeout = 10000)
    public void shouldTimeoutImageWhenBehindForTooLongWithMaxMulticastFlowControlStrategy() {
        final int numMessagesToSend = (FlowControlStrategiesTest.NUM_MESSAGES_PER_TERM) * 3;
        driverBContext.imageLivenessTimeoutNs(TimeUnit.MILLISECONDS.toNanos(500));
        driverAContext.multicastFlowControlSupplier(( udpChannel, streamId, registrationId) -> new MaxMulticastFlowControl());
        launch();
        subscriptionA = clientA.addSubscription(FlowControlStrategiesTest.MULTICAST_URI, FlowControlStrategiesTest.STREAM_ID);
        subscriptionB = clientB.addSubscription(FlowControlStrategiesTest.MULTICAST_URI, FlowControlStrategiesTest.STREAM_ID);
        publication = clientA.addPublication(FlowControlStrategiesTest.MULTICAST_URI, FlowControlStrategiesTest.STREAM_ID);
        while ((!(subscriptionA.isConnected())) || (!(subscriptionB.isConnected()))) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
        for (int i = 0; i < numMessagesToSend; i++) {
            while ((publication.offer(buffer, 0, buffer.capacity())) < 0L) {
                SystemTest.checkInterruptedStatus();
                Thread.yield();
            } 
            // A keeps up
            final MutableInteger fragmentsRead = new MutableInteger();
            SystemTest.executeUntil(() -> (fragmentsRead.get()) > 0, ( j) -> {
                fragmentsRead.value += subscriptionA.poll(fragmentHandlerA, 10);
                Thread.yield();
            }, Integer.MAX_VALUE, TimeUnit.MILLISECONDS.toNanos(500));
            fragmentsRead.set(0);
            // B receives slowly and eventually can't keep up
            if ((i % 10) == 0) {
                SystemTest.executeUntil(() -> (fragmentsRead.get()) > 0, ( j) -> {
                    fragmentsRead.value += subscriptionB.poll(fragmentHandlerB, 1);
                    Thread.yield();
                }, Integer.MAX_VALUE, TimeUnit.MILLISECONDS.toNanos(500));
            }
        }
        Mockito.verify(fragmentHandlerA, Mockito.times(numMessagesToSend)).onFragment(ArgumentMatchers.any(DirectBuffer.class), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(FlowControlStrategiesTest.MESSAGE_LENGTH), ArgumentMatchers.any(Header.class));
        Mockito.verify(fragmentHandlerB, Mockito.atMost((numMessagesToSend - 1))).onFragment(ArgumentMatchers.any(DirectBuffer.class), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(FlowControlStrategiesTest.MESSAGE_LENGTH), ArgumentMatchers.any(Header.class));
    }

    @Test(timeout = 10000)
    public void shouldSlowDownWhenBehindWithMinMulticastFlowControlStrategy() {
        final int numMessagesToSend = (FlowControlStrategiesTest.NUM_MESSAGES_PER_TERM) * 3;
        int numMessagesLeftToSend = numMessagesToSend;
        int numFragmentsFromA = 0;
        int numFragmentsFromB = 0;
        driverBContext.imageLivenessTimeoutNs(TimeUnit.MILLISECONDS.toNanos(500));
        driverAContext.multicastFlowControlSupplier(( udpChannel, streamId, registrationId) -> new MinMulticastFlowControl());
        launch();
        subscriptionA = clientA.addSubscription(FlowControlStrategiesTest.MULTICAST_URI, FlowControlStrategiesTest.STREAM_ID);
        subscriptionB = clientB.addSubscription(FlowControlStrategiesTest.MULTICAST_URI, FlowControlStrategiesTest.STREAM_ID);
        publication = clientA.addPublication(FlowControlStrategiesTest.MULTICAST_URI, FlowControlStrategiesTest.STREAM_ID);
        while ((!(subscriptionA.isConnected())) || (!(subscriptionB.isConnected()))) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
        for (long i = 0; (numFragmentsFromA < numMessagesToSend) || (numFragmentsFromB < numMessagesToSend); i++) {
            if (numMessagesLeftToSend > 0) {
                if ((publication.offer(buffer, 0, buffer.capacity())) >= 0L) {
                    numMessagesLeftToSend--;
                }
            }
            SystemTest.checkInterruptedStatus();
            Thread.yield();
            // A keeps up
            numFragmentsFromA += subscriptionA.poll(fragmentHandlerA, 10);
            // B receives slowly
            if ((i % 2) == 0) {
                numFragmentsFromB += subscriptionB.poll(fragmentHandlerB, 1);
            }
        }
        Mockito.verify(fragmentHandlerA, Mockito.times(numMessagesToSend)).onFragment(ArgumentMatchers.any(DirectBuffer.class), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(FlowControlStrategiesTest.MESSAGE_LENGTH), ArgumentMatchers.any(Header.class));
        Mockito.verify(fragmentHandlerB, Mockito.times(numMessagesToSend)).onFragment(ArgumentMatchers.any(DirectBuffer.class), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(FlowControlStrategiesTest.MESSAGE_LENGTH), ArgumentMatchers.any(Header.class));
    }

    @Test(timeout = 10000)
    public void shouldRemoveDeadReceiverWithMinMulticastFlowControlStrategy() {
        final int numMessagesToSend = (FlowControlStrategiesTest.NUM_MESSAGES_PER_TERM) * 3;
        int numMessagesLeftToSend = numMessagesToSend;
        int numFragmentsFromA = 0;
        int numFragmentsFromB = 0;
        boolean isClosedB = false;
        driverBContext.imageLivenessTimeoutNs(TimeUnit.MILLISECONDS.toNanos(500));
        driverAContext.multicastFlowControlSupplier(( udpChannel, streamId, registrationId) -> new MinMulticastFlowControl());
        launch();
        subscriptionA = clientA.addSubscription(FlowControlStrategiesTest.MULTICAST_URI, FlowControlStrategiesTest.STREAM_ID);
        subscriptionB = clientB.addSubscription(FlowControlStrategiesTest.MULTICAST_URI, FlowControlStrategiesTest.STREAM_ID);
        publication = clientA.addPublication(FlowControlStrategiesTest.MULTICAST_URI, FlowControlStrategiesTest.STREAM_ID);
        while ((!(subscriptionA.isConnected())) || (!(subscriptionB.isConnected()))) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
        while (numFragmentsFromA < numMessagesToSend) {
            if (numMessagesLeftToSend > 0) {
                if ((publication.offer(buffer, 0, buffer.capacity())) >= 0L) {
                    numMessagesLeftToSend--;
                }
            }
            // A keeps up
            numFragmentsFromA += subscriptionA.poll(fragmentHandlerA, 10);
            // B receives up to 1/8 of the messages, then stops
            if (numFragmentsFromB < (numMessagesToSend / 8)) {
                numFragmentsFromB += subscriptionB.poll(fragmentHandlerB, 10);
            } else
                if (!isClosedB) {
                    subscriptionB.close();
                    isClosedB = true;
                }

        } 
        Mockito.verify(fragmentHandlerA, Mockito.times(numMessagesToSend)).onFragment(ArgumentMatchers.any(DirectBuffer.class), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(FlowControlStrategiesTest.MESSAGE_LENGTH), ArgumentMatchers.any(Header.class));
    }

    @Test(timeout = 15000)
    public void shouldSlowToPreferredWithMulticastFlowControlStrategy() {
        final int numMessagesToSend = (FlowControlStrategiesTest.NUM_MESSAGES_PER_TERM) * 3;
        int numMessagesLeftToSend = numMessagesToSend;
        int numFragmentsFromB = 0;
        driverBContext.imageLivenessTimeoutNs(TimeUnit.MILLISECONDS.toNanos(500));
        driverAContext.multicastFlowControlSupplier(( udpChannel, streamId, registrationId) -> new PreferredMulticastFlowControl());
        driverBContext.applicationSpecificFeedback(PREFERRED_ASF_BYTES);
        launch();
        subscriptionA = clientA.addSubscription(FlowControlStrategiesTest.MULTICAST_URI, FlowControlStrategiesTest.STREAM_ID);
        subscriptionB = clientB.addSubscription(FlowControlStrategiesTest.MULTICAST_URI, FlowControlStrategiesTest.STREAM_ID);
        publication = clientA.addPublication(FlowControlStrategiesTest.MULTICAST_URI, FlowControlStrategiesTest.STREAM_ID);
        while ((!(subscriptionA.isConnected())) || (!(subscriptionB.isConnected()))) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
        for (long i = 0; numFragmentsFromB < numMessagesToSend; i++) {
            if (numMessagesLeftToSend > 0) {
                final long result = publication.offer(buffer, 0, buffer.capacity());
                if (result >= 0L) {
                    numMessagesLeftToSend--;
                } else
                    if ((NOT_CONNECTED) == result) {
                        Assert.fail(("Publication not connected, numMessagesLeftToSend=" + numMessagesLeftToSend));
                    }

            }
            SystemTest.checkInterruptedStatus();
            Thread.yield();
            // A keeps up
            subscriptionA.poll(fragmentHandlerA, 10);
            // B receives slowly
            if ((i % 2) == 0) {
                final int bFragments = subscriptionB.poll(fragmentHandlerB, 1);
                if ((0 == bFragments) && (!(subscriptionB.isConnected()))) {
                    if (subscriptionB.isClosed()) {
                        Assert.fail(("Subscription B is closed, numFragmentsFromB=" + numFragmentsFromB));
                    }
                    Assert.fail(("Subscription B not connected, numFragmentsFromB=" + numFragmentsFromB));
                }
                numFragmentsFromB += bFragments;
            }
        }
        Mockito.verify(fragmentHandlerB, Mockito.times(numMessagesToSend)).onFragment(ArgumentMatchers.any(DirectBuffer.class), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(FlowControlStrategiesTest.MESSAGE_LENGTH), ArgumentMatchers.any(Header.class));
    }

    @Test(timeout = 10000)
    public void shouldRemoveDeadPreferredReceiverWithPreferredMulticastFlowControlStrategy() {
        final int numMessagesToSend = (FlowControlStrategiesTest.NUM_MESSAGES_PER_TERM) * 3;
        int numMessagesLeftToSend = numMessagesToSend;
        int numFragmentsReadFromA = 0;
        int numFragmentsReadFromB = 0;
        boolean isBClosed = false;
        driverBContext.imageLivenessTimeoutNs(TimeUnit.MILLISECONDS.toNanos(500));
        driverAContext.multicastFlowControlSupplier(( udpChannel, streamId, registrationId) -> new PreferredMulticastFlowControl());
        driverBContext.applicationSpecificFeedback(PREFERRED_ASF_BYTES);
        launch();
        subscriptionA = clientA.addSubscription(FlowControlStrategiesTest.MULTICAST_URI, FlowControlStrategiesTest.STREAM_ID);
        subscriptionB = clientB.addSubscription(FlowControlStrategiesTest.MULTICAST_URI, FlowControlStrategiesTest.STREAM_ID);
        publication = clientA.addPublication(FlowControlStrategiesTest.MULTICAST_URI, FlowControlStrategiesTest.STREAM_ID);
        while ((!(subscriptionA.isConnected())) || (!(subscriptionB.isConnected()))) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
        while (numFragmentsReadFromA < numMessagesToSend) {
            if (numMessagesLeftToSend > 0) {
                if ((publication.offer(buffer, 0, buffer.capacity())) >= 0L) {
                    numMessagesLeftToSend--;
                }
            }
            // A keeps up
            numFragmentsReadFromA += subscriptionA.poll(fragmentHandlerA, 10);
            // B receives up to 1/8 of the messages, then stops
            if (numFragmentsReadFromB < (numMessagesToSend / 8)) {
                numFragmentsReadFromB += subscriptionB.poll(fragmentHandlerB, 10);
            } else
                if (!isBClosed) {
                    subscriptionB.close();
                    isBClosed = true;
                }

        } 
        Mockito.verify(fragmentHandlerA, Mockito.times(numMessagesToSend)).onFragment(ArgumentMatchers.any(DirectBuffer.class), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(FlowControlStrategiesTest.MESSAGE_LENGTH), ArgumentMatchers.any(Header.class));
    }
}

