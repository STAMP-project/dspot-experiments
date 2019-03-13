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
import org.assertj.core.error.ShouldContainOnly;
import org.assertj.core.internal.DoubleArraysBaseTest;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.test.DoubleArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link DoubleArrays#assertContainsOnly(AssertionInfo, double[], double[])}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class DoubleArrays_assertContainsOnly_Test extends DoubleArraysBaseTest {
    @Test
    public void should_pass_if_actual_contains_given_values_only() {
        arrays.assertContainsOnly(TestData.someInfo(), actual, DoubleArrays.arrayOf(6.0, 8.0, 10.0));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_in_different_order() {
        arrays.assertContainsOnly(TestData.someInfo(), actual, DoubleArrays.arrayOf(10.0, 8.0, 6.0));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_more_than_once() {
        actual = DoubleArrays.arrayOf(6.0, 8.0, 10.0, 8.0, 8.0, 8.0);
        arrays.assertContainsOnly(TestData.someInfo(), actual, DoubleArrays.arrayOf(6.0, 8.0, 10.0));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_even_if_duplicated() {
        arrays.assertContainsOnly(TestData.someInfo(), actual, DoubleArrays.arrayOf(6.0, 8.0, 10.0, 6.0, 8.0, 10.0));
    }

    @Test
    public void should_pass_if_actual_and_given_values_are_empty() {
        actual = DoubleArrays.emptyArray();
        arrays.assertContainsOnly(TestData.someInfo(), actual, DoubleArrays.emptyArray());
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsOnly(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertContainsOnly(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsOnly(someInfo(), null, arrayOf(8.0))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_given_values_only() {
        double[] expected = new double[]{ 6.0, 8.0, 20.0 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsOnly(someInfo(), actual, expected)).withMessage(ShouldContainOnly.shouldContainOnly(actual, expected, Lists.newArrayList(20.0), Lists.newArrayList(10.0)).create());
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContainsOnly(TestData.someInfo(), actual, DoubleArrays.arrayOf(6.0, (-8.0), 10.0));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_in_different_order_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContainsOnly(TestData.someInfo(), actual, DoubleArrays.arrayOf(10.0, (-8.0), 6.0));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_more_than_once_according_to_custom_comparison_strategy() {
        actual = DoubleArrays.arrayOf(6.0, (-8.0), 10.0, (-8.0), (-8.0), (-8.0));
        arraysWithCustomComparisonStrategy.assertContainsOnly(TestData.someInfo(), actual, DoubleArrays.arrayOf(6.0, (-8.0), 10.0));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_even_if_duplicated_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContainsOnly(TestData.someInfo(), actual, DoubleArrays.arrayOf(6.0, (-8.0), 10.0, 6.0, (-8.0), 10.0));
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsOnly(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsOnly(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsOnly(someInfo(), null, arrayOf((-8.0)))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_given_values_only_according_to_custom_comparison_strategy() {
        double[] expected = new double[]{ 6.0, -8.0, 20.0 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsOnly(someInfo(), actual, expected)).withMessage(String.format(ShouldContainOnly.shouldContainOnly(actual, expected, Lists.newArrayList(20.0), Lists.newArrayList(10.0), absValueComparisonStrategy).create()));
    }
}

