package org.robolectric.android;


import android.R.drawable.ic_menu_help;
import android.R.drawable.ic_popup_sync;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build.VERSION_CODES;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.robolectric.R.animator.spinning;
import static org.robolectric.R.color.grey42;
import static org.robolectric.R.drawable.an_image_or_vector;
import static org.robolectric.R.drawable.l0_red;
import static org.robolectric.R.drawable.l7_white;
import static org.robolectric.R.drawable.nine_patch_drawable;
import static org.robolectric.R.drawable.rainbow;


@RunWith(AndroidJUnit4.class)
public class DrawableResourceLoaderTest {
    private Resources resources;

    @Test
    public void testGetDrawable_rainbow() throws Exception {
        Assert.assertNotNull(ApplicationProvider.getApplicationContext().getResources().getDrawable(rainbow));
    }

    @Test
    public void testGetDrawableBundle_shouldWorkWithSystem() throws Exception {
        Assert.assertNotNull(resources.getDrawable(ic_popup_sync));
    }

    @Test
    public void testGetDrawable_red() throws Exception {
        Assert.assertNotNull(Resources.getSystem().getDrawable(ic_menu_help));
    }

    @Test
    public void testDrawableTypes() {
        assertThat(resources.getDrawable(l7_white)).isInstanceOf(BitmapDrawable.class);
        assertThat(resources.getDrawable(l0_red)).isInstanceOf(BitmapDrawable.class);
        assertThat(resources.getDrawable(nine_patch_drawable)).isInstanceOf(NinePatchDrawable.class);
        assertThat(resources.getDrawable(rainbow)).isInstanceOf(LayerDrawable.class);
    }

    @Test
    @Config(maxSdk = VERSION_CODES.KITKAT_WATCH)
    public void testVectorDrawableType_preVectors() {
        assertThat(resources.getDrawable(an_image_or_vector)).isInstanceOf(BitmapDrawable.class);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void testVectorDrawableType() {
        assertThat(resources.getDrawable(an_image_or_vector)).isInstanceOf(VectorDrawable.class);
    }

    @Test
    @Config(qualifiers = "land")
    public void testLayerDrawable_xlarge() {
        Assert.assertEquals(6, getNumberOfLayers());
    }

    @Test
    public void testLayerDrawable() {
        Assert.assertEquals(8, getNumberOfLayers());
    }

    @Test
    public void shouldCreateAnimators() throws Exception {
        Animator animator = AnimatorInflater.loadAnimator(RuntimeEnvironment.application, spinning);
        assertThat(animator).isInstanceOf(((Class<? extends Animator>) (Animator.class)));
    }

    @Test
    public void shouldCreateAnimsAndColors() throws Exception {
        assertThat(resources.getDrawable(grey42)).isInstanceOf(((Class<? extends Drawable>) (ColorDrawable.class)));
    }
}

