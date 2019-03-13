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
package org.assertj.core.internal.bytes;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Percentage;
import org.assertj.core.error.ShouldNotBeEqualWithinPercentage;
import org.assertj.core.internal.BytesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Bytes_assertIsNotCloseToPercentage_Test extends BytesBaseTest {
    private static final Byte ZERO = 0;

    private static final Byte ONE = 1;

    private static final Byte TEN = 10;

    private static final Byte ONE_HUNDRED = 100;

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> bytes.assertIsNotCloseToPercentage(someInfo(), null, ONE, withPercentage(ONE))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_expected_value_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> bytes.assertIsNotCloseToPercentage(someInfo(), ONE, null, withPercentage(ONE)));
    }

    @Test
    public void should_fail_if_percentage_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> bytes.assertIsNotCloseToPercentage(someInfo(), ONE, ZERO, null));
    }

    @Test
    public void should_fail_if_percentage_is_negative() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> bytes.assertIsNotCloseToPercentage(someInfo(), ONE, ZERO, withPercentage((-1))));
    }

    @Test
    public void should_fail_if_actual_is_too_close_to_expected_value() {
        AssertionInfo info = TestData.someInfo();
        try {
            bytes.assertIsNotCloseToPercentage(TestData.someInfo(), Bytes_assertIsNotCloseToPercentage_Test.ONE, Bytes_assertIsNotCloseToPercentage_Test.TEN, Percentage.withPercentage(Bytes_assertIsNotCloseToPercentage_Test.ONE_HUNDRED));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeEqualWithinPercentage.shouldNotBeEqualWithinPercentage(Bytes_assertIsNotCloseToPercentage_Test.ONE, Bytes_assertIsNotCloseToPercentage_Test.TEN, Assertions.withinPercentage(100), ((Bytes_assertIsNotCloseToPercentage_Test.TEN) - (Bytes_assertIsNotCloseToPercentage_Test.ONE))));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

