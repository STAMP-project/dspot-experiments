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
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldMatchPattern;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.StringsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Strings#assertMatches(AssertionInfo, CharSequence, Pattern)}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class Strings_assertMatches_Pattern_Test extends StringsBaseTest {
    private String actual = "Yoda";

    @Test
    public void should_throw_error_if_Pattern_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            Pattern pattern = null;
            strings.assertMatches(someInfo(), actual, pattern);
        }).withMessage(ErrorMessages.regexPatternIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertMatches(someInfo(), null, matchAnything())).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_match_Pattern() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertMatches(someInfo(), actual, Pattern.compile("Luke"))).withMessage(ShouldMatchPattern.shouldMatch(actual, "Luke").create());
    }

    @Test
    public void should_pass_if_actual_matches_Pattern() {
        strings.assertMatches(TestData.someInfo(), actual, Pattern.compile("Yod.*"));
    }

    @Test
    public void should_throw_error_if_Pattern_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            Pattern pattern = null;
            stringsWithCaseInsensitiveComparisonStrategy.assertMatches(someInfo(), actual, pattern);
        }).withMessage(ErrorMessages.regexPatternIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> stringsWithCaseInsensitiveComparisonStrategy.assertMatches(someInfo(), null, matchAnything())).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_match_Pattern_whatever_custom_comparison_strategy_is() {
        AssertionInfo info = TestData.someInfo();
        try {
            stringsWithCaseInsensitiveComparisonStrategy.assertMatches(info, actual, Pattern.compile("Luke"));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldMatchPattern.shouldMatch(actual, "Luke"));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_matches_Pattern_whatever_custom_comparison_strategy_is() {
        stringsWithCaseInsensitiveComparisonStrategy.assertMatches(TestData.someInfo(), actual, Pattern.compile("Yod.*"));
    }
}

