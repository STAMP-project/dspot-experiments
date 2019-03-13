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
import org.assertj.core.error.ShouldNotBeEqualWithinOffset;
import org.assertj.core.internal.FloatsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Floats_assertIsNotCloseTo_Test extends FloatsBaseTest {
    private static final Float ZERO = 0.0F;

    private static final Float ONE = 1.0F;

    private static final Float TWO = 2.0F;

    private static final Float THREE = 3.0F;

    private static final Float TEN = 10.0F;

    @Test
    public void should_pass_if_difference_is_more_than_given_offset() {
        floats.assertIsNotCloseTo(TestData.someInfo(), Floats_assertIsNotCloseTo_Test.ONE, Floats_assertIsNotCloseTo_Test.THREE, Assertions.byLessThan(Floats_assertIsNotCloseTo_Test.ONE));
        floats.assertIsNotCloseTo(TestData.someInfo(), Floats_assertIsNotCloseTo_Test.ONE, Floats_assertIsNotCloseTo_Test.THREE, Assertions.within(Floats_assertIsNotCloseTo_Test.ONE));
        floats.assertIsNotCloseTo(TestData.someInfo(), Floats_assertIsNotCloseTo_Test.ONE, Floats_assertIsNotCloseTo_Test.TEN, Assertions.byLessThan(Floats_assertIsNotCloseTo_Test.TWO));
        floats.assertIsNotCloseTo(TestData.someInfo(), Floats_assertIsNotCloseTo_Test.ONE, Floats_assertIsNotCloseTo_Test.TEN, Assertions.within(Floats_assertIsNotCloseTo_Test.TWO));
    }

    @Test
    public void should_pass_if_difference_is_equal_to_the_given_strict_offset() {
        floats.assertIsNotCloseTo(TestData.someInfo(), Floats_assertIsNotCloseTo_Test.ONE, Floats_assertIsNotCloseTo_Test.TWO, Assertions.byLessThan(Floats_assertIsNotCloseTo_Test.ONE));
        floats.assertIsNotCloseTo(TestData.someInfo(), Floats_assertIsNotCloseTo_Test.TWO, Floats_assertIsNotCloseTo_Test.ONE, Assertions.byLessThan(Floats_assertIsNotCloseTo_Test.ONE));
    }

    @Test
    public void should_pass_if_actual_is_POSITIVE_INFINITY_and_expected_is_not() {
        floats.assertIsNotCloseTo(TestData.someInfo(), Float.POSITIVE_INFINITY, Floats_assertIsNotCloseTo_Test.ONE, Assertions.byLessThan(Floats_assertIsNotCloseTo_Test.ONE));
        floats.assertIsNotCloseTo(TestData.someInfo(), Float.POSITIVE_INFINITY, Floats_assertIsNotCloseTo_Test.ONE, Assertions.within(Floats_assertIsNotCloseTo_Test.ONE));
    }

    @Test
    public void should_pass_if_actual_is_POSITIVE_INFINITY_and_expected_is_NEGATIVE_INFINITY() {
        floats.assertIsNotCloseTo(TestData.someInfo(), Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Assertions.byLessThan(Floats_assertIsNotCloseTo_Test.ONE));
        floats.assertIsNotCloseTo(TestData.someInfo(), Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Assertions.within(Floats_assertIsNotCloseTo_Test.ONE));
    }

    @Test
    public void should_pass_if_actual_is_NEGATIVE_INFINITY_and_expected_is_not() {
        floats.assertIsNotCloseTo(TestData.someInfo(), Float.NEGATIVE_INFINITY, Floats_assertIsNotCloseTo_Test.ONE, Assertions.byLessThan(Floats_assertIsNotCloseTo_Test.ONE));
        floats.assertIsNotCloseTo(TestData.someInfo(), Float.NEGATIVE_INFINITY, Floats_assertIsNotCloseTo_Test.ONE, Assertions.within(Floats_assertIsNotCloseTo_Test.ONE));
    }

    @Test
    public void should_pass_if_actual_is_NEGATIVE_INFINITY_and_expected_is_POSITIVE_INFINITY() {
        floats.assertIsNotCloseTo(TestData.someInfo(), Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Assertions.byLessThan(Floats_assertIsNotCloseTo_Test.ONE));
        floats.assertIsNotCloseTo(TestData.someInfo(), Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Assertions.within(Floats_assertIsNotCloseTo_Test.ONE));
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> floats.assertIsNotCloseTo(someInfo(), null, ONE, byLessThan(ONE))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_expected_value_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> floats.assertIsNotCloseTo(someInfo(), ONE, null, byLessThan(ONE)));
    }

    @Test
    public void should_fail_if_offset_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> floats.assertIsNotCloseTo(someInfo(), ONE, ZERO, null));
    }

    @Test
    public void should_fail_if_actual_is_too_close_to_expected_value() {
        AssertionInfo info = TestData.someInfo();
        try {
            floats.assertIsNotCloseTo(info, Floats_assertIsNotCloseTo_Test.ONE, Floats_assertIsNotCloseTo_Test.TWO, Assertions.within(Floats_assertIsNotCloseTo_Test.TEN));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeEqualWithinOffset.shouldNotBeEqual(Floats_assertIsNotCloseTo_Test.ONE, Floats_assertIsNotCloseTo_Test.TWO, Assertions.within(Floats_assertIsNotCloseTo_Test.TEN), ((Floats_assertIsNotCloseTo_Test.TWO) - (Floats_assertIsNotCloseTo_Test.ONE))));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_too_close_to_expected_value_with_strict_offset() {
        AssertionInfo info = TestData.someInfo();
        try {
            floats.assertIsNotCloseTo(info, Floats_assertIsNotCloseTo_Test.ONE, Floats_assertIsNotCloseTo_Test.TWO, Assertions.byLessThan(Floats_assertIsNotCloseTo_Test.TEN));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeEqualWithinOffset.shouldNotBeEqual(Floats_assertIsNotCloseTo_Test.ONE, Floats_assertIsNotCloseTo_Test.TWO, Assertions.byLessThan(Floats_assertIsNotCloseTo_Test.TEN), ((Floats_assertIsNotCloseTo_Test.TWO) - (Floats_assertIsNotCloseTo_Test.ONE))));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_and_expected_are_NaN() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> floats.assertIsNotCloseTo(someInfo(), Float.NaN, Float.NaN, within(ONE)));
    }

    @Test
    public void should_fail_if_actual_and_expected_are_POSITIVE_INFINITY() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> floats.assertIsNotCloseTo(someInfo(), Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, within(ONE)));
    }

    @Test
    public void should_fail_if_actual_and_expected_are_NEGATIVE_INFINITY() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> floats.assertIsNotCloseTo(someInfo(), Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, within(ONE)));
    }
}

