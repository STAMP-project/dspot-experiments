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
package org.assertj.core.internal.chararrays;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldContainsOnlyOnce;
import org.assertj.core.internal.CharArraysBaseTest;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.test.CharArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link CharArrays#assertContainsOnlyOnce(AssertionInfo, char[], char[])}</code>.
 *
 * @author William Delanoue
 */
public class CharArrays_assertContainsOnlyOnce_Test extends CharArraysBaseTest {
    @Test
    public void should_pass_if_actual_contains_given_values_only() {
        arrays.assertContainsOnlyOnce(TestData.someInfo(), actual, CharArrays.arrayOf('a', 'b', 'c'));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_in_different_order() {
        arrays.assertContainsOnlyOnce(TestData.someInfo(), actual, CharArrays.arrayOf('c', 'b', 'a'));
    }

    @Test
    public void should_fail_if_actual_contains_given_values_only_more_than_once() {
        AssertionInfo info = TestData.someInfo();
        actual = CharArrays.arrayOf('a', 'b', 'b', 'a', 'c', 'd');
        char[] expected = new char[]{ 'a', 'b', 'e' };
        try {
            arrays.assertContainsOnlyOnce(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainsOnlyOnce.shouldContainsOnlyOnce(actual, expected, Sets.newLinkedHashSet('e'), Sets.newLinkedHashSet('a', 'b')));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_even_if_duplicated() {
        arrays.assertContainsOnlyOnce(TestData.someInfo(), actual, CharArrays.arrayOf('a', 'b', 'c', 'a', 'b', 'c'));
    }

    @Test
    public void should_pass_if_actual_and_given_values_are_empty() {
        actual = CharArrays.emptyArray();
        arrays.assertContainsOnlyOnce(TestData.someInfo(), actual, CharArrays.emptyArray());
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsOnlyOnce(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertContainsOnlyOnce(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsOnlyOnce(someInfo(), null, arrayOf('a'))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_given_values_only() {
        AssertionInfo info = TestData.someInfo();
        char[] expected = new char[]{ 'a', 'b', 'd' };
        try {
            arrays.assertContainsOnlyOnce(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainsOnlyOnce.shouldContainsOnlyOnce(actual, expected, Sets.newLinkedHashSet('d'), Sets.newLinkedHashSet()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContainsOnlyOnce(TestData.someInfo(), actual, CharArrays.arrayOf('A', 'b', 'C'));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_in_different_order_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContainsOnlyOnce(TestData.someInfo(), actual, CharArrays.arrayOf('C', 'B', 'A'));
    }

    @Test
    public void should_fail_if_actual_contains_given_values_only_more_than_once_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        actual = CharArrays.arrayOf('a', 'B', 'b', 'A', 'c', 'd');
        char[] expected = new char[]{ 'a', 'B', 'e' };
        try {
            arraysWithCustomComparisonStrategy.assertContainsOnlyOnce(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainsOnlyOnce.shouldContainsOnlyOnce(actual, expected, Sets.newLinkedHashSet('e'), Sets.newLinkedHashSet('a', 'B'), caseInsensitiveComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_even_if_duplicated_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContainsOnlyOnce(TestData.someInfo(), actual, CharArrays.arrayOf('a', 'b', 'c', 'A', 'b', 'C'));
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsOnlyOnce(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsOnlyOnce(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsOnlyOnce(someInfo(), null, arrayOf(((char) (-8))))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_given_values_only_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        char[] expected = new char[]{ 'A', 'b', 'D' };
        try {
            arraysWithCustomComparisonStrategy.assertContainsOnlyOnce(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainsOnlyOnce.shouldContainsOnlyOnce(actual, expected, Sets.newLinkedHashSet('D'), Sets.newLinkedHashSet(), caseInsensitiveComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

