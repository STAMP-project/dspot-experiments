/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.io.kafka;


import BoundedWindow.TIMESTAMP_MIN_VALUE;
import TimestampPolicy.PartitionContext;
import java.util.List;
import java.util.Optional;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.core.Is;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Tests for {@link CustomTimestampPolicyWithLimitedDelay}.
 */
@RunWith(JUnit4.class)
public class CustomTimestampPolicyWithLimitedDelayTest {
    @Test
    public void testCustomTimestampPolicyWithLimitedDelay() {
        // Verifies that max delay is applies appropriately for reporting watermark
        Duration maxDelay = Duration.standardSeconds(60);
        CustomTimestampPolicyWithLimitedDelay<String, String> policy = new CustomTimestampPolicyWithLimitedDelay(( record) -> new Instant(record.getTimestamp()), maxDelay, Optional.empty());
        Instant now = Instant.now();
        TimestampPolicy.PartitionContext ctx = Mockito.mock(PartitionContext.class);
        Mockito.when(ctx.getMessageBacklog()).thenReturn(100L);
        Mockito.when(ctx.getBacklogCheckTime()).thenReturn(now);
        Assert.assertThat(policy.getWatermark(ctx), Is.is(TIMESTAMP_MIN_VALUE));
        // (1) Test simple case : watermark == max_timesatmp - max_delay
        List<Long> input = // <<< Max timestamp
        ImmutableList.of((-200000L), (-150000L), (-120000L), (-140000L), (-100000L), (-110000L));
        Assert.assertThat(CustomTimestampPolicyWithLimitedDelayTest.getTimestampsForRecords(policy, now, input), Is.is(input));
        // Watermark should be max_timestamp - maxDelay
        Assert.assertThat(policy.getWatermark(ctx), Is.is(now.minus(Duration.standardSeconds(100)).minus(maxDelay)));
        // (2) Verify future timestamps
        input = // <<< timestamp is in future
        ImmutableList.of((-200000L), (-150000L), (-120000L), (-140000L), 100000L, (-100000L), (-110000L));
        Assert.assertThat(CustomTimestampPolicyWithLimitedDelayTest.getTimestampsForRecords(policy, now, input), Is.is(input));
        // Watermark should be now - max_delay (backlog in context still non zero)
        Assert.assertThat(policy.getWatermark(ctx, now), Is.is(now.minus(maxDelay)));
        // (3) Verify that Watermark advances when there is no backlog
        // advance current time by 5 minutes
        now = now.plus(Duration.standardMinutes(5));
        Instant backlogCheckTime = now.minus(Duration.standardSeconds(10));
        Mockito.when(ctx.getMessageBacklog()).thenReturn(0L);
        Mockito.when(ctx.getBacklogCheckTime()).thenReturn(backlogCheckTime);
        Assert.assertThat(policy.getWatermark(ctx, now), Is.is(backlogCheckTime.minus(maxDelay)));
    }
}

