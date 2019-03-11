/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.drawee.debug;


import ScalingUtils.ScaleType.CENTER;
import ScalingUtils.ScaleType.CENTER_CROP;
import ScalingUtils.ScaleType.FIT_CENTER;
import ScalingUtils.ScaleType.FIT_XY;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import robolectric3.shadows.ShadowMatrix;


/**
 * Test cases for {@link DebugControllerOverlayDrawable} that are not included in the gradle build
 * as they depend on a working `ShadowMatrix` implementation.
 */
@Config(shadows = { ShadowMatrix.class })
@RunWith(RobolectricTestRunner.class)
public class DebugControllerOverlayDrawableInternalTest {
    DebugControllerOverlayDrawableTestHelper helper;

    @Test
    public void testOverlayWhenScaleTypeFitCenter() {
        helper.assertOverlayColorOk(100, 100, 100, 100, FIT_CENTER);
        helper.assertOverlayColorOk(100, 100, 1000, 100, FIT_CENTER);
        helper.assertOverlayColorOk(100, 100, 100, 1000, FIT_CENTER);
        helper.assertOverlayColorNotOk(100, 100, 1000, 1000, FIT_CENTER);
        helper.assertOverlayColorNotOk(100, 100, 10, 10, FIT_CENTER);
    }

    @Test
    public void testOverlayWhenScaleTypeFitXY() {
        helper.assertOverlayColorOk(100, 100, 100, 100, FIT_XY);
        helper.assertOverlayColorNotOk(100, 100, 1000, 100, FIT_XY);
        helper.assertOverlayColorNotOk(100, 100, 100, 1000, FIT_XY);
        helper.assertOverlayColorNotOk(100, 100, 1000, 1000, FIT_XY);
        helper.assertOverlayColorNotOk(100, 100, 10, 10, FIT_XY);
    }

    @Test
    public void testOverlayWhenScaleTypeCenter() {
        helper.assertOverlayColorOk(100, 100, 100, 100, CENTER);
        helper.assertOverlayColorOk(100, 100, 1000, 100, CENTER);
        helper.assertOverlayColorOk(100, 100, 100, 1000, CENTER);
        helper.assertOverlayColorOk(100, 100, 1000, 1000, CENTER);
        helper.assertOverlayColorNotOk(100, 100, 10, 10, CENTER);
    }

    @Test
    public void testOverlayWhenScaleTypeCenterCrop() {
        helper.assertOverlayColorOk(100, 100, 100, 100, CENTER_CROP);
        helper.assertOverlayColorNotOk(100, 100, 1000, 100, CENTER_CROP);
        helper.assertOverlayColorNotOk(100, 100, 100, 1000, CENTER_CROP);
        helper.assertOverlayColorNotOk(100, 100, 1000, 1000, CENTER_CROP);
        helper.assertOverlayColorNotOk(100, 100, 10, 10, CENTER_CROP);
    }
}

