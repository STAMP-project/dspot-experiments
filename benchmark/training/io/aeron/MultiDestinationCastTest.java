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
import io.aeron.driver.MediaDriver;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.LogBufferDescriptor;
import io.aeron.protocol.DataHeaderFlyweight;
import java.io.File;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.agrona.IoUtil;
import org.agrona.collections.MutableInteger;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;
import org.mockito.Mockito;

import static CommonContext.SPY_PREFIX;


public class MultiDestinationCastTest {
    private static final String PUB_MDC_DYNAMIC_URI = "aeron:udp?control=localhost:54325|control-mode=dynamic";

    private static final String SUB1_MDC_DYNAMIC_URI = "aeron:udp?endpoint=localhost:54326|control=localhost:54325";

    private static final String SUB2_MDC_DYNAMIC_URI = "aeron:udp?endpoint=localhost:54327|control=localhost:54325";

    private static final String SUB3_MDC_DYNAMIC_URI = (SPY_PREFIX) + (MultiDestinationCastTest.PUB_MDC_DYNAMIC_URI);

    private static final String PUB_MDC_MANUAL_URI = "aeron:udp?control=localhost:54325|control-mode=manual";

    private static final String SUB1_MDC_MANUAL_URI = "aeron:udp?endpoint=localhost:54326";

    private static final String SUB2_MDC_MANUAL_URI = "aeron:udp?endpoint=localhost:54327";

    private static final String SUB3_MDC_MANUAL_URI = (SPY_PREFIX) + (MultiDestinationCastTest.PUB_MDC_MANUAL_URI);

    private static final int STREAM_ID = 1;

    private static final int TERM_BUFFER_LENGTH = LogBufferDescriptor.TERM_MIN_LENGTH;

    private static final int NUM_MESSAGES_PER_TERM = 64;

    private static final int MESSAGE_LENGTH = ((MultiDestinationCastTest.TERM_BUFFER_LENGTH) / (MultiDestinationCastTest.NUM_MESSAGES_PER_TERM)) - (DataHeaderFlyweight.HEADER_LENGTH);

    private static final String ROOT_DIR = (((IoUtil.tmpDirName()) + "aeron-system-tests-") + (UUID.randomUUID().toString())) + (File.separator);

    private final Context driverBContext = new MediaDriver.Context();

    private Aeron clientA;

    private Aeron clientB;

    private MediaDriver driverA;

    private MediaDriver driverB;

    private Publication publication;

    private Subscription subscriptionA;

    private Subscription subscriptionB;

    private Subscription subscriptionC;

    private final UnsafeBuffer buffer = new UnsafeBuffer(new byte[MultiDestinationCastTest.MESSAGE_LENGTH]);

    private final FragmentHandler fragmentHandlerA = Mockito.mock(FragmentHandler.class);

    private final FragmentHandler fragmentHandlerB = Mockito.mock(FragmentHandler.class);

    private final FragmentHandler fragmentHandlerC = Mockito.mock(FragmentHandler.class);

    @Test(timeout = 10000)
    public void shouldSpinUpAndShutdownWithDynamic() {
        launch();
        publication = clientA.addPublication(MultiDestinationCastTest.PUB_MDC_DYNAMIC_URI, MultiDestinationCastTest.STREAM_ID);
        subscriptionA = clientA.addSubscription(MultiDestinationCastTest.SUB1_MDC_DYNAMIC_URI, MultiDestinationCastTest.STREAM_ID);
        subscriptionB = clientB.addSubscription(MultiDestinationCastTest.SUB2_MDC_DYNAMIC_URI, MultiDestinationCastTest.STREAM_ID);
        subscriptionC = clientA.addSubscription(MultiDestinationCastTest.SUB3_MDC_DYNAMIC_URI, MultiDestinationCastTest.STREAM_ID);
        while (((subscriptionA.hasNoImages()) || (subscriptionB.hasNoImages())) || (subscriptionC.hasNoImages())) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
    }

    @Test(timeout = 10000)
    public void shouldSpinUpAndShutdownWithManual() {
        launch();
        subscriptionA = clientA.addSubscription(MultiDestinationCastTest.SUB1_MDC_MANUAL_URI, MultiDestinationCastTest.STREAM_ID);
        subscriptionB = clientB.addSubscription(MultiDestinationCastTest.SUB2_MDC_MANUAL_URI, MultiDestinationCastTest.STREAM_ID);
        subscriptionC = clientA.addSubscription(MultiDestinationCastTest.SUB3_MDC_MANUAL_URI, MultiDestinationCastTest.STREAM_ID);
        publication = clientA.addPublication(MultiDestinationCastTest.PUB_MDC_MANUAL_URI, MultiDestinationCastTest.STREAM_ID);
        publication.addDestination(MultiDestinationCastTest.SUB1_MDC_MANUAL_URI);
        publication.addDestination(MultiDestinationCastTest.SUB2_MDC_MANUAL_URI);
        while (((subscriptionA.hasNoImages()) || (subscriptionB.hasNoImages())) || (subscriptionC.hasNoImages())) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
    }

    @Test(timeout = 10000)
    public void shouldSendToTwoPortsWithDynamic() {
        final int numMessagesToSend = (MultiDestinationCastTest.NUM_MESSAGES_PER_TERM) * 3;
        launch();
        subscriptionA = clientA.addSubscription(MultiDestinationCastTest.SUB1_MDC_DYNAMIC_URI, MultiDestinationCastTest.STREAM_ID);
        subscriptionB = clientB.addSubscription(MultiDestinationCastTest.SUB2_MDC_DYNAMIC_URI, MultiDestinationCastTest.STREAM_ID);
        subscriptionC = clientA.addSubscription(MultiDestinationCastTest.SUB3_MDC_DYNAMIC_URI, MultiDestinationCastTest.STREAM_ID);
        publication = clientA.addPublication(MultiDestinationCastTest.PUB_MDC_DYNAMIC_URI, MultiDestinationCastTest.STREAM_ID);
        while (((subscriptionA.hasNoImages()) || (subscriptionB.hasNoImages())) || (subscriptionC.hasNoImages())) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
        for (int i = 0; i < numMessagesToSend; i++) {
            while ((publication.offer(buffer, 0, buffer.capacity())) < 0L) {
                SystemTest.checkInterruptedStatus();
                Thread.yield();
            } 
            final MutableInteger fragmentsRead = new MutableInteger();
            pollForFragment(subscriptionA, fragmentHandlerA, fragmentsRead);
            fragmentsRead.set(0);
            pollForFragment(subscriptionB, fragmentHandlerB, fragmentsRead);
            fragmentsRead.set(0);
            pollForFragment(subscriptionC, fragmentHandlerC, fragmentsRead);
        }
        verifyFragments(fragmentHandlerA, numMessagesToSend);
        verifyFragments(fragmentHandlerB, numMessagesToSend);
        verifyFragments(fragmentHandlerC, numMessagesToSend);
    }

    @Test(timeout = 10000)
    public void shouldSendToTwoPortsWithDynamicSingleDriver() {
        final int numMessagesToSend = (MultiDestinationCastTest.NUM_MESSAGES_PER_TERM) * 3;
        launch();
        subscriptionA = clientA.addSubscription(MultiDestinationCastTest.SUB1_MDC_DYNAMIC_URI, MultiDestinationCastTest.STREAM_ID);
        subscriptionB = clientA.addSubscription(MultiDestinationCastTest.SUB2_MDC_DYNAMIC_URI, MultiDestinationCastTest.STREAM_ID);
        subscriptionC = clientA.addSubscription(MultiDestinationCastTest.SUB3_MDC_DYNAMIC_URI, MultiDestinationCastTest.STREAM_ID);
        publication = clientA.addPublication(MultiDestinationCastTest.PUB_MDC_DYNAMIC_URI, MultiDestinationCastTest.STREAM_ID);
        while (((!(subscriptionA.isConnected())) || (!(subscriptionB.isConnected()))) || (!(subscriptionC.isConnected()))) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
        for (int i = 0; i < numMessagesToSend; i++) {
            while ((publication.offer(buffer, 0, buffer.capacity())) < 0L) {
                SystemTest.checkInterruptedStatus();
                Thread.yield();
            } 
            final MutableInteger fragmentsRead = new MutableInteger();
            pollForFragment(subscriptionA, fragmentHandlerA, fragmentsRead);
            fragmentsRead.set(0);
            pollForFragment(subscriptionB, fragmentHandlerB, fragmentsRead);
            fragmentsRead.set(0);
            pollForFragment(subscriptionC, fragmentHandlerC, fragmentsRead);
        }
        verifyFragments(fragmentHandlerA, numMessagesToSend);
        verifyFragments(fragmentHandlerB, numMessagesToSend);
        verifyFragments(fragmentHandlerC, numMessagesToSend);
    }

    @Test(timeout = 10000)
    public void shouldSendToTwoPortsWithManualSingleDriver() {
        final int numMessagesToSend = (MultiDestinationCastTest.NUM_MESSAGES_PER_TERM) * 3;
        launch();
        subscriptionA = clientA.addSubscription(MultiDestinationCastTest.SUB1_MDC_MANUAL_URI, MultiDestinationCastTest.STREAM_ID);
        subscriptionB = clientA.addSubscription(MultiDestinationCastTest.SUB2_MDC_MANUAL_URI, MultiDestinationCastTest.STREAM_ID);
        publication = clientA.addPublication(MultiDestinationCastTest.PUB_MDC_MANUAL_URI, MultiDestinationCastTest.STREAM_ID);
        publication.addDestination(MultiDestinationCastTest.SUB1_MDC_MANUAL_URI);
        publication.addDestination(MultiDestinationCastTest.SUB2_MDC_MANUAL_URI);
        while ((!(subscriptionA.isConnected())) || (!(subscriptionB.isConnected()))) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
        for (int i = 0; i < numMessagesToSend; i++) {
            while ((publication.offer(buffer, 0, buffer.capacity())) < 0L) {
                SystemTest.checkInterruptedStatus();
                Thread.yield();
            } 
            final MutableInteger fragmentsRead = new MutableInteger();
            pollForFragment(subscriptionA, fragmentHandlerA, fragmentsRead);
            fragmentsRead.set(0);
            pollForFragment(subscriptionB, fragmentHandlerB, fragmentsRead);
        }
        verifyFragments(fragmentHandlerA, numMessagesToSend);
        verifyFragments(fragmentHandlerB, numMessagesToSend);
    }

    @Test(timeout = 10000)
    public void shouldManuallyRemovePortDuringActiveStream() throws Exception {
        final int numMessagesToSend = (MultiDestinationCastTest.NUM_MESSAGES_PER_TERM) * 3;
        final int numMessageForSub2 = 10;
        final CountDownLatch unavailableImage = new CountDownLatch(1);
        driverBContext.imageLivenessTimeoutNs(TimeUnit.MILLISECONDS.toNanos(500));
        launch();
        subscriptionA = clientA.addSubscription(MultiDestinationCastTest.SUB1_MDC_MANUAL_URI, MultiDestinationCastTest.STREAM_ID);
        subscriptionB = clientB.addSubscription(MultiDestinationCastTest.SUB2_MDC_MANUAL_URI, MultiDestinationCastTest.STREAM_ID, null, ( image) -> unavailableImage.countDown());
        publication = clientA.addPublication(MultiDestinationCastTest.PUB_MDC_MANUAL_URI, MultiDestinationCastTest.STREAM_ID);
        publication.addDestination(MultiDestinationCastTest.SUB1_MDC_MANUAL_URI);
        publication.addDestination(MultiDestinationCastTest.SUB2_MDC_MANUAL_URI);
        while ((!(subscriptionA.isConnected())) || (!(subscriptionB.isConnected()))) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
        for (int i = 0; i < numMessagesToSend; i++) {
            while ((publication.offer(buffer, 0, buffer.capacity())) < 0L) {
                SystemTest.checkInterruptedStatus();
                Thread.yield();
            } 
            final MutableInteger fragmentsRead = new MutableInteger();
            pollForFragment(subscriptionA, fragmentHandlerA, fragmentsRead);
            fragmentsRead.set(0);
            if (i < numMessageForSub2) {
                pollForFragment(subscriptionB, fragmentHandlerB, fragmentsRead);
            } else {
                fragmentsRead.value += subscriptionB.poll(fragmentHandlerB, 10);
                Thread.yield();
            }
            if (i == (numMessageForSub2 - 1)) {
                publication.removeDestination(MultiDestinationCastTest.SUB2_MDC_MANUAL_URI);
            }
        }
        unavailableImage.await();
        verifyFragments(fragmentHandlerA, numMessagesToSend);
        verifyFragments(fragmentHandlerB, numMessageForSub2);
    }

    @Test(timeout = 10000)
    public void shouldManuallyAddPortDuringActiveStream() throws Exception {
        final int numMessagesToSend = (MultiDestinationCastTest.NUM_MESSAGES_PER_TERM) * 3;
        final int numMessageForSub2 = 10;
        final CountDownLatch availableImage = new CountDownLatch(1);
        launch();
        subscriptionA = clientA.addSubscription(MultiDestinationCastTest.SUB1_MDC_MANUAL_URI, MultiDestinationCastTest.STREAM_ID);
        subscriptionB = clientB.addSubscription(MultiDestinationCastTest.SUB2_MDC_MANUAL_URI, MultiDestinationCastTest.STREAM_ID, ( image) -> availableImage.countDown(), null);
        publication = clientA.addPublication(MultiDestinationCastTest.PUB_MDC_MANUAL_URI, MultiDestinationCastTest.STREAM_ID);
        publication.addDestination(MultiDestinationCastTest.SUB1_MDC_MANUAL_URI);
        while (!(subscriptionA.isConnected())) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
        for (int i = 0; i < numMessagesToSend; i++) {
            while ((publication.offer(buffer, 0, buffer.capacity())) < 0L) {
                SystemTest.checkInterruptedStatus();
                Thread.yield();
            } 
            final MutableInteger fragmentsRead = new MutableInteger();
            pollForFragment(subscriptionA, fragmentHandlerA, fragmentsRead);
            fragmentsRead.set(0);
            if (i > (numMessagesToSend - numMessageForSub2)) {
                pollForFragment(subscriptionB, fragmentHandlerB, fragmentsRead);
            } else {
                fragmentsRead.value += subscriptionB.poll(fragmentHandlerB, 10);
                Thread.yield();
            }
            if (i == ((numMessagesToSend - numMessageForSub2) - 1)) {
                publication.addDestination(MultiDestinationCastTest.SUB2_MDC_MANUAL_URI);
                availableImage.await();
            }
        }
        verifyFragments(fragmentHandlerA, numMessagesToSend);
        verifyFragments(fragmentHandlerB, numMessageForSub2);
    }
}

