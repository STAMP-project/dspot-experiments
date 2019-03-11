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
package org.assertj.core.api.optionaldouble;


import java.util.OptionalDouble;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.BaseTest;
import org.assertj.core.error.OptionalShouldBeEmpty;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


public class OptionalDoubleAssert_isEmpty_Test extends BaseTest {
    @Test
    public void should_pass_if_optionaldouble_is_empty() {
        Assertions.assertThat(OptionalDouble.empty()).isEmpty();
    }

    @Test
    public void should_fail_when_optionaldouble_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(((OptionalDouble) (null))).isEmpty()).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_optionaldouble_is_present() {
        OptionalDouble actual = OptionalDouble.of(10.0);
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(actual).isEmpty()).withMessage(OptionalShouldBeEmpty.shouldBeEmpty(actual).create());
    }
}

