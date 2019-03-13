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
import org.assertj.core.error.ShouldBeEqualWithinPercentage;
import org.assertj.core.internal.FloatsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Floats_assertIsCloseToPercentage_Test extends FloatsBaseTest {
    private static final Float ZERO = 0.0F;

    private static final Float ONE = 1.0F;

    private static final Float TEN = 10.0F;

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> floats.assertIsCloseToPercentage(someInfo(), null, ONE, withPercentage(ONE))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_expected_value_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> floats.assertIsCloseToPercentage(someInfo(), ONE, null, withPercentage(ONE)));
    }

    @Test
    public void should_fail_if_percentage_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> floats.assertIsCloseToPercentage(someInfo(), ONE, ZERO, null));
    }

    @Test
    public void should_fail_if_percentage_is_negative() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> floats.assertIsCloseToPercentage(someInfo(), ONE, ZERO, withPercentage((-1.0F))));
    }

    @Test
    public void should_fail_if_actual_is_not_close_enough_to_expected_value() {
        AssertionInfo info = TestData.someInfo();
        try {
            floats.assertIsCloseToPercentage(TestData.someInfo(), Floats_assertIsCloseToPercentage_Test.ONE, Floats_assertIsCloseToPercentage_Test.TEN, Percentage.withPercentage(Floats_assertIsCloseToPercentage_Test.TEN));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqualWithinPercentage.shouldBeEqualWithinPercentage(Floats_assertIsCloseToPercentage_Test.ONE, Floats_assertIsCloseToPercentage_Test.TEN, Assertions.withinPercentage(10), ((Floats_assertIsCloseToPercentage_Test.TEN) - (Floats_assertIsCloseToPercentage_Test.ONE))));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_NaN_and_expected_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> floats.assertIsCloseToPercentage(someInfo(), Float.NaN, ONE, withPercentage(ONE)));
    }

    @Test
    public void should_pass_if_actual_and_expected_are_NaN() {
        floats.assertIsCloseToPercentage(TestData.someInfo(), Float.NaN, Float.NaN, Percentage.withPercentage(Floats_assertIsCloseToPercentage_Test.ONE));
    }

    @Test
    public void should_fail_if_actual_is_POSITIVE_INFINITY_and_expected_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> floats.assertIsCloseToPercentage(someInfo(), Float.POSITIVE_INFINITY, ONE, withPercentage(ONE)));
    }

    @Test
    public void should_pass_if_actual_is_POSITIVE_INFINITY_and_expected_is_too() {
        floats.assertIsCloseToPercentage(TestData.someInfo(), Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Percentage.withPercentage(Floats_assertIsCloseToPercentage_Test.ONE));
    }

    @Test
    public void should_fail_if_actual_is_NEGATIVE_INFINITY_and_expected_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> floats.assertIsCloseToPercentage(someInfo(), Float.NEGATIVE_INFINITY, ONE, withPercentage(ONE)));
    }

    @Test
    public void should_pass_if_actual_is_NEGATIVE_INFINITY_and_expected_is_too() {
        floats.assertIsCloseToPercentage(TestData.someInfo(), Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Percentage.withPercentage(Floats_assertIsCloseToPercentage_Test.ONE));
    }

    @Test
    public void should_fail_if_actual_is_POSITIVE_INFINITY_and_expected_is_NEGATIVE_INFINITY() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> floats.assertIsCloseToPercentage(someInfo(), Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, withPercentage(ONE)));
    }

    @Test
    public void should_fail_if_actual_is_NEGATIVE_INFINITY_and_expected_is_POSITIVE_INFINITY() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> floats.assertIsCloseToPercentage(someInfo(), Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, withPercentage(ONE)));
    }
}

