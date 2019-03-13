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


import org.assertj.core.error.ShouldBeEqualIgnoringNewLineDifferences;
import org.assertj.core.internal.StringsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for
 * <code>{@link org.assertj.core.internal.Strings#assertIsEqualToNormalizingNewlines(org.assertj.core.api.AssertionInfo, CharSequence, CharSequence)}</code>
 * .
 *
 * @author Mauricio Aniche
 */
public class Strings_assertIsEqualToNormalizingNewlines_Test extends StringsBaseTest {
    @Test
    public void should_pass_if_both_strings_are_equals_after_normalizing_newline() {
        strings.assertIsEqualToNormalizingNewlines(TestData.someInfo(), "Lord of the Rings\r\nis cool", "Lord of the Rings\nis cool");
        strings.assertIsEqualToNormalizingNewlines(TestData.someInfo(), "Lord of the Rings\nis cool", "Lord of the Rings\nis cool");
    }

    @Test
    public void should_pass_if_comparing_string_with_only_newlines() {
        strings.assertIsEqualToNormalizingNewlines(TestData.someInfo(), "\n", "\r\n");
        strings.assertIsEqualToNormalizingNewlines(TestData.someInfo(), "\r\n", "\n");
        strings.assertIsEqualToNormalizingNewlines(TestData.someInfo(), "\r\n", "\r\n");
        strings.assertIsEqualToNormalizingNewlines(TestData.someInfo(), "\n", "\n");
    }

    @Test
    public void should_fail_if_newlines_are_different_in_both_strings() {
        String actual = "Lord of the Rings\r\n\r\nis cool";
        String expected = "Lord of the Rings\nis cool";
        try {
            strings.assertIsEqualToNormalizingNewlines(TestData.someInfo(), actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(TestData.someInfo(), ShouldBeEqualIgnoringNewLineDifferences.shouldBeEqualIgnoringNewLineDifferences(actual, expected), actual, expected);
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

