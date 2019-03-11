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


import java.util.OptionalDouble;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


public class OptionalDoubleShouldHaveValueCloseToPercentage_create_Test {
    @Test
    public void should_create_error_message_when_optionaldouble_is_empty() {
        String errorMessage = OptionalDoubleShouldHaveValueCloseToPercentage.shouldHaveValueCloseToPercentage(10.0).create();
        Assertions.assertThat(errorMessage).isEqualTo(String.format(("%nExpecting an OptionalDouble with value:%n" + ("  <10.0>%n" + "but was empty."))));
    }

    @Test
    public void should_create_error_message_when_optionaldouble_value_is_not_close_enough_to_expected_value() {
        String errorMessage = OptionalDoubleShouldHaveValueCloseToPercentage.shouldHaveValueCloseToPercentage(OptionalDouble.of(20), 10, Assertions.withinPercentage(2), 3).create();
        Assertions.assertThat(errorMessage).isEqualTo(String.format(("%nExpecting:%n  <OptionalDouble[20.0]>%n" + ((("to be close to:%n" + "  <10.0>%n") + "by less than 2%% but difference was 30.0%%.%n") + "(a difference of exactly 2%% being considered valid)"))));
    }
}

