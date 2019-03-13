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


import RecyclerEventsController.OnRecyclerUpdateListener;
import ThreadUtils.OVERRIDE_MAIN_THREAD_FALSE;
import ThreadUtils.OVERRIDE_MAIN_THREAD_TRUE;
import androidx.recyclerview.widget.RecyclerView;
import com.facebook.litho.ThreadUtils;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(ComponentsTestRunner.class)
public class RecyclerEventsControllerTest {
    private SectionsRecyclerView mSectionsRecyclerView;

    private RecyclerView mRecyclerView;

    private RecyclerEventsController mRecyclerEventsController;

    private OnRecyclerUpdateListener mOnRecyclerUpdateListener;

    @Test
    public void testOnRecyclerListener() {
        Mockito.verify(mOnRecyclerUpdateListener, Mockito.never()).onUpdate(ArgumentMatchers.any(RecyclerView.class));
        mRecyclerEventsController.setOnRecyclerUpdateListener(mOnRecyclerUpdateListener);
        mRecyclerEventsController.setSectionsRecyclerView(null);
        mRecyclerEventsController.setSectionsRecyclerView(mSectionsRecyclerView);
        Mockito.verify(mOnRecyclerUpdateListener, Mockito.times(1)).onUpdate(null);
        Mockito.verify(mOnRecyclerUpdateListener, Mockito.times(1)).onUpdate(mRecyclerView);
    }

    @Test
    public void testClearRefreshingOnNotRefreshingView() {
        Mockito.when(mSectionsRecyclerView.isRefreshing()).thenReturn(false);
        mRecyclerEventsController.clearRefreshing();
        Mockito.verify(mSectionsRecyclerView, Mockito.never()).setRefreshing(ArgumentMatchers.anyBoolean());
        Mockito.verify(mSectionsRecyclerView, Mockito.never()).removeCallbacks(ArgumentMatchers.any(Runnable.class));
        Mockito.verify(mSectionsRecyclerView, Mockito.never()).post(ArgumentMatchers.any(Runnable.class));
    }

    @Test
    public void testClearRefreshingFromUIThread() {
        Mockito.when(mSectionsRecyclerView.isRefreshing()).thenReturn(true);
        ThreadUtils.setMainThreadOverride(OVERRIDE_MAIN_THREAD_TRUE);
        mRecyclerEventsController.clearRefreshing();
        Mockito.verify(mSectionsRecyclerView).setRefreshing(false);
        Mockito.verify(mSectionsRecyclerView, Mockito.never()).removeCallbacks(ArgumentMatchers.any(Runnable.class));
        Mockito.verify(mSectionsRecyclerView, Mockito.never()).post(ArgumentMatchers.any(Runnable.class));
    }

    @Test
    public void testClearRefreshingFromNonUIThread() {
        Mockito.when(mSectionsRecyclerView.isRefreshing()).thenReturn(true);
        ThreadUtils.setMainThreadOverride(OVERRIDE_MAIN_THREAD_FALSE);
        mRecyclerEventsController.clearRefreshing();
        Mockito.verify(mSectionsRecyclerView, Mockito.times(1)).removeCallbacks(ArgumentMatchers.any(Runnable.class));
        Mockito.verify(mSectionsRecyclerView, Mockito.times(1)).post(ArgumentMatchers.any(Runnable.class));
    }

    @Test
    public void testShowRefreshingFromUIThread() {
        Mockito.when(mSectionsRecyclerView.isRefreshing()).thenReturn(false);
        ThreadUtils.setMainThreadOverride(OVERRIDE_MAIN_THREAD_TRUE);
        mRecyclerEventsController.showRefreshing();
        Mockito.verify(mSectionsRecyclerView).setRefreshing(true);
    }

    @Test
    public void testShowRefreshingAlreadyRefreshing() {
        Mockito.when(mSectionsRecyclerView.isRefreshing()).thenReturn(true);
        mRecyclerEventsController.showRefreshing();
        Mockito.verify(mSectionsRecyclerView, Mockito.never()).setRefreshing(ArgumentMatchers.anyBoolean());
    }
}

