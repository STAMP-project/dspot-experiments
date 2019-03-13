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
package com.google.android.exoplayer2.text.ssa;


import RuntimeEnvironment.application;
import com.google.android.exoplayer2.testutil.TestUtil;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit test for {@link SsaDecoder}.
 */
@RunWith(RobolectricTestRunner.class)
public final class SsaDecoderTest {
    private static final String EMPTY = "ssa/empty";

    private static final String TYPICAL = "ssa/typical";

    private static final String TYPICAL_HEADER_ONLY = "ssa/typical_header";

    private static final String TYPICAL_DIALOGUE_ONLY = "ssa/typical_dialogue";

    private static final String TYPICAL_FORMAT_ONLY = "ssa/typical_format";

    private static final String INVALID_TIMECODES = "ssa/invalid_timecodes";

    private static final String NO_END_TIMECODES = "ssa/no_end_timecodes";

    @Test
    public void testDecodeEmpty() throws IOException {
        SsaDecoder decoder = new SsaDecoder();
        byte[] bytes = TestUtil.getByteArray(application, SsaDecoderTest.EMPTY);
        SsaSubtitle subtitle = decoder.decode(bytes, bytes.length, false);
        assertThat(subtitle.getEventTimeCount()).isEqualTo(0);
        assertThat(subtitle.getCues(0).isEmpty()).isTrue();
    }

    @Test
    public void testDecodeTypical() throws IOException {
        SsaDecoder decoder = new SsaDecoder();
        byte[] bytes = TestUtil.getByteArray(application, SsaDecoderTest.TYPICAL);
        SsaSubtitle subtitle = decoder.decode(bytes, bytes.length, false);
        assertThat(subtitle.getEventTimeCount()).isEqualTo(6);
        SsaDecoderTest.assertTypicalCue1(subtitle, 0);
        SsaDecoderTest.assertTypicalCue2(subtitle, 2);
        SsaDecoderTest.assertTypicalCue3(subtitle, 4);
    }

    @Test
    public void testDecodeTypicalWithInitializationData() throws IOException {
        byte[] headerBytes = TestUtil.getByteArray(application, SsaDecoderTest.TYPICAL_HEADER_ONLY);
        byte[] formatBytes = TestUtil.getByteArray(application, SsaDecoderTest.TYPICAL_FORMAT_ONLY);
        ArrayList<byte[]> initializationData = new ArrayList<>();
        initializationData.add(formatBytes);
        initializationData.add(headerBytes);
        SsaDecoder decoder = new SsaDecoder(initializationData);
        byte[] bytes = TestUtil.getByteArray(application, SsaDecoderTest.TYPICAL_DIALOGUE_ONLY);
        SsaSubtitle subtitle = decoder.decode(bytes, bytes.length, false);
        assertThat(subtitle.getEventTimeCount()).isEqualTo(6);
        SsaDecoderTest.assertTypicalCue1(subtitle, 0);
        SsaDecoderTest.assertTypicalCue2(subtitle, 2);
        SsaDecoderTest.assertTypicalCue3(subtitle, 4);
    }

    @Test
    public void testDecodeInvalidTimecodes() throws IOException {
        // Parsing should succeed, parsing the third cue only.
        SsaDecoder decoder = new SsaDecoder();
        byte[] bytes = TestUtil.getByteArray(application, SsaDecoderTest.INVALID_TIMECODES);
        SsaSubtitle subtitle = decoder.decode(bytes, bytes.length, false);
        assertThat(subtitle.getEventTimeCount()).isEqualTo(2);
        SsaDecoderTest.assertTypicalCue3(subtitle, 0);
    }

    @Test
    public void testDecodeNoEndTimecodes() throws IOException {
        SsaDecoder decoder = new SsaDecoder();
        byte[] bytes = TestUtil.getByteArray(application, SsaDecoderTest.NO_END_TIMECODES);
        SsaSubtitle subtitle = decoder.decode(bytes, bytes.length, false);
        assertThat(subtitle.getEventTimeCount()).isEqualTo(3);
        assertThat(subtitle.getEventTime(0)).isEqualTo(0);
        assertThat(subtitle.getCues(subtitle.getEventTime(0)).get(0).text.toString()).isEqualTo("This is the first subtitle.");
        assertThat(subtitle.getEventTime(1)).isEqualTo(2340000);
        assertThat(subtitle.getCues(subtitle.getEventTime(1)).get(0).text.toString()).isEqualTo("This is the second subtitle \nwith a newline \nand another.");
        assertThat(subtitle.getEventTime(2)).isEqualTo(4560000);
        assertThat(subtitle.getCues(subtitle.getEventTime(2)).get(0).text.toString()).isEqualTo("This is the third subtitle, with a comma.");
    }
}

