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
import org.assertj.core.error.ShouldBeGreaterOrEqual;
import org.assertj.core.internal.BigIntegersBaseTest;
import org.assertj.core.internal.NumbersBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link BigIntegers#assertGreaterThanOrEqualTo(AssertionInfo, BigInteger, BigInteger)}</code>.
 */
public class BigIntegers_assertGreaterThanOrEqualTo_Test extends BigIntegersBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> numbers.assertGreaterThanOrEqualTo(someInfo(), null, BigInteger.ONE)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_greater_than_other() {
        numbers.assertGreaterThanOrEqualTo(TestData.someInfo(), BigInteger.TEN, BigInteger.ONE);
    }

    @Test
    public void should_pass_if_actual_is_equal_to_other() {
        numbers.assertGreaterThanOrEqualTo(TestData.someInfo(), BigInteger.ONE, BigInteger.ONE);
    }

    @Test
    public void should_pass_if_actual_is_equal_to_other_by_comparison() {
        numbers.assertGreaterThanOrEqualTo(TestData.someInfo(), BigInteger.ONE, new BigInteger("1"));
    }

    @Test
    public void should_fail_if_actual_is_less_than_other() {
        AssertionInfo info = TestData.someInfo();
        try {
            numbers.assertGreaterThanOrEqualTo(info, BigInteger.ONE, BigInteger.TEN);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeGreaterOrEqual.shouldBeGreaterOrEqual(BigInteger.ONE, BigInteger.TEN));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    // ------------------------------------------------------------------------------------------------------------------
    // tests using a custom comparison strategy
    // ------------------------------------------------------------------------------------------------------------------
    @Test
    public void should_pass_if_actual_is_greater_than_other_according_to_custom_comparison_strategy() {
        numbersWithAbsValueComparisonStrategy.assertGreaterThanOrEqualTo(TestData.someInfo(), BigInteger.TEN.negate(), BigInteger.ONE);
    }

    @Test
    public void should_pass_if_actual_is_equal_to_other_according_to_custom_comparison_strategy() {
        numbersWithAbsValueComparisonStrategy.assertGreaterThanOrEqualTo(TestData.someInfo(), BigInteger.ONE.negate(), BigInteger.ONE);
    }

    @Test
    public void should_fail_if_actual_is_less_than_other_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        try {
            numbersWithAbsValueComparisonStrategy.assertGreaterThanOrEqualTo(info, BigInteger.ONE, BigInteger.TEN.negate());
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeGreaterOrEqual.shouldBeGreaterOrEqual(BigInteger.ONE, BigInteger.TEN.negate(), absValueComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

