/**
 * Copyright (c) 2015-present, Facebook, Inc.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.widget.text.span;


import BetterImageSpan.BetterImageSpanAlignment;
import Paint.FontMetricsInt;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;


/**
 * Tests the dimensions assigned by {@link BetterImageSpan} ensuring the width/height of is
 * calculated correctly for different combinations of image and text height,
 * as well as span alignment.
 */
@RunWith(ParameterizedRobolectricTestRunner.class)
public class BetterImageSpanTest {
    private Drawable mDrawable;

    private final Rect mBounds = new Rect();

    private FontMetricsInt mFontMetrics = new Paint.FontMetricsInt();

    private String mDescription;

    @BetterImageSpan.BetterImageSpanAlignment
    private int mAlignment;

    private int mDrawableHeight;

    private final int mDrawableWidth = 100;

    private int mFontAscent;

    private int mFontDescent;

    private int mExpectedAscent;

    private int mExpectedDescent;

    private int mFontTop;

    private int mFontBottom;

    private int mExpectedTop;

    private int mExpectedBottom;

    public BetterImageSpanTest(String description, int alignment, int drawableHeight, int fontAscent, int fontDescent, int expectedAscent, int expectedDescent, int fontTop, int fontBottom, int expectedTop, int expectedBottom) {
        mDescription = description;
        mAlignment = alignment;
        mDrawableHeight = drawableHeight;
        mFontAscent = fontAscent;
        mFontDescent = fontDescent;
        mExpectedAscent = expectedAscent;
        mExpectedDescent = expectedDescent;
        mFontTop = fontTop;
        mFontBottom = fontBottom;
        mExpectedTop = expectedTop;
        mExpectedBottom = expectedBottom;
    }

    @Test
    public void testHeight() {
        BetterImageSpan span = new BetterImageSpan(mDrawable, mAlignment);
        span.getSize(null, null, 0, 0, mFontMetrics);
        assertThat(mFontMetrics.descent).describedAs(("Descent for " + (mDescription))).isEqualTo(mExpectedDescent);
        assertThat(mFontMetrics.ascent).describedAs(("Ascent for " + (mDescription))).isEqualTo(mExpectedAscent);
        assertThat(mFontMetrics.top).describedAs(("Top for " + (mDescription))).isEqualTo(mExpectedTop);
        assertThat(mFontMetrics.bottom).describedAs(("Bottom for " + (mDescription))).isEqualTo(mExpectedBottom);
    }

    @Test
    public void testWidth() {
        // The width stays consistent irrespective of alignment.
        BetterImageSpan span = new BetterImageSpan(mDrawable, mAlignment);
        int size = span.getSize(null, null, 0, 0, null);
        assertThat(size).isEqualTo(mDrawableWidth);
    }
}

