/**
 * Copyright 2017 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.pubsub.v1;


import ByteString.EMPTY;
import Subscriber.MIN_ACK_DEADLINE_SECONDS;
import com.google.api.gax.batching.FlowController;
import com.google.auto.value.AutoValue;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.ReceivedMessage;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.Test;


public class MessageDispatcherTest {
    private static final ReceivedMessage TEST_MESSAGE = ReceivedMessage.newBuilder().setAckId("ackid").setMessage(PubsubMessage.newBuilder().setData(EMPTY).build()).build();

    private static final Runnable NOOP_RUNNABLE = new Runnable() {
        @Override
        public void run() {
            // No-op; don't do anything.
        }
    };

    private MessageDispatcher dispatcher;

    private LinkedBlockingQueue<AckReplyConsumer> consumers;

    private List<String> sentAcks;

    private List<MessageDispatcherTest.ModAckItem> sentModAcks;

    private FakeClock clock;

    private FlowController flowController;

    @AutoValue
    abstract static class ModAckItem {
        abstract String ackId();

        abstract int seconds();

        static MessageDispatcherTest.ModAckItem of(String ackId, int seconds) {
            return new AutoValue_MessageDispatcherTest_ModAckItem(ackId, seconds);
        }
    }

    @Test
    public void testReceipt() throws Exception {
        dispatcher.processReceivedMessages(Collections.singletonList(MessageDispatcherTest.TEST_MESSAGE), MessageDispatcherTest.NOOP_RUNNABLE);
        dispatcher.processOutstandingAckOperations();
        assertThat(sentModAcks).contains(MessageDispatcherTest.ModAckItem.of(MessageDispatcherTest.TEST_MESSAGE.getAckId(), MIN_ACK_DEADLINE_SECONDS));
    }

    @Test
    public void testAck() throws Exception {
        dispatcher.processReceivedMessages(Collections.singletonList(MessageDispatcherTest.TEST_MESSAGE), MessageDispatcherTest.NOOP_RUNNABLE);
        consumers.take().ack();
        dispatcher.processOutstandingAckOperations();
        assertThat(sentAcks).contains(MessageDispatcherTest.TEST_MESSAGE.getAckId());
    }

    @Test
    public void testNack() throws Exception {
        dispatcher.processReceivedMessages(Collections.singletonList(MessageDispatcherTest.TEST_MESSAGE), MessageDispatcherTest.NOOP_RUNNABLE);
        consumers.take().nack();
        dispatcher.processOutstandingAckOperations();
        assertThat(sentModAcks).contains(MessageDispatcherTest.ModAckItem.of(MessageDispatcherTest.TEST_MESSAGE.getAckId(), 0));
    }

    @Test
    public void testExtension() throws Exception {
        dispatcher.processReceivedMessages(Collections.singletonList(MessageDispatcherTest.TEST_MESSAGE), MessageDispatcherTest.NOOP_RUNNABLE);
        dispatcher.extendDeadlines();
        assertThat(sentModAcks).contains(MessageDispatcherTest.ModAckItem.of(MessageDispatcherTest.TEST_MESSAGE.getAckId(), MIN_ACK_DEADLINE_SECONDS));
        sentModAcks.clear();
        consumers.take().ack();
        dispatcher.extendDeadlines();
        assertThat(sentModAcks).isEmpty();
    }

    @Test
    public void testExtension_Close() throws Exception {
        dispatcher.processReceivedMessages(Collections.singletonList(MessageDispatcherTest.TEST_MESSAGE), MessageDispatcherTest.NOOP_RUNNABLE);
        dispatcher.extendDeadlines();
        assertThat(sentModAcks).contains(MessageDispatcherTest.ModAckItem.of(MessageDispatcherTest.TEST_MESSAGE.getAckId(), MIN_ACK_DEADLINE_SECONDS));
        sentModAcks.clear();
        // Default total expiration is an hour (60*60 seconds). We normally would extend by 10s.
        // However, only extend by 5s here, since there's only 5s left before total expiration.
        clock.advance(((60 * 60) - 5), TimeUnit.SECONDS);
        dispatcher.extendDeadlines();
        assertThat(sentModAcks).contains(MessageDispatcherTest.ModAckItem.of(MessageDispatcherTest.TEST_MESSAGE.getAckId(), 5));
    }

    @Test
    public void testExtension_GiveUp() throws Exception {
        dispatcher.processReceivedMessages(Collections.singletonList(MessageDispatcherTest.TEST_MESSAGE), MessageDispatcherTest.NOOP_RUNNABLE);
        dispatcher.extendDeadlines();
        assertThat(sentModAcks).contains(MessageDispatcherTest.ModAckItem.of(MessageDispatcherTest.TEST_MESSAGE.getAckId(), MIN_ACK_DEADLINE_SECONDS));
        sentModAcks.clear();
        // If we run extendDeadlines after totalExpiration, we shouldn't send anything.
        // In particular, don't send negative modacks.
        clock.advance(1, TimeUnit.DAYS);
        dispatcher.extendDeadlines();
        assertThat(sentModAcks).isEmpty();
        // We should be able to reserve another item in the flow controller and not block shutdown
        flowController.reserve(1, 0);
        dispatcher.stop();
    }

    @Test
    public void testDeadlineAdjustment() throws Exception {
        assertThat(dispatcher.computeDeadlineSeconds()).isEqualTo(10);
        dispatcher.processReceivedMessages(Collections.singletonList(MessageDispatcherTest.TEST_MESSAGE), MessageDispatcherTest.NOOP_RUNNABLE);
        clock.advance(42, TimeUnit.SECONDS);
        consumers.take().ack();
        assertThat(dispatcher.computeDeadlineSeconds()).isEqualTo(42);
    }
}

