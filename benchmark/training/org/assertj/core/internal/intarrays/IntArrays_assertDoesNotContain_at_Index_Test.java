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
package org.assertj.core.internal.intarrays;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Index;
import org.assertj.core.error.ShouldNotContainAtIndex;
import org.assertj.core.internal.IntArraysBaseTest;
import org.assertj.core.test.IntArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link IntArrays#assertDoesNotContain(AssertionInfo, int[], int, Index)}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class IntArrays_assertDoesNotContain_at_Index_Test extends IntArraysBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertDoesNotContain(someInfo(), null, 8, someIndex())).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_does_not_contain_value_at_Index() {
        arrays.assertDoesNotContain(TestData.someInfo(), actual, 6, Index.atIndex(1));
    }

    @Test
    public void should_pass_if_actual_is_empty() {
        arrays.assertDoesNotContain(TestData.someInfo(), IntArrays.emptyArray(), 8, TestData.someIndex());
    }

    @Test
    public void should_throw_error_if_Index_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertDoesNotContain(someInfo(), actual, 8, null)).withMessage("Index should not be null");
    }

    @Test
    public void should_pass_if_Index_is_out_of_bounds() {
        arrays.assertDoesNotContain(TestData.someInfo(), actual, 8, Index.atIndex(6));
    }

    @Test
    public void should_fail_if_actual_contains_value_at_index() {
        AssertionInfo info = TestData.someInfo();
        Index index = Index.atIndex(0);
        try {
            arrays.assertDoesNotContain(info, actual, 6, index);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotContainAtIndex.shouldNotContainAtIndex(actual, 6, index));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertDoesNotContain(someInfo(), null, (-8), someIndex())).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_does_not_contain_value_at_Index_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertDoesNotContain(TestData.someInfo(), actual, 6, Index.atIndex(1));
    }

    @Test
    public void should_pass_if_actual_is_empty_whatever_custom_comparison_strategy_is() {
        arraysWithCustomComparisonStrategy.assertDoesNotContain(TestData.someInfo(), IntArrays.emptyArray(), (-8), TestData.someIndex());
    }

    @Test
    public void should_throw_error_if_Index_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arraysWithCustomComparisonStrategy.assertDoesNotContain(someInfo(), actual, (-8), null)).withMessage("Index should not be null");
    }

    @Test
    public void should_pass_if_Index_is_out_of_bounds_whatever_custom_comparison_strategy_is() {
        arraysWithCustomComparisonStrategy.assertDoesNotContain(TestData.someInfo(), actual, (-8), Index.atIndex(6));
    }

    @Test
    public void should_fail_if_actual_contains_value_at_index_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Index index = Index.atIndex(0);
        try {
            arraysWithCustomComparisonStrategy.assertDoesNotContain(info, actual, 6, index);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotContainAtIndex.shouldNotContainAtIndex(actual, 6, index, absValueComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

