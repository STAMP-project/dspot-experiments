package org.robolectric.shadows;


import GestureDetector.OnDoubleTapListener;
import GestureDetector.OnGestureListener;
import android.app.Application;
import android.view.GestureDetector;
import android.view.MotionEvent;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;


@RunWith(AndroidJUnit4.class)
public class ShadowGestureDetectorTest {
    private GestureDetector detector;

    private MotionEvent motionEvent;

    private Application context;

    @Test
    public void test_getOnTouchEventMotionEvent() throws Exception {
        detector.onTouchEvent(motionEvent);
        Assert.assertSame(motionEvent, Shadows.shadowOf(detector).getOnTouchEventMotionEvent());
    }

    @Test
    public void test_reset() throws Exception {
        detector.onTouchEvent(motionEvent);
        Assert.assertSame(motionEvent, Shadows.shadowOf(detector).getOnTouchEventMotionEvent());
        Shadows.shadowOf(detector).reset();
        Assert.assertNull(Shadows.shadowOf(detector).getOnTouchEventMotionEvent());
    }

    @Test
    public void test_getListener() throws Exception {
        ShadowGestureDetectorTest.TestOnGestureListener listener = new ShadowGestureDetectorTest.TestOnGestureListener();
        Assert.assertSame(listener, Shadows.shadowOf(new GestureDetector(listener)).getListener());
        Assert.assertSame(listener, Shadows.shadowOf(new GestureDetector(null, listener)).getListener());
    }

    @Test
    public void canAnswerLastGestureDetector() throws Exception {
        GestureDetector newDetector = new GestureDetector(context, new ShadowGestureDetectorTest.TestOnGestureListener());
        Assert.assertNotSame(newDetector, ShadowGestureDetector.getLastActiveDetector());
        newDetector.onTouchEvent(motionEvent);
        Assert.assertSame(newDetector, ShadowGestureDetector.getLastActiveDetector());
    }

    @Test
    public void getOnDoubleTapListener_shouldReturnSetDoubleTapListener() throws Exception {
        GestureDetector subject = new GestureDetector(context, new ShadowGestureDetectorTest.TestOnGestureListener());
        GestureDetector.OnDoubleTapListener onDoubleTapListener = new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return false;
            }
        };
        subject.setOnDoubleTapListener(onDoubleTapListener);
        Assert.assertEquals(Shadows.shadowOf(subject).getOnDoubleTapListener(), onDoubleTapListener);
        subject.setOnDoubleTapListener(null);
        Assert.assertNull(Shadows.shadowOf(subject).getOnDoubleTapListener());
    }

    @Test
    public void getOnDoubleTapListener_shouldReturnOnGestureListenerFromConstructor() throws Exception {
        GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener();
        GestureDetector subject = new GestureDetector(context, onGestureListener);
        Assert.assertEquals(Shadows.shadowOf(subject).getOnDoubleTapListener(), onGestureListener);
    }

    private static class TestOnGestureListener implements GestureDetector.OnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }
}

