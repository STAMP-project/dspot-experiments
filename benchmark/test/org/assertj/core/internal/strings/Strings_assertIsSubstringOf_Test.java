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
import org.assertj.core.error.ShouldBeSubstring;
import org.assertj.core.internal.StandardComparisonStrategy;
import org.assertj.core.internal.StringsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


public class Strings_assertIsSubstringOf_Test extends StringsBaseTest {
    @Test
    public void should_pass_if_actual_is_a_substring_of_given_string() {
        strings.assertIsSubstringOf(TestData.someInfo(), "Yo", "Yoda");
    }

    @Test
    public void should_pass_if_actual_is_equal_to_given_string() {
        strings.assertIsSubstringOf(TestData.someInfo(), "Yoda", "Yoda");
    }

    @Test
    public void should_pass_if_actual_is_empty() {
        strings.assertIsSubstringOf(TestData.someInfo(), "", "Yoda");
        strings.assertIsSubstringOf(TestData.someInfo(), "", "");
    }

    @Test
    public void should_fail_if_actual_contains_given_string() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertIsSubstringOf(someInfo(), "Yoda", "oda")).withMessage(ShouldBeSubstring.shouldBeSubstring("Yoda", "oda", StandardComparisonStrategy.instance()).create());
    }

    @Test
    public void should_fail_if_actual_completely_different_from_given_string() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertIsSubstringOf(someInfo(), "Yoda", "Luke")).withMessage(ShouldBeSubstring.shouldBeSubstring("Yoda", "Luke", StandardComparisonStrategy.instance()).create());
    }

    @Test
    public void should_throw_error_if_sequence_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> strings.assertIsSubstringOf(someInfo(), "Yoda", null)).withMessage("Expecting CharSequence not to be null");
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertIsSubstringOf(someInfo(), null, "Yoda")).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_a_part_of_sequence_only_according_to_custom_comparison_strategy() {
        stringsWithCaseInsensitiveComparisonStrategy.assertIsSubstringOf(TestData.someInfo(), "Yo", "Yoda");
        stringsWithCaseInsensitiveComparisonStrategy.assertIsSubstringOf(TestData.someInfo(), "yo", "Yoda");
        stringsWithCaseInsensitiveComparisonStrategy.assertIsSubstringOf(TestData.someInfo(), "YO", "Yoda");
    }

    @Test
    public void should_fail_if_actual_is_not_a_substring_of_sequence_according_to_custom_comparison_strategy() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> stringsWithCaseInsensitiveComparisonStrategy.assertIsSubstringOf(someInfo(), "Yoda", "Luke")).withMessage(ShouldBeSubstring.shouldBeSubstring("Yoda", "Luke", comparisonStrategy).create());
    }
}

