package com.bumptech.glide.load.resource.bitmap;


import Bitmap.Config.ARGB_4444;
import Bitmap.Config.ARGB_8888;
import Bitmap.Config.RGB_565;
import ExifInterface.ORIENTATION_FLIP_HORIZONTAL;
import ExifInterface.ORIENTATION_FLIP_VERTICAL;
import ExifInterface.ORIENTATION_NORMAL;
import ExifInterface.ORIENTATION_ROTATE_180;
import ExifInterface.ORIENTATION_ROTATE_270;
import ExifInterface.ORIENTATION_ROTATE_90;
import ExifInterface.ORIENTATION_TRANSPOSE;
import ExifInterface.ORIENTATION_TRANSVERSE;
import ExifInterface.ORIENTATION_UNDEFINED;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Preconditions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowBitmap;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 28, shadows = { TransformationUtilsTest.AlphaShadowBitmap.class })
public class TransformationUtilsTest {
    @Mock
    private BitmapPool bitmapPool;

    @Test
    public void testFitCenterWithWideBitmap() {
        final int maxSide = 500;
        Bitmap wide = Bitmap.createBitmap(2000, 100, ARGB_8888);
        Bitmap transformed = TransformationUtils.fitCenter(bitmapPool, wide, maxSide, maxSide);
        TransformationUtilsTest.assertHasOriginalAspectRatio(wide, transformed);
        TransformationUtilsTest.assertBitmapFitsExactlyWithinBounds(maxSide, transformed);
    }

    @Test
    public void testFitCenterWithSmallWideBitmap() {
        final int maxSide = 500;
        Bitmap smallWide = Bitmap.createBitmap(400, 40, ARGB_8888);
        Bitmap transformed = TransformationUtils.fitCenter(bitmapPool, smallWide, maxSide, maxSide);
        TransformationUtilsTest.assertHasOriginalAspectRatio(smallWide, transformed);
        TransformationUtilsTest.assertBitmapFitsExactlyWithinBounds(maxSide, transformed);
    }

    @Test
    public void testFitCenterWithTallBitmap() {
        final int maxSide = 500;
        Bitmap tall = Bitmap.createBitmap(65, 3000, ARGB_8888);
        Bitmap transformed = TransformationUtils.fitCenter(bitmapPool, tall, maxSide, maxSide);
        TransformationUtilsTest.assertHasOriginalAspectRatio(tall, transformed);
        TransformationUtilsTest.assertBitmapFitsExactlyWithinBounds(maxSide, transformed);
    }

    @Test
    public void testFitCenterWithSmallTallBitmap() {
        final int maxSide = 500;
        Bitmap smallTall = Bitmap.createBitmap(10, 400, ARGB_8888);
        Bitmap transformed = TransformationUtils.fitCenter(bitmapPool, smallTall, maxSide, maxSide);
        TransformationUtilsTest.assertHasOriginalAspectRatio(smallTall, transformed);
        TransformationUtilsTest.assertBitmapFitsExactlyWithinBounds(maxSide, transformed);
    }

    @Test
    public void testFitCenterWithSquareBitmap() {
        final int maxSide = 500;
        Bitmap square = Bitmap.createBitmap(600, 600, ARGB_8888);
        Bitmap transformed = TransformationUtils.fitCenter(bitmapPool, square, maxSide, maxSide);
        TransformationUtilsTest.assertHasOriginalAspectRatio(square, transformed);
        TransformationUtilsTest.assertBitmapFitsExactlyWithinBounds(maxSide, transformed);
    }

    @Test
    public void testFitCenterWithTooSmallSquareBitmap() {
        final int maxSide = 500;
        Bitmap smallSquare = Bitmap.createBitmap(100, 100, ARGB_8888);
        Bitmap transformed = TransformationUtils.fitCenter(bitmapPool, smallSquare, maxSide, maxSide);
        TransformationUtilsTest.assertHasOriginalAspectRatio(smallSquare, transformed);
        TransformationUtilsTest.assertBitmapFitsExactlyWithinBounds(maxSide, transformed);
    }

    // Test for Issue #195.
    @Test
    public void testFitCenterUsesFloorInsteadOfRoundingForOutputBitmapSize() {
        Bitmap toTransform = Bitmap.createBitmap(1230, 1640, RGB_565);
        Bitmap transformed = TransformationUtils.fitCenter(bitmapPool, toTransform, 1075, 1366);
        Assert.assertEquals(1024, transformed.getWidth());
        Assert.assertEquals(1366, transformed.getHeight());
    }

    @Test
    public void testFitCenterReturnsGivenBitmapIfGivenBitmapMatchesExactly() {
        Bitmap toFit = Bitmap.createBitmap(100, 200, ARGB_4444);
        Bitmap transformed = TransformationUtils.fitCenter(bitmapPool, toFit, toFit.getWidth(), toFit.getHeight());
        Assert.assertTrue((toFit == transformed));
    }

    @Test
    public void testFitCenterReturnsGivenBitmapIfGivenBitmapWidthMatchesExactly() {
        Bitmap toFit = Bitmap.createBitmap(100, 200, ARGB_4444);
        Bitmap transformed = TransformationUtils.fitCenter(bitmapPool, toFit, toFit.getWidth(), ((toFit.getHeight()) * 2));
        Assert.assertTrue((toFit == transformed));
    }

    @Test
    public void testFitCenterReturnsGivenBitmapIfGivenBitmapHeightMatchesExactly() {
        Bitmap toFit = Bitmap.createBitmap(100, 200, ARGB_4444);
        Bitmap transformed = TransformationUtils.fitCenter(bitmapPool, toFit, ((toFit.getWidth()) * 2), toFit.getHeight());
        Assert.assertTrue((toFit == transformed));
    }

    @Test
    public void testCenterCropReturnsGivenBitmapIfGivenBitmapExactlyMatchesGivenDimensions() {
        Bitmap toCrop = Bitmap.createBitmap(200, 300, ARGB_8888);
        Bitmap transformed = TransformationUtils.centerCrop(bitmapPool, toCrop, toCrop.getWidth(), toCrop.getHeight());
        // Robolectric incorrectly implements equals() for Bitmaps, we want the original object not
        // just an equivalent.
        Assert.assertTrue((toCrop == transformed));
    }

    @Test
    @Config(sdk = 19)
    public void testFitCenterHandlesBitmapsWithNullConfigs() {
        Bitmap toFit = Bitmap.createBitmap(100, 100, RGB_565);
        toFit.setConfig(null);
        Bitmap transformed = TransformationUtils.fitCenter(bitmapPool, toFit, 50, 50);
        Assert.assertEquals(ARGB_8888, transformed.getConfig());
    }

    @Test
    public void testCenterCropSetsOutBitmapToHaveAlphaIfInBitmapHasAlphaAndOutBitmapIsReused() {
        Bitmap toTransform = Bitmap.createBitmap(100, 100, ARGB_8888);
        Bitmap toReuse = Bitmap.createBitmap(50, 50, ARGB_8888);
        Mockito.reset(bitmapPool);
        Mockito.when(bitmapPool.get(ArgumentMatchers.eq(50), ArgumentMatchers.eq(50), ArgumentMatchers.eq(ARGB_8888))).thenReturn(toReuse);
        toReuse.setHasAlpha(false);
        toTransform.setHasAlpha(true);
        Bitmap result = TransformationUtils.centerCrop(bitmapPool, toTransform, toReuse.getWidth(), toReuse.getHeight());
        Assert.assertEquals(toReuse, result);
        Assert.assertTrue(result.hasAlpha());
    }

    @Test
    public void testCenterCropSetsOutBitmapToNotHaveAlphaIfInBitmapDoesNotHaveAlphaAndOutBitmapIsReused() {
        Bitmap toTransform = Bitmap.createBitmap(100, 100, ARGB_8888);
        Bitmap toReuse = Bitmap.createBitmap(50, 50, ARGB_8888);
        Mockito.reset(bitmapPool);
        Mockito.when(bitmapPool.get(ArgumentMatchers.eq(50), ArgumentMatchers.eq(50), ArgumentMatchers.eq(ARGB_8888))).thenReturn(toReuse);
        toReuse.setHasAlpha(true);
        toTransform.setHasAlpha(false);
        Bitmap result = TransformationUtils.centerCrop(bitmapPool, toTransform, toReuse.getWidth(), toReuse.getHeight());
        Assert.assertEquals(toReuse, result);
        Assert.assertFalse(result.hasAlpha());
    }

    @Test
    public void testCenterCropSetsOutBitmapToHaveAlphaIfInBitmapHasAlpha() {
        Bitmap toTransform = Bitmap.createBitmap(100, 100, ARGB_8888);
        toTransform.setHasAlpha(true);
        Bitmap result = TransformationUtils.centerCrop(bitmapPool, toTransform, ((toTransform.getWidth()) / 2), ((toTransform.getHeight()) / 2));
        Assert.assertTrue(result.hasAlpha());
    }

    @Test
    @Config(sdk = 19)
    public void testCenterCropHandlesBitmapsWithNullConfigs() {
        Bitmap toTransform = Bitmap.createBitmap(100, 100, RGB_565);
        toTransform.setConfig(null);
        Bitmap transformed = TransformationUtils.centerCrop(bitmapPool, toTransform, 50, 50);
        Assert.assertEquals(ARGB_8888, transformed.getConfig());
    }

    @Test
    public void testCenterCropSetsOutBitmapToNotHaveAlphaIfInBitmapDoesNotHaveAlpha() {
        Bitmap toTransform = Bitmap.createBitmap(100, 100, ARGB_8888);
        toTransform.setHasAlpha(false);
        Bitmap result = TransformationUtils.centerCrop(bitmapPool, toTransform, ((toTransform.getWidth()) / 2), ((toTransform.getHeight()) / 2));
        Assert.assertFalse(result.hasAlpha());
    }

    @Test
    public void testFitCenterSetsOutBitmapToHaveAlphaIfInBitmapHasAlphaAndOutBitmapIsReused() {
        Bitmap toTransform = Bitmap.createBitmap(100, 100, ARGB_8888);
        Bitmap toReuse = Bitmap.createBitmap(50, 50, ARGB_8888);
        Mockito.reset(bitmapPool);
        Mockito.when(bitmapPool.get(ArgumentMatchers.eq(toReuse.getWidth()), ArgumentMatchers.eq(toReuse.getHeight()), ArgumentMatchers.eq(toReuse.getConfig()))).thenReturn(toReuse);
        toReuse.setHasAlpha(false);
        toTransform.setHasAlpha(true);
        Bitmap result = TransformationUtils.fitCenter(bitmapPool, toTransform, toReuse.getWidth(), toReuse.getHeight());
        Assert.assertEquals(toReuse, result);
        Assert.assertTrue(result.hasAlpha());
    }

    @Test
    public void testFitCenterSetsOutBitmapToNotHaveAlphaIfInBitmapDoesNotHaveAlphaAndOutBitmapIsReused() {
        Bitmap toTransform = Bitmap.createBitmap(100, 100, ARGB_8888);
        Bitmap toReuse = Bitmap.createBitmap(50, 50, ARGB_8888);
        Mockito.reset(bitmapPool);
        Mockito.when(bitmapPool.get(ArgumentMatchers.eq(toReuse.getWidth()), ArgumentMatchers.eq(toReuse.getHeight()), ArgumentMatchers.eq(toReuse.getConfig()))).thenReturn(toReuse);
        toReuse.setHasAlpha(true);
        toTransform.setHasAlpha(false);
        Bitmap result = TransformationUtils.fitCenter(bitmapPool, toTransform, toReuse.getWidth(), toReuse.getHeight());
        Assert.assertEquals(toReuse, result);
        Assert.assertFalse(result.hasAlpha());
    }

    @Test
    public void testFitCenterSetsOutBitmapToHaveAlphaIfInBitmapHasAlpha() {
        Bitmap toTransform = Bitmap.createBitmap(100, 100, ARGB_8888);
        toTransform.setHasAlpha(true);
        Bitmap result = TransformationUtils.fitCenter(bitmapPool, toTransform, ((toTransform.getWidth()) / 2), ((toTransform.getHeight()) / 2));
        Assert.assertTrue(result.hasAlpha());
    }

    @Test
    public void testFitCenterSetsOutBitmapToNotHaveAlphaIfInBitmapDoesNotHaveAlpha() {
        Bitmap toTransform = Bitmap.createBitmap(100, 100, ARGB_8888);
        toTransform.setHasAlpha(false);
        Bitmap result = TransformationUtils.fitCenter(bitmapPool, toTransform, ((toTransform.getWidth()) / 2), ((toTransform.getHeight()) / 2));
        Assert.assertFalse(result.hasAlpha());
    }

    @Test
    public void testGetExifOrientationDegrees() {
        Assert.assertEquals(0, TransformationUtils.getExifOrientationDegrees(ORIENTATION_NORMAL));
        Assert.assertEquals(90, TransformationUtils.getExifOrientationDegrees(ORIENTATION_TRANSPOSE));
        Assert.assertEquals(90, TransformationUtils.getExifOrientationDegrees(ORIENTATION_ROTATE_90));
        Assert.assertEquals(180, TransformationUtils.getExifOrientationDegrees(ORIENTATION_ROTATE_180));
        Assert.assertEquals(180, TransformationUtils.getExifOrientationDegrees(ORIENTATION_FLIP_VERTICAL));
        Assert.assertEquals(270, TransformationUtils.getExifOrientationDegrees(ORIENTATION_TRANSVERSE));
        Assert.assertEquals(270, TransformationUtils.getExifOrientationDegrees(ORIENTATION_ROTATE_270));
    }

    @Test
    public void testRotateImage() {
        Bitmap toRotate = Bitmap.createBitmap(2, 2, ARGB_8888);
        Bitmap zero = TransformationUtils.rotateImage(toRotate, 0);
        Assert.assertTrue((toRotate == zero));
        Bitmap ninety = TransformationUtils.rotateImage(toRotate, 90);
        assertThat(Shadows.shadowOf(ninety).getDescription()).contains("rotate=90.0");
        Assert.assertEquals(toRotate.getWidth(), toRotate.getHeight());
    }

    @Test
    public void testRotateImageExifReturnsGivenBitmapIfRotationIsNormal() {
        Bitmap toRotate = Bitmap.createBitmap(100, 200, ARGB_4444);
        // Use assertTrue because Robolectric incorrectly implements equality for Bitmaps. We want
        // not just an identical Bitmap, but our original Bitmap object back.
        Bitmap rotated = TransformationUtils.rotateImageExif(bitmapPool, toRotate, ORIENTATION_NORMAL);
        Assert.assertTrue((toRotate == rotated));
    }

    @Test
    public void testRotateImageExifReturnsGivenBitmapIfRotationIsUndefined() {
        Bitmap toRotate = Bitmap.createBitmap(100, 100, RGB_565);
        // Use assertTrue because Robolectric incorrectly implements equality for Bitmaps. We want
        // not just an identical Bitmap, but our original Bitmap object back.
        Bitmap rotated = TransformationUtils.rotateImageExif(bitmapPool, toRotate, ORIENTATION_UNDEFINED);
        Assert.assertTrue((toRotate == rotated));
    }

    @Test
    public void testRotateImageExifReturnsGivenBitmapIfOrientationIsInvalid() {
        Bitmap toRotate = Bitmap.createBitmap(200, 100, ARGB_8888);
        // Use assertTrue because Robolectric incorrectly implements equality for Bitmaps. We want
        // not just an identical Bitmap, but our original Bitmap object back.
        Bitmap rotated = TransformationUtils.rotateImageExif(bitmapPool, toRotate, (-1));
        Assert.assertTrue((toRotate == rotated));
    }

    @Test
    @Config(sdk = 19)
    public void testRotateImageExifHandlesBitmapsWithNullConfigs() {
        Bitmap toRotate = Bitmap.createBitmap(100, 100, RGB_565);
        toRotate.setConfig(null);
        Bitmap rotated = TransformationUtils.rotateImageExif(bitmapPool, toRotate, ORIENTATION_ROTATE_180);
        Assert.assertEquals(ARGB_8888, rotated.getConfig());
    }

    @Test
    public void testInitializeMatrixSetsScaleIfFlipHorizontal() {
        Matrix matrix = Mockito.mock(Matrix.class);
        TransformationUtils.initializeMatrixForRotation(ORIENTATION_FLIP_HORIZONTAL, matrix);
        Mockito.verify(matrix).setScale((-1), 1);
    }

    @Test
    public void testInitializeMatrixSetsScaleAndRotateIfFlipVertical() {
        Matrix matrix = Mockito.mock(Matrix.class);
        TransformationUtils.initializeMatrixForRotation(ORIENTATION_FLIP_VERTICAL, matrix);
        Mockito.verify(matrix).setRotate(180);
        Mockito.verify(matrix).postScale((-1), 1);
    }

    @Test
    public void testInitializeMatrixSetsScaleAndRotateIfTranspose() {
        Matrix matrix = Mockito.mock(Matrix.class);
        TransformationUtils.initializeMatrixForRotation(ORIENTATION_TRANSPOSE, matrix);
        Mockito.verify(matrix).setRotate(90);
        Mockito.verify(matrix).postScale((-1), 1);
    }

    @Test
    public void testInitializeMatrixSetsScaleAndRotateIfTransverse() {
        Matrix matrix = Mockito.mock(Matrix.class);
        TransformationUtils.initializeMatrixForRotation(ORIENTATION_TRANSVERSE, matrix);
        Mockito.verify(matrix).setRotate((-90));
        Mockito.verify(matrix).postScale((-1), 1);
    }

    @Test
    public void testInitializeMatrixSetsRotateOnRotation() {
        Matrix matrix = Mockito.mock(Matrix.class);
        TransformationUtils.initializeMatrixForRotation(ORIENTATION_ROTATE_90, matrix);
        Mockito.verify(matrix).setRotate(90);
        TransformationUtils.initializeMatrixForRotation(ORIENTATION_ROTATE_180, matrix);
        Mockito.verify(matrix).setRotate(180);
        TransformationUtils.initializeMatrixForRotation(ORIENTATION_ROTATE_270, matrix);
        Mockito.verify(matrix).setRotate((-90));
    }

    @Implements(Bitmap.class)
    public static class AlphaShadowBitmap extends ShadowBitmap {
        @Implementation
        public static Bitmap createBitmap(int width, int height, Bitmap.Config config) {
            // Robolectric doesn't match the framework behavior with null configs, so we have to do so
            // here.
            Preconditions.checkNotNull("Config must not be null");
            return ShadowBitmap.createBitmap(width, height, config);
        }
    }
}

