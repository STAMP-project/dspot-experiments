package org.robolectric.shadows;


import android.R.drawable.bottom_bar;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.Window;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.robolectric.Shadows.shadowOf;


@RunWith(AndroidJUnit4.class)
public class ShadowPhoneWindowTest {
    private Window window;

    private Activity activity;

    @Test
    public void getTitle() throws Exception {
        window.setTitle("Some title");
        assertThat(shadowOf(window).getTitle()).isEqualTo("Some title");
    }

    @Test
    public void getBackgroundDrawable() throws Exception {
        Drawable drawable = activity.getResources().getDrawable(bottom_bar);
        window.setBackgroundDrawable(drawable);
        assertThat(shadowOf(window).getBackgroundDrawable()).isSameAs(drawable);
    }
}

