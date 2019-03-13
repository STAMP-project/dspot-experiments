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
package com.google.android.exoplayer2.source.hls.playlist;


import C.CENC_TYPE_cbcs;
import C.CENC_TYPE_cenc;
import C.LENGTH_UNSET;
import C.PLAYREADY_UUID;
import C.WIDEVINE_UUID;
import HlsMediaPlaylist.PLAYLIST_TYPE_VOD;
import android.net.Uri;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist.Segment;
import com.google.android.exoplayer2.util.Util;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Test for {@link HlsMediaPlaylistParserTest}.
 */
@RunWith(RobolectricTestRunner.class)
public class HlsMediaPlaylistParserTest {
    @Test
    public void testParseMediaPlaylist() throws Exception {
        Uri playlistUri = Uri.parse("https://example.com/test.m3u8");
        String playlistString = "#EXTM3U\n" + ((((((((((((((((((((((((((((((("#EXT-X-VERSION:3\n" + "#EXT-X-PLAYLIST-TYPE:VOD\n") + "#EXT-X-START:TIME-OFFSET=-25") + "#EXT-X-TARGETDURATION:8\n") + "#EXT-X-MEDIA-SEQUENCE:2679\n") + "#EXT-X-DISCONTINUITY-SEQUENCE:4\n") + "#EXT-X-ALLOW-CACHE:YES\n") + "\n") + "#EXTINF:7.975,\n") + "#EXT-X-BYTERANGE:51370@0\n") + "https://priv.example.com/fileSequence2679.ts\n") + "\n") + "#EXT-X-KEY:METHOD=AES-128,") + "URI=\"https://priv.example.com/key.php?r=2680\",IV=0x1566B\n") + "#EXTINF:7.975,segment title\n") + "#EXT-X-BYTERANGE:51501@2147483648\n") + "https://priv.example.com/fileSequence2680.ts\n") + "\n") + "#EXT-X-KEY:METHOD=NONE\n") + "#EXTINF:7.941,segment title .,:/# with interesting chars\n") + "#EXT-X-BYTERANGE:51501\n")// @2147535149
         + "https://priv.example.com/fileSequence2681.ts\n") + "\n") + "#EXT-X-DISCONTINUITY\n") + "#EXT-X-KEY:METHOD=AES-128,URI=\"https://priv.example.com/key.php?r=2682\"\n") + "#EXTINF:7.975\n")// Trailing comma is omitted.
         + "#EXT-X-BYTERANGE:51740\n")// @2147586650
         + "https://priv.example.com/fileSequence2682.ts\n") + "\n") + "#EXTINF:7.975,\n") + "https://priv.example.com/fileSequence2683.ts\n") + "#EXT-X-ENDLIST");
        InputStream inputStream = new ByteArrayInputStream(Util.getUtf8Bytes(playlistString));
        HlsPlaylist playlist = new HlsPlaylistParser().parse(playlistUri, inputStream);
        HlsMediaPlaylist mediaPlaylist = ((HlsMediaPlaylist) (playlist));
        assertThat(mediaPlaylist.playlistType).isEqualTo(PLAYLIST_TYPE_VOD);
        assertThat(mediaPlaylist.startOffsetUs).isEqualTo(((mediaPlaylist.durationUs) - 25000000));
        assertThat(mediaPlaylist.mediaSequence).isEqualTo(2679);
        assertThat(mediaPlaylist.version).isEqualTo(3);
        assertThat(mediaPlaylist.hasEndTag).isTrue();
        assertThat(mediaPlaylist.protectionSchemes).isNull();
        List<Segment> segments = mediaPlaylist.segments;
        assertThat(segments).isNotNull();
        assertThat(segments).hasSize(5);
        Segment segment = segments.get(0);
        assertThat(((mediaPlaylist.discontinuitySequence) + (segment.relativeDiscontinuitySequence))).isEqualTo(4);
        assertThat(segment.durationUs).isEqualTo(7975000);
        assertThat(segment.title).isEqualTo("");
        assertThat(segment.fullSegmentEncryptionKeyUri).isNull();
        assertThat(segment.encryptionIV).isNull();
        assertThat(segment.byterangeLength).isEqualTo(51370);
        assertThat(segment.byterangeOffset).isEqualTo(0);
        assertThat(segment.url).isEqualTo("https://priv.example.com/fileSequence2679.ts");
        segment = segments.get(1);
        assertThat(segment.relativeDiscontinuitySequence).isEqualTo(0);
        assertThat(segment.durationUs).isEqualTo(7975000);
        assertThat(segment.title).isEqualTo("segment title");
        assertThat(segment.fullSegmentEncryptionKeyUri).isEqualTo("https://priv.example.com/key.php?r=2680");
        assertThat(segment.encryptionIV).isEqualTo("0x1566B");
        assertThat(segment.byterangeLength).isEqualTo(51501);
        assertThat(segment.byterangeOffset).isEqualTo(2147483648L);
        assertThat(segment.url).isEqualTo("https://priv.example.com/fileSequence2680.ts");
        segment = segments.get(2);
        assertThat(segment.relativeDiscontinuitySequence).isEqualTo(0);
        assertThat(segment.durationUs).isEqualTo(7941000);
        assertThat(segment.title).isEqualTo("segment title .,:/# with interesting chars");
        assertThat(segment.fullSegmentEncryptionKeyUri).isNull();
        assertThat(segment.encryptionIV).isEqualTo(null);
        assertThat(segment.byterangeLength).isEqualTo(51501);
        assertThat(segment.byterangeOffset).isEqualTo(2147535149L);
        assertThat(segment.url).isEqualTo("https://priv.example.com/fileSequence2681.ts");
        segment = segments.get(3);
        assertThat(segment.relativeDiscontinuitySequence).isEqualTo(1);
        assertThat(segment.durationUs).isEqualTo(7975000);
        assertThat(segment.title).isEqualTo("");
        assertThat(segment.fullSegmentEncryptionKeyUri).isEqualTo("https://priv.example.com/key.php?r=2682");
        // 0xA7A == 2682.
        assertThat(segment.encryptionIV).isNotNull();
        assertThat(Util.toUpperInvariant(segment.encryptionIV)).isEqualTo("A7A");
        assertThat(segment.byterangeLength).isEqualTo(51740);
        assertThat(segment.byterangeOffset).isEqualTo(2147586650L);
        assertThat(segment.url).isEqualTo("https://priv.example.com/fileSequence2682.ts");
        segment = segments.get(4);
        assertThat(segment.relativeDiscontinuitySequence).isEqualTo(1);
        assertThat(segment.durationUs).isEqualTo(7975000);
        assertThat(segment.title).isEqualTo("");
        assertThat(segment.fullSegmentEncryptionKeyUri).isEqualTo("https://priv.example.com/key.php?r=2682");
        // 0xA7B == 2683.
        assertThat(segment.encryptionIV).isNotNull();
        assertThat(Util.toUpperInvariant(segment.encryptionIV)).isEqualTo("A7B");
        assertThat(segment.byterangeLength).isEqualTo(LENGTH_UNSET);
        assertThat(segment.byterangeOffset).isEqualTo(0);
        assertThat(segment.url).isEqualTo("https://priv.example.com/fileSequence2683.ts");
    }

    @Test
    public void testParseSampleAesMethod() throws Exception {
        Uri playlistUri = Uri.parse("https://example.com/test.m3u8");
        String playlistString = "#EXTM3U\n" + (((((((((("#EXT-X-MEDIA-SEQUENCE:0\n" + "#EXTINF:8,\n") + "https://priv.example.com/1.ts\n") + "\n") + "#EXT-X-KEY:METHOD=SAMPLE-AES,URI=") + "\"data:text/plain;base64,VGhpcyBpcyBhbiBlYXN0ZXIgZWdn\",") + "IV=0x9358382AEB449EE23C3D809DA0B9CCD3,KEYFORMATVERSIONS=\"1\",") + "KEYFORMAT=\"com.widevine\",IV=0x1566B\n") + "#EXTINF:8,\n") + "https://priv.example.com/2.ts\n") + "#EXT-X-ENDLIST\n");
        InputStream inputStream = new ByteArrayInputStream(Util.getUtf8Bytes(playlistString));
        HlsMediaPlaylist playlist = ((HlsMediaPlaylist) (new HlsPlaylistParser().parse(playlistUri, inputStream)));
        assertThat(playlist.protectionSchemes.schemeType).isEqualTo(CENC_TYPE_cbcs);
        assertThat(playlist.protectionSchemes.get(0).matches(WIDEVINE_UUID)).isTrue();
        assertThat(playlist.protectionSchemes.get(0).hasData()).isFalse();
        assertThat(playlist.segments.get(0).drmInitData).isNull();
        assertThat(playlist.segments.get(1).drmInitData.get(0).matches(WIDEVINE_UUID)).isTrue();
        assertThat(playlist.segments.get(1).drmInitData.get(0).hasData()).isTrue();
    }

    @Test
    public void testParseSampleAesCencMethod() throws Exception {
        Uri playlistUri = Uri.parse("https://example.com/test.m3u8");
        String playlistString = "#EXTM3U\n" + (((((((((("#EXT-X-MEDIA-SEQUENCE:0\n" + "#EXTINF:8,\n") + "https://priv.example.com/1.ts\n") + "\n") + "#EXT-X-KEY:URI=\"data:text/plain;base64,VGhpcyBpcyBhbiBlYXN0ZXIgZWdn\",") + "IV=0x9358382AEB449EE23C3D809DA0B9CCD3,KEYFORMATVERSIONS=\"1\",") + "KEYFORMAT=\"urn:uuid:edef8ba9-79d6-4ace-a3c8-27dcd51d21ed\",") + "IV=0x1566B,METHOD=SAMPLE-AES-CENC \n") + "#EXTINF:8,\n") + "https://priv.example.com/2.ts\n") + "#EXT-X-ENDLIST\n");
        InputStream inputStream = new ByteArrayInputStream(Util.getUtf8Bytes(playlistString));
        HlsMediaPlaylist playlist = ((HlsMediaPlaylist) (new HlsPlaylistParser().parse(playlistUri, inputStream)));
        assertThat(playlist.protectionSchemes.schemeType).isEqualTo(CENC_TYPE_cenc);
        assertThat(playlist.protectionSchemes.get(0).matches(WIDEVINE_UUID)).isTrue();
        assertThat(playlist.protectionSchemes.get(0).hasData()).isFalse();
    }

    @Test
    public void testParseSampleAesCtrMethod() throws Exception {
        Uri playlistUri = Uri.parse("https://example.com/test.m3u8");
        String playlistString = "#EXTM3U\n" + (((((((((("#EXT-X-MEDIA-SEQUENCE:0\n" + "#EXTINF:8,\n") + "https://priv.example.com/1.ts\n") + "\n") + "#EXT-X-KEY:METHOD=SAMPLE-AES-CTR,URI=") + "\"data:text/plain;base64,VGhpcyBpcyBhbiBlYXN0ZXIgZWdn\",") + "IV=0x9358382AEB449EE23C3D809DA0B9CCD3,KEYFORMATVERSIONS=\"1\",") + "KEYFORMAT=\"com.widevine\",IV=0x1566B\n") + "#EXTINF:8,\n") + "https://priv.example.com/2.ts\n") + "#EXT-X-ENDLIST\n");
        InputStream inputStream = new ByteArrayInputStream(Util.getUtf8Bytes(playlistString));
        HlsMediaPlaylist playlist = ((HlsMediaPlaylist) (new HlsPlaylistParser().parse(playlistUri, inputStream)));
        assertThat(playlist.protectionSchemes.schemeType).isEqualTo(CENC_TYPE_cenc);
        assertThat(playlist.protectionSchemes.get(0).matches(WIDEVINE_UUID)).isTrue();
        assertThat(playlist.protectionSchemes.get(0).hasData()).isFalse();
    }

    @Test
    public void testMultipleExtXKeysForSingleSegment() throws Exception {
        Uri playlistUri = Uri.parse("https://example.com/test.m3u8");
        String playlistString = "#EXTM3U\n" + (((((((((((((((((((((((((((((((((((((("#EXT-X-VERSION:6\n" + "#EXT-X-TARGETDURATION:6\n") + "#EXT-X-MAP:URI=\"map.mp4\"\n") + "#EXTINF:5.005,\n") + "s000000.mp4\n") + "#EXT-X-KEY:METHOD=SAMPLE-AES,") + "KEYFORMAT=\"urn:uuid:edef8ba9-79d6-4ace-a3c8-27dcd51d21ed\",") + "KEYFORMATVERSIONS=\"1\",") + "URI=\"data:text/plain;base64,Tm90aGluZyB0byBzZWUgaGVyZQ==\"\n") + "#EXT-X-KEY:METHOD=SAMPLE-AES,KEYFORMAT=\"com.microsoft.playready\",") + "KEYFORMATVERSIONS=\"1\",") + "URI=\"data:text/plain;charset=UTF-16;base64,VGhpcyBpcyBhbiBlYXN0ZXIgZWdn\"\n") + "#EXT-X-KEY:METHOD=SAMPLE-AES,KEYFORMAT=\"com.apple.streamingkeydelivery\",") + "KEYFORMATVERSIONS=\"1\",URI=\"skd://QW5vdGhlciBlYXN0ZXIgZWdn\"\n") + "#EXT-X-MAP:URI=\"map.mp4\"\n") + "#EXTINF:5.005,\n") + "s000000.mp4\n") + "#EXTINF:5.005,\n") + "s000001.mp4\n") + "#EXT-X-KEY:METHOD=SAMPLE-AES,") + "KEYFORMAT=\"urn:uuid:edef8ba9-79d6-4ace-a3c8-27dcd51d21ed\",") + "KEYFORMATVERSIONS=\"1\",") + "URI=\"data:text/plain;base64,RG9uJ3QgeW91IGdldCB0aXJlZCBvZiBkb2luZyB0aGlzPw==\"") + "\n") + "#EXT-X-KEY:METHOD=SAMPLE-AES,KEYFORMAT=\"com.microsoft.playready\",") + "KEYFORMATVERSIONS=\"1\",") + "URI=\"data:text/plain;charset=UTF-16;base64,T2ssIGl0J3Mgbm90IGZ1biBhbnltb3Jl\"\n") + "#EXT-X-KEY:METHOD=SAMPLE-AES,KEYFORMAT=\"com.apple.streamingkeydelivery\",") + "KEYFORMATVERSIONS=\"1\",") + "URI=\"skd://V2FpdCB1bnRpbCB5b3Ugc2VlIHRoZSBuZXh0IG9uZSE=\"\n") + "#EXTINF:5.005,\n") + "s000024.mp4\n") + "#EXTINF:5.005,\n") + "s000025.mp4\n") + "#EXT-X-KEY:METHOD=NONE\n") + "#EXTINF:5.005,\n") + "s000026.mp4\n") + "#EXTINF:5.005,\n") + "s000026.mp4\n");
        InputStream inputStream = new ByteArrayInputStream(Util.getUtf8Bytes(playlistString));
        HlsMediaPlaylist playlist = ((HlsMediaPlaylist) (new HlsPlaylistParser().parse(playlistUri, inputStream)));
        assertThat(playlist.protectionSchemes.schemeType).isEqualTo(CENC_TYPE_cbcs);
        // Unsupported protection schemes like com.apple.streamingkeydelivery are ignored.
        assertThat(playlist.protectionSchemes.schemeDataCount).isEqualTo(2);
        assertThat(playlist.protectionSchemes.get(0).matches(PLAYREADY_UUID)).isTrue();
        assertThat(playlist.protectionSchemes.get(0).hasData()).isFalse();
        assertThat(playlist.protectionSchemes.get(1).matches(WIDEVINE_UUID)).isTrue();
        assertThat(playlist.protectionSchemes.get(1).hasData()).isFalse();
        assertThat(playlist.segments.get(0).drmInitData).isNull();
        assertThat(playlist.segments.get(1).drmInitData.get(0).matches(PLAYREADY_UUID)).isTrue();
        assertThat(playlist.segments.get(1).drmInitData.get(0).hasData()).isTrue();
        assertThat(playlist.segments.get(1).drmInitData.get(1).matches(WIDEVINE_UUID)).isTrue();
        assertThat(playlist.segments.get(1).drmInitData.get(1).hasData()).isTrue();
        assertThat(playlist.segments.get(1).drmInitData).isEqualTo(playlist.segments.get(2).drmInitData);
        assertThat(playlist.segments.get(2).drmInitData).isNotEqualTo(playlist.segments.get(3).drmInitData);
        assertThat(playlist.segments.get(3).drmInitData.get(0).matches(PLAYREADY_UUID)).isTrue();
        assertThat(playlist.segments.get(3).drmInitData.get(0).hasData()).isTrue();
        assertThat(playlist.segments.get(3).drmInitData.get(1).matches(WIDEVINE_UUID)).isTrue();
        assertThat(playlist.segments.get(3).drmInitData.get(1).hasData()).isTrue();
        assertThat(playlist.segments.get(3).drmInitData).isEqualTo(playlist.segments.get(4).drmInitData);
        assertThat(playlist.segments.get(5).drmInitData).isNull();
        assertThat(playlist.segments.get(6).drmInitData).isNull();
    }

    @Test
    public void testGapTag() throws IOException {
        Uri playlistUri = Uri.parse("https://example.com/test2.m3u8");
        String playlistString = "#EXTM3U\n" + ((((((((((((((((((("#EXT-X-VERSION:3\n" + "#EXT-X-TARGETDURATION:5\n") + "#EXT-X-PLAYLIST-TYPE:VOD\n") + "#EXT-X-MEDIA-SEQUENCE:0\n") + "#EXT-X-PROGRAM-DATE-TIME:2016-09-22T02:00:01+00:00\n") + "#EXT-X-KEY:METHOD=AES-128,URI=\"https://example.com/key?value=something\"\n") + "#EXTINF:5.005,\n") + "02/00/27.ts\n") + "#EXTINF:5.005,\n") + "02/00/32.ts\n") + "#EXT-X-KEY:METHOD=NONE\n") + "#EXTINF:5.005,\n") + "#EXT-X-GAP \n") + "../dummy.ts\n") + "#EXT-X-KEY:METHOD=AES-128,URI=\"https://key-service.bamgrid.com/1.0/key?") + "hex-value=9FB8989D15EEAAF8B21B860D7ED3072A\",IV=0x410C8AC18AA42EFA18B5155484F5FC34\n") + "#EXTINF:5.005,\n") + "02/00/42.ts\n") + "#EXTINF:5.005,\n") + "02/00/47.ts\n");
        InputStream inputStream = new ByteArrayInputStream(Util.getUtf8Bytes(playlistString));
        HlsMediaPlaylist playlist = ((HlsMediaPlaylist) (new HlsPlaylistParser().parse(playlistUri, inputStream)));
        assertThat(playlist.hasEndTag).isFalse();
        assertThat(playlist.segments.get(1).hasGapTag).isFalse();
        assertThat(playlist.segments.get(2).hasGapTag).isTrue();
        assertThat(playlist.segments.get(3).hasGapTag).isFalse();
    }

    @Test
    public void testMapTag() throws IOException {
        Uri playlistUri = Uri.parse("https://example.com/test3.m3u8");
        String playlistString = "#EXTM3U\n" + (((((((((((("#EXT-X-VERSION:3\n" + "#EXT-X-TARGETDURATION:5\n") + "#EXT-X-MEDIA-SEQUENCE:10\n") + "#EXTINF:5.005,\n") + "02/00/27.ts\n") + "#EXT-X-MAP:URI=\"init1.ts\"") + "#EXTINF:5.005,\n") + "02/00/32.ts\n") + "#EXTINF:5.005,\n") + "02/00/42.ts\n") + "#EXT-X-MAP:URI=\"init2.ts\"") + "#EXTINF:5.005,\n") + "02/00/47.ts\n");
        InputStream inputStream = new ByteArrayInputStream(Util.getUtf8Bytes(playlistString));
        HlsMediaPlaylist playlist = ((HlsMediaPlaylist) (new HlsPlaylistParser().parse(playlistUri, inputStream)));
        List<Segment> segments = playlist.segments;
        assertThat(segments.get(0).initializationSegment).isNull();
        assertThat(segments.get(1).initializationSegment).isSameAs(segments.get(2).initializationSegment);
        assertThat(segments.get(1).initializationSegment.url).isEqualTo("init1.ts");
        assertThat(segments.get(3).initializationSegment.url).isEqualTo("init2.ts");
    }

    @Test
    public void testMasterPlaylistAttributeInheritance() throws IOException {
        Uri playlistUri = Uri.parse("https://example.com/test3.m3u8");
        String playlistString = "#EXTM3U\n" + (((((((((((("#EXT-X-VERSION:3\n" + "#EXT-X-TARGETDURATION:5\n") + "#EXT-X-MEDIA-SEQUENCE:10\n") + "#EXTINF:5.005,\n") + "02/00/27.ts\n") + "#EXT-X-MAP:URI=\"init1.ts\"") + "#EXTINF:5.005,\n") + "02/00/32.ts\n") + "#EXTINF:5.005,\n") + "02/00/42.ts\n") + "#EXT-X-MAP:URI=\"init2.ts\"") + "#EXTINF:5.005,\n") + "02/00/47.ts\n");
        InputStream inputStream = new ByteArrayInputStream(Util.getUtf8Bytes(playlistString));
        HlsMediaPlaylist standalonePlaylist = ((HlsMediaPlaylist) (new HlsPlaylistParser().parse(playlistUri, inputStream)));
        assertThat(standalonePlaylist.hasIndependentSegments).isFalse();
        inputStream.reset();
        HlsMasterPlaylist masterPlaylist = /* baseUri= */
        /* tags= */
        /* variants= */
        /* audios= */
        /* subtitles= */
        /* muxedAudioFormat= */
        /* muxedCaptionFormats= */
        /* hasIndependentSegments= */
        /* variableDefinitions */
        new HlsMasterPlaylist("https://example.com/", Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), null, null, true, Collections.emptyMap());
        HlsMediaPlaylist playlistWithInheritance = ((HlsMediaPlaylist) (new HlsPlaylistParser(masterPlaylist).parse(playlistUri, inputStream)));
        assertThat(playlistWithInheritance.hasIndependentSegments).isTrue();
    }

    @Test
    public void testVariableSubstitution() throws IOException {
        Uri playlistUri = Uri.parse("https://example.com/substitution.m3u8");
        String playlistString = "#EXTM3U\n" + ((((((((("#EXT-X-VERSION:8\n" + "#EXT-X-DEFINE:NAME=\"underscore_1\",VALUE=\"{\"\n") + "#EXT-X-DEFINE:NAME=\"dash-1\",VALUE=\"replaced_value.ts\"\n") + "#EXT-X-TARGETDURATION:5\n") + "#EXT-X-MEDIA-SEQUENCE:10\n") + "#EXTINF:5.005,\n") + "segment1.ts\n") + "#EXT-X-MAP:URI=\"{$dash-1}\"") + "#EXTINF:5.005,\n") + "segment{$underscore_1}$name_1}\n");
        InputStream inputStream = new ByteArrayInputStream(Util.getUtf8Bytes(playlistString));
        HlsMediaPlaylist playlist = ((HlsMediaPlaylist) (new HlsPlaylistParser().parse(playlistUri, inputStream)));
        Segment segment = playlist.segments.get(1);
        assertThat(segment.initializationSegment.url).isEqualTo("replaced_value.ts");
        assertThat(segment.url).isEqualTo("segment{$name_1}");
    }

    @Test
    public void testInheritedVariableSubstitution() throws IOException {
        Uri playlistUri = Uri.parse("https://example.com/test3.m3u8");
        String playlistString = "#EXTM3U\n" + ((((((((((("#EXT-X-VERSION:8\n" + "#EXT-X-TARGETDURATION:5\n") + "#EXT-X-MEDIA-SEQUENCE:10\n") + "#EXT-X-DEFINE:IMPORT=\"imported_base\"\n") + "#EXTINF:5.005,\n") + "{$imported_base}1.ts\n") + "#EXTINF:5.005,\n") + "{$imported_base}2.ts\n") + "#EXTINF:5.005,\n") + "{$imported_base}3.ts\n") + "#EXTINF:5.005,\n") + "{$imported_base}4.ts\n");
        InputStream inputStream = new ByteArrayInputStream(Util.getUtf8Bytes(playlistString));
        HashMap<String, String> variableDefinitions = new HashMap<>();
        variableDefinitions.put("imported_base", "long_path");
        HlsMasterPlaylist masterPlaylist = /* baseUri= */
        /* tags= */
        /* variants= */
        /* audios= */
        /* subtitles= */
        /* muxedAudioFormat= */
        /* muxedCaptionFormats= */
        /* hasIndependentSegments= */
        new HlsMasterPlaylist("", Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), null, Collections.emptyList(), false, variableDefinitions);
        HlsMediaPlaylist playlist = ((HlsMediaPlaylist) (new HlsPlaylistParser(masterPlaylist).parse(playlistUri, inputStream)));
        for (int i = 1; i <= 4; i++) {
            assertThat(playlist.segments.get((i - 1)).url).isEqualTo((("long_path" + i) + ".ts"));
        }
    }
}

