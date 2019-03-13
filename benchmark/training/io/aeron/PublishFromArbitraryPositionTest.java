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


import FrameDescriptor.FRAME_ALIGNMENT;
import ThreadingMode.SHARED;
import io.aeron.driver.MediaDriver;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.protocol.DataHeaderFlyweight;
import java.nio.ByteBuffer;
import java.util.Random;
import org.agrona.BitUtil;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.mockito.Mockito;


public class PublishFromArbitraryPositionTest {
    @Rule
    public TestWatcher testWatcher = new TestWatcher() {
        protected void failed(final Throwable t, final Description description) {
            System.err.println((((PublishFromArbitraryPositionTest.class.getName()) + " failed with random seed: ") + (seed)));
        }
    };

    private static final int STREAM_ID = 7;

    private static final int FRAGMENT_COUNT_LIMIT = 10;

    private static final int MAX_MESSAGE_LENGTH = 1024 - (DataHeaderFlyweight.HEADER_LENGTH);

    private final FragmentHandler mockFragmentHandler = Mockito.mock(FragmentHandler.class);

    private final UnsafeBuffer srcBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(PublishFromArbitraryPositionTest.MAX_MESSAGE_LENGTH));

    private long seed;

    private final MediaDriver driver = MediaDriver.launch(new MediaDriver.Context().errorHandler(Throwable::printStackTrace).threadingMode(SHARED));

    private final Aeron aeron = Aeron.connect();

    @Test(timeout = 10000)
    public void shouldPublishFromArbitraryJoinPosition() throws Exception {
        final Random rnd = new Random();
        seed = System.nanoTime();
        rnd.setSeed(seed);
        final int termLength = 1 << (16 + (rnd.nextInt(10)));// 64k to 64M

        final int mtu = 1 << (10 + (rnd.nextInt(3)));// 1024 to 8096

        final int initialTermId = rnd.nextInt(1234);
        final int termOffset = BitUtil.align(rnd.nextInt(termLength), FRAME_ALIGNMENT);
        final int termId = initialTermId + (rnd.nextInt(1000));
        final String channelUri = new ChannelUriStringBuilder().endpoint("localhost:54325").termLength(termLength).initialTermId(initialTermId).termId(termId).termOffset(termOffset).mtu(mtu).media("udp").build();
        final int expectedNumberOfFragments = 10 + (rnd.nextInt(10000));
        try (Subscription subscription = aeron.addSubscription(channelUri, PublishFromArbitraryPositionTest.STREAM_ID);ExclusivePublication publication = aeron.addExclusivePublication(channelUri, PublishFromArbitraryPositionTest.STREAM_ID)) {
            while (!(publication.isConnected())) {
                SystemTest.checkInterruptedStatus();
                Thread.yield();
            } 
            final Thread t = new Thread(() -> {
                int totalFragmentsRead = 0;
                do {
                    int fragmentsRead = subscription.poll(mockFragmentHandler, PublishFromArbitraryPositionTest.FRAGMENT_COUNT_LIMIT);
                    while (0 == fragmentsRead) {
                        Thread.yield();
                        fragmentsRead = subscription.poll(mockFragmentHandler, PublishFromArbitraryPositionTest.FRAGMENT_COUNT_LIMIT);
                    } 
                    totalFragmentsRead += fragmentsRead;
                } while (totalFragmentsRead < expectedNumberOfFragments );
                Assert.assertEquals(expectedNumberOfFragments, totalFragmentsRead);
            });
            t.setDaemon(true);
            t.setName("image-consumer");
            t.start();
            for (int i = 0; i < expectedNumberOfFragments; i++) {
                PublishFromArbitraryPositionTest.publishMessage(srcBuffer, publication, rnd);
            }
            t.join();
        }
    }
}

