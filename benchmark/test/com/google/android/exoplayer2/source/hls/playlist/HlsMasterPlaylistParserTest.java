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


import Format.NO_VALUE;
import HlsMasterPlaylist.HlsUrl;
import MimeTypes.APPLICATION_CEA708;
import MimeTypes.AUDIO_AAC;
import MimeTypes.AUDIO_AC3;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Test for {@link HlsMasterPlaylistParserTest}.
 */
@RunWith(RobolectricTestRunner.class)
public class HlsMasterPlaylistParserTest {
    private static final String PLAYLIST_URI = "https://example.com/test.m3u8";

    private static final String PLAYLIST_SIMPLE = " #EXTM3U \n" + ((((((((((((((("\n" + "#EXT-X-STREAM-INF:BANDWIDTH=1280000,") + "CODECS=\"mp4a.40.2,avc1.66.30\",RESOLUTION=304x128\n") + "http://example.com/low.m3u8\n") + "\n") + "#EXT-X-STREAM-INF:BANDWIDTH=1280000,CODECS=\"mp4a.40.2 , avc1.66.30 \"\n") + "http://example.com/spaces_in_codecs.m3u8\n") + "\n") + "#EXT-X-STREAM-INF:BANDWIDTH=2560000,FRAME-RATE=25,RESOLUTION=384x160\n") + "http://example.com/mid.m3u8\n") + "\n") + "#EXT-X-STREAM-INF:BANDWIDTH=7680000,FRAME-RATE=29.997\n") + "http://example.com/hi.m3u8\n") + "\n") + "#EXT-X-STREAM-INF:BANDWIDTH=65000,CODECS=\"mp4a.40.5\"\n") + "http://example.com/audio-only.m3u8");

    private static final String PLAYLIST_WITH_AVG_BANDWIDTH = " #EXTM3U \n" + ((((((("\n" + "#EXT-X-STREAM-INF:BANDWIDTH=1280000,") + "CODECS=\"mp4a.40.2,avc1.66.30\",RESOLUTION=304x128\n") + "http://example.com/low.m3u8\n") + "\n") + "#EXT-X-STREAM-INF:BANDWIDTH=1280000,AVERAGE-BANDWIDTH=1270000,") + "CODECS=\"mp4a.40.2 , avc1.66.30 \"\n") + "http://example.com/spaces_in_codecs.m3u8\n");

    private static final String PLAYLIST_WITH_INVALID_HEADER = "#EXTMU3\n" + (("#EXT-X-STREAM-INF:BANDWIDTH=1280000," + "CODECS=\"mp4a.40.2,avc1.66.30\",RESOLUTION=304x128\n") + "http://example.com/low.m3u8\n");

    private static final String PLAYLIST_WITH_CC = " #EXTM3U \n" + (((("#EXT-X-MEDIA:TYPE=CLOSED-CAPTIONS,GROUP-ID=\"cc1\"," + "LANGUAGE=\"es\",NAME=\"Eng\",INSTREAM-ID=\"SERVICE4\"\n") + "#EXT-X-STREAM-INF:BANDWIDTH=1280000,") + "CODECS=\"mp4a.40.2,avc1.66.30\",RESOLUTION=304x128\n") + "http://example.com/low.m3u8\n");

    private static final String PLAYLIST_WITH_CHANNELS_ATTRIBUTE = " #EXTM3U \n" + (((((((("#EXT-X-MEDIA:TYPE=AUDIO,GROUP-ID=\"audio\",CHANNELS=\"6\",NAME=\"Eng6\"," + "URI=\"something.m3u8\"\n") + "#EXT-X-MEDIA:TYPE=AUDIO,GROUP-ID=\"audio\",CHANNELS=\"2/6\",NAME=\"Eng26\",") + "URI=\"something2.m3u8\"\n") + "#EXT-X-MEDIA:TYPE=AUDIO,GROUP-ID=\"audio\",NAME=\"Eng\",") + "URI=\"something3.m3u8\"\n") + "#EXT-X-STREAM-INF:BANDWIDTH=1280000,") + "CODECS=\"mp4a.40.2,avc1.66.30\",AUDIO=\"audio\",RESOLUTION=304x128\n") + "http://example.com/low.m3u8\n");

    private static final String PLAYLIST_WITHOUT_CC = " #EXTM3U \n" + ((((("#EXT-X-MEDIA:TYPE=CLOSED-CAPTIONS," + "LANGUAGE=\"es\",NAME=\"Eng\",INSTREAM-ID=\"SERVICE4\"\n") + "#EXT-X-STREAM-INF:BANDWIDTH=1280000,") + "CODECS=\"mp4a.40.2,avc1.66.30\",RESOLUTION=304x128,") + "CLOSED-CAPTIONS=NONE\n") + "http://example.com/low.m3u8\n");

    private static final String PLAYLIST_WITH_SUBTITLES = " #EXTM3U \n" + (((("#EXT-X-MEDIA:TYPE=SUBTITLES,GROUP-ID=\"sub1\"," + "LANGUAGE=\"es\",NAME=\"Eng\"\n") + "#EXT-X-STREAM-INF:BANDWIDTH=1280000,") + "CODECS=\"mp4a.40.2,avc1.66.30\",RESOLUTION=304x128\n") + "http://example.com/low.m3u8\n");

    private static final String PLAYLIST_WITH_AUDIO_MEDIA_TAG = "#EXTM3U\n" + ((((((((((("#EXT-X-STREAM-INF:BANDWIDTH=2227464,CODECS=\"avc1.640020,mp4a.40.2\",AUDIO=\"aud1\"\n" + "uri1.m3u8\n") + "#EXT-X-STREAM-INF:BANDWIDTH=8178040,CODECS=\"avc1.64002a,mp4a.40.2\",AUDIO=\"aud1\"\n") + "uri2.m3u8\n") + "#EXT-X-STREAM-INF:BANDWIDTH=2448841,CODECS=\"avc1.640020,ac-3\",AUDIO=\"aud2\"\n") + "uri1.m3u8\n") + "#EXT-X-STREAM-INF:BANDWIDTH=8399417,CODECS=\"avc1.64002a,ac-3\",AUDIO=\"aud2\"\n") + "uri2.m3u8\n") + "#EXT-X-MEDIA:TYPE=AUDIO,GROUP-ID=\"aud1\",LANGUAGE=\"en\",NAME=\"English\",") + "AUTOSELECT=YES,DEFAULT=YES,CHANNELS=\"2\",URI=\"a1/prog_index.m3u8\"\n") + "#EXT-X-MEDIA:TYPE=AUDIO,GROUP-ID=\"aud2\",LANGUAGE=\"en\",NAME=\"English\",") + "AUTOSELECT=YES,DEFAULT=YES,CHANNELS=\"6\",URI=\"a2/prog_index.m3u8\"\n");

    private static final String PLAYLIST_WITH_INDEPENDENT_SEGMENTS = " #EXTM3U\n" + (((((((("\n" + "#EXT-X-INDEPENDENT-SEGMENTS\n") + "\n") + "#EXT-X-STREAM-INF:BANDWIDTH=1280000,") + "CODECS=\"mp4a.40.2,avc1.66.30\",RESOLUTION=304x128\n") + "http://example.com/low.m3u8\n") + "\n") + "#EXT-X-STREAM-INF:BANDWIDTH=1280000,CODECS=\"mp4a.40.2 , avc1.66.30 \"\n") + "http://example.com/spaces_in_codecs.m3u8\n");

    private static final String PLAYLIST_WITH_VARIABLE_SUBSTITUTION = " #EXTM3U \n" + ((((("\n" + "#EXT-X-DEFINE:NAME=\"codecs\",VALUE=\"mp4a.40.5\"\n") + "#EXT-X-DEFINE:NAME=\"tricky\",VALUE=\"This/{$nested}/reference/shouldnt/work\"\n") + "#EXT-X-DEFINE:NAME=\"nested\",VALUE=\"This should not be inserted\"\n") + "#EXT-X-STREAM-INF:BANDWIDTH=65000,CODECS=\"{$codecs}\"\n") + "http://example.com/{$tricky}\n");

    private static final String PLAYLIST_WITH_MULTIPLE_MUXED_MEDIA_TAGS = "#EXTM3U\n" + ((((((("#EXT-X-VERSION:3\n" + "#EXT-X-MEDIA:TYPE=AUDIO,GROUP-ID=\"a\",NAME=\"audio_0\",DEFAULT=YES,URI=\"0/0.m3u8\"\n") + "#EXT-X-MEDIA:TYPE=AUDIO,GROUP-ID=\"b\",NAME=\"audio_0\",DEFAULT=YES,URI=\"1/1.m3u8\"\n") + "#EXT-X-STREAM-INF:BANDWIDTH=140800,CODECS=\"mp4a.40.2\",AUDIO=\"a\"\n") + "0/0.m3u8\n") + "\n") + "#EXT-X-STREAM-INF:BANDWIDTH=281600,CODECS=\"mp4a.40.2\",AUDIO=\"b\"\n") + "1/1.m3u8\n");

    @Test
    public void testParseMasterPlaylist() throws IOException {
        HlsMasterPlaylist masterPlaylist = HlsMasterPlaylistParserTest.parseMasterPlaylist(HlsMasterPlaylistParserTest.PLAYLIST_URI, HlsMasterPlaylistParserTest.PLAYLIST_SIMPLE);
        List<HlsMasterPlaylist.HlsUrl> variants = masterPlaylist.variants;
        assertThat(variants).hasSize(5);
        assertThat(masterPlaylist.muxedCaptionFormats).isNull();
        assertThat(variants.get(0).format.bitrate).isEqualTo(1280000);
        assertThat(variants.get(0).format.codecs).isEqualTo("mp4a.40.2,avc1.66.30");
        assertThat(variants.get(0).format.width).isEqualTo(304);
        assertThat(variants.get(0).format.height).isEqualTo(128);
        assertThat(variants.get(0).url).isEqualTo("http://example.com/low.m3u8");
        assertThat(variants.get(1).format.bitrate).isEqualTo(1280000);
        assertThat(variants.get(1).format.codecs).isEqualTo("mp4a.40.2 , avc1.66.30 ");
        assertThat(variants.get(1).url).isEqualTo("http://example.com/spaces_in_codecs.m3u8");
        assertThat(variants.get(2).format.bitrate).isEqualTo(2560000);
        assertThat(variants.get(2).format.codecs).isNull();
        assertThat(variants.get(2).format.width).isEqualTo(384);
        assertThat(variants.get(2).format.height).isEqualTo(160);
        assertThat(variants.get(2).format.frameRate).isEqualTo(25.0F);
        assertThat(variants.get(2).url).isEqualTo("http://example.com/mid.m3u8");
        assertThat(variants.get(3).format.bitrate).isEqualTo(7680000);
        assertThat(variants.get(3).format.codecs).isNull();
        assertThat(variants.get(3).format.width).isEqualTo(NO_VALUE);
        assertThat(variants.get(3).format.height).isEqualTo(NO_VALUE);
        assertThat(variants.get(3).format.frameRate).isEqualTo(29.997F);
        assertThat(variants.get(3).url).isEqualTo("http://example.com/hi.m3u8");
        assertThat(variants.get(4).format.bitrate).isEqualTo(65000);
        assertThat(variants.get(4).format.codecs).isEqualTo("mp4a.40.5");
        assertThat(variants.get(4).format.width).isEqualTo(NO_VALUE);
        assertThat(variants.get(4).format.height).isEqualTo(NO_VALUE);
        assertThat(variants.get(4).format.frameRate).isEqualTo(((float) (NO_VALUE)));
        assertThat(variants.get(4).url).isEqualTo("http://example.com/audio-only.m3u8");
    }

    @Test
    public void testMasterPlaylistWithBandwdithAverage() throws IOException {
        HlsMasterPlaylist masterPlaylist = HlsMasterPlaylistParserTest.parseMasterPlaylist(HlsMasterPlaylistParserTest.PLAYLIST_URI, HlsMasterPlaylistParserTest.PLAYLIST_WITH_AVG_BANDWIDTH);
        List<HlsMasterPlaylist.HlsUrl> variants = masterPlaylist.variants;
        assertThat(variants.get(0).format.bitrate).isEqualTo(1280000);
        assertThat(variants.get(1).format.bitrate).isEqualTo(1270000);
    }

    @Test
    public void testPlaylistWithInvalidHeader() throws IOException {
        try {
            HlsMasterPlaylistParserTest.parseMasterPlaylist(HlsMasterPlaylistParserTest.PLAYLIST_URI, HlsMasterPlaylistParserTest.PLAYLIST_WITH_INVALID_HEADER);
            Assert.fail("Expected exception not thrown.");
        } catch (ParserException e) {
            // Expected due to invalid header.
        }
    }

    @Test
    public void testPlaylistWithClosedCaption() throws IOException {
        HlsMasterPlaylist playlist = HlsMasterPlaylistParserTest.parseMasterPlaylist(HlsMasterPlaylistParserTest.PLAYLIST_URI, HlsMasterPlaylistParserTest.PLAYLIST_WITH_CC);
        assertThat(playlist.muxedCaptionFormats).hasSize(1);
        Format closedCaptionFormat = playlist.muxedCaptionFormats.get(0);
        assertThat(closedCaptionFormat.sampleMimeType).isEqualTo(APPLICATION_CEA708);
        assertThat(closedCaptionFormat.accessibilityChannel).isEqualTo(4);
        assertThat(closedCaptionFormat.language).isEqualTo("es");
    }

    @Test
    public void testPlaylistWithChannelsAttribute() throws IOException {
        HlsMasterPlaylist playlist = HlsMasterPlaylistParserTest.parseMasterPlaylist(HlsMasterPlaylistParserTest.PLAYLIST_URI, HlsMasterPlaylistParserTest.PLAYLIST_WITH_CHANNELS_ATTRIBUTE);
        List<HlsMasterPlaylist.HlsUrl> audios = playlist.audios;
        assertThat(audios).hasSize(3);
        assertThat(audios.get(0).format.channelCount).isEqualTo(6);
        assertThat(audios.get(1).format.channelCount).isEqualTo(2);
        assertThat(audios.get(2).format.channelCount).isEqualTo(NO_VALUE);
    }

    @Test
    public void testPlaylistWithoutClosedCaptions() throws IOException {
        HlsMasterPlaylist playlist = HlsMasterPlaylistParserTest.parseMasterPlaylist(HlsMasterPlaylistParserTest.PLAYLIST_URI, HlsMasterPlaylistParserTest.PLAYLIST_WITHOUT_CC);
        assertThat(playlist.muxedCaptionFormats).isEmpty();
    }

    @Test
    public void testCodecPropagation() throws IOException {
        HlsMasterPlaylist playlist = HlsMasterPlaylistParserTest.parseMasterPlaylist(HlsMasterPlaylistParserTest.PLAYLIST_URI, HlsMasterPlaylistParserTest.PLAYLIST_WITH_AUDIO_MEDIA_TAG);
        Format firstAudioFormat = playlist.audios.get(0).format;
        assertThat(firstAudioFormat.codecs).isEqualTo("mp4a.40.2");
        assertThat(firstAudioFormat.sampleMimeType).isEqualTo(AUDIO_AAC);
        Format secondAudioFormat = playlist.audios.get(1).format;
        assertThat(secondAudioFormat.codecs).isEqualTo("ac-3");
        assertThat(secondAudioFormat.sampleMimeType).isEqualTo(AUDIO_AC3);
    }

    @Test
    public void testAudioIdPropagation() throws IOException {
        HlsMasterPlaylist playlist = HlsMasterPlaylistParserTest.parseMasterPlaylist(HlsMasterPlaylistParserTest.PLAYLIST_URI, HlsMasterPlaylistParserTest.PLAYLIST_WITH_AUDIO_MEDIA_TAG);
        Format firstAudioFormat = playlist.audios.get(0).format;
        assertThat(firstAudioFormat.id).isEqualTo("aud1:English");
        Format secondAudioFormat = playlist.audios.get(1).format;
        assertThat(secondAudioFormat.id).isEqualTo("aud2:English");
    }

    @Test
    public void testCCIdPropagation() throws IOException {
        HlsMasterPlaylist playlist = HlsMasterPlaylistParserTest.parseMasterPlaylist(HlsMasterPlaylistParserTest.PLAYLIST_URI, HlsMasterPlaylistParserTest.PLAYLIST_WITH_CC);
        Format firstTextFormat = playlist.muxedCaptionFormats.get(0);
        assertThat(firstTextFormat.id).isEqualTo("cc1:Eng");
    }

    @Test
    public void testSubtitleIdPropagation() throws IOException {
        HlsMasterPlaylist playlist = HlsMasterPlaylistParserTest.parseMasterPlaylist(HlsMasterPlaylistParserTest.PLAYLIST_URI, HlsMasterPlaylistParserTest.PLAYLIST_WITH_SUBTITLES);
        Format firstTextFormat = playlist.subtitles.get(0).format;
        assertThat(firstTextFormat.id).isEqualTo("sub1:Eng");
    }

    @Test
    public void testIndependentSegments() throws IOException {
        HlsMasterPlaylist playlistWithIndependentSegments = HlsMasterPlaylistParserTest.parseMasterPlaylist(HlsMasterPlaylistParserTest.PLAYLIST_URI, HlsMasterPlaylistParserTest.PLAYLIST_WITH_INDEPENDENT_SEGMENTS);
        assertThat(playlistWithIndependentSegments.hasIndependentSegments).isTrue();
        HlsMasterPlaylist playlistWithoutIndependentSegments = HlsMasterPlaylistParserTest.parseMasterPlaylist(HlsMasterPlaylistParserTest.PLAYLIST_URI, HlsMasterPlaylistParserTest.PLAYLIST_SIMPLE);
        assertThat(playlistWithoutIndependentSegments.hasIndependentSegments).isFalse();
    }

    @Test
    public void testVariableSubstitution() throws IOException {
        HlsMasterPlaylist playlistWithSubstitutions = HlsMasterPlaylistParserTest.parseMasterPlaylist(HlsMasterPlaylistParserTest.PLAYLIST_URI, HlsMasterPlaylistParserTest.PLAYLIST_WITH_VARIABLE_SUBSTITUTION);
        HlsMasterPlaylist.HlsUrl variant = playlistWithSubstitutions.variants.get(0);
        assertThat(variant.format.codecs).isEqualTo("mp4a.40.5");
        assertThat(variant.url).isEqualTo("http://example.com/This/{$nested}/reference/shouldnt/work");
    }

    @Test
    public void testMultipleMuxedMediaTags() throws IOException {
        HlsMasterPlaylist playlistWithMultipleMuxedMediaTags = HlsMasterPlaylistParserTest.parseMasterPlaylist(HlsMasterPlaylistParserTest.PLAYLIST_URI, HlsMasterPlaylistParserTest.PLAYLIST_WITH_MULTIPLE_MUXED_MEDIA_TAGS);
        assertThat(playlistWithMultipleMuxedMediaTags.variants).hasSize(2);
        assertThat(playlistWithMultipleMuxedMediaTags.audios).isEmpty();
    }
}

