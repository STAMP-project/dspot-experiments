/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.request;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;


@RunWith(ParameterizedRobolectricTestRunner.class)
public class ImageRequestBuilderCacheEnabledTest {
    private final String mUriScheme;

    private final boolean mExpectedDefaultDiskCacheEnabled;

    public ImageRequestBuilderCacheEnabledTest(String uriScheme, Boolean expectedDefaultDiskCacheEnabled) {
        mUriScheme = uriScheme;
        mExpectedDefaultDiskCacheEnabled = expectedDefaultDiskCacheEnabled;
    }

    @Test
    public void testIsDiskCacheEnabledByDefault() throws Exception {
        ImageRequestBuilder imageRequestBuilder = createBuilder();
        Assert.assertEquals(mExpectedDefaultDiskCacheEnabled, imageRequestBuilder.isDiskCacheEnabled());
    }

    @Test
    public void testIsDiskCacheDisabledIfRequested() throws Exception {
        ImageRequestBuilder imageRequestBuilder = createBuilder();
        imageRequestBuilder.disableDiskCache();
        Assert.assertEquals(false, imageRequestBuilder.isDiskCacheEnabled());
    }
}

