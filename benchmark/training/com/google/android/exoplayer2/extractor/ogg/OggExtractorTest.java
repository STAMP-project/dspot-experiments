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
package com.google.android.exoplayer2.extractor.ogg;


import com.google.android.exoplayer2.testutil.ExtractorAsserts;
import com.google.android.exoplayer2.testutil.ExtractorAsserts.ExtractorFactory;
import com.google.android.exoplayer2.testutil.OggTestData;
import com.google.android.exoplayer2.testutil.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit test for {@link OggExtractor}.
 */
@RunWith(RobolectricTestRunner.class)
public final class OggExtractorTest {
    private static final ExtractorFactory OGG_EXTRACTOR_FACTORY = OggExtractor::new;

    @Test
    public void testOpus() throws Exception {
        ExtractorAsserts.assertBehavior(OggExtractorTest.OGG_EXTRACTOR_FACTORY, "ogg/bear.opus");
    }

    @Test
    public void testFlac() throws Exception {
        ExtractorAsserts.assertBehavior(OggExtractorTest.OGG_EXTRACTOR_FACTORY, "ogg/bear_flac.ogg");
    }

    @Test
    public void testFlacNoSeektable() throws Exception {
        ExtractorAsserts.assertBehavior(OggExtractorTest.OGG_EXTRACTOR_FACTORY, "ogg/bear_flac_noseektable.ogg");
    }

    @Test
    public void testVorbis() throws Exception {
        ExtractorAsserts.assertBehavior(OggExtractorTest.OGG_EXTRACTOR_FACTORY, "ogg/bear_vorbis.ogg");
    }

    @Test
    public void testSniffVorbis() throws Exception {
        byte[] data = // Laces
        TestUtil.joinByteArrays(OggTestData.buildOggHeader(2, 0, 1000, 1), TestUtil.createByteArray(7), new byte[]{ 1, 'v', 'o', 'r', 'b', 'i', 's' });
        assertThat(sniff(data)).isTrue();
    }

    @Test
    public void testSniffFlac() throws Exception {
        byte[] data = // Laces
        TestUtil.joinByteArrays(OggTestData.buildOggHeader(2, 0, 1000, 1), TestUtil.createByteArray(5), new byte[]{ 127, 'F', 'L', 'A', 'C' });
        assertThat(sniff(data)).isTrue();
    }

    @Test
    public void testSniffFailsOpusFile() throws Exception {
        byte[] data = TestUtil.joinByteArrays(OggTestData.buildOggHeader(2, 0, 1000, 0), new byte[]{ 'O', 'p', 'u', 's' });
        assertThat(sniff(data)).isFalse();
    }

    @Test
    public void testSniffFailsInvalidOggHeader() throws Exception {
        byte[] data = OggTestData.buildOggHeader(0, 0, 1000, 0);
        assertThat(sniff(data)).isFalse();
    }

    @Test
    public void testSniffInvalidHeader() throws Exception {
        byte[] data = // Laces
        TestUtil.joinByteArrays(OggTestData.buildOggHeader(2, 0, 1000, 1), TestUtil.createByteArray(7), new byte[]{ 127, 'X', 'o', 'r', 'b', 'i', 's' });
        assertThat(sniff(data)).isFalse();
    }

    @Test
    public void testSniffFailsEOF() throws Exception {
        byte[] data = OggTestData.buildOggHeader(2, 0, 1000, 0);
        assertThat(sniff(data)).isFalse();
    }
}

