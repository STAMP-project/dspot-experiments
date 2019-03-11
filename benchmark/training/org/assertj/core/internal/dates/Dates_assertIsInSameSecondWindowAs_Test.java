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
import org.assertj.core.error.ShouldBeInSameSecondWindow;
import org.assertj.core.internal.DatesBaseTest;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Dates#assertIsInSameSecondWindowAs(AssertionInfo, Date, Date)}</code>.
 *
 * @author Joel Costigliola
 */
public class Dates_assertIsInSameSecondWindowAs_Test extends DatesBaseTest {
    @Test
    public void should_pass_if_actual_is_in_same_second_window_as_given_date() {
        dates.assertIsInSameSecondWindowAs(TestData.someInfo(), actual, DatesBaseTest.parseDatetimeWithMs("2011-01-01T03:15:05.999"));
        dates.assertIsInSameSecondWindowAs(TestData.someInfo(), actual, DatesBaseTest.parseDatetimeWithMs("2011-01-01T03:15:05.001"));
        // in that test, the two dates have different seconds fields : 05 and 04 but their diff < 1s
        dates.assertIsInSameSecondWindowAs(TestData.someInfo(), actual, new Date(((actual.getTime()) - 1)));
    }

    @Test
    public void should_fail_if_actual_is_not_in_same_second_as_given_date() {
        AssertionInfo info = TestData.someInfo();
        Date other = DatesBaseTest.parseDatetime("2011-01-01T03:15:02");
        try {
            dates.assertIsInSameSecondWindowAs(info, actual, other);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeInSameSecondWindow.shouldBeInSameSecondWindow(actual, other));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_if_dates_time_difference_is_exactly_one_second() {
        AssertionInfo info = TestData.someInfo();
        Date other = DatesBaseTest.parseDatetime("2011-01-01T03:15:06");
        try {
            dates.assertIsInSameSecondWindowAs(info, actual, other);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeInSameSecondWindow.shouldBeInSameSecondWindow(actual, other));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> dates.assertIsInSameSecondWindowAs(someInfo(), null, new Date())).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_throw_error_if_given_date_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> dates.assertIsInSameSecondWindowAs(someInfo(), actual, null)).withMessage(ErrorMessages.dateToCompareActualWithIsNull());
    }

    @Test
    public void should_fail_if_actual_is_not_in_same_second_as_given_date_whatever_custom_comparison_strategy_is() {
        AssertionInfo info = TestData.someInfo();
        Date other = DatesBaseTest.parseDatetime("2011-01-01T03:15:02");
        try {
            datesWithCustomComparisonStrategy.assertIsInSameSecondWindowAs(info, actual, other);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeInSameSecondWindow.shouldBeInSameSecondWindow(actual, other));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> datesWithCustomComparisonStrategy.assertIsInSameSecondWindowAs(someInfo(), null, new Date())).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_throw_error_if_given_date_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> datesWithCustomComparisonStrategy.assertIsInSameSecondWindowAs(someInfo(), actual, null)).withMessage(ErrorMessages.dateToCompareActualWithIsNull());
    }

    @Test
    public void should_pass_if_actual_is_in_same_second_as_given_date_whatever_custom_comparison_strategy_is() {
        Date other = DatesBaseTest.parseDatetime("2011-01-01T03:15:05");
        datesWithCustomComparisonStrategy.assertIsInSameSecondWindowAs(TestData.someInfo(), actual, other);
        datesWithCustomComparisonStrategy.assertIsInSameSecondWindowAs(TestData.someInfo(), actual, new Date(((other.getTime()) + 999)));
    }
}

