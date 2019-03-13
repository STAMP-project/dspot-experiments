package org.robolectric.shadows;


import android.graphics.drawable.GradientDrawable;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;


@RunWith(AndroidJUnit4.class)
public class ShadowGradientDrawableTest {
    @Test
    public void testGetColor_returnsColor() throws Exception {
        GradientDrawable gradientDrawable = new GradientDrawable();
        ShadowGradientDrawable shadowGradientDrawable = Shadows.shadowOf(gradientDrawable);
        int color = 123;
        gradientDrawable.setColor(color);
        assertThat(shadowGradientDrawable.getLastSetColor()).isEqualTo(color);
    }
}

