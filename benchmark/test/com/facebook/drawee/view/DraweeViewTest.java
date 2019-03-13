/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.drawee.view;


import android.graphics.drawable.Drawable;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.interfaces.DraweeHierarchy;
import com.facebook.drawee.testing.DraweeMocks;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit test for {@link DraweeView}.
 */
@RunWith(RobolectricTestRunner.class)
public class DraweeViewTest {
    private DraweeView<DraweeHierarchy> mDraweeView;

    private Drawable mDrawable;

    private Drawable mTopLevelDrawable;

    private DraweeHierarchy mDraweeHierarchy;

    private DraweeController mController;

    @Test
    public void testSetContentDescription() {
        String CONTENT_DESCRIPTION = "Test Photo";
        mController.setContentDescription(CONTENT_DESCRIPTION);
        mDraweeView.setController(mController);
        mDraweeView.setContentDescription(mController.getContentDescription());
        Assert.assertSame(mDraweeView.getContentDescription(), CONTENT_DESCRIPTION);
    }

    @Test
    public void testSetHierarchy() {
        mDraweeView.setHierarchy(mDraweeHierarchy);
        Assert.assertSame(mDraweeHierarchy, mDraweeView.getHierarchy());
        Assert.assertSame(mTopLevelDrawable, mDraweeView.getDrawable());
        DraweeHierarchy hierarchy2 = DraweeMocks.mockDraweeHierarchy();
        mDraweeView.setHierarchy(hierarchy2);
        Assert.assertSame(hierarchy2, mDraweeView.getHierarchy());
        Assert.assertSame(hierarchy2.getTopLevelDrawable(), mDraweeView.getDrawable());
    }

    @Test
    public void testSetController() {
        mDraweeView.setHierarchy(mDraweeHierarchy);
        mDraweeView.setController(mController);
        Assert.assertSame(mController, mDraweeView.getController());
        Assert.assertSame(mTopLevelDrawable, mDraweeView.getDrawable());
        Mockito.verify(mController).setHierarchy(mDraweeHierarchy);
    }

    @Test
    public void testClearControllerKeepsHierarchy() {
        mDraweeView.setHierarchy(mDraweeHierarchy);
        mDraweeView.setController(mController);
        mDraweeView.setController(null);
        Assert.assertNull(mDraweeView.getController());
        Assert.assertSame(mTopLevelDrawable, mDraweeView.getDrawable());
        Mockito.verify(mController).setHierarchy(null);
    }

    @Test
    public void testNewControllerKeepsHierarchy() {
        mDraweeView.setHierarchy(mDraweeHierarchy);
        mDraweeView.setController(mController);
        DraweeController controller2 = DraweeMocks.mockController();
        mDraweeView.setController(controller2);
        Assert.assertSame(controller2, mDraweeView.getController());
        Assert.assertSame(mTopLevelDrawable, mDraweeView.getDrawable());
        Mockito.verify(mController).setHierarchy(null);
        Mockito.verify(controller2).setHierarchy(mDraweeHierarchy);
    }

    @Test
    public void testSetDrawable() {
        mDraweeView.setImageDrawable(mDrawable);
        Assert.assertSame(mDrawable, mDraweeView.getDrawable());
        Assert.assertNull(mDraweeView.getController());
    }

    @Test
    public void testSetDrawableAfterController() {
        mDraweeView.setHierarchy(mDraweeHierarchy);
        mDraweeView.onAttachedToWindow();
        mDraweeView.setController(mController);
        mDraweeView.setImageDrawable(mDrawable);
        Assert.assertNull(mDraweeView.getController());
        Assert.assertSame(mDrawable, mDraweeView.getDrawable());
    }

    @Test
    public void testSetControllerAfterDrawable() {
        mDraweeView.setHierarchy(mDraweeHierarchy);
        mDraweeView.onAttachedToWindow();
        mDraweeView.setImageDrawable(mDrawable);
        mDraweeView.setController(mController);
        Assert.assertSame(mController, mDraweeView.getController());
        Assert.assertSame(mTopLevelDrawable, mDraweeView.getDrawable());
    }

    @Test
    public void testLifecycle_Controller() {
        InOrder inOrder = Mockito.inOrder(mController);
        mDraweeView.setHierarchy(mDraweeHierarchy);
        mDraweeView.setController(mController);
        inOrder.verify(mController).setHierarchy(mDraweeHierarchy);
        mDraweeView.onAttachedToWindow();
        inOrder.verify(mController).onAttach();
        mDraweeView.onStartTemporaryDetach();
        inOrder.verify(mController).onDetach();
        mDraweeView.onFinishTemporaryDetach();
        inOrder.verify(mController).onAttach();
        mDraweeView.onDetachedFromWindow();
        inOrder.verify(mController).onDetach();
    }

    @Test
    public void testLifecycle_ControllerSetWhileAttached() {
        InOrder inOrder = Mockito.inOrder(mController);
        mDraweeView.setHierarchy(mDraweeHierarchy);
        mDraweeView.onAttachedToWindow();
        mDraweeView.setController(mController);
        inOrder.verify(mController).setHierarchy(mDraweeHierarchy);
        inOrder.verify(mController).onAttach();
        mDraweeView.onDetachedFromWindow();
        inOrder.verify(mController).onDetach();
    }

    @Test
    public void testLifecycle_NullController() {
        mDraweeView.setHierarchy(mDraweeHierarchy);
        mDraweeView.setController(null);
        mDraweeView.onStartTemporaryDetach();
        mDraweeView.onFinishTemporaryDetach();
    }

    @Test
    public void testLifecycle_Drawable() {
        mDraweeView.setImageDrawable(mDrawable);
        mDraweeView.onStartTemporaryDetach();
        mDraweeView.onFinishTemporaryDetach();
    }
}

