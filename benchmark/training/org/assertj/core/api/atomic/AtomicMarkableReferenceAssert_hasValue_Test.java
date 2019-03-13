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
package org.assertj.core.api.atomic;


import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldBeMarked;
import org.assertj.core.error.ShouldHaveReference;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


public class AtomicMarkableReferenceAssert_hasValue_Test {
    private String expectedValue = "expectedValue";

    @Test
    public void should_fail_when_AtomicMarkableReference_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(((AtomicMarkableReference<String>) (null))).hasReference(expectedValue)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_expected_value_is_null_and_does_not_contain_expected_value() {
        java.util.concurrent.atomic.AtomicMarkableReference<String> actual = new java.util.concurrent.atomic.AtomicMarkableReference<>("actual", true);
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(actual).hasReference(null)).withMessage(ShouldHaveReference.shouldHaveReference(actual, actual.getReference(), null).create());
    }

    @Test
    public void should_fail_if_atomicMarkableReference_does_not_contain_expected_value() {
        java.util.concurrent.atomic.AtomicMarkableReference<String> actual = new java.util.concurrent.atomic.AtomicMarkableReference<>("actual", true);
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(actual).hasReference(expectedValue)).withMessage(ShouldHaveReference.shouldHaveReference(actual, actual.getReference(), expectedValue).create());
    }

    @Test
    public void should_pass_if_AtomicMarkableReference_contains_expected_value() {
        Assertions.assertThat(new java.util.concurrent.atomic.AtomicMarkableReference(expectedValue, true)).hasReference(expectedValue);
        Assertions.assertThat(new java.util.concurrent.atomic.AtomicMarkableReference(expectedValue, true)).hasReference(expectedValue);
    }

    @Test
    public void should_pass_if_atomicMarkableReference_contains_expected_value_and_is_marked() {
        Assertions.assertThat(new java.util.concurrent.atomic.AtomicMarkableReference(expectedValue, true)).hasReference(expectedValue).isMarked();
    }

    @Test
    public void should_pass_if_atomicMarkableReference_contains_expected_value_and_is_not_marked() {
        Assertions.assertThat(new java.util.concurrent.atomic.AtomicMarkableReference(expectedValue, false)).hasReference(expectedValue).isNotMarked();
    }

    @Test
    public void should_fail_if_atomicMarkableReference_contains_expected_value_and_is_not_marked() {
        java.util.concurrent.atomic.AtomicMarkableReference<String> actual = new java.util.concurrent.atomic.AtomicMarkableReference<>(expectedValue, false);
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(actual).hasReference(expectedValue).isMarked()).withMessage(ShouldBeMarked.shouldBeMarked(actual).create());
    }

    @Test
    public void should_fail_if_atomicMarkableReference_contains_expected_value_and_is_marked() {
        java.util.concurrent.atomic.AtomicMarkableReference<String> actual = new java.util.concurrent.atomic.AtomicMarkableReference<>(expectedValue, true);
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(actual).hasReference(expectedValue).isNotMarked().isMarked()).withMessage(ShouldBeMarked.shouldNotBeMarked(actual).create());
    }
}

