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
package org.apache.kafka.streams.kstream;


import java.time.Duration;
import java.util.Map;
import org.apache.kafka.streams.EqualityCheck;
import org.apache.kafka.streams.kstream.internals.TimeWindow;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("deprecation")
public class TimeWindowsTest {
    private static final long ANY_SIZE = 123L;

    @Test
    public void shouldSetWindowSize() {
        Assert.assertEquals(TimeWindowsTest.ANY_SIZE, TimeWindows.of(Duration.ofMillis(TimeWindowsTest.ANY_SIZE)).sizeMs);
    }

    @Test
    public void shouldSetWindowAdvance() {
        final long anyAdvance = 4;
        Assert.assertEquals(anyAdvance, TimeWindows.of(Duration.ofMillis(TimeWindowsTest.ANY_SIZE)).advanceBy(Duration.ofMillis(anyAdvance)).advanceMs);
    }

    // specifically testing deprecated APIs
    @SuppressWarnings("deprecation")
    @Test
    public void shouldSetWindowRetentionTime() {
        Assert.assertEquals(TimeWindowsTest.ANY_SIZE, TimeWindows.of(Duration.ofMillis(TimeWindowsTest.ANY_SIZE)).until(TimeWindowsTest.ANY_SIZE).maintainMs());
    }

    // specifically testing deprecated APIs
    @SuppressWarnings("deprecation")
    @Test
    public void shouldUseWindowSizeAsRentitionTimeIfWindowSizeIsLargerThanDefaultRetentionTime() {
        final long windowSize = 2 * (TimeWindows.of(Duration.ofMillis(1)).maintainMs());
        Assert.assertEquals(windowSize, TimeWindows.of(Duration.ofMillis(windowSize)).maintainMs());
    }

    @Test(expected = IllegalArgumentException.class)
    public void windowSizeMustNotBeZero() {
        TimeWindows.of(Duration.ofMillis(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void windowSizeMustNotBeNegative() {
        TimeWindows.of(Duration.ofMillis((-1)));
    }

    @Test
    public void advanceIntervalMustNotBeZero() {
        final TimeWindows windowSpec = TimeWindows.of(Duration.ofMillis(TimeWindowsTest.ANY_SIZE));
        try {
            windowSpec.advanceBy(Duration.ofMillis(0));
            Assert.fail("should not accept zero advance parameter");
        } catch (final IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void advanceIntervalMustNotBeNegative() {
        final TimeWindows windowSpec = TimeWindows.of(Duration.ofMillis(TimeWindowsTest.ANY_SIZE));
        try {
            windowSpec.advanceBy(Duration.ofMillis((-1)));
            Assert.fail("should not accept negative advance parameter");
        } catch (final IllegalArgumentException e) {
            // expected
        }
    }

    @Deprecated
    @Test
    public void advanceIntervalMustNotBeLargerThanWindowSize() {
        final TimeWindows windowSpec = TimeWindows.of(Duration.ofMillis(TimeWindowsTest.ANY_SIZE));
        try {
            windowSpec.advanceBy(Duration.ofMillis(((TimeWindowsTest.ANY_SIZE) + 1)));
            Assert.fail("should not accept advance greater than window size");
        } catch (final IllegalArgumentException e) {
            // expected
        }
    }

    @Deprecated
    @Test
    public void retentionTimeMustNoBeSmallerThanWindowSize() {
        final TimeWindows windowSpec = TimeWindows.of(Duration.ofMillis(TimeWindowsTest.ANY_SIZE));
        try {
            windowSpec.until(((TimeWindowsTest.ANY_SIZE) - 1));
            Assert.fail("should not accept retention time smaller than window size");
        } catch (final IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void gracePeriodShouldEnforceBoundaries() {
        TimeWindows.of(Duration.ofMillis(3L)).grace(Duration.ofMillis(0L));
        try {
            TimeWindows.of(Duration.ofMillis(3L)).grace(Duration.ofMillis((-1L)));
            Assert.fail("should not accept negatives");
        } catch (final IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void shouldComputeWindowsForHoppingWindows() {
        final TimeWindows windows = TimeWindows.of(Duration.ofMillis(12L)).advanceBy(Duration.ofMillis(5L));
        final Map<Long, TimeWindow> matched = windows.windowsFor(21L);
        Assert.assertEquals(((12L / 5L) + 1), matched.size());
        Assert.assertEquals(new TimeWindow(10L, 22L), matched.get(10L));
        Assert.assertEquals(new TimeWindow(15L, 27L), matched.get(15L));
        Assert.assertEquals(new TimeWindow(20L, 32L), matched.get(20L));
    }

    @Test
    public void shouldComputeWindowsForBarelyOverlappingHoppingWindows() {
        final TimeWindows windows = TimeWindows.of(Duration.ofMillis(6L)).advanceBy(Duration.ofMillis(5L));
        final Map<Long, TimeWindow> matched = windows.windowsFor(7L);
        Assert.assertEquals(1, matched.size());
        Assert.assertEquals(new TimeWindow(5L, 11L), matched.get(5L));
    }

    @Test
    public void shouldComputeWindowsForTumblingWindows() {
        final TimeWindows windows = TimeWindows.of(Duration.ofMillis(12L));
        final Map<Long, TimeWindow> matched = windows.windowsFor(21L);
        Assert.assertEquals(1, matched.size());
        Assert.assertEquals(new TimeWindow(12L, 24L), matched.get(12L));
    }

    @Test
    public void equalsAndHashcodeShouldBeValidForPositiveCases() {
        EqualityCheck.verifyEquality(TimeWindows.of(Duration.ofMillis(3)), TimeWindows.of(Duration.ofMillis(3)));
        EqualityCheck.verifyEquality(TimeWindows.of(Duration.ofMillis(3)).advanceBy(Duration.ofMillis(1)), TimeWindows.of(Duration.ofMillis(3)).advanceBy(Duration.ofMillis(1)));
        EqualityCheck.verifyEquality(TimeWindows.of(Duration.ofMillis(3)).grace(Duration.ofMillis(1)), TimeWindows.of(Duration.ofMillis(3)).grace(Duration.ofMillis(1)));
        EqualityCheck.verifyEquality(TimeWindows.of(Duration.ofMillis(3)).grace(Duration.ofMillis(4)), TimeWindows.of(Duration.ofMillis(3)).grace(Duration.ofMillis(4)));
        EqualityCheck.verifyEquality(TimeWindows.of(Duration.ofMillis(3)).advanceBy(Duration.ofMillis(1)).grace(Duration.ofMillis(1)).grace(Duration.ofMillis(4)), TimeWindows.of(Duration.ofMillis(3)).advanceBy(Duration.ofMillis(1)).grace(Duration.ofMillis(1)).grace(Duration.ofMillis(4)));
    }

    @Test
    public void equalsAndHashcodeShouldBeValidForNegativeCases() {
        EqualityCheck.verifyInEquality(TimeWindows.of(Duration.ofMillis(9)), TimeWindows.of(Duration.ofMillis(3)));
        EqualityCheck.verifyInEquality(TimeWindows.of(Duration.ofMillis(3)).advanceBy(Duration.ofMillis(2)), TimeWindows.of(Duration.ofMillis(3)).advanceBy(Duration.ofMillis(1)));
        EqualityCheck.verifyInEquality(TimeWindows.of(Duration.ofMillis(3)).grace(Duration.ofMillis(2)), TimeWindows.of(Duration.ofMillis(3)).grace(Duration.ofMillis(1)));
        EqualityCheck.verifyInEquality(TimeWindows.of(Duration.ofMillis(3)).grace(Duration.ofMillis(9)), TimeWindows.of(Duration.ofMillis(3)).grace(Duration.ofMillis(4)));
        EqualityCheck.verifyInEquality(TimeWindows.of(Duration.ofMillis(4)).advanceBy(Duration.ofMillis(2)).grace(Duration.ofMillis(2)), TimeWindows.of(Duration.ofMillis(3)).advanceBy(Duration.ofMillis(2)).grace(Duration.ofMillis(2)));
        EqualityCheck.verifyInEquality(TimeWindows.of(Duration.ofMillis(3)).advanceBy(Duration.ofMillis(1)).grace(Duration.ofMillis(2)), TimeWindows.of(Duration.ofMillis(3)).advanceBy(Duration.ofMillis(2)).grace(Duration.ofMillis(2)));
        Assert.assertNotEquals(TimeWindows.of(Duration.ofMillis(3)).advanceBy(Duration.ofMillis(2)).grace(Duration.ofMillis(1)), TimeWindows.of(Duration.ofMillis(3)).advanceBy(Duration.ofMillis(2)).grace(Duration.ofMillis(2)));
    }
}

