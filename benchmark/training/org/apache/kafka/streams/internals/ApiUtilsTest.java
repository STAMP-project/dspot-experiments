/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.internals;


import java.time.Duration;
import java.time.Instant;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


public class ApiUtilsTest {
    // This is the maximum limit that Duration accepts but fails when it converts to milliseconds.
    private static final long MAX_ACCEPTABLE_DAYS_FOR_DURATION = 106751991167300L;

    // This is the maximum limit that Duration accepts and converts to milliseconds with out fail.
    private static final long MAX_ACCEPTABLE_DAYS_FOR_DURATION_TO_MILLIS = 106751991167L;

    @Test
    public void shouldThrowNullPointerExceptionForNullDuration() {
        final String nullDurationPrefix = ApiUtils.prepareMillisCheckFailMsgPrefix(null, "nullDuration");
        try {
            ApiUtils.validateMillisecondDuration(null, nullDurationPrefix);
            Assert.fail("Expected exception when null passed to duration.");
        } catch (final IllegalArgumentException e) {
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString(nullDurationPrefix));
        }
    }

    @Test
    public void shouldThrowArithmeticExceptionForMaxDuration() {
        final Duration maxDurationInDays = Duration.ofDays(ApiUtilsTest.MAX_ACCEPTABLE_DAYS_FOR_DURATION);
        final String maxDurationPrefix = ApiUtils.prepareMillisCheckFailMsgPrefix(maxDurationInDays, "maxDuration");
        try {
            ApiUtils.validateMillisecondDuration(maxDurationInDays, maxDurationPrefix);
            Assert.fail("Expected exception when maximum days passed for duration, because of long overflow");
        } catch (final IllegalArgumentException e) {
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString(maxDurationPrefix));
        }
    }

    @Test
    public void shouldThrowNullPointerExceptionForNullInstant() {
        final String nullInstantPrefix = ApiUtils.prepareMillisCheckFailMsgPrefix(null, "nullInstant");
        try {
            ApiUtils.validateMillisecondInstant(null, nullInstantPrefix);
            Assert.fail("Expected exception when null value passed for instant.");
        } catch (final IllegalArgumentException e) {
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString(nullInstantPrefix));
        }
    }

    @Test
    public void shouldThrowArithmeticExceptionForMaxInstant() {
        final String maxInstantPrefix = ApiUtils.prepareMillisCheckFailMsgPrefix(Instant.MAX, "maxInstant");
        try {
            ApiUtils.validateMillisecondInstant(Instant.MAX, maxInstantPrefix);
            Assert.fail("Expected exception when maximum value passed for instant, because of long overflow.");
        } catch (final IllegalArgumentException e) {
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString(maxInstantPrefix));
        }
    }

    @Test
    public void shouldReturnMillisecondsOnValidDuration() {
        final Duration sampleDuration = Duration.ofDays(ApiUtilsTest.MAX_ACCEPTABLE_DAYS_FOR_DURATION_TO_MILLIS);
        Assert.assertEquals(sampleDuration.toMillis(), ApiUtils.validateMillisecondDuration(sampleDuration, "sampleDuration"));
    }

    @Test
    public void shouldReturnMillisecondsOnValidInstant() {
        final Instant sampleInstant = Instant.now();
        Assert.assertEquals(sampleInstant.toEpochMilli(), ApiUtils.validateMillisecondInstant(sampleInstant, "sampleInstant"));
    }

    @Test
    public void shouldContainsNameAndValueInFailMsgPrefix() {
        final String failMsgPrefix = ApiUtils.prepareMillisCheckFailMsgPrefix("someValue", "variableName");
        MatcherAssert.assertThat(failMsgPrefix, CoreMatchers.containsString("variableName"));
        MatcherAssert.assertThat(failMsgPrefix, CoreMatchers.containsString("someValue"));
    }
}

