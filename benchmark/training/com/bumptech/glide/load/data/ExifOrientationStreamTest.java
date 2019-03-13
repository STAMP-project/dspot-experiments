package com.bumptech.glide.load.data;


import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.resource.bitmap.DefaultImageHeaderParser;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class ExifOrientationStreamTest {
    private ArrayPool byteArrayPool;

    @Test
    public void testIncludesGivenExifOrientation() throws IOException {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                InputStream toWrap = /* isLandscape */
                openOrientationExample(true, (j + 1));
                InputStream wrapped = new ExifOrientationStream(toWrap, i);
                DefaultImageHeaderParser parser = new DefaultImageHeaderParser();
                assertThat(parser.getOrientation(wrapped, byteArrayPool)).isEqualTo(i);
                toWrap = /* isLandscape */
                openOrientationExample(false, (j + 1));
                wrapped = new ExifOrientationStream(toWrap, i);
                assertThat(parser.getOrientation(wrapped, byteArrayPool)).isEqualTo(i);
            }
        }
    }
}

