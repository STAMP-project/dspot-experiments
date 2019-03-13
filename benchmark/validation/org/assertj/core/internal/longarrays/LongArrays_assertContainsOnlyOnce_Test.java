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
package org.assertj.core.internal.longarrays;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldContainsOnlyOnce;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.LongArraysBaseTest;
import org.assertj.core.test.LongArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link LongArrays#assertContainsOnlyOnce(AssertionInfo, long[], long[])}</code>.
 *
 * @author William Delanoue
 */
public class LongArrays_assertContainsOnlyOnce_Test extends LongArraysBaseTest {
    @Test
    public void should_pass_if_actual_contains_given_values_only() {
        arrays.assertContainsOnlyOnce(TestData.someInfo(), actual, LongArrays.arrayOf(6, 8, 10));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_in_different_order() {
        arrays.assertContainsOnlyOnce(TestData.someInfo(), actual, LongArrays.arrayOf(10, 8, 6));
    }

    @Test
    public void should_fail_if_actual_contains_given_values_only_more_than_once() {
        AssertionInfo info = TestData.someInfo();
        actual = LongArrays.arrayOf(6, (-8), 10, (-6), (-8), 10, (-8), 6);
        long[] expected = new long[]{ 6, -8, 20 };
        try {
            arrays.assertContainsOnlyOnce(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainsOnlyOnce.shouldContainsOnlyOnce(actual, expected, Sets.newLinkedHashSet(20L), Sets.newLinkedHashSet(6L, (-8L))));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_even_if_duplicated() {
        arrays.assertContainsOnlyOnce(TestData.someInfo(), actual, LongArrays.arrayOf(6, 8, 10, 6, 8, 10));
    }

    @Test
    public void should_pass_if_actual_and_given_values_are_empty() {
        actual = LongArrays.emptyArray();
        arrays.assertContainsOnlyOnce(TestData.someInfo(), actual, LongArrays.emptyArray());
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
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsOnlyOnce(someInfo(), null, arrayOf(8))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_given_values_only() {
        AssertionInfo info = TestData.someInfo();
        long[] expected = new long[]{ 6, 8, 20 };
        try {
            arrays.assertContainsOnlyOnce(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainsOnlyOnce.shouldContainsOnlyOnce(actual, expected, Sets.newLinkedHashSet(((long) (20))), Sets.newLinkedHashSet()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContainsOnlyOnce(TestData.someInfo(), actual, LongArrays.arrayOf(6, (-8), 10));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_in_different_order_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContainsOnlyOnce(TestData.someInfo(), actual, LongArrays.arrayOf(10, (-8), 6));
    }

    @Test
    public void should_fail_if_actual_contains_given_values_only_more_than_once_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        actual = LongArrays.arrayOf(6, (-8), 10, (-6), (-8), 10, (-8));
        long[] expected = new long[]{ 6, -8, 20 };
        try {
            arraysWithCustomComparisonStrategy.assertContainsOnlyOnce(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainsOnlyOnce.shouldContainsOnlyOnce(actual, expected, Sets.newLinkedHashSet(((long) (20))), Sets.newLinkedHashSet(((long) (6)), ((long) (-8))), absValueComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_even_if_duplicated_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContainsOnlyOnce(TestData.someInfo(), actual, LongArrays.arrayOf(6, 8, 10, 6, (-8), 10));
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
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsOnlyOnce(someInfo(), null, arrayOf((-8)))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_given_values_only_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        long[] expected = new long[]{ 6, -8, 20 };
        try {
            arraysWithCustomComparisonStrategy.assertContainsOnlyOnce(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainsOnlyOnce.shouldContainsOnlyOnce(actual, expected, Sets.newLinkedHashSet(((long) (20))), Sets.newLinkedHashSet(), absValueComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

