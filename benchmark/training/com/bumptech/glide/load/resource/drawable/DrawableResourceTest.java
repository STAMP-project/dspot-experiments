package com.bumptech.glide.load.resource.drawable;


import Drawable.ConstantState;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class DrawableResourceTest {
    private DrawableResourceTest.TestDrawable drawable;

    private DrawableResource<DrawableResourceTest.TestDrawable> resource;

    @Test
    public void testDoesNotReturnOriginalDrawableOnGet() {
        Mockito.when(getConstantState()).thenReturn(Mockito.mock(ConstantState.class));
        Assert.assertNotEquals(drawable, resource.get());
    }

    @Test
    public void testReturnsNewDrawableOnGet() {
        GifDrawable expected = Mockito.mock(GifDrawable.class);
        Drawable.ConstantState constantState = Mockito.mock(ConstantState.class);
        Mockito.when(constantState.newDrawable()).thenReturn(expected);
        Mockito.when(getConstantState()).thenReturn(constantState);
        assertThat(resource.get()).isEqualTo(expected);
        getConstantState();
        Mockito.verify(constantState).newDrawable();
    }

    @Test
    public void get_withNullState_returnsOriginalDrawable() {
        Mockito.when(getConstantState()).thenReturn(null);
        assertThat(resource.get()).isEqualTo(drawable);
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsIfDrawableIsNull() {
        new DrawableResource<DrawableResourceTest.TestDrawable>(null) {
            @NonNull
            @Override
            public Class<DrawableResourceTest.TestDrawable> getResourceClass() {
                return DrawableResourceTest.TestDrawable.class;
            }

            @Override
            public int getSize() {
                return 0;
            }

            @Override
            public void recycle() {
            }
        };
    }

    /**
     * Just to have a type to test with which is not directly Drawable
     */
    private static class TestDrawable extends Drawable {
        @Override
        public void draw(@NonNull
        Canvas canvas) {
        }

        @Override
        public void setAlpha(int alpha) {
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
        }

        @Override
        public int getOpacity() {
            return 0;
        }
    }
}

