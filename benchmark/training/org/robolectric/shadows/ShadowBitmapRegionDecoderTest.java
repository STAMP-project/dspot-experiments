package org.robolectric.shadows;


import Bitmap.Config.ARGB_8888;
import Bitmap.Config.RGB_565;
import BitmapFactory.Options;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
@Config(qualifiers = "hdpi")
public class ShadowBitmapRegionDecoderTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testNewInstance() throws Exception {
        assertThat(BitmapRegionDecoder.newInstance(ByteStreams.toByteArray(ShadowBitmapRegionDecoderTest.getImageInputStream()), 0, 0, false)).isNotNull();
        assertThat(BitmapRegionDecoder.newInstance(ShadowBitmapRegionDecoderTest.getImageFd(), false)).isNotNull();
        assertThat(BitmapRegionDecoder.newInstance(ShadowBitmapRegionDecoderTest.getImageInputStream(), false)).isNotNull();
        assertThat(BitmapRegionDecoder.newInstance(getGeneratedImageFile(), false)).isNotNull();
    }

    @Test
    public void getWidthAndGetHeight_shouldReturnCorrectValuesForImage() throws Exception {
        BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(ShadowBitmapRegionDecoderTest.getImageInputStream(), true);
        assertThat(decoder.getWidth()).isEqualTo(297);
        assertThat(decoder.getHeight()).isEqualTo(251);
    }

    @Test
    public void testDecodeRegionReturnsExpectedSize() throws IOException {
        BitmapRegionDecoder bitmapRegionDecoder = BitmapRegionDecoder.newInstance(ShadowBitmapRegionDecoderTest.getImageInputStream(), false);
        Bitmap bitmap = bitmapRegionDecoder.decodeRegion(new Rect(10, 20, 110, 220), new BitmapFactory.Options());
        assertThat(bitmap.getWidth()).isEqualTo(100);
        assertThat(bitmap.getHeight()).isEqualTo(200);
    }

    @Test
    public void testDecodeRegionReturnsExpectedConfig() throws IOException {
        BitmapRegionDecoder bitmapRegionDecoder = BitmapRegionDecoder.newInstance(ShadowBitmapRegionDecoderTest.getImageInputStream(), false);
        BitmapFactory.Options options = new BitmapFactory.Options();
        assertThat(bitmapRegionDecoder.decodeRegion(new Rect(0, 0, 1, 1), options).getConfig()).isEqualTo(ARGB_8888);
        options.inPreferredConfig = null;
        assertThat(bitmapRegionDecoder.decodeRegion(new Rect(0, 0, 1, 1), options).getConfig()).isEqualTo(ARGB_8888);
        options.inPreferredConfig = Config.RGB_565;
        assertThat(bitmapRegionDecoder.decodeRegion(new Rect(0, 0, 1, 1), options).getConfig()).isEqualTo(RGB_565);
    }
}

