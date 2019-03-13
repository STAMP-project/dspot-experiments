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
import org.assertj.core.error.ShouldNotContain;
import org.assertj.core.internal.DoubleArraysBaseTest;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.test.DoubleArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link DoubleArrays#assertDoesNotContain(AssertionInfo, double[], double[])}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class DoubleArrays_assertDoesNotContain_Test extends DoubleArraysBaseTest {
    @Test
    public void should_pass_if_actual_does_not_contain_given_values() {
        arrays.assertDoesNotContain(TestData.someInfo(), actual, DoubleArrays.arrayOf(12.0));
    }

    @Test
    public void should_pass_if_actual_does_not_contain_given_values_even_if_duplicated() {
        arrays.assertDoesNotContain(TestData.someInfo(), actual, DoubleArrays.arrayOf(12.0, 12.0, 20.0));
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_empty() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> arrays.assertDoesNotContain(someInfo(), actual, emptyArray())).withMessage(ErrorMessages.valuesToLookForIsEmpty());
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertDoesNotContain(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertDoesNotContain(someInfo(), null, arrayOf(8.0))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_contains_given_values() {
        double[] expected = new double[]{ 6.0, 8.0, 20.0 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertDoesNotContain(someInfo(), actual, expected)).withMessage(ShouldNotContain.shouldNotContain(actual, expected, Sets.newLinkedHashSet(6.0, 8.0)).create());
    }

    @Test
    public void should_pass_if_actual_does_not_contain_given_values_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertDoesNotContain(TestData.someInfo(), actual, DoubleArrays.arrayOf(12.0));
    }

    @Test
    public void should_pass_if_actual_does_not_contain_given_values_even_if_duplicated_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertDoesNotContain(TestData.someInfo(), actual, DoubleArrays.arrayOf(12.0, 12.0, 20.0));
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_empty_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> arraysWithCustomComparisonStrategy.assertDoesNotContain(someInfo(), actual, emptyArray())).withMessage(ErrorMessages.valuesToLookForIsEmpty());
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arraysWithCustomComparisonStrategy.assertDoesNotContain(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertDoesNotContain(someInfo(), null, arrayOf((-8.0)))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_contains_given_values_according_to_custom_comparison_strategy() {
        double[] expected = new double[]{ 6.0, -8.0, 20.0 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertDoesNotContain(someInfo(), actual, expected)).withMessage(String.format(ShouldNotContain.shouldNotContain(actual, expected, Sets.newLinkedHashSet(6.0, (-8.0)), absValueComparisonStrategy).create()));
    }
}

