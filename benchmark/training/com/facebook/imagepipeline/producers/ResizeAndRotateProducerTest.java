/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import DefaultImageFormats.JPEG;
import DefaultImageFormats.PNG;
import DefaultImageFormats.WEBP_SIMPLE;
import EncodedImage.UNKNOWN_ROTATION_ANGLE;
import ExifInterface.ORIENTATION_FLIP_HORIZONTAL;
import ExifInterface.ORIENTATION_FLIP_VERTICAL;
import ExifInterface.ORIENTATION_NORMAL;
import ExifInterface.ORIENTATION_ROTATE_180;
import ExifInterface.ORIENTATION_ROTATE_270;
import ExifInterface.ORIENTATION_ROTATE_90;
import ExifInterface.ORIENTATION_TRANSPOSE;
import ExifInterface.ORIENTATION_TRANSVERSE;
import ExifInterface.ORIENTATION_UNDEFINED;
import ResizeOptions.DEFAULT_ROUNDUP_FRACTION;
import RotationOptions.NO_ROTATION;
import RotationOptions.ROTATE_270;
import android.media.ExifInterface;
import android.os.SystemClock;
import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.common.memory.PooledByteBufferOutputStream;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.nativecode.NativeJpegTranscoder;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.testing.FakeClock;
import com.facebook.imagepipeline.testing.TestExecutorService;
import com.facebook.imagepipeline.testing.TestScheduledExecutorService;
import com.facebook.imagepipeline.transcoder.JpegTranscoderUtils;
import com.facebook.soloader.SoLoader;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static ResizeAndRotateProducer.MIN_TRANSFORM_INTERVAL_MS;


@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "androidx.*", "android.*" })
@Config(manifest = Config.NONE)
@PrepareOnlyThisForTest({ NativeJpegTranscoder.class, SystemClock.class, UiThreadImmediateExecutorService.class, JpegTranscoderUtils.class })
public class ResizeAndRotateProducerTest {
    static {
        SoLoader.setInTestMode();
    }

    @Mock
    public Producer mInputProducer;

    @Mock
    public ImageRequest mImageRequest;

    @Mock
    public ProducerListener mProducerListener;

    @Mock
    public Consumer<EncodedImage> mConsumer;

    @Mock
    public ProducerContext mProducerContext;

    @Mock
    public PooledByteBufferFactory mPooledByteBufferFactory;

    @Mock
    public PooledByteBufferOutputStream mPooledByteBufferOutputStream;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private static final int MIN_TRANSFORM_INTERVAL_MS = MIN_TRANSFORM_INTERVAL_MS;

    private static final int MAX_BITMAP_SIZE = 2024;

    private TestExecutorService mTestExecutorService;

    private ResizeAndRotateProducer mResizeAndRotateProducer;

    private Consumer<EncodedImage> mResizeAndRotateProducerConsumer;

    private CloseableReference<PooledByteBuffer> mIntermediateResult;

    private CloseableReference<PooledByteBuffer> mFinalResult;

    private PooledByteBuffer mPooledByteBuffer;

    private FakeClock mFakeClockForWorker;

    private FakeClock mFakeClockForScheduled;

    private TestScheduledExecutorService mTestScheduledExecutorService;

    private UiThreadImmediateExecutorService mUiThreadImmediateExecutorService;

    private EncodedImage mIntermediateEncodedImage;

    private EncodedImage mFinalEncodedImage;

    @Test
    public void testDoesNotTransformIfImageRotationAngleUnkown() {
        whenResizingEnabled();
        whenRequestSpecificRotation(NO_ROTATION);
        provideIntermediateResult(JPEG, 800, 800, UNKNOWN_ROTATION_ANGLE, ORIENTATION_UNDEFINED);
        verifyIntermediateResultPassedThroughUnchanged();
        provideFinalResult(JPEG, 800, 800, UNKNOWN_ROTATION_ANGLE, ORIENTATION_UNDEFINED);
        verifyFinalResultPassedThroughUnchanged();
        ResizeAndRotateProducerTest.verifyZeroJpegTranscoderInteractions();
    }

    @Test
    public void testDoesNotTransformIfRotationDisabled() {
        whenResizingEnabled();
        whenDisableRotation();
        provideIntermediateResult(JPEG);
        verifyIntermediateResultPassedThroughUnchanged();
        provideFinalResult(JPEG);
        verifyFinalResultPassedThroughUnchanged();
        ResizeAndRotateProducerTest.verifyZeroJpegTranscoderInteractions();
    }

    @Test
    public void testDoesNotTransformIfMetadataAngleAndRequestedRotationHaveOppositeValues() {
        whenResizingEnabled();
        whenRequestSpecificRotation(ROTATE_270);
        provideFinalResult(JPEG, 400, 200, 90, ORIENTATION_ROTATE_90);
        verifyAFinalResultPassedThroughNotResized();
        ResizeAndRotateProducerTest.verifyZeroJpegTranscoderInteractions();
    }

    @Test
    public void testDoesNotTransformIfNotRequested() {
        whenResizingDisabled();
        whenRequestsRotationFromMetadataWithoutDeferring();
        provideIntermediateResult(JPEG);
        verifyIntermediateResultPassedThroughUnchanged();
        provideFinalResult(JPEG);
        verifyFinalResultPassedThroughUnchanged();
        ResizeAndRotateProducerTest.verifyZeroJpegTranscoderInteractions();
    }

    @Test
    public void testDoesNotTransformIfNotJpeg() throws Exception {
        whenResizingEnabled();
        whenRequestsRotationFromMetadataWithoutDeferring();
        provideIntermediateResult(WEBP_SIMPLE);
        verifyIntermediateResultPassedThroughUnchanged();
        provideFinalResult(WEBP_SIMPLE);
        verifyFinalResultPassedThroughUnchanged();
        ResizeAndRotateProducerTest.verifyZeroJpegTranscoderInteractions();
    }

    @Test
    public void testDoesRotateIfJpegAndCannotDeferRotationAndResizingDisabled() throws Exception {
        whenResizingDisabled();
        testDoesRotateIfJpegAndCannotDeferRotation();
    }

    @Test
    public void testDoesRotateIfJpegAndCannotDeferRotationAndResizingEnabled() throws Exception {
        whenResizingEnabled();
        testDoesRotateIfJpegAndCannotDeferRotation();
    }

    @Test
    public void testRotateUsingRotationAngleOnlyForJPEG() throws Exception {
        whenResizingEnabled();
        int rotationAngle = 90;
        whenRequestSpecificRotation(rotationAngle);
        provideFinalResult(JPEG, 10, 10, 0, ORIENTATION_UNDEFINED);
        ResizeAndRotateProducerTest.verifyJpegTranscoderInteractions(8, rotationAngle);
        ResizeAndRotateProducerTest.verifyZeroJpegTranscoderExifOrientationInteractions();
    }

    @Test
    public void testRotationAngleIsSetForPNG() throws Exception {
        whenResizingEnabled();
        testNewResultContainsRotationAngleForImageFormat(PNG);
    }

    @Test
    public void testRotationAngleIsSetForWebp() throws Exception {
        whenResizingEnabled();
        testNewResultContainsRotationAngleForImageFormat(WEBP_SIMPLE);
    }

    @Test
    public void testRotateUsingExifOrientationOnly() throws Exception {
        whenResizingEnabled();
        int exifOrientation = ExifInterface.ORIENTATION_FLIP_HORIZONTAL;
        whenRequestsRotationFromMetadataWithoutDeferring();
        provideFinalResult(JPEG, 10, 10, 0, exifOrientation);
        ResizeAndRotateProducerTest.verifyZeroJpegTranscoderInteractions();
        ResizeAndRotateProducerTest.verifyJpegTranscoderExifOrientationInteractions(8, exifOrientation);
    }

    @Test
    public void testDoesNotRotateIfCanDeferRotationAndResizeNotNeeded() throws Exception {
        whenResizingEnabled();
        int rotationAngle = 180;
        int exifOrientation = ExifInterface.ORIENTATION_ROTATE_180;
        int sourceWidth = 10;
        int sourceHeight = 10;
        whenRequestWidthAndHeight(sourceWidth, sourceHeight);
        whenRequestsRotationFromMetadataWithDeferringAllowed();
        provideIntermediateResult(JPEG, sourceWidth, sourceHeight, rotationAngle, exifOrientation);
        verifyIntermediateResultPassedThroughUnchanged();
        provideFinalResult(JPEG, sourceWidth, sourceHeight, rotationAngle, exifOrientation);
        verifyFinalResultPassedThroughUnchanged();
        ResizeAndRotateProducerTest.verifyZeroJpegTranscoderInteractions();
    }

    @Test
    public void testDoesResizeAndRotateIfCanDeferRotationButResizeIsNeeded() throws Exception {
        whenResizingEnabled();
        int rotationAngle = 90;
        int exifOrientation = ExifInterface.ORIENTATION_ROTATE_90;
        int sourceWidth = 10;
        int sourceHeight = 10;
        whenRequestWidthAndHeight(sourceWidth, sourceHeight);
        whenRequestsRotationFromMetadataWithDeferringAllowed();
        provideIntermediateResult(JPEG, (sourceWidth * 2), (sourceHeight * 2), rotationAngle, exifOrientation);
        verifyNoIntermediateResultPassedThrough();
        provideFinalResult(JPEG, (sourceWidth * 2), (sourceHeight * 2), rotationAngle, exifOrientation);
        verifyAFinalResultPassedThroughResized();
        ResizeAndRotateProducerTest.verifyJpegTranscoderInteractions(4, rotationAngle);
    }

    @Test
    public void testDoesResizeIfJpegAndResizingEnabled() throws Exception {
        whenResizingEnabled();
        final int preferredWidth = 300;
        final int preferredHeight = 600;
        whenRequestWidthAndHeight(preferredWidth, preferredHeight);
        whenRequestSpecificRotation(NO_ROTATION);
        provideIntermediateResult(JPEG, (preferredWidth * 2), (preferredHeight * 2), 0, ORIENTATION_NORMAL);
        verifyNoIntermediateResultPassedThrough();
        provideFinalResult(JPEG, (preferredWidth * 2), (preferredHeight * 2), 0, ORIENTATION_NORMAL);
        verifyAFinalResultPassedThroughResized();
        Assert.assertEquals(2, mFinalResult.getUnderlyingReferenceTestOnly().getRefCountTestOnly());
        Assert.assertTrue(mPooledByteBuffer.isClosed());
        ResizeAndRotateProducerTest.verifyJpegTranscoderInteractions(4, 0);
    }

    @Test
    public void testDoesNotResizeIfJpegButResizingDisabled() throws Exception {
        whenResizingDisabled();
        final int preferredWidth = 300;
        final int preferredHeight = 600;
        whenRequestWidthAndHeight(preferredWidth, preferredHeight);
        whenRequestSpecificRotation(NO_ROTATION);
        provideIntermediateResult(JPEG, (preferredWidth * 2), (preferredHeight * 2), 0, ORIENTATION_NORMAL);
        verifyIntermediateResultPassedThroughUnchanged();
        provideFinalResult(JPEG, (preferredWidth * 2), (preferredHeight * 2), 0, ORIENTATION_NORMAL);
        verifyFinalResultPassedThroughUnchanged();
        ResizeAndRotateProducerTest.verifyZeroJpegTranscoderInteractions();
    }

    @Test
    public void testDoesNotUpscale() {
        whenResizingEnabled();
        whenRequestWidthAndHeight(150, 150);
        whenRequestSpecificRotation(NO_ROTATION);
        provideFinalResult(JPEG, 100, 100, 0, ORIENTATION_NORMAL);
        verifyFinalResultPassedThroughUnchanged();
        ResizeAndRotateProducerTest.verifyZeroJpegTranscoderInteractions();
    }

    @Test
    public void testDoesNotUpscaleWhenRotating() {
        whenResizingEnabled();
        whenRequestWidthAndHeight(150, 150);
        whenRequestsRotationFromMetadataWithoutDeferring();
        provideFinalResult(JPEG, 100, 100, 90, ORIENTATION_ROTATE_90);
        verifyAFinalResultPassedThroughNotResized();
        ResizeAndRotateProducerTest.verifyJpegTranscoderInteractions(8, 90);
    }

    @Test
    public void testDoesComputeRightNumeratorWhenRotating_0() {
        testDoesComputeRightNumerator(0, ORIENTATION_NORMAL, 4);
    }

    @Test
    public void testDoesComputeRightNumeratorWhenRotating_90() {
        testDoesComputeRightNumerator(90, ORIENTATION_ROTATE_90, 2);
    }

    @Test
    public void testDoesComputeRightNumeratorWhenRotating_180() {
        testDoesComputeRightNumerator(180, ORIENTATION_ROTATE_180, 4);
    }

    @Test
    public void testDoesComputeRightNumeratorWhenRotating_270() {
        testDoesComputeRightNumerator(270, ORIENTATION_ROTATE_270, 2);
    }

    @Test
    public void testDoesComputeRightNumeratorInvertedOrientation_flipHorizontal() {
        testDoesComputeRightNumeratorInvertedOrientation(ORIENTATION_FLIP_HORIZONTAL, 4);
    }

    @Test
    public void testDoesComputeRightNumeratorInvertedOrientation_flipVertical() {
        testDoesComputeRightNumeratorInvertedOrientation(ORIENTATION_FLIP_VERTICAL, 4);
    }

    @Test
    public void testDoesComputeRightNumeratorInvertedOrientation_transpose() {
        testDoesComputeRightNumeratorInvertedOrientation(ORIENTATION_TRANSPOSE, 2);
    }

    @Test
    public void testDoesComputeRightNumeratorInvertedOrientation_transverse() {
        testDoesComputeRightNumeratorInvertedOrientation(ORIENTATION_TRANSVERSE, 2);
    }

    @Test
    public void testDoesRotateWhenNoResizeOptionsIfCannotBeDeferred() {
        whenResizingEnabled();
        whenRequestWidthAndHeight(0, 0);
        whenRequestsRotationFromMetadataWithoutDeferring();
        provideFinalResult(JPEG, 400, 200, 90, ORIENTATION_ROTATE_90);
        verifyAFinalResultPassedThroughNotResized();
        ResizeAndRotateProducerTest.verifyJpegTranscoderInteractions(8, 90);
    }

    @Test
    public void testDoesNotRotateWhenNoResizeOptionsAndCanBeDeferred() {
        whenResizingEnabled();
        whenRequestWidthAndHeight(0, 0);
        whenRequestsRotationFromMetadataWithDeferringAllowed();
        provideFinalResult(JPEG, 400, 200, 90, ORIENTATION_ROTATE_90);
        verifyFinalResultPassedThroughUnchanged();
        ResizeAndRotateProducerTest.verifyZeroJpegTranscoderInteractions();
    }

    @Test
    public void testDoesRotateWhenSpecificRotationRequested() {
        whenResizingEnabled();
        whenRequestWidthAndHeight(200, 400);
        whenRequestSpecificRotation(ROTATE_270);
        provideFinalResult(JPEG, 400, 200, 0, ORIENTATION_UNDEFINED);
        verifyAFinalResultPassedThroughNotResized();
        ResizeAndRotateProducerTest.verifyJpegTranscoderInteractions(8, 270);
    }

    @Test
    public void testDoesNothingWhenNotAskedToDoAnything() {
        whenResizingEnabled();
        whenRequestWidthAndHeight(0, 0);
        whenDisableRotation();
        provideFinalResult(JPEG, 400, 200, 90, ORIENTATION_ROTATE_90);
        verifyAFinalResultPassedThroughNotResized();
        ResizeAndRotateProducerTest.verifyZeroJpegTranscoderInteractions();
    }

    @Test
    public void testRoundNumerator() {
        Assert.assertEquals(1, JpegTranscoderUtils.roundNumerator((1.0F / 8), DEFAULT_ROUNDUP_FRACTION));
        Assert.assertEquals(1, JpegTranscoderUtils.roundNumerator((5.0F / 32), DEFAULT_ROUNDUP_FRACTION));
        Assert.assertEquals(1, JpegTranscoderUtils.roundNumerator(((1.0F / 6) - 0.01F), DEFAULT_ROUNDUP_FRACTION));
        Assert.assertEquals(2, JpegTranscoderUtils.roundNumerator((1.0F / 6), DEFAULT_ROUNDUP_FRACTION));
        Assert.assertEquals(2, JpegTranscoderUtils.roundNumerator((3.0F / 16), DEFAULT_ROUNDUP_FRACTION));
        Assert.assertEquals(2, JpegTranscoderUtils.roundNumerator((2.0F / 8), DEFAULT_ROUNDUP_FRACTION));
    }

    @Test
    public void testDownsamplingRatioUsage() {
        Assert.assertEquals(8, JpegTranscoderUtils.calculateDownsampleNumerator(1));
        Assert.assertEquals(4, JpegTranscoderUtils.calculateDownsampleNumerator(2));
        Assert.assertEquals(2, JpegTranscoderUtils.calculateDownsampleNumerator(4));
        Assert.assertEquals(1, JpegTranscoderUtils.calculateDownsampleNumerator(8));
        Assert.assertEquals(1, JpegTranscoderUtils.calculateDownsampleNumerator(16));
        Assert.assertEquals(1, JpegTranscoderUtils.calculateDownsampleNumerator(32));
    }

    @Test
    public void testResizeRatio() {
        ResizeOptions resizeOptions = new ResizeOptions(512, 512);
        Assert.assertEquals(0.5F, JpegTranscoderUtils.determineResizeRatio(resizeOptions, 1024, 1024), 0.01);
        Assert.assertEquals(0.25F, JpegTranscoderUtils.determineResizeRatio(resizeOptions, 2048, 4096), 0.01);
        Assert.assertEquals(0.5F, JpegTranscoderUtils.determineResizeRatio(resizeOptions, 4096, 512), 0.01);
    }
}

