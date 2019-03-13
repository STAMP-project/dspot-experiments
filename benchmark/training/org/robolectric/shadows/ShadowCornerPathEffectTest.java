package org.robolectric.shadows;


import android.graphics.CornerPathEffect;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;


@RunWith(AndroidJUnit4.class)
public class ShadowCornerPathEffectTest {
    @Test
    public void shouldGetRadius() throws Exception {
        CornerPathEffect cornerPathEffect = new CornerPathEffect(4.0F);
        assertThat(Shadows.shadowOf(cornerPathEffect).getRadius()).isEqualTo(4.0F);
    }
}

