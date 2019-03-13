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
import org.assertj.core.error.ShouldBeBetween;
import org.assertj.core.internal.DatesBaseTest;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Dates#assertIsBetween(AssertionInfo, Date, Date, Date, boolean, boolean)}</code>.
 *
 * @author Joel Costigliola
 */
public class Dates_assertIsBetween_Test extends DatesBaseTest {
    @Test
    public void should_fail_if_actual_is_not_between_given_period() {
        AssertionInfo info = TestData.someInfo();
        actual = DatesBaseTest.parseDate("2011-10-01");
        Date start = DatesBaseTest.parseDate("2011-09-01");
        Date end = DatesBaseTest.parseDate("2011-09-30");
        boolean inclusiveStart = true;
        boolean inclusiveEnd = true;
        try {
            dates.assertIsBetween(info, actual, start, end, inclusiveStart, inclusiveEnd);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeBetween.shouldBeBetween(actual, start, end, inclusiveStart, inclusiveEnd));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_equals_to_start_of_given_period_and_start_is_not_included_in_given_period() {
        AssertionInfo info = TestData.someInfo();
        actual = DatesBaseTest.parseDate("2011-09-01");
        Date start = DatesBaseTest.parseDate("2011-09-01");
        Date end = DatesBaseTest.parseDate("2011-09-30");
        boolean inclusiveStart = false;
        boolean inclusiveEnd = true;
        try {
            dates.assertIsBetween(info, actual, start, end, inclusiveStart, inclusiveEnd);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeBetween.shouldBeBetween(actual, start, end, inclusiveStart, inclusiveEnd));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_equals_to_end_of_given_period_and_end_is_not_included_in_given_period() {
        AssertionInfo info = TestData.someInfo();
        actual = DatesBaseTest.parseDate("2011-09-30");
        Date start = DatesBaseTest.parseDate("2011-09-01");
        Date end = DatesBaseTest.parseDate("2011-09-30");
        boolean inclusiveStart = true;
        boolean inclusiveEnd = false;
        try {
            dates.assertIsBetween(info, actual, start, end, inclusiveStart, inclusiveEnd);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeBetween.shouldBeBetween(actual, start, end, inclusiveStart, inclusiveEnd));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_throw_error_if_start_date_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            Date end = parseDate("2011-09-30");
            dates.assertIsBetween(someInfo(), actual, null, end, true, true);
        }).withMessage(ErrorMessages.startDateToCompareActualWithIsNull());
    }

    @Test
    public void should_throw_error_if_end_date_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            Date start = parseDate("2011-09-01");
            dates.assertIsBetween(someInfo(), actual, start, null, true, true);
        }).withMessage(ErrorMessages.endDateToCompareActualWithIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            Date start = parseDate("2011-09-01");
            Date end = parseDate("2011-09-30");
            dates.assertIsBetween(someInfo(), null, start, end, true, true);
        }).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_strictly_between_given_period() {
        Date start = DatesBaseTest.parseDate("2011-09-01");
        Date end = DatesBaseTest.parseDate("2011-09-30");
        dates.assertIsBetween(TestData.someInfo(), actual, start, end, false, false);
    }

    @Test
    public void should_pass_if_actual_is_equals_to_start_of_given_period_and_start_is_included_in_given_period() {
        actual = DatesBaseTest.parseDate("2011-09-01");
        Date start = DatesBaseTest.parseDate("2011-09-01");
        Date end = DatesBaseTest.parseDate("2011-09-30");
        dates.assertIsBetween(TestData.someInfo(), actual, start, end, true, false);
        dates.assertIsBetween(TestData.someInfo(), actual, start, end, true, true);
    }

    @Test
    public void should_pass_if_actual_is_equals_to_end_of_given_period_and_end_is_included_in_given_period() {
        actual = DatesBaseTest.parseDate("2011-09-30");
        Date start = DatesBaseTest.parseDate("2011-09-01");
        Date end = DatesBaseTest.parseDate("2011-09-30");
        dates.assertIsBetween(TestData.someInfo(), actual, start, end, false, true);
        dates.assertIsBetween(TestData.someInfo(), actual, start, end, true, true);
    }

    @Test
    public void should_fail_if_actual_is_not_between_given_period_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        actual = DatesBaseTest.parseDate("2011-10-01");
        Date start = DatesBaseTest.parseDate("2011-09-01");
        Date end = DatesBaseTest.parseDate("2011-09-30");
        boolean inclusiveStart = true;
        boolean inclusiveEnd = true;
        try {
            datesWithCustomComparisonStrategy.assertIsBetween(info, actual, start, end, inclusiveStart, inclusiveEnd);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeBetween.shouldBeBetween(actual, start, end, inclusiveStart, inclusiveEnd, yearAndMonthComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_equals_to_start_of_given_period_and_start_is_not_included_in_given_period_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        actual = DatesBaseTest.parseDate("2011-09-01");
        Date start = DatesBaseTest.parseDate("2011-09-30");// = 2011-09-01 according to comparison strategy

        Date end = DatesBaseTest.parseDate("2011-10-30");
        boolean inclusiveStart = false;
        boolean inclusiveEnd = true;
        try {
            datesWithCustomComparisonStrategy.assertIsBetween(info, actual, start, end, inclusiveStart, inclusiveEnd);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeBetween.shouldBeBetween(actual, start, end, inclusiveStart, inclusiveEnd, yearAndMonthComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_equals_to_end_of_given_period_and_end_is_not_included_in_given_period_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        actual = DatesBaseTest.parseDate("2011-09-01");
        Date start = DatesBaseTest.parseDate("2011-08-01");
        Date end = DatesBaseTest.parseDate("2011-09-30");// = 2011-09-01 according to comparison strategy

        boolean inclusiveStart = true;
        boolean inclusiveEnd = false;
        try {
            datesWithCustomComparisonStrategy.assertIsBetween(info, actual, start, end, inclusiveStart, inclusiveEnd);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeBetween.shouldBeBetween(actual, start, end, inclusiveStart, inclusiveEnd, yearAndMonthComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_throw_error_if_start_date_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            Date end = parseDate("2011-09-30");
            datesWithCustomComparisonStrategy.assertIsBetween(someInfo(), actual, null, end, true, true);
        }).withMessage(ErrorMessages.startDateToCompareActualWithIsNull());
    }

    @Test
    public void should_throw_error_if_end_date_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            Date start = parseDate("2011-09-01");
            datesWithCustomComparisonStrategy.assertIsBetween(someInfo(), actual, start, null, true, true);
        }).withMessage(ErrorMessages.endDateToCompareActualWithIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            Date start = parseDate("2011-09-01");
            Date end = parseDate("2011-09-30");
            datesWithCustomComparisonStrategy.assertIsBetween(someInfo(), null, start, end, true, true);
        }).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_strictly_between_given_period_according_to_custom_comparison_strategy() {
        Date start = DatesBaseTest.parseDate("2011-08-30");
        Date end = DatesBaseTest.parseDate("2011-10-01");
        datesWithCustomComparisonStrategy.assertIsBetween(TestData.someInfo(), actual, start, end, false, false);
    }

    @Test
    public void should_pass_if_actual_is_equals_to_start_of_given_period_and_start_is_included_in_given_period_according_to_custom_comparison_strategy() {
        actual = DatesBaseTest.parseDate("2011-09-01");
        Date start = DatesBaseTest.parseDate("2011-09-10");// = 2011-09-01 according to comparison strategy

        Date end = DatesBaseTest.parseDate("2011-10-01");
        datesWithCustomComparisonStrategy.assertIsBetween(TestData.someInfo(), actual, start, end, true, false);
        datesWithCustomComparisonStrategy.assertIsBetween(TestData.someInfo(), actual, start, end, true, true);
    }

    @Test
    public void should_pass_if_actual_is_equals_to_end_of_given_period_and_end_is_included_in_given_period_according_to_custom_comparison_strategy() {
        actual = DatesBaseTest.parseDate("2011-09-15");
        Date start = DatesBaseTest.parseDate("2011-08-30");
        Date end = DatesBaseTest.parseDate("2011-09-30");// = 2011-09-01 according to comparison strategy

        datesWithCustomComparisonStrategy.assertIsBetween(TestData.someInfo(), actual, start, end, false, true);
        datesWithCustomComparisonStrategy.assertIsBetween(TestData.someInfo(), actual, start, end, true, true);
    }
}

