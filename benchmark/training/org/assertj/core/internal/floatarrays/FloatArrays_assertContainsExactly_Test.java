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
package org.assertj.core.internal.floatarrays;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldContainExactly;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.FloatArraysBaseTest;
import org.assertj.core.test.FloatArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link FloatArrays#assertContainsExactly(AssertionInfo, float[], float[])}</code>.
 */
public class FloatArrays_assertContainsExactly_Test extends FloatArraysBaseTest {
    @Test
    public void should_pass_if_actual_contains_given_values_exactly() {
        arrays.assertContainsExactly(TestData.someInfo(), actual, FloatArrays.arrayOf(6.0F, 8.0F, 10.0F));
    }

    @Test
    public void should_pass_if_actual_and_given_values_are_empty() {
        arrays.assertContainsExactly(TestData.someInfo(), FloatArrays.emptyArray(), FloatArrays.emptyArray());
    }

    @Test
    public void should_fail_if_actual_contains_given_values_exactly_but_in_different_order() {
        AssertionInfo info = TestData.someInfo();
        try {
            arrays.assertContainsExactly(info, actual, FloatArrays.arrayOf(6.0F, 10.0F, 8.0F));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainExactly.elementsDifferAtIndex(8.0F, 10.0F, 1));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_arrays_have_different_sizes() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsExactly(someInfo(), actual, arrayOf(6.0F, 8.0F)));
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsExactly(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertContainsExactly(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsExactly(someInfo(), null, arrayOf(8.0F))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_given_values_exactly() {
        AssertionInfo info = TestData.someInfo();
        float[] expected = new float[]{ 6.0F, 8.0F, 20.0F };
        try {
            arrays.assertContainsExactly(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainExactly.shouldContainExactly(actual, Arrays.asList(expected), Lists.newArrayList(20.0F), Lists.newArrayList(10.0F)));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_contains_all_given_values_but_size_differ() {
        AssertionInfo info = TestData.someInfo();
        float[] expected = new float[]{ 6.0F, 8.0F, 10.0F, 10.0F };
        try {
            arrays.assertContainsExactly(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainExactly.shouldContainExactly(actual, Arrays.asList(expected), Lists.newArrayList(10.0F), Lists.newArrayList()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    // ------------------------------------------------------------------------------------------------------------------
    // tests using a custom comparison strategy
    // ------------------------------------------------------------------------------------------------------------------
    @Test
    public void should_pass_if_actual_contains_given_values_exactly_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContainsExactly(TestData.someInfo(), actual, FloatArrays.arrayOf(6.0F, (-8.0F), 10.0F));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_exactly_in_different_order_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        float[] expected = new float[]{ -6.0F, 10.0F, 8.0F };
        try {
            arraysWithCustomComparisonStrategy.assertContainsExactly(TestData.someInfo(), actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainExactly.elementsDifferAtIndex(8.0F, 10.0F, 1, absValueComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsExactly(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsExactly(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsExactly(someInfo(), null, arrayOf((-8.0F)))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_given_values_exactly_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        float[] expected = new float[]{ 6.0F, -8.0F, 20.0F };
        try {
            arraysWithCustomComparisonStrategy.assertContainsExactly(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainExactly.shouldContainExactly(actual, Arrays.asList(expected), Lists.newArrayList(20.0F), Lists.newArrayList(10.0F), absValueComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_contains_all_given_values_but_size_differ_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        float[] expected = new float[]{ 6.0F, -8.0F, -10.0F, 10.0F };
        try {
            arraysWithCustomComparisonStrategy.assertContainsExactly(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainExactly.shouldContainExactly(actual, Arrays.asList(expected), Lists.newArrayList(10.0F), Lists.newArrayList(), absValueComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

