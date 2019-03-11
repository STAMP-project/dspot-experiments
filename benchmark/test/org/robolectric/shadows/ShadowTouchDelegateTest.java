package org.robolectric.shadows;


import android.graphics.Rect;
import android.view.View;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ShadowTouchDelegateTest {
    private ShadowTouchDelegate td;

    private Rect rect;

    private View view;

    @Test
    public void testBounds() {
        Rect bounds = td.getBounds();
        assertThat(bounds).isEqualTo(rect);
    }

    @Test
    public void tetsDelegateView() {
        View view = td.getDelegateView();
        assertThat(view).isEqualTo(this.view);
    }
}

