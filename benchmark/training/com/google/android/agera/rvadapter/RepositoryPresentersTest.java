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


import RecyclerView.ViewHolder;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
import android.view.View;
import com.google.android.agera.Binder;
import com.google.android.agera.Binders;
import com.google.android.agera.Function;
import com.google.android.agera.Functions;
import com.google.android.agera.Receiver;
import com.google.android.agera.Result;
import com.google.android.agera.rvadapter.test.DiffingLogic;
import com.google.android.agera.rvadapter.test.VerifyingWrappers;
import com.google.android.agera.rvadapter.test.matchers.HasPrivateConstructor;
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
public class RepositoryPresentersTest {
    private static final String STRING = "string";

    private static final String FIRST_STRING_CHARACTER = "s";

    private static final String SECOND_STRING = "string2";

    private static final Result<String> STRING_RESULT = Result.present(RepositoryPresentersTest.STRING);

    private static final List<String> STRING_LIST = Arrays.asList(RepositoryPresentersTest.STRING, RepositoryPresentersTest.SECOND_STRING);

    private static final Result<List<String>> STRING_LIST_RESULT = Result.success(RepositoryPresentersTest.STRING_LIST);

    private static final Result<String> FAILURE = Result.failure();

    private static final Result<List<String>> LIST_FAILURE = Result.failure();

    private static final int LAYOUT_ID = 0;

    private static final int DYNAMIC_LAYOUT_ID = 1;

    private static final long STABLE_ID = 2;

    @Mock
    private Binder<String, View> binder;

    @Mock
    private Binder<String, View> collectionBinder;

    @Mock
    private Receiver<View> recycler;

    @Mock
    private Function<String, Integer> layoutForItem;

    private ViewHolder viewHolder;

    @Mock
    private View view;

    @Mock
    private ListUpdateCallback listUpdateCallback;

    @Test
    public void shouldBindRepositoryPresenterOfResult() {
        final RepositoryPresenter<Result<String>> resultRepositoryPresenter = RepositoryPresenters.repositoryPresenterOf(String.class).layout(RepositoryPresentersTest.LAYOUT_ID).bindWith(binder).forResult();
        resultRepositoryPresenter.bind(RepositoryPresentersTest.STRING_RESULT, 0, viewHolder);
        Mockito.verify(binder).bind(RepositoryPresentersTest.STRING, view);
    }

    @Test
    public void shouldBindRepositoryPresenterOfResultWithoutBinder() {
        final RepositoryPresenter<Result<String>> resultRepositoryPresenter = RepositoryPresenters.repositoryPresenterOf(String.class).layout(RepositoryPresentersTest.LAYOUT_ID).forResult();
        resultRepositoryPresenter.bind(RepositoryPresentersTest.STRING_RESULT, 0, viewHolder);
    }

    @Test
    public void shouldBindRepositoryPresenterOfResultList() {
        final RepositoryPresenter<Result<List<String>>> resultListRepositoryPresenter = RepositoryPresenters.repositoryPresenterOf(String.class).layout(RepositoryPresentersTest.LAYOUT_ID).bindWith(binder).forResultList();
        resultListRepositoryPresenter.bind(RepositoryPresentersTest.STRING_LIST_RESULT, 1, viewHolder);
        Mockito.verify(binder).bind(RepositoryPresentersTest.SECOND_STRING, view);
    }

    @Test
    public void shouldBindRepositoryPresenterOfItem() {
        final RepositoryPresenter<String> itemRepositoryPresenter = RepositoryPresenters.repositoryPresenterOf(String.class).layout(RepositoryPresentersTest.LAYOUT_ID).bindWith(binder).forItem();
        itemRepositoryPresenter.bind(RepositoryPresentersTest.STRING, 0, viewHolder);
        Mockito.verify(binder).bind(RepositoryPresentersTest.STRING, view);
    }

    @Test
    public void shouldBindRepositoryPresenterOfList() {
        final RepositoryPresenter<List<String>> listRepositoryPresenter = RepositoryPresenters.repositoryPresenterOf(String.class).layout(RepositoryPresentersTest.LAYOUT_ID).bindWith(binder).forList();
        listRepositoryPresenter.bind(RepositoryPresentersTest.STRING_LIST, 1, viewHolder);
        Mockito.verify(binder).bind(RepositoryPresentersTest.SECOND_STRING, view);
    }

    @Test
    public void shouldBindRepositoryPresenterOfCollection() {
        final RepositoryPresenter<String> repositoryPresenter = RepositoryPresenters.repositoryPresenterOf(String.class).layout(RepositoryPresentersTest.LAYOUT_ID).bindWith(binder).forCollection(new Function<String, List<String>>() {
            @NonNull
            @Override
            public List<String> apply(@NonNull
            final String input) {
                return Collections.singletonList(String.valueOf(input.charAt(0)));
            }
        });
        repositoryPresenter.bind(RepositoryPresentersTest.STRING, 0, viewHolder);
        Mockito.verify(binder).bind(RepositoryPresentersTest.FIRST_STRING_CHARACTER, view);
    }

    @Test
    public void shouldBindRepositoryPresenterCollectionOfCollection() {
        final RepositoryPresenter<String> repositoryPresenter = RepositoryPresenters.repositoryPresenterOf(String.class).layout(RepositoryPresentersTest.LAYOUT_ID).bindWith(binder).bindCollectionWith(collectionBinder).forCollection(new Function<String, List<String>>() {
            @NonNull
            @Override
            public List<String> apply(@NonNull
            final String input) {
                return Collections.singletonList(String.valueOf(input.charAt(0)));
            }
        });
        repositoryPresenter.bind(RepositoryPresentersTest.STRING, 0, viewHolder);
        Mockito.verify(binder).bind(RepositoryPresentersTest.FIRST_STRING_CHARACTER, view);
        Mockito.verify(collectionBinder).bind(RepositoryPresentersTest.STRING, view);
    }

    @Test
    public void shouldRecycleViewInRepositoryPresenter() {
        final RepositoryPresenter<List<String>> listRepositoryPresenter = RepositoryPresenters.repositoryPresenterOf(String.class).layout(RepositoryPresentersTest.LAYOUT_ID).bindWith(binder).recycleWith(recycler).forList();
        listRepositoryPresenter.bind(RepositoryPresentersTest.STRING_LIST, 1, viewHolder);
        listRepositoryPresenter.recycle(viewHolder);
        Mockito.verify(recycler).accept(view);
    }

    @Test
    public void shouldHandleRecycleWithoutRecycler() {
        final RepositoryPresenter<List<String>> listRepositoryPresenter = RepositoryPresenters.repositoryPresenterOf(String.class).layout(RepositoryPresentersTest.LAYOUT_ID).bindWith(binder).forList();
        listRepositoryPresenter.bind(RepositoryPresentersTest.STRING_LIST, 1, viewHolder);
        listRepositoryPresenter.recycle(viewHolder);
    }

    @Test
    public void shouldReturnZeroForCountOfRepositoryPresenterOfFailedResult() {
        final RepositoryPresenter<Result<String>> resultRepositoryPresenter = RepositoryPresenters.repositoryPresenterOf(String.class).layout(RepositoryPresentersTest.LAYOUT_ID).bindWith(binder).forResult();
        MatcherAssert.assertThat(resultRepositoryPresenter.getItemCount(RepositoryPresentersTest.FAILURE), Matchers.is(0));
    }

    @Test
    public void shouldReturnOneForCountOfRepositoryPresenterOfSuccessfulResult() {
        final RepositoryPresenter<Result<String>> resultRepositoryPresenter = RepositoryPresenters.repositoryPresenterOf(String.class).layout(RepositoryPresentersTest.LAYOUT_ID).bindWith(binder).forResult();
        MatcherAssert.assertThat(resultRepositoryPresenter.getItemCount(RepositoryPresentersTest.STRING_RESULT), Matchers.is(1));
    }

    @Test
    public void shouldReturnListSizeForCountOfRepositoryPresenterOfList() {
        final RepositoryPresenter<List<String>> listRepositoryPresenter = RepositoryPresenters.repositoryPresenterOf(String.class).layout(RepositoryPresentersTest.LAYOUT_ID).bindWith(binder).forList();
        MatcherAssert.assertThat(listRepositoryPresenter.getItemCount(RepositoryPresentersTest.STRING_LIST), Matchers.is(RepositoryPresentersTest.STRING_LIST.size()));
    }

    @Test
    public void shouldReturnZeroForCountOfRepositoryPresenterOfFailedResultList() {
        final RepositoryPresenter<Result<List<String>>> resultListRepositoryPresenter = RepositoryPresenters.repositoryPresenterOf(String.class).layout(RepositoryPresentersTest.LAYOUT_ID).bindWith(binder).forResultList();
        MatcherAssert.assertThat(resultListRepositoryPresenter.getItemCount(RepositoryPresentersTest.LIST_FAILURE), Matchers.is(0));
    }

    @Test
    public void shouldReturnListSizeForCountOfRepositoryPresenterOfSuccessfulResultList() {
        final RepositoryPresenter<Result<List<String>>> resultListRepositoryPresenter = RepositoryPresenters.repositoryPresenterOf(String.class).layout(RepositoryPresentersTest.LAYOUT_ID).bindWith(binder).forResultList();
        MatcherAssert.assertThat(resultListRepositoryPresenter.getItemCount(RepositoryPresentersTest.STRING_LIST_RESULT), Matchers.is(RepositoryPresentersTest.STRING_LIST.size()));
    }

    @Test
    public void shouldGenerateLayoutForItemOfRepositoryPresenterOfResultList() {
        final RepositoryPresenter<Result<List<String>>> resultListRepositoryPresenter = RepositoryPresenters.repositoryPresenterOf(String.class).layoutForItem(layoutForItem).forResultList();
        MatcherAssert.assertThat(resultListRepositoryPresenter.getLayoutResId(RepositoryPresentersTest.STRING_LIST_RESULT, 1), Matchers.is(RepositoryPresentersTest.DYNAMIC_LAYOUT_ID));
    }

    @Test
    public void shouldReturnStableIdForRepositoryPresenterOfResult() {
        final RepositoryPresenter<Result<String>> resultRepositoryPresenter = RepositoryPresenters.repositoryPresenterOf(String.class).layout(RepositoryPresentersTest.LAYOUT_ID).stableIdForItem(Functions.<String, Long>staticFunction(RepositoryPresentersTest.STABLE_ID)).forResult();
        MatcherAssert.assertThat(resultRepositoryPresenter.getItemId(RepositoryPresentersTest.STRING_RESULT, 0), Matchers.is(RepositoryPresentersTest.STABLE_ID));
    }

    @Test
    public void shouldReturnStableIdForRepositoryPresenterOfItem() {
        final RepositoryPresenter<String> resultRepositoryPresenter = RepositoryPresenters.repositoryPresenterOf(String.class).layout(RepositoryPresentersTest.LAYOUT_ID).stableIdForItem(Functions.<String, Long>staticFunction(RepositoryPresentersTest.STABLE_ID)).forItem();
        MatcherAssert.assertThat(resultRepositoryPresenter.getItemId(RepositoryPresentersTest.STRING, 0), Matchers.is(RepositoryPresentersTest.STABLE_ID));
    }

    @Test
    public void shouldReturnStaticStableIdForRepositoryPresenterOfResult() {
        final RepositoryPresenter<Result<String>> resultRepositoryPresenter = RepositoryPresenters.repositoryPresenterOf(String.class).layout(RepositoryPresentersTest.LAYOUT_ID).stableId(RepositoryPresentersTest.STABLE_ID).forResult();
        MatcherAssert.assertThat(resultRepositoryPresenter.getItemId(RepositoryPresentersTest.STRING_RESULT, 0), Matchers.is(RepositoryPresentersTest.STABLE_ID));
    }

    @Test
    public void shouldReturnStaticStableIdForRepositoryPresenterOfItem() {
        final RepositoryPresenter<String> resultRepositoryPresenter = RepositoryPresenters.repositoryPresenterOf(String.class).layout(RepositoryPresentersTest.LAYOUT_ID).stableId(RepositoryPresentersTest.STABLE_ID).forItem();
        MatcherAssert.assertThat(resultRepositoryPresenter.getItemId(RepositoryPresentersTest.STRING, 0), Matchers.is(RepositoryPresentersTest.STABLE_ID));
    }

    @Test
    public void shouldReturnStableIdForRepositoryPresenterOfResultList() {
        final RepositoryPresenter<Result<List<String>>> resultListRepositoryPresenter = RepositoryPresenters.repositoryPresenterOf(String.class).layout(RepositoryPresentersTest.LAYOUT_ID).stableIdForItem(Functions.<String, Long>staticFunction(RepositoryPresentersTest.STABLE_ID)).forResultList();
        MatcherAssert.assertThat(resultListRepositoryPresenter.getItemId(RepositoryPresentersTest.STRING_LIST_RESULT, 1), Matchers.is(RepositoryPresentersTest.STABLE_ID));
    }

    @Test
    public void shouldReturnStableIdForRepositoryPresenterOfList() {
        final RepositoryPresenter<List<String>> listRepositoryPresenter = RepositoryPresenters.repositoryPresenterOf(String.class).layout(RepositoryPresentersTest.LAYOUT_ID).stableIdForItem(Functions.<String, Long>staticFunction(RepositoryPresentersTest.STABLE_ID)).forList();
        MatcherAssert.assertThat(listRepositoryPresenter.getItemId(RepositoryPresentersTest.STRING_LIST, 1), Matchers.is(RepositoryPresentersTest.STABLE_ID));
    }

    @Test
    public void shouldReturnStableIdForRepositoryPresenterOfListWithBinder() {
        final RepositoryPresenter<List<String>> resultRepositoryPresenter = RepositoryPresenters.repositoryPresenterOf(String.class).layout(RepositoryPresentersTest.LAYOUT_ID).stableIdForItem(Functions.<String, Long>staticFunction(RepositoryPresentersTest.STABLE_ID)).bindWith(binder).forList();
        MatcherAssert.assertThat(resultRepositoryPresenter.getItemId(RepositoryPresentersTest.STRING_LIST, 1), Matchers.is(RepositoryPresentersTest.STABLE_ID));
    }

    @Test
    public void shouldAllowStableIdMethodForAnySuperType() {
        RepositoryPresenters.repositoryPresenterOf(String.class).layout(RepositoryPresentersTest.LAYOUT_ID).stableIdForItem(Functions.<CharSequence, Long>staticFunction(RepositoryPresentersTest.STABLE_ID)).forResult();
    }

    @Test
    public void shouldHandleRebindWithNewData() {
        final RepositoryPresenter<String> resultRepositoryPresenter = RepositoryPresenters.repositoryPresenterOf(String.class).layout(RepositoryPresentersTest.LAYOUT_ID).bindWith(binder).forItem();
        resultRepositoryPresenter.bind(RepositoryPresentersTest.STRING, 0, viewHolder);
        Mockito.verify(binder).bind(RepositoryPresentersTest.STRING, view);
        Mockito.reset(binder);
        resultRepositoryPresenter.bind(RepositoryPresentersTest.SECOND_STRING, 0, viewHolder);
        Mockito.verify(binder).bind(RepositoryPresentersTest.SECOND_STRING, view);
    }

    @Test
    public void shouldHandleRebindWithSameData() {
        final RepositoryPresenter<String> resultRepositoryPresenter = RepositoryPresenters.repositoryPresenterOf(String.class).layout(RepositoryPresentersTest.LAYOUT_ID).bindWith(binder).forItem();
        resultRepositoryPresenter.bind(RepositoryPresentersTest.STRING, 0, viewHolder);
        Mockito.verify(binder).bind(RepositoryPresentersTest.STRING, view);
        Mockito.reset(binder);
        resultRepositoryPresenter.bind(RepositoryPresentersTest.STRING, 0, viewHolder);
        Mockito.verify(binder).bind(RepositoryPresentersTest.STRING, view);
    }

    @Test
    public void shouldRefuseFineGrainedEventsWithoutDiffWith() {
        final RepositoryPresenter<String> presenter = RepositoryPresenters.repositoryPresenterOf(String.class).layout(RepositoryPresentersTest.LAYOUT_ID).forItem();
        final boolean fineGrained = presenter.getUpdates("String1", "String2", listUpdateCallback);
        MatcherAssert.assertThat(fineGrained, Matchers.is(false));
    }

    @Test
    public void shouldNotifyFineGrainedEventsWithDiffWith() {
        final List<String> oldData = Arrays.asList("A:1", "B:2", "C:3");
        final List<String> newData = Arrays.asList("B:2", "A:4", "C:5");
        final DiffingLogic diffingLogic = new DiffingLogic(oldData, newData);
        final RepositoryPresenter<List<String>> diffingPresenter = RepositoryPresenters.repositoryPresenterOf(String.class).layout(RepositoryPresentersTest.LAYOUT_ID).diffWith(diffingLogic, false).forList();
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
        final RepositoryPresenter<List<String>> diffingPresenter = // to test compiling this line.
        // restricts to collection
        RepositoryPresenters.repositoryPresenterOf(String.class).layout(RepositoryPresentersTest.LAYOUT_ID).bindCollectionWith(Binders.<List<String>, View>nullBinder()).diffWith(diffingLogic, true).forCollection(Functions.<List<String>>identityFunction());
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
        final RepositoryPresenter<Result<String>> diffingPresenter = RepositoryPresenters.repositoryPresenterOf(String.class).layout(RepositoryPresentersTest.LAYOUT_ID).diff().forResult();
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
        final RepositoryPresenter<List<String>> diffingPresenter = RepositoryPresenters.repositoryPresenterOf(String.class).layout(RepositoryPresentersTest.LAYOUT_ID).diffWith(diffingLogic, false).forList();
        final boolean fineGrained = diffingPresenter.getUpdates(oneList, oneList, listUpdateCallback);
        MatcherAssert.assertThat(fineGrained, Matchers.is(true));
        Mockito.verify(listUpdateCallback).onChanged(0, oneList.size(), null);
        Mockito.verifyNoMoreInteractions(listUpdateCallback);
    }

    @Test
    public void shouldHavePrivateConstructor() {
        MatcherAssert.assertThat(RepositoryPresenters.class, HasPrivateConstructor.hasPrivateConstructor());
    }
}

