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
package org.assertj.core.internal.doubles;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.assertj.core.error.ShouldBeEqualWithinOffset;
import org.assertj.core.internal.DoublesBaseTest;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Doubles_assertIsCloseTo_Test extends DoublesBaseTest {
    private static final Double ZERO = 0.0;

    private static final Double ONE = 1.0;

    private static final Double TWO = 2.0;

    private static final Double TEN = 10.0;

    // success
    @Test
    public void should_pass_if_difference_is_less_than_given_offset() {
        doubles.assertIsCloseTo(TestData.someInfo(), Doubles_assertIsCloseTo_Test.ONE, Doubles_assertIsCloseTo_Test.ONE, Assertions.within(Doubles_assertIsCloseTo_Test.ONE));
        doubles.assertIsCloseTo(TestData.someInfo(), Doubles_assertIsCloseTo_Test.ONE, Doubles_assertIsCloseTo_Test.TWO, Assertions.within(Doubles_assertIsCloseTo_Test.TEN));
        doubles.assertIsCloseTo(TestData.someInfo(), Doubles_assertIsCloseTo_Test.ONE, Doubles_assertIsCloseTo_Test.TWO, Assertions.byLessThan(Doubles_assertIsCloseTo_Test.TEN));
    }

    @Test
    public void should_pass_if_difference_is_equal_to_given_offset() {
        doubles.assertIsCloseTo(TestData.someInfo(), Doubles_assertIsCloseTo_Test.ONE, Doubles_assertIsCloseTo_Test.ONE, Assertions.within(Doubles_assertIsCloseTo_Test.ZERO));
        doubles.assertIsCloseTo(TestData.someInfo(), Doubles_assertIsCloseTo_Test.ONE, Doubles_assertIsCloseTo_Test.ZERO, Assertions.within(Doubles_assertIsCloseTo_Test.ONE));
        doubles.assertIsCloseTo(TestData.someInfo(), Doubles_assertIsCloseTo_Test.ONE, Doubles_assertIsCloseTo_Test.TWO, Assertions.within(Doubles_assertIsCloseTo_Test.ONE));
    }

    @Test
    public void should_pass_if_actual_and_expected_are_POSITIVE_INFINITY() {
        doubles.assertIsCloseTo(TestData.someInfo(), Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Assertions.within(Doubles_assertIsCloseTo_Test.ONE));
        doubles.assertIsCloseTo(TestData.someInfo(), Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Assertions.byLessThan(Doubles_assertIsCloseTo_Test.ONE));
    }

    @Test
    public void should_pass_if_actual_and_expected_are_NEGATIVE_INFINITY() {
        doubles.assertIsCloseTo(TestData.someInfo(), Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Assertions.within(Doubles_assertIsCloseTo_Test.ONE));
        doubles.assertIsCloseTo(TestData.someInfo(), Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Assertions.byLessThan(Doubles_assertIsCloseTo_Test.ONE));
    }

    @Test
    public void should_pass_if_actual_and_expected_are_NaN() {
        doubles.assertIsCloseTo(TestData.someInfo(), Double.NaN, Double.NaN, Assertions.within(Doubles_assertIsCloseTo_Test.ONE));
        doubles.assertIsCloseTo(TestData.someInfo(), Double.NaN, Double.NaN, Assertions.byLessThan(Doubles_assertIsCloseTo_Test.ONE));
    }

    // error or failure
    @Test
    public void should_throw_error_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> doubles.assertIsCloseTo(someInfo(), null, ONE, within(ONE))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_throw_error_if_expected_value_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> doubles.assertIsCloseTo(someInfo(), 6.0, null, offset(1.0))).withMessage("The given number should not be null");
    }

    @Test
    public void should_throw_error_if_offset_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> doubles.assertIsCloseTo(someInfo(), ONE, ZERO, null));
    }

    @Test
    public void should_fail_if_actual_is_not_close_enough_to_expected_value() {
        AssertionInfo info = TestData.someInfo();
        try {
            doubles.assertIsCloseTo(info, Doubles_assertIsCloseTo_Test.ONE, Doubles_assertIsCloseTo_Test.TEN, Assertions.within(Doubles_assertIsCloseTo_Test.ONE));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqualWithinOffset.shouldBeEqual(Doubles_assertIsCloseTo_Test.ONE, Doubles_assertIsCloseTo_Test.TEN, Assertions.within(Doubles_assertIsCloseTo_Test.ONE), ((Doubles_assertIsCloseTo_Test.TEN) - (Doubles_assertIsCloseTo_Test.ONE))));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_not_close_enough_to_expected_value_with_a_strict_offset() {
        AssertionInfo info = TestData.someInfo();
        try {
            doubles.assertIsCloseTo(info, Doubles_assertIsCloseTo_Test.ONE, Doubles_assertIsCloseTo_Test.TEN, Assertions.byLessThan(Doubles_assertIsCloseTo_Test.ONE));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqualWithinOffset.shouldBeEqual(Doubles_assertIsCloseTo_Test.ONE, Doubles_assertIsCloseTo_Test.TEN, Assertions.byLessThan(Doubles_assertIsCloseTo_Test.ONE), ((Doubles_assertIsCloseTo_Test.TEN) - (Doubles_assertIsCloseTo_Test.ONE))));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_difference_is_equal_to_the_given_strict_offset() {
        AssertionInfo info = TestData.someInfo();
        try {
            doubles.assertIsCloseTo(info, Doubles_assertIsCloseTo_Test.TWO, Doubles_assertIsCloseTo_Test.ONE, Assertions.byLessThan(Doubles_assertIsCloseTo_Test.ONE));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqualWithinOffset.shouldBeEqual(Doubles_assertIsCloseTo_Test.TWO, Doubles_assertIsCloseTo_Test.ONE, Assertions.byLessThan(Doubles_assertIsCloseTo_Test.ONE), ((Doubles_assertIsCloseTo_Test.TWO) - (Doubles_assertIsCloseTo_Test.ONE))));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_NaN_and_expected_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> doubles.assertIsCloseTo(someInfo(), Double.NaN, ONE, within(ONE)));
    }

    @Test
    public void should_fail_if_actual_is_POSITIVE_INFINITY_and_expected_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> doubles.assertIsCloseTo(someInfo(), Double.POSITIVE_INFINITY, ONE, within(ONE)));
    }

    @Test
    public void should_fail_if_actual_is_NEGATIVE_INFINITY_and_expected_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> doubles.assertIsCloseTo(someInfo(), Double.NEGATIVE_INFINITY, ONE, within(ONE)));
    }

    @Test
    public void should_fail_if_actual_is_POSITIVE_INFINITY_and_expected_is_NEGATIVE_INFINITY() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> doubles.assertIsCloseTo(someInfo(), Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, within(ONE)));
    }

    @Test
    public void should_fail_if_actual_is_NEGATIVE_INFINITY_and_expected_is_POSITIVE_INFINITY() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> doubles.assertIsCloseTo(someInfo(), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, within(ONE)));
    }

    // with comparison strategy
    @Test
    public void should_pass_if_difference_is_less_than_given_offset_whatever_custom_comparison_strategy_is() {
        doublesWithAbsValueComparisonStrategy.assertIsCloseTo(TestData.someInfo(), Doubles_assertIsCloseTo_Test.ONE, Doubles_assertIsCloseTo_Test.ONE, Assertions.within(Doubles_assertIsCloseTo_Test.ONE));
        doublesWithAbsValueComparisonStrategy.assertIsCloseTo(TestData.someInfo(), Doubles_assertIsCloseTo_Test.ONE, Doubles_assertIsCloseTo_Test.TWO, Assertions.within(Doubles_assertIsCloseTo_Test.TEN));
        doublesWithAbsValueComparisonStrategy.assertIsCloseTo(TestData.someInfo(), Doubles_assertIsCloseTo_Test.ONE, Doubles_assertIsCloseTo_Test.TWO, Assertions.byLessThan(Doubles_assertIsCloseTo_Test.TEN));
    }

    @Test
    public void should_pass_if_difference_is_equal_to_given_offset_whatever_custom_comparison_strategy_is() {
        doublesWithAbsValueComparisonStrategy.assertIsCloseTo(TestData.someInfo(), Doubles_assertIsCloseTo_Test.ONE, Doubles_assertIsCloseTo_Test.ONE, Assertions.within(Doubles_assertIsCloseTo_Test.ZERO));
        doublesWithAbsValueComparisonStrategy.assertIsCloseTo(TestData.someInfo(), Doubles_assertIsCloseTo_Test.ONE, Doubles_assertIsCloseTo_Test.ZERO, Assertions.within(Doubles_assertIsCloseTo_Test.ONE));
        doublesWithAbsValueComparisonStrategy.assertIsCloseTo(TestData.someInfo(), Doubles_assertIsCloseTo_Test.ONE, Doubles_assertIsCloseTo_Test.TWO, Assertions.within(Doubles_assertIsCloseTo_Test.ONE));
    }

    @Test
    public void should_throw_error_if_offset_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> doublesWithAbsValueComparisonStrategy.assertIsCloseTo(someInfo(), new Double(8.0), new Double(8.0), null)).withMessage(ErrorMessages.offsetIsNull());
    }

    @Test
    public void should_fail_if_actual_is_not_close_enough_to_expected_value_whatever_custom_comparison_strategy_is() {
        AssertionInfo info = TestData.someInfo();
        try {
            doublesWithAbsValueComparisonStrategy.assertIsCloseTo(info, new Double(6.0), new Double(8.0), Offset.offset(1.0));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqualWithinOffset.shouldBeEqual(6.0, 8.0, Offset.offset(1.0), 2.0));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_not_strictly_close_enough_to_expected_value_whatever_custom_comparison_strategy_is() {
        AssertionInfo info = TestData.someInfo();
        try {
            doublesWithAbsValueComparisonStrategy.assertIsCloseTo(info, new Double(6.0), new Double(8.0), Assertions.byLessThan(1.0));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqualWithinOffset.shouldBeEqual(6.0, 8.0, Assertions.byLessThan(1.0), 2.0));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_throw_error_if_expected_value_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> doublesWithAbsValueComparisonStrategy.assertIsCloseTo(someInfo(), 6.0, null, offset(1.0))).withMessage("The given number should not be null");
    }
}

