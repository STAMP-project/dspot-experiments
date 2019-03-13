/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.drawee.drawable;


import ScaleType.CENTER;
import ScaleType.CENTER_CROP;
import ScaleType.CENTER_INSIDE;
import ScaleType.FIT_BOTTOM_START;
import ScaleType.FIT_CENTER;
import ScaleType.FIT_END;
import ScaleType.FIT_START;
import ScaleType.FIT_XY;
import ScaleType.FOCUS_CROP;
import android.graphics.Matrix;
import android.graphics.Rect;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests for scale type calculations.
 */
@RunWith(RobolectricTestRunner.class)
public class ScalingUtilsTest {
    private final Matrix mExpectedMatrix = new Matrix();

    private final Matrix mActualMatrix = new Matrix();

    private final Rect mParentBounds = new Rect(10, 15, 410, 315);

    @Test
    public void testFitXY() {
        test(1.6F, 2.0F, 10, 15, 250, 150, FIT_XY);
        test(0.5F, 1.5F, 10, 15, 800, 200, FIT_XY);
        test(0.5F, 0.75F, 10, 15, 800, 400, FIT_XY);
        test(2.0F, 2.0F, 10, 15, 200, 150, FIT_XY);
        test(1.0F, 1.0F, 10, 15, 400, 300, FIT_XY);
        test(0.5F, 0.5F, 10, 15, 800, 600, FIT_XY);
        test(2.0F, 1.5F, 10, 15, 200, 200, FIT_XY);
        test(2.0F, 0.75F, 10, 15, 200, 400, FIT_XY);
        test(0.8F, 0.75F, 10, 15, 500, 400, FIT_XY);
    }

    @Test
    public void testFitStart() {
        test(1.6F, 1.6F, 10, 15, 250, 150, FIT_START);
        test(0.5F, 0.5F, 10, 15, 800, 200, FIT_START);
        test(0.5F, 0.5F, 10, 15, 800, 400, FIT_START);
        test(2.0F, 2.0F, 10, 15, 200, 150, FIT_START);
        test(1.0F, 1.0F, 10, 15, 400, 300, FIT_START);
        test(0.5F, 0.5F, 10, 15, 800, 600, FIT_START);
        test(1.5F, 1.5F, 10, 15, 200, 200, FIT_START);
        test(0.75F, 0.75F, 10, 15, 200, 400, FIT_START);
        test(0.75F, 0.75F, 10, 15, 500, 400, FIT_START);
    }

    @Test
    public void testFitCenter() {
        test(1.6F, 1.6F, 10, 45, 250, 150, FIT_CENTER);
        test(0.5F, 0.5F, 10, 115, 800, 200, FIT_CENTER);
        test(0.5F, 0.5F, 10, 65, 800, 400, FIT_CENTER);
        test(2.0F, 2.0F, 10, 15, 200, 150, FIT_CENTER);
        test(1.0F, 1.0F, 10, 15, 400, 300, FIT_CENTER);
        test(0.5F, 0.5F, 10, 15, 800, 600, FIT_CENTER);
        test(1.5F, 1.5F, 60, 15, 200, 200, FIT_CENTER);
        test(0.75F, 0.75F, 135, 15, 200, 400, FIT_CENTER);
        test(0.75F, 0.75F, 23, 15, 500, 400, FIT_CENTER);
    }

    @Test
    public void testFitEnd() {
        test(1.6F, 1.6F, 10, 75, 250, 150, FIT_END);
        test(0.5F, 0.5F, 10, 215, 800, 200, FIT_END);
        test(0.5F, 0.5F, 10, 115, 800, 400, FIT_END);
        test(2.0F, 2.0F, 10, 15, 200, 150, FIT_END);
        test(1.0F, 1.0F, 10, 15, 400, 300, FIT_END);
        test(0.5F, 0.5F, 10, 15, 800, 600, FIT_END);
        test(1.5F, 1.5F, 110, 15, 200, 200, FIT_END);
        test(0.75F, 0.75F, 260, 15, 200, 400, FIT_END);
        test(0.75F, 0.75F, 35, 15, 500, 400, FIT_END);
    }

    @Test
    public void testCenter() {
        test(1.0F, 1.0F, 85, 90, 250, 150, CENTER);
        test(1.0F, 1.0F, (-189), 65, 800, 200, CENTER);
        test(1.0F, 1.0F, (-189), (-34), 800, 400, CENTER);
        test(1.0F, 1.0F, 110, 90, 200, 150, CENTER);
        test(1.0F, 1.0F, 10, 15, 400, 300, CENTER);
        test(1.0F, 1.0F, (-189), (-134), 800, 600, CENTER);
        test(1.0F, 1.0F, 110, 65, 200, 200, CENTER);
        test(1.0F, 1.0F, 110, (-34), 200, 400, CENTER);
        test(1.0F, 1.0F, (-39), (-34), 500, 400, CENTER);
    }

    @Test
    public void testCenterInside() {
        test(1.0F, 1.0F, 85, 90, 250, 150, CENTER_INSIDE);
        test(0.5F, 0.5F, 10, 115, 800, 200, CENTER_INSIDE);
        test(0.5F, 0.5F, 10, 65, 800, 400, CENTER_INSIDE);
        test(1.0F, 1.0F, 110, 90, 200, 150, CENTER_INSIDE);
        test(1.0F, 1.0F, 10, 15, 400, 300, CENTER_INSIDE);
        test(0.5F, 0.5F, 10, 15, 800, 600, CENTER_INSIDE);
        test(1.0F, 1.0F, 110, 65, 200, 200, CENTER_INSIDE);
        test(0.75F, 0.75F, 135, 15, 200, 400, CENTER_INSIDE);
        test(0.75F, 0.75F, 23, 15, 500, 400, CENTER_INSIDE);
    }

    @Test
    public void testCenterCrop() {
        test(2.0F, 2.0F, (-39), 15, 250, 150, CENTER_CROP);
        test(1.5F, 1.5F, (-389), 15, 800, 200, CENTER_CROP);
        test(0.75F, 0.75F, (-89), 15, 800, 400, CENTER_CROP);
        test(2.0F, 2.0F, 10, 15, 200, 150, CENTER_CROP);
        test(1.0F, 1.0F, 10, 15, 400, 300, CENTER_CROP);
        test(0.5F, 0.5F, 10, 15, 800, 600, CENTER_CROP);
        test(2.0F, 2.0F, 10, (-34), 200, 200, CENTER_CROP);
        test(2.0F, 2.0F, 10, (-234), 200, 400, CENTER_CROP);
        test(0.8F, 0.8F, 10, 5, 500, 400, CENTER_CROP);
    }

    @Test
    public void testFocusCrop_DefaultFocus() {
        test(2.0F, 2.0F, (-39), 15, 250, 150, 0.5F, 0.5F, FOCUS_CROP);
        test(1.5F, 1.5F, (-389), 15, 800, 200, 0.5F, 0.5F, FOCUS_CROP);
        test(0.75F, 0.75F, (-89), 15, 800, 400, 0.5F, 0.5F, FOCUS_CROP);
        test(2.0F, 2.0F, 10, 15, 200, 150, 0.5F, 0.5F, FOCUS_CROP);
        test(1.0F, 1.0F, 10, 15, 400, 300, 0.5F, 0.5F, FOCUS_CROP);
        test(0.5F, 0.5F, 10, 15, 800, 600, 0.5F, 0.5F, FOCUS_CROP);
        test(2.0F, 2.0F, 10, (-34), 200, 200, 0.5F, 0.5F, FOCUS_CROP);
        test(2.0F, 2.0F, 10, (-234), 200, 400, 0.5F, 0.5F, FOCUS_CROP);
        test(0.8F, 0.8F, 10, 5, 500, 400, 0.5F, 0.5F, FOCUS_CROP);
    }

    @Test
    public void testFocusCrop_FocusCentered() {
        test(2.0F, 2.0F, (-14), 15, 250, 150, 0.45F, 0.55F, FOCUS_CROP);
        test(1.5F, 1.5F, (-329), 15, 800, 200, 0.45F, 0.55F, FOCUS_CROP);
        test(0.75F, 0.75F, (-59), 15, 800, 400, 0.45F, 0.55F, FOCUS_CROP);
        test(2.0F, 2.0F, 10, 15, 200, 150, 0.45F, 0.55F, FOCUS_CROP);
        test(1.0F, 1.0F, 10, 15, 400, 300, 0.45F, 0.55F, FOCUS_CROP);
        test(0.5F, 0.5F, 10, 15, 800, 600, 0.45F, 0.55F, FOCUS_CROP);
        test(2.0F, 2.0F, 10, (-54), 200, 200, 0.45F, 0.55F, FOCUS_CROP);
        test(2.0F, 2.0F, 10, (-274), 200, 400, 0.45F, 0.55F, FOCUS_CROP);
        test(0.8F, 0.8F, 10, 2, 500, 400, 0.45F, 0.51F, FOCUS_CROP);
    }

    @Test
    public void testFocusCrop_FocusTopLeft() {
        test(2.0F, 2.0F, 10, 15, 250, 150, 0.0F, 0.0F, FOCUS_CROP);
        test(1.5F, 1.5F, 10, 15, 800, 200, 0.0F, 0.0F, FOCUS_CROP);
        test(0.75F, 0.75F, 10, 15, 800, 400, 0.0F, 0.0F, FOCUS_CROP);
        test(2.0F, 2.0F, 10, 15, 200, 150, 0.0F, 0.0F, FOCUS_CROP);
        test(1.0F, 1.0F, 10, 15, 400, 300, 0.0F, 0.0F, FOCUS_CROP);
        test(0.5F, 0.5F, 10, 15, 800, 600, 0.0F, 0.0F, FOCUS_CROP);
        test(2.0F, 2.0F, 10, 15, 200, 200, 0.0F, 0.0F, FOCUS_CROP);
        test(2.0F, 2.0F, 10, 15, 200, 400, 0.0F, 0.0F, FOCUS_CROP);
        test(0.8F, 0.8F, 10, 15, 500, 400, 0.0F, 0.0F, FOCUS_CROP);
    }

    @Test
    public void testFocusCrop_FocusBottomRight() {
        test(2.0F, 2.0F, (-89), 15, 250, 150, 1.0F, 1.0F, FOCUS_CROP);
        test(1.5F, 1.5F, (-789), 15, 800, 200, 1.0F, 1.0F, FOCUS_CROP);
        test(0.75F, 0.75F, (-189), 15, 800, 400, 1.0F, 1.0F, FOCUS_CROP);
        test(2.0F, 2.0F, 10, 15, 200, 150, 1.0F, 1.0F, FOCUS_CROP);
        test(1.0F, 1.0F, 10, 15, 400, 300, 1.0F, 1.0F, FOCUS_CROP);
        test(0.5F, 0.5F, 10, 15, 800, 600, 1.0F, 1.0F, FOCUS_CROP);
        test(2.0F, 2.0F, 10, (-84), 200, 200, 1.0F, 1.0F, FOCUS_CROP);
        test(2.0F, 2.0F, 10, (-484), 200, 400, 1.0F, 1.0F, FOCUS_CROP);
        test(0.8F, 0.8F, 10, (-4), 500, 400, 1.0F, 1.0F, FOCUS_CROP);
    }

    @Test
    public void testFitBottomStart() {
        test(1.6F, 1.6F, 10, 75, 250, 150, FIT_BOTTOM_START);
        test(0.5F, 0.5F, 10, 215, 800, 200, FIT_BOTTOM_START);
        test(0.5F, 0.5F, 10, 115, 800, 400, FIT_BOTTOM_START);
        test(2.0F, 2.0F, 10, 15, 200, 150, FIT_BOTTOM_START);
        test(1.0F, 1.0F, 10, 15, 400, 300, FIT_BOTTOM_START);
        test(0.5F, 0.5F, 10, 15, 800, 600, FIT_BOTTOM_START);
        test(1.5F, 1.5F, 10, 15, 200, 200, FIT_BOTTOM_START);
        test(0.75F, 0.75F, 10, 15, 200, 400, FIT_BOTTOM_START);
        test(0.75F, 0.75F, 10, 15, 500, 400, FIT_BOTTOM_START);
    }
}

