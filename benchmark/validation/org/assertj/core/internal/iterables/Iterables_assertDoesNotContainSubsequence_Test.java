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
import org.assertj.core.api.Assertions;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.IterablesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link Iterables#assertDoesNotContainSubsequence(AssertionInfo, Iterable, Object[])} </code>.
 *
 * @author Marcin Mikosik
 */
public class Iterables_assertDoesNotContainSubsequence_Test extends IterablesBaseTest {
    @Test
    public void should_throw_error_if_subsequence_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> iterables.assertDoesNotContainSubsequence(someInfo(), actual, null)).withMessage(ErrorMessages.nullSubsequence());
    }

    @Test
    public void should_throw_error_if_subsequence_is_empty() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> iterables.assertDoesNotContainSubsequence(someInfo(), actual, emptyArray())).withMessage(ErrorMessages.emptySubsequence());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> iterables.assertDoesNotContainSubsequence(someInfo(), null, array("Yoda"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_subsequence_is_bigger_than_actual() {
        Object[] subsequence = new Object[]{ "Luke", "Leia", "Obi-Wan", "Han", "C-3PO", "R2-D2", "Anakin" };
        iterables.assertDoesNotContainSubsequence(TestData.someInfo(), actual, subsequence);
    }

    @Test
    public void should_pass_if_actual_does_not_contain_whole_subsequence() {
        Object[] subsequence = new Object[]{ "Han", "C-3PO" };
        iterables.assertDoesNotContainSubsequence(TestData.someInfo(), actual, subsequence);
    }

    @Test
    public void should_pass_if_actual_contains_first_elements_of_subsequence_but_not_whole_subsequence() {
        Object[] subsequence = new Object[]{ "Luke", "Leia", "Han" };
        iterables.assertDoesNotContainSubsequence(TestData.someInfo(), actual, subsequence);
    }

    @Test
    public void should_fail_if_actual_contains_subsequence_without_elements_between() {
        Object[] subsequence = Arrays.array("Luke", "Leia");
        expectFailure(iterables, actual, subsequence, 1);
    }

    @Test
    public void should_fail_if_actual_contains_subsequence_with_elements_between() {
        Object[] subsequence = Arrays.array("Yoda", "Leia");
        expectFailure(iterables, actual, subsequence, 0);
    }

    @Test
    public void should_fail_if_actual_and_subsequence_are_equal() {
        Object[] subsequence = Arrays.array("Yoda", "Luke", "Leia", "Obi-Wan");
        expectFailure(iterables, actual, subsequence, 0);
    }

    @Test
    public void should_fail_if_actual_contains_both_partial_and_complete_subsequence() {
        actual = Lists.newArrayList("Yoda", "Luke", "Yoda", "Obi-Wan");
        Object[] subsequence = Arrays.array("Yoda", "Obi-Wan");
        expectFailure(iterables, actual, subsequence, 0);
    }

    // ------------------------------------------------------------------------------------------------------------------
    // tests using a custom comparison strategy
    // ------------------------------------------------------------------------------------------------------------------
    @Test
    public void should_pass_if_actual_does_not_contain_whole_subsequence_according_to_custom_comparison_strategy() {
        Object[] subsequence = new Object[]{ "Han", "C-3PO" };
        iterables.assertDoesNotContainSubsequence(TestData.someInfo(), actual, subsequence);
    }

    @Test
    public void should_pass_if_actual_contains_first_elements_of_subsequence_but_not_whole_subsequence_according_to_custom_comparison_strategy() {
        Object[] subsequence = new Object[]{ "Luke", "LEIA", "Han" };
        iterablesWithCaseInsensitiveComparisonStrategy.assertDoesNotContainSubsequence(TestData.someInfo(), actual, subsequence);
    }

    @Test
    public void should_fail_if_actual_contains_subsequence_according_to_custom_comparison_strategy() {
        Object[] subsequence = Arrays.array("yODa", "leia");
        expectFailure(iterablesWithCaseInsensitiveComparisonStrategy, actual, subsequence, 0);
    }

    @Test
    public void should_fail_if_actual_and_subsequence_are_equal_according_to_custom_comparison_strategy() {
        Object[] subsequence = Arrays.array("YODA", "luke", "lEIA", "Obi-wan");
        expectFailure(iterablesWithCaseInsensitiveComparisonStrategy, actual, subsequence, 0);
    }
}

