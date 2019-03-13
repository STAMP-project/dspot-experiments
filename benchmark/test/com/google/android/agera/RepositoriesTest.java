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
import com.google.android.agera.test.matchers.HasPrivateConstructor;
import com.google.android.agera.test.matchers.SupplierGives;
import com.google.android.agera.test.matchers.UpdatableUpdated;
import com.google.android.agera.test.mocks.MockUpdatable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@Config(manifest = NONE)
@RunWith(RobolectricTestRunner.class)
public final class RepositoriesTest {
    private static final int INITIAL_INT_VALUE = 0;

    private static final int INT_VALUE = 2;

    private static final String INITIAL_STRING_VALUE = "init";

    private static final String STRING_VALUE = "string";

    private static final Result<String> RESULT_STRING_VALUE = Result.success(RepositoriesTest.STRING_VALUE);

    private static final List<Integer> INITIAL_VALUE = Collections.singletonList(1);

    private static final List<Integer> LIST = Arrays.asList(1, 2, 3);

    private static final List<Integer> OTHER_LIST = Arrays.asList(4, 5);

    private static final List<Integer> LIST_AND_OTHER_LIST = Arrays.asList(1, 2, 3, 4, 5);

    private static final List<Integer> LIST_PLUS_TWO = Arrays.asList(3, 4, 5);

    private MutableRepository<List<Integer>> listSource;

    private MutableRepository<List<Integer>> otherListSource;

    private MockUpdatable updatable;

    private UpdateDispatcher updateDispatcher;

    @Mock
    private Receiver<Integer> mockIntegerReceiver;

    @Mock
    private Receiver<List<Integer>> mockIntegerListReceiver;

    @Mock
    private Binder<List<Integer>, String> mockIntegerListStringBinder;

    @Mock
    private Supplier<List<Integer>> mockIntegerListSupplier;

    @Mock
    private Supplier<String> mockStringSupplier;

    @Mock
    private Supplier<Result<String>> mockResultStringSupplier;

    @Mock
    private Supplier<Result<String>> mockFailedResultStringSupplier;

    @Mock
    private Function<String, Result<String>> mockResultStringFunction;

    @Mock
    private Function<String, Result<String>> mockFailedResultStringFunction;

    @Mock
    private Predicate<List<Integer>> mockIntegerListPredicate;

    @Mock
    private Function<List<Integer>, Integer> mockIntegerListToIntValueFunction;

    @Mock
    private Merger<String, String, Result<String>> mockResultStringMerger;

    @Mock
    private Merger<String, String, Result<String>> mockFailedResultStringMerger;

    @Test
    public void shouldCreateStaticRepository() {
        MatcherAssert.assertThat(Repositories.repository(1), SupplierGives.has(1));
    }

    @Test
    public void shouldNotGetUpdatesFromStaticRepository() {
        final Repository<Integer> repository = Repositories.repository(RepositoriesTest.INITIAL_INT_VALUE);
        updatable.addToObservable(repository);
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasNotUpdated());
    }

    @Test
    public void shouldBePossibleToObserveMutableRepository() {
        final Repository<Integer> repository = Repositories.mutableRepository(RepositoriesTest.INITIAL_INT_VALUE);
        updatable.addToObservable(repository);
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasNotUpdated());
    }

    @Test
    public void shouldGetUpdateFromChangedMutableRepository() {
        final MutableRepository<Integer> repository = Repositories.mutableRepository(RepositoriesTest.INITIAL_INT_VALUE);
        updatable.addToObservable(repository);
        repository.accept(RepositoriesTest.INT_VALUE);
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasUpdated());
    }

    @Test
    public void shouldNotGetUpdateFromMutableRepositoryChangedToSameValue() {
        final MutableRepository<Integer> repository = Repositories.mutableRepository(RepositoriesTest.INITIAL_INT_VALUE);
        updatable.addToObservable(repository);
        repository.accept(RepositoriesTest.INITIAL_INT_VALUE);
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasNotUpdated());
    }

    @Test
    public void shouldGetUpdateFromRepositoryChangedToNewValue() {
        final Repository<List<Integer>> repository = Repositories.repositoryWithInitialValue(RepositoriesTest.INITIAL_VALUE).observe().onUpdatesPerLoop().thenGetFrom(listSource).compile();
        updatable.addToObservable(repository);
        listSource.accept(RepositoriesTest.OTHER_LIST);
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasUpdated());
    }

    @Test
    public void shouldNotGetUpdateFromRepositoryChangedToSameValue() {
        final Repository<List<Integer>> repository = Repositories.repositoryWithInitialValue(RepositoriesTest.LIST).observe().onUpdatesPerLoop().thenGetFrom(listSource).compile();
        updatable.addToObservable(repository);
        listSource.accept(RepositoriesTest.LIST);
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasNotUpdated());
    }

    @Test
    public void shouldGetUpdateFromAlwaysNotifyRepositoryChangedToSameValue() {
        final Repository<List<Integer>> repository = Repositories.repositoryWithInitialValue(RepositoriesTest.LIST).observe().onUpdatesPerLoop().thenGetFrom(listSource).notifyIf(Mergers.staticMerger(true)).compile();
        updatable.addToObservable(repository);
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasUpdated());
    }

    @Test
    public void shouldContainCorrectValueForLazyRepository() {
        final Repository<List<Integer>> repository = Repositories.repositoryWithInitialValue(RepositoriesTest.INITIAL_VALUE).observe().onUpdatesPerLoop().goLazy().thenGetFrom(listSource).compile();
        updatable.addToObservable(repository);
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoriesTest.LIST));
    }

    @Test
    public void shouldContainCorrectValueForTransformInExecutor() {
        final Repository<List<Integer>> repository = Repositories.repositoryWithInitialValue(RepositoriesTest.INITIAL_VALUE).observe().onUpdatesPerLoop().goTo(new RepositoriesTest.SyncExecutor()).getFrom(listSource).thenTransform(new RepositoriesTest.AddTwoForEachFunction()).compile();
        updatable.addToObservable(repository);
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoriesTest.LIST_PLUS_TWO));
    }

    @Test
    public void shouldContainCorrectValueForTransformInExecutorMidFlow() {
        final Repository<List<Integer>> repository = Repositories.repositoryWithInitialValue(RepositoriesTest.INITIAL_VALUE).observe().onUpdatesPerLoop().getFrom(listSource).goTo(new RepositoriesTest.SyncExecutor()).thenTransform(new RepositoriesTest.AddTwoForEachFunction()).compile();
        updatable.addToObservable(repository);
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoriesTest.LIST_PLUS_TWO));
    }

    @Test
    public void shouldContainCorrectValueForLazyTransformMidFlow() {
        final Repository<List<Integer>> repository = Repositories.repositoryWithInitialValue(RepositoriesTest.INITIAL_VALUE).observe().onUpdatesPerLoop().getFrom(listSource).goLazy().thenTransform(new RepositoriesTest.AddTwoForEachFunction()).compile();
        updatable.addToObservable(repository);
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoriesTest.LIST_PLUS_TWO));
    }

    @Test
    public void shouldBeAbleToObserveRepository() {
        final Repository<List<Integer>> repository = Repositories.repositoryWithInitialValue(RepositoriesTest.INITIAL_VALUE).observe().onUpdatesPerLoop().thenGetFrom(listSource).compile();
        updatable.addToObservable(repository);
    }

    @Test
    public void shouldMergeInSecondSource() {
        final Repository<List<Integer>> repository = Repositories.repositoryWithInitialValue(RepositoriesTest.INITIAL_VALUE).observe().onUpdatesPerLoop().getFrom(listSource).thenMergeIn(otherListSource, new RepositoriesTest.ListMerger<Integer, Integer, Integer>()).compile();
        updatable.addToObservable(repository);
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoriesTest.LIST_AND_OTHER_LIST));
    }

    @Test
    public void shouldUpdateOnExplicitObservable() {
        final Repository<List<Integer>> repository = Repositories.repositoryWithInitialValue(RepositoriesTest.INITIAL_VALUE).observe(updateDispatcher).onUpdatesPerLoop().thenGetFrom(mockIntegerListSupplier).compile();
        updatable.addToObservable(repository);
        updateDispatcher.update();
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoriesTest.LIST));
        Mockito.verify(mockIntegerListSupplier, Mockito.times(2)).get();
    }

    @Test
    public void shouldReturnDataWithGoLazyOnDemand() {
        final Repository<List<Integer>> repository = Repositories.repositoryWithInitialValue(RepositoriesTest.INITIAL_VALUE).observe().onUpdatesPerLoop().goLazy().thenGetFrom(mockIntegerListSupplier).compile();
        updatable.addToObservable(repository);
        Mockito.verifyZeroInteractions(mockIntegerListSupplier);
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoriesTest.LIST));
        Mockito.verify(mockIntegerListSupplier).get();
    }

    @Test
    public void shouldCompileIntoNextRepository() throws Exception {
        final Repository<Integer> repository = Repositories.repositoryWithInitialValue(RepositoriesTest.INITIAL_VALUE).observe().onUpdatesPerLoop().thenGetFrom(listSource).compileIntoRepositoryWithInitialValue(RepositoriesTest.INITIAL_INT_VALUE).onUpdatesPerLoop().thenTransform(mockIntegerListToIntValueFunction).compile();
        updatable.addToObservable(repository);
        Mockito.verify(mockIntegerListToIntValueFunction, Mockito.atLeastOnce()).apply(RepositoriesTest.LIST);
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoriesTest.INT_VALUE));
    }

    @Test
    public void shouldSendTo() throws Exception {
        final Repository<List<Integer>> repository = Repositories.repositoryWithInitialValue(RepositoriesTest.INITIAL_VALUE).observe().onUpdatesPerLoop().sendTo(mockIntegerListReceiver).thenSkip().compile();
        updatable.addToObservable(repository);
        Mockito.verify(mockIntegerListReceiver).accept(RepositoriesTest.INITIAL_VALUE);
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasNotUpdated());
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoriesTest.INITIAL_VALUE));
    }

    @Test
    public void shouldBindWith() throws Exception {
        final Repository<List<Integer>> repository = Repositories.repositoryWithInitialValue(RepositoriesTest.INITIAL_VALUE).observe().onUpdatesPerLoop().bindWith(mockStringSupplier, mockIntegerListStringBinder).thenSkip().compile();
        updatable.addToObservable(repository);
        Mockito.verify(mockIntegerListStringBinder).bind(RepositoriesTest.INITIAL_VALUE, RepositoriesTest.STRING_VALUE);
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasNotUpdated());
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoriesTest.INITIAL_VALUE));
    }

    @Test
    public void shouldGetSuccessfulValueFromThenAttemptGetFrom() {
        final Repository<String> repository = Repositories.repositoryWithInitialValue(RepositoriesTest.INITIAL_STRING_VALUE).observe().onUpdatesPerLoop().thenAttemptGetFrom(mockResultStringSupplier).orSkip().compile();
        updatable.addToObservable(repository);
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasUpdated());
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoriesTest.STRING_VALUE));
    }

    @Test
    public void shouldNotUpdateForSkippedFailedValueFromThenAttemptGetFrom() {
        final Repository<String> repository = Repositories.repositoryWithInitialValue(RepositoriesTest.INITIAL_STRING_VALUE).observe().onUpdatesPerLoop().thenAttemptGetFrom(mockFailedResultStringSupplier).orSkip().compile();
        updatable.addToObservable(repository);
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasNotUpdated());
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoriesTest.INITIAL_STRING_VALUE));
    }

    @Test
    public void shouldGetSuccessfulValueFromThenAttemptTransform() {
        final Repository<String> repository = Repositories.repositoryWithInitialValue(RepositoriesTest.INITIAL_STRING_VALUE).observe().onUpdatesPerLoop().thenAttemptTransform(mockResultStringFunction).orSkip().compile();
        updatable.addToObservable(repository);
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasUpdated());
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoriesTest.STRING_VALUE));
    }

    @Test
    public void shouldNotUpdateForSkippedFailedValueFromThenAttemptTransform() {
        final Repository<String> repository = Repositories.repositoryWithInitialValue(RepositoriesTest.INITIAL_STRING_VALUE).observe().onUpdatesPerLoop().thenAttemptTransform(mockFailedResultStringFunction).orSkip().compile();
        updatable.addToObservable(repository);
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasNotUpdated());
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoriesTest.INITIAL_STRING_VALUE));
    }

    @Test
    public void shouldGetSuccessfulValueFromThenAttemptMergeIn() {
        final Repository<String> repository = Repositories.repositoryWithInitialValue(RepositoriesTest.INITIAL_STRING_VALUE).observe().onUpdatesPerLoop().thenAttemptMergeIn(mockStringSupplier, mockResultStringMerger).orSkip().compile();
        updatable.addToObservable(repository);
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasUpdated());
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoriesTest.STRING_VALUE));
    }

    @Test
    public void shouldNotUpdateForSkippedFailedValueFromThenAttemptMergeIn() {
        final Repository<String> repository = Repositories.repositoryWithInitialValue(RepositoriesTest.INITIAL_STRING_VALUE).observe().onUpdatesPerLoop().thenAttemptMergeIn(mockStringSupplier, mockFailedResultStringMerger).orSkip().compile();
        updatable.addToObservable(repository);
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasNotUpdated());
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoriesTest.INITIAL_STRING_VALUE));
    }

    @Test
    public void shouldHavePrivateConstructor() {
        MatcherAssert.assertThat(Repositories.class, HasPrivateConstructor.hasPrivateConstructor());
    }

    private static final class ListMerger<A extends C, B extends C, C> implements Merger<List<A>, List<B>, List<C>> {
        @NonNull
        @Override
        public List<C> merge(@NonNull
        final List<A> firstList, @NonNull
        final List<B> secondList) {
            final List<C> result = new ArrayList<>(((firstList.size()) + (secondList.size())));
            result.addAll(firstList);
            result.addAll(secondList);
            return result;
        }
    }

    private static final class AddTwoForEachFunction implements Function<List<Integer>, List<Integer>> {
        @NonNull
        @Override
        public List<Integer> apply(@NonNull
        final List<Integer> integers) {
            final List<Integer> result = new ArrayList<>();
            for (final Integer integer : integers) {
                result.add((integer + 2));
            }
            return result;
        }
    }

    private static class SyncExecutor implements Executor {
        @Override
        public void execute(@NonNull
        final Runnable command) {
            command.run();
        }
    }
}

