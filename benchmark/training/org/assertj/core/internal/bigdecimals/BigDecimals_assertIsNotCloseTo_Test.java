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
import org.assertj.core.error.ShouldNotBeEqualWithinOffset;
import org.assertj.core.internal.BigDecimalsBaseTest;
import org.assertj.core.internal.NumbersBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link org.assertj.core.internal.BigDecimals#assertIsNotCloseTo(AssertionInfo, BigDecimal, BigDecimal, org.assertj.core.data.Offset)}</code>.
 *
 * @author Chris Arnott
 */
public class BigDecimals_assertIsNotCloseTo_Test extends BigDecimalsBaseTest {
    private static final BigDecimal FIVE = new BigDecimal("5");

    @Test
    public void should_pass_if_difference_is_greater_than_offset() {
        numbers.assertIsNotCloseTo(TestData.someInfo(), BigDecimal.TEN, BigDecimal.ONE, Assertions.byLessThan(BigDecimal.ONE));
        numbers.assertIsNotCloseTo(TestData.someInfo(), BigDecimal.TEN, BigDecimal.ONE, Assertions.within(BigDecimal.ONE));
        numbers.assertIsNotCloseTo(TestData.someInfo(), BigDecimal.TEN, BigDecimal.ONE, Offset.offset(BigDecimal.ONE));
    }

    @Test
    public void should_fail_if_difference_is_less_than_given_offset() {
        BigDecimal fiveDotOne = new BigDecimal("5.1");
        AssertionInfo info = TestData.someInfo();
        try {
            numbersWithAbsValueComparisonStrategy.assertIsNotCloseTo(info, fiveDotOne, BigDecimals_assertIsNotCloseTo_Test.FIVE, Assertions.within(BigDecimal.ONE));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeEqualWithinOffset.shouldNotBeEqual(fiveDotOne, BigDecimals_assertIsNotCloseTo_Test.FIVE, Assertions.within(BigDecimal.ONE), fiveDotOne.subtract(BigDecimals_assertIsNotCloseTo_Test.FIVE)));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_difference_is_less_than_given_strict_offset() {
        BigDecimal fiveDotOne = new BigDecimal("5.1");
        AssertionInfo info = TestData.someInfo();
        try {
            numbersWithAbsValueComparisonStrategy.assertIsNotCloseTo(info, fiveDotOne, BigDecimals_assertIsNotCloseTo_Test.FIVE, Assertions.byLessThan(BigDecimal.ONE));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeEqualWithinOffset.shouldNotBeEqual(fiveDotOne, BigDecimals_assertIsNotCloseTo_Test.FIVE, Assertions.byLessThan(BigDecimal.ONE), fiveDotOne.subtract(BigDecimals_assertIsNotCloseTo_Test.FIVE)));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> numbers.assertIsNotCloseTo(someInfo(), null, BigDecimal.ONE, byLessThan(BigDecimal.ONE))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_expected_value_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> numbers.assertIsNotCloseTo(someInfo(), BigDecimal.ONE, null, byLessThan(BigDecimal.ONE)));
    }

    @Test
    public void should_fail_if_offset_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> numbers.assertIsNotCloseTo(someInfo(), BigDecimal.ONE, BigDecimal.ZERO, null));
    }

    // with comparison strategy
    @Test
    public void should_pass_if_difference_is_greater_than_offset_whatever_custom_comparison_strategy_is() {
        numbersWithAbsValueComparisonStrategy.assertIsNotCloseTo(TestData.someInfo(), BigDecimal.TEN, BigDecimal.ONE, Assertions.byLessThan(BigDecimal.ONE));
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> numbersWithAbsValueComparisonStrategy.assertIsNotCloseTo(someInfo(), null, BigDecimal.ONE, byLessThan(BigDecimal.ONE))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_big_decimals_are_equal_whatever_custom_comparison_strategy_is() {
        BigDecimal fiveDotZero = new BigDecimal("5.0");
        AssertionInfo info = TestData.someInfo();
        try {
            numbersWithAbsValueComparisonStrategy.assertIsNotCloseTo(info, fiveDotZero, BigDecimals_assertIsNotCloseTo_Test.FIVE, Assertions.byLessThan(BigDecimal.ONE));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeEqualWithinOffset.shouldNotBeEqual(fiveDotZero, BigDecimals_assertIsNotCloseTo_Test.FIVE, Assertions.byLessThan(BigDecimal.ONE), fiveDotZero.subtract(BigDecimals_assertIsNotCloseTo_Test.FIVE)));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

