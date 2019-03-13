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
import org.assertj.core.error.ShouldBeEqualIgnoringCase;
import org.assertj.core.internal.StringsBaseTest;
import org.assertj.core.test.CharArrays;
import org.assertj.core.test.TestData;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link Strings#assertEqualsIgnoringCase(AssertionInfo, CharSequence, CharSequence)}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class Strings_assertEqualsIgnoringCase_Test extends StringsBaseTest {
    @Test
    public void should_fail_if_actual_is_null_and_expected_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertEqualsIgnoringCase(someInfo(), null, "Luke")).withMessage(ShouldBeEqualIgnoringCase.shouldBeEqual(null, "Luke").create());
    }

    @Test
    public void should_fail_if_actual_is_not_null_and_expected_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertEqualsIgnoringCase(someInfo(), "Luke", null)).withMessage(ShouldBeEqualIgnoringCase.shouldBeEqual("Luke", null).create());
    }

    @Test
    public void should_fail_if_both_Strings_are_not_equal_regardless_of_case() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertEqualsIgnoringCase(someInfo(), "Yoda", "Luke")).withMessage(ShouldBeEqualIgnoringCase.shouldBeEqual("Yoda", "Luke").create());
    }

    @Test
    public void should_pass_if_both_Strings_are_null() {
        strings.assertEqualsIgnoringCase(TestData.someInfo(), null, null);
    }

    @Test
    public void should_pass_if_both_Strings_are_the_same() {
        String s = "Yoda";
        strings.assertEqualsIgnoringCase(TestData.someInfo(), s, s);
    }

    @Test
    public void should_pass_if_both_Strings_are_equal_but_not_same() {
        strings.assertEqualsIgnoringCase(TestData.someInfo(), "Yoda", new String(CharArrays.arrayOf('Y', 'o', 'd', 'a')));
    }

    @Test
    public void should_pass_if_both_Strings_are_equal_ignoring_case() {
        strings.assertEqualsIgnoringCase(TestData.someInfo(), "Yoda", "YODA");
    }

    @Test
    public void should_fail_if_actual_is_null_and_expected_is_not_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> stringsWithCaseInsensitiveComparisonStrategy.assertEqualsIgnoringCase(someInfo(), null, "Luke")).withMessage(ShouldBeEqualIgnoringCase.shouldBeEqual(null, "Luke").create());
    }

    @Test
    public void should_fail_if_both_Strings_are_not_equal_regardless_of_case_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> stringsWithCaseInsensitiveComparisonStrategy.assertEqualsIgnoringCase(someInfo(), "Yoda", "Luke")).withMessage(ShouldBeEqualIgnoringCase.shouldBeEqual("Yoda", "Luke").create());
    }

    @Test
    public void should_pass_if_both_Strings_are_null_whatever_custom_comparison_strategy_is() {
        stringsWithCaseInsensitiveComparisonStrategy.assertEqualsIgnoringCase(TestData.someInfo(), null, null);
    }

    @Test
    public void should_pass_if_both_Strings_are_the_same_whatever_custom_comparison_strategy_is() {
        String s = "Yoda";
        stringsWithCaseInsensitiveComparisonStrategy.assertEqualsIgnoringCase(TestData.someInfo(), s, s);
    }

    @Test
    public void should_pass_if_both_Strings_are_equal_but_not_same_whatever_custom_comparison_strategy_is() {
        stringsWithCaseInsensitiveComparisonStrategy.assertEqualsIgnoringCase(TestData.someInfo(), "Yoda", new String(CharArrays.arrayOf('Y', 'o', 'd', 'a')));
    }

    @Test
    public void should_pass_if_both_Strings_are_equal_ignoring_case_whatever_custom_comparison_strategy_is() {
        stringsWithCaseInsensitiveComparisonStrategy.assertEqualsIgnoringCase(TestData.someInfo(), "Yoda", "YODA");
    }
}

