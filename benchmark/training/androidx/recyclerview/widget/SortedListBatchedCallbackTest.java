/**
 * Copyright 2018 The Android Open Source Project
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
package androidx.recyclerview.widget;


import SortedList.BatchedCallback;
import SortedList.Callback;
import androidx.test.filters.SmallTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
@SmallTest
public class SortedListBatchedCallbackTest {
    BatchedCallback mBatchedCallback;

    Callback mMockCallback;

    @Test
    public void onChange() {
        mBatchedCallback.onChanged(1, 2);
        Mockito.verifyZeroInteractions(mMockCallback);
        mBatchedCallback.dispatchLastEvent();
        Mockito.verify(mMockCallback).onChanged(1, 2, null);
        Mockito.verifyNoMoreInteractions(mMockCallback);
    }

    @Test
    public void onChangeWithPayload() {
        final Object payload = 7;
        mBatchedCallback.onChanged(1, 2, payload);
        Mockito.verifyZeroInteractions(mMockCallback);
        mBatchedCallback.dispatchLastEvent();
        Mockito.verify(mMockCallback).onChanged(1, 2, payload);
        Mockito.verifyNoMoreInteractions(mMockCallback);
    }

    @Test
    public void onRemoved() {
        mBatchedCallback.onRemoved(2, 3);
        Mockito.verifyZeroInteractions(mMockCallback);
        mBatchedCallback.dispatchLastEvent();
        Mockito.verify(mMockCallback).onRemoved(2, 3);
        Mockito.verifyNoMoreInteractions(mMockCallback);
    }

    @Test
    public void onInserted() {
        mBatchedCallback.onInserted(3, 4);
        Mockito.verifyNoMoreInteractions(mMockCallback);
        mBatchedCallback.dispatchLastEvent();
        Mockito.verify(mMockCallback).onInserted(3, 4);
        Mockito.verifyNoMoreInteractions(mMockCallback);
    }

    @Test
    public void onMoved() {
        mBatchedCallback.onMoved(5, 6);
        // moves are not merged
        Mockito.verify(mMockCallback).onMoved(5, 6);
        Mockito.verifyNoMoreInteractions(mMockCallback);
    }

    @Test
    public void compare() {
        Object o1 = new Object();
        Object o2 = new Object();
        mBatchedCallback.compare(o1, o2);
        Mockito.verify(mMockCallback).compare(o1, o2);
        Mockito.verifyNoMoreInteractions(mMockCallback);
    }

    @Test
    public void areContentsTheSame() {
        Object o1 = new Object();
        Object o2 = new Object();
        mBatchedCallback.areContentsTheSame(o1, o2);
        Mockito.verify(mMockCallback).areContentsTheSame(o1, o2);
        Mockito.verifyNoMoreInteractions(mMockCallback);
    }

    @Test
    public void areItemsTheSame() {
        Object o1 = new Object();
        Object o2 = new Object();
        mBatchedCallback.areItemsTheSame(o1, o2);
        Mockito.verify(mMockCallback).areItemsTheSame(o1, o2);
        Mockito.verifyNoMoreInteractions(mMockCallback);
    }
}

