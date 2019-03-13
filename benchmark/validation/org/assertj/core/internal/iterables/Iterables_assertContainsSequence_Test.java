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
import org.assertj.core.error.ShouldContainSequence;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.IterablesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Iterables#assertContainsSequence(AssertionInfo, Collection, Object[])}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class Iterables_assertContainsSequence_Test extends IterablesBaseTest {
    @Test
    public void should_throw_error_if_sequence_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            Object[] nullArray = null;
            iterables.assertContainsSequence(someInfo(), actual, nullArray);
        }).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_pass_if_actual_and_given_values_are_empty() {
        actual.clear();
        iterables.assertContainsSequence(TestData.someInfo(), actual, Arrays.array());
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> iterables.assertContainsSequence(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> iterables.assertContainsSequence(someInfo(), null, array("Yoda"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_sequence_is_bigger_than_actual() {
        AssertionInfo info = TestData.someInfo();
        Object[] sequence = new Object[]{ "Luke", "Leia", "Obi-Wan", "Han", "C-3PO", "R2-D2", "Anakin" };
        try {
            iterables.assertContainsSequence(info, actual, sequence);
        } catch (AssertionError e) {
            verifyFailureThrownWhenSequenceNotFound(info, sequence);
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_does_not_contain_whole_sequence() {
        AssertionInfo info = TestData.someInfo();
        Object[] sequence = new Object[]{ "Han", "C-3PO" };
        try {
            iterables.assertContainsSequence(info, actual, sequence);
        } catch (AssertionError e) {
            verifyFailureThrownWhenSequenceNotFound(info, sequence);
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_contains_first_elements_of_sequence_but_not_whole_sequence() {
        AssertionInfo info = TestData.someInfo();
        Object[] sequence = new Object[]{ "Luke", "Leia", "Han" };
        try {
            iterables.assertContainsSequence(info, actual, sequence);
        } catch (AssertionError e) {
            verifyFailureThrownWhenSequenceNotFound(info, sequence);
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_contains_sequence() {
        iterables.assertContainsSequence(TestData.someInfo(), actual, Arrays.array("Luke", "Leia"));
    }

    @Test
    public void should_pass_if_actual_and_sequence_are_equal() {
        iterables.assertContainsSequence(TestData.someInfo(), actual, Arrays.array("Yoda", "Luke", "Leia", "Obi-Wan"));
    }

    @Test
    public void should_pass_if_actual_contains_both_partial_and_complete_sequence() {
        actual = Lists.newArrayList("Yoda", "Luke", "Yoda", "Obi-Wan");
        iterables.assertContainsSequence(TestData.someInfo(), actual, Arrays.array("Yoda", "Obi-Wan"));
    }

    @Test
    public void should_pass_if_actual_contains_sequence_that_specifies_multiple_times_the_same_value_bug_544() {
        actual = Lists.newArrayList("a", "-", "b", "-", "c");
        iterables.assertContainsSequence(TestData.someInfo(), actual, Arrays.array("a", "-", "b", "-", "c"));
    }

    // ------------------------------------------------------------------------------------------------------------------
    // tests using a custom comparison strategy
    // ------------------------------------------------------------------------------------------------------------------
    @Test
    public void should_fail_if_actual_does_not_contain_whole_sequence_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Object[] sequence = new Object[]{ "Han", "C-3PO" };
        try {
            iterablesWithCaseInsensitiveComparisonStrategy.assertContainsSequence(info, actual, sequence);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainSequence.shouldContainSequence(actual, sequence, comparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_contains_first_elements_of_sequence_but_not_whole_sequence_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Object[] sequence = new Object[]{ "Luke", "Leia", "Han" };
        try {
            iterablesWithCaseInsensitiveComparisonStrategy.assertContainsSequence(info, actual, sequence);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainSequence.shouldContainSequence(actual, sequence, comparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_contains_sequence_according_to_custom_comparison_strategy() {
        iterablesWithCaseInsensitiveComparisonStrategy.assertContainsSequence(TestData.someInfo(), actual, Arrays.array("LUKe", "leia"));
    }

    @Test
    public void should_pass_if_actual_and_sequence_are_equal_according_to_custom_comparison_strategy() {
        iterablesWithCaseInsensitiveComparisonStrategy.assertContainsSequence(TestData.someInfo(), actual, Arrays.array("YODA", "luke", "lEIA", "Obi-wan"));
    }
}

