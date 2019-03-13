/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.internal.chararrays;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldContainExactly;
import org.assertj.core.internal.CharArraysBaseTest;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.test.CharArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link CharArrays#assertContainsExactly(AssertionInfo, char[], char[])}</code>.
 */
public class CharArrays_assertContainsExactly_Test extends CharArraysBaseTest {
    @Test
    public void should_pass_if_actual_contains_given_values_exactly() {
        arrays.assertContainsExactly(TestData.someInfo(), actual, CharArrays.arrayOf('a', 'b', 'c'));
    }

    @Test
    public void should_pass_if_actual_and_given_values_are_empty() {
        arrays.assertContainsExactly(TestData.someInfo(), CharArrays.emptyArray(), CharArrays.emptyArray());
    }

    @Test
    public void should_fail_if_actual_contains_given_values_exactly_but_in_different_order() {
        AssertionInfo info = TestData.someInfo();
        try {
            arrays.assertContainsExactly(info, actual, CharArrays.arrayOf('a', 'c', 'b'));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainExactly.elementsDifferAtIndex('b', 'c', 1));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_arrays_have_different_sizes() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsExactly(someInfo(), actual, arrayOf('a', 'b')));
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsExactly(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertContainsExactly(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsExactly(someInfo(), null, arrayOf('b'))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_given_values_exactly() {
        AssertionInfo info = TestData.someInfo();
        char[] expected = new char[]{ 'a', 'b', 'e' };
        try {
            arrays.assertContainsExactly(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainExactly.shouldContainExactly(actual, Arrays.asList(expected), Lists.newArrayList('e'), Lists.newArrayList('c')));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_contains_all_given_values_but_size_differ() {
        AssertionInfo info = TestData.someInfo();
        char[] expected = new char[]{ 'a', 'b', 'c', 'c' };
        try {
            arrays.assertContainsExactly(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainExactly.shouldContainExactly(actual, Arrays.asList(expected), Lists.newArrayList('c'), Lists.newArrayList()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    // ------------------------------------------------------------------------------------------------------------------
    // tests using a custom comparison strategy
    // ------------------------------------------------------------------------------------------------------------------
    @Test
    public void should_pass_if_actual_contains_given_values_exactly_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContainsExactly(TestData.someInfo(), actual, CharArrays.arrayOf('a', 'B', 'c'));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_exactly_in_different_order_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        char[] expected = new char[]{ 'A', 'c', 'b' };
        try {
            arraysWithCustomComparisonStrategy.assertContainsExactly(TestData.someInfo(), actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainExactly.elementsDifferAtIndex('b', 'c', 1, caseInsensitiveComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsExactly(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsExactly(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsExactly(someInfo(), null, arrayOf('b'))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_given_values_exactly_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        char[] expected = new char[]{ 'a', 'B', 'e' };
        try {
            arraysWithCustomComparisonStrategy.assertContainsExactly(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainExactly.shouldContainExactly(actual, Arrays.asList(expected), Lists.newArrayList('e'), Lists.newArrayList('c'), caseInsensitiveComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_contains_all_given_values_but_size_differ_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        char[] expected = new char[]{ 'a', 'b', 'C', 'c' };
        try {
            arraysWithCustomComparisonStrategy.assertContainsExactly(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainExactly.shouldContainExactly(actual, Arrays.asList(expected), Lists.newArrayList('c'), Lists.newArrayList(), caseInsensitiveComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

