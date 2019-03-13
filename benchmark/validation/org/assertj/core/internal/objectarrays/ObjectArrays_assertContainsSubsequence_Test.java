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
import org.assertj.core.error.ShouldContainSubsequence;
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
 * Tests for <code>{@link ObjectArrays#assertContainsSubsequence(AssertionInfo, Object[], Object[])}</code>.
 *
 * @author Marcin Mikosik
 */
public class ObjectArrays_assertContainsSubsequence_Test extends ObjectArraysBaseTest {
    @Test
    public void should_pass_if_actual_contains_sequence() {
        arrays.assertContainsSubsequence(TestData.someInfo(), actual, Arrays.array("Yoda", "Leia"));
    }

    @Test
    public void should_pass_if_actual_and_sequence_are_equal() {
        arrays.assertContainsSubsequence(TestData.someInfo(), actual, Arrays.array("Yoda", "Luke", "Leia", "Obi-Wan"));
    }

    @Test
    public void should_pass_if_actual_contains_full_sequence_even_if_partial_sequence_is_found_before() {
        actual = Arrays.array("Yoda", "Luke", "Leia", "Yoda", "Luke", "Obi-Wan");
        // note that actual starts with {"Yoda", "Luke"} a partial sequence of {"Yoda", "Luke", "Obi-Wan"}
        arrays.assertContainsSubsequence(TestData.someInfo(), actual, Arrays.array("Yoda", "Luke", "Obi-Wan"));
    }

    @Test
    public void should_pass_if_actual_and_given_values_are_empty() {
        actual = new String[0];
        arrays.assertContainsSubsequence(TestData.someInfo(), actual, ObjectArrays.emptyArray());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsSubsequence(someInfo(), null, array("Yoda"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_throw_error_if_sequence_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertContainsSubsequence(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsSubsequence(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_fail_if_subsequence_is_bigger_than_actual() {
        AssertionInfo info = TestData.someInfo();
        Object[] subsequence = new Object[]{ "Luke", "Leia", "Obi-Wan", "Han", "C-3PO", "R2-D2", "Anakin" };
        try {
            arrays.assertContainsSubsequence(info, actual, subsequence);
        } catch (AssertionError e) {
            verifyFailureThrownWhenSubsequenceNotFound(info, subsequence);
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_does_not_contain_whole_subsequence() {
        AssertionInfo info = TestData.someInfo();
        Object[] subsequence = new Object[]{ "Han", "C-3PO" };
        try {
            arrays.assertContainsSubsequence(info, actual, subsequence);
        } catch (AssertionError e) {
            verifyFailureThrownWhenSubsequenceNotFound(info, subsequence);
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_contains_first_elements_of_subsequence() {
        AssertionInfo info = TestData.someInfo();
        Object[] subsequence = new Object[]{ "Leia", "Obi-Wan", "Han" };
        try {
            arrays.assertContainsSubsequence(info, actual, subsequence);
        } catch (AssertionError e) {
            verifyFailureThrownWhenSubsequenceNotFound(info, subsequence);
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsSubsequence(someInfo(), null, array("YOda"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_throw_error_if_subsequence_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsSubsequence(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsSubsequence(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_fail_if_sequence_is_bigger_than_actual_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Object[] subsequence = new Object[]{ "LUKE", "LeiA", "Obi-Wan", "Han", "C-3PO", "R2-D2", "Anakin" };
        try {
            arraysWithCustomComparisonStrategy.assertContainsSubsequence(info, actual, subsequence);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainSubsequence.shouldContainSubsequence(actual, subsequence, caseInsensitiveStringComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_does_not_contain_whole_subsequence_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Object[] sequence = new Object[]{ "Han", "C-3PO" };
        try {
            arraysWithCustomComparisonStrategy.assertContainsSubsequence(info, actual, sequence);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainSubsequence.shouldContainSubsequence(actual, sequence, caseInsensitiveStringComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_contains_first_elements_of_subsequence_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Object[] sequence = new Object[]{ "LeiA", "Obi-Wan", "Han" };
        try {
            arraysWithCustomComparisonStrategy.assertContainsSubsequence(info, actual, sequence);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainSubsequence.shouldContainSubsequence(actual, sequence, caseInsensitiveStringComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_contains_sequence_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContainsSubsequence(TestData.someInfo(), actual, Arrays.array("LUKE", "LeiA"));
    }

    @Test
    public void should_pass_if_actual_and_sequence_are_equal_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContainsSubsequence(TestData.someInfo(), actual, Arrays.array("YOda", "LUKE", "LeiA", "Obi-WAn"));
    }
}

