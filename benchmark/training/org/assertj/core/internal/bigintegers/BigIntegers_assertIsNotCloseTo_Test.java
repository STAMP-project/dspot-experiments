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
import org.assertj.core.error.ShouldNotBeEqualWithinOffset;
import org.assertj.core.internal.BigIntegersBaseTest;
import org.assertj.core.internal.NumbersBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link org.assertj.core.internal.BigIntegers#assertIsNotCloseTo(AssertionInfo, BigInteger, BigInteger, Offset)}</code>.
 */
public class BigIntegers_assertIsNotCloseTo_Test extends BigIntegersBaseTest {
    private static final BigInteger FIVE = new BigInteger("5");

    @Test
    public void should_pass_if_difference_is_greater_than_offset() {
        numbers.assertIsNotCloseTo(TestData.someInfo(), BigInteger.TEN, BigInteger.ONE, Assertions.within(BigInteger.ONE));
        numbers.assertIsNotCloseTo(TestData.someInfo(), BigInteger.TEN, BigInteger.ONE, Offset.offset(BigInteger.ONE));
        numbers.assertIsNotCloseTo(TestData.someInfo(), BigInteger.TEN, BigInteger.ONE, Assertions.byLessThan(BigInteger.ONE));
    }

    @Test
    public void should_fail_if_difference_is_less_than_given_offset() {
        AssertionInfo info = TestData.someInfo();
        try {
            numbersWithAbsValueComparisonStrategy.assertIsNotCloseTo(info, BigInteger.ONE, BigIntegers_assertIsNotCloseTo_Test.FIVE, Assertions.within(BigInteger.TEN));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeEqualWithinOffset.shouldNotBeEqual(BigInteger.ONE, BigIntegers_assertIsNotCloseTo_Test.FIVE, Assertions.within(BigInteger.TEN), BigIntegers_assertIsNotCloseTo_Test.FIVE.subtract(BigInteger.ONE)));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_difference_is_less_than_given_strict_offset() {
        AssertionInfo info = TestData.someInfo();
        try {
            numbersWithAbsValueComparisonStrategy.assertIsNotCloseTo(info, BigInteger.ONE, BigIntegers_assertIsNotCloseTo_Test.FIVE, Assertions.byLessThan(BigInteger.TEN));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeEqualWithinOffset.shouldNotBeEqual(BigInteger.ONE, BigIntegers_assertIsNotCloseTo_Test.FIVE, Assertions.byLessThan(BigInteger.TEN), BigIntegers_assertIsNotCloseTo_Test.FIVE.subtract(BigInteger.ONE)));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> numbers.assertIsNotCloseTo(someInfo(), null, BigInteger.ONE, byLessThan(BigInteger.ONE))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_expected_value_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> numbers.assertIsNotCloseTo(someInfo(), BigInteger.ONE, null, byLessThan(BigInteger.ONE)));
    }

    @Test
    public void should_fail_if_offset_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> numbers.assertIsNotCloseTo(someInfo(), BigInteger.ONE, BigInteger.ZERO, null));
    }

    // with comparison strategy
    @Test
    public void should_pass_if_big_integers_are_not_close_whatever_custom_comparison_strategy_is() {
        numbersWithAbsValueComparisonStrategy.assertIsNotCloseTo(TestData.someInfo(), BigInteger.TEN, BigInteger.ONE, Assertions.byLessThan(BigInteger.ONE));
    }

    @Test
    public void should_fail_if_difference_is_less_than_given_offset_whatever_custom_comparison_strategy_is() {
        AssertionInfo info = TestData.someInfo();
        try {
            numbersWithAbsValueComparisonStrategy.assertIsNotCloseTo(info, BigIntegers_assertIsNotCloseTo_Test.FIVE, BigIntegers_assertIsNotCloseTo_Test.FIVE, Assertions.byLessThan(BigInteger.ONE));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeEqualWithinOffset.shouldNotBeEqual(BigIntegers_assertIsNotCloseTo_Test.FIVE, BigIntegers_assertIsNotCloseTo_Test.FIVE, Assertions.byLessThan(BigInteger.ONE), BigIntegers_assertIsNotCloseTo_Test.FIVE.subtract(BigIntegers_assertIsNotCloseTo_Test.FIVE)));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> numbersWithAbsValueComparisonStrategy.assertIsNotCloseTo(someInfo(), null, BigInteger.ONE, byLessThan(BigInteger.ONE))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_big_integers_are_equal_whatever_custom_comparison_strategy_is() {
        AssertionInfo info = TestData.someInfo();
        try {
            numbersWithAbsValueComparisonStrategy.assertIsNotCloseTo(info, BigIntegers_assertIsNotCloseTo_Test.FIVE, BigIntegers_assertIsNotCloseTo_Test.FIVE, Assertions.byLessThan(BigInteger.ONE));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeEqualWithinOffset.shouldNotBeEqual(BigIntegers_assertIsNotCloseTo_Test.FIVE, BigIntegers_assertIsNotCloseTo_Test.FIVE, Assertions.byLessThan(BigInteger.ONE), BigIntegers_assertIsNotCloseTo_Test.FIVE.subtract(BigIntegers_assertIsNotCloseTo_Test.FIVE)));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

