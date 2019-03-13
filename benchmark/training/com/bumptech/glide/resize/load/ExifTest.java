package com.bumptech.glide.resize.load;


import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.resource.bitmap.DefaultImageHeaderParser;
import com.bumptech.glide.testutil.TestResourceUtil;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class ExifTest {
    private ArrayPool byteArrayPool;

    @Test
    public void testIssue387() throws IOException {
        InputStream is = TestResourceUtil.openResource(getClass(), "issue387_rotated_jpeg.jpg");
        assertThat(new DefaultImageHeaderParser().getOrientation(is, byteArrayPool)).isEqualTo(6);
    }

    @Test
    public void testLandscape() throws IOException {
        for (int i = 1; i <= 8; i++) {
            assertOrientation("Landscape", i);
        }
    }

    @Test
    public void testPortrait() throws IOException {
        for (int i = 1; i <= 8; i++) {
            assertOrientation("Portrait", i);
        }
    }

    @Test
    public void testHandlesInexactSizesInByteArrayPools() {
        for (int i = 1; i <= 8; i++) {
            byteArrayPool.put(new byte[ArrayPool.STANDARD_BUFFER_SIZE_BYTES]);
            assertOrientation("Portrait", i);
        }
        for (int i = 1; i <= 8; i++) {
            byteArrayPool.put(new byte[ArrayPool.STANDARD_BUFFER_SIZE_BYTES]);
            assertOrientation("Landscape", i);
        }
    }
}

