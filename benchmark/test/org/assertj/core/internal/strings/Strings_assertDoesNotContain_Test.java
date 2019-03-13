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
import org.assertj.core.error.ShouldNotContainCharSequence;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.StandardComparisonStrategy;
import org.assertj.core.internal.StringsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;


/**
 * Tests for <code>{@link Strings#assertDoesNotContain(AssertionInfo, CharSequence, CharSequence...)}</code>.
 *
 * @author Alex Ruiz
 */
public class Strings_assertDoesNotContain_Test extends StringsBaseTest {
    @Test
    public void should_fail_if_actual_contains_any_of_values() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertDoesNotContain(someInfo(), "Yoda", "oda")).withMessage(ShouldNotContainCharSequence.shouldNotContain("Yoda", "oda", StandardComparisonStrategy.instance()).create());
    }

    @Test
    public void should_throw_error_if_list_of_values_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            String[] expected = null;
            strings.assertDoesNotContain(someInfo(), "Yoda", expected);
        }).withMessage(ErrorMessages.arrayOfValuesToLookForIsNull());
    }

    @Test
    public void should_throw_error_if_given_value_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            String expected = null;
            strings.assertDoesNotContain(someInfo(), "Yoda", expected);
        }).withMessage("The char sequence to look for should not be null");
    }

    @Test
    public void should_throw_error_if_any_element_of_values_is_null() {
        String[] values = new String[]{ "author", null };
        Assertions.assertThatNullPointerException().isThrownBy(() -> strings.assertDoesNotContain(someInfo(), "Yoda", values)).withMessage("Expecting CharSequence elements not to be null but found one at index 1");
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertDoesNotContain(someInfo(), null, "Yoda")).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_does_not_contain_sequence() {
        strings.assertDoesNotContain(TestData.someInfo(), "Yoda", "Lu");
    }

    @Test
    public void should_pass_if_actual_does_not_contain_sequence_according_to_custom_comparison_strategy() {
        stringsWithCaseInsensitiveComparisonStrategy.assertDoesNotContain(TestData.someInfo(), "Yoda", "Lu");
    }

    @Test
    public void should_fail_if_actual_does_not_contain_sequence_according_to_custom_comparison_strategy() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> stringsWithCaseInsensitiveComparisonStrategy.assertDoesNotContain(someInfo(), "Yoda", "yoda")).withMessage(ShouldNotContainCharSequence.shouldNotContain("Yoda", "yoda", comparisonStrategy).create());
    }

    @Test
    public void should_pass_if_actual_does_not_contain_all_of_given_values() {
        String[] values = new String[]{ "practice", "made", "good" };
        strings.assertDoesNotContain(TestData.someInfo(), "Practice makes perfect", values);
    }

    @Test
    public void should_fail_if_actual_contains_any_of_given_values() {
        String[] values = new String[]{ "practice", "make", "good" };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertDoesNotContain(someInfo(), "Practice makes perfect", values)).withMessage(String.format(ShouldNotContainCharSequence.shouldNotContain("Practice makes perfect", values, Sets.newSet("make"), StandardComparisonStrategy.instance()).create()));
    }

    @Test
    public void should_pass_if_actual_does_not_contain_all_of_given_values_according_to_custom_comparison_strategy() {
        String[] values = new String[]{ "p1ractice", "made", "good" };
        stringsWithCaseInsensitiveComparisonStrategy.assertDoesNotContain(TestData.someInfo(), "Practice makes perfect", values);
    }

    @Test
    public void should_fail_if_actual_contains_any_of_given_values_according_to_custom_comparison_strategy() {
        String[] values = new String[]{ "practice", "made", "good" };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> stringsWithCaseInsensitiveComparisonStrategy.assertDoesNotContain(someInfo(), "Practice makes perfect", values)).withMessage(String.format(ShouldNotContainCharSequence.shouldNotContain("Practice makes perfect", values, Sets.newSet("practice"), comparisonStrategy).create()));
    }
}

