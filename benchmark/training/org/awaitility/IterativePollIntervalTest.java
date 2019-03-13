/**
 * Copyright 2015 the original author or authors.
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
package org.awaitility;


import Duration.FIVE_HUNDRED_MILLISECONDS;
import Duration.ONE_SECOND;
import Duration.ZERO;
import org.awaitility.pollinterval.IterativePollInterval;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

import static Duration.ONE_MILLISECOND;


public class IterativePollIntervalTest {
    @Test
    public void iterative_poll_interval_allows_specifying_start_duration() {
        // Given
        IterativePollInterval pollInterval = new IterativePollInterval(( prev) -> prev.multiply(2), ONE_MILLISECOND);
        // When
        Duration duration = pollInterval.next(1, ZERO);
        // Then
        Assert.assertThat(duration.getValueInMS(), Is.is(2L));
    }

    @Test
    public void iterative_poll_interval_use_no_start_value_by_default() {
        // Given
        IterativePollInterval pollInterval = new IterativePollInterval(( prev) -> prev.multiply(2));
        // When
        Duration duration = pollInterval.next(1, ONE_SECOND);
        // Then
        Assert.assertThat(duration.getValueInMS(), Is.is(2000L));
    }

    @Test
    public void iterative_poll_interval_allows_specifying_start_duration_through_dsl() {
        // Given
        IterativePollInterval pollInterval = iterative(( prev) -> prev.multiply(2)).with().startDuration(FIVE_HUNDRED_MILLISECONDS);
        // When
        Duration duration = pollInterval.next(1, ONE_SECOND);
        // Then
        Assert.assertThat(duration.getValueInMS(), Is.is(1000L));
    }
}

