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
import com.google.android.agera.test.mocks.MockUpdatable;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@Config(manifest = NONE)
@RunWith(RobolectricTestRunner.class)
public final class RepositoryContinuationTest {
    private static final int INITIAL_VALUE = 8;

    private static final Result<Integer> SUPPLIER_SUCCESSFUL_RESULT = Result.success(100);

    private static final Result<Integer> MERGER_SUCCESSFUL_RESULT = Result.success(200);

    private static final Result<Integer> FUNCTION_SUCCESSFUL_RESULT = Result.success(300);

    private static final Result<Integer> SUPPLIER_FAILED_RESULT = Result.failure(new Throwable("-100"));

    private static final Result<Integer> MERGER_FAILED_RESULT = Result.failure(new Throwable("-200"));

    private static final Result<Integer> FUNCTION_FAILED_RESULT = Result.failure(new Throwable("-300"));

    private static final Supplier<Integer> SECOND_SUPPLIER = Suppliers.staticSupplier(400);

    private static final int RECOVERY_VALUE = 42;

    private Repository<Integer> repository;

    private MockUpdatable updatable;

    @Mock
    private Supplier<Result<Integer>> mockAttemptSupplier;

    @Mock
    private Merger<Throwable, Integer, Result<Integer>> mockAttemptMerger;

    @Mock
    private Function<Throwable, Result<Integer>> mockAttemptFunction;

    @Mock
    private Function<Throwable, Integer> mockRecoveryFunction;

    @Test
    public void shouldProduceSupplierResultIfSupplierSucceeds() {
        Mockito.when(mockAttemptSupplier.get()).thenReturn(RepositoryContinuationTest.SUPPLIER_SUCCESSFUL_RESULT);
        updatable.addToObservable(repository);
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoryContinuationTest.SUPPLIER_SUCCESSFUL_RESULT.get()));
        Mockito.verifyNoMoreInteractions(mockAttemptMerger, mockAttemptFunction, mockRecoveryFunction);
    }

    @Test
    public void shouldProduceMergerResultIfMergerSucceeds() {
        Mockito.when(mockAttemptSupplier.get()).thenReturn(RepositoryContinuationTest.SUPPLIER_FAILED_RESULT);
        Mockito.when(mockAttemptMerger.merge(RepositoryContinuationTest.SUPPLIER_FAILED_RESULT.getFailure(), RepositoryContinuationTest.SECOND_SUPPLIER.get())).thenReturn(RepositoryContinuationTest.MERGER_SUCCESSFUL_RESULT);
        updatable.addToObservable(repository);
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoryContinuationTest.MERGER_SUCCESSFUL_RESULT.get()));
        Mockito.verifyNoMoreInteractions(mockAttemptFunction, mockRecoveryFunction);
    }

    @Test
    public void shouldProduceFunctionResultIfFunctionSucceeds() {
        Mockito.when(mockAttemptSupplier.get()).thenReturn(RepositoryContinuationTest.SUPPLIER_FAILED_RESULT);
        Mockito.when(mockAttemptMerger.merge(RepositoryContinuationTest.SUPPLIER_FAILED_RESULT.getFailure(), RepositoryContinuationTest.SECOND_SUPPLIER.get())).thenReturn(RepositoryContinuationTest.MERGER_FAILED_RESULT);
        Mockito.when(mockAttemptFunction.apply(RepositoryContinuationTest.MERGER_FAILED_RESULT.getFailure())).thenReturn(RepositoryContinuationTest.FUNCTION_SUCCESSFUL_RESULT);
        updatable.addToObservable(repository);
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoryContinuationTest.FUNCTION_SUCCESSFUL_RESULT.get()));
        Mockito.verifyNoMoreInteractions(mockRecoveryFunction);
    }

    @Test
    public void shouldProduceRecoveryResultIfAllAttemptsFail() {
        Mockito.when(mockAttemptSupplier.get()).thenReturn(RepositoryContinuationTest.SUPPLIER_FAILED_RESULT);
        Mockito.when(mockAttemptMerger.merge(RepositoryContinuationTest.SUPPLIER_FAILED_RESULT.getFailure(), RepositoryContinuationTest.SECOND_SUPPLIER.get())).thenReturn(RepositoryContinuationTest.MERGER_FAILED_RESULT);
        Mockito.when(mockAttemptFunction.apply(RepositoryContinuationTest.MERGER_FAILED_RESULT.getFailure())).thenReturn(RepositoryContinuationTest.FUNCTION_FAILED_RESULT);
        Mockito.when(mockRecoveryFunction.apply(RepositoryContinuationTest.FUNCTION_FAILED_RESULT.getFailure())).thenReturn(RepositoryContinuationTest.RECOVERY_VALUE);
        updatable.addToObservable(repository);
        MatcherAssert.assertThat(repository, SupplierGives.has(RepositoryContinuationTest.RECOVERY_VALUE));
    }
}

