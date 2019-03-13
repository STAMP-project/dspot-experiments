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
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldHaveDateField;
import org.assertj.core.internal.DatesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Dates#assertHasDayOfWeek(AssertionInfo, Date, int)}</code>.
 *
 * @author Joel Costigliola
 */
public class Dates_assertHasDayOfWeek_Test extends DatesBaseTest {
    @Test
    public void should_fail_if_actual_has_not_given_day_of_week() {
        AssertionInfo info = TestData.someInfo();
        int day_of_week = Calendar.SUNDAY;
        try {
            dates.assertHasDayOfWeek(info, actual, day_of_week);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldHaveDateField.shouldHaveDateField(actual, "day of week", day_of_week));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> dates.assertHasDayOfWeek(someInfo(), null, 1)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_has_given_day_of_week() {
        dates.assertHasDayOfWeek(TestData.someInfo(), actual, Calendar.SATURDAY);
    }

    @Test
    public void should_fail_if_actual_has_not_given_day_of_week_whatever_custom_comparison_strategy_is() {
        AssertionInfo info = TestData.someInfo();
        int day_of_week = Calendar.SUNDAY;
        try {
            datesWithCustomComparisonStrategy.assertHasDayOfWeek(info, actual, day_of_week);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldHaveDateField.shouldHaveDateField(actual, "day of week", day_of_week));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> datesWithCustomComparisonStrategy.assertHasDayOfWeek(someInfo(), null, 1)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_has_given_day_of_week_whatever_custom_comparison_strategy_is() {
        datesWithCustomComparisonStrategy.assertHasDayOfWeek(TestData.someInfo(), actual, Calendar.SATURDAY);
    }
}

