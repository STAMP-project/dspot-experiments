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
public class SessionWindowsTest {
    @Test
    public void shouldSetWindowGap() {
        final long anyGap = 42L;
        Assert.assertEquals(anyGap, SessionWindows.with(Duration.ofMillis(anyGap)).inactivityGap());
    }

    @Test
    public void shouldSetWindowGraceTime() {
        final long anyRetentionTime = 42L;
        Assert.assertEquals(anyRetentionTime, SessionWindows.with(Duration.ofMillis(1)).grace(Duration.ofMillis(anyRetentionTime)).gracePeriodMs());
    }

    @Test
    public void gracePeriodShouldEnforceBoundaries() {
        SessionWindows.with(Duration.ofMillis(3L)).grace(Duration.ofMillis(0));
        try {
            SessionWindows.with(Duration.ofMillis(3L)).grace(Duration.ofMillis((-1L)));
            Assert.fail("should not accept negatives");
        } catch (final IllegalArgumentException e) {
            // expected
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void windowSizeMustNotBeNegative() {
        SessionWindows.with(Duration.ofMillis((-1)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void windowSizeMustNotBeZero() {
        SessionWindows.with(Duration.ofMillis(0));
    }

    // specifically testing deprecated apis
    @SuppressWarnings("deprecation")
    @Test
    public void retentionTimeShouldBeGapIfGapIsLargerThanDefaultRetentionTime() {
        final long windowGap = 2 * (SessionWindows.with(Duration.ofMillis(1)).maintainMs());
        Assert.assertEquals(windowGap, SessionWindows.with(Duration.ofMillis(windowGap)).maintainMs());
    }

    @Deprecated
    @Test
    public void retentionTimeMustNotBeNegative() {
        final SessionWindows windowSpec = SessionWindows.with(Duration.ofMillis(42));
        try {
            windowSpec.until(41);
            Assert.fail("should not accept retention time smaller than gap");
        } catch (final IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void equalsAndHashcodeShouldBeValidForPositiveCases() {
        EqualityCheck.verifyEquality(SessionWindows.with(Duration.ofMillis(1)), SessionWindows.with(Duration.ofMillis(1)));
        EqualityCheck.verifyEquality(SessionWindows.with(Duration.ofMillis(1)).grace(Duration.ofMillis(6)), SessionWindows.with(Duration.ofMillis(1)).grace(Duration.ofMillis(6)));
        EqualityCheck.verifyEquality(SessionWindows.with(Duration.ofMillis(1)).grace(Duration.ofMillis(7)), SessionWindows.with(Duration.ofMillis(1)).grace(Duration.ofMillis(7)));
        EqualityCheck.verifyEquality(SessionWindows.with(Duration.ofMillis(1)).grace(Duration.ofMillis(6)).grace(Duration.ofMillis(7)), SessionWindows.with(Duration.ofMillis(1)).grace(Duration.ofMillis(6)).grace(Duration.ofMillis(7)));
    }

    @Test
    public void equalsAndHashcodeShouldBeValidForNegativeCases() {
        EqualityCheck.verifyInEquality(SessionWindows.with(Duration.ofMillis(9)), SessionWindows.with(Duration.ofMillis(1)));
        EqualityCheck.verifyInEquality(SessionWindows.with(Duration.ofMillis(1)).grace(Duration.ofMillis(9)), SessionWindows.with(Duration.ofMillis(1)).grace(Duration.ofMillis(6)));
        EqualityCheck.verifyInEquality(SessionWindows.with(Duration.ofMillis(1)).grace(Duration.ofMillis(9)), SessionWindows.with(Duration.ofMillis(1)).grace(Duration.ofMillis(7)));
        EqualityCheck.verifyInEquality(SessionWindows.with(Duration.ofMillis(2)).grace(Duration.ofMillis(6)).grace(Duration.ofMillis(7)), SessionWindows.with(Duration.ofMillis(1)).grace(Duration.ofMillis(6)));
        EqualityCheck.verifyInEquality(SessionWindows.with(Duration.ofMillis(1)).grace(Duration.ofMillis(0)).grace(Duration.ofMillis(7)), SessionWindows.with(Duration.ofMillis(1)).grace(Duration.ofMillis(6)));
    }
}

