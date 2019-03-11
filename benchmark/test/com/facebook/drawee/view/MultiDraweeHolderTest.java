/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.drawee.view;


import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests for {@link MultiDraweeHolder}
 */
@RunWith(RobolectricTestRunner.class)
public class MultiDraweeHolderTest {
    MultiDraweeHolder mMultiHolder;

    DraweeHolder mHolder1;

    DraweeHolder mHolder2;

    DraweeHolder mHolder3;

    @Test
    public void testAttaching() {
        mMultiHolder.onAttach();
        Mockito.verify(mHolder1).onAttach();
        Mockito.verify(mHolder2).onAttach();
        Mockito.verify(mHolder3).onAttach();
        mMultiHolder.onDetach();
        Mockito.verify(mHolder1).onDetach();
        Mockito.verify(mHolder2).onDetach();
        Mockito.verify(mHolder3).onDetach();
    }

    @Test
    public void testTouchEvent_Handled() {
        MotionEvent event = Mockito.mock(MotionEvent.class);
        Mockito.when(mHolder1.onTouchEvent(event)).thenReturn(false);
        Mockito.when(mHolder2.onTouchEvent(event)).thenReturn(true);
        Mockito.when(mHolder3.onTouchEvent(event)).thenReturn(true);
        boolean ret = mMultiHolder.onTouchEvent(event);
        Assert.assertEquals(true, ret);
        Mockito.verify(mHolder1).onTouchEvent(event);
        Mockito.verify(mHolder2).onTouchEvent(event);
        Mockito.verify(mHolder3, Mockito.never()).onTouchEvent(event);
    }

    @Test
    public void testTouchEvent_NotHandled() {
        MotionEvent event = Mockito.mock(MotionEvent.class);
        Mockito.when(mHolder1.onTouchEvent(event)).thenReturn(false);
        Mockito.when(mHolder2.onTouchEvent(event)).thenReturn(false);
        Mockito.when(mHolder3.onTouchEvent(event)).thenReturn(false);
        boolean ret = mMultiHolder.onTouchEvent(event);
        Assert.assertEquals(false, ret);
        Mockito.verify(mHolder1).onTouchEvent(event);
        Mockito.verify(mHolder2).onTouchEvent(event);
        Mockito.verify(mHolder3).onTouchEvent(event);
    }

    @Test
    public void testClear_Detached() {
        Assert.assertEquals(3, mMultiHolder.mHolders.size());
        mMultiHolder.clear();
        Assert.assertTrue(mMultiHolder.mHolders.isEmpty());
    }

    @Test
    public void testClear_Attached() {
        mMultiHolder.onAttach();
        Mockito.reset(mHolder1, mHolder2, mHolder3);
        Assert.assertEquals(3, mMultiHolder.mHolders.size());
        mMultiHolder.clear();
        Assert.assertTrue(mMultiHolder.mHolders.isEmpty());
        Mockito.verify(mHolder1).onDetach();
        Mockito.verify(mHolder2).onDetach();
        Mockito.verify(mHolder2).onDetach();
    }

    @Test
    public void testAdd_Detached() {
        mMultiHolder.clear();
        Mockito.reset(mHolder1, mHolder2, mHolder3);
        Assert.assertEquals(0, mMultiHolder.mHolders.size());
        mMultiHolder.add(mHolder1);
        Assert.assertEquals(1, mMultiHolder.mHolders.size());
        Assert.assertEquals(mHolder1, mMultiHolder.mHolders.get(0));
        mMultiHolder.add(1, mHolder3);
        Assert.assertEquals(2, mMultiHolder.mHolders.size());
        Assert.assertEquals(mHolder1, mMultiHolder.mHolders.get(0));
        Assert.assertEquals(mHolder3, mMultiHolder.mHolders.get(1));
        mMultiHolder.add(1, mHolder2);
        Assert.assertEquals(3, mMultiHolder.mHolders.size());
        Assert.assertEquals(mHolder1, mMultiHolder.mHolders.get(0));
        Assert.assertEquals(mHolder2, mMultiHolder.mHolders.get(1));
        Assert.assertEquals(mHolder3, mMultiHolder.mHolders.get(2));
        Mockito.verify(mHolder1, Mockito.never()).onAttach();
        Mockito.verify(mHolder2, Mockito.never()).onAttach();
        Mockito.verify(mHolder3, Mockito.never()).onAttach();
    }

    @Test
    public void testAdd_Attached() {
        mMultiHolder.clear();
        mMultiHolder.onAttach();
        Mockito.reset(mHolder1, mHolder2, mHolder3);
        Assert.assertEquals(0, mMultiHolder.mHolders.size());
        mMultiHolder.add(mHolder1);
        Assert.assertEquals(mHolder1, mMultiHolder.mHolders.get(0));
        mMultiHolder.add(1, mHolder3);
        Assert.assertEquals(mHolder1, mMultiHolder.mHolders.get(0));
        Assert.assertEquals(mHolder3, mMultiHolder.mHolders.get(1));
        mMultiHolder.add(1, mHolder2);
        Assert.assertEquals(3, mMultiHolder.mHolders.size());
        Assert.assertEquals(mHolder1, mMultiHolder.mHolders.get(0));
        Assert.assertEquals(mHolder2, mMultiHolder.mHolders.get(1));
        Assert.assertEquals(mHolder3, mMultiHolder.mHolders.get(2));
        Mockito.verify(mHolder1).onAttach();
        Mockito.verify(mHolder2).onAttach();
        Mockito.verify(mHolder3).onAttach();
    }

    @Test
    public void testRemove_Detached() {
        Assert.assertEquals(3, mMultiHolder.mHolders.size());
        Assert.assertEquals(mHolder1, mMultiHolder.mHolders.get(0));
        Assert.assertEquals(mHolder2, mMultiHolder.mHolders.get(1));
        Assert.assertEquals(mHolder3, mMultiHolder.mHolders.get(2));
        mMultiHolder.remove(1);
        Assert.assertEquals(2, mMultiHolder.mHolders.size());
        Assert.assertEquals(mHolder1, mMultiHolder.mHolders.get(0));
        Assert.assertEquals(mHolder3, mMultiHolder.mHolders.get(1));
        mMultiHolder.remove(1);
        Assert.assertEquals(1, mMultiHolder.mHolders.size());
        Assert.assertEquals(mHolder1, mMultiHolder.mHolders.get(0));
        mMultiHolder.remove(0);
        Assert.assertEquals(0, mMultiHolder.mHolders.size());
    }

    @Test
    public void testRemove_attached() {
        mMultiHolder.onAttach();
        Mockito.reset(mHolder1, mHolder2, mHolder3);
        Assert.assertEquals(3, mMultiHolder.mHolders.size());
        Assert.assertEquals(mHolder1, mMultiHolder.mHolders.get(0));
        Assert.assertEquals(mHolder2, mMultiHolder.mHolders.get(1));
        Assert.assertEquals(mHolder3, mMultiHolder.mHolders.get(2));
        mMultiHolder.remove(1);
        Assert.assertEquals(2, mMultiHolder.mHolders.size());
        Assert.assertEquals(mHolder1, mMultiHolder.mHolders.get(0));
        Assert.assertEquals(mHolder3, mMultiHolder.mHolders.get(1));
        mMultiHolder.remove(1);
        Assert.assertEquals(1, mMultiHolder.mHolders.size());
        Assert.assertEquals(mHolder1, mMultiHolder.mHolders.get(0));
        mMultiHolder.remove(0);
        Assert.assertEquals(0, mMultiHolder.mHolders.size());
        Mockito.verify(mHolder1).onDetach();
        Mockito.verify(mHolder2).onDetach();
        Mockito.verify(mHolder3).onDetach();
    }

    @Test
    public void testGet() {
        Assert.assertEquals(mHolder1, mMultiHolder.get(0));
        Assert.assertEquals(mHolder2, mMultiHolder.get(1));
        Assert.assertEquals(mHolder3, mMultiHolder.get(2));
    }

    @Test
    public void testDraw() {
        Canvas canvas = Mockito.mock(Canvas.class);
        Drawable drawable1 = Mockito.mock(Drawable.class);
        Drawable drawable2 = Mockito.mock(Drawable.class);
        Drawable drawable3 = Mockito.mock(Drawable.class);
        Mockito.when(mHolder1.getTopLevelDrawable()).thenReturn(drawable1);
        Mockito.when(mHolder2.getTopLevelDrawable()).thenReturn(drawable2);
        Mockito.when(mHolder3.getTopLevelDrawable()).thenReturn(drawable3);
        mMultiHolder.draw(canvas);
        Mockito.verify(drawable1).draw(canvas);
        Mockito.verify(drawable2).draw(canvas);
        Mockito.verify(drawable3).draw(canvas);
    }

    @Test
    public void testVerifyDrawable() {
        Drawable drawable1 = Mockito.mock(Drawable.class);
        Drawable drawable2 = Mockito.mock(Drawable.class);
        Mockito.when(mHolder1.getTopLevelDrawable()).thenReturn(drawable1);
        Assert.assertTrue(mMultiHolder.verifyDrawable(drawable1));
        Assert.assertFalse(mMultiHolder.verifyDrawable(drawable2));
    }
}

