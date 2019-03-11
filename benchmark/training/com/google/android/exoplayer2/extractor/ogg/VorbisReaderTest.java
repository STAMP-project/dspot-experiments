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


import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ogg.VorbisReader.VorbisSetup;
import com.google.android.exoplayer2.testutil.FakeExtractorInput;
import com.google.android.exoplayer2.testutil.OggTestData;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit test for {@link VorbisReader}.
 */
@RunWith(RobolectricTestRunner.class)
public final class VorbisReaderTest {
    @Test
    public void testReadBits() throws Exception {
        assertThat(VorbisReader.readBits(((byte) (0)), 2, 2)).isEqualTo(0);
        assertThat(VorbisReader.readBits(((byte) (2)), 1, 1)).isEqualTo(1);
        assertThat(VorbisReader.readBits(((byte) (240)), 4, 4)).isEqualTo(15);
        assertThat(VorbisReader.readBits(((byte) (128)), 1, 7)).isEqualTo(1);
    }

    @Test
    public void testAppendNumberOfSamples() throws Exception {
        ParsableByteArray buffer = new ParsableByteArray(4);
        buffer.setLimit(0);
        VorbisReader.appendNumberOfSamples(buffer, 19088743);
        assertThat(buffer.limit()).isEqualTo(4);
        assertThat(buffer.data[0]).isEqualTo(103);
        assertThat(buffer.data[1]).isEqualTo(69);
        assertThat(buffer.data[2]).isEqualTo(35);
        assertThat(buffer.data[3]).isEqualTo(1);
    }

    @Test
    public void testReadSetupHeadersWithIOExceptions() throws IOException, InterruptedException {
        byte[] data = OggTestData.getVorbisHeaderPages();
        ExtractorInput input = new FakeExtractorInput.Builder().setData(data).setSimulateIOErrors(true).setSimulateUnknownLength(true).setSimulatePartialReads(true).build();
        VorbisReader reader = new VorbisReader();
        VorbisReader.VorbisSetup vorbisSetup = VorbisReaderTest.readSetupHeaders(reader, input);
        assertThat(vorbisSetup.idHeader).isNotNull();
        assertThat(vorbisSetup.commentHeader).isNotNull();
        assertThat(vorbisSetup.setupHeaderData).isNotNull();
        assertThat(vorbisSetup.modes).isNotNull();
        assertThat(vorbisSetup.commentHeader.length).isEqualTo(45);
        assertThat(vorbisSetup.idHeader.data).hasLength(30);
        assertThat(vorbisSetup.setupHeaderData).hasLength(3597);
        assertThat(vorbisSetup.idHeader.bitrateMax).isEqualTo((-1));
        assertThat(vorbisSetup.idHeader.bitrateMin).isEqualTo((-1));
        assertThat(vorbisSetup.idHeader.bitrateNominal).isEqualTo(66666);
        assertThat(vorbisSetup.idHeader.blockSize0).isEqualTo(512);
        assertThat(vorbisSetup.idHeader.blockSize1).isEqualTo(1024);
        assertThat(vorbisSetup.idHeader.channels).isEqualTo(2);
        assertThat(vorbisSetup.idHeader.framingFlag).isTrue();
        assertThat(vorbisSetup.idHeader.sampleRate).isEqualTo(22050);
        assertThat(vorbisSetup.idHeader.version).isEqualTo(0);
        assertThat(vorbisSetup.commentHeader.vendor).isEqualTo("Xiph.Org libVorbis I 20030909");
        assertThat(vorbisSetup.iLogModes).isEqualTo(1);
        assertThat(vorbisSetup.setupHeaderData[((vorbisSetup.setupHeaderData.length) - 1)]).isEqualTo(data[((data.length) - 1)]);
        assertThat(vorbisSetup.modes[0].blockFlag).isFalse();
        assertThat(vorbisSetup.modes[1].blockFlag).isTrue();
    }
}

