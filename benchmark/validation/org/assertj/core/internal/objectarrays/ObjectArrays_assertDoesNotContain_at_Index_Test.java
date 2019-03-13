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
package org.assertj.core.internal.objectarrays;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Index;
import org.assertj.core.error.ShouldNotContainAtIndex;
import org.assertj.core.internal.ObjectArraysBaseTest;
import org.assertj.core.test.ObjectArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link ObjectArrays#assertDoesNotContain(AssertionInfo, Object[], Object, Index)}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class ObjectArrays_assertDoesNotContain_at_Index_Test extends ObjectArraysBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertDoesNotContain(someInfo(), null, "Yoda", someIndex())).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_does_not_contain_value_at_Index() {
        arrays.assertDoesNotContain(TestData.someInfo(), actual, "Yoda", Index.atIndex(1));
    }

    @Test
    public void should_pass_if_actual_is_empty() {
        arrays.assertDoesNotContain(TestData.someInfo(), ObjectArrays.emptyArray(), "Yoda", TestData.someIndex());
    }

    @Test
    public void should_throw_error_if_Index_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertDoesNotContain(someInfo(), actual, "Yoda", null)).withMessage("Index should not be null");
    }

    @Test
    public void should_pass_if_Index_is_out_of_bounds() {
        arrays.assertDoesNotContain(TestData.someInfo(), actual, "Yoda", Index.atIndex(6));
    }

    @Test
    public void should_fail_if_actual_contains_value_at_index() {
        AssertionInfo info = TestData.someInfo();
        Index index = Index.atIndex(0);
        try {
            arrays.assertDoesNotContain(info, actual, "Yoda", index);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotContainAtIndex.shouldNotContainAtIndex(actual, "Yoda", index));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_does_not_contain_value_at_Index_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertDoesNotContain(TestData.someInfo(), actual, "YOda", Index.atIndex(1));
    }

    @Test
    public void should_pass_if_actual_is_empty_whatever_custom_comparison_strategy_is() {
        arraysWithCustomComparisonStrategy.assertDoesNotContain(TestData.someInfo(), ObjectArrays.emptyArray(), "YOda", TestData.someIndex());
    }

    @Test
    public void should_throw_error_if_Index_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arraysWithCustomComparisonStrategy.assertDoesNotContain(someInfo(), actual, "YOda", null)).withMessage("Index should not be null");
    }

    @Test
    public void should_pass_if_Index_is_out_of_bounds_whatever_custom_comparison_strategy_is() {
        arraysWithCustomComparisonStrategy.assertDoesNotContain(TestData.someInfo(), actual, "YOda", Index.atIndex(6));
    }

    @Test
    public void should_fail_if_actual_contains_value_at_index_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Index index = Index.atIndex(0);
        try {
            arraysWithCustomComparisonStrategy.assertDoesNotContain(info, actual, "YOda", index);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotContainAtIndex.shouldNotContainAtIndex(actual, "YOda", index, caseInsensitiveStringComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

