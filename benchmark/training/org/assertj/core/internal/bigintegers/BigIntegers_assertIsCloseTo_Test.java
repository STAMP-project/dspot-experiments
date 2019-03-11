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
package org.assertj.core.internal.bigintegers;


import java.math.BigInteger;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.assertj.core.error.ShouldBeEqualWithinOffset;
import org.assertj.core.internal.BigIntegersBaseTest;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.NumbersBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link org.assertj.core.internal.BigIntegers#assertIsCloseTo(AssertionInfo, BigInteger, BigInteger, org.assertj.core.data.Offset)}</code>.
 */
public class BigIntegers_assertIsCloseTo_Test extends BigIntegersBaseTest {
    private static final BigInteger TWO = new BigInteger("2");

    private static final BigInteger FIVE = new BigInteger("5");

    private static final BigInteger SIX = new BigInteger("6");

    private static final BigInteger NINE = new BigInteger("9");

    @Test
    public void should_pass_if_difference_is_less_than_given_offset() {
        numbers.assertIsCloseTo(TestData.someInfo(), BigIntegers_assertIsCloseTo_Test.FIVE, BigIntegers_assertIsCloseTo_Test.SIX, Offset.offset(BigIntegers_assertIsCloseTo_Test.TWO));
        numbers.assertIsCloseTo(TestData.someInfo(), BigIntegers_assertIsCloseTo_Test.FIVE, BigIntegers_assertIsCloseTo_Test.SIX, Assertions.byLessThan(BigIntegers_assertIsCloseTo_Test.TWO));
        numbers.assertIsCloseTo(TestData.someInfo(), BigInteger.ONE, BigInteger.ONE, Assertions.within(BigInteger.ONE));
        numbers.assertIsCloseTo(TestData.someInfo(), BigInteger.ONE, BigInteger.ONE, Assertions.byLessThan(BigInteger.ONE));
        numbers.assertIsCloseTo(TestData.someInfo(), BigInteger.ONE, BigIntegers_assertIsCloseTo_Test.TWO, Assertions.within(BigInteger.TEN));
        numbers.assertIsCloseTo(TestData.someInfo(), BigInteger.ONE, BigIntegers_assertIsCloseTo_Test.TWO, Assertions.byLessThan(BigInteger.TEN));
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> numbers.assertIsCloseTo(someInfo(), null, BigInteger.ONE, offset(BigInteger.ONE))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if__expected_value_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> numbers.assertIsCloseTo(someInfo(), BigInteger.ONE, null, within(BigInteger.ONE)));
    }

    @Test
    public void should_fail_if_offset_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> numbers.assertIsCloseTo(someInfo(), BigInteger.ONE, BigInteger.ZERO, null));
    }

    // error or failure
    @Test
    public void should_throw_error_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> numbers.assertIsCloseTo(someInfo(), null, BigInteger.ONE, within(BigInteger.ONE))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_throw_error_if_expected_value_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> numbers.assertIsCloseTo(someInfo(), SIX, null, offset(BigInteger.ONE))).withMessage("The given number should not be null");
    }

    @Test
    public void should_throw_error_if_offset_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> numbers.assertIsCloseTo(someInfo(), BigInteger.ONE, BigInteger.ZERO, null));
    }

    @Test
    public void should_fail_if_actual_is_not_close_enough_to_expected_value() {
        AssertionInfo info = TestData.someInfo();
        try {
            numbers.assertIsCloseTo(info, BigInteger.ONE, BigInteger.TEN, Assertions.within(BigInteger.ONE));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqualWithinOffset.shouldBeEqual(BigInteger.ONE, BigInteger.TEN, Assertions.within(BigInteger.ONE), BigIntegers_assertIsCloseTo_Test.NINE));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_not_close_enough_to_expected_value_with_a_strict_offset() {
        AssertionInfo info = TestData.someInfo();
        try {
            numbers.assertIsCloseTo(info, BigInteger.ONE, BigInteger.TEN, Assertions.byLessThan(BigInteger.ONE));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqualWithinOffset.shouldBeEqual(BigInteger.ONE, BigInteger.TEN, Assertions.byLessThan(BigInteger.ONE), BigIntegers_assertIsCloseTo_Test.NINE));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_difference_is_equal_to_the_given_strict_offset() {
        AssertionInfo info = TestData.someInfo();
        try {
            numbers.assertIsCloseTo(info, BigIntegers_assertIsCloseTo_Test.TWO, BigInteger.ONE, Assertions.byLessThan(BigInteger.ONE));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqualWithinOffset.shouldBeEqual(BigIntegers_assertIsCloseTo_Test.TWO, BigInteger.ONE, Assertions.byLessThan(BigInteger.ONE), BigInteger.ONE));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    // with comparison strategy
    @Test
    public void should_pass_if_difference_is_less_than_given_offset_whatever_custom_comparison_strategy_is() {
        numbersWithAbsValueComparisonStrategy.assertIsCloseTo(TestData.someInfo(), BigInteger.ONE, BigInteger.ONE, Assertions.within(BigInteger.ONE));
        numbersWithAbsValueComparisonStrategy.assertIsCloseTo(TestData.someInfo(), BigInteger.ONE, BigIntegers_assertIsCloseTo_Test.TWO, Assertions.within(BigInteger.TEN));
        numbersWithAbsValueComparisonStrategy.assertIsCloseTo(TestData.someInfo(), BigInteger.ONE, BigIntegers_assertIsCloseTo_Test.TWO, Assertions.byLessThan(BigInteger.TEN));
    }

    @Test
    public void should_pass_if_difference_is_equal_to_given_offset_whatever_custom_comparison_strategy_is() {
        numbersWithAbsValueComparisonStrategy.assertIsCloseTo(TestData.someInfo(), BigInteger.ONE, BigInteger.ONE, Assertions.within(BigInteger.ZERO));
        numbersWithAbsValueComparisonStrategy.assertIsCloseTo(TestData.someInfo(), BigInteger.ONE, BigInteger.ZERO, Assertions.within(BigInteger.ONE));
        numbersWithAbsValueComparisonStrategy.assertIsCloseTo(TestData.someInfo(), BigInteger.ONE, BigIntegers_assertIsCloseTo_Test.TWO, Assertions.within(BigInteger.ONE));
    }

    @Test
    public void should_throw_error_if_offset_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> numbersWithAbsValueComparisonStrategy.assertIsCloseTo(someInfo(), BigInteger.ONE, TWO, null)).withMessage(ErrorMessages.offsetIsNull());
    }

    @Test
    public void should_fail_if_actual_is_not_close_enough_to_expected_value_whatever_custom_comparison_strategy_is() {
        AssertionInfo info = TestData.someInfo();
        try {
            numbersWithAbsValueComparisonStrategy.assertIsCloseTo(info, BigInteger.ONE, BigInteger.TEN, Offset.offset(BigInteger.ONE));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqualWithinOffset.shouldBeEqual(BigInteger.ONE, BigInteger.TEN, Offset.offset(BigInteger.ONE), BigIntegers_assertIsCloseTo_Test.NINE));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_not_strictly_close_enough_to_expected_value_whatever_custom_comparison_strategy_is() {
        AssertionInfo info = TestData.someInfo();
        try {
            numbersWithAbsValueComparisonStrategy.assertIsCloseTo(info, BigInteger.ONE, BigInteger.TEN, Assertions.byLessThan(BigInteger.ONE));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqualWithinOffset.shouldBeEqual(BigInteger.ONE, BigInteger.TEN, Assertions.byLessThan(BigInteger.ONE), BigIntegers_assertIsCloseTo_Test.NINE));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_throw_error_if_expected_value_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> numbersWithAbsValueComparisonStrategy.assertIsCloseTo(someInfo(), TWO, null, offset(BigInteger.ONE))).withMessage("The given number should not be null");
    }
}

