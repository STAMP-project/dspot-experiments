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
import org.assertj.core.error.ShouldBeEqual;
import org.assertj.core.internal.FloatsBaseTest;
import org.assertj.core.presentation.StandardRepresentation;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Floats#assertEqual(AssertionInfo, Float, float)}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class Floats_assertEqual_Test extends FloatsBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> floats.assertEqual(someInfo(), null, 8.0F)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_floats_are_equal() {
        floats.assertEqual(TestData.someInfo(), 8.0F, 8.0F);
    }

    @Test
    public void should_fail_if_floats_are_not_equal() {
        AssertionInfo info = TestData.someInfo();
        try {
            floats.assertEqual(info, 6.0F, 8.0F);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqual.shouldBeEqual(6.0F, 8.0F, info.representation()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> floatsWithAbsValueComparisonStrategy.assertEqual(someInfo(), null, 8.0F)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_floats_are_equal_according_to_custom_comparison_strategy() {
        floatsWithAbsValueComparisonStrategy.assertEqual(TestData.someInfo(), (-8.0F), 8.0F);
    }

    @Test
    public void should_fail_if_floats_are_not_equal_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        try {
            floatsWithAbsValueComparisonStrategy.assertEqual(info, 6.0F, (-8.0F));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqual.shouldBeEqual(6.0F, (-8.0F), absValueComparisonStrategy, new StandardRepresentation()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

