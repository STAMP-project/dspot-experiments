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
import org.assertj.core.data.Index;
import org.assertj.core.error.ShouldContainAtIndex;
import org.assertj.core.internal.FloatArraysBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link FloatArrays#assertContains(AssertionInfo, float[], float, Index)}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class FloatArrays_assertContains_at_Index_Test extends FloatArraysBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContains(someInfo(), null, 8.0F, someIndex())).withMessage(actualIsNull());
    }

    @Test
    public void should_fail_if_actual_is_empty() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContains(someInfo(), emptyArray(), 8.0F, someIndex())).withMessage(actualIsEmpty());
    }

    @Test
    public void should_throw_error_if_Index_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertContains(someInfo(), actual, 8.0F, null)).withMessage("Index should not be null");
    }

    @Test
    public void should_throw_error_if_Index_is_out_of_bounds() {
        Assertions.assertThatExceptionOfType(IndexOutOfBoundsException.class).isThrownBy(() -> arrays.assertContains(someInfo(), actual, 8.0F, atIndex(6))).withMessageContaining(String.format("Index should be between <0> and <2> (inclusive) but was:%n <6>"));
    }

    @Test
    public void should_fail_if_actual_does_not_contain_value_at_index() {
        float value = 6.0F;
        AssertionInfo info = TestData.someInfo();
        Index index = Index.atIndex(1);
        try {
            arrays.assertContains(info, actual, value, index);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainAtIndex.shouldContainAtIndex(actual, value, index, 8.0F));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_contains_value_at_index() {
        arrays.assertContains(TestData.someInfo(), actual, 8.0F, Index.atIndex(1));
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContains(someInfo(), null, (-8.0F), someIndex())).withMessage(actualIsNull());
    }

    @Test
    public void should_fail_if_actual_is_empty_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContains(someInfo(), emptyArray(), (-8.0F), someIndex())).withMessage(actualIsEmpty());
    }

    @Test
    public void should_throw_error_if_Index_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContains(someInfo(), actual, (-8.0F), null)).withMessage("Index should not be null");
    }

    @Test
    public void should_throw_error_if_Index_is_out_of_bounds_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(IndexOutOfBoundsException.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContains(someInfo(), actual, (-8.0F), atIndex(6))).withMessageContaining(String.format("Index should be between <0> and <2> (inclusive) but was:%n <6>"));
    }

    @Test
    public void should_fail_if_actual_does_not_contain_value_at_index_according_to_custom_comparison_strategy() {
        float value = 6.0F;
        AssertionInfo info = TestData.someInfo();
        Index index = Index.atIndex(1);
        try {
            arraysWithCustomComparisonStrategy.assertContains(info, actual, value, index);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainAtIndex.shouldContainAtIndex(actual, value, index, 8.0F, absValueComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_contains_value_at_index_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContains(TestData.someInfo(), actual, (-8.0F), Index.atIndex(1));
    }
}

