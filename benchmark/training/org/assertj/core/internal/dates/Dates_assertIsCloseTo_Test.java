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
import org.assertj.core.error.ShouldBeCloseTo;
import org.assertj.core.internal.DatesBaseTest;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Dates#assertIsCloseTo(AssertionInfo, Date, Date, long)}</code>.
 *
 * @author Joel Costigliola
 */
public class Dates_assertIsCloseTo_Test extends DatesBaseTest {
    private Date other;

    private int delta;

    @Test
    public void should_fail_if_actual_is_not_close_to_given_date_by_less_than_given_delta() {
        AssertionInfo info = TestData.someInfo();
        try {
            dates.assertIsCloseTo(info, actual, other, delta);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeCloseTo.shouldBeCloseTo(actual, other, delta, 101));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_throw_error_if_given_date_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> dates.assertIsCloseTo(someInfo(), actual, null, 10)).withMessage(ErrorMessages.dateToCompareActualWithIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> dates.assertIsCloseTo(someInfo(), null, parseDate("2010-01-01"), 10)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_close_to_given_date_by_less_than_given_delta() {
        dates.assertIsCloseTo(TestData.someInfo(), actual, DatesBaseTest.parseDatetime("2011-01-01T03:15:05"), delta);
    }

    @Test
    public void should_fail_if_actual_is_not_close_to_given_date_by_less_than_given_delta_whatever_custom_comparison_strategy_is() {
        AssertionInfo info = TestData.someInfo();
        try {
            datesWithCustomComparisonStrategy.assertIsCloseTo(info, actual, other, delta);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeCloseTo.shouldBeCloseTo(actual, other, delta, 101));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_throw_error_if_given_date_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> datesWithCustomComparisonStrategy.assertIsCloseTo(someInfo(), actual, null, 10)).withMessage(ErrorMessages.dateToCompareActualWithIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> datesWithCustomComparisonStrategy.assertIsCloseTo(someInfo(), null, parseDate("2010-01-01"), 10)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_close_to_given_date_by_less_than_given_delta_whatever_custom_comparison_strategy_is() {
        datesWithCustomComparisonStrategy.assertIsCloseTo(TestData.someInfo(), actual, DatesBaseTest.parseDatetime("2011-01-01T03:15:05"), delta);
    }
}

