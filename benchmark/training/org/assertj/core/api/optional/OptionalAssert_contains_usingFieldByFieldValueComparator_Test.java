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


public class OptionalAssert_contains_usingFieldByFieldValueComparator_Test extends BaseTest {
    @Test
    public void should_fail_when_optional_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(((Optional<org.assertj.core.api.optional.Foo>) (null))).usingFieldByFieldValueComparator().contains(new org.assertj.core.api.optional.Foo("something"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_expected_value_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(java.util.Optional.of(new org.assertj.core.api.optional.Foo("something"))).usingFieldByFieldValueComparator().contains(null)).withMessage("The expected value should not be <null>.");
    }

    @Test
    public void should_pass_if_optional_contains_expected_value() {
        Assertions.assertThat(of(new OptionalAssert_contains_usingFieldByFieldValueComparator_Test.Foo("something"))).usingFieldByFieldValueComparator().contains(new OptionalAssert_contains_usingFieldByFieldValueComparator_Test.Foo("something"));
    }

    @Test
    public void should_fail_if_optional_does_not_contain_expected_value() {
        java.util.Optional<OptionalAssert_contains_usingFieldByFieldValueComparator_Test.Foo> actual = of(new OptionalAssert_contains_usingFieldByFieldValueComparator_Test.Foo("something"));
        OptionalAssert_contains_usingFieldByFieldValueComparator_Test.Foo expectedValue = new OptionalAssert_contains_usingFieldByFieldValueComparator_Test.Foo("something else");
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(actual).usingFieldByFieldValueComparator().contains(expectedValue)).withMessage(OptionalShouldContain.shouldContain(actual, expectedValue).create());
    }

    @Test
    public void should_fail_if_optional_is_empty() {
        OptionalAssert_contains_usingFieldByFieldValueComparator_Test.Foo expectedValue = new OptionalAssert_contains_usingFieldByFieldValueComparator_Test.Foo("test");
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(java.util.Optional.empty()).usingFieldByFieldValueComparator().contains(expectedValue)).withMessage(OptionalShouldContain.shouldContain(expectedValue).create());
    }

    private static class Foo {
        private final String value;

        public Foo(String value) {
            this.value = value;
        }

        @SuppressWarnings("unused")
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return ((("Foo{" + "value='") + (value)) + '\'') + '}';
        }
    }
}

