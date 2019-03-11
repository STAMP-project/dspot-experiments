/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.drawee.span;


import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import com.facebook.drawee.view.DraweeHolder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests {@link DraweeSpan}
 */
@RunWith(RobolectricTestRunner.class)
public class DraweeSpanTest {
    @Mock
    public DraweeHolder mDraweeHierarchy;

    @Mock
    public Rect mBounds;

    @Mock
    public Drawable mDrawable;

    private DraweeSpan mDraweeSpan;

    @Test
    public void testLifecycle() {
        mDraweeSpan.onAttach();
        Mockito.verify(mDraweeHierarchy).onAttach();
        mDraweeSpan.onDetach();
        Mockito.verify(mDraweeHierarchy).onDetach();
    }

    @Test
    public void testGetDrawable() {
        Drawable drawable = mDraweeSpan.getDrawable();
        Mockito.verify(mDraweeHierarchy).getTopLevelDrawable();
        assertThat(drawable).isEqualTo(mDrawable);
    }
}

