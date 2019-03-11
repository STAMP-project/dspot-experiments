/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.drawee.view;


import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.interfaces.DraweeHierarchy;
import com.facebook.drawee.testing.DraweeMocks;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class DraweeHolderTest {
    private DraweeHolder mDraweeHolder;

    private Drawable mTopLevelDrawable;

    private DraweeHierarchy mDraweeHierarchy;

    private DraweeController mController;

    private InOrder mInOrderVerifier;

    @Test
    public void testOverrideControllerHierarchy() {
        DraweeHierarchy otherHierarchy = Mockito.mock(DraweeHierarchy.class);
        mController.setHierarchy(otherHierarchy);
        Assert.assertSame(otherHierarchy, mController.getHierarchy());
        mDraweeHolder.setController(mController);
        Assert.assertSame(mController, mDraweeHolder.getController());
        Assert.assertSame(mDraweeHierarchy, mDraweeHolder.getHierarchy());
        Assert.assertSame(mDraweeHierarchy, mController.getHierarchy());
    }

    @Test
    public void testSetControllerWithoutHierarchy() {
        mDraweeHolder.setController(mController);
        Assert.assertSame(mController, mDraweeHolder.getController());
        Assert.assertSame(mDraweeHierarchy, mDraweeHolder.getHierarchy());
        Assert.assertSame(mDraweeHierarchy, mController.getHierarchy());
    }

    @Test
    public void testSetControllerBeforeHierarchy() {
        mDraweeHolder = new DraweeHolder(null);
        mDraweeHolder.setController(mController);
        mDraweeHolder.setHierarchy(mDraweeHierarchy);
        Assert.assertSame(mController, mDraweeHolder.getController());
        Assert.assertSame(mDraweeHierarchy, mDraweeHolder.getHierarchy());
        Assert.assertSame(mDraweeHierarchy, mController.getHierarchy());
    }

    @Test
    public void testClearControllerKeepsHierarchy() {
        mDraweeHolder.setController(mController);
        mDraweeHolder.setController(null);
        Assert.assertSame(mDraweeHierarchy, mDraweeHolder.getHierarchy());
        Assert.assertNull(mDraweeHolder.getController());
        Assert.assertNull(mController.getHierarchy());
    }

    @Test
    public void testNewControllerKeepsHierarchy() {
        mDraweeHolder.setController(mController);
        Assert.assertSame(mDraweeHierarchy, mDraweeHolder.getHierarchy());
        DraweeController another = DraweeMocks.mockController();
        mDraweeHolder.setController(another);
        Assert.assertSame(mDraweeHierarchy, mDraweeHolder.getHierarchy());
        Assert.assertSame(another, mDraweeHolder.getController());
        Assert.assertNull(mController.getHierarchy());
        Assert.assertSame(mDraweeHierarchy, another.getHierarchy());
    }

    @Test
    public void testLifecycle() {
        mDraweeHolder.setController(mController);
        Assert.assertFalse(mDraweeHolder.isAttached());
        mDraweeHolder.onAttach();
        Assert.assertTrue(mDraweeHolder.isAttached());
        mDraweeHolder.onDetach();
        Assert.assertFalse(mDraweeHolder.isAttached());
        Mockito.verify(mController).onAttach();
        Mockito.verify(mController).onDetach();
    }

    @Test
    public void testSetControllerWhenAlreadyAttached() {
        mDraweeHolder.onAttach();
        mDraweeHolder.setController(mController);
        mDraweeHolder.onDetach();
        Mockito.verify(mController).onAttach();
        Mockito.verify(mController).onDetach();
    }

    @Test
    public void testSetNullController() {
        mDraweeHolder.setController(null);
        mDraweeHolder.onAttach();
        mDraweeHolder.onDetach();
        mDraweeHolder.onAttach();
    }

    @Test
    public void testSetNewControllerWithInvalidController() {
        final DraweeHierarchy draweeHierarchy2 = DraweeMocks.mockDraweeHierarchyOf(mTopLevelDrawable);
        final DraweeHolder draweeHolder2 = new DraweeHolder(draweeHierarchy2);
        mDraweeHolder.onAttach();
        mDraweeHolder.setController(mController);
        draweeHolder2.setController(mController);
        mDraweeHolder.setController(null);
        Mockito.verify(mController, Mockito.never()).onDetach();
        Assert.assertEquals(draweeHierarchy2, mController.getHierarchy());
    }

    @Test
    public void testSetNewHierarchyWithInvalidController() {
        final DraweeHierarchy draweeHierarchy2 = DraweeMocks.mockDraweeHierarchyOf(mTopLevelDrawable);
        final DraweeHolder draweeHolder2 = new DraweeHolder(draweeHierarchy2);
        mDraweeHolder.setController(mController);
        draweeHolder2.setController(mController);
        final DraweeHierarchy draweeHierarchy3 = DraweeMocks.mockDraweeHierarchyOf(mTopLevelDrawable);
        mDraweeHolder.setHierarchy(draweeHierarchy3);
        Assert.assertEquals(draweeHierarchy2, mController.getHierarchy());
    }

    @Test
    public void testOnDetachWithInvalidController() {
        final DraweeHierarchy draweeHierarchy2 = DraweeMocks.mockDraweeHierarchyOf(mTopLevelDrawable);
        final DraweeHolder draweeHolder2 = new DraweeHolder(draweeHierarchy2);
        mDraweeHolder.onAttach();
        mDraweeHolder.setController(mController);
        draweeHolder2.setController(mController);
        mDraweeHolder.onDetach();
        Mockito.verify(mController, Mockito.never()).onDetach();
    }

    @Test
    public void testTouchEventWithInvalidController() {
        final DraweeHierarchy draweeHierarchy2 = DraweeMocks.mockDraweeHierarchyOf(mTopLevelDrawable);
        final DraweeHolder draweeHolder2 = new DraweeHolder(draweeHierarchy2);
        mDraweeHolder.setController(mController);
        draweeHolder2.setController(mController);
        mDraweeHolder.onTouchEvent(Mockito.mock(MotionEvent.class));
        Mockito.verify(mController, Mockito.never()).onTouchEvent(ArgumentMatchers.any(MotionEvent.class));
    }

    /**
     * There are 8 possible state transitions with two variables
     * 1. (visible, unattached)   -> (visible, attached)
     * 2. (visible, attached)     -> (invisible, attached)
     * 3. (invisible, attached)   -> (invisible, unattached)
     * 4. (invisible, unattached) -> (visible, unattached)
     * 5. (visible, unattached)   -> (invisible, unattached)
     * 6. (invisible, unattached) -> (invisible, attached)
     * 7. (invisible, attached)   -> (visible, attached)
     * 8. (visible, attached)     -> (visible, unattached)
     */
    @Test
    public void testVisibilityStateTransitions() {
        boolean restart = true;
        // Initial state (mIsVisible, !mIsHolderAttached)
        mDraweeHolder.setController(mController);
        verifyControllerLifecycleCalls(0, 0);
        /**
         * 1
         */
        mDraweeHolder.onAttach();
        verifyControllerLifecycleCalls(1, 0);
        /**
         * 2
         */
        mTopLevelDrawable.setVisible(false, restart);
        verifyControllerLifecycleCalls(0, 1);
        /**
         * 3
         */
        mDraweeHolder.onDetach();
        verifyControllerLifecycleCalls(0, 0);
        /**
         * 4
         */
        mTopLevelDrawable.setVisible(true, restart);
        verifyControllerLifecycleCalls(0, 0);
        /**
         * 5
         */
        mTopLevelDrawable.setVisible(false, restart);
        verifyControllerLifecycleCalls(0, 0);
        /**
         * 6
         */
        mDraweeHolder.onAttach();
        verifyControllerLifecycleCalls(0, 0);
        /**
         * 7
         */
        mTopLevelDrawable.setVisible(true, restart);
        verifyControllerLifecycleCalls(1, 0);
        /**
         * 8
         */
        mDraweeHolder.onDetach();
        verifyControllerLifecycleCalls(0, 1);
    }
}

