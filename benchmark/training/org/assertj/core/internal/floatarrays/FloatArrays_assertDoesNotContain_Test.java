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
import org.assertj.core.error.ShouldNotContain;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.FloatArraysBaseTest;
import org.assertj.core.test.FloatArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link FloatArrays#assertDoesNotContain(AssertionInfo, float[], float[])}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class FloatArrays_assertDoesNotContain_Test extends FloatArraysBaseTest {
    @Test
    public void should_pass_if_actual_does_not_contain_given_values() {
        arrays.assertDoesNotContain(TestData.someInfo(), actual, FloatArrays.arrayOf(12.0F));
    }

    @Test
    public void should_pass_if_actual_does_not_contain_given_values_even_if_duplicated() {
        arrays.assertDoesNotContain(TestData.someInfo(), actual, FloatArrays.arrayOf(12.0F, 12.0F, 20.0F));
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_empty() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> arrays.assertDoesNotContain(someInfo(), actual, emptyArray())).withMessage(ErrorMessages.valuesToLookForIsEmpty());
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertDoesNotContain(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertDoesNotContain(someInfo(), null, arrayOf(8.0F))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_contains_given_values() {
        AssertionInfo info = TestData.someInfo();
        float[] expected = new float[]{ 6.0F, 8.0F, 20.0F };
        try {
            arrays.assertDoesNotContain(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotContain.shouldNotContain(actual, expected, Sets.newLinkedHashSet(6.0F, 8.0F)));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_does_not_contain_given_values_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertDoesNotContain(TestData.someInfo(), actual, FloatArrays.arrayOf(12.0F));
    }

    @Test
    public void should_pass_if_actual_does_not_contain_given_values_even_if_duplicated_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertDoesNotContain(TestData.someInfo(), actual, FloatArrays.arrayOf(12.0F, 12.0F, 20.0F));
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_empty_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> arraysWithCustomComparisonStrategy.assertDoesNotContain(someInfo(), actual, emptyArray())).withMessage(ErrorMessages.valuesToLookForIsEmpty());
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arraysWithCustomComparisonStrategy.assertDoesNotContain(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertDoesNotContain(someInfo(), null, arrayOf((-8.0F)))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_contains_given_values_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        float[] expected = new float[]{ 6.0F, -8.0F, 20.0F };
        try {
            arraysWithCustomComparisonStrategy.assertDoesNotContain(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotContain.shouldNotContain(actual, expected, Sets.newLinkedHashSet(6.0F, (-8.0F)), absValueComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

