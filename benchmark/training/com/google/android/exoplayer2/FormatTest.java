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
package com.google.android.exoplayer2;


import DrmInitData.SchemeData;
import Format.CREATOR;
import android.os.Parcel;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.id3.TextInformationFrame;
import com.google.android.exoplayer2.testutil.TestUtil;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.video.ColorInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static C.COLOR_RANGE_LIMITED;
import static C.COLOR_SPACE_BT709;
import static C.COLOR_TRANSFER_SDR;
import static C.ENCODING_PCM_24BIT;
import static C.SELECTION_FLAG_DEFAULT;
import static C.STEREO_MODE_TOP_BOTTOM;
import static C.UUID_NIL;
import static Format.NO_VALUE;
import static Format.OFFSET_SAMPLE_RELATIVE;


/**
 * Unit test for {@link Format}.
 */
@RunWith(RobolectricTestRunner.class)
public final class FormatTest {
    private static final List<byte[]> INIT_DATA;

    static {
        byte[] initData1 = new byte[]{ 1, 2, 3 };
        byte[] initData2 = new byte[]{ 4, 5, 6 };
        List<byte[]> initData = new ArrayList<>();
        initData.add(initData1);
        initData.add(initData2);
        INIT_DATA = Collections.unmodifiableList(initData);
    }

    @Test
    public void testParcelable() {
        DrmInitData.SchemeData drmData1 = new DrmInitData.SchemeData(C.WIDEVINE_UUID, MimeTypes.VIDEO_MP4, /* data seed */
        TestUtil.buildTestData(128, 1));
        DrmInitData.SchemeData drmData2 = new DrmInitData.SchemeData(UUID_NIL, MimeTypes.VIDEO_WEBM, /* data seed */
        TestUtil.buildTestData(128, 1));
        DrmInitData drmInitData = new DrmInitData(drmData1, drmData2);
        byte[] projectionData = new byte[]{ 1, 2, 3 };
        Metadata metadata = new Metadata(new TextInformationFrame("id1", "description1", "value1"), new TextInformationFrame("id2", "description2", "value2"));
        ColorInfo colorInfo = new ColorInfo(COLOR_SPACE_BT709, COLOR_RANGE_LIMITED, COLOR_TRANSFER_SDR, new byte[]{ 1, 2, 3, 4, 5, 6, 7 });
        Format formatToParcel = /* containerMimeType= */
        /* sampleMimeType= */
        /* bitrate= */
        /* maxInputSize= */
        /* width= */
        /* height= */
        /* frameRate= */
        /* rotationDegrees= */
        /* pixelWidthHeightRatio= */
        /* channelCount= */
        /* sampleRate= */
        /* encoderDelay= */
        /* encoderPadding= */
        /* accessibilityChannel= */
        new Format("id", "label", MimeTypes.VIDEO_MP4, MimeTypes.VIDEO_H264, "codec", 1024, 2048, 1920, 1080, 24, 90, 2, projectionData, STEREO_MODE_TOP_BOTTOM, colorInfo, 6, 44100, ENCODING_PCM_24BIT, 1001, 1002, SELECTION_FLAG_DEFAULT, "language", NO_VALUE, OFFSET_SAMPLE_RELATIVE, FormatTest.INIT_DATA, drmInitData, metadata);
        Parcel parcel = Parcel.obtain();
        formatToParcel.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Format formatFromParcel = CREATOR.createFromParcel(parcel);
        assertThat(formatFromParcel).isEqualTo(formatToParcel);
        parcel.recycle();
    }
}

