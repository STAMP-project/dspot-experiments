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


import BitUtil.SIZE_OF_INT;
import io.aeron.driver.MediaDriver;
import io.aeron.logbuffer.FragmentHandler;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.IntConsumer;
import org.agrona.ExpandableArrayBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.collections.MutableInteger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test that a second subscriber can be stopped and started again while data is being published.
 */
public class StopStartSecondSubscriberTest {
    public static final String CHANNEL1 = "aeron:udp?endpoint=localhost:54325";

    public static final String CHANNEL2 = "aeron:udp?endpoint=localhost:54326";

    private static final int STREAM_ID1 = 1;

    private static final int STREAM_ID2 = 2;

    private MediaDriver driverOne;

    private MediaDriver driverTwo;

    private Aeron publisherOne;

    private Aeron subscriberOne;

    private Aeron publisherTwo;

    private Aeron subscriberTwo;

    private Subscription subscriptionOne;

    private Publication publicationOne;

    private Subscription subscriptionTwo;

    private Publication publicationTwo;

    private final MutableDirectBuffer buffer = new ExpandableArrayBuffer();

    private final MutableInteger subOneCount = new MutableInteger();

    private final FragmentHandler fragmentHandlerOne = ( buffer, offset, length, header) -> subOneCount.value++;

    private final MutableInteger subTwoCount = new MutableInteger();

    private final FragmentHandler fragmentHandlerTwo = ( buffer, offset, length, header) -> subTwoCount.value++;

    @Test(timeout = 10000)
    public void shouldSpinUpAndShutdown() {
        launch(StopStartSecondSubscriberTest.CHANNEL1, StopStartSecondSubscriberTest.STREAM_ID1, StopStartSecondSubscriberTest.CHANNEL2, StopStartSecondSubscriberTest.STREAM_ID2);
    }

    @Test(timeout = 10000)
    public void shouldReceivePublishedMessage() {
        launch(StopStartSecondSubscriberTest.CHANNEL1, StopStartSecondSubscriberTest.STREAM_ID1, StopStartSecondSubscriberTest.CHANNEL2, StopStartSecondSubscriberTest.STREAM_ID2);
        buffer.putInt(0, 1);
        final int messagesPerPublication = 1;
        while ((publicationOne.offer(buffer, 0, SIZE_OF_INT)) < 0L) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
        while ((publicationTwo.offer(buffer, 0, SIZE_OF_INT)) < 0L) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
        final MutableInteger fragmentsRead1 = new MutableInteger();
        final MutableInteger fragmentsRead2 = new MutableInteger();
        SystemTest.executeUntil(() -> ((fragmentsRead1.get()) >= messagesPerPublication) && ((fragmentsRead2.get()) >= messagesPerPublication), ( i) -> {
            fragmentsRead1.value += subscriptionOne.poll(fragmentHandlerOne, 10);
            fragmentsRead2.value += subscriptionTwo.poll(fragmentHandlerTwo, 10);
            Thread.yield();
        }, Integer.MAX_VALUE, TimeUnit.MILLISECONDS.toNanos(9900));
        Assert.assertEquals(messagesPerPublication, subOneCount.get());
        Assert.assertEquals(messagesPerPublication, subTwoCount.get());
    }

    @Test(timeout = 10000)
    public void shouldReceiveMessagesAfterStopStartOnSameChannelSameStream() {
        shouldReceiveMessagesAfterStopStart(StopStartSecondSubscriberTest.CHANNEL1, StopStartSecondSubscriberTest.STREAM_ID1, StopStartSecondSubscriberTest.CHANNEL1, StopStartSecondSubscriberTest.STREAM_ID1);
    }

    @Test(timeout = 10000)
    public void shouldReceiveMessagesAfterStopStartOnSameChannelDifferentStreams() {
        shouldReceiveMessagesAfterStopStart(StopStartSecondSubscriberTest.CHANNEL1, StopStartSecondSubscriberTest.STREAM_ID1, StopStartSecondSubscriberTest.CHANNEL1, StopStartSecondSubscriberTest.STREAM_ID2);
    }

    @Test(timeout = 10000)
    public void shouldReceiveMessagesAfterStopStartOnDifferentChannelsSameStream() {
        shouldReceiveMessagesAfterStopStart(StopStartSecondSubscriberTest.CHANNEL1, StopStartSecondSubscriberTest.STREAM_ID1, StopStartSecondSubscriberTest.CHANNEL2, StopStartSecondSubscriberTest.STREAM_ID1);
    }

    @Test(timeout = 10000)
    public void shouldReceiveMessagesAfterStopStartOnDifferentChannelsDifferentStreams() {
        shouldReceiveMessagesAfterStopStart(StopStartSecondSubscriberTest.CHANNEL1, StopStartSecondSubscriberTest.STREAM_ID1, StopStartSecondSubscriberTest.CHANNEL2, StopStartSecondSubscriberTest.STREAM_ID2);
    }
}

