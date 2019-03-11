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
import org.apache.kafka.streams.EqualityCheck;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("deprecation")
public class JoinWindowsTest {
    private static final long ANY_SIZE = 123L;

    private static final long ANY_OTHER_SIZE = 456L;// should be larger than anySize


    @Test
    public void validWindows() {
        // [ anySize ; anyOtherSize ]
        // [ 0 ; anyOtherSize ]
        // [ -anySize ; anyOtherSize ]
        // [ -anyOtherSize ; anyOtherSize ]
        JoinWindows.of(Duration.ofMillis(JoinWindowsTest.ANY_OTHER_SIZE)).before(Duration.ofMillis(JoinWindowsTest.ANY_SIZE)).before(Duration.ofMillis(0)).before(Duration.ofMillis((-(JoinWindowsTest.ANY_SIZE)))).before(Duration.ofMillis((-(JoinWindowsTest.ANY_OTHER_SIZE))));
        // [ anyOtherSize ; anyOtherSize ]
        // [ -anyOtherSize ; -anySize ]
        // [ -anyOtherSize ; 0 ]
        // [ -anyOtherSize ; anySize ]
        // [ -anyOtherSize ; anyOtherSize ]
        JoinWindows.of(Duration.ofMillis(JoinWindowsTest.ANY_OTHER_SIZE)).after(Duration.ofMillis(JoinWindowsTest.ANY_SIZE)).after(Duration.ofMillis(0)).after(Duration.ofMillis((-(JoinWindowsTest.ANY_SIZE)))).after(Duration.ofMillis((-(JoinWindowsTest.ANY_OTHER_SIZE))));// [ -anyOtherSize ; -anyOtherSize ]

    }

    @Test(expected = IllegalArgumentException.class)
    public void timeDifferenceMustNotBeNegative() {
        JoinWindows.of(Duration.ofMillis((-1)));
    }

    @Test
    public void endTimeShouldNotBeBeforeStart() {
        final JoinWindows windowSpec = JoinWindows.of(Duration.ofMillis(JoinWindowsTest.ANY_SIZE));
        try {
            windowSpec.after(Duration.ofMillis(((-(JoinWindowsTest.ANY_SIZE)) - 1)));
            Assert.fail("window end time should not be before window start time");
        } catch (final IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void startTimeShouldNotBeAfterEnd() {
        final JoinWindows windowSpec = JoinWindows.of(Duration.ofMillis(JoinWindowsTest.ANY_SIZE));
        try {
            windowSpec.before(Duration.ofMillis(((-(JoinWindowsTest.ANY_SIZE)) - 1)));
            Assert.fail("window start time should not be after window end time");
        } catch (final IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void untilShouldSetGraceDuration() {
        final JoinWindows windowSpec = JoinWindows.of(Duration.ofMillis(JoinWindowsTest.ANY_SIZE));
        final long windowSize = windowSpec.size();
        Assert.assertEquals(windowSize, windowSpec.grace(Duration.ofMillis(windowSize)).gracePeriodMs());
    }

    @Deprecated
    @Test
    public void retentionTimeMustNoBeSmallerThanWindowSize() {
        final JoinWindows windowSpec = JoinWindows.of(Duration.ofMillis(JoinWindowsTest.ANY_SIZE));
        final long windowSize = windowSpec.size();
        try {
            windowSpec.until((windowSize - 1));
            Assert.fail("should not accept retention time smaller than window size");
        } catch (final IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void gracePeriodShouldEnforceBoundaries() {
        JoinWindows.of(Duration.ofMillis(3L)).grace(Duration.ofMillis(0L));
        try {
            JoinWindows.of(Duration.ofMillis(3L)).grace(Duration.ofMillis((-1L)));
            Assert.fail("should not accept negatives");
        } catch (final IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void equalsAndHashcodeShouldBeValidForPositiveCases() {
        EqualityCheck.verifyEquality(JoinWindows.of(Duration.ofMillis(3)), JoinWindows.of(Duration.ofMillis(3)));
        EqualityCheck.verifyEquality(JoinWindows.of(Duration.ofMillis(3)).after(Duration.ofMillis(2)), JoinWindows.of(Duration.ofMillis(3)).after(Duration.ofMillis(2)));
        EqualityCheck.verifyEquality(JoinWindows.of(Duration.ofMillis(3)).before(Duration.ofMillis(2)), JoinWindows.of(Duration.ofMillis(3)).before(Duration.ofMillis(2)));
        EqualityCheck.verifyEquality(JoinWindows.of(Duration.ofMillis(3)).grace(Duration.ofMillis(2)), JoinWindows.of(Duration.ofMillis(3)).grace(Duration.ofMillis(2)));
        EqualityCheck.verifyEquality(JoinWindows.of(Duration.ofMillis(3)).grace(Duration.ofMillis(60)), JoinWindows.of(Duration.ofMillis(3)).grace(Duration.ofMillis(60)));
        EqualityCheck.verifyEquality(JoinWindows.of(Duration.ofMillis(3)).before(Duration.ofMillis(1)).after(Duration.ofMillis(2)).grace(Duration.ofMillis(3)).grace(Duration.ofMillis(60)), JoinWindows.of(Duration.ofMillis(3)).before(Duration.ofMillis(1)).after(Duration.ofMillis(2)).grace(Duration.ofMillis(3)).grace(Duration.ofMillis(60)));
        // JoinWindows is a little weird in that before and after set the same fields as of.
        EqualityCheck.verifyEquality(JoinWindows.of(Duration.ofMillis(9)).before(Duration.ofMillis(1)).after(Duration.ofMillis(2)).grace(Duration.ofMillis(3)).grace(Duration.ofMillis(60)), JoinWindows.of(Duration.ofMillis(3)).before(Duration.ofMillis(1)).after(Duration.ofMillis(2)).grace(Duration.ofMillis(3)).grace(Duration.ofMillis(60)));
    }

    @Test
    public void equalsAndHashcodeShouldBeValidForNegativeCases() {
        EqualityCheck.verifyInEquality(JoinWindows.of(Duration.ofMillis(9)), JoinWindows.of(Duration.ofMillis(3)));
        EqualityCheck.verifyInEquality(JoinWindows.of(Duration.ofMillis(3)).after(Duration.ofMillis(9)), JoinWindows.of(Duration.ofMillis(3)).after(Duration.ofMillis(2)));
        EqualityCheck.verifyInEquality(JoinWindows.of(Duration.ofMillis(3)).before(Duration.ofMillis(9)), JoinWindows.of(Duration.ofMillis(3)).before(Duration.ofMillis(2)));
        EqualityCheck.verifyInEquality(JoinWindows.of(Duration.ofMillis(3)).grace(Duration.ofMillis(9)), JoinWindows.of(Duration.ofMillis(3)).grace(Duration.ofMillis(2)));
        EqualityCheck.verifyInEquality(JoinWindows.of(Duration.ofMillis(3)).grace(Duration.ofMillis(90)), JoinWindows.of(Duration.ofMillis(3)).grace(Duration.ofMillis(60)));
        EqualityCheck.verifyInEquality(JoinWindows.of(Duration.ofMillis(3)).before(Duration.ofMillis(9)).after(Duration.ofMillis(2)).grace(Duration.ofMillis(3)), JoinWindows.of(Duration.ofMillis(3)).before(Duration.ofMillis(1)).after(Duration.ofMillis(2)).grace(Duration.ofMillis(3)));
        EqualityCheck.verifyInEquality(JoinWindows.of(Duration.ofMillis(3)).before(Duration.ofMillis(1)).after(Duration.ofMillis(9)).grace(Duration.ofMillis(3)), JoinWindows.of(Duration.ofMillis(3)).before(Duration.ofMillis(1)).after(Duration.ofMillis(2)).grace(Duration.ofMillis(3)));
        EqualityCheck.verifyInEquality(JoinWindows.of(Duration.ofMillis(3)).before(Duration.ofMillis(1)).after(Duration.ofMillis(2)).grace(Duration.ofMillis(9)), JoinWindows.of(Duration.ofMillis(3)).before(Duration.ofMillis(1)).after(Duration.ofMillis(2)).grace(Duration.ofMillis(3)));
    }
}

