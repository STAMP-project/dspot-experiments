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
package org.assertj.core.internal.characters;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldBeLessOrEqual;
import org.assertj.core.internal.CharactersBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Characters#assertLessThanOrEqualTo(AssertionInfo, Character, char)}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class Characters_assertLessThanOrEqualTo_Test extends CharactersBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> characters.assertLessThanOrEqualTo(someInfo(), null, 'a')).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_less_than_other() {
        characters.assertLessThanOrEqualTo(TestData.someInfo(), 'a', 'b');
    }

    @Test
    public void should_pass_if_actual_is_equal_to_other() {
        characters.assertLessThanOrEqualTo(TestData.someInfo(), 'b', 'b');
    }

    @Test
    public void should_fail_if_actual_is_greater_than_other() {
        AssertionInfo info = TestData.someInfo();
        try {
            characters.assertLessThanOrEqualTo(info, 'b', 'a');
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeLessOrEqual.shouldBeLessOrEqual('b', 'a'));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_null_according_to_custom_comparison_strategy() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> charactersWithCaseInsensitiveComparisonStrategy.assertLessThanOrEqualTo(someInfo(), null, 'a')).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_less_than_other_according_to_custom_comparison_strategy() {
        charactersWithCaseInsensitiveComparisonStrategy.assertLessThanOrEqualTo(TestData.someInfo(), 'a', 'B');
    }

    @Test
    public void should_pass_if_actual_is_equal_to_other_according_to_custom_comparison_strategy() {
        charactersWithCaseInsensitiveComparisonStrategy.assertLessThanOrEqualTo(TestData.someInfo(), 'b', 'B');
    }

    @Test
    public void should_fail_if_actual_is_greater_than_other_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        try {
            charactersWithCaseInsensitiveComparisonStrategy.assertLessThanOrEqualTo(info, 'B', 'a');
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeLessOrEqual.shouldBeLessOrEqual('B', 'a', caseInsensitiveComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

