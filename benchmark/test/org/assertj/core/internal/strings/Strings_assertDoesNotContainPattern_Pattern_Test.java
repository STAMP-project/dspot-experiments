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


import java.util.regex.Pattern;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldNotContainPattern;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.StringsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link Strings#assertDoesNotContainPattern(AssertionInfo, CharSequence, Pattern)}</code>.
 */
public class Strings_assertDoesNotContainPattern_Pattern_Test extends StringsBaseTest {
    private static final String CONTAINED_PATTERN = "y.*u?";

    private static final String NOT_CONTAINED_PATTERN = "Y.*U?";

    private static final String ACTUAL = "No soup for you!";

    @Test
    public void should_throw_error_if_pattern_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            Pattern nullPattern = null;
            strings.assertDoesNotContainPattern(someInfo(), ACTUAL, nullPattern);
        }).withMessage(ErrorMessages.regexPatternIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertDoesNotContainPattern(someInfo(), null, matchAnything())).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_contains_pattern() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertDoesNotContainPattern(someInfo(), ACTUAL, Pattern.compile(CONTAINED_PATTERN))).withMessage(ShouldNotContainPattern.shouldNotContainPattern(Strings_assertDoesNotContainPattern_Pattern_Test.ACTUAL, Strings_assertDoesNotContainPattern_Pattern_Test.CONTAINED_PATTERN).create());
    }

    @Test
    public void should_pass_if_actual_does_not_contain_pattern() {
        strings.assertDoesNotContainPattern(TestData.someInfo(), Strings_assertDoesNotContainPattern_Pattern_Test.ACTUAL, Pattern.compile(Strings_assertDoesNotContainPattern_Pattern_Test.NOT_CONTAINED_PATTERN));
    }

    @Test
    public void should_throw_error_if_pattern_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            Pattern nullPattern = null;
            stringsWithCaseInsensitiveComparisonStrategy.assertDoesNotContainPattern(someInfo(), ACTUAL, nullPattern);
        }).withMessage(ErrorMessages.regexPatternIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> stringsWithCaseInsensitiveComparisonStrategy.assertDoesNotContainPattern(someInfo(), null, matchAnything())).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_contains_pattern_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            stringsWithCaseInsensitiveComparisonStrategy.assertDoesNotContainPattern(someInfo(), ACTUAL, Pattern.compile(CONTAINED_PATTERN));
        }).withMessage(ShouldNotContainPattern.shouldNotContainPattern(Strings_assertDoesNotContainPattern_Pattern_Test.ACTUAL, Strings_assertDoesNotContainPattern_Pattern_Test.CONTAINED_PATTERN).create());
    }

    @Test
    public void should_pass_if_actual_does_not_contain_pattern_whatever_custom_comparison_strategy_is() {
        stringsWithCaseInsensitiveComparisonStrategy.assertDoesNotContainPattern(TestData.someInfo(), Strings_assertDoesNotContainPattern_Pattern_Test.ACTUAL, Pattern.compile(Strings_assertDoesNotContainPattern_Pattern_Test.NOT_CONTAINED_PATTERN));
    }
}

