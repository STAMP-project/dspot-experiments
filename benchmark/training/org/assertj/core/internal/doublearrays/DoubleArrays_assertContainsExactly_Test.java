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
package org.assertj.core.internal.doublearrays;


import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldContainExactly;
import org.assertj.core.internal.DoubleArraysBaseTest;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.test.DoubleArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link DoubleArrays#assertContainsExactly(AssertionInfo, double[], double[])}</code>.
 */
public class DoubleArrays_assertContainsExactly_Test extends DoubleArraysBaseTest {
    @Test
    public void should_pass_if_actual_contains_given_values_exactly() {
        arrays.assertContainsExactly(TestData.someInfo(), actual, DoubleArrays.arrayOf(6.0, 8.0, 10.0));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_exactly_with_duplicates() {
        actual = DoubleArrays.arrayOf(6.0, 8.0, 8.0);
        arrays.assertContainsExactly(TestData.someInfo(), actual, DoubleArrays.arrayOf(6.0, 8.0, 8.0));
    }

    @Test
    public void should_pass_if_actual_and_given_values_are_empty() {
        arrays.assertContainsExactly(TestData.someInfo(), DoubleArrays.emptyArray(), DoubleArrays.emptyArray());
    }

    @Test
    public void should_fail_if_actual_contains_given_values_exactly_but_in_different_order() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsExactly(someInfo(), actual, arrayOf(6.0, 10.0, 8.0))).withMessage(ShouldContainExactly.elementsDifferAtIndex(8.0, 10.0, 1).create());
    }

    @Test
    public void should_fail_if_arrays_have_different_sizes() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsExactly(someInfo(), actual, arrayOf(6.0, 8.0)));
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
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsExactly(someInfo(), null, arrayOf(8.0))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_given_values_exactly() {
        double[] expected = new double[]{ 6.0, 8.0, 20.0 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsExactly(someInfo(), actual, expected)).withMessage(ShouldContainExactly.shouldContainExactly(actual, Arrays.asList(expected), Lists.newArrayList(20.0), Lists.newArrayList(10.0)).create());
    }

    @Test
    public void should_fail_if_actual_contains_all_given_values_but_size_differ() {
        double[] expected = new double[]{ 6.0, 8.0, 10.0, 10.0 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsExactly(someInfo(), actual, expected)).withMessage(ShouldContainExactly.shouldContainExactly(actual, Arrays.asList(expected), Lists.newArrayList(10.0), Lists.newArrayList()).create());
    }

    // ------------------------------------------------------------------------------------------------------------------
    // tests using a custom comparison strategy
    // ------------------------------------------------------------------------------------------------------------------
    @Test
    public void should_pass_if_actual_contains_given_values_exactly_according_to_custom_comparison_strategy() {
        actual = DoubleArrays.arrayOf(6, (-8), 8);
        arraysWithCustomComparisonStrategy.assertContainsExactly(TestData.someInfo(), actual, DoubleArrays.arrayOf(6.0, (-8.0), 8.0));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_exactly_in_different_order_according_to_custom_comparison_strategy() {
        double[] expected = new double[]{ -6.0, 10.0, 8.0 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsExactly(someInfo(), actual, expected)).withMessage(ShouldContainExactly.elementsDifferAtIndex(8.0, 10.0, 1, absValueComparisonStrategy).create());
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
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsExactly(someInfo(), null, arrayOf((-8.0)))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_given_values_exactly_according_to_custom_comparison_strategy() {
        double[] expected = new double[]{ 6.0, -8.0, 20.0 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsExactly(someInfo(), actual, expected)).withMessage(String.format(ShouldContainExactly.shouldContainExactly(actual, Arrays.asList(expected), Lists.newArrayList(20.0), Lists.newArrayList(10.0), absValueComparisonStrategy).create()));
    }

    @Test
    public void should_fail_if_actual_contains_all_given_values_but_size_differ_according_to_custom_comparison_strategy() {
        double[] expected = new double[]{ 6.0, 8.0, 10.0, 10.0 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsExactly(someInfo(), actual, expected)).withMessage(String.format(ShouldContainExactly.shouldContainExactly(actual, Arrays.asList(expected), Lists.newArrayList(10.0), Lists.newArrayList(), absValueComparisonStrategy).create()));
    }
}

