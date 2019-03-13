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
package org.assertj.core.api.optional;


import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.BaseTest;
import org.assertj.core.error.OptionalShouldContain;
import org.assertj.core.util.AssertionsUtil;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;


public class OptionalAssert_contains_Test extends BaseTest {
    @Test
    public void should_fail_when_optional_is_null() {
        // GIVEN
        Optional<String> nullActual = null;
        // THEN
        AssertionsUtil.assertThatAssertionErrorIsThrownBy(() -> assertThat(nullActual).contains("something")).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_expected_value_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(Optional.of("something")).contains(null)).withMessage("The expected value should not be <null>.");
    }

    @Test
    public void should_pass_if_optional_contains_expected_value() {
        Assertions.assertThat(Optional.of("something")).contains("something");
    }

    @Test
    public void should_fail_if_optional_does_not_contain_expected_value() {
        // GIVEN
        Optional<String> actual = Optional.of("not-expected");
        String expectedValue = "something";
        // WHEN
        AssertionFailedError error = Assertions.catchThrowableOfType(() -> assertThat(actual).contains(expectedValue), AssertionFailedError.class);
        // THEN
        Assertions.assertThat(error).hasMessage(OptionalShouldContain.shouldContain(actual, expectedValue).create());
        Assertions.assertThat(error.getActual().getStringRepresentation()).isEqualTo(actual.get());
        Assertions.assertThat(error.getExpected().getStringRepresentation()).isEqualTo(expectedValue);
    }

    @Test
    public void should_fail_if_optional_is_empty() {
        // GIVEN
        String expectedValue = "something";
        // WHEN
        Throwable error = Assertions.catchThrowable(() -> assertThat(Optional.empty()).contains(expectedValue));
        // THEN
        Assertions.assertThat(error).hasMessage(OptionalShouldContain.shouldContain(expectedValue).create());
    }
}

