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
import org.assertj.core.error.ShouldHaveSizeGreaterThanOrEqualTo;
import org.assertj.core.internal.StringsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link Strings#assertHasSizeGreaterThanOrEqualTo(AssertionInfo, CharSequence, int)} </code>.
 *
 * @author Sandra Parsick
 * @author Georg Berky
 */
public class Strings_assertHasSizeGreaterThanOrEqualTo_Test extends StringsBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        AssertionInfo info = TestData.someInfo();
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertHasSizeGreaterThanOrEqualTo(info, null, 3)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_size_of_actual_is_less_than_expected_size() {
        AssertionInfo info = TestData.someInfo();
        String actual = "Han";
        String expectedErrorMessage = ShouldHaveSizeGreaterThanOrEqualTo.shouldHaveSizeGreaterThanOrEqualTo(actual, actual.length(), 4).create();
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> strings.assertHasSizeGreaterThanOrEqualTo(info, actual, 4)).withMessage(expectedErrorMessage);
    }

    @Test
    public void should_pass_if_size_of_actual_is_equal_to_expected_size() {
        strings.assertHasSizeGreaterThanOrEqualTo(TestData.someInfo(), "Han", 3);
    }

    @Test
    public void should_pass_if_size_of_actual_is_greater_than_expected_size() {
        strings.assertHasSizeGreaterThanOrEqualTo(TestData.someInfo(), "Han", 2);
    }
}

