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
import org.assertj.core.error.ShouldContainCharSequenceOnlyOnce;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.StringsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link Strings#assertContainsOnlyOnce(AssertionInfo, CharSequence, CharSequence)}</code>.
 */
public class Strings_assertContainsOnlyOnce_Test extends StringsBaseTest {
    @Test
    public void should_pass_if_actual_contains_given_string_only_once() {
        strings.assertContainsOnlyOnce(TestData.someInfo(), "Yoda", "Yo");
    }

    @Test
    public void should_fail_if_actual_contains_given_string_more_than_once() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertContainsOnlyOnce(someInfo(), "Yodayoda", "oda")).withMessage(ShouldContainCharSequenceOnlyOnce.shouldContainOnlyOnce("Yodayoda", "oda", 2).create());
    }

    @Test
    public void should_fail_if_actual_contains_sequence_only_once_but_in_different_case() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertContainsOnlyOnce(someInfo(), "Yoda", "yo")).withMessage(ShouldContainCharSequenceOnlyOnce.shouldContainOnlyOnce("Yoda", "yo", 0).create());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_given_string() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertContainsOnlyOnce(someInfo(), "Yoda", "Luke")).withMessage(ShouldContainCharSequenceOnlyOnce.shouldContainOnlyOnce("Yoda", "Luke", 0).create());
    }

    @Test
    public void should_throw_error_if_sequence_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> strings.assertContainsOnlyOnce(someInfo(), "Yoda", null)).withMessage(ErrorMessages.charSequenceToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertContainsOnlyOnce(someInfo(), null, "Yoda")).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_contains_sequence_only_once_according_to_custom_comparison_strategy() {
        stringsWithCaseInsensitiveComparisonStrategy.assertContainsOnlyOnce(TestData.someInfo(), "Yoda", "Yo");
        stringsWithCaseInsensitiveComparisonStrategy.assertContainsOnlyOnce(TestData.someInfo(), "Yoda", "yo");
        stringsWithCaseInsensitiveComparisonStrategy.assertContainsOnlyOnce(TestData.someInfo(), "Yoda", "YO");
    }

    @Test
    public void should_fail_if_actual_does_not_contain_sequence_only_once_according_to_custom_comparison_strategy() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> stringsWithCaseInsensitiveComparisonStrategy.assertContainsOnlyOnce(someInfo(), "Yoda", "Luke")).withMessage(ShouldContainCharSequenceOnlyOnce.shouldContainOnlyOnce("Yoda", "Luke", 0, comparisonStrategy).create());
    }

    @Test
    public void should_fail_if_actual_contains_sequence_several_times_according_to_custom_comparison_strategy() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> stringsWithCaseInsensitiveComparisonStrategy.assertContainsOnlyOnce(someInfo(), "Yoda", "Luke")).withMessage(ShouldContainCharSequenceOnlyOnce.shouldContainOnlyOnce("Yoda", "Luke", 0, comparisonStrategy).create());
    }
}

