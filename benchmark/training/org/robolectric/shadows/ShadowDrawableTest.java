package org.robolectric.shadows;


import ShadowDrawable.corruptStreamSources;
import android.app.Application;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build.VERSION_CODES;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

import static org.robolectric.R.drawable.an_image;
import static org.robolectric.R.drawable.an_image_or_vector;
import static org.robolectric.R.drawable.drawable_with_nine_patch;
import static org.robolectric.R.drawable.robolectric;


@RunWith(AndroidJUnit4.class)
public class ShadowDrawableTest {
    private Application context;

    @Test
    public void createFromStream__shouldReturnNullWhenAskedToCreateADrawableFromACorruptedSourceStream() throws Exception {
        String corruptedStreamSource = "http://foo.com/image.jpg";
        ShadowDrawable.addCorruptStreamSource(corruptedStreamSource);
        Assert.assertNull(ShadowDrawable.createFromStream(new ByteArrayInputStream(new byte[0]), corruptedStreamSource));
    }

    @Test
    public void createFromResourceStream_shouldWorkWithoutSourceName() {
        Drawable drawable = Drawable.createFromResourceStream(context.getResources(), null, new ByteArrayInputStream(new byte[0]), null, new BitmapFactory.Options());
        Assert.assertNotNull(drawable);
    }

    @Test
    public void createFromStream__shouldReturnDrawableWithSpecificSource() throws Exception {
        Drawable drawable = ShadowDrawable.createFromStream(new ByteArrayInputStream(new byte[0]), "my_source");
        Assert.assertNotNull(drawable);
        Assert.assertEquals("my_source", getSource());
    }

    @Test
    public void reset__shouldClearStaticState() throws Exception {
        String src = "source1";
        ShadowDrawable.addCorruptStreamSource(src);
        Assert.assertTrue(corruptStreamSources.contains(src));
        ShadowDrawable.clearCorruptStreamSources();
        Assert.assertFalse(corruptStreamSources.contains(src));
    }

    @Test
    public void testCreateFromStream_shouldSetTheInputStreamOnTheReturnedDrawable() throws Exception {
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(new byte[0]);
        Drawable drawable = Drawable.createFromStream(byteInputStream, "src name");
        assertThat(Shadows.shadowOf(drawable).getInputStream()).isEqualTo(((InputStream) (byteInputStream)));
    }

    @Test
    public void copyBoundsWithPassedRect() {
        Drawable drawable = ShadowDrawable.createFromStream(new ByteArrayInputStream(new byte[0]), "my_source");
        drawable.setBounds(1, 2, 3, 4);
        Rect r = new Rect();
        drawable.copyBounds(r);
        assertThat(r.left).isEqualTo(1);
        assertThat(r.top).isEqualTo(2);
        assertThat(r.right).isEqualTo(3);
        assertThat(r.bottom).isEqualTo(4);
    }

    @Test
    public void copyBoundsToReturnedRect() {
        Drawable drawable = ShadowDrawable.createFromStream(new ByteArrayInputStream(new byte[0]), "my_source");
        drawable.setBounds(1, 2, 3, 4);
        Rect r = drawable.copyBounds();
        assertThat(r.left).isEqualTo(1);
        assertThat(r.top).isEqualTo(2);
        assertThat(r.right).isEqualTo(3);
        assertThat(r.bottom).isEqualTo(4);
    }

    @Test
    public void createFromPath__shouldReturnDrawableWithSpecificPath() throws Exception {
        Drawable drawable = ShadowDrawable.createFromPath("/foo");
        Assert.assertNotNull(drawable);
        Assert.assertEquals("/foo", getPath());
    }

    @Test
    public void testGetLoadedFromResourceId_shouldDefaultToNegativeOne() throws Exception {
        Drawable drawable = new ShadowDrawableTest.TestDrawable();
        assertThat(Shadows.shadowOf(drawable).getCreatedFromResId()).isEqualTo((-1));
    }

    @Test
    public void testCreateFromResourceId_shouldSetTheId() throws Exception {
        Drawable drawable = ShadowDrawable.createFromResourceId(34758);
        ShadowDrawable shadowDrawable = Shadows.shadowOf(drawable);
        assertThat(shadowDrawable.getCreatedFromResId()).isEqualTo(34758);
    }

    @Test
    public void testWasSelfInvalidated() throws Exception {
        Drawable drawable = ShadowDrawable.createFromResourceId(34758);
        ShadowDrawable shadowDrawable = Shadows.shadowOf(drawable);
        assertThat(shadowDrawable.wasInvalidated()).isFalse();
        drawable.invalidateSelf();
        assertThat(shadowDrawable.wasInvalidated()).isTrue();
    }

    @Test
    public void shouldLoadNinePatchFromDrawableXml() throws Exception {
        assertThat(context.getResources().getDrawable(drawable_with_nine_patch)).isNotNull();
    }

    @Test
    public void settingBoundsShouldInvokeCallback() {
        ShadowDrawableTest.TestDrawable drawable = new ShadowDrawableTest.TestDrawable();
        assertThat(drawable.boundsChanged).isFalse();
        setBounds(0, 0, 10, 10);
        assertThat(drawable.boundsChanged).isTrue();
    }

    @Test
    public void drawableIntrinsicWidthAndHeightShouldBeCorrect() {
        final Drawable anImage = context.getResources().getDrawable(an_image);
        assertThat(anImage.getIntrinsicHeight()).isEqualTo(53);
        assertThat(anImage.getIntrinsicWidth()).isEqualTo(64);
    }

    @Test
    @Config(qualifiers = "mdpi")
    public void drawableShouldLoadImageOfCorrectSizeWithMdpiQualifier() {
        final Drawable anImage = context.getResources().getDrawable(robolectric);
        assertThat(anImage.getIntrinsicHeight()).isEqualTo(167);
        assertThat(anImage.getIntrinsicWidth()).isEqualTo(198);
    }

    @Test
    @Config(qualifiers = "hdpi")
    public void drawableShouldLoadImageOfCorrectSizeWithHdpiQualifier() {
        final Drawable anImage = context.getResources().getDrawable(robolectric);
        assertThat(anImage.getIntrinsicHeight()).isEqualTo(251);
        assertThat(anImage.getIntrinsicWidth()).isEqualTo(297);
    }

    @Test
    @Config(maxSdk = VERSION_CODES.KITKAT_WATCH)
    public void testGetBitmapOrVectorDrawableAt19() {
        // at API 21+ and mdpi, the drawable-anydpi-v21/image_or_vector.xml should be loaded instead
        // of drawable/image_or_vector.png
        final Drawable aDrawable = context.getResources().getDrawable(an_image_or_vector);
        assertThat(aDrawable).isInstanceOf(BitmapDrawable.class);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void testGetBitmapOrVectorDrawableAt21() {
        final Drawable aDrawable = context.getResources().getDrawable(an_image_or_vector);
        assertThat(aDrawable).isInstanceOf(VectorDrawable.class);
    }

    private static class TestDrawable extends Drawable {
        public boolean boundsChanged;

        @Override
        public void draw(Canvas canvas) {
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

        @Override
        protected void onBoundsChange(Rect bounds) {
            boundsChanged = true;
            super.onBoundsChange(bounds);
        }
    }
}

