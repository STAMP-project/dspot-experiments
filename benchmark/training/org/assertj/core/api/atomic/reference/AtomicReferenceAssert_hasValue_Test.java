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
package org.assertj.core.api.atomic.reference;


import java.util.concurrent.atomic.AtomicReference;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldHaveValue;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


public class AtomicReferenceAssert_hasValue_Test {
    @Test
    public void should_pass_when_actual_has_the_expected_value() {
        String initialValue = "foo";
        AtomicReference<String> actual = new AtomicReference<>(initialValue);
        Assertions.assertThat(actual).hasValue(initialValue);
    }

    @Test
    public void should_fail_when_actual_does_not_have_the_expected_value() {
        AtomicReference<String> actual = new AtomicReference<>("foo");
        String expectedValue = "bar";
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(actual).hasValue(expectedValue)).withMessage(ShouldHaveValue.shouldHaveValue(actual, expectedValue).create());
    }

    @Test
    public void should_fail_when_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            AtomicReference<String> actual = null;
            assertThat(actual).hasValue("foo");
        }).withMessage(FailureMessages.actualIsNull());
    }
}

