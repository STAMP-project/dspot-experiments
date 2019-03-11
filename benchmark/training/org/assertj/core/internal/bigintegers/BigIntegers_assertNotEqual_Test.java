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
import org.assertj.core.error.ShouldNotBeEqual;
import org.assertj.core.internal.BigIntegersBaseTest;
import org.assertj.core.internal.NumbersBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link BigIntegers#assertNotEqual(AssertionInfo, BigInteger, BigInteger)}</code>.
 */
public class BigIntegers_assertNotEqual_Test extends BigIntegersBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> numbers.assertNotEqual(someInfo(), null, BigInteger.ONE)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_big_integers_are_not_equal() {
        numbers.assertNotEqual(TestData.someInfo(), BigInteger.ONE, BigInteger.TEN);
    }

    @Test
    public void should_fail_if_big_integers_are_equal() {
        AssertionInfo info = TestData.someInfo();
        try {
            numbers.assertNotEqual(info, BigInteger.ONE, BigInteger.ONE);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeEqual.shouldNotBeEqual(BigInteger.ONE, BigInteger.ONE));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> numbersWithComparatorComparisonStrategy.assertNotEqual(someInfo(), null, BigInteger.ONE)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_big_integers_are_not_equal_according_to_custom_comparison_strategy() {
        numbersWithComparatorComparisonStrategy.assertNotEqual(TestData.someInfo(), BigInteger.TEN, BigInteger.ONE);
    }

    @Test
    public void should_fail_if_big_integers_are_equal_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        try {
            numbersWithComparatorComparisonStrategy.assertNotEqual(info, BigInteger.ONE, BigInteger.ONE);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeEqual.shouldNotBeEqual(BigInteger.ONE, BigInteger.ONE, comparatorComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

