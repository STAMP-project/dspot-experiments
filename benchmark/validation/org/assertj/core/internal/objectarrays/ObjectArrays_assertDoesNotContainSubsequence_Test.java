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
package org.assertj.core.internal.objectarrays;


import org.assertj.core.api.Assertions;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.ObjectArraysBaseTest;
import org.assertj.core.test.ObjectArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link ObjectArrays#assertDoesNotContainSubsequence(AssertionInfo, Object[], Object[])} </code>.
 *
 * @author Marcin Mikosik
 */
public class ObjectArrays_assertDoesNotContainSubsequence_Test extends ObjectArraysBaseTest {
    @Test
    public void should_fail_if_actual_contains_sequence() {
        Object[] subsequence = Arrays.array("Yoda", "Leia");
        expectFailure(arrays, actual, subsequence, 0);
    }

    @Test
    public void should_fail_if_actual_and_sequence_are_equal() {
        Object[] subsequence = Arrays.array("Yoda", "Luke", "Leia", "Obi-Wan");
        expectFailure(arrays, actual, subsequence, 0);
    }

    @Test
    public void should_fail_if_actual_contains_full_sequence_even_if_partial_sequence_is_found_before() {
        // note that actual starts with {"Yoda", "Luke"} a partial sequence of {"Yoda", "Luke", "Obi-Wan"}
        actual = Arrays.array("Yoda", "Luke", "Leia", "Yoda", "Luke", "Obi-Wan");
        Object[] subsequence = Arrays.array("Yoda", "Luke", "Obi-Wan");
        expectFailure(arrays, actual, subsequence, 0);
    }

    @Test
    public void should_pass_if_actual_and_given_values_are_empty() {
        actual = new String[0];
        arrays.assertDoesNotContainSubsequence(TestData.someInfo(), actual, ObjectArrays.emptyArray());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertDoesNotContainSubsequence(someInfo(), null, array("Yoda"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_throw_error_if_sequence_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertDoesNotContainSubsequence(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertDoesNotContainSubsequence(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_pass_if_subsequence_is_bigger_than_actual() {
        Object[] subsequence = new Object[]{ "Luke", "Leia", "Obi-Wan", "Han", "C-3PO", "R2-D2", "Anakin" };
        arrays.assertDoesNotContainSubsequence(TestData.someInfo(), actual, subsequence);
    }

    @Test
    public void should_pass_if_actual_does_not_contain_whole_subsequence() {
        Object[] subsequence = new Object[]{ "Han", "C-3PO" };
        arrays.assertDoesNotContainSubsequence(TestData.someInfo(), actual, subsequence);
    }

    @Test
    public void should_pass_if_actual_contains_first_elements_of_subsequence() {
        Object[] subsequence = new Object[]{ "Leia", "Obi-Wan", "Han" };
        arrays.assertDoesNotContainSubsequence(TestData.someInfo(), actual, subsequence);
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertDoesNotContainSubsequence(someInfo(), null, array("YOda"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_throw_error_if_subsequence_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arraysWithCustomComparisonStrategy.assertDoesNotContainSubsequence(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertDoesNotContainSubsequence(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_pass_if_sequence_is_bigger_than_actual_according_to_custom_comparison_strategy() {
        Object[] subsequence = new Object[]{ "LUKE", "LeiA", "Obi-Wan", "Han", "C-3PO", "R2-D2", "Anakin" };
        arraysWithCustomComparisonStrategy.assertDoesNotContainSubsequence(TestData.someInfo(), actual, subsequence);
    }

    @Test
    public void should_pass_if_actual_does_not_contain_whole_subsequence_according_to_custom_comparison_strategy() {
        Object[] sequence = new Object[]{ "Han", "C-3PO" };
        arraysWithCustomComparisonStrategy.assertDoesNotContainSubsequence(TestData.someInfo(), actual, sequence);
    }

    @Test
    public void should_pass_if_actual_contains_first_elements_of_subsequence_according_to_custom_comparison_strategy() {
        Object[] sequence = new Object[]{ "LeiA", "Obi-Wan", "Han" };
        arraysWithCustomComparisonStrategy.assertDoesNotContainSubsequence(TestData.someInfo(), actual, sequence);
    }

    @Test
    public void should_fail_if_actual_contains_sequence_according_to_custom_comparison_strategy() {
        Object[] subsequence = Arrays.array("LUKE", "LeiA");
        expectFailure(arraysWithCustomComparisonStrategy, actual, subsequence, 1);
    }

    @Test
    public void should_fail_if_actual_and_sequence_are_equal_according_to_custom_comparison_strategy() {
        Object[] subsequence = Arrays.array("YOda", "LUKE", "LeiA", "Obi-WAn");
        expectFailure(arraysWithCustomComparisonStrategy, actual, subsequence, 0);
    }
}

