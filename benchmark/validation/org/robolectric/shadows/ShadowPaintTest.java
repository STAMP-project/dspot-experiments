package org.robolectric.shadows;


import Color.RED;
import Paint.Align.CENTER;
import Paint.Align.LEFT;
import android.graphics.Paint;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.shadow.api.Shadow;


@RunWith(AndroidJUnit4.class)
public class ShadowPaintTest {
    @Test
    public void shouldGetIsDitherInfo() {
        Paint paint = Shadow.newInstanceOf(Paint.class);
        Assert.assertFalse(paint.isAntiAlias());
        ShadowPaint shadowPaint = Shadows.shadowOf(paint);
        shadowPaint.setAntiAlias(true);
        Assert.assertTrue(paint.isAntiAlias());
    }

    @Test
    public void shouldGetIsAntiAlias() {
        Paint paint = Shadow.newInstanceOf(Paint.class);
        Assert.assertFalse(paint.isAntiAlias());
        ShadowPaint shadowPaint = Shadows.shadowOf(paint);
        shadowPaint.setAntiAlias(true);
        Assert.assertTrue(paint.isAntiAlias());
        shadowPaint.setAntiAlias(false);
        Assert.assertFalse(paint.isAntiAlias());
    }

    @Test
    public void testCtor() {
        assertThat(isAntiAlias()).isTrue();
        assertThat(new Paint(0).isAntiAlias()).isFalse();
    }

    @Test
    public void testCtorWithPaint() {
        Paint paint = new Paint();
        paint.setColor(RED);
        paint.setAlpha(72);
        paint.setFlags(2345);
        Paint other = new Paint(paint);
        assertThat(other.getColor()).isEqualTo(RED);
        assertThat(other.getAlpha()).isEqualTo(72);
        assertThat(other.getFlags()).isEqualTo(2345);
    }

    @Test
    public void shouldGetAndSetTextAlignment() throws Exception {
        Paint paint = Shadow.newInstanceOf(Paint.class);
        assertThat(paint.getTextAlign()).isEqualTo(LEFT);
        paint.setTextAlign(CENTER);
        assertThat(paint.getTextAlign()).isEqualTo(CENTER);
    }

    @Test
    public void measureTextActuallyMeasuresLength() throws Exception {
        Paint paint = Shadow.newInstanceOf(Paint.class);
        assertThat(paint.measureText("Hello")).isEqualTo(5.0F);
        assertThat(paint.measureText("Hello", 1, 3)).isEqualTo(2.0F);
        assertThat(paint.measureText(new StringBuilder("Hello"), 1, 4)).isEqualTo(3.0F);
    }

    @Test
    public void createPaintFromPaint() throws Exception {
        Paint origPaint = new Paint();
        assertThat(getTextLocale()).isSameAs(origPaint.getTextLocale());
    }
}

