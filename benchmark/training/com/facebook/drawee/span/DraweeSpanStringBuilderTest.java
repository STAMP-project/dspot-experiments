/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.drawee.span;


import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.facebook.drawee.view.DraweeHolder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests {@link DraweeSpanStringBuilder}
 */
@RunWith(RobolectricTestRunner.class)
public class DraweeSpanStringBuilderTest {
    private static final String TEXT = "ABCDEFG";

    private static final int DRAWABLE_WIDTH = 10;

    private static final int DRAWABLE_HEIGHT = 32;

    @Mock
    public DraweeHolder mDraweeHolder;

    @Mock
    public Drawable mTopLevelDrawable;

    @Mock
    public Rect mDrawableBounds;

    @Mock
    public View mView;

    private DraweeSpanStringBuilder mDraweeSpanStringBuilder;

    @Test
    public void testTextCorrect() {
        assertThat(mDraweeSpanStringBuilder.toString()).isEqualTo(DraweeSpanStringBuilderTest.TEXT);
    }

    @Test
    public void testNoDraweeSpan() {
        assertThat(mDraweeSpanStringBuilder.hasDraweeSpans()).isFalse();
    }

    @Test
    public void testDraweeSpanAdded() {
        DraweeSpanStringBuilderTest.addDraweeSpan(mDraweeSpanStringBuilder, mDraweeHolder, 3, 1);
        assertThat(mDraweeSpanStringBuilder.toString()).isEqualTo(DraweeSpanStringBuilderTest.TEXT);
        assertThat(mDraweeSpanStringBuilder.hasDraweeSpans()).isTrue();
    }

    @Test
    public void testLifecycle() {
        DraweeSpanStringBuilderTest.addDraweeSpan(mDraweeSpanStringBuilder, mDraweeHolder, 3, 1);
        mDraweeSpanStringBuilder.onAttach();
        Mockito.verify(mDraweeHolder).onAttach();
        mDraweeSpanStringBuilder.onDetach();
        Mockito.verify(mDraweeHolder).onDetach();
    }

    @Test
    public void testDraweeSpanInSpannable() {
        DraweeSpanStringBuilderTest.addDraweeSpan(mDraweeSpanStringBuilder, mDraweeHolder, 3, 1);
        DraweeSpan[] draweeSpans = mDraweeSpanStringBuilder.getSpans(0, mDraweeSpanStringBuilder.length(), DraweeSpan.class);
        assertThat(draweeSpans).hasSize(1);
        assertThat(draweeSpans[0].getDrawable()).isEqualTo(mTopLevelDrawable);
    }
}

