package org.robolectric.shadows;


import Choreographer.FrameCallback;
import android.view.Choreographer;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.util.TimeUtils;


@RunWith(AndroidJUnit4.class)
public class ShadowChoreographerTest {
    @Test
    public void setFrameInterval_shouldUpdateFrameInterval() {
        final long frameInterval = 10 * (TimeUtils.NANOS_PER_MS);
        ShadowChoreographer.setFrameInterval(frameInterval);
        final Choreographer instance = ShadowChoreographer.getInstance();
        long time1 = instance.getFrameTimeNanos();
        long time2 = instance.getFrameTimeNanos();
        assertThat((time2 - time1)).isEqualTo(frameInterval);
    }

    @Test
    public void removeFrameCallback_shouldRemoveCallback() {
        Choreographer instance = ShadowChoreographer.getInstance();
        Choreographer.FrameCallback callback = Mockito.mock(FrameCallback.class);
        instance.postFrameCallbackDelayed(callback, 1000);
        instance.removeFrameCallback(callback);
        ShadowApplication.getInstance().getForegroundThreadScheduler().advanceToLastPostedRunnable();
        Mockito.verify(callback, Mockito.never()).doFrame(ArgumentMatchers.anyLong());
    }

    @Test
    public void reset_shouldResetFrameInterval() {
        ShadowChoreographer.setFrameInterval(1);
        assertThat(ShadowChoreographer.getFrameInterval()).isEqualTo(1);
        ShadowChoreographer.reset();
        assertThat(ShadowChoreographer.getFrameInterval()).isEqualTo((10 * (TimeUtils.NANOS_PER_MS)));
    }
}

