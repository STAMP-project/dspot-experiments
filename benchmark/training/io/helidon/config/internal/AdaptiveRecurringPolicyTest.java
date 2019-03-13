/**
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.config.internal;


import ScheduledPollingStrategy.AdaptiveRecurringPolicy;
import ScheduledPollingStrategy.RecurringPolicy;
import java.time.Duration;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;


/**
 * Tests {@link ScheduledPollingStrategy.AdaptiveRecurringPolicy}
 */
public class AdaptiveRecurringPolicyTest {
    @Test
    public void testCustomShortenFunction() {
        ScheduledPollingStrategy.AdaptiveRecurringPolicy policy = new ScheduledPollingStrategy.AdaptiveRecurringPolicy(Duration.ofMillis(1), Duration.ofMillis(10), Duration.ofMillis(100), ( current, changeFactor) -> current.minusMillis(1), ( current, changeFactor) -> current.plusMillis(1));
        policy.shorten();
        MatcherAssert.assertThat(policy.delay().toMillis(), Is.is(9L));
    }

    @Test
    public void testCustomLengthenFunction() {
        ScheduledPollingStrategy.AdaptiveRecurringPolicy policy = new ScheduledPollingStrategy.AdaptiveRecurringPolicy(Duration.ofMillis(1), Duration.ofMillis(10), Duration.ofMillis(100), ( current, changeFactor) -> current.minusMillis(1), ( current, changeFactor) -> current.plusMillis(1));
        policy.lengthen();
        MatcherAssert.assertThat(policy.delay().toMillis(), Is.is(11L));
    }

    @Test
    public void testDefaultShortenFunction() {
        ScheduledPollingStrategy.AdaptiveRecurringPolicy policy = ((ScheduledPollingStrategy.AdaptiveRecurringPolicy) (RecurringPolicy.adaptiveBuilder(Duration.ofMillis(10)).min(Duration.ofMillis(1)).max(Duration.ofMillis(100)).build()));
        policy.shorten();
        MatcherAssert.assertThat(policy.delay().toMillis(), Is.is(5L));
        policy.shorten();
        MatcherAssert.assertThat(policy.delay().toMillis(), Is.is(2L));
    }

    @Test
    public void testDefaultLengthenFunction() {
        ScheduledPollingStrategy.AdaptiveRecurringPolicy policy = ((ScheduledPollingStrategy.AdaptiveRecurringPolicy) (RecurringPolicy.adaptiveBuilder(Duration.ofMillis(10)).min(Duration.ofMillis(1)).max(Duration.ofMillis(100)).build()));
        policy.lengthen();
        MatcherAssert.assertThat(policy.delay().toMillis(), Is.is(20L));
        policy.lengthen();
        MatcherAssert.assertThat(policy.delay().toMillis(), Is.is(40L));
    }

    @Test
    public void testMin() {
        ScheduledPollingStrategy.AdaptiveRecurringPolicy policy = ((ScheduledPollingStrategy.AdaptiveRecurringPolicy) (RecurringPolicy.adaptiveBuilder(Duration.ofMillis(10)).min(Duration.ofMillis(2)).max(Duration.ofMillis(100)).build()));
        policy.shorten();
        MatcherAssert.assertThat(policy.delay().toMillis(), Is.is(5L));
        policy.shorten();
        MatcherAssert.assertThat(policy.delay().toMillis(), Is.is(2L));
        policy.shorten();
        MatcherAssert.assertThat(policy.delay().toMillis(), Is.is(2L));
    }

    @Test
    public void testMax() {
        ScheduledPollingStrategy.AdaptiveRecurringPolicy policy = ((ScheduledPollingStrategy.AdaptiveRecurringPolicy) (RecurringPolicy.adaptiveBuilder(Duration.ofMillis(10)).min(Duration.ofMillis(1)).max(Duration.ofMillis(100)).build()));
        policy.lengthen();
        MatcherAssert.assertThat(policy.delay().toMillis(), Is.is(20L));
        policy.lengthen();
        MatcherAssert.assertThat(policy.delay().toMillis(), Is.is(40L));
        policy.lengthen();
        MatcherAssert.assertThat(policy.delay().toMillis(), Is.is(80L));
        policy.lengthen();
        MatcherAssert.assertThat(policy.delay().toMillis(), Is.is(100L));
        policy.lengthen();
        MatcherAssert.assertThat(policy.delay().toMillis(), Is.is(100L));
    }

    @Test
    public void testDefaultMin() {
        ScheduledPollingStrategy.AdaptiveRecurringPolicy policy = ((ScheduledPollingStrategy.AdaptiveRecurringPolicy) (RecurringPolicy.adaptiveBuilder(Duration.ofMillis(10)).build()));
        policy.shorten();
        policy.shorten();
        policy.shorten();
        policy.shorten();
        MatcherAssert.assertThat(policy.delay().toMillis(), Is.is(1L));
        policy.shorten();
        MatcherAssert.assertThat(policy.delay().toMillis(), Is.is(1L));
    }

    @Test
    public void testDefaultMax() {
        ScheduledPollingStrategy.AdaptiveRecurringPolicy policy = ((ScheduledPollingStrategy.AdaptiveRecurringPolicy) (RecurringPolicy.adaptiveBuilder(Duration.ofMillis(10)).build()));
        policy.lengthen();
        policy.lengthen();
        policy.lengthen();
        MatcherAssert.assertThat(policy.delay().toMillis(), Is.is(50L));
        policy.lengthen();
        MatcherAssert.assertThat(policy.delay().toMillis(), Is.is(50L));
    }

    @Test
    public void testAllWithBuilder() {
        ScheduledPollingStrategy.AdaptiveRecurringPolicy policy = ((ScheduledPollingStrategy.AdaptiveRecurringPolicy) (RecurringPolicy.adaptiveBuilder(Duration.ofMillis(10)).min(Duration.ofMillis(9)).max(Duration.ofMillis(11)).shorten(( current, changesFactor) -> current.minusMillis(1)).lengthen(( current, changesFactor) -> current.plusMillis(1)).build()));
        MatcherAssert.assertThat(policy.delay().toMillis(), Is.is(10L));
        policy.lengthen();
        MatcherAssert.assertThat(policy.delay().toMillis(), Is.is(11L));
        policy.lengthen();
        MatcherAssert.assertThat(policy.delay().toMillis(), Is.is(11L));
        policy.shorten();
        MatcherAssert.assertThat(policy.delay().toMillis(), Is.is(10L));
        policy.shorten();
        MatcherAssert.assertThat(policy.delay().toMillis(), Is.is(9L));
        policy.shorten();
        MatcherAssert.assertThat(policy.delay().toMillis(), Is.is(9L));
    }
}

