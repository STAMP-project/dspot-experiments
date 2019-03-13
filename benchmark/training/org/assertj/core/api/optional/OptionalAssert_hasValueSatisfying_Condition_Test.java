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
import org.assertj.core.api.AssertionsForClassTypes;
import org.assertj.core.api.BaseTest;
import org.assertj.core.api.Condition;
import org.assertj.core.error.OptionalShouldBePresent;
import org.assertj.core.error.ShouldBe;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;

import static java.util.Optional.empty;
import static java.util.Optional.of;


public class OptionalAssert_hasValueSatisfying_Condition_Test extends BaseTest {
    private Condition<String> passingCondition = new org.assertj.core.api.TestCondition(true);

    private Condition<String> notPassingCondition = new org.assertj.core.api.TestCondition();

    @Test
    public void should_fail_when_optional_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(((Optional<String>) (null))).hasValueSatisfying(passingCondition)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_when_optional_is_empty() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(java.util.Optional.<String>empty()).hasValueSatisfying(passingCondition)).withMessage(OptionalShouldBePresent.shouldBePresent(empty()).create());
    }

    @Test
    public void should_fail_when_condition_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> assertThat(java.util.Optional.of("something")).hasValueSatisfying(((Condition<String>) (null)))).withMessage("The condition to evaluate should not be null");
    }

    @Test
    public void should_pass_when_condition_is_met() {
        AssertionsForClassTypes.assertThat(of("something")).hasValueSatisfying(passingCondition);
    }

    @Test
    public void should_fail_when_condition_is_not_met() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(java.util.Optional.of("something")).hasValueSatisfying(notPassingCondition)).withMessage(ShouldBe.shouldBe("something", notPassingCondition).create());
    }
}

