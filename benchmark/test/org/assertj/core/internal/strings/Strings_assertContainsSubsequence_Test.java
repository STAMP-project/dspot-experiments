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
import org.assertj.core.error.ShouldContainSubsequenceOfCharSequence;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.StringsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;


public class Strings_assertContainsSubsequence_Test extends StringsBaseTest {
    @Test
    public void should_pass_if_actual_contains_subsequence() {
        strings.assertContainsSubsequence(TestData.someInfo(), "Yoda", Arrays.array("Yo", "da"));
    }

    @Test
    public void should_pass_if_actual_contains_subsequence_with_values_between() {
        String actual = "{ 'title':'A Game of Thrones', 'author':'George Martin'}";
        String[] sequenceValues = new String[]{ "{", "title", "A Game of Thrones", "}" };
        strings.assertContainsSubsequence(TestData.someInfo(), actual, sequenceValues);
    }

    @Test
    public void should_fail_if_actual_does_not_contain_all_given_strings() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertContainsSubsequence(someInfo(), "Yoda", array("Yo", "da", "Han"))).withMessage(ShouldContainCharSequence.shouldContain("Yoda", Arrays.array("Yo", "da", "Han"), Sets.newLinkedHashSet("Han")).create());
    }

    @Test
    public void should_fail_if_actual_contains_values_but_not_in_given_order() {
        String actual = "{ 'title':'A Game of Thrones', 'author':'George Martin'}";
        String[] sequenceValues = new String[]{ "{", "author", "A Game of Thrones", "}" };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertContainsSubsequence(someInfo(), actual, sequenceValues)).withMessage(ShouldContainSubsequenceOfCharSequence.shouldContainSubsequence(actual, sequenceValues, 1).create());
    }

    @Test
    public void should_throw_error_if_subsequence_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> strings.assertContainsSubsequence(someInfo(), "Yoda", null)).withMessage(ErrorMessages.arrayOfValuesToLookForIsNull());
    }

    @Test
    public void should_throw_error_if_any_value_of_subsequence_is_null() {
        String[] sequenceValues = new String[]{ "author", null };
        Assertions.assertThatNullPointerException().isThrownBy(() -> strings.assertContainsSubsequence(someInfo(), "'author':'George Martin'", sequenceValues)).withMessage("Expecting CharSequence elements not to be null but found one at index 1");
    }

    @Test
    public void should_throw_error_if_subsequence_values_is_empty() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> strings.assertContainsSubsequence(someInfo(), "Yoda", new String[0])).withMessage(ErrorMessages.arrayOfValuesToLookForIsEmpty());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertContainsSubsequence(someInfo(), null, array("Yo", "da"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_contains_subsequence_that_specifies_multiple_times_the_same_value_bug_544() {
        strings.assertContainsSubsequence(TestData.someInfo(), "a-b-c-", Arrays.array("a", "-", "b", "-", "c"));
        strings.assertContainsSubsequence(TestData.someInfo(), "{ 'title':'A Game of Thrones', 'author':'George Martin'}", Arrays.array("George", " ", "Martin"));
    }

    @Test
    public void should_pass_if_actual_contains_subsequence_according_to_custom_comparison_strategy() {
        stringsWithCaseInsensitiveComparisonStrategy.assertContainsSubsequence(TestData.someInfo(), "Yoda", Arrays.array("Yo", "da"));
        stringsWithCaseInsensitiveComparisonStrategy.assertContainsSubsequence(TestData.someInfo(), "Yoda", Arrays.array("Yo", "DA"));
        stringsWithCaseInsensitiveComparisonStrategy.assertContainsSubsequence(TestData.someInfo(), "Yoda", Arrays.array("YO", "dA"));
    }

    @Test
    public void should_fail_if_actual_does_not_contain_subsequence_according_to_custom_comparison_strategy() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> stringsWithCaseInsensitiveComparisonStrategy.assertContainsSubsequence(someInfo(), "Yoda", array("Yo", "da", "Han"))).withMessage(ShouldContainCharSequence.shouldContain("Yoda", Arrays.array("Yo", "da", "Han"), Sets.newLinkedHashSet("Han"), comparisonStrategy).create());
    }

    @Test
    public void should_fail_if_actual_contains_values_but_not_in_given_order_according_to_custom_comparison_strategy() {
        String actual = "{ 'title':'A Game of Thrones', 'author':'George Martin'}";
        String[] sequenceValues = new String[]{ "{", "author", "A Game of Thrones", "}" };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> stringsWithCaseInsensitiveComparisonStrategy.assertContainsSubsequence(someInfo(), actual, sequenceValues)).withMessage(ShouldContainSubsequenceOfCharSequence.shouldContainSubsequence(actual, sequenceValues, 1, comparisonStrategy).create());
    }
}

