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
package org.assertj.core.internal.chararrays;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Index;
import org.assertj.core.error.ShouldContainAtIndex;
import org.assertj.core.internal.CharArraysBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link CharArrays#assertContains(AssertionInfo, char[], char, Index)}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class CharArrays_assertContains_at_Index_Test extends CharArraysBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContains(someInfo(), null, 'a', someIndex())).withMessage(actualIsNull());
    }

    @Test
    public void should_fail_if_actual_is_empty() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContains(someInfo(), emptyArray(), 'a', someIndex())).withMessage(actualIsEmpty());
    }

    @Test
    public void should_throw_error_if_Index_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertContains(someInfo(), actual, 'a', null)).withMessage("Index should not be null");
    }

    @Test
    public void should_throw_error_if_Index_is_out_of_bounds() {
        Assertions.assertThatExceptionOfType(IndexOutOfBoundsException.class).isThrownBy(() -> arrays.assertContains(someInfo(), actual, 'a', atIndex(6))).withMessageContaining(String.format("Index should be between <0> and <2> (inclusive) but was:%n <6>"));
    }

    @Test
    public void should_fail_if_actual_does_not_contain_value_at_index() {
        AssertionInfo info = TestData.someInfo();
        Index index = Index.atIndex(1);
        try {
            arrays.assertContains(info, actual, 'a', index);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainAtIndex.shouldContainAtIndex(actual, 'a', index, 'b'));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_contains_value_at_index() {
        arrays.assertContains(TestData.someInfo(), actual, 'b', Index.atIndex(1));
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContains(someInfo(), null, 'A', someIndex())).withMessage(actualIsNull());
    }

    @Test
    public void should_fail_if_actual_is_empty_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContains(someInfo(), emptyArray(), 'A', someIndex())).withMessage(actualIsEmpty());
    }

    @Test
    public void should_throw_error_if_Index_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContains(someInfo(), actual, 'A', null)).withMessage("Index should not be null");
    }

    @Test
    public void should_throw_error_if_Index_is_out_of_bounds_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(IndexOutOfBoundsException.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContains(someInfo(), actual, 'A', atIndex(6))).withMessageContaining(String.format("Index should be between <0> and <2> (inclusive) but was:%n <6>"));
    }

    @Test
    public void should_fail_if_actual_does_not_contain_value_at_index_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Index index = Index.atIndex(1);
        try {
            arraysWithCustomComparisonStrategy.assertContains(info, actual, 'a', index);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainAtIndex.shouldContainAtIndex(actual, 'a', index, 'b', caseInsensitiveComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_contains_value_at_index_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContains(TestData.someInfo(), actual, 'b', Index.atIndex(1));
    }
}

