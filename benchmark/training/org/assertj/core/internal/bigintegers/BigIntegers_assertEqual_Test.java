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
import org.assertj.core.error.ShouldBeEqual;
import org.assertj.core.internal.BigIntegersBaseTest;
import org.assertj.core.internal.NumbersBaseTest;
import org.assertj.core.presentation.StandardRepresentation;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link BigIntegers#assertEqual(AssertionInfo, BigInteger, BigInteger)}</code>.
 */
public class BigIntegers_assertEqual_Test extends BigIntegersBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> numbers.assertEqual(someInfo(), null, BigInteger.ONE)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_big_integers_are_equal() {
        numbers.assertEqual(TestData.someInfo(), BigInteger.ONE, BigInteger.ONE);
    }

    @Test
    public void should_fail_if_big_integers_are_not_equal() {
        AssertionInfo info = TestData.someInfo();
        try {
            numbers.assertEqual(info, BigInteger.ONE, BigInteger.TEN);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqual.shouldBeEqual(BigInteger.ONE, BigInteger.TEN, info.representation()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> numbersWithComparatorComparisonStrategy.assertEqual(someInfo(), null, BigInteger.ONE)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_big_integers_are_equal_according_to_custom_comparison_strategy() {
        numbersWithComparatorComparisonStrategy.assertEqual(TestData.someInfo(), BigInteger.ONE, BigInteger.ONE);
    }

    @Test
    public void should_fail_if_big_integers_are_not_equal_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        try {
            numbersWithComparatorComparisonStrategy.assertEqual(info, BigInteger.TEN, BigInteger.ONE);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqual.shouldBeEqual(BigInteger.TEN, BigInteger.ONE, comparatorComparisonStrategy, new StandardRepresentation()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

