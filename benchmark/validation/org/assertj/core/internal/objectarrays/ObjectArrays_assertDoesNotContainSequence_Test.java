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


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldNotContainSequence;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.ObjectArraysBaseTest;
import org.assertj.core.test.ObjectArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link ObjectArrays#assertDoesNotContainSequence(AssertionInfo, Object[], Object[])}</code>.
 *
 * @author Chris Arnott
 */
public class ObjectArrays_assertDoesNotContainSequence_Test extends ObjectArraysBaseTest {
    @Test
    public void should_fail_if_actual_contains_sequence() {
        AssertionInfo info = TestData.someInfo();
        Object[] sequence = Arrays.array("Luke", "Leia");
        try {
            arrays.assertDoesNotContainSequence(info, actual, sequence);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotContainSequence.shouldNotContainSequence(actual, sequence, 1));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_and_sequence_are_equal() {
        AssertionInfo info = TestData.someInfo();
        Object[] sequence = Arrays.array("Yoda", "Luke", "Leia", "Obi-Wan");
        try {
            arrays.assertDoesNotContainSequence(info, actual, sequence);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotContainSequence.shouldNotContainSequence(actual, sequence, 0));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_contains_full_sequence_even_if_partial_sequence_is_found_before() {
        AssertionInfo info = TestData.someInfo();
        actual = Arrays.array("Yoda", "Luke", "Leia", "Yoda", "Luke", "Obi-Wan");
        // note that actual starts with {"Yoda", "Luke"} a partial sequence of {"Yoda", "Luke", "Obi-Wan"}
        Object[] sequence = Arrays.array("Yoda", "Luke", "Obi-Wan");
        try {
            arrays.assertDoesNotContainSequence(info, actual, sequence);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotContainSequence.shouldNotContainSequence(actual, sequence, 3));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_and_given_values_are_empty() {
        actual = new String[0];
        arrays.assertDoesNotContainSequence(TestData.someInfo(), actual, ObjectArrays.emptyArray());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertDoesNotContainSequence(someInfo(), null, array("Yoda"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_throw_error_if_sequence_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertDoesNotContainSequence(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertDoesNotContainSequence(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_pass_if_sequence_is_bigger_than_actual() {
        arrays.assertDoesNotContainSequence(TestData.someInfo(), actual, Arrays.array("Luke", "Leia", "Obi-Wan", "Han", "C-3PO", "R2-D2", "Anakin"));
    }

    @Test
    public void should_pass_if_actual_does_not_contain_whole_sequence() {
        arrays.assertDoesNotContainSequence(TestData.someInfo(), actual, Arrays.array("Han", "C-3PO"));
    }

    @Test
    public void should_pass_if_actual_contains_first_elements_of_sequence() {
        arrays.assertDoesNotContainSequence(TestData.someInfo(), actual, Arrays.array("Leia", "Obi-Wan", "Han"));
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertDoesNotContainSequence(someInfo(), null, array("YOda"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_throw_error_if_sequence_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arraysWithCustomComparisonStrategy.assertDoesNotContainSequence(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertDoesNotContainSequence(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_pass_if_sequence_is_bigger_than_actual_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertDoesNotContainSequence(TestData.someInfo(), actual, Arrays.array("LUKE", "LeiA", "Obi-Wan", "Han", "C-3PO", "R2-D2", "Anakin"));
    }

    @Test
    public void should_pass_if_actual_does_not_contain_whole_sequence_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertDoesNotContainSequence(TestData.someInfo(), actual, Arrays.array("Han", "C-3PO"));
    }

    @Test
    public void should_pass_if_actual_contains_first_elements_of_sequence_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertDoesNotContainSequence(TestData.someInfo(), actual, Arrays.array("LeiA", "Obi-Wan", "Han"));
    }

    @Test
    public void should_fail_if_actual_contains_sequence_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Object[] sequence = Arrays.array("LUKE", "LeiA");
        try {
            arraysWithCustomComparisonStrategy.assertDoesNotContainSequence(info, actual, sequence);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotContainSequence.shouldNotContainSequence(actual, sequence, 1, caseInsensitiveStringComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_and_sequence_are_equal_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Object[] sequence = Arrays.array("YOda", "LUKE", "LeiA", "Obi-WAn");
        try {
            arraysWithCustomComparisonStrategy.assertDoesNotContainSequence(info, actual, sequence);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotContainSequence.shouldNotContainSequence(actual, sequence, 0, caseInsensitiveStringComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

