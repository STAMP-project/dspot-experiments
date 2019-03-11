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


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldNotBeEqualIgnoringCase;
import org.assertj.core.internal.StringsBaseTest;
import org.assertj.core.test.CharArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.junit.jupiter.api.Test;


/**
 * Tests for
 * <code>{@link org.assertj.core.internal.Strings#assertNotEqualsIgnoringCase(org.assertj.core.api.AssertionInfo, CharSequence, CharSequence)}</code>
 * .
 *
 * @author Alexander Bischof
 */
public class Strings_assertNotEqualsIgnoringCase_Test extends StringsBaseTest {
    @Test
    public void should_pass_if_actual_is_null_and_expected_is_not() {
        strings.assertNotEqualsIgnoringCase(TestData.someInfo(), null, "Luke");
    }

    @Test
    public void should_pass_if_actual_is_not_null_and_expected_is() {
        strings.assertNotEqualsIgnoringCase(TestData.someInfo(), "Luke", null);
    }

    @Test
    public void should_pass_if_both_Strings_are_not_equal_regardless_of_case() {
        strings.assertNotEqualsIgnoringCase(TestData.someInfo(), "Yoda", "Luke");
    }

    @Test
    public void should_fail_if_both_Strings_are_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertNotEqualsIgnoringCase(someInfo(), null, null)).withMessage(ShouldNotBeEqualIgnoringCase.shouldNotBeEqualIgnoringCase(null, null).create());
    }

    @Test
    public void should_fail_if_both_Strings_are_the_same() {
        String s = "Yoda";
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertNotEqualsIgnoringCase(someInfo(), s, s)).withMessage(ShouldNotBeEqualIgnoringCase.shouldNotBeEqualIgnoringCase(s, s).create());
    }

    @Test
    public void should_fail_if_both_Strings_are_equal_but_not_same() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertNotEqualsIgnoringCase(someInfo(), "Yoda", new String(arrayOf('Y', 'o', 'd', 'a')))).withMessage(ShouldNotBeEqualIgnoringCase.shouldNotBeEqualIgnoringCase("Yoda", "Yoda").create());
    }

    @Test
    public void should_fail_if_both_Strings_are_equal_ignoring_case() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertNotEqualsIgnoringCase(someInfo(), "Yoda", "YODA")).withMessage(ShouldNotBeEqualIgnoringCase.shouldNotBeEqualIgnoringCase("Yoda", "YODA").create());
    }

    @Test
    public void should_pass_if_actual_is_null_and_expected_is_not_whatever_custom_comparison_strategy_is() {
        stringsWithCaseInsensitiveComparisonStrategy.assertNotEqualsIgnoringCase(TestData.someInfo(), null, "Luke");
    }

    @Test
    public void should_pass_if_both_Strings_are_not_equal_regardless_of_case_whatever_custom_comparison_strategy_is() {
        stringsWithCaseInsensitiveComparisonStrategy.assertNotEqualsIgnoringCase(TestData.someInfo(), "Yoda", "Luke");
    }

    @Test
    public void should_fail_if_both_Strings_are_null_whatever_custom_comparison_strategy_is() {
        try {
            stringsWithCaseInsensitiveComparisonStrategy.assertNotEqualsIgnoringCase(TestData.someInfo(), null, null);
        } catch (AssertionError e) {
            verifyFailureThrownWhenStringsAreNotEqual(TestData.someInfo(), null, null);
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_both_Strings_are_the_same_whatever_custom_comparison_strategy_is() {
        AssertionInfo info = TestData.someInfo();
        String s = "Yoda";
        try {
            stringsWithCaseInsensitiveComparisonStrategy.assertNotEqualsIgnoringCase(info, s, s);
        } catch (AssertionError e) {
            verifyFailureThrownWhenStringsAreNotEqual(info, s, s);
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_both_Strings_are_equal_but_not_same_whatever_custom_comparison_strategy_is() {
        AssertionInfo info = TestData.someInfo();
        try {
            stringsWithCaseInsensitiveComparisonStrategy.assertNotEqualsIgnoringCase(info, "Yoda", new String(CharArrays.arrayOf('Y', 'o', 'd', 'a')));
        } catch (AssertionError e) {
            verifyFailureThrownWhenStringsAreNotEqual(info, "Yoda", "Yoda");
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_both_Strings_are_equal_ignoring_case_whatever_custom_comparison_strategy_is() {
        AssertionInfo info = TestData.someInfo();
        try {
            stringsWithCaseInsensitiveComparisonStrategy.assertNotEqualsIgnoringCase(info, "Yoda", "YODA");
        } catch (AssertionError e) {
            verifyFailureThrownWhenStringsAreNotEqual(info, "Yoda", "YODA");
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

