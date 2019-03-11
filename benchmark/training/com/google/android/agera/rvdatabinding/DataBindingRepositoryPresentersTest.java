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


import R.id.agera__rvdatabinding__collection_id;
import R.id.agera__rvdatabinding__item_id;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import com.google.android.agera.Function;
import com.google.android.agera.Functions;
import com.google.android.agera.Result;
import com.google.android.agera.rvadapter.RepositoryPresenter;
import com.google.android.agera.rvdatabinding.test.DiffingLogic;
import com.google.android.agera.rvdatabinding.test.VerifyingWrappers;
import com.google.android.agera.rvdatabinding.test.matchers.HasPrivateConstructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
public class DataBindingRepositoryPresentersTest {
    private static final String STRING = "string";

    private static final String FIRST_STRING_CHARACTER = "s";

    private static final String SECOND_STRING = "string2";

    private static final Result<String> STRING_RESULT = Result.present(DataBindingRepositoryPresentersTest.STRING);

    private static final List<String> STRING_LIST = Arrays.asList(DataBindingRepositoryPresentersTest.STRING, DataBindingRepositoryPresentersTest.SECOND_STRING);

    private static final Result<List<String>> STRING_LIST_RESULT = Result.success(DataBindingRepositoryPresentersTest.STRING_LIST);

    private static final Result<String> FAILURE = Result.failure();

    private static final Result<List<String>> LIST_FAILURE = Result.failure();

    private static final Object HANDLER = new Object();

    private static final Object SECOND_HANDLER = new Object();

    @LayoutRes
    private static final int LAYOUT_ID = 1;

    private static final int DYNAMIC_LAYOUT_ID = 2;

    private static final int ITEM_ID = 3;

    private static final int HANDLER_ID = 4;

    private static final int SECOND_HANDLER_ID = 5;

    private static final int COLLECTION_ID = 6;

    private static final long STABLE_ID = 2;

    @Mock
    private Function<String, Integer> layoutForItem;

    @Mock
    private Function<String, Integer> itemIdForItem;

    @Mock
    private ViewDataBinding viewDataBinding;

    @Mock
    private View view;

    @Mock
    private ListUpdateCallback listUpdateCallback;

    private ViewHolder viewHolder;

    @Test
    public void shouldBindRepositoryPresenterOfResult() {
        final RepositoryPresenter<Result<String>> resultRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).handler(DataBindingRepositoryPresentersTest.HANDLER_ID, DataBindingRepositoryPresentersTest.HANDLER).handler(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, DataBindingRepositoryPresentersTest.SECOND_HANDLER).forResult();
        resultRepositoryPresenter.bind(DataBindingRepositoryPresentersTest.STRING_RESULT, 0, viewHolder);
        Mockito.verify(view).setTag(agera__rvdatabinding__item_id, DataBindingRepositoryPresentersTest.ITEM_ID);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.ITEM_ID, DataBindingRepositoryPresentersTest.STRING);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.HANDLER_ID, DataBindingRepositoryPresentersTest.HANDLER);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, DataBindingRepositoryPresentersTest.SECOND_HANDLER);
        Mockito.verify(viewDataBinding).executePendingBindings();
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldBindRepositoryPresenterWithoutItem() {
        final RepositoryPresenter<String> repositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).forItem();
        repositoryPresenter.bind(DataBindingRepositoryPresentersTest.STRING, 0, viewHolder);
        Mockito.verify(viewDataBinding).executePendingBindings();
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldBindRepositoryPresenterOfCollection() {
        final RepositoryPresenter<String> repositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).forCollection(new Function<String, List<String>>() {
            @NonNull
            @Override
            public List<String> apply(@NonNull
            final String input) {
                return Collections.singletonList(String.valueOf(input.charAt(0)));
            }
        });
        repositoryPresenter.bind(DataBindingRepositoryPresentersTest.STRING, 0, viewHolder);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.ITEM_ID, DataBindingRepositoryPresentersTest.FIRST_STRING_CHARACTER);
        Mockito.verify(viewDataBinding).executePendingBindings();
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldBindRepositoryPresenterCollectionOfCollection() {
        final RepositoryPresenter<String> repositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).collectionId(DataBindingRepositoryPresentersTest.COLLECTION_ID).forCollection(new DataBindingRepositoryPresentersTest.StringToFirstCharStringList());
        repositoryPresenter.bind(DataBindingRepositoryPresentersTest.STRING, 0, viewHolder);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.ITEM_ID, DataBindingRepositoryPresentersTest.FIRST_STRING_CHARACTER);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.COLLECTION_ID, DataBindingRepositoryPresentersTest.STRING);
        Mockito.verify(viewDataBinding).executePendingBindings();
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldHandleRecycleOfRepositoryPresenterWithoutItemId() {
        final RepositoryPresenter<String> repositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).onRecycle(RecycleConfig.CLEAR_ALL).forItem();
        repositoryPresenter.recycle(viewHolder);
        Mockito.verify(viewDataBinding).executePendingBindings();
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldNotRecycleRepositoryPresenterOfResultWithNoRecycling() {
        final RepositoryPresenter<Result<String>> resultRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).handler(DataBindingRepositoryPresentersTest.HANDLER_ID, DataBindingRepositoryPresentersTest.HANDLER).handler(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, DataBindingRepositoryPresentersTest.SECOND_HANDLER).onRecycle(RecycleConfig.DO_NOTHING).forResult();
        resultRepositoryPresenter.recycle(viewHolder);
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldRecycleRepositoryPresenterOfResultWithItemRecycling() {
        Mockito.when(view.getTag(agera__rvdatabinding__item_id)).thenReturn(DataBindingRepositoryPresentersTest.ITEM_ID);
        final RepositoryPresenter<Result<String>> resultRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).handler(DataBindingRepositoryPresentersTest.HANDLER_ID, DataBindingRepositoryPresentersTest.HANDLER).handler(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, DataBindingRepositoryPresentersTest.SECOND_HANDLER).onRecycle(RecycleConfig.CLEAR_ITEM).forResult();
        resultRepositoryPresenter.recycle(viewHolder);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.ITEM_ID, null);
        Mockito.verify(viewDataBinding).executePendingBindings();
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldRecycleRepositoryPresenterOfResultWithAllRecycling() {
        Mockito.when(view.getTag(agera__rvdatabinding__item_id)).thenReturn(DataBindingRepositoryPresentersTest.ITEM_ID);
        final RepositoryPresenter<Result<String>> resultRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).handler(DataBindingRepositoryPresentersTest.HANDLER_ID, DataBindingRepositoryPresentersTest.HANDLER).handler(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, DataBindingRepositoryPresentersTest.SECOND_HANDLER).onRecycle(RecycleConfig.CLEAR_ALL).forResult();
        resultRepositoryPresenter.recycle(viewHolder);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.ITEM_ID, null);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.HANDLER_ID, null);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, null);
        Mockito.verify(viewDataBinding).executePendingBindings();
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldRecycleRepositoryPresenterOfResultWithHandlerRecycling() {
        Mockito.when(view.getTag(agera__rvdatabinding__item_id)).thenReturn(DataBindingRepositoryPresentersTest.ITEM_ID);
        final RepositoryPresenter<Result<String>> resultRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).handler(DataBindingRepositoryPresentersTest.HANDLER_ID, DataBindingRepositoryPresentersTest.HANDLER).handler(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, DataBindingRepositoryPresentersTest.SECOND_HANDLER).onRecycle(RecycleConfig.CLEAR_HANDLERS).forResult();
        resultRepositoryPresenter.recycle(viewHolder);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.HANDLER_ID, null);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, null);
        Mockito.verify(viewDataBinding).executePendingBindings();
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldNotRecycleRepositoryPresenterOfCollectionWithNoRecycling() {
        final RepositoryPresenter<String> resultRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).handler(DataBindingRepositoryPresentersTest.HANDLER_ID, DataBindingRepositoryPresentersTest.HANDLER).handler(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, DataBindingRepositoryPresentersTest.SECOND_HANDLER).onRecycle(RecycleConfig.DO_NOTHING).collectionId(DataBindingRepositoryPresentersTest.COLLECTION_ID).forCollection(new DataBindingRepositoryPresentersTest.StringToFirstCharStringList());
        resultRepositoryPresenter.recycle(viewHolder);
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldRecycleRepositoryPresenterOfCollectionWithItemRecycling() {
        Mockito.when(view.getTag(agera__rvdatabinding__item_id)).thenReturn(DataBindingRepositoryPresentersTest.ITEM_ID);
        final RepositoryPresenter<String> resultRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).handler(DataBindingRepositoryPresentersTest.HANDLER_ID, DataBindingRepositoryPresentersTest.HANDLER).handler(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, DataBindingRepositoryPresentersTest.SECOND_HANDLER).onRecycle(RecycleConfig.CLEAR_ITEM).collectionId(DataBindingRepositoryPresentersTest.COLLECTION_ID).forCollection(new DataBindingRepositoryPresentersTest.StringToFirstCharStringList());
        resultRepositoryPresenter.recycle(viewHolder);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.ITEM_ID, null);
        Mockito.verify(viewDataBinding).executePendingBindings();
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldRecycleRepositoryPresenterOfCollectionWithAllRecycling() {
        Mockito.when(view.getTag(agera__rvdatabinding__item_id)).thenReturn(DataBindingRepositoryPresentersTest.ITEM_ID);
        Mockito.when(view.getTag(agera__rvdatabinding__collection_id)).thenReturn(DataBindingRepositoryPresentersTest.COLLECTION_ID);
        final RepositoryPresenter<String> resultRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).handler(DataBindingRepositoryPresentersTest.HANDLER_ID, DataBindingRepositoryPresentersTest.HANDLER).handler(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, DataBindingRepositoryPresentersTest.SECOND_HANDLER).onRecycle(RecycleConfig.CLEAR_ALL).collectionId(DataBindingRepositoryPresentersTest.COLLECTION_ID).forCollection(new DataBindingRepositoryPresentersTest.StringToFirstCharStringList());
        resultRepositoryPresenter.recycle(viewHolder);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.ITEM_ID, null);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.HANDLER_ID, null);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, null);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.COLLECTION_ID, null);
        Mockito.verify(viewDataBinding).executePendingBindings();
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldRecycleRepositoryPresenterOfCollectionWithCollectionRecycling() {
        Mockito.when(view.getTag(agera__rvdatabinding__item_id)).thenReturn(DataBindingRepositoryPresentersTest.ITEM_ID);
        Mockito.when(view.getTag(agera__rvdatabinding__collection_id)).thenReturn(DataBindingRepositoryPresentersTest.COLLECTION_ID);
        final RepositoryPresenter<String> resultRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).handler(DataBindingRepositoryPresentersTest.HANDLER_ID, DataBindingRepositoryPresentersTest.HANDLER).handler(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, DataBindingRepositoryPresentersTest.SECOND_HANDLER).onRecycle(RecycleConfig.CLEAR_COLLECTION).collectionId(DataBindingRepositoryPresentersTest.COLLECTION_ID).forCollection(new DataBindingRepositoryPresentersTest.StringToFirstCharStringList());
        resultRepositoryPresenter.recycle(viewHolder);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.COLLECTION_ID, null);
        Mockito.verify(viewDataBinding).executePendingBindings();
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldRecycleRepositoryPresenterOfCollectionWithHandlerRecycling() {
        Mockito.when(view.getTag(agera__rvdatabinding__item_id)).thenReturn(DataBindingRepositoryPresentersTest.ITEM_ID);
        Mockito.when(view.getTag(agera__rvdatabinding__collection_id)).thenReturn(DataBindingRepositoryPresentersTest.COLLECTION_ID);
        final RepositoryPresenter<String> resultRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).handler(DataBindingRepositoryPresentersTest.HANDLER_ID, DataBindingRepositoryPresentersTest.HANDLER).handler(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, DataBindingRepositoryPresentersTest.SECOND_HANDLER).onRecycle(RecycleConfig.CLEAR_HANDLERS).collectionId(DataBindingRepositoryPresentersTest.COLLECTION_ID).forCollection(new DataBindingRepositoryPresentersTest.StringToFirstCharStringList());
        resultRepositoryPresenter.recycle(viewHolder);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.HANDLER_ID, null);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, null);
        Mockito.verify(viewDataBinding).executePendingBindings();
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldBindRepositoryPresenterOfResultList() {
        final RepositoryPresenter<Result<List<String>>> resultListRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).handler(DataBindingRepositoryPresentersTest.HANDLER_ID, DataBindingRepositoryPresentersTest.HANDLER).forResultList();
        resultListRepositoryPresenter.bind(DataBindingRepositoryPresentersTest.STRING_LIST_RESULT, 1, viewHolder);
        Mockito.verify(view).setTag(agera__rvdatabinding__item_id, DataBindingRepositoryPresentersTest.ITEM_ID);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.ITEM_ID, DataBindingRepositoryPresentersTest.SECOND_STRING);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.HANDLER_ID, DataBindingRepositoryPresentersTest.HANDLER);
        Mockito.verify(viewDataBinding).executePendingBindings();
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldNotRecycleRepositoryPresenterOfResultListWithNoRecycling() {
        final RepositoryPresenter<Result<List<String>>> resultRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).handler(DataBindingRepositoryPresentersTest.HANDLER_ID, DataBindingRepositoryPresentersTest.HANDLER).handler(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, DataBindingRepositoryPresentersTest.SECOND_HANDLER).onRecycle(RecycleConfig.DO_NOTHING).forResultList();
        resultRepositoryPresenter.recycle(viewHolder);
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldRecycleRepositoryPresenterOfResultListWithItemRecycling() {
        Mockito.when(view.getTag(agera__rvdatabinding__item_id)).thenReturn(DataBindingRepositoryPresentersTest.ITEM_ID);
        final RepositoryPresenter<Result<List<String>>> resultRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).handler(DataBindingRepositoryPresentersTest.HANDLER_ID, DataBindingRepositoryPresentersTest.HANDLER).handler(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, DataBindingRepositoryPresentersTest.SECOND_HANDLER).onRecycle(RecycleConfig.CLEAR_ITEM).forResultList();
        resultRepositoryPresenter.recycle(viewHolder);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.ITEM_ID, null);
        Mockito.verify(viewDataBinding).executePendingBindings();
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldRecycleRepositoryPresenterOfResultListWithAllRecycling() {
        Mockito.when(view.getTag(agera__rvdatabinding__item_id)).thenReturn(DataBindingRepositoryPresentersTest.ITEM_ID);
        final RepositoryPresenter<Result<List<String>>> resultRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).handler(DataBindingRepositoryPresentersTest.HANDLER_ID, DataBindingRepositoryPresentersTest.HANDLER).handler(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, DataBindingRepositoryPresentersTest.SECOND_HANDLER).onRecycle(RecycleConfig.CLEAR_ALL).forResultList();
        resultRepositoryPresenter.recycle(viewHolder);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.ITEM_ID, null);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.HANDLER_ID, null);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, null);
        Mockito.verify(viewDataBinding).executePendingBindings();
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldRecycleRepositoryPresenterOfResultListWithHandlerRecycling() {
        Mockito.when(view.getTag(agera__rvdatabinding__item_id)).thenReturn(DataBindingRepositoryPresentersTest.ITEM_ID);
        final RepositoryPresenter<Result<List<String>>> resultRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).handler(DataBindingRepositoryPresentersTest.HANDLER_ID, DataBindingRepositoryPresentersTest.HANDLER).handler(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, DataBindingRepositoryPresentersTest.SECOND_HANDLER).onRecycle(RecycleConfig.CLEAR_HANDLERS).forResultList();
        resultRepositoryPresenter.recycle(viewHolder);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.HANDLER_ID, null);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, null);
        Mockito.verify(viewDataBinding).executePendingBindings();
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldRecycleRepositoryPresenterOfItemWithItemRecycling() {
        Mockito.when(view.getTag(agera__rvdatabinding__item_id)).thenReturn(DataBindingRepositoryPresentersTest.ITEM_ID);
        final RepositoryPresenter<String> resultRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).handler(DataBindingRepositoryPresentersTest.HANDLER_ID, DataBindingRepositoryPresentersTest.HANDLER).handler(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, DataBindingRepositoryPresentersTest.SECOND_HANDLER).onRecycle(RecycleConfig.CLEAR_ITEM).forItem();
        resultRepositoryPresenter.recycle(viewHolder);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.ITEM_ID, null);
        Mockito.verify(viewDataBinding).executePendingBindings();
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldRecycleRepositoryPresenterOfItemWithAllRecycling() {
        Mockito.when(view.getTag(agera__rvdatabinding__item_id)).thenReturn(DataBindingRepositoryPresentersTest.ITEM_ID);
        final RepositoryPresenter<String> resultRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).handler(DataBindingRepositoryPresentersTest.HANDLER_ID, DataBindingRepositoryPresentersTest.HANDLER).handler(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, DataBindingRepositoryPresentersTest.SECOND_HANDLER).onRecycle(RecycleConfig.CLEAR_ALL).forItem();
        resultRepositoryPresenter.recycle(viewHolder);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.ITEM_ID, null);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.HANDLER_ID, null);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, null);
        Mockito.verify(viewDataBinding).executePendingBindings();
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldRecycleRepositoryPresenterOfItemWithHandlerRecycling() {
        Mockito.when(view.getTag(agera__rvdatabinding__item_id)).thenReturn(DataBindingRepositoryPresentersTest.ITEM_ID);
        final RepositoryPresenter<String> resultRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).handler(DataBindingRepositoryPresentersTest.HANDLER_ID, DataBindingRepositoryPresentersTest.HANDLER).handler(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, DataBindingRepositoryPresentersTest.SECOND_HANDLER).onRecycle(RecycleConfig.CLEAR_HANDLERS).forItem();
        resultRepositoryPresenter.recycle(viewHolder);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.HANDLER_ID, null);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, null);
        Mockito.verify(viewDataBinding).executePendingBindings();
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldBindRepositoryPresenterOfItem() {
        final RepositoryPresenter<String> itemRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).forItem();
        itemRepositoryPresenter.bind(DataBindingRepositoryPresentersTest.STRING, 0, viewHolder);
    }

    @Test
    public void shouldBindRepositoryPresenterOfList() {
        final RepositoryPresenter<List<String>> listRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).forList();
        listRepositoryPresenter.bind(DataBindingRepositoryPresentersTest.STRING_LIST, 1, viewHolder);
        Mockito.verify(view).setTag(agera__rvdatabinding__item_id, DataBindingRepositoryPresentersTest.ITEM_ID);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.ITEM_ID, DataBindingRepositoryPresentersTest.SECOND_STRING);
        Mockito.verify(viewDataBinding).executePendingBindings();
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldNotRecycleRepositoryPresenterOfListWithNoRecycling() {
        final RepositoryPresenter<List<String>> resultRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).handler(DataBindingRepositoryPresentersTest.HANDLER_ID, DataBindingRepositoryPresentersTest.HANDLER).handler(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, DataBindingRepositoryPresentersTest.SECOND_HANDLER).onRecycle(RecycleConfig.DO_NOTHING).forList();
        resultRepositoryPresenter.recycle(viewHolder);
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldRecycleRepositoryPresenterOfListWithItemRecycling() {
        Mockito.when(view.getTag(agera__rvdatabinding__item_id)).thenReturn(DataBindingRepositoryPresentersTest.ITEM_ID);
        final RepositoryPresenter<List<String>> resultRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).handler(DataBindingRepositoryPresentersTest.HANDLER_ID, DataBindingRepositoryPresentersTest.HANDLER).handler(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, DataBindingRepositoryPresentersTest.SECOND_HANDLER).onRecycle(RecycleConfig.CLEAR_ITEM).forList();
        resultRepositoryPresenter.recycle(viewHolder);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.ITEM_ID, null);
        Mockito.verify(viewDataBinding).executePendingBindings();
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldRecycleRepositoryPresenterOfListWithAllRecycling() {
        Mockito.when(view.getTag(agera__rvdatabinding__item_id)).thenReturn(DataBindingRepositoryPresentersTest.ITEM_ID);
        final RepositoryPresenter<List<String>> resultRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).handler(DataBindingRepositoryPresentersTest.HANDLER_ID, DataBindingRepositoryPresentersTest.HANDLER).handler(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, DataBindingRepositoryPresentersTest.SECOND_HANDLER).onRecycle(RecycleConfig.CLEAR_ALL).forList();
        resultRepositoryPresenter.recycle(viewHolder);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.ITEM_ID, null);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.HANDLER_ID, null);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, null);
        Mockito.verify(viewDataBinding).executePendingBindings();
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldRecycleRepositoryPresenterOfListWithHandlerRecycling() {
        Mockito.when(view.getTag(agera__rvdatabinding__item_id)).thenReturn(DataBindingRepositoryPresentersTest.ITEM_ID);
        final RepositoryPresenter<List<String>> resultRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).handler(DataBindingRepositoryPresentersTest.HANDLER_ID, DataBindingRepositoryPresentersTest.HANDLER).handler(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, DataBindingRepositoryPresentersTest.SECOND_HANDLER).onRecycle(RecycleConfig.CLEAR_HANDLERS).forList();
        resultRepositoryPresenter.recycle(viewHolder);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.HANDLER_ID, null);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.SECOND_HANDLER_ID, null);
        Mockito.verify(viewDataBinding).executePendingBindings();
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldReturnZeroForCountOfRepositoryPresenterOfFailedResult() {
        final RepositoryPresenter<Result<String>> resultRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).handler(DataBindingRepositoryPresentersTest.HANDLER_ID, DataBindingRepositoryPresentersTest.HANDLER).forResult();
        MatcherAssert.assertThat(resultRepositoryPresenter.getItemCount(DataBindingRepositoryPresentersTest.FAILURE), Matchers.is(0));
    }

    @Test
    public void shouldReturnOneForCountOfRepositoryPresenterOfSuccessfulResult() {
        final RepositoryPresenter<Result<String>> resultRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).forResult();
        MatcherAssert.assertThat(resultRepositoryPresenter.getItemCount(DataBindingRepositoryPresentersTest.STRING_RESULT), Matchers.is(1));
    }

    @Test
    public void shouldReturnListSizeForCountOfRepositoryPresenterOfList() {
        final RepositoryPresenter<List<String>> listRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).forList();
        MatcherAssert.assertThat(listRepositoryPresenter.getItemCount(DataBindingRepositoryPresentersTest.STRING_LIST), Matchers.is(DataBindingRepositoryPresentersTest.STRING_LIST.size()));
    }

    @Test
    public void shouldReturnZeroForCountOfRepositoryPresenterOfFailedResultList() {
        final RepositoryPresenter<Result<List<String>>> resultListRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).forResultList();
        MatcherAssert.assertThat(resultListRepositoryPresenter.getItemCount(DataBindingRepositoryPresentersTest.LIST_FAILURE), Matchers.is(0));
    }

    @Test
    public void shouldReturnListSizeForCountOfRepositoryPresenterOfSuccessfulResultList() {
        final RepositoryPresenter<Result<List<String>>> resultListRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).forResultList();
        MatcherAssert.assertThat(resultListRepositoryPresenter.getItemCount(DataBindingRepositoryPresentersTest.STRING_LIST_RESULT), Matchers.is(DataBindingRepositoryPresentersTest.STRING_LIST.size()));
    }

    @Test
    public void shouldGenerateLayoutForItemOfRepositoryPresenterOfResultList() {
        final RepositoryPresenter<Result<List<String>>> resultListRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layoutForItem(layoutForItem).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).forResultList();
        MatcherAssert.assertThat(resultListRepositoryPresenter.getLayoutResId(DataBindingRepositoryPresentersTest.STRING_LIST_RESULT, 1), Matchers.is(DataBindingRepositoryPresentersTest.DYNAMIC_LAYOUT_ID));
    }

    @Test
    public void shouldGenerateItemIdForItemOfRepositoryPresenterOfResultList() {
        final RepositoryPresenter<Result<List<String>>> resultListRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemIdForItem(itemIdForItem).forResultList();
        resultListRepositoryPresenter.bind(DataBindingRepositoryPresentersTest.STRING_LIST_RESULT, 1, viewHolder);
    }

    @Test
    public void shouldReturnStableIdForRepositoryPresenterOfItem() {
        final RepositoryPresenter<String> resultRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).stableIdForItem(Functions.<String, Long>staticFunction(DataBindingRepositoryPresentersTest.STABLE_ID)).forItem();
        MatcherAssert.assertThat(resultRepositoryPresenter.getItemId(DataBindingRepositoryPresentersTest.STRING, 0), Matchers.is(DataBindingRepositoryPresentersTest.STABLE_ID));
    }

    @Test
    public void shouldReturnStableIdForRepositoryPresenterOfResult() {
        final RepositoryPresenter<Result<String>> resultRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).stableIdForItem(Functions.<String, Long>staticFunction(DataBindingRepositoryPresentersTest.STABLE_ID)).forResult();
        MatcherAssert.assertThat(resultRepositoryPresenter.getItemId(DataBindingRepositoryPresentersTest.STRING_RESULT, 0), Matchers.is(DataBindingRepositoryPresentersTest.STABLE_ID));
    }

    @Test
    public void shouldReturnStaticStableIdForRepositoryPresenterOfItem() {
        final RepositoryPresenter<String> resultRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).stableId(DataBindingRepositoryPresentersTest.STABLE_ID).forItem();
        MatcherAssert.assertThat(resultRepositoryPresenter.getItemId(DataBindingRepositoryPresentersTest.STRING, 0), Matchers.is(DataBindingRepositoryPresentersTest.STABLE_ID));
    }

    @Test
    public void shouldReturnStaticStableIdForRepositoryPresenterOfResult() {
        final RepositoryPresenter<Result<String>> resultRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).stableId(DataBindingRepositoryPresentersTest.STABLE_ID).forResult();
        MatcherAssert.assertThat(resultRepositoryPresenter.getItemId(DataBindingRepositoryPresentersTest.STRING_RESULT, 0), Matchers.is(DataBindingRepositoryPresentersTest.STABLE_ID));
    }

    @Test
    public void shouldReturnStableIdForRepositoryPresenterOfResultList() {
        final RepositoryPresenter<Result<List<String>>> resultListRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).stableIdForItem(Functions.<String, Long>staticFunction(DataBindingRepositoryPresentersTest.STABLE_ID)).forResultList();
        MatcherAssert.assertThat(resultListRepositoryPresenter.getItemId(DataBindingRepositoryPresentersTest.STRING_LIST_RESULT, 0), Matchers.is(DataBindingRepositoryPresentersTest.STABLE_ID));
    }

    @Test
    public void shouldReturnStableIdForRepositoryPresenterOfList() {
        final RepositoryPresenter<List<String>> listRepositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).stableIdForItem(Functions.<String, Long>staticFunction(DataBindingRepositoryPresentersTest.STABLE_ID)).forList();
        MatcherAssert.assertThat(listRepositoryPresenter.getItemId(DataBindingRepositoryPresentersTest.STRING_LIST, 0), Matchers.is(DataBindingRepositoryPresentersTest.STABLE_ID));
    }

    @Test
    public void shouldHandleRebindWithSameData() {
        final RepositoryPresenter<String> repositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).forItem();
        repositoryPresenter.bind(DataBindingRepositoryPresentersTest.STRING, 0, viewHolder);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.ITEM_ID, DataBindingRepositoryPresentersTest.STRING);
        Mockito.verify(viewDataBinding).executePendingBindings();
        Mockito.verifyNoMoreInteractions(viewDataBinding);
        Mockito.reset(viewDataBinding);
        repositoryPresenter.bind(DataBindingRepositoryPresentersTest.STRING, 0, viewHolder);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.ITEM_ID, DataBindingRepositoryPresentersTest.STRING);
        Mockito.verify(viewDataBinding).executePendingBindings();
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldHandleRebindWithNewData() {
        final RepositoryPresenter<String> repositoryPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).forItem();
        repositoryPresenter.bind(DataBindingRepositoryPresentersTest.STRING, 0, viewHolder);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.ITEM_ID, DataBindingRepositoryPresentersTest.STRING);
        Mockito.verify(viewDataBinding).executePendingBindings();
        Mockito.verifyNoMoreInteractions(viewDataBinding);
        Mockito.reset(viewDataBinding);
        repositoryPresenter.bind(DataBindingRepositoryPresentersTest.SECOND_STRING, 0, viewHolder);
        Mockito.verify(viewDataBinding).setVariable(DataBindingRepositoryPresentersTest.ITEM_ID, DataBindingRepositoryPresentersTest.SECOND_STRING);
        Mockito.verify(viewDataBinding).executePendingBindings();
        Mockito.verifyNoMoreInteractions(viewDataBinding);
    }

    @Test
    public void shouldRefuseFineGrainedEventsWithoutDiffWith() {
        final RepositoryPresenter<String> presenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).forItem();
        final boolean fineGrained = presenter.getUpdates("String1", "String2", listUpdateCallback);
        MatcherAssert.assertThat(fineGrained, Matchers.is(false));
    }

    @Test
    public void shouldNotifyFineGrainedEventsWithDiffWith() {
        final List<String> oldData = Arrays.asList("A:1", "B:2", "C:3");
        final List<String> newData = Arrays.asList("B:2", "A:4", "C:5");
        final DiffingLogic diffingLogic = new DiffingLogic(oldData, newData);
        final RepositoryPresenter<List<String>> diffingPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).diffWith(diffingLogic, false).forList();
        final boolean fineGrained = diffingPresenter.getUpdates(oldData, newData, listUpdateCallback);
        MatcherAssert.assertThat(fineGrained, Matchers.is(true));
        DiffUtil.calculateDiff(diffingLogic, false).dispatchUpdatesTo(VerifyingWrappers.verifyingWrapper(listUpdateCallback));
        Mockito.verifyNoMoreInteractions(listUpdateCallback);
    }

    @Test
    public void shouldNotifyFineGrainedEventsWithDiffWithMoveDetection() {
        final List<String> oldData = Arrays.asList("A:1", "B:2", "C:3", "D:0");
        final List<String> newData = Arrays.asList("B:2", "D:0", "A:4", "C:5");
        final DiffingLogic diffingLogic = new DiffingLogic(oldData, newData);
        final RepositoryPresenter<List<String>> diffingPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).diffWith(diffingLogic, true).forCollection(Functions.<List<String>>identityFunction());
        final boolean fineGrained = diffingPresenter.getUpdates(oldData, newData, listUpdateCallback);
        MatcherAssert.assertThat(fineGrained, Matchers.is(true));
        DiffUtil.calculateDiff(diffingLogic, true).dispatchUpdatesTo(VerifyingWrappers.verifyingWrapper(listUpdateCallback));
        Mockito.verifyNoMoreInteractions(listUpdateCallback);
    }

    @Test
    public void shouldNotifySingleItemFineGrainedEventsWithDiff() {
        final Result<String> withA = Result.success("A");
        final Result<String> withB = Result.success("B");
        final Result<String> without = Result.failure();
        final RepositoryPresenter<Result<String>> diffingPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).diff().forResult();
        boolean fineGrained = diffingPresenter.getUpdates(withA, withB, listUpdateCallback);
        MatcherAssert.assertThat(fineGrained, Matchers.is(true));
        Mockito.verify(listUpdateCallback).onChanged(0, 1, null);
        Mockito.verifyNoMoreInteractions(listUpdateCallback);
        fineGrained = diffingPresenter.getUpdates(withA, without, listUpdateCallback);
        MatcherAssert.assertThat(fineGrained, Matchers.is(true));
        Mockito.verify(listUpdateCallback).onRemoved(0, 1);
        Mockito.verifyNoMoreInteractions(listUpdateCallback);
        fineGrained = diffingPresenter.getUpdates(without, withB, listUpdateCallback);
        MatcherAssert.assertThat(fineGrained, Matchers.is(true));
        Mockito.verify(listUpdateCallback).onInserted(0, 1);
        Mockito.verifyNoMoreInteractions(listUpdateCallback);
    }

    @Test
    public void shouldNotifyBlanketChangeEventForSameObjectForOldAndNewData() {
        final List<String> oneList = Arrays.asList("A:0", "B:1");
        final DiffingLogic diffingLogic = new DiffingLogic(oneList, oneList);
        final RepositoryPresenter<List<String>> diffingPresenter = DataBindingRepositoryPresenters.dataBindingRepositoryPresenterOf(String.class).layout(DataBindingRepositoryPresentersTest.LAYOUT_ID).itemId(DataBindingRepositoryPresentersTest.ITEM_ID).diffWith(diffingLogic, false).forList();
        final boolean fineGrained = diffingPresenter.getUpdates(oneList, oneList, listUpdateCallback);
        MatcherAssert.assertThat(fineGrained, Matchers.is(true));
        Mockito.verify(listUpdateCallback).onChanged(0, oneList.size(), null);
        Mockito.verifyNoMoreInteractions(listUpdateCallback);
    }

    @Test
    public void shouldHavePrivateConstructor() {
        MatcherAssert.assertThat(DataBindingRepositoryPresenters.class, HasPrivateConstructor.hasPrivateConstructor());
    }

    private static final class StringToFirstCharStringList implements Function<String, List<String>> {
        @NonNull
        @Override
        public List<String> apply(@NonNull
        final String input) {
            return Collections.singletonList(String.valueOf(input.charAt(0)));
        }
    }
}

