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


import TransformGestureDetector.Listener;
import android.view.MotionEvent;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests for {@link MultiPointerGestureDetector}
 */
@RunWith(RobolectricTestRunner.class)
public class TransformGestureDetectorTest {
    private Listener mListener;

    private MultiPointerGestureDetector mMultiPointerGestureDetector;

    private TransformGestureDetector mGestureDetector;

    @Test
    public void testInitialstate() {
        Assert.assertEquals(false, mGestureDetector.isGestureInProgress());
        Mockito.verify(mMultiPointerGestureDetector).setListener(mGestureDetector);
    }

    @Test
    public void testReset() {
        mGestureDetector.reset();
        Mockito.verify(mMultiPointerGestureDetector).reset();
    }

    @Test
    public void testOnTouchEvent() {
        MotionEvent motionEvent = Mockito.mock(MotionEvent.class);
        mGestureDetector.onTouchEvent(motionEvent);
        Mockito.verify(mMultiPointerGestureDetector).onTouchEvent(motionEvent);
    }

    @Test
    public void testOnGestureBegin() {
        mGestureDetector.onGestureBegin(mMultiPointerGestureDetector);
        Mockito.verify(mListener).onGestureBegin(mGestureDetector);
    }

    @Test
    public void testOnGestureUpdate() {
        mGestureDetector.onGestureUpdate(mMultiPointerGestureDetector);
        Mockito.verify(mListener).onGestureUpdate(mGestureDetector);
    }

    @Test
    public void testOnGestureEnd() {
        mGestureDetector.onGestureEnd(mMultiPointerGestureDetector);
        Mockito.verify(mListener).onGestureEnd(mGestureDetector);
    }

    @Test
    public void testIsGestureInProgress() {
        Mockito.when(mMultiPointerGestureDetector.isGestureInProgress()).thenReturn(true);
        Assert.assertEquals(true, mGestureDetector.isGestureInProgress());
        Mockito.verify(mMultiPointerGestureDetector, Mockito.times(1)).isGestureInProgress();
        Mockito.when(mMultiPointerGestureDetector.isGestureInProgress()).thenReturn(false);
        Assert.assertEquals(false, mGestureDetector.isGestureInProgress());
        Mockito.verify(mMultiPointerGestureDetector, Mockito.times(2)).isGestureInProgress();
    }

    @Test
    public void testPivot() {
        Mockito.when(mMultiPointerGestureDetector.getPointerCount()).thenReturn(0);
        Assert.assertEquals(0, mGestureDetector.getPivotX(), 0);
        Assert.assertEquals(0, mGestureDetector.getPivotY(), 0);
        Mockito.when(mMultiPointerGestureDetector.getPointerCount()).thenReturn(1);
        Assert.assertEquals(100, mGestureDetector.getPivotX(), 0);
        Assert.assertEquals(500, mGestureDetector.getPivotY(), 0);
        Mockito.when(mMultiPointerGestureDetector.getPointerCount()).thenReturn(2);
        Assert.assertEquals(150, mGestureDetector.getPivotX(), 0);
        Assert.assertEquals(550, mGestureDetector.getPivotY(), 0);
    }

    @Test
    public void testTranslation() {
        Mockito.when(mMultiPointerGestureDetector.getPointerCount()).thenReturn(0);
        Assert.assertEquals(0, mGestureDetector.getTranslationX(), 0);
        Assert.assertEquals(0, mGestureDetector.getTranslationY(), 0);
        Mockito.when(mMultiPointerGestureDetector.getPointerCount()).thenReturn(1);
        Assert.assertEquals((-90), mGestureDetector.getTranslationX(), 0);
        Assert.assertEquals((-450), mGestureDetector.getTranslationY(), 0);
        Mockito.when(mMultiPointerGestureDetector.getPointerCount()).thenReturn(2);
        Assert.assertEquals((-135), mGestureDetector.getTranslationX(), 0);
        Assert.assertEquals((-505), mGestureDetector.getTranslationY(), 0);
    }

    @Test
    public void testScale() {
        Mockito.when(mMultiPointerGestureDetector.getPointerCount()).thenReturn(0);
        Assert.assertEquals(1, mGestureDetector.getScale(), 0);
        Mockito.when(mMultiPointerGestureDetector.getPointerCount()).thenReturn(1);
        Assert.assertEquals(1, mGestureDetector.getScale(), 0);
        Mockito.when(mMultiPointerGestureDetector.getPointerCount()).thenReturn(2);
        Assert.assertEquals(0.1F, mGestureDetector.getScale(), 1.0E-6);
    }

    @Test
    public void testRotation() {
        Mockito.when(mMultiPointerGestureDetector.getPointerCount()).thenReturn(0);
        Assert.assertEquals(0, mGestureDetector.getRotation(), 0);
        Mockito.when(mMultiPointerGestureDetector.getPointerCount()).thenReturn(1);
        Assert.assertEquals(0, mGestureDetector.getRotation(), 0);
        Mockito.when(mMultiPointerGestureDetector.getPointerCount()).thenReturn(2);
        Assert.assertEquals((((float) (-(Math.PI))) / 2), mGestureDetector.getRotation(), 1.0E-6);
    }
}

