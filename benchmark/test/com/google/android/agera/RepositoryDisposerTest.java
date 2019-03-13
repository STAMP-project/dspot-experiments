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


import com.google.android.agera.test.SingleSlotDelayedExecutor;
import com.google.android.agera.test.mocks.MockUpdatable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@Config(manifest = NONE)
@RunWith(RobolectricTestRunner.class)
public final class RepositoryDisposerTest {
    private static final Object INITIAL_VALUE = new Object();

    private static final Object FIRST_VALUE = new Object();

    private static final Object SECOND_VALUE = new Object();

    private static final Object BREAK_VALUE = new Object();

    private static final Object FINAL_VALUE = new Object();

    private MockUpdatable updatable;

    private SingleSlotDelayedExecutor executor;

    @Mock
    private Predicate<Object> mockPredicate;

    @Mock
    private Receiver<Object> mockDisposer;

    @Test
    public void shouldNotDiscardInitialValue() {
        Repository<Object> repository = Repositories.repositoryWithInitialValue(RepositoryDisposerTest.INITIAL_VALUE).observe().onUpdatesPerLoop().check(Predicates.falsePredicate()).orSkip().thenGetFrom(Suppliers.staticSupplier(RepositoryDisposerTest.FINAL_VALUE)).sendDiscardedValuesTo(mockDisposer).compile();
        updatable.addToObservable(repository);
        Mockito.verify(mockDisposer, Mockito.never()).accept(ArgumentMatchers.any());
    }

    @Test
    public void shouldNotDiscardPublishedValue() {
        Repository<Object> repository = Repositories.repositoryWithInitialValue(RepositoryDisposerTest.INITIAL_VALUE).observe().onUpdatesPerLoop().check(mockPredicate).orSkip().thenTransform(Functions.staticFunction(RepositoryDisposerTest.FINAL_VALUE)).sendDiscardedValuesTo(mockDisposer).compile();
        Mockito.when(mockPredicate.apply(RepositoryDisposerTest.INITIAL_VALUE)).thenReturn(true);
        Mockito.when(mockPredicate.apply(RepositoryDisposerTest.FINAL_VALUE)).thenReturn(false);
        updatable.addToObservable(repository);
        updatable.removeFromObservables();
        updatable.addToObservable(repository);
        Mockito.verify(mockPredicate).apply(RepositoryDisposerTest.INITIAL_VALUE);
        Mockito.verify(mockPredicate).apply(RepositoryDisposerTest.FINAL_VALUE);
        Mockito.verify(mockDisposer, Mockito.never()).accept(ArgumentMatchers.any());
    }

    @Test
    public void shouldDiscardFirstValueDueToSkipClause() {
        Repository<Object> repository = Repositories.repositoryWithInitialValue(RepositoryDisposerTest.INITIAL_VALUE).observe().onUpdatesPerLoop().getFrom(Suppliers.staticSupplier(RepositoryDisposerTest.FIRST_VALUE)).check(Predicates.falsePredicate()).orSkip().thenGetFrom(Suppliers.staticSupplier(RepositoryDisposerTest.FINAL_VALUE)).sendDiscardedValuesTo(mockDisposer).compile();
        updatable.addToObservable(repository);
        Mockito.verify(mockDisposer).accept(RepositoryDisposerTest.FIRST_VALUE);
    }

    @Test
    public void shouldDiscardFirstValueDueToEndClause() {
        Repository<Object> repository = Repositories.repositoryWithInitialValue(RepositoryDisposerTest.INITIAL_VALUE).observe().onUpdatesPerLoop().getFrom(Suppliers.staticSupplier(RepositoryDisposerTest.FIRST_VALUE)).check(Predicates.falsePredicate()).orEnd(Functions.staticFunction(RepositoryDisposerTest.BREAK_VALUE)).thenGetFrom(Suppliers.staticSupplier(RepositoryDisposerTest.FINAL_VALUE)).sendDiscardedValuesTo(mockDisposer).compile();
        updatable.addToObservable(repository);
        Mockito.verify(mockDisposer).accept(RepositoryDisposerTest.FIRST_VALUE);
    }

    @Test
    public void shouldDiscardSecondValueDueToSkipDirective() {
        Repository<Object> repository = Repositories.repositoryWithInitialValue(RepositoryDisposerTest.INITIAL_VALUE).observe().onUpdatesPerLoop().getFrom(Suppliers.staticSupplier(RepositoryDisposerTest.FIRST_VALUE)).check(Predicates.truePredicate()).orEnd(Functions.staticFunction(RepositoryDisposerTest.BREAK_VALUE)).transform(Functions.staticFunction(RepositoryDisposerTest.SECOND_VALUE)).thenSkip().sendDiscardedValuesTo(mockDisposer).compile();
        updatable.addToObservable(repository);
        Mockito.verify(mockDisposer).accept(RepositoryDisposerTest.SECOND_VALUE);
    }

    @Test
    public void shouldDiscardFirstValueDueToDeactivation() {
        Repository<Object> repository = Repositories.repositoryWithInitialValue(RepositoryDisposerTest.INITIAL_VALUE).observe().onUpdatesPerLoop().getFrom(Suppliers.staticSupplier(RepositoryDisposerTest.FIRST_VALUE)).goTo(executor).thenTransform(Functions.staticFunction(RepositoryDisposerTest.FINAL_VALUE)).onDeactivation(RepositoryConfig.CANCEL_FLOW).sendDiscardedValuesTo(mockDisposer).compile();
        updatable.addToObservable(repository);
        updatable.removeFromObservables();
        executor.resumeOrThrow();
        Mockito.verify(mockDisposer).accept(RepositoryDisposerTest.FIRST_VALUE);
    }
}

