package org.robolectric.shadows;


import View.MeasureSpec;
import View.MeasureSpec.AT_MOST;
import android.widget.FrameLayout;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ShadowFrameLayoutTest {
    private FrameLayout frameLayout;

    @Test
    public void testNotNull() {
        Assert.assertNotNull(frameLayout);
    }

    @Test
    public void onMeasure_shouldNotLayout() throws Exception {
        assertThat(frameLayout.getHeight()).isEqualTo(0);
        assertThat(frameLayout.getWidth()).isEqualTo(0);
        frameLayout.measure(MeasureSpec.makeMeasureSpec(150, AT_MOST), MeasureSpec.makeMeasureSpec(300, AT_MOST));
        assertThat(frameLayout.getHeight()).isEqualTo(0);
        assertThat(frameLayout.getWidth()).isEqualTo(0);
    }
}

