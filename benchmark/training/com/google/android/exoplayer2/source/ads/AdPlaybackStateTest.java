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
package com.google.android.exoplayer2.source.ads;


import AdPlaybackState.AD_STATE_AVAILABLE;
import AdPlaybackState.AD_STATE_ERROR;
import AdPlaybackState.AD_STATE_UNAVAILABLE;
import C.LENGTH_UNSET;
import android.net.Uri;
import com.google.android.exoplayer2.C;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit test for {@link AdPlaybackState}.
 */
@RunWith(RobolectricTestRunner.class)
public final class AdPlaybackStateTest {
    private static final long[] TEST_AD_GROUP_TMES_US = new long[]{ 0, C.msToUs(10000) };

    private static final Uri TEST_URI = Uri.EMPTY;

    private AdPlaybackState state;

    @Test
    public void testSetAdCount() {
        assertThat(state.adGroups[0].count).isEqualTo(LENGTH_UNSET);
        state = /* adGroupIndex= */
        /* adCount= */
        state.withAdCount(0, 1);
        assertThat(state.adGroups[0].count).isEqualTo(1);
    }

    @Test
    public void testSetAdUriBeforeAdCount() {
        state = /* adGroupIndex= */
        /* adIndexInAdGroup= */
        state.withAdUri(0, 1, AdPlaybackStateTest.TEST_URI);
        state = /* adGroupIndex= */
        /* adCount= */
        state.withAdCount(0, 2);
        assertThat(state.adGroups[0].uris[0]).isNull();
        assertThat(state.adGroups[0].states[0]).isEqualTo(AD_STATE_UNAVAILABLE);
        assertThat(state.adGroups[0].uris[1]).isSameAs(AdPlaybackStateTest.TEST_URI);
        assertThat(state.adGroups[0].states[1]).isEqualTo(AD_STATE_AVAILABLE);
    }

    @Test
    public void testSetAdErrorBeforeAdCount() {
        state = /* adGroupIndex= */
        /* adIndexInAdGroup= */
        state.withAdLoadError(0, 0);
        state = /* adGroupIndex= */
        /* adCount= */
        state.withAdCount(0, 2);
        assertThat(state.adGroups[0].uris[0]).isNull();
        assertThat(state.adGroups[0].states[0]).isEqualTo(AD_STATE_ERROR);
        assertThat(state.adGroups[0].states[1]).isEqualTo(AD_STATE_UNAVAILABLE);
    }

    @Test
    public void testGetFirstAdIndexToPlayIsZero() {
        state = /* adGroupIndex= */
        /* adCount= */
        state.withAdCount(0, 3);
        state = /* adGroupIndex= */
        /* adIndexInAdGroup= */
        state.withAdUri(0, 0, AdPlaybackStateTest.TEST_URI);
        state = /* adGroupIndex= */
        /* adIndexInAdGroup= */
        state.withAdUri(0, 2, AdPlaybackStateTest.TEST_URI);
        assertThat(state.adGroups[0].getFirstAdIndexToPlay()).isEqualTo(0);
    }

    @Test
    public void testGetFirstAdIndexToPlaySkipsPlayedAd() {
        state = /* adGroupIndex= */
        /* adCount= */
        state.withAdCount(0, 3);
        state = /* adGroupIndex= */
        /* adIndexInAdGroup= */
        state.withAdUri(0, 0, AdPlaybackStateTest.TEST_URI);
        state = /* adGroupIndex= */
        /* adIndexInAdGroup= */
        state.withAdUri(0, 2, AdPlaybackStateTest.TEST_URI);
        state = /* adGroupIndex= */
        /* adIndexInAdGroup= */
        state.withPlayedAd(0, 0);
        assertThat(state.adGroups[0].getFirstAdIndexToPlay()).isEqualTo(1);
        assertThat(state.adGroups[0].states[1]).isEqualTo(AD_STATE_UNAVAILABLE);
        assertThat(state.adGroups[0].states[2]).isEqualTo(AD_STATE_AVAILABLE);
    }

    @Test
    public void testGetFirstAdIndexToPlaySkipsSkippedAd() {
        state = /* adGroupIndex= */
        /* adCount= */
        state.withAdCount(0, 3);
        state = /* adGroupIndex= */
        /* adIndexInAdGroup= */
        state.withAdUri(0, 0, AdPlaybackStateTest.TEST_URI);
        state = /* adGroupIndex= */
        /* adIndexInAdGroup= */
        state.withAdUri(0, 2, AdPlaybackStateTest.TEST_URI);
        state = /* adGroupIndex= */
        /* adIndexInAdGroup= */
        state.withSkippedAd(0, 0);
        assertThat(state.adGroups[0].getFirstAdIndexToPlay()).isEqualTo(1);
        assertThat(state.adGroups[0].states[1]).isEqualTo(AD_STATE_UNAVAILABLE);
        assertThat(state.adGroups[0].states[2]).isEqualTo(AD_STATE_AVAILABLE);
    }

    @Test
    public void testGetFirstAdIndexToPlaySkipsErrorAds() {
        state = /* adGroupIndex= */
        /* adCount= */
        state.withAdCount(0, 3);
        state = /* adGroupIndex= */
        /* adIndexInAdGroup= */
        state.withAdUri(0, 0, AdPlaybackStateTest.TEST_URI);
        state = /* adGroupIndex= */
        /* adIndexInAdGroup= */
        state.withAdUri(0, 2, AdPlaybackStateTest.TEST_URI);
        state = /* adGroupIndex= */
        /* adIndexInAdGroup= */
        state.withPlayedAd(0, 0);
        state = /* adGroupIndex= */
        /* adIndexInAdGroup= */
        state.withAdLoadError(0, 1);
        assertThat(state.adGroups[0].getFirstAdIndexToPlay()).isEqualTo(2);
    }

    @Test
    public void testGetNextAdIndexToPlaySkipsErrorAds() {
        state = /* adGroupIndex= */
        /* adCount= */
        state.withAdCount(0, 3);
        state = /* adGroupIndex= */
        /* adIndexInAdGroup= */
        state.withAdUri(0, 1, AdPlaybackStateTest.TEST_URI);
        state = /* adGroupIndex= */
        /* adIndexInAdGroup= */
        state.withAdLoadError(0, 1);
        assertThat(state.adGroups[0].getNextAdIndexToPlay(0)).isEqualTo(2);
    }

    @Test
    public void testSetAdStateTwiceThrows() {
        state = /* adGroupIndex= */
        /* adCount= */
        state.withAdCount(0, 1);
        state = /* adGroupIndex= */
        /* adIndexInAdGroup= */
        state.withPlayedAd(0, 0);
        try {
            /* adGroupIndex= */
            /* adIndexInAdGroup= */
            state.withAdLoadError(0, 0);
            Assert.fail();
        } catch (Exception e) {
            // Expected.
        }
    }

    @Test
    public void testSkipAllWithoutAdCount() {
        state = state.withSkippedAdGroup(0);
        state = state.withSkippedAdGroup(1);
        assertThat(state.adGroups[0].count).isEqualTo(0);
        assertThat(state.adGroups[1].count).isEqualTo(0);
    }
}

