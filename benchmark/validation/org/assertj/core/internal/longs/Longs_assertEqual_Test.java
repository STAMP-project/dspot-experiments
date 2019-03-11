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
import org.assertj.core.error.ShouldBeEqual;
import org.assertj.core.internal.LongsBaseTest;
import org.assertj.core.presentation.StandardRepresentation;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Longs#assertEqual(AssertionInfo, Long, long)}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class Longs_assertEqual_Test extends LongsBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> longs.assertEqual(someInfo(), null, 8L)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_longs_are_equal() {
        longs.assertEqual(TestData.someInfo(), 8L, 8L);
    }

    @Test
    public void should_fail_if_longs_are_not_equal() {
        AssertionInfo info = TestData.someInfo();
        try {
            longs.assertEqual(info, 6L, 8L);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqual.shouldBeEqual(6L, 8L, info.representation()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> longsWithAbsValueComparisonStrategy.assertEqual(someInfo(), null, 8L)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_longs_are_equal_according_to_custom_comparison_strategy() {
        longsWithAbsValueComparisonStrategy.assertEqual(TestData.someInfo(), 8L, (-8L));
    }

    @Test
    public void should_fail_if_longs_are_not_equal_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        try {
            longsWithAbsValueComparisonStrategy.assertEqual(info, 6L, 8L);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqual.shouldBeEqual(6L, 8L, absValueComparisonStrategy, new StandardRepresentation()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

