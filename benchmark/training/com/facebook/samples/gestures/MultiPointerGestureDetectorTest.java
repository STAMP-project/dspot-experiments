/**
 * This file provided by Facebook is for non-commercial testing and evaluation
 * purposes only.  Facebook reserves all rights not expressly granted.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * FACEBOOK BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.facebook.samples.gestures;


import MotionEvent.ACTION_DOWN;
import MotionEvent.ACTION_MOVE;
import MotionEvent.ACTION_POINTER_DOWN;
import MotionEvent.ACTION_POINTER_UP;
import MotionEvent.ACTION_UP;
import MultiPointerGestureDetector.Listener;
import android.view.MotionEvent;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import robolectric3.shadows.ShadowMotionEvent;


/**
 * Tests for {@link MultiPointerGestureDetector}
 */
@Config(shadows = { ShadowMotionEvent.class })
@RunWith(RobolectricTestRunner.class)
public class MultiPointerGestureDetectorTest {
    private Listener mListener;

    private MultiPointerGestureDetector mGestureDetector;

    @Test
    public void testInitialstate() {
        Assert.assertEquals(false, mGestureDetector.isGestureInProgress());
    }

    @Test
    public void testSinglePointer() {
        MotionEvent event1 = MotionEventTestUtils.obtainMotionEvent(1000, 1000, ACTION_DOWN, 0, 100.0F, 300.0F);
        MotionEvent event2 = MotionEventTestUtils.obtainMotionEvent(1000, 1010, ACTION_MOVE, 0, 150.0F, 350.0F);
        MotionEvent event3 = MotionEventTestUtils.obtainMotionEvent(1000, 1020, ACTION_MOVE, 0, 200.0F, 400.0F);
        MotionEvent event4 = MotionEventTestUtils.obtainMotionEvent(1000, 1030, ACTION_UP, 0, 200.0F, 400.0F);
        InOrder inOrder = Mockito.inOrder(mListener);
        mGestureDetector.onTouchEvent(event1);
        mGestureDetector.onTouchEvent(event2);
        Assert.assertTrue(mGestureDetector.isGestureInProgress());
        Assert.assertEquals(1, mGestureDetector.getPointerCount());
        Assert.assertEquals(100.0F, mGestureDetector.getStartX()[0], 0);
        Assert.assertEquals(300.0F, mGestureDetector.getStartY()[0], 0);
        Assert.assertEquals(150.0F, mGestureDetector.getCurrentX()[0], 0);
        Assert.assertEquals(350.0F, mGestureDetector.getCurrentY()[0], 0);
        inOrder.verify(mListener).onGestureBegin(mGestureDetector);
        inOrder.verify(mListener).onGestureUpdate(mGestureDetector);
        mGestureDetector.onTouchEvent(event3);
        Assert.assertTrue(mGestureDetector.isGestureInProgress());
        Assert.assertEquals(1, mGestureDetector.getPointerCount());
        Assert.assertEquals(100.0F, mGestureDetector.getStartX()[0], 0);
        Assert.assertEquals(300.0F, mGestureDetector.getStartY()[0], 0);
        Assert.assertEquals(200.0F, mGestureDetector.getCurrentX()[0], 0);
        Assert.assertEquals(400.0F, mGestureDetector.getCurrentY()[0], 0);
        inOrder.verify(mListener).onGestureUpdate(mGestureDetector);
        mGestureDetector.onTouchEvent(event4);
        Assert.assertFalse(mGestureDetector.isGestureInProgress());
        Assert.assertEquals(0, mGestureDetector.getPointerCount());
        inOrder.verify(mListener).onGestureEnd(mGestureDetector);
        inOrder.verifyNoMoreInteractions();
        event1.recycle();
        event2.recycle();
        event3.recycle();
        event4.recycle();
    }

    @Test
    public void testTwoPointers() {
        MotionEvent event1 = MotionEventTestUtils.obtainMotionEvent(100, 100, ACTION_DOWN, 0, 100.0F, 300.0F);
        MotionEvent event2 = MotionEventTestUtils.obtainMotionEvent(100, 110, ACTION_MOVE, 0, 150.0F, 350.0F);
        MotionEvent event3 = MotionEventTestUtils.obtainMotionEvent(100, 120, ACTION_POINTER_DOWN, 0, 150.0F, 350.0F, 1, 500.0F, 600.0F);
        MotionEvent event4 = MotionEventTestUtils.obtainMotionEvent(100, 130, ACTION_MOVE, 0, 200.0F, 400.0F, 1, 550.0F, 650.0F);
        MotionEvent event5 = MotionEventTestUtils.obtainMotionEvent(100, 140, ACTION_POINTER_UP, 0, 200.0F, 400.0F, 1, 550.0F, 650.0F);
        MotionEvent event6 = MotionEventTestUtils.obtainMotionEvent(100, 150, ACTION_MOVE, 1, 600.0F, 700.0F);
        MotionEvent event7 = MotionEventTestUtils.obtainMotionEvent(100, 160, ACTION_UP, 1, 600.0F, 700.0F);
        InOrder inOrder = Mockito.inOrder(mListener);
        mGestureDetector.onTouchEvent(event1);
        mGestureDetector.onTouchEvent(event2);
        Assert.assertTrue(mGestureDetector.isGestureInProgress());
        Assert.assertEquals(1, mGestureDetector.getPointerCount());
        Assert.assertEquals(100.0F, mGestureDetector.getStartX()[0], 0);
        Assert.assertEquals(300.0F, mGestureDetector.getStartY()[0], 0);
        Assert.assertEquals(150.0F, mGestureDetector.getCurrentX()[0], 0);
        Assert.assertEquals(350.0F, mGestureDetector.getCurrentY()[0], 0);
        inOrder.verify(mListener).onGestureBegin(mGestureDetector);
        inOrder.verify(mListener).onGestureUpdate(mGestureDetector);
        mGestureDetector.onTouchEvent(event3);
        Assert.assertTrue(mGestureDetector.isGestureInProgress());
        Assert.assertEquals(2, mGestureDetector.getPointerCount());
        Assert.assertEquals(150.0F, mGestureDetector.getStartX()[0], 0);
        Assert.assertEquals(350.0F, mGestureDetector.getStartY()[0], 0);
        Assert.assertEquals(150.0F, mGestureDetector.getCurrentX()[0], 0);
        Assert.assertEquals(350.0F, mGestureDetector.getCurrentY()[0], 0);
        Assert.assertEquals(500.0F, mGestureDetector.getStartX()[1], 0);
        Assert.assertEquals(600.0F, mGestureDetector.getStartY()[1], 0);
        Assert.assertEquals(500.0F, mGestureDetector.getCurrentX()[1], 0);
        Assert.assertEquals(600.0F, mGestureDetector.getCurrentY()[1], 0);
        inOrder.verify(mListener).onGestureEnd(mGestureDetector);
        inOrder.verify(mListener).onGestureBegin(mGestureDetector);
        mGestureDetector.onTouchEvent(event4);
        Assert.assertTrue(mGestureDetector.isGestureInProgress());
        Assert.assertEquals(2, mGestureDetector.getPointerCount());
        Assert.assertEquals(150.0F, mGestureDetector.getStartX()[0], 0);
        Assert.assertEquals(350.0F, mGestureDetector.getStartY()[0], 0);
        Assert.assertEquals(200.0F, mGestureDetector.getCurrentX()[0], 0);
        Assert.assertEquals(400.0F, mGestureDetector.getCurrentY()[0], 0);
        Assert.assertEquals(500.0F, mGestureDetector.getStartX()[1], 0);
        Assert.assertEquals(600.0F, mGestureDetector.getStartY()[1], 0);
        Assert.assertEquals(550.0F, mGestureDetector.getCurrentX()[1], 0);
        Assert.assertEquals(650.0F, mGestureDetector.getCurrentY()[1], 0);
        inOrder.verify(mListener).onGestureUpdate(mGestureDetector);
        mGestureDetector.onTouchEvent(event5);
        Assert.assertTrue(mGestureDetector.isGestureInProgress());
        Assert.assertEquals(1, mGestureDetector.getPointerCount());
        Assert.assertEquals(550.0F, mGestureDetector.getStartX()[0], 0);
        Assert.assertEquals(650.0F, mGestureDetector.getStartY()[0], 0);
        Assert.assertEquals(550.0F, mGestureDetector.getCurrentX()[0], 0);
        Assert.assertEquals(650.0F, mGestureDetector.getCurrentY()[0], 0);
        inOrder.verify(mListener).onGestureEnd(mGestureDetector);
        inOrder.verify(mListener).onGestureBegin(mGestureDetector);
        mGestureDetector.onTouchEvent(event6);
        Assert.assertTrue(mGestureDetector.isGestureInProgress());
        Assert.assertEquals(1, mGestureDetector.getPointerCount());
        Assert.assertEquals(550.0F, mGestureDetector.getStartX()[0], 0);
        Assert.assertEquals(650.0F, mGestureDetector.getStartY()[0], 0);
        Assert.assertEquals(600.0F, mGestureDetector.getCurrentX()[0], 0);
        Assert.assertEquals(700.0F, mGestureDetector.getCurrentY()[0], 0);
        inOrder.verify(mListener).onGestureUpdate(mGestureDetector);
        mGestureDetector.onTouchEvent(event7);
        Assert.assertFalse(mGestureDetector.isGestureInProgress());
        Assert.assertEquals(0, mGestureDetector.getPointerCount());
        inOrder.verify(mListener).onGestureEnd(mGestureDetector);
        inOrder.verifyNoMoreInteractions();
        event1.recycle();
        event2.recycle();
        event3.recycle();
        event4.recycle();
    }
}

