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
package org.assertj.core.internal.shorts;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Percentage;
import org.assertj.core.error.ShouldBeEqualWithinPercentage;
import org.assertj.core.internal.ShortsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Shorts_assertIsCloseToPercentage_Test extends ShortsBaseTest {
    private static final Short ZERO = ((short) (0));

    private static final Short ONE = ((short) (1));

    private static final Short TEN = ((short) (10));

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> shorts.assertIsCloseToPercentage(someInfo(), null, ONE, withPercentage(ONE))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_expected_value_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> shorts.assertIsCloseToPercentage(someInfo(), ONE, null, withPercentage(ONE)));
    }

    @Test
    public void should_fail_if_percentage_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> shorts.assertIsCloseToPercentage(someInfo(), ONE, ZERO, null));
    }

    @Test
    public void should_fail_if_percentage_is_negative() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> shorts.assertIsCloseToPercentage(someInfo(), ONE, ZERO, withPercentage(((short) (-1)))));
    }

    @Test
    public void should_fail_if_actual_is_not_close_enough_to_expected_value() {
        AssertionInfo info = TestData.someInfo();
        try {
            shorts.assertIsCloseToPercentage(TestData.someInfo(), Shorts_assertIsCloseToPercentage_Test.ONE, Shorts_assertIsCloseToPercentage_Test.TEN, Percentage.withPercentage(Shorts_assertIsCloseToPercentage_Test.TEN));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqualWithinPercentage.shouldBeEqualWithinPercentage(Shorts_assertIsCloseToPercentage_Test.ONE, Shorts_assertIsCloseToPercentage_Test.TEN, Assertions.withinPercentage(10), ((short) ((Shorts_assertIsCloseToPercentage_Test.TEN) - (Shorts_assertIsCloseToPercentage_Test.ONE)))));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

