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


import ComponentLifecycle.StateUpdate;
import LithoRecylerView.TouchInterceptor;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ItemAnimator;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;
import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.ComponentLifecycle;
import com.facebook.litho.Output;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for {@link RecyclerSpec}
 */
@RunWith(ComponentsTestRunner.class)
public class RecyclerSpecTest {
    private ComponentContext mComponentContext;

    private SectionsRecyclerView mSectionsRecyclerView;

    private LithoRecylerView mRecyclerView;

    private ItemAnimator mAnimator;

    @Test
    public void testRecyclerSpecOnBind() {
        OnRefreshListener onRefreshListener = Mockito.mock(OnRefreshListener.class);
        Binder<RecyclerView> binder = Mockito.mock(Binder.class);
        Output<ItemAnimator> oldAnimator = Mockito.mock(Output.class);
        SnapHelper snapHelper = Mockito.mock(SnapHelper.class);
        final int size = 3;
        List<RecyclerView.OnScrollListener> scrollListeners = RecyclerSpecTest.createListOfScrollListeners(size);
        LithoRecylerView.TouchInterceptor touchInterceptor = Mockito.mock(TouchInterceptor.class);
        RecyclerSpec.onBind(mComponentContext, mSectionsRecyclerView, binder, mAnimator, null, scrollListeners, snapHelper, true, touchInterceptor, onRefreshListener, oldAnimator);
        Mockito.verify(mSectionsRecyclerView).setEnabled(true);
        Mockito.verify(mSectionsRecyclerView).setOnRefreshListener(onRefreshListener);
        Mockito.verify(mSectionsRecyclerView, Mockito.times(1)).getRecyclerView();
        Mockito.verify(oldAnimator).set(mAnimator);
        Mockito.verify(mRecyclerView).setItemAnimator(ArgumentMatchers.any(ItemAnimator.class));
        Mockito.verify(mRecyclerView, Mockito.times(size)).addOnScrollListener(ArgumentMatchers.any(OnScrollListener.class));
        Mockito.verify(mRecyclerView).setTouchInterceptor(touchInterceptor);
        Mockito.verify(binder).bind(mRecyclerView);
        Mockito.verify(mRecyclerView, Mockito.times(1)).requestLayout();
        Mockito.verify(mSectionsRecyclerView).setHasBeenDetachedFromWindow(false);
        Mockito.verify(snapHelper).attachToRecyclerView(mRecyclerView);
    }

    @Test
    public void testRecyclerSpecOnUnbind() {
        Mockito.when(mSectionsRecyclerView.hasBeenDetachedFromWindow()).thenReturn(true);
        Binder<RecyclerView> binder = Mockito.mock(Binder.class);
        final int size = 3;
        List<RecyclerView.OnScrollListener> scrollListeners = RecyclerSpecTest.createListOfScrollListeners(size);
        RecyclerSpec.onUnbind(mComponentContext, mSectionsRecyclerView, binder, null, scrollListeners, mAnimator);
        Mockito.verify(mRecyclerView).setItemAnimator(mAnimator);
        Mockito.verify(binder).unbind(mRecyclerView);
        Mockito.verify(mRecyclerView, Mockito.times(size)).removeOnScrollListener(ArgumentMatchers.any(OnScrollListener.class));
        Mockito.verify(mSectionsRecyclerView).setOnRefreshListener(null);
    }

    @Test
    public void testUpdateStateAsyncWithRemeasureEvent() {
        final Recycler recycler = Recycler.create(mComponentContext).binder(Mockito.mock(Binder.class)).build();
        final RecyclerSpecTest.TestComponentContext testComponentContext = new RecyclerSpecTest.TestComponentContext(mComponentContext.getAndroidContext(), recycler);
        RecyclerSpec.onRemeasure(testComponentContext, 0);
        assertThat(testComponentContext.isUpdateStateAsync()).isTrue();
    }

    @Test
    public void testShouldAlwaysRemeasure() {
        final Binder<RecyclerView> binder = Mockito.mock(Binder.class);
        final Recycler recycler = Recycler.create(mComponentContext).binder(binder).build();
        Mockito.when(binder.isWrapContent()).thenReturn(false);
        assertThat(recycler.shouldAlwaysRemeasure()).isFalse();
        Mockito.when(binder.isWrapContent()).thenReturn(true);
        assertThat(recycler.shouldAlwaysRemeasure()).isTrue();
    }

    private static class TestComponentContext extends ComponentContext {
        private Recycler mRecycler;

        private boolean mIsUpdateStateAsync = false;

        public TestComponentContext(Context context, Recycler recycler) {
            super(context);
            mRecycler = recycler;
        }

        @Override
        public Component getComponentScope() {
            return mRecycler;
        }

        @Override
        public void updateStateSync(ComponentLifecycle.StateUpdate stateUpdate, String attribution) {
            super.updateStateSync(stateUpdate, attribution);
            mIsUpdateStateAsync = false;
        }

        @Override
        public void updateStateAsync(ComponentLifecycle.StateUpdate stateUpdate, String attribution) {
            super.updateStateAsync(stateUpdate, attribution);
            mIsUpdateStateAsync = true;
        }

        public boolean isUpdateStateAsync() {
            return mIsUpdateStateAsync;
        }
    }
}

