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
package com.google.android.exoplayer2.trackselection;


import C.SELECTION_REASON_ADAPTIVE;
import C.SELECTION_REASON_INITIAL;
import C.TIME_UNSET;
import android.net.Uri;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import com.google.android.exoplayer2.testutil.FakeClock;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit test for {@link AdaptiveTrackSelection}.
 */
@RunWith(RobolectricTestRunner.class)
public final class AdaptiveTrackSelectionTest {
    private static final MediaChunkIterator[] THREE_EMPTY_MEDIA_CHUNK_ITERATORS = new MediaChunkIterator[]{ MediaChunkIterator.EMPTY, MediaChunkIterator.EMPTY, MediaChunkIterator.EMPTY };

    @Mock
    private BandwidthMeter mockBandwidthMeter;

    private FakeClock fakeClock;

    private AdaptiveTrackSelection adaptiveTrackSelection;

    @Test
    public void testFactoryUsesInitiallyProvidedBandwidthMeter() {
        BandwidthMeter initialBandwidthMeter = Mockito.mock(BandwidthMeter.class);
        BandwidthMeter injectedBandwidthMeter = Mockito.mock(BandwidthMeter.class);
        Format format = /* bitrate= */
        /* width= */
        /* height= */
        AdaptiveTrackSelectionTest.videoFormat(500, 320, 240);
        @SuppressWarnings("deprecation")
        AdaptiveTrackSelection adaptiveTrackSelection = /* tracks= */
        new AdaptiveTrackSelection.Factory(initialBandwidthMeter).createTrackSelection(new TrackGroup(format), injectedBandwidthMeter, 0);
        /* playbackPositionUs= */
        /* bufferedDurationUs= */
        /* availableDurationUs= */
        /* queue= */
        /* mediaChunkIterators= */
        adaptiveTrackSelection.updateSelectedTrack(0, 0, TIME_UNSET, Collections.emptyList(), new MediaChunkIterator[]{ MediaChunkIterator.EMPTY });
        Mockito.verify(initialBandwidthMeter, Mockito.atLeastOnce()).getBitrateEstimate();
        Mockito.verifyZeroInteractions(injectedBandwidthMeter);
    }

    @Test
    public void testSelectInitialIndexUseMaxInitialBitrateIfNoBandwidthEstimate() {
        Format format1 = /* bitrate= */
        /* width= */
        /* height= */
        AdaptiveTrackSelectionTest.videoFormat(500, 320, 240);
        Format format2 = /* bitrate= */
        /* width= */
        /* height= */
        AdaptiveTrackSelectionTest.videoFormat(1000, 640, 480);
        Format format3 = /* bitrate= */
        /* width= */
        /* height= */
        AdaptiveTrackSelectionTest.videoFormat(2000, 960, 720);
        TrackGroup trackGroup = new TrackGroup(format1, format2, format3);
        Mockito.when(mockBandwidthMeter.getBitrateEstimate()).thenReturn(1000L);
        adaptiveTrackSelection = adaptiveTrackSelection(trackGroup);
        assertThat(adaptiveTrackSelection.getSelectedFormat()).isEqualTo(format2);
        assertThat(adaptiveTrackSelection.getSelectionReason()).isEqualTo(SELECTION_REASON_INITIAL);
    }

    @Test
    public void testSelectInitialIndexUseBandwidthEstimateIfAvailable() {
        Format format1 = /* bitrate= */
        /* width= */
        /* height= */
        AdaptiveTrackSelectionTest.videoFormat(500, 320, 240);
        Format format2 = /* bitrate= */
        /* width= */
        /* height= */
        AdaptiveTrackSelectionTest.videoFormat(1000, 640, 480);
        Format format3 = /* bitrate= */
        /* width= */
        /* height= */
        AdaptiveTrackSelectionTest.videoFormat(2000, 960, 720);
        TrackGroup trackGroup = new TrackGroup(format1, format2, format3);
        Mockito.when(mockBandwidthMeter.getBitrateEstimate()).thenReturn(500L);
        adaptiveTrackSelection = adaptiveTrackSelection(trackGroup);
        assertThat(adaptiveTrackSelection.getSelectedFormat()).isEqualTo(format1);
        assertThat(adaptiveTrackSelection.getSelectionReason()).isEqualTo(SELECTION_REASON_INITIAL);
    }

    @Test
    public void testUpdateSelectedTrackDoNotSwitchUpIfNotBufferedEnough() {
        Format format1 = /* bitrate= */
        /* width= */
        /* height= */
        AdaptiveTrackSelectionTest.videoFormat(500, 320, 240);
        Format format2 = /* bitrate= */
        /* width= */
        /* height= */
        AdaptiveTrackSelectionTest.videoFormat(1000, 640, 480);
        Format format3 = /* bitrate= */
        /* width= */
        /* height= */
        AdaptiveTrackSelectionTest.videoFormat(2000, 960, 720);
        TrackGroup trackGroup = new TrackGroup(format1, format2, format3);
        // The second measurement onward returns 2000L, which prompts the track selection to switch up
        // if possible.
        Mockito.when(mockBandwidthMeter.getBitrateEstimate()).thenReturn(1000L, 2000L);
        adaptiveTrackSelection = /* minDurationForQualityIncreaseMs= */
        adaptiveTrackSelectionWithMinDurationForQualityIncreaseMs(trackGroup, 10000);
        /* playbackPositionUs= */
        /* bufferedDurationUs= */
        /* availableDurationUs= */
        /* queue= */
        /* mediaChunkIterators= */
        adaptiveTrackSelection.updateSelectedTrack(0, 9999000, TIME_UNSET, Collections.emptyList(), AdaptiveTrackSelectionTest.THREE_EMPTY_MEDIA_CHUNK_ITERATORS);
        // When bandwidth estimation is updated to 2000L, we can switch up to use a higher bitrate
        // format. However, since we only buffered 9_999_000 us, which is smaller than
        // minDurationForQualityIncreaseMs, we should defer switch up.
        assertThat(adaptiveTrackSelection.getSelectedFormat()).isEqualTo(format2);
        assertThat(adaptiveTrackSelection.getSelectionReason()).isEqualTo(SELECTION_REASON_INITIAL);
    }

    @Test
    public void testUpdateSelectedTrackSwitchUpIfBufferedEnough() {
        Format format1 = /* bitrate= */
        /* width= */
        /* height= */
        AdaptiveTrackSelectionTest.videoFormat(500, 320, 240);
        Format format2 = /* bitrate= */
        /* width= */
        /* height= */
        AdaptiveTrackSelectionTest.videoFormat(1000, 640, 480);
        Format format3 = /* bitrate= */
        /* width= */
        /* height= */
        AdaptiveTrackSelectionTest.videoFormat(2000, 960, 720);
        TrackGroup trackGroup = new TrackGroup(format1, format2, format3);
        // The second measurement onward returns 2000L, which prompts the track selection to switch up
        // if possible.
        Mockito.when(mockBandwidthMeter.getBitrateEstimate()).thenReturn(1000L, 2000L);
        adaptiveTrackSelection = /* minDurationForQualityIncreaseMs= */
        adaptiveTrackSelectionWithMinDurationForQualityIncreaseMs(trackGroup, 10000);
        /* playbackPositionUs= */
        /* bufferedDurationUs= */
        /* availableDurationUs= */
        /* queue= */
        /* mediaChunkIterators= */
        adaptiveTrackSelection.updateSelectedTrack(0, 10000000, TIME_UNSET, Collections.emptyList(), AdaptiveTrackSelectionTest.THREE_EMPTY_MEDIA_CHUNK_ITERATORS);
        // When bandwidth estimation is updated to 2000L, we can switch up to use a higher bitrate
        // format. When we have buffered enough (10_000_000 us, which is equal to
        // minDurationForQualityIncreaseMs), we should switch up now.
        assertThat(adaptiveTrackSelection.getSelectedFormat()).isEqualTo(format3);
        assertThat(adaptiveTrackSelection.getSelectionReason()).isEqualTo(SELECTION_REASON_ADAPTIVE);
    }

    @Test
    public void testUpdateSelectedTrackDoNotSwitchDownIfBufferedEnough() {
        Format format1 = /* bitrate= */
        /* width= */
        /* height= */
        AdaptiveTrackSelectionTest.videoFormat(500, 320, 240);
        Format format2 = /* bitrate= */
        /* width= */
        /* height= */
        AdaptiveTrackSelectionTest.videoFormat(1000, 640, 480);
        Format format3 = /* bitrate= */
        /* width= */
        /* height= */
        AdaptiveTrackSelectionTest.videoFormat(2000, 960, 720);
        TrackGroup trackGroup = new TrackGroup(format1, format2, format3);
        // The second measurement onward returns 500L, which prompts the track selection to switch down
        // if necessary.
        Mockito.when(mockBandwidthMeter.getBitrateEstimate()).thenReturn(1000L, 500L);
        adaptiveTrackSelection = /* maxDurationForQualityDecreaseMs= */
        adaptiveTrackSelectionWithMaxDurationForQualityDecreaseMs(trackGroup, 25000);
        /* playbackPositionUs= */
        /* bufferedDurationUs= */
        /* availableDurationUs= */
        /* queue= */
        /* mediaChunkIterators= */
        adaptiveTrackSelection.updateSelectedTrack(0, 25000000, TIME_UNSET, Collections.emptyList(), AdaptiveTrackSelectionTest.THREE_EMPTY_MEDIA_CHUNK_ITERATORS);
        // When bandwidth estimation is updated to 500L, we should switch down to use a lower bitrate
        // format. However, since we have enough buffer at higher quality (25_000_000 us, which is equal
        // to maxDurationForQualityDecreaseMs), we should defer switch down.
        assertThat(adaptiveTrackSelection.getSelectedFormat()).isEqualTo(format2);
        assertThat(adaptiveTrackSelection.getSelectionReason()).isEqualTo(SELECTION_REASON_INITIAL);
    }

    @Test
    public void testUpdateSelectedTrackSwitchDownIfNotBufferedEnough() {
        Format format1 = /* bitrate= */
        /* width= */
        /* height= */
        AdaptiveTrackSelectionTest.videoFormat(500, 320, 240);
        Format format2 = /* bitrate= */
        /* width= */
        /* height= */
        AdaptiveTrackSelectionTest.videoFormat(1000, 640, 480);
        Format format3 = /* bitrate= */
        /* width= */
        /* height= */
        AdaptiveTrackSelectionTest.videoFormat(2000, 960, 720);
        TrackGroup trackGroup = new TrackGroup(format1, format2, format3);
        // The second measurement onward returns 500L, which prompts the track selection to switch down
        // if necessary.
        Mockito.when(mockBandwidthMeter.getBitrateEstimate()).thenReturn(1000L, 500L);
        adaptiveTrackSelection = /* maxDurationForQualityDecreaseMs= */
        adaptiveTrackSelectionWithMaxDurationForQualityDecreaseMs(trackGroup, 25000);
        /* playbackPositionUs= */
        /* bufferedDurationUs= */
        /* availableDurationUs= */
        /* queue= */
        /* mediaChunkIterators= */
        adaptiveTrackSelection.updateSelectedTrack(0, 24999000, TIME_UNSET, Collections.emptyList(), AdaptiveTrackSelectionTest.THREE_EMPTY_MEDIA_CHUNK_ITERATORS);
        // When bandwidth estimation is updated to 500L, we should switch down to use a lower bitrate
        // format. When we don't have enough buffer at higher quality (24_999_000 us is smaller than
        // maxDurationForQualityDecreaseMs), we should switch down now.
        assertThat(adaptiveTrackSelection.getSelectedFormat()).isEqualTo(format1);
        assertThat(adaptiveTrackSelection.getSelectionReason()).isEqualTo(SELECTION_REASON_ADAPTIVE);
    }

    @Test
    public void testEvaluateQueueSizeReturnQueueSizeIfBandwidthIsNotImproved() {
        Format format1 = /* bitrate= */
        /* width= */
        /* height= */
        AdaptiveTrackSelectionTest.videoFormat(500, 320, 240);
        Format format2 = /* bitrate= */
        /* width= */
        /* height= */
        AdaptiveTrackSelectionTest.videoFormat(1000, 640, 480);
        Format format3 = /* bitrate= */
        /* width= */
        /* height= */
        AdaptiveTrackSelectionTest.videoFormat(2000, 960, 720);
        TrackGroup trackGroup = new TrackGroup(format1, format2, format3);
        AdaptiveTrackSelectionTest.FakeMediaChunk chunk1 = /* startTimeUs= */
        /* endTimeUs= */
        new AdaptiveTrackSelectionTest.FakeMediaChunk(format1, 0, 10000000);
        AdaptiveTrackSelectionTest.FakeMediaChunk chunk2 = /* startTimeUs= */
        /* endTimeUs= */
        new AdaptiveTrackSelectionTest.FakeMediaChunk(format1, 10000000, 20000000);
        AdaptiveTrackSelectionTest.FakeMediaChunk chunk3 = /* startTimeUs= */
        /* endTimeUs= */
        new AdaptiveTrackSelectionTest.FakeMediaChunk(format1, 20000000, 30000000);
        List<AdaptiveTrackSelectionTest.FakeMediaChunk> queue = new ArrayList<>();
        queue.add(chunk1);
        queue.add(chunk2);
        queue.add(chunk3);
        Mockito.when(mockBandwidthMeter.getBitrateEstimate()).thenReturn(500L);
        adaptiveTrackSelection = adaptiveTrackSelection(trackGroup);
        int size = adaptiveTrackSelection.evaluateQueueSize(0, queue);
        assertThat(size).isEqualTo(3);
    }

    @Test
    public void testEvaluateQueueSizeDoNotReevaluateUntilAfterMinTimeBetweenBufferReevaluation() {
        Format format1 = /* bitrate= */
        /* width= */
        /* height= */
        AdaptiveTrackSelectionTest.videoFormat(500, 320, 240);
        Format format2 = /* bitrate= */
        /* width= */
        /* height= */
        AdaptiveTrackSelectionTest.videoFormat(1000, 640, 480);
        Format format3 = /* bitrate= */
        /* width= */
        /* height= */
        AdaptiveTrackSelectionTest.videoFormat(2000, 960, 720);
        TrackGroup trackGroup = new TrackGroup(format1, format2, format3);
        AdaptiveTrackSelectionTest.FakeMediaChunk chunk1 = /* startTimeUs= */
        /* endTimeUs= */
        new AdaptiveTrackSelectionTest.FakeMediaChunk(format1, 0, 10000000);
        AdaptiveTrackSelectionTest.FakeMediaChunk chunk2 = /* startTimeUs= */
        /* endTimeUs= */
        new AdaptiveTrackSelectionTest.FakeMediaChunk(format1, 10000000, 20000000);
        AdaptiveTrackSelectionTest.FakeMediaChunk chunk3 = /* startTimeUs= */
        /* endTimeUs= */
        new AdaptiveTrackSelectionTest.FakeMediaChunk(format1, 20000000, 30000000);
        List<AdaptiveTrackSelectionTest.FakeMediaChunk> queue = new ArrayList<>();
        queue.add(chunk1);
        queue.add(chunk2);
        queue.add(chunk3);
        Mockito.when(mockBandwidthMeter.getBitrateEstimate()).thenReturn(500L);
        adaptiveTrackSelection = /* durationToRetainAfterDiscardMs= */
        /* minTimeBetweenBufferReevaluationMs= */
        adaptiveTrackSelectionWithMinTimeBetweenBufferReevaluationMs(trackGroup, 15000, 2000);
        int initialQueueSize = adaptiveTrackSelection.evaluateQueueSize(0, queue);
        fakeClock.advanceTime(1999);
        Mockito.when(mockBandwidthMeter.getBitrateEstimate()).thenReturn(1000L);
        // When bandwidth estimation is updated, we can discard chunks at the end of the queue now.
        // However, since min duration between buffer reevaluation = 2000, we will not reevaluate
        // queue size if time now is only 1999 ms after last buffer reevaluation.
        int newSize = adaptiveTrackSelection.evaluateQueueSize(0, queue);
        assertThat(newSize).isEqualTo(initialQueueSize);
    }

    @Test
    public void testEvaluateQueueSizeRetainMoreThanMinimumDurationAfterDiscard() {
        Format format1 = /* bitrate= */
        /* width= */
        /* height= */
        AdaptiveTrackSelectionTest.videoFormat(500, 320, 240);
        Format format2 = /* bitrate= */
        /* width= */
        /* height= */
        AdaptiveTrackSelectionTest.videoFormat(1000, 640, 480);
        Format format3 = /* bitrate= */
        /* width= */
        /* height= */
        AdaptiveTrackSelectionTest.videoFormat(2000, 960, 720);
        TrackGroup trackGroup = new TrackGroup(format1, format2, format3);
        AdaptiveTrackSelectionTest.FakeMediaChunk chunk1 = /* startTimeUs= */
        /* endTimeUs= */
        new AdaptiveTrackSelectionTest.FakeMediaChunk(format1, 0, 10000000);
        AdaptiveTrackSelectionTest.FakeMediaChunk chunk2 = /* startTimeUs= */
        /* endTimeUs= */
        new AdaptiveTrackSelectionTest.FakeMediaChunk(format1, 10000000, 20000000);
        AdaptiveTrackSelectionTest.FakeMediaChunk chunk3 = /* startTimeUs= */
        /* endTimeUs= */
        new AdaptiveTrackSelectionTest.FakeMediaChunk(format1, 20000000, 30000000);
        List<AdaptiveTrackSelectionTest.FakeMediaChunk> queue = new ArrayList<>();
        queue.add(chunk1);
        queue.add(chunk2);
        queue.add(chunk3);
        Mockito.when(mockBandwidthMeter.getBitrateEstimate()).thenReturn(500L);
        adaptiveTrackSelection = /* durationToRetainAfterDiscardMs= */
        /* minTimeBetweenBufferReevaluationMs= */
        adaptiveTrackSelectionWithMinTimeBetweenBufferReevaluationMs(trackGroup, 15000, 2000);
        int initialQueueSize = adaptiveTrackSelection.evaluateQueueSize(0, queue);
        assertThat(initialQueueSize).isEqualTo(3);
        fakeClock.advanceTime(2000);
        Mockito.when(mockBandwidthMeter.getBitrateEstimate()).thenReturn(1000L);
        // When bandwidth estimation is updated and time has advanced enough, we can discard chunks at
        // the end of the queue now.
        // However, since duration to retain after discard = 15 000 ms, we need to retain at least the
        // first 2 chunks
        int newSize = adaptiveTrackSelection.evaluateQueueSize(0, queue);
        assertThat(newSize).isEqualTo(2);
    }

    private static final class FakeMediaChunk extends MediaChunk {
        private static final DataSource DATA_SOURCE = new DefaultHttpDataSource("TEST_AGENT", null);

        public FakeMediaChunk(Format trackFormat, long startTimeUs, long endTimeUs) {
            super(AdaptiveTrackSelectionTest.FakeMediaChunk.DATA_SOURCE, new com.google.android.exoplayer2.upstream.DataSpec(Uri.EMPTY), trackFormat, SELECTION_REASON_ADAPTIVE, null, startTimeUs, endTimeUs, 0);
        }

        @Override
        public void cancelLoad() {
            // Do nothing.
        }

        @Override
        public void load() throws IOException, InterruptedException {
            // Do nothing.
        }

        @Override
        public boolean isLoadCompleted() {
            return true;
        }
    }
}

