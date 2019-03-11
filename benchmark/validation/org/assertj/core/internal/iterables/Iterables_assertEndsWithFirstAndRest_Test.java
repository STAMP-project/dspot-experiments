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
package org.assertj.core.internal.iterables;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldEndWith;
import org.assertj.core.internal.IterablesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Iterables#assertEndsWith(AssertionInfo, Object[], Object, Object[])}</code>.
 */
public class Iterables_assertEndsWithFirstAndRest_Test extends IterablesBaseTest {
    @Test
    public void should_throw_error_if_sequence_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> iterables.assertEndsWith(someInfo(), actual, "Luke", null));
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> iterables.assertEndsWith(someInfo(), null, "Luke", array("Yoda"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_sequence_is_bigger_than_actual() {
        AssertionInfo info = TestData.someInfo();
        Object[] sequence = new Object[]{ "Yoda", "Luke", "Leia", "Obi-Wan", "Han", "C-3PO", "R2-D2", "Anakin" };
        try {
            iterables.assertEndsWith(info, actual, "Luke", sequence);
        } catch (AssertionError e) {
            verifyFailureThrownWhenSequenceNotFound(info, "Luke", sequence);
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_does_not_end_with_sequence() {
        AssertionInfo info = TestData.someInfo();
        Object[] sequence = new Object[]{ "Han", "C-3PO" };
        try {
            iterables.assertEndsWith(info, actual, "Obi-Wan", sequence);
        } catch (AssertionError e) {
            verifyFailureThrownWhenSequenceNotFound(info, "Obi-Wan", sequence);
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_ends_with_first_elements_of_sequence_only() {
        AssertionInfo info = TestData.someInfo();
        Object[] sequence = new Object[]{ "Leia", "Obi-Wan", "Han" };
        try {
            iterables.assertEndsWith(info, actual, "Luke", sequence);
        } catch (AssertionError e) {
            verifyFailureThrownWhenSequenceNotFound(info, "Luke", sequence);
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_ends_with_first_then_sequence() {
        iterables.assertEndsWith(TestData.someInfo(), actual, "Luke", Arrays.array("Leia", "Obi-Wan"));
    }

    @Test
    public void should_pass_if_actual_and_first_then_sequence_are_equal() {
        iterables.assertEndsWith(TestData.someInfo(), actual, "Yoda", Arrays.array("Luke", "Leia", "Obi-Wan"));
    }

    @Test
    public void should_fail_if_sequence_is_bigger_than_actual_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Object[] sequence = new Object[]{ "LUKE", "Leia", "Obi-Wan", "Han", "C-3PO", "R2-D2", "Anakin" };
        try {
            iterablesWithCaseInsensitiveComparisonStrategy.assertEndsWith(info, actual, "Yoda", sequence);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldEndWith.shouldEndWith(actual, Arrays.prepend("Yoda", sequence), comparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_does_not_end_with_sequence_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Object[] sequence = new Object[]{ "Han", "C-3PO" };
        try {
            iterablesWithCaseInsensitiveComparisonStrategy.assertEndsWith(info, actual, "Yoda", sequence);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldEndWith.shouldEndWith(actual, Arrays.prepend("Yoda", sequence), comparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_ends_with_first_elements_of_sequence_only_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Object[] sequence = new Object[]{ "Leia", "Obi-Wan", "Han" };
        try {
            iterablesWithCaseInsensitiveComparisonStrategy.assertEndsWith(info, actual, "Luke", sequence);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldEndWith.shouldEndWith(actual, Arrays.prepend("Luke", sequence), comparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_ends_with_sequence_according_to_custom_comparison_strategy() {
        iterablesWithCaseInsensitiveComparisonStrategy.assertEndsWith(TestData.someInfo(), actual, "LUKE", Arrays.array("Leia", "Obi-Wan"));
    }

    @Test
    public void should_pass_if_actual_and_sequence_are_equal_according_to_custom_comparison_strategy() {
        iterablesWithCaseInsensitiveComparisonStrategy.assertEndsWith(TestData.someInfo(), actual, "Yoda", Arrays.array("LUKE", "Leia", "Obi-Wan"));
    }
}

