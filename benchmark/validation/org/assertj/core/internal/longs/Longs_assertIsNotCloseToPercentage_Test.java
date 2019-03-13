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
package org.assertj.core.internal.longs;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Percentage;
import org.assertj.core.error.ShouldNotBeEqualWithinPercentage;
import org.assertj.core.internal.LongsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Longs_assertIsNotCloseToPercentage_Test extends LongsBaseTest {
    private static final Long ZERO = 0L;

    private static final Long ONE = 1L;

    private static final Long TEN = 10L;

    private static final Long ONE_HUNDRED = 100L;

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> longs.assertIsNotCloseToPercentage(someInfo(), null, ONE, withPercentage(ONE))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_expected_value_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> longs.assertIsNotCloseToPercentage(someInfo(), ONE, null, withPercentage(ONE)));
    }

    @Test
    public void should_fail_if_percentage_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> longs.assertIsNotCloseToPercentage(someInfo(), ONE, ZERO, null));
    }

    @Test
    public void should_fail_if_percentage_is_negative() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> longs.assertIsNotCloseToPercentage(someInfo(), ONE, ZERO, withPercentage((-1L))));
    }

    @Test
    public void should_fail_if_actual_is_too_close_to_expected_value() {
        AssertionInfo info = TestData.someInfo();
        try {
            longs.assertIsNotCloseToPercentage(TestData.someInfo(), Longs_assertIsNotCloseToPercentage_Test.ONE, Longs_assertIsNotCloseToPercentage_Test.TEN, Percentage.withPercentage(Longs_assertIsNotCloseToPercentage_Test.ONE_HUNDRED));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeEqualWithinPercentage.shouldNotBeEqualWithinPercentage(Longs_assertIsNotCloseToPercentage_Test.ONE, Longs_assertIsNotCloseToPercentage_Test.TEN, Assertions.withinPercentage(100), ((Longs_assertIsNotCloseToPercentage_Test.TEN) - (Longs_assertIsNotCloseToPercentage_Test.ONE))));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

