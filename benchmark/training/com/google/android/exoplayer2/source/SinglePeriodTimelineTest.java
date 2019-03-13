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
import C.TIME_UNSET;
import android.util.Pair;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Timeline.Period;
import com.google.android.exoplayer2.Timeline.Window;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit test for {@link SinglePeriodTimeline}.
 */
@RunWith(RobolectricTestRunner.class)
public final class SinglePeriodTimelineTest {
    private Window window;

    private Period period;

    @Test
    public void testGetPeriodPositionDynamicWindowUnknownDuration() {
        SinglePeriodTimeline timeline = new SinglePeriodTimeline(C.TIME_UNSET, false, true);
        // Should return null with any positive position projection.
        Pair<Object, Long> position = timeline.getPeriodPosition(window, period, 0, TIME_UNSET, 1);
        assertThat(position).isNull();
        // Should return (0, 0) without a position projection.
        position = timeline.getPeriodPosition(window, period, 0, TIME_UNSET, 0);
        assertThat(position.first).isEqualTo(timeline.getUidOfPeriod(0));
        assertThat(position.second).isEqualTo(0);
    }

    @Test
    public void testGetPeriodPositionDynamicWindowKnownDuration() {
        long windowDurationUs = 1000;
        SinglePeriodTimeline timeline = /* windowPositionInPeriodUs= */
        /* windowDefaultStartPositionUs= */
        /* isSeekable= */
        /* isDynamic= */
        /* tag= */
        new SinglePeriodTimeline(windowDurationUs, windowDurationUs, 0, 0, false, true, null);
        // Should return null with a positive position projection beyond window duration.
        Pair<Object, Long> position = timeline.getPeriodPosition(window, period, 0, TIME_UNSET, (windowDurationUs + 1));
        assertThat(position).isNull();
        // Should return (0, duration) with a projection equal to window duration.
        position = timeline.getPeriodPosition(window, period, 0, TIME_UNSET, windowDurationUs);
        assertThat(position.first).isEqualTo(timeline.getUidOfPeriod(0));
        assertThat(position.second).isEqualTo(windowDurationUs);
        // Should return (0, 0) without a position projection.
        position = timeline.getPeriodPosition(window, period, 0, TIME_UNSET, 0);
        assertThat(position.first).isEqualTo(timeline.getUidOfPeriod(0));
        assertThat(position.second).isEqualTo(0);
    }

    @Test
    public void setNullTag_returnsNullTag_butUsesDefaultUid() {
        SinglePeriodTimeline timeline = /* durationUs= */
        /* isSeekable= */
        /* isDynamic= */
        /* tag= */
        new SinglePeriodTimeline(C.TIME_UNSET, false, false, null);
        assertThat(/* windowIndex= */
        /* setTag= */
        timeline.getWindow(0, window, false).tag).isNull();
        assertThat(/* windowIndex= */
        /* setTag= */
        timeline.getWindow(0, window, true).tag).isNull();
        assertThat(/* periodIndex= */
        /* setIds= */
        timeline.getPeriod(0, period, false).id).isNull();
        assertThat(/* periodIndex= */
        /* setIds= */
        timeline.getPeriod(0, period, true).id).isNull();
        assertThat(/* periodIndex= */
        /* setIds= */
        timeline.getPeriod(0, period, false).uid).isNull();
        assertThat(/* periodIndex= */
        /* setIds= */
        timeline.getPeriod(0, period, true).uid).isNotNull();
    }

    @Test
    public void setTag_isUsedForWindowTag() {
        Object tag = new Object();
        SinglePeriodTimeline timeline = /* durationUs= */
        /* isSeekable= */
        /* isDynamic= */
        new SinglePeriodTimeline(C.TIME_UNSET, false, false, tag);
        assertThat(/* windowIndex= */
        /* setTag= */
        timeline.getWindow(0, window, false).tag).isNull();
        assertThat(/* windowIndex= */
        /* setTag= */
        timeline.getWindow(0, window, true).tag).isEqualTo(tag);
    }

    @Test
    public void getIndexOfPeriod_returnsPeriod() {
        SinglePeriodTimeline timeline = /* durationUs= */
        /* isSeekable= */
        /* isDynamic= */
        /* tag= */
        new SinglePeriodTimeline(C.TIME_UNSET, false, false, null);
        Object uid = /* periodIndex= */
        /* setIds= */
        timeline.getPeriod(0, period, true).uid;
        assertThat(timeline.getIndexOfPeriod(uid)).isEqualTo(0);
        assertThat(/* uid= */
        timeline.getIndexOfPeriod(null)).isEqualTo(INDEX_UNSET);
        assertThat(/* uid= */
        timeline.getIndexOfPeriod(new Object())).isEqualTo(INDEX_UNSET);
    }
}

