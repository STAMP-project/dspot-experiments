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


import com.google.android.agera.test.matchers.SupplierGives;
import com.google.android.agera.test.matchers.UpdatableUpdated;
import com.google.android.agera.test.mocks.MockUpdatable;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@Config(manifest = NONE)
@RunWith(RobolectricTestRunner.class)
public final class RepositoryTerminationTest {
    private static final List<Integer> INITIAL_LIST = Arrays.asList(1, 2, 3);

    private static final List<Integer> LIST = Arrays.asList(4, 5);

    private static final List<Integer> OTHER_LIST = Arrays.asList(6, 7);

    private static final int INITIAL_VALUE = 8;

    private static final int VALUE = 42;

    private static final Result<Integer> SUCCESS_WITH_VALUE = Result.success(RepositoryTerminationTest.VALUE);

    private static final Result<Integer> FAILURE = Result.failure();

    private MutableRepository<List<Integer>> listSource;

    private MutableRepository<List<Integer>> otherListSource;

    private MockUpdatable updatable;

    @Mock
    private Predicate<List<Integer>> mockPredicate;

    @Mock
    private Receiver<Object> mockReceiver;

    @Mock
    private Function<List<Integer>, List<Integer>> mockFunction;

    @Mock
    private Function<List<Integer>, List<Integer>> mockOtherFunction;

    @Mock
    private Supplier<Result<Integer>> mockAttemptSupplier;

    @Test
    public void shouldEndAfterFailedCheck() {
        Mockito.when(mockPredicate.apply(ArgumentMatchers.anyListOf(Integer.class))).thenReturn(true);
        Mockito.when(mockPredicate.apply(RepositoryTerminationTest.INITIAL_LIST)).thenReturn(false);
        Mockito.when(mockFunction.apply(RepositoryTerminationTest.INITIAL_LIST)).thenReturn(RepositoryTerminationTest.LIST);
        final Repository<List<Integer>> repository = Repositories.repositoryWithInitialValue(RepositoryTerminationTest.INITIAL_LIST).observe().onUpdatesPerLoop().check(mockPredicate).orEnd(mockFunction).thenGetFrom(otherListSource).compile();
        updatable.addToObservable(repository);
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoryTerminationTest.LIST));
    }

    @Test
    public void shouldSkipAfterFailedCheck() {
        Mockito.when(mockPredicate.apply(ArgumentMatchers.anyListOf(Integer.class))).thenReturn(true);
        Mockito.when(mockPredicate.apply(RepositoryTerminationTest.INITIAL_LIST)).thenReturn(false);
        final Repository<List<Integer>> repository = Repositories.repositoryWithInitialValue(RepositoryTerminationTest.INITIAL_LIST).observe().onUpdatesPerLoop().check(mockPredicate).orSkip().thenGetFrom(otherListSource).compile();
        updatable.addToObservable(repository);
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoryTerminationTest.INITIAL_LIST));
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasNotUpdated());
    }

    @Test
    public void shouldNotSkipAfterPassedCheck() {
        Mockito.when(mockPredicate.apply(ArgumentMatchers.anyListOf(Integer.class))).thenReturn(true);
        final Repository<List<Integer>> repository = Repositories.repositoryWithInitialValue(RepositoryTerminationTest.INITIAL_LIST).observe().onUpdatesPerLoop().check(mockPredicate).orSkip().thenGetFrom(listSource).compile();
        updatable.addToObservable(repository);
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoryTerminationTest.LIST));
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasUpdated());
    }

    @Test
    public void shouldSkipWhenAttemptGetFromFails() {
        Mockito.when(mockAttemptSupplier.get()).thenReturn(RepositoryTerminationTest.FAILURE);
        final Repository<Integer> repository = Repositories.repositoryWithInitialValue(RepositoryTerminationTest.INITIAL_VALUE).observe().onUpdatesPerLoop().attemptGetFrom(mockAttemptSupplier).orSkip().thenTransform(Functions.<Integer>identityFunction()).compile();
        updatable.addToObservable(repository);
        Mockito.verify(mockAttemptSupplier).get();
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoryTerminationTest.INITIAL_VALUE));
    }

    @Test
    public void shouldNotSkipWhenAttemptGetFromSucceeds() {
        Mockito.when(mockAttemptSupplier.get()).thenReturn(RepositoryTerminationTest.SUCCESS_WITH_VALUE);
        final Repository<Integer> repository = Repositories.repositoryWithInitialValue(RepositoryTerminationTest.INITIAL_VALUE).observe().onUpdatesPerLoop().attemptGetFrom(mockAttemptSupplier).orSkip().thenTransform(Functions.<Integer>identityFunction()).compile();
        updatable.addToObservable(repository);
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoryTerminationTest.VALUE));
    }
}

