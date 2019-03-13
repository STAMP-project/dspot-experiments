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


import CommonContext.UDP_MEDIA;
import MediaDriver.Context;
import io.aeron.driver.MediaDriver;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.LogBufferDescriptor;
import io.aeron.protocol.DataHeaderFlyweight;
import java.io.File;
import java.util.UUID;
import org.agrona.SystemUtil;
import org.agrona.collections.MutableInteger;
import org.agrona.concurrent.UnsafeBuffer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class MultiDestinationSubscriptionTest {
    private static final String UNICAST_ENDPOINT_A = "localhost:54325";

    private static final String UNICAST_ENDPOINT_B = "localhost:54326";

    private static final String PUB_UNICAST_URI = "aeron:udp?endpoint=localhost:54325";

    private static final String PUB_MULTICAST_URI = "aeron:udp?endpoint=224.20.30.39:54326|interface=localhost";

    private static final String PUB_MDC_URI = "aeron:udp?control=localhost:54325|control-mode=dynamic";

    private static final String SUB_URI = "aeron:udp?control-mode=manual";

    private static final String SUB_MDC_DESTINATION_URI = "aeron:udp?endpoint=localhost:54326|control=localhost:54325";

    private static final int STREAM_ID = 1;

    private static final int TERM_BUFFER_LENGTH = LogBufferDescriptor.TERM_MIN_LENGTH;

    private static final int NUM_MESSAGES_PER_TERM = 64;

    private static final int MESSAGE_LENGTH = ((MultiDestinationSubscriptionTest.TERM_BUFFER_LENGTH) / (MultiDestinationSubscriptionTest.NUM_MESSAGES_PER_TERM)) - (DataHeaderFlyweight.HEADER_LENGTH);

    private static final String ROOT_DIR = (((SystemUtil.tmpDirName()) + "aeron-system-tests-") + (UUID.randomUUID().toString())) + (File.separator);

    private final Context driverContextA = new MediaDriver.Context();

    private Aeron clientA;

    private MediaDriver driverA;

    private Publication publicationA;

    private Publication publicationB;

    private Subscription subscription;

    private final UnsafeBuffer buffer = new UnsafeBuffer(new byte[MultiDestinationSubscriptionTest.MESSAGE_LENGTH]);

    private final FragmentHandler fragmentHandler = Mockito.mock(FragmentHandler.class);

    @Test(timeout = 10000)
    public void shouldSpinUpAndShutdownWithUnicast() {
        launch();
        subscription = clientA.addSubscription(MultiDestinationSubscriptionTest.SUB_URI, MultiDestinationSubscriptionTest.STREAM_ID);
        subscription.addDestination(MultiDestinationSubscriptionTest.PUB_UNICAST_URI);
        publicationA = clientA.addPublication(MultiDestinationSubscriptionTest.PUB_UNICAST_URI, MultiDestinationSubscriptionTest.STREAM_ID);
        while (subscription.hasNoImages()) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
    }

    @Test(timeout = 10000)
    public void shouldSpinUpAndShutdownWithMulticast() {
        launch();
        subscription = clientA.addSubscription(MultiDestinationSubscriptionTest.SUB_URI, MultiDestinationSubscriptionTest.STREAM_ID);
        subscription.addDestination(MultiDestinationSubscriptionTest.PUB_MULTICAST_URI);
        publicationA = clientA.addPublication(MultiDestinationSubscriptionTest.PUB_MULTICAST_URI, MultiDestinationSubscriptionTest.STREAM_ID);
        while (subscription.hasNoImages()) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
    }

    @Test(timeout = 10000)
    public void shouldSpinUpAndShutdownWithDynamicMdc() {
        launch();
        subscription = clientA.addSubscription(MultiDestinationSubscriptionTest.SUB_URI, MultiDestinationSubscriptionTest.STREAM_ID);
        subscription.addDestination(MultiDestinationSubscriptionTest.SUB_MDC_DESTINATION_URI);
        publicationA = clientA.addPublication(MultiDestinationSubscriptionTest.PUB_MDC_URI, MultiDestinationSubscriptionTest.STREAM_ID);
        while (subscription.hasNoImages()) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
    }

    @Test(timeout = 10000)
    public void shouldSendToSingleDestinationSubscriptionWithUnicast() {
        final int numMessagesToSend = (MultiDestinationSubscriptionTest.NUM_MESSAGES_PER_TERM) * 3;
        launch();
        subscription = clientA.addSubscription(MultiDestinationSubscriptionTest.SUB_URI, MultiDestinationSubscriptionTest.STREAM_ID);
        subscription.addDestination(MultiDestinationSubscriptionTest.PUB_UNICAST_URI);
        publicationA = clientA.addPublication(MultiDestinationSubscriptionTest.PUB_UNICAST_URI, MultiDestinationSubscriptionTest.STREAM_ID);
        while (subscription.hasNoImages()) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
        for (int i = 0; i < numMessagesToSend; i++) {
            while ((publicationA.offer(buffer, 0, buffer.capacity())) < 0L) {
                SystemTest.checkInterruptedStatus();
                Thread.yield();
            } 
            final MutableInteger fragmentsRead = new MutableInteger();
            pollForFragment(subscription, fragmentHandler, fragmentsRead);
        }
        verifyFragments(fragmentHandler, numMessagesToSend);
    }

    @Test(timeout = 10000)
    public void shouldSendToSingleDestinationSubscriptionWithMulticast() {
        final int numMessagesToSend = (MultiDestinationSubscriptionTest.NUM_MESSAGES_PER_TERM) * 3;
        launch();
        subscription = clientA.addSubscription(MultiDestinationSubscriptionTest.SUB_URI, MultiDestinationSubscriptionTest.STREAM_ID);
        subscription.addDestination(MultiDestinationSubscriptionTest.PUB_MULTICAST_URI);
        publicationA = clientA.addPublication(MultiDestinationSubscriptionTest.PUB_MULTICAST_URI, MultiDestinationSubscriptionTest.STREAM_ID);
        while (subscription.hasNoImages()) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
        for (int i = 0; i < numMessagesToSend; i++) {
            while ((publicationA.offer(buffer, 0, buffer.capacity())) < 0L) {
                SystemTest.checkInterruptedStatus();
                Thread.yield();
            } 
            final MutableInteger fragmentsRead = new MutableInteger();
            pollForFragment(subscription, fragmentHandler, fragmentsRead);
        }
        verifyFragments(fragmentHandler, numMessagesToSend);
    }

    @Test(timeout = 10000)
    public void shouldSendToSingleDestinationSubscriptionWithDynamicMdc() {
        final int numMessagesToSend = (MultiDestinationSubscriptionTest.NUM_MESSAGES_PER_TERM) * 3;
        launch();
        subscription = clientA.addSubscription(MultiDestinationSubscriptionTest.SUB_URI, MultiDestinationSubscriptionTest.STREAM_ID);
        subscription.addDestination(MultiDestinationSubscriptionTest.SUB_MDC_DESTINATION_URI);
        publicationA = clientA.addPublication(MultiDestinationSubscriptionTest.PUB_MDC_URI, MultiDestinationSubscriptionTest.STREAM_ID);
        while (subscription.hasNoImages()) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
        for (int i = 0; i < numMessagesToSend; i++) {
            while ((publicationA.offer(buffer, 0, buffer.capacity())) < 0L) {
                SystemTest.checkInterruptedStatus();
                Thread.yield();
            } 
            final MutableInteger fragmentsRead = new MutableInteger();
            pollForFragment(subscription, fragmentHandler, fragmentsRead);
        }
        verifyFragments(fragmentHandler, numMessagesToSend);
    }

    @Test(timeout = 10000)
    public void shouldSendToMultipleDestinationSubscriptionWithSameStream() {
        final int numMessagesToSend = (MultiDestinationSubscriptionTest.NUM_MESSAGES_PER_TERM) * 3;
        final int numMessagesToSendForA = numMessagesToSend / 2;
        final int numMessagesToSendForB = numMessagesToSend / 2;
        final String tags = "1,2";
        final int pubTag = 2;
        launch();
        final ChannelUriStringBuilder builder = new ChannelUriStringBuilder();
        builder.clear().tags(tags).media(UDP_MEDIA).endpoint(MultiDestinationSubscriptionTest.UNICAST_ENDPOINT_A);
        final String publicationChannelA = builder.build();
        subscription = clientA.addSubscription(MultiDestinationSubscriptionTest.SUB_URI, MultiDestinationSubscriptionTest.STREAM_ID);
        subscription.addDestination(publicationChannelA);
        publicationA = clientA.addPublication(publicationChannelA, MultiDestinationSubscriptionTest.STREAM_ID);
        while (subscription.hasNoImages()) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
        for (int i = 0; i < numMessagesToSendForA; i++) {
            while ((publicationA.offer(buffer, 0, buffer.capacity())) < 0L) {
                SystemTest.checkInterruptedStatus();
                Thread.yield();
            } 
            final MutableInteger fragmentsRead = new MutableInteger();
            pollForFragment(subscription, fragmentHandler, fragmentsRead);
        }
        final long position = publicationA.position();
        final int initialTermId = publicationA.initialTermId();
        final int positionBitsToShift = Long.numberOfTrailingZeros(publicationA.termBufferLength());
        final int termId = LogBufferDescriptor.computeTermIdFromPosition(position, positionBitsToShift, initialTermId);
        final int termOffset = ((int) (position & ((publicationA.termBufferLength()) - 1)));
        builder.clear().media(UDP_MEDIA).isSessionIdTagged(true).sessionId(pubTag).initialTermId(initialTermId).termId(termId).termOffset(termOffset).endpoint(MultiDestinationSubscriptionTest.UNICAST_ENDPOINT_B);
        final String publicationChannelB = builder.build();
        publicationB = clientA.addExclusivePublication(publicationChannelB, MultiDestinationSubscriptionTest.STREAM_ID);
        builder.clear().media(UDP_MEDIA).endpoint(MultiDestinationSubscriptionTest.UNICAST_ENDPOINT_B);
        final String destinationChannel = builder.build();
        subscription.addDestination(destinationChannel);
        for (int i = 0; i < numMessagesToSendForB; i++) {
            while ((publicationB.offer(buffer, 0, buffer.capacity())) < 0L) {
                SystemTest.checkInterruptedStatus();
                Thread.yield();
            } 
            final MutableInteger fragmentsRead = new MutableInteger();
            pollForFragment(subscription, fragmentHandler, fragmentsRead);
        }
        Assert.assertThat(subscription.imageCount(), CoreMatchers.is(1));
        verifyFragments(fragmentHandler, numMessagesToSend);
    }
}

