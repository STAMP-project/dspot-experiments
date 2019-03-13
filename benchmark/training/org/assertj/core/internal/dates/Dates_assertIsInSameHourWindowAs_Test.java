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
import org.assertj.core.error.ShouldBeInSameHourWindow;
import org.assertj.core.internal.DatesBaseTest;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Dates#assertIsInSameHourWindowAs(AssertionInfo, Date, Date)}</code>.
 *
 * @author Joel Costigliola
 */
public class Dates_assertIsInSameHourWindowAs_Test extends DatesBaseTest {
    @Test
    public void should_pass_if_actual_is_in_same_hour_window_as_given_date() {
        dates.assertIsInSameHourWindowAs(TestData.someInfo(), actual, DatesBaseTest.parseDatetime("2011-01-01T03:59:02"));
        dates.assertIsInSameHourWindowAs(TestData.someInfo(), actual, DatesBaseTest.parseDatetime("2011-01-01T02:01:03"));
    }

    @Test
    public void should_fail_if_time_difference_is_exactly_one_hour() {
        AssertionInfo info = TestData.someInfo();
        Date other = DatesBaseTest.parseDatetime("2011-01-01T04:01:02");
        try {
            dates.assertIsInSameHourWindowAs(info, actual, other);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeInSameHourWindow.shouldBeInSameHourWindow(actual, other));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_not_in_same_hour_window_as_given_date() {
        AssertionInfo info = TestData.someInfo();
        Date other = DatesBaseTest.parseDatetime("2011-01-01T04:01:03");
        try {
            dates.assertIsInSameHourWindowAs(info, actual, other);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeInSameHourWindow.shouldBeInSameHourWindow(actual, other));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> dates.assertIsInSameHourWindowAs(someInfo(), null, new Date())).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_throw_error_if_given_date_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> dates.assertIsInSameHourWindowAs(someInfo(), actual, null)).withMessage(ErrorMessages.dateToCompareActualWithIsNull());
    }

    @Test
    public void should_fail_if_actual_is_not_in_same_hour_window_as_given_date_whatever_custom_comparison_strategy_is() {
        AssertionInfo info = TestData.someInfo();
        Date other = DatesBaseTest.parseDatetime("2011-01-01T04:01:03");
        try {
            datesWithCustomComparisonStrategy.assertIsInSameHourWindowAs(info, actual, other);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeInSameHourWindow.shouldBeInSameHourWindow(actual, other));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> datesWithCustomComparisonStrategy.assertIsInSameHourWindowAs(someInfo(), null, new Date())).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_throw_error_if_given_date_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> datesWithCustomComparisonStrategy.assertIsInSameHourWindowAs(someInfo(), actual, null)).withMessage(ErrorMessages.dateToCompareActualWithIsNull());
    }

    @Test
    public void should_pass_if_actual_is_in_same_hour_window_as_given_date_whatever_custom_comparison_strategy_is() {
        datesWithCustomComparisonStrategy.assertIsInSameHourWindowAs(TestData.someInfo(), actual, DatesBaseTest.parseDatetime("2011-01-01T03:59:02"));
        datesWithCustomComparisonStrategy.assertIsInSameHourWindowAs(TestData.someInfo(), actual, DatesBaseTest.parseDatetime("2011-01-01T04:01:00"));
    }
}

