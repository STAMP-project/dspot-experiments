/**
 * Copyright 2015 Google Inc. All Rights Reserved.
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
package com.google.android.agera;


import com.google.android.agera.test.matchers.HasPrivateConstructor;
import com.google.android.agera.test.matchers.UpdatableUpdated;
import com.google.android.agera.test.mocks.MockUpdatable;
import java.util.ArrayList;
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
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.util.Scheduler;


@Config(manifest = NONE)
@RunWith(RobolectricTestRunner.class)
public final class ObservablesTest {
    private static final int FILTER_TIME = 10000;

    private Observable compositeObservableOfMany;

    private Observable chainedCompositeObservableOfOne;

    private Observable chainedCompositeObservable;

    private Observable chainedDupeCompositeObservable;

    private UpdateDispatcher firstUpdateDispatcher;

    private UpdateDispatcher secondUpdateDispatcher;

    private UpdateDispatcher thirdUpdateDispatcher;

    private Observable trueConditionalObservable;

    private Observable falseConditionalObservable;

    private MockUpdatable updatable;

    private MockUpdatable secondUpdatable;

    private Scheduler scheduler;

    private UpdateDispatcher updateDispatcher;

    @Mock
    private ActivationHandler mockActivationHandler;

    private UpdateDispatcher updateDispatcherWithUpdatablesChanged;

    private ShadowLooper looper;

    @Test
    public void shouldUpdateFromFirstObservablesInCompositeOfMany() {
        updatable.addToObservable(compositeObservableOfMany);
        firstUpdateDispatcher.update();
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasUpdated());
    }

    @Test
    public void shouldUpdateFromSecondObservablesInCompositeOfMany() {
        updatable.addToObservable(compositeObservableOfMany);
        secondUpdateDispatcher.update();
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasUpdated());
    }

    @Test
    public void shouldUpdateFromFirstObservablesInChainedCompositeOfOne() {
        updatable.addToObservable(chainedCompositeObservableOfOne);
        firstUpdateDispatcher.update();
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasUpdated());
    }

    @Test
    public void shouldUpdateFromFirstChainInChainedComposite() {
        updatable.addToObservable(chainedCompositeObservable);
        secondUpdateDispatcher.update();
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasUpdated());
    }

    @Test
    public void shouldUpdateFromSecondChainInChainedComposite() {
        updatable.addToObservable(chainedCompositeObservable);
        thirdUpdateDispatcher.update();
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasUpdated());
    }

    @Test
    public void shouldUpdateOnlyOnceFromDupeInChainedComposite() {
        final Updatable updatable = Mockito.mock(Updatable.class);
        chainedDupeCompositeObservable.addUpdatable(updatable);
        runUiThreadTasksIncludingDelayedTasks();
        thirdUpdateDispatcher.update();
        runUiThreadTasksIncludingDelayedTasks();
        Mockito.verify(updatable).update();
    }

    @Test
    public void shouldNotUpdateConditionalObservableForFalseCondition() {
        updatable.addToObservable(trueConditionalObservable);
        firstUpdateDispatcher.update();
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasUpdated());
    }

    @Test
    public void shouldUpdateConditionalObservableForTrueCondition() {
        updatable.addToObservable(falseConditionalObservable);
        firstUpdateDispatcher.update();
        MatcherAssert.assertThat(updatable, Matchers.not(UpdatableUpdated.wasUpdated()));
    }

    @Test
    public void shouldBeAbleToCreateEmptyObservable() {
        MatcherAssert.assertThat(Observables.compositeObservable(), Matchers.notNullValue());
    }

    @Test
    public void shouldCallFirstAddedForUpdateDispatcher() {
        updatable.addToObservable(updateDispatcherWithUpdatablesChanged);
        Mockito.verify(mockActivationHandler).observableActivated(updateDispatcherWithUpdatablesChanged);
    }

    @Test
    public void shouldCallFirstAddedOnceOnlyForUpdateDispatcher() {
        updatable.addToObservable(updateDispatcherWithUpdatablesChanged);
        MockUpdatable.mockUpdatable().addToObservable(updateDispatcherWithUpdatablesChanged);
        Mockito.verify(mockActivationHandler).observableActivated(updateDispatcherWithUpdatablesChanged);
    }

    @Test
    public void shouldCallLastRemovedForUpdateDispatcher() {
        updatable.addToObservable(updateDispatcherWithUpdatablesChanged);
        Mockito.verify(mockActivationHandler).observableActivated(updateDispatcherWithUpdatablesChanged);
    }

    @Test
    public void shouldCallLastRemovedOnceOnlyForUpdateDispatcher() {
        updatable.addToObservable(updateDispatcherWithUpdatablesChanged);
        secondUpdatable.addToObservable(updateDispatcherWithUpdatablesChanged);
        updatable.removeFromObservables();
        looper.runToEndOfTasks();
        secondUpdatable.removeFromObservables();
        looper.runToEndOfTasks();
        Mockito.verify(mockActivationHandler).observableActivated(updateDispatcherWithUpdatablesChanged);
    }

    @Test
    public void shouldNotRefreshObservableIfReactivatedWithinSameCycle() {
        final Supplier supplier = Mockito.mock(Supplier.class);
        Mockito.when(supplier.get()).thenReturn(new Object());
        final Repository repository = Repositories.repositoryWithInitialValue(new Object()).observe().onUpdatesPerLoop().thenGetFrom(supplier).compile();
        updatable.addToObservable(repository);
        looper.pause();
        updatable.removeFromObservables();
        updatable.addToObservable(repository);
        looper.unPause();
        Mockito.verify(supplier).get();
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionForAddNullUpdatable() {
        updateDispatcher.addUpdatable(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionForRemoveNullUpdatable() {
        updateDispatcher.removeUpdatable(null);
    }

    @Test
    public void shouldOnlyUpdateOncePerLooper() {
        final Supplier supplier = Mockito.mock(Supplier.class);
        Mockito.when(supplier.get()).thenReturn(new Object());
        final Repository repository = Repositories.repositoryWithInitialValue(new Object()).observe(updateDispatcher).onUpdatesPerLoop().thenGetFrom(supplier).compile();
        updatable.addToObservable(repository);
        looper.pause();
        updateDispatcher.update();
        updateDispatcher.update();
        updateDispatcher.update();
        looper.unPause();
        Mockito.verify(supplier, Mockito.times(2)).get();
    }

    @Test
    public void shouldUpdateAllUpdatablesWhenUpdateFromSameThreadForUpdateDispatcher() {
        updatable.addToObservable(updateDispatcher);
        secondUpdatable.addToObservable(updateDispatcher);
        updateDispatcher.update();
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasUpdated());
        MatcherAssert.assertThat(secondUpdatable, UpdatableUpdated.wasUpdated());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowIllegalArgumentExceptionIfAddingTheSameUpdatableTwice() {
        updatable.addToObservable(updateDispatcher);
        updatable.addToObservable(updateDispatcher);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowIllegalArgumentExceptionIfRemovingANotAddedUpdatable() {
        updateDispatcher.removeUpdatable(updatable);
    }

    @Test
    public void shouldUpdatePerCycleObservable() {
        updatable.addToObservable(Observables.perLoopObservable(updateDispatcher));
        updateDispatcher.update();
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasUpdated());
    }

    @Test
    public void shouldUpdatePerMillisecondObservable() {
        final long expectedDelayedTime = (scheduler.getCurrentTime()) + (ObservablesTest.FILTER_TIME);
        updatable.addToObservable(Observables.perMillisecondObservable(ObservablesTest.FILTER_TIME, updateDispatcher));
        updateDispatcher.update();
        idleMainLooper(ObservablesTest.FILTER_TIME);
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasUpdated());
        MatcherAssert.assertThat(scheduler.getCurrentTime(), Matchers.greaterThanOrEqualTo(expectedDelayedTime));
    }

    @Test
    public void shouldUpdateCompositeOfPerMillisecondObservable() {
        final long expectedDelayedTime = (scheduler.getCurrentTime()) + (ObservablesTest.FILTER_TIME);
        updatable.addToObservable(Observables.compositeObservable(Observables.perMillisecondObservable(ObservablesTest.FILTER_TIME, updateDispatcher), Observables.updateDispatcher()));
        updateDispatcher.update();
        idleMainLooper(ObservablesTest.FILTER_TIME);
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasUpdated());
        MatcherAssert.assertThat(scheduler.getCurrentTime(), Matchers.greaterThanOrEqualTo(expectedDelayedTime));
    }

    @Test
    public void shouldUpdateCompositeOfSinglePerMillisecondObservable() {
        final long expectedDelayedTime = (scheduler.getCurrentTime()) + (ObservablesTest.FILTER_TIME);
        updatable.addToObservable(Observables.compositeObservable(Observables.perMillisecondObservable(ObservablesTest.FILTER_TIME, updateDispatcher)));
        updateDispatcher.update();
        idleMainLooper(ObservablesTest.FILTER_TIME);
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasUpdated());
        MatcherAssert.assertThat(scheduler.getCurrentTime(), Matchers.greaterThanOrEqualTo(expectedDelayedTime));
    }

    @Test
    public void shouldHandleManyObservables() {
        final int numberOfObservables = 10;
        for (int passes = 0; passes < 3; passes++) {
            List<MockUpdatable> mockUpdatables = new ArrayList<>(numberOfObservables);
            for (int i = 0; i < numberOfObservables; i++) {
                MockUpdatable mockUpdatable = MockUpdatable.mockUpdatable();
                mockUpdatables.add(mockUpdatable);
                mockUpdatable.addToObservable(updateDispatcher);
            }
            Collections.shuffle(mockUpdatables);
            for (MockUpdatable mockUpdatable : mockUpdatables) {
                mockUpdatable.removeFromObservables();
                looper.runToEndOfTasks();
            }
        }
    }

    @Test
    public void shouldIgnoreUnknownMessage() {
        WorkerHandler.workerHandler().obtainMessage((-1)).sendToTarget();
    }

    @Test
    public void shouldNotAllowAddingUpdatablesOnNonLooperThreadInBaseObservable() {
        final Observable observable = new BaseObservable() {};
        MatcherAssert.assertThat(throwsIllegalStateExceptionForCallOnNonLooperThread(new Runnable() {
            @Override
            public void run() {
                updatable.addToObservable(observable);
            }
        }), Matchers.is(true));
    }

    @Test
    public void shouldNotAllowCreatingBaseObservableOnNonLooperThread() {
        MatcherAssert.assertThat(throwsIllegalStateExceptionForCallOnNonLooperThread(new Runnable() {
            @Override
            public void run() {
                new BaseObservable() {};
            }
        }), Matchers.is(true));
    }

    @Test
    public void shouldNotAllowRemovingUpdatablesOnNonLooperThreadInBaseObservable() {
        final Observable observable = new BaseObservable() {};
        updatable.addToObservable(observable);
        MatcherAssert.assertThat(throwsIllegalStateExceptionForCallOnNonLooperThread(new Runnable() {
            @Override
            public void run() {
                updatable.removeFromObservables();
            }
        }), Matchers.is(true));
    }

    @Test
    public void shouldHandleLifeCycleInBaseObservable() {
        final Observable observable = new BaseObservable() {};
        updatable.addToObservable(observable);
        updatable.removeFromObservables();
    }

    @Test
    public void shouldHavePrivateConstructor() {
        MatcherAssert.assertThat(Observables.class, HasPrivateConstructor.hasPrivateConstructor());
    }
}

