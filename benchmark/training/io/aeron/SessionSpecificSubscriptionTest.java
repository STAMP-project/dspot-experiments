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


import LogBufferDescriptor.TERM_MIN_LENGTH;
import ThreadingMode.SHARED;
import io.aeron.driver.MediaDriver;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.protocol.DataHeaderFlyweight;
import java.nio.ByteBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class SessionSpecificSubscriptionTest {
    private static final String ENDPOINT = "localhost:54325";

    private static final int SESSION_ID_1 = 1077;

    private static final int SESSION_ID_2 = 1078;

    private static final int STREAM_ID = 7;

    private static final int FRAGMENT_COUNT_LIMIT = 10;

    private static final int MESSAGE_LENGTH = 1024 - (DataHeaderFlyweight.HEADER_LENGTH);

    private static final int EXPECTED_NUMBER_OF_MESSAGES = 10;

    private final FragmentHandler mockFragmentHandler = Mockito.mock(FragmentHandler.class);

    private final UnsafeBuffer srcBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(SessionSpecificSubscriptionTest.MESSAGE_LENGTH));

    private final String channelUriWithoutSessionId = new ChannelUriStringBuilder().endpoint(SessionSpecificSubscriptionTest.ENDPOINT).media(CommonContext.UDP_MEDIA).build();

    private final String channelUriWithSessionIdOne = new ChannelUriStringBuilder().endpoint(SessionSpecificSubscriptionTest.ENDPOINT).media(CommonContext.UDP_MEDIA).sessionId(SessionSpecificSubscriptionTest.SESSION_ID_1).build();

    private final String channelUriWithSessionIdTwo = new ChannelUriStringBuilder().endpoint(SessionSpecificSubscriptionTest.ENDPOINT).media(CommonContext.UDP_MEDIA).sessionId(SessionSpecificSubscriptionTest.SESSION_ID_2).build();

    private final FragmentHandler handlerSessionIdOne = ( buffer, offset, length, header) -> assertThat(header.sessionId(), is(SESSION_ID_1));

    private final FragmentHandler handlerSessionIdTwo = ( buffer, offset, length, header) -> assertThat(header.sessionId(), is(SESSION_ID_2));

    private final MediaDriver driver = MediaDriver.launch(new MediaDriver.Context().errorHandler(Throwable::printStackTrace).publicationTermBufferLength(TERM_MIN_LENGTH).threadingMode(SHARED));

    private final Aeron aeron = Aeron.connect();

    @Test(timeout = 10000)
    public void shouldSubscribeToSpecificSessionIdsAndWildcard() {
        try (Subscription subscriptionOne = aeron.addSubscription(channelUriWithSessionIdOne, SessionSpecificSubscriptionTest.STREAM_ID);Subscription subscriptionTwo = aeron.addSubscription(channelUriWithSessionIdTwo, SessionSpecificSubscriptionTest.STREAM_ID);Subscription subscriptionWildcard = aeron.addSubscription(channelUriWithoutSessionId, SessionSpecificSubscriptionTest.STREAM_ID);Publication publicationOne = aeron.addExclusivePublication(channelUriWithSessionIdOne, SessionSpecificSubscriptionTest.STREAM_ID);Publication publicationTwo = aeron.addExclusivePublication(channelUriWithSessionIdTwo, SessionSpecificSubscriptionTest.STREAM_ID)) {
            while ((((subscriptionOne.imageCount()) != 1) || ((subscriptionTwo.imageCount()) != 1)) || ((subscriptionWildcard.imageCount()) != 2)) {
                SystemTest.checkInterruptedStatus();
                Thread.yield();
            } 
            for (int i = 0; i < (SessionSpecificSubscriptionTest.EXPECTED_NUMBER_OF_MESSAGES); i++) {
                SessionSpecificSubscriptionTest.publishMessage(srcBuffer, publicationOne);
                SessionSpecificSubscriptionTest.publishMessage(srcBuffer, publicationTwo);
            }
            int numFragments = 0;
            do {
                SystemTest.checkInterruptedStatus();
                numFragments += subscriptionOne.poll(handlerSessionIdOne, SessionSpecificSubscriptionTest.FRAGMENT_COUNT_LIMIT);
            } while (numFragments < (SessionSpecificSubscriptionTest.EXPECTED_NUMBER_OF_MESSAGES) );
            numFragments = 0;
            do {
                SystemTest.checkInterruptedStatus();
                numFragments += subscriptionTwo.poll(handlerSessionIdTwo, SessionSpecificSubscriptionTest.FRAGMENT_COUNT_LIMIT);
            } while (numFragments < (SessionSpecificSubscriptionTest.EXPECTED_NUMBER_OF_MESSAGES) );
            numFragments = 0;
            do {
                SystemTest.checkInterruptedStatus();
                numFragments += subscriptionWildcard.poll(mockFragmentHandler, SessionSpecificSubscriptionTest.FRAGMENT_COUNT_LIMIT);
            } while (numFragments < ((SessionSpecificSubscriptionTest.EXPECTED_NUMBER_OF_MESSAGES) * 2) );
        }
    }

    @Test
    public void shouldNotSubscribeWithoutSpecificSession() {
        try (Subscription subscription = aeron.addSubscription(channelUriWithSessionIdOne, SessionSpecificSubscriptionTest.STREAM_ID);Publication publication = aeron.addExclusivePublication(channelUriWithSessionIdOne, SessionSpecificSubscriptionTest.STREAM_ID);Publication publicationWildcard = aeron.addExclusivePublication(channelUriWithoutSessionId, SessionSpecificSubscriptionTest.STREAM_ID);Publication publicationWrongSession = aeron.addExclusivePublication(channelUriWithSessionIdTwo, SessionSpecificSubscriptionTest.STREAM_ID)) {
            while (!(publication.isConnected())) {
                SystemTest.checkInterruptedStatus();
                Thread.yield();
            } 
            MatcherAssert.assertThat(subscription.imageCount(), Matchers.is(1));
            for (int i = 0; i < (SessionSpecificSubscriptionTest.EXPECTED_NUMBER_OF_MESSAGES); i++) {
                SessionSpecificSubscriptionTest.publishMessage(srcBuffer, publication);
            }
            int numFragments = 0;
            do {
                SystemTest.checkInterruptedStatus();
                numFragments += subscription.poll(handlerSessionIdOne, SessionSpecificSubscriptionTest.FRAGMENT_COUNT_LIMIT);
            } while (numFragments < (SessionSpecificSubscriptionTest.EXPECTED_NUMBER_OF_MESSAGES) );
            Assert.assertFalse(publicationWildcard.isConnected());
            Assert.assertFalse(publicationWrongSession.isConnected());
        }
    }
}

