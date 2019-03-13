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
import org.assertj.core.error.ShouldBeLessOrEqual;
import org.assertj.core.internal.LongsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Longs#assertLessThanOrEqualTo(AssertionInfo, Long, long)}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class Longs_assertLessThanOrEqualTo_Test extends LongsBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> longs.assertLessThanOrEqualTo(someInfo(), null, 8L)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_less_than_other() {
        longs.assertLessThanOrEqualTo(TestData.someInfo(), 6L, 8L);
    }

    @Test
    public void should_pass_if_actual_is_equal_to_other() {
        longs.assertLessThanOrEqualTo(TestData.someInfo(), 6L, 6L);
    }

    @Test
    public void should_fail_if_actual_is_greater_than_other() {
        AssertionInfo info = TestData.someInfo();
        try {
            longs.assertLessThanOrEqualTo(info, 8L, 6L);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeLessOrEqual.shouldBeLessOrEqual(8L, 6L));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> longsWithAbsValueComparisonStrategy.assertLessThanOrEqualTo(someInfo(), null, 8L)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_less_than_other_according_to_custom_comparison_strategy() {
        longsWithAbsValueComparisonStrategy.assertLessThanOrEqualTo(TestData.someInfo(), 6L, (-8L));
    }

    @Test
    public void should_pass_if_actual_is_equal_to_other_according_to_custom_comparison_strategy() {
        longsWithAbsValueComparisonStrategy.assertLessThanOrEqualTo(TestData.someInfo(), 6L, (-6L));
    }

    @Test
    public void should_fail_if_actual_is_greater_than_other_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        try {
            longsWithAbsValueComparisonStrategy.assertLessThanOrEqualTo(info, (-8L), 6L);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeLessOrEqual.shouldBeLessOrEqual((-8L), 6L, absValueComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

