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


import Alignment.ALIGN_CENTER;
import Alignment.ALIGN_NORMAL;
import Alignment.ALIGN_OPPOSITE;
import Cue.ANCHOR_TYPE_END;
import Cue.ANCHOR_TYPE_START;
import Cue.DIMEN_UNSET;
import Cue.LINE_TYPE_FRACTION;
import Cue.LINE_TYPE_NUMBER;
import Cue.TYPE_UNSET;
import RuntimeEnvironment.application;
import Typeface.BOLD;
import Typeface.ITALIC;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import com.google.android.exoplayer2.testutil.TestUtil;
import com.google.android.exoplayer2.text.SubtitleDecoderException;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit test for {@link WebvttDecoder}.
 */
@RunWith(RobolectricTestRunner.class)
public class WebvttDecoderTest {
    private static final String TYPICAL_FILE = "webvtt/typical";

    private static final String TYPICAL_WITH_BAD_TIMESTAMPS = "webvtt/typical_with_bad_timestamps";

    private static final String TYPICAL_WITH_IDS_FILE = "webvtt/typical_with_identifiers";

    private static final String TYPICAL_WITH_COMMENTS_FILE = "webvtt/typical_with_comments";

    private static final String WITH_POSITIONING_FILE = "webvtt/with_positioning";

    private static final String WITH_BAD_CUE_HEADER_FILE = "webvtt/with_bad_cue_header";

    private static final String WITH_TAGS_FILE = "webvtt/with_tags";

    private static final String WITH_CSS_STYLES = "webvtt/with_css_styles";

    private static final String WITH_CSS_COMPLEX_SELECTORS = "webvtt/with_css_complex_selectors";

    private static final String WITH_BOM = "webvtt/with_bom";

    private static final String EMPTY_FILE = "webvtt/empty";

    @Test
    public void testDecodeEmpty() throws IOException {
        WebvttDecoder decoder = new WebvttDecoder();
        byte[] bytes = TestUtil.getByteArray(application, WebvttDecoderTest.EMPTY_FILE);
        try {
            /* reset= */
            decoder.decode(bytes, bytes.length, false);
            Assert.fail();
        } catch (SubtitleDecoderException expected) {
            // Do nothing.
        }
    }

    @Test
    public void testDecodeTypical() throws SubtitleDecoderException, IOException {
        WebvttSubtitle subtitle = getSubtitleForTestAsset(WebvttDecoderTest.TYPICAL_FILE);
        // Test event count.
        assertThat(subtitle.getEventTimeCount()).isEqualTo(4);
        // Test cues.
        /* eventTimeIndex= */
        /* startTimeUs= */
        /* endTimeUs= */
        WebvttDecoderTest.assertCue(subtitle, 0, 0, 1234000, "This is the first subtitle.");
        /* eventTimeIndex= */
        /* startTimeUs= */
        /* endTimeUs= */
        WebvttDecoderTest.assertCue(subtitle, 2, 2345000, 3456000, "This is the second subtitle.");
    }

    @Test
    public void testDecodeWithBom() throws SubtitleDecoderException, IOException {
        WebvttSubtitle subtitle = getSubtitleForTestAsset(WebvttDecoderTest.WITH_BOM);
        // Test event count.
        assertThat(subtitle.getEventTimeCount()).isEqualTo(4);
        // Test cues.
        /* eventTimeIndex= */
        /* startTimeUs= */
        /* endTimeUs= */
        WebvttDecoderTest.assertCue(subtitle, 0, 0, 1234000, "This is the first subtitle.");
        /* eventTimeIndex= */
        /* startTimeUs= */
        /* endTimeUs= */
        WebvttDecoderTest.assertCue(subtitle, 2, 2345000, 3456000, "This is the second subtitle.");
    }

    @Test
    public void testDecodeTypicalWithBadTimestamps() throws SubtitleDecoderException, IOException {
        WebvttSubtitle subtitle = getSubtitleForTestAsset(WebvttDecoderTest.TYPICAL_WITH_BAD_TIMESTAMPS);
        // Test event count.
        assertThat(subtitle.getEventTimeCount()).isEqualTo(4);
        // Test cues.
        /* eventTimeIndex= */
        /* startTimeUs= */
        /* endTimeUs= */
        WebvttDecoderTest.assertCue(subtitle, 0, 0, 1234000, "This is the first subtitle.");
        /* eventTimeIndex= */
        /* startTimeUs= */
        /* endTimeUs= */
        WebvttDecoderTest.assertCue(subtitle, 2, 2345000, 3456000, "This is the second subtitle.");
    }

    @Test
    public void testDecodeTypicalWithIds() throws SubtitleDecoderException, IOException {
        WebvttSubtitle subtitle = getSubtitleForTestAsset(WebvttDecoderTest.TYPICAL_WITH_IDS_FILE);
        // Test event count.
        assertThat(subtitle.getEventTimeCount()).isEqualTo(4);
        // Test cues.
        /* eventTimeIndex= */
        /* startTimeUs= */
        /* endTimeUs= */
        WebvttDecoderTest.assertCue(subtitle, 0, 0, 1234000, "This is the first subtitle.");
        /* eventTimeIndex= */
        /* startTimeUs= */
        /* endTimeUs= */
        WebvttDecoderTest.assertCue(subtitle, 2, 2345000, 3456000, "This is the second subtitle.");
    }

    @Test
    public void testDecodeTypicalWithComments() throws SubtitleDecoderException, IOException {
        WebvttSubtitle subtitle = getSubtitleForTestAsset(WebvttDecoderTest.TYPICAL_WITH_COMMENTS_FILE);
        // test event count
        assertThat(subtitle.getEventTimeCount()).isEqualTo(4);
        // test cues
        /* eventTimeIndex= */
        /* startTimeUs= */
        /* endTimeUs= */
        WebvttDecoderTest.assertCue(subtitle, 0, 0, 1234000, "This is the first subtitle.");
        /* eventTimeIndex= */
        /* startTimeUs= */
        /* endTimeUs= */
        WebvttDecoderTest.assertCue(subtitle, 2, 2345000, 3456000, "This is the second subtitle.");
    }

    @Test
    public void testDecodeWithTags() throws SubtitleDecoderException, IOException {
        WebvttSubtitle subtitle = getSubtitleForTestAsset(WebvttDecoderTest.WITH_TAGS_FILE);
        // Test event count.
        assertThat(subtitle.getEventTimeCount()).isEqualTo(8);
        // Test cues.
        /* eventTimeIndex= */
        /* startTimeUs= */
        /* endTimeUs= */
        WebvttDecoderTest.assertCue(subtitle, 0, 0, 1234000, "This is the first subtitle.");
        /* eventTimeIndex= */
        /* startTimeUs= */
        /* endTimeUs= */
        WebvttDecoderTest.assertCue(subtitle, 2, 2345000, 3456000, "This is the second subtitle.");
        /* eventTimeIndex= */
        /* startTimeUs= */
        /* endTimeUs= */
        WebvttDecoderTest.assertCue(subtitle, 4, 4000000, 5000000, "This is the third subtitle.");
        /* eventTimeIndex= */
        /* startTimeUs= */
        /* endTimeUs= */
        WebvttDecoderTest.assertCue(subtitle, 6, 6000000, 7000000, "This is the <fourth> &subtitle.");
    }

    @Test
    public void testDecodeWithPositioning() throws SubtitleDecoderException, IOException {
        WebvttSubtitle subtitle = getSubtitleForTestAsset(WebvttDecoderTest.WITH_POSITIONING_FILE);
        // Test event count.
        assertThat(subtitle.getEventTimeCount()).isEqualTo(12);
        // Test cues.
        /* eventTimeIndex= */
        /* startTimeUs= */
        /* endTimeUs= */
        /* line= */
        /* lineType= */
        /* lineAnchor= */
        /* position= */
        /* positionAnchor= */
        /* size= */
        WebvttDecoderTest.assertCue(subtitle, 0, 0, 1234000, "This is the first subtitle.", ALIGN_NORMAL, DIMEN_UNSET, TYPE_UNSET, TYPE_UNSET, 0.1F, ANCHOR_TYPE_START, 0.35F);
        /* eventTimeIndex= */
        /* startTimeUs= */
        /* endTimeUs= */
        /* line= */
        /* lineType= */
        /* lineAnchor= */
        /* position= */
        /* positionAnchor= */
        /* size= */
        WebvttDecoderTest.assertCue(subtitle, 2, 2345000, 3456000, "This is the second subtitle.", ALIGN_OPPOSITE, DIMEN_UNSET, TYPE_UNSET, TYPE_UNSET, DIMEN_UNSET, TYPE_UNSET, 0.35F);
        /* eventTimeIndex= */
        /* startTimeUs= */
        /* endTimeUs= */
        /* line= */
        /* lineType= */
        /* lineAnchor= */
        /* position= */
        /* positionAnchor= */
        /* size= */
        WebvttDecoderTest.assertCue(subtitle, 4, 4000000, 5000000, "This is the third subtitle.", ALIGN_CENTER, 0.45F, LINE_TYPE_FRACTION, ANCHOR_TYPE_END, DIMEN_UNSET, TYPE_UNSET, 0.35F);
        /* eventTimeIndex= */
        /* startTimeUs= */
        /* endTimeUs= */
        /* line= */
        /* lineType= */
        /* lineAnchor= */
        /* position= */
        /* positionAnchor= */
        /* size= */
        WebvttDecoderTest.assertCue(subtitle, 6, 6000000, 7000000, "This is the fourth subtitle.", ALIGN_CENTER, (-11.0F), LINE_TYPE_NUMBER, TYPE_UNSET, DIMEN_UNSET, TYPE_UNSET, DIMEN_UNSET);
        /* eventTimeIndex= */
        /* startTimeUs= */
        /* endTimeUs= */
        /* line= */
        /* lineType= */
        /* lineAnchor= */
        /* position= */
        /* positionAnchor= */
        /* size= */
        WebvttDecoderTest.assertCue(subtitle, 8, 7000000, 8000000, "This is the fifth subtitle.", ALIGN_OPPOSITE, DIMEN_UNSET, TYPE_UNSET, TYPE_UNSET, 0.1F, ANCHOR_TYPE_END, 0.1F);
        /* eventTimeIndex= */
        /* startTimeUs= */
        /* endTimeUs= */
        /* line= */
        /* lineType= */
        /* lineAnchor= */
        /* position= */
        /* positionAnchor= */
        /* size= */
        WebvttDecoderTest.assertCue(subtitle, 10, 10000000, 11000000, "This is the sixth subtitle.", ALIGN_CENTER, 0.45F, LINE_TYPE_FRACTION, ANCHOR_TYPE_END, DIMEN_UNSET, TYPE_UNSET, 0.35F);
    }

    @Test
    public void testDecodeWithBadCueHeader() throws SubtitleDecoderException, IOException {
        WebvttSubtitle subtitle = getSubtitleForTestAsset(WebvttDecoderTest.WITH_BAD_CUE_HEADER_FILE);
        // Test event count.
        assertThat(subtitle.getEventTimeCount()).isEqualTo(4);
        // Test cues.
        /* eventTimeIndex= */
        /* startTimeUs= */
        /* endTimeUs= */
        WebvttDecoderTest.assertCue(subtitle, 0, 0, 1234000, "This is the first subtitle.");
        /* eventTimeIndex= */
        /* startTimeUs= */
        /* endTimeUs= */
        WebvttDecoderTest.assertCue(subtitle, 2, 4000000, 5000000, "This is the third subtitle.");
    }

    @Test
    public void testWebvttWithCssStyle() throws SubtitleDecoderException, IOException {
        WebvttSubtitle subtitle = getSubtitleForTestAsset(WebvttDecoderTest.WITH_CSS_STYLES);
        // Test event count.
        assertThat(subtitle.getEventTimeCount()).isEqualTo(8);
        // Test cues.
        /* eventTimeIndex= */
        /* startTimeUs= */
        /* endTimeUs= */
        WebvttDecoderTest.assertCue(subtitle, 0, 0, 1234000, "This is the first subtitle.");
        /* eventTimeIndex= */
        /* startTimeUs= */
        /* endTimeUs= */
        WebvttDecoderTest.assertCue(subtitle, 2, 2345000, 3456000, "This is the second subtitle.");
        Spanned s1 = /* timeUs= */
        getUniqueSpanTextAt(subtitle, 0);
        Spanned s2 = /* timeUs= */
        getUniqueSpanTextAt(subtitle, 2345000);
        Spanned s3 = /* timeUs= */
        getUniqueSpanTextAt(subtitle, 20000000);
        Spanned s4 = /* timeUs= */
        getUniqueSpanTextAt(subtitle, 25000000);
        assertThat(/* start= */
        s1.getSpans(0, s1.length(), ForegroundColorSpan.class)).hasLength(1);
        assertThat(/* start= */
        s1.getSpans(0, s1.length(), BackgroundColorSpan.class)).hasLength(1);
        assertThat(/* start= */
        s2.getSpans(0, s2.length(), ForegroundColorSpan.class)).hasLength(2);
        assertThat(/* start= */
        s3.getSpans(10, s3.length(), UnderlineSpan.class)).hasLength(1);
        assertThat(/* start= */
        /* end= */
        s4.getSpans(0, 16, BackgroundColorSpan.class)).hasLength(2);
        assertThat(/* start= */
        s4.getSpans(17, s4.length(), StyleSpan.class)).hasLength(1);
        assertThat(/* start= */
        s4.getSpans(17, s4.length(), StyleSpan.class)[0].getStyle()).isEqualTo(BOLD);
    }

    @Test
    public void testWithComplexCssSelectors() throws SubtitleDecoderException, IOException {
        WebvttSubtitle subtitle = getSubtitleForTestAsset(WebvttDecoderTest.WITH_CSS_COMPLEX_SELECTORS);
        Spanned text = /* timeUs= */
        getUniqueSpanTextAt(subtitle, 0);
        assertThat(/* start= */
        text.getSpans(30, text.length(), ForegroundColorSpan.class)).hasLength(1);
        assertThat(/* start= */
        text.getSpans(30, text.length(), ForegroundColorSpan.class)[0].getForegroundColor()).isEqualTo(-1146130);
        assertThat(/* start= */
        text.getSpans(30, text.length(), TypefaceSpan.class)).hasLength(1);
        assertThat(/* start= */
        text.getSpans(30, text.length(), TypefaceSpan.class)[0].getFamily()).isEqualTo("courier");
        text = /* timeUs= */
        getUniqueSpanTextAt(subtitle, 2000000);
        assertThat(/* start= */
        text.getSpans(5, text.length(), TypefaceSpan.class)).hasLength(1);
        assertThat(/* start= */
        text.getSpans(5, text.length(), TypefaceSpan.class)[0].getFamily()).isEqualTo("courier");
        text = /* timeUs= */
        getUniqueSpanTextAt(subtitle, 2500000);
        assertThat(/* start= */
        text.getSpans(5, text.length(), StyleSpan.class)).hasLength(1);
        assertThat(/* start= */
        text.getSpans(5, text.length(), StyleSpan.class)[0].getStyle()).isEqualTo(BOLD);
        assertThat(/* start= */
        text.getSpans(5, text.length(), TypefaceSpan.class)).hasLength(1);
        assertThat(/* start= */
        text.getSpans(5, text.length(), TypefaceSpan.class)[0].getFamily()).isEqualTo("courier");
        text = /* timeUs= */
        getUniqueSpanTextAt(subtitle, 4000000);
        assertThat(/* start= */
        /* end= */
        text.getSpans(6, 22, StyleSpan.class)).hasLength(0);
        assertThat(/* start= */
        text.getSpans(30, text.length(), StyleSpan.class)).hasLength(1);
        assertThat(/* start= */
        text.getSpans(30, text.length(), StyleSpan.class)[0].getStyle()).isEqualTo(BOLD);
        text = /* timeUs= */
        getUniqueSpanTextAt(subtitle, 5000000);
        assertThat(/* start= */
        /* end= */
        text.getSpans(9, 17, StyleSpan.class)).hasLength(0);
        assertThat(/* start= */
        text.getSpans(19, text.length(), StyleSpan.class)).hasLength(1);
        assertThat(/* start= */
        text.getSpans(19, text.length(), StyleSpan.class)[0].getStyle()).isEqualTo(ITALIC);
    }
}

