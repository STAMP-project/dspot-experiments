/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.facebook.imagepipeline.filter;


import android.content.Context;
import android.graphics.Bitmap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static RenderScriptBlurFilter.BLUR_MAX_RADIUS;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class RenderScriptBlurFilterTest {
    @Mock
    private Bitmap srcBitmap;

    @Mock
    private Bitmap destBitmap;

    @Mock
    private Context mContext;

    @Test(expected = IllegalArgumentException.class)
    public void invalidNegativeRadiusBlur() {
        RenderScriptBlurFilter.blurBitmap(destBitmap, srcBitmap, mContext, (-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidZeroRadiusBlur() {
        RenderScriptBlurFilter.blurBitmap(destBitmap, srcBitmap, mContext, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidBigRadiusBlur() {
        RenderScriptBlurFilter.blurBitmap(destBitmap, srcBitmap, mContext, ((BLUR_MAX_RADIUS) + 1));
    }
}

