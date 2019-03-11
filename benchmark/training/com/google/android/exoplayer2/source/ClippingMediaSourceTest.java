/**
 * Copyright (C) 2016 The Android Open Source Project
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
import C.TIME_END_OF_SOURCE;
import C.TIME_UNSET;
import IllegalClippingException.REASON_NOT_SEEKABLE_TO_START;
import Player.REPEAT_MODE_ALL;
import Player.REPEAT_MODE_OFF;
import Player.REPEAT_MODE_ONE;
import RobolectricUtil.CustomLooper;
import RobolectricUtil.CustomMessageQueue;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Timeline.Period;
import com.google.android.exoplayer2.Timeline.Window;
import com.google.android.exoplayer2.source.ClippingMediaSource.IllegalClippingException;
import com.google.android.exoplayer2.source.MediaSourceEventListener.MediaLoadData;
import com.google.android.exoplayer2.testutil.FakeTimeline.TimelineWindowDefinition;
import com.google.android.exoplayer2.testutil.TimelineAsserts;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Unit tests for {@link ClippingMediaSource}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(shadows = { CustomLooper.class, CustomMessageQueue.class })
public final class ClippingMediaSourceTest {
    private static final long TEST_PERIOD_DURATION_US = 1000000;

    private static final long TEST_CLIP_AMOUNT_US = 300000;

    private Window window;

    private Period period;

    @Test
    public void testNoClipping() throws IOException {
        Timeline timeline = new SinglePeriodTimeline(ClippingMediaSourceTest.TEST_PERIOD_DURATION_US, true, false);
        Timeline clippedTimeline = ClippingMediaSourceTest.getClippedTimeline(timeline, 0, ClippingMediaSourceTest.TEST_PERIOD_DURATION_US);
        assertThat(clippedTimeline.getWindowCount()).isEqualTo(1);
        assertThat(clippedTimeline.getPeriodCount()).isEqualTo(1);
        assertThat(clippedTimeline.getWindow(0, window).getDurationUs()).isEqualTo(ClippingMediaSourceTest.TEST_PERIOD_DURATION_US);
        assertThat(clippedTimeline.getPeriod(0, period).getDurationUs()).isEqualTo(ClippingMediaSourceTest.TEST_PERIOD_DURATION_US);
    }

    @Test
    public void testClippingUnseekableWindowThrows() throws IOException {
        Timeline timeline = new SinglePeriodTimeline(ClippingMediaSourceTest.TEST_PERIOD_DURATION_US, false, false);
        // If the unseekable window isn't clipped, clipping succeeds.
        ClippingMediaSourceTest.getClippedTimeline(timeline, 0, ClippingMediaSourceTest.TEST_PERIOD_DURATION_US);
        try {
            // If the unseekable window is clipped, clipping fails.
            ClippingMediaSourceTest.getClippedTimeline(timeline, 1, ClippingMediaSourceTest.TEST_PERIOD_DURATION_US);
            Assert.fail("Expected clipping to fail.");
        } catch (IllegalClippingException e) {
            assertThat(e.reason).isEqualTo(REASON_NOT_SEEKABLE_TO_START);
        }
    }

    @Test
    public void testClippingStart() throws IOException {
        Timeline timeline = new SinglePeriodTimeline(ClippingMediaSourceTest.TEST_PERIOD_DURATION_US, true, false);
        Timeline clippedTimeline = ClippingMediaSourceTest.getClippedTimeline(timeline, ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US, ClippingMediaSourceTest.TEST_PERIOD_DURATION_US);
        assertThat(clippedTimeline.getWindow(0, window).getDurationUs()).isEqualTo(((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) - (ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US)));
        assertThat(clippedTimeline.getPeriod(0, period).getDurationUs()).isEqualTo(ClippingMediaSourceTest.TEST_PERIOD_DURATION_US);
    }

    @Test
    public void testClippingEnd() throws IOException {
        Timeline timeline = new SinglePeriodTimeline(ClippingMediaSourceTest.TEST_PERIOD_DURATION_US, true, false);
        Timeline clippedTimeline = ClippingMediaSourceTest.getClippedTimeline(timeline, 0, ((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) - (ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US)));
        assertThat(clippedTimeline.getWindow(0, window).getDurationUs()).isEqualTo(((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) - (ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US)));
        assertThat(clippedTimeline.getPeriod(0, period).getDurationUs()).isEqualTo(((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) - (ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US)));
    }

    @Test
    public void testClippingStartAndEndInitial() throws IOException {
        // Timeline that's dynamic and not seekable. A child source might report such a timeline prior
        // to it having loaded sufficient data to establish its duration and seekability. Such timelines
        // should not result in clipping failure.
        Timeline timeline = /* isSeekable= */
        /* isDynamic= */
        new SinglePeriodTimeline(C.TIME_UNSET, false, true);
        Timeline clippedTimeline = ClippingMediaSourceTest.getClippedTimeline(timeline, ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US, ((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) - ((ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US) * 2)));
        assertThat(clippedTimeline.getWindow(0, window).getDurationUs()).isEqualTo(((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) - ((ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US) * 3)));
        assertThat(clippedTimeline.getPeriod(0, period).getDurationUs()).isEqualTo(((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) - ((ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US) * 2)));
    }

    @Test
    public void testClippingToEndOfSourceWithDurationSetsDuration() throws IOException {
        // Create a child timeline that has a known duration.
        Timeline timeline = /* durationUs= */
        /* isSeekable= */
        /* isDynamic= */
        new SinglePeriodTimeline(ClippingMediaSourceTest.TEST_PERIOD_DURATION_US, true, false);
        // When clipping to the end, the clipped timeline should also have a duration.
        Timeline clippedTimeline = ClippingMediaSourceTest.getClippedTimeline(timeline, ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US, TIME_END_OF_SOURCE);
        assertThat(/* windowIndex= */
        clippedTimeline.getWindow(0, window).getDurationUs()).isEqualTo(((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) - (ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US)));
    }

    @Test
    public void testClippingToEndOfSourceWithUnsetDurationDoesNotSetDuration() throws IOException {
        // Create a child timeline that has an unknown duration.
        Timeline timeline = /* durationUs= */
        /* isSeekable= */
        /* isDynamic= */
        new SinglePeriodTimeline(C.TIME_UNSET, true, false);
        // When clipping to the end, the clipped timeline should also have an unset duration.
        Timeline clippedTimeline = ClippingMediaSourceTest.getClippedTimeline(timeline, ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US, TIME_END_OF_SOURCE);
        assertThat(/* windowIndex= */
        clippedTimeline.getWindow(0, window).getDurationUs()).isEqualTo(TIME_UNSET);
    }

    @Test
    public void testClippingStartAndEnd() throws IOException {
        Timeline timeline = new SinglePeriodTimeline(ClippingMediaSourceTest.TEST_PERIOD_DURATION_US, true, false);
        Timeline clippedTimeline = ClippingMediaSourceTest.getClippedTimeline(timeline, ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US, ((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) - ((ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US) * 2)));
        assertThat(clippedTimeline.getWindow(0, window).getDurationUs()).isEqualTo(((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) - ((ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US) * 3)));
        assertThat(clippedTimeline.getPeriod(0, period).getDurationUs()).isEqualTo(((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) - ((ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US) * 2)));
    }

    @Test
    public void testClippingFromDefaultPosition() throws IOException {
        Timeline timeline = /* periodDurationUs= */
        /* windowDurationUs= */
        /* windowPositionInPeriodUs= */
        /* windowDefaultStartPositionUs= */
        /* isSeekable= */
        /* isDynamic= */
        /* tag= */
        new SinglePeriodTimeline((3 * (ClippingMediaSourceTest.TEST_PERIOD_DURATION_US)), ClippingMediaSourceTest.TEST_PERIOD_DURATION_US, ClippingMediaSourceTest.TEST_PERIOD_DURATION_US, ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US, true, true, null);
        Timeline clippedTimeline = /* durationUs= */
        ClippingMediaSourceTest.getClippedTimeline(timeline, ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US);
        assertThat(clippedTimeline.getWindow(0, window).getDurationUs()).isEqualTo(ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US);
        assertThat(clippedTimeline.getWindow(0, window).getDefaultPositionUs()).isEqualTo(0);
        assertThat(clippedTimeline.getWindow(0, window).getPositionInFirstPeriodUs()).isEqualTo(((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) + (ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US)));
        assertThat(clippedTimeline.getPeriod(0, period).getDurationUs()).isEqualTo(((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) + (2 * (ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US))));
    }

    @Test
    public void testAllowDynamicUpdatesWithOverlappingLiveWindow() throws IOException {
        Timeline timeline1 = /* periodDurationUs= */
        /* windowDurationUs= */
        /* windowPositionInPeriodUs= */
        /* windowDefaultStartPositionUs= */
        /* isSeekable= */
        /* isDynamic= */
        /* tag= */
        new SinglePeriodTimeline((2 * (ClippingMediaSourceTest.TEST_PERIOD_DURATION_US)), ClippingMediaSourceTest.TEST_PERIOD_DURATION_US, ClippingMediaSourceTest.TEST_PERIOD_DURATION_US, ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US, true, true, null);
        Timeline timeline2 = /* periodDurationUs= */
        /* windowDurationUs= */
        /* windowPositionInPeriodUs= */
        /* windowDefaultStartPositionUs= */
        /* isSeekable= */
        /* isDynamic= */
        /* tag= */
        new SinglePeriodTimeline((3 * (ClippingMediaSourceTest.TEST_PERIOD_DURATION_US)), ClippingMediaSourceTest.TEST_PERIOD_DURATION_US, (2 * (ClippingMediaSourceTest.TEST_PERIOD_DURATION_US)), ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US, true, true, null);
        Timeline[] clippedTimelines = /* startUs= */
        /* endUs= */
        /* allowDynamicUpdates= */
        /* fromDefaultPosition= */
        ClippingMediaSourceTest.getClippedTimelines(0, ClippingMediaSourceTest.TEST_PERIOD_DURATION_US, true, true, timeline1, timeline2);
        assertThat(clippedTimelines[0].getWindow(0, window).getDurationUs()).isEqualTo(((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) - (ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US)));
        assertThat(clippedTimelines[0].getWindow(0, window).getDefaultPositionUs()).isEqualTo(0);
        assertThat(clippedTimelines[0].getWindow(0, window).isDynamic).isTrue();
        assertThat(clippedTimelines[0].getWindow(0, window).getPositionInFirstPeriodUs()).isEqualTo(((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) + (ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US)));
        assertThat(clippedTimelines[0].getPeriod(0, period).getDurationUs()).isEqualTo((2 * (ClippingMediaSourceTest.TEST_PERIOD_DURATION_US)));
        assertThat(clippedTimelines[1].getWindow(0, window).getDurationUs()).isEqualTo(((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) - (ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US)));
        assertThat(clippedTimelines[1].getWindow(0, window).getDefaultPositionUs()).isEqualTo(0);
        assertThat(clippedTimelines[1].getWindow(0, window).isDynamic).isTrue();
        assertThat(clippedTimelines[1].getWindow(0, window).getPositionInFirstPeriodUs()).isEqualTo(((2 * (ClippingMediaSourceTest.TEST_PERIOD_DURATION_US)) + (ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US)));
        assertThat(clippedTimelines[1].getPeriod(0, period).getDurationUs()).isEqualTo((3 * (ClippingMediaSourceTest.TEST_PERIOD_DURATION_US)));
    }

    @Test
    public void testAllowDynamicUpdatesWithNonOverlappingLiveWindow() throws IOException {
        Timeline timeline1 = /* periodDurationUs= */
        /* windowDurationUs= */
        /* windowPositionInPeriodUs= */
        /* windowDefaultStartPositionUs= */
        /* isSeekable= */
        /* isDynamic= */
        /* tag= */
        new SinglePeriodTimeline((2 * (ClippingMediaSourceTest.TEST_PERIOD_DURATION_US)), ClippingMediaSourceTest.TEST_PERIOD_DURATION_US, ClippingMediaSourceTest.TEST_PERIOD_DURATION_US, ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US, true, true, null);
        Timeline timeline2 = /* periodDurationUs= */
        /* windowDurationUs= */
        /* windowPositionInPeriodUs= */
        /* windowDefaultStartPositionUs= */
        /* isSeekable= */
        /* isDynamic= */
        /* tag= */
        new SinglePeriodTimeline((4 * (ClippingMediaSourceTest.TEST_PERIOD_DURATION_US)), ClippingMediaSourceTest.TEST_PERIOD_DURATION_US, (3 * (ClippingMediaSourceTest.TEST_PERIOD_DURATION_US)), ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US, true, true, null);
        Timeline[] clippedTimelines = /* startUs= */
        /* endUs= */
        /* allowDynamicUpdates= */
        /* fromDefaultPosition= */
        ClippingMediaSourceTest.getClippedTimelines(0, ClippingMediaSourceTest.TEST_PERIOD_DURATION_US, true, true, timeline1, timeline2);
        assertThat(clippedTimelines[0].getWindow(0, window).getDurationUs()).isEqualTo(((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) - (ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US)));
        assertThat(clippedTimelines[0].getWindow(0, window).getDefaultPositionUs()).isEqualTo(0);
        assertThat(clippedTimelines[0].getWindow(0, window).isDynamic).isTrue();
        assertThat(clippedTimelines[0].getWindow(0, window).getPositionInFirstPeriodUs()).isEqualTo(((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) + (ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US)));
        assertThat(clippedTimelines[0].getPeriod(0, period).getDurationUs()).isEqualTo((2 * (ClippingMediaSourceTest.TEST_PERIOD_DURATION_US)));
        assertThat(clippedTimelines[1].getWindow(0, window).getDurationUs()).isEqualTo(((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) - (ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US)));
        assertThat(clippedTimelines[1].getWindow(0, window).getDefaultPositionUs()).isEqualTo(0);
        assertThat(clippedTimelines[1].getWindow(0, window).isDynamic).isTrue();
        assertThat(clippedTimelines[1].getWindow(0, window).getPositionInFirstPeriodUs()).isEqualTo(((3 * (ClippingMediaSourceTest.TEST_PERIOD_DURATION_US)) + (ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US)));
        assertThat(clippedTimelines[1].getPeriod(0, period).getDurationUs()).isEqualTo((4 * (ClippingMediaSourceTest.TEST_PERIOD_DURATION_US)));
    }

    @Test
    public void testDisallowDynamicUpdatesWithOverlappingLiveWindow() throws IOException {
        Timeline timeline1 = /* periodDurationUs= */
        /* windowDurationUs= */
        /* windowPositionInPeriodUs= */
        /* windowDefaultStartPositionUs= */
        /* isSeekable= */
        /* isDynamic= */
        /* tag= */
        new SinglePeriodTimeline((2 * (ClippingMediaSourceTest.TEST_PERIOD_DURATION_US)), ClippingMediaSourceTest.TEST_PERIOD_DURATION_US, ClippingMediaSourceTest.TEST_PERIOD_DURATION_US, ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US, true, true, null);
        Timeline timeline2 = /* periodDurationUs= */
        /* windowDurationUs= */
        /* windowPositionInPeriodUs= */
        /* windowDefaultStartPositionUs= */
        /* isSeekable= */
        /* isDynamic= */
        /* tag= */
        new SinglePeriodTimeline((3 * (ClippingMediaSourceTest.TEST_PERIOD_DURATION_US)), ClippingMediaSourceTest.TEST_PERIOD_DURATION_US, (2 * (ClippingMediaSourceTest.TEST_PERIOD_DURATION_US)), ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US, true, true, null);
        Timeline[] clippedTimelines = /* startUs= */
        /* endUs= */
        /* allowDynamicUpdates= */
        /* fromDefaultPosition= */
        ClippingMediaSourceTest.getClippedTimelines(0, ClippingMediaSourceTest.TEST_PERIOD_DURATION_US, false, true, timeline1, timeline2);
        assertThat(clippedTimelines[0].getWindow(0, window).getDurationUs()).isEqualTo(((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) - (ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US)));
        assertThat(clippedTimelines[0].getWindow(0, window).getDefaultPositionUs()).isEqualTo(0);
        assertThat(clippedTimelines[0].getWindow(0, window).isDynamic).isTrue();
        assertThat(clippedTimelines[0].getWindow(0, window).getPositionInFirstPeriodUs()).isEqualTo(((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) + (ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US)));
        assertThat(clippedTimelines[0].getPeriod(0, period).getDurationUs()).isEqualTo((2 * (ClippingMediaSourceTest.TEST_PERIOD_DURATION_US)));
        assertThat(clippedTimelines[1].getWindow(0, window).getDurationUs()).isEqualTo(ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US);
        assertThat(clippedTimelines[1].getWindow(0, window).getDefaultPositionUs()).isEqualTo(ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US);
        assertThat(clippedTimelines[1].getWindow(0, window).isDynamic).isFalse();
        assertThat(clippedTimelines[1].getWindow(0, window).getPositionInFirstPeriodUs()).isEqualTo((2 * (ClippingMediaSourceTest.TEST_PERIOD_DURATION_US)));
        assertThat(clippedTimelines[1].getPeriod(0, period).getDurationUs()).isEqualTo(((2 * (ClippingMediaSourceTest.TEST_PERIOD_DURATION_US)) + (ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US)));
    }

    @Test
    public void testDisallowDynamicUpdatesWithNonOverlappingLiveWindow() throws IOException {
        Timeline timeline1 = /* periodDurationUs= */
        /* windowDurationUs= */
        /* windowPositionInPeriodUs= */
        /* windowDefaultStartPositionUs= */
        /* isSeekable= */
        /* isDynamic= */
        /* tag= */
        new SinglePeriodTimeline((2 * (ClippingMediaSourceTest.TEST_PERIOD_DURATION_US)), ClippingMediaSourceTest.TEST_PERIOD_DURATION_US, ClippingMediaSourceTest.TEST_PERIOD_DURATION_US, ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US, true, true, null);
        Timeline timeline2 = /* periodDurationUs= */
        /* windowDurationUs= */
        /* windowPositionInPeriodUs= */
        /* windowDefaultStartPositionUs= */
        /* isSeekable= */
        /* isDynamic= */
        /* tag= */
        new SinglePeriodTimeline((4 * (ClippingMediaSourceTest.TEST_PERIOD_DURATION_US)), ClippingMediaSourceTest.TEST_PERIOD_DURATION_US, (3 * (ClippingMediaSourceTest.TEST_PERIOD_DURATION_US)), ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US, true, true, null);
        Timeline[] clippedTimelines = /* startUs= */
        /* endUs= */
        /* allowDynamicUpdates= */
        /* fromDefaultPosition= */
        ClippingMediaSourceTest.getClippedTimelines(0, ClippingMediaSourceTest.TEST_PERIOD_DURATION_US, false, true, timeline1, timeline2);
        assertThat(clippedTimelines[0].getWindow(0, window).getDurationUs()).isEqualTo(((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) - (ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US)));
        assertThat(clippedTimelines[0].getWindow(0, window).getDefaultPositionUs()).isEqualTo(0);
        assertThat(clippedTimelines[0].getWindow(0, window).isDynamic).isTrue();
        assertThat(clippedTimelines[0].getWindow(0, window).getPositionInFirstPeriodUs()).isEqualTo(((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) + (ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US)));
        assertThat(clippedTimelines[0].getPeriod(0, period).getDurationUs()).isEqualTo((2 * (ClippingMediaSourceTest.TEST_PERIOD_DURATION_US)));
        assertThat(clippedTimelines[1].getWindow(0, window).getDurationUs()).isEqualTo(0);
        assertThat(clippedTimelines[1].getWindow(0, window).getDefaultPositionUs()).isEqualTo(0);
        assertThat(clippedTimelines[1].getWindow(0, window).isDynamic).isFalse();
        assertThat(clippedTimelines[1].getWindow(0, window).getPositionInFirstPeriodUs()).isEqualTo((3 * (ClippingMediaSourceTest.TEST_PERIOD_DURATION_US)));
        assertThat(clippedTimelines[1].getPeriod(0, period).getDurationUs()).isEqualTo((3 * (ClippingMediaSourceTest.TEST_PERIOD_DURATION_US)));
    }

    @Test
    public void testWindowAndPeriodIndices() throws IOException {
        Timeline timeline = new com.google.android.exoplayer2.testutil.FakeTimeline(new TimelineWindowDefinition(1, 111, true, false, ClippingMediaSourceTest.TEST_PERIOD_DURATION_US));
        Timeline clippedTimeline = ClippingMediaSourceTest.getClippedTimeline(timeline, ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US, ((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) - (ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US)));
        TimelineAsserts.assertWindowTags(clippedTimeline, 111);
        TimelineAsserts.assertPeriodCounts(clippedTimeline, 1);
        TimelineAsserts.assertPreviousWindowIndices(clippedTimeline, REPEAT_MODE_OFF, false, INDEX_UNSET);
        TimelineAsserts.assertPreviousWindowIndices(clippedTimeline, REPEAT_MODE_ONE, false, 0);
        TimelineAsserts.assertPreviousWindowIndices(clippedTimeline, REPEAT_MODE_ALL, false, 0);
        TimelineAsserts.assertNextWindowIndices(clippedTimeline, REPEAT_MODE_OFF, false, INDEX_UNSET);
        TimelineAsserts.assertNextWindowIndices(clippedTimeline, REPEAT_MODE_ONE, false, 0);
        TimelineAsserts.assertNextWindowIndices(clippedTimeline, REPEAT_MODE_ALL, false, 0);
    }

    @Test
    public void testEventTimeWithinClippedRange() throws IOException {
        MediaLoadData mediaLoadData = /* clippingStartUs= */
        /* clippingEndUs= */
        /* eventStartUs= */
        /* eventEndUs= */
        ClippingMediaSourceTest.getClippingMediaSourceMediaLoadData(ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US, ((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) - (ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US)), ((ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US) + 1000), (((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) - (ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US)) - 1000));
        assertThat(C.msToUs(mediaLoadData.mediaStartTimeMs)).isEqualTo(1000);
        assertThat(C.msToUs(mediaLoadData.mediaEndTimeMs)).isEqualTo((((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) - (2 * (ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US))) - 1000));
    }

    @Test
    public void testEventTimeOutsideClippedRange() throws IOException {
        MediaLoadData mediaLoadData = /* clippingStartUs= */
        /* clippingEndUs= */
        /* eventStartUs= */
        /* eventEndUs= */
        ClippingMediaSourceTest.getClippingMediaSourceMediaLoadData(ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US, ((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) - (ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US)), ((ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US) - 1000), (((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) - (ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US)) + 1000));
        assertThat(C.msToUs(mediaLoadData.mediaStartTimeMs)).isEqualTo(0);
        assertThat(C.msToUs(mediaLoadData.mediaEndTimeMs)).isEqualTo(((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) - (2 * (ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US))));
    }

    @Test
    public void testUnsetEventTime() throws IOException {
        MediaLoadData mediaLoadData = /* clippingStartUs= */
        /* clippingEndUs= */
        /* eventStartUs= */
        /* eventEndUs= */
        ClippingMediaSourceTest.getClippingMediaSourceMediaLoadData(ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US, ((ClippingMediaSourceTest.TEST_PERIOD_DURATION_US) - (ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US)), TIME_UNSET, TIME_UNSET);
        assertThat(C.msToUs(mediaLoadData.mediaStartTimeMs)).isEqualTo(TIME_UNSET);
        assertThat(C.msToUs(mediaLoadData.mediaEndTimeMs)).isEqualTo(TIME_UNSET);
    }

    @Test
    public void testEventTimeWithUnsetDuration() throws IOException {
        MediaLoadData mediaLoadData = /* clippingStartUs= */
        /* clippingEndUs= */
        /* eventStartUs= */
        /* eventEndUs= */
        ClippingMediaSourceTest.getClippingMediaSourceMediaLoadData(ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US, TIME_END_OF_SOURCE, ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US, ((ClippingMediaSourceTest.TEST_CLIP_AMOUNT_US) + 1000000));
        assertThat(C.msToUs(mediaLoadData.mediaStartTimeMs)).isEqualTo(0);
        assertThat(C.msToUs(mediaLoadData.mediaEndTimeMs)).isEqualTo(1000000);
    }
}

