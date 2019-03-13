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
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link Strings#assertContains(AssertionInfo, CharSequence, CharSequence)}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class Strings_assertContains_Test extends StringsBaseTest {
    @Test
    public void should_fail_if_actual_does_not_contain_sequence() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertContains(someInfo(), "Yoda", "Luke")).withMessage(ShouldContainCharSequence.shouldContain("Yoda", "Luke").create());
    }

    @Test
    public void should_fail_if_actual_contains_sequence_but_in_different_case() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertContains(someInfo(), "Yoda", "yo")).withMessage(ShouldContainCharSequence.shouldContain("Yoda", "yo").create());
    }

    @Test
    public void should_throw_error_if_sequence_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> strings.assertContains(someInfo(), "Yoda", ((String) (null)))).withMessage(ErrorMessages.charSequenceToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertContains(someInfo(), null, "Yoda")).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_contains_sequence() {
        strings.assertContains(TestData.someInfo(), "Yoda", "Yo");
    }

    @Test
    public void should_fail_if_actual_does_not_contain_all_given_strings() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertContains(someInfo(), "Yoda", "Yo", "da", "Han")).withMessage(ShouldContainCharSequence.shouldContain("Yoda", Arrays.array("Yo", "da", "Han"), Sets.newLinkedHashSet("Han")).create());
    }

    @Test
    public void should_pass_if_actual_contains_all_given_strings() {
        strings.assertContains(TestData.someInfo(), "Yoda", "Yo", "da");
    }

    @Test
    public void should_pass_if_actual_contains_sequence_according_to_custom_comparison_strategy() {
        stringsWithCaseInsensitiveComparisonStrategy.assertContains(TestData.someInfo(), "Yoda", "Yo");
        stringsWithCaseInsensitiveComparisonStrategy.assertContains(TestData.someInfo(), "Yoda", "yo");
        stringsWithCaseInsensitiveComparisonStrategy.assertContains(TestData.someInfo(), "Yoda", "YO");
    }

    @Test
    public void should_pass_if_actual_contains_all_given_strings_according_to_custom_comparison_strategy() {
        stringsWithCaseInsensitiveComparisonStrategy.assertContains(TestData.someInfo(), "Yoda", "YO", "dA");
    }

    @Test
    public void should_fail_if_actual_does_not_contain_sequence_according_to_custom_comparison_strategy() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> stringsWithCaseInsensitiveComparisonStrategy.assertContains(someInfo(), "Yoda", "Luke")).withMessage(ShouldContainCharSequence.shouldContain("Yoda", "Luke", comparisonStrategy).create());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_all_given_strings_according_to_custom_comparison_strategy() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> stringsWithCaseInsensitiveComparisonStrategy.assertContains(someInfo(), "Yoda", "Yo", "da", "Han")).withMessage(ShouldContainCharSequence.shouldContain("Yoda", Arrays.array("Yo", "da", "Han"), Sets.newLinkedHashSet("Han"), comparisonStrategy).create());
    }
}

