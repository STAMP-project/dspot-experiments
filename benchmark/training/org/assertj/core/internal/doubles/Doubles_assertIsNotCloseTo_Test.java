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
import org.assertj.core.error.ShouldNotBeEqualWithinOffset;
import org.assertj.core.internal.DoublesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Doubles_assertIsNotCloseTo_Test extends DoublesBaseTest {
    private static final Double ZERO = 0.0;

    private static final Double ONE = 1.0;

    private static final Double TWO = 2.0;

    private static final Double THREE = 3.0;

    private static final Double TEN = 10.0;

    @Test
    public void should_pass_if_difference_is_more_than_given_offset() {
        doubles.assertIsNotCloseTo(TestData.someInfo(), Doubles_assertIsNotCloseTo_Test.ONE, Doubles_assertIsNotCloseTo_Test.THREE, Assertions.byLessThan(Doubles_assertIsNotCloseTo_Test.ONE));
        doubles.assertIsNotCloseTo(TestData.someInfo(), Doubles_assertIsNotCloseTo_Test.ONE, Doubles_assertIsNotCloseTo_Test.THREE, Assertions.within(Doubles_assertIsNotCloseTo_Test.ONE));
        doubles.assertIsNotCloseTo(TestData.someInfo(), Doubles_assertIsNotCloseTo_Test.ONE, Doubles_assertIsNotCloseTo_Test.TEN, Assertions.byLessThan(Doubles_assertIsNotCloseTo_Test.TWO));
        doubles.assertIsNotCloseTo(TestData.someInfo(), Doubles_assertIsNotCloseTo_Test.ONE, Doubles_assertIsNotCloseTo_Test.TEN, Assertions.within(Doubles_assertIsNotCloseTo_Test.TWO));
    }

    @Test
    public void should_pass_if_difference_is_equal_to_the_given_strict_offset() {
        doubles.assertIsNotCloseTo(TestData.someInfo(), Doubles_assertIsNotCloseTo_Test.ONE, Doubles_assertIsNotCloseTo_Test.TWO, Assertions.byLessThan(Doubles_assertIsNotCloseTo_Test.ONE));
        doubles.assertIsNotCloseTo(TestData.someInfo(), Doubles_assertIsNotCloseTo_Test.TWO, Doubles_assertIsNotCloseTo_Test.ONE, Assertions.byLessThan(Doubles_assertIsNotCloseTo_Test.ONE));
    }

    @Test
    public void should_pass_if_actual_is_POSITIVE_INFINITY_and_expected_is_not() {
        doubles.assertIsNotCloseTo(TestData.someInfo(), Double.POSITIVE_INFINITY, Doubles_assertIsNotCloseTo_Test.ONE, Assertions.byLessThan(Doubles_assertIsNotCloseTo_Test.ONE));
        doubles.assertIsNotCloseTo(TestData.someInfo(), Double.POSITIVE_INFINITY, Doubles_assertIsNotCloseTo_Test.ONE, Assertions.within(Doubles_assertIsNotCloseTo_Test.ONE));
    }

    @Test
    public void should_pass_if_actual_is_POSITIVE_INFINITY_and_expected_is_NEGATIVE_INFINITY() {
        doubles.assertIsNotCloseTo(TestData.someInfo(), Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Assertions.byLessThan(Doubles_assertIsNotCloseTo_Test.ONE));
        doubles.assertIsNotCloseTo(TestData.someInfo(), Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Assertions.within(Doubles_assertIsNotCloseTo_Test.ONE));
    }

    @Test
    public void should_pass_if_actual_is_NEGATIVE_INFINITY_and_expected_is_not() {
        doubles.assertIsNotCloseTo(TestData.someInfo(), Double.NEGATIVE_INFINITY, Doubles_assertIsNotCloseTo_Test.ONE, Assertions.byLessThan(Doubles_assertIsNotCloseTo_Test.ONE));
        doubles.assertIsNotCloseTo(TestData.someInfo(), Double.NEGATIVE_INFINITY, Doubles_assertIsNotCloseTo_Test.ONE, Assertions.within(Doubles_assertIsNotCloseTo_Test.ONE));
    }

    @Test
    public void should_pass_if_actual_is_NEGATIVE_INFINITY_and_expected_is_POSITIVE_INFINITY() {
        doubles.assertIsNotCloseTo(TestData.someInfo(), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Assertions.byLessThan(Doubles_assertIsNotCloseTo_Test.ONE));
        doubles.assertIsNotCloseTo(TestData.someInfo(), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Assertions.within(Doubles_assertIsNotCloseTo_Test.ONE));
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> doubles.assertIsNotCloseTo(someInfo(), null, ONE, byLessThan(ONE))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_expected_value_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> doubles.assertIsNotCloseTo(someInfo(), ONE, null, byLessThan(ONE)));
    }

    @Test
    public void should_fail_if_offset_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> doubles.assertIsNotCloseTo(someInfo(), ONE, ZERO, null));
    }

    @Test
    public void should_fail_if_actual_is_too_close_to_expected_value() {
        AssertionInfo info = TestData.someInfo();
        try {
            doubles.assertIsNotCloseTo(info, Doubles_assertIsNotCloseTo_Test.ONE, Doubles_assertIsNotCloseTo_Test.TWO, Assertions.within(Doubles_assertIsNotCloseTo_Test.TEN));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeEqualWithinOffset.shouldNotBeEqual(Doubles_assertIsNotCloseTo_Test.ONE, Doubles_assertIsNotCloseTo_Test.TWO, Assertions.within(Doubles_assertIsNotCloseTo_Test.TEN), ((Doubles_assertIsNotCloseTo_Test.TWO) - (Doubles_assertIsNotCloseTo_Test.ONE))));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_too_close_to_expected_value_with_strict_offset() {
        AssertionInfo info = TestData.someInfo();
        try {
            doubles.assertIsNotCloseTo(info, Doubles_assertIsNotCloseTo_Test.ONE, Doubles_assertIsNotCloseTo_Test.TWO, Assertions.byLessThan(Doubles_assertIsNotCloseTo_Test.TEN));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeEqualWithinOffset.shouldNotBeEqual(Doubles_assertIsNotCloseTo_Test.ONE, Doubles_assertIsNotCloseTo_Test.TWO, Assertions.byLessThan(Doubles_assertIsNotCloseTo_Test.TEN), ((Doubles_assertIsNotCloseTo_Test.TWO) - (Doubles_assertIsNotCloseTo_Test.ONE))));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_and_expected_are_NaN() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> doubles.assertIsNotCloseTo(someInfo(), Double.NaN, Double.NaN, within(ONE)));
    }

    @Test
    public void should_fail_if_actual_and_expected_are_POSITIVE_INFINITY() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> doubles.assertIsNotCloseTo(someInfo(), Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, within(ONE)));
    }

    @Test
    public void should_fail_if_actual_and_expected_are_NEGATIVE_INFINITY() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> doubles.assertIsNotCloseTo(someInfo(), Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, within(ONE)));
    }
}

