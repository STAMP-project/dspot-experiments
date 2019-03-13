/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.core;


import android.net.Uri;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Some tests for ImagePipelineConfigTest
 */
@RunWith(RobolectricTestRunner.class)
public class ImagePipelineConfigTest {
    private Uri mUri;

    @Test
    public void testDefaultConfigIsFalseByDefault() {
        ImagePipelineConfig.resetDefaultRequestConfig();
        Assert.assertFalse(ImagePipelineConfig.getDefaultImageRequestConfig().isProgressiveRenderingEnabled());
    }

    @Test
    public void testDefaultConfigIsTrueIfChanged() {
        ImagePipelineConfig.resetDefaultRequestConfig();
        ImagePipelineConfig.getDefaultImageRequestConfig().setProgressiveRenderingEnabled(true);
        Assert.assertTrue(ImagePipelineConfig.getDefaultImageRequestConfig().isProgressiveRenderingEnabled());
    }

    @Test
    public void testImageRequestDefault() {
        ImagePipelineConfig.resetDefaultRequestConfig();
        final ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(mUri).build();
        Assert.assertFalse(imageRequest.getProgressiveRenderingEnabled());
    }

    @Test
    public void testImageRequestWhenChanged() {
        ImagePipelineConfig.resetDefaultRequestConfig();
        ImagePipelineConfig.getDefaultImageRequestConfig().setProgressiveRenderingEnabled(true);
        final ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(mUri).build();
        Assert.assertTrue(imageRequest.getProgressiveRenderingEnabled());
    }

    @Test
    public void testImageRequestWhenChangedAndOverriden() {
        ImagePipelineConfig.resetDefaultRequestConfig();
        final ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(mUri).setProgressiveRenderingEnabled(true).build();
        Assert.assertTrue(imageRequest.getProgressiveRenderingEnabled());
        final ImageRequest imageRequest2 = ImageRequestBuilder.newBuilderWithSource(mUri).setProgressiveRenderingEnabled(false).build();
        Assert.assertFalse(imageRequest2.getProgressiveRenderingEnabled());
    }
}

