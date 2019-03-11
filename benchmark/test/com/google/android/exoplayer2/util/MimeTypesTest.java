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
package com.google.android.exoplayer2.util;


import MimeTypes.AUDIO_AAC;
import MimeTypes.AUDIO_AC3;
import MimeTypes.AUDIO_DTS;
import MimeTypes.AUDIO_DTS_HD;
import MimeTypes.AUDIO_E_AC3;
import MimeTypes.AUDIO_E_AC3_JOC;
import MimeTypes.AUDIO_MPEG;
import MimeTypes.AUDIO_OPUS;
import MimeTypes.AUDIO_VORBIS;
import MimeTypes.VIDEO_H264;
import MimeTypes.VIDEO_H265;
import MimeTypes.VIDEO_MP4V;
import MimeTypes.VIDEO_MPEG2;
import MimeTypes.VIDEO_VP8;
import MimeTypes.VIDEO_VP9;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit test for {@link MimeTypes}.
 */
@RunWith(RobolectricTestRunner.class)
public final class MimeTypesTest {
    @Test
    public void testGetMediaMimeType_fromValidCodecs_returnsCorrectMimeType() {
        assertThat(MimeTypes.getMediaMimeType("avc1")).isEqualTo(VIDEO_H264);
        assertThat(MimeTypes.getMediaMimeType("avc1.42E01E")).isEqualTo(VIDEO_H264);
        assertThat(MimeTypes.getMediaMimeType("avc1.42E01F")).isEqualTo(VIDEO_H264);
        assertThat(MimeTypes.getMediaMimeType("avc1.4D401F")).isEqualTo(VIDEO_H264);
        assertThat(MimeTypes.getMediaMimeType("avc1.4D4028")).isEqualTo(VIDEO_H264);
        assertThat(MimeTypes.getMediaMimeType("avc1.640028")).isEqualTo(VIDEO_H264);
        assertThat(MimeTypes.getMediaMimeType("avc1.640029")).isEqualTo(VIDEO_H264);
        assertThat(MimeTypes.getMediaMimeType("avc3")).isEqualTo(VIDEO_H264);
        assertThat(MimeTypes.getMediaMimeType("hev1")).isEqualTo(VIDEO_H265);
        assertThat(MimeTypes.getMediaMimeType("hvc1")).isEqualTo(VIDEO_H265);
        assertThat(MimeTypes.getMediaMimeType("vp08")).isEqualTo(VIDEO_VP8);
        assertThat(MimeTypes.getMediaMimeType("vp8")).isEqualTo(VIDEO_VP8);
        assertThat(MimeTypes.getMediaMimeType("vp09")).isEqualTo(VIDEO_VP9);
        assertThat(MimeTypes.getMediaMimeType("vp9")).isEqualTo(VIDEO_VP9);
        assertThat(MimeTypes.getMediaMimeType("ac-3")).isEqualTo(AUDIO_AC3);
        assertThat(MimeTypes.getMediaMimeType("dac3")).isEqualTo(AUDIO_AC3);
        assertThat(MimeTypes.getMediaMimeType("dec3")).isEqualTo(AUDIO_E_AC3);
        assertThat(MimeTypes.getMediaMimeType("ec-3")).isEqualTo(AUDIO_E_AC3);
        assertThat(MimeTypes.getMediaMimeType("ec+3")).isEqualTo(AUDIO_E_AC3_JOC);
        assertThat(MimeTypes.getMediaMimeType("dtsc")).isEqualTo(AUDIO_DTS);
        assertThat(MimeTypes.getMediaMimeType("dtse")).isEqualTo(AUDIO_DTS);
        assertThat(MimeTypes.getMediaMimeType("dtsh")).isEqualTo(AUDIO_DTS_HD);
        assertThat(MimeTypes.getMediaMimeType("dtsl")).isEqualTo(AUDIO_DTS_HD);
        assertThat(MimeTypes.getMediaMimeType("opus")).isEqualTo(AUDIO_OPUS);
        assertThat(MimeTypes.getMediaMimeType("vorbis")).isEqualTo(AUDIO_VORBIS);
        assertThat(MimeTypes.getMediaMimeType("mp4a")).isEqualTo(AUDIO_AAC);
        assertThat(MimeTypes.getMediaMimeType("mp4a.40.02")).isEqualTo(AUDIO_AAC);
        assertThat(MimeTypes.getMediaMimeType("mp4a.40.05")).isEqualTo(AUDIO_AAC);
        assertThat(MimeTypes.getMediaMimeType("mp4a.40.2")).isEqualTo(AUDIO_AAC);
        assertThat(MimeTypes.getMediaMimeType("mp4a.40.5")).isEqualTo(AUDIO_AAC);
        assertThat(MimeTypes.getMediaMimeType("mp4a.40.29")).isEqualTo(AUDIO_AAC);
        assertThat(MimeTypes.getMediaMimeType("mp4a.66")).isEqualTo(AUDIO_AAC);
        assertThat(MimeTypes.getMediaMimeType("mp4a.67")).isEqualTo(AUDIO_AAC);
        assertThat(MimeTypes.getMediaMimeType("mp4a.68")).isEqualTo(AUDIO_AAC);
        assertThat(MimeTypes.getMediaMimeType("mp4a.69")).isEqualTo(AUDIO_MPEG);
        assertThat(MimeTypes.getMediaMimeType("mp4a.6B")).isEqualTo(AUDIO_MPEG);
        assertThat(MimeTypes.getMediaMimeType("mp4a.a5")).isEqualTo(AUDIO_AC3);
        assertThat(MimeTypes.getMediaMimeType("mp4a.A5")).isEqualTo(AUDIO_AC3);
        assertThat(MimeTypes.getMediaMimeType("mp4a.a6")).isEqualTo(AUDIO_E_AC3);
        assertThat(MimeTypes.getMediaMimeType("mp4a.A6")).isEqualTo(AUDIO_E_AC3);
        assertThat(MimeTypes.getMediaMimeType("mp4a.A9")).isEqualTo(AUDIO_DTS);
        assertThat(MimeTypes.getMediaMimeType("mp4a.AC")).isEqualTo(AUDIO_DTS);
        assertThat(MimeTypes.getMediaMimeType("mp4a.AA")).isEqualTo(AUDIO_DTS_HD);
        assertThat(MimeTypes.getMediaMimeType("mp4a.AB")).isEqualTo(AUDIO_DTS_HD);
        assertThat(MimeTypes.getMediaMimeType("mp4a.AD")).isEqualTo(AUDIO_OPUS);
    }

    @Test
    public void testGetMimeTypeFromMp4ObjectType_forValidObjectType_returnsCorrectMimeType() {
        assertThat(MimeTypes.getMimeTypeFromMp4ObjectType(96)).isEqualTo(VIDEO_MPEG2);
        assertThat(MimeTypes.getMimeTypeFromMp4ObjectType(97)).isEqualTo(VIDEO_MPEG2);
        assertThat(MimeTypes.getMimeTypeFromMp4ObjectType(32)).isEqualTo(VIDEO_MP4V);
        assertThat(MimeTypes.getMimeTypeFromMp4ObjectType(33)).isEqualTo(VIDEO_H264);
        assertThat(MimeTypes.getMimeTypeFromMp4ObjectType(35)).isEqualTo(VIDEO_H265);
        assertThat(MimeTypes.getMimeTypeFromMp4ObjectType(107)).isEqualTo(AUDIO_MPEG);
        assertThat(MimeTypes.getMimeTypeFromMp4ObjectType(64)).isEqualTo(AUDIO_AAC);
        assertThat(MimeTypes.getMimeTypeFromMp4ObjectType(102)).isEqualTo(AUDIO_AAC);
        assertThat(MimeTypes.getMimeTypeFromMp4ObjectType(103)).isEqualTo(AUDIO_AAC);
        assertThat(MimeTypes.getMimeTypeFromMp4ObjectType(104)).isEqualTo(AUDIO_AAC);
        assertThat(MimeTypes.getMimeTypeFromMp4ObjectType(165)).isEqualTo(AUDIO_AC3);
        assertThat(MimeTypes.getMimeTypeFromMp4ObjectType(166)).isEqualTo(AUDIO_E_AC3);
        assertThat(MimeTypes.getMimeTypeFromMp4ObjectType(169)).isEqualTo(AUDIO_DTS);
        assertThat(MimeTypes.getMimeTypeFromMp4ObjectType(172)).isEqualTo(AUDIO_DTS);
        assertThat(MimeTypes.getMimeTypeFromMp4ObjectType(170)).isEqualTo(AUDIO_DTS_HD);
        assertThat(MimeTypes.getMimeTypeFromMp4ObjectType(171)).isEqualTo(AUDIO_DTS_HD);
        assertThat(MimeTypes.getMimeTypeFromMp4ObjectType(173)).isEqualTo(AUDIO_OPUS);
    }

    @Test
    public void testGetMimeTypeFromMp4ObjectType_forInvalidObjectType_returnsNull() {
        assertThat(MimeTypes.getMimeTypeFromMp4ObjectType(0)).isNull();
        assertThat(MimeTypes.getMimeTypeFromMp4ObjectType(1536)).isNull();
        assertThat(MimeTypes.getMimeTypeFromMp4ObjectType(1)).isNull();
        assertThat(MimeTypes.getMimeTypeFromMp4ObjectType((-1))).isNull();
    }
}

