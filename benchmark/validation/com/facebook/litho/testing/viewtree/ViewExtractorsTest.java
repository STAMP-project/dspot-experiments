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
package com.facebook.litho.testing.viewtree;


import ViewExtractors.GET_DRAWABLE_FUNCTION;
import ViewExtractors.GET_TEXT_FUNCTION;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests {@link ViewExtractors}
 */
@RunWith(ComponentsTestRunner.class)
public class ViewExtractorsTest {
    private View mView;

    private TextView mTextView;

    private TextView mGoneTextView;

    private ImageView mImageView;

    private ImageView mGoneImageView;

    private Drawable mLithoDrawable;

    @Test
    public void testGetTextFromTextViewHasTextContent() {
        assertThat(GET_TEXT_FUNCTION.apply(mTextView)).contains("example");
    }

    @Test
    public void testGetTextPrintsVisibity() {
        assertThat(GET_TEXT_FUNCTION.apply(mTextView)).contains("view is visible");
        assertThat(GET_TEXT_FUNCTION.apply(mGoneTextView)).contains("view is not visible");
    }

    @Test
    public void testViewWithoutText() {
        assertThat(GET_TEXT_FUNCTION.apply(mView)).contains("No text found");
    }

    @Test
    public void testGetDrawableOutOfImageView() {
        assertThat(GET_DRAWABLE_FUNCTION.apply(mImageView)).contains(mLithoDrawable.toString());
    }

    @Test
    public void testGetDrawablePrintsVisibity() {
        assertThat(GET_DRAWABLE_FUNCTION.apply(mImageView)).contains("view is visible");
        assertThat(GET_DRAWABLE_FUNCTION.apply(mGoneImageView)).contains("view is not visible");
    }
}

