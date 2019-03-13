package android.graphics;


import Bitmap.Config.ARGB_8888;
import Color.GREEN;
import CompressFormat.JPEG;
import R.drawable.an_image;
import android.content.res.Resources;
import androidx.test.filters.SdkSuppress;
import androidx.test.runner.AndroidJUnit4;
import java.io.ByteArrayOutputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.internal.DoNotInstrument;

import static Paint.ANTI_ALIAS_FLAG;


/**
 * Compatibility test for {@link Bitmap}
 */
@DoNotInstrument
@RunWith(AndroidJUnit4.class)
public class BitmapTest {
    private Resources resources;

    @Config(minSdk = P)
    @SdkSuppress(minSdkVersion = P)
    @Test
    public void createBitmap() {
        Picture picture = new Picture();
        Canvas canvas = picture.beginRecording(200, 100);
        Paint p = new Paint(ANTI_ALIAS_FLAG);
        p.setColor(-1996554240);
        canvas.drawCircle(50, 50, 40, p);
        p.setColor(GREEN);
        p.setTextSize(30);
        canvas.drawText("Pictures", 60, 60, p);
        picture.endRecording();
        Bitmap bitmap = Bitmap.createBitmap(picture);
        assertThat(bitmap.isMutable()).isFalse();
    }

    @Test
    public void testEraseColor() {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, ARGB_8888);
        bitmap.eraseColor(-65536);
        assertThat(bitmap.getPixel(10, 10)).isEqualTo(-65536);
        assertThat(bitmap.getPixel(50, 50)).isEqualTo(-65536);
    }

    // getAlpha() returns 0 on less than M
    @Test
    @SdkSuppress(minSdkVersion = M)
    public void testExtractAlpha() {
        // normal case
        Bitmap bitmap = BitmapFactory.decodeResource(resources, an_image, new BitmapFactory.Options());
        Bitmap ret = bitmap.extractAlpha();
        int source = bitmap.getPixel(10, 20);
        int result = ret.getPixel(10, 20);
        assertThat(Color.alpha(result)).isEqualTo(Color.alpha(source));
    }

    @Test
    public void testCopy() {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, ARGB_8888);
        Bitmap copy = bitmap.copy(ARGB_8888, false);
        assertThat(copy.getWidth()).isEqualTo(bitmap.getWidth());
        assertThat(copy.getHeight()).isEqualTo(bitmap.getHeight());
        assertThat(copy.getConfig()).isEqualTo(bitmap.getConfig());
    }

    @Test
    public void testCopyAndEraseColor() {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, ARGB_8888);
        Bitmap copy = bitmap.copy(ARGB_8888, true);
        copy.eraseColor(-65536);
        assertThat(copy.getPixel(10, 10)).isEqualTo(-65536);
        assertThat(copy.getPixel(50, 50)).isEqualTo(-65536);
    }

    @Test
    public void compress() {
        Bitmap bitmap = BitmapFactory.decodeResource(resources, an_image);
        ByteArrayOutputStream stm = new ByteArrayOutputStream();
        assertThat(bitmap.compress(JPEG, 0, stm)).isTrue();
        assertThat(stm.toByteArray()).isNotEmpty();
    }
}

