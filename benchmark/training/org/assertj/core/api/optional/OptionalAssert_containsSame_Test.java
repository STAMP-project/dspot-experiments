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


import org.assertj.core.api.Assertions;
import org.assertj.core.api.BaseTest;
import org.assertj.core.error.OptionalShouldContain;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;

import static java.util.Optional.of;


public class OptionalAssert_containsSame_Test extends BaseTest {
    @Test
    public void should_fail_when_optional_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(((Optional<String>) (null))).containsSame("something")).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_expected_value_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(java.util.Optional.of("something")).containsSame(null)).withMessage("The expected value should not be <null>.");
    }

    @Test
    public void should_pass_if_optional_contains_the_expected_object_reference() {
        String containedAndExpected = "something";
        Assertions.assertThat(of(containedAndExpected)).containsSame(containedAndExpected);
    }

    @Test
    public void should_fail_if_optional_does_not_contain_the_expected_object_reference() {
        java.util.Optional<String> actual = of("not-expected");
        String expectedValue = "something";
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(actual).containsSame(expectedValue)).withMessage(OptionalShouldContain.shouldContainSame(actual, expectedValue).create());
    }

    @Test
    public void should_fail_if_optional_contains_equal_but_not_same_value() {
        java.util.Optional<String> actual = of(new String("something"));
        String expectedValue = new String("something");
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(actual).containsSame(expectedValue)).withMessage(OptionalShouldContain.shouldContainSame(actual, expectedValue).create());
    }

    @Test
    public void should_fail_if_optional_is_empty() {
        String expectedValue = "something";
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(java.util.Optional.empty()).containsSame(expectedValue)).withMessage(OptionalShouldContain.shouldContain(expectedValue).create());
    }
}

