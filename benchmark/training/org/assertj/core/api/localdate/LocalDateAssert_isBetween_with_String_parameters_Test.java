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
package org.assertj.core.api.localdate;


import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.LocalDateAssertBaseTest;
import org.junit.jupiter.api.Test;


public class LocalDateAssert_isBetween_with_String_parameters_Test extends LocalDateAssertBaseTest {
    private LocalDate before = now.minusDays(1);

    private LocalDate after = now.plusDays(1);

    @Test
    public void should_throw_a_DateTimeParseException_if_start_String_parameter_cant_be_converted() {
        // GIVEN
        String abc = "abc";
        // WHEN
        Throwable thrown = Assertions.catchThrowable(() -> assertions.isBetween(abc, after.toString()));
        // THEN
        Assertions.assertThat(thrown).isInstanceOf(DateTimeParseException.class);
    }

    @Test
    public void should_throw_a_DateTimeParseException_if_end_String_parameter_cant_be_converted() {
        // GIVEN
        String abc = "abc";
        // WHEN
        Throwable thrown = Assertions.catchThrowable(() -> assertions.isBetween(before.toString(), abc));
        // THEN
        Assertions.assertThat(thrown).isInstanceOf(DateTimeParseException.class);
    }
}

