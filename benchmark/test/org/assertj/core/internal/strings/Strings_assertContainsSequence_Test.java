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
import org.assertj.core.error.ShouldContainSequenceOfCharSequence;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.StringsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link Strings#assertContainsSequence(AssertionInfo, CharSequence, CharSequence[])}</code>.
 *
 * @author Billy Yuan
 */
public class Strings_assertContainsSequence_Test extends StringsBaseTest {
    String actual = "{ 'title':'A Game of Thrones', 'author':'George Martin'}";

    @Test
    public void should_pass_if_actual_contains_sequence() {
        String[] sequenceValues = new String[]{ "{ ", "'title':", "'A Game of Thrones'", "," };
        strings.assertContainsSequence(TestData.someInfo(), actual, sequenceValues);
    }

    @Test
    public void should_fail_if_actual_contains_sequence_with_values_between() {
        String[] sequenceValues = new String[]{ "{ ", "'author':'George Martin'}" };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertContainsSequence(someInfo(), actual, sequenceValues)).withMessage(ShouldContainSequenceOfCharSequence.shouldContainSequence(actual, sequenceValues).create());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_all_given_strings() {
        String[] sequenceValues = new String[]{ "{ ", "'title':", "'A Game of Thrones'", "unexpectedString" };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertContainsSequence(someInfo(), actual, sequenceValues)).withMessage(ShouldContainCharSequence.shouldContain(actual, sequenceValues, Sets.newLinkedHashSet("unexpectedString")).create());
    }

    @Test
    public void should_fail_if_actual_contains_values_but_not_in_the_given_order() {
        String[] sequenceValues = new String[]{ "'A Game of Thrones'", "'title':" };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertContainsSequence(someInfo(), actual, sequenceValues)).withMessage(ShouldContainSequenceOfCharSequence.shouldContainSequence(actual, sequenceValues).create());
    }

    @Test
    public void should_throw_error_if_sequence_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> strings.assertContainsSequence(someInfo(), actual, null)).withMessage(ErrorMessages.arrayOfValuesToLookForIsNull());
    }

    @Test
    public void should_throw_error_if_any_value_of_sequence_is_null() {
        String[] sequenceValues = new String[]{ "author", null };
        Assertions.assertThatNullPointerException().isThrownBy(() -> strings.assertContainsSequence(someInfo(), actual, sequenceValues)).withMessage("Expecting CharSequence elements not to be null but found one at index 1");
    }

    @Test
    public void should_throw_error_if_sequence_values_are_empty() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> strings.assertContainsSequence(someInfo(), actual, new String[0])).withMessage(ErrorMessages.arrayOfValuesToLookForIsEmpty());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        String[] sequenceValues = new String[]{ "{ ", "'title':", "'A Game of Thrones'", "," };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertContainsSequence(someInfo(), null, sequenceValues)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_contains_sequence_that_specifies_multiple_times_the_same_value() {
        strings.assertContainsSequence(TestData.someInfo(), "a-b-c-", Arrays.array("a", "-", "b", "-", "c"));
    }

    @Test
    public void should_pass_if_actual_contains_sequence_according_to_custom_comparison_strategy() {
        stringsWithCaseInsensitiveComparisonStrategy.assertContainsSequence(TestData.someInfo(), "Yoda", Arrays.array("Yo", "da"));
        stringsWithCaseInsensitiveComparisonStrategy.assertContainsSequence(TestData.someInfo(), "Yoda", Arrays.array("Yo", "DA"));
        stringsWithCaseInsensitiveComparisonStrategy.assertContainsSequence(TestData.someInfo(), "Yoda", Arrays.array("YO", "dA"));
    }

    @Test
    public void should_fail_if_actual_does_not_contain_sequence_according_to_custom_comparison_strategy() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> stringsWithCaseInsensitiveComparisonStrategy.assertContainsSequence(someInfo(), "Yoda", array("Yo", "da", "Han"))).withMessage(ShouldContainCharSequence.shouldContain("Yoda", Arrays.array("Yo", "da", "Han"), Sets.newLinkedHashSet("Han"), comparisonStrategy).create());
    }

    @Test
    public void should_fail_if_actual_contains_values_but_not_in_given_order_according_to_custom_comparison_strategy() {
        String[] sequenceValues = new String[]{ ", 'author'", "'A Game of Thrones'" };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> stringsWithCaseInsensitiveComparisonStrategy.assertContainsSequence(someInfo(), actual, sequenceValues)).withMessage(ShouldContainSequenceOfCharSequence.shouldContainSequence(actual, sequenceValues, comparisonStrategy).create());
    }
}

