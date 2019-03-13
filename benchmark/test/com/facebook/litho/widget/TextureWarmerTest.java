/**
 * Copyright 2014-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.litho.widget;


import TextureWarmer.WarmDrawable;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowLooper;


/**
 * Tests {@link TextureWarmer}.
 */
@RunWith(ComponentsTestRunner.class)
@Config(shadows = TextureWarmerTest.ShadowPicture.class)
public class TextureWarmerTest {
    private ShadowLooper mShadowLooper;

    private TextureWarmer mTextureWarmer;

    @Test
    public void testWarmGlyph() {
        Layout layout = Mockito.mock(Layout.class);
        mTextureWarmer.warmLayout(layout);
        mShadowLooper.runOneTask();
        Mockito.verify(layout).draw(ArgumentMatchers.any(Canvas.class));
    }

    @Test
    public void testWarmTexture() {
        Drawable drawable = Mockito.mock(Drawable.class);
        TextureWarmer.WarmDrawable warmDrawable = new TextureWarmer.WarmDrawable(drawable, 1, 1);
        mTextureWarmer.warmDrawable(warmDrawable);
        mShadowLooper.runOneTask();
        Mockito.verify(drawable).draw(ArgumentMatchers.any(Canvas.class));
    }

    @Implements(Picture.class)
    public static class ShadowPicture {
        @Implementation
        public void __constructor__(int nativePicture, boolean fromStream) {
        }

        @Implementation
        public void __constructor__(int nativePicture) {
        }

        @Implementation
        public void __constructor__() {
        }

        @Implementation
        public Canvas beginRecording(int width, int height) {
            return new Canvas();
        }
    }
}

