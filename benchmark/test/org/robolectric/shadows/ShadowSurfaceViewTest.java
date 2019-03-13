package org.robolectric.shadows;


import ShadowSurfaceView.FakeSurfaceHolder;
import SurfaceHolder.Callback;
import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;


@RunWith(AndroidJUnit4.class)
public class ShadowSurfaceViewTest {
    private Callback callback1 = new ShadowSurfaceViewTest.TestCallback();

    private Callback callback2 = new ShadowSurfaceViewTest.TestCallback();

    private SurfaceView view = new SurfaceView(Robolectric.buildActivity(Activity.class).create().get());

    private SurfaceHolder surfaceHolder = view.getHolder();

    private ShadowSurfaceView shadowSurfaceView = ((ShadowSurfaceView) (Shadows.shadowOf(view)));

    private FakeSurfaceHolder fakeSurfaceHolder = shadowSurfaceView.getFakeSurfaceHolder();

    @Test
    public void addCallback() {
        assertThat(fakeSurfaceHolder.getCallbacks()).isEmpty();
        surfaceHolder.addCallback(callback1);
        assertThat(fakeSurfaceHolder.getCallbacks()).contains(callback1);
        surfaceHolder.addCallback(callback2);
        assertThat(fakeSurfaceHolder.getCallbacks()).contains(callback1);
        assertThat(fakeSurfaceHolder.getCallbacks()).contains(callback2);
    }

    @Test
    public void removeCallback() {
        surfaceHolder.addCallback(callback1);
        surfaceHolder.addCallback(callback2);
        assertThat(fakeSurfaceHolder.getCallbacks().size()).isEqualTo(2);
        surfaceHolder.removeCallback(callback1);
        assertThat(fakeSurfaceHolder.getCallbacks()).doesNotContain(callback1);
        assertThat(fakeSurfaceHolder.getCallbacks()).contains(callback2);
    }

    @Test
    public void canCreateASurfaceView_attachedToAWindowWithActionBar() throws Exception {
        ShadowSurfaceViewTest.TestActivity testActivity = Robolectric.buildActivity(ShadowSurfaceViewTest.TestActivity.class).create().start().resume().visible().get();
        assertThat(testActivity).isNotNull();
    }

    private static class TestCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        }
    }

    private static class TestActivity extends Activity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_ACTION_BAR);
            setContentView(new SurfaceView(this));
        }
    }
}

