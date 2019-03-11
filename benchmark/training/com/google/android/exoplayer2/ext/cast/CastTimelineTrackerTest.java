/**
 * Copyright (C) 2018 The Android Open Source Project
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
package com.google.android.exoplayer2.ext.cast;


import C.TIME_UNSET;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.testutil.TimelineAsserts;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests for {@link CastTimelineTracker}.
 */
@RunWith(RobolectricTestRunner.class)
public class CastTimelineTrackerTest {
    private static final long DURATION_1_MS = 1000;

    private static final long DURATION_2_MS = 2000;

    private static final long DURATION_3_MS = 3000;

    private static final long DURATION_4_MS = 4000;

    private static final long DURATION_5_MS = 5000;

    /**
     * Tests that duration of the current media info is correctly propagated to the timeline.
     */
    @Test
    public void testGetCastTimeline() {
        MediaInfo mediaInfo;
        MediaStatus status = CastTimelineTrackerTest.mockMediaStatus(new int[]{ 1, 2, 3 }, new String[]{ "contentId1", "contentId2", "contentId3" }, new long[]{ CastTimelineTrackerTest.DURATION_1_MS, MediaInfo.UNKNOWN_DURATION, MediaInfo.UNKNOWN_DURATION });
        CastTimelineTracker tracker = new CastTimelineTracker();
        mediaInfo = CastTimelineTrackerTest.getMediaInfo("contentId1", CastTimelineTrackerTest.DURATION_1_MS);
        Mockito.when(status.getMediaInfo()).thenReturn(mediaInfo);
        TimelineAsserts.assertPeriodDurations(tracker.getCastTimeline(status), C.msToUs(CastTimelineTrackerTest.DURATION_1_MS), TIME_UNSET, TIME_UNSET);
        mediaInfo = CastTimelineTrackerTest.getMediaInfo("contentId3", CastTimelineTrackerTest.DURATION_3_MS);
        Mockito.when(status.getMediaInfo()).thenReturn(mediaInfo);
        TimelineAsserts.assertPeriodDurations(tracker.getCastTimeline(status), C.msToUs(CastTimelineTrackerTest.DURATION_1_MS), TIME_UNSET, C.msToUs(CastTimelineTrackerTest.DURATION_3_MS));
        mediaInfo = CastTimelineTrackerTest.getMediaInfo("contentId2", CastTimelineTrackerTest.DURATION_2_MS);
        Mockito.when(status.getMediaInfo()).thenReturn(mediaInfo);
        TimelineAsserts.assertPeriodDurations(tracker.getCastTimeline(status), C.msToUs(CastTimelineTrackerTest.DURATION_1_MS), C.msToUs(CastTimelineTrackerTest.DURATION_2_MS), C.msToUs(CastTimelineTrackerTest.DURATION_3_MS));
        MediaStatus newStatus = CastTimelineTrackerTest.mockMediaStatus(new int[]{ 4, 1, 5, 3 }, new String[]{ "contentId4", "contentId1", "contentId5", "contentId3" }, new long[]{ MediaInfo.UNKNOWN_DURATION, MediaInfo.UNKNOWN_DURATION, CastTimelineTrackerTest.DURATION_5_MS, MediaInfo.UNKNOWN_DURATION });
        mediaInfo = CastTimelineTrackerTest.getMediaInfo("contentId5", CastTimelineTrackerTest.DURATION_5_MS);
        Mockito.when(newStatus.getMediaInfo()).thenReturn(mediaInfo);
        TimelineAsserts.assertPeriodDurations(tracker.getCastTimeline(newStatus), TIME_UNSET, C.msToUs(CastTimelineTrackerTest.DURATION_1_MS), C.msToUs(CastTimelineTrackerTest.DURATION_5_MS), C.msToUs(CastTimelineTrackerTest.DURATION_3_MS));
        mediaInfo = CastTimelineTrackerTest.getMediaInfo("contentId3", CastTimelineTrackerTest.DURATION_3_MS);
        Mockito.when(newStatus.getMediaInfo()).thenReturn(mediaInfo);
        TimelineAsserts.assertPeriodDurations(tracker.getCastTimeline(newStatus), TIME_UNSET, C.msToUs(CastTimelineTrackerTest.DURATION_1_MS), C.msToUs(CastTimelineTrackerTest.DURATION_5_MS), C.msToUs(CastTimelineTrackerTest.DURATION_3_MS));
        mediaInfo = CastTimelineTrackerTest.getMediaInfo("contentId4", CastTimelineTrackerTest.DURATION_4_MS);
        Mockito.when(newStatus.getMediaInfo()).thenReturn(mediaInfo);
        TimelineAsserts.assertPeriodDurations(tracker.getCastTimeline(newStatus), C.msToUs(CastTimelineTrackerTest.DURATION_4_MS), C.msToUs(CastTimelineTrackerTest.DURATION_1_MS), C.msToUs(CastTimelineTrackerTest.DURATION_5_MS), C.msToUs(CastTimelineTrackerTest.DURATION_3_MS));
    }
}

