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
import org.assertj.core.error.ShouldBeBetween;
import org.assertj.core.internal.BigDecimalsBaseTest;
import org.assertj.core.internal.NumbersBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link BigDecimals#assertIsBetween(AssertionInfo, BigDecimal, BigDecimal, BigDecimal)}</code>.
 *
 * @author William Delanoue
 */
public class BigDecimals_assertIsBetween_Test extends BigDecimalsBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> numbers.assertIsBetween(someInfo(), null, ZERO, ONE)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_start_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> numbers.assertIsBetween(someInfo(), ONE, null, ONE));
    }

    @Test
    public void should_fail_if_end_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> numbers.assertIsBetween(someInfo(), ONE, ZERO, null));
    }

    @Test
    public void should_pass_if_actual_is_in_range() {
        numbers.assertIsBetween(TestData.someInfo(), BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.TEN);
        numbers.assertIsBetween(TestData.someInfo(), BigDecimal.ONE, BigDecimal.ONE, BigDecimal.TEN);
        numbers.assertIsBetween(TestData.someInfo(), BigDecimal.ONE, new BigDecimal("1.00"), BigDecimal.TEN);
        numbers.assertIsBetween(TestData.someInfo(), BigDecimal.ONE, BigDecimal.ZERO, new BigDecimal("1.00"));
    }

    @Test
    public void should_pass_if_actual_is_equal_to_range_start() {
        numbers.assertIsBetween(TestData.someInfo(), BigDecimal.ONE, BigDecimal.ONE, BigDecimal.TEN);
    }

    @Test
    public void should_pass_if_actual_is_equal_to_range_end() {
        numbers.assertIsBetween(TestData.someInfo(), BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ONE);
    }

    @Test
    public void should_fail_if_actual_is_not_in_range_start() {
        AssertionInfo info = TestData.someInfo();
        try {
            numbers.assertIsBetween(info, BigDecimal.ONE, new BigDecimal(2), BigDecimal.TEN);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeBetween.shouldBeBetween(BigDecimal.ONE, new BigDecimal(2), BigDecimal.TEN, true, true));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_not_in_range_end() {
        AssertionInfo info = TestData.someInfo();
        try {
            numbers.assertIsBetween(info, BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ZERO);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeBetween.shouldBeBetween(BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ZERO, true, true));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

