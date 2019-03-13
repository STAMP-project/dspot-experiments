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
package org.assertj.core.internal.floats;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.assertj.core.error.ShouldBeEqualWithinOffset;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.FloatsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Floats_assertIsCloseTo_Test extends FloatsBaseTest {
    private static final Float ZERO = 0.0F;

    private static final Float ONE = 1.0F;

    private static final Float TWO = 2.0F;

    private static final Float TEN = 10.0F;

    // success
    @Test
    public void should_pass_if_difference_is_less_than_given_offset() {
        floats.assertIsCloseTo(TestData.someInfo(), Floats_assertIsCloseTo_Test.ONE, Floats_assertIsCloseTo_Test.ONE, Assertions.within(Floats_assertIsCloseTo_Test.ONE));
        floats.assertIsCloseTo(TestData.someInfo(), Floats_assertIsCloseTo_Test.ONE, Floats_assertIsCloseTo_Test.TWO, Assertions.within(Floats_assertIsCloseTo_Test.TEN));
        floats.assertIsCloseTo(TestData.someInfo(), Floats_assertIsCloseTo_Test.ONE, Floats_assertIsCloseTo_Test.TWO, Assertions.byLessThan(Floats_assertIsCloseTo_Test.TEN));
    }

    @Test
    public void should_pass_if_difference_is_equal_to_given_offset() {
        floats.assertIsCloseTo(TestData.someInfo(), Floats_assertIsCloseTo_Test.ONE, Floats_assertIsCloseTo_Test.ONE, Assertions.within(Floats_assertIsCloseTo_Test.ZERO));
        floats.assertIsCloseTo(TestData.someInfo(), Floats_assertIsCloseTo_Test.ONE, Floats_assertIsCloseTo_Test.ZERO, Assertions.within(Floats_assertIsCloseTo_Test.ONE));
        floats.assertIsCloseTo(TestData.someInfo(), Floats_assertIsCloseTo_Test.ONE, Floats_assertIsCloseTo_Test.TWO, Assertions.within(Floats_assertIsCloseTo_Test.ONE));
    }

    @Test
    public void should_pass_if_actual_and_expected_are_POSITIVE_INFINITY() {
        floats.assertIsCloseTo(TestData.someInfo(), Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Assertions.within(Floats_assertIsCloseTo_Test.ONE));
        floats.assertIsCloseTo(TestData.someInfo(), Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Assertions.byLessThan(Floats_assertIsCloseTo_Test.ONE));
    }

    @Test
    public void should_pass_if_actual_and_expected_are_NEGATIVE_INFINITY() {
        floats.assertIsCloseTo(TestData.someInfo(), Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Assertions.within(Floats_assertIsCloseTo_Test.ONE));
        floats.assertIsCloseTo(TestData.someInfo(), Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Assertions.byLessThan(Floats_assertIsCloseTo_Test.ONE));
    }

    @Test
    public void should_pass_if_actual_and_expected_are_NaN() {
        floats.assertIsCloseTo(TestData.someInfo(), Float.NaN, Float.NaN, Assertions.within(Floats_assertIsCloseTo_Test.ONE));
        floats.assertIsCloseTo(TestData.someInfo(), Float.NaN, Float.NaN, Assertions.byLessThan(Floats_assertIsCloseTo_Test.ONE));
    }

    // error or failure
    @Test
    public void should_throw_error_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> floats.assertIsCloseTo(someInfo(), null, ONE, within(ONE))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_throw_error_if_expected_value_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> floats.assertIsCloseTo(someInfo(), 6.0F, null, offset(1.0F))).withMessage("The given number should not be null");
    }

    @Test
    public void should_throw_error_if_offset_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> floats.assertIsCloseTo(someInfo(), ONE, ZERO, null));
    }

    @Test
    public void should_fail_if_actual_is_not_close_enough_to_expected_value() {
        AssertionInfo info = TestData.someInfo();
        try {
            floats.assertIsCloseTo(info, Floats_assertIsCloseTo_Test.ONE, Floats_assertIsCloseTo_Test.TEN, Assertions.within(Floats_assertIsCloseTo_Test.ONE));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqualWithinOffset.shouldBeEqual(Floats_assertIsCloseTo_Test.ONE, Floats_assertIsCloseTo_Test.TEN, Assertions.within(Floats_assertIsCloseTo_Test.ONE), ((Floats_assertIsCloseTo_Test.TEN) - (Floats_assertIsCloseTo_Test.ONE))));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_not_close_enough_to_expected_value_with_a_strict_offset() {
        AssertionInfo info = TestData.someInfo();
        try {
            floats.assertIsCloseTo(info, Floats_assertIsCloseTo_Test.ONE, Floats_assertIsCloseTo_Test.TEN, Assertions.byLessThan(Floats_assertIsCloseTo_Test.ONE));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqualWithinOffset.shouldBeEqual(Floats_assertIsCloseTo_Test.ONE, Floats_assertIsCloseTo_Test.TEN, Assertions.byLessThan(Floats_assertIsCloseTo_Test.ONE), ((Floats_assertIsCloseTo_Test.TEN) - (Floats_assertIsCloseTo_Test.ONE))));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_difference_is_equal_to_the_given_strict_offset() {
        AssertionInfo info = TestData.someInfo();
        try {
            floats.assertIsCloseTo(info, Floats_assertIsCloseTo_Test.TWO, Floats_assertIsCloseTo_Test.ONE, Assertions.byLessThan(Floats_assertIsCloseTo_Test.ONE));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqualWithinOffset.shouldBeEqual(Floats_assertIsCloseTo_Test.TWO, Floats_assertIsCloseTo_Test.ONE, Assertions.byLessThan(Floats_assertIsCloseTo_Test.ONE), ((Floats_assertIsCloseTo_Test.TWO) - (Floats_assertIsCloseTo_Test.ONE))));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_NaN_and_expected_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> floats.assertIsCloseTo(someInfo(), Float.NaN, ONE, within(ONE)));
    }

    @Test
    public void should_fail_if_actual_is_POSITIVE_INFINITY_and_expected_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> floats.assertIsCloseTo(someInfo(), Float.POSITIVE_INFINITY, ONE, within(ONE)));
    }

    @Test
    public void should_fail_if_actual_is_NEGATIVE_INFINITY_and_expected_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> floats.assertIsCloseTo(someInfo(), Float.NEGATIVE_INFINITY, ONE, within(ONE)));
    }

    @Test
    public void should_fail_if_actual_is_POSITIVE_INFINITY_and_expected_is_NEGATIVE_INFINITY() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> floats.assertIsCloseTo(someInfo(), Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, within(ONE)));
    }

    @Test
    public void should_fail_if_actual_is_NEGATIVE_INFINITY_and_expected_is_POSITIVE_INFINITY() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> floats.assertIsCloseTo(someInfo(), Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, within(ONE)));
    }

    // with comparison strategy
    @Test
    public void should_pass_if_difference_is_less_than_given_offset_whatever_custom_comparison_strategy_is() {
        floatsWithAbsValueComparisonStrategy.assertIsCloseTo(TestData.someInfo(), Floats_assertIsCloseTo_Test.ONE, Floats_assertIsCloseTo_Test.ONE, Assertions.within(Floats_assertIsCloseTo_Test.ONE));
        floatsWithAbsValueComparisonStrategy.assertIsCloseTo(TestData.someInfo(), Floats_assertIsCloseTo_Test.ONE, Floats_assertIsCloseTo_Test.TWO, Assertions.within(Floats_assertIsCloseTo_Test.TEN));
        floatsWithAbsValueComparisonStrategy.assertIsCloseTo(TestData.someInfo(), Floats_assertIsCloseTo_Test.ONE, Floats_assertIsCloseTo_Test.TWO, Assertions.byLessThan(Floats_assertIsCloseTo_Test.TEN));
    }

    @Test
    public void should_pass_if_difference_is_equal_to_given_offset_whatever_custom_comparison_strategy_is() {
        floatsWithAbsValueComparisonStrategy.assertIsCloseTo(TestData.someInfo(), Floats_assertIsCloseTo_Test.ONE, Floats_assertIsCloseTo_Test.ONE, Assertions.within(Floats_assertIsCloseTo_Test.ZERO));
        floatsWithAbsValueComparisonStrategy.assertIsCloseTo(TestData.someInfo(), Floats_assertIsCloseTo_Test.ONE, Floats_assertIsCloseTo_Test.ZERO, Assertions.within(Floats_assertIsCloseTo_Test.ONE));
        floatsWithAbsValueComparisonStrategy.assertIsCloseTo(TestData.someInfo(), Floats_assertIsCloseTo_Test.ONE, Floats_assertIsCloseTo_Test.TWO, Assertions.within(Floats_assertIsCloseTo_Test.ONE));
    }

    @Test
    public void should_throw_error_if_offset_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> floatsWithAbsValueComparisonStrategy.assertIsCloseTo(someInfo(), new Float(8.0F), new Float(8.0F), null)).withMessage(ErrorMessages.offsetIsNull());
    }

    @Test
    public void should_fail_if_actual_is_not_close_enough_to_expected_value_whatever_custom_comparison_strategy_is() {
        AssertionInfo info = TestData.someInfo();
        try {
            floatsWithAbsValueComparisonStrategy.assertIsCloseTo(info, new Float(6.0F), new Float(8.0F), Offset.offset(1.0F));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqualWithinOffset.shouldBeEqual(6.0F, 8.0F, Offset.offset(1.0F), 2.0F));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_not_strictly_close_enough_to_expected_value_whatever_custom_comparison_strategy_is() {
        AssertionInfo info = TestData.someInfo();
        try {
            floatsWithAbsValueComparisonStrategy.assertIsCloseTo(info, new Float(6.0F), new Float(8.0F), Assertions.byLessThan(1.0F));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqualWithinOffset.shouldBeEqual(6.0F, 8.0F, Assertions.byLessThan(1.0F), 2.0F));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_throw_error_if_expected_value_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> floatsWithAbsValueComparisonStrategy.assertIsCloseTo(someInfo(), 6.0F, null, offset(1.0F))).withMessage("The given number should not be null");
    }
}

