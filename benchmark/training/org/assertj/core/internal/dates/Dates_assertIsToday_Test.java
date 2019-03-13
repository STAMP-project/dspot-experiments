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
import org.assertj.core.error.ShouldBeToday;
import org.assertj.core.internal.DatesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Dates#assertIsToday(AssertionInfo, Date)}</code>.
 *
 * @author Joel Costigliola
 */
public class Dates_assertIsToday_Test extends DatesBaseTest {
    @Test
    public void should_fail_if_actual_is_not_today() {
        AssertionInfo info = TestData.someInfo();
        try {
            actual = DatesBaseTest.parseDate("2111-01-01");
            dates.assertIsToday(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeToday.shouldBeToday(actual));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> dates.assertIsToday(someInfo(), null)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_today() {
        dates.assertIsToday(TestData.someInfo(), new Date());
    }

    @Test
    public void should_fail_if_actual_is_not_today_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        try {
            actual = DatesBaseTest.parseDate("2111-01-01");
            datesWithCustomComparisonStrategy.assertIsToday(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeToday.shouldBeToday(actual, yearAndMonthComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> datesWithCustomComparisonStrategy.assertIsToday(someInfo(), null)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_today_according_to_custom_comparison_strategy() {
        // we want actual to be different from today but still in the same month so that it is equal to today
        // according to our comparison strategy (that compares only month and year).
        // => if we are at the end of the month we subtract one day instead of adding one
        actual = ((monthOf(tomorrow())) == (monthOf(new Date()))) ? tomorrow() : yesterday();
        datesWithCustomComparisonStrategy.assertIsToday(TestData.someInfo(), actual);
    }
}

