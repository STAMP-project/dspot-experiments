package org.robolectric.shadows;


import android.graphics.SurfaceTexture;
import android.view.Surface;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;


@RunWith(AndroidJUnit4.class)
public class ShadowSurfaceTest {
    private final SurfaceTexture texture = new SurfaceTexture(0);

    private final Surface surface = new Surface(texture);

    @Test
    public void getSurfaceTexture_returnsSurfaceTexture() throws Exception {
        assertThat(Shadows.shadowOf(surface).getSurfaceTexture()).isEqualTo(texture);
    }

    @Test
    public void release_doesNotThrow() throws Exception {
        surface.release();
    }

    @Test
    public void toString_returnsNotEmptyString() throws Exception {
        assertThat(surface.toString()).isNotEmpty();
    }
}

