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
package com.google.android.agera.rvadapter;


import android.support.annotation.LayoutRes;
import android.view.View;
import com.google.android.agera.Receiver;
import com.google.android.agera.rvadapter.test.matchers.HasPrivateConstructor;
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
public class LayoutPresentersTest {
    @LayoutRes
    private static final int LAYOUT_ID = 0;

    @Mock
    private Receiver<View> binder;

    @Mock
    private Receiver<View> recycler;

    @Mock
    private View view;

    @Test
    public void shouldReturnLayoutIdForCompiledLayout() {
        final LayoutPresenter layoutPresenter = LayoutPresenters.layoutPresenterFor(LayoutPresentersTest.LAYOUT_ID).build();
        MatcherAssert.assertThat(layoutPresenter.getLayoutResId(), Matchers.is(LayoutPresentersTest.LAYOUT_ID));
    }

    @Test
    public void shouldReturnLayoutIdForLayout() {
        final LayoutPresenter layoutPresenter = LayoutPresenters.layout(LayoutPresentersTest.LAYOUT_ID);
        MatcherAssert.assertThat(layoutPresenter.getLayoutResId(), Matchers.is(LayoutPresentersTest.LAYOUT_ID));
    }

    @Test
    public void shouldBindCompiledLayout() {
        final LayoutPresenter layoutPresenter = LayoutPresenters.layoutPresenterFor(LayoutPresentersTest.LAYOUT_ID).bindWith(binder).build();
        layoutPresenter.bind(view);
        Mockito.verify(binder).accept(view);
    }

    @Test
    public void shouldBindCompiledLayoutWithoutBinder() {
        final LayoutPresenter layoutPresenter = LayoutPresenters.layoutPresenterFor(LayoutPresentersTest.LAYOUT_ID).build();
        layoutPresenter.bind(view);
    }

    @Test
    public void shouldBindLayoutWithoutBinder() {
        final LayoutPresenter layoutPresenter = LayoutPresenters.layout(LayoutPresentersTest.LAYOUT_ID);
        layoutPresenter.bind(view);
    }

    @Test
    public void shouldRecycleViewInCompiledLayout() {
        final LayoutPresenter layoutPresenter = LayoutPresenters.layoutPresenterFor(LayoutPresentersTest.LAYOUT_ID).bindWith(binder).recycleWith(recycler).build();
        layoutPresenter.recycle(view);
        Mockito.verify(recycler).accept(view);
    }

    @Test
    public void shouldHandleRecycleWithoutRecyclerInCompiledLayout() {
        final LayoutPresenter layoutPresenter = LayoutPresenters.layoutPresenterFor(LayoutPresentersTest.LAYOUT_ID).bindWith(binder).build();
        layoutPresenter.recycle(view);
    }

    @Test
    public void shouldHandleRecycleWithoutRecyclerInLayout() {
        final LayoutPresenter layoutPresenter = LayoutPresenters.layout(LayoutPresentersTest.LAYOUT_ID);
        layoutPresenter.recycle(view);
    }

    @Test
    public void shouldHavePrivateConstructor() {
        MatcherAssert.assertThat(LayoutPresenters.class, HasPrivateConstructor.hasPrivateConstructor());
    }
}

