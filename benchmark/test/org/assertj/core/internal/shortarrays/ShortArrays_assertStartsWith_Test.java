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
package org.assertj.core.internal.shortarrays;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldStartWith;
import org.assertj.core.internal.ShortArraysBaseTest;
import org.assertj.core.test.ShortArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link ShortArrays#assertStartsWith(AssertionInfo, short[], short[])}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class ShortArrays_assertStartsWith_Test extends ShortArraysBaseTest {
    @Test
    public void should_throw_error_if_sequence_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertStartsWith(someInfo(), actual, null)).withMessage(valuesToLookForIsNull());
    }

    @Test
    public void should_pass_if_actual_and_given_values_are_empty() {
        actual = ShortArrays.emptyArray();
        arrays.assertStartsWith(TestData.someInfo(), actual, ShortArrays.emptyArray());
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertStartsWith(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertStartsWith(someInfo(), null, arrayOf(8))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_sequence_is_bigger_than_actual() {
        AssertionInfo info = TestData.someInfo();
        short[] sequence = new short[]{ 6, 8, 10, 12, 20, 22 };
        try {
            arrays.assertStartsWith(info, actual, sequence);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldStartWith.shouldStartWith(actual, sequence));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_does_not_start_with_sequence() {
        AssertionInfo info = TestData.someInfo();
        short[] sequence = new short[]{ 8, 10 };
        try {
            arrays.assertStartsWith(info, actual, sequence);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldStartWith.shouldStartWith(actual, sequence));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_starts_with_first_elements_of_sequence_only() {
        AssertionInfo info = TestData.someInfo();
        short[] sequence = new short[]{ 6, 20 };
        try {
            arrays.assertStartsWith(info, actual, sequence);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldStartWith.shouldStartWith(actual, sequence));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_starts_with_sequence() {
        arrays.assertStartsWith(TestData.someInfo(), actual, ShortArrays.arrayOf(6, 8, 10));
    }

    @Test
    public void should_pass_if_actual_and_sequence_are_equal() {
        arrays.assertStartsWith(TestData.someInfo(), actual, ShortArrays.arrayOf(6, 8, 10, 12));
    }

    @Test
    public void should_throw_error_if_sequence_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arraysWithCustomComparisonStrategy.assertStartsWith(someInfo(), actual, null)).withMessage(valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertStartsWith(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertStartsWith(someInfo(), null, arrayOf((-8)))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_sequence_is_bigger_than_actual_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        short[] sequence = new short[]{ 6, -8, 10, 12, 20, 22 };
        try {
            arraysWithCustomComparisonStrategy.assertStartsWith(info, actual, sequence);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldStartWith.shouldStartWith(actual, sequence, absValueComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_does_not_start_with_sequence_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        short[] sequence = new short[]{ -8, 10 };
        try {
            arraysWithCustomComparisonStrategy.assertStartsWith(info, actual, sequence);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldStartWith.shouldStartWith(actual, sequence, absValueComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_starts_with_first_elements_of_sequence_only_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        short[] sequence = new short[]{ 6, 20 };
        try {
            arraysWithCustomComparisonStrategy.assertStartsWith(info, actual, sequence);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldStartWith.shouldStartWith(actual, sequence, absValueComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_starts_with_sequence_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertStartsWith(TestData.someInfo(), actual, ShortArrays.arrayOf(6, (-8), 10));
    }

    @Test
    public void should_pass_if_actual_and_sequence_are_equal_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertStartsWith(TestData.someInfo(), actual, ShortArrays.arrayOf(6, (-8), 10, 12));
    }
}

