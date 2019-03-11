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


import java.util.List;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldNotContainSequence;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.IterablesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Iterables#assertDoesNotContainSequence(AssertionInfo, Iterable, Object[])}</code>.
 *
 * @author Chris Arnott
 */
public class Iterables_assertDoesNotContainSequence_Test extends IterablesBaseTest {
    @Test
    public void should_throw_error_if_sequence_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            Object[] nullArray = null;
            iterables.assertDoesNotContainSequence(someInfo(), actual, nullArray);
        }).withMessage(ErrorMessages.nullSequence());
    }

    @Test
    public void should_throw_error_if_sequence_is_empty() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> iterables.assertDoesNotContainSequence(someInfo(), actual, emptyArray())).withMessage(ErrorMessages.emptySequence());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> iterables.assertDoesNotContainSequence(someInfo(), null, array("Yoda"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_sequence_is_bigger_than_actual() {
        AssertionInfo info = TestData.someInfo();
        Object[] sequence = new Object[]{ "Luke", "Leia", "Obi-Wan", "Han", "C-3PO", "R2-D2", "Anakin" };
        iterables.assertDoesNotContainSequence(info, actual, sequence);
    }

    @Test
    public void should_pass_if_actual_does_not_contain_the_whole_sequence() {
        AssertionInfo info = TestData.someInfo();
        Object[] sequence = new Object[]{ "Han", "C-3PO" };
        iterables.assertDoesNotContainSequence(info, actual, sequence);
    }

    @Test
    public void should_pass_if_actual_contains_the_first_elements_of_sequence_but_not_the_whole_sequence() {
        AssertionInfo info = TestData.someInfo();
        Object[] sequence = new Object[]{ "Luke", "Leia", "Han" };
        iterables.assertDoesNotContainSequence(info, actual, sequence);
    }

    @Test
    public void should_fail_if_actual_contains_sequence() {
        AssertionInfo info = TestData.someInfo();
        Object[] sequence = new Object[]{ "Luke", "Leia" };
        try {
            iterables.assertDoesNotContainSequence(info, actual, sequence);
        } catch (AssertionError e) {
            verifyFailureThrownWhenSequenceNotFound(info, sequence, 1);
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_and_sequence_are_equal() {
        AssertionInfo info = TestData.someInfo();
        Object[] sequence = new Object[]{ "Yoda", "Luke", "Leia", "Obi-Wan" };
        try {
            iterables.assertDoesNotContainSequence(info, actual, sequence);
        } catch (AssertionError e) {
            verifyFailureThrownWhenSequenceNotFound(info, sequence, 0);
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_contains_both_partial_and_complete_sequence() {
        AssertionInfo info = TestData.someInfo();
        actual = Lists.newArrayList("Yoda", "Luke", "Yoda", "Obi-Wan");
        Object[] sequence = new Object[]{ "Yoda", "Obi-Wan" };
        try {
            iterables.assertDoesNotContainSequence(info, actual, sequence);
        } catch (AssertionError e) {
            verifyFailureThrownWhenSequenceNotFound(info, sequence, 2);
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_contains_sequence_that_specifies_multiple_times_the_same_value() {
        AssertionInfo info = TestData.someInfo();
        actual = Lists.newArrayList("a", "-", "b", "-", "c");
        Object[] sequence = new Object[]{ "a", "-", "b", "-", "c" };
        try {
            iterables.assertDoesNotContainSequence(info, actual, sequence);
        } catch (AssertionError e) {
            verifyFailureThrownWhenSequenceNotFound(info, sequence, 0);
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    // ------------------------------------------------------------------------------------------------------------------
    // tests using a custom comparison strategy
    // ------------------------------------------------------------------------------------------------------------------
    @Test
    public void should_pass_if_actual_does_not_contain_whole_sequence_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Object[] sequence = new Object[]{ "Han", "C-3PO" };
        iterablesWithCaseInsensitiveComparisonStrategy.assertDoesNotContainSequence(info, actual, sequence);
    }

    @Test
    public void should_pass_if_actual_contains_first_elements_of_sequence_but_not_whole_sequence_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Object[] sequence = new Object[]{ "Luke", "Leia", "Han" };
        iterablesWithCaseInsensitiveComparisonStrategy.assertDoesNotContainSequence(info, actual, sequence);
    }

    @Test
    public void should_fail_if_actual_contains_sequence_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Object[] sequence = new Object[]{ "LUKe", "leia" };
        try {
            iterablesWithCaseInsensitiveComparisonStrategy.assertDoesNotContainSequence(info, actual, sequence);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotContainSequence.shouldNotContainSequence(actual, sequence, 1, comparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_and_sequence_are_equal_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Object[] sequence = new Object[]{ "YODA", "luke", "lEIA", "Obi-wan" };
        try {
            iterablesWithCaseInsensitiveComparisonStrategy.assertDoesNotContainSequence(info, actual, sequence);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotContainSequence.shouldNotContainSequence(actual, sequence, 0, comparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

