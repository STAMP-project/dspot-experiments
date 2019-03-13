/**
 * Copyright 2017 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.storm.kafka.spout;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.common.TopicPartition;
import org.apache.storm.kafka.spout.KafkaSpoutRetryExponentialBackoff.TimeInterval;
import org.apache.storm.utils.Time;
import org.apache.storm.utils.Time.SimulatedTime;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class KafkaSpoutRetryExponentialBackoffTest {
    private final TopicPartition testTopic = new TopicPartition("topic", 0);

    private final TopicPartition testTopic2 = new TopicPartition("other-topic", 0);

    @Test
    public void testCanScheduleRetry() {
        KafkaSpoutRetryExponentialBackoff retryService = createNoWaitRetryService();
        long offset = 0;
        KafkaSpoutMessageId msgId = retryService.getMessageId(testTopic, offset);
        msgId.incrementNumFails();
        boolean scheduled = retryService.schedule(msgId);
        Assert.assertThat("The service must schedule the message for retry", scheduled, CoreMatchers.is(true));
        KafkaSpoutMessageId retrievedMessageId = retryService.getMessageId(testTopic, offset);
        Assert.assertThat("The service should return the original message id when asked for the same tp/offset twice", retrievedMessageId, CoreMatchers.sameInstance(msgId));
        Assert.assertThat(retryService.isScheduled(msgId), CoreMatchers.is(true));
        Assert.assertThat(retryService.isReady(msgId), CoreMatchers.is(true));
        Assert.assertThat(retryService.readyMessageCount(), CoreMatchers.is(1));
        Assert.assertThat(retryService.earliestRetriableOffsets(), CoreMatchers.is(Collections.singletonMap(testTopic, msgId.offset())));
    }

    @Test
    public void testCanRescheduleRetry() {
        try (SimulatedTime time = new SimulatedTime()) {
            KafkaSpoutRetryExponentialBackoff retryService = createOneSecondWaitRetryService();
            long offset = 0;
            KafkaSpoutMessageId msgId = retryService.getMessageId(testTopic, offset);
            msgId.incrementNumFails();
            retryService.schedule(msgId);
            Time.advanceTime(500);
            boolean scheduled = retryService.schedule(msgId);
            Assert.assertThat("The service must be able to reschedule an already scheduled id", scheduled, CoreMatchers.is(true));
            Time.advanceTime(500);
            Assert.assertThat("The message should not be ready for retry yet since it was rescheduled", retryService.isReady(msgId), CoreMatchers.is(false));
            Assert.assertThat(retryService.isScheduled(msgId), CoreMatchers.is(true));
            Assert.assertThat(retryService.earliestRetriableOffsets(), CoreMatchers.is(Collections.emptyMap()));
            Assert.assertThat(retryService.readyMessageCount(), CoreMatchers.is(0));
            Time.advanceTime(500);
            Assert.assertThat("The message should be ready for retry once the full delay has passed", retryService.isReady(msgId), CoreMatchers.is(true));
            Assert.assertThat(retryService.isScheduled(msgId), CoreMatchers.is(true));
            Assert.assertThat(retryService.earliestRetriableOffsets(), CoreMatchers.is(Collections.singletonMap(testTopic, msgId.offset())));
            Assert.assertThat(retryService.readyMessageCount(), CoreMatchers.is(1));
        }
    }

    @Test
    public void testCannotContainMultipleSchedulesForId() {
        try (SimulatedTime time = new SimulatedTime()) {
            KafkaSpoutRetryExponentialBackoff retryService = createOneSecondWaitRetryService();
            long offset = 0;
            KafkaSpoutMessageId msgId = retryService.getMessageId(testTopic, offset);
            msgId.incrementNumFails();
            retryService.schedule(msgId);
            Time.advanceTime(500);
            boolean scheduled = retryService.schedule(msgId);
            retryService.remove(msgId);
            Assert.assertThat("The message should no longer be scheduled", retryService.isScheduled(msgId), CoreMatchers.is(false));
            Time.advanceTime(500);
            Assert.assertThat("The message should not be ready for retry because it isn't scheduled", retryService.isReady(msgId), CoreMatchers.is(false));
        }
    }

    @Test
    public void testCanRemoveRetry() {
        KafkaSpoutRetryExponentialBackoff retryService = createNoWaitRetryService();
        long offset = 0;
        KafkaSpoutMessageId msgId = retryService.getMessageId(testTopic, offset);
        msgId.incrementNumFails();
        retryService.schedule(msgId);
        boolean removed = retryService.remove(msgId);
        Assert.assertThat(removed, CoreMatchers.is(true));
        Assert.assertThat(retryService.isScheduled(msgId), CoreMatchers.is(false));
        Assert.assertThat(retryService.isReady(msgId), CoreMatchers.is(false));
        Assert.assertThat(retryService.earliestRetriableOffsets(), CoreMatchers.is(Collections.emptyMap()));
        Assert.assertThat(retryService.readyMessageCount(), CoreMatchers.is(0));
    }

    @Test
    public void testCanHandleMultipleTopics() {
        try (SimulatedTime time = new SimulatedTime()) {
            // Tests that isScheduled, isReady and earliestRetriableOffsets are mutually consistent when there are messages from multiple partitions scheduled
            KafkaSpoutRetryExponentialBackoff retryService = createOneSecondWaitRetryService();
            long offset = 0;
            KafkaSpoutMessageId msgIdTp1 = retryService.getMessageId(testTopic, offset);
            KafkaSpoutMessageId msgIdTp2 = retryService.getMessageId(testTopic2, offset);
            msgIdTp1.incrementNumFails();
            msgIdTp2.incrementNumFails();
            boolean scheduledOne = retryService.schedule(msgIdTp1);
            Time.advanceTime(500);
            boolean scheduledTwo = retryService.schedule(msgIdTp2);
            // The retry schedules for two messages should be unrelated
            Assert.assertThat(scheduledOne, CoreMatchers.is(true));
            Assert.assertThat(retryService.isScheduled(msgIdTp1), CoreMatchers.is(true));
            Assert.assertThat(scheduledTwo, CoreMatchers.is(true));
            Assert.assertThat(retryService.isScheduled(msgIdTp2), CoreMatchers.is(true));
            Assert.assertThat(retryService.isReady(msgIdTp1), CoreMatchers.is(false));
            Assert.assertThat(retryService.isReady(msgIdTp2), CoreMatchers.is(false));
            Time.advanceTime(500);
            Assert.assertThat(retryService.isReady(msgIdTp1), CoreMatchers.is(true));
            Assert.assertThat(retryService.isReady(msgIdTp2), CoreMatchers.is(false));
            Assert.assertThat(retryService.earliestRetriableOffsets(), CoreMatchers.is(Collections.singletonMap(testTopic, offset)));
            Time.advanceTime(500);
            Assert.assertThat(retryService.isReady(msgIdTp2), CoreMatchers.is(true));
            Map<TopicPartition, Long> earliestOffsets = new HashMap<>();
            earliestOffsets.put(testTopic, offset);
            earliestOffsets.put(testTopic2, offset);
            Assert.assertThat(retryService.earliestRetriableOffsets(), CoreMatchers.is(earliestOffsets));
            // The service must be able to remove retry schedules for unnecessary partitions
            retryService.retainAll(Collections.singleton(testTopic2));
            Assert.assertThat(retryService.isScheduled(msgIdTp1), CoreMatchers.is(false));
            Assert.assertThat(retryService.isScheduled(msgIdTp2), CoreMatchers.is(true));
            Assert.assertThat(retryService.isReady(msgIdTp1), CoreMatchers.is(false));
            Assert.assertThat(retryService.isReady(msgIdTp2), CoreMatchers.is(true));
            Assert.assertThat(retryService.earliestRetriableOffsets(), CoreMatchers.is(Collections.singletonMap(testTopic2, offset)));
        }
    }

    @Test
    public void testCanHandleMultipleMessagesOnPartition() {
        try (SimulatedTime time = new SimulatedTime()) {
            // Tests that isScheduled, isReady and earliestRetriableOffsets are mutually consistent when there are multiple messages scheduled on a partition
            KafkaSpoutRetryExponentialBackoff retryService = createOneSecondWaitRetryService();
            long offset = 0;
            KafkaSpoutMessageId msgIdEarliest = retryService.getMessageId(testTopic, offset);
            KafkaSpoutMessageId msgIdLatest = retryService.getMessageId(testTopic, (offset + 1));
            msgIdEarliest.incrementNumFails();
            msgIdLatest.incrementNumFails();
            retryService.schedule(msgIdEarliest);
            Time.advanceTime(500);
            retryService.schedule(msgIdLatest);
            Assert.assertThat(retryService.isScheduled(msgIdEarliest), CoreMatchers.is(true));
            Assert.assertThat(retryService.isScheduled(msgIdLatest), CoreMatchers.is(true));
            Time.advanceTime(500);
            Assert.assertThat(retryService.isReady(msgIdEarliest), CoreMatchers.is(true));
            Assert.assertThat(retryService.isReady(msgIdLatest), CoreMatchers.is(false));
            Assert.assertThat(retryService.earliestRetriableOffsets(), CoreMatchers.is(Collections.singletonMap(testTopic, msgIdEarliest.offset())));
            Time.advanceTime(500);
            Assert.assertThat(retryService.isReady(msgIdEarliest), CoreMatchers.is(true));
            Assert.assertThat(retryService.isReady(msgIdLatest), CoreMatchers.is(true));
            Assert.assertThat(retryService.earliestRetriableOffsets(), CoreMatchers.is(Collections.singletonMap(testTopic, msgIdEarliest.offset())));
            retryService.remove(msgIdEarliest);
            Assert.assertThat(retryService.earliestRetriableOffsets(), CoreMatchers.is(Collections.singletonMap(testTopic, msgIdLatest.offset())));
        }
    }

    @Test
    public void testMaxRetries() {
        try (SimulatedTime time = new SimulatedTime()) {
            int maxRetries = 3;
            KafkaSpoutRetryExponentialBackoff retryService = new KafkaSpoutRetryExponentialBackoff(TimeInterval.seconds(0), TimeInterval.seconds(0), maxRetries, TimeInterval.seconds(0));
            long offset = 0;
            KafkaSpoutMessageId msgId = retryService.getMessageId(testTopic, offset);
            for (int i = 0; i < maxRetries; i++) {
                msgId.incrementNumFails();
            }
            // Should be allowed to retry 3 times, in addition to original try
            boolean scheduled = retryService.schedule(msgId);
            Assert.assertThat(scheduled, CoreMatchers.is(true));
            Assert.assertThat(retryService.isScheduled(msgId), CoreMatchers.is(true));
            retryService.remove(msgId);
            msgId.incrementNumFails();
            boolean rescheduled = retryService.schedule(msgId);
            Assert.assertThat("The message should not be allowed to retry once the limit is reached", rescheduled, CoreMatchers.is(false));
            Assert.assertThat(retryService.isScheduled(msgId), CoreMatchers.is(false));
        }
    }

    @Test
    public void testMaxDelay() {
        try (SimulatedTime time = new SimulatedTime()) {
            int maxDelaySecs = 2;
            KafkaSpoutRetryExponentialBackoff retryService = new KafkaSpoutRetryExponentialBackoff(TimeInterval.seconds(500), TimeInterval.seconds(0), 1, TimeInterval.seconds(maxDelaySecs));
            long offset = 0;
            KafkaSpoutMessageId msgId = retryService.getMessageId(testTopic, offset);
            msgId.incrementNumFails();
            retryService.schedule(msgId);
            Time.advanceTimeSecs(maxDelaySecs);
            Assert.assertThat("The message should be ready for retry after the max delay", retryService.isReady(msgId), CoreMatchers.is(true));
        }
    }

    @Test
    public void testExponentialBackoff() {
        try (SimulatedTime time = new SimulatedTime()) {
            KafkaSpoutRetryExponentialBackoff retryService = new KafkaSpoutRetryExponentialBackoff(TimeInterval.seconds(0), TimeInterval.seconds(4), Integer.MAX_VALUE, TimeInterval.seconds(Integer.MAX_VALUE));
            long offset = 0;
            KafkaSpoutMessageId msgId = retryService.getMessageId(testTopic, offset);
            msgId.incrementNumFails();
            msgId.incrementNumFails();// First failure is the initial delay, so not interesting

            // Expecting 4*2^(failCount-1)
            List<Integer> expectedBackoffsSecs = Arrays.asList(new Integer[]{ 8, 16, 32 });
            for (Integer expectedBackoffSecs : expectedBackoffsSecs) {
                retryService.schedule(msgId);
                Time.advanceTimeSecs((expectedBackoffSecs - 1));
                Assert.assertThat((("The message should not be ready for retry until backoff " + expectedBackoffSecs) + " has expired"), retryService.isReady(msgId), CoreMatchers.is(false));
                Time.advanceTimeSecs(1);
                Assert.assertThat((("The message should be ready for retry once backoff " + expectedBackoffSecs) + " has expired"), retryService.isReady(msgId), CoreMatchers.is(true));
                msgId.incrementNumFails();
                retryService.remove(msgId);
            }
        }
    }
}

