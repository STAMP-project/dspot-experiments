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
import org.assertj.core.error.ShouldBeBeforeOrEqualsTo;
import org.assertj.core.internal.DatesBaseTest;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Dates#assertIsBeforeOrEqualsTo(AssertionInfo, Date, Date)}</code>.
 *
 * @author Joel Costigliola
 */
public class Dates_assertIsBeforeOrEqualsTo_Test extends DatesBaseTest {
    @Test
    public void should_fail_if_actual_is_not_strictly_before_given_date() {
        AssertionInfo info = TestData.someInfo();
        Date other = DatesBaseTest.parseDate("2000-01-01");
        try {
            dates.assertIsBeforeOrEqualsTo(info, actual, other);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeBeforeOrEqualsTo.shouldBeBeforeOrEqualsTo(actual, other));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_throw_error_if_given_date_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> dates.assertIsBeforeOrEqualsTo(someInfo(), actual, null)).withMessage(ErrorMessages.dateToCompareActualWithIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> dates.assertIsBeforeOrEqualsTo(someInfo(), null, parseDate("2010-01-01"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_strictly_before_given_date() {
        dates.assertIsBeforeOrEqualsTo(TestData.someInfo(), actual, DatesBaseTest.parseDate("2020-01-01"));
    }

    @Test
    public void should_pass_if_actual_is_equals_to_given_date() {
        dates.assertIsBeforeOrEqualsTo(TestData.someInfo(), actual, DatesBaseTest.parseDate("2011-01-01"));
    }

    @Test
    public void should_fail_if_actual_is_not_strictly_before_given_date_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Date other = DatesBaseTest.parseDate("2000-01-01");
        try {
            datesWithCustomComparisonStrategy.assertIsBeforeOrEqualsTo(info, actual, other);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeBeforeOrEqualsTo.shouldBeBeforeOrEqualsTo(actual, other, yearAndMonthComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_throw_error_if_given_date_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> datesWithCustomComparisonStrategy.assertIsBeforeOrEqualsTo(someInfo(), actual, null)).withMessage(ErrorMessages.dateToCompareActualWithIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> datesWithCustomComparisonStrategy.assertIsBeforeOrEqualsTo(someInfo(), null, parseDate("2010-01-01"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_strictly_before_given_date_according_to_custom_comparison_strategy() {
        datesWithCustomComparisonStrategy.assertIsBeforeOrEqualsTo(TestData.someInfo(), actual, DatesBaseTest.parseDate("2020-01-01"));
    }

    @Test
    public void should_pass_if_actual_is_equals_to_given_date_according_to_custom_comparison_strategy() {
        datesWithCustomComparisonStrategy.assertIsBeforeOrEqualsTo(TestData.someInfo(), actual, DatesBaseTest.parseDate("2011-01-31"));
    }
}

