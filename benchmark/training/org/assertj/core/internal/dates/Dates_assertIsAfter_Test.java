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
package org.assertj.core.internal.dates;


import java.util.Date;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldBeAfter;
import org.assertj.core.internal.DatesBaseTest;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Dates#assertIsAfter(AssertionInfo, java.util.Date, java.util.Date)}</code>.
 *
 * @author Joel Costigliola
 */
public class Dates_assertIsAfter_Test extends DatesBaseTest {
    @Test
    public void should_fail_if_actual_is_not_strictly_after_given_date() {
        AssertionInfo info = TestData.someInfo();
        Date other = DatesBaseTest.parseDate("2022-01-01");
        try {
            dates.assertIsAfter(info, actual, other);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeAfter.shouldBeAfter(actual, other));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_equals_to_given_date() {
        AssertionInfo info = TestData.someInfo();
        Date other = DatesBaseTest.parseDate("2011-01-15");
        try {
            dates.assertIsAfter(info, actual, other);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeAfter.shouldBeAfter(actual, other));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_throw_error_if_given_date_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> dates.assertIsAfter(someInfo(), actual, null)).withMessage(ErrorMessages.dateToCompareActualWithIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> dates.assertIsAfter(someInfo(), null, parseDate("2010-01-01"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_strictly_after_given_date() {
        dates.assertIsAfter(TestData.someInfo(), actual, DatesBaseTest.parseDate("2000-01-01"));
    }

    @Test
    public void should_fail_if_actual_is_not_strictly_after_given_date_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Date other = DatesBaseTest.parseDate("2022-01-01");
        try {
            datesWithCustomComparisonStrategy.assertIsAfter(info, actual, other);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeAfter.shouldBeAfter(actual, other, yearAndMonthComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_equals_to_given_date_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Date other = DatesBaseTest.parseDate("2011-01-31");
        try {
            datesWithCustomComparisonStrategy.assertIsAfter(info, actual, other);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeAfter.shouldBeAfter(actual, other, yearAndMonthComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_throw_error_if_given_date_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> datesWithCustomComparisonStrategy.assertIsAfter(someInfo(), actual, null)).withMessage(ErrorMessages.dateToCompareActualWithIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> datesWithCustomComparisonStrategy.assertIsAfter(someInfo(), null, parseDate("2010-01-01"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_strictly_after_given_date_according_to_custom_comparison_strategy() {
        datesWithCustomComparisonStrategy.assertIsAfter(TestData.someInfo(), actual, DatesBaseTest.parseDate("2000-01-01"));
    }
}

