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


import ThreadingMode.SHARED;
import io.aeron.driver.MediaDriver;
import io.aeron.logbuffer.FragmentHandler;
import org.agrona.concurrent.UnsafeBuffer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class MultiSubscriberTest {
    private static final String CHANNEL_1 = "aeron:udp?endpoint=localhost:54325|fruit=banana";

    private static final String CHANNEL_2 = "aeron:udp?endpoint=localhost:54325|fruit=apple";

    private static final int STREAM_ID = 1;

    private static final int FRAGMENT_COUNT_LIMIT = 10;

    private final MediaDriver driver = MediaDriver.launch(new MediaDriver.Context().errorHandler(Throwable::printStackTrace).threadingMode(SHARED));

    private final Aeron aeron = Aeron.connect();

    @Test(timeout = 10000)
    public void shouldReceiveMessageOnSeparateSubscriptions() {
        final FragmentHandler mockFragmentHandlerOne = Mockito.mock(FragmentHandler.class);
        final FragmentHandler mockFragmentHandlerTwo = Mockito.mock(FragmentHandler.class);
        final FragmentAssembler adapterOne = new FragmentAssembler(mockFragmentHandlerOne);
        final FragmentAssembler adapterTwo = new FragmentAssembler(mockFragmentHandlerTwo);
        try (Subscription subscriptionOne = aeron.addSubscription(MultiSubscriberTest.CHANNEL_1, MultiSubscriberTest.STREAM_ID);Subscription subscriptionTwo = aeron.addSubscription(MultiSubscriberTest.CHANNEL_2, MultiSubscriberTest.STREAM_ID);Publication publication = aeron.addPublication(MultiSubscriberTest.CHANNEL_1, MultiSubscriberTest.STREAM_ID)) {
            final byte[] expectedBytes = "Hello, World! here is a small message".getBytes();
            final UnsafeBuffer srcBuffer = new UnsafeBuffer(expectedBytes);
            Assert.assertThat(subscriptionOne.poll(adapterOne, MultiSubscriberTest.FRAGMENT_COUNT_LIMIT), CoreMatchers.is(0));
            Assert.assertThat(subscriptionTwo.poll(adapterTwo, MultiSubscriberTest.FRAGMENT_COUNT_LIMIT), CoreMatchers.is(0));
            while ((!(subscriptionOne.isConnected())) || (!(subscriptionTwo.isConnected()))) {
                SystemTest.checkInterruptedStatus();
                Thread.yield();
            } 
            while ((publication.offer(srcBuffer)) < 0L) {
                SystemTest.checkInterruptedStatus();
                Thread.yield();
            } 
            while ((subscriptionOne.poll(adapterOne, MultiSubscriberTest.FRAGMENT_COUNT_LIMIT)) == 0) {
                SystemTest.checkInterruptedStatus();
                Thread.yield();
            } 
            while ((subscriptionTwo.poll(adapterTwo, MultiSubscriberTest.FRAGMENT_COUNT_LIMIT)) == 0) {
                SystemTest.checkInterruptedStatus();
                Thread.yield();
            } 
            verifyData(srcBuffer, mockFragmentHandlerOne);
            verifyData(srcBuffer, mockFragmentHandlerTwo);
        }
    }
}

