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
import io.aeron.logbuffer.Header;
import java.nio.ByteBuffer;
import org.agrona.DirectBuffer;
import org.junit.Assert;
import org.junit.Test;


public class MemoryOrderingTest {
    private static final String CHANNEL = "aeron:udp?endpoint=localhost:54325";

    private static final int STREAM_ID = 1;

    private static final int FRAGMENT_COUNT_LIMIT = 10;

    private static final int MESSAGE_LENGTH = 2000;

    private static final int TERM_BUFFER_LENGTH = 1024 * 64;

    private static final int NUM_MESSAGES = 15000;

    private static final int BURST_LENGTH = 7;

    private static final int INTER_BURST_DURATION_NS = 100000;

    private static volatile String failedMessage = null;

    private final MediaDriver driver = MediaDriver.launch(new MediaDriver.Context().errorHandler(Throwable::printStackTrace).threadingMode(SHARED).publicationTermBufferLength(MemoryOrderingTest.TERM_BUFFER_LENGTH));

    private final Aeron aeron = Aeron.connect();

    @Test(timeout = 10000)
    public void shouldReceiveMessagesInOrderWithFirstLongWordIntact() throws Exception {
        final UnsafeBuffer srcBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(MemoryOrderingTest.MESSAGE_LENGTH));
        srcBuffer.setMemory(0, MemoryOrderingTest.MESSAGE_LENGTH, ((byte) (7)));
        try (Subscription subscription = aeron.addSubscription(MemoryOrderingTest.CHANNEL, MemoryOrderingTest.STREAM_ID);Publication publication = aeron.addPublication(MemoryOrderingTest.CHANNEL, MemoryOrderingTest.STREAM_ID)) {
            final BusySpinIdleStrategy idleStrategy = new BusySpinIdleStrategy();
            final Thread subscriberThread = new Thread(new MemoryOrderingTest.Subscriber(subscription));
            subscriberThread.setDaemon(true);
            subscriberThread.start();
            for (int i = 0; i < (MemoryOrderingTest.NUM_MESSAGES); i++) {
                if (null != (MemoryOrderingTest.failedMessage)) {
                    Assert.fail(MemoryOrderingTest.failedMessage);
                }
                srcBuffer.putLong(0, i);
                while ((publication.offer(srcBuffer)) < 0L) {
                    if (null != (MemoryOrderingTest.failedMessage)) {
                        Assert.fail(MemoryOrderingTest.failedMessage);
                    }
                    SystemTest.checkInterruptedStatus();
                    idleStrategy.idle();
                } 
                if ((i % (MemoryOrderingTest.BURST_LENGTH)) == 0) {
                    final long timeout = (System.nanoTime()) + (MemoryOrderingTest.INTER_BURST_DURATION_NS);
                    long now;
                    do {
                        now = System.nanoTime();
                    } while (now < timeout );
                }
            }
            subscriberThread.join();
        }
    }

    @Test(timeout = 10000)
    public void shouldReceiveMessagesInOrderWithFirstLongWordIntactFromExclusivePublication() throws Exception {
        final UnsafeBuffer srcBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(MemoryOrderingTest.MESSAGE_LENGTH));
        srcBuffer.setMemory(0, MemoryOrderingTest.MESSAGE_LENGTH, ((byte) (7)));
        try (Subscription subscription = aeron.addSubscription(MemoryOrderingTest.CHANNEL, MemoryOrderingTest.STREAM_ID);ExclusivePublication publication = aeron.addExclusivePublication(MemoryOrderingTest.CHANNEL, MemoryOrderingTest.STREAM_ID)) {
            final BusySpinIdleStrategy idleStrategy = new BusySpinIdleStrategy();
            final Thread subscriberThread = new Thread(new MemoryOrderingTest.Subscriber(subscription));
            subscriberThread.setDaemon(true);
            subscriberThread.start();
            for (int i = 0; i < (MemoryOrderingTest.NUM_MESSAGES); i++) {
                if (null != (MemoryOrderingTest.failedMessage)) {
                    Assert.fail(MemoryOrderingTest.failedMessage);
                }
                srcBuffer.putLong(0, i);
                while ((publication.offer(srcBuffer)) < 0L) {
                    SystemTest.checkInterruptedStatus();
                    if (null != (MemoryOrderingTest.failedMessage)) {
                        Assert.fail(MemoryOrderingTest.failedMessage);
                    }
                    idleStrategy.idle();
                } 
                if ((i % (MemoryOrderingTest.BURST_LENGTH)) == 0) {
                    final long timeout = (System.nanoTime()) + (MemoryOrderingTest.INTER_BURST_DURATION_NS);
                    long now;
                    do {
                        now = System.nanoTime();
                    } while (now < timeout );
                }
            }
            subscriberThread.join();
        }
    }

    static class Subscriber implements FragmentHandler , Runnable {
        private final FragmentAssembler fragmentAssembler = new FragmentAssembler(this);

        private final Subscription subscription;

        long previousValue = -1;

        int messageNum = 0;

        Subscriber(final Subscription subscription) {
            this.subscription = subscription;
        }

        public void run() {
            final BusySpinIdleStrategy idleStrategy = new BusySpinIdleStrategy();
            while (((messageNum) < (MemoryOrderingTest.NUM_MESSAGES)) && (null == (MemoryOrderingTest.failedMessage))) {
                idleStrategy.idle(subscription.poll(fragmentAssembler, MemoryOrderingTest.FRAGMENT_COUNT_LIMIT));
            } 
        }

        public void onFragment(final DirectBuffer buffer, final int offset, final int length, final Header header) {
            final long messageValue = buffer.getLong(offset);
            final long expectedValue = (previousValue) + 1;
            if (messageValue != expectedValue) {
                final long messageValueSecondRead = buffer.getLong(offset);
                final String msg = (("Issue at message number transition: " + (previousValue)) + " -> ") + messageValue;
                System.out.println(((((((((((((((((((((((((msg + "\n") + "offset: ") + offset) + "\n") + "length: ") + length) + "\n") + "expected bytes: ") + (byteString(expectedValue))) + "\n") + "received bytes: ") + (byteString(messageValue))) + "\n") + "expected bits: ") + (Long.toBinaryString(expectedValue))) + "\n") + "received bits: ") + (Long.toBinaryString(messageValue))) + "\n") + "messageValue on second read: ") + messageValueSecondRead) + "\n") + "messageValue on third read: ") + (buffer.getLong(offset))));
                MemoryOrderingTest.failedMessage = msg;
            }
            previousValue = messageValue;
            (messageNum)++;
        }

        private String byteString(final long value) {
            return String.format("%x %x %x %x %x %x %x %x", ((byte) (value >>> 56)), ((byte) (value >>> 48)), ((byte) (value >>> 40)), ((byte) (value >>> 32)), ((byte) (value >>> 24)), ((byte) (value >>> 18)), ((byte) (value >>> 8)), ((byte) (value)));
        }
    }
}

