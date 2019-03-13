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


import io.aeron.driver.MediaDriver;
import io.aeron.driver.ThreadingMode;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.LogBufferDescriptor;
import io.aeron.protocol.DataHeaderFlyweight;
import java.io.File;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.IntConsumer;
import org.agrona.SystemUtil;
import org.agrona.collections.MutableInteger;
import org.agrona.concurrent.UnsafeBuffer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MultiDriverTest {
    private static final String MULTICAST_URI = "aeron:udp?endpoint=224.20.30.39:54326|interface=localhost";

    private static final int STREAM_ID = 1;

    private static final ThreadingMode THREADING_MODE = ThreadingMode.SHARED;

    private static final int TERM_BUFFER_LENGTH = LogBufferDescriptor.TERM_MIN_LENGTH;

    private static final int NUM_MESSAGES_PER_TERM = 64;

    private static final int MESSAGE_LENGTH = ((MultiDriverTest.TERM_BUFFER_LENGTH) / (MultiDriverTest.NUM_MESSAGES_PER_TERM)) - (DataHeaderFlyweight.HEADER_LENGTH);

    private static final String ROOT_DIR = (((SystemUtil.tmpDirName()) + "aeron-system-tests-") + (UUID.randomUUID().toString())) + (File.separator);

    private Aeron clientA;

    private Aeron clientB;

    private MediaDriver driverA;

    private MediaDriver driverB;

    private Publication publication;

    private Subscription subscriptionA;

    private Subscription subscriptionB;

    private final UnsafeBuffer buffer = new UnsafeBuffer(new byte[MultiDriverTest.MESSAGE_LENGTH]);

    private final MutableInteger fragmentCountA = new MutableInteger();

    private final FragmentHandler fragmentHandlerA = ( buffer1, offset, length, header) -> fragmentCountA.value++;

    private final MutableInteger fragmentCountB = new MutableInteger();

    private final FragmentHandler fragmentHandlerB = ( buffer1, offset, length, header) -> fragmentCountB.value++;

    @Test(timeout = 10000)
    public void shouldSpinUpAndShutdown() {
        launch();
        subscriptionA = clientA.addSubscription(MultiDriverTest.MULTICAST_URI, MultiDriverTest.STREAM_ID);
        subscriptionB = clientB.addSubscription(MultiDriverTest.MULTICAST_URI, MultiDriverTest.STREAM_ID);
        publication = clientA.addPublication(MultiDriverTest.MULTICAST_URI, MultiDriverTest.STREAM_ID);
        while ((!(subscriptionA.isConnected())) && (!(subscriptionB.isConnected()))) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
    }

    @Test(timeout = 10000)
    public void shouldJoinExistingStreamWithLockStepSendingReceiving() throws Exception {
        final int numMessagesToSendPreJoin = (MultiDriverTest.NUM_MESSAGES_PER_TERM) / 2;
        final int numMessagesToSendPostJoin = MultiDriverTest.NUM_MESSAGES_PER_TERM;
        launch();
        subscriptionA = clientA.addSubscription(MultiDriverTest.MULTICAST_URI, MultiDriverTest.STREAM_ID);
        publication = clientA.addPublication(MultiDriverTest.MULTICAST_URI, MultiDriverTest.STREAM_ID);
        for (int i = 0; i < numMessagesToSendPreJoin; i++) {
            while ((publication.offer(buffer, 0, buffer.capacity())) < 0L) {
                SystemTest.checkInterruptedStatus();
                Thread.yield();
            } 
            final MutableInteger fragmentsRead = new MutableInteger();
            SystemTest.executeUntil(() -> (fragmentsRead.get()) > 0, ( j) -> {
                fragmentsRead.value += subscriptionA.poll(fragmentHandlerA, 10);
                Thread.yield();
            }, Integer.MAX_VALUE, TimeUnit.MILLISECONDS.toNanos(500));
        }
        final CountDownLatch newImageLatch = new CountDownLatch(1);
        subscriptionB = clientB.addSubscription(MultiDriverTest.MULTICAST_URI, MultiDriverTest.STREAM_ID, ( image) -> newImageLatch.countDown(), null);
        newImageLatch.await();
        for (int i = 0; i < numMessagesToSendPostJoin; i++) {
            while ((publication.offer(buffer, 0, buffer.capacity())) < 0L) {
                SystemTest.checkInterruptedStatus();
                Thread.yield();
            } 
            final MutableInteger fragmentsRead = new MutableInteger();
            SystemTest.executeUntil(() -> (fragmentsRead.get()) > 0, ( j) -> {
                fragmentsRead.value += subscriptionA.poll(fragmentHandlerA, 10);
                Thread.yield();
            }, Integer.MAX_VALUE, TimeUnit.MILLISECONDS.toNanos(500));
            fragmentsRead.set(0);
            SystemTest.executeUntil(() -> (fragmentsRead.get()) > 0, ( j) -> {
                fragmentsRead.value += subscriptionB.poll(fragmentHandlerB, 10);
                Thread.yield();
            }, Integer.MAX_VALUE, TimeUnit.MILLISECONDS.toNanos(500));
        }
        Assert.assertThat(fragmentCountA.value, CoreMatchers.is((numMessagesToSendPreJoin + numMessagesToSendPostJoin)));
        Assert.assertThat(fragmentCountB.value, CoreMatchers.is(numMessagesToSendPostJoin));
    }

    @Test(timeout = 10000)
    public void shouldJoinExistingIdleStreamWithLockStepSendingReceiving() throws Exception {
        final int numMessagesToSendPreJoin = 0;
        final int numMessagesToSendPostJoin = MultiDriverTest.NUM_MESSAGES_PER_TERM;
        launch();
        subscriptionA = clientA.addSubscription(MultiDriverTest.MULTICAST_URI, MultiDriverTest.STREAM_ID);
        publication = clientA.addPublication(MultiDriverTest.MULTICAST_URI, MultiDriverTest.STREAM_ID);
        while ((!(publication.isConnected())) && (!(subscriptionA.isConnected()))) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
        final CountDownLatch newImageLatch = new CountDownLatch(1);
        subscriptionB = clientB.addSubscription(MultiDriverTest.MULTICAST_URI, MultiDriverTest.STREAM_ID, ( image) -> newImageLatch.countDown(), null);
        newImageLatch.await();
        for (int i = 0; i < numMessagesToSendPostJoin; i++) {
            while ((publication.offer(buffer, 0, buffer.capacity())) < 0L) {
                SystemTest.checkInterruptedStatus();
                Thread.yield();
            } 
            final MutableInteger fragmentsRead = new MutableInteger();
            SystemTest.executeUntil(() -> (fragmentsRead.get()) > 0, ( j) -> {
                fragmentsRead.value += subscriptionA.poll(fragmentHandlerA, 10);
                Thread.yield();
            }, Integer.MAX_VALUE, TimeUnit.MILLISECONDS.toNanos(500));
            fragmentsRead.set(0);
            SystemTest.executeUntil(() -> (fragmentsRead.get()) > 0, ( j) -> {
                fragmentsRead.value += subscriptionB.poll(fragmentHandlerB, 10);
                Thread.yield();
            }, Integer.MAX_VALUE, TimeUnit.MILLISECONDS.toNanos(500));
        }
        Assert.assertThat(fragmentCountA.value, CoreMatchers.is((numMessagesToSendPreJoin + numMessagesToSendPostJoin)));
        Assert.assertThat(fragmentCountB.value, CoreMatchers.is(numMessagesToSendPostJoin));
    }
}

