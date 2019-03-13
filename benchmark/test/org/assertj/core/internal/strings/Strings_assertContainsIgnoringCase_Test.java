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
package org.assertj.core.internal.strings;


import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldContainCharSequence;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.StringsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link Strings#assertContainsIgnoringCase(AssertionInfo, CharSequence, CharSequence)}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class Strings_assertContainsIgnoringCase_Test extends StringsBaseTest {
    @Test
    public void should_fail_if_actual_does_not_contain_sequence() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertContainsIgnoringCase(someInfo(), "Yoda", "Luke")).withMessage(ShouldContainCharSequence.shouldContainIgnoringCase("Yoda", "Luke").create());
    }

    @Test
    public void should_throw_error_if_sequence_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> strings.assertContainsIgnoringCase(someInfo(), "Yoda", null)).withMessage(ErrorMessages.charSequenceToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertContainsIgnoringCase(someInfo(), null, "Yoda")).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_contains_sequence() {
        strings.assertContainsIgnoringCase(TestData.someInfo(), "Yoda", "Yo");
    }

    @Test
    public void should_pass_if_actual_contains_sequence_in_different_case() {
        strings.assertContainsIgnoringCase(TestData.someInfo(), "Yoda", "yo");
    }

    @Test
    public void should_fail_if_actual_does_not_contain_sequence_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> stringsWithCaseInsensitiveComparisonStrategy.assertContainsIgnoringCase(someInfo(), "Yoda", "Luke")).withMessage(ShouldContainCharSequence.shouldContainIgnoringCase("Yoda", "Luke").create());
    }

    @Test
    public void should_throw_error_if_sequence_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> stringsWithCaseInsensitiveComparisonStrategy.assertContainsIgnoringCase(someInfo(), "Yoda", null)).withMessage(ErrorMessages.charSequenceToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> stringsWithCaseInsensitiveComparisonStrategy.assertContainsIgnoringCase(someInfo(), null, "Yoda")).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_contains_sequence_whatever_custom_comparison_strategy_is() {
        stringsWithCaseInsensitiveComparisonStrategy.assertContainsIgnoringCase(TestData.someInfo(), "Yoda", "Yo");
    }

    @Test
    public void should_pass_if_actual_contains_sequence_in_different_case_whatever_custom_comparison_strategy_is() {
        stringsWithCaseInsensitiveComparisonStrategy.assertContainsIgnoringCase(TestData.someInfo(), "Yoda", "yo");
    }
}

