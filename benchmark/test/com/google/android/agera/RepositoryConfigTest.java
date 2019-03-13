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


import android.support.annotation.NonNull;
import com.google.android.agera.test.SingleSlotDelayedExecutor;
import com.google.android.agera.test.matchers.SupplierGives;
import com.google.android.agera.test.matchers.UpdatableUpdated;
import com.google.android.agera.test.mocks.MockUpdatable;
import java.util.concurrent.Executors;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;


@Config(manifest = NONE)
@RunWith(RobolectricTestRunner.class)
public final class RepositoryConfigTest {
    private static final Object INITIAL_VALUE = "INITIAL_VALUE";

    private static final Object UPDATED_VALUE = "UPDATED_VALUE";

    private static final Object ANOTHER_VALUE = "ANOTHER_VALUE";

    private static final Object RESUMED_VALUE = "RESUMED_VALUE";

    private static final Object UNEXPECTED_VALUE = "UNEXPECTED_VALUE";

    private MockUpdatable updatable;

    private UpdateDispatcher updateDispatcher;

    private SingleSlotDelayedExecutor delayedExecutor;

    private RepositoryConfigTest.InterruptibleMonitoredSupplier monitoredSupplier;

    @Mock
    private Supplier<Object> mockSupplier;

    private ShadowLooper looper;

    @Test
    public void shouldNotNotifyWhenValueUnchanged() throws Exception {
        final Repository<Object> repository = Repositories.repositoryWithInitialValue(RepositoryConfigTest.INITIAL_VALUE).observe(updateDispatcher).onUpdatesPerLoop().thenGetFrom(mockSupplier).compile();
        updatable.addToObservable(repository);
        retriggerUpdate();
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasNotUpdated());
        Mockito.verify(mockSupplier, Mockito.times(2)).get();
    }

    @Test
    public void shouldNotifyWhenValueUnchangedButAlwaysNotify() throws Exception {
        final Repository<Object> repository = Repositories.repositoryWithInitialValue(RepositoryConfigTest.INITIAL_VALUE).observe(updateDispatcher).onUpdatesPerLoop().thenGetFrom(mockSupplier).notifyIf(Mergers.staticMerger(true)).compile();
        updatable.addToObservable(repository);
        retriggerUpdate();
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasUpdated());
    }

    @Test
    public void shouldContinueFlowOnDeactivate() throws Exception {
        final Repository<Object> repository = Repositories.repositoryWithInitialValue(RepositoryConfigTest.INITIAL_VALUE).observe(updateDispatcher).onUpdatesPerLoop().goTo(delayedExecutor).thenGetFrom(mockSupplier).compile();
        updatable.addToObservable(repository);
        MatcherAssert.assertThat(delayedExecutor.hasRunnable(), Matchers.is(true));
        updatable.removeFromObservables();
        looper.runToEndOfTasks();
        delayedExecutor.resumeOrThrow();
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoryConfigTest.UPDATED_VALUE));
    }

    @Test
    public void shouldContinueFlowOnConcurrentUpdate() throws Exception {
        Mockito.when(mockSupplier.get()).thenReturn(RepositoryConfigTest.UPDATED_VALUE, RepositoryConfigTest.ANOTHER_VALUE);
        final Repository<Object> repository = Repositories.repositoryWithInitialValue(RepositoryConfigTest.INITIAL_VALUE).observe(updateDispatcher).onUpdatesPerLoop().goTo(delayedExecutor).thenGetFrom(mockSupplier).compile();
        updatable.addToObservable(repository);
        MatcherAssert.assertThat(delayedExecutor.hasRunnable(), Matchers.is(true));
        retriggerUpdate();
        delayedExecutor.resumeOrThrow();
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasUpdated());
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoryConfigTest.UPDATED_VALUE));
        updatable.resetUpdated();
        delayedExecutor.resumeOrThrow();// this asserts second run started for the triggered update

        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasUpdated());
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoryConfigTest.ANOTHER_VALUE));
    }

    @Test
    public void shouldCancelFlowOnDeactivate() throws Exception {
        final Repository<Object> repository = Repositories.repositoryWithInitialValue(RepositoryConfigTest.INITIAL_VALUE).observe(updateDispatcher).onUpdatesPerLoop().goTo(delayedExecutor).thenGetFrom(mockSupplier).onDeactivation(RepositoryConfig.CANCEL_FLOW).compile();
        updatable.addToObservable(repository);
        MatcherAssert.assertThat(delayedExecutor.hasRunnable(), Matchers.is(true));
        updatable.removeFromObservables();
        looper.runToEndOfTasks();
        delayedExecutor.resumeOrThrow();
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoryConfigTest.INITIAL_VALUE));
        Mockito.verifyZeroInteractions(mockSupplier);
    }

    @Test
    public void shouldCancelFlowOnConcurrentUpdate() throws Exception {
        final Repository<Object> repository = Repositories.repositoryWithInitialValue(RepositoryConfigTest.INITIAL_VALUE).observe(updateDispatcher).onUpdatesPerLoop().goTo(delayedExecutor).thenGetFrom(mockSupplier).onConcurrentUpdate(RepositoryConfig.CANCEL_FLOW).compile();
        updatable.addToObservable(repository);
        MatcherAssert.assertThat(delayedExecutor.hasRunnable(), Matchers.is(true));
        retriggerUpdate();
        delayedExecutor.resumeOrThrow();
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasNotUpdated());
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoryConfigTest.INITIAL_VALUE));
        delayedExecutor.resumeOrThrow();// this asserts second run started for the triggered update

        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasUpdated());
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoryConfigTest.UPDATED_VALUE));
    }

    @Test
    public void shouldCancelFlowMidFlow() throws Exception {
        final Function<Object, Object> cancellingFunction = new Function<Object, Object>() {
            @NonNull
            @Override
            public Object apply(@NonNull
            Object input) {
                // Sneak in a deactivation here to test cancellation mid-flow.
                updatable.removeFromObservables();
                looper.runToEndOfTasks();
                return input;
            }
        };
        final Repository<Object> repository = Repositories.repositoryWithInitialValue(RepositoryConfigTest.INITIAL_VALUE).observe(updateDispatcher).onUpdatesPerLoop().goTo(delayedExecutor).transform(cancellingFunction).thenGetFrom(mockSupplier).onDeactivation(RepositoryConfig.CANCEL_FLOW).compile();
        updatable.addToObservable(repository);
        delayedExecutor.resumeOrThrow();
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoryConfigTest.INITIAL_VALUE));
        Mockito.verifyZeroInteractions(mockSupplier);
    }

    @Test
    public void shouldResetToInitialValueOnDeactivate() throws Exception {
        final Repository<Object> repository = Repositories.repositoryWithInitialValue(RepositoryConfigTest.INITIAL_VALUE).observe(updateDispatcher).onUpdatesPerLoop().thenGetFrom(mockSupplier).onDeactivation(RepositoryConfig.RESET_TO_INITIAL_VALUE).compile();
        updatable.addToObservable(repository);
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoryConfigTest.UPDATED_VALUE));
        updatable.removeFromObservables();
        looper.runToEndOfTasks();
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoryConfigTest.INITIAL_VALUE));
    }

    @Test
    public void shouldInterruptOnDeactivate() throws Exception {
        final Repository<Object> repository = // need background thread to test interrupt
        Repositories.repositoryWithInitialValue(RepositoryConfigTest.INITIAL_VALUE).observe(updateDispatcher).onUpdatesPerLoop().goTo(Executors.newSingleThreadExecutor()).thenGetFrom(monitoredSupplier).onDeactivation(RepositoryConfig.SEND_INTERRUPT).compile();
        updatable.addToObservable(repository);
        monitoredSupplier.waitForGetToStart();
        updatable.removeFromObservables();
        looper.runToEndOfTasks();
        monitoredSupplier.waitForGetToEnd();
        MatcherAssert.assertThat(monitoredSupplier.wasInterrupted(), Matchers.is(true));
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoryConfigTest.INITIAL_VALUE));
    }

    private static final class InterruptibleMonitoredSupplier implements Supplier<Object> {
        private static final int ENDED = 0;

        private static final int STARTED = 1;

        private static final int RESUMED = 2;

        private int state = RepositoryConfigTest.InterruptibleMonitoredSupplier.ENDED;

        private boolean interrupted;

        @NonNull
        @Override
        public synchronized Object get() {
            interrupted = false;
            state = RepositoryConfigTest.InterruptibleMonitoredSupplier.STARTED;
            notifyAll();
            try {
                return waitForState(RepositoryConfigTest.InterruptibleMonitoredSupplier.RESUMED) ? RepositoryConfigTest.RESUMED_VALUE : RepositoryConfigTest.UNEXPECTED_VALUE;
            } catch (InterruptedException e) {
                interrupted = true;
                return RepositoryConfigTest.UNEXPECTED_VALUE;
            } finally {
                state = RepositoryConfigTest.InterruptibleMonitoredSupplier.ENDED;
                notifyAll();
            }
        }

        public synchronized RepositoryConfigTest.InterruptibleMonitoredSupplier waitForGetToStart() throws InterruptedException {
            MatcherAssert.assertThat("monitoredSupplier.get() should start", waitForState(RepositoryConfigTest.InterruptibleMonitoredSupplier.STARTED));
            return this;
        }

        public synchronized RepositoryConfigTest.InterruptibleMonitoredSupplier resumeIfWaiting() {
            state = RepositoryConfigTest.InterruptibleMonitoredSupplier.RESUMED;
            notifyAll();
            return this;
        }

        public synchronized void waitForGetToEnd() throws InterruptedException {
            MatcherAssert.assertThat("monitoredSupplier.get() should end", waitForState(RepositoryConfigTest.InterruptibleMonitoredSupplier.ENDED));
        }

        private boolean waitForState(int waitForState) throws InterruptedException {
            long now = System.currentTimeMillis();
            long giveUpTime = now + 20000;
            while (((state) != waitForState) && (now < giveUpTime)) {
                wait((giveUpTime - now));
                now = System.currentTimeMillis();
            } 
            return (state) == waitForState;
        }

        public synchronized boolean wasInterrupted() {
            return interrupted;
        }
    }
}

