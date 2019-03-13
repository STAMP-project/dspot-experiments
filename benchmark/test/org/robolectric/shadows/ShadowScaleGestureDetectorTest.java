package org.robolectric.shadows;


import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;


@RunWith(AndroidJUnit4.class)
public class ShadowScaleGestureDetectorTest {
    private ScaleGestureDetector detector;

    private MotionEvent motionEvent;

    @Test
    public void test_getOnTouchEventMotionEvent() throws Exception {
        detector.onTouchEvent(motionEvent);
        Assert.assertSame(motionEvent, Shadows.shadowOf(detector).getOnTouchEventMotionEvent());
    }

    @Test
    public void test_getScaleFactor() throws Exception {
        Shadows.shadowOf(detector).setScaleFactor(2.0F);
        assertThat(2.0F).isEqualTo(detector.getScaleFactor());
    }

    @Test
    public void test_getFocusXY() throws Exception {
        Shadows.shadowOf(detector).setFocusXY(2.0F, 3.0F);
        assertThat(2.0F).isEqualTo(detector.getFocusX());
        assertThat(3.0F).isEqualTo(detector.getFocusY());
    }

    @Test
    public void test_getListener() throws Exception {
        ShadowScaleGestureDetectorTest.TestOnGestureListener listener = new ShadowScaleGestureDetectorTest.TestOnGestureListener();
        Assert.assertSame(listener, Shadows.shadowOf(new ScaleGestureDetector(ApplicationProvider.getApplicationContext(), listener)).getListener());
    }

    @Test
    public void test_reset() throws Exception {
        assertDefaults();
        detector.onTouchEvent(motionEvent);
        Shadows.shadowOf(detector).setFocusXY(3.0F, 3.0F);
        Shadows.shadowOf(detector).setScaleFactor(4.0F);
        Assert.assertSame(motionEvent, Shadows.shadowOf(detector).getOnTouchEventMotionEvent());
        Shadows.shadowOf(detector).reset();
        assertDefaults();
    }

    private static class TestOnGestureListener implements ScaleGestureDetector.OnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return false;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
        }
    }
}

