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
package org.assertj.core.internal.booleanarrays;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldContainExactlyInAnyOrder;
import org.assertj.core.internal.BooleanArraysBaseTest;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.StandardComparisonStrategy;
import org.assertj.core.test.BooleanArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link BooleanArrays#assertContainsExactlyInAnyOrder(AssertionInfo, boolean[], boolean[])}</code>.
 */
public class BooleanArrays_assertContainsExactlyInAnyOrder_Test extends BooleanArraysBaseTest {
    @Test
    public void should_pass_if_actual_contains_given_values_exactly_in_any_order() {
        arrays.assertContainsExactlyInAnyOrder(TestData.someInfo(), actual, BooleanArrays.arrayOf(true, false));
    }

    @Test
    public void should_pass_if_actual_and_given_values_are_empty() {
        arrays.assertContainsExactlyInAnyOrder(TestData.someInfo(), BooleanArrays.emptyArray(), BooleanArrays.emptyArray());
    }

    @Test
    public void should_pass_if_actual_contains_given_values_exactly_but_in_different_order() {
        AssertionInfo info = TestData.someInfo();
        arrays.assertContainsExactlyInAnyOrder(info, actual, BooleanArrays.arrayOf(false, true));
    }

    @Test
    public void should_fail_if_arrays_have_different_sizes() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsExactlyInAnyOrder(someInfo(), actual, arrayOf(true)));
    }

    @Test
    public void should_fail_if_expected_is_empty_and_actual_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsExactlyInAnyOrder(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_throw_error_if_expected_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertContainsExactlyInAnyOrder(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsExactlyInAnyOrder(someInfo(), null, arrayOf(true))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_given_values_exactly() {
        AssertionInfo info = TestData.someInfo();
        boolean[] expected = new boolean[]{ true, true };
        try {
            arrays.assertContainsExactlyInAnyOrder(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainExactlyInAnyOrder.shouldContainExactlyInAnyOrder(actual, expected, Lists.newArrayList(true), Lists.newArrayList(false), StandardComparisonStrategy.instance()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_contains_all_given_values_but_size_differ() {
        AssertionInfo info = TestData.someInfo();
        boolean[] expected = new boolean[]{ true };
        try {
            arrays.assertContainsExactlyInAnyOrder(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainExactlyInAnyOrder.shouldContainExactlyInAnyOrder(actual, expected, Lists.emptyList(), Lists.newArrayList(false), StandardComparisonStrategy.instance()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_contains_duplicates_and_expected_does_not() {
        AssertionInfo info = TestData.someInfo();
        actual = BooleanArrays.arrayOf(true, false, true);
        boolean[] expected = new boolean[]{ true, false };
        try {
            arrays.assertContainsExactlyInAnyOrder(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainExactlyInAnyOrder.shouldContainExactlyInAnyOrder(actual, expected, Lists.emptyList(), Lists.newArrayList(true), StandardComparisonStrategy.instance()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_expected_contains_duplicates_and_actual_does_not() {
        AssertionInfo info = TestData.someInfo();
        actual = BooleanArrays.arrayOf(true, false);
        boolean[] expected = new boolean[]{ true, false, true };
        try {
            arrays.assertContainsExactlyInAnyOrder(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainExactlyInAnyOrder.shouldContainExactlyInAnyOrder(actual, expected, Lists.newArrayList(true), Lists.emptyList(), StandardComparisonStrategy.instance()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

