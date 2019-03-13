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


import java.util.Calendar;
import java.util.Date;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldBeInTheFuture;
import org.assertj.core.internal.DatesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.DateUtil;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Dates#assertIsInTheFuture(AssertionInfo, Date)}</code>.
 *
 * @author Joel Costigliola
 */
public class Dates_assertIsInTheFuture_Test extends DatesBaseTest {
    @Test
    public void should_fail_if_actual_is_not_in_the_future() {
        AssertionInfo info = TestData.someInfo();
        try {
            dates.assertIsInTheFuture(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeInTheFuture.shouldBeInTheFuture(actual));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_today() {
        AssertionInfo info = TestData.someInfo();
        try {
            actual = new Date();
            dates.assertIsInTheFuture(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeInTheFuture.shouldBeInTheFuture(actual));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> dates.assertIsInTheFuture(someInfo(), null)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_in_the_future() {
        actual = DatesBaseTest.parseDate("2111-01-01");
        dates.assertIsInTheFuture(TestData.someInfo(), actual);
    }

    @Test
    public void should_fail_if_actual_is_not_in_the_future_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        try {
            datesWithCustomComparisonStrategy.assertIsInTheFuture(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeInTheFuture.shouldBeInTheFuture(actual, yearAndMonthComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_today_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        try {
            // we want actual to be different from today but still in the same month so that it is = today according to our
            // comparison strategy (that compares only month and year)
            // => if we are at the end of the month we subtract one day instead of adding one
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 1);
            Date tomorrow = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, (-2));
            Date yesterday = cal.getTime();
            actual = ((DateUtil.monthOf(tomorrow)) == (DateUtil.monthOf(new Date()))) ? tomorrow : yesterday;
            datesWithCustomComparisonStrategy.assertIsInTheFuture(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeInTheFuture.shouldBeInTheFuture(actual, yearAndMonthComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> datesWithCustomComparisonStrategy.assertIsInTheFuture(someInfo(), null)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_in_the_future_according_to_custom_comparison_strategy() {
        actual = DatesBaseTest.parseDate("2111-01-01");
        datesWithCustomComparisonStrategy.assertIsInTheFuture(TestData.someInfo(), actual);
    }
}

