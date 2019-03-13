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


import java.time.Instant;
import java.util.Map;
import org.apache.kafka.streams.EqualityCheck;
import org.apache.kafka.streams.kstream.internals.UnlimitedWindow;
import org.junit.Assert;
import org.junit.Test;


public class UnlimitedWindowsTest {
    private static long anyStartTime = 10L;

    @Test
    public void shouldSetWindowStartTime() {
        Assert.assertEquals(UnlimitedWindowsTest.anyStartTime, UnlimitedWindows.of().startOn(Instant.ofEpochMilli(UnlimitedWindowsTest.anyStartTime)).startMs);
    }

    @Test(expected = IllegalArgumentException.class)
    public void startTimeMustNotBeNegative() {
        UnlimitedWindows.of().startOn(Instant.ofEpochMilli((-1)));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void shouldThrowOnUntil() {
        final UnlimitedWindows windowSpec = UnlimitedWindows.of();
        try {
            windowSpec.until(42);
            Assert.fail("should not allow to set window retention time");
        } catch (final IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void shouldIncludeRecordsThatHappenedOnWindowStart() {
        final UnlimitedWindows w = UnlimitedWindows.of().startOn(Instant.ofEpochMilli(UnlimitedWindowsTest.anyStartTime));
        final Map<Long, UnlimitedWindow> matchedWindows = w.windowsFor(w.startMs);
        Assert.assertEquals(1, matchedWindows.size());
        Assert.assertEquals(new UnlimitedWindow(UnlimitedWindowsTest.anyStartTime), matchedWindows.get(UnlimitedWindowsTest.anyStartTime));
    }

    @Test
    public void shouldIncludeRecordsThatHappenedAfterWindowStart() {
        final UnlimitedWindows w = UnlimitedWindows.of().startOn(Instant.ofEpochMilli(UnlimitedWindowsTest.anyStartTime));
        final long timestamp = (w.startMs) + 1;
        final Map<Long, UnlimitedWindow> matchedWindows = w.windowsFor(timestamp);
        Assert.assertEquals(1, matchedWindows.size());
        Assert.assertEquals(new UnlimitedWindow(UnlimitedWindowsTest.anyStartTime), matchedWindows.get(UnlimitedWindowsTest.anyStartTime));
    }

    @Test
    public void shouldExcludeRecordsThatHappenedBeforeWindowStart() {
        final UnlimitedWindows w = UnlimitedWindows.of().startOn(Instant.ofEpochMilli(UnlimitedWindowsTest.anyStartTime));
        final long timestamp = (w.startMs) - 1;
        final Map<Long, UnlimitedWindow> matchedWindows = w.windowsFor(timestamp);
        Assert.assertTrue(matchedWindows.isEmpty());
    }

    @Test
    public void equalsAndHashcodeShouldBeValidForPositiveCases() {
        EqualityCheck.verifyEquality(UnlimitedWindows.of(), UnlimitedWindows.of());
        EqualityCheck.verifyEquality(UnlimitedWindows.of().startOn(Instant.ofEpochMilli(1)), UnlimitedWindows.of().startOn(Instant.ofEpochMilli(1)));
    }

    @Test
    public void equalsAndHashcodeShouldBeValidForNegativeCases() {
        EqualityCheck.verifyInEquality(UnlimitedWindows.of().startOn(Instant.ofEpochMilli(9)), UnlimitedWindows.of().startOn(Instant.ofEpochMilli(1)));
    }
}

