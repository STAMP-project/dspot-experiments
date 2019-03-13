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
package com.google.android.exoplayer2.drm;


import RobolectricUtil.CustomLooper;
import RobolectricUtil.CustomMessageQueue;
import android.util.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Tests {@link OfflineLicenseHelper}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(shadows = { CustomLooper.class, CustomMessageQueue.class })
public class OfflineLicenseHelperTest {
    private OfflineLicenseHelper<?> offlineLicenseHelper;

    @Mock
    private MediaDrmCallback mediaDrmCallback;

    @Mock
    private ExoMediaDrm<ExoMediaCrypto> mediaDrm;

    @Test
    public void testDownloadRenewReleaseKey() throws Exception {
        setStubLicenseAndPlaybackDurationValues(1000, 200);
        byte[] keySetId = new byte[]{ 2, 5, 8 };
        setStubKeySetId(keySetId);
        byte[] offlineLicenseKeySetId = offlineLicenseHelper.downloadLicense(OfflineLicenseHelperTest.newDrmInitData());
        OfflineLicenseHelperTest.assertOfflineLicenseKeySetIdEqual(keySetId, offlineLicenseKeySetId);
        byte[] keySetId2 = new byte[]{ 6, 7, 0, 1, 4 };
        setStubKeySetId(keySetId2);
        byte[] offlineLicenseKeySetId2 = offlineLicenseHelper.renewLicense(offlineLicenseKeySetId);
        OfflineLicenseHelperTest.assertOfflineLicenseKeySetIdEqual(keySetId2, offlineLicenseKeySetId2);
        offlineLicenseHelper.releaseLicense(offlineLicenseKeySetId2);
    }

    @Test
    public void testDownloadLicenseFailsIfNullInitData() throws Exception {
        try {
            offlineLicenseHelper.downloadLicense(null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // Expected.
        }
    }

    @Test
    public void testDownloadLicenseFailsIfNoKeySetIdIsReturned() throws Exception {
        setStubLicenseAndPlaybackDurationValues(1000, 200);
        byte[] offlineLicenseKeySetId = offlineLicenseHelper.downloadLicense(OfflineLicenseHelperTest.newDrmInitData());
        assertThat(offlineLicenseKeySetId).isNull();
    }

    @Test
    public void testDownloadLicenseDoesNotFailIfDurationNotAvailable() throws Exception {
        setDefaultStubKeySetId();
        byte[] offlineLicenseKeySetId = offlineLicenseHelper.downloadLicense(OfflineLicenseHelperTest.newDrmInitData());
        assertThat(offlineLicenseKeySetId).isNotNull();
    }

    @Test
    public void testGetLicenseDurationRemainingSec() throws Exception {
        long licenseDuration = 1000;
        int playbackDuration = 200;
        setStubLicenseAndPlaybackDurationValues(licenseDuration, playbackDuration);
        setDefaultStubKeySetId();
        byte[] offlineLicenseKeySetId = offlineLicenseHelper.downloadLicense(OfflineLicenseHelperTest.newDrmInitData());
        Pair<Long, Long> licenseDurationRemainingSec = offlineLicenseHelper.getLicenseDurationRemainingSec(offlineLicenseKeySetId);
        assertThat(licenseDurationRemainingSec.first).isEqualTo(licenseDuration);
        assertThat(licenseDurationRemainingSec.second).isEqualTo(playbackDuration);
    }

    @Test
    public void testGetLicenseDurationRemainingSecExpiredLicense() throws Exception {
        long licenseDuration = 0;
        int playbackDuration = 0;
        setStubLicenseAndPlaybackDurationValues(licenseDuration, playbackDuration);
        setDefaultStubKeySetId();
        byte[] offlineLicenseKeySetId = offlineLicenseHelper.downloadLicense(OfflineLicenseHelperTest.newDrmInitData());
        Pair<Long, Long> licenseDurationRemainingSec = offlineLicenseHelper.getLicenseDurationRemainingSec(offlineLicenseKeySetId);
        assertThat(licenseDurationRemainingSec.first).isEqualTo(licenseDuration);
        assertThat(licenseDurationRemainingSec.second).isEqualTo(playbackDuration);
    }
}

