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
package org.assertj.core.internal.doublearrays;


import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldBeSorted;
import org.assertj.core.internal.DoubleArraysBaseTest;
import org.assertj.core.test.DoubleArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link DoubleArrays#assertIsSorted(AssertionInfo, Object[])}</code>.
 *
 * @author Joel Costigliola
 */
public class DoubleArrays_assertIsSorted_Test extends DoubleArraysBaseTest {
    @Test
    public void should_pass_if_actual_is_sorted_in_ascending_order() {
        arrays.assertIsSorted(TestData.someInfo(), actual);
    }

    @Test
    public void should_pass_if_actual_is_empty() {
        arrays.assertIsSorted(TestData.someInfo(), DoubleArrays.emptyArray());
    }

    @Test
    public void should_pass_if_actual_contains_only_one_element() {
        arrays.assertIsSorted(TestData.someInfo(), DoubleArrays.arrayOf(1.0));
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertIsSorted(someInfo(), ((double[]) (null)))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_is_not_sorted_in_ascending_order() {
        actual = DoubleArrays.arrayOf(1.0, 3.0, 2.0);
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertIsSorted(someInfo(), actual)).withMessage(ShouldBeSorted.shouldBeSorted(1, actual).create());
    }

    @Test
    public void should_pass_if_actual_is_sorted_in_ascending_order_according_to_custom_comparison_strategy() {
        actual = DoubleArrays.arrayOf(1.0, (-2.0), 3.0, (-4.0), 4.0);
        arraysWithCustomComparisonStrategy.assertIsSorted(TestData.someInfo(), actual);
    }

    @Test
    public void should_pass_if_actual_is_empty_whatever_custom_comparison_strategy_is() {
        arraysWithCustomComparisonStrategy.assertIsSorted(TestData.someInfo(), DoubleArrays.emptyArray());
    }

    @Test
    public void should_pass_if_actual_contains_only_one_element_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertIsSorted(TestData.someInfo(), DoubleArrays.arrayOf(1.0));
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertIsSorted(someInfo(), ((double[]) (null)))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_is_not_sorted_in_ascending_order_according_to_custom_comparison_strategy() {
        actual = DoubleArrays.arrayOf(1.0, 3.0, 2.0);
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertIsSorted(someInfo(), actual)).withMessage(ShouldBeSorted.shouldBeSortedAccordingToGivenComparator(1, actual, comparatorForCustomComparisonStrategy()).create());
    }
}

