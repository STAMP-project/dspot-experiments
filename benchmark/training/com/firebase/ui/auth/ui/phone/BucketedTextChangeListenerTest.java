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


import BucketedTextChangeListener.ContentChangeCallback;
import android.widget.EditText;
import com.firebase.ui.auth.util.ui.BucketedTextChangeListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class BucketedTextChangeListenerTest {
    private static final int EXPECTED_LENGTH = 6;

    private static final String PLACE_HOLDER = "-";

    private static final int ANY_INT = -1;

    private EditText mEditText;

    private ContentChangeCallback mContentChangeCallback;

    private BucketedTextChangeListener mTextChangeListener;

    @Test
    public void testTextChange_empty() {
        mTextChangeListener.onTextChanged("------", BucketedTextChangeListenerTest.ANY_INT, BucketedTextChangeListenerTest.ANY_INT, BucketedTextChangeListenerTest.ANY_INT);
        testListener(mEditText, "------", 0, false);
    }

    @Test
    public void testTextChange_atIndex0() {
        mTextChangeListener.onTextChanged("1------", BucketedTextChangeListenerTest.ANY_INT, BucketedTextChangeListenerTest.ANY_INT, BucketedTextChangeListenerTest.ANY_INT);
        testListener(mEditText, "1-----", 1, false);
    }

    @Test
    public void testTextChange_atIndex1() {
        mTextChangeListener.onTextChanged("12-----", BucketedTextChangeListenerTest.ANY_INT, BucketedTextChangeListenerTest.ANY_INT, BucketedTextChangeListenerTest.ANY_INT);
        testListener(mEditText, "12----", 2, false);
    }

    @Test
    public void testTextChange_atIndex5() {
        mTextChangeListener.onTextChanged("123456-", BucketedTextChangeListenerTest.ANY_INT, BucketedTextChangeListenerTest.ANY_INT, BucketedTextChangeListenerTest.ANY_INT);
        testListener(mEditText, "123456", 6, true);
    }

    @Test
    public void testTextChange_exceedingMaxLength() {
        mTextChangeListener.onTextChanged("1234567", BucketedTextChangeListenerTest.ANY_INT, BucketedTextChangeListenerTest.ANY_INT, BucketedTextChangeListenerTest.ANY_INT);
        testListener(mEditText, "123456", 6, true);
    }

    @Test
    public void testTextChange_onClear() {
        mTextChangeListener.onTextChanged("", BucketedTextChangeListenerTest.ANY_INT, BucketedTextChangeListenerTest.ANY_INT, BucketedTextChangeListenerTest.ANY_INT);
        testListener(mEditText, "------", 0, false);
    }

    @Test
    public void testTextChange_onPartialClear() {
        mTextChangeListener.onTextChanged("123", BucketedTextChangeListenerTest.ANY_INT, BucketedTextChangeListenerTest.ANY_INT, BucketedTextChangeListenerTest.ANY_INT);
        testListener(mEditText, "123---", 3, false);
    }

    @Test
    public void testTextChange_onIncorrectInsertion() {
        mTextChangeListener.onTextChanged("1--3--", BucketedTextChangeListenerTest.ANY_INT, BucketedTextChangeListenerTest.ANY_INT, BucketedTextChangeListenerTest.ANY_INT);
        testListener(mEditText, "13----", 2, false);
    }
}

