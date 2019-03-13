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
import org.assertj.core.error.ShouldBeLess;
import org.assertj.core.internal.BigDecimalsBaseTest;
import org.assertj.core.internal.NumbersBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link BigDecimals#assertLessThan(AssertionInfo, BigDecimal, bigdecimal)}</code>.
 *
 * @author Joel Costigliola
 */
public class BigDecimals_assertLessThan_Test extends BigDecimalsBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> numbers.assertLessThan(someInfo(), null, ONE)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_less_than_other() {
        numbers.assertLessThan(TestData.someInfo(), BigDecimal.ONE, BigDecimal.TEN);
    }

    @Test
    public void should_fail_if_actual_is_equal_to_other() {
        AssertionInfo info = TestData.someInfo();
        try {
            numbers.assertLessThan(info, BigDecimal.TEN, BigDecimal.TEN);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeLess.shouldBeLess(BigDecimal.TEN, BigDecimal.TEN));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_equal_to_other_by_comparison() {
        AssertionInfo info = TestData.someInfo();
        try {
            numbers.assertLessThan(info, BigDecimal.TEN, new BigDecimal("10.00"));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeLess.shouldBeLess(BigDecimal.TEN, new BigDecimal("10.00")));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_less_than_other() {
        AssertionInfo info = TestData.someInfo();
        try {
            numbers.assertLessThan(info, BigDecimal.TEN, BigDecimal.ONE);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeLess.shouldBeLess(BigDecimal.TEN, BigDecimal.ONE));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    // ------------------------------------------------------------------------------------------------------------------
    // tests using a custom comparison strategy
    // ------------------------------------------------------------------------------------------------------------------
    @Test
    public void should_pass_if_actual_is_less_than_other_according_to_custom_comparison_strategy() {
        numbersWithAbsValueComparisonStrategy.assertLessThan(TestData.someInfo(), BigDecimal.ONE, BigDecimal.TEN.negate());
    }

    @Test
    public void should_fail_if_actual_is_equal_to_other_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        try {
            numbersWithAbsValueComparisonStrategy.assertLessThan(info, BigDecimal.TEN.negate(), BigDecimal.TEN);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeLess.shouldBeLess(BigDecimal.TEN.negate(), BigDecimal.TEN, absValueComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_less_than_other_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        try {
            numbersWithAbsValueComparisonStrategy.assertLessThan(info, BigDecimal.TEN.negate(), BigDecimal.ONE);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeLess.shouldBeLess(BigDecimal.TEN.negate(), BigDecimal.ONE, absValueComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

