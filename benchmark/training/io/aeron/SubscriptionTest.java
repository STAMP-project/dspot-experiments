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


import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.FrameDescriptor;
import io.aeron.logbuffer.Header;
import io.aeron.protocol.DataHeaderFlyweight;
import java.nio.ByteBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class SubscriptionTest {
    private static final String CHANNEL = "aeron:udp?endpoint=localhost:40124";

    private static final int STREAM_ID_1 = 2;

    private static final long SUBSCRIPTION_CORRELATION_ID = 100;

    private static final int READ_BUFFER_CAPACITY = 1024;

    private static final byte FLAGS = FrameDescriptor.UNFRAGMENTED;

    private static final int FRAGMENT_COUNT_LIMIT = Integer.MAX_VALUE;

    private static final int HEADER_LENGTH = DataHeaderFlyweight.HEADER_LENGTH;

    private final UnsafeBuffer atomicReadBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(SubscriptionTest.READ_BUFFER_CAPACITY));

    private final ClientConductor conductor = Mockito.mock(ClientConductor.class);

    private final FragmentHandler fragmentHandler = Mockito.mock(FragmentHandler.class);

    private final Image imageOneMock = Mockito.mock(Image.class);

    private final Header header = Mockito.mock(Header.class);

    private final Image imageTwoMock = Mockito.mock(Image.class);

    private final AvailableImageHandler availableImageHandlerMock = Mockito.mock(AvailableImageHandler.class);

    private final UnavailableImageHandler unavailableImageHandlerMock = Mockito.mock(UnavailableImageHandler.class);

    private Subscription subscription;

    @Test
    public void shouldEnsureTheSubscriptionIsOpenWhenPolling() {
        subscription.close();
        Assert.assertTrue(subscription.isClosed());
        Mockito.verify(conductor).releaseSubscription(subscription);
    }

    @Test
    public void shouldReadNothingWhenNoImages() {
        Assert.assertThat(subscription.poll(fragmentHandler, 1), Matchers.is(0));
    }

    @Test
    public void shouldReadNothingWhenThereIsNoData() {
        subscription.addImage(imageOneMock);
        Assert.assertThat(subscription.poll(fragmentHandler, 1), Matchers.is(0));
    }

    @Test
    public void shouldReadData() {
        subscription.addImage(imageOneMock);
        Mockito.when(imageOneMock.poll(ArgumentMatchers.any(FragmentHandler.class), ArgumentMatchers.anyInt())).then(( invocation) -> {
            final FragmentHandler handler = ((FragmentHandler) (invocation.getArguments()[0]));
            handler.onFragment(atomicReadBuffer, HEADER_LENGTH, ((READ_BUFFER_CAPACITY) - (HEADER_LENGTH)), header);
            return 1;
        });
        Assert.assertThat(subscription.poll(fragmentHandler, SubscriptionTest.FRAGMENT_COUNT_LIMIT), Matchers.is(1));
        Mockito.verify(fragmentHandler).onFragment(ArgumentMatchers.eq(atomicReadBuffer), ArgumentMatchers.eq(SubscriptionTest.HEADER_LENGTH), ArgumentMatchers.eq(((SubscriptionTest.READ_BUFFER_CAPACITY) - (SubscriptionTest.HEADER_LENGTH))), ArgumentMatchers.any(Header.class));
    }

    @Test
    public void shouldReadDataFromMultipleSources() {
        subscription.addImage(imageOneMock);
        subscription.addImage(imageTwoMock);
        Mockito.when(imageOneMock.poll(ArgumentMatchers.any(FragmentHandler.class), ArgumentMatchers.anyInt())).then(( invocation) -> {
            final FragmentHandler handler = ((FragmentHandler) (invocation.getArguments()[0]));
            handler.onFragment(atomicReadBuffer, HEADER_LENGTH, ((READ_BUFFER_CAPACITY) - (HEADER_LENGTH)), header);
            return 1;
        });
        Mockito.when(imageTwoMock.poll(ArgumentMatchers.any(FragmentHandler.class), ArgumentMatchers.anyInt())).then(( invocation) -> {
            final FragmentHandler handler = ((FragmentHandler) (invocation.getArguments()[0]));
            handler.onFragment(atomicReadBuffer, HEADER_LENGTH, ((READ_BUFFER_CAPACITY) - (HEADER_LENGTH)), header);
            return 1;
        });
        Assert.assertThat(subscription.poll(fragmentHandler, SubscriptionTest.FRAGMENT_COUNT_LIMIT), Matchers.is(2));
    }
}

