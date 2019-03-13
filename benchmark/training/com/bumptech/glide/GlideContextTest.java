package com.bumptech.glide;


import GlideContext.DEFAULT_TRANSITION_OPTIONS;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public final class GlideContextTest {
    private Map<Class<?>, TransitionOptions<?, ?>> transitionOptions;

    private GlideContext context;

    @Test
    public void getDefaultTransitionOptions_withNoOptionsRegistered_returnsDefaultOptions() {
        assertThat(context.getDefaultTransitionOptions(Object.class)).isEqualTo(DEFAULT_TRANSITION_OPTIONS);
    }

    @Test
    public void getDefaultTransitionOptions_withNonMatchingOptionRegistered_returnsDefaultOptions() {
        transitionOptions.put(Bitmap.class, new GenericTransitionOptions());
        assertThat(context.getDefaultTransitionOptions(Drawable.class)).isEqualTo(DEFAULT_TRANSITION_OPTIONS);
    }

    @Test
    public void getDefaultTransitionOptions_withMatchingOptionsRegistered_returnsMatchingOptions() {
        GenericTransitionOptions<Object> expected = new GenericTransitionOptions();
        transitionOptions.put(Bitmap.class, expected);
        assertThat(context.getDefaultTransitionOptions(Bitmap.class)).isEqualTo(expected);
    }

    @Test
    public void getDefaultTransitionOptions_withSuperClassRegistered_returnsSuperClassOptions() {
        DrawableTransitionOptions expected = new DrawableTransitionOptions();
        transitionOptions.put(Drawable.class, expected);
        assertThat(context.getDefaultTransitionOptions(BitmapDrawable.class)).isEqualTo(expected);
        assertThat(context.getDefaultTransitionOptions(GifDrawable.class)).isEqualTo(expected);
    }
}

