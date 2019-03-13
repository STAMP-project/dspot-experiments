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
package com.google.android.exoplayer2.text.tx3g;


import C.SERIF_NAME;
import Color.GREEN;
import Color.RED;
import RuntimeEnvironment.application;
import Typeface.BOLD;
import Typeface.BOLD_ITALIC;
import Typeface.ITALIC;
import android.text.SpannedString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import com.google.android.exoplayer2.testutil.TestUtil;
import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.exoplayer2.text.SubtitleDecoderException;
import java.io.IOException;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit test for {@link Tx3gDecoder}.
 */
@RunWith(RobolectricTestRunner.class)
public final class Tx3gDecoderTest {
    private static final String NO_SUBTITLE = "tx3g/no_subtitle";

    private static final String SAMPLE_JUST_TEXT = "tx3g/sample_just_text";

    private static final String SAMPLE_WITH_STYL = "tx3g/sample_with_styl";

    private static final String SAMPLE_WITH_STYL_ALL_DEFAULTS = "tx3g/sample_with_styl_all_defaults";

    private static final String SAMPLE_UTF16_BE_NO_STYL = "tx3g/sample_utf16_be_no_styl";

    private static final String SAMPLE_UTF16_LE_NO_STYL = "tx3g/sample_utf16_le_no_styl";

    private static final String SAMPLE_WITH_MULTIPLE_STYL = "tx3g/sample_with_multiple_styl";

    private static final String SAMPLE_WITH_OTHER_EXTENSION = "tx3g/sample_with_other_extension";

    private static final String SAMPLE_WITH_TBOX = "tx3g/sample_with_tbox";

    private static final String INITIALIZATION = "tx3g/initialization";

    private static final String INITIALIZATION_ALL_DEFAULTS = "tx3g/initialization_all_defaults";

    @Test
    public void testDecodeNoSubtitle() throws SubtitleDecoderException, IOException {
        Tx3gDecoder decoder = new Tx3gDecoder(Collections.emptyList());
        byte[] bytes = TestUtil.getByteArray(application, Tx3gDecoderTest.NO_SUBTITLE);
        Subtitle subtitle = decoder.decode(bytes, bytes.length, false);
        assertThat(subtitle.getCues(0)).isEmpty();
    }

    @Test
    public void testDecodeJustText() throws SubtitleDecoderException, IOException {
        Tx3gDecoder decoder = new Tx3gDecoder(Collections.emptyList());
        byte[] bytes = TestUtil.getByteArray(application, Tx3gDecoderTest.SAMPLE_JUST_TEXT);
        Subtitle subtitle = decoder.decode(bytes, bytes.length, false);
        SpannedString text = new SpannedString(subtitle.getCues(0).get(0).text);
        assertThat(text.toString()).isEqualTo("CC Test");
        assertThat(text.getSpans(0, text.length(), Object.class)).hasLength(0);
        Tx3gDecoderTest.assertFractionalLinePosition(subtitle.getCues(0).get(0), 0.85F);
    }

    @Test
    public void testDecodeWithStyl() throws SubtitleDecoderException, IOException {
        Tx3gDecoder decoder = new Tx3gDecoder(Collections.emptyList());
        byte[] bytes = TestUtil.getByteArray(application, Tx3gDecoderTest.SAMPLE_WITH_STYL);
        Subtitle subtitle = decoder.decode(bytes, bytes.length, false);
        SpannedString text = new SpannedString(subtitle.getCues(0).get(0).text);
        assertThat(text.toString()).isEqualTo("CC Test");
        assertThat(text.getSpans(0, text.length(), Object.class)).hasLength(3);
        StyleSpan styleSpan = Tx3gDecoderTest.findSpan(text, 0, 6, StyleSpan.class);
        assertThat(styleSpan.getStyle()).isEqualTo(BOLD_ITALIC);
        Tx3gDecoderTest.findSpan(text, 0, 6, UnderlineSpan.class);
        ForegroundColorSpan colorSpan = Tx3gDecoderTest.findSpan(text, 0, 6, ForegroundColorSpan.class);
        assertThat(colorSpan.getForegroundColor()).isEqualTo(GREEN);
        Tx3gDecoderTest.assertFractionalLinePosition(subtitle.getCues(0).get(0), 0.85F);
    }

    @Test
    public void testDecodeWithStylAllDefaults() throws SubtitleDecoderException, IOException {
        Tx3gDecoder decoder = new Tx3gDecoder(Collections.emptyList());
        byte[] bytes = TestUtil.getByteArray(application, Tx3gDecoderTest.SAMPLE_WITH_STYL_ALL_DEFAULTS);
        Subtitle subtitle = decoder.decode(bytes, bytes.length, false);
        SpannedString text = new SpannedString(subtitle.getCues(0).get(0).text);
        assertThat(text.toString()).isEqualTo("CC Test");
        assertThat(text.getSpans(0, text.length(), Object.class)).hasLength(0);
        Tx3gDecoderTest.assertFractionalLinePosition(subtitle.getCues(0).get(0), 0.85F);
    }

    @Test
    public void testDecodeUtf16BeNoStyl() throws SubtitleDecoderException, IOException {
        Tx3gDecoder decoder = new Tx3gDecoder(Collections.emptyList());
        byte[] bytes = TestUtil.getByteArray(application, Tx3gDecoderTest.SAMPLE_UTF16_BE_NO_STYL);
        Subtitle subtitle = decoder.decode(bytes, bytes.length, false);
        SpannedString text = new SpannedString(subtitle.getCues(0).get(0).text);
        assertThat(text.toString()).isEqualTo("??");
        assertThat(text.getSpans(0, text.length(), Object.class)).hasLength(0);
        Tx3gDecoderTest.assertFractionalLinePosition(subtitle.getCues(0).get(0), 0.85F);
    }

    @Test
    public void testDecodeUtf16LeNoStyl() throws SubtitleDecoderException, IOException {
        Tx3gDecoder decoder = new Tx3gDecoder(Collections.emptyList());
        byte[] bytes = TestUtil.getByteArray(application, Tx3gDecoderTest.SAMPLE_UTF16_LE_NO_STYL);
        Subtitle subtitle = decoder.decode(bytes, bytes.length, false);
        SpannedString text = new SpannedString(subtitle.getCues(0).get(0).text);
        assertThat(text.toString()).isEqualTo("??");
        assertThat(text.getSpans(0, text.length(), Object.class)).hasLength(0);
        Tx3gDecoderTest.assertFractionalLinePosition(subtitle.getCues(0).get(0), 0.85F);
    }

    @Test
    public void testDecodeWithMultipleStyl() throws SubtitleDecoderException, IOException {
        Tx3gDecoder decoder = new Tx3gDecoder(Collections.emptyList());
        byte[] bytes = TestUtil.getByteArray(application, Tx3gDecoderTest.SAMPLE_WITH_MULTIPLE_STYL);
        Subtitle subtitle = decoder.decode(bytes, bytes.length, false);
        SpannedString text = new SpannedString(subtitle.getCues(0).get(0).text);
        assertThat(text.toString()).isEqualTo("Line 2\nLine 3");
        assertThat(text.getSpans(0, text.length(), Object.class)).hasLength(4);
        StyleSpan styleSpan = Tx3gDecoderTest.findSpan(text, 0, 5, StyleSpan.class);
        assertThat(styleSpan.getStyle()).isEqualTo(ITALIC);
        Tx3gDecoderTest.findSpan(text, 7, 12, UnderlineSpan.class);
        ForegroundColorSpan colorSpan = Tx3gDecoderTest.findSpan(text, 0, 5, ForegroundColorSpan.class);
        assertThat(colorSpan.getForegroundColor()).isEqualTo(GREEN);
        colorSpan = Tx3gDecoderTest.findSpan(text, 7, 12, ForegroundColorSpan.class);
        assertThat(colorSpan.getForegroundColor()).isEqualTo(GREEN);
        Tx3gDecoderTest.assertFractionalLinePosition(subtitle.getCues(0).get(0), 0.85F);
    }

    @Test
    public void testDecodeWithOtherExtension() throws SubtitleDecoderException, IOException {
        Tx3gDecoder decoder = new Tx3gDecoder(Collections.emptyList());
        byte[] bytes = TestUtil.getByteArray(application, Tx3gDecoderTest.SAMPLE_WITH_OTHER_EXTENSION);
        Subtitle subtitle = decoder.decode(bytes, bytes.length, false);
        SpannedString text = new SpannedString(subtitle.getCues(0).get(0).text);
        assertThat(text.toString()).isEqualTo("CC Test");
        assertThat(text.getSpans(0, text.length(), Object.class)).hasLength(2);
        StyleSpan styleSpan = Tx3gDecoderTest.findSpan(text, 0, 6, StyleSpan.class);
        assertThat(styleSpan.getStyle()).isEqualTo(BOLD);
        ForegroundColorSpan colorSpan = Tx3gDecoderTest.findSpan(text, 0, 6, ForegroundColorSpan.class);
        assertThat(colorSpan.getForegroundColor()).isEqualTo(GREEN);
        Tx3gDecoderTest.assertFractionalLinePosition(subtitle.getCues(0).get(0), 0.85F);
    }

    @Test
    public void testInitializationDecodeWithStyl() throws SubtitleDecoderException, IOException {
        byte[] initBytes = TestUtil.getByteArray(application, Tx3gDecoderTest.INITIALIZATION);
        Tx3gDecoder decoder = new Tx3gDecoder(Collections.singletonList(initBytes));
        byte[] bytes = TestUtil.getByteArray(application, Tx3gDecoderTest.SAMPLE_WITH_STYL);
        Subtitle subtitle = decoder.decode(bytes, bytes.length, false);
        SpannedString text = new SpannedString(subtitle.getCues(0).get(0).text);
        assertThat(text.toString()).isEqualTo("CC Test");
        assertThat(text.getSpans(0, text.length(), Object.class)).hasLength(5);
        StyleSpan styleSpan = Tx3gDecoderTest.findSpan(text, 0, text.length(), StyleSpan.class);
        assertThat(styleSpan.getStyle()).isEqualTo(BOLD_ITALIC);
        Tx3gDecoderTest.findSpan(text, 0, text.length(), UnderlineSpan.class);
        TypefaceSpan typefaceSpan = Tx3gDecoderTest.findSpan(text, 0, text.length(), TypefaceSpan.class);
        assertThat(typefaceSpan.getFamily()).isEqualTo(SERIF_NAME);
        ForegroundColorSpan colorSpan = Tx3gDecoderTest.findSpan(text, 0, text.length(), ForegroundColorSpan.class);
        assertThat(colorSpan.getForegroundColor()).isEqualTo(RED);
        colorSpan = Tx3gDecoderTest.findSpan(text, 0, 6, ForegroundColorSpan.class);
        assertThat(colorSpan.getForegroundColor()).isEqualTo(GREEN);
        Tx3gDecoderTest.assertFractionalLinePosition(subtitle.getCues(0).get(0), 0.1F);
    }

    @Test
    public void testInitializationDecodeWithTbox() throws SubtitleDecoderException, IOException {
        byte[] initBytes = TestUtil.getByteArray(application, Tx3gDecoderTest.INITIALIZATION);
        Tx3gDecoder decoder = new Tx3gDecoder(Collections.singletonList(initBytes));
        byte[] bytes = TestUtil.getByteArray(application, Tx3gDecoderTest.SAMPLE_WITH_TBOX);
        Subtitle subtitle = decoder.decode(bytes, bytes.length, false);
        SpannedString text = new SpannedString(subtitle.getCues(0).get(0).text);
        assertThat(text.toString()).isEqualTo("CC Test");
        assertThat(text.getSpans(0, text.length(), Object.class)).hasLength(4);
        StyleSpan styleSpan = Tx3gDecoderTest.findSpan(text, 0, text.length(), StyleSpan.class);
        assertThat(styleSpan.getStyle()).isEqualTo(BOLD_ITALIC);
        Tx3gDecoderTest.findSpan(text, 0, text.length(), UnderlineSpan.class);
        TypefaceSpan typefaceSpan = Tx3gDecoderTest.findSpan(text, 0, text.length(), TypefaceSpan.class);
        assertThat(typefaceSpan.getFamily()).isEqualTo(SERIF_NAME);
        ForegroundColorSpan colorSpan = Tx3gDecoderTest.findSpan(text, 0, text.length(), ForegroundColorSpan.class);
        assertThat(colorSpan.getForegroundColor()).isEqualTo(RED);
        Tx3gDecoderTest.assertFractionalLinePosition(subtitle.getCues(0).get(0), 0.1875F);
    }

    @Test
    public void testInitializationAllDefaultsDecodeWithStyl() throws SubtitleDecoderException, IOException {
        byte[] initBytes = TestUtil.getByteArray(application, Tx3gDecoderTest.INITIALIZATION_ALL_DEFAULTS);
        Tx3gDecoder decoder = new Tx3gDecoder(Collections.singletonList(initBytes));
        byte[] bytes = TestUtil.getByteArray(application, Tx3gDecoderTest.SAMPLE_WITH_STYL);
        Subtitle subtitle = decoder.decode(bytes, bytes.length, false);
        SpannedString text = new SpannedString(subtitle.getCues(0).get(0).text);
        assertThat(text.toString()).isEqualTo("CC Test");
        assertThat(text.getSpans(0, text.length(), Object.class)).hasLength(3);
        StyleSpan styleSpan = Tx3gDecoderTest.findSpan(text, 0, 6, StyleSpan.class);
        assertThat(styleSpan.getStyle()).isEqualTo(BOLD_ITALIC);
        Tx3gDecoderTest.findSpan(text, 0, 6, UnderlineSpan.class);
        ForegroundColorSpan colorSpan = Tx3gDecoderTest.findSpan(text, 0, 6, ForegroundColorSpan.class);
        assertThat(colorSpan.getForegroundColor()).isEqualTo(GREEN);
        Tx3gDecoderTest.assertFractionalLinePosition(subtitle.getCues(0).get(0), 0.85F);
    }
}

