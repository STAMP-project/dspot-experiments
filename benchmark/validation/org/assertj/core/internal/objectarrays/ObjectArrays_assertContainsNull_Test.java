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
import org.assertj.core.error.ShouldContainNull;
import org.assertj.core.internal.ObjectArraysBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link ObjectArrays#assertContainsNull(AssertionInfo, Object[])}</code>.
 *
 * @author Joel Costigliola
 */
public class ObjectArrays_assertContainsNull_Test extends ObjectArraysBaseTest {
    @Test
    public void should_pass_if_actual_contains_null() {
        arrays.assertContainsNull(TestData.someInfo(), actual);
    }

    @Test
    public void should_pass_if_actual_contains_only_null_values() {
        actual = Arrays.array(((String) (null)), ((String) (null)));
        arrays.assertContainsNull(TestData.someInfo(), actual);
    }

    @Test
    public void should_pass_if_actual_contains_null_more_than_once() {
        actual = Arrays.array("Luke", null, null);
        arrays.assertContainsNull(TestData.someInfo(), actual);
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsNull(someInfo(), null)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_null() {
        AssertionInfo info = TestData.someInfo();
        actual = Arrays.array("Luke", "Yoda");
        try {
            arrays.assertContainsNull(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainNull.shouldContainNull(actual));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_contains_null_whatever_custom_comparison_strategy_is() {
        arraysWithCustomComparisonStrategy.assertContainsNull(TestData.someInfo(), actual);
    }

    @Test
    public void should_pass_if_actual_contains_only_null_values_according_to_custom_comparison_strategy() {
        actual = Arrays.array(((String) (null)), ((String) (null)));
        arraysWithCustomComparisonStrategy.assertContainsNull(TestData.someInfo(), actual);
    }

    @Test
    public void should_pass_if_actual_contains_null_more_than_once_according_to_custom_comparison_strategy() {
        actual = Arrays.array("Luke", null, null);
        arraysWithCustomComparisonStrategy.assertContainsNull(TestData.someInfo(), actual);
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsNull(someInfo(), null)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_null_whatever_custom_comparison_strategy_is() {
        AssertionInfo info = TestData.someInfo();
        actual = Arrays.array("Luke", "Yoda");
        try {
            arraysWithCustomComparisonStrategy.assertContainsNull(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainNull.shouldContainNull(actual));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

