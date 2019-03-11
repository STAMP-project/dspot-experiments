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
import org.assertj.core.error.ShouldContainsOnlyOnce;
import org.assertj.core.internal.DoubleArraysBaseTest;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.test.DoubleArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link DoubleArrays#assertContainsOnlyOnce(AssertionInfo, double[], double[])}</code>.
 *
 * @author William Delanoue
 */
public class DoubleArrays_assertContainsOnlyOnce_Test extends DoubleArraysBaseTest {
    @Test
    public void should_pass_if_actual_contains_given_values_only() {
        arrays.assertContainsOnlyOnce(TestData.someInfo(), actual, DoubleArrays.arrayOf(6, 8, 10));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_in_different_order() {
        arrays.assertContainsOnlyOnce(TestData.someInfo(), actual, DoubleArrays.arrayOf(10, 8, 6));
    }

    @Test
    public void should_fail_if_actual_contains_given_values_only_more_than_once() {
        actual = DoubleArrays.arrayOf(6, (-8), 10, (-6), (-8), 10, (-8), 6);
        double[] expected = new double[]{ 6, -8, 20 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsOnlyOnce(someInfo(), actual, expected)).withMessage(String.format(ShouldContainsOnlyOnce.shouldContainsOnlyOnce(actual, expected, Sets.newLinkedHashSet(((double) (20))), Sets.newLinkedHashSet(((double) (6)), ((double) (-8)))).create()));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_even_if_duplicated() {
        arrays.assertContainsOnlyOnce(TestData.someInfo(), actual, DoubleArrays.arrayOf(6, 8, 10, 6, 8, 10));
    }

    @Test
    public void should_pass_if_actual_and_given_values_are_empty() {
        actual = DoubleArrays.emptyArray();
        arrays.assertContainsOnlyOnce(TestData.someInfo(), actual, DoubleArrays.emptyArray());
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsOnlyOnce(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertContainsOnlyOnce(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsOnlyOnce(someInfo(), null, arrayOf(8))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_given_values_only() {
        double[] expected = new double[]{ 6, 8, 20 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsOnlyOnce(someInfo(), actual, expected)).withMessage(ShouldContainsOnlyOnce.shouldContainsOnlyOnce(actual, expected, Sets.newLinkedHashSet(((double) (20))), Sets.newLinkedHashSet()).create());
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContainsOnlyOnce(TestData.someInfo(), actual, DoubleArrays.arrayOf(6, (-8), 10));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_in_different_order_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContainsOnlyOnce(TestData.someInfo(), actual, DoubleArrays.arrayOf(10, (-8), 6));
    }

    @Test
    public void should_fail_if_actual_contains_given_values_only_more_than_once_according_to_custom_comparison_strategy() {
        actual = DoubleArrays.arrayOf(6, (-8), 10, (-6), (-8), 10, (-8));
        double[] expected = new double[]{ 6, -8, 20 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsOnlyOnce(someInfo(), actual, expected)).withMessage(String.format(ShouldContainsOnlyOnce.shouldContainsOnlyOnce(actual, expected, Sets.newLinkedHashSet(((double) (20))), Sets.newLinkedHashSet(((double) (6)), ((double) (-8))), absValueComparisonStrategy).create()));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_even_if_duplicated_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContainsOnlyOnce(TestData.someInfo(), actual, DoubleArrays.arrayOf(6, 8, 10, 6, (-8), 10));
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsOnlyOnce(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsOnlyOnce(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsOnlyOnce(someInfo(), null, arrayOf((-8)))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_given_values_only_according_to_custom_comparison_strategy() {
        double[] expected = new double[]{ 6, -8, 20 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsOnlyOnce(someInfo(), actual, expected)).withMessage(String.format(ShouldContainsOnlyOnce.shouldContainsOnlyOnce(actual, expected, Sets.newLinkedHashSet(((double) (20))), Sets.newLinkedHashSet(), absValueComparisonStrategy).create()));
    }
}

