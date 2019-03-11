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


import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import com.google.android.agera.MutableRepository;
import com.google.android.agera.UpdateDispatcher;
import com.google.android.agera.rvadapter.test.VerifyingWrappers;
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
public final class RepositoryAdapterFineGrainedEventsTest {
    private static final Object REPOSITORY_VALUE_A = new Object();

    private static final Object REPOSITORY_VALUE_B = new Object();

    private static final Object STATIC_ITEM = new Object();

    private static final int PRESENTER_1_A_ITEM_COUNT = 1;

    private static final int PRESENTER_1_B_ITEM_COUNT = 3;

    private static final int STATIC_ITEM_COUNT = 2;

    private static final int PRESENTER_2_A_ITEM_COUNT = 4;

    private static final int PRESENTER_2_B_ITEM_COUNT = 5;

    private static final Object PAYLOAD_PRESENTER_1_VALUE_A_TO_B = new Object();

    private static final Object PAYLOAD_PRESENTER_1_VALUE_B_REFRESH = new Object();

    private static final Object PAYLOAD_PRESENTER_2_VALUE_B_REFRESH = new Object();

    @Mock
    private RepositoryPresenter mockPresenter1;

    @Mock
    private RepositoryPresenter mockStaticItemPresenter;

    @Mock
    private RepositoryPresenter mockPresenter2;

    @Mock
    private ListUpdateCallback fineGrainedEvents;

    @Mock
    private Runnable onChangeEvent;

    private AdapterDataObserver redirectingObserver;

    private UpdateDispatcher updateDispatcher;

    private MutableRepository repository1;

    private MutableRepository repository2;

    private RepositoryAdapter repositoryAdapter;

    @Test
    public void shouldNotApplyPresenter1ChangesWhenNotObservedUntilGetItemCount() {
        Mockito.when(mockPresenter1.getItemId(RepositoryAdapterFineGrainedEventsTest.REPOSITORY_VALUE_B, 2)).thenReturn(10L);
        repositoryAdapter.unregisterAdapterDataObserver(redirectingObserver);
        repository1.accept(RepositoryAdapterFineGrainedEventsTest.REPOSITORY_VALUE_B);
        runUiThreadTasksIncludingDelayedTasks();
        Mockito.verify(mockPresenter1, Mockito.never()).getItemCount(ArgumentMatchers.any());
        final int itemCount = repositoryAdapter.getItemCount();
        Mockito.verify(mockPresenter1, Mockito.never()).getItemCount(RepositoryAdapterFineGrainedEventsTest.REPOSITORY_VALUE_A);
        Mockito.verify(mockPresenter1).getItemCount(RepositoryAdapterFineGrainedEventsTest.REPOSITORY_VALUE_B);
        MatcherAssert.assertThat(itemCount, Matchers.is((((RepositoryAdapterFineGrainedEventsTest.PRESENTER_1_B_ITEM_COUNT) + (RepositoryAdapterFineGrainedEventsTest.STATIC_ITEM_COUNT)) + (RepositoryAdapterFineGrainedEventsTest.PRESENTER_2_A_ITEM_COUNT))));
        MatcherAssert.assertThat(repositoryAdapter.getItemId(2), Matchers.is((10L + (RepositoryAdapterFineGrainedEventsTest.STATIC_ITEM_COUNT))));
        Mockito.verify(mockPresenter1, Mockito.never()).getUpdates(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(ListUpdateCallback.class));
    }

    @Test
    public void shouldNotApplyPresenter2ChangesEvenWhenObservedUntilGetItemCount() {
        Mockito.when(mockPresenter2.getItemId(RepositoryAdapterFineGrainedEventsTest.REPOSITORY_VALUE_B, 4)).thenReturn(100L);
        repository2.accept(RepositoryAdapterFineGrainedEventsTest.REPOSITORY_VALUE_B);
        runUiThreadTasksIncludingDelayedTasks();
        Mockito.verify(mockPresenter1, Mockito.never()).getItemCount(ArgumentMatchers.any());
        final int itemCount = repositoryAdapter.getItemCount();
        Mockito.verify(mockPresenter2, Mockito.never()).getItemCount(RepositoryAdapterFineGrainedEventsTest.REPOSITORY_VALUE_A);
        Mockito.verify(mockPresenter2).getItemCount(RepositoryAdapterFineGrainedEventsTest.REPOSITORY_VALUE_B);
        MatcherAssert.assertThat(itemCount, Matchers.is((((RepositoryAdapterFineGrainedEventsTest.PRESENTER_1_A_ITEM_COUNT) + (RepositoryAdapterFineGrainedEventsTest.STATIC_ITEM_COUNT)) + (RepositoryAdapterFineGrainedEventsTest.PRESENTER_2_B_ITEM_COUNT))));
        MatcherAssert.assertThat(repositoryAdapter.getItemId((((RepositoryAdapterFineGrainedEventsTest.PRESENTER_1_A_ITEM_COUNT) + (RepositoryAdapterFineGrainedEventsTest.STATIC_ITEM_COUNT)) + 4)), Matchers.is((100L + (RepositoryAdapterFineGrainedEventsTest.STATIC_ITEM_COUNT))));
        Mockito.verify(mockPresenter2, Mockito.never()).getUpdates(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(ListUpdateCallback.class));
    }

    @Test
    public void shouldApplyPresenter1ChangesWithEventsWhenObserved() {
        repositoryAdapter.getItemCount();// usage

        Mockito.when(mockPresenter1.getItemId(RepositoryAdapterFineGrainedEventsTest.REPOSITORY_VALUE_B, 2)).thenReturn(10L);
        repository1.accept(RepositoryAdapterFineGrainedEventsTest.REPOSITORY_VALUE_B);
        runUiThreadTasksIncludingDelayedTasks();
        MatcherAssert.assertThat(repositoryAdapter.getItemCount(), Matchers.is((((RepositoryAdapterFineGrainedEventsTest.PRESENTER_1_B_ITEM_COUNT) + (RepositoryAdapterFineGrainedEventsTest.STATIC_ITEM_COUNT)) + (RepositoryAdapterFineGrainedEventsTest.PRESENTER_2_A_ITEM_COUNT))));
        MatcherAssert.assertThat(repositoryAdapter.getItemId(2), Matchers.is((10L + (RepositoryAdapterFineGrainedEventsTest.STATIC_ITEM_COUNT))));
        Mockito.verify(mockPresenter1).getUpdates(ArgumentMatchers.eq(RepositoryAdapterFineGrainedEventsTest.REPOSITORY_VALUE_A), ArgumentMatchers.eq(RepositoryAdapterFineGrainedEventsTest.REPOSITORY_VALUE_B), ArgumentMatchers.any(ListUpdateCallback.class));
        RepositoryAdapterFineGrainedEventsTest.applyPresenter1FromAToBChanges(VerifyingWrappers.verifyingWrapper(fineGrainedEvents));
        Mockito.verifyNoMoreInteractions(fineGrainedEvents);
        Mockito.verify(onChangeEvent, Mockito.never()).run();
    }

    @Test
    public void shouldApplyPresenter2ChangesWithEventsWhenObserved() {
        repositoryAdapter.getItemCount();// usage

        Mockito.when(mockPresenter2.getItemId(RepositoryAdapterFineGrainedEventsTest.REPOSITORY_VALUE_B, 4)).thenReturn(100L);
        repository2.accept(RepositoryAdapterFineGrainedEventsTest.REPOSITORY_VALUE_B);
        runUiThreadTasksIncludingDelayedTasks();
        MatcherAssert.assertThat(repositoryAdapter.getItemCount(), Matchers.is((((RepositoryAdapterFineGrainedEventsTest.PRESENTER_1_A_ITEM_COUNT) + (RepositoryAdapterFineGrainedEventsTest.STATIC_ITEM_COUNT)) + (RepositoryAdapterFineGrainedEventsTest.PRESENTER_2_B_ITEM_COUNT))));
        MatcherAssert.assertThat(repositoryAdapter.getItemId((((RepositoryAdapterFineGrainedEventsTest.PRESENTER_1_A_ITEM_COUNT) + (RepositoryAdapterFineGrainedEventsTest.STATIC_ITEM_COUNT)) + 4)), Matchers.is((100L + (RepositoryAdapterFineGrainedEventsTest.STATIC_ITEM_COUNT))));
        Mockito.verify(mockPresenter2).getUpdates(ArgumentMatchers.eq(RepositoryAdapterFineGrainedEventsTest.REPOSITORY_VALUE_A), ArgumentMatchers.eq(RepositoryAdapterFineGrainedEventsTest.REPOSITORY_VALUE_B), ArgumentMatchers.any(ListUpdateCallback.class));
        RepositoryAdapterFineGrainedEventsTest.applyPresenter2FromAToBChanges(((RepositoryAdapterFineGrainedEventsTest.PRESENTER_1_A_ITEM_COUNT) + (RepositoryAdapterFineGrainedEventsTest.STATIC_ITEM_COUNT)), VerifyingWrappers.verifyingWrapper(fineGrainedEvents));
        Mockito.verifyNoMoreInteractions(fineGrainedEvents);
        Mockito.verify(onChangeEvent, Mockito.never()).run();
    }

    @Test
    public void shouldAskBothPresentersOnAdditionalObservableUpdate() {
        repository1.accept(RepositoryAdapterFineGrainedEventsTest.REPOSITORY_VALUE_B);
        repository2.accept(RepositoryAdapterFineGrainedEventsTest.REPOSITORY_VALUE_B);
        runUiThreadTasksIncludingDelayedTasks();
        repositoryAdapter.getItemCount();// usage

        Mockito.verify(mockPresenter1).getItemCount(RepositoryAdapterFineGrainedEventsTest.REPOSITORY_VALUE_B);// consume method call

        Mockito.verify(mockPresenter2).getItemCount(RepositoryAdapterFineGrainedEventsTest.REPOSITORY_VALUE_B);// consume method call

        updateDispatcher.update();
        runUiThreadTasksIncludingDelayedTasks();
        repositoryAdapter.getItemCount();
        Mockito.verify(mockPresenter1).getUpdates(ArgumentMatchers.eq(RepositoryAdapterFineGrainedEventsTest.REPOSITORY_VALUE_B), ArgumentMatchers.eq(RepositoryAdapterFineGrainedEventsTest.REPOSITORY_VALUE_B), ArgumentMatchers.any(ListUpdateCallback.class));
        Mockito.verifyNoMoreInteractions(mockPresenter1);// should not have called getItemCount() again

        Mockito.verify(mockPresenter2).getUpdates(ArgumentMatchers.eq(RepositoryAdapterFineGrainedEventsTest.REPOSITORY_VALUE_B), ArgumentMatchers.eq(RepositoryAdapterFineGrainedEventsTest.REPOSITORY_VALUE_B), ArgumentMatchers.any(ListUpdateCallback.class));
        Mockito.verifyNoMoreInteractions(mockPresenter2);// should not have called getItemCount() again

        RepositoryAdapterFineGrainedEventsTest.applyPresenter1RefreshBChanges(VerifyingWrappers.verifyingWrapper(fineGrainedEvents));
        RepositoryAdapterFineGrainedEventsTest.applyPresenter2RefreshBChanges(((RepositoryAdapterFineGrainedEventsTest.PRESENTER_1_B_ITEM_COUNT) + (RepositoryAdapterFineGrainedEventsTest.STATIC_ITEM_COUNT)), VerifyingWrappers.verifyingWrapper(fineGrainedEvents));
        Mockito.verifyNoMoreInteractions(fineGrainedEvents);
        Mockito.verify(onChangeEvent, Mockito.never()).run();
    }

    @Test
    public void shouldSendDataChangeEventAndDisableUpdatesUntilGetItemCount() {
        Mockito.when(mockPresenter1.getUpdates(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(ListUpdateCallback.class))).thenReturn(false);
        repositoryAdapter.getItemCount();// usage

        repository1.accept(RepositoryAdapterFineGrainedEventsTest.REPOSITORY_VALUE_B);
        runUiThreadTasksIncludingDelayedTasks();
        Mockito.verify(mockPresenter1).getUpdates(ArgumentMatchers.eq(RepositoryAdapterFineGrainedEventsTest.REPOSITORY_VALUE_A), ArgumentMatchers.eq(RepositoryAdapterFineGrainedEventsTest.REPOSITORY_VALUE_B), ArgumentMatchers.any(ListUpdateCallback.class));
        Mockito.verifyZeroInteractions(fineGrainedEvents);
        Mockito.verify(onChangeEvent).run();
        updateDispatcher.update();
        runUiThreadTasksIncludingDelayedTasks();
        Mockito.verifyZeroInteractions(fineGrainedEvents);
        Mockito.verifyNoMoreInteractions(onChangeEvent);
        repository2.accept(RepositoryAdapterFineGrainedEventsTest.REPOSITORY_VALUE_B);
        runUiThreadTasksIncludingDelayedTasks();
        Mockito.verifyZeroInteractions(fineGrainedEvents);
        Mockito.verifyNoMoreInteractions(onChangeEvent);
        repositoryAdapter.getItemCount();// usage

        repository2.accept(RepositoryAdapterFineGrainedEventsTest.REPOSITORY_VALUE_A);
        runUiThreadTasksIncludingDelayedTasks();
        Mockito.verifyNoMoreInteractions(onChangeEvent);
        RepositoryAdapterFineGrainedEventsTest.applyPresenter2FromBToAChanges(((RepositoryAdapterFineGrainedEventsTest.PRESENTER_1_B_ITEM_COUNT) + (RepositoryAdapterFineGrainedEventsTest.STATIC_ITEM_COUNT)), VerifyingWrappers.verifyingWrapper(fineGrainedEvents));
        Mockito.verifyNoMoreInteractions(fineGrainedEvents);
    }
}

