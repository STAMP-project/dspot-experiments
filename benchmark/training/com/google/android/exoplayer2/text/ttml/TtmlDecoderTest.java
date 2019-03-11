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
package com.google.android.exoplayer2.text.ttml;


import Cue.DIMEN_UNSET;
import Layout.Alignment.ALIGN_CENTER;
import TtmlNode.TAG_BODY;
import TtmlNode.TAG_DIV;
import TtmlNode.TAG_P;
import TtmlStyle.STYLE_BOLD_ITALIC;
import TtmlStyle.STYLE_ITALIC;
import TtmlStyle.STYLE_NORMAL;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.RelativeSizeSpan;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.SubtitleDecoderException;
import com.google.android.exoplayer2.util.ColorParser;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit test for {@link TtmlDecoder}.
 */
@RunWith(RobolectricTestRunner.class)
public final class TtmlDecoderTest {
    private static final String INLINE_ATTRIBUTES_TTML_FILE = "ttml/inline_style_attributes.xml";

    private static final String INHERIT_STYLE_TTML_FILE = "ttml/inherit_style.xml";

    private static final String INHERIT_STYLE_OVERRIDE_TTML_FILE = "ttml/inherit_and_override_style.xml";

    private static final String INHERIT_GLOBAL_AND_PARENT_TTML_FILE = "ttml/inherit_global_and_parent.xml";

    private static final String INHERIT_MULTIPLE_STYLES_TTML_FILE = "ttml/inherit_multiple_styles.xml";

    private static final String CHAIN_MULTIPLE_STYLES_TTML_FILE = "ttml/chain_multiple_styles.xml";

    private static final String MULTIPLE_REGIONS_TTML_FILE = "ttml/multiple_regions.xml";

    private static final String NO_UNDERLINE_LINETHROUGH_TTML_FILE = "ttml/no_underline_linethrough.xml";

    private static final String FONT_SIZE_TTML_FILE = "ttml/font_size.xml";

    private static final String FONT_SIZE_MISSING_UNIT_TTML_FILE = "ttml/font_size_no_unit.xml";

    private static final String FONT_SIZE_INVALID_TTML_FILE = "ttml/font_size_invalid.xml";

    private static final String FONT_SIZE_EMPTY_TTML_FILE = "ttml/font_size_empty.xml";

    private static final String FRAME_RATE_TTML_FILE = "ttml/frame_rate.xml";

    private static final String BITMAP_REGION_FILE = "ttml/bitmap_percentage_region.xml";

    private static final String BITMAP_PIXEL_REGION_FILE = "ttml/bitmap_pixel_region.xml";

    private static final String BITMAP_UNSUPPORTED_REGION_FILE = "ttml/bitmap_unsupported_region.xml";

    @Test
    public void testInlineAttributes() throws SubtitleDecoderException, IOException {
        TtmlSubtitle subtitle = getSubtitle(TtmlDecoderTest.INLINE_ATTRIBUTES_TTML_FILE);
        assertThat(subtitle.getEventTimeCount()).isEqualTo(4);
        TtmlNode root = subtitle.getRoot();
        TtmlNode body = queryChildrenForTag(root, TAG_BODY, 0);
        TtmlNode firstDiv = queryChildrenForTag(body, TAG_DIV, 0);
        TtmlStyle firstPStyle = queryChildrenForTag(firstDiv, TAG_P, 0).style;
        assertThat(firstPStyle.getFontColor()).isEqualTo(ColorParser.parseTtmlColor("yellow"));
        assertThat(firstPStyle.getBackgroundColor()).isEqualTo(ColorParser.parseTtmlColor("blue"));
        assertThat(firstPStyle.getFontFamily()).isEqualTo("serif");
        assertThat(firstPStyle.getStyle()).isEqualTo(STYLE_BOLD_ITALIC);
        assertThat(firstPStyle.isUnderline()).isTrue();
    }

    @Test
    public void testInheritInlineAttributes() throws SubtitleDecoderException, IOException {
        TtmlSubtitle subtitle = getSubtitle(TtmlDecoderTest.INLINE_ATTRIBUTES_TTML_FILE);
        assertThat(subtitle.getEventTimeCount()).isEqualTo(4);
        assertSpans(subtitle, 20, "text 2", "sansSerif", STYLE_ITALIC, -16711681, ColorParser.parseTtmlColor("lime"), false, true, null);
    }

    /**
     * Regression test for devices on JellyBean where some named colors are not correctly defined on
     * framework level. Tests that <i>lime</i> resolves to <code>#FF00FF00</code> not <code>#00FF00
     * </code>.
     *
     * @see <a
    href="https://github.com/android/platform_frameworks_base/blob/jb-mr2-release/graphics/java/android/graphics/Color.java#L414">
    JellyBean Color</a> <a
    href="https://github.com/android/platform_frameworks_base/blob/kitkat-mr2.2-release/graphics/java/android/graphics/Color.java#L414">
    Kitkat Color</a>
     * @throws IOException
     * 		thrown if reading subtitle file fails.
     */
    @Test
    public void testLime() throws SubtitleDecoderException, IOException {
        TtmlSubtitle subtitle = getSubtitle(TtmlDecoderTest.INLINE_ATTRIBUTES_TTML_FILE);
        assertThat(subtitle.getEventTimeCount()).isEqualTo(4);
        assertSpans(subtitle, 20, "text 2", "sansSerif", STYLE_ITALIC, -16711681, -16711936, false, true, null);
    }

    @Test
    public void testInheritGlobalStyle() throws SubtitleDecoderException, IOException {
        TtmlSubtitle subtitle = getSubtitle(TtmlDecoderTest.INHERIT_STYLE_TTML_FILE);
        assertThat(subtitle.getEventTimeCount()).isEqualTo(2);
        assertSpans(subtitle, 10, "text 1", "serif", STYLE_BOLD_ITALIC, -16776961, -256, true, false, null);
    }

    @Test
    public void testInheritGlobalStyleOverriddenByInlineAttributes() throws SubtitleDecoderException, IOException {
        TtmlSubtitle subtitle = getSubtitle(TtmlDecoderTest.INHERIT_STYLE_OVERRIDE_TTML_FILE);
        assertThat(subtitle.getEventTimeCount()).isEqualTo(4);
        assertSpans(subtitle, 10, "text 1", "serif", STYLE_BOLD_ITALIC, -16776961, -256, true, false, null);
        assertSpans(subtitle, 20, "text 2", "sansSerif", STYLE_ITALIC, -65536, -256, true, false, null);
    }

    @Test
    public void testInheritGlobalAndParent() throws SubtitleDecoderException, IOException {
        TtmlSubtitle subtitle = getSubtitle(TtmlDecoderTest.INHERIT_GLOBAL_AND_PARENT_TTML_FILE);
        assertThat(subtitle.getEventTimeCount()).isEqualTo(4);
        assertSpans(subtitle, 10, "text 1", "sansSerif", STYLE_NORMAL, -65536, ColorParser.parseTtmlColor("lime"), false, true, ALIGN_CENTER);
        assertSpans(subtitle, 20, "text 2", "serif", STYLE_BOLD_ITALIC, -16776961, -256, true, true, ALIGN_CENTER);
    }

    @Test
    public void testInheritMultipleStyles() throws SubtitleDecoderException, IOException {
        TtmlSubtitle subtitle = getSubtitle(TtmlDecoderTest.INHERIT_MULTIPLE_STYLES_TTML_FILE);
        assertThat(subtitle.getEventTimeCount()).isEqualTo(12);
        assertSpans(subtitle, 10, "text 1", "sansSerif", STYLE_BOLD_ITALIC, -16776961, -256, false, true, null);
    }

    @Test
    public void testInheritMultipleStylesWithoutLocalAttributes() throws SubtitleDecoderException, IOException {
        TtmlSubtitle subtitle = getSubtitle(TtmlDecoderTest.INHERIT_MULTIPLE_STYLES_TTML_FILE);
        assertThat(subtitle.getEventTimeCount()).isEqualTo(12);
        assertSpans(subtitle, 20, "text 2", "sansSerif", STYLE_BOLD_ITALIC, -16776961, -16777216, false, true, null);
    }

    @Test
    public void testMergeMultipleStylesWithParentStyle() throws SubtitleDecoderException, IOException {
        TtmlSubtitle subtitle = getSubtitle(TtmlDecoderTest.INHERIT_MULTIPLE_STYLES_TTML_FILE);
        assertThat(subtitle.getEventTimeCount()).isEqualTo(12);
        assertSpans(subtitle, 30, "text 2.5", "sansSerifInline", STYLE_ITALIC, -65536, -256, true, true, null);
    }

    @Test
    public void testMultipleRegions() throws SubtitleDecoderException, IOException {
        TtmlSubtitle subtitle = getSubtitle(TtmlDecoderTest.MULTIPLE_REGIONS_TTML_FILE);
        List<Cue> cues = subtitle.getCues(1000000);
        assertThat(cues).hasSize(2);
        Cue cue = cues.get(0);
        assertThat(cue.text.toString()).isEqualTo("lorem");
        assertThat(cue.position).isEqualTo((10.0F / 100.0F));
        assertThat(cue.line).isEqualTo((10.0F / 100.0F));
        assertThat(cue.size).isEqualTo((20.0F / 100.0F));
        cue = cues.get(1);
        assertThat(cue.text.toString()).isEqualTo("amet");
        assertThat(cue.position).isEqualTo((60.0F / 100.0F));
        assertThat(cue.line).isEqualTo((10.0F / 100.0F));
        assertThat(cue.size).isEqualTo((20.0F / 100.0F));
        cues = subtitle.getCues(5000000);
        assertThat(cues).hasSize(1);
        cue = cues.get(0);
        assertThat(cue.text.toString()).isEqualTo("ipsum");
        assertThat(cue.position).isEqualTo((40.0F / 100.0F));
        assertThat(cue.line).isEqualTo((40.0F / 100.0F));
        assertThat(cue.size).isEqualTo((20.0F / 100.0F));
        cues = subtitle.getCues(9000000);
        assertThat(cues).hasSize(1);
        cue = cues.get(0);
        assertThat(cue.text.toString()).isEqualTo("dolor");
        assertThat(cue.position).isEqualTo(DIMEN_UNSET);
        assertThat(cue.line).isEqualTo(DIMEN_UNSET);
        assertThat(cue.size).isEqualTo(DIMEN_UNSET);
        // TODO: Should be as below, once https://github.com/google/ExoPlayer/issues/2953 is fixed.
        // assertEquals(10f / 100f, cue.position);
        // assertEquals(80f / 100f, cue.line);
        // assertEquals(1f, cue.size);
        cues = subtitle.getCues(21000000);
        assertThat(cues).hasSize(1);
        cue = cues.get(0);
        assertThat(cue.text.toString()).isEqualTo("She first said this");
        assertThat(cue.position).isEqualTo((45.0F / 100.0F));
        assertThat(cue.line).isEqualTo((45.0F / 100.0F));
        assertThat(cue.size).isEqualTo((35.0F / 100.0F));
        cues = subtitle.getCues(25000000);
        cue = cues.get(0);
        assertThat(cue.text.toString()).isEqualTo("She first said this\nThen this");
        cues = subtitle.getCues(29000000);
        assertThat(cues).hasSize(1);
        cue = cues.get(0);
        assertThat(cue.text.toString()).isEqualTo("She first said this\nThen this\nFinally this");
        assertThat(cue.position).isEqualTo((45.0F / 100.0F));
        assertThat(cue.line).isEqualTo((45.0F / 100.0F));
    }

    @Test
    public void testEmptyStyleAttribute() throws SubtitleDecoderException, IOException {
        TtmlSubtitle subtitle = getSubtitle(TtmlDecoderTest.INHERIT_MULTIPLE_STYLES_TTML_FILE);
        assertThat(subtitle.getEventTimeCount()).isEqualTo(12);
        TtmlNode root = subtitle.getRoot();
        TtmlNode body = queryChildrenForTag(root, TAG_BODY, 0);
        TtmlNode fourthDiv = queryChildrenForTag(body, TAG_DIV, 3);
        assertThat(queryChildrenForTag(fourthDiv, TAG_P, 0).getStyleIds()).isNull();
    }

    @Test
    public void testNonexistingStyleId() throws SubtitleDecoderException, IOException {
        TtmlSubtitle subtitle = getSubtitle(TtmlDecoderTest.INHERIT_MULTIPLE_STYLES_TTML_FILE);
        assertThat(subtitle.getEventTimeCount()).isEqualTo(12);
        TtmlNode root = subtitle.getRoot();
        TtmlNode body = queryChildrenForTag(root, TAG_BODY, 0);
        TtmlNode fifthDiv = queryChildrenForTag(body, TAG_DIV, 4);
        assertThat(queryChildrenForTag(fifthDiv, TAG_P, 0).getStyleIds()).hasLength(1);
    }

    @Test
    public void testNonExistingAndExistingStyleIdWithRedundantSpaces() throws SubtitleDecoderException, IOException {
        TtmlSubtitle subtitle = getSubtitle(TtmlDecoderTest.INHERIT_MULTIPLE_STYLES_TTML_FILE);
        assertThat(subtitle.getEventTimeCount()).isEqualTo(12);
        TtmlNode root = subtitle.getRoot();
        TtmlNode body = queryChildrenForTag(root, TAG_BODY, 0);
        TtmlNode sixthDiv = queryChildrenForTag(body, TAG_DIV, 5);
        String[] styleIds = queryChildrenForTag(sixthDiv, TAG_P, 0).getStyleIds();
        assertThat(styleIds).hasLength(2);
    }

    @Test
    public void testMultipleChaining() throws SubtitleDecoderException, IOException {
        TtmlSubtitle subtitle = getSubtitle(TtmlDecoderTest.CHAIN_MULTIPLE_STYLES_TTML_FILE);
        assertThat(subtitle.getEventTimeCount()).isEqualTo(2);
        Map<String, TtmlStyle> globalStyles = subtitle.getGlobalStyles();
        TtmlStyle style = globalStyles.get("s2");
        assertThat(style.getFontFamily()).isEqualTo("serif");
        assertThat(style.getBackgroundColor()).isEqualTo(-65536);
        assertThat(style.getFontColor()).isEqualTo(-16777216);
        assertThat(style.getStyle()).isEqualTo(STYLE_BOLD_ITALIC);
        assertThat(style.isLinethrough()).isTrue();
        style = globalStyles.get("s3");
        // only difference: color must be RED
        assertThat(style.getFontColor()).isEqualTo(-65536);
        assertThat(style.getFontFamily()).isEqualTo("serif");
        assertThat(style.getBackgroundColor()).isEqualTo(-65536);
        assertThat(style.getStyle()).isEqualTo(STYLE_BOLD_ITALIC);
        assertThat(style.isLinethrough()).isTrue();
    }

    @Test
    public void testNoUnderline() throws SubtitleDecoderException, IOException {
        TtmlSubtitle subtitle = getSubtitle(TtmlDecoderTest.NO_UNDERLINE_LINETHROUGH_TTML_FILE);
        assertThat(subtitle.getEventTimeCount()).isEqualTo(4);
        TtmlNode root = subtitle.getRoot();
        TtmlNode body = queryChildrenForTag(root, TAG_BODY, 0);
        TtmlNode div = queryChildrenForTag(body, TAG_DIV, 0);
        TtmlStyle style = queryChildrenForTag(div, TAG_P, 0).style;
        assertWithMessage("noUnderline from inline attribute expected").that(style.isUnderline()).isFalse();
    }

    @Test
    public void testNoLinethrough() throws SubtitleDecoderException, IOException {
        TtmlSubtitle subtitle = getSubtitle(TtmlDecoderTest.NO_UNDERLINE_LINETHROUGH_TTML_FILE);
        assertThat(subtitle.getEventTimeCount()).isEqualTo(4);
        TtmlNode root = subtitle.getRoot();
        TtmlNode body = queryChildrenForTag(root, TAG_BODY, 0);
        TtmlNode div = queryChildrenForTag(body, TAG_DIV, 1);
        TtmlStyle style = queryChildrenForTag(div, TAG_P, 0).style;
        assertWithMessage("noLineThrough from inline attribute expected in second pNode").that(style.isLinethrough()).isFalse();
    }

    @Test
    public void testFontSizeSpans() throws SubtitleDecoderException, IOException {
        TtmlSubtitle subtitle = getSubtitle(TtmlDecoderTest.FONT_SIZE_TTML_FILE);
        assertThat(subtitle.getEventTimeCount()).isEqualTo(10);
        List<Cue> cues = subtitle.getCues((10 * 1000000));
        assertThat(cues).hasSize(1);
        SpannableStringBuilder spannable = ((SpannableStringBuilder) (cues.get(0).text));
        assertThat(String.valueOf(spannable)).isEqualTo("text 1");
        assertAbsoluteFontSize(spannable, 32);
        cues = subtitle.getCues((20 * 1000000));
        assertThat(cues).hasSize(1);
        spannable = ((SpannableStringBuilder) (cues.get(0).text));
        assertThat(String.valueOf(cues.get(0).text)).isEqualTo("text 2");
        assertRelativeFontSize(spannable, 2.2F);
        cues = subtitle.getCues((30 * 1000000));
        assertThat(cues).hasSize(1);
        spannable = ((SpannableStringBuilder) (cues.get(0).text));
        assertThat(String.valueOf(cues.get(0).text)).isEqualTo("text 3");
        assertRelativeFontSize(spannable, 1.5F);
        cues = subtitle.getCues((40 * 1000000));
        assertThat(cues).hasSize(1);
        spannable = ((SpannableStringBuilder) (cues.get(0).text));
        assertThat(String.valueOf(cues.get(0).text)).isEqualTo("two values");
        assertAbsoluteFontSize(spannable, 16);
        cues = subtitle.getCues((50 * 1000000));
        assertThat(cues).hasSize(1);
        spannable = ((SpannableStringBuilder) (cues.get(0).text));
        assertThat(String.valueOf(cues.get(0).text)).isEqualTo("leading dot");
        assertRelativeFontSize(spannable, 0.5F);
    }

    @Test
    public void testFontSizeWithMissingUnitIsIgnored() throws SubtitleDecoderException, IOException {
        TtmlSubtitle subtitle = getSubtitle(TtmlDecoderTest.FONT_SIZE_MISSING_UNIT_TTML_FILE);
        assertThat(subtitle.getEventTimeCount()).isEqualTo(2);
        List<Cue> cues = subtitle.getCues((10 * 1000000));
        assertThat(cues).hasSize(1);
        SpannableStringBuilder spannable = ((SpannableStringBuilder) (cues.get(0).text));
        assertThat(String.valueOf(spannable)).isEqualTo("no unit");
        assertThat(spannable.getSpans(0, spannable.length(), RelativeSizeSpan.class)).hasLength(0);
        assertThat(spannable.getSpans(0, spannable.length(), AbsoluteSizeSpan.class)).hasLength(0);
    }

    @Test
    public void testFontSizeWithInvalidValueIsIgnored() throws SubtitleDecoderException, IOException {
        TtmlSubtitle subtitle = getSubtitle(TtmlDecoderTest.FONT_SIZE_INVALID_TTML_FILE);
        assertThat(subtitle.getEventTimeCount()).isEqualTo(6);
        List<Cue> cues = subtitle.getCues((10 * 1000000));
        assertThat(cues).hasSize(1);
        SpannableStringBuilder spannable = ((SpannableStringBuilder) (cues.get(0).text));
        assertThat(String.valueOf(spannable)).isEqualTo("invalid");
        assertThat(spannable.getSpans(0, spannable.length(), RelativeSizeSpan.class)).hasLength(0);
        assertThat(spannable.getSpans(0, spannable.length(), AbsoluteSizeSpan.class)).hasLength(0);
        cues = subtitle.getCues((20 * 1000000));
        assertThat(cues).hasSize(1);
        spannable = ((SpannableStringBuilder) (cues.get(0).text));
        assertThat(String.valueOf(spannable)).isEqualTo("invalid");
        assertThat(spannable.getSpans(0, spannable.length(), RelativeSizeSpan.class)).hasLength(0);
        assertThat(spannable.getSpans(0, spannable.length(), AbsoluteSizeSpan.class)).hasLength(0);
        cues = subtitle.getCues((30 * 1000000));
        assertThat(cues).hasSize(1);
        spannable = ((SpannableStringBuilder) (cues.get(0).text));
        assertThat(String.valueOf(spannable)).isEqualTo("invalid dot");
        assertThat(spannable.getSpans(0, spannable.length(), RelativeSizeSpan.class)).hasLength(0);
        assertThat(spannable.getSpans(0, spannable.length(), AbsoluteSizeSpan.class)).hasLength(0);
    }

    @Test
    public void testFontSizeWithEmptyValueIsIgnored() throws SubtitleDecoderException, IOException {
        TtmlSubtitle subtitle = getSubtitle(TtmlDecoderTest.FONT_SIZE_EMPTY_TTML_FILE);
        assertThat(subtitle.getEventTimeCount()).isEqualTo(2);
        List<Cue> cues = subtitle.getCues((10 * 1000000));
        assertThat(cues).hasSize(1);
        SpannableStringBuilder spannable = ((SpannableStringBuilder) (cues.get(0).text));
        assertThat(String.valueOf(spannable)).isEqualTo("empty");
        assertThat(spannable.getSpans(0, spannable.length(), RelativeSizeSpan.class)).hasLength(0);
        assertThat(spannable.getSpans(0, spannable.length(), AbsoluteSizeSpan.class)).hasLength(0);
    }

    @Test
    public void testFrameRate() throws SubtitleDecoderException, IOException {
        TtmlSubtitle subtitle = getSubtitle(TtmlDecoderTest.FRAME_RATE_TTML_FILE);
        assertThat(subtitle.getEventTimeCount()).isEqualTo(4);
        assertThat(subtitle.getEventTime(0)).isEqualTo(1000000);
        assertThat(subtitle.getEventTime(1)).isEqualTo(1010000);
        assertThat(((double) (subtitle.getEventTime(2)))).isWithin(1000).of(1001000000);
        assertThat(((double) (subtitle.getEventTime(3)))).isWithin(2000).of(2002000000);
    }

    @Test
    public void testBitmapPercentageRegion() throws SubtitleDecoderException, IOException {
        TtmlSubtitle subtitle = getSubtitle(TtmlDecoderTest.BITMAP_REGION_FILE);
        List<Cue> cues = subtitle.getCues(1000000);
        assertThat(cues).hasSize(1);
        Cue cue = cues.get(0);
        assertThat(cue.text).isNull();
        assertThat(cue.bitmap).isNotNull();
        assertThat(cue.position).isEqualTo((24.0F / 100.0F));
        assertThat(cue.line).isEqualTo((28.0F / 100.0F));
        assertThat(cue.size).isEqualTo((51.0F / 100.0F));
        assertThat(cue.bitmapHeight).isEqualTo(DIMEN_UNSET);
        cues = subtitle.getCues(4000000);
        assertThat(cues).hasSize(1);
        cue = cues.get(0);
        assertThat(cue.text).isNull();
        assertThat(cue.bitmap).isNotNull();
        assertThat(cue.position).isEqualTo((21.0F / 100.0F));
        assertThat(cue.line).isEqualTo((35.0F / 100.0F));
        assertThat(cue.size).isEqualTo((57.0F / 100.0F));
        assertThat(cue.bitmapHeight).isEqualTo(DIMEN_UNSET);
        cues = subtitle.getCues(7500000);
        assertThat(cues).hasSize(1);
        cue = cues.get(0);
        assertThat(cue.text).isNull();
        assertThat(cue.bitmap).isNotNull();
        assertThat(cue.position).isEqualTo((24.0F / 100.0F));
        assertThat(cue.line).isEqualTo((28.0F / 100.0F));
        assertThat(cue.size).isEqualTo((51.0F / 100.0F));
        assertThat(cue.bitmapHeight).isEqualTo(DIMEN_UNSET);
    }

    @Test
    public void testBitmapPixelRegion() throws SubtitleDecoderException, IOException {
        TtmlSubtitle subtitle = getSubtitle(TtmlDecoderTest.BITMAP_PIXEL_REGION_FILE);
        List<Cue> cues = subtitle.getCues(1000000);
        assertThat(cues).hasSize(1);
        Cue cue = cues.get(0);
        assertThat(cue.text).isNull();
        assertThat(cue.bitmap).isNotNull();
        assertThat(cue.position).isEqualTo((307.0F / 1280.0F));
        assertThat(cue.line).isEqualTo((562.0F / 720.0F));
        assertThat(cue.size).isEqualTo((653.0F / 1280.0F));
        assertThat(cue.bitmapHeight).isEqualTo(DIMEN_UNSET);
        cues = subtitle.getCues(4000000);
        assertThat(cues).hasSize(1);
        cue = cues.get(0);
        assertThat(cue.text).isNull();
        assertThat(cue.bitmap).isNotNull();
        assertThat(cue.position).isEqualTo((269.0F / 1280.0F));
        assertThat(cue.line).isEqualTo((612.0F / 720.0F));
        assertThat(cue.size).isEqualTo((730.0F / 1280.0F));
        assertThat(cue.bitmapHeight).isEqualTo(DIMEN_UNSET);
    }

    @Test
    public void testBitmapUnsupportedRegion() throws SubtitleDecoderException, IOException {
        TtmlSubtitle subtitle = getSubtitle(TtmlDecoderTest.BITMAP_UNSUPPORTED_REGION_FILE);
        List<Cue> cues = subtitle.getCues(1000000);
        assertThat(cues).hasSize(1);
        Cue cue = cues.get(0);
        assertThat(cue.text).isNull();
        assertThat(cue.bitmap).isNotNull();
        assertThat(cue.position).isEqualTo(DIMEN_UNSET);
        assertThat(cue.line).isEqualTo(DIMEN_UNSET);
        assertThat(cue.size).isEqualTo(DIMEN_UNSET);
        assertThat(cue.bitmapHeight).isEqualTo(DIMEN_UNSET);
        cues = subtitle.getCues(4000000);
        assertThat(cues).hasSize(1);
        cue = cues.get(0);
        assertThat(cue.text).isNull();
        assertThat(cue.bitmap).isNotNull();
        assertThat(cue.position).isEqualTo(DIMEN_UNSET);
        assertThat(cue.line).isEqualTo(DIMEN_UNSET);
        assertThat(cue.size).isEqualTo(DIMEN_UNSET);
        assertThat(cue.bitmapHeight).isEqualTo(DIMEN_UNSET);
    }
}

