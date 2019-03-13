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
package org.assertj.core.internal.floats;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Percentage;
import org.assertj.core.error.ShouldNotBeEqualWithinPercentage;
import org.assertj.core.internal.FloatsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Floats_assertIsNotCloseToPercentage_Test extends FloatsBaseTest {
    private static final Float ZERO = 0.0F;

    private static final Float ONE = 1.0F;

    private static final Float TEN = 10.0F;

    private static final Float ONE_HUNDRED = 100.0F;

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> floats.assertIsNotCloseToPercentage(someInfo(), null, ONE, withPercentage(ONE))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_expected_value_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> floats.assertIsNotCloseToPercentage(someInfo(), ONE, null, withPercentage(ONE)));
    }

    @Test
    public void should_fail_if_percentage_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> floats.assertIsNotCloseToPercentage(someInfo(), ONE, ZERO, null));
    }

    @Test
    public void should_fail_if_percentage_is_negative() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> floats.assertIsNotCloseToPercentage(someInfo(), ONE, ZERO, withPercentage((-1.0F))));
    }

    @Test
    public void should_fail_if_actual_is_too_close_to_expected_value() {
        AssertionInfo info = TestData.someInfo();
        try {
            floats.assertIsNotCloseToPercentage(TestData.someInfo(), Floats_assertIsNotCloseToPercentage_Test.ONE, Floats_assertIsNotCloseToPercentage_Test.TEN, Percentage.withPercentage(Floats_assertIsNotCloseToPercentage_Test.ONE_HUNDRED));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeEqualWithinPercentage.shouldNotBeEqualWithinPercentage(Floats_assertIsNotCloseToPercentage_Test.ONE, Floats_assertIsNotCloseToPercentage_Test.TEN, Assertions.withinPercentage(100), ((Floats_assertIsNotCloseToPercentage_Test.TEN) - (Floats_assertIsNotCloseToPercentage_Test.ONE))));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_and_expected_are_NaN() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> floats.assertIsNotCloseToPercentage(someInfo(), Float.NaN, Float.NaN, withPercentage(ONE)));
    }

    @Test
    public void should_fail_if_actual_and_expected_are_POSITIVE_INFINITY() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> floats.assertIsNotCloseToPercentage(someInfo(), Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, withPercentage(ONE)));
    }

    @Test
    public void should_fail_if_actual_and_expected_are_NEGATIVE_INFINITY() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> floats.assertIsNotCloseToPercentage(someInfo(), Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, withPercentage(ONE)));
    }

    @Test
    public void should_pass_if_actual_is_POSITIVE_INFINITY_and_expected_is_not() {
        floats.assertIsNotCloseToPercentage(TestData.someInfo(), Float.POSITIVE_INFINITY, Floats_assertIsNotCloseToPercentage_Test.ONE, Percentage.withPercentage(Floats_assertIsNotCloseToPercentage_Test.ONE));
    }

    @Test
    public void should_pass_if_actual_is_POSITIVE_INFINITY_and_expected_is_NEGATIVE_INFINITY() {
        floats.assertIsNotCloseToPercentage(TestData.someInfo(), Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Percentage.withPercentage(Floats_assertIsNotCloseToPercentage_Test.ONE));
    }

    @Test
    public void should_pass_if_actual_is_NEGATIVE_INFINITY_and_expected_is_not() {
        floats.assertIsNotCloseToPercentage(TestData.someInfo(), Float.NEGATIVE_INFINITY, Floats_assertIsNotCloseToPercentage_Test.ONE, Percentage.withPercentage(Floats_assertIsNotCloseToPercentage_Test.ONE));
    }

    @Test
    public void should_pass_if_actual_is_NEGATIVE_INFINITY_and_expected_is_POSITIVE_INFINITY() {
        floats.assertIsNotCloseToPercentage(TestData.someInfo(), Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Percentage.withPercentage(Floats_assertIsNotCloseToPercentage_Test.ONE));
    }
}

