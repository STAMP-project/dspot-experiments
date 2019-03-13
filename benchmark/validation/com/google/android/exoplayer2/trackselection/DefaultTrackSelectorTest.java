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
package com.google.android.exoplayer2.trackselection;


import C.SELECTION_FLAG_DEFAULT;
import C.SELECTION_FLAG_FORCED;
import Format.NO_VALUE;
import MimeTypes.AUDIO_AAC;
import Parameters.CREATOR;
import Parameters.DEFAULT;
import TrackSelection.Factory;
import android.os.Parcel;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.RendererConfiguration;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector.Parameters;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector.ParametersBuilder;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector.SelectionOverride;
import com.google.android.exoplayer2.trackselection.TrackSelector.InvalidationListener;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.util.MimeTypes;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit tests for {@link DefaultTrackSelector}.
 */
@RunWith(RobolectricTestRunner.class)
public final class DefaultTrackSelectorTest {
    private static final RendererCapabilities ALL_AUDIO_FORMAT_SUPPORTED_RENDERER_CAPABILITIES = new DefaultTrackSelectorTest.FakeRendererCapabilities(C.TRACK_TYPE_AUDIO);

    private static final RendererCapabilities ALL_TEXT_FORMAT_SUPPORTED_RENDERER_CAPABILITIES = new DefaultTrackSelectorTest.FakeRendererCapabilities(C.TRACK_TYPE_TEXT);

    private static final RendererCapabilities ALL_AUDIO_FORMAT_EXCEEDED_RENDERER_CAPABILITIES = new DefaultTrackSelectorTest.FakeRendererCapabilities(C.TRACK_TYPE_AUDIO, RendererCapabilities.FORMAT_EXCEEDS_CAPABILITIES);

    private static final RendererCapabilities VIDEO_CAPABILITIES = new DefaultTrackSelectorTest.FakeRendererCapabilities(C.TRACK_TYPE_VIDEO);

    private static final RendererCapabilities AUDIO_CAPABILITIES = new DefaultTrackSelectorTest.FakeRendererCapabilities(C.TRACK_TYPE_AUDIO);

    private static final RendererCapabilities NO_SAMPLE_CAPABILITIES = new DefaultTrackSelectorTest.FakeRendererCapabilities(C.TRACK_TYPE_NONE);

    private static final RendererCapabilities[] RENDERER_CAPABILITIES = new RendererCapabilities[]{ DefaultTrackSelectorTest.VIDEO_CAPABILITIES, DefaultTrackSelectorTest.AUDIO_CAPABILITIES };

    private static final RendererCapabilities[] RENDERER_CAPABILITIES_WITH_NO_SAMPLE_RENDERER = new RendererCapabilities[]{ DefaultTrackSelectorTest.VIDEO_CAPABILITIES, DefaultTrackSelectorTest.NO_SAMPLE_CAPABILITIES };

    private static final Format VIDEO_FORMAT = DefaultTrackSelectorTest.buildVideoFormat("video");

    private static final Format AUDIO_FORMAT = DefaultTrackSelectorTest.buildAudioFormat("audio");

    private static final TrackGroup VIDEO_TRACK_GROUP = new TrackGroup(DefaultTrackSelectorTest.VIDEO_FORMAT);

    private static final TrackGroup AUDIO_TRACK_GROUP = new TrackGroup(DefaultTrackSelectorTest.AUDIO_FORMAT);

    private static final TrackGroupArray TRACK_GROUPS = new TrackGroupArray(DefaultTrackSelectorTest.VIDEO_TRACK_GROUP, DefaultTrackSelectorTest.AUDIO_TRACK_GROUP);

    private static final TrackSelection[] TRACK_SELECTIONS = new TrackSelection[]{ new FixedTrackSelection(DefaultTrackSelectorTest.VIDEO_TRACK_GROUP, 0), new FixedTrackSelection(DefaultTrackSelectorTest.AUDIO_TRACK_GROUP, 0) };

    private static final TrackSelection[] TRACK_SELECTIONS_WITH_NO_SAMPLE_RENDERER = new TrackSelection[]{ new FixedTrackSelection(DefaultTrackSelectorTest.VIDEO_TRACK_GROUP, 0), null };

    @Mock
    private InvalidationListener invalidationListener;

    @Mock
    private BandwidthMeter bandwidthMeter;

    private DefaultTrackSelector trackSelector;

    /**
     * Tests {@link Parameters} {@link android.os.Parcelable} implementation.
     */
    @Test
    public void testParametersParcelable() {
        SparseArray<Map<TrackGroupArray, SelectionOverride>> selectionOverrides = new SparseArray();
        Map<TrackGroupArray, SelectionOverride> videoOverrides = new HashMap<>();
        videoOverrides.put(new TrackGroupArray(DefaultTrackSelectorTest.VIDEO_TRACK_GROUP), new SelectionOverride(0, 1));
        selectionOverrides.put(2, videoOverrides);
        SparseBooleanArray rendererDisabledFlags = new SparseBooleanArray();
        rendererDisabledFlags.put(3, true);
        Parameters parametersToParcel = /* preferredAudioLanguage= */
        /* preferredTextLanguage= */
        /* selectUndeterminedTextLanguage= */
        /* disabledTextTrackSelectionFlags= */
        /* forceLowestBitrate= */
        /* forceHighestSupportedBitrate= */
        /* allowMixedMimeAdaptiveness= */
        /* allowNonSeamlessAdaptiveness= */
        /* maxVideoWidth= */
        /* maxVideoHeight= */
        /* maxVideoFrameRate= */
        /* maxVideoBitrate= */
        /* exceedVideoConstraintsIfNecessary= */
        /* exceedRendererCapabilitiesIfNecessary= */
        /* viewportWidth= */
        /* viewportHeight= */
        /* viewportOrientationMayChange= */
        /* tunnelingAudioSessionId= */
        new Parameters(selectionOverrides, rendererDisabledFlags, "en", "de", false, 0, true, true, false, true, 1, 2, 3, 4, false, true, 5, 6, false, C.AUDIO_SESSION_ID_UNSET);
        Parcel parcel = Parcel.obtain();
        parametersToParcel.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Parameters parametersFromParcel = CREATOR.createFromParcel(parcel);
        assertThat(parametersFromParcel).isEqualTo(parametersToParcel);
        parcel.recycle();
    }

    /**
     * Tests {@link SelectionOverride}'s {@link android.os.Parcelable} implementation.
     */
    @Test
    public void testSelectionOverrideParcelable() {
        int[] tracks = new int[]{ 2, 3 };
        SelectionOverride selectionOverrideToParcel = /* groupIndex= */
        new SelectionOverride(1, tracks);
        Parcel parcel = Parcel.obtain();
        selectionOverrideToParcel.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        SelectionOverride selectionOverrideFromParcel = SelectionOverride.CREATOR.createFromParcel(parcel);
        assertThat(selectionOverrideFromParcel).isEqualTo(selectionOverrideToParcel);
        parcel.recycle();
    }

    /**
     * Tests that a null override clears a track selection.
     */
    @Test
    public void testSelectTracksWithNullOverride() throws ExoPlaybackException {
        DefaultTrackSelector trackSelector = new DefaultTrackSelector();
        trackSelector.init(invalidationListener, bandwidthMeter);
        trackSelector.setParameters(trackSelector.buildUponParameters().setSelectionOverride(0, new TrackGroupArray(DefaultTrackSelectorTest.VIDEO_TRACK_GROUP), null));
        TrackSelectorResult result = trackSelector.selectTracks(DefaultTrackSelectorTest.RENDERER_CAPABILITIES, DefaultTrackSelectorTest.TRACK_GROUPS);
        DefaultTrackSelectorTest.assertTrackSelections(result, new TrackSelection[]{ null, DefaultTrackSelectorTest.TRACK_SELECTIONS[1] });
        assertThat(result.rendererConfigurations).isEqualTo(new RendererConfiguration[]{ null, RendererConfiguration.DEFAULT });
    }

    /**
     * Tests that a null override can be cleared.
     */
    @Test
    public void testSelectTracksWithClearedNullOverride() throws ExoPlaybackException {
        DefaultTrackSelector trackSelector = new DefaultTrackSelector();
        trackSelector.init(invalidationListener, bandwidthMeter);
        trackSelector.setParameters(trackSelector.buildUponParameters().setSelectionOverride(0, new TrackGroupArray(DefaultTrackSelectorTest.VIDEO_TRACK_GROUP), null).clearSelectionOverride(0, new TrackGroupArray(DefaultTrackSelectorTest.VIDEO_TRACK_GROUP)));
        TrackSelectorResult result = trackSelector.selectTracks(DefaultTrackSelectorTest.RENDERER_CAPABILITIES, DefaultTrackSelectorTest.TRACK_GROUPS);
        DefaultTrackSelectorTest.assertTrackSelections(result, DefaultTrackSelectorTest.TRACK_SELECTIONS);
        assertThat(result.rendererConfigurations).isEqualTo(new RendererConfiguration[]{ RendererConfiguration.DEFAULT, RendererConfiguration.DEFAULT });
    }

    /**
     * Tests that an override is not applied for a different set of available track groups.
     */
    @Test
    public void testSelectTracksWithNullOverrideForDifferentTracks() throws ExoPlaybackException {
        DefaultTrackSelector trackSelector = new DefaultTrackSelector();
        trackSelector.init(invalidationListener, bandwidthMeter);
        trackSelector.setParameters(trackSelector.buildUponParameters().setSelectionOverride(0, new TrackGroupArray(DefaultTrackSelectorTest.VIDEO_TRACK_GROUP), null));
        TrackSelectorResult result = trackSelector.selectTracks(DefaultTrackSelectorTest.RENDERER_CAPABILITIES, new TrackGroupArray(DefaultTrackSelectorTest.VIDEO_TRACK_GROUP, DefaultTrackSelectorTest.AUDIO_TRACK_GROUP, DefaultTrackSelectorTest.VIDEO_TRACK_GROUP));
        DefaultTrackSelectorTest.assertTrackSelections(result, DefaultTrackSelectorTest.TRACK_SELECTIONS);
        assertThat(result.rendererConfigurations).isEqualTo(new RendererConfiguration[]{ RendererConfiguration.DEFAULT, RendererConfiguration.DEFAULT });
    }

    /**
     * Tests disabling a renderer.
     */
    @Test
    public void testSelectTracksWithDisabledRenderer() throws ExoPlaybackException {
        DefaultTrackSelector trackSelector = new DefaultTrackSelector();
        trackSelector.init(invalidationListener, bandwidthMeter);
        trackSelector.setParameters(trackSelector.buildUponParameters().setRendererDisabled(1, true));
        TrackSelectorResult result = trackSelector.selectTracks(DefaultTrackSelectorTest.RENDERER_CAPABILITIES, DefaultTrackSelectorTest.TRACK_GROUPS);
        DefaultTrackSelectorTest.assertTrackSelections(result, new TrackSelection[]{ DefaultTrackSelectorTest.TRACK_SELECTIONS[0], null });
        assertThat(new RendererConfiguration[]{ RendererConfiguration.DEFAULT, null }).isEqualTo(result.rendererConfigurations);
    }

    /**
     * Tests that a disabled renderer can be enabled again.
     */
    @Test
    public void testSelectTracksWithClearedDisabledRenderer() throws ExoPlaybackException {
        DefaultTrackSelector trackSelector = new DefaultTrackSelector();
        trackSelector.init(invalidationListener, bandwidthMeter);
        trackSelector.setParameters(trackSelector.buildUponParameters().setRendererDisabled(1, true).setRendererDisabled(1, false));
        TrackSelectorResult result = trackSelector.selectTracks(DefaultTrackSelectorTest.RENDERER_CAPABILITIES, DefaultTrackSelectorTest.TRACK_GROUPS);
        DefaultTrackSelectorTest.assertTrackSelections(result, DefaultTrackSelectorTest.TRACK_SELECTIONS);
        assertThat(new RendererConfiguration[]{ RendererConfiguration.DEFAULT, RendererConfiguration.DEFAULT }).isEqualTo(result.rendererConfigurations);
    }

    /**
     * Tests a no-sample renderer is enabled without a track selection by default.
     */
    @Test
    public void testSelectTracksWithNoSampleRenderer() throws ExoPlaybackException {
        DefaultTrackSelector trackSelector = new DefaultTrackSelector();
        trackSelector.init(invalidationListener, bandwidthMeter);
        TrackSelectorResult result = trackSelector.selectTracks(DefaultTrackSelectorTest.RENDERER_CAPABILITIES_WITH_NO_SAMPLE_RENDERER, DefaultTrackSelectorTest.TRACK_GROUPS);
        DefaultTrackSelectorTest.assertTrackSelections(result, DefaultTrackSelectorTest.TRACK_SELECTIONS_WITH_NO_SAMPLE_RENDERER);
        assertThat(new RendererConfiguration[]{ RendererConfiguration.DEFAULT, RendererConfiguration.DEFAULT }).isEqualTo(result.rendererConfigurations);
    }

    /**
     * Tests disabling a no-sample renderer.
     */
    @Test
    public void testSelectTracksWithDisabledNoSampleRenderer() throws ExoPlaybackException {
        DefaultTrackSelector trackSelector = new DefaultTrackSelector();
        trackSelector.init(invalidationListener, bandwidthMeter);
        trackSelector.setParameters(trackSelector.buildUponParameters().setRendererDisabled(1, true));
        TrackSelectorResult result = trackSelector.selectTracks(DefaultTrackSelectorTest.RENDERER_CAPABILITIES_WITH_NO_SAMPLE_RENDERER, DefaultTrackSelectorTest.TRACK_GROUPS);
        DefaultTrackSelectorTest.assertTrackSelections(result, DefaultTrackSelectorTest.TRACK_SELECTIONS_WITH_NO_SAMPLE_RENDERER);
        assertThat(new RendererConfiguration[]{ RendererConfiguration.DEFAULT, null }).isEqualTo(result.rendererConfigurations);
    }

    /**
     * Tests that track selector will not call
     * {@link InvalidationListener#onTrackSelectionsInvalidated()} when it's set with default
     * values of {@link Parameters}.
     */
    @Test
    public void testSetParameterWithDefaultParametersDoesNotNotifyInvalidationListener() throws Exception {
        /* bandwidthMeter= */
        trackSelector.init(invalidationListener, null);
        Mockito.verify(invalidationListener, Mockito.never()).onTrackSelectionsInvalidated();
    }

    /**
     * Tests that track selector will call {@link InvalidationListener#onTrackSelectionsInvalidated()}
     * when it's set with non-default values of {@link Parameters}.
     */
    @Test
    public void testSetParameterWithNonDefaultParameterNotifyInvalidationListener() throws Exception {
        Parameters parameters = new ParametersBuilder().setPreferredAudioLanguage("eng").build();
        /* bandwidthMeter= */
        trackSelector.init(invalidationListener, null);
        trackSelector.setParameters(parameters);
        Mockito.verify(invalidationListener).onTrackSelectionsInvalidated();
    }

    /**
     * Tests that track selector will not call
     * {@link InvalidationListener#onTrackSelectionsInvalidated()} again when it's set with
     * the same values of {@link Parameters}.
     */
    @Test
    public void testSetParameterWithSameParametersDoesNotNotifyInvalidationListenerAgain() throws Exception {
        ParametersBuilder builder = new ParametersBuilder().setPreferredAudioLanguage("eng");
        /* bandwidthMeter= */
        trackSelector.init(invalidationListener, null);
        trackSelector.setParameters(builder.build());
        trackSelector.setParameters(builder.build());
        Mockito.verify(invalidationListener, Mockito.times(1)).onTrackSelectionsInvalidated();
    }

    /**
     * Tests that track selector will select audio track with {@link C#SELECTION_FLAG_DEFAULT}
     * given default values of {@link Parameters}.
     */
    @Test
    public void testSelectTracksSelectTrackWithSelectionFlag() throws Exception {
        Format audioFormat = /* language= */
        /* selectionFlags= */
        DefaultTrackSelectorTest.buildAudioFormat("audio", null, 0);
        Format formatWithSelectionFlag = /* language= */
        DefaultTrackSelectorTest.buildAudioFormat("audio", null, SELECTION_FLAG_DEFAULT);
        TrackSelectorResult result = trackSelector.selectTracks(new RendererCapabilities[]{ DefaultTrackSelectorTest.ALL_AUDIO_FORMAT_SUPPORTED_RENDERER_CAPABILITIES }, DefaultTrackSelectorTest.singleTrackGroup(formatWithSelectionFlag, audioFormat));
        assertThat(result.selections.get(0).getSelectedFormat()).isEqualTo(formatWithSelectionFlag);
    }

    /**
     * Tests that track selector will select audio track with language that match preferred language
     * given by {@link Parameters}.
     */
    @Test
    public void testSelectTracksSelectPreferredAudioLanguage() throws Exception {
        trackSelector.setParameters(new ParametersBuilder().setPreferredAudioLanguage("eng").build());
        Format frAudioFormat = Format.createAudioSampleFormat("audio", AUDIO_AAC, null, NO_VALUE, NO_VALUE, 2, 44100, null, null, 0, "fra");
        Format enAudioFormat = Format.createAudioSampleFormat("audio", AUDIO_AAC, null, NO_VALUE, NO_VALUE, 2, 44100, null, null, 0, "eng");
        TrackSelectorResult result = trackSelector.selectTracks(new RendererCapabilities[]{ DefaultTrackSelectorTest.ALL_AUDIO_FORMAT_SUPPORTED_RENDERER_CAPABILITIES }, DefaultTrackSelectorTest.wrapFormats(frAudioFormat, enAudioFormat));
        assertThat(result.selections.get(0).getSelectedFormat()).isEqualTo(enAudioFormat);
    }

    /**
     * Tests that track selector will prefer selecting audio track with language that match preferred
     * language given by {@link Parameters} over track with {@link C#SELECTION_FLAG_DEFAULT}.
     */
    @Test
    public void testSelectTracksSelectPreferredAudioLanguageOverSelectionFlag() throws Exception {
        trackSelector.setParameters(new ParametersBuilder().setPreferredAudioLanguage("eng").build());
        Format frAudioFormat = Format.createAudioSampleFormat("audio", AUDIO_AAC, null, NO_VALUE, NO_VALUE, 2, 44100, null, null, SELECTION_FLAG_DEFAULT, "fra");
        Format enAudioFormat = Format.createAudioSampleFormat("audio", AUDIO_AAC, null, NO_VALUE, NO_VALUE, 2, 44100, null, null, 0, "eng");
        TrackSelectorResult result = trackSelector.selectTracks(new RendererCapabilities[]{ DefaultTrackSelectorTest.ALL_AUDIO_FORMAT_SUPPORTED_RENDERER_CAPABILITIES }, DefaultTrackSelectorTest.wrapFormats(frAudioFormat, enAudioFormat));
        assertThat(result.selections.get(0).getSelectedFormat()).isEqualTo(enAudioFormat);
    }

    /**
     * Tests that track selector will prefer tracks that are within renderer's capabilities over
     * track that exceed renderer's capabilities.
     */
    @Test
    public void testSelectTracksPreferTrackWithinCapabilities() throws Exception {
        Format supportedFormat = DefaultTrackSelectorTest.buildAudioFormat("supportedFormat");
        Format exceededFormat = DefaultTrackSelectorTest.buildAudioFormat("exceededFormat");
        Map<String, Integer> mappedCapabilities = new HashMap<>();
        mappedCapabilities.put(supportedFormat.id, RendererCapabilities.FORMAT_HANDLED);
        mappedCapabilities.put(exceededFormat.id, RendererCapabilities.FORMAT_EXCEEDS_CAPABILITIES);
        RendererCapabilities mappedAudioRendererCapabilities = new DefaultTrackSelectorTest.FakeMappedRendererCapabilities(C.TRACK_TYPE_AUDIO, mappedCapabilities);
        TrackSelectorResult result = trackSelector.selectTracks(new RendererCapabilities[]{ mappedAudioRendererCapabilities }, DefaultTrackSelectorTest.singleTrackGroup(exceededFormat, supportedFormat));
        assertThat(result.selections.get(0).getSelectedFormat()).isEqualTo(supportedFormat);
    }

    /**
     * Tests that track selector will select a track that exceeds the renderer's capabilities when
     * there are no other choice, given the default {@link Parameters}.
     */
    @Test
    public void testSelectTracksWithNoTrackWithinCapabilitiesSelectExceededCapabilityTrack() throws Exception {
        Format audioFormat = Format.createAudioSampleFormat("audio", AUDIO_AAC, null, NO_VALUE, NO_VALUE, 2, 44100, null, null, 0, null);
        TrackSelectorResult result = trackSelector.selectTracks(new RendererCapabilities[]{ DefaultTrackSelectorTest.ALL_AUDIO_FORMAT_EXCEEDED_RENDERER_CAPABILITIES }, DefaultTrackSelectorTest.singleTrackGroup(audioFormat));
        assertThat(result.selections.get(0).getSelectedFormat()).isEqualTo(audioFormat);
    }

    /**
     * Tests that track selector will return a null track selection for a renderer when
     * all tracks exceed that renderer's capabilities when {@link Parameters} does not allow
     * exceeding-capabilities tracks.
     */
    @Test
    public void testSelectTracksWithNoTrackWithinCapabilitiesAndSetByParamsReturnNoSelection() throws Exception {
        trackSelector.setParameters(new ParametersBuilder().setExceedRendererCapabilitiesIfNecessary(false).build());
        Format audioFormat = Format.createAudioSampleFormat("audio", AUDIO_AAC, null, NO_VALUE, NO_VALUE, 2, 44100, null, null, 0, null);
        TrackSelectorResult result = trackSelector.selectTracks(new RendererCapabilities[]{ DefaultTrackSelectorTest.ALL_AUDIO_FORMAT_EXCEEDED_RENDERER_CAPABILITIES }, DefaultTrackSelectorTest.singleTrackGroup(audioFormat));
        assertThat(result.selections.get(0)).isNull();
    }

    /**
     * Tests that track selector will prefer tracks that are within renderer's capabilities over
     * tracks that have {@link C#SELECTION_FLAG_DEFAULT} but exceed renderer's capabilities.
     */
    @Test
    public void testSelectTracksPreferTrackWithinCapabilitiesOverSelectionFlag() throws Exception {
        Format supportedFormat = Format.createAudioSampleFormat("supportedFormat", AUDIO_AAC, null, NO_VALUE, NO_VALUE, 2, 44100, null, null, 0, null);
        Format exceededWithSelectionFlagFormat = Format.createAudioSampleFormat("exceededFormat", AUDIO_AAC, null, NO_VALUE, NO_VALUE, 2, 44100, null, null, SELECTION_FLAG_DEFAULT, null);
        Map<String, Integer> mappedCapabilities = new HashMap<>();
        mappedCapabilities.put(supportedFormat.id, RendererCapabilities.FORMAT_HANDLED);
        mappedCapabilities.put(exceededWithSelectionFlagFormat.id, RendererCapabilities.FORMAT_EXCEEDS_CAPABILITIES);
        RendererCapabilities mappedAudioRendererCapabilities = new DefaultTrackSelectorTest.FakeMappedRendererCapabilities(C.TRACK_TYPE_AUDIO, mappedCapabilities);
        TrackSelectorResult result = trackSelector.selectTracks(new RendererCapabilities[]{ mappedAudioRendererCapabilities }, DefaultTrackSelectorTest.singleTrackGroup(exceededWithSelectionFlagFormat, supportedFormat));
        assertThat(result.selections.get(0).getSelectedFormat()).isEqualTo(supportedFormat);
    }

    /**
     * Tests that track selector will prefer tracks that are within renderer's capabilities over
     * track that have language matching preferred audio given by {@link Parameters} but exceed
     * renderer's capabilities.
     */
    @Test
    public void testSelectTracksPreferTrackWithinCapabilitiesOverPreferredLanguage() throws Exception {
        trackSelector.setParameters(new ParametersBuilder().setPreferredAudioLanguage("eng").build());
        Format supportedFrFormat = Format.createAudioSampleFormat("supportedFormat", AUDIO_AAC, null, NO_VALUE, NO_VALUE, 2, 44100, null, null, 0, "fra");
        Format exceededEnFormat = Format.createAudioSampleFormat("exceededFormat", AUDIO_AAC, null, NO_VALUE, NO_VALUE, 2, 44100, null, null, 0, "eng");
        Map<String, Integer> mappedCapabilities = new HashMap<>();
        mappedCapabilities.put(exceededEnFormat.id, RendererCapabilities.FORMAT_EXCEEDS_CAPABILITIES);
        mappedCapabilities.put(supportedFrFormat.id, RendererCapabilities.FORMAT_HANDLED);
        RendererCapabilities mappedAudioRendererCapabilities = new DefaultTrackSelectorTest.FakeMappedRendererCapabilities(C.TRACK_TYPE_AUDIO, mappedCapabilities);
        TrackSelectorResult result = trackSelector.selectTracks(new RendererCapabilities[]{ mappedAudioRendererCapabilities }, DefaultTrackSelectorTest.singleTrackGroup(exceededEnFormat, supportedFrFormat));
        assertThat(result.selections.get(0).getSelectedFormat()).isEqualTo(supportedFrFormat);
    }

    /**
     * Tests that track selector will prefer tracks that are within renderer's capabilities over
     * track that have both language matching preferred audio given by {@link Parameters} and
     * {@link C#SELECTION_FLAG_DEFAULT}, but exceed renderer's capabilities.
     */
    @Test
    public void testSelectTracksPreferTrackWithinCapabilitiesOverSelectionFlagAndPreferredLanguage() throws Exception {
        trackSelector.setParameters(new ParametersBuilder().setPreferredAudioLanguage("eng").build());
        Format supportedFrFormat = Format.createAudioSampleFormat("supportedFormat", AUDIO_AAC, null, NO_VALUE, NO_VALUE, 2, 44100, null, null, 0, "fra");
        Format exceededDefaultSelectionEnFormat = Format.createAudioSampleFormat("exceededFormat", AUDIO_AAC, null, NO_VALUE, NO_VALUE, 2, 44100, null, null, SELECTION_FLAG_DEFAULT, "eng");
        Map<String, Integer> mappedCapabilities = new HashMap<>();
        mappedCapabilities.put(exceededDefaultSelectionEnFormat.id, RendererCapabilities.FORMAT_EXCEEDS_CAPABILITIES);
        mappedCapabilities.put(supportedFrFormat.id, RendererCapabilities.FORMAT_HANDLED);
        RendererCapabilities mappedAudioRendererCapabilities = new DefaultTrackSelectorTest.FakeMappedRendererCapabilities(C.TRACK_TYPE_AUDIO, mappedCapabilities);
        TrackSelectorResult result = trackSelector.selectTracks(new RendererCapabilities[]{ mappedAudioRendererCapabilities }, DefaultTrackSelectorTest.singleTrackGroup(exceededDefaultSelectionEnFormat, supportedFrFormat));
        assertThat(result.selections.get(0).getSelectedFormat()).isEqualTo(supportedFrFormat);
    }

    /**
     * Tests that track selector will select audio tracks with higher num channel when other factors
     * are the same, and tracks are within renderer's capabilities.
     */
    @Test
    public void testSelectTracksWithinCapabilitiesSelectHigherNumChannel() throws Exception {
        Format lowerChannelFormat = Format.createAudioSampleFormat("audioFormat", AUDIO_AAC, null, NO_VALUE, NO_VALUE, 2, 44100, null, null, 0, null);
        Format higherChannelFormat = Format.createAudioSampleFormat("audioFormat", AUDIO_AAC, null, NO_VALUE, NO_VALUE, 6, 44100, null, null, 0, null);
        TrackSelectorResult result = trackSelector.selectTracks(new RendererCapabilities[]{ DefaultTrackSelectorTest.ALL_AUDIO_FORMAT_SUPPORTED_RENDERER_CAPABILITIES }, DefaultTrackSelectorTest.singleTrackGroup(higherChannelFormat, lowerChannelFormat));
        assertThat(result.selections.get(0).getSelectedFormat()).isEqualTo(higherChannelFormat);
    }

    /**
     * Tests that track selector will select audio tracks with higher sample rate when other factors
     * are the same, and tracks are within renderer's capabilities.
     */
    @Test
    public void testSelectTracksWithinCapabilitiesSelectHigherSampleRate() throws Exception {
        Format higherSampleRateFormat = Format.createAudioSampleFormat("audioFormat", AUDIO_AAC, null, NO_VALUE, NO_VALUE, 2, 44100, null, null, 0, null);
        Format lowerSampleRateFormat = Format.createAudioSampleFormat("audioFormat", AUDIO_AAC, null, NO_VALUE, NO_VALUE, 2, 22050, null, null, 0, null);
        TrackSelectorResult result = trackSelector.selectTracks(new RendererCapabilities[]{ DefaultTrackSelectorTest.ALL_AUDIO_FORMAT_SUPPORTED_RENDERER_CAPABILITIES }, DefaultTrackSelectorTest.singleTrackGroup(higherSampleRateFormat, lowerSampleRateFormat));
        assertThat(result.selections.get(0).getSelectedFormat()).isEqualTo(higherSampleRateFormat);
    }

    /**
     * Tests that track selector will select audio tracks with higher bit-rate when other factors
     * are the same, and tracks are within renderer's capabilities.
     */
    @Test
    public void testSelectTracksWithinCapabilitiesSelectHigherBitrate() throws Exception {
        Format lowerBitrateFormat = Format.createAudioSampleFormat("audioFormat", AUDIO_AAC, null, 15000, NO_VALUE, 2, 44100, null, null, 0, null);
        Format higherBitrateFormat = Format.createAudioSampleFormat("audioFormat", AUDIO_AAC, null, 30000, NO_VALUE, 2, 44100, null, null, 0, null);
        TrackSelectorResult result = trackSelector.selectTracks(new RendererCapabilities[]{ DefaultTrackSelectorTest.ALL_AUDIO_FORMAT_SUPPORTED_RENDERER_CAPABILITIES }, DefaultTrackSelectorTest.singleTrackGroup(lowerBitrateFormat, higherBitrateFormat));
        assertThat(result.selections.get(0).getSelectedFormat()).isEqualTo(higherBitrateFormat);
    }

    /**
     * Tests that track selector will prefer audio tracks with higher channel count over tracks with
     * higher sample rate when other factors are the same, and tracks are within renderer's
     * capabilities.
     */
    @Test
    public void testSelectTracksPreferHigherNumChannelBeforeSampleRate() throws Exception {
        Format lowerChannelHigherSampleRateFormat = Format.createAudioSampleFormat("audioFormat", AUDIO_AAC, null, NO_VALUE, NO_VALUE, 2, 44100, null, null, 0, null);
        Format higherChannelLowerSampleRateFormat = Format.createAudioSampleFormat("audioFormat", AUDIO_AAC, null, NO_VALUE, NO_VALUE, 6, 22050, null, null, 0, null);
        TrackSelectorResult result = trackSelector.selectTracks(new RendererCapabilities[]{ DefaultTrackSelectorTest.ALL_AUDIO_FORMAT_SUPPORTED_RENDERER_CAPABILITIES }, DefaultTrackSelectorTest.singleTrackGroup(higherChannelLowerSampleRateFormat, lowerChannelHigherSampleRateFormat));
        assertThat(result.selections.get(0).getSelectedFormat()).isEqualTo(higherChannelLowerSampleRateFormat);
    }

    /**
     * Tests that track selector will prefer audio tracks with higher sample rate over tracks with
     * higher bitrate when other factors are the same, and tracks are within renderer's
     * capabilities.
     */
    @Test
    public void testSelectTracksPreferHigherSampleRateBeforeBitrate() throws Exception {
        Format higherSampleRateLowerBitrateFormat = Format.createAudioSampleFormat("audioFormat", AUDIO_AAC, null, 15000, NO_VALUE, 2, 44100, null, null, 0, null);
        Format lowerSampleRateHigherBitrateFormat = Format.createAudioSampleFormat("audioFormat", AUDIO_AAC, null, 30000, NO_VALUE, 2, 22050, null, null, 0, null);
        TrackSelectorResult result = trackSelector.selectTracks(new RendererCapabilities[]{ DefaultTrackSelectorTest.ALL_AUDIO_FORMAT_SUPPORTED_RENDERER_CAPABILITIES }, DefaultTrackSelectorTest.singleTrackGroup(higherSampleRateLowerBitrateFormat, lowerSampleRateHigherBitrateFormat));
        assertThat(result.selections.get(0).getSelectedFormat()).isEqualTo(higherSampleRateLowerBitrateFormat);
    }

    /**
     * Tests that track selector will select audio tracks with lower num channel when other factors
     * are the same, and tracks exceed renderer's capabilities.
     */
    @Test
    public void testSelectTracksExceedingCapabilitiesSelectLowerNumChannel() throws Exception {
        Format lowerChannelFormat = Format.createAudioSampleFormat("audioFormat", AUDIO_AAC, null, NO_VALUE, NO_VALUE, 2, 44100, null, null, 0, null);
        Format higherChannelFormat = Format.createAudioSampleFormat("audioFormat", AUDIO_AAC, null, NO_VALUE, NO_VALUE, 6, 44100, null, null, 0, null);
        TrackSelectorResult result = trackSelector.selectTracks(new RendererCapabilities[]{ DefaultTrackSelectorTest.ALL_AUDIO_FORMAT_EXCEEDED_RENDERER_CAPABILITIES }, DefaultTrackSelectorTest.singleTrackGroup(higherChannelFormat, lowerChannelFormat));
        assertThat(result.selections.get(0).getSelectedFormat()).isEqualTo(lowerChannelFormat);
    }

    /**
     * Tests that track selector will select audio tracks with lower sample rate when other factors
     * are the same, and tracks exceed renderer's capabilities.
     */
    @Test
    public void testSelectTracksExceedingCapabilitiesSelectLowerSampleRate() throws Exception {
        Format lowerSampleRateFormat = Format.createAudioSampleFormat("audioFormat", AUDIO_AAC, null, NO_VALUE, NO_VALUE, 2, 22050, null, null, 0, null);
        Format higherSampleRateFormat = Format.createAudioSampleFormat("audioFormat", AUDIO_AAC, null, NO_VALUE, NO_VALUE, 2, 44100, null, null, 0, null);
        TrackSelectorResult result = trackSelector.selectTracks(new RendererCapabilities[]{ DefaultTrackSelectorTest.ALL_AUDIO_FORMAT_EXCEEDED_RENDERER_CAPABILITIES }, DefaultTrackSelectorTest.singleTrackGroup(higherSampleRateFormat, lowerSampleRateFormat));
        assertThat(result.selections.get(0).getSelectedFormat()).isEqualTo(lowerSampleRateFormat);
    }

    /**
     * Tests that track selector will select audio tracks with lower bit-rate when other factors
     * are the same, and tracks exceed renderer's capabilities.
     */
    @Test
    public void testSelectTracksExceedingCapabilitiesSelectLowerBitrate() throws Exception {
        Format lowerBitrateFormat = Format.createAudioSampleFormat("audioFormat", AUDIO_AAC, null, 15000, NO_VALUE, 2, 44100, null, null, 0, null);
        Format higherBitrateFormat = Format.createAudioSampleFormat("audioFormat", AUDIO_AAC, null, 30000, NO_VALUE, 2, 44100, null, null, 0, null);
        TrackSelectorResult result = trackSelector.selectTracks(new RendererCapabilities[]{ DefaultTrackSelectorTest.ALL_AUDIO_FORMAT_EXCEEDED_RENDERER_CAPABILITIES }, DefaultTrackSelectorTest.singleTrackGroup(lowerBitrateFormat, higherBitrateFormat));
        assertThat(result.selections.get(0).getSelectedFormat()).isEqualTo(lowerBitrateFormat);
    }

    /**
     * Tests that track selector will prefer audio tracks with lower channel count over tracks with
     * lower sample rate when other factors are the same, and tracks are within renderer's
     * capabilities.
     */
    @Test
    public void testSelectTracksExceedingCapabilitiesPreferLowerNumChannelBeforeSampleRate() throws Exception {
        Format lowerChannelHigherSampleRateFormat = Format.createAudioSampleFormat("audioFormat", AUDIO_AAC, null, NO_VALUE, NO_VALUE, 2, 44100, null, null, 0, null);
        Format higherChannelLowerSampleRateFormat = Format.createAudioSampleFormat("audioFormat", AUDIO_AAC, null, NO_VALUE, NO_VALUE, 6, 22050, null, null, 0, null);
        TrackSelectorResult result = trackSelector.selectTracks(new RendererCapabilities[]{ DefaultTrackSelectorTest.ALL_AUDIO_FORMAT_EXCEEDED_RENDERER_CAPABILITIES }, DefaultTrackSelectorTest.singleTrackGroup(higherChannelLowerSampleRateFormat, lowerChannelHigherSampleRateFormat));
        assertThat(result.selections.get(0).getSelectedFormat()).isEqualTo(lowerChannelHigherSampleRateFormat);
    }

    /**
     * Tests that track selector will prefer audio tracks with lower sample rate over tracks with
     * lower bitrate when other factors are the same, and tracks are within renderer's
     * capabilities.
     */
    @Test
    public void testSelectTracksExceedingCapabilitiesPreferLowerSampleRateBeforeBitrate() throws Exception {
        Format higherSampleRateLowerBitrateFormat = Format.createAudioSampleFormat("audioFormat", AUDIO_AAC, null, 15000, NO_VALUE, 2, 44100, null, null, 0, null);
        Format lowerSampleRateHigherBitrateFormat = Format.createAudioSampleFormat("audioFormat", AUDIO_AAC, null, 30000, NO_VALUE, 2, 22050, null, null, 0, null);
        TrackSelectorResult result = trackSelector.selectTracks(new RendererCapabilities[]{ DefaultTrackSelectorTest.ALL_AUDIO_FORMAT_EXCEEDED_RENDERER_CAPABILITIES }, DefaultTrackSelectorTest.singleTrackGroup(higherSampleRateLowerBitrateFormat, lowerSampleRateHigherBitrateFormat));
        assertThat(result.selections.get(0).getSelectedFormat()).isEqualTo(lowerSampleRateHigherBitrateFormat);
    }

    /**
     * Tests text track selection flags.
     */
    @Test
    public void testsTextTrackSelectionFlags() throws ExoPlaybackException {
        Format forcedOnly = DefaultTrackSelectorTest.buildTextFormat("forcedOnly", "eng", SELECTION_FLAG_FORCED);
        Format forcedDefault = DefaultTrackSelectorTest.buildTextFormat("forcedDefault", "eng", ((C.SELECTION_FLAG_FORCED) | (C.SELECTION_FLAG_DEFAULT)));
        Format defaultOnly = DefaultTrackSelectorTest.buildTextFormat("defaultOnly", "eng", SELECTION_FLAG_DEFAULT);
        Format forcedOnlySpanish = DefaultTrackSelectorTest.buildTextFormat("forcedOnlySpanish", "spa", SELECTION_FLAG_FORCED);
        Format noFlag = DefaultTrackSelectorTest.buildTextFormat("noFlag", "eng");
        RendererCapabilities[] textRendererCapabilities = new RendererCapabilities[]{ DefaultTrackSelectorTest.ALL_TEXT_FORMAT_SUPPORTED_RENDERER_CAPABILITIES };
        // There is no text language preference, the first track flagged as default should be selected.
        TrackSelectorResult result = trackSelector.selectTracks(textRendererCapabilities, DefaultTrackSelectorTest.wrapFormats(forcedOnly, forcedDefault, defaultOnly, noFlag));
        assertThat(result.selections.get(0).getFormat(0)).isSameAs(forcedDefault);
        // Ditto.
        result = trackSelector.selectTracks(textRendererCapabilities, DefaultTrackSelectorTest.wrapFormats(forcedOnly, noFlag, defaultOnly));
        assertThat(result.selections.get(0).getFormat(0)).isSameAs(defaultOnly);
        // With no language preference and no text track flagged as default, the first forced should be
        // selected.
        result = trackSelector.selectTracks(textRendererCapabilities, DefaultTrackSelectorTest.wrapFormats(forcedOnly, noFlag));
        assertThat(result.selections.get(0).getFormat(0)).isSameAs(forcedOnly);
        trackSelector.setParameters(DEFAULT.buildUpon().setDisabledTextTrackSelectionFlags(SELECTION_FLAG_DEFAULT).build());
        // Default flags are disabled, so the first track flagged as forced should be selected.
        result = trackSelector.selectTracks(textRendererCapabilities, DefaultTrackSelectorTest.wrapFormats(defaultOnly, noFlag, forcedOnly, forcedDefault));
        assertThat(result.selections.get(0).getFormat(0)).isSameAs(forcedOnly);
        trackSelector.setParameters(trackSelector.getParameters().buildUpon().setPreferredAudioLanguage("spa").build());
        // Default flags are disabled, but there is a text track flagged as forced whose language
        // matches the preferred audio language.
        result = trackSelector.selectTracks(textRendererCapabilities, DefaultTrackSelectorTest.wrapFormats(forcedDefault, forcedOnly, defaultOnly, noFlag, forcedOnlySpanish));
        assertThat(result.selections.get(0).getFormat(0)).isSameAs(forcedOnlySpanish);
        trackSelector.setParameters(trackSelector.getParameters().buildUpon().setDisabledTextTrackSelectionFlags(((C.SELECTION_FLAG_DEFAULT) | (C.SELECTION_FLAG_FORCED))).build());
        // All selection flags are disabled and there is no language preference, so nothing should be
        // selected.
        result = trackSelector.selectTracks(textRendererCapabilities, DefaultTrackSelectorTest.wrapFormats(forcedOnly, forcedDefault, defaultOnly, noFlag));
        assertThat(result.selections.get(0)).isNull();
        trackSelector.setParameters(DEFAULT.buildUpon().setPreferredTextLanguage("eng").build());
        // There is a preferred language, so the first language-matching track flagged as default should
        // be selected.
        result = trackSelector.selectTracks(textRendererCapabilities, DefaultTrackSelectorTest.wrapFormats(forcedOnly, forcedDefault, defaultOnly, noFlag));
        assertThat(result.selections.get(0).getFormat(0)).isSameAs(forcedDefault);
        trackSelector.setParameters(trackSelector.getParameters().buildUpon().setDisabledTextTrackSelectionFlags(SELECTION_FLAG_DEFAULT).build());
        // Same as above, but the default flag is disabled. If multiple tracks match the preferred
        // language, those not flagged as forced are preferred, as they likely include the contents of
        // forced subtitles.
        result = trackSelector.selectTracks(textRendererCapabilities, DefaultTrackSelectorTest.wrapFormats(noFlag, forcedOnly, forcedDefault, defaultOnly));
        assertThat(result.selections.get(0).getFormat(0)).isSameAs(noFlag);
    }

    /**
     * Tests that the default track selector will select a text track with undetermined language if no
     * text track with the preferred language is available but
     * {@link Parameters#selectUndeterminedTextLanguage} is true.
     */
    @Test
    public void testSelectUndeterminedTextLanguageAsFallback() throws ExoPlaybackException {
        Format spanish = DefaultTrackSelectorTest.buildTextFormat("spanish", "spa");
        Format german = DefaultTrackSelectorTest.buildTextFormat("german", "de");
        Format undeterminedUnd = DefaultTrackSelectorTest.buildTextFormat("undeterminedUnd", "und");
        Format undeterminedNull = DefaultTrackSelectorTest.buildTextFormat("undeterminedNull", null);
        RendererCapabilities[] textRendererCapabilites = new RendererCapabilities[]{ DefaultTrackSelectorTest.ALL_TEXT_FORMAT_SUPPORTED_RENDERER_CAPABILITIES };
        TrackSelectorResult result = trackSelector.selectTracks(textRendererCapabilites, DefaultTrackSelectorTest.wrapFormats(spanish, german, undeterminedUnd, undeterminedNull));
        assertThat(result.selections.get(0)).isNull();
        trackSelector.setParameters(new ParametersBuilder().setSelectUndeterminedTextLanguage(true).build());
        result = trackSelector.selectTracks(textRendererCapabilites, DefaultTrackSelectorTest.wrapFormats(spanish, german, undeterminedUnd, undeterminedNull));
        assertThat(result.selections.get(0).getFormat(0)).isSameAs(undeterminedUnd);
        ParametersBuilder builder = new ParametersBuilder().setPreferredTextLanguage("spa");
        trackSelector.setParameters(builder.build());
        result = trackSelector.selectTracks(textRendererCapabilites, DefaultTrackSelectorTest.wrapFormats(spanish, german, undeterminedUnd, undeterminedNull));
        assertThat(result.selections.get(0).getFormat(0)).isSameAs(spanish);
        result = trackSelector.selectTracks(textRendererCapabilites, DefaultTrackSelectorTest.wrapFormats(german, undeterminedUnd, undeterminedNull));
        assertThat(result.selections.get(0)).isNull();
        trackSelector.setParameters(builder.setSelectUndeterminedTextLanguage(true).build());
        result = trackSelector.selectTracks(textRendererCapabilites, DefaultTrackSelectorTest.wrapFormats(german, undeterminedUnd, undeterminedNull));
        assertThat(result.selections.get(0).getFormat(0)).isSameAs(undeterminedUnd);
        result = trackSelector.selectTracks(textRendererCapabilites, DefaultTrackSelectorTest.wrapFormats(german, undeterminedNull));
        assertThat(result.selections.get(0).getFormat(0)).isSameAs(undeterminedNull);
        result = trackSelector.selectTracks(textRendererCapabilites, DefaultTrackSelectorTest.wrapFormats(german));
        assertThat(result.selections.get(0)).isNull();
    }

    /**
     * Tests audio track selection when there are multiple audio renderers.
     */
    @Test
    public void testSelectPreferredTextTrackMultipleRenderers() throws Exception {
        Format english = DefaultTrackSelectorTest.buildTextFormat("en", "en");
        Format german = DefaultTrackSelectorTest.buildTextFormat("de", "de");
        // First renderer handles english.
        Map<String, Integer> firstRendererMappedCapabilities = new HashMap<>();
        firstRendererMappedCapabilities.put(english.id, RendererCapabilities.FORMAT_HANDLED);
        firstRendererMappedCapabilities.put(german.id, RendererCapabilities.FORMAT_UNSUPPORTED_SUBTYPE);
        RendererCapabilities firstRendererCapabilities = new DefaultTrackSelectorTest.FakeMappedRendererCapabilities(C.TRACK_TYPE_TEXT, firstRendererMappedCapabilities);
        // Second renderer handles german.
        Map<String, Integer> secondRendererMappedCapabilities = new HashMap<>();
        secondRendererMappedCapabilities.put(english.id, RendererCapabilities.FORMAT_UNSUPPORTED_SUBTYPE);
        secondRendererMappedCapabilities.put(german.id, RendererCapabilities.FORMAT_HANDLED);
        RendererCapabilities secondRendererCapabilities = new DefaultTrackSelectorTest.FakeMappedRendererCapabilities(C.TRACK_TYPE_TEXT, secondRendererMappedCapabilities);
        RendererCapabilities[] rendererCapabilities = new RendererCapabilities[]{ firstRendererCapabilities, secondRendererCapabilities };
        // Without an explicit language preference, nothing should be selected.
        TrackSelectorResult result = trackSelector.selectTracks(rendererCapabilities, DefaultTrackSelectorTest.wrapFormats(english, german));
        assertThat(result.selections.get(0)).isNull();
        assertThat(result.selections.get(1)).isNull();
        // Explicit language preference for english. First renderer should be used.
        trackSelector.setParameters(trackSelector.buildUponParameters().setPreferredTextLanguage("en"));
        result = trackSelector.selectTracks(rendererCapabilities, DefaultTrackSelectorTest.wrapFormats(english, german));
        assertThat(result.selections.get(0).getFormat(0)).isSameAs(english);
        assertThat(result.selections.get(1)).isNull();
        // Explicit language preference for German. Second renderer should be used.
        trackSelector.setParameters(trackSelector.buildUponParameters().setPreferredTextLanguage("de"));
        result = trackSelector.selectTracks(rendererCapabilities, DefaultTrackSelectorTest.wrapFormats(english, german));
        assertThat(result.selections.get(0)).isNull();
        assertThat(result.selections.get(1).getFormat(0)).isSameAs(german);
    }

    /**
     * Tests that track selector will select audio tracks with lower bitrate when {@link Parameters}
     * indicate lowest bitrate preference, even when tracks are within capabilities.
     */
    @Test
    public void testSelectTracksWithinCapabilitiesAndForceLowestBitrateSelectLowerBitrate() throws Exception {
        trackSelector.setParameters(new ParametersBuilder().setForceLowestBitrate(true).build());
        Format lowerBitrateFormat = Format.createAudioSampleFormat("audioFormat", AUDIO_AAC, null, 15000, NO_VALUE, 2, 44100, null, null, 0, null);
        Format higherBitrateFormat = Format.createAudioSampleFormat("audioFormat", AUDIO_AAC, null, 30000, NO_VALUE, 2, 44100, null, null, 0, null);
        TrackSelectorResult result = trackSelector.selectTracks(new RendererCapabilities[]{ DefaultTrackSelectorTest.ALL_AUDIO_FORMAT_SUPPORTED_RENDERER_CAPABILITIES }, DefaultTrackSelectorTest.singleTrackGroup(lowerBitrateFormat, higherBitrateFormat));
        assertThat(result.selections.get(0).getSelectedFormat()).isEqualTo(lowerBitrateFormat);
    }

    @Test
    public void testSelectTracksWithMultipleAudioTracksReturnsAdaptiveTrackSelection() throws Exception {
        TrackSelection adaptiveTrackSelection = Mockito.mock(TrackSelection.class);
        TrackSelection.Factory adaptiveTrackSelectionFactory = Mockito.mock(Factory.class);
        Mockito.when(adaptiveTrackSelectionFactory.createTrackSelection(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyVararg())).thenReturn(adaptiveTrackSelection);
        trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
        trackSelector.init(invalidationListener, bandwidthMeter);
        TrackGroupArray trackGroupArray = DefaultTrackSelectorTest.singleTrackGroup(DefaultTrackSelectorTest.AUDIO_FORMAT, DefaultTrackSelectorTest.AUDIO_FORMAT);
        TrackSelectorResult result = trackSelector.selectTracks(new RendererCapabilities[]{ DefaultTrackSelectorTest.AUDIO_CAPABILITIES }, trackGroupArray);
        assertThat(result.length).isEqualTo(1);
        assertThat(result.selections.get(0)).isEqualTo(adaptiveTrackSelection);
        Mockito.verify(adaptiveTrackSelectionFactory).createTrackSelection(trackGroupArray.get(0), bandwidthMeter, 0, 1);
    }

    @Test
    public void testSelectTracksWithMultipleAudioTracksOverrideReturnsAdaptiveTrackSelection() throws Exception {
        TrackSelection adaptiveTrackSelection = Mockito.mock(TrackSelection.class);
        TrackSelection.Factory adaptiveTrackSelectionFactory = Mockito.mock(Factory.class);
        Mockito.when(adaptiveTrackSelectionFactory.createTrackSelection(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyVararg())).thenReturn(adaptiveTrackSelection);
        trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
        trackSelector.init(invalidationListener, bandwidthMeter);
        TrackGroupArray trackGroupArray = DefaultTrackSelectorTest.singleTrackGroup(DefaultTrackSelectorTest.AUDIO_FORMAT, DefaultTrackSelectorTest.AUDIO_FORMAT, DefaultTrackSelectorTest.AUDIO_FORMAT);
        trackSelector.setParameters(/* rendererIndex= */
        trackSelector.buildUponParameters().setSelectionOverride(0, trackGroupArray, /* groupIndex= */
        /* tracks= */
        new SelectionOverride(0, 1, 2)));
        TrackSelectorResult result = trackSelector.selectTracks(new RendererCapabilities[]{ DefaultTrackSelectorTest.AUDIO_CAPABILITIES }, trackGroupArray);
        assertThat(result.length).isEqualTo(1);
        assertThat(result.selections.get(0)).isEqualTo(adaptiveTrackSelection);
        Mockito.verify(adaptiveTrackSelectionFactory).createTrackSelection(trackGroupArray.get(0), bandwidthMeter, 1, 2);
    }

    /**
     * Tests audio track selection when there are multiple audio renderers.
     */
    @Test
    public void testSelectPreferredAudioTrackMultipleRenderers() throws Exception {
        Format english = DefaultTrackSelectorTest.buildAudioFormat("en", "en");
        Format german = DefaultTrackSelectorTest.buildAudioFormat("de", "de");
        // First renderer handles english.
        Map<String, Integer> firstRendererMappedCapabilities = new HashMap<>();
        firstRendererMappedCapabilities.put(english.id, RendererCapabilities.FORMAT_HANDLED);
        firstRendererMappedCapabilities.put(german.id, RendererCapabilities.FORMAT_UNSUPPORTED_SUBTYPE);
        RendererCapabilities firstRendererCapabilities = new DefaultTrackSelectorTest.FakeMappedRendererCapabilities(C.TRACK_TYPE_AUDIO, firstRendererMappedCapabilities);
        // Second renderer handles german.
        Map<String, Integer> secondRendererMappedCapabilities = new HashMap<>();
        secondRendererMappedCapabilities.put(english.id, RendererCapabilities.FORMAT_UNSUPPORTED_SUBTYPE);
        secondRendererMappedCapabilities.put(german.id, RendererCapabilities.FORMAT_HANDLED);
        RendererCapabilities secondRendererCapabilities = new DefaultTrackSelectorTest.FakeMappedRendererCapabilities(C.TRACK_TYPE_AUDIO, secondRendererMappedCapabilities);
        RendererCapabilities[] rendererCapabilities = new RendererCapabilities[]{ firstRendererCapabilities, secondRendererCapabilities };
        // Without an explicit language preference, prefer the first renderer.
        TrackSelectorResult result = trackSelector.selectTracks(rendererCapabilities, DefaultTrackSelectorTest.wrapFormats(english, german));
        assertThat(result.selections.get(0).getFormat(0)).isSameAs(english);
        assertThat(result.selections.get(1)).isNull();
        // Explicit language preference for english. First renderer should be used.
        trackSelector.setParameters(trackSelector.buildUponParameters().setPreferredAudioLanguage("en"));
        result = trackSelector.selectTracks(rendererCapabilities, DefaultTrackSelectorTest.wrapFormats(english, german));
        assertThat(result.selections.get(0).getFormat(0)).isSameAs(english);
        assertThat(result.selections.get(1)).isNull();
        // Explicit language preference for German. Second renderer should be used.
        trackSelector.setParameters(trackSelector.buildUponParameters().setPreferredAudioLanguage("de"));
        result = trackSelector.selectTracks(rendererCapabilities, DefaultTrackSelectorTest.wrapFormats(english, german));
        assertThat(result.selections.get(0)).isNull();
        assertThat(result.selections.get(1).getFormat(0)).isSameAs(german);
    }

    @Test
    public void testSelectTracksWithMultipleVideoTracksReturnsAdaptiveTrackSelection() throws Exception {
        TrackSelection adaptiveTrackSelection = Mockito.mock(TrackSelection.class);
        TrackSelection.Factory adaptiveTrackSelectionFactory = Mockito.mock(Factory.class);
        Mockito.when(adaptiveTrackSelectionFactory.createTrackSelection(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyVararg())).thenReturn(adaptiveTrackSelection);
        trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
        trackSelector.init(invalidationListener, bandwidthMeter);
        TrackGroupArray trackGroupArray = DefaultTrackSelectorTest.singleTrackGroup(DefaultTrackSelectorTest.VIDEO_FORMAT, DefaultTrackSelectorTest.VIDEO_FORMAT);
        TrackSelectorResult result = trackSelector.selectTracks(new RendererCapabilities[]{ DefaultTrackSelectorTest.VIDEO_CAPABILITIES }, trackGroupArray);
        assertThat(result.length).isEqualTo(1);
        assertThat(result.selections.get(0)).isEqualTo(adaptiveTrackSelection);
        Mockito.verify(adaptiveTrackSelectionFactory).createTrackSelection(trackGroupArray.get(0), bandwidthMeter, 0, 1);
    }

    @Test
    public void testSelectTracksWithMultipleVideoTracksOverrideReturnsAdaptiveTrackSelection() throws Exception {
        TrackSelection adaptiveTrackSelection = Mockito.mock(TrackSelection.class);
        TrackSelection.Factory adaptiveTrackSelectionFactory = Mockito.mock(Factory.class);
        Mockito.when(adaptiveTrackSelectionFactory.createTrackSelection(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyVararg())).thenReturn(adaptiveTrackSelection);
        trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
        trackSelector.init(invalidationListener, bandwidthMeter);
        TrackGroupArray trackGroupArray = DefaultTrackSelectorTest.singleTrackGroup(DefaultTrackSelectorTest.VIDEO_FORMAT, DefaultTrackSelectorTest.VIDEO_FORMAT, DefaultTrackSelectorTest.VIDEO_FORMAT);
        trackSelector.setParameters(/* rendererIndex= */
        trackSelector.buildUponParameters().setSelectionOverride(0, trackGroupArray, /* groupIndex= */
        /* tracks= */
        new SelectionOverride(0, 1, 2)));
        TrackSelectorResult result = trackSelector.selectTracks(new RendererCapabilities[]{ DefaultTrackSelectorTest.VIDEO_CAPABILITIES }, trackGroupArray);
        assertThat(result.length).isEqualTo(1);
        assertThat(result.selections.get(0)).isEqualTo(adaptiveTrackSelection);
        Mockito.verify(adaptiveTrackSelectionFactory).createTrackSelection(trackGroupArray.get(0), bandwidthMeter, 1, 2);
    }

    /**
     * A {@link RendererCapabilities} that advertises support for all formats of a given type using
     * a provided support value. For any format that does not have the given track type,
     * {@link #supportsFormat(Format)} will return {@link #FORMAT_UNSUPPORTED_TYPE}.
     */
    private static final class FakeRendererCapabilities implements RendererCapabilities {
        private final int trackType;

        private final int supportValue;

        /**
         * Returns {@link FakeRendererCapabilities} that advertises adaptive support for all
         * tracks of the given type.
         *
         * @param trackType
         * 		the track type of all formats that this renderer capabilities advertises
         * 		support for.
         */
        FakeRendererCapabilities(int trackType) {
            this(trackType, ((RendererCapabilities.FORMAT_HANDLED) | (ADAPTIVE_SEAMLESS)));
        }

        /**
         * Returns {@link FakeRendererCapabilities} that advertises support level using given value
         * for all tracks of the given type.
         *
         * @param trackType
         * 		the track type of all formats that this renderer capabilities advertises
         * 		support for.
         * @param supportValue
         * 		the support level value that will be returned for formats with
         * 		the given type.
         */
        FakeRendererCapabilities(int trackType, int supportValue) {
            this.trackType = trackType;
            this.supportValue = supportValue;
        }

        @Override
        public int getTrackType() {
            return trackType;
        }

        @Override
        public int supportsFormat(Format format) throws ExoPlaybackException {
            return (MimeTypes.getTrackType(format.sampleMimeType)) == (trackType) ? supportValue : FORMAT_UNSUPPORTED_TYPE;
        }

        @Override
        public int supportsMixedMimeTypeAdaptation() throws ExoPlaybackException {
            return ADAPTIVE_SEAMLESS;
        }
    }

    /**
     * A {@link RendererCapabilities} that advertises support for different formats using a mapping
     * between format ID and format-support value.
     */
    private static final class FakeMappedRendererCapabilities implements RendererCapabilities {
        private final int trackType;

        private final Map<String, Integer> formatToCapability;

        /**
         * Returns {@link FakeRendererCapabilities} that advertises support level using the given
         * mapping between format ID and format-support value.
         *
         * @param trackType
         * 		the track type to be returned for {@link #getTrackType()}
         * @param formatToCapability
         * 		a map of (format id, support level) that will be used to return
         * 		support level for any given format. For any format that's not in the map,
         * 		{@link #supportsFormat(Format)} will return {@link #FORMAT_UNSUPPORTED_TYPE}.
         */
        FakeMappedRendererCapabilities(int trackType, Map<String, Integer> formatToCapability) {
            this.trackType = trackType;
            this.formatToCapability = new HashMap<>(formatToCapability);
        }

        @Override
        public int getTrackType() {
            return trackType;
        }

        @Override
        public int supportsFormat(Format format) throws ExoPlaybackException {
            return ((format.id) != null) && (formatToCapability.containsKey(format.id)) ? formatToCapability.get(format.id) : FORMAT_UNSUPPORTED_TYPE;
        }

        @Override
        public int supportsMixedMimeTypeAdaptation() throws ExoPlaybackException {
            return ADAPTIVE_SEAMLESS;
        }
    }
}

