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
package com.google.android.exoplayer2.trackselection;


import Format.NO_VALUE;
import MimeTypes.AUDIO_AAC;
import MimeTypes.VIDEO_H264;
import android.util.Pair;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.RendererConfiguration;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.util.MimeTypes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit tests for {@link MappingTrackSelector}.
 */
@RunWith(RobolectricTestRunner.class)
public final class MappingTrackSelectorTest {
    private static final RendererCapabilities VIDEO_CAPABILITIES = new MappingTrackSelectorTest.FakeRendererCapabilities(C.TRACK_TYPE_VIDEO);

    private static final RendererCapabilities AUDIO_CAPABILITIES = new MappingTrackSelectorTest.FakeRendererCapabilities(C.TRACK_TYPE_AUDIO);

    private static final RendererCapabilities[] RENDERER_CAPABILITIES = new RendererCapabilities[]{ MappingTrackSelectorTest.VIDEO_CAPABILITIES, MappingTrackSelectorTest.AUDIO_CAPABILITIES };

    private static final TrackGroup VIDEO_TRACK_GROUP = new TrackGroup(Format.createVideoSampleFormat("video", VIDEO_H264, null, NO_VALUE, NO_VALUE, 1024, 768, NO_VALUE, null, null));

    private static final TrackGroup AUDIO_TRACK_GROUP = new TrackGroup(Format.createAudioSampleFormat("audio", AUDIO_AAC, null, NO_VALUE, NO_VALUE, 2, 44100, null, null, 0, null));

    private static final TrackGroupArray TRACK_GROUPS = new TrackGroupArray(MappingTrackSelectorTest.VIDEO_TRACK_GROUP, MappingTrackSelectorTest.AUDIO_TRACK_GROUP);

    /**
     * Tests that the video and audio track groups are mapped onto the correct renderers.
     */
    @Test
    public void testMapping() throws ExoPlaybackException {
        MappingTrackSelectorTest.FakeMappingTrackSelector trackSelector = new MappingTrackSelectorTest.FakeMappingTrackSelector();
        trackSelector.selectTracks(MappingTrackSelectorTest.RENDERER_CAPABILITIES, MappingTrackSelectorTest.TRACK_GROUPS);
        trackSelector.assertMappedTrackGroups(0, MappingTrackSelectorTest.VIDEO_TRACK_GROUP);
        trackSelector.assertMappedTrackGroups(1, MappingTrackSelectorTest.AUDIO_TRACK_GROUP);
    }

    /**
     * Tests that the video and audio track groups are mapped onto the correct renderers when the
     * renderer ordering is reversed.
     */
    @Test
    public void testMappingReverseOrder() throws ExoPlaybackException {
        MappingTrackSelectorTest.FakeMappingTrackSelector trackSelector = new MappingTrackSelectorTest.FakeMappingTrackSelector();
        RendererCapabilities[] reverseOrderRendererCapabilities = new RendererCapabilities[]{ MappingTrackSelectorTest.AUDIO_CAPABILITIES, MappingTrackSelectorTest.VIDEO_CAPABILITIES };
        trackSelector.selectTracks(reverseOrderRendererCapabilities, MappingTrackSelectorTest.TRACK_GROUPS);
        trackSelector.assertMappedTrackGroups(0, MappingTrackSelectorTest.AUDIO_TRACK_GROUP);
        trackSelector.assertMappedTrackGroups(1, MappingTrackSelectorTest.VIDEO_TRACK_GROUP);
    }

    /**
     * Tests video and audio track groups are mapped onto the correct renderers when there are
     * multiple track groups of the same type.
     */
    @Test
    public void testMappingMulti() throws ExoPlaybackException {
        MappingTrackSelectorTest.FakeMappingTrackSelector trackSelector = new MappingTrackSelectorTest.FakeMappingTrackSelector();
        TrackGroupArray multiTrackGroups = new TrackGroupArray(MappingTrackSelectorTest.VIDEO_TRACK_GROUP, MappingTrackSelectorTest.AUDIO_TRACK_GROUP, MappingTrackSelectorTest.VIDEO_TRACK_GROUP);
        trackSelector.selectTracks(MappingTrackSelectorTest.RENDERER_CAPABILITIES, multiTrackGroups);
        trackSelector.assertMappedTrackGroups(0, MappingTrackSelectorTest.VIDEO_TRACK_GROUP, MappingTrackSelectorTest.VIDEO_TRACK_GROUP);
        trackSelector.assertMappedTrackGroups(1, MappingTrackSelectorTest.AUDIO_TRACK_GROUP);
    }

    /**
     * A {@link MappingTrackSelector} that stashes the {@link MappedTrackInfo} passed to {@link #selectTracks(MappedTrackInfo, int[][][], int[])}.
     */
    private static final class FakeMappingTrackSelector extends MappingTrackSelector {
        private MappedTrackInfo lastMappedTrackInfo;

        @Override
        protected Pair<RendererConfiguration[], TrackSelection[]> selectTracks(MappedTrackInfo mappedTrackInfo, int[][][] rendererFormatSupports, int[] rendererMixedMimeTypeAdaptationSupports) throws ExoPlaybackException {
            int rendererCount = mappedTrackInfo.getRendererCount();
            lastMappedTrackInfo = mappedTrackInfo;
            return Pair.create(new RendererConfiguration[rendererCount], new TrackSelection[rendererCount]);
        }

        public void assertMappedTrackGroups(int rendererIndex, TrackGroup... expected) {
            TrackGroupArray rendererTrackGroupArray = lastMappedTrackInfo.getTrackGroups(rendererIndex);
            assertThat(rendererTrackGroupArray.length).isEqualTo(expected.length);
            for (int i = 0; i < (expected.length); i++) {
                assertThat(rendererTrackGroupArray.get(i)).isEqualTo(expected[i]);
            }
        }
    }

    /**
     * A {@link RendererCapabilities} that advertises adaptive support for all tracks of a given type.
     */
    private static final class FakeRendererCapabilities implements RendererCapabilities {
        private final int trackType;

        public FakeRendererCapabilities(int trackType) {
            this.trackType = trackType;
        }

        @Override
        public int getTrackType() {
            return trackType;
        }

        @Override
        public int supportsFormat(Format format) throws ExoPlaybackException {
            return (MimeTypes.getTrackType(format.sampleMimeType)) == (trackType) ? (FORMAT_HANDLED) | (ADAPTIVE_SEAMLESS) : FORMAT_UNSUPPORTED_TYPE;
        }

        @Override
        public int supportsMixedMimeTypeAdaptation() throws ExoPlaybackException {
            return ADAPTIVE_SEAMLESS;
        }
    }
}

