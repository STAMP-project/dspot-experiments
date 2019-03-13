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
import org.assertj.core.error.ShouldBeLessOrEqual;
import org.assertj.core.internal.ShortsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Shorts#assertLessThanOrEqualTo(AssertionInfo, Short, short)}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class Shorts_assertLessThanOrEqualTo_Test extends ShortsBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> shorts.assertLessThanOrEqualTo(someInfo(), null, ((short) (8)))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_less_than_other() {
        shorts.assertLessThanOrEqualTo(TestData.someInfo(), ((short) (6)), ((short) (8)));
    }

    @Test
    public void should_pass_if_actual_is_equal_to_other() {
        shorts.assertLessThanOrEqualTo(TestData.someInfo(), ((short) (6)), ((short) (6)));
    }

    @Test
    public void should_fail_if_actual_is_greater_than_other() {
        AssertionInfo info = TestData.someInfo();
        try {
            shorts.assertLessThanOrEqualTo(info, ((short) (8)), ((short) (6)));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeLessOrEqual.shouldBeLessOrEqual(((short) (8)), ((short) (6))));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> shortsWithAbsValueComparisonStrategy.assertLessThanOrEqualTo(someInfo(), null, ((short) (8)))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_less_than_other_according_to_custom_comparison_strategy() {
        shortsWithAbsValueComparisonStrategy.assertLessThanOrEqualTo(TestData.someInfo(), ((short) (6)), ((short) (-8)));
    }

    @Test
    public void should_pass_if_actual_is_equal_to_other_according_to_custom_comparison_strategy() {
        shortsWithAbsValueComparisonStrategy.assertLessThanOrEqualTo(TestData.someInfo(), ((short) (6)), ((short) (-6)));
    }

    @Test
    public void should_fail_if_actual_is_greater_than_other_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        try {
            shortsWithAbsValueComparisonStrategy.assertLessThanOrEqualTo(info, ((short) (-8)), ((short) (6)));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeLessOrEqual.shouldBeLessOrEqual(((short) (-8)), ((short) (6)), absValueComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

