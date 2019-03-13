/**
 * Copyright (C) 2017 The Android Open Source Project
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
package com.google.android.exoplayer2.source;


import C.INDEX_UNSET;
import Player.REPEAT_MODE_ALL;
import Player.REPEAT_MODE_OFF;
import Player.REPEAT_MODE_ONE;
import RobolectricUtil.CustomLooper;
import RobolectricUtil.CustomMessageQueue;
import Timeline.EMPTY;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.testutil.FakeTimeline;
import com.google.android.exoplayer2.testutil.TimelineAsserts;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Unit tests for {@link LoopingMediaSource}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(shadows = { CustomLooper.class, CustomMessageQueue.class })
public class LoopingMediaSourceTest {
    private FakeTimeline multiWindowTimeline;

    @Test
    public void testSingleLoopTimeline() throws IOException {
        Timeline timeline = LoopingMediaSourceTest.getLoopingTimeline(multiWindowTimeline, 1);
        TimelineAsserts.assertWindowTags(timeline, 111, 222, 333);
        TimelineAsserts.assertPeriodCounts(timeline, 1, 1, 1);
        for (boolean shuffled : new boolean[]{ false, true }) {
            TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_OFF, shuffled, INDEX_UNSET, 0, 1);
            TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_ONE, shuffled, 0, 1, 2);
            TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_ALL, shuffled, 2, 0, 1);
            TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_OFF, shuffled, 1, 2, INDEX_UNSET);
            TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_ONE, shuffled, 0, 1, 2);
            TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_ALL, shuffled, 1, 2, 0);
        }
    }

    @Test
    public void testMultiLoopTimeline() throws IOException {
        Timeline timeline = LoopingMediaSourceTest.getLoopingTimeline(multiWindowTimeline, 3);
        TimelineAsserts.assertWindowTags(timeline, 111, 222, 333, 111, 222, 333, 111, 222, 333);
        TimelineAsserts.assertPeriodCounts(timeline, 1, 1, 1, 1, 1, 1, 1, 1, 1);
        for (boolean shuffled : new boolean[]{ false, true }) {
            TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_OFF, shuffled, INDEX_UNSET, 0, 1, 2, 3, 4, 5, 6, 7, 8);
            TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_ONE, shuffled, 0, 1, 2, 3, 4, 5, 6, 7, 8);
            TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_ALL, shuffled, 8, 0, 1, 2, 3, 4, 5, 6, 7);
            TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_OFF, shuffled, 1, 2, 3, 4, 5, 6, 7, 8, INDEX_UNSET);
            TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_ONE, shuffled, 0, 1, 2, 3, 4, 5, 6, 7, 8);
            TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_ALL, shuffled, 1, 2, 3, 4, 5, 6, 7, 8, 0);
        }
    }

    @Test
    public void testInfiniteLoopTimeline() throws IOException {
        Timeline timeline = LoopingMediaSourceTest.getLoopingTimeline(multiWindowTimeline, Integer.MAX_VALUE);
        TimelineAsserts.assertWindowTags(timeline, 111, 222, 333);
        TimelineAsserts.assertPeriodCounts(timeline, 1, 1, 1);
        for (boolean shuffled : new boolean[]{ false, true }) {
            TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_OFF, shuffled, 2, 0, 1);
            TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_ONE, shuffled, 0, 1, 2);
            TimelineAsserts.assertPreviousWindowIndices(timeline, REPEAT_MODE_ALL, shuffled, 2, 0, 1);
            TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_OFF, shuffled, 1, 2, 0);
            TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_ONE, shuffled, 0, 1, 2);
            TimelineAsserts.assertNextWindowIndices(timeline, REPEAT_MODE_ALL, shuffled, 1, 2, 0);
        }
    }

    @Test
    public void testEmptyTimelineLoop() throws IOException {
        Timeline timeline = LoopingMediaSourceTest.getLoopingTimeline(EMPTY, 1);
        TimelineAsserts.assertEmpty(timeline);
        timeline = LoopingMediaSourceTest.getLoopingTimeline(EMPTY, 3);
        TimelineAsserts.assertEmpty(timeline);
        timeline = LoopingMediaSourceTest.getLoopingTimeline(EMPTY, Integer.MAX_VALUE);
        TimelineAsserts.assertEmpty(timeline);
    }

    @Test
    public void testSingleLoopPeriodCreation() throws Exception {
        /* loopCount= */
        LoopingMediaSourceTest.testMediaPeriodCreation(multiWindowTimeline, 1);
    }

    @Test
    public void testMultiLoopPeriodCreation() throws Exception {
        /* loopCount= */
        LoopingMediaSourceTest.testMediaPeriodCreation(multiWindowTimeline, 3);
    }

    @Test
    public void testInfiniteLoopPeriodCreation() throws Exception {
        /* loopCount= */
        LoopingMediaSourceTest.testMediaPeriodCreation(multiWindowTimeline, Integer.MAX_VALUE);
    }
}

