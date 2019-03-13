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


import com.google.android.exoplayer2.trackselection.TrackSelector.InvalidationListener;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit test for {@link TrackSelector}.
 */
@RunWith(RobolectricTestRunner.class)
public class TrackSelectorTest {
    private TrackSelector trackSelector;

    @Test
    public void getBandwidthMeter_beforeInitialization_throwsException() {
        try {
            trackSelector.getBandwidthMeter();
            Assert.fail();
        } catch (Exception e) {
            // Expected.
        }
    }

    @Test
    public void getBandwidthMeter_afterInitialization_returnsProvidedBandwidthMeter() {
        InvalidationListener invalidationListener = Mockito.mock(InvalidationListener.class);
        BandwidthMeter bandwidthMeter = Mockito.mock(BandwidthMeter.class);
        trackSelector.init(invalidationListener, bandwidthMeter);
        assertThat(trackSelector.getBandwidthMeter()).isEqualTo(bandwidthMeter);
    }
}

