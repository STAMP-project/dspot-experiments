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
import org.assertj.core.data.TolkienCharacter;
import org.assertj.core.error.OptionalShouldBePresent;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.data.TolkienCharacter.Race.HOBBIT;


/**
 *
 *
 * @author Filip Hrisafov
 */
public class OptionalAssert_get_Test extends BaseTest {
    @Test
    public void should_fail_when_optional_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(((Optional<String>) (null))).get()).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_when_optional_is_empty() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(java.util.Optional.<String>empty()).get()).withMessage(OptionalShouldBePresent.shouldBePresent(<String>empty()).create());
    }

    @Test
    public void should_pass_when_optional_contains_a_value() {
        TolkienCharacter frodo = TolkienCharacter.of("Frodo", 33, HOBBIT);
        Assertions.assertThat(of(frodo)).get().hasNoNullFieldsOrProperties();
    }
}

