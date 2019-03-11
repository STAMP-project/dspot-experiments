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
import org.assertj.core.error.ShouldNotHaveDuplicates;
import org.assertj.core.internal.DoubleArraysBaseTest;
import org.assertj.core.test.DoubleArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link DoubleArrays#assertDoesNotHaveDuplicates(AssertionInfo, double[])}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class DoubleArrays_assertDoesNotHaveDuplicates_Test extends DoubleArraysBaseTest {
    @Test
    public void should_pass_if_actual_does_not_have_duplicates() {
        arrays.assertDoesNotHaveDuplicates(TestData.someInfo(), actual);
    }

    @Test
    public void should_pass_if_actual_is_empty() {
        arrays.assertDoesNotHaveDuplicates(TestData.someInfo(), DoubleArrays.emptyArray());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertDoesNotHaveDuplicates(someInfo(), null)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_contains_duplicates() {
        actual = DoubleArrays.arrayOf(6.0, 8.0, 6.0, 8.0);
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertDoesNotHaveDuplicates(someInfo(), actual)).withMessage(ShouldNotHaveDuplicates.shouldNotHaveDuplicates(actual, Sets.newLinkedHashSet(6.0, 8.0)).create());
    }

    @Test
    public void should_pass_if_actual_does_not_have_duplicates_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertDoesNotHaveDuplicates(TestData.someInfo(), actual);
    }

    @Test
    public void should_pass_if_actual_is_empty_whatever_custom_comparison_strategy_is() {
        arraysWithCustomComparisonStrategy.assertDoesNotHaveDuplicates(TestData.someInfo(), DoubleArrays.emptyArray());
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertDoesNotHaveDuplicates(someInfo(), null)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_contains_duplicates_according_to_custom_comparison_strategy() {
        actual = DoubleArrays.arrayOf(6.0, (-8.0), 6.0, (-8.0));
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertDoesNotHaveDuplicates(someInfo(), actual)).withMessage(ShouldNotHaveDuplicates.shouldNotHaveDuplicates(actual, Sets.newLinkedHashSet(6.0, (-8.0)), absValueComparisonStrategy).create());
    }
}

