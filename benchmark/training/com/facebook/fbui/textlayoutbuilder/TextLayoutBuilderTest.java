/**
 * Copyright 2016-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.fbui.textlayoutbuilder;


import Layout.Alignment.ALIGN_CENTER;
import Spanned.SPAN_INCLUSIVE_EXCLUSIVE;
import Spanned.SPAN_INCLUSIVE_INCLUSIVE;
import TextDirectionHeuristicsCompat.LOCALE;
import TextUtils.TruncateAt.MARQUEE;
import Typeface.MONOSPACE;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.view.View;
import com.facebook.fbui.textlayoutbuilder.shadows.ShadowPicture;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Tests {@link TextLayoutBuilder}
 */
@Config(manifest = Config.NONE, shadows = { ShadowPicture.class })
@RunWith(RobolectricTestRunner.class)
public class TextLayoutBuilderTest {
    private static final String TEST = "TEST";

    private static final String LONG_TEXT = "Lorem ipsum dolor sit amet test \n" + (("Lorem ipsum dolor sit amet test \n" + "Lorem ipsum dolor sit amet test \n") + "Lorem ipsum dolor sit amet test \n");

    private TextLayoutBuilder mBuilder;

    private Layout mLayout;

    // Test setters.
    @Test
    public void testSetText() {
        mLayout = mBuilder.setText("Android").build();
        Assert.assertEquals(mBuilder.getText(), "Android");
        Assert.assertEquals(mLayout.getText(), "Android");
    }

    @Test
    public void testSetTextNull() {
        mLayout = mBuilder.setText(null).build();
        Assert.assertEquals(mBuilder.getText(), null);
        Assert.assertEquals(mLayout, null);
    }

    @Test
    public void testSetTextSize() {
        mLayout = mBuilder.setTextSize(10).build();
        Assert.assertEquals(mBuilder.getTextSize(), 10.0F, 0.0F);
        Assert.assertEquals(mLayout.getPaint().getTextSize(), 10.0F, 0.0F);
    }

    @Test
    public void testSetTextColor() {
        mLayout = mBuilder.setTextColor(-65536).build();
        Assert.assertEquals(mBuilder.getTextColor(), -65536);
        Assert.assertEquals(mLayout.getPaint().getColor(), -65536);
    }

    @Test
    public void testSetTextColorStateList() {
        mLayout = mBuilder.setTextColor(ColorStateList.valueOf(-65536)).build();
        Assert.assertEquals(mBuilder.getTextColor(), -65536);
        Assert.assertEquals(mLayout.getPaint().getColor(), -65536);
    }

    @Test
    public void testSetLinkColor() {
        mLayout = mBuilder.setLinkColor(-65536).build();
        Assert.assertEquals(mBuilder.getLinkColor(), -65536);
        Assert.assertEquals(mLayout.getPaint().linkColor, -65536);
    }

    @Test
    public void testSetTextSpacingExtra() {
        mLayout = mBuilder.setTextSpacingExtra(10).build();
        Assert.assertEquals(mBuilder.getTextSpacingExtra(), 10.0F, 0.0F);
        Assert.assertEquals(mLayout.getSpacingAdd(), 10.0F, 0.0F);
    }

    @Test
    public void testSetTextSpacingMultiplier() {
        mLayout = mBuilder.setTextSpacingMultiplier(1.5F).build();
        Assert.assertEquals(mBuilder.getTextSpacingMultiplier(), 1.5F, 0.0F);
        Assert.assertEquals(mLayout.getSpacingMultiplier(), 1.5F, 0.0F);
    }

    @Test
    public void testSetTextLineHeight() {
        final float lineHeight = 15.0F;
        mLayout = mBuilder.setLineHeight(lineHeight).build();
        Assert.assertEquals(mBuilder.getLineHeight(), 15.0F, 0.0F);
        Assert.assertEquals(mLayout.getSpacingMultiplier(), 1.0F, 0.0F);
        Assert.assertEquals(mLayout.getSpacingAdd(), (lineHeight - (mLayout.getPaint().getFontMetrics(null))), 0.0F);
    }

    @Test
    public void testSetIncludeFontPadding() {
        mLayout = mBuilder.setIncludeFontPadding(false).build();
        Assert.assertEquals(mBuilder.getIncludeFontPadding(), false);
    }

    @Test
    public void testSetAlignment() {
        mLayout = mBuilder.setAlignment(ALIGN_CENTER).build();
        Assert.assertEquals(mBuilder.getAlignment(), ALIGN_CENTER);
        Assert.assertEquals(mLayout.getAlignment(), ALIGN_CENTER);
    }

    @Test
    public void testSetTextDirection() {
        mLayout = mBuilder.setTextDirection(LOCALE).build();
        Assert.assertEquals(mBuilder.getTextDirection(), LOCALE);
    }

    @Test
    public void testSetTypeface() {
        mLayout = mBuilder.setTypeface(MONOSPACE).build();
        Assert.assertEquals(mBuilder.getTypeface(), MONOSPACE);
    }

    @Test
    public void testSetEllipsize() {
        mLayout = mBuilder.setEllipsize(MARQUEE).build();
        Assert.assertEquals(mBuilder.getEllipsize(), MARQUEE);
    }

    @Test
    public void testSetSingleLine() {
        mLayout = mBuilder.setSingleLine(true).build();
        Assert.assertEquals(mBuilder.getSingleLine(), true);
    }

    @Test
    public void testSetMaxLines() {
        mLayout = mBuilder.setMaxLines(10).build();
        Assert.assertEquals(mBuilder.getMaxLines(), 10.0F, 0.0F);
    }

    @Test
    public void testSetShouldCacheLayout() {
        mLayout = mBuilder.setShouldCacheLayout(false).build();
        Assert.assertEquals(mBuilder.getShouldCacheLayout(), false);
    }

    @Test
    public void testSetShouldWarmText() {
        mLayout = mBuilder.setShouldWarmText(true).build();
        Assert.assertEquals(mBuilder.getShouldWarmText(), true);
    }

    @Test
    public void testSetBreakStrategy() {
        mLayout = mBuilder.setBreakStrategy(1).build();
        Assert.assertEquals(mBuilder.getBreakStrategy(), 1);
    }

    @Test
    public void testSetHyphenationFrequency() {
        mLayout = mBuilder.setHyphenationFrequency(1).build();
        Assert.assertEquals(mBuilder.getHyphenationFrequency(), 1);
    }

    @Test
    public void testSetJustificationMode() {
        mLayout = mBuilder.setJustificationMode(1).build();
        Assert.assertEquals(mBuilder.getJustificationMode(), 1);
    }

    @Test
    public void testSetLeftIndents() {
        int[] leftIndents = new int[]{ 0, 1 };
        mLayout = mBuilder.setIndents(leftIndents, null).build();
        Assert.assertEquals(mBuilder.getLeftIndents(), leftIndents);
    }

    @Test
    public void testSetRightIndents() {
        int[] rightIndents = new int[]{ 0, 1 };
        mLayout = mBuilder.setIndents(null, rightIndents).build();
        Assert.assertEquals(mBuilder.getRightIndents(), rightIndents);
    }

    @Test
    public void testSetGlyphWarmer() {
        TextLayoutBuilderTest.FakeGlyphWarmer glyphWarmer = new TextLayoutBuilderTest.FakeGlyphWarmer();
        mLayout = mBuilder.setGlyphWarmer(glyphWarmer).build();
        Assert.assertEquals(mBuilder.getGlyphWarmer(), glyphWarmer);
    }

    // Test functionality.
    @Test
    public void testSingleLine() {
        mLayout = mBuilder.setText(TextLayoutBuilderTest.LONG_TEXT).setSingleLine(true).setWidth(1000).build();
        Assert.assertEquals(mLayout.getLineCount(), 1);
    }

    @Test
    public void testMaxLines() {
        mLayout = mBuilder.setText(TextLayoutBuilderTest.LONG_TEXT).setMaxLines(2).setWidth(1000).build();
        Assert.assertEquals(mLayout.getLineCount(), 2);
    }

    @Test
    public void testMinEms() {
        mBuilder.setText(TextLayoutBuilderTest.LONG_TEXT).setMinEms(10).build();
        Assert.assertEquals(mBuilder.getMinEms(), 10);
        Assert.assertEquals(mBuilder.getMinWidth(), (-1));
    }

    @Test
    public void testMaxEms() {
        mBuilder.setText(TextLayoutBuilderTest.LONG_TEXT).setMaxEms(10).build();
        Assert.assertEquals(mBuilder.getMaxEms(), 10);
        Assert.assertEquals(mBuilder.getMaxWidth(), (-1));
    }

    @Test
    public void testMinWidth() {
        mBuilder.setText(TextLayoutBuilderTest.LONG_TEXT).setMinWidth(100).build();
        Assert.assertEquals(mBuilder.getMinWidth(), 100);
        Assert.assertEquals(mBuilder.getMinEms(), (-1));
    }

    @Test
    public void testMaxWidth() {
        mBuilder.setText(TextLayoutBuilderTest.LONG_TEXT).setMaxWidth(100).build();
        Assert.assertEquals(mBuilder.getMaxWidth(), 100);
        Assert.assertEquals(mBuilder.getMaxEms(), (-1));
    }

    @Test
    public void testDensity() {
        mBuilder.setDensity(1.5F).build();
        Assert.assertEquals(mBuilder.getDensity(), 1.5F, 0.001F);
    }

    @Test
    public void testDrawableState() {
        int[] drawableState = new int[]{ 0, 1 };
        mLayout = mBuilder.setDrawableState(drawableState).build();
        Assert.assertArrayEquals(mBuilder.getDrawableState(), drawableState);
    }

    @Test
    public void testNewPaint() {
        Paint oldPaint = mBuilder.mParams.paint;
        // Build the current builder.
        mBuilder.build();
        // Change paint properties.
        mBuilder.setShadowLayer(10.0F, 1.0F, 1.0F, 0);
        Paint newPaint = mBuilder.mParams.paint;
        Assert.assertNotEquals(oldPaint, newPaint);
    }

    @Test
    public void testWarmText() {
        TextLayoutBuilderTest.FakeGlyphWarmer warmer = new TextLayoutBuilderTest.FakeGlyphWarmer();
        mLayout = mBuilder.setShouldWarmText(true).setGlyphWarmer(warmer).build();
        Assert.assertEquals(warmer.getLayout(), mLayout);
    }

    @Test
    public void testDoNotWarmText() {
        TextLayoutBuilderTest.FakeGlyphWarmer warmer = new TextLayoutBuilderTest.FakeGlyphWarmer();
        mLayout = mBuilder.setShouldWarmText(false).setGlyphWarmer(warmer).build();
        Assert.assertEquals(warmer.getLayout(), null);
    }

    @Test
    public void testCaching() {
        mLayout = mBuilder.setShouldCacheLayout(true).build();
        Layout newLayout = mBuilder.build();
        Assert.assertEquals(mLayout, newLayout);
        Assert.assertEquals(mBuilder.sCache.size(), 1);
        Assert.assertEquals(mBuilder.sCache.get(mBuilder.mParams.hashCode()), mLayout);
    }

    @Test
    public void testNoCaching() {
        mLayout = mBuilder.setShouldCacheLayout(false).build();
        Layout newLayout = mBuilder.build();
        Assert.assertNotEquals(mLayout, newLayout);
        Assert.assertEquals(mBuilder.sCache.size(), 0);
        Assert.assertEquals(mBuilder.sCache.get(mBuilder.mParams.hashCode()), null);
    }

    @Test
    public void testTwoBuildersWithSameParamsAndCaching() {
        mLayout = mBuilder.setShouldCacheLayout(true).build();
        TextLayoutBuilder newBuilder = new TextLayoutBuilder();
        Layout newLayout = newBuilder.setText(TextLayoutBuilderTest.TEST).setShouldCacheLayout(true).build();
        Assert.assertEquals(mLayout, newLayout);
    }

    @Test
    public void testTwoBuildersWithSameParamsAndNoCaching() {
        mLayout = mBuilder.setShouldCacheLayout(false).build();
        TextLayoutBuilder newBuilder = new TextLayoutBuilder();
        Layout newLayout = newBuilder.setText(TextLayoutBuilderTest.TEST).setShouldCacheLayout(false).build();
        Assert.assertNotEquals(mLayout, newLayout);
    }

    @Test
    public void testSpannableString() {
        SpannableStringBuilder spannable = new SpannableStringBuilder("This is a bold text");
        spannable.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), 10, 13, SPAN_INCLUSIVE_INCLUSIVE);
        mLayout = mBuilder.setText(spannable).build();
        Assert.assertEquals(mLayout.getText(), spannable);
    }

    @Test
    public void testCachingSpannableString() {
        SpannableStringBuilder spannable = new SpannableStringBuilder("This is a bold text");
        spannable.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), 10, 13, SPAN_INCLUSIVE_INCLUSIVE);
        mLayout = mBuilder.setText(spannable).setShouldCacheLayout(true).build();
        Assert.assertEquals(mBuilder.sCache.size(), 1);
        Assert.assertEquals(mBuilder.sCache.get(mBuilder.mParams.hashCode()), mLayout);
    }

    @Test
    public void testNoCachingSpannableString() {
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // Do nothing.
            }
        };
        SpannableStringBuilder spannable = new SpannableStringBuilder("This is a bold text");
        spannable.setSpan(clickableSpan, 10, 13, SPAN_INCLUSIVE_INCLUSIVE);
        mLayout = mBuilder.setText(spannable).setShouldCacheLayout(true).build();
        Assert.assertEquals(mBuilder.sCache.size(), 0);
        Assert.assertEquals(mBuilder.sCache.get(mBuilder.mParams.hashCode()), null);
    }

    @Config(sdk = 21)
    @Test(expected = IllegalArgumentException.class)
    public void testNullSpansAreCaught() {
        SpannableStringBuilder ssb = new SpannableStringBuilder().append("abcd", null, SPAN_INCLUSIVE_EXCLUSIVE);
        mBuilder.setText(ssb).build();
    }

    private static class FakeGlyphWarmer implements GlyphWarmer {
        private Layout mLayout = null;

        @Override
        public void warmLayout(Layout layout) {
            mLayout = layout;
        }

        Layout getLayout() {
            return mLayout;
        }
    }
}

