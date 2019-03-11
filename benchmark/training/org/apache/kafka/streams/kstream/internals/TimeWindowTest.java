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
package org.apache.kafka.streams.kstream.internals;


import java.time.Duration;
import java.util.Map;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.junit.Assert;
import org.junit.Test;


public class TimeWindowTest {
    private long start = 50;

    private long end = 100;

    private final TimeWindow window = new TimeWindow(start, end);

    private final SessionWindow sessionWindow = new SessionWindow(start, end);

    @Test(expected = IllegalArgumentException.class)
    public void endMustBeLargerThanStart() {
        new TimeWindow(start, start);
    }

    @Test
    public void shouldNotOverlapIfOtherWindowIsBeforeThisWindow() {
        /* This:        [-------)
        Other: [-----)
         */
        Assert.assertFalse(window.overlap(new TimeWindow(0, 25)));
        Assert.assertFalse(window.overlap(new TimeWindow(0, ((start) - 1))));
        Assert.assertFalse(window.overlap(new TimeWindow(0, start)));
    }

    @Test
    public void shouldOverlapIfOtherWindowEndIsWithinThisWindow() {
        /* This:        [-------)
        Other: [---------)
         */
        Assert.assertTrue(window.overlap(new TimeWindow(0, ((start) + 1))));
        Assert.assertTrue(window.overlap(new TimeWindow(0, 75)));
        Assert.assertTrue(window.overlap(new TimeWindow(0, ((end) - 1))));
        Assert.assertTrue(window.overlap(new TimeWindow(((start) - 1), ((start) + 1))));
        Assert.assertTrue(window.overlap(new TimeWindow(((start) - 1), 75)));
        Assert.assertTrue(window.overlap(new TimeWindow(((start) - 1), ((end) - 1))));
    }

    @Test
    public void shouldOverlapIfOtherWindowContainsThisWindow() {
        /* This:        [-------)
        Other: [------------------)
         */
        Assert.assertTrue(window.overlap(new TimeWindow(0, end)));
        Assert.assertTrue(window.overlap(new TimeWindow(0, ((end) + 1))));
        Assert.assertTrue(window.overlap(new TimeWindow(0, 150)));
        Assert.assertTrue(window.overlap(new TimeWindow(((start) - 1), end)));
        Assert.assertTrue(window.overlap(new TimeWindow(((start) - 1), ((end) + 1))));
        Assert.assertTrue(window.overlap(new TimeWindow(((start) - 1), 150)));
        Assert.assertTrue(window.overlap(new TimeWindow(start, end)));
        Assert.assertTrue(window.overlap(new TimeWindow(start, ((end) + 1))));
        Assert.assertTrue(window.overlap(new TimeWindow(start, 150)));
    }

    @Test
    public void shouldOverlapIfOtherWindowIsWithinThisWindow() {
        /* This:        [-------)
        Other:         [---)
         */
        Assert.assertTrue(window.overlap(new TimeWindow(start, 75)));
        Assert.assertTrue(window.overlap(new TimeWindow(start, end)));
        Assert.assertTrue(window.overlap(new TimeWindow(75, end)));
    }

    @Test
    public void shouldOverlapIfOtherWindowStartIsWithinThisWindow() {
        /* This:        [-------)
        Other:           [-------)
         */
        Assert.assertTrue(window.overlap(new TimeWindow(start, ((end) + 1))));
        Assert.assertTrue(window.overlap(new TimeWindow(start, 150)));
        Assert.assertTrue(window.overlap(new TimeWindow(75, ((end) + 1))));
        Assert.assertTrue(window.overlap(new TimeWindow(75, 150)));
    }

    @Test
    public void shouldNotOverlapIsOtherWindowIsAfterThisWindow() {
        /* This:        [-------)
        Other:               [------)
         */
        Assert.assertFalse(window.overlap(new TimeWindow(end, ((end) + 1))));
        Assert.assertFalse(window.overlap(new TimeWindow(end, 150)));
        Assert.assertFalse(window.overlap(new TimeWindow(((end) + 1), 150)));
        Assert.assertFalse(window.overlap(new TimeWindow(125, 150)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotCompareTimeWindowWithDifferentWindowType() {
        window.overlap(sessionWindow);
    }

    @Test
    public void shouldReturnMatchedWindowsOrderedByTimestamp() {
        final TimeWindows windows = TimeWindows.of(Duration.ofMillis(12L)).advanceBy(Duration.ofMillis(5L));
        final Map<Long, TimeWindow> matched = windows.windowsFor(21L);
        final Long[] expected = matched.keySet().toArray(new Long[matched.size()]);
        Assert.assertEquals(expected[0].longValue(), 10L);
        Assert.assertEquals(expected[1].longValue(), 15L);
        Assert.assertEquals(expected[2].longValue(), 20L);
    }
}

