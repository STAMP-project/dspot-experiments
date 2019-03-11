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
package com.google.android.exoplayer2.text.webvtt;


import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.exoplayer2.text.SubtitleDecoderException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit test for {@link Mp4WebvttDecoder}.
 */
@RunWith(RobolectricTestRunner.class)
public final class Mp4WebvttDecoderTest {
    private static final byte[] SINGLE_CUE_SAMPLE = new byte[]{ 0, 0, 0, 28// Size
    , 118, 116, 116, 99// "vttc" Box type. VTT Cue box begins:
    , 0, 0, 0, 20// Contained payload box's size
    , 112, 97, 121, 108// Contained payload box's type (payl), Cue Payload Box begins:
    , 72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100, 10// Hello World\n
     };

    private static final byte[] DOUBLE_CUE_SAMPLE = new byte[]{ 0, 0, 0, 27// Size
    , 118, 116, 116, 99// "vttc" Box type. First VTT Cue box begins:
    , 0, 0, 0, 19// First contained payload box's size
    , 112, 97, 121, 108// First contained payload box's type (payl), Cue Payload Box begins:
    , 72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100// Hello World
    , 0, 0, 0, 23// Size
    , 118, 116, 116, 99// "vttc" Box type. Second VTT Cue box begins:
    , 0, 0, 0, 15// Contained payload box's size
    , 112, 97, 121, 108// Contained payload box's type (payl), Payload begins:
    , 66, 121, 101, 32, 66, 121, 101// Bye Bye
     };

    private static final byte[] NO_CUE_SAMPLE = new byte[]{ 0, 0, 0, 27// Size
    , 116, 116, 116, 99// "tttc" Box type, which is not a Cue. Should be skipped:
    , 0, 0, 0, 19// Contained payload box's size
    , 112, 97, 121, 108// Contained payload box's type (payl), Cue Payload Box begins:
    , 72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100// Hello World
     };

    private static final byte[] INCOMPLETE_HEADER_SAMPLE = new byte[]{ 0, 0, 0, 35// Size
    , 118, 116, 116, 99// "vttc" Box type. VTT Cue box begins:
    , 0, 0, 0, 20// Contained payload box's size
    , 112, 97, 121, 108// Contained payload box's type (payl), Cue Payload Box begins:
    , 72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100, 10// Hello World\n
    , 0, 0, 0, 7// Size of an incomplete header, which belongs to the first vttc box.
    , 118, 116, 116 };

    // Positive tests.
    @Test
    public void testSingleCueSample() throws SubtitleDecoderException {
        Mp4WebvttDecoder decoder = new Mp4WebvttDecoder();
        Subtitle result = decoder.decode(Mp4WebvttDecoderTest.SINGLE_CUE_SAMPLE, Mp4WebvttDecoderTest.SINGLE_CUE_SAMPLE.length, false);
        Cue expectedCue = new Cue("Hello World");// Line feed must be trimmed by the decoder

        Mp4WebvttDecoderTest.assertMp4WebvttSubtitleEquals(result, expectedCue);
    }

    @Test
    public void testTwoCuesSample() throws SubtitleDecoderException {
        Mp4WebvttDecoder decoder = new Mp4WebvttDecoder();
        Subtitle result = decoder.decode(Mp4WebvttDecoderTest.DOUBLE_CUE_SAMPLE, Mp4WebvttDecoderTest.DOUBLE_CUE_SAMPLE.length, false);
        Cue firstExpectedCue = new Cue("Hello World");
        Cue secondExpectedCue = new Cue("Bye Bye");
        Mp4WebvttDecoderTest.assertMp4WebvttSubtitleEquals(result, firstExpectedCue, secondExpectedCue);
    }

    @Test
    public void testNoCueSample() throws SubtitleDecoderException {
        Mp4WebvttDecoder decoder = new Mp4WebvttDecoder();
        Subtitle result = decoder.decode(Mp4WebvttDecoderTest.NO_CUE_SAMPLE, Mp4WebvttDecoderTest.NO_CUE_SAMPLE.length, false);
        Mp4WebvttDecoderTest.assertMp4WebvttSubtitleEquals(result);
    }

    // Negative tests.
    @Test
    public void testSampleWithIncompleteHeader() {
        Mp4WebvttDecoder decoder = new Mp4WebvttDecoder();
        try {
            decoder.decode(Mp4WebvttDecoderTest.INCOMPLETE_HEADER_SAMPLE, Mp4WebvttDecoderTest.INCOMPLETE_HEADER_SAMPLE.length, false);
        } catch (SubtitleDecoderException e) {
            return;
        }
        Assert.fail();
    }
}

