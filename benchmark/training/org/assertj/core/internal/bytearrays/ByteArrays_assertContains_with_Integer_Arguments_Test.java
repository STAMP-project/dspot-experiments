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
package org.assertj.core.internal.bytearrays;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldContain;
import org.assertj.core.internal.ByteArraysBaseTest;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.test.ByteArrays;
import org.assertj.core.test.IntArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link ByteArrays#assertContains(AssertionInfo, byte[], int[])}</code>.
 */
public class ByteArrays_assertContains_with_Integer_Arguments_Test extends ByteArraysBaseTest {
    @Test
    public void should_pass_if_actual_contains_given_values() {
        arrays.assertContains(TestData.someInfo(), actual, IntArrays.arrayOf(6));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_in_different_order() {
        arrays.assertContains(TestData.someInfo(), actual, IntArrays.arrayOf(8, 10));
    }

    @Test
    public void should_pass_if_actual_contains_all_given_values() {
        arrays.assertContains(TestData.someInfo(), actual, IntArrays.arrayOf(6, 8, 10));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_more_than_once() {
        actual = ByteArrays.arrayOf(6, 8, 10, 10, 8);
        arrays.assertContains(TestData.someInfo(), actual, IntArrays.arrayOf(8));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_even_if_duplicated() {
        arrays.assertContains(TestData.someInfo(), actual, IntArrays.arrayOf(6, 6));
    }

    @Test
    public void should_pass_if_actual_and_given_values_are_empty() {
        actual = ByteArrays.emptyArray();
        arrays.assertContains(TestData.someInfo(), actual, IntArrays.emptyArray());
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContains(someInfo(), actual, IntArrays.emptyArray()));
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertContains(someInfo(), actual, ((int[]) (null)))).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContains(someInfo(), null, IntArrays.arrayOf(8))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_values() {
        AssertionInfo info = TestData.someInfo();
        byte[] expected = new byte[]{ 6, 8, 9 };
        try {
            arrays.assertContains(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContain.shouldContain(actual, expected, Sets.newLinkedHashSet(((byte) (9)))));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_contains_given_values_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContains(TestData.someInfo(), actual, IntArrays.arrayOf(6));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_in_different_order_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContains(TestData.someInfo(), actual, IntArrays.arrayOf((-8), 10));
    }

    @Test
    public void should_pass_if_actual_contains_all_given_values_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContains(TestData.someInfo(), actual, IntArrays.arrayOf(6, (-8), 10));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_more_than_once_according_to_custom_comparison_strategy() {
        actual = ByteArrays.arrayOf(6, (-8), 10, 10, (-8));
        arraysWithCustomComparisonStrategy.assertContains(TestData.someInfo(), actual, IntArrays.arrayOf((-8)));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_even_if_duplicated_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContains(TestData.someInfo(), actual, IntArrays.arrayOf(6, 6));
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContains(someInfo(), actual, IntArrays.emptyArray()));
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContains(someInfo(), actual, ((int[]) (null)))).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContains(someInfo(), null, IntArrays.arrayOf((-8)))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_values_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        try {
            arraysWithCustomComparisonStrategy.assertContains(info, actual, IntArrays.arrayOf(6, (-8), 9));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContain.shouldContain(actual, ByteArrays.arrayOf(6, (-8), 9), Sets.newLinkedHashSet(((byte) (9))), absValueComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

