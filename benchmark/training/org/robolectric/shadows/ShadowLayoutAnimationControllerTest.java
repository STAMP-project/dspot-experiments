package org.robolectric.shadows;


import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ShadowLayoutAnimationControllerTest {
    private ShadowLayoutAnimationController shadow;

    @Test
    public void testResourceId() {
        int id = 1;
        shadow.setLoadedFromResourceId(1);
        assertThat(shadow.getLoadedFromResourceId()).isEqualTo(id);
    }
}

