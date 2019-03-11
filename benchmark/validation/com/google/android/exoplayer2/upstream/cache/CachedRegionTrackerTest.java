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
package com.google.android.exoplayer2.upstream.cache;


import CachedRegionTracker.CACHED_TO_END;
import CachedRegionTracker.NOT_CACHED;
import com.google.android.exoplayer2.extractor.ChunkIndex;
import java.io.File;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests for {@link CachedRegionTracker}.
 */
@RunWith(RobolectricTestRunner.class)
public final class CachedRegionTrackerTest {
    private static final String CACHE_KEY = "abc";

    private static final long MS_IN_US = 1000;

    // 5 chunks, each 20 bytes long and 100 ms long.
    private static final ChunkIndex CHUNK_INDEX = new ChunkIndex(new int[]{ 20, 20, 20, 20, 20 }, new long[]{ 100, 120, 140, 160, 180 }, new long[]{ 100 * (CachedRegionTrackerTest.MS_IN_US), 100 * (CachedRegionTrackerTest.MS_IN_US), 100 * (CachedRegionTrackerTest.MS_IN_US), 100 * (CachedRegionTrackerTest.MS_IN_US), 100 * (CachedRegionTrackerTest.MS_IN_US) }, new long[]{ 0, 100 * (CachedRegionTrackerTest.MS_IN_US), 200 * (CachedRegionTrackerTest.MS_IN_US), 300 * (CachedRegionTrackerTest.MS_IN_US), 400 * (CachedRegionTrackerTest.MS_IN_US) });

    @Mock
    private Cache cache;

    private CachedRegionTracker tracker;

    private CachedContentIndex index;

    private File cacheDir;

    @Test
    public void testGetRegion_noSpansInCache() {
        assertThat(tracker.getRegionEndTimeMs(100)).isEqualTo(NOT_CACHED);
        assertThat(tracker.getRegionEndTimeMs(150)).isEqualTo(NOT_CACHED);
    }

    @Test
    public void testGetRegion_fullyCached() throws Exception {
        tracker.onSpanAdded(cache, newCacheSpan(100, 100));
        assertThat(tracker.getRegionEndTimeMs(101)).isEqualTo(CACHED_TO_END);
        assertThat(tracker.getRegionEndTimeMs(121)).isEqualTo(CACHED_TO_END);
    }

    @Test
    public void testGetRegion_partiallyCached() throws Exception {
        tracker.onSpanAdded(cache, newCacheSpan(100, 40));
        assertThat(tracker.getRegionEndTimeMs(101)).isEqualTo(200);
        assertThat(tracker.getRegionEndTimeMs(121)).isEqualTo(200);
    }

    @Test
    public void testGetRegion_multipleSpanAddsJoinedCorrectly() throws Exception {
        tracker.onSpanAdded(cache, newCacheSpan(100, 20));
        tracker.onSpanAdded(cache, newCacheSpan(120, 20));
        assertThat(tracker.getRegionEndTimeMs(101)).isEqualTo(200);
        assertThat(tracker.getRegionEndTimeMs(121)).isEqualTo(200);
    }

    @Test
    public void testGetRegion_fullyCachedThenPartiallyRemoved() throws Exception {
        // Start with the full stream in cache.
        tracker.onSpanAdded(cache, newCacheSpan(100, 100));
        // Remove the middle bit.
        tracker.onSpanRemoved(cache, newCacheSpan(140, 40));
        assertThat(tracker.getRegionEndTimeMs(101)).isEqualTo(200);
        assertThat(tracker.getRegionEndTimeMs(121)).isEqualTo(200);
        assertThat(tracker.getRegionEndTimeMs(181)).isEqualTo(CACHED_TO_END);
    }

    @Test
    public void testGetRegion_subchunkEstimation() throws Exception {
        tracker.onSpanAdded(cache, newCacheSpan(100, 10));
        assertThat(tracker.getRegionEndTimeMs(101)).isEqualTo(50);
        assertThat(tracker.getRegionEndTimeMs(111)).isEqualTo(NOT_CACHED);
    }
}

