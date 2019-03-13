package com.bumptech.glide.load.resource.bitmap;


import Bitmap.Config.ARGB_8888;
import BitmapEncoder.COMPRESSION_FORMAT;
import BitmapEncoder.COMPRESSION_QUALITY;
import CompressFormat.JPEG;
import CompressFormat.PNG;
import CompressFormat.WEBP;
import EncodeStrategy.TRANSFORMED;
import RuntimeEnvironment.application;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.LruArrayPool;
import com.bumptech.glide.tests.Util;
import com.bumptech.glide.util.ByteBufferUtil;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class BitmapEncoderTest {
    private BitmapEncoderTest.EncoderHarness harness;

    @Test
    public void testBitmapIsEncoded() throws IOException {
        assertThat(harness.encode()).isEqualTo(harness.expectedData(JPEG, 90));
    }

    @Test
    public void testBitmapIsEncodedWithGivenQuality() throws IOException {
        int quality = 7;
        harness.setQuality(quality);
        assertThat(harness.encode()).isEqualTo(harness.expectedData(JPEG, quality));
    }

    @Test
    public void testEncoderObeysNonNullCompressFormat() throws IOException {
        Bitmap.CompressFormat format = CompressFormat.WEBP;
        harness.setFormat(format);
        assertThat(harness.encode()).isEqualTo(harness.expectedData(WEBP, 90));
    }

    @Test
    public void testEncoderEncodesJpegWithNullFormatAndBitmapWithoutAlpha() throws IOException {
        harness.setFormat(null);
        harness.bitmap.setHasAlpha(false);
        assertThat(harness.encode()).isEqualTo(harness.expectedData(JPEG, 90));
    }

    @Test
    public void testEncoderEncodesPngWithNullFormatAndBitmapWithAlpha() throws IOException {
        harness.setFormat(null);
        harness.bitmap.setHasAlpha(true);
        assertThat(harness.encode()).isEqualTo(harness.expectedData(PNG, 90));
    }

    @Test
    public void testReturnsTrueFromWrite() {
        BitmapEncoder encoder = new BitmapEncoder(harness.arrayPool);
        Assert.assertTrue(encoder.encode(harness.resource, harness.file, harness.options));
    }

    @Test
    public void testEncodeStrategy_alwaysReturnsTransformed() {
        BitmapEncoder encoder = new BitmapEncoder(harness.arrayPool);
        Assert.assertEquals(TRANSFORMED, encoder.getEncodeStrategy(harness.options));
    }

    private static class EncoderHarness {
        final Resource<Bitmap> resource = Util.mockResource();

        final Bitmap bitmap = Bitmap.createBitmap(100, 100, ARGB_8888);

        final Options options = new Options();

        final File file = new File(application.getCacheDir(), "test");

        final ArrayPool arrayPool = new LruArrayPool();

        EncoderHarness() {
            Mockito.when(resource.get()).thenReturn(bitmap);
        }

        void setQuality(int quality) {
            options.set(COMPRESSION_QUALITY, quality);
        }

        void setFormat(Bitmap.CompressFormat format) {
            options.set(COMPRESSION_FORMAT, format);
        }

        byte[] encode() throws IOException {
            BitmapEncoder encoder = new BitmapEncoder(arrayPool);
            encoder.encode(resource, file, options);
            return ByteBufferUtil.toBytes(ByteBufferUtil.fromFile(file));
        }

        byte[] expectedData(CompressFormat expectedFormat, int expectedQuality) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(expectedFormat, expectedQuality, os);
            return os.toByteArray();
        }

        void tearDown() {
            // GC before delete() to release files on Windows (https://stackoverflow.com/a/4213208/253468)
            System.gc();
            if ((file.exists()) && (!(file.delete()))) {
                throw new IllegalStateException(("Failed to delete: " + (file)));
            }
        }
    }
}

