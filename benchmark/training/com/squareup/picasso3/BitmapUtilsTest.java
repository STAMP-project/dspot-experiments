/**
 * Copyright (C) 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.picasso3;


import Bitmap.Config;
import BitmapFactory.Options;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class BitmapUtilsTest {
    @Test
    public void bitmapConfig() {
        for (Bitmap.Config config : Config.values()) {
            Request data = build();
            Request copy = build();
            assertThat(BitmapUtils.createBitmapOptions(data).inPreferredConfig).isSameAs(config);
            assertThat(BitmapUtils.createBitmapOptions(copy).inPreferredConfig).isSameAs(config);
        }
    }

    @Test
    public void requiresComputeInSampleSize() {
        assertThat(BitmapUtils.requiresInSampleSize(null)).isFalse();
        final BitmapFactory.Options defaultOptions = new BitmapFactory.Options();
        assertThat(BitmapUtils.requiresInSampleSize(defaultOptions)).isFalse();
        final BitmapFactory.Options justBounds = new BitmapFactory.Options();
        justBounds.inJustDecodeBounds = true;
        assertThat(BitmapUtils.requiresInSampleSize(justBounds)).isTrue();
    }

    @Test
    public void calculateInSampleSizeNoResize() {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        Request data = build();
        BitmapUtils.calculateInSampleSize(100, 100, 150, 150, options, data);
        assertThat(options.inSampleSize).isEqualTo(1);
    }

    @Test
    public void calculateInSampleSizeResize() {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        Request data = build();
        BitmapUtils.calculateInSampleSize(100, 100, 200, 200, options, data);
        assertThat(options.inSampleSize).isEqualTo(2);
    }

    @Test
    public void calculateInSampleSizeResizeCenterInside() {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        Request data = build();
        BitmapUtils.calculateInSampleSize(data.targetWidth, data.targetHeight, 400, 200, options, data);
        assertThat(options.inSampleSize).isEqualTo(4);
    }

    @Test
    public void calculateInSampleSizeKeepAspectRatioWithWidth() {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        Request data = build();
        BitmapUtils.calculateInSampleSize(data.targetWidth, data.targetHeight, 800, 200, options, data);
        assertThat(options.inSampleSize).isEqualTo(2);
    }

    @Test
    public void calculateInSampleSizeKeepAspectRatioWithHeight() {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        Request data = build();
        BitmapUtils.calculateInSampleSize(data.targetWidth, data.targetHeight, 800, 200, options, data);
        assertThat(options.inSampleSize).isEqualTo(2);
    }

    @Test
    public void nullBitmapOptionsIfNoResizingOrPurgeable() {
        // No resize must return no bitmap options
        final Request noResize = build();
        final BitmapFactory.Options noResizeOptions = BitmapUtils.createBitmapOptions(noResize);
        assertThat(noResizeOptions).isNull();
    }

    @Test
    public void inJustDecodeBoundsIfResizing() {
        // Resize must return bitmap options with inJustDecodeBounds = true
        final Request requiresResize = build();
        final BitmapFactory.Options resizeOptions = BitmapUtils.createBitmapOptions(requiresResize);
        assertThat(resizeOptions).isNotNull();
        assertThat(resizeOptions.inJustDecodeBounds).isTrue();
        assertThat(resizeOptions.inPurgeable).isFalse();
        assertThat(resizeOptions.inInputShareable).isFalse();
    }

    @Test
    public void inPurgeableIfInPurgeable() {
        final Request request = build();
        final BitmapFactory.Options options = BitmapUtils.createBitmapOptions(request);
        assertThat(options).isNotNull();
        assertThat(options.inPurgeable).isTrue();
        assertThat(options.inInputShareable).isTrue();
        assertThat(options.inJustDecodeBounds).isFalse();
    }

    @Test
    public void createWithConfigAndNotInJustDecodeBoundsOrInPurgeable() {
        // Given a config, must return bitmap options and false inJustDecodeBounds/inPurgeable
        final Request config = build();
        final BitmapFactory.Options configOptions = BitmapUtils.createBitmapOptions(config);
        assertThat(configOptions).isNotNull();
        assertThat(configOptions.inJustDecodeBounds).isFalse();
        assertThat(configOptions.inPurgeable).isFalse();
        assertThat(configOptions.inInputShareable).isFalse();
    }
}

