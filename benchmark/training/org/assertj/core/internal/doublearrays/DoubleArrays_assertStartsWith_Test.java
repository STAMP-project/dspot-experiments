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
import org.assertj.core.error.ShouldStartWith;
import org.assertj.core.internal.DoubleArraysBaseTest;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.test.DoubleArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link DoubleArrays#assertStartsWith(AssertionInfo, double[], double[])}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class DoubleArrays_assertStartsWith_Test extends DoubleArraysBaseTest {
    @Test
    public void should_throw_error_if_sequence_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertStartsWith(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_pass_if_actual_and_given_values_are_empty() {
        actual = DoubleArrays.emptyArray();
        arrays.assertStartsWith(TestData.someInfo(), actual, DoubleArrays.emptyArray());
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertStartsWith(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertStartsWith(someInfo(), null, arrayOf(8.0))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_sequence_is_bigger_than_actual() {
        double[] sequence = new double[]{ 6.0, 8.0, 10.0, 12.0, 20.0, 22.0 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertStartsWith(someInfo(), actual, sequence)).withMessage(ShouldStartWith.shouldStartWith(actual, sequence).create());
    }

    @Test
    public void should_fail_if_actual_does_not_start_with_sequence() {
        double[] sequence = new double[]{ 8.0, 10.0 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertStartsWith(someInfo(), actual, sequence)).withMessage(ShouldStartWith.shouldStartWith(actual, sequence).create());
    }

    @Test
    public void should_fail_if_actual_starts_with_first_elements_of_sequence_only() {
        double[] sequence = new double[]{ 6.0, 20.0 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertStartsWith(someInfo(), actual, sequence)).withMessage(ShouldStartWith.shouldStartWith(actual, sequence).create());
    }

    @Test
    public void should_pass_if_actual_starts_with_sequence() {
        arrays.assertStartsWith(TestData.someInfo(), actual, DoubleArrays.arrayOf(6.0, 8.0, 10.0));
    }

    @Test
    public void should_pass_if_actual_and_sequence_are_equal() {
        arrays.assertStartsWith(TestData.someInfo(), actual, DoubleArrays.arrayOf(6.0, 8.0, 10.0, 12.0));
    }

    @Test
    public void should_throw_error_if_sequence_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arraysWithCustomComparisonStrategy.assertStartsWith(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertStartsWith(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertStartsWith(someInfo(), null, arrayOf((-8.0)))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_sequence_is_bigger_than_actual_according_to_custom_comparison_strategy() {
        double[] sequence = new double[]{ 6.0, -8.0, 10.0, 12.0, 20.0, 22.0 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertStartsWith(someInfo(), actual, sequence)).withMessage(ShouldStartWith.shouldStartWith(actual, sequence, absValueComparisonStrategy).create());
    }

    @Test
    public void should_fail_if_actual_does_not_start_with_sequence_according_to_custom_comparison_strategy() {
        double[] sequence = new double[]{ -8.0, 10.0 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertStartsWith(someInfo(), actual, sequence)).withMessage(ShouldStartWith.shouldStartWith(actual, sequence, absValueComparisonStrategy).create());
    }

    @Test
    public void should_fail_if_actual_starts_with_first_elements_of_sequence_only_according_to_custom_comparison_strategy() {
        double[] sequence = new double[]{ 6.0, 20.0 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertStartsWith(someInfo(), actual, sequence)).withMessage(ShouldStartWith.shouldStartWith(actual, sequence, absValueComparisonStrategy).create());
    }

    @Test
    public void should_pass_if_actual_starts_with_sequence_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertStartsWith(TestData.someInfo(), actual, DoubleArrays.arrayOf(6.0, (-8.0), 10.0));
    }

    @Test
    public void should_pass_if_actual_and_sequence_are_equal_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertStartsWith(TestData.someInfo(), actual, DoubleArrays.arrayOf(6.0, (-8.0), 10.0, 12.0));
    }
}

