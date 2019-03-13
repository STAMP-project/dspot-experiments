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
package org.assertj.core.internal.floats;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldBeLess;
import org.assertj.core.internal.FloatsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Floats#assertLessThan(AssertionInfo, Float, float)}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class Floats_assertLessThan_Test extends FloatsBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> floats.assertLessThan(someInfo(), null, 8.0F)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_less_than_other() {
        floats.assertLessThan(TestData.someInfo(), 6.0F, 8.0F);
    }

    @Test
    public void should_fail_if_actual_is_equal_to_other() {
        AssertionInfo info = TestData.someInfo();
        try {
            floats.assertLessThan(info, 6.0F, 6.0F);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeLess.shouldBeLess(6.0F, 6.0F));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_greater_than_other() {
        AssertionInfo info = TestData.someInfo();
        try {
            floats.assertLessThan(info, 8.0F, 6.0F);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeLess.shouldBeLess(8.0F, 6.0F));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> floatsWithAbsValueComparisonStrategy.assertLessThan(someInfo(), null, 8.0F)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_less_than_other_according_to_custom_comparison_strategy() {
        floatsWithAbsValueComparisonStrategy.assertLessThan(TestData.someInfo(), 6.0F, (-8.0F));
    }

    @Test
    public void should_fail_if_actual_is_equal_to_other_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        try {
            floatsWithAbsValueComparisonStrategy.assertLessThan(info, 6.0F, (-6.0F));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeLess.shouldBeLess(6.0F, (-6.0F), absValueComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_greater_than_other_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        try {
            floatsWithAbsValueComparisonStrategy.assertLessThan(info, (-8.0F), 6.0F);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeLess.shouldBeLess((-8.0F), 6.0F, absValueComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

