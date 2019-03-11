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
package org.assertj.core.error;


import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


public class OptionalShouldBePresent_create_Test {
    @Test
    public void should_create_error_message_with_optional() {
        String errorMessage = OptionalShouldBePresent.shouldBePresent(Optional.empty()).create();
        Assertions.assertThat(errorMessage).isEqualTo(String.format("%nExpecting Optional to contain a value but it was empty."));
    }

    @Test
    public void should_create_error_message_with_optionaldouble() {
        String errorMessage = OptionalShouldBePresent.shouldBePresent(OptionalDouble.empty()).create();
        Assertions.assertThat(errorMessage).isEqualTo(String.format("%nExpecting OptionalDouble to contain a value but it was empty."));
    }

    @Test
    public void should_create_error_message_with_optionalint() {
        String errorMessage = OptionalShouldBePresent.shouldBePresent(OptionalInt.empty()).create();
        Assertions.assertThat(errorMessage).isEqualTo(String.format("%nExpecting OptionalInt to contain a value but it was empty."));
    }

    @Test
    public void should_create_error_message_with_optionallong() {
        String errorMessage = OptionalShouldBePresent.shouldBePresent(OptionalLong.empty()).create();
        Assertions.assertThat(errorMessage).isEqualTo(String.format("%nExpecting OptionalLong to contain a value but it was empty."));
    }
}

