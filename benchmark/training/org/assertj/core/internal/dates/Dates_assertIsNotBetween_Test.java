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
import org.assertj.core.error.ShouldNotBeBetween;
import org.assertj.core.internal.DatesBaseTest;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Dates#assertIsNotBetween(AssertionInfo, Date, Date, Date, boolean, boolean)}</code>.
 *
 * @author Joel Costigliola
 */
public class Dates_assertIsNotBetween_Test extends DatesBaseTest {
    @Test
    public void should_fail_if_actual_is_between_given_period() {
        AssertionInfo info = TestData.someInfo();
        Date start = DatesBaseTest.parseDate("2011-09-01");
        Date end = DatesBaseTest.parseDate("2011-09-30");
        boolean inclusiveStart = true;
        boolean inclusiveEnd = true;
        try {
            dates.assertIsNotBetween(info, actual, start, end, inclusiveStart, inclusiveEnd);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeBetween.shouldNotBeBetween(actual, start, end, inclusiveStart, inclusiveEnd));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_equals_to_start_of_given_period_and_start_is_included_in_given_period() {
        AssertionInfo info = TestData.someInfo();
        actual = DatesBaseTest.parseDate("2011-09-01");
        Date start = DatesBaseTest.parseDate("2011-09-01");
        Date end = DatesBaseTest.parseDate("2011-09-30");
        boolean inclusiveStart = true;
        boolean inclusiveEnd = false;
        try {
            dates.assertIsNotBetween(info, actual, start, end, inclusiveStart, inclusiveEnd);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeBetween.shouldNotBeBetween(actual, start, end, inclusiveStart, inclusiveEnd));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_equals_to_end_of_given_period_and_end_is_included_in_given_period() {
        AssertionInfo info = TestData.someInfo();
        actual = DatesBaseTest.parseDate("2011-09-30");
        Date start = DatesBaseTest.parseDate("2011-09-01");
        Date end = DatesBaseTest.parseDate("2011-09-30");
        boolean inclusiveStart = false;
        boolean inclusiveEnd = true;
        try {
            dates.assertIsNotBetween(info, actual, start, end, inclusiveStart, inclusiveEnd);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeBetween.shouldNotBeBetween(actual, start, end, inclusiveStart, inclusiveEnd));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_throw_error_if_start_date_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            Date end = parseDate("2011-09-30");
            dates.assertIsNotBetween(someInfo(), actual, null, end, true, true);
        }).withMessage(ErrorMessages.startDateToCompareActualWithIsNull());
    }

    @Test
    public void should_throw_error_if_end_date_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            Date start = parseDate("2011-09-01");
            dates.assertIsNotBetween(someInfo(), actual, start, null, true, true);
        }).withMessage(ErrorMessages.endDateToCompareActualWithIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            Date start = parseDate("2011-09-01");
            Date end = parseDate("2011-09-30");
            dates.assertIsNotBetween(someInfo(), null, start, end, true, true);
        }).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_not_between_given_period() {
        actual = DatesBaseTest.parseDate("2011-12-31");
        Date start = DatesBaseTest.parseDate("2011-09-01");
        Date end = DatesBaseTest.parseDate("2011-09-30");
        dates.assertIsNotBetween(TestData.someInfo(), actual, start, end, true, true);
    }

    @Test
    public void should_pass_if_actual_is_equals_to_start_of_given_period_and_start_is_not_included_in_given_period() {
        actual = DatesBaseTest.parseDate("2011-09-01");
        Date start = DatesBaseTest.parseDate("2011-09-01");
        Date end = DatesBaseTest.parseDate("2011-09-30");
        dates.assertIsNotBetween(TestData.someInfo(), actual, start, end, false, false);
        dates.assertIsNotBetween(TestData.someInfo(), actual, start, end, false, true);
    }

    @Test
    public void should_pass_if_actual_is_equals_to_end_of_given_period_and_end_is_not_included_in_given_period() {
        actual = DatesBaseTest.parseDate("2011-09-30");
        Date start = DatesBaseTest.parseDate("2011-09-01");
        Date end = DatesBaseTest.parseDate("2011-09-30");
        dates.assertIsNotBetween(TestData.someInfo(), actual, start, end, false, false);
        dates.assertIsNotBetween(TestData.someInfo(), actual, start, end, true, false);
    }

    @Test
    public void should_fail_if_actual_is_between_given_period_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Date start = DatesBaseTest.parseDate("2011-08-31");
        Date end = DatesBaseTest.parseDate("2011-09-30");
        boolean inclusiveStart = true;
        boolean inclusiveEnd = true;
        try {
            datesWithCustomComparisonStrategy.assertIsNotBetween(info, actual, start, end, inclusiveStart, inclusiveEnd);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeBetween.shouldNotBeBetween(actual, start, end, inclusiveStart, inclusiveEnd, yearAndMonthComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_equals_to_start_of_given_period_and_start_is_included_in_given_period_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        actual = DatesBaseTest.parseDate("2011-09-15");
        Date start = DatesBaseTest.parseDate("2011-09-01");// = 2011-09-15 according to comparison strategy

        Date end = DatesBaseTest.parseDate("2011-10-01");
        boolean inclusiveStart = true;
        boolean inclusiveEnd = false;
        try {
            datesWithCustomComparisonStrategy.assertIsNotBetween(info, actual, start, end, inclusiveStart, inclusiveEnd);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeBetween.shouldNotBeBetween(actual, start, end, inclusiveStart, inclusiveEnd, yearAndMonthComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_equals_to_end_of_given_period_and_end_is_included_in_given_period_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        actual = DatesBaseTest.parseDate("2011-09-15");
        Date start = DatesBaseTest.parseDate("2011-08-31");
        Date end = DatesBaseTest.parseDate("2011-09-30");// = 2011-09-15 according to comparison strategy

        boolean inclusiveStart = false;
        boolean inclusiveEnd = true;
        try {
            datesWithCustomComparisonStrategy.assertIsNotBetween(info, actual, start, end, inclusiveStart, inclusiveEnd);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeBetween.shouldNotBeBetween(actual, start, end, inclusiveStart, inclusiveEnd, yearAndMonthComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_throw_error_if_start_date_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            Date end = parseDate("2011-09-30");
            datesWithCustomComparisonStrategy.assertIsNotBetween(someInfo(), actual, null, end, true, true);
        }).withMessage(ErrorMessages.startDateToCompareActualWithIsNull());
    }

    @Test
    public void should_throw_error_if_end_date_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            Date start = parseDate("2011-09-01");
            datesWithCustomComparisonStrategy.assertIsNotBetween(someInfo(), actual, start, null, true, true);
        }).withMessage(ErrorMessages.endDateToCompareActualWithIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            Date start = parseDate("2011-09-01");
            Date end = parseDate("2011-09-30");
            datesWithCustomComparisonStrategy.assertIsNotBetween(someInfo(), null, start, end, true, true);
        }).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_not_between_given_period_according_to_custom_comparison_strategy() {
        actual = DatesBaseTest.parseDate("2011-12-31");
        Date start = DatesBaseTest.parseDate("2011-09-01");
        Date end = DatesBaseTest.parseDate("2011-11-30");
        datesWithCustomComparisonStrategy.assertIsNotBetween(TestData.someInfo(), actual, start, end, true, true);
    }

    @Test
    public void should_pass_if_actual_is_equals_to_start_of_given_period_and_start_is_not_included_in_given_period_according_to_custom_comparison_strategy() {
        actual = DatesBaseTest.parseDate("2011-09-01");
        Date start = DatesBaseTest.parseDate("2011-09-15");// = 2011-09-01 according to comparison strategy

        Date end = DatesBaseTest.parseDate("2011-09-30");
        datesWithCustomComparisonStrategy.assertIsNotBetween(TestData.someInfo(), actual, start, end, false, false);
        datesWithCustomComparisonStrategy.assertIsNotBetween(TestData.someInfo(), actual, start, end, false, true);
    }

    @Test
    public void should_pass_if_actual_is_equals_to_end_of_given_period_and_end_is_not_included_in_given_period_according_to_custom_comparison_strategy() {
        actual = DatesBaseTest.parseDate("2011-09-30");
        Date start = DatesBaseTest.parseDate("2011-09-01");
        Date end = DatesBaseTest.parseDate("2011-09-15");// = 2011-09-30 according to comparison strategy

        datesWithCustomComparisonStrategy.assertIsNotBetween(TestData.someInfo(), actual, start, end, false, false);
        datesWithCustomComparisonStrategy.assertIsNotBetween(TestData.someInfo(), actual, start, end, true, false);
    }
}

