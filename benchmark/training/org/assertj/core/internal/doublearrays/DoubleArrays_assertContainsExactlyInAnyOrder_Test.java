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


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldContainExactlyInAnyOrder;
import org.assertj.core.internal.DoubleArraysBaseTest;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.StandardComparisonStrategy;
import org.assertj.core.test.DoubleArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link DoubleArrays#assertContainsExactlyInAnyOrder(AssertionInfo, double[], double[])}</code>.
 */
public class DoubleArrays_assertContainsExactlyInAnyOrder_Test extends DoubleArraysBaseTest {
    @Test
    public void should_pass_if_actual_contains_given_values_exactly_in_any_order() {
        arrays.assertContainsExactlyInAnyOrder(TestData.someInfo(), actual, DoubleArrays.arrayOf(6.0, 8.0, 10.0));
    }

    @Test
    public void should_pass_if_actual_and_given_values_are_empty() {
        arrays.assertContainsExactlyInAnyOrder(TestData.someInfo(), DoubleArrays.emptyArray(), DoubleArrays.emptyArray());
    }

    @Test
    public void should_pass_if_actual_contains_given_values_exactly_but_in_different_order() {
        AssertionInfo info = TestData.someInfo();
        arrays.assertContainsExactlyInAnyOrder(info, actual, DoubleArrays.arrayOf(6.0, 10.0, 8.0));
    }

    @Test
    public void should_fail_if_arrays_have_different_sizes() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsExactlyInAnyOrder(someInfo(), actual, arrayOf(6.0, 8.0)));
    }

    @Test
    public void should_fail_if_expected_is_empty_and_actual_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsExactlyInAnyOrder(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_throw_error_if_expected_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertContainsExactlyInAnyOrder(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsExactlyInAnyOrder(someInfo(), null, arrayOf(8.0))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_given_values_exactly() {
        double[] expected = new double[]{ 6.0, 8.0, 20.0 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsExactlyInAnyOrder(someInfo(), actual, expected)).withMessage(String.format(ShouldContainExactlyInAnyOrder.shouldContainExactlyInAnyOrder(actual, expected, Lists.newArrayList(20.0), Lists.newArrayList(10.0), StandardComparisonStrategy.instance()).create()));
    }

    @Test
    public void should_fail_if_actual_contains_all_given_values_but_size_differ() {
        double[] expected = new double[]{ 6.0, 8.0 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsExactlyInAnyOrder(someInfo(), actual, expected)).withMessage(String.format(ShouldContainExactlyInAnyOrder.shouldContainExactlyInAnyOrder(actual, expected, Lists.emptyList(), Lists.newArrayList(10.0), StandardComparisonStrategy.instance()).create()));
    }

    @Test
    public void should_fail_if_actual_contains_duplicates_and_expected_does_not() {
        actual = DoubleArrays.arrayOf(1.0, 2.0, 3.0);
        double[] expected = new double[]{ 1.0, 2.0 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsExactlyInAnyOrder(someInfo(), actual, expected)).withMessage(String.format(ShouldContainExactlyInAnyOrder.shouldContainExactlyInAnyOrder(actual, expected, Lists.emptyList(), Lists.newArrayList(3.0), StandardComparisonStrategy.instance()).create()));
    }

    @Test
    public void should_fail_if_expected_contains_duplicates_and_actual_does_not() {
        actual = DoubleArrays.arrayOf(1.0, 2.0);
        double[] expected = new double[]{ 1.0, 2.0, 3.0 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsExactlyInAnyOrder(someInfo(), actual, expected)).withMessage(ShouldContainExactlyInAnyOrder.shouldContainExactlyInAnyOrder(actual, expected, Lists.newArrayList(3.0), Lists.emptyList(), StandardComparisonStrategy.instance()).create());
    }

    // ------------------------------------------------------------------------------------------------------------------
    // tests using a custom comparison strategy
    // ------------------------------------------------------------------------------------------------------------------
    @Test
    public void should_pass_if_actual_contains_given_values_exactly_in_any_order_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContainsExactlyInAnyOrder(TestData.someInfo(), actual, DoubleArrays.arrayOf(6.0, (-8.0), 10.0));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_exactly_in_different_order_according_to_custom_comparison_strategy() {
        double[] expected = new double[]{ -6.0, 10.0, 8.0 };
        arraysWithCustomComparisonStrategy.assertContainsExactlyInAnyOrder(TestData.someInfo(), actual, expected);
    }

    @Test
    public void should_fail_if_expected_is_empty_and_actual_is_not_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsExactlyInAnyOrder(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_throw_error_if_expected_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsExactlyInAnyOrder(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsExactlyInAnyOrder(someInfo(), null, arrayOf((-8.0)))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_given_values_exactly_according_to_custom_comparison_strategy() {
        double[] expected = new double[]{ 6.0, -8.0, 20.0 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsExactlyInAnyOrder(someInfo(), actual, expected)).withMessage(String.format(ShouldContainExactlyInAnyOrder.shouldContainExactlyInAnyOrder(actual, expected, Lists.newArrayList(20.0), Lists.newArrayList(10.0), absValueComparisonStrategy).create()));
    }

    @Test
    public void should_fail_if_actual_contains_all_given_values_but_size_differ_according_to_custom_comparison_strategy() {
        double[] expected = new double[]{ 6.0, 8.0 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsExactlyInAnyOrder(someInfo(), actual, expected)).withMessage(String.format(ShouldContainExactlyInAnyOrder.shouldContainExactlyInAnyOrder(actual, expected, Lists.emptyList(), Lists.newArrayList(10.0), absValueComparisonStrategy).create()));
    }

    @Test
    public void should_fail_if_actual_contains_duplicates_and_expected_does_not_according_to_custom_comparison_strategy() {
        actual = DoubleArrays.arrayOf(1.0, 2.0, 3.0);
        double[] expected = new double[]{ 1.0, 2.0 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsExactlyInAnyOrder(someInfo(), actual, expected)).withMessage(String.format(ShouldContainExactlyInAnyOrder.shouldContainExactlyInAnyOrder(actual, expected, Lists.emptyList(), Lists.newArrayList(3.0), absValueComparisonStrategy).create()));
    }

    @Test
    public void should_fail_if_expected_contains_duplicates_and_actual_does_not_according_to_custom_comparison_strategy() {
        actual = DoubleArrays.arrayOf(1.0, 2.0);
        double[] expected = new double[]{ 1.0, 2.0, 3.0 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsExactlyInAnyOrder(someInfo(), actual, expected)).withMessage(String.format(ShouldContainExactlyInAnyOrder.shouldContainExactlyInAnyOrder(actual, expected, Lists.newArrayList(3.0), Lists.emptyList(), absValueComparisonStrategy).create()));
    }
}

