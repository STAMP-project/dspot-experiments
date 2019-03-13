/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.agera.rvdatabinding;


import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.view.View;
import com.google.android.agera.Binder;
import com.google.android.agera.rvadapter.LayoutPresenter;
import com.google.android.agera.rvdatabinding.test.matchers.HasPrivateConstructor;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class DataBindingLayoutPresentersTest {
    private static final Object HANDLER = new Object();

    private static final Object SECOND_HANDLER = new Object();

    @LayoutRes
    private static final int LAYOUT_ID = 1;

    private static final int HANDLER_ID = 4;

    private static final int SECOND_HANDLER_ID = 5;

    @Mock
    private Binder<String, View> binder;

    @Mock
    private ViewDataBinding viewDataBinding;

    @Mock
    private View view;

    @Test
    public void shouldReturnLayoutForLayoutResId() {
        final LayoutPresenter layoutPresenter = DataBindingLayoutPresenters.dataBindingLayoutPresenterFor(DataBindingLayoutPresentersTest.LAYOUT_ID).onRecycle(RecycleConfig.CLEAR_ITEM).build();
        MatcherAssert.assertThat(layoutPresenter.getLayoutResId(), Matchers.is(DataBindingLayoutPresentersTest.LAYOUT_ID));
    }

    @Test
    public void shouldDoNothingWithLayoutPresenterOnRecycleItemOnlyRecycling() {
        final LayoutPresenter layoutPresenter = DataBindingLayoutPresenters.dataBindingLayoutPresenterFor(DataBindingLayoutPresentersTest.LAYOUT_ID).handler(DataBindingLayoutPresentersTest.HANDLER_ID, DataBindingLayoutPresentersTest.HANDLER).handler(DataBindingLayoutPresentersTest.SECOND_HANDLER_ID, DataBindingLayoutPresentersTest.SECOND_HANDLER).onRecycle(RecycleConfig.CLEAR_ITEM).build();
        layoutPresenter.recycle(view);
        Mockito.verifyZeroInteractions(viewDataBinding);
    }

    @Test
    public void shouldRecycleLayoutPresenterWithAllRecycling() {
        final LayoutPresenter layoutPresenter = DataBindingLayoutPresenters.dataBindingLayoutPresenterFor(DataBindingLayoutPresentersTest.LAYOUT_ID).handler(DataBindingLayoutPresentersTest.HANDLER_ID, DataBindingLayoutPresentersTest.HANDLER).handler(DataBindingLayoutPresentersTest.SECOND_HANDLER_ID, DataBindingLayoutPresentersTest.SECOND_HANDLER).onRecycle(RecycleConfig.CLEAR_ALL).build();
        layoutPresenter.recycle(view);
        Mockito.verify(viewDataBinding).setVariable(DataBindingLayoutPresentersTest.HANDLER_ID, null);
        Mockito.verify(viewDataBinding).setVariable(DataBindingLayoutPresentersTest.SECOND_HANDLER_ID, null);
        Mockito.verify(viewDataBinding).executePendingBindings();
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldRecycleLayoutPresenterWithHandlerRecycling() {
        final LayoutPresenter layoutPresenter = DataBindingLayoutPresenters.dataBindingLayoutPresenterFor(DataBindingLayoutPresentersTest.LAYOUT_ID).handler(DataBindingLayoutPresentersTest.HANDLER_ID, DataBindingLayoutPresentersTest.HANDLER).handler(DataBindingLayoutPresentersTest.SECOND_HANDLER_ID, DataBindingLayoutPresentersTest.SECOND_HANDLER).onRecycle(RecycleConfig.CLEAR_HANDLERS).build();
        layoutPresenter.recycle(view);
        Mockito.verify(viewDataBinding).setVariable(DataBindingLayoutPresentersTest.HANDLER_ID, null);
        Mockito.verify(viewDataBinding).setVariable(DataBindingLayoutPresentersTest.SECOND_HANDLER_ID, null);
        Mockito.verify(viewDataBinding).executePendingBindings();
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldBindLayoutPresenter() {
        final LayoutPresenter layoutPresenter = DataBindingLayoutPresenters.dataBindingLayoutPresenterFor(DataBindingLayoutPresentersTest.LAYOUT_ID).handler(DataBindingLayoutPresentersTest.HANDLER_ID, DataBindingLayoutPresentersTest.HANDLER).handler(DataBindingLayoutPresentersTest.SECOND_HANDLER_ID, DataBindingLayoutPresentersTest.SECOND_HANDLER).build();
        layoutPresenter.bind(view);
        Mockito.verify(viewDataBinding).setVariable(DataBindingLayoutPresentersTest.HANDLER_ID, DataBindingLayoutPresentersTest.HANDLER);
        Mockito.verify(viewDataBinding).setVariable(DataBindingLayoutPresentersTest.SECOND_HANDLER_ID, DataBindingLayoutPresentersTest.SECOND_HANDLER);
        Mockito.verify(viewDataBinding).executePendingBindings();
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldHavePrivateConstructor() {
        MatcherAssert.assertThat(DataBindingLayoutPresenters.class, HasPrivateConstructor.hasPrivateConstructor());
    }
}

