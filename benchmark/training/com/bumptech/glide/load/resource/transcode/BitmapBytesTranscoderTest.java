package com.bumptech.glide.load.resource.transcode;


import Bitmap.CompressFormat;
import Bitmap.Config.ALPHA_8;
import android.graphics.Bitmap;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.tests.Util;
import com.bumptech.glide.util.Preconditions;
import java.io.ByteArrayOutputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class BitmapBytesTranscoderTest {
    private BitmapBytesTranscoderTest.BitmapBytesTranscoderHarness harness;

    @Test
    public void testReturnsBytesOfGivenBitmap() {
        assertThat(harness.getTranscodeResult()).isEqualTo(harness.getExpectedData());
    }

    @Test
    public void testUsesGivenQuality() {
        harness.quality = 66;
        assertThat(harness.getTranscodeResult()).isEqualTo(harness.getExpectedData());
    }

    @Test
    public void testUsesGivenFormat() {
        for (Bitmap.CompressFormat format : CompressFormat.values()) {
            harness.compressFormat = format;
            assertThat(harness.getTranscodeResult()).isEqualTo(harness.getExpectedData());
        }
    }

    @Test
    public void testBitmapResourceIsRecycled() {
        harness.getTranscodeResult();
        Mockito.verify(harness.bitmapResource).recycle();
    }

    private static class BitmapBytesTranscoderHarness {
        CompressFormat compressFormat = CompressFormat.JPEG;

        int quality = 100;

        final Bitmap bitmap = Bitmap.createBitmap(100, 100, ALPHA_8);

        final Resource<Bitmap> bitmapResource = Util.mockResource();

        final Options options = new Options();

        BitmapBytesTranscoderHarness() {
            Mockito.when(bitmapResource.get()).thenReturn(bitmap);
        }

        byte[] getTranscodeResult() {
            BitmapBytesTranscoder transcoder = new BitmapBytesTranscoder(compressFormat, quality);
            Resource<byte[]> bytesResource = Preconditions.checkNotNull(transcoder.transcode(bitmapResource, options));
            return bytesResource.get();
        }

        byte[] getExpectedData() {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(compressFormat, quality, os);
            return os.toByteArray();
        }
    }
}

