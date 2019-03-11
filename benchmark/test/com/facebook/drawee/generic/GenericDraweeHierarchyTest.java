/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.drawee.generic;


import FadeDrawable.TRANSITION_NONE;
import FadeDrawable.TRANSITION_STARTING;
import ScaleType.CENTER;
import ScaleType.CENTER_CROP;
import ScaleType.CENTER_INSIDE;
import ScaleType.FIT_CENTER;
import ScaleType.FIT_XY;
import ScaleType.FOCUS_CROP;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import com.facebook.drawee.drawable.AndroidGraphicsTestUtils;
import com.facebook.drawee.drawable.DrawableTestUtils;
import com.facebook.drawee.drawable.FadeDrawable;
import com.facebook.drawee.drawable.ForwardingDrawable;
import com.facebook.drawee.drawable.Rounded;
import com.facebook.drawee.drawable.RoundedBitmapDrawable;
import com.facebook.drawee.drawable.RoundedCornersDrawable;
import com.facebook.drawee.drawable.ScaleTypeDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import static com.facebook.drawee.drawable.ScalingUtils.ScaleType.CENTER;
import static com.facebook.drawee.drawable.ScalingUtils.ScaleType.FOCUS_CROP;


@RunWith(RobolectricTestRunner.class)
public class GenericDraweeHierarchyTest {
    private GenericDraweeHierarchyBuilder mBuilder;

    private Drawable mBackground;

    private Drawable mOverlay1;

    private Drawable mOverlay2;

    private BitmapDrawable mPlaceholderImage;

    private BitmapDrawable mFailureImage;

    private BitmapDrawable mRetryImage;

    private BitmapDrawable mProgressBarImage;

    private BitmapDrawable mActualImage1;

    private BitmapDrawable mActualImage2;

    private ColorDrawable mWrappedLeaf;

    private ForwardingDrawable mWrappedImage;

    private PointF mFocusPoint;

    @Test
    public void testHierarchy_WithScaleType() throws Exception {
        GenericDraweeHierarchy dh = mBuilder.setPlaceholderImage(mPlaceholderImage, CENTER).setRetryImage(mRetryImage, FIT_CENTER).setFailureImage(mFailureImage, CENTER_INSIDE).setProgressBarImage(mProgressBarImage, CENTER).setActualImageScaleType(FOCUS_CROP).setActualImageFocusPoint(mFocusPoint).build();
        RootDrawable rootDrawable = ((RootDrawable) (dh.getTopLevelDrawable()));
        FadeDrawable fadeDrawable = ((FadeDrawable) (rootDrawable.getCurrent()));
        Assert.assertEquals(7, fadeDrawable.getNumberOfLayers());
        Assert.assertNull(fadeDrawable.getDrawable(0));
        assertScaleTypeAndDrawable(mPlaceholderImage, CENTER, fadeDrawable.getDrawable(1));
        assertActualImageScaleType(FOCUS_CROP, mFocusPoint, fadeDrawable.getDrawable(2));
        assertScaleTypeAndDrawable(mProgressBarImage, CENTER, fadeDrawable.getDrawable(3));
        assertScaleTypeAndDrawable(mRetryImage, FIT_CENTER, fadeDrawable.getDrawable(4));
        assertScaleTypeAndDrawable(mFailureImage, CENTER_INSIDE, fadeDrawable.getDrawable(5));
        Assert.assertNull(fadeDrawable.getDrawable(6));
        verifyCallback(rootDrawable, mPlaceholderImage);
    }

    @Test
    public void testHierarchy_NoScaleTypeNorMatrix() throws Exception {
        GenericDraweeHierarchy dh = mBuilder.setPlaceholderImage(mPlaceholderImage, null).setRetryImage(mRetryImage, null).setFailureImage(mFailureImage, null).setProgressBarImage(mProgressBarImage, null).setActualImageScaleType(null).build();
        RootDrawable rootDrawable = ((RootDrawable) (dh.getTopLevelDrawable()));
        FadeDrawable fadeDrawable = ((FadeDrawable) (rootDrawable.getCurrent()));
        Assert.assertEquals(7, fadeDrawable.getNumberOfLayers());
        Assert.assertNull(fadeDrawable.getDrawable(0));
        Assert.assertSame(mPlaceholderImage, fadeDrawable.getDrawable(1));
        Assert.assertSame(ForwardingDrawable.class, fadeDrawable.getDrawable(2).getClass());
        Assert.assertSame(mProgressBarImage, fadeDrawable.getDrawable(3));
        Assert.assertSame(mRetryImage, fadeDrawable.getDrawable(4));
        Assert.assertSame(mFailureImage, fadeDrawable.getDrawable(5));
        Assert.assertNull(fadeDrawable.getDrawable(6));
        verifyCallback(rootDrawable, mPlaceholderImage);
    }

    @Test
    public void testHierarchy_NoBranches() throws Exception {
        GenericDraweeHierarchy dh = mBuilder.build();
        RootDrawable rootDrawable = ((RootDrawable) (dh.getTopLevelDrawable()));
        FadeDrawable fadeDrawable = ((FadeDrawable) (rootDrawable.getCurrent()));
        Assert.assertEquals(7, fadeDrawable.getNumberOfLayers());
        Assert.assertNull(fadeDrawable.getDrawable(0));
        Assert.assertNull(fadeDrawable.getDrawable(1));
        assertActualImageScaleType(CENTER_CROP, null, fadeDrawable.getDrawable(2));
        Assert.assertNull(fadeDrawable.getDrawable(3));
        Assert.assertNull(fadeDrawable.getDrawable(4));
        Assert.assertNull(fadeDrawable.getDrawable(5));
        Assert.assertNull(fadeDrawable.getDrawable(6));
        verifyCallback(rootDrawable, fadeDrawable);
    }

    @Test
    public void testHierarchy_WithPlaceholderImage() throws Exception {
        GenericDraweeHierarchy dh = mBuilder.setPlaceholderImage(mPlaceholderImage, CENTER).build();
        RootDrawable rootDrawable = ((RootDrawable) (dh.getTopLevelDrawable()));
        FadeDrawable fadeDrawable = ((FadeDrawable) (rootDrawable.getCurrent()));
        assertScaleTypeAndDrawable(mPlaceholderImage, CENTER, fadeDrawable.getDrawable(1));
        verifyCallback(rootDrawable, mPlaceholderImage);
    }

    @Test
    public void testHierarchy_WithFailureImage() throws Exception {
        GenericDraweeHierarchy dh = mBuilder.setFailureImage(mFailureImage, CENTER).build();
        RootDrawable rootDrawable = ((RootDrawable) (dh.getTopLevelDrawable()));
        FadeDrawable fadeDrawable = ((FadeDrawable) (rootDrawable.getCurrent()));
        assertScaleTypeAndDrawable(mFailureImage, CENTER, fadeDrawable.getDrawable(5));
        verifyCallback(rootDrawable, mFailureImage);
    }

    @Test
    public void testHierarchy_WithRetryImage() throws Exception {
        GenericDraweeHierarchy dh = mBuilder.setRetryImage(mRetryImage, CENTER).build();
        RootDrawable rootDrawable = ((RootDrawable) (dh.getTopLevelDrawable()));
        FadeDrawable fadeDrawable = ((FadeDrawable) (rootDrawable.getCurrent()));
        assertScaleTypeAndDrawable(mRetryImage, CENTER, fadeDrawable.getDrawable(4));
        verifyCallback(rootDrawable, mRetryImage);
    }

    @Test
    public void testHierarchy_WithProgressBarImage() throws Exception {
        GenericDraweeHierarchy dh = mBuilder.setProgressBarImage(mProgressBarImage, CENTER).build();
        RootDrawable rootDrawable = ((RootDrawable) (dh.getTopLevelDrawable()));
        FadeDrawable fadeDrawable = ((FadeDrawable) (rootDrawable.getCurrent()));
        assertScaleTypeAndDrawable(mProgressBarImage, CENTER, fadeDrawable.getDrawable(3));
        verifyCallback(rootDrawable, mProgressBarImage);
    }

    @Test
    public void testHierarchy_WithAllBranches() throws Exception {
        GenericDraweeHierarchy dh = mBuilder.setPlaceholderImage(mPlaceholderImage, CENTER).setRetryImage(mRetryImage, FIT_CENTER).setFailureImage(mFailureImage, FIT_CENTER).setProgressBarImage(mProgressBarImage, CENTER).setActualImageScaleType(CENTER_CROP).build();
        RootDrawable rootDrawable = ((RootDrawable) (dh.getTopLevelDrawable()));
        FadeDrawable fadeDrawable = ((FadeDrawable) (rootDrawable.getCurrent()));
        Assert.assertEquals(7, fadeDrawable.getNumberOfLayers());
        Assert.assertNull(fadeDrawable.getDrawable(0));
        assertScaleTypeAndDrawable(mPlaceholderImage, CENTER, fadeDrawable.getDrawable(1));
        assertActualImageScaleType(CENTER_CROP, null, fadeDrawable.getDrawable(2));
        assertScaleTypeAndDrawable(mProgressBarImage, CENTER, fadeDrawable.getDrawable(3));
        assertScaleTypeAndDrawable(mRetryImage, FIT_CENTER, fadeDrawable.getDrawable(4));
        assertScaleTypeAndDrawable(mFailureImage, FIT_CENTER, fadeDrawable.getDrawable(5));
        Assert.assertNull(fadeDrawable.getDrawable(6));
        verifyCallback(rootDrawable, mPlaceholderImage);
    }

    @Test
    public void testHierarchy_WithBackground() throws Exception {
        GenericDraweeHierarchy dh = mBuilder.setBackground(mBackground).build();
        RootDrawable rootDrawable = ((RootDrawable) (dh.getTopLevelDrawable()));
        FadeDrawable fadeDrawable = ((FadeDrawable) (rootDrawable.getCurrent()));
        Assert.assertEquals(7, fadeDrawable.getNumberOfLayers());
        Assert.assertSame(mBackground, fadeDrawable.getDrawable(0));
        verifyCallback(rootDrawable, mBackground);
    }

    @Test
    public void testHierarchy_WithOverlays() throws Exception {
        GenericDraweeHierarchy dh = mBuilder.setOverlays(Arrays.asList(mOverlay1, mOverlay2)).build();
        RootDrawable rootDrawable = ((RootDrawable) (dh.getTopLevelDrawable()));
        FadeDrawable fadeDrawable = ((FadeDrawable) (rootDrawable.getCurrent()));
        Assert.assertEquals(8, fadeDrawable.getNumberOfLayers());
        Assert.assertSame(mOverlay1, fadeDrawable.getDrawable(6));
        Assert.assertSame(mOverlay2, fadeDrawable.getDrawable(7));
        verifyCallback(rootDrawable, mOverlay1);
        verifyCallback(rootDrawable, mOverlay2);
    }

    @Test
    public void testHierarchy_WithSingleOverlay() throws Exception {
        GenericDraweeHierarchy dh = mBuilder.setPlaceholderImage(mPlaceholderImage, null).setOverlay(mOverlay1).build();
        RootDrawable rootDrawable = ((RootDrawable) (dh.getTopLevelDrawable()));
        FadeDrawable fadeDrawable = ((FadeDrawable) (rootDrawable.getCurrent()));
        Assert.assertEquals(7, fadeDrawable.getNumberOfLayers());
        Assert.assertSame(mOverlay1, fadeDrawable.getDrawable(6));
        verifyCallback(rootDrawable, mOverlay1);
    }

    @Test
    public void testHierarchy_WithBackgroundAndSingleOverlay() throws Exception {
        GenericDraweeHierarchy dh = mBuilder.setBackground(mBackground).setOverlay(mOverlay2).build();
        RootDrawable rootDrawable = ((RootDrawable) (dh.getTopLevelDrawable()));
        FadeDrawable fadeDrawable = ((FadeDrawable) (rootDrawable.getCurrent()));
        Assert.assertEquals(7, fadeDrawable.getNumberOfLayers());
        Assert.assertSame(mBackground, fadeDrawable.getDrawable(0));
        Assert.assertSame(mOverlay2, fadeDrawable.getDrawable(6));
        verifyCallback(rootDrawable, mBackground);
        verifyCallback(rootDrawable, mOverlay2);
    }

    @Test
    public void testHierarchy_WithBackgroundAndMultipleOverlays() throws Exception {
        GenericDraweeHierarchy dh = mBuilder.setPlaceholderImage(mPlaceholderImage, CENTER).setRetryImage(mRetryImage, FIT_CENTER).setFailureImage(mFailureImage, FIT_CENTER).setProgressBarImage(mProgressBarImage, CENTER).setActualImageScaleType(CENTER_CROP).setBackground(mBackground).setOverlays(Arrays.asList(mOverlay1, mOverlay2)).build();
        RootDrawable rootDrawable = ((RootDrawable) (dh.getTopLevelDrawable()));
        FadeDrawable fadeDrawable = ((FadeDrawable) (rootDrawable.getCurrent()));
        Assert.assertEquals(8, fadeDrawable.getNumberOfLayers());
        Assert.assertSame(mBackground, fadeDrawable.getDrawable(0));
        assertScaleTypeAndDrawable(mPlaceholderImage, CENTER, fadeDrawable.getDrawable(1));
        assertActualImageScaleType(CENTER_CROP, null, fadeDrawable.getDrawable(2));
        assertScaleTypeAndDrawable(mProgressBarImage, CENTER, fadeDrawable.getDrawable(3));
        assertScaleTypeAndDrawable(mRetryImage, FIT_CENTER, fadeDrawable.getDrawable(4));
        assertScaleTypeAndDrawable(mFailureImage, FIT_CENTER, fadeDrawable.getDrawable(5));
        Assert.assertSame(mOverlay1, fadeDrawable.getDrawable(6));
        Assert.assertSame(mOverlay2, fadeDrawable.getDrawable(7));
        verifyCallback(rootDrawable, mBackground);
        verifyCallback(rootDrawable, mPlaceholderImage);
        verifyCallback(rootDrawable, mOverlay2);
    }

    @Test
    public void testHierarchy_WithPressedStateOverlay() throws Exception {
        GenericDraweeHierarchy dh = mBuilder.setOverlay(mOverlay2).setPressedStateOverlay(mOverlay1).build();
        RootDrawable rootDrawable = ((RootDrawable) (dh.getTopLevelDrawable()));
        FadeDrawable fadeDrawable = ((FadeDrawable) (rootDrawable.getCurrent()));
        Assert.assertEquals(8, fadeDrawable.getNumberOfLayers());
        Assert.assertSame(mOverlay2, fadeDrawable.getDrawable(6));
        StateListDrawable stateListDrawable = ((StateListDrawable) (fadeDrawable.getDrawable(7)));
        Assert.assertNotNull(stateListDrawable);
    }

    @Test
    public void testHierarchy_WithRoundedOverlayColor() throws Exception {
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(10).setOverlayColor(-1);
        GenericDraweeHierarchy dh = mBuilder.setRoundingParams(roundingParams).build();
        RootDrawable rootDrawable = ((RootDrawable) (dh.getTopLevelDrawable()));
        RoundedCornersDrawable roundedDrawable = ((RoundedCornersDrawable) (rootDrawable.getCurrent()));
        assertRoundingParams(roundingParams, roundedDrawable);
        Assert.assertEquals(roundingParams.getOverlayColor(), roundedDrawable.getOverlayColor());
        FadeDrawable fadeDrawable = ((FadeDrawable) (roundedDrawable.getCurrent()));
        Assert.assertNotNull(fadeDrawable);
        verifyCallback(rootDrawable, fadeDrawable);
    }

    @Test
    public void testHierarchy_WithRoundedLeafs() throws Exception {
        RoundingParams roundingParams = RoundingParams.asCircle();
        GenericDraweeHierarchy dh = mBuilder.setPlaceholderImage(mWrappedImage, CENTER).setFailureImage(mFailureImage, CENTER).setRetryImage(mRetryImage, null).setRoundingParams(roundingParams).build();
        RootDrawable rootDrawable = ((RootDrawable) (dh.getTopLevelDrawable()));
        FadeDrawable fadeDrawable = ((FadeDrawable) (rootDrawable.getCurrent()));
        Assert.assertNotNull(fadeDrawable);
        assertScaleTypeAndDrawable(mWrappedImage, CENTER, fadeDrawable.getDrawable(1));
        Rounded roundedPlaceholder = ((Rounded) (mWrappedImage.getCurrent().getCurrent()));
        assertRoundingParams(roundingParams, roundedPlaceholder);
        Rounded roundedFailureImage = ((Rounded) (fadeDrawable.getDrawable(5).getCurrent()));
        assertRoundingParams(roundingParams, roundedFailureImage);
        Rounded roundedRetryImage = ((Rounded) (fadeDrawable.getDrawable(4)));
        assertRoundingParams(roundingParams, roundedRetryImage);
        verifyCallback(rootDrawable, mWrappedImage.getCurrent().getCurrent());
        verifyCallback(rootDrawable, ((Drawable) (roundedFailureImage)));
        verifyCallback(rootDrawable, ((Drawable) (roundedRetryImage)));
    }

    @Test
    public void testControlling_WithPlaceholderOnly() throws Exception {
        GenericDraweeHierarchy dh = mBuilder.setPlaceholderImage(mPlaceholderImage, null).setActualImageScaleType(null).setFadeDuration(250).build();
        // image indexes in DH tree
        final int placeholderImageIndex = 1;
        final int actualImageIndex = 2;
        FadeDrawable fadeDrawable = ((FadeDrawable) (dh.getTopLevelDrawable().getCurrent()));
        Assert.assertEquals(mPlaceholderImage, fadeDrawable.getDrawable(placeholderImageIndex));
        Assert.assertEquals(ForwardingDrawable.class, fadeDrawable.getDrawable(actualImageIndex).getClass());
        ForwardingDrawable actualImageSettableDrawable = ((ForwardingDrawable) (fadeDrawable.getDrawable(actualImageIndex)));
        // initial state -> final image (non-immediate)
        // initial state
        Assert.assertEquals(ColorDrawable.class, actualImageSettableDrawable.getCurrent().getClass());
        Assert.assertEquals(true, fadeDrawable.isLayerOn(placeholderImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(actualImageIndex));
        Assert.assertEquals(TRANSITION_NONE, fadeDrawable.getTransitionState());
        // set final image (non-immediate)
        dh.setImage(mActualImage1, 1.0F, false);
        Assert.assertEquals(mActualImage1, actualImageSettableDrawable.getCurrent());
        Assert.assertEquals(false, fadeDrawable.isLayerOn(placeholderImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(actualImageIndex));
        Assert.assertEquals(TRANSITION_STARTING, fadeDrawable.getTransitionState());
        Assert.assertEquals(250, fadeDrawable.getTransitionDuration());
        // initial state -> final image (immediate)
        // reset hierarchy to initial state
        dh.reset();
        Assert.assertEquals(ColorDrawable.class, actualImageSettableDrawable.getCurrent().getClass());
        Assert.assertEquals(true, fadeDrawable.isLayerOn(placeholderImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(actualImageIndex));
        Assert.assertEquals(TRANSITION_NONE, fadeDrawable.getTransitionState());
        // set final image (immediate)
        dh.setImage(mActualImage2, 1.0F, true);
        Assert.assertEquals(mActualImage2, actualImageSettableDrawable.getCurrent());
        Assert.assertEquals(false, fadeDrawable.isLayerOn(placeholderImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(actualImageIndex));
        Assert.assertEquals(TRANSITION_NONE, fadeDrawable.getTransitionState());
        // initial state -> retry
        // reset hierarchy to initial state
        dh.reset();
        Assert.assertEquals(ColorDrawable.class, actualImageSettableDrawable.getCurrent().getClass());
        Assert.assertEquals(true, fadeDrawable.isLayerOn(placeholderImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(actualImageIndex));
        Assert.assertEquals(TRANSITION_NONE, fadeDrawable.getTransitionState());
        // set retry
        dh.setRetry(new RuntimeException());
        Assert.assertEquals(ColorDrawable.class, actualImageSettableDrawable.getCurrent().getClass());
        Assert.assertEquals(true, fadeDrawable.isLayerOn(placeholderImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(actualImageIndex));
        Assert.assertEquals(TRANSITION_STARTING, fadeDrawable.getTransitionState());
        Assert.assertEquals(250, fadeDrawable.getTransitionDuration());
        // initial state -> failure
        // reset hierarchy to initial state
        dh.reset();
        Assert.assertEquals(ColorDrawable.class, actualImageSettableDrawable.getCurrent().getClass());
        Assert.assertEquals(true, fadeDrawable.isLayerOn(placeholderImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(actualImageIndex));
        Assert.assertEquals(TRANSITION_NONE, fadeDrawable.getTransitionState());
        // set failure
        dh.setFailure(new RuntimeException());
        Assert.assertEquals(ColorDrawable.class, actualImageSettableDrawable.getCurrent().getClass());
        Assert.assertEquals(true, fadeDrawable.isLayerOn(placeholderImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(actualImageIndex));
        Assert.assertEquals(TRANSITION_STARTING, fadeDrawable.getTransitionState());
        Assert.assertEquals(250, fadeDrawable.getTransitionDuration());
    }

    @Test
    public void testControlling_WithAllLayers() throws Exception {
        GenericDraweeHierarchy dh = mBuilder.setBackground(mBackground).setOverlays(Arrays.asList(mOverlay1, mOverlay2)).setPlaceholderImage(mPlaceholderImage, null).setRetryImage(mRetryImage, null).setFailureImage(mFailureImage, null).setProgressBarImage(mProgressBarImage, null).setActualImageScaleType(null).setFadeDuration(250).build();
        // image indexes in DH tree
        final int backgroundIndex = 0;
        final int placeholderImageIndex = 1;
        final int actualImageIndex = 2;
        final int progressBarImageIndex = 3;
        final int retryImageIndex = 4;
        final int failureImageIndex = 5;
        final int overlaysIndex = 6;
        int numOverlays = 2;
        FadeDrawable fadeDrawable = ((FadeDrawable) (dh.getTopLevelDrawable().getCurrent()));
        Assert.assertEquals(mPlaceholderImage, fadeDrawable.getDrawable(placeholderImageIndex));
        Assert.assertEquals(mProgressBarImage, fadeDrawable.getDrawable(progressBarImageIndex));
        Assert.assertEquals(mRetryImage, fadeDrawable.getDrawable(retryImageIndex));
        Assert.assertEquals(mFailureImage, fadeDrawable.getDrawable(failureImageIndex));
        Assert.assertEquals(ForwardingDrawable.class, fadeDrawable.getDrawable(actualImageIndex).getClass());
        ForwardingDrawable finalImageSettableDrawable = ((ForwardingDrawable) (fadeDrawable.getDrawable(actualImageIndex)));
        // initial state -> final image (immediate)
        // initial state, show progress bar
        Assert.assertEquals(false, fadeDrawable.isLayerOn(progressBarImageIndex));
        dh.setProgress(0.0F, true);
        Assert.assertEquals(ColorDrawable.class, finalImageSettableDrawable.getCurrent().getClass());
        Assert.assertEquals(true, fadeDrawable.isLayerOn(placeholderImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(actualImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(progressBarImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(retryImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(failureImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(backgroundIndex));
        assertLayersOn(fadeDrawable, overlaysIndex, numOverlays);
        Assert.assertEquals(TRANSITION_NONE, fadeDrawable.getTransitionState());
        // set final image (immediate)
        dh.setImage(mActualImage2, 1.0F, true);
        Assert.assertEquals(mActualImage2, finalImageSettableDrawable.getCurrent());
        Assert.assertEquals(false, fadeDrawable.isLayerOn(placeholderImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(actualImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(progressBarImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(retryImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(failureImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(backgroundIndex));
        assertLayersOn(fadeDrawable, overlaysIndex, numOverlays);
        Assert.assertEquals(TRANSITION_NONE, fadeDrawable.getTransitionState());
        // initial state -> final image (non-immediate)
        // reset hierarchy to initial state, show progress bar
        dh.reset();
        Assert.assertEquals(false, fadeDrawable.isLayerOn(progressBarImageIndex));
        dh.setProgress(0.0F, true);
        Assert.assertEquals(ColorDrawable.class, finalImageSettableDrawable.getCurrent().getClass());
        Assert.assertEquals(true, fadeDrawable.isLayerOn(placeholderImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(actualImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(progressBarImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(retryImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(failureImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(backgroundIndex));
        assertLayersOn(fadeDrawable, overlaysIndex, numOverlays);
        Assert.assertEquals(TRANSITION_NONE, fadeDrawable.getTransitionState());
        // set final image (non-immediate)
        dh.setImage(mActualImage2, 1.0F, false);
        Assert.assertEquals(mActualImage2, finalImageSettableDrawable.getCurrent());
        Assert.assertEquals(false, fadeDrawable.isLayerOn(placeholderImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(actualImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(progressBarImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(retryImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(failureImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(backgroundIndex));
        assertLayersOn(fadeDrawable, overlaysIndex, numOverlays);
        Assert.assertEquals(TRANSITION_STARTING, fadeDrawable.getTransitionState());
        Assert.assertEquals(250, fadeDrawable.getTransitionDuration());
        // initial state -> temporary image (immediate) -> final image (non-immediate)
        // reset hierarchy to initial state, show progress bar
        dh.reset();
        Assert.assertEquals(false, fadeDrawable.isLayerOn(progressBarImageIndex));
        dh.setProgress(0.0F, true);
        Assert.assertEquals(ColorDrawable.class, finalImageSettableDrawable.getCurrent().getClass());
        Assert.assertEquals(true, fadeDrawable.isLayerOn(placeholderImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(actualImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(progressBarImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(retryImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(failureImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(backgroundIndex));
        assertLayersOn(fadeDrawable, overlaysIndex, numOverlays);
        Assert.assertEquals(TRANSITION_NONE, fadeDrawable.getTransitionState());
        // set temporary image (immediate)
        dh.setImage(mActualImage1, 0.5F, true);
        Assert.assertEquals(mActualImage1, finalImageSettableDrawable.getCurrent());
        Assert.assertEquals(false, fadeDrawable.isLayerOn(placeholderImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(actualImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(progressBarImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(retryImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(failureImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(backgroundIndex));
        assertLayersOn(fadeDrawable, overlaysIndex, numOverlays);
        Assert.assertEquals(TRANSITION_NONE, fadeDrawable.getTransitionState());
        // set final image (non-immediate)
        dh.setImage(mActualImage2, 1.0F, false);
        Assert.assertEquals(mActualImage2, finalImageSettableDrawable.getCurrent());
        Assert.assertEquals(false, fadeDrawable.isLayerOn(placeholderImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(actualImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(progressBarImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(retryImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(failureImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(backgroundIndex));
        assertLayersOn(fadeDrawable, overlaysIndex, numOverlays);
        Assert.assertEquals(TRANSITION_STARTING, fadeDrawable.getTransitionState());
        Assert.assertEquals(250, fadeDrawable.getTransitionDuration());
        // initial state -> temporary image (non-immediate) -> final image (non-immediate)
        // reset hierarchy to initial state, show progress bar
        dh.reset();
        Assert.assertEquals(false, fadeDrawable.isLayerOn(progressBarImageIndex));
        dh.setProgress(0.0F, true);
        Assert.assertEquals(ColorDrawable.class, finalImageSettableDrawable.getCurrent().getClass());
        Assert.assertEquals(true, fadeDrawable.isLayerOn(placeholderImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(actualImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(progressBarImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(retryImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(failureImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(backgroundIndex));
        assertLayersOn(fadeDrawable, overlaysIndex, numOverlays);
        Assert.assertEquals(TRANSITION_NONE, fadeDrawable.getTransitionState());
        // set temporary image (non-immediate)
        dh.setImage(mActualImage1, 0.5F, false);
        Assert.assertEquals(mActualImage1, finalImageSettableDrawable.getCurrent());
        Assert.assertEquals(false, fadeDrawable.isLayerOn(placeholderImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(actualImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(progressBarImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(retryImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(failureImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(backgroundIndex));
        assertLayersOn(fadeDrawable, overlaysIndex, numOverlays);
        Assert.assertEquals(TRANSITION_STARTING, fadeDrawable.getTransitionState());
        Assert.assertEquals(250, fadeDrawable.getTransitionDuration());
        // set final image (non-immediate)
        dh.setImage(mActualImage2, 1.0F, false);
        Assert.assertEquals(mActualImage2, finalImageSettableDrawable.getCurrent());
        Assert.assertEquals(false, fadeDrawable.isLayerOn(placeholderImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(actualImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(progressBarImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(retryImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(failureImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(backgroundIndex));
        assertLayersOn(fadeDrawable, overlaysIndex, numOverlays);
        Assert.assertEquals(TRANSITION_STARTING, fadeDrawable.getTransitionState());
        Assert.assertEquals(250, fadeDrawable.getTransitionDuration());
        // initial state -> temporary image (immediate) -> retry
        // reset hierarchy to initial state, show progress bar
        dh.reset();
        Assert.assertEquals(false, fadeDrawable.isLayerOn(progressBarImageIndex));
        dh.setProgress(0.0F, true);
        Assert.assertEquals(ColorDrawable.class, finalImageSettableDrawable.getCurrent().getClass());
        Assert.assertEquals(true, fadeDrawable.isLayerOn(placeholderImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(actualImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(progressBarImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(retryImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(failureImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(backgroundIndex));
        assertLayersOn(fadeDrawable, overlaysIndex, numOverlays);
        Assert.assertEquals(TRANSITION_NONE, fadeDrawable.getTransitionState());
        // set temporary image (immediate)
        dh.setImage(mActualImage1, 0.5F, true);
        Assert.assertEquals(mActualImage1, finalImageSettableDrawable.getCurrent());
        Assert.assertEquals(false, fadeDrawable.isLayerOn(placeholderImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(actualImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(progressBarImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(retryImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(failureImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(backgroundIndex));
        assertLayersOn(fadeDrawable, overlaysIndex, numOverlays);
        Assert.assertEquals(TRANSITION_NONE, fadeDrawable.getTransitionState());
        // set retry
        dh.setRetry(new RuntimeException());
        Assert.assertEquals(mActualImage1, finalImageSettableDrawable.getCurrent());
        Assert.assertEquals(false, fadeDrawable.isLayerOn(placeholderImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(actualImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(progressBarImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(retryImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(failureImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(backgroundIndex));
        assertLayersOn(fadeDrawable, overlaysIndex, numOverlays);
        Assert.assertEquals(TRANSITION_STARTING, fadeDrawable.getTransitionState());
        Assert.assertEquals(250, fadeDrawable.getTransitionDuration());
        // initial state -> temporary image (immediate) -> failure
        // reset hierarchy to initial state, show progress bar
        dh.reset();
        Assert.assertEquals(false, fadeDrawable.isLayerOn(progressBarImageIndex));
        dh.setProgress(0.0F, true);
        Assert.assertEquals(ColorDrawable.class, finalImageSettableDrawable.getCurrent().getClass());
        Assert.assertEquals(true, fadeDrawable.isLayerOn(placeholderImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(actualImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(progressBarImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(retryImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(failureImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(backgroundIndex));
        assertLayersOn(fadeDrawable, overlaysIndex, numOverlays);
        Assert.assertEquals(TRANSITION_NONE, fadeDrawable.getTransitionState());
        // set temporary image (immediate)
        dh.setImage(mActualImage1, 0.5F, true);
        Assert.assertEquals(mActualImage1, finalImageSettableDrawable.getCurrent());
        Assert.assertEquals(false, fadeDrawable.isLayerOn(placeholderImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(actualImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(progressBarImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(retryImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(failureImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(backgroundIndex));
        assertLayersOn(fadeDrawable, overlaysIndex, numOverlays);
        Assert.assertEquals(TRANSITION_NONE, fadeDrawable.getTransitionState());
        // set failure
        dh.setFailure(new RuntimeException());
        Assert.assertEquals(mActualImage1, finalImageSettableDrawable.getCurrent());
        Assert.assertEquals(false, fadeDrawable.isLayerOn(placeholderImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(actualImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(progressBarImageIndex));
        Assert.assertEquals(false, fadeDrawable.isLayerOn(retryImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(failureImageIndex));
        Assert.assertEquals(true, fadeDrawable.isLayerOn(backgroundIndex));
        assertLayersOn(fadeDrawable, overlaysIndex, numOverlays);
        Assert.assertEquals(TRANSITION_STARTING, fadeDrawable.getTransitionState());
        Assert.assertEquals(250, fadeDrawable.getTransitionDuration());
    }

    @Test
    public void testControlling_WithCornerRadii() throws Exception {
        GenericDraweeHierarchy dh = mBuilder.setPlaceholderImage(mPlaceholderImage, null).setActualImageScaleType(null).setRoundingParams(RoundingParams.fromCornersRadius(10)).setFadeDuration(250).build();
        // actual image index in DH tree
        final int imageIndex = 2;
        FadeDrawable fadeDrawable = ((FadeDrawable) (dh.getTopLevelDrawable().getCurrent()));
        ForwardingDrawable settableDrawable = ((ForwardingDrawable) (fadeDrawable.getDrawable(imageIndex)));
        // set temporary image
        dh.setImage(mActualImage1, 0.5F, true);
        Assert.assertNotSame(mActualImage1, settableDrawable.getCurrent());
        Assert.assertEquals(RoundedBitmapDrawable.class, settableDrawable.getCurrent().getClass());
        Assert.assertEquals(true, fadeDrawable.isLayerOn(imageIndex));
        Assert.assertEquals(TRANSITION_NONE, fadeDrawable.getTransitionState());
        verifyCallback(dh.getTopLevelDrawable(), settableDrawable.getCurrent());
        // set final image
        dh.setImage(mActualImage2, 1.0F, false);
        Assert.assertNotSame(mActualImage2, settableDrawable.getCurrent());
        Assert.assertEquals(RoundedBitmapDrawable.class, settableDrawable.getCurrent().getClass());
        Assert.assertEquals(true, fadeDrawable.isLayerOn(imageIndex));
        Assert.assertEquals(TRANSITION_STARTING, fadeDrawable.getTransitionState());
        Assert.assertEquals(250, fadeDrawable.getTransitionDuration());
        verifyCallback(dh.getTopLevelDrawable(), settableDrawable.getCurrent());
    }

    @Test
    public void testControlling_WithControllerOverlay() {
        GenericDraweeHierarchy dh = mBuilder.setPlaceholderImage(mPlaceholderImage, null).setActualImageScaleType(null).setFadeDuration(250).build();
        RootDrawable rootDrawable = ((RootDrawable) (dh.getTopLevelDrawable()));
        // set controller overlay
        Drawable controllerOverlay = DrawableTestUtils.mockDrawable();
        dh.setControllerOverlay(controllerOverlay);
        Assert.assertSame(controllerOverlay, rootDrawable.mControllerOverlay);
        // clear controller overlay
        dh.setControllerOverlay(null);
        Assert.assertNull(rootDrawable.mControllerOverlay);
    }

    @Test
    public void testDrawVisibleDrawableOnly() {
        GenericDraweeHierarchy dh = mBuilder.setPlaceholderImage(mPlaceholderImage).build();
        Canvas mockCanvas = Mockito.mock(Canvas.class);
        dh.getTopLevelDrawable().setVisible(false, true);
        dh.getTopLevelDrawable().draw(mockCanvas);
        Mockito.verify(mPlaceholderImage, Mockito.never()).draw(mockCanvas);
        dh.getTopLevelDrawable().setVisible(true, true);
        dh.getTopLevelDrawable().draw(mockCanvas);
        Mockito.verify(mPlaceholderImage).draw(mockCanvas);
    }

    @Test
    public void testSetPlaceholderImage() throws Exception {
        final GenericDraweeHierarchy dh = mBuilder.setPlaceholderImage(mPlaceholderImage, FIT_XY).build();
        testSetDrawable(dh, 1, new GenericDraweeHierarchyTest.SetDrawableCallback() {
            @Override
            public void setDrawable(Drawable drawable) {
                dh.setPlaceholderImage(drawable);
            }

            @Override
            public void setDrawable(Drawable drawable, ScalingUtils.ScaleType scaleType) {
                dh.setPlaceholderImage(drawable, scaleType);
            }
        });
    }

    @Test
    public void testSetFailureImage() throws Exception {
        final GenericDraweeHierarchy dh = mBuilder.setFailureImage(mFailureImage, null).build();
        testSetDrawable(dh, 5, new GenericDraweeHierarchyTest.SetDrawableCallback() {
            @Override
            public void setDrawable(Drawable drawable) {
                dh.setFailureImage(drawable);
            }

            @Override
            public void setDrawable(Drawable drawable, ScalingUtils.ScaleType scaleType) {
                dh.setFailureImage(drawable, scaleType);
            }
        });
    }

    @Test
    public void testSetRetryImage() throws Exception {
        final GenericDraweeHierarchy dh = mBuilder.setRetryImage(mRetryImage, null).build();
        testSetDrawable(dh, 4, new GenericDraweeHierarchyTest.SetDrawableCallback() {
            @Override
            public void setDrawable(Drawable drawable) {
                dh.setRetryImage(drawable);
            }

            @Override
            public void setDrawable(Drawable drawable, ScalingUtils.ScaleType scaleType) {
                dh.setRetryImage(drawable, scaleType);
            }
        });
    }

    @Test
    public void testSetProgressBarImage() throws Exception {
        final GenericDraweeHierarchy dh = mBuilder.setProgressBarImage(mProgressBarImage, null).build();
        testSetDrawable(dh, 3, new GenericDraweeHierarchyTest.SetDrawableCallback() {
            @Override
            public void setDrawable(Drawable drawable) {
                dh.setProgressBarImage(drawable);
            }

            @Override
            public void setDrawable(Drawable drawable, ScalingUtils.ScaleType scaleType) {
                dh.setProgressBarImage(drawable, scaleType);
            }
        });
    }

    private interface SetDrawableCallback {
        void setDrawable(Drawable drawable);

        void setDrawable(Drawable drawable, ScalingUtils.ScaleType scaleType);
    }

    @Test
    public void testSetActualImageFocusPoint() {
        GenericDraweeHierarchy dh = mBuilder.setPlaceholderImage(mPlaceholderImage).setProgressBarImage(mProgressBarImage).setActualImageScaleType(FOCUS_CROP).build();
        // actual image index in DH tree
        final int imageIndex = 2;
        FadeDrawable fadeDrawable = ((FadeDrawable) (dh.getTopLevelDrawable().getCurrent()));
        ScaleTypeDrawable scaleTypeDrawable = ((ScaleTypeDrawable) (fadeDrawable.getDrawable(imageIndex)));
        Assert.assertNull(scaleTypeDrawable.getFocusPoint());
        PointF focus1 = new PointF(0.3F, 0.4F);
        dh.setActualImageFocusPoint(focus1);
        AndroidGraphicsTestUtils.assertEquals(focus1, scaleTypeDrawable.getFocusPoint(), 0.0F);
        PointF focus2 = new PointF(0.6F, 0.7F);
        dh.setActualImageFocusPoint(focus2);
        AndroidGraphicsTestUtils.assertEquals(focus2, scaleTypeDrawable.getFocusPoint(), 0.0F);
    }

    @Test
    public void testSetActualImageScaleType() {
        GenericDraweeHierarchy dh = mBuilder.setPlaceholderImage(mPlaceholderImage).build();
        // actual image index in DH tree
        final int imageIndex = 2;
        FadeDrawable fadeDrawable = ((FadeDrawable) (dh.getTopLevelDrawable().getCurrent()));
        ScaleTypeDrawable scaleTypeDrawable = ((ScaleTypeDrawable) (fadeDrawable.getDrawable(imageIndex)));
        ScalingUtils.ScaleType scaleType1 = FOCUS_CROP;
        dh.setActualImageScaleType(scaleType1);
        Assert.assertEquals(scaleType1, scaleTypeDrawable.getScaleType());
        ScalingUtils.ScaleType scaleType2 = CENTER;
        dh.setActualImageScaleType(scaleType2);
        Assert.assertEquals(scaleType2, scaleTypeDrawable.getScaleType());
    }

    @Test
    public void testSetRoundingParams_NoneToNone() {
        testSetRoundingParams_ToNoneFrom(null);
    }

    @Test
    public void testSetRoundingParams_OverlayToNone() {
        testSetRoundingParams_ToNoneFrom(RoundingParams.asCircle().setOverlayColor(305419896));
    }

    @Test
    public void testSetRoundingParams_RoundedLeafsToNone() {
        testSetRoundingParams_ToNoneFrom(RoundingParams.asCircle());
    }

    @Test
    public void testSetRoundingParams_NoneToOverlay() {
        testSetRoundingParams_ToOverlayFrom(null);
    }

    @Test
    public void testSetRoundingParams_OverlayToOverlay() {
        testSetRoundingParams_ToOverlayFrom(RoundingParams.asCircle().setOverlayColor(305419896));
    }

    @Test
    public void testSetRoundingParams_RoundedLeafsToOverlay() {
        testSetRoundingParams_ToOverlayFrom(RoundingParams.fromCornersRadius(10));
    }

    @Test
    public void testSetRoundingParams_NoneToRoundedLeafs() {
        testSetRoundingParams_ToRoundedLeafsFrom(null);
    }

    @Test
    public void testSetRoundingParams_OverlayToRoundedLeafs() {
        testSetRoundingParams_ToRoundedLeafsFrom(RoundingParams.asCircle().setOverlayColor(305419896));
    }

    @Test
    public void testSetRoundingParams_RoundedLeafsToRoundedLeafs() {
        testSetRoundingParams_ToRoundedLeafsFrom(RoundingParams.fromCornersRadius(10));
    }
}

