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
import org.assertj.core.data.Percentage;
import org.assertj.core.error.ShouldNotBeEqualWithinPercentage;
import org.assertj.core.internal.DoublesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Doubles_assertIsNotCloseToPercentage_Test extends DoublesBaseTest {
    private static final Double ZERO = 0.0;

    private static final Double ONE = 1.0;

    private static final Double TEN = 10.0;

    private static final Double ONE_HUNDRED = 100.0;

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> doubles.assertIsNotCloseToPercentage(someInfo(), null, ONE, withPercentage(ONE))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_expected_value_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> doubles.assertIsNotCloseToPercentage(someInfo(), ONE, null, withPercentage(ONE)));
    }

    @Test
    public void should_fail_if_percentage_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> doubles.assertIsNotCloseToPercentage(someInfo(), ONE, ZERO, null));
    }

    @Test
    public void should_fail_if_percentage_is_negative() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> doubles.assertIsNotCloseToPercentage(someInfo(), ONE, ZERO, withPercentage((-1.0))));
    }

    @Test
    public void should_fail_if_actual_is_too_close_to_expected_value() {
        AssertionInfo info = TestData.someInfo();
        try {
            doubles.assertIsNotCloseToPercentage(TestData.someInfo(), Doubles_assertIsNotCloseToPercentage_Test.ONE, Doubles_assertIsNotCloseToPercentage_Test.TEN, Percentage.withPercentage(Doubles_assertIsNotCloseToPercentage_Test.ONE_HUNDRED));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeEqualWithinPercentage.shouldNotBeEqualWithinPercentage(Doubles_assertIsNotCloseToPercentage_Test.ONE, Doubles_assertIsNotCloseToPercentage_Test.TEN, Assertions.withinPercentage(100), ((Doubles_assertIsNotCloseToPercentage_Test.TEN) - (Doubles_assertIsNotCloseToPercentage_Test.ONE))));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_and_expected_are_NaN() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> doubles.assertIsNotCloseToPercentage(someInfo(), Double.NaN, Double.NaN, withPercentage(ONE)));
    }

    @Test
    public void should_fail_if_actual_and_expected_are_POSITIVE_INFINITY() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> doubles.assertIsNotCloseToPercentage(someInfo(), Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, withPercentage(ONE)));
    }

    @Test
    public void should_fail_if_actual_and_expected_are_NEGATIVE_INFINITY() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> doubles.assertIsNotCloseToPercentage(someInfo(), Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, withPercentage(ONE)));
    }

    @Test
    public void should_pass_if_actual_is_POSITIVE_INFINITY_and_expected_is_not() {
        doubles.assertIsNotCloseToPercentage(TestData.someInfo(), Double.POSITIVE_INFINITY, Doubles_assertIsNotCloseToPercentage_Test.ONE, Percentage.withPercentage(Doubles_assertIsNotCloseToPercentage_Test.ONE));
    }

    @Test
    public void should_pass_if_actual_is_POSITIVE_INFINITY_and_expected_is_NEGATIVE_INFINITY() {
        doubles.assertIsNotCloseToPercentage(TestData.someInfo(), Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Percentage.withPercentage(Doubles_assertIsNotCloseToPercentage_Test.ONE));
    }

    @Test
    public void should_pass_if_actual_is_NEGATIVE_INFINITY_and_expected_is_not() {
        doubles.assertIsNotCloseToPercentage(TestData.someInfo(), Double.NEGATIVE_INFINITY, Doubles_assertIsNotCloseToPercentage_Test.ONE, Percentage.withPercentage(Doubles_assertIsNotCloseToPercentage_Test.ONE));
    }

    @Test
    public void should_pass_if_actual_is_NEGATIVE_INFINITY_and_expected_is_POSITIVE_INFINITY() {
        doubles.assertIsNotCloseToPercentage(TestData.someInfo(), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Percentage.withPercentage(Doubles_assertIsNotCloseToPercentage_Test.ONE));
    }
}

