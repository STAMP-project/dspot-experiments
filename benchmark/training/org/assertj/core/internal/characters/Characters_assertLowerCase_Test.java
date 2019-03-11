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
import org.assertj.core.error.ShouldBeLowerCase;
import org.assertj.core.internal.CharactersBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Characters#assertLowerCase(AssertionInfo, Character)}</code>.
 *
 * @author Yvonne Wang
 * @author Joel Costigliola
 */
public class Characters_assertLowerCase_Test extends CharactersBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> characters.assertLowerCase(someInfo(), null)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_lowercase() {
        characters.assertLowerCase(TestData.someInfo(), 'a');
    }

    @Test
    public void should_fail_if_actual_is_not_lowercase() {
        AssertionInfo info = TestData.someInfo();
        try {
            characters.assertLowerCase(info, 'A');
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeLowerCase.shouldBeLowerCase('A'));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> charactersWithCaseInsensitiveComparisonStrategy.assertLowerCase(someInfo(), null)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_lowercase_whatever_custom_comparison_strategy_is() {
        charactersWithCaseInsensitiveComparisonStrategy.assertLowerCase(TestData.someInfo(), 'a');
    }

    @Test
    public void should_fail_if_actual_is_not_lowercase_whatever_custom_comparison_strategy_is() {
        AssertionInfo info = TestData.someInfo();
        try {
            charactersWithCaseInsensitiveComparisonStrategy.assertLowerCase(info, 'A');
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeLowerCase.shouldBeLowerCase('A'));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

