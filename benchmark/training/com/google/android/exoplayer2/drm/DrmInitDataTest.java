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


import DrmInitData.CREATOR;
import android.os.Parcel;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.drm.DrmInitData.SchemeData;
import com.google.android.exoplayer2.testutil.TestUtil;
import com.google.android.exoplayer2.util.MimeTypes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit test for {@link DrmInitData}.
 */
@RunWith(RobolectricTestRunner.class)
public class DrmInitDataTest {
    private static final SchemeData DATA_1 = new SchemeData(C.WIDEVINE_UUID, MimeTypes.VIDEO_MP4, /* data seed */
    TestUtil.buildTestData(128, 1));

    private static final SchemeData DATA_2 = new SchemeData(C.PLAYREADY_UUID, MimeTypes.VIDEO_MP4, /* data seed */
    TestUtil.buildTestData(128, 2));

    private static final SchemeData DATA_1B = new SchemeData(C.WIDEVINE_UUID, MimeTypes.VIDEO_MP4, /* data seed */
    TestUtil.buildTestData(128, 1));

    private static final SchemeData DATA_2B = new SchemeData(C.PLAYREADY_UUID, MimeTypes.VIDEO_MP4, /* data seed */
    TestUtil.buildTestData(128, 2));

    private static final SchemeData DATA_UNIVERSAL = new SchemeData(C.UUID_NIL, MimeTypes.VIDEO_MP4, /* data seed */
    TestUtil.buildTestData(128, 3));

    @Test
    public void testParcelable() {
        DrmInitData drmInitDataToParcel = new DrmInitData(DrmInitDataTest.DATA_1, DrmInitDataTest.DATA_2);
        Parcel parcel = Parcel.obtain();
        drmInitDataToParcel.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        DrmInitData drmInitDataFromParcel = CREATOR.createFromParcel(parcel);
        assertThat(drmInitDataFromParcel).isEqualTo(drmInitDataToParcel);
        parcel.recycle();
    }

    @Test
    public void testEquals() {
        DrmInitData drmInitData = new DrmInitData(DrmInitDataTest.DATA_1, DrmInitDataTest.DATA_2);
        // Basic non-referential equality test.
        DrmInitData testInitData = new DrmInitData(DrmInitDataTest.DATA_1, DrmInitDataTest.DATA_2);
        assertThat(testInitData).isEqualTo(drmInitData);
        assertThat(testInitData.hashCode()).isEqualTo(drmInitData.hashCode());
        // Basic non-referential equality test with non-referential scheme data.
        testInitData = new DrmInitData(DrmInitDataTest.DATA_1B, DrmInitDataTest.DATA_2B);
        assertThat(testInitData).isEqualTo(drmInitData);
        assertThat(testInitData.hashCode()).isEqualTo(drmInitData.hashCode());
        // Passing the scheme data in reverse order shouldn't affect equality.
        testInitData = new DrmInitData(DrmInitDataTest.DATA_2, DrmInitDataTest.DATA_1);
        assertThat(testInitData).isEqualTo(drmInitData);
        assertThat(testInitData.hashCode()).isEqualTo(drmInitData.hashCode());
        // Ditto.
        testInitData = new DrmInitData(DrmInitDataTest.DATA_2B, DrmInitDataTest.DATA_1B);
        assertThat(testInitData).isEqualTo(drmInitData);
        assertThat(testInitData.hashCode()).isEqualTo(drmInitData.hashCode());
        // Different number of tuples should affect equality.
        testInitData = new DrmInitData(DrmInitDataTest.DATA_1);
        assertThat(drmInitData).isNotEqualTo(testInitData);
        // Different data in one of the tuples should affect equality.
        testInitData = new DrmInitData(DrmInitDataTest.DATA_1, DrmInitDataTest.DATA_UNIVERSAL);
        assertThat(testInitData).isNotEqualTo(drmInitData);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testGetByUuid() {
        // Basic matching.
        DrmInitData testInitData = new DrmInitData(DrmInitDataTest.DATA_1, DrmInitDataTest.DATA_2);
        assertThat(testInitData.get(C.WIDEVINE_UUID)).isEqualTo(DrmInitDataTest.DATA_1);
        assertThat(testInitData.get(C.PLAYREADY_UUID)).isEqualTo(DrmInitDataTest.DATA_2);
        assertThat(testInitData.get(C.UUID_NIL)).isNull();
        // Basic matching including universal data.
        testInitData = new DrmInitData(DrmInitDataTest.DATA_1, DrmInitDataTest.DATA_2, DrmInitDataTest.DATA_UNIVERSAL);
        assertThat(testInitData.get(C.WIDEVINE_UUID)).isEqualTo(DrmInitDataTest.DATA_1);
        assertThat(testInitData.get(C.PLAYREADY_UUID)).isEqualTo(DrmInitDataTest.DATA_2);
        assertThat(testInitData.get(C.UUID_NIL)).isEqualTo(DrmInitDataTest.DATA_UNIVERSAL);
        // Passing the scheme data in reverse order shouldn't affect equality.
        testInitData = new DrmInitData(DrmInitDataTest.DATA_UNIVERSAL, DrmInitDataTest.DATA_2, DrmInitDataTest.DATA_1);
        assertThat(testInitData.get(C.WIDEVINE_UUID)).isEqualTo(DrmInitDataTest.DATA_1);
        assertThat(testInitData.get(C.PLAYREADY_UUID)).isEqualTo(DrmInitDataTest.DATA_2);
        assertThat(testInitData.get(C.UUID_NIL)).isEqualTo(DrmInitDataTest.DATA_UNIVERSAL);
        // Universal data should be returned in the absence of a specific match.
        testInitData = new DrmInitData(DrmInitDataTest.DATA_1, DrmInitDataTest.DATA_UNIVERSAL);
        assertThat(testInitData.get(C.WIDEVINE_UUID)).isEqualTo(DrmInitDataTest.DATA_1);
        assertThat(testInitData.get(C.PLAYREADY_UUID)).isEqualTo(DrmInitDataTest.DATA_UNIVERSAL);
        assertThat(testInitData.get(C.UUID_NIL)).isEqualTo(DrmInitDataTest.DATA_UNIVERSAL);
    }

    @Test
    public void testGetByIndex() {
        DrmInitData testInitData = new DrmInitData(DrmInitDataTest.DATA_1, DrmInitDataTest.DATA_2);
        assertThat(getAllSchemeData(testInitData)).containsAllOf(DrmInitDataTest.DATA_1, DrmInitDataTest.DATA_2);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testSchemeDatasWithSameUuid() {
        DrmInitData testInitData = new DrmInitData(DrmInitDataTest.DATA_1, DrmInitDataTest.DATA_1B);
        assertThat(testInitData.schemeDataCount).isEqualTo(2);
        // Deprecated get method should return first entry.
        assertThat(testInitData.get(C.WIDEVINE_UUID)).isEqualTo(DrmInitDataTest.DATA_1);
        // Test retrieval of first and second entry.
        assertThat(testInitData.get(0)).isEqualTo(DrmInitDataTest.DATA_1);
        assertThat(testInitData.get(1)).isEqualTo(DrmInitDataTest.DATA_1B);
    }

    @Test
    public void testSchemeDataMatches() {
        assertThat(DrmInitDataTest.DATA_1.matches(C.WIDEVINE_UUID)).isTrue();
        assertThat(DrmInitDataTest.DATA_1.matches(C.PLAYREADY_UUID)).isFalse();
        assertThat(DrmInitDataTest.DATA_2.matches(C.UUID_NIL)).isFalse();
        assertThat(DrmInitDataTest.DATA_2.matches(C.WIDEVINE_UUID)).isFalse();
        assertThat(DrmInitDataTest.DATA_2.matches(C.PLAYREADY_UUID)).isTrue();
        assertThat(DrmInitDataTest.DATA_2.matches(C.UUID_NIL)).isFalse();
        assertThat(DrmInitDataTest.DATA_UNIVERSAL.matches(C.WIDEVINE_UUID)).isTrue();
        assertThat(DrmInitDataTest.DATA_UNIVERSAL.matches(C.PLAYREADY_UUID)).isTrue();
        assertThat(DrmInitDataTest.DATA_UNIVERSAL.matches(C.UUID_NIL)).isTrue();
    }
}

