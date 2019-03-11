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


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.agera.MutableRepository;
import com.google.android.agera.Repository;
import com.google.android.agera.UpdateDispatcher;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
@SuppressWarnings("unchecked")
public final class RepositoryAdapterTest {
    private static final int MULTI_ITEM_COUNT = 3;

    private static final int STATIC_ITEM_COUNT = 6;

    private static final List<String> REPOSITORY_LIST = Arrays.asList("a", "b", "c");

    private static final String REPOSITORY_ITEM = "d";

    private static final String ALTERNATIVE_REPOSITORY_ITEM = "e";

    private static final String ITEM = "f";

    @LayoutRes
    private static final int LAYOUT_ID = 3;

    @Mock
    private RepositoryPresenter repositoryPresenter;

    @Mock
    private RepositoryPresenter secondRepositoryPresenter;

    @Mock
    private RepositoryPresenter singleItemRepositoryPresenter;

    @Mock
    private RepositoryPresenter multiItemRepositoryPresenter;

    @Mock
    private RepositoryPresenter zeroItemRepositoryPresenter;

    @Mock
    private LayoutPresenter layoutPresenter;

    @Mock
    private LayoutPresenter secondLayoutPresenter;

    @Mock
    private ViewHolder viewHolder;

    @Mock
    private ViewGroup viewGroup;

    @Mock
    private Context context;

    @Mock
    private LayoutInflater layoutInflater;

    @Mock
    private View view;

    @Mock
    private Activity activity;

    @Mock
    private Application application;

    @Mock
    private AdapterDataObserver observer;

    private UpdateDispatcher updateDispatcher;

    private MutableRepository repository;

    private Repository secondRepository;

    private RepositoryAdapter repositoryAdapter;

    private RepositoryAdapter repositoryAdapterWithoutStatic;

    private Adapter repositoryAdapterWhileResumed;

    private Adapter repositoryAdapterWhileStarted;

    @Test
    public void shouldReturnItemCountFromPresenters() {
        MatcherAssert.assertThat(repositoryAdapter.getItemCount(), Matchers.is(10));
    }

    @Test
    public void shouldReturnItemIdFromFirstPresenter() {
        Mockito.when(repositoryPresenter.getItemId(RepositoryAdapterTest.REPOSITORY_ITEM, 0)).thenReturn(10L);
        MatcherAssert.assertThat(repositoryAdapter.getItemId(0), Matchers.is((10L + (RepositoryAdapterTest.STATIC_ITEM_COUNT))));
        Mockito.verify(secondRepositoryPresenter, Mockito.never()).getItemId(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
    }

    @Test
    public void shouldReturnItemIdFromFirstPresenterWithoutStatic() {
        Mockito.when(repositoryPresenter.getItemId(RepositoryAdapterTest.REPOSITORY_ITEM, 0)).thenReturn(10L);
        MatcherAssert.assertThat(repositoryAdapterWithoutStatic.getItemId(0), Matchers.is(10L));
    }

    @Test
    public void shouldReturnItemIdFromSecondPresenter() {
        Mockito.when(secondRepositoryPresenter.getItemId(RepositoryAdapterTest.REPOSITORY_LIST, 0)).thenReturn(11L);
        MatcherAssert.assertThat(repositoryAdapter.getItemId(1), Matchers.is((11L + (RepositoryAdapterTest.STATIC_ITEM_COUNT))));
        Mockito.verify(repositoryPresenter, Mockito.never()).getItemId(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
    }

    @Test
    public void shouldReturnItemIdForStaticItems() {
        // See comments at repositoryAdapter initialization in setUp()
        for (int i = 4; i <= 9; i++) {
            MatcherAssert.assertThat(repositoryAdapter.getItemId(i), Matchers.is((i - 4L)));
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowExceptionForOutOfBoundsIndex() {
        repositoryAdapter.getItemId(10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowExceptionForNegativeIndex() {
        repositoryAdapter.getItemId((-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForAdapterWithoutRepositories() {
        RepositoryAdapter.repositoryAdapter().addAdditionalObservable(updateDispatcher).build();
    }

    @Test
    public void shouldReturnItemViewTypeFromFirstPresenter() {
        Mockito.when(repositoryPresenter.getLayoutResId(RepositoryAdapterTest.REPOSITORY_ITEM, 0)).thenReturn(1);
        MatcherAssert.assertThat(repositoryAdapter.getItemViewType(0), Matchers.is(1));
    }

    @Test
    public void shouldReturnItemViewTypeFromSecondPresenter() {
        Mockito.when(secondRepositoryPresenter.getLayoutResId(RepositoryAdapterTest.REPOSITORY_LIST, 0)).thenReturn(2);
        MatcherAssert.assertThat(repositoryAdapter.getItemViewType(1), Matchers.is(2));
    }

    @Test
    public void shouldReturnItemViewTypeFromLayoutPresenter() {
        Mockito.when(layoutPresenter.getLayoutResId()).thenReturn(34);
        MatcherAssert.assertThat(repositoryAdapter.getItemViewType(4), Matchers.is(34));
    }

    @Test
    public void shouldReturnItemViewTypeFromItemPresenter() {
        Mockito.when(multiItemRepositoryPresenter.getLayoutResId(RepositoryAdapterTest.ITEM, 2)).thenReturn(42);
        MatcherAssert.assertThat(repositoryAdapter.getItemViewType(8), Matchers.is(42));
    }

    @Test
    public void shouldCreateViewHolder() {
        MatcherAssert.assertThat(repositoryAdapter.onCreateViewHolder(viewGroup, RepositoryAdapterTest.LAYOUT_ID).itemView, Matchers.is(view));
    }

    @Test
    public void shouldNotifyChangeOnStartObservingAfterUsage() {
        repositoryAdapter.registerAdapterDataObserver(observer);
        repositoryAdapter.getItemCount();// "usage".

        // Because the adapter is not observing any data change, after the usage, it doesn't know of any
        // change of data, so for extra guarantee, it'll notify change as soon as it starts observing.
        repositoryAdapter.startObserving();
        runUiThreadTasksIncludingDelayedTasks();
        repositoryAdapter.stopObserving();
        Mockito.verify(observer).onChanged();
    }

    @Test
    public void shouldNotifyChangeOnAdditionalObservablesUpdateWhenObservingAndObserved() {
        repositoryAdapter.startObserving();
        repositoryAdapter.registerAdapterDataObserver(observer);
        runUiThreadTasksIncludingDelayedTasks();
        Mockito.verify(observer, Mockito.never()).onChanged();
        repositoryAdapter.getItemCount();// usage before event

        updateDispatcher.update();
        runUiThreadTasksIncludingDelayedTasks();
        repositoryAdapter.stopObserving();
        Mockito.verify(observer).onChanged();
    }

    @Test
    public void shouldCallRecycleForOnViewRecycled() {
        Mockito.when(repositoryPresenter.getItemCount(RepositoryAdapterTest.ALTERNATIVE_REPOSITORY_ITEM)).thenReturn(1);
        repositoryAdapter.getItemCount();// Trigger a refresh

        repositoryAdapter.startObserving();
        repository.accept(RepositoryAdapterTest.ALTERNATIVE_REPOSITORY_ITEM);
        runUiThreadTasksIncludingDelayedTasks();
        repositoryAdapter.stopObserving();
        repositoryAdapter.onBindViewHolder(viewHolder, 0);
        repositoryAdapter.onViewRecycled(viewHolder);
        Mockito.verify(repositoryPresenter).recycle(viewHolder);
    }

    @Test
    public void shouldCallRecycleForOnFailedToRecycleView() {
        Mockito.when(repositoryPresenter.getItemCount(RepositoryAdapterTest.ALTERNATIVE_REPOSITORY_ITEM)).thenReturn(1);
        repositoryAdapter.getItemCount();// Trigger a refresh

        repositoryAdapter.startObserving();
        repository.accept(RepositoryAdapterTest.ALTERNATIVE_REPOSITORY_ITEM);
        runUiThreadTasksIncludingDelayedTasks();
        repositoryAdapter.stopObserving();
        repositoryAdapter.onBindViewHolder(viewHolder, 0);
        repositoryAdapter.onFailedToRecycleView(viewHolder);
        Mockito.verify(repositoryPresenter).recycle(viewHolder);
    }

    @Test
    public void shouldCallRecycleForOnViewRecycledForSecondPresenter() {
        Mockito.when(repositoryPresenter.getItemCount(RepositoryAdapterTest.ALTERNATIVE_REPOSITORY_ITEM)).thenReturn(1);
        repositoryAdapter.getItemCount();// Trigger a refresh

        repositoryAdapter.startObserving();
        repository.accept(RepositoryAdapterTest.ALTERNATIVE_REPOSITORY_ITEM);
        runUiThreadTasksIncludingDelayedTasks();
        repositoryAdapter.stopObserving();
        repositoryAdapter.onBindViewHolder(viewHolder, 1);
        repositoryAdapter.onViewRecycled(viewHolder);
        Mockito.verify(secondRepositoryPresenter).recycle(viewHolder);
    }

    @Test
    public void shouldCallRecycleForOnFailedToRecycleViewForSecondPresenter() {
        Mockito.when(repositoryPresenter.getItemCount(RepositoryAdapterTest.ALTERNATIVE_REPOSITORY_ITEM)).thenReturn(1);
        repositoryAdapter.getItemCount();// Trigger a refresh

        repositoryAdapter.startObserving();
        repository.accept(RepositoryAdapterTest.ALTERNATIVE_REPOSITORY_ITEM);
        runUiThreadTasksIncludingDelayedTasks();
        repositoryAdapter.stopObserving();
        repositoryAdapter.onBindViewHolder(viewHolder, 1);
        repositoryAdapter.onFailedToRecycleView(viewHolder);
        Mockito.verify(secondRepositoryPresenter).recycle(viewHolder);
    }

    @Test
    public void shouldCallRecycleForOnViewRecycledForLayoutPresenter() {
        repositoryAdapter.getItemCount();// Trigger a refresh

        repositoryAdapter.startObserving();
        updateDispatcher.update();
        runUiThreadTasksIncludingDelayedTasks();
        repositoryAdapter.stopObserving();
        final ViewHolder viewHolder = new ViewHolder(view) {};
        repositoryAdapter.onBindViewHolder(viewHolder, 4);
        repositoryAdapter.onViewRecycled(viewHolder);
        Mockito.verify(layoutPresenter).recycle(view);
    }

    @Test
    public void shouldCallRecycleForOnFailedToRecycleViewForLayoutPresenter() {
        repositoryAdapter.getItemCount();// Trigger a refresh

        repositoryAdapter.startObserving();
        updateDispatcher.update();
        runUiThreadTasksIncludingDelayedTasks();
        repositoryAdapter.stopObserving();
        final ViewHolder viewHolder = new ViewHolder(view) {};
        repositoryAdapter.onBindViewHolder(viewHolder, 4);
        repositoryAdapter.onFailedToRecycleView(viewHolder);
        Mockito.verify(layoutPresenter).recycle(view);
    }

    @Test
    public void shouldCallRecycleForOnViewRecycledForItemPresenter() {
        repositoryAdapter.getItemCount();// Trigger a refresh

        repositoryAdapter.startObserving();
        updateDispatcher.update();
        runUiThreadTasksIncludingDelayedTasks();
        repositoryAdapter.stopObserving();
        final ViewHolder viewHolder = new ViewHolder(view) {};
        repositoryAdapter.onBindViewHolder(viewHolder, 5);
        repositoryAdapter.onViewRecycled(viewHolder);
        Mockito.verify(singleItemRepositoryPresenter).recycle(viewHolder);
    }

    @Test
    public void shouldCallRecycleForOnFailedToRecycleViewForItemPresenter() {
        repositoryAdapter.getItemCount();// Trigger a refresh

        repositoryAdapter.startObserving();
        updateDispatcher.update();
        runUiThreadTasksIncludingDelayedTasks();
        repositoryAdapter.stopObserving();
        final ViewHolder viewHolder = new ViewHolder(view) {};
        repositoryAdapter.onBindViewHolder(viewHolder, 5);
        repositoryAdapter.onFailedToRecycleView(viewHolder);
        Mockito.verify(singleItemRepositoryPresenter).recycle(viewHolder);
    }

    @Test
    public void shouldBindLayoutPresenter() {
        repositoryAdapter.getItemCount();// Trigger a refresh

        repositoryAdapter.startObserving();
        updateDispatcher.update();
        runUiThreadTasksIncludingDelayedTasks();
        repositoryAdapter.stopObserving();
        final ViewHolder viewHolder = new ViewHolder(view) {};
        repositoryAdapter.onBindViewHolder(viewHolder, 4);
        Mockito.verify(layoutPresenter).bind(view);
    }

    @Test
    public void shouldBindItemPresenter() {
        repositoryAdapter.getItemCount();// Trigger a refresh

        repositoryAdapter.startObserving();
        updateDispatcher.update();
        runUiThreadTasksIncludingDelayedTasks();
        repositoryAdapter.stopObserving();
        final ViewHolder viewHolder = new ViewHolder(view) {};
        repositoryAdapter.onBindViewHolder(viewHolder, 7);
        Mockito.verify(multiItemRepositoryPresenter).bind(RepositoryAdapterTest.ITEM, 1, viewHolder);
    }

    @Test
    public void shouldUpdateOnChangingRepositoryWhenObserving() {
        Mockito.when(repositoryPresenter.getItemCount(RepositoryAdapterTest.ALTERNATIVE_REPOSITORY_ITEM)).thenReturn(1);
        repositoryAdapter.getItemCount();// Trigger a refresh

        repositoryAdapter.startObserving();
        repository.accept(RepositoryAdapterTest.ALTERNATIVE_REPOSITORY_ITEM);
        runUiThreadTasksIncludingDelayedTasks();
        repositoryAdapter.stopObserving();
        repositoryAdapter.onBindViewHolder(viewHolder, 0);
        Mockito.verify(repositoryPresenter).bind(RepositoryAdapterTest.ALTERNATIVE_REPOSITORY_ITEM, 0, viewHolder);
    }

    @Test
    public void shouldNotUpdateOnChangingRepositoryWhenNotObserving() {
        repositoryAdapter.getItemCount();// Trigger a refresh

        repository.accept(RepositoryAdapterTest.ALTERNATIVE_REPOSITORY_ITEM);
        runUiThreadTasksIncludingDelayedTasks();
        repositoryAdapter.onBindViewHolder(viewHolder, 0);
        Mockito.verify(repositoryPresenter).bind(RepositoryAdapterTest.REPOSITORY_ITEM, 0, viewHolder);
    }

    @Test
    public void shouldUpdateOnChangingRepositoryWhenStarted() {
        repositoryAdapterWhileStarted = RepositoryAdapter.repositoryAdapter().add(repository, repositoryPresenter).add(secondRepository, secondRepositoryPresenter).addAdditionalObservable(updateDispatcher).whileStarted(activity);
        Mockito.when(repositoryPresenter.getItemCount(RepositoryAdapterTest.ALTERNATIVE_REPOSITORY_ITEM)).thenReturn(1);
        repositoryAdapterWhileStarted.getItemCount();// Trigger a refresh

        setActivityToStarted();
        repository.accept(RepositoryAdapterTest.ALTERNATIVE_REPOSITORY_ITEM);
        runUiThreadTasksIncludingDelayedTasks();
        setActivityToStopped();
        repositoryAdapterWhileStarted.onBindViewHolder(viewHolder, 0);
        Mockito.verify(repositoryPresenter).bind(RepositoryAdapterTest.ALTERNATIVE_REPOSITORY_ITEM, 0, viewHolder);
    }

    @Test
    public void shouldNotUpdateOnChangingRepositoryWhenNotStarted() {
        repositoryAdapterWhileStarted = RepositoryAdapter.repositoryAdapter().add(repository, repositoryPresenter).add(secondRepository, secondRepositoryPresenter).addAdditionalObservable(updateDispatcher).whileStarted(activity);
        repositoryAdapterWhileStarted.getItemCount();// Trigger a refresh

        repository.accept(RepositoryAdapterTest.ALTERNATIVE_REPOSITORY_ITEM);
        runUiThreadTasksIncludingDelayedTasks();
        repositoryAdapterWhileStarted.onBindViewHolder(viewHolder, 0);
        Mockito.verify(repositoryPresenter).bind(RepositoryAdapterTest.REPOSITORY_ITEM, 0, viewHolder);
    }

    @Test
    public void shouldUpdateOnChangingRepositoryWhenResumed() {
        repositoryAdapterWhileResumed = RepositoryAdapter.repositoryAdapter().add(repository, repositoryPresenter).add(secondRepository, secondRepositoryPresenter).addAdditionalObservable(updateDispatcher).whileResumed(activity);
        Mockito.when(repositoryPresenter.getItemCount(RepositoryAdapterTest.ALTERNATIVE_REPOSITORY_ITEM)).thenReturn(1);
        repositoryAdapterWhileResumed.getItemCount();// Trigger a refresh

        setActivityToResumed();
        repository.accept(RepositoryAdapterTest.ALTERNATIVE_REPOSITORY_ITEM);
        runUiThreadTasksIncludingDelayedTasks();
        setActivityToPaused();
        repositoryAdapterWhileResumed.onBindViewHolder(viewHolder, 0);
        Mockito.verify(repositoryPresenter).bind(RepositoryAdapterTest.ALTERNATIVE_REPOSITORY_ITEM, 0, viewHolder);
    }

    @Test
    public void shouldNotUpdateOnChangingRepositoryWhenNotResumed() {
        repositoryAdapterWhileResumed = RepositoryAdapter.repositoryAdapter().add(repository, repositoryPresenter).add(secondRepository, secondRepositoryPresenter).addAdditionalObservable(updateDispatcher).whileResumed(activity);
        repositoryAdapterWhileResumed.getItemCount();// Trigger a refresh

        repository.accept(RepositoryAdapterTest.ALTERNATIVE_REPOSITORY_ITEM);
        runUiThreadTasksIncludingDelayedTasks();
        repositoryAdapterWhileResumed.onBindViewHolder(viewHolder, 0);
        Mockito.verify(repositoryPresenter).bind(RepositoryAdapterTest.REPOSITORY_ITEM, 0, viewHolder);
    }

    @Test
    public void shouldDoNothingOnActivityCreatedForWhileResumed() {
        repositoryAdapterWhileResumed = RepositoryAdapter.repositoryAdapter().add(repository, repositoryPresenter).add(secondRepository, secondRepositoryPresenter).addAdditionalObservable(updateDispatcher).whileResumed(activity);
        runUiThreadTasksIncludingDelayedTasks();
        setActivityToCreated();
    }

    @Test
    public void shouldDoNothingOnActivityDestroyedForWhileResumed() {
        repositoryAdapterWhileResumed = RepositoryAdapter.repositoryAdapter().add(repository, repositoryPresenter).add(secondRepository, secondRepositoryPresenter).addAdditionalObservable(updateDispatcher).whileResumed(activity);
        runUiThreadTasksIncludingDelayedTasks();
        setActivityToDestroyed();
    }

    @Test
    public void shouldDoNothingOnActivityCreatedForWhileStarted() {
        repositoryAdapterWhileResumed = RepositoryAdapter.repositoryAdapter().add(repository, repositoryPresenter).add(secondRepository, secondRepositoryPresenter).addAdditionalObservable(updateDispatcher).whileStarted(activity);
        runUiThreadTasksIncludingDelayedTasks();
        setActivityToCreated();
    }

    @Test
    public void shouldDoNothingOnActivityDestroyedForWhileStarted() {
        repositoryAdapterWhileResumed = RepositoryAdapter.repositoryAdapter().add(repository, repositoryPresenter).add(secondRepository, secondRepositoryPresenter).addAdditionalObservable(updateDispatcher).whileStarted(activity);
        runUiThreadTasksIncludingDelayedTasks();
        setActivityToDestroyed();
    }

    @Test
    public void shouldDoNothingOnActivityStoppedForWhileResumed() {
        repositoryAdapterWhileResumed = RepositoryAdapter.repositoryAdapter().add(repository, repositoryPresenter).add(secondRepository, secondRepositoryPresenter).addAdditionalObservable(updateDispatcher).whileResumed(activity);
        runUiThreadTasksIncludingDelayedTasks();
        setActivityToStopped();
    }

    @Test
    public void shouldDoNothingOnActivityStartedForWhileResumed() {
        repositoryAdapterWhileResumed = RepositoryAdapter.repositoryAdapter().add(repository, repositoryPresenter).add(secondRepository, secondRepositoryPresenter).addAdditionalObservable(updateDispatcher).whileResumed(activity);
        runUiThreadTasksIncludingDelayedTasks();
        setActivityToStarted();
    }

    @Test
    public void shouldDoNothingOnActivityResumedForWhileStarted() {
        repositoryAdapterWhileResumed = RepositoryAdapter.repositoryAdapter().add(repository, repositoryPresenter).add(secondRepository, secondRepositoryPresenter).addAdditionalObservable(updateDispatcher).whileStarted(activity);
        runUiThreadTasksIncludingDelayedTasks();
        setActivityToResumed();
    }

    @Test
    public void shouldDoNothingOnActivityPausedForWhileStarted() {
        repositoryAdapterWhileResumed = RepositoryAdapter.repositoryAdapter().add(repository, repositoryPresenter).add(secondRepository, secondRepositoryPresenter).addAdditionalObservable(updateDispatcher).whileStarted(activity);
        runUiThreadTasksIncludingDelayedTasks();
        setActivityToPaused();
    }

    @Test
    public void shouldDoNothingOnSaveInstanceStateForWhileResumed() {
        repositoryAdapterWhileResumed = RepositoryAdapter.repositoryAdapter().add(repository, repositoryPresenter).add(secondRepository, secondRepositoryPresenter).addAdditionalObservable(updateDispatcher).whileResumed(activity);
        runUiThreadTasksIncludingDelayedTasks();
        saveActivityInstanceState();
    }

    @Test
    public void shouldDoNothingOnSaveInstanceStateForWhileStarted() {
        repositoryAdapterWhileResumed = RepositoryAdapter.repositoryAdapter().add(repository, repositoryPresenter).add(secondRepository, secondRepositoryPresenter).addAdditionalObservable(updateDispatcher).whileStarted(activity);
        runUiThreadTasksIncludingDelayedTasks();
        saveActivityInstanceState();
    }
}

