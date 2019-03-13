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
package org.assertj.core.internal.shortarrays;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldNotHaveDuplicates;
import org.assertj.core.internal.ShortArraysBaseTest;
import org.assertj.core.test.ShortArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link ShortArrays#assertDoesNotHaveDuplicates(AssertionInfo, short[])}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class ShortArrays_assertDoesNotHaveDuplicates_Test extends ShortArraysBaseTest {
    @Test
    public void should_pass_if_actual_does_not_have_duplicates() {
        arrays.assertDoesNotHaveDuplicates(TestData.someInfo(), actual);
    }

    @Test
    public void should_pass_if_actual_is_empty() {
        arrays.assertDoesNotHaveDuplicates(TestData.someInfo(), ShortArrays.emptyArray());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertDoesNotHaveDuplicates(someInfo(), null)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_contains_duplicates() {
        AssertionInfo info = TestData.someInfo();
        actual = ShortArrays.arrayOf(6, 8, 6, 8);
        try {
            arrays.assertDoesNotHaveDuplicates(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotHaveDuplicates.shouldNotHaveDuplicates(actual, Sets.newLinkedHashSet(((short) (6)), ((short) (8)))));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_does_not_have_duplicates_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertDoesNotHaveDuplicates(TestData.someInfo(), actual);
    }

    @Test
    public void should_pass_if_actual_is_empty_whatever_custom_comparison_strategy_is() {
        arraysWithCustomComparisonStrategy.assertDoesNotHaveDuplicates(TestData.someInfo(), ShortArrays.emptyArray());
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertDoesNotHaveDuplicates(someInfo(), null)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_contains_duplicates_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        actual = ShortArrays.arrayOf(6, (-8), 6, 8);
        try {
            arraysWithCustomComparisonStrategy.assertDoesNotHaveDuplicates(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotHaveDuplicates.shouldNotHaveDuplicates(actual, Sets.newLinkedHashSet(((short) (6)), ((short) (8))), absValueComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

