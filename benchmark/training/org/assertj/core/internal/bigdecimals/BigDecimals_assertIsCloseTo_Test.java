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
package org.assertj.core.internal.bigdecimals;


import java.math.BigDecimal;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.assertj.core.error.ShouldBeEqualWithinOffset;
import org.assertj.core.internal.BigDecimalsBaseTest;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.NumbersBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link org.assertj.core.internal.BigDecimals#assertIsCloseTo(org.assertj.core.api.AssertionInfo, java.math.BigDecimal, java.math.BigDecimal, org.assertj.core.data.Offset)}</code>.
 *
 * @author Joel Costigliola
 */
public class BigDecimals_assertIsCloseTo_Test extends BigDecimalsBaseTest {
    private static final BigDecimal NINE = new BigDecimal(9.0);

    private static final BigDecimal TWO = new BigDecimal(2);

    @Test
    public void should_pass_if_difference_is_less_than_given_offset() {
        numbers.assertIsCloseTo(TestData.someInfo(), BigDecimal.ONE, BigDecimal.ONE, Assertions.within(BigDecimal.ONE));
        numbers.assertIsCloseTo(TestData.someInfo(), BigDecimal.ONE, BigDecimals_assertIsCloseTo_Test.TWO, Assertions.within(BigDecimal.TEN));
        numbers.assertIsCloseTo(TestData.someInfo(), BigDecimal.ONE, BigDecimals_assertIsCloseTo_Test.TWO, Assertions.byLessThan(BigDecimal.TEN));
    }

    // error or failure
    @Test
    public void should_throw_error_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> numbers.assertIsCloseTo(someInfo(), null, BigDecimal.ONE, within(BigDecimal.ONE))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_throw_error_if_expected_value_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> numbers.assertIsCloseTo(someInfo(), new BigDecimal(6.0), null, offset(BigDecimal.ONE))).withMessage("The given number should not be null");
    }

    @Test
    public void should_throw_error_if_offset_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> numbers.assertIsCloseTo(someInfo(), BigDecimal.ONE, BigDecimal.ZERO, null));
    }

    @Test
    public void should_fail_if_actual_is_not_close_enough_to_expected_value() {
        AssertionInfo info = TestData.someInfo();
        try {
            numbers.assertIsCloseTo(info, BigDecimal.ONE, BigDecimal.TEN, Assertions.within(BigDecimal.ONE));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqualWithinOffset.shouldBeEqual(BigDecimal.ONE, BigDecimal.TEN, Assertions.within(BigDecimal.ONE), BigDecimals_assertIsCloseTo_Test.NINE));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_not_close_enough_to_expected_value_with_a_strict_offset() {
        AssertionInfo info = TestData.someInfo();
        try {
            numbers.assertIsCloseTo(info, BigDecimal.ONE, BigDecimal.TEN, Assertions.byLessThan(BigDecimal.ONE));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqualWithinOffset.shouldBeEqual(BigDecimal.ONE, BigDecimal.TEN, Assertions.byLessThan(BigDecimal.ONE), BigDecimals_assertIsCloseTo_Test.NINE));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_difference_is_equal_to_the_given_strict_offset() {
        AssertionInfo info = TestData.someInfo();
        try {
            numbers.assertIsCloseTo(info, BigDecimals_assertIsCloseTo_Test.TWO, BigDecimal.ONE, Assertions.byLessThan(BigDecimal.ONE));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqualWithinOffset.shouldBeEqual(BigDecimals_assertIsCloseTo_Test.TWO, BigDecimal.ONE, Assertions.byLessThan(BigDecimal.ONE), BigDecimal.ONE));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    // with comparison strategy
    @Test
    public void should_pass_if_difference_is_less_than_given_offset_whatever_custom_comparison_strategy_is() {
        numbersWithAbsValueComparisonStrategy.assertIsCloseTo(TestData.someInfo(), BigDecimal.ONE, BigDecimal.ONE, Assertions.within(BigDecimal.ONE));
        numbersWithAbsValueComparisonStrategy.assertIsCloseTo(TestData.someInfo(), BigDecimal.ONE, BigDecimals_assertIsCloseTo_Test.TWO, Assertions.within(BigDecimal.TEN));
        numbersWithAbsValueComparisonStrategy.assertIsCloseTo(TestData.someInfo(), BigDecimal.ONE, BigDecimals_assertIsCloseTo_Test.TWO, Assertions.byLessThan(BigDecimal.TEN));
    }

    @Test
    public void should_pass_if_difference_is_equal_to_given_offset_whatever_custom_comparison_strategy_is() {
        numbersWithAbsValueComparisonStrategy.assertIsCloseTo(TestData.someInfo(), BigDecimal.ONE, BigDecimal.ONE, Assertions.within(BigDecimal.ZERO));
        numbersWithAbsValueComparisonStrategy.assertIsCloseTo(TestData.someInfo(), BigDecimal.ONE, BigDecimal.ZERO, Assertions.within(BigDecimal.ONE));
        numbersWithAbsValueComparisonStrategy.assertIsCloseTo(TestData.someInfo(), BigDecimal.ONE, BigDecimals_assertIsCloseTo_Test.TWO, Assertions.within(BigDecimal.ONE));
    }

    @Test
    public void should_throw_error_if_offset_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> numbersWithAbsValueComparisonStrategy.assertIsCloseTo(someInfo(), BigDecimal.ONE, TWO, null)).withMessage(ErrorMessages.offsetIsNull());
    }

    @Test
    public void should_fail_if_actual_is_not_close_enough_to_expected_value_whatever_custom_comparison_strategy_is() {
        AssertionInfo info = TestData.someInfo();
        try {
            numbersWithAbsValueComparisonStrategy.assertIsCloseTo(info, BigDecimal.ONE, BigDecimal.TEN, Offset.offset(BigDecimal.ONE));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqualWithinOffset.shouldBeEqual(BigDecimal.ONE, BigDecimal.TEN, Offset.offset(BigDecimal.ONE), BigDecimals_assertIsCloseTo_Test.NINE));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_not_strictly_close_enough_to_expected_value_whatever_custom_comparison_strategy_is() {
        AssertionInfo info = TestData.someInfo();
        try {
            numbersWithAbsValueComparisonStrategy.assertIsCloseTo(info, BigDecimal.ONE, BigDecimal.TEN, Assertions.byLessThan(BigDecimal.ONE));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqualWithinOffset.shouldBeEqual(BigDecimal.ONE, BigDecimal.TEN, Assertions.byLessThan(BigDecimal.ONE), BigDecimals_assertIsCloseTo_Test.NINE));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_throw_error_if_expected_value_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> numbersWithAbsValueComparisonStrategy.assertIsCloseTo(someInfo(), TWO, null, offset(BigDecimal.ONE))).withMessage("The given number should not be null");
    }
}

