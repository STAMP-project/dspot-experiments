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
package org.assertj.core.api.optionaldouble;


import java.util.OptionalDouble;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.BaseTest;
import org.assertj.core.data.Percentage;
import org.assertj.core.error.OptionalDoubleShouldHaveValueCloseToPercentage;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


public class OptionalDoubleAssert_hasValueCloseToPercentage_Test extends BaseTest {
    @Test
    public void should_fail_when_actual_is_null() {
        // GIVEN
        OptionalDouble actual = null;
        double expectedValue = 10;
        Percentage percentage = Assertions.withinPercentage(5);
        // THEN
        OptionalDoubleAssert_hasValueCloseToPercentage_Test.hasValueCloseToThrowsAssertionError(actual, expectedValue, percentage).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_when_actual_is_empty() {
        // GIVEN
        OptionalDouble actual = OptionalDouble.empty();
        double expectedValue = 10;
        Percentage percentage = Assertions.withinPercentage(5);
        // THEN
        String errorMessage = OptionalDoubleShouldHaveValueCloseToPercentage.shouldHaveValueCloseToPercentage(expectedValue).create();
        OptionalDoubleAssert_hasValueCloseToPercentage_Test.hasValueCloseToThrowsAssertionError(actual, expectedValue, percentage).withMessage(errorMessage);
    }

    @Test
    public void should_fail_when_expected_is_null() {
        // GIVEN
        OptionalDouble actual = OptionalDouble.of(5);
        Double expectedValue = null;
        Percentage percentage = Assertions.withinPercentage(5);
        // THEN
        Assertions.assertThatNullPointerException().isThrownBy(() -> assertThat(actual).hasValueCloseTo(expectedValue, percentage));
    }

    @Test
    public void should_fail_when_percentage_is_null() {
        // GIVEN
        OptionalDouble actual = OptionalDouble.of(5);
        double expectedValue = 5;
        Percentage percentage = null;
        // THEN
        Assertions.assertThatNullPointerException().isThrownBy(() -> assertThat(actual).hasValueCloseTo(expectedValue, percentage));
    }

    @Test
    public void should_fail_when_percentage_is_negative() {
        // GIVEN
        OptionalDouble actual = OptionalDouble.of(5);
        double expectedValue = 5;
        // THEN
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(actual).hasValueCloseTo(expectedValue, withPercentage((-5))));
    }

    @Test
    public void should_fail_if_actual_is_not_close_enough_to_expected_value() {
        // GIVEN
        OptionalDouble actual = OptionalDouble.of(1);
        double expectedValue = 10;
        Percentage percentage = Percentage.withPercentage(10);
        // THEN
        String errorMessage = OptionalDoubleShouldHaveValueCloseToPercentage.shouldHaveValueCloseToPercentage(actual, expectedValue, percentage, Math.abs((expectedValue - (actual.getAsDouble())))).create();
        OptionalDoubleAssert_hasValueCloseToPercentage_Test.hasValueCloseToThrowsAssertionError(actual, expectedValue, percentage).withMessage(errorMessage);
    }

    @Test
    public void should_fail_if_actual_is_NaN_and_expected_is_not() {
        // GIVEN
        OptionalDouble actual = OptionalDouble.of(Double.NaN);
        double expectedValue = 1;
        Percentage percentage = Percentage.withPercentage(10);
        // THEN
        String errorMessage = OptionalDoubleShouldHaveValueCloseToPercentage.shouldHaveValueCloseToPercentage(actual, expectedValue, percentage, Math.abs((expectedValue - (actual.getAsDouble())))).create();
        OptionalDoubleAssert_hasValueCloseToPercentage_Test.hasValueCloseToThrowsAssertionError(actual, expectedValue, percentage).withMessage(errorMessage);
    }

    @Test
    public void should_pass_if_actual_and_expected_are_NaN() {
        Assertions.assertThat(OptionalDouble.of(Double.NaN)).hasValueCloseTo(Double.NaN, Percentage.withPercentage(10));
    }

    @Test
    public void should_fail_if_actual_is_POSITIVE_INFINITY_and_expected_is_not() {
        // GIVEN
        OptionalDouble actual = OptionalDouble.of(Double.POSITIVE_INFINITY);
        double expectedValue = 1;
        Percentage percentage = Percentage.withPercentage(10);
        // THEN
        String errorMessage = OptionalDoubleShouldHaveValueCloseToPercentage.shouldHaveValueCloseToPercentage(actual, expectedValue, percentage, Math.abs((expectedValue - (actual.getAsDouble())))).create();
        OptionalDoubleAssert_hasValueCloseToPercentage_Test.hasValueCloseToThrowsAssertionError(actual, expectedValue, percentage).withMessage(errorMessage);
    }

    @Test
    public void should_pass_if_actual_and_expected_are_POSITIVE_INFINITY() {
        Assertions.assertThat(OptionalDouble.of(Double.POSITIVE_INFINITY)).hasValueCloseTo(Double.POSITIVE_INFINITY, Percentage.withPercentage(10));
    }

    @Test
    public void should_fail_if_actual_is_NEGATIVE_INFINITY_and_expected_is_not() {
        // GIVEN
        OptionalDouble actual = OptionalDouble.of(Double.NEGATIVE_INFINITY);
        double expectedValue = 1;
        Percentage percentage = Percentage.withPercentage(10);
        // THEN
        String errorMessage = OptionalDoubleShouldHaveValueCloseToPercentage.shouldHaveValueCloseToPercentage(actual, expectedValue, percentage, Math.abs((expectedValue - (actual.getAsDouble())))).create();
        OptionalDoubleAssert_hasValueCloseToPercentage_Test.hasValueCloseToThrowsAssertionError(actual, expectedValue, percentage).withMessage(errorMessage);
    }

    @Test
    public void should_pass_if_actual_and_expected_are_NEGATIVE_INFINITY() {
        Assertions.assertThat(OptionalDouble.of(Double.NEGATIVE_INFINITY)).hasValueCloseTo(Double.NEGATIVE_INFINITY, Percentage.withPercentage(10));
    }

    @Test
    public void should_fail_if_actual_is_POSITIVE_INFINITY_and_expected_is_NEGATIVE_INFINITY() {
        // GIVEN
        OptionalDouble actual = OptionalDouble.of(Double.POSITIVE_INFINITY);
        double expectedValue = Double.NEGATIVE_INFINITY;
        Percentage percentage = Percentage.withPercentage(10);
        // THEN
        String errorMessage = OptionalDoubleShouldHaveValueCloseToPercentage.shouldHaveValueCloseToPercentage(actual, expectedValue, percentage, Math.abs((expectedValue - (actual.getAsDouble())))).create();
        OptionalDoubleAssert_hasValueCloseToPercentage_Test.hasValueCloseToThrowsAssertionError(actual, expectedValue, percentage).withMessage(errorMessage);
    }

    @Test
    public void should_fail_if_actual_is_NEGATIVE_INFINITY_and_expected_is_POSITIVE_INFINITY() {
        // GIVEN
        OptionalDouble actual = OptionalDouble.of(Double.NEGATIVE_INFINITY);
        double expectedValue = Double.POSITIVE_INFINITY;
        Percentage percentage = Percentage.withPercentage(10);
        // THEN
        String errorMessage = OptionalDoubleShouldHaveValueCloseToPercentage.shouldHaveValueCloseToPercentage(actual, expectedValue, percentage, Math.abs((expectedValue - (actual.getAsDouble())))).create();
        OptionalDoubleAssert_hasValueCloseToPercentage_Test.hasValueCloseToThrowsAssertionError(actual, expectedValue, percentage).withMessage(errorMessage);
    }
}

