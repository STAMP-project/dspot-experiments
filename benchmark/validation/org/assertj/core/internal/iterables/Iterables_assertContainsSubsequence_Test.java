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
import org.assertj.core.error.ShouldContainSubsequence;
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
 * Tests for <code>{@link Iterables#assertContainsSubsequence(AssertionInfo, Collection, Object[])}</code>.
 *
 * @author Marcin Mikosik
 */
public class Iterables_assertContainsSubsequence_Test extends IterablesBaseTest {
    @Test
    public void should_throw_error_if_subsequence_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            Object[] nullArray = null;
            iterables.assertContainsSubsequence(someInfo(), actual, nullArray);
        }).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_pass_if_actual_and_given_values_are_empty() {
        actual.clear();
        iterables.assertContainsSubsequence(TestData.someInfo(), actual, Arrays.array());
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> iterables.assertContainsSubsequence(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> iterables.assertContainsSubsequence(someInfo(), null, array("Yoda"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_subsequence_is_bigger_than_actual() {
        AssertionInfo info = TestData.someInfo();
        Object[] subsequence = new Object[]{ "Luke", "Leia", "Obi-Wan", "Han", "C-3PO", "R2-D2", "Anakin" };
        try {
            iterables.assertContainsSubsequence(info, actual, subsequence);
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
            iterables.assertContainsSubsequence(info, actual, subsequence);
        } catch (AssertionError e) {
            verifyFailureThrownWhenSubsequenceNotFound(info, subsequence);
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_contains_first_elements_of_subsequence_but_not_whole_subsequence() {
        AssertionInfo info = TestData.someInfo();
        Object[] subsequence = new Object[]{ "Luke", "Leia", "Han" };
        try {
            iterables.assertContainsSubsequence(info, actual, subsequence);
        } catch (AssertionError e) {
            verifyFailureThrownWhenSubsequenceNotFound(info, subsequence);
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_contains_subsequence_without_elements_between() {
        iterables.assertContainsSubsequence(TestData.someInfo(), actual, Arrays.array("Luke", "Leia"));
    }

    @Test
    public void should_pass_if_actual_contains_subsequence_with_elements_between() {
        iterables.assertContainsSubsequence(TestData.someInfo(), actual, Arrays.array("Yoda", "Leia"));
    }

    @Test
    public void should_pass_if_actual_with_duplicate_elements_contains_subsequence() {
        actual = Lists.newArrayList("Yoda", "Luke", "Yoda", "Obi-Wan");
        iterables.assertContainsSubsequence(TestData.someInfo(), actual, Arrays.array("Yoda", "Obi-Wan"));
        iterables.assertContainsSubsequence(TestData.someInfo(), actual, Arrays.array("Luke", "Obi-Wan"));
        iterables.assertContainsSubsequence(TestData.someInfo(), actual, Arrays.array("Yoda", "Yoda"));
    }

    @Test
    public void should_pass_if_actual_and_subsequence_are_equal() {
        iterables.assertContainsSubsequence(TestData.someInfo(), actual, Arrays.array("Yoda", "Luke", "Leia", "Obi-Wan"));
    }

    @Test
    public void should_pass_if_actual_contains_both_partial_and_complete_subsequence() {
        actual = Lists.newArrayList("Yoda", "Luke", "Yoda", "Obi-Wan");
        iterables.assertContainsSubsequence(TestData.someInfo(), actual, Arrays.array("Yoda", "Obi-Wan"));
    }

    // ------------------------------------------------------------------------------------------------------------------
    // tests using a custom comparison strategy
    // ------------------------------------------------------------------------------------------------------------------
    @Test
    public void should_fail_if_actual_does_not_contain_whole_subsequence_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Object[] subsequence = new Object[]{ "Han", "C-3PO" };
        try {
            iterablesWithCaseInsensitiveComparisonStrategy.assertContainsSubsequence(info, actual, subsequence);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainSubsequence.shouldContainSubsequence(actual, subsequence, comparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_contains_first_elements_of_subsequence_but_not_whole_subsequence_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Object[] subsequence = new Object[]{ "Luke", "Leia", "Han" };
        try {
            iterablesWithCaseInsensitiveComparisonStrategy.assertContainsSubsequence(info, actual, subsequence);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainSubsequence.shouldContainSubsequence(actual, subsequence, comparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_contains_subsequence_according_to_custom_comparison_strategy() {
        iterablesWithCaseInsensitiveComparisonStrategy.assertContainsSubsequence(TestData.someInfo(), actual, Arrays.array("yODa", "leia"));
    }

    @Test
    public void should_pass_if_actual_and_subsequence_are_equal_according_to_custom_comparison_strategy() {
        iterablesWithCaseInsensitiveComparisonStrategy.assertContainsSubsequence(TestData.someInfo(), actual, Arrays.array("YODA", "luke", "lEIA", "Obi-wan"));
    }
}

