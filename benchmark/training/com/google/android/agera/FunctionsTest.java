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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public final class FunctionsTest {
    private static final int VALUE = 42;

    private static final int VALUE_PLUS_TWO = 44;

    private static final int RECOVER_VALUE = 43;

    private static final String INPUT_STRING = "input";

    private static final List<String> INPUT_LIST = Arrays.asList("some", "strings", "for", "testing");

    @SuppressWarnings("ThrowableInstanceNeverThrown")
    private static final Throwable THROWABLE = new Throwable();

    private static final Result<Integer> FAILURE = Result.failure(FunctionsTest.THROWABLE);

    private static final Result<Integer> RECOVER_SUCCESS = Result.success(FunctionsTest.RECOVER_VALUE);

    private static final Result<Integer> PRESENT_WITH_VALUE = Result.present(FunctionsTest.VALUE);

    private static final Result<Integer> ABSENT = Result.absent();

    private static final Result<List<Integer>> FAILURE_LIST = Result.failure(FunctionsTest.THROWABLE);

    private static final Result<List<Integer>> ABSENT_LIST = Result.absent();

    private static final Result<List<String>> PRESENT_WITH_LIST = Result.present(FunctionsTest.INPUT_LIST);

    @Mock
    private Function<Integer, Result<Integer>> mockDivideTenFunction;

    @Mock
    private Function<Integer, Integer> mockPlusTwoFunction;

    @Mock
    private Function<Throwable, Result<Integer>> mockTryRecoverFunction;

    @Mock
    private Function<Throwable, Integer> mockRecoverFunction;

    @Mock
    private Supplier<String> mockSupplier;

    @Test
    public void shouldWrapThrowableInFailedResult() {
        final Throwable throwable = new Throwable();
        MatcherAssert.assertThat(Functions.failedResult().apply(throwable).getFailure(), Matchers.is(throwable));
    }

    @Test
    public void shouldFunctionReturningEmptyListForAbsent() {
        MatcherAssert.assertThat(Functions.<Integer>resultAsList().apply(FunctionsTest.ABSENT), Matchers.is(Matchers.empty()));
    }

    @Test
    public void shouldReturnFunctionReturingEmptyListForFailure() {
        MatcherAssert.assertThat(Functions.<Integer>resultAsList().apply(FunctionsTest.FAILURE), Matchers.is(Matchers.empty()));
    }

    @Test
    public void shouldReturnFunctionReturningListWithValueForPresentWithValue() {
        MatcherAssert.assertThat(Functions.<Integer>resultAsList().apply(FunctionsTest.PRESENT_WITH_VALUE), Matchers.contains(FunctionsTest.VALUE));
    }

    @Test
    public void shouldFunctionReturningEmptyListForAbsentList() {
        MatcherAssert.assertThat(Functions.<Integer>resultListAsList().apply(FunctionsTest.ABSENT_LIST), Matchers.is(Matchers.empty()));
    }

    @Test
    public void shouldReturnFunctionReturingEmptyListForFailureList() {
        MatcherAssert.assertThat(Functions.<Integer>resultListAsList().apply(FunctionsTest.FAILURE_LIST), Matchers.is(Matchers.empty()));
    }

    @Test
    public void shouldReturnFunctionReturningListWithValueForPresentWithList() {
        MatcherAssert.assertThat(Functions.<String>resultListAsList().apply(FunctionsTest.PRESENT_WITH_LIST), Matchers.is(FunctionsTest.INPUT_LIST));
    }

    @Test
    public void shouldReturnFunctionReturningListWithValue() {
        MatcherAssert.assertThat(Functions.<Integer>itemAsList().apply(FunctionsTest.VALUE), Matchers.contains(FunctionsTest.VALUE));
    }

    @Test
    public void shouldReturnObjectFromStaticFunction() {
        MatcherAssert.assertThat(Functions.staticFunction(FunctionsTest.INPUT_STRING).apply(new Object()), Matchers.is(Matchers.sameInstance(FunctionsTest.INPUT_STRING)));
    }

    @Test
    public void shouldReturnObjectFromSupplierForSupplierAsFunction() {
        MatcherAssert.assertThat(Functions.supplierAsFunction(mockSupplier).apply(new Object()), Matchers.is(Matchers.sameInstance(FunctionsTest.INPUT_STRING)));
    }

    @Test
    public void shouldReturnFromObject() {
        MatcherAssert.assertThat(Functions.<String>identityFunction().apply(FunctionsTest.INPUT_STRING), Matchers.is(Matchers.sameInstance(FunctionsTest.INPUT_STRING)));
    }

    @Test
    public void shouldBeASingleton() {
        MatcherAssert.assertThat(Functions.identityFunction(), Matchers.is(Matchers.sameInstance(Functions.identityFunction())));
    }

    @Test
    public void shouldHavePrivateConstructor() {
        MatcherAssert.assertThat(Functions.class, HasPrivateConstructor.hasPrivateConstructor());
    }

    @Test
    public void shouldCreateFunctionFromItemToItem() {
        final Function<String, Integer> function = Functions.functionFrom(String.class).apply(new FunctionsTest.DoubleString()).thenApply(new FunctionsTest.StringLength());
        MatcherAssert.assertThat(function.apply(FunctionsTest.INPUT_STRING), Matchers.is(10));
    }

    @Test
    public void shouldCreateFunctionFromItemToItemViaList() {
        final Function<String, String> function = Functions.functionFrom(String.class).apply(new FunctionsTest.DoubleString()).unpack(new FunctionsTest.StringToListChar()).morph(new FunctionsTest.SortList<Character>()).limit(5).filter(new FunctionsTest.CharacterFilter('n')).thenApply(new FunctionsTest.CharacterListToString());
        MatcherAssert.assertThat(function.apply(FunctionsTest.INPUT_STRING), Matchers.is("nn"));
    }

    @Test
    public void shouldCreateFunctionFromListToItem() {
        final Function<List<String>, Integer> function = Functions.functionFromListOf(String.class).limit(3).map(new FunctionsTest.StringLength()).thenApply(new FunctionsTest.SumOfIntegersInList());
        MatcherAssert.assertThat(function.apply(FunctionsTest.INPUT_LIST), Matchers.is(14));
    }

    @Test
    public void shouldCreateFunctionFromItemToList() {
        final Function<String, List<Character>> function = Functions.functionFrom(String.class).apply(new FunctionsTest.DoubleString()).unpack(new FunctionsTest.StringToListChar()).thenLimit(7);
        MatcherAssert.assertThat(function.apply(FunctionsTest.INPUT_STRING), Matchers.contains('i', 'n', 'p', 'u', 't', 'i', 'n'));
    }

    @Test
    public void shouldCreateFunctionFromItemToListWithThenFilter() {
        final Function<String, List<Character>> function = Functions.functionFrom(String.class).apply(new FunctionsTest.DoubleString()).unpack(new FunctionsTest.StringToListChar()).thenFilter(new FunctionsTest.CharacterFilter('n'));
        MatcherAssert.assertThat(function.apply(FunctionsTest.INPUT_STRING), Matchers.contains('n', 'n'));
    }

    @Test
    public void shouldCreateFunctionFromListToList() {
        final Function<List<String>, List<Integer>> function = Functions.functionFromListOf(String.class).limit(3).thenMap(new FunctionsTest.StringLength());
        MatcherAssert.assertThat(function.apply(FunctionsTest.INPUT_LIST), Matchers.contains(4, 7, 3));
    }

    @Test
    public void shouldCreateFunctionFromListToSortedList() {
        final Function<List<String>, List<Integer>> function = Functions.functionFromListOf(String.class).map(new FunctionsTest.StringLength()).thenSort(new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return lhs.compareTo(rhs);
            }
        });
        final List<String> inputList = new ArrayList<>(FunctionsTest.INPUT_LIST);
        MatcherAssert.assertThat(function.apply(inputList), Matchers.contains(3, 4, 7, 7));
    }

    @Test
    public void shouldNotMutateInputListWhenSorting() {
        final Function<List<String>, List<String>> function = Functions.functionFromListOf(String.class).thenSort(new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);
            }
        });
        final List<String> inputList = new ArrayList<>(FunctionsTest.INPUT_LIST);
        MatcherAssert.assertThat(function.apply(inputList), Matchers.contains("for", "some", "strings", "testing"));
        MatcherAssert.assertThat(inputList, Matchers.is(FunctionsTest.INPUT_LIST));
    }

    @Test
    public void shouldCreateFunctionFromListToListWithZeroLimit() {
        final Function<List<String>, List<Integer>> function = Functions.functionFromListOf(String.class).limit(0).thenMap(new FunctionsTest.StringLength());
        MatcherAssert.assertThat(function.apply(FunctionsTest.INPUT_LIST), Matchers.<Integer>emptyIterable());
    }

    @Test
    public void shouldCreateFunctionFromListToListWithLimitLargerThanInputList() {
        final Function<List<String>, List<Integer>> function = Functions.functionFromListOf(String.class).limit(((FunctionsTest.INPUT_LIST.size()) * 2)).thenMap(new FunctionsTest.StringLength());
        MatcherAssert.assertThat(function.apply(FunctionsTest.INPUT_LIST), Matchers.contains(4, 7, 3, 7));
    }

    @Test
    public void shouldReturnEmptyListForFilterOfEmptyList() {
        final Predicate<String> predicate = Mockito.mock(Predicate.class);
        Mockito.when(predicate.apply(ArgumentMatchers.anyString())).thenReturn(true);
        final Function<List<String>, List<String>> function = Functions.functionFromListOf(String.class).thenFilter(predicate);
        MatcherAssert.assertThat(function.apply(new ArrayList<String>()), Matchers.sameInstance(Collections.<String>emptyList()));
    }

    @Test
    public void shouldReturnIdentityFunctionIfNoFunctionsAddedToCompiler() {
        final Function<List<String>, List<String>> function = Functions.functionFromListOf(String.class).map(Functions.<String>identityFunction()).filter(Predicates.<String>truePredicate()).thenApply(Functions.<List<String>>identityFunction());
        MatcherAssert.assertThat(function, Matchers.sameInstance(Functions.<List<String>>identityFunction()));
    }

    private static final class DoubleString implements Function<String, String> {
        @NonNull
        @Override
        public String apply(@NonNull
        final String input) {
            return input + input;
        }
    }

    private static final class StringLength implements Function<String, Integer> {
        @NonNull
        @Override
        public Integer apply(@NonNull
        final String input) {
            return input.length();
        }
    }

    private static final class StringToListChar implements Function<String, List<Character>> {
        @NonNull
        @Override
        public List<Character> apply(@NonNull
        final String input) {
            List<Character> list = new ArrayList<>();
            for (final char c : input.toCharArray()) {
                list.add(c);
            }
            return list;
        }
    }

    private static final class SortList<T extends Comparable<T>> implements Function<List<T>, List<T>> {
        @NonNull
        @Override
        public List<T> apply(@NonNull
        final List<T> input) {
            final List<T> output = new ArrayList<>(input);
            Collections.sort(output);
            return output;
        }
    }

    private static final class CharacterFilter implements Predicate<Character> {
        private final char character;

        public CharacterFilter(final char p) {
            character = p;
        }

        @Override
        public boolean apply(@NonNull
        final Character value) {
            return value.equals(character);
        }
    }

    private final class CharacterListToString implements Function<List<Character>, String> {
        @NonNull
        @Override
        public String apply(@NonNull
        final List<Character> input) {
            final StringBuilder stringBuilder = new StringBuilder();
            for (final char character : input) {
                stringBuilder.append(character);
            }
            return stringBuilder.toString();
        }
    }

    private static final class SumOfIntegersInList implements Function<List<Integer>, Integer> {
        @NonNull
        @Override
        public Integer apply(@NonNull
        final List<Integer> input) {
            int output = 0;
            for (final int integer : input) {
                output += integer;
            }
            return output;
        }
    }
}

