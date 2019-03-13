package com.bumptech.glide.load.resource.bitmap;


import Bitmap.Config.ARGB_4444;
import Target.SIZE_ORIGINAL;
import android.app.Application;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import java.security.MessageDigest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class BitmapTransformationTest {
    @Mock
    private BitmapPool bitmapPool;

    private Application context;

    @Test
    public void testReturnsGivenResourceWhenBitmapNotTransformed() {
        BitmapTransformation transformation = new BitmapTransformation() {
            @Override
            public void updateDiskCacheKey(@NonNull
            MessageDigest messageDigest) {
            }

            @Override
            protected Bitmap transform(@NonNull
            BitmapPool pool, @NonNull
            Bitmap toTransform, int outWidth, int outHeight) {
                return toTransform;
            }
        };
        Resource<Bitmap> resource = mockResource(100, 100);
        Assert.assertEquals(resource, transformation.transform(context, resource, 1, 1));
    }

    @Test
    public void testReturnsNewResourceWhenBitmapTransformed() {
        final Bitmap transformed = Bitmap.createBitmap(100, 100, ARGB_4444);
        BitmapTransformation transformation = new BitmapTransformation() {
            @Override
            public void updateDiskCacheKey(@NonNull
            MessageDigest messageDigest) {
            }

            @Override
            protected Bitmap transform(@NonNull
            BitmapPool pool, @NonNull
            Bitmap bitmap, int outWidth, int outHeight) {
                return transformed;
            }
        };
        Resource<Bitmap> resource = mockResource(1, 2);
        Assert.assertNotSame(resource, transformation.transform(context, resource, 100, 100));
    }

    @Test
    public void testPassesGivenArgumentsToTransform() {
        final int expectedWidth = 13;
        final int expectedHeight = 148;
        final Resource<Bitmap> resource = mockResource(223, 4123);
        BitmapTransformation transformation = new BitmapTransformation() {
            @Override
            public void updateDiskCacheKey(@NonNull
            MessageDigest messageDigest) {
            }

            @Override
            protected Bitmap transform(@NonNull
            BitmapPool pool, @NonNull
            Bitmap toTransform, int outWidth, int outHeight) {
                Assert.assertEquals(bitmapPool, pool);
                Assert.assertEquals(resource.get(), toTransform);
                Assert.assertEquals(expectedWidth, outWidth);
                Assert.assertEquals(expectedHeight, outHeight);
                return resource.get();
            }
        };
        transformation.transform(context, resource, expectedWidth, expectedHeight);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIfGivenInvalidWidth() {
        BitmapTransformation transformation = new BitmapTransformation() {
            @Override
            public void updateDiskCacheKey(@NonNull
            MessageDigest messageDigest) {
            }

            @Override
            protected Bitmap transform(@NonNull
            BitmapPool bitmapPool, @NonNull
            Bitmap toTransform, int outWidth, int outHeight) {
                return null;
            }
        };
        transformation.transform(context, mockResource(1, 1), (-1), 100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIfGivenInvalidHeight() {
        BitmapTransformation transformation = new BitmapTransformation() {
            @Override
            public void updateDiskCacheKey(@NonNull
            MessageDigest messageDigest) {
            }

            @Override
            protected Bitmap transform(@NonNull
            BitmapPool bitmapPool, @NonNull
            Bitmap toTransform, int outWidth, int outHeight) {
                return null;
            }
        };
        transformation.transform(context, mockResource(1, 1), 100, (-1));
    }

    @Test
    public void testReturnsNullIfTransformReturnsNull() {
        BitmapTransformation transform = new BitmapTransformation() {
            @Override
            public void updateDiskCacheKey(@NonNull
            MessageDigest messageDigest) {
            }

            @Override
            protected Bitmap transform(@NonNull
            BitmapPool pool, @NonNull
            Bitmap toTransform, int outWidth, int outHeight) {
                return null;
            }
        };
        Resource<Bitmap> resource = mockResource(100, 100);
        Assert.assertNull(transform.transform(context, resource, 100, 100));
    }

    @Test
    public void testCallsTransformWithGivenBitmapWidthIfWidthIsSizeOriginal() {
        BitmapTransformationTest.SizeTrackingTransform transform = new BitmapTransformationTest.SizeTrackingTransform();
        int expectedWidth = 200;
        Resource<Bitmap> resource = mockResource(expectedWidth, 300);
        transform.transform(context, resource, SIZE_ORIGINAL, 500);
        Assert.assertEquals(expectedWidth, transform.givenWidth);
    }

    @Test
    public void testCallsTransformWithGivenBitmapHeightIfHeightIsSizeOriginal() {
        BitmapTransformationTest.SizeTrackingTransform transform = new BitmapTransformationTest.SizeTrackingTransform();
        int expectedHeight = 500;
        Resource<Bitmap> resource = mockResource(123, expectedHeight);
        transform.transform(context, resource, 444, expectedHeight);
        Assert.assertEquals(expectedHeight, transform.givenHeight);
    }

    private static final class SizeTrackingTransform extends BitmapTransformation {
        int givenWidth;

        int givenHeight;

        @Override
        protected Bitmap transform(@NonNull
        BitmapPool pool, @NonNull
        Bitmap toTransform, int outWidth, int outHeight) {
            givenWidth = outWidth;
            givenHeight = outHeight;
            return null;
        }

        @Override
        public void updateDiskCacheKey(@NonNull
        MessageDigest messageDigest) {
        }
    }
}

