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
import org.assertj.core.error.ShouldBeSorted;
import org.assertj.core.internal.LongArraysBaseTest;
import org.assertj.core.test.LongArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link LongArrays#assertIsSorted(AssertionInfo, Object[])}</code>.
 *
 * @author Joel Costigliola
 */
public class LongArrays_assertIsSorted_Test extends LongArraysBaseTest {
    @Test
    public void should_pass_if_actual_is_sorted_in_ascending_order() {
        arrays.assertIsSorted(TestData.someInfo(), actual);
    }

    @Test
    public void should_pass_if_actual_is_empty() {
        arrays.assertIsSorted(TestData.someInfo(), LongArrays.emptyArray());
    }

    @Test
    public void should_pass_if_actual_contains_only_one_element() {
        arrays.assertIsSorted(TestData.someInfo(), LongArrays.arrayOf(1L));
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertIsSorted(someInfo(), ((long[]) (null)))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_is_not_sorted_in_ascending_order() {
        AssertionInfo info = TestData.someInfo();
        actual = LongArrays.arrayOf(1L, 3L, 2L);
        try {
            arrays.assertIsSorted(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeSorted.shouldBeSorted(1, actual));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_is_sorted_in_ascending_order_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertIsSorted(TestData.someInfo(), LongArrays.arrayOf(1L, (-2L), 3L, (-4L), 4L));
    }

    @Test
    public void should_pass_if_actual_is_empty_whatever_custom_comparison_strategy_is() {
        arraysWithCustomComparisonStrategy.assertIsSorted(TestData.someInfo(), LongArrays.emptyArray());
    }

    @Test
    public void should_pass_if_actual_contains_only_one_element_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertIsSorted(TestData.someInfo(), LongArrays.arrayOf(1L));
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertIsSorted(someInfo(), ((long[]) (null)))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_is_not_sorted_in_ascending_order_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        actual = LongArrays.arrayOf(1L, 3L, 2L);
        try {
            arraysWithCustomComparisonStrategy.assertIsSorted(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeSorted.shouldBeSortedAccordingToGivenComparator(1, actual, comparatorForCustomComparisonStrategy()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

