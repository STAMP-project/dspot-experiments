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
import org.assertj.core.error.ShouldNotStartWith;
import org.assertj.core.internal.StringsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link Strings#assertDoesNotStartWith(AssertionInfo, CharSequence, CharSequence)}</code>.
 *
 * @author Michal Kordas
 */
public class Strings_assertDoesNotStartWith_Test extends StringsBaseTest {
    @Test
    public void should_fail_if_actual_starts_with_prefix() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertDoesNotStartWith(someInfo(), "Yoda", "Yo")).withMessage(ShouldNotStartWith.shouldNotStartWith("Yoda", "Yo").create());
    }

    @Test
    public void should_throw_error_if_prefix_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> strings.assertDoesNotStartWith(someInfo(), "Yoda", null)).withMessage("The given prefix should not be null");
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertDoesNotStartWith(someInfo(), null, "Yoda")).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_does_not_start_with_prefix() {
        strings.assertDoesNotStartWith(TestData.someInfo(), "Yoda", "Luke");
        strings.assertDoesNotStartWith(TestData.someInfo(), "Yoda", "YO");
    }

    @Test
    public void should_pass_if_actual_does_not_start_with_prefix_according_to_custom_comparison_strategy() {
        stringsWithCaseInsensitiveComparisonStrategy.assertDoesNotStartWith(TestData.someInfo(), "Yoda", "Luke");
    }

    @Test
    public void should_fail_if_actual_starts_with_prefix_according_to_custom_comparison_strategy() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> stringsWithCaseInsensitiveComparisonStrategy.assertDoesNotStartWith(someInfo(), "Yoda", "yODA")).withMessage(ShouldNotStartWith.shouldNotStartWith("Yoda", "yODA", comparisonStrategy).create());
    }
}

