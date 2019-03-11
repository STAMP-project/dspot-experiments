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


import ChannelEndpointStatus.ACTIVE;
import ChannelEndpointStatus.ERRORED;
import io.aeron.driver.MediaDriver;
import io.aeron.driver.ThreadingMode;
import io.aeron.exceptions.ChannelEndpointException;
import io.aeron.logbuffer.LogBufferDescriptor;
import io.aeron.protocol.DataHeaderFlyweight;
import io.aeron.status.ChannelEndpointStatus;
import java.io.File;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.agrona.ErrorHandler;
import org.agrona.IoUtil;
import org.agrona.concurrent.UnsafeBuffer;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ChannelEndpointStatusTest {
    private static final String URI = "aeron:udp?endpoint=localhost:54326";

    private static final String URI_NO_CONFLICT = "aeron:udp?endpoint=localhost:54327";

    private static final String URI_WITH_INTERFACE_PORT = "aeron:udp?endpoint=localhost:54326|interface=localhost:34567";

    private static final int STREAM_ID = 1;

    private static final ThreadingMode THREADING_MODE = ThreadingMode.DEDICATED;

    private static final int TERM_BUFFER_LENGTH = LogBufferDescriptor.TERM_MIN_LENGTH;

    private static final int NUM_MESSAGES_PER_TERM = 64;

    private static final int MESSAGE_LENGTH = ((ChannelEndpointStatusTest.TERM_BUFFER_LENGTH) / (ChannelEndpointStatusTest.NUM_MESSAGES_PER_TERM)) - (DataHeaderFlyweight.HEADER_LENGTH);

    private static final String ROOT_DIR = (((IoUtil.tmpDirName()) + "aeron-system-tests-") + (UUID.randomUUID().toString())) + (File.separator);

    private Aeron clientA;

    private Aeron clientB;

    private Aeron clientC;

    private MediaDriver driverA;

    private MediaDriver driverB;

    private final UnsafeBuffer buffer = new UnsafeBuffer(new byte[ChannelEndpointStatusTest.MESSAGE_LENGTH]);

    private final ErrorHandler errorHandlerClientA = Mockito.mock(ErrorHandler.class);

    private final ErrorHandler errorHandlerClientB = Mockito.mock(ErrorHandler.class);

    private final ErrorHandler errorHandlerClientC = Mockito.mock(ErrorHandler.class);

    private final AtomicInteger errorCounter = new AtomicInteger();

    private final ErrorHandler countingErrorHandler = ( ex) -> errorCounter.getAndIncrement();

    @Test(timeout = 5000)
    public void shouldBeAbleToQueryChannelStatusForSubscription() {
        final Subscription subscription = clientA.addSubscription(ChannelEndpointStatusTest.URI, ChannelEndpointStatusTest.STREAM_ID);
        while ((subscription.channelStatus()) == (ChannelEndpointStatus.INITIALIZING)) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
        Assert.assertThat(subscription.channelStatus(), CoreMatchers.is(ACTIVE));
    }

    @Test(timeout = 5000)
    public void shouldBeAbleToQueryChannelStatusForPublication() {
        final Publication publication = clientA.addPublication(ChannelEndpointStatusTest.URI, ChannelEndpointStatusTest.STREAM_ID);
        while ((publication.channelStatus()) == (ChannelEndpointStatus.INITIALIZING)) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
        Assert.assertThat(publication.channelStatus(), CoreMatchers.is(ACTIVE));
    }

    @Test
    public void shouldCatchErrorOnAddressAlreadyInUseForSubscriptions() {
        final Subscription subscriptionA = clientA.addSubscription(ChannelEndpointStatusTest.URI, ChannelEndpointStatusTest.STREAM_ID);
        while ((subscriptionA.channelStatus()) == (ChannelEndpointStatus.INITIALIZING)) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
        Assert.assertThat(subscriptionA.channelStatus(), CoreMatchers.is(ACTIVE));
        final Subscription subscriptionB = clientB.addSubscription(ChannelEndpointStatusTest.URI, ChannelEndpointStatusTest.STREAM_ID);
        final ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
        Mockito.verify(errorHandlerClientB, Mockito.timeout(5000)).onError(captor.capture());
        MatcherAssert.assertThat(captor.getValue(), CoreMatchers.instanceOf(ChannelEndpointException.class));
        final ChannelEndpointException channelEndpointException = ((ChannelEndpointException) (captor.getValue()));
        final long status = clientB.countersReader().getCounterValue(channelEndpointException.statusIndicatorId());
        Assert.assertThat(status, CoreMatchers.is(ERRORED));
        Assert.assertThat(errorCounter.get(), Matchers.greaterThan(0));
        Assert.assertThat(subscriptionB.channelStatusId(), CoreMatchers.is(channelEndpointException.statusIndicatorId()));
        Assert.assertThat(subscriptionA.channelStatus(), CoreMatchers.is(ACTIVE));
    }

    @Test
    public void shouldCatchErrorOnAddressAlreadyInUseForPublications() {
        final Publication publicationA = clientA.addPublication(ChannelEndpointStatusTest.URI_WITH_INTERFACE_PORT, ChannelEndpointStatusTest.STREAM_ID);
        while ((publicationA.channelStatus()) == (ChannelEndpointStatus.INITIALIZING)) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
        Assert.assertThat(publicationA.channelStatus(), CoreMatchers.is(ACTIVE));
        final Publication publicationB = clientB.addPublication(ChannelEndpointStatusTest.URI_WITH_INTERFACE_PORT, ChannelEndpointStatusTest.STREAM_ID);
        final ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
        Mockito.verify(errorHandlerClientB, Mockito.timeout(5000)).onError(captor.capture());
        MatcherAssert.assertThat(captor.getValue(), CoreMatchers.instanceOf(ChannelEndpointException.class));
        final ChannelEndpointException channelEndpointException = ((ChannelEndpointException) (captor.getValue()));
        final long status = clientB.countersReader().getCounterValue(channelEndpointException.statusIndicatorId());
        Assert.assertThat(status, CoreMatchers.is(ERRORED));
        Assert.assertThat(errorCounter.get(), Matchers.greaterThan(0));
        Assert.assertThat(publicationB.channelStatusId(), CoreMatchers.is(channelEndpointException.statusIndicatorId()));
        Assert.assertTrue(publicationB.isClosed());
        Assert.assertThat(publicationA.channelStatus(), CoreMatchers.is(ACTIVE));
    }

    @Test
    public void shouldNotErrorOnAddressAlreadyInUseOnActiveChannelEndpointForSubscriptions() {
        final Subscription subscriptionA = clientA.addSubscription(ChannelEndpointStatusTest.URI, ChannelEndpointStatusTest.STREAM_ID);
        while ((subscriptionA.channelStatus()) == (ChannelEndpointStatus.INITIALIZING)) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
        final Subscription subscriptionB = clientB.addSubscription(ChannelEndpointStatusTest.URI_NO_CONFLICT, ChannelEndpointStatusTest.STREAM_ID);
        final Subscription subscriptionC = clientC.addSubscription(ChannelEndpointStatusTest.URI, ChannelEndpointStatusTest.STREAM_ID);
        while (((subscriptionB.channelStatus()) == (ChannelEndpointStatus.INITIALIZING)) || ((subscriptionC.channelStatus()) == (ChannelEndpointStatus.INITIALIZING))) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
        Mockito.verify(errorHandlerClientC, Mockito.timeout(5000)).onError(ArgumentMatchers.any(ChannelEndpointException.class));
        Assert.assertThat(errorCounter.get(), Matchers.greaterThan(0));
        Assert.assertThat(subscriptionC.channelStatus(), CoreMatchers.is(ERRORED));
        Assert.assertTrue(subscriptionC.isClosed());
        Assert.assertThat(subscriptionA.channelStatus(), CoreMatchers.is(ACTIVE));
        Assert.assertThat(subscriptionB.channelStatus(), CoreMatchers.is(ACTIVE));
    }
}

