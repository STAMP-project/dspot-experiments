/**
 * Copyright (C) 2015 Twitter, Inc.
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
 *
 * Modifications copyright (C) 2017 Google Inc
 */
package com.firebase.ui.auth.ui.phone;


import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class SpacedEditTextTest {
    private static final float SPACING_PROPORTION = 1.1F;

    private SpacedEditText mSpacedEditText;

    @Test
    public void testSpacedEditText_setTextEmpty() {
        mSpacedEditText.setText("");
        testSpacing("", "", mSpacedEditText);
    }

    @Test
    public void testSpacedEditText_setTextNonEmpty() {
        mSpacedEditText.setText("123456");
        testSpacing("1 2 3 4 5 6", "123456", mSpacedEditText);
    }

    @Test
    public void testSpacedEditText_setTextWithOneCharacter() {
        mSpacedEditText.setText("1");
        testSpacing("1", "1", mSpacedEditText);
    }

    @Test
    public void testSpacedEditText_setTextWithExistingSpaces() {
        mSpacedEditText.setText("1 2 3");
        testSpacing("1   2   3", "1 2 3", mSpacedEditText);
    }

    @Test
    public void testSpacedEditText_noSetText() {
        testSpacing("", "", mSpacedEditText);
    }

    @Test
    public void testSpacedEditText_setLeadingSelection() {
        mSpacedEditText.setText("123456");
        mSpacedEditText.setSelection(0);
        Assert.assertEquals(0, mSpacedEditText.getSelectionStart());
    }

    @Test
    public void testSpacedEditText_setInnerSelection() {
        mSpacedEditText.setText("123456");
        mSpacedEditText.setSelection(3);
        Assert.assertEquals(5, mSpacedEditText.getSelectionStart());
    }
}

